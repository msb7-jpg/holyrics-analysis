package com.limagiran.holyrics.edit;

import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class EditDialog extends JDialog {
   private final Runnable callback;
   private JButton jButtonOK;
   private JPanel jPanelButtons;
   private JPanel jPanelGeneral;

   private EditDialog(Window owner, int panel, Runnable callback) {
      super(owner);
      this.callback = callback;
      this.initComponents();
      this.init(panel);
   }

   private void init(int panel) {
      this.jPanelGeneral.add(EditPanel.getContent(this, panel), "Center");
      this.jButtonOK.addActionListener(e -> BooleanOptional.runIfTrue(EditPanel.exit(), this::dispose));
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            BooleanOptional.runIfTrue(EditPanel.exit(), EditDialog.this::dispose);
         }
      });
      SwingUtils.maximize(this, 100);
      InsertShortcut.addEscToDispose(this);
   }

   @Override
   public void dispose() {
      Optional.ofNullable(this.callback).ifPresent(r -> r.run());
      super.dispose();
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanelButtons = new JPanel();
      this.jButtonOK = new JButton();
      this.setDefaultCloseOperation(0);
      this.setTitle("Holyrics");
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setModal(true);
      this.jPanelGeneral.setLayout(new BorderLayout());
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.jButtonOK.setFont(new Font("Georgia", 0, 18));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jButtonOK.setText(bundle.getString("word.ok"));
      this.jButtonOK.setMaximumSize(new Dimension(100, 35));
      this.jButtonOK.setMinimumSize(new Dimension(100, 35));
      this.jButtonOK.setPreferredSize(new Dimension(100, 35));
      this.jPanelButtons.add(this.jButtonOK);
      this.getContentPane().add(this.jPanelButtons, "Last");
      this.pack();
   }

   public static void main(Window owner, int panel, Runnable callback) {
      SwingUtilities.invokeLater(() -> new EditDialog(owner, panel, callback).setVisible(true));
   }

   public static void main(Window owner, Runnable callback) {
      SwingUtilities.invokeLater(() -> new EditDialog(owner, 0, callback).setVisible(true));
   }
}
