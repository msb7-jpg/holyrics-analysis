import { Pack } from './Pack';
import type { IRemoteControlBibleWS } from './IRemoteControlBibleWS';

export interface BibleVerseInfo {
  id: string; // 8 chars e.g. "01001001"
  book: number;
  chapter: number;
  verse: number;
  text: string;
}

export interface RemoteControlBibleWSHooks {
  getPasswordBible?: () => string;
  getMapChaptersAndVerses?: () => Record<string, any>;
  getVerse?: (book: number, chapter: number, verse: number) => BibleVerseInfo | null;
  getCurrentVerse?: () => BibleVerseInfo | null;
  showVerse?: (verse: BibleVerseInfo) => void;
  onExit?: () => void;
  onNextVerse?: (verse: BibleVerseInfo, visible: boolean) => BibleVerseInfo | null;
  onPreviousVerse?: (verse: BibleVerseInfo, visible: boolean) => BibleVerseInfo | null;
  onBlackScreen?: (black: boolean) => void;
  onBlankScreen?: (blank: boolean) => void;
  isBlack?: () => boolean;
  isBlank?: () => boolean;
  onAddHistory?: (verseIds: string[]) => void;
  getDisplayedVersesCount?: () => number;
  getTabVersions?: () => string[];
  setDisplayedVersesAndVersion?: (displayed: number, tabVersion: number) => void;
}

export class RemoteControlBibleWS implements IRemoteControlBibleWS {
  public static readonly DEFAULT_PORT = 777;
  public static readonly WS_PATH = '/remoteControlBibleWS';
  private static readonly VERSE_ERROR = '01001001Error\n\nError\n\nError';

  private static instance: RemoteControlBibleWS | null = null;
  private static serverInstance: any = null;
  private static isServerRunning: boolean = false;
  private static currentIP: string = '0.0.0.0';

  private hooks: RemoteControlBibleWSHooks = {};
  private currentVerse: BibleVerseInfo = {
    id: '01001001',
    book: 1,
    chapter: 1,
    verse: 1,
    text: 'No princípio, criou Deus os céus e a terra.',
  };
  private isBlackScreenState: boolean = false;
  private isBlankScreenState: boolean = false;
  private displayedVersesCount: number = 1;
  private tabVersions: string[] = ['ARC', 'NVI'];

  constructor(hooks?: RemoteControlBibleWSHooks) {
    if (hooks) {
      this.hooks = hooks;
    }
  }

  public static getInstance(): RemoteControlBibleWS {
    if (!RemoteControlBibleWS.instance) {
      RemoteControlBibleWS.instance = new RemoteControlBibleWS();
    }
    return RemoteControlBibleWS.instance;
  }

  public setHooks(hooks: RemoteControlBibleWSHooks): void {
    this.hooks = { ...this.hooks, ...hooks };
  }

  public static async open(port: number = RemoteControlBibleWS.DEFAULT_PORT, host: string = '0.0.0.0'): Promise<void> {
    RemoteControlBibleWS.close();
    const wsHandler = RemoteControlBibleWS.getInstance();
    RemoteControlBibleWS.currentIP = host;

    if (typeof Bun !== 'undefined') {
      RemoteControlBibleWS.serverInstance = Bun.serve({
        port,
        hostname: host,
        async fetch(req: Request, server: any) {
          const url = new URL(req.url);

          // WebSocket upgrade
          if (req.headers.get('upgrade')?.toLowerCase() === 'websocket') {
            const success = server.upgrade(req, { data: { path: url.pathname } });
            if (success) return undefined;
            return new Response('WebSocket upgrade failed', { status: 400 });
          }

          // CORS / OPTIONS
          if (req.method === 'OPTIONS') {
            return new Response(null, {
              headers: {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
                'Access-Control-Allow-Headers': 'Content-Type',
              },
            });
          }

          // HTTP POST / GET
          try {
            let bodyText = '';
            if (req.method === 'POST') {
              bodyText = await req.text();
            } else {
              bodyText = url.searchParams.get('pack') || '';
            }

            const responseText = await wsHandler.handleHttpRequest(url.pathname, req.method, bodyText, url.searchParams);
            return new Response(responseText, {
              headers: {
                'Content-Type': 'application/json; charset=utf-8',
                'Access-Control-Allow-Origin': '*',
              },
            });
          } catch (err: any) {
            return new Response(Pack.create(false, err?.message || 'exception').toJSon(), {
              status: 500,
              headers: { 'Content-Type': 'application/json; charset=utf-8' },
            });
          }
        },
        websocket: {
          async message(ws: any, message: string | Buffer) {
            try {
              const msgStr = typeof message === 'string' ? message : Buffer.from(message).toString('utf8');
              const res = await wsHandler.handleSocketMessage(msgStr);
              ws.send(res);
            } catch {
              ws.send(Pack.create(false, 'exception').toJSon());
            }
          },
          open(ws: any) {},
          close(ws: any) {},
        },
      });
      RemoteControlBibleWS.isServerRunning = true;
    }
  }

  public static close(): void {
    if (RemoteControlBibleWS.serverInstance) {
      if (typeof RemoteControlBibleWS.serverInstance.stop === 'function') {
        RemoteControlBibleWS.serverInstance.stop();
      }
      RemoteControlBibleWS.serverInstance = null;
    }
    RemoteControlBibleWS.isServerRunning = false;
  }

  public static isOpen(): boolean {
    return RemoteControlBibleWS.isServerRunning && RemoteControlBibleWS.serverInstance !== null;
  }

  public checkPassword(password: string): boolean {
    const configuredPassword = this.hooks.getPasswordBible?.() || '';
    return configuredPassword === '' || password === configuredPassword;
  }

  public checkPasswordPack(p: Pack): boolean {
    return this.checkPassword(p.getString('password', ''));
  }

  public async handleHttpRequest(pathname: string, method: string, body: string, searchParams: URLSearchParams): Promise<string> {
    const action = searchParams.get('action') || searchParams.get('request') || '';

    if (action === 'connect') {
      return JSON.stringify({ ok: this.connect(searchParams.get('key') || '') });
    }
    if (action === 'getMapChaptersAndVerses') {
      return this.getMapChaptersAndVerses();
    }
    if (action === 'goTo') {
      const book = parseInt(searchParams.get('book') || '1', 10);
      const chapter = parseInt(searchParams.get('chapter') || '1', 10);
      const verse = parseInt(searchParams.get('verse') || '1', 10);
      return this.goTo(book, chapter, verse);
    }
    if (action === 'show') {
      const book = parseInt(searchParams.get('book') || '1', 10);
      const chapter = parseInt(searchParams.get('chapter') || '1', 10);
      const verse = parseInt(searchParams.get('verse') || '1', 10);
      return this.show(book, chapter, verse);
    }
    if (action === 'exit') {
      this.exit();
      return JSON.stringify({ ok: true });
    }
    if (action === 'ws') {
      return this.ws(body || searchParams.get('pack') || '');
    }

    return this.webService(body || searchParams.get('pack') || '');
  }

  public async handleSocketMessage(message: string): Promise<string> {
    try {
      const p = Pack.create(message);
      // Support both webService2 & webService3 (ws) routing
      if (p.getString('password', '') !== '' || p.getString('request', '') !== '') {
        return this.ws(message);
      }
      return this.webService(message);
    } catch {
      return Pack.create(false, 'exception').toJSon();
    }
  }

  // --- IRemoteControlBibleWS Implementation ---

  public isConnected(): boolean {
    return true;
  }

  public connect(key: string): boolean {
    return this.checkPassword(key);
  }

  public getMapChaptersAndVerses(): string {
    try {
      if (this.hooks.getMapChaptersAndVerses) {
        return JSON.stringify(this.hooks.getMapChaptersAndVerses());
      }
      const map: Record<number, Record<number, number>> = {};
      map[1] = { 1: 31, 2: 25 };
      return JSON.stringify(map);
    } catch {
      return '';
    }
  }

  public goTo(book: number, chapter: number, verse: number): string {
    try {
      const v = this.resolveVerse(book, chapter, verse);
      return this.formatReturn(v);
    } catch {
      return RemoteControlBibleWS.VERSE_ERROR;
    }
  }

  public getCurrentID_VersePreviousAndNextVerse(): string {
    const v = this.hooks.getCurrentVerse ? this.hooks.getCurrentVerse() || this.currentVerse : this.currentVerse;
    return this.formatReturn(v);
  }

  public show(book: number, chapter: number, verse: number): string {
    const v = this.resolveVerse(book, chapter, verse);
    this.currentVerse = v;
    this.hooks.showVerse?.(v);
    return this.formatReturn(v);
  }

  public exit(): void {
    this.hooks.onExit?.();
  }

  public next(book: number, chapter: number, verse: number, visible: boolean): string {
    const cur = this.resolveVerse(book, chapter, verse);
    let v: BibleVerseInfo;
    if (this.hooks.onNextVerse) {
      v = this.hooks.onNextVerse(cur, visible) || this.computeNextVerse(cur);
    } else {
      v = this.computeNextVerse(cur);
    }
    this.currentVerse = v;
    if (visible) {
      this.hooks.showVerse?.(v);
    }
    return this.formatReturn(v);
  }

  public previous(book: number, chapter: number, verse: number, visible: boolean): string {
    const cur = this.resolveVerse(book, chapter, verse);
    let v: BibleVerseInfo;
    if (this.hooks.onPreviousVerse) {
      v = this.hooks.onPreviousVerse(cur, visible) || this.computePreviousVerse(cur);
    } else {
      v = this.computePreviousVerse(cur);
    }
    this.currentVerse = v;
    if (visible) {
      this.hooks.showVerse?.(v);
    }
    return this.formatReturn(v);
  }

  public webService(pack: string): string {
    const p = this.webService2(pack);
    p.put('wb', true);
    return p.toJSon();
  }

  public ws(pack: string): string {
    try {
      const p = Pack.create(pack);
      if (!this.checkPasswordPack(p)) {
        return Pack.create(false, 'invalid_password').toJSon();
      }
      return this.webService3(p).toJSon();
    } catch {
      return Pack.create(false, 'exception').toJSon();
    }
  }

  private webService2(pack: string): Pack {
    const p = Pack.create(pack);
    const req = p.getString('request', '');
    switch (req) {
      case 'sendHistory':
        return this.sendHistory(p);
      case 'getDisplayedVersesAndVersion':
        return this.getDisplayedVersesAndVersion();
      case 'setDisplayedVersesAndVersion':
        return this.setDisplayedVersesAndVersion(p);
      case 'blackScreen':
        return this.blackScreen(p);
      case 'blankScreen':
        return this.blankScreen(p);
      default:
        return Pack.create(false, 'not_found');
    }
  }

  private webService3(p: Pack): Pack {
    try {
      const req = p.getString('request', '');
      switch (req) {
        case 'getMapChaptersAndVerses':
          return this.fillMapChaptersAndVerses(Pack.create(true));
        case 'getVerse':
          return this.getVerseAction(p);
        case 'getCurrentVerse':
          return this.getCurrentVerseAction();
        case 'show':
          return this.show2(p);
        case 'exit':
          return this.exit2();
        case 'previous':
          return this.previous2(p);
        case 'next':
          return this.next2(p);
        case 'getDisplayedVersesAndVersion':
          return this.getDisplayedVersesAndVersion();
        case 'setDisplayedVersesAndVersion':
          return this.setDisplayedVersesAndVersion2(p);
        case 'blackScreen':
          return this.blackScreen(p);
        case 'blankScreen':
          return this.blankScreen(p);
        default:
          return Pack.create(false, 'not_found');
      }
    } catch {
      return Pack.create(false, 'exception');
    }
  }

  private fillMapChaptersAndVerses(pack: Pack): Pack {
    if (this.hooks.getMapChaptersAndVerses) {
      const map = this.hooks.getMapChaptersAndVerses();
      let i = 1;
      for (const [k, v] of Object.entries(map)) {
        pack.put(`b${i}`, JSON.stringify(v));
        i++;
      }
    } else {
      pack.put('b1', '{"1":31,"2":25}');
    }
    return pack;
  }

  private resolveID(p: Pack): [number, number, number] {
    try {
      const id = p.getString('id', '');
      if (id.length >= 8) {
        const b = parseInt(id.substring(0, 2), 10) || 1;
        const c = parseInt(id.substring(2, 5), 10) || 1;
        const v = parseInt(id.substring(5, 8), 10) || 1;
        return [b, c, v];
      }
      return [p.getInt('book', 1), p.getInt('chapter', 1), p.getInt('verse', 1)];
    } catch {
      return [1, 1, 1];
    }
  }

  private getVerseAction(p: Pack): Pack {
    const [b, c, v] = this.resolveID(p);
    const verse = this.resolveVerse(b, c, v);
    return this.fillReturnVersesPack(Pack.create(true), verse);
  }

  private getCurrentVerseAction(): Pack {
    const v = this.hooks.getCurrentVerse ? this.hooks.getCurrentVerse() || this.currentVerse : this.currentVerse;
    return this.fillReturnVersesPack(Pack.create(true), v);
  }

  private show2(p: Pack): Pack {
    const [b, c, v] = this.resolveID(p);
    const verse = this.resolveVerse(b, c, v);
    this.currentVerse = verse;
    this.hooks.showVerse?.(verse);
    return this.fillReturnVersesPack(Pack.create(true), verse);
  }

  private exit2(): Pack {
    this.exit();
    return Pack.create(true);
  }

  private previous2(p: Pack): Pack {
    const [b, c, v] = this.resolveID(p);
    const visible = p.getBoolean('show');
    const cur = this.resolveVerse(b, c, v);
    let verse: BibleVerseInfo;
    if (this.hooks.onPreviousVerse) {
      verse = this.hooks.onPreviousVerse(cur, visible) || this.computePreviousVerse(cur);
    } else {
      verse = this.computePreviousVerse(cur);
    }
    this.currentVerse = verse;
    if (visible) {
      this.hooks.showVerse?.(verse);
    }
    return this.fillReturnVersesPack(Pack.create(true), verse);
  }

  private next2(p: Pack): Pack {
    const [b, c, v] = this.resolveID(p);
    const visible = p.getBoolean('show');
    const cur = this.resolveVerse(b, c, v);
    let verse: BibleVerseInfo;
    if (this.hooks.onNextVerse) {
      verse = this.hooks.onNextVerse(cur, visible) || this.computeNextVerse(cur);
    } else {
      verse = this.computeNextVerse(cur);
    }
    this.currentVerse = verse;
    if (visible) {
      this.hooks.showVerse?.(verse);
    }
    return this.fillReturnVersesPack(Pack.create(true), verse);
  }

  private sendHistory(p: Pack): Pack {
    try {
      const list = p.getString('list', '').split(';').filter(Boolean);
      this.hooks.onAddHistory?.(list);
      return Pack.create(true);
    } catch {
      return Pack.create(false, 'exception');
    }
  }

  private getDisplayedVersesAndVersion(): Pack {
    try {
      const displayed = this.hooks.getDisplayedVersesCount ? this.hooks.getDisplayedVersesCount() : this.displayedVersesCount;
      const versions = this.hooks.getTabVersions ? this.hooks.getTabVersions() : this.tabVersions;
      return Pack.create(true)
        .put('displayed', displayed)
        .put('versions', versions.join(';'));
    } catch {
      return Pack.create(false, 'exception');
    }
  }

  private setDisplayedVersesAndVersion(p: Pack): Pack {
    try {
      const verseId = p.getString('verse_id', '01001001');
      const displayed = p.getInt('displayed', this.displayedVersesCount);
      const tabVersion = p.getInt('tab_version', 1);

      this.displayedVersesCount = displayed;
      this.hooks.setDisplayedVersesAndVersion?.(displayed, tabVersion);

      const [b, c, v] = this.resolveID(Pack.create().put('id', verseId));
      const verse = this.resolveVerse(b, c, v);
      return Pack.create(true).put('verses', this.formatReturn(verse));
    } catch {
      return Pack.create(false, 'exception');
    }
  }

  private setDisplayedVersesAndVersion2(p: Pack): Pack {
    try {
      const verseId = p.getString('verse_id', '01001001');
      const displayed = p.getInt('displayed', this.displayedVersesCount);
      const tabVersion = p.getInt('tab_version', 1);

      this.displayedVersesCount = displayed;
      this.hooks.setDisplayedVersesAndVersion?.(displayed, tabVersion);

      const [b, c, v] = this.resolveID(Pack.create().put('id', verseId));
      const verse = this.resolveVerse(b, c, v);
      return this.fillReturnVersesPack(Pack.create(true), verse);
    } catch {
      return Pack.create(false, 'exception');
    }
  }

  private blackScreen(p: Pack): Pack {
    const flag = p.getBoolean('value');
    this.isBlackScreenState = flag;
    this.hooks.onBlackScreen?.(flag);
    return Pack.create(true);
  }

  private blankScreen(p: Pack): Pack {
    const flag = p.getBoolean('value');
    this.isBlankScreenState = flag;
    this.hooks.onBlankScreen?.(flag);
    return Pack.create(true);
  }

  private resolveVerse(book: number, chapter: number, verse: number): BibleVerseInfo {
    if (this.hooks.getVerse) {
      const found = this.hooks.getVerse(book, chapter, verse);
      if (found) return found;
    }
    const id = `${String(book).padStart(2, '0')}${String(chapter).padStart(3, '0')}${String(verse).padStart(3, '0')}`;
    return {
      id,
      book,
      chapter,
      verse,
      text: `Versículo ${book}:${chapter}:${verse}`,
    };
  }

  private computeNextVerse(v: BibleVerseInfo): BibleVerseInfo {
    return this.resolveVerse(v.book, v.chapter, v.verse + 1);
  }

  private computePreviousVerse(v: BibleVerseInfo): BibleVerseInfo {
    return this.resolveVerse(v.book, v.chapter, Math.max(1, v.verse - 1));
  }

  private formatReturn(v: BibleVerseInfo): string {
    const prev = this.computePreviousVerse(v);
    const next = this.computeNextVerse(v);
    return `${v.id}${prev.text}\n\n${v.text}\n\n${next.text}`;
  }

  private fillReturnVersesPack(p: Pack, v: BibleVerseInfo): Pack {
    try {
      const prev = this.computePreviousVerse(v);
      const next = this.computeNextVerse(v);
      p.put('id', v.id)
        .put('book', v.book)
        .put('chapter', v.chapter)
        .put('verse', v.verse)
        .put('current', v.text)
        .put('previous', prev.text)
        .put('next', next.text);
    } catch {
      p.put('id', '01001001')
        .put('book', 1)
        .put('chapter', 1)
        .put('verse', 1)
        .put('current', 'Error')
        .put('previous', 'Error')
        .put('next', 'Error');
    }
    return p;
  }
}
