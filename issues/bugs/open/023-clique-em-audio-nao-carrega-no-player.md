# BUG-023 — Clicar em um áudio na aba Áudio não carrega/reproduz no player

- **Status:** Open
- **Severidade:** Alto | **Área:** `slices/media`

## Descrição / Problema
Ao clicar em um arquivo de áudio na aba `Áudio` da coluna esquerda (`MediaGridPanel`), a faixa não é carregada nem reproduzida no `AudioPlayerPanel`.

## Arquivos Envolvidos
- `new-holyrics/slices/media/ui/components/MediaGridPanel.tsx`
- `new-holyrics/slices/media/ui/components/AudioPlayerPanel.tsx`

## Plano de Ação
1. Conectar o clique em itens de áudio da coluna esquerda para carregar e iniciar a reprodução diretamente no player unificado.
