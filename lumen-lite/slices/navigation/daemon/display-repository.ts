/**
 * @module @slices/navigation/daemon/display-repository
 * @description Repositório para persistência de configurações dos renderizadores de Broadcast, Stage e Remote.
 *
 * ## Responsabilidades:
 * - Carregar e salvar atomicamente as configurações de broadcast (ex: `broadcast-settings.json`).
 * - Fornecer os parâmetros de customização para os diferentes modos de exibição.
 */

import { existsSync, readFileSync, renameSync, unlinkSync, writeFileSync } from 'node:fs'
import { join } from 'node:path'
import { createLogger } from '@lumen/core'
import { BroadcastSettingsSchema, type BroadcastSettings } from '../contracts/broadcast-contracts'

const logger = createLogger('display-repo')

export const DEFAULT_BROADCAST_SETTINGS: BroadcastSettings = {
  lowerThirdStyle: 'classic',
  textAlignment: 'center',
  fontFamily: 'Inter',
  fontSize: 48,
  backgroundOpacity: 0.7,
}

/**
 * Repositório para configurações de exibição.
 */
export class DisplayRepository {
  private broadcastSettingsPath: string
  private broadcastSettings: BroadcastSettings

  /**
   * Inicializa o repositório de configurações de exibição.
   *
   * @param customConfigPath - Caminho opcional para o arquivo de configurações de broadcast.
   */
  constructor(customConfigPath?: string) {
    this.broadcastSettingsPath = customConfigPath ?? join(process.cwd(), 'broadcast-settings.json')
    this.broadcastSettings = this.loadBroadcastSettings()
  }

  /**
   * Carrega as configurações de broadcast do disco ou retorna os padrões.
   *
   * @returns As configurações de broadcast carregadas ou padrão.
   */
  public loadBroadcastSettings(): BroadcastSettings {
    if (!existsSync(this.broadcastSettingsPath)) {
      return { ...DEFAULT_BROADCAST_SETTINGS }
    }

    try {
      const rawContent = readFileSync(this.broadcastSettingsPath, 'utf-8')
      const parsed = BroadcastSettingsSchema.parse(JSON.parse(rawContent))
      return parsed
    } catch (error) {
      logger.error({ error, path: this.broadcastSettingsPath }, 'Falha ao carregar broadcast-settings.json. Usando padrões.')
      return { ...DEFAULT_BROADCAST_SETTINGS }
    }
  }

  /**
   * Salva as configurações de broadcast de forma atômica no disco.
   *
   * @param settings - O objeto de configurações a ser salvo.
   */
  public saveBroadcastSettings(settings: BroadcastSettings): void {
    const validated = BroadcastSettingsSchema.parse(settings)
    const tempPath = `${this.broadcastSettingsPath}.tmp`

    try {
      writeFileSync(tempPath, JSON.stringify(validated, null, 2), 'utf-8')
      renameSync(tempPath, this.broadcastSettingsPath)
      this.broadcastSettings = validated
      logger.info({ path: this.broadcastSettingsPath }, 'broadcast-settings.json salvo com sucesso.')
    } catch (error) {
      if (existsSync(tempPath)) {
        unlinkSync(tempPath)
      }
      logger.error({ error }, 'Erro crítico ao gravar broadcast-settings.json')
      throw error
    }
  }

  /**
   * Retorna as configurações de broadcast atualmente em memória.
   *
   * @returns As configurações de broadcast.
   */
  public getBroadcastSettings(): BroadcastSettings {
    return this.broadcastSettings
  }
}
