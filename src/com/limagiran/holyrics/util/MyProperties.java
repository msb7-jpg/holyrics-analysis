package com.limagiran.holyrics.util;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.text.WebTextField;
import com.limagiran.holyrics.i18n.I18N;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Stream;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class MyProperties {
   private static final transient Map<String, String> FLAGS = new HashMap<>();
   private static final transient Object FLAGS_LOCK = new Object();
   private static final File FILE = new File("config.properties").getAbsoluteFile();
   private static volatile Properties properties;
   private static long lastLoad;

   private static synchronized String get(String key) throws Exception {
      try {
         Properties prop = load();
         return prop.getProperty(key);
      } catch (Exception var2) {
         throw var2;
      }
   }

   public static synchronized String get(String key, String def) {
      try {
         return Optional.ofNullable(get(key)).orElse(def);
      } catch (Exception var3) {
         return def;
      }
   }

   public static synchronized void set(String key, String value) {
      try {
         Properties prop = load();
         prop.put(key, value);

         try (FileOutputStream fos = new FileOutputStream(FILE)) {
            prop.store(fos, "Holyrics");
         }
      } catch (Exception var16) {
      }
   }

   public static synchronized Map<String, String> mapStartsWith(String prefix) {
      Map<String, String> map = new HashMap<>();

      try {
         Properties prop = load();
         prop.forEach((k, v) -> {
            if (String.valueOf(k).startsWith(prefix)) {
               map.put(String.valueOf(k), String.valueOf(v));
            }
         });
      } catch (Exception var3) {
      }

      return map;
   }

   private static synchronized Properties load() {
      if (properties != null && System.currentTimeMillis() - lastLoad < 10000L) {
         return properties;
      } else {
         lastLoad = System.currentTimeMillis();

         try (FileInputStream fis = new FileInputStream(FILE)) {
            properties = new Properties();
            properties.load(fis);
            return properties;
         } catch (Exception var14) {
            return new Properties();
         }
      }
   }

   public static void init() {
      if (!get("check", "").equals("ok")) {
         set("check", "ok");
      }
   }

   public static String getFlag(String key, String _default) {
      synchronized (FLAGS_LOCK) {
         return FLAGS.getOrDefault(key, _default);
      }
   }

   public static void setFlag(String key, String value) {
      synchronized (FLAGS_LOCK) {
         FLAGS.put(key, value);
      }
   }

   public static void editDialog(Component c) {
      SwingUtilities.invokeLater(() -> {
         JLabel lblParam = new JLabel("Parâmetro: ", 4);
         MyProperties.EnumProperties[] availables = MyProperties.EnumProperties.availables();
         JComboBox<MyProperties.EnumProperties> cmbbxParam = new JComboBox<>(availables);
         JLabel lblValue = new JLabel("Valor: ", 4);
         JTextField tfValue = new JTextField(availables[0].getString(), 32);
         cmbbxParam.setFocusable(false);
         cmbbxParam.addItemListener(evt -> {
            if (evt.getStateChange() == 1 && cmbbxParam.getSelectedItem() instanceof MyProperties.EnumProperties) {
               tfValue.setText(((MyProperties.EnumProperties)cmbbxParam.getSelectedItem()).getString());
            }
         });
         JPanel pGrid = new JPanel(new GridBagLayout());
         GridBagConstraints gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = 0;
         gbc.anchor = 13;
         pGrid.add(lblParam, gbc);
         gbc = new GridBagConstraints();
         gbc.gridx = 1;
         gbc.gridy = 0;
         gbc.gridwidth = 1;
         gbc.fill = 2;
         pGrid.add(cmbbxParam, gbc);
         gbc = new GridBagConstraints();
         gbc.gridx = 0;
         gbc.gridy = 1;
         gbc.anchor = 13;
         pGrid.add(lblValue, gbc);
         gbc = new GridBagConstraints();
         gbc.gridx = 1;
         gbc.gridy = 1;
         gbc.gridwidth = 1;
         gbc.fill = 2;
         pGrid.add(tfValue, gbc);
         SwingUtils.requestFocusOnVisible(tfValue);
         int op = JOptionPane.showConfirmDialog(c, pGrid, I18N.WORD_SETTINGS.val(), 2, -1);
         if (op == 0) {
            String param = ((MyProperties.EnumProperties)cmbbxParam.getSelectedItem()).key;
            String value = tfValue.getText();
            Utils.startThreadDaemon(() -> {
               set(param, value);
               Utils.msg(c, I18N.MSG_RESTART_TO_APPLY_CHANGES.val());
            });
         }
      });
   }

   public static enum EnumProperties {
      LOCALE("locale", ".default", null, false),
      FORCE_FX_VIDEO_THEME("force-fx-video-theme", Boolean.class, "false", null, false),
      VAGALUME_API_KEY("vagalume-api-key", "", null, false),
      MAP_SLIDE_LABEL_TITLE_COLOR("map-slide-label-title-color", "", null, true),
      AUTOMATIC_PRESENTATION_DELAY("automatic-presentation-delay", Number.class, "250", null, true),
      COMMUNICATION_PANEL_DISABLE_BREAK_LINE("communication-panel-disable-break-line", Boolean.class, "false", null, true),
      DISPLAY_FONT_NAME_WITH_FONT("display-font-name-with-font", Boolean.class, "true", null, true),
      COMMUNICATION_PANEL_CLOCK_SIZE("communication-panel-clock-size", Number.class, "500", null, true),
      CHAT_TEXT_FONT_SIZE("chat-text-font-size", Number.class, "12", null, true),
      DISPLAY_PREVIOUS_NEXT_BUTTON_FOOTER_CONTROL("display-previous-next-button-footer-control", Boolean.class, "false", null, true),
      READY_MESSAGES_UNIQUE_FILE("ready-messages-unique-file", Boolean.class, "true", null, true);

      public final String key;
      public final Class clazz;
      private final String _default;
      public final String help;
      public final boolean available;

      private EnumProperties(String key, Class clazz, String _default, String help, boolean available) {
         this.key = key;
         this.clazz = clazz;
         this._default = _default;
         this.help = help;
         this.available = available;
      }

      private EnumProperties(String key, String _default, String help, boolean available) {
         this(key, String.class, _default, help, available);
      }

      public boolean is() {
         return Boolean.parseBoolean(MyProperties.get(this.key, this._default));
      }

      public String getString() {
         return MyProperties.get(this.key, this._default);
      }

      public long getLong() {
         return Utils.longOrElse(MyProperties.get(this.key, this._default), Utils.longOrElse(this._default, 0L));
      }

      public long getLongRange(long min, long max) {
         return Utils.range(this.getLong(), min, max);
      }

      public int getInt() {
         return Utils.intOrElse(MyProperties.get(this.key, this._default), Utils.intOrElse(this._default, 0));
      }

      public int getIntRange(int min, int max) {
         return Utils.range(this.getInt(), min, max);
      }

      public void set(Object value) {
         MyProperties.set(this.key, String.valueOf(value));
      }

      public boolean isEditable() {
         return this.help != null;
      }

      protected static JComponent createComponent(MyProperties.EnumProperties en) {
         if (Boolean.class.equals(en.clazz)) {
            WebCheckBox chkbx = new WebCheckBox(en.is());
            chkbx.setFocusable(false);
            return chkbx;
         } else {
            WebTextField tf = new WebTextField();
            tf.setDocument(Number.class.equals(en.clazz) ? SwingUtils.createDocOnlyNumber(6) : SwingUtils.createDocMaxSize(768));
            tf.setText(en.getString());
            tf.setColumns(16);
            tf.setFont(new Font("Arial", 0, 14));
            return tf;
         }
      }

      protected static String getValueFromComponent(JComponent component) {
         if (component instanceof WebCheckBox) {
            return String.valueOf(((WebCheckBox)component).isSelected());
         } else {
            return component instanceof WebTextField ? ((WebTextField)component).getText() : "";
         }
      }

      protected static MyProperties.EnumProperties[] availables() {
         return Stream.of(values()).filter(en -> en.available).toArray(MyProperties.EnumProperties[]::new);
      }
   }
}
