/**
 * @module @slices/navigation/ui/components/BackgroundVideoLoop
 * @description Componente de vídeo em background com double-buffering para o telão e modo leve para previews.
 *
 * ## Responsabilidades:
 * - No telão principal (`isPreview = false`): renderizar dois elementos <video> (Buffer A e B) com crossfade suave (~200ms) sem dual-autoplay simultâneo.
 * - No preview do console/remoto (`isPreview = true`): renderizar apenas um <video> leve com `preload="metadata"` e pausa automática quando oculto.
 * - Suportar backgrounds estáticos de imagem (com `decoding="async"`) e cor sólida.
 */

import { useState, useRef, useEffect } from 'react'
import type { BackgroundLayer } from '@lumen/core'

export interface BackgroundVideoLoopProps {
  background: BackgroundLayer
  crossfadeDurationMs?: number
  isPreview?: boolean
}

/**
 * Renderizador de fundo com double buffering de vídeo no telão e decodificação otimizada em previews.
 *
 * @param props - Propriedades de camada de fundo, tempo de transição e flag de preview.
 * @returns Elemento JSX contendo a camada de fundo do telão.
 */
export function BackgroundVideoLoop({
  background,
  crossfadeDurationMs = 200,
  isPreview = false,
}: BackgroundVideoLoopProps) {
  const [activeBuffer, setActiveBuffer] = useState<'A' | 'B'>('A')
  const activeBufferRef = useRef<'A' | 'B'>('A')
  const containerRef = useRef<HTMLDivElement>(null)
  const previewVideoRef = useRef<HTMLVideoElement>(null)
  const videoRefA = useRef<HTMLVideoElement>(null)
  const videoRefB = useRef<HTMLVideoElement>(null)
  const isVisibleRef = useRef(true)

  // Controle de visibilidade (IntersectionObserver + visibilitychange) para poupar CPU/GPU quando oculto
  useEffect(() => {
    if (background.type !== 'video') return
    const container = containerRef.current
    if (!container) return

    const syncPlayback = (visible: boolean) => {
      isVisibleRef.current = visible
      if (isPreview) {
        const video = previewVideoRef.current
        if (!video) return
        if (visible) {
          video.play().catch(() => {})
        } else {
          video.pause()
        }
        return
      }

      const activeVid = activeBufferRef.current === 'A' ? videoRefA.current : videoRefB.current
      if (!activeVid) return
      if (visible) {
        activeVid.play().catch(() => {})
      } else {
        activeVid.pause()
      }
    }

    const observer =
      typeof IntersectionObserver !== 'undefined'
        ? new IntersectionObserver(
            ([entry]) => {
              syncPlayback(entry.isIntersecting && !document.hidden)
            },
            { threshold: 0.05 },
          )
        : null

    observer?.observe(container)

    const handleVisibilityChange = () => {
      syncPlayback(!document.hidden)
    }
    document.addEventListener('visibilitychange', handleVisibilityChange)

    return () => {
      observer?.disconnect()
      document.removeEventListener('visibilitychange', handleVisibilityChange)
    }
  }, [background.type, background.source, isPreview])

  // Se o fundo for vídeo no telão principal, controla o double-buffering sem recriar listeners a cada troca
  useEffect(() => {
    if (isPreview || background.type !== 'video' || !background.source) return

    const videoA = videoRefA.current
    const videoB = videoRefB.current
    if (!videoA || !videoB) return

    activeBufferRef.current = 'A'
    setActiveBuffer('A')

    let isSwapping = false
    let pauseTimer: number | undefined

    const handleTimeUpdate = (event: Event) => {
      const currentVideo = event.target as HTMLVideoElement
      const isCurrentA = currentVideo === videoA
      if (
        (isCurrentA && activeBufferRef.current !== 'A') ||
        (!isCurrentA && activeBufferRef.current !== 'B')
      ) {
        return
      }

      const remainingTime = currentVideo.duration - currentVideo.currentTime

      // Quando faltar menos de 250ms para acabar o vídeo, inicia apenas o próximo buffer
      if (!isSwapping && remainingTime <= 0.25 && currentVideo.duration > 0.5) {
        isSwapping = true
        const nextBuffer = activeBufferRef.current === 'A' ? 'B' : 'A'
        const incomingVideo = nextBuffer === 'A' ? videoA : videoB
        const outgoingVideo = nextBuffer === 'A' ? videoB : videoA

        incomingVideo.currentTime = 0
        if (isVisibleRef.current) {
          incomingVideo.play().catch(() => {})
        }

        activeBufferRef.current = nextBuffer
        setActiveBuffer(nextBuffer)

        pauseTimer = window.setTimeout(() => {
          outgoingVideo.pause()
          isSwapping = false
        }, crossfadeDurationMs + 50)
      }
    }

    videoA.addEventListener('timeupdate', handleTimeUpdate)
    videoB.addEventListener('timeupdate', handleTimeUpdate)

    videoB.pause()
    if (isVisibleRef.current) {
      videoA.play().catch(() => {})
    }

    return () => {
      if (pauseTimer !== undefined) {
        window.clearTimeout(pauseTimer)
      }
      videoA.removeEventListener('timeupdate', handleTimeUpdate)
      videoB.removeEventListener('timeupdate', handleTimeUpdate)
    }
  }, [background.type, background.source, crossfadeDurationMs, isPreview])

  if (background.type === 'color') {
    return (
      <div
        className="absolute inset-0 transition-colors duration-500"
        style={{ backgroundColor: background.source || '#0F172A' }}
      />
    )
  }

  if (background.type === 'image') {
    return (
      <img
        src={background.source}
        alt=""
        decoding="async"
        className="absolute inset-0 h-full w-full object-cover transition-opacity duration-500"
      />
    )
  }

  if (isPreview) {
    return (
      <div
        ref={containerRef}
        className="absolute inset-0 overflow-hidden bg-black pointer-events-none"
        style={{ transform: 'translateZ(0)', backfaceVisibility: 'hidden' }}
      >
        <video
          ref={previewVideoRef}
          src={background.source}
          autoPlay
          loop
          muted
          playsInline
          preload="metadata"
          className="absolute inset-0 h-full w-full object-cover"
        />
        <div className="absolute inset-0 bg-black/30 z-20 pointer-events-none" />
      </div>
    )
  }

  return (
    <div
      ref={containerRef}
      className="absolute inset-0 overflow-hidden bg-black pointer-events-none"
      style={{ transform: 'translateZ(0)', backfaceVisibility: 'hidden' }}
    >
      {/* Buffer de Vídeo A */}
      <video
        ref={videoRefA}
        src={background.source}
        autoPlay
        muted
        playsInline
        preload="auto"
        className={`absolute inset-0 h-full w-full object-cover transition-opacity ${
          activeBuffer === 'A' ? 'opacity-100 z-10' : 'opacity-0 z-0'
        }`}
        style={{ transitionDuration: `${crossfadeDurationMs}ms` }}
      />

      {/* Buffer de Vídeo B (Seamless Double-Buffer sob demanda) */}
      <video
        ref={videoRefB}
        src={background.source}
        muted
        playsInline
        preload="metadata"
        className={`absolute inset-0 h-full w-full object-cover transition-opacity ${
          activeBuffer === 'B' ? 'opacity-100 z-10' : 'opacity-0 z-0'
        }`}
        style={{ transitionDuration: `${crossfadeDurationMs}ms` }}
      />

      {/* Overlay escuro sutil para garantir legibilidade de texto */}
      <div className="absolute inset-0 bg-black/30 z-20 pointer-events-none" />
    </div>
  )
}
