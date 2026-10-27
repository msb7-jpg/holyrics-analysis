package com.limagiran.holyrics.edit;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

interface IEditView {
   boolean exit();

   String getTitle();

   JPanel getPanel();

   ImageIcon getIcon();
}
