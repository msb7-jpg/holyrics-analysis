import * as fs from 'node:fs/promises';
import * as path from 'node:path';

export type ProgressCallback = (percent: number, message: string) => void;
export type DoneCallback = (success: boolean, data?: any) => void;

export interface ShareAppData {
  musics?: any[];
  presentations?: any[];
  schedules?: any[];
  history?: any[];
  members?: any[];
  themes?: any[];
  alerts?: any[];
  files?: Record<string, string>;
  settings?: Record<string, any>;
  [key: string]: any;
}

export interface UpdateFromJsonOptions {
  json: string | ShareAppData;
  targetDir?: string;
  onProgress?: ProgressCallback;
  onDone?: DoneCallback;
  dbHandler?: (data: ShareAppData) => Promise<void>;
  signal?: AbortSignal;
}

export interface UpdateResult {
  success: boolean;
  importedItems?: {
    musics?: number;
    presentations?: number;
    schedules?: number;
    members?: number;
    themes?: number;
    alerts?: number;
    files?: number;
  };
  error?: string;
}

/**
 * Worker responsible for parsing JSON streams or payload (ShareFilesApp export),
 * loading data, inserting into local databases/files, and notifying progress.
 * Replaces Java SwingWorker UpdateFromJsonWorker.
 */
export class UpdateFromJsonWorker {
  private json: string | ShareAppData;
  private targetDir: string;
  private onProgress?: ProgressCallback;
  private onDone?: DoneCallback;
  private dbHandler?: (data: ShareAppData) => Promise<void>;
  private signal?: AbortSignal;
  private isCancelled: boolean = false;

  constructor(options: UpdateFromJsonOptions);
  constructor(
    json: string | ShareAppData,
    onProgress?: ProgressCallback,
    onDone?: DoneCallback,
    dbHandler?: (data: ShareAppData) => Promise<void>
  );
  constructor(
    jsonOrOptions: string | ShareAppData | UpdateFromJsonOptions,
    onProgress?: ProgressCallback,
    onDone?: DoneCallback,
    dbHandler?: (data: ShareAppData) => Promise<void>
  ) {
    if (typeof jsonOrOptions === 'object' && ('json' in jsonOrOptions || 'onProgress' in jsonOrOptions)) {
      const opts = jsonOrOptions as UpdateFromJsonOptions;
      this.json = opts.json;
      this.targetDir = opts.targetDir ?? path.join(process.cwd(), 'data');
      this.onProgress = opts.onProgress;
      this.onDone = opts.onDone;
      this.dbHandler = opts.dbHandler;
      this.signal = opts.signal;
    } else {
      this.json = jsonOrOptions as string | ShareAppData;
      this.targetDir = path.join(process.cwd(), 'data');
      this.onProgress = onProgress;
      this.onDone = onDone;
      this.dbHandler = dbHandler;
    }
  }

  public cancel(): void {
    this.isCancelled = true;
  }

  /**
   * Executes the update from JSON background task
   */
  public async execute(): Promise<UpdateResult> {
    try {
      this.onProgress?.(5, 'Parsing JSON stream...');

      let parsed: ShareAppData;
      if (typeof this.json === 'string') {
        parsed = JSON.parse(this.json);
      } else {
        parsed = this.json;
      }

      if (this.isCancelled) {
        throw new Error('Update cancelled by user');
      }

      this.onProgress?.(20, 'Analyzing data elements...');

      const counts: UpdateResult['importedItems'] = {
        musics: parsed.musics?.length ?? 0,
        presentations: parsed.presentations?.length ?? 0,
        schedules: parsed.schedules?.length ?? 0,
        members: parsed.members?.length ?? 0,
        themes: parsed.themes?.length ?? 0,
        alerts: parsed.alerts?.length ?? 0,
        files: parsed.files ? Object.keys(parsed.files).length : 0,
      };

      // Custom database handler if provided
      if (this.dbHandler) {
        this.onProgress?.(40, 'Updating local database...');
        await this.dbHandler(parsed);
      }

      // If files map is provided (key: relative path, value: base64/content)
      if (parsed.files && Object.keys(parsed.files).length > 0) {
        const fileKeys = Object.keys(parsed.files);
        const total = fileKeys.length;

        for (let i = 0; i < total; i++) {
          if (this.isCancelled) {
            throw new Error('Update cancelled by user');
          }

          const relativeFilePath = fileKeys[i];
          if (!relativeFilePath) {
            continue;
          }
          const fileContent = parsed.files[relativeFilePath];
          if (fileContent === undefined) continue;
          const fullPath = path.join(this.targetDir, relativeFilePath);

          await fs.mkdir(path.dirname(fullPath), { recursive: true });

          // Support base64 or plain string
          if (typeof fileContent === 'string' && fileContent.startsWith('base64:')) {
            const buffer = Buffer.from(fileContent.substring(7), 'base64');
            await fs.writeFile(fullPath, buffer);
          } else {
            await fs.writeFile(fullPath, fileContent, 'utf-8');
          }

          const percent = 40 + Math.floor(((i + 1) / total) * 50);
          this.onProgress?.(percent, `Writing file ${i + 1}/${total}: ${relativeFilePath}`);
        }
      }

      this.onProgress?.(95, 'Finalizing data synchronization...');

      const result: UpdateResult = {
        success: true,
        importedItems: counts,
      };

      this.onProgress?.(100, 'Update completed successfully');
      this.onDone?.(true, result);
      return result;
    } catch (error: any) {
      const result: UpdateResult = {
        success: false,
        error: error.message ?? String(error),
      };
      this.onDone?.(false, result);
      return result;
    }
  }
}

/**
 * Convenience helper to execute UpdateFromJson
 */
export async function updateFromJsonWorker(
  json: string | ShareAppData,
  options?: {
    targetDir?: string;
    onProgress?: ProgressCallback;
    onDone?: DoneCallback;
    dbHandler?: (data: ShareAppData) => Promise<void>;
    signal?: AbortSignal;
  }
): Promise<UpdateResult> {
  const worker = new UpdateFromJsonWorker({
    json,
    targetDir: options?.targetDir,
    onProgress: options?.onProgress,
    onDone: options?.onDone,
    dbHandler: options?.dbHandler,
    signal: options?.signal,
  });

  return worker.execute();
}
