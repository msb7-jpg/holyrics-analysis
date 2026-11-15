package com.limagiran.holyrics.model.announcement;

import com.alee.laf.button.WebButton;
import com.limagiran.holyrics.control.AnnouncementController;
import com.limagiran.holyrics.event.AnnouncementTitlePanelListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Announcement;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class AnnouncementTitlePanel extends JPanel {
   private final Announcement announcement;
   private final AnnouncementTitlePanelListener listener;
   private JButton jButtonDownward;
   private JButton jButtonRemove;
   private JButton jButtonUpward;
   private JPanel jPanelHeader;
   private JPanel jPanelUpDown;
   private JTextArea jTextAreaTitle;

   public AnnouncementTitlePanel(Announcement announcement, AnnouncementTitlePanelListener listener) {
      this.announcement = announcement;
      this.listener = listener;
      this.initComponents();
      this.init();
   }

   private void init() {
      Arrays.asList(this.jButtonRemove, this.jButtonUpward, this.jButtonDownward).stream().map(b -> (WebButton)b).forEach(b -> b.setUndecorated(true));
      this.jButtonRemove.addActionListener(e -> this.remove());
      this.jButtonUpward.addActionListener(e -> this.listener.upward(this));
      this.jButtonDownward.addActionListener(e -> this.listener.downward(this));
      this.setBackground(StyleUI.COLOR_BLUE);
      this.jTextAreaTitle.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            e.consume();
         }

         @Override
         public void mousePressed(MouseEvent e) {
            AnnouncementTitlePanel.this.listener.selected(AnnouncementTitlePanel.this);
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            e.consume();
         }
      });
      this.update();
   }

   private void initComponents() {
      this.jPanelHeader = new JPanel();
      this.jPanelUpDown = new JPanel();
      this.jButtonUpward = new WebButton();
      this.jButtonDownward = new WebButton();
      this.jTextAreaTitle = new JTextArea();
      this.jButtonRemove = new WebButton();
      this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.setMinimumSize(new Dimension(0, 0));
      this.setOpaque(false);
      this.setLayout(new BorderLayout(2, 0));
      this.jPanelHeader.setOpaque(false);
      this.jPanelHeader.setLayout(new BorderLayout(5, 0));
      this.jPanelUpDown.setOpaque(false);
      this.jPanelUpDown.setLayout(new GridLayout(1, 2));
      this.jButtonUpward.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/upward16.png")));
      this.jButtonUpward.setMargin(new Insets(2, 0, 0, 0));
      this.jPanelUpDown.add(this.jButtonUpward);
      this.jButtonDownward.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/downward16.png")));
      this.jButtonDownward.setMargin(new Insets(2, 0, 0, 0));
      this.jPanelUpDown.add(this.jButtonDownward);
      this.jPanelHeader.add(this.jPanelUpDown, "West");
      this.jTextAreaTitle.setEditable(false);
      this.jTextAreaTitle.setColumns(15);
      this.jTextAreaTitle.setFont(new Font("Courier New", 1, 14));
      this.jTextAreaTitle.setLineWrap(true);
      this.jTextAreaTitle.setRows(1);
      this.jTextAreaTitle.setWrapStyleWord(true);
      this.jTextAreaTitle.setBorder(null);
      this.jTextAreaTitle.setFocusable(false);
      this.jTextAreaTitle.setOpaque(false);
      this.jPanelHeader.add(this.jTextAreaTitle, "Center");
      this.jButtonRemove.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/delete16.png")));
      this.jButtonRemove.setMargin(new Insets(2, 0, 0, 0));
      this.jButtonRemove.setOpaque(false);
      this.jPanelHeader.add(this.jButtonRemove, "East");
      this.add(this.jPanelHeader, "Center");
   }

   private void remove() {
      if (Utils.confirm(this.getRootPane(), I18N.MSG_CONFIRM_REMOVE_THIS_ANNOUNCEMENT.val())) {
         AnnouncementController.remove(this.announcement);
         this.listener.removed(this);
      }
   }

   public void update() {
      this.jTextAreaTitle.setText(this.announcement.getName());
      this.revalidate();
      this.repaint();
   }

   public void updateButtons() {
      this.jButtonDownward.setEnabled(!this.listener.isLast(this));
      this.jButtonUpward.setEnabled(!this.listener.isFirst(this));
   }

   public Announcement getAnnouncement() {
      return this.announcement;
   }
}
