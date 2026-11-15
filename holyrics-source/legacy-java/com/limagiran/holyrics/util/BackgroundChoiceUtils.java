package com.limagiran.holyrics.util;

import com.alee.laf.colorchooser.PaletteColorChooser;
import com.alee.laf.colorchooser.WebColorChooserPanel;
import com.limagiran.holyrics.event.BackgroundChoiceListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.BackgroundColor;
import com.limagiran.holyrics.swing.SelectablePanel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Field;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

public class BackgroundChoiceUtils {
   public static JButton createButtonAddBackground(ActionListener action, String text, Dimension size) {
      ImageIcon icon = new ImageIcon(BackgroundChoiceUtils.class.getResource("/com/limagiran/holyrics/img/imgFind.png"));
      icon = new ImageIcon(Img.opacity(icon.getImage(), 0.8F));
      JButton b = new JButton(SwingUtils.getHTMLCenterJLabel(" " + text + " "), icon);
      b.setPreferredSize(size);
      b.setMaximumSize(size);
      b.setFont(new Font("Tahoma", 1, 15));
      b.setHorizontalTextPosition(0);
      b.setForeground(new Color(80, 80, 80, 200));
      b.setMargin(new Insets(0, 0, 0, 0));
      b.setBorder(new LineBorder(new Color(80, 80, 80)));
      b.addActionListener(action);
      return b;
   }

   public static void changeSelectedPanel(Component c, List<SelectablePanel> list) {
      if (c instanceof SelectablePanel) {
         list.stream().filter(SelectablePanel::isSelected).forEach(v -> v.setSelected(false));
         ((SelectablePanel)c).setSelected(true);
      }
   }

   public static <T extends Component & BackgroundChoiceListener> void colorSelected(T t, Color c) {
      if (c == null) {
         Utils.error(t, I18N.MSG_ERROR_COLOR_NOT_SELECTED.val());
      } else {
         t.onSelected(new BackgroundColor(c));
      }
   }

   public static void setupWebColorChooserPanel(WebColorChooserPanel panel, final Runnable run) {
      try {
         Field f = WebColorChooserPanel.class.getDeclaredField("palette");
         f.setAccessible(true);
         PaletteColorChooser pallete = (PaletteColorChooser)f.get(panel);
         pallete.getColorChooser().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
               if (Utils.isDoubleClick(e)) {
                  run.run();
               }
            }
         });
      } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchFieldException var4) {
      }
   }
}
