package org.apache.manifoldcf.core.interfaces;

public interface ICacheManager {
   String _rcsid = "@(#)$Id: ICacheManager.java 988245 2010-08-23 18:39:35Z kwright $";

   void findObjectsAndExecute(ICacheDescription[] var1, StringSet var2, ICacheExecutor var3, String var4) throws ManifoldCFException;

   ICacheHandle enterCache(ICacheDescription[] var1, StringSet var2, String var3) throws ManifoldCFException;

   ICacheCreateHandle enterCreateSection(ICacheHandle var1) throws ManifoldCFException;

   Object lookupObject(ICacheCreateHandle var1, ICacheDescription var2) throws ManifoldCFException;

   void saveObject(ICacheCreateHandle var1, ICacheDescription var2, Object var3) throws ManifoldCFException;

   void leaveCreateSection(ICacheCreateHandle var1) throws ManifoldCFException;

   void invalidateKeys(ICacheHandle var1) throws ManifoldCFException;

   void leaveCache(ICacheHandle var1) throws ManifoldCFException;

   void startTransaction(String var1, String var2) throws ManifoldCFException;

   void commitTransaction(String var1) throws ManifoldCFException;

   void rollbackTransaction(String var1) throws ManifoldCFException;

   void expireObjects(long var1) throws ManifoldCFException;
}
