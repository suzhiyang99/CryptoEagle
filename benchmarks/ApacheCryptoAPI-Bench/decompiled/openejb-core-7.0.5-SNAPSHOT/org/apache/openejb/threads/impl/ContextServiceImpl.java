package org.apache.openejb.threads.impl;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import javax.enterprise.concurrent.ContextService;
import javax.transaction.Transaction;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.threads.task.CUTask;

public class ContextServiceImpl implements ContextService {
   private static final HashMap<String, String> EMPTY_PROPS = new HashMap();

   public <T> T createContextualProxy(T instance, Class<T> intf) {
      return (T)intf.cast(this.createContextualProxy(instance, intf));
   }

   public Object createContextualProxy(Object instance, Class<?>... interfaces) {
      return this.createContextualProxy(instance, EMPTY_PROPS, interfaces);
   }

   public <T> T createContextualProxy(T instance, Map<String, String> executionProperties, Class<T> intf) {
      return (T)intf.cast(this.createContextualProxy(instance, executionProperties, intf));
   }

   public Object createContextualProxy(Object instance, Map<String, String> executionProperties, Class<?>... interfaces) {
      return Proxy.newProxyInstance(instance.getClass().getClassLoader(), interfaces, new CUHandler(instance, executionProperties));
   }

   public Map<String, String> getExecutionProperties(Object contextualProxy) {
      return ((CUHandler)CUHandler.class.cast(Proxy.getInvocationHandler(contextualProxy))).properties;
   }

   private static final class CUHandler extends CUTask<Object> implements InvocationHandler, Serializable {
      private final Object instance;
      private final Map<String, String> properties;
      private final boolean suspendTx;

      private CUHandler(Object instance, Map<String, String> props) {
         super(instance);
         this.instance = instance;
         this.properties = props;
         this.suspendTx = "SUSPEND".equals(props.get("javax.enterprise.concurrent.TRANSACTION"));
      }

      public Object invoke(Object proxy, final Method method, final Object[] args) throws Throwable {
         if (method.getDeclaringClass() == Object.class) {
            return method.invoke(this, args);
         } else {
            Transaction suspendedTx;
            if (this.suspendTx) {
               suspendedTx = OpenEJB.getTransactionManager().suspend();
            } else {
               suspendedTx = null;
            }

            Object var5;
            try {
               var5 = this.invoke(new Callable<Object>() {
                  public Object call() throws Exception {
                     return method.invoke(CUHandler.this.instance, args);
                  }
               });
            } finally {
               if (suspendedTx != null) {
                  OpenEJB.getTransactionManager().resume(suspendedTx);
               }

            }

            return var5;
         }
      }
   }
}
