package com.limagiran.holyrics.webservice;

import com.google.gson.reflect.TypeToken;
import com.limagiran.bible.Bible;
import com.limagiran.bible.Verse;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BibleSlideUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WebServiceUtils;
import com.limagiran.holyrics.view.BibleMain;
import com.limagiran.holyrics.view.BiblePresentationView;
import com.limagiran.holyrics.view.control.BiblePresentationControl;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import javax.jws.WebService;
import javax.swing.SwingUtilities;
import javax.xml.ws.Endpoint;

@WebService(
   endpointInterface = "com.limagiran.holyrics.webservice.IRemoteControlBibleWS"
)
public class RemoteControlBibleWS implements IRemoteControlBibleWS {
   private static final String VERSE_ERROR = "01001001Error\n\nError\n\nError";
   private static Endpoint ENDPOINT;
   private static String currentIP = "";

   public static void open() {
      if (Settings.EnumSettings2.WEB_SERVICE_ENABLED.is()) {
         Utils.startThreadDaemon(
            () -> {
               try {
                  close();
                  Optional.ofNullable(WebServiceUtils.getIP())
                     .ifPresent(ip -> ENDPOINT = Endpoint.publish("http://" + (currentIP = ip.ip) + ":777/remoteControlBibleWS", new RemoteControlBibleWS()));
               } catch (Exception var1) {
               }
            }
         );
      }
   }

   public static void close() {
      Optional.ofNullable(ENDPOINT).ifPresent(e -> e.stop());
   }

   public static boolean isOpen() {
      WebServiceUtils.IP ip = WebServiceUtils.getIP();
      return ENDPOINT != null && ENDPOINT.isPublished() && ip != null && currentIP.equals(ip.ip);
   }

   @Override
   public boolean isConnected() {
      return true;
   }

   @Override
   public boolean connect(String key) {
      return checkPassword(key);
   }

   public static boolean checkPassword(Pack p) {
      return checkPassword(p.getString("password", ""));
   }

   public static boolean checkPassword(String password) {
      String _password = Settings.EnumSettings.PASSWORD_REMOTE_CONTROL_BIBLE.getString();
      return _password.isEmpty() || password.equals(_password);
   }

   @Override
   public String getMapChaptersAndVerses() {
      try {
         return HolyricsWS.GSON.toJson(BibleMain.getInstance().getCurrentBible().getMap(), (new TypeToken<HashMap<Integer, HashMap<Integer, Integer>>>() {
         }).getType());
      } catch (Exception var2) {
         return "";
      }
   }

   private static Pack fillMapChaptersAndVerses(Pack pack) {
      List<String> mapList = BibleMain.getInstance().getCurrentBible().getMapAsList();

      for (int i = 0; i < mapList.size(); i++) {
         pack.put("b" + (i + 1), mapList.get(i));
      }

      return pack;
   }

   @Override
   public String goTo(int book, int chapter, int verse) {
      try {
         Verse v = BibleMain.getInstance().getCurrentBible().getVerse(book, chapter, verse, true);
         return formatReturn(v);
      } catch (Exception var5) {
         return "01001001Error\n\nError\n\nError";
      }
   }

   private static int[] resolveID(Pack p) {
      int[] _return = new int[]{1, 1, 1};

      try {
         String id = p.getString("id", "");
         boolean b = id.isEmpty();
         _return[0] = b ? p.getInt("book", 1) : Utils.intOrElse(id.substring(0, 2), 1);
         _return[1] = b ? p.getInt("chapter", 1) : Utils.intOrElse(id.substring(2, 5), 1);
         _return[2] = b ? p.getInt("verse", 1) : Utils.intOrElse(id.substring(5, 8), 1);
      } catch (Exception var4) {
      }

      return _return;
   }

   private static Pack getVerse(Pack p) {
      int[] arr = resolveID(p);
      Verse v = BibleMain.getInstance().getCurrentBible().getVerse(arr[0], arr[1], arr[2], true);
      return fillReturnVersesPack(Pack.create(true), v);
   }

   private static Pack getCurrentVerse() {
      return fillReturnVersesPack(Pack.create(true), BibleMain.getInstance().getCurrentVerse());
   }

   @Override
   public String getCurrentID_VersePreviousAndNextVerse() {
      Verse v = BibleMain.getInstance().getCurrentVerse();
      return formatReturn(v);
   }

   @Override
   public String show(int book, int chapter, int verse) {
      Verse v = BibleMain.getInstance().getCurrentBible().getVerse(book, chapter, verse, true);
      BibleMain.getInstance().showVerse(v);
      return formatReturn(v);
   }

   private static Pack show2(Pack p) {
      int[] arr = resolveID(p);
      Verse v = BibleMain.getInstance().getCurrentBible().getVerse(arr[0], arr[1], arr[2], true);
      BibleMain.getInstance().showVerse(v);
      return fillReturnVersesPack(Pack.create(true), v);
   }

   @Override
   public void exit() {
      SwingUtilities.invokeLater(BiblePresentationView::staticExit);
   }

   private static Pack exit2() {
      SwingUtilities.invokeLater(BiblePresentationView::staticExit);
      return Pack.create(true);
   }

   @Override
   public String next(int book, int chapter, int verse, boolean visible) {
      Bible b = BibleMain.getInstance().getCurrentBible();
      Verse v = BiblePresentationControl.nextVerse(b.verses, b.getVerse(book, chapter, verse, true));
      action(v, visible);
      return formatReturn(v);
   }

   private static Pack next2(Pack p) {
      int[] arr = resolveID(p);
      boolean visible = p.getBoolean("show");
      Bible bible = BibleMain.getInstance().getCurrentBible();
      Verse v = BiblePresentationControl.nextVerse(bible.verses, bible.getVerse(arr[0], arr[1], arr[2], true));
      action(v, visible);
      return fillReturnVersesPack(Pack.create(true), v);
   }

   @Override
   public String previous(int book, int chapter, int verse, boolean visible) {
      Bible b = BibleMain.getInstance().getCurrentBible();
      Verse v = BiblePresentationControl.previousVerse(b.verses, b.getVerse(book, chapter, verse, true));
      action(v, visible);
      return formatReturn(v);
   }

   private static Pack previous2(Pack p) {
      int[] arr = resolveID(p);
      boolean visible = p.getBoolean("show");
      Bible b = BibleMain.getInstance().getCurrentBible();
      Verse v = BiblePresentationControl.previousVerse(b.verses, b.getVerse(arr[0], arr[1], arr[2], true));
      action(v, visible);
      return fillReturnVersesPack(Pack.create(true), v);
   }

   private static void action(Verse v, boolean visible) {
      if (visible) {
         SwingUtilities.invokeLater(() -> {
            if (BiblePresentationView.staticIsVisible() && BiblePresentationView.staticIsAllVerses()) {
               if (!BiblePresentationView.staticSetVerse(v, true)) {
                  BibleMain.getInstance().showVerse(v);
               }
            } else {
               BibleMain.getInstance().showVerse(v);
            }
         });
      }
   }

   private static String formatReturn(Verse v) {
      try {
         List<Verse> list = BibleMain.getInstance().getCurrentBible().verses;
         String content = BibleSlideUtils.getVerses(v, list, Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt())
            .stream()
            .map(Verse::toString)
            .collect(Collectors.joining("\n"));
         return v.getId() + BiblePresentationControl.previousVerse(list, v) + "\n\n" + content + "\n\n" + BiblePresentationControl.nextVerse(list, v);
      } catch (Exception var3) {
         return "01001001Error\n\nError\n\nError";
      }
   }

   private static Pack fillReturnVersesPack(Pack p, Verse v) {
      try {
         List<Verse> list = BibleMain.getInstance().getCurrentBible().verses;
         String content = BibleSlideUtils.getVerses(v, list, Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt())
            .stream()
            .map(Verse::toString)
            .collect(Collectors.joining("\n"));
         p.put("id", v.getId())
            .put("book", v.book)
            .put("chapter", v.chapter)
            .put("verse", v.verse)
            .put("current", content)
            .put("previous", BiblePresentationControl.previousVerse(list, v).toString())
            .put("next", BiblePresentationControl.nextVerse(list, v).toString());
      } catch (Exception var4) {
         p.put("id", "01001001").put("book", 1).put("chapter", 1).put("verse", 1).put("current", "Error").put("previous", "Error").put("next", "Error");
      }

      return p;
   }

   @Override
   public String webService(String pack) {
      return this.webService2(pack).put("wb", true).toJSon();
   }

   @Override
   public String ws(String pack) {
      try {
         return checkPassword(Pack.create(pack)) ? this.webService3(pack).toJSon() : Pack.create(false, "invalid_password").toJSon();
      } catch (Exception var3) {
         return Pack.create(false, "exception").toJSon();
      }
   }

   private Pack webService2(String pack) {
      Pack p = Pack.create(pack);
      String var3 = p.getString("request", "");
      switch (var3) {
         case "sendHistory":
            return this.sendHistory(p);
         case "getDisplayedVersesAndVersion":
            return this.getDisplayedVersesAndVersion();
         case "setDisplayedVersesAndVersion":
            return this.setDisplayedVersesAndVersion(p);
         case "blackScreen":
            return this.blackScreen(p);
         case "blankScreen":
            return this.blankScreen(p);
         default:
            return Pack.create(false, "not_found");
      }
   }

   private Pack webService3(String pack) {
      try {
         Pack p = Pack.create(pack);
         String var3 = p.getString("request", "");
         switch (var3) {
            case "getMapChaptersAndVerses":
               return fillMapChaptersAndVerses(Pack.create(true));
            case "getVerse":
               return getVerse(p);
            case "getCurrentVerse":
               return getCurrentVerse();
            case "show":
               return show2(p);
            case "exit":
               return exit2();
            case "previous":
               return previous2(p);
            case "next":
               return next2(p);
            case "getDisplayedVersesAndVersion":
               return this.getDisplayedVersesAndVersion();
            case "setDisplayedVersesAndVersion":
               return this.setDisplayedVersesAndVersion2(p);
            case "blackScreen":
               return this.blackScreen(p);
            case "blankScreen":
               return this.blankScreen(p);
            default:
               return Pack.create(false, "not_found");
         }
      } catch (Exception var5) {
         return Pack.create(false, "exception");
      }
   }

   private Pack sendHistory(Pack p) {
      try {
         Bible b = BibleMain.getInstance().getBible();
         BibleMain.getInstance().addPlayList(Arrays.stream(p.getString("list", "").split(";")).map(id -> b.getVerse(id, true)).collect(Collectors.toList()));
         return Pack.create(true);
      } catch (Exception var3) {
         return Pack.create(false, "exception");
      }
   }

   private Pack getDisplayedVersesAndVersion() {
      try {
         return Pack.create(true)
            .put("displayed", Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt())
            .put("versions", BibleMain.getInstance().getTabVersions().stream().collect(Collectors.joining(";")));
      } catch (Exception var2) {
         return Pack.create(false, "exception");
      }
   }

   private Pack setDisplayedVersesAndVersion(Pack p) {
      try {
         Verse v = BibleMain.getInstance().getBible().getVerse(p.getString("verse_id", "01001001"), true);
         CountDownLatch count = new CountDownLatch(1);
         SwingUtilities.invokeLater(() -> {
            int displayed = p.getInt("displayed", Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt());
            if (displayed != Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt()) {
               BibleMain.getInstance().setDisplayedVerses(displayed);
            }

            int tabVersion = p.getInt("tab_version", 1);
            if (tabVersion != 1) {
               BibleMain.getInstance().makePrimary(tabVersion);
            }

            count.countDown();
         });
         count.await();
         v = BibleMain.getInstance().getBible().getVerse(v.getId(), true);
         return Pack.create(true).put("verses", formatReturn(v));
      } catch (Exception var4) {
         return Pack.create(false, "exception");
      }
   }

   private Pack setDisplayedVersesAndVersion2(Pack p) {
      try {
         Verse v = BibleMain.getInstance().getBible().getVerse(p.getString("verse_id", "01001001"), true);
         CountDownLatch count = new CountDownLatch(1);
         SwingUtilities.invokeLater(() -> {
            int displayed = p.getInt("displayed", Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt());
            if (displayed != Settings.EnumSettings.BIBLE_SIZE_VERSE_PRESENTATION.getInt()) {
               BibleMain.getInstance().setDisplayedVerses(displayed);
            }

            int tabVersion = p.getInt("tab_version", 1);
            if (tabVersion != 1) {
               BibleMain.getInstance().makePrimary(tabVersion);
            }

            count.countDown();
         });
         count.await();
         v = BibleMain.getInstance().getBible().getVerse(v.getId(), true);
         return fillReturnVersesPack(Pack.create(true), v);
      } catch (Exception var4) {
         return Pack.create(false, "exception");
      }
   }

   private Pack blackScreen(Pack p) {
      boolean flag = p.getBoolean("value");
      if (flag != BiblePresentationView.staticIsBlack()) {
         BiblePresentationView.staticBlack();
      }

      return Pack.create(true);
   }

   private Pack blankScreen(Pack p) {
      boolean flag = p.getBoolean("value");
      if (flag != BiblePresentationView.staticIsBlank()) {
         BiblePresentationView.staticBlank();
      }

      return Pack.create(true);
   }
}
