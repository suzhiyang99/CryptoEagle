package org.apache.taverna.workbench.myexperiment.config;

import java.util.HashMap;
import java.util.Map;
import org.apache.taverna.configuration.AbstractConfigurable;
import org.apache.taverna.configuration.ConfigurationManager;

public class MyExperimentConfiguration extends AbstractConfigurable {
   private Map<String, String> defaultPropertyMap;

   public MyExperimentConfiguration(ConfigurationManager configurationManager) {
      super(configurationManager);
   }

   public String getCategory() {
      return "general";
   }

   public Map<String, String> getDefaultPropertyMap() {
      if (this.defaultPropertyMap == null) {
         this.defaultPropertyMap = new HashMap();
      }

      return this.defaultPropertyMap;
   }

   public String getDisplayName() {
      return "myExperiment";
   }

   public String getFilePrefix() {
      return "myExperiment";
   }

   public String getUUID() {
      return "d25867g1-6078-22ee-bf27-1911311d0b77";
   }
}
