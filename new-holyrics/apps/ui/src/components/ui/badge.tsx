import type { HTMLAttributes } from 'react'
import { cva, type VariantProps } from 'class-variance-authority'
import { cn } from '@ui/lib/utils'

export const badgeVariants = cva(
  'inline-flex items-center gap-1 rounded font-mono text-[10px] font-semibold uppercase tracking-wider px-1.5 py-0.5 border select-none',
  {
    variants: {
      variant: {
        default: 'border-zinc-800 bg-zinc-900 text-zinc-300',
        live: 'border-emerald-500/20 bg-emerald-500/10 text-emerald-400',
        panic: 'border-red-500/30 bg-red-500/15 text-red-400',
        success: 'border-emerald-500/20 bg-emerald-500/10 text-emerald-400',
        bible: 'border-zinc-700 bg-zinc-800 text-zinc-200',
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
