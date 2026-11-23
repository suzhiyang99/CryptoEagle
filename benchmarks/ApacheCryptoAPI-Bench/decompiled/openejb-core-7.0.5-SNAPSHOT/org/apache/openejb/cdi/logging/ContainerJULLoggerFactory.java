package org.apache.openejb.cdi.logging;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import org.apache.webbeans.logger.WebBeansLoggerFactory;

public class ContainerJULLoggerFactory implements WebBeansLoggerFactory {
   public Logger getLogger(Class<?> clazz, Locale desiredLocale) {
      Thread th = Thread.currentThread();
      ClassLoader l = th.getContextClassLoader();
      th.setContextClassLoader(WebBeansLoggerFactory.class.getClassLoader());

      Logger var5;
      try {
         var5 = Logger.getLogger(clazz.getName(), ResourceBundle.getBundle("openwebbeans/Messages", desiredLocale).toString());
      } finally {
         th.setContextClassLoader(l);
      }

      return var5;
   }

   public Logger getLogger(Class<?> clazz) {
      Thread th = Thread.currentThread();
      ClassLoader l = th.getContextClassLoader();
      th.setContextClassLoader(WebBeansLoggerFactory.class.getClassLoader());

      Logger var4;
      try {
         var4 = Logger.getLogger(clazz.getName(), "openwebbeans/Messages");
      } finally {
         th.setContextClassLoader(l);
      }

      return var4;
   }
}
