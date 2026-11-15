package com.limagiran.holyrics.edit.event;

import com.alee.extended.date.WebDateField;
import com.alee.laf.rootpane.WebDialog;
import com.limagiran.holyrics.control.EventController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.lgsaveopen.Utils;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.JSpinner.DateEditor;

public final class EventFormCreate extends WebDialog {
   private final WebDateField webDateField = new WebDateField(new Date());
   private final EventPanel event;
   private JButton jButtonExit;
   private JButton jButtonSave;
   private JPanel jPanelButtons;
   private JPanel jPanelDateTime;
   private JPanel jPanelForm;
   private JScrollPane jScrollPaneDesc;
   private JSpinner jSpinnerEventTime;
   private JTextArea jTextAreaDesc;
   private JTextField jTextFieldName;

   private EventFormCreate(Component owner, EventPanel event) {
      super(owner);
      this.event = event;
      this.initComponents();
      this.init();
   }

   private EventFormCreate(Frame owner, EventPanel event) {
      super(owner, true);
      this.event = event;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.setIconImage(Images.load("event16.png"));
      this.jTextFieldName.setDocument(SwingUtils.createDocMaxSize(25));
      this.jTextAreaDesc.setDocument(SwingUtils.createDocMaxSize(2048));
      this.webDateField.setHorizontalAlignment(0);
      this.webDateField.setCalendarCustomizer(calendar -> calendar.setStartWeekFromSunday(true));
      this.jPanelDateTime.add(this.webDateField, 0);
      this.jButtonExit.addActionListener(e -> this.exit());
      this.jButtonSave.addActionListener(e -> this.create());
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            EventFormCreate.this.exit();
         }
      });
      this.jSpinnerEventTime.setValue(this.event.event.getDate().getTime());
      this.webDateField.setDate(this.event.event.getDate().getTime());
      this.webDateField.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            super.keyPressed(e);
            BooleanOptional.runIfTrue(27 == e.getKeyCode(), () -> EventFormCreate.this.exit());
         }
      });
      InsertShortcut.addEscToDispose(this);
      this.jTextFieldName.requestFocus();
      this.jPanelForm.requestFocus();
   }

   private void initComponents() {
      this.jPanelForm = new JPanel();
      this.jTextFieldName = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_NAME.val());
      this.jPanelDateTime = new JPanel();
      this.jSpinnerEventTime = new JSpinner();
      this.jScrollPaneDesc = new JScrollPane();
      this.jTextAreaDesc = SwingUtils.createJTextAreaPlaceHolder(I18N.WORD_DESCRIPTION.val());
      this.jPanelButtons = new JPanel();
      this.jButtonExit = new JButton();
      this.jButtonSave = new JButton();
      this.setDefaultCloseOperation(0);
      this.setTitle(I18N.WORD_EVENT.val());
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(500, 450));
      this.setModal(true);
      this.setResizable(false);
      this.jPanelForm.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelForm.setLayout(new GridBagLayout());
      this.jTextFieldName.setFont(new Font("Monospaced", 0, 14));
      this.jTextFieldName.setHorizontalAlignment(0);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelForm.add(this.jTextFieldName, gridBagConstraints);
      this.jSpinnerEventTime.setFont(new Font("Georgia", 0, 14));
      this.jSpinnerEventTime.setModel(new SpinnerDateModel());
      this.jSpinnerEventTime.setEditor(new DateEditor(this.jSpinnerEventTime, "HH:mm"));
      this.jPanelDateTime.add(this.jSpinnerEventTime);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.gridwidth = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelForm.add(this.jPanelDateTime, gridBagConstraints);
      this.jTextAreaDesc.setFont(new Font("Monospaced", 0, 14));
      this.jTextAreaDesc.setLineWrap(true);
      this.jTextAreaDesc.setWrapStyleWord(true);
      this.jScrollPaneDesc.setViewportView(this.jTextAreaDesc);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanelForm.add(this.jScrollPaneDesc, gridBagConstraints);
      this.jPanelButtons.setLayout(new FlowLayout(1, 15, 5));
      this.jButtonExit.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/close24.png")));
      this.jButtonExit.setFocusable(false);
      this.jButtonExit.setMargin(new Insets(2, 2, 2, 2));
      this.jPanelButtons.add(this.jButtonExit);
      this.jButtonSave.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/saveBlack24.png")));
      this.jButtonSave.setFocusable(false);
      this.jButtonSave.setMargin(new Insets(2, 2, 2, 2));
      this.jPanelButtons.add(this.jButtonSave);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelForm.add(this.jPanelButtons, gridBagConstraints);
      this.getContentPane().add(this.jPanelForm, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(Component owner, EventPanel event) {
      SwingUtilities.invokeLater(() -> new EventFormCreate(owner, event).setVisible(true));
   }

   private void create() {
      try {
         this.jSpinnerEventTime.commitEdit();
         this.webDateField.updateDateFromField(true);
      } catch (Exception var10) {
      }

      try {
         this.event.event.setName(this.jTextFieldName.getText());
         Calendar date = Calendar.getInstance();
         date.setTime(this.webDateField.getDate());
         Calendar time = Calendar.getInstance();
         time.setTime((Date)this.jSpinnerEventTime.getValue());
         Calendar _new = Calendar.getInstance();
         _new.set(date.get(1), date.get(2), date.get(5), time.get(11), time.get(12));
         this.event.event.setDate(_new);
         this.event.event.setDesc(this.jTextAreaDesc.getText());
         EventController.add(this.event.event);
         this.event.fireUpdate();
      } catch (Exception var8) {
         Utils.error(this, I18N.MSG_ERROR_TRY_AGAIN.val());
      } finally {
         this.dispose();
      }
   }

   private void exit() {
      super.dispose();
   }
}
