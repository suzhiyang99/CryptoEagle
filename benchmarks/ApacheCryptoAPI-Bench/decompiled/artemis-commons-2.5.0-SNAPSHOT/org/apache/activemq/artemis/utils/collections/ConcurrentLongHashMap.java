package org.apache.activemq.artemis.utils.collections;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.locks.StampedLock;
import java.util.function.LongFunction;

public class ConcurrentLongHashMap<V> {
   private static final Object EmptyValue = null;
   private static final Object DeletedValue = new Object();
   private static final float MapFillFactor = 0.66F;
   private static final int DefaultExpectedItems = 256;
   private static final int DefaultConcurrencyLevel = 16;
   private final Section<V>[] sections;
   private static final long HashMixer = -4132994306676758123L;
   private static final int R = 47;

   public ConcurrentLongHashMap() {
      this(256);
   }

   public ConcurrentLongHashMap(int expectedItems) {
      this(expectedItems, 16);
   }

   public ConcurrentLongHashMap(int expectedItems, int numSections) {
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

      for(Section<V> s : this.sections) {
         size += s.size;
      }

      return size;
   }

   long getUsedBucketCount() {
      long usedBucketCount = 0L;

      for(Section<V> s : this.sections) {
         usedBucketCount += (long)s.usedBuckets;
      }

      return usedBucketCount;
   }

   public long capacity() {
      long capacity = 0L;

      for(Section<V> s : this.sections) {
         capacity += (long)s.capacity;
      }

      return capacity;
   }

   public boolean isEmpty() {
      for(Section<V> s : this.sections) {
         if (s.size != 0) {
            return false;
         }
      }

      return true;
   }

   public V get(long key) {
      long h = hash(key);
      return (V)this.getSection(h).get(key, (int)h);
   }

   public boolean containsKey(long key) {
      return this.get(key) != null;
   }

   public V put(long key, V value) {
      Preconditions.checkNotNull(value);
      long h = hash(key);
      return (V)this.getSection(h).put(key, value, (int)h, false, (LongFunction)null);
   }

   public V putIfAbsent(long key, V value) {
      Preconditions.checkNotNull(value);
      long h = hash(key);
      return (V)this.getSection(h).put(key, value, (int)h, true, (LongFunction)null);
   }

   public V computeIfAbsent(long key, LongFunction<V> provider) {
      Preconditions.checkNotNull(provider);
      long h = hash(key);
      return (V)this.getSection(h).put(key, (Object)null, (int)h, true, provider);
   }

   public V remove(long key) {
      long h = hash(key);
      return (V)this.getSection(h).remove(key, (Object)null, (int)h);
   }

   public boolean remove(long key, Object value) {
      Preconditions.checkNotNull(value);
      long h = hash(key);
      return this.getSection(h).remove(key, value, (int)h) != null;
   }

   private Section<V> getSection(long hash) {
      int sectionIdx = (int)(hash >>> 32) & this.sections.length - 1;
      return this.sections[sectionIdx];
   }

   public void clear() {
      for(Section<V> s : this.sections) {
         s.clear();
      }

   }

   public void forEach(EntryProcessor<V> processor) {
      for(Section<V> s : this.sections) {
         s.forEach(processor);
      }

   }

   public List<Long> keys() {
      List<Long> keys = Lists.newArrayListWithExpectedSize(this.size());
      this.forEach((key, value) -> keys.add(key));
      return keys;
   }

   public ConcurrentLongHashSet keysLongHashSet() {
      ConcurrentLongHashSet concurrentLongHashSet = new ConcurrentLongHashSet(this.size());
      this.forEach((key, value) -> concurrentLongHashSet.add(key));
      return concurrentLongHashSet;
   }

   public List<V> values() {
      List<V> values = Lists.newArrayListWithExpectedSize(this.size());
      this.forEach((key, value) -> values.add(value));
      return values;
   }

   static long hash(long key) {
      long hash = key * -4132994306676758123L;
      hash ^= hash >>> 47;
      hash *= -4132994306676758123L;
      return hash;
   }

   static int signSafeMod(long n, int Max) {
      return (int)n & Max - 1;
   }

   static int alignToPowerOfTwo(int n) {
      return (int)Math.pow((double)2.0F, (double)(32 - Integer.numberOfLeadingZeros(n - 1)));
   }

   private static final class Section<V> extends StampedLock {
      private long[] keys;
      private V[] values;
      private int capacity;
      private volatile int size;
      private int usedBuckets;
      private int resizeThreshold;

      Section(int capacity) {
         this.capacity = ConcurrentLongHashMap.alignToPowerOfTwo(capacity);
         this.keys = new long[this.capacity];
         this.values = (V[])(new Object[this.capacity]);
         this.size = 0;
         this.usedBuckets = 0;
         this.resizeThreshold = (int)((float)this.capacity * 0.66F);
      }

      V get(long key, int keyHash) {
         int bucket = keyHash;
         long stamp = this.tryOptimisticRead();
         boolean acquiredLock = false;

         try {
            while(true) {
               while(true) {
                  int capacity = this.capacity;
                  bucket = ConcurrentLongHashMap.signSafeMod((long)bucket, capacity);
                  long storedKey = this.keys[bucket];
                  V storedValue = (V)this.values[bucket];
                  if (!acquiredLock && this.validate(stamp)) {
                     if (storedKey == key) {
                        Object var19 = storedValue != ConcurrentLongHashMap.DeletedValue ? storedValue : null;
                        return (V)var19;
                     }

                     if (storedValue == ConcurrentLongHashMap.EmptyValue) {
                        Object var18 = null;
                        return (V)var18;
                     }
                     break;
                  }

                  if (!acquiredLock) {
                     stamp = this.readLock();
                     acquiredLock = true;
                     storedKey = this.keys[bucket];
                     storedValue = (V)this.values[bucket];
                  }

                  if (capacity == this.capacity) {
                     if (storedKey == key) {
                        Object var17 = storedValue != ConcurrentLongHashMap.DeletedValue ? storedValue : null;
                        return (V)var17;
                     }

                     if (storedValue == ConcurrentLongHashMap.EmptyValue) {
                        Object var12 = null;
                        return (V)var12;
                     }
                     break;
                  }

                  bucket = keyHash;
               }

               ++bucket;
            }
         } finally {
            if (acquiredLock) {
               this.unlockRead(stamp);
            }

         }
      }

      V put(long key, V value, int keyHash, boolean onlyIfAbsent, LongFunction<V> valueProvider) {
         int bucket = keyHash;
         long stamp = this.writeLock();
         int capacity = this.capacity;
         int firstDeletedKey = -1;

         try {
            while(true) {
               bucket = ConcurrentLongHashMap.signSafeMod((long)bucket, capacity);
               long storedKey = this.keys[bucket];
               V storedValue = (V)this.values[bucket];
               if (storedKey != key) {
                  if (storedValue != ConcurrentLongHashMap.EmptyValue) {
                     if (storedValue == ConcurrentLongHashMap.DeletedValue && firstDeletedKey == -1) {
                        firstDeletedKey = bucket;
                     }

                     ++bucket;
                     continue;
                  }

                  if (firstDeletedKey != -1) {
                     bucket = firstDeletedKey;
                  } else {
                     ++this.usedBuckets;
                  }

                  this.keys[bucket] = key;
                  this.values[bucket] = value != null ? value : valueProvider.apply(key);
                  ++this.size;
                  Object var78 = valueProvider != null ? this.values[bucket] : null;
                  return (V)var78;
               }

               if (storedValue == ConcurrentLongHashMap.EmptyValue) {
                  this.values[bucket] = value != null ? value : valueProvider.apply(key);
                  ++this.size;
                  ++this.usedBuckets;
                  Object var77 = valueProvider != null ? this.values[bucket] : null;
                  return (V)var77;
               }

               if (storedValue == ConcurrentLongHashMap.DeletedValue) {
                  this.values[bucket] = value != null ? value : valueProvider.apply(key);
                  ++this.size;
                  Object var76 = valueProvider != null ? this.values[bucket] : null;
                  return (V)var76;
               }

               if (!onlyIfAbsent) {
                  this.values[bucket] = value;
                  Object var75 = storedValue;
                  return (V)var75;
               }

               Object var15 = storedValue;
               return (V)var15;
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

      private V remove(long key, Object value, int keyHash) {
         int bucket = keyHash;
         long stamp = this.writeLock();

         try {
            while(true) {
               int capacity = this.capacity;
               bucket = ConcurrentLongHashMap.signSafeMod((long)bucket, capacity);
               long storedKey = this.keys[bucket];
               V storedValue = (V)this.values[bucket];
               if (storedKey != key) {
                  if (storedValue != ConcurrentLongHashMap.EmptyValue) {
                     ++bucket;
                     continue;
                  }

                  Object var20 = null;
                  return (V)var20;
               }

               if (value != null && !value.equals(storedValue)) {
                  Object var19 = null;
                  return (V)var19;
               }

               if (storedValue != ConcurrentLongHashMap.EmptyValue && storedValue != ConcurrentLongHashMap.DeletedValue) {
                  --this.size;
                  V nextValueInArray = (V)this.values[ConcurrentLongHashMap.signSafeMod((long)(bucket + 1), capacity)];
                  if (nextValueInArray == ConcurrentLongHashMap.EmptyValue) {
                     this.values[bucket] = ConcurrentLongHashMap.EmptyValue;
                     --this.usedBuckets;
                  } else {
                     this.values[bucket] = ConcurrentLongHashMap.DeletedValue;
                  }

                  Object var13 = storedValue;
                  return (V)var13;
               }

               Object var12 = null;
               return (V)var12;
            }
         } finally {
            this.unlockWrite(stamp);
         }
      }

      void clear() {
         long stamp = this.writeLock();

         try {
            Arrays.fill(this.keys, 0L);
            Arrays.fill(this.values, ConcurrentLongHashMap.EmptyValue);
            this.size = 0;
            this.usedBuckets = 0;
         } finally {
            this.unlockWrite(stamp);
         }

      }

      public void forEach(EntryProcessor<V> processor) {
         long stamp = this.tryOptimisticRead();
         int capacity = this.capacity;
         long[] keys = this.keys;
         V[] values = this.values;
         boolean acquiredReadLock = false;

         try {
            if (!this.validate(stamp)) {
               stamp = this.readLock();
               acquiredReadLock = true;
               capacity = this.capacity;
               keys = this.keys;
               values = this.values;
            }

            for(int bucket = 0; bucket < capacity; ++bucket) {
               long storedKey = keys[bucket];
               V storedValue = (V)values[bucket];
               if (!acquiredReadLock && !this.validate(stamp)) {
                  stamp = this.readLock();
                  acquiredReadLock = true;
                  storedKey = keys[bucket];
                  storedValue = (V)values[bucket];
               }

               if (storedValue != ConcurrentLongHashMap.DeletedValue && storedValue != ConcurrentLongHashMap.EmptyValue) {
                  processor.accept(storedKey, storedValue);
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
         long[] newKeys = new long[newCapacity];
         V[] newValues = (V[])(new Object[newCapacity]);

         for(int i = 0; i < this.keys.length; ++i) {
            long storedKey = this.keys[i];
            V storedValue = (V)this.values[i];
            if (storedValue != ConcurrentLongHashMap.EmptyValue && storedValue != ConcurrentLongHashMap.DeletedValue) {
               insertKeyValueNoLock(newKeys, newValues, storedKey, storedValue);
            }
         }

         this.capacity = newCapacity;
         this.keys = newKeys;
         this.values = newValues;
         this.usedBuckets = this.size;
         this.resizeThreshold = (int)((float)this.capacity * 0.66F);
      }

      private static <V> void insertKeyValueNoLock(long[] keys, V[] values, long key, V value) {
         int bucket = (int)ConcurrentLongHashMap.hash(key);

         while(true) {
            bucket = ConcurrentLongHashMap.signSafeMod((long)bucket, keys.length);
            V storedValue = (V)values[bucket];
            if (storedValue == ConcurrentLongHashMap.EmptyValue) {
               keys[bucket] = key;
               values[bucket] = value;
               return;
            }

            ++bucket;
         }
      }
   }

   public interface EntryProcessor<V> {
      void accept(long var1, V var3);
   }
}
