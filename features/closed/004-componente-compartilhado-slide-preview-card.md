# FEAT-004 — Extrair Componente Compartilhado SlidePreviewCard

- **Status:** Closed
- **Área:** `slices/navigation/ui/components/SlidePreviewCard.tsx`, `.agents/skills/lumen-domain/SKILL.md`

## Motivação
Eliminar duplicação da pré-visualização de slides 16:9 em `SongImporterModal.tsx`, `OperatorView.tsx` e `PptxDeckModal.tsx`, garantindo que nunca instancie `<video>` por card.

## O que foi atingido
- Criado `SlidePreviewCard.tsx` reutilizável consumindo `/api/media/thumb/:id` com `loading="lazy"` e `decoding="async"`, documentado na skill `lumen-domain`.
