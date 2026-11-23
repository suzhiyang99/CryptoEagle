package org.apache.openejb.util;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class Memoizer<K, V> implements Computable<K, V> {
   private final ConcurrentMap<K, Future<V>> cache = new ConcurrentHashMap();
   private final Computable<K, V> c;

   public Memoizer(Computable<K, V> c) {
      if (c == null) {
         throw new NullPointerException("Computable cache value source algorithm may not be null");
      } else {
         this.c = c;
      }
   }

   public V compute(final K key) throws InterruptedException {
      while(true) {
         Future<V> future = (Future)this.cache.get(key);
         if (future == null) {
            Callable<V> eval = new Callable<V>() {
               public V call() throws Exception {
                  return Memoizer.this.c.compute(key);
               }
            };
            FutureTask<V> futureTask = new FutureTask(eval);
            future = (Future)this.cache.putIfAbsent(key, futureTask);
            if (future == null) {
               future = futureTask;
               futureTask.run();
            }
         }

         try {
            return (V)future.get();
         } catch (ExecutionException var5) {
            if (var5.getCause() != null && NoClassDefFoundError.class.isInstance(var5.getCause())) {
               return null;
            }

            var5.printStackTrace();
         }
      }
   }

   public ConcurrentMap<K, Future<V>> getCache() {
      return this.cache;
   }
}
