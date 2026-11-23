package org.apache.openejb.resource.jdbc.plugin;

public interface DataSourcePlugin {
   String updatedUrl(String var1);

   boolean enableUserDirHack();
}
