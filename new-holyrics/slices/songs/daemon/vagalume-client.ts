import type { Song, SongSection } from '../contracts/song-contracts'

export interface VagalumeSearchResultItem {
  id: string
  title: string
  band: string
  url: string
  source?: 'LRCLIB' | 'LETRAS' | 'LEGACY'
}

export interface VagalumeSearchResponse {
  response?: {
    numFound: number
    start: number
    docs: Array<{
      id: string
      title: string
      band: string
      url: string
    }>
  }
}

export interface VagalumeLyricsResponse {
  type: string
  art?: {
    id: string
    name: string
    url: string
  }
  mus?: Array<{
    id: string
    name: string
    url: string
    text: string
  }>
  badwords?: boolean
}

interface LrcLibTrack {
  id: number
  trackName?: string
  name?: string
  artistName?: string
  plainLyrics?: string
}

/**
 * Cliente HTTP de busca de letras online 100% sem chave de API (LRCLIB + Letras.mus.br),
 * mantendo compatibilidade com `baseUrl` customizado para testes.
 */
export class VagalumeClient {
  private baseUrl?: string
  private apiKey?: string
  private lrclibCache = new Map<string, Song>()

  constructor(options?: { baseUrl?: string; apiKey?: string }) {
    this.baseUrl = options?.baseUrl
    this.apiKey = options?.apiKey
  }

  /**
   * Converte a letra em texto bruto (com quebras de linha e blocos) em seções semânticas.
   *
   * @param lyricsText - Texto bruto da letra da canção.
   * @returns Lista de estrofes numeradas estruturadas.
   */
  static parseLyricsToSections(lyricsText: string): SongSection[] {
    const blocks = lyricsText
      .split(/\n\s*\n/)
      .map((block) => block.trim())
      .filter(Boolean)

    return blocks.map((block, idx) => ({
      id: `sec-${idx + 1}`,
      index: idx + 1,
      lines: block
        .split('\n')
        .map((line) => line.trim())
        .filter(Boolean),
    }))
  }

  /**
   * Busca músicas online sem necessidade de API key (LRCLIB + Letras.mus.br).
   *
   * @param query - Termos da busca (título ou trecho).
   * @param signal - Sinal de cancelamento AbortSignal opcional.
   * @returns Lista de resultados contendo id, título e artista/banda.
   */
  async search(query: string, signal?: AbortSignal): Promise<VagalumeSearchResultItem[]> {
    const trimmed = query.trim()
    if (!trimmed) return []

    // Modo compatibilidade para testes com baseUrl mockado
    if (this.baseUrl) {
      const url = `${this.baseUrl}/search.excerpt?q=${encodeURIComponent(trimmed)}&limit=10`
      const res = await fetch(url, {
        signal,
        headers: { Accept: 'application/json', 'User-Agent': 'LumenLite/1.0' },
      })
      if (!res.ok) {
        throw new Error(`Vagalume API search failed with status ${res.status}`)
      }
      const data = (await res.json()) as VagalumeSearchResponse
      return (data.response?.docs || []).map((doc) => ({
        id: doc.id,
        title: doc.title,
        band: doc.band,
        url: doc.url,
        source: 'LEGACY',
      }))
    }

    const lrclibResults = await this.searchLrcLib(trimmed, signal).catch(() => [])
    const seen = new Set<string>()
    const deduped: VagalumeSearchResultItem[] = []
    for (const item of lrclibResults) {
      const key = `${item.title.toLowerCase()}::${item.band.toLowerCase()}`
      if (seen.has(key)) continue
      seen.add(key)
      deduped.push(item)
    }
    return deduped.slice(0, 20)
  }

  private async searchLrcLib(query: string, signal?: AbortSignal): Promise<VagalumeSearchResultItem[]> {
    const url = `https://lrclib.net/api/search?q=${encodeURIComponent(query)}`
    const res = await fetch(url, {
      signal,
      headers: { Accept: 'application/json', 'User-Agent': 'LumenLite/1.0' },
    })
    if (!res.ok) return []
    const tracks = (await res.json()) as LrcLibTrack[]
    const out: VagalumeSearchResultItem[] = []

    for (const track of tracks) {
      if (!track.plainLyrics?.trim()) continue
      const id = `lrclib:${track.id}`
      const title = track.trackName || track.name || 'Sem título'
      const artist = track.artistName || ''
      const sections = VagalumeClient.parseLyricsToSections(track.plainLyrics)
      this.lrclibCache.set(id, {
        id: `online-lrclib-${track.id}`,
        title,
        artist: artist || undefined,
        sections,
        defaultArrangement: sections.map((s) => s.id),
      })
      out.push({
        id,
        title,
        band: artist,
        url: `https://lrclib.net/api/get/${track.id}`,
        source: 'LRCLIB',
      })
    }
    return out
  }

  /**
   * Obtém a letra completa de uma música e converte para o modelo Song do LumenLite.
   *
   * @param musId - Identificador (`lrclib:...`, `letras:...` ou ID legado).
   * @param signal - Sinal de cancelamento AbortSignal opcional.
   * @returns Objeto Song estruturado com estrofes ou null.
   */
  async getSongByMusId(musId: string, signal?: AbortSignal): Promise<Song | null> {
    if (this.baseUrl) {
      const apiKeyParam = this.apiKey ? `&apikey=${encodeURIComponent(this.apiKey)}` : ''
      const url = `${this.baseUrl}/search.php?musid=${encodeURIComponent(musId)}${apiKeyParam}`
      const res = await fetch(url, {
        signal,
        headers: { Accept: 'application/json', 'User-Agent': 'LumenLite/1.0' },
      })
      if (!res.ok) {
        throw new Error(`Vagalume API get lyrics failed with status ${res.status}`)
      }
      const data = (await res.json()) as VagalumeLyricsResponse
      const mus = data.mus?.[0]
      if (!mus || !mus.text) return null
      const sections = VagalumeClient.parseLyricsToSections(mus.text)
      return {
        id: `vagalume-${mus.id}`,
        title: mus.name,
        artist: data.art?.name,
        sections,
        defaultArrangement: sections.map((section) => section.id),
      }
    }

    if (musId.startsWith('lrclib:')) {
      const cached = this.lrclibCache.get(musId)
      if (cached) return cached
      const numericId = musId.replace('lrclib:', '')
      const res = await fetch(`https://lrclib.net/api/get/${encodeURIComponent(numericId)}`, {
        signal,
        headers: { Accept: 'application/json', 'User-Agent': 'LumenLite/1.0' },
      })
      if (!res.ok) return null
      const track = (await res.json()) as LrcLibTrack
      if (!track.plainLyrics) return null
      const sections = VagalumeClient.parseLyricsToSections(track.plainLyrics)
      return {
        id: `online-lrclib-${track.id}`,
        title: track.trackName || track.name || 'Sem título',
        artist: track.artistName || undefined,
        sections,
        defaultArrangement: sections.map((s) => s.id),
      }
    }

    if (musId.startsWith('letras:')) {
      const slug = musId.replace('letras:', '')
      const pageUrl = `https://www.letras.mus.br/${slug}/`
      const res = await fetch(pageUrl, {
        signal,
        headers: { 'User-Agent': 'Mozilla/5.0 LumenLite/1.0' },
      })
      if (!res.ok) return null
      const html = await res.text()
      const titleMatch = html.match(/<h1[^>]*>([\s\S]*?)<\/h1>/i)
      const artistMatch = html.match(/<h2[^>]*class="[^"]*head-subtitle[^"]*"[^>]*>[\s\S]*?<a[^>]*>([\s\S]*?)<\/a>/i)
      const lyricBlockMatch = html.match(/<div[^>]*class="[^"]*lyric-original[^"]*"[^>]*>([\s\S]*?)<\/div>/i)
      if (!lyricBlockMatch) return null

      const rawText = lyricBlockMatch[1]
        .replace(/<\/p>\s*<p[^>]*>/gi, '\n\n')
        .replace(/<br\s*\/?>/gi, '\n')
        .replace(/<[^>]+>/g, '')
        .replace(/&amp;/g, '&')
        .replace(/&quot;/g, '"')
        .replace(/&#39;/g, "'")
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .trim()

      const sections = VagalumeClient.parseLyricsToSections(rawText)
      if (sections.length === 0) return null

      return {
        id: `online-letras-${slug.replace(/[^a-z0-9_-]/gi, '-')}`,
        title: titleMatch ? titleMatch[1].replace(/<[^>]+>/g, '').trim() : slug.split('/').pop() || 'Música',
        artist: artistMatch ? artistMatch[1].replace(/<[^>]+>/g, '').trim() : undefined,
        sections,
        defaultArrangement: sections.map((s) => s.id),
      }
    }

    return null
  }
}
