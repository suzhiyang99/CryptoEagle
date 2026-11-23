package org.apache.wicket.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EmptyStackException;

public class ArrayListStack<T> extends ArrayList<T> {
   private static final long serialVersionUID = 1L;

   public ArrayListStack(int initialCapacity) {
      super(initialCapacity);
   }

   public ArrayListStack() {
      this(10);
   }

   public ArrayListStack(Collection<T> collection) {
      super(collection);
   }

   public final void push(T item) {
      this.add(item);
   }

   public final T pop() {
      T top = (T)this.peek();
      this.remove(this.size() - 1);
      return top;
   }

   public final T peek() {
      int size = this.size();
      if (size == 0) {
         throw new EmptyStackException();
      } else {
         return (T)this.get(size - 1);
      }
   }

   public final boolean empty() {
      return this.size() == 0;
   }

   public final int search(T o) {
      int i = this.lastIndexOf(o);
      return i >= 0 ? this.size() - i : -1;
   }
}
