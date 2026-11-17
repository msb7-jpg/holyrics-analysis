# FEAT-002 — Distinção entre "Fundo de Louvor" e "Mídia em Tela Cheia"

- **Status:** Closed
- **Área:** `packages/core/src/fsm-reducer.ts`, `slices/media`, `slices/navigation`

## Motivação
Permitir tanto o uso de vídeos/imagens como plano de fundo contínuo atrás das letras quanto a projeção limpa de avisos e vídeos institucionais em tela cheia.

## O que foi atingido
- `PRESENT_ITEM` com `mode === "MEDIA"` limpa textos e slides residuais (`foreground.content: []`, `slides: []`).
- No `MediaGridPanel`, o duplo clique projeta em tela cheia e o menu de contexto oferece *Projetar em Tela Cheia* e *Definir como Fundo de Louvor*.
