# Backlog de Melhorias Futuras & TODOs (`LumenLite / new-holyrics`)

Este arquivo centraliza melhorias e otimizações futuras solicitadas durante o desenvolvimento e testes, separadas do escopo imediato de correção de bugs ([BUGS.md](file:///home/miguel/workspace/holyrics-analysis/bugs/BUGS.md)).

---

## Performance & Renderização

- [ ] **Otimização de Miniaturas de Imagens (`MediaGridPanel.tsx` / Backend)**
  - **Contexto:** Mesmo com downscaling via `createImageBitmap` em memória no frontend, acervos com muitas imagens em alta resolução ainda apresentam lentidão inicial e ao alternar abas.
  - **Solução futura:**
    - Criar endpoint dedicado `GET /api/media/thumb/:id` no daemon com cache persistente de thumbnails leves (WebP/JPEG ~320px) em disco.
    - Adicionar virtualização na grade de mídias para renderizar apenas os cards visíveis na viewport.

---

## Sistema de Arquivos & Experiência de Biblioteca (`Imagens`, `Vídeos`, `Áudio`, `Arquivos`)

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

---

## Arquitetura de Projeção, Troca de Conteúdo & Fundo Principal

- [ ] **Separação de Preview Local vs Ao Vivo na Coluna Central (`OperatorView.tsx`)**
  - **Causa-raiz:** Em [OperatorView.tsx](file:///home/miguel/workspace/holyrics-analysis/new-holyrics/apps/ui/src/operator/OperatorView.tsx#L219), a condição `state.mode === 'PPTX'` tem prioridade absoluta sobre `activeBible` e `currentSong`. Como clicar em uma música na aba `Letras` (`handleSelectSong`) atualiza apenas o estado local `selectedSongId` sem mudar `state.mode`, a Coluna Central fica travada nos slides do PDF/PPTX e impede o operador de ver e projetar a música selecionada.
  - **Direção de solução:** Controlar o que aparece na Coluna Central por um estado de **Preview/Seleção ativo do operador** (`SONG` | `BIBLE` | `PPTX`), independente de `state.mode` (o que está no ar), mantendo o indicador `"AO VIVO"` apenas no item que está realmente projetado. 
  - - Mesma coisa acontece com as imagens, se por exemplo eu to c uma letra e mostro uma imagem, ele deve mostrar a imagem, e não assumir q eu quero alterar o fundo do louvor, devemos ter alguma distinção melhor sobre isso

- [ ] **Distinção entre "Fundo Principal" e "Projeção Exclusiva de Mídia/Slide" (`fsm-reducer.ts`, `screen-state.ts`, `stage-router.ts`)**
  - **Causa-raiz:**
    1. Em [fsm-reducer.ts](file:///home/miguel/workspace/holyrics-analysis/new-holyrics/packages/core/src/fsm/fsm-reducer.ts#L151-L158), `SET_BACKGROUND` altera apenas `state.background` e nunca limpa `state.mode` nem `state.foreground`. Assim, ao tentar projetar uma imagem/vídeo quando há um slide PDF/PPTX (`mode === 'PPTX'`) ou texto no ar, o slide/texto anterior permanece na tela.
    2. Não existe um campo separado para **Fundo Principal do Culto** vs **Fundo Temporário/Mídia Avulsa**: quando uma música, versículo ou o cronômetro ([stage-router.ts](file:///home/miguel/workspace/holyrics-analysis/new-holyrics/slices/stage/daemon/routes/stage-router.ts#L97-L115)) é projetado, ele herda o último `state.background` residual em vez de retornar ao Fundo Principal.
  - **Direção de solução:**
    - Definir um **Fundo Principal** configurável no estado/tema do culto, usado automaticamente por **Letras** (salvo se a música tiver `defaultBackground`), **Versículos** e **Cronômetro**.
    - Diferenciar claramente **Aplicar Fundo** (`SET_BACKGROUND` sobre texto) de **Projetar Mídia/Slide em Tela Cheia** (`PRESENT_ITEM` com `mode: 'MEDIA'` ou `'PPTX'`), onde projetar uma imagem, vídeo, slide ou letra sempre limpa/substitui o `foreground` anterior (ex.: tira o slide PPTX do ar ao projetar uma imagem, vídeo ou música).

