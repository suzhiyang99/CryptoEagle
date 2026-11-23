package org.apache.openejb.spi;

import java.lang.reflect.Method;
import java.security.Principal;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import org.apache.openejb.InterfaceType;

public interface SecurityService<T> extends Service {
   T login(String var1, String var2) throws LoginException;

   T login(String var1, String var2, String var3) throws LoginException;

   void associate(T var1) throws LoginException;

   T disassociate();

   void logout(T var1) throws LoginException;

   boolean isCallerInRole(String var1);

   Principal getCallerPrincipal();

   boolean isCallerAuthorized(Method var1, InterfaceType var2);

   void setState(Object var1);

   Object currentState();

   void onLogout(HttpServletRequest var1);
}
