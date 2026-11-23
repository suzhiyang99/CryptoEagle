package org.apache.openejb.core.timer;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.ejb.ScheduleExpression;
import javax.ejb.TimerConfig;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class MemoryTimerStore implements TimerStore {
   private static final long serialVersionUID = 1L;
   private static final Logger log;
   private final Map<Long, TimerData> taskStore = new ConcurrentHashMap();
   private final Map<Transaction, TimerDataView> tasksByTransaction = new ConcurrentHashMap();
   private final AtomicLong counter = new AtomicLong(0L);
   private final TransactionManager transactionManager;

   public MemoryTimerStore(TransactionManager transactionManager) {
      this.transactionManager = transactionManager;
   }

   public TimerData getTimer(String deploymentId, long timerId) {
      try {
         TimerDataView tasks = this.getTasks();
         return (TimerData)tasks.getTasks().get(timerId);
      } catch (TimerStoreException var5) {
         return null;
      }
   }

   public Collection<TimerData> getTimers(String deploymentId) {
      try {
         TimerDataView tasks = this.getTasks();
         return new ArrayList(tasks.getTasks().values());
      } catch (TimerStoreException var3) {
         return Collections.emptySet();
      }
   }

   public Collection<TimerData> loadTimers(EjbTimerServiceImpl timerService, String deploymentId) throws TimerStoreException {
      TimerDataView tasks = this.getTasks();
      Collection<TimerData> out = new LinkedList();

      for(TimerData data : tasks.getTasks().values()) {
         if (deploymentId == null || deploymentId.equals(data.getDeploymentId())) {
            out.add(data);
         }
      }

      return out;
   }

   public void addTimerData(TimerData timerData) throws TimerStoreException {
      this.getTasks().addTimerData(timerData);
   }

   public TimerData createCalendarTimer(EjbTimerServiceImpl timerService, String deploymentId, Object primaryKey, Method timeoutMethod, ScheduleExpression scheduleExpression, TimerConfig timerConfig, boolean auto) throws TimerStoreException {
      long id = this.counter.incrementAndGet();
      TimerData timerData = new CalendarTimerData(id, timerService, deploymentId, primaryKey, timeoutMethod, timerConfig, scheduleExpression, auto);
      this.getTasks().addTimerData(timerData);
      return timerData;
   }

   public TimerData createIntervalTimer(EjbTimerServiceImpl timerService, String deploymentId, Object primaryKey, Method timeoutMethod, Date initialExpiration, long intervalDuration, TimerConfig timerConfig) throws TimerStoreException {
      long id = this.counter.incrementAndGet();
      TimerData timerData = new IntervalTimerData(id, timerService, deploymentId, primaryKey, timeoutMethod, timerConfig, initialExpiration, intervalDuration);
      this.getTasks().addTimerData(timerData);
      return timerData;
   }

   public TimerData createSingleActionTimer(EjbTimerServiceImpl timerService, String deploymentId, Object primaryKey, Method timeoutMethod, Date expiration, TimerConfig timerConfig) throws TimerStoreException {
      long id = this.counter.incrementAndGet();
      TimerData timerData = new SingleActionTimerData(id, timerService, deploymentId, primaryKey, timeoutMethod, timerConfig, expiration);
      this.getTasks().addTimerData(timerData);
      return timerData;
   }

   public void removeTimer(long id) {
      try {
         this.getTasks().removeTimerData(id);
      } catch (TimerStoreException e) {
         log.warning("Unable to remove timer data from memory store", (Throwable)e);
      }

   }

   public void updateIntervalTimer(TimerData timerData) {
   }

   private TimerDataView getTasks() throws TimerStoreException {
      Transaction transaction = null;
      int status = 6;

      try {
         transaction = this.transactionManager.getTransaction();
         if (transaction != null) {
            status = transaction.getStatus();
         }
      } catch (SystemException var4) {
      }

      if (status != 0 && status != 1) {
         return new LiveTimerDataView();
      } else {
         TxTimerDataView tasks = (TxTimerDataView)this.tasksByTransaction.get(transaction);
         if (tasks == null) {
            tasks = new TxTimerDataView(transaction);
            this.tasksByTransaction.put(transaction, tasks);
         }

         return tasks;
      }
   }

   static {
      log = Logger.getInstance(LogCategory.TIMER, "org.apache.openejb.util.resources");
   }

   private class LiveTimerDataView implements TimerDataView {
      private LiveTimerDataView() {
      }

      public Map<Long, TimerData> getTasks() {
         return new TreeMap(MemoryTimerStore.this.taskStore);
      }

      public void addTimerData(TimerData timerData) {
         MemoryTimerStore.this.taskStore.put(timerData.getId(), timerData);
      }

      public void removeTimerData(Long timerId) {
         MemoryTimerStore.this.taskStore.remove(timerId);
      }
   }

   private class TxTimerDataView implements Synchronization, TimerDataView {
      private final Map<Long, TimerData> add = new TreeMap();
      private final Set<Long> remove = new TreeSet();
      private final Lock lock = new ReentrantLock();
      private final RuntimeException concurentException;
      private final WeakReference<Transaction> tansactionReference;

      public TxTimerDataView(Transaction transaction) throws TimerStoreException {
         this.lock.lock();
         this.concurentException = new IllegalThreadStateException("Object can only be invoked by Thread[" + Thread.currentThread().getName() + "] in Transaction[" + transaction + "]");
         this.concurentException.fillInStackTrace();

         try {
            transaction.registerSynchronization(this);
            this.tansactionReference = new WeakReference(transaction);
         } catch (RollbackException var4) {
            throw new TimerStoreException("Transaction has been rolled back");
         } catch (SystemException var5) {
            throw new TimerStoreException("Error registering transaction synchronization callback");
         }
      }

      private void checkThread() {
         if (!this.lock.tryLock()) {
            throw new IllegalStateException("Illegal access by Thread[" + Thread.currentThread().getName() + "]", this.concurentException);
         }
      }

      public Map<Long, TimerData> getTasks() {
         this.checkThread();
         TreeMap<Long, TimerData> allTasks = new TreeMap();
         allTasks.putAll(MemoryTimerStore.this.taskStore);

         for(Long key : this.remove) {
            allTasks.remove(key);
         }

         allTasks.putAll(this.add);
         return Collections.unmodifiableMap(allTasks);
      }

      public void addTimerData(TimerData timerData) {
         this.checkThread();
         Long timerId = timerData.getId();
         this.remove.remove(timerId);
         this.add.put(timerId, timerData);
      }

      public void removeTimerData(Long timerId) {
         this.checkThread();
         this.add.remove(timerId);
         this.remove.add(timerId);
      }

      public void beforeCompletion() {
         this.checkThread();
      }

      public void afterCompletion(int status) {
         this.checkThread();
         if (status == 3) {
            MemoryTimerStore.this.taskStore.putAll(this.add);
            MemoryTimerStore.this.taskStore.keySet().removeAll(this.remove);
            MemoryTimerStore.this.tasksByTransaction.remove(this.tansactionReference.get());
         }
      }
   }

   private interface TimerDataView {
      Map<Long, TimerData> getTasks();

      void addTimerData(TimerData var1);

      void removeTimerData(Long var1);
   }
}
