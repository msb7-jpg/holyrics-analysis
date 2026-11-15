package com.limagiran.holyrics.swing;

import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.CountdownSettings;
import com.limagiran.holyrics.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;

public class CountdownValueInputDialog extends JPanel {
   private final CountdownSettings set;
   private JLabel jLabelSeparator1;
   private JLabel jLabelSeparator2;
   private JPanel jPanelLeftHeader;
   private JPanel jPanelLeftHeaderTypeSettings;
   private JPanel jPanelType;
   private JPanel jPanelTypeSetDetermine;
   private JPanel jPanelTypeSetExact;
   private JRadioButton jRadioButtonDetermine;
   private JRadioButton jRadioButtonExact;
   private JSpinner jSpinnerHourExact;
   private JSpinner jSpinnerMinDetermine;
   private JSpinner jSpinnerMinExact;
   private JSpinner jSpinnerSecDetermine;

   private CountdownValueInputDialog(CountdownSettings set) {
      this.set = set;
      this.initComponents();
      this.init();
   }

   private void init() {
      ButtonGroup buttonGroup = new ButtonGroup();
      buttonGroup.add(this.jRadioButtonDetermine);
      buttonGroup.add(this.jRadioButtonExact);
      this.jSpinnerMinDetermine.setValue(this.set.getMinuteDetermine());
      this.jSpinnerSecDetermine.setValue(this.set.getSecondDetermine());
      this.jSpinnerHourExact.setValue(this.set.getHourExact());
      this.jSpinnerMinExact.setValue(this.set.getMinuteExact());
      this.jRadioButtonDetermine.addActionListener(this::radioChanged);
      this.jRadioButtonExact.addActionListener(this::radioChanged);
      this.jSpinnerMinDetermine.addChangeListener(new ChangeEventDelay(400L, () -> this.spinnerChange(0)));
      this.jSpinnerSecDetermine.addChangeListener(new ChangeEventDelay(400L, () -> this.spinnerChange(1)));
      this.jSpinnerHourExact.addChangeListener(new ChangeEventDelay(400L, () -> this.spinnerChange(2)));
      this.jSpinnerMinExact.addChangeListener(new ChangeEventDelay(400L, () -> this.spinnerChange(3)));
      this.updateCardLayout();
   }

   private void initComponents() {
      this.jPanelLeftHeader = new JPanel();
      this.jPanelType = new JPanel();
      this.jRadioButtonDetermine = new JRadioButton();
      this.jRadioButtonExact = new JRadioButton();
      this.jPanelLeftHeaderTypeSettings = new JPanel();
      this.jPanelTypeSetDetermine = new JPanel();
      this.jSpinnerMinDetermine = this.createDefaultSpinner();
      this.jLabelSeparator1 = new JLabel();
      this.jSpinnerSecDetermine = this.createDefaultSpinner();
      this.jPanelTypeSetExact = new JPanel();
      this.jSpinnerHourExact = this.createDefaultSpinner();
      this.jLabelSeparator2 = new JLabel();
      this.jSpinnerMinExact = this.createDefaultSpinner();
      this.setLayout(new BorderLayout());
      this.jPanelLeftHeader.setLayout(new BorderLayout(0, 4));
      this.jPanelType.setLayout(new FlowLayout(1, 10, 0));
      this.jRadioButtonDetermine.setFont(new Font("Georgia", 0, 14));
      this.jRadioButtonDetermine.setSelected(this.set.isDetermine());
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jRadioButtonDetermine.setText(bundle.getString("msg.determineTime"));
      this.jRadioButtonDetermine.setFocusable(false);
      this.jPanelType.add(this.jRadioButtonDetermine);
      this.jRadioButtonExact.setFont(new Font("Georgia", 0, 14));
      this.jRadioButtonExact.setSelected(!this.set.isDetermine());
      this.jRadioButtonExact.setText(bundle.getString("msg.exactTime"));
      this.jRadioButtonExact.setFocusable(false);
      this.jPanelType.add(this.jRadioButtonExact);
      this.jPanelLeftHeader.add(this.jPanelType, "First");
      this.jPanelLeftHeaderTypeSettings.setLayout(new CardLayout());
      this.jPanelTypeSetDetermine.setLayout(new FlowLayout(1, 5, 3));
      this.jSpinnerMinDetermine.setFont(new Font("Courier New", 0, 14));
      this.jSpinnerMinDetermine.setModel(new SpinnerNumberModel(5, 0, 59, 1));
      this.jPanelTypeSetDetermine.add(this.jSpinnerMinDetermine);
      this.jLabelSeparator1.setFont(new Font("Courier New", 0, 14));
      this.jLabelSeparator1.setText(":");
      this.jPanelTypeSetDetermine.add(this.jLabelSeparator1);
      this.jSpinnerSecDetermine.setFont(new Font("Courier New", 0, 14));
      this.jSpinnerSecDetermine.setModel(new SpinnerNumberModel(0, 0, 59, 5));
      this.jPanelTypeSetDetermine.add(this.jSpinnerSecDetermine);
      this.jPanelLeftHeaderTypeSettings.add(this.jPanelTypeSetDetermine, "determine");
      this.jPanelTypeSetExact.setLayout(new FlowLayout(1, 5, 3));
      this.jSpinnerHourExact.setFont(new Font("Courier New", 0, 14));
      this.jSpinnerHourExact.setModel(new SpinnerNumberModel(19, 0, 23, 1));
      this.jPanelTypeSetExact.add(this.jSpinnerHourExact);
      this.jLabelSeparator2.setFont(new Font("Courier New", 0, 14));
      this.jLabelSeparator2.setText(":");
      this.jPanelTypeSetExact.add(this.jLabelSeparator2);
      this.jSpinnerMinExact.setFont(new Font("Courier New", 0, 14));
      this.jSpinnerMinExact.setModel(new SpinnerNumberModel(0, 0, 59, 1));
      this.jPanelTypeSetExact.add(this.jSpinnerMinExact);
      this.jPanelLeftHeaderTypeSettings.add(this.jPanelTypeSetExact, "exact");
      this.jPanelLeftHeader.add(this.jPanelLeftHeaderTypeSettings, "Last");
      this.add(this.jPanelLeftHeader, "Center");
   }

   public static void main(CountdownSettings set, Runnable callback) {
      SwingUtilities.invokeLater(() -> {
         CountdownValueInputDialog panel = new CountdownValueInputDialog(set);
         SwingUtils.requestFocusOnVisible(set.isDetermine() ? panel.jSpinnerSecDetermine : panel.jSpinnerMinExact);
         int op = JOptionPane.showConfirmDialog(null, panel, I18N.WORD_COUNTDOWN.val(), 2, -1);
         panel.commitAllSpinners();
         if (op == 0) {
            callback.run();
         }
      });
   }

   private WebSpinner createDefaultSpinner() {
      return new WebSpinner();
   }

   private void radioChanged(ActionEvent e) {
      if (e.getSource() == this.jRadioButtonDetermine) {
         this.set.setDetermine(this.jRadioButtonDetermine.isSelected());
      } else {
         this.set.setDetermine(!this.jRadioButtonExact.isSelected());
      }

      this.updateCardLayout();
   }

   private void updateCardLayout() {
      ((CardLayout)this.jPanelLeftHeaderTypeSettings.getLayout()).show(this.jPanelLeftHeaderTypeSettings, this.set.isDetermine() ? "determine" : "exact");
   }

   private void spinnerChange(int index) {
      try {
         switch (index) {
            case 0:
               this.set.setMinuteDetermine(Integer.parseInt(this.jSpinnerMinDetermine.getValue().toString()));
               break;
            case 1:
               this.set.setSecondDetermine(Integer.parseInt(this.jSpinnerSecDetermine.getValue().toString()));
               break;
            case 2:
               this.set.setHourExact(Integer.parseInt(this.jSpinnerHourExact.getValue().toString()));
               break;
            case 3:
               this.set.setMinuteExact(Integer.parseInt(this.jSpinnerMinExact.getValue().toString()));
         }
      } catch (Exception var3) {
      }

      this.set.save();
   }

   private void commitAllSpinners() {
      Arrays.asList(this.jSpinnerMinDetermine, this.jSpinnerSecDetermine, this.jSpinnerHourExact, this.jSpinnerMinExact).forEach(SwingUtils::commitEdit);
      IntStream.rangeClosed(0, 3).forEach(this::spinnerChange);
   }
}
