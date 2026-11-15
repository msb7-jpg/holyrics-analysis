export interface IRemoteControlBibleWS {
  connect(key: string): Promise<boolean> | boolean;
  isConnected(): Promise<boolean> | boolean;
  getMapChaptersAndVerses(): Promise<string> | string;
  goTo(book: number, chapter: number, verse: number): Promise<string> | string;
  getCurrentID_VersePreviousAndNextVerse(): Promise<string> | string;
  show(book: number, chapter: number, verse: number): Promise<string> | string;
  exit(): Promise<void> | void;
  next(book: number, chapter: number, verse: number, visible: boolean): Promise<string> | string;
  previous(book: number, chapter: number, verse: number, visible: boolean): Promise<string> | string;
  webService(pack: string): Promise<string> | string;
  ws(pack: string): Promise<string> | string;
}
