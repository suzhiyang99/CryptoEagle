package org.apache.openejb.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ServiceManagerProxy {
   static Logger logger;
   private final Object serviceManager;
   private final Class serviceManagerClass;

   public ServiceManagerProxy() throws AlreadyStartedException {
      this(true);
   }

   public ServiceManagerProxy(boolean checkAlreadyStarted) throws AlreadyStartedException {
      ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

      try {
         this.serviceManagerClass = classLoader.loadClass("org.apache.openejb.server.ServiceManager");
      } catch (ClassNotFoundException e) {
         String msg = "Enabling option 'openejb.embedded.remotable' requires class 'org.apache.openejb.server.ServiceManager' to be available.  Make sure you have the openejb-server-*.jar in your classpath and at least one protocol implementation such as openejb-ejbd-*.jar.";
         throw new IllegalStateException("Enabling option 'openejb.embedded.remotable' requires class 'org.apache.openejb.server.ServiceManager' to be available.  Make sure you have the openejb-server-*.jar in your classpath and at least one protocol implementation such as openejb-ejbd-*.jar.", e);
      }

      Method get = this.getMethod("get");
      Method getManager = this.getMethod("getManager");
      if (checkAlreadyStarted && this.invoke(get, (Object)null) != null) {
         throw new AlreadyStartedException("Server services already started");
      } else {
         this.serviceManager = this.invoke(getManager, (Object)null);
         logger.info("Initializing network services");
         Method init = this.getMethod("init");
         this.invoke(init, this.serviceManager);
      }
   }

   private Method getMethod(String name, Class... parameterTypes) {
      try {
         return this.serviceManagerClass.getMethod(name, parameterTypes);
      } catch (NoSuchMethodException e) {
         throw new IllegalStateException("Cannot load the ServiceManager", e);
      }
   }

   private Object invoke(Method method, Object obj, Object... args) {
      try {
         return method.invoke(obj, args);
      } catch (IllegalAccessException e) {
         throw new IllegalStateException(e);
      } catch (InvocationTargetException e) {
         throw new IllegalStateException("Failed executing ServiceManager." + method.getName(), e.getCause());
      }
   }

   public void start() {
      logger.info("Initializing network services");
      Method start = this.getMethod("start", Boolean.TYPE);
      this.invoke(start, this.serviceManager, false);
   }

   public void stop() {
      logger.info("Stopping network services");
      Method stop = this.getMethod("stop");
      this.invoke(stop, this.serviceManager);
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB_STARTUP, ServiceManagerProxy.class);
   }

   public class AlreadyStartedException extends Exception {
      public AlreadyStartedException(String s) {
         super(s);
      }
   }
}
