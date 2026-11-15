export class Color {
  public static readonly BLACK = new Color(0, 0, 0);
  public static readonly WHITE = new Color(255, 255, 255);

  private readonly r: number;
  private readonly g: number;
  private readonly b: number;
  private readonly a: number;

  constructor(r: number, g: number, b: number, a: number = 255) {
    this.r = r;
    this.g = g;
    this.b = b;
    this.a = a;
  }

  public getRed(): number {
    return this.r;
  }

  public getGreen(): number {
    return this.g;
  }

  public getBlue(): number {
    return this.b;
  }

  public getAlpha(): number {
    return this.a;
  }

  public toRGB(): string {
    return `rgb(${this.r}, ${this.g}, ${this.b})`;
  }

  public toRGBA(): string {
    const alpha = (this.a / 255).toFixed(3);
    return `rgba(${this.r}, ${this.g}, ${this.b}, ${alpha})`;
  }

  public equals(other: unknown): boolean {
    if (other instanceof Color) {
      return (
        this.r === other.r &&
        this.g === other.g &&
        this.b === other.b &&
        this.a === other.a
      );
    }
    return false;
  }
}

export class Font {
  public static readonly PLAIN = 0;
  public static readonly BOLD = 1;
  public static readonly ITALIC = 2;

  public readonly name: string;
  public readonly style: number;
  public readonly size: number;

  constructor(name: string, style: number = Font.PLAIN, size: number = 12) {
    this.name = name;
    this.style = style;
    this.size = size;
  }

  public getName(): string {
    return this.name;
  }

  public getStyle(): number {
    return this.style;
  }

  public getSize(): number {
    return this.size;
  }

  public isBold(): boolean {
    return (this.style & Font.BOLD) !== 0;
  }

  public isItalic(): boolean {
    return (this.style & Font.ITALIC) !== 0;
  }

  public isPlain(): boolean {
    return this.style === 0;
  }

  public deriveFont(styleOrSize: number, isSize?: boolean): Font {
    if (isSize ?? styleOrSize > 3) {
      return new Font(this.name, this.style, styleOrSize);
    }
    return new Font(this.name, styleOrSize, this.size);
  }

  public deriveFontSize(size: number): Font {
    return new Font(this.name, this.style, size);
  }
}
