package org.apache.deltaspike.core.impl.scope.window;

import java.io.Serializable;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import org.apache.deltaspike.core.spi.scope.window.WindowContext;

@RequestScoped
public class WindowContextQuotaHandlerCache implements Serializable {
   private String checkedWindowId;
   private String windowIdToRemove;
   @Inject
   private WindowContext windowContext;

   public boolean cacheWindowId(String currentWindowId) {
      boolean result = currentWindowId.equals(this.checkedWindowId);
      this.checkedWindowId = currentWindowId;
      return result;
   }

   public void setWindowIdToDestroy(String windowIdToRemove) {
      this.windowIdToRemove = windowIdToRemove;
   }

   @PreDestroy
   public void cleanup() {
      if (this.windowIdToRemove != null) {
         this.windowContext.closeWindow(this.windowIdToRemove);
      }

   }
}
