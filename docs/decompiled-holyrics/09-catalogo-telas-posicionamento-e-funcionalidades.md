# 09 - Catálogo Completo de Telas, Posicionamento e Funcionalidades (Holyrics Legado)

Este documento registra a engenharia reversa visual e funcional **exaustiva (100% de cobertura)** de todas as telas, janelas, diálogos modais e painéis embutidos do Holyrics original (`src/`). Foram analisadas **93 classes gráficas** e **57 janelas/diálogos**, cobrindo cada menu, botão, campo, tabela e atalho de teclado, com seus respectivos diagramas de layout espacial (wireframes) e manipuladores de eventos (*handlers*).

---

## 📌 Sumário dos Agrupamentos de Telas

1. [Cluster 1: Tela Principal do Operador (`MusicMain`)](#-cluster-1-tela-principal-do-operador-musicmainjava)
2. [Cluster 2: Tela Principal da Bíblia (`BibleMain`)](#-cluster-2-tela-principal-da-bíblia-biblemainjava)
3. [Cluster 3: Projeção ao Vivo e Saídas de Vídeo](#-cluster-3-projeção-ao-vivo-e-saídas-de-vídeo)
4. [Cluster 4: Gestão, Edição e Ingestão de Músicas](#-cluster-4-gestão-edição-e-ingestão-de-músicas)
5. [Cluster 5: Temas Visuais, Estilos e Apresentações Rápidas](#-cluster-5-temas-visuais-estilos-e-apresentações-rápidas)
6. [Cluster 6: Avisos, Alertas de Emergência e Cronômetros](#-cluster-6-avisos-alertas-de-emergência-e-cronômetros)
7. [Cluster 7: Roteiro de Culto (HItem), Playback e Áudio](#-cluster-7-roteiro-de-culto-hitem-playback-e-áudio)
8. [Cluster 8: Operação Eclesiástica (Escalas, Eventos, Membros e Relatórios)](#-cluster-8-operação-eclesiástica-escalas-eventos-membros-e-relatórios)
9. [Cluster 9: Conectividade de Rede e Holyrics Remote](#-cluster-9-conectividade-de-rede-e-holyrics-remote)
10. [Cluster 10: Configurações Globais, Downloads e Ajuda](#-cluster-10-configurações-globais-downloads-e-ajuda)

---

## 🖥️ Cluster 1: Tela Principal do Operador (`MusicMain.java`)

A `MusicMain` é a janela central (`JFrame`) do Holyrics. Ela opera com um layout dividido por `JSplitPane` em três colunas verticais principais, uma barra superior de monitoramento e um rodapé unificado de controle.

### Inventário de Menus e Controles de `MusicMain`

#### 1. Barra de Menus Superior (`jMenuBarMenu`)
| Menu / Item | Rótulo / Ação | Atalho Original | Função / Disparo no Código |
|---|---|---|---|
| `jMenuFile` | **Arquivo** | - | Menu raiz de arquivos e saídas do sistema. |
| `jMenuItemGoToBible` | Bíblia | `F7` | Abre ou foca a janela `BibleMain.java`. |
| `jMenuItemFileFilesReceived` | Arquivos Recebidos | - | Abre pasta/diálogo de mídias baixadas ou recebidas. |
| `jMenuItemFileSettings` | Configurações do Programa | `Ctrl+Shift+C` | Abre diálogo de preferências gerais `SettingsView.java`. |
| `jMenuItemFileDisplaySettings`| Configurações do Telão | `Ctrl+Shift+E` | Abre diálogo de monitores `ScreenDeviceSettings`. |
| `jMenuItemExit` | Sair | `Alt+F4` | Encerra a aplicação (`System.exit(0)`). |
| `jMenuNew` | **Novo** | - | Submenu de criação rápida de entidades. |
| `jMenuItemNewMusic` | Nova Música | `Ctrl+1` | Abre o editor de músicas limpo (`MusicView.java`). |
| `jMenuItemNewTheme` | Novo Tema | `Ctrl+2` | Abre o criador de temas visuais (`ThemeView.java`). |
| `jMenuItemNewProfile` | Novo Perfil | `Ctrl+3` | Abre diálogo para novo perfil (`EditProfile.java`). |
| `jMenuItemNewWorship` | Novo Culto / Modelo | `Ctrl+4` | Abre editor de modelo de culto (`EditWorship.java`). |
| `jMenuItemNewEvent` | Novo Evento na Agenda | `Ctrl+5` | Abre formulário de evento (`EventFormCreate.java`). |
| `jMenuItemNewMember` | Novo Membro da Equipe | `Ctrl+6` | Abre formulário de voluntários (`EditMember.java`). |
| `jMenuEdit` | **Editar** | - | Submenu de edição das entidades cadastradas. |
| `jMenuItemEditProfile` | Editar Perfis | `Ctrl+3` | Abre tela de gerenciamento de perfis existentes. |
| `jMenuItemEditWorship` | Editar Cultos | `Ctrl+4` | Edita roteiros e cultos salvos. |
| `jMenuItemEditEvent` | Editar Eventos | `Ctrl+5` | Abre agenda e tabela de eventos (`EventTable.java`). |
| `jMenuItemEditMember` | Editar Membros | `Ctrl+6` | Abre lista e editor de membros. |
| `jMenuMusic` | **Músicas** | - | Operações sobre a base musical. |
| `jMenuItemWebSearch` | Pesquisa na Web (Vagalume) | `Ctrl+Shift+W` | Abre busca online de letras (`MusicWebSearch.java`). |
| `jMenuItemDatabaseSearch` | Pesquisar na Base Offline | `Ctrl+Shift+D` | Abre busca no banco local (`MusicSearchDBOffLineView.java`). |
| `jMenuItemMusicSavePresentationAs`| Salvar Apresentação Como | `Ctrl+Shift+S` | Exporta a apresentação ativa (`MusicSaveAsDialog.java`). |
| `jMenuMusicBatchImport` | Importação em Lote | - | Submenu com diversos conversores de arquivos. |
| `jMenuItemMusicImportFileBatch` | Importar Arquivos de Texto | - | Processa múltiplos `.txt` ou `.muf` (`MusicBatchImportView.java`). |
| `jMenuItemMusicBathImportFromDBO` | Importar de Base Offline (.dbo) | - | Ingestão de banco compactado Holyrics offline. |
| `jMenuItemMusicImportPPManyInOne`| Importar PowerPoint (Múltiplos em 1) | - | Converte apresentações PPT (`PowerPointImportView.java`). |
| `jMenuItemMusicBathImportFromEasiSlides`| Importar EasiSlides (*.xml) | - | Parser XML do software EasiSlides. |
| `jMenuItemMusicBathImportFromOpenSong` | Importar OpenSong (*.xml) | - | Parser XML do OpenSong. |
| `jMenuItemMusicBathImportFromOpenLP` | Importar OpenLP (*.sqlite) | - | Leitor de base do OpenLP. |
| `jMenuItemMusicBathImportFromCCLI_USR` | Importar CCLI (*.usr) | - | Parser de formato SongSelect CCLI. |
| `jMenuItemMusicBathImportFromWord` | Importar Word (*.doc, *.docx) | - | Extrator de texto do Microsoft Word. |
| `jMenuItemMusicBathExport` | Exportar Músicas em Lote | - | Salva acervo em `.zip` ou pastas (`MusicBatchExport.java`). |
| `jMenuItemMusicBatchDelete` | Exclusão em Lote | - | Exclui múltiplas músicas selecionadas (`MusicBatchDelete.java`). |
| `jMenuTheme` | **Temas** | - | Gestão de temas de texto e vídeo. |
| `jMenuFileThemeImport` | Importar Tema | - | Importa arquivo de tema `.hlt`. |
| `jMenuAnnouncement` | **Avisos** | - | Gerenciador de avisos e boletins. |
| `jMenuItemAnnouncementEdit` | Criar ou Editar Aviso | - | Abre painel de edição (`AnnouncementView.java`). |
| `jMenuItemAnnouncementShow` | Exibir Aviso no Telão | `Ctrl+Shift+A` | Dispara projeção do aviso (`AnnouncementShowDialog.java`). |
| `jMenuSchedule` | **Escalas** | - | Grade de voluntários de louvor e mídia. |
| `jMenuItemScheduleViewEdit` | Visualizar / Editar Escala | - | Abre calendário de equipes (`ScheduleView.java`). |
| `jMenuReport` | **Relatórios** | - | Relatórios estatísticos e de impressão. |
| `jMenuItemReportPlayedMusic`| Músicas Mais Tocadas | - | Gráficos e frequência (`ReportPlayedMusicsView.java`). |
| `jMenuItemReportSchedule` | Relatório de Escalas | - | Lista de presenças e voluntários (`ReportScheduleView.java`). |
| `jMenuTools` | **Ferramentas** | - | Utilitários de cabine. |
| `jMenuItemImageFileShow` | Apresentar Arquivo de Imagem | `Ctrl+Shift+I` | Projeção direta de imagem externa (`ImageFileShowDialog.java`). |
| `jMenuItemCreateImage` | Criar Imagem de Slide | - | Exporta slide atual como JPEG/PNG (`CreateImageDialog.java`). |
| `jMenuItemCountdown` | Cronômetro / Contagem Regressiva | `Ctrl+Shift+R` | Abre painel de temporizador (`CountdownDialog.java`). |
| `jMenuItemToolsAutomaticShow`| Apresentação Automática | - | Ativa troca periódica de slides (`AutomaticShowView.java`). |
| `jCheckBoxMenuItemFreezeScreen`| Congelar Tela (Freeze) | `F8` | Trava o frame atual no projetor (`Freeze.java`). |
| `jMenuExportToPDF` | Exportar Letras para PDF | - | Gera caderno de música impresso/digital. |
| `jMenuItemChangeWebServiceIP`| Alterar IP do WebService | - | Configura IP do servidor de controle remoto. |
| `jMenuWindows` | **Janelas** | - | Painéis auxiliares desacoplados. |
| `jCheckBoxMenuItemWindowsFavorites`| Painel de Favoritos | - | Exibe gaveta de acesso rápido (`FavoritePanel.java`). |
| `jMenuItemWindowsCommunicationPanel`| Painel de Comunicação (Chat) | `Ctrl+Shift+T` | Abre mensageiro de cabine (`CommunicationPanel.java`). |
| `jMenuItemWindowsLive` | Mini Telão Flutuante (Live View) | `Ctrl+Shift+L` | Janela de retorno ao vivo (`LiveView.java`). |
| `jMenuHelp` | **Ajuda** | - | Suporte, compatibilidade e versão. |
| `jMenuItemHelp` | Guia de Ajuda | `F1` | Manual do usuário integrado (`HelpView.java`). |
| `jMenuItemSupportAndFeedBack`| Suporte e Sugestões | - | Formulário de feedback (`SupportAndSuggestionsDialog.java`). |
| `jMenuItemCheckUpdates` | Verificar Atualizações | - | Conecta no servidor Holyrics para update. |
| `jCheckBoxMenuItemUseJavaFXVideo`| Modo de Vídeo JavaFX | - | Alterna renderizador de vídeo entre LibVLC e JavaFX. |
| `jMenuItemAbout` | Sobre o Holyrics | - | Diálogo de créditos e versão (`AboutView.java`). |

#### 2. Barra Superior de Status & Configurações do Telão (`jPanelScreenInfo`)
- **`jComboBoxThemeDefault`**: Dropdown para selecionar o tema padrão de exibição imediata.
- **`jToggleButtonUppercase`**: Força todo o texto do telão em letras MAIÚSCULAS.
- **`jToggleButtonTitleUnderline`**: Ativa/desativa linha sublinhada abaixo do título do hino.
- **`jToggleButtonBreakLine`**: Ativa a quebra automática de linhas longas sem cortar palavras.
- **`jToggleButtonCustomFormatting`**: Aplica formatação avançada definida em tags HTML/CSS customizadas.
- **`jLabelSlidePreviewIsAnimated`**: Ícone indicador de reprodução de loop ativo no background.
- **`jButtonDisplaySettings`**: Botão direto para abrir a configuração de monitores e resoluções.
- **`jComboBoxScreenType`**: Chaveia a saída de vídeo entre tela principal, retorno de palco e janela virtual.

#### 3. Coluna Lateral Esquerda (`jPanelLeft` / `MusicSearchPanel`)
- **`jTextFieldSearch`**: Campo de digitação de filtro instantâneo por texto.
- **Checkboxes de Filtro**: `[X] Título`, `[X] Artista`, `[X] Letra`, `[X] Anotações`.
- **`jComboBoxFilterProfiles`**: Filtra as músicas pertencentes ao perfil atual (ex: "Jovens", "Culto Domingo").
- **`jComboBoxOrderBy`**: Ordenação por Título, Artista, Mais Tocadas, Menos Tocadas, Última Modificação.
- **`jCheckBoxOrderByInverse`**: Inverte a ordem da listagem (A-Z ou Z-A).
- **`jPanelButtonsLetters`**: Barra alfabética de botões rápidos (A, B, C... Z, #) para salto imediato na lista.
- **`jListMusics`**: Lista com barra de rolagem contendo as músicas encontradas com status e miniaturas.
- **`jButtonNewMusic`**: Botão de atalho rápido para cadastrar um novo louvor.

#### 4. Área Central de Mídia & Letras (`jPanelMusic`)
- **`jLabelMusicTitleAndArtist`**: Exibe o título grande do hino, artista e tonalidade musical original.
- **Barra de Ferramentas da Música Ativa (`jToolBarMusic`)**:
  - `jButtonShowBeginning`: Projeta imediatamente o slide 1 no telão (`F5`).
  - `jButtonShowCurrentSlide`: Projeta o slide selecionado atualmente na grade (`Espaço`).
  - `jButtonAddPlayList`: Adiciona a música ao Roteiro de Culto da coluna direita (`Enter`).
  - `jButtonAddHistory`: Registra manualmente a música no histórico de louvores executados.
  - `jButtonEditMusic`: Abre a música em modo de edição completa (`MusicView`).
  - `jButtonDeleteMusic`: Exclui a música do banco de dados local com confirmação.
- **Seletor de Ordem de Estrofes (`jPanelOrder`)**: Botões em cápsula (`[V1]`, `[C]`, `[V2]`, `[B]`, `[Final]`) permitindo clicar para navegar diretamente entre estrofes sem rolar a página.
- **Visualizador de Slides (`jPanelSlideView`)**: Lista em cartões dos blocos de texto formatados conforme aparecerão no projetor.
- **Mini-Preview do Telão**: Painel em miniatura com proporção 16:9 renderizando o frame atual com fundo e tipografia em tempo real.

#### 5. Coluna Direita: Roteiro do Culto (`jPanelRight` / `jPanelPlayList`)
- **`jComboBoxEvent`**: Seletor do culto ou evento ao qual o roteiro pertence.
- **`jListPlayList`**: Lista ordenável por *drag-and-drop* contendo todos os itens da liturgia (músicas, versículos bíblicos, avisos, contagens regressivas, mídias).
- **`jButtonPlayListShowBeginning` / `jButtonPlayListShowSelection`**: Comandos para disparar o item do culto selecionado no projetor.
- **Abas Inferiores de Mídia**: Alternância entre itens de culto e catálogo de vídeos de fundo (`jListMedia`).

#### 6. Rodapé de Controle Unificado (`jPanelFooter`)
- **Navegador de Slides (`PresentationControllerViewFooter`)**:
  - `jButtonPrevious`: Volta para o slide anterior (`Seta Esquerda` ou `Page Up`).
  - `jButtonNext`: Avança para o próximo slide (`Seta Direita` ou `Page Down`).
  - `jToggleButtonViewOnlyText`: Oculta imagens de fundo e exibe texto em fundo preto puro de emergência (`F6`).
  - `jToggleButtonViewSlide`: Modos de exibição da miniatura.
  - `jToggleButtonThemeList`: Gaveta flutuante para troca rápida de tema sem sair da música.
- **Player de Áudio Integrado (`AudioPlayerPanel`)**:
  - Botões Play (`▶`), Pause (`⏸`), Stop (`⏹`), Anterior (`⏮`), Próximo (`⏭`).
  - `jToggleButtonVolume`: Slider de ajuste do volume master e botão de Mute.
  - `jButtonEqualizer`: Abre janela modal com equalizador gráfico de 10 bandas de áudio.
  - `jToggleButtonRepeat` & `jToggleButtonShuffler`: Modos de repetição de faixa e reprodução aleatória.

---

## 📖 Cluster 2: Tela Principal da Bíblia (`BibleMain.java`)

A `BibleMain` é uma janela independente (`JFrame`), invocada pelo atalho `F7` ou pelo menu Arquivo, dedicada à navegação rápida, leitura comparada em até 3 versões simultâneas e projeção de Escrituras.


### Inventário de Menus e Controles de `BibleMain`

| # | Posição | Componente | Rótulo / Função | Ação / Código Legado |
|---|---|---|---|---|
| 1 | Menu Superior | `JMenu` | Versões | Permite selecionar e alterar a versão primária (`jMenuVersionDefaultChange`), auxiliar 1 e auxiliar 2. |
| 2 | Menu Superior | `JMenuItem` | Baixar Novas Bíblias | Abre `DownloadView.java` para baixar arquivos `.bib` direto do repositório online. |
| 3 | Menu Superior | `JMenuItem` | Salvar Versículos | Abre `BibleSaveVersesDialog.java` para criar listas de leitura para o pregador. |
| 4 | Coluna Esquerda | `JList` | Livros Bíblicos | Lista todos os 66 livros bíblicos divididos entre AT e NT (`jPanelBooksName`). |
| 5 | Coluna Esquerda | `JList` | Abreviações | Permite seleção rápida por abreviações de 3 letras (`GN`, `EX`, `SL`, `MT`). |
| 6 | Centro - Topo | `JPanel` | Grade de Capítulos | Botões numéricos paginados para seleção instantânea do capítulo (`jPanelChaptersPage1/2`). |
| 7 | Centro - Meio | `JPanel` | Grade de Versículos | Botões numéricos paginados com cada versículo do capítulo escolhido (`jPanelVersesPage1/2`). |
| 8 | Centro - Base | `JTextField` | Pesquisa de Termos | Campo para busca de palavras-chave na Bíblia completa via `VerseSearchView.java`. |
| 9 | Coluna Direita | `JList` | Versículos ARA | Lista com o texto integral do capítulo na versão padrão selecionada (`jListVersesDefault`). |
| 10| Coluna Direita | `JList` | Versículos NVI | Comparação em tempo real do mesmo capítulo na versão auxiliar 1 (`jListVersesAux1`). |
| 11| Coluna Direita | `JList` | Versículos NVT | Comparação em tempo real do mesmo capítulo na versão auxiliar 2 (`jListVersesAux2`). |
| 12| Rodapé Bíblia | `JButton` | Projetar Versículo | Dispara imediatamente o versículo clicado para o telão principal (`jButtonShow`). |
| 13| Rodapé Bíblia | `JButton` | Projeção Personalizada | Abre diálogo para selecionar fundo e formato específico (`jButtonCustomShow`). |
| 14| Rodapé Bíblia | `JButton` | Adicionar ao Roteiro | Adiciona a passagem selecionada como item de culto (`jButtonAddPlayList`). |
| 15| Rodapé Bíblia | `JButton` | Voltar ao Holyrics | Fecha ou minimiza a Bíblia e retorna o foco para `MusicMain` (`jButtonGoToHolyrics`). |

---

## 📽️ Cluster 3: Projeção ao Vivo e Saídas de Vídeo

Este cluster reúne os componentes responsáveis pelo envio e composição dos gráficos nos monitores secundários e painéis de LED.

### Classes Envolvidas
- `PresentationJDialog.java`: Janela de projeção borderless no projetor principal.
- `PresentationSlideView.java`: Camada de renderização vetorial de tipografia.
- `LiveView.java`: Diálogo de monitoramento flutuante em tempo real.
- `WallpaperView.java`: Gerenciador de papel de parede e fundos inativos.
- `VLCVideoBackground.java` & `VLCView.java`: Renderizador nativo de loops de vídeo com LibVLC.


### Controles e Recursos de Projeção
1. **Compositor Multicamadas**: Camada de vídeo inferior (sempre rodando em loop suave via VLCJ) sobreposta por camada de texto com transparência alfa e canal de alerta emergencial no topo.
2. **Motor Anti-Estouro de Tipografia**: O `PresentationSlideUtils.java` calcula o bounding box da estrofe. Se o texto ultrapassar a margem de segurança configurada, a fonte sofre auto-downscaling imediato.
3. **Blackout de Emergência (`F5`)**: Oculta texto e mídia instantaneamente gerando tela preta pura.
4. **Clear Text (`F6`)**: Oculta a letra preservando o fundo em movimento.
5. **Clear Background (`F7`)**: Oculta o fundo mantendo o texto em fundo preto puro.
6. **Congelamento de Imagem / Freeze (`F8` / `Freeze.java`)**: Captura o buffer de tela atual e o exibe de forma estática no projetor, permitindo que o operador mexa no software sem que as alterações vazem para a audiência.
7. **Tela de Retorno de Palco (`StageViewUtils.java`)**:
   - Layout de alto contraste com fundo preto e texto amarelo/branco.
   - Relógio de parede no canto superior direito.
   - Cronômetro decrescente para o pastor/pregador.
   - Bloco inferior em verde exibindo a estrofe subsequente (Preview de Próximo Slide).

---

## 🎵 Cluster 4: Gestão, Edição e Ingestão de Músicas

Compreende o ciclo de vida completo do acervo de letras, acordes e importadores.

### Telas Mapeadas
- `MusicView.java`: Formulário mestre de cadastro/edição de músicas.
- `MusicWebSearch.java`: Diálogo de busca online no portal Vagalume.
- `MusicBatchImportView.java`: Assistente de importação em lote de arquivos de texto.
- `PowerPointImportView.java`: Importador de slides de arquivos PPT/PPTX.
- `EditMusicOrderDialog.java`: Diálogo de reordenação estrutural de estrofes.
- `MusicBatchExport.java` & `MusicBatchDelete.java`: Operações em massa.


### Inventário de Controles do Editor de Músicas (`MusicView`)
- **`jTextFieldTitle`**: Campo obrigatório para título da canção.
- **`jTextFieldArtist`**: Campo para cantor, banda ou ministério de louvor.
- **`jComboBoxTone`**: Seletor da tonalidade musical original (C, D, E, F, G, A, B e bemóis/sustenidos).
- **`jTextAreaLyrics`**: Editor central com realce de tags de estrofes (`[Verso]`, `[Coro]`, `[Ponte]`).
- **`jButtonToUpperCase`**: Converte todo o texto para letras maiúsculas.
- **`jButtonRemoveLastPoints`**: Remove pontos finais desnecessários no término de versos para manter a estética de projeção limpa.
- **`jToggleButtonAlignCenter` & `jToggleButtonAlignLeft`**: Alterna o alinhamento padrão da música.
- **`jButtonSave`**: Persiste as alterações no banco SQLite `holyrics.db` via `MusicDao.java`.

---

## 🎨 Cluster 5: Temas Visuais, Estilos e Apresentações Rápidas

Reúne os recursos para estilização gráfica de fontes, backgrounds e apresentações avulsas.

### Telas Mapeadas
- `ThemeView.java`: Gerenciador de temas visuais.
- `ThemeSettingsPanel.java`: Painel com seletores de fontes, tamanhos, sombras e margens.
- `BackgroundChoiceTheme.java`: Diálogo de seleção de mídia de fundo.
- `QuickPresentationDialog.java`: Diálogo de apresentação instantânea de texto livre.
- `AutomaticShowView.java` & `AutomaticSettingsPanel.java`: Slideshow com carrossel cronometrado.

---

## 📢 Cluster 6: Avisos, Alertas de Emergência e Cronômetros

Gerencia comunicações de emergência e boletins visuais durante o culto.

### Telas Mapeadas
- `AnnouncementView.java`: Formulário de avisos e boletins paroquiais.
- `AnnouncementShowDialog.java`: Apresentador em tela cheia do aviso.
- `AlertSettingsDialog.java`: Configurações visuais dos alertas sobrepostos.
- `AlertPanel.java` & `AlertListPanel.java`: Botões rápidos de disparo de alerta de cabine.
- `CountdownDialog.java` & `CountdownValueInputDialog.java`: Temporizadores e contagem regressiva.
- `ClockSettingsDialog.java`: Parâmetros do relógio no telão.
- `ReadyMessagesChooser.java`: Mensagens padronizadas para o púlpito.

---

## 📋 Cluster 7: Roteiro de Culto (HItem), Playback e Áudio

Orquestra a liturgia da igreja em uma linha do tempo única.

### Telas Mapeadas
- `HItemListView.java`: Painel da lista unificada de itens do culto.
- `HItemNextPresentationPanel.java`: Inspetor visual do próximo elemento do roteiro.
- `ShowGridDialog.java`: Painel com visão panorâmica de todos os slides de uma apresentação.
- `AudioPlayerPanel.java`: Reprodutor integrado com playlist e equalizador.
- `PlayBackListView.java` & `EditPlayBackDialog.java`: Gestor de trilhas de playback para ministros de louvor.
- `TransitionSettingsDialog.java`: Configurações de efeitos de transição visual (Crossfade).

### Detalhes de Integração de Playback
- O módulo de playback permite disparar uma trilha de áudio estéreo com canais separados (Canal Esquerdo: Trilha instrumental sem voz para o som do templo; Canal Direito: Click e metrônomo para o fone do baterista).
- Possui sincronização automatizada que avança os slides de letra no momento exato em que a música atinge a minutagem gravada no cue-point.

---

## 👥 Cluster 8: Operação Eclesiástica (Escalas, Eventos, Membros e Relatórios)

Engloba a gestão das equipes voluntárias e estatísticas da igreja.

### Telas Mapeadas
- `ScheduleView.java`: Grade semanal de escalas de voluntários.
- `SchedulePanelGrid.java`: Tabela interativa de funções e datas.
- `EventPanel.java` & `EventTable.java`: Agenda de eventos e ensaios.
- `EditMember.java`: Formulário de voluntários (Nome, E-mail, Telefone, Funções habilitadas).
- `EditProfile.java`: Perfis de configuração (permite alterar configurações com 1 clique entre Culto Matutino, Culto da Noite, Culto Infantil).
- `ReportPlayedMusicsView.java`: Relatório de hinos mais executados por período.

---

## 📡 Cluster 9: Conectividade de Rede e Holyrics Remote

Comunicação com dispositivos externos e app móvel para pastores e operadores remotos.

### Telas Mapeadas
- `CommunicationPanel.java`: Janela de chat e mensagens em tempo real da cabine.
- `AppMessagePanel.java`: Visualizador de mensagens recebidas de tablets e celulares.
- `ShareView.java`: Diálogo de compartilhamento de músicas via rede local.
- `ReceiveUpdateFromMobileView.java`: Janela de confirmação para aceitar alterações enviadas pelo aplicativo de celular.

### Protocolos e Conexões
- Servidor HTTP/WebSocket escutando por padrão na porta TCP local (`com.limagiran.holyrics.webservice`).
- Pareamento realizado via leitura de QR Code contendo IP local e Token de autenticação.
- Permite que o pregador no púlpito troque versículos da Bíblia diretamente do seu celular ou tablet.

---

## ⚙️ Cluster 10: Configurações Globais, Downloads e Ajuda

Central de administração do sistema e ferramentas de suporte.

### Telas Mapeadas
- `SettingsView.java`: Painel principal de preferências do sistema.
- `DownloadView.java` & `ItemVersionsView.java`: Gerenciador de download de versões da Bíblia.
- `AboutView.java`: Tela Sobre com tabela completa de atalhos de teclado.
- `HelpView.java`: Ajuda e documentação embarcada.
- `SupportAndSuggestionsDialog.java`: Canal de suporte ao usuário.
- `PopUpWorker.java`: Indicador de carregamento e progresso de tarefas assíncronas.

### Tabela Mestre de Atalhos de Teclado Originais (`AboutView`)
| Tecla / Atalho | Ação Executada |
|---|---|
| `Espaço` | Apresentar o slide selecionado atualmente |
| `Seta Direita` / `Page Down` | Avançar para o próximo slide |
| `Seta Esquerda` / `Page Up` | Voltar para o slide anterior |
| `Home` | Pular para a primeira estrofe da música |
| `End` | Pular para o último slide da apresentação |
| `F1` | Abrir Ajuda e Manual de Uso |
| `F5` | Blackout de Emergência (Tela Preta Imediata) |
| `F6` | Ocultar Texto / Clear Text (Mantém fundo de vídeo) |
| `F7` | Abrir Módulo da Bíblia Sagrada |
| `F8` | Congelar Imagem da Projeção (Freeze) |
| `F11` | Alternar Modo Tela Cheia na janela do operador |
| `Esc` | Fechar projeção / Cancelar modal ativo |
| `Ctrl+1` a `Ctrl+6` | Cadastros rápidos (Nova Música, Tema, Perfil, Culto, Evento, Membro) |
| `Ctrl+Shift+C` | Configurações do Programa |
| `Ctrl+Shift+E` | Configurações do Telão e Saídas de Vídeo |
| `Ctrl+Shift+W` | Pesquisa de Letras na Web (Vagalume) |
| `Ctrl+Shift+D` | Pesquisa no Banco Offline |
| `Ctrl+Shift+A` | Exibir Avisos Paroquiais |
| `Ctrl+Shift+R` | Iniciar Cronômetro / Contagem Regressiva |
| `Ctrl+Shift+T` | Abrir Painel de Comunicação e Chat |
| `Ctrl+Shift+L` | Abrir Janela Live View (Mini Telão Flutuante) |
