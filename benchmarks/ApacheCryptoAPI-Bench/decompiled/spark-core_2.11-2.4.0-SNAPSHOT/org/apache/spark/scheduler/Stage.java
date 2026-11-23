package org.apache.spark.scheduler;

import org.apache.spark.executor.TaskMetrics;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.rdd.RDD;
import org.apache.spark.util.CallSite;
import org.slf4j.Logger;
import scala.Function0;
import scala.Some;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.immutable.List;
import scala.collection.mutable.HashSet;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005MfAB\u0001\u0003\u0003\u0003\u0011!BA\u0003Ti\u0006<WM\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON\u0019\u0001aC\t\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\t\u0011R#D\u0001\u0014\u0015\t!B!\u0001\u0005j]R,'O\\1m\u0013\t12CA\u0004M_\u001e<\u0017N\\4\t\u0011a\u0001!Q1A\u0005\u0002i\t!!\u001b3\u0004\u0001U\t1\u0004\u0005\u0002\r9%\u0011Q$\u0004\u0002\u0004\u0013:$\b\u0002C\u0010\u0001\u0005\u0003\u0005\u000b\u0011B\u000e\u0002\u0007%$\u0007\u0005\u0003\u0005\"\u0001\t\u0015\r\u0011\"\u0001#\u0003\r\u0011H\rZ\u000b\u0002GA\u0012Ae\u000b\t\u0004K\u001dJS\"\u0001\u0014\u000b\u0005\u0005\"\u0011B\u0001\u0015'\u0005\r\u0011F\t\u0012\t\u0003U-b\u0001\u0001B\u0005-[\u0005\u0005\t\u0011!B\u0001g\t\u0019q\fJ\u0019\t\u00119\u0002!\u0011!Q\u0001\n=\nAA\u001d3eAA\u0012\u0001G\r\t\u0004K\u001d\n\u0004C\u0001\u00163\t%aS&!A\u0001\u0002\u000b\u00051'\u0005\u00025oA\u0011A\"N\u0005\u0003m5\u0011qAT8uQ&tw\r\u0005\u0002\rq%\u0011\u0011(\u0004\u0002\u0004\u0003:L\b\u0002C\u001e\u0001\u0005\u000b\u0007I\u0011\u0001\u000e\u0002\u00119,X\u000eV1tWND\u0001\"\u0010\u0001\u0003\u0002\u0003\u0006IaG\u0001\n]VlG+Y:lg\u0002B\u0001b\u0010\u0001\u0003\u0006\u0004%\t\u0001Q\u0001\ba\u0006\u0014XM\u001c;t+\u0005\t\u0005c\u0001\"K\u001b:\u00111\t\u0013\b\u0003\t\u001ek\u0011!\u0012\u0006\u0003\rf\ta\u0001\u0010:p_Rt\u0014\"\u0001\b\n\u0005%k\u0011a\u00029bG.\fw-Z\u0005\u0003\u00172\u0013A\u0001T5ti*\u0011\u0011*\u0004\t\u0003\u001d\u0002i\u0011A\u0001\u0005\t!\u0002\u0011\t\u0011)A\u0005\u0003\u0006A\u0001/\u0019:f]R\u001c\b\u0005\u0003\u0005S\u0001\t\u0015\r\u0011\"\u0001\u001b\u0003)1\u0017N]:u\u0015>\u0014\u0017\n\u001a\u0005\t)\u0002\u0011\t\u0011)A\u00057\u0005Ya-\u001b:ti*{'-\u00133!\u0011!1\u0006A!b\u0001\n\u00039\u0016\u0001C2bY2\u001c\u0016\u000e^3\u0016\u0003a\u0003\"!\u0017/\u000e\u0003iS!a\u0017\u0003\u0002\tU$\u0018\u000e\\\u0005\u0003;j\u0013\u0001bQ1mYNKG/\u001a\u0005\t?\u0002\u0011\t\u0011)A\u00051\u0006I1-\u00197m'&$X\r\t\u0005\u0006C\u0002!\tAY\u0001\u0007y%t\u0017\u000e\u001e \u0015\u000f5\u001bG-\u001b6lY\")\u0001\u0004\u0019a\u00017!)\u0011\u0005\u0019a\u0001KB\u0012a\r\u001b\t\u0004K\u001d:\u0007C\u0001\u0016i\t%aC-!A\u0001\u0002\u000b\u00051\u0007C\u0003<A\u0002\u00071\u0004C\u0003@A\u0002\u0007\u0011\tC\u0003SA\u0002\u00071\u0004C\u0003WA\u0002\u0007\u0001\fC\u0004o\u0001\t\u0007I\u0011\u0001\u000e\u0002\u001b9,X\u000eU1si&$\u0018n\u001c8t\u0011\u0019\u0001\b\u0001)A\u00057\u0005qa.^7QCJ$\u0018\u000e^5p]N\u0004\u0003b\u0002:\u0001\u0005\u0004%\ta]\u0001\u0007U>\u0014\u0017\nZ:\u0016\u0003Q\u00042!\u001e>\u001c\u001b\u00051(BA<y\u0003\u001diW\u000f^1cY\u0016T!!_\u0007\u0002\u0015\r|G\u000e\\3di&|g.\u0003\u0002|m\n9\u0001*Y:i'\u0016$\bBB?\u0001A\u0003%A/A\u0004k_\nLEm\u001d\u0011\t\u000f}\u0004\u0001\u0019!C\u00055\u0005ia.\u001a=u\u0003R$X-\u001c9u\u0013\u0012D\u0011\"a\u0001\u0001\u0001\u0004%I!!\u0002\u0002#9,\u0007\u0010^!ui\u0016l\u0007\u000f^%e?\u0012*\u0017\u000f\u0006\u0003\u0002\b\u00055\u0001c\u0001\u0007\u0002\n%\u0019\u00111B\u0007\u0003\tUs\u0017\u000e\u001e\u0005\n\u0003\u001f\t\t!!AA\u0002m\t1\u0001\u001f\u00132\u0011\u001d\t\u0019\u0002\u0001Q!\nm\taB\\3yi\u0006#H/Z7qi&#\u0007\u0005C\u0005\u0002\u0018\u0001\u0011\r\u0011\"\u0001\u0002\u001a\u0005!a.Y7f+\t\tY\u0002\u0005\u0003\u0002\u001e\u0005\rbb\u0001\u0007\u0002 %\u0019\u0011\u0011E\u0007\u0002\rA\u0013X\rZ3g\u0013\u0011\t)#a\n\u0003\rM#(/\u001b8h\u0015\r\t\t#\u0004\u0005\t\u0003W\u0001\u0001\u0015!\u0003\u0002\u001c\u0005)a.Y7fA!I\u0011q\u0006\u0001C\u0002\u0013\u0005\u0011\u0011D\u0001\bI\u0016$\u0018-\u001b7t\u0011!\t\u0019\u0004\u0001Q\u0001\n\u0005m\u0011\u0001\u00033fi\u0006LGn\u001d\u0011\t\u0013\u0005]\u0002\u00011A\u0005\n\u0005e\u0012aC0mCR,7\u000f^%oM>,\"!a\u000f\u0011\u00079\u000bi$C\u0002\u0002@\t\u0011\u0011b\u0015;bO\u0016LeNZ8\t\u0013\u0005\r\u0003\u00011A\u0005\n\u0005\u0015\u0013aD0mCR,7\u000f^%oM>|F%Z9\u0015\t\u0005\u001d\u0011q\t\u0005\u000b\u0003\u001f\t\t%!AA\u0002\u0005m\u0002\u0002CA&\u0001\u0001\u0006K!a\u000f\u0002\u0019}c\u0017\r^3ti&sgm\u001c\u0011\t\u0011\u0005=\u0003A1A\u0005\u0002M\fQCZ3uG\"4\u0015-\u001b7fI\u0006#H/Z7qi&#7\u000fC\u0004\u0002T\u0001\u0001\u000b\u0011\u0002;\u0002-\u0019,Go\u00195GC&dW\rZ!ui\u0016l\u0007\u000f^%eg\u0002B\u0001\"a\u0016\u0001\t\u0003\u0011\u0011\u0011L\u0001\u000eG2,\u0017M\u001d$bS2,(/Z:\u0015\u0005\u0005\u001d\u0001bBA/\u0001\u0011\u0005\u0011qL\u0001\u0014[\u0006\\WMT3x'R\fw-Z!ui\u0016l\u0007\u000f\u001e\u000b\u0007\u0003\u000f\t\t'!\u001a\t\u000f\u0005\r\u00141\fa\u00017\u00051b.^7QCJ$\u0018\u000e^5p]N$vnQ8naV$X\r\u0003\u0006\u0002h\u0005m\u0003\u0013!a\u0001\u0003S\nq\u0003^1tW2{7-\u00197jif\u0004&/\u001a4fe\u0016t7-Z:\u0011\u000b\t\u000bY'a\u001c\n\u0007\u00055DJA\u0002TKF\u0004RAQA6\u0003c\u00022ATA:\u0013\r\t)H\u0001\u0002\r)\u0006\u001c8\u000eT8dCRLwN\u001c\u0005\b\u0003s\u0002A\u0011AA\u001d\u0003)a\u0017\r^3ti&sgm\u001c\u0005\b\u0003{\u0002AQIA@\u0003!A\u0017m\u001d5D_\u0012,G#A\u000e\t\u000f\u0005\r\u0005\u0001\"\u0012\u0002\u0006\u00061Q-];bYN$B!a\"\u0002\u000eB\u0019A\"!#\n\u0007\u0005-UBA\u0004C_>dW-\u00198\t\u000f\u0005=\u0015\u0011\u0011a\u0001o\u0005)q\u000e\u001e5fe\"9\u00111\u0013\u0001\u0007\u0002\u0005U\u0015!\u00064j]\u0012l\u0015n]:j]\u001e\u0004\u0016M\u001d;ji&|gn\u001d\u000b\u0003\u0003/\u0003BAQA67!I\u00111\u0014\u0001\u0012\u0002\u0013\u0005\u0011QT\u0001\u001e[\u0006\\WMT3x'R\fw-Z!ui\u0016l\u0007\u000f\u001e\u0013eK\u001a\fW\u000f\u001c;%eU\u0011\u0011q\u0014\u0016\u0005\u0003S\n\tk\u000b\u0002\u0002$B!\u0011QUAX\u001b\t\t9K\u0003\u0003\u0002*\u0006-\u0016!C;oG\",7m[3e\u0015\r\ti+D\u0001\u000bC:tw\u000e^1uS>t\u0017\u0002BAY\u0003O\u0013\u0011#\u001e8dQ\u0016\u001c7.\u001a3WCJL\u0017M\\2f\u0001"
)
public abstract class Stage implements Logging {
   private final int id;
   private final RDD<?> rdd;
   private final int numTasks;
   private final List<Stage> parents;
   private final int firstJobId;
   private final CallSite callSite;
   private final int numPartitions;
   private final HashSet<Object> jobIds;
   private int nextAttemptId;
   private final String name;
   private final String details;
   private StageInfo _latestInfo;
   private final HashSet<Object> fetchFailedAttemptIds;
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

   public int id() {
      return this.id;
   }

   public RDD<?> rdd() {
      return this.rdd;
   }

   public int numTasks() {
      return this.numTasks;
   }

   public List<Stage> parents() {
      return this.parents;
   }

   public int firstJobId() {
      return this.firstJobId;
   }

   public CallSite callSite() {
      return this.callSite;
   }

   public int numPartitions() {
      return this.numPartitions;
   }

   public HashSet<Object> jobIds() {
      return this.jobIds;
   }

   private int nextAttemptId() {
      return this.nextAttemptId;
   }

   private void nextAttemptId_$eq(int x$1) {
      this.nextAttemptId = x$1;
   }

   public String name() {
      return this.name;
   }

   public String details() {
      return this.details;
   }

   private StageInfo _latestInfo() {
      return this._latestInfo;
   }

   private void _latestInfo_$eq(StageInfo x$1) {
      this._latestInfo = x$1;
   }

   public HashSet<Object> fetchFailedAttemptIds() {
      return this.fetchFailedAttemptIds;
   }

   public void clearFailures() {
      this.fetchFailedAttemptIds().clear();
   }

   public void makeNewStageAttempt(int numPartitionsToCompute, Seq<Seq<TaskLocation>> taskLocalityPreferences) {
      TaskMetrics metrics = new TaskMetrics();
      metrics.register(this.rdd().sparkContext());
      this._latestInfo_$eq(StageInfo$.MODULE$.fromStage(this, this.nextAttemptId(), new Some(BoxesRunTime.boxToInteger(numPartitionsToCompute)), metrics, taskLocalityPreferences));
      this.nextAttemptId_$eq(this.nextAttemptId() + 1);
   }

   public Seq<Seq<TaskLocation>> makeNewStageAttempt$default$2() {
      return (Seq)Seq$.MODULE$.empty();
   }

   public StageInfo latestInfo() {
      return this._latestInfo();
   }

   public final int hashCode() {
      return this.id();
   }

   public final boolean equals(Object other) {
      boolean var4;
      if (other instanceof Stage) {
         Stage var3 = (Stage)other;
         var4 = var3 != null && var3.id() == this.id();
      } else {
         var4 = false;
      }

      return var4;
   }

   public abstract Seq<Object> findMissingPartitions();

   public Stage(int id, RDD<?> rdd, int numTasks, List<Stage> parents, int firstJobId, CallSite callSite) {
      this.id = id;
      this.rdd = rdd;
      this.numTasks = numTasks;
      this.parents = parents;
      this.firstJobId = firstJobId;
      this.callSite = callSite;
      Logging$class.$init$(this);
      this.numPartitions = rdd.partitions().length;
      this.jobIds = new HashSet<Object>();
      this.nextAttemptId = 0;
      this.name = callSite.shortForm();
      this.details = callSite.longForm();
      this._latestInfo = StageInfo$.MODULE$.fromStage(this, this.nextAttemptId(), StageInfo$.MODULE$.fromStage$default$3(), StageInfo$.MODULE$.fromStage$default$4(), StageInfo$.MODULE$.fromStage$default$5());
      this.fetchFailedAttemptIds = new HashSet<Object>();
   }
}
