package org.spark_project.jetty.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.component.Dumpable;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ExecutionStrategy;
import org.spark_project.jetty.util.thread.Scheduler;

public abstract class SelectorManager extends ContainerLifeCycle implements Dumpable {
   public static final int DEFAULT_CONNECT_TIMEOUT = 15000;
   protected static final Logger LOG = Log.getLogger(SelectorManager.class);
   private final Executor executor;
   private final Scheduler scheduler;
   private final ManagedSelector[] _selectors;
   private long _connectTimeout;
   private ExecutionStrategy.Factory _executionFactory;
   private long _selectorIndex;

   protected SelectorManager(Executor executor, Scheduler scheduler) {
      this(executor, scheduler, (Runtime.getRuntime().availableProcessors() + 1) / 2);
   }

   protected SelectorManager(Executor executor, Scheduler scheduler, int selectors) {
      this._connectTimeout = 15000L;
      this._executionFactory = ExecutionStrategy.Factory.getDefault();
      if (selectors <= 0) {
         throw new IllegalArgumentException("No selectors");
      } else {
         this.executor = executor;
         this.scheduler = scheduler;
         this._selectors = new ManagedSelector[selectors];
      }
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public Scheduler getScheduler() {
      return this.scheduler;
   }

   public long getConnectTimeout() {
      return this._connectTimeout;
   }

   public void setConnectTimeout(long milliseconds) {
      this._connectTimeout = milliseconds;
   }

   public ExecutionStrategy.Factory getExecutionStrategyFactory() {
      return this._executionFactory;
   }

   public void setExecutionStrategyFactory(ExecutionStrategy.Factory _executionFactory) {
      if (this.isRunning()) {
         throw new IllegalStateException("Cannot change " + ExecutionStrategy.Factory.class.getSimpleName() + " after start()");
      } else {
         this._executionFactory = _executionFactory;
      }
   }

   /** @deprecated */
   @Deprecated
   public int getSelectorPriorityDelta() {
      return 0;
   }

   /** @deprecated */
   @Deprecated
   public void setSelectorPriorityDelta(int selectorPriorityDelta) {
   }

   protected void execute(Runnable task) {
      this.executor.execute(task);
   }

   public int getSelectorCount() {
      return this._selectors.length;
   }

   private ManagedSelector chooseSelector(SocketChannel channel) {
      ManagedSelector candidate1 = null;
      if (channel != null) {
         try {
            SocketAddress remote = channel.getRemoteAddress();
            if (remote instanceof InetSocketAddress) {
               byte[] addr = ((InetSocketAddress)remote).getAddress().getAddress();
               if (addr != null) {
                  int s = addr[addr.length - 1] & 255;
                  candidate1 = this._selectors[s % this.getSelectorCount()];
               }
            }
         } catch (IOException x) {
            LOG.ignore(x);
         }
      }

      long s = (long)(this._selectorIndex++);
      int index = (int)(s % (long)this.getSelectorCount());
      ManagedSelector candidate2 = this._selectors[index];
      return candidate1 != null && candidate1.size() < candidate2.size() * 2 ? candidate1 : candidate2;
   }

   public void connect(SocketChannel channel, Object attachment) {
      ManagedSelector set = this.chooseSelector(channel);
      set.submit(set.new Connect(channel, attachment));
   }

   public void accept(SocketChannel channel) {
      this.accept(channel, (Object)null);
   }

   public void accept(SocketChannel channel, Object attachment) {
      ManagedSelector selector = this.chooseSelector(channel);
      selector.submit(selector.new Accept(channel, attachment));
   }

   public void acceptor(ServerSocketChannel server) {
      ManagedSelector selector = this.chooseSelector((SocketChannel)null);
      selector.submit(selector.new Acceptor(server));
   }

   protected void accepted(SocketChannel channel) throws IOException {
      throw new UnsupportedOperationException();
   }

   protected void doStart() throws Exception {
      for(int i = 0; i < this._selectors.length; ++i) {
         ManagedSelector selector = this.newSelector(i);
         this._selectors[i] = selector;
         this.addBean(selector);
      }

      super.doStart();
   }

   protected ManagedSelector newSelector(int id) {
      return new ManagedSelector(this, id, this.getExecutionStrategyFactory());
   }

   protected void doStop() throws Exception {
      super.doStop();

      for(ManagedSelector selector : this._selectors) {
         this.removeBean(selector);
      }

   }

   protected void endPointOpened(EndPoint endpoint) {
      endpoint.onOpen();
   }

   protected void endPointClosed(EndPoint endpoint) {
      endpoint.onClose();
   }

   public void connectionOpened(Connection connection) {
      try {
         connection.onOpen();
      } catch (Throwable var3) {
         if (this.isRunning()) {
            LOG.warn("Exception while notifying connection " + connection, var3);
         } else {
            LOG.debug("Exception while notifying connection " + connection, var3);
         }

         throw var3;
      }
   }

   public void connectionClosed(Connection connection) {
      try {
         connection.onClose();
      } catch (Throwable x) {
         LOG.debug("Exception while notifying connection " + connection, x);
      }

   }

   protected boolean finishConnect(SocketChannel channel) throws IOException {
      return channel.finishConnect();
   }

   protected void connectionFailed(SocketChannel channel, Throwable ex, Object attachment) {
      LOG.warn(String.format("%s - %s", channel, attachment), ex);
   }

   protected abstract EndPoint newEndPoint(SocketChannel var1, ManagedSelector var2, SelectionKey var3) throws IOException;

   public abstract Connection newConnection(SocketChannel var1, EndPoint var2, Object var3) throws IOException;
}
