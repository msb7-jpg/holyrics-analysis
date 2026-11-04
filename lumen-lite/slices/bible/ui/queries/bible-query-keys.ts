/**
 * @module @slices/bible/ui/queries/bible-query-keys
 * @description Fábrica de chaves de consulta para o motor bíblico no TanStack Query v5.
 */

export const bibleKeys = {
  all: ['bible'] as const,
  lookups: () => [...bibleKeys.all, 'lookup'] as const,
  lookup: (ref: string, version = 'ACF') => [...bibleKeys.lookups(), { ref, version }] as const,
  searches: () => [...bibleKeys.all, 'search'] as const,
  search: (query: string, limit = 25, version = 'ACF') =>
    [...bibleKeys.searches(), { query, limit, version }] as const,
  versions: () => [...bibleKeys.all, 'versions'] as const,
}
