/**
 * @module @slices/bible/ui/components/BibleQuickLookupCard
 * @description Card compacto exibido na Coluna 2 quando uma referência bíblica é reconhecida na busca.
 */

import { BookOpen, CornerDownLeft } from 'lucide-react'
import { Badge } from '@ui/components/ui/badge'
import type { BibleLookupResponse } from '../../contracts/bible-contracts'

export interface BibleQuickLookupCardProps {
  lookup: BibleLookupResponse
  onProject: (lookup: BibleLookupResponse) => void
}

/**
 * Card destacado que permite ao operador projetar a referência bíblica instantaneamente.
 *
 * @param props - Propriedades do card.
 * @returns Elemento JSX com o resumo do versículo e atalho de projeção.
 */
export function BibleQuickLookupCard({ lookup, onProject }: BibleQuickLookupCardProps) {
  const firstVerse = lookup.verses[0]
  if (!firstVerse) return null

  return (
    <div
      onClick={() => onProject(lookup)}
      className="flex flex-col gap-1.5 rounded-lg border border-zinc-700 bg-zinc-900 p-2.5 cursor-pointer hover:bg-zinc-800 transition mb-2 select-none"
    >
      <div className="flex items-center justify-between">
        <div className="flex items-center gap-1.5">
          <BookOpen className="h-3.5 w-3.5 text-zinc-400" />
          <span className="font-semibold text-xs text-white">{lookup.reference}</span>
        </div>
        <div className="flex items-center gap-2">
          <Badge variant="bible">{lookup.version}</Badge>
          <div className="flex items-center gap-1 text-[10px] text-zinc-400 font-mono">
            <span>Projetar</span>
            <CornerDownLeft className="h-2.5 w-2.5" />
          </div>
        </div>
      </div>

      <p className="text-[11px] text-zinc-300 line-clamp-2 leading-relaxed">
        <span className="font-mono text-zinc-400 font-bold mr-1">{firstVerse.verse}.</span>
        {firstVerse.text}
      </p>
    </div>
  )
}
