package org.apache.spark.deploy.worker;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.spark.SecurityManager;
import org.apache.spark.SecurityManager$;
import org.apache.spark.SparkConf;
import org.apache.spark.deploy.ApplicationDescription;
import org.apache.spark.deploy.DeployMessages;
import org.apache.spark.deploy.ExecutorState$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.util.ShutdownHookManager$;
import org.apache.spark.util.Utils$;
import org.apache.spark.util.logging.FileAppender;
import org.apache.spark.util.logging.FileAppender$;
import org.slf4j.Logger;
import org.spark_project.guava.io.Files;
import scala.Enumeration;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.TraversableOnce;
import scala.collection.immutable.StringOps;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;

@ScalaSignature(
   bytes = "\u0006\u0001\t}a!B\u0001\u0003\u0001\u0011a!AD#yK\u000e,Ho\u001c:Sk:tWM\u001d\u0006\u0003\u0007\u0011\taa^8sW\u0016\u0014(BA\u0003\u0007\u0003\u0019!W\r\u001d7ps*\u0011q\u0001C\u0001\u0006gB\f'o\u001b\u0006\u0003\u0013)\ta!\u00199bG\",'\"A\u0006\u0002\u0007=\u0014xmE\u0002\u0001\u001bM\u0001\"AD\t\u000e\u0003=Q\u0011\u0001E\u0001\u0006g\u000e\fG.Y\u0005\u0003%=\u0011a!\u00118z%\u00164\u0007C\u0001\u000b\u0018\u001b\u0005)\"B\u0001\f\u0007\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\r\u0016\u0005\u001daunZ4j]\u001eD\u0001B\u0007\u0001\u0003\u0006\u0004%\t\u0001H\u0001\u0006CB\u0004\u0018\nZ\u0002\u0001+\u0005i\u0002C\u0001\u0010\"\u001d\tqq$\u0003\u0002!\u001f\u00051\u0001K]3eK\u001aL!AI\u0012\u0003\rM#(/\u001b8h\u0015\t\u0001s\u0002\u0003\u0005&\u0001\t\u0005\t\u0015!\u0003\u001e\u0003\u0019\t\u0007\u000f]%eA!Aq\u0005\u0001BC\u0002\u0013\u0005\u0001&\u0001\u0004fq\u0016\u001c\u0017\nZ\u000b\u0002SA\u0011aBK\u0005\u0003W=\u00111!\u00138u\u0011!i\u0003A!A!\u0002\u0013I\u0013aB3yK\u000eLE\r\t\u0005\t_\u0001\u0011)\u0019!C\u0001a\u00059\u0011\r\u001d9EKN\u001cW#A\u0019\u0011\u0005I\u001aT\"\u0001\u0003\n\u0005Q\"!AF!qa2L7-\u0019;j_:$Um]2sSB$\u0018n\u001c8\t\u0011Y\u0002!\u0011!Q\u0001\nE\n\u0001\"\u00199q\t\u0016\u001c8\r\t\u0005\tq\u0001\u0011)\u0019!C\u0001Q\u0005)1m\u001c:fg\"A!\b\u0001B\u0001B\u0003%\u0011&\u0001\u0004d_J,7\u000f\t\u0005\ty\u0001\u0011)\u0019!C\u0001Q\u00051Q.Z7pefD\u0001B\u0010\u0001\u0003\u0002\u0003\u0006I!K\u0001\b[\u0016lwN]=!\u0011!\u0019\u0001A!b\u0001\n\u0003\u0001U#A!\u0011\u0005\t+U\"A\"\u000b\u0005\u00113\u0011a\u0001:qG&\u0011ai\u0011\u0002\u000f%B\u001cWI\u001c3q_&tGOU3g\u0011!A\u0005A!A!\u0002\u0013\t\u0015aB<pe.,'\u000f\t\u0005\t\u0015\u0002\u0011)\u0019!C\u00019\u0005Aqo\u001c:lKJLE\r\u0003\u0005M\u0001\t\u0005\t\u0015!\u0003\u001e\u0003%9xN]6fe&#\u0007\u0005\u0003\u0005O\u0001\t\u0015\r\u0011\"\u0001\u001d\u0003\u0011Awn\u001d;\t\u0011A\u0003!\u0011!Q\u0001\nu\tQ\u0001[8ti\u0002B\u0001B\u0015\u0001\u0003\u0006\u0004%\t\u0001K\u0001\no\u0016\u0014W+\u001b)peRD\u0001\u0002\u0016\u0001\u0003\u0002\u0003\u0006I!K\u0001\u000bo\u0016\u0014W+\u001b)peR\u0004\u0003\u0002\u0003,\u0001\u0005\u000b\u0007I\u0011\u0001\u000f\u0002\u001bA,(\r\\5d\u0003\u0012$'/Z:t\u0011!A\u0006A!A!\u0002\u0013i\u0012A\u00049vE2L7-\u00113ee\u0016\u001c8\u000f\t\u0005\t5\u0002\u0011)\u0019!C\u00017\u0006I1\u000f]1sW\"{W.Z\u000b\u00029B\u0011QLY\u0007\u0002=*\u0011q\fY\u0001\u0003S>T\u0011!Y\u0001\u0005U\u00064\u0018-\u0003\u0002d=\n!a)\u001b7f\u0011!)\u0007A!A!\u0002\u0013a\u0016AC:qCJ\\\u0007j\\7fA!Aq\r\u0001BC\u0002\u0013\u00051,A\u0006fq\u0016\u001cW\u000f^8s\t&\u0014\b\u0002C5\u0001\u0005\u0003\u0005\u000b\u0011\u0002/\u0002\u0019\u0015DXmY;u_J$\u0015N\u001d\u0011\t\u0011-\u0004!Q1A\u0005\u0002q\t\u0011b^8sW\u0016\u0014XK\u001d7\t\u00115\u0004!\u0011!Q\u0001\nu\t!b^8sW\u0016\u0014XK\u001d7!\u0011!y\u0007A!A!\u0002\u0013\u0001\u0018\u0001B2p]\u001a\u0004\"!\u001d:\u000e\u0003\u0019I!a\u001d\u0004\u0003\u0013M\u0003\u0018M]6D_:4\u0007\u0002C;\u0001\u0005\u000b\u0007I\u0011\u0001<\u0002\u0019\u0005\u0004\b\u000fT8dC2$\u0015N]:\u0016\u0003]\u0004B\u0001_A\u0001;9\u0011\u0011P \b\u0003uvl\u0011a\u001f\u0006\u0003yn\ta\u0001\u0010:p_Rt\u0014\"\u0001\t\n\u0005}|\u0011a\u00029bG.\fw-Z\u0005\u0005\u0003\u0007\t)AA\u0002TKFT!a`\b\t\u0013\u0005%\u0001A!A!\u0002\u00139\u0018!D1qa2{7-\u00197ESJ\u001c\b\u0005\u0003\u0006\u0002\u000e\u0001\u0011\t\u0019!C\u0001\u0003\u001f\tQa\u001d;bi\u0016,\"!!\u0005\u0011\t\u0005M\u0011\u0011\u0004\b\u0004e\u0005U\u0011bAA\f\t\u0005iQ\t_3dkR|'o\u0015;bi\u0016LA!a\u0007\u0002\u001e\t)a+\u00197vK&\u0019\u0011qD\b\u0003\u0017\u0015sW/\\3sCRLwN\u001c\u0005\u000b\u0003G\u0001!\u00111A\u0005\u0002\u0005\u0015\u0012!C:uCR,w\fJ3r)\u0011\t9#!\f\u0011\u00079\tI#C\u0002\u0002,=\u0011A!\u00168ji\"Q\u0011qFA\u0011\u0003\u0003\u0005\r!!\u0005\u0002\u0007a$\u0013\u0007\u0003\u0006\u00024\u0001\u0011\t\u0011)Q\u0005\u0003#\taa\u001d;bi\u0016\u0004\u0003\u0006BA\u0019\u0003o\u00012ADA\u001d\u0013\r\tYd\u0004\u0002\tm>d\u0017\r^5mK\"9\u0011q\b\u0001\u0005\u0002\u0005\u0005\u0013A\u0002\u001fj]&$h\b\u0006\u0012\u0002D\u0005\u001d\u0013\u0011JA&\u0003\u001b\ny%!\u0015\u0002T\u0005U\u0013qKA-\u00037\ni&a\u0018\u0002b\u0005\r\u0014Q\r\t\u0004\u0003\u000b\u0002Q\"\u0001\u0002\t\ri\ti\u00041\u0001\u001e\u0011\u00199\u0013Q\ba\u0001S!1q&!\u0010A\u0002EBa\u0001OA\u001f\u0001\u0004I\u0003B\u0002\u001f\u0002>\u0001\u0007\u0011\u0006\u0003\u0004\u0004\u0003{\u0001\r!\u0011\u0005\u0007\u0015\u0006u\u0002\u0019A\u000f\t\r9\u000bi\u00041\u0001\u001e\u0011\u0019\u0011\u0016Q\ba\u0001S!1a+!\u0010A\u0002uAaAWA\u001f\u0001\u0004a\u0006BB4\u0002>\u0001\u0007A\f\u0003\u0004l\u0003{\u0001\r!\b\u0005\u0007_\u0006u\u0002\u0019\u00019\t\rU\fi\u00041\u0001x\u0011!\ti!!\u0010A\u0002\u0005E\u0001\"CA5\u0001\t\u0007I\u0011BA6\u0003\u00191W\u000f\u001c7JIV\u0011\u0011Q\u000e\t\u0005\u0003_\n)(\u0004\u0002\u0002r)\u0019\u00111\u000f1\u0002\t1\fgnZ\u0005\u0004E\u0005E\u0004\u0002CA=\u0001\u0001\u0006I!!\u001c\u0002\u000f\u0019,H\u000e\\%eA!I\u0011Q\u0010\u0001A\u0002\u0013%\u0011qP\u0001\ro>\u00148.\u001a:UQJ,\u0017\rZ\u000b\u0003\u0003\u0003\u0003B!a\u001c\u0002\u0004&!\u0011QQA9\u0005\u0019!\u0006N]3bI\"I\u0011\u0011\u0012\u0001A\u0002\u0013%\u00111R\u0001\u0011o>\u00148.\u001a:UQJ,\u0017\rZ0%KF$B!a\n\u0002\u000e\"Q\u0011qFAD\u0003\u0003\u0005\r!!!\t\u0011\u0005E\u0005\u0001)Q\u0005\u0003\u0003\u000bQb^8sW\u0016\u0014H\u000b\u001b:fC\u0012\u0004\u0003\"CAK\u0001\u0001\u0007I\u0011BAL\u0003\u001d\u0001(o\\2fgN,\"!!'\u0011\t\u0005=\u00141T\u0005\u0005\u0003;\u000b\tHA\u0004Qe>\u001cWm]:\t\u0013\u0005\u0005\u0006\u00011A\u0005\n\u0005\r\u0016a\u00039s_\u000e,7o]0%KF$B!a\n\u0002&\"Q\u0011qFAP\u0003\u0003\u0005\r!!'\t\u0011\u0005%\u0006\u0001)Q\u0005\u00033\u000b\u0001\u0002\u001d:pG\u0016\u001c8\u000f\t\u0005\n\u0003[\u0003\u0001\u0019!C\u0005\u0003_\u000bab\u001d;e_V$\u0018\t\u001d9f]\u0012,'/\u0006\u0002\u00022B!\u00111WA_\u001b\t\t)L\u0003\u0003\u00028\u0006e\u0016a\u00027pO\u001eLgn\u001a\u0006\u0004\u0003w3\u0011\u0001B;uS2LA!a0\u00026\naa)\u001b7f\u0003B\u0004XM\u001c3fe\"I\u00111\u0019\u0001A\u0002\u0013%\u0011QY\u0001\u0013gR$w.\u001e;BaB,g\u000eZ3s?\u0012*\u0017\u000f\u0006\u0003\u0002(\u0005\u001d\u0007BCA\u0018\u0003\u0003\f\t\u00111\u0001\u00022\"A\u00111\u001a\u0001!B\u0013\t\t,A\bti\u0012|W\u000f^!qa\u0016tG-\u001a:!\u0011%\ty\r\u0001a\u0001\n\u0013\ty+\u0001\bti\u0012,'O]!qa\u0016tG-\u001a:\t\u0013\u0005M\u0007\u00011A\u0005\n\u0005U\u0017AE:uI\u0016\u0014(/\u00119qK:$WM]0%KF$B!a\n\u0002X\"Q\u0011qFAi\u0003\u0003\u0005\r!!-\t\u0011\u0005m\u0007\u0001)Q\u0005\u0003c\u000bqb\u001d;eKJ\u0014\u0018\t\u001d9f]\u0012,'\u000f\t\u0005\t\u0003?\u0004!\u0019!C\u0005Q\u0005iR\tW#D+R{%k\u0018+F%6Ke*\u0011+F?RKU*R(V)~k5\u000bC\u0004\u0002d\u0002\u0001\u000b\u0011B\u0015\u0002=\u0015CViQ+U\u001fJ{F+\u0012*N\u0013:\u000bE+R0U\u00136+u*\u0016+`\u001bN\u0003\u0003\"CAt\u0001\u0001\u0007I\u0011BAu\u00031\u0019\b.\u001e;e_^t\u0007j\\8l+\u0005i\u0001\"CAw\u0001\u0001\u0007I\u0011BAx\u0003A\u0019\b.\u001e;e_^t\u0007j\\8l?\u0012*\u0017\u000f\u0006\u0003\u0002(\u0005E\b\"CA\u0018\u0003W\f\t\u00111\u0001\u000e\u0011\u001d\t)\u0010\u0001Q!\n5\tQb\u001d5vi\u0012|wO\u001c%p_.\u0004\u0003\u0002CA}\u0001\u0011\u0005!!a?\u0002\u000bM$\u0018M\u001d;\u0015\u0005\u0005\u001d\u0002bBA\u0000\u0001\u0011%!\u0011A\u0001\fW&dG\u000e\u0015:pG\u0016\u001c8\u000f\u0006\u0003\u0002(\t\r\u0001\u0002\u0003B\u0003\u0003{\u0004\rAa\u0002\u0002\u000f5,7o]1hKB!aB!\u0003\u001e\u0013\r\u0011Ya\u0004\u0002\u0007\u001fB$\u0018n\u001c8\t\u0011\t=\u0001\u0001\"\u0001\u0003\u0003w\fAa[5mY\"A!1\u0003\u0001\u0005\u0002\t\u0011)\"A\ntk\n\u001cH/\u001b;vi\u00164\u0016M]5bE2,7\u000fF\u0002\u001e\u0005/AqA!\u0007\u0003\u0012\u0001\u0007Q$\u0001\u0005be\u001e,X.\u001a8u\u0011\u001d\u0011i\u0002\u0001C\u0005\u0003w\f1CZ3uG\"\fe\u000e\u001a*v]\u0016CXmY;u_J\u0004"
)
public class ExecutorRunner implements Logging {
   private final String appId;
   private final int execId;
   private final ApplicationDescription appDesc;
   private final int cores;
   private final int memory;
   private final RpcEndpointRef worker;
   private final String workerId;
   private final String host;
   private final int webUiPort;
   private final String publicAddress;
   private final File sparkHome;
   private final File executorDir;
   private final String workerUrl;
   private final SparkConf conf;
   private final Seq<String> appLocalDirs;
   private volatile Enumeration.Value state;
   private final String org$apache$spark$deploy$worker$ExecutorRunner$$fullId;
   private Thread workerThread;
   private Process org$apache$spark$deploy$worker$ExecutorRunner$$process;
   private FileAppender stdoutAppender;
   private FileAppender stderrAppender;
   private final int EXECUTOR_TERMINATE_TIMEOUT_MS;
   private Object shutdownHook;
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

   public String appId() {
      return this.appId;
   }

   public int execId() {
      return this.execId;
   }

   public ApplicationDescription appDesc() {
      return this.appDesc;
   }

   public int cores() {
      return this.cores;
   }

   public int memory() {
      return this.memory;
   }

   public RpcEndpointRef worker() {
      return this.worker;
   }

   public String workerId() {
      return this.workerId;
   }

   public String host() {
      return this.host;
   }

   public int webUiPort() {
      return this.webUiPort;
   }

   public String publicAddress() {
      return this.publicAddress;
   }

   public File sparkHome() {
      return this.sparkHome;
   }

   public File executorDir() {
      return this.executorDir;
   }

   public String workerUrl() {
      return this.workerUrl;
   }

   public Seq<String> appLocalDirs() {
      return this.appLocalDirs;
   }

   public Enumeration.Value state() {
      return this.state;
   }

   public void state_$eq(Enumeration.Value x$1) {
      this.state = x$1;
   }

   public String org$apache$spark$deploy$worker$ExecutorRunner$$fullId() {
      return this.org$apache$spark$deploy$worker$ExecutorRunner$$fullId;
   }

   private Thread workerThread() {
      return this.workerThread;
   }

   private void workerThread_$eq(Thread x$1) {
      this.workerThread = x$1;
   }

   public Process org$apache$spark$deploy$worker$ExecutorRunner$$process() {
      return this.org$apache$spark$deploy$worker$ExecutorRunner$$process;
   }

   private void org$apache$spark$deploy$worker$ExecutorRunner$$process_$eq(Process x$1) {
      this.org$apache$spark$deploy$worker$ExecutorRunner$$process = x$1;
   }

   private FileAppender stdoutAppender() {
      return this.stdoutAppender;
   }

   private void stdoutAppender_$eq(FileAppender x$1) {
      this.stdoutAppender = x$1;
   }

   private FileAppender stderrAppender() {
      return this.stderrAppender;
   }

   private void stderrAppender_$eq(FileAppender x$1) {
      this.stderrAppender = x$1;
   }

   private int EXECUTOR_TERMINATE_TIMEOUT_MS() {
      return this.EXECUTOR_TERMINATE_TIMEOUT_MS;
   }

   private Object shutdownHook() {
      return this.shutdownHook;
   }

   private void shutdownHook_$eq(Object x$1) {
      this.shutdownHook = x$1;
   }

   public void start() {
      this.workerThread_$eq(new Thread(this) {
         // $FF: synthetic field
         private final ExecutorRunner $outer;

         public void run() {
            this.$outer.org$apache$spark$deploy$worker$ExecutorRunner$$fetchAndRunExecutor();
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      });
      this.workerThread().start();
      this.shutdownHook_$eq(ShutdownHookManager$.MODULE$.addShutdownHook(new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final ExecutorRunner $outer;

         public final void apply() {
            this.apply$mcV$sp();
         }

         public void apply$mcV$sp() {
            label14: {
               Enumeration.Value var10000 = this.$outer.state();
               Enumeration.Value var1 = ExecutorState$.MODULE$.RUNNING();
               if (var10000 == null) {
                  if (var1 != null) {
                     break label14;
                  }
               } else if (!var10000.equals(var1)) {
                  break label14;
               }

               this.$outer.state_$eq(ExecutorState$.MODULE$.FAILED());
            }

            this.$outer.org$apache$spark$deploy$worker$ExecutorRunner$$killProcess(new Some("Worker shutting down"));
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      }));
   }

   public void org$apache$spark$deploy$worker$ExecutorRunner$$killProcess(Option<String> message) {
      Option exitCode = None$.MODULE$;
      if (this.org$apache$spark$deploy$worker$ExecutorRunner$$process() != null) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Killing process!";
            }
         });
         if (this.stdoutAppender() != null) {
            this.stdoutAppender().stop();
         }

         if (this.stderrAppender() != null) {
            this.stderrAppender().stop();
         }

         exitCode = Utils$.MODULE$.terminateProcess(this.org$apache$spark$deploy$worker$ExecutorRunner$$process(), (long)this.EXECUTOR_TERMINATE_TIMEOUT_MS());
         if (exitCode.isEmpty()) {
            this.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final ExecutorRunner $outer;

               public final String apply() {
                  return (new StringBuilder()).append((Object)"Failed to terminate process: ").append((Object)this.$outer.org$apache$spark$deploy$worker$ExecutorRunner$$process()).append((Object)". This process will likely be orphaned.").toString();
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
      }

      try {
         this.worker().send(new DeployMessages.ExecutorStateChanged(this.appId(), this.execId(), this.state(), message, exitCode));
      } catch (IllegalStateException var4) {
         this.logWarning(new Serializable(this, var4) {
            public static final long serialVersionUID = 0L;
            private final IllegalStateException e$1;

            public final String apply() {
               return this.e$1.getMessage();
            }

            public {
               this.e$1 = e$1;
            }
         }, var4);
      }

   }

   public void kill() {
      if (this.workerThread() != null) {
         this.workerThread().interrupt();
         this.workerThread_$eq((Thread)null);
         this.state_$eq(ExecutorState$.MODULE$.KILLED());

         try {
            ShutdownHookManager$.MODULE$.removeShutdownHook(this.shutdownHook());
         } catch (IllegalStateException var2) {
            None$ var10000 = None$.MODULE$;
         }
      }

   }

   public String substituteVariables(String argument) {
      String var3;
      if ("{{WORKER_URL}}".equals(argument)) {
         var3 = this.workerUrl();
      } else if ("{{EXECUTOR_ID}}".equals(argument)) {
         var3 = BoxesRunTime.boxToInteger(this.execId()).toString();
      } else if ("{{HOSTNAME}}".equals(argument)) {
         var3 = this.host();
      } else if ("{{CORES}}".equals(argument)) {
         var3 = BoxesRunTime.boxToInteger(this.cores()).toString();
      } else if ("{{APP_ID}}".equals(argument)) {
         var3 = this.appId();
      } else {
         var3 = argument;
      }

      return var3;
   }

   public void org$apache$spark$deploy$worker$ExecutorRunner$$fetchAndRunExecutor() {
      try {
         ProcessBuilder builder = CommandUtils$.MODULE$.buildProcessBuilder(this.appDesc().command(), new SecurityManager(this.conf, SecurityManager$.MODULE$.$lessinit$greater$default$2()), this.memory(), this.sparkHome().getAbsolutePath(), new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorRunner $outer;

            public final String apply(String argument) {
               return this.$outer.substituteVariables(argument);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         }, CommandUtils$.MODULE$.buildProcessBuilder$default$6(), CommandUtils$.MODULE$.buildProcessBuilder$default$7());
         List command = builder.command();
         String formattedCommand = ((TraversableOnce)JavaConverters$.MODULE$.asScalaBufferConverter(command).asScala()).mkString("\"", "\" \"", "\"");
         this.logInfo(new Serializable(this, formattedCommand) {
            public static final long serialVersionUID = 0L;
            private final String formattedCommand$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Launch command: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.formattedCommand$1}));
            }

            public {
               this.formattedCommand$1 = formattedCommand$1;
            }
         });
         builder.directory(this.executorDir());
         builder.environment().put("SPARK_EXECUTOR_DIRS", this.appLocalDirs().mkString(File.pathSeparator));
         builder.environment().put("SPARK_LAUNCH_WITH_SCALA", "0");
         String baseUrl = this.conf.getBoolean("spark.ui.reverseProxy", false) ? (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"/proxy/", "/logPage/?appId=", "&executorId=", "&logType="})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.workerId(), this.appId(), BoxesRunTime.boxToInteger(this.execId())})) : (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"http://", ":", "/logPage/?appId=", "&executorId=", "&logType="})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.publicAddress(), BoxesRunTime.boxToInteger(this.webUiPort()), this.appId(), BoxesRunTime.boxToInteger(this.execId())}));
         builder.environment().put("SPARK_LOG_URL_STDERR", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "stderr"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{baseUrl})));
         builder.environment().put("SPARK_LOG_URL_STDOUT", (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "stdout"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{baseUrl})));
         this.org$apache$spark$deploy$worker$ExecutorRunner$$process_$eq(builder.start());
         String header = (new StringOps(Predef$.MODULE$.augmentString("Spark Executor Command: %s\n%s\n\n"))).format(Predef$.MODULE$.genericWrapArray(new Object[]{formattedCommand, (new StringOps(Predef$.MODULE$.augmentString("="))).$times(40)}));
         File stdout = new File(this.executorDir(), "stdout");
         this.stdoutAppender_$eq(FileAppender$.MODULE$.apply(this.org$apache$spark$deploy$worker$ExecutorRunner$$process().getInputStream(), stdout, this.conf));
         File stderr = new File(this.executorDir(), "stderr");
         Files.write(header, stderr, StandardCharsets.UTF_8);
         this.stderrAppender_$eq(FileAppender$.MODULE$.apply(this.org$apache$spark$deploy$worker$ExecutorRunner$$process().getErrorStream(), stderr, this.conf));
         int exitCode = this.org$apache$spark$deploy$worker$ExecutorRunner$$process().waitFor();
         this.state_$eq(ExecutorState$.MODULE$.EXITED());
         String message = (new StringBuilder()).append((Object)"Command exited with code ").append((Object)BoxesRunTime.boxToInteger(exitCode)).toString();
         this.worker().send(new DeployMessages.ExecutorStateChanged(this.appId(), this.execId(), this.state(), new Some(message), new Some(BoxesRunTime.boxToInteger(exitCode))));
      } catch (InterruptedException var12) {
         this.logInfo(new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final ExecutorRunner $outer;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Runner thread for executor ").append((Object)this.$outer.org$apache$spark$deploy$worker$ExecutorRunner$$fullId()).append((Object)" interrupted").toString();
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         });
         this.state_$eq(ExecutorState$.MODULE$.KILLED());
         this.org$apache$spark$deploy$worker$ExecutorRunner$$killProcess(None$.MODULE$);
      } catch (Exception var13) {
         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Error running executor";
            }
         }, var13);
         this.state_$eq(ExecutorState$.MODULE$.FAILED());
         this.org$apache$spark$deploy$worker$ExecutorRunner$$killProcess(new Some(var13.toString()));
      }

   }

   public ExecutorRunner(String appId, int execId, ApplicationDescription appDesc, int cores, int memory, RpcEndpointRef worker, String workerId, String host, int webUiPort, String publicAddress, File sparkHome, File executorDir, String workerUrl, SparkConf conf, Seq<String> appLocalDirs, Enumeration.Value state) {
      this.appId = appId;
      this.execId = execId;
      this.appDesc = appDesc;
      this.cores = cores;
      this.memory = memory;
      this.worker = worker;
      this.workerId = workerId;
      this.host = host;
      this.webUiPort = webUiPort;
      this.publicAddress = publicAddress;
      this.sparkHome = sparkHome;
      this.executorDir = executorDir;
      this.workerUrl = workerUrl;
      this.conf = conf;
      this.appLocalDirs = appLocalDirs;
      this.state = state;
      super();
      Logging$class.$init$(this);
      this.org$apache$spark$deploy$worker$ExecutorRunner$$fullId = (new StringBuilder()).append((Object)appId).append((Object)"/").append((Object)BoxesRunTime.boxToInteger(execId)).toString();
      this.workerThread = null;
      this.org$apache$spark$deploy$worker$ExecutorRunner$$process = null;
      this.stdoutAppender = null;
      this.stderrAppender = null;
      this.EXECUTOR_TERMINATE_TIMEOUT_MS = 10000;
      this.shutdownHook = null;
   }
}
