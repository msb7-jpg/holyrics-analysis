# Documentação de Engenharia Reversa e Arquitetura do Holyrics & LumenLite

Bem-vindo ao índice central da documentação técnica do projeto de análise do **Holyrics** (aplicativo Windows para projeção de letras, bíblia, mídias e liturgia em igrejas) e especificação da nova plataforma moderna (**LumenLite**).

---

## 📌 Sumário da Documentação

### 1. Engenharia Reversa do Holyrics Original (`decompiled-holyrics/`)

| Arquivo | Descrição |
|---|---|
| [01 - Metodologia de Engenharia Reversa](decompiled-holyrics/01-metodologia.md) | Princípios, etapas (reconhecimento, desempacotamento PE, descompilação bytecode, análise nativa) e taxonomia do projeto. |
| [02 - Guia de Instalação e Configuração do Ambiente](decompiled-holyrics/02-guia-instalacao-ambiente.md) | Passo a passo completo de instalação de todas as ferramentas (`7zz`, `innoextract`, `CFR`, `Vineflower`, `REA 6.8.0`, `Ghidra 12.1.4`, Java 21, MCP). |
| [03 - Histórico e Relatório de Execução](decompiled-holyrics/03-historico-execucao.md) | Registro cronológico exato das ações executadas, comandos executados no terminal e saídas verificadas. |
| [04 - Arquitetura de Software do Holyrics](decompiled-holyrics/04-arquitetura-holyrics.md) | Visão sistêmica: ciclo de vida, camadas (MVC/DAO), GUI (Swing + WebLaF + JavaFX), SQLite, VLCJ, persistência `.hly` e streaming. |
| [05 - Catálogo de Módulos e Código-Fonte](decompiled-holyrics/05-catalogo-modulos-e-codigo.md) | Dicionário técnico completo de pacotes e classes de `src/` (`com.limagiran.*`), controllers, models, views e DAOs. |
| [06 - Análise de Binários Nativos e DLLs](decompiled-holyrics/06-analise-nativa-e-dlls.md) | Mapeamento das DLLs embutidas (`sqlitejdbc.dll`, `jnidispatch.dll`) e integração com Ghidra e REA CLI. |
| [07 - Guia de Consulta, Busca e Próximos Passos](decompiled-holyrics/07-guia-de-uso.md) | Dicas práticas para pesquisar regras de negócio, inspecionar banco de dados, reconstruir telas e auditar integrações externas. |
| [08 - Mapeamento Modular e Especificação Funcional](decompiled-holyrics/08-mapeamento-modular-e-especificacao.md) | Mapeamento completo de capacidades, 8 domínios funcionais, modelo de ficha de especificação e roadmap para recriação. |
| [09 - Catálogo Completo de Telas, Posicionamento e Funcionalidades](decompiled-holyrics/09-catalogo-telas-posicionamento-e-funcionalidades.md) | **Inventário exaustivo tela a tela** (93 classes UI, 57 janelas) com wireframes ASCII, menus, botões, posições e códigos de disparo. |

---

### 2. Especificação da Nova Geração (`new-holyrics/` - LumenLite)

| Arquivo | Descrição |
|---|---|
| [01 - Requisitos do Sistema & Engenharia Reversa](new-holyrics/01-requirements.md) | 34 Requisitos Funcionais detalhados com mapeamento do código antigo, diagnóstico de gargalos e nova abordagem (FTS5, GPU, WebSockets, DAG). |
| [02 - Tecnologias, Ferramentas & Estratégia](new-holyrics/02-tech-stack.md) | Stack Vite+, Rust Fast-Path (`Oxlint` + `Oxfmt`), eliminação do YAML em favor de JSON Schema nativo com Zod v4, e contratos `@lumen/core`. |
| [03 - Estrutura Completa de Diretórios](new-holyrics/03-directory-structure.md) | Arquitetura monorepo em fatias verticais unificadas (`slices/*`), cascas finas (`apps/daemon` e `apps/ui`), e componentes completos. |
| [04 - Mapeamento de Funcionalidades Legadas & Novos Menus](new-holyrics/04-missing-features-mapping.md) | De-para dos 7 módulos legados de cabine e modelagem dos componentes modernos em React 19. |
| [05 - Matriz de Telas, Layout e Alocação de Controles](new-holyrics/05-matriz-telas-layout-e-controles.md) | **Matriz mestre de decisão e poda** portando 100% dos recursos para as 5 zonas do Holyrics Studio para revisão e corte pelo usuário. |

---

## 🗂 Estrutura de Diretórios do Workspace

```
/home/miguelsb/workspace/holyrics-analysis/
├── app/
│   └── Holyrics.exe             # Executável original extraído (PE32 + ZIP embedded, 71 MB)
├── docs/                        # Suíte completa de documentação técnica
│   ├── README.md                # Este índice principal
│   ├── decompiled-holyrics/     # 8 relatórios detalhados do sistema original
│   └── new-holyrics/            # 3 especificações completas da nova geração (LumenLite)
├── extracted_classes/           # 1.724 arquivos brutos (.class + recursos como imagens, .bib e .properties)
├── extracted_dlls/              # DLLs nativas x86/x64 extraídas para inspeção em Ghidra
├── src/                         # 484 arquivos Java (.java) descompilados com CFR/Vineflower
│   └── com/
│       └── limagiran/
│           ├── bible/           # Sistema de Bíblia (versões, busca rápida, navegação)
│           ├── holyrics/        # Core da aplicação (UI, controladores, SQLite, projeção)
│           ├── javaupdate/      # Mecanismo de verificação e download de atualizações
│           ├── lgsaveopen/      # Camada de serialização de objetos e arquivos .hly
│           └── vagalumeapi/     # Cliente de integração com a API Vagalume
├── prototype.html               # 🌟 Protótipo interativo de alta fidelidade (Glassmorphism + Tailwind)
└── index.html                   # Atalho de entrada para o protótipo
```

---

## 🔍 O que está no `src/` vs o restante do executável?

- **`src/` (484 arquivos `.java`):** Contém **100% do código proprietário** desenvolvido para o Holyrics. Toda a lógica de negócios, controllers de música, cultos, membros, apresentações, integração de vídeo, layouts e telas Swing foi recuperada aqui.
- **`extracted_classes/`:** Além dos arquivos `.class`, contém os assets originais (ícones PNG, imagens de fundo, bancos bíblicos `.bib`, fontes `.ttf` e textos de ajuda HTML).
- **Bibliotecas de terceiros no `.exe` original:** O `Holyrics.exe` continha mais de 12.000 classes incluindo bibliotecas públicas (WebLaF, JavaFX, VLCJ, SQLite JDBC, JNA, iText). O código proprietário foi isolado com precisão no namespace `com.limagiran.*`.
