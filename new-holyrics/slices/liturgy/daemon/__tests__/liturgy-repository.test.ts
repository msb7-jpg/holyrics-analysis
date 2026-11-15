import { describe, it, expect, beforeEach, afterEach } from 'bun:test'
import { existsSync, unlinkSync } from 'node:fs'
import { resolve } from 'node:path'
import { LiturgyRepository } from '../liturgy-repository'
import type { LiturgyFile, LiturgyItem } from '../../contracts/liturgy-schema'

describe('LiturgyRepository (daemon)', () => {
  const testFilePath = resolve(import.meta.dir, 'test-culto.json')

  beforeEach(() => {
    if (existsSync(testFilePath)) {
      unlinkSync(testFilePath)
    }
  })

  afterEach(() => {
    if (existsSync(testFilePath)) {
      unlinkSync(testFilePath)
    }
  })

  it('cria a liturgia inicial limpa no primeiro boot se o arquivo não existir', () => {
    const repo = new LiturgyRepository(testFilePath)
    const liturgy = repo.getCurrentLiturgy()

    expect(liturgy).toBeDefined()
    expect(liturgy.title).toContain('Culto')
    expect(liturgy.items.length).toBe(0)
    expect(existsSync(testFilePath)).toBe(true)
  })

  it('salva e recarrega uma liturgia customizada de forma atômica', () => {
    const repo = new LiturgyRepository(testFilePath)
    const customLiturgy: LiturgyFile = {
      id: 'culto-especial',
      title: 'Culto de Missões',
      date: '2026-11-15',
      items: [
        {
          id: 'item-1',
          type: 'BIBLE',
          title: 'Grande Comissão',
          reference: 'mt 28 19-20',
          version: 'ACF',
        },
      ],
      hooksEnabled: true,
    }

    repo.saveLiturgy(customLiturgy)
    const loaded = repo.getCurrentLiturgy()

    expect(loaded.id).toBe('culto-especial')
    expect(loaded.title).toBe('Culto de Missões')
    expect(loaded.items.length).toBe(1)
    expect(loaded.items[0].type).toBe('BIBLE')
  })

  it('reordena os itens corretamente mantendo a consistência dos dados', () => {
    const repo = new LiturgyRepository(testFilePath)
    repo.addItem({
      id: 'item-a',
      type: 'COUNTDOWN',
      title: 'Contagem',
      targetDurationSeconds: 300,
    })
    repo.addItem({
      id: 'item-b',
      type: 'SONG',
      title: 'Hino 1',
      songId: 'song-1',
    })
    repo.addItem({
      id: 'item-c',
      type: 'BIBLE',
      title: 'Leitura',
      reference: 'jo 3 16',
      version: 'ACF',
    })

    const initial = repo.getCurrentLiturgy()
    const itemIds = initial.items.map((item) => item.id)
    expect(itemIds.length).toBe(3)

    // Inverte a ordem
    const reversedIds = [...itemIds].reverse()
    const reordered = repo.reorderItems(reversedIds)

    expect(reordered.items.map((item) => item.id)).toEqual(reversedIds)
  })

  it('adiciona e remove itens polimórficos no roteiro', () => {
    const repo = new LiturgyRepository(testFilePath)
    const newItem: LiturgyItem = {
      id: 'item-quick-text',
      type: 'TEXT',
      title: 'Aviso de Veículo',
      content: ['Veículo placa ABC-1234 com farol aceso.'],
      category: 'ALERT',
    }

    const withAdded = repo.addItem(newItem, 0)
    expect(withAdded.items[0].id).toBe('item-quick-text')
    expect(withAdded.items[0].type).toBe('TEXT')

    const withRemoved = repo.removeItem('item-quick-text')
    expect(withRemoved.items.some((item) => item.id === 'item-quick-text')).toBe(false)
  })
})
