package org.apache.openejb.assembler.classic;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.Callable;
import javax.annotation.ManagedBean;
import javax.ejb.EJBContext;
import javax.ejb.TimerService;
import javax.ejb.spi.HandleDelegate;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import javax.naming.LinkRef;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.TransactionManager;
import javax.transaction.UserTransaction;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceContext;
import org.apache.openejb.Injection;
import org.apache.openejb.InterfaceType;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.SystemException;
import org.apache.openejb.core.CoreUserTransaction;
import org.apache.openejb.core.JndiFactory;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.core.TransactionSynchronizationRegistryWrapper;
import org.apache.openejb.core.ivm.naming.ClassReference;
import org.apache.openejb.core.ivm.naming.CrossClassLoaderJndiReference;
import org.apache.openejb.core.ivm.naming.IntraVmJndiReference;
import org.apache.openejb.core.ivm.naming.JaxWsServiceReference;
import org.apache.openejb.core.ivm.naming.JndiReference;
import org.apache.openejb.core.ivm.naming.JndiUrlReference;
import org.apache.openejb.core.ivm.naming.LazyObjectReference;
import org.apache.openejb.core.ivm.naming.MapObjectReference;
import org.apache.openejb.core.ivm.naming.ObjectReference;
import org.apache.openejb.core.ivm.naming.PersistenceContextReference;
import org.apache.openejb.core.ivm.naming.Reference;
import org.apache.openejb.core.ivm.naming.SystemComponentReference;
import org.apache.openejb.core.ivm.naming.URLReference;
import org.apache.openejb.core.webservices.HandlerChainData;
import org.apache.openejb.core.webservices.PortRefData;
import org.apache.openejb.core.webservices.ServiceRefData;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.persistence.JtaEntityManager;
import org.apache.openejb.persistence.JtaEntityManagerRegistry;
import org.apache.openejb.rest.ThreadLocalContextManager;
import org.apache.openejb.spi.ContainerSystem;
import org.apache.openejb.util.Classes;
import org.apache.openejb.util.IntrospectionSupport;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.webbeans.config.WebBeansContext;
import org.apache.webbeans.container.InjectableBeanManager;

public class JndiEncBuilder {
   public static final Logger logger;
   private final boolean beanManagedTransactions;
   private final JndiEncInfo jndiEnc;
   private final URI moduleUri;
   private final String moduleId;
   private final String uniqueId;
   private final Collection<Injection> injections;
   private final ClassLoader classLoader;
   private final Properties properties;
   private boolean useCrossClassLoaderRef;
   private boolean client;

   public JndiEncBuilder(JndiEncInfo jndiEnc, Collection<Injection> injections, String moduleId, URI moduleUri, String uniqueId, ClassLoader classLoader, Properties properties) throws OpenEJBException {
      this(jndiEnc, injections, (String)null, moduleId, moduleUri, uniqueId, classLoader, properties);
   }

   public JndiEncBuilder(JndiEncInfo jndiEnc, Collection<Injection> injections, String transactionType, String moduleId, URI moduleUri, String uniqueId, ClassLoader classLoader, Properties properties) throws OpenEJBException {
      this.useCrossClassLoaderRef = true;
      this.jndiEnc = jndiEnc;
      this.properties = properties;
      this.injections = injections;
      this.beanManagedTransactions = transactionType != null && transactionType.equalsIgnoreCase("Bean");
      this.moduleId = moduleId;
      this.moduleUri = moduleUri;
      this.uniqueId = uniqueId;
      this.classLoader = classLoader;
   }

   public boolean isUseCrossClassLoaderRef() {
      return this.useCrossClassLoaderRef;
   }

   public void setUseCrossClassLoaderRef(boolean useCrossClassLoaderRef) {
      this.useCrossClassLoaderRef = useCrossClassLoaderRef;
   }

   public boolean isClient() {
      return this.client;
   }

   public void setClient(boolean client) {
      this.client = client;
   }

   public Context build(JndiScope type) throws OpenEJBException {
      Map<String, Object> bindings = this.buildBindings(type);
      return this.build(bindings);
   }

   public Context build(Map<String, Object> bindings) throws SystemException {
      JndiFactory jndiFactory = (JndiFactory)SystemInstance.get().getComponent(JndiFactory.class);
      return SystemInstance.get().hasProperty("openejb.geronimo") ? jndiFactory.createComponentContext(new HashMap()) : jndiFactory.createComponentContext(bindings);
   }

   public Map<String, Object> buildBindings(JndiScope type) throws OpenEJBException {
      Map<String, Object> bindings = this.buildMap(type);
      switch (type) {
         case comp:
            this.addSpecialCompBindings(bindings);
            break;
         case module:
            this.addSpecialModuleBindings(bindings);
            break;
         case app:
            this.addSpecialAppBindings(bindings);
            break;
         case global:
            this.addSpecialGlobalBindings(bindings);
      }

      return bindings;
   }

   public Map<String, Object> buildMap(JndiScope scope) throws OpenEJBException {
      Map<String, Object> bindings = new TreeMap();
      JtaEntityManagerRegistry jtaEntityManagerRegistry = (JtaEntityManagerRegistry)SystemInstance.get().getComponent(JtaEntityManagerRegistry.class);

      for(EjbReferenceInfo referenceInfo : this.jndiEnc.ejbReferences) {
         Reference reference;
         if (referenceInfo.location != null) {
            reference = this.buildReferenceLocation(referenceInfo.location);
         } else if (referenceInfo.ejbDeploymentId == null) {
            reference = new LazyEjbReference(new Ref(referenceInfo), this.moduleUri, this.useCrossClassLoaderRef);
         } else {
            String jndiName = "openejb/Deployment/" + JndiBuilder.format(referenceInfo.ejbDeploymentId, referenceInfo.interfaceClassName, referenceInfo.localbean ? InterfaceType.LOCALBEAN : InterfaceType.BUSINESS_REMOTE);
            if (this.useCrossClassLoaderRef && referenceInfo.externalReference) {
               reference = new CrossClassLoaderJndiReference(jndiName);
            } else {
               reference = new IntraVmJndiReference(jndiName);
            }
         }

         bindings.put(this.normalize(referenceInfo.referenceName), reference);
      }

      for(EjbReferenceInfo referenceInfo : this.jndiEnc.ejbLocalReferences) {
         Reference reference;
         if (referenceInfo.location != null) {
            reference = this.buildReferenceLocation(referenceInfo.location);
         } else if (referenceInfo.ejbDeploymentId == null) {
            reference = new LazyEjbReference(new Ref(referenceInfo), this.moduleUri, false);
         } else {
            String jndiName = "openejb/Deployment/" + JndiBuilder.format(referenceInfo.ejbDeploymentId, referenceInfo.interfaceClassName, referenceInfo.localbean ? InterfaceType.LOCALBEAN : InterfaceType.BUSINESS_LOCAL);
            reference = new IntraVmJndiReference(jndiName);
         }

         bindings.put(this.normalize(referenceInfo.referenceName), reference);
      }

      for(EnvEntryInfo entry : this.jndiEnc.envEntries) {
         if (entry.location != null) {
            Reference reference = this.buildReferenceLocation(entry.location);
            bindings.put(this.normalize(entry.referenceName), reference);
         } else if (entry.value != null) {
            try {
               Class type = Classes.deprimitivize(this.getType(entry.type, entry));
               Object obj;
               if (type == String.class) {
                  obj = new String(entry.value);
               } else if (type == Double.class) {
                  obj = new Double(entry.value);
               } else if (type == Integer.class) {
                  obj = new Integer(entry.value);
               } else if (type == Long.class) {
                  obj = new Long(entry.value);
               } else if (type == Float.class) {
                  obj = new Float(entry.value);
               } else if (type == Short.class) {
                  obj = new Short(entry.value);
               } else if (type == Boolean.class) {
                  obj = Boolean.valueOf(entry.value);
               } else if (type == Byte.class) {
                  obj = new Byte(entry.value);
               } else if (type == Character.class) {
                  StringBuilder sb = new StringBuilder(entry.value + " ");
                  obj = new Character(sb.charAt(0));
               } else if (type == URL.class) {
                  obj = new URL(entry.value);
               } else if (type == Class.class) {
                  obj = new ClassReference(entry.value.trim());
               } else {
                  if (!type.isEnum()) {
                     throw new IllegalArgumentException("Invalid env-entry-type " + type);
                  }

                  obj = Enum.valueOf(type, entry.value.trim());
               }

               bindings.put(this.normalize(entry.referenceName), obj);
            } catch (NumberFormatException e) {
               throw new IllegalArgumentException("The env-entry-value for entry " + entry.referenceName + " was not recognizable as type " + entry.type + ". Received Message: " + e.getLocalizedMessage(), e);
            } catch (MalformedURLException e) {
               throw new IllegalArgumentException("URL for reference " + entry.referenceName + " was not a valid URL: " + entry.value, e);
            }
         }
      }

      for(ResourceReferenceInfo referenceInfo : this.jndiEnc.resourceRefs) {
         if (!(referenceInfo instanceof ContextReferenceInfo)) {
            if (referenceInfo.location != null) {
               Reference reference = this.buildReferenceLocation(referenceInfo.location);
               bindings.put(this.normalize(referenceInfo.referenceName), reference);
            } else {
               Class<?> type = this.getType(referenceInfo.referenceType, referenceInfo);
               Object reference;
               if (URL.class.equals(type)) {
                  reference = new URLReference(referenceInfo.resourceID);
               } else if (type.isAnnotationPresent(ManagedBean.class)) {
                  ManagedBean managed = (ManagedBean)type.getAnnotation(ManagedBean.class);
                  String name = managed.value().length() == 0 ? type.getSimpleName() : managed.value();
                  reference = new LinkRef("module/" + name);
               } else if (referenceInfo.resourceID != null) {
                  String jndiName = "openejb/Resource/" + referenceInfo.resourceID;
                  reference = new IntraVmJndiReference(jndiName);
               } else {
                  String jndiName = "openejb/Resource/" + referenceInfo.referenceName;
                  reference = new IntraVmJndiReference(jndiName);
               }

               bindings.put(this.normalize(referenceInfo.referenceName), reference);
            }
         } else {
            Class<?> type = this.getType(referenceInfo.referenceType, referenceInfo);
            Object reference;
            if (Request.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.REQUEST);
            } else if (HttpServletRequest.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.HTTP_SERVLET_REQUEST);
            } else if (ServletRequest.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.SERVLET_REQUEST);
            } else if (UriInfo.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.URI_INFO);
            } else if (HttpHeaders.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.HTTP_HEADERS);
            } else if (SecurityContext.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.SECURITY_CONTEXT);
            } else if (ContextResolver.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.CONTEXT_RESOLVER);
            } else if (Providers.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.PROVIDERS);
            } else if (ServletConfig.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.SERVLET_CONFIG);
            } else if (ServletContext.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.SERVLET_CONTEXT);
            } else if (HttpServletResponse.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.HTTP_SERVLET_RESPONSE);
            } else if (javax.ws.rs.container.ResourceInfo.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.RESOURCE_INFO);
            } else if (ResourceContext.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.RESOURCE_CONTEXT);
            } else if (Configuration.class.equals(type)) {
               reference = new ObjectReference(ThreadLocalContextManager.CONFIGURATION);
            } else {
               reference = new MapObjectReference(ThreadLocalContextManager.OTHERS, referenceInfo.referenceType);
            }

            bindings.put(this.normalize(referenceInfo.referenceName), reference);
         }
      }

      for(ResourceEnvReferenceInfo referenceInfo : this.jndiEnc.resourceEnvRefs) {
         if (referenceInfo.location != null) {
            Reference reference = this.buildReferenceLocation(referenceInfo.location);
            bindings.put(this.normalize(referenceInfo.referenceName), reference);
         } else {
            Class<?> type = this.getType(referenceInfo.resourceEnvRefType, referenceInfo);
            Object reference;
            if (EJBContext.class.isAssignableFrom(type)) {
               String jndiName = "comp/EJBContext";
               reference = new LinkRef("comp/EJBContext");
               if ("comp/EJBContext".equals(referenceInfo.referenceName)) {
                  continue;
               }
            } else if (Validator.class.equals(type)) {
               String jndiName = "comp/Validator";
               reference = new LinkRef("comp/Validator");
            } else if (ValidatorFactory.class.equals(type)) {
               String jndiName = "comp/ValidatorFactory";
               reference = new LinkRef("comp/ValidatorFactory");
            } else if (WebServiceContext.class.equals(type)) {
               String jndiName = "comp/WebServiceContext";
               reference = new LinkRef("comp/WebServiceContext");
            } else if (TimerService.class.equals(type)) {
               String jndiName = "comp/TimerService";
               reference = new LinkRef("comp/TimerService");
            } else if (BeanManager.class.equals(type)) {
               reference = new LazyObjectReference(new Callable<BeanManager>() {
                  public BeanManager call() throws Exception {
                     return new InjectableBeanManager(WebBeansContext.currentInstance().getBeanManagerImpl());
                  }
               });
            } else if (UserTransaction.class.equals(type)) {
               reference = new IntraVmJndiReference("comp/UserTransaction");
            } else if (referenceInfo.resourceID != null) {
               String jndiName = "openejb/Resource/" + referenceInfo.resourceID;
               reference = new IntraVmJndiReference(jndiName);
            } else {
               String jndiName = "openejb/Resource/" + referenceInfo.referenceName;
               reference = new IntraVmJndiReference(jndiName);
            }

            bindings.put(this.normalize(referenceInfo.referenceName), reference);
         }
      }

      for(PersistenceUnitReferenceInfo referenceInfo : this.jndiEnc.persistenceUnitRefs) {
         if (referenceInfo.location != null) {
            Reference reference = this.buildReferenceLocation(referenceInfo.location);
            bindings.put(this.normalize(referenceInfo.referenceName), reference);
         } else {
            String jndiName = PersistenceBuilder.getOpenEJBJndiName(referenceInfo.unitId);
            Reference reference = new IntraVmJndiReference(jndiName);
            bindings.put(this.normalize(referenceInfo.referenceName), reference);
         }
      }

      for(PersistenceContextReferenceInfo contextInfo : this.jndiEnc.persistenceContextRefs) {
         if (contextInfo.location != null) {
            Reference reference = this.buildReferenceLocation(contextInfo.location);
            bindings.put(this.normalize(contextInfo.referenceName), reference);
         } else {
            Context context = ((ContainerSystem)SystemInstance.get().getComponent(ContainerSystem.class)).getJNDIContext();

            EntityManagerFactory factory;
            try {
               String jndiName = PersistenceBuilder.getOpenEJBJndiName(contextInfo.unitId);
               factory = (EntityManagerFactory)context.lookup(jndiName);
            } catch (NamingException var15) {
               throw new OpenEJBException("PersistenceUnit '" + contextInfo.unitId + "' not found for EXTENDED ref '" + contextInfo.referenceName + "'");
            }

            JtaEntityManager jtaEntityManager = new JtaEntityManager(contextInfo.persistenceUnitName, jtaEntityManagerRegistry, factory, contextInfo.properties, contextInfo.extended, contextInfo.synchronizationType);
            Reference reference = new PersistenceContextReference(jtaEntityManager);
            bindings.put(this.normalize(contextInfo.referenceName), reference);
         }
      }

      for(ServiceReferenceInfo referenceInfo : this.jndiEnc.serviceRefs) {
         if (referenceInfo.location != null) {
            Reference reference = this.buildReferenceLocation(referenceInfo.location);
            bindings.put(this.normalize(referenceInfo.referenceName), reference);
         } else {
            Class<? extends Service> serviceClass = Service.class;
            if (referenceInfo.serviceType != null) {
               try {
                  serviceClass = this.classLoader.loadClass(referenceInfo.serviceType).asSubclass(Service.class);
               } catch (Exception e) {
                  throw new OpenEJBException("Could not load service type class " + referenceInfo.serviceType, e);
               }
            }

            Class<?> referenceClass = null;
            if (referenceInfo.referenceType != null) {
               try {
                  referenceClass = this.classLoader.loadClass(referenceInfo.referenceType);
               } catch (Exception e) {
                  throw new OpenEJBException("Could not load reference type class " + referenceInfo.referenceType, e);
               }
            }

            if (referenceClass != null && Service.class.isAssignableFrom(referenceClass)) {
               serviceClass = referenceClass.asSubclass(Service.class);
            }

            URL wsdlUrl = null;
            if (referenceInfo.wsdlFile != null) {
               try {
                  wsdlUrl = new URL(referenceInfo.wsdlFile);
               } catch (MalformedURLException e) {
                  wsdlUrl = this.classLoader.getResource(referenceInfo.wsdlFile);
                  if (wsdlUrl == null) {
                     logger.warning("Error obtaining WSDL: " + referenceInfo.wsdlFile, (Throwable)e);
                  }
               }
            }

            List<PortRefData> portRefs = new ArrayList(referenceInfo.portRefs.size());

            for(PortRefInfo portRefInfo : referenceInfo.portRefs) {
               PortRefData portRef = new PortRefData();
               portRef.setQName(portRefInfo.qname);
               portRef.setServiceEndpointInterface(portRefInfo.serviceEndpointInterface);
               portRef.setEnableMtom(portRefInfo.enableMtom);
               portRef.getProperties().putAll(portRefInfo.properties);
               portRefs.add(portRef);
            }

            List<HandlerChainData> handlerChains = null;
            if (!referenceInfo.handlerChains.isEmpty()) {
               handlerChains = WsBuilder.toHandlerChainData(referenceInfo.handlerChains, this.classLoader);
            }

            if (!this.client) {
               Reference reference = new JaxWsServiceReference(referenceInfo.id, referenceInfo.serviceQName, serviceClass, referenceInfo.portQName, referenceClass, wsdlUrl, portRefs, handlerChains, this.injections, this.properties);
               bindings.put(this.normalize(referenceInfo.referenceName), reference);
            } else {
               ServiceRefData serviceRefData = new ServiceRefData(referenceInfo.id, referenceInfo.serviceQName, serviceClass, referenceInfo.portQName, referenceClass, wsdlUrl, handlerChains, portRefs);
               bindings.put(this.normalize(referenceInfo.referenceName), serviceRefData);
            }
         }
      }

      OpenEjbConfiguration config = (OpenEjbConfiguration)SystemInstance.get().getComponent(OpenEjbConfiguration.class);
      if (config != null) {
         for(ResourceInfo resource : config.facilities.resources) {
            String jndiName = resource.jndiName;
            if (jndiName != null && !jndiName.isEmpty() && this.isNotGobalOrIsHoldByThisApp(resource, scope)) {
               String refName = "openejb/Resource/" + resource.id;
               Object reference = new IntraVmJndiReference(refName);
               String boundName = this.normalize(jndiName);
               bindings.put(boundName, reference);
            }
         }
      }

      return bindings;
   }

   private boolean isNotGobalOrIsHoldByThisApp(ResourceInfo info, JndiScope scope) {
      return !info.jndiName.startsWith("global/") || info.originAppName != null && info.originAppName.equals(this.moduleId) && JndiEncBuilder.JndiScope.global.equals(scope);
   }

   private void addSpecialCompBindings(Map<String, Object> bindings) {
      TransactionManager transactionManager = (TransactionManager)SystemInstance.get().getComponent(TransactionManager.class);
      bindings.put("comp/TransactionManager", transactionManager);
      bindings.put("comp/TransactionSynchronizationRegistry", new TransactionSynchronizationRegistryWrapper());

      try {
         bindings.put("comp/ORB", new SystemComponentReference(ParentClassLoaderFinder.Helper.get().loadClass("org.omg.CORBA.ORB")));
      } catch (ClassNotFoundException | NoClassDefFoundError var4) {
      }

      bindings.put("comp/HandleDelegate", new SystemComponentReference(HandleDelegate.class));
      bindings.put("comp/ValidatorFactory", new IntraVmJndiReference("openejb/ValidatorFactory/" + this.uniqueId));
      bindings.put("comp/Validator", new IntraVmJndiReference("openejb/Validator/" + this.uniqueId));
      if (this.beanManagedTransactions) {
         UserTransaction userTransaction = new CoreUserTransaction(transactionManager);
         bindings.put("comp/UserTransaction", userTransaction);
      }

   }

   private void addSpecialModuleBindings(Map<String, Object> bindings) {
      if (this.moduleId != null) {
         bindings.put("module/ModuleName", this.moduleId);
      }

      if (bindings.isEmpty()) {
         bindings.put("module/dummy", "dummy");
      }

   }

   private void addSpecialAppBindings(Map<String, Object> bindings) {
      if (this.moduleId != null) {
         bindings.put("app/AppName", this.moduleId);
      }

      if (bindings.isEmpty()) {
         bindings.put("app/dummy", "dummy");
      }

   }

   private void addSpecialGlobalBindings(Map<String, Object> bindings) {
      if (bindings.isEmpty()) {
         bindings.put("global/dummy", "dummy");
      }

   }

   public static boolean bindingExists(Context context, Name contextName) {
      try {
         return context.lookup(contextName) != null;
      } catch (NamingException var3) {
         return false;
      }
   }

   private Reference buildReferenceLocation(ReferenceLocationInfo location) {
      if (location.jndiProviderId != null) {
         String subContextName = "openejb/remote_jndi_contexts/" + location.jndiProviderId;
         return new JndiReference(subContextName, location.jndiName);
      } else {
         return new JndiUrlReference(location.jndiName);
      }
   }

   private String normalize(String name) {
      return name;
   }

   private Class getType(String type, InjectableInfo injectable) throws OpenEJBException {
      if (type != null) {
         try {
            return this.classLoader.loadClass(type.trim());
         } catch (ClassNotFoundException var4) {
            throw new OpenEJBException("Unable to load type '" + type + "' for " + injectable.referenceName);
         }
      } else {
         return this.inferType(injectable);
      }
   }

   private Class inferType(InjectableInfo injectable) throws OpenEJBException {
      for(InjectionInfo injection : injectable.targets) {
         try {
            Class<?> target = this.classLoader.loadClass(injection.className.trim());
            return IntrospectionSupport.getPropertyType(target, injection.propertyName.trim());
         } catch (NoSuchFieldException | ClassNotFoundException var5) {
         }
      }

      throw new OpenEJBException("Unable to infer type for " + injectable.referenceName);
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, JndiEncBuilder.class.getPackage().getName());
   }

   public static enum JndiScope {
      comp,
      module,
      app,
      global;
   }

   private static class Ref implements EjbResolver.Reference, Serializable {
      private final EjbReferenceInfo info;

      public Ref(EjbReferenceInfo info) {
         this.info = info;
      }

      public String getEjbLink() {
         return this.info.link;
      }

      public String getHome() {
         return this.info.homeClassName;
      }

      public String getInterface() {
         return this.info.interfaceClassName;
      }

      public String getMappedName() {
         return null;
      }

      public String getName() {
         return this.info.referenceName;
      }

      public EjbResolver.Type getRefType() {
         if (this.info instanceof EjbLocalReferenceInfo) {
            return EjbResolver.Type.LOCAL;
         } else {
            return this.info.homeClassName != null ? EjbResolver.Type.REMOTE : EjbResolver.Type.UNKNOWN;
         }
      }
   }
}
