import Utils from "./Utils";

export interface Point {
  x: number;
  y: number;
}

export interface Rectangle {
  x: number;
  y: number;
  width: number;
  height: number;
}

export interface Dimension {
  width: number;
  height: number;
}

export interface FontMetrics {
  getAscent(): number;
  getLeading(): number;
  getHeight(): number;
  stringWidth(text: string): number;
}

export enum VerticalAlign {
  TOP = "TOP",
  MIDDLE = "MIDDLE",
  BOTTOM = "BOTTOM",
}

export namespace VerticalAlignHelper {
  export function getVerticalAlign(va: number): VerticalAlign {
    return va === -1 ? VerticalAlign.TOP : va === 1 ? VerticalAlign.BOTTOM : VerticalAlign.MIDDLE;
  }

  export function fromTheme(theme: { getTextAlignV?: () => number; textAlignV?: number }): VerticalAlign {
    const val = typeof theme.getTextAlignV === "function" ? theme.getTextAlignV() : theme.textAlignV ?? 0;
    return getVerticalAlign(val);
  }
}

export class G2Utils {
  public static VerticalAlign = VerticalAlign;

  public static getVerticalAlign(va: number): VerticalAlign {
    return VerticalAlignHelper.getVerticalAlign(va);
  }

  public static drawRect(r: Rectangle, ctx: CanvasRenderingContext2D): void {
    ctx.strokeRect(r.x, r.y, r.width, r.height);
  }

  public static fillRect(r: Rectangle, ctx: CanvasRenderingContext2D): void {
    ctx.fillRect(r.x, r.y, r.width, r.height);
  }

  public static drawRoundRect(r: Rectangle, ctx: CanvasRenderingContext2D, strokeWidth: number = 1): void {
    const prevLineWidth = ctx.lineWidth;
    ctx.lineWidth = strokeWidth;
    ctx.beginPath();
    const radius = 15;
    ctx.roundRect
      ? ctx.roundRect(r.x, r.y, r.width, r.height, radius)
      : ctx.rect(r.x, r.y, r.width, r.height);
    ctx.stroke();
    ctx.lineWidth = prevLineWidth;
  }

  public static centralize(r: Rectangle, fm: FontMetrics, s: string): Point {
    const y = fm.getAscent() + fm.getLeading() + r.y + Math.floor((r.height - fm.getHeight()) / 2);
    const x = r.x + Math.floor((r.width - fm.stringWidth(s)) / 2.0);
    return { x, y };
  }

  public static adjustString(
    r: Rectangle,
    ls: number,
    fm: FontMetrics,
    va: VerticalAlign,
    texts: string[]
  ): Point[] {
    const result: Point[] = new Array(texts.length);
    let h = fm.getHeight();
    const lineSpacing = h * ls;
    h = Math.floor(h + lineSpacing);
    let margin = fm.getAscent() + fm.getLeading() + r.y + Math.floor(lineSpacing / 2.0);

    switch (va) {
      case VerticalAlign.MIDDLE:
        margin += Math.floor((r.height - texts.length * h) / 2);
        break;
      case VerticalAlign.BOTTOM:
        margin += r.height - texts.length * h;
        break;
      case VerticalAlign.TOP:
      default:
        break;
    }

    for (let i = 0; i < texts.length; i++) {
      const text = texts[i] ?? "";
      const x = r.x + Math.floor((r.width - fm.stringWidth(text)) / 2.0);
      const y = margin + h * i;
      result[i] = { x, y };
    }

    return result;
  }

  public static drawString(
    r: Rectangle,
    ctx: CanvasRenderingContext2D,
    ls: number,
    fm: FontMetrics,
    va: VerticalAlign,
    texts: string[]
  ): void {
    const points = G2Utils.adjustString(r, ls, fm, va, texts);
    for (let i = 0; i < texts.length; i++) {
      const text = texts[i];
      const point = points[i];
      if (text !== undefined && point !== undefined) {
        ctx.fillText(text, point.x, point.y);
      }
    }
  }

  public static setupAlphaComposite(ctx: CanvasRenderingContext2D, alpha: number): number {
    const old = ctx.globalAlpha;
    ctx.globalAlpha = Utils.range(alpha, 0.0, 1.0);
    return old;
  }

  public static drawArrow(
    ctx: CanvasRenderingContext2D,
    x1: number,
    y1: number,
    x2: number,
    y2: number,
    size: number
  ): void {
    ctx.save();
    const dx = x2 - x1;
    const dy = y2 - y1;
    const angle = Math.atan2(dy, dx);
    const len = Math.sqrt(dx * dx + dy * dy);

    ctx.translate(x1, y1);
    ctx.rotate(angle);

    const oldLineWidth = ctx.lineWidth;
    ctx.lineWidth = size / 4.0;
    ctx.beginPath();
    ctx.moveTo(0, 0);
    ctx.lineTo(len - size / 2, 0);
    ctx.stroke();
    ctx.lineWidth = oldLineWidth;

    ctx.beginPath();
    ctx.moveTo(len, 0);
    ctx.lineTo(len - size, -size);
    ctx.lineTo(len - size, size);
    ctx.closePath();
    ctx.fill();

    ctx.restore();
  }

  public static getBestFontSizeForHeight(
    maxHeight: number,
    measureHeightFn: (fontSize: number) => number
  ): number {
    let middle = 500;
    let left = middle - 1;
    let right = middle;

    while (true) {
      const index = middle - 1;
      const candidate = measureHeightFn(index);
      if (candidate === maxHeight) {
        return index;
      }

      if (candidate > maxHeight) {
        if (left === 0) {
          for (let i = index; i > 0; i--) {
            if (measureHeightFn(i) < maxHeight) {
              return i;
            }
          }
          return 1.0;
        }

        right = Math.floor(left / 2);
        left = left % 2 === 0 ? right - 1 : right;
        middle -= right + 1;
      } else {
        if (right === 0) {
          for (let ix = index; ix <= 1000; ix++) {
            if (measureHeightFn(ix) > maxHeight) {
              return ix - 1.0;
            }
          }
          return 1000.0;
        }

        if (right % 2 === 0) {
          right = Math.floor(right / 2);
          left = right - 1;
        } else {
          right = Math.floor(right / 2);
          left = right;
        }

        middle += left + 1;
      }
    }
  }

  public static getBestFontSize(
    maxWidth: number,
    maxHeight: number,
    text: string,
    measureFn: (fontSize: number, text: string) => { width: number; height: number }
  ): number {
    let middle = 500;
    let left = middle - 1;
    let right = middle;

    while (true) {
      const index = middle - 1;
      const dim = measureFn(index, text);
      const w = dim.width;
      const h = dim.height;

      if (maxWidth === w && maxHeight === h) {
        return index;
      }

      if (w <= maxWidth && h <= maxHeight) {
        if (right === 0) {
          for (let i = index; i <= 1000; i++) {
            const d = measureFn(i, text);
            if (d.width > maxWidth || d.height > maxHeight) {
              return i - 1.0;
            }
          }
          return 1000.0;
        }

        if (right % 2 === 0) {
          right = Math.floor(right / 2);
          left = right - 1;
        } else {
          right = Math.floor(right / 2);
          left = right;
        }

        middle += left + 1;
      } else {
        if (left === 0) {
          for (let ix = index; ix > 0; ix--) {
            const d = measureFn(ix, text);
            if (d.width <= maxWidth && d.height <= maxHeight) {
              return ix;
            }
          }
          return 1.0;
        }

        right = Math.floor(left / 2);
        left = left % 2 === 0 ? right - 1 : right;
        middle -= right + 1;
      }
    }
  }
}

export default G2Utils;
