package org.apache.deltaspike.core.impl.scope.window;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.deltaspike.core.impl.scope.DeltaSpikeContextExtension;
import org.apache.deltaspike.core.spi.scope.window.WindowContext;

@ApplicationScoped
public class WindowContextProducer {
   @Inject
   private DeltaSpikeContextExtension deltaSpikeContextExtension;

   @Produces
   @Named("dsWindowContext")
   @Dependent
   public WindowContext getWindowContext() {
      return new InjectableWindowContext(this.deltaSpikeContextExtension.getWindowContext());
   }
}
