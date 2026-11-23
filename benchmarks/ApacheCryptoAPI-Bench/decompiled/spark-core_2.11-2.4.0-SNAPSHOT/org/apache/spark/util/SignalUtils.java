package org.apache.spark.util;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import scala.Function0;
import scala.Serializable;
import scala.collection.IterableLike;
import scala.collection.JavaConverters$;
import scala.collection.TraversableLike;
import scala.collection.mutable.Buffer$;
import scala.reflect.ScalaSignature;
import sun.misc.Signal;
import sun.misc.SignalHandler;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005}qAB\u0001\u0003\u0011\u0003!!\"A\u0006TS\u001et\u0017\r\\+uS2\u001c(BA\u0002\u0005\u0003\u0011)H/\u001b7\u000b\u0005\u00151\u0011!B:qCJ\\'BA\u0004\t\u0003\u0019\t\u0007/Y2iK*\t\u0011\"A\u0002pe\u001e\u0004\"a\u0003\u0007\u000e\u0003\t1a!\u0004\u0002\t\u0002\u0011q!aC*jO:\fG.\u0016;jYN\u001c2\u0001D\b\u0016!\t\u00012#D\u0001\u0012\u0015\u0005\u0011\u0012!B:dC2\f\u0017B\u0001\u000b\u0012\u0005\u0019\te.\u001f*fMB\u0011a#G\u0007\u0002/)\u0011\u0001\u0004B\u0001\tS:$XM\u001d8bY&\u0011!d\u0006\u0002\b\u0019><w-\u001b8h\u0011\u0015aB\u0002\"\u0001\u001f\u0003\u0019a\u0014N\\5u}\r\u0001A#\u0001\u0006\t\u000f\u0001b\u0001\u0019!C\u0005C\u0005\u0001Bn\\4hKJ\u0014VmZ5ti\u0016\u0014X\rZ\u000b\u0002EA\u0011\u0001cI\u0005\u0003IE\u0011qAQ8pY\u0016\fg\u000eC\u0004'\u0019\u0001\u0007I\u0011B\u0014\u0002)1|wmZ3s%\u0016<\u0017n\u001d;fe\u0016$w\fJ3r)\tA3\u0006\u0005\u0002\u0011S%\u0011!&\u0005\u0002\u0005+:LG\u000fC\u0004-K\u0005\u0005\t\u0019\u0001\u0012\u0002\u0007a$\u0013\u0007\u0003\u0004/\u0019\u0001\u0006KAI\u0001\u0012Y><w-\u001a:SK\u001eL7\u000f^3sK\u0012\u0004\u0003\"\u0002\u0019\r\t\u0003\t\u0014A\u0004:fO&\u001cH/\u001a:M_\u001e<WM\u001d\u000b\u0003QIBQaM\u0018A\u0002Q\n1\u0001\\8h!\t)\u0004(D\u00017\u0015\t9\u0004\"A\u0003tY\u001a$$.\u0003\u0002:m\t1Aj\\4hKJDQa\u000f\u0007\u0005\u0002q\n\u0001B]3hSN$XM\u001d\u000b\u0003{\r#\"\u0001\u000b \t\r}RD\u00111\u0001A\u0003\u0019\t7\r^5p]B\u0019\u0001#\u0011\u0012\n\u0005\t\u000b\"\u0001\u0003\u001fcs:\fW.\u001a \t\u000b\u0011S\u0004\u0019A#\u0002\rMLwM\\1m!\t1\u0015J\u0004\u0002\u0011\u000f&\u0011\u0001*E\u0001\u0007!J,G-\u001a4\n\u0005)[%AB*ue&twM\u0003\u0002I#\u0019!Q\n\u0004\u0003O\u00055\t5\r^5p]\"\u000bg\u000e\u001a7feN\u0019AjT,\u0011\u0005A+V\"A)\u000b\u0005I\u001b\u0016\u0001\u00027b]\u001eT\u0011\u0001V\u0001\u0005U\u00064\u0018-\u0003\u0002W#\n1qJ\u00196fGR\u0004\"\u0001W/\u000e\u0003eS!AW.\u0002\t5L7o\u0019\u0006\u00029\u0006\u00191/\u001e8\n\u0005yK&!D*jO:\fG\u000eS1oI2,'\u000f\u0003\u0005E\u0019\n\u0005\t\u0015!\u0003a!\tA\u0016-\u0003\u0002c3\n11+[4oC2DQ\u0001\b'\u0005\u0002\u0011$\"!Z4\u0011\u0005\u0019dU\"\u0001\u0007\t\u000b\u0011\u001b\u0007\u0019\u00011\t\u000f%d%\u0019!C\u0005U\u00069\u0011m\u0019;j_:\u001cX#A6\u0011\u00071t\u0007/D\u0001n\u0015\t\u00191+\u0003\u0002p[\n!A*[:u!\r\u0001\u0012OI\u0005\u0003eF\u0011\u0011BR;oGRLwN\u001c\u0019\t\rQd\u0005\u0015!\u0003l\u0003!\t7\r^5p]N\u0004\u0003b\u0002<M\u0005\u0004%Ia^\u0001\faJ,g\u000fS1oI2,'/F\u0001X\u0011\u0019IH\n)A\u0005/\u0006a\u0001O]3w\u0011\u0006tG\r\\3sA!)1\u0010\u0014C!y\u00061\u0001.\u00198eY\u0016$\"\u0001K?\t\u000byT\b\u0019\u00011\u0002\u0007MLw\r\u0003\u0004<\u0019\u0012\u0005\u0011\u0011\u0001\u000b\u0004Q\u0005\r\u0001BB \u0000\t\u0003\u0007\u0001\tC\u0005\u0002\b1\u0011\r\u0011\"\u0003\u0002\n\u0005A\u0001.\u00198eY\u0016\u00148/\u0006\u0002\u0002\fA1\u0011QBA\f\u000b\u0016l!!a\u0004\u000b\t\u0005E\u00111C\u0001\b[V$\u0018M\u00197f\u0015\r\t)\"E\u0001\u000bG>dG.Z2uS>t\u0017\u0002BA\r\u0003\u001f\u0011q\u0001S1tQ6\u000b\u0007\u000f\u0003\u0005\u0002\u001e1\u0001\u000b\u0011BA\u0006\u0003%A\u0017M\u001c3mKJ\u001c\b\u0005"
)
public final class SignalUtils {
   public static boolean initializeLogIfNecessary$default$2() {
      return SignalUtils$.MODULE$.initializeLogIfNecessary$default$2();
   }

   public static boolean initializeLogIfNecessary(boolean var0, boolean var1) {
      return SignalUtils$.MODULE$.initializeLogIfNecessary(var0, var1);
   }

   public static void initializeLogIfNecessary(boolean var0) {
      SignalUtils$.MODULE$.initializeLogIfNecessary(var0);
   }

   public static boolean isTraceEnabled() {
      return SignalUtils$.MODULE$.isTraceEnabled();
   }

   public static void logError(Function0<String> var0, Throwable var1) {
      SignalUtils$.MODULE$.logError(var0, var1);
   }

   public static void logWarning(Function0<String> var0, Throwable var1) {
      SignalUtils$.MODULE$.logWarning(var0, var1);
   }

   public static void logTrace(Function0<String> var0, Throwable var1) {
      SignalUtils$.MODULE$.logTrace(var0, var1);
   }

   public static void logDebug(Function0<String> var0, Throwable var1) {
      SignalUtils$.MODULE$.logDebug(var0, var1);
   }

   public static void logInfo(Function0<String> var0, Throwable var1) {
      SignalUtils$.MODULE$.logInfo(var0, var1);
   }

   public static void logError(Function0<String> var0) {
      SignalUtils$.MODULE$.logError(var0);
   }

   public static void logWarning(Function0<String> var0) {
      SignalUtils$.MODULE$.logWarning(var0);
   }

   public static void logTrace(Function0<String> var0) {
      SignalUtils$.MODULE$.logTrace(var0);
   }

   public static void logDebug(Function0<String> var0) {
      SignalUtils$.MODULE$.logDebug(var0);
   }

   public static void logInfo(Function0<String> var0) {
      SignalUtils$.MODULE$.logInfo(var0);
   }

   public static Logger log() {
      return SignalUtils$.MODULE$.log();
   }

   public static String logName() {
      return SignalUtils$.MODULE$.logName();
   }

   public static void register(String var0, Function0<Object> var1) {
      SignalUtils$.MODULE$.register(var0, var1);
   }

   public static void registerLogger(Logger var0) {
      SignalUtils$.MODULE$.registerLogger(var0);
   }

   public static class ActionHandler implements SignalHandler {
      private final Signal signal;
      private final List<Function0<Object>> actions;
      private final SignalHandler prevHandler;

      private List<Function0<Object>> actions() {
         return this.actions;
      }

      private SignalHandler prevHandler() {
         return this.prevHandler;
      }

      public void handle(Signal sig) {
         Signal.handle(this.signal, this.prevHandler());
         boolean escalate = ((IterableLike)((TraversableLike)JavaConverters$.MODULE$.asScalaBufferConverter(this.actions()).asScala()).map(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(Function0<Object> action) {
               return action.apply$mcZ$sp();
            }
         }, Buffer$.MODULE$.canBuildFrom())).forall(new Serializable(this) {
            public static final long serialVersionUID = 0L;

            public final boolean apply(boolean x$1) {
               return !x$1;
            }
         });
         if (escalate) {
            this.prevHandler().handle(sig);
         }

         Signal.handle(this.signal, this);
      }

      public void register(Function0<Object> action) {
         this.actions().add(action);
      }

      public ActionHandler(Signal signal) {
         this.signal = signal;
         this.actions = Collections.synchronizedList(new LinkedList());
         this.prevHandler = Signal.handle(signal, this);
      }
   }
}
