package org.apache.openejb.resource.jdbc;

import java.lang.reflect.Method;
import javax.sql.CommonDataSource;

public final class DataSourceHelper {
   private DataSourceHelper() {
   }

   public static void setUrl(CommonDataSource dataSource, String url, ClassLoader classLoader, String clazz, String method) throws Exception {
      Class<?> loadedClass = classLoader.loadClass(clazz);
      Method setUrl = loadedClass.getMethod(method, String.class);
      setUrl.setAccessible(true);
      setUrl.invoke(dataSource, url);
   }

   public static void setUrl(CommonDataSource dataSource, String url) throws Exception {
      if (url.contains("jdbc:derby:")) {
         setUrl(dataSource, url.replace("jdbc:derby:", ""), dataSource.getClass().getClassLoader(), "org.apache.derby.jdbc.EmbeddedDataSource", "setDatabaseName");
      } else {
         setUrl(dataSource, url, dataSource.getClass().getClassLoader(), "org.hsqldb.jdbc.JDBCDataSource", "setDatabase");
      }

   }
}
