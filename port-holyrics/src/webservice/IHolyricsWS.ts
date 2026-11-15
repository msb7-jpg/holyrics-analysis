export interface IHolyricsWS {
  getSharedFiles(pack: string): Promise<string> | string;
  sendSharedFiles(value: string): Promise<string> | string;
  initRemoteControl(pack: string): Promise<string> | string;
  changeRemoteControlIndex(pack: string): Promise<string> | string;
  getStreaming(pack: string): Promise<string> | string;
  getPlaylist(pack: string): Promise<string> | string;
  webService(pack: string): Promise<string> | string;
}
