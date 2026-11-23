package org.apache.openejb.config;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.annotation.Resources;
import javax.annotation.Resource.AuthenticationType;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.annotation.security.RunAs;
import javax.annotation.sql.DataSourceDefinition;
import javax.annotation.sql.DataSourceDefinitions;
import javax.decorator.Decorator;
import javax.decorator.Delegate;
import javax.ejb.AccessTimeout;
import javax.ejb.ActivationConfigProperty;
import javax.ejb.AfterBegin;
import javax.ejb.AfterCompletion;
import javax.ejb.ApplicationException;
import javax.ejb.Asynchronous;
import javax.ejb.BeforeCompletion;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ConcurrencyManagementType;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.EJBHome;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.EJBObject;
import javax.ejb.EJBs;
import javax.ejb.Init;
import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.LocalHome;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.MessageDriven;
import javax.ejb.PostActivate;
import javax.ejb.PrePassivate;
import javax.ejb.Remote;
import javax.ejb.RemoteHome;
import javax.ejb.Remove;
import javax.ejb.Schedule;
import javax.ejb.Schedules;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Stateful;
import javax.ejb.StatefulTimeout;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.NormalScope;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.DefinitionException;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.AroundTimeout;
import javax.interceptor.ExcludeClassInterceptors;
import javax.interceptor.ExcludeDefaultInterceptors;
import javax.interceptor.Interceptor;
import javax.interceptor.Interceptors;
import javax.jms.JMSConnectionFactoryDefinition;
import javax.jms.JMSConnectionFactoryDefinitions;
import javax.jms.JMSDestinationDefinition;
import javax.jms.JMSDestinationDefinitions;
import javax.jms.Queue;
import javax.jws.WebService;
import javax.persistence.Converter;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.MappedSuperclass;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContexts;
import javax.persistence.PersistenceUnit;
import javax.persistence.PersistenceUnits;
import javax.resource.spi.Activation;
import javax.resource.spi.AdministeredObject;
import javax.resource.spi.ConnectionDefinitions;
import javax.resource.spi.work.WorkContext;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.Provider;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.WebServiceRef;
import javax.xml.ws.WebServiceRefs;
import org.apache.openejb.BeanContext;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.api.LocalClient;
import org.apache.openejb.api.Proxy;
import org.apache.openejb.api.RemoteClient;
import org.apache.openejb.cdi.CdiBeanInfo;
import org.apache.openejb.config.rules.CheckClasses;
import org.apache.openejb.core.EmptyResourcesClassLoader;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.core.TempClassLoader;
import org.apache.openejb.core.webservices.JaxWsUtils;
import org.apache.openejb.dyni.DynamicSubclass;
import org.apache.openejb.jee.ActivationConfig;
import org.apache.openejb.jee.ActivationSpec;
import org.apache.openejb.jee.AdminObject;
import org.apache.openejb.jee.ApplicationClient;
import org.apache.openejb.jee.AssemblyDescriptor;
import org.apache.openejb.jee.AsyncMethod;
import org.apache.openejb.jee.AuthenticationMechanism;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.ConcurrentLockType;
import org.apache.openejb.jee.ConcurrentMethod;
import org.apache.openejb.jee.ConfigProperty;
import org.apache.openejb.jee.ConnectionDefinition;
import org.apache.openejb.jee.Connector;
import org.apache.openejb.jee.ContainerConcurrency;
import org.apache.openejb.jee.ContainerTransaction;
import org.apache.openejb.jee.DataSource;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EjbLocalRef;
import org.apache.openejb.jee.EjbRef;
import org.apache.openejb.jee.Empty;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.ExcludeList;
import org.apache.openejb.jee.FacesConfig;
import org.apache.openejb.jee.FacesManagedBean;
import org.apache.openejb.jee.Filter;
import org.apache.openejb.jee.Handler;
import org.apache.openejb.jee.HandlerChain;
import org.apache.openejb.jee.HandlerChains;
import org.apache.openejb.jee.Icon;
import org.apache.openejb.jee.InboundResourceadapter;
import org.apache.openejb.jee.InitMethod;
import org.apache.openejb.jee.Injectable;
import org.apache.openejb.jee.InjectionTarget;
import org.apache.openejb.jee.InterceptorBinding;
import org.apache.openejb.jee.Invokable;
import org.apache.openejb.jee.IsolationLevel;
import org.apache.openejb.jee.JMSConnectionFactory;
import org.apache.openejb.jee.JMSDestination;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.JndiReference;
import org.apache.openejb.jee.License;
import org.apache.openejb.jee.Lifecycle;
import org.apache.openejb.jee.LifecycleCallback;
import org.apache.openejb.jee.Listener;
import org.apache.openejb.jee.MessageAdapter;
import org.apache.openejb.jee.MessageDrivenBean;
import org.apache.openejb.jee.MessageListener;
import org.apache.openejb.jee.MethodAttribute;
import org.apache.openejb.jee.MethodParams;
import org.apache.openejb.jee.MethodPermission;
import org.apache.openejb.jee.NamedMethod;
import org.apache.openejb.jee.OutboundResourceAdapter;
import org.apache.openejb.jee.ParamValue;
import org.apache.openejb.jee.PersistenceContextRef;
import org.apache.openejb.jee.PersistenceContextSynchronization;
import org.apache.openejb.jee.PersistenceContextType;
import org.apache.openejb.jee.PersistenceUnitRef;
import org.apache.openejb.jee.PortComponent;
import org.apache.openejb.jee.Property;
import org.apache.openejb.jee.RemoteBean;
import org.apache.openejb.jee.RemoveMethod;
import org.apache.openejb.jee.ResAuth;
import org.apache.openejb.jee.ResSharingScope;
import org.apache.openejb.jee.ResourceAdapter;
import org.apache.openejb.jee.ResourceEnvRef;
import org.apache.openejb.jee.ResourceRef;
import org.apache.openejb.jee.SecurityIdentity;
import org.apache.openejb.jee.SecurityPermission;
import org.apache.openejb.jee.SecurityRoleRef;
import org.apache.openejb.jee.ServiceRef;
import org.apache.openejb.jee.Servlet;
import org.apache.openejb.jee.ServletMapping;
import org.apache.openejb.jee.Session;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.SessionType;
import org.apache.openejb.jee.SingletonBean;
import org.apache.openejb.jee.StatefulBean;
import org.apache.openejb.jee.StatelessBean;
import org.apache.openejb.jee.Tag;
import org.apache.openejb.jee.Text;
import org.apache.openejb.jee.Timeout;
import org.apache.openejb.jee.Timer;
import org.apache.openejb.jee.TimerConsumer;
import org.apache.openejb.jee.TimerSchedule;
import org.apache.openejb.jee.TldTaglib;
import org.apache.openejb.jee.TransAttribute;
import org.apache.openejb.jee.TransactionSupportType;
import org.apache.openejb.jee.TransactionType;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.jee.WebserviceDescription;
import org.apache.openejb.jee.EjbReference.Type;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.loader.JarLocation;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.Classes;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.SuperProperties;
import org.apache.openejb.util.URLs;
import org.apache.openejb.util.proxy.DynamicProxyImplFactory;
import org.apache.xbean.finder.Annotated;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.IAnnotationFinder;
import org.apache.xbean.finder.MetaAnnotatedClass;
import org.apache.xbean.finder.archive.Archive;
import org.apache.xbean.finder.archive.ClassesArchive;

public class AnnotationDeployer implements DynamicDeployer {
   public static final Logger logger;
   public static final Logger startupLogger;
   public static final String OPENEJB_JPA_AUTO_SCAN = "openejb.jpa.auto-scan";
   public static final String OPENEJB_JPA_AUTO_SCAN_PACKAGE = "openejb.jpa.auto-scan.package";
   private static final ThreadLocal<DeploymentModule> currentModule;
   private static final Set<String> lookupMissing;
   private static final String[] JSF_CLASSES;
   private static final String[] WEB_CLASSES;
   private static final Collection<String> API_CLASSES;
   public static final Set<String> knownResourceEnvTypes;
   public static final Set<String> knownEnvironmentEntries;
   private final DiscoverAnnotatedBeans discoverAnnotatedBeans = new DiscoverAnnotatedBeans();
   private final ProcessAnnotatedBeans processAnnotatedBeans = new ProcessAnnotatedBeans(SystemInstance.get().getOptions().get("openejb.jaxws.add-remote", false));
   private final EnvEntriesPropertiesDeployer envEntriesPropertiesDeployer = new EnvEntriesPropertiesDeployer();
   private final MBeanDeployer mBeanDeployer = new MBeanDeployer();
   private final BuiltInEnvironmentEntries builtInEnvironmentEntries = new BuiltInEnvironmentEntries(SystemInstance.get().getOptions().get("openejb.environment.default", false));
   private final MergeWebappJndiContext mergeWebappJndiContext = new MergeWebappJndiContext();

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(appModule.getClassLoader());
      setModule(appModule);

      AppModule var3;
      try {
         appModule = this.discoverAnnotatedBeans.deploy(appModule);
         appModule = this.envEntriesPropertiesDeployer.deploy(appModule);
         appModule = this.mergeWebappJndiContext.deploy(appModule);
         appModule = this.builtInEnvironmentEntries.deploy(appModule);
         appModule = this.processAnnotatedBeans.deploy(appModule);
         appModule = this.mergeWebappJndiContext.deploy(appModule);
         appModule = this.mBeanDeployer.deploy(appModule);
         var3 = appModule;
      } finally {
         this.envEntriesPropertiesDeployer.resetAdditionalEnvEntries();
         Thread.currentThread().setContextClassLoader(classLoader);
         removeModule();
      }

      return var3;
   }

   public void deploy(CdiBeanInfo beanInfo) throws OpenEJBException {
      this.processAnnotatedBeans.deploy(beanInfo);
   }

   public WebModule deploy(WebModule webModule) throws OpenEJBException {
      setModule(webModule);
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(webModule.getClassLoader());

      WebModule var3;
      try {
         webModule = this.discoverAnnotatedBeans.deploy(webModule);
         webModule = this.envEntriesPropertiesDeployer.deploy(webModule);
         webModule = this.processAnnotatedBeans.deploy(webModule);
         var3 = webModule;
      } finally {
         this.envEntriesPropertiesDeployer.resetAdditionalEnvEntries();
         Thread.currentThread().setContextClassLoader(classLoader);
         removeModule();
      }

      return var3;
   }

   public static DeploymentModule getModule() {
      return (DeploymentModule)currentModule.get();
   }

   private static void setModule(DeploymentModule module) {
      currentModule.set(module);
   }

   private static void removeModule() {
      currentModule.remove();
   }

   private static ValidationContext getValidationContext() {
      return getModule().getValidation();
   }

   private static void mergeApplicationExceptionAnnotation(AssemblyDescriptor assemblyDescriptor, Class<?> exceptionClass, ApplicationException annotation) {
      org.apache.openejb.jee.ApplicationException applicationException = assemblyDescriptor.getApplicationException(exceptionClass);
      if (applicationException.getRollback() == null) {
         applicationException.setRollback(annotation.rollback());
      }

      if (applicationException.getInherited() == null) {
         applicationException.setInherited(annotation.inherited());
      }

   }

   public static boolean isKnownEnvironmentEntryType(Class type) {
      return knownEnvironmentEntries.contains(type.getName()) || type.isEnum();
   }

   public static boolean isShareableJNDINamespace(String jndiName) {
      return jndiName.startsWith("java:global/") || jndiName.startsWith("java:app/") || jndiName.startsWith("java:module/");
   }

   public static void autoJpa(EjbModule ejbModule) {
      IAnnotationFinder finder = ejbModule.getFinder();
      if (ejbModule.getAppModule() != null) {
         for(PersistenceModule pm : ejbModule.getAppModule().getPersistenceModules()) {
            for(org.apache.openejb.jee.jpa.unit.PersistenceUnit pu : pm.getPersistence().getPersistenceUnit()) {
               if ((pu.isExcludeUnlistedClasses() == null || !pu.isExcludeUnlistedClasses()) && "true".equalsIgnoreCase(pu.getProperties().getProperty("openejb.jpa.auto-scan"))) {
                  doAutoJpa(finder, pu);
               }
            }
         }
      }

   }

   public static void doAutoJpa(IAnnotationFinder finder, org.apache.openejb.jee.jpa.unit.PersistenceUnit pu) {
      String packageName = pu.getProperties().getProperty("openejb.jpa.auto-scan.package");
      String[] packageNames = null;
      if (packageName != null) {
         packageNames = packageName.split(",");
      }

      List<Class<?>> classes = new ArrayList();
      classes.addAll(finder.findAnnotatedClasses(Entity.class));
      classes.addAll(finder.findAnnotatedClasses(Embeddable.class));
      classes.addAll(finder.findAnnotatedClasses(MappedSuperclass.class));
      classes.addAll(finder.findAnnotatedClasses(Converter.class));
      List<String> existingClasses = pu.getClazz();

      for(Class<?> clazz : classes) {
         String name = clazz.getName();
         if (!existingClasses.contains(name)) {
            if (packageNames == null) {
               pu.getClazz().add(name);
            } else {
               for(String pack : packageNames) {
                  if (name.startsWith(pack)) {
                     pu.getClazz().add(name);
                  }
               }
            }
         }
      }

      pu.setScanned(true);
   }

   private static void failIfCdiProducer(Member member, String type) {
      if (FieldMember.class.isInstance(member) && ((FieldMember)FieldMember.class.cast(member)).field.getAnnotation(Produces.class) != null) {
         throw new DefinitionException(((FieldMember)FieldMember.class.cast(member)).field + " is not a " + type);
      }
   }

   private static void addRestClassesToScannedClasses(WebModule webModule, Set<Class> classes, ClassLoader classLoader) throws OpenEJBException {
      for(String rawClassName : webModule.getRestClasses()) {
         String className = realClassName(rawClassName);
         if (className != null) {
            try {
               Class<?> clazz = classLoader.loadClass(className);
               classes.add(clazz);
            } catch (ClassNotFoundException e) {
               throw new OpenEJBException("Unable to load REST class: " + className, e);
            }
         }
      }

   }

   public static List<Annotated<Class<?>>> sortClasses(List<Annotated<Class<?>>> list) {
      Collections.sort(list, new Comparator<Annotated<Class<?>>>() {
         public int compare(Annotated<Class<?>> o1, Annotated<Class<?>> o2) {
            return AnnotationDeployer.compareClasses((Class)o1.get(), (Class)o2.get());
         }
      });
      return list;
   }

   public static List<Class<?>> sortClassesParentFirst(List<Class<?>> list) {
      Collections.sort(list, new Comparator<Class<?>>() {
         public int compare(Class<?> o1, Class<?> o2) {
            return AnnotationDeployer.compareClasses(o2, o1);
         }
      });
      return list;
   }

   public static List<Annotated<Method>> sortMethods(List<Annotated<Method>> list) {
      Collections.sort(list, new Comparator<Annotated<Method>>() {
         public int compare(Annotated<Method> o1, Annotated<Method> o2) {
            return AnnotationDeployer.compareClasses(((Method)o1.get()).getDeclaringClass(), ((Method)o2.get()).getDeclaringClass());
         }
      });
      return list;
   }

   private static int compareClasses(Class<?> a, Class<?> b) {
      if (a == b) {
         return 0;
      } else if (a.isAssignableFrom(b)) {
         return 1;
      } else {
         return b.isAssignableFrom(a) ? -1 : 0;
      }
   }

   public static Collection<String> findRestClasses(WebModule webModule, IAnnotationFinder finder) {
      Collection<String> classes = new HashSet();

      for(Annotated<Class<?>> aClazz : finder.findMetaAnnotatedClasses(Path.class)) {
         Class<?> clazz = (Class)aClazz.get();
         if (isInstantiable(clazz)) {
            if (!isEJB(clazz)) {
               classes.add(clazz.getName());
            } else {
               webModule.getEjbRestServices().add(clazz.getName());
            }
         } else if (clazz.isInterface()) {
            List<Class> impl = finder.findImplementations(clazz);
            if (impl != null && impl.size() == 1) {
               Class implClass = (Class)impl.iterator().next();
               String name = implClass.getName();
               if (!isEJB(implClass)) {
                  classes.add(name);
               } else {
                  webModule.getEjbRestServices().add(name);
               }
            }
         } else if (isEJB(clazz) && DynamicSubclass.isDynamic(clazz)) {
            classes.add(clazz.getName());
         }
      }

      if ("true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.jaxrs.scanning.methods", "false"))) {
         for(Annotated<Method> aMethod : finder.findMetaAnnotatedMethods(Path.class)) {
            Method method = (Method)aMethod.get();
            Class<?> clazz = method.getDeclaringClass();
            if (isInstantiable(clazz)) {
               if (!isEJB(clazz)) {
                  classes.add(clazz.getName());
               } else {
                  webModule.getEjbRestServices().add(clazz.getName());
               }
            } else if (isEJB(clazz) && DynamicSubclass.isDynamic(clazz)) {
               classes.add(clazz.getName());
            }
         }
      }

      return classes;
   }

   public static boolean isInstantiable(Class<?> clazz) {
      int modifiers = clazz.getModifiers();
      return !Modifier.isAbstract(modifiers) && (clazz.getEnclosingClass() == null || Modifier.isStatic(modifiers)) && Modifier.isPublic(modifiers) && !clazz.isEnum();
   }

   private static boolean isEJB(Class<?> clazz) {
      return clazz.isAnnotationPresent(Stateless.class) || clazz.isAnnotationPresent(Singleton.class) || clazz.isAnnotationPresent(ManagedBean.class) || clazz.isAnnotationPresent(Stateful.class);
   }

   private static String realClassName(String rawClassName) {
      if (rawClassName == null) {
         return null;
      } else {
         return rawClassName.contains("/") ? rawClassName.replace("/", ".") : rawClassName;
      }
   }

   private static Collection<Class<?>> metaToClass(List<Annotated<Class<?>>> found) {
      Collection<Class<?>> classes = new ArrayList(found.size());

      for(Annotated<Class<?>> clazz : found) {
         classes.add(clazz.get());
      }

      return classes;
   }

   private static Map<String, Set<String>> addWebAnnotatedClassInfo(Map<String, String> urlByClasses, Map<String, Set<String>> classes, List<Annotated<Class<?>>> found) {
      for(Annotated<Class<?>> clazz : found) {
         Class<?> loadedClass = (Class)clazz.get();
         String name = loadedClass.getName();
         String url = null;
         if (urlByClasses != null) {
            url = (String)urlByClasses.get(name);
         }

         if (url == null) {
            try {
               url = JarLocation.jarLocation(loadedClass).toURI().toURL().toExternalForm();
            } catch (MalformedURLException var11) {
               url = classLocation(loadedClass).toExternalForm();
            }
         }

         Set<String> list = (Set)classes.get(url);
         if (list == null) {
            list = new HashSet();
            classes.put(url, list);
         }

         try {
            File dir = new File((new URL(url)).toURI());
            if (dir.isDirectory()) {
               File fileClazz = new File(dir, name.replace('.', '/') + ".class");
               if (fileClazz.exists()) {
                  list.add(fileClazz.toURI().toURL().toExternalForm());
               } else {
                  list.add(classLocation(loadedClass).toExternalForm());
               }
            } else if (url.endsWith(".jar") && url.startsWith("file:")) {
               list.add("jar:" + url + "!/" + name.replace('.', '/') + ".class");
            } else {
               list.add(classLocation(loadedClass).toExternalForm());
            }
         } catch (Exception var12) {
            list.add(classLocation(loadedClass).toExternalForm());
         }
      }

      return classes;
   }

   public static URL classLocation(Class clazz) {
      try {
         String classFileName = clazz.getName().replace(".", "/") + ".class";
         ClassLoader loader = clazz.getClassLoader();
         URL url;
         if (loader != null) {
            if (TempClassLoader.class.isInstance(loader)) {
               url = ((TempClassLoader)TempClassLoader.class.cast(loader)).getInternalResource(classFileName);
            } else {
               url = loader.getResource(classFileName);
            }
         } else {
            url = clazz.getResource(classFileName);
         }

         if (url == null) {
            throw new IllegalStateException("classloader.getResource(classFileName) returned a null URL");
         } else {
            return url;
         }
      } catch (RuntimeException e) {
         throw e;
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   public EnvEntriesPropertiesDeployer getEnvEntriesPropertiesDeployer() {
      return this.envEntriesPropertiesDeployer;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, AnnotationDeployer.class.getPackage().getName());
      startupLogger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, "org.apache.openejb.util.resources");
      currentModule = new ThreadLocal();
      lookupMissing = new HashSet(2);
      JSF_CLASSES = new String[]{"javax.faces.application.ResourceDependencies", "javax.faces.application.ResourceDependency", "javax.faces.bean.ApplicationScoped", "javax.faces.bean.CustomScoped", "javax.faces.bean.ManagedBean", "javax.faces.bean.ManagedProperty", "javax.faces.bean.NoneScoped", "javax.faces.bean.ReferencedBean", "javax.faces.bean.RequestScoped", "javax.faces.bean.SessionScoped", "javax.faces.bean.ViewScoped", "javax.faces.component.FacesComponent", "javax.faces.component.UIComponent", "javax.faces.convert.Converter", "javax.faces.convert.FacesConverter", "javax.faces.event.ListenerFor", "javax.faces.event.ListenersFor", "javax.faces.event.NamedEvent", "javax.faces.render.FacesBehaviorRenderer", "javax.faces.render.FacesRenderer", "javax.faces.render.Renderer", "javax.faces.validator.FacesValidator", "javax.faces.validator.Validator"};
      WEB_CLASSES = new String[]{"javax.servlet.annotation.WebServlet", "javax.servlet.annotation.WebFilter", "javax.servlet.annotation.WebListener", "javax.websocket.server.ServerEndpoint", "javax.websocket.server.ServerApplicationConfig", "javax.websocket.Endpoint"};
      API_CLASSES = new ArrayList(WEB_CLASSES.length + JSF_CLASSES.length);
      API_CLASSES.addAll(Arrays.asList(JSF_CLASSES));
      API_CLASSES.addAll(Arrays.asList(WEB_CLASSES));
      knownResourceEnvTypes = new TreeSet(Arrays.asList("javax.ejb.EJBContext", "javax.ejb.SessionContext", "javax.ejb.EntityContext", "javax.ejb.MessageDrivenContext", "javax.transaction.UserTransaction", "javax.jms.Queue", "javax.jms.Topic", "javax.xml.ws.WebServiceContext", "javax.ejb.TimerService", "javax.enterprise.inject.spi.BeanManager", "javax.validation.Validator", "javax.validation.ValidatorFactory"));
      knownEnvironmentEntries = new TreeSet(Arrays.asList("boolean", "java.lang.Boolean", "char", "java.lang.Character", "byte", "java.lang.Byte", "short", "java.lang.Short", "int", "java.lang.Integer", "long", "java.lang.Long", "float", "java.lang.Float", "double", "java.lang.Double", "java.lang.String", "java.lang.Class"));
   }

   public static class DiscoverAnnotatedBeans implements DynamicDeployer {
      public AppModule deploy(AppModule appModule) throws OpenEJBException {
         if (!appModule.isWebapp() && !appModule.getWebModules().isEmpty()) {
            try {
               appModule.setEarLibFinder(FinderFactory.createFinder(appModule));
            } catch (Exception e) {
               AnnotationDeployer.logger.error("Can't create a finder for ear libs", (Throwable)e);
            }
         }

         for(EjbModule ejbModule : appModule.getEjbModules()) {
            ejbModule.initAppModule(appModule);
            AnnotationDeployer.setModule(ejbModule);

            try {
               this.deploy(ejbModule);
            } finally {
               AnnotationDeployer.removeModule();
            }
         }

         for(ClientModule clientModule : appModule.getClientModules()) {
            clientModule.initAppModule(appModule);
            AnnotationDeployer.setModule(clientModule);

            try {
               this.deploy(clientModule);
            } finally {
               AnnotationDeployer.removeModule();
            }
         }

         for(ConnectorModule connectorModule : appModule.getConnectorModules()) {
            connectorModule.initAppModule(appModule);
            AnnotationDeployer.setModule(connectorModule);

            try {
               this.deploy(connectorModule);
            } finally {
               AnnotationDeployer.removeModule();
            }
         }

         for(WebModule webModule : appModule.getWebModules()) {
            webModule.initAppModule(appModule);
            AnnotationDeployer.setModule(webModule);

            try {
               this.deploy(webModule);
            } finally {
               AnnotationDeployer.removeModule();
            }
         }

         AdditionalBeanDiscoverer discoverer = (AdditionalBeanDiscoverer)SystemInstance.get().getComponent(AdditionalBeanDiscoverer.class);
         if (discoverer != null) {
            appModule = discoverer.discover(appModule);
         }

         return appModule;
      }

      public ClientModule deploy(ClientModule clientModule) throws OpenEJBException {
         if (clientModule.getApplicationClient() == null) {
            clientModule.setApplicationClient(new ApplicationClient());
         }

         if (clientModule.getFinder() == null && clientModule.getAltDDs().containsKey("application-client.xml") && clientModule.getApplicationClient() != null && clientModule.getApplicationClient().isMetadataComplete()) {
            return clientModule;
         } else {
            IAnnotationFinder finder = clientModule.getFinder();
            if (finder == null) {
               try {
                  finder = FinderFactory.createFinder(clientModule);
               } catch (MalformedURLException var5) {
                  AnnotationDeployer.startupLogger.warning("startup.scrapeFailedForClientModule.url", clientModule.getJarLocation());
                  return clientModule;
               } catch (Exception e) {
                  AnnotationDeployer.startupLogger.warning("startup.scrapeFailedForClientModule", e, clientModule.getJarLocation());
                  return clientModule;
               }
            }

            for(Annotated<Class<?>> clazz : finder.findMetaAnnotatedClasses(LocalClient.class)) {
               clientModule.getLocalClients().add(((Class)clazz.get()).getName());
            }

            for(Annotated<Class<?>> clazz : finder.findMetaAnnotatedClasses(RemoteClient.class)) {
               clientModule.getRemoteClients().add(((Class)clazz.get()).getName());
            }

            if (clientModule.getApplicationClient() == null && (clientModule.getRemoteClients().size() > 0 || clientModule.getLocalClients().size() > 0)) {
               clientModule.setApplicationClient(new ApplicationClient());
            }

            return clientModule;
         }
      }

      public ConnectorModule deploy(ConnectorModule connectorModule) throws OpenEJBException {
         Connector connector = connectorModule.getConnector();
         if (connector == null) {
            connector = new Connector();
         }

         float specVersion = 0.0F;

         try {
            specVersion = Float.parseFloat(connector.getVersion());
         } catch (Exception var22) {
         }

         if (!((double)specVersion < 1.6) && !Boolean.TRUE.equals(connector.isMetadataComplete())) {
            IAnnotationFinder finder = connectorModule.getFinder();
            if (finder == null) {
               try {
                  finder = FinderFactory.createFinder(connectorModule);
                  connectorModule.setFinder(finder);
               } catch (Exception var21) {
                  return connectorModule;
               }
            }

            List<Class<?>> connectorClasses = finder.findAnnotatedClasses(javax.resource.spi.Connector.class);
            if (connector.getResourceAdapter() == null || connector.getResourceAdapter().getResourceAdapterClass() == null || connector.getResourceAdapter().getResourceAdapterClass().length() == 0) {
               if (connectorClasses.size() == 0) {
               }

               if (connectorClasses.size() > 1) {
               }
            }

            Class<?> connectorClass = null;
            if (connectorClasses.size() == 1) {
               connectorClass = (Class)connectorClasses.get(0);
            }

            if (connectorClasses.size() > 1) {
               for(Class<?> cls : connectorClasses) {
                  if (cls.getName().equals(connector.getResourceAdapter().getResourceAdapterClass())) {
                     connectorClass = cls;
                     break;
                  }
               }
            }

            if (connectorClass != null) {
               if (connector.getResourceAdapter() == null) {
                  connector.setResourceAdapter(new ResourceAdapter());
               }

               if (connector.getResourceAdapter().getResourceAdapterClass() == null || connector.getResourceAdapter().getResourceAdapterClass().length() == 0) {
                  connector.getResourceAdapter().setResourceAdapterClass(connectorClass.getName());
               }

               javax.resource.spi.Connector connectorAnnotation = (javax.resource.spi.Connector)connectorClass.getAnnotation(javax.resource.spi.Connector.class);
               connector.setDisplayNames(this.getTexts(connector.getDisplayNames(), connectorAnnotation.displayName()));
               connector.setDescriptions(this.getTexts(connector.getDescriptions(), connectorAnnotation.description()));
               connector.setEisType(this.getString(connector.getEisType(), connectorAnnotation.eisType()));
               connector.setVendorName(this.getString(connector.getVendorName(), connectorAnnotation.vendorName()));
               connector.setResourceAdapterVersion(this.getString(connector.getResourceAdapterVersion(), connectorAnnotation.version()));
               if (connector.getIcons().isEmpty()) {
                  int smallIcons = connectorAnnotation.smallIcon().length;
                  int largeIcons = connectorAnnotation.largeIcon().length;

                  for(int i = 0; i < smallIcons && i < largeIcons; ++i) {
                     Icon icon = new Icon();
                     icon.setLang(Locale.getDefault().getLanguage());
                     if (i < smallIcons) {
                        icon.setSmallIcon(connectorAnnotation.smallIcon()[i]);
                     }

                     if (i < largeIcons) {
                        icon.setLargeIcon(connectorAnnotation.largeIcon()[i]);
                     }

                     connector.getIcons().add(icon);
                  }
               }

               if (connector.getLicense() == null) {
                  License license = new License();
                  connector.setLicense(license);
                  license.setLicenseRequired(connectorAnnotation.licenseRequired());
               }

               connector.getLicense().setDescriptions(this.getTexts(connector.getLicense().getDescriptions(), connectorAnnotation.licenseDescription()));
               javax.resource.spi.SecurityPermission[] annotationSecurityPermissions = connectorAnnotation.securityPermissions();
               List<SecurityPermission> securityPermission = connector.getResourceAdapter().getSecurityPermission();
               if (securityPermission == null || securityPermission.size() == 0) {
                  for(javax.resource.spi.SecurityPermission sp : annotationSecurityPermissions) {
                     SecurityPermission permission = new SecurityPermission();
                     permission.setSecurityPermissionSpec(sp.permissionSpec());
                     permission.setDescriptions(this.stringsToTexts(sp.description()));
                     securityPermission.add(permission);
                  }
               }

               Class<? extends WorkContext>[] annotationRequiredWorkContexts = connectorAnnotation.requiredWorkContexts();
               List<String> requiredWorkContext = connector.getRequiredWorkContext();
               if (requiredWorkContext.size() == 0) {
                  for(Class<? extends WorkContext> cls : annotationRequiredWorkContexts) {
                     requiredWorkContext.add(cls.getName());
                  }
               }

               OutboundResourceAdapter outboundResourceAdapter = connector.getResourceAdapter().getOutboundResourceAdapter();
               if (outboundResourceAdapter == null) {
                  outboundResourceAdapter = new OutboundResourceAdapter();
                  connector.getResourceAdapter().setOutboundResourceAdapter(outboundResourceAdapter);
               }

               List<AuthenticationMechanism> authenticationMechanisms = outboundResourceAdapter.getAuthenticationMechanism();
               javax.resource.spi.AuthenticationMechanism[] authMechanisms = connectorAnnotation.authMechanisms();
               if (authenticationMechanisms.size() == 0) {
                  for(javax.resource.spi.AuthenticationMechanism am : authMechanisms) {
                     AuthenticationMechanism authMechanism = new AuthenticationMechanism();
                     authMechanism.setAuthenticationMechanismType(am.authMechanism());
                     authMechanism.setCredentialInterface(am.credentialInterface().toString());
                     authMechanism.setDescriptions(this.stringsToTexts(am.description()));
                     authenticationMechanisms.add(authMechanism);
                  }
               }

               if (outboundResourceAdapter.getTransactionSupport() == null) {
                  outboundResourceAdapter.setTransactionSupport(TransactionSupportType.fromValue(connectorAnnotation.transactionSupport().toString()));
               }

               if (outboundResourceAdapter.isReauthenticationSupport() == null) {
                  outboundResourceAdapter.setReauthenticationSupport(connectorAnnotation.reauthenticationSupport());
               }
            }

            for(Class<?> cls : finder.findAnnotatedClasses(ConnectionDefinitions.class)) {
               ConnectionDefinitions connectionDefinitionsAnnotation = (ConnectionDefinitions)cls.getAnnotation(ConnectionDefinitions.class);
               javax.resource.spi.ConnectionDefinition[] definitions = connectionDefinitionsAnnotation.value();

               for(javax.resource.spi.ConnectionDefinition definition : definitions) {
                  this.processConnectionDescription(connector.getResourceAdapter(), definition, cls);
               }
            }

            for(Class<?> cls : finder.findAnnotatedClasses(javax.resource.spi.ConnectionDefinition.class)) {
               javax.resource.spi.ConnectionDefinition connectionDefinitionAnnotation = (javax.resource.spi.ConnectionDefinition)cls.getAnnotation(javax.resource.spi.ConnectionDefinition.class);
               this.processConnectionDescription(connector.getResourceAdapter(), connectionDefinitionAnnotation, cls);
            }

            InboundResourceadapter inboundResourceAdapter = connector.getResourceAdapter().getInboundResourceAdapter();
            if (inboundResourceAdapter == null) {
               inboundResourceAdapter = new InboundResourceadapter();
               connector.getResourceAdapter().setInboundResourceAdapter(inboundResourceAdapter);
            }

            MessageAdapter messageAdapter = inboundResourceAdapter.getMessageAdapter();
            if (messageAdapter == null) {
               messageAdapter = new MessageAdapter();
               inboundResourceAdapter.setMessageAdapter(messageAdapter);
            }

            for(Class<?> cls : finder.findAnnotatedClasses(Activation.class)) {
               MessageListener messageListener = null;
               Activation activationAnnotation = (Activation)cls.getAnnotation(Activation.class);

               for(MessageListener ml : messageAdapter.getMessageListener()) {
                  if (cls.getName().equals(ml.getActivationSpec().getActivationSpecClass())) {
                     messageListener = ml;
                     break;
                  }
               }

               if (messageListener == null) {
                  Class<?>[] listeners = activationAnnotation.messageListeners();

                  for(Class<?> listener : listeners) {
                     messageAdapter.addMessageListener(new MessageListener(listener.getName(), cls.getName()));
                  }
               }
            }

            List<Class<?>> var27 = finder.findAnnotatedClasses(AdministeredObject.class);
            List<AdminObject> adminObjects = connector.getResourceAdapter().getAdminObject();

            for(Class<?> cls : var27) {
               AdministeredObject administeredObjectAnnotation = (AdministeredObject)cls.getAnnotation(AdministeredObject.class);
               Class[] adminObjectInterfaces = administeredObjectAnnotation.adminObjectInterfaces();
               AdminObject adminObject = null;

               for(AdminObject admObj : adminObjects) {
                  if (admObj.getAdminObjectClass().equals(cls.getName())) {
                     adminObject = admObj;
                  }
               }

               if (adminObject == null) {
                  for(Class iface : adminObjectInterfaces) {
                     AdminObject newAdminObject = new AdminObject();
                     newAdminObject.setAdminObjectClass(cls.getName());
                     newAdminObject.setAdminObjectInterface(iface.getName());
                     adminObjects.add(newAdminObject);
                  }
               }
            }

            String raCls = connector.getResourceAdapter().getResourceAdapterClass();
            this.process(connectorModule.getClassLoader(), raCls, connector.getResourceAdapter());
            if (connector.getResourceAdapter() != null && connector.getResourceAdapter().getOutboundResourceAdapter() != null) {
               for(ConnectionDefinition connectionDefinition : connector.getResourceAdapter().getOutboundResourceAdapter().getConnectionDefinition()) {
                  this.process(connectorModule.getClassLoader(), connectionDefinition.getManagedConnectionFactoryClass(), connectionDefinition);
               }
            }

            if (connector.getResourceAdapter() != null) {
               for(AdminObject raAdminObject : connector.getResourceAdapter().getAdminObject()) {
                  this.process(connectorModule.getClassLoader(), raAdminObject.getAdminObjectClass(), raAdminObject);
               }
            }

            if (connector.getResourceAdapter() != null && connector.getResourceAdapter().getInboundResourceAdapter() != null && connector.getResourceAdapter().getInboundResourceAdapter().getMessageAdapter() != null) {
               for(MessageListener messageListener : connector.getResourceAdapter().getInboundResourceAdapter().getMessageAdapter().getMessageListener()) {
                  ActivationSpec activationSpec = messageListener.getActivationSpec();
                  this.process(connectorModule.getClassLoader(), activationSpec.getActivationSpecClass(), activationSpec);
               }
            }

            return connectorModule;
         } else {
            return connectorModule;
         }
      }

      void process(ClassLoader cl, String cls, Object object) {
         List<ConfigProperty> configProperties = null;

         try {
            configProperties = (List)object.getClass().getDeclaredMethod("getConfigProperty").invoke(object);
         } catch (Exception var25) {
         }

         if (configProperties != null) {
            ClassLoader classLoader = cl;
            if (cl == null) {
               classLoader = Thread.currentThread().getContextClassLoader();
            }

            List<String> allowedTypes = Arrays.asList(Boolean.class.getName(), String.class.getName(), Integer.class.getName(), Double.class.getName(), Byte.class.getName(), Short.class.getName(), Long.class.getName(), Float.class.getName(), Character.class.getName());

            try {
               Class<?> clazz = classLoader.loadClass(AnnotationDeployer.realClassName(cls));
               Object o = clazz.newInstance();
               BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
               PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

               for(PropertyDescriptor propertyDescriptor : propertyDescriptors) {
                  String name = propertyDescriptor.getName();
                  Class<?> type = propertyDescriptor.getPropertyType();
                  if (type != null) {
                     if (type.isPrimitive()) {
                        type = this.getWrapper(type.getName());
                     }

                     if (allowedTypes.contains(type.getName()) && !this.containsConfigProperty(configProperties, name)) {
                        ConfigProperty configProperty = new ConfigProperty();
                        configProperties.add(configProperty);
                        Object value = null;
                        if (propertyDescriptor.getReadMethod() != null) {
                           try {
                              value = propertyDescriptor.getReadMethod().invoke(o);
                           } catch (Exception var24) {
                           }
                        }

                        Method write = propertyDescriptor.getWriteMethod();
                        javax.resource.spi.ConfigProperty annotation = null;
                        if (write != null) {
                           annotation = (javax.resource.spi.ConfigProperty)write.getAnnotation(javax.resource.spi.ConfigProperty.class);
                           if (annotation == null) {
                              try {
                                 annotation = (javax.resource.spi.ConfigProperty)clazz.getDeclaredField(name).getAnnotation(javax.resource.spi.ConfigProperty.class);
                              } catch (Exception var23) {
                              }
                           }
                        }

                        configProperty.setConfigPropertyName(name);
                        configProperty.setConfigPropertyType(this.getConfigPropertyType(annotation, type));
                        if (value != null) {
                           configProperty.setConfigPropertyValue(value.toString());
                        }

                        if (annotation != null) {
                           if (annotation.defaultValue() != null && annotation.defaultValue().length() > 0) {
                              configProperty.setConfigPropertyValue(annotation.defaultValue());
                           }

                           configProperty.setConfigPropertyConfidential(annotation.confidential());
                           configProperty.setConfigPropertyIgnore(annotation.ignore());
                           configProperty.setConfigPropertySupportsDynamicUpdates(annotation.supportsDynamicUpdates());
                           configProperty.setDescriptions(this.stringsToTexts(annotation.description()));
                        }
                     }
                  }
               }

               Field[] declaredFields = clazz.getDeclaredFields();

               for(Field field : declaredFields) {
                  javax.resource.spi.ConfigProperty annotation = (javax.resource.spi.ConfigProperty)field.getAnnotation(javax.resource.spi.ConfigProperty.class);
                  String name = field.getName();
                  Object value = null;

                  try {
                     value = field.get(o);
                  } catch (Exception var22) {
                  }

                  if (!this.containsConfigProperty(configProperties, name)) {
                     String type = this.getConfigPropertyType(annotation, field.getType());
                     if (type != null) {
                        ConfigProperty configProperty = new ConfigProperty();
                        configProperties.add(configProperty);
                        configProperty.setConfigPropertyName(name);
                        configProperty.setConfigPropertyType(type);
                        if (value != null) {
                           configProperty.setConfigPropertyValue(value.toString());
                        }

                        if (annotation != null) {
                           if (annotation.defaultValue() != null) {
                              configProperty.setConfigPropertyValue(annotation.defaultValue());
                           }

                           configProperty.setConfigPropertyConfidential(annotation.confidential());
                           configProperty.setConfigPropertyIgnore(annotation.ignore());
                           configProperty.setConfigPropertySupportsDynamicUpdates(annotation.supportsDynamicUpdates());
                        }
                     }
                  }
               }
            } catch (Exception e) {
               e.printStackTrace();
            }

         }
      }

      private String getConfigPropertyType(javax.resource.spi.ConfigProperty annotation, Class<?> type) {
         Class<?> t = annotation == null ? null : annotation.type();
         if (t == null && type != null) {
            return type.getName();
         } else if (t == null) {
            return null;
         } else {
            if (t.equals(Object.class)) {
               t = type;
            }

            if (t == null) {
               return null;
            } else {
               if (t.isPrimitive()) {
                  t = this.getWrapper(t.getName());
               }

               return t.getName();
            }
         }
      }

      private boolean containsConfigProperty(List<ConfigProperty> configProperties, String name) {
         for(ConfigProperty configProperty : configProperties) {
            if (configProperty.getConfigPropertyName().equals(name)) {
               return true;
            }
         }

         return false;
      }

      private Class<?> getWrapper(String primitiveType) {
         Map<String, Class<?>> builtInMap = new HashMap();
         builtInMap.put("int", Integer.class);
         builtInMap.put("long", Long.class);
         builtInMap.put("double", Double.class);
         builtInMap.put("float", Float.class);
         builtInMap.put("boolean", Boolean.class);
         builtInMap.put("char", Character.class);
         builtInMap.put("byte", Byte.class);
         builtInMap.put("void", Void.class);
         builtInMap.put("short", Short.class);
         return (Class)builtInMap.get(primitiveType);
      }

      private void processConnectionDescription(ResourceAdapter resourceAdapter, javax.resource.spi.ConnectionDefinition connectionDefinitionAnnotation, Class<?> cls) {
         OutboundResourceAdapter outboundResourceAdapter = resourceAdapter.getOutboundResourceAdapter();
         if (outboundResourceAdapter == null) {
            outboundResourceAdapter = new OutboundResourceAdapter();
            resourceAdapter.setOutboundResourceAdapter(outboundResourceAdapter);
         }

         List<ConnectionDefinition> connectionDefinition = outboundResourceAdapter.getConnectionDefinition();
         ConnectionDefinition definition = null;

         for(ConnectionDefinition cd : connectionDefinition) {
            if (cd.getManagedConnectionFactoryClass().equals(cls.getName())) {
               definition = cd;
               break;
            }
         }

         if (definition == null) {
            definition = new ConnectionDefinition();
            outboundResourceAdapter.getConnectionDefinition().add(definition);
         }

         if (definition.getManagedConnectionFactoryClass() == null) {
            definition.setManagedConnectionFactoryClass(cls.getName());
         }

         if (definition.getConnectionInterface() == null) {
            definition.setConnectionInterface(connectionDefinitionAnnotation.connection().getName());
         }

         if (definition.getConnectionImplClass() == null) {
            definition.setConnectionImplClass(connectionDefinitionAnnotation.connectionImpl().getName());
         }

         if (definition.getConnectionFactoryInterface() == null) {
            definition.setConnectionFactoryInterface(connectionDefinitionAnnotation.connectionFactory().getName());
         }

         if (definition.getConnectionFactoryImplClass() == null) {
            definition.setConnectionFactoryImplClass(connectionDefinitionAnnotation.connectionFactoryImpl().getName());
         }

      }

      private Text[] stringsToTexts(String[] strings) {
         if (strings == null) {
            return null;
         } else {
            Text[] result = new Text[strings.length];

            for(int i = 0; i < result.length; ++i) {
               result[i] = new Text();
               result[i].setValue(strings[i]);
            }

            return result;
         }
      }

      private String getString(String descriptorString, String annotationString) {
         if (descriptorString != null && descriptorString.length() > 0) {
            return descriptorString;
         } else {
            return annotationString != null && annotationString.length() > 0 ? annotationString : null;
         }
      }

      private Text[] getTexts(Text[] originalTexts, String[] newStrings) {
         if (newStrings != null && newStrings.length > 0 && (originalTexts == null || originalTexts.length == 0)) {
            Text[] texts = new Text[newStrings.length];

            for(int i = 0; i < newStrings.length; ++i) {
               texts[i] = new Text((String)null, newStrings[i]);
            }

            return texts;
         } else {
            return originalTexts;
         }
      }

      public WebModule deploy(WebModule webModule) throws OpenEJBException {
         WebApp webApp = webModule.getWebApp();
         if (webApp != null && webApp.isMetadataComplete()) {
            return webModule;
         } else {
            try {
               if (webModule.getFinder() == null) {
                  webModule.setFinder(FinderFactory.createFinder(webModule));
               }
            } catch (Exception e) {
               AnnotationDeployer.startupLogger.warning("Unable to scrape for @WebService or @WebServiceProvider annotations. AnnotationFinder failed.", (Throwable)e);
               return webModule;
            }

            if (webApp == null) {
               webApp = new WebApp();
               webModule.setWebApp(webApp);
            }

            List<String> existingServlets = new ArrayList();

            for(Servlet servlet : webApp.getServlet()) {
               if (servlet.getServletClass() != null) {
                  existingServlets.add(servlet.getServletClass());
               }
            }

            IAnnotationFinder finder = webModule.getFinder();
            List<Class> classes = new ArrayList();
            classes.addAll(finder.findAnnotatedClasses(WebService.class));
            classes.addAll(finder.findAnnotatedClasses(WebServiceProvider.class));

            for(Class<?> webServiceClass : classes) {
               if (!webServiceClass.isAnnotationPresent(Singleton.class) && !webServiceClass.isAnnotationPresent(Stateless.class)) {
                  int modifiers = webServiceClass.getModifiers();
                  if (Modifier.isPublic(modifiers) && !Modifier.isFinal(modifiers) && !Modifier.isAbstract(modifiers) && !existingServlets.contains(webServiceClass.getName())) {
                     Servlet servlet = new Servlet();
                     servlet.setServletName(webServiceClass.getName());
                     servlet.setServletClass(webServiceClass.getName());
                     ParamValue param = new ParamValue();
                     param.setParamName("openejb-internal");
                     param.setParamValue("true");
                     servlet.getInitParam().add(param);
                     webApp.getServlet().add(servlet);
                  }
               } else {
                  webModule.getEjbWebServices().add(webServiceClass.getName());
               }
            }

            boolean restHandledByTheWebApp;
            try {
               restHandledByTheWebApp = webModule.getClassLoader().loadClass(Application.class.getName()) != Application.class;
            } catch (Throwable var22) {
               restHandledByTheWebApp = false;
            }

            if (!restHandledByTheWebApp) {
               webModule.getRestClasses().addAll(AnnotationDeployer.findRestClasses(webModule, finder));
               this.addJaxRsProviders(finder, webModule.getJaxrsProviders(), Provider.class);

               for(Class<? extends Application> app : finder.findSubclasses(Application.class)) {
                  addRestApplicationIfPossible(webModule, app);
               }

               for(Annotated<Class<?>> annotatedApp : finder.findMetaAnnotatedClasses(ApplicationPath.class)) {
                  Class<?> app = (Class)annotatedApp.get();
                  if (!Application.class.isAssignableFrom(app)) {
                     AnnotationDeployer.logger.error("class '" + app.getName() + "' is annotated with @ApplicationPath but doesn't implement " + Application.class.getName());
                  } else {
                     addRestApplicationIfPossible(webModule, app);
                  }
               }
            }

            IAnnotationFinder parentFinder = null;
            AppModule appModule = webModule.getAppModule();
            if (appModule != null) {
               parentFinder = appModule.getEarLibFinder();
            }

            ClassLoader classLoader = webModule.getClassLoader();

            for(String jsfClass : AnnotationDeployer.JSF_CLASSES) {
               Class<? extends Annotation> clazz;
               try {
                  clazz = classLoader.loadClass(jsfClass);
               } catch (ClassNotFoundException var24) {
                  continue;
               }

               Set<String> convertedClasses = new HashSet();
               if (parentFinder != null) {
                  for(Annotated<Class<?>> annotated : parentFinder.findMetaAnnotatedClasses(clazz)) {
                     convertedClasses.add(((Class)annotated.get()).getName());
                  }

                  for(EjbModule module : appModule.getEjbModules()) {
                     if (!appModule.isWebapp() && (!module.isWebapp() || module.getModuleId().equals(webModule.getModuleId()))) {
                        for(Annotated<Class<?>> annotated : module.getFinder().findMetaAnnotatedClasses(clazz)) {
                           convertedClasses.add(((Class)annotated.get()).getName());
                        }
                     }
                  }
               }

               for(Annotated<Class<?>> annotated : finder.findMetaAnnotatedClasses(clazz)) {
                  convertedClasses.add(((Class)annotated.get()).getName());
               }

               webModule.getJsfAnnotatedClasses().put(jsfClass, convertedClasses);
            }

            Map<String, String> urlByClasses = null;

            for(String apiClassName : AnnotationDeployer.WEB_CLASSES) {
               Class<? extends Annotation> clazz;
               try {
                  clazz = classLoader.loadClass(apiClassName);
               } catch (ClassNotFoundException var23) {
                  continue;
               }

               if (urlByClasses == null) {
                  urlByClasses = FinderFactory.urlByClass(finder);
               }

               List<Annotated<Class<?>>> found = finder.findMetaAnnotatedClasses(clazz);
               AnnotationDeployer.addWebAnnotatedClassInfo(urlByClasses, webModule.getWebAnnotatedClasses(), found);
            }

            if (urlByClasses != null) {
               urlByClasses.clear();
            }

            return webModule;
         }
      }

      private void addJaxRsProviders(IAnnotationFinder finder, Collection<String> set, Class<? extends Annotation> annotation) {
         for(Annotated<Class<?>> provider : finder.findMetaAnnotatedClasses(annotation)) {
            set.add(((Class)provider.get()).getName());
         }

      }

      private static void addRestApplicationIfPossible(WebModule webModule, Class<? extends Application> app) {
         if (AnnotationDeployer.isInstantiable(app)) {
            if (app.getConstructors().length == 0) {
               webModule.getRestApplications().add(app.getName());
            } else {
               for(Constructor<?> ctr : app.getConstructors()) {
                  if (ctr.getParameterTypes().length == 0) {
                     webModule.getRestApplications().add(app.getName());
                     break;
                  }
               }
            }

         }
      }

      public EjbModule deploy(EjbModule ejbModule) throws OpenEJBException {
         if (ejbModule.getEjbJar() != null && ejbModule.getEjbJar().isMetadataComplete()) {
            return ejbModule;
         } else {
            try {
               if (ejbModule.getFinder() == null) {
                  ejbModule.setFinder(FinderFactory.createFinder(ejbModule));
               }
            } catch (MalformedURLException var19) {
               AnnotationDeployer.startupLogger.warning("startup.scrapeFailedForModule", ejbModule.getJarLocation());
               return ejbModule;
            } catch (Exception e) {
               AnnotationDeployer.startupLogger.warning("Unable to scrape for @Stateful, @Stateless, @Singleton or @MessageDriven annotations. AnnotationFinder failed.", (Throwable)e);
               return ejbModule;
            }

            IAnnotationFinder finder = ejbModule.getFinder();

            for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
               if (bean instanceof SessionBean) {
                  SessionBean sessionBean = (SessionBean)bean;
                  if (sessionBean.getSessionType() == null) {
                     try {
                        Class<?> clazz = ejbModule.getClassLoader().loadClass(bean.getEjbClass());
                        sessionBean.setSessionType(this.getSessionType(clazz));
                     } catch (Throwable var18) {
                     }
                  }
               }
            }

            for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
               if (bean.getEjbClass() != null && (bean.getEjbName() == null || bean.getEjbName().startsWith("@NULL@"))) {
                  ejbModule.getEjbJar().removeEnterpriseBean(bean.getEjbName());

                  try {
                     Class<?> clazz = ejbModule.getClassLoader().loadClass(bean.getEjbClass());
                     String ejbName = this.getEjbName(bean, clazz);
                     bean.setEjbName(ejbName);
                  } catch (Throwable var17) {
                  }

                  ejbModule.getEjbJar().addEnterpriseBean(bean);
               }
            }

            EjbJar ejbJar = ejbModule.getEjbJar();

            for(Annotated<Class<?>> beanClass : finder.findMetaAnnotatedClasses(Singleton.class)) {
               Singleton singleton = (Singleton)beanClass.getAnnotation(Singleton.class);
               String ejbName = this.getEjbName(singleton, (Class)beanClass.get());
               if (this.isValidEjbAnnotationUsage(Singleton.class, beanClass, ejbName, ejbModule)) {
                  EnterpriseBean enterpriseBean = ejbJar.getEnterpriseBean(ejbName);
                  if (enterpriseBean == null) {
                     enterpriseBean = new SingletonBean(ejbName, (Class)beanClass.get());
                     ejbJar.addEnterpriseBean(enterpriseBean);
                  }

                  if (enterpriseBean.getEjbClass() == null) {
                     enterpriseBean.setEjbClass((Class)beanClass.get());
                  }

                  if (enterpriseBean instanceof SessionBean) {
                     SessionBean sessionBean = (SessionBean)enterpriseBean;
                     sessionBean.setSessionType(SessionType.SINGLETON);
                     if (singleton.mappedName() != null) {
                        sessionBean.setMappedName(singleton.mappedName());
                     }
                  }

                  LegacyProcessor.process((Class)beanClass.get(), enterpriseBean);
               }
            }

            for(Annotated<Class<?>> beanClass : finder.findMetaAnnotatedClasses(Stateless.class)) {
               Stateless stateless = (Stateless)beanClass.getAnnotation(Stateless.class);
               String ejbName = this.getEjbName(stateless, (Class)beanClass.get());
               if (this.isValidEjbAnnotationUsage(Stateless.class, beanClass, ejbName, ejbModule)) {
                  EnterpriseBean enterpriseBean = ejbJar.getEnterpriseBean(ejbName);
                  if (enterpriseBean == null) {
                     enterpriseBean = new StatelessBean(ejbName, (Class)beanClass.get());
                     ejbJar.addEnterpriseBean(enterpriseBean);
                  }

                  if (enterpriseBean.getEjbClass() == null) {
                     enterpriseBean.setEjbClass((Class)beanClass.get());
                  }

                  if (enterpriseBean instanceof SessionBean) {
                     SessionBean sessionBean = (SessionBean)enterpriseBean;
                     sessionBean.setSessionType(SessionType.STATELESS);
                     if (stateless.mappedName() != null) {
                        sessionBean.setMappedName(stateless.mappedName());
                     }
                  }

                  LegacyProcessor.process((Class)beanClass.get(), enterpriseBean);
               }
            }

            for(Annotated<Class<?>> beanClass : finder.findMetaAnnotatedClasses(Stateful.class)) {
               Stateful stateful = (Stateful)beanClass.getAnnotation(Stateful.class);
               String ejbName = this.getEjbName(stateful, (Class)beanClass.get());
               if (this.isValidEjbAnnotationUsage(Stateful.class, beanClass, ejbName, ejbModule)) {
                  EnterpriseBean enterpriseBean = ejbJar.getEnterpriseBean(ejbName);
                  if (enterpriseBean == null) {
                     enterpriseBean = new StatefulBean(ejbName, (Class)beanClass.get());
                     ejbJar.addEnterpriseBean(enterpriseBean);
                  }

                  if (enterpriseBean.getEjbClass() == null) {
                     enterpriseBean.setEjbClass((Class)beanClass.get());
                  }

                  if (enterpriseBean instanceof SessionBean) {
                     SessionBean sessionBean = (SessionBean)enterpriseBean;
                     sessionBean.setSessionType(SessionType.STATEFUL);
                     if (stateful.mappedName() != null) {
                        sessionBean.setMappedName(stateful.mappedName());
                     }

                     if (sessionBean.getPassivationCapable() == null) {
                        sessionBean.setPassivationCapable(stateful.passivationCapable());
                     }
                  }

                  LegacyProcessor.process((Class)beanClass.get(), enterpriseBean);
               }
            }

            for(Annotated<Class<?>> beanClass : finder.findMetaAnnotatedClasses(ManagedBean.class)) {
               ManagedBean managed = (ManagedBean)beanClass.getAnnotation(ManagedBean.class);
               String ejbName = this.getEjbName(managed, (Class)beanClass.get());
               if (this.isValidEjbAnnotationUsage(ManagedBean.class, beanClass, ejbName, ejbModule)) {
                  EnterpriseBean enterpriseBean = ejbJar.getEnterpriseBean(ejbName);
                  if (enterpriseBean == null) {
                     enterpriseBean = new org.apache.openejb.jee.ManagedBean(ejbName, (Class)beanClass.get());
                     ejbJar.addEnterpriseBean(enterpriseBean);
                  }

                  if (enterpriseBean.getEjbClass() == null) {
                     enterpriseBean.setEjbClass((Class)beanClass.get());
                  }

                  if (enterpriseBean instanceof SessionBean) {
                     SessionBean sessionBean = (SessionBean)enterpriseBean;
                     sessionBean.setSessionType(SessionType.MANAGED);
                     TransactionType transactionType = sessionBean.getTransactionType();
                     if (transactionType == null) {
                        sessionBean.setTransactionType(TransactionType.BEAN);
                     }
                  }
               }
            }

            for(Annotated<Class<?>> beanClass : finder.findMetaAnnotatedClasses(MessageDriven.class)) {
               MessageDriven mdb = (MessageDriven)beanClass.getAnnotation(MessageDriven.class);
               String ejbName = this.getEjbName(mdb, (Class)beanClass.get());
               if (this.isValidEjbAnnotationUsage(MessageDriven.class, beanClass, ejbName, ejbModule)) {
                  MessageDrivenBean messageBean = (MessageDrivenBean)ejbJar.getEnterpriseBean(ejbName);
                  if (messageBean == null) {
                     messageBean = new MessageDrivenBean(ejbName);
                     ejbJar.addEnterpriseBean(messageBean);
                  }

                  if (messageBean.getEjbClass() == null) {
                     messageBean.setEjbClass((Class)beanClass.get());
                  }

                  LegacyProcessor.process((Class)beanClass.get(), messageBean);
               }
            }

            AssemblyDescriptor assemblyDescriptor = ejbModule.getEjbJar().getAssemblyDescriptor();
            if (assemblyDescriptor == null) {
               assemblyDescriptor = new AssemblyDescriptor();
               ejbModule.getEjbJar().setAssemblyDescriptor(assemblyDescriptor);
            }

            AnnotationDeployer.startupLogger.debug("Searching for annotated application exceptions (see OPENEJB-980)");

            for(Class<?> exceptionClass : finder.findAnnotatedClasses(ApplicationException.class)) {
               AnnotationDeployer.startupLogger.debug("...handling " + exceptionClass);
               ApplicationException annotation = (ApplicationException)exceptionClass.getAnnotation(ApplicationException.class);
               if (assemblyDescriptor.getApplicationException(exceptionClass) == null) {
                  AnnotationDeployer.startupLogger.debug("...adding " + exceptionClass + " with rollback=" + annotation.rollback());
                  assemblyDescriptor.addApplicationException(exceptionClass, annotation.rollback(), annotation.inherited());
               } else {
                  AnnotationDeployer.mergeApplicationExceptionAnnotation(assemblyDescriptor, exceptionClass, annotation);
               }
            }

            Beans beans = ejbModule.getBeans();
            boolean deployComp;
            if (beans == null && !ejbJar.getEnterpriseBeansByEjbName().isEmpty() && this.isActivateCdiForEjbOnlyModules(ejbModule)) {
               AnnotationDeployer.logger.info("Activating CDI in ACTIVATED mode in module '" + ejbModule.getModuleUri() + "' cause EJB were found\n  add openejb.cdi.activated=false in application.properties to switch it off or\n  openejb.cdi.activated-on-ejb=false in conf/system.properties  to switch it off");
               beans = new Beans();
               beans.setBeanDiscoveryMode("ANNOTATED");
               beans.setVersion("1.1");

               try {
                  ejbModule.getModuleUri().toURL();
                  beans.setUri(ejbModule.getModuleUri().toASCIIString());
               } catch (IllegalArgumentException | MalformedURLException var16) {
                  beans.setUri(URI.create("jar:file://!/" + ejbModule.getModuleUri().toASCIIString() + "/META-INF/beans.xml").toASCIIString());
               }

               ejbModule.setBeans(beans);
               deployComp = false;
            } else {
               deployComp = true;
            }

            if (beans != null) {
               Map<URL, List<String>> managedClasses = beans.getManagedClasses();
               this.getBeanClasses(beans.getUri(), finder, managedClasses, beans.getNotManagedClasses(), ejbModule.getAltDDs());
               if (deployComp) {
                  String id = ejbModule.getModuleId();
                  if (ejbModule.getJarLocation() != null && (ejbModule.getJarLocation().contains(ejbModule.getModuleId() + "/target/test-classes".replace("/", File.separator)) || ejbModule.getJarLocation().contains(ejbModule.getModuleId() + "/build/classes/test".replace("/", File.separator)))) {
                     id = id + "_test";
                  }

                  String name = BeanContext.Comp.openejbCompName(id);
                  org.apache.openejb.jee.ManagedBean managedBean = new CompManagedBean(name, BeanContext.Comp.class);
                  managedBean.setTransactionType(TransactionType.BEAN);
                  ejbModule.getEjbJar().addEnterpriseBean(managedBean);
                  if ("true".equals(SystemInstance.get().getProperty("openejb.cdi.support.@Startup", "true"))) {
                     List<Annotated<Class<?>>> forceStart = finder.findMetaAnnotatedClasses(Startup.class);
                     List<String> startupBeans = beans.getStartupBeans();

                     for(Annotated<Class<?>> clazz : forceStart) {
                        startupBeans.add(((Class)clazz.get()).getName());
                     }
                  }
               }
            }

            if (ejbModule.getAppModule() != null) {
               this.addJaxRsProviders(finder, ejbModule.getAppModule().getJaxRsProviders(), Provider.class);
            }

            AnnotationDeployer.autoJpa(ejbModule);
            return ejbModule;
         }
      }

      private boolean isActivateCdiForEjbOnlyModules(EjbModule ejbModule) {
         String activated = ejbModule.getProperties().getProperty("openejb.cdi.activated");
         String globalConfig = SystemInstance.get().getProperty("openejb.cdi.activated-on-ejb");
         return (globalConfig == null || Boolean.parseBoolean(globalConfig)) && (activated == null && this.hasAtInject(ejbModule) || activated != null && Boolean.parseBoolean(activated));
      }

      private boolean hasAtInject(EjbModule ejbModule) {
         IAnnotationFinder finder = ejbModule.getFinder();
         return finder != null && (!finder.findAnnotatedFields(Inject.class).isEmpty() || !finder.findAnnotatedConstructors(Inject.class).isEmpty() || !finder.findAnnotatedMethods(Inject.class).isEmpty());
      }

      private SessionType getSessionType(Class<?> clazz) {
         if (clazz.isAnnotationPresent(Stateful.class)) {
            return SessionType.STATEFUL;
         } else if (clazz.isAnnotationPresent(Stateless.class)) {
            return SessionType.STATELESS;
         } else if (clazz.isAnnotationPresent(Singleton.class)) {
            return SessionType.SINGLETON;
         } else {
            return clazz.isAnnotationPresent(ManagedBean.class) ? SessionType.MANAGED : null;
         }
      }

      private String getEjbName(EnterpriseBean bean, Class<?> clazz) {
         if (bean instanceof SessionBean) {
            SessionBean sessionBean = (SessionBean)bean;
            switch (sessionBean.getSessionType()) {
               case STATEFUL:
                  Stateful annotation = (Stateful)clazz.getAnnotation(Stateful.class);
                  if (annotation != null && specified(annotation.name())) {
                     return annotation.name();
                  }
               case STATELESS:
                  Stateless annotation = (Stateless)clazz.getAnnotation(Stateless.class);
                  if (annotation != null && specified(annotation.name())) {
                     return annotation.name();
                  }
               case SINGLETON:
                  Singleton annotation = (Singleton)clazz.getAnnotation(Singleton.class);
                  if (annotation != null && specified(annotation.name())) {
                     return annotation.name();
                  }
            }
         }

         if (bean instanceof MessageDrivenBean) {
            MessageDriven annotation = (MessageDriven)clazz.getAnnotation(MessageDriven.class);
            if (annotation != null && specified(annotation.name())) {
               return annotation.name();
            }
         }

         return clazz.getSimpleName();
      }

      private static boolean specified(String name) {
         return name != null && name.length() != 0;
      }

      private void getBeanClasses(String uri, IAnnotationFinder finder, Map<URL, List<String>> classes, Map<URL, List<String>> notManaged, Map<String, Object> altDD) {
         IAnnotationFinder delegate = FinderFactory.ModuleLimitedFinder.class.isInstance(finder) ? ((FinderFactory.ModuleLimitedFinder)FinderFactory.ModuleLimitedFinder.class.cast(finder)).getDelegate() : finder;
         if (AnnotationFinder.class.isInstance(delegate)) {
            AnnotationFinder annotationFinder = (AnnotationFinder)AnnotationFinder.class.cast(delegate);
            Archive archive = annotationFinder.getArchive();
            if (!WebappAggregatedArchive.class.isInstance(archive)) {
               try {
                  List<String> annotatedClassNames = annotationFinder.getAnnotatedClassNames();
                  if (!annotatedClassNames.isEmpty()) {
                     classes.put(uri == null ? null : new URL(uri), annotatedClassNames);
                  }

               } catch (MalformedURLException e) {
                  throw new IllegalStateException(e);
               }
            } else {
               WebappAggregatedArchive aggregatedArchive = (WebappAggregatedArchive)archive;
               Map<URL, List<String>> map = aggregatedArchive.getClassesMap();
               Collection<Class<?>> discoveredBeans = null;
               List<Class<? extends Extension>> extensions = null;
               FolderDDMapper ddMapper = (FolderDDMapper)SystemInstance.get().getComponent(FolderDDMapper.class);

               for(Map.Entry<URL, List<String>> entry : map.entrySet()) {
                  URL key = (URL)entry.getKey();
                  URL beansXml = this.hasBeansXml(key, ddMapper);
                  List<String> value = (List)entry.getValue();
                  if (beansXml != null) {
                     classes.put(beansXml, value);
                  } else if (!value.isEmpty()) {
                     Set<String> fastValue = new HashSet(value);
                     if (discoveredBeans == null) {
                        discoveredBeans = new HashSet();
                        Set<Class<? extends Annotation>> containerAnnot = new HashSet();
                        containerAnnot.add(Stereotype.class);
                        containerAnnot.add(NormalScope.class);
                        containerAnnot.add(Dependent.class);
                        containerAnnot.add(ApplicationScoped.class);
                        containerAnnot.add(ConversationScoped.class);
                        containerAnnot.add(RequestScoped.class);
                        containerAnnot.add(SessionScoped.class);
                        containerAnnot.add(Model.class);
                        containerAnnot.add(Singleton.class);
                        containerAnnot.add(Stateless.class);
                        containerAnnot.add(Stateful.class);
                        containerAnnot.add(MessageDriven.class);
                        containerAnnot.add(Interceptor.class);
                        containerAnnot.add(Decorator.class);
                        ClassLoader classLoader = ParentClassLoaderFinder.Helper.get();

                        try {
                           for(String name : Arrays.asList("javax.faces.flow.FlowScoped", "javax.faces.view.ViewScoped")) {
                              containerAnnot.add(classLoader.loadClass(name));
                           }
                        } catch (Throwable var30) {
                        }

                        Set<Class<?>> newMarkers = new HashSet();

                        for(Class<? extends Annotation> a : containerAnnot) {
                           newMarkers.addAll(finder.findAnnotatedClasses(a));
                        }

                        do {
                           Set<Class<?>> loopMarkers = new HashSet(newMarkers);
                           newMarkers.clear();

                           for(Class<?> marker : loopMarkers) {
                              discoveredBeans.add(marker);

                              for(Class<?> c : finder.findAnnotatedClasses((Class)Class.class.cast(marker))) {
                                 if (c.isAnnotation()) {
                                    newMarkers.add(c);
                                 }

                                 discoveredBeans.add(c);
                              }
                           }
                        } while(!newMarkers.isEmpty());

                        for(Field field : finder.findAnnotatedFields(Delegate.class)) {
                           discoveredBeans.add(field.getDeclaringClass());
                        }

                        extensions = finder.findImplementations(Extension.class);
                     }

                     boolean skip = false;

                     for(Class<?> c : extensions) {
                        if (fastValue.contains(c.getName())) {
                           skip = true;
                        }
                     }

                     if (!skip) {
                        Set<String> beans = new HashSet();

                        for(Class<?> c : discoveredBeans) {
                           String name = c.getName();
                           if (fastValue.contains(name)) {
                              beans.add(name);
                           }
                        }

                        if (!beans.isEmpty()) {
                           notManaged.put(entry.getKey(), new ArrayList(beans));
                        }
                     }
                  }
               }

            }
         }
      }

      public URL hasBeansXml(URL url, FolderDDMapper ddMapper) {
         String urlPath = url.getPath();
         if (urlPath.endsWith("/WEB-INF/beans.xml")) {
            return url;
         } else if (!urlPath.endsWith("WEB-INF/classes/") && !urlPath.endsWith("WEB-INF/classes")) {
            if (url.getPath().endsWith("!/META-INF/beans.xml")) {
               return url;
            } else {
               URLClassLoader loader = null;

               try {
                  loader = new URLClassLoader(new URL[]{url}, new EmptyResourcesClassLoader());
                  String[] paths = new String[]{"META-INF/beans.xml", "WEB-INF/beans.xml", "/WEB-INF/beans.xml", "/META-INF/beans.xml"};

                  for(String path : paths) {
                     URL resource = loader.findResource(path);
                     if (resource != null) {
                        URL var11 = resource;
                        return var11;
                     }
                  }
               } catch (Exception var28) {
               } finally {
                  try {
                     if (loader != null) {
                        loader.close();
                     }
                  } catch (IOException var24) {
                  }

               }

               if (ddMapper != null) {
                  File asFile = URLs.toFile(url);
                  if (asFile.isDirectory()) {
                     File ddFolder = ddMapper.getDDFolder(asFile);
                     File file = new File(ddFolder, "beans.xml");
                     if (file.isFile()) {
                        try {
                           return file.toURI().toURL();
                        } catch (MalformedURLException var27) {
                        }
                     }
                  }
               }

               return null;
            }
         } else {
            File toFile = URLs.toFile(url);
            File file = new File(toFile.getParent(), "beans.xml");
            if (file.exists()) {
               try {
                  return file.toURI().toURL();
               } catch (MalformedURLException var25) {
                  return url;
               }
            } else {
               file = new File(toFile, "META-INF/beans.xml");
               if (file.exists()) {
                  try {
                     return file.toURI().toURL();
                  } catch (MalformedURLException var26) {
                     return url;
                  }
               } else {
                  return null;
               }
            }
         }
      }

      private String getEjbName(MessageDriven mdb, Class<?> beanClass) {
         return mdb.name().isEmpty() ? beanClass.getSimpleName() : mdb.name();
      }

      private String getEjbName(Stateful stateful, Class<?> beanClass) {
         return stateful.name().isEmpty() ? beanClass.getSimpleName() : stateful.name();
      }

      private String getEjbName(Stateless stateless, Class<?> beanClass) {
         return stateless.name().isEmpty() ? beanClass.getSimpleName() : stateless.name();
      }

      private String getEjbName(Singleton singleton, Class<?> beanClass) {
         return singleton.name().isEmpty() ? beanClass.getSimpleName() : singleton.name();
      }

      private String getEjbName(ManagedBean managed, Class<?> beanClass) {
         return managed.value().isEmpty() ? beanClass.getSimpleName() : managed.value();
      }

      private boolean isValidEjbAnnotationUsage(Class annotationClass, Annotated<Class<?>> beanClass, String ejbName, EjbModule ejbModule) {
         List<Class<? extends Annotation>> annotations = new ArrayList(Arrays.asList(Singleton.class, Stateless.class, Stateful.class, MessageDriven.class));
         annotations.remove(annotationClass);
         boolean b = true;

         for(Class<? extends Annotation> secondAnnotation : annotations) {
            Annotation annotation = beanClass.getAnnotation(secondAnnotation);
            if (annotation != null) {
               String secondEjbName = null;
               if (annotation instanceof Stateful) {
                  secondEjbName = this.getEjbName((Stateful)annotation, (Class)beanClass.get());
               } else if (annotation instanceof Stateless) {
                  secondEjbName = this.getEjbName((Stateless)annotation, (Class)beanClass.get());
               } else if (annotation instanceof Singleton) {
                  secondEjbName = this.getEjbName((Singleton)annotation, (Class)beanClass.get());
               } else if (annotation instanceof MessageDriven) {
                  secondEjbName = this.getEjbName((MessageDriven)annotation, (Class)beanClass.get());
               }

               if (ejbName.equals(secondEjbName)) {
                  ejbModule.getValidation().fail(ejbName, "multiplyAnnotatedAsBean", annotationClass.getSimpleName(), secondAnnotation.getSimpleName(), ejbName, ((Class)beanClass.get()).getName());
               }
            }
         }

         if (((Class)beanClass.get()).isInterface()) {
            if (!CheckClasses.isAbstractAllowed((Class)beanClass.get())) {
               ejbModule.getValidation().fail(ejbName, "interfaceAnnotatedAsBean", annotationClass.getSimpleName(), ((Class)beanClass.get()).getName());
               return false;
            }
         } else if (Modifier.isAbstract(((Class)beanClass.get()).getModifiers()) && !CheckClasses.isAbstractAllowed((Class)beanClass.get())) {
            ejbModule.getValidation().fail(ejbName, "abstractAnnotatedAsBean", annotationClass.getSimpleName(), ((Class)beanClass.get()).getName());
            return false;
         }

         return true;
      }
   }

   public static class ProcessAnnotatedBeans implements DynamicDeployer {
      public static final String STRICT_INTERFACE_DECLARATION = "openejb.strict.interface.declaration";
      private final boolean webserviceAsRemote;

      public ProcessAnnotatedBeans(boolean wsAsRemote) {
         this.webserviceAsRemote = wsAsRemote;
      }

      public void deploy(CdiBeanInfo beanInfo) throws OpenEJBException {
         AnnotationFinder annotationFinder = this.createFinder(beanInfo.getBeanClass());
         this.buildAnnotatedRefs(beanInfo, annotationFinder, beanInfo.getClassLoader());
         this.processWebServiceClientHandlers(beanInfo, annotationFinder, beanInfo.getClassLoader());
      }

      public AppModule deploy(AppModule appModule) throws OpenEJBException {
         for(EjbModule ejbModule : appModule.getEjbModules()) {
            AnnotationDeployer.setModule(ejbModule);

            try {
               this.deploy(ejbModule);
            } finally {
               AnnotationDeployer.removeModule();
            }
         }

         for(ClientModule clientModule : appModule.getClientModules()) {
            AnnotationDeployer.setModule(clientModule);

            try {
               this.deploy(clientModule);
            } finally {
               AnnotationDeployer.removeModule();
            }
         }

         for(ConnectorModule connectorModule : appModule.getConnectorModules()) {
            AnnotationDeployer.setModule(connectorModule);

            try {
               this.deploy(connectorModule);
            } finally {
               AnnotationDeployer.removeModule();
            }
         }

         for(WebModule webModule : appModule.getWebModules()) {
            AnnotationDeployer.setModule(webModule);

            try {
               this.deploy(webModule);
            } finally {
               AnnotationDeployer.removeModule();
            }
         }

         return appModule;
      }

      public ClientModule deploy(ClientModule clientModule) throws OpenEJBException {
         if (clientModule.getApplicationClient() != null && clientModule.getApplicationClient().isMetadataComplete()) {
            return clientModule;
         } else {
            ClassLoader classLoader = clientModule.getClassLoader();
            ApplicationClient client = clientModule.getApplicationClient();
            if (client == null) {
               client = new ApplicationClient();
            }

            Set<Class> remoteClients = new HashSet();
            if (clientModule.getMainClass() != null) {
               String className = AnnotationDeployer.realClassName(clientModule.getMainClass());
               if (clientModule.getMainClass().contains("/")) {
                  clientModule.setMainClass(className);
               }

               try {
                  Class clazz = classLoader.loadClass(className);
                  remoteClients.add(clazz);
                  AnnotationFinder annotationFinder = this.createFinder(clazz);
                  this.buildAnnotatedRefs(client, annotationFinder, classLoader);
               } catch (ClassNotFoundException var14) {
                  if (clientModule.getAltDDs().containsKey("application-client.xml")) {
                     AnnotationDeployer.getValidationContext().fail("client", "client.missingMainClass", className);
                  } else {
                     AnnotationDeployer.getValidationContext().warn("client", "client.missingMainClass", className);
                  }
               }
            }

            for(String rawClassName : clientModule.getRemoteClients()) {
               String className = AnnotationDeployer.realClassName(rawClassName);

               Class clazz;
               try {
                  clazz = classLoader.loadClass(className);
                  remoteClients.add(clazz);
               } catch (ClassNotFoundException e) {
                  throw new OpenEJBException("Unable to load RemoteClient class: " + className, e);
               }

               AnnotationFinder annotationFinder = this.createFinder(clazz);
               this.buildAnnotatedRefs(client, annotationFinder, classLoader);
            }

            for(String rawClassName : clientModule.getLocalClients()) {
               String className = AnnotationDeployer.realClassName(rawClassName);

               Class clazz;
               try {
                  clazz = classLoader.loadClass(className);
               } catch (ClassNotFoundException e) {
                  throw new OpenEJBException("Unable to load LocalClient class: " + className, e);
               }

               AnnotationFinder annotationFinder = this.createFinder(clazz);
               this.buildAnnotatedRefs(client, annotationFinder, classLoader);
            }

            this.validateRemoteClientRefs(classLoader, client, remoteClients);
            IAnnotationFinder finder = clientModule.getFinder();
            if (!AnnotationFinder.class.isInstance(finder) && finder != null) {
               Class<?>[] loadedClasses = new Class[finder.getAnnotatedClassNames().size()];
               int i = 0;

               for(String s : finder.getAnnotatedClassNames()) {
                  try {
                     loadedClasses[i++] = classLoader.loadClass(s);
                  } catch (ClassNotFoundException var11) {
                  }
               }

               clientModule.getFinderReference().set(new FinderFactory.OpenEJBAnnotationFinder(new ClassesArchive(loadedClasses)));
            }

            this.processWebServiceClientHandlers(client, (AnnotationFinder)AnnotationFinder.class.cast(clientModule.getFinder()), classLoader);
            return clientModule;
         }
      }

      private void validateRemoteClientRefs(ClassLoader classLoader, ApplicationClient client, Set<Class> remoteClients) {
         for(EjbLocalRef ref : client.getEjbLocalRef()) {
            for(InjectionTarget target : ref.getInjectionTarget()) {
               try {
                  Class<?> targetClass = classLoader.loadClass(AnnotationDeployer.realClassName(target.getInjectionTargetClass()));

                  for(Class remoteClient : remoteClients) {
                     if (targetClass.isAssignableFrom(remoteClient)) {
                        this.fail(remoteClient.getName(), "remoteClient.ejbLocalRef", target.getInjectionTargetClass(), target.getInjectionTargetName());
                     }
                  }
               } catch (ClassNotFoundException var12) {
               }
            }
         }

         for(PersistenceContextRef ref : client.getPersistenceContextRef()) {
            for(InjectionTarget target : ref.getInjectionTarget()) {
               try {
                  Class<?> targetClass = classLoader.loadClass(AnnotationDeployer.realClassName(target.getInjectionTargetClass()));

                  for(Class remoteClient : remoteClients) {
                     if (targetClass.isAssignableFrom(remoteClient)) {
                        this.fail(remoteClient.getName(), "remoteClient.persistenceContextRef", target.getInjectionTargetClass(), target.getInjectionTargetName());
                     }
                  }
               } catch (ClassNotFoundException var11) {
               }
            }
         }

      }

      public ConnectorModule deploy(ConnectorModule connectorModule) throws OpenEJBException {
         return connectorModule;
      }

      public WebModule deploy(WebModule webModule) throws OpenEJBException {
         WebApp webApp = webModule.getWebApp();
         if (webApp != null && webApp.isMetadataComplete()) {
            return webModule;
         } else {
            Set<Class> classes = new HashSet();
            ClassLoader classLoader = webModule.getClassLoader();
            String webXmlApplication = (String)webApp.contextParamsAsMap().get("javax.ws.rs.Application");
            if (webXmlApplication != null) {
               webModule.getRestApplications().clear();
               webModule.getRestApplications().add(webXmlApplication);
            }

            Collection<String> restApp = webModule.getRestApplications();
            if (restApp.isEmpty()) {
               AnnotationDeployer.addRestClassesToScannedClasses(webModule, classes, classLoader);
            } else {
               for(String rawClassName : restApp) {
                  String application = AnnotationDeployer.realClassName(rawClassName);
                  if (application != null) {
                     Class<?> clazz;
                     try {
                        clazz = classLoader.loadClass(application);
                        classes.add(clazz);
                     } catch (ClassNotFoundException e) {
                        throw new OpenEJBException("Unable to load Application class: " + application, e);
                     }

                     if (!Modifier.isAbstract(clazz.getModifiers())) {
                        try {
                           Application app = (Application)Application.class.cast(clazz.newInstance());

                           try {
                              Set<Class<?>> appClasses = app.getClasses();
                              if (!appClasses.isEmpty()) {
                                 classes.addAll(appClasses);
                              } else {
                                 AnnotationDeployer.addRestClassesToScannedClasses(webModule, classes, classLoader);
                              }
                           } catch (RuntimeException npe) {
                              if (app == null) {
                                 throw npe;
                              }
                           }
                        } catch (InstantiationException e) {
                           throw new OpenEJBException("Unable to instantiate Application class: " + application, e);
                        } catch (IllegalAccessException e) {
                           throw new OpenEJBException("Unable to access Application class: " + application, e);
                        }
                     }
                  }
               }
            }

            for(Servlet servlet : webApp.getServlet()) {
               String servletName = servlet.getServletName();
               if (!"javax.ws.rs.core.Application".equals(servletName) && !"javax.ws.rs.Application".equals(servletName)) {
                  String servletClass = AnnotationDeployer.realClassName(servlet.getServletClass());
                  if (servletClass == null) {
                     servletClass = AnnotationDeployer.realClassName(servletName);
                  }

                  if (servletClass != null && servlet.getJspFile() == null) {
                     if (!"org.apache.openejb.server.rest.OpenEJBRestServlet".equals(servletClass)) {
                        try {
                           Class clazz = classLoader.loadClass(servletClass);
                           classes.add(clazz);
                           if (servlet.getServletClass() == null) {
                              servlet.setServletClass(servletClass);
                           }
                        } catch (ClassNotFoundException e) {
                           if (servlet.getServletClass() != null) {
                              throw new OpenEJBException("Unable to load servlet class: " + servletClass, e);
                           }

                           AnnotationDeployer.logger.error("servlet " + servletName + " has no servlet-class defined and is not a subclass of Application");
                        }
                     }

                     for(ParamValue param : servlet.getInitParam()) {
                        if (param.getParamName().equals(Application.class.getName()) || param.getParamName().equals("javax.ws.rs.Application")) {
                           webModule.getRestApplications().clear();
                           webModule.getRestApplications().add(param.getParamValue());
                           break;
                        }
                     }
                  }
               } else {
                  boolean done = false;

                  for(ParamValue pv : servlet.getInitParam()) {
                     if ("javax.ws.rs.core.Application".equals(pv.getParamName()) || "javax.ws.rs.Application".equals(pv.getParamName())) {
                        webModule.getRestApplications().add(pv.getParamValue());
                        done = true;
                        break;
                     }
                  }

                  if (!done) {
                     servlet.setServletName(ProvidedJAXRSApplication.class.getName());
                     webModule.getRestApplications().add(ProvidedJAXRSApplication.class.getName());

                     for(ServletMapping mapping : webApp.getServletMapping()) {
                        if (servletName.equals(mapping.getServletName())) {
                           mapping.setServletName(ProvidedJAXRSApplication.class.getName());
                        }
                     }
                  }
               }
            }

            for(Filter filter : webApp.getFilter()) {
               String filterClass = AnnotationDeployer.realClassName(filter.getFilterClass());
               if (filterClass != null) {
                  try {
                     Class clazz = classLoader.loadClass(filterClass);
                     classes.add(clazz);
                  } catch (ClassNotFoundException e) {
                     throw new OpenEJBException("Unable to load servlet filter class: " + filterClass, e);
                  }
               }
            }

            for(Listener listener : webApp.getListener()) {
               String listenerClass = AnnotationDeployer.realClassName(listener.getListenerClass());
               if (listenerClass != null) {
                  try {
                     Class clazz = classLoader.loadClass(listenerClass);
                     classes.add(clazz);
                  } catch (ClassNotFoundException e) {
                     throw new OpenEJBException("Unable to load servlet listener class: " + listenerClass, e);
                  }
               }
            }

            for(TldTaglib taglib : webModule.getTaglibs()) {
               for(Listener listener : taglib.getListener()) {
                  String listenerClass = AnnotationDeployer.realClassName(listener.getListenerClass());
                  if (listenerClass != null) {
                     try {
                        Class clazz = classLoader.loadClass(listenerClass);
                        classes.add(clazz);
                     } catch (ClassNotFoundException var20) {
                        AnnotationDeployer.logger.error("Unable to load tag library servlet listener class: " + listenerClass);
                     }
                  }
               }

               for(Tag tag : taglib.getTag()) {
                  String tagClass = AnnotationDeployer.realClassName(tag.getTagClass());
                  if (tagClass != null) {
                     try {
                        Class clazz = classLoader.loadClass(tagClass);
                        classes.add(clazz);
                     } catch (ClassNotFoundException var19) {
                        AnnotationDeployer.logger.error("Unable to load tag library tag class: " + tagClass);
                     }
                  }
               }
            }

            if (webModule.getWebservices() != null) {
               for(WebserviceDescription webservice : webModule.getWebservices().getWebserviceDescription()) {
                  for(PortComponent port : webservice.getPortComponent()) {
                     if (port.getServiceImplBean().getEjbLink() == null && port.getHandlerChains() != null) {
                        for(HandlerChain handlerChain : port.getHandlerChains().getHandlerChain()) {
                           for(Handler handler : handlerChain.getHandler()) {
                              String handlerClass = AnnotationDeployer.realClassName(handler.getHandlerClass());
                              if (handlerClass != null) {
                                 try {
                                    Class clazz = classLoader.loadClass(handlerClass);
                                    classes.add(clazz);
                                 } catch (ClassNotFoundException e) {
                                    throw new OpenEJBException("Unable to load webservice handler class: " + handlerClass, e);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            for(FacesConfig facesConfig : webModule.getFacesConfigs()) {
               for(FacesManagedBean bean : facesConfig.getManagedBean()) {
                  String managedBeanClass = AnnotationDeployer.realClassName(bean.getManagedBeanClass().trim());
                  if (managedBeanClass != null) {
                     try {
                        Class clazz = classLoader.loadClass(managedBeanClass);
                        classes.add(clazz);
                     } catch (ClassNotFoundException var17) {
                        AnnotationDeployer.logger.error("Unable to load JSF managed bean class: " + managedBeanClass);
                     }
                  }
               }
            }

            IAnnotationFinder finder = webModule.getFinder();
            if (finder != null) {
               for(String apiClassName : AnnotationDeployer.API_CLASSES) {
                  Class<? extends Annotation> clazz;
                  try {
                     clazz = classLoader.loadClass(apiClassName);
                  } catch (ClassNotFoundException var24) {
                     continue;
                  }

                  if (clazz.isAnnotation()) {
                     classes.addAll(AnnotationDeployer.metaToClass(finder.findMetaAnnotatedClasses(clazz)));
                  } else if (Modifier.isAbstract(clazz.getModifiers())) {
                     classes.addAll(finder.findSubclasses(clazz));
                  } else {
                     classes.addAll(finder.findImplementations(clazz));
                  }
               }
            }

            AnnotationFinder annotationFinder = this.createFinder((Class[])classes.toArray(new Class[classes.size()]));
            this.buildAnnotatedRefs(webApp, annotationFinder, classLoader);
            this.processWebServiceClientHandlers(webApp, annotationFinder, classLoader);
            return webModule;
         }
      }

      public EjbModule deploy(EjbModule ejbModule) throws OpenEJBException {
         if (ejbModule.getEjbJar() != null && ejbModule.getEjbJar().isMetadataComplete()) {
            return ejbModule;
         } else {
            ClassLoader classLoader = ejbModule.getClassLoader();
            EnterpriseBean[] enterpriseBeans = ejbModule.getEjbJar().getEnterpriseBeans();

            for(EnterpriseBean bean : enterpriseBeans) {
               String ejbName = bean.getEjbName();
               String ejbClassName = AnnotationDeployer.realClassName(bean.getEjbClass());
               if (ejbClassName == null) {
                  List<String> others = new ArrayList();

                  for(EnterpriseBean otherBean : enterpriseBeans) {
                     others.add(otherBean.getEjbName());
                  }

                  this.fail(ejbName, "xml.noEjbClass", ejbName, Join.join(", ", (Collection)others));
               }

               Class<?> clazz;
               try {
                  clazz = classLoader.loadClass(ejbClassName);
               } catch (ClassNotFoundException var30) {
                  continue;
               }

               MetaAnnotatedClass<?> metaClass = new MetaAnnotatedClass(clazz);
               boolean dynamicBean = DynamicProxyImplFactory.isKnownDynamicallyImplemented(metaClass, clazz);
               AnnotationFinder finder = null;
               AnnotationFinder annotationFinder;
               if (ejbModule.getFinder() instanceof AnnotationFinder) {
                  AnnotationFinder af = (AnnotationFinder)ejbModule.getFinder();
                  List<Class<?>> ancestors = Classes.ancestors(clazz);
                  ancestors.addAll(Arrays.asList(clazz.getInterfaces()));
                  if (dynamicBean) {
                     Proxy p = (Proxy)metaClass.getAnnotation(Proxy.class);
                     if (p != null) {
                        ancestors.add(p.value());
                     }
                  }

                  String[] names = new String[ancestors.size()];
                  int i = 0;

                  for(Class<?> ancestor : ancestors) {
                     names[i++] = ancestor.getName();
                  }

                  annotationFinder = af.select(names);
               } else if (!dynamicBean) {
                  annotationFinder = this.createFinder(clazz);
               } else {
                  Proxy proxy = (Proxy)metaClass.getAnnotation(Proxy.class);
                  Class<?>[] classes;
                  if (proxy == null) {
                     classes = new Class[]{clazz};
                  } else {
                     classes = new Class[]{clazz, proxy.value()};
                  }

                  annotationFinder = this.createFinder(classes);
               }

               this.processCallbacks(bean, annotationFinder);
               if (bean.getTransactionType() == null) {
                  TransactionManagement tx = (TransactionManagement)this.getInheritableAnnotation(clazz, TransactionManagement.class);
                  TransactionManagementType transactionType = TransactionManagementType.CONTAINER;
                  if (tx != null) {
                     transactionType = tx.value();
                  }

                  switch (transactionType) {
                     case BEAN:
                        bean.setTransactionType(TransactionType.BEAN);
                        break;
                     case CONTAINER:
                        bean.setTransactionType(TransactionType.CONTAINER);
                  }
               }

               AssemblyDescriptor assemblyDescriptor = ejbModule.getEjbJar().getAssemblyDescriptor();
               this.processApplicationExceptions(clazz, assemblyDescriptor);
               if (bean.getTransactionType() == TransactionType.CONTAINER) {
                  this.processAttributes(new TransactionAttributeHandler(assemblyDescriptor, ejbName), clazz, annotationFinder);
               } else {
                  if (finder == null) {
                     finder = annotationFinder.select(new String[]{clazz.getName()});
                  }

                  this.checkAttributes(new TransactionAttributeHandler(assemblyDescriptor, ejbName), ejbName, ejbModule, finder, "invalidTransactionAttribute");
               }

               this.processSecurityAnnotations(clazz, ejbName, ejbModule, annotationFinder, bean);
               this.processSchedules(bean, annotationFinder);

               for(InterceptorBinding binding : assemblyDescriptor.getInterceptorBinding()) {
                  EjbJar ejbJar = ejbModule.getEjbJar();
                  List<String> list = new ArrayList(binding.getInterceptorClass());
                  if (binding.getInterceptorOrder() != null) {
                     list.clear();
                     list.addAll(binding.getInterceptorOrder().getInterceptorClass());
                  }

                  for(String interceptor : list) {
                     if (ejbJar.getInterceptor(interceptor) == null) {
                        AnnotationDeployer.logger.debug("Adding '<ejb-jar><interceptors><interceptor>' entry for undeclared interceptor " + interceptor);
                        ejbJar.addInterceptor(new org.apache.openejb.jee.Interceptor(interceptor));
                     }
                  }
               }

               for(Annotated<Class<?>> interceptorsAnnotatedClass : AnnotationDeployer.sortClasses(annotationFinder.findMetaAnnotatedClasses(Interceptors.class))) {
                  Interceptors interceptors = (Interceptors)interceptorsAnnotatedClass.getAnnotation(Interceptors.class);
                  EjbJar ejbJar = ejbModule.getEjbJar();

                  for(Class interceptor : interceptors.value()) {
                     if (ejbJar.getInterceptor(interceptor.getName()) == null) {
                        ejbJar.addInterceptor(new org.apache.openejb.jee.Interceptor(interceptor.getName()));
                     }
                  }

                  InterceptorBinding binding = new InterceptorBinding(bean, new org.apache.openejb.jee.Interceptor[0]);
                  assemblyDescriptor.getInterceptorBinding().add(0, binding);

                  for(Class interceptor : interceptors.value()) {
                     binding.getInterceptorClass().add(interceptor.getName());
                  }
               }

               for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(Interceptors.class))) {
                  Interceptors interceptors = (Interceptors)method.getAnnotation(Interceptors.class);
                  if (interceptors != null) {
                     EjbJar ejbJar = ejbModule.getEjbJar();

                     for(Class interceptor : interceptors.value()) {
                        if (ejbJar.getInterceptor(interceptor.getName()) == null) {
                           ejbJar.addInterceptor(new org.apache.openejb.jee.Interceptor(interceptor.getName()));
                        }
                     }

                     InterceptorBinding binding = new InterceptorBinding(bean, new org.apache.openejb.jee.Interceptor[0]);
                     assemblyDescriptor.getInterceptorBinding().add(0, binding);

                     for(Class interceptor : interceptors.value()) {
                        binding.getInterceptorClass().add(interceptor.getName());
                     }

                     binding.setMethod(new NamedMethod((Method)method.get()));
                  }
               }

               ExcludeDefaultInterceptors excludeDefaultInterceptors = (ExcludeDefaultInterceptors)clazz.getAnnotation(ExcludeDefaultInterceptors.class);
               if (excludeDefaultInterceptors != null) {
                  InterceptorBinding binding = assemblyDescriptor.addInterceptorBinding(new InterceptorBinding(bean, new org.apache.openejb.jee.Interceptor[0]));
                  binding.setExcludeDefaultInterceptors(true);
               }

               for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(ExcludeDefaultInterceptors.class)) {
                  InterceptorBinding binding = assemblyDescriptor.addInterceptorBinding(new InterceptorBinding(bean, new org.apache.openejb.jee.Interceptor[0]));
                  binding.setExcludeDefaultInterceptors(true);
                  binding.setMethod(new NamedMethod((Method)method.get()));
               }

               for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(ExcludeClassInterceptors.class))) {
                  InterceptorBinding binding = assemblyDescriptor.addInterceptorBinding(new InterceptorBinding(bean, new org.apache.openejb.jee.Interceptor[0]));
                  binding.setExcludeClassInterceptors(true);
                  binding.setMethod(new NamedMethod((Method)method.get()));
               }

               if (bean instanceof RemoteBean) {
                  RemoteBean remoteBean = (RemoteBean)bean;
                  if (remoteBean.getHome() == null) {
                     RemoteHome remoteHome = (RemoteHome)this.getInheritableAnnotation(clazz, RemoteHome.class);
                     if (remoteHome != null) {
                        Class<?> homeClass = remoteHome.value();

                        try {
                           Method create = null;

                           for(Method method : homeClass.getMethods()) {
                              if (method.getName().startsWith("create")) {
                                 create = method;
                                 break;
                              }
                           }

                           if (create == null) {
                              throw new NoSuchMethodException("create");
                           }

                           Class<?> remoteClass = create.getReturnType();
                           remoteBean.setHome(homeClass.getName());
                           remoteBean.setRemote(remoteClass.getName());
                        } catch (NoSuchMethodException var32) {
                           AnnotationDeployer.logger.error("Class annotated as a RemoteHome has no 'create()' method.  Unable to determine remote interface type.  Bean class: " + clazz.getName() + ",  Home class: " + homeClass.getName());
                        }
                     }
                  }

                  if (remoteBean.getLocalHome() == null) {
                     LocalHome localHome = (LocalHome)this.getInheritableAnnotation(clazz, LocalHome.class);
                     if (localHome != null) {
                        Class<?> homeClass = localHome.value();

                        try {
                           Method create = null;

                           for(Method method : homeClass.getMethods()) {
                              if (method.getName().startsWith("create")) {
                                 create = method;
                                 break;
                              }
                           }

                           if (create == null) {
                              throw new NoSuchMethodException("create");
                           }

                           Class<?> remoteClass = create.getReturnType();
                           remoteBean.setLocalHome(homeClass.getName());
                           remoteBean.setLocal(remoteClass.getName());
                        } catch (NoSuchMethodException var31) {
                           AnnotationDeployer.logger.error("Class annotated as a LocalHome has no 'create()' method.  Unable to determine remote interface type.  Bean class: " + clazz.getName() + ",  Home class: " + homeClass.getName());
                        }
                     }
                  }

                  if (remoteBean instanceof SessionBean) {
                     SessionBean sessionBean = (SessionBean)remoteBean;
                     sessionBean.getParents().add(clazz.getName());
                     if (!clazz.isInterface()) {
                        for(Class<?> current = clazz.getSuperclass(); !current.equals(Object.class); current = current.getSuperclass()) {
                           sessionBean.getParents().add(current.getName());
                        }
                     }

                     this.processSessionInterfaces(sessionBean, clazz, ejbModule);
                     this.processAsynchronous(bean, annotationFinder);
                     if (sessionBean.getDependsOn() == null) {
                        DependsOn dependsOn = (DependsOn)this.getInheritableAnnotation(clazz, DependsOn.class);
                        if (dependsOn != null) {
                           sessionBean.setDependsOn(dependsOn.value());
                        } else {
                           sessionBean.setDependsOn(Collections.EMPTY_LIST);
                        }
                     }

                     if (sessionBean.getSessionType() != SessionType.STATEFUL && annotationFinder.isAnnotationPresent(Path.class)) {
                        sessionBean.setRestService(true);
                     }

                     if (sessionBean.getSessionType() == SessionType.SINGLETON) {
                        if (sessionBean.getConcurrencyManagementType() == null) {
                           ConcurrencyManagement tx = (ConcurrencyManagement)this.getInheritableAnnotation(clazz, ConcurrencyManagement.class);
                           ConcurrencyManagementType concurrencyType = ConcurrencyManagementType.CONTAINER;
                           if (tx != null) {
                              concurrencyType = tx.value();
                           }

                           switch (concurrencyType) {
                              case BEAN:
                                 sessionBean.setConcurrencyManagementType(org.apache.openejb.jee.ConcurrencyManagementType.BEAN);
                                 break;
                              case CONTAINER:
                                 sessionBean.setConcurrencyManagementType(org.apache.openejb.jee.ConcurrencyManagementType.CONTAINER);
                           }
                        }

                        LockHandler lockHandler = new LockHandler(assemblyDescriptor, sessionBean);
                        if (sessionBean.getConcurrencyManagementType() == org.apache.openejb.jee.ConcurrencyManagementType.CONTAINER) {
                           this.processAttributes(lockHandler, clazz, annotationFinder);
                        } else {
                           this.checkAttributes(lockHandler, ejbName, ejbModule, annotationFinder, "invalidConcurrencyAttribute");
                        }

                        AccessTimeoutHandler accessTimeoutHandler = new AccessTimeoutHandler(assemblyDescriptor, sessionBean, lockHandler.getContainerConcurrency());
                        this.processAttributes(accessTimeoutHandler, clazz, annotationFinder);
                        if (!sessionBean.hasInitOnStartup()) {
                           Startup startup = (Startup)this.getInheritableAnnotation(clazz, Startup.class);
                           sessionBean.setInitOnStartup(startup != null);
                        }
                     } else if (sessionBean.getSessionType() == SessionType.STATEFUL) {
                        if (sessionBean.getStatefulTimeout() == null) {
                           StatefulTimeout annotation = (StatefulTimeout)this.getInheritableAnnotation(clazz, StatefulTimeout.class);
                           if (annotation != null) {
                              Timeout timeout = new Timeout();
                              timeout.setTimeout(annotation.value());
                              timeout.setUnit(annotation.unit());
                              sessionBean.setStatefulTimeout(timeout);
                           }
                        }

                        AccessTimeoutHandler accessTimeoutHandler = new AccessTimeoutHandler(assemblyDescriptor, sessionBean);
                        this.processAttributes(accessTimeoutHandler, clazz, annotationFinder);
                     }
                  }
               }

               if (bean instanceof MessageDrivenBean) {
                  MessageDrivenBean mdb = (MessageDrivenBean)bean;
                  MessageDriven messageDriven = (MessageDriven)clazz.getAnnotation(MessageDriven.class);
                  if (messageDriven != null) {
                     ActivationConfig activationConfig = mdb.getActivationConfig();
                     if (activationConfig == null) {
                        activationConfig = new ActivationConfig();
                     }

                     if (!messageDriven.mappedName().isEmpty()) {
                        if (mdb.getActivationConfig() == null) {
                           mdb.setActivationConfig(activationConfig);
                        }

                        if (!activationConfig.toProperties().containsKey("destinationType")) {
                           activationConfig.addProperty("destinationType", Queue.class.getName());
                        }

                        activationConfig.addProperty("destination", messageDriven.mappedName());
                     }

                     ActivationConfigProperty[] configProperties = messageDriven.activationConfig();
                     if (configProperties != null) {
                        if (mdb.getActivationConfig() == null) {
                           mdb.setActivationConfig(activationConfig);
                        }

                        Properties properties = activationConfig.toProperties();

                        for(ActivationConfigProperty property : configProperties) {
                           if (!properties.containsKey(property.propertyName())) {
                              activationConfig.addProperty(property.propertyName(), property.propertyValue());
                           }
                        }
                     }

                     if (mdb.getMessagingType() == null) {
                        Class<?> interfce = messageDriven.messageListenerInterface();
                        if (interfce != null && !interfce.equals(Object.class)) {
                           if (!interfce.isInterface()) {
                              throw new OpenEJBException("MessageListenerInterface property of @MessageDriven is not an interface");
                           }

                           mdb.setMessagingType(interfce.getName());
                        }
                     }
                  }

                  if (mdb.getMessagingType() == null) {
                     List<Class<?>> interfaces = new ArrayList();

                     for(Class<?> intf : clazz.getInterfaces()) {
                        String name = intf.getName();
                        if (!name.equals("java.io.Serializable") && !name.equals("java.io.Externalizable") && !name.startsWith("javax.ejb.") && !intf.isSynthetic()) {
                           interfaces.add(intf);
                        }
                     }

                     if (interfaces.size() != 1) {
                        String msg = "When annotating a bean class as @MessageDriven without declaring messageListenerInterface, the bean must implement exactly one interface, no more and no less. beanClass=" + clazz.getName() + " interfaces=";

                        for(Class<?> intf : interfaces) {
                           msg = msg + intf.getName() + ", ";
                        }

                        throw new IllegalStateException(msg);
                     }

                     mdb.setMessagingType(((Class)interfaces.get(0)).getName());
                  }
               }

               this.buildAnnotatedRefs(bean, annotationFinder, classLoader);
               this.processWebServiceHandlers(ejbModule, bean, annotationFinder);
               this.processWebServiceClientHandlers(bean, annotationFinder, classLoader);

               try {
                  if (BeanContext.Comp.class.getName().equals(bean.getEjbClass())) {
                     this.buildAnnotatedRefs(bean, ejbModule.getFinder(), classLoader);
                  }
               } catch (OpenEJBException e) {
                  AnnotationDeployer.logger.error("Processing of @Resource, @EJB, and other references failed for CDI managed beans", (Throwable)e);
               }
            }

            for(org.apache.openejb.jee.Interceptor interceptor : ejbModule.getEjbJar().getInterceptors()) {
               Class<?> clazz;
               try {
                  clazz = classLoader.loadClass(AnnotationDeployer.realClassName(interceptor.getInterceptorClass()));
               } catch (ClassNotFoundException e) {
                  throw new OpenEJBException("Unable to load interceptor class: " + interceptor.getInterceptorClass(), e);
               }

               AnnotationFinder annotationFinder = this.createFinder(clazz);
               this.processCallbacks(interceptor, annotationFinder);
               this.processApplicationExceptions(clazz, ejbModule.getEjbJar().getAssemblyDescriptor());
               this.buildAnnotatedRefs(interceptor, annotationFinder, classLoader);
               this.processWebServiceClientHandlers(interceptor, annotationFinder, classLoader);

               for(EnterpriseBean bean : enterpriseBeans) {
                  this.mergeJndiReferences(interceptor.getEnvEntryMap(), bean.getEnvEntryMap());
                  this.mergeJndiReferences(interceptor.getEjbRefMap(), bean.getEjbRefMap());
                  this.mergeJndiReferences(interceptor.getEjbLocalRefMap(), bean.getEjbLocalRefMap());
                  this.mergeJndiReferences(interceptor.getResourceRefMap(), bean.getResourceRefMap());
                  this.mergeJndiReferences(interceptor.getResourceEnvRefMap(), bean.getResourceEnvRefMap());
                  this.mergeJndiReferences(interceptor.getPersistenceContextRefMap(), bean.getPersistenceContextRefMap());
                  this.mergeJndiReferences(interceptor.getPersistenceUnitRefMap(), bean.getPersistenceUnitRefMap());
                  this.mergeJndiReferences(interceptor.getMessageDestinationRefMap(), bean.getMessageDestinationRefMap());
                  this.mergeJndiReferences(interceptor.getServiceRefMap(), bean.getServiceRefMap());
               }
            }

            return ejbModule;
         }
      }

      private void processAsynchronous(EnterpriseBean bean, AnnotationFinder annotationFinder) {
         if (bean instanceof SessionBean) {
            SessionBean sessionBean = (SessionBean)bean;

            for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(Asynchronous.class)) {
               sessionBean.getAsyncMethod().add(new AsyncMethod((Method)method.get()));
            }

            for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(Asynchronous.class)) {
               if (!((Class)clazz.get()).isInterface()) {
                  sessionBean.getAsynchronousClasses().add(((Class)clazz.get()).getName());
               }
            }

         }
      }

      private <T extends Injectable> void mergeJndiReferences(Map<String, T> from, Map<String, T> to) {
         for(Map.Entry<String, T> entry : from.entrySet()) {
            Injectable injectable = (Injectable)to.get(entry.getKey());
            if (injectable == null) {
               to.put(entry.getKey(), entry.getValue());
            } else {
               injectable.getInjectionTarget().addAll(((Injectable)entry.getValue()).getInjectionTarget());
            }
         }

      }

      private void processApplicationExceptions(Class<?> clazz, AssemblyDescriptor assemblyDescriptor) {
         for(Method method : clazz.getMethods()) {
            for(Class<?> exception : method.getExceptionTypes()) {
               ApplicationException annotation = (ApplicationException)exception.getAnnotation(ApplicationException.class);
               if (annotation != null) {
                  if (assemblyDescriptor.getApplicationException(exception) != null) {
                     AnnotationDeployer.mergeApplicationExceptionAnnotation(assemblyDescriptor, exception, annotation);
                  } else {
                     AnnotationDeployer.logger.debug("Found previously undetected application exception {0} listed on a method {1} with annotation {2}", method, exception, annotation);
                     assemblyDescriptor.addApplicationException(exception, annotation.rollback(), annotation.inherited());
                  }
               }
            }
         }

      }

      private void processSessionInterfaces(SessionBean sessionBean, Class<?> beanClass, EjbModule ejbModule) {
         ValidationContext validation = ejbModule.getValidation();
         String ejbName = sessionBean.getEjbName();
         boolean strict = this.getProperty("openejb.strict.interface.declaration", "false").equalsIgnoreCase("true");
         List<String> descriptor = new ArrayList();
         descriptor.add(sessionBean.getHome());
         descriptor.add(sessionBean.getRemote());
         descriptor.add(sessionBean.getLocalHome());
         descriptor.add(sessionBean.getLocal());
         descriptor.addAll(sessionBean.getBusinessLocal());
         descriptor.addAll(sessionBean.getBusinessRemote());
         descriptor.add(sessionBean.getServiceEndpoint());
         BusinessInterfaces xml = new BusinessInterfaces();
         xml.addLocals(sessionBean.getBusinessLocal(), ejbModule.getClassLoader());
         xml.addRemotes(sessionBean.getBusinessRemote(), ejbModule.getClassLoader());
         if (beanClass.getAnnotation(LocalBean.class) != null) {
            sessionBean.setLocalBean(new Empty());
         }

         if (strict) {
            for(Class interfce : xml.local) {
               if (xml.remote.contains(interfce)) {
                  validation.fail(ejbName, "xml.localRemote.conflict", interfce.getName());
               }
            }
         }

         BusinessInterfaces all = new BusinessInterfaces();
         all.local.addAll(xml.local);
         all.remote.addAll(xml.remote);

         for(Class<?> clazz : strict ? new ArrayList(Arrays.asList(beanClass)) : Classes.ancestors(beanClass)) {
            Class<?> webServiceItf = null;
            if (sessionBean.getServiceEndpoint() == null) {
               Class defaultEndpoint = BeanContext.ServiceEndpoint.class;

               for(Class interfce : clazz.getInterfaces()) {
                  if (interfce.isAnnotationPresent(WebService.class)) {
                     defaultEndpoint = interfce;
                     webServiceItf = interfce;
                  }
               }

               WebService webService = (WebService)clazz.getAnnotation(WebService.class);
               if (webService != null) {
                  String className = webService.endpointInterface();
                  if (!className.equals("")) {
                     sessionBean.setServiceEndpoint(className);
                  } else {
                     sessionBean.setServiceEndpoint(defaultEndpoint.getName());
                  }
               } else if (clazz.isAnnotationPresent(WebServiceProvider.class)) {
                  sessionBean.setServiceEndpoint(defaultEndpoint.getName());
               } else if (!defaultEndpoint.equals(BeanContext.ServiceEndpoint.class)) {
                  sessionBean.setServiceEndpoint(defaultEndpoint.getName());
               }
            }

            List<Class<?>> interfaces = new ArrayList();
            if (!clazz.isInterface()) {
               for(Class<?> interfce : clazz.getInterfaces()) {
                  String name = interfce.getName();
                  if (!name.equals("scala.ScalaObject") && !name.equals("groovy.lang.GroovyObject") && !name.equals("java.io.Serializable") && !name.equals("java.io.Externalizable") && (!name.equals(InvocationHandler.class.getName()) || !DynamicSubclass.isDynamic(beanClass)) && !name.startsWith("javax.ejb.") && !descriptor.contains(interfce.getName()) && !interfce.isSynthetic() && !"net.sourceforge.cobertura.coveragedata.HasBeenInstrumented".equals(name) && !name.startsWith("org.scalatest.")) {
                     interfaces.add(interfce);
                  }
               }
            }

            interfaces.removeAll(all.local);
            interfaces.removeAll(all.remote);
            Local local = (Local)clazz.getAnnotation(Local.class);
            Remote remote = (Remote)clazz.getAnnotation(Remote.class);
            boolean impliedLocal = local != null && local.value().length == 0;
            boolean impliedRemote = remote != null && remote.value().length == 0;
            BusinessInterfaces bean = new BusinessInterfaces();
            if (local != null) {
               bean.local.addAll(Arrays.asList(local.value()));
            }

            if (remote != null) {
               bean.remote.addAll(Arrays.asList(remote.value()));
            }

            if (strict) {
               for(Class interfce : bean.local) {
                  if (bean.remote.contains(interfce)) {
                     validation.fail(ejbName, "ann.localRemote.conflict", interfce.getName());
                  }
               }
            }

            interfaces.removeAll(bean.local);
            interfaces.removeAll(bean.remote);
            if (impliedLocal || impliedRemote) {
               if (interfaces.size() == 1) {
                  if (strict && impliedLocal && impliedRemote) {
                     Class<?> interfce = (Class)interfaces.remove(0);
                     validation.fail(ejbName, "ann.localRemote.ambiguous", interfce.getName());
                  } else {
                     if (impliedLocal) {
                        bean.local.addAll(interfaces);
                     }

                     if (impliedRemote) {
                        bean.remote.addAll(interfaces);
                     }

                     interfaces.clear();
                  }
               } else {
                  List<String> interfaceNames = new ArrayList();

                  for(Class<?> intrfce : interfaces) {
                     interfaceNames.add(intrfce.getName() + ".class");
                  }

                  if (impliedLocal && local.value().length == 0 && interfaces.size() == 0 && !strict) {
                     validation.warn(ejbName, "ann.local.forLocalBean", Join.join(", ", (Collection)interfaceNames));
                  } else if (impliedLocal) {
                     validation.fail(ejbName, "ann.local.noAttributes", Join.join(", ", (Collection)interfaceNames));
                     return;
                  }

                  if (impliedRemote) {
                     validation.fail(ejbName, "ann.remote.noAttributes", Join.join(", ", (Collection)interfaceNames));
                     return;
                  }
               }
            }

            BusinessInterfaces implemented = new BusinessInterfaces();

            for(Class interfce : interfaces) {
               boolean isLocal = interfce.isAnnotationPresent(Local.class);
               boolean isRemote = interfce.isAnnotationPresent(Remote.class);
               if (strict && isLocal && isRemote) {
                  validation.fail(ejbName, "ann.localRemote.conflict", interfce.getName());
               } else {
                  Class[] superInterface = interfce.getInterfaces();
                  if (isLocal) {
                     if (strict) {
                        for(Class si : superInterface) {
                           boolean present = si.isAnnotationPresent(Remote.class);
                           if (present) {
                              validation.fail(ejbName, "ann.remoteOrLocal.converse.parent", interfce.getName(), "Local", si.getName(), "Remote");
                           }
                        }
                     }

                     implemented.local.add(interfce);
                  }

                  if (isRemote) {
                     if (strict) {
                        for(Class si : superInterface) {
                           boolean present = si.isAnnotationPresent(Local.class);
                           if (present) {
                              validation.fail(ejbName, "ann.remoteOrLocal.converse.parent", interfce.getName(), "Remote", si.getName(), "Local");
                           }
                        }
                     }

                     implemented.remote.add(interfce);
                  }
               }
            }

            interfaces.removeAll(implemented.local);
            interfaces.removeAll(implemented.remote);
            bean.local.removeAll(all.local);
            bean.local.removeAll(all.remote);
            bean.remote.removeAll(all.remote);
            bean.remote.removeAll(all.local);

            for(Class interfce : bean.local) {
               this.validateLocalInterface(interfce, validation, ejbName);
            }

            for(Class interfce : bean.remote) {
               this.validateRemoteInterface(interfce, validation, ejbName);
            }

            all.local.addAll(bean.local);
            all.remote.addAll(bean.remote);
            implemented.local.removeAll(all.local);
            implemented.local.removeAll(all.remote);
            implemented.remote.removeAll(all.remote);
            implemented.remote.removeAll(all.local);

            for(Class interfce : implemented.local) {
               this.validateLocalInterface(interfce, validation, ejbName);
            }

            for(Class interfce : implemented.remote) {
               this.validateRemoteInterface(interfce, validation, ejbName);
            }

            all.local.addAll(implemented.local);
            all.remote.addAll(implemented.remote);
            if (clazz == beanClass && sessionBean.getLocalBean() == null && sessionBean.getBusinessLocal().isEmpty() && sessionBean.getBusinessRemote().isEmpty() && sessionBean.getHome() == null && sessionBean.getRemote() == null && sessionBean.getLocalHome() == null && sessionBean.getLocal() == null && all.local.isEmpty() && all.remote.isEmpty()) {
               if (interfaces.size() != 0 && !DynamicProxyImplFactory.isKnownDynamicallyImplemented(clazz)) {
                  if (interfaces.size() != 1) {
                     validation.fail(ejbName, "too.many.interfaces", ejbName, interfaces.toString().replace("interface ", ""));
                     return;
                  }

                  all.local.add(interfaces.remove(0));
               } else {
                  sessionBean.setLocalBean(new Empty());
               }
            }

            if (this.webserviceAsRemote && webServiceItf != null && all.remote.isEmpty()) {
               all.remote.add(webServiceItf);
            }

            if (beanClass.isAnnotationPresent(ManagedBean.class)) {
               sessionBean.setLocalBean(new Empty());
            }
         }

         for(Class interfce : all.local) {
            sessionBean.addBusinessLocal(interfce);
         }

         for(Class interfce : all.remote) {
            sessionBean.addBusinessRemote(interfce);
         }

      }

      private String getProperty(String key, String defaultValue) {
         String value = SystemInstance.get().getOptions().get(key, defaultValue);
         DeploymentModule module = AnnotationDeployer.getModule();
         if (module instanceof EjbModule) {
            EjbModule ejbModule = (EjbModule)module;
            OpenejbJar openejbJar = ejbModule.getOpenejbJar();
            if (openejbJar != null && openejbJar.getProperties() != null) {
               value = openejbJar.getProperties().getProperty(key, value);
            }
         }

         return value;
      }

      private void processSecurityAnnotations(Class<?> beanClass, String ejbName, EjbModule ejbModule, AnnotationFinder annotationFinder, EnterpriseBean bean) {
         AssemblyDescriptor assemblyDescriptor = ejbModule.getEjbJar().getAssemblyDescriptor();
         List<String> classPermissions = this.getDeclaredClassPermissions(assemblyDescriptor, ejbName);

         for(Class<?> clazzz : Classes.ancestors(beanClass)) {
            MetaAnnotatedClass<?> clazz = new MetaAnnotatedClass(clazzz);
            if (!classPermissions.contains("*") || !classPermissions.contains(clazz.getName())) {
               RolesAllowed rolesAllowed = (RolesAllowed)clazz.getAnnotation(RolesAllowed.class);
               PermitAll permitAll = (PermitAll)clazz.getAnnotation(PermitAll.class);
               DenyAll denyAll = (DenyAll)clazz.getAnnotation(DenyAll.class);
               if (rolesAllowed != null && permitAll != null || rolesAllowed != null && denyAll != null || permitAll != null && denyAll != null) {
                  ejbModule.getValidation().fail(ejbName, "permitAllAndRolesAllowedOnClass", clazz.getName());
               }

               if (rolesAllowed != null) {
                  MethodPermission methodPermission = new MethodPermission();
                  methodPermission.getRoleName().addAll(Arrays.asList(rolesAllowed.value()));
                  methodPermission.getMethod().add(new org.apache.openejb.jee.Method(ejbName, clazz.getName(), "*"));
                  assemblyDescriptor.getMethodPermission().add(methodPermission);
                  RemoteBean remoteBean = (RemoteBean)bean;
                  List<SecurityRoleRef> securityRoleRefs = remoteBean.getSecurityRoleRef();

                  for(String role : rolesAllowed.value()) {
                     securityRoleRefs.add(new SecurityRoleRef(role));
                  }
               }

               if (permitAll != null) {
                  MethodPermission methodPermission = new MethodPermission();
                  methodPermission.setUnchecked(true);
                  methodPermission.getMethod().add(new org.apache.openejb.jee.Method(ejbName, clazz.getName(), "*"));
                  assemblyDescriptor.getMethodPermission().add(methodPermission);
               }

               if (denyAll != null) {
                  assemblyDescriptor.getExcludeList().addMethod(new org.apache.openejb.jee.Method(ejbName, clazz.getName(), "*"));
               }
            }

            RunAs runAs = (RunAs)clazz.getAnnotation(RunAs.class);
            if (runAs != null && bean.getSecurityIdentity() == null) {
               SecurityIdentity securityIdentity = new SecurityIdentity();
               securityIdentity.setRunAs(runAs.value());
               bean.setSecurityIdentity(securityIdentity);
            }

            DeclareRoles declareRoles = (DeclareRoles)clazz.getAnnotation(DeclareRoles.class);
            if (declareRoles != null && bean instanceof RemoteBean) {
               RemoteBean remoteBean = (RemoteBean)bean;
               List<SecurityRoleRef> securityRoleRefs = remoteBean.getSecurityRoleRef();

               for(String role : declareRoles.value()) {
                  securityRoleRefs.add(new SecurityRoleRef(role));
               }
            }
         }

         List<Method> seen = new ArrayList();

         for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(RolesAllowed.class)) {
            this.checkConflictingSecurityAnnotations(method, ejbName, ejbModule, seen);
            RolesAllowed rolesAllowed = (RolesAllowed)method.getAnnotation(RolesAllowed.class);
            MethodPermission methodPermission = new MethodPermission();
            methodPermission.getRoleName().addAll(Arrays.asList(rolesAllowed.value()));
            methodPermission.getMethod().add(new org.apache.openejb.jee.Method(ejbName, (Method)method.get()));
            assemblyDescriptor.getMethodPermission().add(methodPermission);
            RemoteBean remoteBean = (RemoteBean)bean;
            List<SecurityRoleRef> securityRoleRefs = remoteBean.getSecurityRoleRef();

            for(String role : rolesAllowed.value()) {
               securityRoleRefs.add(new SecurityRoleRef(role));
            }
         }

         for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(PermitAll.class)) {
            this.checkConflictingSecurityAnnotations(method, ejbName, ejbModule, seen);
            MethodPermission methodPermission = new MethodPermission();
            methodPermission.setUnchecked(true);
            methodPermission.getMethod().add(new org.apache.openejb.jee.Method(ejbName, (Method)method.get()));
            assemblyDescriptor.getMethodPermission().add(methodPermission);
         }

         for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(DenyAll.class)) {
            this.checkConflictingSecurityAnnotations(method, ejbName, ejbModule, seen);
            ExcludeList excludeList = assemblyDescriptor.getExcludeList();
            excludeList.addMethod(new org.apache.openejb.jee.Method(ejbName, (Method)method.get()));
         }

      }

      private void checkConflictingSecurityAnnotations(Annotated<Method> method, String ejbName, EjbModule ejbModule, List<Method> seen) {
         if (!seen.contains(method.get())) {
            seen.add(method.get());
            List<String> annotations = new ArrayList();

            for(Class<? extends Annotation> annotation : Arrays.asList(RolesAllowed.class, PermitAll.class, DenyAll.class)) {
               if (method.getAnnotation(annotation) != null) {
                  annotations.add("@" + annotation.getSimpleName());
               }
            }

            if (annotations.size() > 1) {
               ejbModule.getValidation().fail(ejbName, "conflictingSecurityAnnotations", ((Method)method.get()).getName(), Join.join(" and ", (Collection)annotations), ((Method)method.get()).getDeclaringClass());
            }

         }
      }

      private void processSchedules(EnterpriseBean bean, AnnotationFinder annotationFinder) {
         if (bean instanceof TimerConsumer) {
            TimerConsumer timerConsumer = (TimerConsumer)bean;
            Set<Annotated<Method>> scheduleMethods = new HashSet();
            scheduleMethods.addAll(annotationFinder.findMetaAnnotatedMethods(Schedules.class));
            scheduleMethods.addAll(annotationFinder.findMetaAnnotatedMethods(Schedule.class));
            List<Timer> timers = timerConsumer.getTimer();
            Set<String> methodsConfiguredInDeploymentXml = new HashSet();

            for(Timer timer : timers) {
               NamedMethod namedMethod = timer.getTimeoutMethod();
               methodsConfiguredInDeploymentXml.add(namedMethod.getMethodName() + (namedMethod.getMethodParams() == null ? "" : Join.join("", (Collection)namedMethod.getMethodParams().getMethodParam())));
            }

            for(Annotated<Method> method : scheduleMethods) {
               if (!methodsConfiguredInDeploymentXml.contains(((Method)method.get()).getName() + Join.join("", (Object[])this.asStrings(((Method)method.get()).getParameterTypes())))) {
                  List<Schedule> scheduleAnnotationList = new ArrayList();
                  Schedules schedulesAnnotation = (Schedules)method.getAnnotation(Schedules.class);
                  if (schedulesAnnotation != null) {
                     scheduleAnnotationList.addAll(Arrays.asList(schedulesAnnotation.value()));
                  }

                  Schedule scheduleAnnotation = (Schedule)method.getAnnotation(Schedule.class);
                  if (scheduleAnnotation != null) {
                     scheduleAnnotationList.add(scheduleAnnotation);
                  }

                  for(Schedule schedule : scheduleAnnotationList) {
                     Timer timer = new Timer();
                     timer.setPersistent(schedule.persistent());
                     timer.setInfo(schedule.info() != null && !schedule.info().isEmpty() ? schedule.info() : null);
                     timer.setTimezone(schedule.timezone() != null && !schedule.timezone().isEmpty() ? schedule.timezone() : null);
                     TimerSchedule timerSchedule = new TimerSchedule();
                     timerSchedule.setSecond(schedule.second());
                     timerSchedule.setMinute(schedule.minute());
                     timerSchedule.setHour(schedule.hour());
                     timerSchedule.setDayOfWeek(schedule.dayOfWeek());
                     timerSchedule.setDayOfMonth(schedule.dayOfMonth());
                     timerSchedule.setMonth(schedule.month());
                     timerSchedule.setYear(schedule.year());
                     timer.setSchedule(timerSchedule);
                     timer.setTimeoutMethod(new NamedMethod((Method)method.get()));
                     timers.add(timer);
                  }
               }
            }

         }
      }

      private void processCallbacks(Lifecycle bean, AnnotationFinder annotationFinder) {
         boolean override = "true".equalsIgnoreCase(this.getProperty("openejb.callbacks.override", "false"));
         if (this.apply(override, bean.getPostConstruct())) {
            for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(PostConstruct.class))) {
               bean.getPostConstruct().add(new LifecycleCallback((Method)method.get()));
            }
         }

         if (this.apply(override, bean.getPreDestroy())) {
            for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(PreDestroy.class))) {
               bean.getPreDestroy().add(new LifecycleCallback((Method)method.get()));
            }
         }

         if (bean instanceof Invokable) {
            Invokable invokable = (Invokable)bean;
            if (this.apply(override, invokable.getAroundInvoke())) {
               for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(AroundInvoke.class))) {
                  invokable.getAroundInvoke().add(new org.apache.openejb.jee.AroundInvoke((Method)method.get()));
               }
            }

            if (this.apply(override, invokable.getAroundTimeout())) {
               for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(AroundTimeout.class))) {
                  invokable.getAroundTimeout().add(new org.apache.openejb.jee.AroundTimeout((Method)method.get()));
               }
            }
         }

         if (bean instanceof TimerConsumer) {
            TimerConsumer timerConsumer = (TimerConsumer)bean;
            if (timerConsumer.getTimeoutMethod() == null) {
               List<Annotated<Method>> timeoutMethods = AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(javax.ejb.Timeout.class));
               if (timeoutMethods.size() >= 1) {
                  timerConsumer.setTimeoutMethod(new NamedMethod((Method)((Annotated)timeoutMethods.get(timeoutMethods.size() - 1)).get()));
               }
            }
         }

         if (bean instanceof Session) {
            Session session = (Session)bean;
            LifecycleCallback afterBegin = (LifecycleCallback)this.getFirst(session.getAfterBegin());
            if (afterBegin == null) {
               for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(AfterBegin.class))) {
                  session.getAfterBegin().add(new LifecycleCallback((Method)method.get()));
               }
            }

            LifecycleCallback beforeCompletion = (LifecycleCallback)this.getFirst(session.getBeforeCompletion());
            if (beforeCompletion == null) {
               for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(BeforeCompletion.class))) {
                  session.getBeforeCompletion().add(new LifecycleCallback((Method)method.get()));
               }
            }

            LifecycleCallback afterCompletion = (LifecycleCallback)this.getFirst(session.getAfterCompletion());
            if (afterCompletion == null) {
               for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(AfterCompletion.class))) {
                  session.getAfterCompletion().add(new LifecycleCallback((Method)method.get()));
               }
            }

            if (this.apply(override, session.getPostActivate())) {
               for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(PostActivate.class))) {
                  session.getPostActivate().add(new LifecycleCallback((Method)method.get()));
               }
            }

            if (this.apply(override, session.getPrePassivate())) {
               for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(PrePassivate.class))) {
                  session.getPrePassivate().add(new LifecycleCallback((Method)method.get()));
               }
            }

            for(Annotated<Method> method : AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(Init.class))) {
               InitMethod initMethod = new InitMethod((Method)method.get());
               Init init = (Init)method.getAnnotation(Init.class);
               if (init.value() != null && !init.value().equals("")) {
                  initMethod.setCreateMethod(init.value());
               }

               session.getInitMethod().add(initMethod);
            }

            List<Annotated<Method>> removeMethods = AnnotationDeployer.sortMethods(annotationFinder.findMetaAnnotatedMethods(Remove.class));
            Map<NamedMethod, RemoveMethod> declaredRemoveMethods = new HashMap();

            for(RemoveMethod removeMethod : session.getRemoveMethod()) {
               declaredRemoveMethods.put(removeMethod.getBeanMethod(), removeMethod);
            }

            for(Annotated<Method> method : removeMethods) {
               Remove remove = (Remove)method.getAnnotation(Remove.class);
               RemoveMethod removeMethod = new RemoveMethod((Method)method.get(), remove.retainIfException());
               RemoveMethod declaredRemoveMethod = (RemoveMethod)declaredRemoveMethods.get(removeMethod.getBeanMethod());
               if (declaredRemoveMethod == null) {
                  session.getRemoveMethod().add(removeMethod);
               } else if (!declaredRemoveMethod.isExplicitlySet()) {
                  declaredRemoveMethod.setRetainIfException(remove.retainIfException());
               }
            }
         }

      }

      private boolean apply(boolean override, List<?> list) {
         if (!override) {
            return true;
         } else {
            return list.size() == 0;
         }
      }

      public void buildAnnotatedRefs(JndiConsumer consumer, IAnnotationFinder annotationFinder, ClassLoader classLoader) throws OpenEJBException {
         List<EJB> ejbList = new ArrayList();

         for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(EJBs.class)) {
            EJBs ejbs = (EJBs)clazz.getAnnotation(EJBs.class);
            ejbList.addAll(Arrays.asList(ejbs.value()));
         }

         for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(EJB.class)) {
            EJB e = (EJB)clazz.getAnnotation(EJB.class);
            ejbList.add(e);
         }

         for(EJB ejb : ejbList) {
            this.buildEjbRef(consumer, ejb, (Member)null);
         }

         for(Annotated<Field> field : annotationFinder.findMetaAnnotatedFields(EJB.class)) {
            EJB ejb = (EJB)field.getAnnotation(EJB.class);
            Member member = new FieldMember((Field)field.get());
            this.buildEjbRef(consumer, ejb, member);
         }

         for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(EJB.class)) {
            EJB ejb = (EJB)method.getAnnotation(EJB.class);
            Member member = new MethodMember((Method)method.get());
            this.buildEjbRef(consumer, ejb, member);
         }

         List<Resource> resourceList = new ArrayList();

         for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(Resources.class)) {
            Resources resources = (Resources)clazz.getAnnotation(Resources.class);
            resourceList.addAll(Arrays.asList(resources.value()));
         }

         for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(Resource.class)) {
            Resource resource = (Resource)clazz.getAnnotation(Resource.class);
            resourceList.add(resource);
         }

         for(Resource resource : resourceList) {
            this.buildResource(consumer, resource, (Member)null);
         }

         for(Annotated<Field> field : annotationFinder.findMetaAnnotatedFields(Resource.class)) {
            Resource resource = (Resource)field.getAnnotation(Resource.class);
            Member member = new FieldMember((Field)field.get());
            this.buildResource(consumer, resource, member);
         }

         for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(Resource.class)) {
            Resource resource = (Resource)method.getAnnotation(Resource.class);
            Member member = new MethodMember((Method)method.get());
            this.buildResource(consumer, resource, member);
         }

         for(Annotated<Field> field : annotationFinder.findMetaAnnotatedFields(Context.class)) {
            Member member = new FieldMember((Field)field.get());
            this.buildContext(consumer, member);
         }

         List<WebServiceRef> webservicerefList = new ArrayList();

         for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(WebServiceRefs.class)) {
            WebServiceRefs webServiceRefs = (WebServiceRefs)clazz.getAnnotation(WebServiceRefs.class);
            webservicerefList.addAll(Arrays.asList(webServiceRefs.value()));
         }

         for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(WebServiceRef.class)) {
            WebServiceRef webServiceRef = (WebServiceRef)clazz.getAnnotation(WebServiceRef.class);
            webservicerefList.add(webServiceRef);
         }

         for(WebServiceRef webserviceref : webservicerefList) {
            this.buildWebServiceRef(consumer, webserviceref, (javax.jws.HandlerChain)null, (Member)null, classLoader);
         }

         for(Annotated<Field> field : annotationFinder.findMetaAnnotatedFields(WebServiceRef.class)) {
            WebServiceRef webserviceref = (WebServiceRef)field.getAnnotation(WebServiceRef.class);
            javax.jws.HandlerChain handlerChain = (javax.jws.HandlerChain)field.getAnnotation(javax.jws.HandlerChain.class);
            Member member = new FieldMember((Field)field.get());
            this.buildWebServiceRef(consumer, webserviceref, handlerChain, member, classLoader);
         }

         for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(WebServiceRef.class)) {
            WebServiceRef webserviceref = (WebServiceRef)method.getAnnotation(WebServiceRef.class);
            javax.jws.HandlerChain handlerChain = (javax.jws.HandlerChain)method.getAnnotation(javax.jws.HandlerChain.class);
            Member member = new MethodMember((Method)method.get());
            this.buildWebServiceRef(consumer, webserviceref, handlerChain, member, classLoader);
         }

         List<PersistenceUnit> persistenceUnitList = new ArrayList();

         for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(PersistenceUnits.class)) {
            PersistenceUnits persistenceUnits = (PersistenceUnits)clazz.getAnnotation(PersistenceUnits.class);
            persistenceUnitList.addAll(Arrays.asList(persistenceUnits.value()));
         }

         for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(PersistenceUnit.class)) {
            PersistenceUnit persistenceUnit = (PersistenceUnit)clazz.getAnnotation(PersistenceUnit.class);
            persistenceUnitList.add(persistenceUnit);
         }

         for(PersistenceUnit pUnit : persistenceUnitList) {
            this.buildPersistenceUnit(consumer, pUnit, (Member)null);
         }

         for(Annotated<Field> field : annotationFinder.findMetaAnnotatedFields(PersistenceUnit.class)) {
            PersistenceUnit pUnit = (PersistenceUnit)field.getAnnotation(PersistenceUnit.class);
            Member member = new FieldMember((Field)field.get());
            this.buildPersistenceUnit(consumer, pUnit, member);
         }

         for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(PersistenceUnit.class)) {
            PersistenceUnit pUnit = (PersistenceUnit)method.getAnnotation(PersistenceUnit.class);
            Member member = new MethodMember((Method)method.get());
            this.buildPersistenceUnit(consumer, pUnit, member);
         }

         PersistenceContextAnnFactory pcFactory = new PersistenceContextAnnFactory();
         List<PersistenceContext> persistenceContextList = new ArrayList();

         for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(PersistenceContexts.class)) {
            PersistenceContexts persistenceContexts = (PersistenceContexts)clazz.getAnnotation(PersistenceContexts.class);
            persistenceContextList.addAll(Arrays.asList(persistenceContexts.value()));
            pcFactory.addAnnotations((Class)clazz.get());
         }

         for(Annotated<Class<?>> clazz : annotationFinder.findMetaAnnotatedClasses(PersistenceContext.class)) {
            PersistenceContext persistenceContext = (PersistenceContext)clazz.getAnnotation(PersistenceContext.class);
            persistenceContextList.add(persistenceContext);
            pcFactory.addAnnotations((Class)clazz.get());
            if (((Class)clazz.get()).isInterface()) {
               Member member = new FilledMember("em", EntityManager.class, (Class)clazz.get());
               this.buildPersistenceContext(consumer, pcFactory.create(persistenceContext, member), member);
            }
         }

         for(PersistenceContext pCtx : persistenceContextList) {
            this.buildPersistenceContext(consumer, pcFactory.create(pCtx, (Member)null), (Member)null);
         }

         for(Annotated<Field> field : annotationFinder.findMetaAnnotatedFields(PersistenceContext.class)) {
            PersistenceContext pCtx = (PersistenceContext)field.getAnnotation(PersistenceContext.class);
            Member member = new FieldMember((Field)field.get());
            this.buildPersistenceContext(consumer, pcFactory.create(pCtx, member), member);
         }

         for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(PersistenceContext.class)) {
            PersistenceContext pCtx = (PersistenceContext)method.getAnnotation(PersistenceContext.class);
            Member member = new MethodMember((Method)method.get());
            this.buildPersistenceContext(consumer, pcFactory.create(pCtx, member), member);
         }

         for(Annotated<Class<?>> annotated : annotationFinder.findMetaAnnotatedClasses(DataSourceDefinitions.class)) {
            DataSourceDefinitions defs = (DataSourceDefinitions)annotated.getAnnotation(DataSourceDefinitions.class);

            for(DataSourceDefinition definition : defs.value()) {
               this.buildDataSourceDefinition(consumer, definition);
            }
         }

         for(Annotated<Class<?>> annotated : annotationFinder.findMetaAnnotatedClasses(DataSourceDefinition.class)) {
            DataSourceDefinition definition = (DataSourceDefinition)annotated.getAnnotation(DataSourceDefinition.class);
            this.buildDataSourceDefinition(consumer, definition);
         }

         for(Annotated<Class<?>> annotated : annotationFinder.findMetaAnnotatedClasses(JMSConnectionFactoryDefinitions.class)) {
            JMSConnectionFactoryDefinitions defs = (JMSConnectionFactoryDefinitions)annotated.getAnnotation(JMSConnectionFactoryDefinitions.class);

            for(JMSConnectionFactoryDefinition definition : defs.value()) {
               this.buildConnectionFactoryDefinition(consumer, definition);
            }
         }

         for(Annotated<Class<?>> annotated : annotationFinder.findMetaAnnotatedClasses(JMSConnectionFactoryDefinition.class)) {
            JMSConnectionFactoryDefinition definition = (JMSConnectionFactoryDefinition)annotated.getAnnotation(JMSConnectionFactoryDefinition.class);
            this.buildConnectionFactoryDefinition(consumer, definition);
         }

         for(Annotated<Class<?>> annotated : annotationFinder.findMetaAnnotatedClasses(JMSDestinationDefinitions.class)) {
            JMSDestinationDefinitions defs = (JMSDestinationDefinitions)annotated.getAnnotation(JMSDestinationDefinitions.class);

            for(JMSDestinationDefinition definition : defs.value()) {
               this.buildDestinationDefinition(consumer, definition);
            }
         }

         for(Annotated<Class<?>> annotated : annotationFinder.findMetaAnnotatedClasses(JMSDestinationDefinition.class)) {
            this.buildDestinationDefinition(consumer, (JMSDestinationDefinition)annotated.getAnnotation(JMSDestinationDefinition.class));
         }

      }

      private void buildContext(JndiConsumer consumer, Member member) {
         ContextRef ref = new ContextRef();
         ref.setName(member.getDeclaringClass().getName() + "/" + member.getName());
         ref.setResType(member.getType().getName());
         InjectionTarget target = new InjectionTarget();
         target.setInjectionTargetClass(member.getDeclaringClass().getName());
         target.setInjectionTargetName(member.getName());
         ref.getInjectionTarget().add(target);
         consumer.getResourceRef().add(ref);
      }

      private void buildEjbRef(JndiConsumer consumer, EJB ejb, Member member) {
         String name = consumer.getJndiConsumerName();
         if (member == null) {
            boolean shouldReturn = false;
            if (ejb.name().equals("")) {
               this.fail(name, "ejbAnnotation.onClassWithNoName");
               shouldReturn = true;
            }

            if (ejb.beanInterface().equals(Object.class)) {
               this.fail(name, "ejbAnnotation.onClassWithNoBeanInterface");
               shouldReturn = true;
            }

            if (shouldReturn) {
               return;
            }
         }

         EjbRef ejbRef = new EjbRef();
         ejbRef.setRefType(Type.UNKNOWN);
         String refName = ejb.name();
         if (refName.length() == 0) {
            refName = member.getDeclaringClass().getName() + "/" + member.getName();
         }

         ejbRef.setEjbRefName(this.normalize(refName));
         if (member != null) {
            InjectionTarget target = new InjectionTarget();
            target.setInjectionTargetClass(member.getDeclaringClass().getName());
            target.setInjectionTargetName(member.getName());
            ejbRef.getInjectionTarget().add(target);
         }

         Class<?> interfce = ejb.beanInterface();
         if (interfce.equals(Object.class)) {
            interfce = member == null ? null : member.getType();
         }

         boolean localbean = this.isKnownLocalBean(interfce);
         boolean dynamicallyImplemented = DynamicProxyImplFactory.isKnownDynamicallyImplemented(interfce);
         if (localbean || interfce == null || this.isValidEjbInterface(name, interfce, ejbRef.getName())) {
            if (interfce != null && !interfce.equals(Object.class)) {
               if (EJBHome.class.isAssignableFrom(interfce)) {
                  ejbRef.setHome(interfce.getName());
                  Method[] methods = interfce.getMethods();

                  for(Method method : methods) {
                     if (method.getName().startsWith("create")) {
                        ejbRef.setRemote(method.getReturnType().getName());
                        break;
                     }
                  }

                  ejbRef.setRefType(Type.REMOTE);
               } else if (EJBLocalHome.class.isAssignableFrom(interfce)) {
                  ejbRef.setHome(interfce.getName());
                  Method[] methods = interfce.getMethods();

                  for(Method method : methods) {
                     if (method.getName().startsWith("create")) {
                        ejbRef.setRemote(method.getReturnType().getName());
                        break;
                     }
                  }

                  ejbRef.setRefType(Type.LOCAL);
               } else if (localbean) {
                  ejbRef.setRefType(Type.LOCAL);
                  ejbRef.setRemote(interfce.getName());
               } else if (dynamicallyImplemented) {
                  ejbRef.setRefType(Type.LOCAL);
                  ejbRef.setRemote(interfce.getName());
               } else {
                  ejbRef.setRemote(interfce.getName());
                  if (interfce.getAnnotation(Local.class) != null) {
                     ejbRef.setRefType(Type.LOCAL);
                  } else if (interfce.getAnnotation(Remote.class) != null) {
                     ejbRef.setRefType(Type.REMOTE);
                  }
               }
            }

            String ejbName = ejb.beanName();
            if (ejbName.equals("")) {
               ejbName = null;
            }

            ejbRef.setEjbLink(ejbName);
            String mappedName = ejb.mappedName();
            if (mappedName.equals("")) {
               mappedName = null;
            }

            ejbRef.setMappedName(mappedName);
            String lookupName = getLookupName(ejb);
            if (lookupName.equals("")) {
               lookupName = null;
            }

            ejbRef.setLookupName(lookupName);
            Map<String, EjbRef> remoteRefs = consumer.getEjbRefMap();
            if (remoteRefs.containsKey(ejbRef.getName())) {
               EjbRef ref = (EjbRef)remoteRefs.get(ejbRef.getName());
               if (ref.getRemote() == null) {
                  ref.setRemote(ejbRef.getRemote());
               }

               if (ref.getHome() == null) {
                  ref.setHome(ejbRef.getHome());
               }

               if (ref.getMappedName() == null) {
                  ref.setMappedName(ejbRef.getMappedName());
               }

               ref.getInjectionTarget().addAll(ejbRef.getInjectionTarget());
            } else {
               Map<String, EjbLocalRef> localRefs = consumer.getEjbLocalRefMap();
               if (localRefs.containsKey(ejbRef.getName())) {
                  EjbLocalRef ejbLocalRef = new EjbLocalRef(ejbRef);
                  EjbLocalRef ref = (EjbLocalRef)localRefs.get(ejbLocalRef.getName());
                  if (ref.getLocal() == null) {
                     ref.setLocal(ejbLocalRef.getLocal());
                  }

                  if (ref.getLocalHome() == null) {
                     ref.setLocalHome(ejbLocalRef.getLocalHome());
                  }

                  if (ref.getMappedName() == null) {
                     ref.setMappedName(ejbLocalRef.getMappedName());
                  }

                  if (ref.getEjbLink() == null) {
                     ref.setEjbLink(ejbLocalRef.getEjbLink());
                  }

                  ref.getInjectionTarget().addAll(ejbLocalRef.getInjectionTarget());
               } else {
                  switch (ejbRef.getRefType()) {
                     case UNKNOWN:
                     case REMOTE:
                        consumer.getEjbRef().add(ejbRef);
                        break;
                     case LOCAL:
                        consumer.getEjbLocalRef().add(new EjbLocalRef(ejbRef));
                  }

               }
            }
         }
      }

      private String normalize(String refName) {
         if (refName.startsWith("java:")) {
            return refName.startsWith("/") ? refName.substring(1) : refName;
         } else {
            return "java:comp/env/" + refName;
         }
      }

      private boolean isKnownLocalBean(Class clazz) {
         if (clazz.isAnnotation()) {
            return false;
         } else if (clazz.isArray()) {
            return false;
         } else if (clazz.isEnum()) {
            return false;
         } else if (clazz.isInterface()) {
            return false;
         } else if (clazz.isPrimitive()) {
            return false;
         } else if (Modifier.isAbstract(clazz.getModifiers())) {
            return false;
         } else {
            return !Modifier.isFinal(clazz.getModifiers());
         }
      }

      private boolean isValidEjbInterface(String b, Class clazz, String refName) {
         if (clazz.isInterface()) {
            if (EJBObject.class.isAssignableFrom(clazz)) {
               this.fail(b, "ann.ejb.ejbObject", clazz.getName(), refName);
               return false;
            }

            if (EJBLocalObject.class.isAssignableFrom(clazz)) {
               this.fail(b, "ann.ejb.ejbLocalObject", clazz.getName(), refName);
               return false;
            }
         }

         return true;
      }

      private void fail(String component, String key, Object... details) {
         AnnotationDeployer.getValidationContext().fail(component, key, details);
      }

      private void buildResource(JndiConsumer consumer, Resource resource, Member member) {
         if (member == null && resource.name().length() == 0) {
            this.fail(consumer.getJndiConsumerName(), "resourceAnnotation.onClassWithNoName");
         } else {
            String refName = resource.name();
            if (refName.equals("")) {
               refName = member.getDeclaringClass().getName() + "/" + member.getName();
            }

            refName = this.normalize(refName);
            JndiReference reference = (JndiReference)consumer.getEnvEntryMap().get(refName);
            if (reference == null) {
               if (member != null) {
                  Class type = member.getType();
                  if (EntityManager.class.isAssignableFrom(type)) {
                     this.fail(consumer.getJndiConsumerName(), "resourceRef.onEntityManager", refName);
                     return;
                  }

                  if (EntityManagerFactory.class.isAssignableFrom(type)) {
                     this.fail(consumer.getJndiConsumerName(), "resourceRef.onEntityManagerFactory", refName);
                     return;
                  }
               }

               Class type;
               if (member == null) {
                  type = resource.type();
               } else {
                  type = member.getType();
               }

               if (AnnotationDeployer.knownResourceEnvTypes.contains(type.getName())) {
                  ResourceEnvRef resourceEnvRef = (ResourceEnvRef)consumer.getResourceEnvRefMap().get(refName);
                  if (resourceEnvRef == null) {
                     resourceEnvRef = new ResourceEnvRef();
                     resourceEnvRef.setName(refName);
                     consumer.getResourceEnvRef().add(resourceEnvRef);
                  }

                  if (resourceEnvRef.getResourceEnvRefType() == null || "".equals(resourceEnvRef.getResourceEnvRefType())) {
                     resourceEnvRef.setResourceEnvRefType(type.getName());
                  }

                  reference = resourceEnvRef;
               } else if (AnnotationDeployer.isKnownEnvironmentEntryType(type)) {
                  String lookupName = getLookupName(resource);
                  if (!lookupName.equals("")) {
                     EnvEntry envEntry = new EnvEntry();
                     envEntry.setName(refName);
                     consumer.getEnvEntry().add(envEntry);
                     envEntry.setLookupName(lookupName);
                     reference = envEntry;
                  } else if (AnnotationDeployer.isShareableJNDINamespace(refName)) {
                     EnvEntry envEntry = new EnvEntry();
                     envEntry.setName(refName);
                     consumer.getEnvEntry().add(envEntry);
                     reference = envEntry;
                  } else {
                     String shortName = this.normalize(member.getName());
                     reference = (JndiReference)consumer.getEnvEntryMap().get(shortName);
                     if (reference == null) {
                        EnvEntry envEntry = new EnvEntry();
                        envEntry.setName(refName);
                        consumer.getEnvEntry().add(envEntry);
                        reference = envEntry;
                     }
                  }
               } else {
                  ResourceRef resourceRef = (ResourceRef)consumer.getResourceRefMap().get(refName);
                  if (resourceRef == null) {
                     resourceRef = new ResourceRef();
                     resourceRef.setName(refName);
                     consumer.getResourceRef().add(resourceRef);
                  }

                  if (member != null) {
                     resourceRef.setOrigin(member.getDeclaringClass() + "#" + member.getName());
                  }

                  if (resourceRef.getResAuth() == null) {
                     if (resource.authenticationType() == AuthenticationType.APPLICATION) {
                        resourceRef.setResAuth(ResAuth.APPLICATION);
                     } else {
                        resourceRef.setResAuth(ResAuth.CONTAINER);
                     }
                  }

                  if (resourceRef.getResType() == null || "".equals(resourceRef.getResType())) {
                     resourceRef.setResType(type.getName());
                  }

                  if (resourceRef.getResSharingScope() == null) {
                     if (resource.shareable()) {
                        resourceRef.setResSharingScope(ResSharingScope.SHAREABLE);
                     } else {
                        resourceRef.setResSharingScope(ResSharingScope.UNSHAREABLE);
                     }
                  }

                  reference = resourceRef;
               }
            }

            if (member != null) {
               InjectionTarget target = new InjectionTarget();
               target.setInjectionTargetClass(member.getDeclaringClass().getName());
               target.setInjectionTargetName(member.getName());
               reference.getInjectionTarget().add(target);
            }

            if (reference.getMappedName() == null && !resource.mappedName().equals("")) {
               reference.setMappedName(resource.mappedName());
            }

            if (reference.getLookupName() == null) {
               String lookupName = getLookupName(resource);
               if (!lookupName.equals("")) {
                  reference.setLookupName(lookupName);
               }
            }

         }
      }

      private static Method getLookupMethod(Class cls) {
         String name = cls.getName();
         if (!AnnotationDeployer.lookupMissing.contains(name)) {
            try {
               return cls.getMethod("lookup", (Class[])null);
            } catch (NoSuchMethodException var4) {
               AnnotationDeployer.lookupMissing.add(name);
               String exists = getSourceIfExists(cls);
               AnnotationDeployer.logger.warning("Method 'lookup' is not available for '" + name + "'" + (null != exists ? ". The old API '" + exists + "' was found on the classpath." : ". Probably using an older Runtime."));
            }
         }

         return null;
      }

      private static String getSourceIfExists(Class<?> cls) {
         return cls.getProtectionDomain() != null && cls.getProtectionDomain().getCodeSource() != null && cls.getProtectionDomain().getCodeSource().getLocation() != null ? cls.getProtectionDomain().getCodeSource().getLocation().toString() : null;
      }

      private static String getLookupName(Resource resource) {
         String value = "";
         Method lookupMethod = getLookupMethod(Resource.class);
         if (lookupMethod != null) {
            try {
               value = (String)lookupMethod.invoke(resource, (Object[])null);
            } catch (Exception var4) {
            }
         }

         return value;
      }

      private static String getLookupName(EJB ejb) {
         String value = "";
         Method lookupMethod = getLookupMethod(EJB.class);
         if (lookupMethod != null) {
            try {
               value = (String)lookupMethod.invoke(ejb, (Object[])null);
            } catch (Exception var4) {
            }
         }

         return value;
      }

      private void buildPersistenceUnit(JndiConsumer consumer, PersistenceUnit persistenceUnit, Member member) throws OpenEJBException {
         String refName = persistenceUnit.name();
         if (refName.length() == 0) {
            if (member == null) {
               this.fail(consumer.getJndiConsumerName(), "persistenceUnitAnnotation.onClassWithNoName", persistenceUnit.unitName());
               return;
            }

            refName = member.getDeclaringClass().getName() + "/" + member.getName();
         }

         refName = this.normalize(refName);
         PersistenceUnitRef persistenceUnitRef = (PersistenceUnitRef)consumer.getPersistenceUnitRefMap().get(refName);
         if (persistenceUnitRef == null) {
            persistenceUnitRef = new PersistenceUnitRef();
            persistenceUnitRef.setPersistenceUnitName(persistenceUnit.unitName());
            persistenceUnitRef.setPersistenceUnitRefName(refName);
            consumer.getPersistenceUnitRef().add(persistenceUnitRef);
         }

         if (member != null) {
            Class type = member.getType();
            if (EntityManager.class.isAssignableFrom(type)) {
               AnnotationDeployer.failIfCdiProducer(member, "EntityManagerFactory");
               ValidationContext validationContext = AnnotationDeployer.getValidationContext();
               String jndiConsumerName = consumer.getJndiConsumerName();
               String name = persistenceUnitRef.getName();
               validationContext.fail(jndiConsumerName, "persistenceUnitAnnotation.onEntityManager", name);
            } else if (!EntityManagerFactory.class.isAssignableFrom(type)) {
               AnnotationDeployer.failIfCdiProducer(member, "EntityManagerFactory");
               this.fail(consumer.getJndiConsumerName(), "persistenceUnitAnnotation.onNonEntityManagerFactory", persistenceUnitRef.getName());
            } else {
               InjectionTarget target = new InjectionTarget();
               target.setInjectionTargetClass(member.getDeclaringClass().getName());
               target.setInjectionTargetName(member.getName());
               persistenceUnitRef.getInjectionTarget().add(target);
            }
         }

         if (persistenceUnitRef.getPersistenceUnitName() == null && !persistenceUnit.unitName().equals("")) {
            persistenceUnitRef.setPersistenceUnitName(persistenceUnit.unitName());
         }

      }

      private void buildPersistenceContext(JndiConsumer consumer, PersistenceContextAnn persistenceContext, Member member) throws OpenEJBException {
         AppModule module = null;
         if (AnnotationDeployer.currentModule.get() instanceof AppModule) {
            module = (AppModule)AnnotationDeployer.currentModule.get();
         } else if (AnnotationDeployer.currentModule.get() instanceof Module) {
            module = ((Module)AnnotationDeployer.currentModule.get()).getAppModule();
         }

         if (module != null && org.apache.openejb.jee.jpa.unit.TransactionType.RESOURCE_LOCAL.equals(module.getTransactionType(persistenceContext.unitName()))) {
            String name = persistenceContext.unitName();
            if (name == null || name.isEmpty()) {
               try {
                  name = ((org.apache.openejb.jee.jpa.unit.PersistenceUnit)((PersistenceModule)module.getPersistenceModules().iterator().next()).getPersistence().getPersistenceUnit().iterator().next()).getName();
               } catch (Exception var13) {
                  name = "?";
               }
            }

            AnnotationDeployer.logger.info("PersistenceUnit '" + name + "' is a RESOURCE_LOCAL one, you'll have to manage @PersistenceContext yourself.");
         } else {
            String refName = persistenceContext.name();
            if (refName.length() == 0) {
               if (member == null) {
                  this.fail(consumer.getJndiConsumerName(), "persistenceContextAnnotation.onClassWithNoName", persistenceContext.unitName());
                  return;
               }

               refName = member.getDeclaringClass().getName() + "/" + member.getName();
            }

            refName = this.normalize(refName);
            PersistenceContextRef persistenceContextRef = (PersistenceContextRef)consumer.getPersistenceContextRefMap().get(refName);
            if (persistenceContextRef == null) {
               persistenceContextRef = new PersistenceContextRef();
               if (persistenceContext.synchronization() != null) {
                  persistenceContextRef.setPersistenceContextSynchronization(PersistenceContextSynchronization.valueOf(persistenceContext.synchronization().toUpperCase(Locale.ENGLISH)));
               }

               persistenceContextRef.setPersistenceUnitName(persistenceContext.unitName());
               persistenceContextRef.setPersistenceContextRefName(refName);
               if ("EXTENDED".equalsIgnoreCase(persistenceContext.type())) {
                  persistenceContextRef.setPersistenceContextType(PersistenceContextType.EXTENDED);
               } else {
                  persistenceContextRef.setPersistenceContextType(PersistenceContextType.TRANSACTION);
               }

               consumer.getPersistenceContextRef().add(persistenceContextRef);
            } else {
               if (persistenceContextRef.getPersistenceUnitName() == null || "".equals(persistenceContextRef.getPersistenceUnitName())) {
                  persistenceContextRef.setPersistenceUnitName(persistenceContext.unitName());
               }

               if (persistenceContextRef.getPersistenceContextType() == null) {
                  if ("EXTENDED".equalsIgnoreCase(persistenceContext.type())) {
                     persistenceContextRef.setPersistenceContextType(PersistenceContextType.EXTENDED);
                  } else {
                     persistenceContextRef.setPersistenceContextType(PersistenceContextType.TRANSACTION);
                  }
               }

               if (persistenceContextRef.getPersistenceContextSynchronization() == null && persistenceContext.synchronization() != null) {
                  persistenceContextRef.setPersistenceContextSynchronization(PersistenceContextSynchronization.valueOf(persistenceContext.synchronization().toUpperCase(Locale.ENGLISH)));
               }
            }

            List<Property> persistenceProperties = persistenceContextRef.getPersistenceProperty();
            if (persistenceProperties == null) {
               persistenceProperties = new ArrayList();
               persistenceContextRef.setPersistenceProperty(persistenceProperties);
            }

            for(Map.Entry<String, String> persistenceProperty : persistenceContext.properties().entrySet()) {
               boolean flag = true;

               for(Property prpty : persistenceProperties) {
                  if (prpty.getName().equals(persistenceProperty.getKey())) {
                     flag = false;
                     break;
                  }
               }

               if (flag) {
                  Property property = new Property();
                  property.setName((String)persistenceProperty.getKey());
                  property.setValue((String)persistenceProperty.getValue());
                  persistenceProperties.add(property);
               }
            }

            if (member != null) {
               Class type = member.getType();
               if (EntityManagerFactory.class.isAssignableFrom(type)) {
                  AnnotationDeployer.failIfCdiProducer(member, "EntityManager");
                  this.fail(consumer.getJndiConsumerName(), "persistenceContextAnnotation.onEntityManagerFactory", persistenceContextRef.getName());
               } else if (!EntityManager.class.isAssignableFrom(type)) {
                  AnnotationDeployer.failIfCdiProducer(member, "EntityManager");
                  this.fail(consumer.getJndiConsumerName(), "persistenceContextAnnotation.onNonEntityManager", persistenceContextRef.getName());
               } else {
                  InjectionTarget target = new InjectionTarget();
                  target.setInjectionTargetClass(member.getDeclaringClass().getName());
                  target.setInjectionTargetName(member.getName());
                  persistenceContextRef.getInjectionTarget().add(target);
               }
            }

         }
      }

      private void buildDestinationDefinition(JndiConsumer consumer, JMSDestinationDefinition definition) {
         JMSDestination destination = new JMSDestination();
         destination.setName(definition.name());
         destination.setClassName(definition.className());
         destination.setInterfaceName(definition.interfaceName());
         destination.setResourceAdapter(definition.resourceAdapter());
         destination.setDestinationName(definition.destinationName());

         for(String s : definition.properties()) {
            int equal = s.indexOf(61);
            if (equal < s.length() - 1) {
               SuperProperties props = new SuperProperties();

               try {
                  props.load(new ByteArrayInputStream(s.getBytes()));

                  for(String key : props.stringPropertyNames()) {
                     if (!key.isEmpty()) {
                        destination.property(key, props.getProperty(key));
                     }
                  }
               } catch (IOException var13) {
                  String key = s.substring(0, equal).trim();
                  String value = s.substring(equal + 1).trim();
                  destination.property(key, value);
               }
            } else {
               destination.property(s.trim(), "");
            }
         }

         consumer.getJMSDestination().add(destination);
      }

      private void buildConnectionFactoryDefinition(JndiConsumer consumer, JMSConnectionFactoryDefinition definition) {
         JMSConnectionFactory connectionFactory = new JMSConnectionFactory();
         connectionFactory.setName(definition.name());
         connectionFactory.setMinPoolSize(definition.minPoolSize());
         connectionFactory.setMaxPoolSize(definition.maxPoolSize());
         connectionFactory.setClassName(definition.className());
         connectionFactory.setInterfaceName(definition.interfaceName());
         connectionFactory.setClientId(definition.clientId());
         connectionFactory.setUser(definition.user());
         connectionFactory.setPassword(definition.password());
         connectionFactory.setResourceAdapter(definition.resourceAdapter());
         connectionFactory.setTransactional(definition.transactional());

         for(String s : definition.properties()) {
            int equal = s.indexOf(61);
            if (equal < s.length() - 1) {
               SuperProperties props = new SuperProperties();

               try {
                  props.load(new ByteArrayInputStream(s.getBytes()));

                  for(String key : props.stringPropertyNames()) {
                     if (!key.isEmpty()) {
                        connectionFactory.property(key, props.getProperty(key));
                     }
                  }
               } catch (IOException var13) {
                  String key = s.substring(0, equal).trim();
                  String value = s.substring(equal + 1).trim();
                  connectionFactory.property(key, value);
               }
            } else {
               connectionFactory.property(s.trim(), "");
            }
         }

         consumer.getJMSConnectionFactories().add(connectionFactory);
      }

      private void buildDataSourceDefinition(JndiConsumer consumer, DataSourceDefinition d) {
         DataSource dataSource = new DataSource();
         dataSource.setName(d.name());
         dataSource.setClassName(d.className());
         dataSource.setTransactional(d.transactional());
         DataSource existing = (DataSource)consumer.getDataSourceMap().get(dataSource.getKey());
         if (existing == null) {
            if (!d.databaseName().isEmpty()) {
               dataSource.setDatabaseName(d.databaseName());
            }

            if (d.initialPoolSize() != -1) {
               dataSource.setInitialPoolSize(d.initialPoolSize());
            }

            if (d.isolationLevel() != -1) {
               dataSource.setIsolationLevel(IsolationLevel.fromFlag(d.isolationLevel()));
            }

            if (d.loginTimeout() != 0) {
               dataSource.setLoginTimeout(d.loginTimeout());
            }

            if (d.maxIdleTime() != -1) {
               dataSource.setMaxIdleTime(d.maxIdleTime());
            }

            if (d.maxPoolSize() != -1) {
               dataSource.setMaxPoolSize(d.maxPoolSize());
            }

            if (d.maxStatements() != -1) {
               dataSource.setMaxStatements(d.maxStatements());
            }

            if (d.minPoolSize() != -1) {
               dataSource.setMinPoolSize(d.minPoolSize());
            }

            if (!d.password().isEmpty()) {
               dataSource.setPassword(d.password());
            }

            if (d.portNumber() != -1) {
               dataSource.setPortNumber(d.portNumber());
            }

            if (!"localhost".equals(d.serverName())) {
               dataSource.setServerName(d.serverName());
            }

            if (!d.url().isEmpty()) {
               dataSource.setUrl(d.url());
            }

            if (!d.user().isEmpty()) {
               dataSource.setUser(d.user());
            }

            for(String s : d.properties()) {
               int equal = s.indexOf(61);
               if (equal < s.length() - 1) {
                  SuperProperties props = new SuperProperties();

                  try {
                     props.load(new ByteArrayInputStream(s.getBytes()));

                     for(String key : props.stringPropertyNames()) {
                        if (!key.isEmpty()) {
                           dataSource.property(key, props.getProperty(key));
                        }
                     }
                  } catch (IOException var14) {
                     String key = s.substring(0, equal).trim();
                     String value = s.substring(equal + 1).trim();
                     dataSource.property(key, value);
                  }
               } else {
                  dataSource.property(s.trim(), "");
               }
            }

            consumer.getDataSource().add(dataSource);
         }
      }

      private void buildWebServiceRef(JndiConsumer consumer, WebServiceRef webService, javax.jws.HandlerChain handlerChain, Member member, ClassLoader classLoader) throws OpenEJBException {
         String refName = webService.name();
         if (refName.equals("")) {
            if (member == null) {
               return;
            }

            refName = member.getDeclaringClass().getName() + "/" + member.getName();
         }

         refName = this.normalize(refName);
         ServiceRef serviceRef = (ServiceRef)consumer.getServiceRefMap().get(refName);
         if (serviceRef == null) {
            serviceRef = new ServiceRef();
            serviceRef.setServiceRefName(refName);
            consumer.getServiceRef().add(serviceRef);
         }

         if (member != null) {
            InjectionTarget target = new InjectionTarget();
            target.setInjectionTargetClass(member.getDeclaringClass().getName());
            target.setInjectionTargetName(member.getName());
            serviceRef.getInjectionTarget().add(target);
         }

         Class<?> serviceInterface = null;
         if (serviceRef.getServiceInterface() == null) {
            serviceInterface = webService.type();
            if (serviceInterface.equals(Object.class)) {
               serviceInterface = webService.value();
               if ((javax.xml.ws.Service.class.equals(serviceInterface) || Object.class.equals(serviceInterface)) && member != null) {
                  serviceInterface = member.getType();
               }
            }
         }

         if (serviceInterface == null || !javax.xml.ws.Service.class.isAssignableFrom(serviceInterface)) {
            serviceInterface = javax.xml.ws.Service.class;
         }

         serviceRef.setServiceInterface(serviceInterface.getName());
         if (serviceRef.getServiceRefType() == null || "".equals(serviceRef.getServiceRefType())) {
            if (webService.type() != Object.class) {
               serviceRef.setServiceRefType(webService.type().getName());
            } else {
               serviceRef.setServiceRefType(member.getType().getName());
            }
         }

         Class<?> refType = null;

         try {
            refType = classLoader.loadClass(AnnotationDeployer.realClassName(serviceRef.getType()));
         } catch (ClassNotFoundException var13) {
         }

         if (serviceRef.getMappedName() == null) {
            String mappedName = webService.mappedName();
            if (mappedName.equals("")) {
               mappedName = null;
            }

            serviceRef.setMappedName(mappedName);
         }

         if (serviceRef.getWsdlFile() == null) {
            String wsdlLocation = webService.wsdlLocation();
            if (!wsdlLocation.equals("")) {
               serviceRef.setWsdlFile(wsdlLocation);
            }
         }

         if (!SystemInstance.get().hasProperty("openejb.geronimo")) {
            if (serviceRef.getWsdlFile() == null && refType != null) {
               serviceRef.setWsdlFile(JaxWsUtils.getServiceWsdlLocation(refType, classLoader));
            }

            if (serviceRef.getWsdlFile() == null) {
               serviceRef.setWsdlFile(JaxWsUtils.getServiceWsdlLocation(serviceInterface, classLoader));
            }

            if (serviceRef.getServiceQname() == null && refType != null) {
               try {
                  serviceRef.setServiceQname(JaxWsUtils.getServiceQName(refType));
               } catch (IllegalArgumentException iae) {
                  if (FieldMember.class.isInstance(member) && ((FieldMember)FieldMember.class.cast(member)).field.getAnnotation(Produces.class) != null) {
                     throw new DefinitionException(((FieldMember)FieldMember.class.cast(member)).field + " is not a webservice client");
                  }

                  throw iae;
               }
            }

            if (serviceRef.getServiceQname() == null) {
               serviceRef.setServiceQname(JaxWsUtils.getServiceQName(serviceInterface));
            }

            if (serviceRef.getHandlerChains() == null && handlerChain != null) {
               try {
                  URL handlerFileURL = member.getDeclaringClass().getResource(handlerChain.file());
                  HandlerChains handlerChains = ReadDescriptors.readHandlerChains(handlerFileURL);
                  serviceRef.setHandlerChains(handlerChains);
               } catch (Throwable e) {
                  throw new OpenEJBException("Unable to load handler chain file: " + handlerChain.file(), e);
               }
            }

         }
      }

      private void processWebServiceHandlers(EjbModule ejbModule, EnterpriseBean bean, AnnotationFinder finder) throws OpenEJBException {
         Set<String> classes = new HashSet();
         if (ejbModule.getWebservices() != null) {
            for(WebserviceDescription webservice : ejbModule.getWebservices().getWebserviceDescription()) {
               for(PortComponent port : webservice.getPortComponent()) {
                  if (bean.getEjbName().equals(port.getServiceImplBean().getEjbLink()) && port.getHandlerChains() != null) {
                     for(HandlerChain handlerChain : port.getHandlerChains().getHandlerChain()) {
                        for(Handler handler : handlerChain.getHandler()) {
                           String handlerClass = AnnotationDeployer.realClassName(handler.getHandlerClass());
                           if (handlerClass != null) {
                              classes.add(handlerClass);
                           }
                        }
                     }
                  }
               }
            }
         }

         AnnotationFinder handlersFinder = finder.select(classes);
         this.buildAnnotatedRefs(bean, handlersFinder, ejbModule.getClassLoader());
      }

      private void processWebServiceClientHandlers(JndiConsumer consumer, AnnotationFinder finder, ClassLoader classLoader) throws OpenEJBException {
         if (!SystemInstance.get().hasProperty("openejb.geronimo")) {
            Set<String> processedClasses = new HashSet();
            Set<String> handlerClasses = new HashSet();

            do {
               handlerClasses.clear();

               for(ServiceRef serviceRef : consumer.getServiceRef()) {
                  HandlerChains chains = serviceRef.getAllHandlers();
                  if (chains != null) {
                     for(HandlerChain handlerChain : chains.getHandlerChain()) {
                        for(Handler handler : handlerChain.getHandler()) {
                           if (handler.getHandlerClass() != null) {
                              handlerClasses.add(AnnotationDeployer.realClassName(handler.getHandlerClass()));
                           }
                        }
                     }
                  }
               }

               handlerClasses.removeAll(processedClasses);
               if (!handlerClasses.isEmpty()) {
                  AnnotationFinder handlerAnnotationFinder = (AnnotationFinder)(finder != null ? finder.select(handlerClasses) : new FinderFactory.OpenEJBAnnotationFinder(new FinderFactory.DoLoadClassesArchive(classLoader, handlerClasses)));
                  this.buildAnnotatedRefs(consumer, handlerAnnotationFinder, classLoader);
                  processedClasses.addAll(handlerClasses);
               }
            } while(!handlerClasses.isEmpty());

         }
      }

      private List<String> getDeclaredClassPermissions(AssemblyDescriptor assemblyDescriptor, String ejbName) {
         List<MethodPermission> permissions = assemblyDescriptor.getMethodPermission();
         List<String> classPermissions = new ArrayList();

         for(MethodPermission permission : permissions) {
            for(org.apache.openejb.jee.Method method : permission.getMethod()) {
               if (method.getEjbName().equals(ejbName) && "*".equals(method.getMethodName())) {
                  String className = method.getClassName();
                  if (className == null) {
                     className = "*";
                  }

                  classPermissions.add(className);
               }
            }
         }

         return classPermissions;
      }

      private <A extends Annotation> void checkAttributes(AnnotationHandler<A> handler, String ejbName, EjbModule ejbModule, AnnotationFinder annotationFinder, String messageKey) {
         Map<String, List<MethodAttribute>> existingDeclarations = handler.getExistingDeclarations();
         int xml = 0;

         for(List<MethodAttribute> methodAttributes : existingDeclarations.values()) {
            xml += methodAttributes.size();
         }

         if (xml > 0) {
            ejbModule.getValidation().warn(ejbName, "xml." + messageKey, xml);
         }

         int ann = annotationFinder.findAnnotatedClasses(handler.getAnnotationClass()).size();
         ann += annotationFinder.findAnnotatedMethods(handler.getAnnotationClass()).size();
         if (ann > 0) {
            ejbModule.getValidation().warn(ejbName, "ann." + messageKey, ann);
         }

      }

      private <A extends Annotation> void processAttributes(AnnotationHandler<A> handler, Class<?> clazz, AnnotationFinder annotationFinder) {
         Map<String, List<MethodAttribute>> existingDeclarations = handler.getExistingDeclarations();
         Class<A> annotationClass = handler.getAnnotationClass();
         List<Annotated<Class<?>>> types = AnnotationDeployer.sortClasses(annotationFinder.findMetaAnnotatedClasses(annotationClass));
         if (!this.hasMethodAttribute("*", (Class)null, existingDeclarations)) {
            for(Annotated<Class<?>> type : types) {
               if (((Class)type.get()).isAssignableFrom(clazz) && !this.hasMethodAttribute("*", (Class)type.get(), existingDeclarations)) {
                  A attribute = type.getAnnotation(annotationClass);
                  if (attribute != null) {
                     handler.addClassLevelDeclaration(attribute, (Class)type.get());
                  }
               }
            }
         }

         for(Annotated<Method> method : annotationFinder.findMetaAnnotatedMethods(annotationClass)) {
            A attribute = method.getAnnotation(annotationClass);
            if (!existingDeclarations.containsKey(((Method)method.get()).getName())) {
               handler.addMethodLevelDeclaration(attribute, (Method)method.get());
            } else {
               for(MethodAttribute mtx : (List)existingDeclarations.get(((Method)method.get()).getName())) {
                  MethodParams methodParams = mtx.getMethodParams();
                  if (methodParams == null) {
                     handler.addMethodLevelDeclaration(attribute, (Method)method.get());
                  } else {
                     List<String> params1 = methodParams.getMethodParam();
                     String[] params2 = this.asStrings(((Method)method.get()).getParameterTypes());
                     if (params1.size() != params2.length) {
                        handler.addMethodLevelDeclaration(attribute, (Method)method.get());
                     } else {
                        for(int i = 0; i < params1.size(); ++i) {
                           String a = (String)params1.get(i);
                           String b = params2[i];
                           if (!a.equals(b)) {
                              handler.addMethodLevelDeclaration(attribute, (Method)method.get());
                              break;
                           }
                        }
                     }
                  }
               }
            }
         }

      }

      private boolean hasMethodAttribute(String methodName, Class clazz, Map<String, List<MethodAttribute>> map) {
         return this.getMethodAttribute(methodName, clazz, map) != null;
      }

      private MethodAttribute getMethodAttribute(String methodName, Class clazz, Map<String, List<MethodAttribute>> map) {
         List<MethodAttribute> methodAttributes = (List)map.get(methodName);
         if (methodAttributes == null) {
            return null;
         } else {
            for(MethodAttribute methodAttribute : methodAttributes) {
               String className = clazz != null ? clazz.getName() : "null";
               if (className.equals(methodAttribute.getClassName())) {
                  return methodAttribute;
               }
            }

            return null;
         }
      }

      private <A extends Annotation> A getInheritableAnnotation(Class clazz, Class<A> annotationClass) {
         if (clazz != null && !clazz.equals(Object.class)) {
            MetaAnnotatedClass meta = new MetaAnnotatedClass(clazz);
            Annotation annotation = meta.getAnnotation(annotationClass);
            return (A)(annotation != null ? annotation : this.getInheritableAnnotation(clazz.getSuperclass(), annotationClass));
         } else {
            return null;
         }
      }

      private AnnotationFinder createFinder(Class<?>... classes) {
         Set<Class<?>> parents = new HashSet();

         for(Class<?> clazz : classes) {
            parents.addAll(Classes.ancestors(clazz));
         }

         return (new AnnotationFinder(new ClassesArchive(parents))).enableMetaAnnotations();
      }

      private String[] asStrings(Class[] types) {
         List<String> names = new ArrayList();

         for(Class clazz : types) {
            names.add(clazz.getName());
         }

         return (String[])names.toArray(new String[names.size()]);
      }

      private <T> T getFirst(List<T> list) {
         return (T)(list.size() > 0 ? list.get(0) : null);
      }

      private boolean validateRemoteInterface(Class interfce, ValidationContext validation, String ejbName) {
         return this.isValidInterface(interfce, validation, ejbName, "Remote");
      }

      private boolean validateLocalInterface(Class interfce, ValidationContext validation, String ejbName) {
         return this.isValidInterface(interfce, validation, ejbName, "Local");
      }

      private boolean isValidInterface(Class interfce, ValidationContext validation, String ejbName, String annotationName) {
         if (!interfce.isInterface()) {
            validation.fail(ejbName, "ann.notAnInterface", annotationName, interfce.getName());
            return false;
         } else if (EJBHome.class.isAssignableFrom(interfce)) {
            validation.fail(ejbName, "ann.remoteOrLocal.ejbHome", annotationName, interfce.getName());
            return false;
         } else if (EJBObject.class.isAssignableFrom(interfce)) {
            validation.fail(ejbName, "ann.remoteOrLocal.ejbObject", annotationName, interfce.getName());
            return false;
         } else if (EJBLocalHome.class.isAssignableFrom(interfce)) {
            validation.fail(ejbName, "ann.remoteOrLocal.ejbLocalHome", annotationName, interfce.getName());
            return false;
         } else if (EJBLocalObject.class.isAssignableFrom(interfce)) {
            validation.fail(ejbName, "ann.remoteOrLocal.ejbLocalObject", annotationName, interfce.getName());
            return false;
         } else {
            return true;
         }
      }

      private static class BusinessInterfaces {
         private final Set<Class> local;
         private final Set<Class> remote;

         private BusinessInterfaces() {
            this.local = new LinkedHashSet();
            this.remote = new LinkedHashSet();
         }

         public void addLocals(Collection<String> names, ClassLoader loader) {
            this.add(loader, names, this.local);
         }

         public void addRemotes(Collection<String> names, ClassLoader loader) {
            this.add(loader, names, this.remote);
         }

         private void add(ClassLoader loader, Collection<String> names, Set<Class> classes) {
            for(String className : names) {
               try {
                  classes.add(loader.loadClass(AnnotationDeployer.realClassName(className)));
               } catch (Throwable var7) {
               }
            }

         }
      }

      public static class TransactionAttributeHandler implements AnnotationHandler<TransactionAttribute> {
         private final AssemblyDescriptor assemblyDescriptor;
         private final String ejbName;

         public TransactionAttributeHandler(AssemblyDescriptor assemblyDescriptor, String ejbName) {
            this.assemblyDescriptor = assemblyDescriptor;
            this.ejbName = ejbName;
         }

         public Map<String, List<MethodAttribute>> getExistingDeclarations() {
            return this.assemblyDescriptor.getMethodTransactionMap(this.ejbName);
         }

         public void addClassLevelDeclaration(TransactionAttribute attribute, Class type) {
            ContainerTransaction ctx = new ContainerTransaction(this.cast(attribute.value()), type.getName(), this.ejbName, "*");
            this.assemblyDescriptor.getContainerTransaction().add(ctx);
         }

         public void addMethodLevelDeclaration(TransactionAttribute attribute, Method method) {
            ContainerTransaction ctx = new ContainerTransaction(this.cast(attribute.value()), this.ejbName, method);
            this.assemblyDescriptor.getContainerTransaction().add(ctx);
         }

         public Class<TransactionAttribute> getAnnotationClass() {
            return TransactionAttribute.class;
         }

         private TransAttribute cast(TransactionAttributeType transactionAttributeType) {
            return TransAttribute.valueOf(transactionAttributeType.toString());
         }
      }

      private static class ConcurrentMethodHandler {
         protected final AssemblyDescriptor assemblyDescriptor;
         protected final SessionBean bean;
         protected final Map<Object, ContainerConcurrency> methods;

         public ConcurrentMethodHandler(AssemblyDescriptor assemblyDescriptor, SessionBean bean, Map<Object, ContainerConcurrency> methods) {
            this.assemblyDescriptor = assemblyDescriptor;
            this.bean = bean;
            this.methods = methods;
         }

         public Map<String, List<MethodAttribute>> getExistingDeclarations() {
            Map<String, List<MethodAttribute>> declarations = new HashMap();

            for(ConcurrentMethod method : this.bean.getConcurrentMethod()) {
               List<MethodAttribute> list = (List)declarations.get(method.getMethod().getMethodName());
               if (list == null) {
                  list = new ArrayList();
                  declarations.put(method.getMethod().getMethodName(), list);
               }

               list.add(new MethodAttribute((Object)null, this.bean.getEjbName(), method.getMethod()));
            }

            return declarations;
         }

         public ContainerConcurrency getContainerConcurrency(Method method) {
            ContainerConcurrency concurrency = (ContainerConcurrency)this.methods.get(method);
            if (concurrency == null) {
               concurrency = new ContainerConcurrency((ConcurrentLockType)null, this.bean.getEjbName(), method);
               this.methods.put(method, concurrency);
               this.assemblyDescriptor.getContainerConcurrency().add(concurrency);
            }

            return concurrency;
         }

         public ContainerConcurrency getContainerConcurrency(Class clazz) {
            ContainerConcurrency concurrency = (ContainerConcurrency)this.methods.get(clazz);
            if (concurrency == null) {
               concurrency = new ContainerConcurrency((ConcurrentLockType)null, clazz.getName(), this.bean.getEjbName(), "*");
               this.methods.put(clazz, concurrency);
               this.assemblyDescriptor.getContainerConcurrency().add(concurrency);
            }

            return concurrency;
         }

         protected Map<Object, ContainerConcurrency> getContainerConcurrency() {
            return this.methods;
         }
      }

      public static class LockHandler extends ConcurrentMethodHandler implements AnnotationHandler<Lock> {
         public LockHandler(AssemblyDescriptor assemblyDescriptor, SessionBean bean) {
            this(assemblyDescriptor, bean, new HashMap());
         }

         public LockHandler(AssemblyDescriptor assemblyDescriptor, SessionBean bean, Map<Object, ContainerConcurrency> methods) {
            super(assemblyDescriptor, bean, methods);
         }

         public void addClassLevelDeclaration(Lock attribute, Class type) {
            ContainerConcurrency concurrency = this.getContainerConcurrency(type);
            concurrency.setLock(this.toLock(attribute));
         }

         public void addMethodLevelDeclaration(Lock attribute, Method method) {
            ContainerConcurrency concurrency = this.getContainerConcurrency(method);
            concurrency.setLock(this.toLock(attribute));
         }

         private ConcurrentLockType toLock(Lock annotation) {
            if (LockType.READ.equals(annotation.value())) {
               return ConcurrentLockType.READ;
            } else if (LockType.WRITE.equals(annotation.value())) {
               return ConcurrentLockType.WRITE;
            } else {
               throw new IllegalArgumentException("Unknown lock annotation: " + annotation.value());
            }
         }

         public Class<Lock> getAnnotationClass() {
            return Lock.class;
         }
      }

      public static class AccessTimeoutHandler extends ConcurrentMethodHandler implements AnnotationHandler<AccessTimeout> {
         public AccessTimeoutHandler(AssemblyDescriptor assemblyDescriptor, SessionBean bean) {
            this(assemblyDescriptor, bean, new HashMap());
         }

         public AccessTimeoutHandler(AssemblyDescriptor assemblyDescriptor, SessionBean bean, Map<Object, ContainerConcurrency> methods) {
            super(assemblyDescriptor, bean, methods);
         }

         public void addClassLevelDeclaration(AccessTimeout attribute, Class type) {
            ContainerConcurrency concurrency = this.getContainerConcurrency(type);
            concurrency.setAccessTimeout(this.toTimeout(attribute));
         }

         public void addMethodLevelDeclaration(AccessTimeout attribute, Method method) {
            ContainerConcurrency concurrency = this.getContainerConcurrency(method);
            concurrency.setAccessTimeout(this.toTimeout(attribute));
         }

         private Timeout toTimeout(AccessTimeout annotation) {
            Timeout timeout = new Timeout();
            timeout.setTimeout(annotation.value());
            timeout.setUnit(annotation.unit());
            return timeout;
         }

         public Class<AccessTimeout> getAnnotationClass() {
            return AccessTimeout.class;
         }
      }

      public interface AnnotationHandler<A extends Annotation> {
         Class<A> getAnnotationClass();

         Map<String, List<MethodAttribute>> getExistingDeclarations();

         void addClassLevelDeclaration(A var1, Class var2);

         void addMethodLevelDeclaration(A var1, Method var2);
      }
   }

   public static class FilledMember implements Member {
      private final String name;
      private final Class<?> type;
      private final Class<?> declaringClass;

      public FilledMember(String name, Class<?> type, Class<?> declaringClass) {
         this.name = name;
         this.type = type;
         this.declaringClass = declaringClass;
      }

      public Class getDeclaringClass() {
         return this.declaringClass;
      }

      public String getName() {
         return this.name;
      }

      public Class getType() {
         return this.type;
      }

      public String toString() {
         return "FilledMember{name='" + this.name + '\'' + ", type=" + this.type.getName() + ", declaringClass=" + this.declaringClass.getName() + '}';
      }
   }

   public static class MethodMember implements Member {
      private final Method setter;

      public MethodMember(Method method) {
         this.setter = method;
      }

      public Class<?> getType() {
         return this.setter.getParameterTypes()[0];
      }

      public Class<?> getDeclaringClass() {
         return this.setter.getDeclaringClass();
      }

      public String getName() {
         StringBuilder name = new StringBuilder(this.setter.getName());
         name.delete(0, 3);
         name.setCharAt(0, Character.toLowerCase(name.charAt(0)));
         return name.toString();
      }

      public String toString() {
         return this.setter.toString();
      }
   }

   public static class FieldMember implements Member {
      private final Field field;

      public FieldMember(Field field) {
         this.field = field;
      }

      public Class<?> getType() {
         return this.field.getType();
      }

      public String toString() {
         return this.field.toString();
      }

      public Class<?> getDeclaringClass() {
         return this.field.getDeclaringClass();
      }

      public String getName() {
         return this.field.getName();
      }
   }

   public static class ProvidedJAXRSApplication extends Application {
   }

   public interface FolderDDMapper {
      File getDDFolder(File var1);
   }

   public interface Member {
      Class<?> getDeclaringClass();

      String getName();

      Class<?> getType();
   }
}
