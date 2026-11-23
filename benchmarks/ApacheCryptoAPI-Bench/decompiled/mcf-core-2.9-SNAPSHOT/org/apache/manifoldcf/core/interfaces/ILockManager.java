package org.apache.manifoldcf.core.interfaces;

public interface ILockManager {
   String _rcsid = "@(#)$Id: ILockManager.java 988245 2010-08-23 18:39:35Z kwright $";

   String registerServiceBeginServiceActivity(String var1, String var2, IServiceCleanup var3) throws ManifoldCFException;

   String registerServiceBeginServiceActivity(String var1, String var2, byte[] var3, IServiceCleanup var4) throws ManifoldCFException;

   void updateServiceData(String var1, String var2, byte[] var3) throws ManifoldCFException;

   byte[] retrieveServiceData(String var1, String var2) throws ManifoldCFException;

   void scanServiceData(String var1, IServiceDataAcceptor var2) throws ManifoldCFException;

   int countActiveServices(String var1) throws ManifoldCFException;

   boolean cleanupInactiveService(String var1, IServiceCleanup var2) throws ManifoldCFException;

   void endServiceActivity(String var1, String var2) throws ManifoldCFException;

   boolean checkServiceActive(String var1, String var2) throws ManifoldCFException;

   ManifoldCFConfiguration getSharedConfiguration() throws ManifoldCFException;

   void setGlobalFlag(String var1) throws ManifoldCFException;

   void clearGlobalFlag(String var1) throws ManifoldCFException;

   boolean checkGlobalFlag(String var1) throws ManifoldCFException;

   byte[] readData(String var1) throws ManifoldCFException;

   void writeData(String var1, byte[] var2) throws ManifoldCFException;

   void timedWait(int var1) throws ManifoldCFException;

   void enterWriteLock(String var1) throws ManifoldCFException;

   void enterWriteLockNoWait(String var1) throws ManifoldCFException, LockException;

   void leaveWriteLock(String var1) throws ManifoldCFException;

   void enterNonExWriteLock(String var1) throws ManifoldCFException;

   void enterNonExWriteLockNoWait(String var1) throws ManifoldCFException, LockException;

   void leaveNonExWriteLock(String var1) throws ManifoldCFException;

   void enterReadLock(String var1) throws ManifoldCFException;

   void enterReadLockNoWait(String var1) throws ManifoldCFException, LockException;

   void leaveReadLock(String var1) throws ManifoldCFException;

   void enterLocks(String[] var1, String[] var2, String[] var3) throws ManifoldCFException;

   void enterLocksNoWait(String[] var1, String[] var2, String[] var3) throws ManifoldCFException, LockException;

   void leaveLocks(String[] var1, String[] var2, String[] var3) throws ManifoldCFException;

   void clearLocks() throws ManifoldCFException;

   void enterReadCriticalSection(String var1) throws ManifoldCFException;

   void leaveReadCriticalSection(String var1) throws ManifoldCFException;

   void enterNonExWriteCriticalSection(String var1) throws ManifoldCFException;

   void leaveNonExWriteCriticalSection(String var1) throws ManifoldCFException;

   void enterWriteCriticalSection(String var1) throws ManifoldCFException;

   void leaveWriteCriticalSection(String var1) throws ManifoldCFException;

   void enterCriticalSections(String[] var1, String[] var2, String[] var3) throws ManifoldCFException;

   void leaveCriticalSections(String[] var1, String[] var2, String[] var3) throws ManifoldCFException;
}
