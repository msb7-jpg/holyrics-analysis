import * as fs from "node:fs";
import * as path from "node:path";

function parseProperties(content: string): Map<string, string> {
  const map = new Map<string, string>();
  const lines = content.split(/\r?\n/);
  for (let line of lines) {
    line = line.trim();
    if (!line || line.startsWith("#") || line.startsWith("!")) {
      continue;
    }
    const sepIndex = line.search(/[:=]/);
    if (sepIndex !== -1) {
      const key = line.substring(0, sepIndex).trim();
      const value = line.substring(sepIndex + 1).trim();
      map.set(key, value);
    } else {
      map.set(line, "");
    }
  }
  return map;
}

function stringifyProperties(map: Map<string, string>): string {
  const lines: string[] = ["# Holyrics", `# ${new Date().toISOString()}`];
  for (const [k, v] of map.entries()) {
    lines.push(`${k}=${v}`);
  }
  return lines.join("\n") + "\n";
}

function range(value: number, min: number, max: number): number {
  return Math.min(Math.max(value, min), max);
}

function intOrElse(val: string | null | undefined, def: number): number {
  if (val == null || val === "") return def;
  const parsed = parseInt(val, 10);
  return Number.isNaN(parsed) ? def : parsed;
}

function longOrElse(val: string | null | undefined, def: number): number {
  if (val == null || val === "") return def;
  const parsed = Number(val);
  return Number.isNaN(parsed) ? def : Math.floor(parsed);
}

export class EnumPropertyItem {
  public readonly key: string;
  public readonly clazz: "String" | "Boolean" | "Number";
  public readonly defaultVal: string;
  public readonly help: string | null;
  public readonly available: boolean;

  constructor(
    key: string,
    clazz: "String" | "Boolean" | "Number",
    defaultVal: string,
    help: string | null,
    available: boolean
  ) {
    this.key = key;
    this.clazz = clazz;
    this.defaultVal = defaultVal;
    this.help = help;
    this.available = available;
  }

  public is(): boolean {
    return MyProperties.get(this.key, this.defaultVal).toLowerCase() === "true";
  }

  public getString(): string {
    return MyProperties.get(this.key, this.defaultVal);
  }

  public getLong(): number {
    return longOrElse(MyProperties.get(this.key, this.defaultVal), longOrElse(this.defaultVal, 0));
  }

  public getLongRange(min: number, max: number): number {
    return range(this.getLong(), min, max);
  }

  public getInt(): number {
    return intOrElse(MyProperties.get(this.key, this.defaultVal), intOrElse(this.defaultVal, 0));
  }

  public getIntRange(min: number, max: number): number {
    return range(this.getInt(), min, max);
  }

  public set(value: unknown): void {
    MyProperties.set(this.key, String(value));
  }

  public isEditable(): boolean {
    return this.help !== null;
  }
}

export class MyProperties {
  private static readonly FLAGS = new Map<string, string>();
  private static readonly FILE = path.resolve("config.properties");
  private static properties: Map<string, string> | null = null;
  private static lastLoad: number = 0;

  public static get(key: string, def: string = ""): string {
    try {
      const prop = this.load();
      const val = prop.get(key);
      return val !== undefined ? val : def;
    } catch {
      return def;
    }
  }

  public static set(key: string, value: string): void {
    try {
      const prop = this.load();
      prop.set(key, value);
      const content = stringifyProperties(prop);
      fs.writeFileSync(this.FILE, content, "utf-8");
    } catch {
      // ignore
    }
  }

  public static mapStartsWith(prefix: string): Record<string, string> {
    const map: Record<string, string> = {};
    try {
      const prop = this.load();
      for (const [k, v] of prop.entries()) {
        if (k.startsWith(prefix)) {
          map[k] = v;
        }
      }
    } catch {
      // ignore
    }
    return map;
  }

  private static load(): Map<string, string> {
    const now = Date.now();
    if (this.properties !== null && now - this.lastLoad < 10000) {
      return this.properties;
    }

    this.lastLoad = now;
    try {
      if (fs.existsSync(this.FILE)) {
        const content = fs.readFileSync(this.FILE, "utf-8");
        this.properties = parseProperties(content);
        return this.properties;
      }
    } catch {
      // ignore
    }

    this.properties = new Map<string, string>();
    return this.properties;
  }

  public static init(): void {
    if (this.get("check", "") !== "ok") {
      this.set("check", "ok");
    }
  }

  public static getFlag(key: string, _default: string): string {
    const val = this.FLAGS.get(key);
    return val !== undefined ? val : _default;
  }

  public static setFlag(key: string, value: string): void {
    this.FLAGS.set(key, value);
  }

  public static readonly EnumProperties = {
    LOCALE: new EnumPropertyItem("locale", "String", ".default", null, false),
    FORCE_FX_VIDEO_THEME: new EnumPropertyItem("force-fx-video-theme", "Boolean", "false", null, false),
    VAGALUME_API_KEY: new EnumPropertyItem("vagalume-api-key", "String", "", null, false),
    MAP_SLIDE_LABEL_TITLE_COLOR: new EnumPropertyItem("map-slide-label-title-color", "String", "", null, true),
    AUTOMATIC_PRESENTATION_DELAY: new EnumPropertyItem("automatic-presentation-delay", "Number", "250", null, true),
    COMMUNICATION_PANEL_DISABLE_BREAK_LINE: new EnumPropertyItem("communication-panel-disable-break-line", "Boolean", "false", null, true),
    DISPLAY_FONT_NAME_WITH_FONT: new EnumPropertyItem("display-font-name-with-font", "Boolean", "true", null, true),
    COMMUNICATION_PANEL_CLOCK_SIZE: new EnumPropertyItem("communication-panel-clock-size", "Number", "500", null, true),
    CHAT_TEXT_FONT_SIZE: new EnumPropertyItem("chat-text-font-size", "Number", "12", null, true),
    DISPLAY_PREVIOUS_NEXT_BUTTON_FOOTER_CONTROL: new EnumPropertyItem("display-previous-next-button-footer-control", "Boolean", "false", null, true),
    READY_MESSAGES_UNIQUE_FILE: new EnumPropertyItem("ready-messages-unique-file", "Boolean", "true", null, true),

    availables(): EnumPropertyItem[] {
      return Object.values(MyProperties.EnumProperties).filter(
        (val): val is EnumPropertyItem => val instanceof EnumPropertyItem && val.available
      );
    },
  };
}

export default MyProperties;
