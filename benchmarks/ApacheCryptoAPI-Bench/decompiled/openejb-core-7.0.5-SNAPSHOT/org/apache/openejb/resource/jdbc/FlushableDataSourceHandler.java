package org.apache.openejb.resource.jdbc;

import java.io.Flushable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.sql.CommonDataSource;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;

public class FlushableDataSourceHandler implements DelegatableHandler {
   private static final Logger LOGGER;
   public static final String[] FACTORY_ARGS;
   private final FlushConfig config;
   private final ReadWriteLock lock = new ReentrantReadWriteLock();
   private AtomicReference<CommonDataSource> delegate = new AtomicReference();
   private final ResettableDataSourceHandler resettableHandler;

   public FlushableDataSourceHandler(CommonDataSource original, FlushConfig config, ResettableDataSourceHandler resettableHandler) {
      this.config = config;
      this.delegate.set(original);
      this.resettableHandler = resettableHandler;
   }

   private void createANewDelegate() {
      CommonDataSource old = (CommonDataSource)this.delegate.get();

      try {
         ObjectRecipe recipe = new ObjectRecipe(DataSourceFactory.class.getName(), "create", FACTORY_ARGS);
         recipe.allow(Option.CASE_INSENSITIVE_PROPERTIES);
         recipe.allow(Option.IGNORE_MISSING_PROPERTIES);
         recipe.allow(Option.NAMED_PARAMETERS);
         recipe.allow(Option.PRIVATE_PROPERTIES);
         recipe.setAllProperties(this.config.properties);
         recipe.setProperty("resettableHandler", this.resettableHandler);
         recipe.setProperty("flushableHandler", this);
         this.updateDataSource((CommonDataSource)CommonDataSource.class.cast(recipe.create()));
      } catch (Exception e) {
         LOGGER.error("Can't recreate the datasource, keeping old one", (Throwable)e);
         return;
      }

      if (DataSourceFactory.knows(old)) {
         try {
            DataSourceFactory.destroy(old);
         } catch (Throwable var3) {
         }
      }

   }

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
      CommonDataSource actualDelegate = (CommonDataSource)this.delegate.get();
      if (Object.class == method.getDeclaringClass()) {
         if ("hashCode".equals(method.getName())) {
            return this.hashCode();
         }

         if ("toString".equals(method.getName())) {
            return "Flushable[" + actualDelegate.toString() + "]";
         }
      }

      if (Flushable.class == method.getDeclaringClass()) {
         Lock l = this.lock.writeLock();
         l.lock();

         try {
            this.createANewDelegate();
            if (Flushable.class.isInstance(actualDelegate) && (!Proxy.isProxyClass(actualDelegate.getClass()) || !FlushableDataSourceHandler.class.isInstance(Proxy.getInvocationHandler(actualDelegate)) && !ResettableDataSourceHandler.class.isInstance(Proxy.getInvocationHandler(actualDelegate)))) {
               ((Flushable)Flushable.class.cast(actualDelegate)).flush();
            }
         } finally {
            l.unlock();
         }

         return null;
      } else {
         Lock l = this.lock.readLock();
         l.lock();

         Object var6;
         try {
            var6 = method.invoke(this.getDelegate(), args);
         } catch (InvocationTargetException ite) {
            throw ite.getCause();
         } finally {
            l.unlock();
         }

         return var6;
      }
   }

   public CommonDataSource getDelegate() {
      return (CommonDataSource)this.delegate.get();
   }

   public void updateDataSource(CommonDataSource ds) {
      CommonDataSource current;
      InvocationHandler handler;
      for(current = ds; Proxy.isProxyClass(current.getClass()); current = ((DelegatableHandler)DelegatableHandler.class.cast(handler)).getDelegate()) {
         handler = Proxy.getInvocationHandler(current);
         if (!FlushableDataSourceHandler.class.isInstance(handler) && !ResettableDataSourceHandler.class.isInstance(handler)) {
            break;
         }
      }

      this.delegate.set(current);
   }

   static {
      LOGGER = Logger.getInstance(LogCategory.OPENEJB, FlushableDataSourceHandler.class);
      FACTORY_ARGS = new String[]{"ServiceId", "JtaManaged", "JdbcDriver", "Definition", "MaxWaitTime", "TimeBetweenEvictionRuns", "MinEvictableIdleTime", "OpenEJBResourceClasspath"};
   }

   public static class FlushConfig {
      public final Map<String, Object> properties;

      public FlushConfig(Map<String, Object> properties) {
         this.properties = properties;
      }
   }
}
