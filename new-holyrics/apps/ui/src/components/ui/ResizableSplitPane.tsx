/**
 * @module @ui/components/ui/ResizableSplitPane
 * @description Divisor redimensionável (horizontal ou vertical) com persistência opcional em localStorage.
 */

import { useState, useRef, useEffect, Children, type ReactNode } from 'react'

export interface ResizableSplitPaneProps {
  direction?: 'horizontal' | 'vertical'
  initialSizes: number[]
  minSizes?: number[]
  storageKey?: string
  className?: string
  children: ReactNode
}

/**
 * Renderiza painéis filhos separados por alças arrastáveis com persistência em localStorage.
 *
 * @param props - Propriedades do divisor.
 * @returns Elemento JSX com os painéis redimensionáveis.
 */
export function ResizableSplitPane({
  direction = 'horizontal',
  initialSizes,
  minSizes,
  storageKey,
  className = '',
  children,
}: ResizableSplitPaneProps) {
  const childArray = Children.toArray(children)
  const containerRef = useRef<HTMLDivElement | null>(null)
  const draggingIndexRef = useRef<number | null>(null)

  const [sizes, setSizes] = useState<number[]>(() => {
    if (storageKey && typeof window !== 'undefined') {
      try {
        const saved = window.localStorage.getItem(storageKey)
        if (saved) {
          const parsed = JSON.parse(saved) as unknown
          if (Array.isArray(parsed) && parsed.length === childArray.length) {
            return parsed.map(Number)
          }
        }
      } catch {
        // Ignora localStorage corrompido
      }
    }
    return initialSizes
  })

  useEffect(() => {
    if (storageKey && typeof window !== 'undefined') {
      try {
        window.localStorage.setItem(storageKey, JSON.stringify(sizes))
      } catch {
        // Ignora erro de quota
      }
    }
  }, [sizes, storageKey])

  useEffect(() => {
    const handlePointerMove = (event: MouseEvent) => {
      const dividerIndex = draggingIndexRef.current
      if (dividerIndex === null || !containerRef.current) return

      const rect = containerRef.current.getBoundingClientRect()
      const totalPx = direction === 'horizontal' ? rect.width : rect.height
      if (totalPx <= 0) return

      const pointerPos =
        direction === 'horizontal' ? event.clientX - rect.left : event.clientY - rect.top
      const pointerPercent = (pointerPos / totalPx) * 100

      setSizes((prev) => {
        const prefixSum = prev.slice(0, dividerIndex).reduce((acc, val) => acc + val, 0)
        const pairSum = prev[dividerIndex] + prev[dividerIndex + 1]
        const minA = minSizes?.[dividerIndex] ?? 14
        const minB = minSizes?.[dividerIndex + 1] ?? 14

        const rawA = pointerPercent - prefixSum
        const clampedA = Math.max(minA, Math.min(pairSum - minB, rawA))
        const clampedB = pairSum - clampedA

        const next = [...prev]
        next[dividerIndex] = Number(clampedA.toFixed(2))
        next[dividerIndex + 1] = Number(clampedB.toFixed(2))
        return next
      })
    }

    const handlePointerUp = () => {
      if (draggingIndexRef.current !== null) {
        draggingIndexRef.current = null
        document.body.style.cursor = ''
        document.body.style.userSelect = ''
      }
    }

    window.addEventListener('mousemove', handlePointerMove)
    window.addEventListener('mouseup', handlePointerUp)
    return () => {
      window.removeEventListener('mousemove', handlePointerMove)
      window.removeEventListener('mouseup', handlePointerUp)
    }
  }, [direction, minSizes])

  const startDragging = (index: number) => {
    draggingIndexRef.current = index
    document.body.style.cursor = direction === 'horizontal' ? 'col-resize' : 'row-resize'
    document.body.style.userSelect = 'none'
  }

  const isHorizontal = direction === 'horizontal'

  return (
    <div
      ref={containerRef}
      className={`flex ${isHorizontal ? 'flex-row' : 'flex-col'} overflow-hidden ${className}`}
    >
      {childArray.map((child, index) => {
        const sizePercent = sizes[index] ?? 100 / childArray.length
        return (
          <div key={index} className="contents">
            <div
              style={{
                [isHorizontal ? 'width' : 'height']: `${sizePercent}%`,
              }}
              className="flex min-w-0 min-h-0 flex-col overflow-hidden"
            >
              {child}
            </div>
            {index < childArray.length - 1 && (
              <div
                role="separator"
                aria-orientation={isHorizontal ? 'vertical' : 'horizontal'}
                onMouseDown={() => startDragging(index)}
                className={`shrink-0 transition-colors ${
                  isHorizontal
                    ? 'w-1 cursor-col-resize bg-white/5 hover:bg-sky-500/60 active:bg-sky-400'
                    : 'h-1 cursor-row-resize bg-white/5 hover:bg-sky-500/60 active:bg-sky-400'
                }`}
              />
            )}
          </div>
        )
      })}
    </div>
  )
}
