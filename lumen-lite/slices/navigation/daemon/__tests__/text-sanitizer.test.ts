import { describe, it, expect } from 'bun:test'
import { sanitizeLine, sanitizeSlideLines } from '../text-sanitizer'

describe('Text Sanitizer (slices/navigation)', () => {
  it('remove linhas que sejam apenas tags de estrofe ([CORO], [V1], [PONTE], etc.)', () => {
    expect(sanitizeLine('[CORO]')).toBe('')
    expect(sanitizeLine('[V1]')).toBe('')
    expect(sanitizeLine('[VERSO 1]')).toBe('')
    expect(sanitizeLine('[Ponte]')).toBe('')
    expect(sanitizeLine('[Intro]')).toBe('')
    expect(sanitizeLine('[Final]')).toBe('')
    expect(sanitizeLine('  [Chorus]  ')).toBe('')
  })

  it('preserva linhas normais de letras e orações', () => {
    expect(sanitizeLine('Deus enviou Seu Filho amado')).toBe('Deus enviou Seu Filho amado')
    expect(sanitizeLine('Porque Ele vive, posso crer no amanhã')).toBe('Porque Ele vive, posso crer no amanhã')
  })

  it('limpa tags de estrofe inline que estejam no início de uma frase', () => {
    expect(sanitizeLine('[CORO] Porque Ele vive, posso crer no amanhã')).toBe('Porque Ele vive, posso crer no amanhã')
    expect(sanitizeLine('[V2] Mas um dia eu cruzarei os rios')).toBe('Mas um dia eu cruzarei os rios')
  })

  it('higieniza um conjunto de linhas removendo as tags de seções e linhas vazias', () => {
    const originalSlide = [
      '[CORO]',
      'Porque Ele vive, posso crer no amanhã',
      'Porque Ele vive, temor não há',
      '',
      'Mas eu bem sei, eu sei, que a minha vida',
      'Está nas mãos do meu Jesus, que vivo está',
    ]

    const result = sanitizeSlideLines(originalSlide)

    expect(result.length).toBe(4)
    expect(result[0]).toBe('Porque Ele vive, posso crer no amanhã')
    expect(result[1]).toBe('Porque Ele vive, temor não há')
    expect(result[2]).toBe('Mas eu bem sei, eu sei, que a minha vida')
    expect(result[3]).toBe('Está nas mãos do meu Jesus, que vivo está')
  })
})
