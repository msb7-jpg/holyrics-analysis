import type { ScreenState } from './screen-state'

/**
 * Pilha LIFO para salvar e restaurar o estado da projeção
 * durante interrupções litúrgicas rápidas (oração, leitura bíblica espontânea, aviso de emergência).
 */
export class ReturnStack {
  private stack: ScreenState[] = []
  private readonly maxSize: number

  /**
   * Cria uma nova instância da pilha de retorno litúrgico.
   *
   * @param maxSize - Limite máximo de estados armazenados na memória.
   */
  constructor(maxSize = 10) {
    this.maxSize = maxSize
  }

  /**
   * Salva um snapshot do estado atual no topo da pilha.
   *
   * @param state - Estado a ser salvo na pilha antes de uma interrupção.
   */
  push(state: ScreenState): void {
    if (this.stack.length >= this.maxSize) {
      this.stack.shift()
    }
    this.stack.push({ ...state })
  }

  /**
   * Remove e retorna o último estado salvo da pilha de interrupções.
   *
   * @returns O último estado salvo, ou `undefined` se a pilha estiver vazia.
   */
  pop(): ScreenState | undefined {
    return this.stack.pop()
  }

  /**
   * Consulta o estado no topo da pilha sem removê-lo.
   *
   * @returns O estado no topo, ou `undefined` se a pilha estiver vazia.
   */
  peek(): ScreenState | undefined {
    return this.stack[this.stack.length - 1]
  }

  /**
   * Quantidade de snapshots atualmente armazenados na pilha.
   *
   * @returns O número de elementos presentes na pilha.
   */
  get size(): number {
    return this.stack.length
  }

  /**
   * Limpa todos os estados salvos na pilha.
   */
  clear(): void {
    this.stack = []
  }
}
