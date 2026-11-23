package org.apache.manifoldcf.core.lockmanager;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.apache.manifoldcf.core.interfaces.ILockManager;
import org.apache.manifoldcf.core.interfaces.IServiceCleanup;
import org.apache.manifoldcf.core.interfaces.IServiceDataAcceptor;
import org.apache.manifoldcf.core.interfaces.LockException;
import org.apache.manifoldcf.core.interfaces.ManifoldCFConfiguration;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.Logging;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class BaseLockManager implements ILockManager {
   public static final String _rcsid = "@(#)$Id: LockManager.java 988245 2010-08-23 18:39:35Z kwright $";
   protected static final int TYPE_READ = 1;
   protected static final int TYPE_WRITENONEX = 2;
   protected static final int TYPE_WRITE = 3;
   protected final Long threadID = new Long(Thread.currentThread().getId());
   protected final LocalLockPool localLocks = new LocalLockPool();
   protected static final LockPool myLocks = new LockPool(new LockObjectFactory());
   protected final LocalLockPool localSections = new LocalLockPool();
   protected static final LockPool mySections = new LockPool(new LockObjectFactory());
   protected static final Map<String, Boolean> globalFlags = new HashMap();
   protected static final Map<String, byte[]> globalData = new HashMap();
   protected static final String serviceTypeLockPrefix = "_SERVICELOCK_";
   protected static final String serviceListPrefix = "_SERVICELIST_";
   protected static final String servicePrefix = "_SERVICE_";
   protected static final String activePrefix = "_ACTIVE_";
   protected static final String serviceDataPrefix = "_SERVICEDATA_";
   protected static final String anonymousServiceNamePrefix = "_ANON_";
   protected static final String anonymousServiceTypeCounter = "_SERVICECOUNTER_";

   public BaseLockManager() throws ManifoldCFException {
   }

   public String registerServiceBeginServiceActivity(String serviceType, String serviceName, IServiceCleanup cleanup) throws ManifoldCFException {
      return this.registerServiceBeginServiceActivity(serviceType, serviceName, (byte[])null, cleanup);
   }

   public String registerServiceBeginServiceActivity(String serviceType, String serviceName, byte[] initialData, IServiceCleanup cleanup) throws ManifoldCFException {
      String serviceTypeLockName = buildServiceTypeLockName(serviceType);
      this.enterWriteLock(serviceTypeLockName);

      try {
         if (serviceName == null) {
            serviceName = this.constructUniqueServiceName(serviceType);
         }

         String serviceActiveFlag = makeActiveServiceFlagName(serviceType, serviceName);
         if (this.checkGlobalFlag(serviceActiveFlag)) {
            throw new ManifoldCFException("Service '" + serviceName + "' of type '" + serviceType + "' is already active");
         } else {
            boolean foundService = false;
            boolean foundActiveService = false;
            int i = 0;

            while(true) {
               String resourceName = buildServiceListEntry(serviceType, i);
               String x = this.readServiceName(resourceName);
               if (x == null) {
                  boolean unregisterAll = false;
                  if (cleanup != null) {
                     if (i == 0) {
                        cleanup.cleanUpAllServices();
                        cleanup.clusterInit();
                     } else if (foundService && foundActiveService) {
                        cleanup.cleanUpService(serviceName);
                     } else if (!foundActiveService) {
                        cleanup.cleanUpAllServices();
                        cleanup.clusterInit();
                        unregisterAll = true;
                     }
                  }

                  if (unregisterAll) {
                     int k = i;

                     while(k > 0) {
                        --k;
                        resourceName = buildServiceListEntry(serviceType, k);
                        String x = this.readServiceName(resourceName);
                        this.clearGlobalFlag(makeRegisteredServiceFlagName(serviceType, x));
                        this.writeServiceName(resourceName, (String)null);
                     }

                     foundService = false;
                  }

                  if (!foundService) {
                     this.writeServiceName(resourceName, serviceName);

                     try {
                        this.setGlobalFlag(makeRegisteredServiceFlagName(serviceType, serviceName));
                     } catch (Throwable e) {
                        this.writeServiceName(resourceName, (String)null);
                        if (e instanceof Error) {
                           throw (Error)e;
                        }

                        if (e instanceof RuntimeException) {
                           throw (RuntimeException)e;
                        }

                        if (e instanceof ManifoldCFException) {
                           throw (ManifoldCFException)e;
                        }

                        throw new RuntimeException("Unknown exception of type: " + e.getClass().getName() + ": " + e.getMessage(), e);
                     }
                  }

                  this.setGlobalFlag(serviceActiveFlag);
                  this.writeServiceData(serviceType, serviceName, initialData);
                  String var20 = serviceName;
                  return var20;
               }

               if (x.equals(serviceName)) {
                  foundService = true;
               } else if (this.checkGlobalFlag(makeActiveServiceFlagName(serviceType, x))) {
                  foundActiveService = true;
               }

               ++i;
            }
         }
      } finally {
         this.leaveWriteLock(serviceTypeLockName);
      }
   }

   public void updateServiceData(String serviceType, String serviceName, byte[] serviceData) throws ManifoldCFException {
      String serviceTypeLockName = buildServiceTypeLockName(serviceType);
      this.enterWriteLock(serviceTypeLockName);

      try {
         String serviceActiveFlag = makeActiveServiceFlagName(serviceType, serviceName);
         if (!this.checkGlobalFlag(serviceActiveFlag)) {
            throw new ManifoldCFException("Service '" + serviceName + "' of type '" + serviceType + "' is not active");
         }

         this.writeServiceData(serviceType, serviceName, serviceData);
      } finally {
         this.leaveWriteLock(serviceTypeLockName);
      }

   }

   public byte[] retrieveServiceData(String serviceType, String serviceName) throws ManifoldCFException {
      String serviceTypeLockName = buildServiceTypeLockName(serviceType);
      this.enterReadLock(serviceTypeLockName);

      byte[] rval;
      try {
         String serviceActiveFlag = makeActiveServiceFlagName(serviceType, serviceName);
         if (this.checkGlobalFlag(serviceActiveFlag)) {
            rval = this.readServiceData(serviceType, serviceName);
            if (rval == null) {
               rval = new byte[0];
            }

            byte[] var6 = rval;
            return var6;
         }

         rval = null;
      } finally {
         this.leaveReadLock(serviceTypeLockName);
      }

      return rval;
   }

   public void scanServiceData(String serviceType, IServiceDataAcceptor dataAcceptor) throws ManifoldCFException {
      String serviceTypeLockName = buildServiceTypeLockName(serviceType);
      this.enterReadLock(serviceTypeLockName);

      try {
         int i = 0;

         while(true) {
            String resourceName = buildServiceListEntry(serviceType, i);
            String x = this.readServiceName(resourceName);
            if (x == null) {
               break;
            }

            if (this.checkGlobalFlag(makeActiveServiceFlagName(serviceType, x))) {
               byte[] serviceData = this.readServiceData(serviceType, x);
               if (dataAcceptor.acceptServiceData(x, serviceData)) {
                  break;
               }
            }

            ++i;
         }
      } finally {
         this.leaveReadLock(serviceTypeLockName);
      }

   }

   public int countActiveServices(String serviceType) throws ManifoldCFException {
      String serviceTypeLockName = buildServiceTypeLockName(serviceType);
      this.enterReadLock(serviceTypeLockName);

      try {
         int count = 0;
         int i = 0;

         while(true) {
            String resourceName = buildServiceListEntry(serviceType, i);
            String x = this.readServiceName(resourceName);
            if (x == null) {
               int var10 = count;
               return var10;
            }

            if (this.checkGlobalFlag(makeActiveServiceFlagName(serviceType, x))) {
               ++count;
            }

            ++i;
         }
      } finally {
         this.leaveReadLock(serviceTypeLockName);
      }
   }

   public boolean cleanupInactiveService(String serviceType, IServiceCleanup cleanup) throws ManifoldCFException {
      String serviceTypeLockName = buildServiceTypeLockName(serviceType);
      this.enterWriteLock(serviceTypeLockName);

      try {
         int i = 0;

         while(true) {
            String resourceName = buildServiceListEntry(serviceType, i);
            String serviceName = this.readServiceName(resourceName);
            if (serviceName == null) {
               boolean var16 = true;
               return var16;
            }

            if (!this.checkGlobalFlag(makeActiveServiceFlagName(serviceType, serviceName))) {
               cleanup.cleanUpService(serviceName);
               String serviceRegisteredFlag = makeRegisteredServiceFlagName(serviceType, serviceName);
               int k = i + 1;
               String lastResourceName = null;
               String lastServiceName = null;

               while(true) {
                  String rName = buildServiceListEntry(serviceType, k);
                  String x = this.readServiceName(rName);
                  if (x == null) {
                     this.clearGlobalFlag(serviceRegisteredFlag);
                     if (lastServiceName != null) {
                        this.writeServiceName(resourceName, lastServiceName);
                     }

                     this.writeServiceName(lastResourceName, (String)null);
                     boolean var17 = false;
                     return var17;
                  }

                  lastResourceName = rName;
                  lastServiceName = x;
                  ++k;
               }
            }

            ++i;
         }
      } finally {
         this.leaveWriteLock(serviceTypeLockName);
      }
   }

   public void endServiceActivity(String serviceType, String serviceName) throws ManifoldCFException {
      String serviceTypeLockName = buildServiceTypeLockName(serviceType);
      this.enterWriteLock(serviceTypeLockName);

      try {
         String serviceActiveFlag = makeActiveServiceFlagName(serviceType, serviceName);
         if (!this.checkGlobalFlag(serviceActiveFlag)) {
            throw new ManifoldCFException("Service '" + serviceName + "' of type '" + serviceType + " is not active");
         }

         this.deleteServiceData(serviceType, serviceName);
         this.clearGlobalFlag(serviceActiveFlag);
      } finally {
         this.leaveWriteLock(serviceTypeLockName);
      }

   }

   public boolean checkServiceActive(String serviceType, String serviceName) throws ManifoldCFException {
      String serviceTypeLockName = buildServiceTypeLockName(serviceType);
      this.enterReadLock(serviceTypeLockName);

      boolean var4;
      try {
         var4 = this.checkGlobalFlag(makeActiveServiceFlagName(serviceType, serviceName));
      } finally {
         this.leaveReadLock(serviceTypeLockName);
      }

      return var4;
   }

   protected String constructUniqueServiceName(String serviceType) throws ManifoldCFException {
      String serviceCounterName = makeServiceCounterName(serviceType);
      int serviceUID = this.readServiceCounter(serviceCounterName);
      this.writeServiceCounter(serviceCounterName, serviceUID + 1);
      return "_ANON_" + serviceUID;
   }

   protected static String makeServiceCounterName(String serviceType) {
      return "_SERVICECOUNTER_" + serviceType;
   }

   protected int readServiceCounter(String serviceCounterName) throws ManifoldCFException {
      byte[] serviceCounterData = this.readData(serviceCounterName);
      return serviceCounterData != null && serviceCounterData.length == 4 ? (serviceCounterData[0] & 255) + (serviceCounterData[1] << 8 & '\uff00') + (serviceCounterData[2] << 16 & 16711680) + (serviceCounterData[3] << 24 & -16777216) : 0;
   }

   protected void writeServiceCounter(String serviceCounterName, int counter) throws ManifoldCFException {
      byte[] serviceCounterData = new byte[4];
      serviceCounterData[0] = (byte)(counter & 255);
      serviceCounterData[1] = (byte)(counter >> 8 & 255);
      serviceCounterData[2] = (byte)(counter >> 16 & 255);
      serviceCounterData[3] = (byte)(counter >> 24 & 255);
      this.writeData(serviceCounterName, serviceCounterData);
   }

   protected void writeServiceData(String serviceType, String serviceName, byte[] serviceData) throws ManifoldCFException {
      this.writeData(makeServiceDataName(serviceType, serviceName), serviceData);
   }

   protected byte[] readServiceData(String serviceType, String serviceName) throws ManifoldCFException {
      return this.readData(makeServiceDataName(serviceType, serviceName));
   }

   protected void deleteServiceData(String serviceType, String serviceName) throws ManifoldCFException {
      this.writeServiceData(serviceType, serviceName, (byte[])null);
   }

   protected static String makeServiceDataName(String serviceType, String serviceName) {
      return "_SERVICEDATA_" + serviceType + "_" + serviceName;
   }

   protected static String makeActiveServiceFlagName(String serviceType, String serviceName) {
      return "_ACTIVE_" + serviceType + "_" + serviceName;
   }

   protected static String makeRegisteredServiceFlagName(String serviceType, String serviceName) {
      return "_SERVICE_" + serviceType + "_" + serviceName;
   }

   protected String readServiceName(String resourceName) throws ManifoldCFException {
      byte[] bytes = this.readData(resourceName);
      return bytes == null ? null : new String(bytes, StandardCharsets.UTF_8);
   }

   protected void writeServiceName(String resourceName, String serviceName) throws ManifoldCFException {
      this.writeData(resourceName, serviceName == null ? null : serviceName.getBytes(StandardCharsets.UTF_8));
   }

   protected static String buildServiceListEntry(String serviceType, int i) {
      return "_SERVICELIST_" + serviceType + "_" + i;
   }

   protected static String buildServiceTypeLockName(String serviceType) {
      return "_SERVICELOCK_" + serviceType;
   }

   public ManifoldCFConfiguration getSharedConfiguration() throws ManifoldCFException {
      return ManifoldCF.getConfiguration();
   }

   public void setGlobalFlag(String flagName) throws ManifoldCFException {
      synchronized(globalFlags) {
         globalFlags.put(flagName, new Boolean(true));
      }
   }

   public void clearGlobalFlag(String flagName) throws ManifoldCFException {
      synchronized(globalFlags) {
         globalFlags.remove(flagName);
      }
   }

   public boolean checkGlobalFlag(String flagName) throws ManifoldCFException {
      synchronized(globalFlags) {
         return globalFlags.get(flagName) != null;
      }
   }

   public byte[] readData(String resourceName) throws ManifoldCFException {
      synchronized(globalData) {
         return (byte[])globalData.get(resourceName);
      }
   }

   public void writeData(String resourceName, byte[] data) throws ManifoldCFException {
      synchronized(globalData) {
         if (data == null) {
            globalData.remove(resourceName);
         } else {
            globalData.put(resourceName, data);
         }

      }
   }

   public final void timedWait(int time) throws ManifoldCFException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Waiting for time " + Integer.toString(time));
      }

      try {
         ManifoldCF.sleep((long)time);
      } catch (InterruptedException e) {
         throw new ManifoldCFException("Interrupted", e, 2);
      }
   }

   protected LockPool getGlobalLockPool() {
      return myLocks;
   }

   public final void enterNonExWriteLock(String lockKey) throws ManifoldCFException {
      enterNonExWrite(this.threadID, lockKey, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void enterNonExWriteLockNoWait(String lockKey) throws ManifoldCFException, LockException {
      enterNonExWriteNoWait(this.threadID, lockKey, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void leaveNonExWriteLock(String lockKey) throws ManifoldCFException {
      leaveNonExWrite(lockKey, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void enterWriteLock(String lockKey) throws ManifoldCFException {
      enterWrite(this.threadID, lockKey, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void enterWriteLockNoWait(String lockKey) throws ManifoldCFException, LockException {
      enterWriteNoWait(this.threadID, lockKey, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void leaveWriteLock(String lockKey) throws ManifoldCFException {
      leaveWrite(lockKey, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void enterReadLock(String lockKey) throws ManifoldCFException {
      enterRead(this.threadID, lockKey, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void enterReadLockNoWait(String lockKey) throws ManifoldCFException, LockException {
      enterReadNoWait(this.threadID, lockKey, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void leaveReadLock(String lockKey) throws ManifoldCFException {
      leaveRead(lockKey, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void enterLocks(String[] readLocks, String[] nonExWriteLocks, String[] writeLocks) throws ManifoldCFException {
      enter(this.threadID, readLocks, nonExWriteLocks, writeLocks, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void enterLocksNoWait(String[] readLocks, String[] nonExWriteLocks, String[] writeLocks) throws ManifoldCFException, LockException {
      enterNoWait(this.threadID, readLocks, nonExWriteLocks, writeLocks, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void leaveLocks(String[] readLocks, String[] writeNonExLocks, String[] writeLocks) throws ManifoldCFException {
      leave(readLocks, writeNonExLocks, writeLocks, "lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void clearLocks() throws ManifoldCFException {
      clear("lock", this.localLocks, this.getGlobalLockPool());
   }

   public final void enterReadCriticalSection(String sectionKey) throws ManifoldCFException {
      enterRead(this.threadID, sectionKey, "critical section", this.localSections, mySections);
   }

   public final void leaveReadCriticalSection(String sectionKey) throws ManifoldCFException {
      leaveRead(sectionKey, "critical section", this.localSections, mySections);
   }

   public final void enterNonExWriteCriticalSection(String sectionKey) throws ManifoldCFException {
      enterNonExWrite(this.threadID, sectionKey, "critical section", this.localSections, mySections);
   }

   public final void leaveNonExWriteCriticalSection(String sectionKey) throws ManifoldCFException {
      leaveNonExWrite(sectionKey, "critical section", this.localSections, mySections);
   }

   public final void enterWriteCriticalSection(String sectionKey) throws ManifoldCFException {
      enterWrite(this.threadID, sectionKey, "critical section", this.localSections, mySections);
   }

   public final void leaveWriteCriticalSection(String sectionKey) throws ManifoldCFException {
      leaveWrite(sectionKey, "critical section", this.localSections, mySections);
   }

   public final void enterCriticalSections(String[] readSectionKeys, String[] nonExSectionKeys, String[] writeSectionKeys) throws ManifoldCFException {
      enter(this.threadID, readSectionKeys, nonExSectionKeys, writeSectionKeys, "critical section", this.localSections, mySections);
   }

   public final void leaveCriticalSections(String[] readSectionKeys, String[] nonExSectionKeys, String[] writeSectionKeys) throws ManifoldCFException {
      leave(readSectionKeys, nonExSectionKeys, writeSectionKeys, "critical section", this.localSections, mySections);
   }

   protected static void enterNonExWrite(Long threadID, String lockKey, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Entering non-ex write " + description + " '" + lockKey + "'");
      }

      LocalLock ll = localLocks.getLocalLock(lockKey);
      if (!ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
         if (ll.hasReadLock()) {
            throw new ManifoldCFException("Illegal " + description + " sequence: NonExWrite " + description + " can't be within read " + description, 0);
         } else {
            while(true) {
               LockGate lo = crossLocks.getObject(lockKey);

               try {
                  lo.enterNonExWriteLock(threadID);
                  break;
               } catch (InterruptedException e) {
                  throw new ManifoldCFException("Interrupted", e, 2);
               } catch (ExpiredObjectException var9) {
               }
            }

            ll.incrementNonExWriteLocks();
            if (Logging.lock.isDebugEnabled()) {
               Logging.lock.debug(" Successfully obtained " + description + "!");
            }

         }
      } else {
         ll.incrementNonExWriteLocks();
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained " + description + "!");
         }

      }
   }

   protected static void enterNonExWriteNoWait(Long threadID, String lockKey, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException, LockException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Entering non-ex write " + description + " no wait '" + lockKey + "'");
      }

      LocalLock ll = localLocks.getLocalLock(lockKey);
      if (!ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
         if (ll.hasReadLock()) {
            throw new ManifoldCFException("Illegal " + description + " sequence: NonExWrite " + description + " can't be within read " + description, 0);
         } else {
            while(true) {
               LockGate lo = crossLocks.getObject(lockKey);

               try {
                  synchronized(lo) {
                     lo.enterNonExWriteLockNoWait(threadID);
                     break;
                  }
               } catch (LocalLockException e) {
                  if (Logging.lock.isDebugEnabled()) {
                     Logging.lock.debug(" Could not non-ex write " + description + " '" + lockKey + "', lock exception");
                  }

                  throw new LockException(e.getMessage());
               } catch (InterruptedException e) {
                  throw new ManifoldCFException("Interrupted", e, 2);
               } catch (ExpiredObjectException var12) {
               }
            }

            ll.incrementNonExWriteLocks();
            if (Logging.lock.isDebugEnabled()) {
               Logging.lock.debug(" Successfully obtained " + description + "!");
            }

         }
      } else {
         ll.incrementNonExWriteLocks();
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained " + description + "!");
         }

      }
   }

   protected static void leaveNonExWrite(String lockKey, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Leaving non-ex write " + description + " '" + lockKey + "'");
      }

      LocalLock ll = localLocks.getLocalLock(lockKey);
      ll.decrementNonExWriteLocks();
      if (!ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
         while(true) {
            LockGate lo = crossLocks.getObject(lockKey);

            try {
               lo.leaveNonExWriteLock();
               break;
            } catch (InterruptedException var10) {
               InterruptedException e = var10;

               try {
                  lo.leaveNonExWriteLock();
                  throw new ManifoldCFException("Interrupted", e, 2);
               } catch (InterruptedException e2) {
                  ll.incrementNonExWriteLocks();
                  throw new ManifoldCFException("Interrupted", e2, 2);
               } catch (ExpiredObjectException var9) {
                  ll.incrementNonExWriteLocks();
                  throw new ManifoldCFException("Interrupted", var10, 2);
               }
            } catch (ExpiredObjectException var11) {
            }
         }

         localLocks.releaseLocalLock(lockKey);
      }

   }

   protected static void enterWrite(Long threadID, String lockKey, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Entering write " + description + " '" + lockKey + "'");
      }

      LocalLock ll = localLocks.getLocalLock(lockKey);
      if (ll.hasWriteLock()) {
         ll.incrementWriteLocks();
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained " + description + "!");
         }

      } else if (!ll.hasReadLock() && !ll.hasNonExWriteLock()) {
         while(true) {
            LockGate lo = crossLocks.getObject(lockKey);

            try {
               lo.enterWriteLock(threadID);
               break;
            } catch (InterruptedException e) {
               throw new ManifoldCFException("Interrupted", e, 2);
            } catch (ExpiredObjectException var9) {
            }
         }

         ll.incrementWriteLocks();
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained " + description + "!");
         }

      } else {
         throw new ManifoldCFException("Illegal " + description + " sequence: Write " + description + " can't be within read " + description + " or non-ex write " + description, 0);
      }
   }

   protected static void enterWriteNoWait(Long threadID, String lockKey, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException, LockException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Entering write " + description + " no wait '" + lockKey + "'");
      }

      LocalLock ll = localLocks.getLocalLock(lockKey);
      if (ll.hasWriteLock()) {
         ll.incrementWriteLocks();
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained " + description + "!");
         }

      } else if (!ll.hasReadLock() && !ll.hasNonExWriteLock()) {
         while(true) {
            LockGate lo = crossLocks.getObject(lockKey);

            try {
               synchronized(lo) {
                  lo.enterWriteLockNoWait(threadID);
                  break;
               }
            } catch (LocalLockException e) {
               if (Logging.lock.isDebugEnabled()) {
                  Logging.lock.debug(" Could not write " + description + " '" + lockKey + "', lock exception");
               }

               throw new LockException(e.getMessage());
            } catch (InterruptedException e) {
               throw new ManifoldCFException("Interrupted", e, 2);
            } catch (ExpiredObjectException var12) {
            }
         }

         ll.incrementWriteLocks();
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained " + description + "!");
         }

      } else {
         throw new ManifoldCFException("Illegal " + description + " sequence: Write " + description + " can't be within read " + description + " or non-ex write " + description, 0);
      }
   }

   protected static void leaveWrite(String lockKey, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Leaving write " + description + " '" + lockKey + "'");
      }

      LocalLock ll = localLocks.getLocalLock(lockKey);
      ll.decrementWriteLocks();
      if (!ll.hasWriteLock()) {
         while(true) {
            LockGate lo = crossLocks.getObject(lockKey);

            try {
               lo.leaveWriteLock();
               break;
            } catch (InterruptedException var10) {
               InterruptedException e = var10;

               try {
                  lo.leaveWriteLock();
                  throw new ManifoldCFException("Interrupted", e, 2);
               } catch (InterruptedException e2) {
                  ll.incrementWriteLocks();
                  throw new ManifoldCFException("Interrupted", e2, 2);
               } catch (ExpiredObjectException var9) {
                  ll.incrementWriteLocks();
                  throw new ManifoldCFException("Interrupted", var10, 2);
               }
            } catch (ExpiredObjectException var11) {
            }
         }

         localLocks.releaseLocalLock(lockKey);
      }

   }

   protected static void enterRead(Long threadID, String lockKey, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Entering read " + description + " '" + lockKey + "'");
      }

      LocalLock ll = localLocks.getLocalLock(lockKey);
      if (!ll.hasReadLock() && !ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
         while(true) {
            LockGate lo = crossLocks.getObject(lockKey);

            try {
               lo.enterReadLock(threadID);
               break;
            } catch (InterruptedException e) {
               throw new ManifoldCFException("Interrupted", e, 2);
            } catch (ExpiredObjectException var9) {
            }
         }

         ll.incrementReadLocks();
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained " + description + "!");
         }

      } else {
         ll.incrementReadLocks();
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained " + description + "!");
         }

      }
   }

   protected static void enterReadNoWait(Long threadID, String lockKey, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException, LockException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Entering read " + description + " no wait '" + lockKey + "'");
      }

      LocalLock ll = localLocks.getLocalLock(lockKey);
      if (!ll.hasReadLock() && !ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
         while(true) {
            LockGate lo = crossLocks.getObject(lockKey);

            try {
               synchronized(lo) {
                  lo.enterReadLockNoWait(threadID);
                  break;
               }
            } catch (LocalLockException e) {
               if (Logging.lock.isDebugEnabled()) {
                  Logging.lock.debug(" Could not read " + description + " '" + lockKey + "', lock exception");
               }

               throw new LockException(e.getMessage());
            } catch (InterruptedException e) {
               throw new ManifoldCFException("Interrupted", e, 2);
            } catch (ExpiredObjectException var12) {
            }
         }

         ll.incrementReadLocks();
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained " + description + "!");
         }

      } else {
         ll.incrementReadLocks();
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained " + description + "!");
         }

      }
   }

   protected static void leaveRead(String lockKey, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Leaving read " + description + " '" + lockKey + "'");
      }

      LocalLock ll = localLocks.getLocalLock(lockKey);
      ll.decrementReadLocks();
      if (!ll.hasReadLock() && !ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
         while(true) {
            LockGate lo = crossLocks.getObject(lockKey);

            try {
               lo.leaveReadLock();
               break;
            } catch (InterruptedException var10) {
               InterruptedException e = var10;

               try {
                  lo.leaveReadLock();
                  throw new ManifoldCFException("Interrupted", e, 2);
               } catch (InterruptedException e2) {
                  ll.incrementReadLocks();
                  throw new ManifoldCFException("Interrupted", e2, 2);
               } catch (ExpiredObjectException var9) {
                  ll.incrementReadLocks();
                  throw new ManifoldCFException("Interrupted", var10, 2);
               }
            } catch (ExpiredObjectException var11) {
            }
         }

         localLocks.releaseLocalLock(lockKey);
      }

   }

   protected static void clear(String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Clearing all " + description + "s");
      }

      for(String keyValue : localLocks.keySet()) {
         LocalLock ll = localLocks.getLocalLock(keyValue);

         while(ll.hasWriteLock()) {
            leaveWrite(keyValue, description, localLocks, crossLocks);
         }

         while(ll.hasNonExWriteLock()) {
            leaveNonExWrite(keyValue, description, localLocks, crossLocks);
         }

         while(ll.hasReadLock()) {
            leaveRead(keyValue, description, localLocks, crossLocks);
         }
      }

   }

   protected static void enter(Long threadID, String[] readLocks, String[] nonExWriteLocks, String[] writeLocks, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Entering multiple " + description + "s:");
         if (readLocks != null) {
            int i = 0;

            while(i < readLocks.length) {
               Logging.lock.debug(" Read " + description + " '" + readLocks[i++] + "'");
            }
         }

         if (nonExWriteLocks != null) {
            int i = 0;

            while(i < nonExWriteLocks.length) {
               Logging.lock.debug(" Non-ex write " + description + " '" + nonExWriteLocks[i++] + "'");
            }
         }

         if (writeLocks != null) {
            int i = 0;

            while(i < writeLocks.length) {
               Logging.lock.debug(" Write " + description + " '" + writeLocks[i++] + "'");
            }
         }
      }

      LockDescription[] lds = getSortedUniqueLocks(readLocks, nonExWriteLocks, writeLocks);
      int locksProcessed = 0;

      try {
         for(; locksProcessed < lds.length; ++locksProcessed) {
            LockDescription ld = lds[locksProcessed];
            int lockType = ld.getType();
            String lockKey = ld.getKey();
            switch (lockType) {
               case 1:
                  LocalLock ll = localLocks.getLocalLock(lockKey);
                  if (!ll.hasReadLock() && !ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
                     while(true) {
                        LockGate lo = crossLocks.getObject(lockKey);

                        try {
                           lo.enterReadLock(threadID);
                           break;
                        } catch (ExpiredObjectException var19) {
                        }
                     }
                  }

                  ll.incrementReadLocks();
                  break;
               case 2:
                  LocalLock ll = localLocks.getLocalLock(lockKey);
                  if (ll.hasReadLock() && !ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
                     throw new ManifoldCFException("Illegal " + description + " sequence: NonExWrite " + description + " can't be within read " + description, 0);
                  }

                  if (!ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
                     while(true) {
                        LockGate lo = crossLocks.getObject(lockKey);

                        try {
                           lo.enterNonExWriteLock(threadID);
                           break;
                        } catch (ExpiredObjectException var18) {
                        }
                     }
                  }

                  ll.incrementNonExWriteLocks();
                  break;
               case 3:
                  LocalLock ll = localLocks.getLocalLock(lockKey);
                  if ((ll.hasReadLock() || ll.hasNonExWriteLock()) && !ll.hasWriteLock()) {
                     throw new ManifoldCFException("Illegal " + description + " sequence: Write " + description + " can't be within read " + description + " or non-ex write " + description, 0);
                  }

                  if (!ll.hasWriteLock()) {
                     while(true) {
                        LockGate lo = crossLocks.getObject(lockKey);

                        try {
                           lo.enterWriteLock(threadID);
                           break;
                        } catch (ExpiredObjectException var17) {
                        }
                     }
                  }

                  ll.incrementWriteLocks();
            }
         }

         Logging.lock.debug(" Successfully obtained multiple " + description + "s!");
      } catch (Throwable ex) {
         ManifoldCFException ae = null;
         int errno = 0;

         while(true) {
            --locksProcessed;
            if (locksProcessed < 0) {
               if (ae != null) {
                  throw ae;
               } else if (ex instanceof ManifoldCFException) {
                  throw (ManifoldCFException)ex;
               } else if (ex instanceof InterruptedException) {
                  throw new ManifoldCFException("Interrupted", ex, 2);
               } else if (!(ex instanceof Error)) {
                  throw new Error("Unexpected exception", ex);
               } else {
                  throw (Error)ex;
               }
            }

            LockDescription ld = lds[locksProcessed];
            int lockType = ld.getType();
            String lockKey = ld.getKey();

            try {
               switch (lockType) {
                  case 1:
                     leaveRead(lockKey, description, localLocks, crossLocks);
                     break;
                  case 2:
                     leaveNonExWrite(lockKey, description, localLocks, crossLocks);
                     break;
                  case 3:
                     leaveWrite(lockKey, description, localLocks, crossLocks);
               }
            } catch (ManifoldCFException e) {
               ae = e;
            }
         }
      }
   }

   protected static void enterNoWait(Long threadID, String[] readLocks, String[] nonExWriteLocks, String[] writeLocks, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException, LockException {
      if (Logging.lock.isDebugEnabled()) {
         Logging.lock.debug("Entering multiple " + description + "s no wait:");
         if (readLocks != null) {
            int i = 0;

            while(i < readLocks.length) {
               Logging.lock.debug(" Read " + description + " '" + readLocks[i++] + "'");
            }
         }

         if (nonExWriteLocks != null) {
            int i = 0;

            while(i < nonExWriteLocks.length) {
               Logging.lock.debug(" Non-ex write " + description + " '" + nonExWriteLocks[i++] + "'");
            }
         }

         if (writeLocks != null) {
            int i = 0;

            while(i < writeLocks.length) {
               Logging.lock.debug(" Write " + description + " '" + writeLocks[i++] + "'");
            }
         }
      }

      LockDescription[] lds = getSortedUniqueLocks(readLocks, nonExWriteLocks, writeLocks);
      int locksProcessed = 0;

      try {
         for(; locksProcessed < lds.length; ++locksProcessed) {
            LockDescription ld = lds[locksProcessed];
            int lockType = ld.getType();
            String lockKey = ld.getKey();
            switch (lockType) {
               case 1:
                  LocalLock ll = localLocks.getLocalLock(lockKey);
                  if (!ll.hasReadLock() && !ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
                     while(true) {
                        LockGate lo = crossLocks.getObject(lockKey);
                        synchronized(lo) {
                           try {
                              lo.enterReadLockNoWait(threadID);
                              break;
                           } catch (ExpiredObjectException var24) {
                           }
                        }
                     }
                  }

                  ll.incrementReadLocks();
                  break;
               case 2:
                  LocalLock ll = localLocks.getLocalLock(lockKey);
                  if (ll.hasReadLock() && !ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
                     throw new ManifoldCFException("Illegal " + description + " sequence: NonExWrite " + description + " can't be within read " + description, 0);
                  }

                  if (!ll.hasNonExWriteLock() && !ll.hasWriteLock()) {
                     while(true) {
                        LockGate lo = crossLocks.getObject(lockKey);
                        synchronized(lo) {
                           try {
                              lo.enterNonExWriteLockNoWait(threadID);
                              break;
                           } catch (ExpiredObjectException var22) {
                           }
                        }
                     }
                  }

                  ll.incrementNonExWriteLocks();
                  break;
               case 3:
                  LocalLock ll = localLocks.getLocalLock(lockKey);
                  if ((ll.hasReadLock() || ll.hasNonExWriteLock()) && !ll.hasWriteLock()) {
                     throw new ManifoldCFException("Illegal " + description + " sequence: Write " + description + " can't be within read " + description + " or non-ex write " + description, 0);
                  }

                  if (!ll.hasWriteLock()) {
                     while(true) {
                        LockGate lo = crossLocks.getObject(lockKey);
                        synchronized(lo) {
                           try {
                              lo.enterWriteLockNoWait(threadID);
                              break;
                           } catch (ExpiredObjectException var20) {
                           }
                        }
                     }
                  }

                  ll.incrementWriteLocks();
            }
         }

         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug(" Successfully obtained multiple " + description + "s!");
         }

      } catch (Throwable var26) {
         ManifoldCFException ae = null;
         int errno = 0;

         while(true) {
            --locksProcessed;
            if (locksProcessed < 0) {
               if (ae != null) {
                  throw ae;
               } else if (var26 instanceof ManifoldCFException) {
                  throw (ManifoldCFException)var26;
               } else if (!(var26 instanceof LockException) && !(var26 instanceof LocalLockException)) {
                  if (var26 instanceof InterruptedException) {
                     throw new ManifoldCFException("Interrupted", var26, 2);
                  } else if (!(var26 instanceof Error)) {
                     throw new Error("Unexpected exception", var26);
                  } else {
                     throw (Error)var26;
                  }
               } else {
                  Logging.lock.debug(" Couldn't get " + description + "; throwing LockException");
                  throw new LockException(var26.getMessage());
               }
            }

            LockDescription ld = lds[locksProcessed];
            int lockType = ld.getType();
            String lockKey = ld.getKey();

            try {
               switch (lockType) {
                  case 1:
                     leaveRead(lockKey, description, localLocks, crossLocks);
                     break;
                  case 2:
                     leaveNonExWrite(lockKey, description, localLocks, crossLocks);
                     break;
                  case 3:
                     leaveWrite(lockKey, description, localLocks, crossLocks);
               }
            } catch (ManifoldCFException e) {
               ae = e;
            }
         }
      }
   }

   protected static void leave(String[] readLocks, String[] writeNonExLocks, String[] writeLocks, String description, LocalLockPool localLocks, LockPool crossLocks) throws ManifoldCFException {
      LockDescription[] lds = getSortedUniqueLocks(readLocks, writeNonExLocks, writeLocks);
      ManifoldCFException ae = null;
      int i = lds.length;

      while(true) {
         --i;
         if (i < 0) {
            if (ae != null) {
               throw ae;
            } else {
               return;
            }
         }

         LockDescription ld = lds[i];
         String lockKey = ld.getKey();
         int lockType = ld.getType();

         try {
            switch (lockType) {
               case 1:
                  leaveRead(lockKey, description, localLocks, crossLocks);
                  break;
               case 2:
                  leaveNonExWrite(lockKey, description, localLocks, crossLocks);
                  break;
               case 3:
                  leaveWrite(lockKey, description, localLocks, crossLocks);
            }
         } catch (ManifoldCFException e) {
            ae = e;
         }
      }
   }

   protected static LockDescription[] getSortedUniqueLocks(String[] readLocks, String[] writeNonExLocks, String[] writeLocks) {
      Map<String, LockDescription> ht = new HashMap();
      if (readLocks != null) {
         int i = 0;

         while(i < readLocks.length) {
            String key = readLocks[i++];
            LockDescription ld = (LockDescription)ht.get(key);
            if (ld == null) {
               ld = new LockDescription(1, key);
               ht.put(key, ld);
            } else {
               ld.set(1);
            }
         }
      }

      if (writeNonExLocks != null) {
         int i = 0;

         while(i < writeNonExLocks.length) {
            String key = writeNonExLocks[i++];
            LockDescription ld = (LockDescription)ht.get(key);
            if (ld == null) {
               ld = new LockDescription(2, key);
               ht.put(key, ld);
            } else {
               ld.set(2);
            }
         }
      }

      if (writeLocks != null) {
         int i = 0;

         while(i < writeLocks.length) {
            String key = writeLocks[i++];
            LockDescription ld = (LockDescription)ht.get(key);
            if (ld == null) {
               ld = new LockDescription(3, key);
               ht.put(key, ld);
            } else {
               ld.set(3);
            }
         }
      }

      LockDescription[] rval = new LockDescription[ht.size()];
      String[] sortarray = new String[ht.size()];
      int i = 0;

      for(String key : ht.keySet()) {
         sortarray[i++] = key;
      }

      Arrays.sort(sortarray);
      i = 0;

      for(String key : sortarray) {
         rval[i++] = (LockDescription)ht.get(key);
      }

      return rval;
   }

   protected static class LockDescription {
      protected int lockType;
      protected String lockKey;

      public LockDescription(int lockType, String lockKey) {
         this.lockType = lockType;
         this.lockKey = lockKey;
      }

      public void set(int lockType) {
         if (lockType > this.lockType) {
            this.lockType = lockType;
         }

      }

      public int getType() {
         return this.lockType;
      }

      public String getKey() {
         return this.lockKey;
      }
   }
}
