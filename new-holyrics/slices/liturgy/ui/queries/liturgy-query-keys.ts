/**
 * @module @slices/liturgy/ui/queries/liturgy-query-keys
 * @description Fábrica de chaves hierárquicas de cache do TanStack Query para a fatia de liturgia.
 */

export const liturgyKeys = {
  all: ['liturgy'] as const,
  current: () => [...liturgyKeys.all, 'current'] as const,
}
