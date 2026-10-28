/**
 * @module @slices/playback
 * @description Fatia de Playback Musical Sincronizado com Letras.
 *
 * ## Responsabilidades:
 * - Execução de faixas de playback sincronizadas com estrofes e slides individuais.
 * - Sincronização em tempo real baseada no relógio de alta precisão da Web Audio API.
 * - Editor visual de marcação de tempos de troca de estrofe por segundo/milissegundo.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos `NAV_GOTO` sincronizados para o `@lumen/core`.
 * - **Consome:** Estruturas de canção e áudios de `@slices/songs` e `@slices/media`.
 * - **Exporta para UI:** Barra de controle de playback e modal de edição de minutagem de slides.
 */

export const SLICE_NAME = 'playback'
