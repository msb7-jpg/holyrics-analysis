package com.limagiran.holyrics.swing;

import com.limagiran.holyrics.model.Music;
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

public class JCheckBoxTreeBatchImport extends JTree {
   private final JCheckBoxTreeBatchImport selfPointer = this;
   private final boolean startChecked;
   private HashMap<TreePath, JCheckBoxTreeBatchImport.CheckedNode> nodesCheckingState;
   private HashSet<TreePath> checkedPaths = new HashSet<>();
   private EventListenerList listenerList = new EventListenerList();

   public void addCheckChangeEventListener(JCheckBoxTreeBatchImport.CheckChangeEventListener listener) {
      this.listenerList.add(JCheckBoxTreeBatchImport.CheckChangeEventListener.class, listener);
   }

   public void removeCheckChangeEventListener(JCheckBoxTreeBatchImport.CheckChangeEventListener listener) {
      this.listenerList.remove(JCheckBoxTreeBatchImport.CheckChangeEventListener.class, listener);
   }

   private void fireCheckChangeEvent(JCheckBoxTreeBatchImport.CheckChangeEvent evt) {
      Object[] listeners = this.listenerList.getListenerList();

      for (int i = 0; i < listeners.length; i++) {
         if (listeners[i] == JCheckBoxTreeBatchImport.CheckChangeEventListener.class) {
            ((JCheckBoxTreeBatchImport.CheckChangeEventListener)listeners[i + 1]).checkStateChanged(evt);
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
      JCheckBoxTreeBatchImport.CheckedNode cn = this.nodesCheckingState
         .getOrDefault(path, new JCheckBoxTreeBatchImport.CheckedNode(this.startChecked, false, this.startChecked, ""));
      return cn.isSelected && cn.hasChildren && !cn.allChildrenSelected;
   }

   public List<Object> getSelectedObjects() {
      return Arrays.asList(this.getCheckedPaths())
         .stream()
         .map(p -> this.nodesCheckingState.getOrDefault(p, new JCheckBoxTreeBatchImport.CheckedNode(this.startChecked, false, this.startChecked, "")).source)
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
      JCheckBoxTreeBatchImport.CheckedNode cn = new JCheckBoxTreeBatchImport.CheckedNode(
         this.startChecked, node.getChildCount() > 0, this.startChecked, node.getUserObject()
      );
      if (this.startChecked) {
         this.checkedPaths.add(tp);
      }

      this.nodesCheckingState.put(tp, cn);

      for (int i = 0; i < node.getChildCount(); i++) {
         this.addSubtreeToCheckingStateTracking((DefaultMutableTreeNode)tp.pathByAddingChild(node.getChildAt(i)).getLastPathComponent());
      }
   }

   public JCheckBoxTreeBatchImport(TreeModel model, boolean startChecked) {
      super(model);
      this.startChecked = startChecked;
      this.setModel(model);
      this.init();
   }

   private void init() {
      this.setToggleClickCount(0);
      JCheckBoxTreeBatchImport.CheckBoxCellRenderer cr = new JCheckBoxTreeBatchImport.CheckBoxCellRenderer();
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
            TreePath tp = JCheckBoxTreeBatchImport.this.selfPointer.getPathForLocation(ev.getX(), ev.getY());
            if (tp != null) {
               boolean checkMode = !JCheckBoxTreeBatchImport.this.nodesCheckingState.get(tp).isSelected;
               JCheckBoxTreeBatchImport.this.checkSubTree(tp, checkMode);
               JCheckBoxTreeBatchImport.this.updatePredecessorsWithCheckMode(tp, checkMode);
               JCheckBoxTreeBatchImport.this.fireCheckChangeEvent(JCheckBoxTreeBatchImport.this.new CheckChangeEvent(new Object()));
               JCheckBoxTreeBatchImport.this.selfPointer.repaint();
            }
         }
      });
      this.setSelectionModel(dtsm);
   }

   protected void updatePredecessorsWithCheckMode(TreePath tp, boolean check) {
      TreePath parentPath = tp.getParentPath();
      if (parentPath != null) {
         JCheckBoxTreeBatchImport.CheckedNode parentCheckedNode = this.nodesCheckingState.get(parentPath);
         DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parentPath.getLastPathComponent();
         parentCheckedNode.allChildrenSelected = true;
         parentCheckedNode.isSelected = false;

         for (int i = 0; i < parentNode.getChildCount(); i++) {
            TreePath childPath = parentPath.pathByAddingChild(parentNode.getChildAt(i));
            JCheckBoxTreeBatchImport.CheckedNode childCheckedNode = this.nodesCheckingState.get(childPath);
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
      JCheckBoxTreeBatchImport.CheckedNode cn = this.nodesCheckingState.get(tp);
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
         JCheckBoxTreeBatchImport.CheckedNode cn = JCheckBoxTreeBatchImport.this.nodesCheckingState.get(tp);
         if (cn == null) {
            return this;
         } else {
            this.checkBox.setSelected(cn.isSelected);
            this.checkBox.setOpaque(cn.isSelected && cn.hasChildren && !cn.allChildrenSelected);
            this.checkBox.setText(obj instanceof Music ? ((Music)obj).getTitle() : obj.toString());
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
      void checkStateChanged(JCheckBoxTreeBatchImport.CheckChangeEvent var1);
   }

   public class CheckedNode {
      private boolean isSelected;
      private final boolean hasChildren;
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
