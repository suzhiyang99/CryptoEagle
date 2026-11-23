package org.apache.openejb.core;

import java.io.Serializable;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class CoreUserTransaction implements UserTransaction, Serializable {
   private static final long serialVersionUID = 9203248912222645965L;
   private static final transient Logger transactionLogger;
   private static final ThreadLocal<RuntimeException> ERROR;
   private transient TransactionManager transactionManager;

   public CoreUserTransaction(TransactionManager transactionManager) {
      this.transactionManager = transactionManager;
   }

   public static RuntimeException error(RuntimeException e) {
      RuntimeException p = (RuntimeException)ERROR.get();
      ERROR.set(e);
      return p;
   }

   public static void resetError(RuntimeException old) {
      ERROR.set(old);
   }

   public static RuntimeException error() {
      return (RuntimeException)ERROR.get();
   }

   private TransactionManager transactionManager() {
      if (this.transactionManager == null) {
         this.transactionManager = OpenEJB.getTransactionManager();
      }

      return this.transactionManager;
   }

   public void begin() throws NotSupportedException, SystemException {
      this.check();
      this.transactionManager().begin();
      if (transactionLogger.isDebugEnabled()) {
         transactionLogger.debug("Started user transaction " + this.transactionManager().getTransaction());
      }

   }

   private void check() {
      RuntimeException e = (RuntimeException)ERROR.get();
      if (e != null) {
         throw e;
      }
   }

   public void commit() throws RollbackException, HeuristicMixedException, HeuristicRollbackException, SecurityException, IllegalStateException, SystemException {
      this.check();
      if (transactionLogger.isDebugEnabled()) {
         transactionLogger.debug("Committing user transaction " + this.transactionManager().getTransaction());
      }

      this.transactionManager().commit();
   }

   public int getStatus() throws SystemException {
      this.check();
      int status = this.transactionManager().getStatus();
      if (transactionLogger.isDebugEnabled()) {
         transactionLogger.debug("User transaction " + this.transactionManager().getTransaction() + " has status " + getStatus(status));
      }

      return status;
   }

   public void rollback() throws IllegalStateException, SecurityException, SystemException {
      this.check();
      if (transactionLogger.isDebugEnabled()) {
         transactionLogger.debug("Rolling back user transaction " + this.transactionManager().getTransaction());
      }

      this.transactionManager().rollback();
   }

   public void setRollbackOnly() throws SystemException {
      this.check();
      if (transactionLogger.isDebugEnabled()) {
         transactionLogger.debug("Marking user transaction for rollback: " + this.transactionManager().getTransaction());
      }

      this.transactionManager().setRollbackOnly();
   }

   public void setTransactionTimeout(int seconds) throws SystemException {
      this.check();
      this.transactionManager().setTransactionTimeout(seconds);
   }

   private static String getStatus(int status) {
      StringBuilder buffer = new StringBuilder(100);
      switch (status) {
         case 0:
            buffer.append("STATUS_ACTIVE: ");
            buffer.append("A transaction is associated with the target object and it is in the active state.");
            break;
         case 1:
            buffer.append("STATUS_MARKED_ROLLBACK: ");
            buffer.append("A transaction is associated with the target object and it has been marked for rollback, perhaps as a result of a setRollbackOnly operation.");
            break;
         case 2:
            buffer.append("STATUS_PREPARED: ");
            buffer.append("A transaction is associated with the target object and it has been prepared, i.e.");
            break;
         case 3:
            buffer.append("STATUS_COMMITTED: ");
            buffer.append("A transaction is associated with the target object and it has been committed.");
            break;
         case 4:
            buffer.append("STATUS_ROLLEDBACK: ");
            buffer.append("A transaction is associated with the target object and the outcome has been determined as rollback.");
            break;
         case 5:
         default:
            buffer.append("Unknown status ").append(status);
            break;
         case 6:
            buffer.append("STATUS_NO_TRANSACTION: ");
            buffer.append("No transaction is currently associated with the target object.");
            break;
         case 7:
            buffer.append("STATUS_PREPARING: ");
            buffer.append("A transaction is associated with the target object and it is in the process of preparing.");
            break;
         case 8:
            buffer.append("STATUS_COMMITTING: ");
            buffer.append("A transaction is associated with the target object and it is in the process of committing.");
            break;
         case 9:
            buffer.append("STATUS_ROLLING_BACK: ");
            buffer.append("A transaction is associated with the target object and it is in the process of rolling back.");
      }

      return buffer.toString();
   }

   static {
      transactionLogger = Logger.getInstance(LogCategory.TRANSACTION, "org.apache.openejb.util.resources");
      ERROR = new ThreadLocal();
   }
}
