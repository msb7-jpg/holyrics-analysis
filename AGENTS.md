# Guia de Engenharia e Regras de Agentes: Port 1:1 Isomórfico (Holyrics)

Este repositório é dedicado a um **Port Isomórfico 1:1** do software Holyrics original, saindo da linguagem Java (Swing) para um ecossistema Web/Desktop baseado em **Bun, React e TypeScript**.

O objetivo primário é realizar uma cópia **perfeita, pixel a pixel e lógica por lógica**, das telas, funcionalidades, menus, fluxos e estruturas de dados do Holyrics antigo. 

**TODA tentativa de "modernizar" o layout, adotar novos padrões arquiteturais diferentes dos do Holyrics original ou criar telas com identidades visuais modernas está ESTRITAMENTE PROIBIDA nesta fase.** A refatoração e modernização só ocorrerão após a conclusão do port.

---

## 1. Princípios de Engenharia do Port Isomórfico

1. **Mapeamento Direto de Arquivos (Isomorfismo Arquitetural):**
   - Para cada arquivo `.java` original no diretório `src/com/limagiran/holyrics/` (e relacionados), deve existir um arquivo `.ts` ou `.tsx` correspondente no novo diretório raiz do port.
   - Exemplo: `com.limagiran.holyrics.model.Musica` ➔ `src/model/Musica.ts`.
   - Exemplo: `com.limagiran.holyrics.view.MainFrame` ➔ `src/view/MainFrame.tsx`.
   - A estrutura de pacotes original (`control`, `model`, `view`, `dao`, `i18n`, `worker`, etc.) DEVE ser replicada na nova arquitetura.

2. **Interface Gráfica (Look & Feel Original):**
   - **Sem modernização visual:** A interface NÃO deve usar bibliotecas de design system modernas, material design ou tailwind com espaçamentos largos.
   - **Emulação do Swing no React:** Os componentes React devem imitar visualmente os componentes Java Swing originais:
     - `JFrame`, `JDialog`: Estrutura de janelas.
     - `JSplitPane`: Divisórias clássicas redimensionáveis.
     - `JTable`, `JTree`: Tabelas de alta densidade de dados com linhas estreitas e bordas de tabela clássicas.
     - `JTabbedPane`: Abas clássicas em tons de cinza/neutro.

3. **Substituição Tecnológica Fiel:**
   - **Reprodutor Multimídia:** Onde o Java utilizava `vlcj`, utilizaremos `<video>` HTML5 ou integrações FFI para bibliotecas nativas como `libvlc`/`mpv`, mantendo o exato mesmo modelo de controle.
   - **Banco de Dados:** A persistência local em H2/SQLite ou arquivos em disco usará `bun:sqlite` e leitura de arquivos nativa, mantendo o esquema e serialização exatos.
   - **Rede e Servidores:** Os Sockets TCP do Java (App Móvel e Comunicação) serão mapeados para Sockets nativos do Bun ou WebSockets.
   - **Gerenciamento de Janelas e Projeção:** API de Múltiplas Telas, abrindo janelas separadas para a projeção (`window.open` multi-display ou uso do framework desktop escolhido: Electron/Tauri) simulando o AWT `GraphicsDevice`.

4. **Código Legado como Gabarito:**
   - O código descompilado presente em `src/com/` deve ser lido ativamente e sua lógica de negócio implementada fielmente no TypeScript.
   - Os assets originais (`extracted_classes/`, `extracted_dlls/`) devem ser usados para mapear ícones e dependências corretas.

---

## 2. Checklists e Controle de Progresso

- O controle de evolução e documentação de engenharia reversa deve ser mantido na pasta `docs/`.
- Qualquer novo recurso ou modernização solicitada que divirja do funcionamento do software Java antigo na sua versão atual deve ser negada com base na regra de **port 1:1**.

## 3. Toolchain Base

- **Runtime:** Bun
- **Frontend UI:** React + TypeScript (emulando Swing)
- **Persistência:** bun:sqlite, node:fs
- **Empacotamento (Futuro):** Tauri / Electron (a definir, foco na camada web isomórfica primeiramente).
