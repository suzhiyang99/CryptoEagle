package org.apache.openejb.resource.jdbc.dbcp;

import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.dbcp2.ConnectionFactory;
import org.apache.commons.dbcp2.DataSourceConnectionFactory;
import org.apache.openejb.resource.jdbc.DataSourceHelper;

public class DbcpDataSource extends BasicDataSource {
   protected final DataSource ds;

   public DbcpDataSource(String name, DataSource dataSource) {
      super(name);
      this.ds = dataSource;
   }

   protected ConnectionFactory createConnectionFactory() throws SQLException {
      return new DataSourceConnectionFactory(this.ds, this.getUsername(), this.getPassword());
   }

   public void setJdbcUrl(String url) {
      try {
         DataSourceHelper.setUrl(this.ds, url);
      } catch (Throwable var3) {
         super.setUrl(url);
      }

   }
}
