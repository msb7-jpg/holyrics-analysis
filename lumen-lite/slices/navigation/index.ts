/**
 * @module @slices/navigation
 * @description Fatia de Navegação, Controle de Telão HDMI, Transições e Máscaras de Pânico.
 *
 * ## Responsabilidades:
 * - Renderização completa do projetor HDMI na janela dedicada `/projector`.
 * - Auto-fit dinâmico de texto para evitar quebras ou overflow no telão.
 * - Double-buffering de background com seamless video loop livre de flash preto.
 * - Tratamento instantâneo de máscaras de pânico (blackout, clearText, clearBg, freeze).
 * - Monitor e preview na Coluna 4 do console do operador.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos de navegação (NAV_NEXT, NAV_PREV, TOGGLE_PANIC) para a FSM.
 * - **Consome:** ScreenState atualizado via WebSocket (`ws-handler`).
 * - **Exporta para UI:** Telão do projetor (`ProjectorScreen`), barras de pânico e preview de saída.
 */

export const SLICE_NAME = 'navigation'

export type {
  AspectRatio,
  TextAlignment,
  ProjectorDisplayConfig,
} from './contracts/projector-contracts'

export {
  AspectRatioSchema,
  TextAlignmentSchema,
  ProjectorDisplayConfigSchema,
  DEFAULT_PROJECTOR_CONFIG,
} from './contracts/projector-contracts'

export type {
  NavigationStatePayload,
  KeyAction,
} from './contracts/navigation-contracts'

export { sanitizeLine, sanitizeSlideLines } from './daemon/text-sanitizer'
export { useAutoFitText } from './ui/hooks/useAutoFitText'
export { useMultiMonitor } from './ui/hooks/useMultiMonitor'
export type { MonitorDeviceInfo } from './ui/hooks/useMultiMonitor'
export { BackgroundVideoLoop } from './ui/components/BackgroundVideoLoop'
export { ForegroundTextDisplay } from './ui/components/ForegroundTextDisplay'
export { ProjectorScreen } from './ui/components/ProjectorScreen'
export { OutputMonitorPanel } from './ui/components/OutputMonitorPanel'
export { OperatorTopBar } from './ui/components/OperatorTopBar'
export { PanicToolbar } from './ui/components/PanicToolbar'

// Broadcast, Stage & Remote
export type {
  StageMonitorState,
  PreacherRemoteState,
  BroadcastSettings,
} from './contracts/broadcast-contracts'

export {
  StageMonitorStateSchema,
  PreacherRemoteStateSchema,
  BroadcastSettingsSchema,
} from './contracts/broadcast-contracts'

export { DisplayRepository, DEFAULT_BROADCAST_SETTINGS } from './daemon/display-repository'
export { handleDisplayRoutes } from './daemon/routes/display-router'

