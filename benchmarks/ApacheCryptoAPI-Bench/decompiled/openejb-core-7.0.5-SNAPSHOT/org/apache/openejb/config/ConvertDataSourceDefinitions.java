package org.apache.openejb.config;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.jee.DataSource;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.KeyedCollection;
import org.apache.openejb.jee.Property;
import org.apache.openejb.util.PropertyPlaceHolderHelper;

public class ConvertDataSourceDefinitions extends BaseConvertDefinitions {
   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      List<JndiConsumer> jndiConsumers = this.collectConsumers(appModule);
      KeyedCollection<String, DataSource> dataSources = new KeyedCollection();
      KeyedCollection<String, DataSource> dataSourcesFromCompManagedBeans = new KeyedCollection();

      for(JndiConsumer consumer : jndiConsumers) {
         if (consumer != null) {
            if (consumer instanceof CompManagedBean) {
               dataSourcesFromCompManagedBeans.addAll(consumer.getDataSource());
            } else {
               dataSources.addAll(consumer.getDataSource());
            }
         }
      }

      Map<String, DataSource> dataSourcesMap = dataSources.toMap();

      for(DataSource dataSource : dataSourcesFromCompManagedBeans) {
         if (!dataSourcesMap.containsKey(dataSource.getKey())) {
            dataSources.add(dataSource);
         }
      }

      for(DataSource dataSource : dataSources) {
         appModule.getResources().add(this.toResource(dataSource));
      }

      return appModule;
   }

   private Resource toResource(DataSource datasource) {
      String name = this.cleanUpName(datasource.getName());
      Resource def = new Resource(name, javax.sql.DataSource.class.getName());
      def.setJndi(datasource.getName().replaceFirst("java:", ""));
      def.setType("javax.sql.DataSource");
      Properties p = def.getProperties();
      put(p, "JtaManaged", datasource.getTransactional());
      put(p, "InitialSize", datasource.getInitialPoolSize());
      put(p, "DefaultIsolationLevel", datasource.getIsolationLevel());
      put(p, "LoginTimeout", datasource.getLoginTimeout());
      put(p, "MinEvictableIdleTimeMillis", datasource.getMaxIdleTime());
      put(p, "MaxIdle", datasource.getMaxPoolSize());
      put(p, "MinIdle", datasource.getMinPoolSize());
      put(p, "MaxStatements", datasource.getMaxStatements());
      put(p, "Password", datasource.getPassword());
      put(p, "JdbcUrl", datasource.getUrl());
      put(p, "UserName", datasource.getUser());
      put(p, "JdbcDriver", datasource.getClassName());
      put(p, "PortNumber", datasource.getPortNumber());
      put(p, "DatabaseName", datasource.getDatabaseName());
      put(p, "Description", datasource.getDescription());
      put(p, "ServerName", datasource.getServerName());
      put(p, "Definition", this.rawDefinition(datasource));
      put(p, "Origin", "Annotation");
      this.setProperties(datasource, p);
      put(p, "JndiName", def.getJndi());
      return def;
   }

   private String rawDefinition(DataSource d) {
      try {
         Properties p = new Properties();
         put(p, "transactional", d.getTransactional());
         put(p, "initialPoolSize", d.getInitialPoolSize());
         put(p, "isolationLevel", d.getIsolationLevel());
         put(p, "loginTimeout", d.getLoginTimeout());
         put(p, "maxIdleTime", d.getMaxIdleTime());
         put(p, "maxPoolSize", d.getMaxPoolSize());
         put(p, "maxStatements", d.getMaxStatements());
         put(p, "minPoolSize", d.getMinPoolSize());
         put(p, "portNumber", d.getPortNumber());
         put(p, "databaseName", d.getDatabaseName());
         put(p, "password", d.getPassword());
         put(p, "serverName", d.getServerName());
         put(p, "url", d.getUrl());
         put(p, "user", d.getUser());
         this.setProperties(d, p);
         ByteArrayOutputStream out = new ByteArrayOutputStream();
         p.store(out, "");
         return new String(out.toByteArray());
      } catch (IOException var4) {
         throw new OpenEJBRuntimeException(String.format("Cannot canonicalize the @DataSourceDefinition %s as a properties string", d.getName()));
      }
   }

   private void setProperties(DataSource d, Properties p) {
      for(Property property : d.getProperty()) {
         String key = property.getName();
         String value = property.getValue();
         put(p, key, value);
      }

   }

   private static void put(Properties properties, String key, Object value) {
      if (key != null) {
         if (value != null) {
            properties.put(key, PropertyPlaceHolderHelper.value(String.valueOf(value)));
         }
      }
   }
}
