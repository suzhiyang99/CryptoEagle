package org.apache.openejb.resource.jdbc.plugin;

import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.JavaSecurityManagers;

public class DerbyDataSourcePlugin extends AbstractDataSourcePlugin {
   public String updatedUrl(String dataSourceUrl) {
      JavaSecurityManagers.setSystemProperty("derby.system.home", SystemInstance.get().getProperty("derby.system.home", SystemInstance.get().getBase().getDirectory().getAbsolutePath()));
      return dataSourceUrl;
   }

   public boolean enableUserDirHack() {
      return isActive(this);
   }
}
