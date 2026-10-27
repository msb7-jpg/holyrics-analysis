package com.limagiran.holyrics.playback;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.list.WebList;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ListCellRendererCaptureDialog;
import com.limagiran.holyrics.util.EnumSlideSizePreview;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.UICreate;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WrapLayout;
import com.limagiran.holyrics.view.panel.SimpleSlidePreview;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class CaptureDialog extends JDialog {
   private static CaptureDialog INSTANCE;
   private final PlayBack playback;
   private final PlayBackPlayerMini player;
   private final CaptureDialog.CaptureListener listener;
   private final List<int[]> list = new ArrayList<>();
   private final Object listLock = new Object();
   private WebPanel panel;
   private WebPanel panelHeader;
   private WebPanel panelSlides;
   private final DefaultListModel<int[]> listModel = new DefaultListModel<>();
   private WebList listViewCapture;

   public CaptureDialog(PlayBack playback, PlayBackPlayerMini player, CaptureDialog.CaptureListener listener) {
      super((Frame)null, true);
      this.playback = playback;
      this.player = player;
      this.listener = listener;
      this.init();
   }

   private void initComponents() {
      this.setDefaultCloseOperation(0);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowOpened(WindowEvent e) {
            if (Settings.EnumSettings2.SHOW_HELP_PLAYBACK_CAPTURE_METHOD.is()) {
               Settings.EnumSettings2.SHOW_HELP_PLAYBACK_CAPTURE_METHOD.set(false);
               Utils.startThreadDaemon(() -> {
                  Utils.sleep(1000L);
                  SwingUtilities.invokeLater(() -> CaptureDialog.this.help());
               });
            }
         }

         @Override
         public void windowClosing(WindowEvent e) {
            CaptureDialog.this.close();
         }
      });
      this.setLayout(new BorderLayout());
      this.setTitle(I18N.MSG_CAPTURE_METHOD.val());
      this.setIconImages(Images.HOLYRICS_ICON);
      this.panel = new WebPanel();
      this.panel.setOpaque(false);
      this.panel.setLayout(new BorderLayout(0, 8));
      this.panel.setMargin(16, 8, 16, 8);
      this.getContentPane().add(this.panel, "Center");
      this.panelHeader = new WebPanel(new WrapLayout());
      this.panelHeader.setOpaque(false);
      this.panelHeader.add(this.player);
      this.panelHeader.add(Box.createHorizontalStrut(32));
      WebButton buttonRestart = new WebButton(I18N.WORD_RESET.val(), evt -> {
         if (Utils.confirm(I18N.WORD_RESET.question())) {
            this.player.stop();
            synchronized (this.listLock) {
               this.list.clear();
            }

            this.listModel.clear();
            this.getAllSlides().forEach(SimpleSlidePreview::unselect);
         }
      });
      buttonRestart.setFocusable(false);
      buttonRestart.setFontName("Georgia");
      buttonRestart.setFontSize(14);
      buttonRestart.setMargin(2, 4, 2, 4);
      this.panelHeader.add(buttonRestart);
      this.panelHeader.add(Box.createHorizontalStrut(32));
      WebButton buttonSave = new WebButton(new ImageIcon(Images.load("saveBlack32.png")), evt -> this.exit(true));
      buttonSave.setMargin(2, 4, 2, 4);
      buttonSave.setFocusable(false);
      this.panelHeader.add(buttonSave);
      this.panelHeader.add(Box.createHorizontalStrut(32));
      this.panelHeader.add(UICreate.defaultHelpButton(evt -> this.help()));
      this.panel.add(this.panelHeader, "First");
      this.panelSlides = new WebPanel();
      this.panelSlides.setOpaque(false);
      this.panelSlides.setLayout(new WrapLayout(1, 12, 12));
      WebScrollPane scrollPaneSlides = new WebScrollPane(this.panelSlides);
      scrollPaneSlides.getVerticalScrollBar().setUnitIncrement(32);
      this.panel.add(scrollPaneSlides, "Center");
      this.listViewCapture = new WebList(this.listModel);
      this.listViewCapture.setCellRenderer(new ListCellRendererCaptureDialog(this.playback.music));
      this.listViewCapture.setFontName("Arial");
      this.listViewCapture.setFontSize(14);
      this.listViewCapture.setBorder(new EmptyBorder(0, 4, 0, 4));
      this.listViewCapture.setFocusable(false);
      this.listViewCapture.setPrototypeCellValue(new int[]{-1, -1});
      this.listViewCapture.setSelectionMode(0);
      this.listViewCapture.onMenuTrigger(evt -> {
         if (SwingUtilities.isRightMouseButton(evt)) {
            int i = this.listViewCapture.locationToIndex(evt.getPoint());
            if (i != -1 && this.listViewCapture.getSelectedIndex() != i) {
               this.listViewCapture.setSelectedIndex(this.listViewCapture.locationToIndex(evt.getPoint()));
            }

            Object obj = this.listViewCapture.getSelectedValue();
            if (obj instanceof int[]) {
               JPopupMenu menu = new JPopupMenu();
               JMenuItem edit = new JMenuItem(I18N.WORD_EDIT.val());
               edit.addActionListener(ae -> PlayBackUtils.setStartAt(((int[])obj)[1], newValue -> {
                     ((int[])obj)[1] = newValue;
                     this.sortAndRefreshList((int[])obj);
                  }));
               menu.add(edit);
               JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val());
               remove.addActionListener(ae -> {
                  this.listModel.removeElement((int[])obj);
                  this.list.remove((int[])obj);
               });
               menu.add(remove);
               menu.show(this.listViewCapture, evt.getX(), evt.getY());
            }
         }
      });
      WebScrollPane scrollListView = new WebScrollPane(this.listViewCapture);
      scrollListView.getVerticalScrollBar().setUnitIncrement(32);
      scrollListView.setFocusable(false);
      WebLabel labelTitleListView = new WebLabel("Capturado", 0, new Object[0]);
      labelTitleListView.setFontSizeAndStyle(18, 1);
      scrollListView.setColumnHeaderView(labelTitleListView);
      this.panel.add(scrollListView, "Last");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void initListeners() {
      this.panelSlides.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            try {
               if (!CaptureDialog.this.player.isPlaying()) {
                  return;
               }

               Component c = CaptureDialog.this.panelSlides.getComponentAt(e.getPoint());
               if (!(c instanceof SimpleSlidePreview)) {
                  return;
               }

               int idxOf = CaptureDialog.this.panelSlides.getComponentZOrder(c);
               SimpleSlidePreview cs = (SimpleSlidePreview)c;
               if (idxOf >= 0 && SwingUtilities.isLeftMouseButton(e)) {
                  CaptureDialog.this.getAllSlides().forEach(SimpleSlidePreview::unselect);
                  cs.select();
                  int[] obj = new int[]{idxOf, (int)CaptureDialog.this.player.getTimeMillis()};
                  synchronized (CaptureDialog.this.listLock) {
                     CaptureDialog.this.list.add(obj);
                  }

                  CaptureDialog.this.sortAndRefreshList(obj);
               }
            } catch (Exception var9) {
            }
         }
      });
   }

   private void sortAndRefreshList(int[] selectedValue) {
      synchronized (this.listLock) {
         this.list.sort((o1, o2) -> Integer.compare(o1[1], o2[1]));
         this.listModel.clear();
         this.list.forEach(this.listModel::addElement);
      }

      if (selectedValue != null) {
         this.listViewCapture.setSelectedValue(selectedValue, true);
      }
   }

   private void init() {
      this.initComponents();
      this.initListeners();
      this.playback.music.getParagraphs().forEach(p -> {
         SimpleSlidePreview slide = new SimpleSlidePreview(MusicController.Paragraph.create(p), EnumSlideSizePreview.MEDIUM.size);
         slide.setCursor(Cursor.getPredefinedCursor(12));
         this.panelSlides.add(slide);
      });
      synchronized (this.listLock) {
         this.list.addAll(this.playback.getOrderAndStartAt());
         this.list.forEach(this.listModel::addElement);
      }

      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::close));
      SwingUtils.maximize(this, 150);
   }

   public static void main(PlayBack playback, PlayBackPlayerMini player, CaptureDialog.CaptureListener listener) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.removeAll();
            INSTANCE.setVisible(false);
            INSTANCE.dispose();
         }

         (INSTANCE = new CaptureDialog(playback, player, listener)).setVisible(true);
      });
   }

   private List<SimpleSlidePreview> getAllSlides() {
      return Stream.of(this.panelSlides.getComponents())
         .filter(SimpleSlidePreview.class::isInstance)
         .map(SimpleSlidePreview.class::cast)
         .collect(Collectors.toList());
   }

   private void close() {
      switch (JOptionPane.showConfirmDialog(this, I18N.MSG_CONFIRM_SAVE_CHANGES.val(), I18N.WORD_CONFIRM.val(), 1)) {
         case 0:
            this.exit(true);
            break;
         case 1:
            this.exit(false);
      }
   }

   private void exit(boolean save) {
      List<int[]> clone = new ArrayList<>();
      synchronized (this.listLock) {
         clone.addAll(this.list);
      }

      this.listener.callback(save, clone);
      super.setVisible(false);
      super.dispose();
      this.player.stop();
   }

   private void help() {
      PopupUtils.helpCaptureDialog(null);
   }

   public interface CaptureListener {
      void callback(boolean var1, List<int[]> var2);
   }
}
