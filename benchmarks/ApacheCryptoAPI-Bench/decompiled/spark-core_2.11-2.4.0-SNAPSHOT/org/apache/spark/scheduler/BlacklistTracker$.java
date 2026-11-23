package org.apache.spark.scheduler;

import org.apache.spark.SparkConf;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.internal.config.ConfigEntry;
import org.apache.spark.util.Clock;
import org.apache.spark.util.SystemClock;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.None$;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.Some;
import scala.StringContext;
import scala.collection.IterableLike;
import scala.collection.Seq$;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.StringBuilder;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.TraitSetter;

public final class BlacklistTracker$ implements Logging {
   public static final BlacklistTracker$ MODULE$;
   private final String org$apache$spark$scheduler$BlacklistTracker$$DEFAULT_TIMEOUT;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new BlacklistTracker$();
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

   public String org$apache$spark$scheduler$BlacklistTracker$$DEFAULT_TIMEOUT() {
      return this.org$apache$spark$scheduler$BlacklistTracker$$DEFAULT_TIMEOUT;
   }

   public boolean isBlacklistEnabled(SparkConf conf) {
      Option var2 = (Option)conf.get((ConfigEntry)org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_ENABLED());
      boolean var5;
      if (var2 instanceof Some) {
         Some var3 = (Some)var2;
         boolean enabled = BoxesRunTime.unboxToBoolean(var3.x());
         var5 = enabled;
      } else {
         if (!None$.MODULE$.equals(var2)) {
            throw new MatchError(var2);
         }

         String legacyKey = org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_LEGACY_TIMEOUT_CONF().key();
         var5 = ((Option)conf.get((ConfigEntry)org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_LEGACY_TIMEOUT_CONF())).exists(new Serializable(legacyKey) {
            public static final long serialVersionUID = 0L;
            public final String legacyKey$1;

            public final boolean apply(long legacyTimeout) {
               return this.apply$mcZJ$sp(legacyTimeout);
            }

            public boolean apply$mcZJ$sp(long legacyTimeout) {
               boolean var10000;
               if (legacyTimeout == 0L) {
                  BlacklistTracker$.MODULE$.logWarning(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Turning off blacklisting due to legacy configuration: ", " == 0"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.legacyKey$1}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  var10000 = false;
               } else {
                  BlacklistTracker$.MODULE$.logWarning(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Turning on blacklisting due to legacy configuration: ", " > 0"})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.legacyKey$1}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  var10000 = true;
               }

               return var10000;
            }

            public {
               this.legacyKey$1 = legacyKey$1;
            }
         });
      }

      return var5;
   }

   public long getBlacklistTimeout(SparkConf conf) {
      return BoxesRunTime.unboxToLong(((Option)conf.get((ConfigEntry)org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_TIMEOUT_CONF())).getOrElse(new Serializable(conf) {
         public static final long serialVersionUID = 0L;
         private final SparkConf conf$1;

         public final long apply() {
            return this.apply$mcJ$sp();
         }

         public long apply$mcJ$sp() {
            return BoxesRunTime.unboxToLong(((Option)this.conf$1.get((ConfigEntry)org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_LEGACY_TIMEOUT_CONF())).getOrElse(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final long apply() {
                  return this.apply$mcJ$sp();
               }

               public long apply$mcJ$sp() {
                  return Utils$.MODULE$.timeStringAsMs(BlacklistTracker$.MODULE$.org$apache$spark$scheduler$BlacklistTracker$$DEFAULT_TIMEOUT());
               }
            }));
         }

         public {
            this.conf$1 = conf$1;
         }
      }));
   }

   public void validateBlacklistConfs(SparkConf conf) {
      ((IterableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray(new ConfigEntry[]{org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_ATTEMPTS_PER_EXECUTOR(), org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_ATTEMPTS_PER_NODE(), org.apache.spark.internal.config.package$.MODULE$.MAX_FAILURES_PER_EXEC_STAGE(), org.apache.spark.internal.config.package$.MODULE$.MAX_FAILED_EXEC_PER_NODE_STAGE(), org.apache.spark.internal.config.package$.MODULE$.MAX_FAILURES_PER_EXEC(), org.apache.spark.internal.config.package$.MODULE$.MAX_FAILED_EXEC_PER_NODE()}))).foreach(new Serializable(conf) {
         public static final long serialVersionUID = 0L;
         private final SparkConf conf$2;

         public final void apply(ConfigEntry<Object> config) {
            int v = BoxesRunTime.unboxToInt(this.conf$2.get(config));
            if (v <= 0) {
               BlacklistTracker$.MODULE$.org$apache$spark$scheduler$BlacklistTracker$$mustBePos$1(config.key(), BoxesRunTime.boxToInteger(v).toString());
            }

         }

         public {
            this.conf$2 = conf$2;
         }
      });
      long timeout = this.getBlacklistTimeout(conf);
      if (timeout <= 0L) {
         Option var4 = (Option)conf.get((ConfigEntry)org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_TIMEOUT_CONF());
         if (var4 instanceof Some) {
            this.org$apache$spark$scheduler$BlacklistTracker$$mustBePos$1(org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_TIMEOUT_CONF().key(), BoxesRunTime.boxToLong(timeout).toString());
            BoxedUnit var5 = BoxedUnit.UNIT;
         } else {
            if (!None$.MODULE$.equals(var4)) {
               throw new MatchError(var4);
            }

            this.org$apache$spark$scheduler$BlacklistTracker$$mustBePos$1(org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_LEGACY_TIMEOUT_CONF().key(), BoxesRunTime.boxToLong(timeout).toString());
            BoxedUnit var8 = BoxedUnit.UNIT;
         }
      }

      int maxTaskFailures = BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_FAILURES()));
      int maxNodeAttempts = BoxesRunTime.unboxToInt(conf.get(org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_ATTEMPTS_PER_NODE()));
      if (maxNodeAttempts >= maxTaskFailures) {
         throw new IllegalArgumentException((new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_ATTEMPTS_PER_NODE().key()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"( = ", ") was >= ", " "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(maxNodeAttempts), org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_FAILURES().key()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"( = ", " ).  Though blacklisting is enabled, with this configuration, "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{BoxesRunTime.boxToInteger(maxTaskFailures)}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Spark will not be robust to one bad node.  Decrease "})))).s(Nil$.MODULE$)).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", ", increase ", ", "})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_ATTEMPTS_PER_NODE().key(), org.apache.spark.internal.config.package$.MODULE$.MAX_TASK_FAILURES().key()}))).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"or disable blacklisting with ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{org.apache.spark.internal.config.package$.MODULE$.BLACKLIST_ENABLED().key()}))).toString());
      }
   }

   public Clock $lessinit$greater$default$4() {
      return new SystemClock();
   }

   public final void org$apache$spark$scheduler$BlacklistTracker$$mustBePos$1(String k, String v) {
      throw new IllegalArgumentException((new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", " was ", ", but must be > 0."})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{k, v})));
   }

   private BlacklistTracker$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.org$apache$spark$scheduler$BlacklistTracker$$DEFAULT_TIMEOUT = "1h";
   }
}
