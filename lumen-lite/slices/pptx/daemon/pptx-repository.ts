/**
 * @module @slices/pptx/daemon/pptx-repository
 * @description Repositório para armazenamento atômico e consulta de apresentações PPTX e PDF ingeridas (RF-21).
 */

import { existsSync, mkdirSync, readFileSync, renameSync, writeFileSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import { z } from 'zod'
import { PptxPresentationSchema, type PptxPresentation } from '../contracts/pptx-contracts'

const DEFAULT_SERMON_DECK: PptxPresentation = {
  id: 'pptx-default-sermon',
  title: 'Mensagem Pastoral: Fé Inabalável',
  filename: 'fe-inabalavel.pptx',
  format: 'PPTX',
  createdAt: Date.now(),
  slides: [
    {
      index: 0,
      title: 'Fé Inabalável em Tempos de Tempestade',
      textLines: ['Fé Inabalável', 'Hebreus 11:1 — O fundamento da nossa esperança'],
      speakerNotes:
        'Cumprimentar a igreja e introduzir o contexto da carta aos Hebreus. Lembrar do testemunho missionário.',
    },
    {
      index: 1,
      title: '1. O Olhar Fixo no Autor da Fé',
      textLines: ['1. O Olhar Fixo no Autor da Fé', 'Olhando firmemente para Jesus (Hebreus 12:2)'],
      speakerNotes:
        'Enfatizar que Pedro afundou apenas quando tirou os olhos de Cristo para olhar para o vento.',
    },
    {
      index: 2,
      title: '2. A Perseverança que Produz Fruto',
      textLines: ['2. A Perseverança que Produz Fruto', 'A prova da vossa fé opera a paciência (Tiago 1:3)'],
      speakerNotes:
        'Chamar a equipe de louvor ao palco neste último slide para transição suave.',
    },
  ],
}

/**
 * Repositório de apresentações `.pptx` e `.pdf` com persistência em JSON atômico.
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
        return [DEFAULT_SERMON_DECK]
      }
      const rawContent = readFileSync(this.filePath, 'utf-8')
      const parsed = z.array(PptxPresentationSchema).safeParse(JSON.parse(rawContent))
      return parsed.success ? parsed.data : [DEFAULT_SERMON_DECK]
    } catch {
      return [DEFAULT_SERMON_DECK]
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
      this.writeAtomic([DEFAULT_SERMON_DECK])
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
