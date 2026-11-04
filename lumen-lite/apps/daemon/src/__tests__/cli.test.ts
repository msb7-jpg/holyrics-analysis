import { describe, it, expect, beforeAll, afterAll } from 'bun:test'
import { spawn } from 'node:child_process'
import { resolve } from 'node:path'

describe('LumenLite Headless CLI (apps/daemon/src/cli.ts)', () => {
  const cliPath = resolve(import.meta.dir, '../cli.ts')
  let mockDaemonServer: ReturnType<typeof Bun.serve>
  const mockPort = 4991
  const dispatchedTypes: string[] = []

  beforeAll(() => {
    mockDaemonServer = Bun.serve({
      port: mockPort,
      async fetch(req) {
        const url = new URL(req.url)

        if (url.pathname === '/api/status') {
          return Response.json({
            ok: true,
            state: { mode: 'SONG', panic: { blackout: false, clearText: false, freeze: false } },
            clients: 1,
            songsCount: 42,
          })
        }

        if (url.pathname === '/api/fsm/dispatch' && req.method === 'POST') {
          const body = (await req.json()) as { command: { type: string } }
          dispatchedTypes.push(body.command.type)
          return Response.json({
            ok: true,
            data: { mode: 'SONG', foreground: { slideIndex: 1 } },
          })
        }

        if (url.pathname === '/api/keymap') {
          return Response.json({
            ok: true,
            data: {
              version: 1,
              bindings: [
                { id: 'nav-next', action: 'NAV_NEXT', combo: 'Space', description: 'Next slide' },
              ],
            },
          })
        }

        return new Response('Not Found', { status: 404 })
      },
    })
  })

  afterAll(() => {
    mockDaemonServer.stop()
  })

  function runCli(args: string[]): Promise<{ stdout: string; stderr: string; code: number }> {
    return new Promise((resolveResult) => {
      const child = spawn('bun', [cliPath, ...args, '--url', `http://localhost:${mockPort}`])
      let stdout = ''
      let stderr = ''

      child.stdout.on('data', (data) => {
        stdout += data.toString()
      })
      child.stderr.on('data', (data) => {
        stderr += data.toString()
      })
      child.on('close', (code) => {
        resolveResult({ stdout, stderr, code: code ?? 0 })
      })
    })
  }

  it('exibe ajuda detalhada com lumen --help', async () => {
    const { stdout, code } = await runCli(['--help'])
    expect(code).toBe(0)
    expect(stdout).toContain('LumenLite CLI')
    expect(stdout).toContain('COMANDOS DIRETOS')
    expect(stdout).toContain('SUBCOMANDOS POR FATIA')
  })

  it('despacha comando "next" via POST /api/fsm/dispatch', async () => {
    const { stdout, code } = await runCli(['next'])
    expect(code).toBe(0)
    expect(stdout).toContain('NAV_NEXT')
    expect(dispatchedTypes).toContain('NAV_NEXT')
  })

  it('despacha comando "blackout" com flag --json', async () => {
    const { stdout, code } = await runCli(['blackout', '--json'])
    expect(code).toBe(0)
    const json = JSON.parse(stdout)
    expect(json.ok).toBe(true)
    expect(dispatchedTypes).toContain('TOGGLE_PANIC')
  })

  it('consulta telemetria com lumen status --json', async () => {
    const { stdout, code } = await runCli(['status', '--json'])
    expect(code).toBe(0)
    const json = JSON.parse(stdout)
    expect(json.ok).toBe(true)
    expect(json.state.mode).toBe('SONG')
    expect(json.songsCount).toBe(42)
  })

  it('lista atalhos com lumen keymap list --json', async () => {
    const { stdout, code } = await runCli(['keymap', 'list', '--json'])
    expect(code).toBe(0)
    const json = JSON.parse(stdout)
    expect(json.ok).toBe(true)
    expect(json.data.bindings.length).toBe(1)
    expect(json.data.bindings[0].action).toBe('NAV_NEXT')
  })
})
