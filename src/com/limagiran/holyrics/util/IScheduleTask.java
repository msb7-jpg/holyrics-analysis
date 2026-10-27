package com.limagiran.holyrics.util;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.i18n.I18N;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.DateEditor;

public interface IScheduleTask extends Runnable {
   String getTitle();

   ImageIcon getIcon();

   default String getDesc() {
      return "";
   }

   default void onCancel() {
   }

   default void schedule() {
      scheduleTask(this);
   }

   default void schedule(Object startAt) {
      scheduleTask(this, startAt);
   }

   static void scheduleTask(IScheduleTask task, int hour, int minute) {
      SwingUtilities.invokeLater(() -> {
         boolean[] hackForceStop = new boolean[]{false};
         final JFrame frame = new JFrame();
         frame.setIconImages(Images.loadIcons("timer", ".png", 16, 24, 32, 48, 64));
         frame.setTitle(I18N.WORD_TASK.val());
         frame.setDefaultCloseOperation(0);
         frame.setLayout(new BorderLayout(0, 4));
         frame.setResizable(false);
         InsertShortcut.add(frame, InsertShortcut.Shortcut.create(27, () -> frame.setState(1)));
         frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
               frame.setState(1);
            }
         });
         WebLabel lblTitle = new WebLabel(task.getTitle(), 0, new Object[0]).setFontSize(24);
         lblTitle.setIcon(task.getIcon());
         WebLabel lblDesc = new WebLabel(task.getDesc());
         WebButton btnCancel = new WebButton(I18N.WORD_CANCEL.val()).setFontSize(16).setMargin(2, 8, 2, 8);
         btnCancel.setFocusable(false);
         btnCancel.addActionListener(evt -> hackForceStop[0] = true);
         WebPanel contentPanel = new WebPanel(new BorderLayout());
         contentPanel.setMargin(8, 16, 8, 16);
         contentPanel.add(new WebPanel(new FlowLayout(), new Component[]{lblTitle}).setMargin(2, 32, 0, 32), "First");
         contentPanel.add(new WebPanel(new FlowLayout(), new Component[]{lblDesc}).setMargin(0, 32, 2, 32), "Center");
         contentPanel.add(new WebPanel(new FlowLayout(), new Component[]{btnCancel}).setMargin(0, 32, 2, 32), "Last");
         WebLabel countdownLabel = new WebLabel("00:00:00  " + I18N.MSG_TO_START_THE_TASK.val().toLowerCase(), 0, new Object[0]);
         countdownLabel.setFontName("Arial");
         countdownLabel.setFontSizeAndStyle(14, 1);
         countdownLabel.setMargin(8, 16, 0, 16);
         Consumer<Long> updateCountdownLabel = time -> SwingUtilities.invokeLater(() -> {
               countdownLabel.setText(CountdownUtils.formatTime(time, true) + "  " + I18N.MSG_TO_START_THE_TASK.val().toLowerCase() + ": ");
               countdownLabel.repaint();
            });
         frame.add(countdownLabel, "First");
         frame.add(contentPanel, "Center");
         frame.pack();
         frame.setLocationRelativeTo(null);
         frame.setVisible(true);
         Utils.startThreadDaemon(() -> {
            try {
               while (true) {
                  long sec = CountdownUtils.getSecondsExact(hour, minute);
                  if (sec < 0L || sec > 86000L || hackForceStop[0]) {
                     return;
                  }

                  updateCountdownLabel.accept(sec);
                  Utils.sleep(250L);
               }
            } finally {
               SwingUtilities.invokeLater(() -> {
                  frame.setVisible(false);
                  frame.dispose();
                  frame.removeAll();
                  frame.getContentPane().removeAll();
                  Utils.startThreadDaemon(() -> {
                     if (!hackForceStop[0]) {
                        task.run();
                     } else {
                        task.onCancel();
                     }
                  });
               });
            }
         });
      });
   }

   static void scheduleTask(IScheduleTask schedule, Object startAt) {
      Calendar c;
      if (startAt instanceof Calendar) {
         c = (Calendar)startAt;
      } else {
         if (!(startAt instanceof Date)) {
            return;
         }

         c = Calendar.getInstance();
         c.setTime((Date)startAt);
      }

      scheduleTask(schedule, c.get(11), c.get(12));
   }

   static void scheduleTask(IScheduleTask schedule) {
      SwingUtilities.invokeLater(() -> {
         WebSpinner spinnerTime = new WebSpinner();
         spinnerTime.setFont(new Font("Courier New", 0, 14));
         spinnerTime.setModel(new SpinnerDateModel(new Date(), null, null, 12));
         spinnerTime.setEditor(new DateEditor(spinnerTime, "HH:mm"));
         int op = JOptionPane.showConfirmDialog(null, new Object[]{I18N.WORD_START.val() + ": ", spinnerTime}, I18N.WORD_TASK.val(), 2, -1);
         if (op == 0) {
            SwingUtils.commitEdit(spinnerTime);
            scheduleTask(schedule, spinnerTime.getValue());
         }
      });
   }

   static void setupPopupMenu(JPopupMenu popup, IScheduleTask task) {
      if (task != null) {
         JMenuItem schedule = new JMenuItem(I18N.MSG_TO_SCHEDULE_TASK.val(), new ImageIcon(Images.load("timer16.png")));
         schedule.addActionListener(evt -> task.schedule());
         popup.add(schedule);
      }
   }
}
