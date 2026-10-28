/**
 * @module @slices/hooks
 * @description Fatia do Motor de Automação por Nós Baseado em Grafo Direcionado Acíclico (DAG).
 *
 * ## Responsabilidades:
 * - Execução desacoplada de pipelines de automação externa disparados por transições litúrgicas.
 * - Integração nativa com mesas de iluminação DMX (HTTP Lumikit com timeout estrito de 500ms).
 * - Integração nativa com OBS Studio via WebSocket RPC v5 (`obs-websocket-js`).
 * - Persistência do arquivo declarativo `hooks.json` com validação Zod e JSON Schema.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos HTTP e WebSocket para dispositivos e softwares externos na rede local.
 * - **Consome:** Eventos `STATE_TRANSITION` emitidos pelo `LumenEventBus` do `@lumen/core`.
 * - **Exporta para UI:** Editor visual de automação em blocos arrastáveis (`@xyflow/react`) em `apps/ui`.
 */

export * from './contracts/hook-contracts'
