/**
 * @module @slices/songs/ui/queries/songs-query-keys
 * @description Fábrica centralizada de chaves de consulta para TanStack Query v5 do módulo de músicas.
 */

export const songKeys = {
  all: ['songs'] as const,
  searches: () => [...songKeys.all, 'search'] as const,
  search: (query: string, limit?: number) => [...songKeys.searches(), { query, limit }] as const,
  details: () => [...songKeys.all, 'detail'] as const,
  detail: (id: string) => [...songKeys.details(), id] as const,
}
