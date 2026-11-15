package com.limagiran.holyrics.util;

import com.limagiran.holyrics.i18n.I18N;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class PopUpOpenProgram extends JFrame {
   private JLabel jLabelIcon;
   private JLabel jLabelLoading;
   private JLabel jLabelProgress;
   private JPanel jPanel;
   private JPanel jPanelFooter;

   private PopUpOpenProgram() {
      this.initComponents();
   }

   private void initComponents() {
      this.jPanel = new JPanel();
      this.jLabelIcon = new JLabel();
      this.jPanelFooter = new JPanel();
      this.jLabelLoading = new JLabel();
      this.jLabelProgress = new JLabel();
      this.setDefaultCloseOperation(0);
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setUndecorated(true);
      this.setPreferredSize(new Dimension(600, 450));
      this.setResizable(false);
      this.jPanel.setBackground(new Color(255, 255, 255));
      this.jPanel
         .setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(new Color(48, 63, 159)), BorderFactory.createEmptyBorder(16, 16, 16, 16)));
      this.jPanel.setLayout(new BorderLayout());
      this.jLabelIcon.setHorizontalAlignment(0);
      this.jLabelIcon.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/openProgram.png")));
      this.jLabelIcon.setBorder(BorderFactory.createEmptyBorder(0, 48, 8, 48));
      this.jPanel.add(this.jLabelIcon, "Center");
      this.jPanelFooter.setOpaque(false);
      this.jPanelFooter.setLayout(new GridLayout(1, 2));
      this.jLabelLoading.setFont(new Font("Courier New", 0, 16));
      this.jLabelLoading.setForeground(new Color(50, 50, 50));
      this.jLabelLoading.setHorizontalAlignment(4);
      this.jLabelLoading.setText(I18N.WORD_LOADING.val() + ": ");
      this.jPanelFooter.add(this.jLabelLoading);
      this.jLabelProgress.setFont(new Font("Courier New", 0, 16));
      this.jLabelProgress.setForeground(new Color(50, 50, 50));
      this.jLabelProgress.setHorizontalAlignment(2);
      this.jLabelProgress.setText(" ");
      this.jPanelFooter.add(this.jLabelProgress);
      this.jPanel.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanel, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static PopUpOpenProgram main() {
      PopUpOpenProgram popup = new PopUpOpenProgram();
      SwingUtils.runOnUIThread(() -> popup.setVisible(true));
      return popup;
   }

   public void setProgress(String progress) {
      SwingUtils.runOnUIThread(() -> this.jLabelProgress.setText(progress));
   }
}
