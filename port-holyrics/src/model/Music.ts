/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.model.Music
 * and com.limagiran.holyrics.control.MusicController
 */

export interface Point {
  x: number;
  y: number;
}

export enum EnumSlideType {
  LYRICS = "LYRICS",
  TITLE = "TITLE",
  COUNTDOWN = "COUNTDOWN",
  IMAGE = "IMAGE",
  VIDEO = "VIDEO",
  TEXT = "TEXT",
}

export enum EnumParagraphIntValues {
  COUNTDOWN_FONT_TIME_SIZE = 0,
  MUSIC_AUTOMATIC_SHOW = 1,
}

export class Paragraph {
  public static readonly serialVersionUID = 1;
  public static readonly TOKEN = "##";

  private source: any = null;
  private text: string = "";
  private formatting: string = "";
  private forceFormatting: boolean = false;
  private hackBreakLine: boolean = false;
  private pageCount: Point = { x: 0, y: 0 };
  private titleAndArtist: string = "";
  private slideType: EnumSlideType = EnumSlideType.LYRICS;
  private skipPaintAddInfo: boolean = false;
  private intArray: number[] = [0, 0];
  private paintMargin: boolean = false;

  constructor();
  constructor(text: string);
  constructor(text: string, formatting: string);
  constructor(source: any, text: string, formatting: string);
  constructor(arg1?: any, arg2?: string, arg3?: string) {
    if (arguments.length === 3) {
      this.source = arg1;
      this.text = arg2 ?? "";
      this.formatting = arg3 ?? "";
    } else if (arguments.length === 2) {
      this.source = null;
      this.text = typeof arg1 === "string" ? arg1 : "";
      this.formatting = arg2 ?? "";
    } else if (arguments.length === 1) {
      this.source = null;
      this.text = typeof arg1 === "string" ? arg1 : "";
      this.formatting = "";
    } else {
      this.source = null;
      this.text = "";
      this.formatting = "";
    }
  }

  public static create(source: any = null, text: string = "", formatting: string = ""): Paragraph {
    if (typeof source === "string") {
      return new Paragraph(null, source, text);
    }
    return new Paragraph(source, text, formatting);
  }

  public getClone(): Paragraph {
    const p = new Paragraph(this.source, this.text, this.formatting);
    p.forceFormatting = this.forceFormatting;
    p.hackBreakLine = this.hackBreakLine;
    p.pageCount = { ...this.pageCount };
    p.titleAndArtist = this.titleAndArtist;
    p.slideType = this.slideType;
    p.skipPaintAddInfo = this.skipPaintAddInfo;
    p.intArray = [...this.intArray];
    p.paintMargin = this.paintMargin;
    return p;
  }

  public getLines(): string[] {
    const array = this.getText().split("\n");
    return array.length > 0 ? array : [""];
  }

  public getText(): string {
    return this.text ?? "";
  }

  public setText(text: string): this {
    this.text = text;
    return this;
  }

  public getSource(): any {
    return this.source;
  }

  public setSource(source: any): this {
    this.source = source;
    return this;
  }

  public isForceFormatting(): boolean {
    return this.forceFormatting;
  }

  public setForceFormatting(forceFormatting: boolean): this {
    this.forceFormatting = forceFormatting;
    return this;
  }

  public getFormatting(): string {
    return this.formatting ?? "";
  }

  public getFormattingToPresentation(): string {
    const format = this.getFormatting();
    const label = this.getFormattingLabel();
    if (!format.replace(label, "").toLowerCase().includes("p")) {
      return format;
    }
    return label ? `(${label})` : format;
  }

  public getFormattingToPresentationWithoutLabel(): string {
    return this.getFormattingToPresentation().replace(`(${this.getFormattingLabel()})`, "");
  }

  public setFormatting(formatting: string): this {
    this.formatting = formatting;
    return this;
  }

  public isHackBreakLine(): boolean {
    return this.hackBreakLine;
  }

  public setHackBreakLine(hackBreakLine: boolean): this {
    this.hackBreakLine = hackBreakLine;
    return this;
  }

  public getPageCount(): Point {
    return this.pageCount;
  }

  public setPageCount(indexOf: number | Point, total?: number): this {
    if (typeof indexOf === "object") {
      this.pageCount.x = indexOf.x;
      this.pageCount.y = indexOf.y;
    } else {
      this.pageCount.x = indexOf;
      this.pageCount.y = total ?? 0;
    }
    return this;
  }

  public isPageCount(): boolean {
    const p = this.getPageCount();
    return (
      (p.x > 0 && p.y > 0 && p.x <= p.y) ||
      (p.x === Number.MAX_SAFE_INTEGER && p.y === Number.MAX_SAFE_INTEGER)
    );
  }

  public getPageCountFormated(): string {
    const p = this.getPageCount();
    return p.x === Number.MAX_SAFE_INTEGER && p.y === Number.MAX_SAFE_INTEGER ? " " : `${p.x}/${p.y}`;
  }

  public getTitleAndArtist(): string {
    return this.titleAndArtist ?? "";
  }

  public setTitleAndArtist(titleAndArtist: string): this {
    this.titleAndArtist = titleAndArtist;
    return this;
  }

  public getSlideType(): EnumSlideType {
    return this.slideType;
  }

  public setSlideType(slideType: EnumSlideType): this {
    this.slideType = slideType;
    return this;
  }

  public getInt(key: EnumParagraphIntValues): number {
    const idx = key as number;
    return idx >= 0 && idx < this.intArray.length ? (this.intArray[idx] ?? 1) : 1;
  }

  public setInt(key: EnumParagraphIntValues, value: number): this {
    const idx = key as number;
    if (idx >= 0 && idx < this.intArray.length) {
      this.intArray[idx] = value;
    }
    return this;
  }

  public getBoolean(key: EnumParagraphIntValues): boolean {
    return this.getInt(key) === 1;
  }

  public setBoolean(key: EnumParagraphIntValues, value: boolean): this {
    return this.setInt(key, value ? 1 : 0);
  }

  public isSkipPaintAddInfoAndToFalse(): boolean {
    const ret = this.skipPaintAddInfo;
    this.skipPaintAddInfo = false;
    return ret;
  }

  public isSkipPaintAddInfo(): boolean {
    return this.skipPaintAddInfo;
  }

  public setSkipPaintAddInfo(skipPaintAddInfo: boolean): this {
    this.skipPaintAddInfo = skipPaintAddInfo;
    return this;
  }

  public isPaintMargin(): boolean {
    return this.paintMargin;
  }

  public setPaintMargin(paintMargin: boolean): this {
    this.paintMargin = paintMargin;
    return this;
  }

  public toString(): string {
    return this.getText();
  }

  public isFormat(): boolean {
    return this.formatting != null && this.formatting.startsWith("##");
  }

  public isUppercase(): boolean {
    return this.getFormattingToPresentationWithoutLabel().toLowerCase().includes("m");
  }

  public isBold(): boolean {
    return this.getFormattingToPresentationWithoutLabel().toLowerCase().includes("n");
  }

  public isItalic(): boolean {
    return this.getFormattingToPresentationWithoutLabel().toLowerCase().includes("i");
  }

  public getFormattingLabel(): string {
    const f = this.getFormatting();
    const idxStart = f.indexOf("(");
    if (idxStart < 0) return "";
    const idxEnd = f.lastIndexOf(")");
    return idxStart >= 0 && idxEnd >= idxStart ? f.substring(idxStart + 1, idxEnd) : "";
  }

  public getScale(baseFontSize: number): number {
    const match = this.getFormattingToPresentation().match(/-?\d+/);
    const num = match ? parseInt(match[0], 10) : 0;
    const clamped = Math.max(-90, Math.min(100, num));
    const scale = 100 + clamped;
    return Math.floor(baseFontSize * (scale / 100));
  }

  public isCountdown(): boolean {
    return this.slideType === EnumSlideType.COUNTDOWN;
  }

  public isAutomaticShow(): boolean {
    return this.getBoolean(EnumParagraphIntValues.MUSIC_AUTOMATIC_SHOW);
  }

  public hash(): string {
    const sourceHash = this.source != null && typeof this.source.hashCode === 'function' ? this.source.hashCode() : 0;
    return `${sourceHash}:${this.text}:${this.formatting}:${this.pageCount.x}:${this.pageCount.y}:${this.slideType}`;
  }
}

function removeAccent(str: string): string {
  return str.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
}

export class Music {
  public static readonly serialVersionUID = 12491;
  public static readonly KEY_OBJ = "$%#@keyOBJ*&¨123";

  private id: number;
  private artist: string;
  private title: string;
  private lyrics: string;
  private order: number[];
  private note: string;
  private language: string;
  private formatting: string;
  private formats: string[] | null = null;
  private vagalumeLink: string = "";
  private version: number = 2;

  constructor(
    id: number = 0,
    artist: string = "",
    title: string = "",
    lyrics: string = "",
    note: string = "",
    language: string = "",
    formatting: string = ""
  ) {
    this.id = id;
    this.artist = artist ?? "";
    this.title = title ?? "";
    this.lyrics = lyrics ?? "";
    this.note = note ?? "";
    this.language = language ?? "";
    this.formatting = formatting ?? "";
    this.order = [];
    if (this.formatting) {
      this.formats = this.formatting.split("\n");
    }
  }

  public static readonly BLANK: Music = new Music();

  public getInstance(): Music {
    return this;
  }

  public static create(arg: number | string = 0): Music {
    if (typeof arg === "string") {
      return Music.fromJSON(arg);
    }
    return new Music(arg, "", "", "", "", "", "");
  }

  public getSerial(): string {
    return JSON.stringify(this.toJSON());
  }

  public getId(): number {
    return this.id;
  }

  public setId(id: number): this {
    this.id = id;
    return this;
  }

  public getArtist(): string {
    return this.artist;
  }

  public setArtist(artist: string): this {
    this.artist = artist ?? "";
    return this;
  }

  public getTitle(): string {
    return this.title;
  }

  public setTitle(title: string): this {
    this.title = title ?? "";
    return this;
  }

  public getLyrics(upperCase: boolean = false): string {
    return upperCase ? this.lyrics.toUpperCase() : this.lyrics;
  }

  public setLyrics(lyrics: string): this {
    this.lyrics = lyrics ?? "";
    return this;
  }

  public getNote(): string {
    return this.note;
  }

  public setNote(note: string): this {
    this.note = note ?? "";
    return this;
  }

  public getLanguage(): string {
    return this.language;
  }

  public setLanguage(language: string): this {
    this.language = language ?? "";
    return this;
  }

  public getFormatting(): string {
    return this.formatting ?? "";
  }

  public setFormatting(formatting: string): this {
    let fmt = formatting ?? "";
    if (fmt.length > 1024) {
      fmt = fmt.substring(0, 1024);
    }
    this.formatting = fmt;
    this.formats = this.formatting.split("\n");
    return this;
  }

  public getFormat(paragraphIndex: number): string {
    try {
      if (this.formats === null) {
        this.formats = this.getFormatting().split("\n");
      }
      return paragraphIndex < this.formats.length ? (this.formats[paragraphIndex] ?? "") : "";
    } catch {
      return "";
    }
  }

  public getParagraphs(): string[] {
    const lyrics = this.getLyrics();
    if (!lyrics) return [];
    return lyrics.split("\n\n");
  }

  public getInitialParagraph(): Paragraph {
    const ps = this.getParagraphs();
    if (ps.length === 0) {
      return new Paragraph();
    }
    const p = new Paragraph(ps[0]!, this.getFormat(0));
    p.setPageCount(1, ps.length);
    return p;
  }

  public getOrder(): number[] {
    if (!this.order) {
      this.order = [];
    }
    return this.order;
  }

  public setOrder(orderList: string[]): void {
    this.getOrder().length = 0;
    const paragraphs = this.getParagraphs();
    for (const item of orderList) {
      const idx = paragraphs.indexOf(item);
      if (idx !== -1) {
        this.getOrder().push(idx);
      }
    }
  }

  public getParagraphsInOrder(): string[] {
    const list = this.getParagraphs();
    if (this.getOrder().length === 0) {
      return list;
    }
    const result: string[] = [];
    for (const order of this.getOrder()) {
      if (order >= 0 && order < list.length) {
        const item = list[order];
        if (item !== undefined) {
          result.push(item);
        }
      }
    }
    return result;
  }

  public getTitleAndArtistToShow(): string {
    return `${this.getTitle()}\n${this.getArtist()}`;
  }

  public getTitleAndArtistParagraph(): Paragraph {
    return Paragraph.create(this, `${this.getTitle()}\n${this.getArtist()}`, "")
      .setSlideType(EnumSlideType.TITLE)
      .setPageCount(Number.MAX_SAFE_INTEGER, Number.MAX_SAFE_INTEGER)
      .setTitleAndArtist(" ");
  }

  public getParagraphsToPresentation(): Paragraph[] {
    const result: Paragraph[] = [];
    const titleAndArtist = this.getTitleAndArtistToShow();
    const list = this.getParagraphs();
    const listOrder = this.getOrder();

    if (listOrder.length > 0) {
      const temp = new Map<number, Paragraph>();
      for (let i = 0; i < listOrder.length; i++) {
        const order = listOrder[i]!;
        if (order >= 0 && order < list.length) {
          let p = temp.get(order);
          if (!p) {
            p = Paragraph.create(this, list[order], this.getFormat(order))
              .setPageCount(i + 1, listOrder.length)
              .setTitleAndArtist(titleAndArtist);
            temp.set(order, p);
          }
          result.push(p);
        }
      }
      return result;
    } else {
      for (let ix = 0; ix < list.length; ix++) {
        result.push(
          Paragraph.create(this, list[ix], this.getFormat(ix))
            .setPageCount(ix + 1, list.length)
            .setTitleAndArtist(titleAndArtist)
        );
      }
      return result;
    }
  }

  public getLyricsWithFormatting(): string {
    try {
      const paragraphs = [...this.getParagraphs()];
      for (let i = 0; i < paragraphs.length; i++) {
        const formatting = this.getFormat(i);
        if (formatting.length > 0) {
          paragraphs[i] = `${formatting}\n${paragraphs[i]}`;
        }
      }
      return paragraphs.join("\n\n");
    } catch {
      return this.getLyrics();
    }
  }

  public toString(): string {
    return `${this.title} (${this.artist})`;
  }

  public getMusic(withNote: boolean = false): string {
    let sb = `Artista: ${this.artist}\n`;
    sb += `Título: ${this.title}\n`;
    if (withNote) {
      sb += `Anotação: ${this.note}\n`;
    }
    sb += `\n${this.getLyrics()}`;
    return sb;
  }

  public getInstanceToSearch(): Music {
    const clean = (s: string) =>
      removeAccent(s.toLowerCase()).replace(/[,.?!]/g, "");

    const lyricsClean = removeAccent(this.getLyrics().toLowerCase())
      .replace(/\n/g, " ")
      .replace(/[,.?!]/g, " ")
      .replace(/ {2}/g, " ");

    return Music.create(this.id)
      .setTitle(clean(this.getTitle()))
      .setArtist(clean(this.getArtist()))
      .setLyrics(lyricsClean)
      .setNote(clean(this.getNote()));
  }

  public getVagalumeLink(): string {
    return this.vagalumeLink;
  }

  public setVagalumeLink(vagalumeLink: string): this {
    this.vagalumeLink = vagalumeLink;
    return this;
  }

  public getVersion(): number {
    return this.version;
  }

  public setVersion(version: number): this {
    this.version = version;
    return this;
  }

  public equals(o: unknown): boolean {
    if (o instanceof Music) {
      return o.id === this.id;
    }
    return false;
  }

  public hashCode(): number {
    let hash = 7;
    hash = 47 * hash + (this.id ^ (this.id >>> 32));
    const artistHash = this.stringHashCode(this.artist);
    hash = 47 * hash + artistHash;
    const titleHash = this.stringHashCode(this.title);
    hash = (47 * hash + titleHash) | 0;
    return hash;
  }

  private stringHashCode(str: string): number {
    let h = 0;
    for (let i = 0; i < str.length; i++) {
      h = ((h << 5) - h + str.charCodeAt(i)) | 0;
    }
    return h;
  }

  public getHashCode(): number {
    return this.stringHashCode("com.limagiran.holyrics.model.Music");
  }

  public getExtension(): string {
    return ".muf";
  }

  public getFileDesc(): string {
    return "Music File (*.muf)";
  }

  public toJSON(): Record<string, any> {
    return {
      id: this.id,
      artist: this.artist,
      title: this.title,
      lyrics: this.lyrics,
      order: [...this.getOrder()],
      note: this.note,
      language: this.language,
      formatting: this.formatting,
      version: this.version,
    };
  }

  public static fromJSON(json: string | Record<string, any>): Music {
    const data = typeof json === "string" ? JSON.parse(json) : json;
    const music = new Music(
      data.id ?? 0,
      data.artist ?? "",
      data.title ?? "",
      data.lyrics ?? "",
      data.note ?? "",
      data.language ?? "",
      data.formatting ?? ""
    );
    if (Array.isArray(data.order)) {
      music.order = [...data.order];
    }
    if (typeof data.version === "number") {
      music.version = data.version;
    }
    return music;
  }
}

export default Music;
