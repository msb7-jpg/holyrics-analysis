/**
 * @module @lumen/broadcast/BroadcastView
 * @description Renderizador principal do overlay de Broadcast (Lower Thirds) para o OBS Studio.
 *
 * ## Responsabilidades:
 * - Conectar-se ao daemon LumenLite via WebSocket (usando `useBroadcastConnector`).
 * - Renderizar o texto do slide atual (ForegroundLayer) com fundo transparente.
 * - (Em breve) Aplicar estilos e configurações de exibição (Lower Thirds, alinhamento) buscando do `DisplayRepository`.
 * - Lidar com estados vazios e máscaras de pânico (clearText, blackout).
 */

import { useBroadcastConnector } from './useBroadcastConnector'

/**
 * Componente principal da visualização de broadcast.
 *
 * @returns Elemento JSX do overlay de transmissão.
 */
export function BroadcastView() {
  const { state, isConnected } = useBroadcastConnector()

  // Se o estado for EMPTY ou houver uma máscara de pânico limpando o texto/tela, não renderiza nada
  if (!isConnected || state.mode === 'EMPTY' || state.panic.blackout || state.panic.clearText) {
    return <div className="fixed inset-0 pointer-events-none bg-transparent"></div>
  }

  const { foreground } = state

  // Se não houver conteúdo no slide atual
  if (!foreground.content || foreground.content.length === 0) {
    return <div className="fixed inset-0 pointer-events-none bg-transparent"></div>
  }

  return (
    <div className="fixed inset-0 pointer-events-none bg-transparent flex flex-col justify-end p-12">
      {/* Lower Third Container */}
      <div className="bg-slate-900/80 p-8 rounded-xl max-w-4xl w-full border border-white/10 shadow-2xl backdrop-blur-md">
        
        {/* Título e Referência (Opcionais) */}
        {(foreground.title || foreground.reference) && (
          <div className="mb-4 text-sky-400 font-semibold tracking-wider text-xl uppercase">
            {foreground.title} {foreground.reference && <span className="opacity-70 text-slate-400 font-normal normal-case"> - {foreground.reference}</span>}
          </div>
        )}

        {/* Conteúdo do Slide */}
        <div className="text-white text-5xl font-bold leading-tight drop-shadow-[0_4px_4px_rgba(0,0,0,0.8)] flex flex-col gap-2">
          {foreground.content.map((line, index) => (
            <div key={index} className="text-center">{line}</div>
          ))}
        </div>

      </div>
    </div>
  )
}
