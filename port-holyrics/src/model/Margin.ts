export interface Dimension {
  width: number;
  height: number;
}

export interface RectangleLike {
  x: number;
  y: number;
  width: number;
  height: number;
}

export class Rectangle implements RectangleLike {
  public x: number;
  public y: number;
  public width: number;
  public height: number;

  constructor(x: number = 0, y: number = 0, width: number = 0, height: number = 0) {
    this.x = x;
    this.y = y;
    this.width = width;
    this.height = height;
  }

  public getSize(): Dimension {
    return { width: this.width, height: this.height };
  }
}

export class Margin {
  public static readonly serialVersionUID = 1;
  public static readonly TOP = 0;
  public static readonly RIGHT = 1;
  public static readonly BOTTOM = 2;
  public static readonly LEFT = 3;

  public readonly margins: [number, number, number, number] = [0, 0, 0, 0];

  constructor(top: number = 0, right: number = 0, bottom: number = 0, left: number = 0) {
    if (arguments.length === 1) {
      this.margins[0] = top;
      this.margins[1] = top;
      this.margins[2] = top;
      this.margins[3] = top;
    } else {
      this.margins[0] = top;
      this.margins[1] = right;
      this.margins[2] = bottom;
      this.margins[3] = left;
    }
  }

  public getTop(): number {
    return this.margins[0];
  }

  public setTop(top: number): this {
    this.margins[0] = top;
    return this;
  }

  public getRight(): number {
    return Math.min(99 - this.getLeft(), this.margins[1]);
  }

  public setRight(right: number): this {
    this.margins[1] = right;
    return this;
  }

  public getBottom(): number {
    return Math.min(99 - this.getTop(), this.margins[2]);
  }

  public setBottom(bottom: number): this {
    this.margins[2] = bottom;
    return this;
  }

  public getLeft(): number {
    return this.margins[3];
  }

  public setLeft(left: number): this {
    this.margins[3] = left;
    return this;
  }

  public getRectWithMargin(size: Dimension): Rectangle {
    const x = Math.round((size.width * this.getLeft()) / 100);
    const y = Math.round((size.height * this.getTop()) / 100);
    const width = Math.round((size.width * (100 - this.getLeft() - this.getRight())) / 100);
    const height = Math.round((size.height * (100 - this.getTop() - this.getBottom())) / 100);
    return new Rectangle(x, y, width, height);
  }

  public toString(): string {
    return `[${this.margins[0]},${this.margins[1]},${this.margins[2]},${this.margins[3]}]`;
  }
}

export default Margin;
