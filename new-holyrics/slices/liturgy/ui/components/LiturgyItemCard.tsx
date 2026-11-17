/**
 * @module @slices/liturgy/ui/components/LiturgyItemCard
 * @description Card individual de item polimórfico renderizado na coluna do roteiro litúrgico.
 */

import { Music, BookOpen, Film, FileText, Clock, Presentation } from 'lucide-react'
import { LiveIndicator } from '@ui/components/ui/live-indicator'
import type { LiturgyItem } from '../../contracts/liturgy-schema'

export interface LiturgyItemCardProps {
  item: LiturgyItem
  index: number
  isLive: boolean
  isSelected?: boolean
  isDragging?: boolean
  onSelect: (item: LiturgyItem) => void
  onProject?: (item: LiturgyItem) => void
  onContextMenu: (event: React.MouseEvent, item: LiturgyItem) => void
  onDragStart: (event: React.DragEvent, index: number) => void
  onDragOver: (event: React.DragEvent, index: number) => void
  onDrop: (event: React.DragEvent, index: number) => void
}

/**
 * Retorna o ícone e cor correspondente ao tipo de item polimórfico.
 *
 * @param item - Item litúrgico.
 * @returns Metadados visuais de cor e ícone.
 */
function getItemMeta(item: LiturgyItem) {
  switch (item.type) {
    case 'SONG':
      return {
        label: 'Louvor',
        color: 'text-zinc-400',
        icon: Music,
        subtitle: item.artist ? `${item.artist}` : 'Acervo musical',
      }
    case 'BIBLE':
      return {
        label: 'Escrituras',
        color: 'text-zinc-400',
        icon: BookOpen,
        subtitle: `Versão ${item.version || 'ACF'}`,
      }
    case 'MEDIA':
      return {
        label: item.mediaType === 'VIDEO' ? 'Vídeo' : 'Imagem',
        color: 'text-zinc-400',
        icon: Film,
        subtitle: item.mediaType === 'VIDEO' ? 'Vídeo / Loop' : 'Imagem de fundo',
      }
    case 'PPTX':
      return {
        label: 'Arquivo',
        color: 'text-zinc-400',
        icon: Presentation,
        subtitle: 'Apresentação / Slides',
      }
    case 'COUNTDOWN':
      return {
        label: 'Contagem',
        color: 'text-zinc-400',
        icon: Clock,
        subtitle: `${Math.round((item.targetDurationSeconds || 300) / 60)} min cronometrados`,
      }
    case 'TEXT':
    default:
      return {
        label: item.category === 'ALERT' || item.category === 'ANNOUNCEMENT' ? 'Aviso' : 'Anotação',
        color: 'text-zinc-400',
        icon: FileText,
        subtitle: `${item.content.length} ${item.content.length === 1 ? 'linha' : 'linhas'}`,
      }
  }
}

/**
 * Card de exibição do item com superfície sem borda (#192334), semântica Preview (Azul) vs Live (Esmeralda) e Drag & Drop.
 *
 * @param props - Propriedades do card.
 * @returns Elemento JSX do card.
 */
export function LiturgyItemCard({
  item,
  index,
  isLive,
  isSelected = false,
  isDragging = false,
  onSelect,
  onProject,
  onContextMenu,
  onDragStart,
  onDragOver,
  onDrop,
}: LiturgyItemCardProps) {
  const meta = getItemMeta(item)
  const Icon = meta.icon

  return (
    <div
      draggable
      onDragStart={(event) => onDragStart(event, index)}
      onDragOver={(event) => onDragOver(event, index)}
      onDrop={(event) => onDrop(event, index)}
      onContextMenu={(event) => onContextMenu(event, item)}
      onClick={() => onSelect(item)}
      onDoubleClick={() => onProject?.(item)}
      className={`group relative flex cursor-pointer flex-col rounded-lg p-2.5 transition select-none ${
        isDragging ? 'opacity-40 ring-1 ring-dashed ring-zinc-400 bg-zinc-900/40' : ''
      } ${
        isLive
          ? 'bg-zinc-900 text-zinc-100'
          : isSelected
            ? 'bg-zinc-800/70 text-zinc-50'
            : 'hover:bg-zinc-900/60 text-zinc-300'
      }`}
    >
      <div className="flex items-center justify-between gap-1">
        <span className={`flex items-center gap-1.5 text-[10px] font-mono font-medium uppercase ${
          isLive ? 'text-zinc-300' : isSelected ? 'text-zinc-200' : 'text-zinc-500'
        }`}>
          <Icon className="h-3 w-3" strokeWidth={1.75} />
          {meta.label}
        </span>
        {isLive && <LiveIndicator variant="dot" />}
      </div>

      <p className={`mt-1 text-xs truncate ${isLive || isSelected ? 'font-medium text-zinc-50' : 'font-medium text-zinc-200'}`}>
        <span className="tabular-nums font-mono text-zinc-500 mr-1">{String(index + 1).padStart(2, '0')}.</span>
        {item.title}
      </p>

      <p className="text-[11px] text-zinc-400 truncate">
        {meta.subtitle}
      </p>
    </div>
  )
}
