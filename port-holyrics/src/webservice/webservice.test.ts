import { describe, it, expect, beforeEach, afterEach } from 'bun:test';
import { HolyricsWS } from './HolyricsWS';
import { RemoteControlBibleWS } from './RemoteControlBibleWS';
import { Pack } from './Pack';

describe('Pack tests', () => {
  it('creates and parses json pack correctly', () => {
    const p = Pack.create(true).put('request', 'alert').put('text', 'Test message');
    const json = p.toJSon();
    const p2 = Pack.create(json);
    expect(p2.isOk()).toBe(true);
    expect(p2.getString('request')).toBe('alert');
    expect(p2.getString('text')).toBe('Test message');
  });

  it('handles booleans and numbers in put/get', () => {
    const p = Pack.create()
      .put('count', 42)
      .put('enabled', true)
      .put('flagStr', 'false');
    expect(p.getInt('count')).toBe(42);
    expect(p.getLong('count')).toBe(42);
    expect(p.getBoolean('enabled')).toBe(true);
    expect(p.getBoolean('flagStr')).toBe(false);
  });
});

describe('HolyricsWS routing', () => {
  let ws: HolyricsWS;

  beforeEach(() => {
    ws = new HolyricsWS();
  });

  it('handles alert command and getAlert', async () => {
    let alerted = false;
    ws.setHooks({
      onAlert: (text, state) => {
        alerted = true;
      },
    });

    const alertPack = Pack.create().put('request', 'alert').put('text', 'Hello church').put('state', true);
    const res = await ws.webService2(alertPack.toJSon());
    expect(res.isOk()).toBe(true);
    expect(alerted).toBe(true);

    const getAlertPack = Pack.create().put('request', 'getAlert');
    const getRes = await ws.webService2(getAlertPack.toJSon());
    expect(getRes.isOk()).toBe(true);
    expect(getRes.getString('text')).toBe('Hello church');
    expect(getRes.getBoolean('state')).toBe(true);
  });

  it('handles webService envelope with wb: true', async () => {
    const alertPack = Pack.create().put('request', 'getAlert');
    const resJson = await ws.webService(alertPack.toJSon());
    const res = Pack.create(resJson);
    expect(res.getBoolean('wb')).toBe(true);
  });

  it('handles communication panel actions', async () => {
    const p = Pack.create()
      .put('request', 'communicationPanelAction')
      .put('action', 'presentation')
      .put('show', true)
      .put('text', 'Notices');
    const res = await ws.webService2(p.toJSon());
    expect(res.isOk()).toBe(true);

    const getP = Pack.create().put('request', 'getCommunicationPanel');
    const getRes = await ws.webService2(getP.toJSon());
    expect(getRes.getString('presentation-text')).toBe('Notices');
    expect(getRes.getBoolean('presentation-show')).toBe(true);
  });
});

describe('RemoteControlBibleWS routing', () => {
  let bibleWS: RemoteControlBibleWS;

  beforeEach(() => {
    bibleWS = new RemoteControlBibleWS();
  });

  it('navigates verses', () => {
    const res = bibleWS.goTo(1, 1, 1);
    expect(res).toContain('01001001');

    const nextRes = bibleWS.next(1, 1, 1, true);
    expect(nextRes).toContain('01001002');
  });

  it('handles webService3 commands through ws()', () => {
    const p = Pack.create().put('request', 'getVerse').put('id', '01002003');
    const resJson = bibleWS.ws(p.toJSon());
    const res = Pack.create(resJson);
    expect(res.isOk()).toBe(true);
    expect(res.getInt('book')).toBe(1);
    expect(res.getInt('chapter')).toBe(2);
    expect(res.getInt('verse')).toBe(3);
  });

  it('handles screen toggles', () => {
    let blackToggled = false;
    let blankToggled = false;
    bibleWS.setHooks({
      onBlackScreen: (b) => { blackToggled = b; },
      onBlankScreen: (b) => { blankToggled = b; },
    });

    const pBlack = Pack.create().put('request', 'blackScreen').put('value', true);
    bibleWS.ws(pBlack.toJSon());
    expect(blackToggled).toBe(true);

    const pBlank = Pack.create().put('request', 'blankScreen').put('value', true);
    bibleWS.ws(pBlank.toJSon());
    expect(blankToggled).toBe(true);
  });
});
