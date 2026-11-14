/**
 * @module @slices/remote/daemon/remote-auth
 * @description Gerenciador de descoberta de IP LAN, tokens temporários e autorização RBAC para remotos móveis (RF-26A, RF-26B).
 */

import { networkInterfaces } from 'node:os'
import type { ActionCommand } from '@lumen/core'
import type {
  RemoteAuthorizationResult,
  RemotePairingInfo,
  RemoteRole,
} from '../contracts/remote-contracts'

const PREACHER_ALLOWED_COMMANDS: ReadonlySet<ActionCommand['type']> = new Set([
  'NAV_NEXT',
  'NAV_PREV',
  'NAV_GOTO',
])

/**
 * Detecta o endereço IPv4 principal da máquina na rede local (LAN) para pareamento via QR Code.
 *
 * @returns Endereço IPv4 local (ex: '192.168.1.15') ou 'localhost' como fallback.
 */
export function detectLocalLanIp(): string {
  try {
    const nets = networkInterfaces()
    for (const interfaceName of Object.keys(nets)) {
      const entries = nets[interfaceName]
      if (!entries) continue
      for (const netInfo of entries) {
        if (netInfo.family === 'IPv4' && !netInfo.internal) {
          return netInfo.address
        }
      }
    }
  } catch {
    // Fallback silencioso para localhost
  }
  return 'localhost'
}

/**
 * Gerenciador de autenticação e autorização RBAC para `/remote/preacher` e `/remote/operator`.
 */
export class RemoteAuthManager {
  private preacherToken: string
  private operatorToken: string
  private readonly port: number
  private readonly uiPort: number

  /**
   * Inicializa o gerenciador com portas do daemon e da interface web.
   *
   * @param port - Porta HTTP do daemon (padrão 4040).
   * @param uiPort - Porta HTTP da UI (padrão 3000 em dev ou mesma porta).
   */
  constructor(port = 4040, uiPort = 3000) {
    this.port = port
    this.uiPort = uiPort
    this.preacherToken = this.generateShortToken('prc')
    this.operatorToken = this.generateShortToken('opr')
  }

  /**
   * Rotaciona os tokens de acesso de pregador e operador.
   *
   * @returns Novas informações de pareamento atualizadas.
   */
  rotateTokens(): RemotePairingInfo {
    this.preacherToken = this.generateShortToken('prc')
    this.operatorToken = this.generateShortToken('opr')
    return this.getPairingInfo()
  }

  /**
   * Retorna os metadados de pareamento e URLs completas para geração de QR Code.
   *
   * @returns Objeto `RemotePairingInfo` com IPs, tokens e URLs.
   */
  getPairingInfo(): RemotePairingInfo {
    const lanIp = detectLocalLanIp()
    const baseUiUrl = `http://${lanIp}:${this.uiPort}`
    return {
      lanIp,
      port: this.port,
      preacherToken: this.preacherToken,
      operatorToken: this.operatorToken,
      preacherUrl: `${baseUiUrl}/remote/preacher?token=${this.preacherToken}`,
      operatorUrl: `${baseUiUrl}/remote/operator?token=${this.operatorToken}`,
      stageUrl: `${baseUiUrl}/stage`,
      broadcastUrl: `http://${lanIp}:3002`,
    }
  }

  /**
   * Verifica se um token possui permissão para executar determinado comando na FSM.
   * O papel `preacher` (RF-26A) só pode avançar/recuar slides, impedindo interferência na cabine.
   *
   * @param token - Token enviado pelo dispositivo móvel.
   * @param command - Comando da FSM solicitado.
   * @returns Resultado da autorização contendo o papel identificado.
   */
  authorizeCommand(token: string | null | undefined, command: ActionCommand): RemoteAuthorizationResult {
    if (!token) {
      return {
        authorized: false,
        role: null,
        reason: 'Token de acesso ausente',
      }
    }

    let role: RemoteRole | null = null
    if (token === this.operatorToken) {
      role = 'operator'
    } else if (token === this.preacherToken) {
      role = 'preacher'
    }

    if (!role) {
      return {
        authorized: false,
        role: null,
        reason: 'Token de acesso inválido ou expirado',
      }
    }

    if (role === 'preacher' && !PREACHER_ALLOWED_COMMANDS.has(command.type)) {
      return {
        authorized: false,
        role: 'preacher',
        reason: `Perfil de pregador não possui permissão para o comando ${command.type}`,
      }
    }

    return {
      authorized: true,
      role,
    }
  }

  private generateShortToken(prefix: string): string {
    const randomPart = crypto.randomUUID().replace(/-/g, '').slice(0, 10)
    return `${prefix}_${randomPart}`
  }
}
