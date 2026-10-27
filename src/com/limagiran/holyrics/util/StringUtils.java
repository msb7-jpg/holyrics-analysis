package com.limagiran.holyrics.util;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.concurrent.TimeUnit;

public class StringUtils {
   public static String maxLength(String value, int max, boolean withPoints) {
      return value.length() <= max ? value : value.substring(0, max) + (withPoints ? "..." : "");
   }

   public static String maxLength(String value, int max) {
      return maxLength(value, max, false);
   }

   public static String maxLengthGetEnd(String value, int max, boolean withPoints) {
      return value.length() <= max ? value : (withPoints ? "..." : "") + value.substring(value.length() - max, value.length());
   }

   public static String maxLengthGetEnd(String value, int max) {
      return maxLength(value, max, false);
   }

   public static int firstNumber(String customParagraph, int _default) {
      try {
         StringBuilder sb = new StringBuilder();
         boolean flag = false;
         int i = -1;

         while (++i < customParagraph.length()) {
            char c = customParagraph.charAt(i);
            if (c >= '0' && c <= '9') {
               if (!flag && i > 0 && customParagraph.charAt(i - 1) == '-') {
                  sb.append("-");
               }

               sb.append(c);
               flag = true;
            } else if (flag) {
               break;
            }
         }

         return Integer.parseInt(sb.toString());
      } catch (Exception var6) {
         return _default;
      }
   }

   public static String stringToSearch(String value) {
      return removeAccent(value.replace("\n", " ").toLowerCase());
   }

   public static String removeAccent(String string) {
      return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
   }

   public static String setupHighlight(String text, String value) {
      if (text != null && !text.isEmpty() && value != null && !value.isEmpty()) {
         try {
            value = stringToSearch(value.trim());
            String highlight = "";
            StringBuilder sb = new StringBuilder(text);
            if (stringToSearch(text).indexOf(value) == 0) {
               String var10 = sb.substring(0, value.length());
               highlight = applyHighlight(var10);
               sb.replace(0, value.length(), highlight);
            }

            int index = stringToSearch(sb.toString()).indexOf(value, highlight.length());

            while (index >= 0) {
               int end = index + value.length();
               int size = sb.length();
               highlight = sb.substring(index, end);
               sb.replace(index, end, applyHighlight(highlight));
               index = removeAccent(sb.toString()).toLowerCase().indexOf(value, end + (sb.length() - size));
            }

            return sb.toString();
         } catch (Exception var8) {
            return text;
         }
      } else {
         return text;
      }
   }

   private static String applyHighlight(String value) {
      return "<span style='background-color:" + StyleUI.COLOR_HEX_HIGHLIGHT_TEXT + "'>" + value + "</span>";
   }

   public static String spanNoWrap(String value) {
      return "<span style='white-space:nowrap'>" + value + "</span>";
   }

   public static String removeExcessLineBreak(String value) {
      if (value == null) {
         return value;
      } else {
         while (value.contains("\n\n")) {
            value = value.replace("\n\n", "\n");
         }

         while (value.startsWith("\n")) {
            value = value.substring(1);
         }

         while (value.endsWith("\n")) {
            value = value.substring(0, value.length() - 1);
         }

         return value;
      }
   }

   public static String formatTimeHMS(long millis) {
      long h = TimeUnit.MILLISECONDS.toHours(millis);
      long m = TimeUnit.MILLISECONDS.toMinutes(millis);
      long s = TimeUnit.MILLISECONDS.toSeconds(millis);
      return String.format("%02d:%02d:%02d", h, m % 60L, s % 60L);
   }

   public static String formatTimeMm_Ss_MSms(long millis) {
      long m = TimeUnit.MILLISECONDS.toMinutes(millis);
      long s = TimeUnit.MILLISECONDS.toSeconds(millis);
      return String.format("%02dm %02ds %03dms", m, s % 60L, millis % 1000L);
   }

   public static String wordWrap(String str, int len) {
      if (str != null && str.length() > len && len >= 1) {
         StringBuilder sb = new StringBuilder(str);
         int count = 0;

         for (int i = 0; i < sb.length(); i++) {
            count = sb.charAt(i) != ' ' ? count + 1 : 0;
            if (count == len) {
               sb.insert(++i, " ");
               count = 0;
            }
         }

         return sb.toString();
      } else {
         return str;
      }
   }

   public static String escapeHTML(String html) {
      return html.replace("&", "&#38;").replace("\"", "&#34;").replace("'", "&#39;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>");
   }

   public static String onlyWordNumberAndSpace(String value) {
      return value.replaceAll("[^\\d\\w _àèìòùÀÈÌÒÙáéíóúýÁÉÍÓÚÝâêîôûÂÊÎÔÛãñõÃÑÕäëïöüÿÄËÏÖÜŸçÇ]", "");
   }
}
