/**
 * @module @lumen/broadcast/useBroadcastConnector
 * @description Hook reativo para conexão WebSocket da aplicação de Broadcast (OBS Browser Source) com o daemon.
 */

import { useState, useEffect, useRef } from 'react'
import type { ScreenState } from '@lumen/core'
import { INITIAL_SCREEN_STATE } from '@lumen/core'
import { INITIAL_LIVE_STAGE_STATE, type LiveStageState } from '@slices/stage/contracts/stage-contracts'

const WEBSOCKET_URL = 'ws://localhost:4040/ws?type=broadcast'

/**
 * Hook para conectar a uma fonte de estado da FSM e Stage via WebSocket.
 *
 * @returns Um objeto contendo o estado atualizado da tela, estado de alertas e o status da conexão.
 */
export function useBroadcastConnector() {
  const [state, setState] = useState<ScreenState>(INITIAL_SCREEN_STATE)
  const [stageState, setStageState] = useState<LiveStageState>(INITIAL_LIVE_STAGE_STATE)
  const [isConnected, setIsConnected] = useState(false)
  const websocketRef = useRef<WebSocket | null>(null)

  useEffect(() => {
    function connect() {
      websocketRef.current = new WebSocket(WEBSOCKET_URL)

      websocketRef.current.onopen = () => {
        setIsConnected(true)
      }

      websocketRef.current.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data)
          if (message.type === 'SYNC_STATE' || message.type === 'STATE_CHANGED') {
            if (message.state) {
              setState(message.state as ScreenState)
            }
            if (message.stageState) {
              setStageState(message.stageState as LiveStageState)
            }
          } else if (message.type === 'FSM_STATE_UPDATE' && message.payload) {
            setState(message.payload as ScreenState)
          } else if (message.type === 'STAGE_STATE_CHANGED' && message.stageState) {
            setStageState(message.stageState as LiveStageState)
          }
        } catch {
          // Ignora mensagens malformadas
        }
      }

      websocketRef.current.onclose = () => {
        setIsConnected(false)
        setTimeout(connect, 3000)
      }

      websocketRef.current.onerror = () => {
        websocketRef.current?.close()
      }
    }

    connect()

    return () => {
      websocketRef.current?.close()
    }
  }, [])

  return { state, stageState, isConnected }
}
