import pino from 'pino'

/**
 * Cria uma instância configurada do logger estruturado Pino.
 *
 * Em desenvolvimento ou execução local padrão no servidor, formata de forma limpa.
 * Em produção, gera JSON estruturado de altíssima performance sem travar a thread.
 * No navegador (UI), opera em modo browser-safe sem acesso a `process.env` ou worker transports.
 *
 * @param name - Nome do componente ou subsistema (ex: 'daemon', 'event-bus', 'songs').
 * @returns Instância do Logger Pino.
 */
export function createLogger(name: string) {
  const isBrowser = typeof window !== 'undefined' || typeof process === 'undefined'
  if (isBrowser) {
    return pino({
      name,
      level: 'info',
      browser: { asObject: false },
    })
  }

  const isProduction = process.env.NODE_ENV === 'production'

  return pino({
    name,
    level: process.env.LOG_LEVEL || (isProduction ? 'info' : 'debug'),
    transport: isProduction
      ? undefined
      : {
          target: 'pino-pretty',
          options: {
            colorize: true,
            ignore: 'pid,hostname',
            translateTime: 'SYS:HH:MM:ss.l',
          },
        },
  })
}

export const defaultLogger = createLogger('lumen')
