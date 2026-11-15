import * as crypto from "node:crypto";

export class Security {
  private static readonly IV = "AAAAAAAAAAAAAAAA";

  public static getSHA256(value: string): string {
    const hash = crypto.createHash("sha256").update(Buffer.from(value, "utf-8")).digest("hex");
    return hash.toUpperCase();
  }

  public static getPass16(key: string): string {
    const safeKey = key && key.length > 0 ? key : "A";
    try {
      return this.getSHA256(safeKey).substring(0, 16);
    } catch {
      let k = safeKey;
      while (k.length < 16) {
        k = k + k;
      }
      return k.substring(0, 16);
    }
  }

  public static encrypt(plainText: string, encKey: string): Buffer {
    const pass16 = this.getPass16(encKey);
    const key = Buffer.from(pass16, "utf-8");
    const iv = Buffer.from(this.IV, "utf-8");
    const cipher = crypto.createCipheriv("aes-128-cbc", key, iv);
    return Buffer.concat([cipher.update(Buffer.from(plainText, "utf-8")), cipher.final()]);
  }

  public static decrypt(cipherText: Buffer | Uint8Array, encKey: string): string {
    const pass16 = this.getPass16(encKey);
    const key = Buffer.from(pass16, "utf-8");
    const iv = Buffer.from(this.IV, "utf-8");
    const decipher = crypto.createDecipheriv("aes-128-cbc", key, iv);
    const decrypted = Buffer.concat([decipher.update(Buffer.from(cipherText)), decipher.final()]);
    return decrypted.toString("utf-8");
  }

  public static encryptCompatApp(message: string, key: string): Buffer {
    const pass16 = this.getPass16(key);
    const passBuf = Buffer.from(pass16, "utf-8");
    const cipher = crypto.createCipheriv("aes-128-ecb", passBuf, null);
    return Buffer.concat([cipher.update(Buffer.from(message, "utf-8")), cipher.final()]);
  }

  public static decryptCompatApp(cipherText: Buffer | Uint8Array, key: string): string {
    const pass16 = this.getPass16(key);
    const passBuf = Buffer.from(pass16, "utf-8");
    const decipher = crypto.createDecipheriv("aes-128-ecb", passBuf, null);
    const decrypted = Buffer.concat([decipher.update(Buffer.from(cipherText)), decipher.final()]);
    return decrypted.toString("utf-8");
  }

  public static encryptBytes(bytes: Buffer | Uint8Array, encKey: string): Buffer {
    const pass16 = this.getPass16(encKey);
    const key = Buffer.from(pass16, "utf-8");
    const iv = Buffer.from(this.IV, "utf-8");
    const cipher = crypto.createCipheriv("aes-128-cbc", key, iv);
    return Buffer.concat([cipher.update(Buffer.from(bytes)), cipher.final()]);
  }

  public static decryptBytes(cipherText: Buffer | Uint8Array, encKey: string): Buffer {
    const pass16 = this.getPass16(encKey);
    const key = Buffer.from(pass16, "utf-8");
    const iv = Buffer.from(this.IV, "utf-8");
    const decipher = crypto.createDecipheriv("aes-128-cbc", key, iv);
    return Buffer.concat([decipher.update(Buffer.from(cipherText)), decipher.final()]);
  }

  public static getMD5(value: string, factor?: number): string {
    try {
      if (factor === undefined) {
        const hash = crypto.createHash("md5").update(Buffer.from(value, "utf-8")).digest("hex");
        return BigInt("0x" + hash).toString(16);
      } else {
        const doubled = value + value;
        const bytes = Buffer.from(doubled, "utf-8");
        for (let ix = 0; ix < bytes.length; ix++) {
          if (ix % 3 === 0) {
            const current = bytes[ix] ?? 0;
            bytes[ix] = current ^ (factor & 0xff);
          }
        }
        const slice = bytes.subarray(0, doubled.length);
        const hash = crypto.createHash("md5").update(slice).digest("hex");
        return BigInt("0x" + hash).toString(16);
      }
    } catch {
      return "";
    }
  }

  public static toBase64(value: string | Buffer): string {
    if (typeof value === "string") {
      return Buffer.from(value, "utf-8").toString("base64");
    }
    return value.toString("base64");
  }

  public static fromBase64(base64Str: string): string {
    return Buffer.from(base64Str, "base64").toString("utf-8");
  }

  public static fromBase64Bytes(base64Str: string): Buffer {
    return Buffer.from(base64Str, "base64");
  }
}

export default Security;
