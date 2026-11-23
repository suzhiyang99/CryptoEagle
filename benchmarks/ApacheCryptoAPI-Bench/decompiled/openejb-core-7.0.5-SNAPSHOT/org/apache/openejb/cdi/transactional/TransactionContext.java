package org.apache.openejb.cdi.transactional;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;
import javax.enterprise.context.ContextNotActiveException;
import javax.enterprise.context.spi.Contextual;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionScoped;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.apache.geronimo.transaction.manager.TransactionImpl;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;
import org.apache.webbeans.context.AbstractContext;
import org.apache.webbeans.context.creational.BeanInstanceBag;

public class TransactionContext extends AbstractContext implements Synchronization {
   private static final Class<?>[] MAP = new Class[]{Map.class};
   private final TransactionManager transactionManager = OpenEJB.getTransactionManager();
   private final boolean geronimoTxMgr = GeronimoTransactionManager.class.isInstance(OpenEJB.getTransactionManager());

   public TransactionContext() {
      super(TransactionScoped.class);
   }

   public boolean isActive() {
      try {
         int status = this.transactionManager.getTransaction().getStatus();
         return status == 0 || status == 1 || status == 2 || status == 7 || status == 8 || status == 9 || status == 5;
      } catch (Throwable var2) {
         return false;
      }
   }

   protected void checkActive() {
      if (!this.isActive()) {
         throw new ContextNotActiveException("Context with scope annotation @" + this.getScope().getName() + " is not active");
      }
   }

   protected void setComponentInstanceMap() {
      this.componentInstanceMap = (Map)Map.class.cast(Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), MAP, new TransactionalMapHandler(this, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class))));
   }

   public void beforeCompletion() {
      if (!this.geronimoTxMgr) {
         this.destroy();
      }

   }

   public void afterCompletion(int status) {
      if (this.geronimoTxMgr) {
         this.destroy();
      }

   }

   private static class TransactionalMapHandler implements InvocationHandler {
      private static final String KEY = "@Transactional#OpenEJB.map";
      private final TransactionSynchronizationRegistry registry;
      private final TransactionContext context;

      public TransactionalMapHandler(TransactionContext transactionContext, TransactionSynchronizationRegistry registry) {
         this.context = transactionContext;
         this.registry = registry;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         try {
            return method.invoke(this.findMap(), args);
         } catch (InvocationTargetException ite) {
            throw ite.getCause();
         }
      }

      private Map<Contextual<?>, BeanInstanceBag<?>> findMap() {
         Object resource;
         try {
            resource = this.context.geronimoTxMgr ? ((TransactionImpl)TransactionImpl.class.cast(this.context.transactionManager.getTransaction())).getResource("@Transactional#OpenEJB.map") : this.registry.getResource("@Transactional#OpenEJB.map");
         } catch (SystemException e) {
            throw new IllegalStateException(e);
         }

         if (resource == null) {
            Map<Contextual<?>, BeanInstanceBag<?>> map = new HashMap();
            this.registry.putResource("@Transactional#OpenEJB.map", map);
            this.registry.registerInterposedSynchronization(this.context);
            return map;
         } else {
            return (Map)Map.class.cast(resource);
         }
      }
   }
}
