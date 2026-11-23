package org.spark_project.jetty.server.session;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import javax.servlet.http.HttpServletRequest;
import org.spark_project.jetty.server.Request;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.util.ClassLoadingObjectInputStream;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

public class JDBCSessionManager extends AbstractSessionManager {
   private static final Logger LOG = Log.getLogger(JDBCSessionManager.class);
   private ConcurrentHashMap<String, Session> _sessions;
   protected JDBCSessionIdManager _jdbcSessionIdMgr = null;
   protected long _saveIntervalSec = 60L;
   protected JDBCSessionIdManager.SessionTableSchema _sessionTableSchema;

   public void setSaveInterval(long sec) {
      this._saveIntervalSec = sec;
   }

   public long getSaveInterval() {
      return this._saveIntervalSec;
   }

   public void cacheInvalidate(Session session) {
   }

   public Session getSession(String idInCluster) {
      Session session = null;
      synchronized(this) {
         Session memSession = (Session)this._sessions.get(idInCluster);
         long now = System.currentTimeMillis();
         if (LOG.isDebugEnabled()) {
            if (memSession == null) {
               LOG.debug("getSession(" + idInCluster + "): not in session map, now=" + now + " lastSaved=" + (memSession == null ? 0L : memSession._lastSaved) + " interval=" + this._saveIntervalSec * 1000L);
            } else {
               LOG.debug("getSession(" + idInCluster + "): in session map,  hashcode=" + memSession.hashCode() + " now=" + now + " lastSaved=" + (memSession == null ? 0L : memSession._lastSaved) + " interval=" + this._saveIntervalSec * 1000L + " lastNode=" + memSession._lastNode + " thisNode=" + this.getSessionIdManager().getWorkerName() + " difference=" + (now - memSession._lastSaved));
            }
         }

         try {
            if (memSession == null) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("getSession(" + idInCluster + "): no session in session map. Reloading session data from db.");
               }

               session = this.loadSession(idInCluster, canonicalize(this._context.getContextPath()), getVirtualHost(this._context));
            } else if (now - memSession._lastSaved >= this._saveIntervalSec * 1000L) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("getSession(" + idInCluster + "): stale session. Reloading session data from db.");
               }

               session = this.loadSession(idInCluster, canonicalize(this._context.getContextPath()), getVirtualHost(this._context));
            } else {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("getSession(" + idInCluster + "): session in session map");
               }

               session = memSession;
            }
         } catch (Exception e) {
            LOG.warn("Unable to load session " + idInCluster, (Throwable)e);
            return null;
         }

         if (session != null) {
            if (session.getLastNode().equals(this.getSessionIdManager().getWorkerName()) && memSession != null) {
               session = memSession;
               if (LOG.isDebugEnabled()) {
                  LOG.debug("getSession({}): Session not stale {}", idInCluster, memSession);
               }
            } else if (session._expiryTime > 0L && session._expiryTime <= now) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("getSession ({}): Session has expired", idInCluster);
               }

               this._jdbcSessionIdMgr.removeSession(idInCluster);
               session = null;
            } else {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("getSession(" + idInCluster + "): lastNode=" + session.getLastNode() + " thisNode=" + this.getSessionIdManager().getWorkerName());
               }

               session.setLastNode(this.getSessionIdManager().getWorkerName());
               this._sessions.put(idInCluster, session);

               try {
                  this.updateSessionNode(session);
                  session.didActivate();
               } catch (Exception e) {
                  LOG.warn("Unable to update freshly loaded session " + idInCluster, (Throwable)e);
                  return null;
               }
            }
         } else {
            if (memSession != null) {
               this.removeSession(memSession, true);
            }

            LOG.debug("getSession({}): No session in database matching id={}", idInCluster, idInCluster);
         }

         return session;
      }
   }

   public int getSessions() {
      return this._sessions.size();
   }

   public void doStart() throws Exception {
      if (this._sessionIdManager == null) {
         throw new IllegalStateException("No session id manager defined");
      } else {
         this._jdbcSessionIdMgr = (JDBCSessionIdManager)this._sessionIdManager;
         this._sessionTableSchema = this._jdbcSessionIdMgr.getSessionTableSchema();
         this._sessions = new ConcurrentHashMap();
         super.doStart();
      }
   }

   public void doStop() throws Exception {
      super.doStop();
      this._sessions.clear();
      this._sessions = null;
   }

   protected void shutdownSessions() {
      long gracefulStopMs = this.getContextHandler().getServer().getStopTimeout();
      long stopTime = 0L;
      if (gracefulStopMs > 0L) {
         stopTime = System.nanoTime() + TimeUnit.NANOSECONDS.convert(gracefulStopMs, TimeUnit.MILLISECONDS);
      }

      for(ArrayList<Session> sessions = this._sessions == null ? new ArrayList() : new ArrayList(this._sessions.values()); sessions.size() > 0 && (stopTime > 0L && System.nanoTime() < stopTime || stopTime == 0L); sessions = new ArrayList(this._sessions.values())) {
         for(Session session : sessions) {
            try {
               session.save(false);
            } catch (Exception e) {
               LOG.warn(e);
            }

            this._sessions.remove(session.getClusterId());
         }

         if (stopTime == 0L) {
            break;
         }
      }

   }

   public void renewSessionId(String oldClusterId, String oldNodeId, String newClusterId, String newNodeId) {
      Session session = null;

      try {
         session = (Session)this._sessions.remove(oldClusterId);
         if (session != null) {
            synchronized(session) {
               session.setClusterId(newClusterId);
               session.setNodeId(newNodeId);
               this._sessions.put(newClusterId, session);
               this.updateSession(session);
            }
         }
      } catch (Exception e) {
         LOG.warn(e);
      }

      super.renewSessionId(oldClusterId, oldNodeId, newClusterId, newNodeId);
   }

   protected void invalidateSession(String idInCluster) {
      Session session = (Session)this._sessions.get(idInCluster);
      if (session != null) {
         session.invalidate();
      }

   }

   protected boolean removeSession(String idInCluster) {
      Session session = (Session)this._sessions.remove(idInCluster);

      try {
         if (session != null) {
            this.deleteSession(session);
         }
      } catch (Exception e) {
         LOG.warn("Problem deleting session id=" + idInCluster, (Throwable)e);
      }

      return session != null;
   }

   protected void addSession(AbstractSession session) {
      if (session != null) {
         this._sessions.put(session.getClusterId(), (Session)session);

         try {
            synchronized(session) {
               session.willPassivate();
               this.storeSession((Session)session);
               session.didActivate();
            }
         } catch (Exception e) {
            LOG.warn("Unable to store new session id=" + session.getId(), (Throwable)e);
         }

      }
   }

   protected AbstractSession newSession(HttpServletRequest request) {
      return new Session(request);
   }

   protected AbstractSession newSession(String sessionId, String rowId, long created, long accessed, long maxInterval) {
      return new Session(sessionId, rowId, created, accessed, maxInterval);
   }

   public boolean removeSession(AbstractSession session, boolean invalidate) {
      boolean removed = super.removeSession(session, invalidate);
      if (removed && !invalidate) {
         session.willPassivate();
      }

      return removed;
   }

   protected Set<String> expire(Set<String> sessionIds) {
      if (!this.isStopping() && !this.isStopped()) {
         Thread thread = Thread.currentThread();
         ClassLoader old_loader = thread.getContextClassLoader();
         Set<String> successfullyExpiredIds = new HashSet();

         Object sessionId;
         try {
            for(String sessionId : sessionIds) {
               if (LOG.isDebugEnabled()) {
                  LOG.debug("Expiring session id " + sessionId);
               }

               Session session = (Session)this._sessions.get(sessionId);
               if (session == null) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug("Force loading session id " + sessionId);
                  }

                  session = this.loadSession(sessionId, canonicalize(this._context.getContextPath()), getVirtualHost(this._context));
                  if (session == null) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Unrecognized session id=" + sessionId);
                     }
                     continue;
                  }

                  this._sessions.put(session.getClusterId(), session);
               }

               if (session != null) {
                  session.timeout();
                  successfullyExpiredIds.add(session.getClusterId());
               }
            }

            sessionId = successfullyExpiredIds;
            return (Set<String>)sessionId;
         } catch (Throwable t) {
            LOG.warn("Problem expiring sessions", t);
            sessionId = successfullyExpiredIds;
         } finally {
            thread.setContextClassLoader(old_loader);
         }

         return (Set<String>)sessionId;
      } else {
         return null;
      }
   }

   protected void expireCandidates(Set<String> candidateIds) {
      Iterator<String> itor = candidateIds.iterator();
      long now = System.currentTimeMillis();

      while(itor.hasNext()) {
         String id = (String)itor.next();

         try {
            Session memSession = (Session)this._sessions.get(id);
            if (memSession != null) {
               Session s = this.loadSession(id, canonicalize(this._context.getContextPath()), getVirtualHost(this._context));
               if (s == null) {
                  memSession.timeout();
               }
            }
         } catch (Exception var8) {
            LOG.warn("Error checking db for expiry for session {}", id);
         }
      }

   }

   protected Set<String> getCandidateExpiredIds() {
      HashSet<String> expiredIds = new HashSet();

      for(String id : this._sessions.keySet()) {
         Session session = (Session)this._sessions.get(id);
         if (session._expiryTime > 0L && System.currentTimeMillis() > session._expiryTime) {
            expiredIds.add(id);
         }
      }

      return expiredIds;
   }

   protected Session loadSession(final String id, final String canonicalContextPath, final String vhost) throws Exception {
      final AtomicReference<Session> _reference = new AtomicReference();
      final AtomicReference<Exception> _exception = new AtomicReference();
      Runnable load = new Runnable() {
         public void run() {
            try {
               Connection connection = JDBCSessionManager.this.getConnection();
               Throwable var2 = null;

               try {
                  PreparedStatement statement = JDBCSessionManager.this._sessionTableSchema.getLoadStatement(connection, id, canonicalContextPath, vhost);
                  Throwable var4 = null;

                  try {
                     ResultSet result = statement.executeQuery();
                     Throwable var6 = null;

                     try {
                        Session session = null;
                        if (result.next()) {
                           long maxInterval = result.getLong(JDBCSessionManager.this._sessionTableSchema.getMaxIntervalColumn());
                           if (maxInterval == -999L) {
                              maxInterval = (long)JDBCSessionManager.this.getMaxInactiveInterval();
                           }

                           session = (Session)JDBCSessionManager.this.newSession(id, result.getString(JDBCSessionManager.this._sessionTableSchema.getRowIdColumn()), result.getLong(JDBCSessionManager.this._sessionTableSchema.getCreateTimeColumn()), result.getLong(JDBCSessionManager.this._sessionTableSchema.getAccessTimeColumn()), maxInterval);
                           session.setCookieSetTime(result.getLong(JDBCSessionManager.this._sessionTableSchema.getCookieTimeColumn()));
                           session.setLastAccessedTime(result.getLong(JDBCSessionManager.this._sessionTableSchema.getLastAccessTimeColumn()));
                           session.setLastNode(result.getString(JDBCSessionManager.this._sessionTableSchema.getLastNodeColumn()));
                           session.setLastSaved(result.getLong(JDBCSessionManager.this._sessionTableSchema.getLastSavedTimeColumn()));
                           session.setExpiryTime(result.getLong(JDBCSessionManager.this._sessionTableSchema.getExpiryTimeColumn()));
                           session.setCanonicalContext(result.getString(JDBCSessionManager.this._sessionTableSchema.getContextPathColumn()));
                           session.setVirtualHost(result.getString(JDBCSessionManager.this._sessionTableSchema.getVirtualHostColumn()));
                           InputStream is = ((JDBCSessionIdManager)JDBCSessionManager.this.getSessionIdManager())._dbAdaptor.getBlobInputStream(result, JDBCSessionManager.this._sessionTableSchema.getMapColumn());
                           Throwable var11 = null;

                           try {
                              ClassLoadingObjectInputStream ois = new ClassLoadingObjectInputStream(is);
                              Throwable var13 = null;

                              try {
                                 Object o = ois.readObject();
                                 session.addAttributes((Map)o);
                              } catch (Throwable var125) {
                                 var13 = var125;
                                 throw var125;
                              } finally {
                                 if (ois != null) {
                                    if (var13 != null) {
                                       try {
                                          ois.close();
                                       } catch (Throwable var124) {
                                          var13.addSuppressed(var124);
                                       }
                                    } else {
                                       ois.close();
                                    }
                                 }

                              }
                           } catch (Throwable var127) {
                              var11 = var127;
                              throw var127;
                           } finally {
                              if (is != null) {
                                 if (var11 != null) {
                                    try {
                                       is.close();
                                    } catch (Throwable var123) {
                                       var11.addSuppressed(var123);
                                    }
                                 } else {
                                    is.close();
                                 }
                              }

                           }

                           if (JDBCSessionManager.LOG.isDebugEnabled()) {
                              JDBCSessionManager.LOG.debug("LOADED session " + session);
                           }
                        } else if (JDBCSessionManager.LOG.isDebugEnabled()) {
                           JDBCSessionManager.LOG.debug("Failed to load session " + id);
                        }

                        _reference.set(session);
                     } catch (Throwable var129) {
                        var6 = var129;
                        throw var129;
                     } finally {
                        if (result != null) {
                           if (var6 != null) {
                              try {
                                 result.close();
                              } catch (Throwable var122) {
                                 var6.addSuppressed(var122);
                              }
                           } else {
                              result.close();
                           }
                        }

                     }
                  } catch (Throwable var131) {
                     var4 = var131;
                     throw var131;
                  } finally {
                     if (statement != null) {
                        if (var4 != null) {
                           try {
                              statement.close();
                           } catch (Throwable var121) {
                              var4.addSuppressed(var121);
                           }
                        } else {
                           statement.close();
                        }
                     }

                  }
               } catch (Throwable var133) {
                  var2 = var133;
                  throw var133;
               } finally {
                  if (connection != null) {
                     if (var2 != null) {
                        try {
                           connection.close();
                        } catch (Throwable var120) {
                           var2.addSuppressed(var120);
                        }
                     } else {
                        connection.close();
                     }
                  }

               }
            } catch (Exception e) {
               _exception.set(e);
            }

         }
      };
      if (this._context == null) {
         load.run();
      } else {
         this._context.getContextHandler().handle((Request)null, load);
      }

      if (_exception.get() != null) {
         this._jdbcSessionIdMgr.removeSession(id);
         throw (Exception)_exception.get();
      } else {
         return (Session)_reference.get();
      }
   }

   protected void storeSession(Session session) throws Exception {
      if (session != null) {
         Connection connection = this.getConnection();
         Throwable var3 = null;

         try {
            PreparedStatement statement = connection.prepareStatement(this._jdbcSessionIdMgr._insertSession);
            Throwable var5 = null;

            try {
               String rowId = this.calculateRowId(session);
               long now = System.currentTimeMillis();
               connection.setAutoCommit(true);
               statement.setString(1, rowId);
               statement.setString(2, session.getClusterId());
               statement.setString(3, session.getCanonicalContext());
               statement.setString(4, session.getVirtualHost());
               statement.setString(5, this.getSessionIdManager().getWorkerName());
               statement.setLong(6, session.getAccessed());
               statement.setLong(7, session.getLastAccessedTime());
               statement.setLong(8, session.getCreationTime());
               statement.setLong(9, session.getCookieSetTime());
               statement.setLong(10, now);
               statement.setLong(11, session.getExpiryTime());
               statement.setLong(12, (long)session.getMaxInactiveInterval());
               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               ObjectOutputStream oos = new ObjectOutputStream(baos);
               oos.writeObject(session.getAttributeMap());
               oos.flush();
               byte[] bytes = baos.toByteArray();
               ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
               statement.setBinaryStream(13, bais, bytes.length);
               statement.executeUpdate();
               session.setRowId(rowId);
               session.setLastSaved(now);
            } catch (Throwable var34) {
               var5 = var34;
               throw var34;
            } finally {
               if (statement != null) {
                  if (var5 != null) {
                     try {
                        statement.close();
                     } catch (Throwable var33) {
                        var5.addSuppressed(var33);
                     }
                  } else {
                     statement.close();
                  }
               }

            }
         } catch (Throwable var36) {
            var3 = var36;
            throw var36;
         } finally {
            if (connection != null) {
               if (var3 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var32) {
                     var3.addSuppressed(var32);
                  }
               } else {
                  connection.close();
               }
            }

         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Stored session " + session);
         }

      }
   }

   protected void updateSession(Session data) throws Exception {
      if (data != null) {
         Connection connection = this.getConnection();
         Throwable var3 = null;

         try {
            PreparedStatement statement = connection.prepareStatement(this._jdbcSessionIdMgr._updateSession);
            Throwable var5 = null;

            try {
               long now = System.currentTimeMillis();
               connection.setAutoCommit(true);
               statement.setString(1, data.getClusterId());
               statement.setString(2, this.getSessionIdManager().getWorkerName());
               statement.setLong(3, data.getAccessed());
               statement.setLong(4, data.getLastAccessedTime());
               statement.setLong(5, now);
               statement.setLong(6, data.getExpiryTime());
               statement.setLong(7, (long)data.getMaxInactiveInterval());
               ByteArrayOutputStream baos = new ByteArrayOutputStream();
               ObjectOutputStream oos = new ObjectOutputStream(baos);
               oos.writeObject(data.getAttributeMap());
               oos.flush();
               byte[] bytes = baos.toByteArray();
               ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
               statement.setBinaryStream(8, bais, bytes.length);
               statement.setString(9, data.getRowId());
               statement.executeUpdate();
               data.setLastSaved(now);
            } catch (Throwable var33) {
               var5 = var33;
               throw var33;
            } finally {
               if (statement != null) {
                  if (var5 != null) {
                     try {
                        statement.close();
                     } catch (Throwable var32) {
                        var5.addSuppressed(var32);
                     }
                  } else {
                     statement.close();
                  }
               }

            }
         } catch (Throwable var35) {
            var3 = var35;
            throw var35;
         } finally {
            if (connection != null) {
               if (var3 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var31) {
                     var3.addSuppressed(var31);
                  }
               } else {
                  connection.close();
               }
            }

         }

         if (LOG.isDebugEnabled()) {
            LOG.debug("Updated session " + data);
         }

      }
   }

   protected void updateSessionNode(Session data) throws Exception {
      String nodeId = this.getSessionIdManager().getWorkerName();
      Connection connection = this.getConnection();
      Throwable var4 = null;

      try {
         PreparedStatement statement = connection.prepareStatement(this._jdbcSessionIdMgr._updateSessionNode);
         Throwable var6 = null;

         try {
            connection.setAutoCommit(true);
            statement.setString(1, nodeId);
            statement.setString(2, data.getRowId());
            statement.executeUpdate();
         } catch (Throwable var29) {
            var6 = var29;
            throw var29;
         } finally {
            if (statement != null) {
               if (var6 != null) {
                  try {
                     statement.close();
                  } catch (Throwable var28) {
                     var6.addSuppressed(var28);
                  }
               } else {
                  statement.close();
               }
            }

         }
      } catch (Throwable var31) {
         var4 = var31;
         throw var31;
      } finally {
         if (connection != null) {
            if (var4 != null) {
               try {
                  connection.close();
               } catch (Throwable var27) {
                  var4.addSuppressed(var27);
               }
            } else {
               connection.close();
            }
         }

      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Updated last node for session id=" + data.getId() + ", lastNode = " + nodeId);
      }

   }

   private void updateSessionAccessTime(Session data) throws Exception {
      Connection connection = this.getConnection();
      Throwable var3 = null;

      try {
         PreparedStatement statement = connection.prepareStatement(this._jdbcSessionIdMgr._updateSessionAccessTime);
         Throwable var5 = null;

         try {
            long now = System.currentTimeMillis();
            connection.setAutoCommit(true);
            statement.setString(1, this.getSessionIdManager().getWorkerName());
            statement.setLong(2, data.getAccessed());
            statement.setLong(3, data.getLastAccessedTime());
            statement.setLong(4, now);
            statement.setLong(5, data.getExpiryTime());
            statement.setLong(6, (long)data.getMaxInactiveInterval());
            statement.setString(7, data.getRowId());
            statement.executeUpdate();
            data.setLastSaved(now);
         } catch (Throwable var29) {
            var5 = var29;
            throw var29;
         } finally {
            if (statement != null) {
               if (var5 != null) {
                  try {
                     statement.close();
                  } catch (Throwable var28) {
                     var5.addSuppressed(var28);
                  }
               } else {
                  statement.close();
               }
            }

         }
      } catch (Throwable var31) {
         var3 = var31;
         throw var31;
      } finally {
         if (connection != null) {
            if (var3 != null) {
               try {
                  connection.close();
               } catch (Throwable var27) {
                  var3.addSuppressed(var27);
               }
            } else {
               connection.close();
            }
         }

      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Updated access time session id=" + data.getId() + " with lastsaved=" + data.getLastSaved());
      }

   }

   protected void deleteSession(Session data) throws Exception {
      Connection connection = this.getConnection();
      Throwable var3 = null;

      try {
         PreparedStatement statement = connection.prepareStatement(this._jdbcSessionIdMgr._deleteSession);
         Throwable var5 = null;

         try {
            connection.setAutoCommit(true);
            statement.setString(1, data.getRowId());
            statement.executeUpdate();
            if (LOG.isDebugEnabled()) {
               LOG.debug("Deleted Session " + data);
            }
         } catch (Throwable var28) {
            var5 = var28;
            throw var28;
         } finally {
            if (statement != null) {
               if (var5 != null) {
                  try {
                     statement.close();
                  } catch (Throwable var27) {
                     var5.addSuppressed(var27);
                  }
               } else {
                  statement.close();
               }
            }

         }
      } catch (Throwable var30) {
         var3 = var30;
         throw var30;
      } finally {
         if (connection != null) {
            if (var3 != null) {
               try {
                  connection.close();
               } catch (Throwable var26) {
                  var3.addSuppressed(var26);
               }
            } else {
               connection.close();
            }
         }

      }

   }

   private Connection getConnection() throws SQLException {
      return ((JDBCSessionIdManager)this.getSessionIdManager()).getConnection();
   }

   private String calculateRowId(Session data) {
      String rowId = canonicalize(this._context.getContextPath());
      rowId = rowId + "_" + getVirtualHost(this._context);
      rowId = rowId + "_" + data.getId();
      return rowId;
   }

   private static String getVirtualHost(ContextHandler.Context context) {
      String vhost = "0.0.0.0";
      if (context == null) {
         return vhost;
      } else {
         String[] vhosts = context.getContextHandler().getVirtualHosts();
         return vhosts != null && vhosts.length != 0 && vhosts[0] != null ? vhosts[0] : vhost;
      }
   }

   private static String canonicalize(String path) {
      return path == null ? "" : path.replace('/', '_').replace('.', '_').replace('\\', '_');
   }

   public class Session extends MemSession {
      private static final long serialVersionUID = 5208464051134226143L;
      protected boolean _dirty = false;
      protected long _expiryTime;
      protected long _lastSaved;
      protected String _lastNode;
      protected String _virtualHost;
      protected String _rowId;
      protected String _canonicalContext;

      protected Session(HttpServletRequest request) {
         super(JDBCSessionManager.this, request);
         int maxInterval = this.getMaxInactiveInterval();
         this._expiryTime = maxInterval <= 0 ? 0L : System.currentTimeMillis() + (long)maxInterval * 1000L;
         this._virtualHost = JDBCSessionManager.getVirtualHost(JDBCSessionManager.this._context);
         this._canonicalContext = JDBCSessionManager.canonicalize(JDBCSessionManager.this._context.getContextPath());
         this._lastNode = JDBCSessionManager.this.getSessionIdManager().getWorkerName();
      }

      protected Session(String sessionId, String rowId, long created, long accessed, long maxInterval) {
         super(JDBCSessionManager.this, created, accessed, sessionId);
         this._rowId = rowId;
         super.setMaxInactiveInterval((int)maxInterval);
         this._expiryTime = maxInterval <= 0L ? 0L : System.currentTimeMillis() + maxInterval * 1000L;
      }

      protected synchronized String getRowId() {
         return this._rowId;
      }

      protected synchronized void setRowId(String rowId) {
         this._rowId = rowId;
      }

      public synchronized void setVirtualHost(String vhost) {
         this._virtualHost = vhost;
      }

      public synchronized String getVirtualHost() {
         return this._virtualHost;
      }

      public synchronized long getLastSaved() {
         return this._lastSaved;
      }

      public synchronized void setLastSaved(long time) {
         this._lastSaved = time;
      }

      public synchronized void setExpiryTime(long time) {
         this._expiryTime = time;
      }

      public synchronized long getExpiryTime() {
         return this._expiryTime;
      }

      public synchronized void setCanonicalContext(String str) {
         this._canonicalContext = str;
      }

      public synchronized String getCanonicalContext() {
         return this._canonicalContext;
      }

      public synchronized void setLastNode(String node) {
         this._lastNode = node;
      }

      public synchronized String getLastNode() {
         return this._lastNode;
      }

      public void setAttribute(String name, Object value) {
         Object old = this.changeAttribute(name, value);
         if (value != null || old != null) {
            this._dirty = true;
         }
      }

      public void removeAttribute(String name) {
         Object old = this.changeAttribute(name, (Object)null);
         if (old != null) {
            this._dirty = true;
         }

      }

      protected boolean access(long time) {
         synchronized(this) {
            if (super.access(time)) {
               int maxInterval = this.getMaxInactiveInterval();
               this._expiryTime = maxInterval <= 0 ? 0L : time + (long)maxInterval * 1000L;
               return true;
            } else {
               return false;
            }
         }
      }

      public void setMaxInactiveInterval(int secs) {
         synchronized(this) {
            super.setMaxInactiveInterval(secs);
            int maxInterval = this.getMaxInactiveInterval();
            this._expiryTime = maxInterval <= 0 ? 0L : System.currentTimeMillis() + (long)maxInterval * 1000L;

            try {
               JDBCSessionManager.this.updateSessionAccessTime(this);
            } catch (Exception e) {
               LOG.warn("Problem saving changed max idle time for session " + this, (Throwable)e);
            }

         }
      }

      protected void complete() {
         synchronized(this) {
            super.complete();

            try {
               if (this.isValid()) {
                  if (this._dirty) {
                     this.save(true);
                  } else if (this.getAccessed() - this._lastSaved >= JDBCSessionManager.this.getSaveInterval() * 1000L) {
                     JDBCSessionManager.this.updateSessionAccessTime(this);
                  }
               }
            } catch (Exception e) {
               LOG.warn("Problem persisting changed session data id=" + this.getId(), (Throwable)e);
            } finally {
               this._dirty = false;
            }

         }
      }

      protected void save() throws Exception {
         synchronized(this) {
            try {
               JDBCSessionManager.this.updateSession(this);
            } finally {
               this._dirty = false;
            }

         }
      }

      protected void save(boolean reactivate) throws Exception {
         synchronized(this) {
            if (this._dirty) {
               this.willPassivate();
               JDBCSessionManager.this.updateSession(this);
               if (reactivate) {
                  this.didActivate();
               }
            }

         }
      }

      protected void timeout() throws IllegalStateException {
         if (LOG.isDebugEnabled()) {
            LOG.debug("Timing out session id=" + this.getClusterId());
         }

         super.timeout();
      }

      public String toString() {
         return "Session rowId=" + this._rowId + ",id=" + this.getId() + ",lastNode=" + this._lastNode + ",created=" + this.getCreationTime() + ",accessed=" + this.getAccessed() + ",lastAccessed=" + this.getLastAccessedTime() + ",cookieSet=" + this.getCookieSetTime() + ",maxInterval=" + this.getMaxInactiveInterval() + ",lastSaved=" + this._lastSaved + ",expiry=" + this._expiryTime;
      }
   }
}
