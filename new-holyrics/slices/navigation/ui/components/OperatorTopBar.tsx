/**
 * @module @slices/navigation/ui/components/OperatorTopBar
 * @description Barra superior do console do operador com identidade limpa, título editável do evento, busca global centralizada e controles críticos de saída.
 */

import { useState, useRef, useEffect } from 'react'
import {
  Search,
  Keyboard,
  Workflow,
  Timer,
  QrCode,
  BookOpen,
  Presentation,
  SlidersHorizontal,
  Activity,
  Check,
  WifiOff,
  ChevronDown,
  Pencil,
  Download,
  Upload,
  FilePlus2,
} from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { PanicToolbar } from './PanicToolbar'
import type { PanicMask } from '@lumen/core'

export interface OperatorTopBarProps {
  isConnected: boolean
  panic: PanicMask
  eventTitle?: string
  onChangeEventTitle?: (nextTitle: string) => void
  onExportLiturgy?: () => void
  onImportLiturgy?: (file: File) => void
  onNewLiturgy?: () => void
  onTogglePanic: (target: 'blackout' | 'clearText' | 'freeze') => void
  onOpenSpotlight: () => void
  onOpenKeymap?: () => void
  onOpenAutomation?: () => void
  onOpenStageControl?: () => void
  onOpenRemotePairing?: () => void
  onOpenBibleDownloads?: () => void
  onOpenPptxModal?: () => void
  onOpenDiagnostics?: () => void
}

/**
 * Renderiza o cabeçalho superior do operador com hierarquia Preparar -> Operar -> Monitorar.
 *
 * @param props - Propriedades da barra superior.
 * @returns Elemento JSX da barra superior.
 */
export function OperatorTopBar({
  isConnected,
  panic,
  eventTitle = 'Culto de Celebração',
  onChangeEventTitle,
  onExportLiturgy,
  onImportLiturgy,
  onNewLiturgy,
  onTogglePanic,
  onOpenSpotlight,
  onOpenKeymap,
  onOpenAutomation,
  onOpenStageControl,
  onOpenRemotePairing,
  onOpenBibleDownloads,
  onOpenPptxModal,
  onOpenDiagnostics,
}: OperatorTopBarProps) {
  const [isEditingTitle, setIsEditingTitle] = useState(false)
  const [draftTitle, setDraftTitle] = useState(eventTitle)
  const [isEventMenuOpen, setIsEventMenuOpen] = useState(false)
  const [isToolsMenuOpen, setIsToolsMenuOpen] = useState(false)
  const eventMenuRef = useRef<HTMLDivElement | null>(null)
  const toolsMenuRef = useRef<HTMLDivElement | null>(null)
  const importInputRef = useRef<HTMLInputElement | null>(null)

  useEffect(() => {
    setDraftTitle(eventTitle)
  }, [eventTitle])

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (eventMenuRef.current && !eventMenuRef.current.contains(event.target as Node)) {
        setIsEventMenuOpen(false)
      }
      if (toolsMenuRef.current && !toolsMenuRef.current.contains(event.target as Node)) {
        setIsToolsMenuOpen(false)
      }
    }
    document.addEventListener('mousedown', handleClickOutside)
    return () => document.removeEventListener('mousedown', handleClickOutside)
  }, [])

  const handleCommitTitle = () => {
    setIsEditingTitle(false)
    const trimmed = draftTitle.trim()
    if (trimmed && trimmed !== eventTitle) {
      onChangeEventTitle?.(trimmed)
    } else {
      setDraftTitle(eventTitle)
    }
  }

  return (
    <header className="relative z-30 flex h-12 items-center justify-between border-b border-zinc-800/80 bg-zinc-950 px-4 select-none gap-4">
      {/* Esquerda: Logomark minimalista + Nome editável do evento com Auto-save */}
      <div className="flex items-center gap-3 min-w-0">
        <div
          className="flex items-center gap-2 text-zinc-100 font-semibold text-xs tracking-tight shrink-0"
          title="LumenLite — Console de Projeção"
        >
          <span className="flex h-6 w-6 items-center justify-center rounded-md bg-zinc-900 border border-zinc-800">
            <span className="h-2 w-2 rounded-full bg-zinc-100" />
          </span>
          <span className="hidden sm:inline text-zinc-300 font-medium">LumenLite</span>
        </div>

        <span className="text-zinc-700">/</span>

        {isEditingTitle ? (
          <input
            type="text"
            value={draftTitle}
            autoFocus
            onChange={(event) => setDraftTitle(event.target.value)}
            onBlur={handleCommitTitle}
            onKeyDown={(event) => {
              if (event.key === 'Enter') handleCommitTitle()
              if (event.key === 'Escape') {
                setDraftTitle(eventTitle)
                setIsEditingTitle(false)
              }
            }}
            className="rounded-md bg-zinc-900 px-2 py-0.5 text-xs font-medium text-zinc-100 outline-none ring-1 ring-zinc-600 w-48"
          />
        ) : (
          <div className="relative" ref={eventMenuRef}>
            <button
              type="button"
              onClick={() => setIsEventMenuOpen((prev) => !prev)}
              title="Gerenciar roteiro do culto atual (Renomear, Exportar, Importar, Novo)"
              className="group flex items-center gap-1.5 rounded-md px-2 py-1 text-xs font-semibold text-zinc-100 hover:bg-zinc-900 transition truncate max-w-[240px]"
            >
              <span className="truncate">{eventTitle}</span>
              <span
                className="flex items-center gap-1 text-[10px] font-normal text-zinc-500 shrink-0"
                title="Roteiro salvo automaticamente"
              >
                <Check className="h-3 w-3" />
              </span>
              <ChevronDown className="h-3 w-3 text-zinc-400 shrink-0" />
            </button>

            <input
              ref={importInputRef}
              type="file"
              accept=".json,application/json"
              className="hidden"
              onChange={(event) => {
                const file = event.target.files?.[0]
                if (file) {
                  onImportLiturgy?.(file)
                }
                event.target.value = ''
              }}
            />

            {isEventMenuOpen && (
              <div className="absolute left-0 mt-1.5 w-56 rounded-lg border border-zinc-800 bg-zinc-900 py-1 shadow-xl z-50">
                <button
                  type="button"
                  onClick={() => {
                    setIsEventMenuOpen(false)
                    setIsEditingTitle(true)
                  }}
                  className="flex w-full items-center gap-2.5 px-3 py-1.5 text-left text-xs text-zinc-200 hover:bg-zinc-800"
                >
                  <Pencil className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
                  <span>Renomear Culto</span>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setIsEventMenuOpen(false)
                    onExportLiturgy?.()
                  }}
                  className="flex w-full items-center gap-2.5 px-3 py-1.5 text-left text-xs text-zinc-200 hover:bg-zinc-800"
                >
                  <Download className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
                  <span>Exportar Roteiro (.json)</span>
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setIsEventMenuOpen(false)
                    importInputRef.current?.click()
                  }}
                  className="flex w-full items-center gap-2.5 px-3 py-1.5 text-left text-xs text-zinc-200 hover:bg-zinc-800"
                >
                  <Upload className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
                  <span>Importar Roteiro (.json)</span>
                </button>
                <div className="my-1 border-t border-zinc-800" />
                <button
                  type="button"
                  onClick={() => {
                    setIsEventMenuOpen(false)
                    onNewLiturgy?.()
                  }}
                  className="flex w-full items-center gap-2.5 px-3 py-1.5 text-left text-xs text-red-400 hover:bg-red-500/10"
                >
                  <FilePlus2 className="h-3.5 w-3.5 text-red-400" strokeWidth={1.75} />
                  <span>Novo Culto (Limpar Roteiro)</span>
                </button>
              </div>
            )}
          </div>
        )}

        {/* Alerta discreto exibido APENAS se perder conexão com o serviço local */}
        {!isConnected && (
          <div
            className="flex items-center gap-1.5 rounded-md bg-amber-500/15 border border-amber-500/30 px-2 py-0.5 text-[11px] font-medium text-amber-300"
            title="Reconectando ao serviço de projeção em segundo plano"
          >
            <WifiOff className="h-3 w-3 animate-pulse" />
            <span>Reconectando...</span>
          </div>
        )}
      </div>

      {/* Centro: Barra de Busca Global (Spotlight) */}
      <div className="flex flex-1 justify-center max-w-md">
        <button
          type="button"
          onClick={onOpenSpotlight}
          title="Busca rápida de músicas, versículos e ações (Ctrl+K)"
          className="flex w-full items-center justify-between rounded-md bg-zinc-900 hover:bg-zinc-800/80 px-3 py-1.5 text-xs text-zinc-400 hover:text-zinc-200 transition border border-zinc-800"
        >
          <span className="flex items-center gap-2 truncate">
            <Search className="h-3.5 w-3.5 text-zinc-400 shrink-0" strokeWidth={1.75} />
            <span className="truncate">Buscar hino, versículo ou mídia...</span>
          </span>
          <kbd className="ml-2 rounded text-[10px] tabular-nums font-mono bg-zinc-950 border border-zinc-800 px-1.5 py-0.5 text-zinc-400 shrink-0">
            Ctrl+K
          </kbd>
        </button>
      </div>

      {/* Direita: Controles Críticos de Saída + Menu de Ferramentas */}
      <div className="flex items-center gap-2 shrink-0">
        <PanicToolbar panic={panic} onTogglePanic={onTogglePanic} />

        <div className="h-4 w-px bg-zinc-800 mx-0.5" />

        {/* Menu Gaveta de Ferramentas Secundárias */}
        <div className="relative" ref={toolsMenuRef}>
          <Button
            variant="secondary"
            onClick={() => setIsToolsMenuOpen((previous) => !previous)}
            title="Abrir ferramentas de apoio, palco, controle remoto e configurações"
          >
            <SlidersHorizontal className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
            <span>Ferramentas</span>
            <ChevronDown className="h-3 w-3 text-zinc-400" />
          </Button>

          {isToolsMenuOpen && (
            <div className="absolute right-0 mt-1.5 w-60 rounded-lg border border-zinc-800 bg-zinc-900 p-1.5 shadow-xl z-50 text-xs text-zinc-200">
              <div className="px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wider text-zinc-500">
                Operação & Apoio
              </div>

              {onOpenStageControl && (
                <button
                  type="button"
                  onClick={() => {
                    setIsToolsMenuOpen(false)
                    onOpenStageControl()
                  }}
                  className="flex w-full items-center gap-2.5 rounded-md px-2.5 py-2 text-left hover:bg-zinc-800 transition"
                >
                  <Timer className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
                  <span>Palco, Cronômetros & Alertas</span>
                </button>
              )}

              {onOpenPptxModal && (
                <button
                  type="button"
                  onClick={() => {
                    setIsToolsMenuOpen(false)
                    onOpenPptxModal()
                  }}
                  className="flex w-full items-center gap-2.5 rounded-md px-2.5 py-2 text-left hover:bg-zinc-800 transition"
                >
                  <Presentation className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
                  <span>Apresentações (PPTX / PDF)</span>
                </button>
              )}

              {onOpenBibleDownloads && (
                <button
                  type="button"
                  onClick={() => {
                    setIsToolsMenuOpen(false)
                    onOpenBibleDownloads()
                  }}
                  className="flex w-full items-center gap-2.5 rounded-md px-2.5 py-2 text-left hover:bg-zinc-800 transition"
                >
                  <BookOpen className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
                  <span>Traduções Bíblicas Offline</span>
                </button>
              )}

              {onOpenRemotePairing && (
                <button
                  type="button"
                  onClick={() => {
                    setIsToolsMenuOpen(false)
                    onOpenRemotePairing()
                  }}
                  className="flex w-full items-center gap-2.5 rounded-md px-2.5 py-2 text-left hover:bg-zinc-800 transition"
                >
                  <QrCode className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
                  <span>Controle Remoto via QR Code</span>
                </button>
              )}

              <div className="my-1 border-t border-zinc-800" />
              <div className="px-2.5 py-1 text-[10px] font-semibold uppercase tracking-wider text-zinc-500">
                Configurações & Sistema
              </div>

              {onOpenKeymap && (
                <button
                  type="button"
                  onClick={() => {
                    setIsToolsMenuOpen(false)
                    onOpenKeymap()
                  }}
                  className="flex w-full items-center gap-2.5 rounded-md px-2.5 py-2 text-left hover:bg-zinc-800 transition"
                >
                  <Keyboard className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
                  <span>Atalhos de Teclado</span>
                </button>
              )}

              {onOpenAutomation && (
                <button
                  type="button"
                  onClick={() => {
                    setIsToolsMenuOpen(false)
                    onOpenAutomation()
                  }}
                  className="flex w-full items-center gap-2.5 rounded-md px-2.5 py-2 text-left hover:bg-zinc-800 transition"
                >
                  <Workflow className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
                  <span>Automações & OBS Studio</span>
                </button>
              )}

              {onOpenDiagnostics && (
                <button
                  type="button"
                  onClick={() => {
                    setIsToolsMenuOpen(false)
                    onOpenDiagnostics()
                  }}
                  className="flex w-full items-center gap-2.5 rounded-md px-2.5 py-2 text-left hover:bg-zinc-800 transition"
                >
                  <Activity className="h-3.5 w-3.5 text-zinc-400" strokeWidth={1.75} />
                  <span>Diagnóstico / Sobre</span>
                </button>
              )}
            </div>
          )}
        </div>
      </div>
    </header>
  )
}
