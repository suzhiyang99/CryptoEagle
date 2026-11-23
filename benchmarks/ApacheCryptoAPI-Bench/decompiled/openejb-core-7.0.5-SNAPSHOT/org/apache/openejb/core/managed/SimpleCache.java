package org.apache.openejb.core.managed;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class SimpleCache<K, V> implements Cache<K, V> {
   public static final Logger logger;
   private final ConcurrentHashMap<K, SimpleCache<K, V>.Entry> cache = new ConcurrentHashMap();
   private final Queue<SimpleCache<K, V>.Entry> lru = new LinkedBlockingQueue();
   private Cache.CacheListener<V> listener;
   private PassivationStrategy passivator;
   private int capacity;
   private int bulkPassivate;
   private long timeOut;

   public SimpleCache() {
   }

   public SimpleCache(Cache.CacheListener<V> listener, PassivationStrategy passivator, int capacity, int bulkPassivate, Duration timeOut) {
      this.listener = listener;
      this.passivator = passivator;
      this.capacity = capacity;
      this.bulkPassivate = bulkPassivate;
      this.timeOut = timeOut.getTime(TimeUnit.MILLISECONDS);
   }

   public synchronized Cache.CacheListener<V> getListener() {
      return this.listener;
   }

   public synchronized void setListener(Cache.CacheListener<V> listener) {
      this.listener = listener;
   }

   public synchronized PassivationStrategy getPassivator() {
      return this.passivator;
   }

   public synchronized void setPassivator(PassivationStrategy passivator) {
      this.passivator = passivator;
   }

   public synchronized void setPassivator(Class<? extends PassivationStrategy> passivatorClass) throws Exception {
      this.passivator = (PassivationStrategy)passivatorClass.newInstance();
   }

   public synchronized int getCapacity() {
      return this.capacity;
   }

   public synchronized void setCapacity(int capacity) {
      this.capacity = capacity;
   }

   public synchronized void setPoolSize(int capacity) {
      this.capacity = capacity;
   }

   public synchronized int getBulkPassivate() {
      return this.bulkPassivate;
   }

   public synchronized void setBulkPassivate(int bulkPassivate) {
      this.bulkPassivate = bulkPassivate;
   }

   public synchronized long getTimeOut() {
      return this.timeOut;
   }

   public synchronized void setTimeOut(long timeOut) {
      this.timeOut = timeOut * 60L * 1000L;
   }

   public void add(K key, V value) {
      SimpleCache<K, V>.Entry entry = (Entry)this.cache.get(key);
      if (entry != null) {
         entry.lock.lock();

         try {
            if (entry.getState() != SimpleCache.EntryState.REMOVED) {
               throw new IllegalStateException("An entry for the key " + key + " already exists");
            }

            this.cache.remove(key);
            this.lru.remove(entry);
         } finally {
            entry.lock.unlock();
         }
      }

      entry = new Entry(key, value, SimpleCache.EntryState.CHECKED_OUT);
      this.cache.put(key, entry);
   }

   public V checkOut(K key) throws Exception {
      for(int i = 0; i < 10; ++i) {
         SimpleCache<K, V>.Entry entry = (Entry)this.cache.get(key);
         if (entry == null) {
            entry = this.loadEntry(key);
            if (entry == null) {
               return null;
            }
         }

         entry.lock.lock();

         try {
            switch (entry.getState()) {
               case AVAILABLE:
               default:
                  entry.setState(SimpleCache.EntryState.CHECKED_OUT);
                  this.lru.remove(entry);
                  Object var10 = entry.getValue();
                  return (V)var10;
               case CHECKED_OUT:
                  Object var9 = entry.getValue();
                  return (V)var9;
               case PASSIVATED:
                  this.cache.remove(key, entry);
                  break;
               case REMOVED:
                  Object var4 = null;
                  return (V)var4;
            }
         } finally {
            entry.lock.unlock();
         }
      }

      SimpleCache<K, V>.Entry entry = (Entry)this.cache.remove(key);
      if (entry != null) {
         this.lru.remove(entry);
      }

      throw new OpenEJBRuntimeException("Cache is corrupted: the entry " + key + " in the Map 'cache' is in state PASSIVATED");
   }

   public void checkIn(K key) {
      SimpleCache<K, V>.Entry entry = (Entry)this.cache.get(key);
      if (entry != null) {
         entry.lock.lock();

         try {
            switch (entry.getState()) {
               case AVAILABLE:
                  if (!this.lru.contains(entry)) {
                     throw new IllegalStateException("The entry " + key + " is not checked-out");
                  }

                  entry.resetTimeOut();
                  return;
               case CHECKED_OUT:
               default:
                  entry.setState(SimpleCache.EntryState.AVAILABLE);
                  this.lru.add(entry);
                  entry.resetTimeOut();
                  break;
               case PASSIVATED:
                  throw new IllegalStateException("The entry " + key + " is not checked-out");
               case REMOVED:
                  return;
            }
         } finally {
            entry.lock.unlock();
         }

         this.processLRU();
      }
   }

   public V remove(K key) {
      SimpleCache<K, V>.Entry entry = (Entry)this.cache.get(key);
      if (entry == null) {
         return null;
      } else {
         entry.lock.lock();

         Object var3;
         try {
            this.cache.remove(key);
            this.lru.remove(entry);
            entry.setState(SimpleCache.EntryState.REMOVED);
            var3 = entry.getValue();
         } finally {
            entry.lock.unlock();
         }

         return (V)var3;
      }
   }

   public void removeAll(Cache.CacheFilter<V> filter) {
      Iterator<SimpleCache<K, V>.Entry> iterator = this.cache.values().iterator();

      while(iterator.hasNext()) {
         SimpleCache<K, V>.Entry entry = (Entry)iterator.next();
         entry.lock.lock();

         try {
            if (filter.matches(entry.getValue())) {
               iterator.remove();
               this.lru.remove(entry);
               entry.setState(SimpleCache.EntryState.REMOVED);
            }
         } finally {
            entry.lock.unlock();
         }
      }

   }

   public void processLRU() {
      Cache.CacheListener<V> listener = this.getListener();
      Iterator<SimpleCache<K, V>.Entry> iterator = this.lru.iterator();

      label465:
      while(iterator.hasNext()) {
         SimpleCache<K, V>.Entry entry = (Entry)iterator.next();
         entry.lock.lock();

         try {
            switch (entry.getState()) {
               case AVAILABLE:
               default:
                  if (!entry.isTimedOut()) {
                     break label465;
                  }

                  iterator.remove();
                  this.cache.remove(entry.getKey());
                  entry.setState(SimpleCache.EntryState.REMOVED);
                  if (listener != null) {
                     try {
                        listener.timedOut(entry.getValue());
                     } catch (Exception e) {
                        logger.error("An unexpected exception occured from timedOut callback", (Throwable)e);
                     }
                  }
               case CHECKED_OUT:
                  break;
               case PASSIVATED:
                  iterator.remove();
                  break;
               case REMOVED:
                  iterator.remove();
            }
         } finally {
            entry.lock.unlock();
         }
      }

      if (this.lru.size() >= this.getCapacity()) {
         Map<K, V> valuesToStore = new LinkedHashMap();
         List<SimpleCache<K, V>.Entry> entries = new ArrayList();
         int bulkPassivate = this.getBulkPassivate();
         if (bulkPassivate < 1) {
            bulkPassivate = 1;
         }

         for(int i = 0; i < bulkPassivate; ++i) {
            SimpleCache<K, V>.Entry entry = (Entry)this.lru.poll();
            if (entry == null) {
               break;
            }

            if (entry.lock.tryLock()) {
               try {
                  switch (entry.getState()) {
                     case AVAILABLE:
                     default:
                        this.cache.remove(entry.getKey());
                        this.lru.remove(entry);
                        if (entry.isTimedOut()) {
                           entry.setState(SimpleCache.EntryState.REMOVED);
                           if (listener != null) {
                              try {
                                 listener.timedOut(entry.getValue());
                              } catch (Exception e) {
                                 logger.error("An unexpected exception occured from timedOut callback", (Throwable)e);
                              }
                           }
                        } else {
                           entry.lock.lock();
                           entries.add(entry);
                           entry.setState(SimpleCache.EntryState.PASSIVATED);
                           valuesToStore.put(entry.getKey(), entry.getValue());
                        }
                     case CHECKED_OUT:
                        break;
                     case PASSIVATED:
                        this.lru.remove(entry);
                        break;
                     case REMOVED:
                        this.lru.remove(entry);
                  }
               } finally {
                  entry.lock.unlock();
               }
            }
         }

         if (!valuesToStore.isEmpty()) {
            try {
               this.storeEntries(valuesToStore);
            } finally {
               for(SimpleCache<K, V>.Entry entry : entries) {
                  entry.lock.unlock();
               }

            }
         }
      }

   }

   private SimpleCache<K, V>.Entry loadEntry(K key) throws Exception {
      PassivationStrategy passivator = this.getPassivator();
      if (passivator == null) {
         return null;
      } else {
         V value = null;

         try {
            value = (V)passivator.activate(key);
         } catch (Exception e) {
            logger.error("An unexpected exception occured while reading entries from disk", (Throwable)e);
         }

         if (value == null) {
            return null;
         } else {
            Cache.CacheListener<V> listener = this.getListener();
            if (listener != null) {
               listener.afterLoad(value);
            }

            SimpleCache<K, V>.Entry entry = new Entry(key, value, SimpleCache.EntryState.AVAILABLE);
            this.cache.put(key, entry);
            return entry;
         }
      }
   }

   private void storeEntries(Map<K, V> entriesToStore) {
      Cache.CacheListener<V> listener = this.getListener();
      Iterator<Map.Entry<K, V>> iterator = entriesToStore.entrySet().iterator();

      while(iterator.hasNext()) {
         Map.Entry<K, V> entry = (Map.Entry)iterator.next();
         if (listener != null) {
            try {
               listener.beforeStore(entry.getValue());
            } catch (Exception e) {
               iterator.remove();
               logger.error("An unexpected exception occured from beforeStore callback", (Throwable)e);
            }
         }
      }

      PassivationStrategy passivator = this.getPassivator();
      if (passivator != null) {
         try {
            passivator.passivate(entriesToStore);
         } catch (Exception e) {
            logger.error("An unexpected exception occured while writting the entries to disk", (Throwable)e);
         }

      }
   }

   static {
      logger = Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources");
   }

   private static enum EntryState {
      AVAILABLE,
      CHECKED_OUT,
      PASSIVATED,
      REMOVED;
   }

   private final class Entry {
      private final K key;
      private final V value;
      private final ReentrantLock lock;
      private EntryState state;
      private long lastAccess;

      private Entry(K key, V value, EntryState state) {
         this.lock = new ReentrantLock();
         this.key = key;
         this.value = value;
         this.state = state;
         this.lastAccess = System.currentTimeMillis();
      }

      private K getKey() {
         this.assertLockHeld();
         return this.key;
      }

      private V getValue() {
         this.assertLockHeld();
         return this.value;
      }

      private EntryState getState() {
         this.assertLockHeld();
         return this.state;
      }

      private void setState(EntryState state) {
         this.assertLockHeld();
         this.state = state;
      }

      private boolean isTimedOut() {
         this.assertLockHeld();
         long timeOut = SimpleCache.this.getTimeOut();
         if (timeOut == 0L) {
            return false;
         } else {
            long now = System.currentTimeMillis();
            return now - this.lastAccess > timeOut;
         }
      }

      private void resetTimeOut() {
         this.assertLockHeld();
         if (SimpleCache.this.getTimeOut() > 0L) {
            this.lastAccess = System.currentTimeMillis();
         }

      }

      private void assertLockHeld() {
         if (!this.lock.isHeldByCurrentThread()) {
            throw new IllegalStateException("Entry must be locked");
         }
      }
   }
}
