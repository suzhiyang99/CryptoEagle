package org.apache.openejb.core.transaction;

import javax.transaction.xa.XAResource;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.SystemException;

public interface TransactionPolicy {
   TransactionType getTransactionType();

   boolean isNewTransaction();

   boolean isClientTransaction();

   boolean isTransactionActive();

   boolean isRollbackOnly();

   void setRollbackOnly();

   void setRollbackOnly(Throwable var1);

   void commit() throws ApplicationException, SystemException;

   Object getResource(Object var1);

   void putResource(Object var1, Object var2);

   Object removeResource(Object var1);

   void registerSynchronization(TransactionSynchronization var1);

   void enlistResource(XAResource var1) throws SystemException;

   public interface TransactionSynchronization {
      void beforeCompletion();

      void afterCompletion(Status var1);

      public static enum Status {
         COMMITTED,
         ROLLEDBACK,
         UNKNOWN;
      }
   }
}
