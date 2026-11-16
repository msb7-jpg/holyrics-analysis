/**
 * @module @slices/songs/ui/components/SongStanzaGrid
 * @description Grid central de estrofes de uma música selecionada (Coluna 3 do Operador).
 *
 * ## Responsabilidades:
 * - Exibir as estrofes de forma limpa, sem badges intrusivos ("NO AR" ou "[CORO]").
 * - Aplicar hierarquia de superfície (#192334 sem bordas externas) e semântica de estado (Esmeralda #10B981 quando ao vivo).
 * - Tipografia legível de cabine (15px, line-height 1.5, alto contraste #F8FAFC no ar vs #94A3B8 inativo).
 */

import type { Song } from '../../contracts/song-contracts'

export interface SongStanzaGridProps {
  song: Song
  activeSlideIndex?: number
  isLiveOnScreen?: boolean
  focusedIndex?: number
  onSelectStanza?: (index: number) => void
  onProjectStanza?: (index: number) => void
}

/**
 * Grid de estrofes da música no painel central do operador.
 *
 * @param props - Propriedades do componente.
 * @param props.song - Objeto da música selecionada.
 * @param props.activeSlideIndex - Índice da estrofe atualmente projetada.
 * @param props.isLiveOnScreen - Se esta música é o item efetivamente ao vivo no telão.
 * @param props.focusedIndex - Índice da estrofe com foco local no workspace.
 * @param props.onSelectStanza - Callback disparado ao selecionar/focar uma estrofe (1 clique).
 * @param props.onProjectStanza - Callback disparado ao projetar uma estrofe para o telão (2 cliques).
 * @returns Elemento JSX com os cartões de estrofes.
 */
export function SongStanzaGrid({
  song,
  activeSlideIndex,
  isLiveOnScreen,
  focusedIndex,
  onSelectStanza,
  onProjectStanza,
}: SongStanzaGridProps) {
  return (
    <div className="flex flex-col gap-2">
      <div className="mb-2 flex items-center justify-between text-xs font-semibold">
        <div className="flex items-center gap-2 truncate">
          <span
            className={`inline-block h-2 w-2 rounded-full shrink-0 ${
              isLiveOnScreen ? 'bg-emerald-400 shadow-[0_0_8px_rgba(16,185,129,0.6)]' : 'bg-sky-400'
            }`}
            title={isLiveOnScreen ? 'Música ao vivo no telão' : 'Música em visualização (Preview)'}
          />
          <span className="uppercase tracking-wide text-slate-100 truncate">
            {song.title} <span className="text-slate-400 font-normal">— {song.artist}</span>
          </span>
        </div>
        <span className="text-[11px] text-slate-400 font-mono shrink-0 ml-2">
          {song.sections.length} {song.sections.length === 1 ? 'estrofe' : 'estrofes'}
        </span>
      </div>

      {song.sections.map((section, index) => {
        const isCurrentLive = Boolean(isLiveOnScreen && activeSlideIndex === index)
        const isFocused = !isCurrentLive && focusedIndex === index

        return (
          <div
            key={section.id}
            onClick={() => onSelectStanza?.(index)}
            onDoubleClick={() => onProjectStanza?.(index)}
            className={`group relative flex cursor-pointer rounded-[6px] p-3.5 transition select-none ${
              isCurrentLive
                ? 'bg-[#1D2D3E] ring-1 ring-emerald-500/50 shadow-sm'
                : isFocused
                  ? 'bg-[#172338] ring-1 ring-sky-500/40'
                  : 'bg-[#192334] hover:bg-[#1E2B40]'
            }`}
          >
            {isCurrentLive && (
              <>
                <div className="absolute left-0 top-0 bottom-0 w-1 rounded-l-[6px] bg-emerald-500" />
                <span className="absolute top-2 right-2 rounded bg-emerald-500/20 px-1.5 py-0.5 text-[9px] font-mono font-bold text-emerald-300">
                  AO VIVO
                </span>
              </>
            )}

            <span
              className={`mr-3.5 font-mono text-xs font-semibold pt-0.5 select-none ${
                isCurrentLive
                  ? 'text-emerald-400'
                  : isFocused
                    ? 'text-sky-400'
                    : 'text-slate-500 group-hover:text-slate-400'
              }`}
            >
              {String(index + 1).padStart(2, '0')}
            </span>

            <p
              className={`flex-1 whitespace-pre-line font-sans text-[15px] leading-[1.5] transition-colors ${
                isCurrentLive
                  ? 'text-[#F8FAFC] font-medium'
                  : isFocused
                    ? 'text-slate-100'
                    : 'text-[#94A3B8] group-hover:text-slate-200'
              }`}
            >
              {section.lines.join('\n')}
            </p>
          </div>
        )
      })}
    </div>
  )
}
