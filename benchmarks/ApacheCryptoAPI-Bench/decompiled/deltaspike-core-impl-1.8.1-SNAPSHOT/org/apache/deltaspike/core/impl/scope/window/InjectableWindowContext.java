package org.apache.deltaspike.core.impl.scope.window;

import java.lang.annotation.Annotation;
import javax.enterprise.inject.Typed;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.impl.scope.DeltaSpikeContextExtension;
import org.apache.deltaspike.core.spi.scope.window.WindowContext;

@Typed
public class InjectableWindowContext implements WindowContext {
   private static final long serialVersionUID = -3606786361833889628L;
   private transient volatile WindowContext windowContext;

   InjectableWindowContext(WindowContext windowContext) {
      this.windowContext = windowContext;
   }

   private WindowContext getWindowContext() {
      if (this.windowContext == null) {
         this.windowContext = ((DeltaSpikeContextExtension)BeanProvider.getContextualReference(DeltaSpikeContextExtension.class, new Annotation[0])).getWindowContext();
      }

      return this.windowContext;
   }

   public String getCurrentWindowId() {
      return this.getWindowContext().getCurrentWindowId();
   }

   public void activateWindow(String windowId) {
      this.getWindowContext().activateWindow(windowId);
   }

   public boolean closeWindow(String windowId) {
      return this.getWindowContext().closeWindow(windowId);
   }
}
