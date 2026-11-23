package org.apache.openejb.resource;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;
import javax.resource.ResourceException;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ValidatingManagedConnectionFactory;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.InvalidTransactionException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import org.apache.geronimo.connector.outbound.AbstractSinglePoolConnectionInterceptor;
import org.apache.geronimo.connector.outbound.ConnectionInfo;
import org.apache.geronimo.connector.outbound.ConnectionInterceptor;
import org.apache.geronimo.connector.outbound.ConnectionReturnAction;
import org.apache.geronimo.connector.outbound.GenericConnectionManager;
import org.apache.geronimo.connector.outbound.ManagedConnectionInfo;
import org.apache.geronimo.connector.outbound.MultiPoolConnectionInterceptor;
import org.apache.geronimo.connector.outbound.SinglePoolConnectionInterceptor;
import org.apache.geronimo.connector.outbound.SinglePoolMatchAllConnectionInterceptor;
import org.apache.geronimo.connector.outbound.SubjectSource;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.LocalTransactions;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.NoPool;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.NoTransactions;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.PartitionedPool;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.PoolingSupport;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.SinglePool;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.TransactionSupport;
import org.apache.geronimo.connector.outbound.connectionmanagerconfig.XATransactions;
import org.apache.geronimo.transaction.manager.NamedXAResourceFactory;
import org.apache.geronimo.transaction.manager.RecoverableTransactionManager;
import org.apache.openejb.OpenEJBRuntimeException;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.reflection.Reflections;

public class GeronimoConnectionManagerFactory {
   private String name;
   private ClassLoader classLoader;
   private TransactionManager transactionManager;
   private String transactionSupport;
   private boolean pooling = true;
   private String partitionStrategy;
   private int poolMaxSize = 10;
   private int poolMinSize;
   private boolean allConnectionsEqual = true;
   private boolean assumeOneMatch = false;
   private int connectionMaxWaitMilliseconds = 5000;
   private int connectionMaxIdleMinutes = 15;
   private ManagedConnectionFactory mcf;
   private int validationIntervalMs = -1;

   public boolean isAssumeOneMatch() {
      return this.assumeOneMatch;
   }

   public void setAssumeOneMatch(boolean assumeOneMatch) {
      this.assumeOneMatch = assumeOneMatch;
   }

   public ManagedConnectionFactory getMcf() {
      return this.mcf;
   }

   public void setMcf(ManagedConnectionFactory mcf) {
      this.mcf = mcf;
   }

   public String getName() {
      return this.name;
   }

   public void setName(String name) {
      this.name = name;
   }

   public ClassLoader getClassLoader() {
      return this.classLoader;
   }

   public void setClassLoader(ClassLoader classLoader) {
      this.classLoader = classLoader;
   }

   public TransactionManager getTransactionManager() {
      return this.transactionManager;
   }

   public void setTransactionManager(TransactionManager transactionManager) {
      this.transactionManager = transactionManager;
   }

   public String getTransactionSupport() {
      return this.transactionSupport;
   }

   public void setTransactionSupport(String transactionSupport) {
      this.transactionSupport = transactionSupport;
   }

   public boolean isPooling() {
      return this.pooling;
   }

   public void setPooling(boolean pooling) {
      this.pooling = pooling;
   }

   public String getPartitionStrategy() {
      return this.partitionStrategy;
   }

   public void setPartitionStrategy(String partitionStrategy) {
      this.partitionStrategy = partitionStrategy;
   }

   public int getPoolMaxSize() {
      return this.poolMaxSize;
   }

   public void setPoolMaxSize(int poolMaxSize) {
      this.poolMaxSize = poolMaxSize;
   }

   public int getPoolMinSize() {
      return this.poolMinSize;
   }

   public void setPoolMinSize(int poolMinSize) {
      this.poolMinSize = poolMinSize;
   }

   public boolean isAllConnectionsEqual() {
      return this.allConnectionsEqual;
   }

   public void setAllConnectionsEqual(boolean allConnectionsEqual) {
      this.allConnectionsEqual = allConnectionsEqual;
   }

   public int getConnectionMaxWaitMilliseconds() {
      return this.connectionMaxWaitMilliseconds;
   }

   public void setConnectionMaxWaitMilliseconds(int connectionMaxWaitMilliseconds) {
      this.connectionMaxWaitMilliseconds = connectionMaxWaitMilliseconds;
   }

   public void setConnectionMaxWaitTime(Duration connectionMaxWait) {
      if (connectionMaxWait.getUnit() == null) {
         connectionMaxWait.setUnit(TimeUnit.MILLISECONDS);
      }

      long milleseconds = TimeUnit.MILLISECONDS.convert(connectionMaxWait.getTime(), connectionMaxWait.getUnit());
      this.setConnectionMaxWaitMilliseconds((int)milleseconds);
   }

   public int getConnectionMaxIdleMinutes() {
      return this.connectionMaxIdleMinutes;
   }

   public void setConnectionMaxIdleMinutes(int connectionMaxIdleMinutes) {
      this.connectionMaxIdleMinutes = connectionMaxIdleMinutes;
   }

   public void setConnectionMaxIdleTime(Duration connectionMaxIdle) {
      if (connectionMaxIdle.getUnit() == null) {
         connectionMaxIdle.setUnit(TimeUnit.MINUTES);
      }

      long minutes = TimeUnit.MINUTES.convert(connectionMaxIdle.getTime(), connectionMaxIdle.getUnit());
      this.setConnectionMaxIdleMinutes((int)minutes);
   }

   public int getValidationInterval() {
      return this.validationIntervalMs < 0 ? -1 : (int)TimeUnit.MILLISECONDS.toMinutes((long)this.validationIntervalMs);
   }

   public void setValidationInterval(int validationInterval) {
      this.validationIntervalMs = validationInterval < 0 ? -1 : (int)TimeUnit.MINUTES.toMillis((long)validationInterval);
   }

   public void setValidationInterval(Duration validationInterval) {
      if (validationInterval.getUnit() == null) {
         validationInterval.setUnit(TimeUnit.MINUTES);
      }

      this.validationIntervalMs = (int)validationInterval.getUnit().toMillis(validationInterval.getTime());
   }

   public GenericConnectionManager create() {
      PoolingSupport poolingSupport = this.createPoolingSupport();
      ClassLoader classLoader = this.classLoader;
      if (classLoader == null) {
         Thread.currentThread().getContextClassLoader();
      }

      if (classLoader == null) {
         classLoader = this.getClass().getClassLoader();
      }

      if (classLoader == null) {
         classLoader = ClassLoader.getSystemClassLoader();
      }

      TransactionSupport txSupport = this.createTransactionSupport();
      RecoverableTransactionManager tm;
      if (this.transactionManager instanceof RecoverableTransactionManager) {
         tm = (RecoverableTransactionManager)this.transactionManager;
      } else {
         if (txSupport.isRecoverable()) {
            throw new OpenEJBRuntimeException("currently recoverable tx support (xa) needs a geronimo tx manager");
         }

         tm = new SimpleRecoverableTransactionManager(this.transactionManager, this.name);
      }

      GenericConnectionManager mgr;
      if (this.validationIntervalMs >= 0 && this.mcf instanceof ValidatingManagedConnectionFactory) {
         if (this.name == null) {
            this.name = this.getClass().getSimpleName();
         }

         mgr = new ValidatingGenericConnectionManager(txSupport, poolingSupport, (SubjectSource)null, new AutoConnectionTracker(), tm, this.mcf, this.name, classLoader, (long)this.validationIntervalMs);
      } else {
         mgr = new GenericConnectionManager(txSupport, poolingSupport, (SubjectSource)null, new AutoConnectionTracker(), tm, this.mcf, this.name, classLoader);
      }

      return mgr;
   }

   private TransactionSupport createTransactionSupport() {
      if (this.transactionSupport != null && !"local".equalsIgnoreCase(this.transactionSupport)) {
         if ("none".equalsIgnoreCase(this.transactionSupport)) {
            return NoTransactions.INSTANCE;
         } else if ("xa".equalsIgnoreCase(this.transactionSupport)) {
            return new XATransactions(true, false);
         } else {
            throw new IllegalArgumentException("Unknown transaction type " + this.transactionSupport);
         }
      } else {
         return LocalTransactions.INSTANCE;
      }
   }

   private PoolingSupport createPoolingSupport() {
      if (!this.pooling) {
         return new NoPool();
      } else if (this.partitionStrategy != null && !"none".equalsIgnoreCase(this.partitionStrategy)) {
         if ("by-connector-properties".equalsIgnoreCase(this.partitionStrategy)) {
            return new PartitionedPool(this.poolMaxSize, this.poolMinSize, this.connectionMaxWaitMilliseconds, this.connectionMaxIdleMinutes, this.allConnectionsEqual, !this.allConnectionsEqual, this.assumeOneMatch, true, false);
         } else if ("by-subject".equalsIgnoreCase(this.partitionStrategy)) {
            return new PartitionedPool(this.poolMaxSize, this.poolMinSize, this.connectionMaxWaitMilliseconds, this.connectionMaxIdleMinutes, this.allConnectionsEqual, !this.allConnectionsEqual, this.assumeOneMatch, false, true);
         } else {
            throw new IllegalArgumentException("Unknown partition strategy " + this.partitionStrategy);
         }
      } else {
         return new SinglePool(this.poolMaxSize, this.poolMinSize, this.connectionMaxWaitMilliseconds, this.connectionMaxIdleMinutes, this.allConnectionsEqual, !this.allConnectionsEqual, this.assumeOneMatch);
      }
   }

   private class SimpleRecoverableTransactionManager implements RecoverableTransactionManager {
      private final TransactionManager delegate;
      private final String name;

      public SimpleRecoverableTransactionManager(TransactionManager transactionManager, String name) {
         this.delegate = transactionManager;
         this.name = name;
      }

      public void recoveryError(Exception e) {
         throw new UnsupportedOperationException();
      }

      public void registerNamedXAResourceFactory(NamedXAResourceFactory namedXAResourceFactory) {
         if ((this.name != null || namedXAResourceFactory != null) && (namedXAResourceFactory == null || namedXAResourceFactory.getName() != null) && (this.name == null || namedXAResourceFactory == null || !this.name.equals(namedXAResourceFactory.getName()))) {
            throw new UnsupportedOperationException();
         }
      }

      public void unregisterNamedXAResourceFactory(String namedXAResourceFactoryName) {
         if ((this.name != null || namedXAResourceFactoryName != null) && (this.name == null || !this.name.equals(namedXAResourceFactoryName))) {
            throw new UnsupportedOperationException();
         }
      }

      public void begin() throws NotSupportedException, SystemException {
         this.delegate.begin();
      }

      public void commit() throws HeuristicMixedException, HeuristicRollbackException, IllegalStateException, RollbackException, SecurityException, SystemException {
         this.delegate.commit();
      }

      public int getStatus() throws SystemException {
         return this.delegate.getStatus();
      }

      public Transaction getTransaction() throws SystemException {
         return this.delegate.getTransaction();
      }

      public void resume(Transaction transaction) throws IllegalStateException, InvalidTransactionException, SystemException {
         this.delegate.resume(transaction);
      }

      public void rollback() throws IllegalStateException, SecurityException, SystemException {
         this.delegate.rollback();
      }

      public void setRollbackOnly() throws IllegalStateException, SystemException {
         this.delegate.setRollbackOnly();
      }

      public void setTransactionTimeout(int i) throws SystemException {
         this.delegate.setTransactionTimeout(i);
      }

      public Transaction suspend() throws SystemException {
         return this.delegate.suspend();
      }
   }

   private static class ValidatingGenericConnectionManager extends GenericConnectionManager {
      private static final Timer TIMER = new Timer("ValidatingGenericConnectionManagerTimer", true);
      private final TimerTask validatingTask;
      private final long validationInterval;
      private final ReadWriteLock lock;
      private final Object pool;

      public ValidatingGenericConnectionManager(TransactionSupport txSupport, PoolingSupport poolingSupport, SubjectSource o, AutoConnectionTracker autoConnectionTracker, RecoverableTransactionManager tm, ManagedConnectionFactory mcf, String name, ClassLoader classLoader, long interval) {
         super(txSupport, poolingSupport, o, autoConnectionTracker, tm, mcf, name, classLoader);
         this.validationInterval = interval;
         ConnectionInterceptor stack = this.interceptors.getStack();
         ReadWriteLock foundLock = null;
         ConnectionInterceptor current = stack;

         do {
            if (current instanceof AbstractSinglePoolConnectionInterceptor) {
               try {
                  Field resizeLock = AbstractSinglePoolConnectionInterceptor.class.getDeclaredField("resizeLock");
                  if (!resizeLock.isAccessible()) {
                     resizeLock.setAccessible(true);
                  }

                  foundLock = (ReadWriteLock)resizeLock.get(current);
               } catch (NoSuchFieldException | IllegalAccessException var15) {
               }
               break;
            }

            try {
               current = (ConnectionInterceptor)Reflections.get(current, "next");
            } catch (Exception var16) {
               current = null;
            }
         } while(current != null);

         this.lock = foundLock;
         Object foundPool = null;
         if (current instanceof AbstractSinglePoolConnectionInterceptor) {
            foundPool = Reflections.get(current, "pool");
         } else if (current instanceof MultiPoolConnectionInterceptor) {
            log.warn("validation on stack " + stack + " not supported");
         }

         this.pool = foundPool;
         if (this.pool != null) {
            this.validatingTask = new ValidatingTask(current, this.lock, this.pool, autoConnectionTracker);
         } else {
            this.validatingTask = null;
         }

      }

      public void doStart() throws Exception {
         super.doStart();
         if (this.validatingTask != null) {
            TIMER.schedule(this.validatingTask, this.validationInterval, this.validationInterval);
         }

      }

      public void doStop() throws Exception {
         if (this.validatingTask != null) {
            this.validatingTask.cancel();
         }

         super.doStop();
      }

      private class ValidatingTask extends TimerTask {
         private final ConnectionInterceptor stack;
         private final ReadWriteLock lock;
         private final Object pool;
         private final AutoConnectionTracker autoConnectionTracker;

         public ValidatingTask(ConnectionInterceptor stack, ReadWriteLock lock, Object pool, AutoConnectionTracker autoConnectionTracker) {
            this.stack = stack;
            this.lock = lock;
            this.pool = pool == null ? new Object() : pool;
            this.autoConnectionTracker = autoConnectionTracker;
            if (!SinglePoolConnectionInterceptor.class.isInstance(stack) && !SinglePoolMatchAllConnectionInterceptor.class.isInstance(stack)) {
               GeronimoConnectionManagerFactory.ValidatingGenericConnectionManager.log.info("stack " + stack + " currently not supported, only AutoConnectionTracker ref will be used for validation");
            }

         }

         public void run() {
            synchronized(this.pool) {
               if (this.lock != null) {
                  this.lock.writeLock().lock();
               }

               try {
                  Map<ManagedConnection, ManagedConnectionInfo> connections;
                  if (this.stack instanceof SinglePoolConnectionInterceptor) {
                     connections = new HashMap();

                     for(ManagedConnectionInfo info : (List)this.pool) {
                        connections.put(info.getManagedConnection(), info);
                     }
                  } else if (this.stack instanceof SinglePoolMatchAllConnectionInterceptor) {
                     connections = (Map)this.pool;
                  } else {
                     connections = new HashMap();
                  }

                  for(ManagedConnectionInfo info : this.autoConnectionTracker.connections()) {
                     connections.put(info.getManagedConnection(), info);
                  }

                  try {
                     Set<ManagedConnection> invalids = ((ValidatingManagedConnectionFactory)ValidatingManagedConnectionFactory.class.cast(ValidatingGenericConnectionManager.this.getManagedConnectionFactory())).getInvalidConnections(connections.keySet());
                     if (invalids != null) {
                        for(ManagedConnection invalid : invalids) {
                           ManagedConnectionInfo mci = (ManagedConnectionInfo)connections.get(invalid);
                           if (mci != null) {
                              this.stack.returnConnection(new ConnectionInfo(mci), ConnectionReturnAction.DESTROY);
                           } else {
                              GeronimoConnectionManagerFactory.ValidatingGenericConnectionManager.log.error("Can't find " + invalid + " in " + this.pool);
                           }
                        }
                     }
                  } catch (ResourceException e) {
                     GeronimoConnectionManagerFactory.ValidatingGenericConnectionManager.log.error(e.getMessage(), e);
                  }
               } finally {
                  if (this.lock != null) {
                     this.lock.writeLock().unlock();
                  }

               }

            }
         }
      }
   }
}
