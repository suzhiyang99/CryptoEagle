package org.apache.openejb.config;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.oejb3.EjbDeployment;
import org.apache.openejb.jee.oejb3.OpenejbJar;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.openejb.util.SuperProperties;

public class BeanProperties implements DynamicDeployer {
   private static final Logger log;
   private final Map<String, Properties> additionalProperties = new HashMap();
   private final Properties globalProperties = new Properties();

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      Properties base = new Properties();
      base.putAll(SystemInstance.get().getProperties());
      base.putAll(appModule.getProperties());

      for(EjbModule module : appModule.getEjbModules()) {
         Properties overrides = (new SuperProperties()).caseInsensitive(true);
         overrides.putAll(base);
         overrides.putAll(module.getProperties());
         if (module.getOpenejbJar() == null) {
            module.setOpenejbJar(new OpenejbJar());
         }

         OpenejbJar openejbJar = module.getOpenejbJar();
         Map<String, EjbDeployment> deploymentMap = openejbJar.getDeploymentsByEjbName();

         for(EnterpriseBean bean : module.getEjbJar().getEnterpriseBeans()) {
            SuperProperties properties = (new SuperProperties()).caseInsensitive(true);
            properties.putAll(this.globalProperties);
            String additionalKey = bean.getEjbName();
            if (this.additionalProperties.containsKey(additionalKey)) {
               for(Map.Entry<Object, Object> entry : ((Properties)this.additionalProperties.get(additionalKey)).entrySet()) {
                  properties.put(entry.getKey().toString(), entry.getValue().toString());
               }
            }

            EjbDeployment deployment = (EjbDeployment)deploymentMap.get(bean.getEjbName());
            if (deployment != null) {
               properties.putAll(deployment.getProperties());
               deployment.getProperties().clear();
            }

            String id = bean.getEjbName() + ".";

            for(Map.Entry<Object, Object> entry : overrides.entrySet()) {
               String key = entry.getKey().toString();
               if (key.startsWith(id)) {
                  String property = key.substring(id.length());
                  if (properties.containsKey(property)) {
                     log.debug("Overriding ejb " + bean.getEjbName() + " property " + property + "=" + entry.getValue());
                  } else {
                     log.debug("Adding ejb " + bean.getEjbName() + " property " + property + "=" + entry.getValue());
                  }

                  properties.put(property, entry.getValue());
               }
            }

            if (properties.size() > 0) {
               if (deployment == null) {
                  EjbDeployment ejbDeployment = openejbJar.addEjbDeployment(bean);
                  ejbDeployment.getProperties().putAll(properties);
               } else {
                  deployment.getProperties().putAll(properties);
               }
            }
         }
      }

      this.additionalProperties.clear();
      this.globalProperties.clear();
      return appModule;
   }

   public void addProperties(String id, Properties properties) {
      if (this.additionalProperties.containsKey(id)) {
         ((Properties)this.additionalProperties.get(id)).putAll(properties);
      } else {
         this.additionalProperties.put(id, properties);
      }

   }

   public void addGlobalProperties(Properties properties) {
      this.globalProperties.putAll(properties);
   }

   public void addGlobalProperties(String prefix, Properties properties) {
      if (prefix != null && !prefix.isEmpty()) {
         for(Map.Entry<Object, Object> entry : properties.entrySet()) {
            this.globalProperties.put(prefix + "." + entry.getKey(), entry.getValue());
         }
      } else {
         this.addGlobalProperties(properties);
      }

   }

   static {
      log = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, ModuleProperties.class);
   }
}
