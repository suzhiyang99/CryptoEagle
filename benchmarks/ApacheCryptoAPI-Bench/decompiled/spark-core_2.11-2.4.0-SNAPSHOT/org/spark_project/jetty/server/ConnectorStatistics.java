package org.spark_project.jetty.server;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import org.spark_project.jetty.io.Connection;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.component.Container;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.component.Dumpable;
import org.spark_project.jetty.util.statistic.CounterStatistic;
import org.spark_project.jetty.util.statistic.SampleStatistic;

/** @deprecated */
@Deprecated
@ManagedObject("Connector Statistics")
public class ConnectorStatistics extends AbstractLifeCycle implements Dumpable, Connection.Listener {
   private static final Sample ZERO = new Sample();
   private final AtomicLong _startMillis = new AtomicLong(-1L);
   private final CounterStatistic _connectionStats = new CounterStatistic();
   private final SampleStatistic _messagesIn = new SampleStatistic();
   private final SampleStatistic _messagesOut = new SampleStatistic();
   private final SampleStatistic _connectionDurationStats = new SampleStatistic();
   private final ConcurrentMap<Connection, Sample> _samples = new ConcurrentHashMap();
   private final LongAdder _closedIn = new LongAdder();
   private final LongAdder _closedOut = new LongAdder();
   private AtomicLong _nanoStamp = new AtomicLong();
   private volatile int _messagesInPerSecond;
   private volatile int _messagesOutPerSecond;
   private static final long SECOND_NANOS;

   public void onOpened(Connection connection) {
      if (this.isStarted()) {
         this._connectionStats.increment();
         this._samples.put(connection, ZERO);
      }

   }

   public void onClosed(Connection connection) {
      if (this.isStarted()) {
         int msgsIn = connection.getMessagesIn();
         int msgsOut = connection.getMessagesOut();
         this._messagesIn.set((long)msgsIn);
         this._messagesOut.set((long)msgsOut);
         this._connectionStats.decrement();
         this._connectionDurationStats.set(System.currentTimeMillis() - connection.getCreatedTimeStamp());
         Sample sample = (Sample)this._samples.remove(connection);
         if (sample != null) {
            this._closedIn.add((long)(msgsIn - sample._messagesIn));
            this._closedOut.add((long)(msgsOut - sample._messagesOut));
         }
      }

   }

   @ManagedAttribute("Total number of bytes received by this connector")
   public int getBytesIn() {
      return -1;
   }

   @ManagedAttribute("Total number of bytes sent by this connector")
   public int getBytesOut() {
      return -1;
   }

   @ManagedAttribute("Total number of connections seen by this connector")
   public int getConnections() {
      return (int)this._connectionStats.getTotal();
   }

   @ManagedAttribute("Connection duration maximum in ms")
   public long getConnectionDurationMax() {
      return this._connectionDurationStats.getMax();
   }

   @ManagedAttribute("Connection duration mean in ms")
   public double getConnectionDurationMean() {
      return this._connectionDurationStats.getMean();
   }

   @ManagedAttribute("Connection duration standard deviation")
   public double getConnectionDurationStdDev() {
      return this._connectionDurationStats.getStdDev();
   }

   @ManagedAttribute("Messages In for all connections")
   public int getMessagesIn() {
      return (int)this._messagesIn.getTotal();
   }

   @ManagedAttribute("Messages In per connection maximum")
   public int getMessagesInPerConnectionMax() {
      return (int)this._messagesIn.getMax();
   }

   @ManagedAttribute("Messages In per connection mean")
   public double getMessagesInPerConnectionMean() {
      return this._messagesIn.getMean();
   }

   @ManagedAttribute("Messages In per connection standard deviation")
   public double getMessagesInPerConnectionStdDev() {
      return this._messagesIn.getStdDev();
   }

   @ManagedAttribute("Connections open")
   public int getConnectionsOpen() {
      return (int)this._connectionStats.getCurrent();
   }

   @ManagedAttribute("Connections open maximum")
   public int getConnectionsOpenMax() {
      return (int)this._connectionStats.getMax();
   }

   @ManagedAttribute("Messages Out for all connections")
   public int getMessagesOut() {
      return (int)this._messagesIn.getTotal();
   }

   @ManagedAttribute("Messages In per connection maximum")
   public int getMessagesOutPerConnectionMax() {
      return (int)this._messagesIn.getMax();
   }

   @ManagedAttribute("Messages In per connection mean")
   public double getMessagesOutPerConnectionMean() {
      return this._messagesIn.getMean();
   }

   @ManagedAttribute("Messages In per connection standard deviation")
   public double getMessagesOutPerConnectionStdDev() {
      return this._messagesIn.getStdDev();
   }

   @ManagedAttribute("Connection statistics started ms since epoch")
   public long getStartedMillis() {
      long start = this._startMillis.get();
      return start < 0L ? 0L : System.currentTimeMillis() - start;
   }

   @ManagedAttribute("Messages in per second calculated over period since last called")
   public int getMessagesInPerSecond() {
      this.update();
      return this._messagesInPerSecond;
   }

   @ManagedAttribute("Messages out per second calculated over period since last called")
   public int getMessagesOutPerSecond() {
      this.update();
      return this._messagesOutPerSecond;
   }

   public void doStart() {
      this.reset();
   }

   public void doStop() {
      this._samples.clear();
   }

   @ManagedOperation("Reset the statistics")
   public void reset() {
      this._startMillis.set(System.currentTimeMillis());
      this._messagesIn.reset();
      this._messagesOut.reset();
      this._connectionStats.reset();
      this._connectionDurationStats.reset();
      this._samples.clear();
   }

   @ManagedOperation("dump thread state")
   public String dump() {
      return ContainerLifeCycle.dump((Dumpable)this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      ContainerLifeCycle.dumpObject(out, this);
      ContainerLifeCycle.dump(out, indent, Arrays.asList("connections=" + this._connectionStats, "duration=" + this._connectionDurationStats, "in=" + this._messagesIn, "out=" + this._messagesOut));
   }

   public static void addToAllConnectors(Server server) {
      for(Connector connector : server.getConnectors()) {
         if (connector instanceof Container) {
            ((Container)connector).addBean(new ConnectorStatistics());
         }
      }

   }

   private synchronized void update() {
      long now = System.nanoTime();
      long then = this._nanoStamp.get();
      long duration = now - then;
      if (duration > SECOND_NANOS / 2L && this._nanoStamp.compareAndSet(then, now)) {
         long msgsIn = this._closedIn.sumThenReset();
         long msgsOut = this._closedOut.sumThenReset();

         for(Map.Entry<Connection, Sample> entry : this._samples.entrySet()) {
            Connection connection = (Connection)entry.getKey();
            Sample sample = (Sample)entry.getValue();
            Sample next = new Sample(connection);
            if (this._samples.replace(connection, sample, next)) {
               msgsIn += (long)(next._messagesIn - sample._messagesIn);
               msgsOut += (long)(next._messagesOut - sample._messagesOut);
            }
         }

         this._messagesInPerSecond = (int)(msgsIn * SECOND_NANOS / duration);
         this._messagesOutPerSecond = (int)(msgsOut * SECOND_NANOS / duration);
      }

   }

   static {
      SECOND_NANOS = TimeUnit.SECONDS.toNanos(1L);
   }

   private static class Sample {
      final int _messagesIn;
      final int _messagesOut;

      Sample() {
         this._messagesIn = 0;
         this._messagesOut = 0;
      }

      Sample(Connection connection) {
         this._messagesIn = connection.getMessagesIn();
         this._messagesOut = connection.getMessagesOut();
      }
   }
}
