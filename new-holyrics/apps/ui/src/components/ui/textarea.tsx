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
        'w-full rounded-md bg-zinc-900 px-3 py-2 text-xs text-zinc-100 placeholder:text-zinc-500 border border-zinc-800 transition',
        'focus:border-zinc-600 focus:outline-none focus:ring-1 focus:ring-zinc-600 disabled:opacity-50 resize-none',
        className
      )}
      {...props}
    />
  )
}
