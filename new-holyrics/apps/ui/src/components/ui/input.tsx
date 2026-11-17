import type { InputHTMLAttributes } from 'react'
import { cn } from '@ui/lib/utils'

export type InputProps = InputHTMLAttributes<HTMLInputElement>

/**
 * Campo de texto padrão com suporte a foco no padrão Slate Pro Studio.
 *
 * @param props - Propriedades do campo de entrada.
 * @param props.className - Classes CSS customizadas.
 * @param props.type - Tipo de input HTML nativo.
 * @returns Elemento JSX com estilos consolidados de input.
 */
export function Input({ className, type = 'text', ...props }: InputProps) {
  return (
    <input
      type={type}
      className={cn(
        'w-full rounded-md bg-zinc-900 px-3 py-1.5 text-xs text-zinc-100 placeholder:text-zinc-500 border border-zinc-800 transition',
        'focus:border-zinc-600 focus:outline-none focus:ring-1 focus:ring-zinc-600 disabled:opacity-50',
        className
      )}
      {...props}
    />
  )
}
