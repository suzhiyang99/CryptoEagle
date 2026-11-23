package org.apache.openejb.core.stateful;

import org.apache.openejb.util.Duration;

public interface Cache<K, V> {
   CacheListener<V> getListener();

   void setListener(CacheListener<V> var1);

   void add(K var1, V var2);

   V checkOut(K var1, boolean var2) throws Exception;

   void checkIn(K var1);

   V remove(K var1);

   void removeAll(CacheFilter<V> var1);

   void init();

   void destroy();

   public interface CacheFilter<V> {
      boolean matches(V var1);
   }

   public interface CacheListener<V> {
      void afterLoad(V var1) throws Exception;

      void beforeStore(V var1) throws Exception;

      void timedOut(V var1);
   }

   public interface TimeOut {
      Duration getTimeOut();
   }
}
