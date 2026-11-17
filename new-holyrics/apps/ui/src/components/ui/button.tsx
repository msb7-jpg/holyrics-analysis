import type { ButtonHTMLAttributes } from 'react'
import { cva, type VariantProps } from 'class-variance-authority'
import { cn } from '@ui/lib/utils'

export const buttonVariants = cva(
  'inline-flex items-center justify-center gap-2 rounded-md font-medium transition cursor-pointer select-none disabled:opacity-50 disabled:cursor-not-allowed focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-zinc-400',
  {
    variants: {
      variant: {
        default: 'bg-zinc-100 text-zinc-900 hover:bg-zinc-200 border border-zinc-200',
        secondary: 'bg-zinc-900 text-zinc-200 hover:bg-zinc-800 border border-zinc-800',
        outline: 'border border-zinc-800 text-zinc-300 hover:bg-zinc-800 hover:text-zinc-100',
        ghost: 'text-zinc-400 hover:text-zinc-100 hover:bg-zinc-800/60',
        panic: 'bg-red-500/15 text-red-400 hover:bg-red-500/25 border border-red-500/30',
        activePanic: 'bg-red-600 text-white hover:bg-red-500 border border-red-500',
        activeInfo: 'bg-zinc-800 text-zinc-100 border border-zinc-700',
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
