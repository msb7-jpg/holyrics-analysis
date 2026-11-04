import { QueryClient } from '@tanstack/react-query'

/**
 * Instância centralizada do TanStack QueryClient otimizada para o Daemon LumenLite local.
 * Como o banco de dados é SQLite local com latência sub-3ms, reduzimos re-fetchings desnecessários.
 */
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 30 * 1000, // 30 segundos
      gcTime: 5 * 60 * 1000, // 5 minutos em memória
      retry: 1,
      refetchOnWindowFocus: false,
    },
  },
})
