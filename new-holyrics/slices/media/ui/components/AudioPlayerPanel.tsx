/**
 * @module @slices/media/ui/components/AudioPlayerPanel
 * @description Painel compacto de áudio ambiente e equalizador litúrgico no padrão Slate Pro Studio.
 */

import { useState, useRef } from 'react'
import {
  Play,
  Pause,
  SkipForward,
  SkipBack,
  Volume2,
  VolumeX,
  Sliders,
  Music,
  Repeat,
  Shuffle,
  RotateCcw,
  Plus,
  AudioLines,
} from 'lucide-react'
import { useAudioEngine } from '../hooks/useAudioEngine'
import { AudioSpectrumVisualizer } from './AudioSpectrumVisualizer'
import type { AudioPlaylist, MediaItem } from '../../contracts/media-contracts'

export interface AudioPlayerPanelProps {
  initialPlaylists?: AudioPlaylist[]
  className?: string
}

/**
 * Formata segundos numéricos em string mm:ss.
 *
 * @param seconds - Segundos a formatar.
 * @returns Tempo formatado em mm:ss.
 */
function formatAudioTime(seconds: number): string {
  if (isNaN(seconds) || seconds < 0) return '00:00'
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = Math.floor(seconds % 60)
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

/**
 * Painel compacto de reprodução de áudio ambiente com capa minimalista e upload de faixas locais.
 *
 * @param props - Propriedades do painel.
 * @param props.initialPlaylists - Playlists iniciais para carregar no reprodutor.
 * @param props.className - Classes Tailwind adicionais.
 * @returns Elemento JSX do painel.
 */
export function AudioPlayerPanel({ initialPlaylists, className = '' }: AudioPlayerPanelProps) {
  const {
    playlists,
    currentPlaylist,
    currentTrack,
    isPlaying,
    currentTime,
    duration,
    volume,
    equalizer,
    repeatMode,
    shuffle,
    togglePlay,
    nextTrack,
    prevTrack,
    seekTo,
    setVolumeLevel,
    updateEqualizer,
    resetEqualizer,
    selectPlaylist,
    selectTrack,
    setRepeat,
    toggleShuffleMode,
    getFrequencyData,
  } = useAudioEngine(initialPlaylists)

  const [isEqExpanded, setIsEqExpanded] = useState(false)
  const [previousVolume, setPreviousVolume] = useState(0.8)
  const [isUploadingAudio, setIsUploadingAudio] = useState(false)
  const audioInputRef = useRef<HTMLInputElement | null>(null)

  const toggleMute = () => {
    if (volume > 0) {
      setPreviousVolume(volume)
      setVolumeLevel(0)
    } else {
      setVolumeLevel(previousVolume || 0.8)
    }
  }

  const cycleRepeat = () => {
    if (repeatMode === 'all') setRepeat('one')
    else if (repeatMode === 'one') setRepeat('off')
    else setRepeat('all')
  }

  const handleUploadAudioFile = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return
    setIsUploadingAudio(true)
    try {
      const formData = new FormData()
      formData.append('file', file)
      const uploadRes = await fetch('/api/media/upload', {
        method: 'POST',
        body: formData,
      })
      const uploadJson = (await uploadRes.json()) as { ok: boolean; data?: MediaItem }
      if (uploadJson.ok && uploadJson.data) {
        const uploadedMedia = uploadJson.data
        const targetPlaylist: AudioPlaylist = currentPlaylist ?? {
          id: 'playlist-ambiente',
          name: 'Ambiente & Prelúdio',
          repeatMode: 'all',
          shuffle: false,
          tracks: [],
        }

        const newTrack = {
          id: uploadedMedia.id,
          title: uploadedMedia.name,
          artist: 'Áudio Local',
          durationSeconds: 180,
          url: `/api/media/stream/${encodeURIComponent(uploadedMedia.id)}`,
          tags: ['local'],
        }

        const updatedPlaylist: AudioPlaylist = {
          ...targetPlaylist,
          tracks: [...targetPlaylist.tracks, newTrack],
        }

        await fetch('/api/media/audio/playlists', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(updatedPlaylist),
        })
        window.location.reload()
      }
    } finally {
      setIsUploadingAudio(false)
      if (audioInputRef.current) audioInputRef.current.value = ''
    }
  }

  return (
    <div className={`flex flex-col rounded-md bg-[#131C2B] text-slate-200 select-none overflow-hidden ${className}`}>
      {/* Cabeçalho Compacto do Player */}
      <div className="flex items-center justify-between border-b border-white/5 bg-[#090D14]/50 px-2.5 py-1.5">
        <div className="flex items-center gap-1.5">
          <Music className="h-3.5 w-3.5 text-sky-400" />
          <span className="text-[11px] font-semibold text-slate-200">Player Ambiente</span>
        </div>

        <div className="flex items-center gap-1">
          {playlists.length > 0 && (
            <select
              value={currentPlaylist?.id || ''}
              onChange={(event) => selectPlaylist(event.target.value)}
              className="rounded bg-[#192334] px-2 py-0.5 text-[10px] text-slate-300 focus:outline-none max-w-[120px] truncate"
            >
              {playlists.map((playlist) => (
                <option key={playlist.id} value={playlist.id}>
                  {playlist.name}
                </option>
              ))}
            </select>
          )}

          <input
            ref={audioInputRef}
            type="file"
            accept="audio/*"
            className="hidden"
            onChange={handleUploadAudioFile}
          />
          <button
            type="button"
            disabled={isUploadingAudio}
            onClick={() => audioInputRef.current?.click()}
            title="Adicionar arquivo de áudio local (MP3, WAV, OGG)"
            className="flex items-center gap-1 rounded bg-[#192334] hover:bg-[#1E2B40] px-1.5 py-0.5 text-[10px] text-sky-400 transition"
          >
            <Plus className="h-3 w-3" />
            <span>Áudio</span>
          </button>

          <button
            type="button"
            onClick={() => setIsEqExpanded((previous) => !previous)}
            className={`rounded p-1 text-xs transition ${
              isEqExpanded ? 'bg-sky-500/20 text-sky-400' : 'text-slate-400 hover:bg-[#192334] hover:text-slate-200'
            }`}
            title="Equalizador de 3 Bandas (Graves, Médios e Agudos)"
          >
            <Sliders className="h-3 w-3" />
          </button>
        </div>
      </div>

      {/* Área Central Compacta: Capa Minimalista + Faixa Atual & Espectro */}
      <div className="p-2.5">
        <div className="flex items-center gap-2.5 mb-2">
          {/* Arte minimalista de onda sonora preenchendo o quadrado da faixa */}
          <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-md bg-gradient-to-br from-sky-500/20 to-indigo-500/20 border border-sky-500/20 text-sky-400">
            <AudioLines className={`h-4 w-4 ${isPlaying ? 'animate-pulse text-emerald-400' : 'text-sky-400'}`} />
          </div>

          <div className="min-w-0 flex-1">
            <div className="truncate text-xs font-medium text-slate-100">
              {currentTrack?.title || 'Nenhuma faixa selecionada'}
            </div>
            <div className="truncate text-[10px] text-slate-400">
              {currentTrack?.artist || 'Clique em + Áudio para importar trilhas locais'}
            </div>
          </div>

          <AudioSpectrumVisualizer
            isPlaying={isPlaying}
            getFrequencyData={getFrequencyData}
            barCount={14}
            height={24}
          />
        </div>

        {/* Barra de Progresso / Seek */}
        <div className="space-y-0.5">
          <input
            type="range"
            min={0}
            max={duration || 100}
            step={1}
            value={currentTime}
            onChange={(event) => seekTo(parseFloat(event.target.value))}
            className="h-1 w-full cursor-pointer appearance-none rounded-lg bg-[#090D14] accent-sky-400"
          />
          <div className="flex justify-between text-[9px] font-mono text-slate-400">
            <span>{formatAudioTime(currentTime)}</span>
            <span>{formatAudioTime(duration)}</span>
          </div>
        </div>

        {/* Controles Principais Compactos */}
        <div className="mt-2 flex items-center justify-between">
          <div className="flex items-center gap-0.5">
            <button
              type="button"
              onClick={cycleRepeat}
              className={`rounded p-1 text-[11px] transition ${
                repeatMode !== 'off' ? 'text-sky-400' : 'text-slate-500 hover:text-slate-300'
              }`}
              title={`Modo de repetição: ${repeatMode}`}
            >
              <Repeat className="h-3 w-3" />
            </button>
            <button
              type="button"
              onClick={toggleShuffleMode}
              className={`rounded p-1 text-[11px] transition ${
                shuffle ? 'text-sky-400' : 'text-slate-500 hover:text-slate-300'
              }`}
              title={`Ordem aleatória: ${shuffle ? 'Ativa' : 'Desativada'}`}
            >
              <Shuffle className="h-3 w-3" />
            </button>
          </div>

          <div className="flex items-center gap-1.5">
            <button
              type="button"
              onClick={prevTrack}
              className="rounded p-1 text-slate-400 hover:bg-[#192334] hover:text-slate-100 transition"
              title="Faixa anterior"
            >
              <SkipBack className="h-3.5 w-3.5" />
            </button>

            <button
              type="button"
              onClick={togglePlay}
              disabled={!currentTrack}
              className="flex h-7 w-7 items-center justify-center rounded-full bg-sky-500 text-slate-950 shadow hover:bg-sky-400 disabled:opacity-40 transition"
              title={isPlaying ? 'Pausar áudio ambiente' : 'Reproduzir áudio ambiente'}
            >
              {isPlaying ? <Pause className="h-3.5 w-3.5 fill-current" /> : <Play className="h-3.5 w-3.5 fill-current ml-0.5" />}
            </button>

            <button
              type="button"
              onClick={nextTrack}
              className="rounded p-1 text-slate-400 hover:bg-[#192334] hover:text-slate-100 transition"
              title="Próxima faixa"
            >
              <SkipForward className="h-3.5 w-3.5" />
            </button>
          </div>

          {/* Volume */}
          <div className="flex items-center gap-1">
            <button
              type="button"
              onClick={toggleMute}
              className="text-slate-400 hover:text-slate-200 transition"
              title={volume === 0 ? 'Restaurar som' : 'Silenciar áudio'}
            >
              {volume === 0 ? <VolumeX className="h-3.5 w-3.5" /> : <Volume2 className="h-3.5 w-3.5" />}
            </button>
            <input
              type="range"
              min={0}
              max={1}
              step={0.01}
              value={volume}
              onChange={(event) => setVolumeLevel(parseFloat(event.target.value))}
              className="h-1 w-14 cursor-pointer appearance-none rounded-lg bg-[#090D14] accent-sky-400"
              title={`Volume: ${Math.round(volume * 100)}%`}
            />
          </div>
        </div>
      </div>

      {/* Equalizador de 3 Bandas (Colapsável) */}
      {isEqExpanded && (
        <div className="border-t border-white/5 bg-[#090D14]/60 px-2.5 py-2">
          <div className="mb-1.5 flex items-center justify-between text-[10px] font-semibold uppercase tracking-wider text-slate-400">
            <span>Equalizador (3 Bandas)</span>
            <button
              type="button"
              onClick={resetEqualizer}
              className="flex items-center gap-1 text-sky-400 hover:text-sky-300"
              title="Restaurar equalizador (0 dB)"
            >
              <RotateCcw className="h-2.5 w-2.5" />
              <span>Reset</span>
            </button>
          </div>

          <div className="grid grid-cols-3 gap-2 text-center text-[10px]">
            {(['low', 'mid', 'high'] as const).map((band) => {
              const labels = { low: 'Graves', mid: 'Médios', high: 'Agudos' }
              const bandValue = equalizer[band]
              return (
                <div key={band} className="flex flex-col items-center rounded bg-[#192334] p-1.5">
                  <span className="text-slate-400">{labels[band]}</span>
                  <input
                    type="range"
                    min={-12}
                    max={12}
                    step={0.5}
                    value={bandValue}
                    onChange={(event) => updateEqualizer({ [band]: parseFloat(event.target.value) })}
                    className="my-1 h-1 w-full cursor-pointer appearance-none rounded bg-[#090D14] accent-sky-400"
                  />
                  <span className="font-mono text-[9px] text-sky-400">
                    {bandValue > 0 ? `+${bandValue}` : bandValue} dB
                  </span>
                </div>
              )
            })}
          </div>
        </div>
      )}

      {/* Lista de Faixas da Playlist (se houver faixas reais) */}
      {currentPlaylist && currentPlaylist.tracks.length > 0 && (
        <div className="border-t border-white/5 max-h-24 overflow-y-auto divide-y divide-white/5">
          {currentPlaylist.tracks.map((track) => {
            const isSelected = currentTrack?.id === track.id
            return (
              <button
                key={track.id}
                type="button"
                onClick={() => selectTrack(track)}
                className={`flex w-full items-center justify-between px-2.5 py-1.5 text-left text-[11px] transition ${
                  isSelected ? 'bg-sky-500/15 text-sky-300 font-medium' : 'text-slate-400 hover:bg-[#192334]'
                }`}
              >
                <span className="truncate pr-2">{track.title}</span>
                <span className="font-mono text-[10px] text-slate-500">
                  {formatAudioTime(track.durationSeconds)}
                </span>
              </button>
            )
          })}
        </div>
      )}
    </div>
  )
}
