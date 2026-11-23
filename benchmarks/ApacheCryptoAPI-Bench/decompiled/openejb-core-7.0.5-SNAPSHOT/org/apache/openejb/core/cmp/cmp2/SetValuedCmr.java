package org.apache.openejb.core.cmp.cmp2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;
import javax.ejb.EntityBean;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.BeanContext;
import org.apache.openejb.loader.SystemInstance;

public class SetValuedCmr<Bean extends EntityBean, Proxy extends EJBLocalObject> {
   private final EntityBean source;
   private final String sourceProperty;
   private final String relatedProperty;
   private final BeanContext relatedInfo;
   private final TransactionSynchronizationRegistry transactionRegistry;

   public SetValuedCmr(EntityBean source, String sourceProperty, Class<Bean> relatedType, String relatedProperty) {
      if (source == null) {
         throw new NullPointerException("source is null");
      } else if (relatedType == null) {
         throw new NullPointerException("relatedType is null");
      } else {
         this.source = source;
         this.sourceProperty = sourceProperty;
         this.relatedProperty = relatedProperty;
         this.relatedInfo = Cmp2Util.getBeanContext(relatedType);
         this.transactionRegistry = (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class);
      }
   }

   public Set<Proxy> get(Set<Bean> others) {
      if (this.sourceProperty == null) {
         throw new EJBException("Internal error: this container managed relationship is unidirectional and, this entity does not have a cmr field for the relationship");
      } else if (others == null) {
         throw new NullPointerException("others is null");
      } else {
         CmrSet<Bean, Proxy> cmrSet = null;

         try {
            cmrSet = (CmrSet)this.transactionRegistry.getResource(this);
         } catch (IllegalStateException var5) {
         }

         if (cmrSet == null) {
            cmrSet = new CmrSet<Bean, Proxy>(this.source, this.sourceProperty, this.relatedInfo, this.relatedProperty, others);

            try {
               this.transactionRegistry.putResource(this, cmrSet);
            } catch (IllegalStateException var4) {
            }
         }

         return cmrSet;
      }
   }

   public void set(Set<Bean> relatedBeans, Collection newProxies) {
      if (this.sourceProperty == null) {
         throw new EJBException("Internal error: this container managed relationship is unidirectional and, this entity does not have a cmr field for the relationship");
      } else if (newProxies == null) {
         throw new IllegalArgumentException("null can not be set into a collection-valued cmr-field");
      } else {
         if (this.relatedProperty != null) {
            for(Bean oldBean : relatedBeans) {
               if (oldBean != null) {
                  this.toCmp2Entity(oldBean).OpenEJB_removeCmr(this.relatedProperty, this.source);
               }
            }
         }

         relatedBeans.clear();

         for(Proxy newProxy : new ArrayList(newProxies)) {
            Bean newBean = Cmp2Util.getEntityBean(newProxy);
            if (newProxy != null) {
               Object oldBackRef = null;
               if (this.relatedProperty != null) {
                  oldBackRef = this.toCmp2Entity(newBean).OpenEJB_addCmr(this.relatedProperty, this.source);
               }

               relatedBeans.add(newBean);
               if (this.relatedProperty != null && oldBackRef != null) {
                  this.toCmp2Entity(oldBackRef).OpenEJB_removeCmr(this.sourceProperty, newBean);
               }
            }
         }

      }
   }

   public void deleted(Set<Bean> relatedBeans) {
      CmrSet<Bean, Proxy> cmrSet = (CmrSet)this.transactionRegistry.getResource(this);
      if (cmrSet != null) {
         this.transactionRegistry.putResource(this, (Object)null);
         cmrSet.entityDeleted();
      }

      if (this.relatedProperty != null) {
         for(Bean oldBean : relatedBeans) {
            if (oldBean != null) {
               this.toCmp2Entity(oldBean).OpenEJB_removeCmr(this.relatedProperty, this.source);
            }
         }
      }

   }

   private Cmp2Entity toCmp2Entity(Object object) {
      return (Cmp2Entity)object;
   }
}
