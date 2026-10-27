package com.limagiran.holyrics.view;

import com.alee.extended.button.WebSplitButton;
import com.alee.laf.list.WebList;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.scroll.WebScrollPane;
import com.limagiran.bible.Bible;
import com.limagiran.bible.Verse;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ListCellRendererBibleVerses;
import com.limagiran.holyrics.swing.ListModelVerse;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Window;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class BibleFontSettings extends JDialog {
   private final ListModelVerse listModelBible = new ListModelVerse();
   private final WebSplitButton split = new WebSplitButton();
   private final Bible bible;
   private final Runnable callback;
   private JButton jButtonOk;
   private JComboBox<BibleFontSettings.EnumFontName> jComboBoxFontName;
   private JLabel jLabelTitle;
   private JList<Verse> jListVerses;
   private JPanel jPanel;
   private JPanel jPanelHeader;
   private JPanel jPanelSettings;
   private JScrollPane jScrollPaneVerses;

   public BibleFontSettings(Window parent, Bible bible, Runnable callback) {
      super(parent);
      this.bible = bible;
      this.callback = callback;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.split.setText(String.valueOf(Settings.EnumSettings.BIBLE_FONT_SIZE.getInt()));
      WebPopupMenu popupMenu = new WebPopupMenu();
      IntStream.of(12, 13, 14, 15, 16, 17, 18).forEach(size -> {
         WebMenuItem item = new WebMenuItem(String.valueOf(size));
         item.addActionListener(e -> {
            this.split.setText(String.valueOf(size));
            Settings.EnumSettings.BIBLE_FONT_SIZE.set(size);
            this.listModelBible.refresh();
         });
         popupMenu.add(item);
      });
      this.split.setPopupMenu(popupMenu);
      this.split.setFocusable(false);
      this.jPanelSettings.add(this.split, 0);
      this.jComboBoxFontName.addItemListener(e -> {
         if (e.getStateChange() == 1) {
            Settings.EnumSettings.BIBLE_FONT_NAME.set(((BibleFontSettings.EnumFontName)this.jComboBoxFontName.getSelectedItem()).getFullName());
            this.listModelBible.refresh();
         }
      });
      this.jComboBoxFontName.setSelectedItem(BibleFontSettings.EnumFontName.getCurrent());
      this.listModelBible.addElements(this.bible.getVerses(19, 23));
      ((WebList)this.jListVerses).setRolloverSelectionEnabled(false);
      ((WebList)this.jListVerses).setDecorateSelection(false);
      InsertShortcut.addEscToDispose(this);
      this.jButtonOk.addActionListener(e -> this.dispose());
      this.jScrollPaneVerses.getVerticalScrollBar().setPreferredSize(new Dimension(0, 0));
   }

   private void initComponents() {
      this.jPanel = new JPanel();
      this.jPanelHeader = new JPanel();
      this.jLabelTitle = new JLabel();
      this.jPanelSettings = new JPanel();
      this.jComboBoxFontName = new JComboBox<>();
      this.jScrollPaneVerses = new WebScrollPane(null);
      this.jListVerses = new WebList();
      this.jButtonOk = new JButton();
      this.setDefaultCloseOperation(2);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.settings"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(400, 600));
      this.setModal(true);
      this.setResizable(false);
      this.jPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      this.jPanel.setLayout(new BorderLayout(0, 2));
      this.jPanelHeader.setOpaque(false);
      this.jPanelHeader.setLayout(new BorderLayout(0, 2));
      this.jLabelTitle.setFont(new Font("Georgia", 0, 14));
      this.jLabelTitle.setHorizontalAlignment(0);
      this.jLabelTitle.setText(bundle.getString("word.font"));
      this.jPanelHeader.add(this.jLabelTitle, "First");
      this.jPanelSettings.setOpaque(false);
      this.jPanelSettings.setLayout(new FlowLayout(1, 10, 3));
      this.jComboBoxFontName.setFont(new Font("Georgia", 0, 12));
      this.jComboBoxFontName.setModel(new DefaultComboBoxModel<>(BibleFontSettings.EnumFontName.values()));
      this.jComboBoxFontName.setFocusable(false);
      this.jPanelSettings.add(this.jComboBoxFontName);
      this.jPanelHeader.add(this.jPanelSettings, "Center");
      this.jPanel.add(this.jPanelHeader, "First");
      this.jScrollPaneVerses.setHorizontalScrollBarPolicy(31);
      this.jScrollPaneVerses.setVerticalScrollBarPolicy(22);
      this.jListVerses.setModel(this.listModelBible);
      this.jListVerses.setSelectionMode(0);
      this.jListVerses.setCellRenderer(new ListCellRendererBibleVerses());
      this.jListVerses.setFocusable(false);
      this.jListVerses.setMinimumSize(new Dimension(1, 1));
      this.jScrollPaneVerses.setViewportView(this.jListVerses);
      this.jPanel.add(this.jScrollPaneVerses, "Center");
      this.jButtonOk.setFont(new Font("Georgia", 0, 14));
      this.jButtonOk.setText(bundle.getString("word.ok"));
      this.jButtonOk.setFocusable(false);
      this.jPanel.add(this.jButtonOk, "Last");
      this.getContentPane().add(this.jPanel, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(Window owner, Bible bible, Runnable callback) {
      SwingUtilities.invokeLater(() -> new BibleFontSettings(owner, bible, callback).setVisible(true));
   }

   @Override
   public void dispose() {
      this.callback.run();
      super.dispose();
   }

   public static enum EnumFontName {
      ARIAL("Arial", "sans-serif"),
      BOOK_ANTIQUA("Book Antiqua", "serif"),
      GEORGIA("Georgia", "serif"),
      LUCIDA("Lucida Sans Unicode", "sans-serif"),
      PALATINO("Palatino Linotype", "serif"),
      TIMES_NEW_ROMAN("Times New Roman", "serif"),
      TREBUCHET_MS("Trebuchet MS", "sans-serif");

      public final String name;
      public final String type;

      private EnumFontName(String name, String type) {
         this.name = name;
         this.type = type;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public String getFullName() {
         return String.format("\"%s\", %s", this.name, this.type);
      }

      private static BibleFontSettings.EnumFontName getCurrent() {
         try {
            String full = Settings.EnumSettings.BIBLE_FONT_NAME.getString();
            return Arrays.asList(values()).stream().filter(en -> en.getFullName().equals(full)).findFirst().orElse(BOOK_ANTIQUA);
         } catch (Exception var1) {
            return BOOK_ANTIQUA;
         }
      }
   }
}
