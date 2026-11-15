import * as fs from "node:fs";
import * as path from "node:path";

/**
 * Interface and utility for saving/loading controller collections.
 * Isomorphic TypeScript port of com.limagiran.holyrics.control.ControllerFactory
 */
export class ControllerFactory {
  public static readonly PASSWORD = "&p4ssS3sp";
  private static basePath: string = path.resolve(process.cwd(), "Holyrics", "files");
  private static inMemoryStore: Map<string, any[]> = new Map();
  private static useMemoryOnly: boolean = false;

  public static setBasePath(customPath: string): void {
    this.basePath = customPath;
  }

  public static getBasePath(): string {
    return this.basePath;
  }

  public static setUseMemoryOnly(memoryOnly: boolean): void {
    this.useMemoryOnly = memoryOnly;
  }

  public static clearMemory(): void {
    this.inMemoryStore.clear();
  }

  public static getFile(name: string): string {
    const filePath = path.resolve(this.basePath, name);
    const dir = path.dirname(filePath);
    if (!this.useMemoryOnly) {
      try {
        if (!fs.existsSync(dir)) {
          fs.mkdirSync(dir, { recursive: true });
        }
      } catch {
        // Fallback for sandboxed / read-only filesystems
      }
    }
    return filePath;
  }

  public static load<T>(
    fileName: string,
    reviver?: (item: any) => T,
    preLoad?: () => void
  ): T[] {
    if (preLoad) {
      try {
        preLoad();
      } catch {
        // ignore preload errors
      }
    }

    if (this.useMemoryOnly || this.inMemoryStore.has(fileName)) {
      const items = this.inMemoryStore.get(fileName) || [];
      return reviver ? items.map(reviver) : [...items];
    }

    try {
      const filePath = this.getFile(fileName);
      if (fs.existsSync(filePath)) {
        const content = fs.readFileSync(filePath, "utf-8");
        const parsed = JSON.parse(content);
        if (Array.isArray(parsed)) {
          const list = reviver ? parsed.map(reviver) : (parsed as T[]);
          this.inMemoryStore.set(fileName, list);
          return [...list];
        }
      }
    } catch {
      // Return empty list on parse/read error
    }

    return [];
  }

  public static save<T>(list: T[], fileName: string): boolean {
    this.inMemoryStore.set(fileName, [...list]);

    if (this.useMemoryOnly) {
      return true;
    }

    try {
      const filePath = this.getFile(fileName);
      const json = JSON.stringify(list, null, 2);
      fs.writeFileSync(filePath, json, "utf-8");
      return true;
    } catch {
      return false;
    }
  }

  public static fileSize(fileName: string): number {
    if (this.useMemoryOnly) {
      const items = this.inMemoryStore.get(fileName);
      return items ? JSON.stringify(items).length : 0;
    }

    try {
      const filePath = this.getFile(fileName);
      if (fs.existsSync(filePath)) {
        const stats = fs.statSync(filePath);
        return stats.size;
      }
    } catch {
      // ignore error
    }
    return 0;
  }
}

export default ControllerFactory;
