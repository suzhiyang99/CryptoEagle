package org.apache.deltaspike.core.impl.config;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.deltaspike.core.spi.config.ConfigSource;
import org.apache.deltaspike.core.spi.config.ConfigSourceProvider;
import org.apache.deltaspike.core.util.PropertyFileUtils;

class EnvironmentPropertyConfigSourceProvider implements ConfigSourceProvider {
   private static final Logger LOG = Logger.getLogger(EnvironmentPropertyConfigSourceProvider.class.getName());
   private List<ConfigSource> configSources = new ArrayList();

   EnvironmentPropertyConfigSourceProvider(String propertyFileName, boolean optional) {
      try {
         Enumeration<URL> propertyFileUrls = PropertyFileUtils.resolvePropertyFiles(propertyFileName);
         if (!optional && !propertyFileUrls.hasMoreElements()) {
            throw new IllegalStateException(propertyFileName + " wasn't found.");
         } else {
            while(propertyFileUrls.hasMoreElements()) {
               URL propertyFileUrl = (URL)propertyFileUrls.nextElement();
               LOG.log(Level.INFO, "Custom config found by DeltaSpike. Name: ''{0}'', URL: ''{1}''", new Object[]{propertyFileName, propertyFileUrl});
               this.configSources.add(new PropertyFileConfigSource(propertyFileUrl));
            }

         }
      } catch (IOException ioe) {
         throw new IllegalStateException("problem while loading DeltaSpike property files", ioe);
      } catch (URISyntaxException ue) {
         throw new IllegalStateException("Property file URL is malformed", ue);
      }
   }

   public List<ConfigSource> getConfigSources() {
      return this.configSources;
   }
}
