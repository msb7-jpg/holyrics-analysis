package com.limagiran.holyrics.view.panel;

import com.alee.laf.button.WebButton;
import com.limagiran.holyrics.control.AlertController;
import com.limagiran.holyrics.model.Alert;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Values;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class AlertListPanel extends JPanel {
   private final AlertListPanel.AlertListPanelListener listener;
   private JButton jButtonCreate;
   private JPanel jPanelGeneral;
   private JPanel jPanelHeader;
   private JTextField jTextFieldAdd;

   public AlertListPanel(String initial, AlertListPanel.AlertListPanelListener listener) {
      this.listener = listener;
      this.initComponents();
      this.init(initial);
   }

   private void init(String initial) {
      AlertController.getAll().forEach(this::addRow);
      this.listener.fireDataChanged();
      this.jButtonCreate.addActionListener(e -> this.create());
      if (AlertController.getAll().stream().allMatch(a -> !a.getText().equals(initial))) {
         this.jTextFieldAdd.setText(initial);
      }

      this.jTextFieldAdd.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            if (e.getKeyCode() == 10) {
               AlertListPanel.this.create();
            }
         }
      });
      SwingUtils.requestFocusOnVisible(this.jTextFieldAdd);
   }

   private void initComponents() {
      this.jPanelHeader = new JPanel();
      this.jTextFieldAdd = new JTextField();
      this.jButtonCreate = new JButton();
      this.jPanelGeneral = new JPanel();
      this.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.setLayout(new BorderLayout(0, 8));
      this.jPanelHeader.setOpaque(false);
      this.jPanelHeader.setLayout(new BorderLayout(4, 0));
      this.jTextFieldAdd.setColumns(40);
      this.jTextFieldAdd.setFont(new Font("Georgia", 0, 14));
      this.jPanelHeader.add(this.jTextFieldAdd, "Center");
      this.jButtonCreate.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/saveBlack16.png")));
      this.jButtonCreate.setFocusable(false);
      this.jPanelHeader.add(this.jButtonCreate, "After");
      this.add(this.jPanelHeader, "Last");
      this.jPanelGeneral.setOpaque(false);
      this.jPanelGeneral.setLayout(new GridLayout(0, 1));
      this.add(this.jPanelGeneral, "Center");
   }

   private void addRow(final Alert alert) {
      JPanel p = new JPanel(new BorderLayout(0, 2));
      p.setOpaque(false);
      WebButton select = new WebButton(new ImageIcon(Images.load("checkBlack16.png")));
      select.setShadeColor(Values.COLOR_TRANSPARENT).setFocusable(false);
      select.addActionListener(e -> this.listener.selected(alert));
      WebButton alertButton = new WebButton(new ImageIcon(Images.load("alert16.png")));
      alertButton.setShadeColor(Values.COLOR_TRANSPARENT).setFocusable(false);
      alertButton.addActionListener(e -> AlertSchedulePanel.main(alert, alertButton, AlertController::save));
      final JTextField textField = new JTextField();
      textField.setFont(this.jTextFieldAdd.getFont());
      textField.setColumns(40);
      textField.setDocument(SwingUtils.createDocMaxSize(1024));
      textField.getDocument().addDocumentListener(new DocumentListener() {
         @Override
         public void insertUpdate(DocumentEvent e) {
            alert.setText(textField.getText());
         }

         @Override
         public void removeUpdate(DocumentEvent e) {
            alert.setText(textField.getText());
         }

         @Override
         public void changedUpdate(DocumentEvent e) {
            alert.setText(textField.getText());
         }
      });
      textField.getDocument().addDocumentListener(new DocumentListenerAuto(250L, AlertController::save));
      textField.setText(alert.getText());
      WebButton remove = new WebButton(new ImageIcon(Images.load("delete16.png")));
      remove.setShadeColor(Values.COLOR_TRANSPARENT).setFocusable(false);
      remove.addActionListener(e -> {
         AlertController.remove(alert);
         p.setVisible(false);
         this.jPanelGeneral.remove(p);
         this.listener.fireDataChanged();
      });
      JPanel selectAndAlert = new JPanel(new GridLayout(1, 2, 2, 2));
      selectAndAlert.add(select);
      selectAndAlert.add(alertButton);
      p.add(selectAndAlert, "West");
      p.add(textField, "Center");
      p.add(remove, "East");
      this.jPanelGeneral.add(p);
   }

   private void create() {
      if (!this.jTextFieldAdd.getText().replace(" ", "").isEmpty()) {
         this.addRow(AlertController.add(new Alert(this.jTextFieldAdd.getText())));
         this.jTextFieldAdd.setText("");
         this.listener.fireDataChanged();
      }
   }

   public interface AlertListPanelListener {
      void selected(Alert var1);

      void fireDataChanged();
   }
}
