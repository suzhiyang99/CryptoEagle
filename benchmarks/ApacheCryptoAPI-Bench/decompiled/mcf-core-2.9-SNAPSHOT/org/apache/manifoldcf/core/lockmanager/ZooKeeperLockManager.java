package org.apache.manifoldcf.core.lockmanager;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.util.List;
import org.apache.manifoldcf.core.interfaces.ILockManager;
import org.apache.manifoldcf.core.interfaces.IServiceCleanup;
import org.apache.manifoldcf.core.interfaces.IServiceDataAcceptor;
import org.apache.manifoldcf.core.interfaces.IShutdownHook;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.ManifoldCFConfiguration;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.interfaces.ThreadContextFactory;
import org.apache.manifoldcf.core.system.ManifoldCF;

public class ZooKeeperLockManager extends BaseLockManager implements ILockManager {
   public static final String _rcsid = "@(#)$Id: LockManager.java 988245 2010-08-23 18:39:35Z kwright $";
   protected static final String zookeeperConnectStringParameter = "org.apache.manifoldcf.zookeeper.connectstring";
   protected static final String zookeeperSessionTimeoutParameter = "org.apache.manifoldcf.zookeeper.sessiontimeout";
   private static final String CONFIGURATION_PATH = "/org.apache.manifoldcf/configuration";
   private static final String RESOURCE_PATH_PREFIX = "/org.apache.manifoldcf/resources-";
   private static final String FLAG_PATH_PREFIX = "/org.apache.manifoldcf/flags-";
   private static final String SERVICETYPE_LOCK_PATH_PREFIX = "/org.apache.manifoldcf/servicelock-";
   private static final String SERVICETYPE_ACTIVE_PATH_PREFIX = "/org.apache.manifoldcf/serviceactive-";
   private static final String SERVICETYPE_REGISTER_PATH_PREFIX = "/org.apache.manifoldcf/service-";
   private static final String SERVICETYPE_ANONYMOUS_COUNTER_PREFIX = "/org.apache.manifoldcf/serviceanon-";
   protected static final String anonymousServiceNamePrefix = "_ANON_";
   protected static Integer connectionPoolLock = new Integer(0);
   protected static ZooKeeperConnectionPool pool = null;
   protected static Integer zookeeperPoolLocker = new Integer(0);
   protected static LockPool myZooKeeperLocks = null;
   protected static Integer ephemeralPoolLocker = new Integer(0);
   protected static ZooKeeperEphemeralNodePool myEphemeralNodes = null;
   protected ManifoldCFConfiguration cachedConfiguration = null;

   public ZooKeeperLockManager() throws ManifoldCFException {
      synchronized(connectionPoolLock) {
         if (pool == null) {
            String connectString = ManifoldCF.getStringProperty("org.apache.manifoldcf.zookeeper.connectstring", (String)null);
            if (connectString == null) {
               throw new ManifoldCFException("Zookeeper lock manager requires a valid org.apache.manifoldcf.zookeeper.connectstring property");
            }

            int sessionTimeout = ManifoldCF.getIntProperty("org.apache.manifoldcf.zookeeper.sessiontimeout", 300000);
            ManifoldCF.addShutdownHook(new ZooKeeperShutdown());
            pool = new ZooKeeperConnectionPool(connectString, sessionTimeout);
         }
      }

      synchronized(zookeeperPoolLocker) {
         if (myZooKeeperLocks == null) {
            myZooKeeperLocks = new LockPool(new ZooKeeperLockObjectFactory(pool));
         }
      }

      synchronized(ephemeralPoolLocker) {
         if (myEphemeralNodes == null) {
            myEphemeralNodes = new ZooKeeperEphemeralNodePool(pool);
         }

      }
   }

   public String registerServiceBeginServiceActivity(String serviceType, String serviceName, IServiceCleanup cleanup) throws ManifoldCFException {
      return this.registerServiceBeginServiceActivity(serviceType, serviceName, (byte[])null, cleanup);
   }

   public String registerServiceBeginServiceActivity(String serviceType, String serviceName, byte[] initialData, IServiceCleanup cleanup) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         String var28;
         try {
            this.enterServiceRegistryWriteLock(connection, serviceType);

            try {
               if (serviceName == null) {
                  serviceName = this.constructUniqueServiceName(connection, serviceType);
               }

               String encodedServiceName = ZooKeeperConnection.zooKeeperSafeName(serviceName);
               String activePath = buildServiceTypeActivePath(serviceType, encodedServiceName);
               if (connection.checkNodeExists(activePath)) {
                  throw new ManifoldCFException("Service '" + serviceName + "' of type '" + serviceType + "' is already active");
               }

               String registrationNodePath = buildServiceTypeRegistrationPath(serviceType);
               List<String> children = connection.getChildren(registrationNodePath);
               boolean foundService = false;
               boolean foundActiveService = false;

               for(String encodedRegisteredServiceName : children) {
                  if (encodedRegisteredServiceName.equals(encodedServiceName)) {
                     foundService = true;
                  }

                  if (connection.checkNodeExists(buildServiceTypeActivePath(serviceType, encodedRegisteredServiceName))) {
                     foundActiveService = true;
                  }
               }

               boolean unregisterAll = false;
               if (cleanup != null) {
                  if (children.size() == 0) {
                     cleanup.cleanUpAllServices();
                     cleanup.clusterInit();
                  } else if (foundService && foundActiveService) {
                     cleanup.cleanUpService(serviceName);
                  } else if (!foundActiveService) {
                     cleanup.cleanUpAllServices();
                     cleanup.clusterInit();
                     unregisterAll = true;
                  }
               }

               if (unregisterAll) {
                  for(String encodedRegisteredServiceName : children) {
                     if (!encodedRegisteredServiceName.equals(encodedServiceName)) {
                        connection.deleteChild(registrationNodePath, encodedRegisteredServiceName);
                     }
                  }
               }

               if (!foundService) {
                  connection.createChild(registrationNodePath, encodedServiceName);
               }

               myEphemeralNodes.createNode(activePath, initialData);
               var28 = serviceName;
            } finally {
               this.leaveServiceRegistryLock(connection);
            }
         } finally {
            pool.release(connection);
         }

         return var28;
      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public void updateServiceData(String serviceType, String serviceName, byte[] serviceData) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         try {
            this.enterServiceRegistryWriteLock(connection, serviceType);

            try {
               String activePath = buildServiceTypeActivePath(serviceType, ZooKeeperConnection.zooKeeperSafeName(serviceName));
               myEphemeralNodes.setNodeData(activePath, serviceData == null ? new byte[0] : serviceData);
            } finally {
               this.leaveServiceRegistryLock(connection);
            }
         } finally {
            pool.release(connection);
         }

      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public byte[] retrieveServiceData(String serviceType, String serviceName) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         byte[] var5;
         try {
            this.enterServiceRegistryReadLock(connection, serviceType);

            try {
               String activePath = buildServiceTypeActivePath(serviceType, ZooKeeperConnection.zooKeeperSafeName(serviceName));
               var5 = connection.getNodeData(activePath);
            } finally {
               this.leaveServiceRegistryLock(connection);
            }
         } finally {
            pool.release(connection);
         }

         return var5;
      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public void scanServiceData(String serviceType, IServiceDataAcceptor dataAcceptor) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         try {
            this.enterServiceRegistryReadLock(connection, serviceType);

            try {
               String registrationNodePath = buildServiceTypeRegistrationPath(serviceType);

               for(String encodedRegisteredServiceName : connection.getChildren(registrationNodePath)) {
                  String activeNodePath = buildServiceTypeActivePath(serviceType, encodedRegisteredServiceName);
                  if (connection.checkNodeExists(activeNodePath)) {
                     byte[] serviceData = connection.getNodeData(activeNodePath);
                     if (dataAcceptor.acceptServiceData(ZooKeeperConnection.zooKeeperDecodeSafeName(encodedRegisteredServiceName), serviceData)) {
                        break;
                     }
                  }
               }
            } finally {
               this.leaveServiceRegistryLock(connection);
            }
         } finally {
            pool.release(connection);
         }

      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public int countActiveServices(String serviceType) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         int var19;
         try {
            this.enterServiceRegistryReadLock(connection, serviceType);

            try {
               String registrationNodePath = buildServiceTypeRegistrationPath(serviceType);
               List<String> children = connection.getChildren(registrationNodePath);
               int activeServiceCount = 0;

               for(String encodedRegisteredServiceName : children) {
                  if (connection.checkNodeExists(buildServiceTypeActivePath(serviceType, encodedRegisteredServiceName))) {
                     ++activeServiceCount;
                  }
               }

               var19 = activeServiceCount;
            } finally {
               this.leaveServiceRegistryLock(connection);
            }
         } finally {
            pool.release(connection);
         }

         return var19;
      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public boolean cleanupInactiveService(String serviceType, IServiceCleanup cleanup) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         boolean var20;
         try {
            this.enterServiceRegistryWriteLock(connection, serviceType);

            try {
               String registrationNodePath = buildServiceTypeRegistrationPath(serviceType);
               List<String> children = connection.getChildren(registrationNodePath);
               String encodedServiceName = null;

               for(String encodedRegisteredServiceName : children) {
                  if (!connection.checkNodeExists(buildServiceTypeActivePath(serviceType, encodedRegisteredServiceName))) {
                     encodedServiceName = encodedRegisteredServiceName;
                     break;
                  }
               }

               if (encodedServiceName != null) {
                  cleanup.cleanUpService(ZooKeeperConnection.zooKeeperDecodeSafeName(encodedServiceName));
                  connection.deleteChild(registrationNodePath, encodedServiceName);
                  var20 = false;
                  return var20;
               }

               var20 = true;
            } finally {
               this.leaveServiceRegistryLock(connection);
            }
         } finally {
            pool.release(connection);
         }

         return var20;
      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public void endServiceActivity(String serviceType, String serviceName) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         try {
            this.enterServiceRegistryWriteLock(connection, serviceType);

            try {
               myEphemeralNodes.deleteNode(buildServiceTypeActivePath(serviceType, ZooKeeperConnection.zooKeeperSafeName(serviceName)));
            } finally {
               this.leaveServiceRegistryLock(connection);
            }
         } finally {
            pool.release(connection);
         }

      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public boolean checkServiceActive(String serviceType, String serviceName) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         boolean var4;
         try {
            this.enterServiceRegistryReadLock(connection, serviceType);

            try {
               var4 = connection.checkNodeExists(buildServiceTypeActivePath(serviceType, ZooKeeperConnection.zooKeeperSafeName(serviceName)));
            } finally {
               this.leaveServiceRegistryLock(connection);
            }
         } finally {
            pool.release(connection);
         }

         return var4;
      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   protected void enterServiceRegistryReadLock(ZooKeeperConnection connection, String serviceType) throws ManifoldCFException, InterruptedException {
      String serviceTypeLock = buildServiceTypeLockPath(serviceType);

      while(!connection.obtainReadLockNoWait(serviceTypeLock)) {
         ManifoldCF.sleep(100L);
      }

   }

   protected void enterServiceRegistryWriteLock(ZooKeeperConnection connection, String serviceType) throws ManifoldCFException, InterruptedException {
      String serviceTypeLock = buildServiceTypeLockPath(serviceType);

      while(!connection.obtainWriteLockNoWait(serviceTypeLock)) {
         ManifoldCF.sleep(100L);
      }

   }

   protected void leaveServiceRegistryLock(ZooKeeperConnection connection) throws ManifoldCFException, InterruptedException {
      connection.releaseLock();
   }

   protected String constructUniqueServiceName(ZooKeeperConnection connection, String serviceType) throws ManifoldCFException, InterruptedException {
      String serviceCounterName = makeServiceCounterName(serviceType);
      int serviceUID = this.readServiceCounter(connection, serviceCounterName);
      this.writeServiceCounter(connection, serviceCounterName, serviceUID + 1);
      return "_ANON_" + serviceUID;
   }

   protected static String makeServiceCounterName(String serviceType) {
      return "/org.apache.manifoldcf/serviceanon-" + ZooKeeperConnection.zooKeeperSafeName(serviceType);
   }

   protected int readServiceCounter(ZooKeeperConnection connection, String serviceCounterName) throws ManifoldCFException, InterruptedException {
      byte[] serviceCounterData = connection.readData(serviceCounterName);
      int rval;
      if (serviceCounterData != null && serviceCounterData.length == 4) {
         rval = (serviceCounterData[0] & 255) + (serviceCounterData[1] << 8 & '\uff00') + (serviceCounterData[2] << 16 & 16711680) + (serviceCounterData[3] << 24 & -16777216);
      } else {
         rval = 0;
      }

      return rval;
   }

   protected void writeServiceCounter(ZooKeeperConnection connection, String serviceCounterName, int counter) throws ManifoldCFException, InterruptedException {
      byte[] serviceCounterData = new byte[4];
      serviceCounterData[0] = (byte)(counter & 255);
      serviceCounterData[1] = (byte)(counter >> 8 & 255);
      serviceCounterData[2] = (byte)(counter >> 16 & 255);
      serviceCounterData[3] = (byte)(counter >> 24 & 255);
      connection.writeData(serviceCounterName, serviceCounterData);
   }

   protected static String buildServiceTypeLockPath(String serviceType) {
      return "/org.apache.manifoldcf/servicelock-" + ZooKeeperConnection.zooKeeperSafeName(serviceType);
   }

   protected static String buildServiceTypeActivePath(String serviceType, String encodedServiceName) {
      return "/org.apache.manifoldcf/serviceactive-" + ZooKeeperConnection.zooKeeperSafeName(serviceType) + "-" + encodedServiceName;
   }

   protected static String buildServiceTypeRegistrationPath(String serviceType) {
      return "/org.apache.manifoldcf/service-" + ZooKeeperConnection.zooKeeperSafeName(serviceType);
   }

   public ManifoldCFConfiguration getSharedConfiguration() throws ManifoldCFException {
      if (this.cachedConfiguration == null) {
         try {
            ZooKeeperConnection connection = pool.grab();

            try {
               byte[] configurationData = connection.readData("/org.apache.manifoldcf/configuration");
               if (configurationData != null) {
                  this.cachedConfiguration = new ManifoldCFConfiguration(new ByteArrayInputStream(configurationData));
               } else {
                  this.cachedConfiguration = new ManifoldCFConfiguration();
               }
            } finally {
               pool.release(connection);
            }
         } catch (InterruptedException e) {
            throw new ManifoldCFException(e.getMessage(), e, 2);
         }
      }

      return this.cachedConfiguration;
   }

   public void setSharedConfiguration(InputStream configurationInputStream) throws ManifoldCFException {
      try {
         ByteArrayOutputStream buffer = new ByteArrayOutputStream();
         byte[] data = new byte[65536];

         while(true) {
            int nRead = configurationInputStream.read(data, 0, data.length);
            if (nRead == -1) {
               buffer.flush();
               byte[] toWrite = buffer.toByteArray();
               ZooKeeperConnection connection = pool.grab();

               try {
                  connection.writeData("/org.apache.manifoldcf/configuration", toWrite);
               } finally {
                  pool.release(connection);
               }

               return;
            }

            buffer.write(data, 0, nRead);
         }
      } catch (InterruptedIOException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      } catch (IOException e) {
         throw new ManifoldCFException(e.getMessage(), e);
      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public void setGlobalFlag(String flagName) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         try {
            connection.setGlobalFlag("/org.apache.manifoldcf/flags-" + ZooKeeperConnection.zooKeeperSafeName(flagName));
         } finally {
            pool.release(connection);
         }

      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public void clearGlobalFlag(String flagName) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         try {
            connection.clearGlobalFlag("/org.apache.manifoldcf/flags-" + ZooKeeperConnection.zooKeeperSafeName(flagName));
         } finally {
            pool.release(connection);
         }

      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public boolean checkGlobalFlag(String flagName) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         boolean var3;
         try {
            var3 = connection.checkGlobalFlag("/org.apache.manifoldcf/flags-" + ZooKeeperConnection.zooKeeperSafeName(flagName));
         } finally {
            pool.release(connection);
         }

         return var3;
      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public byte[] readData(String resourceName) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         byte[] var3;
         try {
            var3 = connection.readData("/org.apache.manifoldcf/resources-" + ZooKeeperConnection.zooKeeperSafeName(resourceName));
         } finally {
            pool.release(connection);
         }

         return var3;
      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public void writeData(String resourceName, byte[] data) throws ManifoldCFException {
      try {
         ZooKeeperConnection connection = pool.grab();

         try {
            connection.writeData("/org.apache.manifoldcf/resources-" + ZooKeeperConnection.zooKeeperSafeName(resourceName), data);
         } finally {
            pool.release(connection);
         }

      } catch (InterruptedException e) {
         throw new ManifoldCFException(e.getMessage(), e, 2);
      }
   }

   public static void main(String[] argv) {
      if (argv.length != 1) {
         System.err.println("Usage: ZooKeeperLockManager <shared_configuration_file>");
         System.exit(1);
      }

      File file = new File(argv[0]);

      try {
         IThreadContext tc = ThreadContextFactory.make();
         ManifoldCF.initializeEnvironment(tc);

         try {
            FileInputStream fis = new FileInputStream(file);

            try {
               (new ZooKeeperLockManager()).setSharedConfiguration(fis);
            } finally {
               fis.close();
            }
         } finally {
            ManifoldCF.cleanUpEnvironment(tc);
         }
      } catch (Throwable e) {
         e.printStackTrace(System.err);
         System.exit(-1);
      }

   }

   protected LockPool getGlobalLockPool() {
      return myZooKeeperLocks;
   }

   protected static void shutdownPool() throws ManifoldCFException {
      synchronized(ephemeralPoolLocker) {
         if (myEphemeralNodes != null) {
            try {
               myEphemeralNodes.deleteAll();
               myEphemeralNodes = null;
            } catch (InterruptedException e) {
               throw new ManifoldCFException(e.getMessage(), e, 2);
            }
         }
      }

      synchronized(connectionPoolLock) {
         if (pool != null) {
            try {
               pool.closeAll();
               pool = null;
            } catch (InterruptedException e) {
               throw new ManifoldCFException(e.getMessage(), e, 2);
            }
         }

      }
   }

   protected static class ZooKeeperShutdown implements IShutdownHook {
      public ZooKeeperShutdown() {
      }

      public void doCleanup(IThreadContext threadContext) throws ManifoldCFException {
         ZooKeeperLockManager.shutdownPool();
      }
   }
}
