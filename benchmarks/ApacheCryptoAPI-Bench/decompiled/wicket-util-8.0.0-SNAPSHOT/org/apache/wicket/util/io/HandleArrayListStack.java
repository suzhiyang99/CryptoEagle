package org.apache.wicket.util.io;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;

final class HandleArrayListStack<T> extends ArrayList<T> {
   private static final long serialVersionUID = 1L;

   public HandleArrayListStack() {
      this(10);
   }

   public HandleArrayListStack(Collection<? extends T> collection) {
      super(collection);
   }

   public HandleArrayListStack(int initialCapacity) {
      super(initialCapacity);
   }

   public final boolean empty() {
      return this.size() == 0;
   }

   public int indexOf(Object elem) {
      int size = this.size();
      if (elem == null) {
         for(int i = 0; i < size; ++i) {
            if (this.get(i) == null) {
               return i;
            }
         }
      } else {
         for(int i = 0; i < size; ++i) {
            if (elem == this.get(i)) {
               return i;
            }
         }
      }

      return -1;
   }

   public int lastIndexOf(Object elem) {
      if (elem == null) {
         for(int i = this.size() - 1; i >= 0; --i) {
            if (this.get(i) == null) {
               return i;
            }
         }
      } else {
         for(int i = this.size() - 1; i >= 0; --i) {
            if (elem == this.get(i)) {
               return i;
            }
         }
      }

      return -1;
   }

   public final T peek() {
      int size = this.size();
      if (size == 0) {
         throw new EmptyStackException();
      } else {
         return (T)this.get(size - 1);
      }
   }

   public final T pop() {
      T top = (T)this.peek();
      this.remove(this.size() - 1);
      return top;
   }

   public final void push(T item) {
      this.add(item);
   }

   public final int search(Object o) {
      int i = this.lastIndexOf(o);
      return i >= 0 ? this.size() - i : -1;
   }
}
