package com.limagiran.holyrics.view.panel;

import com.limagiran.holyrics.model.Presentation;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.view.PresentationSlideView;
import java.awt.GridLayout;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class MultiSlidePreview extends JPanel {
   private PresentationSlideView.EnumScreenType screenType = PresentationSlideView.EnumScreenType.DISPLAY_MONITOR;

   public MultiSlidePreview() {
      this.init();
   }

   private void init() {
      this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
      this.setLayout(new GridLayout(5, 5));
   }

   private List<PresentationSlideView> getAllSlides() {
      return Arrays.asList(this.getComponents())
         .stream()
         .filter(PresentationSlideView.class::isInstance)
         .map(PresentationSlideView.class::cast)
         .collect(Collectors.toList());
   }

   public synchronized void refreshPreview(List<Presentation> list) {
      try {
         Theme theme = list.isEmpty() ? Theme.BLACK_SCREEN : list.get(0).theme;
         SwingUtilities.invokeLater(
            () -> {
               int count = this.getComponentCount();
               if (list.size() > count) {
                  IntStream.range(0, list.size() - count)
                     .mapToObj(ix -> PresentationSlideView.builder().theme(theme).screenType(this.screenType).getSlideView())
                     .forEach(this::add);
               } else if (list.size() < count) {
                  IntStream.range(0, count - list.size()).forEach(ix -> this.remove(0));
               }

               this.updateGrid();

               for (int i = 0; i < this.getComponentCount(); i++) {
                  if (this.getComponent(i) instanceof PresentationSlideView) {
                     PresentationSlideView sv = (PresentationSlideView)this.getComponent(i);
                     sv.setText(list.get(i).text);
                  }
               }
            }
         );
      } catch (Exception var3) {
      }
   }

   private synchronized void updateGrid() {
      try {
         int[] grid = SwingUtils.getGrid(this.getComponentCount(), this.getSize(), this.screenType.getSize());
         this.setLayout(new GridLayout(grid[0], grid[1], 5, 5));
         this.revalidate();
         this.repaint();
      } catch (Exception var2) {
      }
   }

   public void setScreenType(PresentationSlideView.EnumScreenType screenType) {
      this.getAllSlides().forEach(p -> p.setScreenType(this.screenType = screenType));
      SwingUtilities.invokeLater(() -> this.updateGrid());
   }

   public void setTheme(Theme theme) {
      this.getAllSlides().forEach(p -> p.setTheme(theme));
   }
}
