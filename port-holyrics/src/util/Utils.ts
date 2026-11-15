let millisecondId = 0;
let lastClickX = -1;
let lastClickY = -1;
let lastClickTime = 0;
let nanoTime = 0;

export interface DimensionLike {
  width: number;
  height: number;
}

export interface ColorRGBA {
  r: number;
  g: number;
  b: number;
  a?: number;
}

export class Utils {
  public static ini(): void {
    nanoTime = typeof performance !== "undefined" ? performance.now() : Date.now();
  }

  public static end(): string {
    const now = typeof performance !== "undefined" ? performance.now() : Date.now();
    const time = (now - nanoTime) / 1000.0;
    let result: string;
    if (time > 60.0) {
      const mod = time % 60.0;
      const minutes = Math.floor(time / 60.0);
      result = `${minutes}min${Math.floor(mod)}s`;
    } else {
      result = `${time.toFixed(3)}s`;
    }
    return result;
  }

  public static sleep(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  public static generateMillisecondId(): number {
    const id = Date.now();
    if (id <= millisecondId) {
      millisecondId += 1;
      return millisecondId;
    }
    millisecondId = id;
    return id;
  }

  public static removeAccent(str: string): string {
    return (str ?? "").normalize("NFD").replace(/[\u0300-\u036f]/g, "");
  }

  public static range(value: number, min: number, max: number): number {
    return value < min ? min : value > max ? max : value;
  }

  public static random(max: number): number {
    if (max < 1) return -1;
    return Math.floor(Math.random() * max) + 1;
  }

  public static randomBoolean(): boolean {
    return Math.random() < 0.5;
  }

  public static lowPassRandom(size: number): string {
    const chars = "ae12345";
    let res = "";
    for (let i = 0; i < size; i++) {
      res += chars.charAt(Utils.random(chars.length) - 1);
    }
    return res;
  }

  public static intOrElse(s: unknown, def: number): number {
    if (s === null || s === undefined) return def;
    const parsed = parseInt(String(s), 10);
    return isNaN(parsed) ? def : parsed;
  }

  public static floatOrElse(s: unknown, def: number): number {
    if (s === null || s === undefined) return def;
    const parsed = parseFloat(String(s));
    return isNaN(parsed) ? def : parsed;
  }

  public static longOrElse(s: unknown, def: number): number {
    return Utils.intOrElse(s, def);
  }

  public static doubleOrElse(s: unknown, def: number): number {
    return Utils.floatOrElse(s, def);
  }

  public static firstLetterToUpperCase(value: string | null | undefined): string | null {
    if (value === null || value === undefined) {
      return null;
    }
    const escape = ["da", "de", "do", "das", "dos"];
    const splits = [" ", "\n"];
    let result = value.toLowerCase();

    for (const split of splits) {
      const palavras = result.split(split);
      const sb: string[] = [];

      for (const s of palavras) {
        if (!s) {
          sb.push("");
          continue;
        }
        if (!escape.includes(s)) {
          sb.push(s.charAt(0).toUpperCase() + s.substring(1));
        } else {
          sb.push(s);
        }
      }

      result = sb.join(split);
    }

    return result;
  }

  public static getHexColor(c: ColorRGBA): string {
    const r = Utils.intToHex(c.r, 2);
    const g = Utils.intToHex(c.g, 2);
    const b = Utils.intToHex(c.b, 2);
    return `${r}${g}${b}`;
  }

  public static intToHex(value: number, size: number): string {
    let hex = (value || 0).toString(16);
    while (hex.length < size) {
      hex = "0" + hex;
    }
    return hex;
  }

  public static getPercentage(value: number, total: number): string {
    if (total > 0.0) {
      return `${((value / total) * 100.0).toFixed(1)}%`;
    }
    return "0.0%";
  }

  public static getPercentageInt(value: number, total: number): number {
    return Utils.range(total > 0.0 ? Math.floor((value / total) * 100.0) : 0, 0, 100);
  }

  public static removeExcessSpace(value: string | null | undefined): string | null {
    if (value === null || value === undefined) return null;
    return value.trim().replace(/[ ]{2,}/g, " ");
  }

  public static notOnlySpace(text: string): string {
    return text.replace(/ /g, "").length === 0 ? "" : text;
  }

  public static isDoubleClick(screenX: number, screenY: number): boolean {
    const now = Date.now();
    const isDouble =
      lastClickX === screenX &&
      lastClickY === screenY &&
      now - lastClickTime <= 500;
    lastClickX = screenX;
    lastClickY = screenY;
    lastClickTime = now;
    return isDouble;
  }

  public static clearDoubleClick(): void {
    lastClickX = -1;
    lastClickY = -1;
    lastClickTime = 0;
  }

  public static decodeColor(value: string | null | undefined, defaultColor: ColorRGBA | null = null): ColorRGBA | null {
    if (!value) return defaultColor;
    try {
      if (value.includes(",")) {
        const rgb = value.split(",");
        const r = Utils.intOrElse(rgb[0]?.trim(), 0);
        const g = Utils.intOrElse(rgb[1]?.trim(), 0);
        const b = Utils.intOrElse(rgb[2]?.trim(), 0);
        const a = rgb.length > 3 ? Utils.intOrElse(rgb[3]?.trim(), 255) : 255;
        return { r, g, b, a };
      } else {
        let hex = value.replace("#", "").replace(/ /g, "");
        if (hex.length === 3) {
          hex = hex.split("").map((ch) => ch + ch).join("");
        } else if (hex.length === 4) {
          hex = hex.split("").map((ch) => ch + ch).join("");
        }

        if (hex.length === 6) {
          const num = parseInt(hex, 16);
          return {
            r: (num >> 16) & 0xff,
            g: (num >> 8) & 0xff,
            b: num & 0xff,
            a: 255,
          };
        } else if (hex.length === 8) {
          const num = parseInt(hex.substring(0, 6), 16);
          const alpha = parseInt(hex.substring(6, 8), 16);
          return {
            r: (num >> 16) & 0xff,
            g: (num >> 8) & 0xff,
            b: num & 0xff,
            a: alpha,
          };
        }
      }
    } catch {
      // ignore
    }
    return defaultColor;
  }

  public static maximum(size: DimensionLike, max: DimensionLike): DimensionLike {
    let ratio: number;
    if (size.width / size.height > max.width / max.height) {
      ratio = max.width / size.width;
    } else {
      ratio = max.height / size.height;
    }
    return {
      width: Math.floor(size.width * ratio),
      height: Math.floor(size.height * ratio),
    };
  }

  public static maximumSetup(size: DimensionLike, max: DimensionLike): void {
    const res = Utils.maximum(size, max);
    size.width = res.width;
    size.height = res.height;
  }

  public static bytesToBase64(bytes: Uint8Array): string {
    if (typeof Buffer !== "undefined") {
      return Buffer.from(bytes).toString("base64");
    }
    let binary = "";
    for (let i = 0; i < bytes.byteLength; i++) {
      const byte = bytes[i];
      if (byte !== undefined) {
        binary += String.fromCharCode(byte);
      }
    }
    return btoa(binary);
  }

  public static base64ToBytes(base64: string): Uint8Array {
    if (typeof Buffer !== "undefined") {
      return new Uint8Array(Buffer.from(base64, "base64"));
    }
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
      bytes[i] = binary.charCodeAt(i);
    }
    return bytes;
  }

  public static safeException(runnable: () => void): void {
    try {
      runnable();
    } catch {
      // safe ignore
    }
  }
}

export default Utils;
