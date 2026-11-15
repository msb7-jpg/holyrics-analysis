package com.limagiran.holyrics.model;

import com.limagiran.holyrics.util.StringUtils;
import java.io.Serializable;
import java.util.Objects;

public class AppMessage implements Serializable {
   private static long LAST_DATETIME = 0L;
   public static final String ADMIN_ID = "holyrics-admin";
   public final long datetime;
   public final String userId;
   public final String name;
   public final String message;

   private AppMessage(long datetime, String userId, String name, String message) {
      this.datetime = datetime;
      this.userId = userId;
      this.name = name.replace("\n", " ");
      this.message = message;
   }

   public static synchronized AppMessage create(String userId, String name, String message) {
      long datetime = System.currentTimeMillis();
      datetime = datetime <= LAST_DATETIME ? ++LAST_DATETIME : (LAST_DATETIME = datetime);
      return new AppMessage(datetime, userId, name, message);
   }

   @Override
   public String toString() {
      return this.name + "\n" + this.message;
   }

   public String toStringHTML() {
      return "<html><b>" + StringUtils.escapeHTML(this.name) + ":</b><br>" + StringUtils.escapeHTML(this.message);
   }

   public boolean checkUser(String userId) {
      return Objects.equals(this.userId, userId);
   }

   public boolean isMyself() {
      return this.checkUser("holyrics-admin");
   }
}
