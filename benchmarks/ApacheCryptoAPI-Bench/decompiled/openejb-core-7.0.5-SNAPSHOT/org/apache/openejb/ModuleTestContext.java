package org.apache.openejb;

import java.net.URI;
import javax.naming.Context;

public class ModuleTestContext extends ModuleContext {
   private Context moduleJndiContextOverride;

   public ModuleTestContext(String id, URI moduleURI, String uniqueId, AppContext appContext, Context moduleJndiContext, ClassLoader classLoader) {
      super(id, moduleURI, uniqueId, appContext, moduleJndiContext, classLoader);
   }

   public void setModuleJndiContextOverride(Context moduleJndiContextOverride) {
      this.moduleJndiContextOverride = moduleJndiContextOverride;
   }

   public Context getModuleJndiContext() {
      return this.moduleJndiContextOverride == null ? super.getModuleJndiContext() : this.moduleJndiContextOverride;
   }
}
