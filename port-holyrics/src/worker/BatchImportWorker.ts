import { Music } from "../model/Music";
import { MusicDao } from "../dao/MusicDao";
import { normalizeFileInput, type FileInput, type ImportFileInput, type ProgressCallback } from "./MusicImportWorker";

export interface ProfileHandler {
  addMusicsInCheckedProfiles?: (musics: Music[]) => void | Promise<void>;
}

export type ProfileTarget = ProfileHandler | ((musics: Music[]) => void | Promise<void>);

export interface BatchImportOptions {
  files: FileInput[];
  onProgress?: ProgressCallback;
  addToProfiles?: ProfileTarget;
  callback?: (result: BatchImportResult) => void | Promise<void>;
  onDuplicatesFound?: (duplicates: Music[]) => void | Promise<void>;
}

export interface BatchImportResult {
  totalParsed: number;
  imported: Music[];
  alreadyAdded: Music[];
  initialSize: number;
  finalSize: number;
  errors: { file: string; error: string }[];
}

/**
 * Parses content from a .muf (single Music) file.
 */
export function parseMufContent(content: string): Music | null {
  try {
    const trimmed = content.trim();
    if (!trimmed) return null;

    if (trimmed.startsWith("{")) {
      const parsed = JSON.parse(trimmed);
      return Music.create(typeof parsed === "object" ? JSON.stringify(parsed) : trimmed);
    }

    try {
      const m = Music.create(trimmed);
      if (m) return m;
    } catch {
      // not json serialized
    }

    // Fallback: plain text format
    const lines = trimmed.split(/\r?\n/).map((l) => l.trim()).filter((l) => l.length > 0);
    if (lines.length > 0 && lines[0] !== undefined) {
      const m = new Music();
      m.setTitle(lines[0]);
      if (lines.length > 1 && lines[1] !== undefined) {
        m.setArtist(lines[1]);
      }
      m.setLyrics(lines.slice(2).join("\n") || lines.join("\n"));
      return m;
    }

    return null;
  } catch (err) {
    return null;
  }
}

/**
 * Parses content from a .mufl (list of Music) file.
 */
export function parseMuflContent(content: string): Music[] {
  try {
    const trimmed = content.trim();
    if (!trimmed) return [];

    if (trimmed.startsWith("[")) {
      const parsed = JSON.parse(trimmed);
      if (Array.isArray(parsed)) {
        const result: Music[] = [];
        for (const item of parsed) {
          if (item instanceof Music) {
            result.push(item);
          } else {
            const m = Music.create(typeof item === "object" ? JSON.stringify(item) : String(item));
            if (m) result.push(m);
          }
        }
        return result;
      }
    }

    // Fallback: check if single music in mufl or delimiter-separated
    const single = Music.create(trimmed);
    return single ? [single] : [];
  } catch (err) {
    return [];
  }
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.worker.BatchImportWorker
 */
export class BatchImportWorker {
  private readonly files: FileInput[];
  private readonly onProgress?: ProgressCallback;
  private readonly addToProfiles?: ProfileTarget;
  private readonly callback?: (result: BatchImportResult) => void | Promise<void>;
  private readonly onDuplicatesFound?: (duplicates: Music[]) => void | Promise<void>;

  constructor(options: BatchImportOptions) {
    this.files = options.files;
    this.onProgress = options.onProgress;
    this.addToProfiles = options.addToProfiles;
    this.callback = options.callback;
    this.onDuplicatesFound = options.onDuplicatesFound;
  }

  /**
   * Reads files, extracts Music objects, checks duplications and saves new models to MusicDao.
   */
  public async doInBackground(): Promise<BatchImportResult> {
    const initialSize = MusicDao.getAll().length;
    const musics: Music[] = [];
    const errors: { file: string; error: string }[] = [];
    const total = this.files.length;

    for (let i = 0; i < total; i++) {
      const rawFile = this.files[i];
      if (!rawFile) continue;
      const percent = Math.round(((i + 1) / total) * 60);

      try {
        const file: ImportFileInput = await normalizeFileInput(rawFile);
        const name = (file.name || "").toLowerCase();

        if (this.onProgress) {
          this.onProgress(percent, `Reading ${file.name}...`);
        }

        if (!file.content) {
          errors.push({ file: file.name, error: "File content could not be read or is empty." });
          continue;
        }

        const rawContent =
          typeof file.content === "string"
            ? file.content
            : new TextDecoder().decode(file.content);

        if (name.endsWith(".muf")) {
          const m = parseMufContent(rawContent);
          if (m) {
            if (!m.getTitle()) {
              m.setTitle(file.name.replace(/\.[^/.]+$/, ""));
            }
            musics.push(m);
          } else {
            errors.push({ file: file.name, error: "Failed to parse .muf file" });
          }
        } else if (name.endsWith(".mufl")) {
          const list = parseMuflContent(rawContent);
          if (list.length > 0) {
            musics.push(...list);
          } else {
            errors.push({ file: file.name, error: "Failed to parse .mufl file" });
          }
        } else {
          // If neither .muf nor .mufl extension, attempt both .muf and .mufl parser
          const list = parseMuflContent(rawContent);
          if (list.length > 0) {
            musics.push(...list);
          } else {
            const m = parseMufContent(rawContent);
            if (m) {
              if (!m.getTitle()) {
                m.setTitle(file.name.replace(/\.[^/.]+$/, ""));
              }
              musics.push(m);
            } else {
              errors.push({ file: file.name, error: `Unsupported file format: ${file.name}` });
            }
          }
        }
      } catch (err: any) {
        const fname = typeof rawFile === "string" ? rawFile : (rawFile as any).name || "unknown";
        errors.push({ file: fname, error: err?.message || String(err) });
      }
    }

    if (this.onProgress) {
      this.onProgress(70, `Checking duplicates for ${musics.length} songs...`);
    }

    // Assign temporary negative IDs matching original Java behavior
    for (let i = 0; i < musics.length; i++) {
      const m = musics[i];
      if (m) {
        m.setId(-i);
      }
    }

    // Filter duplicates using MusicDao.contains
    const alreadyAdded: Music[] = [];
    const toImport: Music[] = [];

    for (let i = 0; i < musics.length; i++) {
      const m = musics[i];
      if (!m) continue;
      if (MusicDao.contains(m)) {
        alreadyAdded.push(m);
      } else {
        toImport.push(m);
      }
    }

    if (this.onProgress) {
      this.onProgress(85, `Saving ${toImport.length} new songs...`);
    }

    // Batch insert new records into MusicDao
    if (toImport.length > 0) {
      MusicDao.addBatch(toImport);
    }

    // Associate with profiles if specified
    if (this.addToProfiles && toImport.length > 0) {
      if (typeof this.addToProfiles === "function") {
        await this.addToProfiles(toImport);
      } else if (typeof this.addToProfiles.addMusicsInCheckedProfiles === "function") {
        await this.addToProfiles.addMusicsInCheckedProfiles(toImport);
      }
    }

    if (this.onProgress) {
      this.onProgress(100, `Completed! Imported ${toImport.length} songs.`);
    }

    const finalSize = MusicDao.getAll().length;

    return {
      totalParsed: musics.length,
      imported: toImport,
      alreadyAdded,
      initialSize,
      finalSize,
      errors,
    };
  }

  /**
   * Executes the batch import worker and triggers lifecycle callbacks.
   */
  public async execute(): Promise<BatchImportResult> {
    const result = await this.doInBackground();

    if (result.alreadyAdded.length > 0 && this.onDuplicatesFound) {
      await this.onDuplicatesFound(result.alreadyAdded);
    }

    if (this.callback) {
      await this.callback(result);
    }

    return result;
  }

  /**
   * Helper static runner.
   */
  public static async run(options: BatchImportOptions): Promise<BatchImportResult> {
    const worker = new BatchImportWorker(options);
    return worker.execute();
  }
}

export default BatchImportWorker;
