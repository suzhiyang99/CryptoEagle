package org.apache.spark.rpc;

import java.util.concurrent.TimeoutException;
import org.apache.spark.SparkConf;
import org.apache.spark.util.ThreadUtils$;
import scala.Function1;
import scala.PartialFunction;
import scala.Serializable;
import scala.collection.Seq;
import scala.collection.mutable.StringBuilder;
import scala.concurrent.Future;
import scala.concurrent.duration.FiniteDuration;
import scala.reflect.ScalaSignature;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005Eb!B\u0001\u0003\u0001\u0011Q!A\u0003*qGRKW.Z8vi*\u00111\u0001B\u0001\u0004eB\u001c'BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u0007\u0001Y\u0011\u0003\u0005\u0002\r\u001f5\tQBC\u0001\u000f\u0003\u0015\u00198-\u00197b\u0013\t\u0001RB\u0001\u0004B]f\u0014VM\u001a\t\u0003\u0019II!aE\u0007\u0003\u0019M+'/[1mSj\f'\r\\3\t\u0011U\u0001!Q1A\u0005\u0002]\t\u0001\u0002Z;sCRLwN\\\u0002\u0001+\u0005A\u0002CA\r\u001e\u001b\u0005Q\"BA\u000b\u001c\u0015\taR\"\u0001\u0006d_:\u001cWO\u001d:f]RL!A\b\u000e\u0003\u001d\u0019Kg.\u001b;f\tV\u0014\u0018\r^5p]\"A\u0001\u0005\u0001B\u0001B\u0003%\u0001$A\u0005ekJ\fG/[8oA!A!\u0005\u0001BC\u0002\u0013\u00051%A\u0006uS6,w.\u001e;Qe>\u0004X#\u0001\u0013\u0011\u0005\u0015BcB\u0001\u0007'\u0013\t9S\"\u0001\u0004Qe\u0016$WMZ\u0005\u0003S)\u0012aa\u0015;sS:<'BA\u0014\u000e\u0011!a\u0003A!A!\u0002\u0013!\u0013\u0001\u0004;j[\u0016|W\u000f\u001e)s_B\u0004\u0003\"\u0002\u0018\u0001\t\u0003y\u0013A\u0002\u001fj]&$h\bF\u00021eM\u0002\"!\r\u0001\u000e\u0003\tAQ!F\u0017A\u0002aAQAI\u0017A\u0002\u0011BQ!\u000e\u0001\u0005\nY\n\u0011d\u0019:fCR,'\u000b]2US6,w.\u001e;Fq\u000e,\u0007\u000f^5p]R\u0011qG\u000f\t\u0003caJ!!\u000f\u0002\u0003'I\u00038\rV5nK>,H/\u0012=dKB$\u0018n\u001c8\t\u000bm\"\u0004\u0019\u0001\u001f\u0002\u0005Q,\u0007CA\u001fD\u001b\u0005q$B\u0001\u000f@\u0015\t\u0001\u0015)\u0001\u0003vi&d'\"\u0001\"\u0002\t)\fg/Y\u0005\u0003\tz\u0012\u0001\u0003V5nK>,H/\u0012=dKB$\u0018n\u001c8\t\u000b\u0019\u0003A\u0011A$\u0002'\u0005$G-T3tg\u0006<W-\u00134US6,w.\u001e;\u0016\u0005!SV#A%\u0011\t1QE\nW\u0005\u0003\u00176\u0011q\u0002U1si&\fGNR;oGRLwN\u001c\t\u0003\u001bVs!AT*\u000f\u0005=\u0013V\"\u0001)\u000b\u0005E3\u0012A\u0002\u001fs_>$h(C\u0001\u000f\u0013\t!V\"A\u0004qC\u000e\\\u0017mZ3\n\u0005Y;&!\u0003+ie><\u0018M\u00197f\u0015\t!V\u0002\u0005\u0002Z52\u0001A!B.F\u0005\u0004a&!\u0001+\u0012\u0005u\u0003\u0007C\u0001\u0007_\u0013\tyVBA\u0004O_RD\u0017N\\4\u0011\u00051\t\u0017B\u00012\u000e\u0005\r\te.\u001f\u0005\u0006I\u0002!\t!Z\u0001\fC^\f\u0017\u000e\u001e*fgVdG/\u0006\u0002gQR\u0011q-\u001b\t\u00033\"$QaW2C\u0002qCQA[2A\u0002-\faAZ;ukJ,\u0007c\u00017nO6\t1$\u0003\u0002o7\t1a)\u001e;ve\u0016<a\u0001\u001d\u0002\t\u0002\u0011\t\u0018A\u0003*qGRKW.Z8viB\u0011\u0011G\u001d\u0004\u0007\u0003\tA\t\u0001B:\u0014\u0007I\\\u0011\u0003C\u0003/e\u0012\u0005Q\u000fF\u0001r\u0011\u00159(\u000f\"\u0001y\u0003\u0015\t\u0007\u000f\u001d7z)\r\u0001\u0014p \u0005\u0006uZ\u0004\ra_\u0001\u0005G>tg\r\u0005\u0002}{6\tA!\u0003\u0002\u007f\t\tI1\u000b]1sW\u000e{gN\u001a\u0005\u0006EY\u0004\r\u0001\n\u0005\u0007oJ$\t!a\u0001\u0015\u000fA\n)!a\u0002\u0002\n!1!0!\u0001A\u0002mDaAIA\u0001\u0001\u0004!\u0003bBA\u0006\u0003\u0003\u0001\r\u0001J\u0001\rI\u00164\u0017-\u001e7u-\u0006dW/\u001a\u0005\u0007oJ$\t!a\u0004\u0015\u000fA\n\t\"a\u0005\u0002\u001e!1!0!\u0004A\u0002mD\u0001\"!\u0006\u0002\u000e\u0001\u0007\u0011qC\u0001\u0010i&lWm\\;u!J|\u0007\u000fT5tiB!Q*!\u0007%\u0013\r\tYb\u0016\u0002\u0004'\u0016\f\bbBA\u0006\u0003\u001b\u0001\r\u0001\n\u0005\n\u0003C\u0011\u0018\u0011!C\u0005\u0003G\t1B]3bIJ+7o\u001c7wKR\u0011\u0011Q\u0005\t\u0005\u0003O\ti#\u0004\u0002\u0002*)\u0019\u00111F!\u0002\t1\fgnZ\u0005\u0005\u0003_\tIC\u0001\u0004PE*,7\r\u001e"
)
public class RpcTimeout implements Serializable {
   private final FiniteDuration duration;
   private final String timeoutProp;

   public static RpcTimeout apply(SparkConf var0, Seq<String> var1, String var2) {
      return RpcTimeout$.MODULE$.apply(var0, var1, var2);
   }

   public static RpcTimeout apply(SparkConf var0, String var1, String var2) {
      return RpcTimeout$.MODULE$.apply(var0, var1, var2);
   }

   public static RpcTimeout apply(SparkConf var0, String var1) {
      return RpcTimeout$.MODULE$.apply(var0, var1);
   }

   public FiniteDuration duration() {
      return this.duration;
   }

   public String timeoutProp() {
      return this.timeoutProp;
   }

   public RpcTimeoutException org$apache$spark$rpc$RpcTimeout$$createRpcTimeoutException(TimeoutException te) {
      return new RpcTimeoutException((new StringBuilder()).append((Object)te.getMessage()).append((Object)". This timeout is controlled by ").append((Object)this.timeoutProp()).toString(), te);
   }

   public <T> PartialFunction<Throwable, T> addMessageIfTimeout() {
      return new Serializable(this) {
         public static final long serialVersionUID = 0L;
         // $FF: synthetic field
         private final RpcTimeout $outer;

         public final <A1 extends Throwable, B1> B1 applyOrElse(A1 x1, Function1<A1, B1> var2) {
            if (x1 instanceof RpcTimeoutException) {
               RpcTimeoutException var4 = (RpcTimeoutException)x1;
               throw var4;
            } else if (x1 instanceof TimeoutException) {
               TimeoutException var6 = (TimeoutException)x1;
               throw this.$outer.org$apache$spark$rpc$RpcTimeout$$createRpcTimeoutException(var6);
            } else {
               Object var5 = var2.apply(x1);
               return (B1)var5;
            }
         }

         public final boolean isDefinedAt(Throwable x1) {
            boolean var3;
            if (x1 instanceof RpcTimeoutException) {
               var3 = true;
            } else if (x1 instanceof TimeoutException) {
               var3 = true;
            } else {
               var3 = false;
            }

            return var3;
         }

         public {
            if ($outer == null) {
               throw null;
            } else {
               this.$outer = $outer;
            }
         }
      };
   }

   public <T> T awaitResult(Future<T> future) {
      Object var10000;
      try {
         var10000 = ThreadUtils$.MODULE$.awaitResult(future, this.duration());
      } catch (Throwable var5) {
         PartialFunction catchExpr1 = this.addMessageIfTimeout();
         if (!catchExpr1.isDefinedAt(var5)) {
            throw var5;
         }

         var10000 = catchExpr1.apply(var5);
         return (T)var10000;
      }

      return (T)var10000;
   }

   public RpcTimeout(FiniteDuration duration, String timeoutProp) {
      this.duration = duration;
      this.timeoutProp = timeoutProp;
   }
}
