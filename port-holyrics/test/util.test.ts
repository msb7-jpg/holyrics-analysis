import { describe, it, expect, beforeEach, afterEach } from 'bun:test';
import * as fs from 'node:fs';
import * as path from 'node:path';
import { Dir } from '../src/util/Dir';
import { RW } from '../src/util/RW';
import { Backup } from '../src/util/Backup';

const TEST_DIR = path.join('/tmp', 'holyrics_test_util_' + Date.now());

describe('Dir, RW, Backup utils', () => {
  beforeEach(() => {
    fs.mkdirSync(TEST_DIR, { recursive: true });
  });

  afterEach(() => {
    if (fs.existsSync(TEST_DIR)) {
      Dir.deleteDir(TEST_DIR);
    }
  });

  describe('RW', () => {
    it('should write and read string files with BOM stripping', () => {
      const testFile = path.join(TEST_DIR, 'test_rw.txt');
      RW.write(testFile, 'Line 1\nLine 2\nLine 3');
      const content = RW.read(testFile);
      expect(content).toBe('Line 1\nLine 2\nLine 3');

      // Test with BOM
      fs.writeFileSync(testFile, '\ufeffHello World', { encoding: 'utf-8' });
      expect(RW.read(testFile)).toBe('Hello World');
    });

    it('should write and read buffers', async () => {
      const testFile = path.join(TEST_DIR, 'test_buf.bin');
      const data = Buffer.from([1, 2, 3, 4, 5]);
      RW.writeBuffer(testFile, data);
      const readData = RW.readBuffer(testFile);
      expect(Buffer.compare(data, readData)).toBe(0);

      const asyncRead = await RW.readBufferAsync(testFile);
      expect(Buffer.compare(data, asyncRead)).toBe(0);
    });
  });

  describe('Dir', () => {
    it('should compute MD5 and SHA-1 hashes accurately', () => {
      const testFile = path.join(TEST_DIR, 'test_hash.txt');
      RW.write(testFile, 'Holyrics');
      const md5 = Dir.getMD5(testFile);
      const sha1 = Dir.getSHA1(testFile);
      expect(md5).toBe('f828b9968aaa59661356ea8b23ae232e');
      expect(sha1).toBe('28e0d6c93a7faecc111519edeaa3ad553625a6ab');
    });

    it('should copy files and create directories recursively', () => {
      const source = path.join(TEST_DIR, 'sub1', 'source.txt');
      const dest = path.join(TEST_DIR, 'sub2', 'dest.txt');
      RW.write(source, 'Copy content');
      expect(Dir.copy(source, dest)).toBe(true);
      expect(RW.read(dest)).toBe('Copy content');
    });

    it('should encrypt and decrypt using AES-128-CBC', () => {
      const testFile = path.join(TEST_DIR, 'secret.txt');
      RW.write(testFile, 'Confidential Holyrics data');
      const key = 'my_secret_key_12';

      expect(Dir.encrypt(testFile, key)).toBe(true);
      const encrypted = fs.readFileSync(testFile);
      expect(encrypted.toString('utf-8')).not.toBe('Confidential Holyrics data');

      expect(Dir.decrypt(testFile, key)).toBe(true);
      const decrypted = RW.read(testFile);
      expect(decrypted).toBe('Confidential Holyrics data');
    });

    it('should get and set modified time', () => {
      const testFile = path.join(TEST_DIR, 'time.txt');
      RW.write(testFile, 'Timestamp test');
      const targetTime = 1600000000000;
      expect(Dir.setLastModifiedTime(testFile, targetTime)).toBe(true);
      const mtime = Dir.getLastModifiedTime(testFile);
      expect(Math.floor(mtime / 1000)).toBe(Math.floor(targetTime / 1000));
    });

    it('should remove invalid characters and get filename', () => {
      expect(Dir.removeInvalidChar('file:name*with?bad"chars<>.txt')).toBe('filenamewithbadchars.txt');
      expect(Dir.getFileName('path/to/song.hly')).toBe('song');
    });

    it('should return system paths', () => {
      expect(typeof Dir.getUserHome()).toBe('string');
      expect(typeof Dir.getAppDataDirectory()).toBe('string');
      expect(typeof Dir.getHolyricsDirectory()).toBe('string');
      expect(typeof Dir.getRootDirectory()).toBe('string');
    });
  });

  describe('Backup', () => {
    it('should find and copy .hly files', () => {
      const sourceDir = path.join(TEST_DIR, 'hly_src');
      const destDir = path.join(TEST_DIR, 'hly_dest');
      RW.write(path.join(sourceDir, 'config.hly'), 'cfg1');
      RW.write(path.join(sourceDir, 'sub', 'other.hly'), 'cfg2');
      RW.write(path.join(sourceDir, 'ignore.txt'), 'not hly');

      const found = Backup.findHlyFiles(sourceDir);
      expect(found.length).toBe(2);

      const copied = Backup.copyHlyFiles(sourceDir, destDir);
      expect(copied.length).toBe(2);
      expect(RW.read(path.join(destDir, 'config.hly'))).toBe('cfg1');
      expect(RW.read(path.join(destDir, 'sub', 'other.hly'))).toBe('cfg2');
    });

    it('should export and import backup (.hbac zip)', async () => {
      const sourceDataDir = path.join(TEST_DIR, 'data');
      RW.write(path.join(sourceDataDir, 'song.hly'), 'Holyrics Song Data');
      RW.write(path.join(sourceDataDir, 'nested', 'theme.hly'), 'Theme Settings');

      const backupFile = path.join(TEST_DIR, 'backup.hbac');
      const exportRes = await Backup.exportBackup(backupFile, sourceDataDir);
      expect(exportRes.success).toBe(true);
      expect(fs.existsSync(backupFile)).toBe(true);

      const restoreDir = path.join(TEST_DIR, 'restored', 'data');
      const importRes = await Backup.importBackup(backupFile, restoreDir);
      expect(importRes.success).toBe(true);
      expect(RW.read(path.join(restoreDir, 'song.hly'))).toBe('Holyrics Song Data');
      expect(RW.read(path.join(restoreDir, 'nested', 'theme.hly'))).toBe('Theme Settings');
    });
  });
});
