# BUG-022 — Performance severa em imagens, thumbnails e double-buffer de vídeo preview

- **Status:** Closed
- **Severidade:** Crítico | **Área:** `slices/media`, `slices/navigation`, `slices/songs`

## Descrição / Problema
Mesmo com poucas imagens ou vídeos, a interface apresentava travamentos e alto uso de CPU/RAM devido a:
1. Decodificação multiplicada de imagens 4K/Full HD ou múltiplos `<video>` no `SongImporterModal.tsx`.
2. Double-buffering pesado no preview do telão (`BackgroundVideoLoop.tsx` + `OutputMonitorPanel.tsx`).
3. Geração de miniaturas na main thread em `MediaGridPanel.tsx` baixando o arquivo bruto via `fetch(streamUrl)`.

## Arquivos Envolvidos
- `new-holyrics/slices/media/daemon/routes/media-router.ts`
- `new-holyrics/slices/navigation/ui/components/BackgroundVideoLoop.tsx`
- `new-holyrics/slices/navigation/ui/components/ProjectorScreen.tsx`
- `new-holyrics/slices/navigation/ui/components/SlidePreviewCard.tsx`
- `new-holyrics/slices/media/ui/components/MediaGridPanel.tsx`

## Solução Aplicada
1. Endpoint `GET /api/media/thumb/:id` com cache em disco (`.lumen-cache/thumbs`), deduplicação de jobs e extração de poster para vídeos.
2. Refatoração do `BackgroundVideoLoop.tsx`: modo `isPreview` usa `<video>` único leve, `useRef` para controle de buffer sem recriar listeners e pausa automática via `IntersectionObserver` / `visibilitychange`.
