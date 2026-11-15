import { Music } from "../model/Music";
import * as fs from "node:fs/promises";
import * as path from "node:path";

export interface ImportFileInput {
  name: string;
  path?: string;
  content?: string | Uint8Array;
}

export type FileInput = string | ImportFileInput | File;

export interface ImportMusicResponse {
  musics: Music[];
  error: (string | FileInput)[];
}

export interface ImportMusicParser {
  name?: string;
  filter?: {
    description: string;
    extensions: string[];
    accept?: (filename: string) => boolean;
  };
  importMusics: (
    files: ImportFileInput[],
    onProgress?: (progress: number, message: string) => void
  ) => Promise<ImportMusicResponse>;
}

export type ProgressCallback = (progress: number, message: string) => void;

export interface MusicImportOptions {
  type: ImportMusicParser;
  files: FileInput[];
  onProgress?: ProgressCallback;
  callback?: (response: ImportMusicResponse) => void | Promise<void>;
  onError?: (errors: (string | FileInput)[]) => void;
  onSuccess?: (musics: Music[]) => void;
}

/**
 * Normalizes various FileInput types into a standard ImportFileInput with content loaded if possible.
 */
export async function normalizeFileInput(input: FileInput): Promise<ImportFileInput> {
  if (typeof input === "string") {
    const filename = path.basename(input);
    try {
      const content = await fs.readFile(input, "utf-8");
      return { name: filename, path: input, content };
    } catch {
      return { name: filename, path: input };
    }
  }

  // Browser / Web File object
  if (typeof File !== "undefined" && input instanceof File) {
    try {
      const content = await input.text();
      return { name: input.name, content };
    } catch {
      return { name: input.name };
    }
  }

  // ImportFileInput object
  const item = input as ImportFileInput;
  if (!item.content && item.path) {
    try {
      const content = await fs.readFile(item.path, "utf-8");
      return { ...item, content };
    } catch {
      return item;
    }
  }

  return item;
}

/**
 * Standard default parser for Holyrics text or json music files.
 */
export const DefaultMusicParser: ImportMusicParser = {
  name: "Default Text/JSON Parser",
  filter: {
    description: "Supported Files (*.txt, *.json, *.muf)",
    extensions: [".txt", ".json", ".muf"],
    accept: (filename: string) => /\.(txt|json|muf)$/i.test(filename),
  },
  async importMusics(files: ImportFileInput[], onProgress?: ProgressCallback): Promise<ImportMusicResponse> {
    const musics: Music[] = [];
    const error: (string | FileInput)[] = [];

    const total = files.length;
    for (let i = 0; i < total; i++) {
      const file = files[i];
      if (!file) {
        continue;
      }
      if (onProgress) {
        onProgress(Math.round(((i + 1) / total) * 100), `Parsing ${file.name}...`);
      }

      try {
        if (!file.content) {
          throw new Error("Empty file content");
        }

        const raw = typeof file.content === "string" ? file.content : new TextDecoder().decode(file.content);
        let music: Music | null = null;

        // Try parsing as JSON first
        try {
          const parsed = JSON.parse(raw);
          if (Array.isArray(parsed)) {
            for (const item of parsed) {
              const m = item instanceof Music ? item : Music.create(typeof item === "string" ? item : JSON.stringify(item));
              if (m) musics.push(m);
            }
            continue;
          } else {
            music = Music.create(typeof parsed === "object" ? JSON.stringify(parsed) : raw);
          }
        } catch {
          try {
            music = Music.create(raw);
          } catch {
            music = null;
          }

          if (!music) {
            const lines = raw.split(/\r?\n/).map((l) => l.trim()).filter((l) => l.length > 0);
            if (lines.length > 0 && lines[0] !== undefined) {
              music = new Music();
              music.setTitle(lines[0]);
              if (lines.length > 1 && lines[1] !== undefined) {
                music.setArtist(lines[1]);
              }
              music.setLyrics(lines.slice(2).join("\n") || lines.join("\n"));
            }
          }
        }

        if (music) {
          if (!music.getTitle()) {
            const defaultTitle = file.name.replace(/\.[^/.]+$/, "");
            music.setTitle(defaultTitle);
          }
          musics.push(music);
        } else {
          error.push(file);
        }
      } catch {
        error.push(file);
      }
    }

    return { musics, error };
  },
};

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.worker.MusicImportWorker
 */
export class MusicImportWorker {
  private readonly type: ImportMusicParser;
  private readonly files: FileInput[];
  private readonly onProgress?: ProgressCallback;
  private readonly callback?: (response: ImportMusicResponse) => void | Promise<void>;
  private readonly onError?: (errors: (string | FileInput)[]) => void;
  private readonly onSuccess?: (musics: Music[]) => void;

  constructor(options: MusicImportOptions) {
    this.type = options.type;
    this.files = options.files;
    this.onProgress = options.onProgress;
    this.callback = options.callback;
    this.onError = options.onError;
    this.onSuccess = options.onSuccess;
  }

  /**
   * Performs the background parsing and file import process.
   */
  public async doInBackground(): Promise<ImportMusicResponse> {
    const normalizedFiles: ImportFileInput[] = [];
    const total = this.files.length;

    for (let i = 0; i < total; i++) {
      const rawFile = this.files[i];
      if (!rawFile) {
        continue;
      }
      if (this.onProgress) {
        this.onProgress(Math.round(((i + 1) / total) * 50), `Loading ${i + 1}/${total} files...`);
      }
      const norm = await normalizeFileInput(rawFile);
      normalizedFiles.push(norm);
    }

    // Run the parser with the remaining 50% - 100% progress
    const response = await this.type.importMusics(normalizedFiles, (p, msg) => {
      if (this.onProgress) {
        this.onProgress(50 + Math.round(p * 0.5), msg);
      }
    });

    return response;
  }

  /**
   * Executes the worker, mimicking the SwingWorker lifecycle (doInBackground -> done).
   */
  public async execute(): Promise<ImportMusicResponse> {
    try {
      const response = await this.doInBackground();

      if (response.error.length > 0 && this.onError) {
        this.onError(response.error);
      }

      if (response.musics.length > 0 && this.onSuccess) {
        this.onSuccess(response.musics);
      }

      if (this.callback) {
        await this.callback(response);
      }

      return response;
    } catch (err) {
      const errorResponse: ImportMusicResponse = {
        musics: [],
        error: this.files,
      };
      if (this.onError) {
        this.onError(this.files);
      }
      return errorResponse;
    }
  }

  /**
   * Helper static runner.
   */
  public static async run(options: MusicImportOptions): Promise<ImportMusicResponse> {
    const worker = new MusicImportWorker(options);
    return worker.execute();
  }
}

export default MusicImportWorker;
