package org.apache.openejb.core.transaction;

import java.util.Timer;
import javax.resource.spi.BootstrapContext;
import javax.resource.spi.XATerminator;
import javax.resource.spi.work.WorkContext;
import javax.resource.spi.work.WorkManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.geronimo.connector.work.GeronimoWorkManager;

public class SimpleBootstrapContext implements BootstrapContext {
   private final WorkManager workManager;
   private final XATerminator xaTerminator;

   public SimpleBootstrapContext(WorkManager workManager) {
      this.workManager = workManager;
      this.xaTerminator = null;
   }

   public SimpleBootstrapContext(WorkManager workManager, XATerminator xaTerminator) {
      this.workManager = workManager;
      this.xaTerminator = xaTerminator;
   }

   public WorkManager getWorkManager() {
      return this.workManager;
   }

   public XATerminator getXATerminator() {
      return this.xaTerminator;
   }

   public Timer createTimer() {
      return new Timer(true);
   }

   public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
      return this.xaTerminator != null && this.xaTerminator instanceof TransactionSynchronizationRegistry ? (TransactionSynchronizationRegistry)this.xaTerminator : null;
   }

   public boolean isContextSupported(Class<? extends WorkContext> cls) {
      if (this.workManager instanceof GeronimoWorkManager) {
         GeronimoWorkManager geronimoWorkManager = (GeronimoWorkManager)this.workManager;
         return geronimoWorkManager.isContextSupported(cls);
      } else {
         return false;
      }
   }
}
