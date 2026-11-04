/**
 * @module @slices/automation
 * @description Fatia de Automação Reativa Baseada em Grafo (DAG) do LumenLite.
 *
 * ## Responsabilidades:
 * - Executar pipelines desacoplados de automação externa disparados por eventos litúrgicos da FSM.
 * - Integração com OBS Studio WebSocket v5 e interfaces HTTP/DMX para iluminação cênica.
 * - Persistência atômica do arquivo declarativo `automation.json` com validação Zod v4 e sem ciclos.
 * - Interface gráfica para composição visual de nós com `@xyflow/react`.
 *
 * ## Comunicação e Fronteiras:
 * - **Escuta:** Eventos assíncronos do `LumenEventBus` emitidos pelo `@lumen/core`.
 * - **Despacha:** Comandos de rede para serviços externos (OBS, Lumikit, APIs HTTP) em thread não-bloqueante (0ms de impacto na FSM).
 * - **Exporta para UI:** Editor visual de automações e nós do grafo de execução.
 */

export type {
  TriggerType,
  ConditionOperator,
  ObsRequestType,
  ObsConnectionConfig,
  ActionObsPayload,
  ActionHttpPayload,
  ActionFsmPayload,
  ActionDelayPayload,
  AutomationNodeKind,
  AutomationNode,
  AutomationEdge,
  AutomationGraph,
} from './contracts/automation-contracts'

export {
  TriggerTypeSchema,
  ConditionOperatorSchema,
  ObsRequestTypeSchema,
  ObsConnectionConfigSchema,
  ActionObsPayloadSchema,
  ActionHttpPayloadSchema,
  ActionFsmPayloadSchema,
  ActionDelayPayloadSchema,
  AutomationNodeKindSchema,
  AutomationNodeSchema,
  AutomationEdgeSchema,
  AutomationGraphSchema,
  validateGraphAcyclic,
  DEFAULT_AUTOMATION_GRAPH,
} from './contracts/automation-contracts'

export { ObsClient } from './daemon/obs-client'
export type { ObsClientStatus } from './daemon/obs-client'

export { AutomationRepository } from './daemon/automation-repository'
export { DagRunner } from './daemon/dag-runner'
export type { AutomationExecutionContext } from './daemon/dag-runner'

export { handleAutomationRoutes } from './daemon/routes/automation-router'

export { TriggerNode, ConditionNode, ActionNode } from './ui/components/CustomNodes'
export { AutomationCanvas } from './ui/components/AutomationCanvas'
export type { AutomationCanvasProps } from './ui/components/AutomationCanvas'

export { AutomationModal } from './ui/components/AutomationModal'
export type { AutomationModalProps } from './ui/components/AutomationModal'
