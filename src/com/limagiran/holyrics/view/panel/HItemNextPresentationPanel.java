package com.limagiran.holyrics.view.panel;

import com.alee.laf.combobox.WebComboBox;
import com.limagiran.holyrics.favorite.Favoritable;
import com.limagiran.holyrics.favorite.FavoriteItem;
import com.limagiran.holyrics.hitem.item.HItemMusic;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.swing.ComboBoxModelHItemNextPresentation;
import com.limagiran.holyrics.swing.ComboBoxRendererHItem;
import com.limagiran.holyrics.util.PopupUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class HItemNextPresentationPanel extends JPanel {
   private JComboBox<Object> jComboBoxHItem;
   private JLabel jLabelHelp;
   private JLabel jLabelHelp2;
   private JLabel jLabelNextPresentation;
   private JPanel jPanelCenter;
   private JPanel jPanelHeader;

   private HItemNextPresentationPanel(Object selected) {
      this.initComponents();
      this.jComboBoxHItem.setSelectedItem(selected);
   }

   private void initComponents() {
      this.jPanelHeader = new JPanel();
      this.jLabelHelp2 = new JLabel() {
         @Override
         protected void paintComponent(Graphics g) {
         }
      };
      this.jLabelNextPresentation = new JLabel();
      this.jLabelHelp = new JLabel();
      this.jPanelCenter = new JPanel();
      this.jComboBoxHItem = new WebComboBox() {
         private boolean layingOut = false;

         public void doLayout() {
            try {
               this.layingOut = true;
               super.doLayout();
            } finally {
               this.layingOut = false;
            }
         }

         public Dimension getSize() {
            Dimension dim = super.getSize();
            if (!this.layingOut) {
               dim.width = Math.max(dim.width, this.getPreferredSize().width);
            }

            return dim;
         }
      };
      this.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.setLayout(new BorderLayout(0, 4));
      this.jPanelHeader.setLayout(new BorderLayout());
      this.jLabelHelp2.setFont(new Font("Courier New", 1, 24));
      this.jLabelHelp2.setForeground(new Color(128, 128, 128));
      this.jLabelHelp2.setText("?");
      this.jPanelHeader.add(this.jLabelHelp2, "West");
      this.jLabelNextPresentation.setFont(new Font("Georgia", 1, 16));
      this.jLabelNextPresentation.setHorizontalAlignment(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jLabelNextPresentation.setText(bundle.getString("msg.nextPresentation"));
      this.jPanelHeader.add(this.jLabelNextPresentation, "Center");
      this.jLabelHelp.setFont(new Font("Courier New", 1, 24));
      this.jLabelHelp.setForeground(new Color(128, 128, 128));
      this.jLabelHelp.setText("?");
      this.jLabelHelp.setCursor(new Cursor(12));
      this.jLabelHelp.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            HItemNextPresentationPanel.this.jLabelHelpMouseReleased(evt);
         }
      });
      this.jPanelHeader.add(this.jLabelHelp, "East");
      this.add(this.jPanelHeader, "First");
      this.jPanelCenter.setLayout(new BorderLayout());
      this.jComboBoxHItem.setModel(new ComboBoxModelHItemNextPresentation());
      this.jComboBoxHItem.setFocusable(false);
      this.jComboBoxHItem.setPreferredSize(new Dimension(256, 36));
      this.jComboBoxHItem.setRenderer(new ComboBoxRendererHItem());
      this.jPanelCenter.add(this.jComboBoxHItem, "Center");
      this.add(this.jPanelCenter, "Center");
   }

   private void jLabelHelpMouseReleased(MouseEvent evt) {
      PopupUtils.helpHItemNextPresentation(null);
   }

   public static HItemNextPresentationPanel create(Object selected) {
      return new HItemNextPresentationPanel(selected);
   }

   public void update() {
      ((ComboBoxModelHItemNextPresentation)this.jComboBoxHItem.getModel()).update();
   }

   public void setSelectedItem(Object selected) {
      SwingUtilities.invokeLater(() -> {
         this.jComboBoxHItem.setSelectedItem(selected);
         this.jComboBoxHItem.repaint();
      });
   }

   public Object getSelectedItem() {
      return this.jComboBoxHItem.getSelectedItem();
   }

   public Favoritable getSelectedItemAsFavoritable() {
      Object obj = this.jComboBoxHItem.getSelectedItem();
      if (obj instanceof FavoriteItem) {
         return ((FavoriteItem)obj).getFavorite();
      } else if (obj instanceof Favoritable) {
         return (Favoritable)obj;
      } else {
         return obj instanceof Music ? new HItemMusic(((Music)obj).getId()) : null;
      }
   }
}
