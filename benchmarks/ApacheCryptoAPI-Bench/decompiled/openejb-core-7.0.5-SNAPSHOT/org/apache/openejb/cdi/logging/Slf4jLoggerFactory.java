package org.apache.openejb.cdi.logging;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import org.apache.openejb.log.logger.Slf4jLogger;
import org.apache.webbeans.logger.WebBeansLoggerFactory;

public class Slf4jLoggerFactory implements WebBeansLoggerFactory {
   public Logger getLogger(Class<?> clazz, Locale desiredLocale) {
      return new Slf4jLogger(clazz.getName(), ResourceBundle.getBundle("openwebbeans/Messages", desiredLocale).toString());
   }

   public Logger getLogger(Class<?> clazz) {
      return new Slf4jLogger(clazz.getName(), "openwebbeans/Messages");
   }
}
