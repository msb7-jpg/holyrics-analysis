import { describe, it, expect, afterAll } from 'bun:test'
import { join } from 'node:path'
import { existsSync, unlinkSync, writeFileSync } from 'node:fs'
import { DisplayRepository, DEFAULT_BROADCAST_SETTINGS } from '../display-repository'

describe('DisplayRepository', () => {
  const testConfigPath = join(process.cwd(), 'test-broadcast-settings.json')

  afterAll(() => {
    // Garante que o arquivo de teste seja limpo após a execução
    if (existsSync(testConfigPath)) {
      unlinkSync(testConfigPath)
    }
    const tempPath = `${testConfigPath}.tmp`
    if (existsSync(tempPath)) {
      unlinkSync(tempPath)
    }
  })

  it('deve carregar as configurações padrão se o arquivo não existir', () => {
    const repo = new DisplayRepository(testConfigPath)
    const settings = repo.getBroadcastSettings()
    expect(settings).toEqual(DEFAULT_BROADCAST_SETTINGS)
  })

  it('deve salvar e persistir as configurações de forma atômica', () => {
    const repo = new DisplayRepository(testConfigPath)
    const newSettings = {
      lowerThirdStyle: 'banner' as const,
      textAlignment: 'left' as const,
      fontFamily: 'Roboto',
      fontSize: 60,
      backgroundOpacity: 1,
    }

    repo.saveBroadcastSettings(newSettings)

    // Verifica se o arquivo foi criado
    expect(existsSync(testConfigPath)).toBe(true)

    // Cria uma nova instância para forçar a leitura do disco
    const reloadedRepo = new DisplayRepository(testConfigPath)
    const loadedSettings = reloadedRepo.getBroadcastSettings()

    expect(loadedSettings).toEqual(newSettings)
  })

  it('deve retornar as configurações padrão se o arquivo JSON for inválido', () => {
    // Simula um arquivo corrompido
    writeFileSync(testConfigPath, '{"fontSize": 200, }') // JSON inválido com vírgula extra

    const repo = new DisplayRepository(testConfigPath)
    const settings = repo.loadBroadcastSettings()

    expect(settings).toEqual(DEFAULT_BROADCAST_SETTINGS)
  })
})
