import * as fs from "node:fs";
import Utils from "../util/Utils.ts";

export interface Dimension {
  width: number;
  height: number;
}

function longHashCode(value: number): number {
  const big = BigInt(value);
  const high = Number(big >> 32n);
  const low = Number(big & 0xffffffffn);
  return (high ^ low) | 0;
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.ImageHL
 */
export class ImageHL {
  public static readonly serialVersionUID = 12491;

  public readonly id: number;
  public readonly bytes: Uint8Array;
  public readonly bytesThumb: Uint8Array;
  private version: number = 1;

  constructor(bytes: Uint8Array | number[] = new Uint8Array(), bytesThumb: Uint8Array | number[] = new Uint8Array()) {
    this.id = longHashCode(Utils.generateMillisecondId());
    this.bytes = bytes instanceof Uint8Array ? bytes : new Uint8Array(bytes);
    this.bytesThumb = bytesThumb instanceof Uint8Array ? bytesThumb : new Uint8Array(bytesThumb);
  }

  public static async create(
    source: string | Uint8Array | Buffer,
    thumbsSize?: Dimension
  ): Promise<ImageHL> {
    let bytes: Uint8Array;
    if (typeof source === "string") {
      bytes = new Uint8Array(fs.readFileSync(source));
    } else if (source instanceof Uint8Array) {
      bytes = source;
    } else {
      bytes = new Uint8Array(source);
    }

    // Default bytesThumb to bytes or resized thumbnail
    const bytesThumb = bytes;
    return new ImageHL(bytes, bytesThumb);
  }

  public getId(): string {
    return "ImageHL" + this.id;
  }

  public equals(obj: unknown): boolean {
    if (obj instanceof ImageHL) {
      return obj.id === this.id;
    }
    return false;
  }

  public hashCode(): number {
    return 85 + this.id;
  }
}

export default ImageHL;
