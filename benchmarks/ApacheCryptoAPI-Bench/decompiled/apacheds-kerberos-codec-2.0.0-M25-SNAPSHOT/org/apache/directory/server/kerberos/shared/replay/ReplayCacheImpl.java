package org.apache.directory.server.kerberos.shared.replay;

import java.io.Serializable;
import javax.security.auth.kerberos.KerberosPrincipal;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.store.AbstractPolicy;
import org.apache.directory.shared.kerberos.KerberosTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ReplayCacheImpl implements ReplayCache {
   private static final Logger LOG = LoggerFactory.getLogger(ReplayCacheImpl.class);
   private Cache cache;
   private static final long DEFAULT_CLOCK_SKEW = 300000L;
   private long clockSkew = 300000L;

   public ReplayCacheImpl(Cache cache) {
      this.cache = cache;
      this.cache.setMemoryStoreEvictionPolicy(new ClockskewExpirationPolicy());
   }

   public ReplayCacheImpl(Cache cache, long clockSkew) {
      this.cache = cache;
      this.clockSkew = clockSkew;
      this.cache.setMemoryStoreEvictionPolicy(new ClockskewExpirationPolicy());
   }

   public void setClockSkew(long clockSkew) {
      this.clockSkew = clockSkew;
   }

   public synchronized boolean isReplay(KerberosPrincipal serverPrincipal, KerberosPrincipal clientPrincipal, KerberosTime clientTime, int clientMicroSeconds) {
      ReplayCacheEntry entry = new ReplayCacheEntry(serverPrincipal, clientPrincipal, clientTime, clientMicroSeconds);
      Element element = this.cache.get(entry.createKey());
      if (element == null) {
         return false;
      } else {
         entry = (ReplayCacheEntry)element.getValue();
         return serverPrincipal.equals(entry.serverPrincipal) && clientTime.equals(entry.clientTime) && clientMicroSeconds == entry.clientMicroSeconds;
      }
   }

   public synchronized void save(KerberosPrincipal serverPrincipal, KerberosPrincipal clientPrincipal, KerberosTime clientTime, int clientMicroSeconds) {
      ReplayCacheEntry entry = new ReplayCacheEntry(serverPrincipal, clientPrincipal, clientTime, clientMicroSeconds);
      Element element = new Element(entry.createKey(), entry);
      this.cache.put(element);
   }

   public void clear() {
      LOG.debug("removing all the elements from cache");
      if (this.cache.getStatus().equals(Status.STATUS_ALIVE)) {
         this.cache.removeAll();
      }

   }

   public static class ReplayCacheEntry implements Serializable {
      private static final long serialVersionUID = 1L;
      private KerberosPrincipal serverPrincipal;
      private KerberosPrincipal clientPrincipal;
      private KerberosTime clientTime;
      private int clientMicroSeconds;

      public ReplayCacheEntry(KerberosPrincipal serverPrincipal, KerberosPrincipal clientPrincipal, KerberosTime clientTime, int clientMicroSeconds) {
         this.serverPrincipal = serverPrincipal;
         this.clientPrincipal = clientPrincipal;
         this.clientTime = clientTime;
         this.clientMicroSeconds = clientMicroSeconds;
      }

      public boolean equals(ReplayCacheEntry that) {
         return this.serverPrincipal.equals(that.serverPrincipal) && this.clientPrincipal.equals(that.clientPrincipal) && this.clientTime.equals(that.clientTime) && this.clientMicroSeconds == that.clientMicroSeconds;
      }

      public boolean isOutsideClockSkew(long clockSkew) {
         return !this.clientTime.isInClockSkew(clockSkew);
      }

      private String createKey() {
         StringBuilder sb = new StringBuilder();
         sb.append(this.clientPrincipal == null ? "null" : this.clientPrincipal.getName());
         sb.append('#');
         sb.append(this.serverPrincipal == null ? "null" : this.serverPrincipal.getName());
         sb.append('#');
         sb.append(this.clientTime == null ? "null" : this.clientTime.getDate());
         sb.append('#');
         sb.append(this.clientMicroSeconds);
         return sb.toString();
      }
   }

   private class ClockskewExpirationPolicy extends AbstractPolicy {
      private ClockskewExpirationPolicy() {
      }

      public String getName() {
         return "CLOCK-SKEW";
      }

      public boolean compare(Element element1, Element element2) {
         ReplayCacheEntry entry = (ReplayCacheEntry)element2.getValue();
         return entry.isOutsideClockSkew(ReplayCacheImpl.this.clockSkew);
      }
   }
}
