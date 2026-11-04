/**
 * @module @ui/operator/hooks/useOperatorController
 * @description Headless Controller principal da tela do operador (OperatorView).
 *
 * ## Responsabilidades:
 * - Coordenar a seleção de músicas e capítulos bíblicos.
 * - Despachar apresentações de slides (PRESENT_ITEM) para a FSM.
 * - Conectar atalhos globais de teclado (useOperatorShortcuts).
 * - Sincronizar estado da busca e primeiro item do acervo automaticamente.
 */

import { useState, useEffect } from 'react'
import { useDebounceValue } from '@siberiacancode/reactuse'
import type { ActionCommand, ScreenState } from '@lumen/core'
import { useSongSearchQuery, useSongDetailQuery } from '@slices/songs/ui/queries/songs-queries'
import { useBibleLookupQuery } from '@slices/bible/ui/queries/bible-queries'
import { useOperatorShortcuts } from '@slices/keymap/ui/hooks/useOperatorShortcuts'
import type { BibleLookupResponse } from '@slices/bible/contracts/bible-contracts'
import type { LiturgyItem } from '@slices/liturgy/contracts/liturgy-schema'

export interface UseOperatorControllerOptions {
  state: ScreenState
  dispatch: (cmd: ActionCommand) => void
}

/**
 * Hook orquestrador do console do operador.
 *
 * @param options - Opções de configuração do controlador.
 * @param options.state - Estado atômico da projeção e pânico.
 * @param options.dispatch - Despachante tipado de comandos para a FSM.
 * @returns Estados e manipuladores de seleção e navegação.
 */
export function useOperatorController({ state, dispatch }: UseOperatorControllerOptions) {
  const [query, setQuery] = useState('')
  const debouncedQuery = useDebounceValue(query, 120)

  const [selectedSongId, setSelectedSongId] = useState<string | null>(null)
  const [activeBible, setActiveBible] = useState<BibleLookupResponse | null>(null)
  const [isSpotlightOpen, setIsSpotlightOpen] = useState(false)

  // 1. Busca textual de canções na Coluna 2
  const { data: songs = [], isLoading: isLoadingSongs } = useSongSearchQuery(debouncedQuery)

  // 2. Detalhes completos da canção selecionada
  const { data: currentSong, isLoading: isLoadingSongDetail } = useSongDetailQuery(selectedSongId)

  // 3. Reconhecimento de referência bíblica em tempo real na busca da Coluna 2
  const { data: bibleLookupResult } = useBibleLookupQuery(debouncedQuery, 'ACF')

  // Auto-seleciona a primeira canção caso nada esteja selecionado
  useEffect(() => {
    if (!selectedSongId && !activeBible && songs.length > 0) {
      setSelectedSongId(songs[0].id)
    }
  }, [songs, selectedSongId, activeBible])

  // Despacha apresentação do primeiro slide de uma música
  const handleSelectSong = (id: string) => {
    setActiveBible(null)
    setSelectedSongId(id)
  }

  // Quando os dados da música chegarem, se for uma nova seleção, despacha o slide inicial
  useEffect(() => {
    if (currentSong && state.mode !== 'SONG') {
      if (currentSong.sections.length > 0) {
        dispatch({
          type: 'PRESENT_ITEM',
          itemId: currentSong.id,
          mode: 'SONG',
          foreground: {
            title: currentSong.title,
            content: currentSong.sections[0].lines,
            slideIndex: 0,
            totalSlides: currentSong.sections.length,
          },
        })
      }
    }
  }, [currentSong, dispatch, state.mode])

  // Despacha estrofe específica
  const handleSelectSection = (index: number) => {
    if (!currentSong) return
    const section = currentSong.sections[index]
    if (!section) return

    dispatch({
      type: 'PRESENT_ITEM',
      itemId: currentSong.id,
      mode: 'SONG',
      foreground: {
        title: currentSong.title,
        content: section.lines,
        slideIndex: index,
        totalSlides: currentSong.sections.length,
      },
    })
  }

  // Seleciona e projeta passagem bíblica
  const handleSelectBible = (lookup: BibleLookupResponse) => {
    setSelectedSongId(null)
    setActiveBible(lookup)

    if (lookup.verses.length > 0) {
      dispatch({
        type: 'PRESENT_ITEM',
        itemId: `bible_${lookup.reference}`,
        mode: 'BIBLE',
        foreground: {
          title: lookup.reference,
          reference: lookup.reference,
          content: [lookup.verses[0].text],
          slideIndex: 0,
          totalSlides: lookup.verses.length,
        },
      })
    }
  }

  // Despacha versículo específico
  const handleSelectVerse = (index: number) => {
    if (!activeBible || !activeBible.verses[index]) return
    const currentVerse = activeBible.verses[index]

    dispatch({
      type: 'PRESENT_ITEM',
      itemId: `bible_${activeBible.reference}`,
      mode: 'BIBLE',
      foreground: {
        title: activeBible.reference,
        reference: `${activeBible.reference} (v.${currentVerse.verse})`,
        content: [currentVerse.text],
        slideIndex: index,
        totalSlides: activeBible.verses.length,
      },
    })
  }

  // Manipula seleção de item da coluna litúrgica
  const handleSelectLiturgyItem = (item: LiturgyItem) => {
    switch (item.type) {
      case 'SONG': {
        handleSelectSong(item.songId)
        break
      }
      case 'BIBLE': {
        // Dispara busca bíblica e projeta
        setActiveBible({
          reference: item.reference,
          book: item.reference,
          chapter: 1,
          verseStart: 1,
          version: item.version || 'ACF',
          verses: [
            {
              id: `${item.reference}_1`,
              bibleId: item.version || 'ACF',
              bookNumber: 1,
              bookName: item.reference,
              bookAbbrev: item.reference,
              chapter: 1,
              verse: 1,
              text: `Carregando ${item.reference}...`,
            },
          ],
        })
        break
      }
      case 'TEXT': {
        dispatch({
          type: 'PRESENT_ITEM',
          itemId: item.id,
          mode: 'EMPTY',
          foreground: {
            title: item.title,
            content: item.content,
            slideIndex: 0,
            totalSlides: 1,
          },
        })
        break
      }
      case 'COUNTDOWN': {
        dispatch({
          type: 'PRESENT_ITEM',
          itemId: item.id,
          mode: 'EMPTY',
          foreground: {
            title: item.title,
            content: [`Início em ${Math.round((item.targetDurationSeconds || 300) / 60)} minutos`],
            slideIndex: 0,
            totalSlides: 1,
          },
        })
        break
      }
      case 'MEDIA': {
        dispatch({
          type: 'PRESENT_ITEM',
          itemId: item.id,
          mode: 'MEDIA',
          foreground: {
            title: item.title,
            content: [],
            slideIndex: 0,
            totalSlides: 1,
          },
        })
        break
      }
    }
  }

  // Registra atalhos de teclado (Espaço, Backspace, F1-F3, Cmd+K)
  useOperatorShortcuts({
    isSpotlightOpen,
    onToggleSpotlight: () => setIsSpotlightOpen((prev) => !prev),
    onNavNext: () => dispatch({ type: 'NAV_NEXT' }),
    onNavPrev: () => dispatch({ type: 'NAV_PREV' }),
    onPanicBlackout: () => dispatch({ type: 'TOGGLE_PANIC', target: 'blackout' }),
    onPanicClearText: () => dispatch({ type: 'TOGGLE_PANIC', target: 'clearText' }),
    onPanicFreeze: () => dispatch({ type: 'TOGGLE_PANIC', target: 'freeze' }),
  })

  return {
    query,
    setQuery,
    songs,
    isLoadingSongs,
    currentSong,
    isLoadingSongDetail,
    selectedSongId,
    activeBible,
    bibleLookupResult,
    isSpotlightOpen,
    setIsSpotlightOpen,
    handleSelectSong,
    handleSelectSection,
    handleSelectBible,
    handleSelectVerse,
    handleSelectLiturgyItem,
  }
}
