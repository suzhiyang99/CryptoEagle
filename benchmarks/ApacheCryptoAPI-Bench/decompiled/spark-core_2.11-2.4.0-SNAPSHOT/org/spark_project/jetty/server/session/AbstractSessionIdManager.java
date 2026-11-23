package org.spark_project.jetty.server.session;

import java.security.SecureRandom;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import org.spark_project.jetty.server.SessionIdManager;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public abstract class AbstractSessionIdManager extends AbstractLifeCycle implements SessionIdManager {
   private static final Logger LOG = Log.getLogger(AbstractSessionIdManager.class);
   private static final String __NEW_SESSION_ID = "org.spark_project.jetty.server.newSessionId";
   protected Random _random;
   protected boolean _weakRandom;
   protected String _workerName;
   protected String _workerAttr;
   protected long _reseed = 100000L;

   public AbstractSessionIdManager() {
   }

   public AbstractSessionIdManager(Random random) {
      this._random = random;
   }

   public String getWorkerName() {
      return this._workerName;
   }

   public void setWorkerName(String workerName) {
      if (this.isRunning()) {
         throw new IllegalStateException(this.getState());
      } else if (workerName.contains(".")) {
         throw new IllegalArgumentException("Name cannot contain '.'");
      } else {
         this._workerName = workerName;
      }
   }

   public Random getRandom() {
      return this._random;
   }

   public synchronized void setRandom(Random random) {
      this._random = random;
      this._weakRandom = false;
   }

   public long getReseed() {
      return this._reseed;
   }

   public void setReseed(long reseed) {
      this._reseed = reseed;
   }

   public String newSessionId(HttpServletRequest request, long created) {
      synchronized(this) {
         if (request == null) {
            return this.newSessionId(created);
         } else {
            String requested_id = request.getRequestedSessionId();
            if (requested_id != null) {
               String cluster_id = this.getClusterId(requested_id);
               if (this.idInUse(cluster_id)) {
                  return cluster_id;
               }
            }

            String new_id = (String)request.getAttribute("org.spark_project.jetty.server.newSessionId");
            if (new_id != null && this.idInUse(new_id)) {
               return new_id;
            } else {
               String id = this.newSessionId((long)request.hashCode());
               request.setAttribute("org.spark_project.jetty.server.newSessionId", id);
               return id;
            }
         }
      }
   }

   public String newSessionId(long seedTerm) {
      String id = null;

      while(id == null || id.length() == 0 || this.idInUse(id)) {
         long r0 = this._weakRandom ? (long)this.hashCode() ^ Runtime.getRuntime().freeMemory() ^ (long)this._random.nextInt() ^ seedTerm << 32 : this._random.nextLong();
         if (r0 < 0L) {
            r0 = -r0;
         }

         if (this._reseed > 0L && r0 % this._reseed == 1L) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Reseeding {}", this);
            }

            if (this._random instanceof SecureRandom) {
               SecureRandom secure = (SecureRandom)this._random;
               secure.setSeed(secure.generateSeed(8));
            } else {
               this._random.setSeed(this._random.nextLong() ^ System.currentTimeMillis() ^ seedTerm ^ Runtime.getRuntime().freeMemory());
            }
         }

         long r1 = this._weakRandom ? (long)this.hashCode() ^ Runtime.getRuntime().freeMemory() ^ (long)this._random.nextInt() ^ seedTerm << 32 : this._random.nextLong();
         if (r1 < 0L) {
            r1 = -r1;
         }

         id = Long.toString(r0, 36) + Long.toString(r1, 36);
         if (this._workerName != null) {
            id = this._workerName + id;
         }
      }

      return id;
   }

   public abstract void renewSessionId(String var1, String var2, HttpServletRequest var3);

   protected void doStart() throws Exception {
      this.initRandom();
      this._workerAttr = this._workerName != null && this._workerName.startsWith("$") ? this._workerName.substring(1) : null;
   }

   protected void doStop() throws Exception {
   }

   public void initRandom() {
      if (this._random == null) {
         try {
            this._random = new SecureRandom();
         } catch (Exception e) {
            LOG.warn("Could not generate SecureRandom for session-id randomness", (Throwable)e);
            this._random = new Random();
            this._weakRandom = true;
         }
      } else {
         this._random.setSeed(this._random.nextLong() ^ System.currentTimeMillis() ^ (long)this.hashCode() ^ Runtime.getRuntime().freeMemory());
      }

   }

   public String getNodeId(String clusterId, HttpServletRequest request) {
      if (this._workerName != null) {
         if (this._workerAttr == null) {
            return clusterId + '.' + this._workerName;
         }

         String worker = (String)request.getAttribute(this._workerAttr);
         if (worker != null) {
            return clusterId + '.' + worker;
         }
      }

      return clusterId;
   }

   public String getClusterId(String nodeId) {
      int dot = nodeId.lastIndexOf(46);
      return dot > 0 ? nodeId.substring(0, dot) : nodeId;
   }
}
