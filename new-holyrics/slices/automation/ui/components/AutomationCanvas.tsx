/**
 * @module @slices/automation/ui/components/AutomationCanvas
 * @description Canvas visual de automações baseado em nós (@xyflow/react) no padrão Slate Pro Studio.
 *
 * ## Responsabilidades:
 * - Renderizar o grafo acíclico dirigido (DAG) permitindo arrastar, conectar e reconfigurar nós.
 * - Permitir adicionar novos nós de Gatilho, Condição, Ação OBS Studio e Ação HTTP.
 * - Fornecer feedback visual em tempo real e validação de aciclicidade antes de salvar.
 */

import { useState, useCallback, useMemo } from 'react'
import {
  ReactFlow,
  Controls,
  Background,
  BackgroundVariant,
  applyNodeChanges,
  applyEdgeChanges,
  addEdge,
  type Node,
  type Edge,
  type OnNodesChange,
  type OnEdgesChange,
  type OnConnect,
} from '@xyflow/react'
import '@xyflow/react/dist/style.css'
import { Plus, Save, Play, CheckCircle2, AlertCircle } from 'lucide-react'
import type { AutomationGraph, AutomationNode } from '../../contracts/automation-contracts'
import { validateGraphAcyclic } from '../../contracts/automation-contracts'
import { TriggerNode, ConditionNode, ActionNode } from './CustomNodes'

export interface AutomationCanvasProps {
  initialGraph: AutomationGraph
  obsConnected?: boolean
  onSaveGraph: (graph: AutomationGraph) => Promise<void>
  onTestTrigger?: (triggerType: string) => Promise<void>
}

/**
 * Editor gráfico de nós para regras de automação litúrgica.
 *
 * @param props - Propriedades do componente.
 * @param props.initialGraph - Grafo inicial a ser carregado.
 * @param props.obsConnected - Indicador se o daemon está conectado ao OBS WebSocket.
 * @param props.onSaveGraph - Callback assíncrono para persistir o grafo.
 * @param props.onTestTrigger - Callback opcional para simular disparo de teste.
 * @returns Elemento JSX com o canvas interativo.
 */
export function AutomationCanvas({
  initialGraph,
  obsConnected = false,
  onSaveGraph,
  onTestTrigger,
}: AutomationCanvasProps) {
  // Converte nós do contrato para nós do ReactFlow
  const initialNodes: Node[] = useMemo(() => {
    return initialGraph.nodes.map((node) => ({
      id: node.id,
      type: node.kind === 'TRIGGER'
        ? 'triggerNode'
        : node.kind === 'CONDITION'
        ? 'conditionNode'
        : 'actionNode',
      position: node.position,
      data: node as unknown as Record<string, unknown>,
    }))
  }, [initialGraph])

  // Converte arestas do contrato para arestas do ReactFlow
  const initialEdges: Edge[] = useMemo(() => {
    return initialGraph.edges.map((edge) => ({
      id: edge.id,
      source: edge.source,
      target: edge.target,
      sourceHandle: edge.conditionOutcome !== 'always' ? edge.conditionOutcome : undefined,
      animated: true,
      style: {
        stroke: edge.conditionOutcome === 'true'
          ? '#34d399'
          : edge.conditionOutcome === 'false'
          ? '#f43f5e'
          : '#38bdf8',
        strokeWidth: 2,
      },
    }))
  }, [initialGraph])

  const [nodes, setNodes] = useState<Node[]>(initialNodes)
  const [edges, setEdges] = useState<Edge[]>(initialEdges)
  const [saveStatus, setSaveStatus] = useState<string | null>(null)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)

  const nodeTypes = useMemo(() => ({
    triggerNode: TriggerNode,
    conditionNode: ConditionNode,
    actionNode: ActionNode,
  }), [])

  const onNodesChange: OnNodesChange = useCallback(
    (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
    [],
  )

  const onEdgesChange: OnEdgesChange = useCallback(
    (changes) => setEdges((eds) => applyEdgeChanges(changes, eds)),
    [],
  )

  const onConnect: OnConnect = useCallback(
    (connection) => {
      setEdges((eds) =>
        addEdge(
          {
            ...connection,
            animated: true,
            style: {
              stroke: connection.sourceHandle === 'true'
                ? '#34d399'
                : connection.sourceHandle === 'false'
                ? '#f43f5e'
                : '#38bdf8',
              strokeWidth: 2,
            },
          },
          eds,
        ),
      )
    },
    [],
  )

  // Adicionar novo nó
  const handleAddNode = (kind: AutomationNode['kind']) => {
    const newId = `node-${Date.now()}`
    let newNodeData: AutomationNode

    if (kind === 'TRIGGER') {
      newNodeData = {
        id: newId,
        kind: 'TRIGGER',
        label: 'Troca de Slide',
        position: { x: 80, y: 150 },
        triggerConfig: { type: 'ON_SLIDE_CHANGE' },
      }
    } else if (kind === 'CONDITION') {
      newNodeData = {
        id: newId,
        kind: 'CONDITION',
        label: 'Verificar Louvor',
        position: { x: 280, y: 150 },
        conditionConfig: { field: 'mode', operator: 'equals', value: 'SONG' },
      }
    } else if (kind === 'ACTION_OBS') {
      newNodeData = {
        id: newId,
        kind: 'ACTION_OBS',
        label: 'Trocar Cena OBS',
        position: { x: 500, y: 120 },
        actionObsConfig: { requestType: 'SetCurrentProgramScene', sceneName: 'Cena Louvor' },
      }
    } else {
      newNodeData = {
        id: newId,
        kind: 'ACTION_HTTP',
        label: 'Webhook DMX',
        position: { x: 500, y: 220 },
        actionHttpConfig: { url: 'http://localhost:8080/api/scene', method: 'POST', timeoutMs: 500 },
      }
    }

    const flowNode: Node = {
      id: newId,
      type: kind === 'TRIGGER' ? 'triggerNode' : kind === 'CONDITION' ? 'conditionNode' : 'actionNode',
      position: newNodeData.position,
      data: newNodeData as unknown as Record<string, unknown>,
    }

    setNodes((prev) => [...prev, flowNode])
  }

  // Salvar Grafo com validação acíclica
  const handleSave = async () => {
    setErrorMessage(null)
    setSaveStatus(null)

    const contractNodes: AutomationNode[] = nodes.map((node) => {
      const data = node.data as unknown as AutomationNode
      return {
        ...data,
        position: node.position,
      }
    })

    const contractEdges = edges.map((edge) => ({
      id: edge.id,
      source: edge.source,
      target: edge.target,
      conditionOutcome: (edge.sourceHandle === 'true'
        ? 'true'
        : edge.sourceHandle === 'false'
        ? 'false'
        : 'always') as 'true' | 'false' | 'always',
    }))

    const acyclicCheck = validateGraphAcyclic(contractNodes, contractEdges)
    if (!acyclicCheck.isValid) {
      setErrorMessage(acyclicCheck.error || 'Grafo possui ciclos inválidos')
      return
    }

    try {
      await onSaveGraph({
        ...initialGraph,
        nodes: contractNodes,
        edges: contractEdges,
      })
      setSaveStatus('Automações salvas com sucesso!')
      setTimeout(() => setSaveStatus(null), 3000)
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Falha ao salvar grafo')
    }
  }

  return (
    <div className="flex h-full w-full flex-col bg-slate-950 text-slate-100 select-none">
      {/* Barra de Ações Superior */}
      <div className="flex items-center justify-between border-b border-white/10 bg-slate-900 px-4 py-2.5">
        <div className="flex items-center gap-3">
          <span className="text-xs font-bold tracking-wider uppercase text-slate-400">
            Editor de Automação (DAG)
          </span>

          {/* Status do OBS WebSocket */}
          <div className="flex items-center gap-1.5 rounded-full bg-slate-800/80 px-2.5 py-0.5 text-[10px] font-mono border border-white/5">
            <span
              className={`h-2 w-2 rounded-full ${
                obsConnected ? 'bg-emerald-400 shadow-sm shadow-emerald-400/50' : 'bg-slate-600'
              }`}
            />
            <span className={obsConnected ? 'text-emerald-400' : 'text-slate-400'}>
              {obsConnected ? 'OBS Conectado (4455)' : 'OBS Desconectado'}
            </span>
          </div>

          {/* Feedback de Validação / Salvo */}
          {saveStatus && (
            <span className="flex items-center gap-1 text-[11px] text-emerald-400">
              <CheckCircle2 className="h-3 w-3" />
              {saveStatus}
            </span>
          )}
          {errorMessage && (
            <span className="flex items-center gap-1 text-[11px] text-rose-400 font-mono">
              <AlertCircle className="h-3 w-3" />
              {errorMessage}
            </span>
          )}
        </div>

        {/* Botões de Ação */}
        <div className="flex items-center gap-2">
          {/* Adição de Nós */}
          <button
            type="button"
            onClick={() => handleAddNode('TRIGGER')}
            className="flex items-center gap-1 rounded bg-slate-800 px-2 py-1 text-xs text-sky-400 hover:bg-slate-700 transition"
          >
            <Plus className="h-3 w-3" /> Gatilho
          </button>
          <button
            type="button"
            onClick={() => handleAddNode('CONDITION')}
            className="flex items-center gap-1 rounded bg-slate-800 px-2 py-1 text-xs text-amber-400 hover:bg-slate-700 transition"
          >
            <Plus className="h-3 w-3" /> Condição
          </button>
          <button
            type="button"
            onClick={() => handleAddNode('ACTION_OBS')}
            className="flex items-center gap-1 rounded bg-slate-800 px-2 py-1 text-xs text-emerald-400 hover:bg-slate-700 transition"
          >
            <Plus className="h-3 w-3" /> Ação OBS
          </button>
          <button
            type="button"
            onClick={() => handleAddNode('ACTION_HTTP')}
            className="flex items-center gap-1 rounded bg-slate-800 px-2 py-1 text-xs text-purple-400 hover:bg-slate-700 transition"
          >
            <Plus className="h-3 w-3" /> Ação HTTP
          </button>

          {/* Testar Gatilho */}
          {onTestTrigger && (
            <button
              type="button"
              onClick={() => onTestTrigger('ON_SLIDE_CHANGE')}
              className="flex items-center gap-1 rounded bg-slate-800 px-2.5 py-1 text-xs text-slate-300 hover:bg-slate-700 hover:text-white transition"
              title="Simular disparo de evento de slide"
            >
              <Play className="h-3 w-3 text-amber-400" /> Testar Fluxo
            </button>
          )}

          {/* Salvar Grafo */}
          <button
            type="button"
            onClick={handleSave}
            className="flex items-center gap-1.5 rounded bg-sky-500 px-3 py-1 text-xs font-semibold text-slate-950 hover:bg-sky-400 transition shadow-sm"
          >
            <Save className="h-3.5 w-3.5" /> Salvar Regras
          </button>
        </div>
      </div>

      {/* Área do Grafo ReactFlow */}
      <div className="flex-1 w-full h-[600px] relative bg-slate-950">
        <ReactFlow
          nodes={nodes}
          edges={edges}
          onNodesChange={onNodesChange}
          onEdgesChange={onEdgesChange}
          onConnect={onConnect}
          nodeTypes={nodeTypes}
          fitView
          className="bg-slate-950"
        >
          <Background variant={BackgroundVariant.Dots} color="#334155" gap={16} size={1} />
          <Controls className="!bg-slate-900 !border-white/10 !text-slate-300" />
        </ReactFlow>
      </div>
    </div>
  )
}
