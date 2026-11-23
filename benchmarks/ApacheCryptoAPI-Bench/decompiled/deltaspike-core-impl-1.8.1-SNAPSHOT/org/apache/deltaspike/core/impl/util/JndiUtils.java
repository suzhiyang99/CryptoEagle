package org.apache.deltaspike.core.impl.util;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.inject.Typed;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import org.apache.deltaspike.core.util.ClassUtils;
import org.apache.deltaspike.core.util.ExceptionUtils;

@Typed
public abstract class JndiUtils {
   private static final Logger LOG = Logger.getLogger(JndiUtils.class.getName());
   private static InitialContext initialContext = null;

   private JndiUtils() {
   }

   public static <T> T lookup(Name name, Class<? extends T> targetType) {
      try {
         return (T)verifyLookupResult(initialContext.lookup(name), name.toString(), targetType);
      } catch (NamingException e) {
         throw ExceptionUtils.throwAsRuntimeException(e);
      }
   }

   public static <T> T lookup(String name, Class<? extends T> targetType) {
      try {
         return (T)verifyLookupResult(initialContext.lookup(name), name, targetType);
      } catch (NamingException e) {
         throw ExceptionUtils.throwAsRuntimeException(e);
      }
   }

   private static <T> T verifyLookupResult(Object result, String name, Class<? extends T> targetType) {
      if (result != null) {
         if (targetType.isAssignableFrom(result.getClass())) {
            return (T)result;
         }

         if (result instanceof String) {
            try {
               Class<?> classOfResult = ClassUtils.loadClassForName((String)result);
               if (targetType.isAssignableFrom(classOfResult)) {
                  try {
                     return (T)classOfResult.newInstance();
                  } catch (Exception e) {
                     LOG.log(Level.SEVERE, "Class " + classOfResult + " from JNDI lookup for name " + name + " could not be instantiated", e);
                  }
               } else {
                  LOG.log(Level.SEVERE, "JNDI lookup for key " + name + " returned class " + classOfResult.getName() + " which does not implement/extend the expected class" + targetType.getName());
               }
            } catch (ClassNotFoundException cnfe) {
               LOG.log(Level.SEVERE, "Could not find Class " + result + " from JNDI lookup for name " + name, cnfe);
            }
         } else {
            LOG.log(Level.SEVERE, "JNDI lookup for key " + name + " should return a value of " + targetType + ", but returned " + result);
         }
      }

      return null;
   }

   public static <T> Map<String, T> list(String name, Class<T> type) {
      Map<String, T> result = new HashMap();

      try {
         NameParser nameParser = initialContext.getNameParser(name);
         NamingEnumeration<NameClassPair> enumeration = initialContext.list(name);

         while(enumeration.hasMoreElements()) {
            try {
               NameClassPair binding = (NameClassPair)enumeration.nextElement();
               Name bindingName = nameParser.parse(name).add(binding.getName());
               result.put(binding.getName(), lookup(bindingName, type));
            } catch (NamingException e) {
               if (LOG.isLoggable(Level.FINEST)) {
                  LOG.log(Level.FINEST, "InitialContext#list failed!", e);
               }
            }
         }
      } catch (NamingException e) {
         LOG.log(Level.WARNING, "Problem reading the name of the JNDI location " + name + " or failuring listing pairs.", e);
      }

      return result;
   }

   static {
      try {
         initialContext = new InitialContext();
      } catch (Exception e) {
         throw new ExceptionInInitializerError(e);
      }
   }
}
