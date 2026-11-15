package com.limagiran.holyrics.view.panel;

import com.alee.laf.combobox.WebComboBox;
import com.limagiran.holyrics.control.MemberController;
import com.limagiran.holyrics.control.ScheduleController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.Event;
import com.limagiran.holyrics.model.Member;
import com.limagiran.holyrics.model.Schedule;
import com.limagiran.holyrics.util.StyleUI;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class EventSchedulePanel extends JPanel {
   private Event event = null;
   private final List<JCheckBox> checkboxs = new ArrayList<>();

   public void setEvent(Event event) {
      this.event = event;
      this.update();
   }

   private void update() {
      this.removeAll();
      this.checkboxs.clear();
      Optional.ofNullable(this.event).ifPresent(e -> {
         List<Member> list = MemberController.getAll(true);
         if (!list.isEmpty()) {
            this.setLayout(new GridBagLayout());
            Schedule s = ScheduleController.get(e);
            GridBagConstraints gbc = this.createGBC(0, 1);
            gbc.anchor = 11;
            JPanel panel = new JPanel(new GridBagLayout()) {
               @Override
               public void setOpaque(boolean isOpaque) {
                  super.setOpaque(true);
               }
            };
            panel.setBackground(Color.WHITE);
            panel.setBorder(new LineBorder(Color.WHITE, 5));
            JLabel label = new JLabel(I18N.WORD_RESPONSIBLE.val()) {
               @Override
               public Dimension getPreferredSize() {
                  return new Dimension(0, super.getPreferredSize().height);
               }
            };
            label.setFont(new Font("Georgia", 0, 12));
            label.setHorizontalAlignment(0);
            panel.add(label, this.createGBC(0, 1));
            Optional.ofNullable(s.getResponsibleAsMember()).filter(mx -> !list.contains(mx)).ifPresent(mx -> list.add(mx));
            DefaultComboBoxModel model = new DefaultComboBoxModel<>(list.toArray(new Member[0]));
            model.insertElementAt(null, 0);
            WebComboBox cmbbx = new WebComboBox(model) {
               public Dimension getPreferredSize() {
                  return new Dimension(0, super.getPreferredSize().height);
               }
            };
            cmbbx.setOpaque(false);
            cmbbx.setFocusable(false);
            cmbbx.setSelectedItem(s.getResponsibleAsMember());
            cmbbx.setFont(cmbbx.getFont().deriveFont(14.0F));
            cmbbx.setShadeWidth(1);
            cmbbx.addItemListener(evt -> {
               if (evt.getStateChange() == 1) {
                  Member mx = (Member)evt.getItem();
                  s.setResponsible(mx != null ? mx.id : null);
                  if (mx != null) {
                     this.checkboxs.stream().filter(ix -> ix.getName().equals(Long.toString(mx.id))).findFirst().ifPresent(c -> c.setSelected(true));
                  }
               } else {
                  s.setResponsible(null);
               }
            });
            panel.add(cmbbx, this.createGBC(1, 1));
            this.add(panel, gbc);

            for (int i = 0; i < list.size(); i++) {
               Color color = i % 2 == 0 ? StyleUI.COLOR_ALTERNATE_ROW : Color.WHITE;
               Member m = list.get(i);
               panel = new JPanel(new GridBagLayout()) {
                  @Override
                  public void setOpaque(boolean isOpaque) {
                     super.setOpaque(true);
                  }
               };
               panel.setBackground(color);
               panel.setBorder(new LineBorder(color, 5));
               label = new JLabel(m.getName()) {
                  @Override
                  public Dimension getPreferredSize() {
                     return new Dimension(0, super.getPreferredSize().height);
                  }
               };
               label.setFont(new Font("Georgia", 0, 14));
               label.setIcon(m.getIcon());
               panel.add(label, this.createGBC(0, -1));
               JCheckBox chkbx = new JCheckBox();
               chkbx.setName(String.valueOf(m.id));
               chkbx.setFont(new Font("Franklin Gothic Medium", 0, 14));
               chkbx.setOpaque(false);
               chkbx.setFocusable(false);
               chkbx.setSelected(s.isScheduled(m));
               chkbx.addItemListener(evt -> {
                  if (evt.getStateChange() == 2 && s.isResponsible(m)) {
                     chkbx.setSelected(true);
                  } else {
                     s.setScheduled(m, evt.getStateChange() == 1, true);
                  }
               });
               GridBagConstraints gbc2 = new GridBagConstraints();
               gbc2.gridx = 1;
               gbc2.gridy = 0;
               panel.add(chkbx, gbc2);
               gbc.gridy++;
               if (i == list.size() - 1) {
                  gbc.weighty = 1.0;
               }

               this.add(panel, gbc);
               this.checkboxs.add(chkbx);
            }
         }
      });
      this.revalidate();
      this.repaint();
   }

   private GridBagConstraints createGBC(int gridy, int gridwidth) {
      GridBagConstraints gbc = new GridBagConstraints();
      gbc.gridx = 0;
      gbc.gridy = gridy;
      gbc.gridwidth = gridwidth;
      gbc.fill = 2;
      gbc.weightx = 1.0;
      return gbc;
   }
}
