package org.apache.openejb.core.cmp.cmp2;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;
import javax.ejb.EntityBean;
import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.BeanContext;
import org.apache.openejb.loader.SystemInstance;

public class CmrSet<Bean extends EntityBean, Proxy extends EJBLocalObject> extends AbstractSet {
   private final EntityBean source;
   private final String sourceProperty;
   private final BeanContext relatedInfo;
   private final String relatedProperty;
   private final Class relatedLocal;
   private boolean mutable = true;
   private Collection<Bean> relatedBeans;

   public CmrSet(EntityBean source, String sourceProperty, BeanContext relatedInfo, String relatedProperty, Collection<Bean> relatedBeans) {
      this.source = source;
      this.sourceProperty = sourceProperty;
      this.relatedInfo = relatedInfo;
      this.relatedProperty = relatedProperty;
      this.relatedBeans = relatedBeans;
      this.relatedLocal = relatedInfo.getLocalInterface();
      TransactionSynchronizationRegistry transactionRegistry = (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class);

      try {
         transactionRegistry.registerInterposedSynchronization(new Synchronization() {
            public void beforeCompletion() {
            }

            public void afterCompletion(int i) {
               CmrSet.this.mutable = false;
            }
         });
      } catch (IllegalStateException var8) {
         this.mutable = false;
      }

   }

   protected void entityDeleted() {
      this.relatedBeans = null;
      this.mutable = false;
   }

   public boolean isEmpty() {
      return this.getRelatedBeans(false, false).isEmpty();
   }

   public int size() {
      return this.getRelatedBeans(false, false).size();
   }

   public boolean contains(Object o) {
      if (!this.relatedLocal.isInstance(o)) {
         return false;
      } else {
         Bean entity = this.getEntityBean((EJBLocalObject)o);
         return entity != null && this.getRelatedBeans(false, false).contains(entity);
      }
   }

   public boolean addAll(Collection c) {
      Set<Bean> entityBeans = getEntityBeans(c, this.relatedLocal);
      boolean changed = false;

      for(Bean bean : entityBeans) {
         changed = this.add(bean) || changed;
      }

      return changed;
   }

   public boolean add(Object proxy) {
      if (!this.relatedLocal.isInstance(proxy)) {
         throw new IllegalArgumentException("Object is not an instance of " + this.relatedLocal.getName() + ": " + (proxy == null ? "null" : proxy.getClass().getName()));
      } else {
         Bean newEntity = this.getEntityBean((EJBLocalObject)proxy);
         if (newEntity == null) {
            throw new IllegalArgumentException("Ejb has been deleted");
         } else {
            return this.add(newEntity);
         }
      }
   }

   private boolean add(Bean newEntity) {
      boolean changed = this.getRelatedBeans(true, true).add(newEntity);
      if (changed && this.relatedProperty != null) {
         if (this.source == null) {
            throw new IllegalStateException("source is null");
         }

         Object oldBackRef = this.toCmp2Entity(newEntity).OpenEJB_addCmr(this.relatedProperty, this.source);
         if (oldBackRef != null) {
            this.toCmp2Entity(oldBackRef).OpenEJB_removeCmr(this.sourceProperty, newEntity);
         }
      }

      return changed;
   }

   public boolean remove(Object o) {
      if (!this.relatedLocal.isInstance(o)) {
         return false;
      } else {
         Bean entity = this.getEntityBean((EJBLocalObject)o);
         boolean changed = entity != null && this.getRelatedBeans(false, true).remove(entity);
         if (changed && this.relatedProperty != null) {
            this.toCmp2Entity(entity).OpenEJB_removeCmr(this.relatedProperty, this.source);
         }

         return changed;
      }
   }

   public boolean retainAll(Collection c) {
      Set entityBeans = getEntityBeans(c, (Class)null);
      boolean changed = false;
      Iterator<Bean> iterator = this.getRelatedBeans(false, true).iterator();

      while(iterator.hasNext()) {
         Bean entity = (Bean)(iterator.next());
         if (!entityBeans.contains(entity)) {
            iterator.remove();
            if (this.relatedProperty != null) {
               this.toCmp2Entity(entity).OpenEJB_removeCmr(this.relatedProperty, this.source);
            }

            changed = true;
         }
      }

      return changed;
   }

   public Iterator<Proxy> iterator() {
      return new Iterator<Proxy>() {
         private Bean currentEntity;
         private final Iterator<Bean> iterator = CmrSet.this.getRelatedBeans(true, false).iterator();

         public boolean hasNext() {
            return this.iterator.hasNext();
         }

         public Proxy next() {
            if (CmrSet.this.relatedBeans == null) {
               throw new ConcurrentModificationException("Entity has been deleted therefore this iterator can no longer be used");
            } else {
               this.currentEntity = (Bean)((EntityBean)this.iterator.next());
               return (Proxy)CmrSet.this.getEjbProxy(this.currentEntity);
            }
         }

         public void remove() {
            if (CmrSet.this.relatedBeans == null) {
               throw new ConcurrentModificationException("Entity has been deleted therefore this iterator can no longer be used");
            } else if (!CmrSet.this.mutable) {
               throw new ConcurrentModificationException("Transaction has completed therefore this cmr collection can no longer be modified");
            } else {
               this.iterator.remove();
               if (CmrSet.this.relatedProperty != null) {
                  CmrSet.this.toCmp2Entity(this.currentEntity).OpenEJB_removeCmr(CmrSet.this.relatedProperty, CmrSet.this.source);
               }

            }
         }
      };
   }

   private Proxy getEjbProxy(Bean entity) throws EJBException {
      if (entity == null) {
         return null;
      } else {
         Proxy ejbProxy = Cmp2Util.getEjbProxy(this.relatedInfo, entity);
         return ejbProxy;
      }
   }

   private Bean getEntityBean(EJBLocalObject proxy) {
      if (proxy == null) {
         return null;
      } else {
         Bean bean = Cmp2Util.getEntityBean(proxy);
         return bean;
      }
   }

   private static <Bean extends EntityBean> Set<Bean> getEntityBeans(Collection<?> proxies, Class type) {
      if (proxies == null) {
         return null;
      } else {
         Set<Bean> entities = new HashSet();

         for(Object value : proxies) {
            if (type != null && !type.isInstance(value)) {
               throw new IllegalArgumentException("Object is not an instance of " + type.getName() + ": " + (value == null ? "null" : value.getClass().getName()));
            }

            Bean entity = Cmp2Util.getEntityBean((EJBLocalObject)value);
            if (entity == null) {
               throw new IllegalArgumentException("Entity has been deleted");
            }

            entities.add(entity);
         }

         return entities;
      }
   }

   private Cmp2Entity toCmp2Entity(Object object) {
      return (Cmp2Entity)object;
   }

   private Collection<Bean> getRelatedBeans(boolean mustExist, boolean mutateOpation) {
      if (this.relatedBeans == null) {
         if (mustExist) {
            throw new IllegalStateException("Entity has been deleted therefore this cmr collection can no longer be modified");
         } else {
            return Collections.emptySet();
         }
      } else if (mutateOpation && !this.mutable) {
         throw new IllegalStateException("Transaction has completed therefore this cmr collection can no longer be modified");
      } else {
         return this.relatedBeans;
      }
   }
}
