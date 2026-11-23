package org.apache.spark.scheduler;

import scala.Function1;
import scala.Option;
import scala.Product;
import scala.Product$class;
import scala.Serializable;
import scala.Tuple2;
import scala.collection.Iterator;
import scala.reflect.ScalaSignature;
import scala.runtime.BoxesRunTime;
import scala.runtime.ScalaRunTime$;

@ScalaSignature(
   bytes = "\u0006\u0001\u0005eb!B\u0001\u0003\u0001\nQ!!D#yK\u000e,Ho\u001c:BI\u0012,GM\u0003\u0002\u0004\t\u0005I1o\u00195fIVdWM\u001d\u0006\u0003\u000b\u0019\tQa\u001d9be.T!a\u0002\u0005\u0002\r\u0005\u0004\u0018m\u00195f\u0015\u0005I\u0011aA8sON)\u0001aC\t\u00161A\u0011AbD\u0007\u0002\u001b)\ta\"A\u0003tG\u0006d\u0017-\u0003\u0002\u0011\u001b\t1\u0011I\\=SK\u001a\u0004\"AE\n\u000e\u0003\tI!\u0001\u0006\u0002\u0003#\u0011\u000buiU2iK\u0012,H.\u001a:Fm\u0016tG\u000f\u0005\u0002\r-%\u0011q#\u0004\u0002\b!J|G-^2u!\ta\u0011$\u0003\u0002\u001b\u001b\ta1+\u001a:jC2L'0\u00192mK\"AA\u0004\u0001BK\u0002\u0013\u0005a$\u0001\u0004fq\u0016\u001c\u0017\nZ\u0002\u0001+\u0005y\u0002C\u0001\u0011$\u001d\ta\u0011%\u0003\u0002#\u001b\u00051\u0001K]3eK\u001aL!\u0001J\u0013\u0003\rM#(/\u001b8h\u0015\t\u0011S\u0002\u0003\u0005(\u0001\tE\t\u0015!\u0003 \u0003\u001d)\u00070Z2JI\u0002B\u0001\"\u000b\u0001\u0003\u0016\u0004%\tAH\u0001\u0005Q>\u001cH\u000f\u0003\u0005,\u0001\tE\t\u0015!\u0003 \u0003\u0015Awn\u001d;!\u0011\u0015i\u0003\u0001\"\u0001/\u0003\u0019a\u0014N\\5u}Q\u0019q\u0006M\u0019\u0011\u0005I\u0001\u0001\"\u0002\u000f-\u0001\u0004y\u0002\"B\u0015-\u0001\u0004y\u0002bB\u001a\u0001\u0003\u0003%\t\u0001N\u0001\u0005G>\u0004\u0018\u0010F\u00020kYBq\u0001\b\u001a\u0011\u0002\u0003\u0007q\u0004C\u0004*eA\u0005\t\u0019A\u0010\t\u000fa\u0002\u0011\u0013!C\u0001s\u0005q1m\u001c9zI\u0011,g-Y;mi\u0012\nT#\u0001\u001e+\u0005}Y4&\u0001\u001f\u0011\u0005u\u0012U\"\u0001 \u000b\u0005}\u0002\u0015!C;oG\",7m[3e\u0015\t\tU\"\u0001\u0006b]:|G/\u0019;j_:L!a\u0011 \u0003#Ut7\r[3dW\u0016$g+\u0019:jC:\u001cW\rC\u0004F\u0001E\u0005I\u0011A\u001d\u0002\u001d\r|\u0007/\u001f\u0013eK\u001a\fW\u000f\u001c;%e!9q\tAA\u0001\n\u0003B\u0015!\u00049s_\u0012,8\r\u001e)sK\u001aL\u00070F\u0001J!\tQu*D\u0001L\u0015\taU*\u0001\u0003mC:<'\"\u0001(\u0002\t)\fg/Y\u0005\u0003I-Cq!\u0015\u0001\u0002\u0002\u0013\u0005!+\u0001\u0007qe>$Wo\u0019;Be&$\u00180F\u0001T!\taA+\u0003\u0002V\u001b\t\u0019\u0011J\u001c;\t\u000f]\u0003\u0011\u0011!C\u00011\u0006q\u0001O]8ek\u000e$X\t\\3nK:$HCA-]!\ta!,\u0003\u0002\\\u001b\t\u0019\u0011I\\=\t\u000fu3\u0016\u0011!a\u0001'\u0006\u0019\u0001\u0010J\u0019\t\u000f}\u0003\u0011\u0011!C!A\u0006y\u0001O]8ek\u000e$\u0018\n^3sCR|'/F\u0001b!\r\u0011W-W\u0007\u0002G*\u0011A-D\u0001\u000bG>dG.Z2uS>t\u0017B\u00014d\u0005!IE/\u001a:bi>\u0014\bb\u00025\u0001\u0003\u0003%\t![\u0001\tG\u0006tW)];bYR\u0011!.\u001c\t\u0003\u0019-L!\u0001\\\u0007\u0003\u000f\t{w\u000e\\3b]\"9QlZA\u0001\u0002\u0004I\u0006bB8\u0001\u0003\u0003%\t\u0005]\u0001\tQ\u0006\u001c\bnQ8eKR\t1\u000bC\u0004s\u0001\u0005\u0005I\u0011I:\u0002\u0011Q|7\u000b\u001e:j]\u001e$\u0012!\u0013\u0005\bk\u0002\t\t\u0011\"\u0011w\u0003\u0019)\u0017/^1mgR\u0011!n\u001e\u0005\b;R\f\t\u00111\u0001Z\u000f!I(!!A\t\u0002\tQ\u0018!D#yK\u000e,Ho\u001c:BI\u0012,G\r\u0005\u0002\u0013w\u001aA\u0011AAA\u0001\u0012\u0003\u0011ApE\u0002|{b\u0001bA`A\u0002?}yS\"A@\u000b\u0007\u0005\u0005Q\"A\u0004sk:$\u0018.\\3\n\u0007\u0005\u0015qPA\tBEN$(/Y2u\rVt7\r^5p]JBa!L>\u0005\u0002\u0005%A#\u0001>\t\u000fI\\\u0018\u0011!C#g\"I\u0011qB>\u0002\u0002\u0013\u0005\u0015\u0011C\u0001\u0006CB\u0004H.\u001f\u000b\u0006_\u0005M\u0011Q\u0003\u0005\u00079\u00055\u0001\u0019A\u0010\t\r%\ni\u00011\u0001 \u0011%\tIb_A\u0001\n\u0003\u000bY\"A\u0004v]\u0006\u0004\b\u000f\\=\u0015\t\u0005u\u0011\u0011\u0006\t\u0006\u0019\u0005}\u00111E\u0005\u0004\u0003Ci!AB(qi&|g\u000eE\u0003\r\u0003Kyr$C\u0002\u0002(5\u0011a\u0001V;qY\u0016\u0014\u0004\"CA\u0016\u0003/\t\t\u00111\u00010\u0003\rAH\u0005\r\u0005\n\u0003_Y\u0018\u0011!C\u0005\u0003c\t1B]3bIJ+7o\u001c7wKR\u0011\u00111\u0007\t\u0004\u0015\u0006U\u0012bAA\u001c\u0017\n1qJ\u00196fGR\u0004"
)
public class ExecutorAdded implements DAGSchedulerEvent, Product, Serializable {
   private final String execId;
   private final String host;

   public static Option<Tuple2<String, String>> unapply(ExecutorAdded var0) {
      return ExecutorAdded$.MODULE$.unapply(var0);
   }

   public static ExecutorAdded apply(String var0, String var1) {
      return ExecutorAdded$.MODULE$.apply(var0, var1);
   }

   public static Function1<Tuple2<String, String>, ExecutorAdded> tupled() {
      return ExecutorAdded$.MODULE$.tupled();
   }

   public static Function1<String, Function1<String, ExecutorAdded>> curried() {
      return ExecutorAdded$.MODULE$.curried();
   }

   public String execId() {
      return this.execId;
   }

   public String host() {
      return this.host;
   }

   public ExecutorAdded copy(String execId, String host) {
      return new ExecutorAdded(execId, host);
   }

   public String copy$default$1() {
      return this.execId();
   }

   public String copy$default$2() {
      return this.host();
   }

   public String productPrefix() {
      return "ExecutorAdded";
   }

   public int productArity() {
      return 2;
   }

   public Object productElement(int x$1) {
      String var10000;
      switch (x$1) {
         case 0:
            var10000 = this.execId();
            break;
         case 1:
            var10000 = this.host();
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
      return x$1 instanceof ExecutorAdded;
   }

   public int hashCode() {
      return ScalaRunTime$.MODULE$._hashCode(this);
   }

   public String toString() {
      return ScalaRunTime$.MODULE$._toString(this);
   }

   public boolean equals(Object x$1) {
      boolean var9;
      if (this != x$1) {
         label62: {
            boolean var3;
            if (x$1 instanceof ExecutorAdded) {
               var3 = true;
            } else {
               var3 = false;
            }

            if (var3) {
               label44: {
                  label53: {
                     ExecutorAdded var4 = (ExecutorAdded)x$1;
                     String var10000 = this.execId();
                     String var5 = var4.execId();
                     if (var10000 == null) {
                        if (var5 != null) {
                           break label53;
                        }
                     } else if (!var10000.equals(var5)) {
                        break label53;
                     }

                     var10000 = this.host();
                     String var6 = var4.host();
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

   public ExecutorAdded(String execId, String host) {
      this.execId = execId;
      this.host = host;
      Product$class.$init$(this);
   }
}
