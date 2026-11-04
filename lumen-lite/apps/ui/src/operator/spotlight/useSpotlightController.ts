/**
 * @module @ui/operator/spotlight/useSpotlightController
 * @description Headless Controller para a Command Palette universal (SpotlightDialog).
 *
 * ## Responsabilidades:
 * - Gerenciar estado do termo de busca com debounce reativo via @siberiacancode/reactuse.
 * - Orquestrar chamadas paralelas ao TanStack Query v5 (Bible Lookup, Bible Search e Song Search).
 * - Disparar lookup instantâneo e cached ao selecionar resultados de versículos.
 * - Desacoplar 100% da lógica assíncrona da árvore JSX.
 */

import { useState } from 'react'
import { useDebounceValue } from '@siberiacancode/reactuse'
import { useQueryClient } from '@tanstack/react-query'
import { useSongSearchQuery } from '@slices/songs/ui/queries/songs-queries'
import {
  useBibleLookupQuery,
  useBibleSearchQuery,
  bibleLookupQueryOptions,
} from '@slices/bible/ui/queries/bible-queries'
import type { BibleLookupResponse } from '@slices/bible/contracts/bible-contracts'

export interface UseSpotlightControllerOptions {
  isOpen: boolean
  onClose: () => void
  onSelectSong: (songId: string) => void
  onSelectBible: (lookup: BibleLookupResponse) => void
  onTogglePanic?: (target: 'blackout' | 'clearText' | 'freeze') => void
}

/**
 * Hook headless com a lógica de busca e controle do Spotlight Dialog.
 *
 * @param options - Callbacks de seleção e fechamento.
 * @param options.isOpen - Se o diálogo modal está aberto.
 * @param options.onClose - Callback disparado para fechar o diálogo.
 * @param options.onSelectSong - Callback acionado ao selecionar música.
 * @param options.onSelectBible - Callback acionado ao selecionar passagem bíblica.
 * @param options.onTogglePanic - Callback opcional para disparar ação de pânico.
 * @returns Estado da busca, listas filtradas e manipuladores de ação.
 */
export function useSpotlightController({
  isOpen,
  onClose,
  onSelectSong,
  onSelectBible,
  onTogglePanic,
}: UseSpotlightControllerOptions) {
  const [searchTerm, setSearchTerm] = useState('')
  const debouncedSearch = useDebounceValue(searchTerm, 120)
  const queryClient = useQueryClient()

  const cleanQuery = debouncedSearch.trim()
  const isEnabled = isOpen && Boolean(cleanQuery)

  // 1. Consulta pontual de versículo (ex: "jo 3 16")
  const { data: bibleLookup, isLoading: isLookupLoading } = useBibleLookupQuery(
    cleanQuery,
    'ACF',
    isEnabled
  )

  // 2. Busca de músicas via FTS5
  const { data: songsData = [], isLoading: isSongsLoading } = useSongSearchQuery(
    cleanQuery,
    8,
    isEnabled && !bibleLookup
  )

  // 3. Busca textual nas Escrituras via FTS5
  const { data: bibleSearchData = [], isLoading: isBibleSearchLoading } = useBibleSearchQuery(
    cleanQuery,
    8,
    'ACF',
    isEnabled && !bibleLookup
  )

  const isLoading = isEnabled && (isLookupLoading || isSongsLoading || isBibleSearchLoading)

  const handleExecuteBibleLookup = () => {
    if (!bibleLookup) return
    onSelectBible(bibleLookup)
    onClose()
  }

  const handleSelectSongItem = (songId: string) => {
    onSelectSong(songId)
    onClose()
  }

  const handleSelectBibleSearchItem = async (reference: string) => {
    try {
      const lookup = await queryClient.fetchQuery(bibleLookupQueryOptions(reference))
      if (lookup) {
        onSelectBible(lookup as BibleLookupResponse)
        onClose()
      }
    } catch (err) {
      console.error('Erro ao projetar passagem bíblica:', err)
    }
  }

  const handleExecutePanic = (target: 'blackout' | 'clearText' | 'freeze') => {
    if (onTogglePanic) {
      onTogglePanic(target)
    }
    onClose()
  }

  return {
    searchTerm,
    setSearchTerm,
    isLoading,
    bibleLookup,
    songResults: songsData,
    bibleResults: bibleSearchData,
    handleExecuteBibleLookup,
    handleSelectSongItem,
    handleSelectBibleSearchItem,
    handleExecutePanic,
  }
}
