/**
 * @module @slices/stage
 * @description Fatia do Monitor de Retorno para Púlpito (Stage View), Cronômetros e Alertas.
 *
 * ## Responsabilidades:
 * - Renderização da rota de alto contraste `/stage` para TVs e tablets no púlpito.
 * - Exibição do slide atual, próximo slide em destaque e notas do orador.
 * - Motor de cronômetro de pregação e contagem regressiva pré-culto sincronizados via WebSocket.
 * - Fila e exibição de alertas de emergência (pais ao berçário, veículos bloqueando garagem).
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos de controle de cronômetro para o `apps/daemon`.
 * - **Consome:** `ScreenState` do `@lumen/core` para prévia do próximo slide.
 * - **Exporta para UI:** Rota web `/stage` e gaveta de disparo de alertas no console do operador.
 */

export const SLICE_NAME = 'stage'
