package com.limagiran.holyrics.report.view;

import com.alee.extended.date.WebDateField;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.control.MemberController;
import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.report.model.ReportScheduleMember;
import com.limagiran.holyrics.report.model.ReportScheduleMusic;
import com.limagiran.holyrics.report.model.TableModelReportScheduleMember;
import com.limagiran.holyrics.report.model.TableModelReportScheduleMusic;
import com.limagiran.holyrics.swing.JLabelToolTip;
import com.limagiran.holyrics.util.DateLG;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.TableToExcel;
import com.limagiran.holyrics.util.UICreate;
import com.limagiran.holyrics.util.WebLafUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.GroupLayout.Alignment;
import javax.swing.table.DefaultTableModel;

public class ReportScheduleView extends JDialog {
   private static ReportScheduleView INSTANCE;
   private final WebDateField webDateFieldStart = new WebDateField();
   private final WebDateField webDateFieldEnd = new WebDateField();
   private final TableModelReportScheduleMember tableModelMember;
   private final TableModelReportScheduleMusic tableModelMusic;
   private final List<ReportScheduleMember> listMember = new ArrayList<>();
   private final List<ReportScheduleMusic> listMusic = new ArrayList<>();
   private final JLabelToolTip toolTip = new JLabelToolTip();
   private Filler filler1;
   private Filler filler2;
   private JButton jButtonSaveExcel;
   private JLabel jLabelEnd;
   private JLabel jLabelStart;
   private JPanel jPanelDates;
   private JPanel jPanelGeneral;
   private JPanel jPanelReport;
   private JPanel jPanelSplitLeft;
   private JPanel jPanelSplitRight;
   private JScrollPane jScrollPaneReportMember;
   private JScrollPane jScrollPaneReportMusics;
   private JSplitPane jSplitPane;
   private JTable jTableReportMember;
   private JTable jTableReportMusic;

   private ReportScheduleView() {
      super((Window)null);
      this.initComponents();
      this.init();
      this.tableModelMember = new TableModelReportScheduleMember(this.listMember);
      this.jTableReportMember.setModel(this.tableModelMember);
      TableModelReportScheduleMember.configTable(this.jTableReportMember);
      this.tableModelMusic = new TableModelReportScheduleMusic(this.listMusic);
      this.jTableReportMusic.setModel(this.tableModelMusic);
      TableModelReportScheduleMusic.configTable(this.jTableReportMusic);
      this.jSplitPane.setDividerLocation(0.5);
   }

   private void init() {
      this.webDateFieldStart.addKeyListener(SwingUtils.escToExit(this::exit));
      this.webDateFieldEnd.addKeyListener(SwingUtils.escToExit(this::exit));
      this.webDateFieldStart.setCalendarCustomizer(calendar -> calendar.setStartWeekFromSunday(true));
      this.webDateFieldEnd.setCalendarCustomizer(calendar -> calendar.setStartWeekFromSunday(true));
      this.webDateFieldStart.addDateSelectionListener(date -> {
         this.listMember.forEach(p -> p.setStart(date));
         this.listMusic.forEach(p -> p.setStart(date));
         this.refresh();
      });
      this.webDateFieldEnd.addDateSelectionListener(date -> {
         this.listMember.forEach(p -> p.setEnd(date));
         this.listMusic.forEach(p -> p.setEnd(date));
         this.refresh();
      });
      this.jPanelDates.add(this.webDateFieldStart, 1);
      this.jPanelDates.add(this.webDateFieldEnd, 4);
      Calendar c = Calendar.getInstance();
      c.add(2, -2);
      this.webDateFieldStart.setDate(new Date(c.getTimeInMillis()));
      c.add(2, 4);
      this.webDateFieldEnd.setDate(new Date(c.getTimeInMillis()));
      SwingUtils.maximize(this, 50);
      this.jScrollPaneReportMember.getVerticalScrollBar().addAdjustmentListener(e -> this.jScrollPaneReportMember.repaint());
      this.jTableReportMember.repaint();
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
      WebLafUtils.setupWebLafLayout(new FlowLayout(1, 5, 2), this.jPanelDates);
      this.jButtonSaveExcel.addActionListener(evt -> this.saveExcel());
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            ReportScheduleView.this.initWorker();
         }
      });
   }

   private void initWorker() {
      PopUpWorker.exe(
         new MySwingWorker<Void, Void>(this) {
            protected Void doInBackground() throws Exception {
               ReportScheduleView.this.listMember.clear();
               ReportScheduleView.this.listMusic.clear();
               ReportScheduleView.this.listMember
                  .addAll(
                     MemberController.getAll(false)
                        .stream()
                        .map(
                           m -> new ReportScheduleMember(
                                 m, ReportScheduleView.this.webDateFieldStart.getDate(), ReportScheduleView.this.webDateFieldEnd.getDate()
                              )
                        )
                        .collect(Collectors.toList())
                  );
               ReportScheduleView.this.listMusic
                  .addAll(
                     MusicDao.getAll()
                        .parallelStream()
                        .map(
                           m -> new ReportScheduleMusic(
                                 m, ReportScheduleView.this.webDateFieldStart.getDate(), ReportScheduleView.this.webDateFieldEnd.getDate()
                              )
                        )
                        .collect(Collectors.toList())
                  );
               return null;
            }

            @Override
            protected void done() {
               super.done();
               ReportScheduleView.this.webDateFieldStart.requestFocus();
               ReportScheduleView.this.refresh();
            }
         }
      );
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanelDates = new WebPanel(true);
      this.jLabelStart = new JLabel();
      this.filler1 = new Filler(new Dimension(25, 0), new Dimension(25, 0), new Dimension(25, 32767));
      this.jLabelEnd = new JLabel();
      this.filler2 = new Filler(new Dimension(48, 0), new Dimension(48, 0), new Dimension(48, 32767));
      this.jButtonSaveExcel = TableToExcel.createDefaultButton(true);
      this.jPanelReport = new JPanel();
      this.jSplitPane = new JSplitPane();
      this.jPanelSplitLeft = new JPanel();
      this.jScrollPaneReportMember = new JScrollPane();
      this.jTableReportMember = UICreate.jTableAlternateRow();
      this.jPanelSplitRight = new JPanel();
      this.jScrollPaneReportMusics = new JScrollPane();
      this.jTableReportMusic = UICreate.jTableAlternateRow();
      this.setDefaultCloseOperation(2);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.report")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(770, 475));
      this.jPanelGeneral.setLayout(new BorderLayout());
      this.jPanelDates.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelDates.setFont(new Font("Georgia", 0, 12));
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
      this.jPanelGeneral.add(this.jPanelDates, "First");
      this.jPanelReport.setLayout(new BorderLayout());
      this.jSplitPane.setDividerLocation(400);
      this.jSplitPane.setDividerSize(3);
      this.jSplitPane.setResizeWeight(0.5);
      this.jPanelSplitLeft.setLayout(new BorderLayout());
      this.jScrollPaneReportMember.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent evt) {
            ReportScheduleView.this.jScrollPaneReportMemberComponentShown(evt);
         }
      });
      this.jTableReportMember.setFont(new Font("Lucida Console", 0, 16));
      this.jTableReportMember.setFocusable(false);
      this.jTableReportMember.setRowHeight(25);
      this.jTableReportMember.getTableHeader().setReorderingAllowed(false);
      this.jTableReportMember.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent evt) {
            ReportScheduleView.this.jTableReportMemberMouseMoved(evt);
         }
      });
      this.jTableReportMember.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseExited(MouseEvent evt) {
            ReportScheduleView.this.jTableReportMemberMouseExited(evt);
         }
      });
      this.jScrollPaneReportMember.setViewportView(this.jTableReportMember);
      this.jPanelSplitLeft.add(this.jScrollPaneReportMember, "Center");
      this.jSplitPane.setLeftComponent(this.jPanelSplitLeft);
      this.jPanelSplitRight.setLayout(new BorderLayout());
      this.jTableReportMusic.setFont(new Font("Lucida Console", 0, 16));
      this.jTableReportMusic.setModel(new DefaultTableModel(new Object[0][], new String[0]));
      this.jTableReportMusic.setFocusable(false);
      this.jTableReportMusic.setRowHeight(25);
      this.jTableReportMusic.getTableHeader().setReorderingAllowed(false);
      this.jTableReportMusic.addMouseMotionListener(new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent evt) {
            ReportScheduleView.this.jTableReportMusicMouseMoved(evt);
         }
      });
      this.jTableReportMusic.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseExited(MouseEvent evt) {
            ReportScheduleView.this.jTableReportMusicMouseExited(evt);
         }
      });
      this.jScrollPaneReportMusics.setViewportView(this.jTableReportMusic);
      this.jPanelSplitRight.add(this.jScrollPaneReportMusics, "Center");
      this.jSplitPane.setRightComponent(this.jPanelSplitRight);
      this.jPanelReport.add(this.jSplitPane, "Center");
      this.jPanelGeneral.add(this.jPanelReport, "Center");
      GroupLayout layout = new GroupLayout(this.getContentPane());
      this.getContentPane().setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addGap(4, 4, 4).addComponent(this.jPanelGeneral, -1, 1032, 32767).addGap(4, 4, 4))
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(layout.createSequentialGroup().addGap(4, 4, 4).addComponent(this.jPanelGeneral, -1, -1, 32767).addGap(4, 4, 4))
      );
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jTableReportMemberMouseMoved(MouseEvent evt) {
      int r = this.jTableReportMember.rowAtPoint(evt.getPoint());
      int c = this.jTableReportMember.columnAtPoint(evt.getPoint());
      Member p = this.tableModelMember.getMember(r);
      if (c == 0 && p != null) {
         this.toolTip.atRight();
         this.toolTip.setText(p.toStringFullHTML());
         this.toolTip.refresh(this);
      } else {
         this.toolTip.setVisible(false);
      }
   }

   private void jScrollPaneReportMemberComponentShown(ComponentEvent evt) {
      this.jTableReportMember.repaint();
   }

   private void jTableReportMemberMouseExited(MouseEvent evt) {
      this.toolTip.setVisible(false);
   }

   private void jTableReportMusicMouseExited(MouseEvent evt) {
      this.toolTip.setVisible(false);
   }

   private void jTableReportMusicMouseMoved(MouseEvent evt) {
      int r = this.jTableReportMusic.rowAtPoint(evt.getPoint());
      int c = this.jTableReportMusic.columnAtPoint(evt.getPoint());
      ReportScheduleMusic m = this.tableModelMusic.getRow(r);
      if (c == 1 && m != null && !m.getToolTipText().isEmpty()) {
         this.toolTip.atLeft();
         this.toolTip.setText(m.getToolTipText());
         this.toolTip.refresh(this);
      } else {
         this.toolTip.setVisible(false);
      }
   }

   public static void main() {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new ReportScheduleView();
         }

         INSTANCE.setVisible(true);
      });
   }

   private void refresh() {
      if (this.tableModelMember != null) {
         this.tableModelMember.refresh();
         ReportScheduleMusic.sort(this.listMusic);
         this.tableModelMusic.refresh();
      }
   }

   private void exit() {
      super.setVisible(false);
      super.dispose();
   }

   private void saveExcel() {
      final String header = String.format(
         "%s --- %s", DateLG.SDF_D_S.format(this.webDateFieldStart.getDate()), DateLG.SDF_D_S.format(this.webDateFieldEnd.getDate())
      );
      TableToExcel.TableToExcelModel modelMembers = new TableToExcel.TableToExcelModel() {
         @Override
         public int getRowCount() {
            return ReportScheduleView.this.tableModelMember.getRowCount();
         }

         @Override
         public int getColumnCount() {
            return ReportScheduleView.this.tableModelMember.getColumnCount();
         }

         @Override
         public String getColumnName(int col) {
            return ReportScheduleView.this.tableModelMember.getColumnName(col);
         }

         @Override
         public Object getValue(int row, int col) {
            try {
               ReportScheduleMember rsm = ReportScheduleView.this.tableModelMember.getRow(row);
               switch (col) {
                  case 0:
                     return rsm.member.toString();
                  case 1:
                     return rsm.getScheduledCount();
                  case 2:
                     return rsm.getResponsibleCount();
               }
            } catch (Exception var4) {
            }

            return "";
         }

         @Override
         public String getHeader() {
            return header;
         }

         @Override
         public String getSheetName() {
            return I18N.WORD_MEMBERS.val();
         }
      };
      TableToExcel.TableToExcelModel modelMusics = new TableToExcel.TableToExcelModel() {
         @Override
         public int getRowCount() {
            return ReportScheduleView.this.tableModelMusic.getRowCount();
         }

         @Override
         public int getColumnCount() {
            return ReportScheduleView.this.tableModelMusic.getColumnCount();
         }

         @Override
         public String getColumnName(int col) {
            return ReportScheduleView.this.tableModelMusic.getColumnName(col);
         }

         @Override
         public Object getValue(int row, int col) {
            return ReportScheduleView.this.tableModelMusic.getValueAt(row, col);
         }

         @Override
         public String getHeader() {
            return header;
         }

         @Override
         public String getSheetName() {
            return I18N.WORD_MUSICS.val();
         }
      };
      TableToExcel.create(modelMembers, modelMusics);
   }
}
