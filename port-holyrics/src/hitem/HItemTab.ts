import { EnumTabType, TabTypeDetails, type TabTypeMetadata } from './EnumTabType';
import type { IHItem } from './HItem';

export interface HItemTabListener {
  addPlaylistMedia(items: IHItem[]): void;
  onFavoriteChange?(item: IHItem, isFavorite: boolean): void;
  onSelect?(item: IHItem | null): void;
}

export class HItemTab {
  public readonly tabType: EnumTabType;
  public readonly metadata: TabTypeMetadata;
  public items: IHItem[] = [];
  public filteredItems: IHItem[] = [];
  public searchText: string = '';
  public selectedIndex: number = -1;

  public isFavoriteEnabled: boolean = true;
  public isPlaylistMediaEnabled: boolean = true;

  public readonly listener?: HItemTabListener;

  // Button icons
  public static readonly ICON_PLAY = 'play24.png';
  public static readonly ICON_ADD_FAVORITE = 'favorite24.png';
  public static readonly ICON_ADD_PLAYLIST = 'playlistAdd24.png';
  public static readonly ICON_OPEN_DIR = 'open24.png';
  public static readonly ICON_REFRESH = 'refresh24.png';

  constructor(
    tabType: EnumTabType,
    listener?: HItemTabListener,
    initialItems: IHItem[] = []
  ) {
    this.tabType = tabType;
    this.metadata = TabTypeDetails[tabType];
    this.listener = listener;
    this.setItems(initialItems);
  }

  public setItems(items: IHItem[]): void {
    this.items = items;
    this.applyFilter();
  }

  public addItem(item: IHItem): void {
    this.items.push(item);
    this.applyFilter();
  }

  public removeItem(id: string | number): void {
    this.items = this.items.filter(item => item.getId() !== String(id));
    this.applyFilter();
  }

  public setFilter(query: string): void {
    this.searchText = query.trim().toLowerCase();
    this.applyFilter();
  }

  private applyFilter(): void {
    if (!this.searchText) {
      this.filteredItems = [...this.items];
    } else {
      this.filteredItems = this.items.filter(item => {
        const name = item.getName().toLowerCase();
        const tooltip = item.getTooltip?.()?.toLowerCase() || '';
        return name.includes(this.searchText) || tooltip.includes(this.searchText);
      });
    }

    if (this.selectedIndex >= this.filteredItems.length) {
      this.selectedIndex = this.filteredItems.length - 1;
    }
  }

  public getSelectedItem(): IHItem | null {
    if (this.selectedIndex >= 0 && this.selectedIndex < this.filteredItems.length) {
      return this.filteredItems[this.selectedIndex] ?? null;
    }
    return null;
  }

  public setSelectedIndex(index: number): void {
    this.selectedIndex = Math.max(-1, Math.min(this.filteredItems.length - 1, index));
    if (this.listener?.onSelect) {
      this.listener.onSelect(this.getSelectedItem());
    }
  }

  public selectNext(): void {
    if (this.filteredItems.length > 0) {
      this.setSelectedIndex(Math.min(this.filteredItems.length - 1, this.selectedIndex + 1));
    }
  }

  public selectPrevious(): void {
    if (this.filteredItems.length > 0) {
      this.setSelectedIndex(Math.max(0, this.selectedIndex - 1));
    }
  }

  public play(immediate: boolean = true): void {
    const selected = this.getSelectedItem();
    if (selected) {
      if (selected.favoriteAction) {
        selected.favoriteAction(immediate);
      } else if (selected.run) {
        selected.run();
      }
    }
  }

  public addPlaylist(): void {
    const selected = this.getSelectedItem();
    if (selected && this.listener?.addPlaylistMedia) {
      this.listener.addPlaylistMedia([selected]);
    }
  }

  public addFavorite(): void {
    const selected = this.getSelectedItem();
    if (selected && this.listener?.onFavoriteChange) {
      this.listener.onFavoriteChange(selected, true);
    }
  }

  public openDir(): void {
    const selected = this.getSelectedItem();
    const file = selected?.getAsFile();
    if (file) {
      // Open directory or file location
    }
  }

  public refresh(): void {
    this.applyFilter();
  }

  public setFavoriteEnabled(flag: boolean): void {
    this.isFavoriteEnabled = flag;
  }

  public setPlaylistMediaEnabled(flag: boolean): void {
    this.isPlaylistMediaEnabled = flag;
  }

  public getActionName(): string {
    return this.metadata.actionName;
  }

  public getTitle(): string {
    return this.metadata.title;
  }
}

export default HItemTab;
