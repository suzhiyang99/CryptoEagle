package org.apache.openejb.config;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.openejb.OpenEJBException;
import org.apache.openejb.jee.EnterpriseBean;
import org.apache.openejb.jee.EnvEntry;
import org.apache.openejb.jee.JndiConsumer;
import org.apache.openejb.loader.IO;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class EnvEntriesPropertiesDeployer implements DynamicDeployer {
   private static final Logger log;
   public static final String ENV_ENTRY_PROPERTIES = "env-entries.properties";
   private final String descriptorName;
   private final Map<String, String> additionalEnvEntries;

   public EnvEntriesPropertiesDeployer() {
      this("env-entries.properties");
   }

   public EnvEntriesPropertiesDeployer(String descriptor) {
      this.additionalEnvEntries = new HashMap();
      this.descriptorName = descriptor;
   }

   public AppModule deploy(AppModule appModule) throws OpenEJBException {
      for(ClientModule module : appModule.getClientModules()) {
         if (module.getApplicationClient() != null) {
            for(Map.Entry<String, String> entry : this.getEnvEntries(module).entrySet()) {
               EnvEntry envEntry = new EnvEntry((String)entry.getKey(), "java.lang.String", (String)entry.getValue());
               this.apply(module.getApplicationClient(), envEntry, "AppClient");
            }
         }
      }

      for(WebModule webModule : appModule.getWebModules()) {
         this.deploy(webModule);
      }

      for(EjbModule module : appModule.getEjbModules()) {
         for(Map.Entry<String, String> entry : this.getEnvEntries(module).entrySet()) {
            EnvEntry envEntry = new EnvEntry((String)entry.getKey(), "java.lang.String", (String)entry.getValue());
            if (envEntry.getName().contains(".")) {
               String name = envEntry.getName();
               String ejbName = name.substring(0, name.indexOf(46));
               name = name.substring(name.indexOf(46) + 1);
               EnterpriseBean bean = module.getEjbJar().getEnterpriseBean(ejbName);
               if (bean != null) {
                  envEntry.setName(name);
                  this.apply(bean, envEntry, bean.getEjbName());
                  continue;
               }
            }

            for(EnterpriseBean bean : module.getEjbJar().getEnterpriseBeans()) {
               this.apply(bean, envEntry, bean.getEjbName());
            }
         }
      }

      return appModule;
   }

   public WebModule deploy(WebModule webModule) {
      for(Map.Entry<String, String> entry : this.getEnvEntries(webModule).entrySet()) {
         EnvEntry envEntry = new EnvEntry((String)entry.getKey(), "java.lang.String", (String)entry.getValue());
         this.apply(webModule.getWebApp(), envEntry, "WebApp");
      }

      return webModule;
   }

   private void apply(JndiConsumer bean, EnvEntry newEntry, String componentName) {
      EnvEntry entry = (EnvEntry)bean.getEnvEntryMap().get(newEntry.getName());
      if (entry == null) {
         entry = (EnvEntry)bean.getEnvEntryMap().get("java:comp/env/" + newEntry.getName());
      }

      if (entry != null) {
         if (SystemInstance.get().getOptions().get("envprops.override", false)) {
            log.debug("envprops.override", componentName, entry.getName(), entry.getEnvEntryValue(), newEntry.getEnvEntryValue());
            entry.setEnvEntryValue(newEntry.getEnvEntryValue());
         }

      } else {
         log.debug("envprops.add", componentName, newEntry.getName(), newEntry.getEnvEntryValue());
         bean.getEnvEntry().add(newEntry);
      }
   }

   private Map<String, String> getEnvEntries(DeploymentModule module) {
      Object value = module.getAltDDs().get(this.descriptorName);
      if (value instanceof Map) {
         return (Map)value;
      } else {
         URL propsUrl = (URL)value;
         if (propsUrl == null) {
            propsUrl = (URL)module.getAltDDs().get(this.descriptorName);
         }

         if (propsUrl == null) {
            return Collections.emptyMap();
         } else {
            try {
               Properties envEntriesProps = IO.readProperties(propsUrl);
               envEntriesProps.putAll(this.additionalEnvEntries);
               return new HashMap(envEntriesProps);
            } catch (IOException e) {
               log.error("envprops.notLoaded", e, module.getModuleId(), propsUrl.toExternalForm());
               return Collections.emptyMap();
            }
         }
      }
   }

   public void addEnvEntries(String key, String value) {
      this.additionalEnvEntries.put(key, value);
   }

   public void resetAdditionalEnvEntries() {
      this.additionalEnvEntries.clear();
   }

   static {
      log = Logger.getInstance(LogCategory.OPENEJB_STARTUP_CONFIG, EnvEntriesPropertiesDeployer.class);
   }
}
