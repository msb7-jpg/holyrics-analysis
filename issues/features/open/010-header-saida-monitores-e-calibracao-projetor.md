# FEAT-010 — Header de Saída Simplificado + Tela de Configuração de Monitores e Projetor

- **Status:** Open
- **Prioridade:** Médio | **Área:** `slices/navigation/ui/components/OutputMonitorPanel.tsx`

## Descrição
1. Substituir o texto `"Saída: Telão Principal (1080p)"` no header do `OutputMonitorPanel` por um botão discreto exibindo apenas o **número de monitores** (ex.: `1 Monitor` / `2 Monitores`).
2. Ao clicar, abrir uma tela/modal de **Configuração de Monitores & Projetor**, prevendo:
   - Seleção de fonte, tamanho de texto, resolução e atributos específicos por saída/monitor.
   - Ajustes físicos para projetor (deslocamento vertical/horizontal para subir, descer e centralizar a imagem na tela do projetor).
