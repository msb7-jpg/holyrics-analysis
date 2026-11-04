/**
 * @module @slices/media/daemon/media-repository
 * @description Repositório em disco e memória para indexação de mídias e gerenciamento de playlists litúrgicas de áudio.
 *
 * ## Responsabilidades:
 * - Persistência atômica e leitura de playlists em `media-playlists.json`.
 * - Consulta e filtragem do catálogo de mídias visuais (loops de vídeo e imagens de fundo).
 * - Varredura de diretórios locais de assets com detecção de extensão e mimeType.
 * - Fornecimento de metadados para streaming com suporte a HTTP Range requests.
 *
 * ## Comunicação e Fronteiras:
 * - Consumido pelo `handleMediaRoutes` em `/api/media/*` e pelo daemon LumenLite.
 */

import { existsSync, readFileSync, renameSync, unlinkSync, writeFileSync, readdirSync, statSync } from 'node:fs'
import { join, extname, basename } from 'node:path'
import { createLogger } from '@lumen/core'
import {
  type MediaItem,
  type AudioPlaylist,
  type AudioTrack,
  type MediaType,
  DEFAULT_MEDIA_ITEMS,
  DEFAULT_AUDIO_PLAYLISTS,
  AudioPlaylistSchema,
} from '../contracts/media-contracts'

const logger = createLogger('media-repo')

export interface MediaFilterOptions {
  type?: MediaType
  query?: string
}

/**
 * Repositório atômico e indexador de mídias do ecossistema LumenLite.
 */
export class MediaRepository {
  private mediaItems: MediaItem[] = []
  private playlists: AudioPlaylist[] = []
  private playlistsFilePath: string
  private mediaDirectory: string

  /**
   * Inicializa o repositório de mídias e playlists.
   *
   * @param customPlaylistsPath - Caminho opcional do arquivo `media-playlists.json`.
   * @param customMediaDirectory - Caminho opcional do diretório de assets de mídia.
   */
  constructor(customPlaylistsPath?: string, customMediaDirectory?: string) {
    this.playlistsFilePath = customPlaylistsPath ?? join(process.cwd(), 'media-playlists.json')
    this.mediaDirectory = customMediaDirectory ?? join(process.cwd(), 'assets', 'media')
    this.mediaItems = [...DEFAULT_MEDIA_ITEMS]
    this.loadPlaylists()
    this.scanMediaDirectory()
  }

  /**
   * Carrega as playlists salvas em disco ou inicializa com os padrões de fábrica.
   *
   * @returns Lista de playlists de áudio carregadas.
   */
  public loadPlaylists(): AudioPlaylist[] {
    if (!existsSync(this.playlistsFilePath)) {
      this.playlists = [...DEFAULT_AUDIO_PLAYLISTS]
      this.savePlaylists(this.playlists)
      return this.playlists
    }

    try {
      const rawContent = readFileSync(this.playlistsFilePath, 'utf-8')
      const parsedJson = JSON.parse(rawContent) as unknown[]
      this.playlists = parsedJson.map((entry) => AudioPlaylistSchema.parse(entry))
      return this.playlists
    } catch (error) {
      logger.error({ error, path: this.playlistsFilePath }, 'Falha ao carregar media-playlists.json. Restaurando padrões.')
      this.playlists = [...DEFAULT_AUDIO_PLAYLISTS]
      return this.playlists
    }
  }

  /**
   * Salva as playlists de áudio de forma atômica no disco (.tmp -> rename).
   *
   * @param playlists - Lista de playlists atualizada.
   */
  public savePlaylists(playlists: AudioPlaylist[]): void {
    const validated = playlists.map((entry) => AudioPlaylistSchema.parse(entry))
    const tempPath = `${this.playlistsFilePath}.tmp`

    try {
      writeFileSync(tempPath, JSON.stringify(validated, null, 2), 'utf-8')
      renameSync(tempPath, this.playlistsFilePath)
      this.playlists = validated
      logger.info({ count: validated.length }, 'media-playlists.json salvo com sucesso')
    } catch (error) {
      if (existsSync(tempPath)) {
        unlinkSync(tempPath)
      }
      logger.error({ error }, 'Erro crítico ao gravar media-playlists.json atomicamente')
      throw error
    }
  }

  /**
   * Realiza a varredura da pasta de mídias locais e indexa arquivos compatíveis.
   */
  public scanMediaDirectory(): void {
    if (!existsSync(this.mediaDirectory)) {
      return
    }

    try {
      const files = readdirSync(this.mediaDirectory, { recursive: true, withFileTypes: true })
      for (const entry of files) {
        if (!entry.isFile()) continue

        const filePath = join(entry.parentPath ?? this.mediaDirectory, entry.name)
        const fileExt = extname(entry.name).toLowerCase()
        const mediaType = this.resolveMediaType(fileExt)

        if (!mediaType) continue

        const stats = statSync(filePath)
        const fileBaseName = basename(entry.name, fileExt)
        const mediaId = `media-${fileBaseName.toLowerCase().replace(/[^a-z0-9_-]/g, '-')}`

        // Evita duplicatas se já estiver cadastrado
        if (!this.mediaItems.some((item) => item.id === mediaId)) {
          this.mediaItems.push({
            id: mediaId,
            name: fileBaseName.replace(/[-_]/g, ' '),
            path: filePath,
            type: mediaType,
            sizeBytes: stats.size,
            mimeType: this.resolveMimeType(fileExt),
            resolution: mediaType === 'AUDIO' ? undefined : '1920x1080',
            tags: [mediaType.toLowerCase(), fileExt.replace('.', '')],
          })
        }
      }
      logger.info({ totalMedia: this.mediaItems.length }, 'Varredura de mídias locais concluída')
    } catch (error) {
      logger.warn({ error, dir: this.mediaDirectory }, 'Varredura de diretório de mídias não pôde ser completada')
    }
  }

  /**
   * Lista itens de mídia aplicando filtros opcionais por tipo e busca textual.
   *
   * @param options - Opções de filtragem.
   * @returns Array de itens de mídia correspondentes.
   */
  public listMedia(options?: MediaFilterOptions): MediaItem[] {
    let results = [...this.mediaItems]

    if (options?.type) {
      results = results.filter((item) => item.type === options.type)
    }

    if (options?.query) {
      const queryLower = options.query.toLowerCase().trim()
      results = results.filter(
        (item) =>
          item.name.toLowerCase().includes(queryLower) ||
          item.tags.some((tag) => tag.toLowerCase().includes(queryLower)),
      )
    }

    return results
  }

  /**
   * Obtém um item de mídia pelo seu identificador único.
   *
   * @param itemId - Identificador da mídia.
   * @returns O item de mídia encontrado ou undefined.
   */
  public getMediaById(itemId: string): MediaItem | undefined {
    return this.mediaItems.find((item) => item.id === itemId)
  }

  /**
   * Cadastra ou substitui um item de mídia dinamicamente.
   *
   * @param item - Objeto da mídia validado.
   */
  public upsertMediaItem(item: MediaItem): void {
    const existingIndex = this.mediaItems.findIndex((existing) => existing.id === item.id)
    if (existingIndex >= 0) {
      this.mediaItems[existingIndex] = item
    } else {
      this.mediaItems.push(item)
    }
  }

  /**
   * Retorna todas as playlists de áudio ativas.
   *
   * @returns Lista de playlists.
   */
  public listPlaylists(): AudioPlaylist[] {
    return [...this.playlists]
  }

  /**
   * Busca uma playlist específica pelo identificador.
   *
   * @param playlistId - ID da playlist.
   * @returns A playlist correspondente ou undefined.
   */
  public getPlaylistById(playlistId: string): AudioPlaylist | undefined {
    return this.playlists.find((playlist) => playlist.id === playlistId)
  }

  /**
   * Adiciona ou atualiza uma playlist de áudio no catálogo.
   *
   * @param playlist - Objeto da playlist.
   */
  public upsertPlaylist(playlist: AudioPlaylist): void {
    const existingIndex = this.playlists.findIndex((entry) => entry.id === playlist.id)
    const updated = [...this.playlists]

    if (existingIndex >= 0) {
      updated[existingIndex] = playlist
    } else {
      updated.push(playlist)
    }

    this.savePlaylists(updated)
  }

  /**
   * Cria uma nova faixa dentro de uma playlist existente.
   *
   * @param playlistId - ID da playlist alvo.
   * @param track - Faixa de áudio a ser inserida.
   * @returns A playlist atualizada.
   */
  public addTrackToPlaylist(playlistId: string, track: AudioTrack): AudioPlaylist {
    const playlist = this.getPlaylistById(playlistId)
    if (!playlist) {
      throw new Error(`Playlist não encontrada: ${playlistId}`)
    }

    const updatedTracks = [...playlist.tracks, track]
    const updatedPlaylist: AudioPlaylist = {
      ...playlist,
      tracks: updatedTracks,
    }

    this.upsertPlaylist(updatedPlaylist)
    return updatedPlaylist
  }

  /**
   * Determina a categoria de mídia a partir da extensão do arquivo.
   *
   * @param extension - Extensão do arquivo com ponto (ex: '.mp4').
   * @returns Tipo de mídia reconhecido ou null.
   */
  private resolveMediaType(extension: string): MediaType | null {
    switch (extension) {
      case '.mp4':
      case '.webm':
      case '.mov':
      case '.mkv':
        return 'VIDEO'
      case '.png':
      case '.jpg':
      case '.jpeg':
      case '.webp':
      case '.svg':
        return 'IMAGE'
      case '.mp3':
      case '.wav':
      case '.ogg':
      case '.flac':
      case '.m4a':
      case '.aac':
        return 'AUDIO'
      default:
        return null
    }
  }

  /**
   * Determina o tipo MIME correspondente para streaming HTTP.
   *
   * @param extension - Extensão do arquivo com ponto (ex: '.mp4').
   * @returns String representando o MIME type.
   */
  private resolveMimeType(extension: string): string {
    switch (extension) {
      case '.mp4':
        return 'video/mp4'
      case '.webm':
        return 'video/webm'
      case '.mov':
        return 'video/quicktime'
      case '.png':
        return 'image/png'
      case '.jpg':
      case '.jpeg':
        return 'image/jpeg'
      case '.webp':
        return 'image/webp'
      case '.svg':
        return 'image/svg+xml'
      case '.mp3':
        return 'audio/mpeg'
      case '.wav':
        return 'audio/wav'
      case '.ogg':
        return 'audio/ogg'
      case '.flac':
        return 'audio/flac'
      case '.m4a':
      case '.aac':
        return 'audio/mp4'
      default:
        return 'application/octet-stream'
    }
  }
}
