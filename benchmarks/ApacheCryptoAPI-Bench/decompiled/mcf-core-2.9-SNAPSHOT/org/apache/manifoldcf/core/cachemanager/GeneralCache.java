package org.apache.manifoldcf.core.cachemanager;

import java.util.HashMap;
import java.util.Iterator;
import org.apache.manifoldcf.core.interfaces.StringSet;

public class GeneralCache {
   public static final String _rcsid = "@(#)$Id: GeneralCache.java 988245 2010-08-23 18:39:35Z kwright $";
   protected ObjectRecordTable hashtable = new ObjectRecordTable();
   protected InvalidationTable invalidationTable = new InvalidationTable();
   protected ObjectClassTable objectClassTable = new ObjectClassTable();
   public ExpirationTree expirationTree = new ExpirationTree();

   public synchronized Object lookup(Object objectDescription) {
      ObjectRecord o = this.hashtable.lookup(objectDescription);
      return o == null ? null : o.getObject();
   }

   public synchronized long getObjectCreationTime(Object objectDescription) {
      ObjectRecord o = this.hashtable.lookup(objectDescription);
      return o == null ? -1L : o.getCreationTime();
   }

   public synchronized StringSet getObjectInvalidationKeys(Object objectDescription) {
      ObjectRecord o = this.hashtable.lookup(objectDescription);
      return o == null ? null : o.getKeys();
   }

   public synchronized long getObjectExpirationTime(Object objectDescription) {
      ObjectRecord o = this.hashtable.lookup(objectDescription);
      return o == null ? -1L : o.getObjectExpiration();
   }

   public synchronized void deleteObject(Object objectDescription) {
      ObjectRecord o = this.hashtable.lookup(objectDescription);
      if (o != null) {
         this.deleteEntry(o);
      }

   }

   public synchronized void setObject(Object objectDescription, Object object, StringSet keys, long timestamp) {
      ObjectRecord record = new ObjectRecord(objectDescription, object, keys, timestamp);
      this.hashtable.add(record);
      this.invalidationTable.addKeys(keys, record);
   }

   public synchronized void setObjectExpiration(Object objectDescription, long expirationTime) {
      ObjectRecord existing = this.hashtable.lookup(objectDescription);
      if (existing != null) {
         if (existing.getObjectExpiration() != -1L) {
            this.expirationTree.removeEntry(existing);
         }

         existing.setObjectExpiration(expirationTime);
         if (expirationTime != -1L) {
            this.expirationTree.addEntry(existing);
         }

      }
   }

   public synchronized void setObjectClass(Object objectDescription, String objectClass, int maxCount) {
      ObjectRecord existing = this.hashtable.lookup(objectDescription);
      if (existing != null) {
         if (existing.getObjectClass() != null) {
            this.objectClassTable.removeEntry(existing);
         }

         existing.setObjectClass(objectClass);
         if (objectClass != null) {
            this.objectClassTable.addEntry(existing);
            if (maxCount >= 0) {
               while(this.objectClassTable.getCurrentMemberCount(objectClass) > maxCount) {
                  ObjectRecord oldestRecord = this.objectClassTable.getOldestEntry(objectClass);
                  this.deleteEntry(oldestRecord);
               }
            }
         }

      }
   }

   public synchronized void invalidateKeys(StringSet keys) {
      Iterator enum2 = keys.getKeys();

      while(enum2.hasNext()) {
         String invalidateKey = (String)enum2.next();
         Iterator enum1 = this.invalidationTable.getObjectRecordsForKey(invalidateKey);

         while(enum1.hasNext()) {
            ObjectRecord record = (ObjectRecord)enum1.next();
            this.hashtable.remove(record);
            if (record.getObjectClass() != null) {
               this.objectClassTable.removeEntry(record);
            }

            if (record.getExpirationTime() >= 0L) {
               this.expirationTree.removeEntry(record);
            }
         }

         this.invalidationTable.removeKey(invalidateKey);
      }

   }

   public void expireRecords(long expireTime) {
      while(true) {
         synchronized(this) {
            ObjectRecord x = this.expirationTree.getOldestEntry();
            if (x != null) {
               if (x.getExpirationTime() <= expireTime) {
                  this.deleteEntry(x);
                  continue;
               }
            }

            return;
         }
      }
   }

   protected void deleteEntry(ObjectRecord record) {
      this.hashtable.remove(record);
      this.invalidationTable.removeObjectRecord(record);
      if (record.getObjectClass() != null) {
         this.objectClassTable.removeEntry(record);
      }

      if (record.getExpirationTime() >= 0L) {
         this.expirationTree.removeEntry(record);
      }

   }

   protected class ObjectRecord {
      protected Object objectDescription;
      protected Object theObject;
      protected StringSet invalidationKeys;
      protected long creationTime;
      protected long expirationTime = -1L;
      protected String objectClass = null;
      protected ObjectRecord prevLRU = null;
      protected ObjectRecord nextLRU = null;
      protected ObjectRecord sameExpirationPrev = null;
      protected ObjectRecord sameExpirationNext = null;

      public ObjectRecord(Object objectDescription, Object theObject, StringSet invalidationKeys, long creationTime) {
         this.creationTime = creationTime;
         this.objectDescription = objectDescription;
         this.theObject = theObject;
         this.invalidationKeys = invalidationKeys;
      }

      public long getCreationTime() {
         return this.creationTime;
      }

      public void setSameExpirationPrev(ObjectRecord x) {
         this.sameExpirationPrev = x;
      }

      public ObjectRecord getSameExpirationPrev() {
         return this.sameExpirationPrev;
      }

      public void setSameExpirationNext(ObjectRecord x) {
         this.sameExpirationNext = x;
      }

      public ObjectRecord getSameExpirationNext() {
         return this.sameExpirationNext;
      }

      public void setObjectExpiration(long expTime) {
         this.expirationTime = expTime;
      }

      public long getObjectExpiration() {
         return this.expirationTime;
      }

      public Object getObjectDescription() {
         return this.objectDescription;
      }

      public void setObjectClass(String className) {
         this.objectClass = className;
      }

      public String getObjectClass() {
         return this.objectClass;
      }

      public ObjectRecord getPrevLRU() {
         return this.prevLRU;
      }

      public ObjectRecord getNextLRU() {
         return this.nextLRU;
      }

      public void setPrevLRU(ObjectRecord prev) {
         this.prevLRU = prev;
      }

      public void setNextLRU(ObjectRecord next) {
         this.nextLRU = next;
      }

      public Object getObject() {
         return this.theObject;
      }

      public StringSet getKeys() {
         return this.invalidationKeys;
      }

      public long getExpirationTime() {
         return this.expirationTime;
      }

      public int hashCode() {
         return this.objectDescription.hashCode();
      }

      public boolean equals(Object o) {
         if (!(o instanceof ObjectRecord)) {
            return false;
         } else {
            ObjectRecord record = (ObjectRecord)o;
            return this.objectDescription.equals(record.objectDescription);
         }
      }
   }

   protected class ObjectRecordTable {
      protected HashMap hashtable = new HashMap();

      public ObjectRecordTable() {
      }

      public void add(ObjectRecord record) {
         this.hashtable.put(record.getObjectDescription(), record);
      }

      public void remove(Object objectDescription) {
         this.hashtable.remove(objectDescription);
      }

      public void remove(ObjectRecord record) {
         this.hashtable.remove(record.getObjectDescription());
      }

      public ObjectRecord lookup(Object objectDescription) {
         return (ObjectRecord)this.hashtable.get(objectDescription);
      }
   }

   protected class InvalidationTable {
      protected HashMap hashtable = new HashMap();

      public InvalidationTable() {
      }

      public void addKeys(StringSet keyset, ObjectRecord objectRecord) {
         HashMap ht;
         for(Iterator enum1 = keyset.getKeys(); enum1.hasNext(); ht.put(objectRecord, objectRecord)) {
            String key = (String)enum1.next();
            ht = (HashMap)this.hashtable.get(key);
            if (ht == null) {
               ht = new HashMap();
               this.hashtable.put(key, ht);
            }
         }

      }

      public Iterator getObjectRecordsForKey(String key) {
         HashMap ht = (HashMap)this.hashtable.get(key);
         if (ht == null) {
            ht = new HashMap();
            this.hashtable.put(key, ht);
         }

         return ht.keySet().iterator();
      }

      public void removeKey(String key) {
         this.hashtable.remove(key);
      }

      public void removeObjectRecord(ObjectRecord record) {
         StringSet keys = record.getKeys();
         Iterator enum1 = keys.getKeys();

         while(enum1.hasNext()) {
            String key = (String)enum1.next();
            this.removeObjectRecordFromKey(key, record);
         }

      }

      public void removeObjectRecordFromKey(String key, ObjectRecord objectRecord) {
         HashMap ht = (HashMap)this.hashtable.get(key);
         if (ht != null) {
            ht.remove(objectRecord);
         }
      }
   }

   protected class ObjectClassTable {
      protected HashMap hashtable = new HashMap();

      public ObjectClassTable() {
      }

      public void addEntry(ObjectRecord record) {
         ObjectClassRecord x = (ObjectClassRecord)this.hashtable.get(record.getObjectClass());
         if (x == null) {
            x = GeneralCache.this.new ObjectClassRecord();
            this.hashtable.put(record.getObjectClass(), x);
         }

         x.addEntry(record);
      }

      public void removeEntry(ObjectRecord record) {
         ObjectClassRecord x = (ObjectClassRecord)this.hashtable.get(record.getObjectClass());
         if (x != null) {
            x.removeEntry(record);
         }
      }

      public int getCurrentMemberCount(String objectClassName) {
         ObjectClassRecord x = (ObjectClassRecord)this.hashtable.get(objectClassName);
         return x == null ? 0 : x.getCurrentMemberCount();
      }

      public ObjectRecord getOldestEntry(String objectClassName) {
         ObjectClassRecord x = (ObjectClassRecord)this.hashtable.get(objectClassName);
         return x == null ? null : x.getOldestEntry();
      }
   }

   protected class ObjectClassRecord {
      protected int currentMemberCount = 0;
      protected ObjectRecord firstLRU = null;
      protected ObjectRecord lastLRU = null;

      public ObjectClassRecord() {
      }

      public int getCurrentMemberCount() {
         return this.currentMemberCount;
      }

      public void removeEntry(ObjectRecord x) {
         --this.currentMemberCount;
         ObjectRecord prev = x.getPrevLRU();
         ObjectRecord next = x.getNextLRU();
         if (prev == null) {
            this.firstLRU = next;
         } else {
            prev.setNextLRU(next);
         }

         if (next == null) {
            this.lastLRU = prev;
         } else {
            next.setPrevLRU(prev);
         }

         x.setPrevLRU((ObjectRecord)null);
         x.setNextLRU((ObjectRecord)null);
      }

      public void addEntry(ObjectRecord x) {
         ++this.currentMemberCount;
         x.setNextLRU((ObjectRecord)null);
         x.setPrevLRU(this.lastLRU);
         if (this.lastLRU == null) {
            this.firstLRU = x;
         } else {
            this.lastLRU.setNextLRU(x);
         }

         this.lastLRU = x;
      }

      public ObjectRecord getOldestEntry() {
         return this.firstLRU;
      }
   }

   protected class ExpirationTree {
      protected ExpirationTreeNode root = null;

      public ExpirationTree() {
      }

      public void removeEntry(ObjectRecord x) {
         ExpirationTreeNode parent = null;
         boolean parentLesser = false;
         long expTime = x.getExpirationTime();
         ExpirationTreeNode current = this.root;

         while(current != null) {
            long nodeExpTime = current.getExpirationTime();
            if (expTime == nodeExpTime) {
               if (current.removeObjectRecord(x)) {
                  ExpirationTreeNode lesserSide = current.getLesser();
                  ExpirationTreeNode greaterSide = current.getGreater();
                  if (lesserSide == null && greaterSide == null) {
                     this.setPointer(parent, parentLesser, (ExpirationTreeNode)null);
                  } else if (lesserSide == null && greaterSide != null) {
                     this.setPointer(parent, parentLesser, greaterSide);
                  } else if (lesserSide != null && greaterSide == null) {
                     this.setPointer(parent, parentLesser, lesserSide);
                  } else {
                     this.setPointer(parent, parentLesser, greaterSide);
                     this.addTreeToBranch(greaterSide, true, lesserSide);
                  }
               }

               return;
            }

            if (expTime < nodeExpTime) {
               parent = current;
               parentLesser = true;
               current = current.getLesser();
            } else {
               parent = current;
               parentLesser = false;
               current = current.getGreater();
            }
         }

      }

      protected void addTreeToBranch(ExpirationTreeNode parent, boolean parentLesser, ExpirationTreeNode toAdd) {
         if (toAdd != null) {
            long expTime = toAdd.getExpirationTime();
            ExpirationTreeNode current;
            if (parent == null) {
               current = this.root;
            } else if (parentLesser) {
               current = parent.getLesser();
            } else {
               current = parent.getGreater();
            }

            while(current != null) {
               long nodeExpTime = current.getExpirationTime();
               if (expTime < nodeExpTime) {
                  parent = current;
                  parentLesser = true;
                  current = current.getLesser();
               } else {
                  parent = current;
                  parentLesser = false;
                  current = current.getGreater();
               }
            }

            this.setPointer(parent, parentLesser, toAdd);
         }
      }

      public void addEntry(ObjectRecord x) {
         long expTime = x.getExpirationTime();
         ExpirationTreeNode previousNode = null;
         boolean lesser = false;
         ExpirationTreeNode current = this.root;

         while(current != null) {
            long nodeExpTime = current.getExpirationTime();
            if (nodeExpTime == expTime) {
               current.addObjectRecord(x);
               return;
            }

            if (nodeExpTime > expTime) {
               previousNode = current;
               lesser = true;
               current = current.getLesser();
            } else {
               previousNode = current;
               lesser = false;
               current = current.getGreater();
            }
         }

         ExpirationTreeNode newNode = GeneralCache.this.new ExpirationTreeNode(x);
         this.setPointer(previousNode, lesser, newNode);
      }

      protected void setPointer(ExpirationTreeNode parent, boolean isLesser, ExpirationTreeNode toAdd) {
         if (parent == null) {
            this.root = toAdd;
         } else if (isLesser) {
            parent.setLesser(toAdd);
         } else {
            parent.setGreater(toAdd);
         }

      }

      public ObjectRecord getOldestEntry() {
         ExpirationTreeNode current = this.root;

         ExpirationTreeNode last;
         for(last = null; current != null; current = current.getLesser()) {
            last = current;
         }

         return last == null ? null : last.getOldest();
      }
   }

   protected class ExpirationTreeNode {
      protected ExpirationTreeNode lesser = null;
      protected ExpirationTreeNode greater = null;
      protected ObjectRecord firstSame = null;
      protected ObjectRecord lastSame = null;

      public ExpirationTreeNode(ObjectRecord record) {
         this.firstSame = record;
         this.lastSame = record;
      }

      public long getExpirationTime() {
         return this.firstSame.getExpirationTime();
      }

      public ExpirationTreeNode getLesser() {
         return this.lesser;
      }

      public void setLesser(ExpirationTreeNode lesser) {
         this.lesser = lesser;
      }

      public ExpirationTreeNode getGreater() {
         return this.greater;
      }

      public void setGreater(ExpirationTreeNode greater) {
         this.greater = greater;
      }

      public void addObjectRecord(ObjectRecord x) {
         x.setSameExpirationNext(this.firstSame);
         this.firstSame.setSameExpirationPrev(x);
         this.firstSame = x;
      }

      public boolean removeObjectRecord(ObjectRecord x) {
         ObjectRecord prev = x.getSameExpirationPrev();
         ObjectRecord next = x.getSameExpirationNext();
         if (prev == null) {
            this.firstSame = next;
         } else {
            prev.setSameExpirationNext(next);
         }

         if (next == null) {
            this.lastSame = prev;
         } else {
            next.setSameExpirationPrev(prev);
         }

         x.setSameExpirationPrev((ObjectRecord)null);
         x.setSameExpirationNext((ObjectRecord)null);
         return this.firstSame == null;
      }

      public ObjectRecord getOldest() {
         return this.lastSame;
      }
   }
}
