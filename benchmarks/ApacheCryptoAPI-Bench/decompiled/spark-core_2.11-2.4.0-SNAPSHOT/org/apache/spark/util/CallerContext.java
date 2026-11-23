package org.apache.spark.util;

import org.apache.spark.deploy.SparkHadoopUtil$;
import org.apache.spark.internal.Logging;
import org.apache.spark.internal.Logging$class;
import org.slf4j.Logger;
import scala.Function0;
import scala.Option;
import scala.Predef$;
import scala.Serializable;
import scala.StringContext;
import scala.collection.mutable.StringBuilder;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxedUnit;
import scala.runtime.BoxesRunTime;
import scala.runtime.IntRef;
import scala.runtime.TraitSetter;
import scala.runtime.VolatileByteRef;
import scala.util.control.NonFatal$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005=qAB\u0001\u0003\u0011\u0003\u0011!\"A\u0007DC2dWM]\"p]R,\u0007\u0010\u001e\u0006\u0003\u0007\u0011\tA!\u001e;jY*\u0011QAB\u0001\u0006gB\f'o\u001b\u0006\u0003\u000f!\ta!\u00199bG\",'\"A\u0005\u0002\u0007=\u0014x\r\u0005\u0002\f\u00195\t!A\u0002\u0004\u000e\u0005!\u0005!A\u0004\u0002\u000e\u0007\u0006dG.\u001a:D_:$X\r\u001f;\u0014\u00071yQ\u0003\u0005\u0002\u0011'5\t\u0011CC\u0001\u0013\u0003\u0015\u00198-\u00197b\u0013\t!\u0012C\u0001\u0004B]f\u0014VM\u001a\t\u0003-ei\u0011a\u0006\u0006\u00031\u0011\t\u0001\"\u001b8uKJt\u0017\r\\\u0005\u00035]\u0011q\u0001T8hO&tw\rC\u0003\u001d\u0019\u0011\u0005a$\u0001\u0004=S:LGOP\u0002\u0001)\u0005Q\u0001b\u0002\u0011\r\u0005\u0004%\t!I\u0001\u0017G\u0006dG.\u001a:D_:$X\r\u001f;TkB\u0004xN\u001d;fIV\t!\u0005\u0005\u0002\u0011G%\u0011A%\u0005\u0002\b\u0005>|G.Z1o\u0011\u00191C\u0002)A\u0005E\u000592-\u00197mKJ\u001cuN\u001c;fqR\u001cV\u000f\u001d9peR,G\r\t\u0005\bQ1\t\n\u0011\"\u0001*\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%eU\t!F\u000b\u0002,kA\u0019\u0001\u0003\f\u0018\n\u00055\n\"AB(qi&|g\u000e\u0005\u00020e9\u0011\u0001\u0003M\u0005\u0003cE\ta\u0001\u0015:fI\u00164\u0017BA\u001a5\u0005\u0019\u0019FO]5oO*\u0011\u0011'E\u0016\u0002mA\u0011q\u0007P\u0007\u0002q)\u0011\u0011HO\u0001\nk:\u001c\u0007.Z2lK\u0012T!aO\t\u0002\u0015\u0005tgn\u001c;bi&|g.\u0003\u0002>q\t\tRO\\2iK\u000e\\W\r\u001a,be&\fgnY3\t\u000f}b\u0011\u0013!C\u0001S\u0005YB\u0005\\3tg&t\u0017\u000e\u001e\u0013he\u0016\fG/\u001a:%I\u00164\u0017-\u001e7uIMBq!\u0011\u0007\u0012\u0002\u0013\u0005\u0011&A\u000e%Y\u0016\u001c8/\u001b8ji\u0012:'/Z1uKJ$C-\u001a4bk2$H\u0005\u000e\u0005\b\u00072\t\n\u0011\"\u0001E\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%kU\tQI\u000b\u0002GkA\u0019\u0001\u0003L$\u0011\u0005AA\u0015BA%\u0012\u0005\rIe\u000e\u001e\u0005\b\u00172\t\n\u0011\"\u0001E\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%m!9Q\nDI\u0001\n\u0003!\u0015a\u0007\u0013mKN\u001c\u0018N\\5uI\u001d\u0014X-\u0019;fe\u0012\"WMZ1vYR$s\u0007C\u0004P\u0019E\u0005I\u0011\u0001)\u00027\u0011bWm]:j]&$He\u001a:fCR,'\u000f\n3fM\u0006,H\u000e\u001e\u00139+\u0005\t&F\u0001*6!\r\u0001Bf\u0015\t\u0003!QK!!V\t\u0003\t1{gn\u001a\u0005\b/2\t\n\u0011\"\u0001E\u0003m!C.Z:tS:LG\u000fJ4sK\u0006$XM\u001d\u0013eK\u001a\fW\u000f\u001c;%s\u0019)QB\u0001\u0001\u00053N\u0019\u0001lD\u000b\t\u0011mC&\u0011!Q\u0001\n9\nAA\u001a:p[\"AQ\f\u0017B\u0001B\u0003%1&A\u000bvaN$(/Z1n\u0007\u0006dG.\u001a:D_:$X\r\u001f;\t\u0011}C&\u0011!Q\u0001\n-\nQ!\u00199q\u0013\u0012D\u0001\"\u0019-\u0003\u0002\u0003\u0006IaK\u0001\rCB\u0004\u0018\t\u001e;f[B$\u0018\n\u001a\u0005\tGb\u0013\t\u0011)A\u0005\r\u0006)!n\u001c2JI\"AQ\r\u0017B\u0001B\u0003%a)A\u0004ti\u0006<W-\u00133\t\u0011\u001dD&\u0011!Q\u0001\n\u0019\u000bab\u001d;bO\u0016\fE\u000f^3naRLE\r\u0003\u0005j1\n\u0005\t\u0015!\u0003S\u0003\u0019!\u0018m]6JI\"A1\u000e\u0017B\u0001B\u0003%a)A\tuCN\\\u0017\t\u001e;f[B$h*^7cKJDQ\u0001\b-\u0005\u00025$\"B\\8qcJ\u001cH/\u001e<x!\tY\u0001\fC\u0003\\Y\u0002\u0007a\u0006C\u0004^YB\u0005\t\u0019A\u0016\t\u000f}c\u0007\u0013!a\u0001W!9\u0011\r\u001cI\u0001\u0002\u0004Y\u0003bB2m!\u0003\u0005\rA\u0012\u0005\bK2\u0004\n\u00111\u0001G\u0011\u001d9G\u000e%AA\u0002\u0019Cq!\u001b7\u0011\u0002\u0003\u0007!\u000bC\u0004lYB\u0005\t\u0019\u0001$\t\u000feD&\u0019!C\u0005u\u000691m\u001c8uKb$X#\u0001\u0018\t\rqD\u0006\u0015!\u0003/\u0003!\u0019wN\u001c;fqR\u0004\u0003\"\u0002@Y\t\u0013y\u0018A\u00049sKB\f'/Z\"p]R,\u0007\u0010\u001e\u000b\u0004]\u0005\u0005\u0001\"B=~\u0001\u0004q\u0003bBA\u00031\u0012\u0005\u0011qA\u0001\u0012g\u0016$8)\u001e:sK:$8i\u001c8uKb$HCAA\u0005!\r\u0001\u00121B\u0005\u0004\u0003\u001b\t\"\u0001B+oSR\u0004"
)
public class CallerContext implements Logging {
   private final String context;
   private transient Logger org$apache$spark$internal$Logging$$log_;

   public static Option<Object> $lessinit$greater$default$9() {
      return CallerContext$.MODULE$.$lessinit$greater$default$9();
   }

   public static Option<Object> $lessinit$greater$default$8() {
      return CallerContext$.MODULE$.$lessinit$greater$default$8();
   }

   public static Option<Object> $lessinit$greater$default$7() {
      return CallerContext$.MODULE$.$lessinit$greater$default$7();
   }

   public static Option<Object> $lessinit$greater$default$6() {
      return CallerContext$.MODULE$.$lessinit$greater$default$6();
   }

   public static Option<Object> $lessinit$greater$default$5() {
      return CallerContext$.MODULE$.$lessinit$greater$default$5();
   }

   public static Option<String> $lessinit$greater$default$4() {
      return CallerContext$.MODULE$.$lessinit$greater$default$4();
   }

   public static Option<String> $lessinit$greater$default$3() {
      return CallerContext$.MODULE$.$lessinit$greater$default$3();
   }

   public static Option<String> $lessinit$greater$default$2() {
      return CallerContext$.MODULE$.$lessinit$greater$default$2();
   }

   public static boolean callerContextSupported() {
      return CallerContext$.MODULE$.callerContextSupported();
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

   private String context() {
      return this.context;
   }

   private String prepareContext(String context) {
      IntRef len$lzy = IntRef.zero();
      VolatileByteRef bitmap$0 = VolatileByteRef.create((byte)0);
      String var10000;
      if (context != null && context.length() > this.len$1(len$lzy, bitmap$0)) {
         String finalContext = context.substring(0, this.len$1(len$lzy, bitmap$0));
         this.logWarning(new Serializable(this, context, finalContext) {
            public static final long serialVersionUID = 0L;
            private final String context$1;
            private final String finalContext$1;

            public final String apply() {
               return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"Truncated Spark caller context from ", " to ", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{this.context$1, this.finalContext$1}));
            }

            public {
               this.context$1 = context$1;
               this.finalContext$1 = finalContext$1;
            }
         });
         var10000 = finalContext;
      } else {
         var10000 = context;
      }

      return var10000;
   }

   public void setCurrentContext() {
      if (CallerContext$.MODULE$.callerContextSupported()) {
         try {
            Class callerContext = Utils$.MODULE$.classForName("org.apache.hadoop.ipc.CallerContext");
            Class builder = Utils$.MODULE$.classForName("org.apache.hadoop.ipc.CallerContext$Builder");
            Object builderInst = builder.getConstructor(String.class).newInstance(this.context());
            Object hdfsContext = builder.getMethod("build").invoke(builderInst);
            callerContext.getMethod("setCurrent", callerContext).invoke((Object)null, hdfsContext);
         } catch (Throwable var11) {
            Option var3 = NonFatal$.MODULE$.unapply(var11);
            if (var3.isEmpty()) {
               throw var11;
            }

            Throwable e = (Throwable)var3.get();
            this.logWarning(new Serializable(this) {
               public static final long serialVersionUID = 0L;

               public final String apply() {
                  return "Fail to set Spark caller context";
               }
            }, e);
            BoxedUnit var5 = BoxedUnit.UNIT;
            return;
         }
      }

   }

   private final int len$lzycompute$1(IntRef len$lzy$1, VolatileByteRef bitmap$0$1) {
      synchronized(this) {
         if ((byte)(bitmap$0$1.elem & 1) == 0) {
            len$lzy$1.elem = SparkHadoopUtil$.MODULE$.get().conf().getInt("hadoop.caller.context.max.size", 128);
            bitmap$0$1.elem = (byte)(bitmap$0$1.elem | 1);
         }

         BoxedUnit var10000 = BoxedUnit.UNIT;
      }

      return len$lzy$1.elem;
   }

   private final int len$1(IntRef len$lzy$1, VolatileByteRef bitmap$0$1) {
      return (byte)(bitmap$0$1.elem & 1) == 0 ? this.len$lzycompute$1(len$lzy$1, bitmap$0$1) : len$lzy$1.elem;
   }

   public CallerContext(String from, Option<String> upstreamCallerContext, Option<String> appId, Option<String> appAttemptId, Option<Object> jobId, Option<Object> stageId, Option<Object> stageAttemptId, Option<Object> taskId, Option<Object> taskAttemptNumber) {
      Logging$class.$init$(this);
      this.context = this.prepareContext((new StringBuilder()).append((Object)"SPARK_").append((Object)from).append(appId.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$31) {
            return (new StringBuilder()).append((Object)"_").append((Object)x$31).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).append(appAttemptId.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$32) {
            return (new StringBuilder()).append((Object)"_").append((Object)x$32).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).append(jobId.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(int x$33) {
            return (new StringBuilder()).append((Object)"_JId_").append((Object)BoxesRunTime.boxToInteger(x$33)).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).append(stageId.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(int x$34) {
            return (new StringBuilder()).append((Object)"_SId_").append((Object)BoxesRunTime.boxToInteger(x$34)).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).append(stageAttemptId.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(int x$35) {
            return (new StringBuilder()).append((Object)"_").append((Object)BoxesRunTime.boxToInteger(x$35)).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).append(taskId.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(long x$36) {
            return (new StringBuilder()).append((Object)"_TId_").append((Object)BoxesRunTime.boxToLong(x$36)).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).append(taskAttemptNumber.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(int x$37) {
            return (new StringBuilder()).append((Object)"_").append((Object)BoxesRunTime.boxToInteger(x$37)).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).append(upstreamCallerContext.map(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply(String x$38) {
            return (new StringBuilder()).append((Object)"_").append((Object)x$38).toString();
         }
      }).getOrElse(new Serializable(this) {
         public static final long serialVersionUID = 0L;

         public final String apply() {
            return "";
         }
      })).toString());
   }
}
