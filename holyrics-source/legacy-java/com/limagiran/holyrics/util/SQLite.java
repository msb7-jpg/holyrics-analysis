package com.limagiran.holyrics.util;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class SQLite {
   public static final File DIR_DB = new File(Values.PATH_FILES + "database.db");

   private static boolean sqlUpdate(String sql) {
      Connection conn = null;
      PreparedStatement stm = null;

      boolean var4;
      try {
         conn = getConnection();
         setPragma(conn, true);
         stm = conn.prepareStatement(sql);
         stm.execute();
         close(stm);
         setPragma(conn, false);
         return true;
      } catch (Exception var8) {
         Log.send(var8);
         var4 = false;
      } finally {
         close(conn, stm);
      }

      return var4;
   }

   public static boolean open() {
      sqlUpdate("CREATE TABLE IF NOT EXISTS music(_id INTEGER PRIMARY KEY, music TEXT)");
      return true;
   }

   public static Connection getConnection() {
      try {
         Class.forName("org.sqlite.JDBC");
         return DriverManager.getConnection("jdbc:sqlite:" + DIR_DB);
      } catch (Exception var1) {
         Log.send(var1);
         return null;
      }
   }

   public static void setPragma(Connection conn, boolean flag) {
      String sql = "PRAGMA FOREIGN_KEYS = " + (flag ? "ON" : "OFF");

      try (PreparedStatement stm = conn.prepareStatement(sql)) {
         stm.execute();
      } catch (Exception var16) {
      }
   }

   public static void deleteTable(String table) {
      sqlUpdate("DELETE FROM " + table);
      sqlUpdate("DROP TABLE IF EXISTS main." + table);
   }

   private static void close(Object... obj) {
      for (Object o : obj) {
         try {
            if (o instanceof AutoCloseable) {
               ((AutoCloseable)o).close();
            }
         } catch (Exception var6) {
         }
      }
   }

   static {
      Utils.mkdirs(DIR_DB);
   }
}
