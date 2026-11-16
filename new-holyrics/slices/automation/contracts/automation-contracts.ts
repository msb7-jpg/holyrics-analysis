/**
 * @module @slices/automation/contracts/automation-contracts
 * @description Esquemas Zod v4 e interfaces tipadas para o motor de automações por grafo acíclico dirigido (DAG) do LumenLite.
 *
 * ## Responsabilidades:
 * - Definir contratos isomórficos de nós de gatilho (Triggers), condições (Filters) e ações (Actions).
 * - Suportar integração nativa com OBS Studio WebSocket v5 (cenas, fontes, áudio) e chamadas HTTP REST (DMX/mesas).
 * - Garantir validação estrita do formato `automation.json` e detecção de ciclos no grafo.
 *
 * ## Comunicação e Fronteiras:
 * - Compartilhado entre o runner assíncrono do backend (`dag-runner.ts`) e o editor visual da interface (`@xyflow/react`).
 */

import { z } from 'zod'

/**
 * Tipos de gatilhos suportados pelo motor de automação.
 */
export const TriggerTypeSchema = z.enum([
  'ON_SLIDE_CHANGE',
  'ON_ITEM_SELECT',
  'ON_PANIC',
  'ON_HTTP_WEBHOOK',
])
export type TriggerType = z.infer<typeof TriggerTypeSchema>

/**
 * Operadores para nós de condição e filtro.
 */
export const ConditionOperatorSchema = z.enum([
  'equals',
  'not_equals',
  'contains',
  'not_contains',
  'starts_with',
  'regex',
  'greater_than',
  'less_than',
])
export type ConditionOperator = z.infer<typeof ConditionOperatorSchema>

/**
 * Operações suportadas no protocolo OBS Studio WebSocket v5.
 */
export const ObsRequestTypeSchema = z.enum([
  'SetCurrentProgramScene',
  'SetCurrentPreviewScene',
  'SetSceneItemEnabled',
  'SetInputMute',
  'ToggleInputMute',
  'StartStream',
  'StopStream',
  'StartRecord',
  'StopRecord',
])
export type ObsRequestType = z.infer<typeof ObsRequestTypeSchema>

/**
 * Configuração de conexão com OBS WebSocket v5.
 */
export const ObsConnectionConfigSchema = z.object({
  host: z.string().default('localhost'),
  port: z.number().int().min(1).max(65535).default(4455),
  password: z.string().optional(),
  autoReconnect: z.boolean().default(true),
})
export type ObsConnectionConfig = z.infer<typeof ObsConnectionConfigSchema>

/**
 * Carga de configuração para cada tipo de nó de ação.
 */
export const ActionObsPayloadSchema = z.object({
  requestType: ObsRequestTypeSchema,
  sceneName: z.string().optional(),
  sceneItemId: z.number().optional(),
  sceneItemEnabled: z.boolean().optional(),
  inputName: z.string().optional(),
  inputMuted: z.boolean().optional(),
})
export type ActionObsPayload = z.infer<typeof ActionObsPayloadSchema>

export const ActionHttpPayloadSchema = z.object({
  url: z.string().url(),
  method: z.enum(['GET', 'POST', 'PUT', 'DELETE']).default('POST'),
  headers: z.record(z.string(), z.string()).optional(),
  body: z.string().optional(),
  timeoutMs: z.number().int().min(50).max(10000).default(500),
})
export type ActionHttpPayload = z.infer<typeof ActionHttpPayloadSchema>

export const ActionFsmPayloadSchema = z.object({
  commandType: z.enum(['NAV_NEXT', 'NAV_PREV', 'CLEAR_SCREEN', 'TOGGLE_PANIC', 'SET_BACKGROUND']),
  panicTarget: z.enum(['blackout', 'clearText', 'clearBg', 'freeze']).optional(),
  backgroundSource: z.string().optional(),
})
export type ActionFsmPayload = z.infer<typeof ActionFsmPayloadSchema>

export const ActionDelayPayloadSchema = z.object({
  durationMs: z.number().int().min(1).max(60000).default(500),
})
export type ActionDelayPayload = z.infer<typeof ActionDelayPayloadSchema>

/**
 * Categoria e discriminador do nó no DAG.
 */
export const AutomationNodeKindSchema = z.enum([
  'TRIGGER',
  'CONDITION',
  'ACTION_OBS',
  'ACTION_HTTP',
  'ACTION_FSM',
  'ACTION_DELAY',
])
export type AutomationNodeKind = z.infer<typeof AutomationNodeKindSchema>

/**
 * Definição atômica de um nó no grafo de automação.
 */
export const AutomationNodeSchema = z.object({
  id: z.string().min(1),
  flowId: z.string().optional(),
  kind: AutomationNodeKindSchema,
  label: z.string().min(1),
  position: z.object({
    x: z.number().default(0),
    y: z.number().default(0),
  }).default({ x: 0, y: 0 }),
  // Configuração específica baseada no tipo de nó
  triggerConfig: z.object({
    type: TriggerTypeSchema,
    webhookTriggerId: z.string().optional(),
  }).optional(),
  conditionConfig: z.object({
    field: z.string().default('mode'), // 'mode', 'content', 'slideIndex', 'tag'
    operator: ConditionOperatorSchema.default('equals'),
    value: z.string().default('SONG'),
  }).optional(),
  actionObsConfig: ActionObsPayloadSchema.optional(),
  actionHttpConfig: ActionHttpPayloadSchema.optional(),
  actionFsmConfig: ActionFsmPayloadSchema.optional(),
  actionDelayConfig: ActionDelayPayloadSchema.optional(),
})
export type AutomationNode = z.infer<typeof AutomationNodeSchema>

/**
 * Aresta direcionada conectando dois nós do grafo.
 */
export const AutomationEdgeSchema = z.object({
  id: z.string().min(1),
  source: z.string().min(1),
  target: z.string().min(1),
  conditionOutcome: z.enum(['true', 'false', 'always']).default('always'),
})
export type AutomationEdge = z.infer<typeof AutomationEdgeSchema>

export const AutomationFlowSchema = z.object({
  id: z.string().min(1),
  name: z.string().min(1),
  enabled: z.boolean().default(true),
})
export type AutomationFlow = z.infer<typeof AutomationFlowSchema>

/**
 * Grafo completo de automação serializável em `automation.json`.
 */
export const AutomationGraphSchema = z.object({
  $schema: z.string().optional(),
  version: z.number().default(1),
  enabled: z.boolean().default(true),
  flows: z.array(AutomationFlowSchema).optional(),
  obsConnection: ObsConnectionConfigSchema.default({
    host: 'localhost',
    port: 4455,
    autoReconnect: true,
  }),
  nodes: z.array(AutomationNodeSchema),
  edges: z.array(AutomationEdgeSchema),
})
export type AutomationGraph = z.infer<typeof AutomationGraphSchema>

/**
 * Validador de aciclicidade (DAG) utilizando Busca em Profundidade (DFS).
 *
 * @param nodes - Lista de nós do grafo.
 * @param edges - Lista de arestas direcionadas entre os nós.
 * @returns Objeto com booleano indicando validade e mensagem descritiva caso haja ciclo.
 */
export function validateGraphAcyclic(
  nodes: AutomationNode[],
  edges: AutomationEdge[],
): { isValid: boolean; error?: string } {
  const nodeIds = new Set(nodes.map((node) => node.id))
  const adjacency = new Map<string, string[]>()

  for (const nodeId of nodeIds) {
    adjacency.set(nodeId, [])
  }

  for (const edge of edges) {
    if (!nodeIds.has(edge.source)) {
      return { isValid: false, error: `Aresta aponta para nó de origem inexistente: ${edge.source}` }
    }
    if (!nodeIds.has(edge.target)) {
      return { isValid: false, error: `Aresta aponta para nó de destino inexistente: ${edge.target}` }
    }
    if (edge.source === edge.target) {
      return { isValid: false, error: `Auto-dependência detectada no nó: ${edge.source}` }
    }
    const currentTargets = adjacency.get(edge.source)
    if (currentTargets) {
      currentTargets.push(edge.target)
    }
  }

  // 0 = não visitado, 1 = visitando na pilha atual, 2 = visitado e concluído
  const visitState = new Map<string, number>()
  for (const nodeId of nodeIds) {
    visitState.set(nodeId, 0)
  }

  function hasCycle(currentId: string): boolean {
    visitState.set(currentId, 1)

    const neighbors = adjacency.get(currentId) || []
    for (const neighborId of neighbors) {
      const neighborState = visitState.get(neighborId) || 0
      if (neighborState === 1) {
        return true // Ciclo encontrado!
      }
      if (neighborState === 0) {
        if (hasCycle(neighborId)) {
          return true
        }
      }
    }

    visitState.set(currentId, 2)
    return false
  }

  for (const nodeId of nodeIds) {
    if ((visitState.get(nodeId) || 0) === 0) {
      if (hasCycle(nodeId)) {
        return { isValid: false, error: `Ciclo detectado no grafo contendo o nó: ${nodeId}` }
      }
    }
  }

  return { isValid: true }
}

/**
 * Grafo padrão inicial caso `automation.json` ainda não exista.
 */
export const DEFAULT_AUTOMATION_GRAPH: AutomationGraph = {
  version: 1,
  enabled: true,
  obsConnection: {
    host: 'localhost',
    port: 4455,
    autoReconnect: true,
  },
  nodes: [
    {
      id: 'trigger-slide',
      kind: 'TRIGGER',
      label: 'Troca de Slide',
      position: { x: 50, y: 100 },
      triggerConfig: {
        type: 'ON_SLIDE_CHANGE',
      },
    },
    {
      id: 'filter-mode-song',
      kind: 'CONDITION',
      label: 'Modo é Louvor?',
      position: { x: 280, y: 100 },
      conditionConfig: {
        field: 'mode',
        operator: 'equals',
        value: 'SONG',
      },
    },
    {
      id: 'action-obs-worship',
      kind: 'ACTION_OBS',
      label: 'OBS: Cena Louvor',
      position: { x: 520, y: 60 },
      actionObsConfig: {
        requestType: 'SetCurrentProgramScene',
        sceneName: 'Cena Louvor',
      },
    },
    {
      id: 'action-obs-preacher',
      kind: 'ACTION_OBS',
      label: 'OBS: Cena Palco',
      position: { x: 520, y: 160 },
      actionObsConfig: {
        requestType: 'SetCurrentProgramScene',
        sceneName: 'Cena Pastor',
      },
    },
  ],
  edges: [
    {
      id: 'edge-1',
      source: 'trigger-slide',
      target: 'filter-mode-song',
      conditionOutcome: 'always',
    },
    {
      id: 'edge-2',
      source: 'filter-mode-song',
      target: 'action-obs-worship',
      conditionOutcome: 'true',
    },
    {
      id: 'edge-3',
      source: 'filter-mode-song',
      target: 'action-obs-preacher',
      conditionOutcome: 'false',
    },
  ],
}
