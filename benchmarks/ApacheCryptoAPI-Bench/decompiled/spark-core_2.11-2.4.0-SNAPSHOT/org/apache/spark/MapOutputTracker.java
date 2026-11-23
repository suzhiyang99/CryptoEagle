package org.apache.spark;

import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.broadcast.BroadcastManager;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.scheduler.MapStatus;
import org.apache.spark.storage.BlockId;
import org.apache.spark.storage.BlockManagerId;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\u00055gAB\u0001\u0003\u0003\u0003\u0011\u0001B\u0001\tNCB|U\u000f\u001e9viR\u0013\u0018mY6fe*\u00111\u0001B\u0001\u0006gB\f'o\u001b\u0006\u0003\u000b\u0019\ta!\u00199bG\",'\"A\u0004\u0002\u0007=\u0014xmE\u0002\u0001\u0013=\u0001\"AC\u0007\u000e\u0003-Q\u0011\u0001D\u0001\u0006g\u000e\fG.Y\u0005\u0003\u001d-\u0011a!\u00118z%\u00164\u0007C\u0001\t\u0014\u001b\u0005\t\"B\u0001\n\u0003\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\u000b\u0012\u0005\u001daunZ4j]\u001eD\u0001B\u0006\u0001\u0003\u0002\u0003\u0006I\u0001G\u0001\u0005G>tgm\u0001\u0001\u0011\u0005eQR\"\u0001\u0002\n\u0005m\u0011!!C*qCJ\\7i\u001c8g\u0011\u0015i\u0002\u0001\"\u0001\u001f\u0003\u0019a\u0014N\\5u}Q\u0011q\u0004\t\t\u00033\u0001AQA\u0006\u000fA\u0002aA\u0011B\t\u0001A\u0002\u0003\u0007I\u0011A\u0012\u0002\u001fQ\u0014\u0018mY6fe\u0016sG\r]8j]R,\u0012\u0001\n\t\u0003K!j\u0011A\n\u0006\u0003O\t\t1A\u001d9d\u0013\tIcE\u0001\bSa\u000e,e\u000e\u001a9pS:$(+\u001a4\t\u0013-\u0002\u0001\u0019!a\u0001\n\u0003a\u0013a\u0005;sC\u000e\\WM]#oIB|\u0017N\u001c;`I\u0015\fHCA\u00171!\tQa&\u0003\u00020\u0017\t!QK\\5u\u0011\u001d\t$&!AA\u0002\u0011\n1\u0001\u001f\u00132\u0011\u0019\u0019\u0004\u0001)Q\u0005I\u0005\u0001BO]1dW\u0016\u0014XI\u001c3q_&tG\u000f\t\u0005\bk\u0001\u0001\r\u0011\"\u00057\u0003\u0015)\u0007o\\2i+\u00059\u0004C\u0001\u00069\u0013\tI4B\u0001\u0003M_:<\u0007bB\u001e\u0001\u0001\u0004%\t\u0002P\u0001\nKB|7\r[0%KF$\"!L\u001f\t\u000fER\u0014\u0011!a\u0001o!1q\b\u0001Q!\n]\na!\u001a9pG\"\u0004\u0003bB!\u0001\u0005\u0004%\tBQ\u0001\nKB|7\r\u001b'pG.,\u0012a\u0011\t\u0003\t&k\u0011!\u0012\u0006\u0003\r\u001e\u000bA\u0001\\1oO*\t\u0001*\u0001\u0003kCZ\f\u0017B\u0001&F\u0005\u0019y%M[3di\"1A\n\u0001Q\u0001\n\r\u000b!\"\u001a9pG\"dunY6!\u0011\u0015q\u0005\u0001\"\u0005P\u0003)\t7o\u001b+sC\u000e\\WM]\u000b\u0003!R#\"!U3\u0015\u0005Ik\u0006CA*U\u0019\u0001!Q!V'C\u0002Y\u0013\u0011\u0001V\t\u0003/j\u0003\"A\u0003-\n\u0005e[!a\u0002(pi\"Lgn\u001a\t\u0003\u0015mK!\u0001X\u0006\u0003\u0007\u0005s\u0017\u0010C\u0004_\u001b\u0006\u0005\t9A0\u0002\u0015\u00154\u0018\u000eZ3oG\u0016$\u0013\u0007E\u0002aGJk\u0011!\u0019\u0006\u0003E.\tqA]3gY\u0016\u001cG/\u0003\u0002eC\nA1\t\\1tgR\u000bw\rC\u0003g\u001b\u0002\u0007!,A\u0004nKN\u001c\u0018mZ3\t\u000b!\u0004A\u0011C5\u0002\u0017M,g\u000e\u001a+sC\u000e\\WM\u001d\u000b\u0003[)DQAZ4A\u0002iCQ\u0001\u001c\u0001\u0005\u00025\fqcZ3u\u001b\u0006\u00048+\u001b>fg\nKX\t_3dkR|'/\u00133\u0015\u000b9\f\t\"a\u0007\u0011\u0007=<(P\u0004\u0002qk:\u0011\u0011\u000f^\u0007\u0002e*\u00111oF\u0001\u0007yI|w\u000e\u001e \n\u00031I!A^\u0006\u0002\u000fA\f7m[1hK&\u0011\u00010\u001f\u0002\u0004'\u0016\f(B\u0001<\f!\u0015Q10`A\u0004\u0013\ta8B\u0001\u0004UkBdWM\r\t\u0004}\u0006\rQ\"A@\u000b\u0007\u0005\u0005!!A\u0004ti>\u0014\u0018mZ3\n\u0007\u0005\u0015qP\u0001\bCY>\u001c7.T1oC\u001e,'/\u00133\u0011\t=<\u0018\u0011\u0002\t\u0006\u0015m\fYa\u000e\t\u0004}\u00065\u0011bAA\b\u007f\n9!\t\\8dW&#\u0007bBA\nW\u0002\u0007\u0011QC\u0001\ng\",hM\u001a7f\u0013\u0012\u00042ACA\f\u0013\r\tIb\u0003\u0002\u0004\u0013:$\bbBA\u000fW\u0002\u0007\u0011QC\u0001\te\u0016$WoY3JI\"1A\u000e\u0001D\u0001\u0003C!rA\\A\u0012\u0003K\tI\u0003\u0003\u0005\u0002\u0014\u0005}\u0001\u0019AA\u000b\u0011!\t9#a\bA\u0002\u0005U\u0011AD:uCJ$\b+\u0019:uSRLwN\u001c\u0005\t\u0003W\ty\u00021\u0001\u0002\u0016\u0005aQM\u001c3QCJ$\u0018\u000e^5p]\"9\u0011q\u0006\u0001\u0007\u0002\u0005E\u0012!E;oe\u0016<\u0017n\u001d;feNCWO\u001a4mKR\u0019Q&a\r\t\u0011\u0005M\u0011Q\u0006a\u0001\u0003+Aq!a\u000e\u0001\t\u0003\tI$\u0001\u0003ti>\u0004H#A\u0017\b\u0011\u0005u\"\u0001#\u0001\u0003\u0003\u007f\t\u0001#T1q\u001fV$\b/\u001e;Ue\u0006\u001c7.\u001a:\u0011\u0007e\t\tEB\u0004\u0002\u0005!\u0005!!a\u0011\u0014\t\u0005\u0005\u0013b\u0004\u0005\b;\u0005\u0005C\u0011AA$)\t\ty\u0004\u0003\u0006\u0002L\u0005\u0005#\u0019!C\u0001\u0003\u001b\nQ\"\u0012(E!>Ke\nV0O\u00036+UCAA(!\r!\u0015\u0011K\u0005\u0004\u0003'*%AB*ue&tw\rC\u0005\u0002X\u0005\u0005\u0003\u0015!\u0003\u0002P\u0005qQI\u0014#Q\u001f&sEk\u0018(B\u001b\u0016\u0003\u0003BCA.\u0003\u0003\u0012\r\u0011\"\u0003\u0002^\u00051A)\u0013*F\u0007R+\"!!\u0006\t\u0013\u0005\u0005\u0014\u0011\tQ\u0001\n\u0005U\u0011a\u0002#J%\u0016\u001bE\u000b\t\u0005\u000b\u0003K\n\tE1A\u0005\n\u0005u\u0013!\u0003\"S\u001f\u0006#5)Q*U\u0011%\tI'!\u0011!\u0002\u0013\t)\"\u0001\u0006C%>\u000bEiQ!T)\u0002B\u0001\"!\u001c\u0002B\u0011\u0005\u0011qN\u0001\u0015g\u0016\u0014\u0018.\u00197ju\u0016l\u0015\r]*uCR,8/Z:\u0015\u0015\u0005E\u00141RAO\u0003O\u000b\t\f\u0005\u0004\u000bw\u0006M\u0014q\u0010\t\u0006\u0015\u0005U\u0014\u0011P\u0005\u0004\u0003oZ!!B!se\u0006L\bc\u0001\u0006\u0002|%\u0019\u0011QP\u0006\u0003\t\tKH/\u001a\t\u0007\u0003\u0003\u000b9)a\u001d\u000e\u0005\u0005\r%bAAC\u0005\u0005I!M]8bI\u000e\f7\u000f^\u0005\u0005\u0003\u0013\u000b\u0019IA\u0005Ce>\fGmY1ti\"A\u0011QRA6\u0001\u0004\ty)\u0001\u0005ti\u0006$Xo]3t!\u0015Q\u0011QOAI!\u0011\t\u0019*!'\u000e\u0005\u0005U%bAAL\u0005\u0005I1o\u00195fIVdWM]\u0005\u0005\u00037\u000b)JA\u0005NCB\u001cF/\u0019;vg\"A\u0011qTA6\u0001\u0004\t\t+\u0001\tce>\fGmY1ti6\u000bg.Y4feB!\u0011\u0011QAR\u0013\u0011\t)+a!\u0003!\t\u0013x.\u00193dCN$X*\u00198bO\u0016\u0014\b\u0002CAU\u0003W\u0002\r!a+\u0002\u000f%\u001cHj\\2bYB\u0019!\"!,\n\u0007\u0005=6BA\u0004C_>dW-\u00198\t\u0011\u0005M\u00161\u000ea\u0001\u0003+\t\u0001#\\5o\u0005J|\u0017\rZ2bgR\u001c\u0016N_3\t\u0011\u0005]\u0016\u0011\tC\u0001\u0003s\u000ba\u0003Z3tKJL\u0017\r\\5{K6\u000b\u0007o\u0015;biV\u001cXm\u001d\u000b\u0005\u0003\u001f\u000bY\f\u0003\u0005\u0002>\u0006U\u0006\u0019AA:\u0003\u0015\u0011\u0017\u0010^3t\u0011!\t\t-!\u0011\u0005\u0002\u0005\r\u0017AE2p]Z,'\u000f^'baN#\u0018\r^;tKN$\u0012B\\Ac\u0003\u000f\fI-a3\t\u0011\u0005M\u0011q\u0018a\u0001\u0003+A\u0001\"a\n\u0002@\u0002\u0007\u0011Q\u0003\u0005\t\u0003W\ty\f1\u0001\u0002\u0016!A\u0011QRA`\u0001\u0004\ty\t"
)
public abstract class MapOutputTracker implements Logging {
   private RpcEndpointRef trackerEndpoint;
   private long epoch;
   private final Object epochLock;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Seq<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>> convertMapStatuses(int var0, int var1, int var2, MapStatus[] var3) {
      return MapOutputTracker$.MODULE$.convertMapStatuses(var0, var1, var2, var3);
   }

   public static MapStatus[] deserializeMapStatuses(byte[] var0) {
      return MapOutputTracker$.MODULE$.deserializeMapStatuses(var0);
   }

   public static Tuple2<byte[], Broadcast<byte[]>> serializeMapStatuses(MapStatus[] var0, BroadcastManager var1, boolean var2, int var3) {
      return MapOutputTracker$.MODULE$.serializeMapStatuses(var0, var1, var2, var3);
   }

   public static String ENDPOINT_NAME() {
      return MapOutputTracker$.MODULE$.ENDPOINT_NAME();
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

   public RpcEndpointRef trackerEndpoint() {
      return this.trackerEndpoint;
   }

   public void trackerEndpoint_$eq(RpcEndpointRef x$1) {
      this.trackerEndpoint = x$1;
   }

   public long epoch() {
      return this.epoch;
   }

   public void epoch_$eq(long x$1) {
      this.epoch = x$1;
   }

   public Object epochLock() {
      return this.epochLock;
   }

   public <T> T askTracker(Object message, ClassTag<T> evidence$1) {
      try {
         return (T)this.trackerEndpoint().askSync(message, evidence$1);
      } catch (Exception var4) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Error communicating with MapOutputTracker";
            }
         }, var4);
         throw new SparkException("Error communicating with MapOutputTracker", var4);
      }
   }

   public void sendTracker(Object message) {
      boolean response = BoxesRunTime.unboxToBoolean(this.askTracker(message, ClassTag$.MODULE$.Boolean()));
      if (!response) {
         throw new SparkException((new StringBuilder()).append((Object)"Error reply received from MapOutputTracker. Expecting true, got ").append((Object)BoxesRunTime.boxToBoolean(response).toString()).toString());
      }
   }

   public Seq<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>> getMapSizesByExecutorId(int shuffleId, int reduceId) {
      return this.getMapSizesByExecutorId(shuffleId, reduceId, reduceId + 1);
   }

   public abstract Seq<Tuple2<BlockManagerId, Seq<Tuple2<BlockId, Object>>>> getMapSizesByExecutorId(int var1, int var2, int var3);

   public abstract void unregisterShuffle(int var1);

   public void stop() {
   }

   public MapOutputTracker(SparkConf conf) {
      Logging$class.$init$(this);
      this.epoch = 0L;
      this.epochLock = new Object();
   }
}
