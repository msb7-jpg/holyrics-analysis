package com.limagiran.holyrics.transition;

import com.alee.laf.checkbox.WebCheckBox;
import com.alee.laf.panel.WebPanel;
import com.limagiran.holyrics.util.Utils;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Random {
   public static final String ALL_RANDOM = "all-random";
   public static final String CHECKEDS = "checkeds-values";
   private static final Map<TransitionType, RendererEffect> MAP_RANDOM = new HashMap<>();
   static final transient List<RendererEffect> LIST_RANDOM = new ArrayList<>();
   static transient volatile RendererEffect LAST_RANDOM = null;

   public static synchronized RendererEffect random() {
      return random(null);
   }

   public static synchronized RendererEffect random(TransitionSettings set) {
      List<TransitionType> list = new ArrayList<>(LIST_RANDOM.size());
      list.addAll((Collection<? extends TransitionType>)(set != null ? set.getListRandomCheckeds() : MAP_RANDOM.keySet()));
      if (list.size() == 1) {
         return MAP_RANDOM.get(list.get(0));
      } else {
         if (list.isEmpty()) {
            list.addAll(MAP_RANDOM.keySet());
         }

         TransitionType random = null;

         do {
            random = list.get(Utils.random(list.size()) - 1);
         } while (Objects.equals(random, LAST_RANDOM));

         return MAP_RANDOM.get(random);
      }
   }

   public static WebPanel getPanelSet(TransitionSettings set) {
      DefaultPanelSet p = new DefaultPanelSet(set);
      set.setValue("all-random", "true");
      List<String> values = new ArrayList<>(Arrays.asList(set.getString("checkeds-values", "").split(";")));
      values.removeIf(name -> !TransitionType.exists(name));
      if (values.isEmpty()) {
         TransitionType.forEachExceptRandom(tt -> values.add(tt.name()));
      }

      set.setValue("checkeds-values", String.join(";", values));
      WebPanel panelList = new WebPanel(new GridLayout(0, 2, 10, 3));
      panelList.setMargin(8, 0, 0, 0);
      TransitionType.forEachExceptRandom(tt -> {
         WebCheckBox chkbx = new WebCheckBox(tt.name, values.contains(tt.name()));
         chkbx.addActionListener(e -> {
            if (!chkbx.isSelected()) {
               values.removeIf(n -> n.equalsIgnoreCase(tt.name()));
            } else if (!values.contains(tt.name())) {
               values.add(tt.name());
            }

            set.setValue("checkeds-values", String.join(";", values));
         });
         chkbx.setFocusable(false);
         panelList.add(chkbx);
      });
      p.put(panelList);
      return p;
   }

   static {
      TransitionType.forEachExceptRandom(tt -> {
         RendererEffect var10000 = MAP_RANDOM.put(tt, tt.rendererFactory.get());
      });
      LIST_RANDOM.addAll(new ArrayList<>(MAP_RANDOM.values()));
   }
}
