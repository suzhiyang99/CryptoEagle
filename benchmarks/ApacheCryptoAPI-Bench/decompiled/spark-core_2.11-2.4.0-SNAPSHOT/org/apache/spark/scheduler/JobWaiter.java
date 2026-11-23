package org.apache.spark.scheduler;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Function2;
import scala.None$;
import scala.Serializable;
import scala.concurrent.Future;
import scala.concurrent.Promise;
import scala.concurrent.Promise$;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Ma!B\u0001\u0003\u0001\u0011Q!!\u0003&pE^\u000b\u0017\u000e^3s\u0015\t\u0019A!A\u0005tG\",G-\u001e7fe*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x-\u0006\u0002\fgM!\u0001\u0001\u0004\n\u0017!\ti\u0001#D\u0001\u000f\u0015\u0005y\u0011!B:dC2\f\u0017BA\t\u000f\u0005\u0019\te.\u001f*fMB\u00111\u0003F\u0007\u0002\u0005%\u0011QC\u0001\u0002\f\u0015>\u0014G*[:uK:,'\u000f\u0005\u0002\u001855\t\u0001D\u0003\u0002\u001a\t\u0005A\u0011N\u001c;fe:\fG.\u0003\u0002\u001c1\t9Aj\\4hS:<\u0007\u0002C\u000f\u0001\u0005\u0003\u0005\u000b\u0011B\u0010\u0002\u0019\u0011\fwmU2iK\u0012,H.\u001a:\u0004\u0001A\u00111\u0003I\u0005\u0003C\t\u0011A\u0002R!H'\u000eDW\rZ;mKJD\u0001b\t\u0001\u0003\u0006\u0004%\t\u0001J\u0001\u0006U>\u0014\u0017\nZ\u000b\u0002KA\u0011QBJ\u0005\u0003O9\u00111!\u00138u\u0011!I\u0003A!A!\u0002\u0013)\u0013A\u00026pE&#\u0007\u0005\u0003\u0005,\u0001\t\u0005\t\u0015!\u0003&\u0003)!x\u000e^1m)\u0006\u001c8n\u001d\u0005\t[\u0001\u0011\t\u0011)A\u0005]\u0005i!/Z:vYRD\u0015M\u001c3mKJ\u0004R!D\u0018&cqJ!\u0001\r\b\u0003\u0013\u0019+hn\u0019;j_:\u0014\u0004C\u0001\u001a4\u0019\u0001!Q\u0001\u000e\u0001C\u0002U\u0012\u0011\u0001V\t\u0003me\u0002\"!D\u001c\n\u0005ar!a\u0002(pi\"Lgn\u001a\t\u0003\u001biJ!a\u000f\b\u0003\u0007\u0005s\u0017\u0010\u0005\u0002\u000e{%\u0011aH\u0004\u0002\u0005+:LG\u000fC\u0003A\u0001\u0011\u0005\u0011)\u0001\u0004=S:LGO\u0010\u000b\u0006\u0005\u000e#UI\u0012\t\u0004'\u0001\t\u0004\"B\u000f@\u0001\u0004y\u0002\"B\u0012@\u0001\u0004)\u0003\"B\u0016@\u0001\u0004)\u0003\"B\u0017@\u0001\u0004q\u0003b\u0002%\u0001\u0005\u0004%I!S\u0001\u000eM&t\u0017n\u001d5fIR\u000b7o[:\u0016\u0003)\u0003\"a\u0013+\u000e\u00031S!!\u0014(\u0002\r\u0005$x.\\5d\u0015\ty\u0005+\u0001\u0006d_:\u001cWO\u001d:f]RT!!\u0015*\u0002\tU$\u0018\u000e\u001c\u0006\u0002'\u0006!!.\u0019<b\u0013\t)FJA\u0007Bi>l\u0017nY%oi\u0016<WM\u001d\u0005\u0007/\u0002\u0001\u000b\u0011\u0002&\u0002\u001d\u0019Lg.[:iK\u0012$\u0016m]6tA!9\u0011\f\u0001b\u0001\n\u0013Q\u0016A\u00036pEB\u0013x.\\5tKV\t1\fE\u0002]=rj\u0011!\u0018\u0006\u0003\u001f:I!aX/\u0003\u000fA\u0013x.\\5tK\"1\u0011\r\u0001Q\u0001\nm\u000b1B[8c!J|W.[:fA!)1\r\u0001C\u0001I\u0006Y!n\u001c2GS:L7\u000f[3e+\u0005)\u0007CA\u0007g\u0013\t9gBA\u0004C_>dW-\u00198\t\u000b%\u0004A\u0011\u00016\u0002!\r|W\u000e\u001d7fi&|gNR;ukJ,W#A6\u0011\u0007qcG(\u0003\u0002n;\n1a)\u001e;ve\u0016DQa\u001c\u0001\u0005\u0002A\faaY1oG\u0016dG#\u0001\u001f\t\u000bI\u0004A\u0011I:\u0002\u001bQ\f7o[*vG\u000e,W\rZ3e)\raDO\u001e\u0005\u0006kF\u0004\r!J\u0001\u0006S:$W\r\u001f\u0005\u0006oF\u0004\r!O\u0001\u0007e\u0016\u001cX\u000f\u001c;\t\u000be\u0004A\u0011\t>\u0002\u0013)|'MR1jY\u0016$GC\u0001\u001f|\u0011\u0015a\b\u00101\u0001~\u0003%)\u0007pY3qi&|g\u000eE\u0002\u007f\u0003\u001bq1a`A\u0005\u001d\u0011\t\t!a\u0002\u000e\u0005\u0005\r!bAA\u0003=\u00051AH]8pizJ\u0011aD\u0005\u0004\u0003\u0017q\u0011a\u00029bG.\fw-Z\u0005\u0005\u0003\u001f\t\tBA\u0005Fq\u000e,\u0007\u000f^5p]*\u0019\u00111\u0002\b"
)
public class JobWaiter<T> implements JobListener, Logging {
   private final DAGScheduler dagScheduler;
   private final int jobId;
   private final int totalTasks;
   private final Function2<Object, T, BoxedUnit> resultHandler;
   private final AtomicInteger finishedTasks;
   private final Promise<BoxedUnit> jobPromise;
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

   public int jobId() {
      return this.jobId;
   }

   private AtomicInteger finishedTasks() {
      return this.finishedTasks;
   }

   private Promise<BoxedUnit> jobPromise() {
      return this.jobPromise;
   }

   public boolean jobFinished() {
      return this.jobPromise().isCompleted();
   }

   public Future<BoxedUnit> completionFuture() {
      return this.jobPromise().future();
   }

   public void cancel() {
      this.dagScheduler.cancelJob(this.jobId(), None$.MODULE$);
   }

   public void taskSucceeded(int index, Object result) {
      synchronized(this) {
         this.resultHandler.apply(BoxesRunTime.boxToInteger(index), result);
      }

      if (this.finishedTasks().incrementAndGet() == this.totalTasks) {
         this.jobPromise().success(BoxedUnit.UNIT);
      }

   }

   public void jobFailed(Exception exception) {
      if (!this.jobPromise().tryFailure(exception)) {
         this.logWarning(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return "Ignore failure";
            }
         }, exception);
      }

   }

   public JobWaiter(DAGScheduler dagScheduler, int jobId, int totalTasks, Function2<Object, T, BoxedUnit> resultHandler) {
      this.dagScheduler = dagScheduler;
      this.jobId = jobId;
      this.totalTasks = totalTasks;
      this.resultHandler = resultHandler;
      Logging$class.$init$(this);
      this.finishedTasks = new AtomicInteger(0);
      this.jobPromise = totalTasks == 0 ? Promise$.MODULE$.successful(BoxedUnit.UNIT) : Promise$.MODULE$.apply();
   }
}
