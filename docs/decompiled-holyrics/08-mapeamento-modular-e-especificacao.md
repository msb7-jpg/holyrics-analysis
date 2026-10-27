# 08 - Mapeamento Modular e Especificação Funcional Completa

Este documento formaliza o mapeamento completo e a decomposição modular do **Holyrics**, estruturado para servir de guia e especificação técnica para a **reengenharia e recriação do sistema** em uma arquitetura moderna.

---

## 🧭 Visão Estratégica: O que é o Holyrics?

O Holyrics não é apenas um exibidor de slides; ele funciona como uma **Central Integrada de Produção de Culto e Mídia Eclesiástica (_Church Media & Worship Operations Engine_)**.

No software original (Java Swing monoplataforma Windows), as regras de negócio encontram-se acopladas a telas Swing e chamadas de DLLs nativas. Para recriá-lo com sucesso, o sistema é dividido em **8 Domínios Funcionais Independentes (Bounded Contexts)**.

```
┌────────────────────────────────────────────────────────────────────────┐
│                     1. LIVE OUTPUT ENGINE (TELAS)                      │
│      Telão Principal  │  Monitor de Retorno (Stage)  │  Overlay OBS    │
└───────────────────────────────────▲────────────────────────────────────┘
                                    │ (Renderiza)
┌───────────────────────────────────┼────────────────────────────────────┐
│      2. LETRAS & CIFRAS           │         3. MOTOR BÍBLICO           │
│  Letras, estrofes, acordes,       │  Multi-traduções (.bib), busca     │
│  transposição, busca Vagalume     │  rápida, exibição automática       │
├───────────────────────────────────┼────────────────────────────────────┤
│      4. ROTEIRO DE CULTO (HITEM)  │         5. LIVE & BROADCAST        │
│  Playlists, apresentações,        │  Alertas de berçário, cronômetro,  │
│  vídeos, imagens, URLs, PPT       │  streaming transparente (OBS/vMix) │
├───────────────────────────────────┼────────────────────────────────────┤
│      6. OPERAÇÃO ECLESIÁSTICA     │         7. CONECTIVIDADE & REMOTE  │
│  Escalas de equipes, voluntários, │  App Mobile (Holyrics Remote),     │
│  membros, eventos, relatórios PDF │  WebSockets, controle em rede Wi-Fi│
└───────────────────────────────────┼────────────────────────────────────┘
                                    │ (Suporte de mídia)
┌───────────────────────────────────▼────────────────────────────────────┐
│                    8. MEDIA, ÁUDIO & PLAYBACK CORE                     │
│    Loops de vídeo (VLC/FFmpeg), Player de Áudio + EQ, Playback Sinc.   │
└────────────────────────────────────────────────────────────────────────┘
```

---

## 📦 Detalhamento dos 8 Módulos Funcionais

---

### Módulo 1: Motor de Projeção & Saídas de Vídeo (_Live Output Engine_)

_Responsável por gerenciar os dispositivos físicos de exibição, renderização gráfica e composições visuais._

- **Detecção e Controle de Monitores:**
    - Reconhecimento automático de múltiplos monitores físicos (`ScreenUtils.java`, `ScreenDeviceSettings.java`).
    - Saída primária (Interface do Operador) vs. Saída secundária (Projetor/TV HDMI/VGA).
- **Modo Exclusivo de Exibição:**
    - Fixação da janela em tela cheia sem bordas (_Borderless Windowed_ / `WS_POPUP`), impedindo que o cursor do mouse ou janelas do SO vazem para o público.
- **Monitor de Retorno / Palco (_Stage View / Stage Display_):**
    - Tela dedicada para músicos, pastores e equipes no púlpito (`StageViewUtils.java`).
    - Exibe: slide atual, **próximo slide em destaque**, relógio de cabeçalho, cronômetro de pregação e avisos exclusivos para a equipe do palco.
- **Freeze (Congelamento de Projeção):**
    - Recurso de congelar a imagem atual na tela secundária (`Freeze.java`), permitindo que o operador pesquise outros hinos ou altere configurações sem que o público veja.
- **Ações Globais de Emergência (_Blackout & Clear_):**
    - _Blackout Geral:_ Tela preta imediata.
    - _Clear Text:_ Oculta o texto e preserva o vídeo/imagem de fundo em execução.
    - _Clear Background:_ Oculta o fundo e preserva apenas o texto.
- **Motor Tipográfico & Auto-Fit:**
    - Cálculo dinâmico do tamanho da fonte (`PresentationRendererUtils.java`) para impedir quebra de layout e extravasamento de margens.
    - Aplicação de contorno tipográfico (_stroke/outline_) e sombra projetada (_drop shadow_) para garantir contraste e legibilidade sobre qualquer mídia.
- **Temas e Estilos Visuais:**
    - Margens customizáveis (esquerda, direita, topo, rodapé), alinhamentos (horizontal: esquerda/centro/direita; vertical: topo/meio/base), gradientes e paletas de cores.

---

### Módulo 2: Gestão de Músicas, Letras & Cifras (_Music & Chords Engine_)

_Responsável pelo acervo musical e apoio instrumental._

- **Estruturação Semântica de Letras:**
    - Identificação de blocos: Estrofe (_Verse_), Coro/Refrão (_Chorus_), Ponte (_Bridge_), Pré-coro (_Pre-Chorus_), Final (_Outro_).
    - Ordem de execução personalizada (ex: `V1 -> C -> V2 -> C -> B -> C`).
- **Cifras e Acordes Musicais:**
    - Reconhecimento e destaque automático de notas e cifras no texto.
    - **Transposição Dinâmica de Tonalidade:** Elevação e rebaixamento de semitons para adaptar a música ao tom do ministro de louvor instantaneamente.
- **Mecanismo de Busca Instantânea:**
    - Busca por título, trecho da letra, autor, compositor ou tags temáticas (`MusicSearchPanel.java`).
    - Busca rápida por histórico de uso recente e favoritos.
- **Base Offline & Operações em Lote:**
    - Base local embutida com hinos tradicionais e músicas contemporâneas (`DBOffLine.java`).
    - Importação em lote (`MusicBatchImportView.java`) e exportação (`MusicBatchExport.java`).
- **Integração Online com API Vagalume:**
    - Consulta remota para download automático de letras, cifras e metadados de artistas com 1 clique (`com.limagiran.vagalumeapi`).
- **Histórico e Métricas de Execução:**
    - Gravação do registro de execução de cada música com data/horário e culto correspondente (`ReportPlayedMusicsView.java`, `History.java`).

---

### Módulo 3: Motor Bíblico & Escrituras (_Bible Engine_)

_Sistema de consulta e projeção de passagens bíblicas com alta performance._

- **Múltiplas Traduções Concorrentes:**
    - Suporte ao formato binário proprietário `.bib` (`FactoryBible.java`, `pt_acrf.bib`), que oferece leitura randômica instantânea sem sobrecarga de banco de dados.
- **Parser de Referências Abreviadas (_Quick Jump_):**
    - Busca por abreviações rápidas digitadas pelo operador (ex: `jo 3 16` -> João 3:16; `sl 23 1-4` -> Salmos 23:1-4).
- **Busca Textual por Palavra-Chave:**
    - Algoritmo de busca por termos com filtros por Antigo/Novo Testamento e livros específicos (`BibleFind.java`).
- **Modos de Apresentação:**
    - Exibição versículo por versículo ou agrupamento contínuo em parágrafos.
    - Transição dinâmica de versões bíblicas em tempo real com o mesmo versículo na tela.
- **Gerenciador de Downloads Bíblicos:**
    - Download online e instalação de novas traduções diretamente pela interface (`DownloadView.java`).

---

### Módulo 4: Roteiro de Culto & Apresentação (_Service Planner & HItem_)

_Montagem do cronograma e liturgia do culto com elementos multimídia._

- **Modelo Polimórfico de Itens (`IHItem`):**
    - Padronização de qualquer recurso projetável em uma mesma interface (`HItemResolve.java`):
        1. `HItemMusic`: Letras de músicas com temas vinculados.
        2. `HItemVerse`: Versículos e passagens bíblicas.
        3. `HItemAnnouncement`: Avisos da igreja com temporizador de troca automática.
        4. `HItemImage`: Imagens fixas e cartazes de eventos (`.jpg`, `.png`).
        5. `HItemVideo`: Vídeos informativos, testemunhos e trailers.
        6. `HItemAudio`: Trilhas sonoras e instrumentais de fundo.
        7. `HItemPlayBack`: Faixas de playback sincronizadas com slides.
        8. `HItemURL`: Renderização e exibição de páginas web ao vivo.
- **Importação de Microsoft PowerPoint:**
    - Importação e conversão de apresentações `.ppt` e `.pptx` em slides de imagem para projeção integrada (`PowerPointImportView.java`, `MSOfficeUtils.java`).
- **Listas de Culto e Favoritos:**
    - Salvamento e carregamento de listas prévias para cultos específicos (ex: "Culto de Domingo - Manhã", "Culto de Santa Ceia").

---

### Módulo 5: Transmissão ao Vivo, Alertas & Broadcast (_Live Broadcast & Overlays_)

_Comunicação direta com a congregação e integração com estúdios de transmissão._

- **Streaming Transparente (OBS Studio / vMix / Wirecast):**
    - Servidor web local embutido (`HolyricsStreaming.java`) que gera uma página HTML transparente (_Alpha Channel / Chroma Key_).
    - Adicionado no software de streaming como fonte de navegador (_Browser Source_), permitindo legendagem em tempo real sobre a câmera.
- **Alertas Sobrepostos na Tela:**
    - Notificações instantâneas exibidas como tarjas/banners sobre o slide ativo (`AlertSettingsDialog.java`):
        - Chamada de pais ao berçário.
        - Avisos de veículos bloqueando passagem.
        - Recados urgentes da liderança.
    - Customização de tempo de exibição, repetição, cores e avisos sonoros discretos.
- **Cronômetro e Contagem Regressiva (_Countdown_):**
    - Contagem regressiva pré-culto com fundos dinâmicos (`CountdownDialog.java`).
    - Cronômetro de tempo de pregação sincronizado com o monitor de retorno.
- **Relógio de Cabeçalho:**
    - Exibição opcional e configurável do horário atual na tela ou no palco (`ClockSettingsDialog.java`).

---

### Módulo 6: Gestão Eclesiástica & Escalas (_Church Operations_)

_Funcionalidades administrativas de apoio aos ministérios e voluntários._

- **Cadastro de Membros & Funções:**
    - Banco de voluntários com telefones, e-mails e atribuições (Vocal, Bateria, Baixo, Teclado, Áudio, Projeção, Câmera) (`Member.java`, `EditMember.java`).
- **Matriz de Escalas de Voluntários:**
    - Criação de grades semanais/mensais por evento/culto (`ScheduleView.java`, `SchedulePanelGrid.java`).
    - Identificação de conflitos e histórico de escalas passadas.
- **Agenda de Eventos:**
    - Calendário de ensaios, vigílias, conferências e reuniões de liderança (`Event.java`).
- **Geração e Impressão de Relatórios:**
    - Exportação de escalas e histórico de músicas cantadas em relatórios PDF (`PrintUtils.java`) e planilhas Excel (`Excel.java`, `TableToExcel.java`).

---

### Módulo 7: Conectividade Remota & Ecossistema (_Network & Mobile_)

_Controle da apresentação a distância via rede local Wi-Fi._

- **Servidor de Conectividade Local:**
    - Servidor Socket/HTTP na porta de rede local (`HolyricsWS.java`, `UDPServer.java`).
    - Pareamento do aplicativo móvel Holyrics Remote (Android/iOS) com o computador de projeção.
- **Operações Disponíveis no Controle Remoto:**
    - Avançar e retroceder slides da música em execução.
    - Busca e projeção direta de passagens bíblicas.
    - Visualização da lista de músicas do culto e envio de avisos e alertas emergenciais diretamente pelo smartphone/tablet.

---

### Módulo 8: Mídia, Áudio & Playback Core (_Media Engine_)

_Processamento de mídias pesadas, áudio de transição e animações._

- **Reprodução de Vídeos de Fundo em Loop:**
    - Execução contínua sem travamentos (_seamless loop_) de backgrounds animados em alta definição sob o texto.
- **Player de Áudio Dedicado:**
    - Reprodutor de música ambiente para pré e pós-culto com playlist, crossfade suave e equalizador gráfico de 10 bandas (`AudioPlayerPanel.java`, `Equalizer.java`).
- **Playback Sincronizado:**
    - Módulo para execução de faixas de playback com avanço temporizado de slides sincronizado com a trilha de áudio (`PlayBack.java`, `EditPlayBackDialog.java`).
- **Processador FFmpeg Embutido:**
    - Extração de miniaturas para visualização rápida no painel (`ThumbMediaController.java`), conversão de mídias e exportação de slides para vídeo (`FFMPEG.java`, `ExportToVideoDialog.java`).

---

## 📑 Modelo Padrão de Ficha de Especificação Funcional

Para manter o mapeamento padronizado ao detalhar cada recurso antes de programar, utiliza-se a seguinte estrutura:

```markdown
### [ID-MODULO] Nome da Funcionalidade

1. **Objetivo & Visão de UX:** O que o operador vê e faz? Quais atalhos de teclado são utilizados?
2. **Entradas & Saídas:** Quais parâmetros entram no sistema e qual é o resultado na tela/banco?
3. **Regras de Negócio & Algoritmos:** Qual a lógica matemática, transposição ou validação aplicada?
4. **Estrutura de Dados:** Quais tabelas SQLite ou arquivos (.json/.bib/.hly) são acessados?
5. **Classes de Referência no Holyrics:** Quais classes em `src/` implementavam essa regra?
```

---

## 🛠 Proposta de Arquitetura Moderna para Recriação

| Componente                  | Holyrics Original (Legado)            | Arquitetura Proposta para o "Novo Holyrics"    | Vantagens da Nova Abordagem                                                                                |
| --------------------------- | ------------------------------------- | ---------------------------------------------- | ---------------------------------------------------------------------------------------------------------- |
| **Runtime Desktop**         | Java 8/21 + Wrapper PE32 (.exe)       | **Tauri 2.0 (Rust) ou Electron**               | Inicialização ultrarrápida, baixo consumo de RAM, compatível nativamente com Windows, Linux e macOS.       |
| **Interface do Usuário**    | Java Swing + WebLaF Look & Feel       | **React / Vue / Svelte + TailwindCSS**         | Suporte a temas escuros modernos para cabines de som, layout responsivo e desacoplamento de componentes.   |
| **Camada de Projeção**      | Java Graphics 2D (Software Rendering) | **HTML5 Canvas / WebGL / CSS Subgrid**         | Renderização 4K fluida a 60fps com aceleração de hardware nativa de GPU.                                   |
| **Banco de Dados**          | SQLite via Xerial JDBC e DLL C        | **SQLite Nativo (Tauri SQL / Better-SQLite3)** | Zero dependência de JVM; consultas rápidas e tipadas com ORMs modernos (Kysely / Drizzle).                 |
| **Formato de Exportação**   | `.hly` binário serializado Java       | **JSON/YAML compactados em ZIP (.zip)**        | Formato aberto, auditável, de fácil backup e integração com outros softwares (ProPresenter, OpenLP).       |
| **Projeção de Palco e OBS** | Servidor HTTP embutido básico         | **WebSockets + Server-Sent Events (SSE)**      | Stage View e Overlay do OBS abertos diretamente no navegador de tablets, smartphones e OBS Studio via URL. |
