package com.limagiran.vagalumeapi;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class Result {
   public final String type;
   public final Artist art;
   public final List<Music> mus;
   public final boolean captcha;
   public final long serial;
   public final String key2;
   public final String captcha_img;

   public Result(String type, Artist art, List<Music> mus, boolean captcha, long serial, String key2, String captcha_img) {
      this.type = type;
      this.art = art;
      this.mus = mus;
      this.captcha = captcha;
      this.serial = serial;
      this.key2 = key2;
      this.captcha_img = captcha_img;
   }

   public final String getCaptcha() {
      try {
         URL url = new URL(this.captcha_img);
         return JOptionPane.showInputDialog(new ImageIcon(url));
      } catch (MalformedURLException var2) {
         return "";
      }
   }

   public boolean isExact() {
      return this.type != null && this.type.equalsIgnoreCase("exact");
   }

   public boolean isAproximate() {
      return this.type != null && this.type.equalsIgnoreCase("aprox");
   }
}
