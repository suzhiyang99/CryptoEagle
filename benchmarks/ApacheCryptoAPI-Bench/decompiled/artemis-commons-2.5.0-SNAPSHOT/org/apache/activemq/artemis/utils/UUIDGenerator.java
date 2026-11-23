package org.apache.activemq.artemis.utils;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.activemq.artemis.api.core.SimpleString;
import org.apache.activemq.artemis.logs.ActiveMQUtilLogger;

public final class UUIDGenerator {
   private static final UUIDGenerator sSingleton = new UUIDGenerator();
   private static final byte[][] BLACK_LIST = new byte[][]{{2, 0, 84, 85, 78, 1}};
   private Random mRnd = null;
   private final Object mTimerLock = new Object();
   private UUIDTimer mTimer = null;
   private byte[] address;

   private UUIDGenerator() {
   }

   public static UUIDGenerator getInstance() {
      return sSingleton;
   }

   public Random getRandomNumberGenerator() {
      if (this.mRnd == null) {
         this.mRnd = new SecureRandom();
      }

      return this.mRnd;
   }

   public UUID generateTimeBasedUUID(byte[] byteAddr) {
      byte[] contents = new byte[16];
      int pos = 10;
      System.arraycopy(byteAddr, 0, contents, pos, 6);
      synchronized(this.mTimerLock) {
         if (this.mTimer == null) {
            this.mTimer = new UUIDTimer(this.getRandomNumberGenerator());
         }

         this.mTimer.getTimestamp(contents);
      }

      return new UUID(1, contents);
   }

   public byte[] generateDummyAddress() {
      Random rnd = this.getRandomNumberGenerator();
      byte[] dummy = new byte[6];
      rnd.nextBytes(dummy);
      dummy[0] = (byte)(dummy[0] | 1);
      if (ActiveMQUtilLogger.LOGGER.isDebugEnabled()) {
         ActiveMQUtilLogger.LOGGER.debug("using dummy address " + asString(dummy));
      }

      return dummy;
   }

   public static byte[] getHardwareAddress() {
      try {
         ExecutorService executor = Executors.newFixedThreadPool(1, ActiveMQThreadFactory.defaultThreadFactory());
         executor.shutdownNow();
      } catch (Throwable var3) {
         return null;
      }

      try {
         List<NetworkInterface> ifaces = getAllNetworkInterfaces();
         if (ifaces.size() == 0) {
            return null;
         } else {
            byte[] address = findFirstMatchingHardwareAddress(ifaces);
            if (address != null) {
               if (ActiveMQUtilLogger.LOGGER.isDebugEnabled()) {
                  ActiveMQUtilLogger.LOGGER.debug("using hardware address " + asString(address));
               }

               return address;
            } else {
               return null;
            }
         }
      } catch (Exception var2) {
         return null;
      }
   }

   public SimpleString generateSimpleStringUUID() {
      return new SimpleString(this.generateStringUUID());
   }

   public UUID generateUUID() {
      byte[] address = this.getAddressBytes();
      UUID uid = this.generateTimeBasedUUID(address);
      return uid;
   }

   public String generateStringUUID() {
      byte[] address = this.getAddressBytes();
      return address == null ? java.util.UUID.randomUUID().toString() : this.generateTimeBasedUUID(address).toString();
   }

   public static byte[] getZeroPaddedSixBytes(byte[] bytes) {
      if (bytes == null) {
         return null;
      } else if (bytes.length > 0 && bytes.length <= 6) {
         if (bytes.length == 6) {
            return bytes;
         } else {
            byte[] paddedAddress = new byte[6];
            System.arraycopy(bytes, 0, paddedAddress, 0, bytes.length);

            for(int i = bytes.length; i < 6; ++i) {
               paddedAddress[i] = 0;
            }

            return paddedAddress;
         }
      } else {
         return null;
      }
   }

   private static boolean isBlackList(byte[] address) {
      for(byte[] blackList : BLACK_LIST) {
         if (Arrays.equals(address, blackList)) {
            return true;
         }
      }

      return false;
   }

   private byte[] getAddressBytes() {
      if (this.address == null) {
         this.address = getHardwareAddress();
         if (this.address == null) {
            this.address = this.generateDummyAddress();
         }
      }

      return this.address;
   }

   private static String asString(byte[] bytes) {
      if (bytes == null) {
         return null;
      } else {
         StringBuilder s = new StringBuilder();

         for(int i = 0; i < bytes.length - 1; ++i) {
            s.append(Integer.toHexString(bytes[i]));
            s.append(":");
         }

         s.append(bytes[bytes.length - 1]);
         return s.toString();
      }
   }

   private static List<NetworkInterface> getAllNetworkInterfaces() {
      try {
         Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
         if (networkInterfaces == null) {
            return Collections.emptyList();
         } else {
            List<NetworkInterface> ifaces = new ArrayList();

            while(networkInterfaces.hasMoreElements()) {
               ifaces.add((NetworkInterface)networkInterfaces.nextElement());
            }

            return ifaces;
         }
      } catch (SocketException var2) {
         return Collections.emptyList();
      }
   }

   private static byte[] findFirstMatchingHardwareAddress(List<NetworkInterface> ifaces) {
      ExecutorService executor = Executors.newFixedThreadPool(ifaces.size(), ActiveMQThreadFactory.defaultThreadFactory());
      Collection<Callable<byte[]>> tasks = new ArrayList(ifaces.size());

      for(final NetworkInterface networkInterface : ifaces) {
         tasks.add(new Callable<byte[]>() {
            public byte[] call() throws Exception {
               boolean up = networkInterface.isUp();
               boolean loopback = networkInterface.isLoopback();
               boolean virtual = networkInterface.isVirtual();
               if (!loopback && !virtual && up) {
                  byte[] address = networkInterface.getHardwareAddress();
                  if (address != null) {
                     byte[] paddedAddress = UUIDGenerator.getZeroPaddedSixBytes(address);
                     if (UUIDGenerator.isBlackList(address)) {
                        throw new Exception("black listed address");
                     }

                     if (paddedAddress != null) {
                        return paddedAddress;
                     }
                  }

                  throw new Exception("invalid network interface");
               } else {
                  throw new Exception("not suitable interface");
               }
            }
         });
      }

      B var11;
      try {
         byte[] address = (byte[])executor.invokeAny(tasks, 5L, TimeUnit.SECONDS);
         var11 = address;
         return (byte[])var11;
      } catch (Exception var8) {
         var11 = null;
      } finally {
         executor.shutdownNow();
      }

      return (byte[])var11;
   }
}
