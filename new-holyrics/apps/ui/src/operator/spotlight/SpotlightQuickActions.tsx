/**
 * @module @ui/operator/spotlight/SpotlightQuickActions
 * @description Grupo de comandos de corte rápido de emergência para o Spotlight.
 */

import { EyeOff, FileText, Snowflake } from 'lucide-react'
import { Command } from 'cmdk'
import { Kbd } from '@ui/components/ui/kbd'

export interface SpotlightQuickActionsProps {
  onExecutePanic: (target: 'blackout' | 'clearText' | 'freeze') => void
}

/**
 * Renderiza o grupo de comandos de pânico dentro da Command Palette.
 *
 * @param props - Propriedades com callback de execução.
 * @param props.onExecutePanic - Callback disparado com o alvo de pânico.
 * @returns Elemento JSX com os itens cmdk.
 */
export function SpotlightQuickActions({ onExecutePanic }: SpotlightQuickActionsProps) {
  return (
    <Command.Group heading="Ações Rápidas">
      <Command.Item
        value="action_blackout"
        onSelect={() => onExecutePanic('blackout')}
        className="flex items-center justify-between rounded-lg px-3 py-2 text-xs cursor-pointer select-none transition data-[selected=true]:bg-sky-500/20 data-[selected=true]:text-white text-slate-300"
      >
        <div className="flex items-center gap-2.5">
          <EyeOff className="h-4 w-4 text-red-400" />
          <span className="font-medium">Alternar Blackout (Telão Preto)</span>
        </div>
        <Kbd>F1</Kbd>
      </Command.Item>

      <Command.Item
        value="action_clear_text"
        onSelect={() => onExecutePanic('clearText')}
        className="flex items-center justify-between rounded-lg px-3 py-2 text-xs cursor-pointer select-none transition data-[selected=true]:bg-sky-500/20 data-[selected=true]:text-white text-slate-300"
      >
        <div className="flex items-center gap-2.5">
          <FileText className="h-4 w-4 text-sky-400" />
          <span className="font-medium">Ocultar Texto / Preservar Fundo</span>
        </div>
        <Kbd>F2</Kbd>
      </Command.Item>

      <Command.Item
        value="action_freeze"
        onSelect={() => onExecutePanic('freeze')}
        className="flex items-center justify-between rounded-lg px-3 py-2 text-xs cursor-pointer select-none transition data-[selected=true]:bg-sky-500/20 data-[selected=true]:text-white text-slate-300"
      >
        <div className="flex items-center gap-2.5">
          <Snowflake className="h-4 w-4 text-cyan-400" />
          <span className="font-medium">Congelar Projeção (Freeze)</span>
        </div>
        <Kbd>F3</Kbd>
      </Command.Item>
    </Command.Group>
  )
}
