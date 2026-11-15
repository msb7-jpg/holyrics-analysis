/**
 * @module @lumen/daemon/api/context
 * @description Definições tipadas de contexto, cabeçalhos de segurança CORS e utilitários de resposta HTTP.
 */

import type { FsmEngine } from '@lumen/core'
import type { SongRepository, SongHistoryRepository, VagalumeClient } from '@slices/songs'
import type { BibleRepository } from '@slices/bible'
import type { LiturgyRepository } from '@slices/liturgy/daemon/liturgy-repository'
import type { AutomationRepository, DagRunner, ObsClient } from '@slices/automation'
import type { KeymapRepository } from '@slices/keymap'
import type { MediaRepository } from '@slices/media'
import type { DisplayRepository } from '@slices/navigation'
import type { StageEngine } from '@slices/stage'
import type { RemoteAuthManager } from '@slices/remote'
import type { PptxRepository } from '@slices/pptx'

/**
 * Contexto compartilhado de dependências injetado nos roteadores da API.
 */
export interface ApiContext {
  fsm: FsmEngine
  songRepo: SongRepository
  songHistoryRepo?: SongHistoryRepository
  vagalumeClient: VagalumeClient
  bibleRepo: BibleRepository
  liturgyRepo: LiturgyRepository
  automationRepo: AutomationRepository
  dagRunner: DagRunner
  keymapRepo: KeymapRepository
  mediaRepo: MediaRepository
  displayRepo: DisplayRepository
  stageEngine?: StageEngine
  remoteAuth?: RemoteAuthManager
  pptxRepo?: PptxRepository
  obsClient?: ObsClient | null
}

/**
 * Cabeçalhos CORS padrão para comunicação com WebViews locais.
 */
export const CORS_HEADERS = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
  'Access-Control-Allow-Headers': 'Content-Type',
} as const

/**
 * Cria uma Response JSON formatada contendo os cabeçalhos de CORS.
 *
 * @param payload - Objeto a ser serializado em JSON.
 * @param statusCode - Código HTTP de status (padrão 200).
 * @returns Instância de Response pronta para despacho.
 */
export function jsonResponse(payload: unknown, statusCode = 200): Response {
  return Response.json(payload, {
    status: statusCode,
    headers: CORS_HEADERS,
  })
}
