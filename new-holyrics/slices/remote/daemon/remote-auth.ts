/**
 * @module @slices/remote/daemon/remote-auth
 * @description Gerenciador de descoberta de IP LAN, tokens temporários, configurações do servidor remoto e autorização RBAC (RF-26A, RF-26B).
 */

import { networkInterfaces } from 'node:os'
import { existsSync, mkdirSync, readFileSync, writeFileSync } from 'node:fs'
import { dirname, resolve } from 'node:path'
import type { ActionCommand } from '@lumen/core'
import {
  RemoteServerConfigSchema,
  type RemoteAuthorizationResult,
  type RemotePairingInfo,
  type RemoteRole,
  type RemoteServerConfig,
} from '../contracts/remote-contracts'

const PREACHER_ALLOWED_COMMANDS: ReadonlySet<ActionCommand['type']> = new Set([
  'NAV_NEXT',
  'NAV_PREV',
  'NAV_GOTO',
])

function isPrivateLanIpv4(address: string): boolean {
  if (address.startsWith('192.168.') || address.startsWith('10.')) return true
  const match = address.match(/^172\.(\d+)\./)
  if (match) {
    const secondOctet = Number(match[1])
    return secondOctet >= 16 && secondOctet <= 31
  }
  return false
}

/**
 * Lista todas as interfaces IPv4 não-internas da máquina, priorizando IPs LAN válidos e evitando APIPA (`169.254.*`).
 */
export function listAvailableNetworkHosts(): Array<{ label: string; address: string }> {
  const hosts: Array<{ label: string; address: string }> = []
  try {
    const nets = networkInterfaces()
    for (const interfaceName of Object.keys(nets)) {
      const entries = nets[interfaceName]
      if (!entries) continue
      for (const netInfo of entries) {
        if (netInfo.family === 'IPv4' && !netInfo.internal) {
          hosts.push({
            label: `${interfaceName} (${netInfo.address})`,
            address: netInfo.address,
          })
        }
      }
    }
  } catch {
    // Fallback silencioso
  }

  hosts.sort((a, b) => {
    const aApipa = a.address.startsWith('169.254.')
    const bApipa = b.address.startsWith('169.254.')
    if (aApipa !== bApipa) return aApipa ? 1 : -1
    const aLan = isPrivateLanIpv4(a.address)
    const bLan = isPrivateLanIpv4(b.address)
    if (aLan !== bLan) return aLan ? -1 : 1
    return 0
  })

  return [
    { label: 'Automático (LAN)', address: 'auto' },
    ...hosts,
    { label: 'Todas as Interfaces (0.0.0.0)', address: '0.0.0.0' },
  ]
}

/**
 * Detecta o endereço IPv4 principal da máquina na rede local (LAN) evitando faixas APIPA (`169.254.x.x`).
 *
 * @returns Endereço IPv4 local (ex: '192.168.1.15') ou 'localhost' como fallback.
 */
export function detectLocalLanIp(): string {
  const hosts = listAvailableNetworkHosts().filter(
    (h) => h.address !== 'auto' && h.address !== '0.0.0.0',
  )
  const nonApipa = hosts.find((h) => !h.address.startsWith('169.254.'))
  if (nonApipa) return nonApipa.address
  if (hosts[0]) return hosts[0].address
  return 'localhost'
}

/**
 * Gerenciador de autenticação, configuração de rede e autorização RBAC para `/remote/preacher` e `/remote/operator`.
 */
export class RemoteAuthManager {
  private preacherToken: string
  private operatorToken: string
  private readonly port: number
  private readonly uiPort: number
  private readonly configPath: string
  private config: RemoteServerConfig

  constructor(port = 4040, uiPort = 3000, customConfigPath?: string) {
    this.port = port
    this.uiPort = uiPort
    this.configPath = customConfigPath || resolve(process.cwd(), 'data/remote-config.json')
    this.preacherToken = this.generateShortToken('prc')
    this.operatorToken = this.generateShortToken('opr')
    this.config = this.loadConfig()
  }

  private loadConfig(): RemoteServerConfig {
    try {
      if (existsSync(this.configPath)) {
        const raw = JSON.parse(readFileSync(this.configPath, 'utf-8'))
        const parsed = RemoteServerConfigSchema.safeParse(raw)
        if (parsed.success) {
          return {
            ...parsed.data,
            enabled: parsed.data.autoStart ? parsed.data.enabled : false,
          }
        }
      }
    } catch {
      // Fallback para padrão
    }
    return { enabled: true, autoStart: true, selectedHost: 'auto' }
  }

  private saveConfig(next: RemoteServerConfig): void {
    try {
      const dir = dirname(this.configPath)
      if (!existsSync(dir)) {
        mkdirSync(dir, { recursive: true })
      }
      writeFileSync(this.configPath, JSON.stringify(next, null, 2), 'utf-8')
    } catch {
      // Ignora erro de gravação em ambientes read-only
    }
  }

  getConfig(): RemoteServerConfig {
    return { ...this.config }
  }

  updateConfig(partial: Partial<RemoteServerConfig>): RemotePairingInfo {
    this.config = RemoteServerConfigSchema.parse({
      ...this.config,
      ...partial,
    })
    this.saveConfig(this.config)
    return this.getPairingInfo()
  }

  /**
   * Rotaciona os tokens de acesso de pregador e operador.
   */
  rotateTokens(): RemotePairingInfo {
    this.preacherToken = this.generateShortToken('prc')
    this.operatorToken = this.generateShortToken('opr')
    return this.getPairingInfo()
  }

  /**
   * Retorna os metadados de pareamento, interfaces disponíveis e URLs completas.
   */
  getPairingInfo(): RemotePairingInfo {
    const autoIp = detectLocalLanIp()
    const lanIp =
      this.config.selectedHost &&
      this.config.selectedHost !== 'auto' &&
      this.config.selectedHost !== '0.0.0.0'
        ? this.config.selectedHost
        : autoIp
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
      availableHosts: listAvailableNetworkHosts(),
      config: this.getConfig(),
    }
  }

  /**
   * Verifica se um token é válido e qual papel ele representa.
   */
  verifyToken(token: string | null | undefined): {
    valid: boolean
    role: RemoteRole | null
    enabled: boolean
    reason?: string
  } {
    if (!this.config.enabled) {
      return {
        valid: false,
        role: null,
        enabled: false,
        reason: 'Servidor de controle remoto desativado na cabine',
      }
    }
    if (!token) {
      return {
        valid: false,
        role: null,
        enabled: true,
        reason: 'Token de acesso ausente',
      }
    }
    if (token === this.operatorToken) {
      return { valid: true, role: 'operator', enabled: true }
    }
    if (token === this.preacherToken) {
      return { valid: true, role: 'preacher', enabled: true }
    }
    return {
      valid: false,
      role: null,
      enabled: true,
      reason: 'Token de acesso inválido ou expirado',
    }
  }

  /**
   * Verifica se um token possui permissão para executar determinado comando na FSM.
   */
  authorizeCommand(token: string | null | undefined, command: ActionCommand): RemoteAuthorizationResult {
    const verification = this.verifyToken(token)
    if (!verification.valid || !verification.role) {
      return {
        authorized: false,
        role: null,
        reason: verification.reason,
      }
    }

    if (verification.role === 'preacher' && !PREACHER_ALLOWED_COMMANDS.has(command.type)) {
      return {
        authorized: false,
        role: 'preacher',
        reason: `Comando "${command.type}" não permitido para o papel pregador`,
      }
    }

    return {
      authorized: true,
      role: verification.role,
    }
  }

  private generateShortToken(prefix: string): string {
    const randomPart = Math.random().toString(36).substring(2, 8)
    return `${prefix}_${randomPart}`
  }
}
