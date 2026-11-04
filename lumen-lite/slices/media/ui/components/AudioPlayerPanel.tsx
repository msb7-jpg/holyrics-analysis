/**
 * @module @slices/media/ui/components/AudioPlayerPanel
 * @description Painel ergonômico de áudio ambiente e equalizador litúrgico no padrão Slate Pro Studio (RF-28).
 *
 * ## Responsabilidades:
 * - Seleção de playlists litúrgicas de prelúdio, oração e pós-culto.
 * - Controle de reprodução (Play, Pause, Próxima, Anterior, Volume e Seek).
 * - Visualização da forma de onda/espectro sonoro em tempo real via Web Audio API.
 * - Ajuste do equalizador paramétrico de 3 bandas (Graves, Médios e Agudos).
 */

import { useState } from 'react'
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
} from 'lucide-react'
import { useAudioEngine } from '../hooks/useAudioEngine'
import { AudioSpectrumVisualizer } from './AudioSpectrumVisualizer'
import type { AudioPlaylist } from '../../contracts/media-contracts'

export interface AudioPlayerPanelProps {
  initialPlaylists?: AudioPlaylist[]
  className?: string
}

/**
 * Formata segundos numéricos em string mm:ss.
 */
function formatAudioTime(seconds: number): string {
  if (isNaN(seconds) || seconds < 0) return '00:00'
  const minutes = Math.floor(seconds / 60)
  const remainingSeconds = Math.floor(seconds % 60)
  return `${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`
}

/**
 * Painel completo de reprodução de áudio e ambientação litúrgica.
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

  return (
    <div className={`flex flex-col rounded-lg border border-white/10 bg-slate-900/90 text-slate-200 select-none overflow-hidden ${className}`}>
      {/* Cabeçalho do Player */}
      <div className="flex items-center justify-between border-b border-white/5 bg-slate-950/60 px-3 py-2">
        <div className="flex items-center gap-2">
          <span className="flex h-6 w-6 items-center justify-center rounded bg-sky-500/20 text-sky-400">
            <Music className="h-3.5 w-3.5" />
          </span>
          <span className="text-xs font-semibold text-slate-100">Áudio Ambiente</span>
        </div>

        {/* Seletor de Playlist Litúrgica */}
        <div className="flex items-center gap-1.5">
          <select
            value={currentPlaylist?.id || ''}
            onChange={(event) => selectPlaylist(event.target.value)}
            className="rounded border border-white/10 bg-slate-800 px-2 py-0.5 text-[11px] text-slate-300 focus:border-sky-500 focus:outline-none"
          >
            {playlists.map((playlist) => (
              <option key={playlist.id} value={playlist.id}>
                {playlist.name}
              </option>
            ))}
          </select>

          <button
            type="button"
            onClick={() => setIsEqExpanded((previous) => !previous)}
            className={`rounded p-1 text-xs transition ${
              isEqExpanded ? 'bg-sky-500/20 text-sky-400' : 'text-slate-400 hover:bg-slate-800 hover:text-slate-200'
            }`}
            title="Equalizador de 3 Bandas"
          >
            <Sliders className="h-3.5 w-3.5" />
          </button>
        </div>
      </div>

      {/* Área Central: Faixa Atual & Espectro */}
      <div className="p-3">
        <div className="flex items-center justify-between mb-2">
          <div className="min-w-0 flex-1 pr-2">
            <div className="truncate text-xs font-medium text-slate-100">
              {currentTrack?.title || 'Nenhuma faixa selecionada'}
            </div>
            <div className="truncate text-[10px] text-slate-400">
              {currentTrack?.artist || 'Lumen Worship'}
            </div>
          </div>
          <AudioSpectrumVisualizer
            isPlaying={isPlaying}
            getFrequencyData={getFrequencyData}
            barCount={20}
            height={28}
          />
        </div>

        {/* Barra de Progresso / Seek */}
        <div className="space-y-1">
          <input
            type="range"
            min={0}
            max={duration || 100}
            step={1}
            value={currentTime}
            onChange={(event) => seekTo(parseFloat(event.target.value))}
            className="h-1.5 w-full cursor-pointer appearance-none rounded-lg bg-slate-800 accent-sky-400"
          />
          <div className="flex justify-between text-[10px] font-mono text-slate-400">
            <span>{formatAudioTime(currentTime)}</span>
            <span>{formatAudioTime(duration)}</span>
          </div>
        </div>

        {/* Controles Principais */}
        <div className="mt-3 flex items-center justify-between">
          <div className="flex items-center gap-1">
            <button
              type="button"
              onClick={cycleRepeat}
              className={`rounded p-1 text-[11px] transition ${
                repeatMode !== 'off' ? 'text-sky-400' : 'text-slate-500 hover:text-slate-300'
              }`}
              title={`Repetir: ${repeatMode}`}
            >
              <Repeat className="h-3.5 w-3.5" />
            </button>
            <button
              type="button"
              onClick={toggleShuffleMode}
              className={`rounded p-1 text-[11px] transition ${
                shuffle ? 'text-sky-400' : 'text-slate-500 hover:text-slate-300'
              }`}
              title={`Aleatório: ${shuffle ? 'Ativo' : 'Desativado'}`}
            >
              <Shuffle className="h-3.5 w-3.5" />
            </button>
          </div>

          <div className="flex items-center gap-2">
            <button
              type="button"
              onClick={prevTrack}
              className="rounded p-1 text-slate-400 hover:bg-slate-800 hover:text-slate-100 transition"
              title="Faixa Anterior"
            >
              <SkipBack className="h-4 w-4" />
            </button>

            <button
              type="button"
              onClick={togglePlay}
              className="flex h-8 w-8 items-center justify-center rounded-full bg-sky-500 text-slate-950 shadow hover:bg-sky-400 transition"
              title={isPlaying ? 'Pausar' : 'Reproduzir'}
            >
              {isPlaying ? <Pause className="h-4 w-4 fill-current" /> : <Play className="h-4 w-4 fill-current ml-0.5" />}
            </button>

            <button
              type="button"
              onClick={nextTrack}
              className="rounded p-1 text-slate-400 hover:bg-slate-800 hover:text-slate-100 transition"
              title="Próxima Faixa"
            >
              <SkipForward className="h-4 w-4" />
            </button>
          </div>

          {/* Volume */}
          <div className="flex items-center gap-1.5">
            <button
              type="button"
              onClick={toggleMute}
              className="text-slate-400 hover:text-slate-200 transition"
              title={volume === 0 ? 'Desmutar' : 'Mutar'}
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
              className="h-1.5 w-16 cursor-pointer appearance-none rounded bg-slate-800 accent-sky-400"
            />
          </div>
        </div>

        {/* Gaveta Retrátil do Equalizador */}
        {isEqExpanded && (
          <div className="mt-3 rounded border border-white/5 bg-slate-950/70 p-2.5 animate-in fade-in duration-150">
            <div className="flex items-center justify-between mb-2">
              <span className="text-[10px] font-semibold text-slate-400 uppercase tracking-wider">
                Equalizador de 3 Bandas (WebAudio)
              </span>
              <button
                type="button"
                onClick={resetEqualizer}
                className="flex items-center gap-1 text-[10px] text-slate-400 hover:text-sky-400 transition"
                title="Zerar Equalizador (Flat 0dB)"
              >
                <RotateCcw className="h-2.5 w-2.5" />
                <span>Flat</span>
              </button>
            </div>

            <div className="grid grid-cols-3 gap-2 text-[10px]">
              <div>
                <div className="flex justify-between text-slate-400 mb-0.5">
                  <span>Graves</span>
                  <span className="font-mono">{equalizer.low > 0 ? `+${equalizer.low}` : equalizer.low}dB</span>
                </div>
                <input
                  type="range"
                  min={-12}
                  max={12}
                  step={0.5}
                  value={equalizer.low}
                  onChange={(event) => updateEqualizer({ low: parseFloat(event.target.value) })}
                  className="h-1 w-full cursor-pointer appearance-none rounded bg-slate-800 accent-sky-400"
                />
              </div>

              <div>
                <div className="flex justify-between text-slate-400 mb-0.5">
                  <span>Médios</span>
                  <span className="font-mono">{equalizer.mid > 0 ? `+${equalizer.mid}` : equalizer.mid}dB</span>
                </div>
                <input
                  type="range"
                  min={-12}
                  max={12}
                  step={0.5}
                  value={equalizer.mid}
                  onChange={(event) => updateEqualizer({ mid: parseFloat(event.target.value) })}
                  className="h-1 w-full cursor-pointer appearance-none rounded bg-slate-800 accent-sky-400"
                />
              </div>

              <div>
                <div className="flex justify-between text-slate-400 mb-0.5">
                  <span>Agudos</span>
                  <span className="font-mono">{equalizer.high > 0 ? `+${equalizer.high}` : equalizer.high}dB</span>
                </div>
                <input
                  type="range"
                  min={-12}
                  max={12}
                  step={0.5}
                  value={equalizer.high}
                  onChange={(event) => updateEqualizer({ high: parseFloat(event.target.value) })}
                  className="h-1 w-full cursor-pointer appearance-none rounded bg-slate-800 accent-sky-400"
                />
              </div>
            </div>
          </div>
        )}

        {/* Lista de Faixas da Playlist */}
        <div className="mt-3 max-h-28 overflow-y-auto space-y-1 pr-1">
          {currentPlaylist?.tracks.map((track, trackIndex) => {
            const isSelected = currentTrack?.id === track.id
            return (
              <button
                key={track.id}
                type="button"
                onClick={() => selectTrack(track)}
                className={`flex w-full items-center justify-between rounded px-2 py-1 text-left text-[11px] transition ${
                  isSelected
                    ? 'bg-sky-500/15 text-sky-400 font-medium'
                    : 'text-slate-300 hover:bg-slate-800/60 hover:text-slate-100'
                }`}
              >
                <div className="truncate flex-1 pr-2">
                  <span className="text-slate-500 mr-1.5 font-mono">{trackIndex + 1}.</span>
                  <span>{track.title}</span>
                </div>
                <span className="font-mono text-[10px] text-slate-500">
                  {formatAudioTime(track.durationSeconds)}
                </span>
              </button>
            )
          })}
        </div>
      </div>
    </div>
  )
}
