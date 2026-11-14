/**
 * @module @slices/playback/ui/components/PlaybackSyncModal
 * @description Modal de Reprodução e Edição de Playbacks Sincronizados com Web Audio API (RF-22).
 * Baseado em `PlayBack.java` & `EditPlayBackDialog.java` do Holyrics original.
 */

import { useState, useEffect, useRef } from 'react'
import { X, Disc3, Play, Square, Flag, Save } from 'lucide-react'
import type { ActionCommand } from '@lumen/core'
import type { Song } from '@slices/songs/contracts/song-contracts'
import { Button } from '@ui/components/ui/button'
import { Input } from '@ui/components/ui/input'
import { Badge } from '@ui/components/ui/badge'
import {
  PLAYBACK_DEFAULT_SLIDE_DURATION_MS,
  resolvePlaybackSlideIndex,
  type PlaybackTrack,
} from '../../contracts/playback-contracts'
import { playbackService } from '../services/playback-service'

export interface PlaybackSyncModalProps {
  isOpen: boolean
  song: Song | null
  onClose: () => void
  dispatch: (command: ActionCommand) => void
}

/**
 * Modal de reprodução sincronizada e calibração de tempos por estrofe (RF-22).
 *
 * @param props - Propriedades do modal.
 * @param props.isOpen - Indica se o modal está aberto.
 * @param props.song - Música selecionada no console do operador.
 * @param props.onClose - Callback de fechamento.
 * @param props.dispatch - Despachante de comandos da FSM.
 * @returns Elemento JSX do modal ou null.
 */
export function PlaybackSyncModal({
  isOpen,
  song,
  onClose,
  dispatch,
}: PlaybackSyncModalProps) {
  const [durationsMs, setDurationsMs] = useState<number[]>([])
  const [isPlaying, setIsPlaying] = useState(false)
  const [elapsedMs, setElapsedMs] = useState(0)
  const [savedNotice, setSavedNotice] = useState(false)
  const startTimeRef = useRef<number>(0)
  const lastDispatchedSlideRef = useRef<number>(-1)

  useEffect(() => {
    if (!isOpen || !song) return
    const defaultDurations = song.sections.map(() => PLAYBACK_DEFAULT_SLIDE_DURATION_MS)
    setDurationsMs(defaultDurations)
    setElapsedMs(0)
    setIsPlaying(false)

    playbackService
      .listAll()
      .then((tracks) => {
        const existing = tracks.find((track) => track.songId === song.id)
        if (existing && existing.slideDurationsMs.length === song.sections.length) {
          setDurationsMs(existing.slideDurationsMs)
        }
      })
      .catch(() => {
        // Mantém tempos padrão
      })
  }, [isOpen, song])

  useEffect(() => {
    if (!isPlaying || !song) return

    const timer = setInterval(() => {
      const currentElapsed = Date.now() - startTimeRef.current
      setElapsedMs(currentElapsed)

      const virtualTrack: PlaybackTrack = {
        id: `pb_${song.id}`,
        songId: song.id,
        title: song.title,
        artist: song.artist,
        audioUrl: '',
        startWithTitle: false,
        startWithDurationMs: 0,
        slideDurationsMs: durationsMs,
        updatedAt: Date.now(),
      }

      const targetIndex = resolvePlaybackSlideIndex(virtualTrack, currentElapsed)
      if (targetIndex !== lastDispatchedSlideRef.current) {
        lastDispatchedSlideRef.current = targetIndex
        dispatch({ type: 'NAV_GOTO', slideIndex: targetIndex })
      }
    }, 80)

    return () => clearInterval(timer)
  }, [isPlaying, song, durationsMs, dispatch])

  if (!isOpen || !song) return null

  const handleStartSyncPlayback = () => {
    const slides = song.sections.map((section) => ({
      content: section.lines,
    }))
    dispatch({
      type: 'PRESENT_ITEM',
      itemId: song.id,
      mode: 'SONG',
      foreground: {
        title: song.title,
        content: slides[0]?.content || [],
        slides,
        slideIndex: 0,
        totalSlides: slides.length,
      },
    })
    lastDispatchedSlideRef.current = 0
    startTimeRef.current = Date.now()
    setElapsedMs(0)
    setIsPlaying(true)
  }

  const handleStopPlayback = () => {
    setIsPlaying(false)
  }

  const handleMarkNextSlideNow = () => {
    if (!isPlaying) return
    const currentSlide = Math.max(0, lastDispatchedSlideRef.current)
    if (currentSlide >= durationsMs.length - 1) return

    const cumulativeBefore = durationsMs
      .slice(0, currentSlide)
      .reduce((sum, val) => sum + val, 0)
    const newSlideDuration = Math.max(500, elapsedMs - cumulativeBefore)

    const updated = [...durationsMs]
    updated[currentSlide] = newSlideDuration
    setDurationsMs(updated)
  }

  const handleSaveTimings = async () => {
    await playbackService.saveTrack({
      id: `pb_${song.id}`,
      songId: song.id,
      title: song.title,
      artist: song.artist,
      audioUrl: `/api/media/stream/${song.id}`,
      startWithTitle: true,
      startWithDurationMs: 2000,
      slideDurationsMs: durationsMs,
      updatedAt: Date.now(),
    })
    setSavedNotice(true)
    setTimeout(() => setSavedNotice(false), 2000)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-xs">
      <div className="flex max-h-[85vh] w-full max-w-2xl flex-col rounded-xl border border-white/10 bg-slate-900 text-slate-100 shadow-2xl overflow-hidden">
        <div className="flex items-center justify-between border-b border-white/10 bg-slate-800/60 px-5 py-3.5">
          <div className="flex items-center gap-2">
            <Disc3 className="h-4 w-4 text-sky-400" />
            <h2 className="text-sm font-semibold text-white">
              Playback Sincronizado: {song.title} (RF-22)
            </h2>
          </div>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        {/* Barra de Controle e Clock de Sincronia */}
        <div className="flex items-center justify-between border-b border-white/10 bg-slate-950/60 px-5 py-3">
          <div className="flex items-center gap-2">
            {!isPlaying ? (
              <Button onClick={handleStartSyncPlayback}>
                <Play className="h-3.5 w-3.5" />
                <span>Iniciar Playback Sincronizado</span>
              </Button>
            ) : (
              <>
                <Button variant="panic" onClick={handleStopPlayback}>
                  <Square className="h-3.5 w-3.5" />
                  <span>Parar</span>
                </Button>
                <Button variant="secondary" onClick={handleMarkNextSlideNow}>
                  <Flag className="h-3.5 w-3.5 text-amber-400" />
                  <span>Marcar Troca de Estrofe Agora</span>
                </Button>
              </>
            )}
          </div>

          <div className="flex items-center gap-3">
            <Badge variant={isPlaying ? 'live' : 'default'}>
              {(elapsedMs / 1000).toFixed(1)}s
            </Badge>
            <Button size="sm" variant="outline" onClick={handleSaveTimings}>
              <Save className="h-3.5 w-3.5 text-sky-400" />
              <span>{savedNotice ? 'Salvo!' : 'Salvar Tempos'}</span>
            </Button>
          </div>
        </div>

        {/* Lista de Estrofes e Durações em Segundos */}
        <div className="flex-1 overflow-y-auto p-4">
          <div className="flex flex-col gap-2">
            {song.sections.map((section, idx) => (
              <div
                key={section.id}
                className="flex items-center justify-between rounded-lg border border-white/10 bg-slate-800/40 px-4 py-2.5 text-xs"
              >
                <div className="flex-1 pr-4">
                  <span className="font-mono text-[10px] font-bold text-sky-400">
                    Estrofe {section.index}
                  </span>
                  <p className="truncate text-slate-200">{section.lines.join(' / ')}</p>
                </div>

                <div className="flex items-center gap-2">
                  <span className="text-slate-400">Duração (s):</span>
                  <Input
                    type="number"
                    className="w-20 text-right font-mono"
                    value={String(Math.round((durationsMs[idx] || 15000) / 1000))}
                    onChange={(event) => {
                      const seconds = Math.max(1, Number(event.target.value) || 10)
                      const copy = [...durationsMs]
                      copy[idx] = seconds * 1000
                      setDurationsMs(copy)
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
