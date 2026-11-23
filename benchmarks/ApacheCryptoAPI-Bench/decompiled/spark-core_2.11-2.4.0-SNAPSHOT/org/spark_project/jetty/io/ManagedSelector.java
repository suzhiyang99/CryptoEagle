package org.spark_project.jetty.io;

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.spark_project.jetty.util.component.AbstractLifeCycle;
import org.spark_project.jetty.util.component.ContainerLifeCycle;
import org.spark_project.jetty.util.component.Dumpable;
import org.spark_project.jetty.util.log.Log;
import org.spark_project.jetty.util.log.Logger;
import org.spark_project.jetty.util.thread.ExecutionStrategy;
import org.spark_project.jetty.util.thread.Locker;
import org.spark_project.jetty.util.thread.Scheduler;

public class ManagedSelector extends AbstractLifeCycle implements Runnable, Dumpable {
   private static final Logger LOG = Log.getLogger(ManagedSelector.class);
   private final Locker _locker;
   private boolean _selecting;
   private final Queue<Runnable> _actions;
   private final SelectorManager _selectorManager;
   private final int _id;
   private final ExecutionStrategy _strategy;
   private Selector _selector;

   public ManagedSelector(SelectorManager selectorManager, int id) {
      this(selectorManager, id, ExecutionStrategy.Factory.getDefault());
   }

   public ManagedSelector(SelectorManager selectorManager, int id, ExecutionStrategy.Factory executionFactory) {
      this._locker = new Locker();
      this._selecting = false;
      this._actions = new ArrayDeque();
      this._selectorManager = selectorManager;
      this._id = id;
      this._strategy = executionFactory.newExecutionStrategy(new SelectorProducer(), selectorManager.getExecutor());
      this.setStopTimeout(5000L);
   }

   public ExecutionStrategy getExecutionStrategy() {
      return this._strategy;
   }

   protected void doStart() throws Exception {
      super.doStart();
      this._selector = this.newSelector();
      this._selectorManager.execute(this);
   }

   protected Selector newSelector() throws IOException {
      return Selector.open();
   }

   public int size() {
      Selector s = this._selector;
      return s == null ? 0 : s.keys().size();
   }

   protected void doStop() throws Exception {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Stopping {}", this);
      }

      CloseEndPoints close_endps = new CloseEndPoints();
      this.submit(close_endps);
      close_endps.await(this.getStopTimeout());
      super.doStop();
      CloseSelector close_selector = new CloseSelector();
      this.submit(close_selector);
      close_selector.await(this.getStopTimeout());
      if (LOG.isDebugEnabled()) {
         LOG.debug("Stopped {}", this);
      }

   }

   public void submit(Runnable change) {
      if (LOG.isDebugEnabled()) {
         LOG.debug("Queued change {} on {}", change, this);
      }

      Selector selector = null;

      try (Locker.Lock lock = this._locker.lock()) {
         this._actions.offer(change);
         if (this._selecting) {
            selector = this._selector;
            this._selecting = false;
         }
      }

      if (selector != null) {
         selector.wakeup();
      }

   }

   public void run() {
      this._strategy.execute();
   }

   private Runnable processConnect(SelectionKey key, final Connect connect) {
      SocketChannel channel = (SocketChannel)key.channel();

      try {
         key.attach(connect.attachment);
         boolean connected = this._selectorManager.finishConnect(channel);
         if (LOG.isDebugEnabled()) {
            LOG.debug("Connected {} {}", connected, channel);
         }

         if (connected) {
            if (connect.timeout.cancel()) {
               key.interestOps(0);
               return new CreateEndPoint(channel, key) {
                  protected void failed(Throwable failure) {
                     super.failed(failure);
                     connect.failed(failure);
                  }
               };
            } else {
               throw new SocketTimeoutException("Concurrent Connect Timeout");
            }
         } else {
            throw new ConnectException();
         }
      } catch (Throwable x) {
         connect.failed(x);
         return null;
      }
   }

   private void processAccept(SelectionKey key) {
      ServerSocketChannel server = (ServerSocketChannel)key.channel();
      SocketChannel channel = null;

      try {
         while((channel = server.accept()) != null) {
            this._selectorManager.accepted(channel);
         }
      } catch (Throwable x) {
         this.closeNoExceptions(channel);
         LOG.warn("Accept failed for channel " + channel, x);
      }

   }

   private void closeNoExceptions(Closeable closeable) {
      try {
         if (closeable != null) {
            closeable.close();
         }
      } catch (Throwable x) {
         LOG.ignore(x);
      }

   }

   private EndPoint createEndPoint(SocketChannel channel, SelectionKey selectionKey) throws IOException {
      EndPoint endPoint = this._selectorManager.newEndPoint(channel, this, selectionKey);
      this._selectorManager.endPointOpened(endPoint);
      Connection connection = this._selectorManager.newConnection(channel, endPoint, selectionKey.attachment());
      endPoint.setConnection(connection);
      selectionKey.attach(endPoint);
      this._selectorManager.connectionOpened(connection);
      if (LOG.isDebugEnabled()) {
         LOG.debug("Created {}", endPoint);
      }

      return endPoint;
   }

   public void destroyEndPoint(final EndPoint endPoint) {
      final Connection connection = endPoint.getConnection();
      this.submit(new Product() {
         public void run() {
            if (ManagedSelector.LOG.isDebugEnabled()) {
               ManagedSelector.LOG.debug("Destroyed {}", endPoint);
            }

            if (connection != null) {
               ManagedSelector.this._selectorManager.connectionClosed(connection);
            }

            ManagedSelector.this._selectorManager.endPointClosed(endPoint);
         }
      });
   }

   public String dump() {
      return ContainerLifeCycle.dump((Dumpable)this);
   }

   public void dump(Appendable out, String indent) throws IOException {
      out.append(String.valueOf(this)).append(" id=").append(String.valueOf(this._id)).append(System.lineSeparator());
      Selector selector = this._selector;
      if (selector != null && selector.isOpen()) {
         ArrayList<Object> dump = new ArrayList(selector.keys().size() * 2);
         DumpKeys dumpKeys = new DumpKeys(dump);
         this.submit(dumpKeys);
         dumpKeys.await(5L, TimeUnit.SECONDS);
         ContainerLifeCycle.dump(out, indent, dump);
      }

   }

   public String toString() {
      Selector selector = this._selector;
      return String.format("%s id=%s keys=%d selected=%d", super.toString(), this._id, selector != null && selector.isOpen() ? selector.keys().size() : -1, selector != null && selector.isOpen() ? selector.selectedKeys().size() : -1);
   }

   private class SelectorProducer implements ExecutionStrategy.Producer {
      private Set<SelectionKey> _keys;
      private Iterator<SelectionKey> _cursor;

      private SelectorProducer() {
         this._keys = Collections.emptySet();
         this._cursor = Collections.emptyIterator();
      }

      public Runnable produce() {
         do {
            Runnable task = this.processSelected();
            if (task != null) {
               return task;
            }

            Runnable action = this.runActions();
            if (action != null) {
               return action;
            }

            this.update();
         } while(this.select());

         return null;
      }

      private Runnable runActions() {
         while(true) {
            Runnable action;
            try (Locker.Lock lock = ManagedSelector.this._locker.lock()) {
               action = (Runnable)ManagedSelector.this._actions.poll();
               if (action == null) {
                  ManagedSelector.this._selecting = true;
                  Object var4 = null;
                  return (Runnable)var4;
               }
            }

            if (action instanceof Product) {
               return action;
            }

            this.runChange(action);
         }
      }

      private void runChange(Runnable change) {
         try {
            if (ManagedSelector.LOG.isDebugEnabled()) {
               ManagedSelector.LOG.debug("Running change {}", change);
            }

            change.run();
         } catch (Throwable x) {
            ManagedSelector.LOG.debug("Could not run change " + change, x);
         }

      }

      private boolean select() {
         try {
            Selector selector = ManagedSelector.this._selector;
            if (selector != null && selector.isOpen()) {
               if (ManagedSelector.LOG.isDebugEnabled()) {
                  ManagedSelector.LOG.debug("Selector loop waiting on select");
               }

               int selected = selector.select();
               if (ManagedSelector.LOG.isDebugEnabled()) {
                  ManagedSelector.LOG.debug("Selector loop woken up from select, {}/{} selected", selected, selector.keys().size());
               }

               try (Locker.Lock lock = ManagedSelector.this._locker.lock()) {
                  ManagedSelector.this._selecting = false;
               }

               this._keys = selector.selectedKeys();
               this._cursor = this._keys.iterator();
               return true;
            }
         } catch (Throwable x) {
            ManagedSelector.this.closeNoExceptions(ManagedSelector.this._selector);
            if (ManagedSelector.this.isRunning()) {
               ManagedSelector.LOG.warn(x);
            } else {
               ManagedSelector.LOG.debug(x);
            }
         }

         return false;
      }

      private Runnable processSelected() {
         while(this._cursor.hasNext()) {
            SelectionKey key = (SelectionKey)this._cursor.next();
            if (key.isValid()) {
               Object attachment = key.attachment();

               try {
                  if (attachment instanceof SelectableEndPoint) {
                     Runnable task = ((SelectableEndPoint)attachment).onSelected();
                     if (task != null) {
                        return task;
                     }
                  } else if (key.isConnectable()) {
                     Runnable task = ManagedSelector.this.processConnect(key, (Connect)attachment);
                     if (task != null) {
                        return task;
                     }
                  } else {
                     if (!key.isAcceptable()) {
                        throw new IllegalStateException("key=" + key + ", att=" + attachment + ", iOps=" + key.interestOps() + ", rOps=" + key.readyOps());
                     }

                     ManagedSelector.this.processAccept(key);
                  }
               } catch (CancelledKeyException var4) {
                  ManagedSelector.LOG.debug("Ignoring cancelled key for channel {}", key.channel());
                  if (attachment instanceof EndPoint) {
                     ManagedSelector.this.closeNoExceptions((EndPoint)attachment);
                  }
               } catch (Throwable x) {
                  ManagedSelector.LOG.warn("Could not process key for channel " + key.channel(), x);
                  if (attachment instanceof EndPoint) {
                     ManagedSelector.this.closeNoExceptions((EndPoint)attachment);
                  }
               }
            } else {
               if (ManagedSelector.LOG.isDebugEnabled()) {
                  ManagedSelector.LOG.debug("Selector loop ignoring invalid key for channel {}", key.channel());
               }

               Object attachment = key.attachment();
               if (attachment instanceof EndPoint) {
                  ManagedSelector.this.closeNoExceptions((EndPoint)attachment);
               }
            }
         }

         return null;
      }

      private void update() {
         for(SelectionKey key : this._keys) {
            this.updateKey(key);
         }

         this._keys.clear();
      }

      private void updateKey(SelectionKey key) {
         Object attachment = key.attachment();
         if (attachment instanceof SelectableEndPoint) {
            ((SelectableEndPoint)attachment).updateKey();
         }

      }
   }

   private class DumpKeys implements Runnable {
      private final CountDownLatch latch;
      private final List<Object> _dumps;

      private DumpKeys(List<Object> dumps) {
         this.latch = new CountDownLatch(1);
         this._dumps = dumps;
      }

      public void run() {
         Selector selector = ManagedSelector.this._selector;
         if (selector != null && selector.isOpen()) {
            Set<SelectionKey> keys = selector.keys();
            this._dumps.add(selector + " keys=" + keys.size());

            for(SelectionKey key : keys) {
               try {
                  this._dumps.add(String.format("SelectionKey@%x{i=%d}->%s", key.hashCode(), key.interestOps(), key.attachment()));
               } catch (Throwable x) {
                  ManagedSelector.LOG.ignore(x);
               }
            }
         }

         this.latch.countDown();
      }

      public boolean await(long timeout, TimeUnit unit) {
         try {
            return this.latch.await(timeout, unit);
         } catch (InterruptedException var5) {
            return false;
         }
      }
   }

   class Acceptor implements Runnable {
      private final ServerSocketChannel _channel;

      public Acceptor(ServerSocketChannel channel) {
         this._channel = channel;
      }

      public void run() {
         try {
            SelectionKey key = this._channel.register(ManagedSelector.this._selector, 16, (Object)null);
            if (ManagedSelector.LOG.isDebugEnabled()) {
               ManagedSelector.LOG.debug("{} acceptor={}", this, key);
            }
         } catch (Throwable x) {
            ManagedSelector.this.closeNoExceptions(this._channel);
            ManagedSelector.LOG.warn(x);
         }

      }
   }

   class Accept implements Runnable, Closeable {
      private final SocketChannel channel;
      private final Object attachment;

      Accept(SocketChannel channel, Object attachment) {
         this.channel = channel;
         this.attachment = attachment;
      }

      public void close() {
         ManagedSelector.LOG.debug("closed accept of {}", this.channel);
         ManagedSelector.this.closeNoExceptions(this.channel);
      }

      public void run() {
         try {
            SelectionKey key = this.channel.register(ManagedSelector.this._selector, 0, this.attachment);
            ManagedSelector.this.submit(ManagedSelector.this.new CreateEndPoint(this.channel, key));
         } catch (Throwable x) {
            ManagedSelector.this.closeNoExceptions(this.channel);
            ManagedSelector.LOG.debug(x);
         }

      }
   }

   private class CreateEndPoint implements Product, Closeable {
      private final SocketChannel channel;
      private final SelectionKey key;

      public CreateEndPoint(SocketChannel channel, SelectionKey key) {
         this.channel = channel;
         this.key = key;
      }

      public void run() {
         try {
            ManagedSelector.this.createEndPoint(this.channel, this.key);
         } catch (Throwable x) {
            ManagedSelector.LOG.debug(x);
            this.failed(x);
         }

      }

      public void close() {
         ManagedSelector.LOG.debug("closed creation of {}", this.channel);
         ManagedSelector.this.closeNoExceptions(this.channel);
      }

      protected void failed(Throwable failure) {
         ManagedSelector.this.closeNoExceptions(this.channel);
         ManagedSelector.LOG.debug(failure);
      }
   }

   class Connect implements Runnable {
      private final AtomicBoolean failed = new AtomicBoolean();
      private final SocketChannel channel;
      private final Object attachment;
      private final Scheduler.Task timeout;

      Connect(SocketChannel channel, Object attachment) {
         this.channel = channel;
         this.attachment = attachment;
         this.timeout = ManagedSelector.this._selectorManager.getScheduler().schedule(ManagedSelector.this.new ConnectTimeout(this), ManagedSelector.this._selectorManager.getConnectTimeout(), TimeUnit.MILLISECONDS);
      }

      public void run() {
         try {
            this.channel.register(ManagedSelector.this._selector, 8, this);
         } catch (Throwable x) {
            this.failed(x);
         }

      }

      private void failed(Throwable failure) {
         if (this.failed.compareAndSet(false, true)) {
            this.timeout.cancel();
            ManagedSelector.this.closeNoExceptions(this.channel);
            ManagedSelector.this._selectorManager.connectionFailed(this.channel, failure, this.attachment);
         }

      }
   }

   private class ConnectTimeout implements Runnable {
      private final Connect connect;

      private ConnectTimeout(Connect connect) {
         this.connect = connect;
      }

      public void run() {
         SocketChannel channel = this.connect.channel;
         if (channel.isConnectionPending()) {
            if (ManagedSelector.LOG.isDebugEnabled()) {
               ManagedSelector.LOG.debug("Channel {} timed out while connecting, closing it", channel);
            }

            this.connect.failed(new SocketTimeoutException("Connect Timeout"));
         }

      }
   }

   private class CloseEndPoints implements Runnable {
      private final CountDownLatch _latch;
      private CountDownLatch _allClosed;

      private CloseEndPoints() {
         this._latch = new CountDownLatch(1);
      }

      public void run() {
         List<EndPoint> end_points = new ArrayList();

         for(SelectionKey key : ManagedSelector.this._selector.keys()) {
            if (key.isValid()) {
               Object attachment = key.attachment();
               if (attachment instanceof EndPoint) {
                  end_points.add((EndPoint)attachment);
               }
            }
         }

         int size = end_points.size();
         if (ManagedSelector.LOG.isDebugEnabled()) {
            ManagedSelector.LOG.debug("Closing {} endPoints on {}", size, ManagedSelector.this);
         }

         this._allClosed = new CountDownLatch(size);
         this._latch.countDown();

         for(EndPoint endp : end_points) {
            ManagedSelector.this.submit(ManagedSelector.this.new EndPointCloser(endp, this._allClosed));
         }

         if (ManagedSelector.LOG.isDebugEnabled()) {
            ManagedSelector.LOG.debug("Closed {} endPoints on {}", size, ManagedSelector.this);
         }

      }

      public boolean await(long timeout) {
         try {
            return this._latch.await(timeout, TimeUnit.MILLISECONDS) && this._allClosed.await(timeout, TimeUnit.MILLISECONDS);
         } catch (InterruptedException var4) {
            return false;
         }
      }
   }

   private class EndPointCloser implements Product {
      private final EndPoint _endPoint;
      private final CountDownLatch _latch;

      private EndPointCloser(EndPoint endPoint, CountDownLatch latch) {
         this._endPoint = endPoint;
         this._latch = latch;
      }

      public void run() {
         ManagedSelector.this.closeNoExceptions(this._endPoint.getConnection());
         this._latch.countDown();
      }
   }

   private class CloseSelector implements Runnable {
      private CountDownLatch _latch;

      private CloseSelector() {
         this._latch = new CountDownLatch(1);
      }

      public void run() {
         Selector selector = ManagedSelector.this._selector;
         ManagedSelector.this._selector = null;
         ManagedSelector.this.closeNoExceptions(selector);
         this._latch.countDown();
      }

      public boolean await(long timeout) {
         try {
            return this._latch.await(timeout, TimeUnit.MILLISECONDS);
         } catch (InterruptedException var4) {
            return false;
         }
      }
   }

   private interface Product extends Runnable {
   }

   public interface SelectableEndPoint extends EndPoint {
      Runnable onSelected();

      void updateKey();
   }
}
