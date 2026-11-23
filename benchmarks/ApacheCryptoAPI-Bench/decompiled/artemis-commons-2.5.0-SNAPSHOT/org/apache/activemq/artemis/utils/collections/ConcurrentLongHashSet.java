package org.apache.activemq.artemis.utils.collections;

import com.google.common.base.Preconditions;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.locks.StampedLock;

public class ConcurrentLongHashSet {
   private static final long EmptyItem = -1L;
   private static final long DeletedItem = -2L;
   private static final float SetFillFactor = 0.66F;
   private static final int DefaultExpectedItems = 256;
   private static final int DefaultConcurrencyLevel = 16;
   private final Section[] sections;
   private static final long HashMixer = -4132994306676758123L;
   private static final int R = 47;

   public ConcurrentLongHashSet() {
      this(256);
   }

   public ConcurrentLongHashSet(int expectedItems) {
      this(expectedItems, 16);
   }

   public ConcurrentLongHashSet(int expectedItems, int numSections) {
      Preconditions.checkArgument(numSections > 0);
      if (expectedItems < numSections) {
         expectedItems = numSections;
      }

      int perSectionExpectedItems = expectedItems / numSections;
      int perSectionCapacity = (int)((float)perSectionExpectedItems / 0.66F);
      this.sections = new Section[numSections];

      for(int i = 0; i < numSections; ++i) {
         this.sections[i] = new Section(perSectionCapacity);
      }

   }

   public int size() {
      int size = 0;

      for(Section s : this.sections) {
         size += s.size;
      }

      return size;
   }

   public long capacity() {
      long capacity = 0L;

      for(Section s : this.sections) {
         capacity += (long)s.capacity;
      }

      return capacity;
   }

   public boolean isEmpty() {
      for(Section s : this.sections) {
         if (s.size != 0) {
            return false;
         }
      }

      return true;
   }

   long getUsedBucketCount() {
      long usedBucketCount = 0L;

      for(Section s : this.sections) {
         usedBucketCount += (long)s.usedBuckets;
      }

      return usedBucketCount;
   }

   public boolean contains(long item) {
      checkBiggerEqualZero(item);
      long h = hash(item);
      return this.getSection(h).contains(item, (int)h);
   }

   public boolean add(long item) {
      checkBiggerEqualZero(item);
      long h = hash(item);
      return this.getSection(h).add(item, (long)((int)h));
   }

   public boolean remove(long item) {
      checkBiggerEqualZero(item);
      long h = hash(item);
      return this.getSection(h).remove(item, (int)h);
   }

   private Section getSection(long hash) {
      int sectionIdx = (int)(hash >>> 32) & this.sections.length - 1;
      return this.sections[sectionIdx];
   }

   public void clear() {
      for(Section s : this.sections) {
         s.clear();
      }

   }

   public void forEach(ConsumerLong processor) {
      for(Section s : this.sections) {
         s.forEach(processor);
      }

   }

   public Set<Long> items() {
      Set<Long> items = new HashSet();
      Objects.requireNonNull(items);
      this.forEach(items::add);
      return items;
   }

   static long hash(long key) {
      long hash = key * -4132994306676758123L;
      hash ^= hash >>> 47;
      hash *= -4132994306676758123L;
      return hash;
   }

   static int signSafeMod(long n, int Max) {
      return (int)(n & (long)(Max - 1));
   }

   static int alignToPowerOfTwo(int n) {
      return (int)Math.pow((double)2.0F, (double)(32 - Integer.numberOfLeadingZeros(n - 1)));
   }

   static void checkBiggerEqualZero(long n) {
      if (n < 0L) {
         throw new IllegalArgumentException("Keys and values must be >= 0");
      }
   }

   private static final class Section extends StampedLock {
      private long[] table;
      private int capacity;
      private volatile int size;
      private int usedBuckets;
      private int resizeThreshold;

      Section(int capacity) {
         this.capacity = ConcurrentLongHashSet.alignToPowerOfTwo(capacity);
         this.table = new long[this.capacity];
         this.size = 0;
         this.usedBuckets = 0;
         this.resizeThreshold = (int)((float)this.capacity * 0.66F);
         Arrays.fill(this.table, -1L);
      }

      boolean contains(long item, int hash) {
         long stamp = this.tryOptimisticRead();
         boolean acquiredLock = false;
         int bucket = ConcurrentLongHashSet.signSafeMod((long)hash, this.capacity);

         try {
            while(true) {
               long storedItem = this.table[bucket];
               if (!acquiredLock && this.validate(stamp)) {
                  if (item == storedItem) {
                     boolean var16 = true;
                     return var16;
                  }

                  if (storedItem == -1L) {
                     boolean var15 = false;
                     return var15;
                  }
               } else {
                  if (!acquiredLock) {
                     stamp = this.readLock();
                     acquiredLock = true;
                     bucket = ConcurrentLongHashSet.signSafeMod((long)hash, this.capacity);
                     storedItem = this.table[bucket];
                  }

                  if (item == storedItem) {
                     boolean var14 = true;
                     return var14;
                  }

                  if (storedItem == -1L) {
                     boolean var10 = false;
                     return var10;
                  }
               }

               bucket = bucket + 1 & this.table.length - 1;
            }
         } finally {
            if (acquiredLock) {
               this.unlockRead(stamp);
            }

         }
      }

      boolean add(long item, long hash) {
         long stamp = this.writeLock();
         int bucket = ConcurrentLongHashSet.signSafeMod(hash, this.capacity);
         int firstDeletedItem = -1;

         try {
            while(true) {
               long storedItem = this.table[bucket];
               if (item == storedItem) {
                  boolean var34 = false;
                  return var34;
               }

               if (storedItem == -1L) {
                  if (firstDeletedItem != -1) {
                     bucket = firstDeletedItem;
                  } else {
                     ++this.usedBuckets;
                  }

                  this.table[bucket] = item;
                  ++this.size;
                  boolean var11 = true;
                  return var11;
               }

               if (storedItem == -2L && firstDeletedItem == -1) {
                  firstDeletedItem = bucket;
               }

               bucket = bucket + 1 & this.table.length - 1;
            }
         } finally {
            if (this.usedBuckets > this.resizeThreshold) {
               try {
                  this.rehash();
               } finally {
                  this.unlockWrite(stamp);
               }
            } else {
               this.unlockWrite(stamp);
            }

         }
      }

      private boolean remove(long item, int hash) {
         long stamp = this.writeLock();
         int bucket = ConcurrentLongHashSet.signSafeMod((long)hash, this.capacity);

         try {
            while(true) {
               long storedItem = this.table[bucket];
               if (item == storedItem) {
                  --this.size;
                  this.cleanBucket(bucket);
                  boolean var13 = true;
                  return var13;
               }

               if (storedItem == -1L) {
                  boolean var9 = false;
                  return var9;
               }

               bucket = bucket + 1 & this.table.length - 1;
            }
         } finally {
            this.unlockWrite(stamp);
         }
      }

      private void cleanBucket(int bucket) {
         int nextInArray = bucket + 1 & this.table.length - 1;
         if (this.table[nextInArray] == -1L) {
            this.table[bucket] = -1L;
            --this.usedBuckets;
         } else {
            this.table[bucket] = -2L;
         }

      }

      void clear() {
         long stamp = this.writeLock();

         try {
            Arrays.fill(this.table, -1L);
            this.size = 0;
            this.usedBuckets = 0;
         } finally {
            this.unlockWrite(stamp);
         }

      }

      public void forEach(ConsumerLong processor) {
         long stamp = this.tryOptimisticRead();
         long[] table = this.table;
         boolean acquiredReadLock = false;

         try {
            if (!this.validate(stamp)) {
               stamp = this.readLock();
               acquiredReadLock = true;
               table = this.table;
            }

            for(int bucket = 0; bucket < table.length; ++bucket) {
               long storedItem = table[bucket];
               if (!acquiredReadLock && !this.validate(stamp)) {
                  stamp = this.readLock();
                  acquiredReadLock = true;
                  storedItem = table[bucket];
               }

               if (storedItem != -2L && storedItem != -1L) {
                  processor.accept(storedItem);
               }
            }
         } finally {
            if (acquiredReadLock) {
               this.unlockRead(stamp);
            }

         }

      }

      private void rehash() {
         int newCapacity = this.capacity * 2;
         long[] newTable = new long[newCapacity];
         Arrays.fill(newTable, -1L);

         for(int i = 0; i < this.table.length; ++i) {
            long storedItem = this.table[i];
            if (storedItem != -1L && storedItem != -2L) {
               insertKeyValueNoLock(newTable, newCapacity, storedItem);
            }
         }

         this.capacity = newCapacity;
         this.table = newTable;
         this.usedBuckets = this.size;
         this.resizeThreshold = (int)((float)this.capacity * 0.66F);
      }

      private static void insertKeyValueNoLock(long[] table, int capacity, long item) {
         int bucket = ConcurrentLongHashSet.signSafeMod(ConcurrentLongHashSet.hash(item), capacity);

         while(true) {
            long storedKey = table[bucket];
            if (storedKey == -1L) {
               table[bucket] = item;
               return;
            }

            bucket = bucket + 1 & table.length - 1;
         }
      }
   }

   public interface ConsumerLong {
      void accept(long var1);
   }
}
