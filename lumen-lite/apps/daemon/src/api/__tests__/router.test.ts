import { describe, expect, it, beforeAll, afterAll } from 'bun:test'
import type { Server } from 'bun'
import { Database } from 'bun:sqlite'
import { resolve } from 'node:path'
import { existsSync, unlinkSync } from 'node:fs'
import { FsmEngine } from '@lumen/core'
import { SongRepository, VagalumeClient } from '@slices/songs'
import type { Song } from '@slices/songs'
import { BibleRepository } from '@slices/bible'
import { LiturgyRepository } from '@slices/liturgy/daemon/liturgy-repository'
import { AutomationRepository, DagRunner } from '@slices/automation'
import type { WebSocketData } from '../../ws/ws-handler'
import { createApiRouter } from '../router'

describe('Daemon API Router', () => {
  let router: ReturnType<typeof createApiRouter>
  let songRepo: SongRepository
  let mockServer: Server<WebSocketData>
  let automationRepo: AutomationRepository

  beforeAll(() => {
    const database = new Database(':memory:')
    songRepo = new SongRepository(database)
    const bibleRepo = new BibleRepository(database)
    const bibFilePath = resolve(import.meta.dir, '../../../../../../src/com/limagiran/bible/database/pt_acrf.bib')
    bibleRepo.seedFromBibFile(bibFilePath, 'ACF', 'Almeida Corrigida e Fiel')

    const fsm = new FsmEngine()
    const vagalumeClient = new VagalumeClient()
    const liturgyRepo = new LiturgyRepository(resolve(import.meta.dir, 'test-culto-router.json'))
    automationRepo = new AutomationRepository(resolve(import.meta.dir, 'test-automation-router.json'))
    const dagRunner = new DagRunner(automationRepo, null, (cmd) => fsm.dispatch(cmd))

    router = createApiRouter({
      fsm,
      songRepo,
      vagalumeClient,
      bibleRepo,
      liturgyRepo,
      automationRepo,
      dagRunner,
    })

    const sampleSong: Song = {
      id: 'test-song-1',
      title: 'Quão Grande És Tu',
      artist: 'Tradicional',
      sections: [
        {
          id: 'sec-1',
          index: 1,
          lines: ['Senhor meu Deus, quando eu maravilhado'],
        },
      ],
    }
    songRepo.upsertSong(sampleSong)

    mockServer = {
      subscriberCount: (_channel: string) => 2,
      upgrade: () => false,
    } as unknown as Server<WebSocketData>
  })

  afterAll(() => {
    const testCultoPath = resolve(import.meta.dir, 'test-culto-router.json')
    if (existsSync(testCultoPath)) unlinkSync(testCultoPath)

    const testAutoPath = resolve(import.meta.dir, 'test-automation-router.json')
    if (existsSync(testAutoPath)) unlinkSync(testAutoPath)
  })

  it('deve responder requisições OPTIONS com cabeçalhos CORS completos', async () => {
    const request = new Request('http://localhost:4040/api/songs', { method: 'OPTIONS' })
    const response = await router.handle(request, mockServer)

    expect(response).toBeDefined()
    expect(response?.status).toBe(200)
    expect(response?.headers.get('Access-Control-Allow-Origin')).toBe('*')
    expect(response?.headers.get('Access-Control-Allow-Methods')).toContain('GET, POST, OPTIONS')
  })

  it('deve retornar métricas e integridade no endpoint /api/status', async () => {
    const request = new Request('http://localhost:4040/api/status', { method: 'GET' })
    const response = await router.handle(request, mockServer)

    expect(response?.status).toBe(200)
    const json = await response?.json()
    expect(json.ok).toBe(true)
    expect(json.clients).toBe(2)
    expect(json.songsCount).toBe(1)
    expect(json.state.mode).toBe('EMPTY')
  })

  it('deve buscar músicas no banco FTS5 via GET /api/songs', async () => {
    const request = new Request('http://localhost:4040/api/songs?q=grande', { method: 'GET' })
    const response = await router.handle(request, mockServer)

    expect(response?.status).toBe(200)
    const json = await response?.json()
    expect(json.ok).toBe(true)
    expect(json.data.length).toBe(1)
    expect(json.data[0].title).toBe('Quão Grande És Tu')
  })

  it('deve retornar música por ID via GET /api/songs/:id', async () => {
    // Encontrada
    const requestOk = new Request('http://localhost:4040/api/songs/test-song-1', { method: 'GET' })
    const responseOk = await router.handle(requestOk, mockServer)
    expect(responseOk?.status).toBe(200)
    const jsonOk = await responseOk?.json()
    expect(jsonOk.data.id).toBe('test-song-1')

    // 404 Not Found
    const requestNotFound = new Request('http://localhost:4040/api/songs/inexistente', { method: 'GET' })
    const responseNotFound = await router.handle(requestNotFound, mockServer)
    expect(responseNotFound?.status).toBe(404)
    const jsonNotFound = await responseNotFound?.json()
    expect(jsonNotFound.ok).toBe(false)
  })

  it('deve inserir nova música via POST /api/songs', async () => {
    const newSong: Song = {
      id: 'test-song-2',
      title: 'Em Espírito, Em Verdade',
      artist: 'Ministério',
      sections: [{ id: 'sec-2-1', index: 1, lines: ['Te adoramos'] }],
    }

    const request = new Request('http://localhost:4040/api/songs', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(newSong),
    })

    const response = await router.handle(request, mockServer)
    expect(response?.status).toBe(201)
    expect(songRepo.count()).toBe(2)
  })

  it('deve retornar rota de fallback amigável para endpoints desconhecidos', async () => {
    const request = new Request('http://localhost:4040/', { method: 'GET' })
    const response = await router.handle(request, mockServer)

    expect(response?.status).toBe(200)
    const text = await response?.text()
    expect(text).toBe('LumenLite Daemon Active')
  })

  it('deve realizar lookup bíblico via GET /api/bible/lookup', async () => {
    const request = new Request('http://localhost:4040/api/bible/lookup?ref=jo+3+16', { method: 'GET' })
    const response = await router.handle(request, mockServer)

    expect(response?.status).toBe(200)
    const json = await response?.json()
    expect(json.ok).toBe(true)
    expect(json.data.reference).toBe('João 3:16')
    expect(json.data.verses.length).toBe(1)
    expect(json.data.verses[0].text).toContain('Porque Deus amou o mundo')
  })

  it('deve realizar busca textual por palavra-chave via GET /api/bible/search', async () => {
    const request = new Request('http://localhost:4040/api/bible/search?q=luz+do+mundo', { method: 'GET' })
    const response = await router.handle(request, mockServer)

    expect(response?.status).toBe(200)
    const json = await response?.json()
    expect(json.ok).toBe(true)
    expect(json.data.length).toBeGreaterThan(0)
    expect(json.data[0].snippet).toContain('<mark')
  })

  it('deve listar versões bíblicas cadastradas via GET /api/bible/versions', async () => {
    const request = new Request('http://localhost:4040/api/bible/versions', { method: 'GET' })
    const response = await router.handle(request, mockServer)

    expect(response?.status).toBe(200)
    const json = await response?.json()
    expect(json.ok).toBe(true)
    expect(json.data.length).toBe(1)
    expect(json.data[0].id).toBe('ACF')
    expect(json.data[0].verseCount).toBe(31106)
  })

  it('deve retornar o grafo ativo e status do OBS via GET /api/automation', async () => {
    const request = new Request('http://localhost:4040/api/automation', { method: 'GET' })
    const response = await router.handle(request, mockServer)

    expect(response?.status).toBe(200)
    const json = await response?.json()
    expect(json.ok).toBe(true)
    expect(json.data.graph).toBeDefined()
    expect(json.data.graph.nodes.length).toBeGreaterThan(0)
    expect(json.data.obsStatus).toBeDefined()
  })

  it('deve salvar e validar novo grafo DAG via PUT /api/automation', async () => {
    const current = automationRepo.load()
    const request = new Request('http://localhost:4040/api/automation', {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(current),
    })
    const response = await router.handle(request, mockServer)

    expect(response?.status).toBe(200)
    const json = await response?.json()
    expect(json.ok).toBe(true)
  })
})

