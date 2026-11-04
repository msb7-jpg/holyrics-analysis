/**
 * @module @ui/core/sync/ws-hooks
 * @description Hooks reativos do React 19 conectados ao store WebSocket via useSyncExternalStore.
 */

import { useSyncExternalStore, useCallback } from 'react'
import type { ActionCommand, ScreenState } from '@lumen/core'
import { wsStore } from './ws-client'

/**
 * Retorna o ScreenState atual da FSM de projeção sincronizado em tempo real.
 *
 * @returns Estado da projeção (background, foreground, pânico, modo).
 */
export function useScreenState(): ScreenState {
  return useSyncExternalStore(wsStore.subscribeState, wsStore.getSnapshot)
}

/**
 * Retorna se o cliente está ativamente conectado ao Daemon local.
 *
 * @returns Booleano indicando integridade da conexão WebSocket.
 */
export function useWsConnected(): boolean {
  return useSyncExternalStore(wsStore.subscribeConnection, wsStore.getConnectionSnapshot)
}

/**
 * Retorna a função de despacho de comandos tipados para a FSM.
 *
 * @returns Função dispatch(command).
 */
export function useCommandDispatch(): (command: ActionCommand) => void {
  return useCallback((command: ActionCommand) => {
    wsStore.dispatch(command)
  }, [])
}
