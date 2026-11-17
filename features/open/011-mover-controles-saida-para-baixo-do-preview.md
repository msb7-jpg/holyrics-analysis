# FEAT-011 — Mover Controles de Saída (Blackout, Ocultar Letra, Congelar) para Baixo do Preview

- **Status:** Open
- **Prioridade:** Médio | **Área:** `slices/navigation/ui/components/OperatorTopBar.tsx`, `OutputMonitorPanel.tsx`

## Descrição
1. Remover o `PanicToolbar` (`Blackout`, `Ocultar Letra`, `Congelar`) da `OperatorTopBar` para liberar espaço no cabeçalho superior.
2. Posicionar os 3 botões logo abaixo do canvas de preview 16:9 (`output-monitor-canvas`) no `OutputMonitorPanel`, alinhados à esquerda.
