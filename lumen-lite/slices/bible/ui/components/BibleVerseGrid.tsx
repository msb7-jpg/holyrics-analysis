/**
 * @module @slices/bible/ui/components/BibleVerseGrid
 * @description Grid de versículos bíblicos de uma passagem selecionada com suporte a Tela Dividida (Dual Version - RF-14).
 */

import { BookOpen, Columns2 } from 'lucide-react'
import { Badge } from '@ui/components/ui/badge'
import { Button } from '@ui/components/ui/button'
import type { BibleLookupResponse } from '../../contracts/bible-contracts'

export interface BibleVerseGridProps {
  activeBible: BibleLookupResponse
  activeSlideIndex?: number
  isLiveOnScreen?: boolean
  dualVersion?: string | null
  onChangeDualVersion?: (dualVersion: string | null) => void
  onSelectVerse: (index: number) => void
}

/**
 * Grid de versículos de uma leitura bíblica no painel do operador com chaveamento Dual Version (RF-14).
 *
 * @param props - Propriedades do componente.
 * @param props.activeBible - Dados da leitura bíblica ativa.
 * @param props.activeSlideIndex - Índice do versículo atualmente no ar.
 * @param props.isLiveOnScreen - Se o modo atual no telão é BIBLE.
 * @param props.dualVersion - Sigla da 2ª versão ativa (se houver).
 * @param props.onChangeDualVersion - Callback para ativar/desativar Dual Version.
 * @param props.onSelectVerse - Callback disparado ao clicar em um versículo.
 * @returns Elemento JSX com os versículos listados.
 */
export function BibleVerseGrid({
  activeBible,
  activeSlideIndex,
  isLiveOnScreen,
  dualVersion,
  onChangeDualVersion,
  onSelectVerse,
}: BibleVerseGridProps) {
  return (
    <div className="flex flex-col gap-2">
      <div className="mb-2 flex items-center justify-between text-xs font-semibold text-sky-400">
        <span className="uppercase truncate flex items-center gap-1.5">
          <BookOpen className="h-3.5 w-3.5" />
          {activeBible.reference} ({activeBible.version}
          {activeBible.dualVersion ? ` + ${activeBible.dualVersion}` : ''})
        </span>

        <div className="flex items-center gap-2">
          {onChangeDualVersion && (
            <Button
              size="sm"
              variant={dualVersion ? 'activeInfo' : 'secondary'}
              onClick={() => onChangeDualVersion(dualVersion ? null : 'NVI')}
              title="Alternar Projeção em Tela Dividida (Dual Version - RF-14)"
            >
              <Columns2 className="h-3.5 w-3.5" />
              <span>{dualVersion ? `Dual: ${dualVersion}` : 'Dual Version'}</span>
            </Button>
          )}
          <span className="text-[10px] text-slate-400 font-mono">
            {activeBible.verses.length}{' '}
            {activeBible.verses.length === 1 ? 'versículo' : 'versículos'}
          </span>
        </div>
      </div>

      {activeBible.verses.map((verseItem, idx) => {
        const isCurrent = isLiveOnScreen && activeSlideIndex === idx
        const secondaryVerse = activeBible.secondaryVerses?.[idx]

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

            <div className="flex-1 flex flex-col gap-1.5">
              <p className="font-sans text-xs leading-relaxed text-slate-200">
                {verseItem.text}
              </p>

              {secondaryVerse && (
                <p className="border-t border-white/10 pt-1.5 font-sans text-xs leading-relaxed text-amber-200/90">
                  <span className="mr-1.5 font-mono text-[10px] font-bold text-amber-400">
                    [{activeBible.dualVersion}]
                  </span>
                  {secondaryVerse.text}
                </p>
              )}
            </div>

            {isCurrent && <Badge variant="live">NO AR</Badge>}
          </div>
        )
      })}
    </div>
  )
}
