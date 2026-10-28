/**
 * @module @slices/keymap
 * @description Fatia de Mapeamento Declarativo de Atalhos de Teclado.
 *
 * ## Responsabilidades:
 * - Leitura e gravação do mapeamento declarativo `keybindings.json` com validação de esquema.
 * - Captura e normalização de eventos de teclado W3C padrão (`event.code`).
 * - Execução de comandos do console ou despachos da FSM a partir de teclas de atalho.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos tipados para o `@lumen/core` ou requisições para a CLI do daemon.
 * - **Consome:** Configuração de atalhos em disco e foco da interface.
 * - **Exporta para UI:** Hook global `useKeymapListener` e diálogo de ajuda de atalhos (`KeymapHelpDialog`).
 */

export const SLICE_NAME = 'keymap'
