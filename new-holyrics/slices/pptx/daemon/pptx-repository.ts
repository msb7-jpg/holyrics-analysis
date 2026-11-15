/**
 * @module @slices/pptx/daemon/pptx-repository
 * @description Repositório para armazenamento atômico e consulta de apresentações PPTX e PDF ingeridas (RF-21).
 */

import { existsSync, mkdirSync, readFileSync, renameSync, writeFileSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { z } from 'zod'
import { PptxPresentationSchema, type PptxPresentation } from '../contracts/pptx-contracts'

/**
 * Repositório de apresentações `.pptx` e `.pdf` com persistência em JSON atômico (sem mocks iniciais).
 */
export class PptxRepository {
  private readonly filePath: string

  /**
   * Inicializa o repositório apontando para o arquivo JSON de decks.
   *
   * @param customFilePath - Caminho customizado opcional (útil para testes).
   */
  constructor(customFilePath?: string) {
    this.filePath = customFilePath || resolve(process.cwd(), 'data/pptx-decks.json')
    this.ensureSeed()
  }

  /**
   * Lista todas as apresentações salvas.
   *
   * @returns Lista ordenada de `PptxPresentation`.
   */
  listAll(): PptxPresentation[] {
    try {
      if (!existsSync(this.filePath)) {
        return []
      }
      const rawContent = readFileSync(this.filePath, 'utf-8')
      const parsed = z.array(PptxPresentationSchema).safeParse(JSON.parse(rawContent))
      return parsed.success ? parsed.data : []
    } catch {
      return []
    }
  }

  /**
   * Busca uma apresentação pelo seu ID.
   *
   * @param id - Identificador único da apresentação.
   * @returns Apresentação encontrada ou null.
   */
  getById(id: string): PptxPresentation | null {
    const allDecks = this.listAll()
    return allDecks.find((deck) => deck.id === id) || null
  }

  /**
   * Salva ou atualiza uma apresentação no repositório.
   *
   * @param presentation - Objeto validado da apresentação.
   * @returns A apresentação salva.
   */
  savePresentation(presentation: PptxPresentation): PptxPresentation {
    const validated = PptxPresentationSchema.parse(presentation)
    const allDecks = this.listAll().filter((item) => item.id !== validated.id)
    const updatedList = [validated, ...allDecks]
    this.writeAtomic(updatedList)
    return validated
  }

  /**
   * Remove uma apresentação pelo seu ID.
   *
   * @param id - ID da apresentação a excluir.
   * @returns Lista atualizada de apresentações.
   */
  deletePresentation(id: string): PptxPresentation[] {
    const filtered = this.listAll().filter((item) => item.id !== id)
    this.writeAtomic(filtered)
    return filtered
  }

  private ensureSeed(): void {
    if (!existsSync(this.filePath)) {
      this.writeAtomic([])
    }
  }

  private writeAtomic(decks: PptxPresentation[]): void {
    const dir = dirname(this.filePath)
    if (!existsSync(dir)) {
      mkdirSync(dir, { recursive: true })
    }
    const tempPath = `${this.filePath}.tmp`
    writeFileSync(tempPath, JSON.stringify(decks, null, 2), 'utf-8')
    renameSync(tempPath, this.filePath)
  }
}
