package com.limagiran.holyrics.favorite;

import com.alee.global.StyleConstants;
import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebMenu;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.menu.WebPopupMenuSeparator;
import com.alee.laf.menu.WebRadioButtonMenuItem;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.alee.laf.separator.WebSeparator;
import com.limagiran.holyrics.control.ControllerListener;
import com.limagiran.holyrics.control.FavoriteController;
import com.limagiran.holyrics.hitem.HItemUtils;
import com.limagiran.holyrics.hitem.item.HItemAudio;
import com.limagiran.holyrics.hitem.item.HItemVideo;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.DragComponentMouseAdapter;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.IScheduleTask;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WrapLayout;
import com.limagiran.holyrics.view.MusicMain;
import com.limagiran.holyrics.vlc.VLC;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;

public class FavoritePanel extends WebPanel implements DragComponentMouseAdapter.DragComponentListener {
   private final WebPanel favoriteBox = new WebPanel();
   private final WebScrollPane scroll = new WebScrollPane(null, false);
   private WebButton buttonMore;
   private FavoritePanel.EnumLabelSize enumLabelSize;
   private FavoriteItemLabel lastOpaque;
   private final WebLabel labelMoreItems = new WebLabel("»");
   private Rectangle lastViewScroll;
   private int moveScroll;
   private static final ImageIcon IC_RENAME = new ImageIcon(Images.load("textFields16.png"));

   public FavoritePanel() {
      super(new BorderLayout());
      this.enumLabelSize = FavoritePanel.EnumLabelSize.getOrDefault(Settings.EnumSettings2.FAVORITE_ENUM_SIZE_NAME.getString());
      this.init();
   }

   private void init() {
      this.setPaintSides(false, false, true, false);
      this.setRound(StyleConstants.largeRound);
      this.setBackground(new Color(215, 215, 215));
      this.setVisible(Settings.EnumSettings2.FAVORITE_PANEL_VISIBLE.is());
      this.setOpaque(true);
      this.buttonMore = this.createButtonMore();
      this.favoriteBox.setOpaque(false);
      DragComponentMouseAdapter dh = new DragComponentMouseAdapter(this);
      this.favoriteBox.addMouseListener(dh);
      this.favoriteBox.addMouseMotionListener(dh);
      this.favoriteBox.addMouseListener(new MouseAdapter() {
         private final Point p = new Point();

         @Override
         public void mouseExited(MouseEvent e) {
            if (FavoritePanel.this.lastOpaque != null) {
               FavoritePanel.this.lastOpaque.setOpaqueAndRepaint(false);
            }

            FavoritePanel.this.moveScroll = 0;
         }

         @Override
         public void mousePressed(MouseEvent e) {
            this.p.setLocation(e.getPoint());
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            Component c = FavoritePanel.this.favoriteBox.getComponentAt(e.getPoint());
            if (this.p.equals(e.getPoint()) && c instanceof FavoriteItemLabel) {
               FavoriteItemLabel fil = (FavoriteItemLabel)c;
               fil.setOpaqueAndRepaint(true);
               if (SwingUtilities.isLeftMouseButton(e)) {
                  fil.favorite.getFavorite().favoriteAction(true);
               } else {
                  if (SwingUtilities.isRightMouseButton(e)) {
                     FavoritePanel.this.rightButton(e, fil);
                  }
               }
            }
         }
      });
      this.favoriteBox.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent evt) {
            try {
               FavoritePanel.this.moveScroll = evt.getX() - FavoritePanel.this.favoriteBox.getVisibleRect().x <= 16 ? -16 : 0;
            } catch (Exception var3) {
            }

            FavoritePanel.this.setupRollover(evt);
         }
      });
      ControllerListener.addChangeListener(FavoriteController.class, clazz -> SwingUtilities.invokeLater(this::setup));
      this.setup();
      this.scroll.setOpaque(false);
      this.scroll.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
      this.scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 0));
      this.scroll.getHorizontalScrollBar().setUnitIncrement(32);
      this.scroll.setMargin(0, 0, 0, 3);
      WebPanel separator = new WebPanel(new GridLayout(1, 2), new Component[]{WebSeparator.createVertical(), WebSeparator.createVertical()});
      separator.setOpaque(false);
      WebPanel westPanel = new WebPanel(new BorderLayout());
      westPanel.setMargin(0, 3, 0, 0);
      westPanel.setOpaque(false);
      westPanel.add(this.buttonMore, "West");
      westPanel.add(separator, "East");
      this.add(westPanel, "West");
      this.labelMoreItems.setFont(this.labelMoreItems.getFont().deriveFont(1, 14.0F));
      this.labelMoreItems.setMargin(0, 4, 0, 4);
      this.labelMoreItems.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent evt) {
            FavoritePanel.this.moveScroll = 16;
         }

         @Override
         public void mouseExited(MouseEvent e) {
            FavoritePanel.this.moveScroll = 0;
         }
      });
      this.add(this.labelMoreItems, "East");
      Dimension d = new Dimension(0, 1);
      Filler lineLast = new Filler(d, d, d);
      lineLast.setBackground(new Color(140, 140, 140));
      lineLast.setOpaque(true);
      this.add(lineLast, "Last");
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            FavoritePanel.this.favoriteBox.revalidate();
            FavoritePanel.this.favoriteBox.repaint();
            FavoritePanel.this.revalidate();
            FavoritePanel.this.repaint();
         }
      });
      Utils.startThreadDaemon(() -> {
         JScrollBar hsb = this.scroll.getHorizontalScrollBar();

         while (true) {
            try {
               if (this.moveScroll > 0 || this.moveScroll < 0 && this.favoriteBox.getVisibleRect().x > 0) {
                  SwingUtilities.invokeLater(() -> hsb.setValue(hsb.getValue() + this.moveScroll));
               }
            } catch (Exception var6) {
            } finally {
               Utils.sleep(100L);
            }
         }
      });
   }

   private void setup() {
      try {
         this.favoriteBox.removeAll();
         this.remove(this.scroll);
         this.remove(this.favoriteBox);
         List<FavoriteItem> list = FavoriteController.getAll();
         if (!Settings.EnumSettings2.FAVORITE_SINGLE_LINE.is() && !list.isEmpty()) {
            this.favoriteBox.setLayout(new WrapLayout(0, 3, 0));
            this.add(this.favoriteBox, "Center");
            this.labelMoreItems.setVisible(false);
         } else {
            this.favoriteBox.setLayout(new BoxLayout(this.favoriteBox, 0));
            this.scroll.setViewportView(this.favoriteBox);
            this.scroll.getViewport().setOpaque(false);
            this.add(this.scroll, "Center");
            this.labelMoreItems.setVisible(true);
         }

         this.buttonMore.setIcon(this.createIconButtonMore());
         if (!list.isEmpty()) {
            list.sort((o1, o2) -> Integer.compare(o1.getPosition(), o2.getPosition()));
            list.forEach(f -> {
               FavoriteItemLabel label = new FavoriteItemLabel(f, this.enumLabelSize);
               this.favoriteBox.add(label);
               if (f.getFavorite() instanceof HItemAudio || f.getFavorite() instanceof HItemVideo) {
                  label.setVisible(VLC.ENABLED);
               }
            });
            return;
         }

         this.favoriteBox.add(new JLabel("   " + I18N.WORD_FAVORITES.val()));
      } finally {
         this.favoriteBox.revalidate();
         this.favoriteBox.repaint();
         this.revalidate();
         this.repaint();

         try {
            if (this.lastViewScroll != null) {
               this.favoriteBox.scrollRectToVisible(this.lastViewScroll);
            }
         } catch (Exception var9) {
         }

         this.lastViewScroll = null;
      }
   }

   @Override
   public void positionChanged() {
      this.lastViewScroll = this.favoriteBox.getVisibleRect();
      Component[] cs = this.favoriteBox.getComponents();
      int i = 0;

      for (Component c : cs) {
         if (c instanceof FavoriteItemLabel) {
            ((FavoriteItemLabel)c).favorite.setPosition(i++);
         }
      }

      FavoriteController.save();
   }

   private WebButton createButtonMore() {
      WebButton button = new WebButton(e -> this.moreOptions(e));
      button.setFocusable(false);
      button.setUndecorated(true);
      button.setMargin(2, 0, 2, 0);
      return button;
   }

   private ImageIcon createIconButtonMore() {
      int h = this.enumLabelSize.getThumbSize().height;
      BufferedImage bi = new BufferedImage((int)((double)h * 0.75), h, 2);
      Graphics2D g = bi.createGraphics();
      G2Utils.setupGraphics(g);
      g.setColor(Color.BLACK);
      int drawableH = (int)((float)h * 0.75F);
      int itemH = (int)((float)drawableH / 3.0F);
      int circleH = (int)((float)itemH * 0.75F);
      int x = (int)((float)bi.getWidth() / 2.0F - (float)circleH / 2.0F);
      int paddingTop = (int)((float)(h - drawableH) / 2.0F);

      for (int i : new int[]{0, 1, 2}) {
         g.fillOval(x, paddingTop + itemH * i + itemH - circleH, circleH, circleH);
      }

      g.dispose();
      return new ImageIcon(bi);
   }

   private void moreOptions(ActionEvent ae) {
      WebPopupMenu popup = new WebPopupMenu();
      HItemUtils.setupPopupMenuAddHItem(popup, (Component)ae.getSource(), id -> FavoriteController.contains(String.valueOf(id.id)), (name, hItem) -> {
         ImageIcon ic = new ImageIcon(hItem.getFavoriteThumb(new Dimension(64, 36)));
         FavoriteUtils.getNameAndCreate((Component)ae.getSource(), hItem, ic, name);
      });
      popup.addSeparator();
      WebMenuItem removeAll = new WebMenuItem(I18N.MSG_REMOVE_ALL.val(), new ImageIcon(Images.ICON_REMOVE_16));
      removeAll.addActionListener(e -> {
         if (Utils.confirm(I18N.WORD_CONFIRM.val() + "?", I18N.MSG_REMOVE_ALL.val())) {
            FavoriteController.clear();
         }
      });
      popup.add(removeAll);
      popup.addSeparator();
      WebMenu settings = new WebMenu(I18N.WORD_SETTINGS.val(), new ImageIcon(Images.ICON_SETTINGS_16));
      WebMenu barSize = new WebMenu(I18N.WORD_HEIGHT.val());

      for (FavoritePanel.EnumLabelSize en : FavoritePanel.EnumLabelSize.values()) {
         WebRadioButtonMenuItem barSizeItem = new WebRadioButtonMenuItem(en.name);
         barSizeItem.addActionListener(e -> {
            Settings.EnumSettings2.FAVORITE_ENUM_SIZE_NAME.set(en.name());
            this.setEnumLabelSize(en);
         });
         barSizeItem.setSelected(this.enumLabelSize == en);
         barSize.add(barSizeItem);
      }

      settings.add(barSize);
      WebMenu adjustment = new WebMenu(I18N.WORD_ADJUSTMENT.val());
      WebRadioButtonMenuItem adjustmentType = new WebRadioButtonMenuItem(I18N.MSG_SINGLE_LINE.val());
      adjustmentType.addActionListener(e -> {
         Settings.EnumSettings2.FAVORITE_SINGLE_LINE.set(true);
         SwingUtilities.invokeLater(this::setup);
      });
      adjustmentType.setSelected(Settings.EnumSettings2.FAVORITE_SINGLE_LINE.is());
      adjustment.add(adjustmentType);
      adjustmentType = new WebRadioButtonMenuItem(I18N.MSG_FIT_TO_SCREEN.val());
      adjustmentType.addActionListener(e -> {
         Settings.EnumSettings2.FAVORITE_SINGLE_LINE.set(false);
         SwingUtilities.invokeLater(this::setup);
      });
      adjustmentType.setSelected(!Settings.EnumSettings2.FAVORITE_SINGLE_LINE.is());
      adjustment.add(adjustmentType);
      settings.add(adjustment);
      WebMenuItem hide = new WebMenuItem(I18N.WORD_HIDE.val());
      hide.addActionListener(e -> this.setVisible(false));
      settings.add(hide);
      popup.add(settings);
      popup.showBelowStart(this.buttonMore);
   }

   private void rightButton(MouseEvent evt, FavoriteItemLabel label) {
      FavoriteItem favorite = label.favorite;
      Favoritable favoritable = favorite.getFavorite();
      WebPopupMenu popup = new WebPopupMenu();
      HItemUtils.setupPopupMenuAction(popup, favoritable.getActionName(), favoritable::favoriteAction, favoritable.listPathIfDirectory(), false);
      popup.addSeparator();
      if (favoritable instanceof IHItem) {
         IHItem hitem = (IHItem)favoritable;
         HItemUtils.setupPopupMenuAddPlaylistEvent(popup, al -> MusicMain.staticAddPlayList(hitem));
         if (favoritable instanceof HItemAudio || favoritable instanceof HItemVideo) {
            HItemUtils.setupPopupMenuAddPlaylistPlayer(popup, hitem.getAsFile());
         }

         IScheduleTask.setupPopupMenu(popup, favorite);
         popup.addSeparator();
      }

      WebMenuItem rename = new WebMenuItem(I18N.WORD_RENAME.val(), IC_RENAME);
      rename.addActionListener(e -> Utils.Input.show(label, I18N.WORD_NAME.val(), I18N.WORD_RENAME.val(), null, favorite.getName(), input -> {
            favorite.setName(input);
            FavoriteController.save();
         }));
      popup.add(rename);
      WebMenuItem remove = new WebMenuItem(I18N.WORD_REMOVE.val(), new ImageIcon(Images.ICON_REMOVE_16));
      remove.addActionListener(e -> FavoriteController.remove(favorite));
      popup.add(remove);
      if (favoritable instanceof IHItem) {
         File file = ((IHItem)favoritable).getAsFile();
         if (file != null) {
            popup.add(new WebPopupMenuSeparator());
            popup.add(HItemUtils.getOpenLocationMenuItem(null, file));
         }
      }

      popup.show(this.favoriteBox, evt.getX(), evt.getY());
   }

   private void setupRollover(MouseEvent e) {
      try {
         Component c = this.favoriteBox.getComponentAt(e.getPoint());
         if (this.lastOpaque != null && (c == null || c != this.lastOpaque)) {
            this.lastOpaque.setOpaqueAndRepaint(false);
            this.lastOpaque = null;
         }

         if (c instanceof FavoriteItemLabel) {
            this.lastOpaque = (FavoriteItemLabel)c;
            this.lastOpaque.setOpaqueAndRepaint(true);
         }
      } catch (Exception var3) {
      }
   }

   public void setVisible(boolean visible) {
      Settings.EnumSettings2.FAVORITE_PANEL_VISIBLE.set(visible);
      super.setVisible(visible);
   }

   public FavoritePanel.EnumLabelSize getEnumLabelSize() {
      return this.enumLabelSize;
   }

   public void setEnumLabelSize(FavoritePanel.EnumLabelSize enumLabelSize) {
      this.enumLabelSize = enumLabelSize;
      SwingUtilities.invokeLater(this::setup);
   }

   public static enum EnumLabelSize {
      SMALL(I18N.WORD_SMALL_F.val(), 2, 12),
      MEDIUM(I18N.WORD_MEDIUM_F.val(), 3, 14),
      BIG(I18N.WORD_BIG.val(), 4, 16);

      public final String name;
      public final int thumbSizeRatio;
      public final int fontSize;

      private EnumLabelSize(String name, int thumbSizeRatio, int fontSize) {
         this.name = name;
         this.thumbSizeRatio = thumbSizeRatio;
         this.fontSize = fontSize;
      }

      public Dimension getThumbSize() {
         return new Dimension(16 * this.thumbSizeRatio, 9 * this.thumbSizeRatio);
      }

      public static FavoritePanel.EnumLabelSize getOrDefault(String name) {
         try {
            return valueOf(name);
         } catch (Exception var2) {
            return SMALL;
         }
      }

      @Override
      public String toString() {
         return this.name;
      }
   }
}
