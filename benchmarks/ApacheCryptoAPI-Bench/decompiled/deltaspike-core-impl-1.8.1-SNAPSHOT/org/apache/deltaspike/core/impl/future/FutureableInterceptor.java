package org.apache.deltaspike.core.impl.future;

import java.io.Serializable;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.apache.deltaspike.core.api.future.Futureable;
import org.apache.deltaspike.core.spi.future.FutureableStrategy;

@Interceptor
@Futureable
public class FutureableInterceptor implements Serializable {
   @Inject
   private FutureableStrategy futureableStrategy;

   @AroundInvoke
   public Object invoke(InvocationContext ic) throws Exception {
      return this.futureableStrategy.execute(ic);
   }
}
