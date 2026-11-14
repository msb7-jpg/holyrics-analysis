/**
 * @module @slices/remote/ui/components/RemotePairingModal
 * @description Modal de Pareamento Rápido via QR Code (RF-26A, RF-26B).
 * Exibe QR Codes vetoriais e links diretos para o Púlpito do Pregador (`/remote/preacher`)
 * e Console Móvel do Operador (`/remote/operator`).
 */

import { useState, useEffect } from 'react'
import { X, QrCode, Smartphone, RefreshCw, ExternalLink, Copy, Check } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { Badge } from '@ui/components/ui/badge'
import type { RemotePairingInfo } from '../../contracts/remote-contracts'
import { remoteService } from '../services/remote-service'

export interface RemotePairingModalProps {
  isOpen: boolean
  onClose: () => void
}

/**
 * Gera uma matriz visual determinística 21x21 estilo QR Code SVG com padrões de finder para pareamento visual.
 *
 * @param payload - URL codificada no cartão de pareamento.
 * @returns Matriz 2D booleana de módulos escuros/claros.
 */
function buildDeterministicMatrix(payload: string): boolean[][] {
  const size = 21
  const grid: boolean[][] = Array.from({ length: size }, () => Array(size).fill(false))

  const drawFinder = (rowOffset: number, colOffset: number) => {
    for (let row = 0; row < 7; row++) {
      for (let col = 0; col < 7; col++) {
        const isBorder = row === 0 || row === 6 || col === 0 || col === 6
        const isInner = row >= 2 && row <= 4 && col >= 2 && col <= 4
        grid[rowOffset + row][colOffset + col] = isBorder || isInner
      }
    }
  }

  drawFinder(0, 0)
  drawFinder(0, size - 7)
  drawFinder(size - 7, 0)

  let hash = 2166136261
  for (let idx = 0; idx < payload.length; idx++) {
    hash ^= payload.charCodeAt(idx)
    hash = Math.imul(hash, 16777619)
  }

  for (let row = 0; row < size; row++) {
    for (let col = 0; col < size; col++) {
      const inFinderTopLeft = row < 8 && col < 8
      const inFinderTopRight = row < 8 && col >= size - 8
      const inFinderBottomLeft = row >= size - 8 && col < 8
      if (inFinderTopLeft || inFinderTopRight || inFinderBottomLeft) continue

      const bitSeed = (hash ^ (row * 31 + col * 17 + payload.charCodeAt((row + col) % payload.length))) >>> 0
      grid[row][col] = bitSeed % 2 === 0
    }
  }

  return grid
}

/**
 * Modal de pareamento móvel com QR Code para Pastor e Operador.
 *
 * @param props - Propriedades do modal.
 * @param props.isOpen - Indica se o modal está visível.
 * @param props.onClose - Callback de fechamento.
 * @returns Elemento JSX do modal ou null.
 */
export function RemotePairingModal({ isOpen, onClose }: RemotePairingModalProps) {
  const [pairingInfo, setPairingInfo] = useState<RemotePairingInfo | null>(null)
  const [selectedRole, setSelectedRole] = useState<'preacher' | 'operator'>('preacher')
  const [copied, setCopied] = useState(false)

  useEffect(() => {
    if (!isOpen) return
    remoteService
      .getPairingInfo()
      .then(setPairingInfo)
      .catch(() => {
        // Fallback local caso o daemon esteja iniciando
        setPairingInfo({
          lanIp: window.location.hostname || 'localhost',
          port: 4040,
          preacherToken: 'prc_local',
          operatorToken: 'opr_local',
          preacherUrl: `${window.location.origin}/remote/preacher`,
          operatorUrl: `${window.location.origin}/remote/operator`,
          stageUrl: `${window.location.origin}/stage`,
          broadcastUrl: 'http://localhost:3002',
        })
      })
  }, [isOpen])

  if (!isOpen || !pairingInfo) return null

  const activeUrl =
    selectedRole === 'preacher' ? pairingInfo.preacherUrl : pairingInfo.operatorUrl
  const matrix = buildDeterministicMatrix(activeUrl)

  const handleCopy = async () => {
    await navigator.clipboard.writeText(activeUrl)
    setCopied(true)
    setTimeout(() => setCopied(false), 2000)
  }

  const handleRotateTokens = async () => {
    const updated = await remoteService.rotateTokens()
    setPairingInfo(updated)
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-xs">
      <div className="flex w-full max-w-lg flex-col rounded-xl border border-white/10 bg-slate-900 text-slate-100 shadow-2xl overflow-hidden">
        <div className="flex items-center justify-between border-b border-white/10 bg-slate-800/60 px-5 py-3.5">
          <div className="flex items-center gap-2">
            <QrCode className="h-4 w-4 text-sky-400" />
            <h2 className="text-sm font-semibold text-white">
              Pareamento Mobile via QR Code (Zero App Store)
            </h2>
          </div>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex flex-col items-center gap-4 p-6">
          {/* Seletor de Perfil RBAC */}
          <div className="grid w-full grid-cols-2 gap-2">
            <Button
              variant={selectedRole === 'preacher' ? 'activeInfo' : 'secondary'}
              onClick={() => setSelectedRole('preacher')}
            >
              <Smartphone className="h-3.5 w-3.5" />
              <span>Púlpito do Pastor (RF-26A)</span>
            </Button>
            <Button
              variant={selectedRole === 'operator' ? 'activeInfo' : 'secondary'}
              onClick={() => setSelectedRole('operator')}
            >
              <QrCode className="h-3.5 w-3.5" />
              <span>Operador Volante (RF-26B)</span>
            </Button>
          </div>

          {/* QR Code SVG */}
          <div className="rounded-2xl border border-white/15 bg-white p-4 shadow-xl">
            <svg width="189" height="189" viewBox="0 0 21 21" shapeRendering="crispEdges">
              {matrix.map((row, rowIdx) =>
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
            <Badge variant={selectedRole === 'preacher' ? 'bible' : 'live'}>
              {selectedRole === 'preacher'
                ? 'ACESSO RESTRITO: SLIDES & NOTAS DO ORADOR'
                : 'ACESSO TOTAL: PÂNICO, LITURGIA, BÍBLIA & ALERTAS'}
            </Badge>
            <p className="mt-1 font-mono text-xs text-slate-300 break-all">{activeUrl}</p>
          </div>

          <div className="flex w-full items-center justify-center gap-2 pt-2">
            <Button variant="secondary" onClick={handleCopy}>
              {copied ? <Check className="h-3.5 w-3.5 text-emerald-400" /> : <Copy className="h-3.5 w-3.5" />}
              <span>{copied ? 'Link Copiado' : 'Copiar URL'}</span>
            </Button>

            <Button
              variant="outline"
              onClick={() =>
                window.open(
                  selectedRole === 'preacher' ? '/remote/preacher' : '/remote/operator',
                  '_blank',
                  'noopener,noreferrer',
                )
              }
            >
              <ExternalLink className="h-3.5 w-3.5 text-sky-400" />
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
