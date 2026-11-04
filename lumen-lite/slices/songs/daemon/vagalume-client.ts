import type { Song, SongSection } from '../contracts/song-contracts'

export interface VagalumeSearchResultItem {
  id: string
  title: string
  band: string
  url: string
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

/**
 * Cliente HTTP para a API do Vagalume com suporte a AbortSignal e conversão para o schema LumenLite.
 */
export class VagalumeClient {
  private baseUrl: string
  private apiKey?: string

  /**
   * Inicializa o cliente Vagalume.
   *
   * @param options - Configurações opcionais de URL base e chave de API.
   * @param options.baseUrl - URL base customizada da API.
   * @param options.apiKey - Chave de autenticação da API Vagalume.
   */
  constructor(options?: { baseUrl?: string; apiKey?: string }) {
    this.baseUrl = options?.baseUrl || 'https://api.vagalume.com.br'
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
   * Busca músicas na API do Vagalume.
   *
   * @param query - Termos da busca (título ou trecho).
   * @param signal - Sinal de cancelamento AbortSignal opcional.
   * @returns Lista de resultados simplificados contendo id, título e banda.
   */
  async search(query: string, signal?: AbortSignal): Promise<VagalumeSearchResultItem[]> {
    const trimmed = query.trim()
    if (!trimmed) return []

    const url = `${this.baseUrl}/search.excerpt?q=${encodeURIComponent(trimmed)}&limit=10`

    const res = await fetch(url, {
      signal,
      headers: {
        Accept: 'application/json',
        'User-Agent': 'LumenLite/1.0',
      },
    })

    if (!res.ok) {
      throw new Error(`Vagalume API search failed with status ${res.status}`)
    }

    const data = (await res.json()) as VagalumeSearchResponse
    const docs = data.response?.docs || []

    return docs.map((doc) => ({
      id: doc.id,
      title: doc.title,
      band: doc.band,
      url: doc.url,
    }))
  }

  /**
   * Obtém a letra completa de uma música e converte para o modelo Song do LumenLite.
   *
   * @param musId - Identificador único da música na API Vagalume.
   * @param signal - Sinal de cancelamento AbortSignal opcional.
   * @returns Objeto Song estruturado com estrofes ou null.
   */
  async getSongByMusId(musId: string, signal?: AbortSignal): Promise<Song | null> {
    const apiKeyParam = this.apiKey ? `&apikey=${encodeURIComponent(this.apiKey)}` : ''
    const url = `${this.baseUrl}/search.php?musid=${encodeURIComponent(musId)}${apiKeyParam}`

    const res = await fetch(url, {
      signal,
      headers: {
        Accept: 'application/json',
        'User-Agent': 'LumenLite/1.0',
      },
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
}
