import type { TextareaHTMLAttributes } from 'react'
import { cn } from '@ui/lib/utils'

export type TextareaProps = TextareaHTMLAttributes<HTMLTextAreaElement>

/**
 * Campo de texto multilinhas padrão com suporte a foco no padrão Slate Pro Studio.
 *
 * @param props - Propriedades da área de texto.
 * @param props.className - Classes CSS customizadas.
 * @returns Elemento JSX com estilos consolidados de textarea.
 */
export function Textarea({ className, ...props }: TextareaProps) {
  return (
    <textarea
      className={cn(
        'w-full rounded-lg bg-slate-800/90 px-3 py-2 text-xs text-slate-100 placeholder:text-slate-500 border border-white/10 transition',
        'focus:border-sky-500/50 focus:outline-none focus:ring-1 focus:ring-sky-500/30 disabled:opacity-50 resize-none',
        className
      )}
      {...props}
    />
  )
}
