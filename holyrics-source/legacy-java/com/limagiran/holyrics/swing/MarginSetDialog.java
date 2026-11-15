package com.limagiran.holyrics.swing;

import com.alee.extended.window.PopOverDirection;
import com.alee.extended.window.WebPopOver;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MarginSetDialog extends WebPopOver {
   private final MarginSetDialog.MarginSetListener listener;

   private MarginSetDialog(Component parent, MarginSetDialog.MarginSetListener listener) {
      super(parent);
      this.listener = listener;
      this.init();
   }

   private void init() {
      this.setLayout(new BorderLayout());
      this.setMovable(false);
      this.setPreferredDirection(PopOverDirection.right);
      InsertShortcut.addEscToDispose(this);
      this.setCloseOnFocusLoss(true);
      this.add(createContent(this.listener, false), "Center");
   }

   public static void main(Component parent, Component show, MarginSetDialog.MarginSetListener listener) {
      new MarginSetDialog(parent, listener).show(show);
   }

   public static Component createContent(MarginSetDialog.MarginSetListener listener, boolean small) {
      WebPanel _return = new WebPanel(new BorderLayout(0, 16));
      WebSpinner[] spinners = new WebSpinner[4];
      IntStream.of(0, 1, 2, 3)
         .forEach(
            i -> {
               spinners[i] = new WebSpinner(
                  new SpinnerNumberModel(
                     listener.getMargins()[i], listener.getMinMaxAndStepValue()[0], listener.getMinMaxAndStepValue()[1], listener.getMinMaxAndStepValue()[2]
                  )
               );
               spinners[i].addChangeListener(new ChangeListener() {
                  private long timeRun = -1L;
                  private long delay = 150L;
                  private final Runnable runnable = () -> {
                     if (this.timeRun < 0L) {
                        for (this.timeRun = this.delay; this.timeRun > 0L; this.timeRun -= 50L) {
                           Utils.sleep(50L);
                        }

                        this.timeRun = -1L;
                        listener.getMargins()[i] = Integer.parseInt(spinners[i].getValue().toString());
                        listener.changed();
                     } else {
                        this.timeRun = this.delay;
                     }
                  };

                  @Override
                  public void stateChanged(ChangeEvent e) {
                     Utils.startThreadDaemon(this.runnable);
                  }
               });
            }
         );
      WebPanel grid = new WebPanel(new GridBagLayout());
      grid.setOpaque(false);
      Consumer<int[]> addSpinner = arrayx -> {
         GridBagConstraints gbcx = new GridBagConstraints();
         gbcx.gridx = arrayx[1];
         gbcx.gridy = arrayx[2];
         if (small) {
            if (arrayx[0] == 0) {
               gbcx.insets.bottom = -10;
            } else if (arrayx[0] == 2) {
               gbcx.insets.top = -10;
            }
         }

         grid.add(spinners[arrayx[0]], gbcx);
      };
      int[][] array = new int[][]{{0, 1, 0}, {3, 0, 1}, {1, 2, 1}, {2, 1, 2}};
      Arrays.asList(array).forEach(addSpinner::accept);
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 1;
      gbc.gridy = 1;
      gbc.ipadx = gbc.ipady = 24;
      grid.add(new JLabel(new ImageIcon(Images.load("margin" + (small ? 16 : 48) + ".png"))), gbc);
      if (!small) {
         _return.setMargin(8, 16, 8, 16);
      }

      _return.setLayout(new BorderLayout(0, 16));
      if (!small) {
         JLabel label = new JLabel(I18N.WORD_MARGIN.val(), 0);
         label.setFont(label.getFont().deriveFont(1, 14.0F));
         _return.add(label, "First");
      }

      _return.add(grid, "Center");
      return _return;
   }

   public interface MarginSetListener {
      int TOP = 0;
      int RIGHT = 1;
      int BOTTOM = 2;
      int LEFT = 3;

      void changed();

      int[] getMargins();

      int[] getMinMaxAndStepValue();
   }
}
