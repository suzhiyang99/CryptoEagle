package org.apache.manifoldcf.core.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.manifoldcf.core.interfaces.CacheKeyFactory;
import org.apache.manifoldcf.core.interfaces.ClauseDescription;
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

public class DBInterfacePostgreSQL extends Database implements IDBInterface {
   public static final String _rcsid = "@(#)$Id: DBInterfacePostgreSQL.java 999670 2010-09-21 22:18:19Z kwright $";
   public static final String postgresqlHostnameProperty = "org.apache.manifoldcf.postgresql.hostname";
   public static final String postgresqlPortProperty = "org.apache.manifoldcf.postgresql.port";
   public static final String postgresqlSslProperty = "org.apache.manifoldcf.postgresql.ssl";
   private static final String _defaultUrl = "jdbc:postgresql://localhost/";
   private static final String _driver = "org.postgresql.Driver";
   protected final ILockManager lockManager;
   protected final String cacheKey;
   protected int serializableDepth = 0;
   protected List<String> tablesToAnalyze = new ArrayList();
   protected List<String> tablesToReindex = new ArrayList();
   protected static Map<String, TableStatistics> currentReindexStatistics = new HashMap();
   protected static Map<String, Integer> reindexThresholds = new HashMap();
   protected static Map<String, TableStatistics> currentAnalyzeStatistics = new HashMap();
   protected static Map<String, Integer> analyzeThresholds = new HashMap();
   protected static final int commitThreshold = 100;
   protected static final String statslockReindexPrefix = "statslock-reindex-";
   protected static final String statsReindexPrefix = "stats-reindex-";
   protected static final String statslockAnalyzePrefix = "statslock-analyze-";
   protected static final String statsAnalyzePrefix = "stats-analyze-";

   public DBInterfacePostgreSQL(IThreadContext tc, String databaseName, String userName, String password) throws ManifoldCFException {
      super(tc, getJdbcUrl(tc, databaseName), "org.postgresql.Driver", databaseName, userName, password);
      this.cacheKey = CacheKeyFactory.makeDatabaseKey(this.databaseName);
      this.lockManager = LockManagerFactory.make(tc);
   }

   private static String getJdbcUrl(IThreadContext tc, String databaseName) throws ManifoldCFException {
      String jdbcUrl = "jdbc:postgresql://localhost/" + databaseName;
      String hostname = LockManagerFactory.getProperty(tc, "org.apache.manifoldcf.postgresql.hostname");
      String ssl = LockManagerFactory.getProperty(tc, "org.apache.manifoldcf.postgresql.ssl");
      String port = LockManagerFactory.getProperty(tc, "org.apache.manifoldcf.postgresql.port");
      if (hostname != null && hostname.length() > 0) {
         jdbcUrl = "jdbc:postgresql://" + hostname;
         if (port != null && port.length() > 0) {
            jdbcUrl = jdbcUrl + ":" + port;
         }

         jdbcUrl = jdbcUrl + "/" + databaseName;
         if (ssl != null && ssl.equals("true")) {
            jdbcUrl = jdbcUrl + "?ssl=true";
         }
      }

      return jdbcUrl;
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
               this.performModification("ALTER TABLE ONLY " + tableName + " DROP " + columnName, (List)null, invalidateKeys);
            }
         }

         if (columnModifyMap != null) {
            for(String columnName : columnModifyMap.keySet()) {
               ColumnDescription cd = (ColumnDescription)columnModifyMap.get(columnName);
               String renameColumn = "__temp__";
               this.performModification("ALTER TABLE ONLY " + tableName + " RENAME " + columnName + " TO " + renameColumn, (List)null, invalidateKeys);
               StringBuilder sb = new StringBuilder();
               appendDescription(sb, columnName, cd, true);
               this.performModification("ALTER TABLE ONLY " + tableName + " ADD " + sb.toString(), (List)null, invalidateKeys);
               this.performModification("UPDATE " + tableName + " SET " + columnName + "=" + renameColumn, (List)null, invalidateKeys);
               if (!cd.getIsNull()) {
                  this.performModification("ALTER TABLE ONLY " + tableName + " ALTER " + columnName + " SET NOT NULL", (List)null, invalidateKeys);
               }

               this.performModification("ALTER TABLE ONLY " + tableName + " DROP " + renameColumn, (List)null, invalidateKeys);
            }
         }

         if (columnMap != null) {
            for(String columnName : columnMap.keySet()) {
               ColumnDescription cd = (ColumnDescription)columnMap.get(columnName);
               StringBuilder sb = new StringBuilder();
               appendDescription(sb, columnName, cd, false);
               this.performModification("ALTER TABLE ONLY " + tableName + " ADD " + sb.toString(), (List)null, invalidateKeys);
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
      if (inputType.equalsIgnoreCase("longtext")) {
         return "text";
      } else {
         return inputType.equalsIgnoreCase("blob") ? "bytea" : inputType;
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
      this.performModification("DROP INDEX " + indexName, (List)null, (StringSet)null);
   }

   public void performDrop(String tableName, StringSet invalidateKeys) throws ManifoldCFException {
      this.performModification("DROP TABLE " + tableName, (List)null, invalidateKeys);
   }

   public void createUserAndDatabase(String adminUserName, String adminPassword, StringSet invalidateKeys) throws ManifoldCFException {
      Database masterDatabase = new DBInterfacePostgreSQL(this.context, "template1", adminUserName, adminPassword);

      try {
         List params = new ArrayList();
         params.add(this.userName);
         IResultSet set = masterDatabase.executeQuery("SELECT * FROM pg_user WHERE usename=?", params, (StringSet)null, (StringSet)null, (String)null, true, -1, (ResultSpecification)null, (ILimitChecker)null);
         if (set.getRowCount() == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("'");

            for(int i = 0; i < this.password.length(); ++i) {
               char x = this.password.charAt(i);
               if (x == '\'') {
                  sb.append("'");
               }

               sb.append(x);
            }

            sb.append("'");
            String quotedPassword = sb.toString();
            masterDatabase.executeQuery("CREATE USER " + this.userName + " PASSWORD " + quotedPassword, (List)null, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
         }

         params.clear();
         params.add(this.databaseName);
         set = masterDatabase.executeQuery("SELECT * FROM pg_database WHERE datname=?", params, (StringSet)null, (StringSet)null, (String)null, true, -1, (ResultSpecification)null, (ILimitChecker)null);
         if (set.getRowCount() == 0) {
            masterDatabase.prepareForDatabaseCreate();
            masterDatabase.executeQuery("CREATE DATABASE " + this.databaseName + " OWNER " + this.userName + " ENCODING 'utf8'", (List)null, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
         }

      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   public void dropUserAndDatabase(String adminUserName, String adminPassword, StringSet invalidateKeys) throws ManifoldCFException {
      Database masterDatabase = new DBInterfacePostgreSQL(this.context, "template1", adminUserName, adminPassword);

      try {
         masterDatabase.executeQuery("DROP DATABASE " + this.databaseName, (List)null, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
         masterDatabase.executeQuery("DROP USER " + this.userName, (List)null, (StringSet)null, invalidateKeys, (String)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
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
            if (sqlState != null && sqlState.equals("08003")) {
               return new ManifoldCFException(message, e, 2);
            } else if (sqlState != null && sqlState.equals("40001")) {
               return new ManifoldCFException(message, e, 6);
            } else if (sqlState != null && sqlState.equals("40P01")) {
               return new ManifoldCFException(message, e, 6);
            } else if (sqlState != null && sqlState.equals("23505")) {
               return new ManifoldCFException(message, e, 6);
            } else if (sqlState != null && sqlState.equals("25P02")) {
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
      query.append("SELECT pg_attribute.attname AS \"Field\",");
      query.append("CASE pg_type.typname WHEN 'int2' THEN 'smallint' WHEN 'int4' THEN 'int'");
      query.append(" WHEN 'int8' THEN 'bigint' WHEN 'varchar' THEN 'varchar(' || pg_attribute.atttypmod-4 || ')'");
      query.append(" WHEN 'text' THEN 'longtext'");
      query.append(" WHEN 'bpchar' THEN 'char(' || pg_attribute.atttypmod-4 || ')'");
      query.append(" ELSE pg_type.typname END AS \"Type\",");
      query.append("CASE WHEN pg_attribute.attnotnull THEN '' ELSE 'YES' END AS \"Null\",");
      query.append("CASE pg_type.typname WHEN 'varchar' THEN substring(pg_attrdef.adsrc from '^(.*).*$') ELSE pg_attrdef.adsrc END AS Default ");
      query.append("FROM pg_class INNER JOIN pg_attribute ON (pg_class.oid=pg_attribute.attrelid) INNER JOIN pg_type ON (pg_attribute.atttypid=pg_type.oid) ");
      query.append("LEFT JOIN pg_attrdef ON (pg_class.oid=pg_attrdef.adrelid AND pg_attribute.attnum=pg_attrdef.adnum) ");
      query.append("WHERE pg_class.relname=? AND pg_attribute.attnum>=1 AND NOT pg_attribute.attisdropped ");
      query.append("ORDER BY pg_attribute.attnum");
      list.add(tableName);
      IResultSet set = this.performQuery(query.toString(), list, cacheKeys, queryClass);
      if (set.getRowCount() == 0) {
         return null;
      } else {
         Map<String, ColumnDescription> rval = new HashMap();
         int i = 0;

         while(i < set.getRowCount()) {
            IResultRow row = set.getRow(i++);
            String fieldName = row.getValue("Field").toString();
            String type = row.getValue("Type").toString();
            boolean isNull = row.getValue("Null").toString().equals("YES");
            boolean isPrimaryKey = false;
            rval.put(fieldName, new ColumnDescription(type, isPrimaryKey, isNull, (String)null, (String)null, false));
         }

         return rval;
      }
   }

   public Map<String, IndexDescription> getTableIndexes(String tableName, StringSet cacheKeys, String queryClass) throws ManifoldCFException {
      Map<String, IndexDescription> rval = new HashMap();
      String query = "SELECT pg_catalog.pg_get_indexdef(i.indexrelid, 0, true) AS indexdef FROM pg_catalog.pg_class c, pg_catalog.pg_class c2, pg_catalog.pg_index i WHERE c.relname = ? AND c.oid = i.indrelid AND i.indexrelid = c2.oid";
      List list = new ArrayList();
      list.add(tableName);
      IResultSet result = this.performQuery(query, list, cacheKeys, queryClass);
      int i = 0;

      while(i < result.getRowCount()) {
         IResultRow row = result.getRow(i++);
         String indexdef = (String)row.getValue("indexdef");
         int parsePosition = 0;
         int beforeMatch = indexdef.indexOf("CREATE UNIQUE INDEX ", parsePosition);
         boolean isUnique;
         if (beforeMatch == -1) {
            beforeMatch = indexdef.indexOf("CREATE INDEX ", parsePosition);
            if (beforeMatch == -1) {
               throw new ManifoldCFException("Cannot parse index description: '" + indexdef + "'");
            }

            isUnique = false;
            parsePosition += "CREATE INDEX ".length();
         } else {
            isUnique = true;
            parsePosition += "CREATE UNIQUE INDEX ".length();
         }

         int afterMatch = indexdef.indexOf(" ON", parsePosition);
         if (afterMatch == -1) {
            throw new ManifoldCFException("Cannot parse index description: '" + indexdef + "'");
         }

         String indexName = indexdef.substring(parsePosition, afterMatch);
         parsePosition = afterMatch + " ON".length();
         int parenPosition = indexdef.indexOf("(", parsePosition);
         if (parenPosition == -1) {
            throw new ManifoldCFException("Cannot parse index description: '" + indexdef + "'");
         }

         parsePosition = parenPosition + 1;
         List<String> columns = new ArrayList();

         while(true) {
            int nextIndex = indexdef.indexOf(",", parsePosition);
            int nextParenIndex = indexdef.indexOf(")", parsePosition);
            if (nextIndex == -1) {
               nextIndex = nextParenIndex;
            }

            if (nextIndex == -1) {
               throw new ManifoldCFException("Cannot parse index description: '" + indexdef + "'");
            }

            if (nextParenIndex != -1 && nextParenIndex < nextIndex) {
               nextIndex = nextParenIndex;
            }

            String columnName = indexdef.substring(parsePosition, nextIndex).trim();
            columns.add(columnName);
            if (nextIndex == nextParenIndex) {
               String[] columnNames = new String[columns.size()];

               for(int j = 0; j < columnNames.length; ++j) {
                  columnNames[j] = (String)columns.get(j);
               }

               rval.put(indexName, new IndexDescription(isUnique, columnNames));
               break;
            }

            parsePosition = nextIndex + 1;
         }
      }

      return rval;
   }

   public StringSet getAllTables(StringSet cacheKeys, String queryClass) throws ManifoldCFException {
      IResultSet set = this.performQuery("SELECT relname FROM pg_class", (List)null, cacheKeys, queryClass);
      StringSetBuffer ssb = new StringSetBuffer();
      String columnName = "relname";
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

   public String constructDoubleCastClause(String value) {
      return "CAST(" + value + " AS DOUBLE PRECISION)";
   }

   public String constructCountClause(String column) {
      return "COUNT(" + column + ")";
   }

   public String constructRegexpClause(String column, String regularExpression, boolean caseInsensitive) {
      return column + "~" + (caseInsensitive ? "*" : "") + regularExpression;
   }

   public String constructSubstringClause(String column, String regularExpression, boolean caseInsensitive) {
      StringBuilder sb = new StringBuilder();
      sb.append("SUBSTRING(");
      if (caseInsensitive) {
         sb.append("LOWER(").append(column).append(")");
      } else {
         sb.append(column);
      }

      sb.append(" FROM ");
      if (caseInsensitive) {
         sb.append("LOWER(").append(regularExpression).append(")");
      } else {
         sb.append(regularExpression);
      }

      sb.append(")");
      return sb.toString();
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
      }

      return sb.toString();
   }

   public String constructDistinctOnClause(List outputParameters, String baseQuery, List baseParameters, String[] distinctFields, String[] orderFields, boolean[] orderFieldsAscending, Map<String, String> otherFields) {
      if (baseParameters != null) {
         outputParameters.addAll(baseParameters);
      }

      StringBuilder sb = new StringBuilder("SELECT DISTINCT ON(");

      for(int i = 0; i < distinctFields.length; sb.append(distinctFields[i++])) {
         if (i > 0) {
            sb.append(",");
         }
      }

      sb.append(") ");
      Iterator<String> iter = otherFields.keySet().iterator();
      boolean needComma = false;

      while(iter.hasNext()) {
         String fieldName = (String)iter.next();
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

         for(int var14 = 0; var14 < distinctFields.length; ++var14) {
            if (k > 0) {
               sb.append(",");
            }

            sb.append(distinctFields[var14]).append(" ASC");
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

   public int findConjunctionClauseMax(ClauseDescription[] otherClauseDescriptions) {
      return this.getMaxOrClause();
   }

   public String buildConjunctionClause(List outputParameters, ClauseDescription[] clauseDescriptions) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < clauseDescriptions.length; ++i) {
         ClauseDescription cd = clauseDescriptions[i];
         if (i > 0) {
            sb.append(" AND ");
         }

         String columnName = cd.getColumnName();
         List values = cd.getValues();
         String operation = cd.getOperation();
         String joinColumn = cd.getJoinColumnName();
         if (values == null) {
            if (joinColumn != null) {
               sb.append(columnName).append(operation).append(joinColumn);
            } else {
               sb.append(columnName).append(operation);
            }
         } else if (values.size() <= 1) {
            sb.append(columnName).append(operation).append("?");
            outputParameters.add(values.get(0));
         } else {
            sb.append(" (");

            for(int j = 0; j < values.size(); ++j) {
               if (j > 0) {
                  sb.append(" OR ");
               }

               sb.append(columnName).append(operation).append("?");
               outputParameters.add(values.get(j));
            }

            sb.append(")");
         }
      }

      return sb.toString();
   }

   public int getWindowedReportMaxRows() {
      return 5000;
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
               super.beginTransaction(1);
               break;
            case 2:
               super.beginTransaction(2);

               try {
                  this.performModification("SET TRANSACTION ISOLATION LEVEL SERIALIZABLE", (List)null, (StringSet)null);
                  break;
               } catch (Error e) {
                  super.signalRollback();
                  super.endTransaction();
                  throw e;
               } catch (ManifoldCFException e) {
                  super.signalRollback();
                  super.endTransaction();
                  throw e;
               }
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
         if (this.getTransactionID() == null) {
            int i = 0;

            while(i < this.tablesToAnalyze.size()) {
               this.analyzeTableInternal((String)this.tablesToAnalyze.get(i++));
            }

            this.tablesToAnalyze.clear();
            i = 0;

            while(i < this.tablesToReindex.size()) {
               this.reindexTableInternal((String)this.tablesToReindex.get(i++));
            }

            this.tablesToReindex.clear();
         }

      }
   }

   protected void startATransaction() throws ManifoldCFException {
      try {
         this.executeViaThread(this.connection == null ? null : this.connection.getConnection(), "START TRANSACTION", (List)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   protected void commitCurrentTransaction() throws ManifoldCFException {
      try {
         this.executeViaThread(this.connection == null ? null : this.connection.getConnection(), "COMMIT", (List)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   protected void rollbackCurrentTransaction() throws ManifoldCFException {
      try {
         this.executeViaThread(this.connection == null ? null : this.connection.getConnection(), "ROLLBACK", (List)null, false, 0, (ResultSpecification)null, (ILimitChecker)null);
      } catch (ManifoldCFException e) {
         throw this.reinterpretException(e);
      }
   }

   protected void explainQuery(String query, List params) throws ManifoldCFException {
      String queryType = "EXPLAIN ";
      if ("SELECT".equalsIgnoreCase(query.substring(0, 6))) {
         queryType = queryType + "ANALYZE ";
      }

      IResultSet x = this.executeUncachedQuery(queryType + query, params, true, -1, (ResultSpecification)null, (ILimitChecker)null);

      for(int k = 0; k < x.getRowCount(); ++k) {
         IResultRow row = x.getRow(k);
         Iterator<String> iter = row.getColumns();
         String colName = (String)iter.next();
         Logging.db.warn(" Plan: " + row.getValue(colName).toString());
      }

      Logging.db.warn("");
      if (query.indexOf("jobqueue") != -1) {
         x = this.executeUncachedQuery("select n_distinct, most_common_vals, most_common_freqs from pg_stats where tablename='jobqueue' and attname='status'", (List)null, true, -1, (ResultSpecification)null, (ILimitChecker)null);

         for(int k = 0; k < x.getRowCount(); ++k) {
            IResultRow row = x.getRow(k);
            Logging.db.warn(" Stats: n_distinct=" + row.getValue("n_distinct").toString() + " most_common_vals=" + row.getValue("most_common_vals").toString() + " most_common_freqs=" + row.getValue("most_common_freqs").toString());
         }

         Logging.db.warn("");
      }

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
      String tableStatisticsLock = "statslock-reindex-" + tableName;
      this.lockManager.enterWriteCriticalSection(tableStatisticsLock);

      try {
         TableStatistics ts = (TableStatistics)currentReindexStatistics.get(tableName);
         this.lockManager.enterWriteLock(tableStatisticsLock);

         try {
            String eventDatum = "stats-reindex-" + tableName;
            this.reindexTableInternal(tableName);
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

   protected void analyzeTableInternal(String tableName) throws ManifoldCFException {
      if (this.getTransactionID() == null) {
         this.performModification("ANALYZE " + tableName, (List)null, (StringSet)null);
      } else {
         this.tablesToAnalyze.add(tableName);
      }

   }

   protected void reindexTableInternal(String tableName) throws ManifoldCFException {
      if (this.getTransactionID() != null) {
         this.tablesToReindex.add(tableName);
      } else {
         long sleepAmt = 0L;

         while(true) {
            try {
               this.performModification("REINDEX TABLE " + tableName, (List)null, (StringSet)null);
               break;
            } catch (ManifoldCFException e) {
               if (e.getErrorCode() != 6) {
                  throw e;
               }

               sleepAmt = this.getSleepAmt();
            } finally {
               this.sleepFor(sleepAmt);
            }
         }
      }

   }

   protected void noteModificationsNoTransactions(String tableName, int insertCount, int modifyCount, int deleteCount) throws ManifoldCFException {
      int eventCount = modifyCount + deleteCount;
      String tableStatisticsLock = "statslock-reindex-" + tableName;
      this.lockManager.enterWriteCriticalSection(tableStatisticsLock);

      try {
         Integer threshold = (Integer)reindexThresholds.get(tableName);
         int reindexThreshold;
         if (threshold == null) {
            reindexThreshold = this.lockManager.getSharedConfiguration().getIntProperty("org.apache.manifoldcf.db.postgres.reindex." + tableName, 250000);
            reindexThresholds.put(tableName, new Integer(reindexThreshold));
         } else {
            reindexThreshold = threshold;
         }

         TableStatistics ts = (TableStatistics)currentReindexStatistics.get(tableName);
         if (ts == null) {
            ts = new TableStatistics();
            currentReindexStatistics.put(tableName, ts);
         }

         ts.add(eventCount);
         if (ts.getEventCount() >= 100) {
            this.lockManager.enterWriteLock(tableStatisticsLock);

            try {
               String eventDatum = "stats-reindex-" + tableName;
               int oldEventCount = this.readDatum(eventDatum);
               oldEventCount += ts.getEventCount();
               if (oldEventCount >= reindexThreshold) {
                  this.reindexTableInternal(tableName);
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

      eventCount = modifyCount + insertCount;
      tableStatisticsLock = "statslock-analyze-" + tableName;
      this.lockManager.enterWriteCriticalSection(tableStatisticsLock);

      try {
         Integer threshold = (Integer)analyzeThresholds.get(tableName);
         int analyzeThreshold;
         if (threshold == null) {
            analyzeThreshold = this.lockManager.getSharedConfiguration().getIntProperty("org.apache.manifoldcf.db.postgres.analyze." + tableName, 2000);
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
