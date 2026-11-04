/**
 * @module @slices/navigation/ui/hooks/useAutoFitText
 * @description Hook de alta performance para redimensionamento e auto-fit dinâmico de fontes no telão.
 *
 * ## Responsabilidades:
 * - Calcular em tempo real o fontSize ideal para preencher a tela de projeção sem corte ou estouro.
 * - Utilizar medição rápida via busca binária baseada na altura e largura disponíveis do container.
 * - Conectar-se com ResizeObserver para reagir a alterações na resolução do monitor HDMI.
 */

import { useState, useLayoutEffect, type RefObject } from 'react'

export interface UseAutoFitTextOptions {
  containerRef: RefObject<HTMLElement | null>
  content: string[]
  minFontSize?: number
  maxFontSize?: number
  lineHeightMultiplier?: number
}

/**
 * Calcula o tamanho de fonte ideal de forma responsiva para evitar overflow no projetor.
 *
 * @param options - Parâmetros com referências de container, conteúdo e limites de fonte.
 * @param options.containerRef - Referência do elemento HTML container pai para medição.
 * @param options.content - Coleção de linhas higienizadas a serem exibidas.
 * @param options.minFontSize - Tamanho mínimo de fonte permitido em pixels.
 * @param options.maxFontSize - Tamanho máximo de fonte permitido em pixels.
 * @param options.lineHeightMultiplier - Multiplicador de altura de linha.
 * @returns Tamanho de fonte em pixels e booleano de pronto.
 */
export function useAutoFitText({
  containerRef,
  content,
  minFontSize = 24,
  maxFontSize = 96,
  lineHeightMultiplier = 1.35,
}: UseAutoFitTextOptions): { fontSize: number; isReady: boolean } {
  const [fontSize, setFontSize] = useState<number>(maxFontSize)
  const [isReady, setIsReady] = useState<boolean>(false)

  useLayoutEffect(() => {
    const container = containerRef.current
    if (!container || content.length === 0) {
      setFontSize(maxFontSize)
      setIsReady(true)
      return
    }

    const calculateFit = () => {
      const containerWidth = container.clientWidth
      const containerHeight = container.clientHeight

      if (containerWidth <= 0 || containerHeight <= 0) return

      // Busca binária para encontrar o fontSize ótimo
      let low = minFontSize
      let high = maxFontSize
      let optimal = minFontSize

      // Cria elemento fantasma invisível para medição precisa de texto
      const measureElement = document.createElement('div')
      measureElement.style.position = 'absolute'
      measureElement.style.visibility = 'hidden'
      measureElement.style.pointerEvents = 'none'
      measureElement.style.width = `${containerWidth}px`
      measureElement.style.whiteSpace = 'pre-wrap'
      measureElement.style.wordBreak = 'break-word'
      measureElement.style.lineHeight = String(lineHeightMultiplier)
      measureElement.style.fontFamily = window.getComputedStyle(container).fontFamily
      measureElement.style.fontWeight = '700'
      measureElement.innerText = content.join('\n')

      document.body.appendChild(measureElement)

      while (low <= high) {
        const mid = Math.floor((low + high) / 2)
        measureElement.style.fontSize = `${mid}px`

        const textHeight = measureElement.offsetHeight
        const textWidth = measureElement.scrollWidth

        if (textHeight <= containerHeight && textWidth <= containerWidth) {
          optimal = mid
          low = mid + 1
        } else {
          high = mid - 1
        }
      }

      document.body.removeChild(measureElement)

      setFontSize(optimal)
      setIsReady(true)
    }

    calculateFit()

    const observer = new ResizeObserver(() => {
      calculateFit()
    })

    observer.observe(container)

    return () => {
      observer.disconnect()
    }
  }, [containerRef, content, minFontSize, maxFontSize, lineHeightMultiplier])

  return { fontSize, isReady }
}
