package org.apache.openejb.core.transaction;

import java.io.Serializable;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.openejb.core.ThreadContext;

public class EjbUserTransaction implements UserTransaction, Serializable {
   private static final long serialVersionUID = 8369364873055306924L;

   public void begin() throws NotSupportedException, SystemException {
      this.getUserTransaction().begin();
   }

   public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException {
      this.getUserTransaction().commit();
   }

   public int getStatus() throws SystemException {
      return this.getUserTransaction().getStatus();
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException {
      this.getUserTransaction().rollback();
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException {
      this.getUserTransaction().setRollbackOnly();
   }

   public void setTransactionTimeout(int i) throws SystemException {
      this.getUserTransaction().setTransactionTimeout(i);
   }

   private UserTransaction getUserTransaction() throws SystemException {
      ThreadContext callContext = ThreadContext.getThreadContext();
      if (callContext != null) {
         TransactionPolicy txPolicy = callContext.getTransactionPolicy();
         if (txPolicy != null && txPolicy instanceof BeanTransactionPolicy) {
            BeanTransactionPolicy beanTxEnv = (BeanTransactionPolicy)txPolicy;
            return beanTxEnv.getUserTransaction();
         }
      }

      throw new SystemException("Current thread context does not contain a bean-managed transaction environment");
   }
}
