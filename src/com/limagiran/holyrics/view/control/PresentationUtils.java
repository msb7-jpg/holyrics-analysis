package com.limagiran.holyrics.view.control;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.control.AnnouncementController;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.EnumSlideSizePreview;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WebLafUtils;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class PresentationUtils {
   public static void editParagraph(String text, Theme themePreview, boolean removeLineBreak, Consumer<String> newValue) {
      JTextArea ta = new JTextArea(text, 10, 30);
      BufferedImage icon = PresentationSlideUtils.createThumb(text, themePreview, EnumSlideSizePreview.BIG.size);
      WebLabel slidePreview = new WebLabel(new ImageIcon(icon), 0);
      slidePreview.setSize(EnumSlideSizePreview.BIG.size);
      slidePreview.setPreferredSize(EnumSlideSizePreview.BIG.size);
      ta.getDocument().addDocumentListener(new DocumentListenerAuto(500L, () -> Utils.startThreadDaemon(() -> {
            String _text = ta.getText();
            if (removeLineBreak) {
               _text = StringUtils.removeExcessLineBreak(_text);
            }

            BufferedImage bi = PresentationSlideUtils.createThumb(_text, themePreview, EnumSlideSizePreview.BIG.size);
            SwingUtilities.invokeLater(() -> slidePreview.setIcon(new ImageIcon(bi)));
         })));
      ta.setCaretPosition(ta.getDocument().getLength());
      WebPanel panel = new WebPanel(new BorderLayout(16, 0));
      panel.setOpaque(false);
      panel.add(ta, "Center");
      panel.add(slidePreview, "East");
      PopupUtils.dialogOkCancel(panel, I18N.MSG_EDIT_TEXT.val(), flag -> {
         if (flag) {
            String _text = ta.getText();
            if (removeLineBreak) {
               _text = StringUtils.removeExcessLineBreak(_text);
            }

            newValue.accept(!_text.isEmpty() ? _text : " ");
         }
      });
   }

   public static void editParagraph(String text, boolean removeLineBreak, Consumer<String> newValue) {
      editParagraph(text, EnumSlideSizePreview.THEME_UNSELECTED, removeLineBreak, newValue);
   }

   public static boolean checkEditRealTime(Presentation p) {
      if (p == null) {
         return false;
      } else if (p.theme == Theme.BLACK_SCREEN) {
         return false;
      } else if (p.text.getSource() instanceof String) {
         return p.text.getSource().equals("quick-presentation");
      } else if (!(p.text.getSource() instanceof Music) && !(p.text.getSource() instanceof Announcement)) {
         return false;
      } else if (p.text.getSource() instanceof Music) {
         if (p.text.getSlideType().isTitle()) {
            return true;
         } else {
            int slideIndex = p.text.getPageCount().x;
            return slideIndex > 0 && slideIndex != Integer.MAX_VALUE;
         }
      } else {
         return true;
      }
   }

   public static void editRealTime(Presentation p, Runnable callback) {
      try {
         if (!checkEditRealTime(p)) {
            return;
         }

         if (p.text.getSource() instanceof Music) {
            if (p.text.getSlideType().isTitle()) {
               editMusicTitleAndArtistRealTime(p, (Music)p.text.getSource(), callback);
            } else {
               editMusicLyricsRealTime(p, (Music)p.text.getSource(), callback);
            }

            return;
         }

         if (p.text.getSource() instanceof Announcement) {
            editAnnouncementRealTime(p, (Announcement)p.text.getSource(), callback);
            return;
         }

         if (p.text.getSource() instanceof String && p.text.getSource().equals("quick-presentation")) {
            editQuickPresentationRealTime(p, callback);
         }
      } catch (Exception var3) {
      }
   }

   private static void editMusicTitleAndArtistRealTime(final Presentation p, final Music music, final Runnable callback) {
      final JTextField tfTitle = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_TITLE.val());
      tfTitle.setDocument(SwingUtils.createDocMaxSize(80));
      tfTitle.setText(music.getTitle());
      final JTextField tfArtist = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_ARTIST.val());
      tfArtist.setDocument(SwingUtils.createDocMaxSize(80));
      tfArtist.setText(music.getArtist());
      MusicController.Paragraph paragraph = music.getTitleAndArtistParagraph();
      BufferedImage icon = PresentationSlideUtils.createImage(null, paragraph, p.theme, EnumSlideSizePreview.BIG.size, true);
      WebLabel slidePreview = new WebLabel(new ImageIcon(icon), 0);
      slidePreview.setSize(EnumSlideSizePreview.BIG.size);
      slidePreview.setPreferredSize(EnumSlideSizePreview.BIG.size);
      DocumentListenerAuto trigger = new DocumentListenerAuto(500L, () -> Utils.startThreadDaemon(() -> {
            paragraph.setText(tfTitle.getText() + "\n" + tfArtist.getText());
            BufferedImage bi = PresentationSlideUtils.createImage(null, paragraph, p.theme, EnumSlideSizePreview.BIG.size, true);
            SwingUtilities.invokeLater(() -> slidePreview.setIcon(new ImageIcon(bi)));
         }));
      Arrays.asList(tfTitle, tfArtist).forEach(tf -> {
         tf.setFont(new Font("Georgia", 0, 12));
         tf.setColumns(30);
         tf.setHorizontalAlignment(0);
         tf.getDocument().addDocumentListener(trigger);
      });
      WebPanel panelTitleAndArtist = new WebPanel(new GridLayout(2, 1, 6, 6));
      panelTitleAndArtist.setOpaque(false);
      panelTitleAndArtist.add(tfTitle);
      panelTitleAndArtist.add(tfArtist);
      final WebPanel panel = new WebPanel(new BorderLayout(16, 16));
      panel.setOpaque(false);
      panel.add(panelTitleAndArtist, "Last");
      panel.add(slidePreview, "Center");
      PopupUtils.dialogOkCancel(panel, I18N.MSG_EDIT_TEXT.val(), new Consumer<Boolean>() {
         public void accept(Boolean flag) {
            if (flag) {
               if (tfTitle.getText().trim().isEmpty()) {
                  Utils.error(I18N.MSG_ERROR_FIELD_X_EMPTY.format(I18N.WORD_TITLE.val()));
                  PopupUtils.dialogOkCancel(panel, I18N.MSG_EDIT_TEXT.val(), this);
                  SwingUtils.requestFocusOnVisible(tfTitle);
                  return;
               }

               if (tfArtist.getText().trim().isEmpty()) {
                  Utils.error(I18N.MSG_ERROR_FIELD_X_EMPTY.format(I18N.WORD_ARTIST.val()));
                  PopupUtils.dialogOkCancel(panel, I18N.MSG_EDIT_TEXT.val(), this);
                  SwingUtils.requestFocusOnVisible(tfArtist);
                  return;
               }

               p.text.setText(tfTitle.getText() + "\n" + tfArtist.getText());
               music.setTitle(tfTitle.getText());
               music.setArtist(tfArtist.getText());
               if (MusicDao.getLazy(music.getId()) != null) {
                  music.save();
               }

               Optional.ofNullable(callback).ifPresent(Runnable::run);
            }
         }
      });
   }

   private static void editMusicLyricsRealTime(Presentation p, Music music, Runnable callback) {
      int slideIndex = p.text.getPageCount().x;
      List<Integer> order = music.getOrder();
      int realIndex = order.isEmpty() ? slideIndex - 1 : order.get(slideIndex - 1);
      String text1 = music.getParagraphs().get(realIndex);
      String text2 = p.text.getText();
      if (!text1.equalsIgnoreCase(text2)) {
         WebLafUtils.notificationErrorDelay(I18N.MSG_ERROR_TRY_AGAIN.val(), 5L);
      } else {
         editParagraph(text1, p.theme, true, newValue -> {
            p.text.setText(Settings.EnumSettings.MUSIC_UPPERCASE.is() ? newValue.toUpperCase() : newValue);
            MusicUtils.setParagraph(music, realIndex, newValue);
            if (MusicDao.getLazy(music.getId()) != null) {
               music.save();
            }

            Optional.ofNullable(callback).ifPresent(Runnable::run);
         });
      }
   }

   private static void editAnnouncementRealTime(Presentation p, Announcement an, Runnable callback) {
      editParagraph(an.getText(), p.theme, false, newValue -> {
         newValue = StringUtils.maxLength(newValue, 2048, false);
         p.text.setText(newValue);
         an.setText(newValue);
         if (AnnouncementController.find(an.id) != null) {
            AnnouncementController.save();
         }

         Optional.ofNullable(callback).ifPresent(Runnable::run);
      });
   }

   private static void editQuickPresentationRealTime(Presentation p, Runnable callback) {
      editParagraph(p.text.getText(), p.theme, true, newValue -> {
         p.text.setText(newValue);
         Optional.ofNullable(callback).ifPresent(Runnable::run);
      });
   }
}
