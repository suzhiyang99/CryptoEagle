package org.apache.openejb.assembler;

import java.io.File;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.loader.SystemInstance;

public interface WebAppDeployer {
   AppInfo deploy(String var1, String var2, File var3);

   void reload(String var1);

   public static final class Helper {
      private Helper() {
      }

      public static boolean isWebApp(File file) {
         return (file.getName().endsWith(".war") || (new File(file, "WEB-INF")).exists()) && SystemInstance.get().getComponent(WebAppDeployer.class) != null;
      }
   }
}
