# BUG-005 — Substituir "Tempo de Pregação" por Cronômetro Progressivo de Projeção

- **Status:** Closed

- **Severidade:** Melhoria / UX | **Tipo:** Alinhamento com Holyrics Original | **Fase:** 1
- **Problema:** O cronômetro atual é focado em uma contagem regressiva fixa para `"Pregação"`, o que não atende ao fluxo real da cabine. O operador precisa de um cronômetro progressivo automático que exiba o tempo decorrido desde o momento em que um slide ou item começa a ser projetado no telão, assim como funciona no Holyrics original.
- **Arquivos envolvidos:**
  - [StageControlModal.tsx](../../new-holyrics/slices/stage/ui/components/StageControlModal.tsx#L38-L42) e [StageControlModal.tsx](../../new-holyrics/slices/stage/ui/components/StageControlModal.tsx#L198-L280)
  - [OutputMonitorPanel.tsx](../../new-holyrics/slices/navigation/ui/components/OutputMonitorPanel.tsx#L38-L64)
  - [PreacherRemoteView.tsx](../../new-holyrics/slices/remote/ui/components/PreacherRemoteView.tsx#L72-L93)
  - [screen-state.ts](../../new-holyrics/packages/core/src/fsm/screen-state.ts#L60-L68)
  - [fsm-reducer.ts](../../new-holyrics/packages/core/src/fsm/fsm-reducer.ts#L130-L173)
- **Causa-raiz:** `StageControlModal.tsx` fixa o rótulo padrão `"Pregação"` em contagem regressiva manual, e o estado da FSM (`ScreenState`) não registra o timestamp de início de projeção do item/slide atual.
- **Solução planejada:**
  1. Registrar `projectedAt` (início da projeção do item atual / slide) em `ScreenState` ao disparar `PRESENT_ITEM` (zerando em `CLEAR_SCREEN`).
  2. Exibir um **Cronômetro Progressivo (`MM:SS` / `HH:MM:SS`)** no cabeçalho do Monitor de Saída (`OutputMonitorPanel.tsx`) e no Palco, contando o tempo decorrido desde o início da projeção do slide/item atual.
- **Mídia ([01-disparar-alerta-overview.png](../media/01-disparar-alerta-overview.png)):** veja preview completo em [BUG-15](#bug-15--bot%C3%A3o-minimal-sobreposto-pelo-campo-dura%C3%A7%C3%A3o-seg).
