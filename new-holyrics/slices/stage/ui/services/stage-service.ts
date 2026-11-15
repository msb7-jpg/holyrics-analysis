/**
 * @module @slices/stage/ui/services/stage-service
 * @description Serviço cliente HTTP para controle de alertas Lower Thirds, cronômetros e mensagens de púlpito.
 */

import { httpClient } from '@ui/core/network/http-client'
import type {
  CreateAlertInput,
  LiveStageState,
  LowerThirdAlert,
  PastorMessage,
  StageTimerState,
  TimerActionInput,
} from '../../contracts/stage-contracts'
import { LiveStageStateSchema } from '../../contracts/stage-contracts'

export const stageService = {
  /**
   * Consulta o estado consolidado atual do palco, alertas e cronômetro.
   *
   * @param signal - Sinal de cancelamento opcional.
   * @returns Estado validado `LiveStageState`.
   */
  async getState(signal?: AbortSignal): Promise<LiveStageState> {
    return httpClient.get('/api/stage/state', {
      signal,
      schema: LiveStageStateSchema,
    })
  },

  /**
   * Dispara um novo alerta sobreposto Lower Third (RF-24).
   *
   * @param input - Dados do alerta de emergência.
   * @returns Alerta criado no servidor.
   */
  async triggerAlert(input: CreateAlertInput): Promise<LowerThirdAlert> {
    return httpClient.post('/api/stage/alert', input)
  },

  /**
   * Oculta o alerta ativo ou limpa toda a fila de alertas.
   *
   * @param clearAll - Se verdadeiro, remove todos os alertas da fila.
   * @returns Estado atualizado do palco.
   */
  async dismissAlert(clearAll = false): Promise<LiveStageState> {
    return httpClient.delete('/api/stage/alert', {
      searchParams: { all: clearAll },
    })
  },

  /**
   * Envia comando para iniciar, pausar ou reiniciar o cronômetro sincronizado (RF-25).
   *
   * @param input - Parâmetros de controle do cronômetro.
   * @returns Novo estado do timer.
   */
  async controlTimer(input: TimerActionInput): Promise<StageTimerState> {
    return httpClient.post('/api/stage/timer', input)
  },

  /**
   * Envia ou limpa um recado exclusivo para o monitor de retorno do pastor.
   *
   * @param message - Texto do recado (ou null para limpar).
   * @param level - Nível de prioridade visual.
   * @returns Objeto da mensagem pastoral atualizada.
   */
  async setPastorMessage(
    message: string | null,
    level: PastorMessage['level'] = 'info',
  ): Promise<PastorMessage | null> {
    return httpClient.post('/api/stage/pastor-message', { message, level })
  },
}
