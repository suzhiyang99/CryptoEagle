package org.apache.manifoldcf.core.connectorpool;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.manifoldcf.core.interfaces.ConfigParams;
import org.apache.manifoldcf.core.interfaces.IConnector;
import org.apache.manifoldcf.core.interfaces.ILockManager;
import org.apache.manifoldcf.core.interfaces.IServiceCleanup;
import org.apache.manifoldcf.core.interfaces.IServiceDataAcceptor;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.LockManagerFactory;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.ManifoldCF;

public abstract class ConnectorPool<T extends IConnector> {
   public static final String _rcsid = "@(#)$Id$";
   protected static final String targetCalcLockPrefix = "_POOLTARGET_";
   protected final String serviceTypePrefix;
   protected final Map<String, ConnectorPool<T>.Pool> poolHash = new HashMap();
   protected static final Random randomNumberGenerator = new Random();

   protected ConnectorPool(String serviceTypePrefix) {
      this.serviceTypePrefix = serviceTypePrefix;
   }

   protected abstract boolean isInstalled(IThreadContext var1, String var2) throws ManifoldCFException;

   protected abstract boolean isConnectionNameValid(IThreadContext var1, String var2) throws ManifoldCFException;

   protected T createConnectorInstance(IThreadContext threadContext, String className) throws ManifoldCFException {
      if (!this.isInstalled(threadContext, className)) {
         return null;
      } else {
         try {
            Class theClass = ManifoldCF.findClass(className);
            Class[] argumentClasses = new Class[0];
            Constructor c = theClass.getConstructor(argumentClasses);
            Object[] arguments = new Object[0];
            Object o = c.newInstance(arguments);

            try {
               return (T)(o);
            } catch (ClassCastException var9) {
               throw new ManifoldCFException("Class '" + className + "' does not implement IConnector.");
            }
         } catch (InvocationTargetException e) {
            Throwable z = e.getTargetException();
            if (z instanceof Error) {
               throw (Error)z;
            } else if (z instanceof RuntimeException) {
               throw (RuntimeException)z;
            } else if (z instanceof ManifoldCFException) {
               throw (ManifoldCFException)z;
            } else {
               throw new RuntimeException("Unknown exception type: " + z.getClass().getName() + ": " + z.getMessage(), z);
            }
         } catch (ClassNotFoundException var11) {
            return null;
         } catch (NoSuchMethodException e) {
            throw new ManifoldCFException("No appropriate constructor for IConnector implementation '" + className + "'.  Need xxx(ConfigParams).", e);
         } catch (SecurityException e) {
            throw new ManifoldCFException("Protected constructor for IConnector implementation '" + className + "'", e);
         } catch (IllegalAccessException e) {
            throw new ManifoldCFException("Unavailable constructor for IConnector implementation '" + className + "'", e);
         } catch (IllegalArgumentException e) {
            throw new ManifoldCFException("Shouldn't happen!!!", e);
         } catch (InstantiationException e) {
            throw new ManifoldCFException("InstantiationException for IConnector implementation '" + className + "'", e);
         } catch (ExceptionInInitializerError e) {
            throw new ManifoldCFException("ExceptionInInitializerError for IConnector implementation '" + className + "'", e);
         }
      }
   }

   public T[] grabMultiple(IThreadContext threadContext, Class<T> clazz, String[] orderingKeys, String[] connectionNames, String[] classNames, ConfigParams[] configInfos, int[] maxPoolSizes) throws ManifoldCFException {
      T[] rval = (IConnector[])Array.newInstance(clazz, classNames.length);
      Map<String, Integer> orderMap = new HashMap();

      for(int i = 0; i < orderingKeys.length; ++i) {
         if (orderMap.get(orderingKeys[i]) != null) {
            throw new ManifoldCFException("Found duplicate order key");
         }

         orderMap.put(orderingKeys[i], new Integer(i));
      }

      Arrays.sort(orderingKeys);

      for(int i = 0; i < orderingKeys.length; ++i) {
         String orderingKey = orderingKeys[i];
         int index = (Integer)orderMap.get(orderingKey);
         String connectionName = connectionNames[index];
         String className = classNames[index];
         ConfigParams cp = configInfos[index];
         int maxPoolSize = maxPoolSizes[index];

         try {
            T connector = this.grab(threadContext, connectionName, className, cp, maxPoolSize);
            rval[index] = connector;
         } catch (Throwable e) {
            while(i > 0) {
               --i;
               orderingKey = orderingKeys[i];
               index = (Integer)orderMap.get(orderingKey);

               try {
                  this.release(threadContext, connectionName, rval[index]);
               } catch (ManifoldCFException var19) {
               }
            }

            if (e instanceof ManifoldCFException) {
               throw (ManifoldCFException)e;
            }

            if (e instanceof RuntimeException) {
               throw (RuntimeException)e;
            }

            if (e instanceof Error) {
               throw (Error)e;
            }

            throw new RuntimeException("Unexpected exception type: " + e.getClass().getName() + ": " + e.getMessage(), e);
         }
      }

      return rval;
   }

   public T grab(IThreadContext threadContext, String connectionName, String className, ConfigParams configInfo, int maxPoolSize) throws ManifoldCFException {
      T rval;
      do {
         ConnectorPool<T>.Pool p;
         synchronized(this.poolHash) {
            p = (Pool)this.poolHash.get(connectionName);
            if (p == null) {
               p = new Pool(threadContext, maxPoolSize, connectionName);
               this.poolHash.put(connectionName, p);
               p.pollAll(threadContext);
            } else {
               p.updateMaximumPoolSize(threadContext, maxPoolSize);
            }
         }

         rval = p.getConnector(threadContext, className, configInfo);
      } while(rval == null);

      return rval;
   }

   public void releaseMultiple(IThreadContext threadContext, String[] connectionNames, T[] connectors) throws ManifoldCFException {
      ManifoldCFException currentException = null;

      for(int i = 0; i < connectors.length; ++i) {
         String connectionName = connectionNames[i];
         T c = connectors[i];

         try {
            this.release(threadContext, connectionName, c);
         } catch (ManifoldCFException e) {
            if (currentException == null) {
               currentException = e;
            }
         }
      }

      if (currentException != null) {
         throw currentException;
      }
   }

   public void release(IThreadContext threadContext, String connectionName, T connector) throws ManifoldCFException {
      if (connector != null) {
         ConnectorPool<T>.Pool p;
         synchronized(this.poolHash) {
            p = (Pool)this.poolHash.get(connectionName);
         }

         if (p != null) {
            p.releaseConnector(threadContext, connector);
         } else {
            connector.setThreadContext(threadContext);

            try {
               connector.disconnect();
            } finally {
               connector.clearThreadContext();
            }
         }

      }
   }

   public void pollAllConnectors(IThreadContext threadContext) throws ManifoldCFException {
      synchronized(this.poolHash) {
         Iterator<String> iter = this.poolHash.keySet().iterator();

         while(iter.hasNext()) {
            String connectionName = (String)iter.next();
            ConnectorPool<T>.Pool p = (Pool)this.poolHash.get(connectionName);
            if (this.isConnectionNameValid(threadContext, connectionName)) {
               p.pollAll(threadContext);
            } else {
               p.releaseAll(threadContext);
               iter.remove();
            }
         }

      }
   }

   public void flushUnusedConnectors(IThreadContext threadContext) throws ManifoldCFException {
      synchronized(this.poolHash) {
         for(ConnectorPool<T>.Pool p : this.poolHash.values()) {
            p.flushUnused(threadContext);
         }

      }
   }

   public void closeAllConnectors(IThreadContext threadContext) throws ManifoldCFException {
      synchronized(this.poolHash) {
         Iterator<ConnectorPool<T>.Pool> iter = this.poolHash.values().iterator();

         while(iter.hasNext()) {
            ConnectorPool<T>.Pool p = (Pool)iter.next();
            p.releaseAll(threadContext);
            iter.remove();
         }

      }
   }

   protected String buildServiceTypeName(String connectionName) {
      return this.serviceTypePrefix + connectionName;
   }

   protected String buildTargetCalcLockName(String connectionName) {
      return "_POOLTARGET_" + this.serviceTypePrefix + connectionName;
   }

   protected static int unpackTarget(byte[] data) {
      return data != null && data.length == 8 ? (data[0] & 255) + (data[1] << 8 & '\uff00') + (data[2] << 16 & 16711680) + (data[3] << 24 & -16777216) : 0;
   }

   protected static int unpackInUse(byte[] data) {
      return data != null && data.length == 8 ? (data[4] & 255) + (data[5] << 8 & '\uff00') + (data[6] << 16 & 16711680) + (data[7] << 24 & -16777216) : 0;
   }

   protected static byte[] pack(int target, int inUse) {
      byte[] rval = new byte[8];
      rval[0] = (byte)(target & 255);
      rval[1] = (byte)(target >> 8 & 255);
      rval[2] = (byte)(target >> 16 & 255);
      rval[3] = (byte)(target >> 24 & 255);
      rval[4] = (byte)(inUse & 255);
      rval[5] = (byte)(inUse >> 8 & 255);
      rval[6] = (byte)(inUse >> 16 & 255);
      rval[7] = (byte)(inUse >> 24 & 255);
      return rval;
   }

   protected class Pool {
      protected boolean isAlive = true;
      protected int globalMax;
      protected final String serviceTypeName;
      protected final String serviceName;
      protected final String targetCalcLockName;
      protected final List<T> stack = new ArrayList();
      protected int numFree = 0;
      protected int localMax = 0;
      protected int localInUse = 0;

      public Pool(IThreadContext threadContext, int maxCount, String connectionName) throws ManifoldCFException {
         this.globalMax = maxCount;
         this.targetCalcLockName = ConnectorPool.this.buildTargetCalcLockName(connectionName);
         this.serviceTypeName = ConnectorPool.this.buildServiceTypeName(connectionName);
         ILockManager lockManager = LockManagerFactory.make(threadContext);
         this.serviceName = lockManager.registerServiceBeginServiceActivity(this.serviceTypeName, (String)null, (IServiceCleanup)null);
      }

      public synchronized void updateMaximumPoolSize(IThreadContext threadContext, int maxPoolSize) throws ManifoldCFException {
         this.globalMax = maxPoolSize;
      }

      public synchronized T getConnector(IThreadContext threadContext, String className, ConfigParams configParams) throws ManifoldCFException {
         while(this.isAlive && this.numFree <= 0) {
            try {
               this.wait();
            } catch (InterruptedException e) {
               throw new ManifoldCFException("Interrupted: " + e.getMessage(), e, 2);
            }
         }

         if (!this.isAlive) {
            return null;
         } else {
            while(true) {
               if (this.stack.size() == 0) {
                  T newrc = (T)ConnectorPool.this.createConnectorInstance(threadContext, className);
                  if (newrc == null) {
                     return null;
                  }

                  newrc.connect(configParams);
                  this.stack.add(newrc);
               }

               T rc = (T)((IConnector)this.stack.remove(this.stack.size() - 1));
               rc.setThreadContext(threadContext);
               if (rc.getClass().getName().equals(className) && rc.getConfiguration().equals(configParams)) {
                  --this.numFree;
                  return rc;
               }

               try {
                  rc.disconnect();
               } finally {
                  rc.clearThreadContext();
               }
            }
         }
      }

      public synchronized void releaseConnector(IThreadContext threadContext, T connector) throws ManifoldCFException {
         if (connector != null) {
            connector.clearThreadContext();
            this.stack.add(connector);
            ++this.numFree;

            while(this.stack.size() > 0 && this.stack.size() > this.numFree) {
               int j;
               for(j = 0; j < this.stack.size() && ((IConnector)this.stack.get(j)).isConnected(); ++j) {
               }

               T rc;
               if (j == this.stack.size()) {
                  rc = (T)((IConnector)this.stack.remove(this.stack.size() - 1));
               } else {
                  rc = (T)((IConnector)this.stack.remove(j));
               }

               rc.setThreadContext(threadContext);

               try {
                  rc.disconnect();
               } finally {
                  rc.clearThreadContext();
               }
            }

            this.notifyAll();
         }
      }

      public synchronized void pollAll(IThreadContext threadContext) throws ManifoldCFException {
         ILockManager lockManager = LockManagerFactory.make(threadContext);
         lockManager.enterWriteLock(this.targetCalcLockName);

         try {
            SumClass sumClass = new SumClass(this.serviceName);
            lockManager.scanServiceData(this.serviceTypeName, sumClass);
            int numServices = sumClass.getNumServices();
            if (numServices == 0) {
               return;
            }

            int globalTarget = sumClass.getGlobalTarget();
            int globalInUse = sumClass.getGlobalInUse();
            int maximumTarget = this.globalMax - globalTarget;
            if (maximumTarget > this.globalMax - globalInUse) {
               maximumTarget = this.globalMax - globalInUse;
            }

            if (maximumTarget < 0) {
               maximumTarget = 0;
            }

            int fairTarget = this.globalMax / numServices;
            int remainder = this.globalMax % numServices;
            if (ConnectorPool.randomNumberGenerator.nextInt(numServices) < remainder) {
               ++fairTarget;
            }

            int localInUse = this.localMax - this.numFree;

            for(T rc : this.stack) {
               rc.setThreadContext(threadContext);

               try {
                  rc.poll();
                  if (rc.isConnected()) {
                     ++localInUse;
                  }
               } finally {
                  rc.clearThreadContext();
               }
            }

            int optimalTarget = this.localMax;
            if (this.localMax > localInUse) {
               --optimalTarget;
            } else {
               int increment = this.globalMax >> 2;
               if (increment == 0) {
                  increment = 1;
               }

               optimalTarget += increment;
            }

            int target = maximumTarget;
            if (maximumTarget > fairTarget) {
               target = fairTarget;
            }

            if (target > optimalTarget) {
               target = optimalTarget;
            }

            lockManager.updateServiceData(this.serviceTypeName, this.serviceName, ConnectorPool.pack(target, localInUse));
            if (target == this.localMax) {
               return;
            }

            localInUse = this.localMax - this.numFree;
            this.localMax = target;
            this.numFree = this.localMax - localInUse;
            this.notifyAll();
         } finally {
            lockManager.leaveWriteLock(this.targetCalcLockName);
         }

         while(this.stack.size() > 0 && this.stack.size() > this.numFree) {
            int j;
            for(j = 0; j < this.stack.size() && ((IConnector)this.stack.get(j)).isConnected(); ++j) {
            }

            T rc;
            if (j == this.stack.size()) {
               rc = (T)((IConnector)this.stack.remove(this.stack.size() - 1));
            } else {
               rc = (T)((IConnector)this.stack.remove(j));
            }

            rc.setThreadContext(threadContext);

            try {
               rc.disconnect();
            } finally {
               rc.clearThreadContext();
            }
         }

      }

      public synchronized void flushUnused(IThreadContext threadContext) throws ManifoldCFException {
         while(this.stack.size() > 0) {
            T rc = (T)((IConnector)this.stack.remove(this.stack.size() - 1));
            rc.setThreadContext(threadContext);

            try {
               rc.disconnect();
            } finally {
               rc.clearThreadContext();
            }
         }

      }

      public synchronized void releaseAll(IThreadContext threadContext) throws ManifoldCFException {
         this.flushUnused(threadContext);
         if (this.isAlive) {
            this.isAlive = false;
            this.notifyAll();
            ILockManager lockManager = LockManagerFactory.make(threadContext);
            lockManager.endServiceActivity(this.serviceTypeName, this.serviceName);
         }

      }
   }

   protected static class SumClass implements IServiceDataAcceptor {
      protected final String serviceName;
      protected int numServices = 0;
      protected int globalTargetTally = 0;
      protected int globalInUseTally = 0;

      public SumClass(String serviceName) {
         this.serviceName = serviceName;
      }

      public boolean acceptServiceData(String serviceName, byte[] serviceData) throws ManifoldCFException {
         ++this.numServices;
         if (!serviceName.equals(this.serviceName)) {
            this.globalTargetTally += ConnectorPool.unpackTarget(serviceData);
            this.globalInUseTally += ConnectorPool.unpackInUse(serviceData);
         }

         return false;
      }

      public int getNumServices() {
         return this.numServices;
      }

      public int getGlobalTarget() {
         return this.globalTargetTally;
      }

      public int getGlobalInUse() {
         return this.globalInUseTally;
      }
   }
}
