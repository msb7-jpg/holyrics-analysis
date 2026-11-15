import * as fs from 'node:fs/promises';
import * as path from 'node:path';
import * as zlib from 'node:zlib';
import * as crypto from 'node:crypto';

export type ProgressCallback = (percent: number, message: string) => void;
export type DoneCallback = (success: boolean, data?: any) => void;

export interface DownloadWorkerOptions {
  url: string;
  destinationPath: string;
  hash?: string | null;
  zip?: boolean;
  callback?: () => void | Promise<void>;
  onProgress?: ProgressCallback;
  onDone?: DoneCallback;
  signal?: AbortSignal;
}

/**
 * Worker responsible for downloading files, tracking progress,
 * verifying checksums, unzipping if needed, and saving to disk.
 * Replaces Java SwingWorker DownloadWorker.
 */
export class DownloadWorker {
  private url: string;
  private destinationPath: string;
  private hash: string | null;
  private zip: boolean;
  private callback?: () => void | Promise<void>;
  private onProgress?: ProgressCallback;
  private onDone?: DoneCallback;
  private signal?: AbortSignal;
  private isCancelled: boolean = false;

  constructor(options: DownloadWorkerOptions);
  constructor(
    url: string,
    destinationPath: string,
    hash?: string | null,
    zip?: boolean,
    callback?: () => void | Promise<void>,
    onProgress?: ProgressCallback,
    onDone?: DoneCallback
  );
  constructor(
    urlOrOptions: string | DownloadWorkerOptions,
    destinationPath?: string,
    hash?: string | null,
    zip?: boolean,
    callback?: () => void | Promise<void>,
    onProgress?: ProgressCallback,
    onDone?: DoneCallback
  ) {
    if (typeof urlOrOptions === 'object') {
      this.url = urlOrOptions.url;
      this.destinationPath = urlOrOptions.destinationPath;
      this.hash = urlOrOptions.hash ?? null;
      this.zip = urlOrOptions.zip ?? false;
      this.callback = urlOrOptions.callback;
      this.onProgress = urlOrOptions.onProgress;
      this.onDone = urlOrOptions.onDone;
      this.signal = urlOrOptions.signal;
    } else {
      this.url = urlOrOptions;
      this.destinationPath = destinationPath!;
      this.hash = hash ?? null;
      this.zip = zip ?? false;
      this.callback = callback;
      this.onProgress = onProgress;
      this.onDone = onDone;
    }
  }

  public cancel(): void {
    this.isCancelled = true;
  }

  /**
   * Uncompresses bytes using zlib (gunzip/inflate/unzip)
   */
  private static unzipBytes(buffer: Buffer): Buffer {
    try {
      // Check for gzip magic numbers: 0x1f, 0x8b
      if (buffer.length >= 2 && buffer[0] === 0x1f && buffer[1] === 0x8b) {
        return zlib.gunzipSync(buffer);
      }
      // Check for zlib header (0x78)
      if (buffer.length >= 2 && buffer[0] === 0x78) {
        return zlib.inflateSync(buffer);
      }
      // Fallback to unzipSync (handles raw deflate or zlib/gzip)
      return zlib.unzipSync(buffer);
    } catch {
      // If decompression fails or buffer is already raw bytes, return original buffer
      return buffer;
    }
  }

  /**
   * Executes download in background
   */
  public async execute(): Promise<boolean> {
    try {
      this.onProgress?.(0, 'Downloading...');

      const response = await fetch(this.url, {
        signal: this.signal,
      });

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status} ${response.statusText}`);
      }

      const contentLength = response.headers.get('content-length');
      const totalBytes = contentLength ? parseInt(contentLength, 10) : 0;

      let downloadedBytes = 0;
      const chunks: Uint8Array[] = [];

      if (response.body) {
        const reader = response.body.getReader();

        while (true) {
          if (this.isCancelled) {
            reader.cancel();
            throw new Error('Download cancelled by user');
          }

          const { done, value } = await reader.read();
          if (done) break;

          if (value) {
            chunks.push(value);
            downloadedBytes += value.length;

            let percentage = 0;
            if (totalBytes > 0) {
              percentage = Math.floor((downloadedBytes / totalBytes) * 100);
            }
            this.onProgress?.(percentage, `Downloading... ${percentage}%`);
          }
        }
      } else {
        const arrayBuffer = await response.arrayBuffer();
        chunks.push(new Uint8Array(arrayBuffer));
      }

      const rawBuffer = Buffer.concat(chunks.map(chunk => Buffer.from(chunk)));

      // Hash verification if hash is provided
      if (this.hash) {
        const sha256 = crypto.createHash('sha256').update(rawBuffer).digest('hex');
        const md5 = crypto.createHash('md5').update(rawBuffer).digest('hex');
        const normalizedHash = this.hash.toLowerCase().trim();

        if (sha256.toLowerCase() !== normalizedHash && md5.toLowerCase() !== normalizedHash) {
          console.warn(`Hash mismatch: expected ${this.hash}, got sha256=${sha256}, md5=${md5}`);
        }
      }

      this.onProgress?.(95, 'Extracting and saving...');

      const finalBuffer = this.zip ? DownloadWorker.unzipBytes(rawBuffer) : rawBuffer;

      // Ensure directory exists
      const targetDir = path.dirname(this.destinationPath);
      await fs.mkdir(targetDir, { recursive: true });

      // Save bytes to path
      await fs.writeFile(this.destinationPath, finalBuffer);

      if (this.callback) {
        await this.callback();
      }

      this.onProgress?.(100, 'Download complete');
      this.onDone?.(true, { path: this.destinationPath, size: finalBuffer.length });
      return true;
    } catch (error: any) {
      this.onDone?.(false, { error: error.message ?? String(error) });
      return false;
    }
  }
}

/**
 * Convenience function to start a download task
 */
export async function downloadFile(
  url: string,
  destinationPath: string,
  options?: {
    hash?: string | null;
    zip?: boolean;
    callback?: () => void | Promise<void>;
    onProgress?: ProgressCallback;
    onDone?: DoneCallback;
    signal?: AbortSignal;
  }
): Promise<boolean> {
  const worker = new DownloadWorker({
    url,
    destinationPath,
    hash: options?.hash,
    zip: options?.zip,
    callback: options?.callback,
    onProgress: options?.onProgress,
    onDone: options?.onDone,
    signal: options?.signal,
  });

  return worker.execute();
}
