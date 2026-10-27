package com.limagiran.holyrics.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.control.EventController;
import com.limagiran.holyrics.control.MemberController;
import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.control.WorshipController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Schedule;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.JLabelToolTip;
import com.limagiran.holyrics.swing.TableModelSchedule;
import com.limagiran.holyrics.swing.TableModelScheduleOnlyMember;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.TableToExcel;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WebLafUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;

public class ScheduleView extends JDialog {
   private static ScheduleView INSTANCE;
   private Runnable callback;
   private TableModelSchedule tableModelSchedule;
   private TableModelScheduleOnlyMember tableModelFirstColumn;
   private Calendar currentDate = Calendar.getInstance();
   private final JLabelToolTip toolTip = new JLabelToolTip();
   private int lastToolTipHeader = -1;
   private final Map<Schedule, Event> map = new HashMap<>();
   private final Point lastToolTipScheduleMember = new Point(-1, -1);
   private Filler filler1;
   private Filler filler3;
   private Filler filler4;
   private JButton jButtonNext;
   private JButton jButtonPrevious;
   private JButton jButtonSaveExcel;
   private JCheckBox jCheckBoxDisplayOnlyEnabledMember;
   private JCheckBox jCheckBoxDisplayOnlyEnabledWorship;
   private JLabel jLabelMonth;
   private JPanel jPanelButtons;
   private JPanel jPanelGeneral;
   private JPanel jPanelHeader;
   private JPanel jPanelMain;
   private JPanel jPanelSettings;
   private JScrollPane jScrollPaneFirstColumn;
   private JScrollPane jScrollPaneSchedule;
   private JTable jTableFirstColumn;
   private JTable jTableSchedule;

   public static void load() {
      SwingUtils.runOnUIThread(() -> INSTANCE = new ScheduleView());
   }

   private ScheduleView() {
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jCheckBoxDisplayOnlyEnabledMember.setText(this.jCheckBoxDisplayOnlyEnabledMember.getText() + " (" + I18N.WORD_MEMBER.val() + ")");
      this.jCheckBoxDisplayOnlyEnabledWorship.setText(this.jCheckBoxDisplayOnlyEnabledWorship.getText() + " (" + I18N.WORD_WORSHIP_EVENT.val() + ")");
      SwingUtils.maximize(this, 50);
      ((JComponent)this.jTableSchedule.getDefaultRenderer(Boolean.class)).setOpaque(true);
      this.toolTip.setFont(new Font("Georgia", 0, 14));
      this.jTableSchedule.addMouseListener(this.toolTip.getMouseExitedListener());
      this.jTableSchedule.getTableHeader().addMouseListener(this.toolTip.getMouseExitedListener());
      this.jTableSchedule.getTableHeader().addMouseMotionListener(this.getTableHeaderMouseMotion());
      ((JComponent)this.jTableFirstColumn.getDefaultRenderer(Boolean.class)).setOpaque(true);
      this.jTableFirstColumn.addMouseListener(this.toolTip.getMouseExitedListener());
      this.jScrollPaneFirstColumn.getVerticalScrollBar().setPreferredSize(new Dimension());
      this.tableModelFirstColumn = new TableModelScheduleOnlyMember(Settings.EnumSettings.SCHEDULE_VIEW_DISPLAY_ONLY_ENABLED_MEMBER.is());
      this.jTableFirstColumn.setModel(this.tableModelFirstColumn);
      TableModelScheduleOnlyMember.configTable(this.jTableFirstColumn);
      this.jTableFirstColumn.revalidate();
      this.repaint();
      this.jScrollPaneFirstColumn.setPreferredSize(new Dimension(this.jTableFirstColumn.getPreferredSize().width + 6, 5000));
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
      this.createAdjustmentListener(this.jScrollPaneFirstColumn, this.jScrollPaneSchedule);
      this.createAdjustmentListener(this.jScrollPaneSchedule, this.jScrollPaneFirstColumn);
      this.jScrollPaneFirstColumn.getVerticalScrollBar().addAdjustmentListener(e -> this.jScrollPaneSchedule.getVerticalScrollBar().setValue(e.getValue()));
      this.jScrollPaneSchedule.getVerticalScrollBar().addAdjustmentListener(e -> this.jScrollPaneFirstColumn.getVerticalScrollBar().setValue(e.getValue()));
      this.setupWebLaf();
      this.jButtonNext.addActionListener(e -> this.addMonth(1));
      this.jButtonPrevious.addActionListener(e -> this.addMonth(-1));
      this.jCheckBoxDisplayOnlyEnabledWorship.addItemListener(e -> {
         Settings.EnumSettings.SCHEDULE_VIEW_DISPLAY_ONLY_ENABLED_WORSHIP.set(e.getStateChange() == 1);
         this.refreshTable(this.currentDate);
      });
      this.jCheckBoxDisplayOnlyEnabledMember.addItemListener(e -> {
         Settings.EnumSettings.SCHEDULE_VIEW_DISPLAY_ONLY_ENABLED_MEMBER.set(e.getStateChange() == 1);
         this.refreshTable(this.currentDate);
      });
      ((WebButton)this.jButtonSaveExcel).setShadeWidth(0);
      this.jButtonSaveExcel.addActionListener(evt -> this.saveExcel());
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            ScheduleView.this.refreshTable(ScheduleView.this.currentDate);
            ScheduleView.this.tableModelFirstColumn.fireTableDataChanged();
         }
      });
   }

   private void setupWebLaf() {
      WebLafUtils.setupWebLafBorderLayout(this.jPanelHeader);
   }

   private void createAdjustmentListener(JScrollPane s1, JScrollPane s2) {
      s1.getVerticalScrollBar().addAdjustmentListener(e -> {
         if (s2.getVerticalScrollBar().getValue() != e.getValue()) {
            s2.getVerticalScrollBar().setValue(e.getValue());
         }
      });
   }

   private void initComponents() {
      this.jPanelMain = new JPanel();
      this.jPanelHeader = new WebPanel(true);
      this.jPanelButtons = new JPanel();
      this.filler4 = new Filler(new Dimension(88, 0), new Dimension(88, 0), new Dimension(88, 32767));
      this.filler3 = new Filler(new Dimension(48, 0), new Dimension(48, 0), new Dimension(48, 32767));
      this.jButtonPrevious = new JButton();
      this.jLabelMonth = new JLabel();
      this.jButtonNext = new JButton();
      this.filler1 = new Filler(new Dimension(48, 0), new Dimension(48, 0), new Dimension(48, 32767));
      this.jButtonSaveExcel = TableToExcel.createDefaultButton(false);
      this.jPanelSettings = new JPanel();
      this.jCheckBoxDisplayOnlyEnabledWorship = new JCheckBox();
      this.jCheckBoxDisplayOnlyEnabledMember = new JCheckBox();
      this.jPanelGeneral = new JPanel();
      this.jScrollPaneFirstColumn = new JScrollPane();
      this.jTableFirstColumn = this.createTableOnlyMember();
      this.jScrollPaneSchedule = new JScrollPane();
      this.jTableSchedule = this.createTableSchedule();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.schedule"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(800, 550));
      this.setModal(true);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            ScheduleView.this.formWindowClosing(evt);
         }
      });
      this.getContentPane().setLayout(new BorderLayout(0, 5));
      this.jPanelMain.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
      this.jPanelMain.setLayout(new BorderLayout());
      this.jPanelHeader.setLayout(new BorderLayout());
      this.jPanelButtons.setOpaque(false);
      this.jPanelButtons.setLayout(new FlowLayout(1, 15, 5));
      this.jPanelButtons.add(this.filler4);
      this.jPanelButtons.add(this.filler3);
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
      this.jPanelButtons.add(this.filler1);
      this.jButtonSaveExcel.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent evt) {
            ScheduleView.this.jButtonSaveExcelComponentResized(evt);
         }
      });
      this.jPanelButtons.add(this.jButtonSaveExcel);
      this.jPanelHeader.add(this.jPanelButtons, "First");
      this.jPanelSettings.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
      this.jPanelSettings.setOpaque(false);
      this.jPanelSettings.setLayout(new FlowLayout(1, 30, 0));
      this.jCheckBoxDisplayOnlyEnabledWorship.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxDisplayOnlyEnabledWorship.setSelected(Settings.EnumSettings.SCHEDULE_VIEW_DISPLAY_ONLY_ENABLED_WORSHIP.is());
      this.jCheckBoxDisplayOnlyEnabledWorship.setText(bundle.getString("msg.displayOnlyEnabled"));
      this.jCheckBoxDisplayOnlyEnabledWorship.setFocusable(false);
      this.jPanelSettings.add(this.jCheckBoxDisplayOnlyEnabledWorship);
      this.jCheckBoxDisplayOnlyEnabledMember.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxDisplayOnlyEnabledMember.setSelected(Settings.EnumSettings.SCHEDULE_VIEW_DISPLAY_ONLY_ENABLED_MEMBER.is());
      this.jCheckBoxDisplayOnlyEnabledMember.setText(bundle.getString("msg.displayOnlyEnabled"));
      this.jCheckBoxDisplayOnlyEnabledMember.setFocusable(false);
      this.jPanelSettings.add(this.jCheckBoxDisplayOnlyEnabledMember);
      this.jPanelHeader.add(this.jPanelSettings, "Center");
      this.jPanelMain.add(this.jPanelHeader, "First");
      this.jPanelGeneral.setLayout(new BorderLayout(-4, 0));
      this.jScrollPaneFirstColumn.setHorizontalScrollBarPolicy(32);
      this.jScrollPaneFirstColumn.setFocusable(false);
      this.jScrollPaneFirstColumn.setPreferredSize(new Dimension(250, 402));
      this.jTableFirstColumn.setFont(new Font("Georgia", 0, 13));
      this.jTableFirstColumn.setAutoResizeMode(0);
      this.jTableFirstColumn.setFocusable(false);
      this.jTableFirstColumn.setRowHeight(25);
      this.jTableFirstColumn.getTableHeader().setReorderingAllowed(false);
      this.jTableFirstColumn.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent evt) {
            ScheduleView.this.jTableFirstColumnMouseMoved(evt);
         }
      });
      this.jScrollPaneFirstColumn.setViewportView(this.jTableFirstColumn);
      this.jPanelGeneral.add(this.jScrollPaneFirstColumn, "West");
      this.jScrollPaneSchedule.setHorizontalScrollBarPolicy(32);
      this.jScrollPaneSchedule.setFocusable(false);
      this.jTableSchedule.setFont(new Font("Georgia", 0, 13));
      this.jTableSchedule.setAutoResizeMode(0);
      this.jTableSchedule.setFocusable(false);
      this.jTableSchedule.setRowHeight(25);
      this.jTableSchedule.getTableHeader().setReorderingAllowed(false);
      this.jScrollPaneSchedule.setViewportView(this.jTableSchedule);
      this.jPanelGeneral.add(this.jScrollPaneSchedule, "Center");
      this.jPanelMain.add(this.jPanelGeneral, "Center");
      this.getContentPane().add(this.jPanelMain, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.exit();
   }

   private void jTableFirstColumnMouseMoved(MouseEvent evt) {
      int r = this.jTableFirstColumn.rowAtPoint(evt.getPoint());
      int c = this.jTableFirstColumn.columnAtPoint(evt.getPoint());
      if (c == 0 && r > 0) {
         if (this.lastToolTipScheduleMember.x != c || this.lastToolTipScheduleMember.y != r) {
            this.lastToolTipScheduleMember.setLocation(c, r);
            Optional.ofNullable(this.tableModelSchedule.getMember(r)).ifPresent(p -> this.toolTip.setText(p.toStringFullHTML()));
         }

         this.toolTip.atRight();
         this.toolTip.atBottom();
         this.toolTip.refresh(this);
      } else {
         this.lastToolTipScheduleMember.setLocation(-1, -1);
         this.toolTip.setVisible(false);
      }
   }

   private void jButtonSaveExcelComponentResized(ComponentEvent evt) {
      this.filler4.setMinimumSize(new Dimension(this.jButtonSaveExcel.getWidth(), 0));
      this.filler4.setMaximumSize(new Dimension(this.jButtonSaveExcel.getWidth(), 32767));
      this.filler4.setPreferredSize(new Dimension(this.jButtonSaveExcel.getWidth(), 0));
   }

   public static void main(Runnable callback) {
      if (!INSTANCE.isVisible()) {
         SwingUtilities.invokeLater(() -> INSTANCE.show(callback));
      }
   }

   private void show(Runnable callback) {
      this.callback = callback;
      this.setVisible(true);
   }

   private void refreshTable(Calendar c) {
      try {
         this.jTableSchedule.getCellEditor().stopCellEditing();
      } catch (Exception var4) {
      }

      this.currentDate = c;
      this.map.clear();
      List<Schedule> list = new ArrayList<>();
      Consumer<Event> consumer = e -> {
         Schedule s = ScheduleController.get(e);
         this.map.put(s, e);
         list.add(s);
      };
      WorshipController.getWorships(this.jCheckBoxDisplayOnlyEnabledWorship.isSelected())
         .forEach(w -> WorshipController.daysOfMonthAsEvent(w, this.currentDate.get(2), this.currentDate.get(1)).forEach(consumer));
      EventController.search(this.currentDate.get(2), this.currentDate.get(1)).stream().forEach(consumer);
      list.sort(Schedule.sort());
      this.tableModelFirstColumn.setOnlyActive(this.jCheckBoxDisplayOnlyEnabledMember.isSelected());
      this.tableModelSchedule = new TableModelSchedule(MemberController.getAll(this.jCheckBoxDisplayOnlyEnabledMember.isSelected()), list);
      this.jTableSchedule.setModel(this.tableModelSchedule);
      TableModelSchedule.configTable(this.jTableSchedule);
      this.jTableSchedule.revalidate();
      this.updateLabelMonth();
      this.repaint();
      this.jTableSchedule.repaint();
   }

   private void updateLabelMonth() {
      this.jLabelMonth.setText(String.format("%s/%s", this.currentDate.getDisplayName(2, 2, Locale.getDefault()), this.currentDate.get(1)));
   }

   private void addMonth(int value) {
      this.currentDate.add(2, value);
      this.updateLabelMonth();
      this.refreshTable((Calendar)this.currentDate.clone());
   }

   private JTable createTableSchedule() {
      return new JTable() {
         private final EmptyBorder border = new EmptyBorder(0, 5, 0, 5);

         @Override
         public TableCellRenderer getCellRenderer(int row, int column) {
            return row == 0 ? this.getDefaultRenderer(Object.class) : this.getDefaultRenderer(Boolean.class);
         }

         @Override
         public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            c.setBackground(row % 2 == 0 ? Color.WHITE : StyleUI.COLOR_ALTERNATE_ROW);
            if (c instanceof JComponent) {
               ((JComponent)c).setBorder(this.border);
            }

            return c;
         }

         @Override
         public boolean isCellSelected(int row, int column) {
            return false;
         }
      };
   }

   private JTable createTableOnlyMember() {
      return new JTable() {
         private final EmptyBorder border = new EmptyBorder(0, 5, 0, 5);

         @Override
         public TableCellRenderer getCellRenderer(int row, int column) {
            return this.getDefaultRenderer(String.class);
         }

         @Override
         public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
            Component c = super.prepareRenderer(renderer, row, column);
            c.setBackground(row % 2 == 0 ? Color.WHITE : StyleUI.COLOR_ALTERNATE_ROW);
            if (c instanceof JComponent) {
               ((JComponent)c).setBorder(this.border);
            }

            return c;
         }

         @Override
         public boolean isCellSelected(int row, int column) {
            return false;
         }
      };
   }

   private void exit() {
      try {
         this.jTableSchedule.getCellEditor().stopCellEditing();
      } catch (Exception var5) {
      } finally {
         this.dispose();
         Optional.ofNullable(this.callback).ifPresent(r -> r.run());
      }
   }

   private MouseMotionAdapter getTableHeaderMouseMotion() {
      return new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent e) {
            int c = ScheduleView.this.jTableSchedule.getTableHeader().columnAtPoint(e.getPoint());
            if (c != ScheduleView.this.lastToolTipHeader) {
               ScheduleView.this.lastToolTipHeader = c;
               Schedule s = ScheduleView.this.tableModelSchedule.getSchedule(c);
               if (s != null) {
                  Event ev = ScheduleView.this.map.getOrDefault(s, new Event(Calendar.getInstance()));
                  List<Music> musics = s.getMusics();
                  if (!musics.isEmpty()) {
                     String format = "<b>%s</b><span style=\"font-size:80%%;line-height:250px;color: "
                        + Utils.getHexColor(StyleUI.FOREGROUND_LIGHT)
                        + "\">&nbsp;&nbsp;&nbsp;&nbsp;(%s)";
                     ScheduleView.this.toolTip
                        .setText(
                           String.format(
                              "<html><p><b>%s</p><br><p>%s: </b></p><br>%s<br>",
                              ev.getName(),
                              I18N.WORD_MUSICS.val(),
                              musics.stream()
                                 .map(m -> String.format(format, m.getTitle(), m.getArtist()))
                                 .collect(Collectors.joining("</p><br><p> • ", "<p> • ", "</p>"))
                           )
                        );
                  } else {
                     ScheduleView.this.toolTip.setText("<html><b>" + ev.getName() + "<br><br>" + I18N.MSG_NO_MUSICS.val());
                  }
               }
            }

            ScheduleView.this.toolTip.atLeft();
            ScheduleView.this.toolTip.atBottom();
            ScheduleView.this.toolTip.refresh(ScheduleView.this);
         }
      };
   }

   private void saveExcel() {
      TableToExcel.TableToExcelModel modelSchedule = new TableToExcel.TableToExcelModel() {
         @Override
         public int getRowCount() {
            return ScheduleView.this.tableModelSchedule.getRowCount();
         }

         @Override
         public int getColumnCount() {
            return ScheduleView.this.tableModelSchedule.getColumnCount() + 1;
         }

         @Override
         public String getColumnName(int col) {
            if (col == 0) {
               return I18N.WORD_MEMBER.val();
            } else {
               Schedule s = ScheduleView.this.tableModelSchedule.getSchedule(col - 1);
               if (s == null) {
                  return "";
               } else {
                  Calendar c = s.getDate();
                  return String.format(" %02d (%s) \n %s ", c.get(5), c.getDisplayName(7, 1, Locale.getDefault()), DateLG.SDF_T_S.format(c.getTime()));
               }
            }
         }

         @Override
         public Object getValue(int row, int col) {
            if (col == 0) {
               return row == 0 ? ScheduleView.this.tableModelFirstColumn.getValueAt(row, col) : ScheduleView.this.tableModelFirstColumn.getMemberName(row);
            } else {
               return ScheduleView.this.tableModelSchedule.getValueAt(row, col - 1);
            }
         }

         @Override
         public String getHeader() {
            return ScheduleView.this.jLabelMonth.getText();
         }

         @Override
         public String getSheetName() {
            return I18N.WORD_SCHEDULE.val();
         }
      };
      TableToExcel.TableToExcelModel modelMusicsAndAnnotations = new TableToExcel.TableToExcelModel() {
         @Override
         public int getRowCount() {
            return ScheduleView.this.tableModelSchedule.getColumnCount();
         }

         @Override
         public int getColumnCount() {
            return 3;
         }

         public String getEventName(Schedule s) {
            Event ev = ScheduleView.this.map.get(s);
            return ev != null && !ev.getName().trim().isEmpty() ? ev.getName() + "\n" : "";
         }

         @Override
         public String getColumnName(int col) {
            switch (col) {
               case 0:
                  return I18N.WORD_WORSHIP_AND_EVENT.val();
               case 1:
                  return I18N.WORD_MUSICS.val();
               case 2:
                  return I18N.WORD_NOTES.val();
               default:
                  return "";
            }
         }

         @Override
         public Object getValue(int row, int col) {
            Schedule s = ScheduleView.this.tableModelSchedule.getSchedule(row);
            if (s == null) {
               return "";
            } else {
               switch (col) {
                  case 0:
                     Calendar c = s.getDate();
                     return String.format(
                        "%s %02d (%s) - %s", this.getEventName(s), c.get(5), c.getDisplayName(7, 2, Locale.getDefault()), DateLG.SDF_T_S.format(c.getTime())
                     );
                  case 1:
                     List<Music> musics = s.getMusics();
                     if (musics.isEmpty()) {
                        return I18N.MSG_NO_MUSICS.val();
                     }

                     return musics.stream().map(m -> String.format("%s (%s)", m.getTitle(), m.getArtist())).collect(Collectors.joining("\n"));
                  case 2:
                     return s.getNotes();
                  default:
                     return "";
               }
            }
         }

         @Override
         public String getHeader() {
            return ScheduleView.this.jLabelMonth.getText();
         }

         @Override
         public String getSheetName() {
            return I18N.WORD_MUSICS.val() + " - " + I18N.WORD_NOTES.val();
         }
      };
      TableToExcel.create(modelSchedule, modelMusicsAndAnnotations);
   }
}
