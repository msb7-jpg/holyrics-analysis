/**
 * @module @slices/songs/ui/components/SongSearchList
 * @description Lista de músicas encontradas na busca FTS5 com suporte a Drag & Drop para o Roteiro e semântica Preview (Azul) vs Ao Vivo (Esmeralda).
 */

import { memo } from 'react'
import { Loader2 } from 'lucide-react'
import type { SongSearchResult } from '../../contracts/song-contracts'

export interface SongSearchColumnProps {
  songs: SongSearchResult[]
  isLoading: boolean
  selectedSongId?: string | null
  liveSongId?: string | null
  onSelectSong: (songId: string) => void
  onProjectSong?: (songId: string) => void
}

/**
 * Renderiza a lista de músicas do catálogo com suporte a arraste para o roteiro.
 *
 * @param props - Propriedades do componente.
 * @returns Elemento JSX com os itens clicáveis e arrastáveis.
 */
export const SongSearchList = memo(function SongSearchList({
  songs,
  isLoading,
  selectedSongId,
  liveSongId,
  onSelectSong,
  onProjectSong,
}: SongSearchColumnProps) {
  if (isLoading && songs.length === 0) {
    return (
      <div className="flex flex-1 items-center justify-center py-10">
        <Loader2 className="h-5 w-5 animate-spin text-sky-400" />
      </div>
    )
  }

  if (songs.length === 0) {
    return (
      <div className="py-8 text-center text-xs text-slate-500">
        Nenhuma música encontrada
      </div>
    )
  }

  return (
    <div className="flex flex-col gap-1.5">
      {songs.map((song) => {
        const isSelected = selectedSongId === song.id
        const isLive = liveSongId === song.id
        return (
          <div
            key={song.id}
            draggable
            onDragStart={(event) => {
              event.dataTransfer.effectAllowed = 'copyMove'
              event.dataTransfer.setData(
                'application/lumen-item',
                JSON.stringify({
                  type: 'SONG',
                  title: song.title,
                  songId: song.id,
                  artist: song.artist,
                }),
              )
            }}
            onClick={() => onSelectSong(song.id)}
            onDoubleClick={() => onProjectSong?.(song.id)}
            className={`relative flex flex-col gap-0.5 rounded-lg p-2.5 cursor-pointer transition select-none ${
              isLive
                ? 'bg-emerald-950/20 text-white ring-1 ring-emerald-500/40'
                : isSelected
                  ? 'bg-[#181C24] text-white ring-1 ring-sky-500/30'
                  : 'text-slate-300 hover:bg-[#181C24]'
            }`}
          >
            {isLive && (
              <div className="absolute left-0 top-0 bottom-0 w-[3px] rounded-l-lg bg-emerald-500" />
            )}
            {!isLive && isSelected && (
              <div className="absolute left-0 top-0 bottom-0 w-[3px] rounded-l-lg bg-sky-500" />
            )}

            <div className="flex items-center justify-between gap-2">
              <span className="font-medium text-xs truncate">{song.title}</span>
              {isLive && (
                <span
                  className="h-2 w-2 rounded-full bg-emerald-400 shadow-[0_0_8px_rgba(16,185,129,0.7)] shrink-0"
                  title="No ar no telão"
                />
              )}
            </div>
            {song.artist && (
              <span className="text-[11px] text-slate-400 truncate">
                {song.artist}
              </span>
            )}
            {song.snippet && (
              <span
                className="text-[10px] text-slate-500 italic truncate"
                dangerouslySetInnerHTML={{ __html: song.snippet }}
              />
            )}
          </div>
        )
      })}
    </div>
  )
})
