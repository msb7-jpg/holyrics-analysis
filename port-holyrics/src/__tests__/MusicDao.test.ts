import { describe, it, expect, beforeEach } from "bun:test";
import { Database } from "bun:sqlite";
import { MusicDao, setDatabase } from "../dao/MusicDao";
import { Music } from "../model/Music";

describe("MusicDao (bun:sqlite)", () => {
  beforeEach(() => {
    // Fresh in-memory sqlite db for each test
    const db = new Database(":memory:");
    setDatabase(db);
  });

  it("should save and find music by id", () => {
    const music = new Music(0, "Artist A", "Title A", "Lyrics content");
    const saved = MusicDao.save(music);
    expect(saved).toBe(true);
    expect(music.getId()).toBeGreaterThan(0);

    const found = MusicDao.find(music.getId());
    expect(found).not.toBeNull();
    expect(found?.getTitle()).toBe("Title A");
    expect(found?.getArtist()).toBe("Artist A");
    expect(found?.getLyrics()).toBe("Lyrics content");
  });

  it("should findAll returns all saved musics", () => {
    const m1 = new Music(0, "Artist 1", "Song 1", "Lyrics 1");
    const m2 = new Music(0, "Artist 2", "Song 2", "Lyrics 2");
    MusicDao.save(m1);
    MusicDao.save(m2);

    const all = MusicDao.findAll();
    expect(all.length).toBe(2);
    expect(all.some((m) => m.getTitle() === "Song 1")).toBe(true);
    expect(all.some((m) => m.getTitle() === "Song 2")).toBe(true);
  });

  it("should update existing music", () => {
    const music = new Music(0, "Artist Old", "Title Old", "Lyrics Old");
    MusicDao.save(music);

    music.setTitle("Title Updated");
    music.setLyrics("Lyrics Updated");
    const updated = MusicDao.update(music);
    expect(updated).toBe(true);

    const found = MusicDao.find(music.getId());
    expect(found?.getTitle()).toBe("Title Updated");
    expect(found?.getLyrics()).toBe("Lyrics Updated");
  });

  it("should delete music by id", () => {
    const music = new Music(0, "Artist Del", "Title Del", "Lyrics Del");
    MusicDao.save(music);
    const id = music.getId();

    expect(MusicDao.find(id)).not.toBeNull();
    const deleted = MusicDao.delete(id);
    expect(deleted).toBe(true);

    expect(MusicDao.find(id)).toBeNull();
    expect(MusicDao.findAll().length).toBe(0);
  });

  it("should support instance methods as well", () => {
    const dao = new MusicDao();
    const music = new Music(0, "Artist Inst", "Title Inst", "Lyrics Inst");

    expect(dao.save(music)).toBe(true);
    const found = dao.find(music.getId());
    expect(found?.getTitle()).toBe("Title Inst");

    music.setTitle("Title Inst 2");
    expect(dao.update(music)).toBe(true);
    expect(dao.find(music.getId())?.getTitle()).toBe("Title Inst 2");

    expect(dao.findAll().length).toBe(1);
    expect(dao.delete(music.getId())).toBe(true);
    expect(dao.findAll().length).toBe(0);
  });

  it("should support contains, getLazy, and search functionality", () => {
    const music = new Music(0, "Rosa de Saron", "Do Alto da Pedra", "Letra");
    MusicDao.save(music);

    expect(MusicDao.contains("Do Alto da Pedra", "Rosa de Saron")).toBe(true);
    expect(MusicDao.contains("do alto da pedra", "rosa de saron")).toBe(true);
    expect(MusicDao.contains(music)).toBe(true);

    const lazy = MusicDao.getLazy(music.getId());
    expect(lazy).not.toBeNull();
    expect(lazy?.getTitle()).toBe("Do Alto da Pedra");

    const lazySearch = MusicDao.getLazyToSearch(music.getId());
    expect(lazySearch).not.toBeNull();
  });
});
