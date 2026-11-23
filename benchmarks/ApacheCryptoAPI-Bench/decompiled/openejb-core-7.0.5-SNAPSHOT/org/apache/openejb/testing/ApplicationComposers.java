package org.apache.openejb.testing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.openejb.AppContext;
import org.apache.openejb.BeanContext;
import org.apache.openejb.InjectionProcessor;
import org.apache.openejb.OpenEJB;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.assembler.classic.OpenEjbConfiguration;
import org.apache.openejb.assembler.classic.WebAppBuilder;
import org.apache.openejb.cdi.CdiBuilder;
import org.apache.openejb.cdi.OptimizedLoaderService;
import org.apache.openejb.cdi.ScopeHelper;
import org.apache.openejb.cdi.ThreadSingletonService;
import org.apache.openejb.config.AppModule;
import org.apache.openejb.config.ConfigurationFactory;
import org.apache.openejb.config.ConnectorModule;
import org.apache.openejb.config.DeploymentLoader;
import org.apache.openejb.config.DeploymentModule;
import org.apache.openejb.config.DeploymentsResolver;
import org.apache.openejb.config.EjbModule;
import org.apache.openejb.config.FinderFactory;
import org.apache.openejb.config.PersistenceModule;
import org.apache.openejb.config.WebModule;
import org.apache.openejb.config.sys.JSonConfigReader;
import org.apache.openejb.config.sys.JaxbOpenejb;
import org.apache.openejb.config.sys.Openejb;
import org.apache.openejb.config.sys.Resources;
import org.apache.openejb.core.LocalInitialContextFactory;
import org.apache.openejb.core.Operation;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.core.ThreadContext;
import org.apache.openejb.core.WebContext;
import org.apache.openejb.core.ivm.naming.InitContextFactory;
import org.apache.openejb.injection.FallbackPropertyInjector;
import org.apache.openejb.jee.Beans;
import org.apache.openejb.jee.Connector;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.ManagedBean;
import org.apache.openejb.jee.NamedModule;
import org.apache.openejb.jee.TransactionType;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.jee.oejb3.PojoDeployment;
import org.apache.openejb.loader.FileUtils;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.JarLocation;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.rest.RESTResourceFinder;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.testing.rest.ContextProvider;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.NetworkUtil;
import org.apache.openejb.util.PropertyPlaceHolderHelper;
import org.apache.openejb.util.ServiceManagerProxy;
import org.apache.openejb.util.URLs;
import org.apache.openejb.util.reflection.Reflections;
import org.apache.openejb.web.LightweightWebAppBuilder;
import org.apache.webbeans.inject.OWBInjector;
import org.apache.webbeans.spi.ContextsService;
import org.apache.webbeans.spi.LoaderService;
import org.apache.webbeans.web.lifecycle.test.MockHttpSession;
import org.apache.webbeans.web.lifecycle.test.MockServletContext;
import org.apache.xbean.finder.AnnotationFinder;
import org.apache.xbean.finder.ClassFinder;
import org.apache.xbean.finder.IAnnotationFinder;
import org.apache.xbean.finder.ResourceFinder;
import org.apache.xbean.finder.UrlSet;
import org.apache.xbean.finder.archive.Archive;
import org.apache.xbean.finder.archive.ClassesArchive;
import org.apache.xbean.finder.archive.CompositeArchive;
import org.apache.xbean.finder.archive.FileArchive;
import org.apache.xbean.finder.archive.FilteredArchive;
import org.apache.xbean.finder.archive.JarArchive;
import org.apache.xbean.finder.filter.Filter;
import org.apache.xbean.finder.filter.Filters;
import org.xml.sax.InputSource;

public class ApplicationComposers {
   public static final String OPENEJB_APPLICATION_COMPOSER_CONTEXT = "openejb.application.composer.context";
   private static final Class[] MODULE_TYPES = new Class[]{IAnnotationFinder.class, ClassesArchive.class, AppModule.class, WebModule.class, EjbModule.class, org.apache.openejb.jee.Application.class, WebApp.class, EjbJar.class, EnterpriseBean.class, Persistence.class, PersistenceUnit.class, Connector.class, Beans.class, Class[].class, Class.class, Resources.class};
   private final Map<Object, ClassFinder> testClassFinders;
   private final Class<?> testClass;
   private ServiceManagerProxy serviceManager;
   private ClassLoader originalLoader;
   private AppInfo appInfo;
   private Assembler assembler;
   private AppContext appContext;
   private ThreadContext previous;
   private MockHttpSession session;
   private MockServletContext servletContext;
   private final Collection<String> globalJndiEntries;
   private final Collection<Runnable> beforeDestroyAfterRunnables;
   private final Collection<Runnable> afterRunnables;
   private Properties originalProperties;

   public ApplicationComposers(Object... modules) {
      this(modules[0].getClass(), modules);
   }

   public ApplicationComposers(Class<?> klass, Object... additionalModules) {
      this.globalJndiEntries = new ArrayList();
      this.beforeDestroyAfterRunnables = new ArrayList();
      this.afterRunnables = new ArrayList();
      this.testClass = klass;
      this.testClassFinders = new HashMap();
      this.testClassFinders.put(this, new ClassFinder(org.apache.openejb.util.Classes.ancestors(klass)));
      if (additionalModules != null) {
         for(Object o : additionalModules) {
            Class<?> aClass = o.getClass();
            if (aClass != klass) {
               this.testClassFinders.put(o, new ClassFinder(org.apache.openejb.util.Classes.ancestors(aClass)));
            }
         }
      }

      this.validate();
      this.assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);
   }

   protected boolean isContainer() {
      return true;
   }

   protected boolean isApplication() {
      return true;
   }

   private void validate() {
      List<Throwable> errors = new ArrayList();
      if (this.isContainer()) {
         Map<Object, List<Method>> annotatedConfigurationMethods = this.findAnnotatedMethods(new HashMap(), Configuration.class);
         int nbProp = 0;
         int nbOpenejb = 0;

         for(List<Method> list : annotatedConfigurationMethods.values()) {
            for(Method m : list) {
               Class<?> type = m.getReturnType();
               if (!Openejb.class.isAssignableFrom(type) && !String.class.equals(type)) {
                  if (Properties.class.isAssignableFrom(type)) {
                     ++nbProp;
                  }
               } else {
                  ++nbOpenejb;
               }
            }
         }

         if (nbProp > 1 || nbOpenejb > 1) {
            String gripe = "Test class should have no more than one @Configuration method by type (Openejb/String or Properties)";
            errors.add(new Exception("Test class should have no more than one @Configuration method by type (Openejb/String or Properties)"));
         }

         nbProp = 0;

         for(List<Method> m : this.findAnnotatedMethods(new HashMap(), org.apache.openejb.junit.MockInjector.class).values()) {
            nbProp += m.size();
         }

         for(List<Method> m : this.findAnnotatedMethods(new HashMap(), MockInjector.class).values()) {
            nbProp += m.size();
         }

         if (nbProp > 1) {
            errors.add(new Exception("Test class should have no more than one @MockInjector method"));
         }

         List<Method> components = new ArrayList();

         for(List<Method> l : this.findAnnotatedMethods(new HashMap(), Component.class).values()) {
            components.addAll(l);
         }

         for(List<Method> l : this.findAnnotatedMethods(new HashMap(), org.apache.openejb.junit.Component.class).values()) {
            components.addAll(l);
         }

         for(Method method : components) {
            if (method.getParameterTypes().length > 0) {
               errors.add(new Exception("@Component methods shouldn't take any parameters"));
            }
         }

         for(ClassFinder finder : this.testClassFinders.values()) {
            for(Field field : finder.findAnnotatedFields(RandomPort.class)) {
               Class<?> type = field.getType();
               if (Integer.TYPE != type && URL.class != type) {
                  throw new IllegalArgumentException("@RandomPort is only supported for int fields");
               }
            }
         }
      }

      if (this.isApplication()) {
         List<Method> descriptors = new ArrayList();

         for(List<Method> l : this.findAnnotatedMethods(new HashMap(), Descriptors.class).values()) {
            descriptors.addAll(l);
         }

         for(List<Method> l : this.findAnnotatedMethods(new HashMap(), org.apache.openejb.junit.Descriptors.class).values()) {
            descriptors.addAll(l);
         }

         for(Method method : descriptors) {
            Class<?> returnType = method.getReturnType();
            if (!returnType.equals(WebModule.class) && !returnType.equals(EjbModule.class) && !returnType.equals(WebApp.class) && !returnType.equals(EjbJar.class) && !returnType.equals(AppModule.class)) {
               errors.add(new Exception("@Descriptors can't be used on " + returnType.getName()));
            }
         }

         List<Method> classes = new ArrayList();

         for(List<Method> l : this.findAnnotatedMethods(new HashMap(), Classes.class).values()) {
            classes.addAll(l);
         }

         for(List<Method> l : this.findAnnotatedMethods(new HashMap(), org.apache.openejb.junit.Classes.class).values()) {
            classes.addAll(l);
         }

         for(Method method : classes) {
            Class<?> returnType = method.getReturnType();
            if (!returnType.equals(WebModule.class) && !returnType.equals(EjbModule.class) && !returnType.equals(WebApp.class) && !returnType.equals(EjbJar.class) && !EnterpriseBean.class.isAssignableFrom(returnType)) {
               errors.add(new Exception("@Classes can't be used on a method returning " + returnType));
            }
         }

         for(List<Method> l : this.findAnnotatedMethods(new HashMap(), Jars.class).values()) {
            for(Method method : l) {
               Class<?> returnType = method.getReturnType();
               if (!returnType.equals(WebModule.class) && !returnType.equals(EjbModule.class) && !returnType.equals(WebApp.class) && !returnType.equals(EjbJar.class) && !EnterpriseBean.class.isAssignableFrom(returnType)) {
                  errors.add(new Exception("@Classes can't be used on a method returning " + returnType));
               }
            }
         }

         int appModules = 0;
         int modules = 0;
         List<Method> moduleMethods = new ArrayList();

         for(List<Method> l : this.findAnnotatedMethods(new HashMap(), Module.class).values()) {
            moduleMethods.addAll(l);
         }

         for(List<Method> l : this.findAnnotatedMethods(new HashMap(), org.apache.openejb.junit.Module.class).values()) {
            moduleMethods.addAll(l);
         }

         for(Method method : moduleMethods) {
            ++modules;
            Class<?> type = method.getReturnType();
            if (org.apache.openejb.jee.Application.class.isAssignableFrom(type)) {
               ++appModules;
            } else if (!this.isValidModuleType(type, MODULE_TYPES)) {
               String gripe = "@Module method must return " + Join.join(" or ", (Object[])MODULE_TYPES).replaceAll("(class|interface) ", "");
               errors.add(new Exception(gripe));
            }
         }

         if (appModules > 1) {
            String gripe = "Test class should have no more than one @Module method that returns " + org.apache.openejb.jee.Application.class.getName();
            errors.add(new Exception(gripe));
         }

         if (modules < 1 && this.testClass.getAnnotation(Classes.class) == null && this.testClass.getAnnotation(Default.class) == null) {
            String gripe = "Test class should have at least one @Module method";
            errors.add(new Exception("Test class should have at least one @Module method"));
         }
      }

      if (!errors.isEmpty()) {
         throw new OpenEJBRuntimeException(errors.toString());
      }
   }

   private Map<Object, List<Method>> findAnnotatedMethods(Map<Object, List<Method>> map, Class<? extends Annotation> annotation) {
      for(Map.Entry<Object, ClassFinder> finder : this.testClassFinders.entrySet()) {
         Object key = finder.getKey();
         List<Method> newAnnotatedMethods = ((ClassFinder)finder.getValue()).findAnnotatedMethods(annotation);
         List<Method> annotatedMethods = (List)map.get(key);
         if (annotatedMethods == null) {
            map.put(key, newAnnotatedMethods);
         } else {
            for(Method m : newAnnotatedMethods) {
               if (!annotatedMethods.contains(m)) {
                  annotatedMethods.add(m);
               }
            }
         }
      }

      return map;
   }

   private boolean isValidModuleType(Class<?> type, Class<?>[] moduleTypes) {
      for(Class<?> moduleType : moduleTypes) {
         if (moduleType.isAssignableFrom(type)) {
            return true;
         }
      }

      return false;
   }

   public void before(Object inputTestInstance) throws Exception {
      this.fixFakeClassFinder(inputTestInstance);
      this.startContainer(inputTestInstance);
      this.servletContext = new MockServletContext();
      this.session = new MockHttpSession();
      this.deployApp(inputTestInstance);
   }

   public void deployApp(Object inputTestInstance) throws Exception {
      ClassFinder testClassFinder = this.fixFakeClassFinder(inputTestInstance);
      ClassLoader loader = this.testClass.getClassLoader();
      AppModule appModule = new AppModule(loader, this.testClass.getSimpleName());
      EjbJar ejbJar = new EjbJar();
      OpenejbJar openejbJar = new OpenejbJar();
      ManagedBean testBean = (ManagedBean)ejbJar.addEnterpriseBean(new ManagedBean(this.testClass.getSimpleName(), this.testClass.getName(), true));
      testBean.localBean();
      testBean.setTransactionType(TransactionType.BEAN);
      EjbDeployment ejbDeployment = openejbJar.addEjbDeployment(testBean);
      ejbDeployment.setDeploymentId(this.testClass.getName());
      EjbModule ejbModule = new EjbModule(ejbJar, openejbJar);
      ejbModule.getProperties().setProperty("openejb.cdi.activated", "false");
      FinderFactory.OpenEJBAnnotationFinder finder = new FinderFactory.OpenEJBAnnotationFinder(new ClassesArchive(org.apache.openejb.util.Classes.ancestors(this.testClass)));
      ejbModule.setFinder(finder);
      if (finder.findMetaAnnotatedFields(Inject.class).size() + finder.findMetaAnnotatedMethods(Inject.class).size() > 0) {
         ejbModule.setBeans(new Beans());
      }

      appModule.getEjbModules().add(ejbModule);
      Map<String, URL> additionalDescriptors = descriptorsToMap(this.testClass.getAnnotation(org.apache.openejb.junit.Descriptors.class));
      Map<String, URL> additionalDescriptorsNew = descriptorsToMap(this.testClass.getAnnotation(Descriptors.class));
      additionalDescriptors.putAll(additionalDescriptorsNew);
      org.apache.openejb.jee.Application application = null;
      int webModulesNb = 0;
      Jars globalJarsAnnotation = (Jars)this.testClass.getAnnotation(Jars.class);
      int moduleNumber = 0;
      int notBusinessModuleNumber = 0;
      Map<Object, List<Method>> moduleMethods = new HashMap();
      this.findAnnotatedMethods(moduleMethods, Module.class);
      this.findAnnotatedMethods(moduleMethods, org.apache.openejb.junit.Module.class);

      for(Map.Entry<Object, List<Method>> methods : moduleMethods.entrySet()) {
         moduleNumber += ((List)methods.getValue()).size();

         for(Method method : (List)methods.getValue()) {
            Object obj = method.invoke(methods.getKey());
            Jars jarsAnnotation = (Jars)method.getAnnotation(Jars.class);
            Classes classesAnnotation = (Classes)method.getAnnotation(Classes.class);
            org.apache.openejb.junit.Classes classesAnnotationOld = (org.apache.openejb.junit.Classes)method.getAnnotation(org.apache.openejb.junit.Classes.class);
            boolean defaultConfig = method.getAnnotation(Default.class) != null;
            Class<?>[] classes = null;
            String[] excludes = null;
            Class<?>[] cdiInterceptors = null;
            Class<?>[] cdiAlternatives = null;
            Class<?>[] cdiStereotypes = null;
            Class<?>[] cdiDecorators = null;
            boolean cdi = false;
            boolean innerClassesAsBean = false;
            if (classesAnnotation != null) {
               classes = classesAnnotation.value();
               excludes = classesAnnotation.excludes();
               innerClassesAsBean = classesAnnotation.innerClassesAsBean();
               cdiInterceptors = classesAnnotation.cdiInterceptors();
               cdiDecorators = classesAnnotation.cdiDecorators();
               cdiAlternatives = classesAnnotation.cdiAlternatives();
               cdiStereotypes = classesAnnotation.cdiStereotypes();
               cdi = this.isCdi(classesAnnotation.cdi(), cdiInterceptors, cdiAlternatives, cdiStereotypes, cdiDecorators);
            } else if (classesAnnotationOld != null) {
               classes = classesAnnotationOld.value();
            }

            if (obj instanceof WebApp) {
               WebApp webApp = (WebApp)WebApp.class.cast(obj);
               if (webApp.getContextRoot() == null && classesAnnotation != null) {
                  webApp.contextRoot(classesAnnotation.context());
               }

               ++webModulesNb;
               this.addWebApp(appModule, testBean, additionalDescriptors, (Descriptors)method.getAnnotation(Descriptors.class), (JaxrsProviders)method.getAnnotation(JaxrsProviders.class), webApp, globalJarsAnnotation, jarsAnnotation, classes, excludes, cdiInterceptors, cdiAlternatives, cdiDecorators, cdiStereotypes, cdi, innerClassesAsBean, defaultConfig);
            } else if (obj instanceof WebModule) {
               ++webModulesNb;
               WebModule webModule = (WebModule)obj;
               webModule.getAltDDs().putAll(additionalDescriptors);
               webModule.getAltDDs().putAll(descriptorsToMap(method.getAnnotation(Descriptors.class)));
               EjbModule ejbModule = DeploymentLoader.addWebModule(webModule, appModule);
               ejbModule.getProperties().put("openejb.cdi.filter.classloader", "false");
               if (cdi) {
                  ejbModule.setBeans(this.beans(new Beans(), cdiDecorators, cdiInterceptors, cdiAlternatives, cdiStereotypes));
               }

               Collection<File> files = findFiles(jarsAnnotation);
               if (defaultConfig) {
                  (files == null ? (files = new LinkedList()) : files).add(JarLocation.jarLocation(this.testClass));
               }

               webModule.setFinder(finderFromClasses(webModule, classes, files, excludes));
               ejbModule.setFinder(webModule.getFinder());
            } else if (obj instanceof EjbModule) {
               EjbModule ejbModule = (EjbModule)obj;
               ejbModule.getAltDDs().putAll(additionalDescriptors);
               ejbModule.getAltDDs().putAll(descriptorsToMap(method.getAnnotation(Descriptors.class)));
               ejbModule.initAppModule(appModule);
               appModule.getEjbModules().add(ejbModule);
               if (cdi) {
                  ejbModule.setBeans(this.beans(new Beans(), cdiDecorators, cdiInterceptors, cdiAlternatives, cdiStereotypes));
               }

               Collection<File> files = findFiles(jarsAnnotation);
               if (defaultConfig) {
                  (files == null ? (files = new LinkedList()) : files).add(JarLocation.jarLocation(this.testClass));
               }

               ejbModule.setFinder(finderFromClasses(ejbModule, classes, files, excludes));
            } else if (obj instanceof EjbJar) {
               EjbJar ejbJar = (EjbJar)obj;
               this.setId(ejbJar, (Method)method);
               EjbModule ejbModule = new EjbModule(ejbJar);
               ejbModule.getAltDDs().putAll(additionalDescriptors);
               ejbModule.getAltDDs().putAll(descriptorsToMap(method.getAnnotation(Descriptors.class)));
               appModule.getEjbModules().add(ejbModule);
               if (cdi) {
                  ejbModule.setBeans(this.beans(new Beans(), cdiDecorators, cdiInterceptors, cdiAlternatives, cdiStereotypes));
               }

               Collection<File> files = findFiles(jarsAnnotation);
               if (defaultConfig) {
                  (files == null ? (files = new LinkedList()) : files).add(JarLocation.jarLocation(this.testClass));
               }

               ejbModule.setFinder(finderFromClasses(ejbModule, classes, files, excludes));
            } else if (obj instanceof EnterpriseBean) {
               EnterpriseBean bean = (EnterpriseBean)obj;
               EjbJar ejbJar = new EjbJar(method.getName());
               ejbJar.addEnterpriseBean(bean);
               EjbModule ejbModule = new EjbModule(ejbJar);
               Beans beans = new Beans();
               beans.addManagedClass(bean.getEjbClass());
               ejbModule.setBeans(beans);
               appModule.getEjbModules().add(ejbModule);
               if (cdi) {
                  ejbModule.setBeans(this.beans(new Beans(), cdiDecorators, cdiInterceptors, cdiAlternatives, cdiStereotypes));
               }

               Collection<File> files = findFiles(jarsAnnotation);
               if (defaultConfig) {
                  (files == null ? (files = new LinkedList()) : files).add(JarLocation.jarLocation(this.testClass));
               }

               ejbModule.setFinder(finderFromClasses(ejbModule, classes, files, excludes));
            } else if (obj instanceof org.apache.openejb.jee.Application) {
               application = (org.apache.openejb.jee.Application)obj;
               this.setId(application, (Method)method);
            } else if (obj instanceof Connector) {
               Connector connector = (Connector)obj;
               this.setId(connector, (Method)method);
               appModule.getConnectorModules().add(new ConnectorModule(connector));
            } else if (obj instanceof Persistence) {
               Persistence persistence = (Persistence)obj;
               appModule.addPersistenceModule(new PersistenceModule(appModule, implicitRootUrl((PersistenceRootUrl)method.getAnnotation(PersistenceRootUrl.class)), persistence));
               ++notBusinessModuleNumber;
            } else if (obj instanceof PersistenceUnit) {
               PersistenceUnit unit = (PersistenceUnit)obj;
               appModule.addPersistenceModule(new PersistenceModule(appModule, implicitRootUrl((PersistenceRootUrl)method.getAnnotation(PersistenceRootUrl.class)), new Persistence(new PersistenceUnit[]{unit})));
               ++notBusinessModuleNumber;
            } else if (obj instanceof Beans) {
               Beans beans = (Beans)obj;
               EjbModule ejbModule = new EjbModule(new EjbJar(method.getName()));
               ejbModule.setBeans(beans);
               appModule.getEjbModules().add(ejbModule);
               if (cdi) {
                  ejbModule.setBeans(this.beans(beans, cdiDecorators, cdiInterceptors, cdiAlternatives, cdiStereotypes));
               }

               Collection<File> files = findFiles(jarsAnnotation);
               if (defaultConfig) {
                  (files == null ? (files = new LinkedList()) : files).add(JarLocation.jarLocation(this.testClass));
               }

               ejbModule.setFinder(finderFromClasses(ejbModule, classes, files, excludes));
            } else if (obj instanceof Class[]) {
               Class[] beans = (Class[])obj;
               EjbModule ejbModule = new EjbModule(new EjbJar(method.getName()));
               ejbModule.setFinder((new AnnotationFinder(new ClassesArchive(beans))).link());
               ejbModule.setBeans(new Beans());
               appModule.getEjbModules().add(ejbModule);
            } else if (obj instanceof Class) {
               Class bean = (Class)obj;
               EjbModule ejbModule = new EjbModule(new EjbJar(method.getName()));
               ejbModule.setFinder((new AnnotationFinder(new ClassesArchive(new Class[]{bean}))).link());
               ejbModule.setBeans(new Beans());
               appModule.getEjbModules().add(ejbModule);
            } else if (obj instanceof IAnnotationFinder) {
               EjbModule ejbModule = new EjbModule(new EjbJar(method.getName()));
               ejbModule.setFinder((IAnnotationFinder)obj);
               ejbModule.setBeans(new Beans());
               appModule.getEjbModules().add(ejbModule);
            } else if (obj instanceof ClassesArchive) {
               EjbModule ejbModule = new EjbModule(new EjbJar(method.getName()));
               ejbModule.setFinder((new AnnotationFinder((Archive)obj)).link());
               ejbModule.setBeans(new Beans());
               appModule.getEjbModules().add(ejbModule);
            } else if (obj instanceof Resources) {
               Resources asResources = (Resources)Resources.class.cast(obj);
               appModule.getResources().addAll(asResources.getResource());
               appModule.getContainers().addAll(asResources.getContainer());
               ++notBusinessModuleNumber;
            } else if (obj instanceof AppModule) {
               AppModule module = (AppModule)obj;
               module.getAltDDs().putAll(additionalDescriptors);
               module.getAltDDs().putAll(descriptorsToMap(method.getAnnotation(Descriptors.class)));
               if (module.getWebModules().size() > 0) {
                  ++webModulesNb;
               }

               appModule.getEjbModules().addAll(module.getEjbModules());
               appModule.getPersistenceModules().addAll(module.getPersistenceModules());
               appModule.getAdditionalLibMbeans().addAll(module.getAdditionalLibMbeans());
               appModule.getWebModules().addAll(module.getWebModules());
               appModule.getConnectorModules().addAll(module.getConnectorModules());
               appModule.getResources().addAll(module.getResources());
               appModule.getServices().addAll(module.getServices());
               appModule.getPojoConfigurations().putAll(module.getPojoConfigurations());
               appModule.getAdditionalLibraries().addAll(module.getAdditionalLibraries());
               appModule.getAltDDs().putAll(module.getAltDDs());
               appModule.getProperties().putAll(module.getProperties());
            } else {
               --moduleNumber;
            }
         }
      }

      Classes classClasses = (Classes)this.testClass.getAnnotation(Classes.class);
      if (classClasses != null) {
         WebApp webapp = new WebApp();
         webapp.setContextRoot(classClasses.context());
         this.addWebApp(appModule, testBean, additionalDescriptors, (Descriptors)null, (JaxrsProviders)null, webapp, globalJarsAnnotation, (Jars)null, classClasses.value(), classClasses.excludes(), classClasses.cdiInterceptors(), classClasses.cdiAlternatives(), classClasses.cdiDecorators(), classClasses.cdiStereotypes(), classClasses.cdi(), classClasses.innerClassesAsBean(), this.testClass.getAnnotation(Default.class) != null);
         ++webModulesNb;
         ++moduleNumber;
      }

      if (application != null) {
         AppModule newModule = new AppModule(appModule.getClassLoader(), appModule.getModuleId(), application, false);
         newModule.getClientModules().addAll(appModule.getClientModules());
         newModule.addPersistenceModules(appModule.getPersistenceModules());
         newModule.getEjbModules().addAll(appModule.getEjbModules());
         newModule.getConnectorModules().addAll(appModule.getConnectorModules());
         appModule = newModule;
      }

      for(Map.Entry<Object, List<Method>> method : this.findAnnotatedMethods(new HashMap(), ApplicationConfiguration.class).entrySet()) {
         for(Method m : (List)method.getValue()) {
            Object o = m.invoke(method.getKey());
            if (Properties.class.isInstance(o)) {
               appModule.getProperties().putAll((Map)Properties.class.cast(o));
            }
         }
      }

      for(EjbModule ejb : appModule.getEjbModules()) {
         EnterpriseBean[] enterpriseBeans = ejb.getEjbJar().getEnterpriseBeans();
         Beans beans = ejb.getBeans();
         if (beans != null && ejb.getEjbJar() != null) {
            for(EnterpriseBean bean : enterpriseBeans) {
               boolean found = false;

               for(List<String> mc : beans.getManagedClasses().values()) {
                  if (mc.contains(bean.getEjbClass())) {
                     found = true;
                     break;
                  }
               }

               if (!found) {
                  beans.addManagedClass(bean.getEjbClass());
               }
            }
         }
      }

      if (moduleNumber - notBusinessModuleNumber == 1 && webModulesNb == 1) {
         appModule.setStandloneWebModule();
      }

      if (webModulesNb > 0 && SystemInstance.get().getComponent(WebAppBuilder.class) == null) {
         SystemInstance.get().setComponent(WebAppBuilder.class, new LightweightWebAppBuilder());
      }

      Context jndiContext = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext();

      for(EnvEntry entry : testBean.getEnvEntry()) {
         String name = entry.getName();
         String jndi;
         if (!name.startsWith("java:") && !name.startsWith("comp/env")) {
            jndi = "java:comp/env/" + name;
         } else {
            jndi = name;
         }

         jndiContext.bind(jndi, entry.getEnvEntryValue());
      }

      this.appInfo = ((ConfigurationFactory)SystemInstance.get().getComponent(ConfigurationFactory.class)).configureApplication(appModule);
      this.appContext = this.assembler.createApplication(this.appInfo);
      if (this.mockCdiContexts() && this.appContext.getWebBeansContext() != null) {
         ScopeHelper.startContexts(this.appContext.getWebBeansContext().getContextsService(), this.servletContext, this.session);
      }

      BeanContext context = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getBeanContext(this.testClass.getName());
      this.enrich(inputTestInstance, context);
      JavaSecurityManagers.setSystemProperty("java.naming.factory.initial", InitContextFactory.class.getName());
      JavaSecurityManagers.setSystemProperty("openejb.application.composer.context", this.appContext.getGlobalJndiContext());
      List<Field> fields = new ArrayList(testClassFinder.findAnnotatedFields(AppResource.class));
      fields.addAll(testClassFinder.findAnnotatedFields(org.apache.openejb.junit.AppResource.class));

      for(Field field : fields) {
         Class<?> type = field.getType();
         if (AppModule.class.isAssignableFrom(type)) {
            field.setAccessible(true);
            field.set(inputTestInstance, appModule);
         } else if (Context.class.isAssignableFrom(type)) {
            field.setAccessible(true);
            field.set(inputTestInstance, new InitialContext(new Properties() {
               {
                  this.setProperty("java.naming.factory.initial", LocalInitialContextFactory.class.getName());
               }
            }));
         } else if (ApplicationComposers.class.isAssignableFrom(type)) {
            field.setAccessible(true);
            field.set(inputTestInstance, this);
         } else {
            if (!ContextProvider.class.isAssignableFrom(type)) {
               throw new IllegalArgumentException("can't find value for type " + type.getName());
            }

            RESTResourceFinder finder = (RESTResourceFinder)SystemInstance.get().getComponent(RESTResourceFinder.class);
            if (finder == null || !ContextProvider.class.isInstance(finder)) {
               finder = new ContextProvider(finder);
               SystemInstance.get().setComponent(RESTResourceFinder.class, finder);
            }

            field.setAccessible(true);
            field.set(inputTestInstance, finder);
         }
      }

      this.previous = ThreadContext.enter(new ThreadContext(context, (Object)null, Operation.BUSINESS));
      this.testClassFinders.put(this, testClassFinder);
   }

   private ClassFinder fixFakeClassFinder(final Object inputTestInstance) {
      ClassFinder testClassFinder = (ClassFinder)this.testClassFinders.get(inputTestInstance);
      if (testClassFinder == null) {
         ClassFinder remove = (ClassFinder)this.testClassFinders.remove(this);
         if (remove != null) {
            this.testClassFinders.put(inputTestInstance, remove);
            testClassFinder = remove;
            this.afterRunnables.add(new Runnable() {
               public void run() {
                  ClassFinder classFinder = (ClassFinder)ApplicationComposers.this.testClassFinders.remove(inputTestInstance);
                  if (classFinder != null) {
                     ApplicationComposers.this.testClassFinders.put(ApplicationComposers.this, classFinder);
                  }

               }
            });
         }
      }

      return testClassFinder;
   }

   private boolean isCdi(boolean cdi, Class<?>[] cdiInterceptors, Class<?>[] cdiAlternatives, Class<?>[] cdiStereotypes, Class<?>[] cdiDecorators) {
      return cdi || this.isNotNullOrEmpty(cdiAlternatives) || this.isNotNullOrEmpty(cdiDecorators) || this.isNotNullOrEmpty(cdiInterceptors) || this.isNotNullOrEmpty(cdiStereotypes);
   }

   private boolean isNotNullOrEmpty(Class<?>[] ca) {
      return null != ca && ca.length > 0;
   }

   protected boolean mockCdiContexts() {
      return "true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.testing.start-cdi-contexts", "true"));
   }

   private void addWebApp(AppModule appModule, ManagedBean testBean, Map<String, URL> additionalDescriptors, Descriptors descriptors, JaxrsProviders providers, WebApp webapp, Jars globalJarsAnnotation, Jars jarsAnnotation, Class<?>[] cdiClasses, String[] excludes, Class<?>[] cdiInterceptors, Class<?>[] cdiAlternatives, Class<?>[] cdiDecorators, Class<?>[] cdiStereotypes, boolean cdi, boolean innerClassesAsBean, boolean autoConfig) throws OpenEJBException {
      String root = webapp.getContextRoot();
      if (root == null) {
         root = "/openejb";
      }

      testBean.getEnvEntry().addAll(webapp.getEnvEntry());
      WebModule webModule = new WebModule(webapp, root, Thread.currentThread().getContextClassLoader(), "", root);
      File thisJar;
      if (autoConfig) {
         thisJar = JarLocation.jarLocation(this.testClass);

         try {
            webModule.getAltDDs().putAll(DeploymentLoader.mapDescriptors(new ResourceFinder("", webModule.getClassLoader(), new URL[]{thisJar.toURI().toURL()})));
            webModule.getAltDDs().putAll(DeploymentLoader.getWebDescriptors(new File(thisJar.getParentFile().getParentFile(), "src/main/webapp")));
         } catch (IOException e) {
            throw new IllegalStateException(e);
         }
      } else {
         thisJar = null;
      }

      webModule.getAltDDs().putAll(additionalDescriptors);

      for(Descriptors d : Arrays.asList((Descriptors)this.testClass.getAnnotation(Descriptors.class), descriptors)) {
         if (d != null) {
            webModule.getAltDDs().putAll(descriptorsToMap(d));
         }
      }

      EjbModule ejbModule = DeploymentLoader.addWebModule(webModule, appModule);
      ejbModule.getProperties().put("openejb.cdi.filter.classloader", "false");
      if (this.isCdi(cdi, cdiInterceptors, cdiAlternatives, cdiStereotypes, cdiDecorators)) {
         ejbModule.setBeans(this.beans(new Beans(), cdiDecorators, cdiInterceptors, cdiAlternatives, cdiStereotypes));
      }

      Class<?>[] classes = cdiClasses;
      Class<?>[] providersClasses = providers == null ? null : providers.value();

      for(JaxrsProviders p : Arrays.asList((JaxrsProviders)this.testClass.getAnnotation(JaxrsProviders.class), providers)) {
         if (p != null) {
            if (classes == null) {
               classes = p.value();
            } else {
               Collection<Class<?>> newClasses = new ArrayList(Arrays.asList(classes));
               newClasses.addAll(Arrays.asList(p.value()));
               classes = (Class[])newClasses.toArray(new Class[newClasses.size()]);
            }
         }
      }

      if (innerClassesAsBean) {
         Collection<Class<?>> inners = new LinkedList();

         for(Class<?> clazz : this.testClass.getClasses()) {
            int modifiers = clazz.getModifiers();

            try {
               if (Modifier.isPublic(modifiers) && Modifier.isStatic(modifiers) && clazz.getConstructor() != null) {
                  inners.add(clazz);
               }
            } catch (NoSuchMethodException var31) {
            }
         }

         if (!inners.isEmpty()) {
            Collection<Class<?>> newClasses = new ArrayList(Arrays.asList(classes));
            newClasses.addAll(inners);
            classes = (Class[])newClasses.toArray(new Class[newClasses.size()]);
         }
      }

      Collection<File> libs = null;

      for(Jars jars : Arrays.asList(jarsAnnotation, globalJarsAnnotation)) {
         Collection<File> files = findFiles(jars);
         if (files != null) {
            if (libs == null) {
               libs = new LinkedList();
            }

            libs.addAll(files);
         }
      }

      if (autoConfig) {
         if (libs == null) {
            libs = new LinkedList();
         }

         libs.add(thisJar);
         if ("test-classes".equals(thisJar.getName()) && "target".equals(thisJar.getParentFile().getName())) {
            File mainClasses = new File(thisJar.getParentFile(), "classes");
            if (mainClasses.exists()) {
               libs.add(mainClasses);
            }
         } else if ("test".equals(thisJar.getName()) && "classes".equals(thisJar.getParentFile().getName())) {
            File mainClasses = new File(thisJar.getParentFile(), "main");
            if (mainClasses.exists()) {
               libs.add(mainClasses);
            }
         }
      }

      IAnnotationFinder finder = finderFromClasses(webModule, classes, libs, excludes);
      webModule.setFinder(finder);
      ejbModule.setFinder(webModule.getFinder());
      if (providersClasses != null) {
         OpenejbJar openejbJar = ejbModule.getOpenejbJar();
         if (openejbJar == null) {
            openejbJar = new OpenejbJar();
            ejbModule.setOpenejbJar(openejbJar);
         }

         PojoDeployment pojoDeployment = new PojoDeployment();
         pojoDeployment.setClassName(providers.applicationName());
         pojoDeployment.getProperties().setProperty("cxf.jaxrs.providers", Join.join(",", (Object[])providersClasses).replace("class ", ""));
         pojoDeployment.getProperties().setProperty("cxf.jaxrs.skip-provider-scanning", "true");
         openejbJar.getPojoDeployment().add(pojoDeployment);
      }

   }

   private void enrich(Object inputTestInstance, BeanContext context) throws OpenEJBException {
      if (context != null) {
         ThreadContext callContext = new ThreadContext(context, (Object)null, Operation.INJECTION);
         ThreadContext oldContext = ThreadContext.enter(callContext);

         try {
            InjectionProcessor processor = new InjectionProcessor(inputTestInstance, context.getInjections(), context.getJndiContext());
            processor.createInstance();
            Throwable error = null;

            try {
               if (this.appContext.getBeanManager() != null) {
                  OWBInjector.inject(this.appContext.getBeanManager(), inputTestInstance, (CreationalContext)null);
               }
            } catch (Throwable t) {
               error = t;
            }

            for(WebContext web : this.appContext.getWebContexts()) {
               if (web.getWebBeansContext() != null) {
                  try {
                     OWBInjector.inject(web.getWebBeansContext().getBeanManagerImpl(), inputTestInstance, (CreationalContext)null);
                     error = null;
                     break;
                  } catch (Throwable t) {
                     if (error == null) {
                        error = t;
                     }
                  }
               }
            }

            if (error != null) {
               error.printStackTrace();
            }
         } finally {
            ThreadContext.exit(oldContext);
         }

      }
   }

   public static Collection<File> findFiles(Jars jarsAnnotation) {
      if (jarsAnnotation == null) {
         return null;
      } else {
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
         List<URL> classpathAppsUrls = new ArrayList(8);
         if (jarsAnnotation.excludeDefaults()) {
            DeploymentsResolver.loadFromClasspath((FileUtils)null, classpathAppsUrls, classLoader);
         } else {
            UrlSet urlSet;
            try {
               urlSet = new UrlSet(classLoader);
               urlSet = URLs.cullSystemJars(urlSet);
            } catch (IOException e) {
               throw new IllegalStateException(e);
            }

            classpathAppsUrls.addAll(urlSet.getUrls());
         }

         String[] value = jarsAnnotation.value();
         Collection<File> files = new ArrayList(value.length);

         for(String v : value) {
            int size = files.size();

            for(URL path : classpathAppsUrls) {
               File file = URLs.toFile(path);
               if (file.getName().startsWith(v) && file.getName().endsWith(".jar")) {
                  files.add(file);
               } else if ("classes".equals(file.getName()) && "target".equals(file.getParentFile().getName()) && file.getParentFile().getParentFile().getName().startsWith(v)) {
                  files.add(file);
               }
            }

            if (size == files.size()) {
               throw new IllegalArgumentException(v + " not found in classpath");
            }
         }

         return files;
      }
   }

   private Beans beans(Beans beans, Class<?>[] cdiDecorators, Class<?>[] cdiInterceptors, Class<?>[] cdiAlternatives, Class<?>[] cdiStereotypes) {
      if (cdiDecorators != null) {
         for(Class<?> clazz : cdiDecorators) {
            beans.addDecorator(clazz);
         }
      }

      if (cdiInterceptors != null) {
         for(Class<?> clazz : cdiInterceptors) {
            beans.addInterceptor(clazz);
         }
      }

      if (cdiAlternatives != null) {
         for(Class<?> clazz : cdiAlternatives) {
            beans.addAlternativeClass(clazz);
         }
      }

      if (cdiStereotypes != null) {
         for(Class<?> clazz : cdiStereotypes) {
            beans.addAlternativeStereotype(clazz);
         }
      }

      return beans;
   }

   private void setComponent(Object testInstance, Method method) throws IllegalAccessException, InvocationTargetException, InstantiationException {
      Object value = method.invoke(testInstance);
      if (value instanceof Class) {
         value = ((Class)value).newInstance();
      }

      Class<?> key = method.getReturnType();
      if (!key.isInstance(value)) {
         throw new OpenEJBRuntimeException(value + " is not an instance of " + key.getName());
      } else {
         SystemInstance.get().setComponent(key, value);
      }
   }

   public <T> T evaluate(Object testInstance, Callable<T> next) throws Exception {
      this.before(testInstance);

      Object var3;
      try {
         var3 = next.call();
      } finally {
         ThreadContext.exit(this.previous);
         this.after();
      }

      return (T)var3;
   }

   public void evaluate(Object testInstance, final Runnable next) throws Exception {
      this.evaluate(testInstance, new Callable<Void>() {
         public Void call() throws Exception {
            next.run();
            return null;
         }
      });
   }

   public void after() throws Exception {
      try {
         this.runAll(this.beforeDestroyAfterRunnables);
         if (this.assembler != null) {
            this.stopApplication();
         }

         if (this.serviceManager != null) {
            try {
               this.serviceManager.stop();
            } catch (RuntimeException var5) {
            }
         }

         OpenEJB.destroy();
      } finally {
         this.runAll(this.afterRunnables);
         if (this.originalLoader != null) {
            Thread.currentThread().setContextClassLoader(this.originalLoader);
         }

         if (this.originalProperties != null) {
            System.setProperties(this.originalProperties);
         }

      }

   }

   public void stopApplication() throws NamingException {
      if (this.appContext != null && this.appContext.getWebBeansContext() != null) {
         ContextsService contextsService = this.appContext.getWebBeansContext().getContextsService();
         contextsService.endContext(SessionScoped.class, this.session);
         contextsService.endContext(RequestScoped.class, (Object)null);
      }

      if (this.appInfo != null) {
         try {
            this.assembler.destroyApplication(this.appInfo.path);
         } catch (Exception var6) {
         }
      }

      ContainerSystem component = (ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class);
      if (null != component) {
         Context context = component.getJNDIContext();

         for(String entry : this.globalJndiEntries) {
            context.unbind(entry);
         }
      }

      this.globalJndiEntries.clear();
      if (this.mockCdiContexts() && this.appContext != null && this.appContext.getWebBeansContext() != null) {
         try {
            ScopeHelper.stopContexts(this.appContext.getWebBeansContext().getContextsService(), this.servletContext, this.session);
         } catch (Exception var5) {
         }
      }

   }

   private void runAll(Collection<Runnable> runnables) {
      for(Runnable r : runnables) {
         try {
            r.run();
         } catch (Exception var5) {
         }
      }

      runnables.clear();
   }

   private <M extends NamedModule> M setId(M module, Method method) {
      return (M)this.setId(module, method.getName());
   }

   private <M extends NamedModule> M setId(M module, String name) {
      if (module.getModuleName() != null) {
         return module;
      } else if (module.getId() != null) {
         return module;
      } else {
         module.setId(name);
         return module;
      }
   }

   private static String implicitRootUrl(PersistenceRootUrl annotation) {
      if (annotation != null) {
         return annotation.value();
      } else {
         ResourceFinder finder = new ResourceFinder("", Thread.currentThread().getContextClassLoader());

         try {
            URL url = (URL)DeploymentLoader.altDDSources(DeploymentLoader.mapDescriptors(finder), false).get("persistence.xml");
            if (url == null) {
               return "";
            } else {
               File file = URLs.toFile(url);
               String filename = file.getName();
               if (filename.endsWith("persistence.xml")) {
                  String parent = file.getParentFile().getName();
                  return parent.equalsIgnoreCase("META-INF") ? file.getParentFile().getParentFile().getAbsolutePath() : file.getParentFile().getAbsolutePath();
               } else {
                  return filename.endsWith(".jar") ? file.toURI().toURL().toExternalForm() : url.toExternalForm();
               }
            }
         } catch (IOException var6) {
            return "";
         }
      }
   }

   private static Map<String, URL> descriptorsToMap(Object descriptors) {
      if (descriptors == null) {
         return new HashMap();
      } else {
         Map<String, URL> dds = new HashMap();
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         if (descriptors instanceof Descriptors) {
            for(Descriptor descriptor : ((Descriptors)descriptors).value()) {
               URL resource = loader.getResource(descriptor.path());

               try {
                  dds.put(descriptor.name(), resource == null ? (new File(descriptor.path())).toURI().toURL() : resource);
               } catch (MalformedURLException e) {
                  throw new IllegalArgumentException(e);
               }
            }
         } else if (descriptors instanceof org.apache.openejb.junit.Descriptors) {
            for(org.apache.openejb.junit.Descriptor descriptor : ((org.apache.openejb.junit.Descriptors)descriptors).value()) {
               URL resource = loader.getResource(descriptor.path());

               try {
                  dds.put(descriptor.name(), resource == null ? (new File(descriptor.path())).toURI().toURL() : resource);
               } catch (MalformedURLException e) {
                  throw new IllegalArgumentException(e);
               }
            }
         }

         return dds;
      }
   }

   private static IAnnotationFinder finderFromClasses(DeploymentModule module, Class<?>[] value, Collection<File> others, String[] excludes) {
      Collection<Archive> archives = new ArrayList(1 + (others == null ? 0 : others.size()));
      Filter filter = excludes != null && excludes.length != 0 ? Filters.invert(Filters.prefixes(excludes)) : null;
      Collection<Class<?>> classes = new ArrayList(Arrays.asList(FinderFactory.ensureMinimalClasses(module)));
      if (value != null) {
         classes.addAll(Arrays.asList(value));
      }

      ClassesArchive classesArchive = new ClassesArchive(classes);
      archives.add(filter == null ? classesArchive : new FilteredArchive(classesArchive, filter));
      if (others != null) {
         ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

         for(File f : others) {
            try {
               Archive archive = (Archive)(f.isDirectory() ? new FileArchive(classLoader, f) : new JarArchive(classLoader, f.toURI().toURL()));
               archives.add(filter == null ? archive : new FilteredArchive(archive, filter));
            } catch (MalformedURLException e) {
               throw new IllegalArgumentException(e);
            }
         }
      }

      return (new FinderFactory.OpenEJBAnnotationFinder(new CompositeArchive(archives))).link();
   }

   private void initFilteredServiceManager(String[] services) {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

      Class serviceManagerClass;
      try {
         serviceManagerClass = classLoader.loadClass("org.apache.openejb.server.FilteredServiceManager");
      } catch (ClassNotFoundException e) {
         String msg = "Services filtering requires class 'org.apache.openejb.server.FilteredServiceManager' to be available.  Make sure you have the openejb-server-*.jar in your classpath.";
         throw new IllegalStateException("Services filtering requires class 'org.apache.openejb.server.FilteredServiceManager' to be available.  Make sure you have the openejb-server-*.jar in your classpath.", e);
      }

      try {
         Method initServiceManager = serviceManagerClass.getMethod("initServiceManager", String[].class);
         initServiceManager.invoke((Object)null, services);
      } catch (Exception e) {
         throw new IllegalStateException("Failed initializing FilteredServiceManager with services " + Arrays.toString(services), e);
      }
   }

   private static void linkageErrorProtection() {
      ClassLoader loader = ApplicationComposers.class.getClassLoader();

      try {
         Class.forName("sun.security.pkcs11.SunPKCS11", true, loader);
         Class.forName("sun.security.pkcs11.SunPKCS11$Descriptor", true, loader);
         Class.forName("sun.security.pkcs11.wrapper.PKCS11Exception", true, loader);
      } catch (Throwable var2) {
      }

   }

   public void startContainer(Object instance) throws Exception {
      this.originalProperties = (Properties)JavaSecurityManagers.getSystemProperties().clone();
      this.originalLoader = Thread.currentThread().getContextClassLoader();
      this.fixFakeClassFinder(instance);
      Properties configuration = new Properties();
      configuration.put("openejb.deployments.classpath", "false");
      EnableServices annotation = (EnableServices)this.testClass.getAnnotation(EnableServices.class);
      if (annotation != null && annotation.httpDebug()) {
         configuration.setProperty("httpejbd.print", "true");
         configuration.setProperty("httpejbd.indent.xml", "true");
         configuration.setProperty("logging.level.OpenEJB.server.http", "FINE");
      }

      org.apache.openejb.junit.EnableServices annotationOld = (org.apache.openejb.junit.EnableServices)this.testClass.getAnnotation(org.apache.openejb.junit.EnableServices.class);
      if (annotationOld != null && annotationOld.httpDebug()) {
         configuration.setProperty("httpejbd.print", "true");
         configuration.setProperty("httpejbd.indent.xml", "true");
         configuration.setProperty("logging.level.OpenEJB.server.http", "FINE");
      }

      WebResource webResource = (WebResource)this.testClass.getAnnotation(WebResource.class);
      if (webResource != null && webResource.value().length > 0) {
         configuration.setProperty("openejb.embedded.http.resources", Join.join(",", (Object[])webResource.value()));
      }

      Openejb openejb = null;
      Map<Object, List<Method>> configs = new HashMap();
      this.findAnnotatedMethods(configs, Configuration.class);
      this.findAnnotatedMethods(configs, org.apache.openejb.junit.Configuration.class);

      for(Map.Entry<Object, List<Method>> method : configs.entrySet()) {
         for(Method m : (List)method.getValue()) {
            Object o = m.invoke(method.getKey());
            if (o instanceof Properties) {
               Properties properties = (Properties)o;
               configuration.putAll(properties);
            } else if (Openejb.class.isInstance(o)) {
               openejb = (Openejb)Openejb.class.cast(o);
            } else if (String.class.isInstance(o)) {
               String path = (String)String.class.cast(o);
               URL url = Thread.currentThread().getContextClassLoader().getResource(path);
               if (url == null) {
                  throw new IllegalArgumentException(o.toString() + " not found");
               }

               InputStream in = url.openStream();

               try {
                  if (path.endsWith(".json")) {
                     openejb = (Openejb)JSonConfigReader.read(Openejb.class, in);
                  } else {
                     openejb = JaxbOpenejb.readConfig(new InputSource(in));
                  }
               } finally {
                  IO.close(in);
               }
            }
         }
      }

      if (SystemInstance.isInitialized()) {
         SystemInstance.reset();
      }

      Collection<String> propertiesToSetAgain = null;
      ContainerProperties configAnnot = (ContainerProperties)this.testClass.getAnnotation(ContainerProperties.class);
      if (configAnnot != null) {
         for(ContainerProperties.Property p : configAnnot.value()) {
            String value = p.value();
            if ("__not__set__so__ignored".equals(value)) {
               System.clearProperty(p.name());
            } else {
               String name = p.name();
               configuration.put(name, value);
               if (value.contains("${")) {
                  if (propertiesToSetAgain == null) {
                     propertiesToSetAgain = new LinkedList();
                  }

                  propertiesToSetAgain.add(name);
               }
            }
         }
      }

      SystemInstance.init(configuration);
      if (SystemInstance.get().getComponent(ThreadSingletonService.class) == null) {
         CdiBuilder.initializeOWB();
      }

      for(Map.Entry<Object, ClassFinder> finder : this.testClassFinders.entrySet()) {
         for(Field field : ((ClassFinder)finder.getValue()).findAnnotatedFields(RandomPort.class)) {
            if (!field.isAccessible()) {
               field.setAccessible(true);
            }

            String service = ((RandomPort)field.getAnnotation(RandomPort.class)).value();
            String key = ("http".equals(service) ? "httpejbd" : service) + ".port";
            String existing = SystemInstance.get().getProperty(key);
            int random;
            if (existing == null) {
               random = NetworkUtil.getNextAvailablePort();
               SystemInstance.get().setProperty(key, Integer.toString(random));
            } else {
               random = Integer.parseInt(existing);
            }

            if (Integer.TYPE == field.getType()) {
               field.set(finder.getKey(), random);
            } else if (URL.class == field.getType()) {
               field.set(finder.getKey(), new URL("http://localhost:" + random + "/"));
            }
         }
      }

      for(Map.Entry<Object, ClassFinder> finder : this.testClassFinders.entrySet()) {
         if (!((ClassFinder)finder.getValue()).findAnnotatedClasses(SimpleLog.class).isEmpty()) {
            SystemInstance.get().setProperty("openejb.jul.forceReload", "true");
            break;
         }
      }

      CdiExtensions cdiExtensions = (CdiExtensions)this.testClass.getAnnotation(CdiExtensions.class);
      if (cdiExtensions != null) {
         SystemInstance.get().setComponent(LoaderService.class, new ExtensionAwareOptimizedLoaderService(cdiExtensions.value()));
      }

      SystemInstance.get().setComponent(TestInstance.class, new TestInstance(this.testClass, instance));
      Map<Object, List<Method>> mockInjectors = new HashMap();
      this.findAnnotatedMethods(mockInjectors, MockInjector.class);
      this.findAnnotatedMethods(mockInjectors, org.apache.openejb.junit.MockInjector.class);
      if (!mockInjectors.isEmpty() && !((List)mockInjectors.values().iterator().next()).isEmpty()) {
         Map.Entry<Object, List<Method>> methods = (Map.Entry)mockInjectors.entrySet().iterator().next();
         Object o = ((Method)((List)methods.getValue()).iterator().next()).invoke(methods.getKey());
         if (o instanceof Class) {
            o = ((Class)o).newInstance();
         }

         if (o instanceof FallbackPropertyInjector) {
            SystemInstance.get().setComponent(FallbackPropertyInjector.class, (FallbackPropertyInjector)o);
         }
      }

      for(Map.Entry<Object, List<Method>> method : this.findAnnotatedMethods(new HashMap(), Component.class).entrySet()) {
         for(Method m : (List)method.getValue()) {
            this.setComponent(method.getKey(), m);
         }
      }

      for(Map.Entry<Object, List<Method>> method : this.findAnnotatedMethods(new HashMap(), org.apache.openejb.junit.Component.class).entrySet()) {
         for(Method m : (List)method.getValue()) {
            this.setComponent(method.getKey(), m);
         }
      }

      ConfigurationFactory config = new ConfigurationFactory();
      config.init(SystemInstance.get().getProperties());
      SystemInstance.get().setComponent(ConfigurationFactory.class, config);
      this.assembler = new Assembler();
      SystemInstance.get().setComponent(Assembler.class, this.assembler);
      OpenEjbConfiguration openEjbConfiguration;
      if (openejb != null) {
         openEjbConfiguration = config.getOpenEjbConfiguration(openejb);
      } else {
         openEjbConfiguration = config.getOpenEjbConfiguration();
      }

      this.assembler.buildContainerSystem(openEjbConfiguration);
      if ("true".equals(configuration.getProperty("openejb.embedded.remotable", "false")) || annotation != null || annotationOld != null) {
         try {
            if (annotation != null) {
               List<String> value = new ArrayList(Arrays.asList(annotation.value()));
               if (annotation.jaxrs()) {
                  value.add("jaxrs");
               }

               if (annotation.jaxws()) {
                  value.add("jaxws");
               }

               this.initFilteredServiceManager((String[])value.toArray(new String[value.size()]));
            }

            if (annotationOld != null) {
               this.initFilteredServiceManager(annotationOld.value());
            }

            this.serviceManager = new ServiceManagerProxy(false);
            this.serviceManager.start();
         } catch (ServiceManagerProxy.AlreadyStartedException e) {
            throw new OpenEJBRuntimeException(e);
         }
      }

      if (propertiesToSetAgain != null) {
         for(String name : propertiesToSetAgain) {
            String value = PropertyPlaceHolderHelper.simpleValue(SystemInstance.get().getProperty(name));
            configuration.put(name, value);
            JavaSecurityManagers.setSystemProperty(name, value);
         }

         propertiesToSetAgain.clear();
      }

   }

   public static void run(Class<?> type, String... args) {
      final ApplicationComposers composer = new ApplicationComposers(type, new Object[0]);

      try {
         Object instance;
         try {
            Constructor<?> constructor = type.getConstructor(String[].class);
            instance = constructor.newInstance(args);
         } catch (Exception var6) {
            instance = type.newInstance();
         }

         composer.before(instance);
         composer.testClassFinders.remove(composer);
         final CountDownLatch latch = new CountDownLatch(1);
         final Thread hook = new Thread() {
            public void run() {
               try {
                  composer.after();
               } catch (Exception var2) {
               }

            }
         };
         Runtime.getRuntime().addShutdownHook(hook);
         composer.afterRunnables.add(new Runnable() {
            public void run() {
               Runtime.getRuntime().removeShutdownHook(hook);
               latch.countDown();
            }
         });
         composer.handleLifecycle(type, instance);
         latch.await();
      } catch (InterruptedException var7) {
         Thread.interrupted();
      } catch (Exception e) {
         throw new OpenEJBRuntimeException(e);
      }

   }

   public void handleLifecycle(final Class<?> type, final Object appInstance) throws IllegalAccessException, InvocationTargetException {
      this.beforeDestroyAfterRunnables.add(new Runnable() {
         public void run() {
            for(Map.Entry<Object, ClassFinder> m : ApplicationComposers.this.testClassFinders.entrySet()) {
               for(Method mtd : ((ClassFinder)m.getValue()).findAnnotatedMethods(PreDestroy.class)) {
                  if (mtd.getParameterTypes().length == 0) {
                     if (!mtd.isAccessible()) {
                        mtd.setAccessible(true);
                     }

                     try {
                        mtd.invoke(mtd.getDeclaringClass() == type ? appInstance : m.getKey());
                     } catch (InvocationTargetException | IllegalAccessException var6) {
                     }
                  }
               }
            }

         }
      });
      if (!this.appContext.getWebContexts().isEmpty()) {
         this.beforeDestroyAfterRunnables.add(new Runnable() {
            public void run() {
               try {
                  Object sessionManager = SystemInstance.get().getComponent(ParentClassLoaderFinder.Helper.get().loadClass("org.apache.openejb.server.httpd.session.SessionManager"));
                  if (sessionManager != null) {
                     Class<?>[] paramTypes = new Class[]{WebContext.class};

                     for(WebContext web : ApplicationComposers.this.appContext.getWebContexts()) {
                        Reflections.invokeByReflection(sessionManager, "destroy", paramTypes, new Object[]{web});
                     }
                  }
               } catch (Throwable var5) {
               }

            }
         });
      }

      for(Map.Entry<Object, ClassFinder> m : this.testClassFinders.entrySet()) {
         for(Method mtd : ((ClassFinder)m.getValue()).findAnnotatedMethods(PostConstruct.class)) {
            if (mtd.getParameterTypes().length == 0) {
               if (!mtd.isAccessible()) {
                  mtd.setAccessible(true);
               }

               mtd.invoke(mtd.getDeclaringClass() == type ? appInstance : m.getKey());
            }
         }
      }

   }

   public static void main(String[] args) throws Exception {
      if (args.length < 1) {
         throw new IllegalArgumentException("provide at least application class as parameter");
      } else {
         Class<?> c = Thread.currentThread().getContextClassLoader().loadClass(args[0]);
         List<String> a = new ArrayList(Arrays.asList(args));
         a.remove(0);
         run(c, (String[])a.toArray(new String[a.size()]));
      }
   }

   static {
      linkageErrorProtection();
   }

   protected static class ExtensionAwareOptimizedLoaderService extends OptimizedLoaderService {
      private final Class<? extends Extension>[] extensions;

      protected ExtensionAwareOptimizedLoaderService(Class<? extends Extension>[] extensions) {
         super(new Properties());
         this.extensions = extensions;
      }

      protected List<? extends Extension> loadExtensions(ClassLoader classLoader) {
         List<Extension> list = new ArrayList();

         for(Class<? extends Extension> e : this.extensions) {
            try {
               list.add(e.newInstance());
            } catch (Exception e1) {
               throw new OpenEJBRuntimeException(e1);
            }
         }

         return list;
      }
   }
}
