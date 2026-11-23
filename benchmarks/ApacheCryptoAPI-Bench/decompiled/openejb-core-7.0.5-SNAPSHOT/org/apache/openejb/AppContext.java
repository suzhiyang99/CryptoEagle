package org.apache.openejb;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import org.apache.openejb.async.AsynchronousPool;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.webbeans.config.WebBeansContext;

public class AppContext extends DeploymentContext {
   private final long startTime = System.currentTimeMillis();
   private final SystemInstance systemInstance;
   private final ClassLoader classLoader;
   private final Context globalJndiContext;
   private final Context appJndiContext;
   private final boolean standaloneModule;
   private boolean cdiEnabled;
   private WebBeansContext webBeansContext;
   private final Collection<Injection> injections = new HashSet();
   private final Map<String, Object> bindings = new HashMap();
   private final List<BeanContext> beanContexts = new ArrayList();
   private final List<WebContext> webContexts = new ArrayList();

   public AppContext(String id, SystemInstance systemInstance, ClassLoader classLoader, Context globalJndiContext, Context appJndiContext, boolean standaloneModule) {
      super(id, systemInstance.getOptions());
      this.classLoader = classLoader;
      this.systemInstance = systemInstance;
      this.globalJndiContext = globalJndiContext;
      this.appJndiContext = appJndiContext;
      this.standaloneModule = standaloneModule;
   }

   public Collection<Injection> getInjections() {
      return this.injections;
   }

   public Map<String, Object> getBindings() {
      return this.bindings;
   }

   public BeanManager getBeanManager() {
      return this.webBeansContext == null ? null : this.webBeansContext.getBeanManagerImpl();
   }

   public WebBeansContext getWebBeansContext() {
      return this.webBeansContext;
   }

   public void setWebBeansContext(WebBeansContext webBeansContext) {
      this.webBeansContext = webBeansContext;
   }

   public List<WebContext> getWebContexts() {
      return this.webContexts;
   }

   public boolean isCdiEnabled() {
      return this.cdiEnabled;
   }

   public void setCdiEnabled(boolean cdiEnabled) {
      this.cdiEnabled = cdiEnabled;
   }

   public String getId() {
      return super.getId();
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   /** @deprecated */
   @Deprecated
   public List<BeanContext> getDeployments() {
      return this.getBeanContexts();
   }

   public List<BeanContext> getBeanContexts() {
      return this.beanContexts;
   }

   public SystemInstance getSystemInstance() {
      return this.systemInstance;
   }

   public Context getAppJndiContext() {
      return this.appJndiContext;
   }

   public Context getGlobalJndiContext() {
      return this.globalJndiContext;
   }

   public boolean isStandaloneModule() {
      return this.standaloneModule;
   }

   public AsynchronousPool getAsynchronousPool() {
      return (AsynchronousPool)this.get(AsynchronousPool.class);
   }

   public long getStartTime() {
      return this.startTime;
   }
}
