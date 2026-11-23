package org.apache.openejb.cipher;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;
import org.apache.xbean.finder.ResourceFinder;

public class PasswordCipherFactory {
   public static PasswordCipher getPasswordCipher(String passwordCipherClass) {
      try {
         PasswordCipher cipher = doInternalPasswordCipher(PasswordCipher.class, passwordCipherClass);
         return cipher;
      } catch (PasswordCipherException initial) {
         try {
            return doInternalPasswordCipher(org.apache.openejb.resource.jdbc.cipher.PasswordCipher.class, passwordCipherClass);
         } catch (PasswordCipherException var4) {
            throw initial;
         }
      }
   }

   private static <T extends PasswordCipher> T doInternalPasswordCipher(Class<T> intf, String passwordCipherClass) {
      ResourceFinder finder = new ResourceFinder("META-INF/");

      Map<String, Class<? extends T>> impls;
      try {
         impls = finder.mapAllImplementations(intf);
      } catch (Throwable t) {
         String message = "Password cipher '" + passwordCipherClass + "' not found in META-INF/org.apache.openejb.cipher.PasswordCipher.";
         throw new PasswordCipherException(message, t);
      }

      Class<? extends T> pwdCipher = (Class)impls.get(passwordCipherClass);
      ClassLoader tccl = Thread.currentThread().getContextClassLoader();
      URL url = tccl.getResource("META-INF/" + intf.getName() + "/" + passwordCipherClass);
      if (url != null) {
         try {
            String clazz = (new BufferedReader(new InputStreamReader(url.openStream()))).readLine().trim();
            pwdCipher = tccl.loadClass(clazz).asSubclass(intf);
         } catch (Exception var12) {
         }
      }

      if (null == pwdCipher) {
         try {
            try {
               pwdCipher = Class.forName(passwordCipherClass).asSubclass(intf);
            } catch (ClassNotFoundException var10) {
               pwdCipher = tccl.loadClass(passwordCipherClass).asSubclass(intf);
            }
         } catch (Throwable t) {
            String message = "Cannot load password cipher class '" + passwordCipherClass + "'";
            throw new PasswordCipherException(message, t);
         }
      }

      try {
         return (T)(pwdCipher.newInstance());
      } catch (Throwable t) {
         String message = "Cannot create password cipher instance";
         throw new PasswordCipherException("Cannot create password cipher instance", t);
      }
   }
}
