import { describe, it, expect, beforeAll, afterAll } from 'bun:test'
import { join } from 'node:path'
import { unlinkSync, existsSync } from 'node:fs'
import type { ScreenState, ActionCommand } from '@lumen/core'
import { INITIAL_SCREEN_STATE } from '@lumen/core'
import { DagRunner } from '../dag-runner'
import { AutomationRepository } from '../automation-repository'
import type { AutomationGraph } from '../../contracts/automation-contracts'

describe('DagRunner (slices/automation/daemon)', () => {
  const testJsonPath = join(process.cwd(), 'slices/automation/daemon/__tests__/dag-test.json')
  let repository: AutomationRepository
  let mockHttpServer: ReturnType<typeof Bun.serve>
  let httpRequestsReceived: string[] = []

  beforeAll(() => {
    mockHttpServer = Bun.serve({
      port: 4567,
      async fetch(req) {
        const url = new URL(req.url)
        httpRequestsReceived.push(url.pathname)
        return new Response(JSON.stringify({ ok: true }))
      },
    })
  })

  afterAll(() => {
    mockHttpServer.stop()
    if (existsSync(testJsonPath)) {
      unlinkSync(testJsonPath)
    }
  })

  it('executa ramificação condicional com sucesso (True branch executado, False branch ignorado)', async () => {
    repository = new AutomationRepository(testJsonPath)

    const dispatchedCommands: ActionCommand[] = []
    const mockDispatch = (command: ActionCommand) => {
      dispatchedCommands.push(command)
    }

    const testGraph: AutomationGraph = {
      version: 1,
      enabled: true,
      obsConnection: { host: 'localhost', port: 4455, autoReconnect: false },
      nodes: [
        {
          id: 'trig-1',
          kind: 'TRIGGER',
          label: 'Slide Alterado',
          position: { x: 0, y: 0 },
          triggerConfig: { type: 'ON_SLIDE_CHANGE' },
        },
        {
          id: 'cond-1',
          kind: 'CONDITION',
          label: 'É Bíblia?',
          position: { x: 100, y: 0 },
          conditionConfig: { field: 'mode', operator: 'equals', value: 'BIBLE' },
        },
        {
          id: 'act-true',
          kind: 'ACTION_FSM',
          label: 'Ação True (Next)',
          position: { x: 200, y: -50 },
          actionFsmConfig: { commandType: 'NAV_NEXT' },
        },
        {
          id: 'act-false',
          kind: 'ACTION_FSM',
          label: 'Ação False (Prev)',
          position: { x: 200, y: 50 },
          actionFsmConfig: { commandType: 'NAV_PREV' },
        },
      ],
      edges: [
        { id: 'e1', source: 'trig-1', target: 'cond-1', conditionOutcome: 'always' },
        { id: 'e2', source: 'cond-1', target: 'act-true', conditionOutcome: 'true' },
        { id: 'e3', source: 'cond-1', target: 'act-false', conditionOutcome: 'false' },
      ],
    }

    repository.save(testGraph)
    const runner = new DagRunner(repository, null, mockDispatch)

    // Contexto com mode: 'BIBLE'
    const bibleState: ScreenState = {
      ...INITIAL_SCREEN_STATE,
      mode: 'BIBLE',
    }

    await runner.executeTrigger('ON_SLIDE_CHANGE', {
      triggerType: 'ON_SLIDE_CHANGE',
      state: bibleState,
    })

    expect(dispatchedCommands.length).toBe(1)
    expect(dispatchedCommands[0].type).toBe('NAV_NEXT')
  })

  it('dispara requisição HTTP externa com sucesso e respeita timeout', async () => {
    httpRequestsReceived = []

    const testGraph: AutomationGraph = {
      version: 1,
      enabled: true,
      obsConnection: { host: 'localhost', port: 4455, autoReconnect: false },
      nodes: [
        {
          id: 'trig-webhook',
          kind: 'TRIGGER',
          label: 'Trigger Webhook',
          position: { x: 0, y: 0 },
          triggerConfig: { type: 'ON_HTTP_WEBHOOK', webhookTriggerId: 'dmx-lights' },
        },
        {
          id: 'act-http',
          kind: 'ACTION_HTTP',
          label: 'Chama API de Luzes',
          position: { x: 100, y: 0 },
          actionHttpConfig: {
            url: 'http://localhost:4567/api/dmx/preset-1',
            method: 'POST',
            timeoutMs: 800,
          },
        },
      ],
      edges: [{ id: 'e1', source: 'trig-webhook', target: 'act-http', conditionOutcome: 'always' }],
    }

    repository.save(testGraph)
    const runner = new DagRunner(repository, null, null)

    await runner.executeTrigger('ON_HTTP_WEBHOOK', {
      triggerType: 'ON_HTTP_WEBHOOK',
      state: INITIAL_SCREEN_STATE,
      webhookTriggerId: 'dmx-lights',
    })

    expect(httpRequestsReceived.length).toBe(1)
    expect(httpRequestsReceived[0]).toBe('/api/dmx/preset-1')
  })

  it('não trava o executor quando chamada HTTP externa expira ou falha (RNF-04)', async () => {
    const testGraph: AutomationGraph = {
      version: 1,
      enabled: true,
      obsConnection: { host: 'localhost', port: 4455, autoReconnect: false },
      nodes: [
        {
          id: 'trig-panic',
          kind: 'TRIGGER',
          label: 'Trigger Panic',
          position: { x: 0, y: 0 },
          triggerConfig: { type: 'ON_PANIC' },
        },
        {
          id: 'act-unreachable',
          kind: 'ACTION_HTTP',
          label: 'Servidor Inexistente',
          position: { x: 100, y: 0 },
          actionHttpConfig: {
            url: 'http://127.0.0.1:59999/unreachable',
            method: 'POST',
            timeoutMs: 100,
          },
        },
      ],
      edges: [{ id: 'e1', source: 'trig-panic', target: 'act-unreachable', conditionOutcome: 'always' }],
    }

    repository.save(testGraph)
    const runner = new DagRunner(repository, null, null)

    // Deve resolver sem estourar exceção não tratada
    await expect(
      runner.executeTrigger('ON_PANIC', {
        triggerType: 'ON_PANIC',
        state: INITIAL_SCREEN_STATE,
      }),
    ).resolves.toBeUndefined()
  })
})
