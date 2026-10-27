package com.limagiran.holyrics.view;

import com.limagiran.holyrics.dao.MusicDao;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.view.panel.AddToProfilesPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MusicAlreadyExistsDialog extends JDialog {
   private final Runnable callback;
   private final List<Music> musics = new ArrayList<>();
   private final AddToProfilesPanel addToProfilesPanel;
   private Music currentMusic = null;
   private JButton jButtonCancel;
   private JButton jButtonOk;
   private JCheckBox jCheckBoxDoIt;
   private JLabel jLabel;
   private JLabel jLabelTitle;
   private JPanel jPanel;
   private JPanel jPanelButtons;
   private JPanel jPanelGeneral;

   private MusicAlreadyExistsDialog(Window parent, List<Music> musics, Runnable callback, AddToProfilesPanel addToProfilesPanel) {
      super(parent);
      this.callback = callback;
      this.musics.addAll(musics);
      this.addToProfilesPanel = addToProfilesPanel;
      this.initComponents();
      this.init();
   }

   private void init() {
      this.jButtonOk.addActionListener(e -> this.ok());
      this.jButtonCancel.addActionListener(e -> this.cancel());
      this.nextMusic();
   }

   private void initComponents() {
      this.jPanelGeneral = new JPanel();
      this.jPanel = new JPanel();
      this.jLabelTitle = new JLabel();
      this.jLabel = new JLabel();
      this.jCheckBoxDoIt = new JCheckBox();
      this.jPanelButtons = new JPanel();
      this.jButtonOk = new JButton();
      this.jButtonCancel = new JButton();
      this.setDefaultCloseOperation(2);
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.setTitle(bundle.getString("word.confirm"));
      this.setIconImage(null);
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(500, 250));
      this.setModal(true);
      this.setPreferredSize(new Dimension(500, 250));
      this.setResizable(false);
      this.jPanelGeneral.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
      this.jPanelGeneral.setLayout(new BorderLayout());
      this.jPanel.setOpaque(false);
      this.jPanel.setLayout(new GridBagLayout());
      this.jLabelTitle.setFont(new Font("Georgia", 0, 16));
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.gridwidth = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      this.jPanel.add(this.jLabelTitle, gridBagConstraints);
      this.jLabel.setFont(new Font("Georgia", 0, 16));
      this.jLabel.setText(" ");
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.anchor = 15;
      gridBagConstraints.insets = new Insets(8, 0, 8, 0);
      this.jPanel.add(this.jLabel, gridBagConstraints);
      this.jCheckBoxDoIt.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxDoIt.setFocusable(false);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 15;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(8, 0, 8, 0);
      this.jPanel.add(this.jCheckBoxDoIt, gridBagConstraints);
      this.jPanelButtons.setLayout(new GridLayout(1, 2, 15, 0));
      this.jButtonOk.setFont(new Font("Georgia", 0, 14));
      this.jButtonOk.setText(bundle.getString("word.duplicate"));
      this.jPanelButtons.add(this.jButtonOk);
      this.jButtonCancel.setFont(new Font("Georgia", 0, 14));
      this.jButtonCancel.setText(bundle.getString("word.cancel"));
      this.jPanelButtons.add(this.jButtonCancel);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.anchor = 16;
      this.jPanel.add(this.jPanelButtons, gridBagConstraints);
      this.jPanelGeneral.add(this.jPanel, "Center");
      this.getContentPane().add(this.jPanelGeneral, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   public static void main(Window parent, List<Music> musics, Runnable callback, AddToProfilesPanel addToProfilesPanel1) {
      SwingUtilities.invokeLater(() -> new MusicAlreadyExistsDialog(parent, musics, callback, addToProfilesPanel1).setVisible(true));
   }

   private void ok() {
      if (this.jCheckBoxDoIt.isSelected()) {
         List<Music> l = new ArrayList<>();
         l.add(this.currentMusic);
         l.addAll(this.musics);
         this.accept(l, () -> this.dispose());
      } else {
         this.accept(Arrays.asList(this.currentMusic), () -> this.nextMusic());
      }
   }

   private void cancel() {
      if (this.jCheckBoxDoIt.isSelected()) {
         this.dispose();
      } else {
         this.nextMusic();
      }
   }

   private void nextMusic() {
      if (this.musics.isEmpty()) {
         this.dispose();
      } else {
         this.currentMusic = this.musics.remove(0);
         this.jLabelTitle.setText(I18N.MSG_MUSIC_ALREADY_EXISTS_FORMAT.format(this.currentMusic.getTitle(), this.currentMusic.getArtist()));
         if (this.musics.isEmpty()) {
            this.jCheckBoxDoIt.setText(" ");
            this.jCheckBoxDoIt.setVisible(false);
         } else {
            this.jCheckBoxDoIt.setText(I18N.MSG_DO_IT_FOR_ALL_INSTANCES.val());
         }
      }
   }

   @Override
   public void dispose() {
      super.dispose();
      Optional.ofNullable(this.callback).ifPresent(r -> r.run());
   }

   private void accept(final List<Music> list, final Runnable callback) {
      PopUpWorker.exe(new MySwingWorker<Void, Void>(this) {
         protected Void doInBackground() throws Exception {
            MusicDao.add(list);
            MusicAlreadyExistsDialog.this.addToProfilesPanel.addMusicsInCheckedProfiles(list);
            return null;
         }

         @Override
         protected void done() {
            super.done();
            callback.run();
         }
      });
   }
}
