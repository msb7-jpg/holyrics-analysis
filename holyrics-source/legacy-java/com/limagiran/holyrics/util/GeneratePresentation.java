package com.limagiran.holyrics.util;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.model.CountdownSettings;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.model.theme.BackgroundImageFile;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.playback.ExportMediaWorker;
import com.limagiran.holyrics.playback.PlayBack;
import com.limagiran.holyrics.playback.PlayBackPlayerMiniShow;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.view.AutomaticShowView;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.view.PresentationView;
import com.limagiran.holyrics.view.PresentationViewRemoteControl;
import com.limagiran.holyrics.view.panel.HItemNextPresentationPanel;
import com.limagiran.holyrics.view.panel.PresentationControllerViewFooter;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class GeneratePresentation {
   public static void showPresentation(MusicMain parent, Theme theme, List<Presentation> list, int initIndex, ScreenDeviceSettings.EnumDeviceType deviceType) {
      if (!list.isEmpty()) {
         GeneratePresentationThreadUtils.put(() -> {
            if (VideoUtils.isSingleMonitor()) {
               PresentationView.create(list, null, initIndex, false, theme, deviceType).setVisible(true);
            } else {
               parent.setFooterControl(new PresentationControllerViewFooter(parent, list, initIndex, theme, deviceType));
            }
         });
      }
   }

   public static void show(MusicMain parent, Theme theme, List<Music> musics, int initIndex) {
      showPresentation(parent, theme, getPresentations(musics, theme), initIndex, ScreenDeviceSettings.EnumDeviceType.MUSIC);
   }

   public static void show(MusicMain parent, List<Announcement> list) {
      List<Presentation> _list = new ArrayList<>(list.stream().map(Announcement::createPresentation).collect(Collectors.toList()));
      showPresentation(parent, Theme.BLACK_SCREEN, _list, 0, ScreenDeviceSettings.EnumDeviceType.ANNOUNCEMENT);
   }

   public static List<String> showRemoteControl(Music music, Theme theme, long id) {
      List<Presentation> list = new ArrayList<>();
      MusicController.Paragraph paragraph = music.getTitleAndArtistParagraph();
      list.add(Presentation.create(paragraph, theme));
      List<MusicController.Paragraph> p = music.getParagraphsToPresentation();

      for (int i = 0; i < p.size(); i++) {
         MusicController.Paragraph text = p.get(i).setPageCount(i + 1, p.size());
         text.setText(Settings.EnumSettings.MUSIC_UPPERCASE.is() ? text.getText().toUpperCase() : text.getText());
         list.add(Presentation.create(text, theme));
      }

      if (list.isEmpty()) {
         list.add(Presentation.BLACK_SCREEN);
      }

      BooleanOptional.runIfTrue(!list.isEmpty(), () -> PresentationViewRemoteControl.show(id, list, music, theme, ScreenDeviceSettings.EnumDeviceType.MUSIC));
      return list.stream().map(ix -> ix.text.toString()).collect(Collectors.toList());
   }

   public static List<String> showRemoteQuickPresentation(Music music, Theme theme, long id) {
      List<Presentation> list = new ArrayList<>();
      List<MusicController.Paragraph> p = music.getParagraphsToPresentation();
      p.forEach(ix -> ix.setPageCount(0, 0).setTitleAndArtist(""));

      for (int i = 0; i < p.size(); i++) {
         MusicController.Paragraph text = p.get(i);
         text.setText(Settings.EnumSettings.MUSIC_UPPERCASE.is() ? text.getText().toUpperCase() : text.getText());
         list.add(Presentation.create(text, theme));
      }

      if (list.isEmpty()) {
         list.add(Presentation.BLACK_SCREEN);
      }

      BooleanOptional.runIfTrue(!list.isEmpty(), () -> PresentationViewRemoteControl.show(id, list, music, theme, ScreenDeviceSettings.EnumDeviceType.TEXT));
      return list.stream().map(ix -> ix.text.toString()).collect(Collectors.toList());
   }

   public static List<Presentation> createPresentations(Music m, Theme t) {
      List<Presentation> list = new ArrayList<>();
      MusicController.Paragraph paragraph = m.getTitleAndArtistParagraph();
      list.add(Presentation.create(paragraph, t));
      List<MusicController.Paragraph> p = m.getParagraphsToPresentation();

      for (int i = 0; i < p.size(); i++) {
         MusicController.Paragraph text = p.get(i);
         text.setPageCount(i + 1, p.size());
         text.setText(Settings.EnumSettings.MUSIC_UPPERCASE.is() ? text.getText().toUpperCase() : text.getText());
         list.add(Presentation.create(text, t));
      }

      return list;
   }

   public static void show(MusicMain parent, Theme theme, List<Music> musics) {
      show(parent, theme, musics, 0);
   }

   private static List<Presentation> getPresentations(List<Music> musics, Theme theme) {
      List<Presentation> _return = new ArrayList<>();
      musics.forEach(m -> {
         Theme themeFinal = m.getDefaultThemeOr(theme);
         MusicController.Paragraph paragraph = m.getTitleAndArtistParagraph();
         _return.add(Presentation.create(paragraph, themeFinal));
         List<MusicController.Paragraph> p = m.getParagraphsToPresentation();

         for (int i = 0; i < p.size(); i++) {
            MusicController.Paragraph text = p.get(i);
            text.setPageCount(i + 1, p.size());
            text.setText(Settings.EnumSettings.MUSIC_UPPERCASE.is() ? text.getText().toUpperCase() : text.getText());
            _return.add(Presentation.create(text, themeFinal));
         }

         _return.add(Presentation.create(new MusicController.Paragraph(), themeFinal, true));
      });
      return _return;
   }

   public static void showImages(final Window parent, final boolean automatic, final boolean repeat, final int sec, final File... files) {
      if (validatePlaylistImages(parent, files)) {
         final List list = new ArrayList();
         PopUpWorker.exe(new MySwingWorker<Void, Void>(parent, I18N.WORD_LOADING.val()) {
            protected Void doInBackground() throws Exception {
               this.setProgress(0);

               for (int i = 0; i < files.length; i++) {
                  list.add((new BackgroundImageFile(files[i])).presentation);
                  this.setProgress(Utils.getPercentageInt((double)(i + 1), (double)files.length));
               }

               return null;
            }

            @Override
            protected void done() {
               super.done();
               if (!this.isCancelled()) {
                  Optional.ofNullable(parent).ifPresent(Window::dispose);
                  if (automatic) {
                     AutomaticShowView.show(list, sec, repeat, ScreenDeviceSettings.EnumDeviceType.IMAGE);
                  } else {
                     GeneratePresentation.showPresentation(MusicMain.INSTANCE, Theme.BLACK_SCREEN, list, 0, ScreenDeviceSettings.EnumDeviceType.IMAGE);
                  }
               }
            }
         }, true);
      }
   }

   public static boolean validatePlaylistImages(Component parent, File... files) {
      if (files.length == 0) {
         Utils.error(parent, I18N.MSG_ERROR_EMPTY_LIST.val());
         return false;
      } else if (files.length > 60) {
         Utils.error(parent, I18N.MSG_MAXIMUM_AMOUNT_X.format(60));
         return false;
      } else {
         return true;
      }
   }

   public static boolean validatePlaylistImages(Component parent, List<File> files) {
      return validatePlaylistImages(parent, files.toArray(new File[0]));
   }

   public static void showCountdown(CountdownSettings set, Object nextPresentation) {
      Utils.startThreadDaemon(
         () -> {
            boolean determine = set.isDetermine();
            int n1 = determine ? set.getMinuteDetermine() : set.getHourExact();
            int n2 = determine ? set.getSecondDetermine() : set.getMinuteExact();
            long start = System.currentTimeMillis();
            String before = set.getTextBefore(true);
            String after = set.getTextAfter(true);
            boolean zerofill = set.isZeroFill();
            Presentation p = CountdownUtils.getPresentation(before, 0L, after, set.getThemeObject(), set.getSize(), zerofill);
            p.text.setText(" \n \n ");
            long id = Utils.generateMillisecondId();
            PresentationViewRemoteControl.show(id, new ArrayList<>(Arrays.asList(p)), Music.create(), p.theme, ScreenDeviceSettings.EnumDeviceType.TEXT);
            PresentationViewRemoteControl.staticSleep(id, 3000L);
            SwingUtilities.invokeLater(
               () -> {
                  boolean[] hackForceStop = new boolean[]{false};
                  final JFrame frame = new JFrame();
                  frame.setIconImages(Images.loadIcons("timer", ".png", 16, 24, 32, 48, 64));
                  frame.setTitle(I18N.WORD_COUNTDOWN.val());
                  frame.setDefaultCloseOperation(0);
                  frame.setLayout(new BorderLayout(0, 4));
                  frame.setResizable(false);
                  InsertShortcut.add(frame, InsertShortcut.Shortcut.create(27, () -> frame.setState(1)));
                  frame.addWindowListener(new WindowAdapter() {
                     @Override
                     public void windowClosing(WindowEvent e) {
                        frame.setState(1);
                     }
                  });
                  WebLabel lbl = new WebLabel(I18N.WORD_COUNTDOWN.val(), 0, new Object[0]).setFontSize(24);
                  WebButton btn = new WebButton(I18N.WORD_STOP_VERB.val()).setFontSize(18).setMargin(2, 8, 2, 8);
                  HItemNextPresentationPanel nextPresentationPanel = HItemNextPresentationPanel.create(nextPresentation);
                  btn.setFocusable(false);
                  btn.addActionListener(evt -> hackForceStop[0] = true);
                  frame.add(new WebPanel(new FlowLayout(), new Component[]{lbl}).setMargin(2, 32, 0, 32), "First");
                  frame.add(new WebPanel(new FlowLayout(), new Component[]{btn}).setMargin(0, 32, 2, 32), "Center");
                  frame.add(nextPresentationPanel, "Last");
                  frame.pack();
                  frame.setLocationRelativeTo(null);
                  frame.setVisible(true);
                  Utils.startThreadDaemon(
                     () -> {
                        try {
                           if (VideoUtils.isSingleMonitor()) {
                              Utils.startThreadDaemon(() -> {
                                 Utils.sleep(2000L);
                                 SwingUtilities.invokeLater(() -> PresentationViewRemoteControl.staticRequestFocus(id));
                              });
                           }

                           for (long lastSec = -1L; PresentationViewRemoteControl.staticId() == id; Utils.sleep(250L)) {
                              long sec;
                              if (determine) {
                                 sec = (long)(n1 * 60 + n2) - (System.currentTimeMillis() - start) / 1000L;
                              } else {
                                 sec = CountdownUtils.getSecondsExact(n1, n2);
                              }

                              if (sec < 0L || sec > 86000L || hackForceStop[0]) {
                                 PresentationViewRemoteControl.staticExit();
                                 break;
                              }

                              if (sec != lastSec) {
                                 lastSec = sec;
                                 p.text.setText(CountdownUtils.getTextToPresentation(before, sec, after, zerofill));
                                 SwingUtilities.invokeLater(
                                    () -> PresentationViewRemoteControl.staticSetSinglePresentation(p, ScreenDeviceSettings.EnumDeviceType.TEXT, false)
                                 );
                              }
                           }
                        } finally {
                           Favoritable nextP = nextPresentationPanel.getSelectedItemAsFavoritable();
                           if (!hackForceStop[0] && nextP != null) {
                              nextP.favoriteAction(true);
                           }

                           SwingUtilities.invokeLater(() -> {
                              frame.setVisible(false);
                              frame.dispose();
                              frame.removeAll();
                              frame.getContentPane().removeAll();
                           });
                        }
                     }
                  );
               }
            );
         }
      );
   }

   public static void showCountdown(CountdownSettings set) {
      showCountdown(set, null);
   }

   public static void showAutomaticPresentation(PlayBack playBack, Theme theme) {
      ExportMediaWorker.main(
         playBack,
         file -> Utils.startThreadDaemon(
               () -> {
                  long id = Utils.generateMillisecondId();
                  int idx = playBack.getIndexAt(0L);
                  Presentation presentation = playBack.getPresentationAtFromIndex(idx, theme);
                  presentation.text.setSource(playBack.getPresentationAtFromIndex(Math.min(idx + 1, 0), theme));
                  PresentationViewRemoteControl.show(
                     id, new ArrayList<>(Arrays.asList(presentation)), Music.create(), theme, ScreenDeviceSettings.EnumDeviceType.MUSIC
                  );
                  PresentationViewRemoteControl.staticSleep(id, 3000L);
                  if (PresentationViewRemoteControl.staticId() == id) {
                     PlayBackPlayerMiniShow player = PlayBackPlayerMiniShow.getInstance();
                     Utils.sleep(1000L);
                     player.setMedia(file, true);
                     player.playPause();

                     for (long delay = 0L; !PlayBackPlayerMiniShow.isPlaying() && delay < 2000L; delay += 100L) {
                        Utils.sleep(100L);
                     }

                     if (PlayBackPlayerMiniShow.isPlaying()) {
                        SwingUtilities.invokeLater(
                           () -> {
                              boolean[] hackForceStop = new boolean[]{false};
                              final JFrame frame = new JFrame();
                              frame.setIconImages(Images.loadIcons("musicVideo", ".png", 16, 24, 32));
                              frame.setTitle(I18N.MSG_AUTOMATIC_SHOW.val());
                              frame.setDefaultCloseOperation(0);
                              frame.setLayout(new BorderLayout(0, 4));
                              frame.setResizable(false);
                              InsertShortcut.add(frame, InsertShortcut.Shortcut.create(27, () -> frame.setState(1)));
                              frame.addWindowListener(new WindowAdapter() {
                                 @Override
                                 public void windowClosing(WindowEvent e) {
                                    frame.setState(1);
                                 }
                              });
                              WebLabel lbl = new WebLabel(I18N.MSG_AUTOMATIC_SHOW.val(), 0, new Object[0]).setFontSize(24);
                              Music m = playBack.getMusic();
                              String htmlText = String.format("<b>%s</b><br>%s", m.getTitle(), m.getArtist());
                              WebLabel lblInfo = new WebLabel(SwingUtils.getHTMLCenterJLabel(htmlText), 0, new Object[0]).setFontSize(24);
                              WebButton btn = new WebButton(I18N.WORD_CLOSE_VERB.val()).setFontSize(18).setMargin(2, 8, 2, 8);
                              btn.setFocusable(false);
                              btn.addActionListener(evt -> hackForceStop[0] = true);
                              WebPanel panelCenter = new WebPanel(new BorderLayout(0, 8));
                              panelCenter.setOpaque(false);
                              panelCenter.add(lblInfo, "First");
                              panelCenter.add(player, "Center");
                              frame.add(new WebPanel(new FlowLayout(), new Component[]{lbl}).setMargin(2, 32, 0, 32), "First");
                              frame.add(new WebPanel(new FlowLayout(), new Component[]{panelCenter}).setMargin(0, 32, 2, 32), "Center");
                              frame.add(new WebPanel(new FlowLayout(), new Component[]{btn}).setMargin(0, 32, 2, 32), "Last");
                              frame.pack();
                              frame.setLocationRelativeTo(null);
                              frame.setVisible(true);
                              Utils.startThreadDaemon(
                                 () -> {
                                    try {
                                       if (VideoUtils.isSingleMonitor()) {
                                          Utils.startThreadDaemon(() -> {
                                             Utils.sleep(2000L);
                                             SwingUtilities.invokeLater(() -> PresentationViewRemoteControl.staticRequestFocus(id));
                                          });
                                       }

                                       int lastIndex = 0;

                                       for (long dif = MyProperties.EnumProperties.AUTOMATIC_PRESENTATION_DELAY.getLong();
                                          PresentationViewRemoteControl.staticId() == id;
                                          Utils.sleep(150L)
                                       ) {
                                          if (hackForceStop[0] || player.isStopped() || !player.isCurrentMedia(file)) {
                                             PresentationViewRemoteControl.staticExitAutomaticShow();
                                             if (player.isCurrentMedia(file)) {
                                                player.setMedia(null, true);
                                             }
                                             break;
                                          }

                                          long milli = player.getTimeMillis() + dif;
                                          int index = playBack.getIndexAt(milli);
                                          if (index != lastIndex) {
                                             lastIndex = index;
                                             Presentation p = playBack.getPresentationAtFromIndex(index, theme);
                                             p.text.setSource(playBack.getPresentationAtFromIndex(Math.min(index + 1, 0), theme));
                                             SwingUtilities.invokeLater(
                                                () -> PresentationViewRemoteControl.staticSetSinglePresentation(
                                                      p, ScreenDeviceSettings.EnumDeviceType.MUSIC, false
                                                   )
                                             );
                                          }
                                       }
                                    } finally {
                                       SwingUtilities.invokeLater(() -> {
                                          if (player.isCurrentMedia(file)) {
                                             player.setMedia(null, true);
                                          }

                                          frame.setVisible(false);
                                          frame.dispose();
                                       });
                                    }
                                 }
                              );
                           }
                        );
                     }
                  }
               }
            )
      );
   }
}
