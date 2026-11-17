# Roadmap Técnico, Melhorias & Backlog (`LumenLite / new-holyrics`)

Este documento centraliza o histórico de evoluções já concluídas (com suas motivações e resultados) e o backlog organizado de padronizações, correções de performance e novas funcionalidades. Para bugs pontuais de fluxo, consulte também [BUGS.md](file:///home/miguel/workspace/holyrics-analysis/bugs/BUGS.md).

---

## 1. Concluído & Motivação (Histórico de Entregas)

### 1.1. Separação de Preview Local vs. Ao Vivo na Coluna Central
- **Arquivos:** `apps/ui/src/operator/OperatorView.tsx`, `apps/ui/src/operator/hooks/useOperatorController.ts`
- **Motivação:** Evitar que clicar em uma música, passagem ou apresentação na coluna esquerda alterasse acidentalmente o que a congregação estava vendo no telão.
- **O que foi atingido:** O conteúdo exibido na Coluna Central passou a ser governado por `workspaceItem` (estado local do operador), totalmente desacoplado de `state.mode`. Clique simples apenas inspeciona no Workspace (Azul Céu); duplo clique ou `Enter` projeta no telão (Esmeralda / `AO VIVO`).

### 1.2. Distinção entre "Fundo de Louvor" e "Mídia em Tela Cheia"
- **Arquivos:** `packages/core/src/fsm-reducer.ts`, `slices/media/ui/components/MediaGridPanel.tsx`, `slices/navigation/ui/components/ProjectorScreen.tsx`
- **Motivação:** Permitir tanto o uso de vídeos/imagens como plano de fundo contínuo atrás das letras quanto a projeção limpa de avisos e vídeos institucionais em tela cheia.
- **O que foi atingido:** `PRESENT_ITEM` com `mode === 'MEDIA'` limpa textos e slides residuais (`foreground.content: []`, `slides: []`). No `MediaGridPanel`, o duplo clique projeta em tela cheia e o menu de contexto oferece ambas as ações (*Projetar em Tela Cheia* e *Definir como Fundo de Louvor*).

### 1.3. UI Refresh — Design System "Slate Pro Studio Refined" (`v1.4.0`)
- **Referência:** [UI_REFRESH_GUIDE.md](file:///home/miguel/workspace/holyrics-analysis/docs/UI_REFRESH_GUIDE.md)
- **Motivação:** Eliminar o excesso de caixas/bordas rígidas (*boxiness*), remover cores decorativas saturadas que competiam com estados operacionais e garantir legibilidade em cabines com baixa luminosidade.
- **O que foi atingido:**
  - Padronização dos primitivos em `apps/ui/src/components/ui/` (`button`, `input`, `textarea`, `badge`, `kbd`) para superfícies `#141820` / `#1F242F` e microbordas `border-white/5`.
  - Migração do fluxo principal (`BibleVerseGrid`, `LiturgyItemCard`, `OperatorView`, `MediaGridPanel`, `PptxFilesPanel`), `SpotlightDialog`, todos os modais secundários e telas remotas (`OperatorRemoteView`, `PreacherRemoteView`) para a hierarquia `#0B0D10` ➔ `#11141A` ➔ `#141820` / `#181C24`.
  - Aplicação de `tabular-nums font-mono` em contadores, atalhos e numeração de estrofes/versículos, além de *segmented controls* para seletores de modo.

---

## 2. Padronização de Componentes & Domínio (A Fazer)

- [x] **Extrair Componente Compartilhado `SlidePreviewCard` e Atualizar Skill de Domínio**
  - **Status:** Concluído! Criado [SlidePreviewCard.tsx](file:///home/miguel/workspace/holyrics-analysis/new-holyrics/slices/navigation/ui/components/SlidePreviewCard.tsx) e adotado em `SongImporterModal.tsx`, `OperatorView.tsx` e `PptxDeckModal.tsx`, além de registrado em [SKILL.md](file:///home/miguel/workspace/holyrics-analysis/.agents/skills/lumen-domain/SKILL.md).
  - **Contexto / Problema:** Atualmente, a pré-visualização de slides em proporção `16:9` está duplicada com estilos e lógicas diferentes em três lugares:
    1. [SongImporterModal.tsx](file:///home/miguel/workspace/holyrics-analysis/new-holyrics/slices/songs/ui/components/SongImporterModal.tsx) (grade de preview no editor de músicas);
    2. [OperatorView.tsx](file:///home/miguel/workspace/holyrics-analysis/new-holyrics/apps/ui/src/operator/OperatorView.tsx) (grade de miniaturas PPTX no Workspace);
    3. [PptxDeckModal.tsx](file:///home/miguel/workspace/holyrics-analysis/new-holyrics/slices/pptx/ui/components/PptxDeckModal.tsx) (inspetor de slides PPTX).
  - **O que precisa ser feito:**
    1. Criar um componente único reutilizável `SlidePreviewCard` (ex.: em `slices/navigation/ui/components/SlidePreviewCard.tsx`) seguindo o padrão Slate Pro Studio (`#141820` inativo, `#18202C` + `sky-500` em foco, `bg-emerald-950/25` + `emerald-500` ao vivo, `border-white/5`, `tabular-nums font-mono` no número do slide).
    2. Substituir as implementações manuais em `SongImporterModal.tsx`, `OperatorView.tsx` e `PptxDeckModal.tsx` pelo novo `SlidePreviewCard`.
    3. Garantir que o `SlidePreviewCard` **nunca** instancie decodificadores `<video>` por card (usar thumbnail/poster estático para fundos em vídeo).
    4. Documentar o novo componente na skill [SKILL.md](file:///home/miguel/workspace/holyrics-analysis/.agents/skills/lumen-domain/SKILL.md) e no espelho [DOMAIN_DICTIONARY.md](file:///home/miguel/workspace/holyrics-analysis/docs/DOMAIN_DICTIONARY.md).

---

## 3. Performance & Renderização de Imagens e Fundos em Loop (Crítico — A Fazer)

### Diagnóstico de Causa-Raiz
Mesmo com poucas imagens ou vídeos, a interface apresenta travamentos severos e alto uso de CPU/RAM pelos seguintes motivos identificados no código:
1. **Decodificação multiplicada no `SongImporterModal.tsx`:** Ao selecionar um fundo para a música, o `.map()` de slides renderiza **N** `<img src="/api/media/stream/...">` em resolução original (4K/Full HD) ou **N** elementos `<video>` simultâneos dentro da grade de preview.
2. **Double-buffering pesado no Preview do Telão (`BackgroundVideoLoop.tsx` + `OutputMonitorPanel.tsx`):**
   - `BackgroundVideoLoop` monta **dois** `<video autoPlay preload="auto">` sobrepostos (Buffer A e Buffer B) e inclui `activeBuffer` nas dependências do `useEffect`, re-anexando listeners e disparando `.play()` a cada ciclo.
   - O `OutputMonitorPanel` renderiza `ProjectorScreen isPreview={true}` dentro do console do operador usando o mesmo `BackgroundVideoLoop`, duplicando decodificadores de vídeo no mesmo processo Chromium.
3. **Geração de miniaturas na main thread (`MediaGridPanel.tsx`):** `MediaThumb` baixa o arquivo bruto completo via `fetch(streamUrl)` e faz downscaling em memória com `createImageBitmap` + `<canvas>`, sem cache persistente em disco e sem virtualização da grade.

### Plano de Correção Recomendado
- [x] **1. Endpoint de Thumbnails no Backend (`GET /api/media/thumb/:id`) + Cache em Disco**
  - **Status:** Concluído em [media-router.ts](file:///home/miguel/workspace/holyrics-analysis/new-holyrics/slices/media/daemon/routes/media-router.ts) (cache em `.lumen-cache/thumbs`, `convert`/`ffmpeg` com deduplicação de jobs e fallback seguro) e adotado em `MediaGridPanel.tsx` e `SlidePreviewCard.tsx` com `loading="lazy"` e `decoding="async"`.
  - Gerar miniaturas leves (WebP/JPEG ~320px–480px) no daemon durante o upload/scan do acervo, salvando em cache local.
  - Para vídeos, extrair um frame de poster (ex.: em `1s` via `ffmpeg` se disponível, ou fallback leve) para exibir nas grades sem abrir decodificadores.
  - Atualizar `MediaThumb` (`MediaGridPanel.tsx`) e `SlidePreviewCard` (`SongImporterModal.tsx`) para consumir `/api/media/thumb/:id` com `loading="lazy"` e `decoding="async"`.
- [x] **2. Refatoração de Performance do `BackgroundVideoLoop.tsx`**
  - **Status:** Concluído em [BackgroundVideoLoop.tsx](file:///home/miguel/workspace/holyrics-analysis/new-holyrics/slices/navigation/ui/components/BackgroundVideoLoop.tsx) e [ProjectorScreen.tsx](file:///home/miguel/workspace/holyrics-analysis/new-holyrics/slices/navigation/ui/components/ProjectorScreen.tsx): modo `isPreview` usa `<video>` único leve, `useEffect` sem dependência de `activeBuffer`, sem dual-`autoPlay` no buffer reserva e pausa automática via `IntersectionObserver` / `visibilitychange`.
  - Quando `ProjectorScreen` estiver em `isPreview={true}` (miniaturas do console do operador e remoto), renderizar apenas um `<video>` simples (ou poster/frame leve) em vez do double-buffer A/B.
  - Remover `activeBuffer` do array de dependências do `useEffect` (usar `useRef` para controlar o buffer ativo sem recriar event listeners).
  - Não usar `autoPlay` simultâneo no buffer inativo; manter o buffer reserva com `preload="metadata"` e iniciar `.play()` apenas nos ~250ms que antecedem o crossfade.
  - Pausar vídeos automaticamente quando o elemento não estiver visível (`IntersectionObserver` / `document.visibilityState === 'hidden'`).
- [ ] **3. Camada de Ativos & Virtualização**
  - Padronizar/recomendar loops de fundo em **H.264 (`.mp4`, YUV 4:2:0) 720p @ 24–30fps** para garantir decodificação por hardware (GPU) sem gargalo de CPU.
  - Adicionar virtualização (`@tanstack/react-virtual`) na grade do `MediaGridPanel.tsx` para acervos maiores e garantir `URL.revokeObjectURL` em qualquer preview temporário de arquivo local.

---

## 4. Sistema de Arquivos & Experiência de Biblioteca (`Imagens`, `Vídeos`, `Áudio`, `Arquivos`)

- [ ] **Seleção em Lote por Arraste (Marquee / Box Selection) e Multi-seleção**
  - Permitir arrastar o cursor na área das abas (`Imagens`, `Vídeos`, `Áudio`, `Arquivos`) para selecionar vários itens/pastas de uma vez (além de `Ctrl+Clique` e `Shift+Clique`).
  - Permitir arrastar todos os itens selecionados em lote para o **Roteiro**, mover para uma pasta ou excluir em lote.

- [ ] **Menu de Contexto Rico no Fundo das Abas e por Item**
  - Ao clicar com o botão direito no espaço vazio de qualquer aba (`Imagens`, `Vídeos`, `Áudio`, `Arquivos`), exibir menu de contexto com:
    - **Nova Pasta (`+ Pasta`)**
    - **Abrir Pasta Local no Explorador do SO**
    - **Importar Arquivos**
    - **Reescanear Diretório**
  - Ao clicar com o botão direito em um arquivo específico, incluir também a opção **Abrir Local do Arquivo no Explorador do SO** (destacando o arquivo no gerenciador de arquivos).

- [ ] **Isolamento de Diretórios por Tipo de Mídia (`images/`, `videos/`, `audios/`, `files/`)**
  - Separar as raízes de armazenamento em disco para que cada aba (`Imagens`, `Vídeos`, `Áudio`, `Arquivos`) tenha seu próprio diretório dedicado, evitando que pastas criadas em uma aba (ex.: `Avisos Novembro` em Imagens) apareçam vazias nas demais abas.

- [ ] **Player Unificado de Áudio & Vídeo + Disparo ao Clicar em Áudio**
  - Corrigir o clique em itens da aba `Áudio` para carregar/reproduzir diretamente no player (ver plano detalhado na Seção 5).
  - Unificar os controles de reprodução tanto de **áudios** quanto de **vídeos** nesse mesmo player.

---

## 5. Otimizações da Coluna Direita (`OutputMonitorPanel`, Player Unificado, Monitores & Tema)

- [ ] **Header de Saída Simplificado + Futura Tela de Configuração de Monitores**
  - Substituir o texto `"Saída: Telão Principal (1080p)"` no header do `OutputMonitorPanel` por um botão/indicador discreto exibindo apenas o **número de monitores** (ex.: `1 Monitor` / `2 Monitores`).
  - Ao clicar, abrir uma futura tela/modal de **Configuração de Monitores & Projetor**, prevendo:
    - Seleção de fonte, tamanho de texto, resolução e atributos específicos por saída/monitor.
    - Ajustes de calibração física para projetor (deslocamento vertical/horizontal para subir/descer/centralizar a imagem na tela do projetor).

- [ ] **Mover `PanicToolbar` (`Blackout`, `Ocultar Letra`, `Congelar`) para baixo do Preview 16:9**
  - Remover o `PanicToolbar` da `OperatorTopBar` para liberar espaço no cabeçalho superior.
  - Posicionar os botões `Blackout`, `Ocultar Letra` e `Congelar` logo abaixo do canvas de preview (`output-monitor-canvas`) no `OutputMonitorPanel`, alinhados à esquerda.

- [ ] **Remover Abas `Áudio Ambiente / Fundos & Loops` e Fixar Player Unificado**
  - Remover o alternador de abas `Áudio Ambiente` / `Fundos & Loops` do `OutputMonitorPanel`.
  - Exibir por padrão o **Player Unificado (Áudio & Vídeo)** na área inferior da coluna direita (ficando o gerenciamento de fundos/loops nas abas `Imagens` e `Vídeos` da coluna esquerda).

- [ ] **Refinamento Visual & Funcional do Player (`AudioPlayerPanel` ➔ Player Unificado A/V)**
  - Remover o quadrado com `<AudioLines />` à esquerda da faixa atual.
  - Remover o botão `+ Áudio` do cabeçalho do player, pois a adição/reprodução passará a ser automática ao clicar em um áudio na coluna esquerda.
  - Implementar visualizador de **waveform / faixas de áudio** estilo **Spotify / WhatsApp**: barras que sobem e descem conforme o volume/amplitude do áudio e vão se **colorindo progressivamente conforme a reprodução avança** (com suporte a seek ao clicar/arrastar).

- [ ] **Botão de `Tema` no Rodapé da Coluna Direita**
  - Adicionar um botão `Tema` na parte inferior da coluna direita para futura configuração do **Tema Global da Projeção** (ex.: imagem/fundo padrão usada em músicas sem fundo definido, leituras bíblicas, etc.).

---

## 6. Refinamentos Minimalistas (Estrofes, `Roteiro` e `Letras`)

- [ ] **Estrofes & Botão `Editar` mais minimalistas (`SongStanzaGrid.tsx`)**
  - Tornar o botão `Editar` da música e a UI das estrofes ainda mais minimalistas.
  - Exibir o destaque de seleção apenas com o fundo cinza meio transparente (`bg-zinc-800/70` ou similar) quando a estrofe estiver selecionada.
  - Remover a exibição da numeração/contagem de estrofes (`00`, `01`, etc.), pois não agrega valor visual/operacional.

- [ ] **Remover botão `Adicionar` da aba `Roteiro` (`LiturgyScheduleColumn.tsx`)**
  - Retirar o botão `Adicionar` do cabeçalho de `Ordem do Culto`, já que ele abre o modal de projeção rápida sem deixar isso explícito e o menu de contexto (botão direito na aba/itens) já cobre essa ação.

- [ ] **Padronizar ações e dimensões da aba `Letras` (`LiturgyScheduleColumn.tsx` / `SongSearchList.tsx`)**
  - Mover `Importar` e `Histórico` para a **mesma linha (row) da barra de busca** de letras, seguindo o mesmo padrão visual das abas `Imagens`, `Vídeos`, `Áudio` e `Arquivos`.
  - Exibir o botão `Histórico` apenas com o **ícone** e padronizar o botão `Importar` com o mesmo estilo dos botões de importação das abas de mídia.
  - Remover o texto auxiliar `"Arraste para o roteiro"` e padronizar espaçamentos/dimensões da aba `Letras` para ficar idêntica às demais abas da coluna esquerda.

---

## 7. Linguagem Ubíqua & Controle Remoto (`Saída`, `PreacherRemoteView`, `OperatorRemoteView`)

- [ ] **Substituir o termo "Pânico" (`Panic`) na Linguagem Ubíqua do Sistema**
  - Renomear referências a `panic` / `PanicToolbar` / "Controles de Pânico" (em código, contratos, UI e documentação do domínio) para um termo que reflita **controle de projeção/saída** (ex.: `OutputControls` / `Controles de Saída` para Blackout, Ocultar Letra e Congelar).

- [ ] **Padronizar `PreacherRemoteView` com o visual do `OperatorRemoteView` + Remover Notas do Orador**
  - Alinhar a tela do pregador (`PreacherRemoteView.tsx`) ao padrão visual do controle remoto do operador (`OperatorRemoteView.tsx`), mantendo foco em:
    - Preview 16:9 limpo e bem acabado do que está sendo projetado.
    - Controles simples de navegação (próximo / anterior) e acesso consistente com a experiência da Bíblia no controle remoto do operador.
  - **Remover Notas do Orador** (UI e campos/estado desnecessários de notas do orador), simplificando a tela do pregador e o `StageControlModal` caso haja acoplamento.

---

## 8. Palco, Cronômetros & Alertas Lower Third (`StageControlModal`)

- [ ] **Separar `Palco`, `Cronômetros` e `Alertas / Lower Third` em Abas ou Subitens de Menu**
  - Dividir o `StageControlModal` em abas dedicadas (ou subitens aninhados no menu `Ferramentas`) para evitar excesso de informação simultânea na mesma tela.

- [ ] **Feedback Visual Claro de Estado Ativo (`Recado para o Pastor` e demais alertas)**
  - Exibir indicador visual inequívoco quando o **Recado para o Pastor** (e os demais alertas/overlays) estiver visível/ativo no palco ou telão.

- [ ] **Cronômetro Regressivo + Bug na opção "Até HH:MM" (ex.: `até 7:30`)**
  - Exibir o cronômetro mostrando claramente o tempo **decrescendo** em tempo real.
  - **Investigar causa raiz e adicionar testes** para o bug em que clicar na opção de contagem até um horário alvo (ex.: `até 7:30`) não está funcionando.
