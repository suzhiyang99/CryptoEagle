package org.apache.deltaspike.core.impl.throttling;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import org.apache.deltaspike.core.spi.throttling.ThrottledStrategy;

@Dependent
public class DefaultThrottledStrategy implements ThrottledStrategy {
   @Inject
   private InvokerStorage metadata;

   public Object execute(InvocationContext ic) throws Exception {
      return this.metadata.getOrCreateInvoker(ic).invoke(ic);
   }
}
