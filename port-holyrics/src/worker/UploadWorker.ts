import * as crypto from 'node:crypto';
import type { Account } from '../model/Account.js';

export type ProgressCallback = (percent: number, message: string) => void;
export type DoneCallback = (success: boolean, data?: any) => void;

export type UploadAccount = Account | {
  token?: string;
  email?: string;
  username?: string;
  accessKey?: string;
  security?: string;
};

export interface UploadWorkerOptions {
  account?: UploadAccount | null;
  uploadUrl?: string;
  payload?: any;
  onProgress?: ProgressCallback;
  onDone?: DoneCallback;
  signal?: AbortSignal;
}

export interface UploadResult {
  success: boolean;
  message?: string;
  response?: any;
  hash?: string;
  error?: string;
}

/**
 * Worker responsible for preparing shared files JSON stream,
 * computing SHA checksums, uploading to Holyrics server/PHP endpoint,
 * and notifying progress.
 * Replaces Java SwingWorker UploadWorker.
 */
export class UploadWorker {
  private account?: UploadAccount | null;
  private uploadUrl: string;
  private payload?: any;
  private onProgress?: ProgressCallback;
  private onDone?: DoneCallback;
  private signal?: AbortSignal;
  private isCancelled: boolean = false;

  constructor(options?: UploadWorkerOptions);
  constructor(
    account?: UploadAccount | null,
    payload?: any,
    onProgress?: ProgressCallback,
    onDone?: DoneCallback
  );
  constructor(
    accountOrOptions?: UploadAccount | null | UploadWorkerOptions,
    payload?: any,
    onProgress?: ProgressCallback,
    onDone?: DoneCallback
  ) {
    if (accountOrOptions && typeof accountOrOptions === 'object' && ('uploadUrl' in accountOrOptions || 'payload' in accountOrOptions || 'onProgress' in accountOrOptions)) {
      const opts = accountOrOptions as UploadWorkerOptions;
      this.account = opts.account;
      this.uploadUrl = opts.uploadUrl ?? 'https://holyrics.com.br/share/upload.php';
      this.payload = opts.payload;
      this.onProgress = opts.onProgress;
      this.onDone = opts.onDone;
      this.signal = opts.signal;
    } else {
      this.account = accountOrOptions as (Account | null | undefined);
      this.uploadUrl = 'https://holyrics.com.br/share/upload.php';
      this.payload = payload;
      this.onProgress = onProgress;
      this.onDone = onDone;
    }
  }

  public cancel(): void {
    this.isCancelled = true;
  }

  /**
   * Generates a hash for the JSON string
   */
  public static calculateHash(jsonContent: string): string {
    return crypto.createHash('sha256').update(jsonContent, 'utf-8').digest('hex');
  }

  /**
   * Executes the upload task in background
   */
  public async execute(): Promise<UploadResult> {
    try {
      this.onProgress?.(10, 'Preparing upload...');

      // Prepare payload JSON
      const jsonString = typeof this.payload === 'string'
        ? this.payload
        : JSON.stringify(this.payload ?? {});

      const hash = UploadWorker.calculateHash(jsonString);

      if (this.isCancelled) {
        throw new Error('Upload cancelled by user');
      }

      this.onProgress?.(30, 'Uploading...');

      // Prepare form data or JSON body matching SharePHP.upload
      const formData = new FormData();
      if (this.account) {
        if ('token' in this.account && this.account.token) {
          formData.append('token', this.account.token);
        }
        if ('email' in this.account && this.account.email) {
          formData.append('email', this.account.email);
        }
      }
      formData.append('hash', hash);
      formData.append('data', jsonString);

      this.onProgress?.(50, 'Uploading data to server...');

      const response = await fetch(this.uploadUrl, {
        method: 'POST',
        body: formData,
        signal: this.signal,
      });

      if (this.isCancelled) {
        throw new Error('Upload cancelled by user');
      }

      this.onProgress?.(85, 'Finalizing upload...');

      if (!response.ok) {
        throw new Error(`Upload failed with status: ${response.status} ${response.statusText}`);
      }

      const responseText = await response.text();
      let responseData: any = responseText;
      try {
        responseData = JSON.parse(responseText);
      } catch {
        // Response is text
      }

      // Holyrics returns 't' prefix for true/success, 'f' for failure in legacy PHP
      const isSuccess = responseText.startsWith('t') || response.status === 200;
      const message = responseText.startsWith('t') || responseText.startsWith('f')
        ? responseText.substring(1)
        : responseText;

      const result: UploadResult = {
        success: isSuccess,
        message,
        response: responseData,
        hash,
      };

      this.onProgress?.(100, isSuccess ? 'Upload successful' : 'Upload failed');
      this.onDone?.(isSuccess, result);
      return result;
    } catch (error: any) {
      const result: UploadResult = {
        success: false,
        error: error.message ?? String(error),
      };
      this.onDone?.(false, result);
      return result;
    }
  }
}

/**
 * Convenience helper to perform an upload
 */
export async function uploadWorker(
  account: Account | null | undefined,
  payload: any,
  options?: {
    uploadUrl?: string;
    onProgress?: ProgressCallback;
    onDone?: DoneCallback;
    signal?: AbortSignal;
  }
): Promise<UploadResult> {
  const worker = new UploadWorker({
    account,
    payload,
    uploadUrl: options?.uploadUrl,
    onProgress: options?.onProgress,
    onDone: options?.onDone,
    signal: options?.signal,
  });

  return worker.execute();
}
