package org.apache.openejb.rest;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.ws.rs.core.MultivaluedMap;

public class MultivaluedMapWithCaseInsensitiveKeySet<V> implements MultivaluedMap<String, V> {
   private final MultivaluedMap<String, V> delegate;

   public MultivaluedMapWithCaseInsensitiveKeySet(MultivaluedMap<String, V> map) {
      this.delegate = map;
   }

   public void add(String key, V value) {
      this.delegate.add(key, value);
   }

   public V getFirst(String key) {
      return (V)this.delegate.getFirst(this.realKey(key));
   }

   public void addAll(String key, V... newValues) {
      this.delegate.addAll(key, newValues);
   }

   public void addAll(String key, List<V> valueList) {
      this.delegate.addAll(key, valueList);
   }

   public void addFirst(String key, V value) {
      this.delegate.addFirst(key, value);
   }

   public boolean equalsIgnoreValueOrder(MultivaluedMap<String, V> otherMap) {
      return this.delegate.equalsIgnoreValueOrder(otherMap);
   }

   public void putSingle(String key, V value) {
      this.delegate.putSingle(key, value);
   }

   public int size() {
      return this.delegate.size();
   }

   public boolean isEmpty() {
      return this.delegate.isEmpty();
   }

   public boolean containsKey(Object key) {
      return getInsensitiveKeySet(this.delegate.keySet()).contains(key.toString());
   }

   public boolean containsValue(Object value) {
      return this.delegate.containsValue(value);
   }

   public List<V> get(Object key) {
      return (List)this.delegate.get(this.realKey(key));
   }

   public List<V> put(String key, List<V> value) {
      return (List)this.delegate.put(key, value);
   }

   public List<V> remove(Object key) {
      return (List)this.delegate.remove(this.realKey(key));
   }

   public void putAll(Map<? extends String, ? extends List<V>> m) {
      this.delegate.putAll(m);
   }

   public void clear() {
      this.delegate.clear();
   }

   public Set<String> keySet() {
      return getInsensitiveKeySet(this.delegate.keySet());
   }

   public Collection<List<V>> values() {
      return this.delegate.values();
   }

   public Set<Map.Entry<String, List<V>>> entrySet() {
      return this.delegate.entrySet();
   }

   private static Set<String> getInsensitiveKeySet(Set<String> values) {
      Set<String> set = new TreeSet(String.CASE_INSENSITIVE_ORDER);
      set.addAll(values);
      return set;
   }

   private static Object lowerCase(Object key) {
      return key instanceof String ? ((String)key).toLowerCase() : key;
   }

   private String realKey(Object key) {
      for(Map.Entry<String, ?> entry : this.delegate.entrySet()) {
         if (((String)entry.getKey()).toLowerCase().equals(lowerCase(key))) {
            return (String)entry.getKey();
         }
      }

      return null;
   }
}
