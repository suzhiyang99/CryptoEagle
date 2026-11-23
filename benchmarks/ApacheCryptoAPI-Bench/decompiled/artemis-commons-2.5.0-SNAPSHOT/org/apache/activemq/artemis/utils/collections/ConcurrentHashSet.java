package org.apache.activemq.artemis.utils.collections;

import java.util.AbstractSet;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements ConcurrentSet<E> {
   private final ConcurrentMap<E, Object> theMap = new ConcurrentHashMap();
   private static final Object dummy = new Object();

   public int size() {
      return this.theMap.size();
   }

   public Iterator<E> iterator() {
      return this.theMap.keySet().iterator();
   }

   public boolean isEmpty() {
      return this.theMap.isEmpty();
   }

   public boolean add(E o) {
      return this.theMap.put(o, dummy) == null;
   }

   public boolean contains(Object o) {
      return this.theMap.containsKey(o);
   }

   public void clear() {
      this.theMap.clear();
   }

   public boolean remove(Object o) {
      return this.theMap.remove(o) == dummy;
   }

   public boolean addIfAbsent(E o) {
      Object obj = this.theMap.putIfAbsent(o, dummy);
      return obj == null;
   }
}
