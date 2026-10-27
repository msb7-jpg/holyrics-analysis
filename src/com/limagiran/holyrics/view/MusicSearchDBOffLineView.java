package com.limagiran.holyrics.view;

import com.limagiran.bible.util.Values;
import com.limagiran.holyrics.event.MusicSearchDBOffLinePanelListener;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.view.panel.MusicSearchDBOffLinePanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

public class MusicSearchDBOffLineView extends JDialog implements MusicSearchDBOffLinePanelListener {
   private static MusicSearchDBOffLineView MUSIC_OFFLINE_SEARCH;
   private final MusicSearchDBOffLinePanel search;
   private final Runnable callback;
   private JButton jButtonCancel;
   private JButton jButtonCreate;
   private JPanel jPanelButtons;
   private JPanel jPanelButtons2;
   private JPanel jPanelFilter;
   private JScrollPane jScrollPaneLyrics;
   private JSplitPane jSplitPane;
   private JTextArea jTextAreaLyrics;

   private MusicSearchDBOffLineView(Runnable callback) {
      super((Frame)null);
      this.callback = callback;
      this.initComponents();
      this.search = new MusicSearchDBOffLinePanel(this, false, this);
      this.init();
   }

   private void init() {
      this.jButtonCreate.addActionListener(e -> this.create());
      this.jButtonCancel.addActionListener(e -> this.exit());
      this.jPanelFilter.add(this.search, "Center");
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
   }

   private void initComponents() {
      this.jSplitPane = new JSplitPane();
      this.jPanelFilter = new JPanel();
      this.jPanelButtons = new JPanel();
      this.jPanelButtons2 = new JPanel();
      this.jButtonCreate = new JButton();
      this.jButtonCancel = new JButton();
      this.jScrollPaneLyrics = new JScrollPane();
      this.jTextAreaLyrics = new JTextArea();
      this.setDefaultCloseOperation(0);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.search")));
      this.setIconImages(Values.ICONS_SEARCH);
      this.setMinimumSize(new Dimension(800, 650));
      this.setModal(true);
      this.setPreferredSize(new Dimension(800, 650));
      this.setResizable(false);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            MusicSearchDBOffLineView.this.formWindowClosing(evt);
         }
      });
      this.jSplitPane.setDividerLocation(300);
      this.jSplitPane.setDividerSize(3);
      this.jPanelFilter.setLayout(new BorderLayout());
      this.jPanelButtons.setLayout(new FlowLayout(1, 15, 5));
      this.jPanelButtons2.setLayout(new GridLayout(1, 2, 20, 0));
      this.jButtonCreate.setFont(new Font("Georgia", 0, 14));
      this.jButtonCreate.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.createMusic")));
      this.jPanelButtons2.add(this.jButtonCreate);
      this.jButtonCancel.setFont(new Font("Georgia", 0, 14));
      this.jButtonCancel.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.cancel")));
      this.jPanelButtons2.add(this.jButtonCancel);
      this.jPanelButtons.add(this.jPanelButtons2);
      this.jPanelFilter.add(this.jPanelButtons, "Last");
      this.jSplitPane.setLeftComponent(this.jPanelFilter);
      this.jTextAreaLyrics.setEditable(false);
      this.jTextAreaLyrics.setColumns(20);
      this.jTextAreaLyrics.setRows(5);
      this.jScrollPaneLyrics.setViewportView(this.jTextAreaLyrics);
      this.jSplitPane.setRightComponent(this.jScrollPaneLyrics);
      this.getContentPane().add(this.jSplitPane, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.exit();
   }

   public static void main(Runnable callback) {
      SwingUtilities.invokeLater(() -> {
         if (MUSIC_OFFLINE_SEARCH == null) {
            MUSIC_OFFLINE_SEARCH = new MusicSearchDBOffLineView(callback);
         }

         MUSIC_OFFLINE_SEARCH.setVisible(true);
      });
   }

   private void exit() {
      this.dispose();
      Optional.ofNullable(this.callback).ifPresent(r -> r.run());
   }

   private void create() {
      BooleanOptional.runIfTrue(
         this.search.getSelectedMusic() != null, () -> MusicView.main(this.search.getSelectedMusic(), true, Settings.SET.getTheme(), null)
      );
   }

   @Override
   public void musicChanged(Music music) {
      this.jTextAreaLyrics.setText(music != null ? music.getLyrics() : "");
      this.search.setupHighlight(this.jTextAreaLyrics);
      this.jTextAreaLyrics.setCaretPosition(0);
   }

   @Override
   public void doubleClick(Music music) {
      MusicView.main(music, true, Settings.SET.getTheme(), null);
   }

   @Override
   public void rightButtonClicked(MouseEvent evt, JList<Music> jlist) {
   }
}
