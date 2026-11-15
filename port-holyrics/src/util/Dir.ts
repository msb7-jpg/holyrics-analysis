import * as fs from 'node:fs';
import * as path from 'node:path';
import * as os from 'node:os';
import * as crypto from 'node:crypto';

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.util.Dir
 * System directory helpers, file manipulation, hashing, and encryption
 */
export class Dir {
  private static readonly isPt = (process.env.LANG || Intl.DateTimeFormat().resolvedOptions().locale || '').toLowerCase().startsWith('pt');

  public static readonly WORD_DIRECTORY: string = Dir.isPt ? 'Diretório' : 'Directory';
  public static readonly MSG_OVERRIDE: string = Dir.isPt ? 'Arquivo já existe.\nDeseja substituí-lo?' : 'File already exists.\nDo you want to replace it?';
  public static readonly MSG_SAVE_FILE: string = Dir.isPt ? 'Salvar Arquivo' : 'Save File';
  public static readonly MSG_ERROR_INVALID_NAME: string = Dir.isPt ? 'O nome do arquivo não pode conter nenhum dos seguintes caracteres:\n {0}' : 'The file name can not contain any of the following characters:\n {0}';
  public static readonly MSG_FILE_NOT_FOUND_IN_DIRECTORY: string = Dir.isPt ? 'Arquivo não encontrado na pasta' : 'File not found in directory';
  public static readonly MSG_FILE_NOT_FOUND: string = Dir.isPt ? 'Arquivo não encontrado' : 'File not found';
  public static readonly MSG_REMOTE_NAME: string = Dir.isPt ? 'Nome remoto' : 'Remote name';

  private static readonly IV = Buffer.from('AAAAAAAAAAAAAAAA', 'utf-8');

  /**
   * Returns user home directory
   */
  public static getUserHome(): string {
    return os.homedir();
  }

  /**
   * Returns OS-specific application data directory
   */
  public static getAppDataDirectory(): string {
    const platform = os.platform();
    if (platform === 'win32') {
      return process.env.APPDATA || path.join(os.homedir(), 'AppData', 'Roaming');
    }
    if (platform === 'darwin') {
      return path.join(os.homedir(), 'Library', 'Application Support');
    }
    return process.env.XDG_CONFIG_HOME || path.join(os.homedir(), '.config');
  }

  /**
   * Returns default Holyrics root directory
   */
  public static getHolyricsDirectory(): string {
    // If holyrics directory exists in working directory or environment
    if (process.env.HOLYRICS_PATH) {
      return path.resolve(process.env.HOLYRICS_PATH);
    }
    const localDir = path.resolve('files');
    if (fs.existsSync(localDir)) {
      return localDir;
    }
    return path.join(Dir.getAppDataDirectory(), 'holyrics');
  }

  /**
   * Returns root directory of the application
   */
  public static getRootDirectory(): string {
    return path.resolve('.');
  }

  /**
   * Resolves absolute directory path
   */
  public static getAbsoluteDir(file: string = ''): string {
    return path.resolve(file);
  }

  /**
   * Recursively deletes a directory or file.
   */
  public static deleteDir(dirPath: string): boolean {
    try {
      if (!fs.existsSync(dirPath)) {
        return true;
      }
      const stat = fs.lstatSync(dirPath);
      if (stat.isDirectory()) {
        const files = fs.readdirSync(dirPath);
        for (const file of files) {
          const curPath = path.join(dirPath, file);
          const success = Dir.deleteDir(curPath);
          if (!success) {
            return false;
          }
        }
        fs.rmdirSync(dirPath);
      } else {
        fs.unlinkSync(dirPath);
      }
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Checks if a file or any file inside directory is locked/cannot be deleted.
   * Returns path of the first undeletable file or null if all can be deleted.
   */
  public static canDelete(filePath: string): string | null {
    try {
      if (!fs.existsSync(filePath)) {
        return null;
      }
      const stat = fs.lstatSync(filePath);
      if (stat.isDirectory()) {
        const files = fs.readdirSync(filePath);
        for (const f of files) {
          const res = Dir.canDelete(path.join(filePath, f));
          if (res !== null) {
            return res;
          }
        }
      }
      // Try testing write access / lock check
      fs.accessSync(filePath, fs.constants.W_OK);
      return null;
    } catch {
      return filePath;
    }
  }

  /**
   * Copies source file to destination.
   */
  public static copy(inPath: string, outPath: string): boolean {
    try {
      if (fs.existsSync(outPath)) {
        fs.unlinkSync(outPath);
      }
      Dir.mkdirs(outPath);
      fs.copyFileSync(inPath, outPath);
      return true;
    } catch {
      try {
        if (fs.existsSync(outPath)) {
          fs.unlinkSync(outPath);
        }
      } catch {
        // ignore
      }
      return false;
    }
  }

  /**
   * Ensures parent directory (if file path has extension) or directory itself exists.
   */
  public static mkdirs(targetPath: string): void {
    try {
      const resolved = path.resolve(targetPath);
      const isFileLike = path.basename(resolved).includes('.');
      const dirToMake = isFileLike ? path.dirname(resolved) : resolved;
      if (!fs.existsSync(dirToMake)) {
        fs.mkdirSync(dirToMake, { recursive: true });
      }
    } catch {
      // ignore
    }
  }

  /**
   * Returns creation time in milliseconds.
   */
  public static getCreationTime(filePath: string): number {
    if (!fs.existsSync(filePath)) {
      throw new Error(Dir.MSG_FILE_NOT_FOUND);
    }
    const stat = fs.statSync(filePath);
    return stat.birthtimeMs;
  }

  /**
   * Returns last modified time in milliseconds.
   */
  public static getLastModifiedTime(filePath: string): number {
    if (!fs.existsSync(filePath)) {
      throw new Error(Dir.MSG_FILE_NOT_FOUND);
    }
    const stat = fs.statSync(filePath);
    return stat.mtimeMs;
  }

  /**
   * Sets last modified time in milliseconds.
   */
  public static setLastModifiedTime(filePath: string, timeMs: number): boolean {
    try {
      const sec = timeMs / 1000;
      fs.utimesSync(filePath, sec, sec);
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Calculates MD5 hash of a file as hexadecimal string.
   */
  public static getMD5(filePath: string): string {
    return Dir.getHash(filePath, 'md5');
  }

  /**
   * Calculates SHA-1 hash of a file as hexadecimal string (matches Java getHash default).
   */
  public static getSHA1(filePath: string): string {
    return Dir.getHash(filePath, 'sha1');
  }

  /**
   * Calculates cryptographic hash of a file. Defaults to 'sha1' matching Java Dir.getHash.
   */
  public static getHash(filePath: string, algorithm: string = 'sha1'): string {
    const hash = crypto.createHash(algorithm);
    const buffer = fs.readFileSync(filePath);
    hash.update(buffer);
    return hash.digest('hex');
  }

  /**
   * Removes invalid characters from file name: \ / : * ? " < > |
   */
  public static removeInvalidChar(name: string): string {
    return name.replace(/[\\/:*?"<>|]/g, '');
  }

  /**
   * Gets file name without extension.
   */
  public static getFileName(filePath: string): string {
    const base = path.basename(filePath);
    const idx = base.lastIndexOf('.');
    return idx > 0 ? base.substring(0, idx) : base;
  }

  /**
   * Returns true if file exists and is a regular file.
   */
  public static isFile(filePath: string): boolean {
    try {
      return fs.existsSync(filePath) && fs.statSync(filePath).isFile();
    } catch {
      return false;
    }
  }

  /**
   * Reads raw bytes from a file.
   */
  public static getBytes(filePath: string): Buffer {
    return fs.readFileSync(filePath);
  }

  /**
   * Reads raw bytes from a file, returning null on error.
   */
  public static getBytesNoException(filePath: string): Buffer | null {
    try {
      return fs.readFileSync(filePath);
    } catch {
      return null;
    }
  }

  /**
   * Saves raw bytes to a file.
   */
  public static saveBytes(bytes: Buffer | Uint8Array, filePath: string): void {
    Dir.mkdirs(filePath);
    fs.writeFileSync(filePath, bytes);
  }

  /**
   * Encrypts a file using AES-128-CBC with PKCS7 padding.
   */
  public static encrypt(filePath: string, key: string): boolean {
    try {
      const bytes = Dir.getBytes(filePath);
      const keyBuf = Dir.getPass16(key);
      const cipher = crypto.createCipheriv('aes-128-cbc', keyBuf, Dir.IV);
      cipher.setAutoPadding(true);
      const encrypted = Buffer.concat([cipher.update(bytes), cipher.final()]);
      fs.writeFileSync(filePath, encrypted);
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Decrypts a file encrypted using AES-128-CBC with PKCS7 padding.
   */
  public static decrypt(filePath: string, key: string): boolean {
    try {
      const bytes = Dir.getBytes(filePath);
      const keyBuf = Dir.getPass16(key);
      const decipher = crypto.createDecipheriv('aes-128-cbc', keyBuf, Dir.IV);
      decipher.setAutoPadding(true);
      const decrypted = Buffer.concat([decipher.update(bytes), decipher.final()]);
      fs.writeFileSync(filePath, decrypted);
      return true;
    } catch {
      return false;
    }
  }

  /**
   * Normalizes key to 16 bytes (AES-128) matching Java Security.getPass16
   */
  private static getPass16(key: string): Buffer {
    const keyBuf = Buffer.from(key, 'utf-8');
    const out = Buffer.alloc(16, 0);
    keyBuf.copy(out, 0, 0, Math.min(16, keyBuf.length));
    return out;
  }

  /**
   * Hides file (on Windows sets hidden attribute)
   */
  public static hideFile(filePath: string): boolean {
    try {
      if (os.platform() === 'win32') {
        const { execSync } = require('node:child_process');
        execSync(`attrib +h "${path.resolve(filePath)}"`);
      }
      return true;
    } catch {
      return false;
    }
  }
}

export default Dir;
