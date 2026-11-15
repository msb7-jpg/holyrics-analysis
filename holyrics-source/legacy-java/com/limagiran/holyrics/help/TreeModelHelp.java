package com.limagiran.holyrics.help;

import com.limagiran.holyrics.i18n.I18N;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class TreeModelHelp {
   public final TreeModel treeModel;
   private final List<EnumHelpPages> list = Arrays.asList(EnumHelpPages.values());

   public TreeModelHelp() {
      DefaultMutableTreeNode root = new DefaultMutableTreeNode(I18N.WORD_HELP.val());
      this.list.stream().map(DefaultMutableTreeNode::new).forEach(root::add);
      this.treeModel = new DefaultTreeModel(root);
   }

   public int rowOf(URL url) {
      try {
         for (int i = 0; i < this.list.size(); i++) {
            if (this.list.get(i).url.toString().equals(url.toString())) {
               return i + 1;
            }
         }
      } catch (Exception var3) {
      }

      return -1;
   }
}
