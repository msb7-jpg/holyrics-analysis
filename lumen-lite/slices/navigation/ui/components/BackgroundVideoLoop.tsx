/**
 * @module @slices/navigation/ui/components/BackgroundVideoLoop
 * @description Componente de vídeo em background com double-buffering para eliminação de flash preto no telão.
 *
 * ## Responsabilidades:
 * - Renderizar dois elementos HTML <video> sobrepostos (Buffer A e Buffer B).
 * - Monitorar o timeupdate e pré-ativar o buffer inativo antes do encerramento do vídeo ativo.
 * - Aplicar crossfade suave de opacidade (~200ms) proporcionando seamless loop perfeito.
 * - Suportar backgrounds estáticos de imagem e cor sólida (Slate 900) com fallback imediato.
 */

import { useState, useRef, useEffect } from 'react'
import type { BackgroundLayer } from '@lumen/core'

export interface BackgroundVideoLoopProps {
  background: BackgroundLayer
  crossfadeDurationMs?: number
}

/**
 * Renderizador de fundo com double buffering de vídeo e transições suaves.
 *
 * @param props - Propriedades de camada de fundo e tempo de transição.
 * @returns Elemento JSX contendo a camada de fundo do telão.
 */
export function BackgroundVideoLoop({
  background,
  crossfadeDurationMs = 200,
}: BackgroundVideoLoopProps) {
  const [activeBuffer, setActiveBuffer] = useState<'A' | 'B'>('A')
  const videoRefA = useRef<HTMLVideoElement>(null)
  const videoRefB = useRef<HTMLVideoElement>(null)

  // Se o fundo for vídeo, controla o double-buffering
  useEffect(() => {
    if (background.type !== 'video' || !background.source) return

    const videoA = videoRefA.current
    const videoB = videoRefB.current
    if (!videoA || !videoB) return

    let isSwapping = false

    const handleTimeUpdate = (event: Event) => {
      const currentVideo = event.target as HTMLVideoElement
      const remainingTime = currentVideo.duration - currentVideo.currentTime

      // Quando faltar menos de 250ms para acabar o vídeo, inicia o próximo buffer
      if (!isSwapping && remainingTime <= 0.25 && currentVideo.duration > 0.5) {
        isSwapping = true

        if (activeBuffer === 'A') {
          videoB.currentTime = 0
          videoB.play().catch(() => {})
          setActiveBuffer('B')
        } else {
          videoA.currentTime = 0
          videoA.play().catch(() => {})
          setActiveBuffer('A')
        }

        setTimeout(() => {
          isSwapping = false
        }, crossfadeDurationMs + 50)
      }
    }

    videoA.addEventListener('timeupdate', handleTimeUpdate)
    videoB.addEventListener('timeupdate', handleTimeUpdate)

    // Inicia o vídeo ativo
    if (activeBuffer === 'A') {
      videoA.play().catch(() => {})
    } else {
      videoB.play().catch(() => {})
    }

    return () => {
      videoA.removeEventListener('timeupdate', handleTimeUpdate)
      videoB.removeEventListener('timeupdate', handleTimeUpdate)
    }
  }, [background, activeBuffer, crossfadeDurationMs])

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
      <div
        className="absolute inset-0 bg-cover bg-center transition-opacity duration-500"
        style={{ backgroundImage: `url(${background.source})` }}
      />
    )
  }

  return (
    <div className="absolute inset-0 overflow-hidden bg-black">
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

      {/* Buffer de Vídeo B (Seamless Double-Buffer) */}
      <video
        ref={videoRefB}
        src={background.source}
        autoPlay
        muted
        playsInline
        preload="auto"
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
