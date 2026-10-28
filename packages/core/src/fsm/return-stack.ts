import type { ScreenState } from './screen-state'

/**
 * Pilha LIFO para salvar e restaurar o estado da projeção
 * durante interrupções litúrgicas rápidas (oração, leitura bíblica espontânea, aviso de emergência).
 */
export class ReturnStack {
  private stack: ScreenState[] = []
  private readonly maxSize: number

  constructor(maxSize = 10) {
    this.maxSize = maxSize
  }

  push(state: ScreenState): void {
    if (this.stack.length >= this.maxSize) {
      this.stack.shift()
    }
    // Cria cópia rasa com snapshot do estado
    this.stack.push({ ...state })
  }

  pop(): ScreenState | undefined {
    return this.stack.pop()
  }

  peek(): ScreenState | undefined {
    return this.stack[this.stack.length - 1]
  }

  get size(): number {
    return this.stack.length
  }

  clear(): void {
    this.stack = []
  }
}
