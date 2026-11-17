/**
 * @module @slices/media/ui/components/MediaGridPanel
 * @description Catálogo visual de mídias, áudio, pastas, preview ampliado (Lightbox) e gerenciamento em disco.
 */

import { useState, useEffect, useMemo, useRef, memo } from 'react'
import {
  Film,
  Image as ImageIcon,
  Music,
  Search,
  Radio,
  Upload,
  Trash2,
  Folder,
  FolderPlus,
  FolderOpen,
  RefreshCw,
  Eye,
  Pencil,
  X,
  ChevronRight,
  Presentation,
} from 'lucide-react'
import type { MediaItem, MediaType } from '../../contracts/media-contracts'
import type { BackgroundLayer } from '@lumen/core'

export interface MediaGridPanelProps {
  activeBackground?: BackgroundLayer
  onSelectBackground: (background: BackgroundLayer) => void
  onProjectMedia?: (item: MediaItem) => void
  filterType?: MediaType | 'ALL' | 'VISUAL'
  onDragMediaItem?: (event: React.DragEvent, item: MediaItem) => void
  className?: string
}

interface ContextMenuState {
  x: number
  y: number
  item?: MediaItem
  folderPath?: string
}

function MediaThumb({ item }: { item: MediaItem }) {
  const [hasError, setHasError] = useState(false)
  const thumbUrl = `/api/media/thumb/${encodeURIComponent(item.id)}`

  if (item.type === 'IMAGE' || (item.type === 'VIDEO' && !hasError)) {
    return (
      <img
        src={thumbUrl}
        alt={item.name}
        loading="lazy"
        decoding="async"
        onError={() => setHasError(true)}
        className="h-full w-full object-cover"
      />
    )
  }

  if (item.type === 'VIDEO') {
    return <Film className="h-5 w-5 text-zinc-400/75" />
  }

  return <Music className="h-5 w-5 text-zinc-400" />
}

/**
 * Painel em grade para seleção, preview ampliado, pastas, upload e arraste de mídias reais.
 *
 * @param props - Propriedades do componente.
 * @returns Elemento JSX da grade de mídias.
 */
export const MediaGridPanel = memo(function MediaGridPanel({
  activeBackground,
  onSelectBackground,
  onProjectMedia,
  filterType = 'VISUAL',
  onDragMediaItem,
  className = '',
}: MediaGridPanelProps) {
  const [mediaList, setMediaList] = useState<MediaItem[]>([])
  const [folders, setFolders] = useState<string[]>([])
  const [currentFolder, setCurrentFolder] = useState<string>('')
  const [selectedType, setSelectedType] = useState<MediaType | 'ALL' | 'VISUAL'>(filterType)
  const [searchQuery, setSearchQuery] = useState('')
  const [isUploading, setIsUploading] = useState(false)
  const [previewItem, setPreviewItem] = useState<MediaItem | null>(null)
  const [contextMenu, setContextMenu] = useState<ContextMenuState | null>(null)
  const fileInputRef = useRef<HTMLInputElement | null>(null)

  useEffect(() => {
    setSelectedType(filterType)
  }, [filterType])

  useEffect(() => {
    const closeMenu = () => setContextMenu(null)
    window.addEventListener('click', closeMenu)
    return () => window.removeEventListener('click', closeMenu)
  }, [])

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

    fetch('/api/media/folders')
      .then((res) => res.json())
      .then((json: { ok: boolean; data?: string[] }) => {
        if (json.ok && Array.isArray(json.data)) {
          setFolders(json.data)
        }
      })
      .catch(() => {
        setFolders([])
      })
  }

  useEffect(() => {
    loadMediaCatalog()
  }, [])

  const immediateChildFolders = useMemo(() => {
    const prefix = currentFolder ? `${currentFolder}/` : ''
    const children = new Set<string>()
    for (const folder of folders) {
      if (!folder.startsWith(prefix)) continue
      const rest = folder.slice(prefix.length)
      if (!rest || rest.includes('/')) continue
      children.add(folder)
    }
    return Array.from(children).sort()
  }, [folders, currentFolder])

  const filteredMedia = useMemo(() => {
    return mediaList.filter((item) => {
      const itemFolder = item.folder || ''
      if (!searchQuery && itemFolder !== currentFolder) {
        return false
      }

      if (selectedType === 'VISUAL') {
        if (item.type === 'AUDIO') return false
      } else if (selectedType === 'ALL') {
        if (item.type === 'AUDIO') return false
      } else if (item.type !== selectedType) {
        return false
      }

      const matchesQuery =
        !searchQuery ||
        item.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
        item.tags.some((tag) => tag.toLowerCase().includes(searchQuery.toLowerCase()))

      return matchesQuery
    })
  }, [mediaList, searchQuery, selectedType, currentFolder])

  const handleApplyBackground = (item: MediaItem) => {
    if (item.type === 'AUDIO') {
      setPreviewItem(item)
      return
    }
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
        if (currentFolder) {
          formData.append('folder', currentFolder)
        }
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

  const handleDeleteMedia = async (itemId: string) => {
    await fetch(`/api/media/${encodeURIComponent(itemId)}`, { method: 'DELETE' })
    loadMediaCatalog()
  }

  const handleCreateFolder = async () => {
    const name = window.prompt('Nome da nova pasta:')
    if (!name?.trim()) return
    const nextPath = currentFolder ? `${currentFolder}/${name.trim()}` : name.trim()
    await fetch('/api/media/folders', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ path: nextPath }),
    })
    loadMediaCatalog()
  }

  const handleRenameMedia = async (item: MediaItem) => {
    const nextName = window.prompt('Novo nome do arquivo:', item.name)
    if (!nextName?.trim() || nextName.trim() === item.name) return
    await fetch('/api/media/rename', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ itemId: item.id, newName: nextName.trim() }),
    })
    loadMediaCatalog()
  }

  const handleRenameFolder = async (folderPath: string) => {
    const base = folderPath.split('/').pop() || folderPath
    const nextName = window.prompt('Novo nome da pasta:', base)
    if (!nextName?.trim() || nextName.trim() === base) return
    await fetch('/api/media/rename', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ folderPath, newName: nextName.trim() }),
    })
    loadMediaCatalog()
  }

  const handleDeleteFolder = async (folderPath: string) => {
    if (!window.confirm(`Excluir a pasta "${folderPath}" e todo o seu conteúdo?`)) return
    await fetch(`/api/media/folders?path=${encodeURIComponent(folderPath)}`, { method: 'DELETE' })
    if (currentFolder === folderPath || currentFolder.startsWith(`${folderPath}/`)) {
      setCurrentFolder('')
    }
    loadMediaCatalog()
  }

  const handleRescan = async () => {
    await fetch('/api/media/rescan', { method: 'POST' })
    loadMediaCatalog()
  }

  const handleOpenLocalFolder = async () => {
    await fetch('/api/media/open-folder', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ folder: currentFolder || undefined }),
    })
  }

  const acceptTypes =
    selectedType === 'AUDIO'
      ? 'audio/*'
      : selectedType === 'IMAGE'
        ? 'image/*'
        : selectedType === 'VIDEO'
          ? 'video/*'
          : 'image/*,video/*'

  const breadcrumbs = currentFolder ? currentFolder.split('/') : []

  return (
    <div
      onDragOver={(event) => event.preventDefault()}
      onDrop={(event) => {
        if (event.dataTransfer.files && event.dataTransfer.files.length > 0) {
          event.preventDefault()
          uploadFiles(event.dataTransfer.files)
        }
      }}
      className={`flex flex-col rounded-md bg-zinc-950 text-zinc-200 select-none overflow-hidden relative ${className}`}
    >
      {/* Barra de Filtro, Busca, Pastas e Upload */}
      <div className="flex flex-wrap items-center justify-between gap-1.5 border-b border-zinc-800/80 bg-zinc-950 p-2">
        {(filterType === 'ALL' || filterType === 'VISUAL') && (
          <div className="flex items-center gap-0.5 rounded-md bg-zinc-900 border border-zinc-800/80 p-0.5 text-xs">
            <button
              type="button"
              onClick={() => setSelectedType('VISUAL')}
              className={`rounded-sm px-2 py-0.5 text-[10px] font-medium transition ${
                selectedType === 'VISUAL' || selectedType === 'ALL'
                  ? 'bg-zinc-100 text-zinc-900'
                  : 'text-zinc-400 hover:text-zinc-200'
              }`}
            >
              Todos
            </button>
            <button
              type="button"
              onClick={() => setSelectedType('VIDEO')}
              className={`flex items-center gap-1 rounded-sm px-2 py-0.5 text-[10px] font-medium transition ${
                selectedType === 'VIDEO' ? 'bg-zinc-100 text-zinc-900' : 'text-zinc-400 hover:text-zinc-200'
              }`}
            >
              <Film className="h-3 w-3" strokeWidth={1.75} />
              <span>Vídeos</span>
            </button>
            <button
              type="button"
              onClick={() => setSelectedType('IMAGE')}
              className={`flex items-center gap-1 rounded-sm px-2 py-0.5 text-[10px] font-medium transition ${
                selectedType === 'IMAGE' ? 'bg-zinc-100 text-zinc-900' : 'text-zinc-400 hover:text-zinc-200'
              }`}
            >
              <ImageIcon className="h-3 w-3" strokeWidth={1.75} />
              <span>Imagens</span>
            </button>
          </div>
        )}

        <div className="relative flex-1 min-w-[90px]">
          <Search className="absolute left-2 top-1.5 h-3 w-3 text-zinc-400" />
          <input
            type="text"
            placeholder="Filtrar..."
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
            className="w-full rounded-md bg-zinc-900 border border-zinc-800 pl-6 pr-2 py-1 text-[11px] text-zinc-200 placeholder:text-zinc-500 focus:outline-none focus:border-zinc-600"
          />
        </div>

        <div className="flex items-center gap-1">
          <button
            type="button"
            onClick={handleCreateFolder}
            title="Nova Pasta"
            className="rounded-md bg-zinc-900 border border-zinc-800 p-1 text-zinc-400 hover:bg-zinc-800 hover:text-zinc-100 transition"
          >
            <FolderPlus className="h-3.5 w-3.5" strokeWidth={1.75} />
          </button>
          <button
            type="button"
            onClick={handleOpenLocalFolder}
            title="Abrir Pasta Local no Explorador do Computador"
            className="rounded-md bg-zinc-900 border border-zinc-800 p-1 text-zinc-400 hover:bg-zinc-800 hover:text-zinc-100 transition"
          >
            <FolderOpen className="h-3.5 w-3.5" strokeWidth={1.75} />
          </button>
          <button
            type="button"
            onClick={handleRescan}
            title="Atualizar / Reescanear Pasta"
            className="rounded-md bg-zinc-900 border border-zinc-800 p-1 text-zinc-400 hover:bg-zinc-800 hover:text-zinc-100 transition"
          >
            <RefreshCw className="h-3.5 w-3.5" strokeWidth={1.75} />
          </button>

          <input
            ref={fileInputRef}
            type="file"
            multiple
            accept={acceptTypes}
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
            title="Importar arquivos do computador"
            className="flex items-center gap-1.5 rounded-md bg-zinc-900 hover:bg-zinc-800 border border-zinc-800 px-2.5 py-1 text-[11px] font-medium text-zinc-200 transition shrink-0"
          >
            <Upload className="h-3 w-3 text-zinc-400" strokeWidth={1.75} />
            <span>{isUploading ? 'Enviando...' : 'Importar'}</span>
          </button>
        </div>
      </div>

      {/* Breadcrumb de Pastas */}
      {(currentFolder || immediateChildFolders.length > 0) && (
        <div className="flex items-center gap-1 border-b border-zinc-800/80 bg-zinc-900/50 px-2.5 py-1 text-[10px] text-zinc-400 overflow-x-auto">
          <button
            type="button"
            onClick={() => setCurrentFolder('')}
            className={`hover:text-zinc-400 transition ${!currentFolder ? 'text-zinc-400 font-semibold' : ''}`}
          >
            Raiz
          </button>
          {breadcrumbs.map((segment, index) => {
            const path = breadcrumbs.slice(0, index + 1).join('/')
            return (
              <span key={path} className="flex items-center gap-1 shrink-0">
                <ChevronRight className="h-2.5 w-2.5 text-zinc-600" />
                <button
                  type="button"
                  onClick={() => setCurrentFolder(path)}
                  className={`hover:text-zinc-400 transition ${
                    path === currentFolder ? 'text-zinc-400 font-semibold' : ''
                  }`}
                >
                  {segment}
                </button>
              </span>
            )
          })}
        </div>
      )}

      {/* Grade de Pastas e Itens de Mídia */}
      <div className="flex-1 overflow-y-auto p-2.5">
        {immediateChildFolders.length === 0 && filteredMedia.length === 0 ? (
          <div
            onClick={() => fileInputRef.current?.click()}
            className="flex flex-col items-center justify-center rounded-lg border border-dashed border-zinc-800 py-8 px-3 text-center cursor-pointer hover:border-zinc-700 transition"
          >
            <Upload className="h-6 w-6 text-zinc-500 mb-1.5" />
            <p className="text-xs font-medium text-zinc-300">Nenhuma mídia nesta pasta</p>
            <p className="text-[10px] text-zinc-500 mt-0.5">
              Arraste arquivos aqui ou clique para importar
            </p>
          </div>
        ) : (
          <div className="flex flex-col gap-2">
            {/* Pastas filhas (altura fixa compacta, independente dos cards de mídia) */}
            {!searchQuery && immediateChildFolders.length > 0 && (
              <div className="grid grid-cols-2 gap-1.5">
                {immediateChildFolders.map((folderPath) => {
                  const folderName = folderPath.split('/').pop() || folderPath
                  return (
                    <div
                      key={folderPath}
                      onClick={() => setCurrentFolder(folderPath)}
                      onContextMenu={(event) => {
                        event.preventDefault()
                        event.stopPropagation()
                        setContextMenu({ x: event.clientX, y: event.clientY, folderPath })
                      }}
                      className="flex h-9 items-center gap-2 rounded-md bg-zinc-900/70 hover:bg-zinc-800/80 px-2.5 cursor-pointer border border-zinc-800/80 transition"
                    >
                      <Folder className="h-3.5 w-3.5 text-zinc-400 shrink-0" strokeWidth={1.75} />
                      <span className="truncate text-xs font-medium text-zinc-200">{folderName}</span>
                    </div>
                  )
                })}
              </div>
            )}

            {/* Itens de Mídia */}
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
                    event.dataTransfer.effectAllowed = 'copyMove'
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
                  onDoubleClick={() => onProjectMedia?.(item)}
                  onContextMenu={(event) => {
                    event.preventDefault()
                    event.stopPropagation()
                    setContextMenu({ x: event.clientX, y: event.clientY, item })
                  }}
                  className={`group relative flex flex-col rounded-md cursor-pointer overflow-hidden border transition ${
                    isCurrentlyActive
                      ? 'border-zinc-200 ring-1 ring-zinc-200 bg-zinc-900'
                      : 'border-zinc-800/80 bg-zinc-900/50 hover:bg-zinc-900 hover:border-zinc-700'
                  }`}
                >
                  <div className="relative h-16 w-full bg-black flex items-center justify-center overflow-hidden">
                    <MediaThumb item={item} />

                    <span className="absolute left-1 top-1 rounded bg-zinc-950/85 px-1 py-0.5 text-[9px] font-mono text-zinc-300 border border-zinc-800/80">
                      {item.type === 'VIDEO' ? 'VÍDEO' : item.type === 'AUDIO' ? 'ÁUDIO' : 'IMG'}
                    </span>

                    <div className="opacity-0 group-hover:opacity-100 absolute right-1 top-1 flex items-center gap-1 transition">
                      <button
                        type="button"
                        onClick={(event) => {
                          event.stopPropagation()
                          setPreviewItem(item)
                        }}
                        title="Visualizar Preview Ampliado"
                        className="rounded bg-black/75 p-1 text-zinc-200 hover:text-white transition"
                      >
                        <Eye className="h-2.5 w-2.5" />
                      </button>
                      {!isCurrentlyActive && (
                        <button
                          type="button"
                          onClick={(event) => {
                            event.stopPropagation()
                            handleDeleteMedia(item.id)
                          }}
                          title="Remover mídia"
                          className="rounded bg-black/75 p-1 text-red-400 hover:text-red-300 transition"
                        >
                          <Trash2 className="h-2.5 w-2.5" />
                        </button>
                      )}
                    </div>

                    {isCurrentlyActive && (
                      <span
                        data-testid="live-indicator"
                        aria-label="Ao vivo"
                        title="Fundo ao vivo no telão"
                        className="absolute right-1 bottom-1 inline-flex items-center justify-center rounded-full bg-zinc-950/85 p-1 text-emerald-400 border border-emerald-500/30"
                      >
                        <Radio className="h-2.5 w-2.5 animate-pulse" strokeWidth={2} />
                      </span>
                    )}
                  </div>

                  <div className="p-1.5">
                    <div className="truncate text-[11px] font-medium text-zinc-200">{item.name}</div>
                  </div>
                </div>
              )
            })}
            </div>
          </div>
        )}
      </div>

      {/* Menu de Contexto (Botão Direito) */}
      {contextMenu && (
        <div
          style={{ left: contextMenu.x, top: contextMenu.y }}
          className="fixed z-50 w-48 rounded-lg border border-zinc-800 bg-zinc-950 py-1 text-xs shadow-2xl"
        >
          {contextMenu.item && (
            <>
              {onProjectMedia && (
                <button
                  type="button"
                  onClick={() => {
                    const target = contextMenu.item!
                    setContextMenu(null)
                    onProjectMedia(target)
                  }}
                  className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-zinc-200 hover:bg-zinc-800"
                >
                  <Presentation className="h-3.5 w-3.5 text-emerald-400" />
                  <span>Projetar em Tela Cheia</span>
                </button>
              )}
              <button
                type="button"
                onClick={() => {
                  const target = contextMenu.item!
                  setContextMenu(null)
                  handleApplyBackground(target)
                }}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-zinc-200 hover:bg-zinc-900"
              >
                <ImageIcon className="h-3.5 w-3.5 text-zinc-400" />
                <span>Definir como Fundo de Louvor</span>
              </button>
              <button
                type="button"
                onClick={() => {
                  const target = contextMenu.item!
                  setContextMenu(null)
                  setPreviewItem(target)
                }}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-zinc-200 hover:bg-zinc-900"
              >
                <Eye className="h-3.5 w-3.5 text-zinc-400" />
                <span>Visualizar Preview</span>
              </button>
              <button
                type="button"
                onClick={() => handleRenameMedia(contextMenu.item!)}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-zinc-200 hover:bg-zinc-900"
              >
                <Pencil className="h-3.5 w-3.5 text-amber-400" />
                <span>Renomear</span>
              </button>
              <button
                type="button"
                onClick={() => handleDeleteMedia(contextMenu.item!.id)}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-rose-300 hover:bg-rose-500/10"
              >
                <Trash2 className="h-3.5 w-3.5 text-rose-400" />
                <span>Excluir</span>
              </button>
            </>
          )}
          {contextMenu.folderPath && (
            <>
              <button
                type="button"
                onClick={() => handleRenameFolder(contextMenu.folderPath!)}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-zinc-200 hover:bg-zinc-900"
              >
                <Pencil className="h-3.5 w-3.5 text-amber-400" />
                <span>Renomear Pasta</span>
              </button>
              <button
                type="button"
                onClick={() => handleDeleteFolder(contextMenu.folderPath!)}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-rose-300 hover:bg-rose-500/10"
              >
                <Trash2 className="h-3.5 w-3.5 text-rose-400" />
                <span>Excluir Pasta</span>
              </button>
            </>
          )}
        </div>
      )}

      {/* Modal Lightbox de Preview Ampliado */}
      {previewItem && (
        <div
          onClick={() => setPreviewItem(null)}
          className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 p-6 backdrop-blur-xs"
        >
          <div
            onClick={(event) => event.stopPropagation()}
            className="flex w-full max-w-3xl flex-col rounded-xl border border-zinc-800 bg-zinc-950 overflow-hidden shadow-2xl"
          >
            <div className="flex items-center justify-between border-b border-zinc-800 px-4 py-3">
              <span className="text-xs font-semibold text-white truncate">{previewItem.name}</span>
              <button
                type="button"
                onClick={() => setPreviewItem(null)}
                className="rounded p-1 text-zinc-400 hover:bg-white/10 hover:text-white"
              >
                <X className="h-4 w-4" />
              </button>
            </div>
            <div className="flex items-center justify-center bg-black p-4 min-h-[300px] max-h-[65vh]">
              {previewItem.type === 'IMAGE' ? (
                <img
                  src={`/api/media/stream/${encodeURIComponent(previewItem.id)}`}
                  alt={previewItem.name}
                  className="max-h-[60vh] max-w-full object-contain"
                />
              ) : previewItem.type === 'VIDEO' ? (
                <video
                  src={`/api/media/stream/${encodeURIComponent(previewItem.id)}`}
                  controls
                  autoPlay
                  className="max-h-[60vh] max-w-full object-contain"
                />
              ) : (
                <audio
                  src={`/api/media/stream/${encodeURIComponent(previewItem.id)}`}
                  controls
                  autoPlay
                  className="w-full max-w-md"
                />
              )}
            </div>
            {previewItem.type !== 'AUDIO' && (
              <div className="flex justify-end gap-2 border-t border-zinc-800 px-4 py-3 bg-zinc-950">
                <button
                  type="button"
                  onClick={() => {
                    handleApplyBackground(previewItem)
                    setPreviewItem(null)
                  }}
                  className="rounded-md bg-sky-500 px-3 py-1.5 text-xs font-semibold text-zinc-950 hover:bg-sky-400 transition"
                >
                  Projetar no Telão
                </button>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  )
})
