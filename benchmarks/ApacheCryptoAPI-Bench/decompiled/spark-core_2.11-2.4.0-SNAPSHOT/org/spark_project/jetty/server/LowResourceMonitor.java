package org.spark_project.jetty.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.spark_project.jetty.io.EndPoint;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.Name;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ScheduledExecutorScheduler;
import org.spark_project.jetty.util.thread.Scheduler;
import org.spark_project.jetty.util.thread.ThreadPool;

@ManagedObject("Monitor for low resource conditions and activate a low resource mode if detected")
public class LowResourceMonitor extends AbstractLifeCycle {
   private static final Logger LOG = Log.getLogger(LowResourceMonitor.class);
   private final Server _server;
   private Scheduler _scheduler;
   private Connector[] _monitoredConnectors;
   private int _period = 1000;
   private int _maxConnections;
   private long _maxMemory;
   private int _lowResourcesIdleTimeout = 1000;
   private int _maxLowResourcesTime = 0;
   private boolean _monitorThreads = true;
   private final AtomicBoolean _low = new AtomicBoolean();
   private String _cause;
   private String _reasons;
   private long _lowStarted;
   private final Runnable _monitor = new Runnable() {
      public void run() {
         if (LowResourceMonitor.this.isRunning()) {
            LowResourceMonitor.this.monitor();
            LowResourceMonitor.this._scheduler.schedule(LowResourceMonitor.this._monitor, (long)LowResourceMonitor.this._period, TimeUnit.MILLISECONDS);
         }

      }
   };

   public LowResourceMonitor(@Name("server") Server server) {
      this._server = server;
   }

   @ManagedAttribute("Are the monitored connectors low on resources?")
   public boolean isLowOnResources() {
      return this._low.get();
   }

   @ManagedAttribute("The reason(s) the monitored connectors are low on resources")
   public String getLowResourcesReasons() {
      return this._reasons;
   }

   @ManagedAttribute("Get the timestamp in ms since epoch that low resources state started")
   public long getLowResourcesStarted() {
      return this._lowStarted;
   }

   @ManagedAttribute("The monitored connectors. If null then all server connectors are monitored")
   public Collection<Connector> getMonitoredConnectors() {
      return this._monitoredConnectors == null ? Collections.emptyList() : Arrays.asList(this._monitoredConnectors);
   }

   public void setMonitoredConnectors(Collection<Connector> monitoredConnectors) {
      if (monitoredConnectors != null && monitoredConnectors.size() != 0) {
         this._monitoredConnectors = (Connector[])monitoredConnectors.toArray(new Connector[monitoredConnectors.size()]);
      } else {
         this._monitoredConnectors = null;
      }

   }

   @ManagedAttribute("The monitor period in ms")
   public int getPeriod() {
      return this._period;
   }

   public void setPeriod(int periodMS) {
      this._period = periodMS;
   }

   @ManagedAttribute("True if low available threads status is monitored")
   public boolean getMonitorThreads() {
      return this._monitorThreads;
   }

   public void setMonitorThreads(boolean monitorThreads) {
      this._monitorThreads = monitorThreads;
   }

   @ManagedAttribute("The maximum connections allowed for the monitored connectors before low resource handling is activated")
   public int getMaxConnections() {
      return this._maxConnections;
   }

   public void setMaxConnections(int maxConnections) {
      this._maxConnections = maxConnections;
   }

   @ManagedAttribute("The maximum memory (in bytes) that can be used before low resources is triggered.  Memory used is calculated as (totalMemory-freeMemory).")
   public long getMaxMemory() {
      return this._maxMemory;
   }

   public void setMaxMemory(long maxMemoryBytes) {
      this._maxMemory = maxMemoryBytes;
   }

   @ManagedAttribute("The idletimeout in ms to apply to all existing connections when low resources is detected")
   public int getLowResourcesIdleTimeout() {
      return this._lowResourcesIdleTimeout;
   }

   public void setLowResourcesIdleTimeout(int lowResourcesIdleTimeoutMS) {
      this._lowResourcesIdleTimeout = lowResourcesIdleTimeoutMS;
   }

   @ManagedAttribute("The maximum time in ms that low resources condition can persist before lowResourcesIdleTimeout is applied to new connections as well as existing connections")
   public int getMaxLowResourcesTime() {
      return this._maxLowResourcesTime;
   }

   public void setMaxLowResourcesTime(int maxLowResourcesTimeMS) {
      this._maxLowResourcesTime = maxLowResourcesTimeMS;
   }

   protected void doStart() throws Exception {
      this._scheduler = (Scheduler)this._server.getBean(Scheduler.class);
      if (this._scheduler == null) {
         this._scheduler = new LRMScheduler();
         this._scheduler.start();
      }

      super.doStart();
      this._scheduler.schedule(this._monitor, (long)this._period, TimeUnit.MILLISECONDS);
   }

   protected void doStop() throws Exception {
      if (this._scheduler instanceof LRMScheduler) {
         this._scheduler.stop();
      }

      super.doStop();
   }

   protected Connector[] getMonitoredOrServerConnectors() {
      return this._monitoredConnectors != null && this._monitoredConnectors.length > 0 ? this._monitoredConnectors : this._server.getConnectors();
   }

   protected void monitor() {
      String reasons = null;
      String cause = "";
      int connections = 0;
      ThreadPool serverThreads = this._server.getThreadPool();
      if (this._monitorThreads && serverThreads.isLowOnThreads()) {
         reasons = this.low(reasons, "Server low on threads: " + serverThreads);
         cause = cause + "S";
      }

      for(Connector connector : this.getMonitoredOrServerConnectors()) {
         connections += connector.getConnectedEndPoints().size();
         Executor executor = connector.getExecutor();
         if (executor instanceof ThreadPool && executor != serverThreads) {
            ThreadPool connectorThreads = (ThreadPool)executor;
            if (this._monitorThreads && connectorThreads.isLowOnThreads()) {
               reasons = this.low(reasons, "Connector low on threads: " + connectorThreads);
               cause = cause + "T";
            }
         }
      }

      if (this._maxConnections > 0 && connections > this._maxConnections) {
         reasons = this.low(reasons, "Max Connections exceeded: " + connections + ">" + this._maxConnections);
         cause = cause + "C";
      }

      long memory = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
      if (this._maxMemory > 0L && memory > this._maxMemory) {
         reasons = this.low(reasons, "Max memory exceeded: " + memory + ">" + this._maxMemory);
         cause = cause + "M";
      }

      if (reasons != null) {
         if (!cause.equals(this._cause)) {
            LOG.warn("Low Resources: {}", reasons);
            this._cause = cause;
         }

         if (this._low.compareAndSet(false, true)) {
            this._reasons = reasons;
            this._lowStarted = System.currentTimeMillis();
            this.setLowResources();
         }

         if (this._maxLowResourcesTime > 0 && System.currentTimeMillis() - this._lowStarted > (long)this._maxLowResourcesTime) {
            this.setLowResources();
         }
      } else if (this._low.compareAndSet(true, false)) {
         LOG.info("Low Resources cleared");
         this._reasons = null;
         this._lowStarted = 0L;
         this._cause = null;
         this.clearLowResources();
      }

   }

   protected void setLowResources() {
      for(Connector connector : this.getMonitoredOrServerConnectors()) {
         for(EndPoint endPoint : connector.getConnectedEndPoints()) {
            endPoint.setIdleTimeout((long)this._lowResourcesIdleTimeout);
         }
      }

   }

   protected void clearLowResources() {
      for(Connector connector : this.getMonitoredOrServerConnectors()) {
         for(EndPoint endPoint : connector.getConnectedEndPoints()) {
            endPoint.setIdleTimeout(connector.getIdleTimeout());
         }
      }

   }

   private String low(String reasons, String newReason) {
      return reasons == null ? newReason : reasons + ", " + newReason;
   }

   private static class LRMScheduler extends ScheduledExecutorScheduler {
      private LRMScheduler() {
      }
   }
}
