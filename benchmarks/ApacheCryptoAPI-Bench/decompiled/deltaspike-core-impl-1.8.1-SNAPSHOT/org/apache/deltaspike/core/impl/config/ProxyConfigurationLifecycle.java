package org.apache.deltaspike.core.impl.config;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import org.apache.deltaspike.core.api.config.ConfigProperty;
import org.apache.deltaspike.core.api.config.ConfigResolver;
import org.apache.deltaspike.core.api.config.Configuration;
import org.apache.deltaspike.core.spi.config.BaseConfigPropertyProducer;
import org.apache.deltaspike.core.util.metadata.builder.ContextualLifecycle;

class ProxyConfigurationLifecycle implements ContextualLifecycle {
   private Class<?>[] api;

   ProxyConfigurationLifecycle(Class<?> proxyType) {
      this.api = new Class[]{proxyType};
   }

   public Object create(Bean bean, CreationalContext creationalContext) {
      Configuration configuration = (Configuration)this.api[0].getAnnotation(Configuration.class);
      long cacheFor = configuration.cacheFor();
      return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), this.api, new ConfigurationHandler(cacheFor <= 0L ? -1L : configuration.cacheUnit().toMillis(cacheFor), configuration.prefix()));
   }

   public void destroy(Bean bean, Object instance, CreationalContext creationalContext) {
   }

   private static final class ConfigurationHandler implements InvocationHandler {
      private final BaseConfigPropertyProducer delegate;
      private final ConcurrentMap<Method, ConfigResolver.TypedResolver<?>> resolvers;
      private final long cacheMs;
      private final String prefix;

      private ConfigurationHandler(long cacheMs, String prefix) {
         this.delegate = new BaseConfigPropertyProducer() {
         };
         this.resolvers = new ConcurrentHashMap();
         this.cacheMs = cacheMs;
         this.prefix = prefix;
      }

      public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
         if (Object.class == method.getDeclaringClass()) {
            try {
               return method.invoke(this, args);
            } catch (InvocationTargetException ite) {
               throw ite.getCause();
            }
         } else {
            ConfigResolver.TypedResolver<?> typedResolver = (ConfigResolver.TypedResolver)this.resolvers.get(method);
            if (typedResolver == null) {
               ConfigProperty annotation = (ConfigProperty)method.getAnnotation(ConfigProperty.class);
               if (annotation == null) {
                  throw new UnsupportedOperationException(method + " doesn't have @ConfigProperty and therefore is illegal");
               }

               Class<?> returnType = method.getReturnType();
               if (Integer.TYPE == returnType) {
                  returnType = Integer.class;
               } else if (Long.TYPE == returnType) {
                  returnType = Long.class;
               } else if (Boolean.TYPE == returnType) {
                  returnType = Boolean.class;
               } else if (Short.TYPE == returnType) {
                  returnType = Short.class;
               } else if (Byte.TYPE == returnType) {
                  returnType = Byte.class;
               } else if (Float.TYPE == returnType) {
                  returnType = Float.class;
               } else if (Double.TYPE == returnType) {
                  returnType = Double.class;
               }

               typedResolver = this.delegate.asResolver(this.prefix + annotation.name(), annotation.defaultValue(), returnType, annotation.converter(), annotation.parameterizedBy(), annotation.projectStageAware(), annotation.evaluateVariables());
               if (this.cacheMs > 0L) {
                  typedResolver.cacheFor(TimeUnit.MILLISECONDS, this.cacheMs);
               }

               ConfigResolver.TypedResolver<?> existing = (ConfigResolver.TypedResolver)this.resolvers.putIfAbsent(method, typedResolver);
               if (existing != null) {
                  typedResolver = existing;
               }
            }

            return typedResolver.getValue();
         }
      }
   }
}
