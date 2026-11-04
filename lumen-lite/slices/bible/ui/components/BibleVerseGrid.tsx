/**
 * @module @slices/bible/ui/components/BibleVerseGrid
 * @description Grid de versículos bíblicos de uma passagem selecionada para projeção no telão.
 */

import { BookOpen } from 'lucide-react'
import { Badge } from '@ui/components/ui/badge'
import type { BibleLookupResponse } from '../../contracts/bible-contracts'

export interface BibleVerseGridProps {
  activeBible: BibleLookupResponse
  activeSlideIndex?: number
  isLiveOnScreen?: boolean
  onSelectVerse: (index: number) => void
}

/**
 * Grid de versículos de uma leitura bíblica no painel do operador.
 *
 * @param props - Propriedades do componente.
 * @returns Elemento JSX com os versículos listados.
 */
export function BibleVerseGrid({
  activeBible,
  activeSlideIndex,
  isLiveOnScreen,
  onSelectVerse,
}: BibleVerseGridProps) {
  return (
    <div className="flex flex-col gap-2">
      <div className="mb-2 flex items-center justify-between text-xs font-semibold text-sky-400">
        <span className="uppercase truncate flex items-center gap-1.5">
          <BookOpen className="h-3.5 w-3.5" />
          {activeBible.reference} ({activeBible.version})
        </span>
        <span className="text-[10px] text-slate-400 font-mono">
          {activeBible.verses.length}{' '}
          {activeBible.verses.length === 1 ? 'versículo' : 'versículos'}
        </span>
      </div>

      {activeBible.verses.map((verseItem, idx) => {
        const isCurrent = isLiveOnScreen && activeSlideIndex === idx
        return (
          <div
            key={verseItem.id}
            onClick={() => onSelectVerse(idx)}
            className={`group relative flex cursor-pointer rounded-lg border p-3 transition select-none ${
              isCurrent
                ? 'border-sky-500/50 bg-slate-800/90 shadow-sm'
                : 'border-white/5 bg-slate-800/40 hover:bg-slate-800/70'
            }`}
          >
            {isCurrent && (
              <div className="absolute left-0 top-0 bottom-0 w-1 rounded-l bg-sky-400" />
            )}

            <span className="mr-3 font-mono text-xs font-bold text-sky-400">
              {verseItem.verse}
            </span>

            <div className="flex-1">
              <p className="font-sans text-xs leading-relaxed text-slate-200">
                {verseItem.text}
              </p>
            </div>

            {isCurrent && <Badge variant="live">NO AR</Badge>}
          </div>
        )
      })}
    </div>
  )
}
