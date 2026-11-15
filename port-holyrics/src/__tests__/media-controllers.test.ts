import { describe, expect, it, beforeEach } from "bun:test";
import ThemeController from "../control/ThemeController.ts";
import MyImagesController from "../control/MyImagesController.ts";
import MyVideosController from "../control/MyVideosController.ts";
import ThumbMediaController from "../control/ThumbMediaController.ts";
import Theme from "../model/theme/Theme.ts";
import ImageHL from "../model/ImageHL.ts";
import VideoHL from "../model/VideoHL.ts";

describe("ThemeController", () => {
  it("should get default theme and manage themes", () => {
    const defaultTheme = ThemeController.getDefault();
    expect(defaultTheme).toBeDefined();
    expect(ThemeController.getThemes().length).toBeGreaterThan(0);

    const newTheme = Theme.create();
    ThemeController.add(newTheme);
    expect(ThemeController.exists(newTheme.id)).toBe(true);
    expect(ThemeController.get(newTheme.id).id).toBe(newTheme.id);

    ThemeController.remove(newTheme);
    expect(ThemeController.exists(newTheme.id)).toBe(false);
  });

  it("should support getOrDefault", () => {
    const fallback = Theme.create();
    const result = ThemeController.getOrDefault(99999999999, fallback);
    expect(result.id).toBe(fallback.id);
  });
});

describe("MyImagesController", () => {
  it("should add, retrieve and remove images", () => {
    const image = new ImageHL(new Uint8Array([1, 2, 3]), new Uint8Array([1]));
    MyImagesController.add(image);

    const found = MyImagesController.get(image.id);
    expect(found).not.toBeNull();
    expect(found?.id).toBe(image.id);

    const list = MyImagesController.get();
    expect(list.some((img) => img.id === image.id)).toBe(true);

    MyImagesController.remove(image);
    expect(MyImagesController.get(image.id)).toBeNull();
  });
});

describe("MyVideosController", () => {
  it("should add, retrieve and remove videos", () => {
    const video = new VideoHL("test_video.mp4", "hash123", new Uint8Array([1, 2]), new Uint8Array([3, 4]));
    MyVideosController.add(video);

    const found = MyVideosController.get(video.id);
    expect(found).not.toBeNull();
    expect(found?.id).toBe(video.id);
    expect(found?.fileName).toBe("test_video.mp4");

    const list = MyVideosController.get();
    expect(list.some((v) => v.id === video.id)).toBe(true);

    MyVideosController.remove(video.id);
    expect(MyVideosController.get(video.id)).toBeNull();
  });
});

describe("ThumbMediaController", () => {
  it("should manage thumbnails cache", () => {
    const pathKey = "sample/path/image.jpg";
    const thumbData = new Uint8Array([10, 20, 30]);

    const created = ThumbMediaController.getOrCreate(pathKey, 1000, () => thumbData, false);
    expect(created.thumb).toEqual(thumbData);
    expect(ThumbMediaController.contains(pathKey)).toBe(true);

    const retrieved = ThumbMediaController.getOrDefault(pathKey, null);
    expect(retrieved).toEqual(thumbData);

    ThumbMediaController.remove(pathKey);
    expect(ThumbMediaController.contains(pathKey)).toBe(false);
  });

  it("should handle error flags", () => {
    const errId = "error-item-1";
    expect(ThumbMediaController.checkError(errId)).toBe(false);
    ThumbMediaController.addError(errId);
    expect(ThumbMediaController.checkError(errId)).toBe(true);
  });

  it("should parse youtube id properly", () => {
    const url = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    expect(ThumbMediaController.extractYouTubeId(url)).toBe("dQw4w9WgXcQ");
  });
});
