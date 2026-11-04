/**
 * @module @lumen/core
 * @description Núcleo Imutável da Máquina de Estados Finita (FSM) e Barramento de Eventos do LumenLite.
 *
 * ## Responsabilidades:
 * - Gerenciamento determinístico do estado completo da projeção (`ScreenState`: Background, Foreground, PanicMask e MasterMode).
 * - Reducer puro sem efeitos colaterais `fsmReducer(State, Action) => State`.
 * - Pilha LIFO `ReturnStack` para suporte a interrupções litúrgicas instantâneas (versículo ou aviso sobreposto com retorno ao ponto anterior).
 * - Coordenador `FsmEngine` e barramento de eventos desacoplado `LumenEventBus`.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Notificações de mudança de estado para subscribers (Daemon / WebSockets / UI).
 * - **Consome:** Comandos de ação tipados (`ActionCommand`) despachados pelo operador, atalhos de teclado ou rede.
 * - **Exporta para Aplicação:** Tipos de domínio, constantes de estado inicial e motor de FSM.
 */

export type {
  MasterMode,
  PanicMask,
  BackgroundLayer,
  ForegroundLayer,
  ScreenState,
  ActionCommand,
  StateListener,
} from './fsm'

export type {
  EventBusListener,
  StateTransitionEvent,
} from './events'

export {
  INITIAL_SCREEN_STATE,
  fsmReducer,
  ReturnStack,
  FsmEngine,
} from './fsm'

export {
  LumenEventBus,
  globalEventBus,
} from './events'

