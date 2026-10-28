import { ScreenState, INITIAL_SCREEN_STATE } from './screen-state'
import { ActionCommand } from './commands'
import { fsmReducer } from './fsm-reducer'
import { ReturnStack } from './return-stack'

export type StateListener = (state: ScreenState, previousState: ScreenState) => void

export class FsmEngine {
  private state: ScreenState
  private readonly returnStack: ReturnStack
  private listeners: Set<StateListener> = new Set()

  constructor(initialState: ScreenState = INITIAL_SCREEN_STATE) {
    this.state = initialState
    this.returnStack = new ReturnStack()
  }

  getState(): ScreenState {
    return this.state
  }

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
   */
  interrupt(action: ActionCommand): ScreenState {
    this.returnStack.push(this.state)
    return this.dispatch(action)
  }

  /**
   * Restaura o último estado empilhado (se houver).
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

  subscribe(listener: StateListener): () => void {
    this.listeners.add(listener)
    return () => this.listeners.delete(listener)
  }
}
