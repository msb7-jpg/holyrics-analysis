package com.limagiran.holyrics.edit;

import com.alee.laf.tabbedpane.WebTabbedPane;
import com.limagiran.holyrics.util.BooleanOptional;
import com.limagiran.holyrics.util.SwingUtils;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Window;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class EditPanel extends JPanel {
   public static final int PANEL_DEFAULT = 0;
   public static final int PANEL_PROFILE = 0;
   public static final int PANEL_WORSHIP = 1;
   public static final int PANEL_EVENT = 2;
   public static final int PANEL_MEMBER = 3;
   private static EditPanel INSTANCE;
   private static final EditProfile PROFILE_VIEW = new EditProfile();
   private static final EditWorship WORSHIP_VIEW = new EditWorship();
   private static final EditEvent EVENT_VIEW = new EditEvent();
   private static final EditMember MEMBER_VIEW = new EditMember();
   private static final List<IEditView> SET_LIST = Arrays.asList(PROFILE_VIEW, WORSHIP_VIEW, EVENT_VIEW, MEMBER_VIEW);
   private JTabbedPane jTabbedPane;

   protected static JPanel getContent(Window owner, int panel) {
      if (INSTANCE == null) {
         SwingUtils.runOnUIThread(() -> INSTANCE = new EditPanel());
      }

      INSTANCE.jTabbedPane.setSelectedComponent(getPanel(panel));
      return INSTANCE;
   }

   protected static JPanel getContent(Window owner) {
      return getContent(owner, 0);
   }

   private static JPanel getPanel(int panel) {
      switch (panel) {
         case 0:
            return PROFILE_VIEW;
         case 1:
            return WORSHIP_VIEW;
         case 2:
            return EVENT_VIEW;
         case 3:
            return MEMBER_VIEW;
         default:
            return PROFILE_VIEW;
      }
   }

   private EditPanel() {
      this.initComponents();
      this.init();
   }

   private void init() {
      SET_LIST.stream().forEach(p -> this.jTabbedPane.addTab(p.getTitle(), p.getIcon(), p.getPanel()));
      this.jTabbedPane
         .addChangeListener(
            e -> Optional.ofNullable(this.jTabbedPane.getSelectedComponent())
                  .ifPresent(c -> BooleanOptional.runIfTrue(c.equals(EVENT_VIEW), EVENT_VIEW::refresh))
         );
      ((WebTabbedPane)this.jTabbedPane).setTabInsets(new Insets(5, 16, 7, 16));
   }

   private void initComponents() {
      this.jTabbedPane = new WebTabbedPane();
      this.setLayout(new BorderLayout());
      this.jTabbedPane.setFocusable(false);
      this.jTabbedPane.setFont(new Font("Arial", 0, 14));
      this.add(this.jTabbedPane, "Center");
   }

   protected static boolean exit() {
      return SET_LIST.stream().map(IEditView.class::cast).allMatch(IEditView::exit);
   }
}
