package org.apache.openejb.resource;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.concurrent.TimeUnit;
import javax.transaction.NotSupportedException;
import javax.transaction.SystemException;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import org.apache.geronimo.transaction.log.HOWLLog;
import org.apache.geronimo.transaction.manager.ExponentialtIntervalRetryScheduler;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.apache.geronimo.transaction.manager.TransactionLog;
import org.apache.geronimo.transaction.manager.TransactionManagerImpl;
import org.apache.geronimo.transaction.manager.WrapperNamedXAResource;
import org.apache.geronimo.transaction.manager.XidFactory;
import org.apache.geronimo.transaction.manager.XidFactoryImpl;
import org.apache.openejb.api.internal.Internal;
import org.apache.openejb.api.jmx.Description;
import org.apache.openejb.api.jmx.MBean;
import org.apache.openejb.api.jmx.ManagedAttribute;
import org.apache.openejb.api.jmx.ManagedOperation;
import org.apache.openejb.api.resource.DestroyableResource;
import org.apache.openejb.core.CoreUserTransaction;
import org.apache.openejb.loader.SystemInstance;
import org.apache.openejb.monitoring.LocalMBeanServer;
import org.apache.openejb.monitoring.ObjectNameBuilder;
import org.apache.openejb.util.Duration;
import org.apache.openejb.util.LogCategory;
import org.apache.openejb.util.Logger;

public class GeronimoTransactionManagerFactory {
   private static final byte[] DEFAULT_TM_ID = new byte[]{71, 84, 77, 73, 68};
   private static final int DEFAULT_BUFFER_SIZE = 32;

   public static GeronimoTransactionManager create(Integer defaultTransactionTimeoutSeconds, Duration defaultTransactionTimeout, boolean txRecovery, byte[] tmId, String bufferClassName, int bufferSizeKb, boolean checksumEnabled, boolean adler32Checksum, Integer flushSleepTimeMilliseconds, Duration flushSleepTime, String logFileDir, String logFileExt, String logFileName, int maxBlocksPerFile, int maxBuffers, int maxLogFiles, int minBuffers, int threadsWaitingForceThreshold) throws Exception {
      if (flushSleepTime.getUnit() == null) {
         flushSleepTime.setUnit(TimeUnit.MILLISECONDS);
      }

      if (flushSleepTimeMilliseconds == null) {
         flushSleepTimeMilliseconds = (int)TimeUnit.MILLISECONDS.convert(flushSleepTime.getTime(), flushSleepTime.getUnit());
      }

      if (defaultTransactionTimeout.getUnit() == null) {
         defaultTransactionTimeout.setUnit(TimeUnit.SECONDS);
      }

      if (defaultTransactionTimeoutSeconds == null) {
         defaultTransactionTimeoutSeconds = (int)TimeUnit.SECONDS.convert(defaultTransactionTimeout.getTime(), defaultTransactionTimeout.getUnit());
      }

      XidFactory xidFactory = null;
      TransactionLog txLog = null;
      if (txRecovery) {
         SystemInstance.get().setComponent(XAResourceWrapper.class, new GeronimoXAResourceWrapper());
         xidFactory = new XidFactoryImpl(tmId == null ? DEFAULT_TM_ID : tmId);
         txLog = new HOWLLog(bufferClassName == null ? "org.objectweb.howl.log.BlockLogBuffer" : bufferClassName, bufferSizeKb == 0 ? 32 : bufferSizeKb, checksumEnabled, adler32Checksum, flushSleepTimeMilliseconds, logFileDir, logFileExt, logFileName, maxBlocksPerFile, maxBuffers, maxLogFiles, minBuffers, threadsWaitingForceThreshold, xidFactory, SystemInstance.get().getBase().getDirectory("."));
         ((HOWLLog)txLog).doStart();
      }

      GeronimoTransactionManager geronimoTransactionManager = new DestroyableTransactionManager(defaultTransactionTimeoutSeconds, xidFactory, txLog);
      ObjectNameBuilder jmxName = (new ObjectNameBuilder("openejb.management")).set("j2eeType", "TransactionManager");
      LocalMBeanServer.registerDynamicWrapperSilently(new TransactionManagerMBean(geronimoTransactionManager, defaultTransactionTimeout, txLog), jmxName.build());
      return geronimoTransactionManager;
   }

   public static class DestroyableTransactionManager extends GeronimoTransactionManager implements DestroyableResource {
      private final TransactionLog txLog;

      public DestroyableTransactionManager(int defaultTransactionTimeoutSeconds, XidFactory xidFactory, TransactionLog transactionLog) throws XAException {
         super(defaultTransactionTimeoutSeconds, xidFactory, transactionLog);
         this.txLog = transactionLog;
      }

      public void destroyResource() {
         try {
            Field f = TransactionManagerImpl.class.getDeclaredField("retryScheduler");
            f.setAccessible(true);
            ExponentialtIntervalRetryScheduler rs = (ExponentialtIntervalRetryScheduler)ExponentialtIntervalRetryScheduler.class.cast(f.get(this));
            Field t = ExponentialtIntervalRetryScheduler.class.getDeclaredField("timer");
            t.setAccessible(true);
            Timer timer = (Timer)Timer.class.cast(t.get(rs));
            timer.cancel();
         } catch (Throwable var6) {
         }

         if (this.txLog != null) {
            try {
               ((HOWLLog)HOWLLog.class.cast(this.txLog)).doStop();
            } catch (Throwable e) {
               Logger.getInstance(LogCategory.OPENEJB, DestroyableTransactionManager.class).error(e.getMessage(), e);
            }
         }

      }

      public void begin() throws NotSupportedException, SystemException {
         try {
            super.begin();
         } catch (NotSupportedException nse) {
            RuntimeException re = CoreUserTransaction.error();
            if (re != null) {
               throw re;
            } else {
               throw nse;
            }
         }
      }
   }

   public static class GeronimoXAResourceWrapper implements XAResourceWrapper {
      public XAResource wrap(XAResource xaResource, String name) {
         return new WrapperNamedXAResource(xaResource, name);
      }
   }

   @MBean
   @Internal
   @Description("Transaction manager statistics")
   public static final class TransactionManagerMBean {
      private final GeronimoTransactionManager transactionManager;
      private final Duration defaultTransactionTimeout;
      private final TransactionLog txLog;

      public TransactionManagerMBean(GeronimoTransactionManager transactionManager, Duration defaultTransactionTimeout, TransactionLog txLog) {
         this.transactionManager = transactionManager;
         this.defaultTransactionTimeout = defaultTransactionTimeout;
         this.txLog = txLog;
      }

      @ManagedAttribute
      @Description("Number of active transactions")
      public long getActive() {
         return this.transactionManager.getActiveCount();
      }

      @ManagedAttribute
      @Description("Number of committed transactions")
      public long getCommits() {
         return this.transactionManager.getTotalCommits();
      }

      @ManagedAttribute
      @Description("Number of rolled back transactions")
      public long getRollbacks() {
         return this.transactionManager.getTotalRollbacks();
      }

      @ManagedOperation
      @Description("Reset statistics counters")
      public void resetStatistics() {
         this.transactionManager.resetStatistics();
      }

      @ManagedAttribute
      @Description("Display the default transaction timeout")
      public String getDefaultTransactionTimeout() {
         return this.defaultTransactionTimeout.toString();
      }
   }
}
