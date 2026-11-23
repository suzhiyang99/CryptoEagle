package org.apache.openejb.resource.jdbc.dbcp;

import java.lang.reflect.Field;
import java.sql.SQLException;
import javax.sql.CommonDataSource;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DataSourceConnectionFactory;
import org.apache.commons.dbcp2.managed.DataSourceXAConnectionFactory;
import org.apache.commons.dbcp2.managed.LocalXAConnectionFactory;
import org.apache.commons.dbcp2.managed.TransactionRegistry;
import org.apache.commons.dbcp2.managed.XAConnectionFactory;
import org.apache.openejb.resource.jdbc.DataSourceHelper;

public class DbcpManagedDataSource extends BasicManagedDataSource {
   private final CommonDataSource ds;

   public DbcpManagedDataSource(String name, CommonDataSource dataSource) {
      super(name);
      this.ds = dataSource;
      if (XADataSource.class.isInstance(dataSource)) {
         this.setXaDataSourceInstance((XADataSource)XADataSource.class.cast(this.ds));
      }

   }

   public void setJdbcUrl(String url) {
      try {
         DataSourceHelper.setUrl(this.ds, url);
      } catch (Throwable var3) {
         super.setUrl(url);
      }

   }

   protected ConnectionFactory createConnectionFactory() throws SQLException {
      if (this.ds instanceof XADataSource) {
         XADataSource xaDataSourceInstance = (XADataSource)this.ds;
         XAConnectionFactory xaConnectionFactory = new DataSourceXAConnectionFactory(this.getTransactionManager(), xaDataSourceInstance, this.getUsername(), this.getPassword());
         this.setTransactionRegistry(xaConnectionFactory.getTransactionRegistry());
         return xaConnectionFactory;
      } else {
         ConnectionFactory connectionFactory = new DataSourceConnectionFactory((DataSource)DataSource.class.cast(this.ds), this.getUsername(), this.getPassword());
         XAConnectionFactory xaConnectionFactory = new LocalXAConnectionFactory(this.getTransactionManager(), connectionFactory);
         this.setTransactionRegistry(xaConnectionFactory.getTransactionRegistry());
         return xaConnectionFactory;
      }
   }

   public void setTransactionRegistry(TransactionRegistry registry) {
      try {
         Field field = org.apache.commons.dbcp2.managed.BasicManagedDataSource.class.getDeclaredField("transactionRegistry");
         field.setAccessible(true);
         field.set(this, registry);
      } catch (Throwable e) {
         throw new IllegalStateException(e);
      }
   }
}
