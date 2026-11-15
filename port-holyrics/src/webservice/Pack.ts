import { createHash } from 'node:crypto';

export class Pack {
  public static readonly KEY_ERROR = 'key_error';
  public static readonly KEY_OK = 'key_ok';
  public map: Record<string, any> = {};

  public static create(arg?: string | boolean | Record<string, any>, error: string = ''): Pack {
    const pack = new Pack();
    if (typeof arg === 'boolean') {
      pack.put(Pack.KEY_OK, String(arg));
      pack.put(Pack.KEY_ERROR, error);
      return pack;
    }

    if (typeof arg === 'object' && arg !== null) {
      if ('map' in arg && typeof (arg as any).map === 'object' && (arg as any).map !== null) {
        pack.map = { ...(arg as any).map };
      } else {
        pack.map = { ...arg };
      }
      return pack;
    }

    if (typeof arg === 'string') {
      try {
        const parsed = JSON.parse(arg);
        if (parsed && typeof parsed === 'object') {
          if ('map' in parsed && typeof parsed.map === 'object' && parsed.map !== null) {
            pack.map = { ...parsed.map };
          } else {
            pack.map = { ...parsed };
          }
          return pack;
        }
      } catch {
        return Pack.create(false, 'exception');
      }
    }

    return pack;
  }

  public static createDecrypt(p: Pack, key: string): Pack {
    try {
      const toJson64 = p.getString('pack64', '');
      if (!toJson64) {
        return Pack.create(false, 'exception');
      }
      const raw = Buffer.from(toJson64, 'base64');
      const decrypted = Pack.decryptCompatApp(raw, key);
      return Pack.create(decrypted);
    } catch {
      return Pack.create(false, 'exception');
    }
  }

  public toJSon(): string {
    return JSON.stringify({ map: this.map });
  }

  public toJSON(): { map: Record<string, any> } {
    return { map: this.map };
  }

  public encrypt(key: string): Pack {
    try {
      const jsonStr = this.toJSon();
      const encryptedBytes = Pack.encryptCompatApp(jsonStr, key);
      const packEnc64 = encryptedBytes.toString('base64');
      return Pack.create(true)
        .put('request', this.getString('request', ''))
        .put('pack64', packEnc64);
    } catch {
      return Pack.create(false, 'exception');
    }
  }

  public put(key: string, value: any): this {
    if (value === undefined || value === null) {
      this.map[key] = '';
    } else if (typeof value === 'number' || typeof value === 'bigint') {
      this.map[key] = String(value);
    } else {
      this.map[key] = value;
    }
    return this;
  }

  public isOk(): boolean {
    const val = this.map[Pack.KEY_OK];
    return val === true || val === 'true';
  }

  public error(): string {
    return this.map[Pack.KEY_ERROR] ? String(this.map[Pack.KEY_ERROR]) : '';
  }

  public getString(key: string, defaultValue: string = ''): string {
    const val = this.map[key];
    return val !== undefined && val !== null ? String(val) : defaultValue;
  }

  public getList(key: string): string[] {
    const val = this.map[key];
    if (Array.isArray(val)) {
      return val.map((v) => String(v));
    }
    return [];
  }

  public getBoolean(key: string): boolean {
    const val = this.map[key];
    return val === true || val === 'true';
  }

  public getInt(key: string, defaultValue: number = 0): number {
    const val = this.map[key];
    if (val === undefined || val === null) {
      return defaultValue;
    }
    const num = parseInt(String(val), 10);
    return isNaN(num) ? defaultValue : num;
  }

  public getLong(key: string, defaultValue: number = 0): number {
    return this.getInt(key, defaultValue);
  }

  public static getMD5(str: string, iterations: number = 1): string {
    let result = str;
    for (let i = 0; i < iterations; i++) {
      result = createHash('md5').update(result).digest('hex');
    }
    return result;
  }

  private static decryptCompatApp(bytes: Buffer, key: string): string {
    // Simple XOR / fallback compatibility decrypt with key
    const keyBuf = Buffer.from(key, 'utf8');
    if (keyBuf.length === 0) return bytes.toString('utf8');
    const out = Buffer.alloc(bytes.length);
    for (let i = 0; i < bytes.length; i++) {
      const b = bytes[i];
      const k = keyBuf[i % keyBuf.length];
      if (b !== undefined && k !== undefined) {
        out[i] = b ^ k;
      }
    }
    return out.toString('utf8');
  }

  private static encryptCompatApp(text: string, key: string): Buffer {
    const bytes = Buffer.from(text, 'utf8');
    const keyBuf = Buffer.from(key, 'utf8');
    if (keyBuf.length === 0) return bytes;
    const out = Buffer.alloc(bytes.length);
    for (let i = 0; i < bytes.length; i++) {
      const b = bytes[i];
      const k = keyBuf[i % keyBuf.length];
      if (b !== undefined && k !== undefined) {
        out[i] = b ^ k;
      }
    }
    return out;
  }
}
