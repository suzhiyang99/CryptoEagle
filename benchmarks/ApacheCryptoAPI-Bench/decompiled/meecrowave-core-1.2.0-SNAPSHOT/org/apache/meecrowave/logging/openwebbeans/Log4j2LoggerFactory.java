package org.apache.meecrowave.logging.openwebbeans;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import org.apache.meecrowave.logging.jul.Log4j2Logger;
import org.apache.webbeans.logger.WebBeansLoggerFactory;

public class Log4j2LoggerFactory implements WebBeansLoggerFactory {
   public Logger getLogger(Class<?> clazz, Locale desiredLocale) {
      return new Log4j2Logger(clazz.getName(), ResourceBundle.getBundle("openwebbeans/Messages", desiredLocale).toString());
   }

   public Logger getLogger(Class<?> clazz) {
      return new Log4j2Logger(clazz.getName(), "openwebbeans/Messages");
   }
}
