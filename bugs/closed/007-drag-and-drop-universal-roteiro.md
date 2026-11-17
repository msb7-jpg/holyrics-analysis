# BUG-007 — Drag & Drop universal de mídias, músicas e arquivos (do PC e entre abas) para o Roteiro

- **Status:** Closed

- **Severidade:** Alto | **Tipo:** Bug de Interação / DnD | **Fase:** 2
- **Problema:** Não é possível arrastar itens diretamente de outras abas (como Imagens, Vídeos, Áudio, Arquivos ou Letras) nem arrastar múltiplos formatos do computador diretamente para dentro do Roteiro quando outra aba está ativa.
- **Arquivos envolvidos:**
  - [LiturgyScheduleColumn.tsx](../../new-holyrics/slices/liturgy/ui/components/LiturgyScheduleColumn.tsx#L144-L263)
  - [useLiturgyController.ts](../../new-holyrics/slices/liturgy/ui/hooks/useLiturgyController.ts#L53-L129)
  - [MediaGridPanel.tsx](../../new-holyrics/slices/media/ui/components/MediaGridPanel.tsx#L211-L226)
  - [SongSearchList.tsx](../../new-holyrics/slices/songs/ui/components/SongSearchList.tsx#L52-L67)
- **Causa-raiz:** Quando o operador está na aba `Imagens`, `Vídeos`, `Arquivos` ou `Letras`, o container do `Roteiro` não está montado no DOM para receber o `drop`. Além disso, `handleOsFilesDropOnRoteiro` em `LiturgyScheduleColumn.tsx#L144-L179` não trata arquivos de Áudio nem permite soltar diretamente sobre o botão da aba `Roteiro`.
- **Solução planejada:**
  1. Transformar o botão da aba `Roteiro` em **drop zone ativa**: ao arrastar qualquer item interno (`application/lumen-item`) ou arquivo do SO sobre o botão `Roteiro`, destacar o botão (e alternar para a aba `Roteiro` ao segurar) e aceitar o `onDrop` diretamente nele ou na lista do roteiro.
  2. Garantir suporte a drag em todos os itens (`Letras`, `Imagens`, `Vídeos`, `Áudio`, `Arquivos`, `Fundos & Loops` na direita e arquivos do sistema operacional).
