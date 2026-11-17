/**
 * @module @slices/keymap/ui/components/KeymapModal
 * @description Modal para consulta e gerenciamento dos atalhos de teclado (keybindings.json) no padrão Slate Pro Studio.
 *
 * ## Responsabilidades:
 * - Listar todos os atalhos configurados com busca dinâmica e formatação visual dos combos.
 * - Permitir restaurar atalhos de fábrica via POST `/api/keymap/reset`.
 * - Fornecer feedback visual em caso de ações concluídas.
 */

import { useState, useEffect, useMemo } from 'react'
import { X, Keyboard, Search, RotateCcw, Check } from 'lucide-react'
import type { Keybinding, KeymapConfig } from '../../contracts/keymap-contracts'
import { DEFAULT_KEYMAP_CONFIG, formatKeyComboDisplay } from '../../contracts/keymap-contracts'

export interface KeymapModalProps {
  isOpen: boolean
  onClose: () => void
}

/**
 * Diálogo modal para consulta e restauração dos atalhos de teclado.
 *
 * @param props - Propriedades do modal.
 * @param props.isOpen - Se o modal está aberto.
 * @param props.onClose - Função para fechar o modal.
 * @returns Elemento JSX do modal.
 */
export function KeymapModal({ isOpen, onClose }: KeymapModalProps) {
  const [bindings, setBindings] = useState<Keybinding[]>(DEFAULT_KEYMAP_CONFIG.bindings)
  const [searchQuery, setSearchQuery] = useState('')
  const [resetFeedback, setResetFeedback] = useState(false)
  const isApple = useMemo(() => {
    if (typeof navigator === 'undefined') return false
    return /Mac|iPhone|iPad|iPod/i.test(navigator.userAgent)
  }, [])

  // Carrega atalhos da API ao abrir
  useEffect(() => {
    if (!isOpen) return

    fetch('/api/keymap')
      .then((res) => res.json())
      .then((json: { ok: boolean; data?: KeymapConfig }) => {
        if (json.ok && json.data?.bindings) {
          setBindings(json.data.bindings)
        }
      })
      .catch(() => {
        // Fallback para os padrões caso o daemon esteja inacessível
      })
  }, [isOpen])

  // Fecha com tecla Escape
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

  const filteredBindings = useMemo(() => {
    const query = searchQuery.toLowerCase().trim()
    if (!query) return bindings

    return bindings.filter(
      (item) =>
        item.description.toLowerCase().includes(query) ||
        item.combo.toLowerCase().includes(query) ||
        item.action.toLowerCase().includes(query),
    )
  }, [bindings, searchQuery])

  const handleReset = async () => {
    try {
      const res = await fetch('/api/keymap/reset', { method: 'POST' })
      const json = await res.json()
      if (json.ok && json.data?.bindings) {
        setBindings(json.data.bindings)
        setResetFeedback(true)
        setTimeout(() => setResetFeedback(false), 2500)
      }
    } catch {
      // Ignora erro de reset
    }
  }

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-6 select-none animate-in fade-in duration-150">
      <div className="flex h-[80vh] w-full max-w-4xl flex-col rounded-xl border border-zinc-800 bg-zinc-950 shadow-2xl overflow-hidden">
        {/* Cabeçalho do Modal */}
        <div className="flex items-center justify-between border-b border-zinc-800 bg-zinc-950 px-5 py-3">
          <div className="flex items-center gap-2.5">
            <span className="flex h-7 w-7 items-center justify-center rounded-lg bg-zinc-800 text-zinc-400">
              <Keyboard className="h-4 w-4" />
            </span>
            <div>
              <h2 className="text-sm font-bold text-zinc-100">Atalhos de Teclado (keybindings.json)</h2>
              <p className="text-[11px] text-zinc-400">
                Mapeamento declarativo de teclas para operação sem mouse e suporte a Stream Deck
              </p>
            </div>
          </div>

          <button
            type="button"
            onClick={onClose}
            className="rounded p-1 text-zinc-400 hover:bg-zinc-800 hover:text-zinc-100 transition"
            title="Fechar (Esc)"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        {/* Barra de Filtro e Ações */}
        <div className="flex items-center justify-between border-b border-zinc-800 bg-zinc-950 px-5 py-2.5">
          <div className="relative w-80">
            <Search className="absolute left-2.5 top-2.5 h-3.5 w-3.5 text-zinc-400" />
            <input
              type="text"
              placeholder="Filtrar por ação, tecla ou descrição..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              className="w-full rounded-md border border-zinc-800 bg-zinc-900 pl-8 pr-3 py-1.5 text-xs text-zinc-200 placeholder:text-zinc-500 focus:border-zinc-700 focus:outline-none"
            />
          </div>

          <button
            type="button"
            onClick={handleReset}
            className="flex items-center gap-1.5 rounded bg-zinc-900 border border-zinc-800 px-3 py-1.5 text-xs font-medium text-zinc-300 hover:bg-zinc-800 hover:text-white transition"
          >
            {resetFeedback ? (
              <>
                <Check className="h-3.5 w-3.5 text-emerald-400" />
                <span className="text-emerald-400">Restaurado!</span>
              </>
            ) : (
              <>
                <RotateCcw className="h-3.5 w-3.5 text-amber-400" />
                <span>Restaurar Padrões</span>
              </>
            )}
          </button>
        </div>

        {/* Tabela de Atalhos */}
        <div className="flex-1 overflow-y-auto p-5">
          <div className="rounded-lg border border-zinc-800 bg-zinc-950 overflow-hidden">
            <table className="w-full text-left border-collapse text-xs">
              <thead>
                <tr className="border-b border-zinc-800 bg-zinc-900 text-[11px] font-mono uppercase text-zinc-400">
                  <th className="py-2.5 px-4 font-semibold">Ação</th>
                  <th className="py-2.5 px-4 font-semibold">Descrição</th>
                  <th className="py-2.5 px-4 font-semibold">Escopo</th>
                  <th className="py-2.5 px-4 font-semibold text-right">Atalho</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5">
                {filteredBindings.map((item) => (
                  <tr key={item.id} className="hover:bg-white/[0.02] transition">
                    <td className="py-2.5 px-4 font-mono text-[11px] font-semibold text-zinc-400">
                      {item.action}
                    </td>
                    <td className="py-2.5 px-4 text-zinc-300">
                      {item.description}
                    </td>
                    <td className="py-2.5 px-4">
                      <span className="rounded bg-zinc-800 px-1.5 py-0.5 text-[10px] font-mono text-zinc-400">
                        {item.scope}
                      </span>
                    </td>
                    <td className="py-2.5 px-4 text-right">
                      <kbd className="inline-flex items-center rounded border border-white/15 bg-zinc-800 px-2 py-0.5 text-[11px] font-mono font-medium text-zinc-200 shadow-sm">
                        {formatKeyComboDisplay(item.combo, isApple)}
                      </kbd>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  )
}
