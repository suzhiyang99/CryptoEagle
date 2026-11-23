package org.apache.openejb.log.commonslogging;

import java.io.Serializable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.Jdk14Logger;
import org.apache.openejb.util.classloader.URLClassLoaderFirst;
import org.apache.openejb.util.reflection.Reflections;

public class Log4J4AppOpenEJB4ContainerLog implements Log, Serializable {
   private static final Class<?>[] NO_PARAM = new Class[0];
   private static final Class<?>[] OBJECT_PARAM = new Class[]{Object.class};
   private static final Class<?>[] OBJECT_THROWABLE_PARAM = new Class[]{Object.class, Throwable.class};
   private static final Object[] NO_ARGS = new Object[0];
   private Object delegate;

   public Log4J4AppOpenEJB4ContainerLog(String category) {
      if (URLClassLoaderFirst.shouldSkip(category)) {
         this.delegate = new OpenEJBCommonsLog(category);
      } else {
         ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

         try {
            this.delegate = contextClassLoader.loadClass("org.apache.commons.logging.impl.Log4JLogger").getConstructor(String.class).newInstance(category);
         } catch (Exception var4) {
            this.delegate = new Jdk14Logger(category);
         }
      }

   }

   public boolean isDebugEnabled() {
      return (Boolean)Reflections.invokeByReflection(this.delegate, "isDebugEnabled", NO_PARAM, NO_ARGS);
   }

   public boolean isErrorEnabled() {
      return (Boolean)Reflections.invokeByReflection(this.delegate, "isErrorEnabled", NO_PARAM, NO_ARGS);
   }

   public boolean isFatalEnabled() {
      return (Boolean)Reflections.invokeByReflection(this.delegate, "isFatalEnabled", NO_PARAM, NO_ARGS);
   }

   public boolean isInfoEnabled() {
      return (Boolean)Reflections.invokeByReflection(this.delegate, "isInfoEnabled", NO_PARAM, NO_ARGS);
   }

   public boolean isTraceEnabled() {
      return (Boolean)Reflections.invokeByReflection(this.delegate, "isTraceEnabled", NO_PARAM, NO_ARGS);
   }

   public boolean isWarnEnabled() {
      return (Boolean)Reflections.invokeByReflection(this.delegate, "isWarnEnabled", NO_PARAM, NO_ARGS);
   }

   public void trace(Object message) {
      Reflections.invokeByReflection(this.delegate, "trace", OBJECT_PARAM, new Object[]{message});
   }

   public void trace(Object message, Throwable t) {
      Reflections.invokeByReflection(this.delegate, "trace", OBJECT_THROWABLE_PARAM, new Object[]{message, t});
   }

   public void debug(Object message) {
      Reflections.invokeByReflection(this.delegate, "debug", OBJECT_PARAM, new Object[]{message});
   }

   public void debug(Object message, Throwable t) {
      Reflections.invokeByReflection(this.delegate, "debug", OBJECT_THROWABLE_PARAM, new Object[]{message, t});
   }

   public void info(Object message) {
      Reflections.invokeByReflection(this.delegate, "info", OBJECT_PARAM, new Object[]{message});
   }

   public void info(Object message, Throwable t) {
      Reflections.invokeByReflection(this.delegate, "info", OBJECT_THROWABLE_PARAM, new Object[]{message, t});
   }

   public void warn(Object message) {
      Reflections.invokeByReflection(this.delegate, "warn", OBJECT_PARAM, new Object[]{message});
   }

   public void warn(Object message, Throwable t) {
      Reflections.invokeByReflection(this.delegate, "warn", OBJECT_THROWABLE_PARAM, new Object[]{message, t});
   }

   public void error(Object message) {
      Reflections.invokeByReflection(this.delegate, "error", OBJECT_PARAM, new Object[]{message});
   }

   public void error(Object message, Throwable t) {
      Reflections.invokeByReflection(this.delegate, "error", OBJECT_THROWABLE_PARAM, new Object[]{message, t});
   }

   public void fatal(Object message) {
      Reflections.invokeByReflection(this.delegate, "fatal", OBJECT_PARAM, new Object[]{message});
   }

   public void fatal(Object message, Throwable t) {
      Reflections.invokeByReflection(this.delegate, "fatal", OBJECT_THROWABLE_PARAM, new Object[]{message, t});
   }
}
