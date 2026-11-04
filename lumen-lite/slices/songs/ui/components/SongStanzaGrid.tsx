/**
 * @module @slices/songs/ui/components/SongStanzaGrid
 * @description Exibição das estrofes numeradas e navegação de slides de uma canção.
 */

import { Badge } from '@ui/components/ui/badge'
import type { Song } from '../../contracts/song-contracts'

export interface SongStanzaGridProps {
  song: Song
  activeSlideIndex?: number
  isLiveOnScreen?: boolean
  onSelectStanza: (index: number) => void
}

/**
 * Grid de estrofes de uma música selecionada para o operador.
 *
 * @param props - Propriedades do componente.
 * @returns Elemento JSX com estrofes ordenadas.
 */
export function SongStanzaGrid({
  song,
  activeSlideIndex,
  isLiveOnScreen,
  onSelectStanza,
}: SongStanzaGridProps) {
  return (
    <div className="flex flex-col gap-2">
      <div className="mb-2 flex items-center justify-between text-xs font-semibold text-slate-400">
        <span className="uppercase truncate">{song.title}</span>
        <span className="text-[10px] text-slate-500 font-mono">
          {song.sections.length} estrofes
        </span>
      </div>

      {song.sections.map((section, idx) => {
        const isCurrent = isLiveOnScreen && activeSlideIndex === idx
        return (
          <div
            key={section.id}
            onClick={() => onSelectStanza(idx)}
            className={`group relative flex cursor-pointer rounded-lg border p-3 transition select-none ${
              isCurrent
                ? 'border-sky-500/50 bg-slate-800/90 shadow-sm'
                : 'border-white/5 bg-slate-800/40 hover:bg-slate-800/70'
            }`}
          >
            {isCurrent && (
              <div className="absolute left-0 top-0 bottom-0 w-1 rounded-l bg-sky-400" />
            )}

            <span className="mr-3 font-mono text-xs text-slate-500 group-hover:text-slate-300">
              {section.index}
            </span>

            <div className="flex-1">
              <pre className="font-sans text-xs leading-relaxed text-slate-200 whitespace-pre-wrap">
                {section.lines.join('\n')}
              </pre>
            </div>

            {isCurrent && <Badge variant="live">NO AR</Badge>}
          </div>
        )
      })}
    </div>
  )
}
