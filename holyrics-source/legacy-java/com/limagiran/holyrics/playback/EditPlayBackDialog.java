package com.limagiran.holyrics.playback;

import com.alee.laf.button.WebButton;
import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.DragComponentMouseAdapterMusicOrder;
import com.limagiran.holyrics.swing.JLabelToolTip;
import com.limagiran.holyrics.util.EnumSlideSizePreview;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.UICreate;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WrapLayout;
import com.limagiran.holyrics.view.control.PresentationUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.GroupLayout.Alignment;

public class EditPlayBackDialog extends JDialog implements DragComponentMouseAdapterMusicOrder.DragComponentListener {
   private static EditPlayBackDialog INSTANCE;
   private static PlayBackPlayerMini PLAYER_MINI;
   private static JLabelToolTip tooltip;
   protected static final Dimension SLIDE_SIZE = new Dimension(getSlideSize(Settings.EnumSettings2.PLAYBACK_SLIDE_SIZE.getInt()));
   private final PlayBack playBack;
   private final Music music;
   private final File fileMedia;
   private final Runnable callback;
   private int lastSelected;
   private CustomSlidePB checkMouseRelease;
   private final CustomSlidePBListener listener = new CustomSlidePBListener() {
      @Override
      public void durationChanged(CustomSlidePB source) {
         List<CustomSlidePB> list = EditPlayBackDialog.this.getAllSlides();
         int duration = EditPlayBackDialog.this.getStartWithDuration();

         for (CustomSlidePB cs : list) {
            cs.setStartAt(duration);
            duration += cs.getDuration();
         }
      }

      @Override
      public void play(CustomSlidePB source, boolean onlyThis) {
         if (source != null && EditPlayBackDialog.PLAYER_MINI != null) {
            EditPlayBackDialog.PLAYER_MINI.play(source.getStartAt(), onlyThis ? source.getEndAt() : Integer.MAX_VALUE);
         }
      }

      @Override
      public JLabelToolTip getToolTip() {
         return EditPlayBackDialog.tooltip;
      }

      @Override
      public void changePosition(long millis) {
         if (EditPlayBackDialog.PLAYER_MINI != null) {
            EditPlayBackDialog.PLAYER_MINI.position(millis);
         }
      }

      @Override
      public void changeStartAt(CustomSlidePB source, int millis) {
         try {
            int idx = EditPlayBackDialog.this.jPanelParagraphs.getComponentZOrder(source);
            if (idx < 0) {
               return;
            }

            List<int[]> list = EditPlayBackDialog.this.playBack.getOrderAndStartAt();
            if (idx < list.size()) {
               list.get(idx)[1] = millis;
               list.sort((o1, o2) -> Integer.compare(o1[1], o2[1]));
               EditPlayBackDialog.this.playBack.setOrderAndStartAt(list, EditPlayBackDialog.PLAYER_MINI.getDuration() * 1000);
               EditPlayBackDialog.this.jSpinnerStartWithDuration.setValue((double)EditPlayBackDialog.this.playBack.getStartWithDuration() / 1000.0);
               EditPlayBackDialog.this.setup();
            }
         } catch (Exception var5) {
         }
      }
   };
   private Filler filler1;
   private Filler filler2;
   private Filler filler3;
   private Filler filler4;
   private JButton jButtonCancel;
   private JButton jButtonCapture;
   private JButton jButtonHelp;
   private JButton jButtonReset;
   private JButton jButtonSave;
   private JComboBox<EnumSlideSizePreview> jComboBoxSlideSize;
   private JComboBox<String> jComboBoxStartWith;
   private JLabel jLabelDuration;
   private JLabel jLabelSeconds;
   private JLabel jLabelSlideSize;
   private JLabel jLabelStartWith;
   private JLabel jLabelTitle;
   private JLabel jLabelTooltip;
   private JPanel jPanel;
   private JPanel jPanelFooter;
   private JPanel jPanelHeader;
   private JPanel jPanelHeaderCenter;
   private JPanel jPanelHeaderCenterBottom;
   private JPanel jPanelHeaderCenterTop;
   private JPanel jPanelParagraphs;
   private JScrollPane jScrollPaneParagraphs;
   private JSpinner jSpinnerStartWithDuration;

   private EditPlayBackDialog(PlayBack playBack, File fileMedia, Runnable callback) {
      super((Frame)null, true);
      this.playBack = playBack;
      this.music = playBack.getMusic();
      this.fileMedia = fileMedia;
      this.callback = callback;
      this.initComponents();
      this.init();
   }

   private void init() {
      if (tooltip == null) {
         tooltip = new JLabelToolTip();
         tooltip.atLeft();
         tooltip.distance = 6;
      }

      this.setupMediaPlayer();
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            EditPlayBackDialog.this.close();
         }
      });
      this.jButtonReset.addActionListener(evt -> this.reset());
      this.jButtonCapture.addActionListener(evt -> this.captureMethod());
      this.jButtonSave.addActionListener(evt -> this.save(true));
      this.jButtonCancel.addActionListener(evt -> this.cancel());
      this.jComboBoxSlideSize.addItemListener(evt -> {
         if (evt.getStateChange() == 1) {
            this.setSlideSize(Settings.EnumSettings2.PLAYBACK_SLIDE_SIZE.set(this.jComboBoxSlideSize.getSelectedIndex()));
         }
      });
      this.jPanelParagraphs.setLayout(new WrapLayout(1, 12, 12));
      this.jLabelTitle.setText(this.playBack.getFile().getName());
      DragComponentMouseAdapterMusicOrder drag = new DragComponentMouseAdapterMusicOrder(this);
      this.jPanelParagraphs.addMouseListener(drag);
      this.jPanelParagraphs
         .addMouseListener(
            new MouseAdapter() {
               @Override
               public void mousePressed(MouseEvent e) {
                  try {
                     EditPlayBackDialog.this.checkMouseRelease = null;
                     List<CustomSlidePB> all = EditPlayBackDialog.this.getAllSlides();
                     Component c = EditPlayBackDialog.this.jPanelParagraphs.getComponentAt(e.getPoint());
                     if (!(c instanceof CustomSlidePB)) {
                        all.forEach(CustomSlidePB::unselect);
                        return;
                     }

                     CustomSlidePB cs = (CustomSlidePB)c;
                     if (e.isControlDown()) {
                        cs.select();
                        EditPlayBackDialog.this.lastSelected = Math.max(all.indexOf(cs), 0);
                     } else if (e.isShiftDown()) {
                        all.forEach(CustomSlidePB::unselect);
                        int indexOf = Math.max(all.indexOf(cs), 0);
                        all.subList(Math.min(indexOf, EditPlayBackDialog.this.lastSelected), Math.max(indexOf, EditPlayBackDialog.this.lastSelected) + 1)
                           .forEach(CustomSlidePB::select);
                     } else if (cs.isSelected()) {
                        EditPlayBackDialog.this.checkMouseRelease = cs;
                     } else {
                        all.forEach(CustomSlidePB::unselect);
                        cs.select();
                        EditPlayBackDialog.this.lastSelected = Math.max(all.indexOf(cs), 0);
                     }
                  } catch (Exception var6) {
                  }
               }

               @Override
               public void mouseReleased(MouseEvent e) {
                  if (SwingUtilities.isLeftMouseButton(e)) {
                     Optional.ofNullable(EditPlayBackDialog.this.checkMouseRelease).ifPresent(cs -> {
                        EditPlayBackDialog.this.getAllSlides().forEach(CustomSlidePB::unselect);
                        cs.select();
                     });
                  } else {
                     EditPlayBackDialog.this.moreOptions(e);
                  }
               }
            }
         );
      this.jPanelParagraphs.addMouseMotionListener(drag);
      this.jScrollPaneParagraphs.getVerticalScrollBar().setUnitIncrement(32);
      this.jComboBoxStartWith.addItemListener(e -> {
         if (e.getStateChange() == 1) {
            if (this.jComboBoxStartWith.getSelectedIndex() == 1) {
               this.playBack.startWithEmpty();
            } else {
               this.playBack.startWithTitleAndArtist();
            }
         }
      });
      this.setup();
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::close));
      SwingUtils.maximize(this, 150);
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentShown(ComponentEvent e) {
            if (Settings.EnumSettings2.SHOW_HELP_PLAYBACK_EDIT.is()) {
               Settings.EnumSettings2.SHOW_HELP_PLAYBACK_EDIT.set(false);
               Utils.startThreadDaemon(() -> {
                  Utils.sleep(1000L);
                  SwingUtilities.invokeLater(EditPlayBackDialog.this::help);
               });
            }

            Utils.startThreadDaemon(() -> {
               while (EditPlayBackDialog.this.isVisible()) {
                  Utils.sleep(125L);
                  if (EditPlayBackDialog.PLAYER_MINI != null) {
                     EditPlayBackDialog.this.getAllSlides().forEach(cs -> cs.setCurrentAudioTime(EditPlayBackDialog.PLAYER_MINI.getTimeMillis()));
                  }
               }
            });
         }
      });
   }

   private void initComponents() {
      this.jPanel = new JPanel();
      this.jPanelHeader = new JPanel();
      this.jLabelTitle = new JLabel();
      this.jPanelHeaderCenter = new JPanel();
      this.jPanelHeaderCenterTop = new JPanel();
      this.jLabelSlideSize = new JLabel();
      this.jComboBoxSlideSize = new JComboBox<>();
      this.filler1 = new Filler(new Dimension(64, 0), new Dimension(64, 0), new Dimension(64, 32767));
      this.jButtonReset = new JButton();
      this.filler3 = new Filler(new Dimension(64, 0), new Dimension(64, 0), new Dimension(64, 32767));
      this.jButtonCapture = new JButton();
      this.filler4 = new Filler(new Dimension(64, 0), new Dimension(64, 0), new Dimension(64, 32767));
      this.jButtonHelp = UICreate.defaultHelpButton(evt -> this.help());
      this.jPanelHeaderCenterBottom = new JPanel();
      this.jPanelHeaderCenterBottom.setLayout(new WrapLayout(1, 5, 3));
      this.jLabelStartWith = new JLabel();
      this.jComboBoxStartWith = new JComboBox<>();
      this.jLabelDuration = new JLabel();
      this.jSpinnerStartWithDuration = this.createSpinnerStartWith();
      this.jLabelSeconds = new JLabel();
      this.filler2 = new Filler(new Dimension(64, 0), new Dimension(64, 0), new Dimension(64, 32767));
      this.jLabelTooltip = new JLabel();
      this.jScrollPaneParagraphs = new JScrollPane();
      this.jPanelParagraphs = new JPanel();
      this.jPanelFooter = new JPanel();
      this.jButtonSave = new JButton();
      this.jButtonCancel = new JButton();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("msg.automaticShow"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(400, 300));
      this.jPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
      this.jPanel.setLayout(new BorderLayout());
      this.jPanelHeader.setLayout(new BorderLayout());
      this.jLabelTitle.setFont(new Font("Georgia", 0, 24));
      this.jLabelTitle.setHorizontalAlignment(0);
      this.jPanelHeader.add(this.jLabelTitle, "First");
      this.jPanelHeaderCenter.setOpaque(false);
      this.jPanelHeaderCenter.setLayout(new GridLayout(2, 1));
      this.jLabelSlideSize.setFont(new Font("Georgia", 0, 14));
      this.jLabelSlideSize.setText(bundle.getString("word.preview"));
      this.jPanelHeaderCenterTop.add(this.jLabelSlideSize);
      this.jComboBoxSlideSize.setFont(new Font("Georgia", 0, 14));
      this.jComboBoxSlideSize.setModel(new DefaultComboBoxModel<>(EnumSlideSizePreview.values()));
      this.jComboBoxSlideSize.setSelectedIndex(Utils.range(Settings.EnumSettings2.PLAYBACK_SLIDE_SIZE.getInt(), 0, EnumSlideSizePreview.values().length - 1));
      this.jComboBoxSlideSize.setFocusable(false);
      this.jPanelHeaderCenterTop.add(this.jComboBoxSlideSize);
      this.jPanelHeaderCenterTop.add(this.filler1);
      this.jButtonReset.setFont(new Font("Georgia", 0, 14));
      this.jButtonReset.setText(bundle.getString("word.reset"));
      this.jButtonReset.setFocusable(false);
      this.jPanelHeaderCenterTop.add(this.jButtonReset);
      this.jPanelHeaderCenterTop.add(this.filler3);
      this.jButtonCapture.setFont(new Font("Georgia", 0, 14));
      this.jButtonCapture.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/play16.png")));
      this.jButtonCapture.setText(bundle.getString("msg.captureMethod"));
      this.jButtonCapture.setFocusable(false);
      this.jPanelHeaderCenterTop.add(this.jButtonCapture);
      this.jPanelHeaderCenterTop.add(this.filler4);
      this.jPanelHeaderCenterTop.add(this.jButtonHelp);
      this.jPanelHeaderCenter.add(this.jPanelHeaderCenterTop);
      this.jPanelHeaderCenterBottom.setOpaque(false);
      this.jLabelStartWith.setFont(new Font("Georgia", 0, 14));
      this.jLabelStartWith.setText(bundle.getString("msg.startWith"));
      this.jPanelHeaderCenterBottom.add(this.jLabelStartWith);
      this.jComboBoxStartWith.setFont(new Font("Georgia", 0, 14));
      this.jComboBoxStartWith.setModel(new DefaultComboBoxModel<>(new String[]{I18N.MSG_TITLE_AND_ARTIST.val(), I18N.WORD_BLANK.val()}));
      this.jComboBoxStartWith.setSelectedIndex(this.playBack.isStartWithTitleAndArtist() ? 0 : 1);
      this.jComboBoxStartWith.setFocusable(false);
      this.jPanelHeaderCenterBottom.add(this.jComboBoxStartWith);
      this.jLabelDuration.setFont(new Font("Georgia", 0, 14));
      this.jLabelDuration.setText(bundle.getString("word.duration"));
      this.jPanelHeaderCenterBottom.add(this.jLabelDuration);
      this.jSpinnerStartWithDuration.setFont(new Font("Courier New", 0, 14));
      this.jPanelHeaderCenterBottom.add(this.jSpinnerStartWithDuration);
      this.jLabelSeconds.setFont(new Font("Courier New", 0, 14));
      this.jLabelSeconds.setText("s");
      this.jPanelHeaderCenterBottom.add(this.jLabelSeconds);
      this.jPanelHeaderCenterBottom.add(this.filler2);
      this.jPanelHeaderCenter.add(this.jPanelHeaderCenterBottom);
      this.jPanelHeader.add(this.jPanelHeaderCenter, "Center");
      this.jLabelTooltip.setFont(new Font("Arial", 0, 14));
      this.jLabelTooltip.setForeground(new Color(153, 0, 0));
      this.jLabelTooltip.setHorizontalAlignment(0);
      this.jLabelTooltip.setText(bundle.getString("msg.reorderTooltip"));
      this.jLabelTooltip.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
      this.jPanelHeader.add(this.jLabelTooltip, "Last");
      this.jPanel.add(this.jPanelHeader, "First");
      this.jPanelParagraphs.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      GroupLayout jPanelParagraphsLayout = new GroupLayout(this.jPanelParagraphs);
      this.jPanelParagraphs.setLayout(jPanelParagraphsLayout);
      jPanelParagraphsLayout.setHorizontalGroup(jPanelParagraphsLayout.createParallelGroup(Alignment.LEADING).addGap(0, 650, 32767));
      jPanelParagraphsLayout.setVerticalGroup(jPanelParagraphsLayout.createParallelGroup(Alignment.LEADING).addGap(0, 294, 32767));
      this.jScrollPaneParagraphs.setViewportView(this.jPanelParagraphs);
      this.jPanel.add(this.jScrollPaneParagraphs, "Center");
      this.jPanelFooter.setLayout(new FlowLayout(1, 48, 5));
      this.jButtonSave.setFont(new Font("Georgia", 0, 18));
      this.jButtonSave.setText(bundle.getString("word.save"));
      this.jButtonSave.setFocusable(false);
      this.jPanelFooter.add(this.jButtonSave);
      this.jButtonCancel.setFont(new Font("Georgia", 0, 18));
      this.jButtonCancel.setText(bundle.getString("word.cancel"));
      this.jButtonCancel.setFocusable(false);
      this.jPanelFooter.add(this.jButtonCancel);
      this.jPanel.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanel, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(PlayBack playBack, Runnable callback) {
      ExportMediaWorker.main(playBack, file -> SwingUtilities.invokeLater(() -> {
            if (INSTANCE != null) {
               INSTANCE.removeAll();
               INSTANCE.setVisible(false);
               INSTANCE.dispose();
            }

            (INSTANCE = new EditPlayBackDialog(playBack, file, callback)).setVisible(true);
         }));
   }

   public void help() {
      PopupUtils.helpAutomaticShow(null);
   }

   private void setup() {
      this.jPanelParagraphs.removeAll();
      List<MusicController.Paragraph> paragraphs = this.music.getParagraphsToPresentation();

      for (int i = 0; i < paragraphs.size(); i++) {
         CustomSlidePB cs = new CustomSlidePB(paragraphs.get(i), this.playBack.getDuration(i), this.listener);
         this.jPanelParagraphs.add(cs);
      }

      this.listener.durationChanged(null);
      this.jPanelParagraphs.revalidate();
      this.jPanelParagraphs.repaint();
   }

   private void moreOptions(MouseEvent evt) {
      List<CustomSlidePB> selection = this.getAllSlides().stream().filter(CustomSlidePB::isSelected).collect(Collectors.toList());
      if (!selection.isEmpty()) {
         JPopupMenu menu = new JPopupMenu();
         JMenu addBefore = new JMenu(I18N.MSG_ADD_BEFORE.val());
         List<String> paragraphs = this.music.getParagraphs();

         for (int i = 0; i < paragraphs.size(); i++) {
            String paragraph = paragraphs.get(i);
            boolean empty = i == 0 && paragraph.equals(" ");
            JMenuItem item = new JMenuItem(MusicUtils.getResume(empty ? "[" + I18N.WORD_EMPTY_O.val() + "]" : paragraph, 64));
            item.setToolTipText("<html><div style='text-align:center;padding: 4px 8px 4px 8px'>" + paragraph.replace("\n", "<br>") + "</div>");
            item.addActionListener(ae -> {
               int indexOfx = this.jPanelParagraphs.getComponentZOrder((Component)selection.get(0));
               if (indexOfx >= 0) {
                  this.jPanelParagraphs.add(new CustomSlidePB(new MusicController.Paragraph(paragraph), PlayBack.DURATION_DEFAULT, this.listener), indexOfx);
                  this.jPanelParagraphs.revalidate();
                  this.jPanelParagraphs.repaint();
                  this.positionChanged();
               }
            });
            addBefore.add(item);
         }

         menu.add(addBefore);
         JMenu addAfter = new JMenu(I18N.MSG_ADD_AFTER.val());

         for (int i = 0; i < paragraphs.size(); i++) {
            String paragraph = paragraphs.get(i);
            boolean empty = i == 0 && paragraph.equals(" ");
            JMenuItem item = new JMenuItem(MusicUtils.getResume(empty ? "[" + I18N.WORD_EMPTY_O.val() + "]" : paragraph, 64));
            if (!empty) {
               item.setToolTipText("<html><div style='text-align:center;padding: 4px 8px 4px 8px'>" + paragraph.replace("\n", "<br>") + "</div>");
            }

            item.addActionListener(
               ae -> {
                  int indexOfx = this.jPanelParagraphs.getComponentZOrder((Component)selection.get(selection.size() - 1));
                  if (indexOfx >= 0) {
                     this.jPanelParagraphs
                        .add(new CustomSlidePB(new MusicController.Paragraph(paragraph), PlayBack.DURATION_DEFAULT, this.listener), indexOfx + 1);
                     this.jPanelParagraphs.revalidate();
                     this.jPanelParagraphs.repaint();
                     this.positionChanged();
                  }
               }
            );
            addAfter.add(item);
         }

         menu.add(addAfter);
         JMenuItem clone = new JMenuItem(I18N.WORD_CLONE.val());
         clone.addActionListener(ae -> {
            int indexOfx = this.jPanelParagraphs.getComponentZOrder((Component)selection.get(selection.size() - 1));
            if (indexOfx >= 0) {
               indexOfx++;

               for (CustomSlidePB csx : selection) {
                  this.jPanelParagraphs.add(new CustomSlidePB(csx.paragraph, csx.getDuration(), this.listener), indexOfx++);
               }

               this.jPanelParagraphs.revalidate();
               this.jPanelParagraphs.repaint();
               this.positionChanged();
            }
         });
         menu.add(clone);
         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val());
         remove.addActionListener(ae -> {
            selection.forEach(this.jPanelParagraphs::remove);
            this.jPanelParagraphs.revalidate();
            this.jPanelParagraphs.repaint();
            this.positionChanged();
         });
         menu.add(remove);
         if (selection.size() == 1) {
            menu.addSeparator();
            CustomSlidePB cs = selection.get(0);
            int indexOf = this.jPanelParagraphs.getComponentZOrder(cs);
            if (indexOf >= 0) {
               JMenuItem editText = new JMenuItem(I18N.MSG_EDIT_TEXT.val());
               editText.addActionListener(ae -> PresentationUtils.editParagraph(cs.paragraph.getText(), true, newValue -> {
                     MusicUtils.setParagraph(this.playBack.getMusic(), indexOf, newValue);
                     this.setup();
                  }));
               menu.add(editText);
            }
         }

         menu.show(this.jPanelParagraphs, evt.getX(), evt.getY());
      }
   }

   @Override
   public void positionChanged() {
      this.music.setOrder(this.getAllSlides().stream().map(cs -> cs.paragraph.getText()).collect(Collectors.toList()));
      this.listener.durationChanged(null);
   }

   private List<CustomSlidePB> getAllSlides() {
      return Stream.of(this.jPanelParagraphs.getComponents())
         .filter(CustomSlidePB.class::isInstance)
         .map(CustomSlidePB.class::cast)
         .collect(Collectors.toList());
   }

   private void close() {
      switch (JOptionPane.showConfirmDialog(this, I18N.MSG_CONFIRM_SAVE_CHANGES.val(), I18N.WORD_CONFIRM.val(), 1)) {
         case 0:
            this.save(true);
            break;
         case 1:
            this.superdispose();
      }
   }

   public void superdispose() {
      super.setVisible(false);
      super.dispose();
      if (PLAYER_MINI != null) {
         PLAYER_MINI.setMedia(null, true);
      }
   }

   private void reset() {
      if (Utils.confirm(this, I18N.WORD_RESET.question())) {
         this.playBack.resetMusicAndDuration();
         this.setup();
      }
   }

   public void save(boolean saveFileAndExit) {
      try {
         SwingUtils.commitEdit(this.jSpinnerStartWithDuration);
         this.getAllSlides().forEach(CustomSlidePB::commit);
         this.playBack.setDuration(this.getAllSlides().stream().map(cs -> cs.getDuration()).collect(Collectors.toList()));
         if (saveFileAndExit) {
            this.playBack.save();
            Utils.msg(this, I18N.MSG_SAVED_SUCCESSFULLY.val());
            this.superdispose();
            Optional.ofNullable(this.callback).ifPresent(Runnable::run);
         }
      } catch (Exception var3) {
         if (saveFileAndExit) {
            Utils.error(this, I18N.MSG_ERROR_SAVING_FILE.val());
         }
      }
   }

   public Container getSelection() {
      this.checkMouseRelease = null;
      List<CustomSlidePB> selected = this.getAllSlides().stream().filter(CustomSlidePB::isSelected).collect(Collectors.toList());
      if (selected.isEmpty()) {
         return null;
      } else {
         int sqrt = (int)Math.ceil(Math.sqrt((double)selected.size()));
         JPanel _return = new JPanel(new GridLayout(sqrt, sqrt, 6, 6));
         _return.setOpaque(false);
         selected.forEach(_return::add);
         return _return;
      }
   }

   @Override
   public Dimension getSlideSize() {
      return SLIDE_SIZE;
   }

   private void setSlideSize(int index) {
      SLIDE_SIZE.setSize(getSlideSize(index));
      this.getAllSlides().forEach(CustomSlidePB::updateSize);
   }

   private static Dimension getSlideSize(int index) {
      switch (index) {
         case 0:
         case 1:
         case 2:
            return EnumSlideSizePreview.values()[index].size;
         default:
            return EnumSlideSizePreview.MEDIUM.size;
      }
   }

   private WebSpinner createSpinnerStartWith() {
      SpinnerNumberModel spinnerModel = new SpinnerNumberModel(
         (double)this.playBack.getStartWithDuration() / 1000.0,
         (double)PlayBack.MIN_DURATION_START_WITH / 1000.0,
         (double)PlayBack.MAX_DURATION_START_WITH / 1000.0,
         0.1
      );
      WebSpinner spinner = new WebSpinner(spinnerModel);
      spinner.setSize(90, 30);
      spinner.setPreferredSize(spinner.getSize());
      spinnerModel.addChangeListener(e -> {
         this.playBack.setStartWithDuration((int)(spinnerModel.getNumber().doubleValue() * 1000.0));
         this.listener.durationChanged(null);
      });
      return spinner;
   }

   private int getStartWithDuration() {
      return this.playBack == null ? 0 : this.playBack.getStartWithDuration();
   }

   private void setupMediaPlayer() {
      synchronized (this) {
         if (PLAYER_MINI == null) {
            PLAYER_MINI = new PlayBackPlayerMini(this.fileMedia);
         } else {
            PLAYER_MINI.setMedia(this.fileMedia, true);
         }
      }

      this.jPanelHeaderCenterBottom.add(PLAYER_MINI);
      WebButton buttonChangeAudio = new WebButton(I18N.MSG_CHANGE_AUDIO.val(), evt -> this.changeAudio());
      buttonChangeAudio.setFocusable(false);
      buttonChangeAudio.setShadeWidth(0);
      buttonChangeAudio.setFontName("Arial").setFontSize(14);
      this.jPanelHeaderCenterBottom.add(buttonChangeAudio);
   }

   private void changeAudio() {
      PlayBackUtils.changeMp3Audio(this.playBack, () -> {
         if (PLAYER_MINI != null) {
            PLAYER_MINI.stop();
            ExportMediaWorker.main(this.playBack, file -> PLAYER_MINI.setMedia(file, true));
         }
      });
   }

   private void cancel() {
      if (Utils.confirm(this, I18N.WORD_CANCEL.question())) {
         this.superdispose();
      }
   }

   private void captureMethod() {
      if (PLAYER_MINI != null) {
         this.save(false);
         CaptureDialog.main(
            this.playBack,
            PLAYER_MINI,
            (saved, list) -> {
               try {
                  if (saved) {
                     this.playBack.setOrderAndStartAt(list, PLAYER_MINI.getDuration() * 1000);
                     this.jSpinnerStartWithDuration
                        .setValue(
                           Utils.range(
                              (double)this.playBack.getStartWithDuration() / 1000.0,
                              (double)PlayBack.MIN_DURATION_START_WITH / 1000.0,
                              (double)PlayBack.MAX_DURATION_START_WITH / 1000.0
                           )
                        );
                  }

                  this.jPanelHeaderCenterBottom.add(PLAYER_MINI, this.jPanelHeaderCenterBottom.getComponentCount() - 1);
                  this.jPanelHeaderCenterBottom.revalidate();
                  if (saved) {
                     this.setup();
                  }
               } catch (Exception var4) {
               }
            }
         );
      }
   }
}
