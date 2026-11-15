import { Pack } from './Pack';
import type { IHolyricsWS } from './IHolyricsWS';
import { createHash } from 'node:crypto';

export interface HolyricsWSHooks {
  getPasswordRemoteUpdate?: () => string;
  getPasswordRemoteControl?: () => string;
  getPasswordRemoteControlMedia?: () => string;
  isReceiveUpdateFromMobileVisible?: () => boolean;
  isReceiveUpdateFromMobileAvailable?: () => boolean;
  setReceiveUpdateFromMobileAvailable?: (available: boolean) => void;
  onUpdateFromJson?: (json: string) => Promise<void> | void;
  onPresentationExit?: () => void;
  onPresentationBlank?: (blank: boolean) => void;
  onPresentationBlack?: (black: boolean) => void;
  onPresentationSetIndex?: (index: number) => void;
  onPresentationSetTheme?: (theme: any) => void;
  onPlayedMusic?: (musicId: number | string) => void;
  onAlert?: (text: string, state: boolean) => void;
  getAlert?: () => { text: string; state: boolean };
  onAnnouncement?: (announcementId: string | number) => void;
  onShowImage?: (pathOrBytes: string) => void;
  onVlcAction?: (action: string, payload: any) => any;
  getVlcStatus?: () => any;
  getCountdownSettings?: () => any;
  onShowCountdown?: (settings: any) => void;
  onCloseCountdown?: () => void;
  getCommunicationPanel?: () => any;
  onCommunicationPanelAction?: (action: string, payload: any) => void;
  onAppMessage?: (msg: { id: string; name: string; message: string }) => any;
}

export class HolyricsWS implements IHolyricsWS {
  public static readonly DEFAULT_PORT = 888;
  public static readonly WS_PATH = '/holyricsWS';
  
  private static instance: HolyricsWS | null = null;
  private static serverInstance: any = null;
  private static isServerRunning: boolean = false;
  private static currentIP: string = '0.0.0.0';

  private hooks: HolyricsWSHooks = {};
  private activePresentationId: number = -1;
  private presentationVisible: boolean = false;
  private currentAlert: { text: string; state: boolean } = { text: '', state: false };
  private countdownSettings: Record<string, any> = {
    determine: false,
    minuteDetermine: 5,
    secondDetermine: 0,
    hourExact: 0,
    minuteExact: 0,
    size: 50,
    textBefore: '',
    textAfter: '',
  };
  private communicationPanelState: Record<string, any> = {
    presentationText: '',
    presentationShow: false,
    presentationDisplayAhead: false,
    alertText: '',
    alertShow: false,
    displayClockBg: false,
    displayClockAlert: false,
  };
  private playlists: any[] = [];
  private mediaList: Record<string, any[]> = {
    AUDIO: [],
    VIDEO: [],
    IMAGE: [],
  };
  private favorites: any[] = [];
  private streamingUpdateCount: number = 0;

  constructor(hooks?: HolyricsWSHooks) {
    if (hooks) {
      this.hooks = hooks;
    }
  }

  public static getInstance(): HolyricsWS {
    if (!HolyricsWS.instance) {
      HolyricsWS.instance = new HolyricsWS();
    }
    return HolyricsWS.instance;
  }

  public setHooks(hooks: HolyricsWSHooks): void {
    this.hooks = { ...this.hooks, ...hooks };
  }

  public static async open(port: number = HolyricsWS.DEFAULT_PORT, host: string = '0.0.0.0'): Promise<void> {
    HolyricsWS.close();
    const wsHandler = HolyricsWS.getInstance();
    HolyricsWS.currentIP = host;

    if (typeof Bun !== 'undefined') {
      HolyricsWS.serverInstance = Bun.serve({
        port,
        hostname: host,
        async fetch(req: Request, server: any) {
          const url = new URL(req.url);

          // Handle WebSocket upgrade
          if (req.headers.get('upgrade')?.toLowerCase() === 'websocket') {
            const success = server.upgrade(req, { data: { path: url.pathname } });
            if (success) return undefined;
            return new Response('WebSocket upgrade failed', { status: 400 });
          }

          // Handle HTTP CORS / OPTIONS
          if (req.method === 'OPTIONS') {
            return new Response(null, {
              headers: {
                'Access-Control-Allow-Origin': '*',
                'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
                'Access-Control-Allow-Headers': 'Content-Type',
              },
            });
          }

          // Handle HTTP POST / GET
          try {
            let bodyText = '';
            if (req.method === 'POST') {
              bodyText = await req.text();
            } else {
              bodyText = url.searchParams.get('pack') || url.searchParams.get('value') || '';
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
            } catch (err: any) {
              ws.send(Pack.create(false, 'exception').toJSon());
            }
          },
          open(ws: any) {},
          close(ws: any) {},
        },
      });
      HolyricsWS.isServerRunning = true;
    }
  }

  public static close(): void {
    if (HolyricsWS.serverInstance) {
      if (typeof HolyricsWS.serverInstance.stop === 'function') {
        HolyricsWS.serverInstance.stop();
      }
      HolyricsWS.serverInstance = null;
    }
    HolyricsWS.isServerRunning = false;
  }

  public static isOpen(): boolean {
    return HolyricsWS.isServerRunning && HolyricsWS.serverInstance !== null;
  }

  public static isOpenFast(): boolean {
    return HolyricsWS.isServerRunning;
  }

  public async handleHttpRequest(pathname: string, method: string, body: string, searchParams: URLSearchParams): Promise<string> {
    // Check method routing
    const action = searchParams.get('action') || '';
    if (pathname.includes('/getSharedFiles') || action === 'getSharedFiles') {
      return this.getSharedFiles(body || searchParams.get('pack') || '');
    }
    if (pathname.includes('/sendSharedFiles') || action === 'sendSharedFiles') {
      return this.sendSharedFiles(body || searchParams.get('value') || '');
    }
    if (pathname.includes('/initRemoteControl') || action === 'initRemoteControl') {
      return this.initRemoteControl(body || searchParams.get('pack') || '');
    }
    if (pathname.includes('/changeRemoteControlIndex') || action === 'changeRemoteControlIndex') {
      return this.changeRemoteControlIndex(body || searchParams.get('pack') || '');
    }
    if (pathname.includes('/getStreaming') || action === 'getStreaming') {
      return this.getStreaming(body || searchParams.get('pack') || '');
    }
    if (pathname.includes('/getPlaylist') || action === 'getPlaylist') {
      return this.getPlaylist(body || searchParams.get('pack') || '');
    }

    // Default router handles webService pack
    return this.webService(body || searchParams.get('pack') || '');
  }

  public async handleSocketMessage(message: string): Promise<string> {
    try {
      const p = Pack.create(message);
      const req = p.getString('action', '') || p.getString('request', '');

      if (req === 'getSharedFiles') return this.getSharedFiles(message);
      if (req === 'sendSharedFiles') return this.sendSharedFiles(p.getString('value', message));
      if (req === 'initRemoteControl') return this.initRemoteControl(message);
      if (req === 'changeRemoteControlIndex') return this.changeRemoteControlIndex(message);
      if (req === 'getStreaming') return this.getStreaming(message);
      if (req === 'getPlaylist') return this.getPlaylist(message);

      return this.webService(message);
    } catch {
      return Pack.create(false, 'exception').toJSon();
    }
  }

  // --- IHolyricsWS Implementations ---

  public async getSharedFiles(pack: string): Promise<string> {
    try {
      const p = Pack.create(pack);
      const configuredPassword = this.hooks.getPasswordRemoteUpdate?.() || '';
      if (configuredPassword && p.getString('password', '') !== configuredPassword) {
        return Pack.create(false, 'invalid_password').toJSon();
      }
      const json = JSON.stringify({ playlist: this.playlists });
      return Pack.create(true).put('json', json).toJSon();
    } catch {
      return Pack.create(false, 'exception').toJSon();
    }
  }

  public async sendSharedFiles(value: string): Promise<string> {
    const isVisible = this.hooks.isReceiveUpdateFromMobileVisible ? this.hooks.isReceiveUpdateFromMobileVisible() : true;
    if (!isVisible) {
      return 'Recebimento de atualização pelo app desativado';
    }
    const isAvailable = this.hooks.isReceiveUpdateFromMobileAvailable ? this.hooks.isReceiveUpdateFromMobileAvailable() : true;
    if (!isAvailable) {
      return 'Holyrics ocupado';
    }
    if (this.hooks.setReceiveUpdateFromMobileAvailable) {
      this.hooks.setReceiveUpdateFromMobileAvailable(false);
    }
    if (this.hooks.onUpdateFromJson) {
      this.hooks.onUpdateFromJson(value);
    }
    return 'Atualização enviada';
  }

  public async initRemoteControl(pack: string): Promise<string> {
    try {
      const p = Pack.create(pack);
      const configuredPassword = this.hooks.getPasswordRemoteControl?.() || '';
      if (configuredPassword && p.getString('password', '') !== configuredPassword) {
        return Pack.create(false, 'invalid_password').toJSon();
      }

      const musicJson = p.getString('music', '');
      if (!musicJson) {
        return Pack.create(false, 'invalid_music').toJSon();
      }

      const id = Date.now();
      this.activePresentationId = id;
      this.presentationVisible = true;

      const slides: string[] = ['Slide 1', 'Slide 2'];
      return Pack.create(true).put('id', id).put('list', slides).toJSon();
    } catch {
      return Pack.create(false, 'exception').toJSon();
    }
  }

  public async changeRemoteControlIndex(pack: string): Promise<string> {
    try {
      if (!this.presentationVisible) {
        return Pack.create(false, 'closed').toJSon();
      }
      const p = Pack.create(pack);
      if (p.getLong('id', -8) !== this.activePresentationId) {
        return Pack.create(false, 'closed').toJSon();
      }

      const res = Pack.create(true);
      if (p.getString('blank', '') !== '') {
        this.hooks.onPresentationBlank?.(p.getBoolean('blank'));
      } else if (p.getString('black', '') !== '') {
        this.hooks.onPresentationBlack?.(p.getBoolean('black'));
      } else if (p.getBoolean('exit')) {
        this.presentationVisible = false;
        this.hooks.onPresentationExit?.();
        if (p.getBoolean('setToMusicPlayed')) {
          this.hooks.onPlayedMusic?.(p.getString('musicId', ''));
        }
      } else if (p.getInt('index', -1) !== -1) {
        this.hooks.onPresentationSetIndex?.(p.getInt('index', 0));
      }

      if (p.getString('theme', '') !== '') {
        const themeVal = p.getString('theme', '');
        if (themeVal.toLowerCase() === 'null') {
          res.put('theme_id', 'null');
          this.hooks.onPresentationSetTheme?.(null);
        } else {
          res.put('theme_id', p.getString('theme_id', 'null'));
          this.hooks.onPresentationSetTheme?.(themeVal);
        }
      }

      return res.toJSon();
    } catch {
      return Pack.create(false, 'exception').toJSon();
    }
  }

  public async getStreaming(pack: string): Promise<string> {
    try {
      const p = Pack.create(pack);
      this.streamingUpdateCount++;
      const currentUpdate = String(this.streamingUpdateCount);
      const reqUpdate = p.getString('update', '');
      if (reqUpdate === currentUpdate) {
        return Pack.create(true).put('image', 'same').toJSon();
      }
      return Pack.create(true)
        .put('image', 'data:image/png;base64,')
        .put('update', currentUpdate)
        .toJSon();
    } catch {
      return Pack.create(true).put('image', 'same').toJSon();
    }
  }

  public async getPlaylist(pack: string): Promise<string> {
    const p = Pack.create(pack);
    const configuredPassword = this.hooks.getPasswordRemoteUpdate?.() || '';
    if (configuredPassword && p.getString('password', '') !== configuredPassword) {
      return Pack.create(false, 'invalid_password').toJSon();
    }
    return Pack.create(true).put('playlist', JSON.stringify(this.playlists)).toJSon();
  }

  public async webService(pack: string): Promise<string> {
    const resultPack = await this.webService2(pack);
    resultPack.put('wb', true);
    return resultPack.toJSon();
  }

  public async webService2(pack: string): Promise<Pack> {
    const p = Pack.create(pack);
    const req = p.getString('request', '');

    switch (req) {
      case 'initQuickPresentation':
        return this.initQuickPresentation(p);
      case 'changeQuickPresentation':
        return this.changeQuickPresentation(p);
      case 'alert':
        return this.alert(p);
      case 'getAlert':
        return this.getAlert(p);
      case 'announcementList':
        return this.announcementList(p);
      case 'announcement':
        return this.announcement(p);
      case 'mediaList':
        return this.mediaListAction(p, ['AUDIO', 'VIDEO', 'IMAGE']);
      case 'audioList':
        return this.mediaListAction(p, ['AUDIO']);
      case 'videoList':
        return this.mediaListAction(p, ['VIDEO']);
      case 'imageList':
        return this.mediaListAction(p, ['IMAGE']);
      case 'showImage':
        return this.showImage(p);
      case 'playlistMedia':
        return this.playlistMedia(p);
      case 'favoritesList':
        return this.favoritesList(p);
      case 'vlcStatus':
        return this.vlcStatus(p);
      case 'vlcAction':
        return this.vlcAction(p);
      case 'sendBytes':
        return this.sendBytes(p);
      case 'sendPlaylistMusic':
        return this.sendPlaylistMusic(p);
      case 'getCountdownSettings':
        return this.getCountdownSettingsAction(p);
      case 'showCountdown':
        return this.showCountdownAction(p);
      case 'closeCountdown':
        return this.closeCountdownAction(p);
      case 'getCommunicationPanel':
        return this.getCommunicationPanelAction(p);
      case 'communicationPanelAction':
        return this.communicationPanelAction(p);
      case 'getObject':
        return this.getObject(p);
      case 'appMessage':
        return this.appMessage(p);
      default:
        return Pack.create(false, 'not_found');
    }
  }

  private defaultPasswordRemoteControlFilter(p: Pack, fn: () => Pack | Promise<Pack>): Promise<Pack> | Pack {
    try {
      const configuredPassword = this.hooks.getPasswordRemoteControl?.() || '';
      if (configuredPassword && p.getString('password', '') !== configuredPassword) {
        return Pack.create(false, 'invalid_password');
      }
      return fn();
    } catch {
      return Pack.create(false, 'exception');
    }
  }

  private defaultPasswordRemoteControlMediaFilter(p: Pack, fn: (decrypted: Pack) => Pack | Promise<Pack>): Promise<Pack> | Pack {
    try {
      const pass = this.hooks.getPasswordRemoteControlMedia?.() || '';
      let decrypted = p;
      if (pass) {
        const md5Key = Pack.getMD5(pass, 7);
        decrypted = Pack.createDecrypt(p, md5Key);
        const clientPass = decrypted.getString('password', '');
        if (clientPass !== md5Key) {
          return Pack.create(false, 'invalid_password');
        }
        const result = fn(decrypted);
        if (result instanceof Promise) {
          return result.then((res) => res.encrypt(md5Key));
        }
        return result.encrypt(md5Key);
      }
      return fn(decrypted);
    } catch {
      return Pack.create(false, 'exception');
    }
  }

  private initQuickPresentation(p: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(p, () => {
      const music = p.getString('music', '');
      if (!music) {
        return Pack.create(false, 'invalid_music');
      }
      const id = Date.now();
      this.activePresentationId = id;
      this.presentationVisible = true;
      return Pack.create(true).put('id', id).put('list', ['Quick Slide 1']);
    });
  }

  private changeQuickPresentation(p: Pack): Pack {
    try {
      if (!this.presentationVisible) {
        return Pack.create(false, 'closed');
      }
      if (p.getLong('id', -8) !== this.activePresentationId) {
        return Pack.create(false, 'closed');
      }
      if (p.getString('blank', '') !== '') {
        this.hooks.onPresentationBlank?.(p.getBoolean('blank'));
      } else if (p.getString('black', '') !== '') {
        this.hooks.onPresentationBlack?.(p.getBoolean('black'));
      } else if (p.getBoolean('exit')) {
        this.presentationVisible = false;
        this.hooks.onPresentationExit?.();
      } else if (p.getInt('index', -1) !== -1) {
        this.hooks.onPresentationSetIndex?.(p.getInt('index', 0));
      }
      return Pack.create(true);
    } catch {
      return Pack.create(false, 'exception');
    }
  }

  private alert(p: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(p, () => {
      const text = p.getString('text', '').replace(/\n/g, ' ');
      const state = p.getBoolean('state');
      this.currentAlert = { text, state };
      this.hooks.onAlert?.(text, state);
      return Pack.create(true);
    });
  }

  private getAlert(p: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(p, () => {
      const alert = this.hooks.getAlert ? this.hooks.getAlert() : this.currentAlert;
      return Pack.create(true).put('text', alert.text).put('state', alert.state);
    });
  }

  private announcementList(p: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(p, () => {
      return Pack.create(true)
        .put('ids', '')
        .put('titles', '')
        .put('images', '');
    });
  }

  private announcement(p: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(p, () => {
      if (p.getBoolean('close')) {
        this.hooks.onPresentationExit?.();
        return Pack.create(true);
      }
      const id = p.getLong('id', -1);
      this.hooks.onAnnouncement?.(id);
      return Pack.create(true);
    });
  }

  private mediaListAction(pack: Pack, types: string[]): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlMediaFilter(pack, (p) => {
      const pReturn = Pack.create(true);
      for (const tabType of types) {
        const list = this.mediaList[tabType] || [];
        pReturn
          .put(`${tabType}-names`, list.map((i) => i.name).join('\n'))
          .put(`${tabType}-names_normalized`, list.map((i) => i.nameNormalized || i.name).join('\n'))
          .put(`${tabType}-paths`, list.map((i) => i.path).join('\n'))
          .put(`${tabType}-isdir`, list.map((i) => String(!!i.dir)).join('\n'))
          .put(`${tabType}-images`, list.map((i) => i.thumb || '').join('\n'));
      }
      return pReturn;
    });
  }

  private showImage(pack: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlMediaFilter(pack, (p) => {
      if (p.getBoolean('close')) {
        this.hooks.onPresentationExit?.();
        return Pack.create(true);
      }
      const image = p.getString('image', '');
      this.hooks.onShowImage?.(image);
      return Pack.create(true);
    });
  }

  private playlistMedia(p: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(p, () => {
      return Pack.create(true)
        .put('names', '')
        .put('action_names', '')
        .put('ids', '')
        .put('files', '')
        .put('types', '')
        .put('images', '');
    });
  }

  private favoritesList(pack: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlMediaFilter(pack, (p) => {
      return Pack.create(true)
        .put('names', '')
        .put('action_names', '')
        .put('ids', '')
        .put('files', '')
        .put('types', '')
        .put('images', '');
    });
  }

  private vlcStatus(pack: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlMediaFilter(pack, (p) => {
      const status = this.hooks.getVlcStatus ? this.hooks.getVlcStatus() : {};
      const res = Pack.create(true);
      for (const [k, v] of Object.entries(status)) {
        res.put(k, v);
      }
      return res;
    });
  }

  private vlcAction(pack: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlMediaFilter(pack, (p) => {
      const action = p.getString('action', 'null');
      if (this.hooks.onVlcAction) {
        this.hooks.onVlcAction(action, p.map);
      }
      const res = Pack.create(true);
      const status = this.hooks.getVlcStatus ? this.hooks.getVlcStatus() : {};
      for (const [k, v] of Object.entries(status)) {
        res.put(k, v);
      }
      return res;
    });
  }

  private sendBytes(pack: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlMediaFilter(pack, (p) => {
      const name = p.getString('name', ':*:null:*:');
      if (name === ':*:null:*:') {
        return Pack.create(false, 'Erro, tente novamente');
      }
      return Pack.create(true).put('fullname', name);
    });
  }

  private sendPlaylistMusic(p: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(p, () => {
      const musicsStr = p.getString('musics', '');
      const musics = musicsStr.split('\n').filter(Boolean);
      for (const m of musics) {
        try {
          this.playlists.push(JSON.parse(m));
        } catch {
          this.playlists.push(m);
        }
      }
      return Pack.create(true);
    });
  }

  private getCountdownSettingsAction(pack: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(pack, () => {
      const s = this.hooks.getCountdownSettings ? this.hooks.getCountdownSettings() : this.countdownSettings;
      return Pack.create(true)
        .put('determine', s.determine)
        .put('minuteDetermine', s.minuteDetermine)
        .put('secondDetermine', s.secondDetermine)
        .put('hourExact', s.hourExact)
        .put('minuteExact', s.minuteExact)
        .put('size', s.size)
        .put('textBefore', s.textBefore)
        .put('textAfter', s.textAfter);
    });
  }

  private showCountdownAction(pack: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(pack, () => {
      const s = {
        determine: pack.getBoolean('determine'),
        minuteDetermine: pack.getInt('minuteDetermine', 5),
        secondDetermine: pack.getInt('secondDetermine', 0),
        hourExact: pack.getInt('hourExact', 0),
        minuteExact: pack.getInt('minuteExact', 0),
        size: pack.getInt('size', 50),
        textBefore: pack.getString('textBefore', ''),
        textAfter: pack.getString('textAfter', ''),
      };
      this.hooks.onShowCountdown?.(s);
      return Pack.create(true);
    });
  }

  private closeCountdownAction(pack: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(pack, () => {
      this.hooks.onCloseCountdown?.();
      return Pack.create(true);
    });
  }

  private getCommunicationPanelAction(p: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(p, () => {
      const s = this.hooks.getCommunicationPanel ? this.hooks.getCommunicationPanel() : this.communicationPanelState;
      return Pack.create(true)
        .put('presentation-text', s.presentationText || '')
        .put('presentation-show', !!s.presentationShow)
        .put('presentation-displayAhead', !!s.presentationDisplayAhead)
        .put('alert-text', s.alertText || '')
        .put('alert-show', !!s.alertShow)
        .put('display-clock-bg', !!s.displayClockBg)
        .put('display-clock-alert', !!s.displayClockAlert);
    });
  }

  private communicationPanelAction(p: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlFilter(p, () => {
      const action = p.getString('action', 'null');
      if (this.hooks.onCommunicationPanelAction) {
        this.hooks.onCommunicationPanelAction(action, p.map);
      }
      switch (action) {
        case 'presentation':
          this.communicationPanelState.presentationShow = p.getBoolean('show');
          if (this.communicationPanelState.presentationShow) {
            this.communicationPanelState.presentationText = p.getString('text', '');
          }
          this.communicationPanelState.presentationDisplayAhead = p.getBoolean('displayAhead');
          break;
        case 'alert':
          this.communicationPanelState.alertShow = p.getBoolean('show');
          if (this.communicationPanelState.alertShow) {
            this.communicationPanelState.alertText = p.getString('text', '').replace(/\n/g, ' ');
          }
          break;
        case 'displayClockAsBackground':
          this.communicationPanelState.displayClockBg = p.getBoolean('value');
          break;
        case 'displayClockOnAlert':
          this.communicationPanelState.displayClockAlert = p.getBoolean('value');
          break;
      }
      return Pack.create(true);
    });
  }

  private getObject(pack: Pack): Promise<Pack> | Pack {
    return this.defaultPasswordRemoteControlMediaFilter(pack, (p) => {
      const item = p.getString('get', 'null');
      switch (item) {
        case 'music':
          return Pack.create(true).put('music_json', '{}');
        case 'announcement':
          return Pack.create(true).put('id', p.getString('id', '')).put('title', '').put('image', '');
        case 'image_file':
          return Pack.create(true).put('files', '').put('images', '');
        case 'file_hash_app':
          return Pack.create(true).put('hash', 'not_found');
        default:
          return Pack.create(false, 'Requisição não encontrada');
      }
    });
  }

  private appMessage(pack: Pack): Pack {
    const type = pack.getString('type', '');
    if (type === 'send') {
      const msg = pack.getString('message', '').trim().slice(0, 1024);
      const name = pack.getString('name', '').trim().slice(0, 24);
      const id = pack.getString('id', '').trim().slice(0, 24);
      if (msg) {
        this.hooks.onAppMessage?.({ id, name, message: msg });
      }
    }
    return Pack.create(true).put('json', '[]');
  }
}
