import { describe, expect, it } from 'bun:test'
import { RemoteAuthManager } from '../remote-auth'

describe('RemoteAuthManager (RF-26A, RF-26B)', () => {
  it('validates tokens and enforces role permissions between preacher and operator', () => {
    const manager = new RemoteAuthManager(4040, 3000)
    const info = manager.getPairingInfo()

    expect(info.preacherToken.startsWith('prc_')).toBe(true)
    expect(info.operatorToken.startsWith('opr_')).toBe(true)
    expect(info.preacherToken).not.toBe(info.operatorToken)

    // Missing or invalid token
    expect(manager.authorizeCommand(null, { type: 'NAV_NEXT' }).authorized).toBe(false)
    expect(manager.authorizeCommand('invalid', { type: 'NAV_NEXT' }).authorized).toBe(false)

    // Preacher can only navigate slides
    expect(manager.authorizeCommand(info.preacherToken, { type: 'NAV_NEXT' }).authorized).toBe(true)
    expect(manager.authorizeCommand(info.preacherToken, { type: 'NAV_PREV' }).authorized).toBe(true)
    expect(
      manager.authorizeCommand(info.preacherToken, { type: 'NAV_GOTO', slideIndex: 1 }).authorized,
    ).toBe(true)
    expect(
      manager.authorizeCommand(info.preacherToken, {
        type: 'TOGGLE_PANIC',
        target: 'blackout',
      }).authorized,
    ).toBe(false)

    // Operator can execute any FSM action
    expect(manager.authorizeCommand(info.operatorToken, { type: 'NAV_NEXT' }).authorized).toBe(true)
    expect(
      manager.authorizeCommand(info.operatorToken, {
        type: 'TOGGLE_PANIC',
        target: 'blackout',
      }).authorized,
    ).toBe(true)

    // Token rotation invalidates old tokens
    const oldPreacher = info.preacherToken
    const rotated = manager.rotateTokens()
    expect(rotated.preacherToken).not.toBe(oldPreacher)
    expect(manager.authorizeCommand(oldPreacher, { type: 'NAV_NEXT' }).authorized).toBe(false)
    expect(manager.authorizeCommand(rotated.preacherToken, { type: 'NAV_NEXT' }).authorized).toBe(
      true,
    )
  })
})
