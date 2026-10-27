package com.limagiran.holyrics.share;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Account;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Download;
import com.limagiran.holyrics.util.StringUtils;
import java.awt.Window;
import java.util.HashMap;
import java.util.Map;

public class SharePHP {
   public static void upload(Account account, String hashFiles, String jsonFiles) throws Exception {
      Map<String, String> map = new HashMap<>();
      map.put("username", account.getUsername().toLowerCase());
      map.put("access_key", account.getAccessKey());
      map.put("security", account.getSecurity());
      map.put("json", jsonFiles);
      map.put("hash_json", hashFiles);
      String[] _return = Download.sendPostLargeGetString("http://www.holyrics.com/login/upload.php", map).split(";");
      if (!_return[0].equalsIgnoreCase("success")) {
         throw new Exception(_return[1]);
      } else {
         Settings.SET.saveNow();
      }
   }

   public static void createLogin(Window owner, Account account) throws Exception {
      Map<String, String> map = new HashMap<>();
      map.put("username", account.getUsername().toLowerCase());
      map.put("access_key", account.getAccessKey());
      map.put("security", account.getSecurity());
      map.put("key", "**k3ySync**hOlYrIcS!");
      String[] _return = Download.sendPostGetString("http://www.holyrics.com/login/create.php", map).split(";");
      if (!_return[0].equalsIgnoreCase("success")) {
         throw new Exception(_return[1]);
      }
   }

   public static String getHashUpdate(Account account) throws Exception {
      Map<String, String> map = new HashMap<>();
      map.put("username", account.getUsername().toLowerCase());
      map.put("access_key", account.getAccessKey());
      String[] response = Download.sendPostGetString("http://www.holyrics.com/login/getUpdate.php", map).split(";");
      if (response[0].equals("success")) {
         return response.length > 1 ? response[1] : "";
      } else {
         throw new Exception(response[1]);
      }
   }

   public static String deleteMyAccount(Window owner, Account account) {
      Map<String, String> map = new HashMap<>();
      map.put("key", "**k3y-Del**hOlYrIcS!");
      map.put("username", account.getUsername().toLowerCase());
      map.put("access_key", account.getAccessKey());
      map.put("security", account.getSecurity());

      try {
         String[] response = Download.sendPostGetString("http://www.holyrics.com/login/delete.php", map).split(";");
         if (!response[0].equalsIgnoreCase("success")) {
            return "f" + response[1];
         } else {
            Settings.EnumSettings.DATETIME_LAST_REGISTER.set(0L);
            return "t" + I18N.MSG_ACCOUNT_DELETED.val();
         }
      } catch (Exception var4) {
         return "f" + I18N.MSG_CONNECTION_FAILURE.val();
      }
   }

   public static String download(Window owner, String username, String accessKey) throws Exception {
      Map<String, String> map = new HashMap<>();
      map.put("username", StringUtils.onlyWordNumberAndSpace(username));
      map.put("access_key", StringUtils.onlyWordNumberAndSpace(accessKey));
      map.put("key", "*k3yDownLoaDhOlYrIcS!");
      String[] _return = Download.sendPostGetString("http://www.holyrics.com/login/download.php", map).split(";");
      if (_return.length <= 0) {
         throw new Exception(I18N.MSG_ERROR_TRY_AGAIN.val());
      } else if (!_return[0].equalsIgnoreCase("success")) {
         throw new Exception(_return[1]);
      } else if (_return.length == 1) {
         throw new Exception(I18N.MSG_NO_UPDATE_AVAILABLE.val());
      } else {
         return _return[1];
      }
   }
}
