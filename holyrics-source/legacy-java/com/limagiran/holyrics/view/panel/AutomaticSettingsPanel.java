package com.limagiran.holyrics.view.panel;

import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.util.Utils;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class AutomaticSettingsPanel extends JPanel {
   private final AutomaticSettingsPanel.AutomaticSettings set;
   private final Runnable onChange;
   private JCheckBox jCheckBoxAutomaticSlideShow;
   private JCheckBox jCheckBoxRepeat;
   private JLabel jLabelSeconds;
   private JSpinner jSpinnerSeconds;

   public AutomaticSettingsPanel(AutomaticSettingsPanel.AutomaticSettings set, Runnable onChange) {
      this.set = set;
      this.onChange = onChange;
      this.set.setOnChange(this.onChange);
      this.initComponents();
      this.init();
   }

   private void init() {
      ((WebSpinner)this.jSpinnerSeconds).setDrawFocus(false);
      this.jCheckBoxAutomaticSlideShow.setSelected(this.set.isAutomatic());
      this.jCheckBoxRepeat.setSelected(this.set.isRepeat());
      this.jSpinnerSeconds.setValue(this.set.getDuration());
      this.setEnabledSubItemsAutomaticSlideShow(this.set.isAutomatic());
      this.jCheckBoxAutomaticSlideShow
         .addItemListener(e -> this.setEnabledSubItemsAutomaticSlideShow(this.set.setAutomatic(e.getStateChange() == 1).isAutomatic()));
      this.jCheckBoxRepeat.addItemListener(e -> this.set.setRepeat(e.getStateChange() == 1));
      this.jSpinnerSeconds.addChangeListener(e -> this.set.setDuration(Utils.intOrElse(this.jSpinnerSeconds.getValue(), 7)));
   }

   public void commit() {
      try {
         this.jSpinnerSeconds.commitEdit();
      } catch (Exception var2) {
      }
   }

   private void initComponents() {
      this.jCheckBoxAutomaticSlideShow = new JCheckBox();
      this.jSpinnerSeconds = new WebSpinner();
      this.jLabelSeconds = new JLabel();
      this.jCheckBoxRepeat = new JCheckBox();
      this.setLayout(new GridBagLayout());
      this.jCheckBoxAutomaticSlideShow.setFont(new Font("Georgia", 0, 14));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jCheckBoxAutomaticSlideShow.setText(bundle.getString("msg.automaticSlideShow"));
      this.jCheckBoxAutomaticSlideShow.setFocusable(false);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 17;
      this.add(this.jCheckBoxAutomaticSlideShow, gridBagConstraints);
      this.jSpinnerSeconds.setFont(new Font("Georgia", 0, 14));
      this.jSpinnerSeconds.setModel(new SpinnerNumberModel(2, 2, 60, 1));
      this.jSpinnerSeconds.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.add(this.jSpinnerSeconds, gridBagConstraints);
      this.jLabelSeconds.setFont(new Font("Georgia", 0, 14));
      this.jLabelSeconds.setText(bundle.getString("word.seconds"));
      this.jLabelSeconds.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 5, 0, 0);
      this.add(this.jLabelSeconds, gridBagConstraints);
      this.jCheckBoxRepeat.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxRepeat.setText(bundle.getString("word.repeat"));
      this.jCheckBoxRepeat.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 3;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 17;
      gridBagConstraints.insets = new Insets(0, 25, 0, 0);
      this.add(this.jCheckBoxRepeat, gridBagConstraints);
   }

   private void setEnabledSubItemsAutomaticSlideShow(boolean flag) {
      Arrays.asList(this.jCheckBoxRepeat, this.jSpinnerSeconds, this.jLabelSeconds).forEach(c -> c.setEnabled(flag));
   }

   public static class AutomaticSettings implements Serializable {
      private static final long serialVersionUID = 1L;
      public static final String KEY_IMAGE = "image";
      public static final String KEY_ANNOUNCEMENT = "announcement";
      public static final String KEY_MEDIA_IMAGE = "media-image";
      private transient Runnable onChange;
      private boolean automatic;
      private boolean repeat;
      private int duration;
      private long version = 1L;

      public AutomaticSettings(boolean automatic, boolean repeat, int duration) {
         this.automatic = automatic;
         this.repeat = repeat;
         this.duration = duration;
      }

      public AutomaticSettings() {
         this(true, true, 7);
      }

      private void fireChange() {
         Optional.ofNullable(this.onChange).ifPresent(Runnable::run);
      }

      public Runnable getOnChange() {
         return this.onChange;
      }

      public AutomaticSettingsPanel.AutomaticSettings setOnChange(Runnable onChange) {
         this.onChange = onChange;
         return this;
      }

      public boolean isAutomatic() {
         return this.automatic;
      }

      public AutomaticSettingsPanel.AutomaticSettings setAutomatic(boolean automatic) {
         this.automatic = automatic;
         this.fireChange();
         return this;
      }

      public boolean isRepeat() {
         return this.repeat;
      }

      public AutomaticSettingsPanel.AutomaticSettings setRepeat(boolean repeat) {
         this.repeat = repeat;
         this.fireChange();
         return this;
      }

      public int getDuration() {
         return Utils.range(this.duration, 2, 30);
      }

      public AutomaticSettingsPanel.AutomaticSettings setDuration(int duration) {
         this.duration = duration;
         this.fireChange();
         return this;
      }

      private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
         ois.defaultReadObject();
      }
   }
}
