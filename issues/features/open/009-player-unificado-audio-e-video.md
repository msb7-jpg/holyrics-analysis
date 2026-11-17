# FEAT-009 — Player Unificado de Áudio & Vídeo com Waveform Spotify/WhatsApp

- **Status:** Open
- **Prioridade:** Alto | **Área:** `slices/media/ui/components/AudioPlayerPanel.tsx`, `slices/navigation`

## Descrição
1. Unificar os controles de reprodução tanto de **áudios** quanto de **vídeos** neste player.
2. Remover o quadrado com `<AudioLines />` à esquerda da faixa atual.
3. Remover o botão `+ Áudio` do cabeçalho do player (carregamento automático ao clicar em um áudio na coluna esquerda — ver `BUG-023`).
4. Implementar visualizador de **waveform / faixas de áudio** estilo **Spotify / WhatsApp**: barras que sobem e descem conforme o volume/amplitude do áudio e vão se **colorindo progressivamente conforme a reprodução avança** (com suporte a seek ao clicar/arrastar).
