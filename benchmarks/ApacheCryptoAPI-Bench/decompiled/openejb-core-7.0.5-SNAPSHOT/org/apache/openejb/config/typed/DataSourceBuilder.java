package org.apache.openejb.config.typed;

import java.net.URI;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.config.typed.util.Builders;
import org.apache.openejb.config.typed.util.DurationAdapter;
import org.apache.openejb.util.Duration;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(
   name = "DataSource"
)
public class DataSourceBuilder extends Resource {
   @XmlAttribute
   private String serviceId;
   @XmlAttribute
   private String definition;
   @XmlAttribute
   private boolean jtaManaged = true;
   @XmlAttribute
   private String jdbcDriver = "org.hsqldb.jdbcDriver";
   @XmlAttribute
   private URI jdbcUrl = URI.create("jdbc:hsqldb:mem:hsqldb");
   @XmlAttribute
   private String userName = "sa";
   @XmlAttribute
   private String password;
   @XmlAttribute
   private String passwordCipher = "PlainText";
   @XmlAttribute
   private String connectionProperties;
   @XmlAttribute
   private boolean defaultAutoCommit = true;
   @XmlAttribute
   private String defaultReadOnly;
   @XmlAttribute
   private int initialSize;
   @XmlAttribute
   private int maxActive = 20;
   @XmlAttribute
   private int maxIdle = 20;
   @XmlAttribute
   private int minIdle;
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration maxWaitTime = Duration.parse("-1 millisecond");
   @XmlAttribute
   private String validationQuery;
   @XmlAttribute
   private boolean testOnBorrow = true;
   @XmlAttribute
   private boolean testOnReturn;
   @XmlAttribute
   private boolean testWhileIdle;
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration timeBetweenEvictionRuns = Duration.parse("-1 millisecond");
   @XmlAttribute
   private int numTestsPerEvictionRun = 3;
   @XmlJavaTypeAdapter(DurationAdapter.class)
   @XmlAttribute
   private Duration minEvictableIdleTime = Duration.parse("30 minutes");
   @XmlAttribute
   private boolean poolPreparedStatements;
   @XmlAttribute
   private int maxOpenPreparedStatements;
   @XmlAttribute
   private boolean accessToUnderlyingConnectionAllowed;
   @XmlAttribute
   private boolean ignoreDefaultValues;

   public DataSourceBuilder() {
      this.setClassName("org.apache.openejb.resource.jdbc.DataSourceFactory");
      this.setType("javax.sql.DataSource");
      this.setId("DataSource");
      this.setConstructor("serviceId, jtaManaged, jdbcDriver, definition, maxWaitTime, timeBetweenEvictionRuns, minEvictableIdleTime, OpenEJBResourceClasspath");
      this.setFactoryName("create");
   }

   public DataSourceBuilder id(String id) {
      this.setId(id);
      return this;
   }

   public DataSourceBuilder withServiceId(String serviceId) {
      this.serviceId = serviceId;
      return this;
   }

   public void setServiceId(String serviceId) {
      this.serviceId = serviceId;
   }

   public String getServiceId() {
      return this.serviceId;
   }

   public DataSourceBuilder withDefinition(String definition) {
      this.definition = definition;
      return this;
   }

   public void setDefinition(String definition) {
      this.definition = definition;
   }

   public String getDefinition() {
      return this.definition;
   }

   public DataSourceBuilder withJtaManaged(boolean jtaManaged) {
      this.jtaManaged = jtaManaged;
      return this;
   }

   public void setJtaManaged(boolean jtaManaged) {
      this.jtaManaged = jtaManaged;
   }

   public boolean getJtaManaged() {
      return this.jtaManaged;
   }

   public DataSourceBuilder withJdbcDriver(String jdbcDriver) {
      this.jdbcDriver = jdbcDriver;
      return this;
   }

   public void setJdbcDriver(String jdbcDriver) {
      this.jdbcDriver = jdbcDriver;
   }

   public String getJdbcDriver() {
      return this.jdbcDriver;
   }

   public DataSourceBuilder withJdbcUrl(URI jdbcUrl) {
      this.jdbcUrl = jdbcUrl;
      return this;
   }

   public void setJdbcUrl(URI jdbcUrl) {
      this.jdbcUrl = jdbcUrl;
   }

   public URI getJdbcUrl() {
      return this.jdbcUrl;
   }

   public DataSourceBuilder withUserName(String userName) {
      this.userName = userName;
      return this;
   }

   public void setUserName(String userName) {
      this.userName = userName;
   }

   public String getUserName() {
      return this.userName;
   }

   public DataSourceBuilder withPassword(String password) {
      this.password = password;
      return this;
   }

   public void setPassword(String password) {
      this.password = password;
   }

   public String getPassword() {
      return this.password;
   }

   public DataSourceBuilder withPasswordCipher(String passwordCipher) {
      this.passwordCipher = passwordCipher;
      return this;
   }

   public void setPasswordCipher(String passwordCipher) {
      this.passwordCipher = passwordCipher;
   }

   public String getPasswordCipher() {
      return this.passwordCipher;
   }

   public DataSourceBuilder withConnectionProperties(String connectionProperties) {
      this.connectionProperties = connectionProperties;
      return this;
   }

   public void setConnectionProperties(String connectionProperties) {
      this.connectionProperties = connectionProperties;
   }

   public String getConnectionProperties() {
      return this.connectionProperties;
   }

   public DataSourceBuilder withDefaultAutoCommit(boolean defaultAutoCommit) {
      this.defaultAutoCommit = defaultAutoCommit;
      return this;
   }

   public void setDefaultAutoCommit(boolean defaultAutoCommit) {
      this.defaultAutoCommit = defaultAutoCommit;
   }

   public boolean getDefaultAutoCommit() {
      return this.defaultAutoCommit;
   }

   public DataSourceBuilder withDefaultReadOnly(String defaultReadOnly) {
      this.defaultReadOnly = defaultReadOnly;
      return this;
   }

   public void setDefaultReadOnly(String defaultReadOnly) {
      this.defaultReadOnly = defaultReadOnly;
   }

   public String getDefaultReadOnly() {
      return this.defaultReadOnly;
   }

   public DataSourceBuilder withInitialSize(int initialSize) {
      this.initialSize = initialSize;
      return this;
   }

   public void setInitialSize(int initialSize) {
      this.initialSize = initialSize;
   }

   public int getInitialSize() {
      return this.initialSize;
   }

   public DataSourceBuilder withMaxActive(int maxActive) {
      this.maxActive = maxActive;
      return this;
   }

   public void setMaxActive(int maxActive) {
      this.maxActive = maxActive;
   }

   public int getMaxActive() {
      return this.maxActive;
   }

   public DataSourceBuilder withMaxIdle(int maxIdle) {
      this.maxIdle = maxIdle;
      return this;
   }

   public void setMaxIdle(int maxIdle) {
      this.maxIdle = maxIdle;
   }

   public int getMaxIdle() {
      return this.maxIdle;
   }

   public DataSourceBuilder withMinIdle(int minIdle) {
      this.minIdle = minIdle;
      return this;
   }

   public void setMinIdle(int minIdle) {
      this.minIdle = minIdle;
   }

   public int getMinIdle() {
      return this.minIdle;
   }

   public DataSourceBuilder withMaxWaitTime(Duration maxWaitTime) {
      this.maxWaitTime = maxWaitTime;
      return this;
   }

   public void setMaxWaitTime(Duration maxWaitTime) {
      this.maxWaitTime = maxWaitTime;
   }

   public Duration getMaxWaitTime() {
      return this.maxWaitTime;
   }

   public DataSourceBuilder withMaxWaitTime(long time, TimeUnit unit) {
      return this.withMaxWaitTime(new Duration(time, unit));
   }

   public void setMaxWaitTime(long time, TimeUnit unit) {
      this.setMaxWaitTime(new Duration(time, unit));
   }

   public DataSourceBuilder withValidationQuery(String validationQuery) {
      this.validationQuery = validationQuery;
      return this;
   }

   public void setValidationQuery(String validationQuery) {
      this.validationQuery = validationQuery;
   }

   public String getValidationQuery() {
      return this.validationQuery;
   }

   public DataSourceBuilder withTestOnBorrow(boolean testOnBorrow) {
      this.testOnBorrow = testOnBorrow;
      return this;
   }

   public void setTestOnBorrow(boolean testOnBorrow) {
      this.testOnBorrow = testOnBorrow;
   }

   public boolean getTestOnBorrow() {
      return this.testOnBorrow;
   }

   public DataSourceBuilder withTestOnReturn(boolean testOnReturn) {
      this.testOnReturn = testOnReturn;
      return this;
   }

   public void setTestOnReturn(boolean testOnReturn) {
      this.testOnReturn = testOnReturn;
   }

   public boolean getTestOnReturn() {
      return this.testOnReturn;
   }

   public DataSourceBuilder withTestWhileIdle(boolean testWhileIdle) {
      this.testWhileIdle = testWhileIdle;
      return this;
   }

   public void setTestWhileIdle(boolean testWhileIdle) {
      this.testWhileIdle = testWhileIdle;
   }

   public boolean getTestWhileIdle() {
      return this.testWhileIdle;
   }

   public DataSourceBuilder withTimeBetweenEvictionRuns(Duration timeBetweenEvictionRuns) {
      this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
      return this;
   }

   public void setTimeBetweenEvictionRuns(Duration timeBetweenEvictionRuns) {
      this.timeBetweenEvictionRuns = timeBetweenEvictionRuns;
   }

   public Duration getTimeBetweenEvictionRuns() {
      return this.timeBetweenEvictionRuns;
   }

   public DataSourceBuilder withTimeBetweenEvictionRuns(long time, TimeUnit unit) {
      return this.withTimeBetweenEvictionRuns(new Duration(time, unit));
   }

   public void setTimeBetweenEvictionRuns(long time, TimeUnit unit) {
      this.setTimeBetweenEvictionRuns(new Duration(time, unit));
   }

   public DataSourceBuilder withNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
      this.numTestsPerEvictionRun = numTestsPerEvictionRun;
      return this;
   }

   public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
      this.numTestsPerEvictionRun = numTestsPerEvictionRun;
   }

   public int getNumTestsPerEvictionRun() {
      return this.numTestsPerEvictionRun;
   }

   public DataSourceBuilder withMinEvictableIdleTime(Duration minEvictableIdleTime) {
      this.minEvictableIdleTime = minEvictableIdleTime;
      return this;
   }

   public void setMinEvictableIdleTime(Duration minEvictableIdleTime) {
      this.minEvictableIdleTime = minEvictableIdleTime;
   }

   public Duration getMinEvictableIdleTime() {
      return this.minEvictableIdleTime;
   }

   public DataSourceBuilder withMinEvictableIdleTime(long time, TimeUnit unit) {
      return this.withMinEvictableIdleTime(new Duration(time, unit));
   }

   public void setMinEvictableIdleTime(long time, TimeUnit unit) {
      this.setMinEvictableIdleTime(new Duration(time, unit));
   }

   public DataSourceBuilder withPoolPreparedStatements(boolean poolPreparedStatements) {
      this.poolPreparedStatements = poolPreparedStatements;
      return this;
   }

   public void setPoolPreparedStatements(boolean poolPreparedStatements) {
      this.poolPreparedStatements = poolPreparedStatements;
   }

   public boolean getPoolPreparedStatements() {
      return this.poolPreparedStatements;
   }

   public DataSourceBuilder withMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
      this.maxOpenPreparedStatements = maxOpenPreparedStatements;
      return this;
   }

   public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
      this.maxOpenPreparedStatements = maxOpenPreparedStatements;
   }

   public int getMaxOpenPreparedStatements() {
      return this.maxOpenPreparedStatements;
   }

   public DataSourceBuilder withAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
      this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
      return this;
   }

   public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
      this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
   }

   public boolean getAccessToUnderlyingConnectionAllowed() {
      return this.accessToUnderlyingConnectionAllowed;
   }

   public DataSourceBuilder withIgnoreDefaultValues(boolean ignoreDefaultValues) {
      this.ignoreDefaultValues = ignoreDefaultValues;
      return this;
   }

   public void setIgnoreDefaultValues(boolean ignoreDefaultValues) {
      this.ignoreDefaultValues = ignoreDefaultValues;
   }

   public boolean getIgnoreDefaultValues() {
      return this.ignoreDefaultValues;
   }

   public Properties getProperties() {
      return Builders.getProperties(this);
   }
}
