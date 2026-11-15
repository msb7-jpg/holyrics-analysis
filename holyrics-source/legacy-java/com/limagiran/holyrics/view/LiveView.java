package com.limagiran.holyrics.view;

import com.alee.extended.window.ComponentMoveAdapter;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.ComponentResizableDelay;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.Img;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.VideoUtils;
import com.limagiran.holyrics.view.panel.DrawableJComponent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;

public class LiveView extends JDialog implements ComponentResizableDelay.ComponentResizableDelayListener {
   private static final ExecutorService LIVE_VIEW_UPDATE_EXECUTOR = Executors.newSingleThreadExecutor();
   private static final Dimension PREFERRED_SIZE = new Dimension(640, 360);
   private static final Dimension MAXIMUM_SIZE = new Dimension(960, 540);
   private static final Dimension MINIMUM_SIZE = new Dimension(320, 180);
   private static LiveView INSTANCE;
   private static BufferedImage PAINT;
   private static BufferedImage PAINT_RESIZED;
   private static final DrawableJComponent COMPONENT_DRAWABLE = new DrawableJComponent(LiveView::getPaintResized, true) {
      {
         this.setCursor(Cursor.getPredefinedCursor(13));
         this.setToolTipText(I18N.MSG_DOUBLE_CLICK_TO_CLOSE.val());
      }

      @Override
      protected void paintComponent(Graphics g) {
         BufferedImage bi = this.image.get();
         if (bi == null) {
            Graphics2D g2 = (Graphics2D)g;
            G2Utils.setupGraphics(g2);
            g2.setColor(StyleUI.FOREGROUND_DARK);
            g2.fill3DRect(0, 0, this.getWidth(), this.getHeight(), true);
         } else {
            super.paintComponent(g);
         }
      }
   };
   private Filler filler1;
   private Filler filler2;
   private JCheckBox jCheckBoxAutoDisplay;
   private JCheckBox jCheckBoxAutoHide;
   private JLabel jLabelHelp;
   private JPanel jPanel;
   private JPanel jPanelFooter;
   private JPanel jPanelHelp;

   private static BufferedImage getPaintResized() {
      return PAINT_RESIZED;
   }

   public LiveView() {
      this.initComponents();
      this.init();
   }

   private void init() {
      this.setTitle(this.getTitle() + "  (" + I18N.MSG_DOUBLE_CLICK_TO_CLOSE.val() + ")");
      this.setIconImage(Images.load("desktop16.png"));
      ComponentMoveAdapter moveAdapter = new ComponentMoveAdapter() {
         public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            if (Utils.isDoubleClick(e)) {
               LiveView.close();
            }
         }
      };
      this.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
      COMPONENT_DRAWABLE.addMouseListener(moveAdapter);
      COMPONENT_DRAWABLE.addMouseMotionListener(moveAdapter);
      COMPONENT_DRAWABLE.addComponentListener(new ComponentResizableDelay(400L, this));
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            LiveView.close();
         }
      });
      this.addComponentListener(new ComponentAdapter() {
         @Override
         public void componentResized(ComponentEvent e) {
            Dimension size = LiveView.this.getSize();
            boolean flag = false;
            if (size.width > LiveView.MAXIMUM_SIZE.width || size.height > LiveView.MAXIMUM_SIZE.height) {
               size.width = Math.min(size.width, LiveView.MAXIMUM_SIZE.width);
               size.height = Math.min(size.height, LiveView.MAXIMUM_SIZE.height);
               flag = true;
            }

            if (size.width < LiveView.MINIMUM_SIZE.width || size.height < LiveView.MINIMUM_SIZE.height) {
               size.width = Math.max(size.width, LiveView.MINIMUM_SIZE.width);
               size.height = Math.max(size.height, LiveView.MINIMUM_SIZE.height);
               flag = true;
            }

            if (flag) {
               LiveView.this.setSize(size);
            }
         }
      });
      this.jPanel.add(COMPONENT_DRAWABLE, "Center");
      this.jCheckBoxAutoDisplay.setSelected(Settings.EnumSettings2.LIVE_VIEW_AUTO_DISPLAY.is());
      this.jCheckBoxAutoDisplay.addActionListener(evt -> {
         Boolean var10000 = Settings.EnumSettings2.LIVE_VIEW_AUTO_DISPLAY.set(this.jCheckBoxAutoDisplay.isSelected());
      });
      this.jCheckBoxAutoHide.setSelected(Settings.EnumSettings2.LIVE_VIEW_AUTO_HIDE.is());
      this.jCheckBoxAutoHide.addActionListener(evt -> {
         Boolean var10000 = Settings.EnumSettings2.LIVE_VIEW_AUTO_HIDE.set(this.jCheckBoxAutoHide.isSelected());
      });
      this.jLabelHelp.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            PopupUtils.helpLive(LiveView.this, null);
         }
      });
   }

   private void initComponents() {
      this.jPanel = new JPanel();
      this.jPanelFooter = new JPanel();
      this.filler1 = new Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(32767, 0));
      this.jCheckBoxAutoDisplay = new JCheckBox();
      this.jCheckBoxAutoHide = new JCheckBox();
      this.jPanelHelp = new JPanel();
      this.filler2 = new Filler(new Dimension(32, 0), new Dimension(32, 0), new Dimension(32, 32767));
      this.jLabelHelp = new JLabel();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.live"));
      this.setAlwaysOnTop(true);
      this.setFocusable(false);
      this.setFocusableWindowState(false);
      this.setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
      this.setPreferredSize(this.getPreferredSizePreference());
      this.jPanel.setBorder(BorderFactory.createEmptyBorder(12, 8, 12, 8));
      this.jPanel.setOpaque(false);
      this.jPanel.setLayout(new BorderLayout(0, 8));
      this.jPanelFooter.setOpaque(false);
      this.jPanelFooter.setLayout(new GridBagLayout());
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelFooter.add(this.filler1, gridBagConstraints);
      this.jCheckBoxAutoDisplay.setFont(new Font("Arial", 0, 12));
      this.jCheckBoxAutoDisplay.setText(bundle.getString("msg.liveViewAutoDisplay"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(4, 0, 2, 0);
      this.jPanelFooter.add(this.jCheckBoxAutoDisplay, gridBagConstraints);
      this.jCheckBoxAutoHide.setFont(new Font("Arial", 0, 12));
      this.jCheckBoxAutoHide.setText(bundle.getString("msg.liveViewAutoHide"));
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.insets = new Insets(2, 0, 4, 0);
      this.jPanelFooter.add(this.jCheckBoxAutoHide, gridBagConstraints);
      this.jPanelHelp.setOpaque(false);
      this.jPanelHelp.setLayout(new FlowLayout(3, 0, 0));
      this.jPanelHelp.add(this.filler2);
      this.jLabelHelp.setFont(new Font("Arial Black", 1, 18));
      this.jLabelHelp.setForeground(new Color(92, 92, 92));
      this.jLabelHelp.setText("?");
      this.jLabelHelp.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jLabelHelp.setCursor(new Cursor(12));
      this.jPanelHelp.add(this.jLabelHelp);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 2;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelFooter.add(this.jPanelHelp, gridBagConstraints);
      this.jPanel.add(this.jPanelFooter, "Last");
      this.getContentPane().add(this.jPanel, "Center");
      this.pack();
   }

   public static void open() {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE == null) {
            INSTANCE = new LiveView();
            Dimension size = VideoUtils.getSizeDisplay();
            int x = (size.width - INSTANCE.getWidth()) / 2;
            INSTANCE.setLocation(x, 80);
            setupBGColor();
         }

         INSTANCE.setVisible(true);
      });
   }

   public static void close() {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.setVisible(false);
         }
      });
   }

   private static void setupBGColor() {
      Color color = PAINT != null ? StyleUI.COLOR_LIVE_VIEW_ON : Color.WHITE;
      if (INSTANCE != null) {
         SwingUtilities.invokeLater(() -> INSTANCE.getContentPane().setBackground(color));
      }
   }

   private Dimension getPreferredSizePreference() {
      try {
         String[] array = Settings.EnumSettings2.LIVE_VIEW_SIZE.getString().split(";");
         return new Dimension(Utils.intOrElse(array[0], PREFERRED_SIZE.width), Utils.intOrElse(array[1], PREFERRED_SIZE.height));
      } catch (Exception var2) {
         return new Dimension(PREFERRED_SIZE);
      }
   }

   public static synchronized void clear() {
      setImage(null, null);
   }

   public static void setImage(Dimension size, Function<Dimension, BufferedImage> generate) {
      try {
         LIVE_VIEW_UPDATE_EXECUTOR.submit(() -> {
            if (size != null && generate != null) {
               Dimension _size = Utils.maximum(size, MAXIMUM_SIZE);
               BufferedImage paint = generate.apply(_size);
               if (paint != PAINT) {
                  PAINT = paint;
                  PAINT_RESIZED = null;
                  if (PAINT == null) {
                     PAINT_RESIZED = null;
                  } else {
                     adjustImage();
                  }
               }
            } else {
               PAINT = null;
               PAINT_RESIZED = null;
               adjustImage();
            }
         });
      } catch (Exception var3) {
      }
   }

   @Override
   public void trigger() {
      adjustImage();
      Settings.EnumSettings2.LIVE_VIEW_SIZE.set(this.getWidth() + ";" + this.getHeight());
   }

   private static void adjustImage() {
      if (PAINT != null) {
         PAINT_RESIZED = Img.resize(PAINT, COMPONENT_DRAWABLE.getDrawableAreaSize());
      }

      setupBGColor();
      COMPONENT_DRAWABLE.repaint();
   }

   public static void fireStartPresentation() {
      if (Settings.EnumSettings2.LIVE_VIEW_AUTO_DISPLAY.is()) {
         open();
      }
   }

   public static void fireFinishPresentation() {
      if (Settings.EnumSettings2.LIVE_VIEW_AUTO_HIDE.is()) {
         close();
      }
   }
}
