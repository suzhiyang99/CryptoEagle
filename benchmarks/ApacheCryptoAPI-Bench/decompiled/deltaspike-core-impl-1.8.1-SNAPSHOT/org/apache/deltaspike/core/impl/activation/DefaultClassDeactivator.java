package org.apache.deltaspike.core.impl.activation;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.spi.activation.ClassDeactivator;
import org.apache.deltaspike.core.spi.activation.Deactivatable;

public class DefaultClassDeactivator implements ClassDeactivator {
   public static final String KEY_PREFIX = "deactivate.";
   private static final Logger LOG = Logger.getLogger(DefaultClassDeactivator.class.getName());

   public Boolean isActivated(Class<? extends Deactivatable> targetClass) {
      String key = "deactivate." + targetClass.getName();
      String value = ConfigResolver.getPropertyValue(key);
      if (value == null) {
         return null;
      } else {
         if (LOG.isLoggable(Level.FINE)) {
            LOG.log(Level.FINE, "Deactivation setting for {0} found to be {1} based on configuration.", new Object[]{key, value});
         }

         return !Boolean.valueOf(value);
      }
   }
}
