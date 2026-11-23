package org.apache.openejb.resource.jdbc.plugin;

import java.io.File;
import java.io.IOException;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.xbean.finder.ResourceFinder;

public class InstantdbDataSourcePlugin extends AbstractDataSourcePlugin {
   public String updatedUrl(String jdbcUrl) {
      String prefix = "jdbc:idb:";
      int index = jdbcUrl.indexOf("jdbc:idb:");
      if (index == -1) {
         return jdbcUrl;
      } else {
         String confFile = jdbcUrl.substring(index + "jdbc:idb:".length());
         File base = SystemInstance.get().getBase().getDirectory();
         File file = new File(base, confFile);
         if (file.exists()) {
            return jdbcUrl;
         } else if (!file.getParentFile().exists()) {
            return jdbcUrl;
         } else {
            try {
               ResourceFinder finder = new ResourceFinder("");
               String defaultProperties = finder.findString("default.instantdb.properties");
               IO.copy(defaultProperties.getBytes(), file);
            } catch (IOException e) {
               e.printStackTrace();
            }

            return jdbcUrl;
         }
      }
   }

   public boolean enableUserDirHack() {
      return isActive(this);
   }
}
