package org.apache.openejb.cdi;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ObserverMethod;
import javax.naming.NamingException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.apache.openejb.core.CoreUserTransaction;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.ee.event.TransactionalEventNotifier;
import org.apache.webbeans.event.EventMetadataImpl;
import org.apache.webbeans.spi.TransactionService;

public class OpenEJBTransactionService implements TransactionService {
   private static final Logger logger;
   private final ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
   private final WebBeansContext webBeansContext;

   public OpenEJBTransactionService(WebBeansContext webBeansContext) {
      this.webBeansContext = webBeansContext;
   }

   public Transaction getTransaction() {
      TransactionManager manager = this.getTransactionManager();
      if (manager != null) {
         try {
            return manager.getTransaction();
         } catch (SystemException e) {
            logger.error(e.getMessage(), (Throwable)e);
         }
      }

      return null;
   }

   public TransactionManager getTransactionManager() {
      return (TransactionManager)SystemInstance.get().getComponent(TransactionManager.class);
   }

   public UserTransaction getUserTransaction() {
      UserTransaction ut;
      try {
         ut = (UserTransaction)this.containerSystem.getJNDIContext().lookup("comp/UserTransaction");
      } catch (NamingException var3) {
         logger.debug("User transaction is not bound to context, lets create it");
         ut = new CoreUserTransaction(this.getTransactionManager());
      }

      return ut;
   }

   public void registerTransactionSynchronization(TransactionPhase phase, ObserverMethod<? super Object> observer, Object event) throws Exception {
      Set<Annotation> qualifiers = observer.getObservedQualifiers();
      if (qualifiers == null) {
         qualifiers = Collections.emptySet();
      }

      TransactionalEventNotifier.registerTransactionSynchronization(phase, observer, event, new EventMetadataImpl(observer.getObservedType(), (Type)null, (InjectionPoint)null, (Annotation[])qualifiers.toArray(new Annotation[qualifiers.size()]), this.webBeansContext));
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_CDI, OpenEJBTransactionService.class);
   }
}
