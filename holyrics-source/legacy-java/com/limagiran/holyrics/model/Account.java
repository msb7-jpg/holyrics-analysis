package com.limagiran.holyrics.model;

import com.limagiran.holyrics.i18n.I18N;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

public class Account implements Serializable {
   private static final long serialVersionUID = 12491L;
   private String username;
   private String accessKey;
   private String security;
   private long version = 1L;

   public Account(String username, String accessKey, String security) {
      this.username = username;
      this.accessKey = accessKey;
      this.security = security;
   }

   public Account() {
      this("", "", "");
   }

   public String getUsername() {
      return this.username;
   }

   public void setUsername(String username) {
      this.username = username;
   }

   public String getAccessKey() {
      return this.accessKey;
   }

   public void setAccessKey(String accessKey) {
      this.accessKey = accessKey;
   }

   public String getSecurity() {
      return this.security;
   }

   public void setSecurity(String security) {
      this.security = security;
   }

   public void validate() throws Exception {
      if (this.username.length() < 4) {
         throw new Exception(I18N.MSG_ERROR_MIN_LENGTH_USERNAME.format(4));
      } else if (this.accessKey.length() < 4 || this.security.length() < 4) {
         throw new Exception(I18N.MSG_ERROR_MIN_LENGTH_PASSWORD.format(4));
      }
   }

   public void clear() {
      this.username = "";
      this.accessKey = "";
      this.security = "";
   }

   public void set(Account create) {
      this.username = create.username;
      this.accessKey = create.accessKey;
      this.security = create.security;
   }

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
      ois.defaultReadObject();
      this.version = 1L;
   }
}
