package org.openejb;

import javax.transaction.TransactionManager;
import org.apache.openejb.spi.ApplicationServer;

/** @deprecated */
public final class OpenEJB {
   public static ApplicationServer getApplicationServer() {
      return org.apache.openejb.OpenEJB.getApplicationServer();
   }

   public static TransactionManager getTransactionManager() {
      return org.apache.openejb.OpenEJB.getTransactionManager();
   }
}
