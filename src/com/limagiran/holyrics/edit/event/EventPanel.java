package com.limagiran.holyrics.edit.event;

import com.alee.laf.button.WebButton;
import com.limagiran.holyrics.control.EventController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.Utils;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class EventPanel extends JPanel {
   private static final Color COLOR_PRIMARY = new Color(205, 205, 205);
   private static final Color COLOR_SECONDARY = new Color(145, 145, 145);
   private static final Color COLOR_BORDER = new Color(40, 40, 40);
   private static final ImageIcon IC_DELETE = new ImageIcon(Images.load("delete16.png"));
   private static final ImageIcon IC_DELETE_LIGHT = new ImageIcon(Images.load("delete16.png", 0.65F));
   private static final ImageIcon IC_EDIT = new ImageIcon(Images.load("eventNote16.png"));
   private static final ImageIcon IC_EDIT_LIGHT = new ImageIcon(Images.load("eventNote16.png", 0.65F));
   private float backgroundOpacity = 1.0F;
   public final EventTable table;
   public final Event event;
   private JButton jButtonRemove;
   private JButton jButtonShow;
   private JLabel jLabelName;
   private JLabel jLabelTime;
   private JPanel jPanelButtons;
   private JPanel jPanelHeader;

   public EventPanel(Event event, EventTable table) {
      this.event = event;
      this.table = table;
      this.initComponents();
      this.init();
   }

   private void init() {
      ((WebButton)this.jButtonShow).setUndecorated(true);
      ((WebButton)this.jButtonRemove).setUndecorated(true);
      this.jButtonShow.addActionListener(e -> this.openForm());
      this.jButtonRemove.addActionListener(e -> this.remove());
      this.setDrawLightForeground(!this.table.isMonth(this.event.getDate()));
   }

   private void initComponents() {
      this.jPanelHeader = new JPanel();
      this.jLabelTime = new JLabel();
      this.jPanelButtons = new JPanel();
      this.jButtonShow = new WebButton();
      this.jButtonRemove = new WebButton();
      this.jLabelName = new JLabel();
      this.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 0));
      this.setMinimumSize(new Dimension(0, 0));
      this.setOpaque(false);
      this.setLayout(new BorderLayout(2, 0));
      this.jPanelHeader.setOpaque(false);
      this.jPanelHeader.setLayout(new BorderLayout());
      this.jLabelTime.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jLabelTime.setText(DateLG.SDF_T_S.format(this.event.getDate().getTime()));
      this.jPanelHeader.add(this.jLabelTime, "Center");
      this.jPanelButtons.setOpaque(false);
      this.jPanelButtons.setLayout(new GridLayout(1, 0, -8, 0));
      this.jButtonShow.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/eventNote16.png")));
      this.jButtonShow.setMargin(new Insets(2, 0, 0, 0));
      this.jButtonShow.setOpaque(false);
      this.jPanelButtons.add(this.jButtonShow);
      this.jButtonRemove.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/delete16.png")));
      this.jButtonRemove.setMargin(new Insets(2, 0, 0, 0));
      this.jButtonRemove.setOpaque(false);
      this.jPanelButtons.add(this.jButtonRemove);
      this.jPanelHeader.add(this.jPanelButtons, "East");
      this.add(this.jPanelHeader, "First");
      this.jLabelName.setFont(new Font("Franklin Gothic Medium", 0, 12));
      this.jLabelName.setHorizontalAlignment(0);
      this.jLabelName.setText(this.event.getName());
      this.jLabelName.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
      this.add(this.jLabelName, "Center");
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

   private void openForm() {
      EventFormEdit.main(this, this);
   }

   protected void fireUpdate() {
      this.table.fireUpdate(this.event);
   }

   private void remove() {
      if (Utils.confirm(this, I18N.MSG_CONFIRM_REMOVE_EVENT.format(this.event))) {
         EventController.remove(this.event);
         this.setVisible(false);
      }
   }

   private void setDrawLightForeground(boolean drawLightForeground) {
      this.jButtonShow.setIcon(drawLightForeground ? IC_EDIT_LIGHT : IC_EDIT);
      this.jButtonRemove.setIcon(drawLightForeground ? IC_DELETE_LIGHT : IC_DELETE);
      this.jLabelName.setForeground(drawLightForeground ? StyleUI.FOREGROUND_LIGHT.brighter() : StyleUI.FOREGROUND_DARK);
      this.jLabelTime.setForeground(drawLightForeground ? StyleUI.FOREGROUND_LIGHT.brighter() : StyleUI.FOREGROUND_DARK);
      this.backgroundOpacity = drawLightForeground ? 0.5F : 1.0F;
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
}
