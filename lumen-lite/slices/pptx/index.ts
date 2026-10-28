/**
 * @module @slices/pptx
 * @description Fatia de Apresentações PowerPoint (PPTX) e PDF sem dependência do Microsoft Office.
 *
 * ## Responsabilidades:
 * - Leitura e extração de slides de arquivos `.pptx` em memória usando streaming ZIP (`fflate`).
 * - Extração puramente funcional das anotações pastorais (`notesSlides.xml`) via `fast-xml-parser`.
 * - Conversão acelerada de slides em imagens WebP de alta fidelidade para projeção.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos `PRESENT_ITEM` carregando as lâminas de apresentação no `@lumen/core`.
 * - **Consome:** Arquivos locais carregados pelo operador ou arrastados para a liturgia.
 * - **Exporta para UI:** Grade de miniaturas da apresentação (`DeckSlideGrid`) e notas para o púlpito.
 */

export const SLICE_NAME = 'pptx'
