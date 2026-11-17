---
name: lumen-domain
description: "Dicionário de domínio, mapeamento espacial de telas e componentes, linguagem ubíqua e arquitetura do LumenLite (port isomórfico/moderno do Holyrics)."
---

# Skill: Domínio, Arquitetura e Mapeamento Espacial do LumenLite

Esta skill documenta o modelo mental, a linguagem ubíqua, a disposição espacial física/visual e a topologia de código do **LumenLite** — a plataforma moderna e de alta fidelidade para projeção litúrgica em igrejas (port evolutivo do Holyrics original).

Qualquer agente ou desenvolvedor atuando no LumenLite deve utilizar esta skill como fonte canônica de termos, layout de interface e mapeamento de componentes.

---

## 1. Visão Geral do Sistema e Filosofia de Operação

O LumenLite foi projetado especificamente para a **cabine multimídia de igreja**, onde o operador trabalha sob alta pressão e ambientes de baixa luminosidade.

### Princípios Operacionais Fundamentais:
1. **Design System "Slate Pro Studio Refined" (v1.4.0):**
   - Hierarquia de superfícies por luminosidade contínua: Nível 0 (`#0B0D10`), Nível 1 (`#11141A`), Nível 2 (`#141820` / `#181C24`) com hover (`#1F242F`), microborda `border-white/5` e eliminação de caixas sobrepostas ("boxiness"). Detalhado em `docs/UI_REFRESH_GUIDE.md`.
   - Tipografia limpa sem poluição visual: letras sem crachás intrusivos (`[V1]`, `[CORO]`) na visualização ao vivo e numeração tabular monotípica (`tabular-nums font-mono`).
   - Sem "luzes de semáforo" berrantes: estados sutis onde o verde esmeralda (`#10B981` / `bg-emerald-950/25`) sinaliza exclusivamente "No Ar / Live" e o azul céu (`sky-500` / `#18202C`) representa "Preview / Workspace".
   - Segmented controls estilo Raycast/macOS em pílula única para abas e botões de pânico (`PanicToolbar.tsx`).
2. **Determinismo e Tolerância a Falhas:**
   - O núcleo do sistema (`@lumen/core`) opera como uma Máquina de Estados Finita (FSM) imutável e determinística.
   - Pânico instantâneo acessível a qualquer momento (Blackout / Clear / Logo).
3. **Multi-Display Nativo e Desacoplado:**
   - A interface do operador não projeta nada diretamente em sua janela: envia o estado via WebSocket/Tauri IPC para renderizadores dedicados (Telão HDMI, Stage Monitor e Mobile Remote).

---

## 2. Mapeamento Espacial da Interface do Operador (Console Central)

A interface principal do operador (`OperatorView.tsx`) é organizada ergonomicamente em um fluxo da esquerda para a direita: **Preparar ➔ Operar ➔ Monitorar & Controlar**.

```
+---------------------------------------------------------------------------------------------------------+
| [TOP BAR] LumenLite | Status Conexão | Modos de Pânico (Preto / Limpar / Logo) | Relógio | Spotlight/Modais  |
+---------------------------------------------------------------------------------------------------------+
| [COLUNA 1]        | [COLUNA CENTRAL / WORKSPACE]                    | [CANTO DIREITO SUPERIOR]          |
| 20%               | 55% (Colunas 2 e 3 integradas)                  | 25%                               |
|-------------------|-------------------------------------------------|-----------------------------------|
| Roteiro / Culto   | Grade de Conteúdo Ativo                         | Monitor de Saída (16:9 Live)      |
| Itens Litúrgicos  | - Letra da Música (Stanzas / Versos)            | - Preview em tempo real do Telão  |
| - Músicas         | - Bíblia Sagrada (Capítulos e Versículos)       | - Badge [NO AR]                   |
| - Versículos      | - Slides PPTX (Miniaturas e Navegação)          | - Ações de Projeção / Janela      |
| - Avisos          |                                                 |-----------------------------------|
| - Mídias          | Preview e Seleção de Versos                     | [CANTO DIREITO INFERIOR]          |
|                   | Navegação por Teclado (Setas / Enter)           | Acessórios e Fundos Rápidos       |
| Adicionar Item    |                                                 | - Seletor de Fundo (Motion/Loop)  |
| Histórico / Ordem |                                                 | - Mensagens Rápidas (Alerta Carro)|
| Reordenar (D&D)   |                                                 | - Contagem Regressiva / Countdown |
+---------------------------------------------------------------------------------------------------------+
```

### Detalhamento das Regiões:

1. **Topo (`OperatorTopBar`):**
   - Identidade visual da aplicação e status de integridade do daemon (indicador verde/vermelho).
   - Botões de Pânico / Ação Instantânea:
     - `Preto` (`BLANK`): Apaga completamente o telão (blackout total).
     - `Limpar` (`CLEAR`): Remove instantaneamente o texto/legenda, mantendo o fundo de louvor ativo.
     - `Logo` (`LOGO`): Substitui a projeção pelo logotipo oficial configurado da igreja.
   - Relógio litúrgico de alta precisão.
   - Acesso rápido à Paleta de Comandos Universal (`SpotlightDialog` via `Cmd+K` / `Ctrl+K`) e ícones de acionamento de modais técnicos.

2. **Canto Esquerdo / Coluna 1 (`LiturgyScheduleColumn`):**
   - Roteiro oficial do culto em execução (`culto.json`).
   - Lista sequencial dos momentos litúrgicos: Prelúdio, Louvores, Oração, Leitura Bíblica, Avisos, Mensagem Pastoral.
   - Permite arrastar e soltar (reordenar), renomear, marcar itens como concluídos e disparar um item diretamente para o Workspace.

3. **Coluna Central / Workspace (`SongStanzaGrid`, `BibleVerseGrid`, `PptxDeckModal`):**
   - Área primária de navegação e operação.
   - Quando uma música é aberta: exibe as estrofes divididas de forma clara (Verso 1, Verso 2, Coro, Ponte, Final), permitindo saltos não lineares solicitados pelo líder de louvor.
   - Quando a Bíblia é aberta: exibe o seletor de livros, capítulos e a grade de versículos com busca rápida via FTS5.
   - Quando um slide PPTX é selecionado: renderiza a grade de miniaturas para troca visual imediata.

4. **Canto Direito Superior / Monitor de Saída 16:9 (`OutputMonitorPanel`):**
   - Miniatura fiel da proporção 16:9 renderizando exatamente o que o público vê no telão HDMI.
   - Indicador de status `NO AR` em destaque discreto Slate/Emerald.
   - Controle de abertura e reposicionamento da janela de projeção secundária (`/projector`).
   - Exibição do modo de tela atual (`ScreenMode`).

5. **Canto Direito Inferior / Acessórios e Fundos:**
   - Gerenciamento de Fundos de Louvor (vídeos em loop MP4/WebM ou imagens de alta resolução).
   - Painel de Mensagens Rápidas de Cabine: avisos discretos para o telão ou palco (ex: placas de carros, recados para os pais no berçário).
   - Disparador de Contagem Regressiva (`COUNTDOWN`) para início do culto.

6. **Janela de Projeção Externa / Telão (`ProjectorView` em `/projector`):**
   - Janela sem bordas (*borderless*) destinada ao projetor ou telão LED secundário.
   - Renderiza fundos em vídeo acelerados por GPU, transições de texto suaves e tipografia litúrgica legível com sombras de contraste.

7. **Monitor de Palco / Púlpito (`StageView` em `/stage`):**
   - Tela voltada exclusivamente para os músicos, cantores e pastor.
   - Apresenta: estrofe atual em fonte grande, prévia da próxima estrofe, relógio em tempo real, cronômetro regressivo da pregação e alertas privados do diretor de culto.

8. **Controle Remoto Mobile (`/remote`):**
   - PWA/Web responsiva para smartphone ou tablet.
   - Emparelhamento seguro via PIN/QR Code (`RemotePairingModal`).
   - Permite que o pregador passe seus próprios slides e versículos do púlpito sem fio.

---

## 3. Mapeamento de Arquivos e Componentes React / Slices

| Região Espacial | Componente React Principal | Caminho Canônico do Arquivo | Fatia / Slice |
|---|---|---|---|
| **Topo** | `OperatorTopBar` | `slices/navigation/ui/components/OperatorTopBar.tsx` | `navigation` |
| **Topo (Busca)** | `SpotlightDialog` | `apps/ui/src/operator/components/SpotlightDialog.tsx` | `operator / core` |
| **Coluna 1 (Roteiro)** | `LiturgyScheduleColumn` | `slices/liturgy/ui/components/LiturgyScheduleColumn.tsx` | `liturgy` |
| **Coluna Central (Músicas)** | `SongStanzaGrid` | `slices/songs/ui/components/SongStanzaGrid.tsx` | `songs` |
| **Coluna Central (Bíblia)** | `BibleVerseGrid` | `slices/bible/ui/components/BibleVerseGrid.tsx` | `bible` |
| **Coluna Central (Slides)** | `PptxDeckModal` | `slices/pptx/ui/components/PptxDeckModal.tsx` | `pptx` |
| **Canto Direito Superior** | `OutputMonitorPanel` | `slices/navigation/ui/components/OutputMonitorPanel.tsx` | `navigation` |
| **Canto Direito Inferior** | `StageControlModal` | `slices/stage/ui/components/StageControlModal.tsx` | `stage` |
| **Janela do Telão** | `ProjectorView` | `apps/ui/src/projector/ProjectorView.tsx` | `apps/ui` |
| **Janela de Palco (Stage)** | `StageView` | `slices/stage/ui/components/StageView.tsx` | `stage` |
| **Console Orquestrador** | `OperatorView` | `apps/ui/src/operator/OperatorView.tsx` | `apps/ui` |

### Modais e Ferramentas Auxiliares:
- `DiagnosticsModal`: `slices/navigation/ui/components/DiagnosticsModal.tsx` (Status do daemon e telas)
- `SongImporterModal`: `slices/songs/ui/components/SongImporterModal.tsx` (Importador Holyrics `.muf` e Vagalume)
- `SongHistoryModal`: `slices/songs/ui/components/SongHistoryModal.tsx` (Histórico de execuções de louvor)
- `BibleDownloadModal`: `slices/bible/ui/components/BibleDownloadModal.tsx` (Downloads de versões das Escrituras)
- `AutomationModal`: `slices/automation/ui/components/AutomationModal.tsx` (Cenas e gatilhos DAG)
- `KeymapModal`: `slices/keymap/ui/components/KeymapModal.tsx` (Atalhos de teclado do operador)
- `RemotePairingModal`: `slices/remote/ui/components/RemotePairingModal.tsx` (Pareamento do controle do pastor)

---

## 4. Dicionário de Linguagem Ubíqua

Termos oficiais que devem ser respeitados rigorosamente em todo o código, documentação e interfaces:

| Termo do Domínio | O que Significa na Cabine | Conceito Técnico no LumenLite |
|---|---|---|
| **Workspace (Área de Preparo)** | Onde o operador busca, inspeciona e seleciona estrofes ou versículos antes de enviar ao público. | Estado local do cliente React (`selectedStanza`, `previewItem`). Não altera o telão HDMI. |
| **Live (No Ar / Ao Vivo)** | O conteúdo que está sendo exibido fisicamente no telão e no retorno de palco no exato instante. | Estado emitido pelo servidor/FSM (`ScreenState.text`, `ScreenState.background`). |
| **Disparo 1-Clique vs 2-Cliques** | - **1-Clique**: No roteiro, seleciona o item para o Workspace (preview). Na grade de estrofes ativas, dispara direto para o Live.<br>- **2-Cliques (ou Enter)**: Força o disparo instantâneo do item do roteiro para o Live. | Eventos de clique `onSelect` (preparar) vs `onTrigger` (despacho de ação FSM). |
| **Fundo de Louvor (Motion Background)** | Vídeo em loop contínuo e suave ou imagem abstrata projetada atrás da letra cantada. | `ScreenState.background` do tipo `video` ou `image`, com `loop: true` e `muted: true`. |
| **Mídia em Tela Cheia (Media Presentation)** | Vídeo com áudio próprio (vídeo institucional, testemunho) ou imagem de aviso que ocupa 100% da tela sem texto sobreposto. | Modo de tela `ScreenMode.MEDIA` onde a reprodução multimídia assume o foco com áudio ativo. |
| **Fundo Padrão (Default Theme Background)** | O fundo padrão configurado para o culto, usado quando uma música não define um fundo individual próprio. | Fallback configurado na FSM e nas preferências do sistema (`app-settings.json`). |
| **Roteiro Litúrgico (Culto)** | A sequência programada de eventos e mídias da reunião religiosa. | Arquivo serializado `culto.json` validado por `liturgy-schema.ts` com tipagem Zod v4. |
| **Pânico (Panic Action)** | Ação de emergência em 1 tecla para limpar o telão instantaneamente (ex: erro de letra, momento íntimo de oração). | Comandos `ActionCommand`: `PANIC_BLANK`, `PANIC_CLEAR`, `PANIC_LOGO`. |

---

## 5. Modos da Máquina de Estados Finita (`ScreenMode`)

O estado da projeção é governado pela enumeração `ScreenMode` em `@lumen/core`:

1. `BLANK` ("Preto"):
   - Apagamento total de vídeo e texto. O projetor recebe uma tela preta absoluta. Utilizado em momentos de silêncio litúrgico, orações de reflexão ou transição entre atos.
2. `CLEAR` ("Limpar"):
   - Mantém o fundo de louvor animado em reprodução suave, mas remove instantaneamente o texto da letra ou versículo.
3. `LOGO` ("Logotipo"):
   - Exibe a marca oficial da comunidade centralizada na tela, útil antes do início e após o término do culto.
4. `TEXT` ("Texto / Projeção"):
   - Modo padrão de exibição litúrgica: texto tipográfico estilizado sobreposto ao fundo de louvor configurado.
5. `MEDIA` ("Mídia em Tela Cheia"):
   - Reprodução de vídeos, avisos ou imagens em tela cheia com prioridade audiovisual completa.
6. `PPTX` ("Apresentação de Slides"):
   - Exibição de lâminas de apresentação originárias de arquivos PowerPoint/PDF.
7. `COUNTDOWN` ("Contagem Regressiva"):
   - Cronômetro visual regressivo animado sobre o fundo anunciando os minutos restantes para o início da celebração.

---

## 6. Protocolo Obrigatório de Auto-Atualização

> **REGRA MANDATÓRIA DE ENGENHARIA:**
> Qualquer agente autônomo ou desenvolvedor que realizar:
> 1. Criação, renomeação ou remoção de componentes de alto nível (telas, modais, painéis de coluna);
> 2. Alteração de rotas (`apps/daemon/src/api/routes/*` ou rotas de UI);
> 3. Modificação nos estados da FSM (`ScreenMode`, `ScreenState`, `ActionCommand`);
> 4. Criação de novas fatias verticais (`slices/*`);
>
> **DEVE OBRIGATORIAMENTE:**
> - Atualizar este arquivo (`.opencode/skills/lumen-domain/SKILL.md`).
> - Atualizar o documento espelho [`docs/DOMAIN_DICTIONARY.md`](file:///home/miguelsb/workspace/holyrics-analysis/docs/DOMAIN_DICTIONARY.md).
> - Registrar a mudança no histórico de alterações do checklist geral (`docs/CHECKLIST_ROADMAP.md`).
