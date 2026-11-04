/**
 * @module @ui/operator/spotlight/SpotlightBibleResults
 * @description Grupos de resultados bíblicos (lookup pontual e busca FTS5) para o Spotlight.
 */

import { BookOpen, CornerDownLeft } from 'lucide-react'
import { Command } from 'cmdk'
import type { BibleLookupResponse, BibleSearchResult } from '@slices/bible/contracts/bible-contracts'

export interface SpotlightBibleResultsProps {
  bibleLookup?: BibleLookupResponse | null
  bibleResults?: BibleSearchResult[]
  onExecuteBibleLookup: () => void
  onSelectBibleSearchItem: (reference: string) => void
}

/**
 * Renderiza os grupos de Escrituras Sagradas no cmdk.
 *
 * @param props - Propriedades com os dados de consulta.
 * @param props.bibleLookup - Resultado pontual de lookup de referência se houver.
 * @param props.bibleResults - Lista de versículos encontrados via FTS5.
 * @param props.onExecuteBibleLookup - Ação acionada para projetar o versículo pontual.
 * @param props.onSelectBibleSearchItem - Ação acionada ao clicar em versículo textual.
 * @returns Elemento JSX com os grupos cmdk.
 */
export function SpotlightBibleResults({
  bibleLookup,
  bibleResults = [],
  onExecuteBibleLookup,
  onSelectBibleSearchItem,
}: SpotlightBibleResultsProps) {
  return (
    <>
      {bibleLookup && (
        <Command.Group heading={`Escrituras Sagradas (${bibleLookup.version})`}>
          <Command.Item
            value={`bible_lookup_${bibleLookup.reference}`}
            onSelect={onExecuteBibleLookup}
            className="flex flex-col gap-1.5 rounded-lg border border-sky-500/40 bg-sky-500/10 p-3 cursor-pointer transition data-[selected=true]:bg-sky-500/25 text-slate-200"
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2">
                <BookOpen className="h-4 w-4 text-sky-400" />
                <span className="font-semibold text-white text-sm">{bibleLookup.reference}</span>
              </div>
              <div className="flex items-center gap-1.5">
                <span className="text-[10px] font-mono text-sky-300">Projetar</span>
                <CornerDownLeft className="h-3 w-3 text-sky-400" />
              </div>
            </div>

            <div className="flex flex-col gap-1">
              {bibleLookup.verses.map((verseItem) => (
                <p key={verseItem.id} className="text-xs text-slate-300 leading-relaxed">
                  <span className="font-mono text-sky-400 mr-1.5 font-bold">{verseItem.verse}.</span>
                  {verseItem.text}
                </p>
              ))}
            </div>
          </Command.Item>
        </Command.Group>
      )}

      {bibleResults.length > 0 && (
        <Command.Group heading="Versículos Bíblicos Encontrados">
          {bibleResults.map((verseResult) => (
            <Command.Item
              key={verseResult.id}
              value={`bible_search_${verseResult.id}`}
              onSelect={() => onSelectBibleSearchItem(verseResult.reference)}
              className="flex flex-col gap-0.5 rounded-lg px-3 py-2 text-xs cursor-pointer transition data-[selected=true]:bg-sky-500/20 data-[selected=true]:text-white text-slate-300"
            >
              <div className="flex items-center justify-between">
                <span className="font-semibold text-sky-300 font-mono">{verseResult.reference}</span>
                <span className="text-[10px] text-slate-500 font-mono">ACF</span>
              </div>
              {verseResult.snippet ? (
                <p
                  className="text-xs text-slate-300 leading-relaxed"
                  dangerouslySetInnerHTML={{ __html: verseResult.snippet }}
                />
              ) : (
                <p className="text-xs text-slate-300 leading-relaxed">{verseResult.text}</p>
              )}
            </Command.Item>
          ))}
        </Command.Group>
      )}
    </>
  )
}
