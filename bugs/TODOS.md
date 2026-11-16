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

- [x] **Separação de Preview Local vs Ao Vivo na Coluna Central (`OperatorView.tsx`, `useOperatorController.ts`)**
  - **Status:** Concluído! O que aparece na Coluna Central agora é controlado por um estado de **Workspace/Inspeção ativo do operador** (`workspaceItem`), totalmente independente de `state.mode`. O operador pode selecionar e inspecionar qualquer música, passagem bíblica ou apresentação sem afetar o que está no telão. O indicador `"AO VIVO NO TELÃO"` só marca o item que realmente está projetado. Clique simples apenas foca/inspeciona no Workspace; duplo clique (ou Enter) projeta no telão.

- [x] **Distinção entre "Fundo de Louvor" e "Projeção Exclusiva de Mídia em Tela Cheia" (`fsm-reducer.ts`, `MediaGridPanel.tsx`, `ProjectorScreen.tsx`)**
  - **Status:** Concluído! `PRESENT_ITEM` com `mode === 'MEDIA'` agora limpa completamente textos e slides (`foreground.content: []`, `slides: []`), atribuindo o `background` à mídia e renderizando em tela cheia sem textos residuais. No `MediaGridPanel`, o duplo clique projeta em tela cheia (`onProjectMedia`) e o menu de contexto oferece ambas as opções: *"Projetar em Tela Cheia"* e *"Definir como Fundo de Louvor"*.


