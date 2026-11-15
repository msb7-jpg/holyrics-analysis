export interface Dimension {
  width: number;
  height: number;
}

export interface IHItem {
  id?: string | number;
  getId(): string;
  getName(): string;
  getTitle?(): string;
  getAsFile(): string | null;
  getActionName?(): string;
  getThumb(size?: Dimension): string | null;
  loadThumbAsIcon?(size?: Dimension): string | null;
  loadDefaultThumbAsIcon?(): string | null;
  getFavoriteThumb?(size?: Dimension): string | null;
  getIcon?(): string | null;
  favoriteAction?(immediate?: boolean): void;
  run?(): void;
  listPathIfDirectory?(): string[];
  getTooltip?(): string;
  getSubItems?(): IHItem[];
}

export abstract class HItem implements IHItem {
  abstract getId(): string;
  abstract getName(): string;

  getTitle(): string {
    return this.getName();
  }

  getAsFile(): string | null {
    return null;
  }

  getActionName(): string {
    return 'Exibir';
  }

  getThumb(_size?: Dimension): string | null {
    return null;
  }

  loadThumbAsIcon(size?: Dimension): string | null {
    return this.getThumb(size);
  }

  loadDefaultThumbAsIcon(): string | null {
    return this.loadThumbAsIcon({ width: 64, height: 36 });
  }

  getFavoriteThumb(size?: Dimension): string | null {
    return this.getThumb(size);
  }

  getIcon(): string | null {
    return this.getFavoriteThumb({ width: 64, height: 36 });
  }

  favoriteAction(_immediate: boolean = true): void {
    this.run();
  }

  run(): void {
    // Default action execution
  }

  listPathIfDirectory(): string[] {
    return [];
  }

  getTooltip(): string {
    return this.getName();
  }

  getSubItems(): IHItem[] {
    return [];
  }
}

export default HItem;
