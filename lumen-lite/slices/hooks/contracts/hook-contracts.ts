import { z } from 'zod'

export const HookNodeTypeSchema = z.enum([
  'trigger_transition',
  'trigger_panic',
  'transform_regex',
  'filter_diff',
  'action_obs_scene',
  'action_http_request',
])

export const HookNodeSchema = z.object({
  id: z.string(),
  type: HookNodeTypeSchema,
  label: z.string(),
  config: z.record(z.unknown()),
})

export type HookNode = z.infer<typeof HookNodeSchema>

export const HookEdgeSchema = z.object({
  id: z.string(),
  source: z.string(),
  target: z.string(),
})

export type HookEdge = z.infer<typeof HookEdgeSchema>

export const HookGraphSchema = z.object({
  version: z.number().default(1),
  enabled: z.boolean().default(true),
  nodes: z.array(HookNodeSchema),
  edges: z.array(HookEdgeSchema),
})

export type HookGraph = z.infer<typeof HookGraphSchema>
