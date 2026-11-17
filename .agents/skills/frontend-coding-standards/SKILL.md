---
name: frontend-coding-standards
description: Diretrizes arquiteturais para o frontend do LumenLite (React 19 + TypeScript): Fatias Verticais (Full-Stack Slices), Serviços HTTP com Ky + Zod v4, TanStack Query v5, External Store WebSocket via useSyncExternalStore, Headless Controllers, eliminação de God Components e Design System Slate Pro Studio.
---

# Diretrizes Arquiteturais de Frontend (React 19, TypeScript & LumenLite)

Este documento estabelece os padrões técnicos mandatórios para o desenvolvimento da interface e consumo de APIs no **LumenLite**.

---

## 🧭 Mapa de Diretrizes Especializadas

| O que você está desenvolvendo ou refatorando? | Referência Especializada |
|---|---|
| **Fatias Verticais (`slices/<dominio>/ui`), Serviços HTTP, Queries e Anatomia de Pastas** | 👉 [`docs/new-holyrics/04-architecture-and-slices.md`](../../../docs/new-holyrics/04-architecture-and-slices.md) |
| **Componentes UI atômicos, Shadcn, Design System Slate Pro Studio e regras de estilização** | 👉 [`references/components-and-registries.md`](references/components-and-registries.md) |
| **Governança de estado assíncrono com TanStack Query v5, `queryOptions` e `signal`** | 👉 [`references/async-state-and-query.md`](references/async-state-and-query.md) |
| **Conformidade com React 19, hooks de sensores via `@siberiacancode/reactuse` e WebSockets** | 👉 [`references/react19-and-performance.md`](references/react19-and-performance.md) |
| **Fluxo limpo de JSX, early returns, animações com `motion/react`** | 👉 [`references/jsx-flow-and-motion.md`](references/jsx-flow-and-motion.md) |
| **Formulários reativos e validação estrita com Zod v4** | 👉 [`references/forms-and-validation.md`](references/forms-and-validation.md) |

---

## ⚡ Princípios Não-Negociáveis (Core Rules)

1. **Zero Chamadas `fetch()` Dispersas:**
   - Todo acesso à rede passa obrigatoriamente por serviços em `slices/<dominio>/ui/services/` consumindo o `httpClient` (`@ui/core/network/http-client`).
2. **Camada Obrigatória de Queries (TanStack Query v5):**
   - Requisições de leitura usam `queryOptions` tipadas e repassam `signal` para cancelamento instantâneo.
   - Separação estrita entre Queries (`*Query`) e Mutations (`*Mutation`).
3. **External Store para WebSocket (`useSyncExternalStore`):**
   - Proibido inicializar `new WebSocket()` dentro de `useEffect` em componentes visuais.
   - O transporte reside no `LumenWebSocketStore` desacoplado do React e consumido via `useScreenState()`, `useWsConnected()` e `useCommandDispatch()`.
4. **Proibição de God Components (< 150–200 linhas):**
   - Telas e diálogos devem atuar como orquestradores limpos, delegando lógica para Headless Controllers (`use<Dominio>Controller.ts`).
5. **Proibição de Barrel Files (`index.ts`) em UI:**
   - Componentes React devem ser importados diretamente pelo seu arquivo correspondente para evitar ciclos e bundle bloat.
6. **Design System Slate Pro Studio:**
   - Proibido o uso de tags HTML nativas (`<button>`, `<input>`) fora de `components/ui/`.
   - Utilizar as primitivas estilizadas em `@ui/components/ui/` (`Button`, `Input`, `Badge`, `Kbd`).
7. **Utilitários Canônicos: `@siberiacancode/reactuse`:**
   - Timers, atalhos de teclado e sensores devem utilizar as primitivas de `@siberiacancode/reactuse` (`useDebounceValue`, `useEventListener`, `useToggle`).
