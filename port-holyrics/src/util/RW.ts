import * as fs from 'node:fs';
import * as path from 'node:path';

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.util.RW
 * Read/Write helpers for files and buffers
 */
export class RW {
  /**
   * Reads a file synchronously into a string with specified encoding.
   * Strips UTF-8 BOM (\ufeff) if present, matching Java RW.read behavior.
   */
  public static read(file: string, charset: BufferEncoding = 'utf-8'): string {
    const content = fs.readFileSync(file, { encoding: charset });
    return content.replace(/^\ufeff/, '').replace(/\ufeff/g, '');
  }

  /**
   * Reads a file asynchronously into a string with specified encoding.
   */
  public static async readAsync(file: string, charset: BufferEncoding = 'utf-8'): Promise<string> {
    const content = await fs.promises.readFile(file, { encoding: charset });
    return content.replace(/^\ufeff/, '').replace(/\ufeff/g, '');
  }

  /**
   * Writes string content to a file synchronously.
   */
  public static write(file: string, value: string, charset: BufferEncoding = 'utf-8'): void {
    const dir = path.dirname(file);
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }
    // Match Java newline handling
    const normalized = value.replace(/\r\n/g, '\n');
    fs.writeFileSync(file, normalized, { encoding: charset });
  }

  /**
   * Writes string content to a file asynchronously.
   */
  public static async writeAsync(file: string, value: string, charset: BufferEncoding = 'utf-8'): Promise<void> {
    const dir = path.dirname(file);
    if (!fs.existsSync(dir)) {
      await fs.promises.mkdir(dir, { recursive: true });
    }
    const normalized = value.replace(/\r\n/g, '\n');
    await fs.promises.writeFile(file, normalized, { encoding: charset });
  }

  /**
   * Reads a file into a Buffer synchronously.
   */
  public static readBuffer(file: string): Buffer {
    return fs.readFileSync(file);
  }

  /**
   * Reads a file into a Buffer asynchronously.
   */
  public static async readBufferAsync(file: string): Promise<Buffer> {
    return fs.promises.readFile(file);
  }

  /**
   * Writes a Buffer or Uint8Array to a file synchronously.
   */
  public static writeBuffer(file: string, data: Buffer | Uint8Array): void {
    const dir = path.dirname(file);
    if (!fs.existsSync(dir)) {
      fs.mkdirSync(dir, { recursive: true });
    }
    fs.writeFileSync(file, data);
  }

  /**
   * Writes a Buffer or Uint8Array to a file asynchronously.
   */
  public static async writeBufferAsync(file: string, data: Buffer | Uint8Array): Promise<void> {
    const dir = path.dirname(file);
    if (!fs.existsSync(dir)) {
      await fs.promises.mkdir(dir, { recursive: true });
    }
    await fs.promises.writeFile(file, data);
  }

  /**
   * Reads bytes from a resource or file path (equivalent to Java readJar / resource stream).
   */
  public static readJar(resourcePath: string): Buffer {
    return fs.readFileSync(resourcePath);
  }
}

export default RW;
