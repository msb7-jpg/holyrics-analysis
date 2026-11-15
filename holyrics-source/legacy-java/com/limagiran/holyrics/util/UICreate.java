package com.limagiran.holyrics.util;

import com.alee.extended.button.WebSwitch;
import com.alee.laf.button.WebButton;
import com.alee.laf.button.WebToggleButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.table.WebTable;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.IntStream;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class UICreate {
   public static JToggleButton buttonTabbed(final Color c) {
      return new JToggleButton() {
         private final Color unselected = new JToggleButton().getForeground();
         private final Color selected = new Color(248, 248, 248);

         @Override
         protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(c);
            if (this.isSelected()) {
               g2.fillRect(0, 0, this.getWidth(), this.getHeight());
            } else {
               g2.drawRect(0, 0, this.getWidth() - 1, this.getHeight() - 1);
            }

            g2.setColor(this.isSelected() ? this.selected : this.unselected);
            G2Utils.drawString(this.getSize(), g2, 0.0, G2Utils.VerticalAlign.MIDDLE, this.getText());
            g2.dispose();
         }
      };
   }

   public static WebTable jTableAlternateRow() {
      return new WebTable() {
         private final EmptyBorder border = new EmptyBorder(0, 5, 0, 5);

         public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            BooleanOptional.runIfTrue(!super.isCellSelected(row, column), () -> c.setBackground(row % 2 == 0 ? Color.WHITE : StyleUI.COLOR_ALTERNATE_ROW));
            BooleanOptional.runIfTrue(c instanceof JComponent, () -> ((JComponent)c).setBorder(this.border));
            return c;
         }
      };
   }

   public static WebSwitch newWebSwitch() {
      WebSwitch _switch = new WebSwitch();
      _switch.setRound(11);
      WebLabel left = new WebLabel(new ImageIcon(Images.load("switchOn.png")), 0);
      left.setMargin(2, 4, 2, 0);
      WebLabel right = new WebLabel(new ImageIcon(Images.load("switchOff.png")), 0);
      right.setMargin(2, 0, 2, 4);
      _switch.setLeftComponent(left);
      _switch.setRightComponent(right);
      return _switch;
   }

   public static WebButton defaultHelpButton(ActionListener listener, int size) {
      WebButton wb = new WebButton("?", listener);
      wb.setFont(new Font("Arial", 1, size));
      wb.setForeground(Color.DARK_GRAY);
      wb.setFocusable(false);
      wb.setCursor(Cursor.getPredefinedCursor(12));
      wb.setShadeWidth(0);
      int h = (int)((double)size * 1.5);
      wb.setSize(h, h);
      wb.setPreferredSize(h, h);
      wb.setRound(size);
      return wb;
   }

   public static WebButton defaultHelpButton(ActionListener listener) {
      return defaultHelpButton(listener, 26);
   }

   public static JPanel nineDirectionsPanel(List<Integer> selected, int fontSize, boolean buttonGroup, BiConsumer<Integer, Boolean> change) {
      JPanel panel = new JPanel();
      panel.setLayout(new GridLayout(3, 3, 0, 0));
      ButtonGroup group = new ButtonGroup();
      String[] arrows = "<html>&#x2196;,↑,<html>&#x2197;,←,■,→,<html>&#x2199;,↓,<html>&#x2198;".split(",");
      WebToggleButton[] toggles = new WebToggleButton[9];
      Font font = new Font("Arial", 0, fontSize);
      IntStream.range(0, 9).forEach(i -> {
         toggles[i] = new WebToggleButton(arrows[i]);
         WebToggleButton tg = toggles[i];
         tg.setMargin(0);
         tg.setLeftRightSpacing(2);
         tg.setFont(font);
         tg.setName(String.valueOf(i));
         tg.setSelected(selected.contains(i));
         tg.setFocusable(false);
         tg.setShineColor(Values.COLOR_TRANSPARENT);
         tg.setShadeWidth(0);
         tg.addItemListener(evt -> change.accept(i, evt.getStateChange() == 1));
         if (buttonGroup) {
            group.add(tg);
         }

         panel.add(tg);
      });
      return panel;
   }

   public static JPanel nineDirectionsPanel(int selected, int fontSize, boolean buttonGroup, BiConsumer<Integer, Boolean> change) {
      return nineDirectionsPanel(Arrays.asList(selected), fontSize, buttonGroup, change);
   }
}
