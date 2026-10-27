# 05 - Catálogo de Módulos e Código-Fonte

Este catálogo documenta os **484 arquivos Java** recuperados em `src/`, organizados por pacotes e responsabilidades.

---

## 📦 1. Módulo Central (`com.limagiran.holyrics`)

### 1.1 Classes Raiz
- `Holyrics.java`: Classe principal do sistema. Configura o Look & Feel, inicializa threads, registra listeners globais de teclado, instancia as janelas principais e trata o encerramento do processo.
- `Run.java`: Inicializador de entrada (*bootstrapper*) do executável.
- `Dev.java` & `Test.java`: Utilitários internos de depuração do desenvolvedor.
- `Update.java`: Verificação rápida de atualizações no startup.

---

### 1.2 Camada de Controle (`com.limagiran.holyrics.control`)
Concentra toda a lógica de negócio e eventos da aplicação:

| Classe | Responsabilidade |
|---|---|
| `MusicController.java` | Gerencia o ciclo de vida das letras de música, estrutura de parágrafos (estrofes, coro, ponte), tags, tonalidades e transposição de acordes. |
| `WorshipController.java` | Orquestra o "Momento de Louvor" ou a ordem do culto em execução, gerenciando a lista ativa e a troca de itens no telão. |
| `BibleFind.java` *(no módulo bíblia)* | Localização rápida de passagens bíblicas digitadas pelo operador ou solicitadas via atalho. |
| `ThemeController.java` | Gerencia temas visuais (fontes, tamanhos, cores, alinhamentos verticais/horizontais e sombras) aplicáveis aos slides. |
| `AnnouncementController.java` | Controle de avisos paroquiais, slides com contagem regressiva e comunicados em tela. |
| `AlertController.java` | Disparo de mensagens de alerta instantâneas sobrepostas ao telão (ex: avisos de trânsito, berçário). |
| `MemberController.java` | Cadastro e gerenciamento dos membros da equipe de louvor e operadores. |
| `ScheduleController.java` | Gerenciamento de escalas semanais de voluntários e equipes. |
| `EventController.java` | Agendamento e gerenciamento de cultos especiais, conferências e ensaios. |
| `FavoriteController.java` | Lista rápida de músicas marcadas como favoritas para acesso imediato. |
| `MusicFavoriteSetController.java` | Conjuntos agrupados de músicas favoritas por ministro de louvor ou evento. |
| `MyImagesController.java` | Catálogo e galeria de imagens estáticas do usuário utilizadas como plano de fundo. |
| `MyVideosController.java` | Catálogo e galeria de vídeos animados (loops) para background. |
| `ProfileController.java` | Perfis de configuração que permitem chavear resoluções e layouts dependendo do templo ou culto. |
| `ThumbCacheController.java` | Sistema de cache em memória/disco de miniaturas para evitar engasgos ao listar centenas de mídias. |
| `ThumbMediaController.java` | Geração assíncrona de miniaturas para arquivos de vídeo e imagem. |
| `TransitionController.java` | Coordena os efeitos de transição visual (fade, slide, dissolve) entre telas. |
| `AppMessageController.java` | Servidor de mensageria para sincronização com o aplicativo Holyrics Remote. |
| `ControllerFactory.java` | Padrão *Factory* para instanciar e desacoplar os controladores do sistema. |
| `ControllerMapFactory.java` | Registro centralizado dos controladores ativos no sistema. |

---

### 1.3 Camada de Persistência (`com.limagiran.holyrics.dao`)
- `MusicDao.java`: Responsável pelas consultas SQL diretas, inserção, atualização e deleção de músicas, artistas e letras no banco `holyrics.db`.

---

### 1.4 Telas e Painéis de Edição (`com.limagiran.holyrics.edit`)
Componentes gráficos para cadastro e personalização:
- `EditPanel.java`: Painel base para edição de dados.
- `EditDialog.java`: Janela modal de diálogo para formulários.
- `EditMember.java`: Formulário de membros e funções.
- `EditProfile.java`: Editor de perfis de exibição e resoluções.
- `EditSchedule.java`: Editor visual da grade de escalas.
- `EditEvent.java`: Formulário de criação de eventos na agenda.

---

### 1.5 Utilitários de Apresentação e Mídia (`com.limagiran.holyrics.util`)
- `PresentationRendererUtils.java`: Algoritmos de cálculo tipográfico, quebra de linha inteligente por estrofe e renderização em gráficos Java 2D.
- `HolyricsStreaming.java`: Módulo de streaming de legendas e slides em tempo real para OBS Studio, vMix ou web navegadores.
- `SQLite.java`: Gerenciador de conexão JDBC com a base de dados embutida, tratando transações seguras e locks.
- `MediaPlayerUtils.java`: Controles de reprodução de áudio/vídeo integrados ao VLCJ.

---

## 📖 2. Módulo Bíblico (`com.limagiran.bible`)

Um subsistema autônomo e de alta performance:

- `Bible.java`: Estrutura de dados central contendo o cânon bíblico.
- `Book.java`: Representação de livro bíblico (abreviações, quantidade de capítulos, testamento).
- `Verse.java`: Representação do versículo (texto, número, capítulo e livro).
- `BibleFind.java`: Algoritmo de busca por palavras-chave com relevância e filtros de Antigo/Novo Testamento.
- `AvailableVersions.java`: Gerenciador de traduções disponíveis instaladas (ex: Almeida, NVI, ARC).
- `VerseSearchPanel.java` & `VerseSearchView.java`: Interface gráfica de busca instantânea com suporte a digitação abreviada (ex: `Sl 23 1`  Salmos 23:1).
- `DownloadView.java` & `AvailableDownloads.java`: Tela de download de novas versões bíblicas diretamente da nuvem.

---

## 💾 3. Módulo de Serialização e Arquivos (`com.limagiran.lgsaveopen`)

Biblioteca responsável pelo formato de arquivo `.hly`:

- `LGSaveOpen.java`: API de alto nível para gravação e leitura de arquivos `.hly`.
- `SerialMap.java`: Contêiner serializado em Java que empacota objetos de configuração, listas e metadados.
- `Utils.java`: Métodos auxiliares de compressão e verificação de integridade dos arquivos salvos.

---

## 🔄 4. Módulo de Atualizações (`com.limagiran.javaupdate`)

- `JavaUpdate.java`: Ponto de entrada do atualizador independente.
- `Update.java`: Verificador HTTP de versões mais recentes e download de patches.
- `IUpdate.java`: Interface para callbacks de progresso do download na interface.

---

## 🎵 5. Módulo Vagalume API (`com.limagiran.vagalumeapi`)

Cliente de integração REST com o portal Vagalume:

- `VagalumeAPI.java`: Cliente HTTP principal.
- `Search.java`: Execução de buscas por termos, títulos e trechos de letras.
- `Artist.java`: Mapeamento de dados do artista/banda.
- `Music.java`: Mapeamento da letra completa e metadados musicais.
- `Download.java`: Utilitário de requisição e parsing de JSON.
