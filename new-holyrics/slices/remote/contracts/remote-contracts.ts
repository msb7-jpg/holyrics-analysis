/**
 * @module @slices/remote/contracts/remote-contracts
 * @description Contratos Zod v4 para Controle Remoto Web de Pregador (`/remote/preacher`) e Operador (`/remote/operator`) (RF-26A, RF-26B, RF-26C).
 */

import { z } from 'zod'

/**
 * Papel de acesso para controle remoto na rede local (RBAC leve).
 */
export const RemoteRoleSchema = z.enum(['preacher', 'operator'])
export type RemoteRole = z.infer<typeof RemoteRoleSchema>

/**
 * Configurações persistentes do servidor de controle remoto.
 */
export const RemoteServerConfigSchema = z.object({
  enabled: z.boolean().default(true),
  autoStart: z.boolean().default(true),
  selectedHost: z.string().default('auto'),
})
export type RemoteServerConfig = z.infer<typeof RemoteServerConfigSchema>

/**
 * Dados de pareamento e endereços LAN para geração de QR Code na cabine.
 */
export const RemotePairingInfoSchema = z.object({
  lanIp: z.string(),
  port: z.number(),
  preacherToken: z.string(),
  operatorToken: z.string(),
  preacherUrl: z.string(),
  operatorUrl: z.string(),
  stageUrl: z.string(),
  broadcastUrl: z.string(),
  availableHosts: z
    .array(
      z.object({
        label: z.string(),
        address: z.string(),
      }),
    )
    .optional(),
  config: RemoteServerConfigSchema.optional(),
})

export type RemotePairingInfo = z.infer<typeof RemotePairingInfoSchema>

/**
 * Resultado de validação de comando remoto.
 */
export interface RemoteAuthorizationResult {
  authorized: boolean
  role: RemoteRole | null
  reason?: string
}
