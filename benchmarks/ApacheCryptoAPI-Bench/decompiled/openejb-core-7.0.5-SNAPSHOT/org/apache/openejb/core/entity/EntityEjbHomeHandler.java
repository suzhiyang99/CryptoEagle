package org.apache.openejb.core.entity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.RemoveException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.ProxyInfo;
import org.apache.openejb.core.ivm.EjbHomeProxyHandler;
import org.apache.openejb.core.ivm.EjbObjectProxyHandler;
import org.apache.openejb.util.ArrayEnumeration;
import org.apache.openejb.util.proxy.ProxyManager;

public class EntityEjbHomeHandler extends EjbHomeProxyHandler {
   public EntityEjbHomeHandler(BeanContext beanContext, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      super(beanContext, interfaceType, interfaces, mainInterface);
   }

   public Object createProxy(Object primaryKey, Class mainInterface) {
      Object proxy = super.createProxy(primaryKey, mainInterface);
      EjbObjectProxyHandler handler = (EjbObjectProxyHandler)ProxyManager.getInvocationHandler(proxy);
      this.registerHandler(handler.getRegistryId(), handler);
      return proxy;
   }

   protected Object findX(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      Object retValue;
      try {
         retValue = this.container.invoke(this.deploymentID, this.interfaceType, interfce, method, args, (Object)null);
      } catch (OpenEJBException e) {
         logger.debug("entityEjbHomeHandler.containerInvocationFailure", e, e.getMessage());
         throw e;
      }

      if (retValue instanceof Collection) {
         Object[] proxyInfos = ((Collection)retValue).toArray();
         Vector proxies = new Vector();

         for(int i = 0; i < proxyInfos.length; ++i) {
            ProxyInfo proxyInfo = (ProxyInfo)proxyInfos[i];
            proxies.addElement(this.createProxy(proxyInfo.getPrimaryKey(), this.getMainInterface()));
         }

         return proxies;
      } else if (retValue instanceof ArrayEnumeration) {
         ArrayEnumeration enumeration = (ArrayEnumeration)retValue;

         for(int i = enumeration.size() - 1; i >= 0; --i) {
            ProxyInfo proxyInfo = (ProxyInfo)enumeration.get(i);
            enumeration.set(i, this.createProxy(proxyInfo.getPrimaryKey(), this.getMainInterface()));
         }

         return enumeration;
      } else if (!(retValue instanceof Enumeration)) {
         ProxyInfo proxyInfo = (ProxyInfo)retValue;
         return this.createProxy(proxyInfo.getPrimaryKey(), this.getMainInterface());
      } else {
         Enumeration enumeration = (Enumeration)retValue;
         List proxies = new ArrayList();

         while(enumeration.hasMoreElements()) {
            ProxyInfo proxyInfo = (ProxyInfo)enumeration.nextElement();
            proxies.add(this.createProxy(proxyInfo.getPrimaryKey(), this.getMainInterface()));
         }

         return new ArrayEnumeration(proxies);
      }
   }

   protected Object removeByPrimaryKey(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      Object primKey = args[0];
      if (primKey instanceof EJBLocalObject) {
         Class ejbObjectProxyClass = primKey.getClass();
         String ejbObjectName = null;

         for(Class clazz : ejbObjectProxyClass.getInterfaces()) {
            if (EJBLocalObject.class.isAssignableFrom(clazz)) {
               ejbObjectName = clazz.getSimpleName();
               break;
            }
         }

         throw new RemoveException("Invalid argument '" + ejbObjectName + "', expected primary key.  Update to ejbLocalHome.remove(" + lcfirst(ejbObjectName) + ".getPrimaryKey())");
      } else if (!(primKey instanceof EJBObject)) {
         this.container.invoke(this.deploymentID, this.interfaceType, interfce, method, args, primKey);
         this.invalidateAllHandlers(EntityEjbObjectHandler.getRegistryId(this.container, this.deploymentID, primKey));
         return null;
      } else {
         Class ejbObjectProxyClass = primKey.getClass();
         String ejbObjectName = null;

         for(Class clazz : ejbObjectProxyClass.getInterfaces()) {
            if (EJBObject.class.isAssignableFrom(clazz)) {
               ejbObjectName = clazz.getSimpleName();
               break;
            }
         }

         throw new RemoveException("Invalid argument '" + ejbObjectName + "', expected primary key.  Update to ejbHome.remove(" + lcfirst(ejbObjectName) + ".getPrimaryKey())");
      }
   }

   private static String lcfirst(String s) {
      if (s != null && s.length() >= 1) {
         StringBuilder sb = new StringBuilder(s);
         sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
         return sb.toString();
      } else {
         return s;
      }
   }

   protected EjbObjectProxyHandler newEjbObjectHandler(BeanContext beanContext, Object pk, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      return new EntityEjbObjectHandler(this.getBeanContext(), pk, interfaceType, interfaces, mainInterface);
   }
}
