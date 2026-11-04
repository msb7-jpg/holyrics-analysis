/**
 * @module @slices/keymap/daemon/keymap-repository
 * @description Repositório para persistência atômica e leitura do arquivo `keybindings.json`.
 *
 * ## Responsabilidades:
 * - Ler e validar a configuração declarativa de atalhos utilizando Zod v4 (`KeymapConfigSchema`).
 * - Gravação atômica em disco com escrita temporária (`.tmp`) e renomeação segura.
 * - Gerar arquivo padrão (`DEFAULT_KEYMAP_CONFIG`) caso ainda não exista.
 * - Restaurar atalhos de fábrica (`resetToDefaults`).
 *
 * ## Comunicação e Fronteiras:
 * - Consumido pelas rotas REST `/api/keymap/*` e pelo daemon.
 */

import { existsSync, readFileSync, renameSync, unlinkSync, writeFileSync } from 'node:fs'
import { join } from 'node:path'
import { createLogger } from '@lumen/core'
import {
  KeymapConfigSchema,
  DEFAULT_KEYMAP_CONFIG,
  type KeymapConfig,
} from '../contracts/keymap-contracts'

const logger = createLogger('keymap-repo')

/**
 * Repositório atômico de persistência para as configurações de atalhos de teclado.
 */
export class KeymapRepository {
  private filePath: string

  /**
   * Inicializa o repositório de atalhos no caminho informado ou padrão da raiz.
   *
   * @param customFilePath - Caminho opcional do arquivo `keybindings.json`.
   */
  constructor(customFilePath?: string) {
    this.filePath = customFilePath ?? join(process.cwd(), 'keybindings.json')
  }

  /**
   * Carrega as configurações de atalhos em disco validando via schema Zod v4.
   *
   * @returns Configuração completa de atalhos.
   */
  public load(): KeymapConfig {
    if (!existsSync(this.filePath)) {
      logger.info({ filePath: this.filePath }, 'Arquivo keybindings.json inexistente. Criando configuração padrão.')
      this.save(DEFAULT_KEYMAP_CONFIG)
      return DEFAULT_KEYMAP_CONFIG
    }

    try {
      const rawContent = readFileSync(this.filePath, 'utf-8')
      const parsedJson = JSON.parse(rawContent)
      return KeymapConfigSchema.parse(parsedJson)
    } catch (error) {
      logger.error({ error, filePath: this.filePath }, 'Falha ao ler keybindings.json. Retornando padrão seguro.')
      return DEFAULT_KEYMAP_CONFIG
    }
  }

  /**
   * Salva a configuração de atalhos de forma atômica no disco.
   *
   * @param config - Configuração completa de atalhos validada.
   */
  public save(config: KeymapConfig): void {
    const validated = KeymapConfigSchema.parse(config)
    const tempFilePath = `${this.filePath}.tmp`
    const serialized = JSON.stringify(validated, null, 2)

    try {
      writeFileSync(tempFilePath, serialized, 'utf-8')
      renameSync(tempFilePath, this.filePath)
      logger.info({ bindingsCount: validated.bindings.length }, 'keybindings.json salvo com sucesso')
    } catch (error) {
      if (existsSync(tempFilePath)) {
        unlinkSync(tempFilePath)
      }
      logger.error({ error }, 'Erro crítico ao gravar keybindings.json de forma atômica')
      throw error
    }
  }

  /**
   * Restaura os atalhos de teclado para os padrões de fábrica do LumenLite.
   *
   * @returns Configuração padrão restaurada e persistida.
   */
  public resetToDefaults(): KeymapConfig {
    this.save(DEFAULT_KEYMAP_CONFIG)
    return DEFAULT_KEYMAP_CONFIG
  }

  /**
   * Retorna o caminho do arquivo gerenciado.
   *
   * @returns Caminho do arquivo.
   */
  public getFilePath(): string {
    return this.filePath
  }
}
