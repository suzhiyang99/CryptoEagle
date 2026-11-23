package org.spark_project.jetty.io;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import org.spark_project.jetty.util.annotation.ManagedAttribute;
import org.spark_project.jetty.util.annotation.ManagedObject;
import org.spark_project.jetty.util.annotation.ManagedOperation;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.component.Dumpable;
import org.spark_project.jetty.util.statistic.CounterStatistic;
import org.spark_project.jetty.util.statistic.SampleStatistic;

@ManagedObject("Tracks statistics on connections")
public class ConnectionStatistics extends AbstractLifeCycle implements Connection.Listener, Dumpable {
   private final CounterStatistic _connections = new CounterStatistic();
   private final SampleStatistic _connectionsDuration = new SampleStatistic();
   private final LongAdder _rcvdBytes = new LongAdder();
   private final AtomicLong _bytesInStamp = new AtomicLong();
   private final LongAdder _sentBytes = new LongAdder();
   private final AtomicLong _bytesOutStamp = new AtomicLong();
   private final LongAdder _messagesIn = new LongAdder();
   private final AtomicLong _messagesInStamp = new AtomicLong();
   private final LongAdder _messagesOut = new LongAdder();
   private final AtomicLong _messagesOutStamp = new AtomicLong();

   @ManagedOperation(
      value = "Resets the statistics",
      impact = "ACTION"
   )
   public void reset() {
      this._connections.reset();
      this._connectionsDuration.reset();
      this._rcvdBytes.reset();
      this._bytesInStamp.set(System.nanoTime());
      this._sentBytes.reset();
      this._bytesOutStamp.set(System.nanoTime());
      this._messagesIn.reset();
      this._messagesInStamp.set(System.nanoTime());
      this._messagesOut.reset();
      this._messagesOutStamp.set(System.nanoTime());
   }

   protected void doStart() throws Exception {
      this.reset();
   }

   public void onOpened(Connection connection) {
      if (this.isStarted()) {
         this._connections.increment();
      }
   }

   public void onClosed(Connection connection) {
      if (this.isStarted()) {
         this._connections.decrement();
         long elapsed = System.currentTimeMillis() - connection.getCreatedTimeStamp();
         this._connectionsDuration.set(elapsed);
         long bytesIn = connection.getBytesIn();
         if (bytesIn > 0L) {
            this._rcvdBytes.add(bytesIn);
         }

         long bytesOut = connection.getBytesOut();
         if (bytesOut > 0L) {
            this._sentBytes.add(bytesOut);
         }

         long messagesIn = (long)connection.getMessagesIn();
         if (messagesIn > 0L) {
            this._messagesIn.add(messagesIn);
         }

         long messagesOut = (long)connection.getMessagesOut();
         if (messagesOut > 0L) {
            this._messagesOut.add(messagesOut);
         }

      }
   }

   @ManagedAttribute("Total number of bytes received by tracked connections")
   public long getReceivedBytes() {
      return this._rcvdBytes.sum();
   }

   @ManagedAttribute("Total number of bytes received per second since the last invocation of this method")
   public long getReceivedBytesRate() {
      long now = System.nanoTime();
      long then = this._bytesInStamp.getAndSet(now);
      long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
      return elapsed == 0L ? 0L : this.getReceivedBytes() * 1000L / elapsed;
   }

   @ManagedAttribute("Total number of bytes sent by tracked connections")
   public long getSentBytes() {
      return this._sentBytes.sum();
   }

   @ManagedAttribute("Total number of bytes sent per second since the last invocation of this method")
   public long getSentBytesRate() {
      long now = System.nanoTime();
      long then = this._bytesOutStamp.getAndSet(now);
      long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
      return elapsed == 0L ? 0L : this.getSentBytes() * 1000L / elapsed;
   }

   @ManagedAttribute("The max duration of a connection in ms")
   public long getConnectionDurationMax() {
      return this._connectionsDuration.getMax();
   }

   @ManagedAttribute("The mean duration of a connection in ms")
   public double getConnectionDurationMean() {
      return this._connectionsDuration.getMean();
   }

   @ManagedAttribute("The standard deviation of the duration of a connection")
   public double getConnectionDurationStdDev() {
      return this._connectionsDuration.getStdDev();
   }

   @ManagedAttribute("The total number of connections opened")
   public long getConnectionsTotal() {
      return this._connections.getTotal();
   }

   @ManagedAttribute("The current number of open connections")
   public long getConnections() {
      return this._connections.getCurrent();
   }

   @ManagedAttribute("The max number of open connections")
   public long getConnectionsMax() {
      return this._connections.getMax();
   }

   @ManagedAttribute("The total number of messages received")
   public long getReceivedMessages() {
      return this._messagesIn.sum();
   }

   @ManagedAttribute("Total number of messages received per second since the last invocation of this method")
   public long getReceivedMessagesRate() {
      long now = System.nanoTime();
      long then = this._messagesInStamp.getAndSet(now);
      long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
      return elapsed == 0L ? 0L : this.getReceivedMessages() * 1000L / elapsed;
   }

   @ManagedAttribute("The total number of messages sent")
   public long getSentMessages() {
      return this._messagesOut.sum();
   }

   @ManagedAttribute("Total number of messages sent per second since the last invocation of this method")
   public long getSentMessagesRate() {
      long now = System.nanoTime();
      long then = this._messagesOutStamp.getAndSet(now);
      long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
      return elapsed == 0L ? 0L : this.getSentMessages() * 1000L / elapsed;
   }

   public String dump() {
      return ContainerLifeCycle.dump((Dumpable)this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      ContainerLifeCycle.dumpObject(out, this);
      List<String> children = new ArrayList();
      children.add(String.format("connections=%s", this._connections));
      children.add(String.format("durations=%s", this._connectionsDuration));
      children.add(String.format("bytes in/out=%s/%s", this.getReceivedBytes(), this.getSentBytes()));
      children.add(String.format("messages in/out=%s/%s", this.getReceivedMessages(), this.getSentMessages()));
      ContainerLifeCycle.dump(out, indent, children);
   }

   public String toString() {
      return String.format("%s@%x", this.getClass().getSimpleName(), this.hashCode());
   }
}
