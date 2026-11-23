package org.apache.spark.deploy.master;

import java.nio.ByteBuffer;
import java.util.List;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.PathAndBytesable;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.SparkCuratorUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.serializer.Serializer;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Serializable;
import scala.Some;
import scala.collection.Iterable;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.TraversableLike;
import scala.collection.mutable.Buffer$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005%c!B\u0001\u0003\u0001\ta!A\u0007.p_.+W\r]3s!\u0016\u00148/[:uK:\u001cW-\u00128hS:,'BA\u0002\u0005\u0003\u0019i\u0017m\u001d;fe*\u0011QAB\u0001\u0007I\u0016\u0004Hn\\=\u000b\u0005\u001dA\u0011!B:qCJ\\'BA\u0005\u000b\u0003\u0019\t\u0007/Y2iK*\t1\"A\u0002pe\u001e\u001c2\u0001A\u0007\u0012!\tqq\"D\u0001\u0003\u0013\t\u0001\"AA\tQKJ\u001c\u0018n\u001d;f]\u000e,WI\\4j]\u0016\u0004\"AE\u000b\u000e\u0003MQ!\u0001\u0006\u0004\u0002\u0011%tG/\u001a:oC2L!AF\n\u0003\u000f1{wmZ5oO\"A\u0001\u0004\u0001B\u0001B\u0003%!$\u0001\u0003d_:47\u0001\u0001\t\u00037qi\u0011AB\u0005\u0003;\u0019\u0011\u0011b\u00159be.\u001cuN\u001c4\t\u0011}\u0001!Q1A\u0005\u0002\u0001\n!b]3sS\u0006d\u0017N_3s+\u0005\t\u0003C\u0001\u0012%\u001b\u0005\u0019#BA\u0010\u0007\u0013\t)3E\u0001\u0006TKJL\u0017\r\\5{KJD\u0001b\n\u0001\u0003\u0002\u0003\u0006I!I\u0001\fg\u0016\u0014\u0018.\u00197ju\u0016\u0014\b\u0005C\u0003*\u0001\u0011\u0005!&\u0001\u0004=S:LGO\u0010\u000b\u0004W1j\u0003C\u0001\b\u0001\u0011\u0015A\u0002\u00061\u0001\u001b\u0011\u0015y\u0002\u00061\u0001\"\u0011\u001dy\u0003A1A\u0005\nA\n1bV(S\u0017&sui\u0018#J%V\t\u0011\u0007\u0005\u00023o5\t1G\u0003\u00025k\u0005!A.\u00198h\u0015\u00051\u0014\u0001\u00026bm\u0006L!\u0001O\u001a\u0003\rM#(/\u001b8h\u0011\u0019Q\u0004\u0001)A\u0005c\u0005aqk\u0014*L\u0013:;u\fR%SA!9A\b\u0001b\u0001\n\u0013i\u0014A\u0001>l+\u0005q\u0004CA E\u001b\u0005\u0001%BA!C\u0003%1'/Y7fo>\u00148N\u0003\u0002D\u0011\u000591-\u001e:bi>\u0014\u0018BA#A\u0005A\u0019UO]1u_J4%/Y7fo>\u00148\u000e\u0003\u0004H\u0001\u0001\u0006IAP\u0001\u0004u.\u0004\u0003\"B%\u0001\t\u0003R\u0015a\u00029feNL7\u000f\u001e\u000b\u0004\u0017FK\u0006C\u0001'P\u001b\u0005i%\"\u0001(\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ak%\u0001B+oSRDQA\u0015%A\u0002M\u000bAA\\1nKB\u0011Ak\u0016\b\u0003\u0019VK!AV'\u0002\rA\u0013X\rZ3g\u0013\tA\u0004L\u0003\u0002W\u001b\")!\f\u0013a\u00017\u0006\u0019qN\u00196\u0011\u0005Ib\u0016BA/4\u0005\u0019y%M[3di\")q\f\u0001C!A\u0006IQO\u001c9feNL7\u000f\u001e\u000b\u0003\u0017\u0006DQA\u00150A\u0002MCQa\u0019\u0001\u0005B\u0011\fAA]3bIV\u0011Q-\u001e\u000b\u0004M\u00065ACA4\u007f!\rA\u0007o\u001d\b\u0003S:t!A[7\u000e\u0003-T!\u0001\\\r\u0002\rq\u0012xn\u001c;?\u0013\u0005q\u0015BA8N\u0003\u001d\u0001\u0018mY6bO\u0016L!!\u001d:\u0003\u0007M+\u0017O\u0003\u0002p\u001bB\u0011A/\u001e\u0007\u0001\t\u00151(M1\u0001x\u0005\u0005!\u0016C\u0001=|!\ta\u00150\u0003\u0002{\u001b\n9aj\u001c;iS:<\u0007C\u0001'}\u0013\tiXJA\u0002B]fD\u0001b 2\u0002\u0002\u0003\u000f\u0011\u0011A\u0001\u000bKZLG-\u001a8dK\u0012\n\u0004#BA\u0002\u0003\u0013\u0019XBAA\u0003\u0015\r\t9!T\u0001\be\u00164G.Z2u\u0013\u0011\tY!!\u0002\u0003\u0011\rc\u0017m]:UC\u001eDa!a\u0004c\u0001\u0004\u0019\u0016A\u00029sK\u001aL\u0007\u0010C\u0004\u0002\u0014\u0001!\t%!\u0006\u0002\u000b\rdwn]3\u0015\u0003-Cq!!\u0007\u0001\t\u0013\tY\"A\ttKJL\u0017\r\\5{K&sGo\u001c$jY\u0016$RaSA\u000f\u0003CAq!a\b\u0002\u0018\u0001\u00071+\u0001\u0003qCRD\u0007\u0002CA\u0012\u0003/\u0001\r!!\n\u0002\u000bY\fG.^3\u0011\u00071\u000b9#C\u0002\u0002*5\u0013a!\u00118z%\u00164\u0007bBA\u0017\u0001\u0011%\u0011qF\u0001\u0014I\u0016\u001cXM]5bY&TXM\u0012:p[\u001aKG.Z\u000b\u0005\u0003c\ti\u0004\u0006\u0003\u00024\u0005\u0015C\u0003BA\u001b\u0003\u007f\u0001R\u0001TA\u001c\u0003wI1!!\u000fN\u0005\u0019y\u0005\u000f^5p]B\u0019A/!\u0010\u0005\rY\fYC1\u0001x\u0011!\t\t%a\u000bA\u0004\u0005\r\u0013!A7\u0011\r\u0005\r\u0011\u0011BA\u001e\u0011\u001d\t9%a\u000bA\u0002M\u000b\u0001BZ5mK:\fW.\u001a"
)
public class ZooKeeperPersistenceEngine extends PersistenceEngine implements Logging {
   private final Serializer serializer;
   private final String WORKING_DIR;
   private final CuratorFramework zk;
   private transient Logger org$apache$spark$internal$Logging$$log_;

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

   public Serializer serializer() {
      return this.serializer;
   }

   private String WORKING_DIR() {
      return this.WORKING_DIR;
   }

   private CuratorFramework zk() {
      return this.zk;
   }

   public void persist(String name, Object obj) {
      this.serializeIntoFile((new StringBuilder()).append((Object)this.WORKING_DIR()).append((Object)"/").append((Object)name).toString(), obj);
   }

   public void unpersist(String name) {
      this.zk().delete().forPath((new StringBuilder()).append((Object)this.WORKING_DIR()).append((Object)"/").append((Object)name).toString());
   }

   public <T> Seq<T> read(String prefix, ClassTag<T> evidence$1) {
      return (Seq)((TraversableLike)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter((List)this.zk().getChildren().forPath(this.WORKING_DIR())).asScala()).filter(new Serializable(this, prefix) {
         public static final long serialVersionUID = 0L;
         private final String prefix$1;

         public final boolean apply(String x$1) {
            return x$1.startsWith(this.prefix$1);
         }

         public {
            this.prefix$1 = prefix$1;
         }
      })).flatMap(new Serializable(this, evidence$1) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ZooKeeperPersistenceEngine $outer;
         private final ClassTag evidence$1$1;

         public final Iterable<T> apply(String filename) {
            return Option$.MODULE$.<T>option2Iterable(this.$outer.org$apache$spark$deploy$master$ZooKeeperPersistenceEngine$$deserializeFromFile(filename, this.evidence$1$1));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
               this.evidence$1$1 = evidence$1$1;
            }
         }
      }, Buffer$.MODULE$.canBuildFrom());
   }

   public void close() {
      this.zk().close();
   }

   private void serializeIntoFile(String path, Object value) {
      ByteBuffer serialized = this.serializer().newInstance().serialize(value, ClassTag$.MODULE$.AnyRef());
      byte[] bytes = new byte[serialized.remaining()];
      serialized.get(bytes);
      ((PathAndBytesable)this.zk().create().withMode(CreateMode.PERSISTENT)).forPath(path, bytes);
   }

   public <T> Option<T> org$apache$spark$deploy$master$ZooKeeperPersistenceEngine$$deserializeFromFile(String filename, ClassTag<T> m) {
      byte[] fileData = (byte[])this.zk().getData().forPath((new StringBuilder()).append((Object)this.WORKING_DIR()).append((Object)"/").append((Object)filename).toString());

      Object var10000;
      try {
         var10000 = new Some(this.serializer().newInstance().deserialize(ByteBuffer.wrap(fileData), m));
      } catch (Exception var5) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Exception while reading persisted file, deleting";
            }
         }, var5);
         this.zk().delete().forPath((new StringBuilder()).append((Object)this.WORKING_DIR()).append((Object)"/").append((Object)filename).toString());
         var10000 = None$.MODULE$;
      }

      return (Option<T>)var10000;
   }

   public ZooKeeperPersistenceEngine(SparkConf conf, Serializer serializer) {
      this.serializer = serializer;
      Logging$class.$init$(this);
      this.WORKING_DIR = (new StringBuilder()).append((Object)conf.get("spark.deploy.zookeeper.dir", "/spark")).append((Object)"/master_status").toString();
      this.zk = SparkCuratorUtil$.MODULE$.newClient(conf, SparkCuratorUtil$.MODULE$.newClient$default$2());
      SparkCuratorUtil$.MODULE$.mkdir(this.zk(), this.WORKING_DIR());
   }
}
