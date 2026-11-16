/**
 * @module @slices/bible/ui/components/BibleVerseGrid
 * @description Grid de versículos bíblicos de uma passagem selecionada com suporte a Tela Dividida (Dual Version).
 */

import { BookOpen, Columns2 } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
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
      <div className="mb-2 flex items-center justify-between text-xs font-semibold">
        <span className="uppercase truncate flex items-center gap-2 text-slate-100">
          <span
            className={`inline-block h-2 w-2 rounded-full shrink-0 ${
              isLiveOnScreen ? 'bg-emerald-400 shadow-[0_0_8px_rgba(16,185,129,0.6)]' : 'bg-sky-400'
            }`}
          />
          <BookOpen className="h-3.5 w-3.5 text-sky-400" />
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
              <Columns2 className="h-3.5 w-3.5" />
              <span>{dualVersion ? `2ª Versão: ${dualVersion}` : 'Comparar Versão'}</span>
            </Button>
          )}
          <span className="text-[11px] text-slate-400 font-mono">
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
              className={`mr-3.5 font-mono text-xs font-bold pt-0.5 select-none ${
                isCurrentLive
                  ? 'text-emerald-400'
                  : isFocused
                    ? 'text-sky-400'
                    : 'text-sky-400/80'
              }`}
            >
              {verseItem.verse}
            </span>

            <div className="flex-1 flex flex-col gap-1.5">
              <p
                className={`font-sans text-[15px] leading-[1.5] transition-colors ${
                  isCurrentLive
                    ? 'text-[#F8FAFC] font-medium'
                    : isFocused
                      ? 'text-slate-100'
                      : 'text-[#94A3B8] group-hover:text-slate-200'
                }`}
              >
                {verseItem.text}
              </p>

              {secondaryVerse && (
                <p className="border-t border-white/10 pt-1.5 font-sans text-sm leading-relaxed text-amber-200/90">
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
