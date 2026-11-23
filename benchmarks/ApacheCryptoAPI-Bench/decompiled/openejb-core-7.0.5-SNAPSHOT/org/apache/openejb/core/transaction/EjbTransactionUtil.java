package org.apache.openejb.core.transaction;

import java.rmi.RemoteException;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InvalidateReferenceException;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.ThreadContextListener;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public final class EjbTransactionUtil {
   private static final Logger logger;

   public static TransactionPolicy createTransactionPolicy(TransactionType type, ThreadContext threadContext) throws SystemException, ApplicationException {
      BeanContext beanContext = threadContext.getBeanContext();
      TransactionPolicy txPolicy = beanContext.getTransactionPolicyFactory().createTransactionPolicy(type);
      TransactionPolicy oldTxPolicy = threadContext.getTransactionPolicy();
      txPolicy.putResource(CallerTransactionEnvironment.class, new CallerTransactionEnvironment(oldTxPolicy));
      threadContext.setTransactionPolicy(txPolicy);
      return txPolicy;
   }

   public static void afterInvoke(TransactionPolicy txPolicy, ThreadContext threadContext) throws SystemException, ApplicationException {
      if (txPolicy == threadContext.getTransactionPolicy()) {
         try {
            txPolicy.commit();
         } finally {
            CallerTransactionEnvironment oldTxEnv = (CallerTransactionEnvironment)txPolicy.getResource(CallerTransactionEnvironment.class);
            if (oldTxEnv != null) {
               threadContext.setTransactionPolicy(oldTxEnv.oldTxPolicy);
            } else {
               threadContext.setTransactionPolicy((TransactionPolicy)null);
            }

         }

      } else {
         try {
            txPolicy.setRollbackOnly();
            txPolicy.commit();
         } catch (Exception e) {
            threadContext.setDiscardInstance(true);
            logger.error("Error rolling back transaction", (Throwable)e);
         }

         TransactionPolicy threadContextTxPolicy = threadContext.getTransactionPolicy();
         if (threadContextTxPolicy != null) {
            try {
               threadContextTxPolicy.setRollbackOnly();
               threadContextTxPolicy.commit();
            } catch (Exception e) {
               threadContext.setDiscardInstance(true);
               logger.error("Error rolling back transaction", (Throwable)e);
            }
         }

         if (threadContextTxPolicy != null) {
            throw new SystemException(new IllegalStateException("ThreadContext is bound to another transaction " + threadContextTxPolicy));
         } else {
            throw new SystemException(new IllegalStateException("ThreadContext is not bound to specified transaction " + threadContextTxPolicy));
         }
      }
   }

   public static void handleApplicationException(TransactionPolicy txPolicy, Throwable appException, boolean rollback) throws ApplicationException {
      if (rollback) {
         txPolicy.setRollbackOnly(appException);
      }

      if (!(appException instanceof ApplicationException)) {
         throw new ApplicationException(appException);
      }
   }

   public static void handleSystemException(TransactionPolicy txPolicy, Throwable sysException, ThreadContext callContext) throws InvalidateReferenceException {
      Operation operation = null;
      if (callContext != null) {
         operation = callContext.getCurrentOperation();
      }

      if (operation != null) {
         logger.error("EjbTransactionUtil.handleSystemException: " + sysException.getMessage(), sysException);
      } else {
         logger.debug("EjbTransactionUtil.handleSystemException: " + sysException.getMessage(), sysException);
      }

      txPolicy.setRollbackOnly(sysException);
      if (txPolicy.isClientTransaction()) {
         String message = "The transaction has been marked rollback only because the bean encountered a non-application exception :" + sysException.getClass().getName() + " : " + sysException.getMessage();
         TransactionRolledbackException txException = new TransactionRolledbackException(message, sysException);
         throw new InvalidateReferenceException(txException);
      } else {
         RemoteException re = new RemoteException("The bean encountered a non-application exception", sysException);
         throw new InvalidateReferenceException(re);
      }
   }

   private EjbTransactionUtil() {
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
      ThreadContext.addThreadContextListener(new ThreadContextListener() {
         public void contextEntered(ThreadContext oldContext, ThreadContext newContext) {
            if (oldContext != null) {
               newContext.setTransactionPolicy(oldContext.getTransactionPolicy());
            }

         }

         public void contextExited(ThreadContext exitedContext, ThreadContext reenteredContext) {
         }
      });
   }

   private static final class CallerTransactionEnvironment {
      private final TransactionPolicy oldTxPolicy;

      private CallerTransactionEnvironment(TransactionPolicy oldTxPolicy) {
         this.oldTxPolicy = oldTxPolicy;
      }
   }
}
