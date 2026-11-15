/**
 * @module @slices/stage/contracts/stage-contracts
 * @description Contratos Zod v4 para o Monitor de Retorno de Palco (`/stage`), Alertas Lower Thirds e Cronômetros (RF-02, RF-24, RF-25).
 *
 * ## Responsabilidades:
 * - Modelar alertas sobrepostos de emergência (Lower Thirds) com fila e expiração.
 * - Modelar cronômetros de pregação e contagens regressivas (duração fixa ou horário alvo exato conforme `CountdownUtils.java`).
 * - Modelar recados diretos e discretos para o púlpito do pregador (`PastorMessage`).
 */

import { z } from 'zod'

/**
 * Estilo visual do alerta Lower Third sobreposto no telão e no OBS.
 */
export const AlertStyleSchema = z.enum(['classic', 'urgent', 'minimal'])
export type AlertStyle = z.infer<typeof AlertStyleSchema>

/**
 * Alerta sobreposto de rodapé/topo (Lower Third - RF-24).
 */
export const LowerThirdAlertSchema = z.object({
  id: z.string(),
  label: z.string().default('AVISO'),
  message: z.string().min(1),
  style: AlertStyleSchema.default('classic'),
  position: z.enum(['bottom', 'top']).default('bottom'),
  durationSeconds: z.number().min(3).max(600).default(15),
  createdAt: z.number(),
  expiresAt: z.number(),
})

export type LowerThirdAlert = z.infer<typeof LowerThirdAlertSchema>

/**
 * Payload de criação de um novo alerta Lower Third.
 */
export const CreateAlertInputSchema = z.object({
  label: z.string().optional(),
  message: z.string().min(1),
  style: AlertStyleSchema.optional(),
  position: z.enum(['bottom', 'top']).optional(),
  durationSeconds: z.number().min(3).max(600).optional(),
})

export type CreateAlertInput = z.infer<typeof CreateAlertInputSchema>

/**
 * Modo de operação do cronômetro (baseado em `CountdownSettings.java`).
 */
export const StageTimerModeSchema = z.enum(['DURATION', 'TARGET_TIME'])
export type StageTimerMode = z.infer<typeof StageTimerModeSchema>

/**
 * Estado do cronômetro sincronizado de palco e contagem regressiva (RF-25).
 */
export const StageTimerStateSchema = z.object({
  isRunning: z.boolean(),
  mode: StageTimerModeSchema.default('DURATION'),
  label: z.string().default('Pregação'),
  durationSeconds: z.number().min(0).default(1800),
  targetTime: z.string().optional(), // Formato "HH:mm"
  startedAt: z.number().nullable().default(null),
  endTime: z.number().nullable().default(null),
  pausedRemainingSeconds: z.number().nullable().default(null),
  textBefore: z.string().default('Início em'),
  textAfter: z.string().default('Bem-vindos ao Culto'),
  zeroFill: z.boolean().default(true),
})

export type StageTimerState = z.infer<typeof StageTimerStateSchema>

/**
 * Comando de controle do cronômetro de palco.
 */
export const TimerActionInputSchema = z.object({
  action: z.enum(['start', 'pause', 'reset']),
  mode: StageTimerModeSchema.optional(),
  label: z.string().optional(),
  durationSeconds: z.number().min(1).max(86400).optional(),
  targetTime: z.string().regex(/^\d{2}:\d{2}$/).optional(),
  textBefore: z.string().optional(),
  textAfter: z.string().optional(),
  projectOnScreen: z.boolean().optional(),
})

export type TimerActionInput = z.infer<typeof TimerActionInputSchema>

/**
 * Recado interno exclusivo para o monitor de palco (`/stage`) e púlpito (`/remote/preacher`).
 */
export const PastorMessageSchema = z.object({
  id: z.string(),
  message: z.string(),
  level: z.enum(['info', 'warning', 'urgent']).default('info'),
  createdAt: z.number(),
})

export type PastorMessage = z.infer<typeof PastorMessageSchema>

/**
 * Estado consolidado da fatia Stage transmitido via WebSocket e REST.
 */
export const LiveStageStateSchema = z.object({
  activeAlert: LowerThirdAlertSchema.nullable(),
  alertQueue: z.array(LowerThirdAlertSchema),
  timer: StageTimerStateSchema,
  pastorMessage: PastorMessageSchema.nullable(),
  uppercaseMode: z.boolean().default(false),
})

export type LiveStageState = z.infer<typeof LiveStageStateSchema>

export const INITIAL_LIVE_STAGE_STATE: LiveStageState = {
  activeAlert: null,
  alertQueue: [],
  timer: {
    isRunning: false,
    mode: 'DURATION',
    label: 'Pregação',
    durationSeconds: 1800,
    startedAt: null,
    endTime: null,
    pausedRemainingSeconds: 1800,
    textBefore: 'Início em',
    textAfter: 'Bem-vindos ao Culto',
    zeroFill: true,
  },
  pastorMessage: null,
  uppercaseMode: false,
}
