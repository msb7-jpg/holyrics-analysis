package com.limagiran.holyrics.favorite;

import com.limagiran.holyrics.util.IScheduleTask;
import java.awt.Dimension;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Objects;
import javax.swing.ImageIcon;

public class FavoriteItem implements Serializable, IScheduleTask {
   private static final long serialVersionUID = 1L;
   private Favoritable favorite;
   private int position;
   private String name;
   private long version = 1L;

   public FavoriteItem(Favoritable favorite, String name) {
      this.favorite = favorite;
      this.position = -1;
      this.name = name;
   }

   public Favoritable getFavorite() {
      return this.favorite;
   }

   public void setFavorite(Favoritable favorite) {
      this.favorite = favorite;
   }

   public int getPosition() {
      return this.position;
   }

   public void setPosition(int position) {
      this.position = position;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof FavoriteItem ? ((FavoriteItem)obj).getFavorite().getId().equalsIgnoreCase(this.getFavorite().getId()) : super.equals(obj);
   }

   @Override
   public int hashCode() {
      return 133 + Objects.hashCode(this.favorite.getId());
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
   }

   @Override
   public String getTitle() {
      return this.name;
   }

   @Override
   public ImageIcon getIcon() {
      return new ImageIcon(this.favorite.getFavoriteThumb(new Dimension(64, 36)));
   }

   @Override
   public void run() {
      this.favorite.favoriteAction(true);
   }
}
