/**
 * @module @slices/media
 * @description Fatia de Gestão de Mídias, Áudio Ambiente e Equalizador Gráfico.
 *
 * ## Responsabilidades:
 * - Reprodução de trilhas de áudio ambiente pré/pós-culto com suporte a crossfade suave.
 * - Equalizador gráfico sonoro com nós de filtragem baseados na Web Audio API (`BiquadFilterNode`).
 * - Geração e armazenamento assíncrono em cache de miniaturas de imagens e vídeos locais.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos de fade de volume durante ações de pânico (Blackout).
 * - **Consome:** Eventos de interrupção e fim de itens da liturgia.
 * - **Exporta para UI:** Gaveta de controle de áudio ambiente e seletor de loops em `apps/ui`.
 */

export const SLICE_NAME = 'media'
