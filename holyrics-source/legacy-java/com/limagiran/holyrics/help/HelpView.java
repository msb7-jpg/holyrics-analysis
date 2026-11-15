package com.limagiran.holyrics.help;

import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.tree.DefaultMutableTreeNode;

public class HelpView extends JDialog {
   private static HelpView INSTANCE;
   private final TreeModelHelp treeModel = new TreeModelHelp();
   private JEditorPane jEditorPaneContent;
   private JPanel jPanelHelp;
   private JScrollPane jScrollPaneContent;
   private JScrollPane jScrollPaneTree;
   private JSplitPane jSplitPane;
   private JTree jTreeHelp;

   public HelpView(Frame parent, EnumHelpPages init) {
      super(parent);
      this.initComponents();
      this.init(init.url);
   }

   private void init(URL url) {
      this.changeUrl(url);
      this.jTreeHelp.setSelectionRow(this.treeModel.rowOf(url));
      InsertShortcut.addEscToDispose(this);
      SwingUtils.maximize(this, 100);
   }

   private void initComponents() {
      this.jSplitPane = new JSplitPane();
      this.jScrollPaneTree = new JScrollPane();
      this.jTreeHelp = new JTree();
      this.jPanelHelp = new JPanel();
      this.jScrollPaneContent = new JScrollPane();
      this.jEditorPaneContent = new JEditorPane();
      this.setDefaultCloseOperation(2);
      this.setTitle(MessageFormat.format(ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle").getString("word.help")));
      this.setIconImages(Images.HOLYRICS_ICON);
      this.setMinimumSize(new Dimension(800, 450));
      this.setModal(true);
      this.jSplitPane.setDividerLocation(275);
      this.jScrollPaneTree.setBackground(new Color(248, 248, 248));
      this.jTreeHelp.setBackground(new Color(248, 248, 248));
      this.jTreeHelp.setFont(new Font("Georgia", 0, 18));
      this.jTreeHelp.setModel(this.treeModel.treeModel);
      this.jTreeHelp.setRowHeight(26);
      this.jTreeHelp.addTreeSelectionListener(new TreeSelectionListener() {
         @Override
         public void valueChanged(TreeSelectionEvent evt) {
            HelpView.this.jTreeHelpValueChanged(evt);
         }
      });
      this.jScrollPaneTree.setViewportView(this.jTreeHelp);
      this.jSplitPane.setLeftComponent(this.jScrollPaneTree);
      this.jPanelHelp.setBackground(new Color(248, 248, 248));
      this.jPanelHelp.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createBevelBorder(0), BorderFactory.createBevelBorder(1)));
      this.jPanelHelp.setLayout(new BorderLayout());
      this.jScrollPaneContent.setBackground(new Color(248, 248, 248));
      this.jEditorPaneContent.setEditable(false);
      this.jEditorPaneContent.setBackground(new Color(248, 248, 248));
      this.jEditorPaneContent.setBorder(BorderFactory.createLineBorder(new Color(248, 248, 248), 4));
      this.jEditorPaneContent.setContentType("text/html; charset=UTF-8");
      this.jEditorPaneContent.addHyperlinkListener(new HyperlinkListener() {
         @Override
         public void hyperlinkUpdate(HyperlinkEvent evt) {
            HelpView.this.jEditorPaneContentHyperlinkUpdate(evt);
         }
      });
      this.jScrollPaneContent.setViewportView(this.jEditorPaneContent);
      this.jPanelHelp.add(this.jScrollPaneContent, "Center");
      this.jSplitPane.setRightComponent(this.jPanelHelp);
      this.getContentPane().add(this.jSplitPane, "Center");
      this.pack();
      this.setLocationRelativeTo(null);
   }

   private void jTreeHelpValueChanged(TreeSelectionEvent evt) {
      this.nodeChanged();
   }

   private void jEditorPaneContentHyperlinkUpdate(HyperlinkEvent evt) {
      if (evt.getEventType() == EventType.ACTIVATED) {
         this.changeUrl(evt.getURL());
         this.jTreeHelp.setSelectionRow(this.treeModel.rowOf(evt.getURL()));
      }
   }

   public static void main(Frame parent, EnumHelpPages init) {
      SwingUtilities.invokeLater(() -> {
         if (INSTANCE != null) {
            INSTANCE.setVisible(false);
            INSTANCE.dispose();
         }

         INSTANCE = new HelpView(parent, init);
         INSTANCE.setVisible(true);
      });
   }

   private void changeUrl(URL url) {
      try {
         this.jEditorPaneContent.setPage(url);
      } catch (IOException var3) {
      }
   }

   private void nodeChanged() {
      try {
         DefaultMutableTreeNode node = (DefaultMutableTreeNode)this.jTreeHelp.getLastSelectedPathComponent();
         if (node == null) {
            return;
         }

         Object nodeInfo = node.getUserObject();
         if (node.isLeaf()) {
            this.changeUrl(((EnumHelpPages)nodeInfo).url);
         }
      } catch (Exception var3) {
      }
   }
}
