import { clsx, type ClassValue } from 'clsx'
import { twMerge } from 'tailwind-merge'

/**
 * Mescla classes utilitárias do Tailwind CSS de forma determinística,
 * combinando condicionalidades do clsx com a resolução de conflitos do tailwind-merge.
 *
 * @param inputs - Lista de classes, booleanos ou expressões condicionais.
 * @returns String unificada de classes CSS sem conflitos.
 */
export function cn(...inputs: ClassValue[]): string {
  return twMerge(clsx(inputs))
}
