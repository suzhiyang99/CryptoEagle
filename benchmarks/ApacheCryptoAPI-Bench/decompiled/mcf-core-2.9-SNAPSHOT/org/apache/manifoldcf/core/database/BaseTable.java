package org.apache.manifoldcf.core.database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.manifoldcf.core.interfaces.CacheKeyFactory;
import org.apache.manifoldcf.core.interfaces.ClauseDescription;
import org.apache.manifoldcf.core.interfaces.IDBInterface;
import org.apache.manifoldcf.core.interfaces.ILimitChecker;
import org.apache.manifoldcf.core.interfaces.IResultRow;
import org.apache.manifoldcf.core.interfaces.IResultSet;
import org.apache.manifoldcf.core.interfaces.IndexDescription;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.StringSet;

public class BaseTable {
   public static final String _rcsid = "@(#)$Id: BaseTable.java 999670 2010-09-21 22:18:19Z kwright $";
   protected IDBInterface dbInterface;
   protected String tableName;

   public BaseTable(IDBInterface dbInterface, String tableName) {
      this.dbInterface = dbInterface;
      this.tableName = tableName;
   }

   protected IDBInterface getDBInterface() {
      return this.dbInterface;
   }

   public String getTableName() {
      return this.tableName;
   }

   public String getDatabaseCacheKey() {
      return this.dbInterface.getDatabaseCacheKey();
   }

   public String getTransactionID() {
      return this.dbInterface.getTransactionID();
   }

   protected void performInsert(Map parameterMap, StringSet invalidateKeys) throws ManifoldCFException {
      this.dbInterface.performInsert(this.tableName, parameterMap, invalidateKeys);
   }

   protected void performUpdate(Map parameterMap, String whereClause, List whereParameters, StringSet invalidateKeys) throws ManifoldCFException {
      this.dbInterface.performUpdate(this.tableName, parameterMap, whereClause, whereParameters, invalidateKeys);
   }

   protected void performDelete(String whereClause, List whereParameters, StringSet invalidateKeys) throws ManifoldCFException {
      this.dbInterface.performDelete(this.tableName, whereClause, whereParameters, invalidateKeys);
   }

   protected void performCreate(Map columnMap, StringSet invalidateKeys) throws ManifoldCFException {
      this.dbInterface.performCreate(this.tableName, columnMap, invalidateKeys);
   }

   public void performAlter(Map columnMap, Map columnModifyMap, List<String> columnDeleteList, StringSet invalidateKeys) throws ManifoldCFException {
      this.dbInterface.performAlter(this.tableName, columnMap, columnModifyMap, columnDeleteList, invalidateKeys);
   }

   protected void addTableIndex(boolean unique, List<String> columnList) throws ManifoldCFException {
      this.dbInterface.addTableIndex(this.tableName, unique, columnList);
   }

   protected void performAddIndex(String indexName, IndexDescription description) throws ManifoldCFException {
      this.dbInterface.performAddIndex(indexName, this.tableName, description);
   }

   public void performRemoveIndex(String indexName) throws ManifoldCFException {
      this.dbInterface.performRemoveIndex(indexName, this.tableName);
   }

   protected void analyzeTable() throws ManifoldCFException {
      this.dbInterface.analyzeTable(this.tableName);
   }

   protected void reindexTable() throws ManifoldCFException {
      this.dbInterface.reindexTable(this.tableName);
   }

   protected void performDrop(StringSet invalidateKeys) throws ManifoldCFException {
      this.dbInterface.performDrop(this.tableName, invalidateKeys);
   }

   protected Map getTableSchema(StringSet invalidateKeys, String queryClass) throws ManifoldCFException {
      return this.dbInterface.getTableSchema(this.tableName, invalidateKeys, queryClass);
   }

   protected Map getTableIndexes(StringSet invalidateKeys, String queryClass) throws ManifoldCFException {
      return this.dbInterface.getTableIndexes(this.tableName, invalidateKeys, queryClass);
   }

   protected void performModification(String query, List params, StringSet invalidateKeys) throws ManifoldCFException {
      this.dbInterface.performModification(query, params, invalidateKeys);
   }

   protected IResultSet performQuery(String query, List params, StringSet cacheKeys, String queryClass) throws ManifoldCFException {
      return this.dbInterface.performQuery(query, params, cacheKeys, queryClass);
   }

   protected IResultSet performQuery(String query, List params, StringSet cacheKeys, String queryClass, int resultLimit) throws ManifoldCFException {
      return this.dbInterface.performQuery(query, params, cacheKeys, queryClass, resultLimit, (ILimitChecker)null);
   }

   protected void beginTransaction() throws ManifoldCFException {
      this.dbInterface.beginTransaction();
   }

   public void performCommit() throws ManifoldCFException {
      this.dbInterface.performCommit();
   }

   protected void signalRollback() {
      this.dbInterface.signalRollback();
   }

   protected void endTransaction() throws ManifoldCFException {
      this.dbInterface.endTransaction();
   }

   protected long getSleepAmt() {
      return this.dbInterface.getSleepAmt();
   }

   protected void sleepFor(long amt) throws ManifoldCFException {
      this.dbInterface.sleepFor(amt);
   }

   public void noteModifications(int insertCount, int modifyCount, int deleteCount) throws ManifoldCFException {
      this.dbInterface.noteModifications(this.tableName, insertCount, modifyCount, deleteCount);
   }

   public String makeTableKey() {
      return CacheKeyFactory.makeTableKey((String)null, this.tableName, this.dbInterface.getDatabaseName());
   }

   public String constructDoubleCastClause(String value) {
      return this.dbInterface.constructDoubleCastClause(value);
   }

   public String constructCountClause(String column) {
      return this.dbInterface.constructCountClause(column);
   }

   public String constructRegexpClause(String column, String regularExpression, boolean caseInsensitive) {
      return this.dbInterface.constructRegexpClause(column, regularExpression, caseInsensitive);
   }

   public String constructSubstringClause(String column, String regularExpression, boolean caseInsensitive) {
      return this.dbInterface.constructSubstringClause(column, regularExpression, caseInsensitive);
   }

   public String constructOffsetLimitClause(int offset, int limit) {
      return this.dbInterface.constructOffsetLimitClause(offset, limit);
   }

   public String constructDistinctOnClause(List outputParameters, String baseQuery, List baseParameters, String[] distinctFields, String[] orderFields, boolean[] orderFieldsAscending, Map<String, String> otherFields) {
      return this.dbInterface.constructDistinctOnClause(outputParameters, baseQuery, baseParameters, distinctFields, orderFields, orderFieldsAscending, otherFields);
   }

   public int findConjunctionClauseMax(ClauseDescription[] otherClauseDescriptions) {
      return this.dbInterface.findConjunctionClauseMax(otherClauseDescriptions);
   }

   public String buildConjunctionClause(List outputParameters, ClauseDescription[] clauseDescriptions) {
      return this.dbInterface.buildConjunctionClause(outputParameters, clauseDescriptions);
   }

   protected int getMaxInClause() {
      return this.dbInterface.getMaxInClause();
   }

   protected int getMaxOrClause() {
      return this.dbInterface.getMaxOrClause();
   }

   public int getWindowedReportMaxRows() {
      return this.dbInterface.getWindowedReportMaxRows();
   }

   public static void readRow(BaseObject object, IResultRow resultRow) {
      Iterator iter = resultRow.getColumns();

      while(iter.hasNext()) {
         String columnName = (String)iter.next();
         Object columnValue = resultRow.getValue(columnName);
         object.setValue(columnName, columnValue);
      }

   }

   public static Map prepareRowForSave(BaseObject object, StringSet fieldSet) {
      HashMap rval = new HashMap();
      Iterator keys = fieldSet.getKeys();

      while(keys.hasNext()) {
         String keyName = (String)keys.next();
         Object x = object.getValue(keyName);
         if (x != null) {
            rval.put(keyName, x);
         }
      }

      return rval;
   }
}
