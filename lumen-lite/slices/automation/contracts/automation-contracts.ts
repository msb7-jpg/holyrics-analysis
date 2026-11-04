/**
 * @module @slices/automation/contracts
 * @description Esquemas Zod v4 e interfaces tipadas para o motor de automações por grafo (DAG) do LumenLite.
 *
 * ## Responsabilidades:
 * - Definir schemas e tipos para nós, arestas e grafo de automações (`automation.json`).
 * - Validar nós de gatilho, transformação de dados, filtros condicionais e ações externas (OBS, DMX, HTTP).
 *
 * ## Comunicação e Fronteiras:
 * - Utilizado pelo executor DAG do backend e pelo editor visual de automações no frontend (`@xyflow/react`).
 */

import { z } from 'zod'

export const AutomationNodeTypeSchema = z.enum([
  'trigger_transition',
  'trigger_panic',
  'transform_regex',
  'filter_diff',
  'action_obs_scene',
  'action_http_request',
])

export type AutomationNodeType = z.infer<typeof AutomationNodeTypeSchema>

export const AutomationNodeSchema = z.object({
  id: z.string(),
  type: AutomationNodeTypeSchema,
  label: z.string(),
  config: z.record(z.string(), z.unknown()),
})

export type AutomationNode = z.infer<typeof AutomationNodeSchema>

export const AutomationEdgeSchema = z.object({
  id: z.string(),
  source: z.string(),
  target: z.string(),
})

export type AutomationEdge = z.infer<typeof AutomationEdgeSchema>

export const AutomationGraphSchema = z.object({
  version: z.number().default(1),
  enabled: z.boolean().default(true),
  nodes: z.array(AutomationNodeSchema),
  edges: z.array(AutomationEdgeSchema),
})

export type AutomationGraph = z.infer<typeof AutomationGraphSchema>
