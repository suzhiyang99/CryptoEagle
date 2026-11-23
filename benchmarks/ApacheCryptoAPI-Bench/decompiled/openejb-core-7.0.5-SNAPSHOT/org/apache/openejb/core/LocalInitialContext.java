package org.apache.openejb.core;

import java.util.Hashtable;
import java.util.Properties;
import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.security.auth.login.LoginException;
import org.apache.openejb.ClientInjections;
import org.apache.openejb.Core;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.core.ivm.ClientSecurity;
import org.apache.openejb.core.ivm.naming.ContextWrapper;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.ServiceManagerProxy;

public class LocalInitialContext extends ContextWrapper {
   public static final String OPENEJB_EMBEDDED_REMOTABLE = "openejb.embedded.remotable";
   static Logger logger;
   private final LocalInitialContextFactory factory;
   private final Properties properties = new Properties();
   private Object clientIdentity;
   public static final String ON_CLOSE = "openejb.embedded.initialcontext.close";
   private final Close onClose;
   private final Options options;
   private ServiceManagerProxy serviceManager;

   public LocalInitialContext(Hashtable env, LocalInitialContextFactory factory) throws NamingException {
      super(getContainerSystemEjbContext());
      this.properties.putAll(env);
      this.options = new Options(this.properties);
      this.onClose = (Close)this.options.get("openejb.embedded.initialcontext.close", LocalInitialContext.Close.LOGOUT);
      this.factory = factory;
      this.login();
      this.startNetworkServices();
   }

   public void close() throws NamingException {
      logger.debug("LocalIntialContext.close()");
      switch (this.onClose) {
         case LOGOUT:
            this.logout();
            break;
         case DESTROY:
            this.logout();
            this.destroy();
      }

   }

   private void destroy() throws NamingException {
      if (this.serviceManager != null) {
         this.serviceManager.stop();
      }

      this.tearDownOpenEJB();
   }

   private void tearDownOpenEJB() throws NamingException {
      if (this.factory.bootedOpenEJB()) {
         logger.info("Destroying container system");
         this.factory.close();
         this.context.close();
         OpenEJB.destroy();
      }

   }

   private void login() throws AuthenticationException {
      String user = (String)this.properties.get("java.naming.security.principal");
      String pass = (String)this.properties.get("java.naming.security.credentials");
      String realmName = (String)this.properties.get("openejb.authentication.realmName");
      if (user != null && pass != null) {
         try {
            if (logger.isDebugEnabled()) {
               logger.debug("Logging in: " + user);
            }

            SecurityService securityService = (SecurityService)SystemInstance.get().getComponent(SecurityService.class);
            if (realmName == null) {
               this.clientIdentity = securityService.login(user, pass);
            } else {
               this.clientIdentity = securityService.login(realmName, user, pass);
            }

            ClientSecurity.setIdentity(this.clientIdentity);
         } catch (LoginException e) {
            throw (AuthenticationException)(new AuthenticationException("User could not be authenticated: " + user)).initCause(e);
         }
      }

   }

   private void logout() {
      try {
         SecurityService securityService = (SecurityService)SystemInstance.get().getComponent(SecurityService.class);
         if (this.clientIdentity != null) {
            if (logger.isDebugEnabled()) {
               logger.debug("Logging out: " + this.clientIdentity);
            }

            securityService.logout(this.clientIdentity);
            ClientSecurity.setIdentity((Object)null);
         }

      } catch (LoginException e) {
         throw new OpenEJBRuntimeException("User could not be logged out.", e);
      }
   }

   private void startNetworkServices() {
      if (this.options.get("openejb.embedded.remotable", false)) {
         try {
            this.serviceManager = new ServiceManagerProxy();
            this.serviceManager.start();
         } catch (ServiceManagerProxy.AlreadyStartedException var2) {
            logger.debug("Network services already started.  Ignoring option openejb.embedded.remotable");
         }

      }
   }

   private static Context getContainerSystemEjbContext() throws NamingException {
      Context context = getRoot();
      context = (Context)context.lookup("openejb/local");
      return context;
   }

   private static Context getRoot() {
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
      return containerSystem.getJNDIContext();
   }

   public void bind(String name, Object obj) throws NamingException {
      if ("inject".equalsIgnoreCase(name)) {
         this.inject(obj);
      } else {
         super.bind(name, obj);
      }

   }

   private void inject(Object obj) throws NamingException {
      try {
         ClientInjections.clientInjector(obj).createInstance();
      } catch (OpenEJBException e) {
         throw (NamingException)(new NamingException("Injection failed")).initCause(e);
      }
   }

   static {
      Core.warmup();
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP.createChild("local"), LocalInitialContext.class);
   }

   public static enum Close {
      LOGOUT,
      DESTROY;
   }
}
