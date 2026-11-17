/**
 * @module @slices/bible/ui/components/BibleVerseGrid
 * @description Grid de versículos bíblicos de uma passagem selecionada com suporte a Tela Dividida (Dual Version).
 */

import { BookOpen, Columns2 } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { LiveIndicator } from '@ui/components/ui/live-indicator'
import type { BibleLookupResponse } from '../../contracts/bible-contracts'

export interface BibleVerseGridProps {
  activeBible: BibleLookupResponse
  activeSlideIndex?: number
  isLiveOnScreen?: boolean
  focusedIndex?: number
  dualVersion?: string | null
  onChangeDualVersion?: (dualVersion: string | null) => void
  onSelectVerse?: (index: number) => void
  onProjectVerse?: (index: number) => void
}

/**
 * Grid de versículos de uma leitura bíblica no painel central do operador.
 *
 * @param props - Propriedades do componente.
 * @param props.activeBible - Dados da leitura bíblica ativa.
 * @param props.activeSlideIndex - Índice do versículo atualmente no ar.
 * @param props.isLiveOnScreen - Se o modo atual no telão é BIBLE.
 * @param props.focusedIndex - Índice do versículo focado no workspace.
 * @param props.dualVersion - Sigla da 2ª versão ativa (se houver).
 * @param props.onChangeDualVersion - Callback para ativar/desativar segunda versão.
 * @param props.onSelectVerse - Callback disparado ao focar/selecionar versículo (1 clique).
 * @param props.onProjectVerse - Callback disparado ao projetar versículo (duplo clique).
 * @returns Elemento JSX com os versículos listados.
 */
export function BibleVerseGrid({
  activeBible,
  activeSlideIndex,
  isLiveOnScreen,
  focusedIndex,
  dualVersion,
  onChangeDualVersion,
  onSelectVerse,
  onProjectVerse,
}: BibleVerseGridProps) {
  return (
    <div className="flex flex-col gap-2">
      <div className="mb-1.5 flex items-center justify-between text-xs font-semibold">
        <span className="uppercase truncate flex items-center gap-2 text-zinc-100">
          <BookOpen className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
          <span>
            {activeBible.reference} ({activeBible.version}
            {activeBible.dualVersion ? ` + ${activeBible.dualVersion}` : ''})
          </span>
        </span>

        <div className="flex items-center gap-2">
          {onChangeDualVersion && (
            <Button
              size="sm"
              variant={dualVersion ? 'activeInfo' : 'secondary'}
              onClick={() => onChangeDualVersion(dualVersion ? null : 'NVI')}
              title="Exibir duas traduções bíblicas lado a lado no telão"
            >
              <Columns2 className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
              <span>{dualVersion ? `2ª Versão: ${dualVersion}` : 'Comparar Versão'}</span>
            </Button>
          )}
          <span className="text-[11px] text-zinc-500 tabular-nums font-mono">
            {activeBible.verses.length}{' '}
            {activeBible.verses.length === 1 ? 'versículo' : 'versículos'}
          </span>
        </div>
      </div>

      {activeBible.verses.map((verseItem, index) => {
        const isCurrentLive = Boolean(isLiveOnScreen && activeSlideIndex === index)
        const isFocused = !isCurrentLive && focusedIndex === index
        const secondaryVerse = activeBible.secondaryVerses?.[index]

        return (
          <div
            key={verseItem.id}
            onClick={() => onSelectVerse?.(index)}
            onDoubleClick={() => onProjectVerse?.(index)}
            className={`group relative flex cursor-pointer rounded-xl p-4 transition select-none ${
              isCurrentLive
                ? 'bg-zinc-900 border border-zinc-300 ring-1 ring-zinc-200'
                : isFocused
                  ? 'bg-zinc-900/90 border border-zinc-700'
                  : 'bg-zinc-900/40 hover:bg-zinc-900/80 border border-zinc-800/60 hover:border-zinc-700'
            }`}
          >
            {isCurrentLive && (
              <LiveIndicator variant="icon" className="absolute top-3 right-3" />
            )}

            <span
              className={`mr-3.5 tabular-nums font-mono text-xs pt-0.5 select-none ${
                isCurrentLive
                  ? 'text-zinc-200 font-medium'
                  : isFocused
                    ? 'text-zinc-300'
                    : 'text-zinc-500 group-hover:text-zinc-400'
              }`}
            >
              {String(verseItem.verse).padStart(2, '0')}
            </span>

            <div className="flex-1 flex flex-col gap-1.5 pr-7">
              <p
                className={`font-sans text-[14px] leading-relaxed transition-colors ${
                  isCurrentLive
                    ? 'text-zinc-50 font-medium'
                    : isFocused
                      ? 'text-zinc-100'
                      : 'text-zinc-300 group-hover:text-zinc-100'
                }`}
              >
                {verseItem.text}
              </p>

              {secondaryVerse && (
                <p className="border-t border-white/5 pt-1.5 font-sans text-sm leading-relaxed text-amber-200/90">
                  <span className="mr-1.5 font-mono text-[10px] font-bold text-amber-400">
                    [{activeBible.dualVersion}]
                  </span>
                  {secondaryVerse.text}
                </p>
              )}
            </div>
          </div>
        )
      })}
    </div>
  )
}
