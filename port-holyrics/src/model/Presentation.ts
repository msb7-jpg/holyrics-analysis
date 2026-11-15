import { Paragraph } from "./Music.ts";

export interface ITheme {
  hash(): string;
  [key: string]: any;
}

export class Theme implements ITheme {
  public static readonly BLACK_SCREEN: Theme = new Theme("BLACK_SCREEN");

  private readonly name: string;

  constructor(name: string = "DEFAULT") {
    this.name = name;
  }

  public hash(): string {
    return `Theme:${this.name}`;
  }
}

export const WallpaperView = {
  WALLPAPER_THEME: new Theme("WALLPAPER"),
};

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.Presentation
 */
export class Presentation {
  public static readonly serialVersionUID = 1;
  public static readonly BLACK_SCREEN: Presentation = Presentation.create(
    new Paragraph(),
    Theme.BLACK_SCREEN,
    false
  );

  public readonly text: Paragraph;
  public readonly theme: Theme | ITheme;
  public readonly separatorOrEnd: boolean;
  private version: number = 1;

  private constructor(text: Paragraph, theme: Theme | ITheme, separatorOrEnd: boolean = false) {
    this.text = text;
    this.theme = theme;
    this.separatorOrEnd = separatorOrEnd;
  }

  public static create(text: Paragraph, theme: Theme | ITheme, separatorOrEnd: boolean = false): Presentation {
    return new Presentation(text, theme, separatorOrEnd);
  }

  public blank(): Presentation {
    return Presentation.create(new Paragraph(), this.theme, false);
  }

  public wallpaper(): Presentation {
    return Presentation.create(new Paragraph(), WallpaperView.WALLPAPER_THEME, false);
  }

  public hash(): string {
    const textHash = typeof this.text?.hash === "function" ? this.text.hash() : "";
    const themeHash = typeof this.theme?.hash === "function" ? this.theme.hash() : "";
    return `${textHash}:${themeHash}:${this.separatorOrEnd}`;
  }
}

export default Presentation;
