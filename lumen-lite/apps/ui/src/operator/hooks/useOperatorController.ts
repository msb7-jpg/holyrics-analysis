/**
 * @module @ui/operator/hooks/useOperatorController
 * @description Headless Controller principal da tela do operador (OperatorView).
 *
 * ## Responsabilidades:
 * - Coordenar a seleção de músicas e capítulos bíblicos (incluindo Dual Version - RF-14).
 * - Despachar apresentações de slides (PRESENT_ITEM) para a FSM com array `slides` indexado.
 * - Conectar atalhos globais de teclado (useOperatorShortcuts).
 * - Sincronizar estado da busca e primeiro item do acervo automaticamente.
 */

import { useState, useEffect } from 'react'
import { useDebounceValue } from '@siberiacancode/reactuse'
import type { ActionCommand, ScreenState, SlideItem } from '@lumen/core'
import { useSongSearchQuery, useSongDetailQuery } from '@slices/songs/ui/queries/songs-queries'
import { useBibleLookupQuery } from '@slices/bible/ui/queries/bible-queries'
import { bibleService } from '@slices/bible/ui/services/bible-service'
import { useOperatorShortcuts } from '@slices/keymap/ui/hooks/useOperatorShortcuts'
import type { BibleLookupResponse } from '@slices/bible/contracts/bible-contracts'
import type { LiturgyItem } from '@slices/liturgy/contracts/liturgy-schema'

export interface UseOperatorControllerOptions {
  state: ScreenState
  dispatch: (cmd: ActionCommand) => void
}

/**
 * Monta o vetor indexado `SlideItem[]` a partir de uma consulta bíblica (suportando Dual Version - RF-14).
 *
 * @param lookup - Resposta da consulta bíblica.
 * @returns Lista de slides prontos para o `ForegroundLayer`.
 */
function buildBibleSlides(lookup: BibleLookupResponse): SlideItem[] {
  return lookup.verses.map((verseItem, idx) => {
    const secondaryVerse = lookup.secondaryVerses?.[idx]
    return {
      content: [verseItem.text],
      secondaryContent: secondaryVerse ? [secondaryVerse.text] : undefined,
      reference: `${lookup.reference} (v.${verseItem.verse}) [${lookup.version}]`,
      secondaryReference:
        secondaryVerse && lookup.dualVersion
          ? `${lookup.reference} (v.${secondaryVerse.verse}) [${lookup.dualVersion}]`
          : undefined,
    }
  })
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
  const [dualBibleVersion, setDualBibleVersion] = useState<string | null>(null)
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

  const handleSelectSong = (id: string) => {
    setActiveBible(null)
    setSelectedSongId(id)
  }

  // Quando os dados da música chegarem, se for uma nova seleção, despacha o slide inicial com `slides` populado
  useEffect(() => {
    if (currentSong && state.mode !== 'SONG') {
      if (currentSong.sections.length > 0) {
        const songSlides: SlideItem[] = currentSong.sections.map((section) => ({
          content: section.lines,
        }))
        dispatch({
          type: 'PRESENT_ITEM',
          itemId: currentSong.id,
          mode: 'SONG',
          foreground: {
            title: currentSong.title,
            content: songSlides[0].content,
            slides: songSlides,
            slideIndex: 0,
            totalSlides: songSlides.length,
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

    const songSlides: SlideItem[] = currentSong.sections.map((sec) => ({
      content: sec.lines,
    }))

    dispatch({
      type: 'PRESENT_ITEM',
      itemId: currentSong.id,
      mode: 'SONG',
      foreground: {
        title: currentSong.title,
        content: section.lines,
        slides: songSlides,
        slideIndex: index,
        totalSlides: songSlides.length,
      },
    })
  }

  // Seleciona e projeta passagem bíblica
  const handleSelectBible = (lookup: BibleLookupResponse) => {
    setSelectedSongId(null)
    setActiveBible(lookup)

    if (lookup.verses.length > 0) {
      const bibleSlides = buildBibleSlides(lookup)
      dispatch({
        type: 'PRESENT_ITEM',
        itemId: `bible_${lookup.reference}`,
        mode: 'BIBLE',
        foreground: {
          title: lookup.reference,
          reference: bibleSlides[0].reference,
          secondaryReference: bibleSlides[0].secondaryReference,
          content: bibleSlides[0].content,
          secondaryContent: bibleSlides[0].secondaryContent,
          slides: bibleSlides,
          slideIndex: 0,
          totalSlides: bibleSlides.length,
        },
      })
    }
  }

  // Alterna 2ª Tradução em Tela Dividida (Dual Version - RF-14)
  const handleChangeDualVersion = async (nextDualVersion: string | null) => {
    setDualBibleVersion(nextDualVersion)
    if (!activeBible) return

    const updatedLookup = await bibleService.lookup(
      activeBible.reference,
      activeBible.version,
      undefined,
      nextDualVersion || undefined,
    )
    if (updatedLookup) {
      handleSelectBible(updatedLookup)
    }
  }

  // Despacha versículo específico
  const handleSelectVerse = (index: number) => {
    if (!activeBible || !activeBible.verses[index]) return
    const bibleSlides = buildBibleSlides(activeBible)
    const targetSlide = bibleSlides[index]

    dispatch({
      type: 'PRESENT_ITEM',
      itemId: `bible_${activeBible.reference}`,
      mode: 'BIBLE',
      foreground: {
        title: activeBible.reference,
        reference: targetSlide.reference,
        secondaryReference: targetSlide.secondaryReference,
        content: targetSlide.content,
        secondaryContent: targetSlide.secondaryContent,
        slides: bibleSlides,
        slideIndex: index,
        totalSlides: bibleSlides.length,
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
        bibleService
          .lookup(item.reference, item.version || 'ACF', undefined, item.dualVersion)
          .then((lookup) => {
            if (lookup) {
              setDualBibleVersion(item.dualVersion || null)
              handleSelectBible(lookup)
            }
          })
          .catch(() => {
            // Fallback local imediato
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
    dualBibleVersion,
    bibleLookupResult,
    isSpotlightOpen,
    setIsSpotlightOpen,
    handleSelectSong,
    handleSelectSection,
    handleSelectBible,
    handleChangeDualVersion,
    handleSelectVerse,
    handleSelectLiturgyItem,
  }
}
