/**
 * @module @slices/songs/ui/components/SongImporterModal
 * @description Modal para Importação Completa do Holyrics Legado (`.zip` ou texto com tags `<c>`, `<t>` - RF-11).
 */

import { useState } from 'react'
import { X, Upload, CheckCircle2, FileArchive } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { Input } from '@ui/components/ui/input'
import { Textarea } from '@ui/components/ui/textarea'
import { songsService } from '../services/songs-service'

export interface SongImporterModalProps {
  isOpen: boolean
  onClose: () => void
  onImported?: () => void
}

/**
 * Modal de importação de backups `.zip` ou letras legadas do Holyrics (RF-11).
 *
 * @param props - Propriedades do modal.
 * @param props.isOpen - Indica se o modal está aberto.
 * @param props.onClose - Callback de fechamento.
 * @param props.onImported - Callback disparado após concluir a importação com sucesso.
 * @returns Elemento JSX do modal ou null.
 */
export function SongImporterModal({ isOpen, onClose, onImported }: SongImporterModalProps) {
  const [title, setTitle] = useState('')
  const [artist, setArtist] = useState('')
  const [rawLyrics, setRawLyrics] = useState('')
  const [statusMessage, setStatusMessage] = useState<string | null>(null)
  const [isImporting, setIsImporting] = useState(false)

  if (!isOpen) return null

  const handleZipFileSelect = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return

    setIsImporting(true)
    setStatusMessage(null)

    try {
      const arrayBuf = await file.arrayBuffer()
      const bytes = new Uint8Array(arrayBuf)
      let binaryStr = ''
      for (let idx = 0; idx < bytes.length; idx++) {
        binaryStr += String.fromCharCode(bytes[idx])
      }
      const zipBase64 = btoa(binaryStr)
      const report = await songsService.importHolyrics({ zipBase64 })
      setStatusMessage(`${report.importedCount} música(s) importada(s) do pacote ZIP com sucesso!`)
      onImported?.()
    } catch {
      setStatusMessage('Falha ao importar pacote ZIP.')
    } finally {
      setIsImporting(false)
    }
  }

  const handleManualLegacyImport = async () => {
    if (!title.trim() || !rawLyrics.trim()) return
    setIsImporting(true)
    setStatusMessage(null)

    try {
      const report = await songsService.importHolyrics({
        songs: [
          {
            title: title.trim(),
            artist: artist.trim() || 'Importado Holyrics',
            lyrics: rawLyrics,
            formatting: '',
            order: [],
            note: '',
            language: 'pt-BR',
          },
        ],
      })
      setStatusMessage(`Música "${report.songs[0]?.title || title}" higienizada e importada!`)
      setTitle('')
      setArtist('')
      setRawLyrics('')
      onImported?.()
    } catch {
      setStatusMessage('Erro ao importar música legada.')
    } finally {
      setIsImporting(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-xs">
      <div className="flex w-full max-w-xl flex-col rounded-xl border border-white/10 bg-slate-900 text-slate-100 shadow-2xl overflow-hidden">
        <div className="flex items-center justify-between border-b border-white/10 bg-slate-800/60 px-5 py-3.5">
          <div className="flex items-center gap-2">
            <FileArchive className="h-4 w-4 text-sky-400" />
            <h2 className="text-sm font-semibold text-white">
              Importador de Acervo e Letras
            </h2>
          </div>
          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        <div className="flex flex-col gap-4 p-5 text-xs">
          {/* Opção 1: Upload de Arquivo .zip */}
          <div className="flex flex-col gap-2 rounded-xl border border-dashed border-white/15 bg-slate-950/50 p-4">
            <span className="font-semibold text-sky-400">
              1. Importar Backup Completo (.zip com JSON/TXT do Holyrics)
            </span>
            <p className="text-slate-400">
              Descompactação instantânea em memória com <code className="text-slate-200">fflate</code> e remoção automática de tags proprietárias.
            </p>
            <Input type="file" accept=".zip" onChange={handleZipFileSelect} />
          </div>

          {/* Opção 2: Colar letra bruta com tags legadas */}
          <div className="flex flex-col gap-2 rounded-xl border border-white/10 bg-slate-950/50 p-4">
            <span className="font-semibold text-slate-200">
              2. Importação Rápida de Letra com Higienização de Tags
            </span>
            <div className="grid grid-cols-2 gap-2">
              <Input
                value={title}
                onChange={(event) => setTitle(event.target.value)}
                placeholder="Título do Louvor"
              />
              <Input
                value={artist}
                onChange={(event) => setArtist(event.target.value)}
                placeholder="Artista / Ministério"
              />
            </div>
            <Textarea
              rows={5}
              value={rawLyrics}
              onChange={(event) => setRawLyrics(event.target.value)}
              placeholder="Cole aqui a letra do Holyrics (mesmo com tags <c>, <t> ou ##)..."
            />
            <Button disabled={isImporting || !title.trim()} onClick={handleManualLegacyImport}>
              <Upload className="h-3.5 w-3.5" />
              <span>Higienizar e Salvar no SQLite FTS5</span>
            </Button>
          </div>

          {statusMessage && (
            <div className="flex items-center gap-2 rounded-lg border border-emerald-500/30 bg-emerald-500/10 px-3 py-2 text-emerald-300">
              <CheckCircle2 className="h-4 w-4 shrink-0" />
              <span>{statusMessage}</span>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
