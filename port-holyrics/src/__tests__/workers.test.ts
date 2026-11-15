import { describe, it, expect, beforeEach } from "bun:test";
import { Database } from "bun:sqlite";
import { MusicDao, setDatabase } from "../dao/MusicDao";
import { Music } from "../model/Music";
import { MusicImportWorker, DefaultMusicParser } from "../worker/MusicImportWorker";
import { BatchImportWorker } from "../worker/BatchImportWorker";

describe("MusicImportWorker and BatchImportWorker", () => {
  beforeEach(() => {
    // Fresh in-memory database for each test
    const db = new Database(":memory:");
    setDatabase(db);
  });

  describe("MusicImportWorker", () => {
    it("should process files and emit progress updates", async () => {
      const progressUpdates: { val: number; msg: string }[] = [];

      const file1 = {
        name: "song1.muf",
        content: JSON.stringify({
          title: "Amazing Grace",
          artist: "John Newton",
          lyrics: "Amazing grace how sweet the sound",
        }),
      };

      const file2 = {
        name: "song2.txt",
        content: "How Great Thou Art\nStuart K. Hine\nO Lord my God",
      };

      const invalidFile = {
        name: "empty.txt",
        content: "",
      };

      const worker = new MusicImportWorker({
        type: DefaultMusicParser,
        files: [file1, file2, invalidFile],
        onProgress: (val, msg) => {
          progressUpdates.push({ val, msg });
        },
      });

      const response = await worker.execute();

      expect(response.musics.length).toBe(2);
      expect(response.error.length).toBe(1);
      expect(progressUpdates.length).toBeGreaterThan(0);
      expect(response.musics[0]!.getTitle()).toBe("Amazing Grace");
    });
  });

  describe("BatchImportWorker", () => {
    it("should import .muf and .mufl, handle duplicates and add to MusicDao", async () => {
      // First preload a music in MusicDao to test duplicate detection
      const existing = new Music();
      existing.setTitle("Existing Song");
      existing.setArtist("Existing Artist");
      MusicDao.add(existing);

      const mufSong = {
        title: "Existing Song",
        artist: "Existing Artist",
        lyrics: "duplicate lyrics",
      };

      const mufNewSong = {
        title: "New Song 1",
        artist: "Artist 1",
        lyrics: "Verse 1",
      };

      const muflSongs = [
        {
          title: "New Song 2",
          artist: "Artist 2",
          lyrics: "Verse 2",
        },
        {
          title: "New Song 3",
          artist: "Artist 3",
          lyrics: "Verse 3",
        },
      ];

      const files = [
        {
          name: "existing.muf",
          content: JSON.stringify(mufSong),
        },
        {
          name: "new1.muf",
          content: JSON.stringify(mufNewSong),
        },
        {
          name: "songs.mufl",
          content: JSON.stringify(muflSongs),
        },
      ];

      const progressLogs: { val: number; msg: string }[] = [];
      const profileAdded: Music[] = [];
      let duplicatesFoundCount = 0;

      const result = await BatchImportWorker.run({
        files,
        onProgress: (val, msg) => {
          progressLogs.push({ val, msg });
        },
        addToProfiles: {
          addMusicsInCheckedProfiles: (musics) => {
            profileAdded.push(...musics);
          },
        },
        onDuplicatesFound: (duplicates) => {
          duplicatesFoundCount = duplicates.length;
        },
      });

      expect(result.totalParsed).toBe(4);
      expect(result.alreadyAdded.length).toBe(1);
      expect(result.alreadyAdded[0]!.getTitle()).toBe("Existing Song");
      expect(result.imported.length).toBe(3);
      expect(duplicatesFoundCount).toBe(1);
      expect(profileAdded.length).toBe(3);
      expect(progressLogs.length).toBeGreaterThan(0);

      // Verify records are saved in MusicDao
      const allInDao = MusicDao.getAll();
      expect(allInDao.length).toBe(4); // 1 preloaded + 3 new
      expect(MusicDao.contains("New Song 1", "Artist 1")).toBe(true);
      expect(MusicDao.contains("New Song 2", "Artist 2")).toBe(true);
      expect(MusicDao.contains("New Song 3", "Artist 3")).toBe(true);
    });

    it("should handle plain text fallback and error files", async () => {
      const files = [
        {
          name: "plain.muf",
          content: "Plain Song Title\nPlain Artist\nVerse line 1\nVerse line 2",
        },
        {
          name: "empty.muf",
          content: "",
        },
      ];

      const result = await BatchImportWorker.run({ files });
      expect(result.totalParsed).toBe(1);
      expect(result.imported.length).toBe(1);
      expect(result.imported[0]!.getTitle()).toBe("Plain Song Title");
      expect(result.imported[0]!.getArtist()).toBe("Plain Artist");
      expect(result.errors.length).toBe(1);
      expect(result.errors[0]!.file).toBe("empty.muf");
    });
  });
});
