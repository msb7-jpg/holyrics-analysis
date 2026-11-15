package com.limagiran.holyrics.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.limagiran.holyrics.i18n.I18N;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseEvent;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;
import javax.swing.Icon;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.xml.bind.DatatypeConverter;

public class Utils {
   private static long x = -1L;
   private static long y = -1L;
   private static long z = 0L;
   private static long nanoTime;
   private static long millisecondId = System.currentTimeMillis();

   public static void ini() {
      nanoTime = System.nanoTime();
   }

   public static String end() {
      double time = ((double)System.nanoTime() - (double)nanoTime) / 1.0E9;
      String _return;
      if (time > 60.0) {
         double mod = time % 60.0;
         int minutes = (int)(time / 60.0);
         _return = minutes + "min" + (int)mod + "s";
      } else {
         _return = new DecimalFormat("#####0.0##").format(time) + "s";
      }

      System.out.println(_return);
      return _return;
   }

   public static double average(int amount, Runnable run) {
      OptionalDouble op = IntStream.range(0, amount).mapToLong(i -> {
         long ini = System.currentTimeMillis();
         run.run();
         return System.currentTimeMillis() - ini;
      }).average();
      op.ifPresent(System.out::println);
      return op.orElse(1.0);
   }

   public static double averageNano(int amount, Runnable run) {
      OptionalDouble op = IntStream.range(0, amount).mapToLong(i -> {
         long ini = System.nanoTime();
         run.run();
         return System.nanoTime() - ini;
      }).average();
      op.ifPresent(System.out::println);
      return op.orElse(1.0);
   }

   public static void sleep(long time) {
      try {
         Thread.sleep(time);
      } catch (Exception var3) {
      }
   }

   public static boolean confirm(Component c, String text, String title) {
      return JOptionPane.showConfirmDialog(c, text, title, 0) == 0;
   }

   public static boolean confirm(Component c, String text) {
      return confirm(c, text, "");
   }

   public static boolean confirm(String text, String title) {
      return confirm(null, text, title);
   }

   public static boolean confirm(String text) {
      return confirm(null, text, I18N.WORD_CONFIRM.val());
   }

   public static void error(Component c, String text, String title) {
      SwingUtils.runOnUIThread(() -> JOptionPane.showMessageDialog(c, text, title, 0));
   }

   public static void error(String text, String title) {
      error(null, text, title);
   }

   public static void error(Component c, String text) {
      error(c, text, I18N.WORD_ERROR.val());
   }

   public static void error(String text) {
      error(null, text, I18N.WORD_ERROR.val());
   }

   public static void msg(Component c, String text, String title) {
      SwingUtils.runOnUIThread(() -> JOptionPane.showMessageDialog(c, text, title, -1));
   }

   public static void msg(String text, String title) {
      msg(null, text, title);
   }

   public static void msg(Component c, String text) {
      msg(c, text, I18N.WORD_MESSAGE.val());
   }

   public static void msg(String text) {
      msg(null, text, I18N.WORD_MESSAGE.val());
   }

   public static <T extends Comparable> T range(T value, T min, T max) {
      return (double)value.compareTo(min) < 0.0 ? min : ((double)value.compareTo(max) > 0.0 ? max : value);
   }

   public static boolean isDoubleClick(MouseEvent e) {
      if (SwingUtilities.isLeftMouseButton(e)) {
         long _x = x;
         long _y = y;
         long _z = z;
         return (x = (long)e.getXOnScreen()) == _x & (y = (long)e.getYOnScreen()) == _y & (z = System.currentTimeMillis()) - _z <= 500L;
      } else {
         x = -1L;
         return false;
      }
   }

   public static void clearDoubleClick() {
      x = -1L;
      y = -1L;
      z = 0L;
   }

   public static String removeAccent(String string) {
      return Normalizer.normalize(string, Form.NFD).replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
   }

   public static byte[] compress(String text) throws Exception {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      try (OutputStream out = new DeflaterOutputStream(baos)) {
         out.write(text.getBytes("UTF-8"));
      }

      return baos.toByteArray();
   }

   public static String decompress(byte[] bytes) throws Exception {
      InputStream in = new InflaterInputStream(new ByteArrayInputStream(bytes));
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      byte[] buffer = new byte[8192];

      int len;
      while ((len = in.read(buffer)) > 0) {
         baos.write(buffer, 0, len);
      }

      return new String(baos.toByteArray(), "UTF-8");
   }

   public static byte[] base64ToBytes(String string) {
      return DatatypeConverter.parseBase64Binary(string);
   }

   public static String bytesToBase64(byte[] bytes) {
      return DatatypeConverter.printBase64Binary(bytes);
   }

   public static Font loadFont(InputStream stream, int type, int size) {
      try {
         Font font = Font.createFont(0, stream);
         return font.deriveFont(type, (float)size);
      } catch (Exception var4) {
         return new Font("Tahoma", type, size);
      }
   }

   public static String firstLetterToUpperCase(String value) {
      if (value == null) {
         return null;
      } else {
         List<String> escape = Arrays.asList("da", "de", "do", "das", "dos");
         List<String> splits = Arrays.asList(" ", "\n");
         value = value.toLowerCase();

         for (String split : splits) {
            String[] palavras = value.split(split);
            StringBuilder sb = new StringBuilder();

            for (String s : palavras) {
               if (!escape.contains(s)) {
                  sb.append(String.valueOf(s.charAt(0)).toUpperCase()).append(s.substring(1));
               } else {
                  sb.append(s);
               }

               sb.append(split);
            }

            value = sb.length() > 1 ? sb.substring(0, sb.length() - 1) : sb.toString();
         }

         return value;
      }
   }

   public static String getHexColor(Color c) {
      return intToHex(c.getRed(), 2) + intToHex(c.getGreen(), 2) + intToHex(c.getBlue(), 2);
   }

   private static String intToHex(int value, int size) {
      String _return = Integer.toHexString(value);

      while (_return.length() < size) {
         _return = "0" + _return;
      }

      return _return;
   }

   public static String getPercentage(double value, double total) {
      return total > 0.0 ? new DecimalFormat("##0.0").format(value / total * 100.0) + "%" : "0.0%";
   }

   public static int getPercentageInt(double value, double total) {
      return range(total > 0.0 ? (int)(value / total * 100.0) : 0, 0, 100);
   }

   public static String removeExcessSpace(String value) {
      return value == null ? null : value.trim().replaceAll("[ ]{2,}", " ");
   }

   public static String notOnlySpace(String text) {
      return text.replace(" ", "").isEmpty() ? "" : text;
   }

   public static String getTextArea(Component owner, String title, String text) {
      JTextArea textArea = new JTextArea(text);
      JScrollPane scrollPane = new JScrollPane(textArea) {
         @Override
         protected void paintComponent(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 6, 6);
            super.paintComponent(g);
         }
      };
      scrollPane.setOpaque(true);
      scrollPane.setFocusable(false);
      textArea.setFont(new Font("Georgia", 0, 16));
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      textArea.setCaretPosition(textArea.getDocument().getLength());
      scrollPane.setPreferredSize(new Dimension(600, 300));
      SwingUtils.requestFocusOnVisible(textArea);
      return JOptionPane.showConfirmDialog(owner, scrollPane, title, 2, -1) == 0 ? textArea.getText() : null;
   }

   public static synchronized long generateMillisecondId() {
      long id = System.currentTimeMillis();
      return id <= millisecondId ? ++millisecondId : (millisecondId = id);
   }

   public static String getMacAddress() {
      try {
         NetworkInterface network = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
         byte[] mac = network.getHardwareAddress();
         StringBuilder sb = new StringBuilder();

         for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], i < mac.length - 1 ? "-" : ""));
         }

         return sb.toString();
      } catch (Exception var4) {
         return "unknown";
      }
   }

   public static int random(int max) {
      return max < 1 ? -1 : (int)(Math.random() * 1000000.0 % (double)max) + 1;
   }

   public static int intOrElse(Object s, int def) {
      try {
         return Integer.parseInt(s.toString());
      } catch (Exception var3) {
         return def;
      }
   }

   public static float floatOrElse(Object s, float def) {
      try {
         return Float.parseFloat(s.toString());
      } catch (Exception var3) {
         return def;
      }
   }

   public static long longOrElse(Object s, long def) {
      try {
         return Long.parseLong(s.toString());
      } catch (Exception var4) {
         return def;
      }
   }

   public static double doubleOrElse(Object s, double def) {
      try {
         return Double.parseDouble(s.toString());
      } catch (Exception var4) {
         return def;
      }
   }

   public static String lowPassRandom(int size) {
      String lowPass = "ae12345";
      return IntStream.range(0, size).mapToObj(i -> "ae12345".charAt(random("ae12345".length()) - 1)).map(String::valueOf).reduce("", String::concat);
   }

   public static boolean is32Bits() {
      return System.getProperty("sun.arch.data.model").equals("32");
   }

   public static void safeException(Runnable runnable) {
      try {
         runnable.run();
      } catch (Exception var2) {
      }
   }

   public static void safeException2(Utils.RunnableException runnable) {
      try {
         runnable.run();
      } catch (Exception var2) {
      }
   }

   public static boolean randomBoolean() {
      return (int)(Math.random() * 10.0) % 2 == 0;
   }

   public static void openFile(File file) {
      try {
         Desktop.getDesktop().open(file);
      } catch (Exception var2) {
      }
   }

   public static void openAppFilesReceivedFolder() {
      File file = new File(Values.PATH_MEDIA_APP);
      if (!file.exists()) {
         file.mkdirs();
      }

      try {
         Desktop.getDesktop().open(file);
      } catch (Exception var2) {
      }
   }

   public static void startThreadDaemon(Runnable run) {
      Thread thread = new Thread(run);
      thread.setDaemon(true);
      thread.start();
   }

   public static void startThreadDaemonMaxSleep(Supplier<Boolean> check, Runnable run, long maxSleep, boolean force) {
      Thread thread = new Thread(run);
      thread.setDaemon(true);

      for (long l = 0L; l <= maxSleep && !check.get(); l += 25L) {
         sleep(25L);
      }

      if (check.get() || force) {
         thread.start();
      }
   }

   public static void startThreadDaemonMaxSleep(Supplier<Boolean> check, Runnable run, long maxSleep) {
      startThreadDaemonMaxSleep(check, run, maxSleep, true);
   }

   public static void openLocation(File file) {
      try {
         Runtime.getRuntime().exec("explorer.exe /select," + file.getAbsolutePath());
      } catch (Exception var2) {
      }
   }

   public static void mkdirs(File file) {
      try {
         file = file.getAbsoluteFile();
         if (file.getName().contains(".")) {
            file.getParentFile().mkdirs();
         } else {
            file.mkdirs();
         }
      } catch (Exception var2) {
      }
   }

   public static String getClipboard() {
      try {
         return Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor).toString();
      } catch (Exception var1) {
         return "";
      }
   }

   public static void setClipboard(String content) {
      try {
         StringSelection selection = new StringSelection(content);
         Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
      } catch (Exception var2) {
      }
   }

   public static void openURL(String url) {
      try {
         Desktop.getDesktop().browse(new URL(url).toURI());
      } catch (Exception var2) {
      }
   }

   public static Dimension maximum(Dimension size, Dimension max) {
      double ratio;
      if (size.getWidth() / size.getHeight() > max.getWidth() / max.getHeight()) {
         ratio = max.getWidth() / size.getWidth();
      } else {
         ratio = max.getHeight() / size.getHeight();
      }

      return new Dimension((int)((double)size.width * ratio), (int)((double)size.height * ratio));
   }

   public static void maximumSetup(Dimension size, Dimension max) {
      double ratio;
      if (size.getWidth() / size.getHeight() > max.getWidth() / max.getHeight()) {
         ratio = max.getWidth() / size.getWidth();
      } else {
         ratio = max.getHeight() / size.getHeight();
      }

      size.width = (int)((double)size.width * ratio);
      size.height = (int)((double)size.height * ratio);
   }

   public static void createPDF(Map<String, List<String>> map) {
      try {
         File file = Dir.windowsSave(I18N.WORD_SAVE.val(), "PDF File (*.pdf)", ".pdf");
         if (file == null) {
            return;
         }

         Document document = new Document();
         document.setPageSize(PageSize.A4);
         document.setMargins(20.0F, 20.0F, 15.0F, 15.0F);

         try (FileOutputStream fos = new FileOutputStream(file)) {
            PdfWriter.getInstance(document, fos);
            document.open();
            Supplier<Paragraph> separator = () -> {
               Paragraph px = new Paragraph("\n");
               px.getFont().setSize(6.0F);
               return px;
            };

            for (Entry<String, List<String>> entry : map.entrySet()) {
               List<String> paragraphs = entry.getValue();
               Paragraph p = new Paragraph(entry.getKey());
               p.getFont().setStyle(1);
               document.add(p);
               document.add((Element)separator.get());

               for (int i = 0; i < paragraphs.size(); i++) {
                  p = new Paragraph(paragraphs.get(i));
                  p.getFont().setSize(10.0F);
                  p.setKeepTogether(true);
                  document.add(p);
                  if (i + 1 < paragraphs.size()) {
                     document.add((Element)separator.get());
                  }
               }
            }

            document.close();
         }

         msg(I18N.MSG_SAVED_SUCCESSFULLY.val());
         openLocation(file);
      } catch (Exception var21) {
         error(I18N.MSG_ERROR_TRY_AGAIN.val());
      }
   }

   public static void createPDF(String title, List<String> paragraphs) {
      Map<String, List<String>> map = new HashMap<>();
      map.put(title, paragraphs);
      createPDF(map);
      map.clear();
   }

   public static <T, U> boolean checkGenerics(Map map, Class<T> classKey, Class<U> classValue) {
      try {
         return map != null && map.keySet().stream().anyMatch(classKey::isInstance) && map.values().stream().anyMatch(classValue::isInstance);
      } catch (Exception var4) {
         return false;
      }
   }

   public static <T, U> boolean checkGenerics(LinkedHashMap map, Class<T> classKey, Class<U> classValue) {
      try {
         return map != null && map.keySet().stream().anyMatch(classKey::isInstance) && map.values().stream().anyMatch(classValue::isInstance);
      } catch (Exception var4) {
         return false;
      }
   }

   public static <T> boolean checkGenerics(List list, Class<T> clazz) {
      try {
         return list != null && list.parallelStream().anyMatch(clazz::isInstance);
      } catch (Exception var3) {
         return false;
      }
   }

   public static String usageMemoryMB() {
      Runtime run = Runtime.getRuntime();
      String val = (run.totalMemory() - run.freeMemory()) / 1000000L + " mb";
      System.out.println(val);
      return val;
   }

   public static Color decodeColor(String value, Color _default) {
      Color color = null;

      try {
         if (value.contains(",")) {
            String[] rgb = value.split(",");
            int r = intOrElse(rgb[0].trim(), 0);
            int g = intOrElse(rgb[1].trim(), 0);
            int b = intOrElse(rgb[2].trim(), 0);
            int a = rgb.length > 3 ? intOrElse(rgb[3].trim(), 255) : 255;
            color = new Color(r, g, b, a);
         } else {
            value = value.replace("#", "").replace(" ", "");
            switch (value.length()) {
               case 3: {
                  char[] array = value.toCharArray();
                  value = String.valueOf(new char[]{array[0], array[0], array[1], array[1], array[2], array[2]});
                  break;
               }
               case 4: {
                  char[] array = value.toCharArray();
                  value = String.valueOf(new char[]{array[0], array[0], array[1], array[1], array[2], array[2], array[3], array[3]});
                  break;
               }
               case 5:
               case 7:
               default:
                  value = "000000";
               case 6:
               case 8:
            }

            switch (value.length()) {
               case 6: {
                  int i = Integer.parseInt(value, 16);
                  color = new Color(i >> 16 & 0xFF, i >> 8 & 0xFF, i & 0xFF);
                  break;
               }
               case 8: {
                  int i = Integer.parseInt(value.substring(0, 6), 16);
                  color = new Color(i >> 16 & 0xFF, i >> 8 & 0xFF, i & 0xFF, Integer.parseInt(value.substring(6, 8), 16));
                  break;
               }
               default:
                  color = null;
            }
         }
      } catch (Exception var8) {
      }

      return color != null ? color : _default;
   }

   public static void decodeColor(String value, Consumer<Color> ifOk) {
      Optional.ofNullable(decodeColor(value, (Color)null)).ifPresent(ifOk);
   }

   public static class Input {
      private Component parent = null;
      private String msg = "";
      private String title = "Input Dialog";
      private String initial = "";
      private Icon icon = null;

      public static Utils.Input create() {
         return new Utils.Input();
      }

      public static void show(Component parent, String msg, String title, Icon icon, String initial, Consumer<String> consumer) {
         SwingUtils.runOnUIThread(() -> {
            Object input = JOptionPane.showInputDialog(parent, msg, title, -1, icon, null, initial);
            Optional.ofNullable(input).ifPresent(o -> consumer.accept(o.toString()));
         });
      }

      public void show(Consumer<String> consumer) {
         show(this.parent, this.msg, this.title, this.icon, this.initial, consumer);
         this.parent = null;
         this.msg = this.title = this.initial = null;
         this.icon = null;
      }

      public Utils.Input setParent(Component parent) {
         this.parent = parent;
         return this;
      }

      public Utils.Input setMsg(String msg) {
         this.msg = msg;
         return this;
      }

      public Utils.Input setTitle(String title) {
         this.title = title;
         return this;
      }

      public Utils.Input setInitial(String initial) {
         this.initial = initial;
         return this;
      }

      public Utils.Input setIcon(Icon icon) {
         this.icon = icon;
         return this;
      }
   }

   public interface RunnableException {
      void run() throws Exception;
   }
}
