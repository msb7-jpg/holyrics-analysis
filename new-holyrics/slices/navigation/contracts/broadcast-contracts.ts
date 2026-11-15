/**
 * @module @slices/navigation/contracts/broadcast-contracts
 * @description Esquemas Zod v4 e tipos para os renderizadores de Broadcast (OBS), Stage Monitor e Preacher Remote (RF-23, RF-24, RF-25, RF-26).
 *
 * ## Responsabilidades:
 * - Tipar o estado completo do monitor de palco (`/stage`), incluindo slides, relógio e cronômetros.
 * - Validar as configurações do overlay de broadcast (`/broadcast`), como estilos de terços inferiores.
 * - Estruturar os dados para o púlpito do pregador (`/remote/preacher`), como notas e timers.
 *
 * ## Comunicação e Fronteiras:
 * - Consumido pelo WebSocket e roteadores que servem as interfaces de `/stage`, `/broadcast` e `/remote`.
 */

import { z } from 'zod'
import { ForegroundLayerSchema } from '@lumen/core'

/**
 * Define o estado completo do telão de retorno de palco (`/stage`).
 */
export const StageMonitorStateSchema = z.object({
  currentItemId: z.string().nullable(),
  currentSlide: ForegroundLayerSchema.nullable(),
  nextSlidePreview: z
    .object({
      title: z.string(),
      content: z.array(z.string()),
    })
    .nullable(),
  clock: z.string().regex(/^\d{2}:\d{2}$/), // "HH:mm"
  timer: z
    .object({
      label: z.string(),
      endTime: z.number().positive(), // Unix timestamp
      isRunning: z.boolean(),
    })
    .nullable(),
  pastorAlert: z
    .object({
      message: z.string(),
      level: z.enum(['info', 'warning', 'urgent']).default('info'),
    })
    .nullable(),
})

export type StageMonitorState = z.infer<typeof StageMonitorStateSchema>

/**
 * Define o estado para o púlpito do pregador (`/remote/preacher`).
 */
export const PreacherRemoteStateSchema = z.object({
  notes: z.string().default(''),
  timer: StageMonitorStateSchema.shape.timer,
  clock: StageMonitorStateSchema.shape.clock,
})

export type PreacherRemoteState = z.infer<typeof PreacherRemoteStateSchema>

/**
 * Configurações de aparência para o overlay de broadcast do OBS.
 */
export const BroadcastSettingsSchema = z.object({
  lowerThirdStyle: z.enum(['classic', 'minimal', 'banner']).default('classic'),
  textAlignment: z.enum(['left', 'center', 'right']).default('center'),
  fontFamily: z.string().default('Inter'),
  fontSize: z.number().min(12).max(120).default(48),
  backgroundOpacity: z.number().min(0).max(1).default(0.7),
})

export type BroadcastSettings = z.infer<typeof BroadcastSettingsSchema>
