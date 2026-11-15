import * as fs from "node:fs";
import * as path from "node:path";
import Utils from "../util/Utils.ts";

function longHashCode(value: number): number {
  const big = BigInt(value);
  const high = Number(big >> 32n);
  const low = Number(big & 0xffffffffn);
  return (high ^ low) | 0;
}

export class FileWrapper {
  public readonly path: string;

  constructor(filePath: string) {
    this.path = filePath;
  }

  public exists(): boolean {
    return fs.existsSync(this.path);
  }

  public delete(): boolean {
    try {
      if (fs.existsSync(this.path)) {
        fs.unlinkSync(this.path);
        return true;
      }
    } catch {
      return false;
    }
    return false;
  }

  public toString(): string {
    return this.path;
  }
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.VideoHL
 */
export class VideoHL {
  public static readonly serialVersionUID = 12491;

  public readonly id: number;
  public readonly fileName: string;
  public readonly hash: string;
  public readonly image: Uint8Array;
  public readonly gif: Uint8Array;
  private version: number = 1;

  constructor(
    fileName: string = "",
    hash: string = "",
    image: Uint8Array | number[] = new Uint8Array(),
    gif: Uint8Array | number[] = new Uint8Array()
  ) {
    this.id = longHashCode(Utils.generateMillisecondId());
    this.fileName = fileName;
    this.hash = hash;
    this.image = image instanceof Uint8Array ? image : new Uint8Array(image);
    this.gif = gif instanceof Uint8Array ? gif : new Uint8Array(gif);
  }

  public getFile(): FileWrapper {
    const videoPath = path.join("Holyrics", "files", "videos", this.fileName);
    return new FileWrapper(videoPath);
  }

  public getId(): string {
    return "VideoHL" + this.id;
  }

  public deleteVideo(): void {
    const file = this.getFile();
    const loop = async () => {
      while (file.exists()) {
        file.delete();
        await new Promise((resolve) => setTimeout(resolve, 3000));
      }
    };
    loop();
  }

  public equals(obj: unknown): boolean {
    if (obj instanceof VideoHL) {
      return obj.id === this.id;
    }
    return false;
  }

  public hashCode(): number {
    return 265 + this.id;
  }
}

export default VideoHL;
