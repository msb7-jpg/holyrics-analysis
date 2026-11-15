package com.limagiran.holyrics.util;

import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.limagiran.bible.Verse;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.control.ProfileController;
import com.limagiran.holyrics.control.WorshipController;
import com.limagiran.holyrics.edit.EditProfile;
import com.limagiran.holyrics.edit.EditWorship;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.hitem.item.HItemAudio;
import com.limagiran.holyrics.hitem.item.HItemMusic;
import com.limagiran.holyrics.hitem.item.HItemVerse;
import com.limagiran.holyrics.hitem.item.HItemVideo;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.model.History;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Profile;
import com.limagiran.holyrics.model.Worship;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ListModelOrder;
import com.limagiran.holyrics.swing.ListModelVerse;
import com.limagiran.holyrics.view.BibleMain;
import com.limagiran.holyrics.view.CommunicationPanel;
import com.limagiran.holyrics.view.EditMusicOrderDialog;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.view.panel.SchedulePanelGrid;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.ListModel;

public class PopUpMenu {
   public static void schedulePopUpPlayList(SchedulePanelGrid view, JList<Music> jlist, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      if (view.scheduleType.withShowButtons && SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint())) {
         JMenuItem show = new JMenuItem(I18N.WORD_SHOW.val(), HItemUtils.IC_PLAY);
         show.setAccelerator(KeyStroke.getKeyStroke(116, 0));
         show.addActionListener(ae -> view.show(jlist.getSelectedValuesList()));
         menu.add(show);
      }

      List<Music> list = jlist.getSelectedValuesList();
      boolean pointOn = SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint());
      boolean separatorFlag = false;
      if (pointOn) {
         menu.addSeparator();
         String format = "<html>%s <span style=\"font-family: Franklin Gothic Medium; font-size: 90%%; color: "
            + Utils.getHexColor(StyleUI.FOREGROUND_LIGHT)
            + "\">(%s)</span>";
         Date d = new Date();
         if (list.stream().anyMatch(m -> History.minutesBetweenLastPlayed(m, d) >= 120L)) {
            separatorFlag = true;
            JMenuItem setToMusicPlayed = new JMenuItem(
               String.format(format, I18N.MSG_SET_TO_MUSIC_PLAYED.val(), I18N.WORD_NOW.val()), new ImageIcon(Images.ICON_PLAYED_MUSIC_16)
            );
            setToMusicPlayed.setAccelerator(KeyStroke.getKeyStroke(80, 128));
            setToMusicPlayed.addActionListener(ae -> list.stream().forEach(MusicMain::staticPlayedMusic));
            menu.add(setToMusicPlayed);
         }

         Event event = view.getEvent();
         if (Objects.nonNull(event)
            && event.getDate().before(Calendar.getInstance())
            && list.stream().anyMatch(m -> !History.isPlayed(m, event.getDate().getTime()))) {
            separatorFlag = true;
            JMenuItem setToMusicPlayed = new JMenuItem(
               String.format(format, I18N.MSG_SET_TO_MUSIC_PLAYED.val(), I18N.MSG_EVENT_TIME.val()), new ImageIcon(Images.ICON_PLAYED_MUSIC_16)
            );
            Date date = event.getDate().getTime();
            setToMusicPlayed.addActionListener(ae -> list.stream().forEach(m -> MusicMain.staticPlayedMusic(m, date)));
            menu.add(setToMusicPlayed);
         }

         if (list.size() == 1) {
            separatorFlag = true;
            IScheduleTask.setupPopupMenu(menu, new HItemMusic(list.get(0).getId()));
         }
      }

      if (pointOn) {
         if (separatorFlag) {
            menu.addSeparator();
         }

         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val(), new ImageIcon(Images.ICON_REMOVE_16));
         remove.setAccelerator(KeyStroke.getKeyStroke(127, 0));
         remove.addActionListener(ae -> SwingUtils.removeSelectedIndices(jlist, false));
         menu.add(remove);
      }

      if (menu.getSubElements().length > 0) {
         menu.show(jlist, e.getX(), e.getY());
      }
   }

   public static void schedulePopUpHItemPlayList(boolean showActions, JList<IHItem> jlist, MouseEvent e) {
      WebPopupMenu popup = new WebPopupMenu();
      List<IHItem> selection = jlist.getSelectedValuesList();
      IHItem selected = selection.isEmpty() ? null : selection.get(0);
      boolean pointOn = SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint());
      boolean onlyOne = selection.size() == 1;
      if (showActions && pointOn && onlyOne && selected != null) {
         HItemUtils.setupPopupMenuAction(popup, selected.getActionName(), selected::favoriteAction, selected.listPathIfDirectory(), false);
         popup.addSeparator();
      }

      boolean separatorFlag = false;
      if (onlyOne && (selected instanceof HItemAudio || selected instanceof HItemVideo)) {
         separatorFlag = true;
         HItemUtils.setupPopupMenuAddPlaylistPlayer(popup, selected.getAsFile());
      }

      if (onlyOne && selected instanceof IHItem) {
         separatorFlag = true;
         HItemUtils.setupPopupMenuAddFavorite(popup, selected);
         IScheduleTask.setupPopupMenu(popup, selected);
      }

      if (pointOn) {
         if (separatorFlag) {
            popup.addSeparator();
         }

         WebMenuItem remove = new WebMenuItem(I18N.WORD_REMOVE.val(), new ImageIcon(Images.ICON_REMOVE_16));
         remove.setAccelerator(KeyStroke.getKeyStroke(127, 0));
         remove.addActionListener(ae -> SwingUtils.removeSelectedIndices(jlist, false));
         popup.add(remove);
      }

      if (popup.getSubElements().length > 0) {
         popup.show(jlist, e.getX(), e.getY());
      }
   }

   public static void mainPopUpPlayList(MusicMain m, JList<Music> jlist, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      if (SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint())) {
         JMenuItem show = new JMenuItem(I18N.WORD_SHOW.val(), HItemUtils.IC_PLAY);
         show.setAccelerator(KeyStroke.getKeyStroke(116, 0));
         show.addActionListener(ae -> m.showMusic(jlist.getSelectedValuesList()));
         menu.add(show);
      }

      boolean pointOn = SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint());
      List<Music> list = jlist.getSelectedValuesList();
      boolean separatorFlag = false;
      if (pointOn) {
         menu.addSeparator();
         Date d = new Date();
         if (list.stream().anyMatch(music -> History.minutesBetweenLastPlayed(music, d) >= 120L)) {
            separatorFlag = true;
            JMenuItem setToMusicPlayed = new JMenuItem(I18N.MSG_SET_TO_MUSIC_PLAYED.val(), new ImageIcon(Images.ICON_PLAYED_MUSIC_16));
            setToMusicPlayed.setAccelerator(KeyStroke.getKeyStroke(80, 128));
            setToMusicPlayed.addActionListener(ae -> list.stream().forEach(MusicMain::staticPlayedMusic));
            menu.add(setToMusicPlayed);
         }

         if (list.size() == 1) {
            separatorFlag = true;
            IScheduleTask.setupPopupMenu(menu, new HItemMusic(list.get(0).getId()));
         }
      }

      if (pointOn) {
         if (separatorFlag) {
            menu.addSeparator();
         }

         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val(), new ImageIcon(Images.ICON_REMOVE_16));
         remove.setAccelerator(KeyStroke.getKeyStroke(127, 0));
         remove.addActionListener(ae -> m.removeSelectedMusicsFromPlayList(jlist));
         menu.add(remove);
      }

      if (menu.getSubElements().length > 0) {
         menu.show(jlist, e.getX(), e.getY());
      }
   }

   public static void mainPopUpOrder(MusicMain parent, JList<MusicController.Paragraph> jlist, MouseEvent e) {
      ListModel model = jlist.getModel();
      if (model instanceof ListModelOrder) {
         Music music = ((ListModelOrder)model).getMusic();
         if (music != null) {
            JPopupMenu menu = new JPopupMenu();
            JMenuItem reorder = new JMenuItem(I18N.MSG_SET_UP_ORDER.val());
            reorder.addActionListener(evt -> EditMusicOrderDialog.main(parent, music, () -> {
                  ((ListModelOrder)model).setMusic(music);
                  jlist.setSelectedIndex(0);
               }));
            menu.add(reorder);
            menu.show(jlist, e.getX(), e.getY());
         }
      }
   }

   public static void mainPopUpMusics(MusicMain m, JList<Music> list, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      Music music = list.getSelectedValue();
      boolean pointOn = SwingUtils.pointOnTheSelectedItem(list, e.getPoint());
      if (pointOn) {
         JMenuItem show = new JMenuItem(I18N.WORD_SHOW.val(), HItemUtils.IC_PLAY);
         show.setAccelerator(KeyStroke.getKeyStroke(116, 0));
         show.addActionListener(ae -> m.showMusic(list.getSelectedValuesList()));
         menu.add(show);
      }

      if (pointOn && !m.playListContains(music)) {
         menu.addSeparator();
         JMenuItem addPlaylist = new JMenuItem(I18N.MSG_ADD_PLAY_LIST.val(), new ImageIcon(Images.ICON_LIST_16));
         addPlaylist.setAccelerator(KeyStroke.getKeyStroke(10, 512));
         addPlaylist.addActionListener(ae -> m.addPlayList(music));
         menu.add(addPlaylist);
      }

      HItemMusic hitem = pointOn && music != null ? new HItemMusic(music.getId()) : null;
      if (pointOn && hitem != null) {
         HItemUtils.setupPopupMenuAddFavorite(menu, hitem);
         JMenuItem addPlaylistMedia = new JMenuItem(I18N.MSG_ADD_PLAY_LIST.val(), new ImageIcon(Images.ICON_PLAY_LIST_PLAYER_16));
         SwingUtils.addHtmlTooltip(addPlaylistMedia, "(" + I18N.WORD_MEDIA.val() + ")");
         addPlaylistMedia.addActionListener(ae -> m.addPlayList(hitem));
         addPlaylistMedia.setAccelerator(KeyStroke.getKeyStroke(10, 640));
         addPlaylistMedia.setEnabled(!m.playListContains(hitem));
         menu.add(addPlaylistMedia);
         IScheduleTask.setupPopupMenu(menu, hitem);
      }

      if (pointOn) {
         JMenuItem edit = new JMenuItem(I18N.WORD_EDIT.val(), new ImageIcon(Images.ICON_EDIT_16));
         edit.setAccelerator(KeyStroke.getKeyStroke(113, 0));
         edit.addActionListener(ae -> m.editMusic(music));
         menu.add(edit);
      }

      if (pointOn) {
         WebMenu addProfile = new WebMenu(I18N.MSG_ADD_PROFILE.val(), new ImageIcon(Images.ICON_PROFILE_16));
         List<Profile> profiles = ProfileController.getProfilesInOrderOfAmount(false);
         profiles.stream().filter(p -> !p.containsMusic(music)).forEach(p -> {
            JMenuItem secAdd = new JMenuItem(p.toString());
            secAdd.addActionListener(ae -> p.addMusics(music));
            addProfile.add(secAdd);
         });
         if (!profiles.isEmpty()) {
            menu.add(addProfile);
         }
      }

      if (pointOn) {
         JMenu removeProfile = new JMenu(I18N.MSG_REMOVE_PROFILE.val());
         ProfileController.getProfilesInOrderOfAmount(false).stream().filter(p -> p.containsMusic(music)).forEach(p -> {
            JMenuItem secAdd = new JMenuItem(p.toString());
            secAdd.addActionListener(ae -> p.removeMusic(music));
            removeProfile.add(secAdd);
         });
         if (removeProfile.getSubElements().length > 0) {
            menu.add(removeProfile);
         }
      }

      if (menu.getSubElements().length > 0) {
         menu.show(list, e.getX(), e.getY());
      }
   }

   public static void mainPopUpHistory(MusicMain m, JList<Date> jlist, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      if (SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint())) {
         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val(), new ImageIcon(Images.ICON_REMOVE_16));
         remove.addActionListener(ae -> m.removeSelectedHistories());
         menu.add(remove);
      }

      if (menu.getSubElements().length > 0) {
         menu.show(jlist, e.getX(), e.getY());
      }
   }

   public static void mainPopUpThemes(MusicMain m, JList<Theme> jlist, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      boolean pointOn = SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint());
      if (pointOn) {
         JMenuItem edit = new JMenuItem(I18N.WORD_EDIT.val(), new ImageIcon(Images.ICON_EDIT_16));
         edit.addActionListener(ae -> m.editTheme(jlist.getSelectedValue()));
         menu.add(edit);
      }

      if (pointOn) {
         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val(), new ImageIcon(Images.ICON_REMOVE_16));
         remove.addActionListener(ae -> m.removeTheme(jlist.getSelectedValue()));
         menu.add(remove);
      }

      if (menu.getSubElements().length > 0) {
         menu.show(jlist, e.getX(), e.getY());
      }
   }

   public static void settingsPopUpWorship(EditWorship set, JList<Worship> jlist, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      Worship w = jlist.getSelectedValue();
      boolean pointOn = SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint());
      if (pointOn) {
         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val(), new ImageIcon(Images.ICON_REMOVE_16));
         remove.addActionListener(ev -> set.removeWorship(jlist.getSelectedValue()));
         menu.add(remove);
      }

      if (pointOn && w != null && w.isActive()) {
         JMenuItem edit = new JMenuItem(I18N.WORD_EDIT.val(), new ImageIcon(Images.ICON_EDIT_16));
         edit.addActionListener(ev -> set.editWorship(w));
         menu.add(edit);
      }

      if (pointOn && w != null) {
         JMenuItem active = new JMenuItem(w.isActive() ? I18N.WORD_DEACTIVATE.val() : I18N.WORD_ACTIVATE.val(), new ImageIcon(Images.ICON_CHECK_BLACK_16));
         active.addActionListener(ev -> {
            w.setActive(!w.isActive());
            WorshipController.save();
            set.update();
         });
         menu.add(active);
      }

      if (menu.getSubElements().length > 0) {
         menu.show(jlist, e.getX(), e.getY());
      }
   }

   public static void profileEditPopUpMusics(EditProfile frame, JList<Music> jlist, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      if (SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint())) {
         JMenuItem addMusic = new JMenuItem(I18N.WORD_ADD.val());
         addMusic.addActionListener(ae -> frame.addSelectedMusics());
         menu.add(addMusic);
      }

      if (menu.getSubElements().length > 0) {
         menu.show(jlist, e.getX(), e.getY());
      }
   }

   public static void profileEditPopUpMusicsProfile(EditProfile frame, JList<Music> jlist, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      if (SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint())) {
         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val());
         remove.addActionListener(ae -> frame.removeSelectedMusics());
         menu.add(remove);
      }

      if (menu.getSubElements().length > 0) {
         menu.show(jlist, e.getX(), e.getY());
      }
   }

   public static void profileEditPopUpProfile(EditProfile frame, JList<Profile> jlist, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      if (SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint())) {
         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val());
         remove.addActionListener(ae -> frame.removeSelectedProfile());
         menu.add(remove);
      }

      if (menu.getSubElements().length > 0) {
         menu.show(jlist, e.getX(), e.getY());
      }
   }

   public static void biblePopUpVerses(BibleMain m, JList<Verse> list, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      boolean pointOn = SwingUtils.pointOnTheSelectedItem(list, e.getPoint());
      if (pointOn && !m.favoriteContains(list.getSelectedValue())) {
         JMenuItem send = new JMenuItem(I18N.MSG_ADD_TO_FAVORITE.val(), new ImageIcon(Images.ICON_FAVORITE_16));
         send.addActionListener(ae -> m.addPlayList(list.getSelectedValue()));
         menu.add(send);
      }

      if (pointOn) {
         JMenuItem show = new JMenuItem(I18N.WORD_SHOW.val(), HItemUtils.IC_PLAY);
         show.addActionListener(ae -> m.showVerse(list.getSelectedValue()));
         menu.add(show);
         Verse verse = list.getSelectedValue();
         if (verse != null && list.getModel() instanceof ListModelVerse) {
            ListModelVerse model = (ListModelVerse)list.getModel();
            EnumVerseDescription en = EnumVerseDescription.valueOfOrDefault(Settings.EnumSettings.BIBLE_VERSE_DESCRIPTION.getString());
            List<Verse> verses = BibleSlideUtils.getVerses(verse, model.copyList(), Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt());
            String header = !verses.isEmpty() ? en.getHeader(verses, Settings.EnumSettings.BIBLE_SHOW_VERSION.is()) : "";
            String content = verses.stream()
               .map(en.getContentFunction(Settings.EnumSettings.BIBLE_UPPERCASE.is(), Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt() > 1))
               .collect(Collectors.joining(Settings.EnumSettings.BIBLE_VERSE_EMPTY_LINE_BETWEEN.is() ? "\n\n" : "\n"));
            String versesTxt = (header.trim().isEmpty() ? "" : header + "\n\n") + content;
            menu.addSeparator();
            JMenuItem copy = new JMenuItem(I18N.WORD_COPY.val(), new ImageIcon(Images.load("textFields16.png")));
            copy.addActionListener(ae -> Utils.setClipboard(StringUtils.removeExcessLineBreak(versesTxt)));
            menu.add(copy);
            menu.addSeparator();
            JMenuItem sendToCommunicationPanel = new JMenuItem(I18N.MSG_SEND_TO_COMMUNICATION_PANEL.val(), new ImageIcon(Images.load("member16.png")));
            sendToCommunicationPanel.addActionListener(ae -> {
               CommunicationPanel.setPresentationText(versesTxt);
               CommunicationPanel.openCommunication();
            });
            menu.add(sendToCommunicationPanel);
            JMenuItem displayOnTheCommunicationPanel = new JMenuItem(
               I18N.MSG_DISPLAY_ON_THE_COMMUNICATION_PANEL.val(), new ImageIcon(Images.load("slideshow16.png"))
            );
            displayOnTheCommunicationPanel.addActionListener(ae -> {
               CommunicationPanel.setPresentationText(versesTxt);
               CommunicationPanel.setPresentationShow(true);
               CommunicationPanel.openCommunication();
            });
            menu.add(displayOnTheCommunicationPanel);
            menu.addSeparator();
            IScheduleTask.setupPopupMenu(menu, new HItemVerse(verse));
         }
      }

      if (menu.getSubElements().length > 0) {
         menu.show(list, e.getX(), e.getY());
      }
   }

   public static void biblePopUpPlayList(BibleMain m, JList<Verse> jlist, MouseEvent e) {
      JPopupMenu menu = new JPopupMenu();
      boolean pointOn = SwingUtils.pointOnTheSelectedItem(jlist, e.getPoint());
      if (pointOn) {
         JMenuItem show = new JMenuItem(I18N.WORD_SHOW.val(), HItemUtils.IC_PLAY);
         show.addActionListener(ae -> m.showVerse(jlist.getSelectedValue()));
         menu.add(show);
      }

      Verse verse = jlist.getSelectedValue();
      if (pointOn && verse != null) {
         IScheduleTask.setupPopupMenu(menu, new HItemVerse(verse));
      }

      if (pointOn) {
         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val(), new ImageIcon(Images.ICON_REMOVE_16));
         remove.addActionListener(ae -> m.removeFromFavorite(jlist.getSelectedValue()));
         menu.add(remove);
      }

      if (pointOn) {
         JMenuItem removeAll = new JMenuItem(I18N.MSG_REMOVE_ALL.val(), new ImageIcon(Images.ICON_CLEAR_ALL_16));
         removeAll.addActionListener(ae -> m.clearPlayList());
         menu.add(removeAll);
      }

      if (menu.getSubElements().length > 0) {
         menu.show(jlist, e.getX(), e.getY());
      }
   }
}
