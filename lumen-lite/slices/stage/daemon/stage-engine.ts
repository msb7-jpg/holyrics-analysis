/**
 * @module @slices/stage/daemon/stage-engine
 * @description Motor em tempo real para fila de alertas Lower Thirds, cronômetros sincronizados e recados de púlpito.
 *
 * ## Responsabilidades:
 * - Gerenciar a fila de alertas de emergência (RF-24) com avanço automático após expiração.
 * - Calcular contagens regressivas por duração determinada ou horário exato (`getSecondsExact` de `CountdownUtils.java` - RF-25).
 * - Formatar tempo (`MM:SS` ou `HH:MM:SS`) com suporte a saldo negativo quando o tempo do pregador estoura.
 * - Notificar ouvintes (WebSocket pub/sub) a cada mudança de estado.
 */

import type {
  CreateAlertInput,
  LiveStageState,
  LowerThirdAlert,
  PastorMessage,
  TimerActionInput,
} from '../contracts/stage-contracts'
import {
  CreateAlertInputSchema,
  INITIAL_LIVE_STAGE_STATE,
  TimerActionInputSchema,
} from '../contracts/stage-contracts'

export type StageStateListener = (state: LiveStageState) => void

/**
 * Calcula os segundos restantes até um horário específico "HH:mm" (espelhando `CountdownUtils.getSecondsExact` do Holyrics).
 *
 * @param targetTime - Horário alvo no formato "HH:mm".
 * @param nowDate - Data de referência (padrão: `new Date()`).
 * @returns Total de segundos até o horário alvo.
 */
export function calculateSecondsUntilTargetTime(targetTime: string, nowDate: Date = new Date()): number {
  const parts = targetTime.split(':')
  const targetHour = parseInt(parts[0] || '0', 10)
  const targetMinute = parseInt(parts[1] || '0', 10)

  const targetTotalSeconds = targetHour * 3600 + targetMinute * 60
  const currentTotalSeconds =
    nowDate.getHours() * 3600 + nowDate.getMinutes() * 60 + nowDate.getSeconds()

  if (targetTotalSeconds <= currentTotalSeconds) {
    return targetTotalSeconds + 86400 - currentTotalSeconds
  }
  return targetTotalSeconds - currentTotalSeconds
}

/**
 * Formata uma duração em segundos no padrão tabular `MM:SS` ou `HH:MM:SS` (baseado em `CountdownUtils.formatTime`).
 *
 * @param rawSeconds - Segundos restantes (pode ser negativo caso o tempo tenha estourado).
 * @param zeroFill - Se deve preencher minutos com zero à esquerda.
 * @returns String formatada para exibição no cronômetro.
 */
export function formatCountdownTime(rawSeconds: number, zeroFill = true): string {
  const isNegative = rawSeconds < 0
  const totalSeconds = Math.abs(Math.floor(rawSeconds))
  const hours = Math.floor(totalSeconds / 3600)
  const minutes = Math.floor((totalSeconds % 3600) / 60)
  const seconds = totalSeconds % 60

  const secStr = String(seconds).padStart(2, '0')
  const minStr = zeroFill || hours > 0 ? String(minutes).padStart(2, '0') : String(minutes)
  const prefix = isNegative ? '-' : ''

  if (hours > 0) {
    const hourStr = String(hours).padStart(2, '0')
    return `${prefix}${hourStr}:${minStr}:${secStr}`
  }

  return `${prefix}${minStr}:${secStr}`
}

/**
 * Coordenador de estado em tempo real de Palco, Alertas e Cronômetros.
 */
export class StageEngine {
  private state: LiveStageState = structuredClone(INITIAL_LIVE_STAGE_STATE)
  private listeners: Set<StageStateListener> = new Set()
  private alertTimerHandle: ReturnType<typeof setTimeout> | null = null

  /**
   * Retorna o estado atual consolidado de palco e alertas.
   *
   * @returns Snapshot imutável de `LiveStageState`.
   */
  getState(): LiveStageState {
    return this.state
  }

  /**
   * Enfileira um novo alerta Lower Third e ativa imediatamente caso não haja outro alerta ativo.
   *
   * @param input - Dados do alerta a ser exibido.
   * @returns O alerta criado e validado.
   */
  enqueueAlert(input: CreateAlertInput): LowerThirdAlert {
    const parsed = CreateAlertInputSchema.parse(input)
    const now = Date.now()
    const durationSeconds = parsed.durationSeconds ?? 15

    const alertItem: LowerThirdAlert = {
      id: `alert_${now}_${Math.random().toString(36).slice(2, 7)}`,
      label: parsed.label ?? 'AVISO IMPORTANTE',
      message: parsed.message,
      style: parsed.style ?? 'classic',
      position: parsed.position ?? 'bottom',
      durationSeconds,
      createdAt: now,
      expiresAt: now + durationSeconds * 1000,
    }

    if (!this.state.activeAlert) {
      this.state = {
        ...this.state,
        activeAlert: alertItem,
      }
      this.scheduleAlertExpiration(durationSeconds * 1000)
    } else {
      this.state = {
        ...this.state,
        alertQueue: [...this.state.alertQueue, alertItem],
      }
    }

    this.notifyListeners()
    return alertItem
  }

  /**
   * Encerra o alerta ativo imediatamente e avança para o próximo da fila (se existir).
   */
  dismissActiveAlert(): void {
    if (this.alertTimerHandle) {
      clearTimeout(this.alertTimerHandle)
      this.alertTimerHandle = null
    }

    if (this.state.alertQueue.length > 0) {
      const [nextAlert, ...remainingQueue] = this.state.alertQueue
      const now = Date.now()
      const activatedNext: LowerThirdAlert = {
        ...nextAlert,
        createdAt: now,
        expiresAt: now + nextAlert.durationSeconds * 1000,
      }
      this.state = {
        ...this.state,
        activeAlert: activatedNext,
        alertQueue: remainingQueue,
      }
      this.scheduleAlertExpiration(activatedNext.durationSeconds * 1000)
    } else {
      this.state = {
        ...this.state,
        activeAlert: null,
      }
    }

    this.notifyListeners()
  }

  /**
   * Limpa todos os alertas ativos e pendentes na fila.
   */
  clearAllAlerts(): void {
    if (this.alertTimerHandle) {
      clearTimeout(this.alertTimerHandle)
      this.alertTimerHandle = null
    }

    this.state = {
      ...this.state,
      activeAlert: null,
      alertQueue: [],
    }
    this.notifyListeners()
  }

  /**
   * Executa um comando no cronômetro sincronizado de palco (iniciar, pausar, resetar).
   *
   * @param input - Ação e parâmetros opcionais de configuração do timer.
   * @returns Novo estado do cronômetro.
   */
  controlTimer(input: TimerActionInput): LiveStageState['timer'] {
    const parsed = TimerActionInputSchema.parse(input)
    const now = Date.now()
    const currentTimer = this.state.timer

    const mode = parsed.mode ?? currentTimer.mode
    const label = parsed.label ?? currentTimer.label
    const textBefore = parsed.textBefore ?? currentTimer.textBefore
    const textAfter = parsed.textAfter ?? currentTimer.textAfter

    let durationSeconds = parsed.durationSeconds ?? currentTimer.durationSeconds
    if (mode === 'TARGET_TIME' && parsed.targetTime) {
      durationSeconds = calculateSecondsUntilTargetTime(parsed.targetTime)
    }

    if (parsed.action === 'start') {
      const baseSeconds =
        parsed.durationSeconds !== undefined || parsed.targetTime !== undefined
          ? durationSeconds
          : (currentTimer.pausedRemainingSeconds ?? durationSeconds)

      this.state = {
        ...this.state,
        timer: {
          ...currentTimer,
          isRunning: true,
          mode,
          label,
          durationSeconds,
          targetTime: parsed.targetTime ?? currentTimer.targetTime,
          startedAt: now,
          endTime: now + baseSeconds * 1000,
          pausedRemainingSeconds: null,
          textBefore,
          textAfter,
        },
      }
    } else if (parsed.action === 'pause') {
      const remainingSeconds =
        currentTimer.isRunning && currentTimer.endTime
          ? Math.round((currentTimer.endTime - now) / 1000)
          : (currentTimer.pausedRemainingSeconds ?? currentTimer.durationSeconds)

      this.state = {
        ...this.state,
        timer: {
          ...currentTimer,
          isRunning: false,
          pausedRemainingSeconds: remainingSeconds,
          endTime: null,
        },
      }
    } else if (parsed.action === 'reset') {
      this.state = {
        ...this.state,
        timer: {
          ...currentTimer,
          isRunning: false,
          mode,
          label,
          durationSeconds,
          targetTime: parsed.targetTime ?? currentTimer.targetTime,
          startedAt: null,
          endTime: null,
          pausedRemainingSeconds: durationSeconds,
          textBefore,
          textAfter,
        },
      }
    }

    this.notifyListeners()
    return this.state.timer
  }

  /**
   * Define ou limpa a mensagem interna exclusiva para o monitor do pastor no púlpito.
   *
   * @param message - Texto do recado (vazio ou null para limpar).
   * @param level - Nível de urgência visual (`info`, `warning`, `urgent`).
   * @returns A mensagem definida ou null.
   */
  setPastorMessage(
    message: string | null,
    level: PastorMessage['level'] = 'info',
  ): PastorMessage | null {
    if (!message || !message.trim()) {
      this.state = {
        ...this.state,
        pastorMessage: null,
      }
      this.notifyListeners()
      return null
    }

    const pastorMsg: PastorMessage = {
      id: `pmsg_${Date.now()}`,
      message: message.trim(),
      level,
      createdAt: Date.now(),
    }

    this.state = {
      ...this.state,
      pastorMessage: pastorMsg,
    }
    this.notifyListeners()
    return pastorMsg
  }

  /**
   * Alterna o modo de letras em caixa alta no monitor de retorno (inspirado em `StageViewUtils.isUpperCase`).
   *
   * @param enabled - Se o texto do palco deve ser forçado em maiúsculas.
   */
  setUppercaseMode(enabled: boolean): void {
    this.state = {
      ...this.state,
      uppercaseMode: enabled,
    }
    this.notifyListeners()
  }

  /**
   * Inscreve um ouvinte para receber atualizações de estado do palco/alertas.
   *
   * @param listener - Callback acionado em cada transição.
   * @returns Função para remover a inscrição.
   */
  subscribe(listener: StageStateListener): () => void {
    this.listeners.add(listener)
    return () => {
      this.listeners.delete(listener)
    }
  }

  private scheduleAlertExpiration(delayMs: number): void {
    if (this.alertTimerHandle) {
      clearTimeout(this.alertTimerHandle)
    }
    this.alertTimerHandle = setTimeout(() => {
      this.dismissActiveAlert()
    }, delayMs)
  }

  private notifyListeners(): void {
    for (const listener of this.listeners) {
      listener(this.state)
    }
  }
}
