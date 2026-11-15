import Utils from "../util/Utils.ts";
import { Color } from "../util/awt.ts";
import { Presentation } from "./Presentation.ts";
import { Paragraph } from "./Music.ts";

export interface I18NValue {
  val(): string;
  toString(): string;
}

export const I18N = {
  MSG_ERROR_CORRUPTED_FILES: {
    val: () => "Arquivos corrompidos ou ausentes.",
    toString: () => "Arquivos corrompidos ou ausentes.",
  },
};

export const AnnouncementController = {
  getAll(): Announcement[] {
    return [];
  },
};

export interface Background {
  [key: string]: any;
}

export class BackgroundAnnouncement implements Background {
  public static readonly KEY = "#hly$k3y#";
}

export class ThemeAnnouncement {
  private background: Background | null = null;
  private fontColor: Color = new Color(30, 30, 30);

  public static create(): ThemeAnnouncement {
    return new ThemeAnnouncement();
  }

  public setBackground(bg: Background | null): this {
    this.background = bg;
    return this;
  }

  public getBackground(): Background | null {
    return this.background;
  }

  public setFontColor(color: Color): this {
    this.fontColor = color;
    return this;
  }

  public getFontColor(): Color {
    return this.fontColor;
  }

  public hash(): string {
    return `ThemeAnnouncement:${this.fontColor.toRGB()}`;
  }
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.Announcement
 */
export class Announcement {
  public static readonly serialVersionUID = 12491;
  private static DEFAULT_BACKGROUND: BackgroundAnnouncement | null = Announcement.createDefaultBackground();

  public readonly id: number;
  private name: string;
  private text: string;
  private theme: ThemeAnnouncement;
  private version: number = 1;

  public static load(): void {
    AnnouncementController.getAll();
  }

  constructor() {
    this.id = Utils.generateMillisecondId();
    this.name = "";
    this.text = "";
    this.theme = ThemeAnnouncement.create()
      .setBackground(Announcement.getDefaultBG())
      .setFontColor(new Color(30, 30, 30));
  }

  public getName(): string {
    return this.name;
  }

  public setName(name: string): void {
    this.name = name;
  }

  public getText(): string {
    return this.text;
  }

  public setText(text: string): void {
    this.text = text;
  }

  public getTheme(): ThemeAnnouncement {
    return this.theme;
  }

  public setTheme(theme: ThemeAnnouncement): void {
    this.theme = theme;
  }

  public createPresentation(): Presentation {
    const paragraph = Paragraph.create(this, this.getText(), "");
    return Presentation.create(paragraph, this.getTheme());
  }

  public toString(): string {
    return this.name.padEnd(8, " ");
  }

  private static createDefaultBackground(): BackgroundAnnouncement | null {
    try {
      return new BackgroundAnnouncement();
    } catch {
      return null;
    }
  }

  public equals(obj: unknown): boolean {
    return obj instanceof Announcement ? obj.id === this.id : false;
  }

  public hashCode(): number {
    return (291 + ((this.id ^ (this.id >>> 32)) | 0)) | 0;
  }

  private static getDefaultBG(): Background | null {
    return Announcement.DEFAULT_BACKGROUND;
  }
}

export default Announcement;
