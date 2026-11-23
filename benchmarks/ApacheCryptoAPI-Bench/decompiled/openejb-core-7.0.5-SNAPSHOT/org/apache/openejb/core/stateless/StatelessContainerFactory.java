package org.apache.openejb.core.stateless;

import org.apache.openejb.spi.SecurityService;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.Pool;

public class StatelessContainerFactory {
   private final Pool.Builder pool = new Pool.Builder();
   private Integer max;
   private Object id;
   private SecurityService securityService;
   private Duration accessTimeout;
   private int callbackThreads = 5;
   private Duration closeTimeout;
   private boolean useOneSchedulerThreadByBean = false;
   private int evictionThreads = 1;

   public void setEvictionThreads(int evictionThreads) {
      this.evictionThreads = evictionThreads;
   }

   public void setUseOneSchedulerThreadByBean(boolean useOneSchedulerThreadByBean) {
      this.useOneSchedulerThreadByBean = useOneSchedulerThreadByBean;
   }

   public void setCallbackThreads(int callbackThreads) {
      this.callbackThreads = callbackThreads;
   }

   public void setId(Object id) {
      this.id = id;
   }

   public void setSecurityService(SecurityService securityService) {
      this.securityService = securityService;
   }

   /** @deprecated */
   @Deprecated
   public void setTimeOut(Duration accessTimeout) {
      this.accessTimeout = accessTimeout;
   }

   public void setAccessTimeout(Duration accessTimeout) {
      if (this.accessTimeout == null) {
         this.setTimeOut(accessTimeout);
      }

   }

   public void setMaxSize(int max) {
      if (this.max == null) {
         this.setPoolSize(max);
      }

   }

   /** @deprecated */
   @Deprecated
   public void setPoolSize(int max) {
      this.max = max;
      this.pool.setPoolSize(max);
   }

   public void setMinSize(int min) {
      this.pool.setMinSize(min);
   }

   public void setStrictPooling(boolean strict) {
      this.pool.setStrictPooling(strict);
   }

   public void setMaxAge(Duration maxAge) {
      this.pool.setMaxAge(maxAge);
   }

   public void setIdleTimeout(Duration idleTimeout) {
      this.pool.setIdleTimeout(idleTimeout);
   }

   public void setSweepInterval(Duration interval) {
      this.pool.setSweepInterval(interval);
   }

   public void setReplaceAged(boolean replaceAged) {
      this.pool.setReplaceAged(replaceAged);
   }

   public void setReplaceFlushed(boolean replaceFlushed) {
      this.pool.setReplaceFlushed(replaceFlushed);
   }

   public void setGarbageCollection(boolean garbageCollection) {
      this.pool.setGarbageCollection(garbageCollection);
   }

   public void setMaxAgeOffset(double maxAgeOffset) {
      this.pool.setMaxAgeOffset(maxAgeOffset);
   }

   public void setCloseTimeout(Duration closeTimeout) {
      this.closeTimeout = closeTimeout;
   }

   public StatelessContainer create() {
      return new StatelessContainer(this.id, this.securityService, this.accessTimeout, this.closeTimeout, this.pool, this.callbackThreads, this.useOneSchedulerThreadByBean, this.evictionThreads);
   }
}
