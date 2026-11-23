package org.apache.manifoldcf.core.interfaces;

import org.apache.manifoldcf.core.system.ManifoldCF;

public class LockManagerFactory {
   public static final String _rcsid = "@(#)$Id: LockManagerFactory.java 988245 2010-08-23 18:39:35Z kwright $";
   private static final String lockManager = "_LockManager_";

   private LockManagerFactory() {
   }

   public static ILockManager make(IThreadContext context) throws ManifoldCFException {
      Object x = context.get("_LockManager_");
      if (x == null || !(x instanceof ILockManager)) {
         String implementationClass = ManifoldCF.getStringProperty("org.apache.manifoldcf.lockmanagerclass", "org.apache.manifoldcf.core.lockmanager.LockManager");

         try {
            Class c = Class.forName(implementationClass);
            x = c.newInstance();
            if (!(x instanceof ILockManager)) {
               throw new ManifoldCFException("Lock manager class " + implementationClass + " does not implement ILockManager", 3);
            }

            context.save("_LockManager_", x);
         } catch (ClassNotFoundException e) {
            throw new ManifoldCFException("Lock manager class " + implementationClass + " could not be found: " + e.getMessage(), e, 3);
         } catch (ExceptionInInitializerError e) {
            throw new ManifoldCFException("Lock manager class " + implementationClass + " could not be instantiated: " + e.getMessage(), e, 3);
         } catch (LinkageError e) {
            throw new ManifoldCFException("Lock manager class " + implementationClass + " could not be linked: " + e.getMessage(), e, 3);
         } catch (InstantiationException e) {
            throw new ManifoldCFException("Lock manager class " + implementationClass + " could not be instantiated: " + e.getMessage(), e, 3);
         } catch (IllegalAccessException e) {
            throw new ManifoldCFException("Lock manager class " + implementationClass + " had no public default initializer: " + e.getMessage(), e, 3);
         }
      }

      return (ILockManager)x;
   }

   public static String getProperty(IThreadContext tc, String s) throws ManifoldCFException {
      return make(tc).getSharedConfiguration().getProperty(s);
   }

   public static String getStringProperty(IThreadContext tc, String s, String defaultValue) throws ManifoldCFException {
      return make(tc).getSharedConfiguration().getStringProperty(s, defaultValue);
   }

   public static String getPossiblyObfuscatedStringProperty(IThreadContext tc, String s, String defaultValue) throws ManifoldCFException {
      return make(tc).getSharedConfiguration().getPossiblyObfuscatedStringProperty(s, defaultValue);
   }

   public static int getIntProperty(IThreadContext tc, String s, int defaultValue) throws ManifoldCFException {
      return make(tc).getSharedConfiguration().getIntProperty(s, defaultValue);
   }

   public static long getLongProperty(IThreadContext tc, String s, long defaultValue) throws ManifoldCFException {
      return make(tc).getSharedConfiguration().getLongProperty(s, defaultValue);
   }

   public static double getDoubleProperty(IThreadContext tc, String s, double defaultValue) throws ManifoldCFException {
      return make(tc).getSharedConfiguration().getDoubleProperty(s, defaultValue);
   }

   public static boolean getBooleanProperty(IThreadContext tc, String s, boolean defaultValue) throws ManifoldCFException {
      return make(tc).getSharedConfiguration().getBooleanProperty(s, defaultValue);
   }
}
