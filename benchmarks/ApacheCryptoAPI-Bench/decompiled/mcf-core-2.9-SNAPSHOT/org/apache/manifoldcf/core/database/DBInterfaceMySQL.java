package org.apache.manifoldcf.core.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.apache.manifoldcf.core.interfaces.CacheKeyFactory;
import org.apache.manifoldcf.core.interfaces.ColumnDescription;
import org.apache.manifoldcf.core.interfaces.IDBInterface;
import org.apache.manifoldcf.core.interfaces.IDFactory;
import org.apache.manifoldcf.core.interfaces.ILimitChecker;
import org.apache.manifoldcf.core.interfaces.ILockManager;
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

public class DBInterfaceMySQL extends Database implements IDBInterface {
   public static final String _rcsid = "@(#)$Id: DBInterfaceMySQL.java 999670 2010-09-21 22:18:19Z kwright $";
   public static final String mysqlServerProperty = "org.apache.manifoldcf.mysql.server";
   public static final String mysqlClientProperty = "org.apache.manifoldcf.mysql.client";
   private static final String _driver = "com.mysql.jdbc.Driver";
   protected ILockManager lockManager;
   protected int serializableDepth;
   protected List<String> tablesToAnalyze;
   protected static Map<String, TableStatistics> currentAnalyzeStatistics = new HashMap();
   protected static Map<String, Integer> analyzeThresholds = new HashMap();
   protected static final int commitThreshold = 100;
   protected static final String statslockAnalyzePrefix = "statslock-analyze-";
   protected static final String statsAnalyzePrefix = "stats-analyze-";
   protected String cacheKey;

   public DBInterfaceMySQL(IThreadContext tc, String databaseName, String userName, String password) throws ManifoldCFException {
      this(tc, "com.mysql.jdbc.Driver", databaseName, userName, password);
   }

   protected DBInterfaceMySQL(IThreadContext tc, String jdbcDriverClass, String databaseName, String userName, String password) throws ManifoldCFException {
      super(tc, getJdbcUrl(tc, databaseName), jdbcDriverClass, databaseName, userName, password);
      this.serializableDepth = 0;
      this.tablesToAnalyze = new ArrayList();
      this.cacheKey = CacheKeyFactory.makeDatabaseKey(this.databaseName);
      this.lockManager = LockManagerFactory.make(tc);
   }

   private static String getJdbcUrl(IThreadContext tc, String theDatabaseName) throws ManifoldCFException {
      String server = LockManagerFactory.getProperty(tc, "org.apache.manifoldcf.mysql.server");
      if (server == null || server.length() == 0) {
         server = "localhost";
      }

      return "jdbc:mysql://" + server + "/" + theDatabaseName + "?useUnicode=true&characterEncoding=utf8";
   }

   protected String getJdbcDriverClass() {
      return "com.mysql.jdbc.Driver";
   }

   protected void interruptCleanup(Connection connection) {
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
            if (sqlState != null && sqlState.equals("23000")) {
               return new ManifoldCFException(message, e, 6);
            } else if (sqlState != null && sqlState.equals("40001")) {
               return new ManifoldCFException(message, e, 6);
            } else if (sqlState != null && sqlState.equals("40P01")) {
               return new ManifoldCFException(message, e, 6);
            } else if (sqlState != null && sqlState.equals("HY000")) {
               return new ManifoldCFException(message, e, 6);
            } else if (sqlState != null && sqlState.equals("41000")) {
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

   protected String mapLookupName(String rawColumnName, String rawLabelName) {
      return rawLabelName;
   }

   public void openDatabase() throws ManifoldCFException {
   }

   public void closeDatabase() throws ManifoldCFException {
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
      StringBuilder queryBuffer = new StringBuilder("CREATE TABLE ");
      queryBuffer.append(tableName);
      queryBuffer.append('(');
      Iterator<String> iter = columnMap.keySet().iterator();

      String columnName;
      ColumnDescription cd;
      for(boolean first = true; iter.hasNext(); appendDescription(queryBuffer, columnName, cd, false, true)) {
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

   protected static void appendDescription(StringBuilder queryBuffer, String columnName, ColumnDescription cd, boolean forceNull, boolean includeRestrict) {
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

      if (cd.getReferenceTable() != null && includeRestrict) {
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

   protected static String mapType(String inputType) {
      if (inputType.equalsIgnoreCase("float")) {
         return "DOUBLE";
      } else {
         return inputType.equalsIgnoreCase("blob") ? "LONGBLOB" : inputType;
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
               appendDescription(sb, columnName, cd, false, false);
               this.performModification("ALTER TABLE " + tableName + " CHANGE " + columnName + " " + sb.toString(), (List)null, invalidateKeys);
            }
         }

         if (columnMap != null) {
            for(String columnName : columnMap.keySet()) {
               ColumnDescription cd = (ColumnDescription)columnMap.get(columnName);
               StringBuilder sb = new StringBuilder();
               appendDescription(sb, columnName, cd, false, true);
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
      this.performModification("DROP INDEX " + indexName + " ON " + tableName, (List)null, (StringSet)null);
   }

   protected int readDatum(String datumName) throws ManifoldCFException {
      byte[] bytes = this.lockManager.readData(datumName);
      return bytes == null ? 0 : (bytes[0] & 255) + ((bytes[1] & 255) << 8) + ((bytes[2] & 255) << 16) + ((bytes[3] & 255) << 24);
   }

   protected void writeDatum(String datumName, int value) throws ManifoldCFException {
      byte[] bytes = new byte[4];
      bytes[0] = (byte)(value & 255);
      bytes[1] = (byte)(value >> 8 & 255);
      bytes[2] = (byte)(value >> 16 & 255);
      bytes[3] = (byte)(value >> 24 & 255);
      this.lockManager.writeData(datumName, bytes);
   }

   public void analyzeTable(String tableName) throws ManifoldCFException {
      String tableStatisticsLock = "statslock-analyze-" + tableName;
      this.lockManager.enterWriteCriticalSection(tableStatisticsLock);

      try {
         TableStatistics ts = (TableStatistics)currentAnalyzeStatistics.get(tableName);
         this.lockManager.enterWriteLock(tableStatisticsLock);

         try {
            String eventDatum = "stats-analyze-" + tableName;
            this.analyzeTableInternal(tableName);
            this.writeDatum(eventDatum, 0);
            if (ts != null) {
               ts.reset();
            }
         } finally {
            this.lockManager.leaveWriteLock(tableStatisticsLock);
         }
      } finally {
         this.lockManager.leaveWriteCriticalSection(tableStatisticsLock);
      }

   }

   public void reindexTable(String tableName) throws ManifoldCFException {
   }

   protected void analyzeTableInternal(String tableName) throws ManifoldCFException {
      if (this.getTransactionID() == null) {
         this.performModification("ANALYZE TABLE " + tableName, (List)null, (StringSet)null);
      } else {
         this.tablesToAnalyze.add(tableName);
      }

   }

   public void performDrop(String tableName, StringSet invalidateKeys) throws ManifoldCFException {
      this.performModification("DROP TABLE " + tableName, (List)null, invalidateKeys);
   }

   public void createUserAndDatabase(String adminUserName, String adminPassword, StringSet invalidateKeys) throws ManifoldCFException {
      String client = this.lockManager.getSharedConfiguration().getProperty("org.apache.manifoldcf.mysql.client");
      if (client == null || client.length() == 0) {
         client = "localhost";
      }

      Database masterDatabase = new DBInterfaceMySQL(this.context, this.getJdbcDriverClass(), "mysql", adminUserName, adminPassword);

      try {
         List list = new ArrayList();

         try {
            list.add("utf8");
            list.add("utf8_bin");
            masterDatabase.executeQuery("CREATE DATABASE " + this.databaseName + " CHARACTER SET ? COLLATE ?", list, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
         } catch (ManifoldCFException e) {
            if (e.getErrorCode() != 4) {
               throw new ManifoldCFException(e.getMessage());
            }
         }

         if (this.userName != null) {
            try {
               list.clear();
               list.add(this.userName);
               list.add(client);
               list.add(this.password);
               masterDatabase.executeQuery("GRANT ALL ON " + this.databaseName + ".* TO ?@? IDENTIFIED BY ?", list, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
            } catch (ManifoldCFException e) {
               if (e.getErrorCode() != 4) {
                  throw new ManifoldCFException(e.getMessage());
               }
            }
         }

      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   public void dropUserAndDatabase(String adminUserName, String adminPassword, StringSet invalidateKeys) throws ManifoldCFException {
      Database masterDatabase = new DBInterfaceMySQL(this.context, this.getJdbcDriverClass(), "mysql", adminUserName, adminPassword);

      try {
         masterDatabase.executeQuery("DROP DATABASE " + this.databaseName, (List)null, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
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
      list.add(this.databaseName.toLowerCase(Locale.ROOT));
      list.add(tableName.toLowerCase(Locale.ROOT));
      query.append("SELECT column_name, is_nullable, data_type, character_maximum_length ").append("FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA=? AND TABLE_NAME=?");
      IResultSet set = this.performQuery(query.toString(), list, cacheKeys, queryClass);
      if (set.getRowCount() == 0) {
         return null;
      } else {
         query = new StringBuilder();
         query.append("SELECT t1.column_name ").append("FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE t1, INFORMATION_SCHEMA.TABLE_CONSTRAINTS t2 ").append("WHERE t1.CONSTRAINT_NAME=t2.CONSTRAINT_NAME AND t1.TABLE_NAME=t2.TABLE_NAME AND ").append("t1.TABLE_SCHEMA=t2.TABLE_SCHEMA AND ").append("t1.TABLE_SCHEMA=? AND t1.TABLE_NAME=? AND t2.CONSTRAINT_TYPE='PRIMARY KEY'");
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
            if (type.equals("VARCHAR")) {
               String dataType = "VARCHAR(" + width.toString() + ")";
            } else if (type.equals("CHAR")) {
               String dataType = "CHAR(" + width.toString() + ")";
            } else if (type.equals("LONGBLOB")) {
               String dataType = "BLOB";
            } else if (type.equals("DOUBLE PRECISION")) {
               String dataType = "FLOAT";
            }
         }

         return rval;
      }
   }

   public Map<String, IndexDescription> getTableIndexes(String tableName, StringSet cacheKeys, String queryClass) throws ManifoldCFException {
      Map<String, IndexDescription> rval = new HashMap();
      String query = "SELECT index_name,column_name,non_unique,seq_in_index FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_SCHEMA=? AND TABLE_NAME=? ORDER BY index_name,seq_in_index ASC";
      List list = new ArrayList();
      list.add(this.databaseName.toLowerCase(Locale.ROOT));
      list.add(tableName.toLowerCase(Locale.ROOT));
      IResultSet result = this.performQuery(query, list, cacheKeys, queryClass);
      String lastIndexName = null;
      List<String> indexColumns = null;
      boolean isUnique = false;

      String nonUnique;
      for(int i = 0; i < result.getRowCount(); isUnique = nonUnique.equals("0")) {
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
      if (!indexName.equals("primary")) {
         String[] columnNames = new String[indexColumns.size()];

         for(int i = 0; i < columnNames.length; ++i) {
            columnNames[i] = (String)indexColumns.get(i);
         }

         rval.put(indexName, new IndexDescription(isUnique, columnNames));
      }
   }

   public StringSet getAllTables(StringSet cacheKeys, String queryClass) throws ManifoldCFException {
      IResultSet set = this.performQuery("SHOW TABLES", (List)null, cacheKeys, queryClass);
      StringSetBuffer ssb = new StringSetBuffer();
      String columnName = "Tables_in_" + this.databaseName.toLowerCase(Locale.ROOT);
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

   public String constructIndexHintClause(String tableName, IndexDescription description) throws ManifoldCFException {
      Map indexes = this.getTableIndexes(tableName, (StringSet)null, (String)null);

      for(String indexName : indexes.keySet()) {
         IndexDescription id = (IndexDescription)indexes.get(indexName);
         if (id.equals(description)) {
            return "FORCE INDEX (" + indexName + ")";
         }
      }

      throw new ManifoldCFException("Expected index description " + description + " not found");
   }

   public String constructDoubleCastClause(String value) {
      return value;
   }

   public String constructCountClause(String column) {
      return "COUNT(" + column + ")";
   }

   public String constructRegexpClause(String column, String regularExpression, boolean caseInsensitive) {
      return column + " LIKE " + regularExpression;
   }

   public String constructSubstringClause(String column, String regularExpression, boolean caseInsensitive) {
      return regularExpression;
   }

   public String constructOffsetLimitClause(int offset, int limit, boolean afterOrderBy) {
      StringBuilder sb = new StringBuilder();
      if (limit != -1) {
         sb.append("LIMIT ").append(Integer.toString(limit));
      }

      if (offset != 0) {
         if (limit != -1) {
            sb.append(" ");
         }

         sb.append("OFFSET ").append(Integer.toString(offset));
      }

      return sb.toString();
   }

   public String constructDistinctOnClause(List outputParameters, String baseQuery, List baseParameters, String[] distinctFields, String[] orderFields, boolean[] orderFieldsAscending, Map<String, String> otherFields) {
      if (baseParameters != null) {
         outputParameters.addAll(baseParameters);
      }

      StringBuilder sb = new StringBuilder("SELECT ");
      boolean needComma = false;

      for(String fieldName : otherFields.keySet()) {
         String columnValue = (String)otherFields.get(fieldName);
         if (needComma) {
            sb.append(",");
         }

         needComma = true;
         sb.append("txxx1.").append(columnValue).append(" AS ").append(fieldName);
      }

      sb.append(" FROM (").append(baseQuery).append(") txxx1");
      if (distinctFields.length > 0 || orderFields.length > 0) {
         sb.append(" ORDER BY ");
         int k = 0;

         for(int i = 0; i < distinctFields.length; ++i) {
            if (k > 0) {
               sb.append(",");
            }

            sb.append(distinctFields[i]).append(" ASC");
            ++k;
         }

         for(int var15 = 0; var15 < orderFields.length; ++k) {
            if (k > 0) {
               sb.append(",");
            }

            sb.append(orderFields[var15]).append(" ");
            if (orderFieldsAscending[var15]) {
               sb.append("ASC");
            } else {
               sb.append("DESC");
            }

            ++var15;
         }
      }

      return sb.toString();
   }

   public int getMaxInClause() {
      return 100;
   }

   public int getMaxOrClause() {
      return 25;
   }

   public int getWindowedReportMaxRows() {
      return 5000;
   }

   public void beginTransaction() throws ManifoldCFException {
      this.beginTransaction(0);
   }

   protected int getActualTransactionType() {
      return this.th == null ? -1 : this.th.getTransactionType();
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
               if (transactionType != this.getActualTransactionType()) {
                  this.performModification("SET TRANSACTION ISOLATION LEVEL READ COMMITTED", (List)null, (StringSet)null);
               }

               super.beginTransaction(transactionType);
               break;
            case 2:
               if (transactionType != this.getActualTransactionType()) {
                  this.performModification("SET TRANSACTION ISOLATION LEVEL SERIALIZABLE", (List)null, (StringSet)null);
               }

               super.beginTransaction(2);
               break;
            case 3:
               if (transactionType != this.getActualTransactionType()) {
                  this.performModification("SET TRANSACTION ISOLATION LEVEL REPEATABLE READ", (List)null, (StringSet)null);
               }

               super.beginTransaction(transactionType);
               break;
            default:
               throw new ManifoldCFException("Bad transaction type: " + Integer.toString(transactionType));
         }

      }
   }

   protected void startATransaction() throws ManifoldCFException {
      try {
         this.executeViaThread(this.connection.getConnection(), "START TRANSACTION", (List)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   protected void commitCurrentTransaction() throws ManifoldCFException {
      try {
         this.executeViaThread(this.connection.getConnection(), "COMMIT", (List)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
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
         if (this.getTransactionID() == null) {
            for(int i = 0; i < this.tablesToAnalyze.size(); ++i) {
               this.analyzeTableInternal((String)this.tablesToAnalyze.get(i));
            }

            this.tablesToAnalyze.clear();
         }

      }
   }

   protected void rollbackCurrentTransaction() throws ManifoldCFException {
      try {
         this.executeViaThread(this.connection.getConnection(), "ROLLBACK", (List)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   protected void noteModificationsNoTransactions(String tableName, int insertCount, int modifyCount, int deleteCount) throws ManifoldCFException {
      int eventCount = modifyCount + insertCount;
      String tableStatisticsLock = "statslock-analyze-" + tableName;
      this.lockManager.enterWriteCriticalSection(tableStatisticsLock);

      try {
         Integer threshold = (Integer)analyzeThresholds.get(tableName);
         int analyzeThreshold;
         if (threshold == null) {
            analyzeThreshold = this.lockManager.getSharedConfiguration().getIntProperty("org.apache.manifoldcf.db.mysql.analyze." + tableName, 10000);
            analyzeThresholds.put(tableName, new Integer(analyzeThreshold));
         } else {
            analyzeThreshold = threshold;
         }

         TableStatistics ts = (TableStatistics)currentAnalyzeStatistics.get(tableName);
         if (ts == null) {
            ts = new TableStatistics();
            currentAnalyzeStatistics.put(tableName, ts);
         }

         ts.add(eventCount);
         if (ts.getEventCount() >= 100) {
            this.lockManager.enterWriteLock(tableStatisticsLock);

            try {
               String eventDatum = "stats-analyze-" + tableName;
               int oldEventCount = this.readDatum(eventDatum);
               oldEventCount += ts.getEventCount();
               if (oldEventCount >= analyzeThreshold) {
                  this.analyzeTableInternal(tableName);
                  this.writeDatum(eventDatum, 0);
               } else {
                  this.writeDatum(eventDatum, oldEventCount);
               }

               ts.reset();
            } finally {
               this.lockManager.leaveWriteLock(tableStatisticsLock);
            }
         }
      } finally {
         this.lockManager.leaveWriteCriticalSection(tableStatisticsLock);
      }

   }

   protected static class TableStatistics {
      protected int eventCount = 0;

      public TableStatistics() {
      }

      public void reset() {
         this.eventCount = 0;
      }

      public void add(int eventCount) {
         this.eventCount += eventCount;
      }

      public int getEventCount() {
         return this.eventCount;
      }
   }
}
