/**
 * @module @slices/automation
 * @description Fatia de Automação Reativa Baseada em Grafo (DAG) do LumenLite.
 *
 * ## Responsabilidades:
 * - Executar pipelines desacoplados de automação externa disparados por eventos litúrgicos da FSM.
 * - Integração com OBS Studio WebSocket v5 e interfaces HTTP/DMX para iluminação cênica.
 * - Persistência do arquivo declarativo `automation.json` com validação Zod v4 e JSON Schema.
 * - Interface gráfica para composição visual de nós com `@xyflow/react`.
 *
 * ## Comunicação e Fronteiras:
 * - **Escuta:** Eventos assíncronos do `LumenEventBus` emitidos pelo `@lumen/core`.
 * - **Despacha:** Comandos de rede para serviços externos (OBS, Lumikit, APIs HTTP) em thread não-bloqueante.
 * - **Exporta para UI:** Editor visual de automações e nós do grafo de execução.
 */

export type {
  AutomationNodeType,
  AutomationNode,
  AutomationEdge,
  AutomationGraph,
} from './contracts/automation-contracts'

export {
  AutomationNodeTypeSchema,
  AutomationNodeSchema,
  AutomationEdgeSchema,
  AutomationGraphSchema,
} from './contracts/automation-contracts'
