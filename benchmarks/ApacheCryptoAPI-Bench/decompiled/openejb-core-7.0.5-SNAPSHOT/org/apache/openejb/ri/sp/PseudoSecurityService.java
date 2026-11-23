package org.apache.openejb.ri.sp;

import java.lang.reflect.Method;
import java.security.Principal;
import java.util.Collections;
import java.util.Properties;
import java.util.Set;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpServletRequest;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.spi.SecurityService;

public class PseudoSecurityService implements SecurityService {
   public PseudoSecurityService() {
      PseudoPolicyConfigurationFactory.install();
   }

   public void init(Properties props) {
   }

   public Object login(String user, String pass) throws LoginException {
      return null;
   }

   public Object login(String securityRealm, String user, String pass) throws LoginException {
      return null;
   }

   public Set<String> getLogicalRoles(Principal[] principals, Set<String> logicalRoles) {
      return Collections.emptySet();
   }

   public void associate(Object securityIdentity) throws LoginException {
   }

   public Object disassociate() {
      return null;
   }

   public void logout(Object securityIdentity) throws LoginException {
   }

   public boolean isCallerInRole(String role) {
      return false;
   }

   public Principal getCallerPrincipal() {
      return null;
   }

   public boolean isCallerAuthorized(Method method, InterfaceType type) {
      return true;
   }

   public void setState(Object o) {
   }

   public Object currentState() {
      return null;
   }

   public void onLogout(HttpServletRequest request) {
   }
}
