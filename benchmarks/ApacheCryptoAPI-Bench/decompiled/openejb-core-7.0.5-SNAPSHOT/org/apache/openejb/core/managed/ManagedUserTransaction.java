package org.apache.openejb.core.managed;

import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;
import org.apache.openejb.BeanContext;
import org.apache.openejb.BeanType;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.persistence.JtaEntityManagerRegistry;

public class ManagedUserTransaction implements UserTransaction {
   private final UserTransaction userTransaction;
   private final JtaEntityManagerRegistry jtaEntityManagerRegistry;

   public ManagedUserTransaction(UserTransaction userTransaction, JtaEntityManagerRegistry jtaEntityManagerRegistry) {
      this.userTransaction = userTransaction;
      this.jtaEntityManagerRegistry = jtaEntityManagerRegistry;
   }

   public void begin() throws NotSupportedException, SystemException {
      this.userTransaction.begin();
      ThreadContext callContext = ThreadContext.getThreadContext();
      if (callContext != null) {
         BeanContext beanContext = callContext.getBeanContext();
         if (beanContext.getComponentType() == BeanType.MANAGED) {
            Object primaryKey = callContext.getPrimaryKey();
            if (primaryKey != null) {
               this.jtaEntityManagerRegistry.transactionStarted((String)beanContext.getDeploymentID(), primaryKey);
            }
         }
      }
   }

   public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException {
      this.userTransaction.commit();
   }

   public int getStatus() throws SystemException {
      return this.userTransaction.getStatus();
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException {
      this.userTransaction.rollback();
   }

   public void setRollbackOnly() throws IllegalStateException, SystemException {
      this.userTransaction.setRollbackOnly();
   }

   public void setTransactionTimeout(int i) throws SystemException {
      this.userTransaction.setTransactionTimeout(i);
   }
}
