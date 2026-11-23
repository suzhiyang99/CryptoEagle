package org.apache.openejb.core.singleton;

import org.apache.openejb.core.BaseContext;
import org.apache.openejb.core.BaseSessionContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.spi.SecurityService;

public class SingletonContext extends BaseSessionContext {
   public SingletonContext(SecurityService securityService) {
      super(securityService);
   }

   public void check(ThreadContext context, BaseContext.Call call) {
      Operation operation = context.getCurrentOperation();
      switch (call) {
         case getEJBLocalObject:
         case getEJBObject:
         case getBusinessObject:
         case getUserTransaction:
         case getTimerService:
         case getContextData:
            switch (operation) {
               case INJECTION:
                  throw this.illegal(call, operation);
               default:
                  return;
            }
         case getCallerPrincipal:
         case isCallerInRole:
            switch (operation) {
               case INJECTION:
               case CREATE:
               case POST_CONSTRUCT:
               case PRE_DESTROY:
                  throw this.illegal(call, operation);
               default:
                  return;
            }
         case timerMethod:
         case setRollbackOnly:
         case getRollbackOnly:
         case UserTransactionMethod:
            switch (operation) {
               case INJECTION:
               case CREATE:
                  throw this.illegal(call, operation);
               default:
                  return;
            }
         case getInvokedBusinessInterface:
            switch (operation) {
               case BUSINESS:
                  return;
               default:
                  throw this.illegal(call, operation);
            }
         case getMessageContext:
            switch (operation) {
               case BUSINESS_WS:
                  return;
               default:
                  throw this.illegal(call, operation);
            }
         default:
      }
   }
}
