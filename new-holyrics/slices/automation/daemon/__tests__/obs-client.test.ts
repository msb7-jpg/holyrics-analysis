import { describe, it, expect, beforeAll, afterAll } from 'bun:test'
import { ObsClient } from '../obs-client'

describe('ObsClient (slices/automation/daemon)', () => {
  let mockServer: ReturnType<typeof Bun.serve>
  const testPort = 4499

  beforeAll(() => {
    // Cria servidor WebSocket simulando o OBS Studio v5
    mockServer = Bun.serve({
      port: testPort,
      fetch(req, server) {
        if (server.upgrade(req, { data: undefined })) {
          return undefined
        }
        return new Response('Mock OBS WebSocket', { status: 400 })
      },
      websocket: {
        open(ws) {
          // OpCode 0: Hello
          ws.send(
            JSON.stringify({
              op: 0,
              d: {
                obsWebSocketVersion: '5.1.0-mock',
                rpcVersion: 1,
              },
            }),
          )
        },
        message(ws, raw) {
          const msg = JSON.parse(String(raw))

          // OpCode 1: Identify -> responde Identified (Op 2)
          if (msg.op === 1) {
            ws.send(
              JSON.stringify({
                op: 2,
                d: { negotiatedRpcVersion: 1 },
              }),
            )
            return
          }

          // OpCode 6: Request -> responde RequestResponse (Op 7)
          if (msg.op === 6) {
            const { requestId, requestType } = msg.d

            if (requestType === 'SetCurrentProgramScene') {
              ws.send(
                JSON.stringify({
                  op: 7,
                  d: {
                    requestId,
                    requestType,
                    requestStatus: { result: true, code: 100 },
                    responseData: { sceneChanged: true },
                  },
                }),
              )
            } else if (requestType === 'ErrorSimulated') {
              ws.send(
                JSON.stringify({
                  op: 7,
                  d: {
                    requestId,
                    requestType,
                    requestStatus: { result: false, code: 600, comment: 'Cena não encontrada' },
                  },
                }),
              )
            }
          }
        },
      },
    })
  })

  afterAll(() => {
    mockServer.stop()
  })

  it('conecta ao mock OBS Studio v5 e completa o handshake', async () => {
    const client = new ObsClient({
      host: 'localhost',
      port: testPort,
      autoReconnect: false,
    })

    client.connect()

    // Aguarda o handshake
    let retries = 20
    while (!client.getStatus().connected && retries > 0) {
      await new Promise((resolve) => setTimeout(resolve, 50))
      retries--
    }

    const status = client.getStatus()
    expect(status.connected).toBe(true)
    expect(status.port).toBe(testPort)

    client.dispose()
  })

  it('envia comando SetCurrentProgramScene e recebe resposta com sucesso', async () => {
    const client = new ObsClient({
      host: 'localhost',
      port: testPort,
      autoReconnect: false,
    })

    client.connect()

    while (!client.getStatus().connected) {
      await new Promise((resolve) => setTimeout(resolve, 30))
    }

    const response = await client.sendRequest<{ sceneChanged: boolean }>(
      'SetCurrentProgramScene',
      { sceneName: 'Púlpito' },
    )

    expect(response.sceneChanged).toBe(true)

    client.dispose()
  })

  it('rejeita promise caso o OBS retorne erro de execução', async () => {
    const client = new ObsClient({
      host: 'localhost',
      port: testPort,
      autoReconnect: false,
    })

    client.connect()

    while (!client.getStatus().connected) {
      await new Promise((resolve) => setTimeout(resolve, 30))
    }

    expect(
      client.sendRequest('SetCurrentPreviewScene', {}),
    ).rejects.toThrow()

    client.dispose()
  })
})
