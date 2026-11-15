export enum EnumTabType {
  MUSIC = 'MUSIC',
  ANNOUNCEMENT = 'ANNOUNCEMENT',
  VIDEO = 'VIDEO',
  AUDIO = 'AUDIO',
  IMAGE = 'IMAGE',
  PLAYBACK = 'PLAYBACK',
  URL = 'URL',
  VERSE = 'VERSE',
  EVENT = 'EVENT',
}

export interface TabTypeMetadata {
  type: EnumTabType;
  title: string;
  actionName: string;
  iconName: string;
  defaultPath?: string;
}

export const TabTypeDetails: Record<EnumTabType, TabTypeMetadata> = {
  [EnumTabType.MUSIC]: {
    type: EnumTabType.MUSIC,
    title: 'Música',
    actionName: 'Exibir',
    iconName: 'holyricsIcon48.png',
  },
  [EnumTabType.ANNOUNCEMENT]: {
    type: EnumTabType.ANNOUNCEMENT,
    title: 'Avisos',
    actionName: 'Exibir',
    iconName: 'announcement16.png',
  },
  [EnumTabType.VIDEO]: {
    type: EnumTabType.VIDEO,
    title: 'Vídeo',
    actionName: 'Reproduzir',
    iconName: 'videoLibrary16.png',
  },
  [EnumTabType.AUDIO]: {
    type: EnumTabType.AUDIO,
    title: 'Áudio',
    actionName: 'Reproduzir',
    iconName: 'file16.png',
  },
  [EnumTabType.IMAGE]: {
    type: EnumTabType.IMAGE,
    title: 'Imagem',
    actionName: 'Exibir',
    iconName: 'image16.png',
  },
  [EnumTabType.PLAYBACK]: {
    type: EnumTabType.PLAYBACK,
    title: 'Playback',
    actionName: 'Reproduzir',
    iconName: 'play16.png',
  },
  [EnumTabType.URL]: {
    type: EnumTabType.URL,
    title: 'URL Link',
    actionName: 'Abrir',
    iconName: 'web16.png',
  },
  [EnumTabType.VERSE]: {
    type: EnumTabType.VERSE,
    title: 'Versículo',
    actionName: 'Exibir',
    iconName: 'bible16.png',
  },
  [EnumTabType.EVENT]: {
    type: EnumTabType.EVENT,
    title: 'Cultos / Eventos',
    actionName: 'Abrir',
    iconName: 'event16.png',
  },
};

export default EnumTabType;
