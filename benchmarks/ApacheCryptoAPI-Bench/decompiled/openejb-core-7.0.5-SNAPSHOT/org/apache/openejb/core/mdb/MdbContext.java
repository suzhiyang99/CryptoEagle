package org.apache.openejb.core.mdb;

import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.MessageDrivenContext;
import org.apache.openejb.core.BaseContext;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.spi.SecurityService;

public class MdbContext extends BaseContext implements MessageDrivenContext {
   public MdbContext(SecurityService securityService) {
      super(securityService);
   }

   public EJBHome getEJBHome() {
      throw new IllegalStateException();
   }

   public EJBLocalHome getEJBLocalHome() {
      throw new IllegalStateException();
   }

   public void check(ThreadContext context, BaseContext.Call call) {
      Operation operation = context.getCurrentOperation();
      switch (call) {
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
            switch (operation) {
               case INJECTION:
               case CREATE:
               case POST_CONSTRUCT:
               case PRE_DESTROY:
                  throw this.illegal(call, operation);
               default:
                  return;
            }
         default:
      }
   }
}
