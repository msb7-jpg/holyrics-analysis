/**
 * @module @slices/stage/ui/components/StageControlModal
 * @description Modal unificado no Console do Operador para disparo de Alertas Lower Thirds (RF-24),
 * controle de Cronômetros de Pregação/Contagens Regressivas (RF-25) e Recados ao Púlpito (RF-02).
 */

import { useState } from 'react'
import { X, Bell, Timer, MessageSquare, Play, Pause, RotateCcw, Trash2, ExternalLink } from 'lucide-react'
import { Button } from '@ui/components/ui/button'
import { Input } from '@ui/components/ui/input'
import { Badge } from '@ui/components/ui/badge'
import type { LiveStageState, AlertStyle } from '../../contracts/stage-contracts'
import { stageService } from '../services/stage-service'

export interface StageControlModalProps {
  isOpen: boolean
  stageState: LiveStageState
  onClose: () => void
}

/**
 * Modal de gerenciamento de Palco, Alertas Lower Thirds e Cronômetros.
 *
 * @param props - Propriedades do modal.
 * @param props.isOpen - Indica se o modal está aberto.
 * @param props.stageState - Estado reativo atual do palco e alertas.
 * @param props.onClose - Callback para fechar o modal.
 * @returns Elemento JSX do modal ou null se fechado.
 */
export function StageControlModal({ isOpen, stageState, onClose }: StageControlModalProps) {
  // Estado do formulário de Lower Third Alert (RF-24)
  const [alertLabel, setAlertLabel] = useState('BERÇÁRIO / AVISO')
  const [alertMessage, setAlertMessage] = useState('')
  const [alertStyle, setAlertStyle] = useState<AlertStyle>('classic')
  const [alertDuration, setAlertDuration] = useState(15)
  const [alertError, setAlertError] = useState<string | null>(null)
  const [alertSuccess, setAlertSuccess] = useState(false)

  // Estado do formulário de Cronômetro (RF-25)
  const [timerLabel, setTimerLabel] = useState(stageState.timer.label || 'Cronômetro')
  const [timerMinutes, setTimerMinutes] = useState('30')
  const [targetTime, setTargetTime] = useState('19:30')
  const [timerMode, setTimerMode] = useState<'DURATION' | 'TARGET_TIME'>('DURATION')
  const [timerError, setTimerError] = useState<string | null>(null)

  // Estado do recado exclusivo ao pastor
  const [pastorNote, setPastorNote] = useState(stageState.pastorMessage?.message || '')

  if (!isOpen) return null

  const handleTriggerAlert = async () => {
    if (!alertMessage.trim()) {
      setAlertError('Digite a mensagem do alerta para disparar.')
      setAlertSuccess(false)
      return
    }
    setAlertError(null)
    await stageService.triggerAlert({
      label: alertLabel,
      message: alertMessage.trim(),
      style: alertStyle,
      durationSeconds: alertDuration,
    })
    setAlertMessage('')
    setAlertSuccess(true)
    setTimeout(() => setAlertSuccess(false), 2500)
  }

  const handleStartTimer = async (projectOnScreen = false) => {
    if (timerMode === 'TARGET_TIME') {
      if (!/^([01]\d|2[0-3]):([0-5]\d)$/.test(targetTime.trim())) {
        setTimerError('Informe um horário válido no formato HH:mm (ex: 17:00).')
        return
      }
    } else {
      const mins = Number(timerMinutes)
      if (!Number.isFinite(mins) || mins < 1 || mins > 600) {
        setTimerError('Informe uma duração entre 1 e 600 minutos.')
        return
      }
    }
    setTimerError(null)
    await stageService.controlTimer({
      action: 'start',
      mode: timerMode,
      label: timerLabel,
      durationSeconds: Math.max(1, Number(timerMinutes) || 30) * 60,
      targetTime: timerMode === 'TARGET_TIME' ? targetTime.trim() : undefined,
      projectOnScreen,
    })
  }

  const handleSendPastorMessage = async () => {
    await stageService.setPastorMessage(pastorNote.trim() || null, 'warning')
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/75 p-4 backdrop-blur-xs">
      <div className="flex max-h-[90vh] w-full max-w-3xl flex-col rounded-xl border border-white/10 bg-slate-900 text-slate-100 shadow-2xl overflow-hidden">
        {/* Cabeçalho */}
        <div className="flex items-center justify-between border-b border-white/10 px-5 py-3.5 bg-slate-800/60">
          <div className="flex items-center gap-2.5">
            <Timer className="h-4 w-4 text-sky-400" />
            <h2 className="text-sm font-semibold text-white">
              Palco, Cronômetros & Alertas Lower Thirds
            </h2>
          </div>

          <div className="flex items-center gap-2">
            <Button
              variant="outline"
              size="sm"
              onClick={() => window.open('/stage', '_blank', 'noopener,noreferrer')}
            >
              <ExternalLink className="h-3.5 w-3.5 text-sky-400" />
              <span>Abrir Monitor de Palco</span>
            </Button>
            <Button variant="ghost" size="icon" onClick={onClose}>
              <X className="h-4 w-4" />
            </Button>
          </div>
        </div>

        {/* Conteúdo */}
        <div className="grid grid-cols-1 gap-6 p-5 overflow-y-auto md:grid-cols-2">
          {/* Coluna 1: Alertas Lower Thirds (RF-24) */}
          <div className="flex flex-col gap-3 rounded-xl border border-white/10 bg-slate-950/50 p-4">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-2 text-xs font-semibold text-sky-400 uppercase tracking-wider">
                <Bell className="h-3.5 w-3.5" />
                <span>Alertas & Lower Thirds</span>
              </div>
              {stageState.activeAlert && <Badge variant="live">NO AR</Badge>}
            </div>

            <div className="flex flex-col gap-2 text-xs">
              <label className="text-slate-400">Categoria / Rótulo</label>
              <Input
                value={alertLabel}
                onChange={(event) => setAlertLabel(event.target.value)}
                placeholder="Ex: BERÇÁRIO, ESTACIONAMENTO..."
              />

              <label className="text-slate-400">Mensagem de Alerta</label>
              <Input
                value={alertMessage}
                onChange={(event) => {
                  setAlertMessage(event.target.value)
                  if (alertError) setAlertError(null)
                }}
                placeholder="Ex: Pais do pequeno Davi, comparecer ao berçário"
                className={alertError ? 'border-rose-500/80 focus-visible:ring-rose-500/40' : undefined}
              />
              {alertError && (
                <p className="text-[11px] font-medium text-rose-400">{alertError}</p>
              )}
              {alertSuccess && (
                <p className="text-[11px] font-medium text-emerald-400">Alerta disparado no telão.</p>
              )}

              <div className="flex flex-wrap items-end justify-between gap-3 pt-1">
                <div>
                  <label className="mb-1 block text-slate-400">Estilo</label>
                  <div className="flex flex-wrap gap-1.5">
                    {(
                      [
                        { id: 'classic', label: 'Clássico' },
                        { id: 'urgent', label: 'Urgente' },
                        { id: 'minimal', label: 'Discreto' },
                      ] as const
                    ).map((styleOption) => (
                      <Button
                        key={styleOption.id}
                        size="sm"
                        variant={alertStyle === styleOption.id ? 'activeInfo' : 'secondary'}
                        onClick={() => setAlertStyle(styleOption.id)}
                      >
                        {styleOption.label}
                      </Button>
                    ))}
                  </div>
                </div>
                <div className="w-28 shrink-0">
                  <label className="mb-1 block text-slate-400">Duração (seg)</label>
                  <Input
                    type="number"
                    value={String(alertDuration)}
                    onChange={(event) => setAlertDuration(Number(event.target.value) || 15)}
                  />
                </div>
              </div>

              <div className="mt-2 flex items-center gap-2">
                <Button className="flex-1" onClick={handleTriggerAlert}>
                  <Bell className="h-3.5 w-3.5" />
                  <span>Disparar Alerta</span>
                </Button>
                {stageState.activeAlert && (
                  <Button
                    variant="panic"
                    onClick={() => stageService.dismissAlert(true)}
                    title="Ocultar alerta imediatamente"
                  >
                    <Trash2 className="h-3.5 w-3.5" />
                    <span>Limpar</span>
                  </Button>
                )}
              </div>

              {/* Atalhos Rápidos de Igreja */}
              <div className="mt-2 flex flex-wrap gap-1 border-t border-white/5 pt-2">
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => {
                    setAlertLabel('BERÇÁRIO')
                    setAlertMessage('Solicitamos a presença dos pais de ')
                  }}
                >
                  + Berçário
                </Button>
                <Button
                  variant="ghost"
                  size="sm"
                  onClick={() => {
                    setAlertLabel('ESTACIONAMENTO')
                    setAlertStyle('urgent')
                    setAlertMessage('Proprietário do veículo placa ')
                  }}
                >
                  + Veículo
                </Button>
              </div>
            </div>
          </div>

          {/* Coluna 2: Cronômetros (RF-25) & Recado de Púlpito */}
          <div className="flex flex-col gap-4">
            <div className="flex flex-col gap-3 rounded-xl border border-white/10 bg-slate-950/50 p-4">
              <div className="flex items-center justify-between">
                <div className="flex items-center gap-2 text-xs font-semibold text-sky-400 uppercase tracking-wider">
                  <Timer className="h-3.5 w-3.5" />
                  <span>Cronômetro de Palco & Culto</span>
                </div>
                {stageState.timer.isRunning && <Badge variant="success">RODANDO</Badge>}
              </div>

              <div className="flex gap-1.5">
                <Button
                  size="sm"
                  variant={timerMode === 'DURATION' ? 'activeInfo' : 'secondary'}
                  onClick={() => {
                    setTimerMode('DURATION')
                    setTimerError(null)
                  }}
                >
                  Contagem (Minutos)
                </Button>
                <Button
                  size="sm"
                  variant={timerMode === 'TARGET_TIME' ? 'activeInfo' : 'secondary'}
                  onClick={() => {
                    setTimerMode('TARGET_TIME')
                    setTimerError(null)
                  }}
                >
                  Até Horário (HH:mm)
                </Button>
              </div>

              <div className="grid grid-cols-2 gap-2 text-xs">
                <div>
                  <label className="mb-1 block text-slate-400">Rótulo</label>
                  <Input
                    value={timerLabel}
                    onChange={(event) => setTimerLabel(event.target.value)}
                  />
                </div>
                {timerMode === 'DURATION' ? (
                  <div>
                    <label className="mb-1 block text-slate-400">Minutos (a partir de agora)</label>
                    <Input
                      type="number"
                      min={1}
                      max={600}
                      value={timerMinutes}
                      onChange={(event) => {
                        setTimerMinutes(event.target.value)
                        if (timerError) setTimerError(null)
                      }}
                    />
                  </div>
                ) : (
                  <div>
                    <label className="mb-1 block text-slate-400">Horário Alvo (ex: 17:00)</label>
                    <Input
                      type="time"
                      value={targetTime}
                      onChange={(event) => {
                        setTargetTime(event.target.value)
                        if (timerError) setTimerError(null)
                      }}
                      className={timerError ? 'border-rose-500/80' : undefined}
                    />
                  </div>
                )}
              </div>
              {timerError && (
                <p className="text-[11px] font-medium text-rose-400">{timerError}</p>
              )}

              <div className="flex flex-wrap items-center gap-2 pt-1">
                <Button onClick={() => handleStartTimer(false)}>
                  <Play className="h-3.5 w-3.5" />
                  <span>Iniciar no Palco</span>
                </Button>
                <Button variant="secondary" onClick={() => handleStartTimer(true)}>
                  <span>Projetar no Telão</span>
                </Button>
                <Button
                  variant="outline"
                  size="icon"
                  onClick={() => stageService.controlTimer({ action: 'pause' })}
                  title="Pausar"
                >
                  <Pause className="h-3.5 w-3.5" />
                </Button>
                <Button
                  variant="outline"
                  size="icon"
                  onClick={() => stageService.controlTimer({ action: 'reset' })}
                  title="Reiniciar"
                >
                  <RotateCcw className="h-3.5 w-3.5" />
                </Button>
              </div>
            </div>

            {/* Recado Direto ao Pastor no Púlpito */}
            <div className="flex flex-col gap-2.5 rounded-xl border border-white/10 bg-slate-950/50 p-4 text-xs">
              <div className="flex items-center gap-2 font-semibold text-amber-400 uppercase tracking-wider">
                <MessageSquare className="h-3.5 w-3.5" />
                <span>Recado Exclusivo ao Púlpito</span>
              </div>
              <div className="flex gap-2">
                <Input
                  value={pastorNote}
                  onChange={(event) => setPastorNote(event.target.value)}
                  placeholder="Visível apenas para o pastor no palco..."
                />
                <Button variant="secondary" onClick={handleSendPastorMessage}>
                  Enviar
                </Button>
                {stageState.pastorMessage && (
                  <Button
                    variant="ghost"
                    onClick={() => {
                      setPastorNote('')
                      stageService.setPastorMessage(null)
                    }}
                  >
                    Limpar
                  </Button>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}
