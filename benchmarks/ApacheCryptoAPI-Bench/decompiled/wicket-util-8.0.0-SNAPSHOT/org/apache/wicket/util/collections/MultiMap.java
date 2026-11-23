package org.apache.wicket.util.collections;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiMap<K, V> extends HashMap<K, List<V>> {
   private static final long serialVersionUID = 1L;

   public MultiMap() {
   }

   public MultiMap(int initialCapacity, float loadFactor) {
      super(initialCapacity, loadFactor);
   }

   public MultiMap(int initialCapacity) {
      super(initialCapacity);
   }

   public MultiMap(Map<? extends K, ? extends List<V>> m) {
      super(m);
   }

   public void addValue(K key, V value) {
      List<V> list = (List)this.get(key);
      if (list == null) {
         list = new ArrayList(1);
         this.put(key, list);
      }

      list.add(value);
   }

   public void removeValue(K key, V value) {
      List<V> list = (List)this.get(key);
      if (list != null) {
         list.remove(value);
      }

   }

   public void replaceValues(K key, V value) {
      List<V> list = (List)this.get(key);
      if (list != null) {
         list.clear();
         list.add(value);
      } else {
         this.addValue(key, value);
      }

   }

   public V getFirstValue(K key) {
      List<V> list = (List)this.get(key);
      return (V)(list != null && !list.isEmpty() ? list.get(0) : null);
   }
}
