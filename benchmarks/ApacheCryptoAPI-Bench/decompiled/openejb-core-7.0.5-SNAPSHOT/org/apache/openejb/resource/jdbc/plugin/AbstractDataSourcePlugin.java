package org.apache.openejb.resource.jdbc.plugin;

import org.apache.openejb.loader.SystemInstance;

public abstract class AbstractDataSourcePlugin implements DataSourcePlugin {
   public boolean isActive() {
      SystemInstance systemInstance = SystemInstance.get();
      return "true".equals(systemInstance.getProperty("openejb.datasource.plugin." + this.getClass().getSimpleName().replace("DataSourcePlugin", "") + ".activaed", systemInstance.getProperty("openejb.datasource.plugin.activated", "true")));
   }

   public static boolean isActive(DataSourcePlugin helper) {
      return helper != null && (!AbstractDataSourcePlugin.class.isInstance(helper) || ((AbstractDataSourcePlugin)AbstractDataSourcePlugin.class.cast(helper)).isActive());
   }
}
