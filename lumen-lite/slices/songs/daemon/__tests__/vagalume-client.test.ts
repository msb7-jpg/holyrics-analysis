import { describe, it, expect } from 'bun:test'
import { VagalumeClient } from '../vagalume-client'
import { SongSchema } from '../../contracts/song-contracts'

describe('VagalumeClient', () => {
  it('converte corretamente blocos de texto em seções semânticas do LumenLite', () => {
    const rawLyrics = `
      Te amo, ó Deus
      Tua graça nunca falha
      
      Desde o amanhecer
      Até o pôr do sol
      Eu cantarei
    `

    const sections = VagalumeClient.parseLyricsToSections(rawLyrics)

    expect(sections).toHaveLength(2)
    expect(sections[0].index).toBe(1)
    expect(sections[0].lines).toEqual(['Te amo, ó Deus', 'Tua graça nunca falha'])
    expect(sections[1].index).toBe(2)
    expect(sections[1].lines).toEqual(['Desde o amanhecer', 'Até o pôr do sol', 'Eu cantarei'])
  })

  it('formata o resultado de busca mockado adequadamente', async () => {
    // Testamos com mock de fetch
    const mockClient = new VagalumeClient({ baseUrl: 'http://mock-api.internal' })

    const originalFetch = globalThis.fetch
    globalThis.fetch = (async (url: string) => {
      if (url.includes('/search.excerpt')) {
        return {
          ok: true,
          json: async () => ({
            response: {
              docs: [
                {
                  id: '123',
                  title: 'Bondade de Deus',
                  band: 'Isaías Saad',
                  url: '/isaias-saad/bondade-de-deus.html',
                },
              ],
            },
          }),
        } as Response
      }

      if (url.includes('/search.php')) {
        return {
          ok: true,
          json: async () => ({
            type: 'exact',
            art: { id: 'art-1', name: 'Isaías Saad', url: '/isaias-saad/' },
            mus: [
              {
                id: '123',
                name: 'Bondade de Deus',
                url: '/isaias-saad/bondade-de-deus.html',
                text: 'Te amo, ó Deus\nTua graça nunca falha\n\nEu cantarei',
              },
            ],
          }),
        } as Response
      }

      return { ok: false, status: 404 } as Response
    }) as typeof fetch

    try {
      const results = await mockClient.search('bondade')
      expect(results).toHaveLength(1)
      expect(results[0].title).toBe('Bondade de Deus')
      expect(results[0].band).toBe('Isaías Saad')

      const song = await mockClient.getSongByMusId('123')
      expect(song).not.toBeNull()
      expect(song?.title).toBe('Bondade de Deus')
      expect(song?.artist).toBe('Isaías Saad')
      expect(song?.sections).toHaveLength(2)

      // Valida com Zod schema
      expect(() => SongSchema.parse(song)).not.toThrow()
    } finally {
      globalThis.fetch = originalFetch
    }
  })
})
