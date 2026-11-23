package org.apache.openejb.resource.jdbc.pool;

import java.util.Properties;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import org.apache.openejb.resource.XAResourceWrapper;
import org.apache.openejb.resource.jdbc.dbcp.BasicDataSource;
import org.apache.openejb.resource.jdbc.dbcp.BasicManagedDataSource;
import org.apache.openejb.resource.jdbc.dbcp.DbcpDataSource;
import org.apache.openejb.resource.jdbc.dbcp.DbcpDataSourceCreator;
import org.apache.openejb.resource.jdbc.dbcp.DbcpManagedDataSource;
import org.apache.openejb.resource.jdbc.dbcp.ManagedDataSourceWithRecovery;

public class DefaultDataSourceCreator extends DbcpDataSourceCreator {
   public DataSource managed(String name, CommonDataSource ds) {
      return new DbcpManagedDataSource(name, ds);
   }

   public DataSource poolManaged(String name, DataSource ds, Properties properties) {
      return (DataSource)this.build(DbcpManagedDataSource.class, new DbcpManagedDataSource(name, ds), properties);
   }

   public DataSource poolManaged(String name, String driver, Properties properties) {
      BasicManagedDataSource ds = new BasicManagedDataSource(name);
      ds.setDriverClassName(driver);
      this.build(BasicManagedDataSource.class, ds, properties);
      return ds;
   }

   public DataSource poolManagedWithRecovery(String name, XAResourceWrapper xaResourceWrapper, String driver, Properties properties) {
      BasicManagedDataSource ds = new ManagedDataSourceWithRecovery(name, xaResourceWrapper);
      ds.setDriverClassName(driver);
      this.build(BasicManagedDataSource.class, ds, properties);
      return ds;
   }

   public DataSource pool(String name, DataSource ds, Properties properties) {
      return (DataSource)this.build(DbcpDataSource.class, new DbcpDataSource(name, ds), properties);
   }

   public DataSource pool(String name, String driver, Properties properties) {
      BasicDataSource ds = new BasicDataSource(name);
      ds.setDriverClassName(driver);
      this.build(BasicManagedDataSource.class, ds, properties);
      return ds;
   }
}
