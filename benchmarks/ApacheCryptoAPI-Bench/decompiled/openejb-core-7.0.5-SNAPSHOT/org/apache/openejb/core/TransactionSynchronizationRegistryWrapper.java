package org.apache.openejb.core;

import javax.transaction.Synchronization;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.loader.SystemInstance;

public class TransactionSynchronizationRegistryWrapper implements TransactionSynchronizationRegistry {
   private SystemInstance system;
   private TransactionSynchronizationRegistry registry;

   public TransactionSynchronizationRegistryWrapper() {
      SystemInstance system = SystemInstance.get();
      this.registry = (TransactionSynchronizationRegistry)system.getComponent(TransactionSynchronizationRegistry.class);
      this.system = system;
   }

   public TransactionSynchronizationRegistry getRegistry() {
      SystemInstance system = SystemInstance.get();
      if (system != this.system) {
         this.registry = (TransactionSynchronizationRegistry)system.getComponent(TransactionSynchronizationRegistry.class);
         this.system = system;
      }

      return this.registry;
   }

   public Object getResource(Object o) {
      return this.getRegistry().getResource(o);
   }

   public boolean getRollbackOnly() {
      return this.getRegistry().getRollbackOnly();
   }

   public Object getTransactionKey() {
      return this.getRegistry().getTransactionKey();
   }

   public int getTransactionStatus() {
      return this.getRegistry().getTransactionStatus();
   }

   public void putResource(Object o, Object o1) {
      this.getRegistry().putResource(o, o1);
   }

   public void registerInterposedSynchronization(Synchronization synchronization) {
      this.getRegistry().registerInterposedSynchronization(synchronization);
   }

   public void setRollbackOnly() {
      this.getRegistry().setRollbackOnly();
   }
}
