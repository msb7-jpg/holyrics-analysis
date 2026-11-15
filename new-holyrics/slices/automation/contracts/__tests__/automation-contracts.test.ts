import { describe, it, expect } from 'bun:test'
import {
  AutomationGraphSchema,
  DEFAULT_AUTOMATION_GRAPH,
  validateGraphAcyclic,
  type AutomationNode,
  type AutomationEdge,
} from '../automation-contracts'

describe('Automation Contracts & DAG Validation (slices/automation)', () => {
  it('valida o grafo padrão inicial com o Zod v4 Schema', () => {
    const parseResult = AutomationGraphSchema.safeParse(DEFAULT_AUTOMATION_GRAPH)
    expect(parseResult.success).toBe(true)

    const acyclicCheck = validateGraphAcyclic(
      DEFAULT_AUTOMATION_GRAPH.nodes,
      DEFAULT_AUTOMATION_GRAPH.edges,
    )
    expect(acyclicCheck.isValid).toBe(true)
    expect(acyclicCheck.error).toBeUndefined()
  })

  it('permite um grafo linear sem ciclos (A -> B -> C)', () => {
    const nodes: AutomationNode[] = [
      { id: 'node-a', kind: 'TRIGGER', label: 'Gatilho A', position: { x: 0, y: 0 } },
      { id: 'node-b', kind: 'CONDITION', label: 'Condição B', position: { x: 100, y: 0 } },
      { id: 'node-c', kind: 'ACTION_HTTP', label: 'Ação C', position: { x: 200, y: 0 } },
    ]
    const edges: AutomationEdge[] = [
      { id: 'edge-ab', source: 'node-a', target: 'node-b', conditionOutcome: 'always' },
      { id: 'edge-bc', source: 'node-b', target: 'node-c', conditionOutcome: 'true' },
    ]

    const result = validateGraphAcyclic(nodes, edges)
    expect(result.isValid).toBe(true)
  })

  it('detecta e rejeita auto-dependência (A -> A)', () => {
    const nodes: AutomationNode[] = [
      { id: 'node-a', kind: 'TRIGGER', label: 'Gatilho A', position: { x: 0, y: 0 } },
    ]
    const edges: AutomationEdge[] = [
      { id: 'edge-loop', source: 'node-a', target: 'node-a', conditionOutcome: 'always' },
    ]

    const result = validateGraphAcyclic(nodes, edges)
    expect(result.isValid).toBe(false)
    expect(result.error).toContain('Auto-dependência')
  })

  it('detecta e rejeita ciclo direto entre dois nós (A -> B -> A)', () => {
    const nodes: AutomationNode[] = [
      { id: 'node-a', kind: 'TRIGGER', label: 'Gatilho A', position: { x: 0, y: 0 } },
      { id: 'node-b', kind: 'ACTION_OBS', label: 'Ação B', position: { x: 100, y: 0 } },
    ]
    const edges: AutomationEdge[] = [
      { id: 'edge-1', source: 'node-a', target: 'node-b', conditionOutcome: 'always' },
      { id: 'edge-2', source: 'node-b', target: 'node-a', conditionOutcome: 'always' },
    ]

    const result = validateGraphAcyclic(nodes, edges)
    expect(result.isValid).toBe(false)
    expect(result.error).toContain('Ciclo detectado')
  })

  it('detecta e rejeita ciclo indireto em cadeia maior (A -> B -> C -> D -> B)', () => {
    const nodes: AutomationNode[] = [
      { id: 'node-a', kind: 'TRIGGER', label: 'A', position: { x: 0, y: 0 } },
      { id: 'node-b', kind: 'CONDITION', label: 'B', position: { x: 50, y: 0 } },
      { id: 'node-c', kind: 'CONDITION', label: 'C', position: { x: 100, y: 0 } },
      { id: 'node-d', kind: 'ACTION_OBS', label: 'D', position: { x: 150, y: 0 } },
    ]
    const edges: AutomationEdge[] = [
      { id: 'edge-ab', source: 'node-a', target: 'node-b', conditionOutcome: 'always' },
      { id: 'edge-bc', source: 'node-b', target: 'node-c', conditionOutcome: 'always' },
      { id: 'edge-cd', source: 'node-c', target: 'node-d', conditionOutcome: 'always' },
      { id: 'edge-db', source: 'node-d', target: 'node-b', conditionOutcome: 'always' }, // Loop de volta para B!
    ]

    const result = validateGraphAcyclic(nodes, edges)
    expect(result.isValid).toBe(false)
    expect(result.error).toContain('Ciclo detectado')
  })

  it('rejeita arestas que apontem para nós inexistentes', () => {
    const nodes: AutomationNode[] = [
      { id: 'node-a', kind: 'TRIGGER', label: 'A', position: { x: 0, y: 0 } },
    ]
    const edges: AutomationEdge[] = [
      { id: 'edge-fake', source: 'node-a', target: 'node-ghost', conditionOutcome: 'always' },
    ]

    const result = validateGraphAcyclic(nodes, edges)
    expect(result.isValid).toBe(false)
    expect(result.error).toContain('destino inexistente')
  })
})
