package org.apache.deltaspike.core.impl.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.deltaspike.core.spi.config.ConfigSource;

public abstract class BaseConfigSource implements ConfigSource {
   protected Logger log = Logger.getLogger(this.getClass().getName());
   private int ordinal = 1000;

   public int getOrdinal() {
      return this.ordinal;
   }

   protected void initOrdinal(int defaultOrdinal) {
      this.ordinal = defaultOrdinal;
      String configuredOrdinalString = this.getPropertyValue("deltaspike_ordinal");

      try {
         if (configuredOrdinalString != null) {
            this.ordinal = Integer.parseInt(configuredOrdinalString.trim());
         }
      } catch (NumberFormatException var4) {
         this.log.log(Level.WARNING, "The configured config-ordinal isn't a valid integer. Invalid value: " + configuredOrdinalString);
      }

   }
}
