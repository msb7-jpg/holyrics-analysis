/**
 * @module @ui/core/network/http-client
 * @description Cliente HTTP universal baseado em Ky para consumo tipado das rotas do daemon LumenLite.
 *
 * ## Responsabilidades:
 * - Prover métodos HTTP tipados (get, post, put, delete) com resolução automática de base URL.
 * - Encaminhar AbortSignal para cancelamento automático de requisições obsoletas.
 * - Desempacotar envelopes de resposta do daemon `{ ok: boolean, data?: T, error?: string }`.
 * - Validar e desserializar payloads em tempo de execução via esquemas Zod (quando informados).
 */

import ky from 'ky'
import type { z } from 'zod'

/**
 * Erro customizado disparado em respostas com falha ou inconsistência de contrato.
 */
export class ApiError extends Error {
  /**
   * Inicializa o ApiError com mensagem e status HTTP.
   *
   * @param message - Mensagem descritiva do erro.
   * @param statusCode - Código HTTP retornado pelo servidor.
   * @param originalData - Dados originais da resposta não processada.
   */
  constructor(
    message: string,
    public readonly statusCode: number,
    public readonly originalData?: unknown
  ) {
    super(message)
    this.name = 'ApiError'
  }
}

interface DaemonEnvelope<TData> {
  ok: boolean
  data?: TData
  error?: string
}

export interface RequestConfig<TSchema extends z.ZodTypeAny = z.ZodTypeAny> {
  searchParams?: Record<string, string | number | boolean | undefined | null>
  signal?: AbortSignal
  schema?: TSchema
  headers?: Record<string, string>
}

function sanitizeParams(params?: RequestConfig['searchParams']): Record<string, string> | undefined {
  if (!params) return undefined
  const cleaned: Record<string, string> = {}
  for (const [key, val] of Object.entries(params)) {
    if (val !== undefined && val !== null && val !== '') {
      cleaned[key] = String(val)
    }
  }
  return cleaned
}

async function unwrapAndParse<TData>(
  response: Response,
  schema?: z.ZodType<TData>
): Promise<TData> {
  let body: DaemonEnvelope<TData>

  try {
    body = (await response.json()) as DaemonEnvelope<TData>
  } catch (err: unknown) {
    throw new ApiError(
      `Falha ao decodificar JSON da resposta: ${err instanceof Error ? err.message : String(err)}`,
      response.status
    )
  }

  if (!response.ok || !body.ok) {
    const errorMsg = body.error || `Erro HTTP ${response.status}: ${response.statusText}`
    throw new ApiError(errorMsg, response.status, body)
  }

  const rawData = body.data as TData

  if (schema) {
    const parsed = schema.safeParse(rawData)
    if (!parsed.success) {
      throw new ApiError(
        `Falha na validação Zod da resposta: ${parsed.error.message}`,
        response.status,
        rawData
      )
    }
    return parsed.data
  }

  return rawData
}

const DEFAULT_API_BASE_URL = 'http://localhost:4040'

export const rawKy = ky.create({
  prefix: DEFAULT_API_BASE_URL,
  timeout: 8000,
  retry: 0,
})

export const httpClient = {
  /**
   * Executa requisição GET com parâmetros higienizados, sinal de cancelamento e desserialização Zod opcional.
   *
   * @param path - Caminho relativo do endpoint (ex: '/api/songs').
   * @param config - Configurações opcionais de parâmetros, AbortSignal e schema Zod.
   * @returns Dado desempacotado e validado.
   */
  async get<TData>(path: string, config?: RequestConfig<z.ZodType<TData>>): Promise<TData> {
    const cleanPath = path.replace(/^\//, '')
    const response = await rawKy.get(cleanPath, {
      searchParams: sanitizeParams(config?.searchParams),
      signal: config?.signal,
      headers: config?.headers,
      throwHttpErrors: false,
    })
    return unwrapAndParse<TData>(response, config?.schema)
  },

  /**
   * Executa requisição POST com corpo JSON, sinal de cancelamento e validação de retorno.
   *
   * @param path - Caminho relativo do endpoint.
   * @param body - Payload a ser enviado como JSON.
   * @param config - Configurações adicionais de requisição.
   * @returns Dado desempacotado da resposta.
   */
  async post<TData, TBody = unknown>(
    path: string,
    body: TBody,
    config?: RequestConfig<z.ZodType<TData>>
  ): Promise<TData> {
    const cleanPath = path.replace(/^\//, '')
    const response = await rawKy.post(cleanPath, {
      json: body,
      searchParams: sanitizeParams(config?.searchParams),
      signal: config?.signal,
      headers: config?.headers,
      throwHttpErrors: false,
    })
    return unwrapAndParse<TData>(response, config?.schema)
  },

  /**
   * Executa requisição DELETE com parâmetros, sinal de cancelamento e validação de retorno.
   *
   * @param path - Caminho relativo do endpoint.
   * @param config - Configurações adicionais de requisição.
   * @returns Dado desempacotado da resposta.
   */
  async delete<TData>(
    path: string,
    config?: RequestConfig<z.ZodType<TData>>
  ): Promise<TData> {
    const cleanPath = path.replace(/^\//, '')
    const response = await rawKy.delete(cleanPath, {
      searchParams: sanitizeParams(config?.searchParams),
      signal: config?.signal,
      headers: config?.headers,
      throwHttpErrors: false,
    })
    return unwrapAndParse<TData>(response, config?.schema)
  },
}
