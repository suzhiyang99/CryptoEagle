package org.apache.wicket.util.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentHashSet<E> extends AbstractSet<E> implements Set<E>, Cloneable, Serializable {
   private static final long serialVersionUID = 1L;
   private transient ConcurrentHashMap<E, Object> map;
   private static final Object PRESENT = new Object();

   public ConcurrentHashSet() {
      this.map = new ConcurrentHashMap();
   }

   public ConcurrentHashSet(Collection<? extends E> c) {
      this.map = new ConcurrentHashMap(Math.max((int)((float)c.size() / 0.75F) + 1, 16));
      this.addAll(c);
   }

   public ConcurrentHashSet(int initialCapacity, float loadFactor) {
      this.map = new ConcurrentHashMap(initialCapacity, loadFactor, 16);
   }

   public ConcurrentHashSet(int initialCapacity) {
      this.map = new ConcurrentHashMap(initialCapacity);
   }

   public Iterator<E> iterator() {
      return this.map.keySet().iterator();
   }

   public int size() {
      return this.map.size();
   }

   public boolean isEmpty() {
      return this.map.isEmpty();
   }

   public boolean contains(Object o) {
      return this.map.containsKey(o);
   }

   public boolean add(E o) {
      return this.map.put(o, PRESENT) == null;
   }

   public boolean remove(Object o) {
      return this.map.remove(o) == PRESENT;
   }

   public void clear() {
      this.map.clear();
   }

   public Object clone() throws CloneNotSupportedException {
      try {
         ConcurrentHashSet<E> newSet = (ConcurrentHashSet)super.clone();
         newSet.map.putAll(this.map);
         return newSet;
      } catch (CloneNotSupportedException var2) {
         throw new InternalError();
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();
      s.writeInt(this.map.size());

      for(E key : this.map.keySet()) {
         s.writeObject(key);
      }

   }

   private void readObject(ObjectInputStream inputStream) throws ClassNotFoundException, IOException {
      inputStream.defaultReadObject();
      this.map = new ConcurrentHashMap();
      int size = inputStream.readInt();

      for(int i = 0; i < size; ++i) {
         E e = (E)inputStream.readObject();
         this.map.put(e, PRESENT);
      }

   }
}
