package org.apache.spark;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.broadcast.BroadcastManager;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rdd.RDD;
import org.apache.spark.rdd.ReliableRDDCheckpointData$;
import org.apache.spark.storage.BlockManagerMaster;
import org.apache.spark.util.AccumulatorContext$;
import org.apache.spark.util.AccumulatorV2;
import org.apache.spark.util.ThreadUtils$;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\tec!B\u0001\u0003\u0001\tA!AD\"p]R,\u0007\u0010^\"mK\u0006tWM\u001d\u0006\u0003\u0007\u0011\tQa\u001d9be.T!!\u0002\u0004\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u00059\u0011aA8sON\u0019\u0001!C\b\u0011\u0005)iQ\"A\u0006\u000b\u00031\tQa]2bY\u0006L!AD\u0006\u0003\r\u0005s\u0017PU3g!\t\u00012#D\u0001\u0012\u0015\t\u0011\"!\u0001\u0005j]R,'O\\1m\u0013\t!\u0012CA\u0004M_\u001e<\u0017N\\4\t\u0011Y\u0001!\u0011!Q\u0001\na\t!a]2\u0004\u0001A\u0011\u0011DG\u0007\u0002\u0005%\u00111D\u0001\u0002\r'B\f'o[\"p]R,\u0007\u0010\u001e\u0005\u0006;\u0001!\tAH\u0001\u0007y%t\u0017\u000e\u001e \u0015\u0005}\u0001\u0003CA\r\u0001\u0011\u00151B\u00041\u0001\u0019\u0011\u001d\u0011\u0003A1A\u0005\n\r\nqB]3gKJ,gnY3Ck\u001a4WM]\u000b\u0002IA\u0019QE\u000b\u0017\u000e\u0003\u0019R!a\n\u0015\u0002\tU$\u0018\u000e\u001c\u0006\u0002S\u0005!!.\u0019<b\u0013\tYcEA\u0002TKR\u0004\"!G\u0017\n\u00059\u0012!\u0001G\"mK\u0006tW\u000f\u001d+bg.<V-Y6SK\u001a,'/\u001a8dK\"1\u0001\u0007\u0001Q\u0001\n\u0011\n\u0001C]3gKJ,gnY3Ck\u001a4WM\u001d\u0011\t\u000fI\u0002!\u0019!C\u0005g\u0005q!/\u001a4fe\u0016t7-Z)vKV,W#\u0001\u001b\u0011\u0007UR\u0014\"D\u00017\u0015\t9\u0004(A\u0002sK\u001aT!!\u000f\u0015\u0002\t1\fgnZ\u0005\u0003wY\u0012aBU3gKJ,gnY3Rk\u0016,X\r\u0003\u0004>\u0001\u0001\u0006I\u0001N\u0001\u0010e\u00164WM]3oG\u0016\fV/Z;fA!9q\b\u0001b\u0001\n\u0013\u0001\u0015!\u00037jgR,g.\u001a:t+\u0005\t\u0005c\u0001\"F\u000f6\t1I\u0003\u0002EM\u0005Q1m\u001c8dkJ\u0014XM\u001c;\n\u0005\u0019\u001b%!F\"p]\u000e,(O]3oi2Kgn[3e#V,W/\u001a\t\u00033!K!!\u0013\u0002\u0003\u001f\rcW-\u00198fe2K7\u000f^3oKJDaa\u0013\u0001!\u0002\u0013\t\u0015A\u00037jgR,g.\u001a:tA!9Q\n\u0001b\u0001\n\u0013q\u0015AD2mK\u0006t\u0017N\\4UQJ,\u0017\rZ\u000b\u0002\u001fB\u0011\u0001+U\u0007\u0002q%\u0011!\u000b\u000f\u0002\u0007)\"\u0014X-\u00193\t\rQ\u0003\u0001\u0015!\u0003P\u0003=\u0019G.Z1oS:<G\u000b\u001b:fC\u0012\u0004\u0003b\u0002,\u0001\u0005\u0004%IaV\u0001\u0012a\u0016\u0014\u0018n\u001c3jG\u001e\u001b5+\u001a:wS\u000e,W#\u0001-\u0011\u0005\tK\u0016B\u0001.D\u0005a\u00196\r[3ek2,G-\u0012=fGV$xN]*feZL7-\u001a\u0005\u00079\u0002\u0001\u000b\u0011\u0002-\u0002%A,'/[8eS\u000e<5iU3sm&\u001cW\r\t\u0005\b=\u0002\u0011\r\u0011\"\u0003`\u0003I\u0001XM]5pI&\u001cwiQ%oi\u0016\u0014h/\u00197\u0016\u0003\u0001\u0004\"AC1\n\u0005\t\\!\u0001\u0002'p]\u001eDa\u0001\u001a\u0001!\u0002\u0013\u0001\u0017a\u00059fe&|G-[2H\u0007&sG/\u001a:wC2\u0004\u0003b\u00024\u0001\u0005\u0004%IaZ\u0001\u0014E2|7m[(o\u00072,\u0017M\\;q)\u0006\u001c8n]\u000b\u0002QB\u0011!\"[\u0005\u0003U.\u0011qAQ8pY\u0016\fg\u000e\u0003\u0004m\u0001\u0001\u0006I\u0001[\u0001\u0015E2|7m[(o\u00072,\u0017M\\;q)\u0006\u001c8n\u001d\u0011\t\u000f9\u0004!\u0019!C\u0005O\u0006Q\"\r\\8dW>s7\u000b[;gM2,7\t\\3b]V\u0004H+Y:lg\"1\u0001\u000f\u0001Q\u0001\n!\f1D\u00197pG.|en\u00155vM\u001adWm\u00117fC:,\b\u000fV1tWN\u0004\u0003b\u0002:\u0001\u0001\u0004%IaZ\u0001\bgR|\u0007\u000f]3e\u0011\u001d!\b\u00011A\u0005\nU\f1b\u001d;paB,Gm\u0018\u0013fcR\u0011a/\u001f\t\u0003\u0015]L!\u0001_\u0006\u0003\tUs\u0017\u000e\u001e\u0005\buN\f\t\u00111\u0001i\u0003\rAH%\r\u0005\u0007y\u0002\u0001\u000b\u0015\u00025\u0002\u0011M$x\u000e\u001d9fI\u0002B#a\u001f@\u0011\u0005)y\u0018bAA\u0001\u0017\tAao\u001c7bi&dW\rC\u0004\u0002\u0006\u0001!\t!a\u0002\u0002\u001d\u0005$H/Y2i\u0019&\u001cH/\u001a8feR\u0019a/!\u0003\t\u000f\u0005-\u00111\u0001a\u0001\u000f\u0006AA.[:uK:,'\u000fC\u0004\u0002\u0010\u0001!\t!!\u0005\u0002\u000bM$\u0018M\u001d;\u0015\u0003YDq!!\u0006\u0001\t\u0003\t\t\"\u0001\u0003ti>\u0004\bbBA\r\u0001\u0011\u0005\u00111D\u0001\u0016e\u0016<\u0017n\u001d;feJ#EIR8s\u00072,\u0017M\\;q)\r1\u0018Q\u0004\u0005\t\u0003?\t9\u00021\u0001\u0002\"\u0005\u0019!\u000f\u001a31\t\u0005\r\u0012\u0011\u0007\t\u0007\u0003K\tI#!\f\u000e\u0005\u0005\u001d\"bAA\u0010\u0005%!\u00111FA\u0014\u0005\r\u0011F\t\u0012\t\u0005\u0003_\t\t\u0004\u0004\u0001\u0005\u0019\u0005M\u0012QDA\u0001\u0002\u0003\u0015\t!!\u000e\u0003\u0007}#\u0013'\u0005\u0003\u00028\u0005u\u0002c\u0001\u0006\u0002:%\u0019\u00111H\u0006\u0003\u000f9{G\u000f[5oOB\u0019!\"a\u0010\n\u0007\u0005\u00053BA\u0002B]fDq!!\u0012\u0001\t\u0003\t9%A\u000fsK\u001eL7\u000f^3s\u0003\u000e\u001cW/\\;mCR|'OR8s\u00072,\u0017M\\;q)\r1\u0018\u0011\n\u0005\t\u0003\u0017\n\u0019\u00051\u0001\u0002N\u0005\t\u0011\r\r\u0004\u0002P\u0005m\u0013\u0011\r\t\t\u0003#\n)&!\u0017\u0002`5\u0011\u00111\u000b\u0006\u0003O\tIA!a\u0016\u0002T\ti\u0011iY2v[Vd\u0017\r^8s-J\u0002B!a\f\u0002\\\u0011a\u0011QLA%\u0003\u0003\u0005\tQ!\u0001\u00026\t\u0019q\f\n\u001a\u0011\t\u0005=\u0012\u0011\r\u0003\r\u0003G\nI%!A\u0001\u0002\u000b\u0005\u0011Q\u0007\u0002\u0004?\u0012\u001a\u0004bBA4\u0001\u0011\u0005\u0011\u0011N\u0001\u001ae\u0016<\u0017n\u001d;feNCWO\u001a4mK\u001a{'o\u00117fC:,\b\u000fF\u0002w\u0003WB\u0001\"!\u001c\u0002f\u0001\u0007\u0011qN\u0001\u0012g\",hM\u001a7f\t\u0016\u0004XM\u001c3f]\u000eL\b\u0007CA9\u0003s\ny(!\"\u0011\u0013e\t\u0019(a\u001e\u0002~\u0005\r\u0015bAA;\u0005\t\t2\u000b[;gM2,G)\u001a9f]\u0012,gnY=\u0011\t\u0005=\u0012\u0011\u0010\u0003\r\u0003w\nY'!A\u0001\u0002\u000b\u0005\u0011Q\u0007\u0002\u0004?\u0012\"\u0004\u0003BA\u0018\u0003\u007f\"A\"!!\u0002l\u0005\u0005\t\u0011!B\u0001\u0003k\u00111a\u0018\u00136!\u0011\ty#!\"\u0005\u0019\u0005\u001d\u00151NA\u0001\u0002\u0003\u0015\t!!\u000e\u0003\u0007}#c\u0007C\u0004\u0002\f\u0002!\t!!$\u00027I,w-[:uKJ\u0014%o\\1eG\u0006\u001cHOR8s\u00072,\u0017M\\;q+\u0011\ty)!)\u0015\u0007Y\f\t\n\u0003\u0005\u0002\u0014\u0006%\u0005\u0019AAK\u0003%\u0011'o\\1eG\u0006\u001cH\u000f\u0005\u0004\u0002\u0018\u0006m\u0015qT\u0007\u0003\u00033S1!a%\u0003\u0013\u0011\ti*!'\u0003\u0013\t\u0013x.\u00193dCN$\b\u0003BA\u0018\u0003C#\u0001\"a)\u0002\n\n\u0007\u0011Q\u0007\u0002\u0002)\"9\u0011q\u0015\u0001\u0005\u0002\u0005%\u0016a\t:fO&\u001cH/\u001a:S\t\u0012\u001b\u0005.Z2la>Lg\u000e\u001e#bi\u00064uN]\"mK\u0006tW\u000f]\u000b\u0005\u0003W\u000b\u0019\rF\u0003w\u0003[\u000bI\f\u0003\u0005\u0002 \u0005\u0015\u0006\u0019AAXa\u0011\t\t,!.\u0011\r\u0005\u0015\u0012\u0011FAZ!\u0011\ty#!.\u0005\u0019\u0005]\u0016QVA\u0001\u0002\u0003\u0015\t!!\u000e\u0003\u0007}#s\u0007\u0003\u0005\u0002<\u0006\u0015\u0006\u0019AA_\u0003!\u0001\u0018M]3oi&#\u0007c\u0001\u0006\u0002@&\u0019\u0011\u0011Y\u0006\u0003\u0007%sG\u000f\u0002\u0005\u0002$\u0006\u0015&\u0019AA\u001b\u0011\u001d\t9\r\u0001C\u0005\u0003\u0013\f!C]3hSN$XM\u001d$pe\u000ecW-\u00198vaR)a/a3\u0002P\"9\u0011QZAc\u0001\u0004I\u0011\u0001E8cU\u0016\u001cGOR8s\u00072,\u0017M\\;q\u0011!\t\t.!2A\u0002\u0005M\u0017\u0001\u0002;bg.\u00042!GAk\u0013\r\t9N\u0001\u0002\f\u00072,\u0017M\\;q)\u0006\u001c8\u000eC\u0004\u0002\\\u0002!I!!\u0005\u0002\u0019-,W\r]\"mK\u0006t\u0017N\\4\t\u000f\u0005}\u0007\u0001\"\u0001\u0002b\u0006aAm\\\"mK\u0006tW\u000f\u001d*E\tR)a/a9\u0002h\"A\u0011Q]Ao\u0001\u0004\ti,A\u0003sI\u0012LE\rC\u0004\u0002j\u0006u\u0007\u0019\u00015\u0002\u0011\tdwnY6j]\u001eDq!!<\u0001\t\u0003\ty/\u0001\te_\u000ecW-\u00198vaNCWO\u001a4mKR)a/!=\u0002v\"A\u00111_Av\u0001\u0004\ti,A\u0005tQV4g\r\\3JI\"9\u0011\u0011^Av\u0001\u0004A\u0007bBA}\u0001\u0011\u0005\u00111`\u0001\u0013I>\u001cE.Z1okB\u0014%o\\1eG\u0006\u001cH\u000fF\u0003w\u0003{\u0014\t\u0001C\u0004\u0002\u0000\u0006]\b\u0019\u00011\u0002\u0017\t\u0014x.\u00193dCN$\u0018\n\u001a\u0005\b\u0003S\f9\u00101\u0001i\u0011\u001d\u0011)\u0001\u0001C\u0001\u0005\u000f\ta\u0002Z8DY\u0016\fg.\u001e9BG\u000e,X\u000eF\u0003w\u0005\u0013\u0011i\u0001C\u0004\u0003\f\t\r\u0001\u0019\u00011\u0002\u000b\u0005\u001c7-\u00133\t\u000f\u0005%(1\u0001a\u0001Q\"9!\u0011\u0003\u0001\u0005\u0002\tM\u0011!\u00053p\u00072,\u0017M\\\"iK\u000e\\\u0007o\\5oiR\u0019aO!\u0006\t\u0011\u0005\u0015(q\u0002a\u0001\u0003{CqA!\u0007\u0001\t\u0013\u0011Y\"\u0001\ncY>\u001c7.T1oC\u001e,'/T1ti\u0016\u0014XC\u0001B\u000f!\u0011\u0011yB!\n\u000e\u0005\t\u0005\"b\u0001B\u0012\u0005\u000591\u000f^8sC\u001e,\u0017\u0002\u0002B\u0014\u0005C\u0011!C\u00117pG.l\u0015M\\1hKJl\u0015m\u001d;fe\"9!1\u0006\u0001\u0005\n\t5\u0012\u0001\u00052s_\u0006$7-Y:u\u001b\u0006t\u0017mZ3s+\t\u0011y\u0003\u0005\u0003\u0002\u0018\nE\u0012\u0002\u0002B\u001a\u00033\u0013\u0001C\u0011:pC\u0012\u001c\u0017m\u001d;NC:\fw-\u001a:\t\u000f\t]\u0002\u0001\"\u0003\u0003:\u00051R.\u00199PkR\u0004X\u000f\u001e+sC\u000e\\WM]'bgR,'/\u0006\u0002\u0003<A\u0019\u0011D!\u0010\n\u0007\t}\"A\u0001\fNCB|U\u000f\u001e9viR\u0013\u0018mY6fe6\u000b7\u000f^3s\u000f\u001d\u0011\u0019E\u0001E\u0005\u0005\u000b\nabQ8oi\u0016DHo\u00117fC:,'\u000fE\u0002\u001a\u0005\u000f2a!\u0001\u0002\t\n\t%3c\u0001B$\u0013!9QDa\u0012\u0005\u0002\t5CC\u0001B#\u0011)\u0011\tFa\u0012C\u0002\u0013%!1K\u0001\u0017%\u00163u,U+F+\u0016{\u0006k\u0014'M?RKU*R(V)V\u0011\u0011Q\u0018\u0005\n\u0005/\u00129\u0005)A\u0005\u0003{\u000bqCU#G?F+V)V#`!>cEj\u0018+J\u001b\u0016{U\u000b\u0016\u0011"
)
public class ContextCleaner implements Logging {
   private final SparkContext sc;
   private final Set<CleanupTaskWeakReference> org$apache$spark$ContextCleaner$$referenceBuffer;
   private final ReferenceQueue<Object> org$apache$spark$ContextCleaner$$referenceQueue;
   private final ConcurrentLinkedQueue<CleanerListener> listeners;
   private final Thread cleaningThread;
   private final ScheduledExecutorService periodicGCService;
   private final long periodicGCInterval;
   private final boolean org$apache$spark$ContextCleaner$$blockOnCleanupTasks;
   private final boolean org$apache$spark$ContextCleaner$$blockOnShuffleCleanupTasks;
   private volatile boolean org$apache$spark$ContextCleaner$$stopped;
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

   public Set<CleanupTaskWeakReference> org$apache$spark$ContextCleaner$$referenceBuffer() {
      return this.org$apache$spark$ContextCleaner$$referenceBuffer;
   }

   public ReferenceQueue<Object> org$apache$spark$ContextCleaner$$referenceQueue() {
      return this.org$apache$spark$ContextCleaner$$referenceQueue;
   }

   private ConcurrentLinkedQueue<CleanerListener> listeners() {
      return this.listeners;
   }

   private Thread cleaningThread() {
      return this.cleaningThread;
   }

   private ScheduledExecutorService periodicGCService() {
      return this.periodicGCService;
   }

   private long periodicGCInterval() {
      return this.periodicGCInterval;
   }

   public boolean org$apache$spark$ContextCleaner$$blockOnCleanupTasks() {
      return this.org$apache$spark$ContextCleaner$$blockOnCleanupTasks;
   }

   public boolean org$apache$spark$ContextCleaner$$blockOnShuffleCleanupTasks() {
      return this.org$apache$spark$ContextCleaner$$blockOnShuffleCleanupTasks;
   }

   public boolean org$apache$spark$ContextCleaner$$stopped() {
      return this.org$apache$spark$ContextCleaner$$stopped;
   }

   private void org$apache$spark$ContextCleaner$$stopped_$eq(boolean x$1) {
      this.org$apache$spark$ContextCleaner$$stopped = x$1;
   }

   public void attachListener(CleanerListener listener) {
      this.listeners().add(listener);
   }

   public void start() {
      this.cleaningThread().setDaemon(true);
      this.cleaningThread().setName("Spark Context Cleaner");
      this.cleaningThread().start();
      this.periodicGCService().scheduleAtFixedRate(new Runnable(this) {
         public void run() {
            System.gc();
         }
      }, this.periodicGCInterval(), this.periodicGCInterval(), TimeUnit.SECONDS);
   }

   public void stop() {
      this.org$apache$spark$ContextCleaner$$stopped_$eq(true);
      synchronized(this) {
         this.cleaningThread().interrupt();
         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      this.cleaningThread().join();
      this.periodicGCService().shutdown();
   }

   public void registerRDDForCleanup(RDD<?> rdd) {
      this.registerForCleanup(rdd, new CleanRDD(rdd.id()));
   }

   public void registerAccumulatorForCleanup(AccumulatorV2<?, ?> a) {
      this.registerForCleanup(a, new CleanAccum(a.id()));
   }

   public void registerShuffleForCleanup(ShuffleDependency<?, ?, ?> shuffleDependency) {
      this.registerForCleanup(shuffleDependency, new CleanShuffle(shuffleDependency.shuffleId()));
   }

   public <T> void registerBroadcastForCleanup(Broadcast<T> broadcast) {
      this.registerForCleanup(broadcast, new CleanBroadcast(broadcast.id()));
   }

   public <T> void registerRDDCheckpointDataForCleanup(RDD<?> rdd, int parentId) {
      this.registerForCleanup(rdd, new CleanCheckpoint(parentId));
   }

   private void registerForCleanup(Object objectForCleanup, CleanupTask task) {
      this.org$apache$spark$ContextCleaner$$referenceBuffer().add(new CleanupTaskWeakReference(task, objectForCleanup, this.org$apache$spark$ContextCleaner$$referenceQueue()));
   }

   public void org$apache$spark$ContextCleaner$$keepCleaning() {
      Utils$.MODULE$.tryOrStopSparkContext(this.sc, new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ContextCleaner $outer;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            while(!this.$outer.org$apache$spark$ContextCleaner$$stopped()) {
               try {
                  Option reference = Option$.MODULE$.apply(this.$outer.org$apache$spark$ContextCleaner$$referenceQueue().remove((long)ContextCleaner$.MODULE$.org$apache$spark$ContextCleaner$$REF_QUEUE_POLL_TIMEOUT())).map(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final CleanupTaskWeakReference apply(Reference<Object> x$1) {
                        return (CleanupTaskWeakReference)x$1;
                     }
                  });
                  synchronized(this.$outer) {
                     reference.foreach(new Serializable(this) {
                        public static final long serialVersionUID = 0L;
                        // $FF: synthetic field
                        private final <undefinedtype> $outer;

                        public final void apply(CleanupTaskWeakReference ref) {
                           this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().logDebug(new Serializable(this, ref) {
                              public static final long serialVersionUID = 0L;
                              private final CleanupTaskWeakReference ref$1;

                              public final String apply() {
                                 return (new StringBuilder()).append((Object)"Got cleaning task ").append((Object)this.ref$1.task()).toString();
                              }

                              public {
                                 this.ref$1 = ref$1;
                              }
                           });
                           this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().org$apache$spark$ContextCleaner$$referenceBuffer().remove(ref);
                           CleanupTask var2 = ref.task();
                           if (var2 instanceof CleanRDD) {
                              CleanRDD var3 = (CleanRDD)var2;
                              int rddId = var3.rddId();
                              this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().doCleanupRDD(rddId, this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().org$apache$spark$ContextCleaner$$blockOnCleanupTasks());
                              BoxedUnit var5 = BoxedUnit.UNIT;
                           } else if (var2 instanceof CleanShuffle) {
                              CleanShuffle var6 = (CleanShuffle)var2;
                              int shuffleId = var6.shuffleId();
                              this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().doCleanupShuffle(shuffleId, this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().org$apache$spark$ContextCleaner$$blockOnShuffleCleanupTasks());
                              BoxedUnit var16 = BoxedUnit.UNIT;
                           } else if (var2 instanceof CleanBroadcast) {
                              CleanBroadcast var8 = (CleanBroadcast)var2;
                              long broadcastId = var8.broadcastId();
                              this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().doCleanupBroadcast(broadcastId, this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().org$apache$spark$ContextCleaner$$blockOnCleanupTasks());
                              BoxedUnit var17 = BoxedUnit.UNIT;
                           } else if (var2 instanceof CleanAccum) {
                              CleanAccum var11 = (CleanAccum)var2;
                              long accId = var11.accId();
                              this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().doCleanupAccum(accId, this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().org$apache$spark$ContextCleaner$$blockOnCleanupTasks());
                              BoxedUnit var18 = BoxedUnit.UNIT;
                           } else {
                              if (!(var2 instanceof CleanCheckpoint)) {
                                 throw new MatchError(var2);
                              }

                              CleanCheckpoint var14 = (CleanCheckpoint)var2;
                              int rddId = var14.rddId();
                              this.$outer.org$apache$spark$ContextCleaner$$anonfun$$$outer().doCleanCheckpoint(rddId);
                              BoxedUnit var19 = BoxedUnit.UNIT;
                           }

                        }

                        public {
                           if ($outer == null) {
                              throw null;
                           } else {
                              this.$outer = $outer;
                           }
                        }
                     });
                     BoxedUnit var7 = BoxedUnit.UNIT;
                  }
               } catch (Throwable var11) {
                  if (var11 instanceof InterruptedException && this.$outer.org$apache$spark$ContextCleaner$$stopped()) {
                     BoxedUnit var12 = BoxedUnit.UNIT;
                  } else {
                     if (!(var11 instanceof Exception)) {
                        throw var11;
                     }

                     Exception var4 = (Exception)var11;
                     this.$outer.logError(new Serializable(this) {
                        public static final long serialVersionUID = 0L;

                        public final String apply() {
                           return "Error in cleaning thread";
                        }
                     }, var4);
                     BoxedUnit var3 = BoxedUnit.UNIT;
                  }

                  BoxedUnit var10000 = BoxedUnit.UNIT;
                  continue;
               }
            }

         }

         // $FF: synthetic method
         public ContextCleaner org$apache$spark$ContextCleaner$$anonfun$$$outer() {
            return this.$outer;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
   }

   public void doCleanupRDD(int rddId, boolean blocking) {
      try {
         this.logDebug(new Serializable(this, rddId) {
            public static final long serialVersionUID = 0L;
            private final int rddId$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Cleaning RDD ").append((Object)BoxesRunTime.boxToInteger(this.rddId$1)).toString();
            }

            public {
               this.rddId$1 = rddId$1;
            }
         });
         this.sc.unpersistRDD(rddId, blocking);
         ((IterableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.listeners()).asScala()).foreach(new Serializable(this, rddId) {
            public static final long serialVersionUID = 0L;
            private final int rddId$1;

            public final void apply(CleanerListener x$2) {
               x$2.rddCleaned(this.rddId$1);
            }

            public {
               this.rddId$1 = rddId$1;
            }
         });
         this.logInfo(new Serializable(this, rddId) {
            public static final long serialVersionUID = 0L;
            private final int rddId$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Cleaned RDD ").append((Object)BoxesRunTime.boxToInteger(this.rddId$1)).toString();
            }

            public {
               this.rddId$1 = rddId$1;
            }
         });
      } catch (Exception var4) {
         this.logError(new Serializable(this, rddId) {
            public static final long serialVersionUID = 0L;
            private final int rddId$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Error cleaning RDD ").append((Object)BoxesRunTime.boxToInteger(this.rddId$1)).toString();
            }

            public {
               this.rddId$1 = rddId$1;
            }
         }, var4);
      }

   }

   public void doCleanupShuffle(int shuffleId, boolean blocking) {
      try {
         this.logDebug(new Serializable(this, shuffleId) {
            public static final long serialVersionUID = 0L;
            private final int shuffleId$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Cleaning shuffle ").append((Object)BoxesRunTime.boxToInteger(this.shuffleId$1)).toString();
            }

            public {
               this.shuffleId$1 = shuffleId$1;
            }
         });
         this.mapOutputTrackerMaster().unregisterShuffle(shuffleId);
         this.blockManagerMaster().removeShuffle(shuffleId, blocking);
         ((IterableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.listeners()).asScala()).foreach(new Serializable(this, shuffleId) {
            public static final long serialVersionUID = 0L;
            private final int shuffleId$1;

            public final void apply(CleanerListener x$3) {
               x$3.shuffleCleaned(this.shuffleId$1);
            }

            public {
               this.shuffleId$1 = shuffleId$1;
            }
         });
         this.logInfo(new Serializable(this, shuffleId) {
            public static final long serialVersionUID = 0L;
            private final int shuffleId$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Cleaned shuffle ").append((Object)BoxesRunTime.boxToInteger(this.shuffleId$1)).toString();
            }

            public {
               this.shuffleId$1 = shuffleId$1;
            }
         });
      } catch (Exception var4) {
         this.logError(new Serializable(this, shuffleId) {
            public static final long serialVersionUID = 0L;
            private final int shuffleId$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Error cleaning shuffle ").append((Object)BoxesRunTime.boxToInteger(this.shuffleId$1)).toString();
            }

            public {
               this.shuffleId$1 = shuffleId$1;
            }
         }, var4);
      }

   }

   public void doCleanupBroadcast(long broadcastId, boolean blocking) {
      try {
         this.logDebug(new Serializable(this, broadcastId) {
            public static final long serialVersionUID = 0L;
            private final long broadcastId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cleaning broadcast ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.broadcastId$1)}));
            }

            public {
               this.broadcastId$1 = broadcastId$1;
            }
         });
         this.broadcastManager().unbroadcast(broadcastId, true, blocking);
         ((IterableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.listeners()).asScala()).foreach(new Serializable(this, broadcastId) {
            public static final long serialVersionUID = 0L;
            private final long broadcastId$1;

            public final void apply(CleanerListener x$4) {
               x$4.broadcastCleaned(this.broadcastId$1);
            }

            public {
               this.broadcastId$1 = broadcastId$1;
            }
         });
         this.logDebug(new Serializable(this, broadcastId) {
            public static final long serialVersionUID = 0L;
            private final long broadcastId$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Cleaned broadcast ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToLong(this.broadcastId$1)}));
            }

            public {
               this.broadcastId$1 = broadcastId$1;
            }
         });
      } catch (Exception var5) {
         this.logError(new Serializable(this, broadcastId) {
            public static final long serialVersionUID = 0L;
            private final long broadcastId$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Error cleaning broadcast ").append((Object)BoxesRunTime.boxToLong(this.broadcastId$1)).toString();
            }

            public {
               this.broadcastId$1 = broadcastId$1;
            }
         }, var5);
      }

   }

   public void doCleanupAccum(long accId, boolean blocking) {
      try {
         this.logDebug(new Serializable(this, accId) {
            public static final long serialVersionUID = 0L;
            private final long accId$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Cleaning accumulator ").append((Object)BoxesRunTime.boxToLong(this.accId$1)).toString();
            }

            public {
               this.accId$1 = accId$1;
            }
         });
         AccumulatorContext$.MODULE$.remove(accId);
         ((IterableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.listeners()).asScala()).foreach(new Serializable(this, accId) {
            public static final long serialVersionUID = 0L;
            private final long accId$1;

            public final void apply(CleanerListener x$5) {
               x$5.accumCleaned(this.accId$1);
            }

            public {
               this.accId$1 = accId$1;
            }
         });
         this.logInfo(new Serializable(this, accId) {
            public static final long serialVersionUID = 0L;
            private final long accId$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Cleaned accumulator ").append((Object)BoxesRunTime.boxToLong(this.accId$1)).toString();
            }

            public {
               this.accId$1 = accId$1;
            }
         });
      } catch (Exception var5) {
         this.logError(new Serializable(this, accId) {
            public static final long serialVersionUID = 0L;
            private final long accId$1;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Error cleaning accumulator ").append((Object)BoxesRunTime.boxToLong(this.accId$1)).toString();
            }

            public {
               this.accId$1 = accId$1;
            }
         }, var5);
      }

   }

   public void doCleanCheckpoint(int rddId) {
      try {
         this.logDebug(new Serializable(this, rddId) {
            public static final long serialVersionUID = 0L;
            private final int rddId$2;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Cleaning rdd checkpoint data ").append((Object)BoxesRunTime.boxToInteger(this.rddId$2)).toString();
            }

            public {
               this.rddId$2 = rddId$2;
            }
         });
         ReliableRDDCheckpointData$.MODULE$.cleanCheckpoint(this.sc, rddId);
         ((IterableLike)JavaConverters$.MODULE$.collectionAsScalaIterableConverter(this.listeners()).asScala()).foreach(new Serializable(this, rddId) {
            public static final long serialVersionUID = 0L;
            private final int rddId$2;

            public final void apply(CleanerListener x$6) {
               x$6.checkpointCleaned((long)this.rddId$2);
            }

            public {
               this.rddId$2 = rddId$2;
            }
         });
         this.logInfo(new Serializable(this, rddId) {
            public static final long serialVersionUID = 0L;
            private final int rddId$2;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Cleaned rdd checkpoint data ").append((Object)BoxesRunTime.boxToInteger(this.rddId$2)).toString();
            }

            public {
               this.rddId$2 = rddId$2;
            }
         });
      } catch (Exception var3) {
         this.logError(new Serializable(this, rddId) {
            public static final long serialVersionUID = 0L;
            private final int rddId$2;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Error cleaning rdd checkpoint data ").append((Object)BoxesRunTime.boxToInteger(this.rddId$2)).toString();
            }

            public {
               this.rddId$2 = rddId$2;
            }
         }, var3);
      }

   }

   private BlockManagerMaster blockManagerMaster() {
      return this.sc.env().blockManager().master();
   }

   private BroadcastManager broadcastManager() {
      return this.sc.env().broadcastManager();
   }

   private MapOutputTrackerMaster mapOutputTrackerMaster() {
      return (MapOutputTrackerMaster)this.sc.env().mapOutputTracker();
   }

   public ContextCleaner(SparkContext sc) {
      this.sc = sc;
      Logging$class.$init$(this);
      this.org$apache$spark$ContextCleaner$$referenceBuffer = Collections.newSetFromMap(new ConcurrentHashMap());
      this.org$apache$spark$ContextCleaner$$referenceQueue = new ReferenceQueue();
      this.listeners = new ConcurrentLinkedQueue();
      this.cleaningThread = new Thread(this) {
         // $FF: synthetic field
         private final ContextCleaner $outer;

         public void run() {
            this.$outer.org$apache$spark$ContextCleaner$$keepCleaning();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
      this.periodicGCService = ThreadUtils$.MODULE$.newDaemonSingleThreadScheduledExecutor("context-cleaner-periodic-gc");
      this.periodicGCInterval = sc.conf().getTimeAsSeconds("spark.cleaner.periodicGC.interval", "30min");
      this.org$apache$spark$ContextCleaner$$blockOnCleanupTasks = sc.conf().getBoolean("spark.cleaner.referenceTracking.blocking", true);
      this.org$apache$spark$ContextCleaner$$blockOnShuffleCleanupTasks = sc.conf().getBoolean("spark.cleaner.referenceTracking.blocking.shuffle", false);
      this.org$apache$spark$ContextCleaner$$stopped = false;
   }
}
