package org.apache.openejb;

import java.net.URI;
import javax.naming.Context;

public class ModuleContext extends DeploymentContext {
   private final AppContext appContext;
   private final Context moduleJndiContext;
   private final String uniqueId;
   private final URI moduleURI;
   private final ClassLoader loader;

   public ModuleContext(String id, URI moduleURI, String uniqueId, AppContext appContext, Context moduleJndiContext, ClassLoader classLoader) {
      super(id, appContext.getOptions());
      this.moduleURI = moduleURI;
      this.appContext = appContext;
      this.moduleJndiContext = moduleJndiContext;
      this.uniqueId = uniqueId;
      if (classLoader != null) {
         this.loader = classLoader;
      } else {
         this.loader = Thread.currentThread().getContextClassLoader();
      }

   }

   public AppContext getAppContext() {
      return this.appContext;
   }

   public ClassLoader getClassLoader() {
      return this.loader;
   }

   public Context getModuleJndiContext() {
      return this.moduleJndiContext;
   }

   public String getUniqueId() {
      return this.uniqueId;
   }

   public URI getModuleURI() {
      return this.moduleURI;
   }
}
