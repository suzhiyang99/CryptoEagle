package org.apache.openejb.cdi.transactional;

import javax.annotation.Priority;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import org.apache.openejb.ApplicationException;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.transaction.TransactionPolicy;
import org.apache.openejb.core.transaction.TxMandatory;

@Interceptor
@Transactional(TxType.MANDATORY)
@Priority(200)
public class MandatoryInterceptor extends InterceptorBase {
   @AroundInvoke
   public Object intercept(InvocationContext ic) throws Exception {
      return super.intercept(ic);
   }

   protected TransactionPolicy getPolicy() throws SystemException, ApplicationException {
      return new TxMandatory(getTransactionManager());
   }
}
