package org.apache.manifoldcf.core.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.jdbcpool.ConnectionPool;
import org.apache.manifoldcf.core.jdbcpool.ConnectionPoolManager;
import org.apache.manifoldcf.core.jdbcpool.WrappedConnection;
import org.apache.manifoldcf.core.system.Logging;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class ConnectionFactory {
   public static final String _rcsid = "@(#)$Id: ConnectionFactory.java 988245 2010-08-23 18:39:35Z kwright $";
   private static HashMap checkedOutConnections = new HashMap();
   private static PoolManager poolManager = new PoolManager();

   private ConnectionFactory() {
   }

   public static WrappedConnection getConnection(String jdbcUrl, String jdbcDriver, String database, String userName, String password, int maxDBConnections, boolean debug) throws ManifoldCFException {
      try {
         Class.forName(jdbcDriver);
      } catch (Exception e) {
         throw new ManifoldCFException("Unable to load database driver: " + e.getMessage(), e, 3);
      }

      ConnectionPoolManager cpm = poolManager.createPoolManager(debug);

      try {
         ConnectionPool cp = cpm.getPool(database);
         if (cp == null) {
            cpm.addAlias(database, jdbcDriver, jdbcUrl, userName, password, maxDBConnections, 300000L);
            cp = cpm.getPool(database);
         }

         return getConnectionWithRetries(cp);
      } catch (InterruptedException e) {
         throw new ManifoldCFException("Interrupted: " + e.getMessage(), e, 2);
      } catch (SQLException e) {
         throw new ManifoldCFException("Error getting connection: " + e.getMessage(), e, 4);
      } catch (ClassNotFoundException e) {
         throw new ManifoldCFException("Fatal error getting connection: " + e.getMessage(), e, 3);
      } catch (InstantiationException e) {
         throw new ManifoldCFException("Fatal error getting connection: " + e.getMessage(), e, 3);
      } catch (IllegalAccessException e) {
         throw new ManifoldCFException("Fatal error getting connection: " + e.getMessage(), e, 3);
      }
   }

   public static void releaseConnection(WrappedConnection c) throws ManifoldCFException {
      c.release();
   }

   public static void flush() {
      if (poolManager != null) {
         poolManager.flush();
      }

   }

   public static void releaseAll() {
      if (poolManager != null) {
         poolManager.releaseAll();
      }

   }

   protected static WrappedConnection getConnectionWithRetries(ConnectionPool cp) throws SQLException, InterruptedException {
      int retryCount = 3;

      while(true) {
         try {
            return cp.getConnection();
         } catch (SQLException e) {
            if (retryCount == 0) {
               throw e;
            }

            --retryCount;
            ManifoldCF.sleep(10000L);
         }
      }
   }

   protected static void checkConnections(long currentTime) {
      synchronized(checkedOutConnections) {
         for(Object key : checkedOutConnections.keySet()) {
            ConnectionTracker ct = (ConnectionTracker)checkedOutConnections.get(key);
            if (ct.hasExpired(currentTime)) {
               ct.printDetails();
            }
         }

      }
   }

   protected static class PoolManager {
      private Integer poolExistenceLock;
      private ConnectionPoolManager _pool;

      private PoolManager() {
         this.poolExistenceLock = new Integer(0);
         this._pool = null;
      }

      public ConnectionPoolManager createPoolManager(boolean debug) throws ManifoldCFException {
         synchronized(this.poolExistenceLock) {
            if (this._pool != null) {
               return this._pool;
            } else {
               this._pool = new ConnectionPoolManager(100, debug);
               return this._pool;
            }
         }
      }

      public void releaseAll() {
         ConnectionPoolManager thisPool;
         synchronized(this.poolExistenceLock) {
            if (this._pool == null) {
               return;
            }

            thisPool = this._pool;
            this._pool = null;
         }

         thisPool.shutdown();
      }

      public void flush() {
         synchronized(this.poolExistenceLock) {
            if (this._pool != null) {
               this._pool.flush();
            }

         }
      }
   }

   protected static class ConnectionTracker {
      protected Connection theConnection;
      protected long checkoutTime;
      protected Exception theTrace;

      public ConnectionTracker(Connection theConnection) {
         this.theConnection = theConnection;
         this.checkoutTime = System.currentTimeMillis();
         this.theTrace = new Exception("Stack trace");
      }

      public boolean hasExpired(long currentTime) {
         return this.checkoutTime + 300000L < currentTime;
      }

      public void printDetails() {
         Logging.db.error("Connection handle may have been abandoned: " + this.theConnection.toString(), this.theTrace);
      }
   }
}
