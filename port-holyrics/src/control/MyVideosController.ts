import VideoHL from "../model/VideoHL.ts";
import ControllerFactory from "./ControllerFactory.ts";

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.control.MyVideosController
 */
export class MyVideosController {
  private static readonly FILE_NAME = "myVideos.hly";
  private static readonly LIST: VideoHL[] = [];

  static {
    const loaded = ControllerFactory.load<VideoHL>(
      MyVideosController.FILE_NAME,
      (data: any) => {
        const vid = new VideoHL(data.fileName, data.hash, data.image, data.gif);
        if (typeof data.id === "number") {
          (vid as any).id = data.id;
        }
        return vid;
      }
    );
    if (loaded && loaded.length > 0) {
      MyVideosController.LIST.push(...loaded);
    }
  }

  public static add(video: VideoHL): void {
    const lastSize = MyVideosController.LIST.length;
    const exists = MyVideosController.LIST.some((v) => v.id === video.id);
    if (!exists) {
      MyVideosController.LIST.push(video);
    }

    if (MyVideosController.LIST.length !== lastSize) {
      MyVideosController.save();
    }
  }

  public static remove(videoOrId: VideoHL | number): void {
    const id = typeof videoOrId === "number" ? videoOrId : videoOrId.id;
    const lastSize = MyVideosController.LIST.length;

    for (let i = MyVideosController.LIST.length - 1; i >= 0; i--) {
      if (MyVideosController.LIST[i]?.id === id) {
        MyVideosController.LIST.splice(i, 1);
      }
    }

    if (MyVideosController.LIST.length !== lastSize) {
      MyVideosController.save();
    }
  }

  public static get(): VideoHL[];
  public static get(id: number): VideoHL | null;
  public static get(id?: number): VideoHL[] | VideoHL | null {
    if (typeof id === "number") {
      for (const v of MyVideosController.LIST) {
        if (v.id === id) {
          return v;
        }
      }
      return null;
    }
    return [...MyVideosController.LIST];
  }

  public static save(): void {
    ControllerFactory.save(MyVideosController.LIST, MyVideosController.FILE_NAME);
  }

  public static fileSize(): number {
    return ControllerFactory.fileSize(MyVideosController.FILE_NAME);
  }
}

export default MyVideosController;
