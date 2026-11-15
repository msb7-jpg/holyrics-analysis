import type { Dimension } from "./G2Utils";

export interface HolyricsStreamRenderer {
  renderer(size: Dimension): string | null;
  getHash(size: Dimension): string;
}

export interface StreamingPack {
  success: boolean;
  image: string;
  update: string;
}

export class HolyricsStreaming {
  public static readonly SIZE_WIDESCREEN: Dimension = { width: 1920, height: 1080 };
  public static readonly SIZE_STANDARD: Dimension = { width: 1200, height: 900 };
  public static readonly SIZES: Dimension[] = [
    HolyricsStreaming.SIZE_WIDESCREEN,
    HolyricsStreaming.SIZE_STANDARD,
  ];

  // Minimal 1x1 black pixel base64 or placeholder default streaming
  public static readonly DEFAULT_STREAMING: string[] = [
    HolyricsStreaming.createDefaultStreaming(true),
    HolyricsStreaming.createDefaultStreaming(false),
  ];

  private static STREAMING: (string | null)[] = [null, null];
  private static STREAMING_HASH: (string | null)[] = [null, null];
  private static DEMAND: boolean[] = [false, false];
  private static RENDERER: HolyricsStreamRenderer | null = null;
  private static UPDATE: number = 0;

  private static getIndex(widescreen: boolean): number {
    return widescreen ? 0 : 1;
  }

  public static setRenderer(renderer: HolyricsStreamRenderer | null): void {
    HolyricsStreaming.RENDERER = renderer;
  }

  public static getRenderer(): HolyricsStreamRenderer | null {
    return HolyricsStreaming.RENDERER;
  }

  public static request(widescreen: boolean): void {
    HolyricsStreaming.DEMAND[HolyricsStreaming.getIndex(widescreen)] = true;
  }

  public static isDemand(widescreen?: boolean): boolean {
    if (widescreen !== undefined) {
      return HolyricsStreaming.DEMAND[HolyricsStreaming.getIndex(widescreen)] ?? false;
    }
    return (HolyricsStreaming.DEMAND[0] ?? false) || (HolyricsStreaming.DEMAND[1] ?? false);
  }

  public static isDemandWidescreen(): boolean {
    return HolyricsStreaming.DEMAND[0] ?? false;
  }

  public static isDemandStandard(): boolean {
    return HolyricsStreaming.DEMAND[1] ?? false;
  }

  public static clear(): void {
    HolyricsStreaming.setRenderer(null);
    HolyricsStreaming.STREAMING = [null, null];
    HolyricsStreaming.STREAMING_HASH = [null, null];
    HolyricsStreaming.UPDATE = Date.now();
  }

  public static getUpdate(): string {
    return String(HolyricsStreaming.UPDATE);
  }

  public static getStreamingPack(widescreen: boolean): StreamingPack {
    const index = HolyricsStreaming.getIndex(widescreen);
    const img = HolyricsStreaming.STREAMING[index];
    const defaultImg = HolyricsStreaming.DEFAULT_STREAMING[index] ?? "";
    return {
      success: true,
      image: img !== null && img !== undefined ? img : defaultImg,
      update: HolyricsStreaming.getUpdate(),
    };
  }

  public static updateStreaming(renderer: HolyricsStreamRenderer | null = HolyricsStreaming.RENDERER): void {
    for (let i = 0; i < HolyricsStreaming.SIZES.length; i++) {
      try {
        if (HolyricsStreaming.isDemand(i === 0)) {
          if (!renderer) {
            HolyricsStreaming.STREAMING[i] = null;
            HolyricsStreaming.STREAMING_HASH[i] = null;
            HolyricsStreaming.UPDATE = 1;
          } else {
            const size = HolyricsStreaming.SIZES[i];
            if (!size) continue;
            const hash = renderer.getHash(size);
            if (HolyricsStreaming.STREAMING_HASH[i] !== hash) {
              HolyricsStreaming.STREAMING_HASH[i] = hash;
              const encoded = renderer.renderer(size);
              HolyricsStreaming.STREAMING[i] = encoded;
              HolyricsStreaming.UPDATE = Date.now();
            }
          }
        }
      } catch {
        HolyricsStreaming.STREAMING[i] = null;
        HolyricsStreaming.STREAMING_HASH[i] = null;
      }
    }
  }

  public static setStreamingImage(widescreen: boolean, base64Image: string, hash?: string): void {
    const index = HolyricsStreaming.getIndex(widescreen);
    HolyricsStreaming.STREAMING[index] = base64Image;
    HolyricsStreaming.STREAMING_HASH[index] = hash ?? String(Date.now());
    HolyricsStreaming.UPDATE = Date.now();
  }

  private static createDefaultStreaming(widescreen: boolean): string {
    // 1x1 png blank dark frame base64 fallback
    return "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mNk+A8AAQUBAScY42YAAAAASUVORK5CYII=";
  }
}

export default HolyricsStreaming;
