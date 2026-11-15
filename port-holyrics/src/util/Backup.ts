import * as fs from 'node:fs';
import * as path from 'node:path';
import * as zlib from 'node:zlib';
import { Dir } from './Dir';

export interface BackupProgress {
  update?: (length: number, total: number, file: string) => void;
}

/**
 * CRC32 implementation for standard ZIP format
 */
function createCrcTable(): Uint32Array {
  const table = new Uint32Array(256);
  for (let i = 0; i < 256; i++) {
    let c = i;
    for (let k = 0; k < 8; k++) {
      c = (c & 1) ? (0xEDB88320 ^ (c >>> 1)) : (c >>> 1);
    }
    table[i] = c >>> 0;
  }
  return table;
}

const CRC_TABLE = createCrcTable();

function calculateCrc32(buf: Buffer): number {
  let crc = 0xFFFFFFFF;
  for (let i = 0; i < buf.length; i++) {
    const byte = buf[i] ?? 0;
    const tableVal = CRC_TABLE[(crc ^ byte) & 0xFF] ?? 0;
    crc = (crc >>> 8) ^ tableVal;
  }
  return (crc ^ 0xFFFFFFFF) >>> 0;
}

interface ZipEntry {
  path: string;
  isDir: boolean;
  data: Buffer;
  crc: number;
  compressedData: Buffer;
  offset: number;
}

/**
 * Pure TypeScript ZIP creator & extractor using node:zlib
 */
export class ZipUtil {
  public static zipDirectory(sourceDir: string, outZipPath: string, onProgress?: (current: number, total: number, file: string) => void): void {
    const entries: { relativePath: string; fullPath: string; isDir: boolean }[] = [];

    function collect(currentDir: string) {
      const items = fs.readdirSync(currentDir);
      for (const item of items) {
        const full = path.join(currentDir, item);
        const rel = path.relative(sourceDir, full).replace(/\\/g, '/');
        const stat = fs.statSync(full);
        if (stat.isDirectory()) {
          entries.push({ relativePath: rel + '/', fullPath: full, isDir: true });
          collect(full);
        } else {
          entries.push({ relativePath: rel, fullPath: full, isDir: false });
        }
      }
    }

    if (fs.existsSync(sourceDir)) {
      collect(sourceDir);
    }

    const zipEntries: ZipEntry[] = [];
    const localHeaders: Buffer[] = [];
    let currentOffset = 0;

    for (let i = 0; i < entries.length; i++) {
      const entry = entries[i];
      if (!entry) continue;
      let data = Buffer.alloc(0);
      let compressed = Buffer.alloc(0);
      let crc = 0;
      let method = 0; // 0 = stored, 8 = deflated

      if (!entry.isDir) {
        data = fs.readFileSync(entry.fullPath);
        crc = calculateCrc32(data);
        if (data.length > 0) {
          compressed = zlib.deflateRawSync(data);
          method = 8;
        }
      }

      const nameBuf = Buffer.from(entry.relativePath, 'utf-8');
      const localHeader = Buffer.alloc(30 + nameBuf.length);

      localHeader.writeUInt32LE(0x04034b50, 0); // Local file header signature
      localHeader.writeUInt16LE(20, 4); // Version needed to extract (2.0)
      localHeader.writeUInt16LE(0x0800, 6); // General purpose bit flag (UTF-8)
      localHeader.writeUInt16LE(method, 8); // Compression method
      localHeader.writeUInt16LE(0, 10); // Mod time
      localHeader.writeUInt16LE(0, 12); // Mod date
      localHeader.writeUInt32LE(crc, 14); // CRC-32
      localHeader.writeUInt32LE(compressed.length, 18); // Compressed size
      localHeader.writeUInt32LE(data.length, 22); // Uncompressed size
      localHeader.writeUInt16LE(nameBuf.length, 26); // File name length
      localHeader.writeUInt16LE(0, 28); // Extra field length
      nameBuf.copy(localHeader, 30);

      zipEntries.push({
        path: entry.relativePath,
        isDir: entry.isDir,
        data,
        crc,
        compressedData: compressed,
        offset: currentOffset,
      });

      localHeaders.push(localHeader);
      if (compressed.length > 0) {
        localHeaders.push(compressed);
      }
      currentOffset += localHeader.length + compressed.length;

      if (onProgress) {
        onProgress(i + 1, entries.length, entry.relativePath);
      }
    }

    const centralDirHeaders: Buffer[] = [];
    let centralDirSize = 0;

    for (const ze of zipEntries) {
      const nameBuf = Buffer.from(ze.path, 'utf-8');
      const cd = Buffer.alloc(46 + nameBuf.length);
      const method = (!ze.isDir && ze.data.length > 0) ? 8 : 0;

      cd.writeUInt32LE(0x02014b50, 0); // Central directory file header signature
      cd.writeUInt16LE(20, 4); // Version made by
      cd.writeUInt16LE(20, 6); // Version needed to extract
      cd.writeUInt16LE(0x0800, 8); // General purpose bit flag (UTF-8)
      cd.writeUInt16LE(method, 10); // Compression method
      cd.writeUInt16LE(0, 12); // Mod time
      cd.writeUInt16LE(0, 14); // Mod date
      cd.writeUInt32LE(ze.crc, 16); // CRC-32
      cd.writeUInt32LE(ze.compressedData.length, 20); // Compressed size
      cd.writeUInt32LE(ze.data.length, 24); // Uncompressed size
      cd.writeUInt16LE(nameBuf.length, 28); // File name length
      cd.writeUInt16LE(0, 30); // Extra field length
      cd.writeUInt16LE(0, 32); // File comment length
      cd.writeUInt16LE(0, 34); // Disk number start
      cd.writeUInt16LE(0, 36); // Internal file attributes
      cd.writeUInt32LE(ze.isDir ? 0x10 : 0, 38); // External file attributes
      cd.writeUInt32LE(ze.offset, 42); // Relative offset of local header
      nameBuf.copy(cd, 46);

      centralDirHeaders.push(cd);
      centralDirSize += cd.length;
    }

    // End of central directory record
    const eocd = Buffer.alloc(22);
    eocd.writeUInt32LE(0x06054b50, 0); // End of central dir signature
    eocd.writeUInt16LE(0, 4); // Number of this disk
    eocd.writeUInt16LE(0, 6); // Disk where central directory starts
    eocd.writeUInt16LE(zipEntries.length, 8); // Number of central dir records on this disk
    eocd.writeUInt16LE(zipEntries.length, 10); // Total number of central dir records
    eocd.writeUInt32LE(centralDirSize, 12); // Size of central directory
    eocd.writeUInt32LE(currentOffset, 16); // Offset of start of central directory
    eocd.writeUInt16LE(0, 20); // ZIP file comment length

    Dir.mkdirs(outZipPath);
    const fd = fs.openSync(outZipPath, 'w');
    try {
      for (const h of localHeaders) {
        fs.writeSync(fd, h);
      }
      for (const cd of centralDirHeaders) {
        fs.writeSync(fd, cd);
      }
      fs.writeSync(fd, eocd);
    } finally {
      fs.closeSync(fd);
    }
  }

  public static unzip(zipFilePath: string, destDir: string): void {
    const buf = fs.readFileSync(zipFilePath);
    let offset = 0;

    while (offset < buf.length - 4) {
      const sig = buf.readUInt32LE(offset);
      if (sig === 0x04034b50) {
        const method = buf.readUInt16LE(offset + 8);
        const compressedSize = buf.readUInt32LE(offset + 18);
        const nameLen = buf.readUInt16LE(offset + 26);
        const extraLen = buf.readUInt16LE(offset + 28);
        const name = buf.toString('utf-8', offset + 30, offset + 30 + nameLen);
        const dataStart = offset + 30 + nameLen + extraLen;
        const compressedData = buf.subarray(dataStart, dataStart + compressedSize);

        const targetPath = path.join(destDir, name);
        if (name.endsWith('/')) {
          fs.mkdirSync(targetPath, { recursive: true });
        } else {
          Dir.mkdirs(targetPath);
          let uncompressed: Buffer;
          if (method === 8) {
            uncompressed = zlib.inflateRawSync(compressedData);
          } else {
            uncompressed = Buffer.from(compressedData);
          }
          fs.writeFileSync(targetPath, uncompressed);
        }
        offset = dataStart + compressedSize;
      } else {
        // Move forward if scanning
        offset++;
      }
    }
  }
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.util.Backup
 * Logic for exporting and importing Holyrics backup files (.hbac) and .hly config files.
 */
export class Backup {
  private static readonly isPt = (process.env.LANG || Intl.DateTimeFormat().resolvedOptions().locale || '').toLowerCase().startsWith('pt');

  public static readonly WORD_EXPORT_IN_PROGRESS: string = Backup.isPt ? 'Exportação em andamento...' : 'Export in progress...';
  public static readonly WORD_IMPORT_IN_PROGRESS: string = Backup.isPt ? 'Importação em andamento...' : 'Import in progress...';
  public static readonly WORD_DONE: string = Backup.isPt ? 'Feito!' : 'Done!';
  public static readonly WORD_RESTARTING: string = Backup.isPt ? 'Reiniciando...' : 'Restarting...';
  public static readonly WORD_DONE_RESTART: string = Backup.isPt ? 'Feito! O programa será reiniciado.' : 'Done! The program will be restarted.';
  public static readonly MSG_ERROR_TRY_AGAIN: string = Backup.isPt ? 'Erro! Tente novamente.' : 'Error! Try again.';
  public static readonly MSG_ERROR_ACCESS_DENIED: string = Backup.isPt ? 'Erro! Acesso negado.' : 'Error! Access denied.';
  public static readonly MSG_CONFIRM_IMPORT_BACKUP: string = Backup.isPt
    ? 'Os dados atuais serão apagados.\nVocê tem certeza que deseja importar o backup?\n(Esta operação não poderá ser desfeita)'
    : 'The current data will be deleted.\nAre you sure want to import the backup?\n(This operation cannot be undone)';

  public static readonly FILE_EXTENSION = '.hbac';
  public static readonly FILE_DESCRIPTION = 'Backup File (*.hbac)';
  public static readonly FILE_EXTENSION_HLY = '.hly';

  public static get PATH_BACKUP(): string {
    return Dir.getHolyricsDirectory();
  }

  /**
   * Finds all .hly config files in a given directory recursively.
   */
  public static findHlyFiles(dirPath: string): string[] {
    const results: string[] = [];
    if (!fs.existsSync(dirPath)) {
      return results;
    }

    const items = fs.readdirSync(dirPath, { withFileTypes: true });
    for (const item of items) {
      const full = path.join(dirPath, item.name);
      if (item.isDirectory()) {
        results.push(...Backup.findHlyFiles(full));
      } else if (item.name.toLowerCase().endsWith(Backup.FILE_EXTENSION_HLY)) {
        results.push(full);
      }
    }
    return results;
  }

  /**
   * Copies .hly files from source to destination directory, preserving folder structure.
   */
  public static copyHlyFiles(sourceDir: string, destDir: string): string[] {
    const files = Backup.findHlyFiles(sourceDir);
    const copied: string[] = [];

    for (const file of files) {
      const rel = path.relative(sourceDir, file);
      const dest = path.join(destDir, rel);
      Dir.mkdirs(dest);
      fs.copyFileSync(file, dest);
      copied.push(dest);
    }
    return copied;
  }

  /**
   * Exports backup of source directory (or PATH_BACKUP) to a target zip/.hbac file.
   */
  public static async exportBackup(
    destZipFile: string,
    sourceDir: string = Backup.PATH_BACKUP,
    progress?: BackupProgress
  ): Promise<{ success: boolean; message: string }> {
    try {
      if (fs.existsSync(destZipFile)) {
        fs.unlinkSync(destZipFile);
      }
      Dir.mkdirs(destZipFile);

      ZipUtil.zipDirectory(sourceDir, destZipFile, (cur, total, file) => {
        if (progress?.update) {
          progress.update(cur, total, file);
        }
      });

      return { success: true, message: 't' + Backup.WORD_DONE };
    } catch (err) {
      try {
        if (fs.existsSync(destZipFile)) {
          fs.unlinkSync(destZipFile);
        }
      } catch {
        // ignore
      }
      return { success: false, message: 'f' + Backup.MSG_ERROR_TRY_AGAIN };
    }
  }

  /**
   * Imports backup from a .hbac / .zip file into target directory (or parent of PATH_BACKUP).
   */
  public static async importBackup(
    sourceZipFile: string,
    targetDir: string = Backup.PATH_BACKUP
  ): Promise<{ success: boolean; message: string }> {
    const noDelete = Dir.canDelete(targetDir);
    if (noDelete) {
      return {
        success: false,
        message: `Não é possível restaurar o backup neste momento. O arquivo ${noDelete} está em uso.`,
      };
    }

    try {
      if (fs.existsSync(targetDir)) {
        const deleted = Dir.deleteDir(targetDir);
        if (!deleted) {
          return { success: false, message: 'f' + Backup.MSG_ERROR_ACCESS_DENIED };
        }
      }

      // Check whether entries in zip already include the targetDir's folder name
      const buf = fs.readFileSync(sourceZipFile);
      let extractTo = targetDir;
      const baseName = path.basename(targetDir);
      if (buf.length > 30) {
        const nameLen = buf.readUInt16LE(26);
        const firstName = buf.toString('utf-8', 30, 30 + nameLen);
        if (firstName.startsWith(baseName + '/') || firstName.startsWith(baseName + '\\')) {
          extractTo = path.dirname(targetDir);
        }
      }

      ZipUtil.unzip(sourceZipFile, extractTo);
      return { success: true, message: 't' + Backup.WORD_DONE_RESTART };
    } catch {
      return { success: false, message: 'f' + Backup.MSG_ERROR_TRY_AGAIN };
    }
  }
}

export default Backup;
