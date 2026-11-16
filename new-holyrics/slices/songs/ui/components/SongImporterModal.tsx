/**
 * @module @slices/songs/ui/components/SongImporterModal
 * @description Editor Visual de Músicas (estilo MusicView.java) com preview 16:9 em tempo real, fundo associado, busca online sem API key e importação .zip.
 */

import { useState, useEffect, useMemo } from 'react'
import {
  X,
  Upload,
  CheckCircle2,
  AlertCircle,
  FileArchive,
  Globe,
  Search,
  Loader2,
  Music,
  Image as ImageIcon,
  Save,
} from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { Input } from '@ui/components/ui/input'
import { Textarea } from '@ui/components/ui/textarea'
import type { MediaItem } from '@slices/media/contracts/media-contracts'
import type { Song } from '../../contracts/song-contracts'
import { songsService } from '../services/songs-service'

export interface SongImporterModalProps {
  isOpen: boolean
  onClose: () => void
  onImported?: () => void
}

type ModalTab = 'EDITOR' | 'ONLINE' | 'BACKUP'

interface OnlineSearchResult {
  id: string
  title: string
  band: string
  url: string
  source?: string
}

/**
 * Quebra texto bruto em blocos/slides por linha em branco (Enter duplo).
 */
function parseLyricsToBlocks(rawText: string): string[][] {
  return rawText
    .split(/\n\s*\n/)
    .map((block) =>
      block
        .split('\n')
        .map((line) => line.trim())
        .filter(Boolean),
    )
    .filter((lines) => lines.length > 0)
}

/**
 * Modal unificado de Edição Visual de Músicas, Busca Online e Importação de Backup.
 *
 * @param props - Propriedades do modal.
 * @returns Elemento JSX do modal ou null.
 */
export function SongImporterModal({ isOpen, onClose, onImported }: SongImporterModalProps) {
  const [activeTab, setActiveTab] = useState<ModalTab>('EDITOR')

  // Estado do Editor Lado a Lado (BUG-10)
  const [title, setTitle] = useState('')
  const [artist, setArtist] = useState('')
  const [rawLyrics, setRawLyrics] = useState('')
  const [selectedBgId, setSelectedBgId] = useState<string>('')
  const [mediaItems, setMediaItems] = useState<MediaItem[]>([])

  // Estado da Busca Online (BUG-11)
  const [onlineQuery, setOnlineQuery] = useState('')
  const [onlineResults, setOnlineResults] = useState<OnlineSearchResult[]>([])
  const [isSearchingOnline, setIsSearchingOnline] = useState(false)
  const [isFetchingLyrics, setIsFetchingLyrics] = useState(false)
  const [previewOnlineSong, setPreviewOnlineSong] = useState<Song | null>(null)

  // Feedback estruturado (BUG-09)
  const [status, setStatus] = useState<{ type: 'success' | 'error'; text: string } | null>(null)
  const [isSaving, setIsSaving] = useState(false)

  useEffect(() => {
    if (!isOpen) return
    fetch('/api/media')
      .then((res) => res.json())
      .then((json: { ok: boolean; data?: MediaItem[] }) => {
        if (json.ok && Array.isArray(json.data)) {
          setMediaItems(json.data.filter((item) => item.type === 'IMAGE' || item.type === 'VIDEO'))
        }
      })
      .catch(() => setMediaItems([]))
  }, [isOpen])

  const liveSlides = useMemo(() => parseLyricsToBlocks(rawLyrics), [rawLyrics])

  const selectedMediaBg = useMemo(
    () => mediaItems.find((item) => item.id === selectedBgId) ?? null,
    [mediaItems, selectedBgId],
  )

  if (!isOpen) return null

  const handleSaveEditorSong = async () => {
    if (!title.trim() || liveSlides.length === 0) {
      setStatus({
        type: 'error',
        text: 'Preencha o título e pelo menos uma estrofe para salvar a música.',
      })
      return
    }

    setIsSaving(true)
    setStatus(null)

    try {
      const sections = liveSlides.map((lines, idx) => ({
        id: `sec-${idx + 1}`,
        index: idx + 1,
        lines,
      }))

      const songId = `song-${title
        .trim()
        .toLowerCase()
        .replace(/[^a-z0-9]+/g, '-')}-${Date.now()}`

      const defaultBackground = selectedMediaBg
        ? {
            type: selectedMediaBg.type === 'VIDEO' ? ('video' as const) : ('image' as const),
            source: `/api/media/stream/${encodeURIComponent(selectedMediaBg.id)}`,
            loop: selectedMediaBg.type === 'VIDEO',
            muted: true,
          }
        : undefined

      const payload: Song = {
        id: songId,
        title: title.trim(),
        artist: artist.trim() || undefined,
        sections,
        defaultArrangement: sections.map((s) => s.id),
        defaultBackground,
      }

      const res = await fetch('/api/songs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload),
      })
      const json = (await res.json()) as { ok: boolean; error?: string }
      if (!res.ok || !json.ok) {
        throw new Error(json.error || 'Erro ao salvar música')
      }

      setStatus({ type: 'success', text: `Música "${title.trim()}" salva no acervo com sucesso!` })
      setTitle('')
      setArtist('')
      setRawLyrics('')
      setSelectedBgId('')
      onImported?.()
    } catch (error) {
      setStatus({
        type: 'error',
        text: error instanceof Error ? error.message : 'Erro ao salvar a música no acervo.',
      })
    } finally {
      setIsSaving(false)
    }
  }

  const handleSearchOnline = async (event?: React.FormEvent) => {
    event?.preventDefault()
    if (!onlineQuery.trim()) return

    setIsSearchingOnline(true)
    setStatus(null)
    setPreviewOnlineSong(null)

    try {
      const res = await fetch(`/api/vagalume/search?q=${encodeURIComponent(onlineQuery.trim())}`)
      const json = (await res.json()) as { ok: boolean; data?: OnlineSearchResult[]; error?: string }
      if (!res.ok || !json.ok) {
        throw new Error(json.error || 'Falha ao buscar letras online.')
      }
      setOnlineResults(json.data || [])
      if ((json.data || []).length === 0) {
        setStatus({ type: 'error', text: 'Nenhuma letra encontrada para essa busca.' })
      }
    } catch (error) {
      setStatus({
        type: 'error',
        text: error instanceof Error ? error.message : 'Erro ao consultar busca online.',
      })
    } finally {
      setIsSearchingOnline(false)
    }
  }

  const handleFetchOnlineSong = async (musId: string) => {
    setIsFetchingLyrics(true)
    setStatus(null)
    try {
      const res = await fetch(`/api/vagalume/fetch?musId=${encodeURIComponent(musId)}`)
      const json = (await res.json()) as { ok: boolean; data?: Song; error?: string }
      if (!res.ok || !json.ok || !json.data) {
        throw new Error(json.error || 'Não foi possível carregar a letra desta música.')
      }
      setPreviewOnlineSong(json.data)
    } catch (error) {
      setStatus({
        type: 'error',
        text: error instanceof Error ? error.message : 'Erro ao baixar letra selecionada.',
      })
    } finally {
      setIsFetchingLyrics(false)
    }
  }

  const handleLoadOnlineIntoEditor = (song: Song) => {
    setTitle(song.title)
    setArtist(song.artist || '')
    setRawLyrics(song.sections.map((s) => s.lines.join('\n')).join('\n\n'))
    setActiveTab('EDITOR')
    setStatus({ type: 'success', text: `Letra "${song.title}" carregada no editor!` })
  }

  const handleSaveOnlineDirect = async (song: Song) => {
    setIsSaving(true)
    setStatus(null)
    try {
      const res = await fetch('/api/songs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(song),
      })
      const json = (await res.json()) as { ok: boolean; error?: string }
      if (!res.ok || !json.ok) throw new Error(json.error || 'Falha ao salvar')
      setStatus({ type: 'success', text: `"${song.title}" importada para o acervo!` })
      onImported?.()
    } catch {
      setStatus({ type: 'error', text: 'Erro ao salvar música no acervo.' })
    } finally {
      setIsSaving(false)
    }
  }

  const handleZipFileSelect = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0]
    if (!file) return

    setIsSaving(true)
    setStatus(null)

    try {
      const arrayBuf = await file.arrayBuffer()
      const bytes = new Uint8Array(arrayBuf)
      let binaryStr = ''
      for (let idx = 0; idx < bytes.length; idx++) {
        binaryStr += String.fromCharCode(bytes[idx])
      }
      const zipBase64 = btoa(binaryStr)
      const report = await songsService.importHolyrics({ zipBase64 })
      setStatus({
        type: 'success',
        text: `${report.importedCount} música(s) importada(s) do pacote ZIP com sucesso!`,
      })
      onImported?.()
    } catch {
      setStatus({
        type: 'error',
        text: 'Falha ao importar pacote ZIP. Verifique se o arquivo é um backup válido.',
      })
    } finally {
      setIsSaving(false)
    }
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-xs">
      <div className="flex max-h-[90vh] w-full max-w-5xl flex-col rounded-xl border border-white/10 bg-slate-900 text-slate-100 shadow-2xl overflow-hidden">
        {/* Cabeçalho + Abas */}
        <div className="flex items-center justify-between border-b border-white/10 bg-slate-800/60 px-5 py-3">
          <div className="flex items-center gap-4">
            <div className="flex items-center gap-2">
              <Music className="h-4 w-4 text-sky-400" />
              <h2 className="text-sm font-semibold text-white">Acervo & Editor de Músicas</h2>
            </div>

            <div className="flex items-center gap-1 rounded-lg bg-slate-950/80 p-1 text-xs">
              <button
                type="button"
                onClick={() => {
                  setActiveTab('EDITOR')
                  setStatus(null)
                }}
                className={`rounded-md px-3 py-1 font-medium transition ${
                  activeTab === 'EDITOR'
                    ? 'bg-sky-500 text-slate-950'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                Editor / Nova Música
              </button>
              <button
                type="button"
                onClick={() => {
                  setActiveTab('ONLINE')
                  setStatus(null)
                }}
                className={`flex items-center gap-1.5 rounded-md px-3 py-1 font-medium transition ${
                  activeTab === 'ONLINE'
                    ? 'bg-sky-500 text-slate-950'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                <Globe className="h-3.5 w-3.5" />
                <span>Buscar na Internet</span>
              </button>
              <button
                type="button"
                onClick={() => {
                  setActiveTab('BACKUP')
                  setStatus(null)
                }}
                className={`flex items-center gap-1.5 rounded-md px-3 py-1 font-medium transition ${
                  activeTab === 'BACKUP'
                    ? 'bg-sky-500 text-slate-950'
                    : 'text-slate-400 hover:text-slate-200'
                }`}
              >
                <FileArchive className="h-3.5 w-3.5" />
                <span>Importar Backup (.zip)</span>
              </button>
            </div>
          </div>

          <Button variant="ghost" size="icon" onClick={onClose}>
            <X className="h-4 w-4" />
          </Button>
        </div>

        {/* Conteúdo */}
        <div className="flex-1 overflow-y-auto p-5 text-xs">
          {/* ABA 1: EDITOR LADO A LADO COM PREVIEW 16:9 AO VIVO */}
          {activeTab === 'EDITOR' && (
            <div className="grid grid-cols-1 gap-5 lg:grid-cols-2">
              {/* Esquerda: Campos e Letra */}
              <div className="flex flex-col gap-3">
                <div className="grid grid-cols-2 gap-2">
                  <div>
                    <label className="mb-1 block text-slate-400">Título do Louvor</label>
                    <Input
                      value={title}
                      onChange={(event) => setTitle(event.target.value)}
                      placeholder="Ex: Bondade de Deus"
                    />
                  </div>
                  <div>
                    <label className="mb-1 block text-slate-400">Artista / Ministério</label>
                    <Input
                      value={artist}
                      onChange={(event) => setArtist(event.target.value)}
                      placeholder="Ex: Isaías Saad"
                    />
                  </div>
                </div>

                <div>
                  <label className="mb-1 flex items-center gap-1.5 text-slate-400">
                    <ImageIcon className="h-3.5 w-3.5 text-sky-400" />
                    <span>Fundo Associado à Música (Opcional)</span>
                  </label>
                  <select
                    value={selectedBgId}
                    onChange={(event) => setSelectedBgId(event.target.value)}
                    className="w-full rounded-md border border-white/10 bg-slate-950 px-3 py-2 text-xs text-slate-100 focus:outline-none focus:ring-1 focus:ring-sky-500"
                  >
                    <option value="">Nenhum (manter fundo atual do telão)</option>
                    {mediaItems.map((media) => (
                      <option key={media.id} value={media.id}>
                        [{media.type === 'VIDEO' ? 'Vídeo' : 'Imagem'}] {media.name}
                      </option>
                    ))}
                  </select>
                </div>

                <div className="flex flex-1 flex-col">
                  <label className="mb-1 flex items-center justify-between text-slate-400">
                    <span>Letra da Música</span>
                    <span className="text-[10px] text-slate-500">
                      Linha em branco (Enter duplo) = novo slide
                    </span>
                  </label>
                  <Textarea
                    rows={12}
                    value={rawLyrics}
                    onChange={(event) => setRawLyrics(event.target.value)}
                    placeholder={`Te amo, ó Deus\nTua graça nunca falha\n\nMinha vida está em Tuas mãos`}
                    className="flex-1 font-mono text-xs leading-relaxed"
                  />
                </div>

                <Button
                  disabled={isSaving || !title.trim() || liveSlides.length === 0}
                  onClick={handleSaveEditorSong}
                >
                  <Save className="h-3.5 w-3.5" />
                  <span>{isSaving ? 'Salvando...' : 'Salvar Música no Acervo'}</span>
                </Button>
              </div>

              {/* Direita: Grade de Preview 16:9 em Tempo Real */}
              <div className="flex flex-col rounded-xl border border-white/10 bg-slate-950/60 p-3">
                <div className="mb-2.5 flex items-center justify-between">
                  <span className="font-semibold text-slate-300">
                    Pré-visualização dos Slides ({liveSlides.length})
                  </span>
                  <span className="text-[10px] text-slate-500">Proporção 16:9</span>
                </div>

                {liveSlides.length === 0 ? (
                  <div className="flex flex-1 flex-col items-center justify-center py-16 text-center text-slate-500">
                    <Music className="h-7 w-7 mb-2 opacity-40" />
                    <p>Digite ou cole a letra à esquerda para visualizar os slides ao vivo.</p>
                  </div>
                ) : (
                  <div className="grid grid-cols-2 auto-rows-max content-start gap-3 overflow-y-auto max-h-[420px] pr-1.5">
                    {liveSlides.map((lines, idx) => (
                      <div
                        key={idx}
                        className="relative aspect-video w-full shrink-0 rounded-md border border-white/10 bg-slate-900 overflow-hidden flex items-center justify-center pt-6 pb-3 px-3 text-center"
                      >
                        {selectedMediaBg && selectedMediaBg.type === 'IMAGE' && (
                          <img
                            src={`/api/media/stream/${encodeURIComponent(selectedMediaBg.id)}`}
                            alt=""
                            className="absolute inset-0 h-full w-full object-cover opacity-50"
                          />
                        )}
                        {selectedMediaBg && selectedMediaBg.type === 'VIDEO' && (
                          <video
                            src={`/api/media/stream/${encodeURIComponent(selectedMediaBg.id)}`}
                            muted
                            className="absolute inset-0 h-full w-full object-cover opacity-50"
                          />
                        )}
                        <span className="absolute left-1.5 top-1 z-20 rounded bg-black/75 px-1.5 py-0.5 text-[9px] font-semibold text-sky-300">
                          Slide {idx + 1}
                        </span>
                        <div className="relative z-10 text-[10px] font-semibold leading-snug text-white drop-shadow-[0_1px_3px_rgba(0,0,0,0.9)] line-clamp-4">
                          {lines.map((line, lIdx) => (
                            <p key={lIdx}>{line}</p>
                          ))}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </div>
            </div>
          )}

          {/* ABA 2: BUSCA ONLINE 100% SEM API KEY */}
          {activeTab === 'ONLINE' && (
            <div className="flex flex-col gap-4">
              <form onSubmit={handleSearchOnline} className="flex gap-2">
                <div className="relative flex-1">
                  <Search className="absolute left-3 top-2.5 h-3.5 w-3.5 text-slate-400" />
                  <Input
                    value={onlineQuery}
                    onChange={(event) => setOnlineQuery(event.target.value)}
                    placeholder="Buscar música ou artista na internet (ex: Porque Ele Vive, Bondade de Deus)..."
                    className="pl-9"
                  />
                </div>
                <Button type="submit" disabled={isSearchingOnline || !onlineQuery.trim()}>
                  {isSearchingOnline ? (
                    <Loader2 className="h-3.5 w-3.5 animate-spin" />
                  ) : (
                    <Search className="h-3.5 w-3.5" />
                  )}
                  <span>Buscar</span>
                </Button>
              </form>

              <div className="grid grid-cols-1 gap-4 lg:grid-cols-2">
                {/* Resultados */}
                <div className="flex flex-col gap-1.5 max-h-[360px] overflow-y-auto rounded-xl border border-white/10 bg-slate-950/50 p-3">
                  <span className="mb-1 text-[11px] font-semibold text-slate-400 uppercase tracking-wider">
                    Resultados ({onlineResults.length})
                  </span>
                  {onlineResults.length === 0 ? (
                    <p className="py-10 text-center text-slate-500">
                      Digite o nome da música acima para buscar online gratuitamente.
                    </p>
                  ) : (
                    onlineResults.map((item) => (
                      <button
                        key={item.id}
                        type="button"
                        onClick={() => handleFetchOnlineSong(item.id)}
                        className="flex items-center justify-between rounded-md bg-slate-900 hover:bg-slate-800 px-3 py-2 text-left transition"
                      >
                        <div className="min-w-0 pr-2">
                          <p className="font-semibold text-slate-100 truncate">{item.title}</p>
                          <p className="text-[11px] text-slate-400 truncate">{item.band}</p>
                        </div>
                        {item.source && (
                          <span className="rounded bg-sky-500/15 px-1.5 py-0.5 text-[9px] font-semibold text-sky-300 shrink-0">
                            {item.source}
                          </span>
                        )}
                      </button>
                    ))
                  )}
                </div>

                {/* Preview da letra selecionada */}
                <div className="flex flex-col justify-between rounded-xl border border-white/10 bg-slate-950/50 p-3 max-h-[360px]">
                  {isFetchingLyrics ? (
                    <div className="flex flex-1 items-center justify-center py-12">
                      <Loader2 className="h-6 w-6 animate-spin text-sky-400" />
                    </div>
                  ) : previewOnlineSong ? (
                    <>
                      <div className="mb-2 border-b border-white/10 pb-2">
                        <h3 className="text-sm font-semibold text-white">{previewOnlineSong.title}</h3>
                        <p className="text-[11px] text-slate-400">{previewOnlineSong.artist}</p>
                      </div>
                      <div className="flex-1 overflow-y-auto space-y-2.5 pr-1 text-slate-200">
                        {previewOnlineSong.sections.map((sec) => (
                          <div key={sec.id} className="rounded bg-slate-900/70 p-2">
                            {sec.lines.map((line, i) => (
                              <p key={i}>{line}</p>
                            ))}
                          </div>
                        ))}
                      </div>
                      <div className="mt-3 flex gap-2 pt-2 border-t border-white/10">
                        <Button
                          variant="secondary"
                          className="flex-1"
                          onClick={() => handleLoadOnlineIntoEditor(previewOnlineSong)}
                        >
                          Editar / Associar Fundo
                        </Button>
                        <Button
                          className="flex-1"
                          disabled={isSaving}
                          onClick={() => handleSaveOnlineDirect(previewOnlineSong)}
                        >
                          <Upload className="h-3.5 w-3.5" />
                          <span>Importar Direto</span>
                        </Button>
                      </div>
                    </>
                  ) : (
                    <div className="flex flex-1 items-center justify-center py-12 text-slate-500">
                      Selecione um resultado à esquerda para visualizar a letra.
                    </div>
                  )}
                </div>
              </div>
            </div>
          )}

          {/* ABA 3: IMPORTAR BACKUP (.ZIP) */}
          {activeTab === 'BACKUP' && (
            <div className="flex flex-col gap-3 rounded-xl border border-dashed border-white/15 bg-slate-950/50 p-6">
              <span className="font-semibold text-sky-400 text-sm">
                Importar Backup Completo do Holyrics (.zip)
              </span>
              <p className="text-slate-400">
                Selecione um arquivo de backup (.zip) exportado do Holyrics para importar todas as músicas automaticamente para o acervo.
              </p>
              <Input type="file" accept=".zip" onChange={handleZipFileSelect} disabled={isSaving} />
            </div>
          )}

          {status && (
            <div
              className={`mt-4 flex items-center gap-2 rounded-lg border px-3 py-2 ${
                status.type === 'error'
                  ? 'border-rose-500/30 bg-rose-500/10 text-rose-300'
                  : 'border-emerald-500/30 bg-emerald-500/10 text-emerald-300'
              }`}
            >
              {status.type === 'error' ? (
                <AlertCircle className="h-4 w-4 shrink-0" />
              ) : (
                <CheckCircle2 className="h-4 w-4 shrink-0" />
              )}
              <span>{status.text}</span>
            </div>
          )}
        </div>
      </div>
    </div>
  )
}
