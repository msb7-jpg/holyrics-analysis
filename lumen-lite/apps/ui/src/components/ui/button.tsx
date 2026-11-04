import type { ButtonHTMLAttributes } from 'react'
import { cva, type VariantProps } from 'class-variance-authority'
import { cn } from '@ui/lib/utils'

export const buttonVariants = cva(
  'inline-flex items-center justify-center gap-2 rounded-lg font-medium transition cursor-pointer select-none disabled:opacity-50 disabled:cursor-not-allowed focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-sky-500/50',
  {
    variants: {
      variant: {
        default: 'bg-sky-600 text-white hover:bg-sky-500 border border-sky-500/50',
        secondary: 'bg-slate-800 text-slate-200 hover:bg-slate-700 border border-white/10',
        outline: 'border border-white/10 text-slate-300 hover:bg-slate-800 hover:text-white',
        ghost: 'text-slate-400 hover:text-slate-100 hover:bg-slate-800/60',
        panic: 'bg-red-500/20 text-red-300 hover:bg-red-500/30 border border-red-500/50',
        activePanic: 'bg-red-500 text-white hover:bg-red-600 shadow-lg shadow-red-500/20',
        activeInfo: 'bg-sky-500/20 text-sky-300 border border-sky-500/50',
      },
      size: {
        default: 'h-8 px-3 text-xs',
        sm: 'h-7 px-2.5 text-[11px]',
        lg: 'h-9 px-4 text-sm',
        icon: 'h-8 w-8 p-0',
      },
    },
    defaultVariants: {
      variant: 'default',
      size: 'default',
    },
  }
)

export interface ButtonProps
  extends ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {}

/**
 * Botão atômico padronizado do Design System Slate Pro Studio.
 *
 * @param props - Propriedades do botão.
 * @param props.className - Classes CSS customizadas adicionais.
 * @param props.variant - Variante visual semântica do botão.
 * @param props.size - Tamanho dimensional do botão.
 * @returns Elemento JSX do botão com variantes tipadas.
 */
export function Button({ className, variant, size, ...props }: ButtonProps) {
  return <button className={cn(buttonVariants({ variant, size }), className)} {...props} />
}
