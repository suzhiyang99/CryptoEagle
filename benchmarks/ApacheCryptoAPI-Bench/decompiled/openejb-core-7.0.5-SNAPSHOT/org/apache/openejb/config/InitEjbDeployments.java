package org.apache.openejb.config;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.api.EjbDeployment;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EntityBean;
import org.apache.openejb.jee.ManagedBean;
import org.apache.openejb.jee.PersistenceType;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.StringTemplate;

public class InitEjbDeployments implements DynamicDeployer {
   public static Logger logger;
   private final StringTemplate deploymentIdTemplate;
   private static final String DEPLOYMENT_ID_FORMAT = "openejb.deploymentId.format";

   public InitEjbDeployments() {
      String format = SystemInstance.get().getOptions().get("openejb.deploymentId.format", "{ejbName}");
      this.deploymentIdTemplate = new StringTemplate(format);
   }

   public synchronized AppModule deploy(AppModule appModule) throws OpenEJBException {
      Set<String> abstractSchemaNames = new HashSet();

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
            if (isCmpEntity(bean)) {
               EntityBean entity = (EntityBean)bean;
               String name = entity.getAbstractSchemaName();
               if (name != null) {
                  abstractSchemaNames.add(name);
               }
            }
         }
      }

      Map<String, String> contextData = new HashMap();
      contextData.put("appId", appModule.getModuleId());

      for(EjbModule ejbModule : appModule.getEjbModules()) {
         contextData.put("host", ejbModule.isWebapp() ? this.findHost(ejbModule.getModuleId(), appModule.getWebModules()) : appModule.uniqueHostIfExists());
         contextData.put("hash", Integer.toString(ejbModule.hashCode()));
         contextData.put("ejbJarId", ejbModule.getModuleId());
         this.deploy(ejbModule, contextData, abstractSchemaNames);
      }

      contextData.clear();
      return appModule;
   }

   private String findCommonHost(Collection<WebModule> webModules) {
      String host = null;

      for(WebModule w : webModules) {
         String wHost = w.getHost();
         if (host == null) {
            host = wHost;
         } else if (!host.equals(wHost)) {
            return "lib";
         }
      }

      return host != null ? host : "localhost";
   }

   private String findHost(String id, Collection<WebModule> webModules) {
      for(WebModule w : webModules) {
         if (w.getModuleId().equals(id)) {
            String host = w.getHost();
            return host != null ? host : "localhost";
         }
      }

      return "localhost";
   }

   public EjbModule deploy(EjbModule ejbModule) throws OpenEJBException {
      return this.deploy(ejbModule, new HashMap(), new HashSet());
   }

   private EjbModule deploy(EjbModule ejbModule, Map<String, String> contextData, Set<String> abstractSchemaNames) throws OpenEJBException {
      contextData.put("moduleId", ejbModule.getModuleId());
      contextData.put("moduleUri", ejbModule.getModuleUri().toString());
      OpenejbJar openejbJar;
      if (ejbModule.getOpenejbJar() != null) {
         openejbJar = ejbModule.getOpenejbJar();
      } else {
         openejbJar = new OpenejbJar();
         ejbModule.setOpenejbJar(openejbJar);
      }

      StringTemplate deploymentIdTemplate = this.deploymentIdTemplate;
      if (openejbJar.getProperties().containsKey("openejb.deploymentId.format")) {
         String format = openejbJar.getProperties().getProperty("openejb.deploymentId.format");
         logger.info("Using openejb.deploymentId.format '" + format + "'");
         deploymentIdTemplate = new StringTemplate(format);
      }

      for(EnterpriseBean bean : ejbModule.getEjbJar().getEnterpriseBeans()) {
         EjbDeployment annotation = this.getEjbDeploymentAnnotation(ejbModule, bean);
         org.apache.openejb.jee.oejb3.EjbDeployment ejbDeployment = (org.apache.openejb.jee.oejb3.EjbDeployment)openejbJar.getDeploymentsByEjbName().get(bean.getEjbName());
         if (ejbDeployment != null) {
            if (ejbDeployment.getDeploymentId() == null) {
               if (annotation != null && this.isDefined(annotation.id())) {
                  StringTemplate var17 = new StringTemplate(annotation.id());
                  ejbDeployment.setDeploymentId(this.formatDeploymentId(bean, contextData, var17));
               } else {
                  ejbDeployment.setDeploymentId(this.formatDeploymentId(bean, contextData, deploymentIdTemplate));
                  logger.info("Auto-assigning deployment-id for ejb " + bean.getEjbName() + ": EjbDeployment(deployment-id=" + ejbDeployment.getDeploymentId() + ")");
               }
            }
         } else {
            ejbDeployment = new org.apache.openejb.jee.oejb3.EjbDeployment();
            ejbDeployment.setEjbName(bean.getEjbName());
            if (annotation != null && this.isDefined(annotation.id())) {
               StringTemplate template = new StringTemplate(annotation.id());
               ejbDeployment.setDeploymentId(this.formatDeploymentId(bean, contextData, template));
            } else {
               ejbDeployment.setDeploymentId(this.formatDeploymentId(bean, contextData, deploymentIdTemplate));
               if (!(bean instanceof ManagedBean) || !((ManagedBean)bean).isHidden()) {
                  logger.info("Auto-deploying ejb " + bean.getEjbName() + ": EjbDeployment(deployment-id=" + ejbDeployment.getDeploymentId() + ")");
               }
            }

            openejbJar.getEjbDeployment().add(ejbDeployment);
         }

         if (ejbDeployment.getContainerId() == null && annotation != null && this.isDefined(annotation.container())) {
            ejbDeployment.setContainerId(annotation.container());
         }

         if (isCmpEntity(bean)) {
            EntityBean entity = (EntityBean)bean;
            if (entity.getAbstractSchemaName() == null) {
               String abstractSchemaName = bean.getEjbName().trim().replaceAll("[ \\t\\n\\r-]+", "_");
               if (abstractSchemaNames.contains(abstractSchemaName)) {
                  int i;
                  for(i = 2; abstractSchemaNames.contains(abstractSchemaName + i); ++i) {
                  }

                  abstractSchemaName = abstractSchemaName + i;
               }

               abstractSchemaNames.add(abstractSchemaName);
               entity.setAbstractSchemaName(abstractSchemaName);
            }
         }
      }

      return ejbModule;
   }

   private EjbDeployment getEjbDeploymentAnnotation(EjbModule ejbModule, EnterpriseBean bean) {
      try {
         Class<?> beanClass = ejbModule.getClassLoader().loadClass(bean.getEjbClass());
         return (EjbDeployment)beanClass.getAnnotation(EjbDeployment.class);
      } catch (ClassNotFoundException var4) {
         return null;
      }
   }

   private boolean isDefined(String s) {
      return s != null && !"".equals(s);
   }

   private static boolean isCmpEntity(EnterpriseBean bean) {
      return bean instanceof EntityBean && ((EntityBean)bean).getPersistenceType() == PersistenceType.CONTAINER;
   }

   private String formatDeploymentId(EnterpriseBean bean, Map<String, String> contextData, StringTemplate template) {
      contextData.put("ejbType", bean.getClass().getSimpleName());
      contextData.put("ejbClass", bean.getEjbClass());
      int simpleNameIdx = bean.getEjbClass().lastIndexOf(".");
      contextData.put("ejbClass.simpleName", bean.getEjbClass().substring(simpleNameIdx + 1));
      contextData.put("ejbName", bean.getEjbName());
      String name = template.apply(contextData);
      return bean instanceof CompManagedBean ? name + System.identityHashCode(bean) : name;
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }
}
