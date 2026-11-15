export class StringUtils {
  public static readonly COLOR_HEX_HIGHLIGHT_TEXT = "#ffff00";

  public static maxLength(value: string, max: number, withPoints: boolean = false): string {
    if (value.length <= max) {
      return value;
    }
    return value.substring(0, max) + (withPoints ? "..." : "");
  }

  public static maxLengthGetEnd(value: string, max: number, withPoints: boolean = false): string {
    if (value.length <= max) {
      return value;
    }
    return (withPoints ? "..." : "") + value.substring(value.length - max, value.length);
  }

  public static firstNumber(customParagraph: string, _default: number): number {
    try {
      let sb = "";
      let flag = false;
      let i = -1;

      while (++i < customParagraph.length) {
        const c = customParagraph.charAt(i);
        if (c >= "0" && c <= "9") {
          if (!flag && i > 0 && customParagraph.charAt(i - 1) === "-") {
            sb += "-";
          }
          sb += c;
          flag = true;
        } else if (flag) {
          break;
        }
      }

      if (!sb) {
        return _default;
      }
      const parsed = parseInt(sb, 10);
      return Number.isNaN(parsed) ? _default : parsed;
    } catch {
      return _default;
    }
  }

  public static stringToSearch(value: string): string {
    return this.removeAccent(value.replace(/\n/g, " ").toLowerCase());
  }

  public static removeAccent(string: string): string {
    return string.normalize("NFD").replace(/[\u0300-\u036f]/g, "");
  }

  public static setupHighlight(text: string, value: string): string {
    if (text && value) {
      try {
        const searchVal = this.stringToSearch(value.trim());
        if (!searchVal) {
          return text;
        }
        let highlight = "";
        let sb = text;
        if (this.stringToSearch(text).indexOf(searchVal) === 0) {
          const var10 = sb.substring(0, searchVal.length);
          highlight = this.applyHighlight(var10);
          sb = highlight + sb.substring(searchVal.length);
        }

        let index = this.stringToSearch(sb).indexOf(searchVal, highlight.length);

        while (index >= 0) {
          const end = index + searchVal.length;
          const size = sb.length;
          highlight = sb.substring(index, end);
          sb = sb.substring(0, index) + this.applyHighlight(highlight) + sb.substring(end);
          index = this.removeAccent(sb).toLowerCase().indexOf(searchVal, end + (sb.length - size));
        }

        return sb;
      } catch {
        return text;
      }
    } else {
      return text;
    }
  }

  private static applyHighlight(value: string): string {
    return `<span style='background-color:${this.COLOR_HEX_HIGHLIGHT_TEXT}'>${value}</span>`;
  }

  public static spanNoWrap(value: string): string {
    return `<span style='white-space:nowrap'>${value}</span>`;
  }

  public static removeExcessLineBreak(value: string | null | undefined): string | null | undefined {
    if (value == null) {
      return value;
    } else {
      let result = value;
      while (result.includes("\n\n")) {
        result = result.replace(/\n\n/g, "\n");
      }
      while (result.startsWith("\n")) {
        result = result.substring(1);
      }
      while (result.endsWith("\n")) {
        result = result.substring(0, result.length - 1);
      }
      return result;
    }
  }

  public static formatTimeHMS(millis: number): string {
    const h = Math.floor(millis / 3600000);
    const m = Math.floor(millis / 60000);
    const s = Math.floor(millis / 1000);
    const hh = String(h).padStart(2, "0");
    const mm = String(m % 60).padStart(2, "0");
    const ss = String(s % 60).padStart(2, "0");
    return `${hh}:${mm}:${ss}`;
  }

  public static formatTimeMm_Ss_MSms(millis: number): string {
    const m = Math.floor(millis / 60000);
    const s = Math.floor(millis / 1000);
    const ms = millis % 1000;
    const mm = String(m).padStart(2, "0");
    const ss = String(s % 60).padStart(2, "0");
    const msStr = String(ms).padStart(3, "0");
    return `${mm}m ${ss}s ${msStr}ms`;
  }

  public static wordWrap(str: string, len: number): string {
    if (str != null && str.length > len && len >= 1) {
      const chars = str.split("");
      let count = 0;

      for (let i = 0; i < chars.length; i++) {
        count = chars[i] !== " " ? count + 1 : 0;
        if (count === len) {
          chars.splice(++i, 0, " ");
          count = 0;
        }
      }

      return chars.join("");
    } else {
      return str;
    }
  }

  public static escapeHTML(html: string): string {
    return html
      .replace(/&/g, "&#38;")
      .replace(/"/g, "&#34;")
      .replace(/'/g, "&#39;")
      .replace(/</g, "&lt;")
      .replace(/>/g, "&gt;")
      .replace(/\n/g, "<br>");
  }

  public static onlyWordNumberAndSpace(value: string): string {
    return value.replace(/[^\d\w _àèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇ]/g, "");
  }
}

export default StringUtils;
