package com.limagiran.holyrics.util;

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
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

public class JCheckBoxTree extends JTree {
   private final JCheckBoxTree selfPointer = this;
   private final boolean startChecked;
   private HashMap<TreePath, JCheckBoxTree.CheckedNode> nodesCheckingState;
   private HashSet<TreePath> checkedPaths = new HashSet<>();
   private EventListenerList listenerList = new EventListenerList();

   public void addCheckChangeEventListener(JCheckBoxTree.CheckChangeEventListener listener) {
      this.listenerList.add(JCheckBoxTree.CheckChangeEventListener.class, listener);
   }

   public void removeCheckChangeEventListener(JCheckBoxTree.CheckChangeEventListener listener) {
      this.listenerList.remove(JCheckBoxTree.CheckChangeEventListener.class, listener);
   }

   private void fireCheckChangeEvent(JCheckBoxTree.CheckChangeEvent evt) {
      Object[] listeners = this.listenerList.getListenerList();

      for (int i = 0; i < listeners.length; i++) {
         if (listeners[i] == JCheckBoxTree.CheckChangeEventListener.class) {
            ((JCheckBoxTree.CheckChangeEventListener)listeners[i + 1]).checkStateChanged(evt);
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
      JCheckBoxTree.CheckedNode cn = this.nodesCheckingState.getOrDefault(path, new JCheckBoxTree.CheckedNode(this.startChecked, false, this.startChecked, ""));
      return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
   }

   public List<Object> getSelectedObjects() {
      return Arrays.asList(this.getCheckedPaths())
         .stream()
         .map(p -> this.nodesCheckingState.getOrDefault(p, new JCheckBoxTree.CheckedNode(this.startChecked, false, this.startChecked, "")).source)
         .collect(Collectors.toList());
   }

   private void resetCheckingState() {
      this.nodesCheckingState = new HashMap<>();
      this.checkedPaths = new HashSet<>();
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.getModel().getRoot();
      if (node != null) {
         this.addSubtreeToCheckingStateTracking(node);
      }
   }

   private void addSubtreeToCheckingStateTracking(DefaultMutableTreeNode node) {
      TreeNode[] path = node.getPath();
      TreePath tp = new TreePath(path);
      JCheckBoxTree.CheckedNode cn = new JCheckBoxTree.CheckedNode(this.startChecked, node.getChildCount() > 0, this.startChecked, node.getUserObject());
      if (this.startChecked) {
         this.checkedPaths.add(tp);
      }

      this.nodesCheckingState.put(tp, cn);

      for (int i = 0; i < node.getChildCount(); i++) {
         this.addSubtreeToCheckingStateTracking((DefaultMutableTreeNode)tp.pathByAddingChild(node.getChildAt(i)).getLastPathComponent());
      }
   }

   public JCheckBoxTree(TreeModel model, boolean startChecked) {
      this.startChecked = startChecked;
      this.setModel(model);
      this.init();
   }

   private void init() {
      this.setToggleClickCount(0);
      JCheckBoxTree.CheckBoxCellRenderer cr = new JCheckBoxTree.CheckBoxCellRenderer();
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
            TreePath tp = JCheckBoxTree.this.selfPointer.getPathForLocation(ev.getX(), ev.getY());
            if (tp != null) {
               boolean checkMode = !JCheckBoxTree.this.nodesCheckingState.get(tp).isSelected;
               JCheckBoxTree.this.checkSubTree(tp, checkMode);
               JCheckBoxTree.this.updatePredecessorsWithCheckMode(tp, checkMode);
               JCheckBoxTree.this.fireCheckChangeEvent(JCheckBoxTree.this.new CheckChangeEvent(new Object()));
               JCheckBoxTree.this.selfPointer.repaint();
            }
         }
      });
      this.setSelectionModel(dtsm);
   }

   protected void updatePredecessorsWithCheckMode(TreePath tp, boolean check) {
      TreePath parentPath = tp.getParentPath();
      if (parentPath != null) {
         JCheckBoxTree.CheckedNode parentCheckedNode = this.nodesCheckingState.get(parentPath);
         DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parentPath.getLastPathComponent();
         parentCheckedNode.allChildrenSelected = true;
         parentCheckedNode.isSelected = false;

         for (int i = 0; i < parentNode.getChildCount(); i++) {
            TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
            JCheckBoxTree.CheckedNode childCheckedNode = this.nodesCheckingState.get(childPath);
            if (!childCheckedNode.allChildrenSelected) {
               parentCheckedNode.allChildrenSelected = false;
            }

            if (childCheckedNode.isSelected) {
               parentCheckedNode.isSelected = true;
            }
         }

         if (parentCheckedNode.isSelected) {
            this.checkedPaths.add(parentPath);
         } else {
            this.checkedPaths.remove(parentPath);
         }

         this.updatePredecessorsWithCheckMode(parentPath, check);
      }
   }

   protected void checkSubTree(TreePath tp, boolean check) {
      JCheckBoxTree.CheckedNode cn = this.nodesCheckingState.get(tp);
      cn.isSelected = check;
      DefaultMutableTreeNode node = (DefaultMutableTreeNode)tp.getLastPathComponent();

      for (int i = 0; i < node.getChildCount(); i++) {
         this.checkSubTree(tp.pathByAddingChild(node.getChildAt(i)), check);
      }

      cn.allChildrenSelected = check;
      if (check) {
         this.checkedPaths.add(tp);
      } else {
         this.checkedPaths.remove(tp);
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
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)value;
         Object obj = node.getUserObject();
         TreePath tp = new TreePath(node.getPath());
         JCheckBoxTree.CheckedNode cn = JCheckBoxTree.this.nodesCheckingState.get(tp);
         if (cn == null) {
            return this;
         } else {
            this.checkBox.setSelected(cn.isSelected);
            this.checkBox.setText(obj.toString());
            this.checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
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
      void checkStateChanged(JCheckBoxTree.CheckChangeEvent var1);
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
}
