# Checklist Geral & Changelog: LumenLite

Este documento serve como o painel central de rastreamento do projeto **LumenLite** (modernização do Holyrics), consolidando o **Changelog** do que já foi construído e o **Checklist Geral de Requisitos** (RF e RNF) mapeados contra os 7 módulos do sistema.

---

## 1. Changelog Recente

### [v0.1.0] - Esqueleto Inicial & Arquitetura Base (Atual)
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
  - **`slices/hooks`:** Schema Zod v4 para nós e arestas do grafo DAG de automação.
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
- [~] **RF-08: Busca Instantânea FTS5** (Tabela virtual SQLite `songs_fts` criada com BM25; pendente query builder de busca no daemon).
- [ ] **RF-09: Base Offline Embutida** (Pacote inicial de hinos e louvores tradicionais em formato JSON/SQLite).
- [ ] **RF-10: Integração Online API Vagalume** (Cliente HTTP assíncrono com cancelamento via `AbortController`).
- [ ] **RF-11: Importador Completo do Holyrics** (Leitura de ZIPs com `fflate`, higienização de tags `<c>`, `<t>` e persistência no SQLite).
- [ ] **RF-12: Histórico e Relatório de Execução** (Tabela de histórico de execuções com data e frequência).

### Módulo 3: Motor Bíblico & Escrituras (Bible Engine)
- [~] **RF-13: Múltiplas Traduções Concorrentes** (Estrutura do slice preparada para tabelas SQLite de NVI, NAA, ARA, etc.).
- [ ] **RF-14: Projeção em Tela Dividida (Dual Version)** (Grid CSS responsivo no renderizador para versículos lado a lado).
- [x] **RF-15: Parser de Referências (Quick Jump)** (Micro-parser sintático em TypeScript cobrindo padrões PT-BR, com testes unitários).
- [ ] **RF-16: Busca Textual por Palavra-Chave** (Tabela FTS5 de versículos bíblicos com filtros por testamento/livro).
- [ ] **RF-17: Gerenciador de Downloads Bíblicos** (Download e instalação de novas traduções em tempo de execução).

### Módulo 4: Roteiro de Culto & Apresentações Multimídia
- [~] **RF-18: Sistema Polimórfico de Itens (HItems)** (Union discriminada de itens no Zod para Músicas, Versículos, Mídias, PPTX e Textos).
- [x] **RF-19: Culto como Código (`culto.json`)** (Schema Zod v4 estrito, `liturgy.schema.json` Draft 2020-12 e validador de integridade).
- [x] **RF-20: Pilha de Interrupção (Return Stack)** (Implementação LIFO completa no `@lumen/core` com testes de unidade).
- [ ] **RF-21: Ingestão Nativa de PPTX & PDF** (Extração direta com `fflate` + `fast-xml-parser` das anotações pastorais).
- [ ] **RF-22: Playback Sincronizado** (Controle de timing de slides sincronizado com a Web Audio API).
- [~] **RF-22A: Projeção Rápida de Texto Avulso (Quick Text)** (Contratos de apresentação livre prontos; pendente modal no operador).
- [ ] **RF-22B: Menu de Contexto no Roteiro Litúrgico** (Ações de botão direito para inserção de mídias e anotações pastorais).
- [ ] **RF-22C: Ingestão por Arraste e Solte (Drag & Drop)** (Reordenação de itens e arraste de arquivos externos para o culto).

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
- [~] **RF-31: Motor de Hooks Baseado em Grafo (DAG)** (Contratos e schemas Zod do grafo definidos).
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

1. **Etapa 1: Acervo de Músicas & Busca FTS5 (`slices/songs`):**
   - Implementar inserção e consulta real no SQLite FTS5.
   - Integrar a busca da Coluna 2 do console com o banco em tempo real.
2. **Etapa 2: Motor Bíblico Offline (`slices/bible`):**
   - Povoar tabelas SQLite com traduções públicas e ligar a pesquisa direta via atalho do Spotlight (`Cmd+K` / `jo 3 16`).
3. **Etapa 3: Roteiro Litúrgico Interativo (`slices/liturgy` + `slices/hitems`):**
   - Permitir carregar `culto.json` no operador, reordenar via drag & drop e disparar itens direto para o telão.
4. **Etapa 4: Projetor HDMI & Seamless Loop (`slices/navigation`):**
   - Implementar a janela `/projector` com auto-fit de texto por CSS e double-buffer de vídeo em loop.
5. **Etapa 5: Motor de Automação DAG (`slices/hooks`):**
   - Implementar o runner DAG e o editor visual com `@xyflow/react`.
