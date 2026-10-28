import { describe, it, expect } from 'bun:test'
import { validateLiturgy } from '../preflight-validator'

describe('Liturgy Preflight Validator', () => {
  it('valida com sucesso um roteiro de culto correto', () => {
    const validCulto = {
      id: 'culto-2026-10-25',
      title: 'Culto de Celebração',
      date: '2026-10-25T19:00:00Z',
      items: [
        {
          id: 'item-1',
          type: 'SONG',
          title: 'Porque Ele Vive',
          songId: 'song-1042',
        },
        {
          id: 'item-2',
          type: 'BIBLE',
          title: 'Leitura',
          reference: 'João 3:16',
          version: 'NVI',
        },
      ],
    }

    const res = validateLiturgy(validCulto)
    expect(res.valid).toBe(true)
    expect(res.itemCount).toBe(2)
    expect(res.errors.length).toBe(0)
  })

  it('rejeita arquivo com campos obrigatórios ausentes', () => {
    const invalidCulto = {
      title: 'Sem ID nem Itens',
    }

    const res = validateLiturgy(invalidCulto)
    expect(res.valid).toBe(false)
    expect(res.errors.length).toBeGreaterThan(0)
  })
})
