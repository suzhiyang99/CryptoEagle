package org.apache.openejb.util;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;
import javax.security.jacc.PolicyContext;

public final class JavaSecurityManagers {
   private static final PrivilegedAction<Properties> GET_SYSTEM_PROPERTIES = new PrivilegedAction<Properties>() {
      public Properties run() {
         return System.getProperties();
      }
   };

   private JavaSecurityManagers() {
   }

   public static String getSystemProperty(final String key) {
      return System.getSecurityManager() == null ? System.getProperty(key) : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(key);
         }
      });
   }

   public static String getSystemProperty(final String key, final String or) {
      return System.getSecurityManager() == null ? System.getProperty(key, or) : (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return System.getProperty(key, or);
         }
      });
   }

   public static Properties getSystemProperties() {
      return System.getSecurityManager() == null ? System.getProperties() : (Properties)AccessController.doPrivileged(GET_SYSTEM_PROPERTIES);
   }

   public static void removeSystemProperty(final String key) {
      if (System.getSecurityManager() == null) {
         System.clearProperty(key);
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               System.clearProperty(key);
               return null;
            }
         });
      }

   }

   public static void setSystemProperty(final String key, final Object value) {
      if (System.getSecurityManager() == null) {
         if (String.class.isInstance(value)) {
            System.setProperty(key, (String)String.class.cast(value));
         } else {
            System.getProperties().put(key, value);
         }
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               if (String.class.isInstance(value)) {
                  System.setProperty(key, (String)String.class.cast(value));
               } else {
                  System.getProperties().put(key, value);
               }

               return null;
            }
         });
      }

   }

   public static void setContextID(final String moduleID) {
      if (System.getSecurityManager() == null) {
         PolicyContext.setContextID(moduleID);
      } else {
         AccessController.doPrivileged(new PrivilegedAction<String>() {
            public String run() {
               PolicyContext.setContextID(moduleID);
               return null;
            }
         });
      }

   }
}
