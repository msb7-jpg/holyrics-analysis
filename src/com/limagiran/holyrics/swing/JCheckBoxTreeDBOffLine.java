package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.view.DBOffLineBatchImportView;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.EventListenerList;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class JCheckBoxTreeDBOffLine extends JTree {
   private final JCheckBoxTreeDBOffLine selfPointer = this;
   private final boolean startChecked;
   private HashMap<TreePath, JCheckBoxTreeDBOffLine.CheckedNode> nodesCheckingState;
   private HashSet<TreePath> checkedPaths = new HashSet<>();
   private EventListenerList listenerList = new EventListenerList();

   public void addCheckChangeEventListener(JCheckBoxTreeDBOffLine.CheckChangeEventListener listener) {
      this.listenerList.add(JCheckBoxTreeDBOffLine.CheckChangeEventListener.class, listener);
   }

   public void removeCheckChangeEventListener(JCheckBoxTreeDBOffLine.CheckChangeEventListener listener) {
      this.listenerList.remove(JCheckBoxTreeDBOffLine.CheckChangeEventListener.class, listener);
   }

   private void fireCheckChangeEvent(JCheckBoxTreeDBOffLine.CheckChangeEvent evt) {
      Object[] listeners = this.listenerList.getListenerList();

      for (int i = 0; i < listeners.length; i++) {
         if (listeners[i] == JCheckBoxTreeDBOffLine.CheckChangeEventListener.class) {
            ((JCheckBoxTreeDBOffLine.CheckChangeEventListener)listeners[i + 1]).checkStateChanged(evt);
         }
      }
   }

   @Override
   public final void setModel(TreeModel newModel) {
      super.setModel(newModel);
      this.resetCheckingState();
   }

   public TreePath[] getCheckedPaths() {
      return this.checkedPaths.toArray(new TreePath[this.checkedPaths.size()]);
   }

   public boolean isSelectedPartially(TreePath path) {
      JCheckBoxTreeDBOffLine.CheckedNode cn = this.nodesCheckingState
         .getOrDefault(path, new JCheckBoxTreeDBOffLine.CheckedNode(this.startChecked, false, this.startChecked, ""));
      return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
   }

   public List<Object> getSelectedObjects() {
      return Arrays.asList(this.getCheckedPaths())
         .stream()
         .map(p -> this.nodesCheckingState.getOrDefault(p, new JCheckBoxTreeDBOffLine.CheckedNode(this.startChecked, false, this.startChecked, "")).source)
         .collect(Collectors.toList());
   }

   private void resetCheckingState() {
      this.nodesCheckingState = new HashMap<>();
      this.checkedPaths = new HashSet<>();
      JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom node = (JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom)this.getModel().getRoot();
      if (node != null) {
         this.addSubtreeToCheckingStateTracking(node);
      }
   }

   private void addSubtreeToCheckingStateTracking(JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom node) {
      TreeNode[] path = node.getPath();
      TreePath tp = new TreePath(path);
      JCheckBoxTreeDBOffLine.CheckedNode cn = new JCheckBoxTreeDBOffLine.CheckedNode(
         this.startChecked, node.getChildCount() > 0, this.startChecked, node.getUserObject()
      );
      if (this.startChecked && node.isEnabled()) {
         this.checkedPaths.add(tp);
      }

      this.nodesCheckingState.put(tp, cn);

      for (int i = 0; i < node.getChildCount(); i++) {
         this.addSubtreeToCheckingStateTracking(
            (JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom)tp.pathByAddingChild(node.getChildAt(i)).getLastPathComponent()
         );
      }
   }

   public JCheckBoxTreeDBOffLine(TreeModel model, boolean startChecked) {
      super(model);
      this.startChecked = startChecked;
      this.setModel(model);
      this.init();
   }

   private void init() {
      this.setToggleClickCount(0);
      JCheckBoxTreeDBOffLine.CheckBoxCellRenderer cr = new JCheckBoxTreeDBOffLine.CheckBoxCellRenderer();
      this.setCellRenderer(cr);
      DefaultTreeSelectionModel dtsm = new DefaultTreeSelectionModel() {
         @Override
         public void setSelectionPath(TreePath path) {
         }

         @Override
         public void addSelectionPath(TreePath path) {
         }

         @Override
         public void removeSelectionPath(TreePath path) {
         }

         @Override
         public void setSelectionPaths(TreePath[] pPaths) {
         }
      };
      this.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent ev) {
            TreePath tp = JCheckBoxTreeDBOffLine.this.selfPointer.getPathForLocation(ev.getX(), ev.getY());
            if (tp != null) {
               try {
                  if (!((JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom)tp.getLastPathComponent()).isEnabled()) {
                     return;
                  }
               } catch (Exception var4) {
               }

               boolean checkMode = !JCheckBoxTreeDBOffLine.this.nodesCheckingState.get(tp).isSelected;
               JCheckBoxTreeDBOffLine.this.checkSubTree(tp, checkMode);
               JCheckBoxTreeDBOffLine.this.updatePredecessorsWithCheckMode(tp, checkMode);
               JCheckBoxTreeDBOffLine.this.fireCheckChangeEvent(JCheckBoxTreeDBOffLine.this.new CheckChangeEvent(new Object()));
               JCheckBoxTreeDBOffLine.this.selfPointer.repaint();
            }
         }
      });
      this.setSelectionModel(dtsm);
   }

   protected void updatePredecessorsWithCheckMode(TreePath tp, boolean check) {
      TreePath parentPath = tp.getParentPath();
      if (parentPath != null) {
         JCheckBoxTreeDBOffLine.CheckedNode parentCheckedNode = this.nodesCheckingState.get(parentPath);
         JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom parentNode = (JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom)parentPath.getLastPathComponent();
         parentCheckedNode.allChildrenSelected = true;
         parentCheckedNode.isSelected = false;

         for (int i = 0; i < parentNode.getChildCount(); i++) {
            TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
            JCheckBoxTreeDBOffLine.CheckedNode childCheckedNode = this.nodesCheckingState.get(childPath);
            if (!childCheckedNode.allChildrenSelected) {
               parentCheckedNode.allChildrenSelected = false;
            }

            if (childCheckedNode.isSelected) {
               parentCheckedNode.isSelected = true;
            }
         }

         if (parentNode.isEnabled()) {
            if (parentCheckedNode.isSelected) {
               this.checkedPaths.add(parentPath);
            } else {
               this.checkedPaths.remove(parentPath);
            }
         }

         this.updatePredecessorsWithCheckMode(parentPath, check);
      }
   }

   protected void checkSubTree(TreePath tp, boolean check) {
      JCheckBoxTreeDBOffLine.CheckedNode cn = this.nodesCheckingState.get(tp);
      cn.isSelected = check;
      JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom node = (JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom)tp.getLastPathComponent();

      for (int i = 0; i < node.getChildCount(); i++) {
         this.checkSubTree(tp.pathByAddingChild(node.getChildAt(i)), check);
      }

      cn.allChildrenSelected = check;
      if (node.isEnabled()) {
         if (check) {
            this.checkedPaths.add(tp);
         } else {
            this.checkedPaths.remove(tp);
         }
      }
   }

   private class CheckBoxCellRenderer extends JPanel implements TreeCellRenderer {
      private JCheckBox checkBox;

      public CheckBoxCellRenderer() {
         this.init();
      }

      private void init() {
         this.setLayout(new BorderLayout());
         this.checkBox = new JCheckBox();
         this.add(this.checkBox, "Center");
         this.checkBox.setFont(new Font("Georgia", 0, 16));
         this.setOpaque(false);
      }

      @Override
      public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
         JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom node = (JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom)value;
         Object obj = node.getUserObject();
         TreePath tp = new TreePath(node.getPath());
         JCheckBoxTreeDBOffLine.CheckedNode cn = JCheckBoxTreeDBOffLine.this.nodesCheckingState.get(tp);
         if (cn == null) {
            return this;
         } else {
            this.checkBox.setSelected(node.isEnabled() && cn.isSelected);
            this.checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
            this.checkBox.setEnabled(node.isEnabled());
            this.checkBox.setText(obj.toString());
            return this;
         }
      }
   }

   public class CheckChangeEvent extends EventObject {
      public CheckChangeEvent(Object source) {
         super(source);
      }
   }

   public interface CheckChangeEventListener extends EventListener {
      void checkStateChanged(JCheckBoxTreeDBOffLine.CheckChangeEvent var1);
   }

   public class CheckedNode {
      private boolean isSelected;
      private boolean hasChildren;
      private boolean allChildrenSelected;
      public final Object source;

      private CheckedNode(boolean isSelected, boolean hasChildren, boolean allChildrenSelected, Object source) {
         this.source = source;
         this.isSelected = isSelected;
         this.hasChildren = hasChildren;
         this.allChildrenSelected = allChildrenSelected;
      }
   }

   public static class DefaultMutableTreeNodeCustom extends DefaultMutableTreeNode {
      public DefaultMutableTreeNodeCustom() {
      }

      public DefaultMutableTreeNodeCustom(Object userObject) {
         super(userObject);
      }

      public DefaultMutableTreeNodeCustom(Object userObject, boolean allowsChildren) {
         super(userObject, allowsChildren);
      }

      @Override
      public void add(MutableTreeNode newChild) {
         if (newChild instanceof JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom) {
            super.add(newChild);
         }
      }

      public boolean isEnabled() {
         Object obj = this.getUserObject();
         if (obj instanceof DBOffLineBatchImportView.MusicJTree) {
            return !((DBOffLineBatchImportView.MusicJTree)obj).alreadyAdded;
         } else {
            for (int i = 0; i < this.getChildCount(); i++) {
               if (((JCheckBoxTreeDBOffLine.DefaultMutableTreeNodeCustom)this.getChildAt(i)).isEnabled()) {
                  return true;
               }
            }

            return false;
         }
      }
   }
}
