package org.apache.wicket.util.collections;

import java.util.Iterator;

public abstract class ReadOnlyIterator<T> implements Iterator<T> {
   public final void remove() {
      throw new UnsupportedOperationException("Iterator " + this.getClass().getName() + " is a read-only iterator. Calls to remove() are not allowed");
   }
}
