# FEAT-005 — Virtualização da Grade de Mídias e Padrão de Ativos H.264

- **Status:** Open
- **Prioridade:** Médio | **Área:** `slices/media/ui/components/MediaGridPanel.tsx`

## Descrição
1. Padronizar/recomendar loops de fundo em **H.264 (`.mp4`, YUV 4:2:0) 720p @ 24–30fps** para decodificação por hardware (GPU) sem gargalo de CPU.
2. Adicionar virtualização (`@tanstack/react-virtual`) na grade do `MediaGridPanel.tsx` para acervos maiores e garantir `URL.revokeObjectURL` em previews temporários.
