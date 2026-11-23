package org.apache.openejb.config;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.openejb.config.sys.Resources;
import org.apache.openejb.jee.bval.ValidationConfigType;
import org.apache.openejb.util.SuperProperties;

public class Module {
   private static int currentId = 1;
   private boolean isStandaloneModule;
   private ClassLoader classLoader;
   private ValidationConfigType validationConfig;
   private final Map<String, Object> altDDs;
   private String uniqueId;
   private AppModule appModule;
   private Resources resources;
   private final Set<String> mbeans;
   private final Properties properties;

   public Module(boolean needId) {
      this.isStandaloneModule = true;
      this.altDDs = new HashMap();
      this.mbeans = new HashSet();
      this.properties = (new SuperProperties()).caseInsensitive(true);
      if (needId) {
         this.uniqueId = Integer.toString(currentId++);
      }

   }

   public Module() {
      this(true);
   }

   public Properties getProperties() {
      return this.properties;
   }

   public ValidationConfigType getValidationConfig() {
      return this.validationConfig;
   }

   public void setValidationConfig(ValidationConfigType v) {
      this.validationConfig = v;
   }

   public Map<String, Object> getAltDDs() {
      return this.altDDs;
   }

   public String getUniqueId() {
      return this.uniqueId;
   }

   public boolean isStandaloneModule() {
      return this.isStandaloneModule;
   }

   public void setStandaloneModule(boolean isStandalone) {
      this.isStandaloneModule = isStandalone;
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public void initAppModule(AppModule appModule) {
      if (this.appModule != appModule) {
         if (this.appModule != null) {
            throw new UnsupportedOperationException("AppModule is already set");
         } else {
            this.appModule = appModule;
            if (this.resources != null) {
               this.appModule.getResources().addAll(this.resources.getResource());
               this.appModule.getContainers().addAll(this.resources.getContainer());
               this.appModule.getServices().addAll(this.resources.getService());
            }

         }
      }
   }

   public AppModule getAppModule() {
      return this.appModule;
   }

   public void initResources(Resources resources) {
      if (this.resources != null) {
         throw new UnsupportedOperationException("resources.xml is already set");
      } else {
         this.resources = resources;
      }
   }

   public Set<String> getMbeans() {
      return this.mbeans;
   }
}
