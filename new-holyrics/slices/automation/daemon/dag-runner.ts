/**
 * @module @slices/automation/daemon/dag-runner
 * @description Motor de execução de grafos acíclicos dirigidos (DAG) para automações em tempo real.
 *
 * ## Responsabilidades:
 * - Avaliar eventos da projeção e disparar árvores de automação de forma assíncrona e não bloqueante (0ms de impacto na FSM).
 * - Executar nós de condição e ramificações concorrentes com `Promise.allSettled`.
 * - Despachar chamadas para OBS Studio WebSocket v5, requisições HTTP REST externas e comandos internos da FSM.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos para o OBS Studio via `ObsClient`, requisições HTTP e comandos para a FSM.
 * - **Consome:** Notificações de mudança de estado do `LumenEventBus` ou requisições de webhooks.
 */

import { createLogger, type ScreenState, type ActionCommand } from '@lumen/core'
import type {
  AutomationGraph,
  AutomationNode,
  TriggerType,
} from '../contracts/automation-contracts'
import type { ObsClient } from './obs-client'
import type { AutomationRepository } from './automation-repository'

const logger = createLogger('dag-runner')

export interface AutomationExecutionContext {
  triggerType: TriggerType
  state: ScreenState
  previousState?: ScreenState
  webhookTriggerId?: string
}

export interface ExecutionHistoryEntry {
  id: string
  timestamp: number
  triggerType: TriggerType
  nodesExecuted: number
  status: 'success' | 'error' | 'skipped'
  details: string
}

/**
 * Executor assíncrono de grafos acíclicos dirigidos (DAG) para automação desacoplada da projeção.
 */
export class DagRunner {
  private repository: AutomationRepository
  private obsClient: ObsClient | null
  private fsmDispatch: ((command: ActionCommand) => void) | null
  private history: ExecutionHistoryEntry[] = []

  /**
   * Inicializa o executor DAG com repositório de grafo e clientes de destino.
   *
   * @param repository - Repositório para consulta das regras ativas.
   * @param obsClient - Cliente opcional do OBS Studio.
   * @param fsmDispatch - Despachante opcional de comandos da FSM interna.
   */
  constructor(
    repository: AutomationRepository,
    obsClient: ObsClient | null = null,
    fsmDispatch: ((command: ActionCommand) => void) | null = null,
  ) {
    this.repository = repository
    this.obsClient = obsClient
    this.fsmDispatch = fsmDispatch
  }

  public getHistory(): ExecutionHistoryEntry[] {
    return [...this.history]
  }

  public clearHistory(): void {
    this.history = []
  }

  private recordHistory(entry: Omit<ExecutionHistoryEntry, 'id' | 'timestamp'>): void {
    this.history.unshift({
      id: `exec-${Date.now()}-${Math.random().toString(36).slice(2, 6)}`,
      timestamp: Date.now(),
      ...entry,
    })
    if (this.history.length > 50) {
      this.history.length = 50
    }
  }

  /**
   * Dispara a execução de automações associadas a um gatilho específico.
   *
   * @param triggerType - Tipo do evento acionado.
   * @param context - Contexto contendo o estado atual da tela e metadados.
   */
  public async executeTrigger(
    triggerType: TriggerType,
    context: AutomationExecutionContext,
  ): Promise<void> {
    const graph = this.repository.load()
    if (!graph.enabled || graph.nodes.length === 0) {
      this.recordHistory({
        triggerType,
        nodesExecuted: 0,
        status: 'skipped',
        details: 'Motor de automações desativado ou sem nós.',
      })
      return
    }

    const disabledFlowIds = new Set(
      (graph.flows || []).filter((f) => !f.enabled).map((f) => f.id),
    )

    // Localiza todos os nós de gatilho correspondentes
    const matchedTriggers = graph.nodes.filter((node) => {
      if (node.kind !== 'TRIGGER' || !node.triggerConfig) return false
      if (node.flowId && disabledFlowIds.has(node.flowId)) return false
      if (node.triggerConfig.type !== triggerType) return false

      if (triggerType === 'ON_HTTP_WEBHOOK') {
        return node.triggerConfig.webhookTriggerId === context.webhookTriggerId
      }

      return true
    })

    if (matchedTriggers.length === 0) {
      this.recordHistory({
        triggerType,
        nodesExecuted: 0,
        status: 'skipped',
        details: 'Nenhum gatilho ativo correspondente.',
      })
      return
    }

    logger.debug(
      { triggerType, count: matchedTriggers.length },
      'Iniciando execução de fluxos DAG...',
    )

    const trace = { executed: 0, errors: 0 }
    const executionPromises = matchedTriggers.map((triggerNode) =>
      this.executeFromNode(triggerNode.id, graph, context, trace),
    )

    await Promise.allSettled(executionPromises)

    this.recordHistory({
      triggerType,
      nodesExecuted: trace.executed,
      status: trace.errors > 0 ? 'error' : 'success',
      details:
        trace.errors > 0
          ? `${trace.executed} nó(s) executado(s), ${trace.errors} falha(s).`
          : `${trace.executed} nó(s) executado(s) com sucesso.`,
    })
  }

  /**
   * Executa um nó e navega recursivamente pelas suas arestas de saída.
   *
   * @param nodeId - Identificador do nó atual.
   * @param graph - Grafo completo de automação.
   * @param context - Contexto de execução.
   */
  private async executeFromNode(
    nodeId: string,
    graph: AutomationGraph,
    context: AutomationExecutionContext,
    trace?: { executed: number; errors: number },
  ): Promise<void> {
    const currentNode = graph.nodes.find((node) => node.id === nodeId)
    if (!currentNode) return

    let conditionResult: boolean | null = null
    if (trace) trace.executed += 1

    try {
      if (currentNode.kind === 'CONDITION') {
        conditionResult = this.evaluateCondition(currentNode, context)
      } else if (currentNode.kind === 'ACTION_OBS') {
        await this.executeObsAction(currentNode)
      } else if (currentNode.kind === 'ACTION_HTTP') {
        await this.executeHttpAction(currentNode)
      } else if (currentNode.kind === 'ACTION_FSM') {
        this.executeFsmAction(currentNode)
      } else if (currentNode.kind === 'ACTION_DELAY') {
        const delayMs = currentNode.actionDelayConfig?.durationMs ?? 100
        await new Promise((resolve) => setTimeout(resolve, delayMs))
      }
    } catch (nodeExecutionError) {
      if (trace) trace.errors += 1
      logger.error(
        { error: nodeExecutionError, nodeId: currentNode.id, kind: currentNode.kind },
        'Falha não fatal na execução de nó DAG',
      )
    }

    // Encontra as arestas de saída deste nó
    const outgoingEdges = graph.edges.filter((edge) => {
      if (edge.source !== nodeId) return false

      if (currentNode.kind === 'CONDITION' && conditionResult !== null) {
        if (edge.conditionOutcome === 'always') return true
        return edge.conditionOutcome === String(conditionResult)
      }

      return true
    })

    if (outgoingEdges.length > 0) {
      const childPromises = outgoingEdges.map((edge) =>
        this.executeFromNode(edge.target, graph, context, trace),
      )
      await Promise.allSettled(childPromises)
    }
  }

  /**
   * Avalia um nó condicional comparando os campos do contexto.
   *
   * @param node - Nó de condição com operador e valor esperado.
   * @param context - Contexto contendo o estado da projeção.
   * @returns Booleano indicando se a condição foi satisfeita.
   */
  private evaluateCondition(
    node: AutomationNode,
    context: AutomationExecutionContext,
  ): boolean {
    const config = node.conditionConfig
    if (!config) return true

    let actualValue = ''

    if (config.field === 'mode') {
      actualValue = context.state.mode
    } else if (config.field === 'slideIndex') {
      actualValue = String(context.state.foreground.slideIndex)
    } else if (config.field === 'tag' || config.field === 'content') {
      actualValue = context.state.foreground.content.join(' ')
    } else if (config.field === 'panic') {
      const isAnyPanic =
        context.state.panic.blackout ||
        context.state.panic.clearText ||
        context.state.panic.clearBg ||
        context.state.panic.freeze
      actualValue = String(isAnyPanic)
    }

    const expectedValue = config.value

    switch (config.operator) {
      case 'equals':
        return actualValue.toLowerCase() === expectedValue.toLowerCase()
      case 'not_equals':
        return actualValue.toLowerCase() !== expectedValue.toLowerCase()
      case 'contains':
        return actualValue.toLowerCase().includes(expectedValue.toLowerCase())
      case 'not_contains':
        return !actualValue.toLowerCase().includes(expectedValue.toLowerCase())
      case 'starts_with':
        return actualValue.toLowerCase().startsWith(expectedValue.toLowerCase())
      case 'greater_than':
        return Number(actualValue) > Number(expectedValue)
      case 'less_than':
        return Number(actualValue) < Number(expectedValue)
      default:
        return false
    }
  }

  /**
   * Executa uma ação direcionada ao OBS Studio v5.
   *
   * @param node - Nó com parâmetros do OBS.
   */
  private async executeObsAction(node: AutomationNode): Promise<void> {
    if (!this.obsClient || !this.obsClient.getStatus().connected) {
      logger.debug('Ação OBS ignorada: OBS Studio desconectado')
      return
    }

    const config = node.actionObsConfig
    if (!config) return

    const requestPayload: Record<string, unknown> = {}

    if (config.sceneName) {
      requestPayload.sceneName = config.sceneName
    }
    if (config.sceneItemId !== undefined) {
      requestPayload.sceneItemId = config.sceneItemId
    }
    if (config.sceneItemEnabled !== undefined) {
      requestPayload.sceneItemEnabled = config.sceneItemEnabled
    }
    if (config.inputName) {
      requestPayload.inputName = config.inputName
    }
    if (config.inputMuted !== undefined) {
      requestPayload.inputMuted = config.inputMuted
    }

    logger.info({ operation: config.requestType, payload: requestPayload }, 'Despachando comando OBS v5')
    await this.obsClient.sendRequest(config.requestType, requestPayload, 1000)
  }

  /**
   * Executa uma chamada HTTP externa protegida por timeout estrito (RNF-04).
   *
   * @param node - Nó com URL, método e timeout.
   */
  private async executeHttpAction(node: AutomationNode): Promise<void> {
    const config = node.actionHttpConfig
    if (!config) return

    const timeout = config.timeoutMs || 500
    const controller = new AbortController()
    const timer = setTimeout(() => controller.abort(), timeout)

    try {
      logger.info({ url: config.url, method: config.method }, 'Disparando webhook HTTP externo')
      const response = await fetch(config.url, {
        method: config.method,
        headers: {
          'Content-Type': 'application/json',
          ...(config.headers || {}),
        },
        body: config.body && config.method !== 'GET' ? config.body : undefined,
        signal: controller.signal,
      })

      logger.debug({ status: response.status }, 'Resposta do webhook HTTP recebida')
    } finally {
      clearTimeout(timer)
    }
  }

  /**
   * Executa um comando na FSM da projeção interna.
   *
   * @param node - Nó com tipo de comando e parâmetros.
   */
  private executeFsmAction(node: AutomationNode): void {
    if (!this.fsmDispatch) return

    const config = node.actionFsmConfig
    if (!config) return

    if (config.commandType === 'TOGGLE_PANIC' && config.panicTarget) {
      this.fsmDispatch({
        type: 'TOGGLE_PANIC',
        target: config.panicTarget,
      })
    } else if (config.commandType === 'NAV_NEXT') {
      this.fsmDispatch({ type: 'NAV_NEXT' })
    } else if (config.commandType === 'NAV_PREV') {
      this.fsmDispatch({ type: 'NAV_PREV' })
    } else if (config.commandType === 'CLEAR_SCREEN') {
      this.fsmDispatch({ type: 'CLEAR_SCREEN' })
    }
  }
}
