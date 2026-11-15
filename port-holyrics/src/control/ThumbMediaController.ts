import * as fs from "node:fs";
import * as path from "node:path";
import Values from "../util/Values.ts";

export interface Dimension {
  width: number;
  height: number;
}

export class Thumb {
  public static readonly serialVersionUID = 1;
  public readonly thumb: Uint8Array | null;
  public readonly lastModified: number;

  constructor(thumb: Uint8Array | null, lastModified: number) {
    this.thumb = thumb;
    this.lastModified = lastModified;
  }
}

class ThumbControllerFactory {
  private static readonly FILE_NAME = "thumbCache.hly";
  private static readonly PASSWORD = "&p4ssS3sp";

  private static getFilePath(): string {
    const dir = path.resolve(process.cwd(), Values.PATH_FILES);
    if (!fs.existsSync(dir)) {
      try {
        fs.mkdirSync(dir, { recursive: true });
      } catch {
        // ignore
      }
    }
    return path.join(dir, ThumbControllerFactory.FILE_NAME);
  }

  public static getInstance(): Map<string, Thumb> {
    const map = new Map<string, Thumb>();
    try {
      const filePath = ThumbControllerFactory.getFilePath();
      if (fs.existsSync(filePath)) {
        const raw = fs.readFileSync(filePath, "utf-8");
        const parsed = JSON.parse(raw);
        if (parsed && typeof parsed === "object") {
          for (const [key, val] of Object.entries(parsed)) {
            const v = val as { thumb?: number[] | string | Uint8Array; lastModified?: number };
            let bytes: Uint8Array | null = null;
            if (v.thumb) {
              if (v.thumb instanceof Uint8Array) {
                bytes = v.thumb;
              } else if (Array.isArray(v.thumb)) {
                bytes = new Uint8Array(v.thumb);
              } else if (typeof v.thumb === "string") {
                bytes = new Uint8Array(Buffer.from(v.thumb, "base64"));
              }
            }
            map.set(key, new Thumb(bytes, v.lastModified ?? 0));
          }
        }
      }
    } catch {
      // Fallback on empty map
    }
    return map;
  }

  public static save(map: Map<string, Thumb>): void {
    try {
      const filePath = ThumbControllerFactory.getFilePath();
      const obj: Record<string, { thumb: string | null; lastModified: number }> = {};
      for (const [key, val] of map.entries()) {
        obj[key] = {
          thumb: val.thumb ? Buffer.from(val.thumb).toString("base64") : null,
          lastModified: val.lastModified,
        };
      }
      fs.writeFileSync(filePath, JSON.stringify(obj, null, 2), "utf-8");
    } catch {
      // ignore write errors
    }
  }
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.control.ThumbMediaController
 */
export class ThumbMediaController {
  public static readonly serialVersionUID = 1;
  public static readonly THUMB_SIZE: Dimension = { width: 64, height: 36 };
  public static readonly IMAGE_NOT_FOUND: Uint8Array = new Uint8Array();
  private static readonly PATH_PREFIX = path.resolve(process.cwd()) + path.sep;

  private static readonly THUMBS: Map<string, Thumb> = ThumbControllerFactory.getInstance();
  private static readonly errors: Map<string, string> = new Map();

  public static getThumbSize(): Dimension {
    return { ...ThumbMediaController.THUMB_SIZE };
  }

  public static contains(pathKey: string): boolean {
    return ThumbMediaController.THUMBS.has(pathKey);
  }

  public static getOrCreate(
    mediaPath: string,
    lastModified: number,
    generate: (size: Dimension) => Uint8Array | null,
    save: boolean = true
  ): Thumb {
    let thumb = ThumbMediaController.THUMBS.get(mediaPath);

    if (!thumb || thumb.lastModified < lastModified || thumb.thumb == null) {
      const generated = generate(ThumbMediaController.THUMB_SIZE);
      thumb = new Thumb(generated, lastModified);
      if (thumb.thumb == null) {
        return thumb;
      }

      ThumbMediaController.THUMBS.set(mediaPath, thumb);

      if (save) {
        ThumbMediaController.save();
      }
    }

    return thumb;
  }

  public static addError(id: string): void {
    const key = "thumbError-" + id;
    const val = ThumbMediaController.errors.get(key) ?? "f";
    if (val === "f") {
      ThumbMediaController.errors.set(key, "t");
    } else if (val === "t") {
      ThumbMediaController.errors.set(key, "tt");
    }
  }

  public static checkError(id: string): boolean {
    const key = "thumbError-" + id;
    const val = ThumbMediaController.errors.get(key) ?? "f";
    return val === "tt" || val === "t";
  }

  public static extractYouTubeId(url: string): string {
    const regExp = /^.*(youtu\.be\/|v\/|u\/\w\/|embed\/|watch\?v=|\&v=)([^#\&\?]*).*/;
    const match = url.match(regExp);
    return match && match[2]?.length === 11 ? match[2] : url;
  }

  public static async loadYouTube(youtubeUrl: string): Promise<void> {
    if (!ThumbMediaController.checkError(youtubeUrl)) {
      const generate = async (size: Dimension): Promise<Uint8Array | null> => {
        try {
          const ytId = ThumbMediaController.extractYouTubeId(youtubeUrl);
          const res = await fetch(`https://img.youtube.com/vi/${ytId}/default.jpg`);
          if (!res.ok) {
            ThumbMediaController.addError(youtubeUrl);
            return null;
          }
          const buffer = await res.arrayBuffer();
          return new Uint8Array(buffer);
        } catch {
          ThumbMediaController.addError(youtubeUrl);
          return null;
        }
      };

      const bytes = await generate(ThumbMediaController.THUMB_SIZE);
      if (bytes) {
        ThumbMediaController.getOrCreate(youtubeUrl, 0, () => bytes, true);
      }
    }
  }

  public static getYouTubeBI(youtubeUrl: string): Uint8Array | null {
    return ThumbMediaController.getOrDefault(youtubeUrl, null);
  }

  public static getOrDefault(pathKey: string, def: Uint8Array | null): Uint8Array | null {
    const thumb = ThumbMediaController.THUMBS.get(pathKey);
    return thumb?.thumb ?? def;
  }

  public static getAsBufferedImage(
    pathKey: string,
    size: Dimension = ThumbMediaController.THUMB_SIZE
  ): Uint8Array | null {
    return ThumbMediaController.getOrDefault(pathKey, ThumbMediaController.IMAGE_NOT_FOUND);
  }

  public static remove(...paths: string[]): void {
    for (const p of paths) {
      ThumbMediaController.THUMBS.delete(p);
    }

    if (paths.length > 0) {
      ThumbMediaController.save();
    }
  }

  public static createIfNotExists(
    id: string,
    file: { lastModified?: number | (() => number); path?: string }
  ): void {
    if (!ThumbMediaController.contains(id)) {
      const mod =
        typeof file.lastModified === "function"
          ? file.lastModified()
          : typeof file.lastModified === "number"
          ? file.lastModified
          : Date.now();
      ThumbMediaController.getOrCreate(id, mod, () => null, false);
    }
  }

  public static save(): void {
    ThumbControllerFactory.save(ThumbMediaController.THUMBS);
  }

  public static getOrDefaultFullPath(
    currentMedia: string,
    def: Uint8Array | null
  ): Uint8Array | null {
    const relative = currentMedia.replace(ThumbMediaController.PATH_PREFIX, "");
    return ThumbMediaController.getOrDefault(relative, def);
  }

  public static load(popup?: { setProgress: (text: string) => void }): void {
    const entries = Array.from(ThumbMediaController.THUMBS.entries());
    for (let i = 0; i < entries.length; i++) {
      const percentage = Math.round(((i + 1) / (entries.length || 1)) * 100);
      popup?.setProgress?.(`Library (${percentage}%)`);
    }
  }
}

export default ThumbMediaController;
