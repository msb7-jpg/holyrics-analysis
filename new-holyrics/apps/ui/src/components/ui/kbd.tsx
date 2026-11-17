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
        'inline-flex items-center justify-center rounded bg-zinc-900 px-1.5 py-0.5 tabular-nums font-mono text-[10px] text-zinc-400 border border-zinc-800 select-none',
        className
      )}
      {...props}
    />
  )
}
