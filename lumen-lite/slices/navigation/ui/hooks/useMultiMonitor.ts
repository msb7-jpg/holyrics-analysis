/**
 * @module @slices/navigation/ui/hooks/useMultiMonitor
 * @description Gerenciador de detecção de múltiplos monitores e janelas de saída HDMI/DisplayPort (RF-01).
 *
 * ## Responsabilidades:
 * - Detectar telas secundárias conectadas via Tauri v2 (`availableMonitors`) ou fallback do navegador.
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

function isTauriRuntime(): boolean {
  return typeof window !== 'undefined' && '__TAURI_INTERNALS__' in window
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
    let active = true

    const syncNativeMonitors = async () => {
      if (!isTauriRuntime()) return
      try {
        const { availableMonitors } = await import('@tauri-apps/api/window')
        const nativeList = await availableMonitors()
        if (!active || nativeList.length === 0) return

        setMonitors(
          nativeList.map((monitorItem, idx) => ({
            id: monitorItem.name || `display-${idx}`,
            label:
              idx === 0
                ? `Monitor 1 (${monitorItem.name || 'Principal'})`
                : `Saída ${idx + 1} (${monitorItem.name || 'HDMI'})`,
            width: monitorItem.size.width,
            height: monitorItem.size.height,
            isPrimary: idx === 0,
          })),
        )
      } catch {
        // Mantém fallback em caso de erro de permissão
      }
    }

    syncNativeMonitors()

    const updateScreenInfo = () => {
      if (typeof window === 'undefined' || isTauriRuntime()) return
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
    return () => {
      active = false
      window.removeEventListener('resize', updateScreenInfo)
    }
  }, [])

  const openProjectorWindow = useCallback(() => {
    if (isTauriRuntime()) {
      void (async () => {
        try {
          const { availableMonitors, PhysicalPosition } = await import('@tauri-apps/api/window')
          const { WebviewWindow } = await import('@tauri-apps/api/webviewWindow')
          const nativeList = await availableMonitors()
          const targetMonitor = nativeList[1] ?? nativeList[0]
          const projectorWin = await WebviewWindow.getByLabel('projector')

          if (projectorWin) {
            if (targetMonitor) {
              await projectorWin.setPosition(
                new PhysicalPosition(targetMonitor.position.x, targetMonitor.position.y),
              )
            }
            await projectorWin.setFullscreen(true)
            await projectorWin.show()
            await projectorWin.setFocus()
            return
          }
        } catch {
          // Fallback para window.open abaixo
        }
      })()
      return
    }

    const primaryWidth = typeof window !== 'undefined' ? window.screen.width : 1920
    window.open(
      '/projector',
      'LumenProjectorWindow',
      `left=${primaryWidth},top=0,width=1920,height=1080,menubar=no,toolbar=no,location=no,status=no`,
    )
  }, [])

  const openStageWindow = useCallback(() => {
    if (isTauriRuntime()) {
      void (async () => {
        try {
          const { WebviewWindow } = await import('@tauri-apps/api/webviewWindow')
          const stageWin = await WebviewWindow.getByLabel('stage')
          if (stageWin) {
            await stageWin.show()
            await stageWin.setFocus()
            return
          }
        } catch {
          // Fallback para window.open abaixo
        }
      })()
      return
    }

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
