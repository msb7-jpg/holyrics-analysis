import { LiturgyFileSchema, type LiturgyFile } from '../contracts/liturgy-schema'

export interface PreflightResult {
  valid: boolean
  errors: string[]
  itemCount: number
}

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
