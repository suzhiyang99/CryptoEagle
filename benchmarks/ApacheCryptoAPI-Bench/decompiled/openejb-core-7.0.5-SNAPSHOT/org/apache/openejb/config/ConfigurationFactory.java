package org.apache.openejb.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.ejb.embeddable.EJBContainer;
import org.apache.openejb.Extensions;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.Vendor;
import org.apache.openejb.api.Proxy;
import org.apache.openejb.api.resource.PropertiesResourceProvider;
import org.apache.openejb.api.resource.Template;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.Assembler;
import org.apache.openejb.assembler.classic.BmpEntityContainerInfo;
import org.apache.openejb.assembler.classic.ClientInfo;
import org.apache.openejb.assembler.classic.CmpEntityContainerInfo;
import org.apache.openejb.assembler.classic.ConnectionManagerInfo;
import org.apache.openejb.assembler.classic.ConnectorInfo;
import org.apache.openejb.assembler.classic.ContainerInfo;
import org.apache.openejb.assembler.classic.ContainerSystemInfo;
import org.apache.openejb.assembler.classic.DeploymentExceptionManager;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.assembler.classic.FacilitiesInfo;
import org.apache.openejb.assembler.classic.HandlerChainInfo;
import org.apache.openejb.assembler.classic.HandlerInfo;
import org.apache.openejb.assembler.classic.JndiContextInfo;
import org.apache.openejb.assembler.classic.ManagedContainerInfo;
import org.apache.openejb.assembler.classic.MdbContainerInfo;
import org.apache.openejb.assembler.classic.OpenEjbConfiguration;
import org.apache.openejb.assembler.classic.OpenEjbConfigurationFactory;
import org.apache.openejb.assembler.classic.ProxyFactoryInfo;
import org.apache.openejb.assembler.classic.ResourceInfo;
import org.apache.openejb.assembler.classic.SecurityServiceInfo;
import org.apache.openejb.assembler.classic.ServiceInfo;
import org.apache.openejb.assembler.classic.SingletonSessionContainerInfo;
import org.apache.openejb.assembler.classic.StatefulSessionContainerInfo;
import org.apache.openejb.assembler.classic.StatelessSessionContainerInfo;
import org.apache.openejb.assembler.classic.TransactionServiceInfo;
import org.apache.openejb.assembler.classic.WebAppInfo;
import org.apache.openejb.component.ClassLoaderEnricher;
import org.apache.openejb.config.sys.AbstractService;
import org.apache.openejb.config.sys.AdditionalDeployments;
import org.apache.openejb.config.sys.ConnectionManager;
import org.apache.openejb.config.sys.Container;
import org.apache.openejb.config.sys.Deployments;
import org.apache.openejb.config.sys.JaxbOpenejb;
import org.apache.openejb.config.sys.JndiProvider;
import org.apache.openejb.config.sys.Openejb;
import org.apache.openejb.config.sys.ProxyFactory;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.config.sys.SecurityService;
import org.apache.openejb.config.sys.ServiceProvider;
import org.apache.openejb.config.sys.TransactionManager;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.jee.Application;
import org.apache.openejb.jee.EjbJar;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.Handler;
import org.apache.openejb.jee.HandlerChain;
import org.apache.openejb.jee.HandlerChains;
import org.apache.openejb.jee.ParamValue;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.loader.FileUtils;
import org.apache.openejb.loader.Files;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.Options;
import org.apache.openejb.loader.ProvisioningUtil;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.resource.jdbc.DataSourceFactory;
import org.apache.openejb.resource.jdbc.pool.DataSourceCreator;
import org.apache.openejb.resource.jdbc.pool.DefaultDataSourceCreator;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.PropertyPlaceHolderHelper;
import org.apache.openejb.util.References;
import org.apache.openejb.util.SuperProperties;
import org.apache.openejb.util.URISupport;
import org.apache.openejb.util.URLs;
import org.apache.openejb.util.classloader.URLClassLoaderFirst;
import org.apache.openejb.util.proxy.QueryProxy;
import org.apache.xbean.finder.IAnnotationFinder;
import org.apache.xbean.finder.MetaAnnotatedClass;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;

public class ConfigurationFactory implements OpenEjbConfigurationFactory {
   public static final String OPENEJB_JDBC_DATASOURCE_CREATOR = "openejb.jdbc.datasource-creator";
   public static final String ADDITIONAL_DEPLOYMENTS = "conf/deployments.xml";
   static final String CONFIGURATION_PROPERTY = "openejb.configuration";
   static final String CONF_FILE_PROPERTY = "openejb.conf.file";
   private static final String DEBUGGABLE_VM_HACKERY_PROPERTY = "openejb.debuggable-vm-hackery";
   protected static final String VALIDATION_SKIP_PROPERTY = "openejb.validation.skip";
   private static final Logger logger;
   private static final Messages messages;
   private static final String IGNORE_DEFAULT_VALUES_PROP = "IgnoreDefaultValues";
   private static final boolean WSDL4J_AVAILABLE;
   private String configLocation;
   private OpenEjbConfiguration sys;
   private Openejb openejb;
   private final DynamicDeployer deployer;
   private final DeploymentLoader deploymentLoader;
   private final boolean offline;
   private final boolean serviceTypeIsAdjustable;
   private static final String CLASSPATH_AS_EAR = "openejb.deployments.classpath.ear";
   static final String WEBSERVICES_ENABLED = "openejb.webservices.enabled";
   static final String OFFLINE_PROPERTY = "openejb.offline";
   private static final Map<Class<? extends ServiceInfo>, DefaultService> defaultProviders;
   private static final Map<Class<? extends ServiceInfo>, Class<? extends Service>> types;
   private static final Map<String, Class<? extends ContainerInfo>> containerTypes;

   public ConfigurationFactory() {
      this(!shouldAutoDeploy());
   }

   private static boolean exists(String s) {
      try {
         ConfigurationFactory.class.getClassLoader().loadClass(s);
         return true;
      } catch (NoClassDefFoundError | ClassNotFoundException var2) {
         return false;
      }
   }

   private static boolean shouldAutoDeploy() {
      Options options = SystemInstance.get().getOptions();
      boolean b = options.get("openejb.offline", false);
      return options.get("tomee.autoconfig", !b);
   }

   public ConfigurationFactory(boolean offline) {
      this(offline, (DynamicDeployer)null);
   }

   public ConfigurationFactory(boolean offline, DynamicDeployer preAutoConfigDeployer) {
      JavaSecurityManagers.setSystemProperty("bval.in-container", JavaSecurityManagers.getSystemProperty("bval.in-container", "true"));
      this.offline = offline;
      this.serviceTypeIsAdjustable = SystemInstance.get().getOptions().get("openejb.service-type-adjustement", true);
      this.deploymentLoader = new DeploymentLoader();
      LocalMBeanServer.reset();
      Options options = SystemInstance.get().getOptions();
      if (SystemInstance.get().getComponent(DataSourceCreator.class) == null) {
         String creator = options.get("openejb.jdbc.datasource-creator", (String)null);
         if (creator == null) {
            SystemInstance.get().setComponent(DataSourceCreator.class, new DefaultDataSourceCreator());
         } else {
            try {
               SystemInstance.get().setComponent(DataSourceCreator.class, DataSourceFactory.creator(creator, false));
            } catch (Exception e) {
               logger.error("can't load " + creator + " will use the default creator", (Throwable)e);
               SystemInstance.get().setComponent(DataSourceCreator.class, new DefaultDataSourceCreator());
            }
         }
      }

      if (SystemInstance.get().getComponent(ClassLoaderEnricher.class) == null) {
         SystemInstance.get().setComponent(ClassLoaderEnricher.class, new ClassLoaderEnricher());
      }

      SystemInstance.get().setComponent(ConfigurationFactory.class, this);
      AnnotationDeployer annotationDeployer = new AnnotationDeployer();
      BeanProperties beanProperties = new BeanProperties();
      AppContextConfigDeployer appContextConfigDeployer = new AppContextConfigDeployer(annotationDeployer.getEnvEntriesPropertiesDeployer(), beanProperties);
      Chain chain = new Chain();
      chain.add(new SystemPropertiesOverride());
      chain.add(new GeneratedClientModules.Add());
      chain.add(new ApplicationComposerDeployer());
      chain.add(new ReadDescriptors());
      chain.add(appContextConfigDeployer);
      chain.add(new ApplicationProperties());
      chain.add(new ModuleProperties());
      chain.add(new LegacyProcessor());
      chain.add(annotationDeployer);
      chain.add(beanProperties);
      chain.add(new ConfigurationDeployer());
      chain.add(new ProxyBeanClassUpdate());
      chain.add(new GeneratedClientModules.Prune());
      chain.add(new ClearEmptyMappedName());
      if (!options.get("openejb.validation.skip", false)) {
         chain.add(new ValidateModules());
      } else {
         DeploymentLoader.logger.info("validationDisabled", "openejb.validation.skip");
      }

      chain.add(new InitEjbDeployments());
      if (options.get("openejb.debuggable-vm-hackery", false)) {
         chain.add(new DebuggableVmHackery());
      }

      if (options.get("openejb.webservices.enabled", true) && WSDL4J_AVAILABLE) {
         chain.add(new WsDeployer());
      } else {
         chain.add(new RemoveWebServices());
      }

      chain.add(new CmpJpaConversion());
      Set<Vendor> support = SystemInstance.get().getOptions().getAll("openejb.vendor.config", Vendor.values());
      if (support.contains(Vendor.GERONIMO) || SystemInstance.get().hasProperty("openejb.geronimo")) {
         chain.add(new OpenEjb2Conversion());
      }

      if (support.contains(Vendor.GLASSFISH)) {
         chain.add(new SunConversion());
      }

      if (support.contains(Vendor.WEBLOGIC)) {
         chain.add(new WlsConversion());
      }

      if (SystemInstance.get().hasProperty("openejb.geronimo")) {
         chain.add(new GeronimoMappedName());
      }

      if (null != preAutoConfigDeployer) {
         chain.add(preAutoConfigDeployer);
      }

      chain.add(new ConvertDataSourceDefinitions());
      chain.add(new ConvertJMSConnectionFactoryDefinitions());
      chain.add(new ConvertJMSDestinationDefinitions());
      chain.add(new CleanEnvEntries());
      chain.add(new LinkBuiltInTypes());
      chain.add(new ActivationConfigPropertyOverride());
      if (offline) {
         AutoConfig autoConfig = new AutoConfig(this);
         autoConfig.autoCreateResources(false);
         autoConfig.autoCreateContainers(false);
         chain.add(autoConfig);
      } else {
         chain.add(new AutoConfig(this));
      }

      chain.add(new ApplyOpenejbJar());
      chain.add(new MappedNameBuilder());
      chain.add(new OutputGeneratedDescriptors());
      this.deployer = chain;
   }

   public ConfigurationFactory(boolean offline, OpenEjbConfiguration configuration) {
      this(offline, (DynamicDeployer)null, configuration);
   }

   public ConfigurationFactory(boolean offline, DynamicDeployer preAutoConfigDeployer, OpenEjbConfiguration configuration) {
      this(offline, preAutoConfigDeployer);
      this.sys = configuration;
   }

   public ConfigurationFactory(boolean offline, Chain deployerChain, OpenEjbConfiguration configuration) {
      this.offline = offline;
      this.deploymentLoader = new DeploymentLoader();
      this.deployer = deployerChain;
      this.sys = configuration;
      this.serviceTypeIsAdjustable = true;
   }

   public boolean isOffline() {
      return this.offline;
   }

   public static List<HandlerChainInfo> toHandlerChainInfo(HandlerChains chains) {
      List<HandlerChainInfo> handlerChains = new ArrayList();
      if (chains == null) {
         return handlerChains;
      } else {
         for(HandlerChain handlerChain : chains.getHandlerChain()) {
            HandlerChainInfo handlerChainInfo = new HandlerChainInfo();
            handlerChainInfo.serviceNamePattern = handlerChain.getServiceNamePattern();
            handlerChainInfo.portNamePattern = handlerChain.getPortNamePattern();
            handlerChainInfo.protocolBindings.addAll(handlerChain.getProtocolBindings());

            for(Handler handler : handlerChain.getHandler()) {
               HandlerInfo handlerInfo = new HandlerInfo();
               handlerInfo.handlerName = handler.getHandlerName();
               handlerInfo.handlerClass = handler.getHandlerClass();
               handlerInfo.soapHeaders.addAll(handler.getSoapHeader());
               handlerInfo.soapRoles.addAll(handler.getSoapRole());

               for(ParamValue param : handler.getInitParam()) {
                  handlerInfo.initParams.setProperty(param.getParamName(), param.getParamValue());
               }

               handlerChainInfo.handlers.add(handlerInfo);
            }

            handlerChains.add(handlerChainInfo);
         }

         return handlerChains;
      }
   }

   public void init(Properties props) throws OpenEJBException {
      this.configLocation = props.getProperty("openejb.conf.file");
      if (this.configLocation == null) {
         this.configLocation = props.getProperty("openejb.configuration");
      }

      this.configLocation = ConfigUtils.searchForConfiguration(this.configLocation);
      if (this.configLocation != null) {
         logger.info("TomEE configuration file is '" + this.configLocation + "'");
         props.setProperty("openejb.configuration", this.configLocation);
      }

   }

   protected void install(ContainerInfo serviceInfo) throws OpenEJBException {
      if (this.sys != null) {
         this.sys.containerSystem.containers.add(serviceInfo);
      } else if (!this.offline) {
         Assembler assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);
         if (assembler == null) {
            throw new OpenEJBException("ContainerInfo: Assembler has not been defined");
         }

         assembler.createContainer(serviceInfo);
      }

   }

   protected void install(ResourceInfo serviceInfo) throws OpenEJBException {
      if (this.sys != null) {
         this.sys.facilities.resources.add(serviceInfo);
      } else if (!this.offline) {
         this.doInstall(serviceInfo);
      }

   }

   void doInstall(ResourceInfo serviceInfo) throws OpenEJBException {
      Assembler assembler = (Assembler)SystemInstance.get().getComponent(Assembler.class);
      if (assembler != null) {
         assembler.createResource((Collection)null, serviceInfo);
      } else {
         throw new OpenEJBException("ResourceInfo: Assembler has not been defined");
      }
   }

   public OpenEjbConfiguration getOpenEjbConfiguration(File configuartionFile) throws OpenEJBException {
      return configuartionFile != null ? this.getOpenEjbConfiguration(JaxbOpenejb.readConfig(configuartionFile.getAbsolutePath())) : this.getOpenEjbConfiguration((Openejb)null);
   }

   public OpenEjbConfiguration getOpenEjbConfiguration(Openejb providedConf) throws OpenEJBException {
      if (this.sys != null) {
         return this.sys;
      } else {
         if (providedConf != null) {
            this.openejb = providedConf;
         } else if (this.configLocation != null) {
            this.openejb = JaxbOpenejb.readConfig(this.configLocation);
         } else {
            this.openejb = JaxbOpenejb.createOpenejb();
         }

         for(SystemProperty sp : this.openejb.getSystemProperties()) {
            String name = sp.getName();
            String value = sp.getValue();
            SystemInstance.get().setProperty(name, value);
            JavaSecurityManagers.setSystemProperty(name, value);
         }

         loadPropertiesDeclaredConfiguration(this.openejb);
         this.sys = new OpenEjbConfiguration();
         this.sys.containerSystem = new ContainerSystemInfo();
         this.sys.facilities = new FacilitiesInfo();

         for(org.apache.openejb.config.sys.Service service : this.openejb.getServices()) {
            ServiceInfo info = this.configureService((Service)service, ServiceInfo.class);
            this.sys.facilities.services.add(info);
         }

         for(JndiProvider provider : this.openejb.getJndiProvider()) {
            JndiContextInfo info = (JndiContextInfo)this.configureService((Service)provider, JndiContextInfo.class);
            this.sys.facilities.remoteJndiContexts.add(info);
         }

         this.sys.facilities.securityService = (SecurityServiceInfo)this.configureService((Service)this.openejb.getSecurityService(), SecurityServiceInfo.class);
         this.sys.facilities.transactionService = (TransactionServiceInfo)this.configureService((Service)this.openejb.getTransactionManager(), TransactionServiceInfo.class);
         List<ResourceInfo> resources = new ArrayList();

         for(Resource resource : this.openejb.getResource()) {
            ResourceInfo resourceInfo = (ResourceInfo)this.configureService((Service)resource, ResourceInfo.class);
            resources.add(resourceInfo);
         }

         resources = sort(resources, (String)null);
         this.sys.facilities.resources.addAll(resources);
         if (this.openejb.getProxyFactory() != null) {
            this.sys.facilities.intraVmServer = (ProxyFactoryInfo)this.configureService((Service)this.openejb.getProxyFactory(), ProxyFactoryInfo.class);
         }

         for(Container declaration : this.openejb.getContainer()) {
            ContainerInfo info = this.createContainerInfo(declaration);
            this.sys.containerSystem.containers.add(info);
         }

         List<File> declaredApps = this.getDeclaredApps();

         for(File jarFile : declaredApps) {
            try {
               AppInfo appInfo = this.configureApplication(jarFile);
               this.sys.containerSystem.applications.add(appInfo);
            } catch (OpenEJBException alreadyHandled) {
               DeploymentExceptionManager exceptionManager = (DeploymentExceptionManager)SystemInstance.get().getComponent(DeploymentExceptionManager.class);
               if (exceptionManager != null) {
                  exceptionManager.pushDelpoymentException(alreadyHandled);
               }
            }
         }

         boolean embedded = SystemInstance.get().hasProperty(EJBContainer.class.getName());
         Options options = SystemInstance.get().getOptions();
         if (options.get("openejb.system.apps", false)) {
            try {
               boolean extended = SystemApps.isExtended();
               AppInfo appInfo;
               if (!extended) {
                  appInfo = SystemAppInfo.preComputedInfo(this);
               } else {
                  appInfo = this.configureApplication(new AppModule(new EjbModule[]{SystemApps.getSystemModule()}));
               }

               appInfo.properties.put("openejb.cdi.activated", "false");
               this.sys.containerSystem.applications.add(appInfo);
            } catch (OpenEJBException e) {
               logger.error("Unable to load the system applications.", (Throwable)e);
            }
         } else if (options.get("openejb.deployments.classpath", !embedded)) {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            ArrayList<File> jarFiles = this.getModulesFromClassPath(declaredApps, classLoader);
            String appId = "classpath.ear";
            boolean classpathAsEar = options.get("openejb.deployments.classpath.ear", true);

            try {
               if (classpathAsEar && !jarFiles.isEmpty()) {
                  AppInfo appInfo = this.configureApplication(classLoader, "classpath.ear", jarFiles);
                  this.sys.containerSystem.applications.add(appInfo);
               } else {
                  for(File jarFile : jarFiles) {
                     AppInfo appInfo = this.configureApplication(jarFile);
                     this.sys.containerSystem.applications.add(appInfo);
                  }
               }

               if (jarFiles.size() == 0) {
                  logger.warning("config.noModulesFoundToDeploy");
               }
            } catch (OpenEJBException var14) {
               logger.debug("config.alreadyHandled");
            }
         }

         for(Deployments deployments : this.openejb.getDeployments()) {
            if (deployments.isAutoDeploy() && deployments.getDir() != null) {
               this.sys.containerSystem.autoDeploy.add(deployments.getDir());
            }
         }

         OpenEjbConfiguration finished = this.sys;
         this.sys = null;
         this.openejb = null;
         return finished;
      }
   }

   public OpenEjbConfiguration getOpenEjbConfiguration() throws OpenEJBException {
      return this.getOpenEjbConfiguration((Openejb)null);
   }

   private List<File> getDeclaredApps() {
      List<Deployments> deployments = new ArrayList();
      if (this.openejb != null) {
         deployments.addAll(this.openejb.getDeployments());
      }

      Collection<Deployments> additionalDeploymentsList = Collections.emptyList();

      try {
         File additionalDeploymentFile = SystemInstance.get().getBase().getFile("conf/deployments.xml", false);
         if (additionalDeploymentFile.exists()) {
            InputStream fis = null;

            try {
               fis = IO.read(additionalDeploymentFile);
               AdditionalDeployments additionalDeployments = (AdditionalDeployments)JaxbOpenejb.unmarshal(AdditionalDeployments.class, fis);
               additionalDeploymentsList = additionalDeployments.getDeployments();
            } catch (Exception e) {
               logger.error("can't read conf/deployments.xml", (Throwable)e);
            } finally {
               IO.close(fis);
            }
         }
      } catch (Exception e) {
         logger.info("No additional deployments found: " + e);
      }

      FileUtils base = SystemInstance.get().getBase();
      List<Deployments> autoDeploy = new ArrayList();
      List<File> declaredAppsUrls = new ArrayList();

      for(Deployments deployment : deployments) {
         try {
            DeploymentsResolver.loadFrom(deployment, base, declaredAppsUrls);
            if (deployment.isAutoDeploy()) {
               autoDeploy.add(deployment);
            }
         } catch (SecurityException se) {
            logger.warning("Security check failed on deployment: " + deployment.getFile(), (Throwable)se);
         }
      }

      for(Deployments additionalDep : additionalDeploymentsList) {
         if (additionalDep.getFile() != null) {
            declaredAppsUrls.add(Files.path(base.getDirectory().getAbsoluteFile(), new String[]{additionalDep.getFile()}));
         } else if (additionalDep.getDir() != null) {
            declaredAppsUrls.add(Files.path(base.getDirectory().getAbsoluteFile(), new String[]{additionalDep.getDir()}));
         }

         if (additionalDep.isAutoDeploy()) {
            autoDeploy.add(additionalDep);
         }
      }

      if (autoDeploy.size() > 0) {
         AutoDeployer autoDeployer = new AutoDeployer(this, autoDeploy);
         SystemInstance.get().setComponent(AutoDeployer.class, autoDeployer);
         SystemInstance.get().addObserver(autoDeployer);
      }

      return declaredAppsUrls;
   }

   public ArrayList<File> getModulesFromClassPath(List<File> declaredApps, ClassLoader classLoader) {
      List<URL> classpathAppsUrls = DeploymentsResolver.loadFromClasspath(classLoader);
      ArrayList<File> jarFiles = new ArrayList();

      for(URL path : classpathAppsUrls) {
         File file = URLs.toFile(path);
         if (declaredApps == null || !declaredApps.contains(file)) {
            jarFiles.add(file);
         }
      }

      return jarFiles;
   }

   public ContainerInfo createContainerInfo(Container container) throws OpenEJBException {
      Class<? extends ContainerInfo> infoClass = getContainerInfoType(container.getType());
      if (infoClass == null) {
         throw new OpenEJBException(messages.format("unrecognizedContainerType", container.getType()));
      } else {
         return (ContainerInfo)this.configureService((Service)container, infoClass);
      }
   }

   public static void loadPropertiesDeclaredConfiguration(Openejb openejb) {
      Properties sysProps = new Properties(JavaSecurityManagers.getSystemProperties());
      sysProps.putAll(SystemInstance.get().getProperties());
      fillOpenEjb(openejb, sysProps);
   }

   public static void fillOpenEjb(Openejb openejb, Properties sysProps) {
      for(Map.Entry<Object, Object> entry : sysProps.entrySet()) {
         Object o = entry.getValue();
         if (o instanceof String && ((String)o).startsWith("new://")) {
            String name = (String)entry.getKey();
            String value = (String)entry.getValue();

            try {
               Object service = toConfigDeclaration(name, value);
               openejb.add(service);
            } catch (URISyntaxException e) {
               logger.error("Error declaring service '" + name + "'. Invalid Service URI '" + value + "'.  java.net.URISyntaxException: " + e.getMessage());
            } catch (OpenEJBException e) {
               logger.error(e.getMessage());
            }
         }
      }

   }

   protected static Object toConfigDeclaration(String name, String value) throws URISyntaxException, OpenEJBException {
      value = value.replaceFirst("(provider=[^#=&]+)#", "$1%23");
      URI uri = URLs.uri(value);
      return toConfigDeclaration(name, uri);
   }

   public static Object toConfigDeclaration(String id, URI uri) throws OpenEJBException {
      try {
         String serviceType = uri.getHost();

         Object object;
         try {
            object = JaxbOpenejb.create(serviceType);
         } catch (Exception e) {
            throw new OpenEJBException("Invalid URI '" + uri + "'. " + e.getMessage());
         }

         Map<String, String> map;
         try {
            map = URISupport.parseParamters(uri);
         } catch (URISyntaxException e) {
            throw new OpenEJBException("Unable to parse URI parameters '" + uri + "'. URISyntaxException: " + e.getMessage());
         }

         if (object instanceof AbstractService) {
            AbstractService service = (AbstractService)object;
            service.setId(id);
            service.setType((String)map.remove("type"));
            service.setProvider((String)map.remove("provider"));
            service.setClassName((String)map.remove("class-name"));
            service.setConstructor((String)map.remove("constructor"));
            service.setFactoryName((String)map.remove("factory-name"));
            service.setPropertiesProvider((String)map.remove("properties-provider"));
            service.setTemplate((String)map.remove("template"));
            String cp = (String)map.remove("classpath");
            if (null != cp) {
               service.setClasspath(cp);
            }

            service.setClasspathAPI((String)map.remove("classpath-api"));
            if (object instanceof Resource) {
               Resource resource = (Resource)Resource.class.cast(object);
               String aliases = (String)map.remove("aliases");
               if (aliases != null) {
                  resource.getAliases().addAll(Arrays.asList(aliases.split(",")));
               }

               String depOn = (String)map.remove("depends-on");
               if (depOn != null) {
                  resource.getDependsOn().addAll(Arrays.asList(depOn.split(",")));
               }

               resource.setPostConstruct((String)map.remove("post-construct"));
               resource.setPreDestroy((String)map.remove("pre-destroy"));
            }

            service.getProperties().putAll(map);
         } else if (object instanceof Deployments) {
            Deployments deployments = (Deployments)object;
            deployments.setDir((String)map.remove("dir"));
            deployments.setFile((String)map.remove("jar"));
            String cp = (String)map.remove("classpath");
            if (cp != null) {
               String[] paths = cp.split(File.pathSeparator);
               List<URL> urls = new ArrayList();

               for(String path : paths) {
                  for(String v : ProvisioningUtil.realLocation(PropertyPlaceHolderHelper.value(path))) {
                     urls.add((new File(v)).toURI().normalize().toURL());
                  }
               }

               deployments.setClasspath(new URLClassLoaderFirst((URL[])urls.toArray(new URL[urls.size()]), ParentClassLoaderFinder.Helper.get()));
            }
         } else if (SystemProperty.class.isInstance(object)) {
            SystemProperty sp = (SystemProperty)SystemProperty.class.cast(object);
            sp.setName((String)map.remove("name"));
            sp.setValue((String)map.remove("value"));
         }

         return object;
      } catch (Exception e) {
         throw new OpenEJBException("Error declaring service '" + id + "'. Unable to create Service definition from URI '" + uri.toString() + "'", e);
      }
   }

   public AppInfo configureApplication(File jarFile) throws OpenEJBException {
      logger.debug("Beginning load: " + jarFile.getAbsolutePath());

      try {
         AppModule appModule = this.deploymentLoader.load(jarFile, (DeploymentLoader.ExternalConfiguration)null);
         AppInfo appInfo = this.configureApplication(appModule);
         if (!appModule.getWebModules().isEmpty()) {
            for(WebAppInfo info : appInfo.webApps) {
               for(EjbModule ejbModule : appModule.getEjbModules()) {
                  if (ejbModule.getModuleId().equals(info.moduleId) && ejbModule.getFinder() != null) {
                     appInfo.properties.put(info, ejbModule);
                  }
               }
            }
         }

         appInfo.paths.add(appInfo.path);
         appInfo.paths.add(jarFile.getAbsolutePath());
         return appInfo;
      } catch (ValidationFailedException e) {
         logger.warning("configureApplication.loadFailed", jarFile.getAbsolutePath(), e.getMessage());
         throw e;
      } catch (OpenEJBException e) {
         logger.warning("configureApplication.loadFailed", e, jarFile.getAbsolutePath(), e.getMessage());
         throw e;
      }
   }

   public AppInfo configureApplication(ClassLoader classLoader, String id, List<File> jarFiles) throws OpenEJBException {
      AppModule collection = this.loadApplication(classLoader, id, jarFiles);

      try {
         AppInfo appInfo = this.configureApplication(collection);
         return appInfo;
      } catch (ValidationFailedException e) {
         logger.warning("configureApplication.loadFailed", collection.getModuleId(), e.getMessage());
         throw e;
      } catch (OpenEJBException e) {
         logger.warning("configureApplication.loadFailed", e, collection.getModuleId(), e.getMessage());
         throw e;
      }
   }

   public AppModule loadApplication(ClassLoader classLoader, String id, List<File> jarFiles) throws OpenEJBException {
      boolean standaloneModule = id == null;
      if (standaloneModule) {
         id = "";
      }

      Application application = new Application();
      application.setApplicationName(id);
      AppModule collection = new AppModule(classLoader, id, application, standaloneModule);
      Map<String, Object> altDDs = collection.getAltDDs();

      for(File jarFile : jarFiles) {
         logger.info("Beginning load: " + jarFile.getAbsolutePath());

         try {
            AppModule module = this.deploymentLoader.load(jarFile, (DeploymentLoader.ExternalConfiguration)null);
            collection.getAdditionalLibraries().addAll(module.getAdditionalLibraries());
            collection.getClientModules().addAll(module.getClientModules());
            collection.getEjbModules().addAll(module.getEjbModules());
            collection.addPersistenceModules(module.getPersistenceModules());
            collection.getConnectorModules().addAll(module.getConnectorModules());
            collection.getWebModules().addAll(module.getWebModules());
            collection.getWatchedResources().addAll(module.getWatchedResources());

            for(Map.Entry<String, Object> entry : module.getAltDDs().entrySet()) {
               Object existingValue = altDDs.get(entry.getKey());
               if (existingValue == null) {
                  altDDs.put(entry.getKey(), entry.getValue());
               } else if (entry.getValue() instanceof Collection) {
                  if (existingValue instanceof Collection) {
                     Collection values = (Collection)existingValue;
                     values.addAll((Collection)entry.getValue());
                  }
               } else if (entry.getValue() instanceof Map && existingValue instanceof Map) {
                  Map values = (Map)existingValue;
                  values.putAll((Map)entry.getValue());
               }
            }
         } catch (ValidationFailedException e) {
            logger.warning("configureApplication.loadFailed", jarFile.getAbsolutePath(), e.getMessage());
            throw e;
         } catch (OpenEJBException e) {
            logger.warning("configureApplication.loadFailed", e, jarFile.getAbsolutePath(), e.getMessage());
            throw e;
         }
      }

      return collection;
   }

   public EjbJarInfo configureApplication(EjbJar ejbJar) throws OpenEJBException {
      EjbModule ejbModule = new EjbModule(ejbJar);
      return this.configureApplication(ejbModule);
   }

   public EjbJarInfo configureApplication(EjbModule ejbModule) throws OpenEJBException {
      AppInfo appInfo = this.configureApplication(new AppModule(new EjbModule[]{ejbModule}));
      return (EjbJarInfo)appInfo.ejbJars.get(0);
   }

   public ClientInfo configureApplication(ClientModule clientModule) throws OpenEJBException {
      AppInfo appInfo = this.configureApplication(new AppModule(new ClientModule[]{clientModule}));
      return (ClientInfo)appInfo.clients.get(0);
   }

   public ConnectorInfo configureApplication(ConnectorModule connectorModule) throws OpenEJBException {
      AppInfo appInfo = this.configureApplication(new AppModule(new ConnectorModule[]{connectorModule}));
      return (ConnectorInfo)appInfo.connectors.get(0);
   }

   public WebAppInfo configureApplication(WebModule webModule) throws OpenEJBException {
      AppInfo appInfo = this.configureApplication(new AppModule(new WebModule[]{webModule}));
      return (WebAppInfo)appInfo.webApps.get(0);
   }

   public AppInfo configureApplication(AppModule appModule) throws OpenEJBException {
      AppInfo var28;
      try {
         Collection<Class<?>> extensions = new HashSet();
         Collection<String> notLoaded = new HashSet();
         List<URL> libs = appModule.getAdditionalLibraries();
         if (libs != null && libs.size() > 0) {
            Extensions.Finder finder = new Extensions.Finder("META-INF", false, (URL[])libs.toArray(new URL[libs.size()]));
            extensions.addAll(Extensions.findExtensions(finder));
            notLoaded.addAll(finder.getResourcesNotLoaded());
         }

         for(EjbModule ejb : appModule.getEjbModules()) {
            try {
               URI uri = ejb.getModuleUri();
               if (uri.isAbsolute()) {
                  URL url = uri.toURL();
                  if (libs != null && !libs.contains(url)) {
                     Extensions.Finder finder = new Extensions.Finder("META-INF", false, new URL[]{url});
                     extensions.addAll(Extensions.findExtensions(finder));
                     notLoaded.addAll(finder.getResourcesNotLoaded());
                  }
               }
            } catch (MalformedURLException | IllegalArgumentException iae) {
               logger.debug("can't look for server event listener for module " + ejb.getModuleUri(), (Throwable)iae);
            } catch (Exception var18) {
               logger.error("can't look for server event listener for module " + ejb.getJarLocation());
            }
         }

         for(WebModule web : appModule.getWebModules()) {
            List<URL> webLibs = web.getScannableUrls();
            if (webLibs != null && webLibs.size() > 0) {
               Extensions.Finder finder = new Extensions.Finder("META-INF", false, (URL[])webLibs.toArray(new URL[webLibs.size()]));
               extensions.addAll(Extensions.findExtensions(finder));
               notLoaded.addAll(finder.getResourcesNotLoaded());
            }
         }

         Extensions.addExtensions(extensions);
         String location = appModule.getJarLocation();
         logger.info("config.configApp", null != location ? location : appModule.getModuleId());
         this.deployer.deploy(appModule);
         AppInfoBuilder appInfoBuilder = new AppInfoBuilder(this);
         AppInfo info = appInfoBuilder.build(appModule);
         info.eventClassesNeedingAppClassloader.addAll(notLoaded);
         var28 = info;
      } finally {
         destroy(appModule.getEarLibFinder());

         for(EjbModule ejb : appModule.getEjbModules()) {
            destroy(ejb.getFinder());
         }

         for(WebModule web : appModule.getWebModules()) {
            destroy(web.getFinder());
         }

      }

      return var28;
   }

   private static void destroy(IAnnotationFinder finder) {
      if (finder != null) {
         if (AutoCloseable.class.isInstance(finder)) {
            try {
               ((AutoCloseable)AutoCloseable.class.cast(finder)).close();
            } catch (Exception var2) {
            }
         }

      }
   }

   public <T extends ServiceInfo> T configureService(Class<? extends T> type) throws OpenEJBException {
      return (T)this.configureService((Service)null, type);
   }

   private <T extends ServiceInfo> Service getDefaultService(Class<? extends T> type) throws OpenEJBException {
      DefaultService defaultService = (DefaultService)defaultProviders.get(type);
      if (defaultService == null) {
         return null;
      } else {
         try {
            Service service = (Service)JaxbOpenejb.create(defaultService.type);
            service.setType(defaultService.id);
            return service;
         } catch (Exception e) {
            String name = defaultService.type == null ? "null" : defaultService.type.getName();
            throw new OpenEJBException("Cannot instantiate class " + name, e);
         }
      }
   }

   public <T extends ServiceInfo> T configureService(Service service, Class<? extends T> infoType) throws OpenEJBException {
      try {
         if (infoType == null) {
            throw new NullPointerException("type");
         } else {
            if (service == null) {
               service = this.getDefaultService(infoType);
               if (service == null) {
                  throw new OpenEJBException(messages.format("configureService.noDefaultService", infoType.getName()));
               }
            }

            String template = service.getTemplate();
            if (template == null) {
               template = SystemInstance.get().getProperty(Template.class.getName());
            }

            if (template != null) {
               template = unaliasPropertiesProvider(template);
               ObjectRecipe recipe = this.newObjectRecipe(template);
               recipe.setProperty("serviceId", service.getId());
               ((Template)Template.class.cast(recipe.create())).configure(service);
            }

            ServiceProvider provider = this.getServiceProvider(service, infoType);
            if (service.getId() == null) {
               service.setId(provider.getId());
            }

            Properties overrides = trim(getSystemProperties(this.overrideKey(service), provider.getService()));
            Properties serviceProperties = service.getProperties();
            trim(serviceProperties);
            trim(provider.getProperties());
            logger.info("configureService.configuring", service.getId(), provider.getService(), provider.getId());
            if (logger.isDebugEnabled()) {
               for(Map.Entry<Object, Object> entry : serviceProperties.entrySet()) {
                  Object key = entry.getKey();
                  Object value = entry.getValue();
                  if (key instanceof String && "password".equalsIgnoreCase((String)key)) {
                     value = "<hidden>";
                  }

                  logger.debug("[" + key + "=" + value + "]");
               }

               for(Map.Entry<Object, Object> entry : overrides.entrySet()) {
                  Object key = entry.getKey();
                  Object value = entry.getValue();
                  if (key instanceof String && "password".equalsIgnoreCase((String)key)) {
                     value = "<hidden>";
                  }

                  logger.debug("Override [" + key + "=" + value + "]");
               }
            }

            Properties props = (new SuperProperties()).caseInsensitive(true);
            if (serviceProperties == null || "false".equals(serviceProperties.getProperty("IgnoreDefaultValues", "false"))) {
               props.putAll(provider.getProperties());
            }

            if (serviceProperties != null) {
               props.putAll(serviceProperties);
            }

            props.putAll(overrides);
            String propertiesProvider = service.getPropertiesProvider();
            if (propertiesProvider == null) {
               propertiesProvider = SystemInstance.get().getProperty(PropertiesResourceProvider.class.getName());
            }

            if (propertiesProvider != null) {
               propertiesProvider = unaliasPropertiesProvider(propertiesProvider);
               ObjectRecipe recipe = this.newObjectRecipe(propertiesProvider);
               recipe.setFactoryMethod("provides");
               recipe.setProperty("serviceId", service.getId());
               recipe.setProperties(props);
               recipe.setProperty("properties", props);
               Properties p = (Properties)Properties.class.cast(recipe.create());
               props.putAll(p);
            }

            props.remove("IgnoreDefaultValues");

            try {
               info = (T)(infoType.newInstance());
            } catch (Exception e) {
               throw new OpenEJBException(messages.format("configureService.cannotInstantiateClass", infoType.getName()), e);
            }

            if (service.getId().startsWith("java:/")) {
               service.setId(service.getId().substring("java:/".length()));
            }

            info.service = provider.getService();
            info.types.addAll(provider.getTypes());
            info.description = provider.getDescription();
            info.displayName = provider.getDisplayName();
            info.className = provider.getClassName();
            info.factoryMethod = provider.getFactoryName();
            info.id = service.getId();
            info.properties = props;
            info.constructorArgs.addAll(this.parseConstructorArgs(provider));
            if (info instanceof ResourceInfo && service instanceof Resource) {
               ResourceInfo ri = (ResourceInfo)ResourceInfo.class.cast(info);
               Resource resource = (Resource)Resource.class.cast(service);
               ri.jndiName = resource.getJndi();
               ri.postConstruct = resource.getPostConstruct();
               ri.preDestroy = resource.getPreDestroy();
               ri.aliases.addAll(resource.getAliases());
               ri.dependsOn.addAll(resource.getDependsOn());
            }

            if (service.getClasspath() != null && service.getClasspath().length() > 0) {
               info.classpath = resolveClasspath(service.getClasspath());
            }

            info.classpathAPI = service.getClasspathAPI();
            this.specialProcessing(info);
            return info;
         }
      } catch (NoSuchProviderException e) {
         String message = logger.fatal("configureService.failed", e, null != service ? service.getId() : "");
         throw new OpenEJBException(message + ": " + e.getMessage());
      } catch (Throwable e) {
         String message = logger.fatal("configureService.failed", e, null != service ? service.getId() : "");
         throw new OpenEJBException(message, e);
      }
   }

   private <T extends ServiceInfo> ServiceProvider getServiceProvider(Service service, Class<? extends T> infoType) throws OpenEJBException {
      String providerType = getProviderType(service);
      ServiceProvider provider = this.resolveServiceProvider(service, infoType);
      if (provider != null) {
         if (!provider.getService().equals(providerType)) {
            throw new OpenEJBException(messages.format("configureService.wrongProviderType", service.getId(), providerType));
         } else {
            return provider;
         }
      } else {
         List<ServiceProvider> providers = ServiceUtils.getServiceProvidersByServiceType(providerType);
         StringBuilder sb = new StringBuilder();
         List<String> types = new ArrayList();

         for(ServiceProvider p : providers) {
            for(String type : p.getTypes()) {
               if (!types.contains(type)) {
                  types.add(type);
                  sb.append(JavaSecurityManagers.getSystemProperty("line.separator"));
                  sb.append("  <").append(p.getService());
                  sb.append(" id=\"").append(service.getId()).append('"');
                  sb.append(" type=\"").append(type).append("\"/>");
               }
            }
         }

         String noProviderMessage = messages.format("configureService.noProviderForService", providerType, service.getId(), service.getType(), service.getProvider(), sb.toString());
         throw new NoSuchProviderException(noProviderMessage);
      }
   }

   private ObjectRecipe newObjectRecipe(String template) {
      ObjectRecipe recipe = new ObjectRecipe(template);
      recipe.allow(Option.CASE_INSENSITIVE_PROPERTIES);
      recipe.allow(Option.PRIVATE_PROPERTIES);
      recipe.allow(Option.FIELD_INJECTION);
      recipe.allow(Option.NAMED_PARAMETERS);
      recipe.allow(Option.IGNORE_MISSING_PROPERTIES);
      return recipe;
   }

   private static String unaliasPropertiesProvider(String propertiesProvider) {
      switch (propertiesProvider.toLowerCase(Locale.ENGLISH)) {
         case "heroku":
            return "org.apache.openejb.resource.heroku.HerokuDatabasePropertiesProvider";
         case "openshift:mysql":
            return "org.apache.openejb.resource.openshift.OpenshiftMySQLPropertiesProvider";
         case "openshift:postgresql":
            return "org.apache.openejb.resource.openshift.OpenshiftPostgreSQLPropertiesProvider";
         default:
            return propertiesProvider;
      }
   }

   private static String unaliasTemplate(String value) {
      return value;
   }

   public static URI[] resolveClasspath(String rawstring) throws IOException {
      FileUtils base = SystemInstance.get().getBase();
      String[] strings = rawstring.contains("mvn:") ? rawstring.split(";") : rawstring.split(File.pathSeparator);
      Collection<URI> classpath = new LinkedList();

      for(String string : strings) {
         for(String location : ProvisioningUtil.realLocation(PropertyPlaceHolderHelper.simpleValue(string))) {
            File file = base.getFile(location, false);
            classpath.add(file.toURI());
         }
      }

      return (URI[])classpath.toArray(new URI[classpath.size()]);
   }

   private String overrideKey(Service service) {
      String origin = (String)String.class.cast(service.getProperties().remove("OriginalId"));
      return origin != null ? origin : service.getId();
   }

   private static String getProviderType(Service service) {
      Class<?> clazz = service.getClass();
      if (AbstractService.class.isAssignableFrom(clazz)) {
         while(!clazz.getSuperclass().equals(AbstractService.class)) {
            clazz = clazz.getSuperclass();
         }
      }

      return clazz.getSimpleName();
   }

   private static Properties trim(Properties properties) {
      for(Map.Entry<Object, Object> entry : properties.entrySet()) {
         Object o = entry.getValue();
         if (o instanceof String) {
            String value = (String)o;
            String trimmed = value.trim();
            if (value.length() != trimmed.length()) {
               properties.put(entry.getKey(), trimmed);
            }
         }
      }

      return properties;
   }

   private <T extends ServiceInfo> void specialProcessing(T info) {
      ConfigurationFactory.TopicOrQueueDefaults.process(info);
   }

   private ServiceProvider resolveServiceProvider(Service service, Class infoType) throws OpenEJBException {
      if (service.getClassName() != null) {
         if (service.getType() == null) {
            service.setType(service.getClassName());
         }

         ServiceProvider provider = new ServiceProvider();
         provider.setId(service.getId());
         provider.setService(getProviderType(service));
         provider.getTypes().add(service.getType());
         provider.setClassName(service.getClassName());
         provider.setConstructor(service.getConstructor());
         provider.setFactoryName(service.getFactoryName());
         return provider;
      } else if (service.getProvider() != null) {
         return ServiceUtils.getServiceProvider(service.getProvider());
      } else {
         if (service.getType() == null && this.serviceTypeIsAdjustable) {
            Properties properties = service.getProperties();
            if ((properties.containsKey("JdbcDriver") || properties.containsKey("JdbcUrl") || properties.containsKey("url")) && (properties.containsKey("JtaManaged") || properties.containsKey("UserName") || properties.containsKey("Password"))) {
               service.setType("javax.sql.DataSource");
            }
         }

         if (service.getType() != null) {
            return ServiceUtils.getServiceProviderByType(getProviderType(service), service.getType());
         } else {
            if (service.getId() != null) {
               try {
                  return ServiceUtils.getServiceProvider(service.getId());
               } catch (NoSuchProviderException e) {
                  logger.debug("resolveServiceProvider", (Throwable)e);
               }
            }

            if (infoType != null) {
               Service defaultService = this.getDefaultService(infoType);
               if (defaultService != null) {
                  return this.resolveServiceProvider(defaultService, (Class)null);
               }
            }

            return null;
         }
      }
   }

   public <T extends ServiceInfo> T configureService(String id, Class<? extends T> type) throws OpenEJBException {
      return (T)this.configureService(type, id, (Properties)null, id, (String)null);
   }

   public <T extends ServiceInfo> T configureService(Class<? extends T> type, String serviceId, Properties declaredProperties, String providerId, String serviceType) throws OpenEJBException {
      if (type == null) {
         throw new NullPointerException("type is null");
      } else {
         Class<? extends Service> serviceClass = (Class)types.get(type);
         if (serviceClass == null) {
            throw new OpenEJBException("Unsupported service info type: " + type.getName());
         } else {
            Service service;
            try {
               service = (Service)serviceClass.newInstance();
            } catch (Exception e) {
               throw new OpenEJBException(messages.format("configureService.cannotInstantiateClass", serviceClass.getName()), e);
            }

            service.setId(serviceId);
            service.setProvider(providerId);
            if (declaredProperties != null) {
               service.getProperties().putAll(declaredProperties);
            }

            return (T)this.configureService(service, type);
         }
      }
   }

   protected static Properties getSystemProperties(String serviceId, String serviceType) {
      Properties sysProps = new Properties(JavaSecurityManagers.getSystemProperties());
      sysProps.putAll(SystemInstance.get().getProperties());
      return getOverrides(sysProps, serviceId, serviceType);
   }

   protected static Properties getOverrides(Properties properties, String serviceId, String serviceType) {
      String fullPrefix = serviceType.toUpperCase() + "." + serviceId + ".";
      String fullPrefix2 = serviceType.toUpperCase() + "." + serviceId + "|";
      String shortPrefix = serviceId + ".";
      String shortPrefix2 = serviceId + "|";
      Properties overrides = new Properties();

      for(Map.Entry<Object, Object> entry : properties.entrySet()) {
         String name = (String)entry.getKey();

         for(String prefix : Arrays.asList(fullPrefix, fullPrefix2, shortPrefix, shortPrefix2)) {
            if (name.toLowerCase().startsWith(prefix.toLowerCase())) {
               name = name.substring(prefix.length());
               String value = entry.getValue().toString();
               overrides.setProperty(name, value);
               break;
            }
         }
      }

      return overrides;
   }

   protected static Class<? extends ContainerInfo> getContainerInfoType(String ctype) {
      return (Class)containerTypes.get(ctype);
   }

   private List<String> parseConstructorArgs(ServiceProvider service) {
      String constructor = service.getConstructor();
      return constructor == null ? Collections.emptyList() : Arrays.asList(constructor.split("[ ,]+"));
   }

   protected List<String> getResourceIds() {
      return this.getResourceIds((String)null);
   }

   protected List<String> getResourceIds(String type) {
      return this.getResourceIds(type, (Properties)null);
   }

   public List<String> getResourceIds(String type, Properties required) {
      List<String> resourceIds = new ArrayList();
      if (required == null) {
         required = new Properties();
      }

      OpenEjbConfiguration runningConfig = this.getRunningConfig();
      if (runningConfig != null) {
         for(ResourceInfo resourceInfo : runningConfig.facilities.resources) {
            if (logger.isDebugEnabled()) {
               logger.debug(String.format("Trying to match resource type %s with classname %s, service %s.", type, resourceInfo.className, resourceInfo.service));
            }

            if ((type != null && type.equals(resourceInfo.className) || isResourceType(resourceInfo.service, resourceInfo.types, type)) && ServiceUtils.implies(required, resourceInfo.properties)) {
               resourceIds.add(resourceInfo.id);
               resourceIds.addAll(resourceInfo.aliases);
            }
         }
      }

      if (this.sys != null) {
         for(ResourceInfo resourceInfo : this.sys.facilities.resources) {
            if (isResourceType(resourceInfo.service, resourceInfo.types, type) && ServiceUtils.implies(required, resourceInfo.properties)) {
               resourceIds.add(resourceInfo.id);
               resourceIds.addAll(resourceInfo.aliases);
            }
         }

         if (this.openejb != null) {
            for(Resource resource : this.openejb.getResource()) {
               ArrayList<String> types = new ArrayList();
               if (resource.getType() != null) {
                  types.add(resource.getType());
               }

               if (isResourceType("Resource", types, type) && ServiceUtils.implies(required, resource.getProperties())) {
                  resourceIds.add(resource.getId());
                  resourceIds.addAll(resource.getAliases());
               }
            }
         }
      }

      return resourceIds;
   }

   protected ResourceInfo getResourceInfo(String id) {
      OpenEjbConfiguration runningConfig = this.getRunningConfig();
      if (runningConfig != null) {
         for(ResourceInfo resourceInfo : runningConfig.facilities.resources) {
            if (id.equals(resourceInfo.id)) {
               return resourceInfo;
            }

            for(String alias : resourceInfo.aliases) {
               if (alias.equals(id)) {
                  return resourceInfo;
               }
            }
         }
      }

      if (this.sys != null) {
         for(ResourceInfo resourceInfo : this.sys.facilities.resources) {
            if (id.equals(resourceInfo.id)) {
               return resourceInfo;
            }

            for(String alias : resourceInfo.aliases) {
               if (alias.equals(id)) {
                  return resourceInfo;
               }
            }
         }
      }

      return null;
   }

   public static boolean isResourceType(String service, List<String> types, String type) {
      return type == null || service != null && types.contains(type);
   }

   protected List<String> getContainerIds() {
      List<String> containerIds = new ArrayList();
      OpenEjbConfiguration runningConfig = this.getRunningConfig();
      if (runningConfig != null) {
         for(ContainerInfo containerInfo : runningConfig.containerSystem.containers) {
            containerIds.add(containerInfo.id);
         }
      }

      if (this.sys != null) {
         for(ContainerInfo containerInfo : this.sys.containerSystem.containers) {
            containerIds.add(containerInfo.id);
         }

         if (this.openejb != null) {
            for(Container container : this.openejb.getContainer()) {
               containerIds.add(container.getId());
            }
         }
      }

      return containerIds;
   }

   protected List<ContainerInfo> getContainerInfos() {
      List<ContainerInfo> containers = new ArrayList();
      OpenEjbConfiguration runningConfig = this.getRunningConfig();
      if (runningConfig != null) {
         for(ContainerInfo containerInfo : runningConfig.containerSystem.containers) {
            containers.add(containerInfo);
         }
      }

      if (this.sys != null) {
         for(ContainerInfo containerInfo : this.sys.containerSystem.containers) {
            containers.add(containerInfo);
         }
      }

      return containers;
   }

   private OpenEjbConfiguration getRunningConfig() {
      return (OpenEjbConfiguration)SystemInstance.get().getComponent(OpenEjbConfiguration.class);
   }

   public static List<ResourceInfo> sort(List<ResourceInfo> infos, final String prefix) {
      final Collection<String> ids = new HashSet();
      return References.<ResourceInfo>sort(infos, new References.Visitor<ResourceInfo>() {
         public String getName(ResourceInfo resourceInfo) {
            String name = prefix != null && resourceInfo.id.startsWith(prefix) ? resourceInfo.id.substring(prefix.length()) : resourceInfo.id;
            ids.add(name);
            return name;
         }

         public Set<String> getReferences(ResourceInfo resourceInfo) {
            Set<String> refs = new HashSet();

            for(Object value : resourceInfo.properties.values()) {
               if (String.class.isInstance(value)) {
                  String string = ((String)String.class.cast(value)).trim();
                  if (!string.isEmpty()) {
                     if (string.contains(",")) {
                        for(String s : string.split(",")) {
                           String trim = s.trim();
                           if (ids.contains(trim)) {
                              refs.add(trim);
                           }
                        }
                     } else {
                        String trim = String.valueOf(value).trim();
                        String id = (trim.startsWith("@") || trim.startsWith("$")) && trim.length() > 1 ? trim.substring(1) : trim;
                        if (ids.contains(id)) {
                           refs.add(id);
                        }
                     }
                  }
               }
            }

            refs.remove(this.getName(resourceInfo));
            refs.addAll(resourceInfo.dependsOn);
            return refs;
         }
      });
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, ConfigurationFactory.class);
      messages = new Messages(ConfigurationFactory.class);
      WSDL4J_AVAILABLE = exists("javax.wsdl.xml.WSDLLocator");
      defaultProviders = new HashMap();
      types = new HashMap();
      defaultProviders.put(MdbContainerInfo.class, new DefaultService("MESSAGE", Container.class));
      defaultProviders.put(ManagedContainerInfo.class, new DefaultService("MANAGED", Container.class));
      defaultProviders.put(StatefulSessionContainerInfo.class, new DefaultService("STATEFUL", Container.class));
      defaultProviders.put(StatelessSessionContainerInfo.class, new DefaultService("STATELESS", Container.class));
      defaultProviders.put(SingletonSessionContainerInfo.class, new DefaultService("SINGLETON", Container.class));
      defaultProviders.put(CmpEntityContainerInfo.class, new DefaultService("CMP_ENTITY", Container.class));
      defaultProviders.put(BmpEntityContainerInfo.class, new DefaultService("BMP_ENTITY", Container.class));
      defaultProviders.put(SecurityServiceInfo.class, new DefaultService("SecurityService", SecurityService.class));
      defaultProviders.put(TransactionServiceInfo.class, new DefaultService("TransactionManager", TransactionManager.class));
      defaultProviders.put(ConnectionManagerInfo.class, new DefaultService("ConnectionManager", ConnectionManager.class));
      defaultProviders.put(ProxyFactoryInfo.class, new DefaultService("ProxyFactory", ProxyFactory.class));

      for(Map.Entry<Class<? extends ServiceInfo>, DefaultService> entry : defaultProviders.entrySet()) {
         types.put(entry.getKey(), ((DefaultService)entry.getValue()).type);
      }

      types.put(ResourceInfo.class, Resource.class);
      containerTypes = new HashMap();
      containerTypes.put("SINGLETON", SingletonSessionContainerInfo.class);
      containerTypes.put("MANAGED", ManagedContainerInfo.class);
      containerTypes.put("STATELESS", StatelessSessionContainerInfo.class);
      containerTypes.put("STATEFUL", StatefulSessionContainerInfo.class);
      containerTypes.put("BMP_ENTITY", BmpEntityContainerInfo.class);
      containerTypes.put("CMP_ENTITY", CmpEntityContainerInfo.class);
      containerTypes.put("MESSAGE", MdbContainerInfo.class);
   }

   public static class ProxyBeanClassUpdate implements DynamicDeployer {
      public AppModule deploy(AppModule appModule) throws OpenEJBException {
         for(EjbModule module : appModule.getEjbModules()) {
            for(EnterpriseBean eb : module.getEjbJar().getEnterpriseBeans()) {
               if (eb instanceof SessionBean) {
                  SessionBean bean = (SessionBean)eb;

                  Class<?> ejbClass;
                  try {
                     ejbClass = module.getClassLoader().loadClass(bean.getEjbClass());
                  } catch (ClassNotFoundException var13) {
                     ConfigurationFactory.logger.warning("can't load " + bean.getEjbClass());
                     continue;
                  }

                  if (ejbClass.isInterface()) {
                     bean.setLocal(ejbClass.getName());
                     Proxy proxyAnnotation = (Proxy)(new MetaAnnotatedClass(ejbClass)).getAnnotation(Proxy.class);
                     Class<?> proxyClass;
                     if (proxyAnnotation != null) {
                        proxyClass = proxyAnnotation.value();
                     } else {
                        proxyClass = QueryProxy.class;
                     }

                     bean.setProxy(proxyClass.getName());

                     for(EnvEntry entry : bean.getEnvEntry()) {
                        if ("java:comp/env/implementingInterfaceClass".equals(entry.getName())) {
                           entry.setEnvEntryValue(ejbClass.getName());
                        }
                     }
                  }
               }
            }
         }

         return appModule;
      }
   }

   public static class Chain implements DynamicDeployer {
      private final List<DynamicDeployer> chain = new ArrayList();

      public boolean add(DynamicDeployer o) {
         return this.chain.add(o);
      }

      public AppModule deploy(AppModule appModule) throws OpenEJBException {
         for(DynamicDeployer deployer : this.chain) {
            appModule = deployer.deploy(appModule);
         }

         return appModule;
      }
   }

   private static class DefaultService {
      private final Class<? extends Service> type;
      private final String id;

      public DefaultService(String id, Class<? extends Service> type) {
         this.id = id;
         this.type = type;
      }
   }

   private static class TopicOrQueueDefaults {
      public static void process(ServiceInfo provider) {
         if (provider.service.equals("Resource")) {
            if (provider.types.contains("Topic") || provider.types.contains("Queue")) {
               if (provider.className.matches("org.apache.activemq.command.ActiveMQ(Topic|Queue)")) {
                  String dest = provider.properties.getProperty("destination");
                  if (dest == null || dest.length() == 0) {
                     provider.properties.setProperty("destination", provider.id);
                  }

               }
            }
         }
      }
   }
}
