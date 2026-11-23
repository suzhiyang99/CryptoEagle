package org.apache.openejb.cdi.transactional;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.CDI;
import javax.interceptor.InvocationContext;
import javax.transaction.RollbackException;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionRequiredException;
import javax.transaction.Transactional;
import javax.transaction.TransactionalException;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.CoreUserTransaction;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.core.transaction.TransactionRolledbackException;
import org.apache.openejb.loader.SystemInstance;

public abstract class InterceptorBase implements Serializable {
   private static final IllegalStateException ILLEGAL_STATE_EXCEPTION = new IllegalStateException("Can't use UserTransaction from @Transaction call");
   private static final boolean HANDLE_EXCEPTION_ONLY_FOR_CLIENT = SystemInstance.get().getOptions().get("openejb.cdi.jta.exception.client-only", false);
   private transient volatile ConcurrentMap<Method, Boolean> rollback = new ConcurrentHashMap();

   protected Object intercept(InvocationContext ic) throws Exception {
      TransactionPolicy policy = null;
      boolean forbidsUt = this.doesForbidUtUsage();
      RuntimeException oldEx;
      IllegalStateException illegalStateException;
      if (forbidsUt) {
         illegalStateException = ILLEGAL_STATE_EXCEPTION;
         oldEx = CoreUserTransaction.error(illegalStateException);
      } else {
         illegalStateException = null;
         oldEx = null;
      }

      Object var24;
      try {
         policy = this.getPolicy();
         Object proceed = ic.proceed();
         policy.commit();
         var24 = proceed;
      } catch (Exception e) {
         if (illegalStateException == e) {
            throw e;
         }

         Exception error = this.unwrap(e);
         if (error != null && (!HANDLE_EXCEPTION_ONLY_FOR_CLIENT || policy.isNewTransaction())) {
            Method method = ic.getMethod();
            if (this.rollback == null) {
               synchronized(this) {
                  if (this.rollback == null) {
                     this.rollback = new ConcurrentHashMap();
                  }
               }
            }

            Boolean doRollback = (Boolean)this.rollback.get(method);
            if (doRollback != null) {
               if (doRollback && policy != null && policy.isTransactionActive()) {
                  policy.setRollbackOnly();
               }
            } else {
               AnnotatedType<?> annotatedType = CDI.current().getBeanManager().createAnnotatedType(method.getDeclaringClass());
               Transactional tx = null;

               for(AnnotatedMethod<?> m : annotatedType.getMethods()) {
                  if (method.equals(m.getJavaMember())) {
                     tx = (Transactional)m.getAnnotation(Transactional.class);
                     break;
                  }
               }

               if (tx == null) {
                  tx = (Transactional)annotatedType.getAnnotation(Transactional.class);
               }

               if (tx != null) {
                  doRollback = (new ExceptionPriotiryRules(tx.rollbackOn(), tx.dontRollbackOn())).accept(error, method.getExceptionTypes());
                  this.rollback.putIfAbsent(method, doRollback);
                  if (doRollback && policy != null && policy.isTransactionActive()) {
                     policy.setRollbackOnly();
                  }
               }
            }
         }

         if (policy != null) {
            try {
               policy.commit();
            } catch (Exception ex) {
               Logger logger = Logger.getLogger(this.getClass().getName());
               if (logger.isLoggable(Level.FINE)) {
                  logger.fine("Swallowing: " + ex.getMessage());
               }
            }
         }

         if (error != null && !TransactionRequiredException.class.isInstance(error)) {
            throw error;
         }

         throw new TransactionalException(e.getMessage(), error);
      } finally {
         if (forbidsUt) {
            CoreUserTransaction.resetError(oldEx);
         }

      }

      return var24;
   }

   private Exception unwrap(Exception e) {
      Exception error;
      Throwable cause;
      for(error = e; error != null && (ApplicationException.class.isInstance(error) || SystemException.class.isInstance(error) || TransactionRolledbackException.class.isInstance(error)); error = Exception.class.isInstance(cause) ? (Exception)Exception.class.cast(cause) : null) {
         cause = error.getCause();
         if (cause == error) {
            break;
         }
      }

      if (RollbackException.class.isInstance(error) && Exception.class.isInstance(error.getCause())) {
         error = (Exception)Exception.class.cast(error.getCause());
      }

      return error;
   }

   protected boolean doesForbidUtUsage() {
      return true;
   }

   protected abstract TransactionPolicy getPolicy() throws SystemException, ApplicationException;

   protected static TransactionManager getTransactionManager() {
      return OpenEJB.getTransactionManager();
   }

   protected static final class ExceptionPriotiryRules {
      private final Class<?>[] includes;
      private final Class<?>[] excludes;

      protected ExceptionPriotiryRules(Class<?>[] includes, Class<?>[] excludes) {
         this.includes = includes;
         this.excludes = excludes;
      }

      public boolean accept(Exception e, Class<?>[] exceptionTypes) {
         if (e == null) {
            return false;
         } else {
            int includeScore = contains(this.includes, e);
            int excludeScore = contains(this.excludes, e);
            if (excludeScore >= 0) {
               return includeScore - excludeScore > 0;
            } else {
               return includeScore >= 0 || isNotChecked(e, exceptionTypes);
            }
         }
      }

      private static int contains(Class<?>[] list, Exception e) {
         int score = -1;

         for(Class<?> clazz : list) {
            if (clazz.isInstance(e)) {
               int thisScore = score(clazz, e.getClass());
               if (score < 0) {
                  score = thisScore;
               } else {
                  score = Math.min(thisScore, score);
               }
            }
         }

         return score;
      }

      private static int score(Class<?> config, Class<?> ex) {
         int score = 0;

         for(Class<?> current = ex; current != null && !current.equals(config); current = current.getSuperclass()) {
            ++score;
         }

         return score;
      }

      private static boolean isNotChecked(Exception e, Class<?>[] exceptionTypes) {
         return RuntimeException.class.isInstance(e) && (exceptionTypes.length == 0 || !Arrays.asList(exceptionTypes).contains(e.getClass()));
      }
   }
}
