package org.apache.openejb.util;

import org.apache.openejb.AppContext;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.webbeans.config.WebBeansContext;

public final class AppFinder {
   public static <T> T findAppContextOrWeb(ClassLoader cl, Transformer<T> transformer) {
      ContainerSystem containerSystem = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);

      for(AppContext appContext : containerSystem.getAppContexts()) {
         ClassLoader appContextClassLoader = appContext.getClassLoader();
         boolean found = false;
         if (appContextClassLoader.equals(cl) || cl != null && cl.equals(appContextClassLoader)) {
            T from = (T)transformer.from(appContext);
            found = true;
            if (from != null) {
               return from;
            }
         }

         for(WebContext web : appContext.getWebContexts()) {
            ClassLoader webClassLoader = web.getClassLoader();
            if (webClassLoader.equals(cl) || cl != null && cl.equals(webClassLoader)) {
               return (T)transformer.from(web);
            }
         }

         if (found) {
            break;
         }
      }

      return null;
   }

   private AppFinder() {
   }

   public static class WebBeansContextTransformer implements Transformer<WebBeansContext> {
      public static final Transformer<WebBeansContext> INSTANCE = new WebBeansContextTransformer();

      public WebBeansContext from(AppContext appCtx) {
         return appCtx.getWebBeansContext();
      }

      public WebBeansContext from(WebContext webCtx) {
         WebBeansContext webBeansContext = webCtx.getWebBeansContext();
         return webBeansContext != null ? webBeansContext : this.from(webCtx.getAppContext());
      }
   }

   public static class AppContextTransformer implements Transformer<AppContext> {
      public static final Transformer<AppContext> INSTANCE = new AppContextTransformer();

      public AppContext from(AppContext appCtx) {
         return appCtx;
      }

      public AppContext from(WebContext webCtx) {
         return webCtx.getAppContext();
      }
   }

   public static class AppOrWebContextTransformer implements Transformer<Object> {
      public static final Transformer<AppContext> INSTANCE = new AppContextTransformer();

      public Object from(AppContext appCtx) {
         return appCtx;
      }

      public Object from(WebContext webCtx) {
         return webCtx;
      }
   }

   public interface Transformer<T> {
      T from(AppContext var1);

      T from(WebContext var1);
   }
}
