package com.limagiran.holyrics.view;

import com.limagiran.holyrics.control.MusicController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.DragComponentMouseAdapterMusicOrder;
import com.limagiran.holyrics.util.EnumSlideSizePreview;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MusicUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WrapLayout;
import com.limagiran.holyrics.view.panel.SimpleSlidePreview;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.GroupLayout.Alignment;

public class EditMusicOrderDialog extends JDialog implements DragComponentMouseAdapterMusicOrder.DragComponentListener {
   private static EditMusicOrderDialog INSTANCE;
   protected static final Dimension SLIDE_SIZE = new Dimension(getSlideSize(Settings.EnumSettings2.MUSIC_ORDER_SLIDE_SIZE.getInt()));
   private final Music music;
   private final Runnable callback;
   private int lastSelected;
   private SimpleSlidePreview checkMouseRelease;
   private Filler filler1;
   private JButton jButtonOk;
   private JButton jButtonReset;
   private JComboBox<EnumSlideSizePreview> jComboBoxSlideSize;
   private JLabel jLabelSlideSize;
   private JLabel jLabelTitle;
   private JLabel jLabelTooltip;
   private JPanel jPanel;
   private JPanel jPanelFooter;
   private JPanel jPanelHeader;
   private JPanel jPanelHeaderCenter;
   private JPanel jPanelParagraphs;
   private JScrollPane jScrollPaneParagraphs;

   private EditMusicOrderDialog(Frame parent, Music music, Runnable callback) {
      super(parent, true);
      this.music = music;
      this.callback = callback;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            EditMusicOrderDialog.this.exit();
         }
      });
      this.jButtonReset.addActionListener(evt -> this.reset());
      this.jButtonOk.addActionListener(evt -> this.exit());
      this.jComboBoxSlideSize.addItemListener(evt -> {
         if (evt.getStateChange() == 1) {
            this.setSlideSize(Settings.EnumSettings2.MUSIC_ORDER_SLIDE_SIZE.set(this.jComboBoxSlideSize.getSelectedIndex()));
         }
      });
      this.jPanelParagraphs.setLayout(new WrapLayout(1, 12, 12));
      this.jLabelTitle.setText(this.music.getTitle() + " (" + this.music.getArtist() + ")");
      DragComponentMouseAdapterMusicOrder drag = new DragComponentMouseAdapterMusicOrder(this);
      this.jPanelParagraphs.addMouseListener(drag);
      this.jPanelParagraphs
         .addMouseListener(
            new MouseAdapter() {
               @Override
               public void mousePressed(MouseEvent e) {
                  try {
                     EditMusicOrderDialog.this.checkMouseRelease = null;
                     List<SimpleSlidePreview> all = EditMusicOrderDialog.this.getAllSlides();
                     Component c = EditMusicOrderDialog.this.jPanelParagraphs.getComponentAt(e.getPoint());
                     if (!(c instanceof SimpleSlidePreview)) {
                        all.forEach(SimpleSlidePreview::unselect);
                        return;
                     }

                     SimpleSlidePreview cs = (SimpleSlidePreview)c;
                     if (e.isControlDown()) {
                        cs.select();
                        EditMusicOrderDialog.this.lastSelected = Math.max(all.indexOf(cs), 0);
                     } else if (e.isShiftDown()) {
                        all.forEach(SimpleSlidePreview::unselect);
                        int indexOf = Math.max(all.indexOf(cs), 0);
                        all.subList(Math.min(indexOf, EditMusicOrderDialog.this.lastSelected), Math.max(indexOf, EditMusicOrderDialog.this.lastSelected) + 1)
                           .forEach(SimpleSlidePreview::select);
                     } else if (cs.isSelected()) {
                        EditMusicOrderDialog.this.checkMouseRelease = cs;
                     } else {
                        all.forEach(SimpleSlidePreview::unselect);
                        cs.select();
                        EditMusicOrderDialog.this.lastSelected = Math.max(all.indexOf(cs), 0);
                     }
                  } catch (Exception var6) {
                  }
               }

               @Override
               public void mouseReleased(MouseEvent e) {
                  if (SwingUtilities.isLeftMouseButton(e)) {
                     Optional.ofNullable(EditMusicOrderDialog.this.checkMouseRelease).ifPresent(cs -> {
                        EditMusicOrderDialog.this.getAllSlides().forEach(SimpleSlidePreview::unselect);
                        cs.select();
                     });
                  } else {
                     EditMusicOrderDialog.this.moreOptions(e);
                  }
               }
            }
         );
      this.jPanelParagraphs.addMouseMotionListener(drag);
      this.jScrollPaneParagraphs.getVerticalScrollBar().setUnitIncrement(32);
      this.setup();
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::exit));
      SwingUtils.maximize(this, 150);
   }

   private void initComponents() {
      this.jPanel = new JPanel();
      this.jPanelHeader = new JPanel();
      this.jLabelTitle = new JLabel();
      this.jPanelHeaderCenter = new JPanel();
      this.jLabelSlideSize = new JLabel();
      this.jComboBoxSlideSize = new JComboBox<>();
      this.filler1 = new Filler(new Dimension(64, 0), new Dimension(64, 0), new Dimension(64, 32767));
      this.jButtonReset = new JButton();
      this.jLabelTooltip = new JLabel();
      this.jScrollPaneParagraphs = new JScrollPane();
      this.jPanelParagraphs = new JPanel();
      this.jPanelFooter = new JPanel();
      this.jButtonOk = new JButton();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.order"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(400, 300));
      this.jPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
      this.jPanel.setLayout(new BorderLayout());
      this.jPanelHeader.setLayout(new BorderLayout());
      this.jLabelTitle.setFont(new Font("Georgia", 0, 24));
      this.jLabelTitle.setHorizontalAlignment(0);
      this.jPanelHeader.add(this.jLabelTitle, "First");
      this.jLabelSlideSize.setFont(new Font("Georgia", 0, 14));
      this.jLabelSlideSize.setText(bundle.getString("word.preview"));
      this.jPanelHeaderCenter.add(this.jLabelSlideSize);
      this.jComboBoxSlideSize.setFont(new Font("Georgia", 0, 14));
      this.jComboBoxSlideSize.setModel(new DefaultComboBoxModel<>(EnumSlideSizePreview.values()));
      this.jComboBoxSlideSize
         .setSelectedIndex(Utils.range(Settings.EnumSettings2.MUSIC_ORDER_SLIDE_SIZE.getInt(), 0, EnumSlideSizePreview.values().length - 1));
      this.jComboBoxSlideSize.setFocusable(false);
      this.jPanelHeaderCenter.add(this.jComboBoxSlideSize);
      this.jPanelHeaderCenter.add(this.filler1);
      this.jButtonReset.setFont(new Font("Georgia", 0, 14));
      this.jButtonReset.setText(bundle.getString("word.reset"));
      this.jButtonReset.setFocusable(false);
      this.jPanelHeaderCenter.add(this.jButtonReset);
      this.jPanelHeader.add(this.jPanelHeaderCenter, "Center");
      this.jLabelTooltip.setFont(new Font("Arial", 0, 14));
      this.jLabelTooltip.setForeground(new Color(153, 0, 0));
      this.jLabelTooltip.setHorizontalAlignment(0);
      this.jLabelTooltip.setText(bundle.getString("msg.reorderTooltip"));
      this.jPanelHeader.add(this.jLabelTooltip, "Last");
      this.jPanel.add(this.jPanelHeader, "First");
      this.jPanelParagraphs.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
      GroupLayout jPanelParagraphsLayout = new GroupLayout(this.jPanelParagraphs);
      this.jPanelParagraphs.setLayout(jPanelParagraphsLayout);
      jPanelParagraphsLayout.setHorizontalGroup(jPanelParagraphsLayout.createParallelGroup(Alignment.LEADING).addGap(0, 650, 32767));
      jPanelParagraphsLayout.setVerticalGroup(jPanelParagraphsLayout.createParallelGroup(Alignment.LEADING).addGap(0, 279, 32767));
      this.jScrollPaneParagraphs.setViewportView(this.jPanelParagraphs);
      this.jPanel.add(this.jScrollPaneParagraphs, "Center");
      this.jButtonOk.setFont(new Font("Georgia", 0, 18));
      this.jButtonOk.setText(bundle.getString("word.ok"));
      this.jButtonOk.setFocusable(false);
      this.jPanelFooter.add(this.jButtonOk);
      this.jPanel.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanel, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(Frame parent, Music music, Runnable callback) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.removeAll();
            INSTANCE.setVisible(false);
            INSTANCE.dispose();
         }

         (INSTANCE = new EditMusicOrderDialog(parent, music, callback)).setVisible(true);
      });
   }

   private void setup() {
      this.jPanelParagraphs.removeAll();
      this.music.getParagraphsToPresentation().forEach(p -> {
         SimpleSlidePreview cs = new SimpleSlidePreview(p, SLIDE_SIZE);
         this.jPanelParagraphs.add(cs);
      });
      this.jPanelParagraphs.revalidate();
      this.jPanelParagraphs.repaint();
   }

   private void moreOptions(MouseEvent evt) {
      List<SimpleSlidePreview> selection = this.getAllSlides().stream().filter(SimpleSlidePreview::isSelected).collect(Collectors.toList());
      if (!selection.isEmpty()) {
         JPopupMenu menu = new JPopupMenu();
         JMenu addBefore = new JMenu(I18N.MSG_ADD_BEFORE.val());
         this.music.getParagraphs().forEach(p -> {
            JMenuItem item = new JMenuItem(MusicUtils.getResume(p, 64));
            item.setToolTipText("<html><div style='text-align:center;padding: 4px 8px 4px 8px'>" + p.replace("\n", "<br>") + "</div>");
            item.addActionListener(ae -> {
               int indexOf = this.jPanelParagraphs.getComponentZOrder((Component)selection.get(0));
               if (indexOf >= 0) {
                  this.jPanelParagraphs.add(new SimpleSlidePreview(new MusicController.Paragraph(p), SLIDE_SIZE), indexOf);
                  this.jPanelParagraphs.revalidate();
                  this.jPanelParagraphs.repaint();
                  this.save();
               }
            });
            addBefore.add(item);
         });
         menu.add(addBefore);
         JMenu addAfter = new JMenu(I18N.MSG_ADD_AFTER.val());
         this.music.getParagraphs().forEach(p -> {
            JMenuItem item = new JMenuItem(MusicUtils.getResume(p, 64));
            item.setToolTipText("<html><div style='text-align:center;padding: 4px 8px 4px 8px'>" + p.replace("\n", "<br>") + "</div>");
            item.addActionListener(ae -> {
               int indexOf = this.jPanelParagraphs.getComponentZOrder((Component)selection.get(selection.size() - 1));
               if (indexOf >= 0) {
                  this.jPanelParagraphs.add(new SimpleSlidePreview(new MusicController.Paragraph(p), SLIDE_SIZE), indexOf + 1);
                  this.jPanelParagraphs.revalidate();
                  this.jPanelParagraphs.repaint();
                  this.save();
               }
            });
            addAfter.add(item);
         });
         menu.add(addAfter);
         JMenuItem clone = new JMenuItem(I18N.WORD_CLONE.val());
         clone.addActionListener(ae -> {
            int indexOf = this.jPanelParagraphs.getComponentZOrder((Component)selection.get(selection.size() - 1));
            if (indexOf >= 0) {
               indexOf++;

               for (SimpleSlidePreview cs : selection) {
                  this.jPanelParagraphs.add(new SimpleSlidePreview(cs.paragraph, SLIDE_SIZE), indexOf++);
               }

               this.jPanelParagraphs.revalidate();
               this.jPanelParagraphs.repaint();
               this.save();
            }
         });
         menu.add(clone);
         JMenuItem remove = new JMenuItem(I18N.WORD_REMOVE.val());
         remove.addActionListener(ae -> {
            selection.forEach(this.jPanelParagraphs::remove);
            this.jPanelParagraphs.revalidate();
            this.jPanelParagraphs.repaint();
            this.save();
         });
         menu.add(remove);
         menu.show(this.jPanelParagraphs, evt.getX(), evt.getY());
      }
   }

   private List<SimpleSlidePreview> getAllSlides() {
      return Stream.of(this.jPanelParagraphs.getComponents())
         .filter(SimpleSlidePreview.class::isInstance)
         .map(SimpleSlidePreview.class::cast)
         .collect(Collectors.toList());
   }

   private void reset() {
      if (Utils.confirm(this, I18N.WORD_RESET.question())) {
         this.music.setOrder(new ArrayList<>());
         this.music.save();
         this.setup();
      }
   }

   private void exit() {
      super.setVisible(false);
      super.dispose();
      Optional.ofNullable(this.callback).ifPresent(Runnable::run);
   }

   @Override
   public void positionChanged() {
      this.save();
   }

   public Container getSelection() {
      this.checkMouseRelease = null;
      List<SimpleSlidePreview> selected = this.getAllSlides().stream().filter(SimpleSlidePreview::isSelected).collect(Collectors.toList());
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
      this.getAllSlides().forEach(SimpleSlidePreview::updateSize);
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

   private void save() {
      this.music.setOrder(this.getAllSlides().stream().map(cs -> cs.paragraph.getText()).collect(Collectors.toList()));
      this.music.save();
   }
}
