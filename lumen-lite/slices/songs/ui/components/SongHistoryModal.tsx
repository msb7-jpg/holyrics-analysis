/**
 * @module @slices/songs/ui/components/SongHistoryModal
 * @description Modal de Histórico e Relatório de Execução de Músicas (RF-12).
 * Exibe tanto o ranking de frequência (`playCount`) quanto o log cronológico das músicas cantadas.
 */

import { useState, useEffect } from 'react'
import { X, History, BarChart3, Clock } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { Badge } from '@ui/components/ui/badge'
import type { SongHistoryEntry, SongUsageStat } from '../../contracts/song-contracts'
import { songsService } from '../services/songs-service'

export interface SongHistoryModalProps {
  isOpen: boolean
  onClose: () => void
  onSelectSong?: (songId: string) => void
}

/**
 * Modal de consulta ao histórico de execução e frequência de louvores (RF-12).
 *
 * @param props - Propriedades do modal.
 * @param props.isOpen - Se o modal está visível.
 * @param props.onClose - Callback para fechar o modal.
 * @param props.onSelectSong - Callback opcional para abrir uma música diretamente do histórico.
 * @returns Elemento JSX do modal ou null.
 */
export function SongHistoryModal({ isOpen, onClose, onSelectSong }: SongHistoryModalProps) {
  const [viewMode, setViewMode] = useState<'stats' | 'timeline'>('stats')
  const [stats, setStats] = useState<SongUsageStat[]>([])
  const [entries, setEntries] = useState<SongHistoryEntry[]>([])

  useEffect(() => {
    if (!isOpen) return
    Promise.all([songsService.getUsageStats(30), songsService.getHistory(50)])
      .then(([loadedStats, loadedEntries]) => {
        setStats(loadedStats)
        setEntries(loadedEntries)
      })
      .catch(() => {
        setStats([])
        setEntries([])
      })
  }, [isOpen])

  if (!isOpen) return null

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-xs">
      <div className="flex max-h-[85vh] w-full max-w-2xl flex-col rounded-xl border border-white/10 bg-slate-900 text-slate-100 shadow-2xl overflow-hidden">
        <div className="flex items-center justify-between border-b border-white/10 bg-slate-800/60 px-5 py-3.5">
          <div className="flex items-center gap-2">
            <History className="h-4 w-4 text-sky-400" />
            <h2 className="text-sm font-semibold text-white">
              Histórico & Relatório de Execução Musical
            </h2>
          </div>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex items-center gap-2 border-b border-white/10 bg-slate-900/80 px-5 py-2.5">
          <Button
            size="sm"
            variant={viewMode === 'stats' ? 'activeInfo' : 'secondary'}
            onClick={() => setViewMode('stats')}
          >
            <BarChart3 className="h-3.5 w-3.5" />
            <span>Mais Cantadas ({stats.length})</span>
          </Button>
          <Button
            size="sm"
            variant={viewMode === 'timeline' ? 'activeInfo' : 'secondary'}
            onClick={() => setViewMode('timeline')}
          >
            <Clock className="h-3.5 w-3.5" />
            <span>Linha do Tempo ({entries.length})</span>
          </Button>
        </div>

        <div className="flex-1 overflow-y-auto p-4">
          {viewMode === 'stats' ? (
            <div className="flex flex-col gap-2">
              {stats.length === 0 ? (
                <p className="py-8 text-center text-xs text-slate-500">
                  Nenhuma música executada ainda nos cultos registrados.
                </p>
              ) : (
                stats.map((stat, idx) => (
                  <div
                    key={stat.songId}
                    className="flex items-center justify-between rounded-lg border border-white/5 bg-slate-800/50 px-4 py-2.5"
                  >
                    <div className="flex items-center gap-3">
                      <span className="font-mono text-xs text-slate-500 w-5">#{idx + 1}</span>
                      <div>
                        <p className="text-xs font-semibold text-white">{stat.title}</p>
                        <p className="text-[11px] text-slate-400">
                          {stat.artist || 'Autor desconhecido'} • Última vez:{' '}
                          {new Date(stat.lastPlayedAt).toLocaleDateString('pt-BR')}
                        </p>
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge variant="bible">{stat.playCount}x executada</Badge>
                      {onSelectSong && (
                        <Button
                          size="sm"
                          variant="ghost"
                          onClick={() => {
                            onSelectSong(stat.songId)
                            onClose()
                          }}
                        >
                          Abrir
                        </Button>
                      )}
                    </div>
                  </div>
                ))
              )}
            </div>
          ) : (
            <div className="flex flex-col gap-2">
              {entries.map((entry) => (
                <div
                  key={entry.id}
                  className="flex items-center justify-between rounded-lg border border-white/5 bg-slate-800/40 px-4 py-2"
                >
                  <div>
                    <p className="text-xs font-semibold text-white">{entry.title}</p>
                    <p className="text-[11px] text-slate-400">{entry.artist}</p>
                  </div>
                  <span className="font-mono text-[11px] text-slate-400">
                    {new Date(entry.playedAt).toLocaleString('pt-BR')}
                  </span>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
