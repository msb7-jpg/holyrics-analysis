import { useState, useEffect, useRef } from 'react'
import type { ScreenState } from '@lumen/core'
import { INITIAL_SCREEN_STATE } from '@lumen/core'

const WEBSOCKET_URL = 'ws://localhost:4040/ws?type=broadcast'

/**
 * Hook para conectar a uma fonte de estado da FSM via WebSocket.
 *
 * @returns Um objeto contendo o estado atualizado e o status da conexão.
 */
export function useBroadcastConnector() {
  const [state, setState] = useState<ScreenState>(INITIAL_SCREEN_STATE)
  const [isConnected, setIsConnected] = useState(false)
  const websocketRef = useRef<WebSocket | null>(null)

  useEffect(() => {
    function connect() {
      websocketRef.current = new WebSocket(WEBSOCKET_URL)

      websocketRef.current.onopen = () => {
        setIsConnected(true)
        console.log('Broadcast connected to LumenLite Daemon')
      }

      websocketRef.current.onmessage = (event) => {
        try {
          const message = JSON.parse(event.data)
          if (message.type === 'FSM_STATE_UPDATE') {
            setState(message.payload)
          }
        } catch (error) {
          console.error('Failed to parse WebSocket message', error)
        }
      }

      websocketRef.current.onclose = () => {
        setIsConnected(false)
        // Tenta reconectar após 3 segundos
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

  return { state, isConnected }
}
