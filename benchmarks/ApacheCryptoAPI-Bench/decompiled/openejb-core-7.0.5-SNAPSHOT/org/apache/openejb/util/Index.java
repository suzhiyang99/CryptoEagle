package org.apache.openejb.util;

import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class Index<K, V> extends AbstractMap<K, V> {
   private final IndexEntry<K, V>[] entries;
   private final LinkedHashMap<K, Integer> keyIndicies;
   private final Index<K, V>.IndexEntrySet entrySet;
   private Index<K, V>.IndexValueList<V> indexValueList;

   public Index(Map<K, V> map) {
      this.entries = new IndexEntry[map.size()];
      this.keyIndicies = new LinkedHashMap(map.size());
      int i = 0;

      for(Map.Entry<K, V> entry : map.entrySet()) {
         this.entries[i] = new IndexEntry(entry);
         this.keyIndicies.put(entry.getKey(), new Integer(i));
         ++i;
      }

      this.entrySet = new IndexEntrySet();
   }

   public Index(List<K> keys) {
      this.entries = new IndexEntry[keys.size()];
      this.keyIndicies = new LinkedHashMap(keys.size());

      for(int i = 0; i < keys.size(); ++i) {
         K key = (K)keys.get(i);
         this.entries[i] = new IndexEntry(key, (Object)null);
         this.keyIndicies.put(key, new Integer(i));
      }

      this.entrySet = new IndexEntrySet();
   }

   public Index(K[] keys) {
      this.entries = new IndexEntry[keys.length];
      this.keyIndicies = new LinkedHashMap(keys.length);

      for(int i = 0; i < keys.length; ++i) {
         K key = (K)keys[i];
         this.entries[i] = new IndexEntry(key, (Object)null);
         this.keyIndicies.put(key, new Integer(i));
      }

      this.entrySet = new IndexEntrySet();
   }

   public List<V> values() {
      if (this.indexValueList == null) {
         this.indexValueList = new IndexValueList<V>();
      }

      return this.indexValueList;
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return this.entrySet;
   }

   public K getKey(int index) {
      if (index >= 0 && index < this.entries.length) {
         return (K)this.entries[index].getKey();
      } else {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      }
   }

   public V get(int index) {
      if (index >= 0 && index < this.entries.length) {
         return (V)this.entries[index].getValue();
      } else {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      }
   }

   public V set(int index, V value) {
      if (index >= 0 && index < this.entries.length) {
         IndexEntry<K, V> entry = this.entries[index];
         V oldValue = entry.getValue();
         entry.setValue(value);
         return oldValue;
      } else {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      }
   }

   public V put(K key, V value) {
      int i = this.indexOf(key);
      if (i < 0) {
         throw new IllegalArgumentException("Index does not contain this key and new entries cannot be added: " + key);
      } else {
         IndexEntry<K, V> entry = this.entries[i];
         V oldValue = entry.getValue();
         entry.setValue(value);
         return oldValue;
      }
   }

   public boolean containsKey(Object key) {
      return this.keyIndicies.containsKey(key);
   }

   public int indexOf(K key) {
      Integer index = (Integer)this.keyIndicies.get(key);
      return index == null ? -1 : index;
   }

   public V get(Object key) {
      int i = this.indexOf(key);
      if (i < 0) {
         return null;
      } else {
         IndexEntry<K, V> entryMetadata = this.entries[i];
         V value = entryMetadata.getValue();
         return value;
      }
   }

   public Iterator<V> iterator() {
      return new IndexIterator<V>();
   }

   public ListIterator<V> listIterator() {
      return new IndexListIterator<V>(0);
   }

   public ListIterator<V> listIterator(int index) {
      if (index >= 0 && index < this.entries.length) {
         return new IndexListIterator<V>(index);
      } else {
         throw new IndexOutOfBoundsException(String.valueOf(index));
      }
   }

   public Object[] toArray() {
      return this.toArray(new Object[this.entries.length]);
   }

   public Object[] toArray(Object[] values) {
      if (values.length < this.entries.length) {
         values = Array.newInstance(values.getClass().getComponentType(), this.entries.length);
      }

      for(int i = 0; i < this.entries.length; ++i) {
         IndexEntry indexEntry = this.entries[i];
         values[i] = indexEntry.getValue();
      }

      return values;
   }

   private static final class IndexEntry<K, V> implements Map.Entry<K, V> {
      private final K key;
      private V value;

      private IndexEntry(K key, V value) {
         this.key = key;
         this.value = value;
      }

      private IndexEntry(Map.Entry<K, V> entry) {
         this.key = (K)entry.getKey();
         this.value = (V)entry.getValue();
      }

      public K getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      public V setValue(V value) {
         V oldValue = this.value;
         this.value = value;
         return oldValue;
      }
   }

   private class IndexEntrySet extends AbstractSet {
      private IndexEntrySet() {
      }

      public Iterator iterator() {
         return new Iterator() {
            private int index;

            public boolean hasNext() {
               return this.index < Index.this.entries.length;
            }

            public Object next() {
               return Index.this.entries[this.index++];
            }

            public void remove() {
               throw new UnsupportedOperationException("Index entries cannot be removed");
            }
         };
      }

      public int size() {
         return Index.this.entries.length;
      }
   }

   private class IndexValueList<E> extends AbstractList<E> {
      private IndexValueList() {
      }

      public E get(int index) {
         return (E)Index.this.get(index);
      }

      public E set(int index, E element) {
         return (E)Index.this.set(index, element);
      }

      public int size() {
         return Index.this.size();
      }
   }

   private class IndexIterator<E> implements Iterator<E> {
      protected int index;

      private IndexIterator() {
      }

      public boolean hasNext() {
         return this.index < Index.this.entries.length;
      }

      public E next() {
         IndexEntry<K, V> entryMetadata = Index.this.entries[this.index++];
         return (E)entryMetadata.getValue();
      }

      public void remove() {
         throw new UnsupportedOperationException("Index entries cannot be removed");
      }
   }

   private class IndexListIterator<E> extends Index<K, V>.IndexIterator<E> implements ListIterator<E> {
      public IndexListIterator(int index) {
         this.index = index;
      }

      public int nextIndex() {
         return this.index;
      }

      public boolean hasPrevious() {
         return this.index > 0;
      }

      public E previous() {
         return (E)Index.this.entries[--this.index].getValue();
      }

      public int previousIndex() {
         return this.index - 1;
      }

      public void set(E o) {
         Index.this.entries[this.index].setValue(o);
      }

      public void add(E o) {
         throw new UnsupportedOperationException("Entries cannot be added to a Index");
      }
   }

   public interface ListSet extends List, Set {
   }
}
