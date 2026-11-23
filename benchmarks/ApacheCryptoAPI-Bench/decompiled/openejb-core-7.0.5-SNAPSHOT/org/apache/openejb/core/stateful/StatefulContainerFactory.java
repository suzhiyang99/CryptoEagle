package org.apache.openejb.core.stateful;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.SuperProperties;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;

public class StatefulContainerFactory {
   private Object id;
   private SecurityService securityService;
   private Cache<Object, Instance> cache;
   private final Properties properties = (new SuperProperties()).caseInsensitive(false);
   private Duration accessTimeout;

   public StatefulContainerFactory() {
      this.accessTimeout = new Duration(0L, TimeUnit.MILLISECONDS);
   }

   public Object getId() {
      return this.id;
   }

   public void setId(Object id) {
      this.id = id;
   }

   public SecurityService getSecurityService() {
      return this.securityService;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   public Duration getAccessTimeout() {
      return this.accessTimeout;
   }

   public void setAccessTimeout(Duration accessTimeout) {
      this.accessTimeout = accessTimeout;
   }

   public Cache<Object, Instance> getCache() {
      return this.cache;
   }

   public void setCache(Cache<Object, Instance> cache) {
      this.cache = cache;
   }

   public void setCache(String s) {
      this.properties.put("Cache", s);
   }

   public void setPassivator(String s) {
      this.properties.put("Passivator", s);
   }

   public void setTimeOut(String s) {
      this.properties.put("TimeOut", s);
   }

   public void setCapacity(String s) {
      this.properties.put("Capacity", s);
   }

   public void setBulkPassivate(String s) {
      this.properties.put("BulkPassivate", s);
   }

   public void setFrequency(String s) {
      this.properties.put("Frequency", s);
   }

   public void setPreventExtendedEntityManagerSerialization(boolean preventExtendedEntityManagerSerialization) {
      this.properties.put("PreventExtendedEntityManagerSerialization", Boolean.toString(preventExtendedEntityManagerSerialization));
   }

   public Properties getProperties() {
      return this.properties;
   }

   public void setProperties(Properties properties) {
      this.properties.putAll(properties);
   }

   public StatefulContainer create() throws Exception {
      if (this.cache == null) {
         this.buildCache();
      }

      this.cache.init();
      return new StatefulContainer(this.id, this.securityService, this.cache, this.accessTimeout, "true".equalsIgnoreCase(this.properties.getProperty("PreventExtendedEntityManagerSerialization", "false").trim()), this.createLockFactory());
   }

   private LockFactory createLockFactory() {
      Object lockFactory = this.properties.remove("LockFactory");
      if (lockFactory != null) {
         try {
            return (LockFactory)LockFactory.class.cast(StatefulContainerFactory.class.getClassLoader().loadClass(lockFactory.toString().trim()).newInstance());
         } catch (Exception e) {
            throw new IllegalArgumentException(e);
         }
      } else {
         return new DefaultLockFactory();
      }
   }

   private void buildCache() throws Exception {
      if (this.properties == null) {
         throw new IllegalArgumentException("No cache defined for StatefulContainer " + this.id);
      } else {
         Object cache = this.getProperty("Cache");
         if (cache == null) {
            throw new IllegalArgumentException("No cache defined for StatefulContainer " + this.id);
         } else if (cache instanceof Cache) {
            this.cache = (Cache)cache;
         } else {
            ObjectRecipe serviceRecipe = new ObjectRecipe((String)cache);
            serviceRecipe.allow(Option.CASE_INSENSITIVE_PROPERTIES);
            serviceRecipe.allow(Option.IGNORE_MISSING_PROPERTIES);
            serviceRecipe.allow(Option.NAMED_PARAMETERS);
            serviceRecipe.setAllProperties(this.properties);
            ClassLoader classLoader = StatefulContainerFactory.class.getClassLoader();
            if (!((String)cache).startsWith("org.apache.tomee")) {
               classLoader = Thread.currentThread().getContextClassLoader();
            }

            cache = serviceRecipe.create(classLoader);
            this.cache = (Cache)cache;
         }
      }
   }

   private Object getProperty(String name) {
      for(Map.Entry<Object, Object> entry : this.properties.entrySet()) {
         Object key = entry.getKey();
         if (key instanceof String && name.equalsIgnoreCase((String)key)) {
            return entry.getValue();
         }
      }

      return null;
   }

   public void setLockFactory(String lockFactory) {
      this.properties.setProperty("LockFactory", lockFactory);
   }
}
