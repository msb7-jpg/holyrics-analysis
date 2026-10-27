package com.limagiran.holyrics.view.panel;

import com.alee.laf.button.WebButton;
import com.alee.managers.hotkey.Hotkey;
import com.limagiran.holyrics.control.AppMessageController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.AppMessage;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.AppMessageUtils;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.StyleUI;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.WebLafUtils;
import com.limagiran.holyrics.view.ReadyMessagesChooser;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D.Double;
import java.awt.image.BufferedImage;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.Box.Filler;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.DefaultCaret;

public class AppMessagePanel extends JPanel implements AppMessageController.AppMessageListener {
   private boolean scrollIsEnd = true;
   private int newMessageCount;
   private final AppMessagePanel.AppMessagePanelListener listener;
   private Filler filler1;
   private JButton jButtonReadyMessage;
   private JButton jButtonSend;
   private JCheckBox jCheckBoxNotification;
   private JLabel jLabelDivider;
   private JLabel jLabelHelp;
   private JLabel jLabelName;
   private JLabel jLabelNameLabel;
   private JLabel jLabelTitle;
   private JPanel jPanelFooter;
   private JPanel jPanelHeader;
   private JScrollPane jScrollPane;
   private JScrollPane jScrollPaneSend;
   private JTextArea jTextAreaSend;
   private JTextPane jTextPane;

   public AppMessagePanel(AppMessagePanel.AppMessagePanelListener listener) {
      if (listener == null) {
         listener = AppMessagePanel.AppMessagePanelListener.createEmpty();
      }

      this.listener = listener;
      this.initComponents();
      this.init();
   }

   private void init() {
      ((WebButton)this.jButtonSend).setShadeWidth(0);
      ((WebButton)this.jButtonReadyMessage).setShadeWidth(0);
      AppMessageController.addListener(this);
      this.jTextPane.setText(AppMessageUtils.htmlModel(MyProperties.EnumProperties.CHAT_TEXT_FONT_SIZE.getInt()));
      this.jButtonSend.setText("<html><style> div {text-align:center;}</style><div>" + this.jButtonSend.getText() + "<br><code>[Enter]</code></div>");
      this.jTextAreaSend.setDocument(SwingUtils.createDocMaxSize(1024));
      this.jTextAreaSend.addKeyListener(new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            switch (e.getKeyCode()) {
               case 10:
                  if (!e.isControlDown() && !e.isShiftDown()) {
                     AppMessagePanel.this.send();
                     e.consume();
                  } else {
                     AppMessagePanel.this.jTextAreaSend.insert("\n", AppMessagePanel.this.jTextAreaSend.getCaretPosition());
                  }
            }
         }
      });
      this.updateUsername();
      this.jCheckBoxNotification.setSelected(Settings.EnumSettings2.CHAT_NOTIFICATION.is());
      this.jLabelName.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            AppMessagePanel.this.changeUsername(Settings.EnumSettings2.CHAT_USERNAME.getString());
         }
      });
      this.jCheckBoxNotification.addActionListener(evt -> {
         Boolean var10000 = Settings.EnumSettings2.CHAT_NOTIFICATION.set(this.jCheckBoxNotification.isSelected());
      });
      ((DefaultCaret)this.jTextPane.getCaret()).setUpdatePolicy(1);
      this.jTextPane.addHyperlinkListener(e -> {
         if (e.getEventType() == EventType.ACTIVATED) {
            this.link(e.getURL().toString());
         }
      });
      SwingUtils.setupCutCopyPaste(this.jTextPane);
      SwingUtils.setupCCPUAndRedo(this.jTextAreaSend);
      this.jButtonSend.addActionListener(evt -> this.send());
      this.jButtonReadyMessage.addActionListener(evt -> this.readyMessage());
      this.jLabelHelp.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            AppMessagePanel.this.help();
         }
      });
      this.jTextAreaSend.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            if (Utils.isDoubleClick(e)) {
               AppMessagePanel.this.jButtonReadyMessage.doClick(0);
            }
         }
      });
      WebLafUtils.addToolTipButton(this, this.jButtonReadyMessage, Hotkey.CTRL_H);
   }

   private void initComponents() {
      this.jPanelHeader = new JPanel();
      this.jLabelTitle = new JLabel();
      this.jLabelNameLabel = new JLabel();
      this.jLabelDivider = new JLabel();
      this.jLabelName = new JLabel();
      this.filler1 = new Filler(new Dimension(12, 0), new Dimension(12, 0), new Dimension(12, 32767));
      this.jCheckBoxNotification = new JCheckBox();
      this.jLabelHelp = new JLabel();
      this.jScrollPane = new AppMessagePanel.JScrollPaneChat();
      this.jTextPane = new AppMessagePanel.JTextPaneChat();
      this.jPanelFooter = new JPanel();
      this.jScrollPaneSend = new JScrollPane();
      this.jTextAreaSend = new JTextArea();
      this.jButtonSend = new WebButton();
      this.jButtonReadyMessage = new WebButton();
      this.setLayout(new BorderLayout(0, 8));
      this.jPanelHeader.setBorder(BorderFactory.createEmptyBorder(4, 8, 0, 8));
      this.jPanelHeader.setLayout(new GridBagLayout());
      this.jLabelTitle.setFont(new Font("Lucida Sans Unicode", 0, 14));
      this.jLabelTitle.setHorizontalAlignment(2);
      this.jLabelTitle.setText("Chat (App)");
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      this.jPanelHeader.add(this.jLabelTitle, gridBagConstraints);
      this.jLabelNameLabel.setFont(new Font("Arial", 0, 11));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jLabelNameLabel.setText(bundle.getString("word.name"));
      this.jPanelHeader.add(this.jLabelNameLabel, new GridBagConstraints());
      this.jLabelDivider.setFont(new Font("Arial", 0, 11));
      this.jLabelDivider.setText(": ");
      this.jPanelHeader.add(this.jLabelDivider, new GridBagConstraints());
      this.jLabelName.setFont(new Font("Arial", 1, 11));
      this.jLabelName.setCursor(new Cursor(12));
      this.jPanelHeader.add(this.jLabelName, new GridBagConstraints());
      this.jPanelHeader.add(this.filler1, new GridBagConstraints());
      this.jCheckBoxNotification.setFont(new Font("Arial", 0, 11));
      this.jCheckBoxNotification.setText(bundle.getString("word.notification"));
      this.jCheckBoxNotification.setFocusable(false);
      this.jPanelHeader.add(this.jCheckBoxNotification, new GridBagConstraints());
      this.jLabelHelp.setFont(new Font("Courier New", 1, 20));
      this.jLabelHelp.setForeground(new Color(92, 92, 92));
      this.jLabelHelp.setHorizontalAlignment(0);
      this.jLabelHelp.setText("?");
      this.jLabelHelp.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
      this.jLabelHelp.setCursor(new Cursor(12));
      this.jPanelHeader.add(this.jLabelHelp, new GridBagConstraints());
      this.add(this.jPanelHeader, "First");
      this.jScrollPane.setFocusable(false);
      this.jScrollPane.setOpaque(false);
      this.jTextPane.setEditable(false);
      this.jTextPane.setContentType("text/html; charset=UTF-8");
      this.jTextPane.setOpaque(false);
      this.jScrollPane.setViewportView(this.jTextPane);
      this.add(this.jScrollPane, "Center");
      this.jPanelFooter.setBorder(BorderFactory.createEmptyBorder(2, 4, 4, 4));
      this.jPanelFooter.setLayout(new GridBagLayout());
      this.jScrollPaneSend.setHorizontalScrollBarPolicy(31);
      this.jScrollPaneSend.setVerticalScrollBarPolicy(21);
      this.jScrollPaneSend.setFocusable(false);
      this.jTextAreaSend.setColumns(20);
      this.jTextAreaSend.setFont(new Font("Lucida Sans Unicode", 0, 14));
      this.jTextAreaSend.setLineWrap(true);
      this.jTextAreaSend.setRows(3);
      this.jTextAreaSend.setWrapStyleWord(true);
      this.jTextAreaSend.addFocusListener(new FocusAdapter() {
         @Override
         public void focusGained(FocusEvent evt) {
            AppMessagePanel.this.jTextAreaSendFocusGained(evt);
         }
      });
      this.jScrollPaneSend.setViewportView(this.jTextAreaSend);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridheight = 2;
      gridBagConstraints.fill = 1;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(0, 0, 0, 4);
      this.jPanelFooter.add(this.jScrollPaneSend, gridBagConstraints);
      this.jButtonSend.setFont(new Font("Georgia", 0, 14));
      this.jButtonSend.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/send24.png")));
      this.jButtonSend.setText(bundle.getString("word.send"));
      this.jButtonSend.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.ipadx = 4;
      gridBagConstraints.ipady = 4;
      this.jPanelFooter.add(this.jButtonSend, gridBagConstraints);
      this.jButtonReadyMessage.setFont(new Font("Arial", 0, 11));
      this.jButtonReadyMessage.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/checkedText16.png")));
      this.jButtonReadyMessage.setText(I18N.WORD_MESSAGES.val());
      this.jButtonReadyMessage.setToolTipText(bundle.getString("msg.readyMessages"));
      this.jButtonReadyMessage.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 1;
      gridBagConstraints.gridy = 1;
      this.jPanelFooter.add(this.jButtonReadyMessage, gridBagConstraints);
      this.add(this.jPanelFooter, "Last");
   }

   private void jTextAreaSendFocusGained(FocusEvent evt) {
      AppMessageController.cancelNotification();
   }

   public void requestFocusOnChat() {
      SwingUtils.requestFocusOnVisible(this.jTextAreaSend);
   }

   @Override
   public void add(AppMessage message) {
      SwingUtilities.invokeLater(() -> AppMessageUtils.insert(this.jTextPane, message));
      this.setNewMessageCount(this.newMessageCount + 1);
      if (!message.isMyself() && !this.isUserFocus()) {
         AppMessageController.notification(message);
      }
   }

   @Override
   public void remove(AppMessage message) {
      SwingUtilities.invokeLater(() -> AppMessageUtils.remove(this.jTextPane, message));
   }

   private void send(String text) {
      AppMessageController.newMessage("holyrics-admin", Settings.EnumSettings2.CHAT_USERNAME.getString(), text);
   }

   private void send() {
      this.send(this.jTextAreaSend.getText());
      this.jTextAreaSend.setText("");
   }

   private void updateUsername() {
      this.jLabelName.setText("<html><u>" + StringUtils.escapeHTML(Settings.EnumSettings2.CHAT_USERNAME.getString()) + "</u>");
   }

   private void changeUsername(String _default) {
      Utils.Input.create().setInitial(_default).setMsg(I18N.WORD_USERNAME.val()).setTitle("Chat").setParent(this.jLabelName).show(username -> {
         username = username.trim();
         if (!username.replace(" ", "").isEmpty()) {
            if (username.length() > 24) {
               Utils.error(I18N.MSG_ERROR_MAX_LENGTH_USERNAME.format(24));
               this.changeUsername(username);
            } else {
               Settings.EnumSettings2.CHAT_USERNAME.set(username);
               this.updateUsername();
            }
         }
      });
   }

   private void link(String string) {
      try {
         if (string.startsWith("http://name=")) {
            string = string.substring(12) + " ";
            this.jTextAreaSend.insert(string, this.jTextAreaSend.getCaretPosition());
            this.jTextAreaSend.requestFocus();
         }
      } catch (Exception var3) {
      }
   }

   private void help() {
      PopupUtils.helpChat(null);
   }

   private void setScrollIsEnd(boolean flag) {
      this.scrollIsEnd = flag;
      if (this.scrollIsEnd) {
         this.setNewMessageCount(0);
         if (this.isUserFocus()) {
            AppMessageController.cancelNotification();
         }
      }
   }

   private boolean isUserFocus() {
      return this.jTextAreaSend.isFocusOwner() || this.jTextPane.isFocusOwner();
   }

   public void scrollToEnd() {
      SwingUtilities.invokeLater(() -> SwingUtils.scrollToEnd(this.jScrollPane));
   }

   private void setNewMessageCount(int newMessageCount) {
      this.newMessageCount = newMessageCount;
      if (!this.scrollIsEnd || newMessageCount == 0) {
         this.listener.newMessageCount(newMessageCount);
      }
   }

   private void readyMessage() {
      BiConsumer<String, Boolean> consumer = (s, b) -> {
         if (b) {
            this.send(s);
         } else {
            this.jTextAreaSend.insert(s, this.jTextAreaSend.getCaretPosition());
         }
      };
      ReadyMessagesChooser.main(consumer, "chat");
   }

   public interface AppMessagePanelListener {
      static AppMessagePanel.AppMessagePanelListener createEmpty() {
         return new AppMessagePanel.AppMessagePanelListener() {
            @Override
            public void newMessageCount(int newMessageCount) {
            }
         };
      }

      void newMessageCount(int var1);
   }

   private class JScrollPaneChat extends JScrollPane {
      private final TexturePaint bgTexture;

      private JScrollPaneChat() {
         BufferedImage icon = Images.ICON_CHAT_BACKGROUND_PATTERN;
         Rectangle rect = new Rectangle(0, 0, icon.getWidth(), icon.getHeight());
         this.bgTexture = new TexturePaint(icon, rect);
         JScrollBar vsb = this.getVerticalScrollBar();
         vsb.addAdjustmentListener(e -> AppMessagePanel.this.setScrollIsEnd(SwingUtils.scrollIsEnd(AppMessagePanel.this.jScrollPane, 50)));
         this.getViewport().setOpaque(false);
      }

      @Override
      protected void paintComponent(Graphics g) {
         Graphics2D g2 = (Graphics2D)g;
         g2.setPaint(this.bgTexture);
         g2.fill(new Rectangle(0, 0, Math.max(1, this.getWidth()), Math.max(1, this.getHeight())));
         super.paintComponent(g);
      }
   }

   private class JTextPaneChat extends JTextPane {
      private final int iconSize = 37;
      private final int[] iconArrowSize = new int[]{4, 2};
      private int lastCursor = 0;
      private final Font newMessageFont = new Font("Verdana", 1, 8);

      private JTextPaneChat() {
         this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
               int newCursor = JTextPaneChat.this.isAboveGoToEndIcon(e) ? 12 : 0;
               if (newCursor != JTextPaneChat.this.lastCursor) {
                  JTextPaneChat.this.setCursor(Cursor.getPredefinedCursor(JTextPaneChat.this.lastCursor = newCursor));
               }
            }
         });
      }

      @Override
      protected void processMouseEvent(MouseEvent e) {
         if (this.isAboveGoToEndIcon(e)) {
            SwingUtils.scrollToEnd(AppMessagePanel.this.jScrollPane);
         } else {
            super.processMouseEvent(e);
         }
      }

      private Rectangle getRectIconGoToEnd() {
         Rectangle rect = AppMessagePanel.this.jScrollPane.getViewport().getViewRect();
         rect.x = (int)rect.getMaxX() - 48;
         rect.y = (int)rect.getMaxY() - 48;
         rect.width = rect.height = 37;
         return rect;
      }

      private boolean isAboveGoToEndIcon(MouseEvent e) {
         try {
            if (AppMessagePanel.this.scrollIsEnd) {
               return false;
            } else {
               Rectangle r = this.getRectIconGoToEnd();
               return new Double((double)r.x, (double)r.y, (double)r.width, (double)r.height).contains(e.getPoint());
            }
         } catch (Exception var3) {
            return false;
         }
      }

      @Override
      protected void paintComponent(Graphics g) {
         super.paintComponent(g);

         try {
            if (!AppMessagePanel.this.scrollIsEnd) {
               Graphics2D g2 = (Graphics2D)g;
               G2Utils.setupGraphics(g2);
               Rectangle r = this.getRectIconGoToEnd();
               this.paintGoToEndIcon((Graphics2D)g, r);
               if (AppMessagePanel.this.newMessageCount > 0) {
                  this.paintNewMessageCount(g2, r);
               }
            }
         } catch (Exception var4) {
         }
      }

      private void paintGoToEndIcon(Graphics2D g2, Rectangle r) {
         g2.setColor(StyleUI.COLOR_BLUE_ACCENT);
         g2.fillOval(r.x, r.y, r.width, r.height);
         g2.setColor(Color.WHITE);
         g2.setStroke(new BasicStroke((float)r.height * 0.05F));
         int x1 = (int)(r.getCenterX() - (double)this.iconArrowSize[0]);
         int y1 = (int)(r.getCenterY() - (double)this.iconArrowSize[1]);
         int x2 = (int)r.getCenterX();
         int y2 = (int)(r.getCenterY() + (double)this.iconArrowSize[1]);
         int x3 = (int)(r.getCenterX() + (double)this.iconArrowSize[0]);
         g2.drawLine(x1, y1, x2, y2);
         g2.drawLine(x2, y2, x3, y1);
      }

      private void paintNewMessageCount(Graphics2D g2, Rectangle r) {
         r.height = (int)((double)r.height * 0.5);
         r.y = (int)((double)r.y - (double)r.height * 0.33);
         g2.setFont(G2Utils.getBestFont(Math.max(2, (int)((double)r.height * 0.9)), this.newMessageFont, g2));
         g2.setColor(Utils.decodeColor("#B1BCBE", Color.LIGHT_GRAY));
         String text = " " + String.valueOf(AppMessagePanel.this.newMessageCount) + " ";
         int stringWidth = g2.getFontMetrics().stringWidth(text) + 2;
         g2.drawOval((int)(r.getCenterX() - (double)(stringWidth / 2)), r.y, stringWidth, r.height);
         g2.setColor(Color.WHITE);
         g2.fillOval((int)(r.getCenterX() - (double)(stringWidth / 2)), r.y, stringWidth, r.height);
         g2.setColor(Color.BLACK);
         G2Utils.drawString(r, g2, 0.0, G2Utils.VerticalAlign.MIDDLE, text);
      }
   }
}
