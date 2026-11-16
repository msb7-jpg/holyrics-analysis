/**
 * @module @slices/automation/ui/components/AutomationModal
 * @description Modal de automações DAG com múltiplas regras/fluxos, editor interativo e histórico de execuções.
 */

import { useState, useEffect, useMemo } from 'react'
import { X, Cpu, Plus, Trash2, History, Workflow, RefreshCw } from 'lucide-react'
import type { AutomationFlow, AutomationGraph } from '../../contracts/automation-contracts'
import { DEFAULT_AUTOMATION_GRAPH } from '../../contracts/automation-contracts'
import { AutomationCanvas } from './AutomationCanvas'

export interface AutomationModalProps {
  isOpen: boolean
  onClose: () => void
}

interface ExecutionHistoryItem {
  id: string
  timestamp: number
  triggerType: string
  nodesExecuted: number
  status: 'success' | 'error' | 'skipped'
  details: string
}

/**
 * Diálogo modal para gerenciamento de múltiplas regras de automação e histórico de execução.
 */
export function AutomationModal({ isOpen, onClose }: AutomationModalProps) {
  const [graph, setGraph] = useState<AutomationGraph>(DEFAULT_AUTOMATION_GRAPH)
  const [obsConnected, setObsConnected] = useState(false)
  const [loading, setLoading] = useState(false)
  const [activeTab, setActiveTab] = useState<'EDITOR' | 'HISTORY'>('EDITOR')
  const [activeFlowId, setActiveFlowId] = useState<string>('default')
  const [history, setHistory] = useState<ExecutionHistoryItem[]>([])

  const flows: AutomationFlow[] = useMemo(() => {
    if (graph.flows && graph.flows.length > 0) return graph.flows
    return [{ id: 'default', name: 'Fluxo Principal', enabled: true }]
  }, [graph.flows])

  const loadHistory = () => {
    fetch('/api/automation/history')
      .then((res) => res.json())
      .then((json: { ok: boolean; data?: ExecutionHistoryItem[] }) => {
        if (json.ok && Array.isArray(json.data)) {
          setHistory(json.data)
        }
      })
      .catch(() => setHistory([]))
  }

  useEffect(() => {
    if (!isOpen) return

    setLoading(true)
    fetch('/api/automation')
      .then((res) => res.json())
      .then((json: { ok: boolean; data?: { graph: AutomationGraph; obsStatus: { connected: boolean } } }) => {
        if (json.ok && json.data) {
          setGraph(json.data.graph)
          setObsConnected(json.data.obsStatus.connected)
          const firstFlow = json.data.graph.flows?.[0]?.id || 'default'
          setActiveFlowId(firstFlow)
        }
      })
      .catch(() => {})
      .finally(() => setLoading(false))

    loadHistory()
  }, [isOpen])

  useEffect(() => {
    if (!isOpen) return
    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') onClose()
    }
    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [isOpen, onClose])

  if (!isOpen) return null

  const handleSaveGraph = async (updatedGraph: AutomationGraph) => {
    const withFlows: AutomationGraph = {
      ...updatedGraph,
      flows,
    }
    const res = await fetch('/api/automation', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(withFlows),
    })

    if (!res.ok) {
      const err = await res.json()
      throw new Error(err.error || 'Erro ao salvar automações')
    }

    setGraph(withFlows)
  }

  const handleCreateFlow = async () => {
    const name = window.prompt('Nome do novo fluxo de automação:')
    if (!name?.trim()) return
    const newFlow: AutomationFlow = {
      id: `flow-${Date.now()}`,
      name: name.trim(),
      enabled: true,
    }
    const nextFlows = [...flows, newFlow]
    const nextGraph: AutomationGraph = { ...graph, flows: nextFlows }
    setGraph(nextGraph)
    setActiveFlowId(newFlow.id)
    await handleSaveGraph(nextGraph)
  }

  const handleToggleFlow = async (flowId: string) => {
    const nextFlows = flows.map((f) => (f.id === flowId ? { ...f, enabled: !f.enabled } : f))
    const nextGraph: AutomationGraph = { ...graph, flows: nextFlows }
    setGraph(nextGraph)
    await handleSaveGraph(nextGraph)
  }

  const handleDeleteFlow = async (flowId: string) => {
    if (flows.length <= 1) return
    const nextFlows = flows.filter((f) => f.id !== flowId)
    const nextNodes = graph.nodes.filter((n) => (n.flowId || 'default') !== flowId)
    const nextNodeIds = new Set(nextNodes.map((n) => n.id))
    const nextEdges = graph.edges.filter((e) => nextNodeIds.has(e.source) && nextNodeIds.has(e.target))
    const nextGraph: AutomationGraph = {
      ...graph,
      flows: nextFlows,
      nodes: nextNodes,
      edges: nextEdges,
    }
    setGraph(nextGraph)
    setActiveFlowId(nextFlows[0].id)
    await handleSaveGraph(nextGraph)
  }

  const handleTestTrigger = async (triggerType: string): Promise<string> => {
    const res = await fetch('/api/automation/test-trigger', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ triggerType }),
    })
    const json = (await res.json()) as { ok: boolean; message?: string; history?: ExecutionHistoryItem[] }
    if (json.history) {
      setHistory(json.history)
    } else {
      loadHistory()
    }
    return json.message || `Gatilho ${triggerType} testado!`
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-5 select-none animate-in fade-in duration-150">
      <div className="flex h-[90vh] w-full max-w-6xl flex-col rounded-xl border border-white/10 bg-slate-950 shadow-2xl overflow-hidden">
        {/* Cabeçalho */}
        <div className="flex items-center justify-between border-b border-white/10 bg-slate-900 px-5 py-3">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2.5">
              <span className="flex h-7 w-7 items-center justify-center rounded-lg bg-sky-500/20 text-sky-400">
                <Cpu className="h-4 w-4" />
              </span>
              <div>
                <h2 className="text-sm font-bold text-slate-100">Automações & Integrações</h2>
                <p className="text-[11px] text-slate-400">
                  Regras visuais integradas com OBS Studio, webhooks HTTP e projeção
                </p>
              </div>
            </div>

            <div className="flex items-center gap-1 rounded-lg bg-slate-950 p-1 text-xs">
              <button
                type="button"
                onClick={() => setActiveTab('EDITOR')}
                className={`flex items-center gap-1.5 rounded-md px-3 py-1 font-medium transition ${
                  activeTab === 'EDITOR'
                    ? 'bg-sky-500 text-slate-950'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                <Workflow className="h-3.5 w-3.5" />
                <span>Editor de Fluxos</span>
              </button>
              <button
                type="button"
                onClick={() => {
                  setActiveTab('HISTORY')
                  loadHistory()
                }}
                className={`flex items-center gap-1.5 rounded-md px-3 py-1 font-medium transition ${
                  activeTab === 'HISTORY'
                    ? 'bg-sky-500 text-slate-950'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                <History className="h-3.5 w-3.5" />
                <span>Histórico ({history.length})</span>
              </button>
            </div>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="rounded p-1 text-slate-400 hover:bg-slate-800 hover:text-slate-100 transition"
            title="Fechar (Esc)"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Corpo */}
        <div className="flex flex-1 min-h-0 overflow-hidden">
          {activeTab === 'EDITOR' ? (
            <>
              {/* Barra Lateral de Múltiplos Fluxos */}
              <aside className="w-56 shrink-0 border-r border-white/10 bg-slate-900/70 p-3 flex flex-col gap-2 text-xs">
                <div className="flex items-center justify-between pb-1 border-b border-white/5">
                  <span className="font-semibold uppercase tracking-wider text-[10px] text-slate-400">
                    Regras / Fluxos ({flows.length})
                  </span>
                  <button
                    type="button"
                    onClick={handleCreateFlow}
                    title="Novo Fluxo de Automação"
                    className="flex items-center gap-1 rounded bg-sky-500/15 px-1.5 py-0.5 text-[10px] font-medium text-sky-300 hover:bg-sky-500/25"
                  >
                    <Plus className="h-3 w-3" /> Novo
                  </button>
                </div>

                <div className="flex flex-col gap-1 overflow-y-auto flex-1">
                  {flows.map((flow) => {
                    const isSelected = flow.id === activeFlowId
                    return (
                      <div
                        key={flow.id}
                        onClick={() => setActiveFlowId(flow.id)}
                        className={`group flex items-center justify-between rounded-md px-2.5 py-2 cursor-pointer transition ${
                          isSelected
                            ? 'bg-slate-800 text-white ring-1 ring-sky-500/50'
                            : 'text-slate-300 hover:bg-slate-800/50'
                        }`}
                      >
                        <div className="flex items-center gap-2 min-w-0">
                          <input
                            type="checkbox"
                            checked={flow.enabled}
                            onClick={(e) => e.stopPropagation()}
                            onChange={() => void handleToggleFlow(flow.id)}
                            title={flow.enabled ? 'Regra Ativa' : 'Regra Pausada'}
                            className="h-3.5 w-3.5 accent-emerald-500 cursor-pointer shrink-0"
                          />
                          <span className="truncate font-medium">{flow.name}</span>
                        </div>
                        {flows.length > 1 && (
                          <button
                            type="button"
                            onClick={(e) => {
                              e.stopPropagation()
                              void handleDeleteFlow(flow.id)
                            }}
                            title="Excluir Fluxo"
                            className="opacity-0 group-hover:opacity-100 rounded p-0.5 text-slate-500 hover:text-rose-400 transition"
                          >
                            <Trash2 className="h-3 w-3" />
                          </button>
                        )}
                      </div>
                    )
                  })}
                </div>
              </aside>

              {/* Canvas */}
              <div className="flex-1 min-w-0 relative">
                {loading ? (
                  <div className="flex h-full w-full items-center justify-center text-xs text-slate-400 font-mono">
                    Carregando regras de automação...
                  </div>
                ) : (
                  <AutomationCanvas
                    initialGraph={graph}
                    activeFlowId={activeFlowId}
                    obsConnected={obsConnected}
                    onSaveGraph={handleSaveGraph}
                    onTestTrigger={handleTestTrigger}
                  />
                )}
              </div>
            </>
          ) : (
            /* Aba de Histórico de Execução */
            <div className="flex flex-1 flex-col p-5 overflow-y-auto text-xs">
              <div className="mb-3 flex items-center justify-between">
                <span className="font-semibold text-slate-300">
                  Últimas Execuções ({history.length})
                </span>
                <div className="flex items-center gap-2">
                  <button
                    type="button"
                    onClick={loadHistory}
                    className="flex items-center gap-1 rounded bg-slate-800 px-2.5 py-1 text-slate-200 hover:bg-slate-700"
                  >
                    <RefreshCw className="h-3 w-3" /> Atualizar
                  </button>
                  <button
                    type="button"
                    onClick={async () => {
                      await fetch('/api/automation/history', { method: 'DELETE' })
                      setHistory([])
                    }}
                    className="flex items-center gap-1 rounded bg-rose-500/15 px-2.5 py-1 text-rose-300 hover:bg-rose-500/25"
                  >
                    <Trash2 className="h-3 w-3" /> Limpar
                  </button>
                </div>
              </div>

              {history.length === 0 ? (
                <div className="flex flex-1 items-center justify-center text-slate-500">
                  Nenhuma execução registrada nesta sessão. Clique em &quot;Testar Fluxo&quot; ou projete um slide.
                </div>
              ) : (
                <div className="flex flex-col gap-2">
                  {history.map((entry) => (
                    <div
                      key={entry.id}
                      className="flex items-center justify-between rounded-lg border border-white/10 bg-slate-900/70 px-3.5 py-2.5"
                    >
                      <div className="flex items-center gap-3">
                        <span
                          className={`rounded px-2 py-0.5 text-[10px] font-bold uppercase ${
                            entry.status === 'success'
                              ? 'bg-emerald-500/20 text-emerald-300'
                              : entry.status === 'error'
                                ? 'bg-rose-500/20 text-rose-300'
                                : 'bg-slate-700 text-slate-300'
                          }`}
                        >
                          {entry.status}
                        </span>
                        <div>
                          <p className="font-semibold text-slate-100">{entry.triggerType}</p>
                          <p className="text-[11px] text-slate-400">{entry.details}</p>
                        </div>
                      </div>
                      <span className="font-mono text-[10px] text-slate-500">
                        {new Date(entry.timestamp).toLocaleTimeString()}
                      </span>
                    </div>
                  ))}
                </div>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
