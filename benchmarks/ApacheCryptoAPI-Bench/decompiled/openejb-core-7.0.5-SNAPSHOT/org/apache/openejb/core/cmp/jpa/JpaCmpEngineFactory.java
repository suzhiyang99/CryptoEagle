package org.apache.openejb.core.cmp.jpa;

import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.core.cmp.CmpCallback;
import org.apache.openejb.core.cmp.CmpEngine;
import org.apache.openejb.core.cmp.CmpEngineFactory;

public class JpaCmpEngineFactory implements CmpEngineFactory {
   private TransactionManager transactionManager;
   private TransactionSynchronizationRegistry transactionSynchronizationRegistry;
   private CmpCallback cmpCallback;

   public TransactionManager getTransactionManager() {
      return this.transactionManager;
   }

   public void setTransactionManager(TransactionManager transactionManager) {
      this.transactionManager = transactionManager;
   }

   public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
      return this.transactionSynchronizationRegistry;
   }

   public void setTransactionSynchronizationRegistry(TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
      this.transactionSynchronizationRegistry = transactionSynchronizationRegistry;
   }

   public CmpCallback getCmpCallback() {
      return this.cmpCallback;
   }

   public void setCmpCallback(CmpCallback cmpCallback) {
      this.cmpCallback = cmpCallback;
   }

   public CmpEngine create() throws OpenEJBException {
      return new JpaCmpEngine(this.cmpCallback);
   }
}
