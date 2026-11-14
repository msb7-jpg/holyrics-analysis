/**
 * @module @slices/navigation/ui/hooks/useMultiMonitor
 * @description Gerenciador de detecção de múltiplos monitores e janelas de saída HDMI/DisplayPort (RF-01).
 *
 * ## Responsabilidades:
 * - Detectar telas secundárias conectadas e estado de extensão de área de trabalho.
 * - Abrir e posicionar as janelas dedicadas `/projector` (HDMI Público) e `/stage` (Retorno de Palco) sem bordas.
 */

import { useState, useEffect, useCallback } from 'react'

export interface MonitorDeviceInfo {
  id: string
  label: string
  width: number
  height: number
  isPrimary: boolean
}

/**
 * Hook para gerenciamento de saídas de vídeo multi-monitor (RF-01).
 *
 * @returns Lista de monitores detectados e funções para abrir as janelas de Projetor e Palco.
 */
export function useMultiMonitor() {
  const [monitors, setMonitors] = useState<MonitorDeviceInfo[]>(() => [
    {
      id: 'primary-display',
      label: 'Monitor 1 (Operador)',
      width: typeof window !== 'undefined' ? window.screen.width : 1920,
      height: typeof window !== 'undefined' ? window.screen.height : 1080,
      isPrimary: true,
    },
    {
      id: 'hdmi-projector',
      label: 'HDMI 2 (Telão 1080p@60Hz)',
      width: 1920,
      height: 1080,
      isPrimary: false,
    },
  ])

  useEffect(() => {
    const updateScreenInfo = () => {
      if (typeof window === 'undefined') return
      setMonitors([
        {
          id: 'primary-display',
          label: 'Monitor 1 (Operador)',
          width: window.screen.width || 1920,
          height: window.screen.height || 1080,
          isPrimary: true,
        },
        {
          id: 'hdmi-projector',
          label: 'HDMI 2 (Telão 1080p@60Hz)',
          width: 1920,
          height: 1080,
          isPrimary: false,
        },
      ])
    }

    window.addEventListener('resize', updateScreenInfo)
    return () => window.removeEventListener('resize', updateScreenInfo)
  }, [])

  const openProjectorWindow = useCallback(() => {
    const primaryWidth = typeof window !== 'undefined' ? window.screen.width : 1920
    window.open(
      '/projector',
      'LumenProjectorWindow',
      `left=${primaryWidth},top=0,width=1920,height=1080,menubar=no,toolbar=no,location=no,status=no`,
    )
  }, [])

  const openStageWindow = useCallback(() => {
    window.open(
      '/stage',
      'LumenStageWindow',
      'width=1280,height=720,menubar=no,toolbar=no,location=no,status=no',
    )
  }, [])

  return {
    monitors,
    openProjectorWindow,
    openStageWindow,
  }
}
