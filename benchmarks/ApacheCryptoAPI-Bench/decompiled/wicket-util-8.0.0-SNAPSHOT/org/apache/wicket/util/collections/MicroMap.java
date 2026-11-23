package org.apache.wicket.util.collections;

import java.io.Serializable;
import java.util.AbstractList;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class MicroMap<K, V> implements Map<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   public static final int MAX_ENTRIES = 1;
   private K key;
   private V value;

   public MicroMap() {
   }

   public MicroMap(K key, V value) {
      this.put(key, value);
   }

   public boolean isFull() {
      return this.size() == 1;
   }

   public int size() {
      return this.key != null ? 1 : 0;
   }

   public boolean isEmpty() {
      return this.size() == 0;
   }

   public boolean containsKey(Object key) {
      return key.equals(this.key);
   }

   public boolean containsValue(Object value) {
      return value.equals(this.value);
   }

   public V get(Object key) {
      return (V)(key.equals(this.key) ? this.value : null);
   }

   public V put(K key, V value) {
      if (key.equals(this.key)) {
         V oldValue = this.value;
         this.value = value;
         return oldValue;
      } else if (this.size() < 1) {
         this.key = key;
         this.value = value;
         return null;
      } else {
         throw new IllegalStateException("Map full");
      }
   }

   public V remove(Object key) {
      if (key.equals(this.key)) {
         V oldValue = this.value;
         this.key = null;
         this.value = null;
         return oldValue;
      } else {
         return null;
      }
   }

   public void putAll(Map<? extends K, ? extends V> map) {
      if (map.size() <= 1) {
         Map.Entry<? extends K, ? extends V> e = (Map.Entry)map.entrySet().iterator().next();
         this.put(e.getKey(), e.getValue());
      } else {
         throw new IllegalStateException("Map full.  Cannot add " + map.size() + " entries");
      }
   }

   public void clear() {
      this.key = null;
      this.value = null;
   }

   public Set<K> keySet() {
      return new AbstractSet<K>() {
         public Iterator<K> iterator() {
            return new Iterator<K>() {
               int index;

               public boolean hasNext() {
                  return this.index < MicroMap.this.size();
               }

               public K next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     ++this.index;
                     return MicroMap.this.key;
                  }
               }

               public void remove() {
                  MicroMap.this.clear();
               }
            };
         }

         public int size() {
            return MicroMap.this.size();
         }
      };
   }

   public Collection<V> values() {
      return new AbstractList<V>() {
         public V get(int index) {
            if (index > this.size() - 1) {
               throw new IndexOutOfBoundsException();
            } else {
               return MicroMap.this.value;
            }
         }

         public int size() {
            return MicroMap.this.size();
         }
      };
   }

   public Set<Map.Entry<K, V>> entrySet() {
      return new AbstractSet<Map.Entry<K, V>>() {
         public Iterator<Map.Entry<K, V>> iterator() {
            return new Iterator<Map.Entry<K, V>>() {
               int index = 0;

               public boolean hasNext() {
                  return this.index < MicroMap.this.size();
               }

               public Map.Entry<K, V> next() {
                  if (!this.hasNext()) {
                     throw new NoSuchElementException();
                  } else {
                     ++this.index;
                     return new Map.Entry<K, V>() {
                        public K getKey() {
                           return MicroMap.this.key;
                        }

                        public V getValue() {
                           return MicroMap.this.value;
                        }

                        public V setValue(V value) {
                           V oldValue = MicroMap.this.value;
                           MicroMap.this.value = value;
                           return oldValue;
                        }
                     };
                  }
               }

               public void remove() {
                  clear();
               }
            };
         }

         public int size() {
            return MicroMap.this.size();
         }
      };
   }
}
