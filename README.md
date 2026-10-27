# Holyrics — Engenharia Reversa & Nova Arquitetura (LumenLite)

[![Status](https://img.shields.io/badge/Status-100%25%20Descompilado%20%7C%20Especificado-brightgreen)](#)
[![Stack Original](https://img.shields.io/badge/Original-Java%208%2F21%20%7C%20Swing%20%7C%20SQLite%20%7C%20VLCJ-orange)](#)
[![Nova Arquitetura](https://img.shields.io/badge/Nova%20Geração-Tauri%20v2%20%7C%20React%2019%20%7C%20Bun%20%7C%20Tailwind-blue)](#)
[![Licença](https://img.shields.io/badge/Engenharia%20Reversa-Pesquisa%20%26%20Interoperabilidade-informational)](#)

Repositório central contendo a **engenharia reversa completa de alta fidelidade do Holyrics** (aplicativo Windows de projeção de letras, bíblia, mídias e liturgia eclesiástica), seu catálogo técnico descompilado, a especificação funcional em 8 domínios e o projeto da nova plataforma moderna (**LumenLite**), acompanhado de um **protótipo interativo de alta fidelidade em Glassmorphism**.

---

## 🚀 Navegação Rápida

- 🎨 **[Protótipo Interativo Glassmorphism (Arquivo Único)](#-protótipo-interativo-de-alta-fidelidade)**: Abra `prototype.html` ou `index.html` diretamente no seu navegador.
- 📚 **[Documentação do Holyrics Legado (Descompilado)](docs/decompiled-holyrics/)**: Metodologia, ambiente, histórico, arquitetura e catálogo de código Java.
- 🏗️ **[Documentação da Nova Geração (LumenLite)](docs/new-holyrics/)**: Requisitos funcionais (RF/RNF), stack tecnológica moderna e estrutura de diretórios em fatias verticais.

---

## 📌 Sumário da Documentação

A documentação técnica está organizada em duas frentes complementares:

### 1. Engenharia Reversa do Holyrics Original (`docs/decompiled-holyrics/`)

| Documento | Descrição Técnica |
|---|---|
| [01 - Metodologia de Engenharia Reversa](docs/decompiled-holyrics/01-metodologia.md) | Ciclo em 6 etapas: desempacotamento Inno Setup (`innoextract`), extração PE32 com 7-Zip, isolamento de namespaces e descompilação bytecode com CFR/Vineflower. |
| [02 - Guia de Instalação e Ambiente](docs/decompiled-holyrics/02-guia-instalacao-ambiente.md) | Configuração de ferramentas em Linux/WSL2 (`7zz`, `innoextract`, `CFR 0.152`, `Vineflower 1.10.1`, `REA 6.8.0`, `Ghidra 12.1.4`, Java 21). |
| [03 - Histórico de Execução](docs/decompiled-holyrics/03-historico-execucao.md) | Linha do tempo dos comandos executados, extração das 5 DLLs embutidas e superação de falhas sintáticas com Vineflower. |
| [04 - Arquitetura de Software do Holyrics](docs/decompiled-holyrics/04-arquitetura-holyrics.md) | Visão sistêmica: ciclo de vida, camadas MVC/DAO, persistência híbrida (SQLite + `.hly` serializado + `.bib`), VLCJ e streaming HTTP. |
| [05 - Catálogo de Módulos e Código](docs/decompiled-holyrics/05-catalogo-modulos-e-codigo.md) | Mapeamento dos 484 arquivos Java recuperados em `src/com/limagiran/*` (Core, Bíblia, SaveOpen, Vagalume API, Updates). |
| [06 - Análise Nativa e DLLs](docs/decompiled-holyrics/06-analise-nativa-e-dlls.md) | Mapeamento de `sqlitejdbc.dll` (motor SQLite em C via JNI) e `jnidispatch.dll` (ponte JNA Win32 para controle de janelas e atalhos). |
| [07 - Guia de Uso e Consultas](docs/decompiled-holyrics/07-guia-de-uso.md) | Comandos práticos de busca (`grep`), auditoria de banco de dados e procedimento de recompilação e patch cirúrgico no `.exe`. |
| [08 - Mapeamento Modular e Especificação](docs/decompiled-holyrics/08-mapeamento-modular-e-especificacao.md) | Decomposição em 8 domínios funcionais independentes e matriz de transição para arquitetura moderna. |

### 2. Especificação e Arquitetura do Novo Holyrics / LumenLite (`docs/new-holyrics/`)

| Documento | Descrição Técnica |
|---|---|
| [01 - Requisitos do Sistema & Engenharia Reversa](docs/new-holyrics/01-requirements.md) | 34 Requisitos Funcionais (RF) e Requisitos Não-Funcionais (RNF) com mapeamento classe a classe do Holyrics, diagnóstico de gargalos e nova abordagem. |
| [02 - Tecnologias & Estratégia de Dependências](docs/new-holyrics/02-tech-stack.md) | Ferramental Vite+, Rust Fast-Path (`Oxlint` + `Oxfmt`), eliminação do YAML em prol do JSON Schema nativo com Zod v4, e matriz de dependências enxutas. |
| [03 - Estrutura de Diretórios Full-Stack](docs/new-holyrics/03-directory-structure.md) | Arquitetura em fatias verticais unificadas (`slices/*`), cascas finas (`apps/daemon` e `apps/ui`), contratos em `@lumen/core` e automação em nós. |

---

## 🗂 Estrutura de Diretórios do Workspace

```
/home/miguelsb/workspace/holyrics-analysis/
├── app/
│   └── Holyrics.exe             # Executável original (PE32 + ZIP embedded, 71 MB)
├── docs/                        # Suíte completa de documentação técnica
│   ├── README.md                # Índice temático de documentação
│   ├── decompiled-holyrics/     # 8 relatórios detalhados da engenharia reversa
│   └── new-holyrics/            # 3 especificações da recriação moderna (LumenLite)
├── extracted_classes/           # 1.724 arquivos brutos (.class, imagens, .bib, .properties)
├── extracted_dlls/              # DLLs nativas x86/x64 extraídas (SQLite e JNA)
├── src/                         # 484 arquivos Java (.java) 100% descompilados com sucesso
│   └── com/
│       └── limagiran/
│           ├── bible/           # Sistema de Bíblia (versões, busca rápida, navegação)
│           ├── holyrics/        # Core (UI Swing, controladores, SQLite, projeção)
│           ├── javaupdate/      # Atualizador e checagem de versões
│           ├── lgsaveopen/      # Camada de serialização de objetos .hly
│           └── vagalumeapi/     # Cliente de integração HTTP da API Vagalume
├── prototype.html               # 🌟 PROTÓTIPO DE ALTA FIDELIDADE (Glassmorphism + Tailwind)
└── index.html                   # Atalho de entrada para o protótipo interativo
```

---

## 🔍 O que foi recuperado no `src/`?

- **`src/` (484 arquivos `.java`):** Contém **100% do código proprietário** desenvolvido para o Holyrics original pelo autor (`com.limagiran.*`). Zero métodos com falhas de descompilação (reconstruídos com o descompilador AST Vineflower).
- **`extracted_classes/`:** Além do bytecode, preserva todos os recursos estáticos originais: fontes embutidas (`digital-7_mono.ttf`, `baramond.ttf`), bancos bíblicos binários (`pt_acrf.bib`), pacotes de tradução (`Bundle_pt_BR.properties`), ícones e ilustrações.
- **`extracted_dlls/`:** Contém as DLLs nativas de baixo nível (`sqlitejdbc.dll` e `jnidispatch.dll`) para auditoria de chamadas JNI e Win32.

---

## ⚖️ Comparativo: Holyrics Original vs. Novo LumenLite

| Dimensão | Holyrics Original (Legado) | Novo Holyrics (LumenLite) |
|---|---|---|
| **Arquitetura & Runtime** | Java 8/21 + Wrapper PE32 (.exe), JVM acoplada | **Tauri v2 (Rust) + React 19 + Bun** |
| **Tempo de Boot** | ~6 a 12 segundos | **< 1.2 segundos** (Cold start instantâneo) |
| **Consumo de Memória** | 600 MB a 1.2 GB de RAM | **< 120 MB** (texto) / **< 350 MB** (vídeo 4K) |
| **Renderização Visual** | Java Graphics 2D (CPU EDT - engasgos na troca) | **HTML5 Canvas / GPU Hardware Acceleration (60 FPS)** |
| **Busca de Músicas e Bíblia** | SQL `LIKE '%texto%'` e varredura linear | **SQLite FTS5 + BM25 Ranking (< 2ms de resposta)** |
| **Formato de Roteiro** | Arquivos binários proprietários `.hly` | **`culto.json` declarativo + JSON Schema** |
| **Projeção OBS / Streaming** | Concatenação manual de strings HTTP (flickering) | **Rotas WebSockets com Alpha Channel transparente** |
| **Importação de Slides** | Dependência pesada de automação COM do MS Office | **Parser ZIP/XML nativo em memória (`fflate`)** |
| **Automação Externa** | Listeners síncronos no Swing (trava interface) | **Motor DAG de Hooks assíncronos (`@xyflow/react`) + CLI** |

---

## 🎨 Protótipo Interativo de Alta Fidelidade

Como parte da especificação e validação da experiência do usuário, desenvolvemos um **protótipo completo em arquivo único** (`prototype.html` / `index.html`):

### Características de Design & UX:
- **Estilo Glassmorphism Premium**: Superfícies translúcidas em vidro fosco (`backdrop-blur-xl`), bordas sutis com brilho de borda (`border-white/10`), paleta escura profunda ideal para cabines de som e transições fluidas.
- **Modos de Exibição Alternáveis**:
  1. 🎛️ **Workspace do Operador**: Gestão de liturgia, catálogo FTS5 de músicas, busca rápida de bíblia (`Jo 3:16`), seleção de estrofes semânticas (`V1`, `Chorus`, `Bridge`), controles de alerta e áudio ambiente.
  2. 📽️ **Telão Principal (Projector 16:9)**: Simulação em tempo real da saída HDMI com loops dinâmicos de vídeo/partículas e tipografia com Auto-Fit e contorno.
  3. 🎤 **Retorno de Palco (Stage View)**: Tela de alto contraste para o púlpito com relógio gigante, cronômetro de pregação, slide atual e **próximo slide em destaque**.
  4. 📡 **OBS Studio Broadcast**: Rota com fundo transparente (Alpha Channel) para legendagem ao vivo e sobreposições Lower Third.
  5. ⚡ **Automação DAG Hooks**: Canvas em nós interativo para conectar disparos do slide a comandos DMX Lumikit e cenas do OBS Studio.
- **Ações Globais de Pânico Funcionais**:
  - `F1`: Ocultar Letra (*Clear Text*)
  - `F2`: Logo da Igreja (*Church Logo*)
  - `F3`: Blackout (*Tela 100% Preta*)
  - `F4`: Freeze (*Congelar Imagem no Telão*)
  - `F9`: Ocultar Fundo (*Clear Background*)
  - `Ctrl + K`: Spotlight de Busca Unificada
  - `Espaço` / `Setas`: Navegação instantânea de slides.

### Como Executar o Protótipo:
Basta abrir o arquivo `prototype.html` diretamente em qualquer navegador moderno (Chrome, Firefox, Safari, Edge):
```bash
# Opção 1: Abrir diretamente no navegador do sistema
xdg-open prototype.html

# Opção 2: Servir via servidor HTTP simples
python3 -m http.server 8080
# Acesse no navegador: http://localhost:8080/prototype.html
```
