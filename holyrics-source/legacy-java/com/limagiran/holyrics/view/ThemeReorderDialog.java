package com.limagiran.holyrics.view;

import com.alee.laf.label.WebLabel;
import com.alee.laf.panel.WebPanel;
import com.alee.laf.scroll.WebScrollPane;
import com.limagiran.holyrics.control.ThemeController;
import com.limagiran.holyrics.i18n.I18N;
import com.limagiran.holyrics.model.theme.Theme;
import com.limagiran.holyrics.swing.DragComponentMouseAdapter;
import com.limagiran.holyrics.util.Images;
import com.limagiran.holyrics.util.InsertShortcut;
import com.limagiran.holyrics.util.MySwingWorker;
import com.limagiran.holyrics.util.PopUpWorker;
import com.limagiran.holyrics.util.PresentationSlideUtils;
import com.limagiran.holyrics.util.SwingUtils;
import com.limagiran.holyrics.util.WrapLayout;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;

public class ThemeReorderDialog extends JDialog implements DragComponentMouseAdapter.DragComponentListener {
   private final Runnable callback;
   private final WebPanel grid;

   public ThemeReorderDialog(Runnable callback) {
      super((Frame)null, true);
      this.callback = callback;
      this.grid = new WebPanel(new WrapLayout(1, 4, 4));
      this.init();
   }

   private void init() {
      this.setLayout(new BorderLayout());
      this.setDefaultCloseOperation(0);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            ThemeReorderDialog.this.dispose();
         }
      });
      this.setTitle(I18N.WORD_THEMES.val());
      this.setIconImages(Images.HOLYRICS_ICON);
      InsertShortcut.addEscToDispose(this);
      this.grid.setOpaque(false);
      ThemeController.getThemes().forEach(t -> this.grid.add(new JLabelTheme(t)));
      MouseAdapter mouseAdapter = new DragComponentMouseAdapter(this);
      this.grid.addMouseListener(mouseAdapter);
      this.grid.addMouseMotionListener(mouseAdapter);
      this.grid.setMargin(16, 32, 16, 32);
      WebScrollPane scrollGrid = new WebScrollPane(this.grid);
      scrollGrid.setFocusable(false);
      WebLabel title = new WebLabel(I18N.MSG_DRAG_TO_REORDER.val(), 0, new Object[0]);
      title.setFontName("Arial");
      title.setFontSizeAndStyle(24, 1);
      title.setMargin(16, 0, 0, 0);
      this.add(title, "First");
      this.add(scrollGrid, "Center");
      this.pack();
      SwingUtils.maximize(this, 200);
      this.setLocationRelativeTo(null);
   }

   @Override
   public void dispose() {
      super.dispose();
      Optional.ofNullable(this.callback).ifPresent(Runnable::run);
   }

   public static void main(Window window, final Runnable callback) {
      PopUpWorker.exe(new MySwingWorker<Void, Void>(window) {
         protected Void doInBackground() throws Exception {
            ThemeController.getThemes().forEach(theme -> PresentationSlideUtils.createThumb(theme, JLabelTheme.SIZE));
            return null;
         }

         @Override
         protected void done() {
            SwingUtilities.invokeLater(() -> new ThemeReorderDialog(callback).setVisible(true));
         }
      });
   }

   @Override
   public void positionChanged() {
      List<Theme> themes = new ArrayList<>();

      try {
         for (Component c : this.grid.getComponents()) {
            if (c instanceof JLabelTheme) {
               themes.add(((JLabelTheme)c).theme);
            }
         }
      } catch (Exception var6) {
      }

      ThemeController.set(themes);
   }
}
