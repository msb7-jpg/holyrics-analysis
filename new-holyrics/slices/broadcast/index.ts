/**
 * @module @slices/broadcast
 * @description Fatia de Transmissão ao Vivo e Overlay Transparente para Softwares de Live Stream.
 *
 * ## Responsabilidades:
 * - Servir rota web com canal alfa transparente (`/broadcast`) para Browser Source no OBS Studio, vMix ou Wirecast.
 * - Renderização de Lower Thirds animados e legendas dinâmicas sobrepostas à transmissão ao vivo.
 * - Gerenciamento de temas visuais específicos para transmissão com contraste adaptativo.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Notificações de status de conexão de transmissão.
 * - **Consome:** `ScreenState` sincronizado via WebSocket pelo `apps/daemon`.
 * - **Exporta para UI:** Rota web `/broadcast` para integração como Browser Source no OBS Studio.
 */

export const SLICE_NAME = 'broadcast'
