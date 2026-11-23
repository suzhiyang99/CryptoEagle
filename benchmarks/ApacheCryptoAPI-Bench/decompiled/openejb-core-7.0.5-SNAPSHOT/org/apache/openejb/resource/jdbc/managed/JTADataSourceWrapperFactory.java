package org.apache.openejb.resource.jdbc.managed;

import javax.naming.NamingException;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.jdbc.DataSourceFactory;
import org.apache.openejb.resource.jdbc.managed.local.ManagedDataSource;
import org.apache.openejb.resource.jdbc.managed.xa.ManagedXADataSource;
import org.apache.openejb.spi.ContainerSystem;

public class JTADataSourceWrapperFactory {
   private String delegate = "datasource";
   private boolean logSql;
   private String logPackages;

   public CommonDataSource create() {
      TransactionManager transactionManager = OpenEJB.getTransactionManager();
      CommonDataSource cds = this.findDelegate();
      if (cds instanceof XADataSource) {
         cds = new ManagedXADataSource(cds, transactionManager, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class));
      } else {
         cds = new ManagedDataSource((DataSource)DataSource.class.cast(cds), transactionManager, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class));
      }

      if (this.logSql) {
         cds = DataSourceFactory.makeItLogging(cds, this.logPackages);
      }

      return cds;
   }

   private DataSource findDelegate() {
      try {
         return (DataSource)DataSource.class.cast(((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext().lookup("openejb/Resource/" + this.delegate));
      } catch (NamingException e) {
         throw new IllegalArgumentException("'" + this.delegate + "' not found", e);
      }
   }

   public void setDelegate(String delegate) {
      this.delegate = delegate;
   }

   public void setLogSql(boolean logSql) {
      this.logSql = logSql;
   }

   public void setLogPackages(String logPackages) {
      this.logPackages = logPackages;
   }
}
