/**
 * @module @slices/automation/ui/components/AutomationModal
 * @description Modal de tela cheia/amplo para configuração visual do motor de automações DAG.
 *
 * ## Responsabilidades:
 * - Carregar o grafo ativo do daemon via GET `/api/automation`.
 * - Exibir o `AutomationCanvas` para visualização e edição das regras de automação.
 * - Despachar alterações via PUT `/api/automation` e disparos manuais de teste.
 */

import { useState, useEffect } from 'react'
import { X, Cpu } from 'lucide-react'
import type { AutomationGraph } from '../../contracts/automation-contracts'
import { DEFAULT_AUTOMATION_GRAPH } from '../../contracts/automation-contracts'
import { AutomationCanvas } from './AutomationCanvas'

export interface AutomationModalProps {
  isOpen: boolean
  onClose: () => void
}

/**
 * Diálogo modal para gerenciamento e criação de automações por grafo.
 *
 * @param props - Propriedades do modal.
 * @param props.isOpen - Se o modal está visível.
 * @param props.onClose - Função para fechar o modal.
 * @returns Elemento JSX do modal.
 */
export function AutomationModal({ isOpen, onClose }: AutomationModalProps) {
  const [graph, setGraph] = useState<AutomationGraph>(DEFAULT_AUTOMATION_GRAPH)
  const [obsConnected, setObsConnected] = useState(false)
  const [loading, setLoading] = useState(false)

  // Carrega automações ao abrir
  useEffect(() => {
    if (!isOpen) return

    setLoading(true)
    fetch('/api/automation')
      .then((res) => res.json())
      .then((json: { ok: boolean; data?: { graph: AutomationGraph; obsStatus: { connected: boolean } } }) => {
        if (json.ok && json.data) {
          setGraph(json.data.graph)
          setObsConnected(json.data.obsStatus.connected)
        }
      })
      .catch(() => {
        // Fallback para grafo padrão caso o backend não responda
      })
      .finally(() => {
        setLoading(false)
      })
  }, [isOpen])

  // Fecha ao pressionar Escape
  useEffect(() => {
    if (!isOpen) return

    const handleKeyDown = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        onClose()
      }
    }

    window.addEventListener('keydown', handleKeyDown)
    return () => window.removeEventListener('keydown', handleKeyDown)
  }, [isOpen, onClose])

  if (!isOpen) return null

  const handleSaveGraph = async (updatedGraph: AutomationGraph) => {
    const res = await fetch('/api/automation', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(updatedGraph),
    })

    if (!res.ok) {
      const err = await res.json()
      throw new Error(err.error || 'Erro ao salvar automações')
    }

    setGraph(updatedGraph)
  }

  const handleTestTrigger = async (triggerType: string) => {
    await fetch('/api/automation/test-trigger', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ triggerType }),
    })
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-6 select-none animate-in fade-in duration-150">
      <div className="flex h-[90vh] w-full max-w-6xl flex-col rounded-xl border border-white/10 bg-slate-950 shadow-2xl overflow-hidden">
        {/* Cabeçalho do Modal */}
        <div className="flex items-center justify-between border-b border-white/10 bg-slate-900 px-5 py-3">
          <div className="flex items-center gap-2.5">
            <span className="flex h-7 w-7 items-center justify-center rounded-lg bg-sky-500/20 text-sky-400">
              <Cpu className="h-4 w-4" />
            </span>
            <div>
              <h2 className="text-sm font-bold text-slate-100">Automação Litúrgica por Grafo (DAG)</h2>
              <p className="text-[11px] text-slate-400">
                Gatilhos da projeção integrados com OBS Studio v5, webhooks HTTP e DMX
              </p>
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

        {/* Corpo com o Canvas */}
        <div className="flex-1 relative">
          {loading ? (
            <div className="flex h-full w-full items-center justify-center text-xs text-slate-400 font-mono">
              Carregando regras de automação...
            </div>
          ) : (
            <AutomationCanvas
              initialGraph={graph}
              obsConnected={obsConnected}
              onSaveGraph={handleSaveGraph}
              onTestTrigger={handleTestTrigger}
            />
          )}
        </div>
      </div>
    </div>
  )
}
