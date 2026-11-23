package org.apache.openejb.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.apache.openejb.BeanContext;
import org.apache.openejb.jee.KeyedCollection;
import org.apache.openejb.jee.ManagedBean;
import org.apache.openejb.jee.PersistenceContextRef;
import org.apache.openejb.jee.PersistenceContextType;

public class CompManagedBean extends ManagedBean {
   public CompManagedBean(String name, Class<BeanContext.Comp> compClass) {
      super(name, compClass);
   }

   public String getJndiConsumerName() {
      return this.ejbName.substring(0, this.ejbName.lastIndexOf(".Comp"));
   }

   public Collection<PersistenceContextRef> getPersistenceContextRef() {
      if (this.persistenceContextRef == null) {
         this.persistenceContextRef = new NoExtendedKeyedCollection();
      }

      return this.persistenceContextRef;
   }

   public Map<String, PersistenceContextRef> getPersistenceContextRefMap() {
      if (this.persistenceContextRef == null) {
         this.persistenceContextRef = new NoExtendedKeyedCollection();
      }

      return this.persistenceContextRef.toMap();
   }

   public boolean isHidden() {
      return true;
   }

   private static class NoExtendedKeyedCollection extends KeyedCollection<String, PersistenceContextRef> {
      private NoExtendedKeyedCollection() {
      }

      public boolean add(PersistenceContextRef value) {
         return !PersistenceContextType.EXTENDED.equals(value.getPersistenceContextType()) && !super.contains(value) ? super.add(value) : false;
      }

      public Map<String, PersistenceContextRef> toMap() {
         return new NoExtendedMap(super.toMap());
      }

      private static class NoExtendedMap implements Map<String, PersistenceContextRef> {
         private final Map<String, PersistenceContextRef> delegate;

         public NoExtendedMap(Map<String, PersistenceContextRef> map) {
            this.delegate = map;
         }

         public int size() {
            return this.delegate.size();
         }

         public boolean isEmpty() {
            return this.delegate.isEmpty();
         }

         public boolean containsKey(Object key) {
            return this.delegate.containsKey(key);
         }

         public boolean containsValue(Object value) {
            return this.delegate.containsValue(value);
         }

         public PersistenceContextRef get(Object key) {
            return (PersistenceContextRef)this.delegate.get(key);
         }

         public PersistenceContextRef put(String key, PersistenceContextRef value) {
            return !PersistenceContextType.EXTENDED.equals(value.getPersistenceContextType()) && !this.delegate.containsValue(key) ? (PersistenceContextRef)this.delegate.put(key, value) : null;
         }

         public PersistenceContextRef remove(Object key) {
            return (PersistenceContextRef)this.delegate.remove(key);
         }

         public void putAll(Map<? extends String, ? extends PersistenceContextRef> m) {
            for(Map.Entry<? extends String, ? extends PersistenceContextRef> entry : m.entrySet()) {
               this.put((String)entry.getKey(), (PersistenceContextRef)entry.getValue());
            }

         }

         public void clear() {
            this.delegate.clear();
         }

         public Set<String> keySet() {
            return this.delegate.keySet();
         }

         public Collection<PersistenceContextRef> values() {
            return this.delegate.values();
         }

         public Set<Map.Entry<String, PersistenceContextRef>> entrySet() {
            return this.delegate.entrySet();
         }
      }
   }
}
