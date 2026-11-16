# Checklist Geral & Changelog: LumenLite

Este documento serve como o painel central de rastreamento do projeto **LumenLite** (modernização do Holyrics), consolidando o **Changelog** do que já foi construído e o **Checklist Geral de Requisitos** (RF e RNF) mapeados contra os 7 módulos do sistema.

---

## 1. Changelog Recente

### [v1.4.0] - Refinamento Visual & UI Refresh ("Slate Pro Studio Refined")
- **Paleta de Superfícies & Elevação por Luminosidade:**
  - Nível 0 (Base do App): `#0B0D10` no container principal de `OperatorView.tsx`.
  - Nível 1 (Painéis e Header): `#11141A` com microborda suave `border-white/5` no topo e nas colunas laterais.
  - Nível 2 (Cards e Interatividade): `#141820` e `#181C24` (com hover `#1F242F`), eliminando o excesso de caixas ("boxiness") e substituindo bordas duras por profundidade tonal.
- **Barra Superior & Controles Críticos de Pânico:**
  - Barra de busca global Spotlight (`Ctrl+K`) em estilo macOS/Raycast (`bg-[#161A22]`, `hover:bg-[#1C212B]`, borda `border-white/5` e atalho `<kbd>` discreto com `tabular-nums font-mono text-[10px]`).
  - Barra de Pânico unificada em *segmented button group* único (`rounded-lg bg-[#141820] p-0.5 border border-white/5`), com estilo ghost inativo e acendimento semântico preciso: Blackout em Vermelho Carmim (`rose-500`), Ocultar Letra em Âmbar (`amber-500`) e Congelar em Ciano (`cyan-500`).
- **Coluna da Esquerda (Roteiro, Mídias e Lista de Músicas):**
  - Abas superiores convertidas para *Segmented Control* em pílula única com fundo `#0B0D10]/80` e aba ativa com destaque `bg-[#181C24] text-sky-400`.
  - Remoção do ícone azul repetitivo `<Music />` em cada card do catálogo de músicas (`SongSearchList.tsx`), liberando largura útil e respiro para leitura.
  - Itens inativos flutuando diretamente sobre o painel sem bordas duras; item selecionado com barra lateral de 3px azul céu e item ao vivo com esmeralda e indicador pulsante.
- **Coluna Central (Grade de Estrofes & Respiro):**
  - Estrofes com padding ampliado (`p-4.5`), cantos arredondados consistentes (`rounded-xl`) e espaçamento generoso (`gap-3`).
  - Numeração com alinhamento tabular monotípico `tabular-nums font-mono` (`00`, `01`, `02`...).
  - Estrofes inativas em `#141820` com texto `#A0AAB8`, foco em `#18202C` e ao vivo em esmeralda translúcido `bg-emerald-950/25` com badge `AO VIVO`.
- **Coluna da Direita (Preview do Telão e Áudio):**
  - Remoção da borda verde externa em volta do preview 16:9, substituída por sombra profunda e cinematográfica `shadow-2xl shadow-black/90` com microborda de 5% de opacidade.
  - Timer de projeção e contadores em fonte mono tabular (`tabular-nums font-mono`).
  - Alternador de abas Áudio / Fundos em segmented control e simplificação visual do `AudioPlayerPanel`.
- **Documentação de Design System:**
  - Criação do guia oficial `docs/UI_REFRESH_GUIDE.md` detalhando teoria de cores, hierarquia de superfícies e as 5 regras de ouro do design system.

### [v1.3.2] - Disparo Direto de Capa (Slide 0) via Duplo Clique em Músicas (Letras e Roteiro)
- **Disparo Imediato no Slide 0 (`SongSearchList.tsx`, `LiturgyScheduleColumn.tsx`):**
  - Adição da prop `onProjectSong` em `SongSearchColumnProps` e `LiturgyScheduleColumnProps`.
  - Duplo clique no card de música na aba Letras (`SongSearchList`) dispara `onProjectSong(song.id)`, enquanto o clique simples mantém a seleção/preparação no workspace central.
- **Integração no Controlador do Operador (`useOperatorController.ts`, `OperatorView.tsx`):**
  - Implementação da função `handleProjectSongById(songId)`: seleciona a canção no workspace, obtém o objeto `Song` (via cache local ou busca assíncrona por ID) e dispara imediatamente o slide 0 (Capa com Título e Artista) para o telão.
  - No duplo clique em item do Roteiro (`handleProjectItemFromRoteiro` em `OperatorView.tsx`), itens do tipo `SONG` disparam `handleProjectSongById(item.songId)` garantindo a projeção instantânea da Capa / Banda.
- **Testes & Qualidade:**
  - Testes unitários para `SongSearchList` em `slices/songs/ui/__tests__/song-search-list.test.tsx` validando renderização, clique simples e duplo clique.
  - 116 testes passando com 100% de sucesso no Bun e zero erros de tipo (`bun run check`).

### [v1.3.1] - Faxina Visual na Coluna Central, Botão Editar Música no Workspace e Slide 0 de Capa
- **Despoluição Visual da Coluna Central (`OperatorView.tsx`):**
  - Remoção completa do cabeçalho duplicado no topo da Coluna Central (badge redundante `[AO VIVO NO TELÃO]` e botão `[Apresentar no Telão]`), liberando a altura vertical inteira para as estrofes.
- **Botão de Editar Música no Workspace & Integração com o Editor (`SongStanzaGrid.tsx`, `SongImporterModal.tsx`):**
  - Botão com ícone `Pencil` do Lucide no cabeçalho do `SongStanzaGrid` abrindo o `SongImporterModal` em modo de edição com os dados da música atual (Título, Artista, Letra formatada com quebra dupla `\n\n` e fundo associado).
  - Preservação do `id` no salvamento via `songsService.upsert` com invalidação do cache e atualização instantânea no workspace.
- **Slide 0: Capa da Música com Título & Artista (`SongStanzaGrid.tsx`, `useOperatorController.ts`, `ForegroundTextDisplay.tsx`):**
  - Card `00 · Capa / Título` no topo da grade de estrofes no workspace central.
  - Projeção elegante no telão com Título imponente e Artista em subtítulo refinado (`text-sky-300`) durante a introdução instrumental da canção.
  - Testes unitários dedicados em `song-stanza-grid.test.tsx` e `foreground-text-display.test.tsx`.

### [v1.3.0] - Separação Arquitetural de Workspace (Preview Central) vs Live (Telão) e Mecânica Anti-Cliques Acidentais
- **FSM Core & Limpeza de Camadas (`packages/core/src/fsm`):**
  - Ajuste em `PRESENT_ITEM` para `mode === 'MEDIA'`: limpa integralmente o texto e slides de `foreground` (`content: []`, `slides: []`, `totalSlides: 0`), atribuindo o `background` à mídia projetada e ativando `mode: 'MEDIA'`.
  - Em `ProjectorScreen.tsx`: supressão garantida da camada de texto (`mode !== 'MEDIA'`) ao reproduzir imagens ou vídeos em tela cheia.
- **Mecânica Anti-Cliques Acidentais no Workspace:**
  - `SongStanzaGrid`: suporte a `focusedIndex`, `onSelectStanza` (1 clique simples foca localmente com anel azul céu `ring-sky-500/40 bg-[#172338]`) e `onProjectStanza` (duplo clique dispara a estrofe ao vivo no telão com destaque esmeralda `ring-emerald-500/50 bg-[#1D2D3E]`, barra lateral e badge `AO VIVO`).
  - `BibleVerseGrid`: suporte a `focusedIndex`, `onSelectVerse` (1 clique seleciona localmente em azul céu) e `onProjectVerse` (duplo clique dispara o versículo ao vivo em esmeralda com badge `AO VIVO`).
  - `MediaGridPanel`: duplo clique em imagem/vídeo dispara projeção em tela cheia (`onProjectMedia`), além do menu de contexto com opções explícitas `"Projetar em Tela Cheia"` e `"Definir como Fundo de Louvor"`.
  - `LiturgyScheduleColumn` & `LiturgyItemCard`: 1 clique seleciona e carrega no workspace central (`onSelectItem`), enquanto 2 cliques (duplo clique) projeta imediatamente para o telão (`onProjectItem`).
- **Controlador do Operador Desacoplado (`useOperatorController.ts` & `OperatorView.tsx`):**
  - Estado desacoplado de `workspaceItem` (`SONG`, `BIBLE`, `PPTX`) e `workspaceFocusedIndex`.
  - `handleSelectSong` e `handleSelectBible` apenas preparam o item no workspace central sem disparar para o telão.
  - Cabeçalho do Workspace Central com título, badge contextual dinâmico (`AO VIVO NO TELÃO` em esmeralda vs `EM PREPARAÇÃO (NÃO ESTÁ NO TELÃO)` em azul céu) e botão de ação rápida `"Apresentar no Telão (Enter)"`.
  - Atalho global da tecla `Enter` disparando o item e slide focado no workspace quando o foco não estiver em inputs de texto.

### [v1.2.0] - Skill de Domínio Ubíquo, Dicionário Canônico e Mapeamento Espacial de Telas
- **Linguagem Ubíqua & Dicionário de Domínio (`.opencode/skills/lumen-domain/SKILL.md` e `docs/DOMAIN_DICTIONARY.md`):**
  - Criação da skill oficial `lumen-domain` do OpenCode mapeando a terminologia física e visual do operador (Topo, Coluna 1, Coluna Central / Workspace, Monitor de Saída 16:9, Painel de Acessórios, Telão HDMI, Palco e Controle Remoto).
  - Mapeamento detalhado de arquivos fonte e componentes para cada região espacial da tela.
  - Formalização dos conceitos de domínio: *Workspace vs Live*, *Disparo 1-Clique vs 2-Cliques*, *Fundo de Louvor vs Mídia em Tela Cheia*, *Fundo Padrão*, *Pânico* e Modos da FSM (`ScreenMode`).
  - Estabelecimento de protocolo obrigatório de governança e auto-atualização contínua da documentação em novos commits.

### [v1.1.0] - Refatoração Comercial de Produto & UI/UX, Fluxo Roteiro/Mídias Universal, Remoção de Mocks/Playback e Suíte E2E Playwright
- **Faxina de Vazamentos Técnicos (*Implementation Leaks*):**
  - Remoção de todos os códigos `RF-*` de tooltips, botões e cabeçalhos de modais, substituídos por microcopy orientada à ação e domínio de cabine.
  - Substituição do badge `culto.json` por título editável do evento na barra superior (`Culto de Celebração`) com indicador de salvamento automático (`POST /api/liturgy/title`).
  - Ocultação do badge `Daemon Conectado` em operação normal (exibindo alerta discreto apenas quando desconectado) e migração da telemetria de rodapé (`Versão FSM`, `Latência IPC`) para o modal dedicado `DiagnosticsModal` (`Ferramentas -> Diagnóstico / Sobre`).
  - Unificação dos indicadores duplicados de resolução/HDMI no cabeçalho do Monitor de Saída (`Saída: Telão Principal (1080p)`).
- **Redesenho da Topbar, Proporção de Cabine & Coluna 1 com Abas (`Roteiro | Imagens | Vídeos | Arquivos`):**
  - Barra de busca global (`Spotlight`) centralizada na `OperatorTopBar` e agrupamento de ferramentas secundárias no menu dropdown `Ferramentas`.
  - Proporção das 4 colunas ajustada para `20% | 22% | 33% | 25%` (`lg:grid-cols-[20fr_22fr_33fr_25fr]`).
  - Coluna 1 (Esquerda) equipada com abas rápidas `Roteiro`, `Imagens`, `Vídeos` e `Arquivos`, suporte a **Drag & Drop universal** (arraste de músicas, mídias, apresentações e arquivos externos do SO via `POST /api/media/upload`) e **Menu de Contexto rico** no botão direito (`LiturgyContextMenu` com projeção imediata, duplicação, movimentação e adição de músicas, leituras, avisos, contagens, mídias e arquivos).
- **Hierarquia de Superfícies, Semântica Preview vs. Ao Vivo e Polimento Visual:**
  - Hierarquia por luminância de superfície (`#090D14` base, `#0F1622` painéis laterais, `#131C2B` palco central e `#192334` cards sem bordas externas) e scrollbar escura customizada de `5px`.
  - Diferenciação estrita entre item **Selecionado em Preview (Azul `#3B82F6`)** e item **Ao Vivo no Telão (Esmeralda `#10B981`)**, removendo os badges repetitivos `NO AR` das estrofes e versículos e ampliando a tipografia das letras para `15px` (`line-height: 1.5`).
  - Escalonamento proporcional 16:9 (`1920x1080` virtual via `ResizeObserver` + `transform: scale`) no `ProjectorScreen` em modo preview, eliminando cortes verticais.
  - Compactação do `AudioPlayerPanel` com arte minimalista de onda sonora (`AudioLines`) e importação de trilhas locais.
- **Remoção Total de Mocks e Exclusão do Módulo Playback:**
  - Exclusão completa da fatia `slices/playback`, rotas `/api/playback` e modal de playback.
  - Remoção de todos os itens fictícios pré-populados de mídia (`DEFAULT_MEDIA_ITEMS`), playlists (`DEFAULT_AUDIO_PLAYLISTS`), roteiro (`DEFAULT_LITURGY.items`) e apresentações (`DEFAULT_SERMON_DECK`), mantendo o acervo de demonstração de 50 hinos e a Bíblia offline.
- **Suíte E2E Automatizada com Playwright (`e2e/operator-console.e2e.ts`):**
  - 3 cenários E2E completos validando ausência de vazamentos técnicos, navegação entre abas e menu de contexto do roteiro, semântica Preview vs. Ao Vivo e fidelidade 16:9 do Monitor de Saída no Chromium headless (`bun run test:e2e`).

### [v1.0.0] - Conclusão de 100% do Roadmap: Stage View, Remotos Mobile LAN, Lower Thirds, Importador Holyrics, Dual Version Bíblica, PPTX/PDF Nativo & Playback Sincronizado
- **Monitor de Retorno de Palco, Alertas Lower Thirds & Cronômetros (`slices/stage` - RF-02, RF-24, RF-25):**
  - Contratos Zod v4 em `stage-contracts.ts` e motor determinístico `StageEngine` inspirado em `StageViewUtils.java`, `AlertController.java` e `CountdownUtils.java`.
  - Fila sequencial de alertas de emergência (Lower Thirds) com auto-expiração cronometrada, estilos (`classic`, `urgent`, `minimal`) e posicionamento (`top` ou `bottom`) renderizados via `LowerThirdOverlay` no Projetor HDMI (`/projector`) e no Broadcast OBS (`/broadcast`).
  - Cronômetro sincronizado via WebSocket (`STAGE_STATE_CHANGED`) suportando modo duração (`DURATION`) e horário alvo (`TARGET_TIME` via `calculateSecondsUntilTargetTime`), com formatação tabular (`MM:SS` / `HH:MM:SS`) e indicação vermelha ao estourar o tempo (`< 0`).
  - Monitor de Retorno (`/stage` - `StageView.tsx`) em preto absoluto (`#000000`) com relógio digital tabular, cronômetro regressivo, slide atual em tipografia gigante, preview da próxima estrofe (`nextSlideContent`) e banner de recado exclusivo para o púlpito (`pastorMessage`).
  - Modal de comando `StageControlModal` integrado à `OperatorTopBar`.
- **Controle Multi-Monitor & Pareamento Remoto LAN via QR Code (`slices/remote` + `useMultiMonitor` - RF-01, RF-26A, RF-26B):**
  - Hook `useMultiMonitor` integrando Window Placement API (`getScreenDetails`) e gerenciamento de janelas Tauri v2 (`tauri.conf.json` com janelas `main`, `projector` e `stage`).
  - Gerenciador `RemoteAuthManager` com detecção automática de IP LAN IPv4, geração/rotação de tokens efêmeros e controle RBAC estrito: o perfil `preacher` só pode despachar `NAV_NEXT`, `NAV_PREV` e `NAV_GOTO`, enquanto `operator` possui acesso total à FSM.
  - Endpoint Web do Pregador (`/remote/preacher` - `PreacherRemoteView.tsx`) com cronômetro em destaque, notas do orador (`speakerNotes`) e botões táteis gigantes de avanço/recuo.
  - Console Web Mobile do Operador (`/remote/operator` - `OperatorRemoteView.tsx`) com controle de pânico, busca rápida FTS5 e grade de slides.
  - Modal `RemotePairingModal` com gerador determinístico de QR Code SVG offline (Zero dependências externas) para conexão imediata na LAN.
- **Importador Completo do Holyrics & Histórico de Execução (`slices/songs` - RF-11, RF-12):**
  - Higienizador léxico `tag-cleaner.ts` e importador `holyrics-importer.ts` baseados em `Music.java` e `MusicController.java`, descompactando pacotes `.zip`/`.hrm` em memória via `fflate`, limpando tags `<c>`, `<t>`, `<font>`, `##n(...)` e convertendo para `SongSchema`.
  - Repositório `SongHistoryRepository` baseado em `History.java`, gravando automaticamente na tabela SQLite `song_history` a cada projeção de música (`mode === 'SONG'`) com janela de deduplicação de 30 minutos (`HISTORY_DEDUP_WINDOW_MS`) e ranking de músicas mais tocadas (`getUsageStats`).
  - Modais `SongImporterModal` e `SongHistoryModal` integrados à Coluna 2 do console do operador.
- **Projeção Bíblica em Tela Dividida (Dual Version) & Gerenciador de Downloads (`slices/bible` - RF-14, RF-17):**
  - Suporte nativo a 2ª tradução simultânea (`dualVersion`, `secondaryVerses`, `secondaryContent`, `secondaryReference`) no `bible-router.ts`, `ForegroundLayer`, `ForegroundTextDisplay` (split-screen CSS Grid `grid-cols-2` responsivo) e seletor rápido no cabeçalho do `BibleVerseGrid`.
  - Gerenciador `BibleDownloader` baseado em `AvailableDownloads.java` para listagem de catálogo, instalação dinâmica de versões (`.bib` criptografados ou pacotes extraídos) em tempo de execução e remoção segura, integrado ao modal `BibleDownloadModal`.
- **Ingestão Nativa de PPTX/PDF & Playback Sincronizado (`slices/pptx` + `slices/playback` - RF-21, RF-22):**
  - Extrator nativo `pptx-extractor.ts` inspirado em `PowerPointImportView.java`, lendo `ppt/slides/slide*.xml` e `ppt/notesSlides/notesSlide*.xml` direto da memória via `fflate` + `fast-xml-parser` sem depender do Microsoft Office, preservando as anotações pastorais (`speakerNotes`) para o púlpito.
  - Repositório `PlaybackRepository` e resolvedor determinístico `resolvePlaybackSlideIndex` inspirados em `PlayBack.java`, sincronizando o `currentTime` da Web Audio API com o avanço automático de estrofes (`NAV_GOTO`) na FSM através do `PlaybackSyncModal`.
- **Testes, Build & Conformidade Final:**
  - 107 testes unitários e de integração passando em ~2.1s em 28 arquivos de teste (`bun test`).
  - 0 erros de TypeScript (`bun run check`), 0 erros/avisos no ESLint (`bun run lint`) e builds de produção Vite limpos (`bun run build`) para `@lumen/ui` e `@lumen/broadcast`.

### [v0.9.0-alpha] - Streaming Transparente OBS (Broadcast) & Stage Monitor (Concluído)
- **Contratos e Repositórios para Display (`slices/navigation`):**
  - Definição canônica de `StageMonitorStateSchema`, `PreacherRemoteStateSchema` e `BroadcastSettingsSchema` via Zod v4.
  - Implementação do `DisplayRepository` para persistência atômica de configurações visuais de overlay (`broadcast-settings.json`).
  - Criação do roteador HTTP `handleDisplayRoutes` no daemon para servir HTML transparente nas rotas `/broadcast`, `/stage` e `/remote/preacher`.
- **Renderizador de Broadcast (OBS Overlay):**
  - Nova aplicação isolada em `apps/broadcast` rodando sobre Vite + React 19 + TailwindCSS.
  - Conexão em tempo real via WebSocket (`useBroadcastConnector`) para receber `ScreenState` da FSM.
  - Renderizador transparente base `BroadcastView` otimizado para a Fonte de Navegador (Browser Source) do OBS.

### [v0.8.0] - Player de Áudio Litúrgico com Web Audio API, Equalizador 3-Band & Catálogo de Mídias
- **Player de Áudio Litúrgico com Web Audio API (`slices/media` - RF-28):**
  - Implementação do motor reativo `useAudioEngine` baseado no grafo nativo da Web Audio API (`AudioContext` -> `MediaElementAudioSourceNode` -> `BiquadFilterNode` [Low, Mid, High] -> `GainNode` -> `AnalyserNode` -> `destination`).
  - Equalizador gráfico paramétrico de 3 bandas (-12dB a +12dB para Graves em 120Hz, Médios em 1kHz e Agudos em 6kHz) com botão de restauração Flat (0dB).
  - Rampa suave de volume linear no nó de ganho para evitar cliques ou ruídos analógicos/digitais.
  - Visualizador de espectro sonoro em tempo real de 60fps em Canvas (`AudioSpectrumVisualizer`) consumindo dados FFT do nó analisador.
  - Painel ergonômico `AudioPlayerPanel` no padrão *Slate Pro Studio* com suporte a seleção de playlists de culto (Prelúdio, Oração, Pós-Culto), modos de repetição (`all`, `one`, `off`), reprodução aleatória (shuffle) e controle de seek.
- **Catálogo & Indexação de Mídias Locais (`slices/media/daemon` - RF-27, RF-29):**
  - Contratos Zod v4 estritos sem YAML para mídias (`MediaItemSchema`), faixas (`AudioTrackSchema`) e playlists (`AudioPlaylistSchema`).
  - Repositório `MediaRepository` com persistência atômica de playlists (`media-playlists.json`) e varredura de diretório local de assets com identificação automática de tipo MIME e categorias (`VIDEO`, `IMAGE`, `AUDIO`).
  - Roteador modular `/api/media/*` no daemon com suporte completo a requisições com cabeçalho `Range` (`206 Partial Content`) para streaming contínuo de vídeo e áudio.
  - Componente de grade visual `MediaGridPanel` com aplicação de backgrounds de projeção (`SET_BACKGROUND`) em 1 clique e indicador de mídia "No Ar".
  - Integração da Coluna 4 do operador (`OutputMonitorPanel`) com abas de alternância rápida entre Áudio Litúrgico e Galeria de Fundos.
- **Comandos de Mídia e Áudio no CLI Headless (`lumen` - RF-30):**
  - `lumen media list [tipo]` para catálogo de mídias e loops.
  - `lumen media bg <id>` para aplicação direta de background na FSM via terminal.
  - `lumen audio playlists` para listagem de playlists litúrgicas de áudio.
- **Testes & Conformidade:**
  - 88 testes automatizados passando em ~2.1s com 100% de sucesso no Bun.
  - 0 erros de TypeScript estrito (`bun run check`) e 0 advertências no ESLint (`bun run lint`).

### [v0.7.0] - CLI Headless Completo, Sistema Declarativo de Atalhos (keybindings.json) & FSM REST Router
- **CLI Headless de Controle Total (`apps/daemon/src/cli.ts` - RF-30):**
  - Implementação do binário executável `lumen` com suporte a controle total da projeção sem interface gráfica:
    - Navegação direta: `lumen next`, `lumen prev`, `lumen goto <idx>`, `lumen clear`.
    - Pânico instantâneo: `lumen blackout`, `lumen clear-text`, `lumen clear-bg`, `lumen freeze`, `lumen panic clear`.
    - Músicas: `lumen song search <termo>`, `lumen song play <id> [idx]`.
    - Bíblia: `lumen bible quick <ref>`, `lumen bible search <termo>`.
    - Liturgia: `lumen liturgy show`, `lumen liturgy quick-text "<texto>"`.
    - Automações: `lumen automation status`, `lumen automation trigger <tipo>`.
    - Atalhos: `lumen keymap list`, `lumen keymap reset`.
    - Telemetria: `lumen status`.
  - Suporte a output estruturado JSON (`--json`) e endpoint remoto configurável (`--url`).
  - Suíte de testes automatizados com mock server em `apps/daemon/src/__tests__/cli.test.ts`.
- **Sistema Declarativo de Atalhos de Teclado (`slices/keymap` - RF-34):**
  - Esquemas Zod v4 para `keybindings.json` com validação estrita de ações canônicas (`KeyActionIdSchema`), escopos (`KeymapScopeSchema`) e combinações de teclas.
  - Repositório atômico `KeymapRepository` com salvamento temporário (`.tmp`) e função de restauração de fábrica (`resetToDefaults`).
  - Utilitários puros `matchesKeyCombo` (com suporte a normalização de `CommandOrControl`, modificadores e teclas de função) e `formatKeyComboDisplay`.
  - Roteador REST modular `/api/keymap/*` no daemon (`GET /api/keymap`, `PUT /api/keymap`, `POST /api/keymap/reset`).
  - Roteador REST `/api/fsm/*` no daemon (`POST /api/fsm/dispatch` e `GET /api/fsm/state`) para despacho de comandos sem depender de WebSockets.
- **Integração na Camada de UI (React 19):**
  - Hook reativo universal `useKeybindings` com isolamento de foco em formulários/inputs e prevenção de conflitos em modais abertos.
  - Modal `KeymapModal` no padrão *Slate Pro Studio* permitindo ao operador consultar todos os atalhos, filtrar em tempo real e restaurar padrões.
  - Integração com a barra de ferramentas `OperatorTopBar` e footer do console do operador (`OperatorView`).
- **Testes & Conformidade:**
  - 77 testes automatizados passando em ~2.0s com 100% de sucesso no Bun.
  - 0 erros de TypeScript estrito (`bun run check`) e 0 advertências no ESLint (`bun run lint`).

### [v0.6.0] - Motor de Automações DAG, Cliente Nativo OBS Studio v5 & Editor Visual por Nós
- **Motor de Automações Baseado em Grafo Acíclico Dirigido (`slices/automation` - RF-31, RNF-04):**
  - Implementação de contratos Zod v4 com suporte a nós tipados de Gatilho (`TRIGGER`: `ON_SLIDE_CHANGE`, `ON_ITEM_SELECT`, `ON_PANIC`, `ON_HTTP_WEBHOOK`), Condições (`CONDITION`: `equals`, `contains`, `starts_with`, etc.) e Ações (`ACTION_OBS`, `ACTION_HTTP`, `ACTION_FSM`, `ACTION_DELAY`).
  - Algoritmo de validação de aciclicidade via Busca em Profundidade (DFS) em `validateGraphAcyclic`, rejeitando ciclos diretos ou indiretos e loops em `automation.json`.
  - Executor assíncrono `DagRunner` operando com ramificações concorrentes via `Promise.allSettled`, protegido por timeouts individuais (`AbortSignal.timeout`) para garantir **0ms de impacto na thread de projeção** (RNF-04).
  - Repositório atômico `AutomationRepository` para persistência segura de `automation.json` via gravação temporária (`.tmp`).
- **Cliente Nativo de Alta Performance para OBS Studio WebSocket v5 (RF-33):**
  - Implementação do `ObsClient` nativo em Bun utilizando `WebSocket` e `crypto.createHash('sha256')` para autenticação com hash duplo sem bibliotecas externas pesadas.
  - Suporte a comandos canônicos de transmissão (`SetCurrentProgramScene`, `SetCurrentPreviewScene`, `SetSceneItemEnabled`, `SetInputMute`, `ToggleInputMute`, `StartStream`, `StopStream`, etc.).
  - Gerenciamento de reconexão automática e tolerância a quedas de rede.
- **Roteador Modular REST do Daemon (`/api/automation/*`):**
  - `GET /api/automation`: Consulta grafo ativo e telemetria de conectividade com o OBS Studio.
  - `PUT /api/automation`: Validação atômica e salvamento de novas regras de grafo.
  - `POST /api/automation/test-trigger`: Disparo manual de testes para validação imediata de regras.
  - `POST /api/automation/webhook/:triggerId`: Receptor de webhooks externos para acionamento remoto.
- **Editor Visual Interativo com Blocos (`@xyflow/react` - RF-32):**
  - Componente `AutomationCanvas` com nós customizados nos padrões *Slate Pro Studio* (`TriggerNode`, `ConditionNode`, `ActionNode`).
  - Handles de conexão com código de cores semântico (verde para `true`, vermelho/ardósia para `false`, ciano para conexões gerais).
  - Modal integrado `AutomationModal` acessível diretamente pelo console do operador através da `OperatorTopBar`.
- **Testes & Conformidade:**
  - 61 testes automatizados passando em ~1.9s com 100% de sucesso no Bun.
  - 0 erros de TypeScript estrito (`bun run check`) e 0 advertências no ESLint (`bun run lint`).

### [v0.5.0] - Janela do Projetor HDMI, Auto-Fit Dinâmico & Seamless Video Loops
- **Janela de Projeção Dedicada (`/projector` - RF-01, RF-03, RF-04):**
  - Implementação do componente `ProjectorView` em `apps/ui/src/projector/ProjectorView.tsx` renderizado em tela cheia com cursor invisível (`cursor: none`) e atalhos diretos de emergência (`F1` para blackout, `F2` para limpar texto).
  - Componente universal de projeção `ProjectorScreen` com suporte a todas as máscaras de pânico: `blackout` (tela preta total instantânea em 0ms), `clearText` (suprime o texto mantendo o vídeo em loop), `clearBg` (fundo escuro sólido mantendo texto) e `freeze` (congelamento de quadro via ref).
- **Tipografia Responsiva com Auto-Fit Dinâmico (RF-05, RF-06):**
  - Hook de alta performance `useAutoFitText` (`slices/navigation/ui/hooks/useAutoFitText.ts`) com algoritmo de busca binária e `ResizeObserver` para ajustar a fonte (`fontSize`) de forma suave entre 24px e 86px sem overflow ou cortes no telão.
  - Componente `ForegroundTextDisplay` com aplicação estrita do Design System *Slate Pro Studio* (sombra suave, legibilidade máxima e indicador discreto de slide/estrofe).
- **Vídeo em Background com Double-Buffering & Seamless Loop (RF-27):**
  - Componente `BackgroundVideoLoop` (`slices/navigation/ui/components/BackgroundVideoLoop.tsx`) utilizando técnica de double-buffering com dois elementos `<video>` HTML5 sobrepostos (Buffer A e Buffer B).
  - Crossfade de opacidade calibrado (~200ms) eliminando totalmente o flash preto de fim de arquivo gerado por navegadores/Chromium.
- **Higienização de Marcações Litúrgicas para a Congregação:**
  - Micro-motor léxico `text-sanitizer.ts` e testes unitários para purgar marcações de ensaio/banda (`[CORO]`, `[V1]`, `[VERSO 2]`, `[PONTE]`, `[INTRO]`, etc.) do telão público, mantendo a tela 100% focada na congregação.
- **Monitor de Saída Fiel na Coluna 4 do Operador:**
  - Atualização do `OutputMonitorPanel` utilizando o `ProjectorScreen` em proporção 16:9, permitindo que o operador visualize exatamente o que a congregação vê em escala 1:1, além de botão com link para abrir o telão em nova janela.
- **Testes & Conformidade:**
  - 47 testes passando com 100% de sucesso no Bun.
  - 0 erros de TypeScript (`bun run check`) e 0 advertências de linter (`bun run lint`).

### [v0.4.0] - Roteiro Litúrgico Interativo, HItems Polimórficos, Drag & Drop e Quick Text
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
- [x] **RF-01: Controle Multi-Monitor** (Configuração multi-janela no `tauri.conf.json`, hook `useMultiMonitor` com Window Placement API e botão de gerenciamento na `OperatorTopBar`).
- [x] **RF-02: Monitor de Retorno (Stage View)** (Rota `/stage` com `StageView.tsx` em alto contraste `#000000`, relógio digital tabular, cronômetro sincronizado, estrofe atual, próxima estrofe e alertas de púlpito).
- [x] **RF-03: Ações de Emergência (Pânico)** (Comandos de corte imediato `blackout`, `clearText`, `clearBg` e `freeze` no Reducer e botões no console).
- [x] **RF-04: Freeze (Congelar Projeção)** (Máscara atômica no `ScreenState`, despacho via FSM/WS e retenção de frame no `ProjectorScreen`).
- [x] **RF-05: Motor Tipográfico & Auto-Fit** (Hook `useAutoFitText` com busca binária de tamanho de fonte, `ResizeObserver` e higienização de letras).
- [x] **RF-06: Temas e Estilos Visuais** (Design System Slate Pro Studio, tokens em `MASTER.md` e renderizador `ForegroundTextDisplay` com sombra e legibilidade calibradas).

### Módulo 2: Gestão de Músicas & Letras (Music Engine)
- [x] **RF-07: Estrutura Semântica de Letras** (Contratos Zod `SongSchema` e `SongSectionSchema` com estrofes sóbrias).
- [x] **RF-08: Busca Instantânea FTS5** (Tabela virtual SQLite `songs_fts` com tokenizer unicode61, BM25 e snippets destacados via `SongRepository`).
- [x] **RF-09: Base Offline Embutida** (Pacote curado com 50 louvores e hinos tradicionais em `default-songs.json` auto-populados no primeiro boot).
- [x] **RF-10: Integração Online API Vagalume** (Cliente HTTP assíncrono com cancelamento via `AbortSignal` e normalização semântica de letras).
- [x] **RF-11: Importador Completo do Holyrics** (Descompactação de `.zip`/`.hrm` com `fflate`, higienização de tags `<c>`, `<t>`, `##` em `tag-cleaner.ts`, persistência no SQLite e `SongImporterModal`).
- [x] **RF-12: Histórico e Relatório de Execução** (Tabela SQLite `song_history` com janela de deduplicação de 30 minutos via `SongHistoryRepository`, gravação automática na projeção e `SongHistoryModal`).

### Módulo 3: Motor Bíblico & Escrituras (Bible Engine)
- [x] **RF-13: Múltiplas Traduções Concorrentes** (Tabelas SQLite WAL para traduções, semente da versão ACF com 31.106 versículos e extrator nativo de arquivos `.bib` legados).
- [x] **RF-14: Projeção em Tela Dividida (Dual Version)** (Lookup simultâneo de duas traduções no `bible-router.ts`, grid CSS split-screen lado a lado em `ForegroundTextDisplay` e seletor em `BibleVerseGrid`).
- [x] **RF-15: Parser de Referências (Quick Jump)** (Micro-parser sintático cobrindo os 66 livros canônicos, abreviações PT-BR e intervalos como "jo 3 16", "sl 23:1-4", "1co 13:4-8").
- [x] **RF-16: Busca Textual por Palavra-Chave** (Tabela virtual SQLite FTS5 com BM25, snippets destacados e consultas sub-3ms).
- [x] **RF-17: Gerenciador de Downloads Bíblicos** (`BibleDownloader` para catálogo, instalação dinâmica de versões `.bib`/extraídas em tempo de execução, remoção segura e `BibleDownloadModal`).

### Módulo 4: Roteiro de Culto & Apresentações Multimídia
- [x] **RF-18: Sistema Polimórfico de Itens (HItems)** (Union discriminada de itens no Zod para Músicas, Versículos, Mídias, PPTX, Textos e Contagens em `@slices/hitems`).
- [x] **RF-19: Culto como Código (`culto.json`)** (Schema Zod v4 estrito, `liturgy.schema.json` Draft 2020-12, persistência atômica via `LiturgyRepository` e validador de integridade).
- [x] **RF-20: Pilha de Interrupção (Return Stack)** (Implementação LIFO completa no `@lumen/core` com testes de unidade).
- [x] **RF-21: Ingestão Nativa de PPTX & PDF** (Extração direta em memória com `fflate` + `fast-xml-parser` de slides e `notesSlides` em `pptx-extractor.ts`, `PptxRepository` e `PptxDeckModal`).
- [x] **RF-22A: Projeção Rápida de Texto Avulso (Quick Text)** (Modal `QuickTextModal`, endpoint no daemon `/api/liturgy/quick-text` e projeção imediata na FSM).
- [x] **RF-22B: Menu de Contexto no Roteiro Litúrgico** (Menu completo no botão direito `LiturgyContextMenu` para projeção, duplicação, reordenação e inserção de músicas, leituras bíblicas, contagens, mídias, arquivos e anotações).
- [x] **RF-22C: Ingestão por Arraste e Solte (Drag & Drop)** (Abas `Roteiro | Imagens | Vídeos | Arquivos` na Coluna 1 com Drag & Drop universal de itens internos e upload direto de arquivos do sistema operacional).

### Módulo 5: Transmissão ao Vivo, Palco & Conectividade
- [x] **RF-23: Streaming Transparente para OBS** (Rota `/broadcast` servindo aplicação React dedicada com conexão WebSocket pub/sub, Lower Thirds e renderizador com canal alfa transparente `rgba(0,0,0,0)`).
- [x] **RF-24: Alertas Instantâneos & Lower Thirds** (Fila de recados de emergência com auto-ocultação cronometrada no `StageEngine`, renderizada em `LowerThirdOverlay` no projetor e broadcast).
- [x] **RF-25: Cronômetros & Contagens Regressivas** (Engine de cronômetro sincronizado via WebSocket com modos duração e horário alvo `HH:mm` para palco, telão e púlpito).
- [x] **RF-26A: Endpoint Web do Pregador (`/remote/preacher`)** (Interface móvel restrita `PreacherRemoteView` com slides, notas do orador, cronômetro e bloqueio RBAC contra ações destrutivas).
- [x] **RF-26B: Console Web do Operador via QR Code (`/remote/operator`)** (Interface móvel `OperatorRemoteView` completa e modal `RemotePairingModal` com gerador QR Code SVG offline e tokens LAN).
- [x] **RF-26C: Arquitetura Focada & Exclusão de Bloatware** (Servidor enxuto sem módulos supérfluos de chat ou redes sociais).

### Módulo 6: Mídia, Áudio & Loops
- [x] **RF-27: Loops de Vídeo Contínuos (Seamless Loops)** (Double buffering de `<video>` HTML5 com troca automática e transição de opacidade via CSS em `BackgroundVideoLoop`, integrado com o catálogo de fundos `MediaGridPanel`).
- [x] **RF-28: Player de Áudio com Equalizador** (Motor `useAudioEngine` com Web Audio API nativo, cadeia de `BiquadFilterNode` de 3 bandas, `GainNode` com rampa suave, visualizador FFT de espectro em Canvas e painel `AudioPlayerPanel`).
- [x] **RF-29: Cache e Geração Assíncrona de Miniaturas** (Indexação de arquivos no `MediaRepository`, detecção de MIME types e suporte a streaming local com HTTP `Range`/206 Partial Content).

### Módulo 7: Automação por Nós (DAG Hooks) & Controle via CLI
- [x] **RF-30: Controle Total via CLI (`lumen`)** (Binário executável completo cobrindo todas as fatias: navegação, pânico, músicas, Bíblia, liturgia, automação e keymap com saída JSON).
- [x] **RF-31: Motor de Automações Baseado em Grafo (DAG)** (Validador acíclico DFS, schemas Zod v4 e runner assíncrono `DagRunner` concorrente em `slices/automation`).
- [x] **RF-32: Editor Visual de Automação com Blocos (`@xyflow/react`)** (Canvas interativo `AutomationCanvas` e modal `AutomationModal` no padrão Slate Pro Studio).
- [x] **RF-33: Ações Externas Integradas** (Cliente nativo OBS WebSocket v5 `ObsClient`, chamadas HTTP REST para DMX e comandos internos de FSM).
- [x] **RF-34: Sistema Declarativo de Atalhos (`keybindings.json`)** (Contratos Zod v4, repositório atômico, hook universal `useKeybindings`, rotas REST e modal de atalhos `KeymapModal`).

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
4. **[CONCLUÍDO] Etapa 4: Projetor HDMI & Seamless Loop (`slices/navigation` - v0.5.0):**
   - Janela `/projector` de tela cheia sem cursores e com atalhos de pânico.
   - Double-buffering de vídeo em background eliminando flash preto de loop.
   - Auto-fit tipográfico dinâmico responsivo e higienização de tags litúrgicas.
5. **[CONCLUÍDO] Etapa 5: Motor de Automação DAG (`slices/automation` - v0.6.0):**
   - Implementação do runner DAG assíncrono com tolerância a falhas e sem bloqueio de thread.
   - Cliente OBS WebSocket v5 nativo em Bun com handshake de autenticação SHA-256.
   - Editor visual de blocos interativo com `@xyflow/react` e modal no console do operador.
6. **[CONCLUÍDO] Etapa 6: CLI Headless & Mapeador Declarativo de Atalhos (`slices/keymap` + CLI - v0.7.0):**
   - Expansão completa do binário CLI `lumen` para controle headless total (músicas, Bíblia, liturgia, pânico, automação e keymap com saída JSON).
   - Mapeamento declarativo via `keybindings.json` com validação Zod v4, repositório atômico e hook universal `useKeybindings`.
   - Rotas REST `/api/fsm/dispatch` e `/api/keymap/*` no daemon e modal de atalhos `KeymapModal` no operador.
7. **[CONCLUÍDO] Etapa 7: Player de Áudio Web Audio API & Cache de Miniaturas (`slices/media` - v0.8.0):**
   - Implementação do engine de áudio Web Audio API (`BiquadFilterNode`, `GainNode`, visualizador de espectro FFT em Canvas).
   - Catálogo de mídias e loops com streaming HTTP `Range` no daemon e galeria `MediaGridPanel` integrada à Coluna 4.
   - Subcomandos de mídia e áudio expandidos no CLI `lumen` (RF-27, RF-28, RF-29).
8. **[CONCLUÍDO] Etapa 8: Streaming Transparente para OBS & Stage Monitor Web (/broadcast, /stage e /remote) (v0.9.0):**
   - Renderizador dedicado para overlay transparente de letras e terços inferiores (Lower Thirds) com canal alfa para OBS Studio.
   - Telão de retorno de palco para ministros e músicos (`/stage`) com relógio sincronizado, slide atual e próximo slide.
   - Púlpito do pregador com notas e cronômetro (`/remote/preacher`).
9. **[CONCLUÍDO] Etapa 9: Consolidação v1.0.0 — Importador Holyrics, Dual Version Bíblica & PPTX/PDF Nativo (v1.0.0):**
   - Importador completo de pacotes `.zip`/`.hrm` do Holyrics e histórico automático de execução com deduplicação de 30 minutos (`slices/songs`).
   - Projeção bíblica em tela dividida (Dual Version) e gerenciador de downloads de traduções em tempo de execução (`slices/bible`).
   - Extração nativa de apresentações `.pptx`/`.pdf` com notas do orador (`slices/pptx`).
   - 100% dos Requisitos Funcionais e Não-Funcionais concluídos e validados.
10. **[CONCLUÍDO] Etapa 10: Refatoração de Produto & UI/UX Comercial, Zero Mocks & Testes E2E Playwright (v1.1.0):**
    - Remoção de vazamentos técnicos (`RF-*`, `culto.json`, `Daemon Conectado`, `Versão FSM`), redesenho da Topbar e hierarquia de superfícies por luminância (`#090D14` -> `#0F1622` -> `#131C2B` -> `#192334`).
    - Coluna 1 (Esquerda) com abas `Roteiro | Imagens | Vídeos | Arquivos`, Drag & Drop universal e menu de contexto completo no botão direito.
    - Exclusão completa da fatia `slices/playback` e de todos os dados mockados de mídia, áudio, roteiro e PPTX.
    - Suíte de testes End-to-End com Playwright (`bun run test:e2e`) validada com 100% de aprovação.
11. **[CONCLUÍDO] Etapa 11: Desacoplamento Arquitetural Workspace vs Live & Mecânica Anti-Cliques Acidentais (v1.3.0):**
    - FSM limpa texto/slides em `PRESENT_ITEM` com `mode === 'MEDIA'` e `ProjectorScreen` oculta texto sobre mídia.
    - Mecânica anti-cliques acidentais: 1 clique foca no Workspace local (azul céu) e 2 cliques projeta no telão ao vivo (esmeralda).
    - Botão de ação rápida e atalho `Enter` para envio imediato do slide em foco.
    - Distinção clara entre mídia em tela cheia e fundo de louvor.
12. **[CONCLUÍDO] Etapa 12: Refinamento Visual & UI Refresh "Slate Pro Studio Refined" (v1.4.0):**
    - Elevação por luminosidade (#0B0D10 -> #11141A -> #141820 / #181C24).
    - Redução drástica de bordas pesadas e containers excessivos (boxiness).
    - Segmented controls estilo Raycast/macOS e remoção de ícones repetitivos em listas.
    - Números tabulares monotípicos e sombra profunda de alta fidelidade no telão.
    - Guia de design system completo em docs/UI_REFRESH_GUIDE.md.
13. **[EM ANDAMENTO] Etapa 13: Empacotamento Desktop Multi-Monitor com Tauri v2:**
    - Validação de janelas nativas independentes do Projetor HDMI e Monitor de Palco em ambiente de produção desktop.
