package org.spark_project.jetty.util.preventers;

import java.security.Security;

public class SecurityProviderLeakPreventer extends AbstractLeakPreventer {
   public void prevent(ClassLoader loader) {
      Security.getProviders();
   }
}
