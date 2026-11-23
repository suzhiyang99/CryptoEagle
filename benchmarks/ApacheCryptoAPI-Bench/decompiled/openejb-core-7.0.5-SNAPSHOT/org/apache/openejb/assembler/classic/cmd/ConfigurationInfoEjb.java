package org.apache.openejb.assembler.classic.cmd;

import java.io.File;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import org.apache.openejb.assembler.classic.OpenEjbConfiguration;
import org.apache.openejb.loader.SystemInstance;

@Singleton(
   name = "openejb/ConfigurationInfo"
)
@Lock(LockType.READ)
@Remote({ConfigurationInfo.class})
public class ConfigurationInfoEjb implements ConfigurationInfo {
   public OpenEjbConfiguration getOpenEjbConfiguration(File tmpFile) throws ConfigurationInfo.UnauthorizedException {
      if (tmpFile.exists()) {
         return (OpenEjbConfiguration)SystemInstance.get().getComponent(OpenEjbConfiguration.class);
      } else {
         throw new ConfigurationInfo.UnauthorizedException("Machine not authorized to see this server's configuration properties");
      }
   }
}
