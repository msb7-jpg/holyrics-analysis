package com.limagiran.holyrics.view;

import com.alee.laf.label.WebLabel;
import com.limagiran.bible.util.Values;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.swing.DocumentListenerAuto;
import com.limagiran.holyrics.swing.ListCellRendererWebSearchMusic;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.vagalumeapi.Logo;
import com.limagiran.vagalumeapi.Result;
import com.limagiran.vagalumeapi.Search;
import com.limagiran.vagalumeapi.VagalumeAPI;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public final class MusicWebSearch extends JDialog {
   private final MusicWebSearch.MusicWebSearchListener listener;
   private final DefaultListModel<Music> listModel = new DefaultListModel<>();
   private WebLabel headerLyrics = new WebLabel("", 0, new Object[0]);
   private JButton jButtonCancel;
   private JButton jButtonCreate;
   private JButton jButtonSearch;
   private JLabel jLabelPageCreateCredential;
   private JLabel jLabelPageCreateCredentialURL;
   private JLabel jLabelTutorial;
   private JLabel jLabelVagalumeApiInfo;
   private JList<Music> jListSearch;
   private JPanel jPanelButtons;
   private JPanel jPanelFooter;
   private JPanel jPanelGeneral;
   private JPanel jPanelLeft;
   private JPanel jPanelSearch;
   private JPanel jPanelTextFieldVagalumeApiKey;
   private JPanel jPanelVagalumeApiInfo;
   private JPanel jPanelVagalumeApiTutorial;
   private JPanel jPanelVagalumeLogo;
   private JScrollPane jScrollPaneLyrics;
   private JScrollPane jScrollPaneSearch;
   private JTextArea jTextAreaLyrics;
   private JTextField jTextFieldSearch;
   private JTextField jTextFieldVagalumeApiKey;

   public MusicWebSearch(Frame parent, MusicWebSearch.MusicWebSearchListener listener) {
      super(parent);
      this.listener = listener;
      this.initComponents();
      this.init();
   }

   private void init() {
      MouseAdapter mouseAdapter = new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            if (e.getSource() == MusicWebSearch.this.headerLyrics) {
               String text = MusicWebSearch.this.headerLyrics.getText();
               if (!text.isEmpty()) {
                  try {
                     text = text.replace("</a>", "");
                     text = text.substring(text.lastIndexOf(">") + 1);
                     Utils.openURL(text);
                  } catch (Exception var4) {
                  }
               }
            } else if (e.getSource() == MusicWebSearch.this.jLabelTutorial) {
               PopupUtils.helpVagalumeTutorialApiKey(null);
            } else if (e.getSource() == MusicWebSearch.this.jLabelPageCreateCredentialURL) {
               Utils.openURL("https://auth.vagalume.com.br/");
            }
         }
      };
      this.headerLyrics.addMouseListener(mouseAdapter);
      this.jLabelTutorial.addMouseListener(mouseAdapter);
      this.jLabelPageCreateCredentialURL.addMouseListener(mouseAdapter);
      this.headerLyrics.setCursor(Cursor.getPredefinedCursor(12));
      this.headerLyrics.setMargin(2, 4, 8, 4);
      this.jScrollPaneLyrics.setColumnHeaderView(this.headerLyrics);
      this.jButtonSearch.addActionListener(e -> this.search());
      this.jButtonCreate.addActionListener(e -> Optional.ofNullable(this.jListSearch.getSelectedValue()).ifPresent(this::selected));
      this.jButtonCancel.addActionListener(e -> this.exit());
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
      this.jTextFieldVagalumeApiKey.setDocument(SwingUtils.createDocOnlyWordNumberAndSpace(64));
      this.jTextFieldVagalumeApiKey.setText(MyProperties.EnumProperties.VAGALUME_API_KEY.getString());
      this.jTextFieldVagalumeApiKey
         .getDocument()
         .addDocumentListener(new DocumentListenerAuto(500L, () -> MyProperties.EnumProperties.VAGALUME_API_KEY.set(this.jTextFieldVagalumeApiKey.getText())));
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanelLeft = new JPanel();
      this.jPanelSearch = new JPanel();
      this.jTextFieldSearch = SwingUtils.createJTextFieldPlaceHolder(I18N.MSG_PH_SEARCH_BY_TITLE.val());
      this.jButtonSearch = new JButton();
      this.jScrollPaneSearch = new JScrollPane();
      this.jListSearch = new JList<>();
      this.jPanelButtons = new JPanel();
      this.jButtonCreate = new JButton();
      this.jButtonCancel = new JButton();
      this.jScrollPaneLyrics = new JScrollPane();
      this.jTextAreaLyrics = new JTextArea();
      this.jPanelFooter = new JPanel();
      this.jPanelVagalumeLogo = new JPanel();
      this.jPanelVagalumeLogo.add(createVagalumeLogo());
      this.jPanelVagalumeApiInfo = new JPanel();
      this.jLabelVagalumeApiInfo = new JLabel();
      this.jPanelVagalumeApiTutorial = new JPanel();
      this.jLabelTutorial = new JLabel();
      this.jLabelPageCreateCredential = new JLabel();
      this.jLabelPageCreateCredentialURL = new JLabel();
      this.jPanelTextFieldVagalumeApiKey = new JPanel();
      this.jTextFieldVagalumeApiKey = SwingUtils.createJTextFieldPlaceHolder("Digite aqui sua credencial");
      this.setDefaultCloseOperation(0);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.search")));
      this.setIconImages(Values.ICONS_SEARCH);
      this.setMinimumSize(new Dimension(750, 650));
      this.setModal(true);
      this.setPreferredSize(new Dimension(750, 650));
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            MusicWebSearch.this.formWindowClosing(evt);
         }
      });
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jPanelGeneral.setLayout(new BorderLayout(5, 5));
      this.jPanelLeft.setLayout(new BorderLayout(0, 5));
      this.jPanelSearch.setLayout(new BorderLayout());
      this.jTextFieldSearch.setDocument(SwingUtils.createDocMaxSize(50));
      this.jTextFieldSearch.setFont(new Font("Georgia", 0, 12));
      this.jTextFieldSearch.setHorizontalAlignment(0);
      this.jTextFieldSearch.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent evt) {
            MusicWebSearch.this.jTextFieldSearchKeyPressed(evt);
         }
      });
      this.jPanelSearch.add(this.jTextFieldSearch, "Center");
      this.jButtonSearch.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/search16.png")));
      this.jButtonSearch.setMargin(new Insets(2, 4, 2, 4));
      this.jPanelSearch.add(this.jButtonSearch, "After");
      this.jPanelLeft.add(this.jPanelSearch, "First");
      this.jScrollPaneSearch.setHorizontalScrollBarPolicy(31);
      this.jScrollPaneSearch.setVerticalScrollBarPolicy(22);
      this.jListSearch.setFont(new Font("Franklin Gothic Medium", 0, 16));
      this.jListSearch.setModel(this.listModel);
      this.jListSearch.setSelectionMode(0);
      this.jListSearch.setCellRenderer(new ListCellRendererWebSearchMusic());
      this.jListSearch.setFixedCellWidth(250);
      this.jListSearch.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent evt) {
            MusicWebSearch.this.jListSearchMousePressed(evt);
         }
      });
      this.jListSearch.addListSelectionListener(new ListSelectionListener() {
         @Override
         public void valueChanged(ListSelectionEvent evt) {
            MusicWebSearch.this.jListSearchValueChanged(evt);
         }
      });
      this.jScrollPaneSearch.setViewportView(this.jListSearch);
      this.jPanelLeft.add(this.jScrollPaneSearch, "Center");
      this.jPanelButtons.setLayout(new FlowLayout(1, 15, 0));
      this.jButtonCreate.setFont(new Font("Georgia", 0, 12));
      this.jButtonCreate.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("msg.createMusic")));
      this.jButtonCreate.setEnabled(false);
      this.jButtonCreate.setMaximumSize(new Dimension(140, 30));
      this.jButtonCreate.setMinimumSize(new Dimension(140, 30));
      this.jButtonCreate.setPreferredSize(new Dimension(140, 30));
      this.jPanelButtons.add(this.jButtonCreate);
      this.jButtonCancel.setFont(new Font("Georgia", 0, 12));
      this.jButtonCancel.setText(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.cancel")));
      this.jButtonCancel.setMaximumSize(new Dimension(140, 30));
      this.jButtonCancel.setMinimumSize(new Dimension(140, 30));
      this.jButtonCancel.setPreferredSize(new Dimension(140, 30));
      this.jPanelButtons.add(this.jButtonCancel);
      this.jPanelLeft.add(this.jPanelButtons, "Last");
      this.jPanelGeneral.add(this.jPanelLeft, "West");
      this.jTextAreaLyrics.setEditable(false);
      this.jTextAreaLyrics.setColumns(20);
      this.jTextAreaLyrics.setRows(5);
      this.jScrollPaneLyrics.setViewportView(this.jTextAreaLyrics);
      this.jPanelGeneral.add(this.jScrollPaneLyrics, "Center");
      this.jPanelFooter.setLayout(new BorderLayout(0, 8));
      this.jPanelFooter.add(this.jPanelVagalumeLogo, "First");
      this.jPanelVagalumeApiInfo.setLayout(new GridBagLayout());
      this.jLabelVagalumeApiInfo.setFont(new Font("Arial", 0, 12));
      this.jLabelVagalumeApiInfo
         .setText(
            "<html><div style='text-align:justify'>O programa utiliza um recurso grátis disponibilizado pelo site de letras Vagalume. Porém o número de pesquisas por dia tem um limite, e como todos os usuários do Holyrics utilizam a mesma credencial é possível que este limite seja atingido e ninguém mais consiga realizar pesquisas no dia.\nCaso você utilize bastante a pesquisa pela internet, você pode criar sua própria credencial para evitar que o limite do programa seja atingido e outros usuários fiquem sem poder pesquisar através desta janela. Obrigado!"
         );
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelVagalumeApiInfo.add(this.jLabelVagalumeApiInfo, gridBagConstraints);
      this.jPanelVagalumeApiTutorial.setLayout(new FlowLayout(1, 0, 0));
      this.jLabelTutorial.setFont(new Font("Arial", 0, 12));
      this.jLabelTutorial.setText("<html><a href='#'>Tutorial: Como criar uma credencial</a>");
      this.jLabelTutorial.setCursor(new Cursor(12));
      this.jPanelVagalumeApiTutorial.add(this.jLabelTutorial);
      this.jLabelPageCreateCredential.setFont(new Font("Arial", 0, 12));
      this.jLabelPageCreateCredential.setText("<html>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Página para criar a credencial:&nbsp;");
      this.jPanelVagalumeApiTutorial.add(this.jLabelPageCreateCredential);
      this.jLabelPageCreateCredentialURL.setFont(new Font("Arial", 0, 12));
      this.jLabelPageCreateCredentialURL.setText("<html><a href='#'>https://auth.vagalume.com.br/</a>");
      this.jLabelPageCreateCredentialURL.setCursor(new Cursor(12));
      this.jPanelVagalumeApiTutorial.add(this.jLabelPageCreateCredentialURL);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(8, 0, 0, 0);
      this.jPanelVagalumeApiInfo.add(this.jPanelVagalumeApiTutorial, gridBagConstraints);
      this.jPanelTextFieldVagalumeApiKey.setLayout(new FlowLayout(1, 0, 0));
      this.jTextFieldVagalumeApiKey.setColumns(32);
      this.jTextFieldVagalumeApiKey.setFont(new Font("Arial", 0, 12));
      this.jTextFieldVagalumeApiKey.setHorizontalAlignment(0);
      this.jPanelTextFieldVagalumeApiKey.add(this.jTextFieldVagalumeApiKey);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(8, 0, 0, 0);
      this.jPanelVagalumeApiInfo.add(this.jPanelTextFieldVagalumeApiKey, gridBagConstraints);
      this.jPanelFooter.add(this.jPanelVagalumeApiInfo, "Center");
      this.jPanelGeneral.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jTextFieldSearchKeyPressed(KeyEvent evt) {
      BooleanOptional.runIfTrue(evt.getKeyCode() == 10, this::search);
   }

   private void jListSearchMousePressed(MouseEvent evt) {
      BooleanOptional.runIfTrue(Utils.isDoubleClick(evt), () -> Optional.ofNullable(this.jListSearch.getSelectedValue()).ifPresent(this::selected));
   }

   private void jListSearchValueChanged(ListSelectionEvent evt) {
      Optional.ofNullable(this.jListSearch.getSelectedValue()).ifPresent(this::setupTextAreaLyrics);
      this.jButtonCreate.setEnabled(this.jListSearch.getSelectedValue() != null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.exit();
   }

   private void search() {
      this.listModel.removeAllElements();
      final String text = this.jTextFieldSearch.getText().replace("=", "").replace("&", "").replace("#", "");
      final String vagalumeApiKey = this.getVagalumeApiKey();
      PopUpWorker.exe(
         new MySwingWorker<List<Music>, Void>(this, com.limagiran.bible.i18n.I18N.WORD_SEARCHING.val()) {
            protected List<Music> doInBackground() throws Exception {
               List<Music> musics = new ArrayList<>();

               try {
                  Search search = VagalumeAPI.searchByMusic(text);

                  for (int i = 0; i < search.response.docs.size(); i++) {
                     if (this.isCancelled()) {
                        return null;
                     }

                     try {
                        Result r = VagalumeAPI.resultByID(search.response.docs.get(i).id, vagalumeApiKey);
                        if (r != null && r.mus.get(0).text != null && (r.isExact() || r.isAproximate())) {
                           com.limagiran.vagalumeapi.Music vagalumeMusic = r.mus.get(0);
                           Music music = Music.create()
                              .setArtist(r.art.name)
                              .setTitle(vagalumeMusic.name)
                              .setLyrics(vagalumeMusic.text)
                              .setVagalumeLink(vagalumeMusic.url);
                           musics.add(music);
                        }
                     } catch (Exception var7) {
                     }
                  }

                  return musics;
               } catch (Exception var8) {
                  return null;
               }
            }

            @Override
            protected void done() {
               super.done();

               try {
                  List<Music> musics = this.get();
                  if (musics == null) {
                     Utils.error(MusicWebSearch.this, I18N.MSG_CONNECTION_FAILURE.val());
                  } else if (musics.isEmpty()) {
                     Utils.error(MusicWebSearch.this, I18N.MSG_NO_RESULTS_FOUND.val());
                  } else {
                     musics.stream().forEach(MusicWebSearch.this.listModel::addElement);
                     MusicWebSearch.this.jListSearch.setSelectedValue(musics.get(0), true);
                     MusicWebSearch.this.setupTextAreaLyrics(musics.get(0));
                  }

                  MusicWebSearch.this.jButtonCreate.setEnabled(musics != null && !musics.isEmpty());
               } catch (Exception var2) {
               }
            }
         },
         true
      );
   }

   private void selected(Music music) {
      this.exit();
      this.listener.createMusic(music);
   }

   private void exit() {
      this.dispose();
   }

   private String getVagalumeApiKey() {
      return !this.jTextFieldVagalumeApiKey.getText().trim().isEmpty()
         ? this.jTextFieldVagalumeApiKey.getText()
         : com.limagiran.holyrics.util.Values.VAGALUME_API_KEYS[Utils.random(com.limagiran.holyrics.util.Values.VAGALUME_API_KEYS.length) - 1];
   }

   private static Logo createVagalumeLogo() {
      return new Logo() {
         {
            this.addMouseListener(new MouseAdapter() {
               @Override
               public void mouseReleased(MouseEvent evt) {
                  Utils.openURL("https://www.vagalume.com.br/");
               }
            });
            this.setCursor(Cursor.getPredefinedCursor(12));
         }
      };
   }

   private void setupTextAreaLyrics(Music m) {
      this.jTextAreaLyrics.setText(m.getLyrics());
      this.jTextAreaLyrics.setCaretPosition(0);
      this.headerLyrics.setText(m.getVagalumeLink() == null ? "" : String.format("<html><a href='%1$s'>%1$s</a>", m.getVagalumeLink()));
   }

   public interface MusicWebSearchListener {
      void createMusic(Music var1);
   }
}
