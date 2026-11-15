package com.limagiran.holyrics.transition;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.spinner.WebSpinner;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.util.Values;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.SpinnerNumberModel;

public class DefaultPanelSet extends WebPanel implements Values {
   private int y;

   public DefaultPanelSet(TransitionSettings set) {
      super(new GridBagLayout());
      this.init(set);
   }

   private void init(TransitionSettings set) {
      this.setOpaque(false);
      this.setMargin(2, 8, 2, 8);
      this.addDuration(set);
      set.setValue("all-random", "false");
   }

   private void addDuration(TransitionSettings set) {
      SpinnerNumberModel spinnerModel = new SpinnerNumberModel(set.getDuration(), 200, 2400, 100);
      WebSpinner spinnerTransitionDuration = new WebSpinner(spinnerModel);
      spinnerTransitionDuration.setDrawFocus(false);
      spinnerTransitionDuration.setFocusable(false);
      spinnerTransitionDuration.addChangeListener(e -> set.setDuration(spinnerModel.getNumber().intValue()));
      spinnerTransitionDuration.setDrawFocus(false);
      this.put(I18N.WORD_DURATION.val(), spinnerTransitionDuration);
   }

   public void put(String name, Component component) {
      GridBagConstraints gbc = new GridBagConstraints();
      if (name != null) {
         gbc.gridy = this.y;
         gbc.gridx = 0;
         gbc.anchor = 13;
         gbc.insets.right = 6;
         this.add(new WebLabel(name + ":", 4, new Object[0]), gbc);
      }

      gbc = new GridBagConstraints();
      gbc.gridy = this.y;
      gbc.gridx = name != null ? 1 : 0;
      gbc.gridwidth = name != null ? 1 : 2;
      gbc.anchor = name != null ? 17 : 10;
      gbc.insets.top = gbc.insets.bottom = 1;
      this.add(component, gbc);
      component.setFocusable(false);
      this.y++;
   }

   public void put(Component component) {
      this.put(null, component);
   }
}
