/**
 * @module @slices/pptx/ui/components/PptxFilesPanel
 * @description Painel padronizado de Apresentações e PDFs com busca, pastas, menu de contexto e abertura de pasta local.
 */

import { useState, useEffect, useMemo, useRef } from 'react'
import {
  Search,
  FolderPlus,
  FolderOpen,
  RefreshCw,
  Upload,
  Folder,
  Presentation,
  Trash2,
  Pencil,
  Plus,
  ChevronRight,
} from 'lucide-react'
import type { PptxPresentation } from '../../contracts/pptx-contracts'
import { pptxService } from '../services/pptx-service'
import { renderPdfFileToPresentation } from '../utils/pdf-renderer'

export interface PptxFilesPanelProps {
  onSelectDeck?: (deck: PptxPresentation) => void
  onAddToLiturgy?: (deck: PptxPresentation) => void
}

const FOLDERS_STORAGE_KEY = 'lumen-pptx-folders'

/**
 * Painel de arquivos (.pptx / .pdf) padronizado com o mesmo layout de MediaGridPanel.
 *
 * @param props - Propriedades do painel.
 * @returns Elemento JSX do painel de arquivos.
 */
export function PptxFilesPanel({ onSelectDeck, onAddToLiturgy }: PptxFilesPanelProps) {
  const [decks, setDecks] = useState<PptxPresentation[]>([])
  const [folders, setFolders] = useState<string[]>(() => {
    try {
      const saved = window.localStorage.getItem(FOLDERS_STORAGE_KEY)
      return saved ? (JSON.parse(saved) as string[]) : []
    } catch {
      return []
    }
  })
  const [currentFolder, setCurrentFolder] = useState<string>('')
  const [searchQuery, setSearchQuery] = useState('')
  const [isUploading, setIsUploading] = useState(false)
  const [contextMenu, setContextMenu] = useState<{
    x: number
    y: number
    deck?: PptxPresentation
    folderPath?: string
  } | null>(null)
  const fileInputRef = useRef<HTMLInputElement | null>(null)

  const loadDecks = () => {
    pptxService
      .listAll()
      .then(setDecks)
      .catch(() => setDecks([]))
  }

  useEffect(() => {
    loadDecks()
  }, [])

  useEffect(() => {
    try {
      window.localStorage.setItem(FOLDERS_STORAGE_KEY, JSON.stringify(folders))
    } catch {
      // Ignora erro de quota
    }
  }, [folders])

  useEffect(() => {
    const close = () => setContextMenu(null)
    window.addEventListener('click', close)
    return () => window.removeEventListener('click', close)
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

  const filteredDecks = useMemo(() => {
    return decks.filter((deck) => {
      const deckFolder = deck.folder || ''
      if (!searchQuery && deckFolder !== currentFolder) return false
      if (searchQuery) {
        return (
          deck.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
          deck.filename.toLowerCase().includes(searchQuery.toLowerCase())
        )
      }
      return true
    })
  }, [decks, currentFolder, searchQuery])

  const handleUploadFiles = async (files: FileList | File[]) => {
    setIsUploading(true)
    try {
      for (const file of Array.from(files)) {
        let created: PptxPresentation
        if (file.name.toLowerCase().endsWith('.pdf')) {
          try {
            const rendered = await renderPdfFileToPresentation(file, currentFolder || undefined)
            created = await pptxService.ingestPresentation(rendered)
          } catch {
            const arrayBuf = await file.arrayBuffer()
            const bytes = new Uint8Array(arrayBuf)
            let binaryStr = ''
            for (let index = 0; index < bytes.length; index++) {
              binaryStr += String.fromCharCode(bytes[index])
            }
            created = await pptxService.ingestFile(file.name, btoa(binaryStr))
          }
        } else {
          const arrayBuf = await file.arrayBuffer()
          const bytes = new Uint8Array(arrayBuf)
          let binaryStr = ''
          for (let index = 0; index < bytes.length; index++) {
            binaryStr += String.fromCharCode(bytes[index])
          }
          created = await pptxService.ingestFile(file.name, btoa(binaryStr))
        }
        if (currentFolder && !created.folder) {
          created.folder = currentFolder
          await pptxService.ingestPresentation(created)
        }
        setDecks((prev) => [created, ...prev.filter((d) => d.id !== created.id)])
      }
    } finally {
      setIsUploading(false)
    }
  }

  const handleCreateFolder = () => {
    const name = window.prompt('Nome da nova pasta:')
    if (!name?.trim()) return
    const nextPath = currentFolder ? `${currentFolder}/${name.trim()}` : name.trim()
    setFolders((prev) => (prev.includes(nextPath) ? prev : [...prev, nextPath]))
  }

  const handleRenameDeck = async (deck: PptxPresentation) => {
    const nextTitle = window.prompt('Novo nome da apresentação:', deck.title)
    if (!nextTitle?.trim() || nextTitle.trim() === deck.title) return
    await fetch('/api/pptx/rename', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ id: deck.id, newTitle: nextTitle.trim() }),
    })
    loadDecks()
  }

  const handleDeleteDeck = async (deckId: string) => {
    const remaining = await pptxService.deletePresentation(deckId)
    setDecks(remaining)
  }

  const handleDeleteFolder = (folderPath: string) => {
    if (!window.confirm(`Remover a pasta "${folderPath}"?`)) return
    setFolders((prev) => prev.filter((f) => f !== folderPath && !f.startsWith(`${folderPath}/`)))
    if (currentFolder === folderPath || currentFolder.startsWith(`${folderPath}/`)) {
      setCurrentFolder('')
    }
  }

  const handleOpenLocalFolder = async () => {
    await fetch('/api/pptx/open-folder', { method: 'POST' })
  }

  const breadcrumbs = currentFolder ? currentFolder.split('/') : []

  return (
    <div
      onDragOver={(event) => event.preventDefault()}
      onDrop={(event) => {
        if (event.dataTransfer.files && event.dataTransfer.files.length > 0) {
          event.preventDefault()
          void handleUploadFiles(event.dataTransfer.files)
        }
      }}
      className="flex flex-1 flex-col rounded-md bg-[#131C2B] text-slate-200 select-none overflow-hidden relative"
    >
      {/* Barra Padronizada: Busca, Pastas, Abrir Local, Reescanear, Importar */}
      <div className="flex flex-wrap items-center justify-between gap-1.5 border-b border-white/5 bg-[#090D14]/50 p-2">
        <div className="relative flex-1 min-w-[90px]">
          <Search className="absolute left-2 top-1.5 h-3 w-3 text-slate-400" />
          <input
            type="text"
            placeholder="Filtrar..."
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
            className="w-full rounded bg-[#192334] pl-6 pr-2 py-1 text-[11px] text-slate-200 placeholder:text-slate-500 focus:outline-none focus:ring-1 focus:ring-sky-500"
          />
        </div>

        <div className="flex items-center gap-1">
          <button
            type="button"
            onClick={handleCreateFolder}
            title="Nova Pasta"
            className="rounded bg-[#192334] p-1 text-slate-300 hover:bg-[#1E2B40] hover:text-sky-400 transition"
          >
            <FolderPlus className="h-3.5 w-3.5" />
          </button>
          <button
            type="button"
            onClick={handleOpenLocalFolder}
            title="Abrir Pasta Local no Explorador do Computador"
            className="rounded bg-[#192334] p-1 text-slate-300 hover:bg-[#1E2B40] hover:text-amber-400 transition"
          >
            <FolderOpen className="h-3.5 w-3.5" />
          </button>
          <button
            type="button"
            onClick={loadDecks}
            title="Atualizar Lista"
            className="rounded bg-[#192334] p-1 text-slate-300 hover:bg-[#1E2B40] hover:text-emerald-400 transition"
          >
            <RefreshCw className="h-3.5 w-3.5" />
          </button>

          <input
            ref={fileInputRef}
            type="file"
            accept=".pptx,.pdf"
            multiple
            className="hidden"
            onChange={(event) => {
              if (event.target.files && event.target.files.length > 0) {
                void handleUploadFiles(event.target.files)
                event.target.value = ''
              }
            }}
          />
          <button
            type="button"
            disabled={isUploading}
            onClick={() => fileInputRef.current?.click()}
            title="Importar arquivos .pptx ou .pdf"
            className="flex items-center gap-1 rounded bg-sky-500/15 hover:bg-sky-500/25 border border-sky-500/30 px-2 py-1 text-[10px] font-medium text-sky-300 transition shrink-0"
          >
            <Upload className="h-3 w-3" />
            <span>{isUploading ? 'Enviando...' : 'Importar'}</span>
          </button>
        </div>
      </div>

      {/* Breadcrumb de Pastas */}
      {(currentFolder || immediateChildFolders.length > 0) && (
        <div className="flex items-center gap-1 border-b border-white/5 bg-[#0F1622]/70 px-2.5 py-1 text-[10px] text-slate-400 overflow-x-auto">
          <button
            type="button"
            onClick={() => setCurrentFolder('')}
            className={`hover:text-sky-400 transition ${!currentFolder ? 'text-sky-400 font-semibold' : ''}`}
          >
            Raiz
          </button>
          {breadcrumbs.map((segment, index) => {
            const path = breadcrumbs.slice(0, index + 1).join('/')
            return (
              <span key={path} className="flex items-center gap-1 shrink-0">
                <ChevronRight className="h-2.5 w-2.5 text-slate-600" />
                <button
                  type="button"
                  onClick={() => setCurrentFolder(path)}
                  className={`hover:text-sky-400 transition ${
                    path === currentFolder ? 'text-sky-400 font-semibold' : ''
                  }`}
                >
                  {segment}
                </button>
              </span>
            )
          })}
        </div>
      )}

      {/* Lista de Pastas e Apresentações */}
      <div className="flex-1 overflow-y-auto p-2.5">
        {immediateChildFolders.length === 0 && filteredDecks.length === 0 ? (
          <div
            onClick={() => fileInputRef.current?.click()}
            className="flex flex-col items-center justify-center rounded-lg border border-dashed border-white/10 py-8 px-3 text-center cursor-pointer hover:border-sky-500/40 transition"
          >
            <Presentation className="h-6 w-6 text-slate-500 mb-1.5" />
            <p className="text-xs font-medium text-slate-300">Nenhum arquivo nesta pasta</p>
            <p className="text-[10px] text-slate-500 mt-0.5">
              Arraste arquivos .pptx ou .pdf aqui ou clique para importar
            </p>
          </div>
        ) : (
          <div className="flex flex-col gap-1.5">
            {!searchQuery &&
              immediateChildFolders.map((folderPath) => {
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
                    className="flex items-center gap-2 rounded-[6px] bg-[#192334]/80 hover:bg-[#1E2B40] p-2 cursor-pointer border border-white/5 transition"
                  >
                    <Folder className="h-4 w-4 text-amber-400 shrink-0" />
                    <span className="truncate text-[11px] font-medium text-slate-200">{folderName}</span>
                  </div>
                )
              })}

            {filteredDecks.map((deck) => (
              <div
                key={deck.id}
                draggable
                onDragStart={(event) => {
                  event.dataTransfer.effectAllowed = 'copyMove'
                  event.dataTransfer.setData(
                    'application/lumen-item',
                    JSON.stringify({
                      type: 'PPTX',
                      title: deck.title,
                      fileSrc: deck.id,
                      slidesCount: deck.slides.length,
                    }),
                  )
                }}
                onClick={() => onSelectDeck?.(deck)}
                onContextMenu={(event) => {
                  event.preventDefault()
                  event.stopPropagation()
                  setContextMenu({ x: event.clientX, y: event.clientY, deck })
                }}
                className="group flex items-center justify-between rounded-[6px] bg-[#192334] hover:bg-[#1E2B40] p-2.5 cursor-pointer transition"
              >
                <div className="min-w-0 pr-2">
                  <p className="text-xs font-medium text-slate-100 truncate">{deck.title}</p>
                  <p className="text-[10px] text-slate-400">
                    {deck.slides.length} slides • {deck.format}
                  </p>
                </div>
                <div className="flex items-center gap-1 shrink-0">
                  <button
                    type="button"
                    onClick={(event) => {
                      event.stopPropagation()
                      onAddToLiturgy?.(deck)
                    }}
                    title="Adicionar ao Roteiro"
                    className="rounded bg-sky-500/15 hover:bg-sky-500/30 px-1.5 py-0.5 text-[10px] text-sky-300 transition"
                  >
                    + Roteiro
                  </button>
                  <button
                    type="button"
                    onClick={(event) => {
                      event.stopPropagation()
                      void handleDeleteDeck(deck.id)
                    }}
                    title="Excluir apresentação"
                    className="rounded p-1 text-slate-500 hover:text-rose-400 transition"
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                  </button>
                </div>
              </div>
            ))}
          </div>
        )}
      </div>

      {/* Menu de Contexto (Botão Direito) */}
      {contextMenu && (
        <div
          style={{ left: contextMenu.x, top: contextMenu.y }}
          className="fixed z-50 w-44 rounded-lg border border-white/10 bg-[#0F1622] py-1 text-xs shadow-2xl"
        >
          {contextMenu.deck && (
            <>
              <button
                type="button"
                onClick={() => onAddToLiturgy?.(contextMenu.deck!)}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-slate-200 hover:bg-[#192334]"
              >
                <Plus className="h-3.5 w-3.5 text-sky-400" />
                <span>Adicionar ao Roteiro</span>
              </button>
              <button
                type="button"
                onClick={() => void handleRenameDeck(contextMenu.deck!)}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-slate-200 hover:bg-[#192334]"
              >
                <Pencil className="h-3.5 w-3.5 text-amber-400" />
                <span>Renomear</span>
              </button>
              <button
                type="button"
                onClick={() => void handleDeleteDeck(contextMenu.deck!.id)}
                className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-rose-300 hover:bg-rose-500/10"
              >
                <Trash2 className="h-3.5 w-3.5 text-rose-400" />
                <span>Excluir</span>
              </button>
            </>
          )}
          {contextMenu.folderPath && (
            <button
              type="button"
              onClick={() => handleDeleteFolder(contextMenu.folderPath!)}
              className="flex w-full items-center gap-2 px-3 py-1.5 text-left text-rose-300 hover:bg-rose-500/10"
            >
              <Trash2 className="h-3.5 w-3.5 text-rose-400" />
              <span>Excluir Pasta</span>
            </button>
          )}
        </div>
      )}
    </div>
  )
}
