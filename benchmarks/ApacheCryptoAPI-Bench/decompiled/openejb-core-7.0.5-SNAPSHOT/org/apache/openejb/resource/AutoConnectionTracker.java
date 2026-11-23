package org.apache.openejb.resource;

import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import javax.resource.ResourceException;
import javax.resource.spi.DissociatableManagedConnection;
import org.apache.commons.lang3.ClassUtils;
import org.apache.geronimo.connector.outbound.ConnectionInfo;
import org.apache.geronimo.connector.outbound.ConnectionReturnAction;
import org.apache.geronimo.connector.outbound.ConnectionTrackingInterceptor;
import org.apache.geronimo.connector.outbound.ManagedConnectionInfo;
import org.apache.geronimo.connector.outbound.connectiontracking.ConnectionTracker;
import org.apache.openejb.dyni.DynamicSubclass;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.proxy.LocalBeanProxyFactory;

public class AutoConnectionTracker implements ConnectionTracker {
   private final ConcurrentMap<ManagedConnectionInfo, ProxyPhantomReference> references = new ConcurrentHashMap();
   private final ReferenceQueue referenceQueue = new ReferenceQueue();
   private final ConcurrentMap<Class<?>, Class<?>> proxies = new ConcurrentHashMap();
   private final ConcurrentMap<Class<?>, Class<?>[]> interfaces = new ConcurrentHashMap();

   public Set<ManagedConnectionInfo> connections() {
      return this.references.keySet();
   }

   public void setEnvironment(ConnectionInfo connectionInfo, String key) {
      for(ProxyPhantomReference reference = (ProxyPhantomReference)this.referenceQueue.poll(); reference != null; reference = (ProxyPhantomReference)this.referenceQueue.poll()) {
         reference.clear();
         this.references.remove(reference.managedConnectionInfo);
         ConnectionInfo released = new ConnectionInfo(reference.managedConnectionInfo);
         reference.interceptor.returnConnection(released, ConnectionReturnAction.DESTROY);
      }

   }

   public void handleObtained(ConnectionTrackingInterceptor interceptor, ConnectionInfo connectionInfo, boolean reassociate) throws ResourceException {
      if (!reassociate) {
         this.proxyConnection(interceptor, connectionInfo);
      }

   }

   public void handleReleased(ConnectionTrackingInterceptor interceptor, ConnectionInfo connectionInfo, ConnectionReturnAction action) {
      PhantomReference phantomReference = (PhantomReference)this.references.remove(connectionInfo.getManagedConnectionInfo());
      if (phantomReference != null) {
         phantomReference.clear();
      }

   }

   private void proxyConnection(ConnectionTrackingInterceptor interceptor, ConnectionInfo connectionInfo) throws ResourceException {
      if (connectionInfo.getConnectionProxy() == null) {
         if (!(connectionInfo.getManagedConnectionInfo().getManagedConnection() instanceof DissociatableManagedConnection)) {
            try {
               Object handle = connectionInfo.getConnectionHandle();
               ConnectionInvocationHandler invocationHandler = new ConnectionInvocationHandler(handle);
               Object proxy = this.newProxy(handle, invocationHandler);
               connectionInfo.setConnectionProxy(proxy);
               ProxyPhantomReference reference = new ProxyPhantomReference(interceptor, connectionInfo.getManagedConnectionInfo(), invocationHandler, this.referenceQueue);
               this.references.put(connectionInfo.getManagedConnectionInfo(), reference);
            } catch (Throwable e) {
               throw new ResourceException("Unable to construct connection proxy", e);
            }
         }
      }
   }

   private Object newProxy(Object handle, InvocationHandler invocationHandler) {
      ClassLoader loader = handle.getClass().getClassLoader();
      if (loader == null) {
         loader = ClassLoader.getSystemClassLoader();
      }

      if (!Proxy.isProxyClass(handle.getClass())) {
         Object proxy = LocalBeanProxyFactory.Unsafe.allocateInstance(this.getProxy(handle.getClass(), loader));
         DynamicSubclass.setHandler(proxy, invocationHandler);
         return proxy;
      } else {
         return Proxy.newProxyInstance(loader, this.getAPi(handle.getClass()), invocationHandler);
      }
   }

   private Class<?>[] getAPi(Class<?> aClass) {
      Class<?>[] found = (Class[])this.interfaces.get(aClass);
      if (found == null) {
         synchronized(this) {
            found = (Class[])this.interfaces.get(aClass);
            if (found == null) {
               List<Class<?>> allInterfaces = ClassUtils.getAllInterfaces(aClass);
               Class<?>[] asArray = (Class[])allInterfaces.toArray(new Class[allInterfaces.size()]);
               this.interfaces.put(aClass, asArray);
               found = (Class[])this.interfaces.get(aClass);
            }
         }
      }

      return found;
   }

   private Class<?> getProxy(Class<?> aClass, ClassLoader loader) {
      Class<?> found = (Class)this.proxies.get(aClass);
      if (found == null) {
         synchronized(this) {
            found = (Class)this.proxies.get(aClass);
            if (found == null) {
               this.proxies.put(aClass, DynamicSubclass.createSubclass(aClass, loader, true));
               found = (Class)this.proxies.get(aClass);
            }
         }
      }

      return found;
   }

   public static class ConnectionInvocationHandler implements InvocationHandler {
      private final Object handle;

      public ConnectionInvocationHandler(Object handle) {
         this.handle = handle;
      }

      public Object invoke(Object object, Method method, Object[] args) throws Throwable {
         if (method.getDeclaringClass() == Object.class) {
            if (method.getName().equals("finalize")) {
               return null;
            }

            if (method.getName().equals("clone")) {
               throw new CloneNotSupportedException();
            }
         }

         try {
            return method.invoke(this.handle, args);
         } catch (InvocationTargetException ite) {
            Throwable t = ite.getTargetException();
            if (AbstractMethodError.class.isInstance(t)) {
               Logger.getInstance(LogCategory.OPENEJB, AutoConnectionTracker.class).error("Missing method: " + method + " on " + this.handle);
            }

            if (t != null) {
               throw t;
            } else {
               throw ite;
            }
         }
      }
   }

   private static class ProxyPhantomReference extends PhantomReference<ConnectionInvocationHandler> {
      private final ConnectionTrackingInterceptor interceptor;
      private final ManagedConnectionInfo managedConnectionInfo;

      public ProxyPhantomReference(ConnectionTrackingInterceptor interceptor, ManagedConnectionInfo managedConnectionInfo, ConnectionInvocationHandler handler, ReferenceQueue referenceQueue) {
         super(handler, referenceQueue);
         this.interceptor = interceptor;
         this.managedConnectionInfo = managedConnectionInfo;
      }
   }
}
