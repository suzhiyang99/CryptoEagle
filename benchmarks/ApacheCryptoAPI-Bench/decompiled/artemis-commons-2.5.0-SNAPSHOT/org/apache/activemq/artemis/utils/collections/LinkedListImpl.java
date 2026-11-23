package org.apache.activemq.artemis.utils.collections;

import java.lang.reflect.Array;
import java.util.NoSuchElementException;

public class LinkedListImpl<E> implements LinkedList<E> {
   private static final int INITIAL_ITERATOR_ARRAY_SIZE = 10;
   private final Node<E> head = new Node<E>((Object)null);
   private Node<E> tail = null;
   private int size;
   private volatile LinkedListImpl<E>.Iterator[] iters = this.createIteratorArray(10);
   private int numIters;
   private int nextIndex;

   public void addHead(E e) {
      Node<E> node = new Node<E>(e);
      node.next = this.head.next;
      node.prev = this.head;
      this.head.next = node;
      if (this.size == 0) {
         this.tail = node;
      } else {
         node.next.prev = node;
      }

      ++this.size;
   }

   public void addTail(E e) {
      if (this.size == 0) {
         this.addHead(e);
      } else {
         Node<E> node = new Node<E>(e);
         node.prev = this.tail;
         this.tail.next = node;
         this.tail = node;
         ++this.size;
      }

   }

   public E poll() {
      Node<E> ret = this.head.next;
      if (ret != null) {
         this.removeAfter(this.head);
         return ret.val;
      } else {
         return null;
      }
   }

   public void clear() {
      this.tail = this.head.next = null;
      this.size = 0;
   }

   public int size() {
      return this.size;
   }

   public LinkedListIterator<E> iterator() {
      return new Iterator();
   }

   public String toString() {
      StringBuilder str = new StringBuilder("LinkedListImpl [ ");

      for(Node<E> node = this.head; node != null; node = node.next) {
         str.append(node.toString());
         if (node.next != null) {
            str.append(", ");
         }
      }

      return str.toString();
   }

   public int numIters() {
      return this.numIters;
   }

   private LinkedListImpl<E>.Iterator[] createIteratorArray(int size) {
      return (Iterator[])Array.newInstance(Iterator.class, size);
   }

   private void removeAfter(Node<E> node) {
      Node<E> toRemove = node.next;
      node.next = toRemove.next;
      if (toRemove.next != null) {
         toRemove.next.prev = node;
      }

      if (toRemove == this.tail) {
         this.tail = node;
      }

      --this.size;
      if (toRemove.iterCount != 0) {
         this.nudgeIterators(toRemove);
      }

      toRemove.next = toRemove.prev = null;
   }

   private synchronized void nudgeIterators(Node<E> node) {
      for(int i = 0; i < this.numIters; ++i) {
         LinkedListImpl<E>.Iterator iter = this.iters[i];
         if (iter != null) {
            iter.nudged(node);
         }
      }

   }

   private synchronized void addIter(LinkedListImpl<E>.Iterator iter) {
      if (this.numIters == this.iters.length) {
         this.resize(2 * this.numIters);
      }

      this.iters[this.nextIndex++] = iter;
      ++this.numIters;
   }

   private synchronized void resize(int newSize) {
      LinkedListImpl<E>.Iterator[] newIters = this.createIteratorArray(newSize);
      System.arraycopy(this.iters, 0, newIters, 0, this.numIters);
      this.iters = newIters;
   }

   private synchronized void removeIter(LinkedListImpl<E>.Iterator iter) {
      for(int i = 0; i < this.numIters; ++i) {
         if (iter == this.iters[i]) {
            this.iters[i] = null;
            if (i != this.numIters - 1) {
               System.arraycopy(this.iters, i + 1, this.iters, i, this.numIters - i - 1);
            }

            --this.numIters;
            if (this.numIters >= 10 && this.numIters == this.iters.length / 2) {
               this.resize(this.numIters);
            }

            --this.nextIndex;
            return;
         }
      }

      throw new IllegalStateException("Cannot find iter to remove");
   }

   private static final class Node<E> {
      Node<E> next;
      Node<E> prev;
      final E val;
      int iterCount;

      Node(E e) {
         this.val = e;
      }

      public String toString() {
         return "Node, value = " + this.val;
      }
   }

   private class Iterator implements LinkedListIterator<E> {
      Node<E> last;
      Node<E> current;
      boolean repeat;

      Iterator() {
         this.current = LinkedListImpl.this.head.next;
         if (this.current != null) {
            ++this.current.iterCount;
         }

         LinkedListImpl.this.addIter(this);
      }

      public void repeat() {
         this.repeat = true;
      }

      public boolean hasNext() {
         Node<E> e = this.getNode();
         return e == null || e == this.last && !this.repeat ? this.canAdvance() : true;
      }

      public E next() {
         Node<E> e = this.getNode();
         if (this.repeat) {
            this.repeat = false;
            if (e != null) {
               return e.val;
            } else if (this.canAdvance()) {
               this.advance();
               e = this.getNode();
               return e.val;
            } else {
               throw new NoSuchElementException();
            }
         } else {
            if (e == null || e == this.last) {
               if (!this.canAdvance()) {
                  throw new NoSuchElementException();
               }

               this.advance();
               e = this.getNode();
            }

            this.last = e;
            this.repeat = false;
            return e.val;
         }
      }

      public void remove() {
         if (this.last == null) {
            throw new NoSuchElementException();
         } else if (this.current == null) {
            throw new NoSuchElementException();
         } else {
            LinkedListImpl.this.removeAfter(this.current.prev);
            this.last = null;
         }
      }

      public void close() {
         LinkedListImpl.this.removeIter(this);
      }

      public void nudged(Node<E> node) {
         if (this.current == node) {
            if (this.canAdvance()) {
               this.advance();
            } else if (this.current.prev != LinkedListImpl.this.head) {
               --this.current.iterCount;
               this.current = this.current.prev;
               ++this.current.iterCount;
            } else {
               this.current = null;
            }
         }

      }

      private Node<E> getNode() {
         if (this.current == null) {
            this.current = LinkedListImpl.this.head.next;
            if (this.current != null) {
               ++this.current.iterCount;
            }
         }

         return this.current != null ? this.current : null;
      }

      private boolean canAdvance() {
         if (this.current == null) {
            this.current = LinkedListImpl.this.head.next;
            if (this.current != null) {
               ++this.current.iterCount;
            }
         }

         return this.current != null && this.current.next != null;
      }

      private void advance() {
         if (this.current != null && this.current.next != null) {
            --this.current.iterCount;
            this.current = this.current.next;
            ++this.current.iterCount;
         } else {
            throw new NoSuchElementException();
         }
      }
   }
}
