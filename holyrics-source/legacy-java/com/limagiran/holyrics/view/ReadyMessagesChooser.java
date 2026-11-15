package com.limagiran.holyrics.view;

import com.alee.laf.button.WebButton;
import com.alee.laf.label.WebLabel;
import com.alee.laf.menu.WebMenuItem;
import com.alee.laf.menu.WebPopupMenu;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.radiobutton.WebRadioButton;
import com.alee.laf.scroll.WebScrollPane;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.swing.DragComponentMouseAdapterWithRolloverAction;
import com.limagiran.holyrics.util.G2Utils;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MyProperties;
import com.limagiran.holyrics.util.PopupUtils;
import com.limagiran.holyrics.util.RWObj;
import com.limagiran.holyrics.util.StringUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import com.limagiran.holyrics.util.Values;
import com.limagiran.holyrics.util.WrapLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

public class ReadyMessagesChooser extends JDialog implements DragComponentMouseAdapterWithRolloverAction.DragComponentListenerWithRolloverAction, Values {
   private static final boolean UNIQUE_FILE = MyProperties.EnumProperties.READY_MESSAGES_UNIQUE_FILE.is();
   private WebPanel panelList;
   private WebScrollPane scrollList;
   private final BiConsumer<String, Boolean> callback;
   private final boolean enableSend;
   private final File file;

   public ReadyMessagesChooser(BiConsumer<String, Boolean> callback, String name, boolean enableSend) {
      super((Frame)null, true);
      this.callback = callback;
      this.enableSend = enableSend;
      this.file = new File(PATH_FILES + "readyMessages" + (UNIQUE_FILE ? "" : "-" + name) + ".hly");
      this.init();
   }

   public static void main(BiConsumer<String, Boolean> callback, String name, boolean enableSend) {
      SwingUtilities.invokeLater(() -> new ReadyMessagesChooser(callback, name, enableSend).setVisible(true));
   }

   public static void main(BiConsumer<String, Boolean> callback, String name) {
      main(callback, name, true);
   }

   private void init() {
      this.setLayout(new BorderLayout());
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setTitle(I18N.MSG_READY_MESSAGES.val());
      InsertShortcut.addEscToDispose(this);
      this.panelList = new WebPanel();
      this.panelList.setLayout(new WrapLayout(0, 0, 0));
      this.panelList.setMargin(6);
      List list = Optional.ofNullable(RWObj.read(this.file, List.class)).orElse(new ArrayList());
      DragComponentMouseAdapterWithRolloverAction adapter = new DragComponentMouseAdapterWithRolloverAction(this);
      this.panelList.addMouseListener(adapter);
      this.panelList.addMouseMotionListener(adapter);
      list.forEach(o -> this.panelList.add(new ReadyMessagesChooser.ReadyMessageItem(String.valueOf(o))));
      this.scrollList = new WebScrollPane(this.panelList, false);
      WebPanel scrollHeader = new WebPanel(new BorderLayout(8, 0));
      scrollHeader.setMargin(8, 13, 8, 6);
      scrollHeader.add(new WebLabel(" ") {
         {
            this.setFont(new Font("Courier New", 1, 13));
            this.setMargin(3, 6, 3, 6);
            this.setBackground(new Color(60, 60, 60));
            this.setForeground(Color.WHITE);
            this.setOpaque(true);
            this.setCursor(Cursor.getPredefinedCursor(12));
            this.addMouseListener(new MouseAdapter() {
               @Override
               public void mouseReleased(MouseEvent e) {
                  PopupUtils.helpReadyMessages(null);
               }
            });
         }

         protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Dimension d = this.getSize();
            Rectangle r = new Rectangle(d);
            if (r.width > 0 && r.height > 0) {
               Graphics2D g2 = (Graphics2D)g;
               G2Utils.setupGraphics(g2);
               g2.fillRect((int)r.getCenterX() - 1, 3, 2, r.height - 8);
               r.setBounds((int)r.getCenterX() - 4, r.height - 8, 8, 4);
               g2.fillPolygon(new int[]{r.x, (int)r.getCenterX(), (int)r.getMaxX()}, new int[]{r.y, (int)r.getMaxY(), r.y}, 3);
               g2.setFont(g2.getFont().deriveFont(11.0F));
               G2Utils.drawString(new Rectangle(d.width / 2 + 1, -1, d.width / 2, d.height), g2, 0.0, G2Utils.VerticalAlign.TOP, "?");
            }
         }
      }, "West");
      WebButton buttonAdd = new WebButton(I18N.WORD_NEW.val());
      buttonAdd.setFocusable(false);
      buttonAdd.setShadeWidth(0);
      buttonAdd.addActionListener(evt -> this.add());
      String label = "<html><b>" + I18N.WORD_SHORTCUTS.val() + "</b> (" + I18N.MSG_CLICK_RIGHT_BUTTON_MORE_OPTIONS.val() + ")";
      scrollHeader.add(new WebLabel(label, 2, new Object[0]), "Center");
      scrollHeader.add(buttonAdd, "East");
      this.scrollList.setColumnHeaderView(scrollHeader);
      this.scrollList.getVerticalScrollBar().setUnitIncrement(48);
      this.scrollList.setFocusable(false);
      this.add(this.scrollList, "Center");
      this.pack();
      this.setSize(new Dimension(750, 500));
      this.setPreferredSize(this.getSize());
      this.setLocationRelativeTo(null);
      this.setResizable(false);
      this.addKeyListener(
         new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
               try {
                  int index = -1;
                  int key = e.getKeyCode();
                  if (key >= 48 && key <= 57) {
                     index = key - 48;
                  }

                  if (key >= 96 && key <= 105) {
                     index = key - 96;
                  }

                  if (key >= 65 && key <= 90) {
                     index = key - 55;
                  }

                  if (index >= 0) {
                     ReadyMessagesChooser.this.selected(
                        (ReadyMessagesChooser.ReadyMessageItem)ReadyMessagesChooser.this.panelList
                           .getComponent(ReadyMessagesChooser.normalizeShortcutToIndex(index)),
                        false
                     );
                  }
               } catch (Exception var4) {
               }
            }
         }
      );
   }

   @Override
   public void positionChanged() {
      try {
         List list = Arrays.stream(this.panelList.getComponents())
            .filter(ReadyMessagesChooser.ReadyMessageItem.class::isInstance)
            .map(i -> ReadyMessagesChooser.ReadyMessageItem.class.cast(i).text)
            .collect(Collectors.toList());
         RWObj.write(list, this.file);
      } catch (Exception var2) {
      }
   }

   @Override
   public void rollover(Component component, boolean rollover) {
      if (component instanceof ReadyMessagesChooser.ReadyMessageItem) {
         ReadyMessagesChooser.ReadyMessageItem item = (ReadyMessagesChooser.ReadyMessageItem)component;
         item.setRollover(rollover);
      }
   }

   @Override
   public void click(MouseEvent e, Component component, boolean leftButton) {
      if (component instanceof ReadyMessagesChooser.ReadyMessageItem) {
         ReadyMessagesChooser.ReadyMessageItem item = (ReadyMessagesChooser.ReadyMessageItem)component;
         if (leftButton) {
            this.selected(item, false);
         } else {
            WebPopupMenu popup = new WebPopupMenu();
            if (this.enableSend) {
               WebMenuItem send = new WebMenuItem(I18N.WORD_SEND.val(), new ImageIcon(Images.ICON_SEND_16));
               send.addActionListener(ae -> this.selected(item, true));
               popup.add(send);
            }

            WebMenuItem select = new WebMenuItem(I18N.WORD_SELECT.val(), new ImageIcon(Images.ICON_CHECK_BLACK_16));
            select.addActionListener(ae -> this.selected(item, false));
            popup.add(select);
            WebMenuItem edit = new WebMenuItem(I18N.WORD_EDIT.val(), new ImageIcon(Images.ICON_EDIT_16));
            edit.addActionListener(ae -> this.edit(item));
            popup.add(edit);
            WebMenuItem remove = new WebMenuItem(I18N.WORD_REMOVE.val(), new ImageIcon(Images.ICON_REMOVE_16));
            remove.addActionListener(ae -> this.delete(item));
            popup.add(remove);
            popup.show(e.getComponent(), e.getX() - 16, e.getY() + 8);
         }
      }
   }

   private void selected(ReadyMessagesChooser.ReadyMessageItem item, boolean send) {
      Optional.ofNullable(item).map(i -> i.text).ifPresent(txt -> this.callback.accept(txt, send));
      this.dispose();
   }

   @Override
   public void dispose() {
      this.setVisible(false);
      super.dispose();
      this.removeAll();
   }

   private void add() {
      JTextArea textArea = new JTextArea("");
      JScrollPane scrollPane = new JScrollPane(textArea) {
         @Override
         protected void paintComponent(Graphics g) {
            g.setColor(Color.WHITE);
            g.fillRoundRect(0, 0, this.getWidth(), this.getHeight(), 6, 6);
            super.paintComponent(g);
         }
      };
      scrollPane.setOpaque(true);
      scrollPane.setFocusable(false);
      textArea.setFont(new Font("Georgia", 0, 16));
      textArea.setLineWrap(true);
      textArea.setWrapStyleWord(true);
      textArea.setCaretPosition(textArea.getDocument().getLength());
      scrollPane.setPreferredSize(new Dimension(600, 300));
      boolean startPosition = Boolean.parseBoolean(Settings.SET.get("ready-msg-add-list-position-start", "false"));
      WebRadioButton radioStart = new WebRadioButton(I18N.WORD_START.val(), startPosition);
      WebRadioButton radioEnd = new WebRadioButton(I18N.WORD_END.val(), !startPosition);
      ButtonGroup group = new ButtonGroup();
      Arrays.asList(radioStart, radioEnd).forEach(b -> {
         b.setFocusable(false);
         b.addItemListener(evt -> {
            if (evt.getStateChange() == 1) {
               Settings.SET.put("ready-msg-add-list-position-start", Boolean.toString(radioStart.isSelected()));
            }
         });
         group.add(b);
      });
      WebPanel panelFooter = new WebPanel(new FlowLayout(1, 8, 2));
      panelFooter.add(new Component[]{new WebLabel(I18N.MSG_LIST_POSITION.val() + ": "), radioStart, radioEnd});
      SwingUtils.requestFocusOnVisible(textArea);
      int op = JOptionPane.showConfirmDialog(this, new Object[]{scrollPane, panelFooter}, I18N.WORD_MESSAGE.val(), 2, -1);
      if (op == 0) {
         String input = textArea.getText().trim();
         if (!input.isEmpty()) {
            if (radioStart.isSelected()) {
               this.panelList.add(0, new Component[]{new ReadyMessagesChooser.ReadyMessageItem(input)});
               this.panelList.revalidate();
            } else {
               this.panelList.add(new ReadyMessagesChooser.ReadyMessageItem(input));
               this.panelList.revalidate();
            }

            Utils.startThreadDaemon(() -> {
               Utils.sleep(300L);
               SwingUtilities.invokeLater(() -> {
                  if (radioStart.isSelected()) {
                     SwingUtils.scrollToStart(this.scrollList);
                  } else {
                     SwingUtils.scrollToEnd(this.scrollList);
                  }
               });
            });
            this.positionChanged();
         }
      }
   }

   private void edit(ReadyMessagesChooser.ReadyMessageItem item) {
      String input = Utils.getTextArea(this, I18N.WORD_MESSAGE.val(), item.text);
      if (input != null && !input.trim().isEmpty()) {
         item.setText(input);
         this.positionChanged();
      }
   }

   private void delete(ReadyMessagesChooser.ReadyMessageItem item) {
      this.panelList.remove(item);
      this.panelList.revalidate();
      this.positionChanged();
   }

   protected static int normalizeShortcutToIndex(int index) {
      return index == 0 ? 9 : (index <= 9 ? index - 1 : index);
   }

   protected static int normalizeIndexToShortcut(int index) {
      return index == 9 ? 0 : (index <= 9 ? index + 1 : index);
   }

   private static class ReadyMessageItem extends WebPanel {
      private static final Color COLOR_BG_ROLLOVER_ON = Utils.decodeColor("#C8EDFC", new Color(186, 186, 186));
      private static final Color COLOR_BG_ROLLOVER_OFF = new Color(220, 220, 220);
      private static final int H_BORDER = 6;
      private static final int V_BORDER = 4;
      private static final Border DEFAULT_BORDER = BorderFactory.createCompoundBorder(
         BorderFactory.createEmptyBorder(4, 6, 4, 6), BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2)
      );
      private String text;
      private WebLabel labelText;

      public ReadyMessageItem(String text) {
         super(new BorderLayout());
         this.text = text;
         this.init();
      }

      private void init() {
         this.setBackground(COLOR_BG_ROLLOVER_OFF);
         this.setOpaque(false);
         this.setBorder(DEFAULT_BORDER);
         WebLabel shortcut = new WebLabel(" ", 0) {
            private int index;

            protected void paintComponent(Graphics g) {
               this.updateIndex();
               if (!this.getText().isEmpty()) {
                  super.paintComponent(g);
               }
            }

            public String getText() {
               try {
                  return this.index >= 0 && this.index < 36 ? BigInteger.valueOf((long)this.index).toString(36) : "";
               } catch (Exception var2) {
                  return "";
               }
            }

            private void updateIndex() {
               try {
                  this.index = ReadyMessageItem.this.getParent().getComponentZOrder(ReadyMessageItem.this);
                  this.index = ReadyMessagesChooser.normalizeIndexToShortcut(this.index);
               } catch (Exception var2) {
               }
            }
         };
         shortcut.setFont(new Font("Courier New", 1, 13));
         shortcut.setMargin(3, 6, 3, 6);
         shortcut.setBackground(new Color(60, 60, 60));
         shortcut.setForeground(Color.WHITE);
         shortcut.setOpaque(true);
         this.labelText = new WebLabel("<html>" + this.text.replace("\n", "<br>"));
         this.labelText.setMinimumWidth(680);
         this.labelText.setMaximumWidth(680);
         this.labelText.setFont(new Font("Arial", 0, 12));
         this.labelText.setMargin(4, 8, 4, 6);
         this.add(shortcut, "West");
         this.add(this.labelText, "Center");
      }

      public String getText() {
         return this.text;
      }

      public void setText(String text) {
         this.text = StringUtils.maxLength(text, 1024);
         this.labelText.setText("<html>" + this.text.replace("\n", "<br>"));
         this.labelText.repaint();
      }

      public void setRollover(boolean rollover) {
         this.setBackground(rollover ? COLOR_BG_ROLLOVER_ON : COLOR_BG_ROLLOVER_OFF);
         this.repaint();
      }

      protected void paintComponent(Graphics g) {
         Dimension d = this.getSize();
         if (d.width > 0 && d.height > 0) {
            Graphics2D g2 = (Graphics2D)g;
            g2.setColor(this.getBackground());
            g2.fillRect(6, 4, d.width - 12, d.height - 8);
         }

         super.paintComponent(g);
      }
   }
}
