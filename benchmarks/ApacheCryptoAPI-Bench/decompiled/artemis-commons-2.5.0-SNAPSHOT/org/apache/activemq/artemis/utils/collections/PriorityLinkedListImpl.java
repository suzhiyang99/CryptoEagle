package org.apache.activemq.artemis.utils.collections;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

public class PriorityLinkedListImpl<T> implements PriorityLinkedList<T> {
   protected LinkedListImpl<T>[] levels;
   private int size;
   private int lastReset;
   private int highestPriority = -1;
   private int lastPriority = -1;

   public PriorityLinkedListImpl(int priorities) {
      this.levels = (LinkedListImpl[])Array.newInstance(LinkedListImpl.class, priorities);

      for(int i = 0; i < priorities; ++i) {
         this.levels[i] = new LinkedListImpl();
      }

   }

   private void checkHighest(int priority) {
      if (this.lastPriority != priority || priority > this.highestPriority) {
         this.lastPriority = priority;
         if (this.lastReset == Integer.MAX_VALUE) {
            this.lastReset = 0;
         } else {
            ++this.lastReset;
         }
      }

      if (priority > this.highestPriority) {
         this.highestPriority = priority;
      }

   }

   public void addHead(T t, int priority) {
      this.checkHighest(priority);
      this.levels[priority].addHead(t);
      ++this.size;
   }

   public void addTail(T t, int priority) {
      this.checkHighest(priority);
      this.levels[priority].addTail(t);
      ++this.size;
   }

   public T poll() {
      T t = null;

      for(int i = this.highestPriority; i >= 0; --i) {
         LinkedListImpl<T> ll = this.levels[i];
         if (ll.size() != 0) {
            t = ll.poll();
            if (t != null) {
               --this.size;
               if (ll.size() == 0 && this.highestPriority == i) {
                  --this.highestPriority;
               }
            }
            break;
         }
      }

      return t;
   }

   public void clear() {
      for(LinkedListImpl<T> list : this.levels) {
         list.clear();
      }

      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public LinkedListIterator<T> iterator() {
      return new PriorityLinkedListIterator();
   }

   private class PriorityLinkedListIterator implements LinkedListIterator<T> {
      private int index;
      private final LinkedListIterator<T>[] cachedIters;
      private LinkedListIterator<T> lastIter;
      private int resetCount;
      volatile boolean closed;

      PriorityLinkedListIterator() {
         this.cachedIters = new LinkedListIterator[PriorityLinkedListImpl.this.levels.length];
         this.resetCount = PriorityLinkedListImpl.this.lastReset;
         this.closed = false;
         this.index = PriorityLinkedListImpl.this.levels.length - 1;
      }

      protected void finalize() {
         this.close();
      }

      public void repeat() {
         if (this.lastIter == null) {
            throw new NoSuchElementException();
         } else {
            this.lastIter.repeat();
         }
      }

      public void close() {
         if (!this.closed) {
            this.closed = true;
            this.lastIter = null;

            for(LinkedListIterator<T> iter : this.cachedIters) {
               if (iter != null) {
                  iter.close();
               }
            }
         }

      }

      private void checkReset() {
         if (PriorityLinkedListImpl.this.lastReset != this.resetCount) {
            this.index = PriorityLinkedListImpl.this.highestPriority;
            this.resetCount = PriorityLinkedListImpl.this.lastReset;
         }

      }

      public boolean hasNext() {
         this.checkReset();

         while(this.index >= 0) {
            this.lastIter = this.cachedIters[this.index];
            if (this.lastIter == null) {
               this.lastIter = this.cachedIters[this.index] = PriorityLinkedListImpl.this.levels[this.index].iterator();
            }

            boolean b = this.lastIter.hasNext();
            if (b) {
               return true;
            }

            --this.index;
            if (this.index < 0) {
               this.index = PriorityLinkedListImpl.this.levels.length - 1;
               break;
            }
         }

         return false;
      }

      public T next() {
         if (this.lastIter == null) {
            throw new NoSuchElementException();
         } else {
            return (T)this.lastIter.next();
         }
      }

      public void remove() {
         if (this.lastIter == null) {
            throw new NoSuchElementException();
         } else {
            this.lastIter.remove();

            for(int i = this.index; i >= 0 && PriorityLinkedListImpl.this.levels[this.index].size() == 0; --i) {
               PriorityLinkedListImpl.this.highestPriority = i;
            }

            PriorityLinkedListImpl.this.size--;
         }
      }
   }
}
