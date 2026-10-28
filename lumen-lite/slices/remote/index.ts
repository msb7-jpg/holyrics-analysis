/**
 * @module @slices/remote
 * @description Fatia de Controle Remoto via Dispositivos Móveis e Pareamento Local.
 *
 * ## Responsabilidades:
 * - Servir a interface web móvel do pregador (`/remote/preacher`) com foco nas anotações e troca de slides.
 * - Servir o console web móvel do operador volante (`/remote/operator`) com botões de pânico e bíblia rápida.
 * - Pareamento instantâneo via QR Code na tela sem necessidade de instalar apps de lojas móveis.
 * - Controle de acesso por papéis (RBAC leve) com tokens temporários de segurança.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos litúrgicos autorizados para o `apps/daemon` via WebSocket.
 * - **Consome:** Estado em tempo real do `@lumen/core` para refletir no smartphone do pastor.
 * - **Exporta para UI:** Modal de geração e exibição de QR Code de pareamento (`RemotePairingModal`).
 */

export const SLICE_NAME = 'remote'
