package org.apache.openejb.core.managed;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import javax.ejb.RemoveException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.core.ivm.EjbHomeProxyHandler;
import org.apache.openejb.core.ivm.EjbObjectProxyHandler;
import org.apache.openejb.util.proxy.ProxyManager;

public class ManagedHomeHandler extends EjbHomeProxyHandler {
   public ManagedHomeHandler(BeanContext beanContext, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      super(beanContext, interfaceType, interfaces, mainInterface);
   }

   public Object createProxy(Object primaryKey, Class mainInterface) {
      Object proxy = super.createProxy(primaryKey, mainInterface);
      EjbObjectProxyHandler handler = null;

      try {
         handler = (EjbObjectProxyHandler)ProxyManager.getInvocationHandler(proxy);
      } catch (Exception var8) {
         try {
            Field field = proxy.getClass().getDeclaredField("invocationHandler");
            field.setAccessible(true);
            handler = (EjbObjectProxyHandler)field.get(proxy);
         } catch (Exception var7) {
         }
      }

      this.registerHandler(handler.getRegistryId(), handler);
      return proxy;
   }

   protected Object findX(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      throw new UnsupportedOperationException("Stateful beans may not have find methods");
   }

   protected Object removeByPrimaryKey(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      throw new RemoveException("Session objects are private resources and do not have primary keys");
   }

   protected EjbObjectProxyHandler newEjbObjectHandler(BeanContext beanContext, Object pk, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      return new ManagedObjectHandler(this.getBeanContext(), pk, interfaceType, interfaces, mainInterface);
   }
}
