/**
 * @module @slices/liturgy/daemon/liturgy-repository
 * @description Repositório para persistência, leitura e validação atômica do arquivo de culto (`culto.json`).
 *
 * ## Responsabilidades:
 * - Carregar o roteiro litúrgico atual com fallback seguro para um template padrão de culto.
 * - Gravação atômica em disco com escrita temporária para evitar corrupção de arquivos.
 * - Validação estrita via Zod v4 antes de qualquer operação de gravação.
 * - Reordenação rápida de itens e inserção de novos itens polimórficos.
 */

import { existsSync, readFileSync, writeFileSync, renameSync } from 'node:fs'
import { resolve } from 'node:path'
import { createLogger } from '@lumen/core/logger'
import {
  LiturgyFileSchema,
  type LiturgyFile,
  type LiturgyItem,
} from '../contracts/liturgy-schema'

const logger = createLogger('liturgy-repo')

/**
 * Liturgia padrão gerada automaticamente caso o arquivo não exista no primeiro boot.
 */
const DEFAULT_LITURGY: LiturgyFile = {
  $schema: './liturgy.schema.json',
  id: 'culto-domingo',
  title: 'Culto de Celebração & Adoração',
  date: new Date().toISOString().split('T')[0],
  theme: {
    fontFamily: 'Inter',
    textColor: '#FFFFFF',
  },
  items: [
    {
      id: 'item-countdown-1',
      type: 'COUNTDOWN',
      title: 'Contagem Regressiva 5min',
      targetDurationSeconds: 300,
      description: 'Pré-culto e recepção',
    },
    {
      id: 'item-song-1',
      type: 'SONG',
      title: 'Porque Ele Vive',
      songId: 'song-1042',
      artist: 'Harpa Cristã',
    },
    {
      id: 'item-bible-1',
      type: 'BIBLE',
      title: 'Leitura Bíblica: João 3:16',
      reference: 'jo 3 16',
      version: 'ACF',
    },
    {
      id: 'item-text-1',
      type: 'TEXT',
      title: 'Avisos & Boas-Vindas',
      content: [
        'Sejam todos bem-vindos à nossa celebração!',
        'Reunião de oração às quartas-feiras às 19:30.',
        'Escola Bíblica Dominical todos os domingos às 09:00.',
      ],
      category: 'ANNOUNCEMENT',
    },
  ],
  hooksEnabled: true,
}

/**
 * Repositório de acesso e manipulação atômica do roteiro de culto (`culto.json`).
 */
export class LiturgyRepository {
  private filePath: string

  /**
   * Inicializa o repositório com o caminho do arquivo `culto.json`.
   *
   * @param customFilePath - Caminho customizado para o arquivo (opcional).
   */
  constructor(customFilePath?: string) {
    this.filePath = customFilePath || resolve(process.cwd(), 'culto.json')
  }

  /**
   * Obtém o caminho do arquivo de persistência atual.
   *
   * @returns Caminho absoluto ou relativo do culto.json.
   */
  getFilePath(): string {
    return this.filePath
  }

  /**
   * Carrega o roteiro litúrgico atual. Cria a liturgia inicial caso não exista.
   *
   * @returns Conteúdo parseado e validado de LiturgyFile.
   */
  getCurrentLiturgy(): LiturgyFile {
    if (!existsSync(this.filePath)) {
      logger.info({ filePath: this.filePath }, 'Arquivo de culto inexistente. Gerando liturgia padrão.')
      this.saveLiturgy(DEFAULT_LITURGY)
      return DEFAULT_LITURGY
    }

    try {
      const rawText = readFileSync(this.filePath, 'utf-8')
      const parsedJson = JSON.parse(rawText)
      const validation = LiturgyFileSchema.safeParse(parsedJson)

      if (!validation.success) {
        logger.error({ errors: validation.error.issues }, 'Estrutura do culto.json inválida. Retornando padrão.')
        return DEFAULT_LITURGY
      }

      return validation.data
    } catch (error: unknown) {
      logger.error({ err: error }, 'Erro ao ler culto.json. Retornando padrão.')
      return DEFAULT_LITURGY
    }
  }

  /**
   * Salva o roteiro litúrgico de forma atômica no disco.
   *
   * @param liturgy - Objeto LiturgyFile validado.
   * @returns Liturgia salva com sucesso.
   */
  saveLiturgy(liturgy: LiturgyFile): LiturgyFile {
    const validated = LiturgyFileSchema.parse(liturgy)
    const tempPath = `${this.filePath}.tmp.${Date.now()}`

    try {
      const jsonContent = JSON.stringify(validated, null, 2)
      writeFileSync(tempPath, jsonContent, 'utf-8')
      renameSync(tempPath, this.filePath)
      logger.info({ id: validated.id, itemsCount: validated.items.length }, 'culto.json salvo com sucesso')
      return validated
    } catch (error: unknown) {
      logger.error({ err: error }, 'Falha na escrita atômica do culto.json')
      throw error
    }
  }

  /**
   * Reordena os itens existentes do roteiro com base em uma nova lista de IDs.
   *
   * @param itemIds - Lista ordenada de identificadores de itens.
   * @returns Liturgia atualizada com os itens na nova ordem.
   */
  reorderItems(itemIds: string[]): LiturgyFile {
    const current = this.getCurrentLiturgy()
    const itemMap = new Map<string, LiturgyItem>()

    for (const item of current.items) {
      itemMap.set(item.id, item)
    }

    const reordered: LiturgyItem[] = []
    for (const itemId of itemIds) {
      const found = itemMap.get(itemId)
      if (found) {
        reordered.push(found)
        itemMap.delete(itemId)
      }
    }

    // Preserva quaisquer itens que não foram passados na lista por segurança
    for (const remaining of itemMap.values()) {
      reordered.push(remaining)
    }

    const updated: LiturgyFile = {
      ...current,
      items: reordered,
    }

    return this.saveLiturgy(updated)
  }

  /**
   * Adiciona um novo item polimórfico ao roteiro.
   *
   * @param item - Novo HItem a ser adicionado.
   * @param insertIndex - Posição de inserção (opcional; padrão ao final).
   * @returns Liturgia atualizada.
   */
  addItem(item: LiturgyItem, insertIndex?: number): LiturgyFile {
    const current = this.getCurrentLiturgy()
    const items = [...current.items]

    if (typeof insertIndex === 'number' && insertIndex >= 0 && insertIndex <= items.length) {
      items.splice(insertIndex, 0, item)
    } else {
      items.push(item)
    }

    return this.saveLiturgy({
      ...current,
      items,
    })
  }

  /**
   * Remove um item do roteiro pelo seu ID.
   *
   * @param itemId - Identificador do item a remover.
   * @returns Liturgia atualizada.
   */
  removeItem(itemId: string): LiturgyFile {
    const current = this.getCurrentLiturgy()
    const items = current.items.filter((item) => item.id !== itemId)

    return this.saveLiturgy({
      ...current,
      items,
    })
  }
}
