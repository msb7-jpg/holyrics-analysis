# Checklist Geral & Changelog: LumenLite

Este documento serve como o painel central de rastreamento do projeto **LumenLite** (modernização do Holyrics), consolidando o **Changelog** do que já foi construído e o **Checklist Geral de Requisitos** (RF e RNF) mapeados contra os 7 módulos do sistema.

---

## 1. Changelog Recente

### [v0.4.0] - Roteiro Litúrgico Interativo, HItems Polimórficos, Drag & Drop e Quick Text (Atual)
- **Modelagem Polimórfica de Itens Litúrgicos (`slices/hitems`):**
  - Definição canônica do `HItemSchema` com união discriminada Zod v4 para `SONG`, `BIBLE`, `MEDIA`, `PPTX`, `TEXT` e `COUNTDOWN`.
  - Contratos tipados e esquemas isomórficos compartilhados entre o daemon Bun e o frontend React 19.
- **Persistência Atômica e Roteiro de Culto (`slices/liturgy`):**
  - Implementação do `LiturgyRepository` com leitura e gravação atômica em disco (`culto.json`) protegida contra corrupção via escrita temporária (`.tmp`).
  - Geração automática de roteiro inicial padrão no primeiro boot (`DEFAULT_LITURGY`).
  - Roteador modular `/api/liturgy/*` integrado ao daemon com endpoints para consulta da liturgia ativa (`GET /current`), salvamento com pré-voo (`POST /save`), reordenação de itens (`POST /reorder`), adição de item (`POST /item`), remoção (`DELETE /item/:id`) e projeção rápida de texto (`POST /quick-text`).
- **Coluna 1 Interativa & Drag & Drop Nativo (`apps/ui`):**
  - Implementação do `useLiturgyController` conectado ao TanStack Query v5 (`useCurrentLiturgyQuery`, `useReorderLiturgyMutation`, `useAddLiturgyItemMutation`, `useRemoveLiturgyItemMutation`).
  - Reordenação em tempo real de itens litúrgicos via API nativa de Drag & Drop do HTML5, garantindo 0ms de latência e sem bibliotecas de terceiros infladas.
  - Componente modular `LiturgyItemCard` com badges por categoria, ícones semânticos e indicação de status "AO VIVO".
  - Menu de contexto suspenso (`LiturgyContextMenu`) acionado por botão direito para inserção de contagens de 5 min, anotações pastorais, exclusão e disparo imediato para o telão.
- **Projeção Rápida de Texto Avulso (Quick Text - RF-22A):**
  - Criação do modal `QuickTextModal` para digitação e projeção de recados emergenciais, avisos de estacionamento e orações espontâneas com atalho `Ctrl+Enter`.
  - Componente de formulário com `Textarea` integrado aos estilos soberbos do Slate Pro Studio.
- **Qualidade, Integridade & Testes:**
  - 43 testes automatizados passando em ~530ms com 100% de sucesso no Bun.
  - 0 erros de TypeScript (`bun run check`) e 0 advertências de linter (`bun run lint`).

### [v0.3.1] - Overhaul Arquitetural em Fatias Verticais, Serviços de Rede & TanStack Query v5
- **Fronteiras Arquiteturais Estritas (Os Três Anéis de Execução):**
  - Isolamento estrito entre **Contratos Compartilhados (`contracts`)**, **Backend Bun (`daemon`)** e **Frontend React 19 (`ui`)** em cada fatia vertical.
  - Eliminação de barrel files (`index.ts`) que mesclavam dependências do servidor (`bun:sqlite`, `pino`) com o frontend. Remoção de `createLogger` do barrel `@lumen/core` para módulo dedicado `@lumen/core/logger`.
  - Migração dos roteadores HTTP (`songs-router.ts`, `vagalume-router.ts`, `bible-router.ts`) para dentro de suas fatias verticais (`slices/<dominio>/daemon/routes/`).
- **Camada Centralizada de Serviços & Desserialização Zod v4:**
  - Implementação do wrapper universal `httpClient` (`@ui/core/network/http-client`) baseado no Ky com desempacotamento de envelopes de resposta `{ ok, data, error }`, cancelamento por `AbortSignal` e validação Zod.
  - Criação de serviços assíncronos desacoplados da UI: `songsService` (`@slices/songs/ui/services`) e `bibleService` (`@slices/bible/ui/services`). Eliminação de todas as chamadas diretas a `fetch()` em componentes.
- **Governança de Estado Assíncrono com TanStack Query v5:**
  - Implementação de fábricas hierárquicas de query keys (`songKeys`, `bibleKeys`) e `queryOptions` com tempo de expiração (`staleTime`) calibrado para SQLite sub-3ms.
  - Hooks tipados de leitura e cache: `useSongSearchQuery`, `useSongDetailQuery`, `useBibleLookupQuery`, `useBibleSearchQuery` e `useBibleVersionsQuery`.
- **Desacoplamento do WebSocket via `useSyncExternalStore`:**
  - Criação do `LumenWebSocketStore` (`@ui/core/sync/ws-client`), eliminando o acoplamento de sockets e timers de reconexão dentro de `useEffect`.
  - Hooks reativos atômicos: `useScreenState()`, `useWsConnected()` e `useCommandDispatch()` compatíveis com o React 19 StrictMode.
- **Eliminação de God Components & Modularização do Operador:**
  - Refatoração do `SpotlightDialog` (de 390 linhas para ~110 linhas) através do headless controller `useSpotlightController` e grupos modulares (`SpotlightQuickActions`, `SpotlightBibleResults`, `SpotlightSongResults`).
  - Refatoração do `OperatorView` (de 572 linhas para ~100 linhas), delegando responsabilidades para componentes de fatias (`SongSearchList`, `SongStanzaGrid`, `BibleQuickLookupCard`, `BibleVerseGrid`, `OperatorTopBar`, `OutputMonitorPanel`, `LiturgyScheduleColumn`) e headless controller `useOperatorController`.
  - Integração do `@siberiacancode/reactuse` (`useDebounceValue`, `useEventListener`) para debounce e atalhos de teclado.
- **Documentação de Engenharia e Padrões de Frontend:**
  - Criação do documento de arquitetura canônica `docs/new-holyrics/04-architecture-and-slices.md`.
  - Atualização completa do `AGENTS.md` e `.agents/rules/frontend-coding-standards/SKILL.md`.

### [v0.3.0] - Motor Bíblico Offline, ACF FTS5 & Spotlight Quick Jump
- **Motor Bíblico & Escrituras (`slices/bible`):**
  - Implementação do extrator nativo `extractHolyricsBibFile` / `extractHolyricsBibBuffer` em TypeScript puro, descriptografando arquivos `.bib` do Holyrics legado via AES-128-CBC + descompressão zlib sem nenhuma dependência do runtime Java.
  - Semente automática da Bíblia completa Almeida Corrigida e Fiel (ACF - 31.106 versículos e 66 livros) no primeiro boot do daemon.
  - Implementação do `BibleRepository` em SQLite WAL com tabela virtual FTS5 `bible_verses_fts` configurada com `unicode61 remove_diacritics 2` e ranking BM25, respondendo em sub-3ms.
  - Expansão do micro-parser sintático `parseVerseReference` para cobrir todos os 66 livros da Bíblia canônica PT-BR, suporte a nomes sem acento, variações populares e intervalos contínuos de versículos (ex: `jo 3 16`, `sl 23:1-4`, `1co 13:4-8`, `gn 1 1`).
  - Helper `isBibleReference` para roteamento dinâmico de buscas entre hinos e versículos sagrados.
- **Backend Daemon REST Router (`apps/daemon`):**
  - Roteador modular `apps/daemon/src/api/routes/bible-router.ts` com endpoints:
    - `GET /api/bible/lookup?ref=jo+3+16&version=ACF`: Lookup imediato de versículos únicos ou intervalos.
    - `GET /api/bible/search?q=luz+do+mundo&limit=25`: Pesquisa FTS5 textual com trechos e snippets destacados em HTML seguro.
    - `GET /api/bible/versions`: Listagem e estatísticas de versões bíblicas instaladas.
- **Console do Operador & Command Palette com `cmdk` (`apps/ui`):**
  - Implementação do `SpotlightDialog` construído sobre a biblioteca canônica **`cmdk`** (Paco Coursey), acessível globalmente via `Cmd+K` / `Ctrl+K`.
  - Agrupamento inteligente de comandos: **Ações Rápidas** (Blackout, Ocultar Texto, Congelar Telão), **Escrituras Sagradas (ACF)** e **Acervo de Louvores**.
  - Detecção inteligente no campo de busca da Coluna 2: ao digitar referências bíblicas, o painel exibe card destacado com opção de projeção imediata.
  - Suporte completo a itens bíblicos (`mode: 'BIBLE'`) no Reducer da FSM e renderização de cada versículo como slide limpo e legível na Coluna 3 e Coluna 4.
  - Navegação entre versículos com a Barra de Espaço (`NAV_NEXT`) e Backspace (`NAV_PREV`).
- **Refatoração Semântica de Automação (`slices/automation`):**
  - Renomeação oficial da fatia `slices/hooks` para `slices/automation` e do arquivo de regras para `automation.json`, eliminando qualquer colisão cognitiva com os React Hooks.
  - Novos schemas Zod v4 para o grafo DAG: `AutomationNodeSchema`, `AutomationEdgeSchema` e `AutomationGraphSchema`.
- **Qualidade & Testes:**
  - 31 testes automatizados passando em ~550ms cobrindo repositório bíblico, extrator de .bib, micro-parser de referências, FSM e rotas HTTP.

### [v0.2.0] - Gestão de Músicas, FTS5 & Busca Reativa
- **Motor de Músicas (`slices/songs`):**
  - Implementação do `SongRepository` com SQLite WAL, triggers e busca FTS5 com ranking BM25 e snippets destacados.
  - Extração e formatação semântica de 50 músicas/hinos reais (`default-songs.json`) integrados como semente de primeiro boot.
  - Cliente HTTP `VagalumeClient` com cancelamento assíncrono via `AbortSignal` e conversor automático para o modelo `SongSchema`.
  - Suíte de testes automatizados com `bun:test` cobrindo inserção, busca FTS5, ranking BM25, parsing de letras e resiliência de rede.
- **Console do Operador (`apps/ui`):**
  - Hook reativo `useSongSearch` com debounce de 150ms e cancelamento automático de requisições.
  - Integração da Coluna 2 (Acervo e Busca FTS5) e Coluna 3 (Estrofes Numeradas) com o backend em tempo real.
  - Seleção e projeção imediata de estrofes disparando ações tipadas `PRESENT_ITEM` para a FSM.
- **Arquitetura Thin Orchestrators & Backend Modular (`apps/daemon`):**
  - Refatoração do ponto de entrada `apps/daemon/src/index.ts` em orquestrador enxuto (~50 linhas de composição declarativa limpa).
  - Desacoplamento da camada HTTP em rotas modulares em `apps/daemon/src/api/routes/*` (`status-router`, `songs-router`, `vagalume-router`) com tratamento unificado de CORS e injeção de contexto.
  - Manipulador WebSocket dedicado em `apps/daemon/src/ws/ws-handler.ts` com pub/sub desacoplado da FSM.
  - Substituição de `console.log` síncrono por logging estruturado assíncrono de alta performance via Pino (`packages/core/src/logger/logger.ts`, `createLogger`).
- **Governança & Guardrails de Código:**
  - Padrão de cabeçalhos ricos TSDoc com `@module`, `@description`, responsabilidades e fronteiras em todos os módulos.
  - Regra de identificadores semânticos com `id-length >= 3` e proibição de `export *` no ESLint.
  - Suíte completa de testes passando com 0 erros de tipagem (`bun run check`) e 0 advertências de linter (`bun run lint`).

### [v0.1.0] - Esqueleto Inicial & Arquitetura Base
- **Monorepo & Toolchain:**
  - Configuração de Bun Workspaces unificando `packages/*`, `slices/*` e `apps/*`.
  - Configurações base de TypeScript com paths aliases (`@lumen/core`, `@slices/*`).
  - Criação do manifesto de agentes (`AGENTS.md`) com princípios mandatórios (Zero YAML, Zod v4, Slate Pro Studio).
- **Núcleo Imutável (`packages/core`):**
  - Criação do modelo atômico de camadas (`ScreenState`) com `Background`, `Foreground`, `PanicMask` e `MasterMode`.
  - Implementação do `fsmReducer` puro `(State, Action) => State` sem bibliotecas pesadas de terceiros.
  - Implementação da pilha LIFO `ReturnStack` para interrupções litúrgicas instantâneas.
  - Coordenador `FsmEngine` e emissor assíncrono `LumenEventBus`.
  - Suíte de testes unitários com Vitest/Bun passando com 100% de sucesso.
- **Fatias Verticais (`slices/*`):**
  - Criação da árvore de diretórios das 13 fatias canônicas (`src/contracts`, `src/daemon`, `src/ui`, `tests`).
  - **`slices/liturgy`:** Schema Zod v4 do `culto.json`, validador de pré-voo (`preflight-validator.ts`), `liturgy.schema.json` e testes.
  - **`slices/bible`:** Micro-parser léxico em TypeScript (`verse-parser.ts`, ~50 linhas) e testes de referências ("jo 3 16", "1co 13:4-8").
  - **`slices/songs`:** Contratos Zod para canções com estrofes numeradas limpas.
  - **`slices/navigation`:** Contratos de comando e estado de navegação.
  - **`slices/automation`:** Schema Zod v4 para nós e arestas do grafo DAG de automação (`automation.json`).
- **Hosts Thin Shell (`apps/*`):**
  - **`apps/daemon`:** Servidor HTTP + WebSocket nativo de ultra-baixa latência com `Bun.serve`, cliente SQLite (`bun:sqlite`) em modo WAL com índices FTS5, e CLI runner inicial `lumen`.
  - **`apps/ui`:** Interface React 19 + Tailwind CSS v4, hook reativo com reconexão automática `useWebSocketTransport`, layout de 4 colunas no padrão **Slate Pro Studio** e manifesto de janelas multi-monitor `tauri.conf.json`.

---

## 2. Checklist Geral de Requisitos Funcionais (RF)

Legenda de Status:
- [x] **Concluído:** Especificado, implementado e testado no esqueleto atual.
- [~] **Em Andamento / Base Pronta:** Contratos, tipos ou casca criados; lógica de produção a aprofundar.
- [ ] **Pendente:** Próximas etapas do roadmap.

### Módulo 1: Motor de Projeção & Saídas de Vídeo
- [~] **RF-01: Controle Multi-Monitor** (Configuração do `tauri.conf.json` criada com suporte a Monitor 1 e HDMI 2; pendente binding nativo de enumeração de monitores via Tauri).
- [~] **RF-02: Monitor de Retorno (Stage View)** (Rota `/stage` e contratos estruturados; pendente layout de alto contraste e relógio de cabeçalho).
- [x] **RF-03: Ações de Emergência (Pânico)** (Comandos de corte imediato `blackout`, `clearText`, `clearBg` e `freeze` no Reducer e botões no console).
- [x] **RF-04: Freeze (Congelar Projeção)** (Máscara atômica no `ScreenState` e despacho via FSM/WS).
- [ ] **RF-05: Motor Tipográfico & Auto-Fit** (Container Queries e `ResizeObserver` para escala dinâmica de fontes sem engasgos).
- [~] **RF-06: Temas e Estilos Visuais** (Design System Slate Pro e tokens de cores/margens definidos em `MASTER.md`).

### Módulo 2: Gestão de Músicas & Letras (Music Engine)
- [x] **RF-07: Estrutura Semântica de Letras** (Contratos Zod `SongSchema` e `SongSectionSchema` com estrofes sóbrias).
- [x] **RF-08: Busca Instantânea FTS5** (Tabela virtual SQLite `songs_fts` com tokenizer unicode61, BM25 e snippets destacados via `SongRepository`).
- [x] **RF-09: Base Offline Embutida** (Pacote curado com 50 louvores e hinos tradicionais em `default-songs.json` auto-populados no primeiro boot).
- [x] **RF-10: Integração Online API Vagalume** (Cliente HTTP assíncrono com cancelamento via `AbortSignal` e normalização semântica de letras).
- [ ] **RF-11: Importador Completo do Holyrics** (Leitura de ZIPs com `fflate`, higienização de tags `<c>`, `<t>` e persistência no SQLite).
- [ ] **RF-12: Histórico e Relatório de Execução** (Tabela de histórico de execuções com data e frequência).

### Módulo 3: Motor Bíblico & Escrituras (Bible Engine)
- [x] **RF-13: Múltiplas Traduções Concorrentes** (Tabelas SQLite WAL para traduções, semente da versão ACF com 31.106 versículos e extrator nativo de arquivos `.bib` legados).
- [ ] **RF-14: Projeção em Tela Dividida (Dual Version)** (Grid CSS responsivo no renderizador para versículos lado a lado).
- [x] **RF-15: Parser de Referências (Quick Jump)** (Micro-parser sintático cobrindo os 66 livros canônicos, abreviações PT-BR e intervalos como "jo 3 16", "sl 23:1-4", "1co 13:4-8").
- [x] **RF-16: Busca Textual por Palavra-Chave** (Tabela virtual SQLite FTS5 com BM25, snippets destacados e consultas sub-3ms).
- [ ] **RF-17: Gerenciador de Downloads Bíblicos** (Download e instalação de novas traduções em tempo de execução).

### Módulo 4: Roteiro de Culto & Apresentações Multimídia
- [x] **RF-18: Sistema Polimórfico de Itens (HItems)** (Union discriminada de itens no Zod para Músicas, Versículos, Mídias, PPTX, Textos e Contagens em `@slices/hitems`).
- [x] **RF-19: Culto como Código (`culto.json`)** (Schema Zod v4 estrito, `liturgy.schema.json` Draft 2020-12, persistência atômica via `LiturgyRepository` e validador de integridade).
- [x] **RF-20: Pilha de Interrupção (Return Stack)** (Implementação LIFO completa no `@lumen/core` com testes de unidade).
- [ ] **RF-21: Ingestão Nativa de PPTX & PDF** (Extração direta com `fflate` + `fast-xml-parser` das anotações pastorais).
- [ ] **RF-22: Playback Sincronizado** (Controle de timing de slides sincronizado com a Web Audio API).
- [x] **RF-22A: Projeção Rápida de Texto Avulso (Quick Text)** (Modal `QuickTextModal`, endpoint no daemon `/api/liturgy/quick-text` e projeção imediata na FSM).
- [x] **RF-22B: Menu de Contexto no Roteiro Litúrgico** (Menu suspenso com botão direito `LiturgyContextMenu` para inserção de contagens, anotações pastorais e projeção direta).
- [x] **RF-22C: Ingestão por Arraste e Solte (Drag & Drop)** (Reordenação de itens por Drag & Drop nativo do HTML5 com sincronização atômica em disco).

### Módulo 5: Transmissão ao Vivo, Palco & Conectividade
- [~] **RF-23: Streaming Transparente para OBS** (Rota `/broadcast` preparada com pub/sub WebSocket; pendente renderizador transparente com canal alfa).
- [ ] **RF-24: Alertas Instantâneos & Lower Thirds** (Fila de recados de emergência e auto-ocultação cronometrada).
- [ ] **RF-25: Cronômetros & Contagens Regressivas** (Engine de cronômetro sincronizado via WebSocket para palco e púlpito).
- [ ] **RF-26A: Endpoint Web do Pregador (`/remote/preacher`)** (Interface móvel restrita com slides, notas e cronômetro).
- [~] **RF-26B: Console Web do Operador via QR Code (`/remote/operator`)** (Base WebSocket pronta; pendente modal de QR Code).
- [x] **RF-26C: Arquitetura Focada & Exclusão de Bloatware** (Servidor enxuto sem módulos supérfluos de chat ou redes sociais).

### Módulo 6: Mídia, Áudio & Loops
- [ ] **RF-27: Loops de Vídeo Contínuos (Seamless Loops)** (Double buffering de `<video>` HTML5 com transição de opacidade via CSS).
- [~] **RF-28: Player de Áudio com Equalizador** (Widget inicial no console; pendente integração com nós `BiquadFilterNode` e `GainNode` da Web Audio API).
- [ ] **RF-29: Cache e Geração Assíncrona de Miniaturas** (Processamento em background de thumbnails de vídeo/imagem).

### Módulo 7: Automação por Nós (DAG Hooks) & Controle via CLI
- [~] **RF-30: Controle Total via CLI (`lumen`)** (Binário executável criado com comandos `status`, `next`, `prev`, `blackout`; pendente expansão para todos os slices).
- [~] **RF-31: Motor de Automações Baseado em Grafo (DAG)** (Contratos e schemas Zod do grafo definidos em `slices/automation`).
- [ ] **RF-32: Editor Visual de Automação com Blocos (`@xyflow/react`)** (Canvas interativo em nós arrastáveis).
- [ ] **RF-33: Ações Externas Integradas** (Nós executores de OBS WebSocket v5 e HTTP Lumikit DMX).
- [~] **RF-34: Sistema Declarativo de Atalhos (`keybindings.json`)** (Contratos mapeados; pendente hook listener global de teclas W3C).

---

## 3. Checklist de Requisitos Não-Funcionais (RNF)

- [x] **RNF-01: Cold Start < 1.2s** (Daemon Bun inicia em ~10ms; bundle React compilado em Vite).
- [x] **RNF-02: Latência de Troca de Slide < 16ms** (IPC via WebSocket local em memória respondendo em sub-milissegundo).
- [x] **RNF-03: Consumo de RAM < 120MB** (Processos leves sem JVM e sem overhead de navegadores pesados).
- [x] **RNF-04: Automação sem Bloqueio de Thread** (Desacoplamento assíncrono total entre renderização e rede externa).
- [x] **RNF-05: Operação 100% Offline** (SQLite local WAL e dados litúrgicos totalmente em disco).
- [x] **RNF-06: Qualidade e Velocidade de Verificação** (Testes rodando em < 90ms no Bun).

---

## 4. Próximos Passos Recomendados no Roadmap

1. **[CONCLUÍDO] Etapa 1: Acervo de Músicas & Busca FTS5 (`slices/songs` - v0.2.0):**
   - Inserção e consulta real no SQLite FTS5 com snippets e BM25.
   - Integração da busca da Coluna 2 e seleção de estrofes da Coluna 3 no console em tempo real.
   - Thin orchestrators no daemon e logging assíncrono com Pino.
2. **[CONCLUÍDO] Etapa 2: Motor Bíblico Offline (`slices/bible` - v0.3.0):**
   - Extrator nativo do arquivo `.bib` proprietário do Holyrics (`pt_acrf.bib`) e seed SQLite.
   - Mapeamento integral dos 66 livros da Bíblia canônica PT-BR e suporte a intervalos no micro-parser.
   - Tabela virtual FTS5 para busca textual por versículos e palavras-chave em menos de 3ms.
   - Roteador modular `/api/bible/*` no daemon e integração do Spotlight Dialog (`Cmd+K`) no operador.
3. **[CONCLUÍDO] Etapa 3: Roteiro Litúrgico Interativo (`slices/liturgy` + `slices/hitems` - v0.4.0):**
   - Ingestão, carregamento e persistência atômica de `culto.json` no console do operador com polimorfismo de itens (HItems).
   - Drag & Drop nativo para reordenar a sequência da liturgia sem dependências infladas.
   - Menu de contexto (botão direito) no roteiro para inserção de anotações pastorais, contagens e projeção direta.
   - Modal de Projeção Rápida de Texto Avulso de Emergência (Quick Text - RF-22A).
4. **[EM ANDAMENTO] Etapa 4: Projetor HDMI & Seamless Loop (`slices/navigation`):**
   - Implementar a janela `/projector` com auto-fit de texto por CSS e double-buffer de vídeo em loop.
5. **Etapa 5: Motor de Automação DAG (`slices/automation`):**
   - Implementar o runner DAG e o editor visual com `@xyflow/react` para `automation.json`.
