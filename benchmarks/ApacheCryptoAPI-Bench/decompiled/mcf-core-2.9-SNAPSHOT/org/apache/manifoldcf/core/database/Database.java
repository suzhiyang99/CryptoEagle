package org.apache.manifoldcf.core.database;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import org.apache.manifoldcf.core.cachemanager.ExecutorBase;
import org.apache.manifoldcf.core.interfaces.BinaryInput;
import org.apache.manifoldcf.core.interfaces.CacheManagerFactory;
import org.apache.manifoldcf.core.interfaces.CharacterInput;
import org.apache.manifoldcf.core.interfaces.ClauseDescription;
import org.apache.manifoldcf.core.interfaces.ICacheDescription;
import org.apache.manifoldcf.core.interfaces.ICacheManager;
import org.apache.manifoldcf.core.interfaces.ILimitChecker;
import org.apache.manifoldcf.core.interfaces.IResultRow;
import org.apache.manifoldcf.core.interfaces.IResultSet;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.IndexDescription;
import org.apache.manifoldcf.core.interfaces.LockManagerFactory;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.PersistentDatabaseObject;
import org.apache.manifoldcf.core.interfaces.ResultSpecification;
import org.apache.manifoldcf.core.interfaces.StringSet;
import org.apache.manifoldcf.core.interfaces.TempFileCharacterInput;
import org.apache.manifoldcf.core.interfaces.TempFileInput;
import org.apache.manifoldcf.core.interfaces.TimeMarker;
import org.apache.manifoldcf.core.jdbcpool.WrappedConnection;
import org.apache.manifoldcf.core.system.Logging;
import org.apache.manifoldcf.core.system.ManifoldCF;

public abstract class Database {
   public static final String _rcsid = "@(#)$Id: Database.java 988245 2010-08-23 18:39:35Z kwright $";
   protected final ICacheManager cacheManager;
   protected final IThreadContext context;
   protected final String jdbcUrl;
   protected final String jdbcDriverClass;
   protected final String databaseName;
   protected String userName;
   protected String password;
   protected TransactionHandle th = null;
   protected WrappedConnection connection = null;
   protected boolean doRollback = false;
   protected boolean commitDone = false;
   protected int delayedTransactionDepth = 0;
   protected Map<String, Modifications> modificationsSet = new HashMap();
   protected final long maxQueryTime;
   protected final boolean debug;
   protected final int maxDBConnections;
   protected static Random random = new Random();
   protected static final String _TRANSACTION_ = "_TRANSACTION_";

   public Database(IThreadContext context, String jdbcUrl, String jdbcDriverClass, String databaseName, String userName, String password) throws ManifoldCFException {
      this.context = context;
      this.jdbcUrl = jdbcUrl;
      this.jdbcDriverClass = jdbcDriverClass;
      this.databaseName = databaseName;
      this.userName = userName;
      this.password = password;
      this.maxQueryTime = (long)LockManagerFactory.getIntProperty(context, "org.apache.manifoldcf.database.maxquerytime", 60) * 1000L;
      this.debug = LockManagerFactory.getBooleanProperty(context, "org.apache.manifoldcf.database.connectiontracking", false);
      this.maxDBConnections = LockManagerFactory.getIntProperty(context, "org.apache.manifoldcf.database.maxhandles", 50);
      this.cacheManager = CacheManagerFactory.make(context);
   }

   public String getDatabaseName() {
      return this.databaseName;
   }

   public String getTransactionID() {
      return this.th == null ? null : this.th.getTransactionID();
   }

   protected void startATransaction() throws ManifoldCFException {
   }

   protected void commitCurrentTransaction() throws ManifoldCFException {
   }

   protected void rollbackCurrentTransaction() throws ManifoldCFException {
   }

   protected void explainQuery(String query, List params) throws ManifoldCFException {
   }

   protected String mapLookupName(String rawColumnName, String rawLabelName) {
      return rawColumnName;
   }

   protected String mapLabelName(String rawLabelName) {
      return rawLabelName;
   }

   public void prepareForDatabaseCreate() throws ManifoldCFException {
      if (this.connection != null) {
         throw new ManifoldCFException("Can't do a database create within a transaction");
      } else {
         ConnectionFactory.flush();
      }
   }

   public IResultSet executeQuery(String query, List params, StringSet cacheKeys, StringSet invalidateKeys, String queryClass, boolean needResult, int maxReturn, ResultSpecification spec, ILimitChecker returnLimits) throws ManifoldCFException {
      if (this.commitDone) {
         throw new ManifoldCFException("Commit already done");
      } else {
         if (Logging.db.isDebugEnabled()) {
            Logging.db.debug("Requested query: [" + query + "]");
         }

         if (!needResult) {
            cacheKeys = null;
         }

         QueryDescription[] queryDescriptions = new QueryDescription[1];
         queryDescriptions[0] = new QueryDescription(this.databaseName, query, params, queryClass, cacheKeys, maxReturn, spec, returnLimits);
         QueryCacheExecutor executor = new QueryCacheExecutor(this, needResult);
         this.cacheManager.findObjectsAndExecute(queryDescriptions, invalidateKeys, executor, this.getTransactionID());
         return executor.getResult();
      }
   }

   public int getCurrentTransactionType() {
      return this.th == null ? 1 : this.th.getTransactionType();
   }

   public void beginTransaction(int transactionType) throws ManifoldCFException {
      if (Logging.db.isDebugEnabled()) {
         Logging.db.debug("Beginning transaction of type " + Integer.toString(transactionType));
      }

      String enclosingID = this.th == null ? null : this.th.getTransactionID();
      ++this.delayedTransactionDepth;
      this.th = new TransactionHandle(this.context, this.th, transactionType);
      this.cacheManager.startTransaction(this.th.getTransactionID(), enclosingID);
      this.doRollback = false;
      this.commitDone = false;
   }

   protected void synchronizeTransactions() throws ManifoldCFException {
      while(this.delayedTransactionDepth > 0) {
         this.internalTransactionBegin();
         --this.delayedTransactionDepth;
      }

   }

   protected void internalTransactionBegin() throws ManifoldCFException {
      if (this.connection == null) {
         this.connection = ConnectionFactory.getConnection(this.jdbcUrl, this.jdbcDriverClass, this.databaseName, this.userName, this.password, this.maxDBConnections, this.debug);

         try {
            this.initializeConnection(this.connection.getConnection());
            this.startATransaction();
         } catch (ManifoldCFException e) {
            if (e.getErrorCode() == 2) {
               this.connection = null;
               throw e;
            }

            ConnectionFactory.releaseConnection(this.connection);
            this.connection = null;
            throw e;
         } catch (Error e) {
            ConnectionFactory.releaseConnection(this.connection);
            this.connection = null;
            throw e;
         }
      } else {
         try {
            this.startATransaction();
         } catch (ManifoldCFException e) {
            if (e.getErrorCode() == 2) {
               this.connection = null;
            }

            throw e;
         }
      }

   }

   public void performCommit() throws ManifoldCFException {
      if (!this.doRollback) {
         if (this.delayedTransactionDepth == 0) {
            Logging.db.debug("Committing transaction!");
            this.commitCurrentTransaction();
            this.commitDone = true;
         }

      }
   }

   public void signalRollback() {
      this.doRollback = true;
   }

   public void endTransaction() throws ManifoldCFException {
      Logging.db.debug("Ending transaction");
      if (this.th == null) {
         throw new ManifoldCFException("End transaction without begin!", 0);
      } else {
         TransactionHandle parentTransaction = this.th.getParent();

         try {
            if (this.delayedTransactionDepth > 0) {
               --this.delayedTransactionDepth;
            } else {
               try {
                  if (this.doRollback) {
                     if (this.commitDone) {
                        this.doRollback = false;
                        throw new ManifoldCFException("Cannot roll back an already committed transaction");
                     }

                     Logging.db.debug("Rolling transaction back!");
                     this.rollbackCurrentTransaction();
                  } else if (!this.commitDone) {
                     Logging.db.debug("Committing transaction!");
                     this.commitCurrentTransaction();
                  }
               } catch (ManifoldCFException e) {
                  if (e.getErrorCode() == 2) {
                     this.connection = null;
                  }

                  throw e;
               } finally {
                  if (parentTransaction == null && this.connection != null) {
                     ConnectionFactory.releaseConnection(this.connection);
                     this.connection = null;
                  }

               }
            }
         } finally {
            if (this.doRollback) {
               this.cacheManager.rollbackTransaction(this.th.getTransactionID());
            } else {
               this.cacheManager.commitTransaction(this.th.getTransactionID());
            }

            this.commitDone = false;
            this.doRollback = false;
            this.th = parentTransaction;
            if (this.th == null) {
               if (this.doRollback) {
                  this.modificationsSet.clear();
               } else {
                  this.playbackModifications();
               }
            }

         }

      }
   }

   private void playbackModifications() throws ManifoldCFException {
      for(String tableName : this.modificationsSet.keySet()) {
         Modifications c = (Modifications)this.modificationsSet.get(tableName);
         this.noteModificationsNoTransactions(tableName, c.getInsertCount(), c.getModifyCount(), c.getDeleteCount());
      }

      this.modificationsSet.clear();
   }

   public void noteModifications(String tableName, int insertCount, int modifyCount, int deleteCount) throws ManifoldCFException {
      if (this.th != null) {
         Modifications c = (Modifications)this.modificationsSet.get(tableName);
         if (c == null) {
            c = new Modifications();
            this.modificationsSet.put(tableName, c);
         }

         c.update(insertCount, modifyCount, deleteCount);
      } else {
         this.noteModificationsNoTransactions(tableName, insertCount, modifyCount, deleteCount);
      }

   }

   protected void noteModificationsNoTransactions(String tableName, int insertCount, int modifyCount, int deleteCount) throws ManifoldCFException {
   }

   public long getSleepAmt() {
      return (long)(random.nextDouble() * (double)60000.0F + (double)500.0F);
   }

   public void sleepFor(long amt) throws ManifoldCFException {
      if (amt != 0L) {
         try {
            ManifoldCF.sleep(amt);
         } catch (InterruptedException e) {
            throw new ManifoldCFException("Interrupted", e, 2);
         }
      }
   }

   public String constructIndexHintClause(String tableName, IndexDescription description) throws ManifoldCFException {
      return "";
   }

   public String constructIndexOrderByClause(String[] fieldNames, boolean direction) {
      if (fieldNames.length == 0) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder("ORDER BY ");
         sb.append(fieldNames[0]);
         if (direction) {
            sb.append(" ASC");
         } else {
            sb.append(" DESC");
         }

         return sb.toString();
      }
   }

   public String constructOffsetLimitClause(int offset, int limit) {
      return this.constructOffsetLimitClause(offset, limit, false);
   }

   public abstract String constructOffsetLimitClause(int var1, int var2, boolean var3);

   public int findConjunctionClauseMax(ClauseDescription[] otherClauseDescriptions) {
      return this.getMaxInClause();
   }

   public abstract int getMaxInClause();

   public String buildConjunctionClause(List outputParameters, ClauseDescription[] clauseDescriptions) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < clauseDescriptions.length; ++i) {
         ClauseDescription cd = clauseDescriptions[i];
         if (i > 0) {
            sb.append(" AND ");
         }

         sb.append(cd.getColumnName());
         String operation = cd.getOperation();
         List values = cd.getValues();
         String joinColumn = cd.getJoinColumnName();
         if (values == null) {
            if (joinColumn != null) {
               sb.append(operation).append(joinColumn);
            } else {
               sb.append(operation);
            }
         } else if (values.size() <= 1) {
            sb.append(operation).append("?");
            outputParameters.add(values.get(0));
         } else {
            sb.append(" IN (");

            for(int j = 0; j < values.size(); ++j) {
               if (j > 0) {
                  sb.append(",");
               }

               sb.append("?");
               outputParameters.add(values.get(j));
            }

            sb.append(")");
         }
      }

      return sb.toString();
   }

   protected IResultSet executeViaThread(Connection connection, String query, List params, boolean bResults, int maxResults, ResultSpecification spec, ILimitChecker returnLimit) throws ManifoldCFException {
      if (connection == null) {
         return null;
      } else {
         ExecuteQueryThread t = new ExecuteQueryThread(connection, query, params, bResults, maxResults, spec, returnLimit);

         try {
            t.start();
            return t.finishUp();
         } catch (InterruptedException e) {
            t.interrupt();
            this.interruptCleanup(connection);
            throw new ManifoldCFException(e.getMessage(), e, 2);
         }
      }
   }

   protected void interruptCleanup(Connection connection) {
      try {
         if (!connection.getAutoCommit()) {
            connection.rollback();
         }

         connection.close();
      } catch (Exception var3) {
      }

   }

   protected IResultSet executeUncachedQuery(String query, List params, boolean bResults, int maxResults, ResultSpecification spec, ILimitChecker returnLimit) throws ManifoldCFException {
      if (this.connection != null) {
         try {
            return this.executeViaThread(this.connection.getConnection(), query, params, bResults, maxResults, spec, returnLimit);
         } catch (ManifoldCFException e) {
            if (e.getErrorCode() == 2) {
               this.connection = null;
            }

            throw e;
         }
      } else {
         WrappedConnection tempConnection = ConnectionFactory.getConnection(this.jdbcUrl, this.jdbcDriverClass, this.databaseName, this.userName, this.password, this.maxDBConnections, this.debug);

         IResultSet var8;
         try {
            this.initializeConnection(tempConnection.getConnection());
            var8 = this.executeViaThread(tempConnection.getConnection(), query, params, bResults, maxResults, spec, returnLimit);
         } catch (ManifoldCFException e) {
            if (e.getErrorCode() == 2) {
               tempConnection = null;
            }

            throw e;
         } finally {
            if (tempConnection != null) {
               ConnectionFactory.releaseConnection(tempConnection);
            }

         }

         return var8;
      }
   }

   protected void initializeConnection(Connection connection) throws ManifoldCFException {
   }

   protected IResultSet execute(Connection connection, String query, List params, boolean bResults, int maxResults, ResultSpecification spec, ILimitChecker returnLimit) throws ManifoldCFException {
      IResultSet rval = null;

      try {
         long queryStartTime = 0L;
         if (Logging.db.isDebugEnabled()) {
            queryStartTime = System.currentTimeMillis();
            Logging.db.debug("Actual query: [" + query + "]");
            if (params != null) {
               for(int i = 0; i < params.size(); ++i) {
                  Logging.db.debug("  Parameter " + i + ": '" + params.get(i).toString() + "'");
               }
            }
         }

         if (params == null) {
            Statement stmt = connection.createStatement();

            try {
               stmt.execute(query);
               ResultSet rs = stmt.getResultSet();

               try {
                  rval = this.getData(rs, bResults, maxResults, spec, returnLimit);
               } finally {
                  if (rs != null) {
                     rs.close();
                  }

               }
            } finally {
               stmt.close();
            }
         } else {
            PreparedStatement ps = connection.prepareStatement(query);

            try {
               loadPS(ps, params);
               if (bResults) {
                  ResultSet rs = ps.executeQuery();

                  try {
                     rval = this.getData(rs, true, maxResults, spec, returnLimit);
                  } finally {
                     if (rs != null) {
                        rs.close();
                     }

                  }
               } else {
                  ps.executeUpdate();
                  rval = this.getData((ResultSet)null, false, 0, spec, (ILimitChecker)null);
               }
            } finally {
               ps.close();
            }
         }

         if (Logging.db.isDebugEnabled()) {
            Logging.db.debug("Done actual query (" + (new Long(System.currentTimeMillis() - queryStartTime)).toString() + "ms): [" + query + "]");
         }
      } catch (SQLException var52) {
         throw new ManifoldCFException("SQLException doing query" + (var52.getSQLState() != null ? " (" + var52.getSQLState() + ")" : "") + ": " + var52.getMessage(), var52, 4);
      } finally {
         if (params != null) {
            cleanupParameters(params);
         }

      }

      return rval;
   }

   protected IResultSet getData(ResultSet rs, boolean bResults, int maxResults, ResultSpecification spec, ILimitChecker returnLimit) throws ManifoldCFException {
      RSet results = new RSet();

      try {
         try {
            if (rs != null) {
               int colcount = 0;
               String[] resultCols = null;
               String[] resultLabels = null;
               ResultSetMetaData rsmd = rs.getMetaData();
               if (rsmd != null) {
                  colcount = rsmd.getColumnCount();
                  resultCols = new String[colcount];
                  resultLabels = new String[colcount];

                  for(int i = 0; i < colcount; ++i) {
                     String labelName = rsmd.getColumnLabel(i + 1);
                     resultCols[i] = this.mapLookupName(rsmd.getColumnName(i + 1), labelName);
                     resultLabels[i] = this.mapLabelName(labelName);
                  }
               }

               if (bResults) {
                  if (colcount == 0) {
                     throw new ManifoldCFException("Empty query, no columns returned", 0);
                  }

                  while(rs.next() && (maxResults == -1 || maxResults > 0) && (returnLimit == null || returnLimit.checkContinue())) {
                     RRow m = new RRow();

                     for(int i = 0; i < colcount; ++i) {
                        String key = resultCols[i];
                        int colnum = this.findColumn(rs, key);
                        Object value = null;
                        if (colnum > -1) {
                           value = this.getObject(rs, rsmd, colnum, spec == null ? 0 : spec.getForm(key.toLowerCase(Locale.ROOT)));
                        }

                        m.put(resultLabels[i], value);
                     }

                     boolean include = true;
                     if (returnLimit != null) {
                        include = returnLimit.checkInclude(m);
                     }

                     if (include) {
                        if (maxResults != -1) {
                           --maxResults;
                        }

                        results.addRow(m);
                     } else {
                        Iterator iter = m.getColumns();

                        while(iter.hasNext()) {
                           String columnName = (String)iter.next();
                           Object colValue = m.getValue(columnName);
                           if (colValue instanceof PersistentDatabaseObject) {
                              ((PersistentDatabaseObject)colValue).discard();
                           }
                        }
                     }
                  }
               }
            }

            return results;
         } catch (SQLException var17) {
            throw new ManifoldCFException("SQLException getting resultset" + (var17.getSQLState() != null ? " (" + var17.getSQLState() + ")" : "") + ": " + var17.getMessage(), var17, 4);
         }
      } catch (Throwable e) {
         int i = 0;

         while(i < results.getRowCount()) {
            IResultRow row = results.getRow(i++);
            int j = 0;
            Iterator iter = row.getColumns();

            while(iter.hasNext()) {
               String colName = (String)iter.next();
               Object o = row.getValue(colName);
               if (o instanceof PersistentDatabaseObject) {
                  ((PersistentDatabaseObject)o).discard();
               }
            }
         }

         if (e instanceof ManifoldCFException) {
            throw (ManifoldCFException)e;
         } else if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
         } else if (e instanceof Error) {
            throw (Error)e;
         } else {
            throw new Error("Unexpected exception caught: " + e.getMessage(), e);
         }
      }
   }

   protected static void loadPS(PreparedStatement ps, List data) throws SQLException, ManifoldCFException {
      if (data != null) {
         for(int i = 0; i < data.size(); ++i) {
            Object x = data.get(i);
            if (x instanceof String) {
               String value = (String)x;
               ps.setString(i + 1, value);
            }

            if (x instanceof BinaryInput) {
               BinaryInput value = (BinaryInput)x;
               long length = value.getLength();
               ps.setBinaryStream(i + 1, value.getStream(), length == -1L ? Integer.MAX_VALUE : (int)length);
            }

            if (x instanceof CharacterInput) {
               CharacterInput value = (CharacterInput)x;
               long length = value.getCharacterLength();
               ps.setCharacterStream(i + 1, value.getStream(), length == -1L ? Integer.MAX_VALUE : (int)length);
            }

            if (x instanceof Date) {
               ps.setDate(i + 1, new java.sql.Date(((Date)x).getTime()));
            }

            if (x instanceof Long) {
               ps.setLong(i + 1, (Long)x);
            }

            if (x instanceof TimeMarker) {
               ps.setTimestamp(i + 1, new Timestamp(((TimeMarker)x).longValue()));
            }

            if (x instanceof Double) {
               ps.setDouble(i + 1, (Double)x);
            }

            if (x instanceof Integer) {
               ps.setInt(i + 1, (Integer)x);
            }

            if (x instanceof Float) {
               ps.setFloat(i + 1, (Float)x);
            }
         }
      }

   }

   protected static void cleanupParameters(List data) throws ManifoldCFException {
      if (data != null) {
         for(Object x : data) {
            if (x instanceof PersistentDatabaseObject) {
               ((PersistentDatabaseObject)x).doneWithStream();
            }
         }
      }

   }

   protected int findColumn(ResultSet rs, String name) throws ManifoldCFException {
      try {
         return rs.findColumn(name);
      } catch (SQLException var4) {
         return -1;
      } catch (Exception e) {
         throw new ManifoldCFException("Error finding " + name + " in resultset: " + e.getMessage(), e, 1);
      }
   }

   protected Blob getBLOB(ResultSet rs, int col) throws ManifoldCFException {
      try {
         return rs.getBlob(col);
      } catch (SQLException var4) {
         throw new ManifoldCFException("SQLException in getBlob" + (var4.getSQLState() != null ? " (" + var4.getSQLState() + ")" : "") + ": " + var4.getMessage(), var4, 4);
      } catch (Exception sqle) {
         throw new ManifoldCFException("Error in getBlob", sqle, 1);
      }
   }

   protected boolean isBLOB(ResultSetMetaData rsmd, int col) throws ManifoldCFException {
      try {
         int type = rsmd.getColumnType(col);
         return type == 2004;
      } catch (SQLException var4) {
         throw new ManifoldCFException("SQLException doing isBlob(" + col + ")" + (var4.getSQLState() != null ? " (" + var4.getSQLState() + ")" : "") + ": " + var4.getMessage(), var4, 4);
      } catch (Exception sqle) {
         throw new ManifoldCFException("Error in isBlob(" + col + "): " + sqle.getMessage(), sqle, 1);
      }
   }

   protected boolean isBinary(ResultSetMetaData rsmd, int col) throws ManifoldCFException {
      try {
         int type = rsmd.getColumnType(col);
         return type == -3 || type == -2 || type == -4;
      } catch (SQLException var4) {
         throw new ManifoldCFException("SQLException doing isBinary(" + col + ")" + (var4.getSQLState() != null ? " (" + var4.getSQLState() + ")" : "") + ": " + var4.getMessage(), var4, 4);
      } catch (Exception sqle) {
         throw new ManifoldCFException("Error in isBinary(" + col + "): " + sqle.getMessage(), sqle, 1);
      }
   }

   protected Object getObject(ResultSet rs, ResultSetMetaData rsmd, int col, int desiredForm) throws ManifoldCFException {
      Object result = null;

      try {
         try {
            if (this.isBLOB(rsmd, col)) {
               Blob blob = this.getBLOB(rs, col);
               if (blob != null) {
                  result = new TempFileInput(blob.getBinaryStream(), blob.length());
               }
            } else if (this.isBinary(rsmd, col)) {
               InputStream is = rs.getBinaryStream(col);
               if (is != null) {
                  result = new TempFileInput(is);
               }
            } else {
               int colType = rsmd.getColumnType(col);
               label92:
               switch (colType) {
                  case -5:
                     long l = rs.getLong(col);
                     if (!rs.wasNull()) {
                        result = new Long(l);
                     }
                     break;
                  case 1:
                  case 12:
                     switch (desiredForm) {
                        case 0:
                        case 1:
                           String resultString;
                           if ((resultString = rs.getString(col)) != null) {
                              result = resultString;
                           }
                           break label92;
                        case 2:
                           result = new TempFileCharacterInput(rs.getCharacterStream(col));
                           break label92;
                        default:
                           throw new ManifoldCFException("Illegal form requested for column " + Integer.toString(col) + ": " + Integer.toString(desiredForm));
                     }
                  case 4:
                     int i = rs.getInt(col);
                     if (!rs.wasNull()) {
                        result = new Integer(i);
                     }
                     break;
                  case 5:
                     short s = rs.getShort(col);
                     if (!rs.wasNull()) {
                        result = new Short(s);
                     }
                     break;
                  case 6:
                  case 7:
                     float f = rs.getFloat(col);
                     if (!rs.wasNull()) {
                        result = new Float(f);
                     }
                     break;
                  case 8:
                     double d = rs.getDouble(col);
                     if (!rs.wasNull()) {
                        result = new Double(d);
                     }
                     break;
                  case 16:
                     boolean b = rs.getBoolean(col);
                     if (!rs.wasNull()) {
                        result = new Boolean(b);
                     }
                     break;
                  case 91:
                     java.sql.Date date;
                     if ((date = rs.getDate(col)) != null) {
                        result = new Date(date.getTime());
                     }
                     break;
                  case 93:
                     Timestamp timestamp;
                     if ((timestamp = rs.getTimestamp(col)) != null) {
                        result = new TimeMarker(timestamp.getTime());
                     }
                     break;
                  case 2004:
                     throw new ManifoldCFException("BLOB is not a string, column = " + col, 0);
                  case 2005:
                     switch (desiredForm) {
                        case 0:
                        case 1:
                           Clob clob;
                           if ((clob = rs.getClob(col)) != null) {
                              result = clob.getSubString(1L, (int)clob.length());
                           }
                           break label92;
                        case 2:
                           result = new TempFileCharacterInput(rs.getCharacterStream(col));
                           break label92;
                        default:
                           throw new ManifoldCFException("Illegal form requested for column " + Integer.toString(col) + ": " + Integer.toString(desiredForm));
                     }
                  default:
                     switch (desiredForm) {
                        case 0:
                        case 1:
                           result = rs.getString(col);
                           break;
                        case 2:
                           result = new TempFileCharacterInput(rs.getCharacterStream(col));
                           break;
                        default:
                           throw new ManifoldCFException("Illegal form requested for column " + Integer.toString(col) + ": " + Integer.toString(desiredForm));
                     }
               }

               if (rs.wasNull()) {
                  if (result instanceof CharacterInput) {
                     ((CharacterInput)result).discard();
                  }

                  result = null;
               }
            }

            return result;
         } catch (SQLException var19) {
            throw new ManifoldCFException("SQLException doing getObject()" + (var19.getSQLState() != null ? " (" + var19.getSQLState() + ")" : "") + ": " + var19.getMessage(), var19, 4);
         }
      } catch (Throwable e) {
         if (result instanceof PersistentDatabaseObject) {
            ((PersistentDatabaseObject)result).discard();
         }

         if (e instanceof ManifoldCFException) {
            throw (ManifoldCFException)e;
         } else if (e instanceof RuntimeException) {
            throw (RuntimeException)e;
         } else if (e instanceof Error) {
            throw (Error)e;
         } else {
            throw new Error("Unexpected exception caught: " + e.getMessage(), e);
         }
      }
   }

   protected static class Modifications {
      protected int insertCount = 0;
      protected int modifyCount = 0;
      protected int deleteCount = 0;

      public Modifications() {
      }

      public void update(int insertCount, int modifyCount, int deleteCount) {
         this.insertCount += insertCount;
         this.modifyCount += modifyCount;
         this.deleteCount += deleteCount;
      }

      public int getInsertCount() {
         return this.insertCount;
      }

      public int getModifyCount() {
         return this.modifyCount;
      }

      public int getDeleteCount() {
         return this.deleteCount;
      }
   }

   protected class ExecuteQueryThread extends Thread {
      protected Connection connection;
      protected String query;
      protected List params;
      protected boolean bResults;
      protected int maxResults;
      protected ResultSpecification spec;
      protected ILimitChecker returnLimit;
      protected Throwable exception = null;
      protected IResultSet rval = null;

      public ExecuteQueryThread(Connection connection, String query, List params, boolean bResults, int maxResults, ResultSpecification spec, ILimitChecker returnLimit) {
         this.setDaemon(true);
         this.connection = connection;
         this.query = query;
         this.params = params;
         this.bResults = bResults;
         this.maxResults = maxResults;
         this.spec = spec;
         this.returnLimit = returnLimit;
      }

      public void run() {
         try {
            this.rval = Database.this.execute(this.connection, this.query, this.params, this.bResults, this.maxResults, this.spec, this.returnLimit);
         } catch (Throwable e) {
            this.exception = e;
         }

      }

      public IResultSet finishUp() throws ManifoldCFException, InterruptedException {
         this.join();
         Throwable thr = this.exception;
         if (thr != null) {
            if (thr instanceof ManifoldCFException) {
               ManifoldCFException me = (ManifoldCFException)thr;
               throw new ManifoldCFException("Database exception: " + me.getMessage(), me.getCause(), me.getErrorCode());
            } else if (thr instanceof Error) {
               throw (Error)thr;
            } else if (thr instanceof RuntimeException) {
               throw (RuntimeException)thr;
            } else {
               throw new RuntimeException("Unknown exception: " + thr.getClass().getName() + ": " + thr.getMessage(), thr);
            }
         } else {
            return this.rval;
         }
      }
   }

   public static class QueryCacheExecutor extends ExecutorBase {
      protected Database database;
      protected boolean needResult;
      protected IResultSet resultset = null;

      public QueryCacheExecutor(Database database, boolean needResult) {
         this.database = database;
         this.needResult = needResult;
      }

      public IResultSet getResult() {
         return this.resultset;
      }

      public Object[] create(ICacheDescription[] objectDescriptions) throws ManifoldCFException {
         Object[] rval = new Object[objectDescriptions.length];

         for(int i = 0; i < objectDescriptions.length; ++i) {
            this.database.synchronizeTransactions();
            QueryDescription description = (QueryDescription)objectDescriptions[i];
            ILimitChecker limit = description.getReturnLimit();
            ResultSpecification spec = description.getResultSpecification();
            long startTime = System.currentTimeMillis();
            rval[i] = this.database.executeUncachedQuery(description.getQuery(), description.getParameters(), this.needResult, description.getMaxReturn(), spec, limit);
            long endTime = System.currentTimeMillis();
            if (endTime - startTime > this.database.maxQueryTime && description.getQuery().length() >= 6 && ("SELECT".equalsIgnoreCase(description.getQuery().substring(0, 6)) || "UPDATE".equalsIgnoreCase(description.getQuery().substring(0, 6)))) {
               Logging.db.warn("Found a long-running query (" + (new Long(endTime - startTime)).toString() + " ms): [" + description.getQuery() + "]");
               if (description.getParameters() != null) {
                  for(int j = 0; j < description.getParameters().size(); ++j) {
                     Logging.db.warn("  Parameter " + j + ": '" + description.getParameters().get(j).toString() + "'");
                  }
               }

               try {
                  this.database.explainQuery(description.getQuery(), description.getParameters());
               } catch (ManifoldCFException var12) {
                  if (var12.getErrorCode() == 6 || var12.getErrorCode() == 2) {
                     throw var12;
                  }

                  Logging.db.warn("Explain failed with error " + var12.getMessage(), var12);
               }
            }
         }

         return rval;
      }

      public void exists(ICacheDescription objectDescription, Object cachedObject) throws ManifoldCFException {
         this.resultset = (IResultSet)cachedObject;
      }

      public void execute() throws ManifoldCFException {
      }
   }
}
