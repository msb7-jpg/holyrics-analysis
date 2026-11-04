# 4. Arquitetura em Fatias Verticais (Full-Stack Vertical Slices)

Este documento estabelece o guia arquitetural definitivo para o **LumenLite**, detalhando a separação de fronteiras entre **Backend (`daemon`)**, **Contratos Compartilhados (`contracts`)** e **Frontend (`ui`)**, além da anatomia interna mandatória de cada fatia de negócio.

---

## 1. Os Três Anéis de Execução (Runtime Rings)

Para garantir que o código seja previsível, modular e imune a vazamento de dependências (como código de backend no bundle do navegador), cada fatia vertical (`slices/<dominio>/`) é estritamente dividida em três anéis de execução:

```
┌────────────────────────────────────────────────────────────────────────┐
│               1. SHARED / ISOMORPHIC (slices/*/contracts)              │
│    • Schemas Zod v4, Tipos TypeScript DTOs, Entidades de Domínio       │
│    • Zero APIs de Node/Bun (sem sqlite, fs), Zero React/DOM            │
└──────────────────────────────────┬─────────────────────────────────────┘
                                   │
         ┌─────────────────────────┴─────────────────────────┐
         ▼                                                   ▼
┌──────────────────────────────────┐ ┌───────────────────────────────────┐
│     2. BACKEND RUNTIME (daemon)  │ │      3. FRONTEND RUNTIME (ui)     │
│ • SQLite WAL + FTS5 Repositories │ │ • Services (Ky + Zod Parse)       │
│ • Bun native server (Bun.serve)  │ │ • Queries (TanStack Query v5)     │
│ • Roteadores HTTP da Fatia       │ │ • Headless Hooks & Controllers    │
│ • Extratores de arquivos legados │ │ • Componentes React 19 (<150 LOC) │
│ • CLI subcommands (`lumen ...`)  │ │ • Utilitários puros de formatação │
└──────────────────────────────────┘ └───────────────────────────────────┘
```

### Regras de Isolamento (Guardrails Estáticos):
1. **Frontend NUNCA importa do Backend:**
   Arquivos sob `apps/ui/**` e `slices/*/ui/**` são proibidos de importar `@slices/*/daemon/**`, `bun:sqlite` ou `@lumen/core/logger`.
2. **Backend NUNCA importa do Frontend:**
   Arquivos sob `apps/daemon/**` e `slices/*/daemon/**` são proibidos de importar `@slices/*/ui/**` ou `@ui/**`.
3. **Proibição de Barrel Files (`index.ts`) em UI:**
   Componentes e hooks React devem ser importados diretamente pelo seu arquivo de origem para evitar ciclos de importação (`circular dependencies`) e preservar o tree-shaking do Vite.

---

## 2. Anatomia Interna de uma Fatia Vertical (`slices/<dominio>/`)

Cada fatia de negócio segue a estrutura de pastas padronizada abaixo. Ao abrir qualquer arquivo, sua responsabilidade única deve ser óbvia:

```text
slices/<dominio>/
├── contracts/                  # Anel Isomórfico Compartilhado
│   └── <dominio>-contracts.ts  # Zod Schemas e DTOs (SongSchema, BibleVerseSchema)
│
├── daemon/                     # Anel Backend Bun
│   ├── routes/                 # Roteador HTTP modular da fatia (/api/<dominio>/*)
│   ├── <dominio>-repo.ts       # Acesso a dados no SQLite WAL e consultas FTS5
│   └── <dominio>-service.ts    # Lógica de negócio servidora e integrações
│
└── ui/                         # Anel Frontend React 19
    ├── services/               # Clientes HTTP assíncronos (httpClient + Zod unwrap)
    │   └── <dominio>-service.ts
    ├── queries/                # TanStack Query v5 (queryOptions e key factories)
    │   ├── <dominio>-query-keys.ts
    │   └── <dominio>-queries.ts
    ├── hooks/                  # Headless Controllers e sensores (@siberiacancode/reactuse)
    │   └── use<Dominio>Controller.ts
    ├── components/             # Componentes visuais JSX (< 150 linhas)
    │   ├── <Dominio>List.tsx
    │   └── <Dominio>Card.tsx
    └── utils/                  # Funções puras e síncronas de formatação (input -> output)
```

---

## 3. Serviços de Rede & Governança de Estado Assíncrono

### Fluxo Unidirecional de Dados:
```
Componente JSX ──► Controller Hook ──► useQuery(*Query) ──► Service ──► httpClient (Ky) ──► Daemon HTTP
```

1. **Zero `fetch()` espalhado:**
   Nenhum componente ou hook React faz chamadas diretas a `fetch()`. Toda comunicação passa obrigatoriamente pelo `httpClient` (`@ui/core/network/http-client`).
2. **Desserialização e Validação Zod:**
   Os serviços desempacotam o envelope `{ ok: true, data: T }` do daemon e validam o contrato com `schema.safeParse()` em tempo de execução.
3. **Cancelamento Automático com `signal`:**
   TanStack Query v5 injeta o `signal` do `QueryFunctionContext` no serviço, cancelando requisições obsoletas instantaneamente ao navegar ou digitar na busca.

---

## 4. Estado em Tempo Real: External Store Desacoplado

Para gerenciar o estado da projeção via WebSocket (`ScreenState`) sem sofrer com montagens duplas do React 19 `StrictMode` ou prop-drilling excessivo:

- **Store Puro (`LumenWebSocketStore` em `@ui/core/sync/ws-client`):**
  Uma classe TypeScript pura sem dependências do React que gerencia o ciclo de vida do socket, reconexão com backoff exponencial e despacho de comandos FSM.
- **Hooks Reativos (`@ui/core/sync/ws-hooks`):**
  Conectados via `useSyncExternalStore`:
  - `useScreenState()`: Retorna o snapshot atual do telão.
  - `useWsConnected()`: Retorna a integridade da conexão.
  - `useCommandDispatch()`: Retorna a função `dispatch(command)`.

---

## 5. Receita: "Como Criar uma Nova Fatia"

1. **Definir Contratos (`contracts/`):** Crie os schemas Zod v4 de entrada, saída e DTOs.
2. **Implementar Persistência & Rotas (`daemon/`):** Crie o repositório SQLite e o roteador HTTP. Registre a rota no `apps/daemon/src/api/router.ts`.
3. **Criar o Serviço HTTP (`ui/services/`):** Implemente as funções assíncronas consumindo `httpClient`.
4. **Criar Query Keys e Hooks (`ui/queries/`):** Defina `queryKeys` e `queryOptions` com TanStack Query v5.
5. **Construir Componentes e Controllers (`ui/components/` & `ui/hooks/`):** Crie componentes focados com menos de 150 linhas utilizando as primitivas do Design System (`@ui/components/ui/*`).
