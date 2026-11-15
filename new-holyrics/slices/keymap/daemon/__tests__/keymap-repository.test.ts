import { describe, it, expect, afterAll } from 'bun:test'
import { join } from 'node:path'
import { existsSync, unlinkSync } from 'node:fs'
import { KeymapRepository } from '../keymap-repository'
import { DEFAULT_KEYMAP_CONFIG } from '../../contracts/keymap-contracts'

describe('KeymapRepository (slices/keymap/daemon)', () => {
  const testJsonPath = join(process.cwd(), 'slices/keymap/daemon/__tests__/test-keybindings.json')

  afterAll(() => {
    if (existsSync(testJsonPath)) {
      unlinkSync(testJsonPath)
    }
  })

  it('cria automaticamente o arquivo keybindings.json padrão se inexistente', () => {
    const repo = new KeymapRepository(testJsonPath)
    const config = repo.load()

    expect(config.bindings.length).toBe(DEFAULT_KEYMAP_CONFIG.bindings.length)
    expect(existsSync(testJsonPath)).toBe(true)
  })

  it('salva alterações de atalhos e as recupera com persistência atômica', () => {
    const repo = new KeymapRepository(testJsonPath)
    const current = repo.load()

    const updated = {
      ...current,
      bindings: [
        ...current.bindings,
        {
          id: 'custom-blackout-b',
          action: 'PANIC_BLACKOUT' as const,
          combo: 'b',
          scope: 'global' as const,
          description: 'Custom Blackout B key',
        },
      ],
    }

    repo.save(updated)
    const loaded = repo.load()
    expect(loaded.bindings.some((b) => b.id === 'custom-blackout-b')).toBe(true)
  })

  it('restaura configurações de fábrica com resetToDefaults', () => {
    const repo = new KeymapRepository(testJsonPath)
    const restored = repo.resetToDefaults()

    expect(restored.bindings.length).toBe(DEFAULT_KEYMAP_CONFIG.bindings.length)
    expect(restored.bindings.some((b) => b.id === 'custom-blackout-b')).toBe(false)
  })
})
