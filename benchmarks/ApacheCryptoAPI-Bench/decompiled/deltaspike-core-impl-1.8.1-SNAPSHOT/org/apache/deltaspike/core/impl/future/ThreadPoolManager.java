package org.apache.deltaspike.core.impl.future;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import org.apache.deltaspike.core.api.config.base.CoreBaseConfig.TimeoutCustomization;

@ApplicationScoped
public class ThreadPoolManager {
   private final ConcurrentMap<String, ExecutorService> pools = new ConcurrentHashMap();
   private volatile ExecutorService defaultPool;
   private volatile boolean closed = false;

   @PreDestroy
   private void shutdown() {
      this.closed = true;
      long timeout = (long)TimeoutCustomization.FUTUREABLE_TERMINATION_TIMEOUT_IN_MILLISECONDS;

      for(ExecutorService es : this.pools.values()) {
         es.shutdown();

         try {
            es.awaitTermination(timeout, TimeUnit.MILLISECONDS);
         } catch (InterruptedException var7) {
            Thread.interrupted();
         }
      }

      if (this.defaultPool != null) {
         this.defaultPool.shutdown();

         try {
            this.defaultPool.awaitTermination(timeout, TimeUnit.MILLISECONDS);
         } catch (InterruptedException var6) {
            Thread.interrupted();
         }
      }

      this.pools.clear();
   }

   public ExecutorService find(String name) {
      if (this.closed) {
         throw new IllegalStateException("Container is shutting down");
      } else {
         ExecutorService pool = (ExecutorService)this.pools.get(name);
         if (pool == null) {
            this.ensureDefaultPool();
            pool = this.defaultPool;
         }

         return pool;
      }
   }

   private void ensureDefaultPool() {
      if (this.defaultPool == null) {
         synchronized(this) {
            if (this.defaultPool == null) {
               this.defaultPool = Executors.newFixedThreadPool(Math.max(2, Runtime.getRuntime().availableProcessors()));
            }
         }
      }

   }
}
