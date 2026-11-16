# Checklist de Correção e Validação de Bugs (`LumenLite / new-holyrics`)

> **Catálogo Detalhado & Previews:** [BUGS.md](file:///home/miguelsb/workspace/holyrics-analysis/bugs/BUGS.md)
> **Pasta de Imagens:** [bugs/media/](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media)

---

## Organização Documental (Concluído)
- [x] Consolidar todas as capturas de tela em pasta única [bugs/media/](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media) (`01` a `13`).
- [x] Criar catálogo mestre classificado de bugs com previews inline das imagens em [bugs/BUGS.md](file:///home/miguelsb/workspace/holyrics-analysis/bugs/BUGS.md).
- [x] Criar este checklist de execução e validação [bugs/checklist.md](file:///home/miguelsb/workspace/holyrics-analysis/bugs/checklist.md).

---

## Fase 1 — Correções Rápidas de UI, Teclado, Alertas & Cabeçalho
- [ ] **BUG-01 (`SpotlightDialog.tsx`, `useKeybindings.ts`, `useOperatorShortcuts.ts`):** Pressionar `Esc` ou clicar fora do modal fecha a Command Palette (`Ctrl+K`) imediatamente.
- [ ] **BUG-04 (`OperatorTopBar.tsx`, `liturgy-router.ts`):** Menu ao clicar no nome do evento no topo com **Renomear**, **Exportar Roteiro (.json)**, **Importar Roteiro (.json)** e **Novo Culto**. (Ref.: [11-general-event-name.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/11-general-event-name.png))
- [ ] **BUG-05 (`StageControlModal.tsx`, `OutputMonitorPanel.tsx`, `screen-state.ts`):** Cronômetro progressivo (`MM:SS`) contando o tempo decorrido desde a projeção do slide/item atual, removendo o foco forçado em `"Pregação"`. (Ref.: [01-disparar-alerta-overview.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/01-disparar-alerta-overview.png))
- [ ] **BUG-09 (`SongImporterModal.tsx`, `OperatorView.tsx`):** Remover jargões (`SQLite FTS5`, `fflate`, `tags proprietárias`), corrigir banner de erro de verde (`emerald`) para vermelho (`rose` + `AlertCircle`) e invalidar cache ao salvar/importar. (Ref.: [05-importar-musica-nova.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/05-importar-musica-nova.png))
- [ ] **BUG-15 (`StageControlModal.tsx`):** Corrigir layout de `Estilo` (`Clássico`, `Urgente`, `Discreto`) e `Duração (seg)` para eliminar sobreposição. (Ref.: [01-disparar-alerta-overview.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/01-disparar-alerta-overview.png))
- [ ] **BUG-16 (`StageControlModal.tsx`):** Exibir erro de validação inline no formulário ao tentar disparar alerta com mensagem vazia.
- [ ] **BUG-17 (`StageControlModal.tsx`):** Remover todas as menções a `/stage` nos botões e títulos do modal.

---

## Fase 2 — Layout Redimensionável, Unificação de Letras, Preview de Mídias, Áudio & Pastas
- [ ] **BUG-02 (`OperatorView.tsx`, `LiturgyScheduleColumn.tsx`):** Mover `Letras` (busca de hinos e bíblia rápida) para as abas da Coluna Esquerda ao lado de `Roteiro`, `Imagens`, `Vídeos`, `Áudio` e `Arquivos`, removendo a coluna avulsa `Biblioteca`. (Ref.: [10-roteiro-unify-lyrics.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/10-roteiro-unify-lyrics.png))
- [ ] **BUG-03 (`OperatorView.tsx`, `OutputMonitorPanel.tsx`):** Adicionar divisórias redimensionáveis (`SplitPane`) entre colunas e seções, persistindo tamanhos no `localStorage`.
- [ ] **BUG-06 (`MediaGridPanel.tsx`):** Renderizar miniaturas reais (`<img>` e `<video>`) nos cards de mídia da esquerda e da direita e adicionar opção de Preview Ampliado (Lightbox). (Ref.: [09-roteiro-image-preview-not-loading.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/09-roteiro-image-preview-not-loading.png))
- [ ] **BUG-07 (`LiturgyScheduleColumn.tsx`, `useLiturgyController.ts`, `SongSearchList.tsx`):** Permitir arrastar músicas, imagens, vídeos, áudios e arquivos (tanto do PC quanto de qualquer aba) para o botão/aba `Roteiro` com auto-switch e drop direto.
- [ ] **BUG-08 (`LiturgyScheduleColumn.tsx`, `MediaGridPanel.tsx`, `media-repository.ts`, `media-router.ts`):** Criar pastas, renomear, deletar (via botão direito), botão **"Abrir Pasta Local"** para importação em lote no SO e suporte completo à aba `Áudio`. (Ref.: [08-roteiro-card-right-click.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/08-roteiro-card-right-click.png))

---

## Fase 3 — Editor Visual de Músicas & Busca Online Sem API Key
- [ ] **BUG-10 (`SongImporterModal.tsx`, `song-contracts.ts`, `song-repository.ts`, `useOperatorController.ts`):** Editor lado a lado estilo Holyrics original (`MusicView.java`) com preview 16:9 ao vivo dos slides conforme quebra por `Enter` e seleção de **Fundo Associado à Música**. (Ref.: [13-holyrics-legacy-music-preview.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/13-holyrics-legacy-music-preview.png))
- [ ] **BUG-11 (`vagalume-client.ts`, `vagalume-router.ts`, UI de Letras):** Implementar busca online gratuita **100% sem chaves de API** (LRCLIB + Letras.mus.br) substituindo a API desativada do Vagalume (`HTTP 503`), com importação direta para o editor/acervo.

---

## Fase 4 — Apresentações PDF e PowerPoint Fiéis
- [ ] **BUG-12 (`pptx-extractor.ts`, `ProjectorScreen.tsx`, `screen-state.ts`):** Renderizar páginas reais de PDF e slides visuais de PPTX com previews na lista, suprimindo o fundo musical (`BackgroundVideoLoop`) quando `mode === 'PPTX'`. (Ref.: [02-apresentar-pdf-iniciar-apresentacao.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/02-apresentar-pdf-iniciar-apresentacao.png), [12-general-text-at-screen.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/12-general-text-at-screen.png))
- [ ] **BUG-13 (`PptxDeckModal.tsx`, `dag-runner.ts`):** Corrigir reatividade do botão `Iniciar Apresentação` (feedback visual, destaque do slide no ar e controles no palco) e não tentar enviar comando OBS no `dag-runner` quando `obsClient` estiver desconectado. (Ref.: [03-apresentar-pdf-backend-error.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/03-apresentar-pdf-backend-error.png))
- [ ] **BUG-14 (`pptx-extractor.ts`, `PptxDeckModal.tsx`):** Remover fallback padrão `"Documento PDF importado."` nas Notas do Orador e remover menções `/stage` e `/remote/preacher`.

---

## Fase 5 — Controle Remoto Mobile, Autenticação & Rede
- [ ] **BUG-18 (`App.tsx`, `OperatorRemoteView.tsx`, `PreacherRemoteView.tsx`, `RemotePairingModal.tsx`):** Exigir e validar token de autenticação real via `/api/remote/dispatch` + incluir **Preview 16:9 ao vivo** e **Carrossel deslizável de slides** (swipe mobile / setas desktop). (Ref.: [06-remote-control-operator.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/06-remote-control-operator.png))
- [ ] **BUG-19 (`remote-auth.ts`, `remote-router.ts`, `RemotePairingModal.tsx`):** Gerar **QR Code real**, priorizar IP LAN válido (evitando `169.254.*`), e adicionar configurações de **Ligar/Desligar servidor**, **Startup** e **Seletor de IP da interface de rede / `0.0.0.0`**. (Ref.: [07-remote-control-pairing-modal.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/07-remote-control-pairing-modal.png))
- [ ] **BUG-20 (`RemotePairingModal.tsx`):** Remover texto `"Zero App Store"` do título do modal.

---

## Fase 6 — Editor de Automações DAG & Histórico
- [ ] **BUG-21 (`CustomNodes.tsx`, `AutomationCanvas.tsx`, `AutomationModal.tsx`, `dag-runner.ts`):** Menu de contexto (botão direito) para editar/duplicar/deletar nós, Painel Inspector para edição de parâmetros, organização por múltiplas regras/fluxos, feedback visual de teste e aba de **Histórico de Execução**. (Ref.: [04-editor-automacoes-overview.png](file:///home/miguelsb/workspace/holyrics-analysis/bugs/media/04-editor-automacoes-overview.png))

---

## Verificação Final de Regressão
- [ ] `cd new-holyrics && bun test` passando 100%.
- [ ] `cd new-holyrics && bun run typecheck` sem erros.
- [ ] Validação manual em `make dev` de todos os 21 itens acima.
