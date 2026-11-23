package org.apache.manifoldcf.core.jdbcpool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.apache.manifoldcf.core.system.Logging;

public class ConnectionPool {
   public static final String _rcsid = "@(#)$Id$";
   protected final String dbURL;
   protected final String userName;
   protected final String password;
   protected volatile int freePointer;
   protected volatile int activeConnections;
   protected volatile boolean closed;
   protected final Connection[] freeConnections;
   protected final long[] connectionCleanupTimeouts;
   protected final long expiration;
   protected final boolean debug;
   protected final Set<WrappedConnection> outstandingConnections = new HashSet();

   public ConnectionPool(String dbURL, String userName, String password, int maxConnections, long expiration, boolean debug) {
      this.dbURL = dbURL;
      this.userName = userName;
      this.password = password;
      this.freeConnections = new Connection[maxConnections];
      this.connectionCleanupTimeouts = new long[maxConnections];
      this.freePointer = 0;
      this.activeConnections = 0;
      this.closed = false;
      this.expiration = expiration;
      this.debug = debug;
   }

   public WrappedConnection getConnection() throws SQLException, InterruptedException {
      Exception instantiationException;
      if (this.debug) {
         instantiationException = new Exception("Possibly leaked db connection");
      } else {
         instantiationException = null;
      }

      Connection rval = null;
      boolean returnedValue = true;

      WrappedConnection var38;
      try {
         WrappedConnection wc;
         while(true) {
            synchronized(this) {
               if (this.freePointer > 0) {
                  if (this.closed) {
                     throw new InterruptedException("Pool already closed");
                  }

                  rval = this.freeConnections[--this.freePointer];
                  this.freeConnections[this.freePointer] = null;
                  boolean isValid = true;

                  try {
                     isValid = rval.isValid(1);
                  } catch (SQLException var29) {
                  } catch (AbstractMethodError var30) {
                  }

                  if (!isValid) {
                     Connection closeValue = rval;
                     rval = null;
                     --this.activeConnections;

                     try {
                        closeValue.close();
                     } catch (SQLException var31) {
                     }
                     continue;
                  }
               } else {
                  if (this.activeConnections == this.freeConnections.length) {
                     if (this.debug) {
                        synchronized(this.outstandingConnections) {
                           Logging.db.warn("Out of db connections, list of outstanding ones follows.");

                           for(WrappedConnection c : this.outstandingConnections) {
                              Logging.db.warn("Found a possibly leaked db connection", c.getInstantiationException());
                           }
                        }
                     }

                     this.wait();
                     continue;
                  }

                  ++this.activeConnections;
                  if (this.userName != null) {
                     rval = DriverManager.getConnection(this.dbURL, this.userName, this.password);
                  } else {
                     rval = DriverManager.getConnection(this.dbURL);
                  }
               }
            }

            wc = new WrappedConnection(this, rval, instantiationException);
            if (this.debug) {
               synchronized(this.outstandingConnections) {
                  this.outstandingConnections.add(wc);
               }
            }
            break;
         }

         var38 = wc;
      } catch (Error e) {
         returnedValue = false;
         throw e;
      } catch (RuntimeException e) {
         returnedValue = false;
         throw e;
      } catch (SQLException e) {
         returnedValue = false;
         throw e;
      } finally {
         if (!returnedValue) {
            if (rval != null) {
               this.release(rval);
            } else {
               synchronized(this) {
                  --this.activeConnections;
               }
            }
         }

      }

      return var38;
   }

   public synchronized void flushPool() {
      for(int i = 0; i < this.freePointer; ++i) {
         try {
            this.freeConnections[i].close();
         } catch (SQLException e) {
            Logging.db.warn("Error closing pooled connection: " + e.getMessage(), e);
         }

         this.freeConnections[i] = null;
         --this.activeConnections;
      }

      this.freePointer = 0;
      this.notifyAll();
   }

   public synchronized void closePool() {
      for(int i = 0; i < this.freePointer; ++i) {
         try {
            this.freeConnections[i].close();
         } catch (SQLException e) {
            Logging.db.warn("Error closing pooled connection: " + e.getMessage(), e);
         }

         this.freeConnections[i] = null;
      }

      this.freePointer = 0;
      this.closed = true;
      this.notifyAll();
   }

   public synchronized void cleanupExpiredConnections(long currentTime) {
      int i = 0;

      while(i < this.freePointer) {
         if (this.connectionCleanupTimeouts[i] <= currentTime) {
            Connection c = this.freeConnections[i];
            this.freeConnections[i] = null;
            --this.freePointer;
            --this.activeConnections;
            if (this.freePointer == i) {
               this.freeConnections[i] = null;
            } else {
               this.freeConnections[i] = this.freeConnections[this.freePointer];
               this.connectionCleanupTimeouts[i] = this.connectionCleanupTimeouts[this.freePointer];
               this.freeConnections[this.freePointer] = null;
            }

            try {
               c.close();
            } catch (SQLException e) {
               Logging.db.warn("Error closing pooled connection: " + e.getMessage(), e);
            }
         } else {
            ++i;
         }
      }

   }

   public void releaseConnection(WrappedConnection connection) {
      if (this.debug) {
         synchronized(this.outstandingConnections) {
            if (!this.outstandingConnections.contains(connection)) {
               Logging.db.warn("Released a connection that wasn't tracked!!");
            }

            this.outstandingConnections.remove(connection);
         }
      }

      this.release(connection.getConnection());
   }

   protected void release(Connection c) {
      synchronized(this) {
         this.freeConnections[this.freePointer] = c;
         this.connectionCleanupTimeouts[this.freePointer] = System.currentTimeMillis() + this.expiration;
         ++this.freePointer;
         this.notifyAll();
      }
   }
}
