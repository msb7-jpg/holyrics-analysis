/**
 * @module @slices/navigation/ui/components/SlidePreviewCard
 * @description Card padronizado de pré-visualização de slides 16:9 (Slate Pro Studio Refined).
 *
 * ## Responsabilidades:
 * - Unificar a renderização de miniaturas de slides em Músicas (SongImporterModal) e PPTX/PDF (OperatorView, PptxDeckModal).
 * - Aplicar semântica visual de estado: Inativo (#141820), Foco (#18202C / Sky) e Ao Vivo (Esmeralda).
 * - Renderizar fundos via thumbnail estática (<img> com loading="lazy" e decoding="async"), evitando decodificadores <video> em grades.
 */

import { StickyNote } from 'lucide-react'
import { LiveIndicator } from '@ui/components/ui/live-indicator'

export interface SlidePreviewCardProps {
  /** Número do slide (base 1). */
  slideNumber: number
  /** Linhas de texto do slide. */
  lines?: string[]
  /** URL da imagem renderizada do slide (ex.: PPTX/PDF). */
  imageUrl?: string
  /** URL da thumbnail de fundo (imagem ou poster de vídeo). */
  backgroundThumbUrl?: string
  /** Notas do orador (exibidas na variante detailed). */
  speakerNotes?: string
  /** Indica se este slide está ao vivo no telão. */
  isLive?: boolean
  /** Indica se este slide está focado no workspace. */
  isFocused?: boolean
  /** Variante visual: compact (grades 2 colunas) ou detailed (lista com notas). */
  variant?: 'compact' | 'detailed'
  /** Callback de clique simples. */
  onClick?: () => void
  /** Callback de duplo clique. */
  onDoubleClick?: () => void
  /** Classes CSS adicionais. */
  className?: string
}

/**
 * Card padronizado de pré-visualização de slide 16:9.
 *
 * @param props - Propriedades do card.
 * @returns Elemento JSX do card de slide.
 */
export function SlidePreviewCard({
  slideNumber,
  lines = [],
  imageUrl,
  backgroundThumbUrl,
  speakerNotes,
  isLive = false,
  isFocused = false,
  variant = 'compact',
  onClick,
  onDoubleClick,
  className = '',
}: SlidePreviewCardProps) {
  const stateClasses = isLive
    ? 'border-zinc-300 bg-zinc-900 ring-1 ring-zinc-200'
    : isFocused
      ? 'border-zinc-700 bg-zinc-900/90'
      : 'border-zinc-800/60 bg-zinc-900/40 hover:bg-zinc-900/80 hover:border-zinc-700'

  if (variant === 'detailed') {
    return (
      <div
        onClick={onClick}
        onDoubleClick={onDoubleClick}
        className={`relative flex cursor-pointer flex-col gap-2.5 rounded-xl border p-4 transition select-none ${stateClasses} ${className}`}
      >
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span
              className={`rounded px-1.5 py-0.5 tabular-nums font-mono text-[10px] font-medium uppercase ${
                isLive
                  ? 'bg-zinc-800 text-zinc-100'
                  : isFocused
                    ? 'bg-zinc-800 text-zinc-200'
                    : 'bg-zinc-950/80 text-zinc-400 border border-zinc-800'
              }`}
            >
              SLIDE {String(slideNumber).padStart(2, '0')}
            </span>
            {isLive && <LiveIndicator variant="icon" />}
          </div>
          <span className="text-[11px] font-mono text-zinc-400">Clique para Projetar</span>
        </div>

        {imageUrl ? (
          <div className="overflow-hidden rounded-lg border border-zinc-800 bg-black">
            <img
              src={imageUrl}
              alt={`Slide ${slideNumber}`}
              loading="lazy"
              decoding="async"
              className="max-h-56 w-full object-contain"
            />
          </div>
        ) : (
          <div className="relative aspect-video w-full overflow-hidden rounded-lg border border-zinc-800 bg-zinc-950/70 flex items-center justify-center p-4 text-center">
            {backgroundThumbUrl && (
              <img
                src={backgroundThumbUrl}
                alt=""
                loading="lazy"
                decoding="async"
                className="absolute inset-0 h-full w-full object-cover opacity-45"
              />
            )}
            <div className="relative z-10 text-sm font-semibold text-white drop-shadow-[0_1px_3px_rgba(0,0,0,0.9)]">
              {lines.map((line, lineIdx) => (
                <p key={lineIdx}>{line}</p>
              ))}
            </div>
          </div>
        )}

        {speakerNotes && (
          <div className="flex items-start gap-2 rounded-lg border border-amber-500/20 bg-amber-500/10 px-3 py-2 text-xs text-amber-200">
            <StickyNote className="mt-0.5 h-3.5 w-3.5 shrink-0 text-amber-400" />
            <p className="whitespace-pre-wrap leading-relaxed">{speakerNotes}</p>
          </div>
        )}
      </div>
    )
  }

  return (
    <div
      onClick={onClick}
      onDoubleClick={onDoubleClick}
      className={`group relative flex flex-col rounded-lg border overflow-hidden transition select-none ${
        onClick || onDoubleClick ? 'cursor-pointer' : ''
      } ${stateClasses} ${className}`}
    >
      <div className="flex items-center justify-between px-2.5 py-1.5 bg-zinc-950/70 text-[10px]">
        <span className="tabular-nums font-mono font-medium text-zinc-300">
          Slide {String(slideNumber).padStart(2, '0')}
        </span>
        {isLive ? (
          <LiveIndicator variant="icon" />
        ) : isFocused ? (
          <span className="rounded bg-zinc-800 px-1.5 py-0.5 font-mono text-[9px] font-medium text-zinc-300">
            FOCADO
          </span>
        ) : null}
      </div>

      <div className="relative aspect-video w-full bg-black flex items-center justify-center overflow-hidden p-2.5 text-center">
        {backgroundThumbUrl && (
          <img
            src={backgroundThumbUrl}
            alt=""
            loading="lazy"
            decoding="async"
            className="absolute inset-0 h-full w-full object-cover opacity-50"
          />
        )}
        {imageUrl ? (
          <img
            src={imageUrl}
            alt={`Slide ${slideNumber}`}
            loading="lazy"
            decoding="async"
            className="relative z-10 h-full w-full object-contain"
          />
        ) : (
          <div className="relative z-10 text-[11px] font-medium leading-snug text-zinc-100 drop-shadow-[0_1px_3px_rgba(0,0,0,0.9)] line-clamp-4">
            {lines.map((line, lIdx) => (
              <p key={lIdx}>{line}</p>
            ))}
          </div>
        )}
      </div>
    </div>
  )
}
