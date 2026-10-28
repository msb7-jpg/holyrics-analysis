import React from 'react'
import { useWebSocketTransport } from './core/sync/useWebSocketTransport'
import { OperatorView } from './operator/OperatorView'

export function App() {
  const { state, connected, dispatch } = useWebSocketTransport()
  const path = window.location.pathname

  if (path === '/projector') {
    return (
      <div className="flex h-screen w-screen items-center justify-center bg-black text-white p-12 text-center">
        {state.panic.blackout ? null : (
          <div className="text-4xl font-bold leading-relaxed">
            {state.foreground.content.join('\n') || 'LumenLite Projector Ready'}
          </div>
        )}
      </div>
    )
  }

  // Rota padrão: Console do Operador
  return <OperatorView state={state} connected={connected} dispatch={dispatch} />
}
