/**
 * @module @ui/operator/hooks/useOperatorController
 * @description Headless Controller principal da tela do operador (OperatorView).
 *
 * ## Responsabilidades:
 * - Coordenar a seleção de músicas e capítulos bíblicos (incluindo Dual Version - RF-14).
 * - Manter estado de Workspace (Preview Central) desacoplado do Live (Monitor do Telão).
 * - Despachar apresentações de slides (PRESENT_ITEM) para a FSM com array `slides` indexado.
 * - Conectar atalhos globais de teclado (useOperatorShortcuts) e atalho Enter de projeção rápida.
 * - Sincronizar estado da busca e primeiro item do acervo automaticamente.
 */

import { useState, useEffect, useCallback } from 'react'
import { useDebounceValue } from '@siberiacancode/reactuse'
import type { ActionCommand, ScreenState, SlideItem } from '@lumen/core'
import { useSongSearchQuery, useSongDetailQuery } from '@slices/songs/ui/queries/songs-queries'
import { useBibleLookupQuery } from '@slices/bible/ui/queries/bible-queries'
import { bibleService } from '@slices/bible/ui/services/bible-service'
import { useOperatorShortcuts } from '@slices/keymap/ui/hooks/useOperatorShortcuts'
import type { BibleLookupResponse } from '@slices/bible/contracts/bible-contracts'
import type { LiturgyItem } from '@slices/liturgy/contracts/liturgy-schema'
import type { Song } from '@slices/songs/contracts/song-contracts'
import type { PptxPresentation } from '@slices/pptx/contracts/pptx-contracts'

export type WorkspaceItem =
  | { type: 'SONG'; songId: string }
  | { type: 'PPTX'; deckId?: string; slides: SlideItem[]; title?: string }
  | { type: 'BIBLE'; lookup: BibleLookupResponse }

export interface MediaProjectInput {
  id: string
  title?: string
  name?: string
  mediaSrc?: string
  path?: string
  type?: string
  mediaType?: string
}

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
  return lookup.verses.map((verseItem, verseIndex) => {
    const secondaryVerse = lookup.secondaryVerses?.[verseIndex]
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
 * Hook orquestrador do console do operador com Workspace e Live desacoplados.
 *
 * @param options - Opções de configuração do controlador.
 * @param options.state - Estado atômico da projeção e pânico.
 * @param options.dispatch - Despachante tipado de comandos para a FSM.
 * @returns Estados e manipuladores de seleção, projeção e navegação.
 */
export function useOperatorController({ state: _state, dispatch }: UseOperatorControllerOptions) {
  const [query, setQuery] = useState('')
  const debouncedQuery = useDebounceValue(query, 120)

  const [selectedSongId, setSelectedSongId] = useState<string | null>(null)
  const [activeBible, setActiveBible] = useState<BibleLookupResponse | null>(null)
  const [dualBibleVersion, setDualBibleVersion] = useState<string | null>(null)
  const [isSpotlightOpen, setIsSpotlightOpen] = useState(false)

  // Estado desacoplado do Workspace (Preview Central)
  const [workspaceItem, setWorkspaceItem] = useState<WorkspaceItem | null>(null)
  const [workspaceFocusedIndex, setWorkspaceFocusedIndex] = useState(0)

  // 1. Busca textual de canções na Coluna 2
  const { data: songs = [], isLoading: isLoadingSongs } = useSongSearchQuery(debouncedQuery)

  // 2. Detalhes completos da canção selecionada no Workspace
  const activeSongId = workspaceItem?.type === 'SONG' ? workspaceItem.songId : selectedSongId
  const { data: currentSong, isLoading: isLoadingSongDetail } = useSongDetailQuery(activeSongId)

  // 3. Reconhecimento de referência bíblica em tempo real na busca da Coluna 2
  const { data: bibleLookupResult } = useBibleLookupQuery(debouncedQuery, 'ACF')

  // Auto-seleciona a primeira canção caso nada esteja selecionado no workspace
  useEffect(() => {
    if (!workspaceItem && !selectedSongId && !activeBible && songs.length > 0) {
      const firstSongId = songs[0].id
      setSelectedSongId(firstSongId)
      setWorkspaceItem({ type: 'SONG', songId: firstSongId })
      setWorkspaceFocusedIndex(0)
    }
  }, [songs, selectedSongId, activeBible, workspaceItem])

  // Apenas carrega a música no workspace, NÃO dispara nada para o telão
  const handleSelectSong = useCallback((songId: string) => {
    setActiveBible(null)
    setSelectedSongId(songId)
    setWorkspaceItem({ type: 'SONG', songId })
    setWorkspaceFocusedIndex(0)
  }, [])

  // Apenas carrega a passagem no workspace, NÃO dispara nada para o telão
  const handleSelectBible = useCallback((lookup: BibleLookupResponse) => {
    setSelectedSongId(null)
    setActiveBible(lookup)
    setWorkspaceItem({ type: 'BIBLE', lookup })
    setWorkspaceFocusedIndex(0)
  }, [])

  // Carrega o deck PPTX no workspace para inspeção
  const handleInspectPptxDeck = useCallback(
    (deck: PptxPresentation | { id?: string; title?: string; slides: SlideItem[] }) => {
      setSelectedSongId(null)
      setActiveBible(null)

      let convertedSlides: SlideItem[]
      if (deck.slides.length > 0 && 'textLines' in deck.slides[0]) {
        const pptxSlides = deck.slides as unknown as Array<{
          textLines?: string[]
          speakerNotes?: string
          imageUrl?: string
        }>
        convertedSlides = pptxSlides.map((slide, slideIdx) => ({
          content: slide.textLines || [],
          speakerNotes: slide.speakerNotes,
          imageUrl: slide.imageUrl,
          reference: `${deck.title || 'Apresentação'} (${slideIdx + 1}/${deck.slides.length})`,
        }))
      } else {
        convertedSlides = deck.slides as SlideItem[]
      }

      setWorkspaceItem({
        type: 'PPTX',
        deckId: deck.id,
        slides: convertedSlides,
        title: deck.title,
      })
      setWorkspaceFocusedIndex(0)
    },
    []
  )

  // Dispara estrofe de louvor especificamente para o telão (Live)
  const handleProjectSongStanza = useCallback(
    (song: Song, stanzaIndex: number) => {
      const section = song.sections[stanzaIndex]
      if (!section) return

      const songSlides: SlideItem[] = song.sections.map((sec) => ({
        content: sec.lines,
      }))

      dispatch({
        type: 'PRESENT_ITEM',
        itemId: song.id,
        mode: 'SONG',
        background: song.defaultBackground,
        foreground: {
          title: song.title,
          content: section.lines,
          slides: songSlides,
          slideIndex: stanzaIndex,
          totalSlides: songSlides.length,
        },
      })
    },
    [dispatch]
  )

  // Dispara versículo bíblico especificamente para o telão (Live)
  const handleProjectBibleVerse = useCallback(
    (lookup: BibleLookupResponse, verseIndex: number) => {
      if (!lookup.verses[verseIndex]) return
      const bibleSlides = buildBibleSlides(lookup)
      const targetSlide = bibleSlides[verseIndex]

      dispatch({
        type: 'PRESENT_ITEM',
        itemId: `bible_${lookup.reference}`,
        mode: 'BIBLE',
        foreground: {
          title: lookup.reference,
          reference: targetSlide.reference,
          secondaryReference: targetSlide.secondaryReference,
          content: targetSlide.content,
          secondaryContent: targetSlide.secondaryContent,
          slides: bibleSlides,
          slideIndex: verseIndex,
          totalSlides: bibleSlides.length,
        },
      })
    },
    [dispatch]
  )

  // Dispara mídia (imagem ou vídeo) em tela cheia para o telão (Live)
  const handleProjectMediaFullscreen = useCallback(
    (media: MediaProjectInput) => {
      const isVideo =
        ('mediaType' in media && media.mediaType === 'VIDEO') || media.type === 'VIDEO'
      const streamUrl =
        'mediaSrc' in media && media.mediaSrc
          ? media.mediaSrc
          : `/api/media/stream/${encodeURIComponent(media.id)}`
      const title =
        ('name' in media && media.name) || ('title' in media && media.title) || 'Mídia'

      dispatch({
        type: 'PRESENT_ITEM',
        itemId: media.id,
        mode: 'MEDIA',
        background: {
          type: isVideo ? 'video' : 'image',
          source: streamUrl,
          loop: isVideo,
          muted: true,
        },
        foreground: {
          title,
          content: [],
          slides: [],
          slideIndex: 0,
          totalSlides: 0,
        },
      })
    },
    [dispatch]
  )

  // Dispara para o telão o item e slide atualmente focado no workspace
  const handleProjectWorkspaceCurrent = useCallback(() => {
    if (!workspaceItem) return

    if (workspaceItem.type === 'SONG') {
      if (!currentSong) return
      handleProjectSongStanza(currentSong, workspaceFocusedIndex)
    } else if (workspaceItem.type === 'BIBLE') {
      handleProjectBibleVerse(workspaceItem.lookup, workspaceFocusedIndex)
    } else if (workspaceItem.type === 'PPTX') {
      if (workspaceItem.slides.length === 0) return
      const targetIndex = Math.max(
        0,
        Math.min(workspaceFocusedIndex, workspaceItem.slides.length - 1)
      )
      const targetSlide = workspaceItem.slides[targetIndex]
      dispatch({
        type: 'PRESENT_ITEM',
        itemId: workspaceItem.deckId || 'pptx_deck',
        mode: 'PPTX',
        foreground: {
          title: workspaceItem.title || 'Apresentação',
          reference: targetSlide?.reference,
          content: targetSlide?.content || [],
          speakerNotes: targetSlide?.speakerNotes,
          imageUrl: targetSlide?.imageUrl,
          slides: workspaceItem.slides,
          slideIndex: targetIndex,
          totalSlides: workspaceItem.slides.length,
        },
      })
    }
  }, [
    workspaceItem,
    currentSong,
    workspaceFocusedIndex,
    handleProjectSongStanza,
    handleProjectBibleVerse,
    dispatch,
  ])

  // Alterna 2ª Tradução em Tela Dividida (Dual Version - RF-14)
  const handleChangeDualVersion = async (nextDualVersion: string | null) => {
    setDualBibleVersion(nextDualVersion)
    const currentBible = workspaceItem?.type === 'BIBLE' ? workspaceItem.lookup : activeBible
    if (!currentBible) return

    const updatedLookup = await bibleService.lookup(
      currentBible.reference,
      currentBible.version,
      undefined,
      nextDualVersion || undefined,
    )
    if (updatedLookup) {
      handleSelectBible(updatedLookup)
    }
  }

  // 1 clique: seleciona/foca a estrofe no workspace
  const handleSelectSection = useCallback((index: number) => {
    setWorkspaceFocusedIndex(index)
  }, [])

  // 1 clique: seleciona/foca o versículo no workspace
  const handleSelectVerse = useCallback((index: number) => {
    setWorkspaceFocusedIndex(index)
  }, [])

  // Manipula seleção de item da coluna litúrgica (1 clique = carregar no workspace)
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
            // Fallback local silencioso
          })
        break
      }
      case 'PPTX': {
        break
      }
      case 'TEXT': {
        break
      }
      case 'COUNTDOWN': {
        break
      }
      case 'MEDIA': {
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
    workspaceItem,
    setWorkspaceItem,
    workspaceFocusedIndex,
    setWorkspaceFocusedIndex,
    handleSelectSong,
    handleSelectSection,
    handleSelectBible,
    handleChangeDualVersion,
    handleSelectVerse,
    handleInspectPptxDeck,
    handleProjectSongStanza,
    handleProjectBibleVerse,
    handleProjectMediaFullscreen,
    handleProjectWorkspaceCurrent,
    handleSelectLiturgyItem,
  }
}
