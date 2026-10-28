import { LiturgyFileSchema } from '../contracts/liturgy-schema'

export interface PreflightResult {
  valid: boolean
  errors: string[]
  itemCount: number
}

/**
 * Validador de pré-voo para roteiros litúrgicos (`culto.json`).
 * Verifica a integridade estrutural do arquivo e campos obrigatórios.
 *
 * @param data - Conteúdo bruto ou parseado do arquivo `culto.json`.
 * @returns Resultado detalhado contendo status de validação, contagem de itens e lista de erros.
 */
export function validateLiturgy(data: unknown): PreflightResult {
  const parsed = LiturgyFileSchema.safeParse(data)
  if (!parsed.success) {
    return {
      valid: false,
      errors: parsed.error.issues.map(
        (issue) => `${issue.path.join('.') || 'root'}: ${issue.message}`
      ),
      itemCount: 0,
    }
  }

  return {
    valid: true,
    errors: [],
    itemCount: parsed.data.items.length,
  }
}
