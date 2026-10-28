/**
 * @module @slices/navigation
 * @description Fatia de Transporte de Slides, Controle do Telão e Ações de Emergência (Pânico).
 *
 * ## Responsabilidades:
 * - Controle de avanço, retrocesso e salto direto entre estrofes e slides.
 * - Gestão de cortes de emergência: Blackout, Clear Text, Clear Background e Freeze.
 * - Gerenciamento de saídas de vídeo multi-monitor (Interface do Operador vs. Saída HDMI sem bordas).
 * - Renderização do telão com aceleração 100% por GPU e auto-fit dinâmico via CSS.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Ações `NAV_NEXT`, `NAV_PREV`, `NAV_GOTO` e `TOGGLE_PANIC` para o `@lumen/core`.
 * - **Consome:** `ScreenState` atualizado emitido via WebSocket pelo `apps/daemon`.
 * - **Exporta para UI:** Barra de pânico (`PanicToolbar`), miniatura 16:9 (`LivePreviewCard`) e viewport do projetor HDMI (`/projector`).
 */

export * from './contracts/navigation-contracts'
