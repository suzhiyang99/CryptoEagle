package org.apache.manifoldcf.core.i18n;

import java.io.InputStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;
import org.apache.commons.collections.ExtendedProperties;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.Logging;
import org.apache.velocity.app.VelocityEngine;

public class Messages {
   protected static Set<BundleKey> bundleSet = new HashSet();
   protected static Set<MessageKey> messageSet = new HashSet();
   protected static Set<ResourceKey> resourceSet = new HashSet();

   protected Messages() {
   }

   public static VelocityEngine createVelocityEngine(Class classInstance) throws ManifoldCFException {
      VelocityEngine engine = new VelocityEngine();
      ExtendedProperties configuration = new ExtendedProperties();
      configuration.setProperty("resource.loader", "mcf");
      configuration.setProperty("mcf.resource.loader.instance", new MCFVelocityResourceLoader(classInstance));
      engine.setExtendedProperties(configuration);
      engine.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.Log4JLogChute");
      engine.setProperty("runtime.log.logsystem.log4j.logger", "velocity");
      return engine;
   }

   public static InputStream getResourceAsStream(Class classInstance, String pathName, Locale originalLocale, String resourceKey) throws ManifoldCFException {
      InputStream is = classInstance.getResourceAsStream(localizeResourceName(pathName, resourceKey, originalLocale));
      if (is == null) {
         complainMissingResource("No resource in path '" + pathName + "' named '" + resourceKey + "' found for locale '" + originalLocale.toString() + "'", new Exception("Resource not found"), pathName, originalLocale, resourceKey);
         Locale locale = new Locale(originalLocale.getLanguage());
         is = classInstance.getResourceAsStream(localizeResourceName(pathName, resourceKey, locale));
         if (is == null) {
            complainMissingResource("No resource in path '" + pathName + "' named '" + resourceKey + "' found for locale '" + locale.toString() + "'", new Exception("Resource not found"), pathName, locale, resourceKey);
            locale = Locale.US;
            is = classInstance.getResourceAsStream(localizeResourceName(pathName, resourceKey, locale));
            if (is == null) {
               complainMissingResource("No resource in path '" + pathName + "' named '" + resourceKey + "' found for locale '" + locale.toString() + "'", new Exception("Resource not found"), pathName, locale, resourceKey);
               locale = new Locale(locale.getLanguage());
               is = classInstance.getResourceAsStream(localizeResourceName(pathName, resourceKey, locale));
               if (is == null) {
                  complainMissingResource("No resource in path '" + pathName + "' named '" + resourceKey + "' found for locale '" + locale.toString() + "'", new Exception("Resource not found"), pathName, locale, resourceKey);
                  is = classInstance.getResourceAsStream(localizeResourceName(pathName, resourceKey, (Locale)null));
                  if (is == null) {
                     throw new ManifoldCFException("No matching language resource in path '" + pathName + "' named '" + resourceKey + "' found for locale '" + originalLocale.toString() + "'");
                  }
               }
            }
         }
      }

      return is;
   }

   private static String localizeResourceName(String pathName, String resourceName, Locale locale) {
      if (locale == null) {
         return resourceName;
      } else {
         int dotIndex = resourceName.lastIndexOf(".");
         return dotIndex == -1 ? resourceName + "_" + locale.toString() : resourceName.substring(0, dotIndex) + "_" + locale.toString() + resourceName.substring(dotIndex);
      }
   }

   public static ResourceBundle getResourceBundle(Class clazz, String bundleName, Locale locale) {
      ClassLoader classLoader = clazz.getClassLoader();

      ResourceBundle resources;
      try {
         resources = ResourceBundle.getBundle(bundleName, locale, classLoader);
      } catch (MissingResourceException e) {
         complainMissingBundle("Missing resource bundle '" + bundleName + "' for locale '" + locale.toString() + "': " + e.getMessage() + "; trying " + locale.getLanguage(), e, bundleName, locale);
         locale = new Locale(locale.getLanguage());

         try {
            resources = ResourceBundle.getBundle(bundleName, locale, classLoader);
         } catch (MissingResourceException e2) {
            complainMissingBundle("Missing resource bundle '" + bundleName + "' for locale '" + locale.toString() + "': " + e2.getMessage() + "; trying en_US", e2, bundleName, locale);
            locale = Locale.US;

            try {
               resources = ResourceBundle.getBundle(bundleName, locale, classLoader);
            } catch (MissingResourceException e3) {
               complainMissingBundle("No backup en_US bundle found! " + e3.getMessage(), e3, bundleName, locale);
               locale = new Locale(locale.getLanguage());

               try {
                  resources = ResourceBundle.getBundle(bundleName, locale, classLoader);
               } catch (MissingResourceException e4) {
                  complainMissingBundle("No backup en bundle found! " + e4.getMessage(), e4, bundleName, locale);
                  return null;
               }
            }
         }
      }

      return resources;
   }

   public static String getMessage(Class clazz, String bundleName, Locale locale, String messageKey) {
      ResourceBundle resources = getResourceBundle(clazz, bundleName, locale);
      return resources == null ? null : getMessage(resources, bundleName, locale, messageKey);
   }

   public static String getMessage(ResourceBundle resources, String bundleName, Locale locale, String messageKey) {
      try {
         return resources.getString(messageKey);
      } catch (MissingResourceException e) {
         complainMissingMessage("Missing resource '" + messageKey + "' in bundle '" + bundleName + "' for locale '" + locale.toString() + "'", e, bundleName, locale, messageKey);
         return null;
      }
   }

   public static String getString(ResourceBundle resourceBundle, String bundleName, Locale locale, String messageKey) {
      return getString((ResourceBundle)resourceBundle, bundleName, locale, messageKey, (Object[])null);
   }

   public static String getString(Class clazz, String bundleName, Locale locale, String messageKey, Object[] args) {
      String message = getMessage(clazz, bundleName, locale, messageKey);
      if (message == null) {
         return messageKey;
      } else {
         String formatMessage;
         if (args != null) {
            MessageFormat fm = new MessageFormat(message, Locale.ROOT);
            fm.setLocale(locale);
            formatMessage = fm.format(args);
         } else {
            formatMessage = message;
         }

         return formatMessage;
      }
   }

   public static String getString(ResourceBundle resourceBundle, String bundleName, Locale locale, String messageKey, Object[] args) {
      String message = getMessage(resourceBundle, bundleName, locale, messageKey);
      if (message == null) {
         return messageKey;
      } else {
         String formatMessage;
         if (args != null) {
            if (locale == null) {
               locale = Locale.ROOT;
            }

            MessageFormat fm = new MessageFormat(message, locale);
            formatMessage = fm.format(args);
         } else {
            formatMessage = message;
         }

         return formatMessage;
      }
   }

   protected static void complainMissingBundle(String errorMessage, Throwable exception, String bundleName, Locale locale) {
      String localeName = locale.toString();
      BundleKey bk = new BundleKey(bundleName, localeName);
      synchronized(bundleSet) {
         if (bundleSet.contains(bk)) {
            return;
         }

         bundleSet.add(bk);
      }

      logError(errorMessage, exception);
   }

   protected static void complainMissingMessage(String errorMessage, Throwable exception, String bundleName, Locale locale, String messageKey) {
      String localeName = locale.toString();
      MessageKey bk = new MessageKey(bundleName, localeName, messageKey);
      synchronized(messageSet) {
         if (messageSet.contains(bk)) {
            return;
         }

         messageSet.add(bk);
      }

      logError(errorMessage, exception);
   }

   protected static void complainMissingResource(String errorMessage, Throwable exception, String pathName, Locale locale, String resourceKey) {
      String localeName = locale.toString();
      ResourceKey bk = new ResourceKey(pathName, localeName, resourceKey);
      synchronized(resourceSet) {
         if (resourceSet.contains(bk)) {
            return;
         }

         resourceSet.add(bk);
      }

      logError(errorMessage, exception);
   }

   protected static void logError(String errorMessage, Throwable exception) {
      if (Logging.misc == null) {
         System.err.println(errorMessage);
         exception.printStackTrace(System.err);
      } else {
         Logging.misc.error(errorMessage, exception);
      }

   }

   protected static class BundleKey {
      protected String bundleName;
      protected String localeName;

      public BundleKey(String bundleName, String localeName) {
         this.bundleName = bundleName;
         this.localeName = localeName;
      }

      public int hashCode() {
         return this.bundleName.hashCode() + this.localeName.hashCode();
      }

      public boolean equals(Object o) {
         if (!(o instanceof BundleKey)) {
            return false;
         } else {
            BundleKey b = (BundleKey)o;
            return b.bundleName.equals(this.bundleName) && b.localeName.equals(this.localeName);
         }
      }
   }

   protected static class MessageKey {
      protected String bundleName;
      protected String localeName;
      protected String messageKey;

      public MessageKey(String bundleName, String localeName, String messageKey) {
         this.bundleName = bundleName;
         this.localeName = localeName;
         this.messageKey = messageKey;
      }

      public int hashCode() {
         return this.bundleName.hashCode() + this.localeName.hashCode() + this.messageKey.hashCode();
      }

      public boolean equals(Object o) {
         if (!(o instanceof MessageKey)) {
            return false;
         } else {
            MessageKey b = (MessageKey)o;
            return b.bundleName.equals(this.bundleName) && b.localeName.equals(this.localeName) && b.messageKey.equals(this.messageKey);
         }
      }
   }

   protected static class ResourceKey {
      protected String pathName;
      protected String localeName;
      protected String resourceKey;

      public ResourceKey(String pathName, String localeName, String resourceKey) {
         this.pathName = pathName;
         this.localeName = localeName;
         this.resourceKey = resourceKey;
      }

      public int hashCode() {
         return this.pathName.hashCode() + this.localeName.hashCode() + this.resourceKey.hashCode();
      }

      public boolean equals(Object o) {
         if (!(o instanceof ResourceKey)) {
            return false;
         } else {
            ResourceKey b = (ResourceKey)o;
            return b.pathName.equals(this.pathName) && b.localeName.equals(this.localeName) && b.resourceKey.equals(this.resourceKey);
         }
      }
   }
}
