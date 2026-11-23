package org.apache.manifoldcf.core.cachemanager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.manifoldcf.core.interfaces.ICacheClass;
import org.apache.manifoldcf.core.interfaces.ICacheCreateHandle;
import org.apache.manifoldcf.core.interfaces.ICacheDescription;
import org.apache.manifoldcf.core.interfaces.ICacheExecutor;
import org.apache.manifoldcf.core.interfaces.ICacheHandle;
import org.apache.manifoldcf.core.interfaces.ICacheManager;
import org.apache.manifoldcf.core.interfaces.ILockManager;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.LockManagerFactory;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.StringSet;
import org.apache.manifoldcf.core.interfaces.StringSetBuffer;
import org.apache.manifoldcf.core.system.Logging;

public class CacheManager implements ICacheManager {
   public static final String _rcsid = "@(#)$Id: CacheManager.java 988245 2010-08-23 18:39:35Z kwright $";
   protected static final String cacheLockPrefix = "_Cache_";
   protected ILockManager lockManager;
   protected static GeneralCache cache = new GeneralCache();
   protected HashMap transactionHash = new HashMap();

   public CacheManager(IThreadContext context) throws ManifoldCFException {
      this.lockManager = LockManagerFactory.make(context);
   }

   public void findObjectsAndExecute(ICacheDescription[] locateObjectDescriptions, StringSet invalidateKeys, ICacheExecutor execObject, String transactionID) throws ManifoldCFException {
      ICacheHandle handle = this.enterCache(locateObjectDescriptions, invalidateKeys, transactionID);

      try {
         if (locateObjectDescriptions != null) {
            HashMap allObjects = new HashMap();
            ICacheCreateHandle createHandle = this.enterCreateSection(handle);

            try {
               ArrayList createList = new ArrayList();
               int i = 0;

               while(i < locateObjectDescriptions.length) {
                  ICacheDescription objectDescription = locateObjectDescriptions[i++];
                  StringSet set = objectDescription.getObjectKeys();
                  if (set == null) {
                     createList.add(objectDescription);
                  } else {
                     Object o = this.lookupObject(createHandle, objectDescription);
                     if (o == null) {
                        createList.add(objectDescription);
                     } else {
                        allObjects.put(objectDescription, o);
                     }
                  }
               }

               ICacheDescription[] createDescriptions = new ICacheDescription[createList.size()];

               for(int var21 = 0; var21 < createList.size(); ++var21) {
                  createDescriptions[var21] = (ICacheDescription)createList.get(var21);
               }

               Object[] createdObjects = execObject.create(createDescriptions);
               if (createdObjects == null) {
                  return;
               }

               for(int var22 = 0; var22 < createdObjects.length; ++var22) {
                  this.saveObject(createHandle, createDescriptions[var22], createdObjects[var22]);
                  allObjects.put(createDescriptions[var22], createdObjects[var22]);
               }
            } finally {
               this.leaveCreateSection(createHandle);
            }

            int var23 = 0;

            while(var23 < locateObjectDescriptions.length) {
               ICacheDescription objectDescription = locateObjectDescriptions[var23++];
               Object o = allObjects.get(objectDescription);
               execObject.exists(objectDescription, o);
            }
         }

         if (execObject != null) {
            execObject.execute();
         }

         this.invalidateKeys(handle);
      } finally {
         this.leaveCache(handle);
      }
   }

   public ICacheHandle enterCache(ICacheDescription[] locateObjectDescriptions, StringSet invalidateKeys, String transactionID) throws ManifoldCFException {
      if (Logging.cache.isDebugEnabled()) {
         StringBuilder sb = new StringBuilder();
         if (locateObjectDescriptions == null) {
            sb.append("NULL");
         } else {
            sb.append("{");

            for(int i = 0; i < locateObjectDescriptions.length; ++i) {
               if (i > 0) {
                  sb.append(",");
               }

               sb.append(locateObjectDescriptions[i].getCriticalSectionName());
            }

            sb.append("}");
         }

         StringBuilder inv = new StringBuilder();
         if (invalidateKeys == null) {
            inv.append("NULL");
         } else {
            inv.append("{");
            boolean isFirst = true;

            for(Iterator iter = invalidateKeys.getKeys(); iter.hasNext(); inv.append((String)iter.next())) {
               if (isFirst) {
                  isFirst = false;
               } else {
                  inv.append(",");
               }
            }

            inv.append("}");
         }

         Logging.cache.debug("Entering cacher; objects = " + sb.toString() + " invalidate keys = " + inv.toString());
      }

      StringSetBuffer readLockTable = new StringSetBuffer();
      if (locateObjectDescriptions != null) {
         int i = 0;

         while(i < locateObjectDescriptions.length) {
            ICacheDescription objectDescription = locateObjectDescriptions[i++];
            StringSet keys = objectDescription.getObjectKeys();
            if (keys != null) {
               readLockTable.add(keys);
            }
         }
      }

      StringSet readKeys = new StringSet(readLockTable);
      CacheHandle ch;
      if (transactionID == null) {
         String[] writeLocks = null;
         if (invalidateKeys != null) {
            writeLocks = invalidateKeys.getArray("_Cache_");
         }

         String[] readLocks = readKeys.getArray("_Cache_");
         ch = new CacheHandle(readLocks, writeLocks, locateObjectDescriptions, invalidateKeys, transactionID);
         Logging.lock.debug("Starting cache outside transaction");
         this.lockManager.enterLocks(readLocks, (String[])null, writeLocks);
         Logging.lock.debug(" Done starting cache");
      } else {
         CacheTransactionHandle handle = (CacheTransactionHandle)this.transactionHash.get(transactionID);
         if (handle == null) {
            ManifoldCFException ex = new ManifoldCFException("Illegal transaction ID: '" + transactionID + "'", 0);
            Logging.cache.error(Thread.currentThread().toString() + ": enterCache: " + transactionID + ": " + this.toString() + ": Transaction hash = " + this.transactionHash.toString(), ex);
            throw ex;
         }

         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug("Starting cache in transaction " + transactionID);
         }

         ch = new CacheHandle((String[])null, (String[])null, locateObjectDescriptions, invalidateKeys, transactionID);
         StringSet newReadLocks = handle.getRemainingReadLocks(readKeys, invalidateKeys);
         StringSet newWriteLocks = handle.getRemainingWriteLocks(readKeys, invalidateKeys);
         this.lockManager.enterLocks(newReadLocks.getArray("_Cache_"), (String[])null, newWriteLocks.getArray("_Cache_"));
         handle.addLocks(newReadLocks, newWriteLocks);
      }

      if (Logging.cache.isDebugEnabled()) {
         Logging.cache.debug("Successfully entered cacher; handle = " + ch.toString());
      }

      return ch;
   }

   public ICacheCreateHandle enterCreateSection(ICacheHandle handle) throws ManifoldCFException {
      if (Logging.cache.isDebugEnabled()) {
         Logging.cache.debug("Entering cache create section; cache handle = " + handle.toString());
      }

      ICacheDescription[] locateObjectDescriptions = handle.getObjectDescriptions();
      if (locateObjectDescriptions == null) {
         throw new ManifoldCFException("Can't enter create section without objects to create", 0);
      } else {
         int i = 0;
         ArrayList writeCriticalSectionArray = new ArrayList();

         while(i < locateObjectDescriptions.length) {
            ICacheDescription objectDescription = locateObjectDescriptions[i++];
            StringSet set = objectDescription.getObjectKeys();
            if (set != null) {
               writeCriticalSectionArray.add(objectDescription.getCriticalSectionName());
            }
         }

         String[] writeCriticalSections = new String[writeCriticalSectionArray.size()];

         for(int var7 = 0; var7 < writeCriticalSectionArray.size(); ++var7) {
            writeCriticalSections[var7] = (String)writeCriticalSectionArray.get(var7);
         }

         CacheCreateHandle ch = new CacheCreateHandle(writeCriticalSections, handle.getTransactionID());
         this.lockManager.enterCriticalSections((String[])null, (String[])null, writeCriticalSections);
         if (Logging.cache.isDebugEnabled()) {
            Logging.cache.debug("Successfully entered cache create section for handle = " + handle.toString() + "; section handle = " + ch.toString());
         }

         return ch;
      }
   }

   public Object lookupObject(ICacheCreateHandle handle, ICacheDescription objectDescription) throws ManifoldCFException {
      if (handle == null) {
         throw new ManifoldCFException("Can't do lookup outside of create section", 0);
      } else {
         if (Logging.cache.isDebugEnabled()) {
            Logging.cache.debug("Looking up object in section handle = " + handle.toString() + "; object name = '" + objectDescription.getCriticalSectionName() + "'");
         }

         StringSet set = objectDescription.getObjectKeys();
         if (set == null) {
            return null;
         } else {
            String transactionID = handle.getTransactionID();
            if (transactionID != null) {
               CacheTransactionHandle transactionHandle = (CacheTransactionHandle)this.transactionHash.get(transactionID);
               if (transactionHandle == null) {
                  ManifoldCFException ex = new ManifoldCFException("Illegal transaction id", 0);
                  Logging.cache.error(Thread.currentThread().toString() + ": lookupObject: " + transactionID + ": " + this.toString() + ": Transaction hash = " + this.transactionHash.toString(), ex);
                  throw ex;
               }

               while(transactionHandle != null) {
                  Object q = transactionHandle.lookupObject(objectDescription);
                  if (q != null) {
                     if (Logging.cache.isDebugEnabled()) {
                        Logging.cache.debug(" Object '" + objectDescription.getCriticalSectionName() + "' found in transaction cache");
                     }

                     return q;
                  }

                  if (transactionHandle.checkCacheKeys(set)) {
                     return null;
                  }

                  transactionHandle = transactionHandle.getParentTransaction();
               }
            }

            Object o = cache.lookup(objectDescription);
            if (o == null) {
               return null;
            } else {
               if (Logging.cache.isDebugEnabled()) {
                  Logging.cache.debug(" Object '" + objectDescription.getCriticalSectionName() + "' exists locally; checking if local copy is valid");
               }

               long expireTime = cache.getObjectExpirationTime(objectDescription);
               if (expireTime != -1L && expireTime <= handle.getLookupTime()) {
                  cache.deleteObject(objectDescription);
                  return null;
               } else {
                  long createTime = cache.getObjectCreationTime(objectDescription);
                  StringSet keys = cache.getObjectInvalidationKeys(objectDescription);
                  Iterator iter = keys.getKeys();

                  while(iter.hasNext()) {
                     String key = (String)iter.next();
                     if (this.hasExpired(key, createTime)) {
                        cache.deleteObject(objectDescription);
                        return null;
                     }
                  }

                  if (Logging.cache.isDebugEnabled()) {
                     Logging.cache.debug(" Object '" + objectDescription.getCriticalSectionName() + "' is valid; resetting local expiration");
                  }

                  this.resetObjectExpiration(objectDescription, handle.getLookupTime());
                  return o;
               }
            }
         }
      }
   }

   protected boolean hasExpired(String key, long createTime) throws ManifoldCFException {
      long createdDate = this.readSharedData(key);
      if (Logging.cache.isDebugEnabled()) {
         Logging.cache.debug(" Checking whether our cached copy of object with key = " + key + " has been invalidated.  It has create time " + (new Long(createTime)).toString() + ", and the last change is " + (new Long(createdDate)).toString());
      }

      if (createdDate == 0L) {
         return false;
      } else {
         return createdDate >= createTime;
      }
   }

   protected void resetObjectExpiration(ICacheDescription objectDescription, long currentTime) {
      long expireInterval = objectDescription.getObjectExpirationTime(currentTime);
      cache.setObjectExpiration(objectDescription, expireInterval);
      ICacheClass objectClass = objectDescription.getObjectClass();
      if (objectClass != null) {
         cache.setObjectClass(objectDescription, objectClass.getClassName(), objectClass.getMaxLRUCount());
      } else {
         cache.setObjectClass(objectDescription, (String)null, Integer.MAX_VALUE);
      }

   }

   public void saveObject(ICacheCreateHandle handle, ICacheDescription objectDescription, Object object) throws ManifoldCFException {
      if (handle == null) {
         throw new ManifoldCFException("Can't do save outside of create section", 0);
      } else {
         if (Logging.cache.isDebugEnabled()) {
            StringSet ks = objectDescription.getObjectKeys();
            StringBuilder sb = new StringBuilder();
            if (ks == null) {
               sb.append("NULL");
            } else {
               sb.append("{");
               Iterator iter = ks.getKeys();

               for(boolean isFirst = true; iter.hasNext(); sb.append((String)iter.next())) {
                  if (isFirst) {
                     isFirst = false;
                  } else {
                     sb.append(",");
                  }
               }

               sb.append("}");
            }

            Logging.cache.debug("Saving new object in section handle = " + handle.toString() + "; object description = '" + objectDescription.getCriticalSectionName() + "; cache keys = " + sb.toString());
         }

         StringSet keys = objectDescription.getObjectKeys();
         if (keys != null) {
            String transactionID = handle.getTransactionID();
            if (transactionID == null) {
               cache.setObject(objectDescription, object, keys, handle.getLookupTime());
               this.resetObjectExpiration(objectDescription, handle.getLookupTime());
            } else {
               CacheTransactionHandle transactionHandle = (CacheTransactionHandle)this.transactionHash.get(transactionID);
               if (transactionHandle == null) {
                  ManifoldCFException ex = new ManifoldCFException("Bad transaction handle", 0);
                  Logging.cache.error(Thread.currentThread().toString() + ": saveObject: " + transactionID + ": " + this.toString() + ": Transaction hash = " + this.transactionHash.toString(), ex);
                  throw ex;
               }

               transactionHandle.saveObject(objectDescription, object, keys);
            }
         }

      }
   }

   public void leaveCreateSection(ICacheCreateHandle handle) throws ManifoldCFException {
      if (Logging.cache.isDebugEnabled()) {
         Logging.cache.debug("Leaving cache create section; section handle = " + handle.toString());
      }

      this.lockManager.leaveCriticalSections((String[])null, (String[])null, handle.getCriticalSectionNames());
   }

   public void invalidateKeys(ICacheHandle handle) throws ManifoldCFException {
      if (Logging.cache.isDebugEnabled()) {
         Logging.cache.debug("Invalidating keys; handle = " + handle.toString());
      }

      StringSet invalidateKeys = handle.getInvalidationKeys();
      String transactionID = handle.getTransactionID();
      if (transactionID == null) {
         this.performInvalidation(invalidateKeys);
      } else {
         CacheTransactionHandle transactionHandle = (CacheTransactionHandle)this.transactionHash.get(transactionID);
         if (transactionHandle == null) {
            ManifoldCFException ex = new ManifoldCFException("Bad transaction ID!", 0);
            Logging.cache.error(Thread.currentThread().toString() + ": invalidateKeys: " + transactionID + ": " + this.toString() + ": Transaction hash = " + this.transactionHash.toString(), ex);
            throw ex;
         }

         transactionHandle.invalidateKeys(invalidateKeys);
      }

   }

   protected void performInvalidation(StringSet keys) throws ManifoldCFException {
      if (keys != null) {
         long invalidationTime = System.currentTimeMillis();

         String keyName;
         for(Iterator iter = keys.getKeys(); iter.hasNext(); this.writeSharedData(keyName, invalidationTime)) {
            keyName = (String)iter.next();
            if (Logging.cache.isDebugEnabled()) {
               Logging.cache.debug(" Invalidating key = " + keyName + " as of time = " + (new Long(invalidationTime)).toString());
            }
         }

         cache.invalidateKeys(keys);
      }

   }

   public void leaveCache(ICacheHandle handle) throws ManifoldCFException {
      if (Logging.cache.isDebugEnabled()) {
         Logging.cache.debug("Leaving cacher; handle = " + handle.toString());
      }

      this.lockManager.leaveLocks(handle.getReadLockStrings(), (String[])null, handle.getWriteLockStrings());
   }

   public void startTransaction(String startingTransactionID, String enclosingTransactionID) throws ManifoldCFException {
      if (Logging.cache.isDebugEnabled()) {
         Logging.cache.debug(Thread.currentThread().toString() + ": Starting transaction: " + startingTransactionID + ": " + this.toString() + ": " + this.transactionHash.toString());
      }

      CacheTransactionHandle parent;
      if (enclosingTransactionID == null) {
         parent = null;
      } else {
         parent = (CacheTransactionHandle)this.transactionHash.get(enclosingTransactionID);
         if (parent == null) {
            ManifoldCFException ex = new ManifoldCFException("Illegal parent transaction ID: " + enclosingTransactionID, 0);
            Logging.cache.error(Thread.currentThread().toString() + ": startTransaction: " + this.toString() + ": Transaction hash = " + this.transactionHash.toString(), ex);
            throw ex;
         }
      }

      this.transactionHash.put(startingTransactionID, new CacheTransactionHandle(parent));
      if (Logging.cache.isDebugEnabled()) {
         Logging.cache.debug("Successfully created transaction: " + startingTransactionID + ": " + this.toString() + ": " + this.transactionHash.toString());
      }

   }

   public void commitTransaction(String transactionID) throws ManifoldCFException {
      if (Logging.cache.isDebugEnabled()) {
         Logging.cache.debug(Thread.currentThread().toString() + ": Committing transaction: " + transactionID + ": " + this.toString() + ": " + this.transactionHash.toString());
      }

      CacheTransactionHandle handle = (CacheTransactionHandle)this.transactionHash.get(transactionID);
      if (handle == null) {
         throw new ManifoldCFException("Cache manager: commit transaction without start!", 0);
      } else {
         CacheTransactionHandle parentTransaction = handle.getParentTransaction();
         Iterator iter = handle.getCurrentObjects();
         StringSet invalidationKeys = handle.getInvalidationKeys();
         if (parentTransaction == null) {
            this.performInvalidation(invalidationKeys);
            long currentTime = System.currentTimeMillis();

            while(iter.hasNext()) {
               ICacheDescription desc = (ICacheDescription)iter.next();
               Object o = handle.lookupObject(desc);
               cache.setObject(desc, o, desc.getObjectKeys(), currentTime);
               this.resetObjectExpiration(desc, currentTime);
            }

            if (Logging.lock.isDebugEnabled()) {
               Logging.lock.debug("Ending transaction write locks for transaction " + transactionID);
            }

            this.lockManager.leaveLocks((String[])null, (String[])null, handle.getWriteLocks().getArray("_Cache_"));
            if (Logging.lock.isDebugEnabled()) {
               Logging.lock.debug("Ending transaction read locks for transaction " + transactionID);
            }

            this.lockManager.leaveLocks(handle.getReadLocks().getArray("_Cache_"), (String[])null, (String[])null);
            if (Logging.lock.isDebugEnabled()) {
               Logging.lock.debug("Done ending " + transactionID);
            }
         } else {
            parentTransaction.invalidateKeys(invalidationKeys);

            while(iter.hasNext()) {
               ICacheDescription desc = (ICacheDescription)iter.next();
               Object o = handle.lookupObject(desc);
               parentTransaction.saveObject(desc, o, desc.getObjectKeys());
            }

            parentTransaction.addLocks(handle.getReadLocks(), handle.getWriteLocks());
         }

         this.transactionHash.remove(transactionID);
      }
   }

   public void rollbackTransaction(String transactionID) throws ManifoldCFException {
      if (Logging.cache.isDebugEnabled()) {
         Logging.cache.debug(Thread.currentThread().toString() + ": Rolling back transaction: " + transactionID + ": " + this.toString() + ": " + this.transactionHash.toString());
      }

      CacheTransactionHandle handle = (CacheTransactionHandle)this.transactionHash.get(transactionID);
      if (handle == null) {
         throw new ManifoldCFException("Cache manager: rollback transaction without start!", 0);
      } else {
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug("Ending rollback write locks for transaction " + transactionID);
         }

         this.lockManager.leaveLocks((String[])null, (String[])null, handle.getWriteLocks().getArray("_Cache_"));
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug("Ending rollback read locks for transaction " + transactionID);
         }

         this.lockManager.leaveLocks(handle.getReadLocks().getArray("_Cache_"), (String[])null, (String[])null);
         if (Logging.lock.isDebugEnabled()) {
            Logging.lock.debug("Done rolling back " + transactionID);
         }

         this.transactionHash.remove(transactionID);
      }
   }

   public void expireObjects(long currentTimestamp) throws ManifoldCFException {
      cache.expireRecords(currentTimestamp);
   }

   protected long readSharedData(String key) throws ManifoldCFException {
      byte[] cacheResourceData = this.lockManager.readData("cache-" + key);
      if (cacheResourceData == null) {
         return 0L;
      } else {
         String expiration = new String(cacheResourceData, StandardCharsets.UTF_8);
         return new Long(expiration);
      }
   }

   protected void writeSharedData(String key, long value) throws ManifoldCFException {
      if (value == 0L) {
         this.lockManager.writeData(key, (byte[])null);
      } else {
         this.lockManager.writeData("cache-" + key, Long.toString(value).getBytes(StandardCharsets.UTF_8));
      }

   }

   protected class CacheHandle implements ICacheHandle {
      protected String[] readLocks;
      protected String[] writeLocks;
      protected ICacheDescription[] objectDescriptions;
      protected StringSet invalidationKeys;
      protected String transactionID;

      public CacheHandle(String[] readLocks, String[] writeLocks, ICacheDescription[] descriptions, StringSet invalidationKeys, String transactionID) {
         this.readLocks = readLocks;
         this.writeLocks = writeLocks;
         this.objectDescriptions = descriptions;
         this.invalidationKeys = invalidationKeys;
         this.transactionID = transactionID;
      }

      public String[] getReadLockStrings() {
         return this.readLocks;
      }

      public String[] getWriteLockStrings() {
         return this.writeLocks;
      }

      public ICacheDescription[] getObjectDescriptions() {
         return this.objectDescriptions;
      }

      public StringSet getInvalidationKeys() {
         return this.invalidationKeys;
      }

      public String getTransactionID() {
         return this.transactionID;
      }
   }

   protected class CacheCreateHandle implements ICacheCreateHandle {
      protected String[] criticalSectionNames;
      protected long theTime;
      protected String transactionID;

      public CacheCreateHandle(String[] criticalSectionNames, String transactionID) {
         this.criticalSectionNames = criticalSectionNames;
         this.theTime = System.currentTimeMillis();
         this.transactionID = transactionID;
      }

      public String[] getCriticalSectionNames() {
         return this.criticalSectionNames;
      }

      public long getLookupTime() {
         return this.theTime;
      }

      public String getTransactionID() {
         return this.transactionID;
      }
   }

   protected class CacheTransactionHandle {
      protected CacheTransactionHandle parentTransaction;
      protected HashMap objectHash = new HashMap();
      protected HashMap cacheKeyMap = new HashMap();
      protected HashMap cacheKeyReadLocks = new HashMap();
      protected HashMap cacheKeyWriteLocks = new HashMap();
      protected StringSetBuffer invalidationKeys = new StringSetBuffer();

      public CacheTransactionHandle(CacheTransactionHandle parentTransaction) {
         this.parentTransaction = parentTransaction;
      }

      public CacheTransactionHandle getParentTransaction() {
         return this.parentTransaction;
      }

      public StringSet getWriteLocks() {
         return new StringSet(this.cacheKeyWriteLocks);
      }

      public StringSet getReadLocks() {
         return new StringSet(this.cacheKeyReadLocks);
      }

      public StringSet getInvalidationKeys() {
         return new StringSet(this.invalidationKeys);
      }

      public Object lookupObject(ICacheDescription descriptionObject) {
         return this.objectHash.get(descriptionObject);
      }

      public void saveObject(ICacheDescription descriptionObject, Object object, StringSet cacheKeys) {
         this.objectHash.put(descriptionObject, object);

         HashMap thisHash;
         for(Iterator iter = cacheKeys.getKeys(); iter.hasNext(); thisHash.put(descriptionObject, descriptionObject)) {
            String key = (String)iter.next();
            thisHash = (HashMap)this.cacheKeyMap.get(key);
            if (thisHash == null) {
               thisHash = new HashMap();
               this.cacheKeyMap.put(key, thisHash);
            }
         }

      }

      public void invalidateKeys(StringSet keys) {
         if (keys != null) {
            this.invalidationKeys.add(keys);
            Iterator iter = keys.getKeys();

            while(iter.hasNext()) {
               String keyName = (String)iter.next();
               HashMap x = (HashMap)this.cacheKeyMap.get(keyName);
               if (x != null) {
                  for(ICacheDescription desc : x.keySet()) {
                     this.objectHash.remove(desc);
                  }

                  this.cacheKeyMap.remove(keyName);
               }
            }

         }
      }

      public boolean checkCacheKeys(StringSet cacheKeys) {
         return this.invalidationKeys.contains(cacheKeys);
      }

      public StringSet getRemainingReadLocks(StringSet cacheKeys, StringSet keys) {
         StringSetBuffer accumulator = new StringSetBuffer();
         if (cacheKeys != null) {
            Iterator iter = cacheKeys.getKeys();

            while(iter.hasNext()) {
               String cacheKey = (String)iter.next();
               if (keys == null || !keys.contains(cacheKey)) {
                  CacheTransactionHandle ptr = this;

                  boolean found;
                  for(found = false; ptr != null; ptr = ptr.parentTransaction) {
                     if (ptr.cacheKeyReadLocks.get(cacheKey) != null || ptr.cacheKeyWriteLocks.get(cacheKey) != null) {
                        found = true;
                        break;
                     }
                  }

                  if (!found) {
                     accumulator.add(cacheKey);
                  }
               }
            }
         }

         return new StringSet(accumulator);
      }

      public StringSet getRemainingWriteLocks(StringSet cacheKeys, StringSet keys) throws ManifoldCFException {
         StringSetBuffer accumulator = new StringSetBuffer();
         if (keys != null) {
            Iterator iter = keys.getKeys();

            while(iter.hasNext()) {
               String invKey = (String)iter.next();
               CacheTransactionHandle ptr = this;

               boolean found;
               for(found = false; ptr != null; ptr = ptr.parentTransaction) {
                  if (ptr.cacheKeyWriteLocks.get(invKey) != null) {
                     found = true;
                     break;
                  }

                  if (ptr.cacheKeyReadLocks.get(invKey) != null) {
                     break;
                  }
               }

               if (!found) {
                  accumulator.add(invKey);
               }
            }
         }

         return new StringSet(accumulator);
      }

      public void addLocks(StringSet thrownReadLocks, StringSet thrownWriteLocks) {
         Iterator iter = thrownReadLocks.getKeys();

         while(iter.hasNext()) {
            String x = (String)iter.next();
            this.cacheKeyReadLocks.put(x, x);
         }

         iter = thrownWriteLocks.getKeys();

         while(iter.hasNext()) {
            String x = (String)iter.next();
            this.cacheKeyWriteLocks.put(x, x);
         }

      }

      public Iterator getCurrentObjects() {
         return this.objectHash.keySet().iterator();
      }
   }
}
