package com.limagiran.holyrics.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.colorchooser.WebColorChooserPanel;
import com.alee.laf.filechooser.WebFileChooser;
import com.alee.laf.filechooser.WebFileChooserStyle;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.limagiran.holyrics.event.BackgroundChoiceListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.ImageHL;
import com.limagiran.holyrics.model.theme.Background;
import com.limagiran.holyrics.model.theme.BackgroundAnnouncement;
import com.limagiran.holyrics.model.theme.BackgroundColor;
import com.limagiran.holyrics.model.theme.BackgroundImage;
import com.limagiran.holyrics.model.theme.BackgroundPattern;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.swing.SelectablePanel;
import com.limagiran.holyrics.util.BackgroundChoiceUtils;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WrapLayout;
import com.limagiran.holyrics.view.panel.AvailableImage;
import com.limagiran.holyrics.view.panel.AvailablePattern;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BackgroundChoiceAnnouncement extends JDialog implements BackgroundChoiceListener {
   private static BackgroundChoiceAnnouncement INSTANCE;
   private static final WebFileChooser FILE_CHOOSER_STATIC = SwingUtils.generateWebFileChooser();
   private static final String TAB_IMAGE = "image";
   private static final String TAB_MY_IMAGES = "my_images";
   private static final String TAB_PATTERN = "pattern";
   private static final String TAB_COLOR = "color";
   private static final String TAB_TRANSPARENT = "transparent";
   private static final Dimension MAX_SIZE_IMAGE = new Dimension(1920, 1080);
   private final List<AvailableImage> availableImages = AvailableImage.getAllAvailableImages();
   private final List<AvailablePattern> availablePatterns = AvailablePattern.getAllAvailablePatterns();
   private final List<SelectablePanel> allSelectablePanels = new ArrayList<>();
   private final WebColorChooserPanel webColorChooserPanel;
   private SelectablePanel init;
   private BackgroundChoiceListener listener;
   private JButton jButtonCancel;
   private JButton jButtonOk;
   private JButton jButtonTransparent;
   private JFileChooser jFileChooserMyImage;
   private JLabel jLabelTransparentInfo;
   private JPanel jPanelColor;
   private JPanel jPanelFooter;
   private JPanel jPanelImages;
   private JPanel jPanelMyImages;
   private JPanel jPanelPattern;
   private JPanel jPanelTransparent;
   private JScrollPane jScrollPaneImages;
   private JScrollPane jScrollPaneMyImages;
   private JScrollPane jScrollPanePattern;
   private JTabbedPane jTabbedPane;

   private BackgroundChoiceAnnouncement() {
      Arrays.asList(this.availableImages, this.availablePatterns).forEach(this.allSelectablePanels::addAll);
      this.webColorChooserPanel = new WebColorChooserPanel();
      this.init = null;
      this.listener = Background::hashCode;
      int old = WebFileChooserStyle.dividerLocation;
      WebFileChooserStyle.dividerLocation = 280;
      this.initComponents();
      WebFileChooserStyle.dividerLocation = old;
      this.init();
   }

   private void init() {
      this.jPanelColor.add(this.webColorChooserPanel, new GridBagConstraints());
      String[] exs = new String[]{"jpg", "png", "gif", "bmp"};
      this.jFileChooserMyImage
         .setFileFilter(new FileNameExtensionFilter("Image File " + Arrays.stream(exs).collect(Collectors.joining(", *", "(*", ")")), exs));
      SwingUtils.maximize(this, 50);
      Arrays.asList(this.jPanelImages, this.jPanelPattern).forEach(p -> p.setLayout(new WrapLayout(1, 20, 20)));
      this.jScrollPaneImages.getVerticalScrollBar().setUnitIncrement(AvailableImage.SIZE.height / 2);
      this.jScrollPanePattern.getVerticalScrollBar().setUnitIncrement(AvailablePattern.SIZE.height / 2);
      this.availableImages.stream().forEach(this.jPanelImages::add);
      this.availablePatterns.stream().forEach(this.jPanelPattern::add);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::buttonCancel));
      BackgroundChoiceUtils.setupWebColorChooserPanel(this.webColorChooserPanel, this::buttonOk);
      this.jButtonTransparent.addActionListener(evt -> BackgroundChoiceUtils.colorSelected(this, Values.COLOR_TRANSPARENT));
   }

   private void initComponents() {
      this.jTabbedPane = new WebTabbedPane();
      ((WebTabbedPane)this.jTabbedPane).setTabInsets(new Insets(5, 16, 7, 16));
      this.jScrollPaneMyImages = new JScrollPane();
      this.jPanelMyImages = new JPanel();
      this.jFileChooserMyImage = this.getFileChooser();
      this.jScrollPaneImages = new JScrollPane();
      this.jPanelImages = new JPanel();
      this.jScrollPanePattern = new JScrollPane();
      this.jPanelPattern = new JPanel();
      this.jPanelColor = new JPanel();
      this.jPanelTransparent = new JPanel();
      this.jLabelTransparentInfo = new JLabel();
      this.jButtonTransparent = new WebButton();
      ((WebButton)this.jButtonTransparent).setShadeWidth(0);
      this.jPanelFooter = new JPanel();
      this.jButtonOk = new JButton();
      this.jButtonCancel = new JButton();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.background"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(600, 450));
      this.setModal(true);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowActivated(WindowEvent evt) {
            BackgroundChoiceAnnouncement.this.formWindowActivated(evt);
         }

         @Override
         public void windowClosing(WindowEvent evt) {
            BackgroundChoiceAnnouncement.this.formWindowClosing(evt);
         }
      });
      this.jTabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jTabbedPane.setFocusable(false);
      this.jTabbedPane.setFont(new Font("Arial", 0, 13));
      this.jScrollPaneMyImages.setFocusable(false);
      this.jScrollPaneMyImages.setName("my_images");
      this.jPanelMyImages.setFocusable(false);
      this.jPanelMyImages.setOpaque(false);
      this.jPanelMyImages.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BackgroundChoiceAnnouncement.this.jPanelReleased(evt);
         }
      });
      this.jPanelMyImages.setLayout(new BorderLayout());
      this.jFileChooserMyImage.setAcceptAllFileFilterUsed(false);
      this.jFileChooserMyImage.setControlButtonsAreShown(false);
      this.jFileChooserMyImage.setFocusable(false);
      this.jFileChooserMyImage.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BackgroundChoiceAnnouncement.this.jFileChooserMyImageActionPerformed(evt);
         }
      });
      this.jPanelMyImages.add(this.jFileChooserMyImage, "Center");
      this.jScrollPaneMyImages.setViewportView(this.jPanelMyImages);
      this.jTabbedPane.addTab(bundle.getString("msg.myImages"), this.jScrollPaneMyImages);
      this.jScrollPaneImages.setFocusable(false);
      this.jScrollPaneImages.setName("image");
      this.jPanelImages.setBackground(new Color(30, 30, 30));
      this.jPanelImages.setForeground(new Color(153, 0, 0));
      this.jPanelImages.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BackgroundChoiceAnnouncement.this.jPanelReleased(evt);
         }
      });
      this.jScrollPaneImages.setViewportView(this.jPanelImages);
      this.jTabbedPane.addTab(bundle.getString("word.images"), this.jScrollPaneImages);
      this.jScrollPanePattern.setFocusable(false);
      this.jScrollPanePattern.setName("pattern");
      this.jPanelPattern.setBackground(new Color(30, 30, 30));
      this.jPanelPattern.setForeground(new Color(153, 0, 0));
      this.jPanelPattern.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BackgroundChoiceAnnouncement.this.jPanelReleased(evt);
         }
      });
      this.jScrollPanePattern.setViewportView(this.jPanelPattern);
      this.jTabbedPane.addTab(bundle.getString("word.pattern"), this.jScrollPanePattern);
      this.jPanelColor.setName("color");
      this.jPanelColor.setLayout(new GridBagLayout());
      this.jTabbedPane.addTab(bundle.getString("word.color"), this.jPanelColor);
      this.jPanelTransparent.setName("transparent");
      this.jPanelTransparent.setLayout(new GridBagLayout());
      this.jLabelTransparentInfo.setText(BackgroundChoiceTheme.TRANSPARENT_INFO);
      this.jPanelTransparent.add(this.jLabelTransparentInfo, new GridBagConstraints());
      this.jButtonTransparent.setFont(new Font("Georgia", 0, 18));
      this.jButtonTransparent.setText(bundle.getString("BackgroundChoiceAnnouncement.jButtonTransparent.text"));
      this.jButtonTransparent.setFocusable(false);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.insets = new Insets(24, 0, 0, 0);
      this.jPanelTransparent.add(this.jButtonTransparent, gridBagConstraints);
      this.jTabbedPane.addTab(bundle.getString("word.transparent"), this.jPanelTransparent);
      this.getContentPane().add(this.jTabbedPane, "Center");
      this.jTabbedPane.getAccessibleContext().setAccessibleName("tabbedPane");
      this.jButtonOk.setFont(new Font("Georgia", 0, 16));
      this.jButtonOk.setText(bundle.getString("BackgroundChoiceAnnouncement.jButtonOk.text"));
      this.jButtonOk.setMargin(new Insets(4, 16, 4, 16));
      this.jButtonOk.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BackgroundChoiceAnnouncement.this.jButtonOkActionPerformed(evt);
         }
      });
      this.jPanelFooter.add(this.jButtonOk);
      this.jButtonCancel.setFont(new Font("Georgia", 0, 16));
      this.jButtonCancel.setText(bundle.getString("BackgroundChoiceAnnouncement.jButtonCancel.text"));
      this.jButtonCancel.setMargin(new Insets(4, 16, 4, 16));
      this.jButtonCancel.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            BackgroundChoiceAnnouncement.this.jButtonCancelActionPerformed(evt);
         }
      });
      this.jPanelFooter.add(this.jButtonCancel);
      this.getContentPane().add(this.jPanelFooter, "Last");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jButtonOkActionPerformed(ActionEvent evt) {
      this.buttonOk();
   }

   private void jButtonCancelActionPerformed(ActionEvent evt) {
      this.buttonCancel();
   }

   private void jPanelReleased(MouseEvent evt) {
      if (Utils.isDoubleClick(evt)) {
         this.buttonOk();
      } else if (evt.getComponent() != null) {
         BackgroundChoiceUtils.changeSelectedPanel(evt.getComponent().getComponentAt(evt.getPoint()), this.allSelectablePanels);
      }
   }

   private void formWindowActivated(WindowEvent evt) {
      Component c = this.jTabbedPane.getSelectedComponent();
      if (c instanceof JScrollPane && this.init != null) {
         ((JScrollPane)c).getVerticalScrollBar().setValue(0);
         ((JScrollPane)c).getViewport().scrollRectToVisible(this.init.getPanel().getBounds());
      }
   }

   private void formWindowClosing(WindowEvent evt) {
      this.close();
   }

   private void jFileChooserMyImageActionPerformed(ActionEvent evt) {
      BooleanOptional.runIfTrue(evt.paramString().contains("ApproveSelection"), () -> this.buttonOk());
   }

   public void openDialog(Background b, BackgroundChoiceListener listener) {
      this.listener = listener;
      if (b instanceof BackgroundColor) {
         this.webColorChooserPanel.setColor(((BackgroundColor)b).color);
         this.jTabbedPane.setSelectedComponent(this.jPanelColor);
      } else {
         this.setSelectedPanel(Optional.ofNullable(b).orElse(Theme.BLACK_SCREEN.getBackground()));
      }

      super.setVisible(true);
   }

   private void setSelectedPanel(Background b) {
      this.allSelectablePanels.stream().filter(SelectablePanel::isSelected).forEach(l -> l.setSelected(false));
      this.init = this.allSelectablePanels.stream().filter(l -> l.getId().equals(b.getId())).findFirst().orElse(null);
      JScrollPane scroll = null;
      if (b instanceof BackgroundImage) {
         scroll = this.jScrollPaneImages;
      } else if (b instanceof BackgroundPattern) {
         scroll = this.jScrollPanePattern;
      }

      if (this.init != null) {
         this.init.setSelected(true);
         if (scroll != null) {
            this.jTabbedPane.setSelectedComponent(scroll);
         }
      }
   }

   public static void show(Background init, BackgroundChoiceListener listener) {
      if (INSTANCE != null) {
         INSTANCE.dispose();
         INSTANCE.setVisible(false);
      }

      SwingUtilities.invokeLater(() -> (INSTANCE = new BackgroundChoiceAnnouncement()).openDialog(init, listener));
   }

   private void buttonCancel() {
      this.close();
   }

   private void buttonOk() {
      Component c = this.jTabbedPane.getSelectedComponent();
      if (c != null) {
         String var2 = c.getName();
         switch (var2) {
            case "my_images":
               this.fileSelected();
               break;
            case "image":
            case "pattern":
               this.backgroundSelected();
               break;
            case "color":
               BackgroundChoiceUtils.colorSelected(this, this.webColorChooserPanel.getColor());
         }
      } else {
         Utils.error(this, I18N.MSG_ERROR_TRY_AGAIN.val(), I18N.WORD_ERROR.val());
      }
   }

   private void fileSelected() {
      File f = this.jFileChooserMyImage.getSelectedFile();
      if (f == null) {
         Utils.error(this, I18N.MSG_ERROR_FILE_NOT_SELECTED.val());
      } else {
         ImageIcon icon = new ImageIcon(f.getAbsolutePath());
         if (icon.getIconWidth() <= MAX_SIZE_IMAGE.width && icon.getIconHeight() <= MAX_SIZE_IMAGE.height) {
            try {
               this.onSelected(new BackgroundAnnouncement(ImageHL.create(f, new Dimension(320, 180))));
            } catch (Exception var4) {
               Utils.error(this, I18N.MSG_FILE_ERROR.val());
            }
         } else {
            Utils.error(this, I18N.MSG_ERROR_LARGE_IMAGE.format(MAX_SIZE_IMAGE.width, MAX_SIZE_IMAGE.height));
         }
      }
   }

   private void backgroundSelected() {
      SelectablePanel sp = this.allSelectablePanels.stream().filter(SelectablePanel::isSelected).findFirst().orElse(null);
      if (sp instanceof AvailableImage) {
         this.onSelected(new BackgroundImage(((AvailableImage)sp).url));
      } else if (sp instanceof AvailablePattern) {
         this.onSelected(new BackgroundPattern(((AvailablePattern)sp).url));
      } else {
         Utils.error(this, I18N.MSG_ERROR_FILE_NOT_SELECTED.val());
      }
   }

   private void close() {
      super.dispose();
      Arrays.asList(this.allSelectablePanels, this.availableImages, this.availablePatterns).forEach(List::clear);
   }

   @Override
   public void onSelected(Background bg) {
      this.listener.onSelected(bg);
      this.close();
   }

   private WebFileChooser getFileChooser() {
      return FILE_CHOOSER_STATIC;
   }
}
