package org.apache.meecrowave.logging.log4j2;

import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Order;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;

@Order(100)
@Plugin(
   name = "MeecrowaveConfigurationFactory",
   category = "ConfigurationFactory"
)
public class MeecrowaveLog4j2ConfigurationFactory extends XmlConfigurationFactory {
   private static final String[] TYPES = new String[]{".xml", ".meecrowave-logging", "*"};

   public String[] getSupportedTypes() {
      return TYPES;
   }

   public Configuration getConfiguration(LoggerContext loggerContext, ConfigurationSource source) {
      return super.getConfiguration(loggerContext, source);
   }
}
