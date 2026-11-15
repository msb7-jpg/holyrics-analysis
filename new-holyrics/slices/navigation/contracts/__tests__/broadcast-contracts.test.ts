import { describe, it, expect } from 'bun:test'
import { StageMonitorStateSchema, BroadcastSettingsSchema, PreacherRemoteStateSchema } from '../broadcast-contracts'

describe('Broadcast & Stage Contracts', () => {
  it('valida um estado de monitor de palco completo', () => {
    const validState = {
      currentItemId: 'song-123',
      currentSlide: {
        title: 'Quão Grande És Tu',
        content: ['Senhor, meu Deus', 'Quando eu, maravilhado'],
        slideIndex: 0,
        totalSlides: 4,
      },
      nextSlidePreview: {
        title: 'Vem, Esta É a Hora',
        content: ['Vem, esta é a hora da adoração'],
      },
      clock: '20:30',
      timer: {
        label: 'Contagem Regressiva',
        endTime: Date.now() + 600000,
        isRunning: true,
      },
      pastorAlert: null,
    }
    const result = StageMonitorStateSchema.safeParse(validState)
    expect(result.success).toBe(true)
  })

  it('rejeita um estado de monitor de palco com formato de relógio inválido', () => {
    const invalidState = {
      clock: '20:30:05', // formato inválido
    }
    const result = StageMonitorStateSchema.partial().safeParse(invalidState)
    expect(result.success).toBe(false)
  })

  it('valida as configurações padrão de broadcast', () => {
    const validSettings = {
      lowerThirdStyle: 'minimal',
      textAlignment: 'left',
      fontSize: 72,
      backgroundOpacity: 0.5,
    }
    const result = BroadcastSettingsSchema.safeParse(validSettings)
    expect(result.success).toBe(true)
  })

  it('rejeita configurações de broadcast com valores fora do limite', () => {
    const invalidSettings = { fontSize: 200 } // Fora do limite max(120)
    const result = BroadcastSettingsSchema.partial().safeParse(invalidSettings)
    expect(result.success).toBe(false)
  })

  it('valida o estado do púlpito do pregador', () => {
    const validState = {
      notes: 'Lembrar da ilustração sobre a ovelha perdida.',
      timer: null,
      clock: '19:45',
    }
    const result = PreacherRemoteStateSchema.safeParse(validState)
    expect(result.success).toBe(true)
  })
})
