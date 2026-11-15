/**
 * @module @slices/media/ui/components/MediaGridPanel
 * @description Catálogo visual de mídias, loops de vídeo e imagens de projeção com suporte a Drag & Drop e upload real (zero mocks).
 */

import { useState, useEffect, useMemo, useRef } from 'react'
import { Film, Image as ImageIcon, Search, Check, Upload, Trash2 } from 'lucide-react'
import type { MediaItem, MediaType } from '../../contracts/media-contracts'
import type { BackgroundLayer } from '@lumen/core'

export interface MediaGridPanelProps {
  activeBackground?: BackgroundLayer
  onSelectBackground: (background: BackgroundLayer) => void
  filterType?: MediaType | 'ALL'
  onDragMediaItem?: (event: React.DragEvent, item: MediaItem) => void
  className?: string
}

/**
 * Painel em grade para seleção, upload e arraste de mídias reais no telão.
 *
 * @param props - Propriedades do componente.
 * @returns Elemento JSX da grade de mídias.
 */
export function MediaGridPanel({
  activeBackground,
  onSelectBackground,
  filterType = 'ALL',
  onDragMediaItem,
  className = '',
}: MediaGridPanelProps) {
  const [mediaList, setMediaList] = useState<MediaItem[]>([])
  const [selectedType, setSelectedType] = useState<MediaType | 'ALL'>(filterType)
  const [searchQuery, setSearchQuery] = useState('')
  const [isUploading, setIsUploading] = useState(false)
  const fileInputRef = useRef<HTMLInputElement | null>(null)

  useEffect(() => {
    setSelectedType(filterType)
  }, [filterType])

  const loadMediaCatalog = () => {
    fetch('/api/media')
      .then((res) => res.json())
      .then((json: { ok: boolean; data?: MediaItem[] }) => {
        if (json.ok && Array.isArray(json.data)) {
          setMediaList(json.data)
        }
      })
      .catch(() => {
        setMediaList([])
      })
  }

  useEffect(() => {
    loadMediaCatalog()
  }, [])

  const filteredMedia = useMemo(() => {
    return mediaList.filter((item) => {
      if (item.type === 'AUDIO') return false
      const matchesType = selectedType === 'ALL' || item.type === selectedType
      const matchesQuery =
        !searchQuery ||
        item.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.tags.some((tag) => tag.toLowerCase().includes(searchQuery.toLowerCase()))

      return matchesType && matchesQuery
    })
  }, [mediaList, searchQuery, selectedType])

  const handleApplyBackground = (item: MediaItem) => {
    const isVideo = item.type === 'VIDEO'
    const streamUrl = `/api/media/stream/${encodeURIComponent(item.id)}`
    onSelectBackground({
      type: isVideo ? 'video' : 'image',
      source: streamUrl,
      loop: isVideo,
      muted: true,
    })
  }

  const uploadFiles = async (files: FileList | File[]) => {
    setIsUploading(true)
    try {
      for (const file of Array.from(files)) {
        const formData = new FormData()
        formData.append('file', file)
        await fetch('/api/media/upload', {
          method: 'POST',
          body: formData,
        })
      }
      loadMediaCatalog()
    } finally {
      setIsUploading(false)
    }
  }

  const handleDeleteMedia = async (event: React.MouseEvent, itemId: string) => {
    event.stopPropagation()
    await fetch(`/api/media/${encodeURIComponent(itemId)}`, { method: 'DELETE' })
    loadMediaCatalog()
  }

  return (
    <div
      onDragOver={(event) => {
        event.preventDefault()
      }}
      onDrop={(event) => {
        if (event.dataTransfer.files && event.dataTransfer.files.length > 0) {
          event.preventDefault()
          uploadFiles(event.dataTransfer.files)
        }
      }}
      className={`flex flex-col rounded-md bg-[#131C2B] text-slate-200 select-none overflow-hidden ${className}`}
    >
      {/* Barra de Filtro, Busca e Upload */}
      <div className="flex flex-wrap items-center justify-between gap-1.5 border-b border-white/5 bg-[#090D14]/50 p-2">
        {filterType === 'ALL' && (
          <div className="flex items-center gap-1 rounded bg-[#192334] p-0.5 text-xs">
            <button
              type="button"
              onClick={() => setSelectedType('ALL')}
              className={`rounded px-2 py-0.5 text-[10px] transition ${
                selectedType === 'ALL' ? 'bg-sky-500 text-slate-950 font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              Todos
            </button>
            <button
              type="button"
              onClick={() => setSelectedType('VIDEO')}
              className={`flex items-center gap-1 rounded px-2 py-0.5 text-[10px] transition ${
                selectedType === 'VIDEO' ? 'bg-sky-500 text-slate-950 font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <Film className="h-3 w-3" />
              <span>Vídeos</span>
            </button>
            <button
              type="button"
              onClick={() => setSelectedType('IMAGE')}
              className={`flex items-center gap-1 rounded px-2 py-0.5 text-[10px] transition ${
                selectedType === 'IMAGE' ? 'bg-sky-500 text-slate-950 font-semibold' : 'text-slate-400 hover:text-slate-200'
              }`}
            >
              <ImageIcon className="h-3 w-3" />
              <span>Imagens</span>
            </button>
          </div>
        )}

        <div className="relative flex-1 min-w-[100px]">
          <Search className="absolute left-2 top-1.5 h-3 w-3 text-slate-400" />
          <input
            type="text"
            placeholder="Filtrar mídias..."
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
            className="w-full rounded bg-[#192334] pl-6 pr-2 py-1 text-[11px] text-slate-200 placeholder:text-slate-500 focus:outline-none focus:ring-1 focus:ring-sky-500"
          />
        </div>

        <input
          ref={fileInputRef}
          type="file"
          multiple
          accept="image/*,video/*"
          className="hidden"
          onChange={(event) => {
            if (event.target.files && event.target.files.length > 0) {
              uploadFiles(event.target.files)
              event.target.value = ''
            }
          }}
        />
        <button
          type="button"
          disabled={isUploading}
          onClick={() => fileInputRef.current?.click()}
          title="Importar imagens ou vídeos do computador"
          className="flex items-center gap-1 rounded bg-sky-500/15 hover:bg-sky-500/25 border border-sky-500/30 px-2 py-1 text-[10px] font-medium text-sky-300 transition shrink-0"
        >
          <Upload className="h-3 w-3" />
          <span>{isUploading ? 'Enviando...' : 'Importar'}</span>
        </button>
      </div>

      {/* Grade de Itens de Mídia ou Estado Vazio */}
      <div className="flex-1 overflow-y-auto p-2.5 max-h-64">
        {filteredMedia.length === 0 ? (
          <div
            onClick={() => fileInputRef.current?.click()}
            className="flex flex-col items-center justify-center rounded-lg border border-dashed border-white/10 py-8 px-3 text-center cursor-pointer hover:border-sky-500/40 transition"
          >
            <Upload className="h-6 w-6 text-slate-500 mb-1.5" />
            <p className="text-xs font-medium text-slate-300">Nenhuma mídia importada</p>
            <p className="text-[10px] text-slate-500 mt-0.5">
              Arraste imagens ou vídeos aqui ou clique para importar
            </p>
          </div>
        ) : (
          <div className="grid grid-cols-2 gap-2">
            {filteredMedia.map((item) => {
              const streamUrl = `/api/media/stream/${encodeURIComponent(item.id)}`
              const isCurrentlyActive =
                activeBackground?.source === item.path || activeBackground?.source === streamUrl
              return (
                <div
                  key={item.id}
                  draggable
                  onDragStart={(event) => {
                    event.dataTransfer.setData(
                      'application/lumen-item',
                      JSON.stringify({
                        type: 'MEDIA',
                        title: item.name,
                        mediaSrc: streamUrl,
                        mediaType: item.type,
                      }),
                    )
                    onDragMediaItem?.(event, item)
                  }}
                  onClick={() => handleApplyBackground(item)}
                  className={`group relative flex flex-col rounded-[6px] cursor-pointer overflow-hidden transition ${
                    isCurrentlyActive
                      ? 'ring-1 ring-emerald-500 bg-[#1D2D3E]'
                      : 'bg-[#192334] hover:bg-[#1E2B40]'
                  }`}
                >
                  <div className="relative h-14 w-full bg-[#090D14] flex items-center justify-center overflow-hidden">
                    {item.type === 'VIDEO' ? (
                      <Film className="h-5 w-5 text-slate-500 group-hover:text-sky-400 transition" />
                    ) : (
                      <ImageIcon className="h-5 w-5 text-slate-500 group-hover:text-sky-400 transition" />
                    )}

                    <span className="absolute left-1 top-1 rounded bg-[#0F1622]/90 px-1 py-0.5 text-[9px] font-mono text-slate-400">
                      {item.type === 'VIDEO' ? 'VÍDEO' : 'IMG'}
                    </span>

                    {isCurrentlyActive ? (
                      <span className="absolute right-1 top-1 flex h-4 w-4 items-center justify-center rounded-full bg-emerald-500 text-slate-950">
                        <Check className="h-2.5 w-2.5 stroke-[3]" />
                      </span>
                    ) : (
                      <button
                        type="button"
                        onClick={(event) => handleDeleteMedia(event, item.id)}
                        title="Remover mídia"
                        className="opacity-0 group-hover:opacity-100 absolute right-1 top-1 rounded bg-black/70 p-1 text-rose-400 hover:text-rose-300 transition"
                      >
                        <Trash2 className="h-2.5 w-2.5" />
                      </button>
                    )}
                  </div>

                  <div className="p-1.5">
                    <div className="truncate text-[11px] font-medium text-slate-200">{item.name}</div>
                  </div>
                </div>
              )
            })}
          </div>
        )}
      </div>
    </div>
  )
}
