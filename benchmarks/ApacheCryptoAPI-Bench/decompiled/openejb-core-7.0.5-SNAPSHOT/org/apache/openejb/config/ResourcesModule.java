package org.apache.openejb.config;

import java.io.File;
import java.net.URI;
import java.util.Set;

public class ResourcesModule extends Module implements DeploymentModule {
   public String getModuleId() {
      return null;
   }

   public URI getModuleUri() {
      return null;
   }

   public String getJarLocation() {
      return null;
   }

   public File getFile() {
      return null;
   }

   public ValidationContext getValidation() {
      return null;
   }

   public Set<String> getWatchedResources() {
      return null;
   }

   public AppModule appModule() {
      return super.getAppModule();
   }
}
