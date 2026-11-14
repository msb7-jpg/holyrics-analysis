/**
 * @module @slices/pptx
 * @description Fatia de Apresentações PowerPoint (PPTX) e PDF sem dependência do Microsoft Office (RF-21).
 *
 * ## Responsabilidades:
 * - Leitura e extração de slides de arquivos `.pptx` em memória usando streaming ZIP (`fflate`).
 * - Extração puramente funcional das anotações pastorais (`notesSlides.xml`) via `fast-xml-parser`.
 * - Sincronização automática das notas do orador com `/stage` e `/remote/preacher`.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos `PRESENT_ITEM` carregando as lâminas de apresentação no `@lumen/core`.
 * - **Consome:** Arquivos locais carregados pelo operador.
 * - **Exporta para UI:** `PptxDeckModal` e `pptxService`.
 */

export const SLICE_NAME = 'pptx'

export type {
  PptxSlide,
  PptxPresentation,
} from './contracts/pptx-contracts'

export {
  PptxSlideSchema,
  PptxPresentationSchema,
} from './contracts/pptx-contracts'

export {
  collectXmlParagraphLines,
  extractPptxFromBuffer,
  extractPdfFromBuffer,
} from './daemon/pptx-extractor'

export { PptxRepository } from './daemon/pptx-repository'
export { handlePptxRoutes } from './daemon/routes/pptx-router'
export { pptxService } from './ui/services/pptx-service'
export { PptxDeckModal } from './ui/components/PptxDeckModal'
