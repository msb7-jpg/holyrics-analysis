package com.limagiran.holyrics.hitem;

import com.alee.laf.list.WebList;
import com.alee.managers.tooltip.AbstractToolTipProvider;
import com.limagiran.holyrics.control.ThumbMediaController;
import com.limagiran.holyrics.hitem.item.HItemAudio;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import java.awt.Rectangle;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ToolTipProviderHItemListView extends AbstractToolTipProvider<WebList> {
   public static final byte[] THUMB_NOT_FOUND;
   public static final byte[] THUMB_AUDIO_DEFAULT;
   private static final String HTML_PREFIX = "<html>  <head>  <title></title>    <style>      body {        font-size: 8px;        font-family: Arial;        text-align: center;        padding: 4px;      }      td {        width: 90px;        padding: 5px;        background-color: #555555;        text-align: center;      }      span {        white-space: nowrap;      }      #span-single {        font-size: 9px;      }    </style>  </head>  <body>";
   private static final String HTML_END = "</body></html>";
   private final EnumTabType tabType;
   private final boolean showSingle;

   public ToolTipProviderHItemListView(EnumTabType tabType, boolean showSingle) {
      this.tabType = tabType;
      this.showSingle = showSingle;
   }

   public String getToolTipText(WebList component, Object value, int index, int column, boolean isSelected) {
      try {
         Object obj = component.getModel().getElementAt(index);
         HItemResolve item = new HItemResolve(obj, this.tabType);
         if (item.isDir()) {
            List<File> files = Stream.of(item.getFile().listFiles()).filter(item.getFileFilterDirectory()).limit(64L).collect(Collectors.toList());
            StringBuilder sb = new StringBuilder();
            int sqrt = this.getColBreak(item.getId(), files.size());
            int i = 0;

            while (i < files.size()) {
               String id = item.getId() + File.separator + files.get(i).getName();
               String img = this.getImg(id);
               String txt = this.getTxt(id, files.get(i).getName());
               sb.append("<td>" + img + txt + "</td>");
               if (++i % sqrt == 0) {
                  sb.append("</tr><tr>");
               }
            }

            return "<html>  <head>  <title></title>    <style>      body {        font-size: 8px;        font-family: Arial;        text-align: center;        padding: 4px;      }      td {        width: 90px;        padding: 5px;        background-color: #555555;        text-align: center;      }      span {        white-space: nowrap;      }      #span-single {        font-size: 9px;      }    </style>  </head>  <body>"
               + (sb.toString().isEmpty() ? I18N.WORD_EMPTY_O.val() : "<table cellspacing='2'><tr>" + sb.toString() + "</tr></table>")
               + "</body></html>";
         } else {
            return this.showSingle
               ? "<html>  <head>  <title></title>    <style>      body {        font-size: 8px;        font-family: Arial;        text-align: center;        padding: 4px;      }      td {        width: 90px;        padding: 5px;        background-color: #555555;        text-align: center;      }      span {        white-space: nowrap;      }      #span-single {        font-size: 9px;      }    </style>  </head>  <body>"
                  + "<img src='"
                  + item.getImageSrc()
                  + "'><br><span id='span-single'>"
                  + item.getName()
                  + "</span>"
                  + "</body></html>"
               : "";
         }
      } catch (Exception var15) {
         return "";
      }
   }

   protected Object getValue(WebList component, int index, int column) {
      return "";
   }

   protected boolean isSelected(WebList component, int index, int column) {
      return component.getSelectedIndex() == index;
   }

   public Rectangle getSourceBounds(WebList component, Object value, int index, int column, boolean isSelected) {
      try {
         Rectangle rect = component.getCellBounds(index, index);
         return rect != null ? rect : new Rectangle(0, 0, 1, 1);
      } catch (Exception var7) {
         return new Rectangle(0, 0, 1, 1);
      }
   }

   protected void showTooltip(WebList component, int index, int column) {
      try {
         if (this.showSingle || new HItemResolve(component.getModel().getElementAt(index), this.tabType).isDir()) {
            super.showTooltip(component, index, column);
         }
      } catch (Exception var5) {
      }
   }

   public long getDelay() {
      return 500L;
   }

   private String getImg(String id) {
      if (id.startsWith(Values.PATH_MEDIA_AUDIO)) {
         return "";
      } else {
         return HItemUtils.isAudio(new File(id))
            ? "<img src='" + Utils.bytesToBase64(THUMB_AUDIO_DEFAULT) + "'><br>"
            : "<img src='" + Utils.bytesToBase64(ThumbMediaController.getOrDefault(id, THUMB_NOT_FOUND)) + "'><br>";
      }
   }

   private String getTxt(String id, String name) {
      return id.startsWith(Values.PATH_MEDIA_AUDIO)
         ? "<div style='width:170px'>  <span id='span-single' style='white-space:normal'>    &nbsp;&nbsp;"
            + StringUtils.maxLength(name, 64, true)
            + "&nbsp;&nbsp;  </span></div>"
         : "<span>" + StringUtils.maxLength(name, 16, true) + "</span>";
   }

   private int getColBreak(String id, int size) {
      return id.startsWith(Values.PATH_MEDIA_AUDIO) ? Math.min((int)Math.ceil(Math.sqrt((double)size)), 3) : (int)Math.ceil(Math.sqrt((double)size));
   }

   static {
      byte[] bytes1;
      byte[] bytes2;
      try {
         String base64_1 = Img.encodeToString(ThumbMediaController.IMAGE_NOT_FOUND, "png");
         String base64_2 = Img.encodeToString(Img.resize(HItemAudio.FAVORITE_THUMB, ThumbMediaController.getThumbSize()), "png");
         bytes1 = Utils.base64ToBytes(base64_1);
         bytes2 = Utils.base64ToBytes(base64_2);
      } catch (Exception var4) {
         bytes1 = null;
         bytes2 = null;
      }

      THUMB_NOT_FOUND = bytes1;
      THUMB_AUDIO_DEFAULT = bytes2;
   }
}
