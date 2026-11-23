package org.apache.openejb.config;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.assembler.classic.AppInfo;
import org.apache.openejb.assembler.classic.ContextReferenceInfo;
import org.apache.openejb.assembler.classic.EjbJarInfo;
import org.apache.openejb.assembler.classic.EjbLocalReferenceInfo;
import org.apache.openejb.assembler.classic.EjbReferenceInfo;
import org.apache.openejb.assembler.classic.EjbResolver;
import org.apache.openejb.assembler.classic.EnterpriseBeanInfo;
import org.apache.openejb.assembler.classic.EnvEntryInfo;
import org.apache.openejb.assembler.classic.InjectableInfo;
import org.apache.openejb.assembler.classic.InjectionInfo;
import org.apache.openejb.assembler.classic.JndiEncInfo;
import org.apache.openejb.assembler.classic.PersistenceContextReferenceInfo;
import org.apache.openejb.assembler.classic.PersistenceUnitReferenceInfo;
import org.apache.openejb.assembler.classic.PortRefInfo;
import org.apache.openejb.assembler.classic.ReferenceLocationInfo;
import org.apache.openejb.assembler.classic.ResourceEnvReferenceInfo;
import org.apache.openejb.assembler.classic.ResourceReferenceInfo;
import org.apache.openejb.assembler.classic.ServiceReferenceInfo;
import org.apache.openejb.jee.EjbLocalRef;
import org.apache.openejb.jee.EjbRef;
import org.apache.openejb.jee.EjbReference;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.Injectable;
import org.apache.openejb.jee.InjectionTarget;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.jee.JndiReference;
import org.apache.openejb.jee.MessageDestinationRef;
import org.apache.openejb.jee.PersistenceContextRef;
import org.apache.openejb.jee.PersistenceContextType;
import org.apache.openejb.jee.PersistenceUnitRef;
import org.apache.openejb.jee.PortComponentRef;
import org.apache.openejb.jee.Property;
import org.apache.openejb.jee.ResAuth;
import org.apache.openejb.jee.ResourceEnvRef;
import org.apache.openejb.jee.ResourceRef;
import org.apache.openejb.jee.ServiceRef;
import org.apache.openejb.jee.SessionBean;
import org.apache.openejb.jee.EjbReference.Type;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.URLs;

public class JndiEncInfoBuilder {
   public static final Logger logger;
   private final EjbResolver earResolver;
   private final Map<String, EjbResolver> ejbJarResolvers = new HashMap();
   private final AppInfo appInfo;

   public JndiEncInfoBuilder(AppInfo appInfo) {
      this.appInfo = appInfo;
      EjbResolver globalResolver = (EjbResolver)SystemInstance.get().getComponent(EjbResolver.class);
      this.earResolver = new EjbResolver(globalResolver, EjbResolver.Scope.EAR, appInfo.ejbJars);

      for(EjbJarInfo ejbJarInfo : appInfo.ejbJars) {
         EjbResolver ejbJarResolver = new EjbResolver(this.earResolver, EjbResolver.Scope.EJBJAR, new EjbJarInfo[]{ejbJarInfo});
         this.ejbJarResolvers.put(ejbJarInfo.moduleName, ejbJarResolver);
      }

   }

   private EjbResolver getEjbResolver(String moduleId) {
      EjbResolver resolver = (EjbResolver)this.ejbJarResolvers.get(moduleId);
      if (resolver == null) {
         resolver = this.earResolver;
      }

      return resolver;
   }

   public void build(JndiConsumer jndiConsumer, String ejbName, String moduleId, URI moduleUri, JndiEncInfo moduleJndiEnc, JndiEncInfo compJndiEnc) throws OpenEJBException {
      assert moduleJndiEnc != null;

      assert compJndiEnc != null;

      assert jndiConsumer != null;

      this.buildEnvEntryInfos(jndiConsumer, moduleJndiEnc, compJndiEnc);
      this.buildResourceRefInfos(jndiConsumer, moduleJndiEnc, compJndiEnc);
      this.buildResourceEnvRefInfos(jndiConsumer, moduleJndiEnc, compJndiEnc);
      this.buildEjbRefs(jndiConsumer, moduleUri, moduleId, ejbName, moduleJndiEnc, compJndiEnc);
      this.buildPersistenceUnitRefInfos(jndiConsumer, moduleJndiEnc, compJndiEnc);
      this.buildPersistenceContextRefInfos(jndiConsumer, moduleJndiEnc, compJndiEnc);
      this.buildServiceRefInfos(jndiConsumer, moduleJndiEnc, compJndiEnc);
   }

   private void buildEjbRefs(JndiConsumer jndiConsumer, URI moduleUri, String moduleId, String ejbName, JndiEncInfo moduleJndiEnc, JndiEncInfo compJndiEnc) throws OpenEJBException {
      Collection<EjbRef> ejbRefs = jndiConsumer.getEjbRef();
      Collection<EjbLocalRef> ejbLocalRefs = jndiConsumer.getEjbLocalRef();
      List<EjbReference> references = new ArrayList(ejbRefs.size() + ejbLocalRefs.size());
      references.addAll(ejbRefs);
      references.addAll(ejbLocalRefs);

      for(EjbReference ref : references) {
         EjbReferenceInfo info = new EjbReferenceInfo();
         info.homeClassName = ref.getHome();
         info.interfaceClassName = ref.getInterface();
         info.referenceName = ref.getName();
         info.link = ref.getEjbLink();
         info.location = this.buildLocationInfo(ref);
         info.targets.addAll(this.buildInjectionInfos(ref));
         info.localbean = this.isIntefaceLocalBean(info.interfaceClassName);
         if (info.location != null) {
            if (ref.getRefType() == Type.LOCAL) {
               this.insert(this.toLocal(info), this.appInfo.globalJndiEnc.ejbLocalReferences, this.appInfo.appJndiEnc.ejbLocalReferences, moduleJndiEnc.ejbLocalReferences, compJndiEnc.ejbLocalReferences);
            } else {
               this.insert(info, this.appInfo.globalJndiEnc.ejbReferences, this.appInfo.appJndiEnc.ejbReferences, moduleJndiEnc.ejbReferences, compJndiEnc.ejbReferences);
            }
         } else {
            EjbResolver ejbResolver = this.getEjbResolver(moduleId);
            String deploymentId = ejbResolver.resolve(new Ref(ref), moduleUri);
            info.ejbDeploymentId = deploymentId;
            if (info.ejbDeploymentId == null) {
               if (info.link != null) {
                  logger.warning("config.noBeanFoundEjbLink", ref.getName(), ejbName, ref.getEjbLink());
               } else {
                  logger.warning("config.noBeanFound", ref.getName(), ejbName, ref.getEjbLink());
               }
            } else {
               EjbResolver.Scope scope = ejbResolver.getScope(deploymentId);
               info.externalReference = scope != EjbResolver.Scope.EAR && scope != EjbResolver.Scope.EJBJAR;
               if (ref.getRefType() == Type.UNKNOWN) {
                  EnterpriseBeanInfo otherBean = ejbResolver.getEnterpriseBeanInfo(deploymentId);
                  if (otherBean != null) {
                     if (!otherBean.businessLocal.contains(ref.getInterface()) && !otherBean.ejbClass.equals(ref.getInterface())) {
                        ref.setRefType(Type.REMOTE);
                     } else {
                        ref.setRefType(Type.LOCAL);
                        ejbRefs.remove(ref);
                        ejbLocalRefs.add(new EjbLocalRef(ref));
                     }
                  }
               }
            }

            if (ref.getRefType() == Type.LOCAL) {
               this.insert(this.toLocal(info), this.appInfo.globalJndiEnc.ejbLocalReferences, this.appInfo.appJndiEnc.ejbLocalReferences, moduleJndiEnc.ejbLocalReferences, compJndiEnc.ejbLocalReferences);
            } else {
               this.insert(info, this.appInfo.globalJndiEnc.ejbReferences, this.appInfo.appJndiEnc.ejbReferences, moduleJndiEnc.ejbReferences, compJndiEnc.ejbReferences);
            }
         }
      }

   }

   private EjbLocalReferenceInfo toLocal(EjbReferenceInfo referenceInfo) {
      EjbLocalReferenceInfo local = new EjbLocalReferenceInfo();
      local.ejbDeploymentId = referenceInfo.ejbDeploymentId;
      local.externalReference = referenceInfo.externalReference;
      local.homeClassName = referenceInfo.homeClassName;
      local.interfaceClassName = referenceInfo.interfaceClassName;
      local.referenceName = referenceInfo.referenceName;
      local.link = referenceInfo.link;
      local.location = referenceInfo.location;
      local.targets.addAll(referenceInfo.targets);
      local.localbean = referenceInfo.localbean;
      return local;
   }

   private void buildServiceRefInfos(JndiConsumer jndiConsumer, JndiEncInfo moduleJndiEnc, JndiEncInfo compJndiEnc) {
      for(ServiceRef ref : jndiConsumer.getServiceRef()) {
         ServiceReferenceInfo info = new ServiceReferenceInfo();
         info.referenceName = ref.getName();
         info.location = this.buildLocationInfo(ref);
         info.targets.addAll(this.buildInjectionInfos(ref));
         this.insert(info, this.appInfo.globalJndiEnc.serviceRefs, this.appInfo.appJndiEnc.serviceRefs, moduleJndiEnc.serviceRefs, compJndiEnc.serviceRefs);
         if (!SystemInstance.get().hasProperty("openejb.geronimo")) {
            info.id = ref.getMappedName();
            info.serviceQName = ref.getServiceQname();
            info.serviceType = ref.getServiceInterface();
            info.referenceType = ref.getServiceRefType();
            info.wsdlFile = ref.getWsdlFile();
            info.jaxrpcMappingFile = ref.getJaxrpcMappingFile();
            info.handlerChains.addAll(ConfigurationFactory.toHandlerChainInfo(ref.getAllHandlers()));

            for(PortComponentRef portComponentRef : ref.getPortComponentRef()) {
               PortRefInfo portRefInfo = new PortRefInfo();
               portRefInfo.qname = portComponentRef.getQName();
               portRefInfo.serviceEndpointInterface = portComponentRef.getServiceEndpointInterface();
               portRefInfo.enableMtom = portComponentRef.isEnableMtom();
               portRefInfo.properties.putAll(portComponentRef.getProperties());
               info.portRefs.add(portRefInfo);
            }
         }
      }

   }

   private void buildPersistenceUnitRefInfos(JndiConsumer jndiConsumer, JndiEncInfo moduleJndiEnc, JndiEncInfo compJndiEnc) {
      for(PersistenceUnitRef puRef : jndiConsumer.getPersistenceUnitRef()) {
         PersistenceUnitReferenceInfo info = new PersistenceUnitReferenceInfo();
         info.referenceName = puRef.getPersistenceUnitRefName();
         info.persistenceUnitName = puRef.getPersistenceUnitName();
         info.unitId = puRef.getMappedName();
         info.location = this.buildLocationInfo(puRef);
         info.targets.addAll(this.buildInjectionInfos(puRef));
         this.insert(info, this.appInfo.globalJndiEnc.persistenceUnitRefs, this.appInfo.appJndiEnc.persistenceUnitRefs, moduleJndiEnc.persistenceUnitRefs, compJndiEnc.persistenceUnitRefs);
      }

   }

   private void buildPersistenceContextRefInfos(JndiConsumer jndiConsumer, JndiEncInfo moduleJndiEnc, JndiEncInfo compJndiEnc) {
      for(PersistenceContextRef contextRef : jndiConsumer.getPersistenceContextRef()) {
         PersistenceContextReferenceInfo info = new PersistenceContextReferenceInfo();
         info.referenceName = contextRef.getPersistenceContextRefName();
         info.persistenceUnitName = contextRef.getPersistenceUnitName();
         info.synchronizationType = contextRef.getPersistenceContextSynchronization() != null ? contextRef.getPersistenceContextSynchronization().name() : null;
         info.unitId = contextRef.getMappedName();
         info.location = this.buildLocationInfo(contextRef);
         info.extended = contextRef.getPersistenceContextType() == PersistenceContextType.EXTENDED;

         for(Property property : contextRef.getPersistenceProperty()) {
            String name = property.getName();
            String value = property.getValue();
            info.properties.setProperty(name, value);
         }

         info.targets.addAll(this.buildInjectionInfos(contextRef));
         this.insert(info, this.appInfo.globalJndiEnc.persistenceContextRefs, this.appInfo.appJndiEnc.persistenceContextRefs, moduleJndiEnc.persistenceContextRefs, compJndiEnc.persistenceContextRefs);
      }

   }

   private void buildResourceRefInfos(JndiConsumer item, JndiEncInfo moduleJndiEnc, JndiEncInfo compJndiEnc) {
      for(ResourceRef res : item.getResourceRef()) {
         ResourceReferenceInfo info;
         if (res instanceof ContextRef) {
            info = new ContextReferenceInfo();
         } else {
            info = new ResourceReferenceInfo();
         }

         if (res.getResAuth() != null) {
            info.referenceAuth = res.getResAuth().toString();
         } else {
            info.referenceAuth = ResAuth.CONTAINER.toString();
         }

         info.referenceName = res.getResRefName();
         info.referenceType = res.getResType();
         info.resourceID = res.getMappedName();
         info.location = this.buildLocationInfo(res);
         info.targets.addAll(this.buildInjectionInfos(res));
         this.insert(info, this.appInfo.globalJndiEnc.resourceRefs, this.appInfo.appJndiEnc.resourceRefs, moduleJndiEnc.resourceRefs, compJndiEnc.resourceRefs);
      }

   }

   private void buildResourceEnvRefInfos(JndiConsumer item, JndiEncInfo moduleJndiEnc, JndiEncInfo compJndiEnc) {
      for(ResourceEnvRef res : item.getResourceEnvRef()) {
         ResourceEnvReferenceInfo info = new ResourceEnvReferenceInfo();
         info.referenceName = res.getResourceEnvRefName();
         info.resourceEnvRefType = res.getResourceEnvRefType();
         info.resourceID = res.getMappedName();
         info.location = this.buildLocationInfo(res);
         info.targets.addAll(this.buildInjectionInfos(res));
         this.insert(info, this.appInfo.globalJndiEnc.resourceEnvRefs, this.appInfo.appJndiEnc.resourceEnvRefs, moduleJndiEnc.resourceEnvRefs, compJndiEnc.resourceEnvRefs);
      }

      for(MessageDestinationRef res : item.getMessageDestinationRef()) {
         ResourceEnvReferenceInfo info = new ResourceEnvReferenceInfo();
         info.referenceName = res.getMessageDestinationRefName();
         info.resourceEnvRefType = res.getMessageDestinationType();
         info.resourceID = res.getMappedName();
         info.location = this.buildLocationInfo(res);
         info.targets.addAll(this.buildInjectionInfos(res));
         this.insert(info, this.appInfo.globalJndiEnc.resourceEnvRefs, this.appInfo.appJndiEnc.resourceEnvRefs, moduleJndiEnc.resourceEnvRefs, compJndiEnc.resourceEnvRefs);
      }

   }

   private void buildEnvEntryInfos(JndiConsumer item, JndiEncInfo moduleJndiEnc, JndiEncInfo compJndiEnc) {
      for(EnvEntry env : item.getEnvEntry()) {
         if (env.getEnvEntryValue() != null || env.getLookupName() != null || this.isShareableJNDINamespace(env.getEnvEntryName()) || CompManagedBean.class.isInstance(item)) {
            EnvEntryInfo info = new EnvEntryInfo();
            info.referenceName = env.getEnvEntryName();
            info.type = env.getEnvEntryType();
            info.value = env.getEnvEntryValue();
            info.location = this.buildLocationInfo(env);
            info.targets.addAll(this.buildInjectionInfos(env));
            this.insert(info, this.appInfo.globalJndiEnc.envEntries, this.appInfo.appJndiEnc.envEntries, moduleJndiEnc.envEntries, compJndiEnc.envEntries);
         }
      }

   }

   private boolean isShareableJNDINamespace(String jndiName) {
      return jndiName.startsWith("java:global/") || jndiName.startsWith("java:app/") || jndiName.startsWith("java:module/");
   }

   private ReferenceLocationInfo buildLocationInfo(JndiReference reference) {
      String lookupName = reference.getLookupName();
      if (lookupName != null) {
         ReferenceLocationInfo location = new ReferenceLocationInfo();
         location.jndiName = lookupName;
         return location;
      } else {
         String mappedName = reference.getMappedName();
         if (mappedName != null && mappedName.startsWith("jndi:")) {
            ReferenceLocationInfo location = new ReferenceLocationInfo();
            String name = mappedName.substring(5);
            if (name.startsWith("ext://")) {
               URI uri = URLs.uri(name);
               location.jndiProviderId = uri.getHost();
               location.jndiName = uri.getPath();
            } else {
               location.jndiName = name;
            }

            return location;
         } else {
            return null;
         }
      }
   }

   private Collection<? extends InjectionInfo> buildInjectionInfos(Injectable injectable) {
      List<InjectionInfo> infos = new ArrayList();

      for(InjectionTarget target : injectable.getInjectionTarget()) {
         InjectionInfo info = new InjectionInfo();
         info.className = target.getInjectionTargetClass();
         info.propertyName = target.getInjectionTargetName();
         infos.add(info);
      }

      return infos;
   }

   public void buildDependsOnRefs(EnterpriseBean enterpriseBean, EnterpriseBeanInfo beanInfo, String moduleId) throws OpenEJBException {
      if (enterpriseBean instanceof SessionBean) {
         SessionBean sessionBean = (SessionBean)enterpriseBean;
         URI moduleUri;
         if (moduleId == null) {
            moduleUri = null;
         } else {
            moduleUri = URLs.uri(moduleId);
         }

         EjbResolver ejbResolver = this.getEjbResolver(moduleId);
         if (sessionBean.getDependsOn() != null) {
            for(String ejbName : sessionBean.getDependsOn()) {
               String deploymentId = ejbResolver.resolve(new SimpleRef(ejbName), moduleUri);
               if (deploymentId != null) {
                  beanInfo.dependsOn.add(deploymentId);
               }
            }
         }

      }
   }

   private boolean isIntefaceLocalBean(String interfaceClassName) {
      if (interfaceClassName == null) {
         return false;
      } else {
         EnterpriseBeanInfo beanInfo = this.getInterfaceBeanInfo(interfaceClassName);
         return this.isLocalBean(beanInfo) && beanInfo.parents.contains(interfaceClassName);
      }
   }

   private EnterpriseBeanInfo getInterfaceBeanInfo(String interfaceClassName) {
      if (interfaceClassName == null) {
         throw new IllegalArgumentException("interfaceClassName cannot be null");
      } else {
         List<EjbJarInfo> ejbJars = this.appInfo.ejbJars;

         for(EjbJarInfo ejbJar : ejbJars) {
            for(EnterpriseBeanInfo enterpriseBean : ejbJar.enterpriseBeans) {
               if (interfaceClassName.equals(enterpriseBean.ejbClass) || interfaceClassName.equals(enterpriseBean.local) || interfaceClassName.equals(enterpriseBean.remote) || enterpriseBean.businessLocal.contains(interfaceClassName) || enterpriseBean.businessRemote.contains(interfaceClassName)) {
                  return enterpriseBean;
               }
            }
         }

         for(EjbJarInfo ejbJar : ejbJars) {
            for(EnterpriseBeanInfo enterpriseBean : ejbJar.enterpriseBeans) {
               if (enterpriseBean.parents.contains(interfaceClassName)) {
                  return enterpriseBean;
               }
            }
         }

         return null;
      }
   }

   private boolean isLocalBean(EnterpriseBeanInfo beanInfo) {
      return beanInfo != null && beanInfo.localbean;
   }

   public <I extends InjectableInfo> void insert(I i, List<I> global, List<I> app, List<I> module, List<I> comp) {
      String name = i.referenceName;
      if (!name.startsWith("java:")) {
         i.referenceName = "comp/env/" + name;
         comp.add(i);
      } else if (name.startsWith("java:global/")) {
         i.referenceName = name.substring(5);
         global.add(i);
      } else if (name.startsWith("java:app/")) {
         i.referenceName = name.substring(5);
         app.add(i);
      } else if (name.startsWith("java:module/")) {
         i.referenceName = name.substring(5);
         module.add(i);
      } else if (name.startsWith("java:comp/")) {
         i.referenceName = name.substring(5);
         comp.add(i);
      } else if (name.startsWith("java:/")) {
         i.referenceName = name.substring(6);
         comp.add(i);
      } else {
         logger.warning("config.invalid.referenceName.suffix", name);
      }

   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, JndiEncInfoBuilder.class);
   }

   private static class SimpleRef implements EjbResolver.Reference {
      private final String name;

      public SimpleRef(String name) {
         this.name = name;
      }

      public String getEjbLink() {
         return this.name;
      }

      public String getHome() {
         return null;
      }

      public String getInterface() {
         return null;
      }

      public String getMappedName() {
         return null;
      }

      public String getName() {
         return this.name;
      }

      public EjbResolver.Type getRefType() {
         return EjbResolver.Type.UNKNOWN;
      }
   }

   private static class Ref implements EjbResolver.Reference {
      private final EjbReference ref;

      public Ref(EjbReference ref) {
         this.ref = ref;
      }

      public String getName() {
         return this.ref.getName();
      }

      public String getEjbLink() {
         return this.ref.getEjbLink();
      }

      public String getHome() {
         return this.ref.getHome();
      }

      public String getInterface() {
         return this.ref.getInterface();
      }

      public String getMappedName() {
         return this.ref.getMappedName();
      }

      public EjbResolver.Type getRefType() {
         switch (this.ref.getRefType()) {
            case LOCAL:
               return EjbResolver.Type.LOCAL;
            case REMOTE:
               return EjbResolver.Type.REMOTE;
            case UNKNOWN:
               return EjbResolver.Type.UNKNOWN;
            default:
               return EjbResolver.Type.UNKNOWN;
         }
      }
   }
}
