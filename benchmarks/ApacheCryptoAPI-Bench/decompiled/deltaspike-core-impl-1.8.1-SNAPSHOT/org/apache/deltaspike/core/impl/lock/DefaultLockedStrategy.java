package org.apache.deltaspike.core.impl.lock;

import java.util.concurrent.locks.Lock;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.interceptor.InvocationContext;
import org.apache.deltaspike.core.spi.lock.LockedStrategy;

@Dependent
public class DefaultLockedStrategy implements LockedStrategy {
   @Inject
   private LockSupplierStorage lockSupplierStorage;

   public Object execute(InvocationContext ic) throws Exception {
      Lock lock = this.lockSupplierStorage.getLockSupplier(ic).get();

      Object var3;
      try {
         var3 = ic.proceed();
      } finally {
         lock.unlock();
      }

      return var3;
   }
}
