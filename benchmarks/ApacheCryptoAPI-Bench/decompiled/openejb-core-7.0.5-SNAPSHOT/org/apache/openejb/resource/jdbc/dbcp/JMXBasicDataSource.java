package org.apache.openejb.resource.jdbc.dbcp;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import org.apache.openejb.api.internal.Internal;
import org.apache.openejb.api.jmx.Description;
import org.apache.openejb.api.jmx.ManagedAttribute;
import org.apache.openejb.api.jmx.ManagedOperation;
import org.apache.openejb.monitoring.DynamicMBeanWrapper;
import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.monitoring.ObjectNameBuilder;

@Internal
@Description("describe a datasource pool")
public class JMXBasicDataSource {
   private final transient org.apache.commons.dbcp2.BasicDataSource ds;
   private transient ObjectName objectName;

   public JMXBasicDataSource(String name, org.apache.commons.dbcp2.BasicDataSource ds) {
      this.ds = ds;
      if (LocalMBeanServer.isJMXActive()) {
         this.objectName = ObjectNameBuilder.uniqueName("datasources", name, ds);
         MBeanServer server = LocalMBeanServer.get();
         LocalMBeanServer.registerSilently(new DynamicMBeanWrapper(this), this.objectName);
      }

   }

   public void unregister() {
      if (this.objectName != null) {
         try {
            LocalMBeanServer.get().unregisterMBean(this.objectName);
         } catch (Exception var2) {
         }
      }

   }

   @ManagedAttribute
   @Description("The class driver name.")
   public String getDriverClassName() {
      return this.ds.getDriverClassName();
   }

   @ManagedAttribute
   @Description("The connection URL to be passed to our JDBC driver to establish a connection.")
   public String getUrl() {
      return this.ds.getUrl();
   }

   @ManagedAttribute
   @Description("The SQL query that will be used to validate connections from this pool before returning them to the caller.")
   public String getValidationQuery() {
      return this.ds.getValidationQuery();
   }

   @ManagedAttribute
   @Description("The connection username to be passed to our JDBC driver to establish a connection.")
   public String getUsername() {
      return this.ds.getUsername();
   }

   @ManagedAttribute
   @Description("Timeout in seconds before connection validation queries fail.")
   public int getValidationQueryTimeout() {
      return this.ds.getValidationQueryTimeout();
   }

   @ManagedAttribute
   @Description("The initial number of connections that are created when the pool is started.")
   public int getInitialSize() {
      return this.ds.getInitialSize();
   }

   @ManagedAttribute
   @Description("The maximum number of active connections that can be allocated from this pool at the same time, or negative for no limit.")
   public int getMaxActive() {
      return this.ds.getMaxTotal();
   }

   @ManagedAttribute
   @Description("The maximum number of connections that can remain idle in the pool, without extra ones beingdestroyed, or negative for no limit.")
   public int getMaxIdle() {
      return this.ds.getMaxIdle();
   }

   @ManagedAttribute
   @Description("The minimum number of active connections that can remain idle in the pool, without extra ones being created when the evictor runs, or 0 to create none.")
   public int getMinIdle() {
      return this.ds.getMinIdle();
   }

   @ManagedAttribute
   @Description("The minimum number of active connections that can remain idle in the pool, without extra ones being created when the evictor runs, or 0 to create none.")
   public int getNumTestsPerEvictionRun() {
      return this.ds.getNumTestsPerEvictionRun();
   }

   @ManagedAttribute
   @Description("The minimum amount of time an object may sit idle in the pool before it is eligible for eviction by the idle object evictor (if any).")
   public long getMinEvictableIdleTimeMillis() {
      return this.ds.getMinEvictableIdleTimeMillis();
   }

   @ManagedAttribute
   @Description("The number of milliseconds to sleep between runs of the idle object evictor thread.")
   public long getTimeBetweenEvictionRunsMillis() {
      return this.ds.getTimeBetweenEvictionRunsMillis();
   }

   @ManagedAttribute
   @Description("The maximum number of open statements that can be allocated from the statement pool at the same time, or non-positive for no limit.")
   public int getMaxOpenPreparedStatements() {
      return this.ds.getMaxOpenPreparedStatements();
   }

   @ManagedAttribute
   @Description("The maximum number of milliseconds that the pool will wait (when there are no available connections) for a connection to be returned before throwing an exception, or <= 0 to wait indefinitely.")
   public long getMaxWait() {
      return this.ds.getMaxWaitMillis();
   }

   @ManagedAttribute
   @Description("The default auto-commit state of connections created by this pool.")
   public boolean getDefaultAutoCommit() {
      return Boolean.TRUE.equals(this.ds.getDefaultAutoCommit());
   }

   @ManagedAttribute
   @Description("Prepared statement pooling for this pool.")
   public boolean getPoolPreparedStatements() {
      return Boolean.TRUE.equals(this.ds.isPoolPreparedStatements());
   }

   @ManagedAttribute
   @Description("The indication of whether objects will be validated before being borrowed from the pool.")
   public boolean getTestOnBorrow() {
      return Boolean.TRUE.equals(this.ds.getTestOnBorrow());
   }

   @ManagedAttribute
   @Description("The indication of whether objects will be validated before being returned to the pool.")
   public boolean getTestOnReturn() {
      return Boolean.TRUE.equals(this.ds.getTestOnReturn());
   }

   @ManagedAttribute
   @Description("The indication of whether objects will be validated by the idle object evictor (if any).")
   public boolean getTestWhileIdle() {
      return Boolean.TRUE.equals(this.ds.getTestWhileIdle());
   }

   @ManagedAttribute
   @Description("The default \"catalog\" of connections created by this pool.")
   public String getDefaultCatalog() {
      return this.ds.getDefaultCatalog();
   }

   @ManagedAttribute
   @Description("The default read-only state of connections created by this pool.")
   public boolean getDefaultReadOnly() {
      return Boolean.TRUE.equals(this.ds.getDefaultReadOnly());
   }

   @ManagedAttribute
   @Description("The default TransactionIsolation state of connections created by this pool.")
   public int getDefaultTransactionIsolation() {
      return this.ds.getDefaultTransactionIsolation();
   }

   @ManagedAttribute
   @Description("The number of active connections in the pool.")
   public int getNumActive() {
      return this.ds.getNumActive();
   }

   @ManagedAttribute
   @Description("The number of idle connections in the pool.")
   public int getNumIdle() {
      return this.ds.getNumIdle();
   }

   @ManagedOperation
   @Description("Execute the validation query.")
   public String executeValidationQuery() {
      String query = this.ds.getValidationQuery();
      if (query != null && !query.trim().isEmpty()) {
         Connection conn;
         try {
            conn = this.ds.getConnection();
         } catch (SQLException e) {
            return e.getMessage();
         }

         Statement statement = null;

         String var4;
         try {
            statement = conn.createStatement();
            if (!statement.execute(query)) {
               var4 = "KO";
               return var4;
            }

            var4 = "OK";
         } catch (SQLException e) {
            String var5 = e.getMessage();
            return var5;
         } finally {
            if (statement != null) {
               try {
                  statement.close();
               } catch (SQLException var21) {
               }
            }

            if (conn != null) {
               try {
                  conn.close();
               } catch (SQLException var20) {
               }
            }

         }

         return var4;
      } else {
         return "no validation query defined";
      }
   }

   @ManagedAttribute
   @Description("Set the class driver.")
   public void setDriverClassName(String name) {
      this.ds.setDriverClassName(name);
   }

   @ManagedAttribute
   @Description("Set the connection URL.")
   public void setUrl(String url) {
      this.ds.setUrl(url);
   }

   @ManagedAttribute
   @Description("Set the SQL validation query.")
   public void setValidationQuery(String query) {
      this.ds.setValidationQuery(query);
   }

   @ManagedAttribute
   @Description("Set the connection username to be passed to our JDBC driver to establish a connection.")
   public void setUsername(String user) {
      this.ds.setUsername(user);
   }

   @ManagedAttribute
   @Description("Set the timeout in seconds before connection validation queries fail.")
   public void setValidationQueryTimeout(int timeout) {
      this.ds.setValidationQueryTimeout(timeout);
   }

   @ManagedAttribute
   @Description("Set the initial number of connections that are created when the pool is started.")
   public void setInitialSize(int size) {
      this.ds.setInitialSize(size);
   }

   @ManagedAttribute
   @Description("Set the maximum number of active connections that can be allocated from this pool at the same time, or negative for no limit.")
   public void setMaxActive(int max) {
      this.ds.setMaxTotal(max);
   }

   @ManagedAttribute
   @Description("Set the maximum number of connections that can remain idle in the pool, without extra ones beingdestroyed, or negative for no limit.")
   public void setMaxIdle(int max) {
      this.ds.setMaxIdle(max);
   }

   @ManagedAttribute
   @Description("Set the minimum number of active connections that can remain idle in the pool, without extra ones being created when the evictor runs, or 0 to create none.")
   public void setMinIdle(int min) {
      this.ds.setMinIdle(min);
   }

   @ManagedAttribute
   @Description("Set the minimum number of active connections that can remain idle in the pool, without extra ones being created when the evictor runs, or 0 to create none.")
   public void setNumTestsPerEvictionRun(int num) {
      this.ds.setNumTestsPerEvictionRun(num);
   }

   @ManagedAttribute
   @Description("Set the minimum amount of time an object may sit idle in the pool before it is eligible for eviction by the idle object evictor (if any).")
   public void setMinEvictableIdleTimeMillis(long time) {
      this.ds.setMinEvictableIdleTimeMillis(time);
   }

   @ManagedAttribute
   @Description("Set the number of milliseconds to sleep between runs of the idle object evictor thread.")
   public void setTimeBetweenEvictionRunsMillis(long time) {
      this.ds.setTimeBetweenEvictionRunsMillis(time);
   }

   @ManagedAttribute
   @Description("Set the maximum number of open statements that can be allocated from the statement pool at the same time, or non-positive for no limit.")
   public void setMaxOpenPreparedStatements(int max) {
      this.ds.setMaxOpenPreparedStatements(max);
   }

   @ManagedAttribute
   @Description("Set the maximum number of milliseconds that the pool will wait (when there are no available connections) for a connection to be returned before throwing an exception, or <= 0 to wait indefinitely.")
   public void setMaxWait(long max) {
      this.ds.setMaxWaitMillis(max);
   }

   @ManagedAttribute
   @Description("Set the default auto-commit state of connections created by this pool.")
   public void setDefaultAutoCommit(boolean auto) {
      this.ds.setDefaultAutoCommit(auto);
   }

   @ManagedAttribute
   @Description("Set the prepared statement pooling for this pool.")
   public void setPoolPreparedStatements(boolean pool) {
      this.ds.setPoolPreparedStatements(pool);
   }

   @ManagedAttribute
   @Description("Set the indication of whether objects will be validated before being borrowed from the pool.")
   public void setTestOnBorrow(boolean test) {
      this.ds.setTestOnBorrow(test);
   }

   @ManagedAttribute
   @Description("Set the indication of whether objects will be validated before being returned to the pool.")
   public void setTestOnReturn(boolean test) {
      this.ds.setTestOnReturn(test);
   }

   @ManagedAttribute
   @Description("Set the indication of whether objects will be validated by the idle object evictor (if any).")
   public void setTestWhileIdle(boolean test) {
      this.ds.setTestWhileIdle(test);
   }

   @ManagedAttribute
   @Description("The default \"catalog\" of connections created by this pool.")
   public void setDefaultCatalog(String catalog) {
      this.ds.setDefaultCatalog(catalog);
   }

   @ManagedAttribute
   @Description("Set the default read-only state of connections created by this pool.")
   public void setDefaultReadOnly(boolean ro) {
      this.ds.setDefaultReadOnly(ro);
   }

   @ManagedAttribute
   @Description("Set the default TransactionIsolation state of connections created by this pool ([NONE: 0, READ_COMMITTED: 2, READ_UNCOMMITTED: 4, SERIALIZABLE: 8]).")
   public void setDefaultTransactionIsolation(int level) {
      this.ds.setDefaultTransactionIsolation(level);
   }
}
