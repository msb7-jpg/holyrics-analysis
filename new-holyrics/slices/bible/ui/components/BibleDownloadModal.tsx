/**
 * @module @slices/bible/ui/components/BibleDownloadModal
 * @description Modal do Gerenciador de Downloads e Traduções Bíblicas (RF-17).
 * Permite instalar e remover traduções (ACF, NVI, ARA, NAA, NVT, ARC, KJV) em tempo de execução sem reiniciar.
 */

import { useState, useEffect } from 'react'
import { X, Download, Check, Trash2, BookOpen } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { Badge } from '@ui/components/ui/badge'
import type { AvailableBibleDownloadItem } from '../../contracts/bible-contracts'
import { bibleService } from '../services/bible-service'

export interface BibleDownloadModalProps {
  isOpen: boolean
  onClose: () => void
}

/**
 * Modal de gerenciamento e instalação de traduções bíblicas (RF-17).
 *
 * @param props - Propriedades do modal.
 * @param props.isOpen - Indica se o modal está visível.
 * @param props.onClose - Callback de fechamento.
 * @returns Elemento JSX do gerenciador de traduções ou null.
 */
export function BibleDownloadModal({ isOpen, onClose }: BibleDownloadModalProps) {
  const [items, setItems] = useState<AvailableBibleDownloadItem[]>([])
  const [loadingId, setLoadingId] = useState<string | null>(null)

  const refreshCatalog = () => {
    bibleService
      .listAvailableDownloads()
      .then(setItems)
      .catch(() => setItems([]))
  }

  useEffect(() => {
    if (isOpen) {
      refreshCatalog()
    }
  }, [isOpen])

  if (!isOpen) return null

  const handleInstall = async (item: AvailableBibleDownloadItem) => {
    setLoadingId(item.id)
    try {
      await bibleService.installVersion(item.id, item.name)
      refreshCatalog()
    } finally {
      setLoadingId(null)
    }
  }

  const handleRemove = async (versionId: string) => {
    setLoadingId(versionId)
    try {
      const updated = await bibleService.removeVersion(versionId)
      setItems(updated)
    } finally {
      setLoadingId(null)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-xs">
      <div className="flex max-h-[85vh] w-full max-w-xl flex-col rounded-xl border border-white/10 bg-slate-900 text-slate-100 shadow-2xl overflow-hidden">
        <div className="flex items-center justify-between border-b border-white/10 bg-slate-800/60 px-5 py-3.5">
          <div className="flex items-center gap-2">
            <BookOpen className="h-4 w-4 text-sky-400" />
            <h2 className="text-sm font-semibold text-white">
              Gerenciador de Traduções Bíblicas Offline
            </h2>
          </div>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex-1 overflow-y-auto p-4">
          <div className="flex flex-col gap-2">
            {items.map((item) => (
              <div
                key={item.id}
                className="flex items-center justify-between rounded-xl border border-white/10 bg-slate-800/50 px-4 py-3"
              >
                <div className="flex flex-col">
                  <div className="flex items-center gap-2">
                    <span className="font-mono text-xs font-bold text-sky-400">{item.id}</span>
                    <span className="text-xs font-semibold text-white">{item.name}</span>
                    <Badge variant="default">{item.language}</Badge>
                  </div>
                  <span className="mt-0.5 text-[11px] text-slate-400">
                    {item.verseCount.toLocaleString('pt-BR')} versículos indexados via SQLite FTS5
                  </span>
                </div>

                <div className="flex items-center gap-2">
                  {item.installed ? (
                    <>
                      <Badge variant="success">
                        <Check className="h-3 w-3" />
                        <span>INSTALADA</span>
                      </Badge>
                      {item.id !== 'ACF' && (
                        <Button
                          size="icon"
                          variant="ghost"
                          disabled={loadingId === item.id}
                          onClick={() => handleRemove(item.id)}
                          title="Remover tradução"
                        >
                          <Trash2 className="h-3.5 w-3.5 text-slate-400" />
                        </Button>
                      )}
                    </>
                  ) : (
                    <Button
                      size="sm"
                      disabled={loadingId === item.id}
                      onClick={() => handleInstall(item)}
                    >
                      <Download className="h-3.5 w-3.5" />
                      <span>{loadingId === item.id ? 'Instalando...' : 'Instalar'}</span>
                    </Button>
                  )}
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  )
}
