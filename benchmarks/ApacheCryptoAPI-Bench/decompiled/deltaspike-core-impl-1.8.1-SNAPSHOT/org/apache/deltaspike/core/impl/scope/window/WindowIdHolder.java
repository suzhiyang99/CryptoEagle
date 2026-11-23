package org.apache.deltaspike.core.impl.scope.window;

import javax.enterprise.context.RequestScoped;

@RequestScoped
public class WindowIdHolder {
   private String windowId;

   public String getWindowId() {
      return this.windowId;
   }

   public void setWindowId(String windowId) {
      this.windowId = windowId;
   }
}
