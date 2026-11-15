/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.ScreenDeviceSettings
 */

import { Margin, Rectangle } from "./Margin";
import type { Dimension, RectangleLike } from "./Margin";
import { Color, Font } from "./ClockSettings";

export { Margin, Rectangle, Color, Font };
export type { Dimension, RectangleLike };

export const Utils = {
  range(value: number, min: number, max: number): number {
    return Math.max(min, Math.min(max, value));
  },
};

export enum EnumDeviceType {
  MUSIC = "MUSIC",
  TEXT = "TEXT",
  IMAGE = "IMAGE",
  BIBLE = "BIBLE",
  ANNOUNCEMENT = "ANNOUNCEMENT",
  MEDIA = "MEDIA",
}

export namespace EnumDeviceType {
  export function getBackgroundColor(type: EnumDeviceType): Color {
    switch (type) {
      case EnumDeviceType.MUSIC:
      case EnumDeviceType.TEXT:
      case EnumDeviceType.BIBLE:
        return new Color(0, 0, 0, 0);
      default:
        return Color.BLACK;
    }
  }

  export function isMusicTextOrBible(type: EnumDeviceType): boolean {
    switch (type) {
      case EnumDeviceType.MUSIC:
      case EnumDeviceType.TEXT:
      case EnumDeviceType.BIBLE:
        return true;
      default:
        return false;
    }
  }
}

export class StageViewSettings {
  public static readonly serialVersionUID = 1;

  private enabled: boolean = false;
  private id: number = 0;
  private upperCase: boolean = false;
  private removeLineBreak: boolean = false;
  private themeEnabled: boolean = false;
  private bibleEnabled: boolean = true;
  private themeId: number = -1;
  private version: number = 1;

  constructor() {
    this.enabled = false;
    this.id = 0;
    this.upperCase = false;
    this.removeLineBreak = false;
    this.themeEnabled = false;
    this.bibleEnabled = true;
    this.themeId = -1;
  }

  public isEnabled(): boolean {
    return this.enabled;
  }

  public setEnabled(enabled: boolean): void {
    this.enabled = enabled;
  }

  public getId(): number {
    return this.id;
  }

  public setId(id: number): void {
    this.id = id;
  }

  public isUpperCase(): boolean {
    return this.upperCase;
  }

  public setUpperCase(upperCase: boolean): void {
    this.upperCase = upperCase;
  }

  public isRemoveLineBreak(): boolean {
    return this.removeLineBreak;
  }

  public setRemoveLineBreak(removeLineBreak: boolean): void {
    this.removeLineBreak = removeLineBreak;
  }

  public isThemeEnabled(): boolean {
    return this.themeEnabled;
  }

  public setThemeEnabled(themeEnabled: boolean): void {
    this.themeEnabled = themeEnabled;
  }

  public isBibleEnabled(): boolean {
    return this.bibleEnabled;
  }

  public setBibleEnabled(bibleEnabled: boolean): void {
    this.bibleEnabled = bibleEnabled;
  }

  public getThemeId(): number {
    return this.themeId;
  }

  public setThemeId(themeId: number): void {
    this.themeId = themeId;
  }

  public toString(): string {
    return `[enabled=${this.enabled};type=${this.id};themeEnabled=${this.themeEnabled};bibleEnabled=${this.bibleEnabled};themeId=${this.themeId}]`;
  }

  public hashKey(): string {
    return `:${this.enabled}:${this.id}:${this.upperCase}:${this.removeLineBreak}:${this.themeEnabled}:${this.bibleEnabled}:${this.themeId}`;
  }
}

export interface GraphicsDeviceLike {
  getDefaultConfiguration(): {
    getBounds(): RectangleLike;
  };
}

export class GraphicsDeviceCustom {
  public static readonly serialVersionUID = 1;

  private sds?: ScreenDeviceSettings;
  private view?: unknown;
  public readonly device: GraphicsDeviceLike | unknown;
  public readonly deviceType: EnumDeviceType;
  public readonly index: number;
  private version: number = 1;

  constructor(device: GraphicsDeviceLike | unknown, deviceType: EnumDeviceType, index: number) {
    this.device = device;
    this.deviceType = deviceType;
    this.index = index;
  }

  public getScreenDeviceSettings(): ScreenDeviceSettings {
    if (!this.sds) {
      this.sds = new ScreenDeviceSettings();
      this.sds.setIndex(this.index);
    }
    return this.sds;
  }

  public getView(): unknown {
    return this.view;
  }

  public setView(view: unknown): void {
    this.view = view;
  }

  public getBounds(): Rectangle {
    return GraphicsDeviceCustom.getBounds(this.device, this.getScreenDeviceSettings());
  }

  public static getBounds(device: any, sds: ScreenDeviceSettings): Rectangle {
    const rawBounds = device?.getDefaultConfiguration?.()?.getBounds?.() ?? { x: 0, y: 0, width: 1920, height: 1080 };
    const size: Dimension = { width: rawBounds.width, height: rawBounds.height };
    const screenPosition = sds.getMargin().getRectWithMargin(size);
    return new Rectangle(
      rawBounds.x + screenPosition.x,
      rawBounds.y + screenPosition.y,
      screenPosition.width,
      screenPosition.height
    );
  }
}

export class ScreenDeviceSettings {
  public static readonly serialVersionUID = 12491;
  public static readonly STREAM_ID = -1;
  public static readonly PRIMARY_ID = 1;
  public static readonly ADDITIONAL_INFO_LINE_HTIGHT_MIN = 4;
  public static readonly ADDITIONAL_INFO_LINE_HEIGHT_MIN = 4;
  public static readonly ADDITIONAL_INFO_LINE_HTIGHT_MAX = 15;
  public static readonly ADDITIONAL_INFO_LINE_HEIGHT_MAX = 15;

  public static readonly StageViewSettings = StageViewSettings;
  public static readonly GraphicsDeviceCustom = GraphicsDeviceCustom;
  public static readonly EnumDeviceType = EnumDeviceType;

  private index: number = 0;
  private stageViewSettings: StageViewSettings;
  private showPageCount: boolean = false;
  private showTitleAndArtistAllSlides: boolean = false;
  private showTitleAndArtistAllSlidesPosition: number = 5;
  private showPageCountPosition: number = 5;
  private additionalInfoLineHeight: number = 7;
  private additionalInfoFont: string | null = null;
  private additionalInfoBold: boolean | null = null;
  private additionalInfoItalic: boolean | null = null;
  private additionalInfoColor: Color | null = null;
  private additionalInfoTwoLines: boolean = false;
  private additionalInfoPaintEffects: boolean = true;
  private margin: Margin;
  private version: number = 3;

  constructor() {
    this.index = 0;
    this.stageViewSettings = new StageViewSettings();
    this.showPageCount = false;
    this.showTitleAndArtistAllSlides = false;
    this.showTitleAndArtistAllSlidesPosition = 5;
    this.showPageCountPosition = 5;
    this.additionalInfoLineHeight = 7;
    this.additionalInfoFont = null;
    this.additionalInfoBold = null;
    this.additionalInfoItalic = null;
    this.additionalInfoColor = null;
    this.additionalInfoTwoLines = false;
    this.additionalInfoPaintEffects = true;
    this.margin = new Margin();
  }

  public getIndex(): number {
    return this.index;
  }

  public setIndex(index: number): void {
    this.index = index;
  }

  public getStageViewSettings(): StageViewSettings {
    if (!this.stageViewSettings) {
      this.stageViewSettings = new StageViewSettings();
    }
    return this.stageViewSettings;
  }

  public setStageViewSettings(stageViewSettings: StageViewSettings): void {
    this.stageViewSettings = stageViewSettings;
  }

  public isShowPageCount(): boolean {
    return this.showPageCount;
  }

  public setShowPageCount(showPageCount: boolean): void {
    this.showPageCount = showPageCount;
  }

  public isShowTitleAndArtistAllSlides(): boolean {
    return this.showTitleAndArtistAllSlides;
  }

  public setShowTitleAndArtistAllSlides(showTitleAndArtistAllSlides: boolean): void {
    this.showTitleAndArtistAllSlides = showTitleAndArtistAllSlides;
  }

  public getShowTitleAndArtistAllSlidesPosition(): number {
    return this.showTitleAndArtistAllSlidesPosition;
  }

  public setShowTitleAndArtistAllSlidesPosition(showTitleAndArtistAllSlidesPosition: number): void {
    this.showTitleAndArtistAllSlidesPosition = Utils.range(showTitleAndArtistAllSlidesPosition, 0, 5);
  }

  public getShowPageCountPosition(): number {
    return this.showPageCountPosition;
  }

  public setShowPageCountPosition(showPageCountPosition: number): void {
    this.showPageCountPosition = Utils.range(showPageCountPosition, 0, 5);
  }

  public getAdditionalInfoLineHeight(): number {
    return this.additionalInfoLineHeight;
  }

  public setAdditionalInfoLineHeight(additionalInfoLineHeight: number): void {
    this.additionalInfoLineHeight = Utils.range(
      additionalInfoLineHeight,
      ScreenDeviceSettings.ADDITIONAL_INFO_LINE_HEIGHT_MIN,
      ScreenDeviceSettings.ADDITIONAL_INFO_LINE_HEIGHT_MAX
    );
  }

  public getAdditionalInfoLineHeightTo(height: number): number {
    return Math.max(Math.floor(height * (this.getAdditionalInfoLineHeight() / 100.0)), 3);
  }

  public getAdditionalInfoFont(): string | null {
    return this.additionalInfoFont;
  }

  public setAdditionalInfoFont(additionalInfoFont: string | null): void {
    this.additionalInfoFont = additionalInfoFont;
  }

  public getAdditionalInfoBold(): boolean | null {
    return this.additionalInfoBold;
  }

  public setAdditionalInfoBold(additionalInfoBold: boolean | null): void {
    this.additionalInfoBold = additionalInfoBold;
  }

  public getAdditionalInfoItalic(): boolean | null {
    return this.additionalInfoItalic;
  }

  public setAdditionalInfoItalic(additionalInfoItalic: boolean | null): void {
    this.additionalInfoItalic = additionalInfoItalic;
  }

  public getAdditionalInfoColor(): Color | null {
    return this.additionalInfoColor;
  }

  public setAdditionalInfoColor(additionalInfoColor: Color | null): void {
    this.additionalInfoColor = additionalInfoColor;
  }

  public isAdditionalInfoTwoLines(): boolean {
    return this.additionalInfoTwoLines;
  }

  public setAdditionalInfoTwoLines(additionalInfoTwoLines: boolean): void {
    this.additionalInfoTwoLines = additionalInfoTwoLines;
  }

  public isAdditionalInfoPaintEffects(): boolean {
    return this.additionalInfoPaintEffects;
  }

  public setAdditionalInfoPaintEffects(additionalInfoPaintEffects: boolean): void {
    this.additionalInfoPaintEffects = additionalInfoPaintEffects;
  }

  public getMargin(): Margin {
    if (!this.margin) {
      this.margin = new Margin();
    }
    return this.margin;
  }

  public translatePageCount(rect: RectangleLike, height: number): void {
    rect.height -= height;
    if (this.isPageCountPositionTop()) {
      rect.y += height;
    }
  }

  public translateTitleAndArtist(rect: RectangleLike, height: number): void {
    rect.height -= height;
    if (this.isTitleAndArtistPositionTop()) {
      rect.y += height;
    }
  }

  public translateAddInfo(rect: RectangleLike, height: number): void {
    if (this.isShowPageCount() && this.isShowTitleAndArtistAllSlides()) {
      this.translatePageCount(rect, height);
    } else {
      if (this.isShowPageCount()) {
        this.translatePageCount(rect, height);
      }
      if (this.isShowTitleAndArtistAllSlides()) {
        this.translateTitleAndArtist(rect, height);
      }
    }
  }

  public getName(): string {
    switch (this.index) {
      case -1:
        return "Streaming App";
      case 1:
        return "Screen (Public)";
      default:
        return "Screen " + this.index;
    }
  }

  public isPrimary(): boolean {
    return this.index === 1;
  }

  public isStream(): boolean {
    return this.index === -1;
  }

  public isPageCountPositionTop(): boolean {
    return ScreenDeviceSettings.isTop(this.showPageCountPosition);
  }

  public isTitleAndArtistPositionTop(): boolean {
    return ScreenDeviceSettings.isTop(this.showTitleAndArtistAllSlidesPosition);
  }

  public static isTop(position: number): boolean {
    return position === 0 || position === 1 || position === 2;
  }

  public static isLeft(position: number): boolean {
    return position === 0 || position === 3;
  }

  public static isCenter(position: number): boolean {
    return position === 1 || position === 4;
  }

  public static isRight(position: number): boolean {
    return position === 2 || position === 5;
  }

  public getColor(_default: Color): Color {
    return this.additionalInfoColor !== null ? this.additionalInfoColor : _default;
  }

  public getFont(_default: Font): Font;
  public getFont(defaultName: string, size: number, defaultBold: boolean, defaultItalic: boolean): Font;
  public getFont(
    arg0: Font | string,
    size?: number,
    defaultBold?: boolean,
    defaultItalic?: boolean
  ): Font {
    if (typeof arg0 === "object" && arg0 instanceof Font) {
      return this.getFont(arg0.getName(), arg0.getSize(), arg0.isBold(), arg0.isItalic());
    }

    const defaultName = arg0;
    const name = this.getAdditionalInfoFont() ?? defaultName;
    const bold = this.getAdditionalInfoBold() ?? (defaultBold ?? false);
    const italic = this.getAdditionalInfoItalic() ?? (defaultItalic ?? false);

    let style = 0;
    if (bold) style |= 1;
    if (italic) style |= 2;

    return new Font(name, style, size ?? 12);
  }

  public hash(): string {
    return !this.showPageCount && !this.showTitleAndArtistAllSlides
      ? ":false:"
      : `${this.showPageCount}:${this.showTitleAndArtistAllSlides}:${this.showPageCountPosition}:${this.showTitleAndArtistAllSlidesPosition}:${this.additionalInfoLineHeight}:${this.additionalInfoFont}:${this.additionalInfoBold}:${this.additionalInfoColor}:${this.additionalInfoTwoLines}:${this.additionalInfoPaintEffects}:${this.additionalInfoItalic}:${this.margin}`;
  }
}

export default ScreenDeviceSettings;
