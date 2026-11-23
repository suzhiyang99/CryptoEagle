package org.apache.openejb;

import org.apache.openejb.loader.SystemInstance;

public class EnvProps {
   public static final String CONFIGURATION = "org/openejb/configuration_source";
   public static final String ASSEMBLER = "org/openejb/assembler_class";
   public static final String CONFIGURATION_FACTORY = "org/openejb/configuration_factory";
   public static final String DEPLOYED_RAR_DIRECTORY = "org/openejb/assembler/rar_directory";
   public static final String DEPLOYED_JAR_DIRECTORY = "org/openejb/assembler/jar_directory";
   public static final String AUTO_CONFIGURE = "openejb.extract.configuration";

   public static boolean extractConfigurationFiles() {
      return SystemInstance.get().getOptions().get("openejb.extract.configuration", true);
   }
}
