import { HItem, type Dimension, type IHItem } from '../HItem';

export interface AnnouncementModel {
  id?: number | string;
  name?: string;
  title?: string;
  description?: string;
  images?: string[];
  [key: string]: unknown;
}

export class HItemAnnouncement extends HItem implements IHItem {
  public static readonly DEFAULT_THUMB: string = 'announcement16.png';
  public readonly id: number | string;
  private announcementData: AnnouncementModel | null = null;

  constructor(id: number | string, announcementData?: AnnouncementModel | null) {
    super();
    this.id = id;
    if (announcementData) {
      this.announcementData = announcementData;
    }
  }

  public getInstance(): HItemAnnouncement {
    return this;
  }

  public override getId(): string {
    return String(this.id);
  }

  public override getActionName(): string {
    return 'Exibir';
  }

  public getAnnouncement(): AnnouncementModel | null {
    return this.announcementData;
  }

  public setAnnouncement(announcement: AnnouncementModel | null): void {
    this.announcementData = announcement;
  }

  public override getName(): string {
    if (this.announcementData) {
      return this.announcementData.name || this.announcementData.title || `Aviso ${this.id}`;
    }
    return `Aviso #${this.id}`;
  }

  public override getTitle(): string {
    return this.getName();
  }

  public override getTooltip(): string {
    if (this.announcementData?.description) {
      return `${this.getName()}\n${this.announcementData.description}`;
    }
    return this.getName();
  }

  public override favoriteAction(immediate: boolean = true): void {
    this.run();
  }

  public override run(): void {
    // Dispatch announcement presentation action
  }

  public override getThumb(_size?: Dimension): string {
    return HItemAnnouncement.DEFAULT_THUMB;
  }

  public override getFavoriteThumb(size?: Dimension): string {
    return this.getThumb(size);
  }

  public override loadThumbAsIcon(size?: Dimension): string {
    return this.getThumb(size);
  }

  public override getAsFile(): string | null {
    return null;
  }

  public override listPathIfDirectory(): string[] {
    return [];
  }

  public override getSubItems(): IHItem[] {
    return [];
  }
}

export default HItemAnnouncement;
