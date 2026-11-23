package org.spark_project.jetty.util;

import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class ConcurrentArrayQueue<T> extends AbstractQueue<T> {
   public static final int DEFAULT_BLOCK_SIZE = 512;
   public static final Object REMOVED_ELEMENT = new Object() {
      public String toString() {
         return "X";
      }
   };
   private static final int HEAD_OFFSET = MemoryUtils.getIntegersPerCacheLine() - 1;
   private static final int TAIL_OFFSET = MemoryUtils.getIntegersPerCacheLine() * 2 - 1;
   private final AtomicReferenceArray<Block<T>> _blocks;
   private final int _blockSize;

   public ConcurrentArrayQueue() {
      this(512);
   }

   public ConcurrentArrayQueue(int blockSize) {
      this._blocks = new AtomicReferenceArray(TAIL_OFFSET + 1);
      this._blockSize = blockSize;
      Block<T> block = this.newBlock();
      this._blocks.set(HEAD_OFFSET, block);
      this._blocks.set(TAIL_OFFSET, block);
   }

   public int getBlockSize() {
      return this._blockSize;
   }

   protected Block<T> getHeadBlock() {
      return (Block)this._blocks.get(HEAD_OFFSET);
   }

   protected Block<T> getTailBlock() {
      return (Block)this._blocks.get(TAIL_OFFSET);
   }

   public boolean offer(T item) {
      item = (T)Objects.requireNonNull(item);
      Block<T> initialTailBlock = this.getTailBlock();
      Block<T> currentTailBlock = initialTailBlock;
      int tail = initialTailBlock.tail();

      while(true) {
         while(tail != this.getBlockSize()) {
            if (currentTailBlock.peek(tail) == null) {
               if (currentTailBlock.store(tail, item)) {
                  this.updateTailBlock(initialTailBlock, currentTailBlock);
                  return true;
               }

               ++tail;
            } else {
               ++tail;
            }
         }

         Block<T> nextTailBlock = currentTailBlock.next();
         if (nextTailBlock == null) {
            nextTailBlock = this.newBlock();
            if (currentTailBlock.link(nextTailBlock)) {
               currentTailBlock = nextTailBlock;
            } else {
               currentTailBlock = currentTailBlock.next();
            }
         } else {
            currentTailBlock = nextTailBlock;
         }

         tail = currentTailBlock.tail();
      }
   }

   private void updateTailBlock(Block<T> oldTailBlock, Block<T> newTailBlock) {
      if (oldTailBlock != newTailBlock) {
         this.casTailBlock(oldTailBlock, newTailBlock);
      }

   }

   protected boolean casTailBlock(Block<T> current, Block<T> update) {
      return this._blocks.compareAndSet(TAIL_OFFSET, current, update);
   }

   public T poll() {
      Block<T> initialHeadBlock = this.getHeadBlock();
      Block<T> currentHeadBlock = initialHeadBlock;
      int head = initialHeadBlock.head();
      T result = null;

      while(true) {
         if (head == this.getBlockSize()) {
            Block<T> nextHeadBlock = currentHeadBlock.next();
            if (nextHeadBlock == null) {
               break;
            }

            currentHeadBlock = nextHeadBlock;
            head = nextHeadBlock.head();
         } else {
            Object element = currentHeadBlock.peek(head);
            if (element == REMOVED_ELEMENT) {
               ++head;
            } else {
               result = (T)element;
               if (element == null || currentHeadBlock.remove(head, element, true)) {
                  break;
               }

               ++head;
            }
         }
      }

      this.updateHeadBlock(initialHeadBlock, currentHeadBlock);
      return result;
   }

   private void updateHeadBlock(Block<T> oldHeadBlock, Block<T> newHeadBlock) {
      if (oldHeadBlock != newHeadBlock) {
         this.casHeadBlock(oldHeadBlock, newHeadBlock);
      }

   }

   protected boolean casHeadBlock(Block<T> current, Block<T> update) {
      return this._blocks.compareAndSet(HEAD_OFFSET, current, update);
   }

   public T peek() {
      Block<T> currentHeadBlock = this.getHeadBlock();
      int head = currentHeadBlock.head();

      while(true) {
         while(head != this.getBlockSize()) {
            T element = currentHeadBlock.peek(head);
            if (element != REMOVED_ELEMENT) {
               return element;
            }

            ++head;
         }

         Block<T> nextHeadBlock = currentHeadBlock.next();
         if (nextHeadBlock == null) {
            return null;
         }

         currentHeadBlock = nextHeadBlock;
         head = nextHeadBlock.head();
      }
   }

   public boolean remove(Object o) {
      Block<T> currentHeadBlock = this.getHeadBlock();
      int head = currentHeadBlock.head();
      boolean result = false;

      while(true) {
         if (head == this.getBlockSize()) {
            Block<T> nextHeadBlock = currentHeadBlock.next();
            if (nextHeadBlock == null) {
               break;
            }

            currentHeadBlock = nextHeadBlock;
            head = nextHeadBlock.head();
         } else {
            Object element = currentHeadBlock.peek(head);
            if (element == REMOVED_ELEMENT) {
               ++head;
            } else {
               if (element == null) {
                  break;
               }

               if (element.equals(o)) {
                  if (currentHeadBlock.remove(head, o, false)) {
                     result = true;
                     break;
                  }

                  ++head;
               } else {
                  ++head;
               }
            }
         }
      }

      return result;
   }

   public boolean removeAll(Collection<?> c) {
      return super.removeAll(c);
   }

   public boolean retainAll(Collection<?> c) {
      return super.retainAll(c);
   }

   public Iterator<T> iterator() {
      final List<Object[]> blocks = new ArrayList();

      for(Block<T> currentHeadBlock = this.getHeadBlock(); currentHeadBlock != null; currentHeadBlock = currentHeadBlock.next()) {
         Object[] elements = currentHeadBlock.arrayCopy();
         blocks.add(elements);
      }

      return new Iterator<T>() {
         private int blockIndex;
         private int index;

         public boolean hasNext() {
            while(this.blockIndex != blocks.size()) {
               Object element = ((Object[])blocks.get(this.blockIndex))[this.index];
               if (element == null) {
                  return false;
               }

               if (element != ConcurrentArrayQueue.REMOVED_ELEMENT) {
                  return true;
               }

               this.advance();
            }

            return false;
         }

         public T next() {
            while(this.blockIndex != blocks.size()) {
               Object element = ((Object[])blocks.get(this.blockIndex))[this.index];
               if (element == null) {
                  throw new NoSuchElementException();
               }

               this.advance();
               if (element != ConcurrentArrayQueue.REMOVED_ELEMENT) {
                  return (T)element;
               }
            }

            throw new NoSuchElementException();
         }

         private void advance() {
            if (++this.index == ConcurrentArrayQueue.this.getBlockSize()) {
               this.index = 0;
               ++this.blockIndex;
            }

         }

         public void remove() {
            throw new UnsupportedOperationException();
         }
      };
   }

   public int size() {
      Block<T> currentHeadBlock = this.getHeadBlock();
      int head = currentHeadBlock.head();
      int size = 0;

      while(true) {
         if (head == this.getBlockSize()) {
            Block<T> nextHeadBlock = currentHeadBlock.next();
            if (nextHeadBlock == null) {
               break;
            }

            currentHeadBlock = nextHeadBlock;
            head = nextHeadBlock.head();
         } else {
            Object element = currentHeadBlock.peek(head);
            if (element == REMOVED_ELEMENT) {
               ++head;
            } else {
               if (element == null) {
                  break;
               }

               ++size;
               ++head;
            }
         }
      }

      return size;
   }

   protected Block<T> newBlock() {
      return new Block<T>(this.getBlockSize());
   }

   protected int getBlockCount() {
      int result = 0;

      for(Block<T> headBlock = this.getHeadBlock(); headBlock != null; headBlock = headBlock.next()) {
         ++result;
      }

      return result;
   }

   protected static final class Block<E> {
      private static final int headOffset = MemoryUtils.getIntegersPerCacheLine() - 1;
      private static final int tailOffset = MemoryUtils.getIntegersPerCacheLine() * 2 - 1;
      private final AtomicReferenceArray<Object> elements;
      private final AtomicReference<Block<E>> next = new AtomicReference();
      private final AtomicIntegerArray indexes;

      protected Block(int blockSize) {
         this.indexes = new AtomicIntegerArray(ConcurrentArrayQueue.TAIL_OFFSET + 1);
         this.elements = new AtomicReferenceArray(blockSize);
      }

      public E peek(int index) {
         return (E)this.elements.get(index);
      }

      public boolean store(int index, E item) {
         boolean result = this.elements.compareAndSet(index, (Object)null, item);
         if (result) {
            this.indexes.incrementAndGet(tailOffset);
         }

         return result;
      }

      public boolean remove(int index, Object item, boolean updateHead) {
         boolean result = this.elements.compareAndSet(index, item, ConcurrentArrayQueue.REMOVED_ELEMENT);
         if (result && updateHead) {
            this.indexes.incrementAndGet(headOffset);
         }

         return result;
      }

      public Block<E> next() {
         return (Block)this.next.get();
      }

      public boolean link(Block<E> nextBlock) {
         return this.next.compareAndSet((Object)null, nextBlock);
      }

      public int head() {
         return this.indexes.get(headOffset);
      }

      public int tail() {
         return this.indexes.get(tailOffset);
      }

      public Object[] arrayCopy() {
         Object[] result = new Object[this.elements.length()];

         for(int i = 0; i < result.length; ++i) {
            result[i] = this.elements.get(i);
         }

         return result;
      }
   }
}
