package org.apache.manifoldcf.core.lockmanager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class ZooKeeperEphemeralNodePool {
   protected final ZooKeeperConnectionPool pool;
   protected final Map<String, ZooKeeperEphemeralNodeObject> nodes = new HashMap();

   public ZooKeeperEphemeralNodePool(ZooKeeperConnectionPool pool) {
      this.pool = pool;
   }

   public void createNode(String nodePath, byte[] nodeData) throws ManifoldCFException, InterruptedException {
      this.getObject(nodePath).createNode(nodeData);
   }

   public void setNodeData(String nodePath, byte[] nodeData) throws ManifoldCFException, InterruptedException {
      this.getObject(nodePath).setNodeData(nodeData);
   }

   public void deleteNode(String nodePath) throws ManifoldCFException, InterruptedException {
      synchronized(this) {
         ZooKeeperEphemeralNodeObject rval = (ZooKeeperEphemeralNodeObject)this.nodes.get(nodePath);
         if (rval != null) {
            rval.deleteNode();
            this.nodes.remove(nodePath);
         }

      }
   }

   public void deleteAll() throws ManifoldCFException, InterruptedException {
      synchronized(this) {
         while(this.nodes.size() > 0) {
            Iterator<String> nodePathIter = this.nodes.keySet().iterator();
            String nodePath = (String)nodePathIter.next();
            this.deleteNode(nodePath);
         }

      }
   }

   protected synchronized ZooKeeperEphemeralNodeObject getObject(String nodePath) {
      ZooKeeperEphemeralNodeObject rval = (ZooKeeperEphemeralNodeObject)this.nodes.get(nodePath);
      if (rval != null) {
         return rval;
      } else {
         rval = new ZooKeeperEphemeralNodeObject(nodePath, this.pool);
         this.nodes.put(nodePath, rval);
         return rval;
      }
   }
}
