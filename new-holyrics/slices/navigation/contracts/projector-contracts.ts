/**
 * @module @slices/navigation/contracts/projector-contracts
 * @description Contratos e tipos para a renderização na saída HDMI e janela do projetor (/projector).
 *
 * ## Responsabilidades:
 * - Tipificar configurações de exibição (resolução, proporção, margens seguras e alinhamento).
 * - Definir contratos de limpeza de letras de hinos (remoção de marcadores legados [V1], [CORO], etc).
 * - Tipificar estado do buffer duplo de vídeo para seamless loop sem flash preto.
 */

import { z } from 'zod'

/**
 * Esquema de proporção de tela suportada.
 */
export const AspectRatioSchema = z.enum(['16:9', '16:10', '4:3', 'custom'])
export type AspectRatio = z.infer<typeof AspectRatioSchema>

/**
 * Alinhamento vertical e horizontal do texto da projeção.
 */
export const TextAlignmentSchema = z.object({
  vertical: z.enum(['top', 'center', 'bottom']).default('center'),
  horizontal: z.enum(['left', 'center', 'right']).default('center'),
})
export type TextAlignment = z.infer<typeof TextAlignmentSchema>

/**
 * Configuração visual da janela de projeção do telão HDMI.
 */
export const ProjectorDisplayConfigSchema = z.object({
  aspectRatio: AspectRatioSchema.default('16:9'),
  safeAreaMarginPercent: z.number().min(0).max(30).default(6),
  alignment: TextAlignmentSchema.default({ vertical: 'center', horizontal: 'center' }),
  fontFamily: z.string().default('Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif'),
  textColor: z.string().default('#FFFFFF'),
  textShadow: z.string().default('0 2px 10px rgba(0, 0, 0, 0.85)'),
  letterSpacing: z.string().default('0.02em'),
  maxLinesPerSlide: z.number().int().min(1).max(10).default(4),
  videoCrossfadeDurationMs: z.number().int().min(50).max(1000).default(200),
})

export type ProjectorDisplayConfig = z.infer<typeof ProjectorDisplayConfigSchema>

/**
 * Configurações padrão ideais para auditório e telões de projeção (Slate Pro Studio).
 */
export const DEFAULT_PROJECTOR_CONFIG: ProjectorDisplayConfig = {
  aspectRatio: '16:9',
  safeAreaMarginPercent: 6,
  alignment: { vertical: 'center', horizontal: 'center' },
  fontFamily: 'Inter, -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, sans-serif',
  textColor: '#FFFFFF',
  textShadow: '0 2px 10px rgba(0, 0, 0, 0.85)',
  letterSpacing: '0.02em',
  maxLinesPerSlide: 4,
  videoCrossfadeDurationMs: 200,
}
