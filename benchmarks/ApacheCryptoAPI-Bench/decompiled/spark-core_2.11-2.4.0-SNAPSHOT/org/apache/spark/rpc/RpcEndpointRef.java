package org.apache.spark.rpc;

import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.RpcUtils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.concurrent.Future;
import scala.reflect.ClassTag;
import scala.reflect.ScalaSignature;
import scala.runtime.TraitSetter;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005\u0005bAB\u0001\u0003\u0003\u0003!!B\u0001\bSa\u000e,e\u000e\u001a9pS:$(+\u001a4\u000b\u0005\r!\u0011a\u0001:qG*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014xm\u0005\u0003\u0001\u0017E!\u0002C\u0001\u0007\u0010\u001b\u0005i!\"\u0001\b\u0002\u000bM\u001c\u0017\r\\1\n\u0005Ai!AB!osJ+g\r\u0005\u0002\r%%\u00111#\u0004\u0002\r'\u0016\u0014\u0018.\u00197ju\u0006\u0014G.\u001a\t\u0003+ai\u0011A\u0006\u0006\u0003/\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00033Y\u0011q\u0001T8hO&tw\r\u0003\u0005\u001c\u0001\t\u0005\t\u0015!\u0003\u001e\u0003\u0011\u0019wN\u001c4\u0004\u0001A\u0011adH\u0007\u0002\t%\u0011\u0001\u0005\u0002\u0002\n'B\f'o[\"p]\u001aDQA\t\u0001\u0005\u0002\r\na\u0001P5oSRtDC\u0001\u0013'!\t)\u0003!D\u0001\u0003\u0011\u0015Y\u0012\u00051\u0001\u001e\u0011\u0019A\u0003\u0001)A\u0005S\u0005QQ.\u0019=SKR\u0014\u0018.Z:\u0011\u00051Q\u0013BA\u0016\u000e\u0005\rIe\u000e\u001e\u0005\u0007[\u0001\u0001\u000b\u0011\u0002\u0018\u0002\u0017I,GO]=XC&$Xj\u001d\t\u0003\u0019=J!\u0001M\u0007\u0003\t1{gn\u001a\u0005\u0007e\u0001\u0001\u000b\u0011B\u001a\u0002#\u0011,g-Y;mi\u0006\u001b8\u000eV5nK>,H\u000f\u0005\u0002&i%\u0011QG\u0001\u0002\u000b%B\u001cG+[7f_V$\b\"B\u001c\u0001\r\u0003A\u0014aB1eIJ,7o]\u000b\u0002sA\u0011QEO\u0005\u0003w\t\u0011!B\u00159d\u0003\u0012$'/Z:t\u0011\u0015i\u0004A\"\u0001?\u0003\u0011q\u0017-\\3\u0016\u0003}\u0002\"\u0001Q\"\u000f\u00051\t\u0015B\u0001\"\u000e\u0003\u0019\u0001&/\u001a3fM&\u0011A)\u0012\u0002\u0007'R\u0014\u0018N\\4\u000b\u0005\tk\u0001\"B$\u0001\r\u0003A\u0015\u0001B:f]\u0012$\"!\u0013'\u0011\u00051Q\u0015BA&\u000e\u0005\u0011)f.\u001b;\t\u000b53\u0005\u0019\u0001(\u0002\u000f5,7o]1hKB\u0011AbT\u0005\u0003!6\u00111!\u00118z\u0011\u0015\u0011\u0006A\"\u0001T\u0003\r\t7o[\u000b\u0003)z#2!\u00167n)\t1F\rE\u0002X5rk\u0011\u0001\u0017\u0006\u000336\t!bY8oGV\u0014(/\u001a8u\u0013\tY\u0006L\u0001\u0004GkR,(/\u001a\t\u0003;zc\u0001\u0001B\u0003`#\n\u0007\u0001MA\u0001U#\t\tg\n\u0005\u0002\rE&\u00111-\u0004\u0002\b\u001d>$\b.\u001b8h\u0011\u001d)\u0017+!AA\u0004\u0019\f!\"\u001a<jI\u0016t7-\u001a\u00132!\r9'\u000eX\u0007\u0002Q*\u0011\u0011.D\u0001\be\u00164G.Z2u\u0013\tY\u0007N\u0001\u0005DY\u0006\u001c8\u000fV1h\u0011\u0015i\u0015\u000b1\u0001O\u0011\u0015q\u0017\u000b1\u00014\u0003\u001d!\u0018.\\3pkRDQA\u0015\u0001\u0005\u0002A,\"!];\u0015\u0005ILHCA:w!\r9&\f\u001e\t\u0003;V$QaX8C\u0002\u0001Dqa^8\u0002\u0002\u0003\u000f\u00010\u0001\u0006fm&$WM\\2fII\u00022a\u001a6u\u0011\u0015iu\u000e1\u0001O\u0011\u0015Y\b\u0001\"\u0001}\u0003\u001d\t7o[*z]\u000e,2!`A\u0001)\rq\u0018\u0011\u0002\u000b\u0004\u007f\u0006\r\u0001cA/\u0002\u0002\u0011)qL\u001fb\u0001A\"I\u0011Q\u0001>\u0002\u0002\u0003\u000f\u0011qA\u0001\u000bKZLG-\u001a8dK\u0012\u001a\u0004cA4k\u007f\")QJ\u001fa\u0001\u001d\"11\u0010\u0001C\u0001\u0003\u001b)B!a\u0004\u0002\u0016Q1\u0011\u0011CA\u000f\u0003?!B!a\u0005\u0002\u0018A\u0019Q,!\u0006\u0005\r}\u000bYA1\u0001a\u0011)\tI\"a\u0003\u0002\u0002\u0003\u000f\u00111D\u0001\u000bKZLG-\u001a8dK\u0012\"\u0004\u0003B4k\u0003'Aa!TA\u0006\u0001\u0004q\u0005B\u00028\u0002\f\u0001\u00071\u0007"
)
public abstract class RpcEndpointRef implements Serializable, Logging {
   private final int maxRetries;
   private final long retryWaitMs;
   private final RpcTimeout defaultAskTimeout;
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

   public abstract RpcAddress address();

   public abstract String name();

   public abstract void send(Object var1);

   public abstract <T> Future<T> ask(Object var1, RpcTimeout var2, ClassTag<T> var3);

   public <T> Future<T> ask(Object message, ClassTag<T> evidence$2) {
      return this.<T>ask(message, this.defaultAskTimeout, evidence$2);
   }

   public <T> T askSync(Object message, ClassTag<T> evidence$3) {
      return (T)this.askSync(message, this.defaultAskTimeout, evidence$3);
   }

   public <T> T askSync(Object message, RpcTimeout timeout, ClassTag<T> evidence$4) {
      Future future = this.ask(message, timeout, evidence$4);
      return (T)timeout.awaitResult(future);
   }

   public RpcEndpointRef(SparkConf conf) {
      Logging$class.$init$(this);
      this.maxRetries = RpcUtils$.MODULE$.numRetries(conf);
      this.retryWaitMs = RpcUtils$.MODULE$.retryWaitMs(conf);
      this.defaultAskTimeout = RpcUtils$.MODULE$.askRpcTimeout(conf);
   }
}
