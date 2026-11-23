package org.apache.spark;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u000153Q!\u0001\u0002\u0001\u0005!\u0011Q\u0004\u00165s_^\f'\r\\3TKJL\u0017\r\\5{CRLwN\\,sCB\u0004XM\u001d\u0006\u0003\u0007\u0011\tQa\u001d9be.T!!\u0002\u0004\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u00059\u0011aA8sON!\u0001!C\b\u0013!\tQQ\"D\u0001\f\u0015\u0005a\u0011!B:dC2\f\u0017B\u0001\b\f\u0005\u0019\te.\u001f*fMB\u0011!\u0002E\u0005\u0003#-\u0011AbU3sS\u0006d\u0017N_1cY\u0016\u0004\"a\u0005\f\u000e\u0003QQ!!\u0006\u0002\u0002\u0011%tG/\u001a:oC2L!a\u0006\u000b\u0003\u000f1{wmZ5oO\"A\u0011\u0004\u0001BA\u0002\u0013\u00051$A\u0005fq\u000e,\u0007\u000f^5p]\u000e\u0001Q#\u0001\u000f\u0011\u0005u)cB\u0001\u0010$\u001d\ty\"%D\u0001!\u0015\t\t#$\u0001\u0004=e>|GOP\u0005\u0002\u0019%\u0011AeC\u0001\ba\u0006\u001c7.Y4f\u0013\t1sEA\u0005UQJ|w/\u00192mK*\u0011Ae\u0003\u0005\tS\u0001\u0011\t\u0019!C\u0001U\u0005iQ\r_2faRLwN\\0%KF$\"a\u000b\u0018\u0011\u0005)a\u0013BA\u0017\f\u0005\u0011)f.\u001b;\t\u000f=B\u0013\u0011!a\u00019\u0005\u0019\u0001\u0010J\u0019\t\u0011E\u0002!\u0011!Q!\nq\t!\"\u001a=dKB$\u0018n\u001c8!\u0011\u0015\u0019\u0004\u0001\"\u00015\u0003\u0019a\u0014N\\5u}Q\u0011Qg\u000e\t\u0003m\u0001i\u0011A\u0001\u0005\u00063I\u0002\r\u0001\b\u0005\u0006s\u0001!IAO\u0001\foJLG/Z(cU\u0016\u001cG\u000f\u0006\u0002,w!)A\b\u000fa\u0001{\u0005\u0019q.\u001e;\u0011\u0005y\u001aU\"A \u000b\u0005\u0001\u000b\u0015AA5p\u0015\u0005\u0011\u0015\u0001\u00026bm\u0006L!\u0001R \u0003%=\u0013'.Z2u\u001fV$\b/\u001e;TiJ,\u0017-\u001c\u0005\u0006\r\u0002!IaR\u0001\u000be\u0016\fGm\u00142kK\u000e$HCA\u0016I\u0011\u0015IU\t1\u0001K\u0003\tIg\u000e\u0005\u0002?\u0017&\u0011Aj\u0010\u0002\u0012\u001f\nTWm\u0019;J]B,Ho\u0015;sK\u0006l\u0007"
)
public class ThrowableSerializationWrapper implements Serializable, Logging {
   private Throwable exception;
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

   public Throwable exception() {
      return this.exception;
   }

   public void exception_$eq(Throwable x$1) {
      this.exception = x$1;
   }

   private void writeObject(ObjectOutputStream out) {
      out.writeObject(this.exception());
   }

   private void readObject(ObjectInputStream in) {
      try {
         this.exception_$eq((Throwable)in.readObject());
      } catch (Exception var3) {
         this.log().warn("Task exception could not be deserialized", var3);
      }

   }

   public ThrowableSerializationWrapper(Throwable exception) {
      this.exception = exception;
      super();
      Logging$class.$init$(this);
   }
}
