package org.apache.openejb.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Context;
import javax.naming.NamingException;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Container;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.spi.ContainerSystem;

public class CoreContainerSystem implements ContainerSystem {
   private final Map<Object, AppContext> apps = new ConcurrentHashMap();
   private final Map<Object, BeanContext> deployments = new ConcurrentHashMap();
   private final Map<Object, Container> containers = new ConcurrentHashMap();
   private final Map<String, List<WebContext>> webDeployments = new ConcurrentHashMap();
   private final Context jndiContext;

   public CoreContainerSystem(JndiFactory jndiFactory) {
      if (jndiFactory == null) {
         throw new NullPointerException("JndiFactory required");
      } else {
         this.jndiContext = jndiFactory.createRootContext();

         try {
            if (!(this.jndiContext.lookup("openejb/local") instanceof Context) || !(this.jndiContext.lookup("openejb/remote") instanceof Context) || !(this.jndiContext.lookup("openejb/client") instanceof Context) || !(this.jndiContext.lookup("openejb/Deployment") instanceof Context) || !(this.jndiContext.lookup("openejb/global") instanceof Context)) {
               throw new OpenEJBRuntimeException("core openejb naming context not properly initialized.  It must have subcontexts for openejb/local, openejb/remote, openejb/client, and openejb/Deployment already present");
            }
         } catch (NamingException exception) {
            throw new OpenEJBRuntimeException("core openejb naming context not properly initialized.  It must have subcontexts for openejb/local, openejb/remote, openejb/client, and openejb/Deployment already present", exception);
         }

         SystemInstance.get().setComponent(JndiFactory.class, jndiFactory);
      }
   }

   public BeanContext getBeanContext(Object deploymentID) {
      return (BeanContext)this.deployments.get(deploymentID);
   }

   public BeanContext[] deployments() {
      return (BeanContext[])this.deployments.values().toArray(new BeanContext[this.deployments.size()]);
   }

   public void addDeployment(BeanContext deployment) {
      this.deployments.put(deployment.getDeploymentID(), deployment);
   }

   public void removeBeanContext(BeanContext info) {
      this.deployments.remove(info.getDeploymentID());
   }

   public Container getContainer(Object id) {
      return (Container)this.containers.get(id);
   }

   public Container[] containers() {
      return (Container[])this.containers.values().toArray(new Container[this.containers.size()]);
   }

   public void addContainer(Object id, Container c) {
      this.containers.put(id, c);
   }

   public void removeContainer(Object id) {
      this.containers.remove(id);
   }

   public WebContext getWebContextByHost(String id, String host) {
      List<WebContext> webContexts = (List)this.webDeployments.get(id);
      if (webContexts != null && !webContexts.isEmpty()) {
         if (webContexts.size() == 1 && ((WebContext)webContexts.get(0)).getHost() == null) {
            return (WebContext)webContexts.get(0);
         } else {
            for(WebContext web : webContexts) {
               if (web.getHost() != null && web.getHost().equals(host)) {
                  return web;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public WebContext getWebContext(String id) {
      List<WebContext> webContexts = (List)this.webDeployments.get(id);
      return webContexts != null && !webContexts.isEmpty() ? (WebContext)webContexts.get(0) : null;
   }

   public WebContext[] WebDeployments() {
      Collection<WebContext> all = new ArrayList(this.webDeployments.size());

      for(Collection<WebContext> list : this.webDeployments.values()) {
         all.addAll(list);
      }

      return (WebContext[])all.toArray(new WebContext[all.size()]);
   }

   public void addWebContext(WebContext webDeployment) {
      String id = webDeployment.getId();
      List<WebContext> list = (List)this.webDeployments.get(id);
      if (list == null) {
         list = new ArrayList();
         this.webDeployments.put(id, list);
      }

      list.add(webDeployment);
   }

   public void removeWebContext(WebContext info) {
      this.webDeployments.remove(info.getId());
   }

   public Context getJNDIContext() {
      return this.jndiContext;
   }

   public List<AppContext> getAppContexts() {
      return new ArrayList(this.apps.values());
   }

   public AppContext getAppContext(Object id) {
      AppContext context = (AppContext)this.apps.get(id);
      if (null == context && null != id) {
         context = (AppContext)this.apps.get(id.toString().toLowerCase(Locale.ENGLISH));
      }

      return context;
   }

   public void addAppContext(AppContext appContext) {
      this.apps.put(appContext.getId().toLowerCase(Locale.ENGLISH), appContext);
   }

   public AppContext removeAppContext(Object id) {
      AppContext context = (AppContext)this.apps.remove(id);
      if (null == context && null != id) {
         context = (AppContext)this.apps.remove(id.toString().toLowerCase(Locale.ENGLISH));
      }

      return context;
   }

   public synchronized Object[] getAppContextKeys() {
      return this.apps.keySet().toArray();
   }
}
