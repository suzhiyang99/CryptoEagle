package org.spark_project.jetty.io;

import java.io.Closeable;
import java.nio.ByteBuffer;

public interface Connection extends Closeable {
   void addListener(Listener var1);

   void removeListener(Listener var1);

   void onOpen();

   void onClose();

   EndPoint getEndPoint();

   void close();

   boolean onIdleExpired();

   int getMessagesIn();

   int getMessagesOut();

   long getBytesIn();

   long getBytesOut();

   long getCreatedTimeStamp();

   public interface Listener {
      void onOpened(Connection var1);

      void onClosed(Connection var1);

      public static class Adapter implements Listener {
         public void onOpened(Connection connection) {
         }

         public void onClosed(Connection connection) {
         }
      }
   }

   public interface UpgradeFrom extends Connection {
      ByteBuffer onUpgradeFrom();
   }

   public interface UpgradeTo extends Connection {
      void onUpgradeTo(ByteBuffer var1);
   }
}
