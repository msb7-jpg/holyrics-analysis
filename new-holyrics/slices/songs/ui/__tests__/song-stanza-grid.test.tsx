/**
 * @module @slices/songs/ui/__tests__/song-stanza-grid.test
 * @description Testes unitários para SongStanzaGrid com Slide 0 de Capa e botão de edição.
 */

import { describe, expect, it } from 'bun:test'
import { renderToString } from 'react-dom/server'
import type { Song } from '../../contracts/song-contracts'
import { SongStanzaGrid } from '../components/SongStanzaGrid'

const mockSong: Song = {
  id: 'song-maranata',
  title: 'Maranata',
  artist: 'Ministério Avivah',
  sections: [
    {
      id: 'sec-1',
      index: 1,
      lines: ['Tu és a minha luz', 'A minha salvação'],
    },
    {
      id: 'sec-2',
      index: 2,
      lines: ['Maranata, vem, Senhor Jesus'],
    },
  ],
}

describe('SongStanzaGrid', () => {
  it('deve renderizar o card 00 de Capa/Título com título e artista', () => {
    const html = renderToString(<SongStanzaGrid song={mockSong} />)

    expect(html).toContain('00 · Capa / Título')
    expect(html).toContain('Maranata')
    expect(html).toContain('Ministério Avivah')
    expect(html).toContain('00')
  })

  it('deve renderizar as estrofes com numeração 01 e 02', () => {
    const html = renderToString(<SongStanzaGrid song={mockSong} />)

    expect(html).toContain('01')
    expect(html).toContain('Tu és a minha luz')
    expect(html).toContain('02')
    expect(html).toContain('Maranata, vem, Senhor Jesus')
  })

  it('deve renderizar o botão de Editar com texto e título corretos', () => {
    const html = renderToString(<SongStanzaGrid song={mockSong} />)

    expect(html).toContain('Editar')
    expect(html).toContain('Editar letra, título e fundo desta música')
  })

  it('deve exibir badge AO VIVO no card 00 quando activeSlideIndex for 0 e isLiveOnScreen for true', () => {
    const html = renderToString(
      <SongStanzaGrid song={mockSong} activeSlideIndex={0} isLiveOnScreen={true} />
    )

    expect(html).toContain('AO VIVO')
  })

  it('deve exibir badge AO VIVO na estrofe 01 quando activeSlideIndex for 1 e isLiveOnScreen for true', () => {
    const html = renderToString(
      <SongStanzaGrid song={mockSong} activeSlideIndex={1} isLiveOnScreen={true} />
    )

    expect(html).toContain('AO VIVO')
  })
})
