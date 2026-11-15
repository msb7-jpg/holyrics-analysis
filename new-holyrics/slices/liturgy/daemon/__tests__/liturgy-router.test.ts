import { describe, it, expect } from 'bun:test'
import { FsmEngine } from '@lumen/core'
import { LiturgyRepository } from '../liturgy-repository'
import { handleLiturgyRoutes } from '../routes/liturgy-router'
import { existsSync, unlinkSync } from 'node:fs'
import { resolve } from 'node:path'
import type { ApiContext } from '../../../../apps/daemon/src/api/context'

describe('Liturgy Router (daemon)', () => {
  const testFilePath = resolve(import.meta.dir, 'router-test-culto.json')
  const fsm = new FsmEngine()
  const liturgyRepo = new LiturgyRepository(testFilePath)

  const fakeContext = {
    fsm,
    liturgyRepo,
  } as unknown as ApiContext

  it('GET /api/liturgy/current retorna o roteiro ativo', async () => {
    const request = new Request('http://localhost:4040/api/liturgy/current')
    const url = new URL(request.url)
    const response = await handleLiturgyRoutes(request, url, fakeContext)

    expect(response).toBeDefined()
    expect(response?.status).toBe(200)

    const payload = (await response?.json()) as { ok: boolean; data: { title: string; items: unknown[] } }
    expect(payload.ok).toBe(true)
    expect(payload.data.title).toBeDefined()
    expect(Array.isArray(payload.data.items)).toBe(true)
  })

  it('POST /api/liturgy/quick-text dispara texto avulso na FSM', async () => {
    const request = new Request('http://localhost:4040/api/liturgy/quick-text', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        title: 'Recado Pastoral',
        text: 'Atenção pais das crianças da salinha 2.',
        category: 'ALERT',
      }),
    })
    const url = new URL(request.url)
    const response = await handleLiturgyRoutes(request, url, fakeContext)

    expect(response?.status).toBe(200)
    const payload = (await response?.json()) as { ok: boolean; data: { lines: string[] } }
    expect(payload.ok).toBe(true)
    expect(payload.data.lines).toContain('Atenção pais das crianças da salinha 2.')

    const screenState = fsm.getState()
    expect(screenState.foreground.title).toBe('Recado Pastoral')
    expect(screenState.foreground.content).toContain('Atenção pais das crianças da salinha 2.')
  })

  it('POST /api/liturgy/item adiciona novo item e DELETE remove pelo ID', async () => {
    // 1. Adicionar item
    const newItem = {
      id: 'test-hitem-custom',
      type: 'TEXT',
      title: 'Momento de Intercessão',
      content: ['Oremos pelos enfermos e missionários.'],
      category: 'PASTORAL_NOTE',
    }

    const addRequest = new Request('http://localhost:4040/api/liturgy/item', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ item: newItem }),
    })
    const addUrl = new URL(addRequest.url)
    const addResponse = await handleLiturgyRoutes(addRequest, addUrl, fakeContext)

    expect(addResponse?.status).toBe(200)

    // 2. Remover item
    const delRequest = new Request('http://localhost:4040/api/liturgy/item/test-hitem-custom', {
      method: 'DELETE',
    })
    const delUrl = new URL(delRequest.url)
    const delResponse = await handleLiturgyRoutes(delRequest, delUrl, fakeContext)

    expect(delResponse?.status).toBe(200)

    if (existsSync(testFilePath)) {
      unlinkSync(testFilePath)
    }
  })
})
