package org.apache.wicket.util.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class IntHashMap<V> implements Cloneable, Serializable {
   transient volatile Set<Integer> keySet;
   transient volatile Collection<V> values;
   static final int DEFAULT_INITIAL_CAPACITY = 16;
   static final int MAXIMUM_CAPACITY = 1073741824;
   static final float DEFAULT_LOAD_FACTOR = 0.75F;
   transient Entry<V>[] table;
   transient int size;
   int threshold;
   final float loadFactor;
   transient AtomicInteger modCount;
   private transient Set<Entry<V>> entrySet;
   private static final long serialVersionUID = 362498820763181265L;

   public IntHashMap(int initialCapacity, float loadFactor) {
      this.keySet = null;
      this.values = null;
      this.modCount = new AtomicInteger(0);
      this.entrySet = null;
      if (initialCapacity < 0) {
         throw new IllegalArgumentException("Illegal initial capacity: " + initialCapacity);
      } else {
         if (initialCapacity > 1073741824) {
            initialCapacity = 1073741824;
         }

         if (!(loadFactor <= 0.0F) && !Float.isNaN(loadFactor)) {
            int capacity;
            for(capacity = 1; capacity < initialCapacity; capacity <<= 1) {
            }

            this.loadFactor = loadFactor;
            this.threshold = (int)((float)capacity * loadFactor);
            this.table = new Entry[capacity];
            this.init();
         } else {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
         }
      }
   }

   public IntHashMap(int initialCapacity) {
      this(initialCapacity, 0.75F);
   }

   public IntHashMap() {
      this.keySet = null;
      this.values = null;
      this.modCount = new AtomicInteger(0);
      this.entrySet = null;
      this.loadFactor = 0.75F;
      this.threshold = 12;
      this.table = new Entry[16];
      this.init();
   }

   void init() {
   }

   static int indexFor(int h, int length) {
      return h & length - 1;
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public V get(int key) {
      int i = indexFor(key, this.table.length);

      for(Entry<V> e = this.table[i]; e != null; e = e.next) {
         if (key == e.key) {
            return e.value;
         }
      }

      return null;
   }

   public boolean containsKey(int key) {
      int i = indexFor(key, this.table.length);

      for(Entry<V> e = this.table[i]; e != null; e = e.next) {
         if (key == e.key) {
            return true;
         }
      }

      return false;
   }

   Entry<V> getEntry(int key) {
      int i = indexFor(key, this.table.length);

      Entry<V> e;
      for(e = this.table[i]; e != null && key != e.key; e = e.next) {
      }

      return e;
   }

   public V put(int key, V value) {
      int i = indexFor(key, this.table.length);

      for(Entry<V> e = this.table[i]; e != null; e = e.next) {
         if (key == e.key) {
            V oldValue = e.value;
            e.value = value;
            return oldValue;
         }
      }

      this.modCount.incrementAndGet();
      this.addEntry(key, value, i);
      return null;
   }

   private void putForCreate(int key, V value) {
      int i = indexFor(key, this.table.length);

      for(Entry<V> e = this.table[i]; e != null; e = e.next) {
         if (key == e.key) {
            e.value = value;
            return;
         }
      }

      this.createEntry(key, value, i);
   }

   void putAllForCreate(IntHashMap<V> m) {
      for(Entry<V> entry : m.entrySet()) {
         this.putForCreate(entry.getKey(), entry.getValue());
      }

   }

   void resize(int newCapacity) {
      Entry<V>[] oldTable = this.table;
      int oldCapacity = oldTable.length;
      if (oldCapacity == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         Entry<V>[] newTable = new Entry[newCapacity];
         this.transfer(newTable);
         this.table = newTable;
         this.threshold = (int)((float)newCapacity * this.loadFactor);
      }
   }

   void transfer(Entry<V>[] newTable) {
      Entry<V>[] src = this.table;
      int newCapacity = newTable.length;

      for(int j = 0; j < src.length; ++j) {
         Entry<V> e = src[j];
         if (e != null) {
            src[j] = null;

            while(true) {
               Entry<V> next = e.next;
               int i = indexFor(e.key, newCapacity);
               e.next = newTable[i];
               newTable[i] = e;
               e = next;
               if (next == null) {
                  break;
               }
            }
         }
      }

   }

   public void putAll(IntHashMap<V> m) {
      int numKeysToBeAdded = m.size();
      if (numKeysToBeAdded != 0) {
         if (numKeysToBeAdded > this.threshold) {
            int targetCapacity = (int)((float)numKeysToBeAdded / this.loadFactor + 1.0F);
            if (targetCapacity > 1073741824) {
               targetCapacity = 1073741824;
            }

            int newCapacity;
            for(newCapacity = this.table.length; newCapacity < targetCapacity; newCapacity <<= 1) {
            }

            if (newCapacity > this.table.length) {
               this.resize(newCapacity);
            }
         }

         for(Entry<V> entry : m.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
         }

      }
   }

   public V remove(int key) {
      Entry<V> e = this.removeEntryForKey(key);
      return (V)(e == null ? null : e.value);
   }

   Entry<V> removeEntryForKey(int key) {
      int i = indexFor(key, this.table.length);
      Entry<V> prev = this.table[i];

      Entry<V> e;
      Entry<V> next;
      for(e = prev; e != null; e = next) {
         next = e.next;
         if (key == e.key) {
            this.modCount.incrementAndGet();
            --this.size;
            if (prev == e) {
               this.table[i] = next;
            } else {
               prev.next = next;
            }

            return e;
         }

         prev = e;
      }

      return e;
   }

   Entry<V> removeMapping(Object o) {
      if (!(o instanceof Entry)) {
         return null;
      } else {
         Entry<V> entry = (Entry)o;
         int key = entry.getKey();
         int i = indexFor(key, this.table.length);
         Entry<V> prev = this.table[i];

         Entry<V> e;
         Entry<V> next;
         for(e = prev; e != null; e = next) {
            next = e.next;
            if (e.key == key && e.equals(entry)) {
               this.modCount.incrementAndGet();
               --this.size;
               if (prev == e) {
                  this.table[i] = next;
               } else {
                  prev.next = next;
               }

               return e;
            }

            prev = e;
         }

         return e;
      }
   }

   public void clear() {
      this.modCount.incrementAndGet();
      Entry<V>[] tab = this.table;

      for(int i = 0; i < tab.length; ++i) {
         tab[i] = null;
      }

      this.size = 0;
   }

   public boolean containsValue(Object value) {
      if (value == null) {
         return this.containsNullValue();
      } else {
         for(Entry<V> entry : this.table) {
            for(Entry<V> e = entry; e != null; e = e.next) {
               if (value.equals(e.value)) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   private boolean containsNullValue() {
      Entry<V>[] tab = this.table;

      for(Entry<V> tabEntry : tab) {
         for(Entry<V> e = tabEntry; e != null; e = e.next) {
            if (e.value == null) {
               return true;
            }
         }
      }

      return false;
   }

   public Object clone() throws CloneNotSupportedException {
      IntHashMap<V> result = null;

      try {
         result = (IntHashMap)super.clone();
         result.table = new Entry[this.table.length];
         result.entrySet = null;
         result.modCount.set(0);
         result.size = 0;
         result.init();
         result.putAllForCreate(this);
      } catch (CloneNotSupportedException var3) {
      }

      return result;
   }

   void addEntry(int key, V value, int bucketIndex) {
      this.table[bucketIndex] = new Entry(key, value, this.table[bucketIndex]);
      if (this.size++ >= this.threshold) {
         this.resize(2 * this.table.length);
      }

   }

   void createEntry(int key, V value, int bucketIndex) {
      this.table[bucketIndex] = new Entry(key, value, this.table[bucketIndex]);
      ++this.size;
   }

   Iterator<Integer> newKeyIterator() {
      return new KeyIterator();
   }

   Iterator<V> newValueIterator() {
      return new ValueIterator();
   }

   Iterator<Entry<V>> newEntryIterator() {
      return new EntryIterator();
   }

   public Set<Integer> keySet() {
      Set<Integer> ks = this.keySet;
      return ks != null ? ks : (this.keySet = new KeySet());
   }

   public Collection<V> values() {
      Collection<V> vs = this.values;
      return vs != null ? vs : (this.values = new Values());
   }

   public Set<Entry<V>> entrySet() {
      Set<Entry<V>> es = this.entrySet;
      return es != null ? es : (this.entrySet = new EntrySet());
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      s.writeInt(this.table.length);
      s.writeInt(this.size);

      for(Entry<V> entry : this.entrySet()) {
         s.writeInt(entry.getKey());
         s.writeObject(entry.getValue());
      }

   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      this.modCount = new AtomicInteger(0);
      s.defaultReadObject();
      int numBuckets = s.readInt();
      this.table = new Entry[numBuckets];
      this.init();
      int size = s.readInt();

      for(int i = 0; i < size; ++i) {
         int key = s.readInt();
         V value = (V)s.readObject();
         this.putForCreate(key, value);
      }

   }

   int capacity() {
      return this.table.length;
   }

   float loadFactor() {
      return this.loadFactor;
   }

   public static class Entry<V> {
      final int key;
      V value;
      Entry<V> next;

      Entry(int k, V v, Entry<V> n) {
         this.value = v;
         this.next = n;
         this.key = k;
      }

      public int getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V newValue) {
         V oldValue = this.value;
         this.value = newValue;
         return oldValue;
      }

      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<V> e = (Entry)o;
            int k1 = this.getKey();
            int k2 = e.getKey();
            if (k1 == k2) {
               Object v1 = this.getValue();
               Object v2 = e.getValue();
               if (v1 == v2 || v1 != null && v1.equals(v2)) {
                  return true;
               }
            }

            return false;
         }
      }

      public int hashCode() {
         return this.key ^ (this.value == null ? 0 : this.value.hashCode());
      }

      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }

   private abstract class HashIterator<H> implements Iterator<H> {
      Entry<V> next;
      int expectedModCount;
      int index;
      Entry<V> current;

      HashIterator() {
         this.expectedModCount = IntHashMap.this.modCount.get();
         Entry<V>[] t = IntHashMap.this.table;
         int i = t.length;
         Entry<V> n = null;
         if (IntHashMap.this.size != 0) {
            while(i > 0) {
               --i;
               if ((n = t[i]) != null) {
                  break;
               }
            }
         }

         this.next = n;
         this.index = i;
      }

      public boolean hasNext() {
         return this.next != null;
      }

      Entry<V> nextEntry() {
         if (!IntHashMap.this.modCount.compareAndSet(this.expectedModCount, this.expectedModCount)) {
            throw new ConcurrentModificationException();
         } else {
            Entry<V> e = this.next;
            if (e == null) {
               throw new NoSuchElementException();
            } else {
               Entry<V> n = e.next;
               Entry<V>[] t = IntHashMap.this.table;

               int i;
               for(i = this.index; n == null && i > 0; n = t[i]) {
                  --i;
               }

               this.index = i;
               this.next = n;
               return this.current = e;
            }
         }
      }

      public void remove() {
         if (this.current == null) {
            throw new IllegalStateException();
         } else if (!IntHashMap.this.modCount.compareAndSet(this.expectedModCount, this.expectedModCount)) {
            throw new ConcurrentModificationException();
         } else {
            int k = this.current.key;
            this.current = null;
            IntHashMap.this.removeEntryForKey(k);
            this.expectedModCount = IntHashMap.this.modCount.get();
         }
      }
   }

   private class ValueIterator extends IntHashMap<V>.HashIterator<V> {
      private ValueIterator() {
      }

      public V next() {
         return this.nextEntry().value;
      }
   }

   private class KeyIterator extends IntHashMap<V>.HashIterator<Integer> {
      private KeyIterator() {
      }

      public Integer next() {
         return this.nextEntry().getKey();
      }
   }

   private class EntryIterator extends IntHashMap<V>.HashIterator<Entry<V>> {
      private EntryIterator() {
      }

      public Entry<V> next() {
         return this.nextEntry();
      }
   }

   private class KeySet extends AbstractSet<Integer> {
      private KeySet() {
      }

      public Iterator<Integer> iterator() {
         return IntHashMap.this.newKeyIterator();
      }

      public int size() {
         return IntHashMap.this.size;
      }

      public boolean contains(Object o) {
         return o instanceof Number ? IntHashMap.this.containsKey(((Number)o).intValue()) : false;
      }

      public boolean remove(Object o) {
         if (o instanceof Number) {
            return IntHashMap.this.removeEntryForKey(((Number)o).intValue()) != null;
         } else {
            return false;
         }
      }

      public void clear() {
         IntHashMap.this.clear();
      }
   }

   private class Values extends AbstractCollection<V> {
      private Values() {
      }

      public Iterator<V> iterator() {
         return IntHashMap.this.newValueIterator();
      }

      public int size() {
         return IntHashMap.this.size;
      }

      public boolean contains(Object o) {
         return IntHashMap.this.containsValue(o);
      }

      public void clear() {
         IntHashMap.this.clear();
      }
   }

   private class EntrySet extends AbstractSet<Entry<V>> {
      private EntrySet() {
      }

      public Iterator<Entry<V>> iterator() {
         return IntHashMap.this.newEntryIterator();
      }

      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<V> e = (Entry)o;
            Entry<V> candidate = IntHashMap.this.getEntry(e.getKey());
            return candidate != null && candidate.equals(e);
         }
      }

      public boolean remove(Object o) {
         return IntHashMap.this.removeMapping(o) != null;
      }

      public int size() {
         return IntHashMap.this.size;
      }

      public void clear() {
         IntHashMap.this.clear();
      }
   }
}
