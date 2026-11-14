/**
 * @module @slices/bible/daemon/bible-repository
 * @description Repositório SQLite WAL e motor de busca FTS5 para Escrituras Sagradas do LumenLite.
 *
 * ## Responsabilidades:
 * - Persistir traduções bíblicas completas (cânon de 66 livros e ~31.106 versículos por versão).
 * - Indexar textos bíblicos via SQLite FTS5 com BM25 e snippets destacados sem acentos (unicode61).
 * - Realizar lookup de alta performance por referência (ex: João 3:16 ou Salmos 23:1-6).
 * - Realizar pesquisas textuais globais por termos e palavras-chave em sub-milissegundos.
 *
 * ## Comunicação e Fronteiras:
 * - Recebe conexão ativa do `bun:sqlite` em modo WAL.
 * - Expõe consultas rápidas consumidas pelo roteador `apps/daemon/src/api/routes/bible-router.ts`.
 */

import type { Database } from 'bun:sqlite'
import { createLogger } from '@lumen/core/logger'
import type {
  BibleVerse,
  BibleSearchResult,
  BibleVersionSummary,
  ParsedVerseRef,
  ExtractedBibleData,
} from '../contracts/bible-contracts'
import { extractHolyricsBibFile } from './bib-extractor'

const logger = createLogger('bible-repository')

/**
 * Repositório SQLite em modo WAL e índice FTS5 para armazenamento e busca bíblica de alta velocidade.
 */
export class BibleRepository {
  private database: Database

  /**
   * Construtor do repositório bíblico.
   *
   * @param database - Instância aberta do Database SQLite WAL.
   */
  constructor(database: Database) {
    this.database = database
    this.initTables()
  }

  /**
   * Inicializa as tabelas relacionais e a tabela virtual FTS5 com triggers de sincronização.
   */
  private initTables(): void {
    this.database.run(`
      CREATE TABLE IF NOT EXISTS bibles (
        id TEXT PRIMARY KEY,
        version TEXT NOT NULL,
        name TEXT NOT NULL,
        language TEXT NOT NULL,
        copyright TEXT
      );
    `)

    this.database.run(`
      CREATE TABLE IF NOT EXISTS bible_books (
        id TEXT PRIMARY KEY,
        bible_id TEXT NOT NULL,
        number INTEGER NOT NULL,
        name TEXT NOT NULL,
        abbrev TEXT NOT NULL,
        testament TEXT NOT NULL
      );
    `)

    this.database.run(`
      CREATE TABLE IF NOT EXISTS bible_verses (
        id TEXT PRIMARY KEY,
        bible_id TEXT NOT NULL,
        book_number INTEGER NOT NULL,
        book_name TEXT NOT NULL,
        book_abbrev TEXT NOT NULL,
        chapter INTEGER NOT NULL,
        verse INTEGER NOT NULL,
        text TEXT NOT NULL
      );
    `)

    this.database.run(`
      CREATE INDEX IF NOT EXISTS idx_bible_lookup 
      ON bible_verses (bible_id, book_number, chapter, verse);
    `)

    this.database.run(`
      CREATE VIRTUAL TABLE IF NOT EXISTS bible_verses_fts USING fts5(
        verse_id UNINDEXED,
        bible_id UNINDEXED,
        book_number UNINDEXED,
        chapter UNINDEXED,
        verse UNINDEXED,
        book_name,
        book_abbrev,
        text,
        tokenize='unicode61 remove_diacritics 2'
      );
    `)
  }

  /**
   * Realiza semente da base bíblica a partir de um arquivo `.bib` do Holyrics.
   *
   * @param bibFilePath - Caminho do arquivo `.bib` legado no disco.
   * @param customVersionId - ID opcional da versão (ex: 'ACF').
   * @param customVersionName - Nome legível opcional (ex: 'Almeida Corrigida e Fiel').
   */
  seedFromBibFile(bibFilePath: string, customVersionId?: string, customVersionName?: string): void {
    logger.info({ bibFilePath }, 'Extraindo arquivo .bib legado...')
    const extractedData = extractHolyricsBibFile(bibFilePath)
    this.seedFromExtractedData(extractedData, customVersionId, customVersionName)
  }

  /**
   * Grava dados extraídos da Bíblia em lote com transação atômica de alta performance.
   *
   * @param extracted - Objeto estruturado contendo livros e versículos.
   * @param customVersionId - ID da versão para identificação no sistema.
   * @param customVersionName - Nome completo da versão bíblica.
   */
  seedFromExtractedData(
    extracted: ExtractedBibleData,
    customVersionId = 'ACF',
    customVersionName?: string,
  ): void {
    const versionId = customVersionId.toUpperCase()
    const versionName = customVersionName || extracted.version

    const existingCount = this.database
      .query<{ count: number }, [string]>('SELECT count(*) as count FROM bibles WHERE id = ?')
      .get(versionId)

    if (existingCount && existingCount.count > 0) {
      logger.info({ versionId }, 'Versão bíblica já instalada no banco. Ignorando seed.')
      return
    }

    logger.info({ versionId, totalVerses: extracted.verses.length }, 'Persistindo Bíblia no SQLite WAL...')

    const insertVersionStmt = this.database.prepare(`
      INSERT INTO bibles (id, version, name, language, copyright)
      VALUES (?, ?, ?, ?, ?)
    `)

    const insertBookStmt = this.database.prepare(`
      INSERT INTO bible_books (id, bible_id, number, name, abbrev, testament)
      VALUES (?, ?, ?, ?, ?, ?)
    `)

    const insertVerseStmt = this.database.prepare(`
      INSERT INTO bible_verses (id, bible_id, book_number, book_name, book_abbrev, chapter, verse, text)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `)

    const insertFtsStmt = this.database.prepare(`
      INSERT INTO bible_verses_fts (verse_id, bible_id, book_number, chapter, verse, book_name, book_abbrev, text)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?)
    `)

    // Mapeamento de abreviações dos livros por número
    const bookLookup = new Map<number, { name: string; abbreviation: string }>()
    for (const bookItem of extracted.books) {
      bookLookup.set(bookItem.number, {
        name: bookItem.name,
        abbreviation: bookItem.abbreviation,
      })
    }

    const transactionRunner = this.database.transaction(() => {
      insertVersionStmt.run(
        versionId,
        versionId,
        versionName,
        extracted.language,
        'Domínio Público / Holyrics Database',
      )

      for (const bookItem of extracted.books) {
        insertBookStmt.run(
          `${versionId}_${bookItem.number}`,
          versionId,
          bookItem.number,
          bookItem.name,
          bookItem.abbreviation,
          bookItem.testament,
        )
      }

      for (const verseItem of extracted.verses) {
        const bookInfo = bookLookup.get(verseItem.bookNumber)
        const bookName = bookInfo ? bookInfo.name : `Livro ${verseItem.bookNumber}`
        const bookAbbrev = bookInfo ? bookInfo.abbreviation : `L${verseItem.bookNumber}`
        const verseId = `${versionId}_${verseItem.bookNumber}_${verseItem.chapter}_${verseItem.verse}`

        insertVerseStmt.run(
          verseId,
          versionId,
          verseItem.bookNumber,
          bookName,
          bookAbbrev,
          verseItem.chapter,
          verseItem.verse,
          verseItem.text,
        )

        insertFtsStmt.run(
          verseId,
          versionId,
          verseItem.bookNumber,
          verseItem.chapter,
          verseItem.verse,
          bookName,
          bookAbbrev,
          verseItem.text,
        )
      }
    })

    transactionRunner()
    logger.info({ versionId, versesCount: this.count(versionId) }, 'Bíblia persistida com sucesso no SQLite FTS5')
  }

  /**
   * Localiza versículos com base em referência bíblica estruturada.
   *
   * @param reference - Objeto de referência processado pelo micro-parser.
   * @param versionId - ID opcional da versão (padrão primeira versão disponível ou ACF).
   * @returns Lista ordenada de versículos encontrados para a referência.
   */
  lookupReference(reference: ParsedVerseRef, versionId = 'ACF'): BibleVerse[] {
    const endVerse = reference.verseEnd ?? reference.verseStart

    let querySql = `
      SELECT id, bible_id as bibleId, book_number as bookNumber, book_name as bookName,
             book_abbrev as bookAbbrev, chapter, verse, text
      FROM bible_verses
      WHERE bible_id = ?
    `
    const parameters: (string | number)[] = [versionId]

    if (reference.bookNumber) {
      querySql += ' AND book_number = ?'
      parameters.push(reference.bookNumber)
    } else {
      querySql += ' AND lower(book_name) = lower(?)'
      parameters.push(reference.book)
    }

    querySql += ' AND chapter = ? AND verse >= ? AND verse <= ? ORDER BY verse ASC'
    parameters.push(reference.chapter, reference.verseStart, endVerse)

    return this.database.query<BibleVerse, (string | number)[]>(querySql).all(...parameters)
  }

  /**
   * Busca textual instantânea por palavras ou termos em toda a Bíblia via FTS5.
   *
   * @param searchTerm - Termo ou frase a ser pesquisada.
   * @param options - Parâmetros opcionais de paginação e versão.
   * @param options.versionId - Identificador opcional da versão bíblica (padrão ACF).
   * @param options.limit - Limite máximo de registros retornados (padrão 25).
   * @returns Resultados ranqueados com trechos em destaque.
   */
  search(
    searchTerm: string,
    options: { versionId?: string; limit?: number } = {},
  ): BibleSearchResult[] {
    const limit = options.limit || 25
    const versionId = (options.versionId || 'ACF').toUpperCase()

    const cleanTerm = searchTerm
      .replace(/["*]/g, '')
      .trim()
      .split(/\s+/)
      .filter((termWord) => termWord.length > 0)
      .map((termWord) => `"${termWord}"*`)
      .join(' AND ')

    if (!cleanTerm) return []

    const querySql = `
      SELECT 
        verse_id as id,
        bible_id as bibleId,
        (book_name || ' ' || chapter || ':' || verse) as reference,
        book_name as bookName,
        chapter,
        verse,
        text,
        snippet(bible_verses_fts, 7, '<mark class="bg-sky-500/30 text-sky-200 rounded px-0.5 font-semibold">', '</mark>', '...', 16) as snippet
      FROM bible_verses_fts
      WHERE bible_verses_fts MATCH ? AND bible_id = ?
      ORDER BY bm25(bible_verses_fts)
      LIMIT ?
    `

    return this.database
      .query<BibleSearchResult, [string, string, number]>(querySql)
      .all(cleanTerm, versionId, limit)
  }

  /**
   * Retorna a contagem total de versículos cadastrados em uma versão específica.
   *
   * @param versionId - ID opcional da versão.
   * @returns Número total de versículos encontrados.
   */
  count(versionId?: string): number {
    if (versionId) {
      const result = this.database
        .query<{ count: number }, [string]>('SELECT count(*) as count FROM bible_verses WHERE bible_id = ?')
        .get(versionId)
      return result?.count || 0
    }
    const result = this.database
      .query<{ count: number }, []>('SELECT count(*) as count FROM bible_verses')
      .get()
    return result?.count || 0
  }

  /**
   * Lista todas as versões bíblicas cadastradas no repositório.
   *
   * @returns Lista de resumos de versão bíblica.
   */
  listVersions(): BibleVersionSummary[] {
    const querySql = `
      SELECT b.id, b.name, b.version as abbrev, b.language, count(v.id) as verseCount
      FROM bibles b
      LEFT JOIN bible_verses v ON b.id = v.bible_id
      GROUP BY b.id
    `
    return this.database.query<BibleVersionSummary, []>(querySql).all()
  }

  /**
   * Verifica se há alguma versão bíblica registrada no banco de dados.
   *
   * @returns Booleano indicando presença de versões bíblicas.
   */
  hasBibles(): boolean {
    const result = this.database
      .query<{ count: number }, []>('SELECT count(*) as count FROM bibles')
      .get()
    return (result?.count || 0) > 0
  }

  /**
   * Remove uma tradução bíblica e seus versículos/índices FTS5 (impede remoção caso seja a única instalada).
   *
   * @param versionId - ID da versão a ser removida (ex: 'NVI').
   * @returns Verdadeiro se foi removida com sucesso.
   */
  removeVersion(versionId: string): boolean {
    const targetId = versionId.trim().toUpperCase()
    const totalVersions = this.database
      .query<{ count: number }, []>('SELECT count(*) as count FROM bibles')
      .get()
    if ((totalVersions?.count || 0) <= 1) {
      return false
    }

    const transaction = this.database.transaction(() => {
      this.database.prepare('DELETE FROM bible_verses_fts WHERE bible_id = ?').run(targetId)
      this.database.prepare('DELETE FROM bible_verses WHERE bible_id = ?').run(targetId)
      this.database.prepare('DELETE FROM bible_books WHERE bible_id = ?').run(targetId)
      this.database.prepare('DELETE FROM bibles WHERE id = ?').run(targetId)
    })

    transaction()
    return true
  }
}
