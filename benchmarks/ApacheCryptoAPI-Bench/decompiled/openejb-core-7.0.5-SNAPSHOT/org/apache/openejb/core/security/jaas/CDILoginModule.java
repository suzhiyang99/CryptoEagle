package org.apache.openejb.core.security.jaas;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Set;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.inject.spi.Bean;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;
import org.apache.openejb.AppContext;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.BeanManagerImpl;
import org.apache.webbeans.context.creational.CreationalContextImpl;
import org.apache.webbeans.inject.OWBInjector;

public class CDILoginModule implements LoginModule {
   private CreationalContextImpl<?> cc;
   private LoginModule loginModule;

   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
      WebBeansContext webBeansContext = WebBeansContext.currentInstance();
      BeanManagerImpl bm = webBeansContext.getBeanManagerImpl();
      if (!bm.isInUse()) {
         throw new OpenEJBRuntimeException("CDI not activated");
      } else {
         String delegate = String.valueOf(options.get("delegate"));
         if ("null".equals(delegate)) {
            String app = findAppName(webBeansContext);
            delegate = String.valueOf(options.get(app));
            if ("null".equals(delegate)) {
               throw new OpenEJBRuntimeException("Please specify a delegate class");
            }
         }

         Class<?> clazz;
         try {
            clazz = Thread.currentThread().getContextClassLoader().loadClass(delegate);
         } catch (ClassNotFoundException e) {
            throw new OpenEJBRuntimeException(e.getMessage(), e);
         }

         this.cc = bm.createCreationalContext((Contextual)null);
         String cdiName = String.valueOf(options.get("cdiName"));
         if ("true".equals(String.valueOf(options.get("loginModuleAsCdiBean")))) {
            Set<Bean<?>> beans;
            if ("null".equals(cdiName)) {
               beans = bm.getBeans(clazz, new Annotation[0]);
            } else {
               beans = bm.getBeans(cdiName);
            }

            this.loginModule = (LoginModule)LoginModule.class.cast(bm.getReference(bm.resolve(beans), clazz, this.cc));
         } else {
            try {
               this.loginModule = (LoginModule)LoginModule.class.cast(clazz.newInstance());
               OWBInjector.inject(bm, this.loginModule, this.cc);
            } catch (Exception e) {
               throw new OpenEJBRuntimeException("Can't inject into delegate class " + this.loginModule, e);
            }
         }

         this.loginModule.initialize(subject, callbackHandler, sharedState, options);
      }
   }

   private static String findAppName(WebBeansContext webBeansContext) {
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);

      for(AppContext appContext : containerSystem.getAppContexts()) {
         if (appContext.getWebBeansContext() == webBeansContext) {
            return appContext.getId();
         }

         for(WebContext web : appContext.getWebContexts()) {
            if (web.getWebbeansContext() == webBeansContext) {
               return web.getId();
            }
         }
      }

      return "defaultDelegate";
   }

   public boolean login() throws LoginException {
      return this.loginModule != null && this.loginModule.login();
   }

   public boolean commit() throws LoginException {
      return this.loginModule == null || this.loginModule.commit();
   }

   public boolean abort() throws LoginException {
      boolean var1;
      try {
         var1 = this.loginModule == null || this.loginModule.abort();
      } finally {
         this.cleanUp();
      }

      return var1;
   }

   public boolean logout() throws LoginException {
      boolean var1;
      try {
         var1 = this.loginModule == null || this.loginModule.logout();
      } finally {
         this.cleanUp();
      }

      return var1;
   }

   private void cleanUp() {
      if (this.cc != null) {
         this.cc.release();
         this.cc = null;
      }

   }
}
