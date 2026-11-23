package org.apache.openejb.resource.jdbc.plugin;

import java.io.File;
import org.apache.openejb.loader.SystemInstance;

public class HsqldbDataSourcePlugin extends AbstractDataSourcePlugin {
   private static final String HSQL_FILE_URL = "jdbc:hsqldb:file:";

   public static String toAbsolutePath(String url) {
      if (url != null && url.startsWith("jdbc:hsqldb:file:")) {
         String path = url.substring("jdbc:hsqldb:file:".length());
         File file = new File(path);
         if (!file.isAbsolute()) {
            File base = SystemInstance.get().getBase().getDirectory();
            file = new File(base, path);
         }

         path = file.getAbsolutePath();
         return "jdbc:hsqldb:file:" + path;
      } else {
         return url;
      }
   }

   public String updatedUrl(String dataSourceUrl) {
      return toAbsolutePath(dataSourceUrl);
   }

   public boolean enableUserDirHack() {
      return false;
   }
}
