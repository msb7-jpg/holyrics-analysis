package com.limagiran.holyrics.util;

import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.combobox.WebComboBoxCellRenderer;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollBar;
import com.alee.managers.hotkey.HotkeyData;
import com.alee.managers.hotkey.HotkeyManager;
import com.alee.managers.language.data.TooltipWay;
import com.alee.managers.notification.NotificationIcon;
import com.alee.managers.notification.NotificationManager;
import com.alee.managers.notification.WebNotification;
import com.alee.managers.style.skin.web.WebScrollBarPainter;
import com.alee.managers.tooltip.TooltipManager;
import com.alee.utils.FileUtils;
import com.alee.utils.MathUtils;
import com.alee.utils.file.FileThumbnailProvider;
import com.limagiran.holyrics.control.ThumbCacheController;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.i18n.I18N;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.filter.VideoFileFilter;

public class WebLafUtils {
   public static void setupWebLafBorderLayout(Component... components) {
      Arrays.asList(components).stream().map(WebPanel.class::cast).forEach(wb -> {
         wb.setRound(StyleConstants.smallRound);
         wb.setMargin(5);
         Map<Component, Object> map = new HashMap<>();

         for (Component c : wb.getComponents()) {
            map.put(c, ((BorderLayout)wb.getLayout()).getConstraints(c));
         }

         wb.setLayout(new BorderLayout());
         map.forEach((t, u) -> wb.add(t, u));
      });
   }

   public static void setupWebLafCardLayout(Component... components) {
      Arrays.asList(components).stream().map(WebPanel.class::cast).forEach(wb -> wb.setMargin(5).setRound(StyleConstants.smallRound).revalidate());
   }

   public static void setupWebLafLayout(LayoutManager layout, Component... components) {
      Arrays.asList(components).stream().map(WebPanel.class::cast).forEach(wb -> {
         wb.setMargin(5);
         wb.setRound(StyleConstants.smallRound);
         List<Component> list = Arrays.asList(wb.getComponents());
         wb.setLayout(layout);
         list.forEach(wb::add);
      });
   }

   public static void addToolTipButton(Component parent, AbstractButton b, HotkeyData... keys) {
      Arrays.asList(keys).forEach(k -> HotkeyManager.registerHotkey(parent, b, k, TooltipWay.up));
      TooltipManager.setTooltip(b, b.getToolTipText(), TooltipWay.up, 300);
      b.setToolTipText(null);
   }

   public static void addToolTipButtonDown(Component parent, AbstractButton b, HotkeyData... keys) {
      Arrays.asList(keys).forEach(k -> HotkeyManager.registerHotkey(parent, b, k, TooltipWay.down));
      TooltipManager.setTooltip(b, b.getToolTipText(), TooltipWay.down, 300);
      b.setToolTipText(null);
   }

   public static void addToolTipButtonZeroDelay(Component parent, AbstractButton b, HotkeyData... keys) {
      Arrays.asList(keys).forEach(k -> HotkeyManager.registerHotkey(parent, b, k, TooltipWay.up));
      TooltipManager.setTooltip(b, b.getToolTipText(), TooltipWay.up, 0);
      b.setToolTipText(null);
   }

   public static void notificationDelay(String string, long sec, Icon icon) {
      SwingUtilities.invokeLater(() -> {
         WebNotification notificationPopup = new WebNotification();
         notificationPopup.setIcon(icon);
         if (sec > 0L) {
            notificationPopup.setDisplayTime(sec * 1000L);
         }

         notificationPopup.setContent(string);
         NotificationManager.showNotification(notificationPopup);
      });
   }

   public static void notificationDelay(String string, long sec) {
      notificationDelay(string, sec, NotificationIcon.warning.getIcon());
   }

   public static void notificationErrorDelay(String string, long sec) {
      notificationDelay(string, sec, NotificationIcon.error.getIcon());
   }

   public static void notification(String text) {
      SwingUtilities.invokeLater(() -> {
         WebNotification wn = new WebNotification();
         wn.setIcon(new ImageIcon(Images.load("notifications48.png")));
         WebButton close = new WebButton(I18N.WORD_CLOSE.val());
         close.setShadeColor(Values.COLOR_TRANSPARENT);
         close.setFont(close.getFont().deriveFont(1));
         close.addActionListener(e -> wn.hidePopup());
         JPanel buttonsPanel = new JPanel(new FlowLayout(2, 8, 0));
         buttonsPanel.add(close);
         buttonsPanel.setOpaque(false);
         JPanel content = new JPanel(new BorderLayout(0, 8));
         content.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
         content.setOpaque(false);
         content.add(new WebLabel(text), "Center");
         content.add(buttonsPanel, "Last");
         wn.setContent(content);
         NotificationManager.showNotification(wn);
      });
   }

   public static void setupScrollBarLargeThumbWithoutButton(WebScrollBar scrollBar) {
      scrollBar.setPainter(new WebScrollBarPainter() {
         protected void paintThumb(Graphics2D g2d, JScrollBar scrollbar, Rectangle bounds) {
            Insets m = this.getCurrentThumbMargin(scrollbar);
            m.bottom = m.top = 0;
            int w = bounds.width - m.left - m.right;
            int h = bounds.height;
            int round = MathUtils.min(new int[]{this.thumbRound, w - 1, h - 1});
            g2d.setPaint(this.getCurrentThumbBackgroundColor(scrollbar));
            g2d.fillRoundRect(bounds.x + m.left, bounds.y, w, h, round, round);
            g2d.setPaint(this.getCurrentThumbBorderColor(scrollbar));
            g2d.drawRoundRect(bounds.x + m.left, bounds.y, w - 1, h - 1, round, round);
         }

         protected void paintBackground(Graphics2D g2d, JScrollBar scrollbar, Rectangle bounds) {
            g2d.setPaint(Color.WHITE);
            g2d.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
         }
      });
      scrollBar.setPaintButtons(false);
      boolean flag = scrollBar.getOrientation() == 0;
      scrollBar.setPreferredSize(new Dimension(flag ? 0 : 10, flag ? 10 : 0));
   }

   public static WebComboBoxCellRenderer createComboBoxRendererSimple(final Function<Object, String> function) {
      return new WebComboBoxCellRenderer() {
         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, function.apply(value), index, isSelected, cellHasFocus);
         }
      };
   }

   public static WebComboBoxCellRenderer createComboBoxRendererFontName(final int maxHeight) {
      return new WebComboBoxCellRenderer() {
         public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, " " + value + " ", index, isSelected, cellHasFocus);
            if (Values.DISPLAY_FONT_NAME_WITH_FONT && c instanceof JComponent) {
               Font f = ((JComponent)c).getFont();
               int fontSize = f.getSize() + 6;
               if (maxHeight > 0 && Values.GRAPHICS.getFontMetrics(f).getHeight() > maxHeight) {
                  fontSize = G2Utils.getBestFont(maxHeight, f, Values.GRAPHICS).getSize();
               }

               ((JComponent)c).setFont(new Font(String.valueOf(value), f.getStyle(), fontSize));
            }

            return c;
         }
      };
   }

   public static WebComboBoxCellRenderer createComboBoxRendererFontName() {
      return createComboBoxRendererFontName(-1);
   }

   public static FileThumbnailProvider createFileThumbnailProvider() {
      return new FileThumbnailProvider() {
         public boolean accept(File file) {
            if (!file.isFile()) {
               return false;
            } else {
               String name = file.getName();

               for (String ex : VideoFileFilter.INSTANCE.getExtensions()) {
                  if (name.endsWith("." + ex)) {
                     return true;
                  }
               }

               return false;
            }
         }

         public ImageIcon provide(File file, Dimension size, boolean preview) {
            try {
               if (preview) {
                  String id = "FileThumbnailProvider-" + file.getAbsolutePath();
                  ThumbMediaController.Thumb thumb = ThumbCacheController.getOrCreate(
                     id, file.lastModified(), () -> ThumbCacheController.generateVideoThumbnail(file)
                  );
                  if (thumb != null && thumb.thumb != null) {
                     BufferedImage bi = Img.load(thumb.thumb);
                     if (bi != null) {
                        return new ImageIcon(Img.resize(bi, size));
                     }
                  }
               }

               ImageIcon icon = FileUtils.getStandartFileIcon(file, true, true);
               BufferedImage bi = Img.toBufferedImage2(icon.getImage());
               bi = Img.resize(bi, size);
               bi.getWidth();
               return new ImageIcon(bi);
            } catch (Exception var7) {
               return new ImageIcon(Img.resize(ThumbMediaController.IMAGE_NOT_FOUND, size));
            }
         }
      };
   }
}
