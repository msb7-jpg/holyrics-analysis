/**
 * @module @slices/media/ui/components/MediaGridPanel
 * @description Catálogo visual de mídias, loops de vídeo e imagens de projeção no padrão Slate Pro Studio (RF-27, RF-29).
 *
 * ## Responsabilidades:
 * - Listar mídias disponíveis com filtros por tipo (Vídeos em Loop, Imagens de Fundo) e busca textual.
 * - Permitir aplicar uma mídia diretamente como fundo do telão (BackgroundLayer) com 1 clique.
 * - Indicar visualmente qual mídia está atualmente projetada.
 */

import { useState, useEffect, useMemo } from 'react'
import { Film, Image as ImageIcon, Search, Check, Play } from 'lucide-react'
import type { MediaItem, MediaType } from '../../contracts/media-contracts'
import { DEFAULT_MEDIA_ITEMS } from '../../contracts/media-contracts'
import type { BackgroundLayer } from '@lumen/core'

export interface MediaGridPanelProps {
  activeBackground?: BackgroundLayer
  onSelectBackground: (background: BackgroundLayer) => void
  className?: string
}

/**
 * Painel em grade para seleção e aplicação de mídias no telão.
 *
 * @param props - Propriedades do componente.
 * @param props.activeBackground - Camada de fundo atualmente projetada na FSM.
 * @param props.onSelectBackground - Callback disparado ao selecionar um fundo.
 * @param props.className - Classes CSS adicionais.
 * @returns Elemento JSX da grade de mídias.
 */
export function MediaGridPanel({
  activeBackground,
  onSelectBackground,
  className = '',
}: MediaGridPanelProps) {
  const [mediaList, setMediaList] = useState<MediaItem[]>(DEFAULT_MEDIA_ITEMS)
  const [selectedType, setSelectedType] = useState<MediaType | 'ALL'>('ALL')
  const [searchQuery, setSearchQuery] = useState('')

  // Carrega catálogo do daemon
  useEffect(() => {
    fetch('/api/media')
      .then((res) => res.json())
      .then((json: { ok: boolean; data?: MediaItem[] }) => {
        if (json.ok && Array.isArray(json.data) && json.data.length > 0) {
          setMediaList(json.data)
        }
      })
      .catch(() => {
        // Fallback limpo com os itens padrão
      })
  }, [])

  const filteredMedia = useMemo(() => {
    return mediaList.filter((item) => {
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
    onSelectBackground({
      type: isVideo ? 'video' : 'image',
      source: item.path,
      loop: isVideo,
      muted: true,
    })
  }

  return (
    <div className={`flex flex-col rounded-lg border border-white/10 bg-slate-900/90 text-slate-200 select-none overflow-hidden ${className}`}>
      {/* Barra de Filtro e Busca */}
      <div className="flex flex-wrap items-center justify-between gap-2 border-b border-white/5 bg-slate-950/60 p-2.5">
        <div className="flex items-center gap-1 rounded bg-slate-800 p-0.5 text-xs">
          <button
            type="button"
            onClick={() => setSelectedType('ALL')}
            className={`rounded px-2 py-0.5 text-[11px] transition ${
              selectedType === 'ALL' ? 'bg-sky-500 text-slate-950 font-medium' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            Todos
          </button>
          <button
            type="button"
            onClick={() => setSelectedType('VIDEO')}
            className={`flex items-center gap-1 rounded px-2 py-0.5 text-[11px] transition ${
              selectedType === 'VIDEO' ? 'bg-sky-500 text-slate-950 font-medium' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <Film className="h-3 w-3" />
            <span>Loops</span>
          </button>
          <button
            type="button"
            onClick={() => setSelectedType('IMAGE')}
            className={`flex items-center gap-1 rounded px-2 py-0.5 text-[11px] transition ${
              selectedType === 'IMAGE' ? 'bg-sky-500 text-slate-950 font-medium' : 'text-slate-400 hover:text-slate-200'
            }`}
          >
            <ImageIcon className="h-3 w-3" />
            <span>Imagens</span>
          </button>
        </div>

        <div className="relative min-w-[140px] flex-1 max-w-[200px]">
          <Search className="absolute left-2 top-2 h-3 w-3 text-slate-400" />
          <input
            type="text"
            placeholder="Buscar mídias..."
            value={searchQuery}
            onChange={(event) => setSearchQuery(event.target.value)}
            className="w-full rounded border border-white/10 bg-slate-800/80 pl-7 pr-2 py-1 text-xs text-slate-200 placeholder:text-slate-500 focus:border-sky-500 focus:outline-none"
          />
        </div>
      </div>

      {/* Grade de Itens de Mídia */}
      <div className="flex-1 overflow-y-auto p-2.5 max-h-56">
        <div className="grid grid-cols-2 gap-2">
          {filteredMedia.map((item) => {
            const isCurrentlyActive = activeBackground?.source === item.path
            return (
              <div
                key={item.id}
                onClick={() => handleApplyBackground(item)}
                className={`group relative flex flex-col rounded border cursor-pointer overflow-hidden transition ${
                  isCurrentlyActive
                    ? 'border-sky-500 ring-1 ring-sky-500 bg-sky-950/20'
                    : 'border-white/5 bg-slate-950/50 hover:border-white/20'
                }`}
              >
                {/* Miniatura Placeholder Slate Pro */}
                <div className="relative h-16 w-full bg-slate-800 flex items-center justify-center overflow-hidden">
                  {item.type === 'VIDEO' ? (
                    <Film className="h-6 w-6 text-slate-500 group-hover:text-sky-400 transition" />
                  ) : (
                    <ImageIcon className="h-6 w-6 text-slate-500 group-hover:text-sky-400 transition" />
                  )}

                  {/* Badge de Tipo */}
                  <span className="absolute left-1 top-1 rounded bg-slate-900/80 px-1 py-0.5 text-[9px] font-mono text-slate-400">
                    {item.type === 'VIDEO' ? 'LOOP' : 'IMG'}
                  </span>

                  {/* Indicador de No Ar */}
                  {isCurrentlyActive && (
                    <span className="absolute right-1 top-1 flex items-center gap-0.5 rounded bg-sky-500 px-1.5 py-0.5 text-[9px] font-bold text-slate-950 shadow">
                      <Check className="h-2.5 w-2.5 stroke-[3]" />
                      <span>NO AR</span>
                    </span>
                  )}

                  <div className="absolute inset-0 bg-sky-500/10 opacity-0 group-hover:opacity-100 flex items-center justify-center transition">
                    <Play className="h-5 w-5 text-white fill-current opacity-80" />
                  </div>
                </div>

                {/* Metadados */}
                <div className="p-1.5">
                  <div className="truncate text-[11px] font-medium text-slate-200" title={item.name}>
                    {item.name}
                  </div>
                  <div className="flex items-center justify-between text-[9px] text-slate-400 mt-0.5 font-mono">
                    <span>{item.resolution || '1080p'}</span>
                    {item.durationSeconds && <span>{item.durationSeconds}s</span>}
                  </div>
                </div>
              </div>
            )
          })}
        </div>
      </div>
    </div>
  )
}
