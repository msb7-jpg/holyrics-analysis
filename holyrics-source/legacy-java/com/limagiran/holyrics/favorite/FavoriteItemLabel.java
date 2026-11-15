package com.limagiran.holyrics.favorite;

import com.alee.laf.label.WebLabel;
import java.awt.Color;
import javax.swing.ImageIcon;

class FavoriteItemLabel extends WebLabel {
   private static final Color BGCOLOR = new Color(186, 186, 186);
   public final FavoriteItem favorite;
   private final FavoritePanel.EnumLabelSize en;
   private boolean opaque;

   public FavoriteItemLabel(FavoriteItem favorite, FavoritePanel.EnumLabelSize en) {
      this.favorite = favorite;
      this.en = en;
      this.init();
   }

   private void init() {
      this.setMargin(4, 8, 4, 8);
      this.setBackground(BGCOLOR);
      this.setIcon(new ImageIcon(this.favorite.getFavorite().getFavoriteThumb(this.en.getThumbSize())));
      this.setIconTextGap(6);
      this.setFont(this.getFont().deriveFont((float)this.en.fontSize));
      this.setText(this.favorite.getName());
      this.setMinimumWidth(50);
   }

   public void setOpaqueAndRepaint(boolean opaque) {
      if (this.opaque != opaque) {
         super.setOpaque(this.opaque = opaque);
         this.repaint();
      }
   }
}
