import type { HTMLAttributes } from 'react'
import { cva, type VariantProps } from 'class-variance-authority'
import { cn } from '@ui/lib/utils'

export const badgeVariants = cva(
  'inline-flex items-center gap-1 rounded font-mono text-[10px] font-semibold uppercase tracking-wider px-1.5 py-0.5 border select-none',
  {
    variants: {
      variant: {
        default: 'border-white/10 bg-slate-800 text-slate-300',
        live: 'border-sky-500/40 bg-sky-500/15 text-sky-300 animate-pulse',
        panic: 'border-red-500/40 bg-red-500/15 text-red-300',
        success: 'border-emerald-500/40 bg-emerald-500/15 text-emerald-300',
        bible: 'border-sky-500/30 bg-sky-500/10 text-sky-400',
      },
    },
    defaultVariants: {
      variant: 'default',
    },
  }
)

export interface BadgeProps
  extends HTMLAttributes<HTMLSpanElement>,
    VariantProps<typeof badgeVariants> {}

/**
 * Badge sóbrio para exibição de status, metadados e tags litúrgicas.
 *
 * @param props - Propriedades do badge.
 * @param props.className - Classes CSS customizadas adicionais.
 * @param props.variant - Variante visual semântica do badge.
 * @returns Elemento JSX tipado.
 */
export function Badge({ className, variant, ...props }: BadgeProps) {
  return <span className={cn(badgeVariants({ variant }), className)} {...props} />
}
