import { useState, useEffect, useCallback, type ReactNode } from 'react'
import { Lock, Loader2 } from 'lucide-react'
import type { ActionCommand } from '@lumen/core'
import {
  useScreenState,
  useStageState,
  useWsConnected,
  useCommandDispatch,
} from './core/sync/ws-hooks'
import { OperatorView } from './operator/OperatorView'
import { ProjectorView } from './projector/ProjectorView'
import { StageView } from '@slices/stage/ui/components/StageView'
import { PreacherRemoteView } from '@slices/remote/ui/components/PreacherRemoteView'
import { OperatorRemoteView } from '@slices/remote/ui/components/OperatorRemoteView'
import { remoteService } from '@slices/remote/ui/services/remote-service'
import type { RemoteRole } from '@slices/remote/contracts/remote-contracts'

function RemoteTokenGate({
  requiredRole,
  children,
}: {
  requiredRole: RemoteRole
  children: (dispatchAuthenticated: (cmd: ActionCommand) => void) => ReactNode
}) {
  const [token, setToken] = useState<string>(() => {
    const params = new URLSearchParams(window.location.search)
    return params.get('token') || ''
  })
  const [draftToken, setDraftToken] = useState('')
  const [status, setStatus] = useState<'checking' | 'authorized' | 'denied'>('checking')
  const [errorReason, setErrorReason] = useState<string>('Token de acesso obrigatório.')

  useEffect(() => {
    let cancelled = false
    if (!token.trim()) {
      setStatus('denied')
      setErrorReason('Token de acesso ausente. Escaneie o QR Code na cabine.')
      return
    }
    setStatus('checking')
    remoteService
      .verifyToken(token.trim())
      .then((res) => {
        if (cancelled) return
        if (!res.valid) {
          setStatus('denied')
          setErrorReason(res.reason || 'Token inválido ou controle remoto desativado.')
          return
        }
        if (requiredRole === 'operator' && res.role !== 'operator') {
          setStatus('denied')
          setErrorReason('Este token é restrito ao modo Pregador.')
          return
        }
        setStatus('authorized')
      })
      .catch(() => {
        if (!cancelled) {
          setStatus('denied')
          setErrorReason('Não foi possível validar o token com o servidor.')
        }
      })
    return () => {
      cancelled = true
    }
  }, [token, requiredRole])

  const dispatchAuthenticated = useCallback(
    (command: ActionCommand) => {
      void remoteService.dispatchRemoteCommand(token.trim(), command).catch(() => {
        // Comando rejeitado pelo servidor
      })
    },
    [token],
  )

  if (status === 'checking') {
    return (
      <div className="flex min-h-screen items-center justify-center bg-zinc-950 text-zinc-300">
        <Loader2 className="h-6 w-6 animate-spin text-zinc-400" />
      </div>
    )
  }

  if (status === 'denied') {
    return (
      <div className="flex min-h-screen items-center justify-center bg-zinc-950 p-6 text-zinc-100">
        <div className="w-full max-w-sm rounded-xl border border-zinc-800 bg-zinc-950 p-6 text-center shadow-2xl">
          <div className="mx-auto mb-3 flex h-12 w-12 items-center justify-center rounded-full bg-rose-500/15 text-rose-400">
            <Lock className="h-6 w-6" />
          </div>
          <h1 className="text-base font-semibold text-white">Acesso Restrito</h1>
          <p className="mt-1.5 text-xs text-zinc-400">{errorReason}</p>
          <form
            onSubmit={(event) => {
              event.preventDefault()
              if (draftToken.trim()) {
                setToken(draftToken.trim())
              }
            }}
            className="mt-4 flex flex-col gap-2"
          >
            <input
              type="text"
              value={draftToken}
              onChange={(event) => setDraftToken(event.target.value)}
              placeholder="Digite o token de acesso..."
              className="rounded-md border border-zinc-800 bg-zinc-900 px-3 py-2 text-xs text-white focus:outline-none focus:ring-1 focus:ring-zinc-600"
            />
            <button
              type="submit"
              className="rounded-md bg-sky-500 px-3 py-2 text-xs font-semibold text-zinc-950 hover:bg-sky-400 transition"
            >
              Validar Token
            </button>
          </form>
        </div>
      </div>
    )
  }

  return <>{children(dispatchAuthenticated)}</>
}

/**
 * Componente raiz da aplicação cliente React 19.
 */
export function App() {
  const state = useScreenState()
  const stageState = useStageState()
  const connected = useWsConnected()
  const dispatch = useCommandDispatch()
  const path = window.location.pathname

  if (path === '/projector') {
    return <ProjectorView state={state} stageState={stageState} dispatch={dispatch} />
  }

  if (path === '/stage') {
    return <StageView state={state} stageState={stageState} />
  }

  if (path === '/remote/preacher') {
    return (
      <RemoteTokenGate requiredRole="preacher">
        {(remoteDispatch) => (
          <PreacherRemoteView state={state} stageState={stageState} dispatch={remoteDispatch} />
        )}
      </RemoteTokenGate>
    )
  }

  if (path === '/remote/operator') {
    return (
      <RemoteTokenGate requiredRole="operator">
        {(remoteDispatch) => <OperatorRemoteView state={state} dispatch={remoteDispatch} />}
      </RemoteTokenGate>
    )
  }

  // Rota padrão: Console do Operador
  return (
    <OperatorView
      state={state}
      stageState={stageState}
      connected={connected}
      dispatch={dispatch}
    />
  )
}
