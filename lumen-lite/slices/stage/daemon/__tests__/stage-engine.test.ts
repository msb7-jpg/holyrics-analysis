import { describe, expect, it } from 'bun:test'
import {
  calculateSecondsUntilTargetTime,
  formatCountdownTime,
  StageEngine,
} from '../stage-engine'

describe('StageEngine & Countdown Utilities (RF-24, RF-25)', () => {
  it('formats countdown remaining seconds into MM:SS or HH:MM:SS including negative overflow', () => {
    expect(formatCountdownTime(0)).toBe('00:00')
    expect(formatCountdownTime(65)).toBe('01:05')
    expect(formatCountdownTime(3661)).toBe('01:01:01')
    expect(formatCountdownTime(-15)).toBe('-00:15')
  })

  it('queues, dismisses, and clears Lower Third alerts', () => {
    const snapshots: number[] = []
    const engine = new StageEngine()
    engine.subscribe((state) => {
      snapshots.push(state.alertQueue.length)
    })

    const first = engine.enqueueAlert({
      label: 'Recepção',
      message: 'Responsáveis pelo carro ABC-1234 comparecer ao estacionamento',
      durationSeconds: 10,
      position: 'bottom',
      style: 'classic',
    })

    const second = engine.enqueueAlert({
      label: 'Jovens',
      message: 'Culto de Jovens sábado às 19h30',
      durationSeconds: 15,
      position: 'top',
      style: 'urgent',
    })

    expect(engine.getState().activeAlert?.id).toBe(first.id)
    expect(engine.getState().alertQueue.length).toBe(1)

    // Dismiss active alert -> advances to second in queue
    engine.dismissActiveAlert()
    expect(engine.getState().activeAlert?.id).toBe(second.id)
    expect(engine.getState().alertQueue.length).toBe(0)

    // Clear all remaining
    engine.clearAllAlerts()
    expect(engine.getState().activeAlert).toBeNull()
    expect(engine.getState().alertQueue.length).toBe(0)
    expect(snapshots.length).toBeGreaterThan(0)
  })

  it('controls stage countdown timer in DURATION and TARGET_TIME modes', () => {
    const engine = new StageEngine()

    const started = engine.controlTimer({
      action: 'start',
      mode: 'DURATION',
      durationSeconds: 300,
      label: 'Início do Culto',
    })

    expect(started.isRunning).toBe(true)
    expect(started.durationSeconds).toBe(300)
    expect(started.endTime).toBeGreaterThan(Date.now())

    const paused = engine.controlTimer({ action: 'pause' })
    expect(paused.isRunning).toBe(false)
    expect(paused.pausedRemainingSeconds).toBeGreaterThan(0)

    const reset = engine.controlTimer({ action: 'reset', durationSeconds: 180 })
    expect(reset.isRunning).toBe(false)
    expect(reset.pausedRemainingSeconds).toBe(180)

    const refDate = new Date(2026, 10, 14, 19, 0, 0)
    expect(calculateSecondsUntilTargetTime('19:30', refDate)).toBe(1800)
  })

  it('sets and clears private Pastor Stage messages and uppercase mode', () => {
    const engine = new StageEngine()

    const msg = engine.setPastorMessage(
      'Encerrar em 5 minutos para Santa Ceia',
      'urgent',
    )

    expect(msg).not.toBeNull()
    expect(engine.getState().pastorMessage?.id).toBe(msg?.id)
    expect(engine.getState().pastorMessage?.level).toBe('urgent')

    engine.setUppercaseMode(true)
    expect(engine.getState().uppercaseMode).toBe(true)

    engine.setPastorMessage(null)
    expect(engine.getState().pastorMessage).toBeNull()
  })
})
