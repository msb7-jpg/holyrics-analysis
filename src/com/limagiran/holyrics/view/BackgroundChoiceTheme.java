package com.limagiran.holyrics.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.colorchooser.WebColorChooserPanel;
import com.alee.laf.tabbedpane.WebTabbedPane;
import com.limagiran.holyrics.control.MyImagesController;
import com.limagiran.holyrics.control.MyVideosController;
import com.limagiran.holyrics.event.BackgroundChoiceListener;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.ImageHL;
import com.limagiran.holyrics.model.VideoHL;
import com.limagiran.holyrics.model.theme.Background;
import com.limagiran.holyrics.model.theme.BackgroundColor;
import com.limagiran.holyrics.model.theme.BackgroundImage;
import com.limagiran.holyrics.model.theme.BackgroundMyImage;
import com.limagiran.holyrics.model.theme.BackgroundMyVideo;
import com.limagiran.holyrics.model.theme.BackgroundPattern;
import com.limagiran.holyrics.model.theme.BackgroundVideo;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.swing.SelectablePanel;
import com.limagiran.holyrics.util.BackgroundChoiceUtils;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Dir;
import com.limagiran.holyrics.util.FFMPEG;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WrapLayout;
import com.limagiran.holyrics.view.panel.AvailableImage;
import com.limagiran.holyrics.view.panel.AvailableMyImages;
import com.limagiran.holyrics.view.panel.AvailableMyVideos;
import com.limagiran.holyrics.view.panel.AvailablePattern;
import com.limagiran.holyrics.view.panel.AvailableVideo;
import com.limagiran.holyrics.worker.SelectMyVideosWorker;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;

public class BackgroundChoiceTheme extends JDialog implements BackgroundChoiceListener {
   private static BackgroundChoiceTheme INSTANCE;
   public static final String TRANSPARENT_INFO = createTransparentInfo();
   private static final String TAB_IMAGE = "image";
   private static final String TAB_MY_IMAGES = "my_images";
   private static final String TAB_MY_VIDEOS = "my_videos";
   private static final String TAB_PATTERN = "pattern";
   private static final String TAB_ANIMATION = "animation";
   private static final String TAB_COLOR = "color";
   private static final String TAB_TRANSPARENT = "transparent";
   private static final Dimension MAX_SIZE_IMAGE = new Dimension(1920, 1080);
   private final List<AvailableMyImages> availableMyImages = this.getAllAvailableMyImages();
   private final List<AvailableMyVideos> availableMyVideos = this.getAllAvailableMyVideos();
   private final List<AvailableImage> availableImages = AvailableImage.getAllAvailableImages();
   private final List<AvailableVideo> availableVideos = AvailableVideo.getAllAvailableVideos();
   private final List<AvailablePattern> availablePatterns = AvailablePattern.getAllAvailablePatterns();
   private final List<SelectablePanel> allSelectablePanels = new ArrayList<>();
   private final WebColorChooserPanel webColorChooserPanel;
   private SelectablePanel init;
   private BackgroundChoiceListener listener;
   private boolean initRemoved;
   private JButton jButtonCancel;
   private JButton jButtonOk;
   private JButton jButtonTransparent;
   private JLabel jLabelTransparentInfo;
   private JPanel jPanelAnimations;
   private JPanel jPanelColor;
   private JPanel jPanelFooter;
   private JPanel jPanelImages;
   private JPanel jPanelMyImages;
   private JPanel jPanelMyVideos;
   private JPanel jPanelPattern;
   private JPanel jPanelTransparent;
   private JScrollPane jScrollPaneAnimations;
   private JScrollPane jScrollPaneImages;
   private JScrollPane jScrollPaneMyImages;
   private JScrollPane jScrollPaneMyVideos;
   private JScrollPane jScrollPanePattern;
   private JTabbedPane jTabbedPane;

   private BackgroundChoiceTheme() {
      Arrays.asList(this.availableMyImages, this.availableMyVideos, this.availableImages, this.availablePatterns, this.availableVideos)
         .forEach(this.allSelectablePanels::addAll);
      this.webColorChooserPanel = new WebColorChooserPanel();
      this.init = null;
      this.listener = Background::hashCode;
      this.initRemoved = false;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jButtonOk.addActionListener(e -> this.buttonOk());
      this.jButtonCancel.addActionListener(e -> this.buttonCancel());
      this.jPanelColor.add(this.webColorChooserPanel, new GridBagConstraints());
      SwingUtils.maximize(this, 50);
      Arrays.asList(this.jPanelMyImages, this.jPanelMyVideos, this.jPanelImages, this.jPanelPattern, this.jPanelAnimations)
         .forEach(p -> p.setLayout(new WrapLayout(1, 20, 20)));
      this.jScrollPaneMyImages.getVerticalScrollBar().setUnitIncrement(AvailableMyImages.SIZE.height / 2);
      this.jScrollPaneMyVideos.getVerticalScrollBar().setUnitIncrement(AvailableMyVideos.SIZE.height / 2);
      this.jScrollPaneImages.getVerticalScrollBar().setUnitIncrement(AvailableImage.SIZE.height / 2);
      this.jScrollPanePattern.getVerticalScrollBar().setUnitIncrement(AvailablePattern.SIZE.height / 2);
      this.jScrollPaneAnimations.getVerticalScrollBar().setUnitIncrement(AvailableVideo.SIZE.height / 2);
      this.availableMyImages.stream().forEach(this.jPanelMyImages::add);
      this.availableMyVideos.stream().forEach(this.jPanelMyVideos::add);
      this.jPanelMyImages
         .add(BackgroundChoiceUtils.createButtonAddBackground(e -> this.addMyImage(), I18N.MSG_CLICK_TO_ADD_IMAGE.val(), AvailableMyImages.SIZE));
      this.jPanelMyVideos
         .add(BackgroundChoiceUtils.createButtonAddBackground(e -> this.addMyVideo(), I18N.MSG_CLICK_TO_ADD_VIDEO.val(), AvailableMyVideos.SIZE));
      this.availableImages.stream().forEach(this.jPanelImages::add);
      this.availablePatterns.stream().forEach(this.jPanelPattern::add);
      this.availableVideos.stream().forEach(this.jPanelAnimations::add);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::buttonCancel));
      BackgroundChoiceUtils.setupWebColorChooserPanel(this.webColorChooserPanel, this::buttonOk);
      this.jButtonTransparent.addActionListener(evt -> BackgroundChoiceUtils.colorSelected(this, Values.COLOR_TRANSPARENT));
   }

   private void initComponents() {
      this.jTabbedPane = new WebTabbedPane();
      ((WebTabbedPane)this.jTabbedPane).setTabInsets(new Insets(5, 16, 7, 16));
      this.jScrollPaneMyImages = new JScrollPane();
      this.jPanelMyImages = new JPanel();
      this.jScrollPaneMyVideos = new JScrollPane();
      this.jPanelMyVideos = new JPanel();
      this.jScrollPaneImages = new JScrollPane();
      this.jPanelImages = new JPanel();
      this.jScrollPanePattern = new JScrollPane();
      this.jPanelPattern = new JPanel();
      this.jScrollPaneAnimations = new JScrollPane();
      this.jPanelAnimations = new JPanel();
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
            BackgroundChoiceTheme.this.formWindowActivated(evt);
         }

         @Override
         public void windowClosing(WindowEvent evt) {
            BackgroundChoiceTheme.this.formWindowClosing(evt);
         }
      });
      this.jTabbedPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
      this.jTabbedPane.setFocusable(false);
      this.jTabbedPane.setFont(new Font("Arial", 0, 13));
      this.jScrollPaneMyImages.setFocusable(false);
      this.jScrollPaneMyImages.setName("my_images");
      this.jPanelMyImages.setBackground(new Color(30, 30, 30));
      this.jPanelMyImages.setForeground(new Color(153, 0, 0));
      this.jPanelMyImages.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BackgroundChoiceTheme.this.jPanelReleased(evt);
         }
      });
      this.jScrollPaneMyImages.setViewportView(this.jPanelMyImages);
      this.jTabbedPane.addTab(bundle.getString("msg.myImages"), this.jScrollPaneMyImages);
      this.jScrollPaneMyVideos.setFocusable(false);
      this.jScrollPaneMyVideos.setName("my_videos");
      this.jPanelMyVideos.setBackground(new Color(30, 30, 30));
      this.jPanelMyVideos.setForeground(new Color(153, 0, 0));
      this.jPanelMyVideos.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BackgroundChoiceTheme.this.jPanelReleased(evt);
         }
      });
      this.jScrollPaneMyVideos.setViewportView(this.jPanelMyVideos);
      this.jTabbedPane.addTab(bundle.getString("msg.myVideos"), this.jScrollPaneMyVideos);
      this.jScrollPaneImages.setFocusable(false);
      this.jScrollPaneImages.setName("image");
      this.jPanelImages.setBackground(new Color(30, 30, 30));
      this.jPanelImages.setForeground(new Color(153, 0, 0));
      this.jPanelImages.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BackgroundChoiceTheme.this.jPanelReleased(evt);
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
            BackgroundChoiceTheme.this.jPanelReleased(evt);
         }
      });
      this.jScrollPanePattern.setViewportView(this.jPanelPattern);
      this.jTabbedPane.addTab(bundle.getString("word.pattern"), this.jScrollPanePattern);
      this.jScrollPaneAnimations.setFocusable(false);
      this.jScrollPaneAnimations.setName("animation");
      this.jPanelAnimations.setBackground(new Color(30, 30, 30));
      this.jPanelAnimations.setForeground(new Color(153, 0, 0));
      this.jPanelAnimations.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            BackgroundChoiceTheme.this.jPanelReleased(evt);
         }
      });
      this.jScrollPaneAnimations.setViewportView(this.jPanelAnimations);
      this.jTabbedPane.addTab(bundle.getString("word.animation"), this.jScrollPaneAnimations);
      this.jPanelColor.setName("color");
      this.jPanelColor.setLayout(new GridBagLayout());
      this.jTabbedPane.addTab(bundle.getString("word.color"), this.jPanelColor);
      this.jPanelTransparent.setName("transparent");
      this.jPanelTransparent.setLayout(new GridBagLayout());
      this.jLabelTransparentInfo.setText(TRANSPARENT_INFO);
      this.jPanelTransparent.add(this.jLabelTransparentInfo, new GridBagConstraints());
      this.jButtonTransparent.setFont(new Font("Georgia", 0, 18));
      this.jButtonTransparent.setText(bundle.getString("word.transparent"));
      this.jButtonTransparent.setFocusable(false);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridy = 1;
      gridBagConstraints.insets = new Insets(24, 0, 0, 0);
      this.jPanelTransparent.add(this.jButtonTransparent, gridBagConstraints);
      this.jTabbedPane.addTab(bundle.getString("word.transparent"), this.jPanelTransparent);
      this.getContentPane().add(this.jTabbedPane, "Center");
      this.jTabbedPane.getAccessibleContext().setAccessibleName("tabbedPane");
      this.jButtonOk.setFont(new Font("Georgia", 0, 16));
      this.jButtonOk.setText(bundle.getString("BackgroundChoiceTheme.jButtonOk.text"));
      this.jButtonOk.setMargin(new Insets(4, 16, 4, 16));
      this.jPanelFooter.add(this.jButtonOk);
      this.jButtonCancel.setFont(new Font("Georgia", 0, 16));
      this.jButtonCancel.setText(bundle.getString("BackgroundChoiceTheme.jButtonCancel.text"));
      this.jButtonCancel.setMargin(new Insets(4, 16, 4, 16));
      this.jPanelFooter.add(this.jButtonCancel);
      this.getContentPane().add(this.jPanelFooter, "Last");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jPanelReleased(MouseEvent evt) {
      if (Utils.isDoubleClick(evt)) {
         this.buttonOk();
      } else if (evt.getComponent() != null) {
         BackgroundChoiceUtils.changeSelectedPanel(evt.getComponent().getComponentAt(evt.getPoint()), this.allSelectablePanels);
      }
   }

   private void formWindowActivated(WindowEvent evt) {
      this.moveScrollToSelected(this.init);
   }

   private void formWindowClosing(WindowEvent evt) {
      if (this.initRemoved) {
         this.onSelected(new BackgroundColor(new Color(40, 40, 40)));
      } else {
         this.close();
      }
   }

   public void openDialog(Background b, BackgroundChoiceListener listener) {
      this.listener = listener;
      if (b instanceof BackgroundColor) {
         Color color = ((BackgroundColor)b).color;
         if (color.getRGB() == Values.COLOR_TRANSPARENT.getRGB()) {
            this.jTabbedPane.setSelectedComponent(this.jPanelTransparent);
         } else {
            this.webColorChooserPanel.setColor(color);
            this.jTabbedPane.setSelectedComponent(this.jPanelColor);
         }
      } else {
         this.setSelectedPanel(Optional.ofNullable(b).orElse(Theme.BLACK_SCREEN.getBackground()));
      }

      super.setVisible(true);
   }

   private void setSelectedPanel(Background b) {
      this.allSelectablePanels.stream().filter(SelectablePanel::isSelected).forEach(l -> l.setSelected(false));
      this.init = this.allSelectablePanels.stream().filter(l -> l.getId().equals(b.getId())).findFirst().orElse(null);
      JScrollPane scroll = null;
      if (b instanceof BackgroundMyImage) {
         scroll = this.jScrollPaneMyImages;
      } else if (b instanceof BackgroundMyVideo) {
         scroll = this.jScrollPaneMyVideos;
      } else if (b instanceof BackgroundImage) {
         scroll = this.jScrollPaneImages;
      } else if (b instanceof BackgroundPattern) {
         scroll = this.jScrollPanePattern;
      } else if (b instanceof BackgroundVideo) {
         scroll = this.jScrollPaneAnimations;
      }

      if (this.init != null) {
         this.init.setSelected(true);
         if (scroll != null) {
            this.jTabbedPane.setSelectedComponent(scroll);
         }
      }
   }

   public static void show(Background init, BackgroundChoiceListener listener) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.dispose();
            INSTANCE.setVisible(false);
            INSTANCE.removeAll();
         }

         (INSTANCE = new BackgroundChoiceTheme()).openDialog(init, listener);
      });
   }

   private void buttonCancel() {
      if (this.initRemoved) {
         this.onSelected(new BackgroundColor(new Color(40, 40, 40)));
      } else {
         this.close();
      }
   }

   private void buttonOk() {
      Component c = this.jTabbedPane.getSelectedComponent();
      if (c != null) {
         String var2 = c.getName();
         switch (var2) {
            case "my_images":
            case "my_videos":
            case "image":
            case "pattern":
            case "animation":
               this.backgroundSelected();
               break;
            case "color":
               BackgroundChoiceUtils.colorSelected(this, this.webColorChooserPanel.getColor());
         }
      } else {
         Utils.error(this, I18N.MSG_ERROR_TRY_AGAIN.val(), I18N.WORD_ERROR.val());
      }
   }

   private void backgroundSelected() {
      SelectablePanel sp = this.allSelectablePanels.stream().filter(SelectablePanel::isSelected).findFirst().orElse(null);
      if (sp instanceof AvailableMyImages) {
         this.onSelected(new BackgroundMyImage(((AvailableMyImages)sp).imageByteId));
      } else if (sp instanceof AvailableMyVideos) {
         this.onSelected(new BackgroundMyVideo(((AvailableMyVideos)sp).videoByteId));
      } else if (sp instanceof AvailableImage) {
         this.onSelected(new BackgroundImage(((AvailableImage)sp).url));
      } else if (sp instanceof AvailablePattern) {
         this.onSelected(new BackgroundPattern(((AvailablePattern)sp).url));
      } else if (sp instanceof AvailableVideo) {
         AvailableVideo v = (AvailableVideo)sp;
         if (v.isAvailable()) {
            this.onSelected(new BackgroundVideo(v.video.fileName, v.video.hash, v.video.imageUrl));
         } else if (!v.isDownloading() && Utils.confirm(this, I18N.MSG_CONFIRM_DOWNLOAD_VIDEO.val())) {
            v.download();
         }
      } else {
         Utils.error(this, I18N.MSG_ERROR_FILE_NOT_SELECTED.val());
      }
   }

   private void addMyImage() {
      if (MyImagesController.fileSize() >= 16777216L) {
         Utils.error(I18N.MSG_MEMORY_LIMIT_REACHED.val());
      } else {
         Optional.ofNullable(Dir.windowsImage(I18N.MSG_CLICK_TO_ADD_IMAGE.val())).ifPresent(f -> {
            try {
               ImageIcon icon = new ImageIcon(f.getAbsolutePath());
               if (icon.getIconWidth() > MAX_SIZE_IMAGE.width || icon.getIconHeight() > MAX_SIZE_IMAGE.height) {
                  Utils.error(this, I18N.MSG_ERROR_LARGE_IMAGE.format(MAX_SIZE_IMAGE.width, MAX_SIZE_IMAGE.height));
                  return;
               }

               this.insert(ImageHL.create(f, new Dimension(320, 180)));
            } catch (Exception var3) {
               Utils.error(this, I18N.MSG_FILE_ERROR.val());
            }
         });
      }
   }

   private void addMyVideo() {
      if (MyVideosController.fileSize() >= 20971520L) {
         Utils.error(I18N.MSG_MEMORY_LIMIT_REACHED.val());
      } else {
         Runnable run = () -> {
            String[] validExtensions = new String[]{".mp4", ".avi", ".mpg", ".wmv", ".3gp", ".mkv", ".flv"};
            Optional.ofNullable(Dir.windowsFile(I18N.MSG_CLICK_TO_ADD_VIDEO.val(), "Video File", validExtensions))
               .ifPresent(f -> PopUpWorker.exe(new SelectMyVideosWorker(this, f, this::insert)));
         };
         if (FFMPEG.checkFile()) {
            run.run();
         } else if (Utils.confirm(this, I18N.MSG_ASK_FFFMPEG_NOT_FOUND.val())) {
            FFMPEG.download(this, run);
         }
      }
   }

   private void insert(VideoHL video) {
      AvailableMyVideos amv = this.createDefaultAV(video);
      MyVideosController.add(video);
      this.insert(amv, this.jPanelMyVideos);
   }

   private void insert(ImageHL image) {
      AvailableMyImages ami = this.createDefaultAI(image);
      MyImagesController.add(image);
      this.insert(ami, this.jPanelMyImages);
   }

   private <T extends JComponent & SelectablePanel> void insert(T t, JPanel panel) {
      this.allSelectablePanels.add(t);
      panel.add(t, panel.getComponentCount() - 1);
      panel.revalidate();
      panel.repaint();
      BackgroundChoiceUtils.changeSelectedPanel(t, this.allSelectablePanels);
      SwingUtilities.invokeLater(() -> this.moveScrollToSelected(t));
   }

   private void removeMyImage(AvailableMyImages ai) {
      if (Utils.confirm(I18N.MSG_CONFIRM_REMOVE_IMAGE.val())) {
         MyImagesController.remove(ai.imageByteId);
         this.remove(ai, this.jPanelMyImages, this.availableMyImages);
         this.initRemoved = this.init != null && this.init.getId().equals("MyImage" + ai.imageByteId);
      }
   }

   private void removeMyVideo(AvailableMyVideos av) {
      if (Utils.confirm(I18N.MSG_CONFIRM_REMOVE_VIDEO.val())) {
         VideoHL v = MyVideosController.get(av.videoByteId);
         MyVideosController.remove(av.videoByteId);
         BooleanOptional.runIfTrue(v != null, () -> v.deleteVideo());
         this.remove(av, this.jPanelMyVideos, this.availableMyVideos);
         this.initRemoved = this.init != null && this.init.getId().equals("MyVideo" + av.videoByteId);
      }
   }

   private <T extends JComponent & SelectablePanel> void remove(T t, JPanel panel, List<? extends SelectablePanel> list) {
      list.remove(t);
      this.allSelectablePanels.remove(t);
      panel.remove(t);
      panel.revalidate();
      panel.repaint();
      list.stream().filter(SelectablePanel::isSelected).forEach(v -> v.setSelected(false));
   }

   private void close() {
      super.dispose();
      super.removeAll();
      Arrays.asList(
            this.allSelectablePanels, this.availableMyImages, this.availableMyVideos, this.availableImages, this.availableVideos, this.availablePatterns
         )
         .forEach(List::clear);
      this.availableMyImages.forEach(SelectablePanel::clear);
      this.availableMyVideos.forEach(SelectablePanel::clear);
      INSTANCE = null;
   }

   public List<AvailableMyImages> getAllAvailableMyImages() {
      return MyImagesController.get().stream().map(img -> this.createDefaultAI(img)).collect(Collectors.toList());
   }

   public List<AvailableMyVideos> getAllAvailableMyVideos() {
      return MyVideosController.get().stream().map(img -> this.createDefaultAV(img)).collect(Collectors.toList());
   }

   public AvailableMyImages createDefaultAI(ImageHL img) {
      final String id = "MyImage" + img.id;
      return new AvailableMyImages(img) {
         @Override
         public String getId() {
            return id;
         }

         @Override
         public void remove() {
            BackgroundChoiceTheme.this.removeMyImage(this);
         }
      };
   }

   public AvailableMyVideos createDefaultAV(VideoHL video) {
      final String id = "MyVideo" + video.id;
      return new AvailableMyVideos(video) {
         @Override
         public String getId() {
            return id;
         }

         @Override
         public void remove() {
            BackgroundChoiceTheme.this.removeMyVideo(this);
         }
      };
   }

   @Override
   public void onSelected(Background bg) {
      this.listener.onSelected(bg);
      this.close();
   }

   private static String createTransparentInfo() {
      return "<html><div style='width:400px;text-align:justify;font-size:12px;'>"
         + I18N.MSG_BG_TRANSPARENT_INFO.format(Images.getImageHTMLTagBase64("margin16.png"));
   }

   private void moveScrollToSelected(SelectablePanel sp) {
      try {
         Component c = this.jTabbedPane.getSelectedComponent();
         if (c instanceof JScrollPane && sp != null) {
            ((JScrollPane)c).getVerticalScrollBar().setValue(0);
            ((JScrollPane)c).getViewport().scrollRectToVisible(sp.getPanel().getBounds());
         }
      } catch (Exception var3) {
      }
   }
}
