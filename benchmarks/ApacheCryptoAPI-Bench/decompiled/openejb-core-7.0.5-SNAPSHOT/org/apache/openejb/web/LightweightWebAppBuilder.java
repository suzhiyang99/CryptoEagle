package org.apache.openejb.web;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameNotFoundException;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebListener;
import javax.servlet.annotation.WebServlet;
import javax.ws.rs.core.Application;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.Injection;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.assembler.classic.ClassListInfo;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.assembler.classic.FilterInfo;
import org.apache.openejb.assembler.classic.InjectionBuilder;
import org.apache.openejb.assembler.classic.JndiEncBuilder;
import org.apache.openejb.assembler.classic.ListenerInfo;
import org.apache.openejb.assembler.classic.ParamValueInfo;
import org.apache.openejb.assembler.classic.PortInfo;
import org.apache.openejb.assembler.classic.ServletInfo;
import org.apache.openejb.assembler.classic.WebAppBuilder;
import org.apache.openejb.assembler.classic.WebAppInfo;
import org.apache.openejb.cdi.CdiBuilder;
import org.apache.openejb.cdi.OpenEJBLifecycle;
import org.apache.openejb.core.CoreContainerSystem;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.observer.Event;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.ArrayEnumeration;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.OpenEjbVersion;
import org.apache.webbeans.spi.ContainerLifecycle;
import org.apache.webbeans.web.lifecycle.test.MockServletContext;
import org.apache.webbeans.web.lifecycle.test.MockServletContextEvent;

public class LightweightWebAppBuilder implements WebAppBuilder {
   private static final Logger LOGGER;
   private static Method addServletMethod;
   private static Method removeServletMethod;
   private static Method addFilterMethod;
   private static Method removeFilterMethod;
   private static Method addDefaults;
   private final Map<WebAppInfo, DeployedWebObjects> servletDeploymentInfo = new HashMap();
   private final Map<WebAppInfo, List<Object>> listeners = new HashMap();
   private final Map<WebAppInfo, ServletContextEvent> servletContextEvents = new HashMap();
   private final Map<String, ClassLoader> loaderByWebContext = new HashMap();

   public void setClassLoader(String id, ClassLoader loader) {
      this.loaderByWebContext.put(id, loader);
   }

   public void removeClassLoader(String id) {
      this.loaderByWebContext.remove(id);
   }

   public void deployWebApps(AppInfo appInfo, ClassLoader appClassLoader) throws Exception {
      CoreContainerSystem cs = (CoreContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
      AppContext appContext = cs.getAppContext(appInfo.appId);
      if (appContext == null) {
         throw new OpenEJBRuntimeException("Can't find app context for " + appInfo.appId);
      } else {
         for(WebAppInfo webAppInfo : appInfo.webApps) {
            ClassLoader classLoader = (ClassLoader)this.loaderByWebContext.get(webAppInfo.moduleId);
            if (classLoader == null) {
               classLoader = appClassLoader;
            }

            Set<Injection> injections = new HashSet(appContext.getInjections());
            injections.addAll((new InjectionBuilder(classLoader)).buildInjections(webAppInfo.jndiEnc));
            List<BeanContext> beanContexts;
            if (!appInfo.webAppAlone) {
               Assembler assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);
               beanContexts = assembler.initEjbs(classLoader, appInfo, appContext, injections, new ArrayList(), webAppInfo.moduleId);
               appContext.getBeanContexts().addAll(beanContexts);
            } else {
               beanContexts = null;
            }

            Map<String, Object> bindings = new HashMap();
            bindings.putAll(appContext.getBindings());
            bindings.putAll((new JndiEncBuilder(webAppInfo.jndiEnc, injections, webAppInfo.moduleId, "Bean", (URI)null, webAppInfo.uniqueId, classLoader, appInfo.properties)).buildBindings(JndiEncBuilder.JndiScope.comp));
            final WebContext webContext = new WebContext(appContext);
            webContext.setBindings(bindings);
            webContext.getBindings().putAll((new JndiEncBuilder(webAppInfo.jndiEnc, injections, webAppInfo.moduleId, "Bean", (URI)null, webAppInfo.uniqueId, classLoader, appInfo.properties)).buildBindings(JndiEncBuilder.JndiScope.comp));
            webContext.setJndiEnc(WebInitialContext.create(bindings, appContext.getGlobalJndiContext()));
            webContext.setClassLoader(classLoader);
            webContext.setId(webAppInfo.moduleId);
            webContext.setContextRoot(webAppInfo.contextRoot);
            webContext.setHost(webAppInfo.host);
            webContext.getInjections().addAll(injections);
            webContext.setInitialContext(new EmbeddedInitialContext(webContext.getJndiEnc(), webContext.getBindings()));
            ServletContext component = (ServletContext)SystemInstance.get().getComponent(ServletContext.class);
            final ServletContextEvent sce = (ServletContextEvent)(component == null ? new MockServletContextEvent() : new ServletContextEvent(new LightServletContext(component, webContext.getClassLoader())));
            this.servletContextEvents.put(webAppInfo, sce);
            webContext.setServletContext(sce.getServletContext());
            SystemInstance.get().fireEvent(new EmbeddedServletContextCreated(sce.getServletContext()));
            appContext.getWebContexts().add(webContext);
            cs.addWebContext(webContext);
            if (!appInfo.webAppAlone && this.hasCdi(appInfo)) {
               Assembler assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);
               (new CdiBuilder()).build(appInfo, appContext, beanContexts, webContext);
               assembler.startEjbs(true, beanContexts);
            }

            for(ListenerInfo listener : webAppInfo.listeners) {
               Class<?> clazz = webContext.getClassLoader().loadClass(listener.classname);
               final Object instance = webContext.newInstance(clazz);
               if (ServletContextListener.class.isInstance(instance)) {
                  this.switchServletContextIfNeeded(sce.getServletContext(), new Runnable() {
                     public void run() {
                        ((ServletContextListener)instance).contextInitialized(sce);
                     }
                  });
               }

               List<Object> list = (List)this.listeners.get(webAppInfo);
               if (list == null) {
                  list = new ArrayList();
                  this.listeners.put(webAppInfo, list);
               }

               list.add(instance);
            }

            for(ClassListInfo info : webAppInfo.webAnnotatedClasses) {
               String url = info.name;

               for(String filterPath : info.list) {
                  Class<?> clazz = loadFromUrls(webContext.getClassLoader(), url, filterPath);
                  WebListener annotation = (WebListener)clazz.getAnnotation(WebListener.class);
                  if (annotation != null) {
                     final Object instance = webContext.newInstance(clazz);
                     if (ServletContextListener.class.isInstance(instance)) {
                        this.switchServletContextIfNeeded(sce.getServletContext(), new Runnable() {
                           public void run() {
                              ((ServletContextListener)instance).contextInitialized(sce);
                           }
                        });
                     }

                     List<Object> list = (List)this.listeners.get(webAppInfo);
                     if (list == null) {
                        list = new ArrayList();
                        this.listeners.put(webAppInfo, list);
                     }

                     list.add(instance);
                  }
               }
            }

            final DeployedWebObjects deployedWebObjects = new DeployedWebObjects();
            deployedWebObjects.webContext = webContext;
            this.servletDeploymentInfo.put(webAppInfo, deployedWebObjects);
            if (webContext.getWebBeansContext() != null && webContext.getWebBeansContext().getBeanManagerImpl().isInUse()) {
               Thread thread = Thread.currentThread();
               ClassLoader old = thread.getContextClassLoader();
               thread.setContextClassLoader(webContext.getClassLoader());

               try {
                  ((OpenEJBLifecycle)OpenEJBLifecycle.class.cast(webContext.getWebBeansContext().getService(ContainerLifecycle.class))).startServletContext(sce.getServletContext());
               } finally {
                  thread.setContextClassLoader(old);
               }
            }

            if (addServletMethod != null) {
               for(final FilterInfo info : webAppInfo.filters) {
                  this.switchServletContextIfNeeded(sce.getServletContext(), new Runnable() {
                     public void run() {
                        for(String mapping : info.mappings) {
                           FilterConfig config = new SimpleFilterConfig(sce.getServletContext(), info.name, info.initParams);

                           try {
                              LightweightWebAppBuilder.addFilterMethod.invoke((Object)null, info.classname, webContext, mapping, config);
                              deployedWebObjects.filterMappings.add(mapping);
                           } catch (Exception e) {
                              LightweightWebAppBuilder.LOGGER.warning(e.getMessage(), (Throwable)e);
                           }
                        }

                     }
                  });
               }

               for(ClassListInfo info : webAppInfo.webAnnotatedClasses) {
                  String url = info.name;

                  for(String filterPath : info.list) {
                     final Class<?> clazz = loadFromUrls(webContext.getClassLoader(), url, filterPath);
                     WebFilter annotation = (WebFilter)clazz.getAnnotation(WebFilter.class);
                     if (annotation != null) {
                        Properties initParams = new Properties();

                        for(WebInitParam param : annotation.initParams()) {
                           initParams.put(param.name(), param.value());
                        }

                        final FilterConfig config = new SimpleFilterConfig(sce.getServletContext(), info.name, initParams);

                        for(final String[] mappings : Arrays.asList(annotation.urlPatterns(), annotation.value())) {
                           this.switchServletContextIfNeeded(sce.getServletContext(), new Runnable() {
                              public void run() {
                                 for(String mapping : mappings) {
                                    try {
                                       LightweightWebAppBuilder.addFilterMethod.invoke((Object)null, clazz.getName(), webContext, mapping, config);
                                       deployedWebObjects.filterMappings.add(mapping);
                                    } catch (Exception e) {
                                       LightweightWebAppBuilder.LOGGER.warning(e.getMessage(), (Throwable)e);
                                    }
                                 }

                              }
                           });
                        }
                     }
                  }
               }

               Map<String, PortInfo> ports = new TreeMap();

               for(PortInfo port : webAppInfo.portInfos) {
                  ports.put(port.serviceLink, port);
               }

               for(final ServletInfo info : webAppInfo.servlets) {
                  if ("true".equalsIgnoreCase(appInfo.properties.getProperty("openejb.jaxrs.on", "true"))) {
                     boolean skip = false;

                     for(ParamValueInfo pvi : info.initParams) {
                        if ("javax.ws.rs.Application".equals(pvi.name) || Application.class.getName().equals(pvi.name)) {
                           skip = true;
                        }
                     }

                     if (skip) {
                        continue;
                     }

                     if (info.servletClass == null) {
                        try {
                           if (Application.class.isAssignableFrom(classLoader.loadClass(info.servletName))) {
                              continue;
                           }
                        } catch (Exception var30) {
                        }
                     }
                  }

                  if (!ports.containsKey(info.servletName) && !ports.containsKey(info.servletClass)) {
                     for(final String mapping : info.mappings) {
                        this.switchServletContextIfNeeded(sce.getServletContext(), new Runnable() {
                           public void run() {
                              try {
                                 LightweightWebAppBuilder.addServletMethod.invoke((Object)null, info.servletClass, webContext, mapping);
                                 deployedWebObjects.mappings.add(mapping);
                              } catch (Exception e) {
                                 LightweightWebAppBuilder.LOGGER.warning(e.getMessage(), (Throwable)e);
                              }

                           }
                        });
                     }
                  }
               }

               for(ClassListInfo info : webAppInfo.webAnnotatedClasses) {
                  String url = info.name;

                  for(String servletPath : info.list) {
                     final Class<?> clazz = loadFromUrls(webContext.getClassLoader(), url, servletPath);
                     WebServlet annotation = (WebServlet)clazz.getAnnotation(WebServlet.class);
                     if (annotation != null) {
                        for(final String[] mappings : Arrays.asList(annotation.urlPatterns(), annotation.value())) {
                           this.switchServletContextIfNeeded(sce.getServletContext(), new Runnable() {
                              public void run() {
                                 for(String mapping : mappings) {
                                    try {
                                       LightweightWebAppBuilder.addServletMethod.invoke((Object)null, clazz.getName(), webContext, mapping);
                                       deployedWebObjects.mappings.add(mapping);
                                    } catch (Exception e) {
                                       LightweightWebAppBuilder.LOGGER.warning(e.getMessage(), (Throwable)e);
                                    }
                                 }

                              }
                           });
                        }
                     }
                  }
               }

               if (addDefaults != null && tryJsp()) {
                  addDefaults.invoke((Object)null, webContext);
                  deployedWebObjects.mappings.add("*\\.jsp");
               }
            }
         }

      }
   }

   private boolean hasCdi(AppInfo appInfo) {
      for(EjbJarInfo jar : appInfo.ejbJars) {
         if (jar.beans != null) {
            return true;
         }
      }

      return false;
   }

   private void switchServletContextIfNeeded(ServletContext sc, Runnable runnable) {
      if (sc == null) {
         runnable.run();
      } else {
         SystemInstance systemInstance = SystemInstance.get();
         ServletContext old = (ServletContext)systemInstance.getComponent(ServletContext.class);
         systemInstance.setComponent(ServletContext.class, sc);

         try {
            runnable.run();
         } finally {
            if (old == null) {
               systemInstance.removeComponent(ServletContext.class);
            } else {
               systemInstance.setComponent(ServletContext.class, old);
            }

         }

      }
   }

   private static boolean tryJsp() {
      return "true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.embedded.try-jsp", "true"));
   }

   public Collection<Object> listenersFor(String context) {
      for(Map.Entry<WebAppInfo, List<Object>> info : this.listeners.entrySet()) {
         if (context != null && context.replace("/", "").equals(((WebAppInfo)info.getKey()).contextRoot.replace("/", ""))) {
            return (Collection)info.getValue();
         }
      }

      return null;
   }

   private static Class<?> loadFromUrls(ClassLoader loader, String url, String path) throws ClassNotFoundException {
      String classname;
      if (path.startsWith("archive:") && path.contains(".war/")) {
         classname = path.substring(path.indexOf(".war") + ".war".length() + 1);
      } else if ("jar:file://!/WEB-INF/classes/".equals(url) && path.contains("classes/")) {
         classname = path.substring(path.lastIndexOf("classes/") + "classes/".length());
      } else {
         classname = path.substring(url.length());
      }

      try {
         return loader.loadClass(className(classname));
      } catch (ClassNotFoundException var5) {
         return loader.loadClass(className(path.substring(path.indexOf("!") + 2)));
      }
   }

   private static String className(String value) {
      return value.substring(0, value.length() - ".class".length()).replace(File.separatorChar, '/').replace('/', '.');
   }

   public void undeployWebApps(AppInfo appInfo) throws Exception {
      for(WebAppInfo webAppInfo : appInfo.webApps) {
         final DeployedWebObjects context = (DeployedWebObjects)this.servletDeploymentInfo.remove(webAppInfo);
         final ServletContextEvent sce = (ServletContextEvent)this.servletContextEvents.remove(webAppInfo);
         List<Object> listenerInstances = (List)this.listeners.remove(webAppInfo);
         if (addServletMethod != null) {
            this.switchServletContextIfNeeded(sce.getServletContext(), new Runnable() {
               public void run() {
                  for(String mapping : context.mappings) {
                     try {
                        LightweightWebAppBuilder.removeServletMethod.invoke((Object)null, mapping, context.webContext);
                     } catch (Exception var5) {
                     }
                  }

                  for(String mapping : context.filterMappings) {
                     try {
                        LightweightWebAppBuilder.removeFilterMethod.invoke((Object)null, mapping, context.webContext);
                     } catch (Exception var4) {
                     }
                  }

               }
            });
         }

         if (listenerInstances != null) {
            for(final Object instance : listenerInstances) {
               if (ServletContextListener.class.isInstance(instance)) {
                  this.switchServletContextIfNeeded(sce.getServletContext(), new Runnable() {
                     public void run() {
                        ((ServletContextListener)instance).contextDestroyed(sce);
                     }
                  });
               }
            }
         }
      }

   }

   public Map<ClassLoader, Map<String, Set<String>>> getJsfClasses() {
      return Collections.emptyMap();
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, LightweightWebAppBuilder.class);

      try {
         Class<?> utilClass = Class.forName("org.apache.openejb.server.httpd.util.HttpUtil", true, LightweightWebAppBuilder.class.getClassLoader());
         addServletMethod = utilClass.getMethod("addServlet", String.class, WebContext.class, String.class);
         removeServletMethod = utilClass.getMethod("removeServlet", String.class, WebContext.class);
         addFilterMethod = utilClass.getMethod("addFilter", String.class, WebContext.class, String.class, FilterConfig.class);
         removeFilterMethod = utilClass.getMethod("removeFilter", String.class, WebContext.class);
         addDefaults = utilClass.getMethod("addDefaultsIfAvailable", WebContext.class);
      } catch (Exception var1) {
         LOGGER.info("Web features will not be available, add openejb-http if you need them");
      }

   }

   private static class DeployedWebObjects {
      public List<String> mappings;
      public List<String> filterMappings;
      public WebContext webContext;

      private DeployedWebObjects() {
         this.mappings = new ArrayList();
         this.filterMappings = new ArrayList();
      }
   }

   private static class EmbeddedInitialContext implements Context {
      private final Context delegate;
      private final Map<String, Object> bindings;

      public EmbeddedInitialContext(Context jndiEnc, Map<String, Object> bindings) {
         this.delegate = jndiEnc;
         this.bindings = bindings;
      }

      public Object lookup(Name name) throws NamingException {
         return this.lookup(name.toString());
      }

      public Object lookup(String name) throws NamingException {
         try {
            return this.delegate.lookup(name);
         } catch (NameNotFoundException var3) {
            return this.bindings.get(name);
         }
      }

      public void bind(Name name, Object obj) throws NamingException {
      }

      public void bind(String name, Object obj) throws NamingException {
      }

      public void rebind(Name name, Object obj) throws NamingException {
      }

      public void rebind(String name, Object obj) throws NamingException {
      }

      public void unbind(Name name) throws NamingException {
      }

      public void unbind(String name) throws NamingException {
      }

      public void rename(Name oldName, Name newName) throws NamingException {
      }

      public void rename(String oldName, String newName) throws NamingException {
      }

      public NamingEnumeration<NameClassPair> list(Name name) throws NamingException {
         return null;
      }

      public NamingEnumeration<NameClassPair> list(String name) throws NamingException {
         return null;
      }

      public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
         return null;
      }

      public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
         return null;
      }

      public void destroySubcontext(Name name) throws NamingException {
      }

      public void destroySubcontext(String name) throws NamingException {
      }

      public Context createSubcontext(Name name) throws NamingException {
         return null;
      }

      public Context createSubcontext(String name) throws NamingException {
         return null;
      }

      public Object lookupLink(Name name) throws NamingException {
         return null;
      }

      public Object lookupLink(String name) throws NamingException {
         return null;
      }

      public NameParser getNameParser(Name name) throws NamingException {
         return null;
      }

      public NameParser getNameParser(String name) throws NamingException {
         return null;
      }

      public Name composeName(Name name, Name prefix) throws NamingException {
         return null;
      }

      public String composeName(String name, String prefix) throws NamingException {
         return null;
      }

      public Object addToEnvironment(String propName, Object propVal) throws NamingException {
         return null;
      }

      public Object removeFromEnvironment(String propName) throws NamingException {
         return null;
      }

      public Hashtable<?, ?> getEnvironment() throws NamingException {
         return null;
      }

      public void close() throws NamingException {
      }

      public String getNameInNamespace() throws NamingException {
         return null;
      }
   }

   private static class SimpleFilterConfig implements FilterConfig {
      private final Properties params;
      private final String name;
      private final ServletContext servletContext;

      public SimpleFilterConfig(ServletContext sc, String name, Properties initParams) {
         this.name = name;
         this.params = initParams;
         this.servletContext = sc;
      }

      public String getFilterName() {
         return this.name;
      }

      public ServletContext getServletContext() {
         return this.servletContext;
      }

      public String getInitParameter(String name) {
         return this.params.getProperty(name);
      }

      public Enumeration<String> getInitParameterNames() {
         return new ArrayEnumeration(this.params.keySet());
      }
   }

   @Event
   public static class EmbeddedServletContextCreated {
      private final ServletContext context;

      public EmbeddedServletContextCreated(ServletContext context) {
         this.context = context;
      }

      public ServletContext getContext() {
         return this.context;
      }

      public String toString() {
         return "EmbeddedServletContextCreated{context=" + this.context + '}';
      }
   }

   public static class LightServletContext extends MockServletContext {
      private final Map<String, Object> attributes = new ConcurrentHashMap();
      private final ServletContext delegate;
      private final ClassLoader loader;

      public LightServletContext(ServletContext delegate, ClassLoader loader) {
         this.delegate = delegate;
         this.loader = loader;
      }

      public ClassLoader getClassLoader() {
         return this.loader;
      }

      public URL getResource(String path) throws MalformedURLException {
         return this.delegate.getResource(path);
      }

      public InputStream getResourceAsStream(String path) {
         return this.delegate.getResourceAsStream(path);
      }

      public int getMajorVersion() {
         return 3;
      }

      public int getEffectiveMajorVersion() {
         return 3;
      }

      public String getVirtualServerName() {
         return "openejb-embedded";
      }

      public void setAttribute(String name, Object object) {
         this.attributes.put(name, object);
      }

      public Object getAttribute(String name) {
         Object o = this.attributes.get(name);
         return o == null ? this.delegate.getAttribute(name) : o;
      }

      public Enumeration<String> getAttributeNames() {
         Set<String> c = new HashSet(this.attributes.keySet());
         c.addAll(Collections.list(this.delegate.getAttributeNames()));
         return Collections.enumeration(c);
      }

      public String getServerInfo() {
         return "EmbeddedOpenEJB/" + OpenEjbVersion.get().getVersion();
      }
   }
}
