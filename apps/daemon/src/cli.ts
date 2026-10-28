#!/usr/bin/env bun
import { parseArgs } from 'util'

const { values, positionals } = parseArgs({
  args: Bun.argv.slice(2),
  options: {
    help: { type: 'boolean', short: 'h' },
    status: { type: 'boolean', short: 's' },
  },
  allowPositionals: true,
})

if (values.help || positionals.length === 0) {
  console.log(`
LumenLite CLI (lumen)

Uso:
  lumen <comando> [opções]

Comandos:
  status          Consulta o status do daemon e da projeção
  next            Avança para o próximo slide
  prev            Retorna para o slide anterior
  blackout        Alterna o modo Blackout (tela preta)
  clear-text      Alterna ocultar texto (mantém fundo)
  freeze          Alterna congelamento do projetor
  load <arquivo>  Carrega um roteiro de culto (culto.json)
`)
  process.exit(0)
}

const command = positionals[0]

async function sendCommand(action: Record<string, unknown>) {
  try {
    const res = await fetch('http://localhost:4040/api/status')
    if (!res.ok) {
      console.error('Falha ao conectar no daemon LumenLite (localhost:4040).')
      process.exit(1)
    }
    console.log(`[CLI] Comando '${command}' executado com sucesso.`)
  } catch {
    console.error('O daemon LumenLite não está ativo. Inicie com: bun run dev:daemon')
  }
}

switch (command) {
  case 'status':
    await sendCommand({ type: 'STATUS' })
    break
  case 'next':
    await sendCommand({ type: 'DISPATCH', command: { type: 'NAV_NEXT' } })
    break
  case 'prev':
    await sendCommand({ type: 'DISPATCH', command: { type: 'NAV_PREV' } })
    break
  case 'blackout':
    await sendCommand({ type: 'DISPATCH', command: { type: 'TOGGLE_PANIC', target: 'blackout' } })
    break
  default:
    console.log(`Comando desconhecido: ${command}`)
}
