package org.apache.openejb.config;

import java.io.File;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;
import org.apache.openejb.config.sys.Container;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.jee.Application;
import org.apache.openejb.jee.NamedModule;
import org.apache.openejb.jee.jpa.EntityMappings;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.jee.jpa.unit.TransactionType;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.SuperProperties;
import org.apache.xbean.finder.IAnnotationFinder;

public class AppModule implements DeploymentModule {
   public static final boolean DELEGATE_FIRST_DEFAULT = SystemInstance.get().getOptions().get("openejb.classloader.delegate-first", ParentClassLoaderFinder.Helper.get() != ClassLoader.getSystemClassLoader());
   private final Properties properties;
   private final Application application;
   private final ValidationContext validation;
   private final List<URL> additionalLibraries;
   private final List<URL> scannableContainerUrls;
   private final List<ConnectorModule> connectorModules;
   private final List<WebModule> webModules;
   private final List<ClientModule> clientModules;
   private final List<EjbModule> ejbModules;
   private final List<PersistenceModule> persistenceModules;
   private final Map<String, TransactionType> txTypeByUnit;
   private final Collection<Resource> resources;
   private final Collection<Container> containers;
   private final Collection<org.apache.openejb.config.sys.Service> services;
   private final ClassLoader classLoader;
   private EntityMappings cmpMappings;
   private final Map<String, Object> altDDs;
   private final Set<String> watchedResources;
   private final boolean standaloneModule;
   private boolean delegateFirst;
   private final Set<String> additionalLibMbeans;
   private final Collection<String> jaxRsProviders;
   private final Map<String, PojoConfiguration> pojoConfigurations;
   private IAnnotationFinder earLibFinder;
   private DeploymentModule.ID id;
   private boolean webapp;

   public AppModule(ClassLoader classLoader, String jarLocation) {
      this(classLoader, jarLocation, (Application)null, false);
   }

   public List<URL> getScannableContainerUrls() {
      return this.scannableContainerUrls;
   }

   public String persistenceUnitId(String rootUrl, String name) {
      return name + " " + rootUrl.hashCode() + this.uniqueHostIfExists();
   }

   public String uniqueHostIfExists() {
      boolean hasWebApps = !this.getWebModules().isEmpty();
      if (this.isWebapp() && hasWebApps) {
         return ((WebModule)this.getWebModules().iterator().next()).getHost();
      } else if (hasWebApps) {
         String id = null;

         for(WebModule web : this.getWebModules()) {
            if (id == null) {
               id = web.getHost();
            } else if (!id.equals(web.getHost())) {
               return "";
            }
         }

         return id;
      } else {
         return "";
      }
   }

   public <T extends DeploymentModule> AppModule(T... modules) {
      this.properties = (new SuperProperties()).caseInsensitive(true);
      this.additionalLibraries = new ArrayList();
      this.scannableContainerUrls = new ArrayList();
      this.connectorModules = new ArrayList();
      this.webModules = new ArrayList();
      this.clientModules = new ArrayList();
      this.ejbModules = new ArrayList();
      this.persistenceModules = new ArrayList();
      this.txTypeByUnit = new HashMap();
      this.resources = new LinkedHashSet();
      this.containers = new HashSet();
      this.services = new HashSet();
      this.altDDs = new HashMap();
      this.watchedResources = new TreeSet();
      this.delegateFirst = DELEGATE_FIRST_DEFAULT;
      this.additionalLibMbeans = new TreeSet();
      this.jaxRsProviders = new TreeSet();
      this.pojoConfigurations = new HashMap();
      T firstModule = modules[0];
      this.standaloneModule = true;
      this.classLoader = firstModule.getClassLoader();
      this.application = new Application(firstModule.getModuleId());
      this.id = new DeploymentModule.ID((NamedModule)null, this.application, (String)null, firstModule.getFile(), firstModule.getModuleUri(), this);
      this.validation = new ValidationContext(this);

      for(T module : modules) {
         Class<? extends DeploymentModule> type = module.getClass();
         if (type == EjbModule.class) {
            this.getEjbModules().add((EjbModule)module);
         } else if (type == ClientModule.class) {
            this.getClientModules().add((ClientModule)module);
         } else if (type == ConnectorModule.class) {
            this.getConnectorModules().add((ConnectorModule)module);
         } else if (type == WebModule.class) {
            this.getWebModules().add((WebModule)module);
         } else {
            if (type != PersistenceModule.class) {
               throw new IllegalArgumentException("Unknown module type: " + type.getName());
            }

            this.addPersistenceModule((PersistenceModule)module);
         }
      }

   }

   public boolean isDelegateFirst() {
      return this.delegateFirst;
   }

   public void setDelegateFirst(boolean delegateFirst) {
      this.delegateFirst = delegateFirst;
   }

   public Properties getProperties() {
      return this.properties;
   }

   public AppModule appModule() {
      return this;
   }

   public AppModule(ClassLoader classLoader, String jarLocation, Application application, boolean standaloneModule) {
      this.properties = (new SuperProperties()).caseInsensitive(true);
      this.additionalLibraries = new ArrayList();
      this.scannableContainerUrls = new ArrayList();
      this.connectorModules = new ArrayList();
      this.webModules = new ArrayList();
      this.clientModules = new ArrayList();
      this.ejbModules = new ArrayList();
      this.persistenceModules = new ArrayList();
      this.txTypeByUnit = new HashMap();
      this.resources = new LinkedHashSet();
      this.containers = new HashSet();
      this.services = new HashSet();
      this.altDDs = new HashMap();
      this.watchedResources = new TreeSet();
      this.delegateFirst = DELEGATE_FIRST_DEFAULT;
      this.additionalLibMbeans = new TreeSet();
      this.jaxRsProviders = new TreeSet();
      this.pojoConfigurations = new HashMap();
      this.classLoader = classLoader;
      this.application = application;
      File file = jarLocation == null ? null : new File(jarLocation);
      this.id = new DeploymentModule.ID((NamedModule)null, application, (String)null, file, (URI)null, this);
      this.validation = new ValidationContext(this);
      this.standaloneModule = standaloneModule;
   }

   public Set<String> getAdditionalLibMbeans() {
      return this.additionalLibMbeans;
   }

   public boolean isStandaloneModule() {
      return this.standaloneModule;
   }

   public void setStandaloneModule(boolean isStandalone) {
   }

   public ValidationContext getValidation() {
      return this.validation;
   }

   public boolean hasWarnings() {
      if (this.validation.hasWarnings()) {
         return true;
      } else {
         for(EjbModule module : this.ejbModules) {
            if (module.getValidation().hasWarnings()) {
               return true;
            }
         }

         for(ClientModule module : this.clientModules) {
            if (module.getValidation().hasWarnings()) {
               return true;
            }
         }

         for(ConnectorModule module : this.connectorModules) {
            if (module.getValidation().hasWarnings()) {
               return true;
            }
         }

         for(WebModule module : this.webModules) {
            if (module.getValidation().hasWarnings()) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean hasFailures() {
      if (this.validation.hasFailures()) {
         return true;
      } else {
         for(EjbModule module : this.ejbModules) {
            if (module.getValidation().hasFailures()) {
               return true;
            }
         }

         for(ClientModule module : this.clientModules) {
            if (module.getValidation().hasFailures()) {
               return true;
            }
         }

         for(ConnectorModule module : this.connectorModules) {
            if (module.getValidation().hasFailures()) {
               return true;
            }
         }

         for(WebModule module : this.webModules) {
            if (module.getValidation().hasFailures()) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean hasErrors() {
      if (this.validation.hasErrors()) {
         return true;
      } else {
         for(EjbModule module : this.ejbModules) {
            if (module.getValidation().hasErrors()) {
               return true;
            }
         }

         for(ClientModule module : this.clientModules) {
            if (module.getValidation().hasErrors()) {
               return true;
            }
         }

         for(ConnectorModule module : this.connectorModules) {
            if (module.getValidation().hasErrors()) {
               return true;
            }
         }

         for(WebModule module : this.webModules) {
            if (module.getValidation().hasErrors()) {
               return true;
            }
         }

         return false;
      }
   }

   public List<ValidationContext> getValidationContexts() {
      List<ValidationContext> contexts = new ArrayList();
      contexts.add(this.getValidation());

      for(EjbModule module : this.ejbModules) {
         contexts.add(module.getValidation());
      }

      for(ClientModule module : this.clientModules) {
         contexts.add(module.getValidation());
      }

      for(ConnectorModule module : this.connectorModules) {
         contexts.add(module.getValidation());
      }

      for(WebModule module : this.webModules) {
         contexts.add(module.getValidation());
      }

      return contexts;
   }

   public String getJarLocation() {
      return this.id.getLocation() != null ? this.id.getLocation().getAbsolutePath() : null;
   }

   public void setModuleId(String moduleId) {
      this.id = new DeploymentModule.ID((NamedModule)null, this.application, moduleId, this.id.getLocation(), this.id.getUri(), this);
   }

   public String getModuleId() {
      return this.id.getName();
   }

   public File getFile() {
      return this.id.getLocation();
   }

   public URI getModuleUri() {
      return this.id.getUri();
   }

   public Map<String, Object> getAltDDs() {
      return this.altDDs;
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public Application getApplication() {
      return this.application;
   }

   public List<ClientModule> getClientModules() {
      return this.clientModules;
   }

   public List<EjbModule> getEjbModules() {
      return this.ejbModules;
   }

   public List<PersistenceModule> getPersistenceModules() {
      return this.persistenceModules;
   }

   public List<URL> getAdditionalLibraries() {
      return this.additionalLibraries;
   }

   public EntityMappings getCmpMappings() {
      return this.cmpMappings;
   }

   public void setCmpMappings(EntityMappings cmpMappings) {
      this.cmpMappings = cmpMappings;
   }

   public List<ConnectorModule> getConnectorModules() {
      return this.connectorModules;
   }

   public List<WebModule> getWebModules() {
      return this.webModules;
   }

   public Set<String> getWatchedResources() {
      return this.watchedResources;
   }

   public Collection<Resource> getResources() {
      return this.resources;
   }

   public Collection<Container> getContainers() {
      return this.containers;
   }

   public Collection<org.apache.openejb.config.sys.Service> getServices() {
      return this.services;
   }

   public Collection<DeploymentModule> getDeploymentModule() {
      ArrayList<DeploymentModule> modules = new ArrayList();
      modules.addAll(this.ejbModules);
      modules.addAll(this.webModules);
      modules.addAll(this.connectorModules);
      modules.addAll(this.clientModules);
      return modules;
   }

   public String toString() {
      return "AppModule{moduleId='" + this.id.getName() + '\'' + '}';
   }

   public void setStandloneWebModule() {
      this.webapp = true;
   }

   public boolean isWebapp() {
      return this.webapp;
   }

   public Collection<String> getJaxRsProviders() {
      return this.jaxRsProviders;
   }

   public void addPersistenceModule(PersistenceModule root) {
      this.persistenceModules.add(root);
      Persistence persistence = root.getPersistence();

      for(PersistenceUnit unit : persistence.getPersistenceUnit()) {
         this.txTypeByUnit.put(unit.getName(), unit.getTransactionType());
      }

   }

   public void addPersistenceModules(Collection<PersistenceModule> roots) {
      for(PersistenceModule root : roots) {
         this.addPersistenceModule(root);
      }

   }

   public TransactionType getTransactionType(String unit) {
      if ((unit == null || unit.isEmpty()) && this.txTypeByUnit.size() == 1) {
         return (TransactionType)this.txTypeByUnit.values().iterator().next();
      } else {
         TransactionType type = (TransactionType)this.txTypeByUnit.get(unit);
         if (type == null) {
            type = TransactionType.JTA;
         }

         return type;
      }
   }

   public Map<String, PojoConfiguration> getPojoConfigurations() {
      return this.pojoConfigurations;
   }

   public IAnnotationFinder getEarLibFinder() {
      return this.earLibFinder;
   }

   public void setEarLibFinder(IAnnotationFinder earLibFinder) {
      this.earLibFinder = earLibFinder;
   }
}
