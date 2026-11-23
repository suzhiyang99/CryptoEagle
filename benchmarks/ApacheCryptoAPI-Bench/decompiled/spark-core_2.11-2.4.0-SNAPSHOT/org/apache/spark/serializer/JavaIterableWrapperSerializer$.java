package org.apache.spark.serializer;

import java.lang.reflect.Method;
import java.util.List;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.collection.JavaConverters$;
import scala.collection.Seq;
import scala.collection.Seq$;
import scala.collection.mutable.StringBuilder;
import scala.runtime.TraitSetter;

public final class JavaIterableWrapperSerializer$ implements Logging {
   public static final JavaIterableWrapperSerializer$ MODULE$;
   private final Class<? extends List<Object>> wrapperClass;
   private final Option<Method> org$apache$spark$serializer$JavaIterableWrapperSerializer$$underlyingMethodOpt;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new JavaIterableWrapperSerializer$();
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

   public Class<? extends List<Object>> wrapperClass() {
      return this.wrapperClass;
   }

   public Option<Method> org$apache$spark$serializer$JavaIterableWrapperSerializer$$underlyingMethodOpt() {
      return this.org$apache$spark$serializer$JavaIterableWrapperSerializer$$underlyingMethodOpt;
   }

   private final Option liftedTree1$1() {
      Object var10000;
      try {
         var10000 = new Some(this.wrapperClass().getDeclaredMethod("underlying"));
      } catch (Exception var2) {
         this.logError(new Serializable() {
            public static final long serialVersionUID = 0L;

            public final String apply() {
               return (new StringBuilder()).append((Object)"Failed to find the underlying field in ").append((Object)JavaIterableWrapperSerializer$.MODULE$.wrapperClass()).toString();
            }
         }, var2);
         var10000 = None$.MODULE$;
      }

      return (Option)var10000;
   }

   private JavaIterableWrapperSerializer$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.wrapperClass = JavaConverters$.MODULE$.seqAsJavaListConverter((Seq)Seq$.MODULE$.apply(Predef$.MODULE$.wrapIntArray(new int[]{1}))).asJava().getClass();
      this.org$apache$spark$serializer$JavaIterableWrapperSerializer$$underlyingMethodOpt = this.liftedTree1$1();
   }
}
