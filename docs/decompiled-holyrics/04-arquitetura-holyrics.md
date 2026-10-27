# 04 - Arquitetura de Software do Holyrics

Este documento descreve a arquitetura de software, padrões de projeto e subsistemas internos do **Holyrics**, identificados a partir da análise dos 484 arquivos Java recuperados em `src/`.

---

## 🏛 Visão Macro da Arquitetura

O Holyrics é construído como uma aplicação desktop multithread com arquitetura baseada em **MVC (Model-View-Controller)** adaptado para Java Swing, associado a camadas de **DAO (Data Access Object)** e integração de mídia nativa.

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Camada de Apresentação (GUI)                    │
│   Java Swing (WebLaF Look & Feel) + JavaFX (JFXPanel) para Web/Media   │
│   Telas de Edição (EditPanel)  │  Painel de Projeção  │  Palco/Retorno │
└───────────────────────────────────┬────────────────────────────────────┘
                                    │
┌───────────────────────────────────▼────────────────────────────────────┐
│                        Camada de Controle (Controllers)                │
│   MusicController  │  WorshipController  │  BibleFind  │  AlertCtrl    │
│   EventController  │  ThemeController    │  MediaCtrl  │  ScheduleCtrl │
└──────────────────────┬────────────────────────────┬────────────────────┘
                       │                            │
┌──────────────────────▼───────┐   ┌────────────────▼────────────────────┐
│      Lógica de Projeção      │   │    Persistência e Armazenamento     │
│   PresentationRendererUtils  │   │   SQLite Database (SQLite.java)     │
│   HolyricsStreaming (OBS)    │   │   MusicDao / HistoryDao             │
│   VLCJ Player & Transitions  │   │   Arquivos .hly (LGSaveOpen)        │
│   Múltiplos Monitores/Telões │   │   Banco Bíblico (.bib)              │
└──────────────────────────────┘   └─────────────────────────────────────┘
```

---

## 🚀 Ciclo de Inicialização da Aplicação

O ponto de entrada principal é `com.limagiran.holyrics.Holyrics` (invocado por `Run.java`):

1. **Checagem de Instância Única:**
   Cria um socket ou arquivo de lock temporário para evitar que duas instâncias do programa rodem ao mesmo tempo e corrompam a base SQLite.
2. **Registro de Fontes e Tema Visual:**
   - Carrega as fontes embutidas `digital-7_mono.ttf` e `baramond.ttf` diretamente na JVM via `Font.createFont()`.
   - Inicializa a biblioteca **WebLaF** (`WebLookAndFeel.install()`), configurando os estilos visuais de botões, tooltips, tabelas e menus.
3. **Inicialização do Banco de Dados SQLite:**
   A classe `com.limagiran.holyrics.util.SQLite` inicializa a conexão JDBC com o arquivo `holyrics.db` no diretório de dados do usuário (`AppData/Roaming/Holyrics`), executando migrações de esquema se necessário.
4. **Carregamento de Configurações (`Settings.java`):**
   Restaura resoluções de tela, monitor selecionado para projeção, caminhos de mídia, atalhos de teclado e preferências de idioma (via `I18N.java` e `Bundle_pt_BR.properties`).
5. **Construção da Janela Principal e do Telão:**
   - Cria o painel do operador (janela com abas para pesquisa de músicas, controle de culto, bíblia, avisos e mídias).
   - Cria a janela do projetor (`Screen` / `SecondaryWindow`), fixada na segunda saída de vídeo (HDMI/VGA) em tela cheia sem bordas.

---

## 🗄 Persistência e Modelos de Dados

O Holyrics emprega uma estratégia híbrida de persistência:

### 1. Banco Relacional SQLite (`holyrics.db`)
Gerenciado por `SQLite.java` e acessado via DAOs como `MusicDao.java`:
- **Tabela de Músicas (`musics`):** Título, compositor, artista, tonalidade, tags, data de inclusão e texto da letra estruturado com marcações de estrofe, coro e ponte.
- **Histórico (`history`):** Registro de todas as músicas exibidas em cada data/culto, permitindo gerar relatórios de frequência de hinos.
- **Membros e Escalas (`members`, `schedules`):** Equipe de louvor, músicos, pregadores e operadores de mídia associados aos eventos.

### 2. Formato Proprietário de Arquivo `.hly` (`com.limagiran.lgsaveopen`)
Para exportar e importar itens isolados sem depender de SQL direto, utiliza-se a biblioteca `LGSaveOpen`:
- Os arquivos `.hly` são mapas binários serializados (`SerialMap.java`) que armazenam temas (`themes.hly`), modelos de culto, acordes e configurações de transição.
- Permite compartilhamento fácil de músicas entre igrejas via pendrive ou e-mail.

### 3. Banco Bíblico (`.bib`)
As traduções da Bíblia (ex: `pt_acrf.bib` - Almeida Corrigida e Fiel) utilizam estruturas binárias otimizadas para leitura randômica veloz, permitindo que a busca por capítulos e versículos responda instantaneamente durante o culto.

---

## 📽 Motor de Renderização e Projeção

Um dos pontos mais críticos do software é a projeção sem atraso:

1. **`PresentationRendererUtils`:**
   Responsável pelo cálculo de quebra de linhas, ajuste dinâmico do tamanho da fonte para que o texto nunca ultrapasse os limites da tela do projetor, e aplicação de efeitos de contorno/sombra para garantir contraste sobre qualquer fundo.
2. **Suporte a Fundos Dinâmicos (Vídeo e Imagem):**
   - Usa **VLCJ** (wrapper sobre o VLC) para reproduzir vídeos de plano de fundo em loop com aceleração por hardware.
   - Suporta fundos animados em GIF e imagens estáticas (JPG/PNG).
3. **Transições:**
   O pacote `com.limagiran.holyrics.transition` provê efeitos de *crossfade*, *slide* e *wipe* entre estrofes ou entre mídias diferentes.
4. **Holyrics Streaming (OBS Studio / NDI):**
   A classe `HolyricsStreaming.java` gera uma camada de texto sobre transparência ou chroma key, exportando via servidor HTTP local ou canvas em tempo real para ser capturada como "Browser Source" no OBS Studio.

---

## 🌐 Integrações de Rede e Web Services

O pacote `com.limagiran.holyrics.webservice` implementa servidores leves na máquina do operador:

1. **Holyrics Remote (App Mobile):**
   Disponibiliza uma API REST/WebSocket na rede local (Wi-Fi da igreja), permitindo que o pastor ou líder de louvor use um smartphone para:
   - Trocar versículos e slides remotamente.
   - Ver a música atual e as próximas da lista.
   - Enviar alertas de texto emergenciais (ex: placa de carro, recado de berçário).
2. **API Vagalume (`com.limagiran.vagalumeapi`):**
   Cliente HTTP integrado que consulta o serviço Vagalume para baixar letras de músicas e cifras automaticamente pela internet com um clique.
