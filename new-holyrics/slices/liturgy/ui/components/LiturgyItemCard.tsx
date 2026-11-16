/**
 * @module @slices/liturgy/ui/components/LiturgyItemCard
 * @description Card individual de item polimórfico renderizado na coluna do roteiro litúrgico.
 */

import { Music, BookOpen, Film, FileText, Clock, Presentation } from 'lucide-react'
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
        color: 'text-sky-400',
        icon: Music,
        subtitle: item.artist ? `${item.artist}` : 'Acervo musical',
      }
    case 'BIBLE':
      return {
        label: 'Escrituras',
        color: 'text-amber-400',
        icon: BookOpen,
        subtitle: `Versão ${item.version || 'ACF'}`,
      }
    case 'MEDIA':
      return {
        label: item.mediaType === 'VIDEO' ? 'Vídeo' : 'Imagem',
        color: 'text-purple-400',
        icon: Film,
        subtitle: item.mediaType === 'VIDEO' ? 'Vídeo / Loop' : 'Imagem de fundo',
      }
    case 'PPTX':
      return {
        label: 'Arquivo',
        color: 'text-orange-400',
        icon: Presentation,
        subtitle: 'Apresentação / Slides',
      }
    case 'COUNTDOWN':
      return {
        label: 'Contagem',
        color: 'text-emerald-400',
        icon: Clock,
        subtitle: `${Math.round((item.targetDurationSeconds || 300) / 60)} min cronometrados`,
      }
    case 'TEXT':
    default:
      return {
        label: item.category === 'ALERT' || item.category === 'ANNOUNCEMENT' ? 'Aviso' : 'Anotação',
        color: 'text-indigo-400',
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
      className={`group relative flex cursor-pointer flex-col rounded-[6px] p-2.5 transition select-none ${
        isDragging ? 'opacity-40 ring-1 ring-dashed ring-sky-400 bg-[#192334]/40' : ''
      } ${
        isLive
          ? 'bg-[#1D2D3E] ring-1 ring-emerald-500/50 shadow-sm'
          : isSelected
            ? 'bg-[#1B2A42] ring-1 ring-[#3B82F6]/60'
            : 'bg-[#192334] hover:bg-[#1E2B40]'
      }`}
    >
      {isLive && (
        <div className="absolute left-0 top-0 bottom-0 w-1 rounded-l-[6px] bg-emerald-500" />
      )}
      {!isLive && isSelected && (
        <div className="absolute left-0 top-0 bottom-0 w-1 rounded-l-[6px] bg-[#3B82F6]" />
      )}

      <div className="flex items-center justify-between gap-1">
        <span className={`flex items-center gap-1.5 text-[10px] font-mono font-medium uppercase ${meta.color}`}>
          <Icon className="h-3 w-3" />
          {meta.label}
        </span>
        {isLive && (
          <span className="flex items-center gap-1 text-[10px] font-medium text-emerald-400">
            <span className="h-1.5 w-1.5 rounded-full bg-emerald-400 shadow-[0_0_6px_rgba(16,185,129,0.8)]" />
            <span>No ar</span>
          </span>
        )}
      </div>

      <p className="mt-1 font-medium text-slate-100 text-xs truncate">
        {index + 1}. {item.title}
      </p>

      <p className="text-[11px] text-slate-400 truncate">
        {meta.subtitle}
      </p>
    </div>
  )
}
