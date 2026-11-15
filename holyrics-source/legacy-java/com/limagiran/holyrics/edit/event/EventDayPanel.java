package com.limagiran.holyrics.edit.event;

import com.alee.laf.button.WebButton;
import com.alee.laf.scroll.WebScrollPane;
import com.limagiran.holyrics.control.EventController;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;

public class EventDayPanel extends JPanel {
   private static final Color COLOR_LIGHT = new Color(210, 210, 210);
   private static final ImageIcon IC_PLUS = new ImageIcon(Images.load("plus16.png"));
   private static final ImageIcon IC_PLUS_LIGHT = new ImageIcon(Images.load("plus16.png", 0.7F));
   private static final Border BORDER = BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(EventTable.COLOR_BORDER, 1), BorderFactory.createEmptyBorder(3, 7, 5, 7)
   );
   private static final Border BORDER_LIGHT = BorderFactory.createEmptyBorder(4, 8, 6, 8);
   private static final Border BORDER_TODAY = BorderFactory.createCompoundBorder(
      BorderFactory.createLineBorder(EventTable.COLOR_DARK.darker(), 2), BorderFactory.createEmptyBorder(3, 7, 5, 7)
   );
   private final EventTable table;
   private final Calendar date;
   private JButton jButtonCreate;
   private JLabel jLabelDayOfMonth;
   private JPanel jPanelDay;
   private JPanel jPanelHeader;
   private JScrollPane jScrollPaneDay;

   public EventDayPanel(EventTable table, Calendar date) {
      this.table = table;
      this.date = Calendar.getInstance();
      this.date.setTimeInMillis(date.getTimeInMillis());
      this.initComponents();
      this.init();
   }

   public void setDate(List<Event> worships, List<Event> events, Calendar date) {
      this.date.setTimeInMillis(date.getTimeInMillis());
      this.jLabelDayOfMonth.setText(String.valueOf(date.get(5)));
      this.setBackground(DateLG.isWeekend(date) ? EventTable.COLOR_LIGHT : EventTable.COLOR_DARK);
      this.jPanelDay.removeAll();
      List<JPanel> panels = new ArrayList<>();
      EventController.searchByDay(worships, date).forEach(e -> panels.add(new EventWorshipPanel(e, this.table)));
      EventController.searchByDay(events, date).forEach(e -> panels.add(new EventPanel(e, this.table)));
      panels.sort((o1, o2) -> {
         Calendar c1 = null;
         Calendar c2 = null;
         if (o1 instanceof EventWorshipPanel) {
            c1 = ((EventWorshipPanel)o1).event.getDate();
         } else if (o1 instanceof EventPanel) {
            c1 = ((EventPanel)o1).event.getDate();
         }

         if (o2 instanceof EventWorshipPanel) {
            c2 = ((EventWorshipPanel)o2).event.getDate();
         } else if (o2 instanceof EventPanel) {
            c2 = ((EventPanel)o2).event.getDate();
         }

         if (c1 == null) {
            return c2 == null ? 0 : -1;
         } else {
            return c2 == null ? 1 : c1.compareTo(c2);
         }
      });

      for (int i = 0; i < panels.size(); i++) {
         this.jPanelDay.add(panels.get(i), EventPanel.createConstraints(i, i == panels.size() - 1));
      }

      boolean month = this.table.isMonth(date);
      boolean today = DateLG.isToday(date);
      this.setLightForeground(!month);
      this.setBorder(today && month ? BORDER_TODAY : this.getBorder());
      this.setBackground(today && month ? EventTable.COLOR_DARK.darker() : this.getBackground());
      this.jPanelDay.revalidate();
   }

   private void init() {
      this.setBackground(DateLG.isWeekend(this.date) ? EventTable.COLOR_LIGHT : EventTable.COLOR_DARK);
      this.jLabelDayOfMonth.setText(String.valueOf(this.date.get(5)));
      this.jScrollPaneDay.getVerticalScrollBar().setPreferredSize(new Dimension());
      this.jScrollPaneDay.getVerticalScrollBar().setUnitIncrement(8);
      this.jPanelDay.setMinimumSize(new Dimension(80, 0));
      this.jPanelDay.revalidate();
      this.jScrollPaneDay.getViewport().setOpaque(false);
      this.jScrollPaneDay.getViewport().setFocusable(false);
      ((WebButton)this.jButtonCreate).setUndecorated(true);
      this.jButtonCreate.addActionListener(e -> this.table.create(this.getDateToCreate()));
   }

   private void initComponents() {
      this.jPanelHeader = new JPanel();
      this.jLabelDayOfMonth = new JLabel();
      this.jButtonCreate = new WebButton();
      this.jScrollPaneDay = new WebScrollPane(null, false);
      this.jPanelDay = new JPanel();
      this.setLayout(new BorderLayout());
      this.jPanelHeader.setOpaque(false);
      this.jPanelHeader.setLayout(new BorderLayout());
      this.jLabelDayOfMonth.setFont(new Font("Franklin Gothic Medium", 1, 13));
      this.jLabelDayOfMonth.setBorder(BorderFactory.createEmptyBorder(1, 5, 1, 5));
      this.jPanelHeader.add(this.jLabelDayOfMonth, "Center");
      this.jButtonCreate.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/plus16.png")));
      this.jButtonCreate.setMargin(new Insets(0, 0, 0, 0));
      this.jPanelHeader.add(this.jButtonCreate, "East");
      this.add(this.jPanelHeader, "First");
      this.jScrollPaneDay.setBorder(null);
      this.jScrollPaneDay.setFocusable(false);
      this.jScrollPaneDay.setMinimumSize(new Dimension(0, 0));
      this.jScrollPaneDay.setOpaque(false);
      this.jPanelDay.setFocusable(false);
      this.jPanelDay.setOpaque(false);
      this.jPanelDay.setLayout(new GridBagLayout());
      this.jScrollPaneDay.setViewportView(this.jPanelDay);
      this.add(this.jScrollPaneDay, "Center");
   }

   public Calendar getDate() {
      return (Calendar)this.date.clone();
   }

   public Calendar getDateToCreate() {
      Calendar _return = Calendar.getInstance();
      _return.setTimeInMillis(this.date.getTimeInMillis());
      _return.set(11, 19);
      _return.set(12, 0);
      return _return;
   }

   private void setLightForeground(boolean drawLightForeground) {
      this.jLabelDayOfMonth.setForeground(drawLightForeground ? StyleUI.FOREGROUND_LIGHT : StyleUI.FOREGROUND_DARK);
      this.jButtonCreate.setIcon(drawLightForeground ? IC_PLUS_LIGHT : IC_PLUS);
      this.setBackground(drawLightForeground ? COLOR_LIGHT : this.getBackground());
      this.setOpaque(!drawLightForeground);
      this.setBorder(drawLightForeground ? BORDER_LIGHT : BORDER);
      this.jLabelDayOfMonth.setFont(this.jLabelDayOfMonth.getFont().deriveFont(drawLightForeground ? 0 : 1));
      this.repaint();
   }
}
