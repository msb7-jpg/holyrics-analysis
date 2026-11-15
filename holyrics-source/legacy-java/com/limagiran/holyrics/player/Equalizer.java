package com.limagiran.holyrics.player;

import com.alee.extended.window.WebPopOver;
import com.alee.laf.slider.WebSlider;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.InsertShortcut;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.MediaPlayer;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Equalizer {
   private static final int[] FREQUENCY = new int[]{32, 64, 125, 250, 500, 1000, 2000, 4000, 8000, 16000};
   private static final int[] BAND = new int[]{19, 39, 78, 156, 312, 625, 1250, 2500, 5000, 10000};

   public static void showEqualizer(MediaPlayer player, AudioPlayerSettings set, Component c) {
      List<EqualizerBand> bands;
      if (player != null) {
         bands = player.getAudioEqualizer().getBands();
      } else {
         bands = new ArrayList<>();

         for (int i = 0; i < 10; i++) {
            bands.add(new EqualizerBand((double)FREQUENCY[i], (double)BAND[i], (double)set.getEqualizer(FREQUENCY[i])));
         }
      }

      JComboBox<Equalizer.EnumEqualizer> cmbbx = new JComboBox<>(Equalizer.EnumEqualizer.values());
      cmbbx.setSelectedItem(set.getEnumEqualizer());
      cmbbx.setFocusable(false);
      cmbbx.setMaximumRowCount(15);
      cmbbx.addItemListener(e -> BooleanOptional.runIfTrue(e.getStateChange() == 1, () -> set.setEnumEqualizer((Equalizer.EnumEqualizer)e.getItem())));
      JCheckBox chkbx = new JCheckBox(I18N.WORD_ACTIVATE.val());
      chkbx.setSelected(set.isEqualization());
      chkbx.setFocusable(false);
      chkbx.addItemListener(e -> {
         set.setEqualization(e.getStateChange() == 1);
         if (player != null) {
            player.getAudioEqualizer().setEnabled(set.isEqualization());
         }
      });
      JPanel top = new JPanel(new FlowLayout(1, 25, 0));
      top.add(cmbbx);
      top.add(chkbx);
      JPanel sliders = new JPanel(new GridLayout(1, FREQUENCY.length, 4, 0));
      sliders.setOpaque(false);

      for (int i = 0; i < bands.size(); i++) {
         int iFinal = i;
         EqualizerBand b = bands.get(iFinal);
         WebSlider slider = new WebSlider(1, -12, 12, (int)b.getGain());
         slider.setMinorTickSpacing(1);
         slider.setMajorTickSpacing(12);
         slider.setFocusable(false);
         slider.setPaintTicks(true);
         slider.addChangeListener(e -> {
            if (!slider.getValueIsAdjusting()) {
               b.setGain((double)slider.getValue());
               set.setEqualizer((int)b.getCenterFrequency(), slider.getValue());
            }
         });
         cmbbx.addItemListener(e -> {
            if (e.getStateChange() == 1 && e.getItem() instanceof Equalizer.EnumEqualizer) {
               slider.setValue(((Equalizer.EnumEqualizer)e.getItem()).getValue(iFinal));
            }
         });
         JPanel col = new JPanel(new BorderLayout(0, 6));
         col.setOpaque(false);
         col.add(new JLabel(String.valueOf((int)b.getCenterFrequency()), 0), "First");
         col.add(slider, "Center");
         sliders.add(col);
      }

      WebPopOver popOver = new WebPopOver();
      popOver.setDefaultCloseOperation(2);
      popOver.setCloseOnFocusLoss(true);
      popOver.setMargin(10);
      popOver.setLayout(new BorderLayout());
      JPanel panel = new JPanel(new BorderLayout(8, 16));
      panel.add(top, "First");
      panel.add(sliders, "Center");
      popOver.add(panel);
      InsertShortcut.add(popOver, InsertShortcut.Shortcut.create(27, popOver::dispose));
      popOver.show(c);
   }

   public static enum EnumEqualizer implements Serializable {
      DEFAULT(I18N.WORD_DEFAULT.val(), new int[]{50, 50, 50, 50, 50, 50, 50, 50, 50, 50}),
      CLASSICAL("Classical", new int[]{50, 50, 50, 50, 50, 50, 70, 70, 70, 76}),
      CLUB("Club", new int[]{50, 50, 42, 34, 34, 34, 42, 50, 50, 50}),
      DANCE("Dance", new int[]{26, 34, 46, 50, 50, 66, 70, 70, 50, 50}),
      FULLBASS("Full Bass", new int[]{26, 26, 26, 36, 46, 62, 76, 78, 78, 78}),
      FULLBASSTREBLE("Full Bass Treble", new int[]{34, 34, 50, 68, 62, 46, 28, 22, 18, 18}),
      FULLTREBLE("Full Treble", new int[]{78, 78, 78, 62, 42, 24, 8, 8, 8, 8}),
      LAPTOP("Laptop", new int[]{38, 22, 36, 60, 58, 46, 38, 24, 16, 14}),
      LIVE("Live", new int[]{66, 50, 40, 36, 34, 34, 40, 42, 42, 42}),
      PARTY("Party", new int[]{32, 32, 50, 50, 50, 50, 50, 50, 32, 32}),
      POP("Pop", new int[]{56, 38, 32, 30, 38, 54, 56, 56, 54, 54}),
      REGGAE("Reggae", new int[]{48, 48, 50, 66, 48, 34, 34, 48, 48, 48}),
      ROCK("Rock", new int[]{32, 38, 64, 72, 56, 40, 28, 24, 24, 24}),
      TECHNO("Techno", new int[]{30, 34, 48, 66, 64, 48, 30, 24, 24, 28});

      private static final long serialVersionUID = 1L;
      private final int[] values;
      public final String name;
      private long version = 1L;

      private EnumEqualizer(String name, int[] values) {
         this.name = name;
         this.values = values;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public int[] getValues() {
         int[] _return = new int[this.values.length];

         for (int i = 0; i < _return.length; i++) {
            _return[i] = -1 * (int)(24.0 * ((double)this.values[i] / 100.0) - 12.0);
         }

         return _return;
      }

      private int getValue(int i) {
         try {
            return -1 * (int)(24.0 * ((double)this.values[i] / 100.0) - 12.0);
         } catch (Exception var3) {
            return 0;
         }
      }

      private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
         ois.defaultReadObject();
         this.version = 1L;
      }
   }
}
