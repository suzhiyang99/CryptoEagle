package org.apache.manifoldcf.core.jdbcpool;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class ConnectionPoolManager {
   public static final String _rcsid = "@(#)$Id$";
   protected final Map<String, ConnectionPool> poolMap;
   protected final ConnectionCloserThread connectionCloserThread;
   protected volatile AtomicBoolean shuttingDown = new AtomicBoolean(false);
   protected final boolean debug;

   public ConnectionPoolManager(int count, boolean debug) throws ManifoldCFException {
      this.debug = debug;
      this.poolMap = new HashMap(count);
      this.connectionCloserThread = new ConnectionCloserThread();
      this.connectionCloserThread.start();
   }

   public synchronized ConnectionPool getPool(String poolKey) {
      return (ConnectionPool)this.poolMap.get(poolKey);
   }

   public synchronized ConnectionPool addAlias(String poolKey, String driverClassName, String dbURL, String userName, String password, int maxSize, long expiration) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
      Class.forName(driverClassName).newInstance();
      ConnectionPool cp = new ConnectionPool(dbURL, userName, password, maxSize, expiration, this.debug);
      this.poolMap.put(poolKey, cp);
      return cp;
   }

   public void flush() {
      synchronized(this) {
         for(String poolKey : this.poolMap.keySet()) {
            ConnectionPool cp = (ConnectionPool)this.poolMap.get(poolKey);
            cp.flushPool();
         }

      }
   }

   public void shutdown() {
      this.shuttingDown.set(true);

      while(this.connectionCloserThread.isAlive()) {
         try {
            Thread.sleep(1000L);
         } catch (InterruptedException var6) {
            this.connectionCloserThread.interrupt();
         }
      }

      synchronized(this) {
         for(String poolKey : this.poolMap.keySet()) {
            ConnectionPool cp = (ConnectionPool)this.poolMap.get(poolKey);
            cp.closePool();
         }

      }
   }

   protected void cleanupExpiredConnections(long cleanupTime) {
      ConnectionPool[] connectionPools;
      synchronized(this) {
         connectionPools = new ConnectionPool[this.poolMap.size()];
         int i = 0;

         for(String poolKey : this.poolMap.keySet()) {
            connectionPools[i++] = (ConnectionPool)this.poolMap.get(poolKey);
         }
      }

      for(int i = 0; i < connectionPools.length; ++i) {
         connectionPools[i].cleanupExpiredConnections(cleanupTime);
      }

   }

   protected class ConnectionCloserThread extends Thread {
      public ConnectionCloserThread() {
         this.setName("Connection pool reaper");
         this.setDaemon(true);
      }

      public void run() {
         while(true) {
            if (!ConnectionPoolManager.this.shuttingDown.get()) {
               ConnectionPoolManager.this.cleanupExpiredConnections(System.currentTimeMillis());
               if (!ConnectionPoolManager.this.shuttingDown.get()) {
                  try {
                     Thread.sleep(5000L);
                     continue;
                  } catch (InterruptedException var2) {
                  }
               }
            }

            return;
         }
      }
   }
}
