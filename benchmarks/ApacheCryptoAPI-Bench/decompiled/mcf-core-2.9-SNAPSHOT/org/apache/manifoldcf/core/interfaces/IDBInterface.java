package org.apache.manifoldcf.core.interfaces;

import java.util.List;
import java.util.Map;

public interface IDBInterface {
   String _rcsid = "@(#)$Id: IDBInterface.java 999670 2010-09-21 22:18:19Z kwright $";
   int TRANSACTION_ENCLOSING = 0;
   int TRANSACTION_READCOMMITTED = 1;
   int TRANSACTION_SERIALIZED = 2;
   int TRANSACTION_REPEATABLEREAD = 3;

   void openDatabase() throws ManifoldCFException;

   void closeDatabase() throws ManifoldCFException;

   String getDatabaseName();

   String getTransactionID();

   String getDatabaseCacheKey();

   void performInsert(String var1, Map<String, Object> var2, StringSet var3) throws ManifoldCFException;

   void performUpdate(String var1, Map<String, Object> var2, String var3, List var4, StringSet var5) throws ManifoldCFException;

   void performDelete(String var1, String var2, List var3, StringSet var4) throws ManifoldCFException;

   void performCreate(String var1, Map<String, ColumnDescription> var2, StringSet var3) throws ManifoldCFException;

   void performAlter(String var1, Map<String, ColumnDescription> var2, Map<String, ColumnDescription> var3, List<String> var4, StringSet var5) throws ManifoldCFException;

   void addTableIndex(String var1, boolean var2, List<String> var3) throws ManifoldCFException;

   void performAddIndex(String var1, String var2, IndexDescription var3) throws ManifoldCFException;

   void performRemoveIndex(String var1, String var2) throws ManifoldCFException;

   void analyzeTable(String var1) throws ManifoldCFException;

   void reindexTable(String var1) throws ManifoldCFException;

   void performDrop(String var1, StringSet var2) throws ManifoldCFException;

   void createUserAndDatabase(String var1, String var2, StringSet var3) throws ManifoldCFException;

   void dropUserAndDatabase(String var1, String var2, StringSet var3) throws ManifoldCFException;

   Map<String, ColumnDescription> getTableSchema(String var1, StringSet var2, String var3) throws ManifoldCFException;

   Map<String, IndexDescription> getTableIndexes(String var1, StringSet var2, String var3) throws ManifoldCFException;

   StringSet getAllTables(StringSet var1, String var2) throws ManifoldCFException;

   void performModification(String var1, List var2, StringSet var3) throws ManifoldCFException;

   IResultSet performQuery(String var1, List var2, StringSet var3, String var4) throws ManifoldCFException;

   IResultSet performQuery(String var1, List var2, StringSet var3, String var4, int var5, ILimitChecker var6) throws ManifoldCFException;

   IResultSet performQuery(String var1, List var2, StringSet var3, String var4, int var5, ResultSpecification var6, ILimitChecker var7) throws ManifoldCFException;

   String constructIndexHintClause(String var1, IndexDescription var2) throws ManifoldCFException;

   String constructIndexOrderByClause(String[] var1, boolean var2);

   String constructDoubleCastClause(String var1);

   String constructCountClause(String var1);

   String constructRegexpClause(String var1, String var2, boolean var3);

   String constructSubstringClause(String var1, String var2, boolean var3);

   String constructOffsetLimitClause(int var1, int var2, boolean var3);

   String constructOffsetLimitClause(int var1, int var2);

   String constructDistinctOnClause(List var1, String var2, List var3, String[] var4, String[] var5, boolean[] var6, Map<String, String> var7);

   int findConjunctionClauseMax(ClauseDescription[] var1);

   String buildConjunctionClause(List var1, ClauseDescription[] var2);

   int getMaxInClause();

   int getMaxOrClause();

   int getWindowedReportMaxRows();

   void beginTransaction() throws ManifoldCFException;

   void beginTransaction(int var1) throws ManifoldCFException;

   void performCommit() throws ManifoldCFException;

   void signalRollback();

   void endTransaction() throws ManifoldCFException;

   void noteModifications(String var1, int var2, int var3, int var4) throws ManifoldCFException;

   long getSleepAmt();

   void sleepFor(long var1) throws ManifoldCFException;
}
