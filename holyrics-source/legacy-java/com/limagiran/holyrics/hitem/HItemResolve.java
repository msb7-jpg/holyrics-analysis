package com.limagiran.holyrics.hitem;

import com.limagiran.bible.Verse;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.hitem.item.HItemAudio;
import com.limagiran.holyrics.hitem.item.HItemImage;
import com.limagiran.holyrics.hitem.item.HItemVerse;
import com.limagiran.holyrics.hitem.item.HItemVideo;
import com.limagiran.holyrics.hitem.item.IHItem;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.function.Predicate;

class HItemResolve {
   final Object item;
   private final EnumTabType tabType;

   public HItemResolve(Object item, EnumTabType tabType) {
      this.item = item;
      this.tabType = tabType;
   }

   String getName() {
      if (this.item instanceof String) {
         return this.item.toString();
      } else if (this.item instanceof IHItem) {
         if (this.item instanceof HItemVerse) {
            Verse v = ((HItemVerse)this.item).getVerse();
            return v.getFullDesc() + "<br><div style='width:128px'><span id='span-single' style='white-space: normal;'>" + v.content + "</span></div>";
         } else {
            return ((IHItem)this.item).getName();
         }
      } else {
         return "";
      }
   }

   File getFile() {
      if (this.item instanceof String) {
         return new File(this.tabType.path, this.item.toString());
      } else {
         return this.item instanceof IHItem ? ((IHItem)this.item).getAsFile() : null;
      }
   }

   boolean isDir() {
      File f = this.getFile();
      return f != null && f.isDirectory();
   }

   Predicate<File> getFileFilterDirectory() {
      if (this.tabType != null) {
         return this.tabType.getFileFilterDirectory();
      } else {
         if (this.item instanceof IHItem) {
            IHItem hItem = (IHItem)this.item;
            if (hItem instanceof HItemImage) {
               return HItemUtils::isImage;
            }

            if (hItem instanceof HItemAudio || hItem instanceof HItemVideo) {
               return HItemUtils::isMedia;
            }
         }

         return File::isFile;
      }
   }

   String getId() {
      if (this.tabType != null) {
         return this.tabType.path + String.valueOf(this.item);
      } else {
         return this.item instanceof IHItem ? ((IHItem)this.item).getId() : "";
      }
   }

   String getImageSrc() {
      try {
         if (HItemUtils.isAudio(new File(this.getId()))) {
            return Utils.bytesToBase64(ToolTipProviderHItemListView.THUMB_AUDIO_DEFAULT);
         }

         if (this.getId().startsWith(Values.PATH_MEDIA)) {
            return Utils.bytesToBase64(ThumbMediaController.getOrDefault(this.getId(), ToolTipProviderHItemListView.THUMB_NOT_FOUND));
         }

         if (this.item instanceof HItemVerse) {
            return Img.encodeToString(Images.NULL1X1, "jpg");
         }

         if (this.item instanceof IHItem) {
            BufferedImage bi = ((IHItem)this.item).getFavoriteThumb(ThumbMediaController.getThumbSize());
            return Img.encodeToString(bi, "jpg");
         }
      } catch (Exception var2) {
      }

      return "";
   }
}
