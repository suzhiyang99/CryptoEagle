package org.apache.openejb.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Hashtable;
import java.util.Properties;
import java.util.concurrent.locks.ReentrantLock;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import org.apache.openejb.loader.OpenEJBInstance;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.util.OptionsLog;

/** @deprecated */
@Deprecated
public class LocalInitialContextFactory implements InitialContextFactory {
   private static final ReentrantLock lock = new ReentrantLock();
   private static OpenEJBInstance openejb;
   private boolean bootedOpenEJB;

   public Context getInitialContext(Hashtable env) throws NamingException {
      this.init(env);
      return this.getLocalInitialContext(env);
   }

   protected void init(Hashtable env) throws NamingException {
      ReentrantLock l = lock;
      l.lock();

      try {
         if (openejb == null || !openejb.isInitialized()) {
            try {
               Properties properties = new Properties();
               properties.putAll(env);
               this.init(properties);
               return;
            } catch (Exception e) {
               throw (NamingException)(new NamingException("Attempted to load OpenEJB. " + e.getMessage())).initCause(e);
            }
         }
      } finally {
         l.unlock();
      }

   }

   boolean bootedOpenEJB() {
      ReentrantLock l = lock;
      l.lock();

      boolean var2;
      try {
         var2 = this.bootedOpenEJB;
      } finally {
         l.unlock();
      }

      return var2;
   }

   private void init(Properties properties) throws Exception {
      if (openejb == null || !openejb.isInitialized()) {
         openejb = new OpenEJBInstance();
         if (!openejb.isInitialized()) {
            this.bootedOpenEJB = true;
            SystemInstance.init(properties);
            OptionsLog.install();
            SystemInstance.get().setProperty("openejb.embedded", "true");
            openejb.init(properties);
         }
      }
   }

   public void close() {
      ReentrantLock l = lock;
      l.lock();

      try {
         openejb = null;
      } finally {
         l.unlock();
      }

   }

   private Context getLocalInitialContext(Hashtable env) throws NamingException {
      try {
         ClassLoader cl = SystemInstance.get().getClassLoader();
         Class localInitialContext = Class.forName("org.apache.openejb.core.LocalInitialContext", true, cl);
         Constructor constructor = localInitialContext.getConstructor(Hashtable.class, LocalInitialContextFactory.class);
         Context context = (Context)constructor.newInstance(env, this);
         return context;
      } catch (Throwable var6) {
         Throwable e = var6;
         if (var6 instanceof InvocationTargetException) {
            InvocationTargetException ite = (InvocationTargetException)var6;
            if (ite.getTargetException() != null) {
               e = ite.getTargetException();
            }
         }

         if (e instanceof NamingException) {
            throw (NamingException)e;
         } else {
            throw (NamingException)(new NamingException("Cannot instantiate a LocalInitialContext. Exception: " + e.getClass().getName() + " " + e.getMessage())).initCause(e);
         }
      }
   }
}
