# Dicionário de Domínio, Mapeamento Espacial e Linguagem Ubíqua: LumenLite

> **Status:** Canônico & Ativo  
> **Skill Associada:** `.opencode/skills/lumen-domain/SKILL.md`  
> **Público:** Desenvolvedores, Engenheiros de Software e Agentes de IA Autônomos.

Este documento consolida o **modelo mental**, a **linguagem ubíqua** e o **mapeamento espacial e arquitetural** do **LumenLite** — a plataforma moderna, reativa e de alta performance para projeção litúrgica em igrejas (port evolutivo e isomórfico do Holyrics original).

---

## 1. Visão Geral do Sistema e Filosofia de Operação

O LumenLite foi concebido para o ambiente de alta criticidade da **cabine multimídia de igreja**, onde os operadores (geralmente voluntários) necessitam de ergonomia visual, resposta imediata sem engasgos e previsibilidade total sob baixa luminosidade.

### 1.1 Princípios Arquiteturais e de Operação

1. **Design System "Slate Pro Studio":**
   - Paleta escura profunda (`#090D14` -> `#0F1622` -> `#131C2B`), reduzindo a fadiga ocular em salas escuras.
   - Letra limpa: tipografia de louvor sem badges intrusivos (`[CORO]`, `[V1]`) na transmissão ou no telão.
   - Sem poluição visual ("luzes de semáforo"): o verde esmeralda (`#10B981`) é reservado estritamente para o estado "No Ar / Live", e o azul petróleo (`#0284C7`) representa "Preview / Workspace".
2. **Determinismo FSM (Zero Efeitos Colaterais no Núcleo):**
   - O núcleo `@lumen/core` funciona como um Reducer puro `(State, Action) => State`.
   - Modos de pânico e transições ocorrem de maneira atômica e previsível.
3. **Desacoplamento Físico de Displays:**
   - A janela de controle do operador é puramente gerencial e não renderiza a projeção final para o público. A exibição no telão ocorre via janela dedicada (`/projector`), acelerada por GPU, sincronizada via WebSocket e IPC nativo (Tauri v2).
4. **Arquitetura Vertical de Fatias (`slices/*`):**
   - Cada funcionalidade é autocontida em três camadas estritas: `contracts/` (schemas Zod v4 e tipos imutáveis), `daemon/` (repositórios, SQLite WAL e serviços de rede) e `ui/` (componentes React 19).

---

## 2. Topologia do Sistema e Diagramas Espaciais

### 2.1 Topologia Física e Distribuição de Rede

```
                                  +---------------------------+
                                  |   LumenLite Daemon        |
                                  |   (Bun + SQLite WAL)      |
                                  |   Porta Local: 4040       |
                                  +-------------+-------------+
                                                |
                 +------------------------------+------------------------------+
                 | (WebSocket / IPC)            | (WebSocket / IPC)            | (HTTP / SSE / WS)
                 v                              v                              v
   +---------------------------+  +---------------------------+  +---------------------------+
   | Console do Operador       |  | Janela do Telão (HDMI)    |  | Monitor de Palco (Stage)  |
   | (React 19 / OperatorView) |  | (/projector - 16:9 4K)    |  | (/stage - Púlpito/Banda)  |
   +---------------------------+  +---------------------------+  +---------------------------+
                 |
                 | (Pareamento PIN via Web)
                 v
   +---------------------------+
   | Controle Remoto Pastor    |
   | (/remote - Mobile Web PWA)|
   +---------------------------+
```

---

### 2.2 Diagrama Espacial do Console do Operador (`OperatorView.tsx`)

A interface foi projetada no fluxo da esquerda para a direita: **Preparar ➔ Operar ➔ Monitorar & Controlar**.

```
+---------------------------------------------------------------------------------------------------------------------------------+
| [OPERATOR TOP BAR]                                                                                                              |
| LumenLite Studio | Conexão: [● Online] | Modos Pânico: [ Preto (F1) ] [ Limpar (F2) ] [ Logo (F3) ] | 19:45:00 | [🔍 Cmd+K] [⚙] |
+-----------------------+-------------------------------------------------------------+-------------------------------------------+
| COLUNA 1: PREPARAÇÃO  | COLUNA CENTRAL: WORKSPACE DE NAVEGAÇÃO                      | COLUNA DIREITA: MONITOR E ACESSÓRIOS      |
| (20% largura)         | (55% largura - Colunas 2 e 3 integradas)                    | (25% largura)                             |
+-----------------------+-------------------------------------------------------------+-------------------------------------------+
| [Roteiro do Culto]    | [Grade de Estrofes / Versículos / Slides]                   | [MONITOR DE SAÍDA (LIVE 16:9)]            |
|                       |                                                             | +---------------------------------------+ |
| 1. Prelúdio           | +---------------------------------------------------------+ | |                                       | |
| 2. Porque Ele Vive 𝅘𝅥𝅮  | | Verso 1 (Workspace Preview - Borda Azul)                | | |          PORQUE ELE VIVE              | |
|    - Tom: G           | | Deus enviou Seu Filho amado...                          | | |          POSSO CRER NO AMANHÃ         | |
|    - 4 estrofes       | +---------------------------------------------------------+ | |                                       | |
| 3. Oração Pastoral    | | Coro (NO AR / LIVE - Borda Esmeralda)                   | | +---------------------------------------+ |
| 4. João 3:16 📖       | | Porque Ele vive, posso crer no amanhã...                | | Status: [NO AR] | Modo: TEXT | 1080p 60fps  |
| 5. Avisos Gerais      | +---------------------------------------------------------+ | [Projetor HDMI: Ativo] [Abrir em Janela]  |
| 6. Oferta & Louvor    | | Verso 2                                                 | +-----------------------------------------+ |
|                       | | E quando, enfim, chegar a hora...                       | [ACESSÓRIOS & FUNDOS RÁPIDOS]             |
|                       | +---------------------------------------------------------+ | - Seletor de Fundo de Louvor:             |
|                       |                                                             |   [ Motion Azul ] [ Abstrato Dourado ]    |
|                       | [Atalhos de Teclado]                                        | - Alertas Rápidos para o Telão:           |
|                       |  [▲ / ▼] Navegar  |  [Enter] Disparar  |  [Esc] Limpar      |   [ "Placa ABC-1234 farol aceso" ] [Enviar]|
| [+ Novo Item]         |                                                             | - Contagem Regressiva (Countdown):        |
| [Importar / Exportar] |                                                             |   [ 05:00 ] [ Iniciar Contagem ]          |
+-----------------------+-------------------------------------------------------------+-------------------------------------------+
```

---

## 3. Mapeamento Espacial de Componentes e Arquivos

| Região Espacial | Componente React | Caminho do Arquivo Fonte | Responsabilidade |
|---|---|---|---|
| **Barra Superior (Topo)** | `OperatorTopBar` | `slices/navigation/ui/components/OperatorTopBar.tsx` | Status do daemon, relógio do culto, botões de pânico imediato e atalho para o Spotlight. |
| **Busca Universal (Spotlight)** | `SpotlightDialog` | `apps/ui/src/operator/components/SpotlightDialog.tsx` | Command Palette (`Cmd+K`) com busca instantânea em músicas, versículos, atalhos e ações do sistema. |
| **Coluna 1 (Esquerda)** | `LiturgyScheduleColumn` | `slices/liturgy/ui/components/LiturgyScheduleColumn.tsx` | Cronograma litúrgico (`culto.json`). Permite adicionar, remover, reordenar (drag & drop) e carregar itens. |
| **Workspace (Músicas)** | `SongStanzaGrid` | `slices/songs/ui/components/SongStanzaGrid.tsx` | Grade com as seções semânticas da música ativa (Versos, Coros, Pontes), com destaque de preview vs ao vivo. |
| **Workspace (Bíblia)** | `BibleVerseGrid` | `slices/bible/ui/components/BibleVerseGrid.tsx` | Visualizador de Escrituras, capítulos, navegação veloz e seleção com busca FTS5 integrada. |
| **Workspace (Slides)** | `PptxDeckModal` | `slices/pptx/ui/components/PptxDeckModal.tsx` | Navegação visual de miniaturas de lâminas PowerPoint/PDF para seleção e projeção direta. |
| **Monitor Superior Direito** | `OutputMonitorPanel` | `slices/navigation/ui/components/OutputMonitorPanel.tsx` | Preview 16:9 em tempo real espelhando com precisão de pixels a janela do projetor secundário. |
| **Painel Inferior Direito** | `StageControlModal` | `slices/stage/ui/components/StageControlModal.tsx` | Ajuste de cronômetros pastorais, alertas do púlpito e customização do monitor de retorno. |
| **Janela do Projetor** | `ProjectorView` | `apps/ui/src/projector/ProjectorView.tsx` | Renderizador em tela cheia na saída de vídeo secundária (HDMI), sem menus ou cursores visíveis. |
| **Monitor de Retorno** | `StageView` | `slices/stage/ui/components/StageView.tsx` | Visão de alto contraste para cantores e pregador (estrofe atual, próxima estrofe e cronômetro). |
| **Controle Remoto Web** | `PreacherRemoteView` | `slices/remote/ui/components/RemotePairingModal.tsx` | PWA para navegação remota móvel via Wi-Fi pelo pastor ou ministro de louvor. |
| **Console Orquestrador** | `OperatorView` | `apps/ui/src/operator/OperatorView.tsx` | Ponto de composição do console geral do operador em 4 colunas Slate Pro Studio. |

---

## 4. Dicionário de Linguagem Ubíqua e Tabela de Referência Cruzada

Esta tabela mapeia os termos utilizados na prática da igreja versus os identificadores técnicos adotados no código do LumenLite e seus equivalentes legados do Holyrics clássico:

| Termo do Operador (Cabine) | Termo Técnico (LumenLite) | Equivalente Legado (Holyrics Java) | Definição Semântica e Regras de Negócio |
|---|---|---|---|
| **Workspace (Área de Preparação)** | `previewState` / `selectedItem` | Painel de Pré-visualização | Estado local do cliente que permite inspecionar estrofes ou versículos antes de enviá-los ao público. |
| **Live (No Ar / Ao Vivo)** | `ScreenState.text` / `isLive` | Exibição / Saída do Telão | O conteúdo físico que está sendo projetado no telão HDMI e nas saídas NDI/Web no presente instante. |
| **Disparo 1-Clique (Preview)** | `handleSelectStanza` | Clique simples na tabela | No roteiro litúrgico, 1 clique seleciona o item para edição e visualização no Workspace sem afetar o telão. |
| **Disparo Duplo (Ao Vivo)** | `handleTriggerStanza` | Duplo clique / Barra de Espaço | Na grade de estrofes ativas, 1 clique na estrofe ou tecla `Enter` dispara o texto instantaneamente para o Live. |
| **Fundo de Louvor (Motion)** | `ScreenBackground` (`video`/`image`) | Vídeo de Fundo / Tema | Vídeo em loop contínuo e suave ou imagem estática de baixa distração visual projetada atrás do texto. |
| **Mídia em Tela Cheia** | `ScreenMode.MEDIA` | Player de Vídeo / Apresentação | Exibição de avisos em vídeo com áudio original, vídeos institucionais ou banners sem texto de estrofe sobreposto. |
| **Fundo Padrão (Tema do Culto)** | `defaultBackground` | Tema Padrão do Sistema | Fundo configurado nas preferências para ser assumido automaticamente caso a música não especifique um fundo próprio. |
| **Roteiro Litúrgico (Culto)** | `LiturgySchedule` (`culto.json`) | Lista de Apresentação / Culto | Estrutura ordenada de momentos (músicas, avisos, leituras bíblicas, oração) do culto em andamento. |
| **Pânico: Preto** | `ScreenMode.BLANK` | Botão Preto / Atalho F5 | Apagamento imediato do projetor (tela preta absoluta). Utilizado em momentos de silêncio ou falhas graves. |
| **Pânico: Limpar** | `ScreenMode.CLEAR` | Botão Limpar / Atalho F6 | Remove imediatamente o texto do telão, preservando o fundo em movimento ativo de forma suave. |
| **Pânico: Logo** | `ScreenMode.LOGO` | Botão Logo / Atalho F7 | Projeta a insígnia oficial da comunidade centralizada na tela durante transições solenes. |
| **Monitor de Retorno (Palco)** | `StageView` (`/stage`) | Monitor de Retorno / Palco | Tela voltada para o altar com contraste elevado, tamanho de fonte ampliado e previsão da próxima estrofe. |
| **Controle do Pastor** | `PreacherRemote` (`/remote`) | Holyrics Mobile / Remote App | Interface web emparelhada via token de 6 dígitos que autoriza a passagem de slides diretamente do púlpito. |
| **Aviso de Cabine** | `QuickAlert` | Alerta / Notificação na Tela | Mensagem rápida e discreta renderizada em banner inferior (ex.: alerta de veículos, chamados infantis). |
| **Contagem Regressiva** | `ScreenMode.COUNTDOWN` | Cronômetro de Início | Temporizador regressivo projetado com contagem de minutos e segundos até a abertura oficial da reunião. |

---

## 5. Modos da Máquina de Estados Finita (`ScreenMode`)

A FSM implementada em `@lumen/core` dita rigorosamente as regras de exibição e transição no telão:

```
                          +-------------------+
                          |     BLANK (F1)    | <---+ (Blackout total)
                          +---------+---------+     |
                                    |               |
       +----------------------------+---------------+----------------------------+
       |                            |                                            |
       v                            v                                            v
+--------------+             +--------------+                             +--------------+
|  CLEAR (F2)  |             |  LOGO (F3)   |                             |  TEXT (F4)   |
| (Fundo vivo, |             | (Logotipo da |                             | (Letra /     |
| sem texto)   |             |  comunidade) |                             |  Escrituras) |
+-------+------+             +-------+------+                             +-------+------+
        |                            |                                            |
        +----------------------------+--------------------------------------------+
                                     |
                 +-------------------+-------------------+
                 |                                       |
                 v                                       v
          +--------------+                        +--------------+
          |  MEDIA (F5)  |                        |  COUNTDOWN   |
          | (Vídeo com   |                        | (Regressivo  |
          |  áudio 100%) |                        |  de Abertura)|
          +--------------+                        +--------------+
```

### 5.1 Matriz de Comportamento dos Modos:

| `ScreenMode` | Saída no Projetor HDMI | Saída no Monitor de Retorno (Stage) | Prioridade de Áudio |
|---|---|---|---|
| `BLANK` | Tela preta digital absoluta (#000000). | Exibe aviso discreto "TELÃO APAGADO" e mantém relógio visível. | Mudo / Pausado. |
| `CLEAR` | Reproduz o fundo de louvor configurado sem qualquer texto ou legenda. | Exibe a letra no retorno para os músicos não se perderem na harmonia. | Não se aplica. |
| `LOGO` | Exibe o logotipo oficial configurado com fundo estilizado. | Exibe logotipo em miniatura e contadores de tempo ativos. | Não se aplica. |
| `TEXT` | Letra do louvor ou versículo bíblico tipografado sobre o fundo animado. | Estrofe atual em fonte grande + prévia da próxima estrofe abaixo. | Não se aplica. |
| `MEDIA` | Vídeo ou imagem institucional preenchendo 100% da viewport. | Miniatura espelhada do vídeo + barra de progresso de reprodução. | Áudio original ativo e desmutado. |
| `PPTX` | Lâmina da apresentação em resolução nativa com transição de slide. | Slide atual + próximo slide + notas do orador (se existirem). | Não se aplica. |
| `COUNTDOWN` | Temporizador decrescente de alta visibilidade com título configurado. | Cronômetro espelhado sincronizado em milissegundos. | Beep opcional no zero. |

---

## 6. Protocolo de Governança e Auto-Atualização

> **DIRETRIZ MANDATÓRIA PARA HUMANOS E AGENTES:**
> 
> Toda e qualquer modificação na estrutura de pastas, acréscimo de telas ou modais, modificação de rotas ou extensão da Máquina de Estados Finita **exige obrigatoriamente** a atualização síncrona dos seguintes artefatos:
> 
> 1. **Skill do OpenCode:** `.opencode/skills/lumen-domain/SKILL.md`
> 2. **Dicionário Canônico:** `docs/DOMAIN_DICTIONARY.md`
> 3. **Changelog e Checklist Geral:** `docs/CHECKLIST_ROADMAP.md`
> 
> Nenhuma funcionalidade de alto nível ou alteração de convenção de interface deve ser considerada concluída sem que a documentação espacial e de domínio esteja 100% sincronizada.
