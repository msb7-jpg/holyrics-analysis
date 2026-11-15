/**
 * @module @lumen/core/logger
 * @description Módulo de logging estruturado assíncrono baseado em Pino.
 *
 * ## Responsabilidades:
 * - Fornecer instâncias de loggers estruturados de alta performance (`createLogger`).
 * - Formatar logs legíveis em desenvolvimento com `pino-pretty` e JSON assíncrono em produção.
 * - Evitar I/O síncrono bloqueante no loop de eventos do Bun/Node.
 */

export { createLogger, defaultLogger } from './logger'
