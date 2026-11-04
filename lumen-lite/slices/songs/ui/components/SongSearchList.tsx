/**
 * @module @slices/songs/ui/components/SongSearchColumn
 * @description Coluna de busca textual instantânea FTS5 no acervo de músicas.
 */

import { Music, Loader2 } from 'lucide-react'
import type { SongSearchResult } from '../../contracts/song-contracts'

export interface SongSearchColumnProps {
  songs: SongSearchResult[]
  isLoading: boolean
  selectedSongId?: string | null
  onSelectSong: (songId: string) => void
}

/**
 * Renderiza a lista de músicas encontradas na busca FTS5.
 *
 * @param props - Propriedades do componente.
 * @returns Elemento JSX com os itens clicáveis.
 */
export function SongSearchList({
  songs,
  isLoading,
  selectedSongId,
  onSelectSong,
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
    <div className="flex flex-col gap-1">
      {songs.map((song) => {
        const isSelected = selectedSongId === song.id
        return (
          <div
            key={song.id}
            onClick={() => onSelectSong(song.id)}
            className={`flex flex-col gap-0.5 rounded-lg p-2.5 cursor-pointer transition select-none ${
              isSelected
                ? 'bg-sky-500/20 border border-sky-500/40 text-white'
                : 'hover:bg-slate-800/80 text-slate-300 border border-transparent'
            }`}
          >
            <div className="flex items-center gap-2">
              <Music className="h-3.5 w-3.5 text-sky-400 shrink-0" />
              <span className="font-medium text-xs truncate">{song.title}</span>
            </div>
            {song.artist && (
              <span className="text-[11px] text-slate-400 pl-5.5 truncate">
                {song.artist}
              </span>
            )}
            {song.snippet && (
              <span
                className="text-[10px] text-slate-500 pl-5.5 italic truncate"
                dangerouslySetInnerHTML={{ __html: song.snippet }}
              />
            )}
          </div>
        )
      })}
    </div>
  )
}
