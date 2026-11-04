import { describe, it, expect } from 'bun:test'
import { httpClient, ApiError } from '../http-client'

describe('httpClient (Ky Service Wrapper)', () => {
  it('instancia o cliente HTTP corretamente', () => {
    expect(httpClient).toBeDefined()
    expect(typeof httpClient.get).toBe('function')
    expect(typeof httpClient.post).toBe('function')
  })

  it('ApiError formata mensagem e status code', () => {
    const error = new ApiError('Not found', 404, { detail: 'missing' })
    expect(error.message).toBe('Not found')
    expect(error.statusCode).toBe(404)
    expect(error.name).toBe('ApiError')
    expect(error.originalData).toEqual({ detail: 'missing' })
  })
})
