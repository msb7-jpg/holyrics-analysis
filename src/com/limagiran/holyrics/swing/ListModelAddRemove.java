package com.limagiran.holyrics.swing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import javax.swing.AbstractListModel;

public class ListModelAddRemove<T> extends AbstractListModel<T> {
   protected final List<T> list;
   private ListModelAddRemove.ListModelAddRemoveListener listener;

   public ListModelAddRemove(List<T> list) {
      this.list = list;
      this.addElements(this.list, false);
   }

   public ListModelAddRemove() {
      this(new ArrayList<>());
   }

   public void setListModelAddRemoveListener(ListModelAddRemove.ListModelAddRemoveListener listener) {
      this.listener = listener;
   }

   @Override
   public int getSize() {
      return this.list.size();
   }

   @Override
   public T getElementAt(int index) {
      return index >= 0 && index < this.list.size() ? this.list.get(index) : null;
   }

   public void addElement(T t) {
      if (!this.list.contains(t)) {
         int index = this.list.size();
         this.list.add(t);
         this.fireIntervalAdded(this, index, index);
         this.changed();
      }
   }

   public final void addElements(List<T> l, boolean changed) {
      int i = this.list.size();
      l.stream().distinct().filter(m -> !this.list.contains(m)).forEach(this.list::add);
      int i2 = this.list.size() - 1;
      this.fireIntervalAdded(this, i, i2 < 0 ? 0 : i2);
      if (changed) {
         this.changed();
      }
   }

   public void addElements(List<T> list) {
      this.addElements(list, true);
   }

   public boolean contains(T item) {
      return this.list.contains(item);
   }

   public void removeElement(T t) {
      if (this.list.contains(t)) {
         int index = this.list.size();
         this.list.remove(t);
         this.fireIntervalAdded(this, index, index);
         this.changed();
      }
   }

   public void removeElements(List<T> l) {
      int i = this.list.size();
      l.stream().distinct().forEach(this.list::remove);
      int i2 = this.list.size() - 1;
      this.fireIntervalRemoved(this, i2 < 0 ? 0 : i2, i);
      this.changed();
   }

   public void removeAllElements(boolean changed) {
      int index = this.list.size() - 1;
      this.list.clear();
      if (index >= 0) {
         this.fireIntervalRemoved(this, 0, index);
      }

      if (changed) {
         this.changed();
      }
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public void sort(Comparator<? super T> c) {
      this.list.sort(c);
      this.fireContentsChanged(this, 0, this.list.size());
   }

   public List<T> copyList() {
      return new ArrayList<>(this.list);
   }

   public void changed() {
      Optional.ofNullable(this.listener).ifPresent(l -> l.changed());
   }

   public interface ListModelAddRemoveListener {
      void changed();
   }
}
