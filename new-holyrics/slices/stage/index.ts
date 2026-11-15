/**
 * @module @slices/stage
 * @description Fatia do Monitor de Retorno para Púlpito (Stage View), Cronômetros e Alertas Lower Thirds.
 *
 * ## Responsabilidades:
 * - Renderização da rota de alto contraste `/stage` para TVs e tablets no púlpito (RF-02).
 * - Exibição do slide atual, próximo slide em destaque e notas do orador.
 * - Motor de cronômetro de pregação e contagem regressiva pré-culto sincronizados via WebSocket (RF-25).
 * - Fila e exibição de alertas de emergência (pais ao berçário, veículos bloqueando garagem - RF-24).
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos de controle de cronômetro e alertas para o `apps/daemon`.
 * - **Consome:** `ScreenState` do `@lumen/core` e `LiveStageState` via WebSocket.
 * - **Exporta para UI:** Rota web `/stage`, `LowerThirdOverlay` e `StageControlModal`.
 */

export const SLICE_NAME = 'stage'

export type {
  AlertStyle,
  LowerThirdAlert,
  CreateAlertInput,
  StageTimerMode,
  StageTimerState,
  TimerActionInput,
  PastorMessage,
  LiveStageState,
} from './contracts/stage-contracts'

export {
  AlertStyleSchema,
  LowerThirdAlertSchema,
  CreateAlertInputSchema,
  StageTimerModeSchema,
  StageTimerStateSchema,
  TimerActionInputSchema,
  PastorMessageSchema,
  LiveStageStateSchema,
  INITIAL_LIVE_STAGE_STATE,
} from './contracts/stage-contracts'

export {
  StageEngine,
  calculateSecondsUntilTargetTime,
  formatCountdownTime,
} from './daemon/stage-engine'

export { handleStageRoutes } from './daemon/routes/stage-router'
export { stageService } from './ui/services/stage-service'
export { LowerThirdOverlay } from './ui/components/LowerThirdOverlay'
export { StageView } from './ui/components/StageView'
export { StageControlModal } from './ui/components/StageControlModal'
