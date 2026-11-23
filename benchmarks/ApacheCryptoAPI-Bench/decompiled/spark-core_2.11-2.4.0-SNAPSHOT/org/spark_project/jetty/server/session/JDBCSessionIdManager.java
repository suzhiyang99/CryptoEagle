package org.spark_project.jetty.server.session;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import org.spark_project.jetty.server.Handler;
import org.spark_project.jetty.server.Server;
import org.spark_project.jetty.server.SessionManager;
import org.spark_project.jetty.server.handler.ContextHandler;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ScheduledExecutorScheduler;
import org.spark_project.jetty.util.thread.Scheduler;

public class JDBCSessionIdManager extends AbstractSessionIdManager {
   static final Logger LOG;
   public static final int MAX_INTERVAL_NOT_SET = -999;
   protected final HashSet<String> _sessionIds = new HashSet();
   protected Server _server;
   protected Driver _driver;
   protected String _driverClassName;
   protected String _connectionUrl;
   protected DataSource _datasource;
   protected String _jndiName;
   protected int _deleteBlockSize = 10;
   protected Scheduler.Task _task;
   protected Scheduler _scheduler;
   protected Scavenger _scavenger;
   protected boolean _ownScheduler;
   protected long _lastScavengeTime;
   protected long _scavengeIntervalMs = 600000L;
   protected String _createSessionIdTable;
   protected String _createSessionTable;
   protected String _selectBoundedExpiredSessions;
   private String _selectExpiredSessions;
   protected String _insertId;
   protected String _deleteId;
   protected String _queryId;
   protected String _insertSession;
   protected String _deleteSession;
   protected String _updateSession;
   protected String _updateSessionNode;
   protected String _updateSessionAccessTime;
   protected DatabaseAdaptor _dbAdaptor = new DatabaseAdaptor();
   protected SessionIdTableSchema _sessionIdTableSchema = new SessionIdTableSchema();
   protected SessionTableSchema _sessionTableSchema = new SessionTableSchema();

   public JDBCSessionIdManager(Server server) {
      this._server = server;
   }

   public JDBCSessionIdManager(Server server, Random random) {
      super(random);
      this._server = server;
   }

   public void setDriverInfo(String driverClassName, String connectionUrl) {
      this._driverClassName = driverClassName;
      this._connectionUrl = connectionUrl;
   }

   public void setDriverInfo(Driver driverClass, String connectionUrl) {
      this._driver = driverClass;
      this._connectionUrl = connectionUrl;
   }

   public void setDatasource(DataSource ds) {
      this._datasource = ds;
   }

   public DataSource getDataSource() {
      return this._datasource;
   }

   public String getDriverClassName() {
      return this._driverClassName;
   }

   public String getConnectionUrl() {
      return this._connectionUrl;
   }

   public void setDatasourceName(String jndi) {
      this._jndiName = jndi;
   }

   public String getDatasourceName() {
      return this._jndiName;
   }

   /** @deprecated */
   @Deprecated
   public void setBlobType(String name) {
      this._dbAdaptor.setBlobType(name);
   }

   public DatabaseAdaptor getDbAdaptor() {
      return this._dbAdaptor;
   }

   public void setDbAdaptor(DatabaseAdaptor dbAdaptor) {
      if (dbAdaptor == null) {
         throw new IllegalStateException("DbAdaptor cannot be null");
      } else {
         this._dbAdaptor = dbAdaptor;
      }
   }

   /** @deprecated */
   @Deprecated
   public String getBlobType() {
      return this._dbAdaptor.getBlobType();
   }

   /** @deprecated */
   @Deprecated
   public String getLongType() {
      return this._dbAdaptor.getLongType();
   }

   /** @deprecated */
   @Deprecated
   public void setLongType(String longType) {
      this._dbAdaptor.setLongType(longType);
   }

   public SessionIdTableSchema getSessionIdTableSchema() {
      return this._sessionIdTableSchema;
   }

   public void setSessionIdTableSchema(SessionIdTableSchema sessionIdTableSchema) {
      if (sessionIdTableSchema == null) {
         throw new IllegalArgumentException("Null SessionIdTableSchema");
      } else {
         this._sessionIdTableSchema = sessionIdTableSchema;
      }
   }

   public SessionTableSchema getSessionTableSchema() {
      return this._sessionTableSchema;
   }

   public void setSessionTableSchema(SessionTableSchema sessionTableSchema) {
      this._sessionTableSchema = sessionTableSchema;
   }

   public void setDeleteBlockSize(int bsize) {
      this._deleteBlockSize = bsize;
   }

   public int getDeleteBlockSize() {
      return this._deleteBlockSize;
   }

   public void setScavengeInterval(long sec) {
      if (sec <= 0L) {
         sec = 60L;
      }

      long old_period = this._scavengeIntervalMs;
      long period = sec * 1000L;
      this._scavengeIntervalMs = period;
      long tenPercent = this._scavengeIntervalMs / 10L;
      if (System.currentTimeMillis() % 2L == 0L) {
         this._scavengeIntervalMs += tenPercent;
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("Scavenging every " + this._scavengeIntervalMs + " ms");
      }

      synchronized(this) {
         if (this._scheduler != null && (period != old_period || this._task == null)) {
            if (this._task != null) {
               this._task.cancel();
            }

            if (this._scavenger == null) {
               this._scavenger = new Scavenger();
            }

            this._task = this._scheduler.schedule(this._scavenger, this._scavengeIntervalMs, TimeUnit.MILLISECONDS);
         }

      }
   }

   public long getScavengeInterval() {
      return this._scavengeIntervalMs / 1000L;
   }

   public void addSession(HttpSession session) {
      if (session != null) {
         synchronized(this._sessionIds) {
            String id = ((JDBCSessionManager.Session)session).getClusterId();

            try {
               this.insert(id);
               this._sessionIds.add(id);
            } catch (Exception e) {
               LOG.warn("Problem storing session id=" + id, (Throwable)e);
            }

         }
      }
   }

   public void addSession(String id) {
      if (id != null) {
         synchronized(this._sessionIds) {
            try {
               this.insert(id);
               this._sessionIds.add(id);
            } catch (Exception e) {
               LOG.warn("Problem storing session id=" + id, (Throwable)e);
            }

         }
      }
   }

   public void removeSession(HttpSession session) {
      if (session != null) {
         this.removeSession(((JDBCSessionManager.Session)session).getClusterId());
      }
   }

   public void removeSession(String id) {
      if (id != null) {
         synchronized(this._sessionIds) {
            if (LOG.isDebugEnabled()) {
               LOG.debug("Removing sessionid=" + id);
            }

            try {
               this._sessionIds.remove(id);
               this.delete(id);
            } catch (Exception e) {
               LOG.warn("Problem removing session id=" + id, (Throwable)e);
            }

         }
      }
   }

   public boolean idInUse(String id) {
      if (id == null) {
         return false;
      } else {
         String clusterId = this.getClusterId(id);
         boolean inUse = false;
         synchronized(this._sessionIds) {
            inUse = this._sessionIds.contains(clusterId);
         }

         if (inUse) {
            return true;
         } else {
            try {
               return this.exists(clusterId);
            } catch (Exception e) {
               LOG.warn("Problem checking inUse for id=" + clusterId, (Throwable)e);
               return false;
            }
         }
      }
   }

   public void invalidateAll(String id) {
      this.removeSession(id);
      synchronized(this._sessionIds) {
         Handler[] contexts = this._server.getChildHandlersByClass(ContextHandler.class);

         for(int i = 0; contexts != null && i < contexts.length; ++i) {
            SessionHandler sessionHandler = (SessionHandler)((ContextHandler)contexts[i]).getChildHandlerByClass(SessionHandler.class);
            if (sessionHandler != null) {
               SessionManager manager = sessionHandler.getSessionManager();
               if (manager != null && manager instanceof JDBCSessionManager) {
                  ((JDBCSessionManager)manager).invalidateSession(id);
               }
            }
         }

      }
   }

   public void renewSessionId(String oldClusterId, String oldNodeId, HttpServletRequest request) {
      String newClusterId = this.newSessionId((long)request.hashCode());
      synchronized(this._sessionIds) {
         this.removeSession(oldClusterId);
         this.addSession(newClusterId);
         Handler[] contexts = this._server.getChildHandlersByClass(ContextHandler.class);

         for(int i = 0; contexts != null && i < contexts.length; ++i) {
            SessionHandler sessionHandler = (SessionHandler)((ContextHandler)contexts[i]).getChildHandlerByClass(SessionHandler.class);
            if (sessionHandler != null) {
               SessionManager manager = sessionHandler.getSessionManager();
               if (manager != null && manager instanceof JDBCSessionManager) {
                  ((JDBCSessionManager)manager).renewSessionId(oldClusterId, oldNodeId, newClusterId, this.getNodeId(newClusterId, request));
               }
            }
         }

      }
   }

   public void doStart() throws Exception {
      this.initializeDatabase();
      this.prepareTables();
      super.doStart();
      if (LOG.isDebugEnabled()) {
         LOG.debug("Scavenging interval = " + this.getScavengeInterval() + " sec");
      }

      this._scheduler = (Scheduler)this._server.getBean(Scheduler.class);
      if (this._scheduler == null) {
         this._scheduler = new ScheduledExecutorScheduler();
         this._ownScheduler = true;
         this._scheduler.start();
      } else if (!this._scheduler.isStarted()) {
         throw new IllegalStateException("Shared scheduler not started");
      }

      this.setScavengeInterval(this.getScavengeInterval());
   }

   public void doStop() throws Exception {
      synchronized(this) {
         if (this._task != null) {
            this._task.cancel();
         }

         this._task = null;
         if (this._ownScheduler && this._scheduler != null) {
            this._scheduler.stop();
         }

         this._scheduler = null;
      }

      this._sessionIds.clear();
      super.doStop();
   }

   protected Connection getConnection() throws SQLException {
      return this._datasource != null ? this._datasource.getConnection() : DriverManager.getConnection(this._connectionUrl);
   }

   private void prepareTables() throws SQLException {
      if (this._sessionIdTableSchema == null) {
         throw new IllegalStateException("No SessionIdTableSchema");
      } else if (this._sessionTableSchema == null) {
         throw new IllegalStateException("No SessionTableSchema");
      } else {
         Connection connection = this.getConnection();
         Throwable var2 = null;

         try {
            Statement statement = connection.createStatement();
            Throwable var4 = null;

            try {
               connection.setAutoCommit(true);
               DatabaseMetaData metaData = connection.getMetaData();
               this._dbAdaptor.adaptTo(metaData);
               this._sessionTableSchema.setDatabaseAdaptor(this._dbAdaptor);
               this._sessionIdTableSchema.setDatabaseAdaptor(this._dbAdaptor);
               this._createSessionIdTable = this._sessionIdTableSchema.getCreateStatementAsString();
               this._insertId = this._sessionIdTableSchema.getInsertStatementAsString();
               this._deleteId = this._sessionIdTableSchema.getDeleteStatementAsString();
               this._queryId = this._sessionIdTableSchema.getSelectStatementAsString();
               String tableName = this._dbAdaptor.convertIdentifier(this._sessionIdTableSchema.getTableName());
               String schemaName = this._sessionIdTableSchema.getSchemaName() != null ? this._dbAdaptor.convertIdentifier(this._sessionIdTableSchema.getSchemaName()) : null;
               ResultSet result = metaData.getTables((String)null, schemaName, tableName, (String[])null);
               Throwable var9 = null;

               try {
                  if (!result.next()) {
                     statement.executeUpdate(this._createSessionIdTable);
                  }
               } catch (Throwable var158) {
                  var9 = var158;
                  throw var158;
               } finally {
                  if (result != null) {
                     if (var9 != null) {
                        try {
                           result.close();
                        } catch (Throwable var150) {
                           var9.addSuppressed(var150);
                        }
                     } else {
                        result.close();
                     }
                  }

               }

               tableName = this._dbAdaptor.convertIdentifier(this._sessionTableSchema.getTableName());
               schemaName = this._sessionTableSchema.getSchemaName() != null ? this._dbAdaptor.convertIdentifier(this._sessionTableSchema.getSchemaName()) : null;
               result = metaData.getTables((String)null, schemaName, tableName, (String[])null);
               var9 = null;

               try {
                  if (!result.next()) {
                     this._createSessionTable = this._sessionTableSchema.getCreateStatementAsString();
                     statement.executeUpdate(this._createSessionTable);
                  } else {
                     ResultSet colResult = null;

                     try {
                        colResult = metaData.getColumns((String)null, schemaName, tableName, this._dbAdaptor.convertIdentifier(this._sessionTableSchema.getMaxIntervalColumn()));
                     } catch (SQLException s) {
                        LOG.warn("Problem checking if " + this._sessionTableSchema.getTableName() + " table contains " + this._sessionTableSchema.getMaxIntervalColumn() + " column. Ensure table contains column definition: \"" + this._sessionTableSchema.getMaxIntervalColumn() + " long not null default -999\"");
                        throw s;
                     }

                     try {
                        if (!colResult.next()) {
                           try {
                              statement.executeUpdate(this._sessionTableSchema.getAlterTableForMaxIntervalAsString());
                           } catch (SQLException s) {
                              LOG.warn("Problem adding " + this._sessionTableSchema.getMaxIntervalColumn() + " column. Ensure table contains column definition: \"" + this._sessionTableSchema.getMaxIntervalColumn() + " long not null default -999\"");
                              throw s;
                           }
                        }
                     } finally {
                        colResult.close();
                     }
                  }
               } catch (Throwable var154) {
                  var9 = var154;
                  throw var154;
               } finally {
                  if (result != null) {
                     if (var9 != null) {
                        try {
                           result.close();
                        } catch (Throwable var149) {
                           var9.addSuppressed(var149);
                        }
                     } else {
                        result.close();
                     }
                  }

               }

               String index1 = "idx_" + this._sessionTableSchema.getTableName() + "_expiry";
               String index2 = "idx_" + this._sessionTableSchema.getTableName() + "_session";
               boolean index1Exists = false;
               boolean index2Exists = false;
               ResultSet result = metaData.getIndexInfo((String)null, schemaName, tableName, false, true);
               Throwable var13 = null;

               try {
                  while(result.next()) {
                     String idxName = result.getString("INDEX_NAME");
                     if (index1.equalsIgnoreCase(idxName)) {
                        index1Exists = true;
                     } else if (index2.equalsIgnoreCase(idxName)) {
                        index2Exists = true;
                     }
                  }
               } catch (Throwable var156) {
                  var13 = var156;
                  throw var156;
               } finally {
                  if (result != null) {
                     if (var13 != null) {
                        try {
                           result.close();
                        } catch (Throwable var148) {
                           var13.addSuppressed(var148);
                        }
                     } else {
                        result.close();
                     }
                  }

               }

               if (!index1Exists) {
                  statement.executeUpdate(this._sessionTableSchema.getCreateIndexOverExpiryStatementAsString(index1));
               }

               if (!index2Exists) {
                  statement.executeUpdate(this._sessionTableSchema.getCreateIndexOverSessionStatementAsString(index2));
               }

               this._insertSession = this._sessionTableSchema.getInsertSessionStatementAsString();
               this._deleteSession = this._sessionTableSchema.getDeleteSessionStatementAsString();
               this._updateSession = this._sessionTableSchema.getUpdateSessionStatementAsString();
               this._updateSessionNode = this._sessionTableSchema.getUpdateSessionNodeStatementAsString();
               this._updateSessionAccessTime = this._sessionTableSchema.getUpdateSessionAccessTimeStatementAsString();
               this._selectBoundedExpiredSessions = this._sessionTableSchema.getBoundedExpiredSessionsStatementAsString();
               this._selectExpiredSessions = this._sessionTableSchema.getSelectExpiredSessionsStatementAsString();
            } catch (Throwable var160) {
               var4 = var160;
               throw var160;
            } finally {
               if (statement != null) {
                  if (var4 != null) {
                     try {
                        statement.close();
                     } catch (Throwable var147) {
                        var4.addSuppressed(var147);
                     }
                  } else {
                     statement.close();
                  }
               }

            }
         } catch (Throwable var162) {
            var2 = var162;
            throw var162;
         } finally {
            if (connection != null) {
               if (var2 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var146) {
                     var2.addSuppressed(var146);
                  }
               } else {
                  connection.close();
               }
            }

         }

      }
   }

   private void insert(String id) throws SQLException {
      Connection connection = this.getConnection();
      Throwable var3 = null;

      try {
         PreparedStatement query = connection.prepareStatement(this._queryId);
         Throwable var5 = null;

         try {
            connection.setAutoCommit(true);
            query.setString(1, id);
            ResultSet result = query.executeQuery();
            Throwable var7 = null;

            try {
               if (!result.next()) {
                  PreparedStatement statement = connection.prepareStatement(this._insertId);
                  Throwable var9 = null;

                  try {
                     statement.setString(1, id);
                     statement.executeUpdate();
                  } catch (Throwable var81) {
                     var9 = var81;
                     throw var81;
                  } finally {
                     if (statement != null) {
                        if (var9 != null) {
                           try {
                              statement.close();
                           } catch (Throwable var80) {
                              var9.addSuppressed(var80);
                           }
                        } else {
                           statement.close();
                        }
                     }

                  }
               }
            } catch (Throwable var83) {
               var7 = var83;
               throw var83;
            } finally {
               if (result != null) {
                  if (var7 != null) {
                     try {
                        result.close();
                     } catch (Throwable var79) {
                        var7.addSuppressed(var79);
                     }
                  } else {
                     result.close();
                  }
               }

            }
         } catch (Throwable var85) {
            var5 = var85;
            throw var85;
         } finally {
            if (query != null) {
               if (var5 != null) {
                  try {
                     query.close();
                  } catch (Throwable var78) {
                     var5.addSuppressed(var78);
                  }
               } else {
                  query.close();
               }
            }

         }
      } catch (Throwable var87) {
         var3 = var87;
         throw var87;
      } finally {
         if (connection != null) {
            if (var3 != null) {
               try {
                  connection.close();
               } catch (Throwable var77) {
                  var3.addSuppressed(var77);
               }
            } else {
               connection.close();
            }
         }

      }

   }

   private void delete(String id) throws SQLException {
      Connection connection = this.getConnection();
      Throwable var3 = null;

      try {
         PreparedStatement statement = connection.prepareStatement(this._deleteId);
         Throwable var5 = null;

         try {
            connection.setAutoCommit(true);
            statement.setString(1, id);
            statement.executeUpdate();
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

   private boolean exists(String id) throws SQLException {
      Connection connection = this.getConnection();
      Throwable var3 = null;

      Throwable var8;
      try {
         PreparedStatement statement = connection.prepareStatement(this._queryId);
         Throwable var5 = null;

         try {
            connection.setAutoCommit(true);
            statement.setString(1, id);
            ResultSet result = statement.executeQuery();
            Throwable var7 = null;

            try {
               var8 = result.next();
            } catch (Throwable var52) {
               var8 = var52;
               var7 = var52;
               throw var52;
            } finally {
               if (result != null) {
                  if (var7 != null) {
                     try {
                        result.close();
                     } catch (Throwable var51) {
                        var7.addSuppressed(var51);
                     }
                  } else {
                     result.close();
                  }
               }

            }
         } catch (Throwable var54) {
            var5 = var54;
            throw var54;
         } finally {
            if (statement != null) {
               if (var5 != null) {
                  try {
                     statement.close();
                  } catch (Throwable var50) {
                     var5.addSuppressed(var50);
                  }
               } else {
                  statement.close();
               }
            }

         }
      } catch (Throwable var56) {
         var3 = var56;
         throw var56;
      } finally {
         if (connection != null) {
            if (var3 != null) {
               try {
                  connection.close();
               } catch (Throwable var49) {
                  var3.addSuppressed(var49);
               }
            } else {
               connection.close();
            }
         }

      }

      return (boolean)var8;
   }

   private void scavenge() {
      Set<String> candidateIds = this.getAllCandidateExpiredSessionIds();
      Connection connection = null;

      try {
         if (LOG.isDebugEnabled()) {
            LOG.debug(this.getWorkerName() + "- Scavenge sweep started at " + System.currentTimeMillis());
         }

         if (this._lastScavengeTime > 0L) {
            connection = this.getConnection();
            connection.setAutoCommit(true);
            Set<String> expiredSessionIds = new HashSet();
            long lowerBound = this._lastScavengeTime - this._scavengeIntervalMs;
            long upperBound = this._lastScavengeTime;
            if (LOG.isDebugEnabled()) {
               LOG.debug(this.getWorkerName() + "- Pass 1: Searching for sessions expired between " + lowerBound + " and " + upperBound);
            }

            PreparedStatement statement = connection.prepareStatement(this._selectBoundedExpiredSessions);
            Throwable var9 = null;

            try {
               statement.setString(1, this.getWorkerName());
               statement.setLong(2, lowerBound);
               statement.setLong(3, upperBound);
               ResultSet result = statement.executeQuery();
               Throwable var11 = null;

               try {
                  while(result.next()) {
                     String sessionId = result.getString(this._sessionTableSchema.getIdColumn());
                     expiredSessionIds.add(sessionId);
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Found expired sessionId=" + sessionId);
                     }
                  }
               } catch (Throwable var167) {
                  var11 = var167;
                  throw var167;
               } finally {
                  if (result != null) {
                     if (var11 != null) {
                        try {
                           result.close();
                        } catch (Throwable var160) {
                           var11.addSuppressed(var160);
                        }
                     } else {
                        result.close();
                     }
                  }

               }
            } catch (Throwable var169) {
               var9 = var169;
               throw var169;
            } finally {
               if (statement != null) {
                  if (var9 != null) {
                     try {
                        statement.close();
                     } catch (Throwable var159) {
                        var9.addSuppressed(var159);
                     }
                  } else {
                     statement.close();
                  }
               }

            }

            this.scavengeSessions(candidateIds, expiredSessionIds, false);
            statement = connection.prepareStatement(this._selectExpiredSessions);
            var9 = null;

            try {
               expiredSessionIds.clear();
               upperBound = this._lastScavengeTime - 2L * this._scavengeIntervalMs;
               if (upperBound > 0L) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug(this.getWorkerName() + "- Pass 2: Searching for sessions expired before " + upperBound);
                  }

                  statement.setLong(1, upperBound);
                  ResultSet result = statement.executeQuery();
                  Throwable var179 = null;

                  try {
                     while(result.next()) {
                        String sessionId = result.getString(this._sessionTableSchema.getIdColumn());
                        String lastNode = result.getString(this._sessionTableSchema.getLastNodeColumn());
                        if (this.getWorkerName() == null && lastNode == null || this.getWorkerName() != null && this.getWorkerName().equals(lastNode)) {
                           expiredSessionIds.add(sessionId);
                        }

                        if (LOG.isDebugEnabled()) {
                           LOG.debug("Found expired sessionId=" + sessionId + " last managed by " + this.getWorkerName());
                        }
                     }
                  } catch (Throwable var161) {
                     var179 = var161;
                     throw var161;
                  } finally {
                     if (result != null) {
                        if (var179 != null) {
                           try {
                              result.close();
                           } catch (Throwable var158) {
                              var179.addSuppressed(var158);
                           }
                        } else {
                           result.close();
                        }
                     }

                  }

                  this.scavengeSessions(candidateIds, expiredSessionIds, false);
               }

               upperBound = this._lastScavengeTime - 3L * this._scavengeIntervalMs;
               expiredSessionIds.clear();
               if (upperBound > 0L) {
                  if (LOG.isDebugEnabled()) {
                     LOG.debug(this.getWorkerName() + "- Pass 3: searching for sessions expired before " + upperBound);
                  }

                  statement.setLong(1, upperBound);
                  ResultSet result = statement.executeQuery();
                  Throwable var180 = null;

                  try {
                     while(result.next()) {
                        String sessionId = result.getString(this._sessionTableSchema.getIdColumn());
                        expiredSessionIds.add(sessionId);
                        if (LOG.isDebugEnabled()) {
                           LOG.debug("Found expired sessionId=" + sessionId);
                        }
                     }
                  } catch (Throwable var163) {
                     var180 = var163;
                     throw var163;
                  } finally {
                     if (result != null) {
                        if (var180 != null) {
                           try {
                              result.close();
                           } catch (Throwable var157) {
                              var180.addSuppressed(var157);
                           }
                        } else {
                           result.close();
                        }
                     }

                  }

                  this.scavengeSessions(candidateIds, expiredSessionIds, true);
               }
            } catch (Throwable var165) {
               var9 = var165;
               throw var165;
            } finally {
               if (statement != null) {
                  if (var9 != null) {
                     try {
                        statement.close();
                     } catch (Throwable var156) {
                        var9.addSuppressed(var156);
                     }
                  } else {
                     statement.close();
                  }
               }

            }

            this.scavengeSessions(candidateIds);
         }
      } catch (Exception e) {
         if (this.isRunning()) {
            LOG.warn("Problem selecting expired sessions", (Throwable)e);
         } else {
            LOG.ignore(e);
         }
      } finally {
         this._lastScavengeTime = System.currentTimeMillis();
         if (LOG.isDebugEnabled()) {
            LOG.debug(this.getWorkerName() + "- Scavenge sweep ended at " + this._lastScavengeTime);
         }

         if (connection != null) {
            try {
               connection.close();
            } catch (SQLException e) {
               LOG.warn(e);
            }
         }

      }

   }

   private void scavengeSessions(Set<String> candidateIds, Set<String> expiredSessionIds, boolean forceDelete) {
      Set<String> remainingIds = new HashSet(expiredSessionIds);

      for(SessionManager m : this.getAllSessionManagers()) {
         Set<String> successfullyExpiredIds = ((JDBCSessionManager)m).expire(expiredSessionIds);
         if (successfullyExpiredIds != null) {
            remainingIds.removeAll(successfullyExpiredIds);
            candidateIds.removeAll(successfullyExpiredIds);
         }
      }

      if (!remainingIds.isEmpty() && forceDelete) {
         LOG.info("Forcibly deleting unrecoverable expired sessions {}", remainingIds);

         try {
            synchronized(this._sessionIds) {
               this._sessionIds.removeAll(remainingIds);
            }

            this.cleanExpiredSessionIds(remainingIds);
         } catch (Exception e) {
            LOG.warn("Error removing expired session ids", (Throwable)e);
         }
      }

   }

   private void scavengeSessions(Set<String> candidateIds) {
      if (!candidateIds.isEmpty()) {
         for(SessionManager m : this.getAllSessionManagers()) {
            ((JDBCSessionManager)m).expireCandidates(candidateIds);
         }

      }
   }

   private Set<String> getAllCandidateExpiredSessionIds() {
      HashSet<String> candidateIds = new HashSet();

      for(SessionManager m : this.getAllSessionManagers()) {
         candidateIds.addAll(((JDBCSessionManager)m).getCandidateExpiredIds());
      }

      return candidateIds;
   }

   private Set<SessionManager> getAllSessionManagers() {
      HashSet<SessionManager> managers = new HashSet();
      Handler[] contexts = this._server.getChildHandlersByClass(ContextHandler.class);

      for(int i = 0; contexts != null && i < contexts.length; ++i) {
         SessionHandler sessionHandler = (SessionHandler)((ContextHandler)contexts[i]).getChildHandlerByClass(SessionHandler.class);
         if (sessionHandler != null) {
            SessionManager manager = sessionHandler.getSessionManager();
            if (manager != null && manager instanceof JDBCSessionManager) {
               managers.add(manager);
            }
         }
      }

      return managers;
   }

   private void cleanExpiredSessionIds(Set<String> expiredIds) throws Exception {
      if (expiredIds != null && !expiredIds.isEmpty()) {
         String[] ids = (String[])expiredIds.toArray(new String[expiredIds.size()]);
         Connection con = this.getConnection();
         Throwable var4 = null;

         try {
            con.setTransactionIsolation(2);
            con.setAutoCommit(false);
            int start = 0;
            int end = 0;
            int blocksize = this._deleteBlockSize;
            int block = 0;

            try {
               Statement statement = con.createStatement();
               Throwable var10 = null;

               try {
                  while(end < ids.length) {
                     start = block * blocksize;
                     if (ids.length - start >= blocksize) {
                        end = start + blocksize;
                     } else {
                        end = ids.length;
                     }

                     statement.executeUpdate(this.fillInClause("delete from " + this._sessionIdTableSchema.getSchemaTableName() + " where " + this._sessionIdTableSchema.getIdColumn() + " in ", ids, start, end));
                     statement.executeUpdate(this.fillInClause("delete from " + this._sessionTableSchema.getSchemaTableName() + " where " + this._sessionTableSchema.getIdColumn() + " in ", ids, start, end));
                     ++block;
                  }
               } catch (Throwable var35) {
                  var10 = var35;
                  throw var35;
               } finally {
                  if (statement != null) {
                     if (var10 != null) {
                        try {
                           statement.close();
                        } catch (Throwable var34) {
                           var10.addSuppressed(var34);
                        }
                     } else {
                        statement.close();
                     }
                  }

               }
            } catch (Exception e) {
               con.rollback();
               throw e;
            }

            con.commit();
         } catch (Throwable var38) {
            var4 = var38;
            throw var38;
         } finally {
            if (con != null) {
               if (var4 != null) {
                  try {
                     con.close();
                  } catch (Throwable var33) {
                     var4.addSuppressed(var33);
                  }
               } else {
                  con.close();
               }
            }

         }

      }
   }

   private String fillInClause(String sql, String[] literals, int start, int end) throws Exception {
      StringBuffer buff = new StringBuffer();
      buff.append(sql);
      buff.append("(");

      for(int i = start; i < end; ++i) {
         buff.append("'" + literals[i] + "'");
         if (i + 1 < end) {
            buff.append(",");
         }
      }

      buff.append(")");
      return buff.toString();
   }

   private void initializeDatabase() throws Exception {
      if (this._datasource == null) {
         if (this._jndiName != null) {
            InitialContext ic = new InitialContext();
            this._datasource = (DataSource)ic.lookup(this._jndiName);
         } else if (this._driver != null && this._connectionUrl != null) {
            DriverManager.registerDriver(this._driver);
         } else {
            if (this._driverClassName == null || this._connectionUrl == null) {
               throw new IllegalStateException("No database configured for sessions");
            }

            Class.forName(this._driverClassName);
         }

      }
   }

   static {
      LOG = SessionHandler.LOG;
   }

   public static class SessionTableSchema {
      protected DatabaseAdaptor _dbAdaptor;
      protected String _tableName = "JettySessions";
      protected String _schemaName = null;
      protected String _rowIdColumn = "rowId";
      protected String _idColumn = "sessionId";
      protected String _contextPathColumn = "contextPath";
      protected String _virtualHostColumn = "virtualHost";
      protected String _lastNodeColumn = "lastNode";
      protected String _accessTimeColumn = "accessTime";
      protected String _lastAccessTimeColumn = "lastAccessTime";
      protected String _createTimeColumn = "createTime";
      protected String _cookieTimeColumn = "cookieTime";
      protected String _lastSavedTimeColumn = "lastSavedTime";
      protected String _expiryTimeColumn = "expiryTime";
      protected String _maxIntervalColumn = "maxInterval";
      protected String _mapColumn = "map";

      protected void setDatabaseAdaptor(DatabaseAdaptor dbadaptor) {
         this._dbAdaptor = dbadaptor;
      }

      public String getTableName() {
         return this._tableName;
      }

      public void setTableName(String tableName) {
         this.checkNotNull(tableName);
         this._tableName = tableName;
      }

      public String getSchemaName() {
         return this._schemaName;
      }

      public void setSchemaName(String schemaName) {
         this.checkNotNull(schemaName);
         this._schemaName = schemaName;
      }

      public String getRowIdColumn() {
         if ("rowId".equals(this._rowIdColumn) && this._dbAdaptor.isRowIdReserved()) {
            this._rowIdColumn = "srowId";
         }

         return this._rowIdColumn;
      }

      public void setRowIdColumn(String rowIdColumn) {
         this.checkNotNull(rowIdColumn);
         if (this._dbAdaptor == null) {
            throw new IllegalStateException("DbAdaptor is null");
         } else if (this._dbAdaptor.isRowIdReserved() && "rowId".equals(rowIdColumn)) {
            throw new IllegalArgumentException("rowId is reserved word for Oracle");
         } else {
            this._rowIdColumn = rowIdColumn;
         }
      }

      public String getIdColumn() {
         return this._idColumn;
      }

      public void setIdColumn(String idColumn) {
         this.checkNotNull(idColumn);
         this._idColumn = idColumn;
      }

      public String getContextPathColumn() {
         return this._contextPathColumn;
      }

      public void setContextPathColumn(String contextPathColumn) {
         this.checkNotNull(contextPathColumn);
         this._contextPathColumn = contextPathColumn;
      }

      public String getVirtualHostColumn() {
         return this._virtualHostColumn;
      }

      public void setVirtualHostColumn(String virtualHostColumn) {
         this.checkNotNull(virtualHostColumn);
         this._virtualHostColumn = virtualHostColumn;
      }

      public String getLastNodeColumn() {
         return this._lastNodeColumn;
      }

      public void setLastNodeColumn(String lastNodeColumn) {
         this.checkNotNull(lastNodeColumn);
         this._lastNodeColumn = lastNodeColumn;
      }

      public String getAccessTimeColumn() {
         return this._accessTimeColumn;
      }

      public void setAccessTimeColumn(String accessTimeColumn) {
         this.checkNotNull(accessTimeColumn);
         this._accessTimeColumn = accessTimeColumn;
      }

      public String getLastAccessTimeColumn() {
         return this._lastAccessTimeColumn;
      }

      public void setLastAccessTimeColumn(String lastAccessTimeColumn) {
         this.checkNotNull(lastAccessTimeColumn);
         this._lastAccessTimeColumn = lastAccessTimeColumn;
      }

      public String getCreateTimeColumn() {
         return this._createTimeColumn;
      }

      public void setCreateTimeColumn(String createTimeColumn) {
         this.checkNotNull(createTimeColumn);
         this._createTimeColumn = createTimeColumn;
      }

      public String getCookieTimeColumn() {
         return this._cookieTimeColumn;
      }

      public void setCookieTimeColumn(String cookieTimeColumn) {
         this.checkNotNull(cookieTimeColumn);
         this._cookieTimeColumn = cookieTimeColumn;
      }

      public String getLastSavedTimeColumn() {
         return this._lastSavedTimeColumn;
      }

      public void setLastSavedTimeColumn(String lastSavedTimeColumn) {
         this.checkNotNull(lastSavedTimeColumn);
         this._lastSavedTimeColumn = lastSavedTimeColumn;
      }

      public String getExpiryTimeColumn() {
         return this._expiryTimeColumn;
      }

      public void setExpiryTimeColumn(String expiryTimeColumn) {
         this.checkNotNull(expiryTimeColumn);
         this._expiryTimeColumn = expiryTimeColumn;
      }

      public String getMaxIntervalColumn() {
         return this._maxIntervalColumn;
      }

      public void setMaxIntervalColumn(String maxIntervalColumn) {
         this.checkNotNull(maxIntervalColumn);
         this._maxIntervalColumn = maxIntervalColumn;
      }

      public String getMapColumn() {
         return this._mapColumn;
      }

      public void setMapColumn(String mapColumn) {
         this.checkNotNull(mapColumn);
         this._mapColumn = mapColumn;
      }

      public String getCreateStatementAsString() {
         if (this._dbAdaptor == null) {
            throw new IllegalStateException("No DBAdaptor");
         } else {
            String blobType = this._dbAdaptor.getBlobType();
            String longType = this._dbAdaptor.getLongType();
            return "create table " + this.getSchemaTableName() + " (" + this.getRowIdColumn() + " varchar(120), " + this._idColumn + " varchar(120), " + this._contextPathColumn + " varchar(60), " + this._virtualHostColumn + " varchar(60), " + this._lastNodeColumn + " varchar(60), " + this._accessTimeColumn + " " + longType + ", " + this._lastAccessTimeColumn + " " + longType + ", " + this._createTimeColumn + " " + longType + ", " + this._cookieTimeColumn + " " + longType + ", " + this._lastSavedTimeColumn + " " + longType + ", " + this._expiryTimeColumn + " " + longType + ", " + this._maxIntervalColumn + " " + longType + ", " + this._mapColumn + " " + blobType + ", primary key(" + this.getRowIdColumn() + "))";
         }
      }

      public String getCreateIndexOverExpiryStatementAsString(String indexName) {
         return "create index " + indexName + " on " + this.getSchemaTableName() + " (" + this.getExpiryTimeColumn() + ")";
      }

      public String getCreateIndexOverSessionStatementAsString(String indexName) {
         return "create index " + indexName + " on " + this.getSchemaTableName() + " (" + this.getIdColumn() + ", " + this.getContextPathColumn() + ")";
      }

      public String getAlterTableForMaxIntervalAsString() {
         if (this._dbAdaptor == null) {
            throw new IllegalStateException("No DBAdaptor");
         } else {
            String longType = this._dbAdaptor.getLongType();
            String stem = "alter table " + this.getSchemaTableName() + " add " + this.getMaxIntervalColumn() + " " + longType;
            return this._dbAdaptor.getDBName().contains("oracle") ? stem + " default " + -999 + " not null" : stem + " not null default " + -999;
         }
      }

      private String getSchemaTableName() {
         return (this.getSchemaName() != null ? this.getSchemaName() + "." : "") + this.getTableName();
      }

      private void checkNotNull(String s) {
         if (s == null) {
            throw new IllegalArgumentException(s);
         }
      }

      public String getInsertSessionStatementAsString() {
         return "insert into " + this.getSchemaTableName() + " (" + this.getRowIdColumn() + ", " + this.getIdColumn() + ", " + this.getContextPathColumn() + ", " + this.getVirtualHostColumn() + ", " + this.getLastNodeColumn() + ", " + this.getAccessTimeColumn() + ", " + this.getLastAccessTimeColumn() + ", " + this.getCreateTimeColumn() + ", " + this.getCookieTimeColumn() + ", " + this.getLastSavedTimeColumn() + ", " + this.getExpiryTimeColumn() + ", " + this.getMaxIntervalColumn() + ", " + this.getMapColumn() + ")  values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
      }

      public String getDeleteSessionStatementAsString() {
         return "delete from " + this.getSchemaTableName() + " where " + this.getRowIdColumn() + " = ?";
      }

      public String getUpdateSessionStatementAsString() {
         return "update " + this.getSchemaTableName() + " set " + this.getIdColumn() + " = ?, " + this.getLastNodeColumn() + " = ?, " + this.getAccessTimeColumn() + " = ?, " + this.getLastAccessTimeColumn() + " = ?, " + this.getLastSavedTimeColumn() + " = ?, " + this.getExpiryTimeColumn() + " = ?, " + this.getMaxIntervalColumn() + " = ?, " + this.getMapColumn() + " = ? where " + this.getRowIdColumn() + " = ?";
      }

      public String getUpdateSessionNodeStatementAsString() {
         return "update " + this.getSchemaTableName() + " set " + this.getLastNodeColumn() + " = ? where " + this.getRowIdColumn() + " = ?";
      }

      public String getUpdateSessionAccessTimeStatementAsString() {
         return "update " + this.getSchemaTableName() + " set " + this.getLastNodeColumn() + " = ?, " + this.getAccessTimeColumn() + " = ?, " + this.getLastAccessTimeColumn() + " = ?, " + this.getLastSavedTimeColumn() + " = ?, " + this.getExpiryTimeColumn() + " = ?, " + this.getMaxIntervalColumn() + " = ? where " + this.getRowIdColumn() + " = ?";
      }

      public String getBoundedExpiredSessionsStatementAsString() {
         return "select * from " + this.getSchemaTableName() + " where " + this.getLastNodeColumn() + " = ? and " + this.getExpiryTimeColumn() + " >= ? and " + this.getExpiryTimeColumn() + " <= ?";
      }

      public String getSelectExpiredSessionsStatementAsString() {
         return "select * from " + this.getSchemaTableName() + " where " + this.getExpiryTimeColumn() + " >0 and " + this.getExpiryTimeColumn() + " <= ?";
      }

      public PreparedStatement getLoadStatement(Connection connection, String rowId, String contextPath, String virtualHosts) throws SQLException {
         if (this._dbAdaptor == null) {
            throw new IllegalStateException("No DB adaptor");
         } else if ((contextPath == null || "".equals(contextPath)) && this._dbAdaptor.isEmptyStringNull()) {
            PreparedStatement statement = connection.prepareStatement("select * from " + this.getSchemaTableName() + " where " + this.getIdColumn() + " = ? and " + this.getContextPathColumn() + " is null and " + this.getVirtualHostColumn() + " = ?");
            statement.setString(1, rowId);
            statement.setString(2, virtualHosts);
            return statement;
         } else {
            PreparedStatement statement = connection.prepareStatement("select * from " + this.getSchemaTableName() + " where " + this.getIdColumn() + " = ? and " + this.getContextPathColumn() + " = ? and " + this.getVirtualHostColumn() + " = ?");
            statement.setString(1, rowId);
            statement.setString(2, contextPath);
            statement.setString(3, virtualHosts);
            return statement;
         }
      }
   }

   public static class SessionIdTableSchema {
      protected DatabaseAdaptor _dbAdaptor;
      protected String _tableName = "JettySessionIds";
      protected String _schemaName = null;
      protected String _idColumn = "id";

      public void setDatabaseAdaptor(DatabaseAdaptor dbAdaptor) {
         this._dbAdaptor = dbAdaptor;
      }

      public String getIdColumn() {
         return this._idColumn;
      }

      public void setIdColumn(String idColumn) {
         this.checkNotNull(idColumn);
         this._idColumn = idColumn;
      }

      public String getTableName() {
         return this._tableName;
      }

      public void setTableName(String tableName) {
         this.checkNotNull(tableName);
         this._tableName = tableName;
      }

      public String getSchemaName() {
         return this._schemaName;
      }

      public void setSchemaName(String schemaName) {
         this.checkNotNull(schemaName);
         this._schemaName = schemaName;
      }

      public String getInsertStatementAsString() {
         return "insert into " + this.getSchemaTableName() + " (" + this._idColumn + ")  values (?)";
      }

      public String getDeleteStatementAsString() {
         return "delete from " + this.getSchemaTableName() + " where " + this._idColumn + " = ?";
      }

      public String getSelectStatementAsString() {
         return "select * from " + this.getSchemaTableName() + " where " + this._idColumn + " = ?";
      }

      public String getCreateStatementAsString() {
         return "create table " + this.getSchemaTableName() + " (" + this._idColumn + " varchar(120), primary key(" + this._idColumn + "))";
      }

      private String getSchemaTableName() {
         return (this.getSchemaName() != null ? this.getSchemaName() + "." : "") + this.getTableName();
      }

      private void checkNotNull(String s) {
         if (s == null) {
            throw new IllegalArgumentException(s);
         }
      }
   }

   public static class DatabaseAdaptor {
      String _dbName;
      boolean _isLower;
      boolean _isUpper;
      protected String _blobType;
      protected String _longType;

      public void adaptTo(DatabaseMetaData dbMeta) throws SQLException {
         this._dbName = dbMeta.getDatabaseProductName().toLowerCase(Locale.ENGLISH);
         if (JDBCSessionIdManager.LOG.isDebugEnabled()) {
            JDBCSessionIdManager.LOG.debug("Using database {}", this._dbName);
         }

         this._isLower = dbMeta.storesLowerCaseIdentifiers();
         this._isUpper = dbMeta.storesUpperCaseIdentifiers();
      }

      public void setBlobType(String blobType) {
         this._blobType = blobType;
      }

      public String getBlobType() {
         if (this._blobType != null) {
            return this._blobType;
         } else {
            return this._dbName.startsWith("postgres") ? "bytea" : "blob";
         }
      }

      public void setLongType(String longType) {
         this._longType = longType;
      }

      public String getLongType() {
         if (this._longType != null) {
            return this._longType;
         } else if (this._dbName == null) {
            throw new IllegalStateException("DbAdaptor missing metadata");
         } else {
            return this._dbName.startsWith("oracle") ? "number(20)" : "bigint";
         }
      }

      public String convertIdentifier(String identifier) {
         if (this._dbName == null) {
            throw new IllegalStateException("DbAdaptor missing metadata");
         } else if (this._isLower) {
            return identifier.toLowerCase(Locale.ENGLISH);
         } else {
            return this._isUpper ? identifier.toUpperCase(Locale.ENGLISH) : identifier;
         }
      }

      public String getDBName() {
         return this._dbName;
      }

      public InputStream getBlobInputStream(ResultSet result, String columnName) throws SQLException {
         if (this._dbName == null) {
            throw new IllegalStateException("DbAdaptor missing metadata");
         } else if (this._dbName.startsWith("postgres")) {
            byte[] bytes = result.getBytes(columnName);
            return new ByteArrayInputStream(bytes);
         } else {
            Blob blob = result.getBlob(columnName);
            return blob.getBinaryStream();
         }
      }

      public boolean isEmptyStringNull() {
         if (this._dbName == null) {
            throw new IllegalStateException("DbAdaptor missing metadata");
         } else {
            return this._dbName.startsWith("oracle");
         }
      }

      public boolean isRowIdReserved() {
         if (this._dbName == null) {
            throw new IllegalStateException("DbAdaptor missing metadata");
         } else {
            return this._dbName != null && this._dbName.startsWith("oracle");
         }
      }
   }

   protected class Scavenger implements Runnable {
      public void run() {
         try {
            JDBCSessionIdManager.this.scavenge();
         } finally {
            if (JDBCSessionIdManager.this._scheduler != null && JDBCSessionIdManager.this._scheduler.isRunning()) {
               JDBCSessionIdManager.this._task = JDBCSessionIdManager.this._scheduler.schedule(this, JDBCSessionIdManager.this._scavengeIntervalMs, TimeUnit.MILLISECONDS);
            }

         }

      }
   }
}
