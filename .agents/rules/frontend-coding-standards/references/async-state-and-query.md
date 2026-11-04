# Reference: Asynchronous State Governance with TanStack Query v5

This guide defines canonical patterns for data fetching, remote state synchronization, mutations, and race condition prevention with `@tanstack/react-query` v5.

---

## 1. Core Principles

1. **Strict Separation of Concerns:**
   - Read hooks are suffixed with `*Query` (e.g., `useSuggestionsCatalogQuery`).
   - Write/mutation hooks are suffixed with `*Mutation` (e.g., `useUpdateProviderMutation`).
   - Never mix queries and mutations within the same custom hook.
2. **Query Key Factories (Hierarchical and Type-Safe):**
   - Query keys must be immutable serializable arrays defined in centralized factories using `as const`:

```typescript
// features/chat/queries/chatQueryKeys.ts
export const chatKeys = {
  all: ['chat'] as const,
  threads: () => [...chatKeys.all, 'threads'] as const,
  threadDetail: (threadId: string) => [...chatKeys.threads(), threadId] as const,
  messages: (threadId: string) => [...chatKeys.threadDetail(threadId), 'messages'] as const,
}
```

---

## 2. Camada de Serviços e `queryOptions` Pattern (Type-Safe & Reusable)

Em vez de espalhar chamadas a `fetch()` em componentes ou hooks, toda comunicação com o daemon reside em um serviço dedicado que consome o wrapper `httpClient`:

```typescript
// slices/songs/ui/services/songs-service.ts
import { httpClient } from '@ui/core/network/http-client'
import { SongSchema, type Song } from '../../contracts/song-contracts'

export const songsService = {
  async getById(songId: string, signal?: AbortSignal): Promise<Song> {
    return httpClient.get(`/api/songs/${encodeURIComponent(songId)}`, {
      signal,
      schema: SongSchema,
    })
  },
}
```

E a composição reutilizável com `queryOptions`:

```typescript
// slices/songs/ui/queries/songs-queries.ts
import { queryOptions } from '@tanstack/react-query'
import { songsService } from '../services/songs-service'
import { songKeys } from './songs-query-keys'

export const songDetailQueryOptions = (songId: string) =>
  queryOptions({
    queryKey: songKeys.detail(songId),
    queryFn: ({ signal }) => songsService.getById(songId, signal),
    staleTime: 1000 * 60,
  })
```

Consuming in a component or hook:

```tsx
import { useQuery } from '@tanstack/react-query'
import { providerConfigQueryOptions } from './settingsQueries'

export function useProviderConfigQuery() {
  return useQuery(providerConfigQueryOptions())
}
```

---

## 3. Automatic Cancellation with `signal`

Always consume the `signal` provided in `QueryFunctionContext` and pass it down to `fetch`, `ky`, or `axios`:

```typescript
queryFn: async ({ signal }) => {
  const response = await fetch('/api/analytics/schema', { signal })
  if (!response.ok) {
    throw new Error('Failed to load Lakehouse metadata')
  }
  return response.json()
}
```

This guarantees that if the user switches tabs, navigates to another page, or types rapidly in a search field, obsolete requests are cancelled by the browser at the TCP/HTTP layer.

---

## 4. Mutations and Asynchronous Action Fencing

### 4.1 Fencing Against Race Conditions
- Every mutation action (submitting forms, starting streams, deleting threads) must implement immediate visual fencing:
  - Buttons disabled during execution: `disabled={mutation.isPending}`.
  - Loading indicators within buttons (`<Spinner />`).

### 4.2 The Premature Unmount Pitfall (`key={...}`)
- **Never use `key={dynamicRemoteState}`** on parent nodes that unmount before the feedback callback (`onSuccess`) or before toast completion.
- Keep component keys stable to preserve React's visual transition tree.

### 4.3 Intelligent Cache Invalidation
```typescript
import { useMutation, useQueryClient } from '@tanstack/react-query'
import { chatKeys } from './chatQueryKeys'

export function useDeleteThreadMutation() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (threadId: string) => deleteThreadApi(threadId),
    onSuccess: (_, deletedId) => {
      // Evict from cache and invalidate the thread list
      queryClient.invalidateQueries({ queryKey: chatKeys.threads() })
    },
  })
}
```

---

## 5. Official References and Further Reading

- [TanStack Query v5 React Overview](https://tanstack.com/query/latest/docs/framework/react/overview)
- [TanStack Query Options Guide](https://tanstack.com/query/latest/docs/framework/react/guides/query-options)
- [TkDodo's Practical React Query Blog (Canonical Reference for Query Keys and Mutations)](https://tkdodo.eu/blog/practical-react-query)
- [TkDodo: Effective React Query Keys](https://tkdodo.eu/blog/effective-react-query-keys)
- [TanStack Query Network Mode and AbortSignal](https://tanstack.com/query/latest/docs/framework/react/guides/query-cancellation)
