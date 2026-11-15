import { HItem, type Dimension, type IHItem } from '../HItem';

export interface EventScheduleModel {
  id?: number | string;
  name?: string;
  title?: string;
  date?: string | Date;
  description?: string;
  items?: IHItem[];
  [key: string]: unknown;
}

export class HItemEvent extends HItem implements IHItem {
  public static readonly DEFAULT_THUMB: string = 'event16.png';
  public readonly id: number | string;
  public name: string;
  public date?: string | Date;
  public items: IHItem[] = [];
  private eventData: EventScheduleModel | null = null;

  constructor(
    id: number | string,
    name?: string,
    items: IHItem[] = [],
    eventData?: EventScheduleModel | null
  ) {
    super();
    this.id = id;
    this.name = name || `Culto / Evento ${id}`;
    this.items = items;
    if (eventData) {
      this.eventData = eventData;
      if (eventData.name) this.name = eventData.name;
      if (eventData.date) this.date = eventData.date;
      if (eventData.items) this.items = eventData.items;
    }
  }

  public getInstance(): HItemEvent {
    return this;
  }

  public override getId(): string {
    return String(this.id);
  }

  public override getActionName(): string {
    return 'Abrir';
  }

  public getEventData(): EventScheduleModel | null {
    return this.eventData;
  }

  public setEventData(eventData: EventScheduleModel | null): void {
    this.eventData = eventData;
    if (eventData?.name) this.name = eventData.name;
    if (eventData?.date) this.date = eventData.date;
    if (eventData?.items) this.items = eventData.items;
  }

  public override getName(): string {
    return this.name;
  }

  public override getTitle(): string {
    return this.getName();
  }

  public override getTooltip(): string {
    const dateStr = this.date ? `\nData: ${String(this.date)}` : '';
    const itemsCount = this.items.length > 0 ? `\nItens: ${this.items.length}` : '';
    return `${this.getName()}${dateStr}${itemsCount}`;
  }

  public override getSubItems(): IHItem[] {
    return this.items;
  }

  public addItem(item: IHItem): void {
    this.items.push(item);
  }

  public removeItem(index: number): void {
    if (index >= 0 && index < this.items.length) {
      this.items.splice(index, 1);
    }
  }

  public override favoriteAction(immediate: boolean = true): void {
    this.run();
  }

  public override run(): void {
    // Open/present event schedule
  }

  public override getThumb(_size?: Dimension): string {
    return HItemEvent.DEFAULT_THUMB;
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
}

export default HItemEvent;
