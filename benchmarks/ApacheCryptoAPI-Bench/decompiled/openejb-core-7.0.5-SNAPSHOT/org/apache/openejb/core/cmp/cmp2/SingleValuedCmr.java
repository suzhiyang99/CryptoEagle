package org.apache.openejb.core.cmp.cmp2;

import javax.ejb.EJBException;
import javax.ejb.EJBLocalObject;
import javax.ejb.EntityBean;
import org.apache.openejb.BeanContext;

public class SingleValuedCmr<Bean extends EntityBean, Proxy extends EJBLocalObject> {
   private final EntityBean source;
   private final String sourceProperty;
   private final String relatedProperty;
   private final BeanContext relatedInfo;

   public SingleValuedCmr(EntityBean source, String sourceProperty, Class<Bean> relatedType, String relatedProperty) {
      if (source == null) {
         throw new NullPointerException("source is null");
      } else if (relatedType == null) {
         throw new NullPointerException("relatedType is null");
      } else {
         this.source = source;
         this.sourceProperty = sourceProperty;
         this.relatedProperty = relatedProperty;
         this.relatedInfo = Cmp2Util.getBeanContext(relatedType);
      }
   }

   public Proxy get(Bean entity) throws EJBException {
      if (this.sourceProperty == null) {
         throw new EJBException("Internal error: this container managed relationship is unidirectional and, this entity does not have a cmr field for the relationship");
      } else if (entity == null) {
         return null;
      } else {
         Proxy ejbProxy = Cmp2Util.getEjbProxy(this.relatedInfo, entity);
         return ejbProxy;
      }
   }

   public Bean set(Bean oldBean, Proxy newValue) throws EJBException {
      Bean newBean = Cmp2Util.getEntityBean(newValue);
      if (newValue != null && newBean == null) {
         throw new IllegalArgumentException("A deleted bean can not be assigned to a relationship");
      } else {
         if (this.relatedProperty != null) {
            if (oldBean != null) {
               this.toCmp2Entity(oldBean).OpenEJB_removeCmr(this.relatedProperty, this.source);
            }

            if (newValue != null) {
               Object oldBackRef = this.toCmp2Entity(newBean).OpenEJB_addCmr(this.relatedProperty, this.source);
               if (oldBackRef != null) {
                  this.toCmp2Entity(oldBackRef).OpenEJB_removeCmr(this.sourceProperty, newBean);
               }
            }
         }

         return newBean;
      }
   }

   public void deleted(Bean oldBean) throws EJBException {
      this.set(oldBean, (EJBLocalObject)null);
   }

   private Cmp2Entity toCmp2Entity(Object object) {
      return (Cmp2Entity)object;
   }
}
