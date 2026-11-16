/**
 * @module @slices/liturgy/ui/queries/liturgy-queries
 * @description Hooks de consulta e mutação do TanStack Query v5 para o roteiro de culto.
 */

import { queryOptions, useQuery, useMutation, useQueryClient } from '@tanstack/react-query'
import { liturgyKeys } from './liturgy-query-keys'
import { liturgyService } from '../services/liturgy-service'
import type { LiturgyFile, LiturgyItem, QuickTextPayload } from '../../contracts/liturgy-schema'

/**
 * Cria as opções de consulta da liturgia ativa.
 *
 * @returns Configuração de queryOptions com cache de 5 minutos.
 */
export function currentLiturgyQueryOptions() {
  return queryOptions({
    queryKey: liturgyKeys.current(),
    queryFn: ({ signal }) => liturgyService.getCurrentLiturgy(signal),
    staleTime: 1000 * 60 * 5,
  })
}

/**
 * Hook para leitura reativa da liturgia ativa.
 *
 * @returns Resultado da query com os dados e status de carregamento.
 */
export function useCurrentLiturgyQuery() {
  return useQuery(currentLiturgyQueryOptions())
}

/**
 * Hook de mutação para atualizar o título do evento/culto ativo.
 *
 * @returns Mutation do TanStack Query.
 */
export function useUpdateLiturgyTitleMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (title: string) => liturgyService.updateTitle(title),
    onSuccess: (updatedLiturgy) => {
      queryClient.setQueryData(liturgyKeys.current(), updatedLiturgy)
    },
  })
}

/**
 * Hook de mutação para salvar/importar/resetar a liturgia completa.
 *
 * @returns Mutation do TanStack Query.
 */
export function useSaveLiturgyMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (liturgy: LiturgyFile) => liturgyService.saveLiturgy(liturgy),
    onSuccess: (savedLiturgy) => {
      queryClient.setQueryData(liturgyKeys.current(), savedLiturgy)
    },
  })
}

/**
 * Hook de mutação para reordenar itens da liturgia (Drag & Drop).
 *
 * @returns Mutation do TanStack Query que invalida e atualiza a liturgia em cache.
 */
export function useReorderLiturgyMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (itemIds: string[]) => liturgyService.reorderItems(itemIds),
    onSuccess: (updatedLiturgy) => {
      queryClient.setQueryData(liturgyKeys.current(), updatedLiturgy)
    },
  })
}

/**
 * Hook de mutação para adicionar um item polimórfico na liturgia.
 *
 * @returns Mutation do TanStack Query.
 */
export function useAddLiturgyItemMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: ({ item, insertIndex }: { item: LiturgyItem; insertIndex?: number }) =>
      liturgyService.addItem(item, insertIndex),
    onSuccess: (updatedLiturgy) => {
      queryClient.setQueryData(liturgyKeys.current(), updatedLiturgy)
    },
  })
}

/**
 * Hook de mutação para remover um item da liturgia.
 *
 * @returns Mutation do TanStack Query.
 */
export function useRemoveLiturgyItemMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (itemId: string) => liturgyService.removeItem(itemId),
    onSuccess: (updatedLiturgy) => {
      queryClient.setQueryData(liturgyKeys.current(), updatedLiturgy)
    },
  })
}

/**
 * Hook de mutação para disparo de aviso rápido (Quick Text).
 *
 * @returns Mutation para despachar o texto de emergência.
 */
export function useQuickTextMutation() {
  return useMutation({
    mutationFn: (payload: QuickTextPayload) => liturgyService.presentQuickText(payload),
  })
}
