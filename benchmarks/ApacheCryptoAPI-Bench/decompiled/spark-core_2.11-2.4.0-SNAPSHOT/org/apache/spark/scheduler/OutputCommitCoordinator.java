package org.apache.spark.scheduler;

import org.apache.spark.SparkConf;
import org.apache.spark.Success$;
import org.apache.spark.TaskCommitDenied;
import org.apache.spark.TaskEndReason;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rpc.RpcAddress;
import org.apache.spark.rpc.RpcCallContext;
import org.apache.spark.rpc.RpcEndpoint;
import org.apache.spark.rpc.RpcEndpoint$class;
import org.apache.spark.rpc.RpcEndpointRef;
import org.apache.spark.rpc.RpcEnv;
import org.apache.spark.util.RpcUtils$;
import org.apache.spark.util.ThreadUtils$;
import org.slf4j.Logger;
import scala.Array$;
import scala.Function0;
import scala.Function1;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.PartialFunction;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.Iterator;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.Map;
import scala.collection.mutable.Map$;
import scala.collection.mutable.Set;
import scala.collection.mutable.Set$;
import scala.collection.mutable.SetLike;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ClassTag$;
import scala.reflect.ScalaSignature;
import scala.runtime.AbstractFunction1;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.NonLocalReturnControl;
import scala.runtime.NonLocalReturnControl$mcV$sp;
import scala.runtime.Nothing$;
import scala.runtime.ScalaRunTime$;
import scala.runtime.Statics;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\t\rd!B\u0001\u0003\u0001\u0011Q!aF(viB,HoQ8n[&$8i\\8sI&t\u0017\r^8s\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xmE\u0002\u0001\u0017E\u0001\"\u0001D\b\u000e\u00035Q\u0011AD\u0001\u0006g\u000e\fG.Y\u0005\u0003!5\u0011a!\u00118z%\u00164\u0007C\u0001\n\u0016\u001b\u0005\u0019\"B\u0001\u000b\u0005\u0003!Ig\u000e^3s]\u0006d\u0017B\u0001\f\u0014\u0005\u001daunZ4j]\u001eD\u0001\u0002\u0007\u0001\u0003\u0002\u0003\u0006IAG\u0001\u0005G>tgm\u0001\u0001\u0011\u0005maR\"\u0001\u0003\n\u0005u!!!C*qCJ\\7i\u001c8g\u0011!y\u0002A!A!\u0002\u0013\u0001\u0013\u0001C5t\tJLg/\u001a:\u0011\u00051\t\u0013B\u0001\u0012\u000e\u0005\u001d\u0011un\u001c7fC:DQ\u0001\n\u0001\u0005\u0002\u0015\na\u0001P5oSRtDc\u0001\u0014)SA\u0011q\u0005A\u0007\u0002\u0005!)\u0001d\ta\u00015!)qd\ta\u0001A!91\u0006\u0001a\u0001\n\u0003a\u0013AD2p_J$\u0017N\\1u_J\u0014VMZ\u000b\u0002[A\u0019AB\f\u0019\n\u0005=j!AB(qi&|g\u000e\u0005\u00022i5\t!G\u0003\u00024\t\u0005\u0019!\u000f]2\n\u0005U\u0012$A\u0004*qG\u0016sG\r]8j]R\u0014VM\u001a\u0005\bo\u0001\u0001\r\u0011\"\u00019\u0003I\u0019wn\u001c:eS:\fGo\u001c:SK\u001a|F%Z9\u0015\u0005eb\u0004C\u0001\u0007;\u0013\tYTB\u0001\u0003V]&$\bbB\u001f7\u0003\u0003\u0005\r!L\u0001\u0004q\u0012\n\u0004BB \u0001A\u0003&Q&A\bd_>\u0014H-\u001b8bi>\u0014(+\u001a4!\u000b\u0011\t\u0005\u0001\u0002\"\u0003\u000fM#\u0018mZ3JIB\u0011AbQ\u0005\u0003\t6\u00111!\u00138u\u000b\u00111\u0005\u0001\u0002\"\u0003\u0017A\u000b'\u000f^5uS>t\u0017\nZ\u0003\u0005\u0011\u0002!!IA\tUCN\\\u0017\t\u001e;f[B$h*^7cKJDqA\u0013\u0001C\u0002\u0013%1*A\fO\u001f~\u000bU\u000b\u0016%P%&SV\tR0D\u001f6k\u0015\n\u0016+F%V\tA\n\u0005\u0002N\u000f6\t\u0001\u0001\u0003\u0004P\u0001\u0001\u0006I\u0001T\u0001\u0019\u001d>{\u0016)\u0016+I\u001fJK%,\u0012#`\u0007>kU*\u0013+U\u000bJ\u0003c\u0001B)\u0001\tJ\u0013!b\u0015;bO\u0016\u001cF/\u0019;f'\u0011\u00016b\u0015,\u0011\u00051!\u0016BA+\u000e\u0005\u001d\u0001&o\u001c3vGR\u0004\"\u0001D,\n\u0005ak!\u0001D*fe&\fG.\u001b>bE2,\u0007\u0002\u0003.Q\u0005+\u0007I\u0011A.\u0002\u001b9,X\u000eU1si&$\u0018n\u001c8t+\u0005\u0011\u0005\u0002C/Q\u0005#\u0005\u000b\u0011\u0002\"\u0002\u001d9,X\u000eU1si&$\u0018n\u001c8tA!)A\u0005\u0015C\u0001?R\u0011\u0001-\u0019\t\u0003\u001bBCQA\u00170A\u0002\tCqa\u0019)C\u0002\u0013\u0005A-\u0001\u000bbkRDwN]5{K\u0012\u001cu.\\7jiR,'o]\u000b\u0002KB\u0019AB\u001a'\n\u0005\u001dl!!B!se\u0006L\bBB5QA\u0003%Q-A\u000bbkRDwN]5{K\u0012\u001cu.\\7jiR,'o\u001d\u0011\t\u000f-\u0004&\u0019!C\u0001Y\u0006Aa-Y5mkJ,7/F\u0001n!\u0011q7OQ;\u000e\u0003=T!\u0001]9\u0002\u000f5,H/\u00192mK*\u0011!/D\u0001\u000bG>dG.Z2uS>t\u0017B\u0001;p\u0005\ri\u0015\r\u001d\t\u0004]Z\u0014\u0015BA<p\u0005\r\u0019V\r\u001e\u0005\u0007sB\u0003\u000b\u0011B7\u0002\u0013\u0019\f\u0017\u000e\\;sKN\u0004\u0003bB>Q\u0003\u0003%\t\u0001`\u0001\u0005G>\u0004\u0018\u0010\u0006\u0002a{\"9!L\u001fI\u0001\u0002\u0004\u0011\u0005\u0002C@Q#\u0003%\t!!\u0001\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%cU\u0011\u00111\u0001\u0016\u0004\u0005\u0006\u00151FAA\u0004!\u0011\tI!a\u0005\u000e\u0005\u0005-!\u0002BA\u0007\u0003\u001f\t\u0011\"\u001e8dQ\u0016\u001c7.\u001a3\u000b\u0007\u0005EQ\"\u0001\u0006b]:|G/\u0019;j_:LA!!\u0006\u0002\f\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u0013\u0005e\u0001+!A\u0005B\u0005m\u0011!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070\u0006\u0002\u0002\u001eA!\u0011qDA\u0015\u001b\t\t\tC\u0003\u0003\u0002$\u0005\u0015\u0012\u0001\u00027b]\u001eT!!a\n\u0002\t)\fg/Y\u0005\u0005\u0003W\t\tC\u0001\u0004TiJLgn\u001a\u0005\t\u0003_\u0001\u0016\u0011!C\u00017\u0006a\u0001O]8ek\u000e$\u0018I]5us\"I\u00111\u0007)\u0002\u0002\u0013\u0005\u0011QG\u0001\u000faJ|G-^2u\u000b2,W.\u001a8u)\u0011\t9$!\u0010\u0011\u00071\tI$C\u0002\u0002<5\u00111!\u00118z\u0011!i\u0014\u0011GA\u0001\u0002\u0004\u0011\u0005\"CA!!\u0006\u0005I\u0011IA\"\u0003=\u0001(o\u001c3vGRLE/\u001a:bi>\u0014XCAA#!\u0019\t9%!\u0013\u000285\t\u0011/C\u0002\u0002LE\u0014\u0001\"\u0013;fe\u0006$xN\u001d\u0005\n\u0003\u001f\u0002\u0016\u0011!C\u0001\u0003#\n\u0001bY1o\u000bF,\u0018\r\u001c\u000b\u0004A\u0005M\u0003\"C\u001f\u0002N\u0005\u0005\t\u0019AA\u001c\u0011%\t9\u0006UA\u0001\n\u0003\nI&\u0001\u0005iCND7i\u001c3f)\u0005\u0011\u0005\"CA/!\u0006\u0005I\u0011IA0\u0003!!xn\u0015;sS:<GCAA\u000f\u0011%\t\u0019\u0007UA\u0001\n\u0003\n)'\u0001\u0004fcV\fGn\u001d\u000b\u0004A\u0005\u001d\u0004\"C\u001f\u0002b\u0005\u0005\t\u0019AA\u001c\u000f%\tY\u0007AA\u0001\u0012\u0013\ti'\u0001\u0006Ti\u0006<Wm\u0015;bi\u0016\u00042!TA8\r!\t\u0006!!A\t\n\u0005E4#BA8\u0003g2\u0006CBA;\u0003w\u0012\u0005-\u0004\u0002\u0002x)\u0019\u0011\u0011P\u0007\u0002\u000fI,h\u000e^5nK&!\u0011QPA<\u0005E\t%m\u001d;sC\u000e$h)\u001e8di&|g.\r\u0005\bI\u0005=D\u0011AAA)\t\ti\u0007\u0003\u0006\u0002^\u0005=\u0014\u0011!C#\u0003?B!\"a\"\u0002p\u0005\u0005I\u0011QAE\u0003\u0015\t\u0007\u000f\u001d7z)\r\u0001\u00171\u0012\u0005\u00075\u0006\u0015\u0005\u0019\u0001\"\t\u0015\u0005=\u0015qNA\u0001\n\u0003\u000b\t*A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005M\u0015Q\u0013\t\u0004\u00199\u0012\u0005\"CAL\u0003\u001b\u000b\t\u00111\u0001a\u0003\rAH\u0005\r\u0005\n\u00037\u0003!\u0019!C\u0005\u0003;\u000b1b\u001d;bO\u0016\u001cF/\u0019;fgV\u0011\u0011q\u0014\t\u0005]N\u0014\u0005\r\u0003\u0005\u0002$\u0002\u0001\u000b\u0011BAP\u00031\u0019H/Y4f'R\fG/Z:!\u0011\u001d\t9\u000b\u0001C\u0001\u0003S\u000bq![:F[B$\u00180F\u0001!\u0011\u001d\ti\u000b\u0001C\u0001\u0003_\u000b\u0011bY1o\u0007>lW.\u001b;\u0015\u000f\u0001\n\t,a.\u0002>\"A\u00111WAV\u0001\u0004\t),A\u0003ti\u0006<W\r\u0005\u0002N\u0001\"A\u0011\u0011XAV\u0001\u0004\tY,A\u0005qCJ$\u0018\u000e^5p]B\u0011Q*\u0012\u0005\b\u0003\u007f\u000bY\u000b1\u0001M\u00035\tG\u000f^3naRtU/\u001c2fe\"A\u00111\u0019\u0001\u0005\u0002\t\t)-\u0001\u0006ti\u0006<Wm\u0015;beR$R!OAd\u0003\u0013D\u0001\"a-\u0002B\u0002\u0007\u0011Q\u0017\u0005\b\u0003\u0017\f\t\r1\u0001C\u00039i\u0017\r\u001f)beRLG/[8o\u0013\u0012D\u0001\"a4\u0001\t\u0003\u0011\u0011\u0011[\u0001\tgR\fw-Z#oIR\u0019\u0011(a5\t\u0011\u0005M\u0016Q\u001aa\u0001\u0003kC\u0001\"a6\u0001\t\u0003\u0011\u0011\u0011\\\u0001\u000ei\u0006\u001c8nQ8na2,G/\u001a3\u0015\u0013e\nY.!8\u0002`\u0006\u0005\b\u0002CAZ\u0003+\u0004\r!!.\t\u0011\u0005e\u0016Q\u001ba\u0001\u0003wCq!a0\u0002V\u0002\u0007A\n\u0003\u0005\u0002d\u0006U\u0007\u0019AAs\u0003\u0019\u0011X-Y:p]B\u00191$a:\n\u0007\u0005%HAA\u0007UCN\\WI\u001c3SK\u0006\u001cxN\u001c\u0005\b\u0003[\u0004A\u0011AAx\u0003\u0011\u0019Ho\u001c9\u0015\u0003eB\u0001\"a=\u0001\t\u0003\u0011\u0011Q_\u0001\u001cQ\u0006tG\r\\3Bg.\u0004VM]7jgNLwN\u001c+p\u0007>lW.\u001b;\u0015\u000f\u0001\n90!?\u0002|\"A\u00111WAy\u0001\u0004\t)\f\u0003\u0005\u0002:\u0006E\b\u0019AA^\u0011\u001d\ty,!=A\u00021Cq!a@\u0001\t\u0013\u0011\t!A\u0007biR,W\u000e\u001d;GC&dW\r\u001a\u000b\bA\t\r!q\u0001B\u0005\u0011\u001d\u0011)!!@A\u0002\u0001\f!b\u001d;bO\u0016\u001cF/\u0019;f\u0011!\tI,!@A\u0002\u0005m\u0006b\u0002B\u0006\u0003{\u0004\r\u0001T\u0001\bCR$X-\u001c9u\u000f!\u0011yA\u0001E\u0001\t\tE\u0011aF(viB,HoQ8n[&$8i\\8sI&t\u0017\r^8s!\r9#1\u0003\u0004\b\u0003\tA\t\u0001\u0002B\u000b'\r\u0011\u0019b\u0003\u0005\bI\tMA\u0011\u0001B\r)\t\u0011\tB\u0002\u0005\u0003\u001e\tM\u0001\u0001\u0002B\u0010\u0005}yU\u000f\u001e9vi\u000e{W.\\5u\u0007>|'\u000fZ5oCR|'/\u00128ea>Lg\u000e^\n\u0007\u00057Y!\u0011E\t\u0011\u0007E\u0012\u0019#C\u0002\u0003&I\u00121B\u00159d\u000b:$\u0007o\\5oi\"Y!\u0011\u0006B\u000e\u0005\u000b\u0007I\u0011\tB\u0016\u0003\u0019\u0011\boY#omV\u0011!Q\u0006\t\u0004c\t=\u0012b\u0001B\u0019e\t1!\u000b]2F]ZD1B!\u000e\u0003\u001c\t\u0005\t\u0015!\u0003\u0003.\u00059!\u000f]2F]Z\u0004\u0003B\u0003B\u001d\u00057\u0011\t\u0011)A\u0005M\u00059r.\u001e;qkR\u001cu.\\7ji\u000e{wN\u001d3j]\u0006$xN\u001d\u0005\bI\tmA\u0011\u0001B\u001f)\u0019\u0011yDa\u0011\u0003FA!!\u0011\tB\u000e\u001b\t\u0011\u0019\u0002\u0003\u0005\u0003*\tm\u0002\u0019\u0001B\u0017\u0011\u001d\u0011IDa\u000fA\u0002\u0019B\u0001B!\u0013\u0003\u001c\u0011\u0005#1J\u0001\be\u0016\u001cW-\u001b<f+\t\u0011i\u0005\u0005\u0004\r\u0005\u001f\n9$O\u0005\u0004\u0005#j!a\u0004)beRL\u0017\r\u001c$v]\u000e$\u0018n\u001c8\t\u0011\tU#1\u0004C!\u0005/\nqB]3dK&4X-\u00118e%\u0016\u0004H.\u001f\u000b\u0005\u0005\u001b\u0012I\u0006\u0003\u0005\u0003\\\tM\u0003\u0019\u0001B/\u0003\u001d\u0019wN\u001c;fqR\u00042!\rB0\u0013\r\u0011\tG\r\u0002\u000f%B\u001c7)\u00197m\u0007>tG/\u001a=u\u0001"
)
public class OutputCommitCoordinator implements Logging {
   private final SparkConf conf;
   private final boolean isDriver;
   private Option<RpcEndpointRef> coordinatorRef;
   private final int org$apache$spark$scheduler$OutputCommitCoordinator$$NO_AUTHORIZED_COMMITTER;
   private final Map<Object, StageState> stageStates;
   private volatile StageState$ org$apache$spark$scheduler$OutputCommitCoordinator$$StageState$module;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   private StageState$ org$apache$spark$scheduler$OutputCommitCoordinator$$StageState$lzycompute() {
      synchronized(this) {
         if (this.org$apache$spark$scheduler$OutputCommitCoordinator$$StageState$module == null) {
            this.org$apache$spark$scheduler$OutputCommitCoordinator$$StageState$module = new StageState$();
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      Object var3 = null;
      return this.org$apache$spark$scheduler$OutputCommitCoordinator$$StageState$module;
   }

   public Logger org$apache$spark$internal$Logging$$log_() {
      return this.org$apache$spark$internal$Logging$$log_;
   }

   @TraitSetter
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

   public Option<RpcEndpointRef> coordinatorRef() {
      return this.coordinatorRef;
   }

   public void coordinatorRef_$eq(Option<RpcEndpointRef> x$1) {
      this.coordinatorRef = x$1;
   }

   public int org$apache$spark$scheduler$OutputCommitCoordinator$$NO_AUTHORIZED_COMMITTER() {
      return this.org$apache$spark$scheduler$OutputCommitCoordinator$$NO_AUTHORIZED_COMMITTER;
   }

   public StageState$ org$apache$spark$scheduler$OutputCommitCoordinator$$StageState() {
      return this.org$apache$spark$scheduler$OutputCommitCoordinator$$StageState$module == null ? this.org$apache$spark$scheduler$OutputCommitCoordinator$$StageState$lzycompute() : this.org$apache$spark$scheduler$OutputCommitCoordinator$$StageState$module;
   }

   private Map<Object, StageState> stageStates() {
      return this.stageStates;
   }

   public boolean isEmpty() {
      return this.stageStates().isEmpty();
   }

   public boolean canCommit(int stage, int partition, int attemptNumber) {
      AskPermissionToCommitOutput msg = new AskPermissionToCommitOutput(stage, partition, attemptNumber);
      Option var5 = this.coordinatorRef();
      boolean var8;
      if (var5 instanceof Some) {
         Some var6 = (Some)var5;
         RpcEndpointRef endpointRef = (RpcEndpointRef)var6.x();
         var8 = BoxesRunTime.unboxToBoolean(ThreadUtils$.MODULE$.awaitResult(endpointRef.ask(msg, ClassTag$.MODULE$.Boolean()), RpcUtils$.MODULE$.askRpcTimeout(this.conf).duration()));
      } else {
         if (!None$.MODULE$.equals(var5)) {
            throw new MatchError(var5);
         }

         this.logError(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "canCommit called after coordinator was stopped (is SparkEnv shutdown in progress)?";
            }
         });
         var8 = false;
      }

      return var8;
   }

   public synchronized void stageStart(int stage, int maxPartitionId) {
      this.stageStates().update(BoxesRunTime.boxToInteger(stage), new StageState(maxPartitionId + 1));
   }

   public void stageEnd(int stage) {
      synchronized(this) {
         this.stageStates().remove(BoxesRunTime.boxToInteger(stage));
      }
   }

   public synchronized void taskCompleted(int stage, int partition, int attemptNumber, TaskEndReason reason) {
      Object var5 = new Object();

      try {
         StageState stageState = (StageState)this.stageStates().getOrElse(BoxesRunTime.boxToInteger(stage), new Serializable(this, var5) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final OutputCommitCoordinator $outer;
            private final Object nonLocalReturnKey1$1;

            public final Nothing$ apply() {
               this.$outer.logDebug(new Serializable(this) {
                  public static final long serialVersionUID = 0L;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Ignoring task completion for completed stage"})))).s(Nil$.MODULE$);
                  }
               });
               throw new NonLocalReturnControl$mcV$sp(this.nonLocalReturnKey1$1, BoxedUnit.UNIT);
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.nonLocalReturnKey1$1 = nonLocalReturnKey1$1;
               }
            }
         });
         if (Success$.MODULE$.equals(reason)) {
            BoxedUnit var9 = BoxedUnit.UNIT;
         } else if (reason instanceof TaskCommitDenied) {
            this.logInfo(new Serializable(this, stage, partition, attemptNumber) {
               public static final long serialVersionUID = 0L;
               private final int stage$1;
               private final int partition$1;
               private final int attemptNumber$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Task was denied committing, stage: ", ", partition: ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stage$1), BoxesRunTime.boxToInteger(this.partition$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"attempt: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.attemptNumber$1)}))).toString();
               }

               public {
                  this.stage$1 = stage$1;
                  this.partition$1 = partition$1;
                  this.attemptNumber$1 = attemptNumber$1;
               }
            });
            BoxedUnit var11 = BoxedUnit.UNIT;
         } else {
            ((SetLike)stageState.failures().getOrElseUpdate(BoxesRunTime.boxToInteger(partition), new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final Set<Object> apply() {
                  return (Set)Set$.MODULE$.apply(Nil$.MODULE$);
               }
            })).$plus$eq(BoxesRunTime.boxToInteger(attemptNumber));
            if (stageState.authorizedCommitters()[partition] == attemptNumber) {
               this.logDebug(new Serializable(this, stage, partition, attemptNumber) {
                  public static final long serialVersionUID = 0L;
                  private final int stage$1;
                  private final int partition$1;
                  private final int attemptNumber$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Authorized committer (attemptNumber=", ", stage=", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.attemptNumber$1), BoxesRunTime.boxToInteger(this.stage$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"partition=", ") failed; clearing lock"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.partition$1)}))).toString();
                  }

                  public {
                     this.stage$1 = stage$1;
                     this.partition$1 = partition$1;
                     this.attemptNumber$1 = attemptNumber$1;
                  }
               });
               stageState.authorizedCommitters()[partition] = this.org$apache$spark$scheduler$OutputCommitCoordinator$$NO_AUTHORIZED_COMMITTER();
               BoxedUnit var10000 = BoxedUnit.UNIT;
            } else {
               BoxedUnit var12 = BoxedUnit.UNIT;
            }
         }
      } catch (NonLocalReturnControl var10) {
         if (var10.key() != var5) {
            throw var10;
         }

         var10.value$mcV$sp();
      }

   }

   public synchronized void stop() {
      if (this.isDriver) {
         this.coordinatorRef().foreach(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final void apply(RpcEndpointRef x$1) {
               x$1.send(StopCoordinator$.MODULE$);
            }
         });
         this.coordinatorRef_$eq(None$.MODULE$);
         this.stageStates().clear();
      }

   }

   public synchronized boolean handleAskPermissionToCommit(int stage, int partition, int attemptNumber) {
      boolean var4 = false;
      Some var5 = null;
      Option var6 = this.stageStates().get(BoxesRunTime.boxToInteger(stage));
      boolean var8;
      if (var6 instanceof Some) {
         var4 = true;
         var5 = (Some)var6;
         StageState state = (StageState)var5.x();
         if (this.attemptFailed(state, partition, attemptNumber)) {
            this.logInfo(new Serializable(this, stage, partition, attemptNumber) {
               public static final long serialVersionUID = 0L;
               private final int stage$2;
               private final int partition$2;
               private final int attemptNumber$2;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Denying attemptNumber=", " to commit for stage=", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.attemptNumber$2), BoxesRunTime.boxToInteger(this.stage$2)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" partition=", " as task attempt ", " has already failed."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.partition$2), BoxesRunTime.boxToInteger(this.attemptNumber$2)}))).toString();
               }

               public {
                  this.stage$2 = stage$2;
                  this.partition$2 = partition$2;
                  this.attemptNumber$2 = attemptNumber$2;
               }
            });
            var8 = false;
            return var8;
         }
      }

      if (var4) {
         StageState state = (StageState)var5.x();
         int var10 = state.authorizedCommitters()[partition];
         boolean var11;
         if (this.org$apache$spark$scheduler$OutputCommitCoordinator$$NO_AUTHORIZED_COMMITTER() == var10) {
            this.logDebug(new Serializable(this, stage, partition, attemptNumber) {
               public static final long serialVersionUID = 0L;
               private final int stage$2;
               private final int partition$2;
               private final int attemptNumber$2;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Authorizing attemptNumber=", " to commit for stage=", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.attemptNumber$2), BoxesRunTime.boxToInteger(this.stage$2)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"partition=", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.partition$2)}))).toString();
               }

               public {
                  this.stage$2 = stage$2;
                  this.partition$2 = partition$2;
                  this.attemptNumber$2 = attemptNumber$2;
               }
            });
            state.authorizedCommitters()[partition] = attemptNumber;
            var11 = true;
         } else {
            boolean var10000;
            if (var10 == attemptNumber) {
               this.logWarning(new Serializable(this, stage, partition, attemptNumber, var10) {
                  public static final long serialVersionUID = 0L;
                  private final int stage$2;
                  private final int partition$2;
                  private final int attemptNumber$2;
                  private final int x1$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Authorizing duplicate request to commit for "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"attemptNumber=", " to commit for stage=", ","})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.attemptNumber$2), BoxesRunTime.boxToInteger(this.stage$2)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" partition=", "; existingCommitter = ", "."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.partition$2), BoxesRunTime.boxToInteger(this.x1$1)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" This can indicate dropped network traffic."})))).s(Nil$.MODULE$)).toString();
                  }

                  public {
                     this.stage$2 = stage$2;
                     this.partition$2 = partition$2;
                     this.attemptNumber$2 = attemptNumber$2;
                     this.x1$1 = x1$1;
                  }
               });
               var10000 = true;
            } else {
               this.logDebug(new Serializable(this, stage, partition, attemptNumber, var10) {
                  public static final long serialVersionUID = 0L;
                  private final int stage$2;
                  private final int partition$2;
                  private final int attemptNumber$2;
                  private final int x1$1;

                  public final String apply() {
                     return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Denying attemptNumber=", " to commit for stage=", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.attemptNumber$2), BoxesRunTime.boxToInteger(this.stage$2)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"partition=", "; existingCommitter = ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.partition$2), BoxesRunTime.boxToInteger(this.x1$1)}))).toString();
                  }

                  public {
                     this.stage$2 = stage$2;
                     this.partition$2 = partition$2;
                     this.attemptNumber$2 = attemptNumber$2;
                     this.x1$1 = x1$1;
                  }
               });
               var10000 = false;
            }

            var11 = var10000;
         }

         var8 = var11;
      } else {
         if (!None$.MODULE$.equals(var6)) {
            throw new MatchError(var6);
         }

         this.logDebug(new Serializable(this, stage, partition, attemptNumber) {
            public static final long serialVersionUID = 0L;
            private final int stage$2;
            private final int partition$2;
            private final int attemptNumber$2;

            public final String apply() {
               return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Stage ", " has completed, so not allowing"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.stage$2)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{" attempt number ", " of partition ", " to commit"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(this.attemptNumber$2), BoxesRunTime.boxToInteger(this.partition$2)}))).toString();
            }

            public {
               this.stage$2 = stage$2;
               this.partition$2 = partition$2;
               this.attemptNumber$2 = attemptNumber$2;
            }
         });
         var8 = false;
      }

      return var8;
   }

   private synchronized boolean attemptFailed(StageState stageState, int partition, int attempt) {
      return stageState.failures().get(BoxesRunTime.boxToInteger(partition)).exists(new Serializable(this, attempt) {
         public static final long serialVersionUID = 0L;
         private final int attempt$1;

         public final boolean apply(Set<Object> x$2) {
            return x$2.contains(BoxesRunTime.boxToInteger(this.attempt$1));
         }

         public {
            this.attempt$1 = attempt$1;
         }
      });
   }

   public OutputCommitCoordinator(SparkConf conf, boolean isDriver) {
      this.conf = conf;
      this.isDriver = isDriver;
      Logging$class.$init$(this);
      this.coordinatorRef = None$.MODULE$;
      this.org$apache$spark$scheduler$OutputCommitCoordinator$$NO_AUTHORIZED_COMMITTER = -1;
      this.stageStates = (Map)Map$.MODULE$.apply(Nil$.MODULE$);
   }

   public class StageState implements Product, Serializable {
      private final int numPartitions;
      private final int[] authorizedCommitters;
      private final Map<Object, Set<Object>> failures;
      // $FF: synthetic field
      public final OutputCommitCoordinator $outer;

      public int numPartitions() {
         return this.numPartitions;
      }

      public int[] authorizedCommitters() {
         return this.authorizedCommitters;
      }

      public Map<Object, Set<Object>> failures() {
         return this.failures;
      }

      public StageState copy(int numPartitions) {
         return this.org$apache$spark$scheduler$OutputCommitCoordinator$StageState$$$outer().new StageState(numPartitions);
      }

      public int copy$default$1() {
         return this.numPartitions();
      }

      public String productPrefix() {
         return "StageState";
      }

      public int productArity() {
         return 1;
      }

      public Object productElement(int x$1) {
         switch (x$1) {
            case 0:
               return BoxesRunTime.boxToInteger(this.numPartitions());
            default:
               throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
         }
      }

      public Iterator<Object> productIterator() {
         return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
      }

      public boolean canEqual(Object x$1) {
         return x$1 instanceof StageState;
      }

      public int hashCode() {
         int var1 = -889275714;
         var1 = Statics.mix(var1, this.numPartitions());
         return Statics.finalizeHash(var1, 1);
      }

      public String toString() {
         return ScalaRunTime$.MODULE$._toString(this);
      }

      public boolean equals(Object x$1) {
         boolean var10000;
         if (this != x$1) {
            label48: {
               boolean var3;
               if (x$1 instanceof StageState) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               if (var3) {
                  StageState var4 = (StageState)x$1;
                  if (this.numPartitions() == var4.numPartitions() && var4.canEqual(this)) {
                     break label48;
                  }
               }

               var10000 = false;
               return var10000;
            }
         }

         var10000 = true;
         return var10000;
      }

      // $FF: synthetic method
      public OutputCommitCoordinator org$apache$spark$scheduler$OutputCommitCoordinator$StageState$$$outer() {
         return this.$outer;
      }

      public StageState(int numPartitions) {
         this.numPartitions = numPartitions;
         if (OutputCommitCoordinator.this == null) {
            throw null;
         } else {
            this.$outer = OutputCommitCoordinator.this;
            super();
            Product$class.$init$(this);
            this.authorizedCommitters = (int[])Array$.MODULE$.fill(numPartitions, new Serializable(this) {
               public static final long serialVersionUID = 0L;
               // $FF: synthetic field
               private final StageState $outer;

               public final int apply() {
                  return this.apply$mcI$sp();
               }

               public int apply$mcI$sp() {
                  return this.$outer.org$apache$spark$scheduler$OutputCommitCoordinator$StageState$$$outer().org$apache$spark$scheduler$OutputCommitCoordinator$$NO_AUTHORIZED_COMMITTER();
               }

               public {
                  if ($outer == null) {
                     throw null;
                  } else {
                     this.$outer = $outer;
                  }
               }
            }, ClassTag$.MODULE$.apply(Integer.TYPE));
            this.failures = (Map)Map$.MODULE$.apply(Nil$.MODULE$);
         }
      }
   }

   public class StageState$ extends AbstractFunction1<Object, StageState> implements Serializable {
      // $FF: synthetic field
      private final OutputCommitCoordinator $outer;

      public final String toString() {
         return "StageState";
      }

      public StageState apply(int numPartitions) {
         return this.$outer.new StageState(numPartitions);
      }

      public Option<Object> unapply(StageState x$0) {
         return (Option<Object>)(x$0 == null ? None$.MODULE$ : new Some(BoxesRunTime.boxToInteger(x$0.numPartitions())));
      }

      public StageState$() {
         if (OutputCommitCoordinator.this == null) {
            throw null;
         } else {
            this.$outer = OutputCommitCoordinator.this;
            super();
         }
      }
   }

   public static class OutputCommitCoordinatorEndpoint implements RpcEndpoint, Logging {
      private final RpcEnv rpcEnv;
      public final OutputCommitCoordinator org$apache$spark$scheduler$OutputCommitCoordinator$OutputCommitCoordinatorEndpoint$$outputCommitCoordinator;
      private transient Logger org$apache$spark$internal$Logging$$log_;

      public Logger org$apache$spark$internal$Logging$$log_() {
         return this.org$apache$spark$internal$Logging$$log_;
      }

      @TraitSetter
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

      public final RpcEndpointRef self() {
         return RpcEndpoint$class.self(this);
      }

      public void onError(Throwable cause) {
         RpcEndpoint$class.onError(this, cause);
      }

      public void onConnected(RpcAddress remoteAddress) {
         RpcEndpoint$class.onConnected(this, remoteAddress);
      }

      public void onDisconnected(RpcAddress remoteAddress) {
         RpcEndpoint$class.onDisconnected(this, remoteAddress);
      }

      public void onNetworkError(Throwable cause, RpcAddress remoteAddress) {
         RpcEndpoint$class.onNetworkError(this, cause, remoteAddress);
      }

      public void onStart() {
         RpcEndpoint$class.onStart(this);
      }

      public void onStop() {
         RpcEndpoint$class.onStop(this);
      }

      public final void stop() {
         RpcEndpoint$class.stop(this);
      }

      public RpcEnv rpcEnv() {
         return this.rpcEnv;
      }

      public PartialFunction<Object, BoxedUnit> receive() {
         return new Serializable(this) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final OutputCommitCoordinatorEndpoint $outer;

            public final <A1, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
               Object var4;
               if (StopCoordinator$.MODULE$.equals(x1)) {
                  this.$outer.logInfo(new Serializable(this) {
                     public static final long serialVersionUID = 0L;

                     public final String apply() {
                        return "OutputCommitCoordinator stopped!";
                     }
                  });
                  this.$outer.stop();
                  var4 = BoxedUnit.UNIT;
               } else {
                  var4 = var2.apply(x1);
               }

               return (B1)var4;
            }

            public final boolean isDefinedAt(Object x1) {
               boolean var3;
               if (StopCoordinator$.MODULE$.equals(x1)) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
               }
            }
         };
      }

      public PartialFunction<Object, BoxedUnit> receiveAndReply(RpcCallContext context) {
         return new Serializable(this, context) {
            public static final long serialVersionUID = 0L;
            // $FF: synthetic field
            private final OutputCommitCoordinatorEndpoint $outer;
            private final RpcCallContext context$1;

            public final <A1, B1> B1 applyOrElse(A1 x2, Function1<A1, B1> var2) {
               Object var8;
               if (x2 instanceof AskPermissionToCommitOutput) {
                  AskPermissionToCommitOutput var4 = (AskPermissionToCommitOutput)x2;
                  int stage = var4.stage();
                  int partition = var4.partition();
                  int attemptNumber = var4.attemptNumber();
                  this.context$1.reply(BoxesRunTime.boxToBoolean(this.$outer.org$apache$spark$scheduler$OutputCommitCoordinator$OutputCommitCoordinatorEndpoint$$outputCommitCoordinator.handleAskPermissionToCommit(stage, partition, attemptNumber)));
                  var8 = BoxedUnit.UNIT;
               } else {
                  var8 = var2.apply(x2);
               }

               return (B1)var8;
            }

            public final boolean isDefinedAt(Object x2) {
               boolean var3;
               if (x2 instanceof AskPermissionToCommitOutput) {
                  var3 = true;
               } else {
                  var3 = false;
               }

               return var3;
            }

            public {
               if ($outer == null) {
                  throw null;
               } else {
                  this.$outer = $outer;
                  this.context$1 = context$1;
               }
            }
         };
      }

      public OutputCommitCoordinatorEndpoint(RpcEnv rpcEnv, OutputCommitCoordinator outputCommitCoordinator) {
         this.rpcEnv = rpcEnv;
         this.org$apache$spark$scheduler$OutputCommitCoordinator$OutputCommitCoordinatorEndpoint$$outputCommitCoordinator = outputCommitCoordinator;
         RpcEndpoint$class.$init$(this);
         Logging$class.$init$(this);
         this.logDebug(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "init";
            }
         });
      }
   }
}
