/**
 * @module @slices/songs/ui/__tests__/song-search-list.test
 * @description Testes unitários para SongSearchList com duplo clique para projetar no telão.
 */

import { describe, expect, it, mock } from 'bun:test'
import { renderToString } from 'react-dom/server'
import type { SongSearchResult } from '../../contracts/song-contracts'
import { SongSearchList } from '../components/SongSearchList'

const mockSongs: SongSearchResult[] = [
  {
    id: 'song-1',
    title: 'Porque Ele Vive',
    artist: 'Harpa Cristã',
    snippet: 'Deus enviou seu filho amado',
  },
  {
    id: 'song-2',
    title: 'Grandioso És Tu',
    artist: 'Harpa Cristã',
  },
]

describe('SongSearchList', () => {
  it('deve renderizar a lista de músicas', () => {
    const onSelect = mock(() => {})
    const html = renderToString(
      <SongSearchList
        songs={mockSongs}
        isLoading={false}
        onSelectSong={onSelect}
      />
    )

    expect(html).toContain('Porque Ele Vive')
    expect(html).toContain('Harpa Cristã')
    expect(html).toContain('Grandioso És Tu')
  })

  it('deve disparar onSelectSong no clique simples e onProjectSong no duplo clique', () => {
    const onSelect = mock(() => {})
    const onProject = mock(() => {})

    interface CardElementProps {
      onClick: () => void
      onDoubleClick: () => void
    }
    interface ContainerElementProps {
      children: Array<React.ReactElement<CardElementProps>>
    }

    // Chama o componente funcional diretamente
    const renderComponent = (
      SongSearchList as unknown as {
        type: (props: unknown) => React.ReactElement<ContainerElementProps>
      }
    ).type

    const element = renderComponent({
      songs: mockSongs,
      isLoading: false,
      onSelectSong: onSelect,
      onProjectSong: onProject,
    })

    const children = element.props.children
    const firstCard = children[0]

    // Dispara onClick
    firstCard.props.onClick()
    expect(onSelect).toHaveBeenCalledTimes(1)
    expect(onSelect).toHaveBeenCalledWith('song-1')

    // Dispara onDoubleClick
    firstCard.props.onDoubleClick()
    expect(onProject).toHaveBeenCalledTimes(1)
    expect(onProject).toHaveBeenCalledWith('song-1')
  })
})
