package org.spark_project.jetty.util.thread;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import org.spark_project.jetty.util.BlockingArrayQueue;
import org.spark_project.jetty.util.ConcurrentHashSet;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;
import org.spark_project.jetty.util.annotation.Name;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.component.Dumpable;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;

@ManagedObject("A thread pool")
public class QueuedThreadPool extends AbstractLifeCycle implements ThreadPool.SizedThreadPool, Dumpable {
   private static final Logger LOG = Log.getLogger(QueuedThreadPool.class);
   private final AtomicInteger _threadsStarted;
   private final AtomicInteger _threadsIdle;
   private final AtomicLong _lastShrink;
   private final ConcurrentHashSet<Thread> _threads;
   private final Object _joinLock;
   private final BlockingQueue<Runnable> _jobs;
   private final ThreadGroup _threadGroup;
   private String _name;
   private int _idleTimeout;
   private int _maxThreads;
   private int _minThreads;
   private int _priority;
   private boolean _daemon;
   private boolean _detailedDump;
   private int _lowThreadsThreshold;
   private Runnable _runnable;

   public QueuedThreadPool() {
      this(200);
   }

   public QueuedThreadPool(@Name("maxThreads") int maxThreads) {
      this(maxThreads, 8);
   }

   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads) {
      this(maxThreads, minThreads, 60000);
   }

   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads, @Name("idleTimeout") int idleTimeout) {
      this(maxThreads, minThreads, idleTimeout, (BlockingQueue)null);
   }

   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads, @Name("idleTimeout") int idleTimeout, @Name("queue") BlockingQueue<Runnable> queue) {
      this(maxThreads, minThreads, idleTimeout, queue, (ThreadGroup)null);
   }

   public QueuedThreadPool(@Name("maxThreads") int maxThreads, @Name("minThreads") int minThreads, @Name("idleTimeout") int idleTimeout, @Name("queue") BlockingQueue<Runnable> queue, @Name("threadGroup") ThreadGroup threadGroup) {
      this._threadsStarted = new AtomicInteger();
      this._threadsIdle = new AtomicInteger();
      this._lastShrink = new AtomicLong();
      this._threads = new ConcurrentHashSet<Thread>();
      this._joinLock = new Object();
      this._name = "qtp" + this.hashCode();
      this._priority = 5;
      this._daemon = false;
      this._detailedDump = false;
      this._lowThreadsThreshold = 1;
      this._runnable = new Runnable() {
         public void run() {
            boolean shrink = false;
            boolean ignore = false;

            try {
               Runnable job = (Runnable)QueuedThreadPool.this._jobs.poll();
               if (job != null && QueuedThreadPool.this._threadsIdle.get() == 0) {
                  QueuedThreadPool.this.startThreads(1);
               }

               while(QueuedThreadPool.this.isRunning()) {
                  while(job != null && QueuedThreadPool.this.isRunning()) {
                     if (QueuedThreadPool.LOG.isDebugEnabled()) {
                        QueuedThreadPool.LOG.debug("run {}", job);
                     }

                     QueuedThreadPool.this.runJob(job);
                     if (QueuedThreadPool.LOG.isDebugEnabled()) {
                        QueuedThreadPool.LOG.debug("ran {}", job);
                     }

                     if (Thread.interrupted()) {
                        ignore = true;
                        return;
                     }

                     job = (Runnable)QueuedThreadPool.this._jobs.poll();
                  }

                  try {
                     QueuedThreadPool.this._threadsIdle.incrementAndGet();

                     while(QueuedThreadPool.this.isRunning() && job == null) {
                        if (QueuedThreadPool.this._idleTimeout <= 0) {
                           job = (Runnable)QueuedThreadPool.this._jobs.take();
                        } else {
                           int size = QueuedThreadPool.this._threadsStarted.get();
                           if (size > QueuedThreadPool.this._minThreads) {
                              long last = QueuedThreadPool.this._lastShrink.get();
                              long now = System.nanoTime();
                              if ((last == 0L || now - last > TimeUnit.MILLISECONDS.toNanos((long)QueuedThreadPool.this._idleTimeout)) && QueuedThreadPool.this._lastShrink.compareAndSet(last, now) && QueuedThreadPool.this._threadsStarted.compareAndSet(size, size - 1)) {
                                 shrink = true;
                                 return;
                              }
                           }

                           job = QueuedThreadPool.this.idleJobPoll();
                        }
                     }
                  } finally {
                     if (QueuedThreadPool.this._threadsIdle.decrementAndGet() == 0) {
                        QueuedThreadPool.this.startThreads(1);
                     }

                  }
               }
            } catch (InterruptedException e) {
               ignore = true;
               QueuedThreadPool.LOG.ignore(e);
            } catch (Throwable e) {
               QueuedThreadPool.LOG.warn(e);
            } finally {
               if (!shrink && QueuedThreadPool.this.isRunning()) {
                  if (!ignore) {
                     QueuedThreadPool.LOG.warn("Unexpected thread death: {} in {}", this, QueuedThreadPool.this);
                  }

                  if (QueuedThreadPool.this._threadsStarted.decrementAndGet() < QueuedThreadPool.this.getMaxThreads()) {
                     QueuedThreadPool.this.startThreads(1);
                  }
               }

               QueuedThreadPool.this._threads.remove(Thread.currentThread());
            }

         }
      };
      this.setMinThreads(minThreads);
      this.setMaxThreads(maxThreads);
      this.setIdleTimeout(idleTimeout);
      this.setStopTimeout(5000L);
      if (queue == null) {
         int capacity = Math.max(this._minThreads, 8);
         queue = new BlockingArrayQueue<Runnable>(capacity, capacity);
      }

      this._jobs = queue;
      this._threadGroup = threadGroup;
   }

   protected void doStart() throws Exception {
      super.doStart();
      this._threadsStarted.set(0);
      this.startThreads(this._minThreads);
   }

   protected void doStop() throws Exception {
      super.doStop();
      long timeout = this.getStopTimeout();
      BlockingQueue<Runnable> jobs = this.getQueue();
      if (timeout <= 0L) {
         jobs.clear();
      }

      Runnable noop = () -> {
      };
      int i = this._threadsStarted.get();

      while(i-- > 0) {
         jobs.offer(noop);
      }

      long stopby = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout) / 2L;

      for(Thread thread : this._threads) {
         long canwait = TimeUnit.NANOSECONDS.toMillis(stopby - System.nanoTime());
         if (canwait > 0L) {
            thread.join(canwait);
         }
      }

      if (this._threadsStarted.get() > 0) {
         for(Thread thread : this._threads) {
            thread.interrupt();
         }
      }

      stopby = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(timeout) / 2L;

      for(Thread thread : this._threads) {
         long canwait = TimeUnit.NANOSECONDS.toMillis(stopby - System.nanoTime());
         if (canwait > 0L) {
            thread.join(canwait);
         }
      }

      Thread.yield();
      int size = this._threads.size();
      if (size > 0) {
         Thread.yield();
         if (LOG.isDebugEnabled()) {
            for(Thread unstopped : this._threads) {
               StringBuilder dmp = new StringBuilder();

               for(StackTraceElement element : unstopped.getStackTrace()) {
                  dmp.append(System.lineSeparator()).append("\tat ").append(element);
               }

               LOG.warn("Couldn't stop {}{}", unstopped, dmp.toString());
            }
         } else {
            for(Thread unstopped : this._threads) {
               LOG.warn("{} Couldn't stop {}", this, unstopped);
            }
         }
      }

      synchronized(this._joinLock) {
         this._joinLock.notifyAll();
      }
   }

   public void setDaemon(boolean daemon) {
      this._daemon = daemon;
   }

   public void setIdleTimeout(int idleTimeout) {
      this._idleTimeout = idleTimeout;
   }

   public void setMaxThreads(int maxThreads) {
      this._maxThreads = maxThreads;
      if (this._minThreads > this._maxThreads) {
         this._minThreads = this._maxThreads;
      }

   }

   public void setMinThreads(int minThreads) {
      this._minThreads = minThreads;
      if (this._minThreads > this._maxThreads) {
         this._maxThreads = this._minThreads;
      }

      int threads = this._threadsStarted.get();
      if (this.isStarted() && threads < this._minThreads) {
         this.startThreads(this._minThreads - threads);
      }

   }

   public void setName(String name) {
      if (this.isRunning()) {
         throw new IllegalStateException("started");
      } else {
         this._name = name;
      }
   }

   public void setThreadsPriority(int priority) {
      this._priority = priority;
   }

   @ManagedAttribute("maximum time a thread may be idle in ms")
   public int getIdleTimeout() {
      return this._idleTimeout;
   }

   @ManagedAttribute("maximum number of threads in the pool")
   public int getMaxThreads() {
      return this._maxThreads;
   }

   @ManagedAttribute("minimum number of threads in the pool")
   public int getMinThreads() {
      return this._minThreads;
   }

   @ManagedAttribute("name of the thread pool")
   public String getName() {
      return this._name;
   }

   @ManagedAttribute("priority of threads in the pool")
   public int getThreadsPriority() {
      return this._priority;
   }

   @ManagedAttribute("size of the job queue")
   public int getQueueSize() {
      return this._jobs.size();
   }

   @ManagedAttribute("thread pool uses daemon threads")
   public boolean isDaemon() {
      return this._daemon;
   }

   @ManagedAttribute("reports additional details in the dump")
   public boolean isDetailedDump() {
      return this._detailedDump;
   }

   public void setDetailedDump(boolean detailedDump) {
      this._detailedDump = detailedDump;
   }

   @ManagedAttribute("threshold at which the pool is low on threads")
   public int getLowThreadsThreshold() {
      return this._lowThreadsThreshold;
   }

   public void setLowThreadsThreshold(int lowThreadsThreshold) {
      this._lowThreadsThreshold = lowThreadsThreshold;
   }

   public void execute(Runnable job) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("queue {}", job);
      }

      if (this.isRunning() && this._jobs.offer(job)) {
         if (this.getThreads() == 0) {
            this.startThreads(1);
         }

      } else {
         LOG.warn("{} rejected {}", this, job);
         throw new RejectedExecutionException(job.toString());
      }
   }

   public void join() throws InterruptedException {
      synchronized(this._joinLock) {
         while(this.isRunning()) {
            this._joinLock.wait();
         }
      }

      while(this.isStopping()) {
         Thread.sleep(1L);
      }

   }

   @ManagedAttribute("number of threads in the pool")
   public int getThreads() {
      return this._threadsStarted.get();
   }

   @ManagedAttribute("number of idle threads in the pool")
   public int getIdleThreads() {
      return this._threadsIdle.get();
   }

   @ManagedAttribute("number of busy threads in the pool")
   public int getBusyThreads() {
      return this.getThreads() - this.getIdleThreads();
   }

   @ManagedAttribute(
      value = "thread pool is low on threads",
      readonly = true
   )
   public boolean isLowOnThreads() {
      return this.getMaxThreads() - this.getThreads() + this.getIdleThreads() - this.getQueueSize() <= this.getLowThreadsThreshold();
   }

   private boolean startThreads(int threadsToStart) {
      while(threadsToStart > 0 && this.isRunning()) {
         int threads = this._threadsStarted.get();
         if (threads >= this._maxThreads) {
            return false;
         }

         if (this._threadsStarted.compareAndSet(threads, threads + 1)) {
            boolean started = false;

            try {
               Thread thread = this.newThread(this._runnable);
               thread.setDaemon(this.isDaemon());
               thread.setPriority(this.getThreadsPriority());
               thread.setName(this._name + "-" + thread.getId());
               this._threads.add(thread);
               thread.start();
               started = true;
               --threadsToStart;
            } finally {
               if (!started) {
                  this._threadsStarted.decrementAndGet();
               }

            }
         }
      }

      return true;
   }

   protected Thread newThread(Runnable runnable) {
      return new Thread(this._threadGroup, runnable);
   }

   @ManagedOperation("dumps thread pool state")
   public String dump() {
      return ContainerLifeCycle.dump((Dumpable)this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      List<Object> threads = new ArrayList(this.getMaxThreads());

      for(final Thread thread : this._threads) {
         final StackTraceElement[] trace = thread.getStackTrace();
         final boolean inIdleJobPoll = false;

         for(StackTraceElement t : trace) {
            if ("idleJobPoll".equals(t.getMethodName())) {
               inIdleJobPoll = true;
               break;
            }
         }

         if (this.isDetailedDump()) {
            threads.add(new Dumpable() {
               public void dump(Appendable out, String indent) throws IOException {
                  out.append(String.valueOf(thread.getId())).append(' ').append(thread.getName()).append(' ').append(thread.getState().toString()).append(inIdleJobPoll ? " IDLE" : "");
                  if (thread.getPriority() != 5) {
                     out.append(" prio=").append(String.valueOf(thread.getPriority()));
                  }

                  out.append(System.lineSeparator());
                  if (!inIdleJobPoll) {
                     ContainerLifeCycle.dump(out, indent, Arrays.asList(trace));
                  }

               }

               public String dump() {
                  return null;
               }
            });
         } else {
            int p = thread.getPriority();
            threads.add(thread.getId() + " " + thread.getName() + " " + thread.getState() + " @ " + (trace.length > 0 ? trace[0] : "???") + (inIdleJobPoll ? " IDLE" : "") + (p == 5 ? "" : " prio=" + p));
         }
      }

      List<Runnable> jobs = Collections.emptyList();
      if (this.isDetailedDump()) {
         jobs = new ArrayList(this.getQueue());
      }

      ContainerLifeCycle.dumpObject(out, this);
      ContainerLifeCycle.dump(out, indent, threads, jobs);
   }

   public String toString() {
      return String.format("%s{%s,%d<=%d<=%d,i=%d,q=%d}", this._name, this.getState(), this.getMinThreads(), this.getThreads(), this.getMaxThreads(), this.getIdleThreads(), this._jobs == null ? -1 : this._jobs.size());
   }

   private Runnable idleJobPoll() throws InterruptedException {
      return (Runnable)this._jobs.poll((long)this._idleTimeout, TimeUnit.MILLISECONDS);
   }

   protected void runJob(Runnable job) {
      job.run();
   }

   protected BlockingQueue<Runnable> getQueue() {
      return this._jobs;
   }

   /** @deprecated */
   @Deprecated
   public void setQueue(BlockingQueue<Runnable> queue) {
      throw new UnsupportedOperationException("Use constructor injection");
   }

   @ManagedOperation("interrupts a pool thread")
   public boolean interruptThread(@Name("id") long id) {
      for(Thread thread : this._threads) {
         if (thread.getId() == id) {
            thread.interrupt();
            return true;
         }
      }

      return false;
   }

   @ManagedOperation("dumps a pool thread stack")
   public String dumpThread(@Name("id") long id) {
      for(Thread thread : this._threads) {
         if (thread.getId() == id) {
            StringBuilder buf = new StringBuilder();
            buf.append(thread.getId()).append(" ").append(thread.getName()).append(" ");
            buf.append(thread.getState()).append(":").append(System.lineSeparator());

            for(StackTraceElement element : thread.getStackTrace()) {
               buf.append("  at ").append(element.toString()).append(System.lineSeparator());
            }

            return buf.toString();
         }
      }

      return null;
   }
}
