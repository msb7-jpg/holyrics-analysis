package com.limagiran.vagalumeapi;

import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.GroupLayout.Alignment;

public class Logo extends JPanel {
   private JLabel jLabel;

   public Logo() {
      this.initComponents();
   }

   private void initComponents() {
      this.jLabel = new JLabel();
      this.jLabel.setFont(new Font("Tahoma", 0, 12));
      this.jLabel.setHorizontalAlignment(11);
      this.jLabel.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/vagalumeapi/icon.png")));
      this.jLabel.setText("<html>Pesquisa realizada pela API do Vagalume<br><span style=\"color:blue\"><u>http://www.vagalume.com.br/<u></span>");
      this.jLabel.setVerticalAlignment(3);
      this.jLabel.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.jLabel.setHorizontalTextPosition(2);
      this.jLabel.setVerticalTextPosition(3);
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel, -2, -1, -2));
      layout.setVerticalGroup(layout.createParallelGroup(Alignment.LEADING).addComponent(this.jLabel, -2, -1, -2));
   }
}
