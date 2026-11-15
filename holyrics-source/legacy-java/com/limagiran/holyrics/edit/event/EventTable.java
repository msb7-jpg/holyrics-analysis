package com.limagiran.holyrics.edit.event;

import com.limagiran.holyrics.control.EventController;
import com.limagiran.holyrics.control.WorshipController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.EnumWorship;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class EventTable extends JPanel {
   public static final Color COLOR_LIGHT = new Color(252, 252, 252);
   public static final Color COLOR_DARK = new Color(210, 210, 210);
   public static final Color COLOR_BORDER = COLOR_DARK.darker();
   private final Calendar month = Calendar.getInstance();
   private final List<EventDayPanel> days = new ArrayList<>();
   private JButton jButtonNext;
   private JButton jButtonPrevious;
   private JCheckBox jCheckBoxDisplayOnlyEnabled;
   private JLabel jLabelMonth;
   private JPanel jPanelButtons;
   private JPanel jPanelDays;
   private JPanel jPanelGrid;
   private JPanel jPanelHeader;
   private JPanel jPanelSettings;

   public EventTable() {
      this.initComponents();
      this.init();
   }

   private void init() {
      Arrays.asList(EnumWorship.EnumWorshipDays.values()).forEach(en -> {
         JLabel label = new JLabel(String.valueOf(en.day.getDisplayName(TextStyle.SHORT, Locale.getDefault())));
         boolean flag = en.equals(EnumWorship.EnumWorshipDays.SUN) || en.equals(EnumWorship.EnumWorshipDays.SAT);
         label.setForeground(flag ? StyleUI.FOREGROUND_DARK : StyleUI.FOREGROUND_LIGHT);
         label.setOpaque(true);
         label.setBackground(flag ? COLOR_LIGHT : COLOR_DARK);
         label.setFont(new Font("Georgia", 1, 20));
         label.setHorizontalAlignment(0);
         label.setBorder(new CompoundBorder(new LineBorder(COLOR_BORDER, 1), new EmptyBorder(5, 5, 5, 5)));
         this.jPanelDays.add(label);
      });
      IntStream.range(0, 42).mapToObj(i -> new EventDayPanel(this, Calendar.getInstance())).forEach(this.days::add);
      this.days.forEach(this.jPanelGrid::add);
      this.jButtonNext.addActionListener(e -> this.addMonth(1));
      this.jButtonPrevious.addActionListener(e -> this.addMonth(-1));
      this.jCheckBoxDisplayOnlyEnabled.addItemListener(e -> {
         Settings.EnumSettings.EVENT_TABLE_DISPLAY_ONLY_ENABLED.set(e.getStateChange() == 1);
         this.refresh();
      });
      this.jCheckBoxDisplayOnlyEnabled.setText(this.jCheckBoxDisplayOnlyEnabled.getText() + " (" + I18N.WORD_WORSHIP_EVENT.val() + ")");
      this.refresh();
   }

   private void initComponents() {
      this.jPanelHeader = new JPanel();
      this.jPanelButtons = new JPanel();
      this.jButtonPrevious = new JButton();
      this.jLabelMonth = new JLabel();
      this.jButtonNext = new JButton();
      this.jPanelSettings = new JPanel();
      this.jCheckBoxDisplayOnlyEnabled = new JCheckBox();
      this.jPanelDays = new JPanel();
      this.jPanelGrid = new JPanel();
      this.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      this.setOpaque(false);
      this.setLayout(new BorderLayout());
      this.jPanelHeader.setOpaque(false);
      this.jPanelHeader.setLayout(new BorderLayout());
      this.jPanelButtons.setOpaque(false);
      this.jPanelButtons.setLayout(new FlowLayout(1, 15, 5));
      this.jButtonPrevious.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/previous24.png")));
      this.jButtonPrevious.setFocusable(false);
      this.jPanelButtons.add(this.jButtonPrevious);
      this.jLabelMonth.setFont(new Font("Georgia", 0, 24));
      this.jLabelMonth.setHorizontalAlignment(0);
      this.jLabelMonth.setPreferredSize(new Dimension(250, 40));
      this.jPanelButtons.add(this.jLabelMonth);
      this.jButtonNext.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/next24.png")));
      this.jButtonNext.setFocusable(false);
      this.jPanelButtons.add(this.jButtonNext);
      this.jPanelHeader.add(this.jPanelButtons, "First");
      this.jPanelSettings.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
      this.jPanelSettings.setOpaque(false);
      this.jPanelSettings.setLayout(new FlowLayout(1, 5, 0));
      this.jCheckBoxDisplayOnlyEnabled.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxDisplayOnlyEnabled.setSelected(Settings.EnumSettings.EVENT_TABLE_DISPLAY_ONLY_ENABLED.is());
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jCheckBoxDisplayOnlyEnabled.setText(bundle.getString("msg.displayOnlyEnabled"));
      this.jCheckBoxDisplayOnlyEnabled.setFocusable(false);
      this.jPanelSettings.add(this.jCheckBoxDisplayOnlyEnabled);
      this.jPanelHeader.add(this.jPanelSettings, "Center");
      this.jPanelDays.setOpaque(false);
      this.jPanelDays.setLayout(new GridLayout(1, 7));
      this.jPanelHeader.add(this.jPanelDays, "Last");
      this.add(this.jPanelHeader, "First");
      this.jPanelGrid.setOpaque(false);
      this.jPanelGrid.setLayout(new GridLayout(6, 7));
      this.add(this.jPanelGrid, "Center");
   }

   protected void fireUpdate(Event e) {
      if (this.days.stream().noneMatch(d -> DateLG.equalDays(d.getDate(), e.getDate()))) {
         this.month.setTimeInMillis(e.getDate().getTimeInMillis());
      }

      this.refresh();
   }

   public void refresh() {
      Calendar start = this.getStart();
      List<Event> worships = WorshipController.searchAsEventMonthAndPreviousAndNext(
         this.month.get(2), this.month.get(1), this.jCheckBoxDisplayOnlyEnabled.isSelected()
      );
      List<Event> events = EventController.search3Months(this.month.get(2), this.month.get(1));
      this.days.stream().peek(e -> start.add(5, 1)).forEach(ev -> ev.setDate(worships, events, start));
      this.jLabelMonth.setText(String.format("%s/%s", this.month.getDisplayName(2, 2, Locale.getDefault()), this.month.get(1)));
      this.revalidate();
      this.repaint();
   }

   protected void create(Calendar date) {
      EventFormCreate.main(this, new EventPanel(new Event(date), this));
   }

   private Calendar getStart() {
      this.month.set(5, 1);
      Calendar start = (Calendar)this.month.clone();
      start.add(5, -this.month.get(7));
      return start;
   }

   public boolean isMonth(Calendar date) {
      return date.get(2) == this.month.get(2);
   }

   private void addMonth(int value) {
      this.month.add(2, value);
      this.refresh();
   }
}
