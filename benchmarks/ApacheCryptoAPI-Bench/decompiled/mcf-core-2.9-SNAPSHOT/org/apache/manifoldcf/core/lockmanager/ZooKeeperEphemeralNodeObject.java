package org.apache.manifoldcf.core.lockmanager;

import org.apache.manifoldcf.core.interfaces.ManifoldCFException;

public class ZooKeeperEphemeralNodeObject {
   private final ZooKeeperConnectionPool pool;
   private final String nodePath;
   private ZooKeeperConnection currentConnection = null;

   public ZooKeeperEphemeralNodeObject(String nodePath, ZooKeeperConnectionPool pool) {
      this.nodePath = nodePath;
      this.pool = pool;
   }

   public synchronized void createNode(byte[] nodeData) throws ManifoldCFException, InterruptedException {
      if (this.currentConnection != null) {
         throw new IllegalStateException("Already have a created node for '" + this.nodePath + "'");
      } else {
         this.currentConnection = this.pool.grab();

         try {
            this.currentConnection.createNode(this.nodePath, nodeData);
         } catch (Throwable t) {
            this.pool.release(this.currentConnection);
            this.currentConnection = null;
            if (t instanceof ManifoldCFException) {
               throw (ManifoldCFException)t;
            } else if (t instanceof InterruptedException) {
               throw (InterruptedException)t;
            } else if (t instanceof Error) {
               throw (Error)t;
            } else if (t instanceof RuntimeException) {
               throw (RuntimeException)t;
            } else {
               throw new RuntimeException("Unexpected exception type: " + t.getClass().getName() + ": " + t.getMessage(), t);
            }
         }
      }
   }

   public synchronized void setNodeData(byte[] nodeData) throws ManifoldCFException, InterruptedException {
      if (this.currentConnection == null) {
         throw new IllegalStateException("Node not yet created for node path '" + this.nodePath + "'");
      } else {
         this.currentConnection.setNodeData(nodeData);
      }
   }

   public synchronized void deleteNode() throws ManifoldCFException, InterruptedException {
      if (this.currentConnection == null) {
         throw new IllegalStateException("Can't delete node '" + this.nodePath + "' that we don't own");
      } else {
         this.currentConnection.deleteNode();
         this.pool.release(this.currentConnection);
         this.currentConnection = null;
      }
   }
}
