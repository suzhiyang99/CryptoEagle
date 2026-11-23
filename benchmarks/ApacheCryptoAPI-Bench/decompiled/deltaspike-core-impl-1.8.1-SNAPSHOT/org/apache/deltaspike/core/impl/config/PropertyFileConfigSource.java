package org.apache.deltaspike.core.impl.config;

import java.net.URL;
import org.apache.deltaspike.core.util.PropertyFileUtils;

class PropertyFileConfigSource extends PropertiesConfigSource {
   private String fileName;

   PropertyFileConfigSource(URL propertyFileUrl) {
      super(PropertyFileUtils.loadProperties(propertyFileUrl));
      this.fileName = propertyFileUrl.toExternalForm();
      this.initOrdinal(100);
   }

   public String getConfigName() {
      return this.fileName;
   }
}
