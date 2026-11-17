/**
 * @module @slices/songs/ui/components/SongSearchList
 * @description Lista de músicas encontradas na busca FTS5 com suporte a Drag & Drop para o Roteiro e semântica Preview (Azul) vs Ao Vivo (Esmeralda).
 */

import { memo } from 'react'
import { Loader2 } from 'lucide-react'
import { LiveIndicator } from '@ui/components/ui/live-indicator'
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
        <Loader2 className="h-5 w-5 animate-spin text-zinc-400" />
      </div>
    )
  }

  if (songs.length === 0) {
    return (
      <div className="py-8 text-center text-xs text-zinc-500">
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
                ? 'bg-zinc-900 text-zinc-100'
                : isSelected
                  ? 'bg-zinc-800/70 text-zinc-50'
                  : 'text-zinc-300 hover:bg-zinc-900/60'
            }`}
          >
            <div className="flex items-center justify-between gap-2">
              <span className="font-medium text-xs truncate">{song.title}</span>
              {isLive && <LiveIndicator variant="dot" />}
            </div>
            {song.artist && (
              <span className="text-[11px] text-zinc-400 truncate">
                {song.artist}
              </span>
            )}
            {song.snippet && (
              <span
                className="text-[10px] text-zinc-500 italic truncate"
                dangerouslySetInnerHTML={{ __html: song.snippet }}
              />
            )}
          </div>
        )
      })}
    </div>
  )
})
