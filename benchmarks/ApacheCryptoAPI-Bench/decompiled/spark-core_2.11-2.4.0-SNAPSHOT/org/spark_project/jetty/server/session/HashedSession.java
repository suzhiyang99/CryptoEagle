package org.spark_project.jetty.server.session;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import org.spark_project.jetty.util.IO;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class HashedSession extends MemSession {
   private static final Logger LOG = Log.getLogger(HashedSession.class);
   private final HashSessionManager _hashSessionManager;
   private transient boolean _idled = false;
   private transient boolean _saveFailed = false;
   private transient long _lastSaved = 0L;
   private transient boolean _deIdleFailed = false;

   protected HashedSession(HashSessionManager hashSessionManager, HttpServletRequest request) {
      super(hashSessionManager, request);
      this._hashSessionManager = hashSessionManager;
   }

   protected HashedSession(HashSessionManager hashSessionManager, long created, long accessed, String clusterId) {
      super(hashSessionManager, created, accessed, clusterId);
      this._hashSessionManager = hashSessionManager;
   }

   protected void checkValid() {
      if (!this._deIdleFailed && this._hashSessionManager._idleSavePeriodMs != 0L) {
         this.deIdle();
      }

      super.checkValid();
   }

   public void setMaxInactiveInterval(int secs) {
      super.setMaxInactiveInterval(secs);
      if (this.getMaxInactiveInterval() > 0 && (long)this.getMaxInactiveInterval() * 1000L / 10L < this._hashSessionManager._scavengePeriodMs) {
         this._hashSessionManager.setScavengePeriod((secs + 9) / 10);
      }

   }

   protected void doInvalidate() throws IllegalStateException {
      super.doInvalidate();
      this.remove();
   }

   synchronized void remove() {
      if (this._hashSessionManager._storeDir != null && this.getId() != null) {
         String id = this.getId();
         File f = new File(this._hashSessionManager._storeDir, id);
         f.delete();
      }

   }

   synchronized void save(boolean reactivate) throws Exception {
      if (!this.isIdled() && !this._saveFailed) {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Saving {} {}", super.getId(), reactivate);
         }

         try {
            this.willPassivate();
            this.save();
            if (reactivate) {
               this.didActivate();
            } else {
               this.clearAttributes();
            }
         } catch (Exception e) {
            LOG.warn("Problem saving session " + super.getId(), (Throwable)e);
            this._idled = false;
         }
      }

   }

   synchronized void save() throws Exception {
      File file = null;
      if (!this._saveFailed && this._hashSessionManager._storeDir != null && this._lastSaved < this.getAccessed()) {
         file = new File(this._hashSessionManager._storeDir, super.getId());
         if (file.exists()) {
            file.delete();
         }

         try {
            FileOutputStream fos = new FileOutputStream(file, false);
            Throwable var3 = null;

            try {
               this._lastSaved = System.currentTimeMillis();
               this.save(fos);
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            } finally {
               if (fos != null) {
                  if (var3 != null) {
                     try {
                        fos.close();
                     } catch (Throwable var12) {
                        var3.addSuppressed(var12);
                     }
                  } else {
                     fos.close();
                  }
               }

            }
         } catch (Exception e) {
            this.saveFailed();
            if (file != null) {
               file.delete();
            }

            throw e;
         }
      }

   }

   public synchronized void save(OutputStream os) throws IOException {
      DataOutputStream out = new DataOutputStream(os);
      out.writeUTF(this.getClusterId());
      out.writeUTF(this.getNodeId());
      out.writeLong(this.getCreationTime());
      out.writeLong(this.getAccessed());
      out.writeInt(this.getRequests());
      out.writeInt(this.getAttributes());
      ObjectOutputStream oos = new ObjectOutputStream(out);
      Enumeration<String> e = this.getAttributeNames();

      while(e.hasMoreElements()) {
         String key = (String)e.nextElement();
         oos.writeUTF(key);
         oos.writeObject(this.doGet(key));
      }

      out.writeInt(this.getMaxInactiveInterval());
   }

   public synchronized void deIdle() {
      if (this.isIdled() && !this._deIdleFailed) {
         this.access(System.currentTimeMillis());
         if (LOG.isDebugEnabled()) {
            LOG.debug("De-idling " + super.getId());
         }

         FileInputStream fis = null;

         try {
            File file = new File(this._hashSessionManager._storeDir, super.getId());
            if (!file.exists() || !file.canRead()) {
               throw new FileNotFoundException(file.getName());
            }

            fis = new FileInputStream(file);
            this._idled = false;
            this._hashSessionManager.restoreSession(fis, this);
            IO.close((InputStream)fis);
            this.didActivate();
            if (this._hashSessionManager._savePeriodMs == 0L) {
               file.delete();
            }
         } catch (Exception e) {
            this.deIdleFailed();
            LOG.warn("Problem de-idling session " + super.getId(), (Throwable)e);
            if (fis != null) {
               IO.close((InputStream)fis);
            }

            this.invalidate();
         }
      }

   }

   public synchronized void idle() throws Exception {
      this.save(false);
      this._idled = true;
   }

   public synchronized boolean isIdled() {
      return this._idled;
   }

   public synchronized boolean isSaveFailed() {
      return this._saveFailed;
   }

   public synchronized void saveFailed() {
      this._saveFailed = true;
   }

   public synchronized void deIdleFailed() {
      this._deIdleFailed = true;
   }

   public synchronized boolean isDeIdleFailed() {
      return this._deIdleFailed;
   }
}
