import { ScreenState, INITIAL_SCREEN_STATE } from './screen-state'
import { ActionCommand } from './commands'
import { fsmReducer } from './fsm-reducer'
import { ReturnStack } from './return-stack'

export type StateListener = (state: ScreenState, previousState: ScreenState) => void

/**
 * Coordenador central da Máquina de Estados da Projeção.
 * Orquestra o Reducer puro, a pilha de interrupções (LIFO) e notificações de ouvintes.
 */
export class FsmEngine {
  private state: ScreenState
  private readonly returnStack: ReturnStack
  private listeners: Set<StateListener> = new Set()

  /**
   * Inicializa a engine com um estado inicial opcional.
   *
   * @param initialState - Estado inicial da tela (padrão: INITIAL_SCREEN_STATE).
   */
  constructor(initialState: ScreenState = INITIAL_SCREEN_STATE) {
    this.state = initialState
    this.returnStack = new ReturnStack()
  }

  /**
   * Obtém o estado atual da projeção.
   *
   * @returns O snapshot imutável do estado atual.
   */
  getState(): ScreenState {
    return this.state
  }

  /**
   * Despacha um comando para a máquina de estados.
   *
   * @param action - Comando litúrgico a ser processado pelo reducer.
   * @returns O novo estado resultante da ação.
   */
  dispatch(action: ActionCommand): ScreenState {
    const prevState = this.state
    this.state = fsmReducer(this.state, action)

    if (this.state !== prevState) {
      for (const listener of this.listeners) {
        listener(this.state, prevState)
      }
    }

    return this.state
  }

  /**
   * Salva o estado atual na pilha de retorno antes de apresentar uma interrupção.
   *
   * @param action - Comando da interrupção litúrgica (ex: oração espontânea ou aviso).
   * @returns O novo estado após o despacho da interrupção.
   */
  interrupt(action: ActionCommand): ScreenState {
    this.returnStack.push(this.state)
    return this.dispatch(action)
  }

  /**
   * Restaura o último estado empilhado (se houver) ao concluir a interrupção.
   *
   * @returns O estado restaurado anterior à interrupção, ou o estado atual se não houver pilha.
   */
  returnFromInterrupt(): ScreenState {
    const previous = this.returnStack.pop()
    if (!previous) return this.state

    const prevState = this.state
    this.state = {
      ...previous,
      version: this.state.version + 1,
      timestamp: Date.now(),
    }

    for (const listener of this.listeners) {
      listener(this.state, prevState)
    }

    return this.state
  }

  /**
   * Inscreve um ouvinte para receber notificações a cada transição de estado.
   *
   * @param listener - Função chamada quando houver mudança de estado.
   * @returns Função para cancelar a inscrição do ouvinte.
   */
  subscribe(listener: StateListener): () => void {
    this.listeners.add(listener)
    return () => this.listeners.delete(listener)
  }
}
