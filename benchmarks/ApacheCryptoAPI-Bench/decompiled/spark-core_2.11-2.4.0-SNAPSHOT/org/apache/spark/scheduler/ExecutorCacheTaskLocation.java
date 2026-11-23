package org.apache.spark.scheduler;

import scala.Function1;
import scala.Option;
import scala.Predef$;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.StringContext;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005mb!B\u0001\u0003\u0001\u0012Q!!G#yK\u000e,Ho\u001c:DC\u000eDW\rV1tW2{7-\u0019;j_:T!a\u0001\u0003\u0002\u0013M\u001c\u0007.\u001a3vY\u0016\u0014(BA\u0003\u0007\u0003\u0015\u0019\b/\u0019:l\u0015\t9\u0001\"\u0001\u0004ba\u0006\u001c\u0007.\u001a\u0006\u0002\u0013\u0005\u0019qN]4\u0014\u000b\u0001Y\u0011#\u0006\r\u0011\u00051yQ\"A\u0007\u000b\u00039\tQa]2bY\u0006L!\u0001E\u0007\u0003\r\u0005s\u0017PU3g!\t\u00112#D\u0001\u0003\u0013\t!\"A\u0001\u0007UCN\\Gj\\2bi&|g\u000e\u0005\u0002\r-%\u0011q#\u0004\u0002\b!J|G-^2u!\ta\u0011$\u0003\u0002\u001b\u001b\ta1+\u001a:jC2L'0\u00192mK\"AA\u0004\u0001BK\u0002\u0013\u0005c$\u0001\u0003i_N$8\u0001A\u000b\u0002?A\u0011\u0001e\t\b\u0003\u0019\u0005J!AI\u0007\u0002\rA\u0013X\rZ3g\u0013\t!SE\u0001\u0004TiJLgn\u001a\u0006\u0003E5A\u0001b\n\u0001\u0003\u0012\u0003\u0006IaH\u0001\u0006Q>\u001cH\u000f\t\u0005\tS\u0001\u0011)\u001a!C\u0001=\u0005QQ\r_3dkR|'/\u00133\t\u0011-\u0002!\u0011#Q\u0001\n}\t1\"\u001a=fGV$xN]%eA!)Q\u0006\u0001C\u0001]\u00051A(\u001b8jiz\"2a\f\u00192!\t\u0011\u0002\u0001C\u0003\u001dY\u0001\u0007q\u0004C\u0003*Y\u0001\u0007q\u0004C\u00034\u0001\u0011\u0005C'\u0001\u0005u_N#(/\u001b8h)\u0005y\u0002b\u0002\u001c\u0001\u0003\u0003%\taN\u0001\u0005G>\u0004\u0018\u0010F\u00020qeBq\u0001H\u001b\u0011\u0002\u0003\u0007q\u0004C\u0004*kA\u0005\t\u0019A\u0010\t\u000fm\u0002\u0011\u0013!C\u0001y\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#A\u001f+\u0005}q4&A \u0011\u0005\u0001+U\"A!\u000b\u0005\t\u001b\u0015!C;oG\",7m[3e\u0015\t!U\"\u0001\u0006b]:|G/\u0019;j_:L!AR!\u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004I\u0001E\u0005I\u0011\u0001\u001f\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%e!9!\nAA\u0001\n\u0003Z\u0015!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001M!\ti%+D\u0001O\u0015\ty\u0005+\u0001\u0003mC:<'\"A)\u0002\t)\fg/Y\u0005\u0003I9Cq\u0001\u0016\u0001\u0002\u0002\u0013\u0005Q+\u0001\u0007qe>$Wo\u0019;Be&$\u00180F\u0001W!\taq+\u0003\u0002Y\u001b\t\u0019\u0011J\u001c;\t\u000fi\u0003\u0011\u0011!C\u00017\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HC\u0001/`!\taQ,\u0003\u0002_\u001b\t\u0019\u0011I\\=\t\u000f\u0001L\u0016\u0011!a\u0001-\u0006\u0019\u0001\u0010J\u0019\t\u000f\t\u0004\u0011\u0011!C!G\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001e!\r)\u0007\u000eX\u0007\u0002M*\u0011q-D\u0001\u000bG>dG.Z2uS>t\u0017BA5g\u0005!IE/\u001a:bi>\u0014\bbB6\u0001\u0003\u0003%\t\u0001\\\u0001\tG\u0006tW)];bYR\u0011Q\u000e\u001d\t\u0003\u00199L!a\\\u0007\u0003\u000f\t{w\u000e\\3b]\"9\u0001M[A\u0001\u0002\u0004a\u0006b\u0002:\u0001\u0003\u0003%\te]\u0001\tQ\u0006\u001c\bnQ8eKR\ta\u000bC\u0004v\u0001\u0005\u0005I\u0011\t<\u0002\r\u0015\fX/\u00197t)\tiw\u000fC\u0004ai\u0006\u0005\t\u0019\u0001/\b\u0011e\u0014\u0011\u0011!E\u0001\ti\f\u0011$\u0012=fGV$xN]\"bG\",G+Y:l\u0019>\u001c\u0017\r^5p]B\u0011!c\u001f\u0004\t\u0003\t\t\t\u0011#\u0001\u0005yN\u001910 \r\u0011\ry\f\u0019aH\u00100\u001b\u0005y(bAA\u0001\u001b\u00059!/\u001e8uS6,\u0017bAA\u0003\u007f\n\t\u0012IY:ue\u0006\u001cGOR;oGRLwN\u001c\u001a\t\r5ZH\u0011AA\u0005)\u0005Q\b\u0002C\u001a|\u0003\u0003%)%!\u0004\u0015\u00031C\u0011\"!\u0005|\u0003\u0003%\t)a\u0005\u0002\u000b\u0005\u0004\b\u000f\\=\u0015\u000b=\n)\"a\u0006\t\rq\ty\u00011\u0001 \u0011\u0019I\u0013q\u0002a\u0001?!I\u00111D>\u0002\u0002\u0013\u0005\u0015QD\u0001\bk:\f\u0007\u000f\u001d7z)\u0011\ty\"a\u000b\u0011\u000b1\t\t#!\n\n\u0007\u0005\rRB\u0001\u0004PaRLwN\u001c\t\u0006\u0019\u0005\u001drdH\u0005\u0004\u0003Si!A\u0002+va2,'\u0007C\u0005\u0002.\u0005e\u0011\u0011!a\u0001_\u0005\u0019\u0001\u0010\n\u0019\t\u0013\u0005E20!A\u0005\n\u0005M\u0012a\u0003:fC\u0012\u0014Vm]8mm\u0016$\"!!\u000e\u0011\u00075\u000b9$C\u0002\u0002:9\u0013aa\u00142kK\u000e$\b"
)
public class ExecutorCacheTaskLocation implements TaskLocation, Product, Serializable {
   private final String host;
   private final String executorId;

   public static Option<Tuple2<String, String>> unapply(ExecutorCacheTaskLocation var0) {
      return ExecutorCacheTaskLocation$.MODULE$.unapply(var0);
   }

   public static ExecutorCacheTaskLocation apply(String var0, String var1) {
      return ExecutorCacheTaskLocation$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<String, String>, ExecutorCacheTaskLocation> tupled() {
      return ExecutorCacheTaskLocation$.MODULE$.tupled();
   }

   public static Function1<String, Function1<String, ExecutorCacheTaskLocation>> curried() {
      return ExecutorCacheTaskLocation$.MODULE$.curried();
   }

   public String host() {
      return this.host;
   }

   public String executorId() {
      return this.executorId;
   }

   public String toString() {
      return (new StringContext(Predef$.MODULE$.wrapRefArray((Object[])(new String[]{"", "", "_", ""})))).s(Predef$.MODULE$.genericWrapArray(new Object[]{TaskLocation$.MODULE$.executorLocationTag(), this.host(), this.executorId()}));
   }

   public ExecutorCacheTaskLocation copy(String host, String executorId) {
      return new ExecutorCacheTaskLocation(host, executorId);
   }

   public String copy$default$1() {
      return this.host();
   }

   public String copy$default$2() {
      return this.executorId();
   }

   public String productPrefix() {
      return "ExecutorCacheTaskLocation";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      String var10000;
      switch (x$1) {
         case 0:
            var10000 = this.host();
            break;
         case 1:
            var10000 = this.executorId();
            break;
         default:
            throw new IndexOutOfBoundsException(BoxesRunTime.boxToInteger(x$1).toString());
      }

      return var10000;
   }

   public Iterator<Object> productIterator() {
      return ScalaRunTime$.MODULE$.<Object>typedProductIterator(this);
   }

   public boolean canEqual(Object x$1) {
      return x$1 instanceof ExecutorCacheTaskLocation;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label62: {
            boolean var3;
            if (x$1 instanceof ExecutorCacheTaskLocation) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label44: {
                  label53: {
                     ExecutorCacheTaskLocation var4 = (ExecutorCacheTaskLocation)x$1;
                     String var10000 = this.host();
                     String var5 = var4.host();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label53;
                     }

                     var10000 = this.executorId();
                     String var6 = var4.executorId();
                     if (var10000 == null) {
                        if (var6 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var6)) {
                        break label53;
                     }

                     if (var4.canEqual(this)) {
                        var9 = true;
                        break label44;
                     }
                  }

                  var9 = false;
               }

               if (var9) {
                  break label62;
               }
            }

            var9 = false;
            return var9;
         }
      }

      var9 = true;
      return var9;
   }

   public ExecutorCacheTaskLocation(String host, String executorId) {
      this.host = host;
      this.executorId = executorId;
      Product$class.$init$(this);
   }
}
