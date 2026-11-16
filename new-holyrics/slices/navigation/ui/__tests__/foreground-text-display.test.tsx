/**
 * @module @slices/navigation/ui/__tests__/foreground-text-display.test
 * @description Testes unitários para renderização de texto e Capa (Slide 0) no ForegroundTextDisplay.
 */

import { describe, expect, it } from 'bun:test'
import { renderToString } from 'react-dom/server'
import type { ForegroundLayer } from '@lumen/core'
import { ForegroundTextDisplay } from '../components/ForegroundTextDisplay'

describe('ForegroundTextDisplay', () => {
  it('deve renderizar a Capa da música centralizada com título imponente e artista quando reference for CAPA', () => {
    const coverForeground: ForegroundLayer = {
      title: 'Bondade de Deus',
      reference: 'CAPA',
      content: ['Bondade de Deus'],
      secondaryContent: ['Isaías Saad'],
      slideIndex: 0,
      totalSlides: 5,
    }

    const html = renderToString(
      <ForegroundTextDisplay foreground={coverForeground} mode="SONG" />
    )

    expect(html).toContain('Bondade de Deus')
    expect(html).toContain('Isaías Saad')
    expect(html).toContain('text-sky-300')
    expect(html).toContain('1 / 5')
  })

  it('deve renderizar estrofe normal quando reference não for CAPA', () => {
    const stanzaForeground: ForegroundLayer = {
      title: 'Bondade de Deus',
      reference: '',
      content: ['Te amo, ó Deus', 'Tua graça nunca falha'],
      slideIndex: 1,
      totalSlides: 5,
    }

    const html = renderToString(
      <ForegroundTextDisplay foreground={stanzaForeground} mode="SONG" />
    )

    expect(html).toContain('Te amo, ó Deus')
    expect(html).toContain('Tua graça nunca falha')
    expect(html).toContain('2 / 5')
  })
})
