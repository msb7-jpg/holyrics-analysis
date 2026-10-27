package com.limagiran.holyrics.report.view;

import com.alee.extended.date.WebDateField;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.control.ProfileController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.report.model.PlayedMusics;
import com.limagiran.holyrics.report.model.TableModelPlayedMusic;
import com.limagiran.holyrics.swing.JLabelToolTip;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.EnumWorship;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.TableToExcel;
import com.limagiran.holyrics.util.UICreate;
import com.limagiran.holyrics.util.WebLafUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;

public class ReportPlayedMusicsView extends JDialog {
   private static ReportPlayedMusicsView INSTANCE;
   private final WebDateField webDateFieldStart = new WebDateField();
   private final WebDateField webDateFieldEnd = new WebDateField();
   private final TableModelPlayedMusic tableModel;
   private final List<PlayedMusics> list = new ArrayList<>();
   private final Map<JCheckBox, EnumWorship.EnumWorshipDays> map = new HashMap<>();
   private final JLabelToolTip toolTip = new JLabelToolTip();
   private Filler filler1;
   private Filler filler2;
   private JButton jButtonSaveExcel;
   private JLabel jLabelEnd;
   private JLabel jLabelStart;
   private JPanel jPanelDates;
   private JPanel jPanelDaysList;
   private JPanel jPanelFilter;
   private JPanel jPanelGeneral;
   private JPanel jPanelReport;
   private JScrollPane jScrollPaneReport;
   private JTable jTableReport;

   public ReportPlayedMusicsView() {
      super((Window)null);
      this.initComponents();
      this.init();
      this.tableModel = new TableModelPlayedMusic(this.list);
      this.jTableReport.setModel(this.tableModel);
      TableModelPlayedMusic.configTable(this.jTableReport);
   }

   private void init() {
      this.webDateFieldStart.addKeyListener(SwingUtils.escToExit(this::exit));
      this.webDateFieldEnd.addKeyListener(SwingUtils.escToExit(this::exit));
      this.webDateFieldStart.setCalendarCustomizer(calendar -> calendar.setStartWeekFromSunday(true));
      this.webDateFieldEnd.setCalendarCustomizer(calendar -> calendar.setStartWeekFromSunday(true));
      this.webDateFieldEnd.setDate(new Date());
      this.jPanelDates.add(this.webDateFieldStart, 1);
      this.jPanelDates.add(this.webDateFieldEnd, 4);
      Calendar c = Calendar.getInstance();
      c.add(2, -3);
      this.webDateFieldStart.setDate(new Date(c.getTimeInMillis()));
      this.webDateFieldStart.addDateSelectionListener(date -> {
         this.list.forEach(p -> p.setStart(date));
         this.refresh();
      });
      this.webDateFieldEnd.addDateSelectionListener(date -> {
         this.list.forEach(p -> p.setEnd(date));
         this.refresh();
      });
      this.list.add(new PlayedMusics(I18N.WORD_ALL.val(), MusicDao.getAll(), this.webDateFieldStart.getDate(), this.webDateFieldEnd.getDate()));
      this.list
         .addAll(
            ProfileController.getProfilesInOrderOfAmount(false)
               .stream()
               .map(p -> new PlayedMusics(p.getName(), p.getMusics(), this.webDateFieldStart.getDate(), this.webDateFieldEnd.getDate()))
               .collect(Collectors.toList())
         );
      Arrays.asList(EnumWorship.EnumWorshipDays.values()).stream().forEach(en -> {
         JCheckBox chkbx = new JCheckBox();
         chkbx.setFont(new Font("Georgia", 0, 12));
         chkbx.setSelected(true);
         chkbx.setText(en.toString());
         chkbx.setFocusable(false);
         chkbx.setHorizontalAlignment(0);
         this.map.put(chkbx, en);
         chkbx.addItemListener(e -> {
            this.list.forEach(p -> p.filter(this.map.get(chkbx), e.getStateChange() == 1));
            this.refresh();
         });
         this.jPanelDaysList.add(chkbx);
      });
      SwingUtils.maximize(this, 50);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
      this.jButtonSaveExcel.addActionListener(evt -> this.saveExcel());
      WebLafUtils.setupWebLafBorderLayout(this.jPanelFilter);
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            ReportPlayedMusicsView.this.refresh();
         }
      });
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanelFilter = new WebPanel(true);
      this.jPanelDates = new JPanel();
      this.jLabelStart = new JLabel();
      this.filler1 = new Filler(new Dimension(25, 0), new Dimension(25, 0), new Dimension(25, 32767));
      this.jLabelEnd = new JLabel();
      this.filler2 = new Filler(new Dimension(48, 0), new Dimension(48, 0), new Dimension(48, 32767));
      this.jButtonSaveExcel = TableToExcel.createDefaultButton(true);
      this.jPanelDaysList = new JPanel();
      this.jPanelReport = new JPanel();
      this.jScrollPaneReport = new JScrollPane();
      this.jTableReport = UICreate.jTableAlternateRow();
      this.setDefaultCloseOperation(2);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.report")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(770, 475));
      this.setModal(true);
      this.jPanelFilter.setMaximumSize(new Dimension(170, 32767));
      this.jPanelFilter.setLayout(new BorderLayout());
      this.jPanelDates.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelDates.setFont(new Font("Georgia", 0, 12));
      this.jPanelDates.setOpaque(false);
      this.jPanelDates.setLayout(new FlowLayout(1, 5, 2));
      this.jLabelStart.setBackground(new Color(0, 0, 0));
      this.jLabelStart.setFont(new Font("Georgia", 0, 12));
      this.jLabelStart.setHorizontalAlignment(4);
      this.jLabelStart.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.start")));
      this.jPanelDates.add(this.jLabelStart);
      this.jPanelDates.add(this.filler1);
      this.jLabelEnd.setFont(new Font("Georgia", 0, 12));
      this.jLabelEnd.setHorizontalAlignment(4);
      this.jLabelEnd.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.end")));
      this.jPanelDates.add(this.jLabelEnd);
      this.jPanelDates.add(this.filler2);
      this.jPanelDates.add(this.jButtonSaveExcel);
      this.jPanelFilter.add(this.jPanelDates, "First");
      this.jPanelDaysList.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelDaysList.setOpaque(false);
      this.jPanelDaysList.setLayout(new GridLayout(1, 7));
      this.jPanelFilter.add(this.jPanelDaysList, "Last");
      this.jTableReport.setFont(new Font("Lucida Console", 0, 15));
      this.jTableReport.setModel(new DefaultTableModel(new Object[0][], new String[0]));
      this.jTableReport.setRowHeight(24);
      this.jTableReport.getTableHeader().setReorderingAllowed(false);
      this.jTableReport.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent evt) {
            ReportPlayedMusicsView.this.jTableReportMouseMoved(evt);
         }
      });
      this.jTableReport.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseExited(MouseEvent evt) {
            ReportPlayedMusicsView.this.jTableReportMouseExited(evt);
         }
      });
      this.jScrollPaneReport.setViewportView(this.jTableReport);
      GroupLayout jPanelReportLayout = new GroupLayout(this.jPanelReport);
      this.jPanelReport.setLayout(jPanelReportLayout);
      jPanelReportLayout.setHorizontalGroup(jPanelReportLayout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPaneReport));
      jPanelReportLayout.setVerticalGroup(
         jPanelReportLayout.createParallelGroup(Alignment.LEADING).addComponent(this.jScrollPaneReport, Alignment.TRAILING, -1, 521, 32767)
      );
      GroupLayout jPanelGeneralLayout = new GroupLayout(this.jPanelGeneral);
      this.jPanelGeneral.setLayout(jPanelGeneralLayout);
      jPanelGeneralLayout.setHorizontalGroup(
         jPanelGeneralLayout.createParallelGroup(Alignment.LEADING)
            .addComponent(this.jPanelReport, -1, -1, 32767)
            .addComponent(this.jPanelFilter, -1, -1, 32767)
      );
      jPanelGeneralLayout.setVerticalGroup(
         jPanelGeneralLayout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               Alignment.TRAILING,
               jPanelGeneralLayout.createSequentialGroup()
                  .addComponent(this.jPanelFilter, -2, -1, -2)
                  .addGap(2, 2, 2)
                  .addComponent(this.jPanelReport, -1, -1, 32767)
            )
      );
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addGap(4, 4, 4).addComponent(this.jPanelGeneral, -1, -1, 32767).addGap(4, 4, 4))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addGap(4, 4, 4).addComponent(this.jPanelGeneral, -1, -1, 32767).addGap(4, 4, 4))
      );
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jTableReportMouseMoved(MouseEvent evt) {
      int col = this.jTableReport.columnAtPoint(evt.getPoint());
      int row = this.jTableReport.rowAtPoint(evt.getPoint());
      if (this.tableModel.getPlayedMusic(col, row) != null) {
         this.toolTip.setText(this.tableModel.getPlayedMusic(col, row));
         this.toolTip.refresh(this, !this.toolTip.getText().trim().isEmpty());
      } else {
         this.toolTip.setVisible(false);
      }
   }

   private void jTableReportMouseExited(MouseEvent evt) {
      this.toolTip.setVisible(false);
   }

   public static void main() {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.setVisible(false);
            INSTANCE.dispose();
            INSTANCE.list.clear();
            INSTANCE = null;
         }

         INSTANCE = new ReportPlayedMusicsView();
         INSTANCE.setVisible(true);
      });
   }

   private void refresh() {
      if (this.tableModel != null) {
         this.tableModel.refresh();
      }
   }

   private void exit() {
      super.setVisible(false);
      super.dispose();
   }

   private void saveExcel() {
      TableToExcel.TableToExcelModel model = new TableToExcel.TableToExcelModel() {
         @Override
         public int getRowCount() {
            return ReportPlayedMusicsView.this.tableModel.getRowCount();
         }

         @Override
         public int getColumnCount() {
            return ReportPlayedMusicsView.this.tableModel.getColumnCount();
         }

         @Override
         public String getColumnName(int col) {
            return ReportPlayedMusicsView.this.tableModel.getColumnName(col);
         }

         @Override
         public Object getValue(int row, int col) {
            return ReportPlayedMusicsView.this.tableModel.getValueAt(row, col);
         }

         @Override
         public String getHeader() {
            return String.format(
               "%s --- %s",
               DateLG.SDF_D_S.format(ReportPlayedMusicsView.this.webDateFieldStart.getDate()),
               DateLG.SDF_D_S.format(ReportPlayedMusicsView.this.webDateFieldEnd.getDate())
            );
         }

         @Override
         public String getSheetName() {
            return I18N.WORD_MUSICS.val();
         }

         @Override
         public boolean isAlignHCenter() {
            return false;
         }
      };
      TableToExcel.create(model);
   }
}
