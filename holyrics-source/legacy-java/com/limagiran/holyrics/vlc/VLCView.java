package com.limagiran.holyrics.vlc;

import com.limagiran.holyrics.model.ScreenDeviceSettings;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.VideoUtils;
import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class VLCView extends JDialog implements Values {
   private final Panel embed;

   protected VLCView(Panel embed) {
      this.embed = embed;
      this.init();
   }

   private void init() {
      this.setUndecorated(true);
      this.setDefaultCloseOperation(0);
      this.setOpacity(0.0F);
      this.setLayout(new BorderLayout());
      this.add(this.embed, "Center");
      this.setAlwaysOnTop(true);
      this.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::stop));
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(115, 512, this::stop));
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(32, this::playPause));
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(10, this::playPause));
      Arrays.asList(this, this.embed).forEach(c -> c.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
               if (Utils.isDoubleClick(e) && VLC.ENABLED) {
                  VLC.PLAYER_CONTROL.playPause();
               }
            }
         }));
      this.setOpacity(0.0F);
      super.setVisible(true);
   }

   private void playPause() {
      if (VLC.ENABLED) {
         VLC.PLAYER_CONTROL.playPause();
      }
   }

   private void stop() {
      if (VLC.ENABLED) {
         VLC.PLAYER_CONTROL.stop();
      }
   }

   @Override
   public void setVisible(boolean flag) {
      SwingUtilities.invokeLater(() -> {
         if (flag) {
            if (!VideoUtils.isSingleMonitor()) {
               this.setFocusable(false);
               this.setFocusableWindowState(false);
            }

            this.setupDevice();
            this.setOpacity(1.0F);
            super.setVisible(true);
            this.toFront();
            this.setFocusable(true);
            this.setFocusableWindowState(true);
         } else {
            this.setOpacity(0.0F);
            Rectangle r = this.getBounds();
            this.setBounds(r.x, r.y, 1, 1);
            this.revalidate();
            VideoUtils.requestFocusInMainIfSingleMonitor();
         }

         this.repaint();
      });
   }

   public void setupDevice() {
      Rectangle size = VideoUtils.getDeviceToPresentation(ScreenDeviceSettings.EnumDeviceType.MEDIA).getBounds();
      this.setBounds(size);
   }

   @Override
   public void dispose() {
      this.setVisible(false);
   }
}
