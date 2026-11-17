/**
 * @module @slices/remote/ui/components/RemotePairingModal
 * @description Modal de Controle Remoto Mobile com QR Code real (ISO/IEC 18004), seleção de IP/interface,
 * controle de ligar/desligar servidor e inicialização automática.
 */

import { useState, useEffect, useMemo } from 'react'
import { encode } from 'uqr'
import { X, QrCode, Smartphone, RefreshCw, ExternalLink, Copy, Check, Power, Wifi } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import type { RemotePairingInfo, RemoteServerConfig } from '../../contracts/remote-contracts'
import { remoteService } from '../services/remote-service'

export interface RemotePairingModalProps {
  isOpen: boolean
  onClose: () => void
}

/**
 * Modal de pareamento e configuração do Controle Remoto Mobile.
 *
 * @param props - Propriedades do modal.
 * @returns Elemento JSX do modal ou null.
 */
export function RemotePairingModal({ isOpen, onClose }: RemotePairingModalProps) {
  const [pairingInfo, setPairingInfo] = useState<RemotePairingInfo | null>(null)
  const [selectedRole, setSelectedRole] = useState<'preacher' | 'operator'>('preacher')
  const [copied, setCopied] = useState(false)
  const [isUpdatingConfig, setIsUpdatingConfig] = useState(false)

  useEffect(() => {
    if (!isOpen) return
    remoteService
      .getPairingInfo()
      .then(setPairingInfo)
      .catch(() => {
        setPairingInfo({
          lanIp: window.location.hostname || 'localhost',
          port: 4040,
          preacherToken: 'prc_local',
          operatorToken: 'opr_local',
          preacherUrl: `${window.location.origin}/remote/preacher?token=prc_local`,
          operatorUrl: `${window.location.origin}/remote/operator?token=opr_local`,
          stageUrl: `${window.location.origin}/stage`,
          broadcastUrl: 'http://localhost:3002',
          availableHosts: [
            { label: 'Automático (LAN)', address: 'auto' },
            { label: 'Todas as Interfaces (0.0.0.0)', address: '0.0.0.0' },
          ],
          config: { enabled: true, autoStart: true, selectedHost: 'auto' },
        })
      })
  }, [isOpen])

  const activeUrl = pairingInfo
    ? selectedRole === 'preacher'
      ? pairingInfo.preacherUrl
      : pairingInfo.operatorUrl
    : ''

  const qrMatrix = useMemo(() => {
    if (!activeUrl) return [] as boolean[][]
    return encode(activeUrl).data
  }, [activeUrl])

  if (!isOpen || !pairingInfo) return null

  const serverConfig: RemoteServerConfig = pairingInfo.config ?? {
    enabled: true,
    autoStart: true,
    selectedHost: 'auto',
  }

  const handleCopy = async () => {
    await navigator.clipboard.writeText(activeUrl)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const handleRotateTokens = async () => {
    const updated = await remoteService.rotateTokens()
    setPairingInfo(updated)
  }

  const handleUpdateConfig = async (partial: Partial<RemoteServerConfig>) => {
    setIsUpdatingConfig(true)
    try {
      const updated = await remoteService.updateConfig(partial)
      setPairingInfo(updated)
    } finally {
      setIsUpdatingConfig(false)
    }
  }

  const qrSize = qrMatrix.length || 21

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-xs">
      <div className="flex max-h-[92vh] w-full max-w-lg flex-col rounded-xl border border-zinc-800 bg-zinc-950 text-zinc-100 shadow-2xl overflow-hidden">
        <div className="flex items-center justify-between border-b border-zinc-800 bg-zinc-900 px-5 py-3.5">
          <div className="flex items-center gap-2">
            <QrCode className="h-4 w-4 text-zinc-400" />
            <h2 className="text-sm font-semibold text-white">Controle Remoto Mobile</h2>
          </div>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex flex-col items-center gap-4 p-5 overflow-y-auto">
          {/* Configurações do Servidor Remoto */}
          <div className="w-full rounded-xl border border-zinc-800 bg-zinc-900 p-3.5 text-xs flex flex-col gap-2.5">
            <div className="flex items-center justify-between">
              <div className="flex flex-col">
                <span className="font-medium text-zinc-200">Servidor Remoto</span>
                <span className="text-[10px] text-zinc-400">
                  {serverConfig.enabled ? 'Ativo na rede local' : 'Desativado'}
                </span>
              </div>
              <button
                type="button"
                disabled={isUpdatingConfig}
                onClick={() => handleUpdateConfig({ enabled: !serverConfig.enabled })}
                title={serverConfig.enabled ? 'Desligar Servidor Remoto' : 'Ligar Servidor Remoto'}
                className={`flex h-10 w-10 items-center justify-center rounded-full border transition-all ${
                  serverConfig.enabled
                    ? 'border-emerald-400/70 bg-emerald-500/20 text-emerald-300 shadow-[0_0_16px_rgba(16,185,129,0.6)] hover:bg-emerald-500/30'
                    : 'border-zinc-800 bg-zinc-800 text-zinc-500 hover:text-zinc-300 hover:bg-zinc-800'
                }`}
              >
                <Power className="h-5 w-5" />
              </button>
            </div>

            <div className="flex items-center justify-between pt-2 border-t border-zinc-800">
              <span className="text-zinc-300">Iniciar junto com o aplicativo (Startup)</span>
              <button
                type="button"
                role="switch"
                aria-checked={serverConfig.autoStart}
                disabled={isUpdatingConfig}
                onClick={() => handleUpdateConfig({ autoStart: !serverConfig.autoStart })}
                className={`relative inline-flex h-5 w-10 shrink-0 cursor-pointer items-center rounded-full border transition-colors ${
                  serverConfig.autoStart
                    ? 'border-sky-400/60 bg-zinc-800'
                    : 'border-zinc-800 bg-zinc-950'
                }`}
              >
                <span
                  className={`inline-block h-3.5 w-3.5 rounded-full transition-transform ${
                    serverConfig.autoStart
                      ? 'tranzinc-x-5 bg-sky-400 shadow-[0_0_8px_rgba(56,189,248,0.6)]'
                      : 'tranzinc-x-1 bg-zinc-400'
                  }`}
                />
              </button>
            </div>

            <div className="flex items-center justify-between gap-2 pt-1 border-t border-zinc-800">
              <span className="flex items-center gap-1.5 text-zinc-400 shrink-0">
                <Wifi className="h-3.5 w-3.5 text-zinc-400" />
                <span>Interface / IP:</span>
              </span>
              <select
                value={serverConfig.selectedHost}
                disabled={isUpdatingConfig}
                onChange={(event) => handleUpdateConfig({ selectedHost: event.target.value })}
                className="flex-1 max-w-[260px] rounded-md border border-zinc-800 bg-zinc-950 px-2.5 py-1 text-xs text-zinc-100 focus:outline-none focus:ring-1 focus:ring-zinc-600"
              >
                {(pairingInfo.availableHosts ?? [{ label: 'Automático (LAN)', address: 'auto' }]).map(
                  (host) => (
                    <option key={host.address} value={host.address}>
                      {host.label}
                    </option>
                  ),
                )}
              </select>
            </div>
          </div>

          {/* Seletor de Perfil RBAC */}
          <div className="grid w-full grid-cols-2 gap-1 rounded-lg bg-zinc-950/80 p-0.5 border border-zinc-800">
            <button
              type="button"
              onClick={() => setSelectedRole('preacher')}
              className={`flex items-center justify-center gap-1.5 rounded-md px-3 py-1.5 text-xs font-medium transition ${
                selectedRole === 'preacher'
                  ? 'bg-zinc-800 text-zinc-100 ring-1 ring-zinc-600'
                  : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-800'
              }`}
            >
              <Smartphone className="h-3.5 w-3.5" />
              <span>Púlpito do Pastor</span>
            </button>
            <button
              type="button"
              onClick={() => setSelectedRole('operator')}
              className={`flex items-center justify-center gap-1.5 rounded-md px-3 py-1.5 text-xs font-medium transition ${
                selectedRole === 'operator'
                  ? 'bg-zinc-800 text-zinc-100 ring-1 ring-zinc-600'
                  : 'text-zinc-400 hover:text-zinc-200 hover:bg-zinc-800'
              }`}
            >
              <QrCode className="h-3.5 w-3.5" />
              <span>Operador Volante</span>
            </button>
          </div>

          {/* QR Code SVG Real (ISO/IEC 18004) */}
          <div className={`rounded-2xl border border-white/15 bg-white p-4 shadow-xl ${!serverConfig.enabled ? 'opacity-40 grayscale' : ''}`}>
            <svg
              width="184"
              height="184"
              viewBox={`0 0 ${qrSize} ${qrSize}`}
              shapeRendering="crispEdges"
            >
              {qrMatrix.map((row, rowIdx) =>
                row.map((cell, colIdx) =>
                  cell ? (
                    <rect
                      key={`${rowIdx}-${colIdx}`}
                      x={colIdx}
                      y={rowIdx}
                      width={1}
                      height={1}
                      fill="#0F172A"
                    />
                  ) : null,
                ),
              )}
            </svg>
          </div>

          <div className="flex flex-col items-center gap-1 text-center">
            <p className="mt-1 font-mono text-xs text-zinc-300 break-all">{activeUrl}</p>
          </div>

          <div className="flex w-full flex-wrap items-center justify-center gap-2 pt-1">
            <Button variant="secondary" onClick={handleCopy}>
              {copied ? <Check className="h-3.5 w-3.5 text-emerald-400" /> : <Copy className="h-3.5 w-3.5" />}
              <span>{copied ? 'Link Copiado' : 'Copiar URL'}</span>
            </Button>

            <Button
              variant="outline"
              disabled={!serverConfig.enabled}
              onClick={() => window.open(activeUrl, '_blank', 'noopener,noreferrer')}
            >
              <ExternalLink className="h-3.5 w-3.5 text-zinc-400" />
              <span>Abrir em Nova Aba</span>
            </Button>

            <Button variant="ghost" onClick={handleRotateTokens} title="Gerar novo token de segurança">
              <RefreshCw className="h-3.5 w-3.5" />
              <span>Renovar Token</span>
            </Button>
          </div>
        </div>
      </div>
    </div>
  )
}
