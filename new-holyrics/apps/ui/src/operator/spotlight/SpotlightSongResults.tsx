/**
 * @module @ui/operator/spotlight/SpotlightSongResults
 * @description Grupo de louvores e canções para a Command Palette.
 */

import { Music } from 'lucide-react'
import { Command } from 'cmdk'
import type { SongSearchResult } from '@slices/songs/contracts/song-contracts'

export interface SpotlightSongResultsProps {
  songs?: SongSearchResult[]
  onSelectSong: (songId: string) => void
}

/**
 * Renderiza o grupo de músicas encontradas dentro do cmdk.
 *
 * @param props - Propriedades com a lista de canções.
 * @param props.songs - Lista de resultados de músicas.
 * @param props.onSelectSong - Callback acionado ao selecionar música.
 * @returns Elemento JSX com a lista de músicas.
 */
export function SpotlightSongResults({
  songs = [],
  onSelectSong,
}: SpotlightSongResultsProps) {
  if (songs.length === 0) return null

  return (
    <Command.Group heading="Acervo de Louvores">
      {songs.map((song) => (
        <Command.Item
          key={song.id}
          value={`song_${song.id}`}
          onSelect={() => onSelectSong(song.id)}
          className="flex items-center justify-between rounded-lg px-3 py-2 text-xs cursor-pointer transition data-[selected=true]:bg-sky-500/20 data-[selected=true]:text-white text-slate-300"
        >
          <div className="flex items-center gap-2.5">
            <Music className="h-4 w-4 text-sky-400 shrink-0" />
            <div>
              <p className="font-medium text-slate-100">{song.title}</p>
              {song.artist && <p className="text-[11px] text-slate-400">{song.artist}</p>}
            </div>
          </div>
          {song.snippet && (
            <span
              className="text-[11px] text-slate-400 italic max-w-xs truncate"
              dangerouslySetInnerHTML={{ __html: song.snippet }}
            />
          )}
        </Command.Item>
      ))}
    </Command.Group>
  )
}
