package org.apache.spark.util;

import org.apache.commons.lang3.SystemUtils;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.IterableLike;
import scala.collection.Seq$;
import scala.collection.immutable.Nil$;
import scala.collection.mutable.HashMap;
import scala.collection.mutable.StringBuilder;
import scala.runtime.TraitSetter;
import sun.misc.Signal;

public final class SignalUtils$ implements Logging {
   public static final SignalUtils$ MODULE$;
   private boolean loggerRegistered;
   private final HashMap<String, SignalUtils.ActionHandler> handlers;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new SignalUtils$();
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

   private boolean loggerRegistered() {
      return this.loggerRegistered;
   }

   private void loggerRegistered_$eq(boolean x$1) {
      this.loggerRegistered = x$1;
   }

   public synchronized void registerLogger(Logger log) {
      if (!this.loggerRegistered()) {
         ((IterableLike)Seq$.MODULE$.apply(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"TERM", "HUP", "INT"})))).foreach(new Serializable(log) {
            public static final long serialVersionUID = 0L;
            public final Logger log$1;

            public final void apply(String sig) {
               SignalUtils$.MODULE$.register(sig, new Serializable(this, sig) {
                  public static final long serialVersionUID = 0L;
                  // $FF: synthetic field
                  private final <undefinedtype> $outer;
                  private final String sig$1;

                  public final boolean apply() {
                     return this.apply$mcZ$sp();
                  }

                  public boolean apply$mcZ$sp() {
                     this.$outer.log$1.error((new StringBuilder()).append((Object)"RECEIVED SIGNAL ").append((Object)this.sig$1).toString());
                     return false;
                  }

                  public {
                     if ($outer == null) {
                        throw null;
                     } else {
                        this.$outer = $outer;
                        this.sig$1 = sig$1;
                     }
                  }
               });
            }

            public {
               this.log$1 = log$1;
            }
         });
         this.loggerRegistered_$eq(true);
      }

   }

   public synchronized void register(String signal, Function0<Object> action) {
      if (SystemUtils.IS_OS_UNIX) {
         try {
            SignalUtils.ActionHandler handler = (SignalUtils.ActionHandler)this.handlers().getOrElseUpdate(signal, new Serializable(signal) {
               public static final long serialVersionUID = 0L;
               public final String signal$1;

               public final SignalUtils.ActionHandler apply() {
                  SignalUtils$.MODULE$.logInfo(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final String apply() {
                        return (new StringBuilder()).append((Object)"Registered signal handler for ").append((Object)this.$outer.signal$1).toString();
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  return new SignalUtils.ActionHandler(new Signal(this.signal$1));
               }

               public {
                  this.signal$1 = signal$1;
               }
            });
            handler.register(action);
         } catch (Exception var5) {
            this.logWarning(new Serializable(signal) {
               public static final long serialVersionUID = 0L;
               private final String signal$1;

               public final String apply() {
                  return (new StringBuilder()).append((Object)(new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to register signal handler for "})))).s(Nil$.MODULE$)).append((Object)this.signal$1).toString();
               }

               public {
                  this.signal$1 = signal$1;
               }
            }, var5);
         }
      }

   }

   private HashMap<String, SignalUtils.ActionHandler> handlers() {
      return this.handlers;
   }

   private SignalUtils$() {
      MODULE$ = this;
      Logging$class.$init$(this);
      this.loggerRegistered = false;
      this.handlers = new HashMap<String, SignalUtils.ActionHandler>();
   }
}
