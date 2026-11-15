package com.limagiran.holyrics.edit.event;

import com.alee.laf.rootpane.WebDialog;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.view.panel.SchedulePanelGrid;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public final class EventWorshipForm extends WebDialog {
   private static EventWorshipForm INSTANCE;
   private final EventWorshipPanel event;
   private JLabel jLabelDateTime;
   private JLabel jLabelName;
   private JPanel jPanelForm;
   private JPanel jPanelSchedule;

   private EventWorshipForm(Component owner, EventWorshipPanel event) {
      super(owner);
      this.event = event;
      this.initComponents();
      this.init();
   }

   private EventWorshipForm(Frame owner, EventWorshipPanel event) {
      super(owner, true);
      this.event = event;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.setIconImage(Images.load("event16.png"));
      this.jLabelName.setText(this.event.event.getName());
      this.jLabelDateTime.setText(DateLG.SDF_DT_FS.format(this.event.event.getDate().getTime()));
      InsertShortcut.addEscToDispose(this);
      this.jLabelName.requestFocus();
      this.jPanelSchedule.add(SchedulePanelGrid.createToEvent(this.event.event), "Center");
      this.jPanelForm.requestFocus();
   }

   private void initComponents() {
      this.jPanelForm = new JPanel();
      this.jLabelName = new JLabel();
      this.jLabelDateTime = new JLabel();
      this.jPanelSchedule = new JPanel();
      this.setTitle(I18N.WORD_WORSHIP_EVENT.val());
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(750, 500));
      this.setModal(true);
      this.setResizable(false);
      this.jPanelForm.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelForm.setLayout(new GridBagLayout());
      this.jLabelName.setFont(new Font("Trebuchet MS", 0, 20));
      this.jLabelName.setHorizontalAlignment(0);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 4, 2, 4);
      this.jPanelForm.add(this.jLabelName, gridBagConstraints);
      this.jLabelDateTime.setFont(new Font("Trebuchet MS", 0, 18));
      this.jLabelDateTime.setHorizontalAlignment(0);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelForm.add(this.jLabelDateTime, gridBagConstraints);
      this.getContentPane().add(this.jPanelForm, "First");
      this.jPanelSchedule.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelSchedule.setLayout(new BorderLayout());
      this.getContentPane().add(this.jPanelSchedule, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(Component owner, EventWorshipPanel event) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.setVisible(false);
            INSTANCE.dispose();
         }

         INSTANCE = new EventWorshipForm(owner, event);
         INSTANCE.setVisible(true);
      });
   }
}
