package org.spark_project.jetty.server.session;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.util.ClassLoadingObjectInputStream;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ScheduledExecutorScheduler;
import org.spark_project.jetty.util.thread.Scheduler;

public class HashSessionManager extends AbstractSessionManager {
   static final Logger LOG;
   protected final ConcurrentMap<String, HashedSession> _sessions = new ConcurrentHashMap();
   private Scheduler _timer;
   private Scheduler.Task _task;
   long _scavengePeriodMs = 30000L;
   long _savePeriodMs = 0L;
   long _idleSavePeriodMs = 0L;
   private Scheduler.Task _saveTask;
   File _storeDir;
   private boolean _lazyLoad = false;
   private volatile boolean _sessionsLoaded = false;
   private boolean _deleteUnrestorableSessions = false;

   public void doStart() throws Exception {
      this._timer = (Scheduler)this.getSessionHandler().getServer().getBean(Scheduler.class);
      if (this._timer == null) {
         ServletContext context = ContextHandler.getCurrentContext();
         if (context != null) {
            this._timer = (Scheduler)context.getAttribute("org.spark_project.jetty.server.session.timer");
         }
      }

      if (this._timer == null) {
         this._timer = new ScheduledExecutorScheduler(this.toString() + "Timer", true);
         this.addBean(this._timer, true);
      } else {
         this.addBean(this._timer, false);
      }

      super.doStart();
      this.setScavengePeriod(this.getScavengePeriod());
      if (this._storeDir != null) {
         if (!this._storeDir.exists()) {
            this._storeDir.mkdirs();
         }

         if (!this._lazyLoad) {
            this.restoreSessions();
         }
      }

      this.setSavePeriod(this.getSavePeriod());
   }

   public void doStop() throws Exception {
      synchronized(this) {
         if (this._saveTask != null) {
            this._saveTask.cancel();
         }

         this._saveTask = null;
         if (this._task != null) {
            this._task.cancel();
         }

         this._task = null;
         if (this.isManaged(this._timer)) {
            this.removeBean(this._timer);
         }

         this._timer = null;
      }

      super.doStop();
      this._sessions.clear();
   }

   public int getScavengePeriod() {
      return (int)(this._scavengePeriodMs / 1000L);
   }

   public int getSessions() {
      int sessions = super.getSessions();
      if (LOG.isDebugEnabled() && this._sessions.size() != sessions) {
         LOG.warn("sessions: " + this._sessions.size() + "!=" + sessions);
      }

      return sessions;
   }

   public int getIdleSavePeriod() {
      return this._idleSavePeriodMs <= 0L ? 0 : (int)(this._idleSavePeriodMs / 1000L);
   }

   public void setIdleSavePeriod(int seconds) {
      this._idleSavePeriodMs = (long)seconds * 1000L;
   }

   public void setMaxInactiveInterval(int seconds) {
      super.setMaxInactiveInterval(seconds);
      if (this._dftMaxIdleSecs > 0 && this._scavengePeriodMs > (long)this._dftMaxIdleSecs * 1000L) {
         this.setScavengePeriod((this._dftMaxIdleSecs + 9) / 10);
      }

   }

   public void setSavePeriod(int seconds) {
      long period = (long)seconds * 1000L;
      if (period < 0L) {
         period = 0L;
      }

      this._savePeriodMs = period;
      if (this._timer != null) {
         synchronized(this) {
            if (this._saveTask != null) {
               this._saveTask.cancel();
            }

            this._saveTask = null;
            if (this._savePeriodMs > 0L && this._storeDir != null) {
               this._saveTask = this._timer.schedule(new Saver(), this._savePeriodMs, TimeUnit.MILLISECONDS);
            }
         }
      }

   }

   public int getSavePeriod() {
      return this._savePeriodMs <= 0L ? 0 : (int)(this._savePeriodMs / 1000L);
   }

   public void setScavengePeriod(int seconds) {
      if (seconds == 0) {
         seconds = 60;
      }

      long old_period = this._scavengePeriodMs;
      long period = (long)seconds * 1000L;
      if (period > 60000L) {
         period = 60000L;
      }

      if (period < 1000L) {
         period = 1000L;
      }

      this._scavengePeriodMs = period;
      synchronized(this) {
         if (this._timer != null && (period != old_period || this._task == null)) {
            if (this._task != null) {
               this._task.cancel();
               this._task = null;
            }

            this._task = this._timer.schedule(new Scavenger(), this._scavengePeriodMs, TimeUnit.MILLISECONDS);
         }

      }
   }

   protected void scavenge() {
      if (!this.isStopping() && !this.isStopped()) {
         Thread thread = Thread.currentThread();
         ClassLoader old_loader = thread.getContextClassLoader();

         try {
            if (this._loader != null) {
               thread.setContextClassLoader(this._loader);
            }

            long now = System.currentTimeMillis();
            __log.debug("Scavenging sessions at {}", now);

            for(HashedSession session : this._sessions.values()) {
               long idleTime = (long)session.getMaxInactiveInterval() * 1000L;
               if (idleTime > 0L && session.getAccessed() + idleTime < now) {
                  try {
                     session.timeout();
                  } catch (Exception e) {
                     __log.warn("Problem scavenging sessions", (Throwable)e);
                  }
               } else if (this._idleSavePeriodMs > 0L && session.getAccessed() + this._idleSavePeriodMs < now) {
                  try {
                     session.idle();
                  } catch (Exception e) {
                     __log.warn("Problem idling session " + session.getId(), (Throwable)e);
                  }
               }
            }
         } finally {
            thread.setContextClassLoader(old_loader);
         }

      }
   }

   protected void addSession(AbstractSession session) {
      if (this.isRunning()) {
         this._sessions.put(session.getClusterId(), (HashedSession)session);
      }

   }

   public AbstractSession getSession(String idInCluster) {
      if (this._lazyLoad && !this._sessionsLoaded) {
         try {
            this.restoreSessions();
         } catch (Exception e) {
            LOG.warn(e);
         }
      }

      Map<String, HashedSession> sessions = this._sessions;
      if (sessions == null) {
         return null;
      } else {
         HashedSession session = (HashedSession)sessions.get(idInCluster);
         if (session == null && this._lazyLoad) {
            session = this.restoreSession(idInCluster);
         }

         if (session == null) {
            return null;
         } else {
            if (this._idleSavePeriodMs != 0L) {
               session.deIdle();
            }

            return session;
         }
      }
   }

   protected void shutdownSessions() throws Exception {
      ArrayList<HashedSession> sessions = new ArrayList(this._sessions.values());

      for(int loop = 100; sessions.size() > 0 && loop-- > 0; sessions = new ArrayList(this._sessions.values())) {
         if (this.isStopping() && this._storeDir != null && this._storeDir.exists() && this._storeDir.canWrite()) {
            for(HashedSession session : sessions) {
               session.save(false);
               this._sessions.remove(session.getClusterId());
            }
         } else {
            for(HashedSession session : sessions) {
               session.invalidate();
            }
         }
      }

   }

   public void renewSessionId(String oldClusterId, String oldNodeId, String newClusterId, String newNodeId) {
      try {
         Map<String, HashedSession> sessions = this._sessions;
         if (sessions == null) {
            return;
         }

         HashedSession session = (HashedSession)sessions.remove(oldClusterId);
         if (session == null) {
            return;
         }

         session.remove();
         session.setClusterId(newClusterId);
         session.setNodeId(newNodeId);
         session.save();
         sessions.put(newClusterId, session);
         super.renewSessionId(oldClusterId, oldNodeId, newClusterId, newNodeId);
      } catch (Exception e) {
         LOG.warn(e);
      }

   }

   protected AbstractSession newSession(HttpServletRequest request) {
      return new HashedSession(this, request);
   }

   protected AbstractSession newSession(long created, long accessed, String clusterId) {
      return new HashedSession(this, created, accessed, clusterId);
   }

   protected boolean removeSession(String clusterId) {
      return this._sessions.remove(clusterId) != null;
   }

   public void setStoreDirectory(File dir) throws IOException {
      this._storeDir = dir.getCanonicalFile();
   }

   public File getStoreDirectory() {
      return this._storeDir;
   }

   public void setLazyLoad(boolean lazyLoad) {
      this._lazyLoad = lazyLoad;
   }

   public boolean isLazyLoad() {
      return this._lazyLoad;
   }

   public boolean isDeleteUnrestorableSessions() {
      return this._deleteUnrestorableSessions;
   }

   public void setDeleteUnrestorableSessions(boolean deleteUnrestorableSessions) {
      this._deleteUnrestorableSessions = deleteUnrestorableSessions;
   }

   public void restoreSessions() throws Exception {
      this._sessionsLoaded = true;
      if (this._storeDir != null && this._storeDir.exists()) {
         if (!this._storeDir.canRead()) {
            LOG.warn("Unable to restore Sessions: Cannot read from Session storage directory " + this._storeDir.getAbsolutePath());
         } else {
            String[] files = this._storeDir.list();

            for(int i = 0; files != null && i < files.length; ++i) {
               this.restoreSession(files[i]);
            }

         }
      }
   }

   protected synchronized HashedSession restoreSession(String idInCuster) {
      File file = new File(this._storeDir, idInCuster);
      Exception error = null;
      if (!file.exists()) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Not loading: {}", file);
         }

         return null;
      } else {
         try {
            FileInputStream in = new FileInputStream(file);
            Throwable var5 = null;

            try {
               HashedSession session = this.restoreSession(in, (HashedSession)null);
               this.addSession(session, false);
               session.didActivate();
               HashedSession var7 = session;
               return var7;
            } catch (Throwable var25) {
               var5 = var25;
               throw var25;
            } finally {
               if (in != null) {
                  if (var5 != null) {
                     try {
                        in.close();
                     } catch (Throwable var24) {
                        var5.addSuppressed(var24);
                     }
                  } else {
                     in.close();
                  }
               }

            }
         } catch (Exception e) {
            error = e;
         } finally {
            if (error != null) {
               if (this.isDeleteUnrestorableSessions() && file.exists() && file.getParentFile().equals(this._storeDir)) {
                  file.delete();
                  LOG.warn("Deleting file for unrestorable session {} {}", idInCuster, error);
                  __log.debug(error);
               } else {
                  __log.warn("Problem restoring session {} {}", idInCuster, error);
                  __log.debug(error);
               }
            } else if (this._savePeriodMs == 0L) {
               file.delete();
            }

         }

         return null;
      }
   }

   public void saveSessions(boolean reactivate) throws Exception {
      if (this._storeDir != null && this._storeDir.exists()) {
         if (!this._storeDir.canWrite()) {
            LOG.warn("Unable to save Sessions: Session persistence storage directory " + this._storeDir.getAbsolutePath() + " is not writeable");
         } else {
            for(HashedSession session : this._sessions.values()) {
               session.save(reactivate);
            }

         }
      }
   }

   public HashedSession restoreSession(InputStream is, HashedSession session) throws Exception {
      DataInputStream di = new DataInputStream(is);
      String clusterId = di.readUTF();
      di.readUTF();
      long created = di.readLong();
      long accessed = di.readLong();
      int requests = di.readInt();
      if (session == null) {
         session = (HashedSession)this.newSession(created, accessed, clusterId);
      }

      session.setRequests(requests);
      int size = di.readInt();
      this.restoreSessionAttributes(di, size, session);

      try {
         int maxIdle = di.readInt();
         session.setMaxInactiveInterval(maxIdle);
      } catch (IOException e) {
         LOG.debug("No maxInactiveInterval persisted for session " + clusterId);
         LOG.ignore(e);
      }

      return session;
   }

   private void restoreSessionAttributes(InputStream is, int size, HashedSession session) throws Exception {
      if (size > 0) {
         ClassLoadingObjectInputStream ois = new ClassLoadingObjectInputStream(is);

         for(int i = 0; i < size; ++i) {
            String key = ois.readUTF();
            Object value = ois.readObject();
            session.setAttribute(key, value);
         }
      }

   }

   static {
      LOG = SessionHandler.LOG;
   }

   protected class Scavenger implements Runnable {
      public void run() {
         try {
            HashSessionManager.this.scavenge();
         } finally {
            if (HashSessionManager.this._timer != null && HashSessionManager.this._timer.isRunning()) {
               HashSessionManager.this._task = HashSessionManager.this._timer.schedule(this, HashSessionManager.this._scavengePeriodMs, TimeUnit.MILLISECONDS);
            }

         }

      }
   }

   protected class Saver implements Runnable {
      public void run() {
         try {
            HashSessionManager.this.saveSessions(true);
         } catch (Exception e) {
            HashSessionManager.LOG.warn(e);
         } finally {
            if (HashSessionManager.this._timer != null && HashSessionManager.this._timer.isRunning()) {
               HashSessionManager.this._saveTask = HashSessionManager.this._timer.schedule(this, HashSessionManager.this._savePeriodMs, TimeUnit.MILLISECONDS);
            }

         }

      }
   }
}
