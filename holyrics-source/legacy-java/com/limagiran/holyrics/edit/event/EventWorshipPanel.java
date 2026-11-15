package com.limagiran.holyrics.edit.event;

import com.alee.laf.button.WebButton;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EventWorshipPanel extends JPanel {
   private static final Color COLOR_PRIMARY = new Color(225, 225, 225);
   private static final Color COLOR_SECONDARY = new Color(165, 165, 165);
   private static final Color COLOR_BORDER = new Color(40, 40, 40);
   private static final ImageIcon IC_VIEW = new ImageIcon(Images.load("eventNote16.png"));
   private static final ImageIcon IC_VIEW_LIGHT = new ImageIcon(Images.load("eventNote16.png", 0.65F));
   private float backgroundOpacity = 1.0F;
   public final EventTable table;
   public final Event event;
   private JButton jButtonShow;
   private JLabel jLabel;

   public EventWorshipPanel(Event event, EventTable table) {
      this.event = event;
      this.table = table;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.setDrawLightForeground(!this.table.isMonth(this.event.getDate()));
      if (this.event.getName().replace(" ", "").isEmpty()) {
         this.jLabel.setText(String.format("%s - %s", DateLG.SDF_T_S.format(this.event.getDate().getTime()), I18N.WORD_WORSHIP_EVENT.val()));
      } else {
         this.jLabel
            .setText(String.format("%s - %s (%s)", DateLG.SDF_T_S.format(this.event.getDate().getTime()), I18N.WORD_WORSHIP_EVENT.val(), this.event.getName()));
      }

      ((WebButton)this.jButtonShow).setUndecorated(true);
      this.jButtonShow.addActionListener(e -> this.openForm());
   }

   private void initComponents() {
      this.jLabel = new JLabel();
      this.jButtonShow = new WebButton();
      this.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));
      this.setMinimumSize(new Dimension(0, 0));
      this.setOpaque(false);
      this.setLayout(new BorderLayout(2, 0));
      this.jLabel.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jLabel.setHorizontalAlignment(0);
      this.jLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
      this.add(this.jLabel, "Center");
      this.jButtonShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/eventNote16.png")));
      this.jButtonShow.setMargin(new Insets(2, 0, 0, 0));
      this.jButtonShow.setOpaque(false);
      this.add(this.jButtonShow, "East");
   }

   @Override
   protected void paintComponent(Graphics g) {
      BufferedImage bi = new BufferedImage(this.getWidth(), this.getHeight(), 2);
      Graphics2D g2 = bi.createGraphics();
      G2Utils.setupGraphics(g2);
      g2.setComposite(AlphaComposite.getInstance(3, this.backgroundOpacity));
      g2.setColor(COLOR_PRIMARY);
      g2.setPaint(new GradientPaint((float)(this.getWidth() / 2), 0.0F, COLOR_PRIMARY, (float)(this.getWidth() / 2), (float)this.getHeight(), COLOR_SECONDARY));
      g2.fillRoundRect(2, 2, this.getWidth() - 3, this.getHeight() - 3, 10, 10);
      g2.setColor(COLOR_BORDER);
      g2.drawRoundRect(1, 1, this.getWidth() - 2, this.getHeight() - 2, 10, 10);
      g2.dispose();
      g.drawImage(bi, 0, 0, null);
      super.paintComponent(g);
   }

   protected void fireUpdate() {
      this.table.fireUpdate(this.event);
   }

   private void setDrawLightForeground(boolean drawLightForeground) {
      this.jLabel.setForeground(drawLightForeground ? StyleUI.FOREGROUND_LIGHT : Color.BLACK);
      this.backgroundOpacity = drawLightForeground ? 0.5F : 1.0F;
      this.jButtonShow.setIcon(drawLightForeground ? IC_VIEW_LIGHT : IC_VIEW);
   }

   @Override
   public Dimension getPreferredSize() {
      Dimension d = super.getPreferredSize();
      d.width = 0;
      return d;
   }

   public static GridBagConstraints createConstraints(int row, boolean isLast) {
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = row;
      gbc.gridwidth = 0;
      gbc.fill = 2;
      gbc.anchor = 11;
      gbc.weightx = 1.0;
      gbc.weighty = isLast ? 1.0 : 0.0;
      return gbc;
   }

   private void openForm() {
      EventWorshipForm.main(this, this);
   }
}
