/**
 * @module @slices/media/ui/components/AudioSpectrumVisualizer
 * @description Visualizador em tempo real de espectro sonoro (FFT) em Canvas para o player litúrgico (RF-28).
 *
 * ## Responsabilidades:
 * - Renderizar em 60fps as barras de frequência de áudio utilizando requestAnimationFrame.
 * - Adotar a estética sóbria Slate Pro Studio com tons de ardósia e realces em Sky.
 * - Desativar o loop de renderização automaticamente quando o áudio estiver pausado.
 */

import { useEffect, useRef } from 'react'

export interface AudioSpectrumVisualizerProps {
  isPlaying: boolean
  getFrequencyData: (outputArray: Uint8Array<ArrayBuffer>) => void
  barCount?: number
  height?: number
  className?: string
}

/**
 * Componente de visualização de frequência sonora em tempo real.
 *
 * @param props - Propriedades do componente.
 * @param props.isPlaying - Se a faixa de áudio está tocando.
 * @param props.getFrequencyData - Callback que preenche o buffer FFT.
 * @param props.barCount - Número de barras de frequência a renderizar (padrão: 24).
 * @param props.height - Altura do canvas em pixels (padrão: 36).
 * @param props.className - Classes Tailwind adicionais.
 * @returns Elemento JSX do canvas renderizado.
 */
export function AudioSpectrumVisualizer({
  isPlaying,
  getFrequencyData,
  barCount = 24,
  height = 36,
  className = '',
}: AudioSpectrumVisualizerProps) {
  const canvasRef = useRef<HTMLCanvasElement | null>(null)
  const animationFrameIdRef = useRef<number | null>(null)
  const frequencyBufferRef = useRef<Uint8Array<ArrayBuffer>>(new Uint8Array(32))

  useEffect(() => {
    const canvas = canvasRef.current
    if (!canvas) return

    const canvasContext = canvas.getContext('2d')
    if (!canvasContext) return

    const renderFrame = () => {
      const buffer = frequencyBufferRef.current
      getFrequencyData(buffer)

      const width = canvas.width
      const canvasHeight = canvas.height
      canvasContext.clearRect(0, 0, width, canvasHeight)

      const barWidth = Math.max(2, (width / barCount) - 2)
      const step = Math.max(1, Math.floor(buffer.length / barCount))

      for (let barIndex = 0; barIndex < barCount; barIndex += 1) {
        const rawValue = buffer[barIndex * step] || 0
        const normalizedHeight = isPlaying ? (rawValue / 255) * (canvasHeight - 4) : 2
        const renderHeight = Math.max(2, normalizedHeight)

        const coordX = barIndex * (barWidth + 2)
        const coordY = canvasHeight - renderHeight

        // Gradiente sóbrio Slate Pro Studio (Sky 400 a Slate 600)
        const barGradient = canvasContext.createLinearGradient(0, coordY, 0, canvasHeight)
        barGradient.addColorStop(0, isPlaying ? 'rgba(56, 189, 248, 0.9)' : 'rgba(100, 116, 139, 0.3)')
        barGradient.addColorStop(1, isPlaying ? 'rgba(14, 165, 233, 0.4)' : 'rgba(51, 65, 85, 0.2)')

        canvasContext.fillStyle = barGradient
        canvasContext.beginPath()
        canvasContext.roundRect(coordX, coordY, barWidth, renderHeight, [2, 2, 0, 0])
        canvasContext.fill()
      }

      animationFrameIdRef.current = requestAnimationFrame(renderFrame)
    }

    renderFrame()

    return () => {
      if (animationFrameIdRef.current) {
        cancelAnimationFrame(animationFrameIdRef.current)
      }
    }
  }, [barCount, getFrequencyData, isPlaying])

  return (
    <canvas
      ref={canvasRef}
      width={barCount * 8}
      height={height}
      className={`rounded bg-slate-950/60 border border-white/5 ${className}`}
    />
  )
}
