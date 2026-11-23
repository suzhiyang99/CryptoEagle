package org.apache.manifoldcf.core.lockmanager;

import java.util.ArrayList;
import java.util.List;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class ZooKeeperConnectionPool {
   public static final String _rcsid = "@(#)$Id$";
   protected final String connectString;
   protected final int sessionTimeout;
   protected final List<ZooKeeperConnection> openConnectionList = new ArrayList();

   public ZooKeeperConnectionPool(String connectString, int sessionTimeout) {
      this.connectString = connectString;
      this.sessionTimeout = sessionTimeout;
   }

   public synchronized ZooKeeperConnection grab() throws ManifoldCFException, InterruptedException {
      if (this.openConnectionList.size() == 0) {
         this.openConnectionList.add(new ZooKeeperConnection(this.connectString, this.sessionTimeout));
      }

      return (ZooKeeperConnection)this.openConnectionList.remove(this.openConnectionList.size() - 1);
   }

   public synchronized void release(ZooKeeperConnection connection) {
      this.openConnectionList.add(connection);
   }

   public synchronized void closeAll() throws InterruptedException {
      for(ZooKeeperConnection c : this.openConnectionList) {
         c.close();
      }

   }
}
