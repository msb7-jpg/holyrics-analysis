package com.limagiran.holyrics.edit;

import com.alee.laf.panel.WebPanel;
import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.control.WorshipController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Worship;
import com.limagiran.holyrics.swing.ListCellRendererWorship;
import com.limagiran.holyrics.swing.ListModelWorship;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.EnumWorship;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Log;
import com.limagiran.holyrics.util.PopUpMenu;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.util.WrapLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class EditWorship extends JPanel implements IEditView {
   private final ListModelWorship listModelWorship = new ListModelWorship();
   private final ListCellRendererWorship listCellRendererWorship = new ListCellRendererWorship();
   private JButton jButtonWorshipAdd;
   private JComboBox<EnumWorship.EnumWorshipDays> jComboBoxWorshipDay;
   private JComboBox<EnumWorship.EnumWorshipWeeks> jComboBoxWorshipWeek;
   private JLabel jLabelTwoPoints;
   private JLabel jLabelWorshipDay;
   private JLabel jLabelWorshipTime;
   private JList<Worship> jListWorship;
   private JPanel jPanelWorshipDay;
   private JPanel jPanelWorshipSettings;
   private JPanel jPanelWorshipTime;
   private JScrollPane jScrollPaneWorship;
   private JSpinner jSpinnerWorshipHour;
   private JSpinner jSpinnerWorshipMinute;
   private JTextField jTextFieldWorshipDescription;

   public EditWorship() {
      this.initComponents();
      this.jTextFieldWorshipDescription.setDocument(SwingUtils.createDocMaxSize(24));
      this.clearWorshipFields();
      this.init();
   }

   private void init() {
      this.jButtonWorshipAdd.addActionListener(e -> this.addWorship());
      WebLafUtils.setupWebLafLayout(new WrapLayout(1, 0, 0), this.jPanelWorshipSettings);
      SwingUtils.setJPanelToOpaqueFalse(this);
   }

   private void initComponents() {
      this.jPanelWorshipSettings = new WebPanel(true);
      this.jPanelWorshipDay = new JPanel();
      this.jLabelWorshipDay = new JLabel();
      this.jComboBoxWorshipWeek = new JComboBox<>();
      this.jComboBoxWorshipDay = new JComboBox<>();
      this.jPanelWorshipTime = new JPanel();
      this.jLabelWorshipTime = new JLabel();
      this.jSpinnerWorshipHour = new WebSpinner();
      this.jLabelTwoPoints = new JLabel();
      this.jSpinnerWorshipMinute = new WebSpinner();
      this.jTextFieldWorshipDescription = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_DESCRIPTION.val(), StyleUI.TOOL_TIP_TEXT_COLOR);
      this.jButtonWorshipAdd = new JButton();
      this.jScrollPaneWorship = new JScrollPane();
      this.jListWorship = new JList<>();
      this.setBorder(BorderFactory.createEtchedBorder());
      this.setMaximumSize(new Dimension(775, 392));
      this.setMinimumSize(new Dimension(775, 392));
      this.setName(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.worship")));
      this.setPreferredSize(new Dimension(775, 392));
      this.setLayout(new BorderLayout(0, 5));
      this.jPanelWorshipSettings.setBorder(BorderFactory.createEtchedBorder());
      this.jPanelWorshipSettings.setLayout(new FlowLayout(1, 5, 0));
      this.jLabelWorshipDay.setFont(new Font("Georgia", 0, 12));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jLabelWorshipDay.setText(bundle.getString("SettingsView.jLabelWorshipDay.text"));
      this.jPanelWorshipDay.add(this.jLabelWorshipDay);
      this.jComboBoxWorshipWeek.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxWorshipWeek.setModel(new DefaultComboBoxModel<>(EnumWorship.EnumWorshipWeeks.values()));
      this.jPanelWorshipDay.add(this.jComboBoxWorshipWeek);
      this.jComboBoxWorshipDay.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxWorshipDay.setModel(new DefaultComboBoxModel<>(EnumWorship.EnumWorshipDays.values()));
      this.jPanelWorshipDay.add(this.jComboBoxWorshipDay);
      this.jPanelWorshipSettings.add(this.jPanelWorshipDay);
      this.jLabelWorshipTime.setFont(new Font("Georgia", 0, 12));
      this.jLabelWorshipTime.setText(bundle.getString("SettingsView.jLabelWorshipTime.text"));
      this.jPanelWorshipTime.add(this.jLabelWorshipTime);
      this.jSpinnerWorshipHour.setFont(new Font("Georgia", 0, 12));
      this.jSpinnerWorshipHour.setModel(new SpinnerNumberModel(19, 0, 23, 1));
      this.jPanelWorshipTime.add(this.jSpinnerWorshipHour);
      this.jLabelTwoPoints.setFont(new Font("Georgia", 0, 14));
      this.jLabelTwoPoints.setText(":");
      this.jPanelWorshipTime.add(this.jLabelTwoPoints);
      this.jSpinnerWorshipMinute.setFont(new Font("Georgia", 0, 12));
      this.jSpinnerWorshipMinute.setModel(new SpinnerNumberModel(0, 0, 55, 5));
      this.jPanelWorshipTime.add(this.jSpinnerWorshipMinute);
      this.jPanelWorshipSettings.add(this.jPanelWorshipTime);
      this.jTextFieldWorshipDescription.setColumns(24);
      this.jTextFieldWorshipDescription.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldWorshipDescription.setHorizontalAlignment(0);
      this.jTextFieldWorshipDescription.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            EditWorship.this.jTextFieldWorshipDescriptionKeyReleased(evt);
         }
      });
      this.jPanelWorshipSettings.add(this.jTextFieldWorshipDescription);
      this.jButtonWorshipAdd.setFont(new Font("Georgia", 0, 11));
      this.jButtonWorshipAdd.setText(bundle.getString("word.add"));
      this.jPanelWorshipSettings.add(this.jButtonWorshipAdd);
      this.add(this.jPanelWorshipSettings, "First");
      this.jListWorship.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
      this.jListWorship.setFont(new Font("Georgia", 1, 16));
      this.jListWorship.setModel(this.listModelWorship);
      this.jListWorship.setSelectionMode(0);
      this.jListWorship
         .setToolTipText(
            MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.ttDoubleClickToEditAndRightToMoreOp"))
         );
      this.jListWorship.setCellRenderer(this.listCellRendererWorship);
      this.jListWorship.setFixedCellHeight(40);
      this.jListWorship.setVisibleRowCount(-1);
      this.jListWorship.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent evt) {
            EditWorship.this.jListWorshipMousePressed(evt);
         }
      });
      this.jScrollPaneWorship.setViewportView(this.jListWorship);
      this.add(this.jScrollPaneWorship, "Center");
   }

   private void jTextFieldWorshipDescriptionKeyReleased(KeyEvent evt) {
      BooleanOptional.runIfTrue(evt.getKeyCode() == 10, this::addWorship);
   }

   private void jListWorshipMousePressed(MouseEvent evt) {
      if (SwingUtilities.isRightMouseButton(evt)) {
         JList list = (JList)evt.getSource();
         int i = list.locationToIndex(evt.getPoint());
         if (i != -1 && !Arrays.stream(list.getSelectedIndices()).anyMatch(x -> x == i)) {
            list.setSelectedIndex(list.locationToIndex(evt.getPoint()));
         }

         PopUpMenu.settingsPopUpWorship(this, this.jListWorship, evt);
      }

      if (Utils.isDoubleClick(evt)) {
         Worship w = this.jListWorship.getSelectedValue();
         if (w == null) {
            return;
         }

         if (w.isActive()) {
            this.editWorship(w);
         } else if (Utils.confirm(this, I18N.WORD_ACTIVATE.val() + " " + I18N.WORD_WORSHIP_EVENT.val() + "?")) {
            w.setActive(true);
            WorshipController.save();
            this.update();
         }
      }
   }

   private void addWorship() {
      try {
         Worship w = new Worship(
            (EnumWorship.EnumWorshipDays)this.jComboBoxWorshipDay.getSelectedItem(),
            (EnumWorship.EnumWorshipWeeks)this.jComboBoxWorshipWeek.getSelectedItem(),
            Integer.parseInt(this.jSpinnerWorshipHour.getValue().toString()),
            Integer.parseInt(this.jSpinnerWorshipMinute.getValue().toString()),
            this.jTextFieldWorshipDescription.getText()
         );
         BooleanOptional.runIfTrue(!WorshipController.add(w), () -> Utils.error(this, I18N.MSG_ALREADY_REGISTERED.val()));
         this.listModelWorship.update();
         this.clearWorshipFields();
      } catch (Exception var2) {
         Log.send(var2);
         Utils.error(this, I18N.MSG_ERROR_TRY_AGAIN.val());
      }
   }

   public void removeWorship(Worship w) {
      if (w != null && Utils.confirm(this, I18N.MSG_CONFIRM_REMOVE_WORSHIP.format(w))) {
         WorshipController.remove(w);
         this.listModelWorship.update();
      }
   }

   public void editWorship(Worship worship) {
      if (worship != null) {
         EditWorship.WorshipEditView.main(worship);
         this.listModelWorship.update();
      }
   }

   public void update() {
      this.listModelWorship.update();
   }

   private void clearWorshipFields() {
      this.jComboBoxWorshipDay.setSelectedIndex(0);
      this.jComboBoxWorshipWeek.setSelectedItem(EnumWorship.EnumWorshipWeeks.ALL);
      this.jSpinnerWorshipHour.setValue(19);
      this.jSpinnerWorshipMinute.setValue(0);
      this.jTextFieldWorshipDescription.setText("");
   }

   @Override
   public boolean exit() {
      this.clearWorshipFields();
      return true;
   }

   @Override
   public String getTitle() {
      return I18N.WORD_WORSHIP_EVENT.val();
   }

   @Override
   public JPanel getPanel() {
      return this;
   }

   @Override
   public ImageIcon getIcon() {
      return new ImageIcon(Images.load("event16.png"));
   }

   private static class WorshipEditView extends JPanel {
      private final Worship w;
      private final JComboBox<EnumWorship.EnumWorshipDays> jComboBoxWorshipDay = new JComboBox<>();
      private final JComboBox<EnumWorship.EnumWorshipWeeks> jComboBoxWorshipWeek = new JComboBox<>();
      private final JLabel jLabelTwoPoints = new JLabel();
      private final JLabel jLabelWorshipDay = new JLabel();
      private final JLabel jLabelWorshipTime = new JLabel();
      private final JPanel jPanelWorshipDay = new JPanel();
      private final JPanel jPanelWorshipSettings = new JPanel();
      private final JPanel jPanelWorshipTime = new JPanel();
      private final JSpinner jSpinnerWorshipHour = new JSpinner();
      private final JSpinner jSpinnerWorshipMinute = new JSpinner();
      private final JTextField jTextFieldWorshipDescription = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_DESCRIPTION.val(), StyleUI.TOOL_TIP_TEXT_COLOR);
      private final JCheckBox jCheckBoxKeepSchedules = new JCheckBox();

      private WorshipEditView(Worship w) {
         super(new BorderLayout());
         this.w = w;
         this.init();
      }

      private void init() {
         Font f = new Font("Georgia", 0, 12);
         this.setBorder(BorderFactory.createEtchedBorder());
         this.jPanelWorshipSettings.setBorder(BorderFactory.createEtchedBorder());
         this.jPanelWorshipSettings.setLayout(new FlowLayout(1, 5, 0));
         this.jLabelWorshipDay.setFont(f);
         ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
         this.jLabelWorshipDay.setText(bundle.getString("SettingsView.jLabelWorshipDay.text"));
         this.jPanelWorshipDay.add(this.jLabelWorshipDay);
         this.jComboBoxWorshipWeek.setFont(f);
         this.jComboBoxWorshipWeek.setModel(new DefaultComboBoxModel<>(EnumWorship.EnumWorshipWeeks.values()));
         this.jComboBoxWorshipWeek.setSelectedItem(this.w.week);
         this.jComboBoxWorshipWeek.setEnabled(false);
         this.jPanelWorshipDay.add(this.jComboBoxWorshipWeek);
         this.jComboBoxWorshipDay.setFont(f);
         this.jComboBoxWorshipDay.setModel(new DefaultComboBoxModel<>(EnumWorship.EnumWorshipDays.values()));
         this.jComboBoxWorshipDay.setSelectedItem(this.w.day);
         this.jComboBoxWorshipDay.setEnabled(false);
         this.jPanelWorshipDay.add(this.jComboBoxWorshipDay);
         this.jPanelWorshipSettings.add(this.jPanelWorshipDay);
         this.jLabelWorshipTime.setFont(f);
         this.jLabelWorshipTime.setText(bundle.getString("SettingsView.jLabelWorshipTime.text"));
         this.jPanelWorshipTime.add(this.jLabelWorshipTime);
         this.jSpinnerWorshipHour.setFont(f);
         this.jSpinnerWorshipHour.setModel(new SpinnerNumberModel(this.w.hour, 0, 23, 1));
         this.jPanelWorshipTime.add(this.jSpinnerWorshipHour);
         this.jLabelTwoPoints.setFont(f.deriveFont(14.0F));
         this.jLabelTwoPoints.setText(":");
         this.jPanelWorshipTime.add(this.jLabelTwoPoints);
         this.jSpinnerWorshipMinute.setFont(f);
         this.jSpinnerWorshipMinute.setModel(new SpinnerNumberModel(this.w.minute, 0, 55, 5));
         this.jPanelWorshipTime.add(this.jSpinnerWorshipMinute);
         this.jTextFieldWorshipDescription.setColumns(20);
         this.jTextFieldWorshipDescription.setDocument(SwingUtils.createDocMaxSize(20));
         this.jTextFieldWorshipDescription.setText(this.w.desc);
         this.jTextFieldWorshipDescription.setFont(f);
         this.jTextFieldWorshipDescription.setHorizontalAlignment(0);
         this.jPanelWorshipTime.add(this.jTextFieldWorshipDescription);
         this.jCheckBoxKeepSchedules.setText(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.keepSchedulesWorship"));
         this.jCheckBoxKeepSchedules.setSelected(true);
         this.jPanelWorshipSettings.add(this.jPanelWorshipTime);
         this.jPanelWorshipSettings.add(this.jCheckBoxKeepSchedules);
         this.add(this.jPanelWorshipSettings);
      }

      private Worship getWorship() {
         try {
            return new Worship(
               (EnumWorship.EnumWorshipDays)this.jComboBoxWorshipDay.getSelectedItem(),
               (EnumWorship.EnumWorshipWeeks)this.jComboBoxWorshipWeek.getSelectedItem(),
               Integer.parseInt(this.jSpinnerWorshipHour.getValue().toString()),
               Integer.parseInt(this.jSpinnerWorshipMinute.getValue().toString()),
               this.jTextFieldWorshipDescription.getText()
            );
         } catch (Exception var2) {
            Log.send(var2);
            return this.w;
         }
      }

      public static void main(Worship worship) {
         show(new EditWorship.WorshipEditView(worship), worship);
      }

      private static void show(EditWorship.WorshipEditView panel, Worship worship) {
         if (JOptionPane.showConfirmDialog(null, panel, I18N.WORD_EDIT.val(), 2, -1) == 0) {
            if (panel.jCheckBoxKeepSchedules.isSelected()) {
               WorshipController.setTimeAndDesc(worship, panel.getWorship());
               WorshipController.save();
            } else if (Utils.confirm(I18N.MSG_CONFIRM_EDIT_WORSHIP.val())) {
               WorshipController.remove(worship);
               WorshipController.add(panel.getWorship());
               WorshipController.save();
            } else {
               show(panel, worship);
            }
         }
      }
   }
}
