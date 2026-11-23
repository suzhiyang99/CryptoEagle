package org.apache.openejb.core.managed;

import javax.transaction.UserTransaction;
import javax.xml.rpc.handler.MessageContext;
import org.apache.openejb.core.BaseContext;
import org.apache.openejb.core.BaseSessionContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.spi.SecurityService;

public class ManagedContext extends BaseSessionContext {
   public ManagedContext(SecurityService securityService, UserTransaction userTransaction) {
      super(securityService, userTransaction);
   }

   public void check(ThreadContext context, BaseContext.Call call) {
      Operation operation = context.getCurrentOperation();
      switch (call) {
         case getCallerPrincipal:
         case isCallerInRole:
         case getUserTransaction:
         case getTimerService:
         case getEJBLocalObject:
         case getEJBObject:
         case getBusinessObject:
         case getContextData:
            switch (operation) {
               case INJECTION:
                  throw this.illegal(call, operation);
               default:
                  return;
            }
         case setRollbackOnly:
         case getRollbackOnly:
         case timerMethod:
            switch (operation) {
               case INJECTION:
               case CREATE:
               case AFTER_COMPLETION:
               case PRE_DESTROY:
               case REMOVE:
               case POST_CONSTRUCT:
                  throw this.illegal(call, operation);
               default:
                  return;
            }
         case getInvokedBusinessInterface:
            switch (operation) {
               case INJECTION:
               case CREATE:
               case AFTER_COMPLETION:
               case PRE_DESTROY:
               case REMOVE:
               case POST_CONSTRUCT:
               case AFTER_BEGIN:
               case BEFORE_COMPLETION:
               case TIMEOUT:
                  throw this.illegal(call, operation);
               default:
                  return;
            }
         case UserTransactionMethod:
            switch (operation) {
               case INJECTION:
               case AFTER_COMPLETION:
                  throw this.illegal(call, operation);
               default:
                  return;
            }
         default:
      }
   }

   public MessageContext getMessageContext() throws IllegalStateException {
      throw new IllegalStateException("@ManagedBeans do not support Web Service interfaces");
   }
}
