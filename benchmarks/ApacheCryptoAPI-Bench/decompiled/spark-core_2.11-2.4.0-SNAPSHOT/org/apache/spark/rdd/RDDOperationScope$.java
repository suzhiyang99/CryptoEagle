package org.apache.spark.rdd;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.scala.DefaultScalaModule.;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.spark.SparkContext;
import org.apache.spark.SparkContext$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.None$;
import scala.Option;
import scala.Option$;
import scala.Predef$;
import scala.Serializable;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

public final class RDDOperationScope$ implements Logging {
   public static final RDDOperationScope$ MODULE$;
   private final ObjectMapper org$apache$spark$rdd$RDDOperationScope$$jsonMapper;
   private final AtomicInteger scopeCounter;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new RDDOperationScope$();
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

   public ObjectMapper org$apache$spark$rdd$RDDOperationScope$$jsonMapper() {
      return this.org$apache$spark$rdd$RDDOperationScope$$jsonMapper;
   }

   private AtomicInteger scopeCounter() {
      return this.scopeCounter;
   }

   public RDDOperationScope fromJson(String s) {
      return (RDDOperationScope)this.org$apache$spark$rdd$RDDOperationScope$$jsonMapper().readValue(s, RDDOperationScope.class);
   }

   public int nextScopeId() {
      return this.scopeCounter().getAndIncrement();
   }

   public <T> T withScope(SparkContext sc, boolean allowNesting, Function0<T> body) {
      String ourMethodName = "withScope";
      String callerMethodName = (String)Predef$.MODULE$.refArrayOps(Predef$.MODULE$.refArrayOps((Object[])Thread.currentThread().getStackTrace()).dropWhile(new Serializable(ourMethodName) {
         public static final long serialVersionUID = 0L;
         private final String ourMethodName$1;

         public final boolean apply(StackTraceElement x$2) {
            boolean var3;
            label23: {
               String var10000 = x$2.getMethodName();
               String var2 = this.ourMethodName$1;
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = true;
               return var3;
            }

            var3 = false;
            return var3;
         }

         public {
            this.ourMethodName$1 = ourMethodName$1;
         }
      })).find(new Serializable(ourMethodName) {
         public static final long serialVersionUID = 0L;
         private final String ourMethodName$1;

         public final boolean apply(StackTraceElement x$3) {
            boolean var3;
            label23: {
               String var10000 = x$3.getMethodName();
               String var2 = this.ourMethodName$1;
               if (var10000 == null) {
                  if (var2 == null) {
                     break label23;
                  }
               } else if (var10000.equals(var2)) {
                  break label23;
               }

               var3 = true;
               return var3;
            }

            var3 = false;
            return var3;
         }

         public {
            this.ourMethodName$1 = ourMethodName$1;
         }
      }).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply(StackTraceElement x$4) {
            return x$4.getMethodName();
         }
      }).getOrElse(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            RDDOperationScope$.MODULE$.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "No valid method name for this RDD operation scope!";
               }
            });
            return "N/A";
         }
      });
      return (T)this.withScope(sc, callerMethodName, allowNesting, false, body);
   }

   public <T> T withScope(SparkContext sc, String name, boolean allowNesting, boolean ignoreParent, Function0<T> body) {
      String scopeKey = SparkContext$.MODULE$.RDD_SCOPE_KEY();
      String noOverrideKey = SparkContext$.MODULE$.RDD_SCOPE_NO_OVERRIDE_KEY();
      String oldScopeJson = sc.getLocalProperty(scopeKey);
      Option oldScope = Option$.MODULE$.apply(oldScopeJson).map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final RDDOperationScope apply(String s) {
            return RDDOperationScope$.MODULE$.fromJson(s);
         }
      });
      String oldNoOverride = sc.getLocalProperty(noOverrideKey);

      Object var10000;
      try {
         if (ignoreParent) {
            sc.setLocalProperty(scopeKey, (new RDDOperationScope(name, this.$lessinit$greater$default$2(), this.$lessinit$greater$default$3())).toJson());
         } else if (sc.getLocalProperty(noOverrideKey) == null) {
            sc.setLocalProperty(scopeKey, (new RDDOperationScope(name, oldScope, this.$lessinit$greater$default$3())).toJson());
         }

         if (!allowNesting) {
            sc.setLocalProperty(noOverrideKey, "true");
         }

         var10000 = body.apply();
      } finally {
         sc.setLocalProperty(scopeKey, oldScopeJson);
         sc.setLocalProperty(noOverrideKey, oldNoOverride);
      }

      return (T)var10000;
   }

   public <T> boolean withScope$default$2() {
      return false;
   }

   public Option<RDDOperationScope> $lessinit$greater$default$2() {
      return None$.MODULE$;
   }

   public String $lessinit$greater$default$3() {
      return BoxesRunTime.boxToInteger(this.nextScopeId()).toString();
   }

   private RDDOperationScope$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.org$apache$spark$rdd$RDDOperationScope$$jsonMapper = (new ObjectMapper()).registerModule(.MODULE$);
      this.scopeCounter = new AtomicInteger(0);
   }
}
