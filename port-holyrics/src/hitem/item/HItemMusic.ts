import { HItem, type Dimension, type IHItem } from '../HItem';

export interface MusicModel {
  id?: number | string;
  title?: string;
  artist?: string;
  lyrics?: string;
  [key: string]: unknown;
}

export class HItemMusic extends HItem implements IHItem {
  public static readonly DEFAULT_THUMB: string = 'holyricsIcon48.png';
  public readonly id: number | string;
  private musicData: MusicModel | null = null;

  constructor(id: number | string, musicData?: MusicModel | null) {
    super();
    this.id = id;
    if (musicData) {
      this.musicData = musicData;
    }
  }

  public getInstance(): HItemMusic {
    return this;
  }

  public override getId(): string {
    return String(this.id);
  }

  public override getActionName(): string {
    return 'Exibir';
  }

  public getMusic(): MusicModel | null {
    return this.musicData;
  }

  public setMusic(music: MusicModel | null): void {
    this.musicData = music;
  }

  public override getName(): string {
    if (this.musicData) {
      const artist = this.musicData.artist ? ` - ${this.musicData.artist}` : '';
      return `${this.musicData.title || `Música ${this.id}`}${artist}`;
    }
    return `Música #${this.id}`;
  }

  public override getTitle(): string {
    return this.getName();
  }

  public override getTooltip(): string {
    if (this.musicData?.lyrics) {
      const preview = this.musicData.lyrics.slice(0, 150);
      return `${this.getName()}\n\n${preview}...`;
    }
    return this.getName();
  }

  public override favoriteAction(immediate: boolean = true): void {
    // Presentation trigger for song/lyrics
    this.run();
  }

  public override run(): void {
    // Dispatch show music event/action
  }

  public override getThumb(_size?: Dimension): string {
    return HItemMusic.DEFAULT_THUMB;
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

export default HItemMusic;
