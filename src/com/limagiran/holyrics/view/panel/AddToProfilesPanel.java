package com.limagiran.holyrics.view.panel;

import com.alee.extended.list.CheckBoxListModel;
import com.alee.extended.list.WebCheckBoxList;
import com.alee.laf.scroll.WebScrollPane;
import com.limagiran.holyrics.control.ProfileController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Music;
import com.limagiran.holyrics.model.Profile;
import java.awt.BorderLayout;
import java.awt.Font;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

public class AddToProfilesPanel extends WebScrollPane {
   private final CheckBoxListModel modelCheckBoxProfiles = new CheckBoxListModel();
   private final WebCheckBoxList checkBoxProfiles = new WebCheckBoxList(this.modelCheckBoxProfiles);

   public AddToProfilesPanel() {
      super(null);
      this.init();
   }

   private void init() {
      Font f = new Font("Courier New", 1, 16);
      JLabel title = new JLabel(I18N.MSG_ADD_TO_PROFILES.val(), 0);
      title.setBorder(new EmptyBorder(2, 0, 2, 0));
      title.setFont(f);
      List<Profile> list = ProfileController.getProfilesInOrderOfAmount(false);
      list.forEach(an -> this.modelCheckBoxProfiles.addCheckBoxElement(an, false));
      this.checkBoxProfiles.setFont(f.deriveFont(15.0F));
      this.checkBoxProfiles.setVisibleRowCount(3);
      this.checkBoxProfiles.setEditable(false);
      this.checkBoxProfiles.setFocusable(false);
      this.checkBoxProfiles.setLayoutOrientation(2);
      this.checkBoxProfiles.setFixedCellWidth(180);
      this.setViewportView(this.checkBoxProfiles);
      JPanel panelTitle = new JPanel(new BorderLayout());
      panelTitle.add(title, "Center");
      JSeparator separator = new JSeparator();
      separator.setOrientation(0);
      panelTitle.add(separator, "Last");
      panelTitle.setOpaque(false);
      this.setColumnHeaderView(panelTitle);
      this.setHorizontalScrollBarPolicy(32);
      this.setVisible(!list.isEmpty());
      this.setPreferredWidth(0);
   }

   public void addMusicsInCheckedProfiles(List<Music> musics) {
      this.checkBoxProfiles.getCheckedValues().stream().forEach(p -> ((Profile)p).addMusics(musics));
   }
}
