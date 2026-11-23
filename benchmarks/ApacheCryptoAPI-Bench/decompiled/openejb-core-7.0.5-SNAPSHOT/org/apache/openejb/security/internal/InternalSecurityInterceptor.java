package org.apache.openejb.security.internal;

import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.Assembler;
import org.apache.openejb.spi.SecurityService;

public class InternalSecurityInterceptor {
   public static final String OPENEJB_INTERNAL_BEANS_SECURITY_ENABLED = "openejb.internal.beans.security.enabled";
   private static final String[] ROLES = new String[]{"openejb-admin", "tomee-admin"};

   @AroundInvoke
   public Object invoke(InvocationContext ic) throws Exception {
      if (!SystemInstance.get().isDefaultProfile() && SystemInstance.get().getOptions().get("openejb.internal.beans.security.enabled", true)) {
         SecurityService<?> ss = ((Assembler)SystemInstance.get().getComponent(Assembler.class)).getSecurityService();

         for(String role : ROLES) {
            if (ss.isCallerInRole(role)) {
               return ic.proceed();
            }
         }

         throw new SecurityException("to invoke this EJB you need to get the right permission");
      } else {
         return ic.proceed();
      }
   }
}
