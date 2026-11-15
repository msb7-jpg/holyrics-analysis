import { describe, expect, it } from "bun:test";
import { Utils } from "../util/Utils";
import { G2Utils, VerticalAlign } from "../util/G2Utils";
import { HolyricsStreaming } from "../util/HolyricsStreaming";

describe("Utils", () => {
  it("range correctly constrains values", () => {
    expect(Utils.range(5, 0, 10)).toBe(5);
    expect(Utils.range(-5, 0, 10)).toBe(0);
    expect(Utils.range(15, 0, 10)).toBe(10);
  });

  it("generateMillisecondId increases monotonically", () => {
    const id1 = Utils.generateMillisecondId();
    const id2 = Utils.generateMillisecondId();
    expect(id2).toBeGreaterThanOrEqual(id1);
  });

  it("removeAccent removes diacritics", () => {
    expect(Utils.removeAccent("ação e bênção")).toBe("acao e bencao");
  });

  it("random and lowPassRandom generate expected formats", () => {
    const r = Utils.random(10);
    expect(r).toBeGreaterThanOrEqual(1);
    expect(r).toBeLessThanOrEqual(10);

    const lowPass = Utils.lowPassRandom(6);
    expect(lowPass.length).toBe(6);
  });

  it("decodeColor handles hex and rgb correctly", () => {
    expect(Utils.decodeColor("#ff0000")).toEqual({ r: 255, g: 0, b: 0, a: 255 });
    expect(Utils.decodeColor("10,20,30,40")).toEqual({ r: 10, g: 20, b: 30, a: 40 });
  });

  it("firstLetterToUpperCase capitalizes words ignoring connectives", () => {
    expect(Utils.firstLetterToUpperCase("casa de praia")).toBe("Casa de Praia");
  });
});

describe("G2Utils", () => {
  it("computes vertical alignment correctly", () => {
    expect(G2Utils.getVerticalAlign(-1)).toBe(VerticalAlign.TOP);
    expect(G2Utils.getVerticalAlign(0)).toBe(VerticalAlign.MIDDLE);
    expect(G2Utils.getVerticalAlign(1)).toBe(VerticalAlign.BOTTOM);
  });

  it("adjustString calculates positions correctly", () => {
    const rect = { x: 0, y: 0, width: 200, height: 100 };
    const mockFm = {
      getAscent: () => 10,
      getLeading: () => 2,
      getHeight: () => 14,
      stringWidth: (_s: string) => 50,
    };

    const points = G2Utils.adjustString(rect, 0.5, mockFm, VerticalAlign.MIDDLE, ["Line 1", "Line 2"]);
    expect(points.length).toBe(2);
    expect(points[0]?.x).toBe(75); // (200 - 50) / 2
    expect(points[1]?.x).toBe(75);
    expect(points[1]?.y).toBeGreaterThan(points[0]?.y ?? 0);
  });

  it("getBestFontSize finds appropriate size", () => {
    const best = G2Utils.getBestFontSize(
      100,
      50,
      "Test",
      (fontSize) => ({ width: fontSize * 5, height: fontSize })
    );
    expect(best).toBeLessThanOrEqual(20);
    expect(best).toBeGreaterThan(0);
  });
});

describe("HolyricsStreaming", () => {
  it("tracks renderer and demand flags", () => {
    HolyricsStreaming.clear();
    expect(HolyricsStreaming.isDemand()).toBe(false);

    HolyricsStreaming.request(true);
    expect(HolyricsStreaming.isDemandWidescreen()).toBe(true);
    expect(HolyricsStreaming.isDemand()).toBe(true);

    const mockRenderer = {
      renderer: () => "mock-img-base64",
      getHash: () => "hash-123",
    };
    HolyricsStreaming.setRenderer(mockRenderer);
    expect(HolyricsStreaming.getRenderer()).toBe(mockRenderer);

    HolyricsStreaming.updateStreaming();
    const pack = HolyricsStreaming.getStreamingPack(true);
    expect(pack.success).toBe(true);
    expect(pack.image).toBe("mock-img-base64");
  });
});
