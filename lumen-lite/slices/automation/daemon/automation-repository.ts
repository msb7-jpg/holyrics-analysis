/**
 * @module @slices/automation/daemon/automation-repository
 * @description Repositório para persistência atômica e leitura do arquivo `automation.json`.
 *
 * ## Responsabilidades:
 * - Ler e validar o grafo de automações usando Zod v4 (`AutomationGraphSchema`).
 * - Gravação atômica em disco utilizando arquivo temporário (`.tmp`) e renomeação atômica.
 * - Criar arquivo padrão inicial com regras básicas (`DEFAULT_AUTOMATION_GRAPH`) se inexistente.
 *
 * ## Comunicação e Fronteiras:
 * - Consumido pelo DAG runner e pelos endpoints de configuração da API REST `/api/automation`.
 */

import { existsSync, readFileSync, renameSync, unlinkSync, writeFileSync } from 'node:fs'
import { join } from 'node:path'
import { createLogger } from '@lumen/core'
import {
  AutomationGraphSchema,
  DEFAULT_AUTOMATION_GRAPH,
  type AutomationGraph,
  validateGraphAcyclic,
} from '../contracts/automation-contracts'

const logger = createLogger('automation-repo')

/**
 * Repositório atômico de persistência e validação para o arquivo automation.json.
 */
export class AutomationRepository {
  private filePath: string

  /**
   * Inicializa o repositório no caminho especificado ou na pasta padrão.
   *
   * @param customFilePath - Caminho opcional do arquivo `automation.json`.
   */
  constructor(customFilePath?: string) {
    this.filePath = customFilePath ?? join(process.cwd(), 'automation.json')
  }

  /**
   * Carrega o grafo de automações do disco com validação Zod.
   *
   * @returns Grafo validado de automações.
   */
  public load(): AutomationGraph {
    if (!existsSync(this.filePath)) {
      logger.info({ filePath: this.filePath }, 'Arquivo automation.json inexistente. Criando grafo padrão.')
      this.save(DEFAULT_AUTOMATION_GRAPH)
      return DEFAULT_AUTOMATION_GRAPH
    }

    try {
      const rawContent = readFileSync(this.filePath, 'utf-8')
      const parsedJson = JSON.parse(rawContent)
      const graph = AutomationGraphSchema.parse(parsedJson)

      const validation = validateGraphAcyclic(graph.nodes, graph.edges)
      if (!validation.isValid) {
        logger.warn({ error: validation.error }, 'Grafo em disco contém ciclos ou inconsistências. Usando padrão.')
        return DEFAULT_AUTOMATION_GRAPH
      }

      return graph
    } catch (error) {
      logger.error({ error, filePath: this.filePath }, 'Falha ao carregar automation.json. Usando grafo padrão.')
      return DEFAULT_AUTOMATION_GRAPH
    }
  }

  /**
   * Salva o grafo de automações de forma atômica protegida contra corrupção.
   *
   * @param graph - Grafo completo a ser persistido.
   */
  public save(graph: AutomationGraph): void {
    const validated = AutomationGraphSchema.parse(graph)

    const acyclicCheck = validateGraphAcyclic(validated.nodes, validated.edges)
    if (!acyclicCheck.isValid) {
      throw new Error(`Grafo inválido para persistência: ${acyclicCheck.error}`)
    }

    const tempFilePath = `${this.filePath}.tmp`
    const serialized = JSON.stringify(validated, null, 2)

    try {
      writeFileSync(tempFilePath, serialized, 'utf-8')
      renameSync(tempFilePath, this.filePath)
      logger.info({ nodesCount: validated.nodes.length }, 'automation.json salvo com sucesso')
    } catch (error) {
      if (existsSync(tempFilePath)) {
        unlinkSync(tempFilePath)
      }
      logger.error({ error }, 'Erro crítico ao gravar automation.json atômico')
      throw error
    }
  }

  /**
   * Retorna o caminho do arquivo gerenciado.
   *
   * @returns Caminho absoluto ou relativo do arquivo.
   */
  public getFilePath(): string {
    return this.filePath
  }
}
