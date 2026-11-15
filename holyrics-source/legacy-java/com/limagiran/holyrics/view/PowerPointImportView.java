package com.limagiran.holyrics.view;

import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.settings.Settings;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class PowerPointImportView extends JDialog {
   private final Runnable callback;
   private JButton jButtonCreate;
   private JPanel jPanelCreate;
   private JPanel jPanelGeneral;
   private JScrollPane jScrollPaneAllSlidess;
   private JScrollPane jScrollPaneCreate;
   private JTextArea jTextAreaAllSlides;
   private JTextArea jTextAreaCreate;

   public PowerPointImportView(Frame parent, String allLyrics, Runnable callback) {
      super(parent);
      this.callback = callback;
      this.initComponents();
      this.jTextAreaAllSlides.setText(allLyrics);
      this.jTextAreaAllSlides.setCaretPosition(0);
      this.init();
   }

   private void init() {
      Arrays.asList(this.jTextAreaAllSlides, this.jTextAreaCreate).stream().forEach(SwingUtils::setupCCPUAndRedo);
      this.jTextAreaCreate.getDocument().addDocumentListener(new DocumentListener() {
         @Override
         public void insertUpdate(DocumentEvent e) {
            PowerPointImportView.this.jButtonCreate.setEnabled(!PowerPointImportView.this.jTextAreaCreate.getText().replace(" ", "").isEmpty());
         }

         @Override
         public void removeUpdate(DocumentEvent e) {
            PowerPointImportView.this.jButtonCreate.setEnabled(!PowerPointImportView.this.jTextAreaCreate.getText().replace(" ", "").isEmpty());
         }

         @Override
         public void changedUpdate(DocumentEvent e) {
            PowerPointImportView.this.jButtonCreate.setEnabled(!PowerPointImportView.this.jTextAreaCreate.getText().replace(" ", "").isEmpty());
         }
      });
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jScrollPaneAllSlidess = new JScrollPane();
      this.jTextAreaAllSlides = new JTextArea();
      this.jPanelCreate = new JPanel();
      this.jScrollPaneCreate = new JScrollPane();
      this.jTextAreaCreate = SwingUtils.createJTextAreaPlaceHolder(I18N.MSG_PH_PASTE_HERE_LYRICS.val());
      this.jButtonCreate = new JButton();
      this.setDefaultCloseOperation(0);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.import"));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(750, 665));
      this.setModal(true);
      this.setResizable(false);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent evt) {
            PowerPointImportView.this.formWindowClosing(evt);
         }
      });
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
      this.jPanelGeneral.setLayout(new BorderLayout());
      this.jTextAreaAllSlides.setColumns(20);
      this.jTextAreaAllSlides.setFont(new Font("Tahoma", 0, 12));
      this.jTextAreaAllSlides.setRows(5);
      this.jScrollPaneAllSlidess.setViewportView(this.jTextAreaAllSlides);
      this.jPanelGeneral.add(this.jScrollPaneAllSlidess, "Center");
      this.jPanelCreate.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
      this.jPanelCreate.setLayout(new BorderLayout());
      this.jTextAreaCreate.setColumns(25);
      this.jTextAreaCreate.setFont(new Font("Tahoma", 0, 12));
      this.jTextAreaCreate.setRows(5);
      this.jScrollPaneCreate.setViewportView(this.jTextAreaCreate);
      this.jPanelCreate.add(this.jScrollPaneCreate, "East");
      this.jButtonCreate.setFont(new Font("Georgia", 0, 14));
      this.jButtonCreate.setText(bundle.getString("msg.createMusic"));
      this.jButtonCreate.setEnabled(false);
      this.jButtonCreate.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent evt) {
            PowerPointImportView.this.jButtonCreateActionPerformed(evt);
         }
      });
      this.jPanelCreate.add(this.jButtonCreate, "Last");
      this.jPanelGeneral.add(this.jPanelCreate, "East");
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void formWindowClosing(WindowEvent evt) {
      this.close();
   }

   private void jButtonCreateActionPerformed(ActionEvent evt) {
      if (!this.jTextAreaCreate.getText().replace(" ", "").isEmpty()) {
         MusicView.main(Music.create().setLyrics(this.jTextAreaCreate.getText()), true, Settings.SET.getTheme(), this.callback);
      }
   }

   public static void main(Frame parent, String allLyrics, Runnable callback) {
      SwingUtilities.invokeLater(() -> new PowerPointImportView(parent, allLyrics, callback).setVisible(true));
   }

   private void close() {
      super.dispose();
   }
}
