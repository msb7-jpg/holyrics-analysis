/**
 * @module @slices/liturgy/ui/components/LiturgyContextMenu
 * @description Menu de contexto completo (botão direito) no roteiro litúrgico para adição, reordenação, duplicação e remoção de itens.
 */

import {
  Trash2,
  Play,
  Clock,
  FileText,
  MessageSquare,
  Music,
  BookOpen,
  Image as ImageIcon,
  Presentation,
  Copy,
  ArrowUp,
  ArrowDown,
} from 'lucide-react'
import type { LiturgyItem } from '../../contracts/liturgy-schema'

export interface LiturgyContextMenuProps {
  posX: number
  posY: number
  targetItem?: LiturgyItem | null
  hasSelectedSong?: boolean
  hasSelectedBible?: boolean
  onClose: () => void
  onProjectItem?: (item: LiturgyItem) => void
  onDuplicateItem?: (item: LiturgyItem) => void
  onMoveItemUp?: (item: LiturgyItem) => void
  onMoveItemDown?: (item: LiturgyItem) => void
  onDeleteItem?: (itemId: string) => void
  onAddSelectedSong?: () => void
  onAddSelectedBible?: () => void
  onOpenQuickText?: () => void
  onAddCountdown?: () => void
  onAddNote?: () => void
  onAddMediaTab?: () => void
  onAddFileTab?: () => void
}

/**
 * Menu suspenso sob o cursor para ações rápidas de montagem e operação do roteiro de culto.
 *
 * @param props - Propriedades do menu de contexto.
 * @returns Elemento JSX do menu flutuante.
 */
export function LiturgyContextMenu({
  posX,
  posY,
  targetItem,
  hasSelectedSong,
  hasSelectedBible,
  onClose,
  onProjectItem,
  onDuplicateItem,
  onMoveItemUp,
  onMoveItemDown,
  onDeleteItem,
  onAddSelectedSong,
  onAddSelectedBible,
  onOpenQuickText,
  onAddCountdown,
  onAddNote,
  onAddMediaTab,
  onAddFileTab,
}: LiturgyContextMenuProps) {
  return (
    <>
      <div
        className="fixed inset-0 z-40"
        onClick={onClose}
        onContextMenu={(event) => {
          event.preventDefault()
          onClose()
        }}
      />
      <div
        data-testid="liturgy-context-menu"
        className="fixed z-50 min-w-56 rounded-xl border border-white/10 bg-[#0F1622]/98 p-1.5 shadow-2xl backdrop-blur-md text-xs text-slate-200"
        style={{
          left: Math.min(posX, window.innerWidth - 240),
          top: Math.min(posY, window.innerHeight - 340),
        }}
      >
        {targetItem ? (
          <>
            <div className="px-2.5 py-1 text-[10px] font-semibold text-slate-400 uppercase tracking-wider border-b border-white/5 mb-1 truncate max-w-[210px]">
              {targetItem.title}
            </div>
            {onProjectItem && (
              <button
                type="button"
                className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-emerald-500/20 hover:text-emerald-300 transition"
                onClick={() => {
                  onProjectItem(targetItem)
                  onClose()
                }}
              >
                <Play className="h-3.5 w-3.5 text-emerald-400" />
                <span>Projetar Agora</span>
              </button>
            )}
            {onDuplicateItem && (
              <button
                type="button"
                className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-[#192334] transition"
                onClick={() => {
                  onDuplicateItem(targetItem)
                  onClose()
                }}
              >
                <Copy className="h-3.5 w-3.5 text-sky-400" />
                <span>Duplicar no Roteiro</span>
              </button>
            )}
            {onMoveItemUp && (
              <button
                type="button"
                className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-[#192334] transition"
                onClick={() => {
                  onMoveItemUp(targetItem)
                  onClose()
                }}
              >
                <ArrowUp className="h-3.5 w-3.5 text-slate-400" />
                <span>Mover para Cima</span>
              </button>
            )}
            {onMoveItemDown && (
              <button
                type="button"
                className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-[#192334] transition"
                onClick={() => {
                  onMoveItemDown(targetItem)
                  onClose()
                }}
              >
                <ArrowDown className="h-3.5 w-3.5 text-slate-400" />
                <span>Mover para Baixo</span>
              </button>
            )}
            {onDeleteItem && (
              <button
                type="button"
                className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-rose-500/20 text-rose-300 transition"
                onClick={() => {
                  onDeleteItem(targetItem.id)
                  onClose()
                }}
              >
                <Trash2 className="h-3.5 w-3.5" />
                <span>Remover do Roteiro</span>
              </button>
            )}
            <div className="my-1 border-t border-white/10" />
          </>
        ) : null}

        <div className="px-2.5 py-1 text-[10px] font-semibold text-slate-400 uppercase tracking-wider">
          Adicionar ao Roteiro
        </div>

        {hasSelectedSong && onAddSelectedSong && (
          <button
            type="button"
            className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-[#192334] transition"
            onClick={() => {
              onAddSelectedSong()
              onClose()
            }}
          >
            <Music className="h-3.5 w-3.5 text-sky-400" />
            <span>Música Selecionada</span>
          </button>
        )}

        {hasSelectedBible && onAddSelectedBible && (
          <button
            type="button"
            className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-[#192334] transition"
            onClick={() => {
              onAddSelectedBible()
              onClose()
            }}
          >
            <BookOpen className="h-3.5 w-3.5 text-amber-400" />
            <span>Leitura Bíblica Atual</span>
          </button>
        )}

        {onOpenQuickText && (
          <button
            type="button"
            className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-[#192334] transition"
            onClick={() => {
              onOpenQuickText()
              onClose()
            }}
          >
            <MessageSquare className="h-3.5 w-3.5 text-amber-400" />
            <span>Aviso Rápido / Texto</span>
          </button>
        )}

        {onAddCountdown && (
          <button
            type="button"
            className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-[#192334] transition"
            onClick={() => {
              onAddCountdown()
              onClose()
            }}
          >
            <Clock className="h-3.5 w-3.5 text-emerald-400" />
            <span>Contagem Regressiva (5 min)</span>
          </button>
        )}

        {onAddMediaTab && (
          <button
            type="button"
            className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-[#192334] transition"
            onClick={() => {
              onAddMediaTab()
              onClose()
            }}
          >
            <ImageIcon className="h-3.5 w-3.5 text-purple-400" />
            <span>Imagem ou Vídeo...</span>
          </button>
        )}

        {onAddFileTab && (
          <button
            type="button"
            className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-[#192334] transition"
            onClick={() => {
              onAddFileTab()
              onClose()
            }}
          >
            <Presentation className="h-3.5 w-3.5 text-orange-400" />
            <span>Arquivo / Apresentação...</span>
          </button>
        )}

        {onAddNote && (
          <button
            type="button"
            className="flex w-full items-center gap-2 rounded-lg px-2.5 py-1.5 text-left hover:bg-[#192334] transition"
            onClick={() => {
              onAddNote()
              onClose()
            }}
          >
            <FileText className="h-3.5 w-3.5 text-indigo-400" />
            <span>Anotação de Culto</span>
          </button>
        )}
      </div>
    </>
  )
}
