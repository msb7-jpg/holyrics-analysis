/**
 * @module @lumen/daemon/api/context
 * @description Definições tipadas de contexto, cabeçalhos de segurança CORS e utilitários de resposta HTTP.
 */

import type { FsmEngine } from '@lumen/core'
import type { SongRepository, VagalumeClient } from '@slices/songs'
import type { BibleRepository } from '@slices/bible'
import type { LiturgyRepository } from '@slices/liturgy/daemon/liturgy-repository'

/**
 * Contexto compartilhado de dependências injetado nos roteadores da API.
 */
export interface ApiContext {
  fsm: FsmEngine
  songRepo: SongRepository
  vagalumeClient: VagalumeClient
  bibleRepo: BibleRepository
  liturgyRepo: LiturgyRepository
}

/**
 * Cabeçalhos CORS padrão para comunicação com WebViews locais.
 */
export const CORS_HEADERS = {
  'Access-Control-Allow-Origin': '*',
  'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
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
