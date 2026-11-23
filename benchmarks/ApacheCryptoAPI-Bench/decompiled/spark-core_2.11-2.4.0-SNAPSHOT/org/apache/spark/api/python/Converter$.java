package org.apache.spark.api.python;

import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.apache.spark.util.Utils$;
import org.slf4j.Logger;
import scala.Function0;
import scala.MatchError;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.util.Failure;
import scala.util.Success;
import scala.util.Try;
import scala.util.Try$;

public final class Converter$ implements Logging, Serializable {
   public static final Converter$ MODULE$;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   static {
      new Converter$();
   }

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

   public Converter<Object, Object> getInstance(Option<String> converterClass, Converter<Object, Object> defaultConverter) {
      return (Converter)converterClass.map(new Serializable() {
         public static final long serialVersionUID = 0L;

         public final Converter<Object, Object> apply(String cc) {
            Try var2 = Try$.MODULE$.apply(new Serializable(this, cc) {
               public static final long serialVersionUID = 0L;
               public final String cc$1;

               public final Converter<Object, Object> apply() {
                  Converter c = (Converter)Utils$.MODULE$.classForName(this.cc$1).newInstance();
                  Converter$.MODULE$.logInfo(new Serializable(this) {
                     public static final long serialVersionUID = 0L;
                     // $FF: synthetic field
                     private final <undefinedtype> $outer;

                     public final String apply() {
                        return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Loaded converter: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.$outer.cc$1}));
                     }

                     public {
                        if ($outer == null) {
                           throw null;
                        } else {
                           this.$outer = $outer;
                        }
                     }
                  });
                  return c;
               }

               public {
                  this.cc$1 = cc$1;
               }
            });
            if (var2 instanceof Success) {
               Success var3 = (Success)var2;
               Converter c = (Converter)var3.value();
               return c;
            } else if (var2 instanceof Failure) {
               Failure var6 = (Failure)var2;
               Throwable err = var6.exception();
               Converter$.MODULE$.logError(new Serializable(this, cc) {
                  public static final long serialVersionUID = 0L;
                  private final String cc$1;

                  public final String apply() {
                     return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Failed to load converter: ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.cc$1}));
                  }

                  public {
                     this.cc$1 = cc$1;
                  }
               });
               throw err;
            } else {
               throw new MatchError(var2);
            }
         }
      }).getOrElse(new Serializable(defaultConverter) {
         public static final long serialVersionUID = 0L;
         private final Converter defaultConverter$1;

         public final Converter<Object, Object> apply() {
            return this.defaultConverter$1;
         }

         public {
            this.defaultConverter$1 = defaultConverter$1;
         }
      });
   }

   private Object readResolve() {
      return MODULE$;
   }

   private Converter$() {
      MODULE$ = this;
      Logging$class.$init$(this);
   }
}
