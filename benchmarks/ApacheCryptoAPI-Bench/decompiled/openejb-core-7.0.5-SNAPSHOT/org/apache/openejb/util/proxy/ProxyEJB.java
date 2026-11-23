package org.apache.openejb.util.proxy;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.apache.openejb.BeanContext;
import org.apache.openejb.RpcContainer;
import org.apache.openejb.core.ivm.IntraVmProxy;

public final class ProxyEJB {
   private ProxyEJB() {
   }

   public static Object subclassProxy(BeanContext beanContext) {
      try {
         return LocalBeanProxyFactory.newProxyInstance(beanContext.getModuleContext().getClassLoader(), new Handler(beanContext), beanContext.getBeanClass(), IntraVmProxy.class, Serializable.class);
      } catch (InternalError var2) {
         return LocalBeanProxyFactory.newProxyInstance(beanContext.getModuleContext().getClassLoader(), new Handler(beanContext), beanContext.getBeanClass(), Serializable.class);
      }
   }

   public static Object simpleProxy(BeanContext beanContext, Class<?>[] itfs) {
      return beanContext.isLocalbean() ? LocalBeanProxyFactory.newProxyInstance(itfs[0].getClassLoader(), new Handler(beanContext), itfs[0]) : Proxy.newProxyInstance(itfs[0].getClassLoader(), itfs, new Handler(beanContext));
   }

   public static Object proxy(BeanContext beanContext, Class<?>[] itfs) {
      return beanContext.isLocalbean() ? LocalBeanProxyFactory.newProxyInstance(itfs[0].getClassLoader(), new Handler(beanContext), itfs[0], IntraVmProxy.class, Serializable.class) : Proxy.newProxyInstance(itfs[0].getClassLoader(), itfs, new Handler(beanContext));
   }

   private static class Handler implements BeanContextInvocationHandler {
      private final transient WeakReference<BeanContext> beanContextRef;
      private final Object deploymentID;

      public Handler(BeanContext bc) {
         this.beanContextRef = new WeakReference(bc);
         this.deploymentID = bc.getDeploymentID();
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         BeanContext beanContext = this.getBeanContext();
         RpcContainer container = (RpcContainer)RpcContainer.class.cast(beanContext.getContainer());
         return container.invoke(beanContext.getDeploymentID(), beanContext.getInterfaceType(method.getDeclaringClass()), method.getDeclaringClass(), method, args, (Object)null);
      }

      public BeanContext getBeanContext() {
         BeanContext beanContext = (BeanContext)this.beanContextRef.get();
         if (beanContext != null && !beanContext.isDestroyed()) {
            return beanContext;
         } else {
            this.beanContextRef.clear();
            throw new IllegalStateException("Bean '" + this.deploymentID + "' has been undeployed.");
         }
      }
   }
}
