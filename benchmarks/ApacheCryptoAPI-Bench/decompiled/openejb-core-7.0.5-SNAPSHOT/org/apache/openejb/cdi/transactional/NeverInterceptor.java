package org.apache.openejb.cdi.transactional;

import java.rmi.RemoteException;
import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.InvalidTransactionException;
import javax.transaction.Transactional;
import javax.transaction.TransactionalException;
import javax.transaction.Transactional.TxType;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.core.transaction.TxNever;

@Interceptor
@Transactional(TxType.NEVER)
@Priority(200)
public class NeverInterceptor extends InterceptorBase {
   @AroundInvoke
   public Object intercept(InvocationContext ic) throws Exception {
      try {
         return super.intercept(ic);
      } catch (RemoteException re) {
         throw new TransactionalException(re.getMessage(), new InvalidTransactionException(re.getMessage()));
      }
   }

   protected TransactionPolicy getPolicy() throws SystemException, ApplicationException {
      return new TxNever(getTransactionManager());
   }

   protected boolean doesForbidUtUsage() {
      return false;
   }
}
