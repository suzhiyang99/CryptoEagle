package org.apache.taverna.workbench.myexperiment.config;

import javax.swing.JPanel;
import org.apache.taverna.configuration.Configurable;
import org.apache.taverna.configuration.ConfigurationManager;
import org.apache.taverna.configuration.ConfigurationUIFactory;

public class MyExperimentConfigurationUIFactory implements ConfigurationUIFactory {
   private ConfigurationManager configurationManager;

   public boolean canHandle(String uuid) {
      return uuid.equals(this.getConfigurable().getUUID());
   }

   public JPanel getConfigurationPanel() {
      return new MyExperimentConfigurationPanel();
   }

   public Configurable getConfigurable() {
      return new MyExperimentConfiguration(this.configurationManager);
   }

   public void setConfigurationManager(ConfigurationManager configurationManager) {
      this.configurationManager = configurationManager;
   }
}
