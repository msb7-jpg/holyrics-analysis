package com.limagiran.bible.view;

import com.limagiran.bible.Bible;
import com.limagiran.bible.Verse;
import com.limagiran.bible.i18n.I18N;
import com.limagiran.bible.swingmodel.ListCellRendererFoundVerses;
import com.limagiran.bible.swingmodel.ListModelVerse;
import com.limagiran.bible.util.Utils;
import com.limagiran.bible.util.VerseFilter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

public class TabResult extends JScrollPane {
   private final VerseFilter verseFilter;
   private final JList<Verse> list = new JList<>();
   protected final List<Bible> bibles = new ArrayList<>();
   private final JLabel footer;
   private final TabResult.TabResultListener listener;
   private final TreeSet<Verse> result = new TreeSet<>();
   private final ListModelVerse listModel = new ListModelVerse();

   public TabResult(List<Bible> bibles, VerseFilter verseFilter, JLabel footer, TabResult.TabResultListener listener) {
      this.bibles.addAll(bibles);
      this.verseFilter = verseFilter;
      this.footer = footer;
      this.listener = listener;
      this.init();
   }

   private void init() {
      this.setHorizontalScrollBarPolicy(31);
      this.setVerticalScrollBarPolicy(22);
      this.list.setModel(this.listModel);
      this.list.setSelectionMode(0);
      this.list.setToolTipText(I18N.MSG_TT_JLIST_TAB_RESULT.val());
      this.list.setCellRenderer(new ListCellRendererFoundVerses(this.verseFilter));
      this.list.setFixedCellWidth(784);
      this.list.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent evt) {
            if (Utils.isDoubleClick(evt) && TabResult.this.list.getSelectedValue() != null) {
               TabResult.this.listener.verseSelected(TabResult.this.list.getSelectedValue());
            }
         }
      });
      this.setViewportView(this.list);
   }

   public void clear() {
      this.listModel.removeAllElements();
      this.result.clear();
   }

   public void search(VerseFilter vf) {
      this.result.clear();
      this.bibles.stream().map(b -> b.find(vf)).forEach(this.result::addAll);
   }

   public void refresh() {
      if (!this.result.isEmpty()) {
         this.fillList();
         this.revalidate();
         this.repaint();
         this.list.revalidate();
         this.list.repaint();
         this.listModel.repaint();
      }
   }

   public synchronized void fillList() {
      new Thread(() -> {
         List<Verse> verses = this.verseFilter.getVersesToShow(new ArrayList<>(this.result));
         SwingUtilities.invokeLater(() -> {
            this.revalidate();
            this.repaint();
            this.listModel.removeAllElements();
            verses.subList(0, Math.min(this.verseFilter.getMaxResults(), verses.size())).forEach(this.listModel::addElement);
            this.footer.setText(I18N.MSG_RESULTS_X.format(verses.size()));
         });
      }).start();
   }

   public interface TabResultListener {
      void verseSelected(Verse var1);
   }
}
