/**
 * @module @ui/operator/components/SpotlightDialog
 * @description Command Palette / Spotlight universal do LumenLite construído sobre cmdk e TanStack Query v5.
 *
 * ## Responsabilidades:
 * - Prover busca universal instantânea com atalhos de teclado e acessibilidade W3C ARIA.
 * - Compor headless controller desacoplado de rede (useSpotlightController).
 * - Renderizar grupos modulares de Ações Rápidas, Escrituras e Louvores.
 */

import { useEffect } from 'react'
import { Command } from 'cmdk'
import { Search, Loader2 } from 'lucide-react'
import { Kbd } from '@ui/components/ui/kbd'
import { useSpotlightController } from '../spotlight/useSpotlightController'
import { SpotlightQuickActions } from '../spotlight/SpotlightQuickActions'
import { SpotlightBibleResults } from '../spotlight/SpotlightBibleResults'
import { SpotlightSongResults } from '../spotlight/SpotlightSongResults'
import type { BibleLookupResponse } from '@slices/bible/contracts/bible-contracts'

export interface SpotlightDialogProps {
  isOpen: boolean
  onClose: () => void
  onSelectSong: (songId: string) => void
  onSelectBible: (lookup: BibleLookupResponse) => void
  onTogglePanic?: (target: 'blackout' | 'clearText' | 'freeze') => void
}

/**
 * Diálogo modal de Command Palette limpo e desacoplado.
 *
 * @param props - Propriedades do componente.
 * @param props.isOpen - Se o Spotlight está visível na tela.
 * @param props.onClose - Callback de fechamento do diálogo.
 * @param props.onSelectSong - Callback acionado ao selecionar uma canção.
 * @param props.onSelectBible - Callback acionado ao projetar leitura bíblica.
 * @param props.onTogglePanic - Callback opcional para disparar comandos de emergência.
 * @returns Elemento JSX com a Command Palette do cmdk.
 */
export function SpotlightDialog({
  isOpen,
  onClose,
  onSelectSong,
  onSelectBible,
  onTogglePanic,
}: SpotlightDialogProps) {
  const {
    searchTerm,
    setSearchTerm,
    isLoading,
    bibleLookup,
    songResults,
    bibleResults,
    handleExecuteBibleLookup,
    handleSelectSongItem,
    handleSelectBibleSearchItem,
    handleExecutePanic,
  } = useSpotlightController({
    isOpen,
    onClose,
    onSelectSong,
    onSelectBible,
    onTogglePanic,
  })

  useEffect(() => {
    if (!isOpen) return

    const handleEscape = (event: KeyboardEvent) => {
      if (event.key === 'Escape') {
        event.preventDefault()
        onClose()
      }
    }

    window.addEventListener('keydown', handleEscape)
    return () => window.removeEventListener('keydown', handleEscape)
  }, [isOpen, onClose])

  if (!isOpen) return null

  const isQuickActionsVisible =
    !searchTerm ||
    'blackout'.includes(searchTerm.toLowerCase()) ||
    'ocultar'.includes(searchTerm.toLowerCase()) ||
    'congelar'.includes(searchTerm.toLowerCase())

  return (
    <div
      onClick={(event) => {
        if (event.target === event.currentTarget) {
          onClose()
        }
      }}
      className="fixed inset-0 z-50 flex items-start justify-center pt-20 bg-slate-950/70 backdrop-blur-sm animate-in fade-in duration-100"
    >
      <Command
        shouldFilter={false}
        label="Spotlight Command Menu"
        className="relative w-full max-w-2xl rounded-xl border border-white/10 bg-slate-900 shadow-2xl overflow-hidden flex flex-col max-h-[75vh]"
      >
        {/* Barra de Entrada de Comando */}
        <div className="relative flex items-center border-b border-white/10 px-4 bg-slate-900/90">
          <Search className="h-4 w-4 text-slate-400 mr-3 shrink-0" />
          <Command.Input
            value={searchTerm}
            onValueChange={setSearchTerm}
            placeholder="Digite para buscar músicas, versículos (jo 3 16) ou ações (blackout)..."
            className="flex-1 bg-transparent py-3.5 text-sm text-slate-100 placeholder:text-slate-500 focus:outline-none"
          />
          {isLoading && <Loader2 className="h-4 w-4 animate-spin text-sky-400 mr-2 shrink-0" />}
          <Kbd>ESC</Kbd>
        </div>

        {/* Lista de Resultados e Ações */}
        <Command.List className="flex-1 overflow-y-auto p-2 scroll-py-2 [&_[cmdk-group-heading]]:text-[10px] [&_[cmdk-group-heading]]:font-semibold [&_[cmdk-group-heading]]:uppercase [&_[cmdk-group-heading]]:tracking-wider [&_[cmdk-group-heading]]:text-slate-400 [&_[cmdk-group-heading]]:px-2 [&_[cmdk-group-heading]]:py-1.5">
          <Command.Empty className="py-8 text-center text-xs text-slate-500">
            Nenhum resultado encontrado para &quot;{searchTerm}&quot;
          </Command.Empty>

          {isQuickActionsVisible && (
            <SpotlightQuickActions onExecutePanic={handleExecutePanic} />
          )}

          <SpotlightBibleResults
            bibleLookup={bibleLookup}
            bibleResults={bibleResults}
            onExecuteBibleLookup={handleExecuteBibleLookup}
            onSelectBibleSearchItem={handleSelectBibleSearchItem}
          />

          <SpotlightSongResults
            songs={songResults}
            onSelectSong={handleSelectSongItem}
          />
        </Command.List>

        {/* Rodapé Informativo */}
        <footer className="flex items-center justify-between border-t border-white/5 bg-slate-900/80 px-4 py-2 text-[11px] text-slate-400">
          <div className="flex items-center gap-3">
            <span>
              <Kbd>↑↓</Kbd> Navegar
            </span>
            <span>
              <Kbd>↵ Enter</Kbd> Executar / Projetar
            </span>
          </div>
          <span className="font-mono text-[10px] text-slate-500">LumenLite cmdk Spotlight</span>
        </footer>
      </Command>
    </div>
  )
}
