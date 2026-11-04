import { describe, it, expect } from 'bun:test'
import {
  KeymapConfigSchema,
  DEFAULT_KEYMAP_CONFIG,
  matchesKeyCombo,
  formatKeyComboDisplay,
  type KeyComboEventLike,
} from '../keymap-contracts'

describe('Keymap Contracts & Matcher (slices/keymap)', () => {
  it('valida a configuração padrão com Zod v4', () => {
    const parseResult = KeymapConfigSchema.safeParse(DEFAULT_KEYMAP_CONFIG)
    expect(parseResult.success).toBe(true)
  })

  it('detecta tecla Space isolada', () => {
    const spaceEvent: KeyComboEventLike = { key: ' ', code: 'Space' }
    expect(matchesKeyCombo(spaceEvent, 'Space')).toBe(true)

    const shiftSpaceEvent: KeyComboEventLike = { key: ' ', code: 'Space', shiftKey: true }
    expect(matchesKeyCombo(shiftSpaceEvent, 'Space')).toBe(false)
  })

  it('detecta Backspace isolado', () => {
    const backspaceEvent: KeyComboEventLike = { key: 'Backspace', code: 'Backspace' }
    expect(matchesKeyCombo(backspaceEvent, 'Backspace')).toBe(true)
  })

  it('detecta CommandOrControl+k tanto no Mac (metaKey) quanto no PC (ctrlKey)', () => {
    const macEvent: KeyComboEventLike = { key: 'k', code: 'KeyK', metaKey: true }
    expect(matchesKeyCombo(macEvent, 'CommandOrControl+k')).toBe(true)

    const pcEvent: KeyComboEventLike = { key: 'k', code: 'KeyK', ctrlKey: true }
    expect(matchesKeyCombo(pcEvent, 'CommandOrControl+k')).toBe(true)

    const rawKEvent: KeyComboEventLike = { key: 'k', code: 'KeyK' }
    expect(matchesKeyCombo(rawKEvent, 'CommandOrControl+k')).toBe(false)
  })

  it('detecta teclas de função como F1 e F2', () => {
    const f1Event: KeyComboEventLike = { key: 'F1', code: 'F1' }
    expect(matchesKeyCombo(f1Event, 'F1')).toBe(true)
    expect(matchesKeyCombo(f1Event, 'F2')).toBe(false)
  })

  it('formata combinações para exibição amigável', () => {
    expect(formatKeyComboDisplay('CommandOrControl+k', true)).toBe('⌘+k')
    expect(formatKeyComboDisplay('CommandOrControl+k', false)).toBe('Ctrl+k')
    expect(formatKeyComboDisplay('Space')).toBe('Espaço')
    expect(formatKeyComboDisplay('ArrowRight')).toBe('→')
  })
})
