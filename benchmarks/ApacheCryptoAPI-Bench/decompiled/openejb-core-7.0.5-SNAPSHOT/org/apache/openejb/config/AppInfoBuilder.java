package org.apache.openejb.config;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.xml.bind.JAXBException;
import org.apache.openejb.ClassLoaderUtil;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.ClassListInfo;
import org.apache.openejb.assembler.classic.ClientInfo;
import org.apache.openejb.assembler.classic.ConnectorInfo;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.assembler.classic.EnterpriseBeanInfo;
import org.apache.openejb.assembler.classic.FilterInfo;
import org.apache.openejb.assembler.classic.HandlerChainInfo;
import org.apache.openejb.assembler.classic.IdPropertiesInfo;
import org.apache.openejb.assembler.classic.JndiEncInfo;
import org.apache.openejb.assembler.classic.ListenerInfo;
import org.apache.openejb.assembler.classic.MdbContainerInfo;
import org.apache.openejb.assembler.classic.MessageDrivenBeanInfo;
import org.apache.openejb.assembler.classic.ParamValueInfo;
import org.apache.openejb.assembler.classic.PersistenceUnitInfo;
import org.apache.openejb.assembler.classic.PortInfo;
import org.apache.openejb.assembler.classic.ResourceInfo;
import org.apache.openejb.assembler.classic.ServiceInfo;
import org.apache.openejb.assembler.classic.ServletInfo;
import org.apache.openejb.assembler.classic.ValidatorBuilder;
import org.apache.openejb.assembler.classic.WebAppInfo;
import org.apache.openejb.config.event.BeforeAppInfoBuilderEvent;
import org.apache.openejb.config.sys.Container;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.config.sys.ServiceProvider;
import org.apache.openejb.jee.AdminObject;
import org.apache.openejb.jee.ApplicationClient;
import org.apache.openejb.jee.ConfigProperty;
import org.apache.openejb.jee.ConnectionDefinition;
import org.apache.openejb.jee.Connector;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.Filter;
import org.apache.openejb.jee.InboundResourceadapter;
import org.apache.openejb.jee.Listener;
import org.apache.openejb.jee.MessageListener;
import org.apache.openejb.jee.OutboundResourceAdapter;
import org.apache.openejb.jee.ParamValue;
import org.apache.openejb.jee.PortComponent;
import org.apache.openejb.jee.ResourceAdapter;
import org.apache.openejb.jee.ServiceImplBean;
import org.apache.openejb.jee.Servlet;
import org.apache.openejb.jee.SessionConfig;
import org.apache.openejb.jee.TransactionSupportType;
import org.apache.openejb.jee.WebApp;
import org.apache.openejb.jee.WebserviceDescription;
import org.apache.openejb.jee.Webservices;
import org.apache.openejb.jee.jpa.EntityMappings;
import org.apache.openejb.jee.jpa.JpaJaxbUtil;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.jee.oejb3.PojoDeployment;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.persistence.PersistenceBootstrap;
import org.apache.openejb.util.CircularReferencesException;
import org.apache.openejb.util.JavaSecurityManagers;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.Messages;
import org.apache.openejb.util.References;
import org.apache.openejb.util.URLs;

class AppInfoBuilder {
   private static final Logger logger;
   private static final boolean USE_EAR_AS_CONTEXT_ROOT_BASE;
   private final ConfigurationFactory configFactory;
   private final EjbJarInfoBuilder ejbJarInfoBuilder = new EjbJarInfoBuilder();

   public AppInfoBuilder(ConfigurationFactory configFactory) {
      this.configFactory = configFactory;
   }

   public AppInfo build(AppModule appModule) throws OpenEJBException {
      SystemInstance.get().fireEvent(new BeforeAppInfoBuilderEvent(appModule));
      AppInfo appInfo = new AppInfo();
      appInfo.appId = appModule.getModuleId();
      appInfo.path = appModule.getJarLocation();
      appInfo.standaloneModule = appModule.isStandaloneModule() || appModule.isWebapp();
      appInfo.delegateFirst = appModule.isDelegateFirst();
      appInfo.watchedResources.addAll(appModule.getWatchedResources());
      appInfo.mbeans.addAll(appModule.getAdditionalLibMbeans());
      appInfo.jaxRsProviders.addAll(appModule.getJaxRsProviders());
      appInfo.properties.putAll(appModule.getProperties());
      if (appInfo.appId == null) {
         throw new IllegalArgumentException("AppInfo.appId cannot be null");
      } else {
         if (appInfo.path == null) {
            appInfo.path = appInfo.appId;
         }

         this.buildPojoConfiguration(appModule, appInfo);
         this.buildAppResources(appModule, appInfo);
         this.buildAppServices(appModule, appInfo);
         this.buildConnectorModules(appModule, appInfo);
         this.buildPersistenceModules(appModule, appInfo);
         List<String> containerIds = this.configFactory.getContainerIds();

         for(ConnectorInfo connectorInfo : appInfo.connectors) {
            for(MdbContainerInfo containerInfo : connectorInfo.inbound) {
               containerIds.add(containerInfo.id);
            }
         }

         containerIds.addAll(appInfo.containerIds);
         Map<EjbModule, EjbJarInfo> ejbJarInfos = new HashMap();

         for(EjbModule ejbModule : appModule.getEjbModules()) {
            try {
               EjbJarInfo ejbJarInfo = this.ejbJarInfoBuilder.buildInfo(ejbModule);
               ejbJarInfo.mbeans = ejbModule.getMbeans();
               Map<String, EjbDeployment> deploymentsByEjbName = ejbModule.getOpenejbJar().getDeploymentsByEjbName();

               for(EnterpriseBeanInfo bean : ejbJarInfo.enterpriseBeans) {
                  EjbDeployment d = (EjbDeployment)deploymentsByEjbName.get(bean.ejbName);
                  if (d.getContainerId() != null && !containerIds.contains(d.getContainerId())) {
                     for(String cId : appInfo.containerIds) {
                        if (cId.endsWith("/" + d.getContainerId())) {
                           d.setContainerId(cId);
                           break;
                        }
                     }
                  }

                  if (bean instanceof MessageDrivenBeanInfo && !containerIds.contains(d.getContainerId()) && !skipMdb(bean)) {
                     MessageDrivenBeanInfo mdb = (MessageDrivenBeanInfo)bean;
                     String newContainerId = d.getContainerId() + "-" + mdb.mdbInterface;
                     if (containerIds.contains(newContainerId)) {
                        d.setContainerId(newContainerId);
                     }
                  }

                  if (!containerIds.contains(d.getContainerId()) && !skipMdb(bean)) {
                     String msg = (new Messages("org.apache.openejb.util.resources")).format("config.noContainerFound", d.getContainerId(), d.getEjbName());
                     logger.fatal(msg);
                     throw new OpenEJBException(msg);
                  }

                  bean.containerId = d.getContainerId();
               }

               for(PojoDeployment pojoDeployment : ejbModule.getOpenejbJar().getPojoDeployment()) {
                  IdPropertiesInfo info = new IdPropertiesInfo();
                  info.id = pojoDeployment.getClassName();
                  info.properties.putAll(pojoDeployment.getProperties());
                  ejbJarInfo.pojoConfigurations.add(info);
               }

               ejbJarInfo.validationInfo = ValidatorBuilder.getInfo(ejbModule.getValidationConfig());
               ejbJarInfo.portInfos.addAll(this.configureWebservices(ejbModule.getWebservices()));
               ejbJarInfo.uniqueId = ejbModule.getUniqueId();
               ejbJarInfo.webapp = ejbModule.isWebapp();
               this.configureWebserviceSecurity(ejbJarInfo, ejbModule);
               ejbJarInfos.put(ejbModule, ejbJarInfo);
               appInfo.ejbJars.add(ejbJarInfo);
            } catch (OpenEJBException e) {
               ConfigUtils.logger.warning("conf.0004", ejbModule.getJarLocation(), e.getMessage());
               throw e;
            }
         }

         JndiEncInfoBuilder jndiEncInfoBuilder = new JndiEncInfoBuilder(appInfo);
         if (appModule.getApplication() != null) {
            jndiEncInfoBuilder.build(appModule.getApplication(), appInfo.appId, (String)null, appModule.getModuleUri(), new JndiEncInfo(), new JndiEncInfo());
         }

         List<EnterpriseBeanInfo> beans = new ArrayList();

         for(EjbModule ejbModule : appModule.getEjbModules()) {
            EjbJarInfo ejbJar = (EjbJarInfo)ejbJarInfos.get(ejbModule);
            Map<String, EnterpriseBean> beanData = ejbModule.getEjbJar().getEnterpriseBeansByEjbName();

            for(EnterpriseBeanInfo beanInfo : ejbJar.enterpriseBeans) {
               beans.add(beanInfo);
               EnterpriseBean enterpriseBean = (EnterpriseBean)beanData.get(beanInfo.ejbName);
               jndiEncInfoBuilder.build(enterpriseBean, beanInfo.ejbName, ejbJar.moduleName, ejbModule.getModuleUri(), ejbJar.moduleJndiEnc, beanInfo.jndiEnc);
               jndiEncInfoBuilder.buildDependsOnRefs(enterpriseBean, beanInfo, ejbJar.moduleName);
            }
         }

         try {
            References.sort(beans, new References.Visitor<EnterpriseBeanInfo>() {
               public String getName(EnterpriseBeanInfo bean) {
                  return bean.ejbDeploymentId;
               }

               public Set<String> getReferences(EnterpriseBeanInfo bean) {
                  return new LinkedHashSet(bean.dependsOn);
               }
            });
         } catch (CircularReferencesException var16) {
         }

         this.buildClientModules(appModule, appInfo, jndiEncInfoBuilder);
         this.buildWebModules(appModule, jndiEncInfoBuilder, appInfo);

         for(URL url : appModule.getAdditionalLibraries()) {
            File file = URLs.toFile(url);

            try {
               appInfo.libs.add(file.getCanonicalPath());
            } catch (IOException var15) {
               throw new OpenEJBException("Invalid application lib path " + file.getAbsolutePath());
            }
         }

         if (appModule.getCmpMappings() != null) {
            try {
               appInfo.cmpMappingsXml = JpaJaxbUtil.marshal(EntityMappings.class, appModule.getCmpMappings());
            } catch (JAXBException e) {
               throw new OpenEJBException("Unable to marshal cmp entity mappings", e);
            }
         }

         ReportValidationResults reportValidationResults = new ReportValidationResults();
         reportValidationResults.deploy(appModule);
         logger.info("config.appLoaded", appInfo.path);
         appInfo.webAppAlone = appModule.isWebapp();
         return appInfo;
      }
   }

   private void buildPojoConfiguration(AppModule appModule, AppInfo appInfo) {
      for(Map.Entry<String, PojoConfiguration> config : appModule.getPojoConfigurations().entrySet()) {
         IdPropertiesInfo info = new IdPropertiesInfo();
         info.id = (String)config.getKey();
         info.properties.putAll(((PojoConfiguration)config.getValue()).getProperties());
         appInfo.pojoConfigurations.add(info);
      }

   }

   private void buildAppServices(AppModule appModule, AppInfo appInfo) throws OpenEJBException {
      for(org.apache.openejb.config.sys.Service service : appModule.getServices()) {
         ServiceInfo info = this.configFactory.configureService((Service)service, ServiceInfo.class);
         appInfo.services.add(info);
      }

   }

   private void buildAppResources(AppModule module, AppInfo info) {
      for(Resource def : module.getResources()) {
         if (!def.getProperties().containsKey("ApplicationWide")) {
            info.resourceIds.add(def.getId());
            info.resourceAliases.addAll(def.getAliases());
         }
      }

      for(Container def : module.getContainers()) {
         if (!def.getProperties().containsKey("ApplicationWide")) {
            info.containerIds.add(def.getId());
         }
      }

   }

   private void buildClientModules(AppModule appModule, AppInfo appInfo, JndiEncInfoBuilder jndiEncInfoBuilder) throws OpenEJBException {
      for(ClientModule clientModule : appModule.getClientModules()) {
         ApplicationClient applicationClient = clientModule.getApplicationClient();
         ClientInfo clientInfo = new ClientInfo();
         clientInfo.description = applicationClient.getDescription();
         clientInfo.displayName = applicationClient.getDisplayName();
         clientInfo.path = clientModule.getJarLocation();
         clientInfo.mainClass = clientModule.getMainClass();
         clientInfo.localClients.addAll(clientModule.getLocalClients());
         clientInfo.remoteClients.addAll(clientModule.getRemoteClients());
         clientInfo.callbackHandler = applicationClient.getCallbackHandler();
         clientInfo.moduleId = getClientModuleId(clientModule);
         clientInfo.watchedResources.addAll(clientModule.getWatchedResources());
         clientInfo.validationInfo = ValidatorBuilder.getInfo(clientModule.getValidationConfig());
         clientInfo.uniqueId = clientModule.getUniqueId();
         jndiEncInfoBuilder.build(applicationClient, clientModule.getJarLocation(), clientInfo.moduleId, clientModule.getModuleUri(), clientInfo.jndiEnc, clientInfo.jndiEnc);
         appInfo.clients.add(clientInfo);
      }

   }

   private void buildWebModules(AppModule appModule, JndiEncInfoBuilder jndiEncInfoBuilder, AppInfo appInfo) throws OpenEJBException {
      for(WebModule webModule : appModule.getWebModules()) {
         WebApp webApp = webModule.getWebApp();
         WebAppInfo webAppInfo = new WebAppInfo();
         webAppInfo.description = webApp.getDescription();
         webAppInfo.displayName = webApp.getDisplayName();
         webAppInfo.path = webModule.getJarLocation();
         webAppInfo.moduleId = webModule.getModuleId();
         webAppInfo.watchedResources.addAll(webModule.getWatchedResources());
         webAppInfo.validationInfo = ValidatorBuilder.getInfo(webModule.getValidationConfig());
         webAppInfo.uniqueId = webModule.getUniqueId();
         webAppInfo.restApplications.addAll(webModule.getRestApplications());
         webAppInfo.restClass.addAll(webModule.getRestClasses());
         webAppInfo.ejbWebServices.addAll(webModule.getEjbWebServices());
         webAppInfo.ejbRestServices.addAll(webModule.getEjbRestServices());
         webAppInfo.jaxRsProviders.addAll(webModule.getJaxrsProviders());

         for(Map.Entry<String, Set<String>> entry : webModule.getWebAnnotatedClasses().entrySet()) {
            ClassListInfo info = new ClassListInfo();
            info.name = (String)entry.getKey();
            info.list.addAll((Collection)entry.getValue());
            webAppInfo.webAnnotatedClasses.add(info);
         }

         for(Map.Entry<String, Set<String>> entry : webModule.getJsfAnnotatedClasses().entrySet()) {
            ClassListInfo info = new ClassListInfo();
            info.name = (String)entry.getKey();
            info.list.addAll((Collection)entry.getValue());
            webAppInfo.jsfAnnotatedClasses.add(info);
         }

         webAppInfo.host = webModule.getHost();
         if (!webModule.isStandaloneModule() && USE_EAR_AS_CONTEXT_ROOT_BASE) {
            webAppInfo.contextRoot = appModule.getModuleId() + "/" + webModule.getContextRoot();
         } else {
            webAppInfo.contextRoot = webModule.getContextRoot();
         }

         webAppInfo.sessionTimeout = 30;
         if (webModule.getWebApp() != null && webModule.getWebApp().getSessionConfig() != null) {
            for(SessionConfig sessionConfig : webModule.getWebApp().getSessionConfig()) {
               if (sessionConfig.getSessionTimeout() != null) {
                  webAppInfo.sessionTimeout = sessionConfig.getSessionTimeout();
                  break;
               }
            }
         }

         jndiEncInfoBuilder.build(webApp, webModule.getJarLocation(), webAppInfo.moduleId, webModule.getModuleUri(), webAppInfo.jndiEnc, webAppInfo.jndiEnc);
         webAppInfo.portInfos.addAll(this.configureWebservices(webModule.getWebservices()));

         for(Servlet servlet : webModule.getWebApp().getServlet()) {
            ServletInfo servletInfo = new ServletInfo();
            servletInfo.servletName = servlet.getServletName();
            servletInfo.servletClass = servlet.getServletClass();
            servletInfo.mappings = webModule.getWebApp().getServletMappings(servletInfo.servletName);

            for(ParamValue pv : servlet.getInitParam()) {
               ParamValueInfo pvi = new ParamValueInfo();
               pvi.name = pv.getParamName();
               pvi.value = pv.getParamValue();
               servletInfo.initParams.add(pvi);
            }

            webAppInfo.servlets.add(servletInfo);
         }

         for(Listener listener : webModule.getWebApp().getListener()) {
            ListenerInfo listenerInfo = new ListenerInfo();
            listenerInfo.classname = listener.getListenerClass();
            webAppInfo.listeners.add(listenerInfo);
         }

         for(Filter filter : webModule.getWebApp().getFilter()) {
            FilterInfo filterInfo = new FilterInfo();
            filterInfo.name = filter.getFilterName();
            filterInfo.classname = filter.getFilterClass();
            filterInfo.mappings = webModule.getWebApp().getFilterMappings(filter.getFilterName());

            for(ParamValue pv : filter.getInitParam()) {
               filterInfo.initParams.put(pv.getParamName(), pv.getParamValue());
            }

            webAppInfo.filters.add(filterInfo);
         }

         appInfo.webApps.add(webAppInfo);
      }

   }

   private void buildConnectorModules(AppModule appModule, AppInfo appInfo) throws OpenEJBException {
      String appId = appModule.getModuleId();

      for(ConnectorModule connectorModule : appModule.getConnectorModules()) {
         Connector connector = connectorModule.getConnector();
         ConnectorInfo connectorInfo = new ConnectorInfo();
         connectorInfo.description = connector.getDescription();
         connectorInfo.displayName = connector.getDisplayName();
         connectorInfo.path = connectorModule.getJarLocation();
         connectorInfo.moduleId = connectorModule.getModuleId();
         connectorInfo.watchedResources.addAll(connectorModule.getWatchedResources());
         connectorInfo.validationInfo = ValidatorBuilder.getInfo(connectorModule.getValidationConfig());
         connectorInfo.uniqueId = connectorModule.getUniqueId();
         connectorInfo.mbeans = connectorModule.getMbeans();

         for(URL url : connectorModule.getLibraries()) {
            File file = URLs.toFile(url);

            try {
               connectorInfo.libs.add(file.getCanonicalPath());
            } catch (IOException var25) {
               throw new IllegalArgumentException("Invalid application lib path " + file.getAbsolutePath());
            }
         }

         ResourceAdapter resourceAdapter = connector.getResourceAdapter();
         if (resourceAdapter.getResourceAdapterClass() != null) {
            String id = this.getId(connectorModule);
            String className = resourceAdapter.getResourceAdapterClass();
            ServiceProvider provider = new ServiceProvider(className, id, "Resource");
            provider.getTypes().add(className);
            ServiceUtils.registerServiceProvider(appId, provider);
            Resource resource = new Resource(id, className, appId + "#" + id);

            for(ConfigProperty property : resourceAdapter.getConfigProperty()) {
               String name = property.getConfigPropertyName();
               String value = property.getConfigPropertyValue();
               if (value != null) {
                  resource.getProperties().setProperty(name, value);
               }
            }

            connectorInfo.resourceAdapter = (ResourceInfo)this.configFactory.configureService((Service)resource, ResourceInfo.class);
         }

         OutboundResourceAdapter outbound = resourceAdapter.getOutboundResourceAdapter();
         if (outbound != null) {
            String transactionSupport = "none";
            TransactionSupportType transactionSupportType = outbound.getTransactionSupport();
            if (transactionSupportType != null) {
               switch (transactionSupportType) {
                  case LOCAL_TRANSACTION:
                     transactionSupport = "local";
                     break;
                  case NO_TRANSACTION:
                     transactionSupport = "none";
                     break;
                  case XA_TRANSACTION:
                     transactionSupport = "xa";
               }
            }

            for(ConnectionDefinition connection : outbound.getConnectionDefinition()) {
               String id = this.getId(connection, outbound, connectorModule);
               String className = connection.getManagedConnectionFactoryClass();
               String type = connection.getConnectionFactoryInterface();
               ServiceProvider provider = new ServiceProvider(className, id, "Resource");
               provider.getTypes().add(type);
               ServiceUtils.registerServiceProvider(appId, provider);
               Resource resource = new Resource(id, type, appId + "#" + id);
               Properties properties = resource.getProperties();

               for(ConfigProperty property : connection.getConfigProperty()) {
                  String name = property.getConfigPropertyName();
                  String value = property.getConfigPropertyValue();
                  if (value != null) {
                     properties.setProperty(name, value);
                  }
               }

               properties.setProperty("TransactionSupport", transactionSupport);
               if (connectorInfo.resourceAdapter != null) {
                  properties.setProperty("ResourceAdapter", connectorInfo.resourceAdapter.id);
               }

               ResourceInfo resourceInfo = (ResourceInfo)this.configFactory.configureService((Service)resource, ResourceInfo.class);
               connectorInfo.outbound.add(resourceInfo);
            }
         }

         InboundResourceadapter inbound = resourceAdapter.getInboundResourceAdapter();
         if (inbound != null) {
            for(MessageListener messageListener : inbound.getMessageAdapter().getMessageListener()) {
               String id = this.getId(messageListener, inbound, connectorModule);
               Container container = new Container(id, "MESSAGE", (String)null);
               Properties properties = container.getProperties();
               properties.setProperty("ResourceAdapter", connectorInfo.resourceAdapter.id);
               properties.setProperty("MessageListenerInterface", messageListener.getMessageListenerType());
               properties.setProperty("ActivationSpecClass", messageListener.getActivationSpec().getActivationSpecClass());
               MdbContainerInfo mdbContainerInfo = (MdbContainerInfo)this.configFactory.configureService((Service)container, MdbContainerInfo.class);
               connectorInfo.inbound.add(mdbContainerInfo);
            }
         }

         for(AdminObject adminObject : resourceAdapter.getAdminObject()) {
            String id = this.getId(adminObject, resourceAdapter, connectorModule);
            String className = adminObject.getAdminObjectClass();
            String type = adminObject.getAdminObjectInterface();
            ServiceProvider provider = new ServiceProvider(className, id, "Resource");
            provider.getTypes().add(type);
            ServiceUtils.registerServiceProvider(appId, provider);
            Resource resource = new Resource(id, type, appId + "#" + id);
            Properties properties = resource.getProperties();

            for(ConfigProperty property : adminObject.getConfigProperty()) {
               String name = property.getConfigPropertyName();
               String value = property.getConfigPropertyValue();
               if (value != null) {
                  properties.setProperty(name, value);
               }
            }

            ResourceInfo resourceInfo = (ResourceInfo)this.configFactory.configureService((Service)resource, ResourceInfo.class);
            connectorInfo.adminObject.add(resourceInfo);
         }

         appInfo.connectors.add(connectorInfo);
      }

   }

   private String getId(AdminObject adminObject, ResourceAdapter resourceAdapter, ConnectorModule connectorModule) {
      String id;
      if (adminObject.getId() != null) {
         id = adminObject.getId();
      } else if (resourceAdapter.getAdminObject().size() == 1) {
         id = connectorModule.getModuleId();
      } else {
         id = connectorModule.getModuleId() + "-" + adminObject.getAdminObjectInterface();
      }

      return id;
   }

   private String getId(MessageListener messageListener, InboundResourceadapter inbound, ConnectorModule connectorModule) {
      String id;
      if (messageListener.getId() != null) {
         id = messageListener.getId();
      } else if (inbound.getMessageAdapter().getMessageListener().size() == 1) {
         id = connectorModule.getModuleId();
      } else {
         id = connectorModule.getModuleId() + "-" + messageListener.getMessageListenerType();
      }

      return id;
   }

   private String getId(ConnectionDefinition connection, OutboundResourceAdapter outbound, ConnectorModule connectorModule) {
      String id;
      if (connection.getId() != null) {
         id = connection.getId();
      } else if (outbound.getConnectionDefinition().size() == 1) {
         id = connectorModule.getModuleId();
      } else {
         id = connectorModule.getModuleId() + "-" + connection.getConnectionFactoryInterface();
      }

      return id;
   }

   private String getId(ConnectorModule connectorModule) {
      String id = connectorModule.getConnector().getResourceAdapter().getId();
      if (id == null) {
         id = connectorModule.getModuleId() + "RA";
      }

      return id;
   }

   private void buildPersistenceModules(AppModule appModule, AppInfo appInfo) {
      for(PersistenceModule persistenceModule : appModule.getPersistenceModules()) {
         String rootUrl = persistenceModule.getRootUrl();
         Persistence persistence = persistenceModule.getPersistence();

         for(PersistenceUnit persistenceUnit : persistence.getPersistenceUnit()) {
            PersistenceUnitInfo info = new PersistenceUnitInfo();
            info.id = appModule.persistenceUnitId(rootUrl, persistenceUnit.getName());
            info.name = persistenceUnit.getName();
            info.watchedResources.addAll(persistenceModule.getWatchedResources());
            info.persistenceUnitRootUrl = rootUrl;
            info.provider = persistenceUnit.getProvider();
            info.transactionType = persistenceUnit.getTransactionType().toString();
            info.webappName = this.findRelatedWebApp(appModule, rootUrl);
            Boolean excludeUnlistedClasses = persistenceUnit.isExcludeUnlistedClasses();
            info.excludeUnlistedClasses = persistenceUnit.isScanned() || excludeUnlistedClasses != null && excludeUnlistedClasses;
            info.jtaDataSource = persistenceUnit.getJtaDataSource();
            info.nonJtaDataSource = persistenceUnit.getNonJtaDataSource();
            info.jarFiles.addAll(persistenceUnit.getJarFile());
            info.classes.addAll(persistenceUnit.getClazz());
            info.mappingFiles.addAll(persistenceUnit.getMappingFile());
            info.persistenceXMLSchemaVersion = persistence.getVersion();
            info.sharedCacheMode = persistenceUnit.getSharedCacheMode().toString();
            info.validationMode = persistenceUnit.getValidationMode().toString();
            info.properties.putAll(persistenceUnit.getProperties());
            AppInfoBuilder.PersistenceProviderProperties.apply(appModule, info);
            appInfo.persistenceUnits.add(info);
         }
      }

   }

   private String findRelatedWebApp(AppModule appModule, String rootUrl) {
      for(WebModule webModule : appModule.getWebModules()) {
         List<URL> pXmls = (List)webModule.getAltDDs().get("ear-webapp-persistence-xml-jars");
         if (pXmls != null) {
            for(URL url : pXmls) {
               if (url.toExternalForm().contains(rootUrl)) {
                  return webModule.getModuleId();
               }
            }
         }
      }

      return null;
   }

   private static String getClientModuleId(ClientModule clientModule) {
      return clientModule.getModuleId();
   }

   private List<PortInfo> configureWebservices(Webservices webservices) {
      List<PortInfo> portMap = new ArrayList();
      if (webservices == null) {
         return portMap;
      } else {
         for(WebserviceDescription desc : webservices.getWebserviceDescription()) {
            String wsdlFile = desc.getWsdlFile();
            String serviceName = desc.getWebserviceDescriptionName();

            for(PortComponent port : desc.getPortComponent()) {
               PortInfo portInfo = new PortInfo();
               ServiceImplBean serviceImplBean = port.getServiceImplBean();
               portInfo.serviceId = desc.getId();
               portInfo.portId = port.getId();
               portInfo.serviceLink = serviceImplBean.getEjbLink();
               if (portInfo.serviceLink == null) {
                  portInfo.serviceLink = serviceImplBean.getServletLink();
               }

               portInfo.seiInterfaceName = port.getServiceEndpointInterface();
               portInfo.portName = port.getPortComponentName();
               portInfo.binding = port.getProtocolBinding();
               portInfo.serviceName = serviceName;
               portInfo.wsdlFile = wsdlFile;
               portInfo.mtomEnabled = port.isEnableMtom();
               portInfo.wsdlPort = port.getWsdlPort();
               portInfo.wsdlService = port.getWsdlService();
               portInfo.location = port.getLocation();
               List<HandlerChainInfo> handlerChains = ConfigurationFactory.toHandlerChainInfo(port.getHandlerChains());
               portInfo.handlerChains.addAll(handlerChains);
               portMap.add(portInfo);
            }
         }

         return portMap;
      }
   }

   void configureWebserviceSecurity(EjbJarInfo ejbJarInfo, EjbModule ejbModule) {
      Object altDD = ejbModule.getOpenejbJar();
      List<PortInfo> infoList = ejbJarInfo.portInfos;
      this.configureWebserviceScurity(infoList, altDD);
   }

   private void configureWebserviceScurity(List<PortInfo> infoList, Object altDD) {
      if (altDD != null && altDD instanceof OpenejbJar) {
         OpenejbJar openejbJar = (OpenejbJar)altDD;
         Map<String, EjbDeployment> deploymentsByEjbName = openejbJar.getDeploymentsByEjbName();

         for(PortInfo portInfo : infoList) {
            EjbDeployment deployment = (EjbDeployment)deploymentsByEjbName.get(portInfo.serviceLink);
            if (deployment != null) {
               portInfo.realmName = deployment.getProperties().getProperty("webservice.security.realm");
               portInfo.securityRealmName = deployment.getProperties().getProperty("webservice.security.securityRealm");
               if (deployment.getProperties().getProperty("webservice.security.transportGarantee") != null) {
                  portInfo.transportGuarantee = deployment.getProperties().getProperty("webservice.security.transportGarantee");
               } else {
                  portInfo.transportGuarantee = "NONE";
               }

               if (deployment.getProperties().getProperty("webservice.security.authMethod") != null) {
                  portInfo.authMethod = deployment.getProperties().getProperty("webservice.security.authMethod");
               } else {
                  portInfo.authMethod = "NONE";
               }

               portInfo.properties = deployment.getProperties();
            }
         }

      }
   }

   private static boolean skipMdb(EnterpriseBeanInfo bean) {
      return bean instanceof MessageDrivenBeanInfo && SystemInstance.get().hasProperty("openejb.geronimo");
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, "org.apache.openejb.util.resources");
      USE_EAR_AS_CONTEXT_ROOT_BASE = SystemInstance.get().getOptions().get("openejb.ear.use-as-webcontext-base", false);
   }

   public static class PersistenceProviderProperties {
      public static final String OPENJPA_RUNTIME_UNENHANCED_CLASSES = "openjpa.RuntimeUnenhancedClasses";
      public static final String DEFAULT_RUNTIME_UNENHANCED_CLASSES = "supported";
      public static final String REMOVE_DEFAULT_RUNTIME_UNENHANCED_CLASSES = "disable";
      public static final String TABLE_PREFIX = "openejb.jpa.table_prefix";
      public static final String OPENJPA_METADATA_REPOSITORY = "openjpa.MetaDataRepository";
      public static final String PREFIX_METADATA_REPOSITORY = "org.apache.openejb.openjpa.PrefixMappingRepository";
      public static final String OPENJPA_SEQUENCE = "openjpa.Sequence";
      public static final String PREFIX_SEQUENCE = "org.apache.openejb.openjpa.PrefixTableJdbcSeq";
      public static final String PROVIDER_PROP = "javax.persistence.provider";
      public static final String TRANSACTIONTYPE_PROP = "javax.persistence.transactionType";
      public static final String JTADATASOURCE_PROP = "javax.persistence.jtaDataSource";
      public static final String NON_JTADATASOURCE_PROP = "javax.persistence.nonJtaDataSource";
      private static final String DEFAULT_PERSISTENCE_PROVIDER;
      public static final String FORCE_PROVIDER_ENV = "openejb.jpa.force.javax.persistence.provider";
      public static final String HIBERNATE_TRANSACTION_MANAGER_LOOKUP_CLASS = "hibernate.transaction.manager_lookup_class";
      public static final String HIBERNATE_JTA_PLATFORM = "hibernate.transaction.jta.platform";
      public static final String HIBERNATE_EJB_NAMING_STRATEGY_PROP = "hibernate.ejb.naming_strategy";
      private static final String HIBERNATE_EJB_NAMING_STRATEGY = "org.apache.openejb.jpa.integration.hibernate.PrefixNamingStrategy";
      private static final String ECLIPSELINK_SESSION_CUSTOMIZER = "eclipselink.session.customizer";
      private static final String ECLIPSELINK_TARGET_SERVER = "eclipselink.target-server";
      private static final String PREFIX_SESSION_CUSTOMIZER = "org.apache.openejb.jpa.integration.eclipselink.PrefixSessionCustomizer";
      private static final String OPENEJB_TARGET_SERVER = "org.apache.openejb.jpa.integration.eclipselink.OpenEJBServerPlatform";
      private static final String providerEnv;
      private static final boolean forceProviderEnv;
      private static final String transactionTypeEnv;
      private static final String jtaDataSourceEnv;
      private static final String nonJtaDataSourceEnv;

      private static void apply(AppModule appModule, PersistenceUnitInfo info) {
         ClassLoader classLoader = appModule.getClassLoader();
         overrideFromSystemProp(info);
         if (!"org.hibernate.ejb.HibernatePersistence".equals(info.provider) && !"org.hibernate.jpa.HibernatePersistenceProvider".equals(info.provider)) {
            if (!"oracle.toplink.essentials.PersistenceProvider".equals(info.provider) && !"oracle.toplink.essentials.ejb.cmp3.EntityManagerFactoryProvider".equals(info.provider)) {
               if ("org.eclipse.persistence.jpa.PersistenceProvider".equals(info.provider) || "org.eclipse.persistence.jpa.osgi.PersistenceProvider".equals(info.provider)) {
                  override(appModule.getProperties(), info, "eclipselink");
                  String className = info.properties.getProperty("eclipselink.target-server");
                  if (className == null || className.startsWith("org.eclipse.persistence.transaction")) {
                     if (classLoader.getResource(ClassLoaderUtil.resourceName("org.apache.openejb.jpa.integration.eclipselink.OpenEJBServerPlatform")) != null) {
                        info.properties.setProperty("eclipselink.target-server", "org.apache.openejb.jpa.integration.eclipselink.OpenEJBServerPlatform");
                        AppInfoBuilder.logger.debug("Adjusting PersistenceUnit(name=" + info.name + ") property to " + "eclipselink.target-server" + "=" + "org.apache.openejb.jpa.integration.eclipselink.OpenEJBServerPlatform");
                     } else {
                        AppInfoBuilder.logger.debug("Can't adjusting PersistenceUnit(name=" + info.name + ") property to " + "eclipselink.target-server" + "=" + "org.apache.openejb.jpa.integration.eclipselink.OpenEJBServerPlatform" + ", using default one");
                     }
                  }

                  String prefix = info.properties.getProperty("openejb.jpa.table_prefix");
                  if (prefix != null) {
                     if (info.properties.containsKey("eclipselink.session.customizer")) {
                        AppInfoBuilder.logger.warning("can't statisfy table prefix since you provided a eclipselink.session.customizer property, add a call to org.apache.openejb.jpa.integration.eclipselink.PrefixSessionCustomizer");
                     } else {
                        info.properties.setProperty("openejb.jpa.init-entitymanager", "true");
                        info.properties.setProperty("eclipselink.session.customizer", "org.apache.openejb.jpa.integration.eclipselink.PrefixSessionCustomizer");
                     }
                  }

                  for(String key : Collections.singletonList("eclipselink.jdbc.sequence-connection-pool.non-jta-data-source")) {
                     String ds = info.properties.getProperty(key);
                     if (ds != null && !ds.contains(":")) {
                        info.properties.setProperty(key, "java:openejb/Resource/" + ds);
                     }
                  }
               } else if (info.provider == null || "org.apache.openjpa.persistence.PersistenceProviderImpl".equals(info.provider)) {
                  override(appModule.getProperties(), info, "openjpa");
                  String existing = info.properties.getProperty("openjpa.RuntimeUnenhancedClasses");
                  if (existing == null) {
                     info.properties.setProperty("openjpa.RuntimeUnenhancedClasses", "supported");
                     AppInfoBuilder.logger.debug("Adjusting PersistenceUnit(name=" + info.name + ") property to " + "openjpa.RuntimeUnenhancedClasses" + "=" + "supported");
                  } else if ("disable".equals(existing.trim())) {
                     info.properties.remove("openjpa.RuntimeUnenhancedClasses");
                     AppInfoBuilder.logger.info("Adjusting PersistenceUnit(name=" + info.name + ") removing property " + "openjpa.RuntimeUnenhancedClasses");
                  }

                  String prefix = info.properties.getProperty("openejb.jpa.table_prefix");
                  if (prefix != null) {
                     String mapping = info.properties.getProperty("openjpa.MetaDataRepository");
                     if (mapping != null && !"org.apache.openjpa.jdbc.meta.MappingRepository".equals(mapping)) {
                        throw new OpenEJBRuntimeException("can't honor table prefixes since you provided a custom mapping repository: " + mapping);
                     }

                     info.properties.setProperty("openjpa.MetaDataRepository", "org.apache.openejb.openjpa.PrefixMappingRepository(prefix=" + prefix + ")");
                     if (!info.properties.containsKey("openjpa.Sequence")) {
                        info.properties.setProperty("openjpa.Sequence", "org.apache.openejb.openjpa.PrefixTableJdbcSeq(prefix=" + prefix + ")");
                     } else {
                        AppInfoBuilder.logger.warning("you configured a custom sequence so the prefix will be ignored");
                     }
                  }

                  for(String key : new HashSet(info.properties.stringPropertyNames())) {
                     if (key.matches("openjpa.Connection(DriverName|URL|UserName|Password)")) {
                        Object o = info.properties.remove(key);
                        AppInfoBuilder.logger.warning("Removing PersistenceUnit(name=" + info.name + ") property " + key + "=" + o + "  [not valid in a container environment]");
                     } else {
                        JPAPropertyConverter.Pair pair = JPAPropertyConverter.toOpenJPAValue(key, info.properties.getProperty(key), info.properties);
                        if (pair != null && !info.properties.containsKey(pair.getKey())) {
                           AppInfoBuilder.logger.info("Converting PersistenceUnit(name=" + info.name + ") property " + key + "=" + info.properties.getProperty(key) + " to " + pair.toString());
                           info.properties.remove(key);
                           info.properties.setProperty(pair.getKey(), pair.getValue());
                        }
                     }
                  }
               }
            } else {
               override(appModule.getProperties(), info, "toplink");
               String lookupProperty = "toplink.target-server";
               String openejbLookupClass = "org.apache.openejb.toplink.JTATransactionController";
               String prefix = info.properties.getProperty("openejb.jpa.table_prefix");
               if (prefix != null) {
                  AppInfoBuilder.logger.warning("table prefix feature is not supported for toplink");
               }

               String className = info.properties.getProperty("toplink.target-server");
               if (className == null || className.startsWith("oracle.toplink.transaction")) {
                  info.properties.setProperty("toplink.target-server", "org.apache.openejb.toplink.JTATransactionController");
                  AppInfoBuilder.logger.debug("Adjusting PersistenceUnit(name=" + info.name + ") property to " + "toplink.target-server" + "=" + "org.apache.openejb.toplink.JTATransactionController");
               }
            }
         } else {
            override(appModule.getProperties(), info, "hibernate");
            String className = info.properties.getProperty("hibernate.transaction.jta.platform");
            if (className == null) {
               className = info.properties.getProperty("hibernate.transaction.manager_lookup_class");
            }

            String prefix = info.properties.getProperty("openejb.jpa.table_prefix");
            if (prefix != null) {
               if (info.properties.containsKey("hibernate.ejb.naming_strategy")) {
                  AppInfoBuilder.logger.warning("can't statisfy table prefix since you provided a hibernate.ejb.naming_strategy property");
               } else {
                  info.properties.setProperty("hibernate.ejb.naming_strategy", "org.apache.openejb.jpa.integration.hibernate.PrefixNamingStrategy");
               }
            }

            if (className == null || className.startsWith("org.hibernate.transaction") || className.startsWith("org.hibernate.service.jta.platform")) {
               String key = "hibernate.transaction.jta.platform";
               String value = "org.apache.openejb.hibernate.OpenEJBJtaPlatform2";
               if (classLoader.getResource(ClassLoaderUtil.resourceName("org.hibernate.engine.transaction.jta.platform.spi.JtaPlatform")) == null) {
                  value = "org.apache.openejb.hibernate.OpenEJBJtaPlatform";
                  if (classLoader.getResource(ClassLoaderUtil.resourceName("org.hibernate.service.jta.platform.spi.JtaPlatform")) == null) {
                     key = "hibernate.transaction.manager_lookup_class";
                     value = "org.apache.openejb.hibernate.TransactionManagerLookup";
                  }
               }

               if (classLoader.getResource(ClassLoaderUtil.resourceName(value)) != null) {
                  info.properties.setProperty(key, value);
                  AppInfoBuilder.logger.debug("Adjusting PersistenceUnit(name=" + info.name + ") property to " + key + "=" + value);
               } else {
                  AppInfoBuilder.logger.debug("can't adjust hibernate jta bridge to openejb one");
               }
            }
         }

         override(appModule.getProperties(), info);

         for(String key : Arrays.asList("javax.persistence.jtaDataSource", "javax.persistence.nonJtaDataSource")) {
            String ds = info.properties.getProperty(key);
            if (ds != null && !ds.contains(":")) {
               info.properties.setProperty(key, "java:openejb/Resource/" + ds);
            }
         }

      }

      private static void overrideFromSystemProp(PersistenceUnitInfo info) {
         if (providerEnv != null && (info.provider == null || forceProviderEnv)) {
            info.provider = providerEnv;
         }

         if (info.provider == null) {
            info.provider = DEFAULT_PERSISTENCE_PROVIDER;
         }

         if (jtaDataSourceEnv != null) {
            info.jtaDataSource = jtaDataSourceEnv;
         }

         if (transactionTypeEnv != null) {
            info.transactionType = transactionTypeEnv.toUpperCase();
         }

         if (nonJtaDataSourceEnv != null) {
            info.nonJtaDataSource = nonJtaDataSourceEnv;
         }

      }

      private static void override(Properties appProperties, PersistenceUnitInfo info) {
         override(appProperties, info, info.name);
      }

      private static void override(Properties appProperties, PersistenceUnitInfo info, String prefix) {
         Properties propertiesToCheckForOverridings = new Properties();
         propertiesToCheckForOverridings.putAll(appProperties);
         propertiesToCheckForOverridings.putAll(JavaSecurityManagers.getSystemProperties());
         propertiesToCheckForOverridings.putAll(SystemInstance.get().getProperties());
         Properties overrides = ConfigurationFactory.getOverrides(propertiesToCheckForOverridings, prefix, "PersistenceUnit");

         for(Map.Entry<Object, Object> entry : overrides.entrySet()) {
            String property = (String)(prefix.equalsIgnoreCase(info.name) ? entry.getKey() : prefix + "." + entry.getKey());
            String value = (String)entry.getValue();
            if ("openjpa.Log".equals(property) && "org.apache.openejb.openjpa.JULOpenJPALogFactory".equals(value)) {
               if (info.properties.containsKey("openjpa.Log") || !appProperties.containsKey("openjpa.Log")) {
                  continue;
               }

               value = appProperties.getProperty(property, value);
            }

            if (info.properties.containsKey(property)) {
               AppInfoBuilder.logger.debug("Overriding persistence-unit " + info.name + " property " + property + "=" + value);
            } else {
               AppInfoBuilder.logger.debug("Adding persistence-unit " + info.name + " property " + property + "=" + value);
            }

            info.properties.put(property, value);
            if (property.endsWith("openjpa.Specification")) {
               info.persistenceXMLSchemaVersion = value.replace("JPA ", "");
            }
         }

      }

      static {
         DEFAULT_PERSISTENCE_PROVIDER = PersistenceBootstrap.DEFAULT_PROVIDER;
         providerEnv = SystemInstance.get().getOptions().get("javax.persistence.provider", (String)null);
         forceProviderEnv = SystemInstance.get().getOptions().get("openejb.jpa.force.javax.persistence.provider", true);
         transactionTypeEnv = SystemInstance.get().getOptions().get("javax.persistence.transactionType", (String)null);
         jtaDataSourceEnv = SystemInstance.get().getOptions().get("javax.persistence.jtaDataSource", (String)null);
         nonJtaDataSourceEnv = SystemInstance.get().getOptions().get("javax.persistence.nonJtaDataSource", (String)null);
      }
   }
}
