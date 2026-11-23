package org.spark_project.jetty.servlet.listener;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.spark_project.jetty.util.Loader;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class ELContextCleaner implements ServletContextListener {
   private static final Logger LOG = Log.getLogger(ELContextCleaner.class);

   public void contextInitialized(ServletContextEvent sce) {
   }

   public void contextDestroyed(ServletContextEvent sce) {
      try {
         Class<?> beanELResolver = Loader.loadClass(this.getClass(), "javax.el.BeanELResolver");
         Field field = this.getField(beanELResolver);
         this.purgeEntries(field);
         if (LOG.isDebugEnabled()) {
            LOG.debug("javax.el.BeanELResolver purged");
         }
      } catch (ClassNotFoundException var4) {
      } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
         LOG.warn("Cannot purge classes from javax.el.BeanELResolver", (Throwable)e);
      } catch (NoSuchFieldException var6) {
         LOG.debug("Not cleaning cached beans: no such field javax.el.BeanELResolver.properties");
      }

   }

   protected Field getField(Class<?> beanELResolver) throws SecurityException, NoSuchFieldException {
      return beanELResolver == null ? null : beanELResolver.getDeclaredField("properties");
   }

   protected void purgeEntries(Field properties) throws IllegalArgumentException, IllegalAccessException {
      if (properties != null) {
         if (!properties.isAccessible()) {
            properties.setAccessible(true);
         }

         Map map = (Map)properties.get((Object)null);
         if (map != null) {
            Iterator<Class<?>> itor = map.keySet().iterator();

            while(itor.hasNext()) {
               Class<?> clazz = (Class)itor.next();
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Clazz: " + clazz + " loaded by " + clazz.getClassLoader());
               }

               if (Thread.currentThread().getContextClassLoader().equals(clazz.getClassLoader())) {
                  itor.remove();
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("removed");
                  }
               } else if (LOG.isDebugEnabled()) {
                  LOG.debug("not removed: contextclassloader=" + Thread.currentThread().getContextClassLoader() + "clazz's classloader=" + clazz.getClassLoader());
               }
            }

         }
      }
   }
}
