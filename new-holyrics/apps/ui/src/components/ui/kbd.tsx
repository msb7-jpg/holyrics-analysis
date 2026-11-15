import type { HTMLAttributes } from 'react'
import { cn } from '@ui/lib/utils'

export type KbdProps = HTMLAttributes<HTMLElement>

/**
 * Indicador visual para teclas e atalhos de teclado.
 *
 * @param props - Propriedades do elemento kbd.
 * @param props.className - Classes CSS customizadas.
 * @returns Elemento JSX com tipografia monoespaçada e borda sutil.
 */
export function Kbd({ className, ...props }: KbdProps) {
  return (
    <kbd
      className={cn(
        'inline-flex items-center justify-center rounded bg-slate-900 px-1.5 py-0.5 font-mono text-[10px] text-slate-400 border border-white/10 select-none shadow-sm',
        className
      )}
      {...props}
    />
  )
}
