package org.apache.openejb.core.singleton;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.List;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.core.ivm.EjbObjectProxyHandler;
import org.apache.openejb.util.proxy.ProxyManager;

public class SingletonEjbObjectHandler extends EjbObjectProxyHandler {
   public Object registryId;

   public SingletonEjbObjectHandler(BeanContext beanContext, Object pk, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      super(beanContext, pk, interfaceType, interfaces, mainInterface);
   }

   public static Object createRegistryId(Object primKey, Object deployId, Container contnr) {
      return String.valueOf(deployId) + contnr.getContainerID();
   }

   public Object getRegistryId() {
      if (this.registryId == null) {
         this.registryId = createRegistryId(this.primaryKey, this.deploymentID, this.container);
      }

      return this.registryId;
   }

   protected Object getPrimaryKey(Method method, Object[] args, Object proxy) throws Throwable {
      throw new RemoteException("Session objects are private resources and do not have primary keys");
   }

   protected Object isIdentical(Method method, Object[] args, Object proxy) throws Throwable {
      try {
         EjbObjectProxyHandler handler = (EjbObjectProxyHandler)ProxyManager.getInvocationHandler(args[0]);
         return new Boolean(this.deploymentID.equals(handler.deploymentID));
      } catch (Throwable var5) {
         return Boolean.FALSE;
      }
   }

   public void invalidateReference() {
   }

   protected Object remove(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      return null;
   }
}
