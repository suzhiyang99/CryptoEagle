package org.apache.wicket.util.collections;

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ReverseListIterator<E> implements Iterator<E>, Iterable<E> {
   private final ListIterator<E> delegateIterator;

   public ReverseListIterator(List<E> list) {
      int start = list.size();
      this.delegateIterator = list.listIterator(start);
   }

   public boolean hasNext() {
      return this.delegateIterator.hasPrevious();
   }

   public E next() {
      return (E)this.delegateIterator.previous();
   }

   public void remove() {
      throw new UnsupportedOperationException();
   }

   public Iterator<E> iterator() {
      return this;
   }
}
