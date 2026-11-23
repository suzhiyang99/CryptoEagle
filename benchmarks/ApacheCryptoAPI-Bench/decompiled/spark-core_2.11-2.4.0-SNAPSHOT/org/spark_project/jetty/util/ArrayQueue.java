package org.spark_project.jetty.util;

import java.util.AbstractList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class ArrayQueue<E> extends AbstractList<E> implements Queue<E> {
   public static final int DEFAULT_CAPACITY = 64;
   public static final int DEFAULT_GROWTH = 32;
   protected final Object _lock;
   protected final int _growCapacity;
   protected Object[] _elements;
   protected int _nextE;
   protected int _nextSlot;
   protected int _size;

   public ArrayQueue() {
      this(64, -1);
   }

   public ArrayQueue(Object lock) {
      this(64, -1, lock);
   }

   public ArrayQueue(int capacity) {
      this(capacity, -1);
   }

   public ArrayQueue(int initCapacity, int growBy) {
      this(initCapacity, growBy, (Object)null);
   }

   public ArrayQueue(int initCapacity, int growBy, Object lock) {
      this._lock = lock == null ? this : lock;
      this._growCapacity = growBy;
      this._elements = new Object[initCapacity];
   }

   public Object lock() {
      return this._lock;
   }

   public int getCapacity() {
      synchronized(this._lock) {
         return this._elements.length;
      }
   }

   public int getNextSlotUnsafe() {
      return this._nextSlot;
   }

   public boolean add(E e) {
      if (!this.offer(e)) {
         throw new IllegalStateException("Full");
      } else {
         return true;
      }
   }

   public boolean offer(E e) {
      synchronized(this._lock) {
         return this.enqueue(e);
      }
   }

   protected boolean enqueue(E e) {
      if (this._size == this._elements.length && !this.growUnsafe()) {
         return false;
      } else {
         ++this._size;
         this._elements[this._nextSlot++] = e;
         if (this._nextSlot == this._elements.length) {
            this._nextSlot = 0;
         }

         return true;
      }
   }

   public void addUnsafe(E e) {
      if (!this.enqueue(e)) {
         throw new IllegalStateException("Full");
      }
   }

   public E element() {
      synchronized(this._lock) {
         if (this.isEmpty()) {
            throw new NoSuchElementException();
         } else {
            return (E)this.at(this._nextE);
         }
      }
   }

   private E at(int index) {
      return (E)this._elements[index];
   }

   public E peek() {
      synchronized(this._lock) {
         return (E)(this._size == 0 ? null : this.at(this._nextE));
      }
   }

   public E peekUnsafe() {
      return (E)(this._size == 0 ? null : this.at(this._nextE));
   }

   public E poll() {
      synchronized(this._lock) {
         return (E)(this._size == 0 ? null : this.dequeue());
      }
   }

   public E pollUnsafe() {
      return (E)(this._size == 0 ? null : this.dequeue());
   }

   protected E dequeue() {
      E e = (E)this.at(this._nextE);
      this._elements[this._nextE] = null;
      --this._size;
      if (++this._nextE == this._elements.length) {
         this._nextE = 0;
      }

      return e;
   }

   public E remove() {
      synchronized(this._lock) {
         if (this._size == 0) {
            throw new NoSuchElementException();
         } else {
            return (E)this.dequeue();
         }
      }
   }

   public void clear() {
      synchronized(this._lock) {
         this._size = 0;
         this._nextE = 0;
         this._nextSlot = 0;
      }
   }

   public boolean isEmpty() {
      synchronized(this._lock) {
         return this._size == 0;
      }
   }

   public int size() {
      synchronized(this._lock) {
         return this._size;
      }
   }

   public int sizeUnsafe() {
      return this._size;
   }

   public E get(int index) {
      synchronized(this._lock) {
         if (index >= 0 && index < this._size) {
            return (E)this.getUnsafe(index);
         } else {
            throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
         }
      }
   }

   public E getUnsafe(int index) {
      int i = (this._nextE + index) % this._elements.length;
      return (E)this.at(i);
   }

   public E remove(int index) {
      synchronized(this._lock) {
         if (index >= 0 && index < this._size) {
            int i = (this._nextE + index) % this._elements.length;
            E old = (E)this.at(i);
            if (i < this._nextSlot) {
               System.arraycopy(this._elements, i + 1, this._elements, i, this._nextSlot - i);
               --this._nextSlot;
               --this._size;
            } else {
               System.arraycopy(this._elements, i + 1, this._elements, i, this._elements.length - i - 1);
               if (this._nextSlot > 0) {
                  this._elements[this._elements.length - 1] = this._elements[0];
                  System.arraycopy(this._elements, 1, this._elements, 0, this._nextSlot - 1);
                  --this._nextSlot;
               } else {
                  this._nextSlot = this._elements.length - 1;
               }

               --this._size;
            }

            return old;
         } else {
            throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
         }
      }
   }

   public E set(int index, E element) {
      synchronized(this._lock) {
         if (index >= 0 && index < this._size) {
            int i = this._nextE + index;
            if (i >= this._elements.length) {
               i -= this._elements.length;
            }

            E old = (E)this.at(i);
            this._elements[i] = element;
            return old;
         } else {
            throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
         }
      }
   }

   public void add(int index, E element) {
      synchronized(this._lock) {
         if (index >= 0 && index <= this._size) {
            if (this._size == this._elements.length && !this.growUnsafe()) {
               throw new IllegalStateException("Full");
            } else {
               if (index == this._size) {
                  this.add(element);
               } else {
                  int i = this._nextE + index;
                  if (i >= this._elements.length) {
                     i -= this._elements.length;
                  }

                  ++this._size;
                  ++this._nextSlot;
                  if (this._nextSlot == this._elements.length) {
                     this._nextSlot = 0;
                  }

                  if (i < this._nextSlot) {
                     System.arraycopy(this._elements, i, this._elements, i + 1, this._nextSlot - i);
                     this._elements[i] = element;
                  } else {
                     if (this._nextSlot > 0) {
                        System.arraycopy(this._elements, 0, this._elements, 1, this._nextSlot);
                        this._elements[0] = this._elements[this._elements.length - 1];
                     }

                     System.arraycopy(this._elements, i, this._elements, i + 1, this._elements.length - i - 1);
                     this._elements[i] = element;
                  }
               }

            }
         } else {
            throw new IndexOutOfBoundsException("!(0<" + index + "<=" + this._size + ")");
         }
      }
   }

   protected void resizeUnsafe(int newCapacity) {
      newCapacity = Math.max(newCapacity, this._size);
      Object[] elements = new Object[newCapacity];
      if (this._size > 0) {
         if (this._nextSlot > this._nextE) {
            System.arraycopy(this._elements, this._nextE, elements, 0, this._size);
         } else {
            int split = this._elements.length - this._nextE;
            System.arraycopy(this._elements, this._nextE, elements, 0, split);
            System.arraycopy(this._elements, 0, elements, split, this._nextSlot);
         }
      }

      this._elements = elements;
      this._nextE = 0;
      this._nextSlot = this._size;
   }

   protected boolean growUnsafe() {
      if (this._growCapacity <= 0) {
         return false;
      } else {
         this.resizeUnsafe(this._elements.length + this._growCapacity);
         return true;
      }
   }
}
