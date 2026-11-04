/**
 * @module @slices/songs/ui/queries/songs-queries
 * @description Definições de queryOptions e hooks TanStack Query v5 para leitura de músicas.
 */

import { queryOptions, useQuery } from '@tanstack/react-query'
import { songKeys } from './songs-query-keys'
import { songsService } from '../services/songs-service'

/**
 * Retorna as queryOptions para busca FTS5 de músicas.
 *
 * @param query - Termo de busca textual.
 * @param limit - Limite de resultados retornados.
 * @returns Configuração reutilizável do TanStack Query.
 */
export function songSearchQueryOptions(query: string, limit = 20) {
  return queryOptions({
    queryKey: songKeys.search(query, limit),
    queryFn: ({ signal }) => songsService.search(query, limit, signal),
    staleTime: 15 * 1000,
  })
}

/**
 * Hook para busca reativa de músicas com TanStack Query v5.
 *
 * @param query - Termo de pesquisa digitado pelo operador.
 * @param limit - Limite de músicas a retornar.
 * @param enabled - Se a busca está habilitada.
 * @returns Resultado da consulta useQuery.
 */
export function useSongSearchQuery(query: string, limit = 20, enabled = true) {
  return useQuery({
    ...songSearchQueryOptions(query, limit),
    enabled,
  })
}

/**
 * Retorna as queryOptions para obter os detalhes completos de uma música por ID.
 *
 * @param songId - Identificador único da música.
 * @returns Configuração reutilizável do TanStack Query.
 */
export function songDetailQueryOptions(songId: string | null | undefined) {
  return queryOptions({
    queryKey: songKeys.detail(songId || ''),
    queryFn: ({ signal }) => {
      if (!songId) throw new Error('ID da música não informado')
      return songsService.getById(songId, signal)
    },
    enabled: Boolean(songId),
    staleTime: 60 * 1000,
  })
}

/**
 * Hook para recuperar detalhes e estrofes de uma música específica.
 *
 * @param songId - Identificador da canção.
 * @returns Consulta da música ativa.
 */
export function useSongDetailQuery(songId: string | null | undefined) {
  return useQuery(songDetailQueryOptions(songId))
}
