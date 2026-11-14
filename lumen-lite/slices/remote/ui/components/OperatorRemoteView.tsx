/**
 * @module @slices/remote/ui/components/OperatorRemoteView
 * @description Console Web Móvel do Operador via QR Code (`/remote/operator` - RF-26B).
 * Permite controle volante das principais funções da cabine pelo celular/tablet:
 * Pânico (Blackout, Clear Text, Clear Bg, Freeze), Navegação do Culto, Bíblia Rápida e Alertas.
 */

import { useState } from 'react'
import {
  EyeOff,
  FileText,
  ImageOff,
  Snowflake,
  ChevronLeft,
  ChevronRight,
  BookOpen,
  Bell,
  ListMusic,
  ShieldAlert,
} from 'lucide-react'
import type { ActionCommand, ScreenState } from '@lumen/core'
import { Button } from '@ui/components/ui/button'
import { Input } from '@ui/components/ui/input'
import { Badge } from '@ui/components/ui/badge'
import { useCurrentLiturgyQuery } from '@slices/liturgy/ui/queries/liturgy-queries'
import { bibleService } from '@slices/bible/ui/services/bible-service'
import { stageService } from '@slices/stage/ui/services/stage-service'

export interface OperatorRemoteViewProps {
  state: ScreenState
  dispatch: (command: ActionCommand) => void
}

/**
 * Aplicação Web Móvel do Operador (`/remote/operator`).
 *
 * @param props - Propriedades do console móvel.
 * @param props.state - Estado atômico atual do telão.
 * @param props.dispatch - Despachante de comandos para a FSM.
 * @returns Elemento JSX responsivo para operação volante via smartphone.
 */
export function OperatorRemoteView({ state, dispatch }: OperatorRemoteViewProps) {
  const [activeTab, setActiveTab] = useState<'control' | 'liturgy' | 'bible' | 'alerts'>('control')
  const [bibleRefInput, setBibleRefInput] = useState('jo 3 16')
  const [bibleFeedback, setBibleFeedback] = useState<string | null>(null)
  const [alertInput, setAlertInput] = useState('')

  const { data: liturgy } = useCurrentLiturgyQuery()

  const handleQuickBibleProject = async () => {
    if (!bibleRefInput.trim()) return
    try {
      const lookup = await bibleService.lookupReference(bibleRefInput.trim(), 'ACF')
      if (lookup && lookup.verses.length > 0) {
        const slides = lookup.verses.map((verseItem) => ({
          content: [verseItem.text],
          reference: `${lookup.reference} (v.${verseItem.verse})`,
        }))
        dispatch({
          type: 'PRESENT_ITEM',
          itemId: `bible_${lookup.reference}`,
          mode: 'BIBLE',
          foreground: {
            title: lookup.reference,
            reference: slides[0].reference,
            content: slides[0].content,
            slides,
            slideIndex: 0,
            totalSlides: slides.length,
          },
        })
        setBibleFeedback(`Projetado: ${lookup.reference}`)
      }
    } catch {
      setBibleFeedback('Referência não encontrada')
    }
  }

  const handleSendQuickAlert = async () => {
    if (!alertInput.trim()) return
    await stageService.triggerAlert({
      label: 'AVISO DA CABINE',
      message: alertInput.trim(),
      style: 'urgent',
      durationSeconds: 15,
    })
    setAlertInput('')
  }

  return (
    <div className="flex min-h-screen w-full flex-col bg-slate-950 text-slate-100 select-none">
      {/* Header */}
      <header className="flex items-center justify-between border-b border-white/10 bg-slate-900 px-4 py-3">
        <div className="flex items-center gap-2">
          <Badge variant="live">OPERADOR MOBILE</Badge>
          <span className="text-xs text-slate-400 truncate max-w-[180px]">
            {state.foreground.title || 'Telão Ativo'}
          </span>
        </div>
        <span className="font-mono text-xs text-sky-400">
          Slide {state.foreground.slideIndex + 1}/{Math.max(1, state.foreground.totalSlides)}
        </span>
      </header>

      {/* Abas de Navegação Móvel */}
      <nav className="grid grid-cols-4 gap-1 border-b border-white/10 bg-slate-900/70 p-2">
        <Button
          size="sm"
          variant={activeTab === 'control' ? 'activeInfo' : 'ghost'}
          onClick={() => setActiveTab('control')}
        >
          <ShieldAlert className="h-3.5 w-3.5" />
          <span>Pânico</span>
        </Button>
        <Button
          size="sm"
          variant={activeTab === 'liturgy' ? 'activeInfo' : 'ghost'}
          onClick={() => setActiveTab('liturgy')}
        >
          <ListMusic className="h-3.5 w-3.5" />
          <span>Culto</span>
        </Button>
        <Button
          size="sm"
          variant={activeTab === 'bible' ? 'activeInfo' : 'ghost'}
          onClick={() => setActiveTab('bible')}
        >
          <BookOpen className="h-3.5 w-3.5" />
          <span>Bíblia</span>
        </Button>
        <Button
          size="sm"
          variant={activeTab === 'alerts' ? 'activeInfo' : 'ghost'}
          onClick={() => setActiveTab('alerts')}
        >
          <Bell className="h-3.5 w-3.5" />
          <span>Alertas</span>
        </Button>
      </nav>

      {/* Conteúdo da Aba Selecionada */}
      <main className="flex flex-1 flex-col gap-4 p-4 overflow-y-auto">
        {activeTab === 'control' && (
          <div className="flex flex-col gap-4">
            <div className="rounded-xl border border-white/10 bg-slate-900 p-4">
              <span className="text-[11px] font-mono text-slate-400 uppercase">
                Texto no Telão Agora
              </span>
              <p className="mt-2 text-lg font-semibold text-white">
                {state.foreground.content.join(' / ') || 'Telão sem texto'}
              </p>
            </div>

            <div className="grid grid-cols-2 gap-3">
              <Button
                variant={state.panic.blackout ? 'activePanic' : 'panic'}
                className="h-14 text-sm font-semibold"
                onClick={() => dispatch({ type: 'TOGGLE_PANIC', target: 'blackout' })}
              >
                <EyeOff className="h-4 w-4" />
                <span>Blackout (F1)</span>
              </Button>

              <Button
                variant={state.panic.clearText ? 'activeInfo' : 'secondary'}
                className="h-14 text-sm font-semibold"
                onClick={() => dispatch({ type: 'TOGGLE_PANIC', target: 'clearText' })}
              >
                <FileText className="h-4 w-4" />
                <span>Ocultar Letra</span>
              </Button>

              <Button
                variant={state.panic.clearBg ? 'activeInfo' : 'secondary'}
                className="h-14 text-sm font-semibold"
                onClick={() => dispatch({ type: 'TOGGLE_PANIC', target: 'clearBg' })}
              >
                <ImageOff className="h-4 w-4" />
                <span>Limpar Fundo</span>
              </Button>

              <Button
                variant={state.panic.freeze ? 'activeInfo' : 'secondary'}
                className="h-14 text-sm font-semibold"
                onClick={() => dispatch({ type: 'TOGGLE_PANIC', target: 'freeze' })}
              >
                <Snowflake className="h-4 w-4" />
                <span>Congelar</span>
              </Button>
            </div>
          </div>
        )}

        {activeTab === 'liturgy' && (
          <div className="flex flex-col gap-2">
            <span className="text-xs font-semibold text-slate-400">
              Itens do Roteiro ({liturgy?.title || 'Culto Ativo'})
            </span>
            {liturgy?.items.map((item) => (
              <div
                key={item.id}
                className="flex items-center justify-between rounded-xl border border-white/10 bg-slate-900 p-3"
              >
                <div className="flex flex-col">
                  <span className="text-sm font-semibold text-white">{item.title}</span>
                  <span className="text-[11px] font-mono text-sky-400">{item.type}</span>
                </div>
                <Button
                  size="sm"
                  onClick={() => {
                    if (item.type === 'TEXT') {
                      dispatch({
                        type: 'PRESENT_ITEM',
                        itemId: item.id,
                        mode: 'EMPTY',
                        foreground: {
                          title: item.title,
                          content: item.content,
                          slideIndex: 0,
                          totalSlides: 1,
                        },
                      })
                    }
                  }}
                >
                  Projetar
                </Button>
              </div>
            ))}
          </div>
        )}

        {activeTab === 'bible' && (
          <div className="flex flex-col gap-3 rounded-xl border border-white/10 bg-slate-900 p-4">
            <span className="text-xs font-semibold text-sky-400 uppercase">
              Busca Bíblica Rápida (Quick Jump)
            </span>
            <Input
              value={bibleRefInput}
              onChange={(event) => setBibleRefInput(event.target.value)}
              placeholder="Ex: jo 3 16, sl 23 1-6..."
            />
            <Button onClick={handleQuickBibleProject}>
              <BookOpen className="h-4 w-4" />
              <span>Projetar Passagem</span>
            </Button>
            {bibleFeedback && (
              <span className="text-xs text-emerald-400">{bibleFeedback}</span>
            )}
          </div>
        )}

        {activeTab === 'alerts' && (
          <div className="flex flex-col gap-3 rounded-xl border border-white/10 bg-slate-900 p-4">
            <span className="text-xs font-semibold text-amber-400 uppercase">
              Alerta Rápido Lower Third
            </span>
            <Input
              value={alertInput}
              onChange={(event) => setAlertInput(event.target.value)}
              placeholder="Mensagem de berçário ou placa de veículo..."
            />
            <div className="flex gap-2">
              <Button className="flex-1" onClick={handleSendQuickAlert}>
                <Bell className="h-4 w-4" />
                <span>Enviar Alerta</span>
              </Button>
              <Button variant="panic" onClick={() => stageService.dismissAlert(true)}>
                Limpar
              </Button>
            </div>
          </div>
        )}
      </main>

      {/* Controles Permanentes de Avanço/Recuo */}
      <footer className="grid grid-cols-2 gap-3 border-t border-white/10 bg-slate-900 p-4">
        <Button
          variant="secondary"
          size="lg"
          className="h-12"
          onClick={() => dispatch({ type: 'NAV_PREV' })}
        >
          <ChevronLeft className="h-4 w-4" />
          <span>Slide Anterior</span>
        </Button>
        <Button
          variant="default"
          size="lg"
          className="h-12"
          onClick={() => dispatch({ type: 'NAV_NEXT' })}
        >
          <span>Próximo Slide</span>
          <ChevronRight className="h-4 w-4" />
        </Button>
      </footer>
    </div>
  )
}
