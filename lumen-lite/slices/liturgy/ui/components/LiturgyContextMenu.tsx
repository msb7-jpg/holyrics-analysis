/**
 * @module @slices/liturgy/ui/components/LiturgyContextMenu
 * @description Menu de contexto (botão direito) no roteiro litúrgico (RF-22B).
 *
 * ## Responsabilidades:
 * - Apresentar opções contextuais sobre um item ou sobre a área vazia do roteiro.
 * - Inserir mídias, anotações pastorais, textos avulsos ou contagens.
 * - Remover ou duplicar itens.
 */

import { Trash2, Play, Clock, FileText, MessageSquare } from 'lucide-react'
import type { LiturgyItem } from '../../contracts/liturgy-schema'

export interface LiturgyContextMenuProps {
  x: number
  y: number
  targetItem?: LiturgyItem | null
  onClose: () => void
  onProjectItem?: (item: LiturgyItem) => void
  onDeleteItem?: (itemId: string) => void
  onOpenQuickText?: () => void
  onAddCountdown?: () => void
  onAddNote?: () => void
}

/**
 * Menu suspenso sob o cursor para ações rápidas no roteiro de culto.
 *
 * @param props - Propriedades do menu.
 * @returns Elemento JSX do menu flutuante.
 */
export function LiturgyContextMenu({
  x,
  y,
  targetItem,
  onClose,
  onProjectItem,
  onDeleteItem,
  onOpenQuickText,
  onAddCountdown,
  onAddNote,
}: LiturgyContextMenuProps) {
  return (
    <>
      <div className="fixed inset-0 z-40" onClick={onClose} onContextMenu={(e) => { e.preventDefault(); onClose(); }} />
      <div
        className="fixed z-50 min-w-48 rounded-lg border border-white/10 bg-slate-900/95 p-1.5 shadow-2xl backdrop-blur-md text-xs text-slate-200"
        style={{ left: Math.min(x, window.innerWidth - 200), top: Math.min(y, window.innerHeight - 250) }}
      >
        {targetItem ? (
          <>
            <div className="px-2 py-1 text-[10px] font-semibold text-slate-400 uppercase tracking-wider border-b border-white/5 mb-1">
              {targetItem.title}
            </div>
            {onProjectItem && (
              <button
                type="button"
                className="flex w-full items-center gap-2 rounded px-2 py-1.5 text-left hover:bg-sky-500/20 hover:text-sky-300 transition"
                onClick={() => {
                  onProjectItem(targetItem)
                  onClose()
                }}
              >
                <Play className="h-3.5 w-3.5 text-sky-400" />
                Projetar Imediatamente
              </button>
            )}
            {onDeleteItem && (
              <button
                type="button"
                className="flex w-full items-center gap-2 rounded px-2 py-1.5 text-left hover:bg-rose-500/20 text-rose-300 transition"
                onClick={() => {
                  onDeleteItem(targetItem.id)
                  onClose()
                }}
              >
                <Trash2 className="h-3.5 w-3.5" />
                Remover do Roteiro
              </button>
            )}
            <div className="my-1 border-t border-white/10" />
          </>
        ) : null}

        <div className="px-2 py-1 text-[10px] font-semibold text-slate-400 uppercase tracking-wider">
          Adicionar ao Roteiro
        </div>

        {onOpenQuickText && (
          <button
            type="button"
            className="flex w-full items-center gap-2 rounded px-2 py-1.5 text-left hover:bg-slate-800 transition"
            onClick={() => {
              onOpenQuickText()
              onClose()
            }}
          >
            <MessageSquare className="h-3.5 w-3.5 text-amber-400" />
            Aviso Rápido (Quick Text)
          </button>
        )}

        {onAddCountdown && (
          <button
            type="button"
            className="flex w-full items-center gap-2 rounded px-2 py-1.5 text-left hover:bg-slate-800 transition"
            onClick={() => {
              onAddCountdown()
              onClose()
            }}
          >
            <Clock className="h-3.5 w-3.5 text-emerald-400" />
            Contagem Regressiva (5 min)
          </button>
        )}

        {onAddNote && (
          <button
            type="button"
            className="flex w-full items-center gap-2 rounded px-2 py-1.5 text-left hover:bg-slate-800 transition"
            onClick={() => {
              onAddNote()
              onClose()
            }}
          >
            <FileText className="h-3.5 w-3.5 text-indigo-400" />
            Anotação Pastoral
          </button>
        )}
      </div>
    </>
  )
}
