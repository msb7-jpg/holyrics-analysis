package com.limagiran.holyrics.share;

import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.model.theme.BackgroundVideo;
import com.limagiran.holyrics.webservice.HolyricsWS;
import java.util.Arrays;
import java.util.Objects;

public class ShareFilesAppJson {
   private ShareFilesApp.MusicApp.Music[] musics = new ShareFilesApp.MusicApp.Music[0];
   private ShareFilesApp.ThemeList.Theme[] themes = new ShareFilesApp.ThemeList.Theme[0];

   public static ShareFilesAppJson create() {
      try {
         ShareFilesAppJson _return = new ShareFilesAppJson();
         _return.musics = MusicDao.getAll().stream().map(ShareFilesApp.MusicApp.Music::new).toArray(ShareFilesApp.MusicApp.Music[]::new);
         _return.themes = ThemeController.getThemes()
            .stream()
            .filter(t -> !BackgroundVideo.class.isInstance(t.getBackground()))
            .map(ShareFilesApp.ThemeList::create)
            .filter(Objects::nonNull)
            .toArray(ShareFilesApp.ThemeList.Theme[]::new);
         Arrays.sort(_return.musics, ShareFilesApp.MusicApp.Music.sort());
         return _return;
      } catch (Exception var1) {
         return new ShareFilesAppJson();
      }
   }

   public String toJSon() {
      return HolyricsWS.GSON.toJson(this);
   }
}
