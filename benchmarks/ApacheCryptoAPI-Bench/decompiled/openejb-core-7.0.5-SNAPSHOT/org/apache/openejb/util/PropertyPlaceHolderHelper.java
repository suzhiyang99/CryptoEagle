package org.apache.openejb.util;

import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.text.StrLookup;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.openejb.cipher.PasswordCipher;
import org.apache.openejb.cipher.PasswordCipherException;
import org.apache.openejb.cipher.PasswordCipherFactory;
import org.apache.openejb.cipher.SafePasswordCipher;
import org.apache.openejb.loader.SystemInstance;

public final class PropertyPlaceHolderHelper {
   private static final String PREFIX = "${";
   private static final String SUFFIX = "}";
   private static final Properties CACHE = new Properties();
   private static final PropertiesLookup RESOLVER = new PropertiesLookup();
   public static final StrSubstitutor SUBSTITUTOR;
   public static final String CIPHER_PREFIX = "cipher:";

   private PropertyPlaceHolderHelper() {
   }

   public static void reset() {
      CACHE.clear();
      RESOLVER.reload();
   }

   public static String simpleValue(String raw) {
      if (raw == null) {
         return null;
      } else if (raw.contains("${") && raw.contains("}")) {
         String value = SUBSTITUTOR.replace(raw);
         if (!value.equals(raw) && value.startsWith("java:")) {
            value = value.substring(5);
         }

         return (String)String.class.cast(decryptIfNeeded(value.replace("${", "").replace("}", ""), false));
      } else {
         return (String)String.class.cast(decryptIfNeeded(raw.replace("${", "").replace("}", ""), false));
      }
   }

   public static Object simpleValueAsStringOrCharArray(String raw) {
      if (raw == null) {
         return null;
      } else if (raw.contains("${") && raw.contains("}")) {
         String value = SUBSTITUTOR.replace(raw);
         if (!value.equals(raw) && value.startsWith("java:")) {
            value = value.substring(5);
         }

         return decryptIfNeeded(value.replace("${", "").replace("}", ""), true);
      } else {
         return decryptIfNeeded(raw.replace("${", "").replace("}", ""), true);
      }
   }

   private static Object decryptIfNeeded(String replace, boolean acceptCharArray) {
      if (replace.startsWith("cipher:")) {
         String algo = replace.substring("cipher:".length(), replace.indexOf(58, "cipher:".length() + 1));

         PasswordCipher cipher;
         try {
            cipher = PasswordCipherFactory.getPasswordCipher(algo);
         } catch (PasswordCipherException var7) {
            try {
               cipher = (PasswordCipher)PasswordCipher.class.cast(Thread.currentThread().getContextClassLoader().loadClass(algo).newInstance());
            } catch (Exception e) {
               throw new IllegalArgumentException(e);
            }
         }

         char[] input = replace.substring("cipher:".length() + algo.length() + 1).toCharArray();
         return acceptCharArray && SafePasswordCipher.class.isInstance(cipher) ? ((SafePasswordCipher)SafePasswordCipher.class.cast(cipher)).decryptAsCharArray(input) : cipher.decrypt(input);
      } else {
         return replace;
      }
   }

   public static String value(String aw) {
      if (aw == null) {
         return null;
      } else if (aw.contains("${") && aw.contains("}")) {
         String value = CACHE.getProperty(aw);
         if (value != null) {
            return value;
         } else {
            value = simpleValue(aw);
            CACHE.setProperty(aw, value);
            return value;
         }
      } else {
         return (String)String.class.cast(decryptIfNeeded(aw, false));
      }
   }

   public static Properties simpleHolds(Properties properties) {
      return holds(properties, false);
   }

   public static Properties holds(Properties properties) {
      return holds(properties, true);
   }

   private static Properties holds(Properties properties, boolean cache) {
      Properties updated = new SuperProperties();
      if (properties == null) {
         return updated;
      } else {
         for(Map.Entry<Object, Object> entry : properties.entrySet()) {
            Object rawValue = entry.getValue();
            if (rawValue instanceof String) {
               String value = (String)rawValue;
               updated.put(entry.getKey(), cache ? value(value) : simpleValueAsStringOrCharArray(value));
            } else {
               updated.put(entry.getKey(), rawValue);
            }
         }

         return updated;
      }
   }

   public static void holdsWithUpdate(Properties props) {
      Properties toUpdate = holds(props);
      props.putAll(toUpdate);
   }

   static {
      SUBSTITUTOR = new StrSubstitutor(RESOLVER);
      SUBSTITUTOR.setEnableSubstitutionInVariables(true);
      SUBSTITUTOR.setValueDelimiter(JavaSecurityManagers.getSystemProperty("openejb.placehodler.delimiter", ":-"));
   }

   private static class PropertiesLookup extends StrLookup<Object> {
      private static final Map<String, String> ENV = System.getenv();

      private PropertiesLookup() {
      }

      public synchronized String lookup(String key) {
         String value = SystemInstance.get().getProperties().getProperty(key);
         if (value != null) {
            return value;
         } else {
            value = (String)ENV.get(key);
            return value != null ? value : null;
         }
      }

      public synchronized void reload() {
      }
   }
}
