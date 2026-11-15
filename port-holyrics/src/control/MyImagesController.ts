import ImageHL from "../model/ImageHL.ts";
import ControllerFactory from "./ControllerFactory.ts";

/**
 * Isomorphic TypeScript port of com.limagiran.holyrics.control.MyImagesController
 */
export class MyImagesController {
  private static readonly FILE_NAME = "myImages.hly";
  private static readonly LIST: ImageHL[] = [];

  static {
    const loaded = ControllerFactory.load<ImageHL>(
      MyImagesController.FILE_NAME,
      (data: any) => {
        const img = new ImageHL(data.bytes, data.bytesThumb);
        if (typeof data.id === "number") {
          (img as any).id = data.id;
        }
        return img;
      }
    );
    if (loaded && loaded.length > 0) {
      MyImagesController.LIST.push(...loaded);
    }
  }

  public static add(image: ImageHL): void {
    const lastSize = MyImagesController.LIST.length;
    const exists = MyImagesController.LIST.some((i) => i.id === image.id);
    if (!exists) {
      MyImagesController.LIST.push(image);
    }

    if (MyImagesController.LIST.length !== lastSize) {
      MyImagesController.save();
    }
  }

  public static remove(imageOrId: ImageHL | number): void {
    const id = typeof imageOrId === "number" ? imageOrId : imageOrId.id;
    const lastSize = MyImagesController.LIST.length;

    for (let i = MyImagesController.LIST.length - 1; i >= 0; i--) {
      if (MyImagesController.LIST[i]?.id === id) {
        MyImagesController.LIST.splice(i, 1);
      }
    }

    if (MyImagesController.LIST.length !== lastSize) {
      MyImagesController.save();
    }
  }

  public static get(): ImageHL[];
  public static get(id: number): ImageHL | null;
  public static get(id?: number): ImageHL[] | ImageHL | null {
    if (typeof id === "number") {
      for (const i of MyImagesController.LIST) {
        if (i.id === id) {
          return i;
        }
      }
      return null;
    }
    return [...MyImagesController.LIST];
  }

  public static save(): void {
    ControllerFactory.save(MyImagesController.LIST, MyImagesController.FILE_NAME);
  }

  public static fileSize(): number {
    return ControllerFactory.fileSize(MyImagesController.FILE_NAME);
  }
}

export default MyImagesController;
