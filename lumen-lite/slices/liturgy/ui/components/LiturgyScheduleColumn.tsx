/**
 * @module @slices/liturgy/ui/components/LiturgyScheduleColumn
 * @description Coluna 1 do operador contendo o roteiro litúrgico do culto (`culto.json`).
 *
 * ## Responsabilidades:
 * - Renderizar a sequência litúrgica polimórfica (HItems).
 * - Indicar o item atualmente AO VIVO na FSM do projetor.
 * - Suportar reordenação nativa por Drag & Drop (RF-22C).
 * - Suportar menu de contexto por botão direito (RF-22B).
 * - Acionar o modal de Quick Text (RF-22A).
 */

import { Calendar, Plus, Loader2 } from 'lucide-react'
import type { ScreenState } from '@lumen/core'
import type { Song } from '@slices/songs/contracts/song-contracts'
import type { BibleLookupResponse } from '@slices/bible/contracts/bible-contracts'
import type { LiturgyItem } from '../../contracts/liturgy-schema'
import { LiturgyItemCard } from './LiturgyItemCard'
import { LiturgyContextMenu } from './LiturgyContextMenu'
import { QuickTextModal } from './QuickTextModal'
import { useLiturgyController } from '../hooks/useLiturgyController'

export interface LiturgyScheduleColumnProps {
  state: ScreenState
  selectedSong?: Song | null
  activeBible?: BibleLookupResponse | null
  onSelectItem?: (item: LiturgyItem) => void
}

/**
 * Coluna interativa do roteiro litúrgico do culto.
 *
 * @param props - Propriedades do roteiro.
 * @returns Elemento JSX com a lista de itens e ações de contexto.
 */
export function LiturgyScheduleColumn({
  state,
  onSelectItem,
}: LiturgyScheduleColumnProps) {
  const {
    liturgy,
    isLoading,
    draggedIndex,
    contextMenu,
    isQuickTextOpen,
    setIsQuickTextOpen,
    handleDragStart,
    handleDragOver,
    handleDrop,
    handleOpenContextMenu,
    handleCloseContextMenu,
    handleAddCountdown,
    handleAddNote,
    handleRemoveItem,
  } = useLiturgyController()

  // Verifica se o item da liturgia é o que está ao vivo na projeção
  const isItemLive = (item: LiturgyItem): boolean => {
    if (state.activeItemId === item.id) return true
    if (item.type === 'SONG' && state.mode === 'SONG' && state.activeItemId === item.songId) return true
    if (item.type === 'BIBLE' && state.mode === 'BIBLE' && state.activeItemId === `bible_${item.reference}`) return true
    return false
  }

  const items = liturgy?.items || []

  return (
    <section
      className="col-span-3 flex flex-col bg-slate-900 p-3 overflow-y-auto relative"
      onContextMenu={(event) => {
        // Se clicar no espaço vazio da coluna, abre menu genérico
        if (event.target === event.currentTarget) {
          handleOpenContextMenu(event, null)
        }
      }}
    >
      {/* Cabeçalho da Liturgia */}
      <div className="mb-3 flex items-center justify-between text-xs font-semibold uppercase tracking-wider text-slate-400">
        <span className="flex items-center gap-1.5">
          <Calendar className="h-3.5 w-3.5 text-sky-400" />
          Roteiro do Culto
        </span>
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={() => setIsQuickTextOpen(true)}
            title="Lançar aviso de emergência (Quick Text)"
            className="flex items-center gap-1 text-[10px] text-sky-400 hover:text-sky-300 font-sans normal-case bg-sky-500/10 px-2 py-0.5 rounded border border-sky-500/20 transition"
          >
            <Plus className="h-3 w-3" />
            Aviso
          </button>
          <span className="text-[10px] text-slate-500 font-mono">culto.json</span>
        </div>
      </div>

      {/* Lista de Itens com Drag & Drop */}
      <div className="flex flex-1 flex-col gap-1.5 text-xs">
        {isLoading ? (
          <div className="flex flex-1 items-center justify-center py-10">
            <Loader2 className="h-5 w-5 animate-spin text-sky-400" />
          </div>
        ) : items.length === 0 ? (
          <div
            className="flex flex-col items-center justify-center py-12 text-center text-xs text-slate-500 border border-dashed border-white/10 rounded-lg cursor-pointer hover:border-sky-500/30"
            onClick={() => setIsQuickTextOpen(true)}
          >
            <Calendar className="h-8 w-8 mb-2 opacity-40 text-slate-400" />
            <p className="font-medium text-slate-400">Roteiro vazio</p>
            <p className="text-[11px] text-slate-600 mt-1">Clique para adicionar itens ou clique com o botão direito</p>
          </div>
        ) : (
          items.map((item, index) => (
            <LiturgyItemCard
              key={item.id}
              item={item}
              index={index}
              isLive={isItemLive(item)}
              isDragging={draggedIndex === index}
              onSelect={(selected) => onSelectItem?.(selected)}
              onContextMenu={handleOpenContextMenu}
              onDragStart={handleDragStart}
              onDragOver={handleDragOver}
              onDrop={handleDrop}
            />
          ))
        )}
      </div>

      {/* Menu de Contexto (Botão Direito) */}
      {contextMenu.isOpen && (
        <LiturgyContextMenu
          x={contextMenu.x}
          y={contextMenu.y}
          targetItem={contextMenu.item}
          onClose={handleCloseContextMenu}
          onProjectItem={onSelectItem}
          onDeleteItem={handleRemoveItem}
          onOpenQuickText={() => setIsQuickTextOpen(true)}
          onAddCountdown={handleAddCountdown}
          onAddNote={handleAddNote}
        />
      )}

      {/* Modal de Quick Text (RF-22A) */}
      <QuickTextModal
        isOpen={isQuickTextOpen}
        onClose={() => setIsQuickTextOpen(false)}
      />
    </section>
  )
}
