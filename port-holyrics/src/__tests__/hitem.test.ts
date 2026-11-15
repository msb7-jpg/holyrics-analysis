import { describe, expect, it } from 'bun:test';
import { HItem, type IHItem } from '../hitem/HItem';
import { HItemMusic } from '../hitem/item/HItemMusic';
import { HItemAnnouncement } from '../hitem/item/HItemAnnouncement';
import { HItemEvent } from '../hitem/item/HItemEvent';
import { HItemUtils } from '../hitem/HItemUtils';
import { HItemTab } from '../hitem/HItemTab';
import { EnumTabType } from '../hitem/EnumTabType';

describe('HItem isomorphic models', () => {
  it('should instantiate HItemMusic properly', () => {
    const item = new HItemMusic(101, {
      id: 101,
      title: 'Amazing Grace',
      artist: 'John Newton',
      lyrics: 'Amazing grace, how sweet the sound...',
    });

    expect(item.getId()).toBe('101');
    expect(item.getName()).toBe('Amazing Grace - John Newton');
    expect(item.getActionName()).toBe('Exibir');
    expect(item.getThumb()).toBe('holyricsIcon48.png');
    expect(item.getAsFile()).toBeNull();
    expect(item.getTooltip()).toContain('Amazing Grace');
    expect(item.listPathIfDirectory()).toEqual([]);
  });

  it('should instantiate HItemAnnouncement properly', () => {
    const item = new HItemAnnouncement(202, {
      id: 202,
      name: 'Reunião de Oração',
      description: 'Quarta-feira às 19:30',
    });

    expect(item.getId()).toBe('202');
    expect(item.getName()).toBe('Reunião de Oração');
    expect(item.getActionName()).toBe('Exibir');
    expect(item.getThumb()).toBe('announcement16.png');
    expect(item.getAsFile()).toBeNull();
    expect(item.getTooltip()).toContain('Quarta-feira');
  });

  it('should instantiate HItemEvent with subitems', () => {
    const musicItem = new HItemMusic(1, { title: 'Song 1' });
    const announcementItem = new HItemAnnouncement(2, { name: 'Notice 1' });

    const event = new HItemEvent(1, 'Culto de Domingo', [musicItem, announcementItem], {
      date: '2026-11-15',
    });

    expect(event.getId()).toBe('1');
    expect(event.getName()).toBe('Culto de Domingo');
    expect(event.getSubItems().length).toBe(2);
    expect(event.getTooltip()).toContain('Culto de Domingo');
  });

  it('should check file extensions in HItemUtils', () => {
    expect(HItemUtils.isAudio('test.mp3')).toBe(true);
    expect(HItemUtils.isAudio('test.wav')).toBe(true);
    expect(HItemUtils.isAudio('test.txt')).toBe(false);

    expect(HItemUtils.isVideo('movie.mp4')).toBe(true);
    expect(HItemUtils.isVideo('movie.mkv')).toBe(true);
    expect(HItemUtils.isVideo('movie.png')).toBe(false);

    expect(HItemUtils.isImage('pic.png')).toBe(true);
    expect(HItemUtils.isImage('pic.jpg')).toBe(true);

    expect(HItemUtils.isMedia('song.flac')).toBe(true);
    expect(HItemUtils.isMedia('video.mp4')).toBe(true);
    expect(HItemUtils.isMedia('doc.pdf')).toBe(false);
  });

  it('should filter and handle tabs in HItemTab', () => {
    const item1 = new HItemMusic(1, { title: 'Santo Santo' });
    const item2 = new HItemMusic(2, { title: 'Grande é o Senhor' });
    const item3 = new HItemMusic(3, { title: 'Aleluia' });

    const addedPlaylist: IHItem[][] = [];
    const tab = new HItemTab(EnumTabType.MUSIC, {
      addPlaylistMedia: (items) => {
        addedPlaylist.push(items);
      },
    }, [item1, item2, item3]);

    expect(tab.items.length).toBe(3);
    expect(tab.filteredItems.length).toBe(3);

    tab.setFilter('santo');
    expect(tab.filteredItems.length).toBe(1);
    expect(tab.filteredItems[0]!.getName()).toContain('Santo');

    tab.setSelectedIndex(0);
    expect(tab.getSelectedItem()?.getId()).toBe('1');

    tab.addPlaylist();
    expect(addedPlaylist.length).toBe(1);
    expect(addedPlaylist[0]![0]!.getId()).toBe('1');

    tab.setFilter('');
    expect(tab.filteredItems.length).toBe(3);
  });
});
