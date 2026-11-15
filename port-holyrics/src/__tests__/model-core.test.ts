import { describe, expect, test } from "bun:test";
import { Account } from "../model/Account.ts";
import { Event, ScheduleController } from "../model/Event.ts";
import { Music, Paragraph, EnumSlideType } from "../model/Music.ts";
import { Worship, WorshipDay, WorshipWeek } from "../model/Worship.ts";

describe("Account", () => {
  test("creates account, getters, setters, validation, serialization", () => {
    const acc = new Account("admin", "12345", "sec123");
    expect(acc.getUsername()).toBe("admin");
    expect(acc.getAccessKey()).toBe("12345");
    expect(acc.getSecurity()).toBe("sec123");
    expect(() => acc.validate()).not.toThrow();

    acc.setUsername("abc");
    expect(() => acc.validate()).toThrow();

    acc.clear();
    expect(acc.getUsername()).toBe("");
    expect(acc.getAccessKey()).toBe("");
    expect(acc.getSecurity()).toBe("");

    const acc2 = Account.create("user", "pass", "secu");
    acc.set(acc2);
    expect(acc.getUsername()).toBe("user");

    const json = JSON.stringify(acc.toJSON());
    const deserialized = Account.fromJSON(json);
    expect(deserialized.getUsername()).toBe("user");
    expect(deserialized.getAccessKey()).toBe("pass");
  });
});

describe("Event", () => {
  test("creates event, date handling, sort, equals, serialization", () => {
    const date = new Date(2026, 10, 15, 10, 30, 45, 123);
    const event = new Event("id1", "Sunday Service", date, "Description");
    expect(event.getId()).toBe("id1");
    expect(event.getName()).toBe("Sunday Service");
    expect(event.getDesc()).toBe("Description");
    expect(event.getDate().getSeconds()).toBe(0);
    expect(event.getDate().getMilliseconds()).toBe(0);

    let changed = false;
    ScheduleController.setListener(() => {
      changed = true;
    });
    const newDate = new Date(2026, 10, 15, 11, 0, 0);
    event.setDate(newDate);
    expect(changed).toBe(true);

    const event2 = new Event(new Date());
    expect(event.equals(new Event("id1", "Other", new Date(), ""))).toBe(true);
    expect(event.equals(event2)).toBe(false);

    const sortFn = Event.sort();
    const e1 = new Event("1", "E1", new Date(2026, 0, 1, 10, 0), "");
    const e2 = new Event("2", "E2", new Date(2026, 0, 2, 10, 0), "");
    expect(sortFn(e1, e2)).toBeLessThan(0);

    const json = JSON.stringify(event.toJSON());
    const fromJson = Event.fromJSON(json);
    expect(fromJson.getId()).toBe("id1");
    expect(fromJson.getName()).toBe("Sunday Service");
  });
});

describe("Music", () => {
  test("creates music, lyrics, paragraphs, order, presentation", () => {
    const m = Music.create(10)
      .setTitle("Amazing Grace")
      .setArtist("John Newton")
      .setLyrics("Verse 1 line 1\nVerse 1 line 2\n\nVerse 2 line 1\nVerse 2 line 2")
      .setNote("Key of G")
      .setFormatting("##m\n##n");

    expect(m.getId()).toBe(10);
    expect(m.getTitle()).toBe("Amazing Grace");
    expect(m.getArtist()).toBe("John Newton");
    expect(m.getFormat(0)).toBe("##m");
    expect(m.getFormat(1)).toBe("##n");

    const paragraphs = m.getParagraphs();
    expect(paragraphs.length).toBe(2);

    const initP = m.getInitialParagraph();
    expect(initP.getText()).toBe("Verse 1 line 1\nVerse 1 line 2");

    m.setOrder([paragraphs[1]!, paragraphs[0]!]);
    expect(m.getOrder()).toEqual([1, 0]);
    expect(m.getParagraphsInOrder()).toEqual([paragraphs[1]!, paragraphs[0]!]);

    const pres = m.getParagraphsToPresentation();
    expect(pres.length).toBe(2);
    expect(pres[0]!.getPageCountFormated()).toBe("1/2");

    const searchInst = m.getInstanceToSearch();
    expect(searchInst.getTitle()).toBe("amazing grace");

    const json = JSON.stringify(m.toJSON());
    const fromJson = Music.fromJSON(json);
    expect(fromJson.getTitle()).toBe("Amazing Grace");
    expect(fromJson.getOrder()).toEqual([1, 0]);
  });
});

describe("Worship", () => {
  test("creates worship, time formatting, sort, equals, serialization", () => {
    const w1 = new Worship(WorshipDay.SUN, WorshipWeek.ALL, 19, 30, "Culto da Família");
    expect(w1.getTime()).toBe("19:30");
    expect(w1.toString()).toContain("Domingo - 19:30 (Culto da Família)");

    const w2 = new Worship(WorshipDay.SUN, WorshipWeek.FIRST, 9, 0, "Manhã");
    expect(w2.toString()).toContain("Primeiro(a) Domingo - 09:00 (Manhã)");

    const sortFn = Worship.sort();
    expect(sortFn(w1, w2)).toBeLessThan(0); // ALL (0) before FIRST (1)

    const json = JSON.stringify(w1.toJSON());
    const fromJson = Worship.fromJSON(json);
    expect(fromJson.day.key).toBe("SUN");
    expect(fromJson.week.key).toBe("ALL");
    expect(fromJson.hour).toBe(19);
    expect(fromJson.minute).toBe(30);
    expect(fromJson.desc).toBe("Culto da Família");
  });
});
