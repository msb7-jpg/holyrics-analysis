# 1. Requisitos do Sistema & Engenharia Reversa: LumenLite (Holyrics Modernizado)

Este documento estabelece os **Requisitos Funcionais (RF)** e **Requisitos Não-Funcionais (RNF)** para a recriação e modernização do **Holyrics**, preservando **100% das suas funcionalidades essenciais de mídia, liturgia e operação** (excluindo cadastros de membros e cifras/transposição de acordes para focar na agilidade da cabine de mídia).

Para cada subsistema, este documento fornece um guia completo de engenharia reversa:
1. **Requisitos Funcionais Detalhados:** Especificação exata do comportamento esperado.
2. **🕵️ Como buscar e inspecionar no código original:** Mapeamento de classes em `src/`, padrões e comandos para consulta rápida.
3. **⚠️ Análise da Abordagem Legada & Problemas Encontrados:** Diagnóstico de gargalos, acoplamentos e limitações do código Java Swing.
4. **🚀 Nova Abordagem & Melhorias:** Como implementar o mesmo recurso de forma moderna, performática e fluida utilizando a stack definida (Tauri v2, React 19, Bun, SQLite, WebSockets e DAG Hooks).

---

## 2. Requisitos Funcionais por Módulo

---

### Módulo 1: Motor de Projeção & Saídas de Vídeo (Live Output Engine)

Responsável pelo controle das telas de exibição para o público e palco, cálculos tipográficos e renderização em tempo real.

| ID | Nome do Requisito | Descrição Detalhada | Fatias Afetadas |
|---|---|---|---|
| **RF-01** | **Controle Multi-Monitor** | Detecção automática de saídas de vídeo (HDMI/VGA/DisplayPort). Fixação da janela do projetor em tela cheia sem bordas na segunda saída, mantendo a interface do operador na tela primária. | `slices/navigation` |
| **RF-02** | **Monitor de Retorno (Stage View)** | Tela dedicada para púlpito com layout de alto contraste: exibe slide atual, **próximo slide em destaque**, relógio de cabeçalho, cronômetro de pregação e notas do orador. | `slices/stage` |
| **RF-03** | **Ações de Emergência (Pânico)** | Comandos instantâneos de corte: <br>• **Blackout:** Telão 100% preto com fade de áudio suave. <br>• **Clear Text:** Oculta letras/textos, mantendo o vídeo/imagem de fundo em loop. <br>• **Clear Background:** Oculta o fundo e preserva apenas o texto. | `packages/core`, `slices/navigation` |
| **RF-04** | **Freeze (Congelar Projeção)** | Congela a imagem exibida no telão secundário, permitindo que o operador pesquise outros hinos, troque configurações ou edite a liturgia sem vazar alterações para o público. | `slices/navigation` |
| **RF-05** | **Motor Tipográfico & Auto-Fit** | Cálculo dinâmico do tamanho da fonte em tempo real para que nenhuma palavra ou estrofe ultrapasse as margens da tela, com suporte a contorno de texto (*stroke/outline*) e sombra projetada (*drop shadow*) para garantir legibilidade sobre qualquer fundo. | `slices/navigation` |
| **RF-06** | **Temas e Estilos Visuais** | Personalização de margens (esquerda, direita, topo, rodapé), alinhamento horizontal (esquerda, centro, direita), alinhamento vertical (topo, meio, base), cores e gradientes. | `slices/navigation` |

#### 🕵️ Como buscar e inspecionar no código original (Holyrics):
- **Classes-chave:**
  - `src/com/limagiran/holyrics/util/PresentationRendererUtils.java`: Algoritmo de cálculo tipográfico e renderização 2D.
  - `src/com/limagiran/holyrics/util/ScreenUtils.java` & `ScreenDeviceSettings.java`: Enumeração e posicionamento de monitores.
  - `src/com/limagiran/holyrics/util/Freeze.java`: Lógica de buffer estático para congelamento de tela.
  - `src/com/limagiran/holyrics/control/ThemeController.java`: Gestão de margens, fontes, cores e alinhamento.
  - `src/com/limagiran/holyrics/util/StageViewUtils.java`: Componentes visuais do monitor de retorno.
- **Comandos de busca úteis:**
  ```bash
  grep -rn "Graphics2D" src/
  grep -rn "FontMetrics" src/
  grep -rn "ScreenDevice" src/
  grep -rn "Freeze" src/
  ```

#### ⚠️ Análise da Abordagem Legada & Problemas Encontrados:
- **Renderização por Software na CPU:** O Holyrics original renderiza cada frame de texto usando `java.awt.Graphics2D` e `FontMetrics.stringWidth()`. Todo cálculo de quebra de linha roda na thread gráfica principal do Swing (EDT - *Event Dispatch Thread*).
- **Quedas de Frame e Congelamentos:** Quando uma música longa é carregada ou um slide com muitas linhas é trocado, a medição síncrona de fontes causa engasgos visíveis no vídeo de fundo.
- **Acoplamento Monolítico do Monitor:** A janela secundária do projetor (`JWindow`) roda no mesmo processo e thread pool que a janela principal de controle. Se o painel do operador travar, a projeção do culto congela.
- **Gerenciamento de Estados via Flags Dispersas:** Estados como "em blackout", "texto limpo" ou "tela congelada" são controlados por dezenas de variáveis booleanas espalhadas em diferentes controladores, gerando inconsistências visuais frequentes.

#### 🚀 Nova Abordagem & Melhorias (LumenLite):
- **Aceleração 100% por GPU:** A renderização das letras e vídeos ocorre através de **HTML5 Canvas / CSS Transforms** acelerados nativamente pela GPU no WebView do Tauri v2. O consumo de CPU cai para próximo de zero.
- **Auto-Fit sem Layout Thrashing:** Em vez de medições manuais pixel a pixel, a tipografia dinâmica utiliza **CSS Container Queries** e `ResizeObserver` nativo, ajustando o tamanho do texto instantaneamente em sub-milissegundos.
- **Processos de Janela Isolados:** A janela do operador e a janela do telão (`/projector`) são instâncias de WebViews desacopladas gerenciadas pelo Tauri. Se a interface de edição sofrer qualquer sobrecarga, o telão permanece rodando a 60 FPS cravados.
- **Pilha de Camadas Determinística:** O telão é composto por camadas independentes (`Background`, `Foreground`, `Overlay`), eliminando flags soltas e garantindo transições perfeitamente suaves.

---

### Módulo 2: Gestão de Músicas & Letras (Music Engine)

Responsável pelo acervo de louvores, organização semântica das letras, buscas ultrarrápidas e consultas online ao Vagalume.

| ID | Nome do Requisito | Descrição Detalhada | Fatias Afetadas |
|---|---|---|---|
| **RF-07** | **Estrutura Semântica de Letras** | Identificação e divisão automática de blocos: Estrofe (*Verse*), Coro/Refrão (*Chorus*), Ponte (*Bridge*), Pré-Coro (*Pre-Chorus*) e Final (*Outro*), permitindo ordem de execução dinâmica (ex: `V1 -> C -> V2 -> C -> B -> C`). | `slices/songs` |
| **RF-08** | **Busca Instantânea FTS5** | Mecanismo de busca ultrarrápido por título, trecho da letra, autor, compositor ou tags temáticas, respondendo em menos de 2ms com destaque dos termos encontrados. | `slices/songs` |
| **RF-09** | **Base Offline Embutida** | Base local embarcada contendo centenas de hinos tradicionais e músicas contemporâneas prontas para uso sem internet desde o primeiro boot. | `slices/songs` |
| **RF-10** | **Integração Online API Vagalume** | Consulta online integrada para baixar letras completas e metadados de artistas com apenas 1 clique diretamente na janela de cadastro/edição de músicas. | `slices/songs` |
| **RF-11** | **Importador Completo do Holyrics** | Leitura de backups `.zip` ou pastas do Holyrics legado, higienização de tags proprietárias e importação integral das músicas para o novo banco de dados. | `slices/songs` |
| **RF-12** | **Histórico e Relatório de Execução** | Gravação automática de cada música executada no culto com data, hora e frequência de uso para relatórios e planejamento litúrgico. | `slices/songs` |

#### 🕵️ Como buscar e inspecionar no código original (Holyrics):
- **Classes-chave:**
  - `src/com/limagiran/holyrics/control/MusicController.java`: Controlador central de músicas e letras.
  - `src/com/limagiran/holyrics/model/Paragraph.java`: Estrutura de parágrafos de letra e identificação de estrofes.
  - `src/com/limagiran/holyrics/dao/MusicDao.java`: Operações SQL diretas sobre a tabela `musics` do SQLite.
  - `src/com/limagiran/vagalumeapi/VagalumeAPI.java`: Cliente HTTP REST para consulta ao serviço Vagalume.
  - `src/com/limagiran/holyrics/file/db-offline.hly` & `db-offline-best.hly`: Bases pré-empacotadas de músicas offline.
  - `src/com/limagiran/holyrics/control/History.java`: Histórico de execução de hinos.
- **Comandos de busca úteis:**
  ```bash
  grep -rn "vagalume" src/ -i
  grep -rn "musics" src/com/limagiran/holyrics/dao/
  grep -rn "Paragraph" src/
  ```

#### ⚠️ Análise da Abordagem Legada & Problemas Encontrados:
- **Busca por SQL `LIKE` Ineficiente:** As consultas no banco SQLite utilizam cláusulas `SELECT * FROM musics WHERE lyrics LIKE '%termo%'`. Em acervos grandes com mais de 3.000 músicas, isso força um *Full Table Scan*, travando a interface enquanto o usuário digita.
- **Bloqueio de Rede no Vagalume:** As chamadas HTTP à API Vagalume ocorrem de forma síncrona ou em `SwingWorkers` básicos; se a conexão oscilar, a janela de pesquisa do operador trava esperando o timeout.
- **Tags Proprietárias Sujas:** O banco de dados do Holyrics armazena a formatação das letras misturada com tags XML proprietárias (`<c>`, `<t>`, `<p>`), dificultando a exportação e interoperabilidade com outros sistemas.

#### 🚀 Nova Abordagem & Melhorias (LumenLite):
- **SQLite FTS5 com Ranking BM25:** A tabela de músicas é indexada nativamente com o motor **Full-Text Search (FTS5)** do SQLite em modo WAL. A pesquisa por qualquer trecho de letra responde em menos de 1ms, com suporte a busca fonética e sem distinção de acentos.
- **Busca Assíncrona com TanStack Query:** As consultas ao banco local e à API do Vagalume utilizam chamadas desacopladas com cancelamento automático (`AbortController`). Se o operador digitar novas letras rapidamente, requisições antigas são abortadas instantaneamente, poupando CPU e rede.
- **Formato Limpo e Semântico:** Letras armazenadas em formato semântico puro (JSON estruturado ou texto limpo com marcadores padronizados), facilitando backups e migrações.

---

### Módulo 3: Motor Bíblico & Escrituras (Bible Engine)

Responsável pela consulta, busca por termos, chaveamento e projeção de passagens bíblicas com resposta instantânea.

| ID | Nome do Requisito | Descrição Detalhada | Fatias Afetadas |
|---|---|---|---|
| **RF-13** | **Múltiplas Traduções Concorrentes** | Suporte offline a diversas versões consagradas da Bíblia (NVI, NAA, ARA, ARC, NVT, ACF, etc.) com troca instantânea de versão em tempo real. | `slices/bible` |
| **RF-14** | **Projeção em Tela Dividida (Dual Version)** | Exibição simultânea de duas versões bíblicas diferentes lado a lado na tela (ex: NVI ao lado de ARA), mantendo a sincronização de versículos. | `slices/bible`, `slices/navigation` |
| **RF-15** | **Parser de Referências (Quick Jump)** | Localização imediata a partir de referências abreviadas digitadas pelo operador no Spotlight (ex: `jo 3 16` $\rightarrow$ João 3:16; `sl 23 1-4` $\rightarrow$ Salmos 23:1 a 4). | `slices/bible` |
| **RF-16** | **Busca Textual por Palavra-Chave** | Motor de pesquisa textual por palavras ou frases em toda a Bíblia, com filtros por testamento (Antigo/Novo Testamento) e livros específicos. | `slices/bible` |
| **RF-17** | **Gerenciador de Downloads Bíblicos** | Interface para consulta, download e instalação de novas traduções bíblicas diretamente da nuvem sem reiniciar a aplicação. | `slices/bible` |

#### 🕵️ Como buscar e inspecionar no código original (Holyrics):
- **Classes-chave:**
  - `src/com/limagiran/bible/Bible.java`: Estrutura do cânon bíblico (livros, capítulos, versículos).
  - `src/com/limagiran/bible/FactoryBible.java`: Leitor do formato binário proprietário `.bib`.
  - `src/com/limagiran/bible/BibleFind.java`: Algoritmo de busca por termos e referências.
  - `src/com/limagiran/bible/download/DownloadView.java` & `AvailableDownloads.java`: Tela de download de novas versões.
  - `src/com/limagiran/bible/database/pt_acrf.bib`: Arquivo binário de exemplo da Bíblia (Almeida Corrigida e Fiel).
- **Comandos de busca úteis:**
  ```bash
  grep -rn "\.bib" src/
  grep -rn "FactoryBible" src/
  grep -rn "BibleFind" src/
  grep -rn "AvailableVersions" src/
  ```

#### ⚠️ Análise da Abordagem Legada & Problemas Encontrados:
- **Formato Binário Proprietário `.bib`:** O Holyrics armazena cada Bíblia em um arquivo binário customizado (`.bib`) lido via `RandomAccessFile`. Embora rápido para leitura posicional, esse formato é fechado, não permite consultas relacionais avançadas e está sujeito a bloqueios de leitura de arquivo (*file locks*) no Windows.
- **Parser Frágil de Referências:** O reconhecimento de atalhos como `jo 3 16` é feito através de múltiplos blocos condicionais e manipulação manual de substrings. Referências com intervalos complexos (ex: `1co 13 4-8`) frequentemente falham ou exigem pontuação rígida.
- **Busca Textual Lenta:** Como o formato `.bib` não possui índice invertido, a pesquisa por palavras-chave varre linearmente os 31.102 versículos da Bíblia em memória, gerando picos de consumo de CPU.

#### 🚀 Nova Abordagem & Melhorias (LumenLite):
- **Bíblias em SQLite com FTS5:** Todas as traduções bíblicas residem no banco de dados SQLite local, indexadas com tabelas virtuais FTS5. A busca por qualquer termo em toda a Bíblia é executada em menos de 3ms.
- **Micro-Parser Sintático em TypeScript:** Implementação de um analisador léxico determinístico com dicionário canônico dos 66 livros da Bíblia e suas abreviações populares em português. Suporta sintaxe flexível com ou sem dois-pontos e intervalos contínuos (`sl 23 1-6`, `rm 8 28`).
- **Modo Dual Version Nativo no Renderizador:** A janela do telão suporta a divisão de versículos lado a lado via CSS Grid responsivo com tipografia ajustada individualmente, garantindo legibilidade perfeita mesmo com extensões de texto diferentes entre as traduções.

---

### Módulo 4: Roteiro de Culto & Apresentações Multimídia (Service Planner & HItems)

Responsável pela montagem da liturgia completa do culto com suporte polimórfico a diferentes tipos de mídia.

| ID | Nome do Requisito | Descrição Detalhada | Fatias Afetadas |
|---|---|---|---|
| **RF-18** | **Sistema Polimórfico de Itens (HItems)** | Capacidade de projetar qualquer tipo de item litúrgico sob a mesma interface unificada: <br>• **Música:** Letras com temas associados. <br>• **Versículo:** Passagens bíblicas. <br>• **Aviso:** Slides informativos com avanço automático configurável. <br>• **Imagem:** Cartazes e banners (`.jpg`, `.png`, `.webp`). <br>• **Vídeo:** Vídeos em tela cheia com áudio ou loops de background. <br>• **Áudio:** Trilhas sonoras e instrumentais de fundo. <br>• **Playback:** Trilha de áudio com slides de letra sincronizados. <br>• **URL:** Renderização de páginas web ao vivo na projeção. | `slices/liturgy`, `slices/pptx` |
| **RF-19** | **Culto como Código (`culto.json`)** | Roteiro completo do culto salvo em arquivo declarativo JSON com validação estrita via JSON Schema, permitindo versionamento no Git e compartilhamento imediato. | `slices/liturgy` |
| **RF-20** | **Pilha de Interrupção (Return Stack)** | Ao projetar um item de improviso (ex: pastor pede um versículo inesperado no meio do louvor), o sistema salva a posição exata anterior e permite retornar à música instantaneamente com um clique ou pressionando `Esc`/`Backspace`. | `packages/core`, `slices/navigation` |
| **RF-21** | **Ingestão Nativa de PowerPoint (PPTX) & PDF** | Leitura direta de apresentações sem depender do Microsoft Office instalado: conversão acelerada dos slides em imagens de alta resolução e extração em XML das notas do orador para exibição no monitor de retorno. | `slices/pptx` |
| **RF-22** | **Playback Sincronizado** | Execução de faixas de playback musical com temporizadores individuais por slide, permitindo avanço automático das letras em perfeita sincronia com a música. | `slices/songs`, `slices/liturgy` |
| **RF-22A** | **Projeção Rápida de Texto Avulso (Quick Text)** | Mecanismo de entrada e projeção instantânea de texto livre de improviso ou emergência (ex.: recado pastoral urgente, aviso de veículo ou oração espontânea). Permite ao operador digitar ou colar uma frase e lançá-la diretamente no telão sob o tema visual ativo, sem a obrigatoriedade de salvar no banco de dados de músicas ou criar um item permanente na liturgia. | `packages/core`, `slices/navigation` |
| **RF-22B** | **Ações de Contexto no Roteiro Litúrgico (Right-Click Context Menu)** | Capacidade de interagir com o roteiro de culto (`culto.json`) através de menu de contexto acionado pelo botão direito do mouse, permitindo inserção e manuseio rápido de itens polimórficos categorizados essencialmente em: <br>• **Mídia:** Inserção rápida de arquivos de imagem (`.png`, `.jpg`, `.webp`), vídeos em tela cheia/background, trilhas de áudio ou músicas/letras com mídia associada. <br>• **Anotações:** Inserção de anotações textuais livres de púlpito, avisos, versículo bíblico único, lista de versículos bíblicos encadeados para a pregação e acionadores de cronômetro/contagem regressiva. | `slices/liturgy`, `slices/hitems` |
| **RF-22C** | **Ingestão por Arraste e Solte no Roteiro (Universal Drag & Drop)** | Capacidade de compor a liturgia arrastando e soltando elementos diretamente sobre a lista do roteiro de culto (`culto.json`). O despachante identifica automaticamente o tipo do item e o insere na posição solta: <br>• **Letras/Louvores:** Arrastadas da lista de músicas da biblioteca para a liturgia. <br>• **Mídias e Arquivos Externos:** Arquivos arrastados diretamente do explorador de arquivos do sistema operacional (imagens `.jpg`/`.png`/`.webp`, vídeos `.mp4`/`.webm`, áudios `.mp3`/`.wav` ou apresentações `.pptx`/`.pdf`). O sistema copia para o cache de mídia e cria o nó polimórfico correspondente no roteiro. <br>• **Reordenação Interna:** Arraste e solte entre itens já presentes no roteiro para reordenar a sequência de execução do culto em tempo real. | `slices/liturgy`, `slices/hitems`, `slices/media` |

#### 🕵️ Como buscar e inspecionar no código original (Holyrics):
- **Classes-chave:**
  - `src/com/limagiran/holyrics/hitem/`: Pacote contendo as implementações polimórficas de itens (`HItemMusic.java`, `HItemVerse.java`, `HItemAnnouncement.java`, `HItemImage.java`, `HItemVideo.java`, `HItemAudio.java`, `HItemPlayBack.java`, `HItemURL.java`).
  - `src/com/limagiran/holyrics/hitem/HItemResolve.java`: Despachante que identifica e resolve o tipo de cada item para projeção.
  - `src/com/limagiran/holyrics/util/PopUpMenu.java` & `HItemUtils.java`: Ações de menu de contexto e inserção rápida de itens.
  - `src/com/limagiran/holyrics/view/QuickPresentationDialog.java`: Apresentação rápida de texto livre de emergência.
  - `src/com/limagiran/holyrics/playback/PlayBack.java` & `EditPlayBackDialog.java`: Motor e editor de playbacks sincronizados.
  - `src/com/limagiran/holyrics/view/PowerPointImportView.java` & `MSOfficeUtils.java`: Importação de arquivos `.ppt` e `.pptx`.
  - `src/com/limagiran/holyrics/control/WorshipController.java`: Orquestrador da lista de culto em execução.
- **Comandos de busca úteis:**
  ```bash
  grep -rn "HItem" src/
  grep -rn "HItemResolve" src/
  grep -rn "QuickPresentation" src/
  grep -rn "schedulePopUpHItemPlayList" src/
  grep -rn "PowerPoint" src/
  grep -rn "PlayBack" src/
  ```

#### ⚠️ Análise da Abordagem Legada & Problemas Encontrados:
- **Dependência do Pacote Office via Automação COM:** No Holyrics, a importação de PowerPoint depende da instalação física do Microsoft Office na máquina do operador e utiliza pontes nativas COM do Windows. Isso causa travamentos frequentes, janelas invisíveis do PowerPoint que não fecham e incompatibilidade no Linux/macOS.
- **Arquivos Proprietários `.hly` Binários:** Os cultos salvos utilizam o formato `.hly` baseado em serialização binária Java (`ObjectOutputStream`). Qualquer pequena alteração na versão de uma classe Java invalida o arquivo salvo, impedindo compatibilidade entre versões diferentes do programa.
- **Playback com Temporização Frágil:** A sincronização de slides no playback depende de `java.util.Timer` do Swing. Se a thread gráfica travar por 1 segundo durante uma busca, o tempo do slide dessincroniza da faixa de áudio.
- **Drag & Drop e Menus Rígidos no Swing:** O sistema legado utiliza implementações antigas de `TransferHandler` e `JPopupMenu` do Swing. A inserção de arquivos externos frequentemente trava ao calcular metadados na thread de UI, e menus de contexto são engessados em diálogos modais pesados.

#### 🚀 Nova Abordagem & Melhorias (LumenLite):
- **Extração Nativa de PPTX com `fflate` + `fast-xml-parser`:** Como arquivos `.pptx` são arquivos ZIP contendo XMLs e imagens, o LumenLite extrai e converte os slides diretamente em memória sem tocar no Microsoft Office. Funciona 100% no Linux, Windows e macOS sem nenhuma dependência externa.
- **Roteiro em JSON Declarativo:** Os cultos são salvos em `culto.json`, um formato de texto aberto, humano e legível, com autocompletion e validação nativa em qualquer editor moderno via JSON Schema.
- **Áudio com Web Audio API Precisa:** A sincronização de playbacks utiliza o clock de alta precisão da **Web Audio API** do navegador, garantindo que o avanço dos slides seja perfeitamente fiel ao milissegundo do áudio, mesmo sob carga pesada no sistema.
- **Drag & Drop e Context Menu Nativos Ultrarrápidos:** A captura de arquivos externos do OS e a reordenação da liturgia utilizam a API nativa de Drag & Drop do HTML5 combinada com eventos de arquivos do Tauri v2. A ingestão de mídias pesadas processa hashes e metadados de forma puramente assíncrona em segundo plano, garantindo que a interface permaneça a 60 FPS sem engasgos.

---

### Módulo 5: Transmissão ao Vivo, Palco & Conectividade (Broadcast & Remote)

Responsável pela comunicação em rede local, integração com softwares de transmissão (OBS Studio/vMix) e controle remoto via dispositivos móveis.

| ID | Nome do Requisito | Descrição Detalhada | Fatias Afetadas |
|---|---|---|---|
| **RF-23** | **Streaming Transparente para OBS (Browser Source)** | Servidor web local embutido que disponibiliza uma rota com canal alfa transparente (`/broadcast`), permitindo adicionar o telão diretamente como fonte de navegador (*Browser Source*) no OBS Studio, vMix ou Wirecast para legendagem ao vivo. | `slices/broadcast` |
| **RF-24** | **Alertas Instantâneos & Lower Thirds** | Sistema de avisos sobrepostos ao telão (banners inferiores/superiores) para mensagens emergenciais: chamada de pais ao berçário, placa de veículo bloqueando passagem e recados pastorais, com temporizador de auto-ocultação. | `slices/stage` |
| **RF-25** | **Cronômetros & Contagens Regressivas** | Módulo de contagem regressiva pré-culto (*countdown*) e cronômetro de tempo de pregação sincronizado em tempo real com o monitor de retorno dos pastores. | `slices/stage` |
| **RF-26A** | **Endpoint Web do Pregador / Pastor (`/remote/preacher`)** | Interface web responsiva e focada na ministração, acessível via navegador em smartphone/tablet na rede local com token restrito. Permite: <br>• Avanço e recuo imediato de slides da apresentação ativa (PowerPoint/PDF/Letra/Versículos). <br>• Exibição em tempo real do slide atual e do próximo slide em destaque. <br>• Sincronização do cronômetro de pregação e exibição das anotações do orador (*speaker notes*). <br>• Bloqueio contra comandos fora da apresentação para evitar interferência na cabine. | `apps/daemon`, `slices/remote` |
| **RF-26B** | **Console Web do Operador via QR Code (`/remote/operator`)** | Pareamento instantâneo via escaneamento de QR Code na tela da cabine, abrindo uma aplicação web móvel completa no navegador (sem necessidade de instalar apps de lojas). Permite controle volante das principais funções: <br>• **Ações de Emergência:** Acionamento de comandos de pânico (Blackout, Clear Text, Clear Background, Freeze). <br>• **Navegação do Culto:** Disparo e avanço dos itens da liturgia (`culto.json`). <br>• **Bíblia Rápida:** Busca e projeção direta de passagens e versículos. <br>• **Alertas Rápidos:** Disparo de mensagens de rodapé (placas de carro, pais de crianças). | `apps/daemon`, `slices/remote` |
| **RF-26C** | **Arquitetura Focada & Exclusão de Bloatware** | Rejeição arquitetural expressa a canais de chat interno entre operadores, feeds sociais ou listas de contatos de membros, focando 100% dos recursos em despacho de comandos litúrgicos de baixa latência e alta estabilidade de rede. | `apps/daemon`, `slices/remote` |

#### 🕵️ Como buscar e inspecionar no código original (Holyrics):
- **Classes-chave:**
  - `src/com/limagiran/holyrics/util/HolyricsStreaming.java`: Servidor HTTP de streaming para OBS Studio.
  - `src/com/limagiran/holyrics/control/AlertController.java` & `AlertSettingsDialog.java`: Gerenciamento e disparo de alertas.
  - `src/com/limagiran/holyrics/webservice/HolyricsWS.java` & `AppMessageController.java`: Servidor Socket para o app móvel Holyrics Remote.
  - `src/com/limagiran/holyrics/view/CountdownDialog.java`: Janela de contagem regressiva e cronômetro.
  - `src/com/limagiran/holyrics/webservice/UDPServer.java`: Descoberta automática de servidores Holyrics na rede local via broadcast UDP.
- **Comandos de busca úteis:**
  ```bash
  grep -rn "ServerSocket" src/
  grep -rn "HolyricsStreaming" src/
  grep -rn "AppMessage" src/
  grep -rn "AlertController" src/
  ```

#### ⚠️ Análise da Abordagem Legada & Problemas Encontrados:
- **Streaming via Concatenação Bruta de HTML:** O `HolyricsStreaming.java` roda um `ServerSocket` rudimentar que gera HTML e CSS concatenando strings na mão a cada requisição HTTP. Não utiliza WebSockets; o navegador do OBS precisa fazer polling ou receber recarregamentos agressivos, causando cintilação (*flickering*).
- **Protocolo de Rede Frágil no App Mobile:** A comunicação com o aplicativo móvel utiliza sockets TCP brutos com serialização própria. Se o smartphone do operador mudar de tela ou oscilar no Wi-Fi, a conexão cai e frequentemente o servidor do Holyrics trava, exigindo reiniciar o programa no meio do culto.
- **Acoplamento com Chat e Funções Desnecessárias:** O app legado mistura comandos litúrgicos com recursos de chat em tempo real e notificações sociais, gerando concorrência de threads e sobrecarga no servidor do programa principal.
- **Pareamento Manual Complexo:** No sistema legado, o operador precisa digitar manualmente endereços IP e portas ou depender de broadcast UDP que frequentemente é bloqueado por roteadores Wi-Fi corporativos de igrejas.
- **Alertas com Estilos Rígidos:** Os alertas em tela são caixas de diálogo Swing desenhadas sobrepostas à janela do projetor, sem suporte a animações suaves de entrada/saída ou adaptação responsiva a diferentes resoluções.

#### 🚀 Nova Abordagem & Melhorias (LumenLite):
- **WebSockets de Baixa Latência com `Bun.serve`:** O servidor de conectividade roda nativamente no runtime Bun com suporte a WebSockets de altíssima performance. As rotas `/broadcast` (para o OBS) e `/stage` (para o palco) recebem as transições instantaneamente em sub-milissegundos com zero cintilação.
- **Controle Web sem Instalação (Zero App Stores):** Todo o controle móvel é servido diretamente pelo daemon local como uma aplicação web responsiva (PWA leve). Tanto o pastor quanto o operador acessam via navegador nativo (Safari, Chrome) sem necessidade de instalar aplicativos de terceiros.
- **Pareamento Instantâneo via QR Code:** O operador ou o pregador escaneia o QR Code gerado na interface desktop do LumenLite, que já embute a URL local com o token de autorização de curta duração (RBAC: papel de pastor ou papel de operador).
- **Papéis Estritos (RBAC Leve):** A rota do pregador (`/remote/preacher`) concede apenas controle da sua apresentação, visão dos próximos slides, notas do orador e relógio, impedindo toques acidentais em configurações da igreja. A rota do operador (`/remote/operator`) libera botões de pânico, liturgia e bíblia.
- **Eliminação de Bloatware (Sem Chat):** Eliminação deliberada de módulos de chat e mensageria, mantendo o tráfego 100% dedicado a pacotes JSON de controle de estado em sub-milissegundos.
- **Transmissão Transparente Fluida com React:** A rota do OBS é uma aplicação web React com fundo transparente nativo. Letras e alertas entram e saem com animações elegantes de fade e slide aceleradas por hardware via `motion/react`.

---

### Módulo 6: Mídia, Áudio & Loops (Media & Playback Core)

Responsável pela reprodução de mídias pesadas, vídeos em loop, trilhas de áudio ambiente e geração de cache visual.

| ID | Nome do Requisito | Descrição Detalhada | Fatias Afetadas |
|---|---|---|---|
| **RF-27** | **Loops de Vídeo Contínuos (*Seamless Loops*)** | Reprodução contínua e sem travamentos de vídeos de plano de fundo (*background loops*) em alta resolução sob o texto das músicas, sem telas pretas ou piscadas entre repetições. | `slices/navigation` |
| **RF-28** | **Player de Áudio Dedicado com Equalizador** | Reprodutor de música ambiente para pré e pós-culto com fila de reprodução, transição suave entre faixas (*crossfade*) e equalizador gráfico de frequências. | `slices/media` |
| **RF-29** | **Cache e Geração Assíncrona de Miniaturas** | Geração e armazenamento em cache de miniaturas (*thumbnails*) de imagens e vídeos para carregamento instantâneo nas grades da interface sem travar a navegação. | `slices/media` |

#### 🕵️ Como buscar e inspecionar no código original (Holyrics):
- **Classes-chave:**
  - `src/com/limagiran/holyrics/util/MediaPlayerUtils.java`: Integração com reprodutor de mídia via biblioteca VLCJ.
  - `src/com/limagiran/holyrics/view/AudioPlayerPanel.java`: Painel da interface do tocador de áudio integrado.
  - `src/com/limagiran/holyrics/control/Equalizer.java`: Controle das bandas de equalização sonora.
  - `src/com/limagiran/holyrics/control/ThumbMediaController.java` & `ThumbCacheController.java`: Geração e cache de miniaturas.
  - `src/com/limagiran/holyrics/util/FFMPEG.java`: Wrapper para chamadas ao executável FFmpeg no sistema.
- **Comandos de busca úteis:**
  ```bash
  grep -rn "MediaPlayerUtils" src/
  grep -rn "AudioPlayer" src/
  grep -rn "Equalizer" src/
  grep -rn "ThumbMedia" src/
  ```

#### ⚠️ Análise da Abordagem Legada & Problemas Encontrados:
- **Dependência do VLC Instalado (VLCJ):** O Holyrics utiliza a biblioteca Java VLCJ, que requer que o VLC Media Player esteja instalado no Windows com arquitetura compatível (32 ou 64 bits). Se as DLLs do VLC não forem encontradas, o programa não reproduz vídeos de fundo.
- **Tela Preta no Final do Loop:** No VLCJ, a repetição de vídeos de fundo frequentemente exibe um breve piscar de tela preta (1 a 2 frames) ao rebobinar o arquivo, prejudicando a estética visual no telão da igreja.
- **Geração de Miniaturas Bloqueante:** A extração de miniaturas aciona processos externos do FFmpeg de forma sequencial, causando lentidão ao abrir pastas com muitos vídeos.

#### 🚀 Nova Abordagem & Melhorias (LumenLite):
- **Decodificação Nativa por Hardware no Navegador:** Os vídeos de fundo são reproduzidos diretamente através do elemento HTML5 `<video>` nativo acelerado pela GPU do sistema operacional no WebView do Tauri v2, sem necessidade de instalar players de terceiros (como VLC).
- **Técnica de *Double Buffering* de Vídeo:** Para eliminar 100% de qualquer piscar de tela na troca ou repetição de loops, o renderizador utiliza duas instâncias de vídeo em memória alternando a opacidade via CSS (`opacity` 0% / 100%), garantindo repetições e transições totalmente imperceptíveis.
- **Áudio Profissional com Web Audio API:** O player de música ambiente utiliza nós nativos de áudio (`BiquadFilterNode` para o equalizador e `GainNode` para o crossfade), garantindo processamento sonoro cristalino com baixíssimo uso de CPU.

---

### Módulo 7: Automação por Nós (DAG Hooks) & Controle Total via CLI

*Este módulo substitui e moderniza completamente o antigo sistema de eventos e triggers procedurais do Holyrics, introduzindo automação visual por blocos e controle total por terminal.*

| ID | Nome do Requisito | Descrição Detalhada | Fatias Afetadas |
|---|---|---|---|
| **RF-30** | **Controle Total via CLI (`lumen <slice> <action>`)** | Cada ação realizável na interface gráfica possui um comando CLI equivalente no terminal (ex: `lumen next`, `lumen song play 1042`, `lumen panic blackout`). Permite operação pura via terminal, scripts em lote e integração nativa com Stream Deck sem precisar de cliques de mouse. | `apps/daemon`, todas as `slices` |
| **RF-31** | **Motor de Hooks Baseado em Grafo (DAG)** | Pipeline assíncrono desacoplado da projeção que escuta mudanças de estado e executa automações externas: <br>`Trigger (Gatilho)` $\rightarrow$ `Transform (Regex/Extração)` $\rightarrow$ `Condition (Filtro/Diff)` $\rightarrow$ `Action (Ação)`. | `slices/hooks` |
| **RF-32** | **Editor Visual de Automação com Blocos (`@xyflow/react`)** | Interface gráfica intuitiva baseada em nós arrastáveis onde o operador monta regras visuais. Exemplo de fluxo: quando o slide exibir a tag `[COR-AMARELA]`, enviar um comando HTTP para a API do Lumikit DMX e trocar a cena no OBS Studio para "Câmera Pastor". | `slices/hooks` |
| **RF-33** | **Ações Externas Integradas nos Nós** | Suporte nativo a nós de ação populares no ambiente de igreja: <br>• Chamada HTTP REST (ex: Lumikit DMX, mesas de som digitais Behringer/Yamaha). <br>• OBS Studio WebSocket v5 (troca de cenas, visibilidade de fontes). <br>• Execução de scripts locais e comandos de terminal. | `slices/hooks` |
| **RF-34** | **Sistema Declarativo de Atalhos (`keybindings.json`)** | Mapeamento completo de teclas do teclado para strings de comando CLI, replicando nativamente 100% dos atalhos clássicos do Holyrics (`Espaço` para avançar, `F9` para ocultar letra, `Ctrl+F` para Bíblia, `Esc` para voltar, etc.). | `slices/keymap` |

#### 🕵️ Como buscar e inspecionar no código original (Holyrics):
- **Classes-chave:**
  - `src/com/limagiran/holyrics/control/EventController.java`: Controlador legado de eventos da aplicação.
  - `src/com/limagiran/holyrics/control/ControllerListener.java`: Interfaces de ouvintes de eventos da UI.
  - `src/com/limagiran/holyrics/util/InsertShortcut.java`: Mapeamento de teclas de atalho da interface.
  - `src/com/limagiran/holyrics/event/`: Listeners de cliques e formulários gráficos.
- **Comandos de busca úteis:**
  ```bash
  grep -rn "addKeyListener" src/
  grep -rn "KeyEvent" src/
  grep -rn "ControllerListener" src/
  ```

#### ⚠️ Análise da Abordagem Legada & Problemas Encontrados:
- **Triggers Acoplados e Código Síncrono:** No Holyrics, qualquer automação (como chamar um webhook ou acionar um comando) é disparada diretamente dentro dos listeners da interface gráfica Swing. Se a API de iluminação demorar para responder ou a rede oscilar, o telão da igreja trava e a passagem de slides é interrompida.
- **Ausência de Linha de Comando (CLI):** O Holyrics é um monolito 100% voltado ao clique de mouse. É impossível criar automações externas, disparar comandos via Stream Deck de forma leve ou operar o sistema sem a janela gráfica aberta.
- **Atalhos Engessados no Código:** Teclas de atalho estão espalhadas em dezenas de classes (`keyPressed`, `keyReleased`), tornando impossível para o usuário remapear atalhos ou personalizar a experiência.

#### 🚀 Nova Abordagem & Melhorias (LumenLite):
- **Isolamento Absoluto de Execução (0ms de Impacto):** O motor de hooks (DAG) é completamente desacoplado do motor de projeção. As ações externas utilizam chamadas assíncronas protegidas por `AbortSignal.timeout(500)`. Mesmo que a mesa de iluminação caia, o telão continua operando a 60 FPS sem nenhum atraso.
- **Arquitetura CLI-First:** O núcleo do sistema é um daemon headless. Toda ação gráfica é simplesmente o envio de uma mensagem IPC que aciona o mesmo comando executável via terminal. Um operador pode controlar todo o culto usando scripts simples, botões de Stream Deck ou atalhos globais.
- **Automação Visual em Blocos:** O editor em nós com `@xyflow/react` oferece uma experiência visual limpa e poderosa, permitindo que operadores leigos criem integrações avançadas arrastando conexões entre caixas na tela, sem escrever uma única linha de código.

---

## 3. Requisitos Não-Funcionais (RNF)

Os Requisitos Não-Funcionais garantem que o sistema seja extremamente leve, ágil e estável em ambientes de produção ao vivo, mesmo em computadores modestos:

| ID | Categoria | Meta / Métrica Exata | Justificativa Arquitetural |
|---|---|---|---|
| **RNF-01** | **Tempo de Inicialização (Cold Start)** | **Inferior a 1.2 segundos** (Daemon + Interface). | Runtime **Bun** (~8ms de boot) + envelope nativo **Tauri v2** + empacotamento ultrarrápido com **Vite+**. |
| **RNF-02** | **Latência de Troca de Slide** | **Inferior a 16 milissegundos** (1 frame cravado a 60Hz). | Transporte local via WebSockets em memória + técnica de *Double Buffering* acelerada na GPU. |
| **RNF-03** | **Consumo de Memória RAM** | **Máximo de 120MB** (texto/Bíblia) e **350MB** (vídeos Full HD em loop). | Eliminação total da máquina virtual Java (JVM) e sem o overhead de 1GB característico do Electron. |
| **RNF-04** | **Automação sem Bloqueio de Thread** | **0 milissegundos de impacto** na projeção durante falhas de rede externa. | Motor de hooks desacoplado da renderização com timeout estrito via `AbortSignal.timeout(500)`. |
| **RNF-05** | **Operação 100% Offline** | Nenhuma dependência de internet para a realização do culto. | Banco SQLite local (`bun:sqlite` WAL + FTS5) e assets multimídia armazenados e validados em disco. |
| **RNF-06** | **Qualidade de Código & Velocidade de Build** | Checagem estática completa em **menos de 600ms**. | Pipeline em Rust via Vite+ (`vp check` = Oxlint + Oxfmt + tsgolint) com regras anti-God-Files. |
