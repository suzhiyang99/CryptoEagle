package org.apache.openejb.ri.sp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.apache.openejb.spi.TransactionService;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class PseudoTransactionService implements TransactionService, TransactionManager, TransactionSynchronizationRegistry {
   private static final Logger logger;
   private final ThreadLocal<MyTransaction> threadTransaction = new ThreadLocal();

   public void init(Properties props) {
   }

   public TransactionManager getTransactionManager() {
      return this;
   }

   public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
      return this;
   }

   public int getStatus() {
      MyTransaction tx = (MyTransaction)this.threadTransaction.get();
      return tx == null ? 6 : tx.getStatus();
   }

   public Transaction getTransaction() {
      return (Transaction)this.threadTransaction.get();
   }

   public boolean getRollbackOnly() {
      MyTransaction tx = (MyTransaction)this.threadTransaction.get();
      if (tx == null) {
         throw new IllegalStateException("No transaction active");
      } else {
         return tx.getRollbackOnly();
      }
   }

   public void setRollbackOnly() {
      MyTransaction tx = (MyTransaction)this.threadTransaction.get();
      if (tx == null) {
         throw new IllegalStateException("No transaction active");
      } else {
         tx.setRollbackOnly();
      }
   }

   public void begin() throws NotSupportedException {
      if (this.threadTransaction.get() != null) {
         throw new NotSupportedException("A transaction is already active");
      } else {
         MyTransaction tx = new MyTransaction();
         this.threadTransaction.set(tx);
      }
   }

   public void commit() throws RollbackException {
      MyTransaction tx = (MyTransaction)this.threadTransaction.get();
      if (tx == null) {
         throw new IllegalStateException("No transaction active");
      } else {
         try {
            tx.commit();
         } finally {
            this.threadTransaction.set((Object)null);
         }

      }
   }

   public void rollback() {
      MyTransaction tx = (MyTransaction)this.threadTransaction.get();
      if (tx == null) {
         throw new IllegalStateException("No transaction active");
      } else {
         try {
            tx.rollback();
         } finally {
            this.threadTransaction.set((Object)null);
         }

      }
   }

   public Transaction suspend() {
      return (Transaction)this.threadTransaction.get();
   }

   public void resume(Transaction tx) throws InvalidTransactionException {
      if (tx == null) {
         throw new InvalidTransactionException("Transaction is null");
      } else if (!(tx instanceof MyTransaction)) {
         throw new InvalidTransactionException("Unknown transaction type " + tx.getClass().getName());
      } else {
         MyTransaction myTransaction = (MyTransaction)tx;
         if (this.threadTransaction.get() != null) {
            throw new IllegalStateException("A transaction is already active");
         } else {
            int status = myTransaction.getStatus();
            if (status != 0 && status != 1) {
               throw new InvalidTransactionException("Expected transaction to be STATUS_ACTIVE or STATUS_MARKED_ROLLBACK, but was " + status);
            } else {
               this.threadTransaction.set(myTransaction);
            }
         }
      }
   }

   public Object getTransactionKey() {
      return this.getTransaction();
   }

   public int getTransactionStatus() {
      return this.getStatus();
   }

   public Object getResource(Object key) {
      MyTransaction tx = (MyTransaction)this.threadTransaction.get();
      if (tx == null) {
         throw new IllegalStateException("No transaction active");
      } else {
         Object value = tx.getResource(key);
         return value;
      }
   }

   public void putResource(Object key, Object value) {
      MyTransaction tx = (MyTransaction)this.threadTransaction.get();
      if (tx == null) {
         throw new IllegalStateException("No transaction active");
      } else {
         tx.putResource(key, value);
      }
   }

   public void registerInterposedSynchronization(Synchronization synchronization) {
      MyTransaction tx = (MyTransaction)this.threadTransaction.get();
      if (tx == null) {
         throw new IllegalStateException("No transaction active");
      } else {
         tx.registerInterposedSynchronization(synchronization);
      }
   }

   public void setTransactionTimeout(int seconds) {
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.core.cmp");
   }

   public class MyTransaction implements Transaction {
      private final List<Synchronization> registeredSynchronizations = Collections.synchronizedList(new ArrayList());
      private final List<XAResource> xaResources = Collections.synchronizedList(new ArrayList());
      private final Map<Object, Object> resources = new HashMap();
      private int status = 0;

      public boolean delistResource(XAResource xaRes, int flag) {
         this.xaResources.remove(xaRes);
         return true;
      }

      public boolean enlistResource(XAResource xaRes) {
         this.xaResources.add(xaRes);
         return true;
      }

      public int getStatus() {
         return this.status;
      }

      public void registerSynchronization(Synchronization synchronization) {
         this.registeredSynchronizations.add(synchronization);
      }

      public void registerInterposedSynchronization(Synchronization synchronization) {
         this.registeredSynchronizations.add(synchronization);
      }

      public boolean getRollbackOnly() {
         return this.status == 1;
      }

      public void setRollbackOnly() {
         this.status = 1;
      }

      public Object getResource(Object key) {
         if (key == null) {
            throw new NullPointerException("key is null");
         } else {
            return this.resources.get(key);
         }
      }

      public void putResource(Object key, Object value) {
         if (key == null) {
            throw new NullPointerException("key is null");
         } else {
            if (value != null) {
               this.resources.put(key, value);
            } else {
               this.resources.remove(key);
            }

         }
      }

      public void commit() throws RollbackException {
         try {
            if (this.status == 1) {
               this.rollback();
               throw new RollbackException();
            }

            try {
               this.doBeforeCompletion();
            } catch (Exception e) {
               this.rollback();
               throw (RollbackException)(new RollbackException()).initCause(e);
            }

            this.doXAResources(3);
            this.status = 3;
            this.doAfterCompletion(3);
         } finally {
            PseudoTransactionService.this.threadTransaction.set((Object)null);
         }

      }

      public void rollback() {
         try {
            this.doXAResources(4);
            this.doAfterCompletion(4);
            this.status = 4;
            this.registeredSynchronizations.clear();
         } finally {
            PseudoTransactionService.this.threadTransaction.set((Object)null);
         }

      }

      private void doBeforeCompletion() {
         for(Synchronization sync : new ArrayList(this.registeredSynchronizations)) {
            sync.beforeCompletion();
         }

      }

      private void doAfterCompletion(int status) {
         for(Synchronization sync : new ArrayList(this.registeredSynchronizations)) {
            try {
               sync.afterCompletion(status);
            } catch (RuntimeException e) {
               PseudoTransactionService.logger.warning("Synchronization afterCompletion threw a RuntimeException", (Throwable)e);
            }
         }

      }

      private void doXAResources(int status) {
         for(XAResource xaRes : new ArrayList(this.xaResources)) {
            if (status == 3) {
               try {
                  xaRes.commit((Xid)null, true);
               } catch (XAException var6) {
               }

               try {
                  xaRes.end((Xid)null, 67108864);
               } catch (XAException var5) {
               }
            } else {
               try {
                  xaRes.rollback((Xid)null);
               } catch (XAException var8) {
               }

               try {
                  xaRes.end((Xid)null, 536870912);
               } catch (XAException var7) {
               }
            }
         }

         this.xaResources.clear();
      }
   }
}
