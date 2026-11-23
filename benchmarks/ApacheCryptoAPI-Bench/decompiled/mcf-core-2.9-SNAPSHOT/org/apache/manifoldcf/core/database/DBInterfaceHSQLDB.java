package org.apache.manifoldcf.core.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.manifoldcf.core.interfaces.CacheKeyFactory;
import org.apache.manifoldcf.core.interfaces.ClauseDescription;
import org.apache.manifoldcf.core.interfaces.ColumnDescription;
import org.apache.manifoldcf.core.interfaces.IDBInterface;
import org.apache.manifoldcf.core.interfaces.IDFactory;
import org.apache.manifoldcf.core.interfaces.ILimitChecker;
import org.apache.manifoldcf.core.interfaces.IResultRow;
import org.apache.manifoldcf.core.interfaces.IResultSet;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.IndexDescription;
import org.apache.manifoldcf.core.interfaces.LockManagerFactory;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.ResultSpecification;
import org.apache.manifoldcf.core.interfaces.StringSet;
import org.apache.manifoldcf.core.interfaces.StringSetBuffer;
import org.apache.manifoldcf.core.system.Logging;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class DBInterfaceHSQLDB extends Database implements IDBInterface {
   public static final String _rcsid = "@(#)$Id$";
   private static final String _localUrl = "jdbc:hsqldb:file:";
   private static final String _remoteUrl = "jdbc:hsqldb:";
   private static final String _driver = "org.hsqldb.jdbcDriver";
   private static Map<String, String> legalProtocolValues = new HashMap();
   public static final String databasePathProperty = "org.apache.manifoldcf.hsqldbdatabasepath";
   public static final String databaseProtocolProperty = "org.apache.manifoldcf.hsqldbdatabaseprotocol";
   public static final String databaseServerProperty = "org.apache.manifoldcf.hsqldbdatabaseserver";
   public static final String databasePortProperty = "org.apache.manifoldcf.hsqldbdatabaseport";
   public static final String databaseInstanceProperty = "org.apache.manifoldcf.hsqldbdatabaseinstance";
   protected String cacheKey;
   protected int serializableDepth = 0;
   protected boolean isRemote;
   protected String schemaNameForQueries;
   protected int depthCount = 0;
   protected boolean inTransaction = false;
   protected int desiredTransactionType = 2;

   public DBInterfaceHSQLDB(IThreadContext tc, String databaseName, String userName, String password) throws ManifoldCFException {
      super(tc, getJDBCString(tc, databaseName), "org.hsqldb.jdbcDriver", getDatabaseString(tc, databaseName), userName, password);
      this.cacheKey = CacheKeyFactory.makeDatabaseKey(this.databaseName);
      this.isRemote = LockManagerFactory.getProperty(tc, "org.apache.manifoldcf.hsqldbdatabaseprotocol") != null;
      this.userName = userName;
      this.password = password;
      if (this.isRemote) {
         this.schemaNameForQueries = databaseName;
      } else {
         this.schemaNameForQueries = "PUBLIC";
      }

   }

   protected static String getJDBCString(IThreadContext tc, String databaseName) throws ManifoldCFException {
      String protocol = LockManagerFactory.getProperty(tc, "org.apache.manifoldcf.hsqldbdatabaseprotocol");
      if (protocol == null) {
         return "jdbc:hsqldb:file:" + getFullDatabasePath(databaseName);
      } else if (legalProtocolValues.get(protocol) == null) {
         throw new ManifoldCFException("The value of the 'org.apache.manifoldcf.hsqldbdatabaseprotocol' property was illegal; try hsql, http, or https");
      } else {
         String server = LockManagerFactory.getProperty(tc, "org.apache.manifoldcf.hsqldbdatabaseserver");
         if (server == null) {
            throw new ManifoldCFException("HSQLDB remote mode requires 'org.apache.manifoldcf.hsqldbdatabaseserver' property, containing a server name or IP address");
         } else {
            String port = LockManagerFactory.getProperty(tc, "org.apache.manifoldcf.hsqldbdatabaseport");
            if (port != null && port.length() > 0) {
               server = server + ":" + port;
            }

            String instanceName = LockManagerFactory.getProperty(tc, "org.apache.manifoldcf.hsqldbdatabaseinstance");
            if (instanceName != null && instanceName.length() > 0) {
               server = server + "/" + instanceName;
            }

            return "jdbc:hsqldb:" + protocol + "://" + server;
         }
      }
   }

   protected static String getDatabaseString(IThreadContext tc, String databaseName) throws ManifoldCFException {
      String protocol = LockManagerFactory.getProperty(tc, "org.apache.manifoldcf.hsqldbdatabaseprotocol");
      return protocol == null ? getFullDatabasePath(databaseName) : databaseName;
   }

   protected static String getFullDatabasePath(String databaseName) throws ManifoldCFException {
      File path = ManifoldCF.getFileProperty("org.apache.manifoldcf.hsqldbdatabasepath");
      if (path == null) {
         throw new ManifoldCFException("HSQLDB database requires 'org.apache.manifoldcf.hsqldbdatabasepath' property, containing a relative path");
      } else {
         String pathString = path.toString().replace("\\\\", "/");
         if (!pathString.endsWith("/")) {
            pathString = pathString + "/";
         }

         return pathString + databaseName;
      }
   }

   protected void initializeConnection(Connection connection) throws ManifoldCFException {
      super.initializeConnection(connection);
      this.executeViaThread(connection, "SET SCHEMA " + this.schemaNameForQueries.toUpperCase(Locale.ROOT), (List)null, false, -1, (ResultSpecification)null, (ILimitChecker)null);
   }

   public void openDatabase() throws ManifoldCFException {
   }

   public void closeDatabase() throws ManifoldCFException {
      if (!this.isRemote) {
         try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
         } catch (Exception e) {
            throw new ManifoldCFException(e.getMessage(), e);
         }

         try {
            Connection c = DriverManager.getConnection("jdbc:hsqldb:file:" + this.databaseName, this.userName, this.password);
            Statement s = c.createStatement();
            s.execute("SHUTDOWN");
            c.close();
         } catch (Exception e) {
            e.printStackTrace();
         }
      }

   }

   public String getDatabaseCacheKey() {
      return this.cacheKey;
   }

   public void performInsert(String tableName, Map<String, Object> parameterMap, StringSet invalidateKeys) throws ManifoldCFException {
      List paramArray = new ArrayList();
      StringBuilder bf = new StringBuilder();
      bf.append("INSERT INTO ");
      bf.append(tableName);
      bf.append(" (");
      StringBuilder values = new StringBuilder(" VALUES (");
      Iterator<Map.Entry<String, Object>> it = parameterMap.entrySet().iterator();
      boolean first = true;

      while(it.hasNext()) {
         Map.Entry<String, Object> e = (Map.Entry)it.next();
         String key = (String)e.getKey();
         Object o = e.getValue();
         if (o != null) {
            paramArray.add(o);
            if (!first) {
               bf.append(',');
               values.append(',');
            }

            bf.append(key);
            values.append('?');
            first = false;
         }
      }

      bf.append(')');
      values.append(')');
      bf.append(values);
      this.performModification(bf.toString(), paramArray, invalidateKeys);
   }

   public void performUpdate(String tableName, Map<String, Object> parameterMap, String whereClause, List whereParameters, StringSet invalidateKeys) throws ManifoldCFException {
      List paramArray = new ArrayList();
      StringBuilder bf = new StringBuilder();
      bf.append("UPDATE ");
      bf.append(tableName);
      bf.append(" SET ");
      Iterator<Map.Entry<String, Object>> it = parameterMap.entrySet().iterator();

      for(boolean first = true; it.hasNext(); first = false) {
         Map.Entry<String, Object> e = (Map.Entry)it.next();
         String key = (String)e.getKey();
         Object o = e.getValue();
         if (!first) {
            bf.append(',');
         }

         bf.append(key);
         bf.append('=');
         if (o == null) {
            bf.append("NULL");
         } else {
            bf.append('?');
            paramArray.add(o);
         }
      }

      if (whereClause != null) {
         bf.append(' ');
         bf.append(whereClause);
         if (whereParameters != null) {
            for(int i = 0; i < whereParameters.size(); ++i) {
               Object value = whereParameters.get(i);
               paramArray.add(value);
            }
         }
      }

      this.performModification(bf.toString(), paramArray, invalidateKeys);
   }

   public void performDelete(String tableName, String whereClause, List whereParameters, StringSet invalidateKeys) throws ManifoldCFException {
      StringBuilder bf = new StringBuilder();
      bf.append("DELETE FROM ");
      bf.append(tableName);
      if (whereClause != null) {
         bf.append(' ');
         bf.append(whereClause);
      } else {
         whereParameters = null;
      }

      this.performModification(bf.toString(), whereParameters, invalidateKeys);
   }

   public void performCreate(String tableName, Map<String, ColumnDescription> columnMap, StringSet invalidateKeys) throws ManifoldCFException {
      StringBuilder queryBuffer = new StringBuilder("CREATE CACHED TABLE ");
      queryBuffer.append(tableName);
      queryBuffer.append('(');
      Iterator<String> iter = columnMap.keySet().iterator();

      String columnName;
      ColumnDescription cd;
      for(boolean first = true; iter.hasNext(); appendDescription(queryBuffer, columnName, cd, false)) {
         columnName = (String)iter.next();
         cd = (ColumnDescription)columnMap.get(columnName);
         if (!first) {
            queryBuffer.append(',');
         } else {
            first = false;
         }
      }

      queryBuffer.append(')');
      this.performModification(queryBuffer.toString(), (List)null, invalidateKeys);
   }

   protected static void appendDescription(StringBuilder queryBuffer, String columnName, ColumnDescription cd, boolean forceNull) {
      queryBuffer.append(columnName);
      queryBuffer.append(' ');
      queryBuffer.append(mapType(cd.getTypeString()));
      if (!forceNull && !cd.getIsNull()) {
         queryBuffer.append(" NOT NULL");
      } else {
         queryBuffer.append(" NULL");
      }

      if (cd.getIsPrimaryKey()) {
         queryBuffer.append(" PRIMARY KEY");
      }

      if (cd.getReferenceTable() != null) {
         queryBuffer.append(" REFERENCES ");
         queryBuffer.append(cd.getReferenceTable());
         queryBuffer.append('(');
         queryBuffer.append(cd.getReferenceColumn());
         queryBuffer.append(") ON DELETE");
         if (cd.getReferenceCascade()) {
            queryBuffer.append(" CASCADE");
         } else {
            queryBuffer.append(" RESTRICT");
         }
      }

   }

   public void performAlter(String tableName, Map<String, ColumnDescription> columnMap, Map<String, ColumnDescription> columnModifyMap, List<String> columnDeleteList, StringSet invalidateKeys) throws ManifoldCFException {
      this.beginTransaction(0);

      try {
         if (columnDeleteList != null) {
            int i = 0;

            while(i < columnDeleteList.size()) {
               String columnName = (String)columnDeleteList.get(i++);
               this.performModification("ALTER TABLE " + tableName + " DROP " + columnName, (List)null, invalidateKeys);
            }
         }

         if (columnModifyMap != null) {
            for(String columnName : columnModifyMap.keySet()) {
               ColumnDescription cd = (ColumnDescription)columnModifyMap.get(columnName);
               StringBuilder sb = new StringBuilder();
               appendDescription(sb, columnName, cd, false);
               this.performModification("ALTER TABLE " + tableName + " ALTER COLUMN " + sb.toString(), (List)null, invalidateKeys);
            }
         }

         if (columnMap != null) {
            for(String columnName : columnMap.keySet()) {
               ColumnDescription cd = (ColumnDescription)columnMap.get(columnName);
               StringBuilder sb = new StringBuilder();
               appendDescription(sb, columnName, cd, false);
               this.performModification("ALTER TABLE " + tableName + " ADD " + sb.toString(), (List)null, invalidateKeys);
            }
         }
      } catch (ManifoldCFException e) {
         this.signalRollback();
         throw e;
      } catch (Error e) {
         this.signalRollback();
         throw e;
      } finally {
         this.endTransaction();
      }

   }

   protected static String mapType(String inputType) {
      return inputType.equalsIgnoreCase("longtext") ? "longvarchar" : inputType;
   }

   public void addTableIndex(String tableName, boolean unique, List<String> columnList) throws ManifoldCFException {
      String[] columns = new String[columnList.size()];

      for(int i = 0; i < columns.length; ++i) {
         columns[i] = (String)columnList.get(i);
      }

      this.performAddIndex((String)null, tableName, new IndexDescription(unique, columns));
   }

   public void performAddIndex(String indexName, String tableName, IndexDescription description) throws ManifoldCFException {
      String[] columnNames = description.getColumnNames();
      if (columnNames.length != 0) {
         if (indexName == null) {
            indexName = "I" + IDFactory.make(this.context);
         }

         StringBuilder queryBuffer = new StringBuilder("CREATE ");
         if (description.getIsUnique()) {
            queryBuffer.append("UNIQUE ");
         }

         queryBuffer.append("INDEX ");
         queryBuffer.append(indexName);
         queryBuffer.append(" ON ");
         queryBuffer.append(tableName);
         queryBuffer.append(" (");

         for(int i = 0; i < columnNames.length; ++i) {
            String colName = columnNames[i];
            if (i > 0) {
               queryBuffer.append(',');
            }

            queryBuffer.append(colName);
         }

         queryBuffer.append(')');
         this.performModification(queryBuffer.toString(), (List)null, (StringSet)null);
      }
   }

   public void performRemoveIndex(String indexName, String tableName) throws ManifoldCFException {
      this.performModification("DROP INDEX " + indexName, (List)null, (StringSet)null);
   }

   public void analyzeTable(String tableName) throws ManifoldCFException {
   }

   public void reindexTable(String tableName) throws ManifoldCFException {
   }

   public void performDrop(String tableName, StringSet invalidateKeys) throws ManifoldCFException {
      this.performModification("DROP TABLE " + tableName, (List)null, invalidateKeys);
   }

   public void createUserAndDatabase(String adminUserName, String adminPassword, StringSet invalidateKeys) throws ManifoldCFException {
      if (this.isRemote) {
         Database masterDatabase = new DBInterfaceHSQLDB(this.context, "PUBLIC", adminUserName, adminPassword);
         ArrayList params = new ArrayList();
         params.add(this.userName);
         IResultSet userResult = masterDatabase.executeQuery("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_USERS WHERE USER_NAME=?", params, (StringSet)null, (StringSet)null, (String)null, true, -1, (ResultSpecification)null, (ILimitChecker)null);
         if (userResult.getRowCount() == 0) {
            masterDatabase.executeQuery("CREATE USER " + quoteString(this.userName) + " PASSWORD " + quoteString(this.password), (List)null, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
         }

         params.clear();
         params.add(this.databaseName.toUpperCase(Locale.ROOT));
         IResultSet schemaResult = masterDatabase.executeQuery("SELECT * FROM INFORMATION_SCHEMA.SYSTEM_SCHEMAS WHERE TABLE_SCHEM=?", params, (StringSet)null, (StringSet)null, (String)null, true, -1, (ResultSpecification)null, (ILimitChecker)null);
         if (schemaResult.getRowCount() == 0) {
            masterDatabase.executeQuery("CREATE SCHEMA " + this.databaseName.toUpperCase(Locale.ROOT) + " AUTHORIZATION " + quoteString(this.userName), (List)null, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
         }
      } else {
         try {
            Class.forName("org.hsqldb.jdbcDriver").newInstance();
            DriverManager.getConnection("jdbc:hsqldb:file:" + this.databaseName, this.userName, this.password).close();
         } catch (Exception e) {
            throw new ManifoldCFException(e.getMessage(), e, 3);
         }

         this.performModification("SET DATABASE TRANSACTION CONTROL MVCC", (List)null, (StringSet)null);
         this.performModification("SET FILES SCALE 512", (List)null, (StringSet)null);
      }

   }

   private static String quoteString(String password) {
      StringBuilder sb = new StringBuilder();
      sb.append("\"");

      for(int i = 0; i < password.length(); ++i) {
         char x = password.charAt(i);
         if (x == '"') {
            sb.append("\"");
         }

         sb.append(x);
      }

      sb.append("\"");
      return sb.toString();
   }

   public void dropUserAndDatabase(String adminUserName, String adminPassword, StringSet invalidateKeys) throws ManifoldCFException {
      if (this.isRemote) {
         Database masterDatabase = new DBInterfaceHSQLDB(this.context, "PUBLIC", adminUserName, adminPassword);

         try {
            masterDatabase.executeQuery("DROP SCHEMA " + this.databaseName, (List)null, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
            masterDatabase.executeQuery("DROP USER " + quoteString(this.userName), (List)null, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
         } catch (ManifoldCFException e) {
            throw this.reinterpretException(e);
         }
      } else {
         File f = new File(this.databaseName + ".properties");
         if (f.exists()) {
            ConnectionFactory.releaseAll();
            this.closeDatabase();
            singleDelete(f);
            singleDelete(new File(this.databaseName + ".data"));
            singleDelete(new File(this.databaseName + ".lck"));
            singleDelete(new File(this.databaseName + ".log"));
            singleDelete(new File(this.databaseName + ".script"));
            recursiveDelete(new File(this.databaseName + ".tmp"));
         }
      }

   }

   protected static void recursiveDelete(File f) {
      if (f.exists()) {
         File[] files = f.listFiles();
         if (files != null) {
            int i = 0;

            while(i < files.length) {
               File newf = files[i++];
               if (newf.isDirectory()) {
                  recursiveDelete(newf);
               } else {
                  singleDelete(newf);
               }
            }
         }

         if (!f.delete()) {
            System.out.println("Failed to delete directory " + f.toString());
         }
      }

   }

   protected static void singleDelete(File f) {
      if (f.exists() && !f.delete()) {
         System.out.println("Failed to delete file " + f.toString());
      }

   }

   protected ManifoldCFException reinterpretException(ManifoldCFException theException) {
      if (Logging.db.isDebugEnabled()) {
         Logging.db.debug("Reinterpreting exception '" + theException.getMessage() + "'.  The exception type is " + Integer.toString(theException.getErrorCode()));
      }

      if (theException.getErrorCode() != 4) {
         return theException;
      } else {
         Throwable e = theException.getCause();
         if (!(e instanceof SQLException)) {
            return theException;
         } else {
            if (Logging.db.isDebugEnabled()) {
               Logging.db.debug("Exception " + theException.getMessage() + " is possibly a transaction abort signal");
            }

            SQLException sqlException = (SQLException)e;
            String message = sqlException.getMessage();
            String sqlState = sqlException.getSQLState();
            if (sqlState != null && sqlState.equals("40001")) {
               return new ManifoldCFException(message, e, 6);
            } else if (sqlState != null && sqlState.equals("40P01")) {
               return new ManifoldCFException(message, e, 6);
            } else if (sqlState != null && sqlState.equals("23505")) {
               return new ManifoldCFException(message, e, 6);
            } else {
               if (Logging.db.isDebugEnabled()) {
                  Logging.db.debug("Exception " + theException.getMessage() + " is NOT a transaction abort signal");
               }

               return theException;
            }
         }
      }
   }

   public void performModification(String query, List params, StringSet invalidateKeys) throws ManifoldCFException {
      try {
         this.executeQuery(query, params, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   public Map<String, ColumnDescription> getTableSchema(String tableName, StringSet cacheKeys, String queryClass) throws ManifoldCFException {
      StringBuilder query = new StringBuilder();
      List list = new ArrayList();
      list.add(this.schemaNameForQueries.toUpperCase(Locale.ROOT));
      list.add(tableName.toUpperCase(Locale.ROOT));
      query.append("SELECT column_name, is_nullable, data_type, character_maximum_length ").append("FROM INFORMATION_SCHEMA.COLUMNS WHERE table_schema=? AND table_name=?");
      IResultSet set = this.performQuery(query.toString(), list, cacheKeys, queryClass);
      if (set.getRowCount() == 0) {
         return null;
      } else {
         query = new StringBuilder();
         query.append("SELECT column_name ").append("FROM INFORMATION_SCHEMA.SYSTEM_PRIMARYKEYS WHERE table_schem=? AND table_name=?");
         IResultSet primarySet = this.performQuery(query.toString(), list, cacheKeys, queryClass);
         String primaryKey = null;
         if (primarySet.getRowCount() != 0) {
            primaryKey = ((String)primarySet.getRow(0).getValue("column_name")).toLowerCase(Locale.ROOT);
         }

         if (primaryKey == null) {
            primaryKey = "";
         }

         Map<String, ColumnDescription> rval = new HashMap();

         String fieldName;
         String type;
         boolean isPrimaryKey;
         boolean isNull;
         for(int i = 0; i < set.getRowCount(); rval.put(fieldName, new ColumnDescription(type, isPrimaryKey, isNull, (String)null, (String)null, false))) {
            IResultRow row = set.getRow(i++);
            fieldName = ((String)row.getValue("column_name")).toLowerCase(Locale.ROOT);
            type = (String)row.getValue("data_type");
            Long width = (Long)row.getValue("character_maximum_length");
            String isNullable = (String)row.getValue("is_nullable");
            isPrimaryKey = primaryKey.equals(fieldName);
            isNull = isNullable.equals("YES");
            if (type.equals("CHARACTER VARYING")) {
               String dataType = "VARCHAR(" + width.toString() + ")";
            } else if (type.equals("CLOB")) {
               String dataType = "LONGVARCHAR";
            }
         }

         return rval;
      }
   }

   public Map<String, IndexDescription> getTableIndexes(String tableName, StringSet cacheKeys, String queryClass) throws ManifoldCFException {
      Map<String, IndexDescription> rval = new HashMap();
      String query = "SELECT index_name,column_name,non_unique,ordinal_position FROM INFORMATION_SCHEMA.SYSTEM_INDEXINFO WHERE table_schem=? AND TABLE_NAME=? ORDER BY index_name,ordinal_position ASC";
      List list = new ArrayList();
      list.add(this.schemaNameForQueries.toUpperCase(Locale.ROOT));
      list.add(tableName.toUpperCase(Locale.ROOT));
      IResultSet result = this.performQuery(query, list, cacheKeys, queryClass);
      String lastIndexName = null;
      List<String> indexColumns = null;
      boolean isUnique = false;

      String nonUnique;
      for(int i = 0; i < result.getRowCount(); isUnique = nonUnique.equals("false")) {
         IResultRow row = result.getRow(i++);
         String indexName = ((String)row.getValue("index_name")).toLowerCase(Locale.ROOT);
         String columnName = ((String)row.getValue("column_name")).toLowerCase(Locale.ROOT);
         nonUnique = row.getValue("non_unique").toString();
         if (lastIndexName != null && !lastIndexName.equals(indexName)) {
            this.addIndex(rval, lastIndexName, isUnique, indexColumns);
            lastIndexName = null;
            indexColumns = null;
            isUnique = false;
         }

         if (lastIndexName == null) {
            lastIndexName = indexName;
            indexColumns = new ArrayList();
            isUnique = false;
         }

         indexColumns.add(columnName);
      }

      if (lastIndexName != null) {
         this.addIndex(rval, lastIndexName, isUnique, indexColumns);
      }

      return rval;
   }

   protected void addIndex(Map rval, String indexName, boolean isUnique, List<String> indexColumns) {
      if (indexName.indexOf("sys_idx") == -1) {
         String[] columnNames = new String[indexColumns.size()];

         for(int i = 0; i < columnNames.length; ++i) {
            columnNames[i] = (String)indexColumns.get(i);
         }

         rval.put(indexName, new IndexDescription(isUnique, columnNames));
      }
   }

   public StringSet getAllTables(StringSet cacheKeys, String queryClass) throws ManifoldCFException {
      ArrayList list = new ArrayList();
      list.add(this.schemaNameForQueries.toUpperCase(Locale.ROOT));
      IResultSet set = this.performQuery("SELECT table_name FROM INFORMATION_SCHEMA.TABLES WHERE table_schema=?", list, cacheKeys, queryClass);
      StringSetBuffer ssb = new StringSetBuffer();
      String columnName = "table_name";
      int i = 0;

      while(i < set.getRowCount()) {
         IResultRow row = set.getRow(i++);
         String value = row.getValue(columnName).toString();
         ssb.add(value);
      }

      return new StringSet(ssb);
   }

   public IResultSet performQuery(String query, List params, StringSet cacheKeys, String queryClass) throws ManifoldCFException {
      try {
         return this.executeQuery(query, params, cacheKeys, (StringSet)null, queryClass, true, -1, (ResultSpecification)null, (ILimitChecker)null);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   public IResultSet performQuery(String query, List params, StringSet cacheKeys, String queryClass, int maxResults, ILimitChecker returnLimit) throws ManifoldCFException {
      try {
         return this.executeQuery(query, params, cacheKeys, (StringSet)null, queryClass, true, maxResults, (ResultSpecification)null, returnLimit);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   public IResultSet performQuery(String query, List params, StringSet cacheKeys, String queryClass, int maxResults, ResultSpecification resultSpec, ILimitChecker returnLimit) throws ManifoldCFException {
      try {
         return this.executeQuery(query, params, cacheKeys, (StringSet)null, queryClass, true, maxResults, resultSpec, returnLimit);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   public String constructIndexOrderByClause(String[] fieldNames, boolean direction) {
      if (fieldNames.length == 0) {
         return "";
      } else {
         StringBuilder sb = new StringBuilder("ORDER BY ");

         for(int i = 0; i < fieldNames.length; ++i) {
            if (i > 0) {
               sb.append(", ");
            }

            sb.append(fieldNames[i]);
            if (direction) {
               sb.append(" ASC");
            } else {
               sb.append(" DESC");
            }
         }

         return sb.toString();
      }
   }

   public String constructDoubleCastClause(String value) {
      return "CAST(" + value + " AS DOUBLE PRECISION)";
   }

   public String constructCountClause(String column) {
      return "CAST(COUNT(" + column + ") AS bigint)";
   }

   public String constructRegexpClause(String column, String regularExpression, boolean caseInsensitive) {
      return "REGEXP_MATCHES(CAST(" + column + " AS VARCHAR(4096))," + regularExpression + ")";
   }

   public String constructSubstringClause(String column, String regularExpression, boolean caseInsensitive) {
      return "REGEXP_SUBSTRING(CAST(" + column + " AS VARCHAR(4096))," + regularExpression + ")";
   }

   public String constructOffsetLimitClause(int offset, int limit, boolean afterOrderBy) {
      StringBuilder sb = new StringBuilder();
      if (offset != 0) {
         sb.append("OFFSET ").append(Integer.toString(offset));
      }

      if (limit != -1) {
         if (offset != 0) {
            sb.append(" ");
         }

         sb.append("LIMIT ").append(Integer.toString(limit));
         if (afterOrderBy) {
            sb.append(" USING INDEX");
         }
      }

      return sb.toString();
   }

   public String constructDistinctOnClause(List outputParameters, String baseQuery, List baseParameters, String[] distinctFields, String[] orderFields, boolean[] orderFieldsAscending, Map<String, String> otherFields) {
      if (baseParameters != null) {
         outputParameters.addAll(baseParameters);
      }

      StringBuilder sb = new StringBuilder("WITH txxx1 (");
      boolean needComma = false;

      for(String fieldName : otherFields.keySet()) {
         if (needComma) {
            sb.append(",");
         }

         sb.append(fieldName);
         needComma = true;
      }

      sb.append(") AS (SELECT ");
      needComma = false;

      for(String fieldName : otherFields.keySet()) {
         String columnValue = (String)otherFields.get(fieldName);
         if (needComma) {
            sb.append(",");
         }

         needComma = true;
         sb.append("txxx2.").append(columnValue).append(" AS ").append(fieldName);
      }

      sb.append(" FROM (").append(baseQuery).append(") txxx2)");
      sb.append(" SELECT * FROM (SELECT DISTINCT ");
      Map<String, String> distinctMap = new HashMap();

      for(int i = 0; i < distinctFields.length; ++i) {
         String distinctField = distinctFields[i];
         if (i > 0) {
            sb.append(",");
         }

         sb.append(distinctField);
         distinctMap.put(distinctField, distinctField);
      }

      sb.append(" FROM txxx1) AS txxx3, LATERAL (SELECT ");
      Iterator<String> var17 = otherFields.keySet().iterator();
      needComma = false;

      while(var17.hasNext()) {
         String fieldName = (String)var17.next();
         if (distinctMap.get(fieldName) == null) {
            if (needComma) {
               sb.append(",");
            }

            needComma = true;
            sb.append(fieldName);
         }
      }

      sb.append(" FROM txxx1 WHERE ");

      for(int var21 = 0; var21 < distinctFields.length; ++var21) {
         String distinctField = distinctFields[var21];
         if (var21 > 0) {
            sb.append(" AND ");
         }

         sb.append(distinctField).append("=txxx3.").append(distinctField);
      }

      if (distinctFields.length > 0 || orderFields.length > 0) {
         sb.append(" ORDER BY ");
         int k = 0;

         for(int var22 = 0; var22 < distinctFields.length; ++var22) {
            if (k > 0) {
               sb.append(",");
            }

            sb.append(distinctFields[var22]).append(" ASC");
            ++k;
         }

         for(int var23 = 0; var23 < orderFields.length; ++k) {
            if (k > 0) {
               sb.append(",");
            }

            sb.append(orderFields[var23]).append(" ");
            if (orderFieldsAscending[var23]) {
               sb.append("ASC");
            } else {
               sb.append("DESC");
            }

            ++var23;
         }
      }

      sb.append(" LIMIT 1) AS txxx4");
      return sb.toString();
   }

   public int findConjunctionClauseMax(ClauseDescription[] otherClauseDescriptions) {
      if (otherClauseDescriptions.length == 0) {
         return super.findConjunctionClauseMax(otherClauseDescriptions);
      } else {
         int number = 1;

         for(int i = 0; i < otherClauseDescriptions.length; ++i) {
            ClauseDescription otherClause = otherClauseDescriptions[i];
            List values = otherClause.getValues();
            if (values != null) {
               number *= values.size();
            }
         }

         int rval = this.getMaxOrClause() / number;
         if (rval == 0) {
            rval = 1;
         }

         return rval;
      }
   }

   public String buildConjunctionClause(List outputParameters, ClauseDescription[] clauseDescriptions) {
      if (clauseDescriptions.length == 1) {
         return super.buildConjunctionClause(outputParameters, clauseDescriptions);
      } else {
         StringBuilder sb = new StringBuilder("(");
         int[] counters = new int[clauseDescriptions.length];

         for(int i = 0; i < counters.length; ++i) {
            counters[i] = 0;
         }

         boolean isFirst = true;

         label61:
         while(true) {
            if (isFirst) {
               isFirst = false;
            } else {
               sb.append(" OR ");
            }

            for(int i = 0; i < counters.length; ++i) {
               ClauseDescription cd = clauseDescriptions[i];
               if (i > 0) {
                  sb.append(" AND ");
               }

               List values = cd.getValues();
               String joinColumn = cd.getJoinColumnName();
               sb.append(cd.getColumnName()).append(cd.getOperation());
               if (values != null) {
                  sb.append("?");
                  outputParameters.add(values.get(counters[i]));
               } else if (joinColumn != null) {
                  sb.append(joinColumn);
               }
            }

            int j = 0;

            while(j != counters.length) {
               int var10002 = counters[j]++;
               ClauseDescription cd = clauseDescriptions[j];
               List values = cd.getValues();
               int size = 1;
               if (values != null) {
                  size = values.size();
               }

               if (counters[j] < size) {
                  continue label61;
               }

               ++j;

               for(int k = 0; k < j; ++k) {
                  counters[k] = 0;
               }
            }

            sb.append(")");
            return sb.toString();
         }
      }
   }

   public int getMaxInClause() {
      return 100;
   }

   public int getMaxOrClause() {
      return 25;
   }

   public int getWindowedReportMaxRows() {
      return 1000;
   }

   public void beginTransaction() throws ManifoldCFException {
      this.beginTransaction(0);
   }

   public void beginTransaction(int transactionType) throws ManifoldCFException {
      if (this.getCurrentTransactionType() == 2) {
         ++this.serializableDepth;
      } else {
         if (transactionType == 0) {
            transactionType = this.getCurrentTransactionType();
         }

         switch (transactionType) {
            case 1:
               this.desiredTransactionType = 2;
               super.beginTransaction(1);
               break;
            case 2:
               this.desiredTransactionType = 8;
               super.beginTransaction(2);
               break;
            default:
               throw new ManifoldCFException("Bad transaction type: " + Integer.toString(transactionType));
         }

      }
   }

   public void signalRollback() {
      if (this.serializableDepth == 0) {
         super.signalRollback();
      }

   }

   public void endTransaction() throws ManifoldCFException {
      if (this.serializableDepth > 0) {
         --this.serializableDepth;
      } else {
         super.endTransaction();
      }
   }

   protected void startATransaction() throws ManifoldCFException {
      if (!this.inTransaction) {
         try {
            this.connection.getConnection().setAutoCommit(false);
            this.connection.getConnection().setTransactionIsolation(this.desiredTransactionType);
         } catch (SQLException e) {
            throw new ManifoldCFException(e.getMessage(), e, 4);
         }

         this.inTransaction = true;
      }

      ++this.depthCount;
   }

   protected void commitCurrentTransaction() throws ManifoldCFException {
      if (!this.inTransaction) {
         throw new ManifoldCFException("Transaction nesting error!");
      } else {
         if (this.depthCount == 1) {
            try {
               if (this.connection != null) {
                  this.connection.getConnection().commit();
                  this.connection.getConnection().setAutoCommit(true);
               }
            } catch (SQLException e) {
               throw new ManifoldCFException(e.getMessage(), e, 4);
            }

            this.inTransaction = false;
         }

         --this.depthCount;
      }
   }

   protected void rollbackCurrentTransaction() throws ManifoldCFException {
      if (!this.inTransaction) {
         throw new ManifoldCFException("Transaction nesting error!");
      } else {
         if (this.depthCount == 1) {
            try {
               if (this.connection != null) {
                  this.connection.getConnection().rollback();
                  this.connection.getConnection().setAutoCommit(true);
               }
            } catch (SQLException e) {
               throw new ManifoldCFException(e.getMessage(), e, 4);
            }

            this.inTransaction = false;
         }

         --this.depthCount;
      }
   }

   protected void explainQuery(String query, List params) throws ManifoldCFException {
      IResultSet x = this.executeUncachedQuery("EXPLAIN PLAN FOR " + query, (List)null, true, -1, (ResultSpecification)null, (ILimitChecker)null);
      int k = 0;

      while(k < x.getRowCount()) {
         IResultRow row = x.getRow(k++);
         Iterator<String> iter = row.getColumns();
         String colName = (String)iter.next();
         Logging.db.warn(" Plan: " + row.getValue(colName).toString());
      }

      Logging.db.warn("");
   }

   protected String mapLabelName(String rawLabelName) {
      return rawLabelName.toLowerCase(Locale.ROOT);
   }

   static {
      legalProtocolValues.put("hsql", "hsql");
      legalProtocolValues.put("http", "http");
      legalProtocolValues.put("https", "https");
   }
}
