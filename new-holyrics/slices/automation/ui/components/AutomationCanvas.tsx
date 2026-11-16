/**
 * @module @slices/automation/ui/components/AutomationCanvas
 * @description Canvas visual interativo de automações (@xyflow/react) com menu de contexto,
 * Painel Inspector de propriedades dos nós, suporte a múltiplos fluxos e feedback de teste.
 */

import { useState, useCallback, useMemo, useEffect } from 'react'
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
import {
  Plus,
  Save,
  Play,
  CheckCircle2,
  AlertCircle,
  Trash2,
  Copy,
  Sliders,
  X,
  Clock,
} from 'lucide-react'
import type {
  AutomationGraph,
  AutomationNode,
  ConditionOperator,
  ObsRequestType,
  TriggerType,
} from '../../contracts/automation-contracts'
import { validateGraphAcyclic } from '../../contracts/automation-contracts'
import { TriggerNode, ConditionNode, ActionNode } from './CustomNodes'

export interface AutomationCanvasProps {
  initialGraph: AutomationGraph
  activeFlowId?: string
  obsConnected?: boolean
  onSaveGraph: (graph: AutomationGraph) => Promise<void>
  onTestTrigger?: (triggerType: string) => Promise<string | void>
}

interface ContextMenuState {
  x: number
  y: number
  nodeId?: string
  edgeId?: string
}

function toFlowNode(node: AutomationNode): Node {
  return {
    id: node.id,
    type:
      node.kind === 'TRIGGER'
        ? 'triggerNode'
        : node.kind === 'CONDITION'
          ? 'conditionNode'
          : 'actionNode',
    position: node.position,
    data: node as unknown as Record<string, unknown>,
  }
}

/**
 * Editor gráfico de nós para regras de automação litúrgica.
 */
export function AutomationCanvas({
  initialGraph,
  activeFlowId = 'default',
  obsConnected = false,
  onSaveGraph,
  onTestTrigger,
}: AutomationCanvasProps) {
  const flowNodes = useMemo(() => {
    return initialGraph.nodes.filter((n) => (n.flowId || 'default') === activeFlowId)
  }, [initialGraph.nodes, activeFlowId])

  const flowNodeIds = useMemo(() => new Set(flowNodes.map((n) => n.id)), [flowNodes])

  const initialFlowNodes: Node[] = useMemo(() => flowNodes.map(toFlowNode), [flowNodes])

  const initialFlowEdges: Edge[] = useMemo(() => {
    return initialGraph.edges
      .filter((e) => flowNodeIds.has(e.source) && flowNodeIds.has(e.target))
      .map((edge) => ({
        id: edge.id,
        source: edge.source,
        target: edge.target,
        sourceHandle: edge.conditionOutcome !== 'always' ? edge.conditionOutcome : undefined,
        animated: true,
        style: {
          stroke:
            edge.conditionOutcome === 'true'
              ? '#34d399'
              : edge.conditionOutcome === 'false'
                ? '#f43f5e'
                : '#38bdf8',
          strokeWidth: 2,
        },
      }))
  }, [initialGraph.edges, flowNodeIds])

  const [nodes, setNodes] = useState<Node[]>(initialFlowNodes)
  const [edges, setEdges] = useState<Edge[]>(initialFlowEdges)
  const [selectedNodeId, setSelectedNodeId] = useState<string | null>(null)
  const [contextMenu, setContextMenu] = useState<ContextMenuState | null>(null)
  const [saveStatus, setSaveStatus] = useState<string | null>(null)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)

  useEffect(() => {
    setNodes(initialFlowNodes)
    setEdges(initialFlowEdges)
    setSelectedNodeId(null)
  }, [initialFlowNodes, initialFlowEdges])

  useEffect(() => {
    const close = () => setContextMenu(null)
    window.addEventListener('click', close)
    return () => window.removeEventListener('click', close)
  }, [])

  const nodeTypes = useMemo(
    () => ({
      triggerNode: TriggerNode,
      conditionNode: ConditionNode,
      actionNode: ActionNode,
    }),
    [],
  )

  const onNodesChange: OnNodesChange = useCallback(
    (changes) => setNodes((nds) => applyNodeChanges(changes, nds)),
    [],
  )

  const onEdgesChange: OnEdgesChange = useCallback(
    (changes) => setEdges((eds) => applyEdgeChanges(changes, eds)),
    [],
  )

  const onConnect: OnConnect = useCallback((connection) => {
    setEdges((eds) =>
      addEdge(
        {
          ...connection,
          animated: true,
          style: {
            stroke:
              connection.sourceHandle === 'true'
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
  }, [])

  const selectedNodeData = useMemo(() => {
    if (!selectedNodeId) return null
    const found = nodes.find((n) => n.id === selectedNodeId)
    return found ? (found.data as unknown as AutomationNode) : null
  }, [nodes, selectedNodeId])

  const updateNodeData = (nodeId: string, updater: (prev: AutomationNode) => AutomationNode) => {
    setNodes((prev) =>
      prev.map((n) => {
        if (n.id !== nodeId) return n
        const updated = updater(n.data as unknown as AutomationNode)
        return {
          ...n,
          data: updated as unknown as Record<string, unknown>,
        }
      }),
    )
  }

  const handleAddNode = (kind: AutomationNode['kind']) => {
    const newId = `node-${Date.now()}`
    let newNodeData: AutomationNode

    if (kind === 'TRIGGER') {
      newNodeData = {
        id: newId,
        flowId: activeFlowId,
        kind: 'TRIGGER',
        label: 'Troca de Slide',
        position: { x: 80, y: 150 },
        triggerConfig: { type: 'ON_SLIDE_CHANGE' },
      }
    } else if (kind === 'CONDITION') {
      newNodeData = {
        id: newId,
        flowId: activeFlowId,
        kind: 'CONDITION',
        label: 'Verificar Louvor',
        position: { x: 280, y: 150 },
        conditionConfig: { field: 'mode', operator: 'equals', value: 'SONG' },
      }
    } else if (kind === 'ACTION_OBS') {
      newNodeData = {
        id: newId,
        flowId: activeFlowId,
        kind: 'ACTION_OBS',
        label: 'Trocar Cena OBS',
        position: { x: 500, y: 120 },
        actionObsConfig: { requestType: 'SetCurrentProgramScene', sceneName: 'Cena Louvor' },
      }
    } else if (kind === 'ACTION_DELAY') {
      newNodeData = {
        id: newId,
        flowId: activeFlowId,
        kind: 'ACTION_DELAY',
        label: 'Aguardar (Delay)',
        position: { x: 420, y: 260 },
        actionDelayConfig: { durationMs: 500 },
      }
    } else {
      newNodeData = {
        id: newId,
        flowId: activeFlowId,
        kind: 'ACTION_HTTP',
        label: 'Webhook DMX',
        position: { x: 500, y: 220 },
        actionHttpConfig: { url: 'http://localhost:8080/api/scene', method: 'POST', timeoutMs: 500 },
      }
    }

    setNodes((prev) => [...prev, toFlowNode(newNodeData)])
    setSelectedNodeId(newId)
  }

  const handleDuplicateNode = (nodeId: string) => {
    const target = nodes.find((n) => n.id === nodeId)
    if (!target) return
    const newId = `node-${Date.now()}`
    const data = target.data as unknown as AutomationNode
    const cloned: AutomationNode = {
      ...data,
      id: newId,
      flowId: activeFlowId,
      label: `${data.label} (Cópia)`,
      position: { x: target.position.x + 40, y: target.position.y + 40 },
    }
    setNodes((prev) => [...prev, toFlowNode(cloned)])
    setSelectedNodeId(newId)
  }

  const handleDeleteNode = (nodeId: string) => {
    setNodes((prev) => prev.filter((n) => n.id !== nodeId))
    setEdges((prev) => prev.filter((e) => e.source !== nodeId && e.target !== nodeId))
    if (selectedNodeId === nodeId) {
      setSelectedNodeId(null)
    }
  }

  const handleDeleteEdge = (edgeId: string) => {
    setEdges((prev) => prev.filter((e) => e.id !== edgeId))
  }

  const handleSave = async () => {
    setErrorMessage(null)
    setSaveStatus(null)

    const currentFlowNodes: AutomationNode[] = nodes.map((node) => {
      const data = node.data as unknown as AutomationNode
      return {
        ...data,
        flowId: activeFlowId,
        position: node.position,
      }
    })

    const currentFlowEdges = edges.map((edge) => ({
      id: edge.id,
      source: edge.source,
      target: edge.target,
      conditionOutcome: (edge.sourceHandle === 'true'
        ? 'true'
        : edge.sourceHandle === 'false'
          ? 'false'
          : 'always') as 'true' | 'false' | 'always',
    }))

    // Preserva nós e arestas de outros fluxos
    const otherNodes = initialGraph.nodes.filter((n) => (n.flowId || 'default') !== activeFlowId)
    const otherNodeIds = new Set(otherNodes.map((n) => n.id))
    const otherEdges = initialGraph.edges.filter(
      (e) => otherNodeIds.has(e.source) && otherNodeIds.has(e.target),
    )

    const mergedNodes = [...otherNodes, ...currentFlowNodes]
    const mergedEdges = [...otherEdges, ...currentFlowEdges]

    const acyclicCheck = validateGraphAcyclic(mergedNodes, mergedEdges)
    if (!acyclicCheck.isValid) {
      setErrorMessage(acyclicCheck.error || 'Grafo possui ciclos inválidos')
      return
    }

    try {
      await onSaveGraph({
        ...initialGraph,
        nodes: mergedNodes,
        edges: mergedEdges,
      })
      setSaveStatus('Automações salvas com sucesso!')
      setTimeout(() => setSaveStatus(null), 3000)
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Falha ao salvar grafo')
    }
  }

  const handleRunTest = async () => {
    if (!onTestTrigger) return
    setErrorMessage(null)
    try {
      const firstTrigger = nodes.find(
        (n) => (n.data as unknown as AutomationNode).kind === 'TRIGGER',
      )
      const triggerType =
        (firstTrigger?.data as unknown as AutomationNode)?.triggerConfig?.type || 'ON_SLIDE_CHANGE'
      const msg = await onTestTrigger(triggerType)
      setSaveStatus(typeof msg === 'string' && msg ? msg : `Teste "${triggerType}" disparado!`)
      setTimeout(() => setSaveStatus(null), 3500)
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : 'Erro ao testar fluxo')
    }
  }

  return (
    <div className="flex h-full w-full flex-col bg-slate-950 text-slate-100 select-none">
      {/* Barra de Ações Superior */}
      <div className="flex flex-wrap items-center justify-between gap-2 border-b border-white/10 bg-slate-900 px-4 py-2">
        <div className="flex items-center gap-3">
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

        <div className="flex flex-wrap items-center gap-1.5">
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
            <Plus className="h-3 w-3" /> HTTP
          </button>
          <button
            type="button"
            onClick={() => handleAddNode('ACTION_DELAY')}
            className="flex items-center gap-1 rounded bg-slate-800 px-2 py-1 text-xs text-slate-300 hover:bg-slate-700 transition"
          >
            <Clock className="h-3 w-3" /> Delay
          </button>

          {onTestTrigger && (
            <button
              type="button"
              onClick={handleRunTest}
              className="flex items-center gap-1 rounded bg-slate-800 px-2.5 py-1 text-xs text-slate-200 hover:bg-slate-700 transition"
              title="Simular disparo deste fluxo"
            >
              <Play className="h-3 w-3 text-amber-400" /> Testar Fluxo
            </button>
          )}

          <button
            type="button"
            onClick={handleSave}
            className="flex items-center gap-1.5 rounded bg-sky-500 px-3 py-1 text-xs font-semibold text-slate-950 hover:bg-sky-400 transition shadow-sm"
          >
            <Save className="h-3.5 w-3.5" /> Salvar Regras
          </button>
        </div>
      </div>

      {/* Área Principal: Canvas + Inspector Lateral */}
      <div className="flex flex-1 min-h-0 relative">
        <div className="flex-1 h-full relative bg-slate-950">
          <ReactFlow
            nodes={nodes}
            edges={edges}
            onNodesChange={onNodesChange}
            onEdgesChange={onEdgesChange}
            onConnect={onConnect}
            onNodeClick={(_event, node) => setSelectedNodeId(node.id)}
            onPaneClick={() => {
              setSelectedNodeId(null)
              setContextMenu(null)
            }}
            onNodeContextMenu={(event, node) => {
              event.preventDefault()
              setContextMenu({ x: event.clientX, y: event.clientY, nodeId: node.id })
            }}
            onEdgeContextMenu={(event, edge) => {
              event.preventDefault()
              setContextMenu({ x: event.clientX, y: event.clientY, edgeId: edge.id })
            }}
            nodeTypes={nodeTypes}
            fitView
            className="bg-slate-950"
          >
            <Background variant={BackgroundVariant.Dots} color="#334155" gap={16} size={1} />
            <Controls className="!bg-slate-900 !border-white/10 !text-slate-300" />
          </ReactFlow>
        </div>

        {/* Painel Inspector Lateral do Nó Selecionado */}
        {selectedNodeData && (
          <aside className="w-72 shrink-0 border-l border-white/10 bg-slate-900 p-4 overflow-y-auto flex flex-col gap-3 text-xs">
            <div className="flex items-center justify-between border-b border-white/10 pb-2">
              <div className="flex items-center gap-1.5 font-semibold text-sky-400">
                <Sliders className="h-3.5 w-3.5" />
                <span>Propriedades do Nó</span>
              </div>
              <button
                type="button"
                onClick={() => setSelectedNodeId(null)}
                className="rounded p-1 text-slate-400 hover:bg-slate-800 hover:text-white"
              >
                <X className="h-3.5 w-3.5" />
              </button>
            </div>

            <div>
              <label className="mb-1 block text-slate-400">Nome / Rótulo</label>
              <input
                type="text"
                value={selectedNodeData.label}
                onChange={(event) =>
                  updateNodeData(selectedNodeData.id, (prev) => ({
                    ...prev,
                    label: event.target.value,
                  }))
                }
                className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100 focus:outline-none focus:ring-1 focus:ring-sky-500"
              />
            </div>

            {selectedNodeData.kind === 'TRIGGER' && (
              <>
                <div>
                  <label className="mb-1 block text-slate-400">Tipo de Gatilho</label>
                  <select
                    value={selectedNodeData.triggerConfig?.type || 'ON_SLIDE_CHANGE'}
                    onChange={(event) =>
                      updateNodeData(selectedNodeData.id, (prev) => ({
                        ...prev,
                        triggerConfig: {
                          ...prev.triggerConfig,
                          type: event.target.value as TriggerType,
                        },
                      }))
                    }
                    className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100"
                  >
                    <option value="ON_SLIDE_CHANGE">Troca de Slide</option>
                    <option value="ON_ITEM_SELECT">Seleção de Item</option>
                    <option value="ON_PANIC">Botão de Pânico</option>
                    <option value="ON_HTTP_WEBHOOK">Webhook Externo</option>
                  </select>
                </div>
                {selectedNodeData.triggerConfig?.type === 'ON_HTTP_WEBHOOK' && (
                  <div>
                    <label className="mb-1 block text-slate-400">ID do Webhook</label>
                    <input
                      type="text"
                      value={selectedNodeData.triggerConfig?.webhookTriggerId || ''}
                      onChange={(event) =>
                        updateNodeData(selectedNodeData.id, (prev) => ({
                          ...prev,
                          triggerConfig: {
                            type: 'ON_HTTP_WEBHOOK',
                            webhookTriggerId: event.target.value,
                          },
                        }))
                      }
                      placeholder="ex: cena-louvor"
                      className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100"
                    />
                  </div>
                )}
              </>
            )}

            {selectedNodeData.kind === 'CONDITION' && (
              <>
                <div>
                  <label className="mb-1 block text-slate-400">Campo Avaliado</label>
                  <select
                    value={selectedNodeData.conditionConfig?.field || 'mode'}
                    onChange={(event) =>
                      updateNodeData(selectedNodeData.id, (prev) => ({
                        ...prev,
                        conditionConfig: {
                          field: event.target.value,
                          operator: prev.conditionConfig?.operator || 'equals',
                          value: prev.conditionConfig?.value || 'SONG',
                        },
                      }))
                    }
                    className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100"
                  >
                    <option value="mode">Modo da Tela (mode)</option>
                    <option value="content">Conteúdo do Slide</option>
                    <option value="slideIndex">Índice do Slide</option>
                    <option value="tag">Tag / Título</option>
                  </select>
                </div>
                <div>
                  <label className="mb-1 block text-slate-400">Operador</label>
                  <select
                    value={selectedNodeData.conditionConfig?.operator || 'equals'}
                    onChange={(event) =>
                      updateNodeData(selectedNodeData.id, (prev) => ({
                        ...prev,
                        conditionConfig: {
                          field: prev.conditionConfig?.field || 'mode',
                          operator: event.target.value as ConditionOperator,
                          value: prev.conditionConfig?.value || '',
                        },
                      }))
                    }
                    className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100"
                  >
                    <option value="equals">Igual a</option>
                    <option value="not_equals">Diferente de</option>
                    <option value="contains">Contém</option>
                    <option value="starts_with">Começa com</option>
                    <option value="regex">Regex</option>
                  </select>
                </div>
                <div>
                  <label className="mb-1 block text-slate-400">Valor Esperado</label>
                  <input
                    type="text"
                    value={selectedNodeData.conditionConfig?.value || ''}
                    onChange={(event) =>
                      updateNodeData(selectedNodeData.id, (prev) => ({
                        ...prev,
                        conditionConfig: {
                          field: prev.conditionConfig?.field || 'mode',
                          operator: prev.conditionConfig?.operator || 'equals',
                          value: event.target.value,
                        },
                      }))
                    }
                    className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100"
                  />
                </div>
              </>
            )}

            {selectedNodeData.kind === 'ACTION_OBS' && (
              <>
                <div>
                  <label className="mb-1 block text-slate-400">Operação OBS v5</label>
                  <select
                    value={selectedNodeData.actionObsConfig?.requestType || 'SetCurrentProgramScene'}
                    onChange={(event) =>
                      updateNodeData(selectedNodeData.id, (prev) => ({
                        ...prev,
                        actionObsConfig: {
                          ...prev.actionObsConfig,
                          requestType: event.target.value as ObsRequestType,
                        },
                      }))
                    }
                    className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100"
                  >
                    <option value="SetCurrentProgramScene">Trocar Cena (Programa)</option>
                    <option value="SetCurrentPreviewScene">Trocar Cena (Preview)</option>
                    <option value="SetSceneItemEnabled">Exibir/Ocultar Fonte</option>
                    <option value="SetInputMute">Mutar/Desmutar Áudio</option>
                    <option value="StartStream">Iniciar Transmissão</option>
                    <option value="StopStream">Parar Transmissão</option>
                    <option value="StartRecord">Iniciar Gravação</option>
                    <option value="StopRecord">Parar Gravação</option>
                  </select>
                </div>
                <div>
                  <label className="mb-1 block text-slate-400">Nome da Cena / Fonte</label>
                  <input
                    type="text"
                    value={selectedNodeData.actionObsConfig?.sceneName || ''}
                    onChange={(event) =>
                      updateNodeData(selectedNodeData.id, (prev) => ({
                        ...prev,
                        actionObsConfig: {
                          requestType: prev.actionObsConfig?.requestType || 'SetCurrentProgramScene',
                          ...prev.actionObsConfig,
                          sceneName: event.target.value,
                        },
                      }))
                    }
                    placeholder="Ex: Cena Louvor"
                    className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100"
                  />
                </div>
              </>
            )}

            {selectedNodeData.kind === 'ACTION_HTTP' && (
              <>
                <div>
                  <label className="mb-1 block text-slate-400">Método HTTP</label>
                  <select
                    value={selectedNodeData.actionHttpConfig?.method || 'POST'}
                    onChange={(event) =>
                      updateNodeData(selectedNodeData.id, (prev) => ({
                        ...prev,
                        actionHttpConfig: {
                          url: prev.actionHttpConfig?.url || 'http://localhost:8080',
                          timeoutMs: prev.actionHttpConfig?.timeoutMs || 500,
                          ...prev.actionHttpConfig,
                          method: event.target.value as 'GET' | 'POST' | 'PUT' | 'DELETE',
                        },
                      }))
                    }
                    className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100"
                  >
                    <option value="POST">POST</option>
                    <option value="GET">GET</option>
                    <option value="PUT">PUT</option>
                    <option value="DELETE">DELETE</option>
                  </select>
                </div>
                <div>
                  <label className="mb-1 block text-slate-400">URL de Destino</label>
                  <input
                    type="url"
                    value={selectedNodeData.actionHttpConfig?.url || ''}
                    onChange={(event) =>
                      updateNodeData(selectedNodeData.id, (prev) => ({
                        ...prev,
                        actionHttpConfig: {
                          method: prev.actionHttpConfig?.method || 'POST',
                          timeoutMs: prev.actionHttpConfig?.timeoutMs || 500,
                          ...prev.actionHttpConfig,
                          url: event.target.value,
                        },
                      }))
                    }
                    className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100"
                  />
                </div>
              </>
            )}

            {selectedNodeData.kind === 'ACTION_DELAY' && (
              <div>
                <label className="mb-1 block text-slate-400">Duração (ms)</label>
                <input
                  type="number"
                  min={50}
                  max={60000}
                  value={selectedNodeData.actionDelayConfig?.durationMs ?? 500}
                  onChange={(event) =>
                    updateNodeData(selectedNodeData.id, (prev) => ({
                      ...prev,
                      actionDelayConfig: { durationMs: Number(event.target.value) || 500 },
                    }))
                  }
                  className="w-full rounded border border-white/10 bg-slate-950 px-2.5 py-1.5 text-slate-100"
                />
              </div>
            )}

            <div className="mt-auto pt-3 border-t border-white/10 flex gap-2">
              <button
                type="button"
                onClick={() => handleDuplicateNode(selectedNodeData.id)}
                className="flex flex-1 items-center justify-center gap-1 rounded bg-slate-800 px-2 py-1.5 text-slate-200 hover:bg-slate-700"
              >
                <Copy className="h-3 w-3" /> Duplicar
              </button>
              <button
                type="button"
                onClick={() => handleDeleteNode(selectedNodeData.id)}
                className="flex flex-1 items-center justify-center gap-1 rounded bg-rose-500/15 px-2 py-1.5 text-rose-300 hover:bg-rose-500/25"
              >
                <Trash2 className="h-3 w-3" /> Excluir
              </button>
            </div>
          </aside>
        )}
      </div>

      {/* Menu de Contexto (Botão Direito sobre Nós e Arestas) */}
      {contextMenu && (
        <div
          style={{ left: contextMenu.x, top: contextMenu.y }}
          className="fixed z-50 w-44 rounded-lg border border-white/10 bg-slate-900 py-1 text-xs shadow-2xl"
        >
          {contextMenu.nodeId && (
            <>
              <button
                type="button"
                onClick={() => setSelectedNodeId(contextMenu.nodeId!)}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-slate-200 hover:bg-slate-800"
              >
                <Sliders className="h-3.5 w-3.5 text-sky-400" />
                <span>Editar Propriedades</span>
              </button>
              <button
                type="button"
                onClick={() => handleDuplicateNode(contextMenu.nodeId!)}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-slate-200 hover:bg-slate-800"
              >
                <Copy className="h-3.5 w-3.5 text-amber-400" />
                <span>Duplicar Nó</span>
              </button>
              <button
                type="button"
                onClick={() => handleDeleteNode(contextMenu.nodeId!)}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-rose-300 hover:bg-rose-500/10"
              >
                <Trash2 className="h-3.5 w-3.5 text-rose-400" />
                <span>Excluir Nó</span>
              </button>
            </>
          )}
          {contextMenu.edgeId && (
            <button
              type="button"
              onClick={() => handleDeleteEdge(contextMenu.edgeId!)}
              className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-rose-300 hover:bg-rose-500/10"
            >
              <Trash2 className="h-3.5 w-3.5 text-rose-400" />
              <span>Excluir Conexão</span>
            </button>
          )}
        </div>
      )}
    </div>
  )
}
