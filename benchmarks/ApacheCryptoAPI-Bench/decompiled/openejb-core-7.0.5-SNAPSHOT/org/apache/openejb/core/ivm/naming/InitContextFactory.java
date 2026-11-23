package org.apache.openejb.core.ivm.naming;

import java.util.Hashtable;
import java.util.Properties;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.spi.InitialContextFactory;
import javax.security.auth.login.LoginException;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.spi.SecurityService;

/** @deprecated */
@Deprecated
public class InitContextFactory implements InitialContextFactory {
   public Context getInitialContext(Hashtable env) throws javax.naming.NamingException {
      if (!OpenEJB.isInitialized()) {
         this.initializeOpenEJB(env);
      }

      String user = (String)env.get("java.naming.security.principal");
      String pass = (String)env.get("java.naming.security.credentials");
      String realmName = (String)env.get("openejb.authentication.realmName");
      if (user != null && pass != null) {
         try {
            SecurityService securityService = (SecurityService)SystemInstance.get().getComponent(SecurityService.class);
            Object identity;
            if (realmName == null) {
               identity = securityService.login(user, pass);
            } else {
               identity = securityService.login(realmName, user, pass);
            }

            securityService.associate(identity);
         } catch (LoginException e) {
            throw (AuthenticationException)(new AuthenticationException("User could not be authenticated: " + user)).initCause(e);
         }
      }

      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
      Context context = containerSystem.getJNDIContext();
      context = (Context)context.lookup("openejb/local");
      return context;
   }

   private void initializeOpenEJB(Hashtable env) throws javax.naming.NamingException {
      try {
         Properties props = new Properties();
         props.put("org/openejb/assembler_class", "org.apache.openejb.assembler.classic.Assembler");
         props.put("org/openejb/configuration_factory", "org.apache.openejb.config.ConfigurationFactory");
         props.put("org/openejb/configuration_source", "conf/default.openejb.conf");
         props.putAll(SystemInstance.get().getProperties());
         props.putAll(env);
         OpenEJB.init(props);
      } catch (Exception e) {
         throw new NamingException("Cannot initailize OpenEJB", e);
      }
   }
}
