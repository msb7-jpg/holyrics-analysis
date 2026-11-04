/**
 * @module @lumen/daemon
 * @description Ponto de entrada e orquestrador central do servidor de aplicação e WebSocket nativo do LumenLite.
 *
 * ## Responsabilidades:
 * - Inicializar persistência SQLite WAL e semente do acervo musical offline.
 * - Compor dependências (`FsmEngine`, `DbClient`, repositórios e clientes de fatias).
 * - Instanciar o roteador de API REST (`createApiRouter`) e o manipulador WebSocket (`createWebSocketHandler`).
 * - Inicializar o servidor HTTP e WebSocket de ultra-baixa latência (`Bun.serve`).
 *
 * ## Comunicação e Fronteiras:
 * - **Expõe:** Endpoints REST e conexões WebSocket bidirecionais com canais dedicados (`operator`, `projector`, `stage`, `broadcast`).
 * - **Conecta:** Despacha requisições para os módulos de fatias (`@slices/songs`) e reflete transições da FSM (`@lumen/core`).
 */

import { FsmEngine } from '@lumen/core'
import { createLogger } from '@lumen/core/logger'
import { DbClient } from './db/db-client'
import { SongRepository, VagalumeClient } from '@slices/songs'
import type { Song } from '@slices/songs'
import { BibleRepository } from '@slices/bible'
import { LiturgyRepository } from '@slices/liturgy/daemon/liturgy-repository'
import { existsSync } from 'node:fs'
import { resolve } from 'node:path'
import defaultSongs from '../../../slices/songs/default-songs.json'
import { createApiRouter } from './api/router'
import { createWebSocketHandler } from './ws/ws-handler'
import type { WebSocketData } from './ws/ws-handler'

const logger = createLogger('daemon')
const PORT = parseInt(process.env.PORT || '4040', 10)

// 1. Inicialização de Dependências e Persistência
const fsm = new FsmEngine()
const dbClient = new DbClient()
const songRepo = new SongRepository(dbClient.getDb())
const vagalumeClient = new VagalumeClient()
const bibleRepo = new BibleRepository(dbClient.getDb())
const liturgyRepo = new LiturgyRepository()

// Seed inicial do acervo musical
songRepo.seedIfEmpty(defaultSongs as Song[])
logger.info({ songsCount: songRepo.count() }, 'Acervo inicializado')

// Seed inicial do motor bíblico (ACF) caso o banco esteja vazio
if (!bibleRepo.hasBibles()) {
  const bibleSeedPath = resolve(import.meta.dir, '../../../slices/bible/pt_acrf.bib')
  if (existsSync(bibleSeedPath)) {
    bibleRepo.seedFromBibFile(bibleSeedPath, 'ACF', 'Almeida Corrigida e Fiel')
    logger.info({ versesCount: bibleRepo.count('ACF') }, 'Bíblia ACF populada no primeiro boot')
  }
}

// 2. Composição de Roteadores e Manipuladores
const apiRouter = createApiRouter({ fsm, songRepo, vagalumeClient, bibleRepo, liturgyRepo })
const wsHandler = createWebSocketHandler({ fsm, logger })

// 3. Servidor Bun Thin Shell
logger.info({ port: PORT }, 'Inicializando Daemon LumenLite...')
const server = Bun.serve<WebSocketData>({
  port: PORT,
  fetch(request, serverInstance) {
    return apiRouter.handle(request, serverInstance)
  },
  websocket: wsHandler.websocket,
})

// 4. Integração do ciclo de vida FSM -> Clientes WebSocket
wsHandler.bindFsm(server)

logger.info({ port: server.port, url: `http://localhost:${server.port}` }, 'Daemon LumenLite ativo e pronto')
