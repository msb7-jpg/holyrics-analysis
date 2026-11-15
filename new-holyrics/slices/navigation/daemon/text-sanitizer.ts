/**
 * @module @slices/navigation/daemon/text-sanitizer
 * @description Higienizador de letras e textos projetáveis para telões e auditórios.
 *
 * ## Responsabilidades:
 * - Limpar tags de seções e estrofes ([V1], [CORO], [INTRO], [PONTE], [REFRÃO], [FINAL], [V2], etc.) da projeção pública.
 * - Manter o telão 100% focado na congregação e no louvor sem ruídos litúrgicos de operação técnica.
 * - Normalizar espaços em branco e quebras de linhas desnecessárias.
 */

/**
 * Expressão regular estrita para identificar marcações de estrofes e blocos musicais legados.
 * Encontra padrões como: [V1], [VERSO 1], [CORO], [CHORUS], [PONTE], [BRIDGE], [INTRO], [FINAL], [OUTRO], [SOLO].
 */
const SECTION_TAG_REGEX = /^\s*\[(verso|verse|v\d*|\d+|coro|chorus|refrão|refrao|ponte|bridge|intro|final|outro|solo|tag|ministração|ministracao)[\s\w-]*\]\s*$/i

/**
 * Remove cabeçalhos e tags de seções intrusivas de uma linha de texto.
 *
 * @param line - Linha individual de texto musical ou aviso.
 * @returns Linha higienizada ou string vazia caso seja uma tag de controle musical.
 */
export function sanitizeLine(line: string): string {
  if (!line) return ''
  const trimmed = line.trim()

  if (SECTION_TAG_REGEX.test(trimmed)) {
    return ''
  }

  // Remove marcações embutidas no início da linha como "[CORO] Porque Ele vive"
  const cleanedLeadingTag = trimmed.replace(/^\[(verso|verse|v\d*|\d+|coro|chorus|refrão|refrao|ponte|bridge|intro|final|outro|solo)[\s\w-]*\]\s*/i, '')
  return cleanedLeadingTag.trim()
}

/**
 * Higieniza uma coleção de linhas ou estrofe completa para projeção limpa.
 *
 * @param lines - Array com as linhas originais do slide.
 * @returns Array de linhas prontas para exibição visual no projetor.
 */
export function sanitizeSlideLines(lines: string[]): string[] {
  if (!lines || lines.length === 0) return []

  const sanitized: string[] = []

  for (const rawLine of lines) {
    const cleaned = sanitizeLine(rawLine)
    if (cleaned.length > 0) {
      sanitized.push(cleaned)
    }
  }

  return sanitized
}
