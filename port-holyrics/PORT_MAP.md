# Port Map: Java (Swing) a TypeScript (React/Bun) 1:1

Este mapa rastreia todos os arquivos `.java` do repositório original e detalha lado a lado a correspondência exata no novo ambiente (TS/TSX).

| Status | Arquivo Original (Java) | Arquivo Portado (TS/TSX) | Evidências / Observações |
|--------|-------------------------|--------------------------|--------------------------|
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/bible/AvailableVersions.java` | `src/bible/AvailableVersions.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/bible/Bible.java` | `src/bible/Bible.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/bible/BibleFind.java` | `src/bible/BibleFind.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/bible/Book.java` | `src/bible/Book.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/bible/FactoryBible.java` | `src/bible/FactoryBible.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/ReportError.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/bible/Verse.java` | `src/bible/Verse.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/download/AvailableDownloads.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/download/DownloadView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/download/ItemVersionsView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/download/Updates.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/i18n/I18N.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/bible/swingmodel/ListCellRendererFoundVerses.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/swingmodel/ListModelVerse.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/bible/util/Dir.java` | `src/util/Dir.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/util/Download.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/util/InsertShortcut.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/util/Log.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/util/MySwingWorker.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/util/PopUpWorker.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/util/RWObj.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/bible/util/Security.java` | `src/util/Security.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/util/SwingUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/bible/util/Utils.java` | `src/util/Utils.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/bible/util/Values.java` | `src/util/Values.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/util/VerseFilter.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/util/WrapLayout.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/view/TabResult.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/view/VerseSearchPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/bible/view/VerseSearchView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/Dev.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/Holyrics.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/Run.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/Test.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/Update.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/AlertController.java` | `src/control/AlertController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/AnnouncementController.java` | `src/control/AnnouncementController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/AppMessageController.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/ControllerFactory.java` | `src/control/ControllerFactory.ts` | Classe recriada e funcional no ecossistema TS/React. |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/ControllerListener.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/ControllerMapFactory.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/EventController.java` | `src/control/EventController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/FavoriteController.java` | `src/control/FavoriteController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/MemberController.java` | `src/control/MemberController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/MusicController.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/MusicFavoriteSetController.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/MyImagesController.java` | `src/control/MyImagesController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/MyVideosController.java` | `src/control/MyVideosController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/ProfileController.java` | `src/control/ProfileController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/ScheduleController.java` | `src/control/ScheduleController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/ThemeController.java` | `src/control/ThemeController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/ThumbCacheController.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/ThumbMediaController.java` | `src/control/ThumbMediaController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/TransitionController.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/control/WorshipController.java` | `src/control/WorshipController.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/dao/MusicDao.java` | `src/dao/MusicDao.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/EditDialog.java` | `src/view/edit/EditDialog.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/EditEvent.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/EditMember.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/EditPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/EditProfile.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/EditWorship.java` | `src/view/edit/EditWorship.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/IEditView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/event/EventDayPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/event/EventFormCreate.java` | `src/view/edit/event/EventFormCreate.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/event/EventFormEdit.java` | `src/view/edit/event/EventFormEdit.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/event/EventPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/event/EventTable.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/event/EventWorshipForm.java` | `src/view/edit/event/EventWorshipForm.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/edit/event/EventWorshipPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/event/AnnouncementTitlePanelListener.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/event/BackgroundChoiceListener.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/event/BibleFastSearchListener.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/event/MusicSearchDBOffLinePanelListener.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/event/MusicSearchPanelListener.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/event/SchedulePanelGridListener.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/event/ThemeSettingsPanelListener.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/favorite/Favoritable.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/favorite/FavoriteItem.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/favorite/FavoriteItemLabel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/favorite/FavoritePanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/favorite/FavoriteUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/help/EnumHelpPages.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/help/HelpView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/help/TreeModelHelp.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/EnumTabType.java` | `src/hitem/EnumTabType.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/HItemListView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/HItemResolve.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/HItemTab.java` | `src/hitem/HItemTab.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/HItemUtils.java` | `src/hitem/HItemUtils.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/ListModelHItem.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/ShowGridDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/ToolTipProviderHItemListView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/item/HItemAnnouncement.java` | `src/hitem/item/HItemAnnouncement.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/item/HItemAudio.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/item/HItemImage.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/item/HItemMusic.java` | `src/hitem/HItemMusic.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/item/HItemPlayBack.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/item/HItemURL.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/item/HItemVerse.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/item/HItemVideo.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/hitem/item/IHItem.java` | `src/hitem/item/IHItem.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/i18n/I18N.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Account.java` | `src/model/Account.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Alert.java` | `src/model/Alert.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/AlertSchedule.java` | `src/model/AlertSchedule.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/AlertSettings.java` | `src/model/AlertSettings.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Announcement.java` | `src/model/Announcement.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/AppMessage.java` | `src/model/AppMessage.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/ClockSettings.java` | `src/model/ClockSettings.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/CountdownSettings.java` | `src/model/CountdownSettings.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/DBOffLine.java` | `src/model/DBOffLine.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Event.java` | `src/model/Event.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/History.java` | `src/model/History.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/ImageHL.java` | `src/model/ImageHL.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Margin.java` | `src/model/Margin.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Member.java` | `src/model/Member.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Music.java` | `src/model/Music.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Presentation.java` | `src/model/Presentation.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Profile.java` | `src/model/Profile.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Schedule.java` | `src/model/Schedule.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/ScreenDeviceSettings.java` | `src/model/ScreenDeviceSettings.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/VideoHL.java` | `src/model/VideoHL.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/Worship.java` | `src/model/Worship.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/announcement/AnnouncementEditPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/announcement/AnnouncementTitlePanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/Background.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundAnnouncement.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundBufferedImageCache.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundBufferedThumbImageCache.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundColor.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundImage.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundImageFile.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundImageThumbs2.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundMyImage.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundMyVideo.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundPattern.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/BackgroundVideo.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/ITheme.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/IThemeUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/Theme.java` | `src/model/theme/Theme.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/model/theme/ThumbnailGenerator.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/CaptureDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/CustomSlidePB.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/CustomSlidePBListener.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/EditPlayBackDialog.java` | `src/playback/EditPlayBackDialog.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/ExportMediaWorker.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/ExportToVideoDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/ListModelPlayBack.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/MyImageTransitionRenderer.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/PlayBack.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/PlayBackListView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/PlayBackPlayerMini.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/PlayBackPlayerMiniShow.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/PlayBackTab.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/PlayBackUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/playback/TransitionEffectRenderer.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/player/AudioPlayerPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/player/AudioPlayerSetUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/player/AudioPlayerSettings.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/player/AudioPlayerTitlePanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/player/Equalizer.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/player/ListCellRendererAudioPlayer.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/player/ListDragAndDropAudioPlayer.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/player/ListItemTransferHandlerAudioPlayer.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/player/ListModelAudioPlayer.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/report/model/PlayedMusics.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/report/model/ReportScheduleMember.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/report/model/ReportScheduleMusic.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/report/model/TableModelPlayedMusic.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/report/model/TableModelReportScheduleMember.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/report/model/TableModelReportScheduleMusic.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/report/view/ReportPlayedMusicsView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/report/view/ReportScheduleView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/settings/Settings.java` | `src/settings/Settings.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/settings/SettingsFactory.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/settings/SettingsView.java` | `src/view/SettingsView.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/share/ReceiveUpdateFromInternetDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/share/ReceiveUpdateFromMobileView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/share/ShareFilesApp.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/share/ShareFilesAppJson.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/share/SharePHP.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/share/ShareRegister.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/AbstractWebListCellRendererIconLabel.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/AbstractWebListCellRendererWithSeparator.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/AbstractWebListCellRendererWithSeparatorDoubleLine.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/AbstractWebListCellRendererWithSeparatorTripleLine.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/AlertPanelAnimated.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/AlertPanelAnimatedDefault.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/BibleMainSelectableJLabel.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/BibleTabbedPaneTitle.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/CaretListenerAuto.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ChangeEventDelay.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ComboBoxModelEvent.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ComboBoxModelHItemNextPresentation.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ComboBoxModelProfile.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ComboBoxModelTheme.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ComboBoxRendererEvent.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ComboBoxRendererHItem.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ComboBoxRendererPlayListType.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ComboBoxRendererTheme.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ComponentResizableDelay.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/CountdownValueInputDialog.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/DocumentListenerAuto.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/DragComponentMouseAdapter.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/DragComponentMouseAdapterMusicOrder.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/DragComponentMouseAdapterWithRolloverAction.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/JCheckBoxTreeBatchImport.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/JCheckBoxTreeDBOffLine.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/JLabelToolTip.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererBibleVerses.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererCaptureDialog.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererFavoriteVerse.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererHItem.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererHItemGrid.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererHItemPlayList.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererHistory.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererMember.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererMusic.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererNextSchedules.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererOrder.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererPlayBack.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererPlayList.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererPresentationImage.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererPresentationText.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererProfile.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererSelectedMusicsBatchExport.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererTheme.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererWebSearchMusic.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListCellRendererWorship.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListDragAndDropFile.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListDragAndDropHItem.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListDragAndDropMusic.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListDragAndDropOrder.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListDragAndDropString.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListDragAndDropVerse.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListItemTransferHandlerFile.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListItemTransferHandlerHItem.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListItemTransferHandlerMusic.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListItemTransferHandlerOrder.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListItemTransferHandlerString.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListItemTransferHandlerVerse.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelAddRemove.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelFavoriteVerses.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelHItemPlayList.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelHistory.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelMember.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelNextSchedules.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelOrder.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelPlayListEvent.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelProfile.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelTheme.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelVerse.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/ListModelWorship.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/MarginSetDialog.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/SelectablePanel.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/TableCellEditorCustom.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/TableCellRendererReportMember.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/TableCellRendererReportString.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/TableCellRendererReportStringTrueFalse.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/TableCellRendererScheduleAlternateRowColor.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/TableCellRendererScheduleMember.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/TableCellRendererScheduleObject.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/TableModelSchedule.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/TableModelScheduleOnlyMember.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/TablePrintableCustom.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/WebDecoratedImageSelectableCustom.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/swing/WebDecoratedImageWithLabelSelectableCustom.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/Accordion.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/Curtain.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/CustomTransitionEffect.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/DefaultPanelSet.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/Direction.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/Expande.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/Fade.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/LinearFade.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/MyImageTransition.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/Random.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/RendererEffect.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/Slide.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/SlideMoveType.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/TransitionSettings.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/TransitionSettingsDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/TransitionType.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/transition/Zoom.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/AlertSettingsUtils.java` | `src/util/AlertSettingsUtils.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/AppMessageUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/BackgroundChoiceUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Backup.java` | `src/util/Backup.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/BibleMainUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/BibleSlideUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/BooleanOptional.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/ClockSettingsUtils.java` | `src/util/ClockSettingsUtils.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/CountdownUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/DateLG.java` | `src/util/DateLG.ts` | Classe recriada e funcional no ecossistema TS/React. |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/DesktopShortcut.java` | N/A | Recurso nativo descontinuado para a Web |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Dir.java` | `src/util/Dir.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Download.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/EnumAvailableVideos.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/EnumPlayList.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/EnumSlideSizePreview.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/EnumVerseDescription.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/EnumWorship.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Excel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/FFMPEG.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Freeze.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/G2Utils.java` | `src/util/G2Utils.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/GeneratePresentation.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/GeneratePresentationThreadUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/HolyricsShortcut.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/HolyricsStreaming.java` | `src/util/HolyricsStreaming.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/IScheduleTask.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/ISwingCRUD.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Images.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Img.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/ImportMusic.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/InsertShortcut.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/JCheckBoxTree.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/KeyboardUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Log.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/MSOfficeUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/MediaPlayerUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/MusicUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/MyProperties.java` | `src/util/MyProperties.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/MySwingWorker.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PHPUtils.java` | N/A | Recurso nativo descontinuado para a Web |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PopUp.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PopUpAlert.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PopUpMenu.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PopUpOpenProgram.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PopUpOpenProgram2.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PopUpWorker.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PopupUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PresentationCache.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PresentationRendererUtils.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PresentationSlideUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/PrintUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/RW.java` | `src/util/RW.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/RWObj.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/ReflectionUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/SQLite.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/SaveSlides.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/ScreenUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Security.java` | `src/util/Security.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/StageViewUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/StringUtils.java` | `src/util/StringUtils.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/StyleUI.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/SwingUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/SystemUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/TableToExcel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/TransitionUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/UDPServer.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/UICreate.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Utils.java` | `src/util/Utils.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/Values.java` | `src/util/Values.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/VideoUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/WebLafUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/WebServiceUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/util/WrapLayout.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/AboutView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/AlertSettingsDialog.java` | `src/view/AlertSettingsDialog.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/AnnouncementShowDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/AnnouncementView.java` | `src/view/AnnouncementView.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/AutomaticShowView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/BackgroundChoiceAnnouncement.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/BackgroundChoiceTheme.java` | `src/view/BackgroundChoiceTheme.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/BibleAutomaticShowDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/BibleFastSearchView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/BibleFontSettings.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/BibleMain.java` | `src/view/BibleMain.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/BiblePresentationView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/BibleSaveVersesDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/BibleSettingsDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/ClockSettingsDialog.java` | `src/view/ClockSettingsDialog.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/CommunicationPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/CountdownDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/CreateImageDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/DBOffLineBatchImportView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/EditMusicOrderDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/FontEditThemeDefaultDialog.java` | `src/view/FontEditThemeDefaultDialog.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/ImageFileShowDialog.java` | `src/view/ImageFileShowDialog.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/JLabelTheme.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/LiveView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/MusicAlreadyExistsDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/MusicBatchDelete.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/MusicBatchExport.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/MusicBatchImportView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/MusicMain.java` | `src/view/MusicMain.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/MusicSaveAsDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/MusicSearchDBOffLineView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/MusicView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/MusicWebSearch.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/PowerPointImportView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/PresentationJDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/PresentationJDialogBackup.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/PresentationSlideView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/PresentationView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/PresentationViewRemoteControl.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/QuickPresentationDialog.java` | `src/view/QuickPresentationDialog.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/ReadyMessagesChooser.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/RendererValues.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/ScheduleView.java` | `src/view/ScheduleView.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/ShareView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/SupportAndSuggestionsDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/ThemeReorderDialog.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/ThemeView.java` | `src/view/ThemeView.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/WallpaperView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/control/AutomaticShowControl.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/control/BiblePresentationControl.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/control/PresentationControl.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/control/PresentationUtils.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AddToProfilesPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AlertListPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AlertPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AlertSchedulePanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AppMessagePanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AutomaticSettingsPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AvailableImage.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AvailableMyImages.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AvailableMyVideos.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AvailablePattern.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/AvailableVideo.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/DrawableJComponent.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/EventSchedulePanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/HItemNextPresentationPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/MultiSlidePreview.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/MusicSearchDBOffLinePanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/MusicSearchPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/PresentationControllerViewFooter.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/SaveAsPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/SchedulePanelGrid.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/SimpleSlidePreview.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/view/panel/ThemeSettingsPanel.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/DirectMediaPlayerComponentTest.java` | N/A | Lógica C++ (vlcj) substituída por engine nativa HTML5 `<video>` e window.open |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLC.java` | `src/vlc/VLC.ts` | Classe recriada e funcional no ecossistema TS/React. |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLCDownloadWorker.java` | N/A | Lógica C++ (vlcj) substituída por engine nativa HTML5 `<video>` e window.open |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLCPlayerMini.java` | `src/vlc/VLCPlayerMini.tsx` | Classe recriada e funcional no ecossistema TS/React. |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLCPlayerStateListener.java` | N/A | Boilerplate do AWT/Swing nativamente mesclado nos componentes React JSX |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLCSetUtils.java` | N/A | Lógica C++ (vlcj) substituída por engine nativa HTML5 `<video>` e window.open |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLCSettings.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLCUtils.java` | N/A | Lógica C++ (vlcj) substituída por engine nativa HTML5 `<video>` e window.open |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLCVideoBackground.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLCVideoBackgroundBase.java` | N/A | Lógica C++ (vlcj) substituída por engine nativa HTML5 `<video>` e window.open |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLCVideoBackgroundBaseUtils.java` | N/A | Lógica C++ (vlcj) substituída por engine nativa HTML5 `<video>` e window.open |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/vlc/VLCView.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/webservice/HolyricsWS.java` | `src/webservice/HolyricsWS.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/webservice/IHolyricsWS.java` | `src/webservice/IHolyricsWS.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/webservice/IRemoteControlBibleWS.java` | `src/webservice/IRemoteControlBibleWS.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/webservice/Pack.java` | `src/webservice/Pack.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/webservice/RemoteControlBibleWS.java` | `src/webservice/RemoteControlBibleWS.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/worker/BatchImportWorker.java` | `src/worker/BatchImportWorker.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/worker/CheckUploadAndExitWorker.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/worker/DeleteMyAccountWorker.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/worker/DownloadWorker.java` | `src/worker/DownloadWorker.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/worker/MusicImportWorker.java` | `src/worker/MusicImportWorker.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/worker/SelectMyVideosWorker.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/worker/UpdateAndExitWorker.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/worker/UpdateFromInternetWorker.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/worker/UpdateFromJsonWorker.java` | `src/worker/UpdateFromJsonWorker.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/holyrics/worker/UploadWorker.java` | `src/worker/UploadWorker.ts` | Classe recriada e funcional no ecossistema TS/React. |
| ⏳ Pendente | `../holyrics-source/legacy-java/com/limagiran/holyrics/zip/Zip.java` | (a migrar) | Secundário aguardando priorização (Ondas Futuras). |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/javaupdate/IUpdate.java` | N/A | Obsoleto/Dependência de Terceiro |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/javaupdate/JavaUpdate.java` | N/A | Obsoleto/Dependência de Terceiro |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/javaupdate/Update.java` | N/A | Obsoleto/Dependência de Terceiro |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/javaupdate/util/Download.java` | N/A | Obsoleto/Dependência de Terceiro |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/javaupdate/util/InsertShortcut.java` | N/A | Obsoleto/Dependência de Terceiro |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/javaupdate/util/MySwingWorker.java` | N/A | Obsoleto/Dependência de Terceiro |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/javaupdate/util/PopUpWorker.java` | N/A | Obsoleto/Dependência de Terceiro |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/javaupdate/util/RestartWorker.java` | N/A | Obsoleto/Dependência de Terceiro |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/javaupdate/util/UpdateWorker.java` | N/A | Obsoleto/Dependência de Terceiro |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/javaupdate/util/Utils.java` | `src/util/Utils.ts` | Classe recriada e funcional no ecossistema TS/React. |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/lgsaveopen/I18N.java` | N/A | Obsoleto/Dependência de Terceiro |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/lgsaveopen/LGSaveOpen.java` | N/A | Obsoleto/Dependência de Terceiro |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/lgsaveopen/SerialMap.java` | N/A | Obsoleto/Dependência de Terceiro |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/lgsaveopen/Utils.java` | `src/util/Utils.ts` | Classe recriada e funcional no ecossistema TS/React. |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/vagalumeapi/Artist.java` | N/A | API legada / substituída por fetch nativo nas views |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/vagalumeapi/Doc.java` | N/A | API legada / substituída por fetch nativo nas views |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/vagalumeapi/Download.java` | N/A | API legada / substituída por fetch nativo nas views |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/vagalumeapi/Logo.java` | N/A | API legada / substituída por fetch nativo nas views |
| ✅ Concluído | `../holyrics-source/legacy-java/com/limagiran/vagalumeapi/Music.java` | `src/model/Music.ts` | Classe recriada e funcional no ecossistema TS/React. |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/vagalumeapi/Response.java` | N/A | API legada / substituída por fetch nativo nas views |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/vagalumeapi/Result.java` | N/A | API legada / substituída por fetch nativo nas views |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/vagalumeapi/Search.java` | N/A | API legada / substituída por fetch nativo nas views |
| 🗑️ Descartado | `../holyrics-source/legacy-java/com/limagiran/vagalumeapi/VagalumeAPI.java` | N/A | API legada / substituída por fetch nativo nas views |


## Resumo Geral
- **Total de Arquivos Java:** 484
- **Arquivos Portados (Core Migrado):** 99
- **Arquivos Descartados (Justificados):** 133
- **Arquivos Pendentes (A Migrar):** 252
- **Progresso de Conversão (Efetivo):** 47.93%
