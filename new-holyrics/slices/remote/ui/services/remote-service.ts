/**
 * @module @slices/remote/ui/services/remote-service
 * @description Serviço cliente HTTP para pareamento via QR Code e despacho autenticado de comandos móveis.
 */

import { httpClient } from '@ui/core/network/http-client'
import type { ActionCommand, ScreenState } from '@lumen/core'
import type { RemotePairingInfo, RemoteRole, RemoteServerConfig } from '../../contracts/remote-contracts'
import { RemotePairingInfoSchema } from '../../contracts/remote-contracts'

export const remoteService = {
  /**
   * Obtém os endereços LAN e tokens atuais para renderizar os QR Codes de pareamento.
   *
   * @param signal - Sinal de cancelamento opcional.
   * @returns Dados validados `RemotePairingInfo`.
   */
  async getPairingInfo(signal?: AbortSignal): Promise<RemotePairingInfo> {
    return httpClient.get('/api/remote/info', {
      signal,
      schema: RemotePairingInfoSchema,
    })
  },

  /**
   * Rotaciona os tokens de pareamento móvel por segurança.
   *
   * @returns Novas URLs e tokens gerados.
   */
  async rotateTokens(): Promise<RemotePairingInfo> {
    return httpClient.post('/api/remote/rotate', {}, { schema: RemotePairingInfoSchema })
  },

  async verifyToken(
    token: string,
  ): Promise<{ valid: boolean; role: RemoteRole | null; enabled: boolean; reason?: string }> {
    const res = await fetch(`/api/remote/verify?token=${encodeURIComponent(token)}`)
    const json = (await res.json()) as {
      ok: boolean
      data?: { valid: boolean; role: RemoteRole | null; enabled: boolean; reason?: string }
    }
    return (
      json.data ?? {
        valid: false,
        role: null,
        enabled: true,
        reason: 'Token inválido',
      }
    )
  },

  async updateConfig(partial: Partial<RemoteServerConfig>): Promise<RemotePairingInfo> {
    return httpClient.post('/api/remote/config', partial, { schema: RemotePairingInfoSchema })
  },

  /**
   * Despacha um comando validado pelo token RBAC do dispositivo móvel.
   *
   * @param token - Token de autorização (`preacher` ou `operator`).
   * @param command - Comando da FSM a executar.
   * @returns Novo estado da projeção.
   */
  async dispatchRemoteCommand(token: string, command: ActionCommand): Promise<ScreenState> {
    return httpClient.post('/api/remote/dispatch', { token, command })
  },
}
