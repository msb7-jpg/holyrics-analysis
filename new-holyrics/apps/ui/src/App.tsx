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

/**
 * Componente raiz da aplicação cliente React 19.
 * Gerencia o roteamento entre o console do operador, projetor HDMI (`/projector`),
 * monitor de palco (`/stage`), púlpito móvel (`/remote/preacher`) e operador volante (`/remote/operator`).
 *
 * @returns Elemento JSX da rota ativa.
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
    return <PreacherRemoteView state={state} stageState={stageState} dispatch={dispatch} />
  }

  if (path === '/remote/operator') {
    return <OperatorRemoteView state={state} dispatch={dispatch} />
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
