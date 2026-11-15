import { describe, expect, it, beforeEach } from "bun:test";
import { Database } from "bun:sqlite";
import * as path from "path";
import * as fs from "fs";
import { FactoryBible, setDatabase } from "../FactoryBible";
import { BibleFind } from "../BibleFind";
import { Bible } from "../Bible";
import { Book } from "../Book";
import { Verse } from "../Verse";

describe("BibleFind Reference Parser", () => {
  it("should parse references correctly", () => {
    const ref1 = BibleFind.parseReference("Jo 3:16");
    expect(ref1).not.toBeNull();
    expect(ref1?.bookQuery).toBe("Jo");
    expect(ref1?.chapter).toBe(3);
    expect(ref1?.verseStart).toBe(16);
    expect(ref1?.verseEnd).toBe(16);

    const ref2 = BibleFind.parseReference("Gn 1");
    expect(ref2).not.toBeNull();
    expect(ref2?.bookQuery).toBe("Gn");
    expect(ref2?.chapter).toBe(1);
    expect(ref2?.verseStart).toBeUndefined();

    const ref3 = BibleFind.parseReference("Ap 2:1-5");
    expect(ref3).not.toBeNull();
    expect(ref3?.bookQuery).toBe("Ap");
    expect(ref3?.chapter).toBe(2);
    expect(ref3?.verseStart).toBe(1);
    expect(ref3?.verseEnd).toBe(5);

    const ref4 = BibleFind.parseReference("1 Jo 1:9");
    expect(ref4).not.toBeNull();
    expect(ref4?.bookQuery).toBe("1 Jo");
    expect(ref4?.chapter).toBe(1);
    expect(ref4?.verseStart).toBe(9);

    const ref5 = BibleFind.parseReference("2 Cr 7:14");
    expect(ref5).not.toBeNull();
    expect(ref5?.bookQuery).toBe("2 Cr");
    expect(ref5?.chapter).toBe(7);
    expect(ref5?.verseStart).toBe(14);

    const ref6 = BibleFind.parseReference("Sl 23:1-6");
    expect(ref6).not.toBeNull();
    expect(ref6?.bookQuery).toBe("Sl");
    expect(ref6?.chapter).toBe(23);
    expect(ref6?.verseStart).toBe(1);
    expect(ref6?.verseEnd).toBe(6);
  });
});

describe("FactoryBible with Real .bib File", () => {
  const bibPath = path.resolve(__dirname, "../../com/limagiran/bible/database/pt_acrf.bib");

  it("should getLanguageAndVersion from .bib file", () => {
    if (!fs.existsSync(bibPath)) return;
    const langAndVer = FactoryBible.getLanguageAndVersion(bibPath);
    expect(langAndVer).toBe("(Português) Almeida Corrigida e Revisada Fiel");
  });

  it("should load Bible instance from .bib file and search/parse references", () => {
    if (!fs.existsSync(bibPath)) return;
    const memDb = new Database(":memory:");
    setDatabase(memDb);

    const bible = FactoryBible.create(bibPath, memDb);
    expect(bible.version).toBe("Almeida Corrigida e Revisada Fiel");
    expect(bible.abbrev).toBe("pt_acrf");
    expect(bible.books.length).toBe(66);
    expect(bible.verses.length).toBeGreaterThan(30000);

    // Test BibleFind book search
    const bookGn = bible.findBookByAbbrev("Gn");
    expect(bookGn).not.toBeNull();
    expect(bookGn?.name).toBe("Gênesis");

    const bookJo = bible.findBookByName("João");
    expect(bookJo).not.toBeNull();
    expect(bookJo?.abbrev).toBe("Jo");

    // Test findVersesByReference
    const versesJo316 = bible.findVersesByReference("Jo 3:16");
    expect(versesJo316.length).toBe(1);
    expect(versesJo316[0]!.content).toContain("Porque Deus amou o mundo");

    const versesAp2Range = bible.findVersesByReference("Ap 2:1-5");
    expect(versesAp2Range.length).toBe(5);
    expect(versesAp2Range[0]!.verse).toBe(1);
    expect(versesAp2Range[4]!.verse).toBe(5);

    // Test SQLite methods
    const v1 = FactoryBible.getVerse(1, 1, 1);
    expect(v1).not.toBeNull();
    expect(v1?.content).toBe("No princípio criou Deus o céu e a terra.");

    const ch1 = FactoryBible.getChapter(1, 1);
    expect(ch1.length).toBe(31);

    const searchResults = FactoryBible.search("princípio criou Deus");
    expect(searchResults.length).toBeGreaterThanOrEqual(1);
    expect(searchResults[0]!.content).toContain("No princípio criou Deus");

    // Test loadMap and loadMapAsList
    const map = bible.getMap();
    expect(map.get(1)?.get(1)).toBe(31);
    const mapList = bible.getMapAsList();
    expect(mapList.length).toBe(66);

    // Test Jó vs João disambiguation
    const bookJob = bible.findBook("Jó");
    expect(bookJob?.id).toBe("18");
    const bookJohn = bible.findBook("Jo");
    expect(bookJohn?.id).toBe("43");

    // Test next/previous verse
    const verse1 = bible.getVerse("01001001", true);
    expect(verse1).not.toBeNull();
    const nextV = bible.nextVerse(verse1!);
    expect(nextV.verse).toBe(2);
    const prevV = bible.previousVerse(nextV);
    expect(prevV.verse).toBe(1);

    // Test VerseFilter search
    const exactMatches = bible.find({
      search: "no princípio criou Deus",
      exactPhrase: true,
    });
    expect(exactMatches.length).toBe(1);
    expect(exactMatches[0]!.getId()).toBe("01001001");

    // Test save and reload
    const tmpFile = path.resolve(__dirname, "../../tmp_test_save.bib");
    try {
      const saved = FactoryBible.save(bible, tmpFile);
      expect(saved).toBe(true);
      expect(fs.existsSync(tmpFile)).toBe(true);

      const reloaded = FactoryBible.create(tmpFile);
      expect(reloaded.version).toBe(bible.version);
      expect(reloaded.verses.length).toBe(bible.verses.length);
    } finally {
      if (fs.existsSync(tmpFile)) {
        fs.unlinkSync(tmpFile);
      }
    }
  });
});
