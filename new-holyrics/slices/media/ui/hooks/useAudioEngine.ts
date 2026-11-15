/**
 * @module @slices/media/ui/hooks/useAudioEngine
 * @description Hook reativo construído sobre a Web Audio API nativa para reprodução litúrgica de áudio e equalização sonoro (RF-28).
 *
 * ## Responsabilidades:
 * - Instanciação lazy e gerenciamento do ciclo de vida de `AudioContext`.
 * - Cadeia de processamento sonoro: Source -> BiquadFilter (Low) -> BiquadFilter (Mid) -> BiquadFilter (High) -> GainNode -> AnalyserNode -> Destination.
 * - Suporte a equalizador paramétrico de 3 bandas (-12dB a +12dB).
 * - Fades suaves de volume para evitar estalos analógicos/digitais.
 * - Extração de dados FFT em tempo real para visualizadores de espectro sonoro.
 * - Gestão de playlists com repetição (`off`, `all`, `one`) e avanço automático.
 */

import { useState, useEffect, useRef, useCallback } from 'react'
import type { AudioPlaylist, AudioTrack, EqualizerSettings, RepeatMode } from '../../contracts/media-contracts'
import { DEFAULT_AUDIO_PLAYLISTS, DEFAULT_EQUALIZER_SETTINGS } from '../../contracts/media-contracts'

export interface AudioEngineHookResult {
  playlists: AudioPlaylist[]
  currentPlaylist: AudioPlaylist | null
  currentTrack: AudioTrack | null
  isPlaying: boolean
  currentTime: number
  duration: number
  volume: number
  equalizer: EqualizerSettings
  repeatMode: RepeatMode
  shuffle: boolean
  play: () => Promise<void>
  pause: () => void
  togglePlay: () => Promise<void>
  nextTrack: () => void
  prevTrack: () => void
  seekTo: (seconds: number) => void
  setVolumeLevel: (level: number) => void
  updateEqualizer: (settings: Partial<EqualizerSettings>) => void
  resetEqualizer: () => void
  selectPlaylist: (playlistId: string) => void
  selectTrack: (track: AudioTrack) => void
  setRepeat: (mode: RepeatMode) => void
  toggleShuffleMode: () => void
  getFrequencyData: (outputArray: Uint8Array<ArrayBuffer>) => void
}

/**
 * Hook universal de processamento e reprodução de áudio litúrgico.
 *
 * @param initialPlaylists - Playlists iniciais fornecidas ao reprodutor.
 * @returns Interface completa de controle e telemetria do reprodutor.
 */
export function useAudioEngine(initialPlaylists: AudioPlaylist[] = DEFAULT_AUDIO_PLAYLISTS): AudioEngineHookResult {
  const [playlists] = useState<AudioPlaylist[]>(initialPlaylists)
  const [currentPlaylist, setCurrentPlaylist] = useState<AudioPlaylist | null>(initialPlaylists[0] || null)
  const [currentTrack, setCurrentTrack] = useState<AudioTrack | null>(initialPlaylists[0]?.tracks[0] || null)
  const [isPlaying, setIsPlaying] = useState(false)
  const [currentTime, setCurrentTime] = useState(0)
  const [duration, setDuration] = useState(0)
  const [volume, setVolume] = useState(0.8)
  const [equalizer, setEqualizer] = useState<EqualizerSettings>(DEFAULT_EQUALIZER_SETTINGS)
  const [repeatMode, setRepeatMode] = useState<RepeatMode>('all')
  const [shuffle, setShuffle] = useState(false)

  // Referências para a Web Audio API
  const audioElementRef = useRef<HTMLAudioElement | null>(null)
  const audioContextRef = useRef<AudioContext | null>(null)
  const sourceNodeRef = useRef<MediaElementAudioSourceNode | null>(null)
  const lowFilterRef = useRef<BiquadFilterNode | null>(null)
  const midFilterRef = useRef<BiquadFilterNode | null>(null)
  const highFilterRef = useRef<BiquadFilterNode | null>(null)
  const masterGainRef = useRef<GainNode | null>(null)
  const analyserNodeRef = useRef<AnalyserNode | null>(null)

  /**
   * Inicializa o grafo de nós da Web Audio API de forma lazy após interação do usuário.
   */
  const initAudioGraph = useCallback(() => {
    if (audioContextRef.current || typeof window === 'undefined') {
      return
    }

    const AudioContextClass = window.AudioContext || (window as unknown as { webkitAudioContext: typeof AudioContext }).webkitAudioContext
    if (!AudioContextClass) {
      return
    }

    const context = new AudioContextClass()
    const audioElement = new Audio()
    audioElement.crossOrigin = 'anonymous'

    // 1. Filtro Graves (Lowshelf ~120Hz)
    const lowFilter = context.createBiquadFilter()
    lowFilter.type = 'lowshelf'
    lowFilter.frequency.value = 120
    lowFilter.gain.value = equalizer.low

    // 2. Filtro Médios (Peaking ~1000Hz)
    const midFilter = context.createBiquadFilter()
    midFilter.type = 'peaking'
    midFilter.frequency.value = 1000
    midFilter.Q.value = 1.0
    midFilter.gain.value = equalizer.mid

    // 3. Filtro Agudos (Highshelf ~6000Hz)
    const highFilter = context.createBiquadFilter()
    highFilter.type = 'highshelf'
    highFilter.frequency.value = 6000
    highFilter.gain.value = equalizer.high

    // 4. Ganho Mestre
    const masterGain = context.createGain()
    masterGain.gain.value = volume

    // 5. Analisador FFT para espectro
    const analyser = context.createAnalyser()
    analyser.fftSize = 64
    analyser.smoothingTimeConstant = 0.8

    // 6. Conecta MediaElementSource aos nós
    const source = context.createMediaElementSource(audioElement)
    source.connect(lowFilter)
    lowFilter.connect(midFilter)
    midFilter.connect(highFilter)
    highFilter.connect(masterGain)
    masterGain.connect(analyser)
    analyser.connect(context.destination)

    audioContextRef.current = context
    audioElementRef.current = audioElement
    sourceNodeRef.current = source
    lowFilterRef.current = lowFilter
    midFilterRef.current = midFilter
    highFilterRef.current = highFilter
    masterGainRef.current = masterGain
    analyserNodeRef.current = analyser

    // Eventos do elemento HTMLAudio
    audioElement.addEventListener('timeupdate', () => {
      setCurrentTime(audioElement.currentTime)
    })

    audioElement.addEventListener('loadedmetadata', () => {
      setDuration(audioElement.duration || 0)
    })

    audioElement.addEventListener('ended', () => {
      handleTrackEnded()
    })
  }, [equalizer.high, equalizer.low, equalizer.mid, volume])

  /**
   * Trata o avanço automático ao término da faixa atual.
   */
  const handleTrackEnded = useCallback(() => {
    if (repeatMode === 'one') {
      if (audioElementRef.current) {
        audioElementRef.current.currentTime = 0
        audioElementRef.current.play().catch(() => {})
      }
      return
    }

    if (!currentPlaylist || currentPlaylist.tracks.length === 0) {
      setIsPlaying(false)
      return
    }

    const currentTrackIndex = currentPlaylist.tracks.findIndex((entry) => entry.id === currentTrack?.id)
    if (currentTrackIndex < 0) return

    let nextTrackIndex = currentTrackIndex + 1

    if (shuffle) {
      nextTrackIndex = Math.floor(Math.random() * currentPlaylist.tracks.length)
    }

    if (nextTrackIndex >= currentPlaylist.tracks.length) {
      if (repeatMode === 'all') {
        nextTrackIndex = 0
      } else {
        setIsPlaying(false)
        return
      }
    }

    const nextTrackItem = currentPlaylist.tracks[nextTrackIndex]
    setCurrentTrack(nextTrackItem)
  }, [currentPlaylist, currentTrack?.id, repeatMode, shuffle])

  // Atualiza a faixa no elemento de áudio quando currentTrack muda
  useEffect(() => {
    if (!currentTrack || !audioElementRef.current) return

    audioElementRef.current.src = currentTrack.url
    audioElementRef.current.load()

    if (isPlaying) {
      audioElementRef.current.play().catch(() => {
        setIsPlaying(false)
      })
    }
  }, [currentTrack, isPlaying])

  /**
   * Inicia ou retoma a reprodução com ramp up suave de volume.
   */
  const play = useCallback(async () => {
    initAudioGraph()

    if (audioContextRef.current && audioContextRef.current.state === 'suspended') {
      await audioContextRef.current.resume()
    }

    if (audioElementRef.current && currentTrack) {
      try {
        if (!audioElementRef.current.src || !audioElementRef.current.src.includes(currentTrack.url)) {
          audioElementRef.current.src = currentTrack.url
        }

        await audioElementRef.current.play()
        setIsPlaying(true)
      } catch {
        setIsPlaying(false)
      }
    }
  }, [currentTrack, initAudioGraph])

  /**
   * Pausa a reprodução.
   */
  const pause = useCallback(() => {
    if (audioElementRef.current) {
      audioElementRef.current.pause()
      setIsPlaying(false)
    }
  }, [])

  /**
   * Alterna estado de reprodução.
   */
  const togglePlay = useCallback(async () => {
    if (isPlaying) {
      pause()
    } else {
      await play()
    }
  }, [isPlaying, pause, play])

  /**
   * Pula para a próxima faixa da playlist.
   */
  const nextTrack = useCallback(() => {
    if (!currentPlaylist || currentPlaylist.tracks.length === 0) return
    const currentIndex = currentPlaylist.tracks.findIndex((entry) => entry.id === currentTrack?.id)
    const nextIndex = (currentIndex + 1) % currentPlaylist.tracks.length
    setCurrentTrack(currentPlaylist.tracks[nextIndex])
  }, [currentPlaylist, currentTrack?.id])

  /**
   * Retorna para a faixa anterior.
   */
  const prevTrack = useCallback(() => {
    if (!currentPlaylist || currentPlaylist.tracks.length === 0) return
    const currentIndex = currentPlaylist.tracks.findIndex((entry) => entry.id === currentTrack?.id)
    const prevIndex = currentIndex <= 0 ? currentPlaylist.tracks.length - 1 : currentIndex - 1
    setCurrentTrack(currentPlaylist.tracks[prevIndex])
  }, [currentPlaylist, currentTrack?.id])

  /**
   * Realiza seek para o tempo indicado em segundos.
   *
   * @param seconds - Tempo em segundos.
   */
  const seekTo = useCallback((seconds: number) => {
    if (audioElementRef.current) {
      audioElementRef.current.currentTime = seconds
      setCurrentTime(seconds)
    }
  }, [])

  /**
   * Define o volume com suavização linear no GainNode.
   *
   * @param level - Volume entre 0 e 1.
   */
  const setVolumeLevel = useCallback((level: number) => {
    const clampedLevel = Math.max(0, Math.min(1, level))
    setVolume(clampedLevel)

    if (masterGainRef.current && audioContextRef.current) {
      const now = audioContextRef.current.currentTime
      masterGainRef.current.gain.cancelScheduledValues(now)
      masterGainRef.current.gain.linearRampToValueAtTime(clampedLevel, now + 0.05)
    }
  }, [])

  /**
   * Atualiza as configurações de ganho das bandas do equalizador.
   *
   * @param settings - Valores parciais de ganho em decibéis.
   */
  const updateEqualizer = useCallback((settings: Partial<EqualizerSettings>) => {
    setEqualizer((previous) => {
      const updated: EqualizerSettings = {
        low: settings.low !== undefined ? Math.max(-12, Math.min(12, settings.low)) : previous.low,
        mid: settings.mid !== undefined ? Math.max(-12, Math.min(12, settings.mid)) : previous.mid,
        high: settings.high !== undefined ? Math.max(-12, Math.min(12, settings.high)) : previous.high,
      }

      if (lowFilterRef.current) lowFilterRef.current.gain.value = updated.low
      if (midFilterRef.current) midFilterRef.current.gain.value = updated.mid
      if (highFilterRef.current) highFilterRef.current.gain.value = updated.high

      return updated
    })
  }, [])

  /**
   * Restaura o equalizador para o perfil Flat (0dB em todas as bandas).
   */
  const resetEqualizer = useCallback(() => {
    updateEqualizer(DEFAULT_EQUALIZER_SETTINGS)
  }, [updateEqualizer])

  /**
   * Seleciona uma playlist ativa.
   *
   * @param playlistId - ID da playlist.
   */
  const selectPlaylist = useCallback((playlistId: string) => {
    const foundPlaylist = playlists.find((entry) => entry.id === playlistId)
    if (foundPlaylist) {
      setCurrentPlaylist(foundPlaylist)
      if (foundPlaylist.tracks.length > 0) {
        setCurrentTrack(foundPlaylist.tracks[0])
      }
    }
  }, [playlists])

  /**
   * Seleciona uma faixa específica para reprodução imediata.
   *
   * @param track - Faixa de áudio.
   */
  const selectTrack = useCallback((track: AudioTrack) => {
    setCurrentTrack(track)
  }, [])

  /**
   * Preenche o array com os dados de frequência FFT instantâneos.
   *
   * @param outputArray - Array de bytes tipado Uint8Array.
   */
  const getFrequencyData = useCallback((outputArray: Uint8Array<ArrayBuffer>) => {
    if (analyserNodeRef.current && isPlaying) {
      analyserNodeRef.current.getByteFrequencyData(outputArray)
    } else {
      outputArray.fill(0)
    }
  }, [isPlaying])

  return {
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
    play,
    pause,
    togglePlay,
    nextTrack,
    prevTrack,
    seekTo,
    setVolumeLevel,
    updateEqualizer,
    resetEqualizer,
    selectPlaylist,
    selectTrack,
    setRepeat: setRepeatMode,
    toggleShuffleMode: () => setShuffle((previous) => !previous),
    getFrequencyData,
  }
}
