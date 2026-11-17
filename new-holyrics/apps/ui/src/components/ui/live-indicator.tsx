import { Radio } from 'lucide-react'
import { cn } from '@ui/lib/utils'

export interface LiveIndicatorProps {
  variant?: 'icon' | 'dot'
  className?: string
}

/**
 * Indicador minimalista de transmissão ao vivo no telão, com animação sutil.
 *
 * @param props - Propriedades do indicador.
 * @param props.variant - Formato visual ('icon' para cards e 'dot' para itens de lista).
 * @param props.className - Classes CSS adicionais.
 * @returns Elemento JSX indicando estado Ao Vivo sem poluição textual.
 */
export function LiveIndicator({ variant = 'icon', className }: LiveIndicatorProps) {
  if (variant === 'dot') {
    return (
      <span
        data-testid="live-indicator"
        role="status"
        aria-label="Ao vivo"
        title="Ao vivo no telão"
        className={cn(
          'inline-block h-1.5 w-1.5 rounded-full bg-emerald-500 animate-pulse shrink-0',
          className
        )}
      />
    )
  }

  return (
    <span
      data-testid="live-indicator"
      role="status"
      aria-label="Ao vivo"
      title="Ao vivo no telão"
      className={cn(
        'inline-flex items-center justify-center rounded-full bg-emerald-500/10 p-1 text-emerald-400 border border-emerald-500/20',
        className
      )}
    >
      <Radio className="h-3 w-3 animate-pulse" strokeWidth={2} />
    </span>
  )
}
