package org.apache.manifoldcf.core.lockmanager;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.core.system.ManifoldCF;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class ZooKeeperConnection {
   public static final String _rcsid = "@(#)$Id$";
   private static final String READ_PREFIX = "read-";
   private static final String NONEXWRITE_PREFIX = "nonexwrite-";
   private static final String WRITE_PREFIX = "write-";
   private static final String CHILD_PREFIX = "child-";
   protected final String connectString;
   protected final int sessionTimeout;
   protected ZooKeeper zookeeper = null;
   protected ZooKeeperWatcher zookeeperWatcher = null;
   protected String lockNode = null;
   protected String nodePath = null;
   protected byte[] nodeData = null;

   public ZooKeeperConnection(String connectString, int sessionTimeout) throws ManifoldCFException, InterruptedException {
      this.connectString = connectString;
      this.sessionTimeout = sessionTimeout;
      this.zookeeperWatcher = new ZooKeeperWatcher();
      this.createSession();
   }

   protected void createSession() throws ManifoldCFException, InterruptedException {
      try {
         this.zookeeper = new ZooKeeper(this.connectString, this.sessionTimeout, this.zookeeperWatcher);
      } catch (InterruptedIOException e) {
         throw new InterruptedException(e.getMessage());
      } catch (IOException e) {
         throw new ManifoldCFException("Zookeeper initialization error: " + e.getMessage(), e);
      }
   }

   public void createNode(String nodePath, byte[] nodeData) throws ManifoldCFException, InterruptedException {
      if (this.nodePath != null) {
         throw new IllegalStateException("Ephemeral node '" + this.nodePath + "' already open; can't open '" + nodePath + "'.");
      } else {
         while(true) {
            try {
               if (this.nodePath == null) {
                  this.zookeeper.create(nodePath, nodeData, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
                  this.nodePath = nodePath;
                  this.nodeData = nodeData;
               }

               return;
            } catch (KeeperException e) {
               this.handleEphemeralNodeKeeperException(e, false);
            }
         }
      }
   }

   public boolean checkNodeExists(String nodePath) throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            return this.zookeeper.exists(nodePath, false) != null;
         } catch (KeeperException e) {
            this.handleKeeperException(e, true);
         }
      }
   }

   public byte[] getNodeData(String nodePath) throws ManifoldCFException, InterruptedException {
      return this.readData(nodePath);
   }

   public void setNodeData(byte[] data) throws ManifoldCFException, InterruptedException {
      if (this.nodePath == null) {
         throw new IllegalStateException("Can't set data for a node path we did not create: '" + this.nodePath + "'");
      } else {
         this.writeData(this.nodePath, data);
         this.nodeData = data;
      }
   }

   public void deleteNode() throws ManifoldCFException, InterruptedException {
      if (this.nodePath == null) {
         throw new IllegalStateException("Can't delete ephemeral node that isn't registered: '" + this.nodePath + "'");
      } else {
         while(true) {
            try {
               if (this.nodePath != null) {
                  this.zookeeper.delete(this.nodePath, -1);
                  this.nodePath = null;
                  this.nodeData = null;
               }

               return;
            } catch (KeeperException e) {
               this.handleEphemeralNodeKeeperException(e, false);
            }
         }
      }
   }

   public void deleteNodeChildren(String nodePath) throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            for(String child : this.zookeeper.getChildren(nodePath, false)) {
               this.zookeeper.delete(nodePath + "/" + child, -1);
            }
         } catch (KeeperException.NoNodeException var5) {
         } catch (KeeperException e) {
            this.handleKeeperException(e, true);
            continue;
         }

         return;
      }
   }

   public List<String> getChildren(String nodePath) throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            List<String> children = this.zookeeper.getChildren(nodePath, false);
            List<String> rval = new ArrayList();

            for(String child : children) {
               if (child.startsWith("child-")) {
                  rval.add(child.substring("child-".length()));
               }
            }

            return rval;
         } catch (KeeperException.NoNodeException var6) {
            return new ArrayList();
         } catch (KeeperException e) {
            this.handleKeeperException(e, true);
         }
      }
   }

   public void createChild(String nodePath, String childName) throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            this.createPersistentPath(nodePath + "/" + "child-" + childName, (byte[])null);
            return;
         } catch (KeeperException e) {
            this.handleKeeperException(e, true);
         }
      }
   }

   protected void createPersistentPath(String path, byte[] data) throws KeeperException, InterruptedException {
      while(true) {
         try {
            this.zookeeper.create(path, data, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
         } catch (KeeperException.NoNodeException e) {
            int lastIndex = path.lastIndexOf("/");
            if (lastIndex != -1 && lastIndex != 0) {
               this.createPersistentPath(path.substring(0, lastIndex), (byte[])null);
               continue;
            }

            throw e;
         } catch (KeeperException.NodeExistsException var7) {
            if (data != null) {
               try {
                  this.zookeeper.setData(path, data, -1);
               } catch (KeeperException.NoNodeException var5) {
                  continue;
               }
            }
         }

         return;
      }
   }

   public void deleteChild(String nodePath, String childName) throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            this.zookeeper.delete(nodePath + "/" + "child-" + childName, -1);
            return;
         } catch (KeeperException e) {
            this.handleKeeperException(e, true);
         }
      }
   }

   public boolean obtainWriteLockNoWait(String lockPath) throws ManifoldCFException, InterruptedException {
      if (this.lockNode != null) {
         throw new IllegalStateException("Already have a lock in place: '" + this.lockNode + "'; can't also write lock '" + lockPath + "'");
      } else {
         while(true) {
            try {
               if (this.lockNode == null) {
                  this.lockNode = this.createSequentialChild(lockPath, "write-");
               }

               String lockSequenceNumber = this.lockNode.substring(lockPath.length() + 1 + "write-".length());

               for(String x : this.zookeeper.getChildren(lockPath, false)) {
                  String otherLock;
                  if (x.startsWith("write-")) {
                     otherLock = x.substring("write-".length());
                  } else if (x.startsWith("nonexwrite-")) {
                     otherLock = x.substring("nonexwrite-".length());
                  } else {
                     if (!x.startsWith("read-")) {
                        continue;
                     }

                     otherLock = x.substring("read-".length());
                  }

                  if (otherLock.compareTo(lockSequenceNumber) < 0) {
                     this.releaseLock();
                     return false;
                  }
               }

               return true;
            } catch (InterruptedException e) {
               this.lockNode = null;
               throw e;
            } catch (KeeperException e) {
               this.handleEphemeralNodeKeeperException(e, true);
            }
         }
      }
   }

   public void obtainWriteLock(String lockPath) throws ManifoldCFException, InterruptedException {
      if (this.lockNode != null) {
         throw new IllegalStateException("Already have a lock in place: '" + this.lockNode + "'; can't also write lock '" + lockPath + "'");
      } else {
         while(true) {
            try {
               if (this.lockNode == null) {
                  this.lockNode = this.createSequentialChild(lockPath, "write-");
               }

               long lockSequenceNumber = new Long(this.lockNode.substring(lockPath.length() + 1 + "write-".length()));

               while(true) {
                  List<String> children = this.zookeeper.getChildren(lockPath, false);
                  String previousLock = null;
                  boolean gotLock = true;
                  long highestPreviousLockIndex = -1L;

                  for(String x : children) {
                     String otherLock;
                     if (x.startsWith("write-")) {
                        otherLock = x.substring("write-".length());
                     } else if (x.startsWith("nonexwrite-")) {
                        otherLock = x.substring("nonexwrite-".length());
                     } else {
                        if (!x.startsWith("read-")) {
                           continue;
                        }

                        otherLock = x.substring("read-".length());
                     }

                     long otherLockSequenceNumber = new Long(otherLock);
                     if (otherLockSequenceNumber < lockSequenceNumber) {
                        gotLock = false;
                        if (otherLockSequenceNumber > highestPreviousLockIndex) {
                           previousLock = x;
                           highestPreviousLockIndex = otherLockSequenceNumber;
                        }
                     }
                  }

                  if (gotLock) {
                     return;
                  }

                  if (previousLock != null) {
                     ExistsWatcher w = new ExistsWatcher();
                     Stat s = this.zookeeper.exists(lockPath + "/" + previousLock, w);
                     if (s != null) {
                        w.waitForEvent();
                     }
                  }
               }
            } catch (InterruptedException e) {
               this.lockNode = null;
               throw e;
            } catch (KeeperException e) {
               this.handleEphemeralNodeKeeperException(e, true);
            }
         }
      }
   }

   public boolean obtainNonExWriteLockNoWait(String lockPath) throws ManifoldCFException, InterruptedException {
      if (this.lockNode != null) {
         throw new IllegalStateException("Already have a lock in place: '" + this.lockNode + "'; can't also non-ex write lock '" + lockPath + "'");
      } else {
         while(true) {
            try {
               if (this.lockNode == null) {
                  this.lockNode = this.createSequentialChild(lockPath, "nonexwrite-");
               }

               String lockSequenceNumber = this.lockNode.substring(lockPath.length() + 1 + "nonexwrite-".length());
               List<String> children = null;

               while(true) {
                  try {
                     children = this.zookeeper.getChildren(lockPath, false);
                     break;
                  } catch (KeeperException.NoNodeException var7) {
                     break;
                  } catch (KeeperException e) {
                     this.handleKeeperException(e, true);
                  }
               }

               if (children != null) {
                  for(String x : children) {
                     String otherLock;
                     if (x.startsWith("write-")) {
                        otherLock = x.substring("write-".length());
                     } else {
                        if (!x.startsWith("read-")) {
                           continue;
                        }

                        otherLock = x.substring("read-".length());
                     }

                     if (otherLock.compareTo(lockSequenceNumber) < 0) {
                        this.releaseLock();
                        return false;
                     }
                  }

                  return true;
               }
            } catch (InterruptedException e) {
               this.lockNode = null;
               throw e;
            } catch (KeeperException e) {
               this.handleEphemeralNodeKeeperException(e, true);
            }
         }
      }
   }

   public void obtainNonExWriteLock(String lockPath) throws ManifoldCFException, InterruptedException {
      if (this.lockNode != null) {
         throw new IllegalStateException("Already have a lock in place: '" + this.lockNode + "'; can't also non-ex write lock '" + lockPath + "'");
      } else {
         while(true) {
            try {
               if (this.lockNode == null) {
                  this.lockNode = this.createSequentialChild(lockPath, "nonexwrite-");
               }

               long lockSequenceNumber = new Long(this.lockNode.substring(lockPath.length() + 1 + "nonexwrite-".length()));

               while(true) {
                  List<String> children = null;

                  while(true) {
                     try {
                        children = this.zookeeper.getChildren(lockPath, false);
                        break;
                     } catch (KeeperException.NoNodeException var14) {
                        break;
                     } catch (KeeperException e) {
                        this.handleKeeperException(e, true);
                     }
                  }

                  if (children == null) {
                     break;
                  }

                  String previousLock = null;
                  boolean gotLock = true;
                  long highestPreviousLockIndex = -1L;

                  for(String x : children) {
                     String otherLock;
                     if (x.startsWith("write-")) {
                        otherLock = x.substring("write-".length());
                     } else {
                        if (!x.startsWith("read-")) {
                           continue;
                        }

                        otherLock = x.substring("read-".length());
                     }

                     long otherLockSequenceNumber = new Long(otherLock);
                     if (otherLockSequenceNumber < lockSequenceNumber) {
                        gotLock = false;
                        if (otherLockSequenceNumber > highestPreviousLockIndex) {
                           previousLock = x;
                           highestPreviousLockIndex = otherLockSequenceNumber;
                        }
                     }
                  }

                  if (gotLock) {
                     return;
                  }

                  if (previousLock != null) {
                     ExistsWatcher w = new ExistsWatcher();
                     Stat s = this.zookeeper.exists(lockPath + "/" + previousLock, w);
                     if (s != null) {
                        w.waitForEvent();
                     }
                  }
               }
            } catch (InterruptedException e) {
               this.lockNode = null;
               throw e;
            } catch (KeeperException e) {
               this.handleEphemeralNodeKeeperException(e, true);
            }
         }
      }
   }

   public boolean obtainReadLockNoWait(String lockPath) throws ManifoldCFException, InterruptedException {
      if (this.lockNode != null) {
         throw new IllegalStateException("Already have a lock in place: '" + this.lockNode + "'; can't also read lock '" + lockPath + "'");
      } else {
         while(true) {
            try {
               if (this.lockNode == null) {
                  this.lockNode = this.createSequentialChild(lockPath, "read-");
               }

               String lockSequenceNumber = this.lockNode.substring(lockPath.length() + 1 + "read-".length());
               List<String> children = null;

               while(true) {
                  try {
                     children = this.zookeeper.getChildren(lockPath, false);
                     break;
                  } catch (KeeperException.NoNodeException var7) {
                     break;
                  } catch (KeeperException e) {
                     this.handleKeeperException(e, true);
                  }
               }

               if (children != null) {
                  for(String x : children) {
                     String otherLock;
                     if (x.startsWith("write-")) {
                        otherLock = x.substring("write-".length());
                     } else {
                        if (!x.startsWith("nonexwrite-")) {
                           continue;
                        }

                        otherLock = x.substring("nonexwrite-".length());
                     }

                     if (otherLock.compareTo(lockSequenceNumber) < 0) {
                        this.releaseLock();
                        return false;
                     }
                  }

                  return true;
               }
            } catch (InterruptedException e) {
               this.lockNode = null;
               throw e;
            } catch (KeeperException e) {
               this.handleEphemeralNodeKeeperException(e, true);
            }
         }
      }
   }

   public void obtainReadLock(String lockPath) throws ManifoldCFException, InterruptedException {
      if (this.lockNode != null) {
         throw new IllegalStateException("Already have a lock in place: '" + this.lockNode + "'; can't also read lock '" + lockPath + "'");
      } else {
         while(true) {
            try {
               if (this.lockNode == null) {
                  this.lockNode = this.createSequentialChild(lockPath, "read-");
               }

               long lockSequenceNumber = new Long(this.lockNode.substring(lockPath.length() + 1 + "read-".length()));

               while(true) {
                  List<String> children = null;

                  while(true) {
                     try {
                        children = this.zookeeper.getChildren(lockPath, false);
                        break;
                     } catch (KeeperException.NoNodeException var14) {
                        break;
                     } catch (KeeperException e) {
                        this.handleKeeperException(e, true);
                     }
                  }

                  if (children == null) {
                     break;
                  }

                  String previousLock = null;
                  boolean gotLock = true;
                  long highestPreviousLockIndex = -1L;

                  for(String x : children) {
                     String otherLock;
                     if (x.startsWith("write-")) {
                        otherLock = x.substring("write-".length());
                     } else {
                        if (!x.startsWith("nonexwrite-")) {
                           continue;
                        }

                        otherLock = x.substring("nonexwrite-".length());
                     }

                     long otherLockSequenceNumber = new Long(otherLock);
                     if (otherLockSequenceNumber < lockSequenceNumber) {
                        gotLock = false;
                        if (otherLockSequenceNumber > highestPreviousLockIndex) {
                           previousLock = x;
                           highestPreviousLockIndex = otherLockSequenceNumber;
                        }
                     }
                  }

                  if (gotLock) {
                     return;
                  }

                  if (previousLock != null) {
                     ExistsWatcher w = new ExistsWatcher();
                     Stat s = this.zookeeper.exists(lockPath + "/" + previousLock, w);
                     if (s != null) {
                        w.waitForEvent();
                     }
                  }
               }
            } catch (InterruptedException e) {
               this.lockNode = null;
               throw e;
            } catch (KeeperException e) {
               this.handleEphemeralNodeKeeperException(e, true);
            }
         }
      }
   }

   public void releaseLock() throws ManifoldCFException, InterruptedException {
      if (this.lockNode == null) {
         throw new IllegalStateException("Can't release lock we don't hold");
      } else {
         while(this.lockNode != null) {
            try {
               this.zookeeper.delete(this.lockNode, -1);
               this.lockNode = null;
               break;
            } catch (InterruptedException e) {
               this.lockNode = null;
               throw e;
            } catch (KeeperException e) {
               this.handleEphemeralNodeKeeperException(e, true);
            }
         }

      }
   }

   public byte[] readData(String resourcePath) throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            return this.zookeeper.getData(resourcePath, false, (Stat)null);
         } catch (KeeperException.NoNodeException var3) {
            return null;
         } catch (KeeperException e) {
            this.handleKeeperException(e, true);
         }
      }
   }

   public void writeData(String resourcePath, byte[] data) throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            if (data == null) {
               try {
                  this.zookeeper.delete(resourcePath, -1);
               } catch (KeeperException.NoNodeException var5) {
               }
            } else {
               try {
                  this.zookeeper.setData(resourcePath, data, -1);
               } catch (KeeperException.NoNodeException var4) {
                  this.createPersistentPath(resourcePath, data);
               }
            }

            return;
         } catch (KeeperException e) {
            this.handleKeeperException(e, true);
         }
      }
   }

   public void setGlobalFlag(String flagPath) throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            this.createPersistentPath(flagPath, (byte[])null);
            return;
         } catch (KeeperException e) {
            this.handleKeeperException(e, true);
         }
      }
   }

   public void clearGlobalFlag(String flagPath) throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            try {
               this.zookeeper.delete(flagPath, -1);
            } catch (KeeperException.NoNodeException var3) {
            }

            return;
         } catch (KeeperException e) {
            this.handleKeeperException(e, true);
         }
      }
   }

   public boolean checkGlobalFlag(String flagPath) throws ManifoldCFException, InterruptedException {
      while(true) {
         try {
            Stat s = this.zookeeper.exists(flagPath, false);
            return s != null;
         } catch (KeeperException e) {
            this.handleKeeperException(e, true);
         }
      }
   }

   public void close() throws InterruptedException {
      if (this.lockNode != null) {
         throw new IllegalStateException("Should not be closing handles that have open locks!  Locknode: '" + this.lockNode + "'");
      } else {
         this.zookeeper.close();
         this.zookeeper = null;
         this.zookeeperWatcher = null;
      }
   }

   protected void handleEphemeralNodeKeeperException(KeeperException e, boolean recreate) throws ManifoldCFException, InterruptedException {
      if (!(e instanceof KeeperException.ConnectionLossException) && !(e instanceof KeeperException.SessionExpiredException)) {
         throw new ManifoldCFException(e.getMessage(), e);
      } else {
         while(true) {
            try {
               this.lockNode = null;
               if (!recreate) {
                  this.nodePath = null;
                  this.nodeData = null;
               }

               this.zookeeper.close();
               this.createSession();
               if (this.nodePath != null) {
                  this.zookeeper.create(this.nodePath, this.nodeData, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
               }

               return;
            } catch (KeeperException e2) {
               if (!(e2 instanceof KeeperException.ConnectionLossException) && !(e2 instanceof KeeperException.SessionExpiredException)) {
                  throw new ManifoldCFException(e2.getMessage(), e2);
               }
            }
         }
      }
   }

   protected void handleKeeperException(KeeperException e, boolean recreate) throws ManifoldCFException, InterruptedException {
      if (e instanceof KeeperException.ConnectionLossException) {
         ManifoldCF.sleep(100L);
      } else {
         if (!(e instanceof KeeperException.SessionExpiredException)) {
            throw new ManifoldCFException(e.getMessage(), e);
         }

         while(true) {
            try {
               this.lockNode = null;
               if (!recreate) {
                  this.nodePath = null;
                  this.nodeData = null;
               }

               this.zookeeper.close();
               this.createSession();
               if (this.nodePath != null) {
                  this.zookeeper.create(this.nodePath, this.nodeData, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
               }
               break;
            } catch (KeeperException e2) {
               if (!(e2 instanceof KeeperException.ConnectionLossException) && !(e2 instanceof KeeperException.SessionExpiredException)) {
                  throw new ManifoldCFException(e2.getMessage(), e2);
               }
            }
         }
      }

   }

   public static String zooKeeperSafeName(String input) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < input.length(); ++i) {
         char x = input.charAt(i);
         if (x == '/') {
            sb.append('\\').append('0');
         } else if (x == 127) {
            sb.append('\\').append('1');
         } else if (x == '\\') {
            sb.append('\\').append('\\');
         } else if (x >= 0 && x < ' ') {
            sb.append('\\').append(x + 64);
         } else if (x >= 128 && x < 160) {
            sb.append('\\').append(x + 96 - 128);
         } else {
            sb.append(x);
         }
      }

      return sb.toString();
   }

   public static String zooKeeperDecodeSafeName(String input) {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < input.length(); ++i) {
         char x = input.charAt(i);
         if (x == '\\') {
            ++i;
            if (i == input.length()) {
               throw new RuntimeException("Supposedly safe zookeeper name is not properly encoded!!");
            }

            x = input.charAt(i);
            if (x == '0') {
               sb.append('/');
            } else if (x == '1') {
               sb.append('\u007f');
            } else if (x == '\\') {
               sb.append('\\');
            } else if (x >= '@' && x < '`') {
               sb.append(x - 64);
            } else {
               if (x < '`' || x >= 128) {
                  throw new RuntimeException("Supposedly safe zookeeper name is not properly encoded!!");
               }

               sb.append(x - 96 + 128);
            }
         } else {
            sb.append(x);
         }
      }

      return sb.toString();
   }

   protected String createSequentialChild(String mainNode, String childPrefix) throws KeeperException, InterruptedException {
      while(true) {
         try {
            return this.zookeeper.create(mainNode + "/" + childPrefix, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
         } catch (KeeperException.NoNodeException var6) {
            try {
               this.zookeeper.create(mainNode, new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            } catch (KeeperException.NodeExistsException var5) {
            }
         }
      }
   }

   protected static class ZooKeeperWatcher implements Watcher {
      public ZooKeeperWatcher() {
      }

      public void process(WatchedEvent event) {
      }
   }

   protected static class ExistsWatcher implements Watcher {
      protected boolean eventTriggered = false;

      public ExistsWatcher() {
      }

      public void process(WatchedEvent event) {
         synchronized(this) {
            this.eventTriggered = true;
            this.notifyAll();
         }
      }

      public void waitForEvent() throws InterruptedException {
         synchronized(this) {
            if (!this.eventTriggered) {
               this.wait();
            }
         }
      }
   }
}
