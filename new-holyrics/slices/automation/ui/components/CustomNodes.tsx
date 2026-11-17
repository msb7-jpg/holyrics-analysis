/**
 * @module @slices/automation/ui/components/CustomNodes
 * @description Nós visuais customizados para o editor de grafo DAG de automações no padrão Slate Pro Studio.
 *
 * ## Responsabilidades:
 * - Renderizar nós de Gatilho (Trigger), Condição (Filter) e Ações (OBS, HTTP, FSM).
 * - Fornecer handles para conexão de arestas e exibição clara de parâmetros.
 */

import { Handle, Position, type Node, type NodeProps } from '@xyflow/react'
import {
  Zap,
  GitBranch,
  Video,
  Globe,
  Layers,
  Clock,
} from 'lucide-react'
import type { AutomationNode } from '../../contracts/automation-contracts'

export type AutomationCustomNode = Node<AutomationNode, 'triggerNode' | 'conditionNode' | 'actionNode'>
export type AutomationCustomNodeProps = NodeProps<AutomationCustomNode>

/**
 * Nó de Gatilho (Trigger) disparado por eventos da FSM ou webhooks.
 *
 * @param props - Propriedades do nó ReactFlow.
 * @param props.data - Dados customizados do nó.
 * @returns Elemento JSX do nó.
 */
export function TriggerNode({ data }: { data: AutomationNode }) {
  const triggerLabel = data.triggerConfig?.type === 'ON_SLIDE_CHANGE'
    ? 'Troca de Slide'
    : data.triggerConfig?.type === 'ON_ITEM_SELECT'
    ? 'Seleção de Item'
    : data.triggerConfig?.type === 'ON_PANIC'
    ? 'Ação de Pânico'
    : 'Webhook HTTP'

  return (
    <div className="min-w-[180px] rounded-lg border border-zinc-700 bg-zinc-900/95 p-3 shadow-xl backdrop-blur-md">
      <div className="flex items-center gap-2 mb-1.5">
        <span className="flex h-5 w-5 items-center justify-center rounded bg-zinc-800 text-zinc-400">
          <Zap className="h-3 w-3" />
        </span>
        <span className="text-[10px] font-mono font-bold tracking-wider uppercase text-zinc-400">
          Gatilho
        </span>
      </div>
      <p className="text-xs font-semibold text-zinc-100">{data.label}</p>
      <span className="text-[10px] text-zinc-400 font-mono">{triggerLabel}</span>

      {/* Handle de saída */}
      <Handle
        type="source"
        position={Position.Right}
        className="!h-2.5 !w-2.5 !bg-sky-400 !border-zinc-900"
      />
    </div>
  )
}

/**
 * Nó de Condição / Filtro com ramificações True / False.
 *
 * @param props - Propriedades do nó ReactFlow.
 * @param props.data - Dados customizados do nó.
 * @returns Elemento JSX do nó.
 */
export function ConditionNode({ data }: { data: AutomationNode }) {
  const config = data.conditionConfig

  return (
    <div className="relative min-w-[200px] rounded-lg border border-amber-500/30 bg-zinc-900/95 p-3 shadow-xl backdrop-blur-md">
      {/* Handle de entrada */}
      <Handle
        type="target"
        position={Position.Left}
        className="!h-2.5 !w-2.5 !bg-amber-400 !border-zinc-900"
      />

      <div className="flex items-center gap-2 mb-1.5">
        <span className="flex h-5 w-5 items-center justify-center rounded bg-amber-500/20 text-amber-400">
          <GitBranch className="h-3 w-3" />
        </span>
        <span className="text-[10px] font-mono font-bold tracking-wider uppercase text-amber-400">
          Condição
        </span>
      </div>
      <p className="text-xs font-semibold text-zinc-100">{data.label}</p>
      {config && (
        <div className="mt-1 flex items-center gap-1 rounded bg-zinc-950/80 px-1.5 py-0.5 text-[10px] font-mono text-amber-300">
          <span>{config.field}</span>
          <span className="text-zinc-500">{config.operator}</span>
          <span className="text-white">"{config.value}"</span>
        </div>
      )}

      {/* Handles de saída (True no topo direito, False na base direita) */}
      <div className="absolute -right-3 top-3 flex items-center gap-1">
        <span className="text-[8px] font-mono text-emerald-400">T</span>
        <Handle
          id="true"
          type="source"
          position={Position.Right}
          className="!h-2.5 !w-2.5 !bg-emerald-400 !border-zinc-900"
        />
      </div>

      <div className="absolute -right-3 bottom-3 flex items-center gap-1">
        <span className="text-[8px] font-mono text-rose-400">F</span>
        <Handle
          id="false"
          type="source"
          position={Position.Right}
          className="!h-2.5 !w-2.5 !bg-rose-400 !border-zinc-900"
        />
      </div>
    </div>
  )
}

/**
 * Nó de Ação externa (OBS Studio, HTTP REST, FSM, Delay).
 *
 * @param props - Propriedades do nó ReactFlow.
 * @param props.data - Dados customizados do nó.
 * @returns Elemento JSX do nó.
 */
export function ActionNode({ data }: { data: AutomationNode }) {
  const isObs = data.kind === 'ACTION_OBS'
  const isHttp = data.kind === 'ACTION_HTTP'
  const isFsm = data.kind === 'ACTION_FSM'
  const isDelay = data.kind === 'ACTION_DELAY'

  const borderClass = isObs
    ? 'border-emerald-500/30'
    : isHttp
    ? 'border-purple-500/30'
    : isFsm
    ? 'border-cyan-500/30'
    : 'border-zinc-500/30'

  const badgeBg = isObs
    ? 'bg-emerald-500/20 text-emerald-400'
    : isHttp
    ? 'bg-purple-500/20 text-purple-400'
    : isFsm
    ? 'bg-cyan-500/20 text-cyan-400'
    : 'bg-zinc-500/20 text-zinc-400'

  return (
    <div className={`min-w-[200px] rounded-lg border ${borderClass} bg-zinc-900/95 p-3 shadow-xl backdrop-blur-md`}>
      {/* Handle de entrada */}
      <Handle
        type="target"
        position={Position.Left}
        className="!h-2.5 !w-2.5 !bg-zinc-400 !border-zinc-900"
      />

      <div className="flex items-center gap-2 mb-1.5">
        <span className={`flex h-5 w-5 items-center justify-center rounded ${badgeBg}`}>
          {isObs && <Video className="h-3 w-3" />}
          {isHttp && <Globe className="h-3 w-3" />}
          {isFsm && <Layers className="h-3 w-3" />}
          {isDelay && <Clock className="h-3 w-3" />}
        </span>
        <span className="text-[10px] font-mono font-bold tracking-wider uppercase text-zinc-400">
          {isObs ? 'OBS v5' : isHttp ? 'HTTP REST' : isFsm ? 'FSM Projeção' : 'Delay'}
        </span>
      </div>

      <p className="text-xs font-semibold text-zinc-100">{data.label}</p>

      {/* Detalhes específicos de cada ação */}
      {isObs && data.actionObsConfig && (
        <p className="mt-1 text-[10px] text-emerald-300 font-mono truncate">
          {data.actionObsConfig.requestType}
          {data.actionObsConfig.sceneName ? ` → ${data.actionObsConfig.sceneName}` : ''}
        </p>
      )}

      {isHttp && data.actionHttpConfig && (
        <p className="mt-1 text-[10px] text-purple-300 font-mono truncate">
          {data.actionHttpConfig.method} {data.actionHttpConfig.url}
        </p>
      )}

      {isDelay && data.actionDelayConfig && (
        <p className="mt-1 text-[10px] text-zinc-400 font-mono">
          Pausa: {data.actionDelayConfig.durationMs}ms
        </p>
      )}

      {/* Handle de saída para encadeamento */}
      <Handle
        type="source"
        position={Position.Right}
        className="!h-2.5 !w-2.5 !bg-zinc-400 !border-zinc-900"
      />
    </div>
  )
}
