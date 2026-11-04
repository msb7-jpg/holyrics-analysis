/**
 * @module @slices/liturgy/ui/components/LiturgyItemCard
 * @description Card individual de item polimórfico renderizado na coluna do roteiro litúrgico.
 *
 * ## Responsabilidades:
 * - Exibir ícone, título, subtítulo e badges de acordo com o tipo (Louvor, Bíblia, Mídia, Aviso, Contagem).
 * - Suporte a arraste (dragstart, dragover, drop) para reordenação nativa (RF-22C).
 * - Suporte a menu de contexto por clique com o botão direito (RF-22B).
 */

import { Music, BookOpen, Film, FileText, Clock, Presentation } from 'lucide-react'
import { Badge } from '@ui/components/ui/badge'
import type { LiturgyItem } from '../../contracts/liturgy-schema'

export interface LiturgyItemCardProps {
  item: LiturgyItem
  index: number
  isLive: boolean
  isDragging?: boolean
  onSelect: (item: LiturgyItem) => void
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
        badgeBg: 'bg-sky-500/10 text-sky-300 border-sky-500/20',
        icon: Music,
        subtitle: item.artist ? `${item.artist}` : 'Acervo musical',
      }
    case 'BIBLE':
      return {
        label: 'Escrituras',
        color: 'text-amber-400',
        badgeBg: 'bg-amber-500/10 text-amber-300 border-amber-500/20',
        icon: BookOpen,
        subtitle: `Versão ${item.version || 'ACF'}`,
      }
    case 'MEDIA':
      return {
        label: 'Mídia',
        color: 'text-purple-400',
        badgeBg: 'bg-purple-500/10 text-purple-300 border-purple-500/20',
        icon: Film,
        subtitle: item.mediaType || 'Vídeo/Imagem',
      }
    case 'PPTX':
      return {
        label: 'Slide',
        color: 'text-orange-400',
        badgeBg: 'bg-orange-500/10 text-orange-300 border-orange-500/20',
        icon: Presentation,
        subtitle: 'Apresentação externa',
      }
    case 'COUNTDOWN':
      return {
        label: 'Contagem',
        color: 'text-emerald-400',
        badgeBg: 'bg-emerald-500/10 text-emerald-300 border-emerald-500/20',
        icon: Clock,
        subtitle: `${Math.round((item.targetDurationSeconds || 300) / 60)} min cronometrados`,
      }
    case 'TEXT':
    default:
      return {
        label: 'Anotação',
        color: 'text-indigo-400',
        badgeBg: 'bg-indigo-500/10 text-indigo-300 border-indigo-500/20',
        icon: FileText,
        subtitle: `${item.content.length} linhas de texto`,
      }
  }
}

/**
 * Card de exibição do item com suporte a Drag & Drop nativo.
 *
 * @param props - Propriedades do card.
 * @returns Elemento JSX do card.
 */
export function LiturgyItemCard({
  item,
  index,
  isLive,
  isDragging,
  onSelect,
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
      className={`group relative flex cursor-pointer flex-col rounded-lg border p-2.5 transition select-none ${
        isDragging ? 'opacity-40 border-dashed border-sky-400 bg-slate-800/40' : ''
      } ${
        isLive
          ? 'border-sky-500/50 bg-slate-800 shadow-md ring-1 ring-sky-500/30'
          : 'border-white/10 bg-slate-800/80 hover:bg-slate-800 hover:border-white/20'
      }`}
    >
      <div className="flex items-center justify-between gap-1">
        <span className={`flex items-center gap-1.5 text-[10px] font-mono font-medium uppercase ${meta.color}`}>
          <Icon className="h-3 w-3" />
          {meta.label}
        </span>
        {isLive && <Badge variant="live">AO VIVO</Badge>}
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
