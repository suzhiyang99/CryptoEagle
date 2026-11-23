package org.apache.openejb.cdi;

import java.util.List;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.config.WebBeansFinder;

public class CdiBuilder {
   private static final Logger logger;
   private static final ThreadSingletonService SINGLETON_SERVICE;

   public void build(AppInfo appInfo, AppContext appContext, List<BeanContext> allDeployments) {
      this.initSingleton().initialize(new StartupObject(appContext, appInfo, allDeployments));
   }

   private ThreadSingletonService initSingleton() {
      ThreadContext.addThreadContextListener(new RequestScopedThreadContextListener());
      ThreadSingletonService singletonService = (ThreadSingletonService)SystemInstance.get().getComponent(ThreadSingletonService.class);
      if (singletonService == null) {
         singletonService = initializeOWB();
      } else {
         logger.info("Existing thread singleton service in SystemInstance(): " + singletonService);
      }

      return singletonService;
   }

   public void build(AppInfo appInfo, AppContext appContext, List<BeanContext> allDeployments, WebContext webContext) {
      ThreadSingletonService singletonService = this.initSingleton();
      singletonService.initialize(new StartupObject(appContext, appInfo, allDeployments, webContext));
   }

   public static synchronized ThreadSingletonService initializeOWB() {
      logger.info("Created new singletonService " + SINGLETON_SERVICE);
      SystemInstance.get().setComponent(ThreadSingletonService.class, SINGLETON_SERVICE);

      try {
         WebBeansFinder.setSingletonService(SINGLETON_SERVICE);
         logger.info("Succeeded in installing singleton service");
      } catch (Exception var1) {
         logger.debug("Could not install our singleton service");
      }

      ThreadContext.addThreadContextListener(new OWBContextThreadListener());
      return SINGLETON_SERVICE;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_CDI, CdiBuilder.class);
      SINGLETON_SERVICE = new ThreadSingletonServiceImpl();
   }
}
