import Theme from "../model/theme/Theme.ts";
import ControllerFactory from "./ControllerFactory.ts";

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.control.ThemeController
 */
export class ThemeController {
  private static readonly FILE_NAME = "themes.hly";
  private static readonly THEMES: Theme[] = [];

  static {
    const loaded = ControllerFactory.load<Theme>(
      ThemeController.FILE_NAME,
      (data: any) => {
        const theme = Theme.create();
        Object.assign(theme, data);
        return theme;
      }
    );

    if (loaded && loaded.length > 0) {
      ThemeController.THEMES.push(...loaded);
    }

    if (ThemeController.THEMES.length === 0) {
      ThemeController.add(Theme.create());
    }
  }

  public static add(t: Theme): void {
    const exists = ThemeController.THEMES.some((theme) => theme.id === t.id);
    if (!exists) {
      ThemeController.THEMES.push(t);
      ThemeController.save();
    }
  }

  public static remove(t: Theme): void {
    const index = ThemeController.THEMES.findIndex((theme) => theme.id === t.id);
    if (index !== -1) {
      ThemeController.THEMES.splice(index, 1);
      ThemeController.save();
    }
  }

  public static getThemes(): Theme[] {
    return [...ThemeController.THEMES];
  }

  public static save(): void {
    if (ThemeController.THEMES.length === 0) {
      ThemeController.THEMES.push(Theme.create());
    }
    ControllerFactory.save(ThemeController.THEMES, ThemeController.FILE_NAME);
  }

  public static set(t: Theme): void {
    const index = ThemeController.THEMES.findIndex((theme) => theme.id === t.id);
    if (index !== -1) {
      ThemeController.THEMES[index] = t;
    }
    ControllerFactory.save(ThemeController.THEMES, ThemeController.FILE_NAME);
  }

  public static setList(list: Theme[]): void {
    try {
      if (list.length === 0) {
        return;
      }

      const backup = [...ThemeController.THEMES];
      ThemeController.THEMES.length = 0;
      ThemeController.THEMES.push(...list);

      if (!ControllerFactory.save(ThemeController.THEMES, ThemeController.FILE_NAME)) {
        ThemeController.THEMES.length = 0;
        ThemeController.THEMES.push(...backup);
        ControllerFactory.save(ThemeController.THEMES, ThemeController.FILE_NAME);
      }
    } catch {
      // Ignore
    }
  }

  public static getDefault(): Theme {
    const first = ThemeController.THEMES[0];
    return first ? first : Theme.create();
  }

  public static get(id: number): Theme {
    const found = ThemeController.THEMES.find((t) => t.id === id);
    return found ? found : ThemeController.getDefault();
  }

  public static getOrDefault(id: number, def: Theme): Theme {
    for (let i = 0; i < ThemeController.THEMES.length; i++) {
      const t = ThemeController.THEMES[i];
      if (t && t.id === id) {
        return t;
      }
    }
    return def;
  }

  public static exists(id: number): boolean {
    for (let i = 0; i < ThemeController.THEMES.length; i++) {
      const t = ThemeController.THEMES[i];
      if (t && t.id === id) {
        return true;
      }
    }
    return false;
  }

  public static load(popup?: { setProgress: (text: string) => void }): void {
    const themes = ThemeController.getThemes();
    for (let i = 0; i < themes.length; i++) {
      const percentage = Math.round(((i + 1) / themes.length) * 100);
      popup?.setProgress?.(`Themes (${percentage}%)`);
    }
  }
}

export default ThemeController;
