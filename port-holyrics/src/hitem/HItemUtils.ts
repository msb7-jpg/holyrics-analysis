import type { IHItem } from './HItem';

export const AUDIO_EXTENSIONS = [
  '.mp3',
  '.wav',
  '.ogg',
  '.aac',
  '.m4a',
  '.flac',
  '.wma',
];

export const VIDEO_EXTENSIONS = [
  '.mp4',
  '.mkv',
  '.avi',
  '.mov',
  '.wmv',
  '.flv',
  '.webm',
  '.m4v',
  '.mpg',
  '.mpeg',
];

export const IMAGE_EXTENSIONS = [
  '.jpg',
  '.jpeg',
  '.png',
  '.gif',
  '.bmp',
  '.webp',
];

export const MEDIA_EXTENSIONS = [...AUDIO_EXTENSIONS, ...VIDEO_EXTENSIONS];

export const HItemIcons = {
  IC_OPEN_LOCATION: 'open16.png',
  IC_PLAY: 'play16.png',
  IC_PLAY_SELECT_FILES: 'viewModule16.png',
  IC_PLAY_SELECT_ONE: 'file16.png',
  IC_ADD_PLAY_LIST_MEDIA: 'videoLibrary16.png',
  IC_ADD_FAVORITE: 'favorite16.png',
  IC_PLAY_LIST_PLAYER: 'playListPlayer16.png',
  IC_ANNOUNCEMENT: 'announcement16.png',
  IC_IMAGE: 'image16.png',
  IC_WEB: 'web16.png',
  IC_BIBLE: 'bible16.png',
  IC_HOLYRICS: 'holyricsIcon48.png',
  IC_EVENT: 'event16.png',
} as const;

export class HItemUtils {
  public static readonly IC_OPEN_LOCATION = HItemIcons.IC_OPEN_LOCATION;
  public static readonly IC_PLAY = HItemIcons.IC_PLAY;
  public static readonly IC_PLAY_SELECT_FILES = HItemIcons.IC_PLAY_SELECT_FILES;
  public static readonly IC_PLAY_SELECT_ONE = HItemIcons.IC_PLAY_SELECT_ONE;
  public static readonly IC_ADD_PLAY_LIST_MEDIA = HItemIcons.IC_ADD_PLAY_LIST_MEDIA;
  public static readonly IC_ADD_FAVORITE = HItemIcons.IC_ADD_FAVORITE;

  public static check(filePath: string, extensions: string[]): boolean {
    if (!filePath) return false;
    const lower = filePath.toLowerCase();
    return extensions.some(ext => lower.endsWith(ext));
  }

  public static isAudio(filePath: string): boolean {
    return this.check(filePath, AUDIO_EXTENSIONS);
  }

  public static isVideo(filePath: string): boolean {
    return this.check(filePath, VIDEO_EXTENSIONS);
  }

  public static isMedia(filePath: string): boolean {
    return this.check(filePath, MEDIA_EXTENSIONS);
  }

  public static isImage(filePath: string): boolean {
    return this.check(filePath, IMAGE_EXTENSIONS);
  }

  public static run(immediate: boolean = true, ...files: string[]): void {
    if (!files || files.length === 0) return;
    const first = files[0];
    if (!first) return;
    if (this.isImage(first)) {
      // Presentation image display
      return;
    }
    if (this.isMedia(first)) {
      // Media playback trigger
      return;
    }
  }

  public static openLocation(filePath: string): void {
    if (!filePath) return;
    try {
      // Native explorer/open action when in desktop / Electron / Tauri environment
      if (typeof window !== 'undefined' && (window as unknown as { electron?: { openPath?: (p: string) => void } }).electron?.openPath) {
        (window as unknown as { electron: { openPath: (p: string) => void } }).electron.openPath(filePath);
      }
    } catch {
      // Fallback
    }
  }

  public static getListIfDirectory(_dirPath: string, _filter?: (item: string) => boolean): string[] {
    return [];
  }

  public static setupPopupMenuAddFavorite(hitem: IHItem, onAdd?: (item: IHItem) => void): {
    label: string;
    icon: string;
    action: () => void;
  } {
    return {
      label: 'Adicionar aos Favoritos',
      icon: HItemIcons.IC_ADD_FAVORITE,
      action: () => {
        if (onAdd) {
          onAdd(hitem);
        }
      },
    };
  }

  public static setupPopupMenuAddPlaylist(hitem: IHItem, onAdd?: (item: IHItem) => void): {
    label: string;
    icon: string;
    action: () => void;
  } {
    return {
      label: 'Adicionar à Lista de Reprodução (Culto / Evento)',
      icon: HItemIcons.IC_ADD_PLAY_LIST_MEDIA,
      action: () => {
        if (onAdd) {
          onAdd(hitem);
        }
      },
    };
  }

  public static formatTooltip(title: string, details?: string): string {
    if (!details) return title;
    return `${title}\n${details}`;
  }
}

export default HItemUtils;
