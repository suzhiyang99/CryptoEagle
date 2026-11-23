package org.apache.openejb.core.cmp.cmp2;

import java.lang.reflect.Field;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.EntityBean;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.core.cmp.CmpContainer;
import org.apache.openejb.core.cmp.KeyGenerator;
import org.apache.openejb.core.ivm.EjbObjectProxyHandler;
import org.apache.openejb.util.proxy.ProxyManager;

public class Cmp2Util {
   public static Object getPrimaryKey(BeanContext beanContext, EntityBean entity) {
      if (entity == null) {
         return null;
      } else {
         KeyGenerator kg = beanContext.getKeyGenerator();
         Object primaryKey = kg.getPrimaryKey(entity);
         return primaryKey;
      }
   }

   public static <Bean extends EntityBean> Bean getEntityBean(EJBLocalObject proxy) {
      if (proxy == null) {
         return null;
      } else {
         EjbObjectProxyHandler handler = (EjbObjectProxyHandler)ProxyManager.getInvocationHandler(proxy);
         if (handler.container == null) {
            return null;
         } else if (!(handler.container instanceof CmpContainer)) {
            throw new IllegalArgumentException("Proxy is not connected to a CMP container but is conect to " + handler.container.getClass().getName());
         } else {
            CmpContainer container = (CmpContainer)handler.container;
            Bean entity = (Bean)(container.getEjbInstance(handler.getBeanContext(), handler.primaryKey));
            return entity;
         }
      }
   }

   public static <Bean extends EntityBean> Bean getEntityBean(EJBObject proxy) {
      if (proxy == null) {
         return null;
      } else {
         EjbObjectProxyHandler handler = (EjbObjectProxyHandler)ProxyManager.getInvocationHandler(proxy);
         if (handler.container == null) {
            return null;
         } else if (!(handler.container instanceof CmpContainer)) {
            throw new IllegalArgumentException("Proxy is not connected to a CMP container but is conect to " + handler.container.getClass().getName());
         } else {
            CmpContainer container = (CmpContainer)handler.container;
            Bean entity = (Bean)(container.getEjbInstance(handler.getBeanContext(), handler.primaryKey));
            return entity;
         }
      }
   }

   public static <Proxy extends EJBLocalObject> Proxy getEjbProxy(BeanContext beanContext, EntityBean entity) {
      if (entity == null) {
         return null;
      } else {
         Object primaryKey = getPrimaryKey(beanContext, entity);
         if (!(beanContext.getContainer() instanceof CmpContainer)) {
            throw new IllegalArgumentException("Proxy is not connected to a CMP container but is conect to " + beanContext.getContainer().getClass().getName());
         } else {
            Proxy proxy = (Proxy)(EjbObjectProxyHandler.createProxy(beanContext, primaryKey, InterfaceType.EJB_LOCAL_HOME, beanContext.getLocalInterface()));
            return proxy;
         }
      }
   }

   public static BeanContext getBeanContext(Class type) {
      try {
         Field deploymentInfoField = type.getField("deploymentInfo");
         BeanContext beanContext = (BeanContext)deploymentInfoField.get((Object)null);
         return beanContext;
      } catch (Exception e) {
         throw new IllegalArgumentException("EntityBean class " + type.getName() + " does not contain a deploymentInfo field.  Is this a generated CMP 2 entity implementation?", e);
      }
   }
}
