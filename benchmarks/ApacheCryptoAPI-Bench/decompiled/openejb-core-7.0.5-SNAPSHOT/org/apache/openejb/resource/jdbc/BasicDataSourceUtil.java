package org.apache.openejb.resource.jdbc;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import org.apache.openejb.resource.jdbc.plugin.DataSourcePlugin;
import org.apache.xbean.finder.ResourceFinder;

public final class BasicDataSourceUtil {
   private BasicDataSourceUtil() {
   }

   public static DataSourcePlugin getDataSourcePlugin(String jdbcUrl) throws SQLException {
      String vendor = getJdbcName(jdbcUrl);
      if (vendor == null) {
         return null;
      } else {
         String pluginClassName = null;

         try {
            ResourceFinder finder = new ResourceFinder("META-INF");
            Map<String, String> plugins = finder.mapAvailableStrings(DataSourcePlugin.class.getName());
            pluginClassName = (String)plugins.get(vendor);
         } catch (IOException var7) {
         }

         if (pluginClassName != null && pluginClassName.length() > 0) {
            try {
               Class pluginClass = Class.forName(pluginClassName);
               return (DataSourcePlugin)pluginClass.newInstance();
            } catch (ClassNotFoundException var5) {
               throw new SQLException("Unable to load data source helper class '" + pluginClassName + "' for database '" + vendor + "'");
            } catch (Exception e) {
               throw (SQLException)(new SQLException("Unable to create data source helper class '" + pluginClassName + "' for database '" + vendor + "'")).initCause(e);
            }
         } else {
            return null;
         }
      }
   }

   public static String getJdbcName(String jdbcUrl) {
      if (jdbcUrl == null) {
         return null;
      } else if (!jdbcUrl.startsWith("jdbc:")) {
         return null;
      } else {
         jdbcUrl = jdbcUrl.substring("jdbc:".length());
         int index = jdbcUrl.indexOf(58);
         if (index >= 0) {
            jdbcUrl = jdbcUrl.substring(0, index);
         }

         return jdbcUrl;
      }
   }
}
