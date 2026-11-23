package org.apache.openejb.core.security;

import java.security.AccessController;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.ProtectionDomain;
import javax.security.jacc.PolicyConfiguration;
import javax.security.jacc.PolicyConfigurationFactory;
import javax.security.jacc.PolicyContextException;

public abstract class JaccProvider {
   private static final String FACTORY_NAME = JaccProvider.class.getName();
   private static JaccProvider jaccProvider;

   public static JaccProvider get() {
      return jaccProvider;
   }

   public static void set(JaccProvider provider) {
      jaccProvider = provider;
   }

   public static void install() throws ClassNotFoundException, PolicyContextException {
      if (jaccProvider == null) {
         final String[] factoryClassName = new String[]{null};

         try {
            jaccProvider = (JaccProvider)AccessController.doPrivileged(new PrivilegedExceptionAction() {
               public Object run() throws Exception {
                  factoryClassName[0] = System.getProperty(JaccProvider.FACTORY_NAME);
                  if (factoryClassName[0] == null) {
                     throw new ClassNotFoundException("Property " + JaccProvider.FACTORY_NAME + " not set");
                  } else {
                     Thread currentThread = Thread.currentThread();
                     ClassLoader tccl = currentThread.getContextClassLoader();
                     return Class.forName(factoryClassName[0], true, tccl).newInstance();
                  }
               }
            });
         } catch (PrivilegedActionException pae) {
            if (pae.getException() instanceof ClassNotFoundException) {
               throw (ClassNotFoundException)pae.getException();
            } else if (pae.getException() instanceof InstantiationException) {
               throw new ClassNotFoundException(factoryClassName[0] + " could not be instantiated");
            } else if (pae.getException() instanceof IllegalAccessException) {
               throw new ClassNotFoundException("Illegal access to " + factoryClassName);
            } else {
               throw new PolicyContextException(pae.getException());
            }
         }
      }
   }

   public abstract PolicyConfiguration getPolicyConfiguration(String var1, boolean var2) throws PolicyContextException;

   public abstract boolean inService(String var1) throws PolicyContextException;

   public abstract PermissionCollection getPermissions(CodeSource var1);

   public abstract void refresh();

   public abstract boolean implies(ProtectionDomain var1, Permission var2);

   public static class Factory extends PolicyConfigurationFactory {
      public Factory() throws PolicyContextException, ClassNotFoundException {
         JaccProvider.install();
      }

      public PolicyConfiguration getPolicyConfiguration(String contextID, boolean remove) throws PolicyContextException {
         return JaccProvider.get().getPolicyConfiguration(contextID, remove);
      }

      public boolean inService(String contextID) throws PolicyContextException {
         return JaccProvider.get().inService(contextID);
      }
   }

   public static class Policy extends java.security.Policy {
      public Policy() throws PolicyContextException, ClassNotFoundException {
         JaccProvider.install();
      }

      public PermissionCollection getPermissions(CodeSource codesource) {
         return JaccProvider.get().getPermissions(codesource);
      }

      public void refresh() {
         JaccProvider.get().refresh();
      }

      public boolean implies(ProtectionDomain domain, Permission permission) {
         return JaccProvider.get().implies(domain, permission);
      }
   }
}
