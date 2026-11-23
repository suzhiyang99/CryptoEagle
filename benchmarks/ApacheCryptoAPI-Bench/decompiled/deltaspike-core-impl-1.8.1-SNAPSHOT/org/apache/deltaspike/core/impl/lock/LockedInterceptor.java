package org.apache.deltaspike.core.impl.lock;

import java.io.Serializable;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import org.apache.deltaspike.core.api.lock.Locked;
import org.apache.deltaspike.core.spi.lock.LockedStrategy;

@Locked
@Interceptor
public class LockedInterceptor implements Serializable {
   @Inject
   private LockedStrategy lockedStrategy;

   @AroundInvoke
   public Object invoke(InvocationContext ic) throws Exception {
      return this.lockedStrategy.execute(ic);
   }
}
