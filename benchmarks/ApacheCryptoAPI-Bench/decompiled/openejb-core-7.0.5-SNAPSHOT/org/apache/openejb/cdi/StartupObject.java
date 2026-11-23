package org.apache.openejb.cdi;

import java.util.List;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.core.WebContext;
import org.apache.webbeans.config.WebBeansContext;

public class StartupObject {
   private final AppInfo appInfo;
   private final AppContext appContext;
   private final List<BeanContext> beanContexts;
   private final WebContext webContext;

   public StartupObject(AppContext appContext, AppInfo appInfo, List<BeanContext> beanContexts) {
      this(appContext, appInfo, beanContexts, (WebContext)null);
   }

   public StartupObject(AppContext appContext, AppInfo appInfo, List<BeanContext> beanContexts, WebContext webContext) {
      assert appContext != null;

      assert appInfo != null;

      assert beanContexts != null;

      this.appContext = appContext;
      this.appInfo = appInfo;
      this.beanContexts = beanContexts;
      this.webContext = webContext;
   }

   public AppContext getAppContext() {
      return this.appContext;
   }

   public AppInfo getAppInfo() {
      return this.appInfo;
   }

   public List<BeanContext> getBeanContexts() {
      return this.beanContexts;
   }

   public WebContext getWebContext() {
      return this.webContext;
   }

   public WebBeansContext getWebBeansContext() {
      return this.isFromWebApp() ? this.webContext.getWebbeansContext() : this.appContext.getWebBeansContext();
   }

   public ClassLoader getClassLoader() {
      return this.isFromWebApp() ? this.webContext.getClassLoader() : this.appContext.getClassLoader();
   }

   public boolean isFromWebApp() {
      return this.webContext != null;
   }
}
