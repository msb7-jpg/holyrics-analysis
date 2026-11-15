#!/usr/bin/env bun
/**
 * @module @lumen/daemon/cli
 * @description Interface de linha de comando (CLI) headless para controle total e automação do LumenLite (RF-30).
 *
 * ## Responsabilidades:
 * - Operação pura via terminal para integração nativa com Stream Deck, scripts de shell e Bitfocus Companion.
 * - Despacho de comandos de projeção, máscaras de pânico, consulta musical, versículos bíblicos e roteiro litúrgico.
 * - Suporte à saída formatada em JSON (`--json`) e parâmetros de rede (`--url`).
 */

import { parseArgs } from 'node:util'

const { values, positionals } = parseArgs({
  args: Bun.argv.slice(2),
  options: {
    help: { type: 'boolean', short: 'h' },
    json: { type: 'boolean', short: 'j' },
    url: { type: 'string', default: 'http://localhost:4040' },
  },
  allowPositionals: true,
})

const baseUrl = values.url || 'http://localhost:4040'
const isJson = Boolean(values.json)

/**
 * Exibe manual de ajuda do CLI no terminal.
 */
function printHelp(): void {
  console.log(`
\x1b[1m\x1b[36mLumenLite CLI (lumen)\x1b[0m - Controle Headless de Projeção Litúrgica

\x1b[1mUSO:\x1b[0m
  lumen <categoria|comando> [subcomando] [argumentos...] [opções]

\x1b[1mCOMANDOS DIRETOS (ATALHOS STREAM DECK):\x1b[0m
  next                           Avança para o próximo slide
  prev                           Retorna ao slide anterior
  goto <índice>                  Salta para o slide de índice informado
  clear                          Limpa o telão (retorna a tela vazia)
  blackout                       Alterna modo Blackout (tela preta instantânea)
  clear-text                     Alterna ocultar texto (mantém vídeo de fundo)
  clear-bg                       Alterna ocultar vídeo (mantém texto legível)
  freeze                         Alterna congelamento do quadro da projeção
  status                         Consulta a telemetria do daemon e telão

\x1b[1mSUBCOMANDOS POR FATIA:\x1b[0m
  \x1b[33mnav\x1b[0m
    lumen nav next               Avança para o próximo slide
    lumen nav prev               Retorna ao slide anterior
    lumen nav goto <índice>      Navega para slide específico
    lumen nav clear              Limpa o conteúdo do telão

  \x1b[33mpanic\x1b[0m
    lumen panic blackout         Alterna tela preta total
    lumen panic clear-text       Alterna ocultação de texto
    lumen panic clear-bg         Alterna fundo preto sólido
    lumen panic freeze           Alterna congelamento de tela
    lumen panic clear            Desativa todas as máscaras de pânico ativas

  \x1b[33msong\x1b[0m
    lumen song search <termo>    Busca músicas no banco offline FTS5
    lumen song play <id> [idx]   Projeta uma música pelo ID no slide indicado

  \x1b[33mbible\x1b[0m
    lumen bible quick <ref>      Busca rápida por referência (ex: "jo 3 16")
    lumen bible search <termo>   Busca textual no acervo bíblico
    lumen bible project <v> <l> <c> <versículo>  Projeta versículo específico

  \x1b[33mliturgy\x1b[0m
    lumen liturgy show           Exibe o roteiro do culto ativo (culto.json)
    lumen liturgy quick-text <t> Projeta texto livre de emergência imediatamente

  \x1b[33mautomation\x1b[0m
    lumen automation status      Exibe o grafo ativo e status do OBS v5
    lumen automation trigger <t> Dispara gatilho de teste (ex: ON_SLIDE_CHANGE)

  \x1b[33mkeymap\x1b[0m
    lumen keymap list            Lista todos os atalhos declarativos ativos
    lumen keymap reset           Restaura atalhos para padrões de fábrica

  \x1b[33mmedia\x1b[0m
    lumen media list [tipo]      Lista itens de mídia (todos, video, image)
    lumen media bg <id>          Aplica item como fundo da projeção no telão

  \x1b[33maudio\x1b[0m
    lumen audio playlists        Lista as playlists litúrgicas de áudio ativas

  \x1b[33mstage\x1b[0m
    lumen stage alert <texto>    Envia alerta Lower Third para telão/palco
    lumen stage countdown <seg>  Inicia cronômetro regressivo de culto
    lumen stage message <texto>  Envia mensagem privada ao monitor do pastor

  \x1b[33mremote\x1b[0m
    lumen remote info            Exibe tokens e URLs LAN para pareamento mobile

\x1b[1mOPÇÕES:\x1b[0m
  -j, --json                     Retorna a saída formatada em JSON estruturado
  --url <daemonUrl>              URL do daemon LumenLite (padrão: http://localhost:4040)
  -h, --help                     Exibe este manual
`)
}

if (values.help || positionals.length === 0) {
  printHelp()
  process.exit(0)
}

/**
 * Utilitário de requisição HTTP para a API do Daemon.
 *
 * @param path - Rota da API REST.
 * @param options - Opções de fetch.
 * @returns Resposta formatada em JSON.
 */
async function requestApi<T = unknown>(path: string, options?: RequestInit): Promise<T> {
  const target = `${baseUrl}${path}`
  try {
    const response = await fetch(target, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        ...(options?.headers || {}),
      },
    })

    if (!response.ok) {
      const errorBody = await response.text()
      throw new Error(`HTTP ${response.status}: ${errorBody}`)
    }

    return (await response.json()) as T
  } catch (err: unknown) {
    const msg = err instanceof Error ? err.message : String(err)
    if (isJson) {
      console.error(JSON.stringify({ ok: false, error: msg }))
    } else {
      console.error(`\x1b[31m[Erro]\x1b[0m Falha ao comunicar com o daemon (${baseUrl}): ${msg}`)
      console.error('Certifique-se de que o daemon está ativo: bun run dev:daemon')
    }
    process.exit(1)
  }
}

/**
 * Despacha um comando para a FSM da projeção.
 *
 * @param command - Objeto do comando de ação.
 */
async function dispatchFsm(command: Record<string, unknown>): Promise<void> {
  const result = await requestApi<{ ok: boolean; data: { mode: string; foreground: { slideIndex: number } } }>(
    '/api/fsm/dispatch',
    {
      method: 'POST',
      body: JSON.stringify({ command }),
    },
  )

  if (isJson) {
    console.log(JSON.stringify(result))
  } else {
    console.log(`\x1b[32m✔\x1b[0m Comando \x1b[1m${command.type}\x1b[0m despachado com sucesso.`)
    console.log(`  Modo Atual: \x1b[36m${result.data.mode}\x1b[0m | Slide: \x1b[33m${result.data.foreground.slideIndex}\x1b[0m`)
  }
}

const primary = positionals[0].toLowerCase()
const secondary = (positionals[1] || '').toLowerCase()
const tertiary = positionals[2] || ''

// 1. Comandos de Navegação Direta
if (primary === 'next' || (primary === 'nav' && secondary === 'next')) {
  await dispatchFsm({ type: 'NAV_NEXT' })
  process.exit(0)
}

if (primary === 'prev' || (primary === 'nav' && secondary === 'prev')) {
  await dispatchFsm({ type: 'NAV_PREV' })
  process.exit(0)
}

if (primary === 'clear' || (primary === 'nav' && secondary === 'clear')) {
  await dispatchFsm({ type: 'CLEAR_SCREEN' })
  process.exit(0)
}

if (primary === 'goto' || (primary === 'nav' && secondary === 'goto')) {
  const targetIndex = parseInt(primary === 'goto' ? secondary : tertiary, 10)
  if (isNaN(targetIndex)) {
    console.error('Informe o índice numérico do slide. Ex: lumen goto 2')
    process.exit(1)
  }
  await dispatchFsm({ type: 'NAV_GOTO', slideIndex: targetIndex })
  process.exit(0)
}

// 2. Comandos de Pânico (Blackout, Clear-Text, Clear-Bg, Freeze)
if (primary === 'blackout' || (primary === 'panic' && secondary === 'blackout')) {
  await dispatchFsm({ type: 'TOGGLE_PANIC', target: 'blackout' })
  process.exit(0)
}

if (primary === 'clear-text' || (primary === 'panic' && secondary === 'clear-text')) {
  await dispatchFsm({ type: 'TOGGLE_PANIC', target: 'clearText' })
  process.exit(0)
}

if (primary === 'clear-bg' || (primary === 'panic' && secondary === 'clear-bg')) {
  await dispatchFsm({ type: 'TOGGLE_PANIC', target: 'clearBg' })
  process.exit(0)
}

if (primary === 'freeze' || (primary === 'panic' && secondary === 'freeze')) {
  await dispatchFsm({ type: 'TOGGLE_PANIC', target: 'freeze' })
  process.exit(0)
}

if (primary === 'panic' && secondary === 'clear') {
  await dispatchFsm({ type: 'CLEAR_PANIC' })
  process.exit(0)
}

// 3. Status e Telemetria
if (primary === 'status') {
  const result = await requestApi<{ ok: boolean; state: { mode: string; panic: Record<string, boolean> }; clients: number; songsCount: number }>('/api/status')
  if (isJson) {
    console.log(JSON.stringify(result, null, 2))
  } else {
    console.log('\n\x1b[1m--- TELEMETRIA DO LUMENLITE ---\x1b[0m')
    console.log(`Modo Master:       \x1b[36m${result.state.mode}\x1b[0m`)
    console.log(`Clientes WS:       \x1b[32m${result.clients}\x1b[0m`)
    console.log(`Acervo de Hinos:   \x1b[33m${result.songsCount}\x1b[0m`)
    console.log(`Pânico Blackout:   ${result.state.panic.blackout ? '\x1b[31mATIVO\x1b[0m' : 'Inativo'}`)
    console.log(`Pânico ClearText:  ${result.state.panic.clearText ? '\x1b[31mATIVO\x1b[0m' : 'Inativo'}`)
    console.log(`Pânico Freeze:     ${result.state.panic.freeze ? '\x1b[31mATIVO\x1b[0m' : 'Inativo'}\n`)
  }
  process.exit(0)
}

// 4. Domínio de Músicas (song)
if (primary === 'song') {
  if (secondary === 'search') {
    const query = positionals.slice(2).join(' ')
    if (!query) {
      console.error('Uso: lumen song search <termo>')
      process.exit(1)
    }
    const result = await requestApi<{ ok: boolean; data: Array<{ id: string; title: string; artist: string }> }>(
      `/api/songs?q=${encodeURIComponent(query)}`,
    )
    if (isJson) {
      console.log(JSON.stringify(result))
    } else {
      console.log(`\n\x1b[1mHinos encontrados para "${query}":\x1b[0m`)
      result.data.forEach((song) => {
        console.log(`  [\x1b[33m${song.id}\x1b[0m] \x1b[1m${song.title}\x1b[0m - ${song.artist}`)
      })
      console.log()
    }
    process.exit(0)
  }

  if (secondary === 'play') {
    const songId = positionals[2]
    const slideIdx = parseInt(positionals[3] || '0', 10)
    if (!songId) {
      console.error('Uso: lumen song play <songId> [slideIndex]')
      process.exit(1)
    }

    const songData = await requestApi<{ ok: boolean; data: { id: string; title: string; sections: Array<{ lines: string[] }> } }>(
      `/api/songs/${encodeURIComponent(songId)}`,
    )

    const sections = songData.data.sections
    const initialLines = sections[slideIdx]?.lines || sections[0]?.lines || []

    await dispatchFsm({
      type: 'PRESENT_ITEM',
      itemId: songData.data.id,
      mode: 'SONG',
      foreground: {
        content: initialLines,
        slideIndex: slideIdx,
      },
    })
    process.exit(0)
  }
}

// 5. Domínio Bíblico (bible)
if (primary === 'bible') {
  if (secondary === 'quick') {
    const reference = positionals.slice(2).join(' ')
    if (!reference) {
      console.error('Uso: lumen bible quick <referência> (ex: lumen bible quick jo 3 16)')
      process.exit(1)
    }
    const result = await requestApi<{ ok: boolean; data: { reference: string; text: string } }>(
      `/api/bible/quick?ref=${encodeURIComponent(reference)}`,
    )
    if (isJson) {
      console.log(JSON.stringify(result))
    } else {
      console.log(`\n\x1b[1m\x1b[36m${result.data.reference}\x1b[0m`)
      console.log(`"${result.data.text}"\n`)
    }
    process.exit(0)
  }

  if (secondary === 'search') {
    const query = positionals.slice(2).join(' ')
    if (!query) {
      console.error('Uso: lumen bible search <termo>')
      process.exit(1)
    }
    const result = await requestApi<{ ok: boolean; data: Array<{ bookName: string; chapter: number; verse: number; text: string }> }>(
      `/api/bible/search?q=${encodeURIComponent(query)}`,
    )
    if (isJson) {
      console.log(JSON.stringify(result))
    } else {
      console.log(`\n\x1b[1mVersículos encontrados para "${query}":\x1b[0m`)
      result.data.slice(0, 5).forEach((verse) => {
        console.log(`  \x1b[33m${verse.bookName} ${verse.chapter}:${verse.verse}\x1b[0m - ${verse.text}`)
      })
      console.log()
    }
    process.exit(0)
  }
}

// 6. Domínio de Liturgia (liturgy)
if (primary === 'liturgy') {
  if (secondary === 'show') {
    const result = await requestApi<{ ok: boolean; data: { id: string; title: string; items: Array<{ id: string; type: string; title: string }> } }>(
      '/api/liturgy/current',
    )
    if (isJson) {
      console.log(JSON.stringify(result, null, 2))
    } else {
      console.log(`\n\x1b[1mRoteiro de Culto:\x1b[0m \x1b[36m${result.data.title}\x1b[0m (${result.data.id})`)
      result.data.items.forEach((item, index) => {
        console.log(`  ${index + 1}. [\x1b[33m${item.type}\x1b[0m] ${item.title} (ID: ${item.id})`)
      })
      console.log()
    }
    process.exit(0)
  }

  if (secondary === 'quick-text') {
    const text = positionals.slice(2).join(' ')
    if (!text) {
      console.error('Uso: lumen liturgy quick-text "<texto de emergência>"')
      process.exit(1)
    }
    const result = await requestApi<{ ok: boolean; message: string }>('/api/liturgy/quick-text', {
      method: 'POST',
      body: JSON.stringify({ text }),
    })
    if (isJson) {
      console.log(JSON.stringify(result))
    } else {
      console.log(`\x1b[32m✔\x1b[0m Texto de emergência projetado com sucesso: "${text}"`)
    }
    process.exit(0)
  }
}

// 7. Domínio de Automações (automation)
if (primary === 'automation') {
  if (secondary === 'status') {
    const result = await requestApi<{ ok: boolean; data: { graph: { enabled: boolean; nodes: Array<unknown> }; obsStatus: { connected: boolean; host: string; port: number } } }>(
      '/api/automation',
    )
    if (isJson) {
      console.log(JSON.stringify(result, null, 2))
    } else {
      console.log('\n\x1b[1m--- AUTOMAÇÃO LITÚRGICA (DAG) ---\x1b[0m')
      console.log(`Status do Grafo:   ${result.data.graph.enabled ? '\x1b[32mAtivo\x1b[0m' : '\x1b[31mDesativado\x1b[0m'}`)
      console.log(`Total de Nós:      \x1b[33m${result.data.graph.nodes.length}\x1b[0m`)
      console.log(`OBS WebSocket v5:  ${result.data.obsStatus.connected ? `\x1b[32mConectado\x1b[0m (${result.data.obsStatus.host}:${result.data.obsStatus.port})` : '\x1b[31mDesconectado\x1b[0m'}\n`)
    }
    process.exit(0)
  }

  if (secondary === 'trigger') {
    const triggerType = tertiary || 'ON_SLIDE_CHANGE'
    const result = await requestApi<{ ok: boolean; message: string }>('/api/automation/test-trigger', {
      method: 'POST',
      body: JSON.stringify({ triggerType }),
    })
    if (isJson) {
      console.log(JSON.stringify(result))
    } else {
      console.log(`\x1b[32m✔\x1b[0m Gatilho ${triggerType} disparado no motor DAG.`)
    }
    process.exit(0)
  }
}

// 8. Domínio de Atalhos (keymap)
if (primary === 'keymap') {
  if (secondary === 'list') {
    const result = await requestApi<{ ok: boolean; data: { bindings: Array<{ id: string; action: string; combo: string; description: string }> } }>(
      '/api/keymap',
    )
    if (isJson) {
      console.log(JSON.stringify(result, null, 2))
    } else {
      console.log('\n\x1b[1m--- ATALHOS DE TECLADO REGISTRADOS (keybindings.json) ---\x1b[0m')
      result.data.bindings.forEach((binding) => {
        console.log(`  \x1b[33m${binding.combo.padEnd(22)}\x1b[0m → \x1b[1m${binding.action.padEnd(18)}\x1b[0m (${binding.description})`)
      })
      console.log()
    }
    process.exit(0)
  }

  if (secondary === 'reset') {
    const result = await requestApi<{ ok: boolean; message: string }>('/api/keymap/reset', {
      method: 'POST',
    })
    if (isJson) {
      console.log(JSON.stringify(result))
    } else {
      console.log(`\x1b[32m✔\x1b[0m ${result.message}`)
    }
    process.exit(0)
  }
}

// 9. Domínio de Mídias Visuais (media)
if (primary === 'media') {
  if (secondary === 'list' || !secondary) {
    const filterType = tertiary ? tertiary.toLowerCase() : ''
    const endpoint = filterType ? `/api/media?type=${encodeURIComponent(filterType)}` : '/api/media'
    const result = await requestApi<{ ok: boolean; data: Array<{ id: string; name: string; type: string; resolution?: string }> }>(
      endpoint,
    )

    if (isJson) {
      console.log(JSON.stringify(result, null, 2))
    } else {
      console.log('\n\x1b[1m--- ACERVO DE MÍDIAS DO LUMENLITE ---\x1b[0m')
      result.data.forEach((mediaItem) => {
        console.log(`  [\x1b[33m${mediaItem.type.padEnd(5)}\x1b[0m] \x1b[1m${mediaItem.name}\x1b[0m (ID: ${mediaItem.id})`)
      })
      console.log()
    }
    process.exit(0)
  }

  if (secondary === 'bg') {
    const mediaId = tertiary
    if (!mediaId) {
      console.error('Uso: lumen media bg <mediaId>')
      process.exit(1)
    }

    const mediaResult = await requestApi<{ ok: boolean; data: { id: string; path: string; type: string } }>(
      `/api/media/${encodeURIComponent(mediaId)}`,
    )

    const isVideo = mediaResult.data.type === 'VIDEO'
    await dispatchFsm({
      type: 'SET_BACKGROUND',
      background: {
        type: isVideo ? 'video' : 'image',
        source: mediaResult.data.path,
        loop: isVideo,
        muted: true,
      },
    })
    process.exit(0)
  }
}

// 10. Domínio de Áudio Ambiente (audio)
if (primary === 'audio') {
  if (secondary === 'playlists' || !secondary) {
    const result = await requestApi<{ ok: boolean; data: Array<{ id: string; name: string; tracks: Array<{ title: string }> }> }>(
      '/api/media/audio/playlists',
    )
    if (isJson) {
      console.log(JSON.stringify(result, null, 2))
    } else {
      console.log('\n\x1b[1m--- PLAYLISTS LITÚRGICAS DE ÁUDIO ---\x1b[0m')
      result.data.forEach((playlist) => {
        console.log(`  \x1b[33m${playlist.name}\x1b[0m (ID: ${playlist.id}) - ${playlist.tracks.length} faixa(s)`)
      })
      console.log()
    }
    process.exit(0)
  }
}

// 11. Domínio de Palco, Alertas e Cronômetro (stage)
if (primary === 'stage') {
  if (secondary === 'alert') {
    const text = positionals.slice(2).join(' ')
    if (!text) {
      console.error('Uso: lumen stage alert "<texto do aviso>"')
      process.exit(1)
    }
    const result = await requestApi<{ id: string; text: string }>('/api/stage/alert', {
      method: 'POST',
      body: JSON.stringify({
        text,
        durationSeconds: 15,
        position: 'BOTTOM',
        showOnProjector: true,
        showOnStage: true,
        showOnBroadcast: true,
      }),
    })
    if (isJson) {
      console.log(JSON.stringify(result))
    } else {
      console.log(`\x1b[32m✔\x1b[0m Alerta Lower Third enfileirado: "${result.text}"`)
    }
    process.exit(0)
  }

  if (secondary === 'countdown') {
    const seconds = parseInt(tertiary || '300', 10)
    const result = await requestApi('/api/stage/countdown', {
      method: 'POST',
      body: JSON.stringify({
        mode: 'DURATION',
        durationSeconds: isNaN(seconds) ? 300 : seconds,
        label: 'Início do Culto',
        showOnProjectorOverlay: true,
        showOnStage: true,
        isRunning: true,
      }),
    })
    if (isJson) {
      console.log(JSON.stringify(result))
    } else {
      console.log(`\x1b[32m✔\x1b[0m Cronômetro iniciado (${seconds}s).`)
    }
    process.exit(0)
  }

  if (secondary === 'message') {
    const text = positionals.slice(2).join(' ')
    if (!text) {
      console.error('Uso: lumen stage message "<mensagem ao pastor>"')
      process.exit(1)
    }
    const result = await requestApi<{ id: string; text: string }>('/api/stage/message', {
      method: 'POST',
      body: JSON.stringify({
        text,
        urgent: true,
        durationSeconds: 60,
      }),
    })
    if (isJson) {
      console.log(JSON.stringify(result))
    } else {
      console.log(`\x1b[32m✔\x1b[0m Mensagem enviada ao monitor de palco: "${result.text}"`)
    }
    process.exit(0)
  }
}

// 12. Pareamento Remoto LAN (remote)
if (primary === 'remote') {
  if (secondary === 'info' || !secondary) {
    const result = await requestApi<{
      lanIp: string
      port: number
      preacherUrl: string
      operatorUrl: string
      stageUrl: string
    }>('/api/remote/info')
    if (isJson) {
      console.log(JSON.stringify(result, null, 2))
    } else {
      console.log('\n\x1b[1m--- ACESSO REMOTO LAN (QR / TOKENS) ---\x1b[0m')
      console.log(`Pregador (Passador):  \x1b[36m${result.preacherUrl}\x1b[0m`)
      console.log(`Operador Mobile:      \x1b[33m${result.operatorUrl}\x1b[0m`)
      console.log(`Stage View (Palco):   \x1b[32m${result.stageUrl}\x1b[0m\n`)
    }
    process.exit(0)
  }
}

console.error(`\x1b[31mComando desconhecido:\x1b[0m "${primary} ${secondary}". Digite \x1b[1mlumen --help\x1b[0m para ver a lista de comandos.`)
process.exit(1)
