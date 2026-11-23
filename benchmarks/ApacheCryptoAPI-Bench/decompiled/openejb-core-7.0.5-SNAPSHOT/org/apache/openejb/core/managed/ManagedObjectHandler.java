package org.apache.openejb.core.managed;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.List;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.core.ivm.EjbObjectProxyHandler;
import org.apache.openejb.util.proxy.ProxyManager;

public class ManagedObjectHandler extends EjbObjectProxyHandler {
   public ManagedObjectHandler(BeanContext beanContext, Object pk, InterfaceType interfaceType, List<Class> interfaces, Class mainInterface) {
      super(beanContext, pk, interfaceType, interfaces, mainInterface);
   }

   public Object getRegistryId() {
      return new RegistryId(this.container, this.deploymentID, this.primaryKey);
   }

   protected Object getPrimaryKey(Method method, Object[] args, Object proxy) throws Throwable {
      throw new RemoteException("Session objects are private resources and do not have primary keys");
   }

   protected Object isIdentical(Method method, Object[] args, Object proxy) throws Throwable {
      this.checkAuthorization(method);
      if (args.length != 1) {
         throw new IllegalArgumentException("Expected one argument to isIdentical, but received " + args.length);
      } else {
         Object that = args[0];
         Object invocationHandler = ProxyManager.getInvocationHandler(that);
         if (invocationHandler instanceof ManagedObjectHandler) {
            ManagedObjectHandler handler = (ManagedObjectHandler)invocationHandler;
            return this.getRegistryId().equals(handler.getRegistryId());
         } else {
            return false;
         }
      }
   }

   protected Object remove(Class interfce, Method method, Object[] args, Object proxy) throws Throwable {
      this.checkAuthorization(method);
      Object value = this.container.invoke(this.deploymentID, this.interfaceType, interfce, method, args, this.primaryKey);
      this.invalidateAllHandlers(this.getRegistryId());
      return value;
   }

   public static class RegistryId implements Serializable {
      private static final long serialVersionUID = 5037368364299042022L;
      private final Object containerId;
      private final Object deploymentId;
      private final Object primaryKey;

      public RegistryId(Container container, Object deploymentId, Object primaryKey) {
         if (container == null) {
            throw new NullPointerException("container is null");
         } else if (deploymentId == null) {
            throw new NullPointerException("deploymentId is null");
         } else {
            this.containerId = container.getContainerID();
            this.deploymentId = deploymentId;
            this.primaryKey = primaryKey;
         }
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            boolean var10000;
            label39: {
               RegistryId that = (RegistryId)o;
               if (this.containerId.equals(that.containerId) && this.deploymentId.equals(that.deploymentId)) {
                  if (this.primaryKey != null) {
                     if (this.primaryKey.equals(that.primaryKey)) {
                        break label39;
                     }
                  } else if (that.primaryKey == null) {
                     break label39;
                  }
               }

               var10000 = false;
               return var10000;
            }

            var10000 = true;
            return var10000;
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.containerId.hashCode();
         result = 31 * result + this.deploymentId.hashCode();
         result = 31 * result + (this.primaryKey != null ? this.primaryKey.hashCode() : 0);
         return result;
      }

      public String toString() {
         return "[" + this.containerId + ", " + this.deploymentId + ", " + this.primaryKey + "]";
      }

      public Object getPrimaryKey() {
         return this.primaryKey;
      }
   }
}
