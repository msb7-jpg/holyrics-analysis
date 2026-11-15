import { describe, expect, it, afterAll } from 'bun:test'
import { existsSync, unlinkSync } from 'node:fs'
import { resolve } from 'node:path'
import { zipSync, strToU8 } from 'fflate'
import { extractPptxFromBuffer } from '../pptx-extractor'
import { PptxRepository } from '../pptx-repository'

const TEST_PPTX_JSON = resolve(import.meta.dir, 'test-pptx-decks.json')

describe('PPTX Native Extractor & Repository (RF-21)', () => {
  afterAll(() => {
    if (existsSync(TEST_PPTX_JSON)) {
      unlinkSync(TEST_PPTX_JSON)
    }
  })

  it('extracts slides and speaker notes from an in-memory .pptx OpenXML archive', () => {
    const slide1Xml = `<?xml version="1.0" encoding="UTF-8"?>
      <p:sld xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:p="http://schemas.openxmlformats.org/presentationml/2006/main">
        <p:cSld>
          <p:spTree>
            <p:sp>
              <p:txBody>
                <a:p><a:r><a:t>Sermão: A Graça Soberana</a:t></a:r></a:p>
                <a:p><a:r><a:t>Efésios 2:8-9</a:t></a:r></a:p>
              </p:txBody>
            </p:sp>
          </p:spTree>
        </p:cSld>
      </p:sld>`

    const notes1Xml = `<?xml version="1.0" encoding="UTF-8"?>
      <p:notes xmlns:a="http://schemas.openxmlformats.org/drawingml/2006/main" xmlns:p="http://schemas.openxmlformats.org/presentationml/2006/main">
        <p:cSld>
          <p:spTree>
            <p:sp>
              <p:txBody>
                <a:p><a:r><a:t>Lembrar de citar a reforma protestante.</a:t></a:r></a:p>
              </p:txBody>
            </p:sp>
          </p:spTree>
        </p:cSld>
      </p:notes>`

    const pptxBuffer = zipSync({
      'ppt/slides/slide1.xml': strToU8(slide1Xml),
      'ppt/notesSlides/notesSlide1.xml': strToU8(notes1Xml),
    })

    const deck = extractPptxFromBuffer(pptxBuffer, 'Sermao_Domingo.pptx')
    expect(deck.title).toBe('Sermao_Domingo')
    expect(deck.slides.length).toBe(1)
    expect(deck.slides[0]?.textLines).toContain('Sermão: A Graça Soberana')
    expect(deck.slides[0]?.textLines).toContain('Efésios 2:8-9')
    expect(deck.slides[0]?.speakerNotes).toContain('Lembrar de citar a reforma protestante.')

    const repository = new PptxRepository(TEST_PPTX_JSON)
    const saved = repository.savePresentation(deck)
    expect(saved.id).toBe(deck.id)

    const loaded = repository.getById(saved.id)
    expect(loaded?.slides.length).toBe(1)
    expect(loaded?.slides[0]?.speakerNotes).toContain('reforma protestante')
  })
})
