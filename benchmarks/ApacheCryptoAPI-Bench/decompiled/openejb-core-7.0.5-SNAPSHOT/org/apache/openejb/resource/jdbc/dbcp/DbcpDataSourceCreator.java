package org.apache.openejb.resource.jdbc.dbcp;

import java.util.Properties;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.jdbc.managed.local.ManagedDataSource;
import org.apache.openejb.resource.jdbc.managed.xa.ManagedXADataSource;
import org.apache.openejb.resource.jdbc.pool.PoolDataSourceCreator;
import org.apache.openejb.resource.jdbc.pool.XADataSourceResource;

public class DbcpDataSourceCreator extends PoolDataSourceCreator {
   public DataSource pool(String name, DataSource ds, Properties properties) {
      return (DataSource)this.build(DbcpDataSource.class, new DbcpDataSource(name, ds), properties);
   }

   public DataSource managed(String name, CommonDataSource ds) {
      TransactionManager transactionManager = OpenEJB.getTransactionManager();
      return (DataSource)(ds instanceof XADataSource ? new ManagedXADataSource(ds, transactionManager, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class)) : new ManagedDataSource((DataSource)DataSource.class.cast(ds), transactionManager, (TransactionSynchronizationRegistry)SystemInstance.get().getComponent(TransactionSynchronizationRegistry.class)));
   }

   public CommonDataSource pool(String name, String driver, Properties properties) {
      properties.setProperty("name", name);
      String xa = (String)String.class.cast(properties.remove("XaDataSource"));
      if (xa == null && !properties.containsKey("JdbcDriver")) {
         properties.setProperty("driverClassName", driver);
      }

      BasicDataSource ds = (BasicDataSource)this.build(BasicDataSource.class, properties);
      ds.setDriverClassName(driver);
      if (xa != null) {
         ds.setDelegate(XADataSourceResource.proxy(Thread.currentThread().getContextClassLoader(), xa));
      }

      return ds;
   }

   protected void doDestroy(CommonDataSource dataSource) throws Throwable {
      ((org.apache.commons.dbcp2.BasicDataSource)dataSource).close();
   }

   protected <T> T build(Class<T> clazz, Properties properties) {
      T object = (T)super.build(clazz, properties);
      this.setDriverLoader(object);
      return object;
   }

   protected <T> T build(Class<T> clazz, Object instance, Properties properties) {
      T object = (T)super.build(clazz, instance, properties);
      this.setDriverLoader(object);
      return object;
   }

   private <T> void setDriverLoader(T object) {
      if (org.apache.commons.dbcp2.BasicDataSource.class.isInstance(object)) {
         org.apache.commons.dbcp2.BasicDataSource basicDataSource = (org.apache.commons.dbcp2.BasicDataSource)object;
         ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
         basicDataSource.setDriverClassLoader(contextClassLoader);
      }

   }
}
