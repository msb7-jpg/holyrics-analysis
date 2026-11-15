import { Bible } from "./Bible";
import { FactoryBible } from "./FactoryBible";
import { Values } from "../util/Values";
import { Dir } from "../util/Dir";
import * as path from "path";
import * as fs from "fs";

// Polyfill for bible-specific Values
const PATH_DATABASE = path.join(Values.PATH_APP || "Holyrics", "files", "Bible LG") + path.sep;

class BibleValues {
  private static readonly FILE_MAP = path.join(PATH_DATABASE, "bible.json");
  private static MAP: Map<string, any> = new Map();

  static {
    try {
      if (fs.existsSync(this.FILE_MAP)) {
        const data = fs.readFileSync(this.FILE_MAP, "utf8");
        const parsed = JSON.parse(data);
        for (const key of Object.keys(parsed)) {
          this.MAP.set(key, parsed[key]);
        }
      }
    } catch (e) {
      // Ignore
    }
  }

  public static put(key: string, value: any): any {
    this.MAP.set(key, value);
    try {
      if (!fs.existsSync(PATH_DATABASE)) {
        fs.mkdirSync(PATH_DATABASE, { recursive: true });
      }
      const obj = Object.fromEntries(this.MAP);
      fs.writeFileSync(this.FILE_MAP, JSON.stringify(obj, null, 2), "utf8");
    } catch (e) {
      // Ignore
    }
    return value;
  }

  public static getOrDefault(key: string, def: any): string {
    return String(this.MAP.has(key) ? this.MAP.get(key) : def);
  }
}

export class AvailableVersions {
  private static VERSIONS: AvailableVersions[] = [];
  public static readonly MAP_VERSIONS = new Map<string, string>();
  public readonly pathBible: string;
  public readonly languageAndVersion: string;

  public static getVersions(): AvailableVersions[] {
    return [...AvailableVersions.VERSIONS];
  }

  public static addVersion(biblePath: string): boolean {
    try {
      AvailableVersions.VERSIONS.push(new AvailableVersions(biblePath));
      AvailableVersions.sortVersions();
      return true;
    } catch (e) {
      console.error(e);
      return false;
    }
  }

  private static sortVersions(): void {
    AvailableVersions.VERSIONS.sort((o1, o2) => {
      // Use language from locale or default to pt
      let lang = "pt";
      try {
        lang = (Intl.DateTimeFormat().resolvedOptions().locale.split('-')[0] ?? 'pt').toLowerCase();
      } catch (e) { }

      const b1 = o1.getVersion().startsWith(lang);
      const b2 = o2.getVersion().startsWith(lang);
      if (b1 === b2) {
        return o1.toString().localeCompare(o2.toString(), undefined, { sensitivity: 'base' });
      }
      return b1 ? -1 : 1;
    });
  }

  constructor(pathBible: string) {
    this.pathBible = pathBible;
    this.languageAndVersion = this.getLanguageAndVersion();
  }

  public getVersion(): string {
    return path.basename(this.pathBible).replace(".bib", "");
  }

  public toString(): string {
    return this.languageAndVersion;
  }

  public async loadBible(): Promise<Bible | null> {
    try {
      return FactoryBible.create(this.pathBible);
    } catch (e) {
      console.error(e);
      return null;
    }
  }

  private getLanguageAndVersion(): string {
    const v = this.getVersion();
    let lav = AvailableVersions.MAP_VERSIONS.get(v);
    if (lav) {
      return lav;
    }
    
    const valLav = BibleValues.getOrDefault(v, "");
    if (valLav !== "") {
      return valLav;
    }
    
    lav = FactoryBible.getLanguageAndVersion(this.pathBible);
    BibleValues.put(v, lav);
    return lav;
  }
}

// Static block replacement
(function initialize() {
  const map = AvailableVersions.MAP_VERSIONS;
  map.set("en_asv", "(English) American Standard-ASV1901");
  map.set("en_bbe", "(English) Bible in Basic English");
  map.set("en_dby", "(English) Darby English Bible");
  map.set("en_kjv", "(English) King James Version");
  map.set("en_web", "(English) World English Bible");
  map.set("en_ylt", "(English) Young's Literal Translation");
  map.set("pt_acrf", "(Português) Almeida Corrigida e Revisada Fiel");
  map.set("pt_arib", "(Português) Almeida Revisada Imprensa Bíblica");
  map.set("pt_nvi", "(Português) Nova Versão Internacional");
  map.set("pt_sbb", "(Português) Sociedade Bíblica Britânica");
  map.set("pt_ara", "(Português) Almeida Revista e Atualizada");
  map.set("pt_arc", "(Português) Almeida Revisada e Corrigida");
  map.set("pt_ntlh", "(Português) Nova Tradução na Linguagem de Hoje");

  const _default = ["pt_acrf"];

  try {
    if (!fs.existsSync(PATH_DATABASE)) {
      fs.mkdirSync(PATH_DATABASE, { recursive: true });
    }

    _default.forEach(abbrev => {
      const fx = path.join(PATH_DATABASE, `${abbrev}.bib`);
      if (!fs.existsSync(fx)) {
        if (typeof (Dir as any).exportFromJar === "function") {
          (Dir as any).exportFromJar(`/com/limagiran/bible/database/${abbrev}.bib`, fx);
        } else {
          // If Dir.exportFromJar is not available, we can't extract it. 
          // For now just ignore or log.
        }
      }
    });

    if (fs.existsSync(PATH_DATABASE)) {
      const files = fs.readdirSync(PATH_DATABASE);
      for (const file of files) {
        if (file.endsWith(".bib")) {
          const filePath = path.join(PATH_DATABASE, file);
          try {
            // Push directly to bypass access limits to private array
            (AvailableVersions as any).VERSIONS.push(new AvailableVersions(filePath));
          } catch (e) {
            console.error(e);
          }
        }
      }
      (AvailableVersions as any).sortVersions();
    }
  } catch (e) {
    console.error(e);
  }
})();
