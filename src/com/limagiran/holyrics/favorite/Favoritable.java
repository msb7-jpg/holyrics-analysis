package com.limagiran.holyrics.favorite;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.List;

public interface Favoritable extends Serializable {
   void favoriteAction(boolean var1);

   String getActionName();

   BufferedImage getFavoriteThumb(Dimension var1);

   String getId();

   File getAsFile();

   List<File> listPathIfDirectory();
}
