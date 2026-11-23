package org.apache.deltaspike.core.impl.config;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.deltaspike.core.api.config.PropertyFileConfig;
import org.apache.deltaspike.core.spi.config.ConfigSource;
import org.apache.deltaspike.core.spi.config.ConfigSourceProvider;
import org.apache.deltaspike.core.util.ServiceUtils;

public class DefaultConfigSourceProvider implements ConfigSourceProvider {
   private static final Logger LOG = Logger.getLogger(DefaultConfigSourceProvider.class.getName());
   private static final String PROPERTY_FILE_NAME = "apache-deltaspike.properties";
   private static final String PROPERTY_FILE_RESOURCE = "META-INF/apache-deltaspike.properties";
   private static final String PROPERTY_FILE_HOME_NAME = "/.deltaspike/apache-deltaspike.properties";
   private List<ConfigSource> configSources = new ArrayList();

   public DefaultConfigSourceProvider() {
      this.configSources.add(new SystemPropertyConfigSource());
      this.configSources.add(new EnvironmentPropertyConfigSource());
      this.configSources.add(new LocalJndiConfigSource());
      this.addUserHomeConfigSource();
      EnvironmentPropertyConfigSourceProvider epcsp = new EnvironmentPropertyConfigSourceProvider("META-INF/apache-deltaspike.properties", true);
      this.configSources.addAll(epcsp.getConfigSources());
      this.registerPropertyFileConfigs();
   }

   private void addUserHomeConfigSource() {
      String userHome = System.getProperty("user.home");
      if (userHome != null && !userHome.isEmpty()) {
         File dsHome = new File(userHome, "/.deltaspike/apache-deltaspike.properties");
         if (dsHome.exists()) {
            try {
               ConfigSource dsHomeConfigSource = new PropertyFileConfigSource(dsHome.toURI().toURL());
               this.configSources.add(dsHomeConfigSource);
               LOG.log(Level.INFO, "Reading configuration from {}", dsHome.getAbsolutePath());
            } catch (MalformedURLException e) {
               LOG.log(Level.WARNING, "Could not read configuration from " + dsHome.getAbsolutePath(), e);
            }
         }
      }

   }

   private void registerPropertyFileConfigs() {
      for(PropertyFileConfig propertyFileConfig : ServiceUtils.loadServiceImplementations(PropertyFileConfig.class)) {
         EnvironmentPropertyConfigSourceProvider environmentPropertyConfigSourceProvider = new EnvironmentPropertyConfigSourceProvider(propertyFileConfig.getPropertyFileName(), propertyFileConfig.isOptional());
         this.configSources.addAll(environmentPropertyConfigSourceProvider.getConfigSources());
      }

   }

   public List<ConfigSource> getConfigSources() {
      return this.configSources;
   }
}
