/**
 * @module @slices/remote
 * @description Fatia de Controle Remoto via Dispositivos Móveis e Pareamento Local (RF-26A, RF-26B, RF-26C).
 *
 * ## Responsabilidades:
 * - Servir a interface web móvel do pregador (`/remote/preacher`) com foco nas anotações e troca de slides.
 * - Servir o console web móvel do operador volante (`/remote/operator`) com botões de pânico e bíblia rápida.
 * - Pareamento instantâneo via QR Code na tela sem necessidade de instalar apps de lojas móveis.
 * - Controle de acesso por papéis (RBAC leve) com tokens temporários de segurança.
 *
 * ## Comunicação e Fronteiras:
 * - **Dispara:** Comandos litúrgicos autorizados para o `apps/daemon` via WebSocket e REST.
 * - **Consome:** Estado em tempo real do `@lumen/core` e `@slices/stage` para refletir no smartphone.
 * - **Exporta para UI:** `PreacherRemoteView`, `OperatorRemoteView` e `RemotePairingModal`.
 */

export const SLICE_NAME = 'remote'

export type {
  RemoteRole,
  RemotePairingInfo,
  RemoteAuthorizationResult,
} from './contracts/remote-contracts'

export {
  RemoteRoleSchema,
  RemotePairingInfoSchema,
} from './contracts/remote-contracts'

export {
  RemoteAuthManager,
  detectLocalLanIp,
} from './daemon/remote-auth'

export { handleRemoteRoutes } from './daemon/routes/remote-router'
export { remoteService } from './ui/services/remote-service'
export { PreacherRemoteView } from './ui/components/PreacherRemoteView'
export { OperatorRemoteView } from './ui/components/OperatorRemoteView'
export { RemotePairingModal } from './ui/components/RemotePairingModal'
