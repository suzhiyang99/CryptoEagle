package org.apache.spark.deploy;

import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.zookeeper.KeeperException;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.mutable.StringBuilder;

public final class SparkCuratorUtil$ implements Logging {
   public static final SparkCuratorUtil$ MODULE$;
   private final int ZK_CONNECTION_TIMEOUT_MILLIS;
   private final int ZK_SESSION_TIMEOUT_MILLIS;
   private final int RETRY_WAIT_MILLIS;
   private final int MAX_RECONNECT_ATTEMPTS;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SparkCuratorUtil$();
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   public void org$apache$spark$internal$Logging$$log__$eq(Logger x$1) {
      this.org$apache$spark$internal$Logging$$log_ = x$1;
   }

   public String logName() {
      return Logging$class.logName(this);
   }

   public Logger log() {
      return Logging$class.log(this);
   }

   public void logInfo(Function0<String> msg) {
      Logging$class.logInfo(this, msg);
   }

   public void logDebug(Function0<String> msg) {
      Logging$class.logDebug(this, msg);
   }

   public void logTrace(Function0<String> msg) {
      Logging$class.logTrace(this, msg);
   }

   public void logWarning(Function0<String> msg) {
      Logging$class.logWarning(this, msg);
   }

   public void logError(Function0<String> msg) {
      Logging$class.logError(this, msg);
   }

   public void logInfo(Function0<String> msg, Throwable throwable) {
      Logging$class.logInfo(this, msg, throwable);
   }

   public void logDebug(Function0<String> msg, Throwable throwable) {
      Logging$class.logDebug(this, msg, throwable);
   }

   public void logTrace(Function0<String> msg, Throwable throwable) {
      Logging$class.logTrace(this, msg, throwable);
   }

   public void logWarning(Function0<String> msg, Throwable throwable) {
      Logging$class.logWarning(this, msg, throwable);
   }

   public void logError(Function0<String> msg, Throwable throwable) {
      Logging$class.logError(this, msg, throwable);
   }

   public boolean isTraceEnabled() {
      return Logging$class.isTraceEnabled(this);
   }

   public void initializeLogIfNecessary(boolean isInterpreter) {
      Logging$class.initializeLogIfNecessary(this, isInterpreter);
   }

   public boolean initializeLogIfNecessary(boolean isInterpreter, boolean silent) {
      return Logging$class.initializeLogIfNecessary(this, isInterpreter, silent);
   }

   public boolean initializeLogIfNecessary$default$2() {
      return Logging$class.initializeLogIfNecessary$default$2(this);
   }

   private int ZK_CONNECTION_TIMEOUT_MILLIS() {
      return this.ZK_CONNECTION_TIMEOUT_MILLIS;
   }

   private int ZK_SESSION_TIMEOUT_MILLIS() {
      return this.ZK_SESSION_TIMEOUT_MILLIS;
   }

   private int RETRY_WAIT_MILLIS() {
      return this.RETRY_WAIT_MILLIS;
   }

   private int MAX_RECONNECT_ATTEMPTS() {
      return this.MAX_RECONNECT_ATTEMPTS;
   }

   public CuratorFramework newClient(SparkConf conf, String zkUrlConf) {
      String ZK_URL = conf.get(zkUrlConf);
      CuratorFramework zk = CuratorFrameworkFactory.newClient(ZK_URL, this.ZK_SESSION_TIMEOUT_MILLIS(), this.ZK_CONNECTION_TIMEOUT_MILLIS(), new ExponentialBackoffRetry(this.RETRY_WAIT_MILLIS(), this.MAX_RECONNECT_ATTEMPTS()));
      zk.start();
      return zk;
   }

   public String newClient$default$2() {
      return "spark.deploy.zookeeper.url";
   }

   public void mkdir(CuratorFramework zk, String path) {
      if (zk.checkExists().forPath(path) == null) {
         try {
            zk.create().creatingParentsIfNeeded().forPath(path);
         } catch (KeeperException.NodeExistsException var5) {
         } catch (Exception var6) {
            throw var6;
         }
      }

   }

   public void deleteRecursive(CuratorFramework zk, String path) {
      if (zk.checkExists().forPath(path) != null) {
         ((IterableLike)JavaConverters$.MODULE$.asScalaBufferConverter((List)zk.getChildren().forPath(path)).asScala()).foreach(new Serializable(zk, path) {
            public static final long serialVersionUID = 0L;
            private final CuratorFramework zk$1;
            private final String path$1;

            public final Void apply(String child) {
               return (Void)this.zk$1.delete().forPath((new StringBuilder()).append((Object)this.path$1).append((Object)"/").append((Object)child).toString());
            }

            public {
               this.zk$1 = zk$1;
               this.path$1 = path$1;
            }
         });
         zk.delete().forPath(path);
      }

   }

   private SparkCuratorUtil$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.ZK_CONNECTION_TIMEOUT_MILLIS = 15000;
      this.ZK_SESSION_TIMEOUT_MILLIS = 60000;
      this.RETRY_WAIT_MILLIS = 5000;
      this.MAX_RECONNECT_ATTEMPTS = 3;
   }
}
