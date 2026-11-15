package com.limagiran.holyrics.edit;

import com.alee.laf.toolbar.WebToolBar;
import com.limagiran.holyrics.control.MemberController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.swing.ListCellRendererMember;
import com.limagiran.holyrics.swing.ListModelMember;
import com.limagiran.holyrics.util.ISwingCRUD;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.Utils;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.GroupLayout.Alignment;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EditMember extends JPanel implements IEditView, ISwingCRUD<Member> {
   private final ListModelMember listModelMember;
   private final Calendar currentDate = Calendar.getInstance();
   private Member currentMember;
   private final List<JCheckBox> checks;
   private JButton jButtonAdd;
   private JButton jButtonRemove;
   private JCheckBox jCheckBoxActive;
   private JCheckBox jCheckBoxInstrument;
   private JCheckBox jCheckBoxOrchestra;
   private JCheckBox jCheckBoxOthers;
   private JCheckBox jCheckBoxVoice;
   private JList<Member> jListMembers;
   private JPanel jPanelDescGroups;
   private JPanel jPanelFields;
   private JPanel jPanelList;
   private JScrollPane jScrollPaneDesc;
   private JScrollPane jScrollPaneMembers;
   private JTextArea jTextAreaSkills;
   private JTextField jTextFieldName;
   private JToolBar jToolBar;

   public EditMember() {
      this.listModelMember = new ListModelMember(this.currentDate);
      this.initComponents();
      this.checks = Arrays.asList(this.jCheckBoxVoice, this.jCheckBoxInstrument, this.jCheckBoxOrchestra, this.jCheckBoxOthers, this.jCheckBoxActive);
      this.init();
   }

   private void init() {
      this.jButtonAdd.addActionListener(e -> this.add());
      this.jButtonRemove.addActionListener(e -> this.remove());
      this.jTextFieldName.setDocument(SwingUtils.createDocMaxSize(32));
      this.refreshJList();
      SwingUtils.setupCCPUAndRedo(this.jTextAreaSkills);
      this.jTextAreaSkills.setDocument(SwingUtils.createDocMaxSize(2048));
      ((WebToolBar)this.jToolBar).setUndecorated(true);
      this.jCheckBoxActive.addItemListener(e -> {
         if (this.currentMember != null) {
            this.currentMember.setActive(this.jCheckBoxActive.isSelected());
            this.refresh();
         }
      });
   }

   private void initComponents() {
      this.jPanelFields = new JPanel();
      this.jToolBar = new WebToolBar();
      this.jButtonAdd = new JButton();
      this.jButtonRemove = new JButton();
      this.jTextFieldName = SwingUtils.createJTextFieldPlaceHolder(I18N.WORD_NAME.val());
      this.jScrollPaneDesc = new JScrollPane();
      this.jTextAreaSkills = SwingUtils.createJTextAreaPlaceHolder(I18N.MSG_PH_SKILLS.val());
      this.jPanelDescGroups = new JPanel();
      this.jCheckBoxVoice = new JCheckBox();
      this.jCheckBoxInstrument = new JCheckBox();
      this.jCheckBoxOrchestra = new JCheckBox();
      this.jCheckBoxOthers = new JCheckBox();
      this.jCheckBoxActive = new JCheckBox();
      this.jPanelList = new JPanel();
      this.jScrollPaneMembers = new JScrollPane();
      this.jListMembers = new JList<>();
      this.setBorder(BorderFactory.createEtchedBorder());
      this.setMaximumSize(new Dimension(775, 392));
      this.setMinimumSize(new Dimension(775, 392));
      this.jPanelFields.setLayout(new GridBagLayout());
      this.jToolBar.setFloatable(false);
      this.jToolBar.setRollover(true);
      this.jButtonAdd.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/plus32.png")));
      ResourceBundle bundle = ResourceBundle.getBundle("com/limagiran/holyrics/i18n/Bundle");
      this.jButtonAdd.setToolTipText(bundle.getString("word.add"));
      this.jButtonAdd.setFocusable(false);
      this.jButtonAdd.setHorizontalTextPosition(0);
      this.jButtonAdd.setVerticalTextPosition(3);
      this.jToolBar.add(this.jButtonAdd);
      this.jButtonRemove.setIcon(new ImageIcon(this.getClass().getResource("/com/limagiran/holyrics/img/delete32.png")));
      this.jButtonRemove.setToolTipText(bundle.getString("word.cancel"));
      this.jButtonRemove.setEnabled(false);
      this.jButtonRemove.setFocusable(false);
      this.jButtonRemove.setHorizontalTextPosition(0);
      this.jButtonRemove.setVerticalTextPosition(3);
      this.jToolBar.add(this.jButtonRemove);
      GridBagConstraints gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 0;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 11;
      this.jPanelFields.add(this.jToolBar, gridBagConstraints);
      this.jTextFieldName.setFont(new Font("Monospaced", 0, 14));
      this.jTextFieldName.setEnabled(false);
      this.jTextFieldName.setNextFocusableComponent(this.jTextAreaSkills);
      this.jTextFieldName.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            EditMember.this.jTextFieldNameKeyReleased(evt);
         }
      });
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 1;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.insets = new Insets(2, 0, 2, 0);
      this.jPanelFields.add(this.jTextFieldName, gridBagConstraints);
      this.jTextAreaSkills.setFont(new Font("Monospaced", 0, 14));
      this.jTextAreaSkills.setLineWrap(true);
      this.jTextAreaSkills.setRows(8);
      this.jTextAreaSkills.setWrapStyleWord(true);
      this.jTextAreaSkills.setEnabled(false);
      this.jTextAreaSkills.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            EditMember.this.jTextAreaSkillsKeyReleased(evt);
         }
      });
      this.jScrollPaneDesc.setViewportView(this.jTextAreaSkills);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 3;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weightx = 1.0;
      this.jPanelFields.add(this.jScrollPaneDesc, gridBagConstraints);
      this.jPanelDescGroups.setLayout(new GridLayout(2, 2));
      this.jCheckBoxVoice.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxVoice.setText(bundle.getString("word.voice"));
      this.jCheckBoxVoice.setFocusable(false);
      this.jCheckBoxVoice.setName("VOICE");
      this.jCheckBoxVoice.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            EditMember.this.jCheckBoxGroupsChanged(evt);
         }
      });
      this.jPanelDescGroups.add(this.jCheckBoxVoice);
      this.jCheckBoxInstrument.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxInstrument.setText(bundle.getString("word.instrument"));
      this.jCheckBoxInstrument.setFocusable(false);
      this.jCheckBoxInstrument.setName("INSTRUMENT");
      this.jCheckBoxInstrument.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            EditMember.this.jCheckBoxGroupsChanged(evt);
         }
      });
      this.jPanelDescGroups.add(this.jCheckBoxInstrument);
      this.jCheckBoxOrchestra.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxOrchestra.setText(bundle.getString("word.orchestra"));
      this.jCheckBoxOrchestra.setFocusable(false);
      this.jCheckBoxOrchestra.setName("ORCHESTRA");
      this.jCheckBoxOrchestra.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            EditMember.this.jCheckBoxGroupsChanged(evt);
         }
      });
      this.jPanelDescGroups.add(this.jCheckBoxOrchestra);
      this.jCheckBoxOthers.setFont(new Font("Georgia", 0, 12));
      this.jCheckBoxOthers.setText(bundle.getString("word.others"));
      this.jCheckBoxOthers.setFocusable(false);
      this.jCheckBoxOthers.setName("OTHERS");
      this.jCheckBoxOthers.addChangeListener(new ChangeListener() {
         @Override
         public void stateChanged(ChangeEvent evt) {
            EditMember.this.jCheckBoxGroupsChanged(evt);
         }
      });
      this.jPanelDescGroups.add(this.jCheckBoxOthers);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 4;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weightx = 1.0;
      this.jPanelFields.add(this.jPanelDescGroups, gridBagConstraints);
      this.jCheckBoxActive.setFont(new Font("Georgia", 0, 14));
      this.jCheckBoxActive.setText(bundle.getString("word.active"));
      this.jCheckBoxActive.setFocusable(false);
      this.jCheckBoxActive.setHorizontalAlignment(0);
      gridBagConstraints = new GridBagConstraints();
      gridBagConstraints.gridx = 0;
      gridBagConstraints.gridy = 2;
      gridBagConstraints.fill = 2;
      gridBagConstraints.anchor = 11;
      gridBagConstraints.weightx = 1.0;
      gridBagConstraints.weighty = 1.0;
      gridBagConstraints.insets = new Insets(0, 2, 0, 2);
      this.jPanelFields.add(this.jCheckBoxActive, gridBagConstraints);
      this.jPanelList.setFocusable(false);
      this.jPanelList.setLayout(new BorderLayout());
      this.jScrollPaneMembers.setFocusable(false);
      this.jListMembers.setFont(new Font("Georgia", 1, 16));
      this.jListMembers.setModel(this.listModelMember);
      this.jListMembers.setSelectionMode(0);
      this.jListMembers.setCellRenderer(new ListCellRendererMember());
      this.jListMembers.setFixedCellWidth(300);
      this.jListMembers.setFocusable(false);
      this.jListMembers.setLayoutOrientation(1);
      this.jListMembers.setVisibleRowCount(-1);
      this.jListMembers.addMouseListener(new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent evt) {
            EditMember.this.jListMembersMousePressed(evt);
         }
      });
      this.jListMembers.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent evt) {
            EditMember.this.jListMembersKeyReleased(evt);
         }
      });
      this.jScrollPaneMembers.setViewportView(this.jListMembers);
      this.jPanelList.add(this.jScrollPaneMembers, "Center");
      GroupLayout layout = new GroupLayout(this);
      this.setLayout(layout);
      layout.setHorizontalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addComponent(this.jPanelFields, -2, 250, -2)
                  .addGap(4, 4, 4)
                  .addComponent(this.jPanelList, -1, -1, 32767)
                  .addGap(7, 7, 7)
            )
      );
      layout.setVerticalGroup(
         layout.createParallelGroup(Alignment.LEADING)
            .addGroup(
               layout.createSequentialGroup()
                  .addGap(0, 0, 0)
                  .addGroup(
                     layout.createParallelGroup(Alignment.LEADING)
                        .addComponent(this.jPanelList, -1, -1, 32767)
                        .addGroup(layout.createSequentialGroup().addComponent(this.jPanelFields, -2, -1, -2).addGap(1, 1, 1))
                  )
            )
      );
   }

   private void jTextFieldNameKeyReleased(KeyEvent evt) {
      if (evt.getKeyCode() == 10) {
         this.jTextFieldName.transferFocus();
      }

      if (this.currentMember != null) {
         this.currentMember.setName(this.jTextFieldName.getText());
         this.jListMembers.repaint();
      }
   }

   private void jListMembersKeyReleased(KeyEvent evt) {
      if (this.jButtonRemove.isEnabled() && evt.getKeyCode() == 127) {
         this.remove();
      }
   }

   private void jListMembersMousePressed(MouseEvent evt) {
      if (SwingUtilities.isLeftMouseButton(evt)) {
         MemberController.sort();
         this.refreshJList(this.jListMembers.getSelectedValue());
      }
   }

   private void jTextAreaSkillsKeyReleased(KeyEvent evt) {
      if (this.currentMember != null) {
         this.currentMember.setSkills(this.jTextAreaSkills.getText());
      }
   }

   private void jCheckBoxGroupsChanged(ChangeEvent evt) {
      if (this.currentMember != null) {
         JCheckBox chkbx = (JCheckBox)evt.getSource();
         this.currentMember.setGroup(Member.EnumGroups.valueOf(chkbx.getName()), chkbx.isSelected());
         this.refresh();
      }
   }

   public void add() {
      Member p = new Member(I18N.WORD_MEMBER.val());
      MemberController.add(p);
      this.refreshJList(p);
      this.jTextFieldName.selectAll();
      this.jTextFieldName.requestFocus();
   }

   public void save(Member p) {
      MemberController.sort();
   }

   @Override
   public JButton getJButtonRemove() {
      return this.jButtonRemove;
   }

   @Override
   public List<JComponent> getAllFieldsEdit() {
      return Arrays.asList(
         this.jTextFieldName,
         this.jTextAreaSkills,
         this.jCheckBoxVoice,
         this.jCheckBoxInstrument,
         this.jCheckBoxOrchestra,
         this.jCheckBoxOthers,
         this.jCheckBoxActive
      );
   }

   public void setCurrentEditable(Member member) {
      this.currentMember = member;
   }

   public void fillFields(Member p) {
      this.jTextFieldName.setText(p.getName());
      this.jTextAreaSkills.setText(p.getSkills());
      this.jCheckBoxVoice.setSelected(p.isVoice());
      this.jCheckBoxInstrument.setSelected(p.isInstrument());
      this.jCheckBoxOrchestra.setSelected(p.isOrchestra());
      this.jCheckBoxOthers.setSelected(p.isOthers());
      this.jCheckBoxActive.setSelected(p.isActive());
   }

   @Override
   public void clearFields() {
      this.jTextFieldName.setText("");
      this.jTextAreaSkills.setText("");
      this.checks.forEach(c -> c.setSelected(false));
   }

   @Override
   public String getTitle() {
      return I18N.WORD_MEMBERS.val();
   }

   @Override
   public JPanel getPanel() {
      return this;
   }

   @Override
   public boolean exit() {
      MemberController.save();
      this.jListMembers.setSelectedIndex(-1);
      return true;
   }

   @Override
   public ImageIcon getIcon() {
      return new ImageIcon(Images.load("member16.png"));
   }

   private void remove() {
      if (Utils.confirm(this, I18N.MSG_CONFIRM_REMOVE_MEMBER.format(this.currentMember))) {
         MemberController.remove(this.currentMember);
         this.currentMember = null;
         this.refreshJList();
      }
   }

   public Member getCurrentEditable() {
      return this.currentMember;
   }

   private void refreshJList(Member e) {
      this.listModelMember.update();
      this.jListMembers.setSelectedValue(e, true);
      this.changeEditable(this.jListMembers.getSelectedValue());
   }

   private void refreshJList() {
      this.listModelMember.update();
      this.jListMembers.setSelectedIndex(-1);
      this.changeEditable(this.jListMembers.getSelectedValue());
   }

   private void refresh() {
      MemberController.sort();
      this.listModelMember.update();
      this.jListMembers.setSelectedValue(this.currentMember, true);
   }
}
