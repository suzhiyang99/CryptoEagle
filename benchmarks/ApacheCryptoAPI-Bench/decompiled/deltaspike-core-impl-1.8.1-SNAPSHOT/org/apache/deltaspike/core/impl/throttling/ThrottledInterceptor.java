package org.apache.deltaspike.core.impl.throttling;

import java.io.Serializable;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.apache.deltaspike.core.api.throttling.Throttled;
import org.apache.deltaspike.core.spi.throttling.ThrottledStrategy;

@Throttled
@Interceptor
public class ThrottledInterceptor implements Serializable {
   @Inject
   private ThrottledStrategy throttledStrategy;

   @AroundInvoke
   public Object invoke(InvocationContext ic) throws Exception {
      return this.throttledStrategy.execute(ic);
   }
}
