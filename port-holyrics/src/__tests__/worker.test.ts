import { describe, it, expect } from 'bun:test';
import * as fs from 'node:fs/promises';
import * as path from 'node:path';
import { DownloadWorker, downloadFile } from '../worker/DownloadWorker.js';
import { UploadWorker, uploadWorker } from '../worker/UploadWorker.js';
import { UpdateFromJsonWorker, updateFromJsonWorker } from '../worker/UpdateFromJsonWorker.js';

describe('Worker Services (DownloadWorker, UploadWorker, UpdateFromJsonWorker)', () => {
  it('DownloadWorker should instantiate and report progress / completion', async () => {
    const testFile = path.join(process.cwd(), 'temp-test-download.txt');
    const progressUpdates: { percent: number; message: string }[] = [];
    let doneCalled = false;

    // Test with data URL or mock fetch
    const worker = new DownloadWorker({
      url: 'data:text/plain;charset=utf-8,Hello%20World',
      destinationPath: testFile,
      zip: false,
      onProgress: (percent, msg) => {
        progressUpdates.push({ percent, message: msg });
      },
      onDone: (success, data) => {
        doneCalled = true;
        expect(success).toBe(true);
      },
    });

    const res = await worker.execute();
    expect(res).toBe(true);
    expect(doneCalled).toBe(true);
    expect(progressUpdates.length).toBeGreaterThan(0);

    const content = await fs.readFile(testFile, 'utf-8');
    expect(content).toBe('Hello World');

    // Clean up
    await fs.unlink(testFile).catch(() => {});
  });

  it('UploadWorker should instantiate and run with progress/done callbacks', async () => {
    const progressUpdates: { percent: number; message: string }[] = [];
    let doneSuccess = false;

    // Mock fetch during upload test
    const origFetch = globalThis.fetch;
    globalThis.fetch = (async (input: any, init?: any) => {
      return new Response('tUpload complete', { status: 200 });
    }) as any;

    try {
      const worker = new UploadWorker({
        account: { email: 'test@example.com', token: 'token123' } as any,
        payload: { songs: [{ title: 'Amazing Grace' }] },
        onProgress: (percent, msg) => {
          progressUpdates.push({ percent, message: msg });
        },
        onDone: (success, data) => {
          doneSuccess = success;
          expect(data?.message).toContain('Upload complete');
        },
      });

      const result = await worker.execute();
      expect(result.success).toBe(true);
      expect(doneSuccess).toBe(true);
      expect(progressUpdates.length).toBeGreaterThan(0);
    } finally {
      globalThis.fetch = origFetch;
    }
  });

  it('UpdateFromJsonWorker should parse JSON and write files with progress tracking', async () => {
    const testTargetDir = path.join(process.cwd(), 'temp-update-json-test');
    const progressUpdates: { percent: number; message: string }[] = [];
    let doneCalled = false;

    const payload = {
      musics: [{ id: 1, name: 'Holy Holy Holy' }],
      files: {
        'test-file.txt': 'sample file content',
        'sub/test-file-2.txt': 'base64:' + Buffer.from('binary content').toString('base64'),
      },
    };

    const worker = new UpdateFromJsonWorker({
      json: JSON.stringify(payload),
      targetDir: testTargetDir,
      onProgress: (percent, msg) => {
        progressUpdates.push({ percent, message: msg });
      },
      onDone: (success, data) => {
        doneCalled = true;
        expect(success).toBe(true);
        expect(data?.importedItems?.musics).toBe(1);
        expect(data?.importedItems?.files).toBe(2);
      },
    });

    const result = await worker.execute();
    expect(result.success).toBe(true);
    expect(doneCalled).toBe(true);
    expect(progressUpdates.length).toBeGreaterThan(0);

    const f1 = await fs.readFile(path.join(testTargetDir, 'test-file.txt'), 'utf-8');
    expect(f1).toBe('sample file content');

    const f2 = await fs.readFile(path.join(testTargetDir, 'sub/test-file-2.txt'), 'utf-8');
    expect(f2).toBe('binary content');

    // Clean up
    await fs.rm(testTargetDir, { recursive: true, force: true }).catch(() => {});
  });
});
