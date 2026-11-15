/**
 * @module @slices/bible/ui/queries/bible-queries
 * @description Definições de queryOptions e hooks reativos para o motor bíblico.
 */

import { queryOptions, useQuery } from '@tanstack/react-query'
import { bibleKeys } from './bible-query-keys'
import { bibleService } from '../services/bible-service'

/**
 * Retorna as queryOptions para lookup de referências bíblicas (ex: "jo 3 16").
 *
 * @param reference - Referência digitada.
 * @param version - Versão da Bíblia (default: ACF).
 * @returns Configuração reutilizável do TanStack Query.
 */
export function bibleLookupQueryOptions(reference: string, version = 'ACF') {
  const cleanRef = reference.trim()
  return queryOptions({
    queryKey: bibleKeys.lookup(cleanRef, version),
    queryFn: ({ signal }) => bibleService.lookup(cleanRef, version, signal),
    enabled: Boolean(cleanRef.length >= 3),
    staleTime: 60 * 1000,
  })
}

/**
 * Hook para consulta pontual de referências bíblicas.
 *
 * @param reference - Texto digitado da referência.
 * @param version - Tradução bíblica.
 * @param enabled - Se a busca está habilitada.
 * @returns Resultado da consulta.
 */
export function useBibleLookupQuery(reference: string, version = 'ACF', enabled = true) {
  const cleanRef = reference.trim()
  return useQuery({
    ...bibleLookupQueryOptions(cleanRef, version),
    enabled: enabled && cleanRef.length >= 3,
  })
}

/**
 * Retorna as queryOptions para busca FTS5 textual no texto bíblico.
 *
 * @param query - Termo textual a buscar.
 * @param limit - Quantidade de versículos.
 * @param version - Tradução bíblica.
 * @returns Configuração da busca bíblica.
 */
export function bibleSearchQueryOptions(query: string, limit = 25, version = 'ACF') {
  const cleanQuery = query.trim()
  return queryOptions({
    queryKey: bibleKeys.search(cleanQuery, limit, version),
    queryFn: ({ signal }) => bibleService.search(cleanQuery, limit, version, signal),
    enabled: Boolean(cleanQuery.length >= 2),
    staleTime: 30 * 1000,
  })
}

/**
 * Hook para pesquisa textual de versículos por palavra-chave.
 *
 * @param query - Termo a buscar.
 * @param limit - Limite de versículos.
 * @param version - Versão bíblica.
 * @param enabled - Se a busca deve executar.
 * @returns Resultado da pesquisa FTS5.
 */
export function useBibleSearchQuery(query: string, limit = 25, version = 'ACF', enabled = true) {
  const cleanQuery = query.trim()
  return useQuery({
    ...bibleSearchQueryOptions(cleanQuery, limit, version),
    enabled: enabled && cleanQuery.length >= 2,
  })
}

/**
 * Retorna as queryOptions para listar traduções bíblicas instaladas.
 *
 * @returns Configuração da lista de versões.
 */
export function bibleVersionsQueryOptions() {
  return queryOptions({
    queryKey: bibleKeys.versions(),
    queryFn: ({ signal }) => bibleService.listVersions(signal),
    staleTime: 5 * 60 * 1000,
  })
}

/**
 * Hook para obter a lista de traduções disponíveis.
 *
 * @returns Consulta das versões bíblicas.
 */
export function useBibleVersionsQuery() {
  return useQuery(bibleVersionsQueryOptions())
}
