package com.limagiran.holyrics.util;

import com.limagiran.holyrics.swing.BibleMainSelectableJLabel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.swing.JPanel;
import org.netbeans.lib.awtextra.AbsoluteConstraints;

public class BibleMainUtils {
   public static void addPages(List<BibleMainSelectableJLabel> list, JPanel page1, JPanel page2, Color color) {
      page1.removeAll();
      page2.removeAll();
      if (!list.isEmpty()) {
         if (list.size() <= 70) {
            addPanels(list, page1, color, true);
         } else {
            List<BibleMainSelectableJLabel> subList = new ArrayList<>();
            subList.addAll(list.subList(0, list.size() / 2));
            subList.add(BibleMainSelectableJLabel.createDefault("»"));
            addPanels(subList, page1, color, true);
            subList.clear();
            subList.addAll(list.subList(list.size() / 2, list.size()));
            subList.add(0, BibleMainSelectableJLabel.createDefault("«"));
            addPanels(subList, page2, color, true);
         }
      }
   }

   public static void addPanels(List<BibleMainSelectableJLabel> list, JPanel panel, Color c, boolean add) {
      Dimension size = panel.getSize();
      Insets in = panel.getBorder().getBorderInsets(panel);
      size.width = size.width - (in.left + in.right);
      size.height = size.height - (in.top + in.bottom);
      int[] grid = SwingUtils.getBestSquareGrid(size, list.size());
      Dimension d = new Dimension(size.width / grid[0], size.height / grid[1]);

      for (int i = 0; i < list.size(); i++) {
         int col = i % grid[0];
         int row = i / grid[0];
         int x = (int)(d.getWidth() * (double)col);
         int y = (int)(d.getHeight() * (double)row);
         AbsoluteConstraints ac = list.get(i).ac;
         ac.x = x + in.left;
         ac.y = y + in.top;
         ac.width = d.width + (col + 1 == grid[0] ? size.width - (x + d.width) : 0);
         ac.height = d.height + (row + 1 == grid[1] ? size.height - (y + d.height) : 0);
         if (add) {
            list.get(i).setBackground(c);
            panel.add(list.get(i), list.get(i).ac);
         }
      }

      panel.setMinimumSize(new Dimension());
      panel.revalidate();
      panel.repaint();
      String s = !list.isEmpty() ? list.get(list.size() - 1).getText() : "";
      SwingUtils.adjustFontOfAllJLabel(panel, 42, s.matches("^[\\d]{3}$") ? 0.15F : 0.35F);
      list.stream().filter(l -> l.getText().equals("»") || l.getText().equals("«")).forEach(l -> {
         Font f = new Font(l.getFont().getName(), l.getFont().getStyle(), 42);

         while ((float)l.getFontMetrics(f).stringWidth(l.getText()) > (float)l.getWidth() - (float)l.getWidth() * 0.5F) {
            f = new Font(f.getName(), f.getStyle(), f.getSize() - 1);
         }

         l.setFont(f);
      });
      panel.repaint();
   }

   public static void addAndResizePanels(JPanel panel) {
      List<BibleMainSelectableJLabel> list = Arrays.asList(panel.getComponents())
         .stream()
         .map(o -> o instanceof BibleMainSelectableJLabel ? (BibleMainSelectableJLabel)o : null)
         .filter(Objects::nonNull)
         .collect(Collectors.toList());
      addPanels(list, panel, null, false);
   }
}
