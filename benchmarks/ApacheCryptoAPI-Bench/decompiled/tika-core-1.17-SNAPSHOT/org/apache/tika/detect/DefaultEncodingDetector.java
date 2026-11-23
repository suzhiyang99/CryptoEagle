package org.apache.tika.detect;

import java.util.Collection;
import org.apache.tika.config.ServiceLoader;

public class DefaultEncodingDetector extends CompositeEncodingDetector {
   public DefaultEncodingDetector() {
      this(new ServiceLoader(DefaultEncodingDetector.class.getClassLoader()));
   }

   public DefaultEncodingDetector(ServiceLoader loader) {
      super(loader.loadServiceProviders(EncodingDetector.class));
   }

   public DefaultEncodingDetector(ServiceLoader loader, Collection<Class<? extends EncodingDetector>> excludeEncodingDetectors) {
      super(loader.loadServiceProviders(EncodingDetector.class), excludeEncodingDetectors);
   }
}
