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
        'w-full rounded-lg bg-slate-800/90 px-3 py-1.5 text-xs text-slate-100 placeholder:text-slate-500 border border-white/10 transition',
        'focus:border-sky-500/50 focus:outline-none focus:ring-1 focus:ring-sky-500/30 disabled:opacity-50',
        className
      )}
      {...props}
    />
  )
}
