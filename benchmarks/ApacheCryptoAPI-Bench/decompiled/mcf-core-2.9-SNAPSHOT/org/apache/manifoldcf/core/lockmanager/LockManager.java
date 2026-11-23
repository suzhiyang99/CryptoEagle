package org.apache.manifoldcf.core.lockmanager;

import java.io.File;
import org.apache.manifoldcf.core.interfaces.ILockManager;
import org.apache.manifoldcf.core.interfaces.IServiceCleanup;
import org.apache.manifoldcf.core.interfaces.IServiceDataAcceptor;
import org.apache.manifoldcf.core.interfaces.LockException;
import org.apache.manifoldcf.core.interfaces.ManifoldCFConfiguration;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class LockManager implements ILockManager {
   public static final String _rcsid = "@(#)$Id: LockManager.java 988245 2010-08-23 18:39:35Z kwright $";
   protected final ILockManager lockManager;

   public LockManager() throws ManifoldCFException {
      File synchDirectory = FileLockManager.getSynchDirectoryProperty();
      if (synchDirectory != null) {
         this.lockManager = new FileLockManager(synchDirectory);
      } else {
         this.lockManager = new BaseLockManager();
      }

   }

   public String registerServiceBeginServiceActivity(String serviceType, String serviceName, IServiceCleanup cleanup) throws ManifoldCFException {
      return this.lockManager.registerServiceBeginServiceActivity(serviceType, serviceName, cleanup);
   }

   public String registerServiceBeginServiceActivity(String serviceType, String serviceName, byte[] initialData, IServiceCleanup cleanup) throws ManifoldCFException {
      return this.lockManager.registerServiceBeginServiceActivity(serviceType, serviceName, initialData, cleanup);
   }

   public void updateServiceData(String serviceType, String serviceName, byte[] serviceData) throws ManifoldCFException {
      this.lockManager.updateServiceData(serviceType, serviceName, serviceData);
   }

   public byte[] retrieveServiceData(String serviceType, String serviceName) throws ManifoldCFException {
      return this.lockManager.retrieveServiceData(serviceType, serviceName);
   }

   public void scanServiceData(String serviceType, IServiceDataAcceptor dataAcceptor) throws ManifoldCFException {
      this.lockManager.scanServiceData(serviceType, dataAcceptor);
   }

   public boolean cleanupInactiveService(String serviceType, IServiceCleanup cleanup) throws ManifoldCFException {
      return this.lockManager.cleanupInactiveService(serviceType, cleanup);
   }

   public int countActiveServices(String serviceType) throws ManifoldCFException {
      return this.lockManager.countActiveServices(serviceType);
   }

   public void endServiceActivity(String serviceType, String serviceName) throws ManifoldCFException {
      this.lockManager.endServiceActivity(serviceType, serviceName);
   }

   public boolean checkServiceActive(String serviceType, String serviceName) throws ManifoldCFException {
      return this.lockManager.checkServiceActive(serviceType, serviceName);
   }

   public ManifoldCFConfiguration getSharedConfiguration() throws ManifoldCFException {
      return this.lockManager.getSharedConfiguration();
   }

   public void setGlobalFlag(String flagName) throws ManifoldCFException {
      this.lockManager.setGlobalFlag(flagName);
   }

   public void clearGlobalFlag(String flagName) throws ManifoldCFException {
      this.lockManager.clearGlobalFlag(flagName);
   }

   public boolean checkGlobalFlag(String flagName) throws ManifoldCFException {
      return this.lockManager.checkGlobalFlag(flagName);
   }

   public byte[] readData(String resourceName) throws ManifoldCFException {
      return this.lockManager.readData(resourceName);
   }

   public void writeData(String resourceName, byte[] data) throws ManifoldCFException {
      this.lockManager.writeData(resourceName, data);
   }

   public void timedWait(int time) throws ManifoldCFException {
      this.lockManager.timedWait(time);
   }

   public void enterNonExWriteLock(String lockKey) throws ManifoldCFException {
      this.lockManager.enterNonExWriteLock(lockKey);
   }

   public void enterNonExWriteLockNoWait(String lockKey) throws ManifoldCFException, LockException {
      this.lockManager.enterNonExWriteLockNoWait(lockKey);
   }

   public void leaveNonExWriteLock(String lockKey) throws ManifoldCFException {
      this.lockManager.leaveNonExWriteLock(lockKey);
   }

   public void enterWriteLock(String lockKey) throws ManifoldCFException {
      this.lockManager.enterWriteLock(lockKey);
   }

   public void enterWriteLockNoWait(String lockKey) throws ManifoldCFException, LockException {
      this.lockManager.enterWriteLockNoWait(lockKey);
   }

   public void leaveWriteLock(String lockKey) throws ManifoldCFException {
      this.lockManager.leaveWriteLock(lockKey);
   }

   public void enterReadLock(String lockKey) throws ManifoldCFException {
      this.lockManager.enterReadLock(lockKey);
   }

   public void enterReadLockNoWait(String lockKey) throws ManifoldCFException, LockException {
      this.lockManager.enterReadLockNoWait(lockKey);
   }

   public void leaveReadLock(String lockKey) throws ManifoldCFException {
      this.lockManager.leaveReadLock(lockKey);
   }

   public void clearLocks() throws ManifoldCFException {
      this.lockManager.clearLocks();
   }

   public void enterLocks(String[] readLocks, String[] nonExWriteLocks, String[] writeLocks) throws ManifoldCFException {
      this.lockManager.enterLocks(readLocks, nonExWriteLocks, writeLocks);
   }

   public void enterLocksNoWait(String[] readLocks, String[] nonExWriteLocks, String[] writeLocks) throws ManifoldCFException, LockException {
      this.lockManager.enterLocksNoWait(readLocks, nonExWriteLocks, writeLocks);
   }

   public void leaveLocks(String[] readLocks, String[] writeNonExLocks, String[] writeLocks) throws ManifoldCFException {
      this.lockManager.leaveLocks(readLocks, writeNonExLocks, writeLocks);
   }

   public void enterReadCriticalSection(String sectionKey) throws ManifoldCFException {
      this.lockManager.enterReadCriticalSection(sectionKey);
   }

   public void leaveReadCriticalSection(String sectionKey) throws ManifoldCFException {
      this.lockManager.leaveReadCriticalSection(sectionKey);
   }

   public void enterNonExWriteCriticalSection(String sectionKey) throws ManifoldCFException {
      this.lockManager.enterNonExWriteCriticalSection(sectionKey);
   }

   public void leaveNonExWriteCriticalSection(String sectionKey) throws ManifoldCFException {
      this.lockManager.leaveNonExWriteCriticalSection(sectionKey);
   }

   public void enterWriteCriticalSection(String sectionKey) throws ManifoldCFException {
      this.lockManager.enterWriteCriticalSection(sectionKey);
   }

   public void leaveWriteCriticalSection(String sectionKey) throws ManifoldCFException {
      this.lockManager.leaveWriteCriticalSection(sectionKey);
   }

   public void enterCriticalSections(String[] readSectionKeys, String[] nonExSectionKeys, String[] writeSectionKeys) throws ManifoldCFException {
      this.lockManager.enterCriticalSections(readSectionKeys, nonExSectionKeys, writeSectionKeys);
   }

   public void leaveCriticalSections(String[] readSectionKeys, String[] nonExSectionKeys, String[] writeSectionKeys) throws ManifoldCFException {
      this.lockManager.leaveCriticalSections(readSectionKeys, nonExSectionKeys, writeSectionKeys);
   }
}
