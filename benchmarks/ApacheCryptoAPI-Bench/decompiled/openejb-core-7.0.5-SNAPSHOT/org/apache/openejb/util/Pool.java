package org.apache.openejb.util;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import org.apache.openejb.core.ParentClassLoaderFinder;
import org.apache.openejb.monitoring.Managed;

public class Pool<T> {
   private final LinkedList<Pool<T>.Entry> pool;
   private final Semaphore instances;
   private final Semaphore available;
   private final Semaphore minimum;
   private final Executor executor;
   @Managed
   private final long maxAge;
   @Managed
   private final AtomicInteger poolVersion;
   private final Supplier<T> supplier;
   private final AtomicReference<ScheduledExecutorService> scheduler;
   private final AtomicReference<ScheduledFuture<?>> future;
   private final Pool<T>.Sweeper sweeper;
   private final CountingLatch out;
   @Managed
   private final long sweepInterval;
   @Managed
   private final boolean replaceAged;
   @Managed
   private final boolean replaceFlushed;
   @Managed
   private final double maxAgeOffset;
   @Managed
   private final Pool<T>.Stats stats;
   @Managed
   private final boolean garbageCollection;

   public Pool(int max, int min, boolean strict) {
      this(max, min, strict, 0L, 0L, 0L, (Executor)null, (Supplier)null, false, (double)-1.0F, false, false);
   }

   public Pool(int max, int min, boolean strict, long maxAge, long idleTimeout, long sweepInterval, Executor executor, Supplier<T> supplier, boolean replaceAged, double maxAgeOffset, boolean garbageCollection, boolean replaceFlushed) {
      this.pool = new LinkedList();
      this.poolVersion = new AtomicInteger();
      this.scheduler = new AtomicReference();
      this.future = new AtomicReference();
      this.out = new CountingLatch();
      if (min > max) {
         this.greater("max", (long)max, "min", (long)min);
      }

      if (maxAge != 0L && idleTimeout > maxAge) {
         this.greater("MaxAge", maxAge, "IdleTimeout", idleTimeout);
      }

      this.executor = executor != null ? executor : this.createExecutor();
      this.supplier = (Supplier<T>)(supplier != null ? supplier : new NoSupplier());
      this.available = (Semaphore)(strict ? new Semaphore(max) : new Overdraft(max));
      this.minimum = new Semaphore(min);
      this.instances = new Semaphore(max);
      this.maxAge = maxAge;
      this.maxAgeOffset = maxAgeOffset;
      this.replaceAged = replaceAged;
      this.replaceFlushed = replaceFlushed;
      if (sweepInterval == 0L) {
         sweepInterval = 300000L;
      }

      this.sweepInterval = sweepInterval;
      this.sweeper = new Sweeper(idleTimeout, max);
      this.stats = new Stats(min, max, idleTimeout);
      this.garbageCollection = garbageCollection;
   }

   public Pool start() {
      ScheduledExecutorService scheduledExecutorService = (ScheduledExecutorService)this.scheduler.get();
      boolean createdSES = scheduledExecutorService == null;
      if (scheduledExecutorService == null) {
         scheduledExecutorService = Executors.newScheduledThreadPool(1, new SchedulerThreadFactory());
         if (!this.scheduler.compareAndSet((Object)null, scheduledExecutorService)) {
            scheduledExecutorService.shutdownNow();
            scheduledExecutorService = (ScheduledExecutorService)this.scheduler.get();
            createdSES = false;
         }
      }

      ScheduledFuture<?> scheduledFuture = scheduledExecutorService.scheduleAtFixedRate(this.sweeper, 0L, this.sweepInterval, TimeUnit.MILLISECONDS);
      if (!this.future.compareAndSet((Object)null, scheduledFuture)) {
         scheduledFuture.cancel(true);
      }

      if (!createdSES) {
         this.scheduler.set((Object)null);
      }

      return this;
   }

   public void stop() {
      ScheduledFuture<?> future = (ScheduledFuture)this.future.getAndSet((Object)null);
      if (future != null && !future.isDone() && !future.isCancelled() && !future.cancel(false)) {
         java.util.logging.Logger.getLogger(Pool.class.getName()).log(Level.WARNING, "Pool scheduler task termination timeout expired");
      }

      ScheduledExecutorService scheduler = (ScheduledExecutorService)this.scheduler.getAndSet((Object)null);
      if (scheduler != null) {
         scheduler.shutdown();

         try {
            if (!scheduler.awaitTermination(10L, TimeUnit.SECONDS)) {
               java.util.logging.Logger.getLogger(Pool.class.getName()).log(Level.WARNING, "Pool scheduler termination timeout expired");
            }
         } catch (InterruptedException var4) {
         }
      }

   }

   public boolean running() {
      return this.future.get() != null;
   }

   private Executor createExecutor() {
      ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(2), new DaemonThreadFactory(new Object[]{"org.apache.openejb.util.Pool", this.hashCode()}));
      threadPoolExecutor.setRejectedExecutionHandler(new RejectedExecutionHandler() {
         public void rejectedExecution(Runnable r, ThreadPoolExecutor tpe) {
            if (null != r && null != tpe && !tpe.isShutdown() && !tpe.isTerminated() && !tpe.isTerminating()) {
               try {
                  if (!tpe.getQueue().offer(r, 20L, TimeUnit.SECONDS)) {
                     Logger.getInstance(LogCategory.OPENEJB, "org.apache.openejb.util.resources").warning("Default pool executor failed to run asynchronous process: " + r);
                  }
               } catch (InterruptedException var4) {
               }

            }
         }
      });
      return threadPoolExecutor;
   }

   private void greater(String maxName, long max, String minName, long min) {
      throw new IllegalArgumentException(minName + " cannot be greater than " + maxName + ": " + minName + "=" + min + ", " + maxName + "=" + max);
   }

   @Managed
   public void flush() {
      this.stats.flushes.record();
      this.poolVersion.incrementAndGet();
   }

   public Pool<T>.Entry pop(long timeout, TimeUnit unit) throws InterruptedException, TimeoutException {
      return this.pop(timeout, unit, true);
   }

   private Pool<T>.Entry pop(long timeout, TimeUnit unit, boolean record) throws InterruptedException, TimeoutException {
      if (timeout == -1L) {
         this.available.tryAcquire();
      } else if (!this.available.tryAcquire(timeout, unit)) {
         if (record) {
            this.stats.accessTimeouts.record();
         }

         throw new TimeoutException("Waited " + timeout + " " + unit);
      }

      while(true) {
         Pool<T>.Entry entry;
         synchronized(this.pool) {
            try {
               entry = (Entry)this.pool.removeFirst();
            } catch (NoSuchElementException var9) {
               return null;
            }
         }

         Pool<T>.Entry.Instance instance = (Entry.Instance)entry.soft.get();
         if (instance != null) {
            boolean notBusy = entry.active.compareAndSet((Object)null, instance);
            if (notBusy) {
               return entry;
            }
         } else {
            this.instances.release();
         }
      }
   }

   public boolean add(T obj) {
      return this.add(obj, 0L);
   }

   public boolean add(T obj, long offset) {
      try {
         if (this.available.tryAcquire(100L, TimeUnit.MILLISECONDS)) {
            try {
               if (this.push(obj, offset)) {
                  return true;
               }

               this.available.release();
            } catch (RuntimeException e) {
               this.available.release();
               throw e;
            }
         }

         return false;
      } catch (InterruptedException e) {
         Thread.interrupted();
         e.printStackTrace();
         return false;
      }
   }

   public boolean push(T obj) {
      return this.push(obj, 0L);
   }

   private boolean push(T obj, long offset) {
      if (this.instances.tryAcquire()) {
         return this.push(new Entry(obj, offset));
      } else {
         if (obj != null) {
            (new Discard(obj, Pool.Event.FULL)).run();
         }

         if (this.available instanceof Overdraft) {
            this.available.release();
         }

         return false;
      }
   }

   public boolean push(Pool<T>.Entry entry) {
      return this.push(entry, false);
   }

   private boolean push(Pool<T>.Entry entry, boolean sweeper) {
      boolean added = false;
      boolean release = true;
      Event event = Pool.Event.FULL;
      Pool<T>.Entry.Instance obj = entry == null ? null : (Entry.Instance)entry.active.getAndSet((Object)null);

      try {
         if (entry == null) {
            boolean var18 = false;
            return var18;
         }

         if (!sweeper) {
            entry.markLastUsed();
         }

         long age = now() - entry.created;
         boolean aged = this.maxAge > 0L && age > this.maxAge;
         boolean flushed = entry.version != this.poolVersion.get();
         if (!aged && !flushed) {
            if (!entry.hasHardReference() && this.minimum.tryAcquire()) {
               entry.hard.set(obj);
            }

            synchronized(this.pool) {
               this.pool.addFirst(entry);
            }

            added = true;
         } else {
            if (aged) {
               event = Pool.Event.AGED;
            }

            if (flushed) {
               event = Pool.Event.FLUSHED;
            }

            if (entry.hasHardReference() || aged && this.replaceAged || flushed && this.replaceFlushed) {
               release = false;
               entry.active.set(obj);
               this.executor.execute(new Replace(entry));
            }
         }
      } finally {
         if (release) {
            this.available.release();
            if (entry != null && !added) {
               this.instances.release();
            }
         }

      }

      if (!added && obj != null) {
         if (sweeper) {
            this.executor.execute(obj.discard(event));
         } else {
            obj.discard(event).run();
         }
      }

      return added;
   }

   public void discard() {
      this.discard((Entry)null);
   }

   public void discard(Pool<T>.Entry entry) {
      if (entry != null) {
         if (entry.hasHardReference()) {
            this.minimum.release();
         }

         this.instances.release();
      }

      this.available.release();
   }

   public boolean close(long timeout, TimeUnit unit) throws InterruptedException {
      this.stop();

      while(this.instances.tryAcquire()) {
         Thread.yield();
      }

      while(this.minimum.tryAcquire()) {
         Thread.yield();
      }

      this.flush();

      try {
         this.sweeper.run();
      } catch (Exception var5) {
      }

      if (!(this.available instanceof Overdraft)) {
         while(this.available.tryAcquire()) {
            Thread.yield();
         }

         this.available.drainPermits();
      }

      this.instances.drainPermits();
      this.minimum.drainPermits();
      return this.out.await(timeout, unit);
   }

   private void discardAndReplace(Pool<T>.Entry hard, Pool<T>.Entry soft) {
      soft.hard.set(soft.active());
      this.push(soft);
      hard.hard.set((Object)null);
      this.discard(hard);
   }

   private static long now() {
      return TimeUnit.MILLISECONDS.convert(System.nanoTime(), TimeUnit.NANOSECONDS);
   }

   public final class Entry {
      private final long created;
      private long used;
      private final int version;
      private final SoftReference<Pool<T>.Entry.Instance> soft;
      private final AtomicReference<Pool<T>.Entry.Instance> hard;
      private final AtomicReference<Pool<T>.Entry.Instance> active;

      private Entry(T obj, long offset) {
         this.hard = new AtomicReference();
         this.active = new AtomicReference();
         if (obj == null) {
            throw new NullPointerException("entry is null");
         } else {
            Pool<T>.Entry.Instance instance = new Instance(obj);
            this.soft = (SoftReference<Pool<T>.Entry.Instance>)(Pool.this.garbageCollection ? new SoftReference(instance) : new HardReference(instance));
            this.version = Pool.this.poolVersion.get();
            this.active.set(instance);
            this.created = Pool.now() + offset;
            this.used = this.created;
         }
      }

      public T get() {
         return this.active().instance;
      }

      private Pool<T>.Entry.Instance active() {
         return (Instance)this.active.get();
      }

      public void harden() {
         this.hard.set(this.active());
      }

      public void markLastUsed() {
         this.used = Pool.now();
      }

      public long getUsed() {
         return this.used;
      }

      public boolean hasHardReference() {
         return this.hard.get() != null;
      }

      public String toString() {
         long now = Pool.now();
         return "Entry{min=" + (this.hard.get() != null) + ", age=" + (now - this.created) + ", idle=" + (now - this.used) + ", bean=" + this.soft.get() + '}';
      }

      private class Discarded implements Runnable {
         private Discarded() {
         }

         public void run() {
         }
      }

      public class Instance {
         private final AtomicBoolean callback = new AtomicBoolean();
         private final T instance;

         public Instance(T instance) {
            this.instance = instance;
         }

         protected void finalize() throws Throwable {
            try {
               this.discard(Pool.Event.GC).run();
            } finally {
               super.finalize();
            }

         }

         public Runnable discard(Event event) {
            return (Runnable)(this.callback.compareAndSet(false, true) ? Pool.this.new Discard(this.instance, event) : Entry.this.new Discarded());
         }
      }
   }

   private final class Sweeper implements Runnable {
      private final AtomicInteger previousVersion;
      private final long idleTimeout;
      private final boolean timeouts;
      private final int max;

      private Sweeper(long idleTimeout, int max) {
         this.previousVersion = new AtomicInteger(Pool.this.poolVersion.get());
         this.idleTimeout = idleTimeout;
         this.timeouts = Pool.this.maxAge > 0L || idleTimeout > 0L;
         this.max = max;
      }

      public void run() {
         Pool.this.stats.sweeps.record();
         int currentVersion = Pool.this.poolVersion.get();
         boolean isCurrent = this.previousVersion.getAndSet(currentVersion) == currentVersion;
         if (this.timeouts || !isCurrent) {
            long now = Pool.now();
            List<Pool<T>.Entry> entries = new ArrayList(this.max);

            try {
               while(true) {
                  Pool<T>.Entry entry = Pool.this.pop(0L, TimeUnit.MILLISECONDS, false);
                  if (entry == null) {
                     Pool.this.push((Entry)null, true);
                     break;
                  }

                  entries.add(entry);
               }
            } catch (InterruptedException var13) {
               Thread.interrupted();
            } catch (TimeoutException var14) {
            }

            List<Pool<T>.Expired> expiredList = new ArrayList(this.max);
            Iterator<Pool<T>.Entry> iter = entries.iterator();

            while(iter.hasNext()) {
               Pool<T>.Entry entry = (Entry)iter.next();
               boolean aged = Pool.this.maxAge > 0L && now - entry.created > Pool.this.maxAge;
               boolean flushed = entry.version != currentVersion;
               if (!aged && !flushed) {
                  if (entry.hasHardReference()) {
                     Pool.this.push(entry, true);
                     iter.remove();
                  }
               } else {
                  iter.remove();
                  Pool<T>.Expired expired = Pool.this.new Expired(entry, aged ? Pool.Event.AGED : Pool.Event.FLUSHED);
                  expiredList.add(expired);
                  if (!expired.entry.hasHardReference() && (!aged || !Pool.this.replaceAged)) {
                     expired.tryDiscard();
                  }
               }
            }

            iter = expiredList.iterator();

            while(iter.hasNext() && entries.size() > 0) {
               if (((Expired)iter.next()).replaceMinEntry((Entry)entries.get(0))) {
                  entries.remove(0);
               }
            }

            Iterator<Pool<T>.Entry> iter = entries.iterator();

            while(iter.hasNext()) {
               Pool<T>.Entry entry = (Entry)iter.next();
               iter.remove();
               long idle = now - entry.used;
               if (this.idleTimeout > 0L && idle > this.idleTimeout) {
                  Pool<T>.Expired expired = Pool.this.new Expired(entry, Pool.Event.IDLE);
                  expiredList.add(expired);
                  expired.tryDiscard();
               } else {
                  Pool.this.push(entry, true);
               }
            }

            List<Pool<T>.Expired> replace = new ArrayList();

            for(Pool<T>.Expired expired : expiredList) {
               Pool.this.executor.execute(expired.entry.active().discard(expired.event));
               if (expired.entry.hasHardReference() || expired.aged() && Pool.this.replaceAged) {
                  replace.add(expired);
               }
            }

            for(int i = 0; i < replace.size(); ++i) {
               long offset = Pool.this.maxAge > 0L ? (long)((double)(Pool.this.maxAge / (long)replace.size() * (long)i) * Pool.this.maxAgeOffset) % Pool.this.maxAge : 0L;
               Pool.this.executor.execute(Pool.this.new Replace(((Expired)replace.get(i)).entry, offset));
            }

         }
      }
   }

   public static enum Event {
      FULL,
      IDLE,
      AGED,
      FLUSHED,
      GC;
   }

   private final class Expired {
      private final Pool<T>.Entry entry;
      private final AtomicBoolean discarded;
      private final Event event;

      private Expired(Pool<T>.Entry entry, Event event) {
         this.discarded = new AtomicBoolean();
         this.entry = entry;
         this.event = event;
      }

      public boolean aged() {
         return this.event == Pool.Event.AGED;
      }

      public boolean tryDiscard() {
         if (this.discarded.getAndSet(true)) {
            return false;
         } else {
            Pool.this.discard(this.entry);
            return true;
         }
      }

      public boolean replaceMinEntry(Pool<T>.Entry replacement) {
         if (!this.entry.hasHardReference()) {
            return false;
         } else if (replacement.hasHardReference()) {
            return false;
         } else if (this.discarded.getAndSet(true)) {
            return false;
         } else {
            Pool.this.discardAndReplace(this.entry, replacement);
            return true;
         }
      }
   }

   private final class Replace implements Runnable {
      private final Pool<T>.Entry expired;
      private final long offset;

      private Replace(Pool<T>.Entry expired) {
         this(expired, 0L);
      }

      private Replace(Pool<T>.Entry expired, long offset) {
         this.expired = expired;
         this.offset = offset;
      }

      public void run() {
         if (!Pool.this.running()) {
            Pool.this.discard(this.expired);
         } else {
            try {
               T t = Pool.this.supplier.create();
               if (t == null) {
                  Pool.this.discard(this.expired);
               } else {
                  Pool<T>.Entry entry = Pool.this.new Entry(t, this.offset);
                  if (this.expired.hasHardReference()) {
                     entry.harden();
                  }

                  Pool.this.push(entry);
               }
            } catch (Throwable var6) {
               Pool.this.discard(this.expired);
            } finally {
               Pool.this.stats.replaced.record();
            }

         }
      }
   }

   private final class Discard implements Runnable {
      private final T expired;
      private final Event event;

      private Discard(T expired, Event event) {
         Pool.this.out.countUp();
         if (expired == null) {
            throw new NullPointerException("expired object cannot be null");
         } else {
            this.expired = expired;
            this.event = event;
         }
      }

      public void run() {
         switch (this.event) {
            case AGED:
               Pool.this.stats.aged.record();
               break;
            case FLUSHED:
               Pool.this.stats.flushed.record();
               break;
            case FULL:
               Pool.this.stats.overdrafts.record();
               break;
            case IDLE:
               Pool.this.stats.idleTimeouts.record();
               break;
            case GC:
               Pool.this.stats.garbageCollected.record();
         }

         try {
            Pool.this.supplier.discard(this.expired, this.event);
         } finally {
            Pool.this.out.countDown();
         }

      }
   }

   private static class NoSupplier implements Supplier {
      private NoSupplier() {
      }

      public void discard(Object o, Event reason) {
      }

      public Object create() {
         return null;
      }
   }

   private static final class Overdraft extends Semaphore {
      private static final long serialVersionUID = 1L;
      private final AtomicInteger permits = new AtomicInteger();

      public Overdraft(int permits) {
         super(0);
         this.permits.set(permits);
      }

      public void acquire() throws InterruptedException {
         this.permits.decrementAndGet();
      }

      public void acquireUninterruptibly() {
         this.permits.decrementAndGet();
      }

      public boolean tryAcquire() {
         this.permits.decrementAndGet();
         return true;
      }

      public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException {
         this.permits.decrementAndGet();
         return true;
      }

      public void acquire(int permits) throws InterruptedException {
         this.permits.addAndGet(-permits);
      }

      public void acquireUninterruptibly(int permits) {
         this.permits.addAndGet(-permits);
      }

      public boolean tryAcquire(int permits) {
         this.permits.addAndGet(-permits);
         return true;
      }

      public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException {
         this.permits.addAndGet(-permits);
         return true;
      }

      public void release() {
         this.permits.incrementAndGet();
      }

      public void release(int permits) {
         this.permits.addAndGet(permits);
      }

      public int availablePermits() {
         return this.permits.get();
      }

      public int drainPermits() {
         return 0;
      }

      protected void reducePermits(int reduction) {
      }
   }

   @Managed
   private final class Stats {
      @Managed
      private final org.apache.openejb.monitoring.Event sweeps;
      @Managed
      private final org.apache.openejb.monitoring.Event flushes;
      @Managed
      private final org.apache.openejb.monitoring.Event accessTimeouts;
      @Managed
      private final org.apache.openejb.monitoring.Event garbageCollected;
      @Managed
      private final org.apache.openejb.monitoring.Event idleTimeouts;
      @Managed
      private final org.apache.openejb.monitoring.Event aged;
      @Managed
      private final org.apache.openejb.monitoring.Event flushed;
      @Managed
      private final org.apache.openejb.monitoring.Event overdrafts;
      @Managed
      private final org.apache.openejb.monitoring.Event replaced;
      @Managed
      private final int minSize;
      @Managed
      private final int maxSize;
      @Managed
      private final long idleTimeout;

      private Stats(int minSize, int maxSize, long idleTimeout) {
         this.sweeps = new org.apache.openejb.monitoring.Event();
         this.flushes = new org.apache.openejb.monitoring.Event();
         this.accessTimeouts = new org.apache.openejb.monitoring.Event();
         this.garbageCollected = new org.apache.openejb.monitoring.Event();
         this.idleTimeouts = new org.apache.openejb.monitoring.Event();
         this.aged = new org.apache.openejb.monitoring.Event();
         this.flushed = new org.apache.openejb.monitoring.Event();
         this.overdrafts = new org.apache.openejb.monitoring.Event();
         this.replaced = new org.apache.openejb.monitoring.Event();
         this.minSize = minSize;
         this.maxSize = maxSize;
         this.idleTimeout = idleTimeout;
      }

      @Managed
      private boolean getStrictPooling() {
         return !(Pool.this.available instanceof Overdraft);
      }

      @Managed
      private int getAvailablePermits() {
         return Pool.this.available.availablePermits();
      }

      @Managed
      private int getInstancesPooled() {
         return this.maxSize - Pool.this.instances.availablePermits();
      }

      @Managed
      private int getInstancesIdle() {
         return Math.max(0, this.getInstancesPooled() - this.getInstancesActive());
      }

      @Managed
      private int getInstancesInitializing() {
         return Math.max(0, this.getInstancesActive() - this.getInstancesPooled());
      }

      @Managed
      private int getInstancesActive() {
         return this.maxSize - this.getAvailablePermits();
      }

      @Managed
      private int getMinimumInstances() {
         return this.minSize - Pool.this.minimum.availablePermits();
      }
   }

   public static class Builder<T> {
      private int max = 10;
      private int min;
      private boolean strict = true;
      private Duration maxAge;
      private double maxAgeOffset;
      private Duration idleTimeout;
      private Duration interval;
      private Supplier<T> supplier;
      private Executor executor;
      private ScheduledExecutorService scheduledExecutorService;
      private boolean replaceAged;
      private boolean replaceFlushed;
      private boolean garbageCollection;

      public Builder(Builder<T> that) {
         this.maxAge = new Duration(0L, TimeUnit.MILLISECONDS);
         this.maxAgeOffset = (double)-1.0F;
         this.idleTimeout = new Duration(0L, TimeUnit.MILLISECONDS);
         this.interval = new Duration(300L, TimeUnit.SECONDS);
         this.garbageCollection = true;
         this.max = that.max;
         this.min = that.min;
         this.strict = that.strict;
         this.maxAge = that.maxAge;
         this.idleTimeout = that.idleTimeout;
         this.interval = that.interval;
         this.executor = that.executor;
         this.supplier = that.supplier;
         this.maxAgeOffset = that.maxAgeOffset;
         this.replaceAged = that.replaceAged;
         this.replaceFlushed = that.replaceFlushed;
         this.garbageCollection = that.garbageCollection;
      }

      public Builder() {
         this.maxAge = new Duration(0L, TimeUnit.MILLISECONDS);
         this.maxAgeOffset = (double)-1.0F;
         this.idleTimeout = new Duration(0L, TimeUnit.MILLISECONDS);
         this.interval = new Duration(300L, TimeUnit.SECONDS);
         this.garbageCollection = true;
      }

      public int getMin() {
         return this.min;
      }

      public boolean isGarbageCollection() {
         return this.garbageCollection;
      }

      public void setGarbageCollection(boolean garbageCollection) {
         this.garbageCollection = garbageCollection;
      }

      public void setReplaceAged(boolean replaceAged) {
         this.replaceAged = replaceAged;
      }

      public void setReplaceFlushed(boolean replaceFlushed) {
         this.replaceFlushed = replaceFlushed;
      }

      public void setMaxSize(int max) {
         this.max = max;
      }

      public void setPoolSize(int max) {
         this.setMaxSize(max);
      }

      public void setMinSize(int min) {
         this.min = min;
      }

      public void setStrictPooling(boolean strict) {
         this.strict = strict;
      }

      public void setMaxAge(Duration maxAge) {
         this.maxAge = maxAge;
      }

      public Duration getMaxAge() {
         return this.maxAge;
      }

      public boolean isStrict() {
         return this.strict;
      }

      public Duration getIdleTimeout() {
         return this.idleTimeout;
      }

      public Duration getInterval() {
         return this.interval;
      }

      public boolean isReplaceAged() {
         return this.replaceAged;
      }

      public void setMaxAgeOffset(double maxAgeOffset) {
         this.maxAgeOffset = maxAgeOffset;
      }

      public double getMaxAgeOffset() {
         return this.maxAgeOffset;
      }

      public void setIdleTimeout(Duration idleTimeout) {
         this.idleTimeout = idleTimeout;
      }

      public void setSweepInterval(Duration interval) {
         this.interval = interval;
      }

      public void setSupplier(Supplier<T> supplier) {
         this.supplier = supplier;
      }

      public void setExecutor(Executor executor) {
         this.executor = executor;
      }

      public void setScheduledExecutor(ScheduledExecutorService scheduledExecutorService) {
         this.scheduledExecutorService = scheduledExecutorService;
      }

      public Pool<T> build() {
         Pool pool = new Pool(this.max, this.min, this.strict, this.maxAge.getTime(TimeUnit.MILLISECONDS), this.idleTimeout.getTime(TimeUnit.MILLISECONDS), this.interval.getTime(TimeUnit.MILLISECONDS), this.executor, this.supplier, this.replaceAged, this.maxAgeOffset, this.garbageCollection, this.replaceFlushed);
         if (this.scheduledExecutorService != null) {
            pool.scheduler.set(this.scheduledExecutorService);
         }

         return pool;
      }
   }

   public static class HardReference<T> extends SoftReference<T> {
      private final T hard;

      public HardReference(T referent) {
         super(referent);
         this.hard = referent;
      }

      public T getHard() {
         return this.hard;
      }
   }

   static class SchedulerThreadFactory implements ThreadFactory {
      private static final AtomicInteger count = new AtomicInteger(1);
      private final ThreadGroup group;

      SchedulerThreadFactory() {
         SecurityManager s = System.getSecurityManager();
         this.group = s != null ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
      }

      public Thread newThread(Runnable r) {
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         ClassLoader containerLoader = ParentClassLoaderFinder.Helper.get();
         Thread.currentThread().setContextClassLoader(containerLoader);

         Thread var5;
         try {
            Thread t = new Thread(this.group, r, "org.apache.openejb.pool.scheduler." + count.getAndIncrement());
            if (!t.isDaemon()) {
               t.setDaemon(true);
            }

            if (t.getPriority() != 5) {
               t.setPriority(5);
            }

            t.setContextClassLoader(containerLoader);
            var5 = t;
         } finally {
            Thread.currentThread().setContextClassLoader(loader);
         }

         return var5;
      }
   }

   public interface Supplier<T> {
      void discard(T var1, Event var2);

      T create();
   }
}
