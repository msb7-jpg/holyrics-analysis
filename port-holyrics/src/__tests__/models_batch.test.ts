import { describe, expect, it } from "bun:test";
import Profile from "../model/Profile.ts";
import Member, { EnumGroups } from "../model/Member.ts";
import History from "../model/History.ts";
import ImageHL from "../model/ImageHL.ts";
import VideoHL from "../model/VideoHL.ts";
import Music from "../model/Music.ts";

describe("Model Port 1:1 Tests", () => {
  describe("Profile", () => {
    it("should manage profile properties and music IDs", () => {
      const p = new Profile("Main Profile");
      expect(p.getName()).toBe("Main Profile");

      p.setName("Updated Profile");
      expect(p.getName()).toBe("Updated Profile");

      p.addIds([1, 2, 3]);
      expect(p.getMusicsId()).toEqual([1, 2, 3]);

      p.removeIds([2]);
      expect(p.getMusicsId()).toEqual([1, 3]);

      p.setMusics([1, 4, 5]);
      expect(p.getMusicsId()).toEqual([1, 4, 5]);

      const m = new Music();
      m.setId(4);
      expect(p.containsMusic(m)).toBe(true);

      const m2 = new Music();
      m2.setId(99);
      expect(p.containsMusic(m2)).toBe(false);

      expect(p.toString()).toBe("Updated Profile");
      expect(p.equals(new Profile("Updated Profile"))).toBe(true);
      expect(p.equals(new Profile("Other"))).toBe(false);
    });
  });

  describe("Member", () => {
    it("should handle member groups and sorting", () => {
      const member = new Member("John", "Vocals & Guitar", [EnumGroups.VOICE, EnumGroups.INSTRUMENT]);
      expect(member.getName()).toBe("John");
      expect(member.getSkills()).toBe("Vocals & Guitar");
      expect(member.isVoice()).toBe(true);
      expect(member.isInstrument()).toBe(true);
      expect(member.isOrchestra()).toBe(false);
      expect(member.isOthers()).toBe(false);
      expect(member.getMinGroup()).toBe(0);

      member.setGroup(EnumGroups.ORCHESTRA, true);
      expect(member.isOrchestra()).toBe(true);

      member.setGroup(EnumGroups.VOICE, false);
      expect(member.isVoice()).toBe(false);
      expect(member.getMinGroup()).toBe(1);

      const mInactive = new Member("Zoe");
      mInactive.setActive(false);
      const mActive = new Member("Adam");
      mActive.setActive(true);

      const sortFn = Member.sortByGroups();
      expect(sortFn(mActive, mInactive)).toBeLessThan(0);
      expect(sortFn(mInactive, mActive)).toBeGreaterThan(0);
    });
  });

  describe("History", () => {
    it("should track music play history", () => {
      const m = new Music();
      m.setId(42);

      const now = new Date();
      History.addHistory(m, now);

      expect(History.count(m)).toBeGreaterThanOrEqual(1);
      expect(History.isPlayed(m, now)).toBe(true);
      expect(History.minutesBetweenLastPlayed(m, now)).toBe(0);
      expect(History.lastPlayed(m)?.getTime()).toBe(now.getTime());
    });
  });

  describe("ImageHL", () => {
    it("should create ImageHL and format id/hash", () => {
      const bytes = new Uint8Array([1, 2, 3]);
      const thumb = new Uint8Array([1]);
      const img = new ImageHL(bytes, thumb);

      expect(img.getId().startsWith("ImageHL")).toBe(true);
      expect(img.bytes).toEqual(bytes);
      expect(img.bytesThumb).toEqual(thumb);
      expect(img.equals(img)).toBe(true);
    });
  });

  describe("VideoHL", () => {
    it("should create VideoHL and manage properties", () => {
      const img = new Uint8Array([1, 2]);
      const gif = new Uint8Array([3, 4]);
      const video = new VideoHL("sample.mp4", "hash123", img, gif);

      expect(video.getId().startsWith("VideoHL")).toBe(true);
      expect(video.fileName).toBe("sample.mp4");
      expect(video.hash).toBe("hash123");
      expect(video.getFile().path).toContain("sample.mp4");
      expect(video.equals(video)).toBe(true);
    });
  });
});
