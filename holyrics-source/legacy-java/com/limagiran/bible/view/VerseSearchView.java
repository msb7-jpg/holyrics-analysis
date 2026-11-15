package com.limagiran.bible.view;

import com.limagiran.bible.Bible;
import com.limagiran.bible.Verse;
import com.limagiran.bible.i18n.I18N;
import com.limagiran.bible.util.InsertShortcut;
import com.limagiran.bible.util.Values;
import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.JDialog;

public final class VerseSearchView extends JDialog implements VerseSearchPanel.VerseSearchPanelListener {
   private VerseSearchPanel findPanel;
   private final VerseSearchView.VerseSearchViewListener listener;

   public VerseSearchView(List<Bible> bibles, VerseSearchView.VerseSearchViewListener listener) {
      this.listener = listener;
      this.init(bibles);
   }

   private void init(List<Bible> bibles) {
      this.setDefaultCloseOperation(0);
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosing(WindowEvent e) {
            VerseSearchView.this.canceled();
         }
      });
      this.setLayout(new BorderLayout());
      this.findPanel = new VerseSearchPanel(this, bibles, this);
      this.add(this.findPanel, "Center");
      this.setIconImages(Values.ICONS_SEARCH);
      this.setTitle(I18N.WORD_SEARCH.val());
      this.setModal(true);
      this.pack();
      this.setLocationRelativeTo(null);
      this.setResizable(false);
      InsertShortcut.add(this, InsertShortcut.Shortcut.create(27, this::canceled));
   }

   @Override
   public void verseSelected(Verse verse) {
      this.dispose();
      this.listener.verseSelected(verse);
   }

   @Override
   public void canceled() {
      this.dispose();
   }

   public interface VerseSearchViewListener {
      void verseSelected(Verse var1);
   }
}
