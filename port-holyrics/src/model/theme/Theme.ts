import Utils from "../../util/Utils.ts";
import { Color, Font } from "../../util/awt.ts";

export interface Dimension {
  width: number;
  height: number;
}

export class BackgroundColor {
  public color: Color;

  constructor(color: Color = new Color(33, 33, 33)) {
    this.color = color;
  }
}

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.theme.Theme
 */
export class Theme {
  public static readonly serialVersionUID = 12491;

  public static readonly SIZE_DEFAULT = 0.1;
  public static readonly SIZE_MIN_VALUE = 0.04;
  public static readonly SIZE_MAX_VALUE = 0.4;
  public static readonly LINE_SPACING_DEFAULT = 0.3;
  public static readonly LINE_SPACING_MIN_VALUE = -0.5;
  public static readonly LINE_SPACING_MAX_VALUE = 1.0;
  public static readonly OUTLINE_DEFAULT = 0.0;
  public static readonly OUTLINE_MIN_VALUE = 0.0;
  public static readonly OUTLINE_MAX_VALUE = 0.1;
  public static readonly BRIGHTNESS_DEFAULT = 0.0;
  public static readonly BRIGHTNESS_MIN_VALUE = 0.0;
  public static readonly BRIGHTNESS_MAX_VALUE = 0.2;
  public static readonly SHADOW_DEFAULT = 0.0;
  public static readonly SHADOW_MIN_VALUE = -0.05;
  public static readonly SHADOW_MAX_VALUE = 0.05;
  public static readonly MARGIN_DEFAULT: number[] = [0.03, 0.03, 0.03, 0.03];
  public static readonly MARGIN_MIN_VALUE = 0.0;
  public static readonly MARGIN_MAX_VALUE = 0.9;
  public static readonly ALIGN_LEFT = -1;
  public static readonly ALIGN_CENTER = 0;
  public static readonly ALIGN_RIGHT = 1;
  public static readonly ALIGN_JUSTIFY = 2;
  public static readonly ALIGN_TOP = -1;
  public static readonly ALIGN_MIDDLE = 0;
  public static readonly ALIGN_BOTTOM = 1;

  public static readonly BLACK_SCREEN: Theme = (() => {
    const t = new Theme();
    t.background = new BackgroundColor(new Color(30, 30, 30));
    return t;
  })();

  public readonly id: number;
  public background: unknown;
  public videoRate: number;
  public readonly fonts: Font[];
  public size: number;
  public lineSpacing: number;
  public transparencyColor: Color;
  public fontColor: Color;
  public textAlign: number;
  public textAlignV: number;
  public margin: number[];
  public outlineColor: Color;
  public outlineThickness: number;
  public brightnessColor: Color;
  public brightnessThickness: number;
  public shadowColor: Color;
  public shadowX: number;
  public shadowY: number;
  public version: number = 4;

  public constructor() {
    this.id = Utils.generateMillisecondId();
    this.background = new BackgroundColor(new Color(33, 33, 33));
    this.videoRate = 1.0;
    this.fonts = new Array(300).fill(new Font("Arial", 0, 8));
    this.size = 0.1;
    this.lineSpacing = 0.3;
    this.transparencyColor = Color.WHITE;
    this.fontColor = new Color(245, 245, 245);
    this.textAlign = 0;
    this.textAlignV = 0;
    this.margin = [...Theme.MARGIN_DEFAULT];
    this.outlineColor = new Color(64, 64, 64); // DARK_GRAY
    this.outlineThickness = 0.0;
    this.brightnessColor = new Color(192, 192, 192); // LIGHT_GRAY
    this.brightnessThickness = 0.0;
    this.shadowColor = new Color(128, 128, 128); // GRAY
    this.shadowX = 0.0;
    this.shadowY = 0.0;
  }

  public static create(): Theme {
    return new Theme();
  }

  public getBackground(): unknown {
    return this.background;
  }

  public setBackground(background: unknown): this {
    this.background = background;
    return this;
  }

  public getVideoRate(): number {
    return this.videoRate;
  }

  public setVideoRate(videoRate: number): this {
    this.videoRate = Utils.range(videoRate, 0.25, 4.0);
    return this;
  }

  public getFont(): Font {
    return this.fonts[0] ?? new Font("Arial", 0, 8);
  }

  public setFont(font: Font): this {
    this.fonts[0] = font;
    return this;
  }

  public getSize(): number {
    return this.size;
  }

  public setSize(size: number): this {
    this.size = size;
    return this;
  }

  public getLineSpacing(): number {
    return this.lineSpacing;
  }

  public setLineSpacing(lineSpacing: number): this {
    this.lineSpacing = lineSpacing;
    return this;
  }

  public getFontColor(): Color {
    return this.fontColor;
  }

  public setFontColor(fontColor: Color): this {
    this.fontColor = fontColor;
    return this;
  }

  public getTransparencyColor(): Color {
    return this.transparencyColor;
  }

  public setTransparencyColor(transparencyColor: Color): this {
    this.transparencyColor = transparencyColor;
    return this;
  }

  public getTextAlign(): number {
    return this.textAlign;
  }

  public setTextAlign(textAlign: number): this {
    this.textAlign = textAlign;
    return this;
  }

  public getTextAlignV(): number {
    return this.textAlignV;
  }

  public setTextAlignV(textAlignV: number): this {
    this.textAlignV = textAlignV;
    return this;
  }

  public getMargin(): number[] {
    return [...this.margin];
  }

  public setMargin(margin: number[]): this {
    this.margin = [...margin];
    return this;
  }

  public getOutlineColor(): Color {
    return this.outlineColor;
  }

  public setOutlineColor(outlineColor: Color): this {
    this.outlineColor = outlineColor;
    return this;
  }

  public getOutlineThickness(): number {
    return this.outlineThickness;
  }

  public setOutlineThickness(outlineThickness: number): this {
    this.outlineThickness = outlineThickness;
    return this;
  }

  public getBrightnessColor(): Color {
    return this.brightnessColor;
  }

  public setBrightnessColor(brightnessColor: Color): this {
    this.brightnessColor = brightnessColor;
    return this;
  }

  public getBrightnessThickness(): number {
    return this.brightnessThickness;
  }

  public setBrightnessThickness(brightnessThickness: number): this {
    this.brightnessThickness = brightnessThickness;
    return this;
  }

  public getShadowColor(): Color {
    return this.shadowColor;
  }

  public setShadowColor(shadowColor: Color): this {
    this.shadowColor = shadowColor;
    return this;
  }

  public getShadowX(): number {
    return this.shadowX;
  }

  public setShadowX(shadowX: number): this {
    this.shadowX = shadowX;
    return this;
  }

  public getShadowY(): number {
    return this.shadowY;
  }

  public setShadowY(shadowY: number): this {
    this.shadowY = shadowY;
    return this;
  }

  public equals(obj: unknown): boolean {
    if (obj instanceof Theme) {
      return obj.id === this.id;
    }
    return false;
  }
}

export default Theme;
