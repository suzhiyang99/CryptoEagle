package org.apache.wicket.util.collections;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public class MiniMap<K, V> implements Map<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final K[] keys;
   private final V[] values;
   private int size;
   private int lastSearchIndex;

   public MiniMap(int maxEntries) {
      this.keys = (K[])(new Object[maxEntries]);
      this.values = (V[])(new Object[maxEntries]);
   }

   public MiniMap(Map<? extends K, ? extends V> map, int maxEntries) {
      this(maxEntries);
      this.putAll(map);
   }

   public boolean isFull() {
      return this.size == this.keys.length;
   }

   public int size() {
      return this.size;
   }

   public boolean isEmpty() {
      return this.size == 0;
   }

   public boolean containsKey(Object key) {
      return this.findKey(0, key) != -1;
   }

   public boolean containsValue(Object value) {
      return this.findValue(0, value) != -1;
   }

   public V get(Object key) {
      int index = this.findKey(key);
      return (V)(index != -1 ? this.values[index] : null);
   }

   public V put(K key, V value) {
      int index = this.findKey(key);
      if (index != -1) {
         V oldValue = (V)this.values[index];
         this.values[index] = value;
         return oldValue;
      } else if (this.size < this.keys.length) {
         int nullIndex = this.nextNullKey(this.lastSearchIndex);
         this.lastSearchIndex = this.nextIndex(nullIndex);
         this.keys[nullIndex] = key;
         this.values[nullIndex] = value;
         ++this.size;
         return null;
      } else {
         throw new IllegalStateException("Map full");
      }
   }

   public V remove(Object key) {
      int index = this.findKey(key);
      if (index != -1) {
         V oldValue = (V)this.values[index];
         this.keys[index] = null;
         this.values[index] = null;
         --this.size;
         return oldValue;
      } else {
         return null;
      }
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      for(Map.Entry<? extends K, ? extends V> entry : map.entrySet()) {
         this.put(entry.getKey(), entry.getValue());
      }

   }

   public void clear() {
      for(int i = 0; i < this.keys.length; ++i) {
         this.keys[i] = null;
         this.values[i] = null;
      }

      this.size = 0;
   }

   public Set<K> keySet() {
      return new AbstractSet<K>() {
         public Iterator<K> iterator() {
            return new Iterator<K>() {
               int i = -1;

               public boolean hasNext() {
                  return this.i < MiniMap.this.size - 1;
               }

               public K next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     this.i = MiniMap.this.nextKey(MiniMap.this.nextIndex(this.i));
                     return (K)MiniMap.this.keys[this.i];
                  }
               }

               public void remove() {
                  MiniMap.this.keys[this.i] = null;
                  MiniMap.this.values[this.i] = null;
                  MiniMap.this.size--;
               }
            };
         }

         public int size() {
            return MiniMap.this.size;
         }
      };
   }

   public Collection<V> values() {
      return new AbstractList<V>() {
         public V get(int index) {
            if (index > MiniMap.this.size - 1) {
               throw new IndexOutOfBoundsException();
            } else {
               int keyIndex = MiniMap.this.nextKey(0);

               for(int i = 0; i < index; ++i) {
                  keyIndex = MiniMap.this.nextKey(keyIndex + 1);
               }

               return (V)MiniMap.this.values[keyIndex];
            }
         }

         public int size() {
            return MiniMap.this.size;
         }
      };
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return new AbstractSet<Map.Entry<K, V>>() {
         public Iterator<Map.Entry<K, V>> iterator() {
            return new Iterator<Map.Entry<K, V>>() {
               int keyIndex = -1;
               int index = 0;

               public boolean hasNext() {
                  return this.index < MiniMap.this.size;
               }

               public Map.Entry<K, V> next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     this.keyIndex = MiniMap.this.nextKey(MiniMap.this.nextIndex(this.keyIndex));
                     ++this.index;
                     return new Map.Entry<K, V>() {
                        public K getKey() {
                           return (K)MiniMap.this.keys[keyIndex];
                        }

                        public V getValue() {
                           return (V)MiniMap.this.values[keyIndex];
                        }

                        public V setValue(V value) {
                           V oldValue = (V)MiniMap.this.values[keyIndex];
                           MiniMap.this.values[keyIndex] = value;
                           return oldValue;
                        }
                     };
                  }
               }

               public void remove() {
                  MiniMap.this.keys[this.keyIndex] = null;
                  MiniMap.this.values[this.keyIndex] = null;
               }
            };
         }

         public int size() {
            return MiniMap.this.size;
         }
      };
   }

   private int nextIndex(int index) {
      return (index + 1) % this.keys.length;
   }

   private int nextKey(int start) {
      int i = start;

      while(this.keys[i] == null) {
         i = this.nextIndex(i);
         if (i == start) {
            return -1;
         }
      }

      return i;
   }

   private int nextNullKey(int start) {
      int i = start;

      while(this.keys[i] != null) {
         i = this.nextIndex(i);
         if (i == start) {
            return -1;
         }
      }

      return i;
   }

   private int findKey(Object key) {
      if (this.size > 0) {
         int index = this.findKey(this.lastSearchIndex, key);
         if (index != -1) {
            this.lastSearchIndex = this.nextIndex(index);
            return index;
         }
      }

      return -1;
   }

   private int findKey(int start, Object key) {
      int i = start;

      while(!key.equals(this.keys[i])) {
         i = this.nextIndex(i);
         if (i == start) {
            return -1;
         }
      }

      return i;
   }

   private int findValue(int start, Object value) {
      int i = start;

      while(!value.equals(this.values[i])) {
         i = this.nextIndex(i);
         if (i == start) {
            return -1;
         }
      }

      return i;
   }
}
