package org.apache.deltaspike.core.impl.scope.viewaccess;

import java.lang.annotation.Annotation;
import javax.enterprise.inject.Typed;
import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.core.impl.scope.DeltaSpikeContextExtension;
import org.apache.deltaspike.core.spi.scope.viewaccess.ViewAccessContextManager;

@Typed
class InjectableViewAccessContextManager implements ViewAccessContextManager {
   private transient volatile ViewAccessContextManager viewAccessContextManager;

   public InjectableViewAccessContextManager(ViewAccessContextManager viewAccessContextManager) {
      this.viewAccessContextManager = viewAccessContextManager;
   }

   private ViewAccessContextManager getConversationManager() {
      if (this.viewAccessContextManager == null) {
         this.viewAccessContextManager = ((DeltaSpikeContextExtension)BeanProvider.getContextualReference(DeltaSpikeContextExtension.class, new Annotation[0])).getViewAccessScopedContext();
      }

      return this.viewAccessContextManager;
   }

   public void close() {
      this.getConversationManager().close();
   }
}
