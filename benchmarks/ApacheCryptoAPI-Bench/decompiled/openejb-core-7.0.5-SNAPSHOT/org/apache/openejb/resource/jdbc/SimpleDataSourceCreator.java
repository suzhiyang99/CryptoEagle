package org.apache.openejb.resource.jdbc;

import java.util.Properties;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.XAResourceWrapper;
import org.apache.openejb.resource.jdbc.managed.local.ManagedDataSource;
import org.apache.openejb.resource.jdbc.managed.xa.ManagedXADataSource;
import org.apache.openejb.resource.jdbc.pool.DataSourceCreator;
import org.apache.xbean.recipe.ObjectRecipe;

public class SimpleDataSourceCreator implements DataSourceCreator {
   public DataSource managed(String name, CommonDataSource ds) {
      TransactionManager transactionManager = OpenEJB.getTransactionManager();
      return (DataSource)(XADataSource.class.isInstance(ds) ? new ManagedXADataSource((CommonDataSource)XADataSource.class.cast(ds), transactionManager, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class)) : new ManagedDataSource((DataSource)DataSource.class.cast(ds), transactionManager, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class)));
   }

   public DataSource poolManaged(String name, DataSource ds, Properties properties) {
      throw new UnsupportedOperationException("pooling not supported");
   }

   public DataSource pool(String name, DataSource ds, Properties properties) {
      throw new UnsupportedOperationException("pooling not supported");
   }

   public DataSource poolManagedWithRecovery(String name, XAResourceWrapper xaResourceWrapper, String driver, Properties properties) {
      throw new UnsupportedOperationException("pooling not supported");
   }

   public DataSource poolManaged(String name, String driver, Properties properties) {
      throw new UnsupportedOperationException("pooling not supported");
   }

   public CommonDataSource pool(String name, String driver, Properties properties) {
      throw new UnsupportedOperationException("pooling not supported");
   }

   public void destroy(Object object) throws Throwable {
   }

   public ObjectRecipe clearRecipe(Object object) {
      return null;
   }
}
