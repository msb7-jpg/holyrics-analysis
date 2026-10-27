# 5. Estrutura Completa de Diretórios (Full-Stack Vertical Codebase)

Abaixo está o mapa definitivo do monorepo, refletindo as **fatias verticais unificadas**, a ausência de prefixos numéricos, as cascas finas (`apps/`) e o ecossistema completo para suportar 100% das funcionalidades do Holyrics:

```text
lumen-lite/
├── package.json                        # Workspaces: ["apps/*", "packages/*", "slices/*"]
├── vite.config.ts                      # Configuração centralizada Vite+ (fmt, lint, run, staged)
├── tsconfig.json                       # Configuração base TypeScript
├── bun.lock                            # Lockfile determinístico do Bun
├── AGENTS.md                           # Instruções para agentes e IA
│
├── packages/
│   └── core/                           # NÚCLEO FSM (@lumen/core, empacotado via vp pack)
│       ├── package.json
│       ├── tsconfig.json
│       ├── vite.config.ts              # Configuração tsdown
│       ├── src/
│       │   ├── index.ts                # Exporta contratos da FSM e EventBus
│       │   ├── fsm/
│       │   │   ├── screen-state.ts     # ScreenState, MasterMode, BackgroundLayer, ForegroundLayer
│       │   │   ├── commands.ts         # Union types imutáveis de ActionCommand
│       │   │   ├── fsm-reducer.ts      # Reducer puro (State, Action) => State
│       │   │   ├── return-stack.ts     # Pilha LIFO de interrupções
│       │   │   ├── panic-ctrl.ts       # Máscaras de pânico (Blackout, Logo, ClearText, Freeze)
│       │   │   └── fsm-engine.ts       # Coordenador enxuto (< 120 linhas)
│       │   └── events/
│       │       └── event-bus.ts        # Emissor assíncrono de StateTransition
│       └── tests/
│           └── fsm.test.ts             # Testes de integridade da máquina de estados
│
├── slices/                             # ◄── TODAS AS FATIAS VERTICAIS FULL-STACK
│   │
│   ├── songs/                          # FATIA DE MÚSICAS, VAGALUME & FTS5
│   │   ├── package.json                # Subpath exports: ./contracts, ./daemon, ./ui
│   │   ├── tsconfig.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # Song, SongSection, SongArrangement DTOs
│   │   │   ├── daemon/                 # Backend Bun:
│   │   │   │   ├── song-repo.ts        # SQLite CRUD
│   │   │   │   ├── fts-search.ts       # Query builder BM25
│   │   │   │   ├── song-cli.ts         # Subcomando lumen song
│   │   │   │   ├── vagalume/           # Cliente Vagalume API:
│   │   │   │   │   ├── vagalume-client.ts
│   │   │   │   │   └── vagalume-mapper.ts
│   │   │   │   ├── song-history-repo.ts# Histórico de músicas cantadas
│   │   │   │   └── importer/           # Importador Holyrics:
│   │   │   │       ├── zip-reader.ts
│   │   │   │       ├── tag-cleaner.ts
│   │   │   │       └── batch-save.ts
│   │   │   └── ui/                     # Frontend React:
│   │   │       ├── queries.ts          # Hooks TanStack Query (useSongSearch, useSongDetail)
│   │   │       ├── SongSearchInput.tsx
│   │   │       ├── SongSearchResultsList.tsx
│   │   │       ├── SongArrangementPills.tsx
│   │   │       ├── VagalumeImportModal.tsx# Importador online em 1 clique
│   │   │       ├── SongHistoryDialog.tsx
│   │   │       ├── SlideThumbnailCard.tsx
│   │   │       └── SongQuickEditorModal.tsx
│   │   └── tests/
│   │       ├── songs-fts.test.ts
│   │       └── songs-importer.test.ts
│   │
│   ├── bible/                          # FATIA DE BÍBLIA OFFLINE & DUAL VERSION
│   │   ├── package.json
│   │   ├── tsconfig.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # VerseReference, BibleVersion DTOs
│   │   │   ├── daemon/
│   │   │   │   ├── verse-parser.ts     # Parser sintático rápido "jo 3 16-18"
│   │   │   │   ├── bible-repo.ts       # Consultas SQLite NVI, NAA, ARA, ARC, NVT
│   │   │   │   ├── bible-downloader.ts # Download online de novas traduções
│   │   │   │   └── bible-cli.ts        # Subcomando lumen bible
│   │   │   └── ui/
│   │   │       ├── queries.ts          # Hooks TanStack Query (useBibleSearch, useChapter)
│   │   │       ├── BibleSpotlightCommand.tsx
│   │   │       ├── VerseSelectorGrid.tsx
│   │   │       └── BibleDownloadModal.tsx
│   │   └── tests/
│   │       └── verse-parser.test.ts
│   │
│   ├── liturgy/                        # FATIA DE CULTO COMO CÓDIGO (JSON)
│   │   ├── package.json
│   │   ├── tsconfig.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/
│   │   │   │   ├── liturgy-schema.ts   # Validador Zod do culto.json
│   │   │   │   └── liturgy.schema.json # JSON Schema com suporte a $schema
│   │   │   ├── daemon/
│   │   │   │   ├── preflight-validator.ts # Checagem de integridade de disco e banco
│   │   │   │   ├── liturgy-loader.ts      # Carregamento e montagem da fila
│   │   │   │   └── liturgy-cli.ts         # lumen validate / lumen load
│   │   │   └── ui/
│   │   │       ├── queries.ts          # Hooks TanStack Query (useLiturgyPreflight)
│   │   │       ├── LiturgySidebar.tsx
│   │   │       ├── LiturgyItemRow.tsx
│   │   │       └── PreFlightStatusDialog.tsx
│   │   └── tests/
│   │       └── liturgy-validator.test.ts
│   │
│   ├── hitems/                         # FATIA DE ITENS POLIMÓRFICOS DE APRESENTAÇÃO
│   │   ├── package.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # HItemMusic, HItemVerse, HItemAnnouncement, HItemURL
│   │   │   ├── daemon/
│   │   │   │   └── hitem-resolver.ts   # Resolução de mídias e tipos
│   │   │   └── ui/
│   │   │       ├── HItemDispatcher.tsx # Despachante visual polimórfico no projetor
│   │   │       ├── AnnouncementSlide.tsx # Avisos com transição cronometrada
│   │   │       └── LiveUrlSlide.tsx    # Renderizador de páginas web
│   │   └── tests/
│   │       └── hitems.test.ts
│   │
│   ├── navigation/                     # FATIA DE TRANSPORTE, SLIDES, TELÃO & PÂNICO
│   │   ├── package.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # NavigationCommand, PanicType
│   │   │   ├── daemon/
│   │   │   │   ├── nav-service.ts      # Despacho para FSM
│   │   │   │   └── nav-cli.ts          # lumen next, prev, goto, panic, return, freeze
│   │   │   └── ui/
│   │   │       ├── PanicToolbar.tsx    # Botoes F1, F2, F3, F9, Freeze
│   │   │       ├── LivePreviewCard.tsx # Miniatura 16:9 do telão
│   │   │       └── projector/          # Micro-componentes da Janela HDMI:
│   │   │           ├── ProjectorViewport.tsx
│   │   │           ├── VideoDoubleBuffer.tsx # Seamless loop sem telas pretas
│   │   │           ├── AutoFitText.tsx       # Tipografia dinâmica com ResizeObserver
│   │   │           ├── ForegroundTextRenderer.tsx
│   │   │           ├── DualBibleSplitRenderer.tsx
│   │   │           ├── FreezeView.tsx
│   │   │           └── PanicCurtain.tsx
│   │   └── tests/
│   │       └── navigation.test.ts
│   │
│   ├── media/                          # FATIA DE MÍDIA, ÁUDIO AMBIENTE & EQUALIZADOR
│   │   ├── package.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # AudioTrack, EqualizerBand, MediaThumbnail
│   │   │   ├── daemon/
│   │   │   │   ├── thumb-cache.ts      # Cache assíncrono de miniaturas
│   │   │   │   └── media-cli.ts        # Subcomando lumen media
│   │   │   └── ui/
│   │   │       ├── audio/
│   │   │       │   ├── audioEngine.ts  # Web Audio API com crossfade e 10-band EQ
│   │   │       │   └── AudioPlayerDrawer.tsx
│   │   │       └── MediaBackgroundDrawer.tsx
│   │   └── tests/
│   │       └── audio-engine.test.ts
│   │
│   ├── playback/                       # FATIA DE PLAYBACK SINCRONIZADO
│   │   ├── package.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # PlaybackTrack, SlideTimestamp
│   │   │   ├── daemon/
│   │   │   │   ├── playback-sync.ts    # Despachante de tempos por slide
│   │   │   │   └── playback-cli.ts
│   │   │   └── ui/
│   │   │       ├── EditPlaybackTimingsModal.tsx
│   │   │       └── PlaybackControlBar.tsx
│   │   └── tests/
│   │       └── playback-sync.test.ts
│   │
│   ├── hooks/                          # FATIA DO MOTOR DE GATILHOS (DAG ENGINE)
│   │   ├── package.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # HookNode, HookEdge, HookGraph, hooks.schema.json
│   │   │   ├── daemon/
│   │   │   │   ├── dag-runner.ts       # Pipeline assíncrono de nós
│   │   │   │   ├── nodes/
│   │   │   │   │   ├── regex-node.ts   # Extração de prefixo de arquivo
│   │   │   │   │   ├── diff-node.ts    # Disparo somente se alterado
│   │   │   │   │   ├── obs-node.ts     # Chamada WebSocket v5 (obs-websocket-js)
│   │   │   │   │   └── http-node.ts    # Chamada HTTP com timeout 500ms (Lumikit)
│   │   │   │   ├── hooks-repo.ts       # Persistência do hooks.json
│   │   │   │   └── hooks-cli.ts        # lumen hook add, list, test
│   │   │   └── ui/
│   │   │       ├── HookDagCanvas.tsx   # Canvas visual de nós (@xyflow/react)
│   │   │       ├── nodes/              # Blocos visuais customizados do XYFlow
│   │   │       │   ├── TriggerNode.tsx # Gatilho (on_transition, on_panic)
│   │   │       │   ├── TransformNode.tsx # Regex e extração de payload
│   │   │       │   ├── DiffNode.tsx    # Filtro condicional
│   │   │       │   └── ActionNode.tsx  # Lumikit HTTP / OBS Scene Switch
│   │   │       ├── HookToggleList.tsx  # Liga/desliga gatilhos em tempo real
│   │   │       └── HookStatusBadge.tsx # Indicador visual de disparo HTTP
│   │   └── tests/
│   │       └── dag-runner.test.ts
│   │
│   ├── keymap/                         # FATIA DE MAPEAMENTO DE ATALHOS
│   │   ├── package.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # keybindings.schema.json
│   │   │   ├── daemon/
│   │   │   │   ├── keymap-repo.ts      # Leitura e escrita do keybindings.json
│   │   │   │   └── keymap-cli.ts
│   │   │   └── ui/
│   │   │       ├── useKeymapListener.ts# Captura eventos W3C event.code nativo
│   │   │       └── KeymapHelpDialog.tsx
│   │   └── tests/
│   │       └── keymap.test.ts
│   │
│   ├── pptx/                           # FATIA DE APRESENTAÇÕES (PPTX & PDF)
│   │   ├── package.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # PptxSlideMetadata DTOs
│   │   │   ├── daemon/
│   │   │   │   ├── xml-notes-reader.ts # Extrator XML das notas (fflate + fast-xml-parser)
│   │   │   │   ├── webp-cache.ts       # Pré-renderizador de imagens acelerado
│   │   │   │   └── pptx-cli.ts
│   │   │   └── ui/
│   │   │       └── DeckSlideGrid.tsx
│   │   └── tests/
│   │       └── xml-notes.test.ts
│   │
│   ├── stage/                          # FATIA DE RETORNO DE PALCO & LOWER THIRDS
│   │   ├── package.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # LowerThirdAlert, CountdownState
│   │   │   ├── daemon/
│   │   │   │   ├── timer-engine.ts     # Cronômetro regressivo
│   │   │   │   ├── alert-queue.ts      # Fila de mensagens de rodapé
│   │   │   │   └── stage-cli.ts
│   │   │   └── ui/
│   │   │       ├── StageView.tsx       # Rota web /stage para TVs e tablets
│   │   │       ├── SpeakerNotesCard.tsx# Exibição das anotações do orador
│   │   │       ├── BigClockWidget.tsx  # Relógio gigante de alto contraste
│   │   │       ├── NextSlidePreview.tsx
│   │   │       ├── LowerThirdAlertDrawer.tsx
│   │   │       └── StageCountdownControl.tsx
│   │   └── tests/
│   │       └── timer-engine.test.ts
│   │
│   ├── broadcast/                      # FATIA DE STREAMING TRANSPARENTE (OBS STUDIO)
│   │   ├── package.json
│   │   ├── src/
│   │   │   ├── index.ts
│   │   │   ├── contracts/              # BroadcastTheme, AlphaConfig
│   │   │   └── ui/
│   │   │       ├── BroadcastOverlayView.tsx # Rota web /broadcast com Alpha Channel
│   │   │       └── LowerThirdRenderer.tsx
│   │   └── tests/
│   │       └── broadcast.test.ts
│   │
│   └── remote/                         # FATIA DE CONECTIVIDADE & MOBILE APP
│       ├── package.json
│       ├── src/
│       │   ├── index.ts
│       │   ├── contracts/              # RemoteMessage, RemoteAuth, RemoteDevice
│       │   ├── daemon/
│       │   │   ├── remote-server.ts    # Servidor WebSocket pareado com o app mobile
│       │   │   ├── udp-discovery.ts    # Descoberta na rede local
│       │   │   └── remote-cli.ts
│       │   └── ui/
│       │       └── RemotePairingModal.tsx # QR Code de pareamento rápido
│       └── tests/
│           └── remote-server.test.ts
│
└── apps/
    ├── daemon/                         # [HOST 1] THIN SHELL DAEMON BUN
    │   ├── package.json
    │   ├── tsconfig.json
    │   └── src/
    │       ├── index.ts                # Inicializa Bun.serve (HTTP + WS) e monta rotas dos slices
    │       ├── cli.ts                  # Runner do binário lumen com Command Registry
    │       └── db/
    │           ├── db-client.ts        # Conexão bun:sqlite modo WAL
    │           └── db-migrate.ts       # Migração de tabelas
    │
    └── ui/                             # [HOST 2] THIN SHELL DESKTOP TAURI V2 + REACT 19
        ├── package.json
        ├── index.html
        ├── tsconfig.json
        ├── src/
        │   ├── index.tsx               # Bootstrap do React 19 + QueryClientProvider
        │   ├── App.tsx                 # Roteador das visões (/operator, /projector, /stage, /broadcast)
        │   ├── core/                   # Utilitários globais de transporte
        │   │   ├── sync/
        │   │   │   ├── useWebSocketTransport.ts # Conexão e heartbeat WS
        │   │   │   ├── useScreenState.ts        # Seletor do ScreenState
        │   │   │   └── useCommandDispatch.ts    # Despachante tipado de comandos
        │   │   └── components/ui/      # Componentes base shadcn/ui (Button, Dialog, Sheet)
        │   └── operator/
        │       └── OperatorWorkspace.tsx # Layout Resizable montando os componentes das fatias
        └── src-tauri/                  # Envelope nativo Tauri v2
            ├── Cargo.toml
            └── tauri.conf.json         # Gerenciamento das janelas (Monitor 1 e HDMI 2)
```
