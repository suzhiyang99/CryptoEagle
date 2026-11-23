package org.apache.openejb.core.stateless;

import java.io.Flushable;
import java.io.IOException;
import org.apache.openejb.core.BaseContext;
import org.apache.openejb.core.BaseSessionContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.spi.SecurityService;

public class StatelessContext extends BaseSessionContext implements Flushable {
   private final Flushable flushable;

   public StatelessContext(SecurityService securityService, Flushable flushable) {
      super(securityService);
      this.flushable = flushable;
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
         case timerMethod:
         case setRollbackOnly:
         case getRollbackOnly:
         case UserTransactionMethod:
            switch (operation) {
               case INJECTION:
               case CREATE:
               case POST_CONSTRUCT:
               case PRE_DESTROY:
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

   public void flush() throws IOException {
      this.flushable.flush();
   }
}
