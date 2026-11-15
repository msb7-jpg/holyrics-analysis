import { describe, expect, it } from "bun:test";
import { Announcement } from "../model/Announcement.ts";
import { AppMessage } from "../model/AppMessage.ts";
import { DBOffLine, LyricsJson } from "../model/DBOffLine.ts";
import { Presentation } from "../model/Presentation.ts";
import { Schedule } from "../model/Schedule.ts";
import { Paragraph, Music } from "../model/Music.ts";
import { Member } from "../model/Member.ts";

describe("Announcement Model", () => {
  it("should create and update announcement correctly", () => {
    const announcement = new Announcement();
    expect(announcement.id).toBeDefined();
    expect(announcement.getName()).toBe("");
    expect(announcement.getText()).toBe("");

    announcement.setName("Culto de Domingo");
    announcement.setText("Sejam bem-vindos!");

    expect(announcement.getName()).toBe("Culto de Domingo");
    expect(announcement.getText()).toBe("Sejam bem-vindos!");
    expect(announcement.toString().trim()).toBe("Culto de Domingo");

    const pres = announcement.createPresentation();
    expect(pres).toBeDefined();
    expect(pres.text.getText()).toBe("Sejam bem-vindos!");

    expect(announcement.equals(announcement)).toBe(true);
    expect(announcement.hashCode()).toBeNumber();
  });
});

describe("AppMessage Model", () => {
  it("should create app messages with sequential datetimes and correct helpers", () => {
    const msg1 = AppMessage.create("user-1", "João\nSilva", "Olá mundo");
    const msg2 = AppMessage.create("holyrics-admin", "Admin", "Aviso geral");

    expect(msg1.datetime).toBeLessThanOrEqual(msg2.datetime);
    expect(msg1.name).toBe("João Silva");
    expect(msg1.message).toBe("Olá mundo");
    expect(msg1.checkUser("user-1")).toBe(true);
    expect(msg1.isMyself()).toBe(false);

    expect(msg2.isMyself()).toBe(true);
    expect(msg2.toString()).toBe("Admin\nAviso geral");
    expect(msg2.toStringHTML()).toContain("<b>Admin:</b><br>Aviso geral");
  });
});

describe("DBOffLine Model", () => {
  it("should support search, lyricsJson, and static lists", () => {
    expect(DBOffLine.LIST).toBeArray();
    expect(DBOffLine.LIST_TO_SEARCH).toBeArray();
    expect(DBOffLine.LIST_MOST_PLAYED).toBeArray();

    const lyric = new LyricsJson("Title", "Artist", "Lyrics content here", "yt123", "pt");
    expect(lyric.title).toBe("Title");
    expect(lyric.youtubeId).toBe("yt123");

    const filter = {
      getSearch: () => "",
      isArtist: () => true,
      isTitle: () => true,
      isLyrics: () => true,
      getSearchContains: () => "",
      getSearchStartsWith: () => "",
      getSearchEndsWith: () => "",
    };

    const results = DBOffLine.search(filter);
    expect(results).toBeArray();
  });
});

describe("Presentation Model", () => {
  it("should create presentation instances and hash properly", () => {
    const p = new Paragraph(null, "Linha de slide", "");
    const pres = Presentation.create(p, Presentation.BLACK_SCREEN.theme, false);

    expect(pres.text).toBe(p);
    expect(pres.separatorOrEnd).toBe(false);
    expect(pres.hash()).toContain("Linha de slide");

    const blank = pres.blank();
    expect(blank).toBeDefined();
    expect(blank.text.getText()).toBe("");

    const wall = pres.wallpaper();
    expect(wall).toBeDefined();

    expect(Presentation.BLACK_SCREEN).toBeDefined();
  });
});

describe("Schedule Model", () => {
  it("should manage date, musics, medias, and staff correctly", () => {
    const date = new Date(2026, 10, 15, 10, 0, 0);
    const schedule = new Schedule("100_culto", date, "Observações gerais", 123, [1, 2], []);

    expect(schedule.id).toBe("100_culto");
    expect(schedule.getDate().getTime()).toBe(date.getTime());
    expect(schedule.getNotes()).toBe("Observações gerais");
    expect(schedule.getResponsible()).toBe(123);

    const member = new Member();
    (member as any).id = 123;
    expect(schedule.isResponsible(member)).toBe(true);

    schedule.setScheduled(member, true);
    expect(schedule.isScheduled(member)).toBe(true);

    const m = Music.create(1);
    expect(schedule.contains(m)).toBe(true);

    const m2 = Music.create(3);
    expect(schedule.contains(m2)).toBe(false);
    schedule.addMusic(m2);
    expect(schedule.contains(m2)).toBe(true);

    const hItem = { getId: () => "item_1" };
    schedule.addMedia(hItem);
    expect(schedule.contains(hItem)).toBe(true);
    expect(schedule.getMedias().length).toBe(1);

    expect(schedule.isActive()).toBe(true);
    expect(schedule.equals(new Schedule("100_CULTO", date))).toBe(true);
  });
});
