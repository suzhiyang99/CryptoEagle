package org.apache.openejb.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import javax.annotation.ManagedBean;
import javax.ejb.TimerService;
import javax.enterprise.inject.spi.BeanManager;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.UserTransaction;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;
import org.apache.openejb.JndiConstants;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.assembler.classic.ContainerInfo;
import org.apache.openejb.assembler.classic.ResourceInfo;
import org.apache.openejb.config.sys.Container;
import org.apache.openejb.config.sys.Resource;
import org.apache.openejb.jee.ActivationConfig;
import org.apache.openejb.jee.ActivationConfigProperty;
import org.apache.openejb.jee.AdminObject;
import org.apache.openejb.jee.AssemblyDescriptor;
import org.apache.openejb.jee.ConnectionDefinition;
import org.apache.openejb.jee.Connector;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EntityBean;
import org.apache.openejb.jee.InboundResourceadapter;
import org.apache.openejb.jee.InjectionTarget;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.JndiReference;
import org.apache.openejb.jee.MessageDestination;
import org.apache.openejb.jee.MessageDestinationRef;
import org.apache.openejb.jee.MessageDrivenBean;
import org.apache.openejb.jee.MessageListener;
import org.apache.openejb.jee.OutboundResourceAdapter;
import org.apache.openejb.jee.PersistenceContextRef;
import org.apache.openejb.jee.PersistenceRef;
import org.apache.openejb.jee.PersistenceType;
import org.apache.openejb.jee.ResourceAdapter;
import org.apache.openejb.jee.ResourceRef;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.SessionType;
import org.apache.openejb.jee.jpa.unit.Persistence;
import org.apache.openejb.jee.jpa.unit.PersistenceUnit;
import org.apache.openejb.jee.jpa.unit.TransactionType;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.jee.oejb3.ResourceLink;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.resource.jdbc.DataSourceFactory;
import org.apache.openejb.util.IntrospectionSupport;
import org.apache.openejb.util.Join;
import org.apache.openejb.util.LinkResolver;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.PropertyPlaceHolderHelper;
import org.apache.openejb.util.SuperProperties;
import org.apache.openejb.util.URISupport;
import org.apache.openejb.util.URLs;

public class AutoConfig implements DynamicDeployer, JndiConstants {
   public static final String ORIGIN_ANNOTATION = "Annotation";
   public static final String ORIGIN_FLAG = "Origin";
   public static final String ORIGINAL_ID = "OriginalId";
   private static final AppResources EMPTY_APP_RESOURCES = new AppResources();
   public static Logger logger;
   private static final int MAX_IMPLICIT_POOL_SIZE = 5;
   private static final Set<String> ignoredReferenceTypes;
   public static final String AUTOCREATE_JTA_DATASOURCE_FROM_NON_JTA_ONE_KEY = "openejb.autocreate.jta-datasource-from-non-jta-one";
   private final ConfigurationFactory configFactory;
   private boolean autoCreateContainers = true;
   private boolean autoCreateResources = true;

   public AutoConfig(ConfigurationFactory configFactory) {
      this.configFactory = configFactory;
   }

   public synchronized boolean autoCreateResources() {
      return this.autoCreateResources;
   }

   public synchronized void autoCreateResources(boolean autoCreateResources) {
      this.autoCreateResources = autoCreateResources;
   }

   public synchronized boolean autoCreateContainers() {
      return this.autoCreateContainers;
   }

   public synchronized void autoCreateContainers(boolean autoCreateContainers) {
      this.autoCreateContainers = autoCreateContainers;
   }

   public void init() throws OpenEJBException {
   }

   public synchronized AppModule deploy(AppModule appModule) throws OpenEJBException {
      AppResources appResources = new AppResources(appModule);
      appResources.dump();
      this.processApplicationResources(appModule);
      this.processApplicationContainers(appModule, appResources);

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         this.processActivationConfig(ejbModule);
      }

      this.resolveDestinationLinks(appModule);
      this.resolvePersistenceRefs(appModule);

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         this.deploy(ejbModule, appResources);
      }

      for(ClientModule clientModule : appModule.getClientModules()) {
         this.deploy(clientModule, appResources);
      }

      for(WebModule webModule : appModule.getWebModules()) {
         this.deploy(webModule, appResources);
      }

      for(PersistenceModule persistenceModule : appModule.getPersistenceModules()) {
         this.deploy(appModule, persistenceModule);
      }

      return appModule;
   }

   private void resolvePersistenceRefs(AppModule appModule) {
      LinkResolver<PersistenceUnit> persistenceUnits = new PersistenceUnitLinkResolver(appModule);

      for(PersistenceModule module : appModule.getPersistenceModules()) {
         String rootUrl = module.getRootUrl();

         for(PersistenceUnit unit : module.getPersistence().getPersistenceUnit()) {
            unit.setId(appModule.persistenceUnitId(rootUrl, unit.getName()));
            persistenceUnits.add((String)rootUrl, unit.getName(), unit);
         }
      }

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         URI moduleURI = ejbModule.getModuleUri();

         for(JndiConsumer component : ejbModule.getEjbJar().getEnterpriseBeans()) {
            this.processPersistenceRefs(component, ejbModule, persistenceUnits, moduleURI);
         }
      }

      for(ClientModule clientModule : appModule.getClientModules()) {
         URI moduleURI = URLs.uri(clientModule.getModuleId());
         this.processPersistenceRefs(clientModule.getApplicationClient(), clientModule, persistenceUnits, moduleURI);
      }

      for(WebModule webModule : appModule.getWebModules()) {
         URI moduleURI = URLs.uri(webModule.getModuleId());
         this.processPersistenceRefs(webModule.getWebApp(), webModule, persistenceUnits, moduleURI);
      }

   }

   private void processPersistenceRefs(JndiConsumer component, DeploymentModule module, LinkResolver<PersistenceUnit> persistenceUnits, URI moduleURI) {
      String componentName = component.getJndiConsumerName();
      ValidationContext validation = module.getValidation();

      for(PersistenceRef ref : component.getPersistenceUnitRef()) {
         this.processPersistenceRef(persistenceUnits, ref, moduleURI, componentName, validation);
      }

      for(PersistenceRef ref : component.getPersistenceContextRef()) {
         this.processPersistenceRef(persistenceUnits, ref, moduleURI, componentName, validation);
      }

   }

   private PersistenceUnit processPersistenceRef(LinkResolver<PersistenceUnit> persistenceUnits, PersistenceRef ref, URI moduleURI, String componentName, ValidationContext validation) {
      if (ref.getMappedName() != null && ref.getMappedName().startsWith("jndi:")) {
         return null;
      } else {
         PersistenceUnit unit = (PersistenceUnit)persistenceUnits.resolveLink(ref.getPersistenceUnitName(), moduleURI);
         Collection<PersistenceUnit> cmpUnits = persistenceUnits.values("cmp");
         if (unit == null && cmpUnits.size() > 0 && persistenceUnits.values().size() - cmpUnits.size() == 1) {
            for(PersistenceUnit persistenceUnit : persistenceUnits.values()) {
               if (!persistenceUnit.getName().equals("cmp")) {
                  unit = persistenceUnit;
                  break;
               }
            }
         }

         if (unit == null) {
            unit = (PersistenceUnit)persistenceUnits.resolveLink(ref.getName(), moduleURI);
         }

         if (unit == null) {
            String shortName = ref.getName().replaceFirst(".*/", "");
            unit = (PersistenceUnit)persistenceUnits.resolveLink(shortName, moduleURI);
         }

         if (unit != null) {
            ref.setPersistenceUnitName(unit.getName());
            ref.setMappedName(unit.getId());
         } else {
            String refType = "persistence";
            if (ref instanceof PersistenceContextRef) {
               refType = refType + "ContextRef";
            } else {
               refType = refType + "UnitRef";
            }

            String refShortName = ref.getName();
            if (refShortName.matches(".*\\..*/.*")) {
               refShortName = refShortName.replaceFirst(".*/", "");
            }

            List<String> availableUnits = new ArrayList();

            for(PersistenceUnit persistenceUnit : persistenceUnits.values()) {
               availableUnits.add(persistenceUnit.getName());
            }

            Collections.sort(availableUnits);
            String unitName = ref.getPersistenceUnitName();
            if (availableUnits.size() == 0) {
               if (unitName == null) {
                  unitName = refShortName;
               }

               validation.fail(componentName, refType + ".noPersistenceUnits", refShortName, unitName);
            } else if ((ref.getPersistenceUnitName() == null || ref.getPersistenceUnitName().length() == 0) && availableUnits.size() > 1) {
               String sampleUnitName = (String)availableUnits.get(0);
               validation.fail(componentName, refType + ".noUnitName", refShortName, Join.join(", ", (Collection)availableUnits), sampleUnitName);
            } else {
               Collection<PersistenceUnit> vagueMatches = persistenceUnits.values(ref.getPersistenceUnitName());
               if (vagueMatches.size() != 0) {
                  List<String> possibleUnits = new ArrayList();

                  for(PersistenceUnit persistenceUnit : persistenceUnits.values()) {
                     try {
                        URI unitURI = URLs.uri(persistenceUnit.getId());
                        unitURI = URISupport.relativize(moduleURI, unitURI);
                        possibleUnits.add(unitURI.toString());
                     } catch (Exception var17) {
                        possibleUnits.add(persistenceUnit.getId());
                     }
                  }

                  Collections.sort(possibleUnits);
                  validation.fail(componentName, refType + ".vagueMatches", refShortName, unitName, possibleUnits.size(), Join.join("\n", (Collection)possibleUnits));
               } else {
                  validation.fail(componentName, refType + ".noMatches", refShortName, unitName, Join.join(", ", (Collection)availableUnits));
               }
            }
         }

         return unit;
      }
   }

   private void processActivationConfig(EjbModule ejbModule) throws OpenEJBException {
      OpenejbJar openejbJar;
      if (ejbModule.getOpenejbJar() != null) {
         openejbJar = ejbModule.getOpenejbJar();
      } else {
         openejbJar = new OpenejbJar();
         ejbModule.setOpenejbJar(openejbJar);
      }

      Map<String, EjbDeployment> deployments = openejbJar.getDeploymentsByEjbName();

      for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
         if (bean instanceof MessageDrivenBean) {
            MessageDrivenBean mdb = (MessageDrivenBean)bean;
            if (mdb.getActivationConfig() == null) {
               mdb.setActivationConfig(new ActivationConfig());
            }

            if (this.isJms(mdb)) {
               EjbDeployment ejbDeployment = (EjbDeployment)deployments.get(bean.getEjbName());
               if (ejbDeployment == null) {
                  throw new OpenEJBException("No ejb deployment found for ejb " + bean.getEjbName());
               }

               Properties properties = mdb.getActivationConfig().toProperties();
               String destination = properties.getProperty("destinationName", properties.getProperty("destinationLookup"));
               if (destination != null) {
                  if (destination.startsWith("openejb:Resource/")) {
                     destination = destination.substring("openejb:Resource/".length());
                  }

                  if (destination.startsWith("java:openejb/Resource/")) {
                     destination = destination.substring("java:openejb/Resource/".length());
                  }

                  mdb.getActivationConfig().addProperty("destination", destination);
                  List<ActivationConfigProperty> list = mdb.getActivationConfig().getActivationConfigProperty();
                  Iterator<ActivationConfigProperty> iterator = list.iterator();

                  while(iterator.hasNext()) {
                     ActivationConfigProperty configProperty = (ActivationConfigProperty)iterator.next();
                     String activationConfigPropertyName = configProperty.getActivationConfigPropertyName();
                     if (activationConfigPropertyName.equals("destinationName") || activationConfigPropertyName.equals("destinationLookup")) {
                        iterator.remove();
                        break;
                     }
                  }
               } else {
                  destination = properties.getProperty("destination");
               }

               if (destination == null) {
                  destination = properties.getProperty("destinationLookup");
               }

               if (destination == null) {
                  destination = ejbDeployment.getDeploymentId();
                  mdb.getActivationConfig().addProperty("destination", destination);
               }

               ResourceLink link = ejbDeployment.getResourceLink("openejb/destination");
               if (link == null && mdb.getMessageDestinationLink() == null) {
                  link = new ResourceLink();
                  link.setResId(destination);
                  link.setResRefName("openejb/destination");
                  ejbDeployment.addResourceLink(link);
               }

               String destinationType = properties.getProperty("destinationType");
               if (destinationType == null && mdb.getMessageDestinationType() != null) {
                  destinationType = mdb.getMessageDestinationType();
                  mdb.getActivationConfig().addProperty("destinationType", destinationType);
               }

               if (mdb.getMessageDestinationType() == null) {
                  mdb.setMessageDestinationType(destinationType);
               }

               if ("javax.jms.Topic".equals(destinationType)) {
                  if (Boolean.parseBoolean(SystemInstance.get().getProperty("openejb.activemq.deploymentId-as-clientId", ejbModule.getProperties().getProperty("openejb.activemq.deploymentId-as-clientId", "true"))) && !properties.containsKey("clientId")) {
                     mdb.getActivationConfig().addProperty("clientId", ejbDeployment.getDeploymentId());
                  }

                  if (!properties.containsKey("subscriptionName")) {
                     mdb.getActivationConfig().addProperty("subscriptionName", ejbDeployment.getDeploymentId() + "_subscription");
                  }
               }
            }
         }
      }

   }

   private boolean isJms(MessageDrivenBean mdb) {
      String messagingType = mdb.getMessagingType();
      return messagingType != null && messagingType.startsWith("javax.jms");
   }

   private void resolveDestinationLinks(AppModule appModule) throws OpenEJBException {
      LinkResolver<MessageDestination> destinationResolver = new LinkResolver<MessageDestination>();

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         AssemblyDescriptor assembly = ejbModule.getEjbJar().getAssemblyDescriptor();
         if (assembly != null) {
            for(MessageDestination destination : assembly.getMessageDestination()) {
               destinationResolver.add((URI)ejbModule.getModuleUri(), destination.getMessageDestinationName(), destination);
            }
         }
      }

      for(ClientModule clientModule : appModule.getClientModules()) {
         for(MessageDestination destination : clientModule.getApplicationClient().getMessageDestination()) {
            destinationResolver.add((URI)appModule.getModuleUri(), destination.getMessageDestinationName(), destination);
         }
      }

      for(WebModule webModule : appModule.getWebModules()) {
         for(MessageDestination destination : webModule.getWebApp().getMessageDestination()) {
            destinationResolver.add((URI)appModule.getModuleUri(), destination.getMessageDestinationName(), destination);
         }
      }

      Map<MessageDestination, String> destinationTypes = new HashMap();

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         AssemblyDescriptor assembly = ejbModule.getEjbJar().getAssemblyDescriptor();
         if (assembly != null) {
            URI moduleUri = ejbModule.getModuleUri();
            OpenejbJar openejbJar = ejbModule.getOpenejbJar();

            for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
               if (bean instanceof MessageDrivenBean) {
                  MessageDrivenBean mdb = (MessageDrivenBean)bean;
                  EjbDeployment ejbDeployment = (EjbDeployment)openejbJar.getDeploymentsByEjbName().get(bean.getEjbName());
                  if (ejbDeployment == null) {
                     throw new OpenEJBException("No ejb deployment found for ejb " + bean.getEjbName());
                  }

                  String link = mdb.getMessageDestinationLink();
                  if (link != null && link.length() != 0) {
                     MessageDestination destination = (MessageDestination)destinationResolver.resolveLink(link, moduleUri);
                     if (destination == null) {
                        throw new OpenEJBException("Message destination " + link + " for message driven bean " + mdb.getEjbName() + " not found");
                     }

                     String destinationId = destination.getMappedName();
                     if (destinationId == null) {
                        Properties properties = mdb.getActivationConfig().toProperties();
                        destinationId = properties.getProperty("destination");
                        destination.setMappedName(destinationId);
                     }

                     if (mdb.getMessageDestinationType() != null && !destinationTypes.containsKey(destination)) {
                        destinationTypes.put(destination, mdb.getMessageDestinationType());
                     }

                     ResourceLink resourceLink = ejbDeployment.getResourceLink("openejb/destination");
                     if (resourceLink == null) {
                        resourceLink = new ResourceLink();
                        resourceLink.setResRefName("openejb/destination");
                        ejbDeployment.addResourceLink(resourceLink);
                     }

                     resourceLink.setResId(destinationId);
                  }
               }
            }
         }
      }

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         AssemblyDescriptor assembly = ejbModule.getEjbJar().getAssemblyDescriptor();
         if (assembly != null) {
            URI moduleUri = ejbModule.getModuleUri();
            OpenejbJar openejbJar = ejbModule.getOpenejbJar();

            for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
               EjbDeployment ejbDeployment = (EjbDeployment)openejbJar.getDeploymentsByEjbName().get(bean.getEjbName());
               if (ejbDeployment == null) {
                  throw new OpenEJBException("No ejb deployment found for ejb " + bean.getEjbName());
               }

               for(MessageDestinationRef ref : bean.getMessageDestinationRef()) {
                  if (ref.getMappedName() == null && ejbDeployment.getResourceLink(ref.getName()) == null) {
                     String destinationId = this.resolveDestinationId(ref, moduleUri, destinationResolver, destinationTypes);
                     if (destinationId != null) {
                        ResourceLink resourceLink = new ResourceLink();
                        resourceLink.setResId(destinationId);
                        resourceLink.setResRefName(ref.getName());
                        ejbDeployment.addResourceLink(resourceLink);
                     }
                  }
               }
            }
         }
      }

      for(ClientModule clientModule : appModule.getClientModules()) {
         URI moduleUri = clientModule.getModuleUri();

         for(MessageDestinationRef ref : clientModule.getApplicationClient().getMessageDestinationRef()) {
            String destinationId = this.resolveDestinationId(ref, moduleUri, destinationResolver, destinationTypes);
            if (destinationId != null) {
               ref.setMappedName(destinationId);
            }
         }
      }

      for(WebModule webModule : appModule.getWebModules()) {
         URI moduleUri = URLs.uri(webModule.getModuleId());

         for(MessageDestinationRef ref : webModule.getWebApp().getMessageDestinationRef()) {
            String destinationId = this.resolveDestinationId(ref, moduleUri, destinationResolver, destinationTypes);
            if (destinationId != null) {
               ref.setMappedName(destinationId);
            }
         }
      }

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         AssemblyDescriptor assembly = ejbModule.getEjbJar().getAssemblyDescriptor();
         if (assembly != null) {
            URI moduleUri = URLs.uri(ejbModule.getModuleId());
            OpenejbJar openejbJar = ejbModule.getOpenejbJar();

            for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
               if (bean instanceof MessageDrivenBean) {
                  MessageDrivenBean mdb = (MessageDrivenBean)bean;
                  if (this.isJms(mdb)) {
                     EjbDeployment ejbDeployment = (EjbDeployment)openejbJar.getDeploymentsByEjbName().get(bean.getEjbName());
                     if (ejbDeployment == null) {
                        throw new OpenEJBException("No ejb deployment found for ejb " + bean.getEjbName());
                     }

                     String destinationType = mdb.getMessageDestinationType();
                     if (destinationType == null) {
                        String link = mdb.getMessageDestinationLink();
                        if (link != null && link.length() != 0) {
                           MessageDestination destination = (MessageDestination)destinationResolver.resolveLink(link, moduleUri);
                           if (destination == null) {
                              throw new OpenEJBException("Message destination " + link + " for message driven bean " + mdb.getEjbName() + " not found");
                           }

                           destinationType = (String)destinationTypes.get(destination);
                        }

                        if (destinationType == null) {
                           Properties properties = mdb.getActivationConfig().toProperties();
                           String destination = properties.getProperty("destination").toLowerCase();
                           if (destination.contains("queue")) {
                              destinationType = Queue.class.getName();
                           } else if (destination.contains("topic")) {
                              destinationType = Topic.class.getName();
                           } else {
                              destinationType = Queue.class.getName();
                           }

                           logger.info("Auto-configuring a message driven bean " + ejbDeployment.getDeploymentId() + " destination " + properties.getProperty("destination") + " to be destinationType " + destinationType);
                        }

                        if (destinationType != null) {
                           mdb.getActivationConfig().addProperty("destinationType", destinationType);
                           mdb.setMessageDestinationType(destinationType);
                           if ("javax.jms.Topic".equals(destinationType)) {
                              Properties properties = mdb.getActivationConfig().toProperties();
                              if (!properties.containsKey("clientId")) {
                                 mdb.getActivationConfig().addProperty("clientId", ejbDeployment.getDeploymentId());
                              }

                              if (!properties.containsKey("subscriptionName")) {
                                 mdb.getActivationConfig().addProperty("subscriptionName", ejbDeployment.getDeploymentId() + "_subscription");
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private String resolveDestinationId(MessageDestinationRef ref, URI moduleUri, LinkResolver<MessageDestination> destinationResolver, Map<MessageDestination, String> destinationTypes) throws OpenEJBException {
      String link = ref.getMessageDestinationLink();
      if (link != null && link.length() != 0) {
         MessageDestination destination = (MessageDestination)destinationResolver.resolveLink(link, moduleUri);
         if (destination == null) {
            throw new OpenEJBException("Message destination " + link + " for message-destination-ref " + ref.getMessageDestinationRefName() + " not found");
         } else {
            if (ref.getMessageDestinationType() != null && !destinationTypes.containsKey(destination)) {
               destinationTypes.put(destination, ref.getMessageDestinationType());
            }

            String destinationId = destination.getMappedName();
            if (destinationId == null) {
               destination.getMessageDestinationName();
            }

            return destinationId;
         }
      } else {
         return null;
      }
   }

   private void deploy(ClientModule clientModule, AppResources appResources) throws OpenEJBException {
      this.processJndiRefs(clientModule.getModuleId(), clientModule.getApplicationClient(), appResources, clientModule.getClassLoader());
   }

   private void deploy(WebModule webModule, AppResources appResources) throws OpenEJBException {
      this.processJndiRefs(webModule.getModuleId(), webModule.getWebApp(), appResources, webModule.getClassLoader());
   }

   private void processJndiRefs(String moduleId, JndiConsumer jndiConsumer, AppResources appResources, ClassLoader classLoader) throws OpenEJBException {
      Iterator var5 = jndiConsumer.getResourceRef().iterator();

      while(true) {
         ResourceRef ref;
         String destinationId;
         while(true) {
            if (!var5.hasNext()) {
               for(JndiReference ref : jndiConsumer.getResourceEnvRef()) {
                  if (ref.getLookupName() == null) {
                     String mappedName = ref.getMappedName() == null ? "" : ref.getMappedName();
                     if (!mappedName.startsWith("jndi:")) {
                        String refType = this.getType(ref, classLoader);
                        if (!this.isIgnoredReferenceType(refType, classLoader)) {
                           destinationId = mappedName.length() == 0 ? ref.getName() : mappedName;
                           destinationId = this.getResourceEnvId(moduleId, destinationId, refType, appResources);
                           ref.setMappedName(destinationId);
                        }
                     }
                  }
               }

               for(MessageDestinationRef ref : jndiConsumer.getMessageDestinationRef()) {
                  if (ref.getLookupName() == null) {
                     String mappedName = ref.getMappedName() == null ? "" : ref.getMappedName();
                     if (!mappedName.startsWith("jndi:")) {
                        String destinationId = mappedName.length() == 0 ? ref.getName() : mappedName;
                        destinationId = this.getResourceEnvId(moduleId, destinationId, ref.getType(), appResources);
                        ref.setMappedName(destinationId);
                     }
                  }
               }

               return;
            }

            ref = (ResourceRef)var5.next();
            if (ref.getLookupName() == null) {
               String mappedName = ref.getMappedName() == null ? "" : ref.getMappedName();
               if (!mappedName.startsWith("jndi:")) {
                  String refType = this.getType(ref, classLoader);
                  if (!this.isIgnoredReferenceType(refType, classLoader)) {
                     destinationId = mappedName.length() == 0 ? ref.getName() : mappedName;

                     try {
                        destinationId = this.getResourceId(moduleId, destinationId, refType, appResources);
                        break;
                     } catch (OpenEJBException ex) {
                        if (!(ref instanceof ContextRef)) {
                           throw ex;
                        }
                     }
                  }
               }
            }
         }

         ref.setMappedName(destinationId);
      }
   }

   private boolean isIgnoredReferenceType(String typeName, ClassLoader loader) {
      if (ignoredReferenceTypes.contains(typeName)) {
         return true;
      } else {
         if (loader != null) {
            try {
               Class<?> type = loader.loadClass(typeName);
               return type.isAnnotationPresent(ManagedBean.class);
            } catch (ClassNotFoundException var4) {
            }
         }

         return false;
      }
   }

   private void deploy(EjbModule ejbModule, AppResources appResources) throws OpenEJBException {
      OpenejbJar openejbJar;
      if (ejbModule.getOpenejbJar() != null) {
         openejbJar = ejbModule.getOpenejbJar();
      } else {
         openejbJar = new OpenejbJar();
         ejbModule.setOpenejbJar(openejbJar);
      }

      Map<String, EjbDeployment> deployments = openejbJar.getDeploymentsByEjbName();

      for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
         EjbDeployment ejbDeployment = (EjbDeployment)deployments.get(bean.getEjbName());
         if (ejbDeployment == null) {
            throw new OpenEJBException("No ejb deployment found for ejb " + bean.getEjbName());
         }

         Class<? extends ContainerInfo> containerInfoType = ConfigurationFactory.getContainerInfoType(getType(bean));
         if (ejbDeployment.getContainerId() == null && !skipMdb(bean)) {
            String containerId = this.getUsableContainer(containerInfoType, bean, appResources);
            if (containerId == null) {
               containerId = this.createContainer(containerInfoType, ejbDeployment, bean);
            }

            ejbDeployment.setContainerId(containerId);
         }

         List<String> containerIds = this.configFactory.getContainerIds();
         containerIds.addAll(appResources.getContainerIds());
         if (!containerIds.contains(ejbDeployment.getContainerId()) && !skipMdb(bean)) {
            this.createContainer(containerInfoType, ejbDeployment, bean);
         }

         for(ResourceRef ref : bean.getResourceRef()) {
            this.processResourceRef(ref, ejbDeployment, appResources, ejbModule);
         }

         for(JndiReference ref : bean.getResourceEnvRef()) {
            this.processResourceEnvRef(ref, ejbDeployment, appResources, ejbModule.getClassLoader());
         }

         for(MessageDestinationRef ref : bean.getMessageDestinationRef()) {
            this.processResourceEnvRef(ref, ejbDeployment, appResources, ejbModule.getClassLoader());
         }

         if (this.autoCreateResources && bean instanceof MessageDrivenBean) {
            MessageDrivenBean mdb = (MessageDrivenBean)bean;
            ResourceLink resourceLink = ejbDeployment.getResourceLink("openejb/destination");
            if (resourceLink != null) {
               try {
                  String destinationId = this.getResourceEnvId(bean.getEjbName(), resourceLink.getResId(), mdb.getMessageDestinationType(), appResources);
                  resourceLink.setResId(destinationId);
               } catch (OpenEJBException var15) {
                  ejbDeployment.removeResourceLink("openejb/destination");
               }
            }
         }
      }

   }

   private void processApplicationContainers(AppModule module, AppResources appResources) throws OpenEJBException {
      if (!module.getContainers().isEmpty()) {
         String prefix = module.getModuleId() + "/";

         for(Container container : module.getContainers()) {
            if (container.getId() == null) {
               throw new IllegalStateException("a container can't get a null id: " + container.getType() + " from " + module.getModuleId());
            }

            if (!container.getId().startsWith(prefix)) {
               container.setId(prefix + container.getId());
            }

            ContainerInfo containerInfo = this.configFactory.createContainerInfo(container);
            this.configFactory.install(containerInfo);
            appResources.addContainer(containerInfo);
         }

      }
   }

   private void processApplicationResources(AppModule module) throws OpenEJBException {
      Collection<Resource> resources = module.getResources();
      if (resources.size() != 0) {
         List<JndiConsumer> jndiConsumers = new ArrayList();

         for(WebModule webModule : module.getWebModules()) {
            JndiConsumer consumer = webModule.getWebApp();
            jndiConsumers.add(consumer);
         }

         for(EjbModule ejbModule : module.getEjbModules()) {
            Collections.addAll(jndiConsumers, ejbModule.getEjbJar().getEnterpriseBeans());
         }

         List<ResourceInfo> resourceInfos = new ArrayList();
         Map<ResourceInfo, Resource> resourcesMap = new HashMap(resources.size());

         for(Resource resource : resources) {
            String originalId = PropertyPlaceHolderHelper.value(resource.getId());
            String modulePrefix = module.getModuleId() + "/";
            if (!"/".equals(modulePrefix) && !originalId.startsWith("global") && !originalId.startsWith("/global")) {
               resource.getProperties().setProperty("OriginalId", originalId);
               resource.setId(modulePrefix + this.replaceJavaAndSlash(originalId));
            } else {
               resource.setId(this.replaceJavaAndSlash(originalId));
            }

            resource.setJndi(PropertyPlaceHolderHelper.value(resource.getJndi()));
            Thread thread = Thread.currentThread();
            ClassLoader oldCl = thread.getContextClassLoader();
            thread.setContextClassLoader(module.getClassLoader());

            try {
               resource.getProperties().putAll(PropertyPlaceHolderHelper.holds(resource.getProperties()));
            } finally {
               thread.setContextClassLoader(oldCl);
            }

            Collection<String> aliases = resource.getAliases();
            if (!aliases.isEmpty()) {
               Collection<String> newAliases = new ArrayList();

               for(String s : aliases) {
                  newAliases.add(module.getModuleId() + "/" + s);
               }

               resource.getAliases().clear();
               resource.getAliases().addAll(newAliases);
            }

            Properties properties = resource.getProperties();
            if (DataSource.class.getName().equals(resource.getType()) || DataSource.class.getSimpleName().equals(resource.getType())) {
               DataSourceFactory.trimNotSupportedDataSourceProperties(properties);
            }

            boolean shouldGenerateJdbcUrl = DataSource.class.getName().equals(resource.getType()) && resource.getProperties().containsKey("Origin") && resource.getProperties().getProperty("Origin").equals("Annotation");
            if (shouldGenerateJdbcUrl && properties.get("JdbcUrl") == null) {
               String url = getVendorUrl(properties);
               if (url != null) {
                  properties.put("JdbcUrl", url);
               }
            }

            ResourceInfo resourceInfo = (ResourceInfo)this.configFactory.configureService((Service)resource, ResourceInfo.class);
            resourceInfo.originAppName = module.getModuleId();
            ResourceRef resourceRef = new ResourceRef();
            resourceRef.setResType(chooseType(module.getClassLoader(), resourceInfo, resource.getType()));
            if (shouldGenerateJdbcUrl) {
               properties.remove("Origin");
               resourceRef.setResRefName(this.dataSourceLookupName(resource));
            } else {
               resourceRef.setResRefName("openejb/Resource/" + resourceInfo.id);
            }

            resourceRef.setMappedName(resourceInfo.id);
            ResourceRef strictRef = new ResourceRef("openejb/Resource/" + originalId, resourceRef.getResType(), resourceRef.getResAuth(), resourceRef.getResSharingScope());
            strictRef.setMappedName(resourceInfo.id);

            for(JndiConsumer consumer : jndiConsumers) {
               addResource(consumer, resourceRef);
               if (!"/".equals(modulePrefix)) {
                  addResource(consumer, strictRef);
               }
            }

            resourceInfos.add(resourceInfo);
            resourcesMap.put(resourceInfo, resource);
         }

         resourceInfos = ConfigurationFactory.sort(resourceInfos, module.getModuleId() + "/");

         for(ResourceInfo resourceInfo : resourceInfos) {
            int originalSize = resourceInfo.aliases.size();
            String id = this.installResource(module.getModuleId(), resourceInfo);
            Resource resource = (Resource)resourcesMap.remove(resourceInfo);
            resource.setId(id);
            if (resourceInfo.aliases.size() > originalSize) {
               resource.getAliases().add(resourceInfo.aliases.get(resourceInfo.aliases.size() - 1));
            }
         }

         resourceInfos.clear();
      }
   }

   private static void addResource(JndiConsumer consumer, ResourceRef resourceRef) {
      ResourceRef existing = (ResourceRef)consumer.getResourceRefMap().get(resourceRef.getKey());
      if (existing != null) {
         existing.setMappedName(resourceRef.getMappedName());
      } else {
         consumer.getResourceRef().add(resourceRef);
      }

   }

   private static String chooseType(ClassLoader classLoader, ResourceInfo info, String defaultType) {
      if (info.types != null) {
         for(String type : info.types) {
            if (canLoad(classLoader, type)) {
               return type;
            }
         }
      }

      return info.className != null ? (canLoad(classLoader, info.className) ? info.className : defaultType) : defaultType;
   }

   private static boolean canLoad(ClassLoader classLoader, String type) {
      try {
         classLoader.loadClass(type);
         return true;
      } catch (ClassNotFoundException var3) {
         return false;
      }
   }

   private String dataSourceLookupName(Resource datasource) {
      String jndi = datasource.getJndi();
      if (jndi.startsWith("java:")) {
         return jndi.startsWith("/") ? jndi.substring(1) : jndi;
      } else if (jndi.startsWith("comp/env/")) {
         return "java:" + jndi;
      } else if (jndi.startsWith("module/")) {
         return "java:" + jndi;
      } else if (jndi.startsWith("global/")) {
         return "java:" + jndi;
      } else {
         return jndi.startsWith("app/") ? "java:" + jndi : "java:comp/env/" + jndi;
      }
   }

   private static String getVendorUrl(Properties properties) {
      String driver = properties.getProperty("JdbcDriver");
      String serverName = properties.getProperty("ServerName");
      int port = getInt(properties.get("PortNumber"));
      boolean remote = port != -1;
      String databaseName = properties.getProperty("DatabaseName");
      if (driver != null && !driver.equals("org.hsqldb.jdbcDriver")) {
         if (driver.startsWith("org.apache.derby.jdbc.Embedded")) {
            return String.format("jdbc:derby:%s%s", databaseName, properties.getProperty("connectionAttributes", ";create=true"));
         } else if (driver.equals("org.apache.derby.jdbc.ClientDriver")) {
            return String.format("jdbc:derby://%s:%s/%s%s", serverName, port, databaseName, properties.getProperty("connectionAttributes", ";create=true"));
         } else if (driver.equals("com.mysql.jdbc.Driver")) {
            return String.format("jdbc:mysql://%s:%s/%s", serverName, port, databaseName);
         } else if (driver.equals("com.postgresql.jdbc.Driver")) {
            return String.format("jdbc:postgresql://%s:%s/%s", serverName, port, databaseName);
         } else {
            return driver.equals("oracle.jdbc.OracleDriver") ? String.format("jdbc:oracle:thin:@//%s:%s/%s", serverName, port, databaseName) : null;
         }
      } else {
         return remote ? String.format("jdbc:hsqldb:hsql://%s:%s/%s", serverName, port, databaseName) : String.format("jdbc:hsqldb:mem:%s", databaseName);
      }
   }

   private static int getInt(Object number) {
      try {
         return (Integer)number;
      } catch (Exception var4) {
         try {
            return Integer.parseInt(String.valueOf(number));
         } catch (NumberFormatException var3) {
            return -1;
         }
      }
   }

   private String createContainer(Class<? extends ContainerInfo> containerInfoType, EjbDeployment ejbDeployment, EnterpriseBean bean) throws OpenEJBException {
      if (!this.autoCreateContainers) {
         throw new OpenEJBException("A container of type " + getType(bean) + " must be declared in the configuration file for bean: " + bean.getEjbName());
      } else {
         ContainerInfo containerInfo = (ContainerInfo)this.configFactory.configureService(containerInfoType);
         logger.info("Auto-creating a container for bean " + ejbDeployment.getDeploymentId() + ": Container(type=" + getType(bean) + ", id=" + containerInfo.id + ")");
         String resourceAdapterId = containerInfo.properties.getProperty("ResourceAdapter");
         if (resourceAdapterId != null) {
            String newResourceId = this.getResourceId(ejbDeployment.getDeploymentId(), resourceAdapterId, (String)null, (AppResources)null);
            if (resourceAdapterId.equals(newResourceId)) {
               containerInfo.properties.setProperty("ResourceAdapter", newResourceId);
            }
         }

         this.configFactory.install(containerInfo);
         return containerInfo.id;
      }
   }

   private void processResourceRef(ResourceRef ref, EjbDeployment ejbDeployment, AppResources appResources, EjbModule ejbModule) throws OpenEJBException {
      if (ref.getLookupName() == null) {
         String mappedName = ref.getMappedName() == null ? "" : ref.getMappedName();
         if (!mappedName.startsWith("jndi:")) {
            String refName = ref.getName();
            String refType = this.getType(ref, ejbModule.getClassLoader());
            if (ignoredReferenceTypes.contains(refType)) {
               ResourceInfo resourceInfo = this.configFactory.getResourceInfo(refName.replace("java:", "").replace("comp/env/", ""));
               if (resourceInfo != null) {
                  ref.setMappedName("jndi:" + (resourceInfo.id.startsWith("java:") ? resourceInfo.id : "openejb:Resource/" + resourceInfo.id));
               }

            } else {
               try {
                  Class<?> clazz = ejbModule.getClassLoader().loadClass(refType);
                  if (clazz.isAnnotationPresent(ManagedBean.class)) {
                     return;
                  }
               } catch (Throwable var13) {
               }

               try {
                  ResourceLink link = ejbDeployment.getResourceLink(refName);
                  if (link == null) {
                     String id = mappedName.length() == 0 ? ref.getName() : mappedName;
                     if (id.startsWith("java:")) {
                        id = id.substring("java:".length());
                     }

                     if (id.startsWith("/")) {
                        id = id.substring(1);
                     }

                     try {
                        AppModule appModule = ejbModule.getAppModule();
                        if (appModule != null) {
                           String newId = this.findResourceId(appModule.getModuleId() + '/' + id.replace("java:", "").replaceAll("^comp/env/", ""), refType, new Properties(), appResources);
                           if (newId != null) {
                              id = this.getResourceId(ejbModule.getModuleId(), newId, refType, appResources);
                           } else {
                              id = this.getResourceId(ejbDeployment.getDeploymentId(), id, refType, appResources);
                           }
                        } else {
                           id = this.getResourceId(ejbDeployment.getDeploymentId(), id, refType, appResources);
                        }
                     } catch (OpenEJBException e) {
                        throw new OpenEJBException("Can't find resource for " + ref.getOrigin() + ". (" + e.getMessage() + ")", e.getCause());
                     }

                     logger.info("Auto-linking resource-ref '" + refName + "' in bean " + ejbDeployment.getDeploymentId() + " to Resource(id=" + id + ")");
                     link = new ResourceLink();
                     link.setResId(id);
                     link.setResRefName(refName);
                     ejbDeployment.addResourceLink(link);
                  } else {
                     String id = this.getResourceId(ejbDeployment.getDeploymentId(), link.getResId(), refType, appResources);
                     link.setResId(id);
                     link.setResRefName(refName);
                  }
               } catch (OpenEJBException ex) {
                  if (!(ref instanceof ContextRef)) {
                     throw ex;
                  }
               }

            }
         }
      }
   }

   private void processResourceEnvRef(JndiReference ref, EjbDeployment ejbDeployment, AppResources appResources, ClassLoader classLoader) throws OpenEJBException {
      if (ref.getLookupName() == null) {
         String mappedName = ref.getMappedName() == null ? "" : ref.getMappedName();
         if (!mappedName.startsWith("jndi:")) {
            String refName = ref.getName();
            String refType = this.getType(ref, classLoader);
            if (!this.isIgnoredReferenceType(refType, classLoader)) {
               ResourceLink link = ejbDeployment.getResourceLink(refName);
               if (link == null) {
                  String id = mappedName.length() == 0 ? refName : mappedName;
                  id = this.getResourceEnvId(ejbDeployment.getDeploymentId(), id, refType, appResources);
                  if (id == null) {
                     return;
                  }

                  logger.info("Auto-linking resource-env-ref '" + refName + "' in bean " + ejbDeployment.getDeploymentId() + " to Resource(id=" + id + ")");
                  link = new ResourceLink();
                  link.setResId(id);
                  link.setResRefName(refName);
                  ejbDeployment.addResourceLink(link);
               } else {
                  String id = this.getResourceEnvId(ejbDeployment.getDeploymentId(), link.getResId(), refType, appResources);
                  link.setResId(id);
                  link.setResRefName(refName);
               }

            }
         }
      }
   }

   private String getType(JndiReference ref, ClassLoader classLoader) throws OpenEJBException {
      String refType = ref.getType();
      if (refType != null) {
         return refType;
      } else {
         if (classLoader != null) {
            for(InjectionTarget injection : ref.getInjectionTarget()) {
               try {
                  Class target = classLoader.loadClass(injection.getInjectionTargetClass().trim());
                  Class type = IntrospectionSupport.getPropertyType(target, injection.getInjectionTargetName().trim());
                  return type.getName();
               } catch (NoSuchFieldException | ClassNotFoundException var9) {
               }
            }
         }

         throw new OpenEJBException("Unable to infer type for " + (String)ref.getKey());
      }
   }

   private static boolean skipMdb(Object bean) {
      return bean instanceof MessageDrivenBean && SystemInstance.get().hasProperty("openejb.geronimo");
   }

   private static String getType(EnterpriseBean enterpriseBean) throws OpenEJBException {
      if (enterpriseBean instanceof EntityBean) {
         return ((EntityBean)enterpriseBean).getPersistenceType() == PersistenceType.CONTAINER ? "CMP_ENTITY" : "BMP_ENTITY";
      } else if (enterpriseBean instanceof SessionBean) {
         if (((SessionBean)enterpriseBean).getSessionType() == SessionType.STATEFUL) {
            return "STATEFUL";
         } else if (((SessionBean)enterpriseBean).getSessionType() == SessionType.SINGLETON) {
            return "SINGLETON";
         } else {
            return ((SessionBean)enterpriseBean).getSessionType() == SessionType.MANAGED ? "MANAGED" : "STATELESS";
         }
      } else if (enterpriseBean instanceof MessageDrivenBean) {
         return "MESSAGE";
      } else {
         throw new OpenEJBException("Unknown enterprise bean type " + enterpriseBean.getClass().getName());
      }
   }

   private void deploy(AppModule app, PersistenceModule persistenceModule) throws OpenEJBException {
      if (this.autoCreateResources) {
         Persistence persistence = persistenceModule.getPersistence();

         for(PersistenceUnit unit : persistence.getPersistenceUnit()) {
            if (unit.getProvider() != null) {
               logger.info("Configuring PersistenceUnit(name=" + unit.getName() + ", provider=" + unit.getProvider() + ")");
            } else {
               logger.info("Configuring PersistenceUnit(name=" + unit.getName() + ")");
            }

            if (unit.getJtaDataSource() == null && unit.getNonJtaDataSource() == null && "true".equalsIgnoreCase(SystemInstance.get().getProperty("openejb.force-unit-type", unit.getProperty("openejb.force-unit-type", "true")))) {
               unit.setTransactionType(TransactionType.JTA);
            }

            boolean resourceLocal = TransactionType.RESOURCE_LOCAL.equals(unit.getTransactionType()) && unit.getJtaDataSource() == null;
            if (!resourceLocal || unit.getNonJtaDataSource() != null || !this.isDataSourcePropertiesConfigured(unit.getProperties())) {
               Properties required = new Properties();
               if ("org.apache.openjpa.persistence.PersistenceProviderImpl".equals(unit.getProvider())) {
                  if (unit.getJtaDataSource() == null) {
                     unit.setJtaDataSource(unit.getProperty("openjpa.ConnectionFactoryName"));
                  }

                  if (unit.getNonJtaDataSource() == null) {
                     unit.setNonJtaDataSource(unit.getProperty("openjpa.ConnectionFactory2Name"));
                  }
               }

               logger.debug("raw <jta-data-source>" + unit.getJtaDataSource() + "</jta-datasource>");
               logger.debug("raw <non-jta-data-source>" + unit.getNonJtaDataSource() + "</non-jta-datasource>");
               String originalJtaDataSource = unit.getJtaDataSource();
               unit.setJtaDataSource(normalizeResourceId(originalJtaDataSource));
               String originalNonJtaDataSource = unit.getNonJtaDataSource();
               unit.setNonJtaDataSource(normalizeResourceId(originalNonJtaDataSource));
               logger.debug("normalized <jta-data-source>" + unit.getJtaDataSource() + "</jta-datasource>");
               logger.debug("normalized <non-jta-data-source>" + unit.getNonJtaDataSource() + "</non-jta-datasource>");
               if (logger.isDebugEnabled()) {
                  required.put("JtaManaged", "true");
                  List<String> managed = this.configFactory.getResourceIds("DataSource", required);
                  required.put("JtaManaged", "false");
                  List<String> unmanaged = this.configFactory.getResourceIds("DataSource", required);
                  required.clear();
                  List<String> unknown = this.configFactory.getResourceIds("DataSource", required);
                  logger.debug("Available DataSources");

                  for(String name : managed) {
                     logger.debug("DataSource(name=" + name + ", JtaManaged=true)");
                  }

                  for(String name : unmanaged) {
                     logger.debug("DataSource(name=" + name + ", JtaManaged=false)");
                  }

                  for(String name : unknown) {
                     if (!managed.contains(name) && !unmanaged.contains(name)) {
                        logger.debug("DataSource(name=" + name + ", JtaManaged=<unknown>)");
                     }
                  }
               }

               String prefix = app.getModuleId() + "/";
               String jtaDataSourceId = null;
               String nonJtaDataSourceId = null;
               String jtaWithJavaAndSlash = this.replaceJavaAndSlash(unit.getJtaDataSource());

               for(String potentialName : Arrays.asList(prefix + jtaWithJavaAndSlash, originalJtaDataSource, jtaWithJavaAndSlash)) {
                  if (potentialName != null) {
                     ResourceInfo jtaInfo = this.configFactory.getResourceInfo(potentialName);
                     if (jtaInfo != null) {
                        if (!"false".equalsIgnoreCase(jtaInfo.properties.getProperty("JtaManaged")) && (jtaInfo.types.contains("DataSource") || jtaInfo.types.contains(DataSource.class.getName()))) {
                           jtaDataSourceId = jtaInfo.id;
                           break;
                        }

                        logger.warning("Found matching datasource: " + jtaInfo.id + " but this one is not a JTA datasource");
                     }
                  }
               }

               String nonJtaWithJavaAndSlash = this.replaceJavaAndSlash(unit.getNonJtaDataSource());

               for(String potentialName : Arrays.asList(prefix + nonJtaWithJavaAndSlash, originalNonJtaDataSource, nonJtaWithJavaAndSlash)) {
                  if (potentialName != null) {
                     ResourceInfo info = this.configFactory.getResourceInfo(potentialName);
                     if (info != null) {
                        if (!"true".equalsIgnoreCase(info.properties.getProperty("JtaManaged")) && (info.types.contains("DataSource") || info.types.contains(DataSource.class.getName()))) {
                           nonJtaDataSourceId = info.id;
                           break;
                        }

                        logger.warning("Found matching datasource: " + info.id + " but this one is a JTA datasource");
                     }
                  }
               }

               if (jtaDataSourceId == null && !resourceLocal) {
                  required.put("JtaManaged", "true");
                  jtaDataSourceId = this.findResourceId(prefix + jtaWithJavaAndSlash, "DataSource", required, (AppResources)null);
                  if (jtaDataSourceId == null) {
                     jtaDataSourceId = this.findResourceId(jtaWithJavaAndSlash, "javax.sql.DataSource", required, (AppResources)null);
                  }
               }

               if (nonJtaDataSourceId == null) {
                  required.put("JtaManaged", "false");
                  nonJtaDataSourceId = this.findResourceId(prefix + nonJtaWithJavaAndSlash, "DataSource", required, (AppResources)null);
                  if (nonJtaDataSourceId == null) {
                     nonJtaDataSourceId = this.findResourceId(nonJtaWithJavaAndSlash, "DataSource", required, (AppResources)null);
                  }
               }

               if ((jtaDataSourceId != null || resourceLocal) && nonJtaDataSourceId != null) {
                  if (jtaDataSourceId != null) {
                     this.setJtaDataSource(unit, jtaDataSourceId);
                  }

                  this.setNonJtaDataSource(unit, nonJtaDataSourceId);
               } else {
                  if (jtaDataSourceId == null && nonJtaDataSourceId == null) {
                     required.put("JtaManaged", ServiceUtils.NONE);
                     if (!resourceLocal) {
                        jtaDataSourceId = this.findResourceId(unit.getJtaDataSource(), "DataSource", required, (AppResources)null);
                     }

                     nonJtaDataSourceId = this.findResourceId(unit.getNonJtaDataSource(), "DataSource", required, (AppResources)null);
                     if (jtaDataSourceId != null || nonJtaDataSourceId != null) {
                        if (jtaDataSourceId != null) {
                           this.setJtaDataSource(unit, jtaDataSourceId);
                        }

                        if (nonJtaDataSourceId != null) {
                           this.setNonJtaDataSource(unit, nonJtaDataSourceId);
                        }
                        continue;
                     }
                  }

                  required.put("JtaManaged", ServiceUtils.ANY);
                  String possibleJta = this.findResourceId(jtaWithJavaAndSlash, "DataSource", required, (AppResources)null);
                  String possibleNonJta = this.findResourceId(nonJtaWithJavaAndSlash, "DataSource", required, (AppResources)null);
                  if (possibleJta != null && possibleJta.equals(possibleNonJta)) {
                     ResourceInfo dataSource = this.configFactory.getResourceInfo(possibleJta);
                     String jtaManaged = (String)dataSource.properties.get("JtaManaged");
                     logger.warning("PeristenceUnit(name=" + unit.getName() + ") invalidly refers to Resource(id=" + dataSource.id + ") as both its <jta-data-source> and <non-jta-data-source>.");
                     if ("true".equalsIgnoreCase(jtaManaged)) {
                        nonJtaDataSourceId = null;
                        unit.setNonJtaDataSource((String)null);
                     } else if ("false".equalsIgnoreCase(jtaManaged)) {
                        jtaDataSourceId = null;
                        unit.setJtaDataSource((String)null);
                     }
                  }

                  this.checkUnitDataSourceRefs(unit);
                  if (jtaDataSourceId == null && nonJtaDataSourceId == null) {
                     jtaDataSourceId = this.findResourceProviderId(unit.getJtaDataSource());
                     nonJtaDataSourceId = this.findResourceProviderId(unit.getNonJtaDataSource());
                     if (jtaDataSourceId != null || nonJtaDataSourceId != null) {
                        Resource jtaResource = new Resource(jtaDataSourceId, "DataSource", jtaDataSourceId);
                        jtaResource.getProperties().setProperty("JtaManaged", "true");
                        Resource nonJtaResource = new Resource(nonJtaDataSourceId, "DataSource", nonJtaDataSourceId);
                        nonJtaResource.getProperties().setProperty("JtaManaged", "false");
                        if (jtaDataSourceId == null) {
                           jtaResource.setId(nonJtaDataSourceId + "Jta");
                           jtaResource.setProvider(nonJtaDataSourceId);
                        } else if (nonJtaDataSourceId == null) {
                           nonJtaResource.setId(jtaDataSourceId + "NonJta");
                           nonJtaResource.setProvider(jtaDataSourceId);
                        }

                        ResourceInfo jtaResourceInfo = (ResourceInfo)this.configFactory.configureService((Service)jtaResource, ResourceInfo.class);
                        ResourceInfo nonJtaResourceInfo = (ResourceInfo)this.configFactory.configureService((Service)nonJtaResource, ResourceInfo.class);
                        if (jtaDataSourceId != null && nonJtaDataSourceId == null) {
                           nonJtaResourceInfo.originAppName = jtaResourceInfo.originAppName;
                        }

                        this.logAutoCreateResource(jtaResourceInfo, "DataSource", unit.getName());
                        jtaDataSourceId = this.installResource(unit.getName(), jtaResourceInfo);
                        this.logAutoCreateResource(nonJtaResourceInfo, "DataSource", unit.getName());
                        nonJtaDataSourceId = this.installResource(unit.getName(), nonJtaResourceInfo);
                        this.setJtaDataSource(unit, jtaDataSourceId);
                        this.setNonJtaDataSource(unit, nonJtaDataSourceId);
                        continue;
                     }
                  }

                  if (jtaDataSourceId == null && nonJtaDataSourceId == null) {
                     List<String> ids = new ArrayList();
                     ids.add(unit.getName());

                     for(WebModule webModule : app.getWebModules()) {
                        ids.add(webModule.getModuleId());
                        ids.add(webModule.getContextRoot());
                     }

                     ids.add(app.getModuleId());

                     for(String id : ids) {
                        required.put("JtaManaged", "true");
                        jtaDataSourceId = this.findResourceId(id, "DataSource", required, (AppResources)null);
                        if (jtaDataSourceId == null) {
                           required.clear();
                           required.put("JtaManaged", "false");
                           nonJtaDataSourceId = this.findResourceId(id, "DataSource", required, (AppResources)null);
                        }

                        if (jtaDataSourceId == null && nonJtaDataSourceId == null) {
                           required.clear();
                           required.put("JtaManaged", ServiceUtils.NONE);
                           jtaDataSourceId = this.findResourceId(id, "DataSource", required, (AppResources)null);
                        }

                        if (jtaDataSourceId != null || nonJtaDataSourceId != null) {
                           break;
                        }
                     }
                  }

                  if (jtaDataSourceId == null && nonJtaDataSourceId == null) {
                     required.clear();
                     required.put("JtaManaged", "true");
                     jtaDataSourceId = this.firstMatching(prefix, "DataSource", required, (AppResources)null);
                     if (jtaDataSourceId == null) {
                        required.clear();
                        required.put("JtaManaged", "false");
                        nonJtaDataSourceId = this.firstMatching(prefix, "DataSource", required, (AppResources)null);
                     }
                  }

                  if (jtaDataSourceId != null && nonJtaDataSourceId == null) {
                     ResourceInfo jtaResourceInfo = this.configFactory.getResourceInfo(jtaDataSourceId);
                     Properties jtaProperties = jtaResourceInfo.properties;
                     if (jtaProperties.containsKey("JtaManaged")) {
                        required.clear();
                        required.put("JtaManaged", "false");

                        for(String key : Arrays.asList("JdbcDriver", "JdbcUrl")) {
                           if (jtaProperties.containsKey(key)) {
                              required.put(key, jtaProperties.get(key));
                           }
                        }

                        nonJtaDataSourceId = this.firstMatching(prefix, "DataSource", required, (AppResources)null);
                        if (nonJtaDataSourceId == null) {
                           ResourceInfo nonJtaResourceInfo = this.copy(jtaResourceInfo);
                           nonJtaResourceInfo.id = jtaResourceInfo.id + "NonJta";
                           nonJtaResourceInfo.originAppName = jtaResourceInfo.originAppName;
                           suffixAliases(nonJtaResourceInfo, "NonJta");
                           configureImplicitDataSource(nonJtaResourceInfo);
                           Properties overrides = ConfigurationFactory.getSystemProperties(nonJtaResourceInfo.id, nonJtaResourceInfo.service);
                           nonJtaResourceInfo.properties.putAll(overrides);
                           nonJtaResourceInfo.properties.setProperty("JtaManaged", "false");
                           nonJtaResourceInfo.properties.remove("Definition");
                           this.logAutoCreateResource(nonJtaResourceInfo, "DataSource", unit.getName());
                           logger.info("configureService.configuring", nonJtaResourceInfo.id, nonJtaResourceInfo.service, jtaResourceInfo.id);
                           nonJtaDataSourceId = this.installResource(unit.getName(), nonJtaResourceInfo);
                        }
                     }
                  }

                  String deduceJtaFromNonJta = unit.getProperty("openejb.autocreate.jta-datasource-from-non-jta-one", SystemInstance.get().getOptions().get("openejb.autocreate.jta-datasource-from-non-jta-one", (String)null));
                  if (nonJtaDataSourceId != null && jtaDataSourceId == null && (!resourceLocal || deduceJtaFromNonJta != null) && (deduceJtaFromNonJta == null || deduceJtaFromNonJta != null && Boolean.parseBoolean(deduceJtaFromNonJta))) {
                     ResourceInfo nonJtaResourceInfo = this.configFactory.getResourceInfo(nonJtaDataSourceId);
                     Properties nonJtaProperties = nonJtaResourceInfo.properties;
                     if (nonJtaProperties.containsKey("JtaManaged")) {
                        required.clear();
                        required.put("JtaManaged", "true");

                        for(String key : Arrays.asList("JdbcDriver", "JdbcUrl")) {
                           if (nonJtaProperties.containsKey(key)) {
                              required.put(key, nonJtaProperties.get(key));
                           }
                        }

                        jtaDataSourceId = this.firstMatching(prefix, "DataSource", required, (AppResources)null);
                        if (jtaDataSourceId == null) {
                           ResourceInfo jtaResourceInfo = this.copy(nonJtaResourceInfo);
                           jtaResourceInfo.id = nonJtaResourceInfo.id + "Jta";
                           suffixAliases(jtaResourceInfo, "Jta");
                           configureImplicitDataSource(jtaResourceInfo);
                           Properties overrides = ConfigurationFactory.getSystemProperties(jtaResourceInfo.id, jtaResourceInfo.service);
                           jtaResourceInfo.properties.putAll(overrides);
                           jtaResourceInfo.properties.setProperty("JtaManaged", "true");
                           jtaResourceInfo.properties.remove("Definition");
                           this.logAutoCreateResource(jtaResourceInfo, "DataSource", unit.getName());
                           logger.info("configureService.configuring", jtaResourceInfo.id, jtaResourceInfo.service, nonJtaResourceInfo.id);
                           jtaDataSourceId = this.installResource(unit.getName(), jtaResourceInfo);
                        }
                     }
                  }

                  if (jtaDataSourceId == null && nonJtaDataSourceId == null) {
                     if (!resourceLocal) {
                        required.put("JtaManaged", "true");
                        jtaDataSourceId = this.autoCreateResource("DataSource", required, unit.getName());
                     }

                     required.put("JtaManaged", "false");
                     nonJtaDataSourceId = this.autoCreateResource("DataSource", required, unit.getName());
                  }

                  if (jtaDataSourceId != null) {
                     this.setJtaDataSource(unit, jtaDataSourceId);
                  }

                  if (nonJtaDataSourceId != null) {
                     this.setNonJtaDataSource(unit, nonJtaDataSourceId);
                  }
               }
            }
         }

      }
   }

   private boolean isDataSourcePropertiesConfigured(Properties properties) {
      return "true".equals(SystemInstance.get().getProperty("openejb.guess.resource-local-datasource-properties-configured", "true")) && (properties.containsKey("javax.persistence.jdbc.driver") || properties.containsKey("javax.persistence.jdbc.url"));
   }

   private static void suffixAliases(ResourceInfo ri, String suffix) {
      Collection<String> aliases = ri.aliases;
      List<String> newAliases = new ArrayList();

      for(String alias : aliases) {
         newAliases.add(alias + suffix);
      }

      ri.aliases = newAliases;
   }

   private static void configureImplicitDataSource(ResourceInfo copy) {
      if (copy != null && copy.properties != null) {
         for(String key : copy.properties.stringPropertyNames()) {
            if ("InitialSize".equalsIgnoreCase(key)) {
               try {
                  int value = Integer.parseInt(copy.properties.getProperty("InitialSize"));
                  if (5 < value) {
                     copy.properties.setProperty(key, Integer.toString(5));
                     logger.warning("Adjusting " + key + " to " + 5 + " for " + copy.id + " DataSource to avoid too much network bandwidth usage. If you want to keep it please define the DataSource explicitely.");
                  }
               } catch (NumberFormatException var4) {
               }
            }
         }
      }

   }

   private String replaceJavaAndSlash(String name) {
      if (name == null) {
         return null;
      } else if (name.startsWith("java:")) {
         return this.replaceJavaAndSlash(name.substring("java:".length()));
      } else {
         return name.startsWith("/") ? name.substring(1) : name;
      }
   }

   private void setNonJtaDataSource(PersistenceUnit unit, String current) {
      String previous = unit.getNonJtaDataSource();
      if (!current.equals(previous)) {
         logger.info("Adjusting PersistenceUnit " + unit.getName() + " <non-jta-data-source> to Resource ID '" + current + "' from '" + previous + "'");
      }

      unit.setNonJtaDataSource(current);
   }

   private void setJtaDataSource(PersistenceUnit unit, String current) {
      String previous = unit.getJtaDataSource();
      if (!current.equals(previous)) {
         logger.info("Adjusting PersistenceUnit " + unit.getName() + " <jta-data-source> to Resource ID '" + current + "' from '" + previous + "'");
      }

      unit.setJtaDataSource(current);
   }

   private ResourceInfo copy(ResourceInfo a) {
      ResourceInfo b = new ResourceInfo();
      b.id = a.id;
      b.service = a.service;
      b.className = a.className;
      b.codebase = a.codebase;
      b.displayName = a.displayName;
      b.description = a.description;
      b.factoryMethod = a.factoryMethod;
      b.constructorArgs.addAll(a.constructorArgs);
      b.originAppName = a.originAppName;
      b.types.addAll(a.types);
      b.properties = new SuperProperties();
      b.properties.putAll(a.properties);
      if (a.classpath != null) {
         b.classpath = new URI[a.classpath.length];
         System.arraycopy(a.classpath, 0, b.classpath, 0, a.classpath.length);
      }

      return b;
   }

   private void checkUnitDataSourceRefs(PersistenceUnit unit) throws OpenEJBException {
      Properties required = new Properties();
      required.put("JtaManaged", "true");
      String invalidNonJta = this.findResourceId(unit.getNonJtaDataSource(), "DataSource", required, (AppResources)null);
      if (invalidNonJta != null) {
         throw new OpenEJBException("PeristenceUnit " + unit.getName() + " <non-jta-data-source> points to a jta managed Resource.  Update Resource \"" + invalidNonJta + "\" to \"JtaManaged=false\", use a different Resource, or delete the <non-jta-data-source> element and a default will be supplied if possible.");
      } else {
         required.put("JtaManaged", "false");
         String invalidJta = this.findResourceId(unit.getJtaDataSource(), "DataSource", required, (AppResources)null);
         if (invalidJta != null) {
            throw new OpenEJBException("PeristenceUnit " + unit.getName() + " <jta-data-source> points to a non jta managed Resource.  Update Resource \"" + invalidJta + "\" to \"JtaManaged=true\", use a different Resource, or delete the <jta-data-source> element and a default will be supplied if possible.");
         }
      }
   }

   private String findResourceProviderId(String resourceId) throws OpenEJBException {
      if (resourceId == null) {
         return null;
      } else if (ServiceUtils.hasServiceProvider(resourceId)) {
         return resourceId;
      } else {
         if (resourceId.startsWith("java:")) {
            String jndi = resourceId.substring("java:".length());
            if (jndi.startsWith("/")) {
               jndi = jndi.substring(1);
            }

            if (ServiceUtils.hasServiceProvider(jndi)) {
               return jndi;
            }
         }

         resourceId = toShortName(resourceId);
         return ServiceUtils.hasServiceProvider(resourceId) ? resourceId : null;
      }
   }

   private String getResourceId(String beanName, String resourceId, String type, AppResources appResources) throws OpenEJBException {
      return this.getResourceId(beanName, resourceId, type, (Properties)null, appResources);
   }

   private String getResourceId(String beanName, String resourceId, String type, Properties required, AppResources appResources) throws OpenEJBException {
      resourceId = normalizeResourceId(resourceId);
      if (resourceId == null) {
         return null;
      } else {
         if (appResources == null) {
            appResources = EMPTY_APP_RESOURCES;
         }

         if (type != null && ignoredReferenceTypes.contains(type)) {
            return null;
         } else {
            String id = this.findResourceId(beanName + '/' + resourceId, type, required, appResources);
            if (id != null) {
               return id;
            } else {
               id = this.findResourceId(resourceId, type, required, appResources);
               if (id != null) {
                  return id;
               } else {
                  id = this.findResourceId(resourceId, (String)null, required, appResources);
                  if (id != null) {
                     return id;
                  } else {
                     if (appResources.appId != null && !appResources.appId.isEmpty() && resourceId.startsWith(appResources.appId + '/')) {
                        id = this.findResourceId(resourceId.substring(appResources.appId.length() + 1), type, required, appResources);
                        if (id != null) {
                           return id;
                        }
                     }

                     String shortName = toShortName(resourceId);
                     String message = "No existing resource found while attempting to Auto-link unmapped resource-ref '" + resourceId + "' of type '" + type + "' for '" + beanName + "'.  Looked for Resource(id=" + resourceId + ") and Resource(id=" + shortName + ")";
                     if (!this.autoCreateResources) {
                        throw new OpenEJBException(message);
                     } else {
                        logger.debug(message);
                        if (ServiceUtils.hasServiceProvider(resourceId)) {
                           ResourceInfo resourceInfo = (ResourceInfo)this.configFactory.configureService(resourceId, ResourceInfo.class);
                           return this.installResource(beanName, resourceInfo);
                        } else if (ServiceUtils.hasServiceProvider(shortName)) {
                           ResourceInfo resourceInfo = (ResourceInfo)this.configFactory.configureService(shortName, ResourceInfo.class);
                           return this.installResource(beanName, resourceInfo);
                        } else {
                           id = this.firstMatching(beanName, type, required, appResources);
                           return id != null ? id : this.autoCreateResource(type, required, beanName);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private String autoCreateResource(String type, Properties required, String beanName) throws OpenEJBException {
      String resourceId = ServiceUtils.getServiceProviderId(type, required);
      if (resourceId == null) {
         throw new OpenEJBException("No provider available for resource-ref '" + resourceId + "' of type '" + type + "' for '" + beanName + "'.");
      } else {
         ResourceInfo resourceInfo = (ResourceInfo)this.configFactory.configureService(resourceId, ResourceInfo.class);
         this.logAutoCreateResource(resourceInfo, type, beanName);
         return this.installResource(beanName, resourceInfo);
      }
   }

   private void logAutoCreateResource(ResourceInfo resourceInfo, String type, String beanName) {
      logger.info("Auto-creating a Resource with id '" + resourceInfo.id + "' of type '" + type + " for '" + beanName + "'.");
   }

   private String firstMatching(final String prefix, String type, Properties required, AppResources appResources) {
      final List<String> resourceIds = this.getResourceIds(appResources, type, required);
      return resourceIds.isEmpty() ? null : (String)Collections.min(resourceIds, new Comparator<String>() {
         public int compare(String o1, String o2) {
            if (o1.startsWith(prefix) && o2.startsWith(prefix)) {
               return resourceIds.indexOf(o1) - resourceIds.indexOf(o2);
            } else if (o1.startsWith(prefix)) {
               return -1;
            } else {
               return o2.startsWith(prefix) ? 1 : resourceIds.indexOf(o1) - resourceIds.indexOf(o2);
            }
         }
      });
   }

   private String findResourceId(String resourceId, String type, Properties required, AppResources appResources) {
      return resourceId == null ? null : findResourceId(this.getResourceIds(appResources, type, required), resourceId);
   }

   public static String findResourceId(Collection<String> resourceIds, String inId) {
      if (inId == null) {
         return null;
      } else {
         String resourceId = normalizeResourceId(inId);

         for(String id : resourceIds) {
            if (id.equalsIgnoreCase(resourceId)) {
               return id;
            }
         }

         String shortName = toShortName(resourceId);

         for(String id : resourceIds) {
            if (id.equalsIgnoreCase(shortName)) {
               return id;
            }
         }

         if (resourceId.startsWith("osgi:")) {
            return resourceId;
         } else {
            return null;
         }
      }
   }

   private List<String> getResourceIds(AppResources appResources, String type, Properties required) {
      List<String> resourceIds = new ArrayList();
      if (appResources != null) {
         resourceIds.addAll(appResources.getResourceIds(type));
      }

      resourceIds.addAll(this.configFactory.getResourceIds(type, required));
      return resourceIds;
   }

   private static String toShortName(String resourceId) {
      return resourceId.replaceFirst(".*/", "");
   }

   private static String normalizeResourceId(String resourceId) {
      if (resourceId == null) {
         return null;
      } else {
         if (resourceId.startsWith("java:")) {
            resourceId = resourceId.substring("java:".length());
            if (resourceId.startsWith("/")) {
               resourceId = resourceId.substring(1);
            }
         }

         if (resourceId.startsWith("comp/env/")) {
            resourceId = resourceId.substring("comp/env/".length());
         }

         if (resourceId.startsWith("openejb/Resource/")) {
            resourceId = resourceId.substring("openejb/Resource/".length());
         }

         if (resourceId.startsWith("openejb/Connector/")) {
            resourceId = resourceId.substring("openejb/Connector/".length());
         }

         return resourceId;
      }
   }

   private String installResource(String beanName, ResourceInfo resourceInfo) throws OpenEJBException {
      String resourceAdapterId = resourceInfo.properties.getProperty("ResourceAdapter");
      if (resourceAdapterId != null) {
         String newResourceId = this.getResourceId(beanName, resourceAdapterId, (String)null, (AppResources)null);
         if (!resourceAdapterId.equals(newResourceId)) {
            resourceInfo.properties.setProperty("ResourceAdapter", newResourceId);
         }
      }

      String dataSourceId = resourceInfo.properties.getProperty("DataSource");
      if (dataSourceId != null && dataSourceId.length() > 0) {
         String newResourceId = this.getResourceId(beanName, dataSourceId, (String)null, (AppResources)null);
         if (!dataSourceId.equals(newResourceId)) {
            resourceInfo.properties.setProperty("DataSource", newResourceId);
         }
      }

      this.configFactory.install(resourceInfo);
      return resourceInfo.id;
   }

   private String getResourceEnvId(String beanName, String resourceId, String type, AppResources appResources) throws OpenEJBException {
      if (resourceId == null) {
         return null;
      } else {
         if (appResources == null) {
            appResources = EMPTY_APP_RESOURCES;
         }

         if (ignoredReferenceTypes.contains(type)) {
            return null;
         } else {
            resourceId = normalizeResourceId(resourceId);
            List<String> resourceEnvIds = this.getResourceIds(appResources, type, (Properties)null);

            for(String id : resourceEnvIds) {
               if (id.equalsIgnoreCase(resourceId)) {
                  return id;
               }
            }

            String message = "No existing resource found while attempting to Auto-link unmapped resource-env-ref '" + resourceId + "' of type '" + type + "' for '" + beanName + "'.  Looked for Resource(id=" + resourceId + ")";
            if (!this.autoCreateResources) {
               throw new OpenEJBException(message);
            } else {
               logger.debug(message);
               String providerId = ServiceUtils.getServiceProviderId(type);
               if (providerId == null) {
                  if (resourceEnvIds.size() > 0) {
                     return (String)resourceEnvIds.get(0);
                  } else {
                     throw new OpenEJBException("No provider available for resource-env-ref '" + resourceId + "' of type '" + type + "' for '" + beanName + "'.");
                  }
               } else {
                  Resource resource = new Resource(resourceId, (String)null, providerId);
                  resource.getProperties().setProperty("destination", resourceId);
                  ResourceInfo resourceInfo = (ResourceInfo)this.configFactory.configureService((Service)resource, ResourceInfo.class);
                  this.logAutoCreateResource(resourceInfo, type, beanName);
                  return this.installResource(beanName, resourceInfo);
               }
            }
         }
      }
   }

   private String getUsableContainer(Class<? extends ContainerInfo> containerInfoType, Object bean, AppResources appResources) {
      if (MessageDrivenBean.class.isInstance(bean)) {
         MessageDrivenBean messageDrivenBean = (MessageDrivenBean)bean;
         String messagingType = messageDrivenBean.getMessagingType();
         List<String> containerIds = (List)appResources.containerIdsByType.get(messagingType);
         if (containerIds != null && !containerIds.isEmpty()) {
            return (String)containerIds.get(0);
         }
      }

      String containerInfo = this.matchContainer(containerInfoType, bean, appResources.getContainerInfos());
      if (containerInfo == null) {
         containerInfo = this.matchContainer(containerInfoType, bean, this.configFactory.getContainerInfos());
      }

      return containerInfo != null ? containerInfo : null;
   }

   private String matchContainer(Class<? extends ContainerInfo> containerInfoType, Object bean, Collection<ContainerInfo> list) {
      for(ContainerInfo containerInfo : list) {
         if (containerInfo.getClass().equals(containerInfoType)) {
            if (!MessageDrivenBean.class.isInstance(bean)) {
               return containerInfo.id;
            }

            MessageDrivenBean messageDrivenBean = (MessageDrivenBean)bean;
            String messagingType = messageDrivenBean.getMessagingType();
            if (containerInfo.properties.get("MessageListenerInterface").equals(messagingType)) {
               return containerInfo.id;
            }
         }
      }

      return null;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, AutoConfig.class);
      ignoredReferenceTypes = new TreeSet();
      ignoredReferenceTypes.add("javax.ejb.SessionContext");
      ignoredReferenceTypes.add("javax.ejb.EntityContext");
      ignoredReferenceTypes.add("javax.ejb.MessageDrivenContext");
      ignoredReferenceTypes.add("javax.ejb.EJBContext");
      ignoredReferenceTypes.add("javax.xml.ws.WebServiceContext");
      ignoredReferenceTypes.add("java.net.URL");
      ignoredReferenceTypes.add(UserTransaction.class.getName());
      ignoredReferenceTypes.add(TransactionManager.class.getName());
      ignoredReferenceTypes.add(TransactionSynchronizationRegistry.class.getName());
      ignoredReferenceTypes.add(TimerService.class.getName());
      ignoredReferenceTypes.add(Validator.class.getName());
      ignoredReferenceTypes.add(ValidatorFactory.class.getName());
      ignoredReferenceTypes.add(BeanManager.class.getName());
      ignoredReferenceTypes.add(Request.class.getName());
      ignoredReferenceTypes.add(UriInfo.class.getName());
      ignoredReferenceTypes.add(HttpHeaders.class.getName());
      ignoredReferenceTypes.add(SecurityContext.class.getName());
      ignoredReferenceTypes.add(ContextResolver.class.getName());
      ignoredReferenceTypes.add(Application.class.getName());
      ignoredReferenceTypes.add(Providers.class.getName());
      ignoredReferenceTypes.add(ServletRequest.class.getName());
      ignoredReferenceTypes.add(HttpServletRequest.class.getName());
      ignoredReferenceTypes.add(ServletConfig.class.getName());
      ignoredReferenceTypes.add(ServletContext.class.getName());
      ignoredReferenceTypes.add(HttpServletResponse.class.getName());
   }

   static class AppResources {
      private String appId;
      private final Set<String> resourceAdapterIds = new TreeSet();
      private final Map<String, List<String>> resourceIdsByType = new TreeMap();
      private final Map<String, List<String>> resourceEnvIdsByType = new TreeMap();
      private final Map<String, List<String>> containerIdsByType = new TreeMap();
      private final Collection<ContainerInfo> containerInfos = new HashSet();

      public void dump() {
         if (AutoConfig.logger.isDebugEnabled()) {
            for(String s : this.resourceAdapterIds) {
               AutoConfig.logger.debug(this.appId + " module contains resource adapter id: " + s);
            }

            for(String s : this.resourceIdsByType.keySet()) {
               for(String value : (List)this.resourceIdsByType.get(s)) {
                  AutoConfig.logger.debug(this.appId + " module contains resource type: " + s + " --> " + value);
               }
            }

            for(String s : this.resourceEnvIdsByType.keySet()) {
               for(String value : (List)this.resourceEnvIdsByType.get(s)) {
                  AutoConfig.logger.debug(this.appId + " module contains resource env type: " + s + " --> " + value);
               }
            }

            for(String s : this.containerIdsByType.keySet()) {
               for(String value : (List)this.containerIdsByType.get(s)) {
                  AutoConfig.logger.debug(this.appId + " module contains container type: " + s + " --> " + value);
               }
            }

         }
      }

      public AppResources() {
      }

      public AppResources(AppModule appModule) {
         this.appId = appModule.getModuleId();

         for(ConnectorModule connectorModule : appModule.getConnectorModules()) {
            Connector connector = connectorModule.getConnector();
            ResourceAdapter resourceAdapter = connector.getResourceAdapter();
            if (resourceAdapter.getResourceAdapterClass() != null) {
               String resourceAdapterId;
               if (resourceAdapter.getId() != null) {
                  resourceAdapterId = resourceAdapter.getId();
               } else {
                  resourceAdapterId = connectorModule.getModuleId() + "RA";
               }

               this.resourceAdapterIds.add(resourceAdapterId);
            }

            OutboundResourceAdapter outbound = resourceAdapter.getOutboundResourceAdapter();
            if (outbound != null) {
               for(ConnectionDefinition connection : outbound.getConnectionDefinition()) {
                  String type = connection.getConnectionFactoryInterface();
                  String resourceId;
                  if (connection.getId() != null) {
                     resourceId = connection.getId();
                  } else if (outbound.getConnectionDefinition().size() == 1) {
                     resourceId = connectorModule.getModuleId();
                  } else {
                     resourceId = connectorModule.getModuleId() + "-" + type;
                  }

                  List<String> resourceIds = (List)this.resourceIdsByType.get(type);
                  if (resourceIds == null) {
                     resourceIds = new ArrayList();
                     this.resourceIdsByType.put(type, resourceIds);
                  }

                  resourceIds.add(resourceId);
               }
            }

            InboundResourceadapter inbound = resourceAdapter.getInboundResourceAdapter();
            if (inbound != null) {
               for(MessageListener messageListener : inbound.getMessageAdapter().getMessageListener()) {
                  String type = messageListener.getMessageListenerType();
                  String containerId;
                  if (messageListener.getId() != null) {
                     containerId = messageListener.getId();
                  } else if (inbound.getMessageAdapter().getMessageListener().size() == 1) {
                     containerId = connectorModule.getModuleId();
                  } else {
                     containerId = connectorModule.getModuleId() + "-" + type;
                  }

                  List<String> containerIds = (List)this.containerIdsByType.get(type);
                  if (containerIds == null) {
                     containerIds = new ArrayList();
                     this.containerIdsByType.put(type, containerIds);
                  }

                  containerIds.add(containerId);
               }
            }

            for(AdminObject adminObject : resourceAdapter.getAdminObject()) {
               String type = adminObject.getAdminObjectInterface();
               String resourceEnvId;
               if (adminObject.getId() != null) {
                  resourceEnvId = adminObject.getId();
               } else if (resourceAdapter.getAdminObject().size() == 1) {
                  resourceEnvId = connectorModule.getModuleId();
               } else {
                  resourceEnvId = connectorModule.getModuleId() + "-" + type;
               }

               List<String> resourceEnvIds = (List)this.resourceEnvIdsByType.get(type);
               if (resourceEnvIds == null) {
                  resourceEnvIds = new ArrayList();
                  this.resourceEnvIdsByType.put(type, resourceEnvIds);
               }

               resourceEnvIds.add(resourceEnvId);
            }
         }

         for(Resource r : appModule.getResources()) {
            String type = r.getType();
            if (type != null) {
               String[] types = type.trim().split(",");

               for(String t : types) {
                  List<String> ids = (List)this.resourceIdsByType.get(t);
                  if (ids == null) {
                     ids = new ArrayList();
                     this.resourceIdsByType.put(t, ids);
                  }

                  ids.add(r.getId());
                  if (r.getJndi() != null) {
                     ids.add(r.getJndi());
                  }
               }
            }
         }

      }

      public Collection<ContainerInfo> getContainerInfos() {
         return this.containerInfos;
      }

      private void addContainer(ContainerInfo container) {
         this.containerInfos.add(container);
      }

      public List<String> getResourceIds(String type) {
         if (type != null) {
            List<String> resourceIds = (List)this.resourceIdsByType.get(type);
            return resourceIds != null ? resourceIds : Collections.emptyList();
         } else {
            List<String> allResourceIds = new ArrayList();

            for(List<String> resourceIds : this.resourceIdsByType.values()) {
               allResourceIds.addAll(resourceIds);
            }

            return allResourceIds;
         }
      }

      public List<String> getResourceEnvIds(String type) {
         if (type != null) {
            List<String> resourceIds = (List)this.resourceEnvIdsByType.get(type);
            if (resourceIds != null) {
               return resourceIds;
            }
         }

         return Collections.emptyList();
      }

      public List<String> getContainerIds() {
         ArrayList<String> ids = new ArrayList();

         for(List<String> list : this.containerIdsByType.values()) {
            ids.addAll(list);
         }

         return ids;
      }
   }
}
